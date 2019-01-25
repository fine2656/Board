package com.spring.product.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spring.product.model.InterProductDAO;

@Service
public class ProductService implements InterProductService{

	@Autowired
	private InterProductDAO dao;

	
	// 제품카테고리코드, 카테고리명 가져오기
	@Override
	public List<HashMap<String, String>> getCatecode() {
		List<HashMap<String, String>> cateCodeList = dao.getCatecode();
		return cateCodeList;
	}

	
	// 제품스펙코드, 제품스펙명 가져오기
	@Override
	public List<HashMap<String, String>> getSpeccode() {
		List<HashMap<String, String>> specCodeList = dao.getSpeccode();
		return specCodeList;
	}
	
	
	// 새로이 입력할 제품번호(시퀀스) 가져오기
	@Override
	public int getProdseq() {
		int prodseq = dao.getProdseq();
		return prodseq;
	}

	
	// 제품정보테이블에 제품정보 등록하기
	@Override
	public int addProduct(HashMap<String, String> mapProduct) {
		int n = dao.addProduct(mapProduct);
		return n;
	}

	
	// 제품이미지테이블에 제품이미지파일 등록하기
	@Override
	public int addProductimage(HashMap<String, String> mapProductimage) {
		int n = dao.addProductimage(mapProductimage);
		return n;
	}
	
	
	// 제품번호, 제품명 가져오기
	@Override
	public List<HashMap<String, String>> getProdseqList() {
		List<HashMap<String, String>> prodseqList = dao.getProdseqList();
		return prodseqList;
	}
	

	// 제품입고 테이블에 제품입고하기
    // 제품정보테이블에 입고된 양만큼 재고량 증가시키기
	@Override
	@Transactional(propagation=Propagation.REQUIRED, isolation= Isolation.READ_COMMITTED, rollbackFor={Throwable.class})
	public int addproductStore(HashMap<String, String> paraMap)
			 throws Throwable{
		int n = dao.insertProductibgo(paraMap); 
		int m = dao.updateProdqty(paraMap);
		
		return n*m;
	}

	
	// 썸네일파일명을 포함한 제품목록 가져오기
	@Override
	public List<HashMap<String, String>> getListProduct() {
		List<HashMap<String, String>> productList = dao.getListProduct();
		return productList;
	}

	
	// 특정제품의 정보 가져오기
	@Override
	public HashMap<String, String> getviewProduct(String fk_prodseq) {
		HashMap<String, String> viewProductmap = dao.getviewProduct(fk_prodseq);
		return viewProductmap;
	}
	
	
	// 특정제품의 원래이미지파일명 및 썸네일이미지파일명 가져오기 
	@Override
	public List<HashMap<String, String>> getviewProductImage(String fk_prodseq) {
		List<HashMap<String, String>> viewProductImageList = dao.getviewProductImage(fk_prodseq);
		return viewProductImageList;
	}

	
	// 특정 썸네일이미지파일명에 대한 원래이미지파일명 가져오기  
	@Override
	public String getLargeImgFilename(HashMap<String, String> map) {
		String imgFilename = dao.getLargeImgFilename(map);
		return imgFilename;
	}

	
	
	
}
