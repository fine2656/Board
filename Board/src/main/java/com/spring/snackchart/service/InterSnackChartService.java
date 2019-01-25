package com.spring.snackchart.service;

import java.util.HashMap;
import java.util.List;

public interface InterSnackChartService {

	List<HashMap<String,String>> getSnackNameList();
	
	List<HashMap<String,String>> getSnackTypeCodeList(String snackno);
	
	void snackorderEnd(HashMap<String,String> paraMap) throws Throwable;
	
	List<HashMap<String,String>> snackorderRankList();
	
	List<HashMap<String,String>> getSnackDetailnameNpercentList(String snackname);
	
	List<HashMap<String,String>> my_snackorderStatistics(String userid);	
	
}
