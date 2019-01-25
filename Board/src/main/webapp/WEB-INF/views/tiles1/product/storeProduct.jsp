<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/resources/jquery-ui-1.11.4.custom/jquery-ui.css" />
<script type="text/javascript" src="<%= request.getContextPath() %>/resources/jquery-ui-1.11.4.custom/jquery-ui.js"></script> 

<style type="text/css">
  th {width: 25%;}
</style>

<script type="text/javascript">

	$(document).ready(function(){

		$("#spinnerIbgoqty").spinner({
  	      spin: function( event, ui ) {
  	        if( ui.value > 100 ) {
  	          $( this ).spinner( "value", 0 ); 
  	          return false;
  	        } 
  	        else if ( ui.value < 0 ) {
  	          $( this ).spinner( "value", 10 );
  	          return false;
  	        }
  	      }
  	    });
		
	}); // end of $(document).ready()-----------------------------------
	

	function goAdd() {
		var addFrm = document.addFrm;
		addFrm.submit();
	}
	
	function goReset() {
		var addFrm = document.addFrm;
		addFrm.reset();
	}
	
</script>

<div class="container" style="margin-bottom: 50px;">
	<h3 style="width: 60%; padding-top: 20px;">제품 입고 하기</h3>

	<form name="addFrm" action="<%= request.getContextPath() %>/product/storeProductEnd.action" method="post">
		<table id="table" class="table table-bordered" style="width: 70%; margin-top: 50px;">
			<tr>
				<th>입고제품선택</th>
				<td>
					<div style="width: 30%;">
					  <select class="form-control" id="prodseq" name="prodseq">
					  	  <c:forEach var="map" items="${prodseqList}">
					  	  	 <option value="${map.PRODSEQ}">${map.PRODNAME}</option>
					  	  </c:forEach>
					  </select>
					</div> 
				</td>
			</tr>
			
         	<tr>
         		<th>입고량</th>
         		<td>
  		            <input id="spinnerIbgoqty" name="ibgoqty" value="0" style="width: 30px; height: 20px;">
          		</td>
         	</tr>
		</table>
		<br/>
		
		<button type="button" class="btn btn-primary" style="margin-right: 10px;" onClick="goAdd();">입고등록</button>
		<button type="button" class="btn btn-primary" style="margin-right: 10px;" onClick="goReset();">취소</button>
	
	</form>
</div>











   