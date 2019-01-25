package com.spring.snackchart.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spring.snackchart.model.SnackChartDAO;

@Service
public class SnackChartService implements InterSnackChartService {

	@Autowired
	private SnackChartDAO dao;

	@Override
	public List<HashMap<String, String>> getSnackNameList() {
		List<HashMap<String, String>> list = dao.getSnackNameList();
		return list;
	}

	@Override
	public List<HashMap<String, String>> getSnackTypeCodeList(String snackno) {
		List<HashMap<String, String>> list = dao.getSnackTypeCodeList(snackno);
		return list;
	}

	
	// >>>>> 트랜잭션처리를 해야할 메소드에 @Transactional 어노테이션을 설정하면 된다. rollbackFor={Throwable.class} 은 롤백을 해야할 범위를 말하는데 Throwable.class 은 error 및 exception 을 포함한 최상위 루트이다. 즉, 해당 메소드 실행시 발생하는 모든 error 및 exception 에 대해서 롤백을 하겠다는 말이다.  
	@Override
	@Transactional(propagation=Propagation.REQUIRED, isolation= Isolation.READ_COMMITTED, rollbackFor={Throwable.class})  
	public void snackorderEnd(HashMap<String, String> paraMap) 
			throws Throwable {
		
		String userid = paraMap.get("USERID");
		
		dao.insert_snackOrder(userid);        // chart_snackOrder 테이블에 insert 하기
		
		dao.insert_snackOrderDetail(paraMap); // chart_snackOrderDetail 테이블에 insert 하기 
		
	}

	@Override
	public List<HashMap<String, String>> snackorderRankList() {
		List<HashMap<String, String>> list = dao.snackorderRankList();
		return list;
	}
	
	
	@Override
	public List<HashMap<String,String>> getSnackDetailnameNpercentList(String snackname) {
		
		List<HashMap<String, String>> snackDetailnameNpercentList = dao.getSnackDetailnameNpercentList(snackname); 
		
		return snackDetailnameNpercentList;
	}
	

	@Override
	public List<HashMap<String, String>> my_snackorderStatistics(String userid) {
		List<HashMap<String, String>> list = dao.my_snackorderStatistics(userid);
		return list;
	}
	
	
	
}
