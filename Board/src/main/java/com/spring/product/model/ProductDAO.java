package com.spring.product.model;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProductDAO implements InterProductDAO {

	@Autowired
	private SqlSessionTemplate sqlsession;

	
	// 제품카테고리코드, 카테고리명 가져오기
	@Override
	public List<HashMap<String, String>> getCatecode() {
		List<HashMap<String, String>> cateCodeList = sqlsession.selectList("product.getCatecode");
		return cateCodeList;
	}

	
	// 제품스펙코드, 제품스펙명 가져오기
	@Override
	public List<HashMap<String, String>> getSpeccode() {
		List<HashMap<String, String>> specCodeList = sqlsession.selectList("product.getSpeccode");
		return specCodeList;
	}
	
	
	// 새로이 입력할 제품번호(시퀀스) 가져오기
	@Override
	public int getProdseq() {
		int prodseq = sqlsession.selectOne("product.getProdseq");
		return prodseq;
	}

	
	// 제품정보테이블에 제품정보 등록하기
	@Override
	public int addProduct(HashMap<String, String> mapProduct) {
		int n = sqlsession.insert("product.addProduct", mapProduct);
		return n;
	}

	
	// 제품이미지테이블에 제품이미지파일 등록하기
	@Override
	public int addProductimage(HashMap<String, String> mapProductimage) {
		int n = sqlsession.insert("product.addProductimage", mapProductimage);
		return n;
	}
	
	
	// 제품번호, 제품명 가져오기
	@Override
	public List<HashMap<String, String>> getProdseqList() {
		List<HashMap<String, String>> prodseqList = sqlsession.selectList("product.getProdseqList");
		return prodseqList;
	}

	
	// 제품입고 테이블에 제품입고하기
	@Override
	public int insertProductibgo(HashMap<String, String> paraMap) {
		int n = sqlsession.insert("product.insertProductibgo", paraMap);
		return n;
	}
	
	
	// 제품정보테이블에 입고된 양만큼 재고량 증가시키기
	@Override
	public int updateProdqty(HashMap<String, String> paraMap) {
		int n = sqlsession.update("product.updateProdqty", paraMap);
		return n;
	}
	
	
	// 썸네일파일명을 포함한 제품목록 가져오기
	@Override
	public List<HashMap<String, String>> getListProduct() {
		List<HashMap<String, String>> productList = sqlsession.selectList("product.getListProduct");
		return productList;
	}

	
	// 특정제품의 정보 가져오기
	@Override
	public HashMap<String, String> getviewProduct(String fk_prodseq) {
		HashMap<String, String> viewProductmap = sqlsession.selectOne("product.getviewProduct", fk_prodseq);  
		return viewProductmap;
	}	
	
	
	// 특정제품의 원래이미지파일명 및 썸네일이미지파일명 가져오기 
	@Override
	public List<HashMap<String, String>> getviewProductImage(String fk_prodseq) {
		List<HashMap<String, String>> viewProductImageList = sqlsession.selectList("product.getviewProductImage", fk_prodseq);  
		return viewProductImageList;
	}

	
	// 특정 썸네일이미지파일명에 대한 원래이미지파일명 가져오기 
	@Override
	public String getLargeImgFilename(HashMap<String, String> map) {
		String imgFilename = sqlsession.selectOne("product.getLargeImgFilename", map);
		return imgFilename;
	}

	

	

	

	
	
	
	
}
