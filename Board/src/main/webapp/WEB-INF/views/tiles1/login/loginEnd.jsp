<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<% String ctxPath = request.getContextPath(); %>

<script type="text/javascript">
	var loginuser = "${sessionScope.loginuser.userid}";
	var gobackURL = "${requestScope.gobackURL}";
	
	if(loginuser != null && loginuser != "" && (gobackURL == null || gobackURL == "") ) {
		alert("로그인 성공 !!");
		location.href="<%=ctxPath%>/index.action";
	}
	
	if(loginuser != null && loginuser != "" && (gobackURL != null && gobackURL != "")) {
		alert("로그인 성공 !!");
		location.href=gobackURL;
	}
</script>
    