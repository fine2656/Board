<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!-- 차트그리기 --> 
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/data.js"></script>
<script src="https://code.highcharts.com/modules/drilldown.js"></script>

<div style="padding-left: 5%; border: solid 0px red; margin-bottom: 50px;">
	<h2>스낵 주문하기</h2>
</div>

<div style="width: 90%; margin: 10px auto; border: solid 0px red;">
	<div id="displayOrderRankTable" style="width: 40%; height: 400px; display: inline-block; border: solid 0px red;">
	
	</div>
	
	<div id="displayOrderRankChart" style="width: 54%; height: 400px; display: inline-block; border: solid 0px red; margin-left: 5%;">
	                           <!-- style="min-width: 310px; height: 400px; max-width: 600px; margin: 0 auto" -->
	</div>
</div>

<div align="center" style="width: 60%; border: solid red 0px; margin: auto; margin-top: 50px;">
	<form name="orderFrm"> <%-- 서브밋으로 전송을 하지 않고 ajax 요청으로 서버로 전송하므로 action 과 method 의 기술은 자바스크립트 함수에서 기재하도록 한다. --%>
		<div style="width:20%; display:inline-block; border: solid orange 0px;"> 
			<label for="snackno">스낵명</label> 
			<select class="form-control" name="snackno" id="snackno">
				<c:if test="${snackNameList != null && not empty snackNameList}">
					<c:forEach var="map" items="${snackNameList}">
						<option value="${map.SNACKNO}">${map.SNACKNAME}</option>
					</c:forEach>
				</c:if>
			</select>
		</div>
	
		<div style="width:20%; display:inline-block; margin-left: 10%; border: solid orange 0px;"> 
			<label for="typecode">타입</label>
				<div id="selectTypecode">
					
				</div>
		</div>
			
		<div style="width:15%; display:inline-block; margin-left: 10%; border: solid orange 0px;"> 
			<label for="oqty">주문량</label>
				<div id="selectOqty">
					
				</div>
		</div>
		
		<div style="margin-top: 30px; border: solid orange 0px;">
			<button type="button" class="btn btn-success" onClick="goOrder();">주문하기</button>  
			<%-- submit 버튼은 현재 페이지를 닫고서 action 처리 페이지로 이동을 하는 것이므로 
			         스크립트 함수를 호출할 수가 없게 된다.  
				  그러므로 스크립트 처리를 위한 버튼은 submit 버튼이 아니라 일반 버튼의 type="button"을 사용해야 한다. --%>
		</div>
												         
	</form>
</div>

