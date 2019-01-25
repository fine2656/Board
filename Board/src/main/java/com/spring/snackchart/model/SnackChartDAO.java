package com.spring.snackchart.model;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SnackChartDAO implements InterSnackChartDAO {

	@Autowired
	private SqlSessionTemplate sqlsession;

	@Override
	public List<HashMap<String, String>> getSnackNameList() {
		List<HashMap<String, String>> list = sqlsession.selectList("snackChart.getSnackNameList");
		return list;
	}

	@Override
	public List<HashMap<String, String>> getSnackTypeCodeList(String snackno) {
		List<HashMap<String, String>> list = sqlsession.selectList("snackChart.getSnackTypeCodeList", snackno);
		return list;
	}

	@Override
	public int insert_snackOrder(String userid) {
		int n = sqlsession.insert("snackChart.insert_snackOrder", userid);   // chart_snackOrder 테이블에 insert 하기
		return n;
	}

	@Override
	public int insert_snackOrderDetail(HashMap<String, String> paraMap) {
		int n = sqlsession.insert("snackChart.insert_snackOrderDetail", paraMap); // chart_snackOrderDetail 테이블에 insert 하기  
		return n;
	}

	@Override
	public List<HashMap<String, String>> snackorderRankList() {
		List<HashMap<String, String>> list = sqlsession.selectList("snackChart.snackorderRankList");
		return list;
	}

	
	@Override
	public List<HashMap<String, String>> getSnackDetailnameNpercentList(String snackname) {
		List<HashMap<String, String>> snackDetailnameNpercentList = sqlsession.selectList("snackChart.getSnackDetailnameNpercentList", snackname); 
		return snackDetailnameNpercentList;
	}
	
	
	@Override
	public List<HashMap<String, String>> my_snackorderStatistics(String userid) {
		List<HashMap<String, String>> list = sqlsession.selectList("snackChart.my_snackorderStatistics", userid);
		return list;
	}
	
}
