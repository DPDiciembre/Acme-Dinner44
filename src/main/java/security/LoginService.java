/*
 * LoginService.java
 * 
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Actor;

@Service
@Transactional
public class LoginService implements UserDetailsService {

	// Managed repository -----------------------------------------------------

	@Autowired
	UserAccountRepository	userRepository;


	// Business methods -------------------------------------------------------

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		Assert.notNull(username);

		UserDetails result;

		result = this.userRepository.findByUsername(username);
		Assert.notNull(result);
		// WARNING: The following sentences prevent lazy initialisation problems!
		Assert.notNull(result.getAuthorities());
		result.getAuthorities().size();

		return result;
	}
	
	public static boolean isAnyAuthenticated() {
		try {
			SecurityContext context;
			Authentication authentication;
			Object principal;

			context = SecurityContextHolder.getContext();
			authentication = context.getAuthentication();
			principal = authentication.getPrincipal();

			return principal instanceof UserAccount;
		} catch(Throwable t) {
			return false;
		}
	}
	
	private static UserAccount getAuthenticated() {
		try {
			UserAccount result;
			SecurityContext context;
			Authentication authentication;
			Object principal;

			context = SecurityContextHolder.getContext();
			authentication = context.getAuthentication();
			principal = authentication.getPrincipal();
			result = (UserAccount) principal;

			return result;
		} catch(Throwable t) {
			return null;
		}
	}
	
	public static boolean hasRole(String role) {
		UserAccount account = LoginService.getAuthenticated();
		
		if(account == null) {
			return false;
		}
		
		for(Authority e : account.getAuthorities()) {
			if(e.getAuthority().equalsIgnoreCase(role)) {
				return true;
			}
		}
		
		return false;
	}
	
	public Actor findActorByUsername(String username) {
		return userRepository.findActorByUsername(LoginService.getPrincipal().getUsername());
	}
	
	public Actor findActorByUsername(Integer id) {
		return userRepository.findActorByUsernameId(id);
	}
	
	public UserAccount findByActorId(Integer id){
		return userRepository.findByActorId(id);
	}

	public static UserAccount getPrincipal() {
		UserAccount result;
		SecurityContext context;
		Authentication authentication;
		Object principal;

		// If the asserts in this method fail, then you're
		// likely to have your Tomcat's working directory
		// corrupt. Please, clear your browser's cache, stop
		// Tomcat, update your Maven's project configuration,
		// clean your project, clean Tomcat's working directory,
		// republish your project, and start it over.

		context = SecurityContextHolder.getContext();
		Assert.notNull(context);
		authentication = context.getAuthentication();
		Assert.notNull(authentication);
		principal = authentication.getPrincipal();
		Assert.isTrue(principal instanceof UserAccount);
		result = (UserAccount) principal;
		Assert.notNull(result);
		Assert.isTrue(result.getId() != 0);

		return result;
	}

	public boolean exists(Integer id) {
		return userRepository.exists(id);
	}

	public UserAccount findOne(Integer id) {
		return userRepository.findOne(id);
	}
}