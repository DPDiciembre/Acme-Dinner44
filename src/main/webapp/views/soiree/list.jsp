<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<!-- Messages -->
<spring:message code="soiree.address" var="soireeAddress" />
<spring:message code="soiree.date" var="soireeDate" />
<spring:message code="soiree.pictures" var="soireePictures" />
<spring:message code="soiree.dishes" var="soireeDishes" />
<spring:message code="soiree.createdish" var="createdish" />
<spring:message code="soiree.view" var="view" />
<spring:message code="soiree.edit" var="edit" />
<spring:message code="soiree.delete" var="delete" />
<spring:message code="soiree.vote" var="vote" />

<!-- Table -->

<display:table  name="soirees" id="row" requestURI="${requestURI}" pagesize="10" class="table table-hover">

	<display:column property="address" title="${soireeAddress}" sortable="false" />
	<display:column title="${soireeDate}" sortable="false" >
		<fmt:formatDate value="${row.date}" pattern="dd/MM/yyyy HH:mm"/>
	</display:column>
	<display:column title="${soireePictures}">
		<jstl:forEach var="e" items="${row.pictures}">
			<img src="${e}" style="max-width:120px;max-height:120px;"><br />
		</jstl:forEach>		
	</display:column>
	
	<!-- Show dishes -->
	<display:column title="${soireeDishes}">
		<acme:url url="event/soiree/dish/list.do?q=${row.id}" code="soiree.dishes"/>
	</display:column>
	
	<!-- Bot�n crear un plato -->
	<display:column title="${createdish}" sortable="false">
		<jstl:if test="${soireesOfDiner.contains(row) and canCreateDish.contains(row)}">
			<acme:url url="soiree/dish/create.do?q=${row.id}" code="soiree.createdish"/>				
		</jstl:if>
	</display:column>
	
	<!-- Mostrar -->
	<display:column title="${view}" sortable="false">
		<acme:url url="soiree/view.do?q=${row.id}" code="soiree.view"/>
	</display:column>
			
	<security:authentication property="principal.id" var="id" />
	
	<!-- Editar -->		
	<display:column title="${edit}" sortable="false">		
		<jstl:if test="${row.organizer.userAccount.id == id}">
			<acme:url url="soiree/edit.do?q=${row.id}" code="soiree.edit"/>
		</jstl:if>
	</display:column>
	
	<!-- Borrar-->
	<display:column title="${delete}" sortable="false">		
		<jstl:if test="${row.organizer.userAccount.id == id}">
			<acme:url url="soiree/delete.do?q=${row.id}" code="soiree.delete"/>
		</jstl:if>
	</display:column>
	
	<!-- Votar -->
	<display:column title="${vote}" sortable="false">		
		<jstl:if test="${isRegisteredInEvent and dinerCanCastAVote.contains(row)}">
			<acme:url url="soiree/vote.do?q=${row.id}" code="soiree.vote"/>
		</jstl:if>
	</display:column>

</display:table>
<!-- Crear type of dishes -->
<input onclick="javascript:location.href='soiree/dish/dishType/create.do'" type="button" class="btn" value="<spring:message code="dish.addDishType" />">

