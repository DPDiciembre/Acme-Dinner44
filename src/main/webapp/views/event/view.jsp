<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<!-- Messages -->
<spring:message code="event.searchtext" var="eventSearchText" />
<spring:message code="event.search" var="eventSearch" />
<spring:message code="event.ticker" var="eventTicker" />
<spring:message code="event.title" var="eventTitle" />
<spring:message code="event.city" var="eventCity" />
<spring:message code="event.description" var="eventDescription"/>
<spring:message code="event.registeredDiners" var="eventRegisteredDiners" />
<spring:message code="event.soirees" var="eventSoirees" />
<spring:message code="event.address" var="address" />
<spring:message code="event.date" var="date" />
<spring:message code="event.pictures" var="pictures" />
<spring:message code="event.dishes" var="dishes" />
<spring:message code="event.organizer" var="organizador" />


<div class=row style="text-align:center">
	<div class="col-md-6" >
	
		<div class='title'>${eventTicker}:</div>
		&nbsp;&nbsp;
		<jstl:out value="${evento.ticker}" />
		<br />
		<br />
		
		<div class='title'>${eventTitle}:</div>
		&nbsp;&nbsp;
		<jstl:out value="${evento.title}" />
		<br />
		<br />
		
		<div class='title'>${eventCity}:</div>
		&nbsp;&nbsp;
		<jstl:out value="${evento.city}" />
		<br />
		<br />
		
		<div class='title'>${eventDescription}:</div>
		&nbsp;&nbsp;
		<jstl:out value="${evento.description}" />
		<br />
		<br />
		
		<div class='title'>${organizador}:</div>
		&nbsp;&nbsp;
		<jstl:out value="${organizer.actorName}" />
		<br />
		<br />

		
	</div>
	<div class="col-md-6">
		<h1 class='title'>${eventSoirees}</h1>
		<!-- Table -->
		<display:table  name="soirees" id="row" requestURI="${requestURI}" class="table">
			
			<display:column property="address" title="${address}" sortable="false" />
			<display:column title="${date}" sortable="false" >
				<fmt:formatDate value="${row.date}" pattern="dd/MM/yyyy HH:mm"/>
			</display:column>
			<display:column title="${pictures}">
				<jstl:forEach var="e" items="${row.pictures}">
					<img src="${e}" style="max-width:120px;max-height:120px;"><br />
				</jstl:forEach>		
			</display:column>
			<display:column title="${dishes}">
				<acme:url url="event/soiree/dish/list.do?q=${row.id}" code="soiree.dishes"/>
			</display:column>
		</display:table>
	</div>
</div>