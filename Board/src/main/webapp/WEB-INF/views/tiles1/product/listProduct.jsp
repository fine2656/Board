<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div style="margin-bottom: 5%; padding-left: 3%;">
	<h2>제품목록</h2>
</div>
<div>
	<c:forEach var="map" items="${productList}" varStatus="status">
		<div style="display: inline-block; width: 20%; padding-left: 3%;">
			제품번호: ${map.PRODSEQ}<br/>
			카테고리: ${map.CATENAME}<br/>
			제품명: ${map.PRODNAME}<br/>
			제조회사: ${map.PRODCOMPANY}<br/>
			스펙명: ${map.SPECNAME}<br/>
			<a href="<%= request.getContextPath() %>/product/viewProduct.action?fk_prodseq=${map.PRODSEQ}"><img src="<%= request.getContextPath() %>/resources/files/${map.THUMBNAILFILENAME}"></a>  
		</div>
		<c:if test="${(status.count % 4) == 0}">
		<div>&nbsp;</div>
		</c:if>
	</c:forEach>
</div>
    