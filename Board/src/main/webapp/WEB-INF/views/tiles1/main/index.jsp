<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
	String cxtPath = request.getContextPath();
%>

<div align="center" style="margin: 0 auto; /* border: solid red 1px; */">
	
	<h2 style="margin-bottom: 50px;">대 박 쇼 핑</h2>
	
	<div style="width:75%; /* border: solid red 1px; */">
		<div id="myCarousel" class="carousel slide" data-ride="carousel">
		  <!-- Indicators -->
		  <ol class="carousel-indicators">
		    <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
		    <li data-target="#myCarousel" data-slide-to="1"></li>
		    <li data-target="#myCarousel" data-slide-to="2"></li>
		    <li data-target="#myCarousel" data-slide-to="3"></li>
		  </ol>
	
		  <!-- Wrapper for slides -->
		  <div class="carousel-inner" role="listbox">
		  	<c:forEach var="imgname" items="${imgfilenameList}" varStatus="status">
		  		<c:if test="${status.index == 0}"> <!-- 첫번째 꺼라면  -->
			  		<div class="item active">
			      		<img src="<%=cxtPath %>/resources/images/${imgname}"> 
			    	</div>
		    	</c:if>
		    	<c:if test="${status.index > 0}">
			    	<div class="item">
			      		<img src="<%=cxtPath %>/resources/images/${imgname}">
			    	</div>
		    	</c:if>
		  	</c:forEach>
		  </div>
	
	  <!-- Left and right controls -->
		  <a class="left carousel-control" href="#myCarousel" role="button" data-slide="prev">
		    <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
		    <span class="sr-only">Previous</span>
		  </a>
		  <a class="right carousel-control" href="#myCarousel" role="button" data-slide="next">
		    <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
		    <span class="sr-only">Next</span>
		  </a>
		</div>
	</div>
	
</div>
    