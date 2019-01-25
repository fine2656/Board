<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 차트그리기 --> 
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/data.js"></script>
<script src="https://code.highcharts.com/modules/drilldown.js"></script>

<div style="padding-left: 5%; border: solid 0px red;">
	<h2>${sessionScope.loginuser.name}님의 주문내역</h2>
</div>

<div id="myorderChart" style="width: 80%; height: 400px; margin: 30px auto; border: solid 1px red;">
	
</div>

<script type="text/javascript">

	$(document).ready(function(){
		
		setMyorderChart();
							
	});
	
	
	
	function setMyorderChart() { 
		
		$.ajax({
			url: "<%= request.getContextPath() %>/snackchart/my_snackorderStatistics.action",
			type: "GET",
			dataType: "JSON",
			success: function(json){
				if(json.length == 0) {
					$("#myorderChart").html("<div align='center' style='margin-top:50px;'><span style='font-size:14pt;'>주문내역이 없습니다.</span></div>");
				}
				else {
					var jepumObjArr = [];
					// 또는 var subArr = new Array();
	
					$.each(json, function(entryIndex, entry){ 
						   jepumObjArr.push([ entry.SNACKNAME, Number(entry.TOTALQTY) ]);
					});	
					
					// Create the chart
					Highcharts.chart('myorderChart', {
					    chart: {
					        type: 'pie',
					        options3d: {
					            enabled: true,
					            alpha: 45
					        }
					    },
					    title: {
					        text: '스낵별 주문수량'
					    },
					    subtitle: {
					        text: '3D donut in Highcharts'
					    },
					    plotOptions: {
					        pie: {
					            innerSize: 100,
					            depth: 45
					        }
					    },
					    series: [{
					        name: '주문수량',
					        data: jepumObjArr   // **** 위에서 구한값을 대입시킴. ****
					        /*	
					        [
								['Bananas', 8],
					            ['Kiwi', 3],
					            ['Mixed nuts', 1],
					            ['Oranges', 6],
					            ['Apples', 8],
					            ['Pears', 4],
					            ['Clementines', 4],
					            ['Reddish (bag)', 1],
					            ['Grapes (bunch)', 1]
								
					        ] 
					       */
					    }]
					});
					
				}// end of if~else---------------------------	
				
			}, // end of success: function(data){ }-------------------------
			error: function(request, status, error){
				   alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			} 
		});
		
	}// end of function setMyorderChart() { }---------------------------
	
</script>