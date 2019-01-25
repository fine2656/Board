package com.spring.board.model;

import org.springframework.web.multipart.MultipartFile;

//==== #스마트에디터2. 단일/다중사진 파일 업로드를 위한 VO 생성하기  ====

public class PhotoVO {
		
 	private MultipartFile Filedata;     
	//photo_uploader.html페이지의 form태그내에 존재하는 file 태그의 name명과 일치시켜줌

	private String callback;
		//callback URL
	
	private String callback_func;
		//콜백함수??
	
	public MultipartFile getFiledata() {
		return Filedata;
	}

	public void setFiledata(MultipartFile filedata) {
		Filedata = filedata;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String getCallback_func() {
		return callback_func;
	}

	public void setCallback_func(String callback_func) {
		this.callback_func = callback_func;
	}
	
}
