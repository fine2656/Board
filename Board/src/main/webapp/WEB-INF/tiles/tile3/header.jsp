<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%-- ======= #25. tiles2 중 header 페이지 만들기  ======= --%>
<%
	String cxtPath = request.getContextPath();
%>

<div align="center">
	<ul class="nav nav-tabs mynav">
		<li class="dropdown"><a class="dropdown-toggle"
			data-toggle="dropdown" href="#">Home <span class="caret"></span></a>
			<ul class="dropdown-menu">
				<li><a href="<%=cxtPath%>/index.action">Home</a></li>
				<li><a href="#">Submenu 1-2</a></li>
				<li><a href="#">Submenu 1-3</a></li>
			</ul></li>
		<li class="dropdown"><a class="dropdown-toggle"
			data-toggle="dropdown" href="#">스낵주문(통계차트) <span class="caret"></span></a>
			<ul class="dropdown-menu">
				<li><a href="<%=cxtPath%>/snackchart/snackorder.action">스낵주문하기</a></li>
				<li><a href="<%=cxtPath%>/snackchart/my_snackorderchart.action">나의주문내역차트</a></li>
				<li><a href="#">Submenu 3-3</a></li>
			</ul></li>
		<li><a href="#">Menu 2</a></li>
		<li><a href="#">Menu 3</a></li>
	</ul>
</div>
