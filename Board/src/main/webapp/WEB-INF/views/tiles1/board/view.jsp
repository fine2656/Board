
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
   
<style type="text/css">
	table, th, td, input, textarea {border: solid gray 1px;}
	
	#table, #table2 {border-collapse: collapse;
	 		         width: 1000px;
	 		        }
	#table th, #table td{padding: 5px;}
	#table th{width: 120px; background-color: #DDDDDD;}
	#table td{width: 860px;}
	.long {width: 470px;}
	.short {width: 120px;} 	
	
	.move  {cursor: pointer;}
	.moveColor {color: #660029; font-weight: bold;}
	
	a{text-decoration: none;}	
	
	

</style>

<script type="text/javascript">
    
	$(document).ready(function(){
	
		$(".move").hover(function(){
					       $(this).addClass("moveColor");
					     }, 
					     function(){
					  	   $(this).removeClass("moveColor");   
					     });
	
		
		if(${boardvo.commentCount >0}){// 댓글이 있을 때에만 하겠다.
			// 초기치 설정(초기치로 최신의 댓글을 최대 5개 까지 보여주겠다.)
			goViewComment("1");// 초기 페이지 : 1
			
		}

			
	});// end of $(document).ready()----------------------
    
	// === 댓글 쓰기 ===
	function goAddWrite() {

	      var frm = document.addWriteFrm;
	      var nameval = frm.name.value.trim();
	      
	      if(nameval == "") {   // 로그인을 안했다면
	         alert("먼저 로그인 하세요!");
	         return;

		}
		var contentval = frm.content.value.trim();
		if(contentval == ""){
	         alert("내용을 입력해주세요!");
	         frm.content.value="";
	         frm.content.focus();
	         return;
		}
		var queryString = $("form[name=addWriteFrm]").serialize(); //form 이름이 addWriteFrm 인 것,
		//console.log(queryString);
		$.ajax({
			  url:"<%=request.getContextPath()%>/addComment.action",
		         data:queryString,
		         type:"POST",
		         dataType:"JSON",
		         success:function(json){
					var html = "<tr>"+
				                "<td style='text-align: center;'>"+json.NAME+"</td>"+
				                "<td>"+json.CONTENT+"</td>"+
				                "<td style='text-align: center;'>"+json.REGDATE+"</td>"+
				                "</tr>";		       
		      
			   $("#commentDisplay").prepend(html);
		      
		       frm.content.value="";// 새로운 댓글 달기 위해서 값을 비워준다.				
			},	error: function(request, status, error){
				alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			}
		});
		
	} // end of function goWrite()--------------------------------
 ////////////////////////////////////////////////////////////////////////////////////////   
	// === 댓글 내용을 Ajax로 페이징 처리하여 보여주기 ===
	function goViewComment(currentShowPageNo) {
		var form_data={"seq":"${boardvo.seq}",// 댓글을 보이기 위한 부모 seq
					   "currentShowPageNo":currentShowPageNo} 
		
		$.ajax({
			url:"<%= request.getContextPath() %>/commentList.action",
			data:form_data,
			type:"GET",
			dataType:"JSON",
			success: function(json) {
	            var resultHTML = "";
	            $.each(json,function(entryIndex,entry){	               
	               resultHTML += "<tr>"+
	                          "<td style='text-align: center;'>"+entry.NAME+"</td>"+
	                          "<td>"+entry.CONTENT+"</td>"+
	                          "<td style='text-align: center;'>"+entry.REGDATE+"</td>"+
	                          "</tr>";
	                          
	            });// end of each-----------------
	            
	            $("#commentDisplay").empty().html(resultHTML);
	            
	            
	            // 페이지바 함수 호출
				//page 넘길 때 기존의 값을 없앤 후 위의 html로 채운다
				
				// ==== 댓글 내용 페이지바 AJAX로 만들기 ====
	            makeCommentPageBar(currentShowPageNo);
			
			},error: function(request, status, error){
			  alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			}
						
		});
	}// end of goViewComment(currentShowPageNo) 
	
	function makeCommentPageBar(currentShowPageNo) {
		
		var form_data = {seq:"${boardvo.seq}",
						sizePerPage:"5"};
		$.ajax({
			url:"<%= request.getContextPath() %>/getCommentTotalPage.action",
			data:form_data,
			type:"GET",
			dataType:"JSON",
			success:function(json){
				if(json.TOTALPAGE > 0){
					// 댓글이 있는 경우
					var pageBarHTML="";
					var totalPage =json.TOTALPAGE; 				
					//{"TOTALPAGE":21}
					
					////////////////////////////
					var blockSize = 10;
					// blockSize 는 1개 블럭(토막)당 보여지는 페이지번호의 갯수이다.
					/*
						 1 	2  3  4  5  6  7  8  9 10 --- 1개 블럭
						11 12 13 14 15 16 17 18 19 20 --- 1개 블럭
					*/
					var loop =1;
					/* loop 는 1부터 증가하여 1개 블력을 이루는 페이지 번호의 갯수*/
					
					var pageNo = Math.floor((currentShowPageNo - 1)/blockSize)*blockSize+1;
					//공식
					
					/*
					Math.floor(); -> 버림
					Math.round(); -> 반올림
					Math.ceil(); -> 올림

						currentShowPageNo 		pageNO
						------------------------------
								1					1 = Math.floor((1-1)/10)*10+1;
								2					1 = Math.floor((2-1)/10)*10+1; 
								3					1
								4					1
								5					1
							   ...				   ...
							    10					1
								11					11
								12					11
								13					11
							   ...				   ...		
								20					11
								21					21
								22					21
								
							    */
				// 자바에서는 1/10 은 0이되지만 자바스크립트에서는 10/0 은 0.1이된다, 그래서 0.1을 0으로 만들기 위해 Math.floor을 쓴다
				// Math.floor(0.1)을 쓰면 0.1보다 작은 최대의 정수가 나온다.
				
				// -----------------------------------------
				// 					[이전] 만들기
					if(pageNo != 1){
						pageBarHTML += "&nbsp;<a href='javascript:goViewComment(\""+(pageNo-1)+"\");'>[이전]</a>";	
					}		
					
				// -----------------------------------------
								 // 현재페이지
					while(!(loop > blockSize || pageNo > totalPage)){
						if(pageNo == currentShowPageNo) {// 보고 있는 페이지가 현재 페이지일 경우
							pageBarHTML += "&nbsp;<span style='color:red; font-size:12pt; font-weight:bold; text-decoration:underline;'>"+pageNo+"</span>";	
						}else{
							pageBarHTML += "&nbsp;<a href='javascript:goViewComment(\""+pageNo+"\");'>"+pageNo+"</a>";	
						}						
						
						loop++;
						pageNo++;
					}
					
				
				// -----------------------------------------					
							   // [다음] 만들기
				if(!(pageNo > totalPage)){
						pageBarHTML += "&nbsp;<a href='javascript:goViewComment(\""+pageNo+"\");'>[다음]</a>";	
					}							
					$("#pageBar").empty().html(pageBarHTML);					
					pageBarHTML = "";//초기화		
					
				}else{
					// 댓글이 없는경우
					$("#pageBar").empty();
				}
					
			},error: function(request, status, error){
			  alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			}
		});
	}// end of makeCommentPageBar(currentShowPageNo)
</script>

<div style="padding-left: 10%; border: solid 0px red;">
	<h1>글내용보기</h1>
	
	<table id="table">
		<tr>
			<th>글번호</th>
			<td>${boardvo.seq}</td>
		</tr>
		<tr>
			<th>성명</th>
			<td>${boardvo.name}</td>
		</tr>
		<tr>
           	<th>제목</th>
           	<td>${boardvo.subject}</td>
        	</tr>
		<tr>
			<th>내용</th>
			<td>${boardvo.content}</td>
		</tr>
		<tr>
			<th>조회수</th>
			<td>${boardvo.readCount}</td>
		</tr>
		<tr>
			<th>날짜</th>
			<td>${boardvo.regDate}</td>
		</tr>
		
		<!-- ==== #148. 첨부파일 이름 및 파일크기를 보여주고 첨부파일 다운로드 받게 만들기 ==== -->
		<tr>
			<th>첨부파일</th>
			<td>
				<c:if test="${sessionScope.loginuser != null}">
					<a href="<%= request.getContextPath() %>/download.action?seq=${boardvo.seq}">${boardvo.orgFilename}</a>
				</c:if>
				<c:if test="${sessionScope.loginuser == null}">
					${boardvo.orgFilename}
				</c:if>
			</td>
		</tr>
		<tr>
			<th>파일크기</<th>
			<td>${boardvo.fileSize}</td>
		</tr>
	</table>
	
	<br/>
	
	<div style="margin-bottom: 1%;"><span class="move" onClick="javascript:location.href='view.action?seq=${boardvo.previousseq}'">이전글 :${boardvo.previoussubject}</span></div>
	<div style="margin-bottom: 1%;"><span class="move" onClick="javascript:location.href='view.action?seq=${boardvo.nextseq}'">다음글 :${boardvo.nextsubject}</span></div>
	
	<br/>
	
	<%-- <button type="button" onClick="javascript:location.href='<%= request.getContextPath() %>/list.action'">목록보기</button> --%>
	<button type="button" onClick="javascript:location.href='<%= request.getContextPath() %>/${goBackURL}'">목록보기</button>
	<button type="button" onClick="javascript:location.href='<%= request.getContextPath() %>/edit.action?seq=${boardvo.seq}'">수정</button>
	<button type="button" onClick="javascript:location.href='<%= request.getContextPath() %>/del.action?seq=${boardvo.seq}'">삭제</button>
	
	<!-- 답변 글 쓰기 버튼 추가하기(현재 보고 있는 글이 작성하려는 답변글의 원글 (부모글이 된다)) -->
	<button type="button" onClick="javascript:location.href='<%= request.getContextPath() %>/add.action?fk_seq=${boardvo.seq}&groupno=${boardvo.groupno}&depthno=${boardvo.depthno}'">답변글쓰기</button>
	<!-- groupno: 부모글의 그룹 no -->
	<br/>
	<br/>
		
	<p style="margin-top: 3%; font-size: 16pt;">댓글쓰기</p>
	<!-- ===== #84. 댓글쓰기 폼 추가 ===== -->
	<form name="addWriteFrm">     
		      <input type="hidden" name="fk_userid" value="${sessionScope.loginuser.userid}" readonly />
		성명 : <input type="text" name="name" value="${sessionScope.loginuser.name}" class="short" readonly/>
	       댓글내용 : <input type="text" name="content" class="long" /> <!--  tblComment 에 있는 content -->
	    
	    <!-- 댓글에 달리는 원게시물 글번호(즉, 댓글의 부모글 글번호) -->
	    <input type="hidden" name="parentSeq" value="${boardvo.seq}" />  	    
	    <button type="button" onClick="goAddWrite();" >쓰기</button>    
	</form>	
	<!--  ==== #91. 댓글 내용 보여주기 -->
	<table id="table2" style="margin-top: 2%; margin-bottom: 3%;">
		<thead>
			<tr>
				<th style="text-align: center;">댓글 작성자</th>
				<th style="text-align: center;">댓글 내용</th>
				<th style="text-align: center;">댓글 작성일</th>
			</tr>
		</thead>
		<tbody id="commentDisplay"></tbody>
	</table>	

	<div id="pageBar" style="height: 50px; margin-left: 30%;"></div>
</div>











