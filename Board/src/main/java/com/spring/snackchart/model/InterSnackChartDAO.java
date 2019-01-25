package com.spring.snackchart.model;

import java.util.HashMap;
import java.util.List;

public interface InterSnackChartDAO {

	List<HashMap<String, String>> getSnackNameList();
	
	List<HashMap<String,String>> getSnackTypeCodeList(String snackno);
	
	int insert_snackOrder(String userid);                         // chart_snackOrder 테이블에 insert 하기
	int insert_snackOrderDetail(HashMap<String, String> paraMap); // chart_snackOrderDetail 테이블에 insert 하기  
	
	List<HashMap<String, String>> snackorderRankList();
	
	List<HashMap<String, String>> getSnackDetailnameNpercentList(String snackname);
	
	List<HashMap<String, String>> my_snackorderStatistics(String userid);	
	
}
