<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 차트그리기 --> 
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/data.js"></script>
<script src="https://code.highcharts.com/modules/drilldown.js"></script>

<script type="text/javascript">
    
	$(document).ready(function() {
				
	   showRank();
		
	}); // end of ready(); ---------------------------------
	
	
	function sideinfo_setOrderRankChart() {
		
		$.getJSON("<%= request.getContextPath() %>/snackchart/snackorderRank.action", 
				  
				 function(json){ // json => ajax 요청에 의해 서버로 부터 리턴받은 데이터.
	 								           
                     var jepumObjArr = [];
            	 		
					 $.each(json, function(entryIndex, entry){ 
						jepumObjArr.push({
			                "name": entry.SNACKNAME,
			                "y": parseFloat(entry.PERCENTAGE),
			                "drilldown": entry.SNACKNAME
			                });
		             }); // end of $.each(json, function(entryIndex, entry)----------------
			
			
		    		var jepumObjDetailArr = [];
		     // 또는 var jepumObjDetailArr = new Array();
		    			
					$.each(json, function(entryIndex, entry) { 
						$.getJSON("<%= request.getContextPath() %>/snackchart/detailRankJsonBySnackname.action?snackname="+entry.SNACKNAME, 
								function(json2){ 
									var subArr = [];
							// 또는   var subArr = new Array();
				
									$.each(json2, function(entryIndex2, entry2){ 
										subArr.push([
													 entry2.TYPENAME,
													 parseFloat(entry2.PERCENT)
												    ]);
									});	// end of $.each(json2, function(entryIndex2, entry2){  --------------
									
									jepumObjDetailArr.push({
										"name": entry.SNACKNAME,
										"id": entry.SNACKNAME,
										"data": subArr
									});	
									
								});
								
							}); // $.each(json, function(entryIndex, entry) { ----------------------
			
			
	 								 
		    // Create the chart
		    $('#chart-container').highcharts({
		        chart: {
		            type: 'column'
		        },
		        title: {
		            text: '제품별 판매 점유율'
		        },
		        subtitle: {
		           // text: 'Click the columns to view versions. Source: <a href="http://netmarketshare.com">netmarketshare.com</a>.'
		        },
		        xAxis: {
		            type: 'category'
		        },
		        yAxis: {
		            title: {
		                text: '점유율(%)'
		            }

		        },
		        legend: {
		            enabled: false
		        },
		        plotOptions: {
		            series: {
		                borderWidth: 0,
		                dataLabels: {
		                    enabled: true,
		                    format: '{point.y:.1f}%'
		                }
		            }
		        },

		        tooltip: {
		            headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
		            pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
		        },

		        series: [{
		            name: 'Brands',
		            colorByPoint: true,
		            data: jepumObjArr   // **** 위에서 구한값을 대입시킴. ****
		        }],
		        drilldown: { 
		        	series: jepumObjDetailArr  // **** 위에서 구한값을 대입시킴. ****
		         /* series: [{
		                name: jepumObjArr[0].name,  // *** 위에서 한 작업으로 키값이 JEPUMNAME 에서 name 으로 변경됨.
		                id: jepumObjArr[0].name,    // *** 위에서 한 작업으로 키값이 JEPUMNAME 에서 name 으로 변경됨.
		                data: [
		                    [
		                        'v11.0',
		                        24.13
		                    ],
		                    [
		                        'v8.0',
		                        17.2
		                    ],
		                    [
		                        'v9.0',
		                        8.11
		                    ],
		                    [
		                        'v10.0',
		                        5.33
		                    ],
		                    [
		                        'v6.0',
		                        1.06
		                    ],
		                    [
		                        'v7.0',
		                        0.5
		                    ]
		                ]
		            }, {
		                name: jepumObjArr[1].name,
		                id: jepumObjArr[1].name,
		                data: [
		                    [
		                        'v40.0',
		                        5
		                    ],
		                    [
		                        'v41.0',
		                        4.32
		                    ],
		                    [
		                        'v42.0',
		                        3.68
		                    ],
		                    [
		                        'v39.0',
		                        2.96
		                    ],
		                    [
		                        'v36.0',
		                        2.53
		                    ],
		                    [
		                        'v43.0',
		                        1.45
		                    ],
		                    [
		                        'v31.0',
		                        1.24
		                    ],
		                    [
		                        'v35.0',
		                        0.85
		                    ],
		                    [
		                        'v38.0',
		                        0.6
		                    ],
		                    [
		                        'v32.0',
		                        0.55
		                    ],
		                    [
		                        'v37.0',
		                        0.38
		                    ],
		                    [
		                        'v33.0',
		                        0.19
		                    ],
		                    [
		                        'v34.0',
		                        0.14
		                    ],
		                    [
		                        'v30.0',
		                        0.14
		                    ]
		                ]
		            }, {
		                name: jepumObjArr[2].name,
		                id: jepumObjArr[2].name,
		                data: [
		                    [
		                        'v35',
		                        2.76
		                    ],
		                    [
		                        'v36',
		                        2.32
		                    ],
		                    [
		                        'v37',
		                        2.31
		                    ],
		                    [
		                        'v34',
		                        1.27
		                    ],
		                    [
		                        'v38',
		                        1.02
		                    ],
		                    [
		                        'v31',
		                        0.33
		                    ],
		                    [
		                        'v33',
		                        0.22
		                    ],
		                    [
		                        'v32',
		                        0.15
		                    ]
		                ]
		            }, {
		                name: jepumObjArr[3].name,
		                id: jepumObjArr[3].name,
		                data: [
		                    [
		                        'v8.0',
		                        2.56
		                    ],
		                    [
		                        'v7.1',
		                        0.77
		                    ],
		                    [
		                        'v5.1',
		                        0.42
		                    ],
		                    [
		                        'v5.0',
		                        0.3
		                    ],
		                    [
		                        'v6.1',
		                        0.29
		                    ],
		                    [
		                        'v7.0',
		                        0.26
		                    ],
		                    [
		                        'v6.2',
		                        0.17
		                    ]
		                ]
		            }, {
		                name: jepumObjArr[4].name,
		                id: jepumObjArr[4].name,
		                data: [
		                    [
		                        'v12.x',
		                        0.34
		                    ],
		                    [
		                        'v28',
		                        0.24
		                    ],
		                    [
		                        'v27',
		                        0.17
		                    ],
		                    [
		                        'v29',
		                        0.16
		                    ]
		                ]
		            }] 
		        */
		        }  
		    });	 								 
		
		}); // end of $.getJSON("rankShowJSON.action", function(data) {} )-----------
	}// end of function sideinfo_setOrderRankChart() { }--------------------------

	
	function showRank(){
		
		sideinfo_setOrderRankChart();
		
	//	var timejugi = 30000;   // 시간을 30초 마다 자동 갱신하려고.
		
	//	setTimeout(function() {
	//			showRank();	
	//		}, timejugi);
	}// end of showRank()-------------------------
	
</script>

 
<div id="sideinfo_displayOrderRankTable" style="min-width: 90%; margin-top: 50px; margin-bottom: 50px; padding-left: 20px; padding-right: 20px;"></div>
<div id="chart-container" style="min-width: 90%; min-height: 400px; margin: 0 auto; border: solid #F0FFFF 5px;"></div>
	
	
	
	
	
	