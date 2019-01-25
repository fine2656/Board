<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="http://www.kma.go.kr/XML/weather/sfc_web_map.xml" charEncoding="UTF-8" />

<%-- http://localhost:9090/board/weatherXML.action  
	 속초 북춘천 철원 동두천 파주 대관령 춘천 백령도 북강릉 강릉 동해 서울 인천 원주 울릉도 수원 영월 충주 서산 울진 청주 대전 추풍령 안동 상주 포항 군산 대구 전주 울산 창원 광주 부산 통영 목포 여수 흑산도 완도 고창 순천 진도(첨찰산)
	 홍성 제주 고산 성산 서귀포 진주 강화 양평 이천 인제 홍천 태백 정선군 제천 보은 천안 보령 부여 금산 부안 임실 정읍 남원 장수 고창군 영광군 김해시 순창군 북창원 양산시 보성군 강진군 장흥 해남 고흥 의령군 함양군 광양시 진도군 봉화
	 영주 문경 청송군 영덕 의성 구미 영천 경주시 거창 합천 밀양 산청 거제 남해
 	
 	=>  상단의 !! trimDirectiveWhitespaces="true" !! 꼭 넣어준다 : 공백제거 를 적어준다
 	trimDirectiveWhitespaces="true"  을 사용해주는 이유는 xml 파일은 항상 맨  위 첫줄에 
 	<?xml version="1.0" encoding="UTF-8" standalone="no" ?> 이 들어와야 한다.
 	그런데 JSP 파일 이기에 <%@ %> 도 함께 설정을 해주어야 한다.
 	이렇게 두 개를 같이 해주면 결과물 윗부분에 여러줄의 공백이 생기게 된다.그래서 이와 같이 생성되는 공백줄을
 	제거해 주는 것이  trimDirectiveWhitespaces="true" 이다. 그래야만 오류가 없이 xml 내용이 잘 나오게 된다.
 --%>