<script type="text/javascript">

	$(document).ready(function(){
		
		 goGetsnackTypeCode();
		 
		 $("#snackno").bind("change", function(){
			 goGetsnackTypeCode();
		 });
		 
		 goGetOqty();
		 		 
		 setOrderRankTable();
		 setOrderRankChart();
		 sideinfo_setOrderRankChart();  // /Board/src/main/webapp/WEB-INF/tiles/tile3/sideinfo.jsp 에 기술되어 있음.
							
	});

	
	function goGetsnackTypeCode() {
		
		var form_data = {snackno : $("#snackno").val() }; // 키값:밸류값

		$.ajax({
			url : "<%= request.getContextPath() %>/snackchart/getSnackTypeCode.action",   // action 에 해당하는 URL 속성값
			type :"GET",                               // method
			data : form_data,                          // 위의 URL 페이지로 사용자가 보내는 ajax 요청 데이터.
			dataType : "JSON",                         // URL 페이지로 부터 받아오는 데이터타입
			success : function(json) {                 // 데이터 전송이 성공적으로 이루어진 후 처리해줄 callback 함수
						$("#selectTypecode").empty();
						
						var html = "<select class='form-control' name='typecode' id='typecode'>";
						
						$.each(json, function(entryIndex, entry){
							html += "<option value=\""+ entry.TYPECODE +"\">"+ entry.TYPENAME +"</option>";	
						});
						
						html += "</select>";
						
						$("#selectTypecode").append(html);
			 },
			 error: function(request, status, error){
					alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			 }         
		});
		
	}// end of goGetsnackTypeCode()----------------------------------------
	
	
	function goGetOqty() {

		$("#selectOqty").empty();
		
		var html = "<select class='form-control' name='oqty' id='oqty'>"+
		           "<option value='1'>1</option>"+
			       "<option value='2'>2</option>"+
			       "<option value='3'>3</option>"+
			       "<option value='4'>4</option>"+
			       "<option value='5'>5</option>"+
			       "</select>";
		
		$("#selectOqty").append(html);
		
	}// end of goGetOqty()-------------------------------------
	
	
	function goOrder() {
		// document.orderFrm.submit();   // ajax 요청으로 전송을 할것이므로 submit 요청을 하면 안되고 아래처럼 ajax 처리로 전송을 해야만 비동기 방식이라 새페이지 갱신이 없으므로 깜박임이 없다.
		
		var form_data = {snackno : $("#snackno").val(),               // 키값:밸류값
				         typecode : $("#typecode").val(),             // 키값:밸류값
						 oqty : $("#oqty").val(),                     // 키값:밸류값 
						 userid : "${sessionScope.loginuser.userid}"  // 키값:밸류값 
					    };
		
		$.ajax({
			url: "<%= request.getContextPath() %>/snackchart/snackorderEnd.action",    // action 에 해당하는 URL 속성값
			type:"POST",                            // method
			data: form_data,                        // 위의 URL 페이지로 사용자가 보내는 ajax 요청 데이터.
			success: function() {                   // 데이터 전송이 성공적으로 이루어진 후 처리해줄 callback 함수
				// alert("snackorderEnd 성공!!");
				setOrderRankTable();
				setOrderRankChart();
			  //goGetOqty();
			  
			    sideinfo_setOrderRankChart(); // /Board/src/main/webapp/WEB-INF/tiles/tile3/sideinfo.jsp 에 기술되어 있음.
			},
			error: function(request, status, error){
				   alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			}          
		});
	}// end of goOrder()------------------------------
	

	function setOrderRankTable() {
		
		$.ajax({
			url: "<%= request.getContextPath() %>/snackchart/snackorderRank.action",
			type: "GET",
			dataType: "JSON", 
			success: function(json){
				           // json => ajax 요청에 의해 서버로 부터 리턴받은 데이터.
				$("#displayOrderRankTable").empty();   
				// <div id="displayOrderRankTable"> 엘리먼트를 모두 비워서 새 데이터를 채울 준비를 한다.
			
				// $.each() 함수는 $(selector).each()와는 다르다.
				// $.each(순회해야할 collection 또는 배열, callback(indexInArray, valueOfElement) );
				// 배열을 다루는 경우에는, 콜백 함수는 인덱스와 값을 인자로 갖는다.
								
				var html = "<table class='table' width='90%'>";
				    html += "<thead>";
				    html += "<tr>";
					html += "<th class='custom_center custom_font'>등수</th>";
					html += "<th class='custom_center custom_font'>제품명</th>";
					html += "<th class='custom_center custom_font'>주문량합</th>";
					html += "</tr>";
					html += "</thead>";
				
					html += "<tbody>";
				
			    $.each(json, function(entryIndex, entry){ 
					html += "<tr>";
					html += "<td class='custom_center custom_tdheight'>";
					html += "<span class='custom_color custom_font'>" + entry.RANKING + "</span>";
					html += "</td>";
					html += "<td class='custom_center custom_tdheight'>";
					html += ""+ entry.SNACKNAME;
					html += "</td>";
					html += "<td class='custom_center custom_tdheight'>";
					html += ""+ entry.TOTALQTY;
					html += "</td>";
					html += "</tr>";
				});
			    
			        html += "</tbody>";
					html += "</table>";
			
				$("#displayOrderRankTable").append(html);	
				
				// /Board/src/main/webapp/WEB-INF/tiles/tile3/sideinfo.jsp 파일에 넣어주기 
				$("#sideinfo_displayOrderRankTable").empty(); 
				$("#sideinfo_displayOrderRankTable").append(html);
			},
			error: function(request, status, error){
				   alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			} 
		});
		
		
	}// end of function setOrderRankTable() { }--------------------------
	
	
	function setOrderRankChart() { 
		
		$.ajax({
			url: "<%= request.getContextPath() %>/snackchart/snackorderRank.action",
			type: "GET",
			dataType: "JSON",
			success: function(json){
				var jepumObjArr = [];
    	 		
				$.each(json, function(entryIndex, entry){ 
					jepumObjArr.push({
		                			   name: entry.SNACKNAME,
		                			   y: parseFloat(entry.PERCENTAGE),
		                             });
				});	// end of $.each(data, function(entryIndex, entry)----------------
						
				// Create the chart
				Highcharts.chart('displayOrderRankChart', {
				    chart: {
				        plotBackgroundColor: null,
				        plotBorderWidth: null,
				        plotShadow: false,
				        type: 'pie'
				    },
				    title: {
				        text: '제품별 판매 비율'
				    },
				    tooltip: {
				        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
				    },
				    plotOptions: {
				        pie: {
				            allowPointSelect: true,
				            cursor: 'pointer',
				            dataLabels: {
				                enabled: true,
				                format: '<b>{point.name}</b>: {point.percentage:.1f} %',
				                style: {
				                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
				                }
				            }
				        }
				    },
				    series: [{
				        name: '판매율',
				        colorByPoint: true,
				        data: jepumObjArr   // **** 위에서 구한값을 대입시킴. ****
				        /* [{
				            name: 'Microsoft Internet Explorer',
				            y: 56.33
				        }, {
				            name: 'Chrome',
				            y: 24.03,
				            sliced: true,
				            selected: true
				        }, {
				            name: 'Firefox',
				            y: 10.38
				        }, {
				            name: 'Safari',
				            y: 4.77
				        }, {
				            name: 'Opera',
				            y: 0.91
				        }, {
				            name: 'Proprietary or Undetectable',
				            y: 0.2
				        }] */
				    }]
				});
				
			}, // end of success: function(data){ }-------------------------
			error: function(request, status, error){
				   alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			} 
		});
		
	}// end of function setOrderRankChart() { }---------------------------
	
	
</script>