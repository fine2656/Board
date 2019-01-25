package com.spring.common;

import java.io.File;
import java.util.Calendar;

import org.springframework.stereotype.Component;

import net.coobird.thumbnailator.Thumbnails;

// ===== #167. 썸네일을 다루어주는 클래스 생성하기 =====
@Component
public class ThumbnailManager {

	public String doCreateThumbnail(String filename, String path) 
		throws Exception {
		
		String thumbnailFileName = null;
		
		// 업로드한 파일의 이름 ==> 2019012519592316420706146795.png
		if(filename.equals(""))
			return null;
		 
		
		// 확장자(.png)가 무엇인지 가져오기       // filename.lastIndexOf(".") 는 문자열 filename을 처음부터 검색하여 맨마지막에 나오는 . 의 위치값을 알아오는 것이다. 
		String fileExt = filename.substring(filename.lastIndexOf("."));
		// 문자열.lastIndexOf("검색어", 검색을 시작할 위치 인덱스)
		// 문자열.lastIndexOf("검색어", 0)
		// 문자열.lastIndexOf("검색어") -- 검색을 시작할 위치 인덱스를 생략하면 기본적으로 0 이 된다.
		// ==> 문자열에서 탐색(검색)하는 문자열이 마지막으로 등장하는 위치에 대한 index를 반환.
		// 
		// 문자열.indexOf("검색어", 검색을 시작할 위치 인덱스)는  
		// 문자열.indexOf("검색어", 0)
		// 문자열.indexOf("검색어") -- 검색을 시작할 위치 인덱스를 생략하면 기본적으로 0 이 된다.
		// ==> 문자열에서 탐색(검색)하는 문자열이 처음 발견되는 문자열에 대한 index를 반환.
		if(fileExt == null || fileExt.equals(""))
			return null;
		
		// 서버에 저장할 새로운 thumbnailFileName 파일명(랜덤하게 고유한 숫자로 되어진 파일명임)을 만든다.
		thumbnailFileName = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", 
				                          Calendar.getInstance());
		thumbnailFileName += System.nanoTime();
		thumbnailFileName += fileExt;
		
		// 만약에 업로드할 경로가 존재하지 않는 경우라면 폴더를 생성 한다.
		File dir = new File(path);
		if(!dir.exists())
			dir.mkdirs();
		
		String pathFilename = path + File.separator + filename; // 경로까지 있는 원본 파일 이름
		String pathThumbnailFileName = path + File.separator + thumbnailFileName; // 썸네일 파일 이름
		
		File image = new File(pathFilename);                // 원래 이미지파일(경로명포함)
		File thumbnail = new File(pathThumbnailFileName);   // 섬네일 되어진 이미지파일(경로명포함) 
		
		if(image.exists()) {  // 원래 파일이 존재한다라면  
		    Thumbnails.of(image).size(100, 100).outputFormat(fileExt.substring(1)).toFile(thumbnail); 
		    // 원래 파일(경로명포함)의 이미지내용을 width 100, height 100 의 크기로 만들어 섬네일 되어진 이미지파일(경로명포함)을 생성해준다.
		    // 해당경로에 파일(경로명포함)을 생성해주므로 파일을 업로드된 것처럼 보인다. // .outputFormat(fileExt.substring(1)).toFile(thumbnail); : 파일 만들기
		    
		}
		
		return thumbnailFileName;  // 섬네일 되어진 이미지파일을 리턴시켜준다. 
	}
	
}
