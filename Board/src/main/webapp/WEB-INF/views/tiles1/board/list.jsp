<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    
<style type="text/css">
	table, th, td {border: solid gray 1px;}
	#table {border-collapse: collapse; width: 750px;} 
	
	.subjectstyle {font-weight: bold;
    	           color: navy;
    	           cursor: pointer; }
	
	
	#table {border-collapse: collapse; width: 920px;}
	#table th, #table td {padding: 5px;}
	#table th {background-color: #DDDDDD;}
	
	a{text-decoration: none;} 
	
	    
</style>

 
<script type="text/javascript">
	$(document).ready(function(){
		
		$(".subject").bind("mouseover", function(event){
			 var $target = $(event.target);
			 $target.addClass("subjectstyle");
		});
		  
		$(".subject").bind("mouseout", function(event){
			 var $target = $(event.target);
			 $target.removeClass("subjectstyle");
		});
		searchKeep();			
	
	});// end of $(document).ready()----------------------
	

	function goSearch() {
		var frm = document.searchFrm;
		frm.action="<%= request.getContextPath() %>/list.action";
		frm.method="GET";
		frm.submit();
	}
	
	function searchKeep(){
		if(search != null && search != "" && search != "null"){
			$("#search").val("${search}");
			$("#colname").val("${colname}");
		}
	
	}
	function goView(seq,gobackURL) {
		var frm = document.goViewFrm;
		frm.seq.value = seq;
		frm.gobackURL.value=gobackURL;
		frm.method="GET";
		frm.action="view.action";
		frm.submit();
	}
		
</script>

<div style="padding-left: 10%; border: solid 0px red;">
	<h1 style="margin-bottom: 30px;">글목록</h1>
	
	<table id="table">

		<tr>
			<th style="width: 70px;  text-align: center;" >글번호</th>
			<th style="width: 360px; text-align: center;" >제목</th>
			<th style="width: 70px;  text-align: center;" >성명</th>
			<th style="width: 180px; text-align: center;" >날짜</th>
			<th style="width: 70px;  text-align: center;" >조회수</th>
		
			<!-- ==== # 145. 파일과 크기를 보여주도록 수정 ==== -->
			<th style="width: 70px;  text-align: center;" >첨부파일</th>
			<th style="width: 100px;  text-align: center;">파일크기</th>
		</tr>
		<c:forEach items="${boardList}" var="boardvo">
			<tr>
				<td style="width: 70px;  text-align: center;" >${boardvo.seq}</td>
				<!-- ==== #97. 글제목에 댓글의 갯수를 붙이도록 한다. -->
				<%-- <td style="width: 360px; text-align: center;" ><span class="subject" onClick="goView(${boardvo.seq})">${boardvo.subject}</span></td> --%>
<%-- 				<td style="width: 360px; text-align: center;" >
				<span class="subject" onClick="goView('${boardvo.seq}','${gobackURL}')">${boardvo.subject} 
					<c:if test="${boardvo.commentCount > 0}">
						<span style="color: red; font-weight: bold;font-size: smaller; vertical-align: super;">[ ${boardvo.commentCount} ]</span>
					</c:if>					
				</span>
				</td> --%>
				
				<!--   ==== #132. 답변형 게시판. == 
					답변글 인 경우 글제목에 앞에 공백과 함께 Re 라는 글자를 붙이도록 한다.
					이어서 글제목에 댓글의 갯수를 붙이도록 한다.  -->
				<%-- <td style="width: 360px; text-align: center;" ><span class="subject" onClick="goView(${boardvo.seq})">${boardvo.subject}</span></td> --%>
				<td style="width: 360px; text-align: center;" >
				 <!-- 원 글인 경우  -->
				 <c:if test="${boardvo.depthno == 0}">	
					 <span class="subject" onClick="goView('${boardvo.seq}','${gobackURL}')">${boardvo.subject} 
						<c:if test="${boardvo.commentCount > 0}">						
						 <span style="color: red; font-weight: bold;font-size: smaller; vertical-align: super;">[ ${boardvo.commentCount} ]</span>	</c:if>											
						</span>
								
				</c:if>
				<!--  답변 글인 경우 -->
				<c:if test="${boardvo.depthno > 0}">
					
						<span class="subject" onClick="goView('${boardvo.seq}','${gobackURL}')"><span style="color: red; font-style: italic; padding-left:${boardvo.depthno *20}px;">└Re</span>&nbsp;${boardvo.subject}				
						<c:if test="${boardvo.commentCount > 0}"><span style="color: red; font-weight: bold;font-size: smaller; vertical-align: super;">[ ${boardvo.commentCount} ]</span></c:if>											
						</span>
					
				</c:if>				
				</td>
				
				<td style="width: 70px;  text-align: center;" >${boardvo.name}</td>
				<td style="width: 180px; text-align: center;" >${boardvo.regDate}</td>
				<td style="width: 70px;  text-align: center;" >${boardvo.readCount}</td>
				
				<!-- ==== # 146. 파일과 크기를 보여주도록 수정 ==== -->
				<td align="center" >
					<c:if test="${not empty boardvo.fileName}">
						<img src=" <%= request.getContextPath() %>/resources/images/disk.gif" border="0"/>
					</c:if>
				</td>
				<td>
						<c:if test="${not empty boardvo.fileSize}">
						${boardvo.fileSize}<!-- 파일 크기 -->						
					</c:if>
				</td>
			</tr>
		</c:forEach>
		
	</table>
	<br/>
	
	<form name="goViewFrm"> <input type="hidden" name="seq" /><input type="hidden" name="gobackURL" /></form>
	<!--  페이지 바 보여주기 -->
	<div align="center" style="width: 70% ;">${pagebar}</div>
	<!-- ==== #105. 글 검색 폼 추가하기 : 글제목, 글내용, 글슨이로 검색을 하도록 한다 ==== -->
	<form name="searchFrm">
		<select id="colname" name="colname" style="height: 26px">
			<option value="subject">제목</option>
			<option value="content">내용</option>			
			<option value="name">성명</option>			
		</select>	
		<input type="text" name="search" id="search" size="40" />
		<button type="button" onclick="goSearch();">검색</button>
	</form>
</div>

	







