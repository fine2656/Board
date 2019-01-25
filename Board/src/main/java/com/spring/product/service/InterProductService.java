package com.spring.product.service;

import java.util.HashMap;
import java.util.List;

public interface InterProductService {

	List<HashMap<String, String>> getCatecode(); // 제품카테고리코드, 카테고리명 가져오기
	
	List<HashMap<String, String>> getSpeccode(); // 제품스펙코드, 제품스펙명 가져오기
	
	int getProdseq(); // 새로이 입력할 제품번호(시퀀스) 가져오기
	
	int addProduct(HashMap<String, String> mapProduct); // 제품정보테이블에 제품정보 등록하기
	
	int addProductimage(HashMap<String, String> mapProductimage); // 제품이미지테이블에 제품이미지파일 등록하기
	
	List<HashMap<String, String>> getProdseqList(); // 제품번호, 제품명 가져오기
	
	int addproductStore(HashMap<String, String> paraMap) throws Throwable; // 제품입고 테이블에 제품입고하기
                                                                           // 제품정보테이블에 입고된 양만큼 재고량 증가시키기
	
	List<HashMap<String, String>> getListProduct(); // 썸네일파일명을 포함한 제품목록 가져오기
	
	HashMap<String, String> getviewProduct(String fk_prodseq); // 특정제품의 정보 가져오기
	List<HashMap<String, String>> getviewProductImage(String fk_prodseq); // 특정제품의 원래이미지파일명 및 썸네일이미지파일명 가져오기 
	
	String getLargeImgFilename(HashMap<String, String> map); // 특정 썸네일이미지파일명에 대한 원래이미지파일명 가져오기 
		
}
