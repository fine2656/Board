<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<% String ctxPath = request.getContextPath(); %>

<style type="text/css">
	table, th, td, input, textarea {border: solid gray 1px;}
	
	#table {border-collapse: collapse;
	 		width: 1000px;
	 		}
	#table th, #table td{padding: 5px;}
	#table th{width: 120px; background-color: #DDDDDD;}
	#table td{width: 860px;}
	.long {width: 470px;}
	.short {width: 120px;} 		

</style>

<script type="text/javascript">
	
	$(document).ready(function(){
		
		/* 스마트에디터 구현 시작 */		
		//전역변수
	    var obj = [];
	    
	    //스마트에디터 프레임생성
	    nhn.husky.EZCreator.createInIFrame({
	        oAppRef: obj,
	        elPlaceHolder: "content",
	        sSkinURI: "<%= request.getContextPath() %>/resources/smarteditor/SmartEditor2Skin.html",
	        htParams : {
	            // 툴바 사용 여부 (true:사용/ false:사용하지 않음)
	            bUseToolbar : true,            
	            // 입력창 크기 조절바 사용 여부 (true:사용/ false:사용하지 않음)
	            bUseVerticalResizer : true,    
	            // 모드 탭(Editor | HTML | TEXT) 사용 여부 (true:사용/ false:사용하지 않음)
	            bUseModeChanger : true,
	        }
	    });
	    /* 스마트에디터 구현 끝 */
	    // 쓰기버튼
	    $("#btnWrite").click(function(){
	    	 //id가 content인 textarea에 에디터에서 대입
	    	 /* 스마트에디터 구현 시작 */
	        obj.getById["content"].exec("UPDATE_CONTENTS_FIELD", []);// 툴바 붙이는 곳
	        
	        //폼 submit
	        var writeFrm = document.addFrm;
			writeFrm.action ="<%= request.getContextPath() %>/addEnd.action";
			writeFrm.method = "POST";
			writeFrm.submit();

	    });
		
	}); // end of ready()-------------------------------------------
		
</script>

<div style="padding-left: 10%; margin-bottom: 0.2%; border: solid 0px red;">
	<h1>글쓰기</h1>
	<%--
	<form name="addFrm">  --%>
	<!-- ==== #134. 파일 첨부하기  
	        먼저 위의 문장을 주석 처리 한후 아래와 같이 한다.
	     enctype="multipart/form-data"를 해주어야만 파일 첨부가 된다. -->
	<form name="addFrm" enctype="multipart/form-data"> 
		<table id="table">
			<tr>
				<th>성명</th>
				<td>
				    <input type="hidden" name="fk_userid" value="${sessionScope.loginuser.userid}" readonly />
					<input type="text" name="name" value="${sessionScope.loginuser.name}" class="short" readonly />
				</td>
			</tr>
			<tr>
				<th>제목</th>
				<td><input type="text" name="subject" id="subject" class="long" /></td>
			</tr>
			<tr>
            	<th>내용</th>
            	<td>
            	<%--   **  textarea 태그에서 required="required" 속성을 사용하면 스마트 에디터는 오류가 발생하므로 사용하지 않는다. ** --%>
            	<textarea name="content" id="content" rows="10" cols="100" style="width:95%; height:412px;"></textarea>
            	</td>
         	</tr>
         	
         	<!-- ==== #135.파일 첨부 타입 추가하기 ==== -->
         	<tr>
         		<th>파일 첨부</th>
         		<td><input type="file" name="attach" /></td> <!-- BoardVO 에서 MultipartFile과 이름이 똑같아야 한다 -->         		
         	</tr>

		<tr>
			<th>암호</th>
			<td><input type="password" name="pw" id="pw" class="short" /></td>
		</tr>
		</table>
		<br/>
		
		<!-- ==== #126. 답변글 쓰기 인 경우 ====
			부모글의 seq 값인 fk_seq 값과 부모글의 groupno 값과 부모글의 depthno 값을 hidden 타입으로 보낸다-->
		<input type="hidden" name="fk_seq" value="${fk_seq}"/>
		<input type="hidden" name="groupno" value="${groupno}"/>
		<input type="hidden" name="depthno" value="${depthno}"/>
		
		<button type="button" id="btnWrite">쓰기</button>
		<button type="button" onClick="javascript:history.back();">취소</button>
  </form> 

</div>	