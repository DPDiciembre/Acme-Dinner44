package controllers;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import domain.Diner;
import domain.Dish;
import domain.Event;
import domain.Soiree;

import security.LoginService;
import services.EventService;
import services.SoireeService;
import services.VoteService;

@Controller
@RequestMapping("/event")
public class EventController extends AbstractController {

	// Services ---------------------------------------------------------------

	@Autowired
	private EventService eventService;

	@Autowired
	private LoginService loginService;

	@Autowired
	private SoireeService soireeService;
	
	@Autowired
	private VoteService voteService;

	// Constructors -----------------------------------------------------------
	public EventController() {
		super();
	}

	// Actions

	@RequestMapping(value = "/listNoRegister", method = RequestMethod.GET)
	public ModelAndView listNoRegister() {
		ModelAndView result;

		result = new ModelAndView("event/list");

		result.addObject("events", eventService.findAll());
		result.addObject("a", 0);

		return result;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;

		result = new ModelAndView("event/list");
		ArrayList<Integer> canCreateSoiree = new ArrayList<Integer>();
		ArrayList<Event> eventCanRegistered = new ArrayList<Event>();

		if (LoginService.hasRole("DINER")) {
			Diner d = (Diner) loginService.findActorByUsername(LoginService
					.getPrincipal().getId());
			result.addObject("myRegisteredEvents", d.getEvents());
			for (Event e : d.getEvents()) {
				if (eventService.findRegisteredDinerInEvents(e.getId()).size() < 4) {
					eventCanRegistered.add(e);
				}
			}

			for (Event e : d.getEvents()) {
				Collection<Diner> organizers = soireeService
						.organizerOfSoireesOfEvent(e.getId());
				if (!organizers.contains(d)) {
					canCreateSoiree.add(e.getId());
				}
			}

			result.addObject("eventCanRegistered", eventCanRegistered);
			result.addObject("canCreateSoiree", canCreateSoiree);
		}

		result.addObject("events", eventService.findAll());

		return result;
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ModelAndView search(@RequestParam(required = false) String q) {
		ModelAndView result;
		ArrayList<Integer> canCreateSoiree = new ArrayList<Integer>();
		ArrayList<Integer> eventCanRegistered = new ArrayList<Integer>();
		ArrayList<Event> dateIsBeforeCurrent = new ArrayList<Event>();
		result = new ModelAndView("event/list");

		result.addObject("a", 0);
		result.addObject("events", eventService.findEventsByKeyWord(q));

		if (LoginService.hasRole("DINER")) {
			Diner d = (Diner) loginService.findActorByUsername(LoginService
					.getPrincipal().getId());
			result.addObject("myRegisteredEvents", d.getEvents());

			for (Event e : eventService.findEventsByKeyWord(q)) {
				Collection<Diner> organizers = soireeService
						.organizerOfSoireesOfEvent(e.getId());
				if (!organizers.contains(d)) {
					canCreateSoiree.add(e.getId());
				}

				if (eventService.findRegisteredDinerInEvents(e.getId()).size() < 4) {
					eventCanRegistered.add(e.getId());
				}
				if (eventService.isOver(e)) {
					dateIsBeforeCurrent.add(e);
				}
			}
			result.addObject("dateIsBeforeCurrent", dateIsBeforeCurrent);
			result.addObject("eventCanRegistered", eventCanRegistered);
			result.addObject("canCreateSoiree", canCreateSoiree);
		}

		return result;
	}

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public ModelAndView view(@RequestParam(required = true) int q) {
		ModelAndView result;
		result = new ModelAndView("event/view");

		Event e = eventService.findOne(q);

		Collection<Dish> dishesView = new ArrayList<Dish>();

		result.addObject("evento", e);
		result.addObject("organizer", e.getOrganizer());
		result.addObject("soirees", e.getSoirees());

		for (Soiree s : e.getSoirees()) {
			dishesView.addAll(s.getDishes());
		}
		result.addObject("dishes", dishesView);

		return result;
	}

	// This event's soirees
	// ----------------------------------------------------------------

	@RequestMapping(value = "/soiree/list", method = RequestMethod.GET)
	public ModelAndView soirees(@RequestParam(required = true) final int q) {

		ModelAndView result;
		result = new ModelAndView("soiree/list");
		Event e = eventService.findOne(q);
		if (LoginService.hasRole("DINER")) {
			Diner d = (Diner) loginService.findActorByUsername(LoginService
					.getPrincipal().getId());

			ArrayList<Soiree> soireesOfDiner = new ArrayList<Soiree>();
			ArrayList<Soiree> canCreateDish = new ArrayList<Soiree>();
			Boolean isRegisteredInEvent = false;
			ArrayList<Soiree> dinerCanCastAVote = new ArrayList<Soiree>();

			for (Soiree s : e.getSoirees()) {
				if (s.getOrganizer() == d) {
					soireesOfDiner.add(s);
				}
				if (s.getDishes().size() < 4) {
					canCreateDish.add(s);
				}
				if (eventService.isOver(e)
						&& voteService.dinerHasVoteInSoiree(d.getId(),
								s.getId()) < 1) {
					dinerCanCastAVote.add(s);
				}
			}

			if (d.getEvents().contains(e)) {
				isRegisteredInEvent = true;
			}
			result.addObject("dinerCanCastAVote", dinerCanCastAVote);
			result.addObject("isRegisteredInEvent", isRegisteredInEvent);
			result.addObject("canCreateDish", canCreateDish);
			result.addObject("soireesOfDiner", soireesOfDiner);
		}
		result.addObject("soirees", e.getSoirees());

		return result;
	}

	// This soiree's dishes
	// ----------------------------------------------------------------

	@RequestMapping(value = "/soiree/dish/list", method = RequestMethod.GET)
	public ModelAndView dishes(@RequestParam(required = true) final int q) {
		ModelAndView result;
		result = new ModelAndView("dish/list");

		Soiree s = soireeService.findOne(q);
		result.addObject("dishes", soireeService.dishesOfSoiree(s.getId()));
		result.addObject("soiree",q);

		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView result;

		result = createNewModelAndView(eventService.create(), null);

		return result;
	}

	@RequestMapping("/edit")
	public ModelAndView edit(@RequestParam Event q) {
		ModelAndView result;
		Diner d = (Diner) loginService.findActorByUsername(LoginService
				.getPrincipal().getId());

		if (d != null) {
			if (q.getOrganizer() == d) {
				result = createNewModelAndView(q, null);
			} else {
				result = new ModelAndView("redirect:/misc/403.do");
			}
		} else {
			return new ModelAndView("redirect:/welcome/index.do");
		}

		return result;
	}

	@RequestMapping(value = "/save-create", method = RequestMethod.POST, params = "save")
	public ModelAndView saveCreateEdit(@Valid Event event, BindingResult binding) {
		ModelAndView result;
		if (binding.hasErrors()) {
			result = createNewModelAndView(event, null);
		} else {
			try {
				eventService.save(event);
				result = new ModelAndView(
						"redirect:/diner/event/organizedList.do");

			} catch (Throwable th) {
				result = createNewModelAndView(event, "event.commit.error");
			}
		}
		return result;
	}

	protected ModelAndView createNewModelAndView(Event event, String message) {
		ModelAndView result;

		if (event.getId() == 0) {
			result = new ModelAndView("event/create");
		} else {
			result = new ModelAndView("event/edit");
		}
		result.addObject("event", event);
		result.addObject("message", message);
		return result;
	}

	@RequestMapping("/delete")
	public ModelAndView delete(@RequestParam Event q) {
		ModelAndView result;

		Diner d = (Diner) loginService.findActorByUsername(LoginService
				.getPrincipal().getId());

		if (d != null) {
			if (q.getOrganizer() == d) {
				eventService.delete(q);
				result = new ModelAndView(
						"redirect:/diner/event/organizedList.do");
			} else {
				result = new ModelAndView("redirect:/misc/403.do");
			}
		} else {
			return new ModelAndView("redirect:/welcome/index.do");
		}

		return result;
	}

}
