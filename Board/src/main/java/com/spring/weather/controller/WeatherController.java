package com.spring.weather.controller;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Component
public class WeatherController {
	// *** 기상청 오픈 API XML 호출 ***
	// http://www.kam.go.kr/XML/weather/sfc_web_map.xml
	
	@RequestMapping(value="weatherXML.action", method= {RequestMethod.GET})
	public String weatherXML() {
		///Board/src/main/webapp/WEB-INF/views/xml/weatherXML.jsp
		return "/xml/weatherXML";
	}
	
	
}
