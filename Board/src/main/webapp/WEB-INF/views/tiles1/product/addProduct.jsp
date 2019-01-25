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

		$("#spinnerOqty").spinner({
  	      spin: function( event, ui ) {
  	        if( ui.value > 10 ) {
  	          $( this ).spinner( "value", 0 ); 
  	          return false;
  	        } 
  	        else if ( ui.value < 0 ) {
  	          $( this ).spinner( "value", 10 );
  	          return false;
  	        }
  	      }
  	    });
		
		
		$("#spinnerOqty").bind("spinstop", function(){
			// 스핀너는 이벤트가 "change" 가 아니라 "spinstop" 이다.
			var html = "";
			
			var spinnerOqtyVal = $("#spinnerOqty").val();
			
			if(spinnerOqtyVal == "0") {
				$("#divFileattach").empty();
				return;
			}
			else
			{
				for(var i=0; i<parseInt(spinnerOqtyVal); i++) {
					html += "<br/>";
					html += "<input type='file' name='attach' class='btn btn-default' />";
				}
				
				$("#divFileattach").empty();
				$("#divFileattach").append(html);	
			}
		});
		
	}); // end of $(document).ready()-----------------------------------
	

	function goAdd() {
		// 유효성 검사는 생략함.
		var addFrm = document.addFrm;
		addFrm.submit();
	}
	
	function goReset() {
		var addFrm = document.addFrm;
		addFrm.reset();
		$("#result").empty();	
	}
	
</script>

<div class="container">
	<h3 style="width: 60%; padding-top: 20px;">다중파일 올리기 및 썸네일 파일 생성하기</h3>
	
	 <%-- >>>> 파일첨부하기
	 	       enctype="multipart/form-data" 을 해주어야만 파일첨부가 된다. --%>
	<form name="addFrm" action="<%= request.getContextPath() %>/product/addProductEnd.action" method="post" enctype="multipart/form-data" >
		<table id="table" class="table table-bordered" style="width: 70%; margin-top: 50px;">
			<tr>
				<th>카테고리명</th>
				<td>
					<div style="width: 30%;">
					  <select class="form-control" id="fk_catecode" name="fk_catecode">
					  	  <c:forEach var="map" items="${cateCodeList}">
					  	  	 <option value="${map.CATECODE}">${map.CATENAME}</option>
					  	  </c:forEach>
					  </select>
					</div> 
				</td>
			</tr>
			<tr>
				<th>제품명</th>
				<td>
				    <div style="width: 50%;">
						<input type="text" name="prodname" class="form-control" />
					</div>
				</td>
			</tr>
			<tr>
				<th>제조회사명</th>
				<td>
					<div style="width: 50%;">
						<input type="text" name="prodcompany" class="form-control" />
					</div>						
				</td>
			</tr>
			<tr>
				<th>제품정가</th>
				<td>
					<div style="width: 30%; border: solid 0px red; display: inline-block;">
						<input type="text" name="price" class="form-control" />
					</div>
					<div style="width: 5%; border: solid 0px red; display: inline-block;">
						원
					</div>						
				</td>
			</tr>
			<tr>
				<th>제품판매가</th>
				<td>
					<div style="width: 30%; border: solid 0px red; display: inline-block;">
						<input type="text" name="saleprice" class="form-control" />
					</div>
					<div style="width: 5%; border: solid 0px red; display: inline-block;">
						원
					</div>						
				</td>
			</tr>
			<tr>
				<th>제조스펙</th>
				<td>
					<div style="width: 30%;">
					  <select class="form-control" id="fk_speccode" name="fk_speccode">
					  	  <c:forEach var="map" items="${specCodeList}">
					  	  	 <option value="${map.SPECCODE}">${map.SPECNAME}</option>
					  	  </c:forEach>
					  </select>
					</div> 
				</td>
			</tr>
			<tr>
            	<th>제품설명</th>
            	<td><textarea name="prodcontent" class="form-control" rows="5"></textarea></td>
         	</tr>
         	<tr>
				<th>제품포인트</th>
				<td>
					<div style="width: 30%; border: solid 0px red; display: inline-block;">
						<input type="text" name="prodpoint" class="form-control" />
					</div>
					<div style="width: 10%; border: solid 0px red; display: inline-block;">
						POINT
					</div>						
				</td>
			</tr>
         	
         	<%-- ==== 다중첨부파일 타입 추가하기 ===== --%>
         	<tr>
         		<th>제품이미지 파일 첨부</th>
         		<td>
         		    <label for="spinnerOqty">파일갯수 : </label>
  		            <input id="spinnerOqty" value="0" style="width: 30px; height: 20px;">
         		    <div id="divFileattach"></div>
         		</td>
         	</tr>
		</table>
		<br/>
		
		<button type="button" class="btn btn-primary" style="margin-right: 10px;" onClick="goAdd();">제품등록</button>
		<button type="button" class="btn btn-primary" style="margin-right: 10px;" onClick="goReset();">취소</button>
		<button type="button" class="btn btn-primary" style="margin-right: 10px;" onClick="javascript:location.href='<%= request.getContextPath() %>/product/listProduct.action'">제품목록</button> 
	
	</form>
	<div style="margin-top: 20px;">&nbsp;</div>
</div>











   