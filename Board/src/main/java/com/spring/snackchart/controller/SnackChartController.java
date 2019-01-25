package com.spring.snackchart.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.snackchart.service.SnackChartService;
import com.spring.member.model.MemberVO;

@Controller
public class SnackChartController {

	@Autowired
	private SnackChartService service;
	
	@RequestMapping(value="/snackchart/snackorder.action", method={RequestMethod.GET})
	public String requireLogin_snackorder(HttpServletRequest req, HttpServletResponse res) {
		
		List<HashMap<String,String>> snackNameList = service.getSnackNameList();
		
		req.setAttribute("snackNameList", snackNameList);
		
		return "snackchart/snackorder.tiles3";
	}
	
	
	@RequestMapping(value="/snackchart/getSnackTypeCode.action", method={RequestMethod.GET})
	@ResponseBody
	public List<HashMap<String, Object>> getSnackTypeCode(HttpServletRequest req) {
		
		List<HashMap<String, Object>> returnmapList = new ArrayList<HashMap<String, Object>>(); 
		
		String snackno = req.getParameter("snackno");
		List<HashMap<String,String>> list = service.getSnackTypeCodeList(snackno);
					
		if(list != null) {
			for(HashMap<String,String> datamap : list) {
				HashMap<String, Object> submap = new HashMap<String, Object>(); 
				submap.put("TYPECODE", datamap.get("TYPECODE"));
				submap.put("TYPENAME", datamap.get("TYPENAME"));
				
				returnmapList.add(submap);
			}
		}
		
		return returnmapList;
	}
		
	
	@RequestMapping(value="/snackchart/snackorderEnd.action", method={RequestMethod.POST})
	public void snackorderEnd(HttpServletRequest req, HttpServletResponse res) 
		throws Throwable{
		
		String snackno = req.getParameter("snackno");
		String typecode = req.getParameter("typecode");
		String oqty = req.getParameter("oqty");
		String userid = req.getParameter("userid");
				
		HashMap<String, String> paraMap = new HashMap<String, String>();
		
		paraMap.put("SNACKNO", snackno);
		paraMap.put("TYPECODE", typecode);
		paraMap.put("OQTY", oqty);
		paraMap.put("USERID", userid);
		
		try {
			service.snackorderEnd(paraMap);
		}
		catch(Exception e) {
			String msg = "주문실패!!";
			String loc = "javascript:history.back();";
			
			req.setAttribute("loc", loc);
			req.setAttribute("msg", msg);
			
			RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/msg.jsp");
			dispatcher.forward(req, res);
		}
		
	}
	
	
	
	@RequestMapping(value="/snackchart/snackorderRank.action", method={RequestMethod.GET})
	@ResponseBody
	public List<HashMap<String, Object>> snackorderRank(HttpServletRequest req) {
		
		List<HashMap<String, Object>> returnmapList = new ArrayList<HashMap<String, Object>>(); 
		
		List<HashMap<String,String>> list = service.snackorderRankList();
					
		if(list != null) {
			for(HashMap<String,String> datamap : list) {
				HashMap<String, Object> submap = new HashMap<String, Object>(); 
				submap.put("RANKING",    datamap.get("RANKING"));
				submap.put("SNACKNAME",  datamap.get("SNACKNAME"));
				submap.put("TOTALQTY",   datamap.get("TOTALQTY"));
				submap.put("PERCENTAGE", datamap.get("PERCENTAGE"));
				
				returnmapList.add(submap);
			}
		}
		
		return returnmapList;
	}
	
	
	@RequestMapping(value="/snackchart/detailRankJsonBySnackname.action", method={RequestMethod.GET})
	@ResponseBody
	public List<HashMap<String, Object>> detailRankJsonBySnackname(HttpServletRequest req) {
		
		String snackname = req.getParameter("snackname");
		
		List<HashMap<String, Object>> jsonMapList = new ArrayList<HashMap<String, Object>>();
		
		if(snackname != null && !snackname.trim().isEmpty()) {
			List<HashMap<String,String>> snackDetailnameNpercentList = service.getSnackDetailnameNpercentList(snackname);
			
			if(snackDetailnameNpercentList != null) {
				for(HashMap<String,String> map : snackDetailnameNpercentList) {
					
					HashMap<String, Object> jsonMap = new HashMap<String, Object>();
					
					String typename = map.get("TYPENAME");
					String percent = map.get("PERCENT");
									
					jsonMap.put("TYPENAME", typename);
					jsonMap.put("PERCENT", percent);
					
					jsonMapList.add(jsonMap);
				}
			}
		}
		
		return jsonMapList;
	}
	//  http://localhost:9090/board/snackchart/detailRankJsonBySnackname.action?snackname=감자깡     으로 확인할 것
	/*
	    [{"TYPENAME":"달콤맛","PERCENT":"4.6"},{"TYPENAME":"매운맛","PERCENT":"4.6"},{"TYPENAME":"순한맛","PERCENT":"11.6"}]   
	 */
	
	
	@RequestMapping(value="/snackchart/my_snackorderchart.action", method={RequestMethod.GET})
	public String requireLogin_myorderchart(HttpServletRequest req, HttpServletResponse res) {
		
		return "snackchart/mysnackorderchart.tiles3";
	}
	
	
	@RequestMapping(value="/snackchart/my_snackorderStatistics.action", method={RequestMethod.GET})
	@ResponseBody
	public List<HashMap<String, Object>> my_snackorderStatistics(HttpServletRequest req, HttpServletResponse res) {
		
		List<HashMap<String, Object>> returnmapList = new ArrayList<HashMap<String, Object>>(); 
		
		HttpSession session = req.getSession();
		MemberVO loginuser = (MemberVO)session.getAttribute("loginuser");
		
		String userid = loginuser.getUserid();
		
		List<HashMap<String,String>> list = service.my_snackorderStatistics(userid);
					
		if(list != null) {
			for(HashMap<String,String> datamap : list) {
				HashMap<String, Object> submap = new HashMap<String, Object>(); 
				submap.put("SNACKNAME",  datamap.get("SNACKNAME"));
				submap.put("TOTALQTY",   datamap.get("TOTALQTY"));
				submap.put("PERCENTAGE", datamap.get("PERCENTAGE"));
				
				returnmapList.add(submap);
			}
		}
		
		return returnmapList;
	}
	
}

