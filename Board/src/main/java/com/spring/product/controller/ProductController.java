package com.spring.product.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.spring.common.ThumbnailManager;
import com.spring.mail.GoogleMail;
import com.spring.member.model.MemberVO;
import com.spring.product.service.InterProductService;
import com.spring.common.FileManager;

@Controller
public class ProductController {

	@Autowired
	private InterProductService service;
	
	@Autowired
	private FileManager fileManager;
	
	// ===== #168. 썸네일을 다루어주는 클래스 및 e메일을 보내주는 의존객체 주입하기(DI: Dependency Injection) =====
	@Autowired
	private ThumbnailManager thumbnailManager;
	
	@Autowired
	private GoogleMail mail;
	
	
	// ===== #169. 다중첨부파일을 첨부해줄 수 있는 제품등록 폼페이지 요청.
	@RequestMapping(value="/product/addProduct.action", method={RequestMethod.GET} ) 
	public String requireLogin_addProduct(HttpServletRequest req, HttpServletResponse res) {
		
		HttpSession session = req.getSession();
		MemberVO loginuser = (MemberVO)session.getAttribute("loginuser");
		int gradelevel = loginuser.getGradelevel();
		
		if(gradelevel < 10) {
			session.invalidate();
			
			String msg = "접근이 불가합니다.";
			String loc = "javascript:history.back();";
			
			req.setAttribute("msg", msg);
			req.setAttribute("loc", loc);
			
			return "msg";
		}
		else {
			  List<HashMap<String, String>> cateCodeList = service.getCatecode();
			  
			  List<HashMap<String, String>> specCodeList = service.getSpeccode();
			  			  
			  req.setAttribute("cateCodeList", cateCodeList);
			  
			  req.setAttribute("specCodeList", specCodeList);
			
			  return "product/addProduct.tiles1";  
			  // ==> /Board/src/main/webapp/WEB-INF/views/tiles1/product/addProduct.jsp 파일을 생성한다.	
		}
	}
	
	// ======== !!! 제품1개에 이미지 첨부 다중파일 업로드하기 !!! =========== //
	@RequestMapping(value="/product/addProductEnd.action", method={RequestMethod.POST} ) 
	public String addDoubleFileEnd(MultipartHttpServletRequest req, HttpServletResponse res) {
		
		String prodseq = String.valueOf(service.getProdseq()); // 먼저 제품일련번호 채번해오기
		String prodname = req.getParameter("prodname");
		String fk_catecode = req.getParameter("fk_catecode");
		String prodcompany = req.getParameter("prodcompany");
		String price = req.getParameter("price");
		String saleprice = req.getParameter("saleprice");
		String fk_speccode = req.getParameter("fk_speccode");
		String prodcontent = req.getParameter("prodcontent");
		String prodpoint = req.getParameter("prodpoint");
		
		List<MultipartFile> attachList = req.getFiles("attach"); // !!! 이미지첨부 다중파일 !!!
		
		
		HashMap<String, String> productMap = new HashMap<String, String>(); // 제품1개 
		productMap.put("PRODSEQ", prodseq);
		productMap.put("PRODNAME", prodname);
		productMap.put("FK_CATECODE", fk_catecode);
		productMap.put("PRODCOMPANY", prodcompany);
		productMap.put("PRICE", price);
		productMap.put("SALEPRICE", saleprice);
		productMap.put("FK_SPECCODE", fk_speccode);
		productMap.put("PRODCONTENT", prodcontent);
		productMap.put("PRODPOINT", prodpoint);
		
		List<HashMap<String, String>> productImageMapList = new ArrayList<HashMap<String, String>>();
		
		if(attachList != null) {  // 이미지첨부 다중파일을 받아왔다라면 
			// 이미지첨부 다중파일을 업로드할 WAS 의 webapp 의 절대경로를 알아와야 한다. 
			HttpSession session = req.getSession();
			String root = session.getServletContext().getRealPath("/"); 
			String path = root + "resources"+File.separator+"files";
			// path 가 첨부파일들을 저장할 WAS(톰캣)의 폴더가 된다. 
			
			String newFileName = ""; // WAS(톰캣) 디스크에 저장할 파일명 
			
			byte[] bytes = null;    // 첨부파일을 WAS(톰캣) 디스크에 저장할때 사용되는 용도 
			
			long fileSize = 0;   	// 파일크기를 읽어오기 위한 용도
			
			String thumbnailFileName = "";  // WAS 디스크에 저장될 thumbnail 파일명 
			
			for(int i=0; i<attachList.size(); i++) { 
				try {
					 bytes = attachList.get(i).getBytes(); // 첨부파일의 내용물(byte)을 읽어옴.
					 
					 // 파일업로드 한 후 업로드되어진 파일명  가져오기
					 newFileName = fileManager.doFileUpload(bytes, attachList.get(i).getOriginalFilename(), path);
					 // 예를들어 newFileName 에는  2019012519592316420706146795.png 와 같은 것이 들어옴.
					 
					 fileSize = attachList.get(i).getSize(); // 첨부파일의 파일크기를 알아온다.
					 
					// ============= >>>> 첨부파일에 대한 thumbnail 파일 생성하기 <<<< =====================  //
					 thumbnailFileName = thumbnailManager.doCreateThumbnail(newFileName, path); 
					// ==============================================================================  //
					 
					 HashMap<String, String> prooductImageMap = new HashMap<String, String>();
					// 제품1개에 딸린 다중 이미지
					 
					 prooductImageMap.put("FK_PRODSEQ", prodseq);
					 prooductImageMap.put("IMAGEFILENAME", newFileName);
					 prooductImageMap.put("IMAGEORGFILENAME", attachList.get(i).getOriginalFilename());
					 prooductImageMap.put("IMAGEFILESIZE", String.valueOf(fileSize));
					 prooductImageMap.put("THUMBNAILFILENAME", thumbnailFileName);
					 
					 productImageMapList.add(prooductImageMap);
					 
				} catch (Exception e) {	}
				
			}// end of for------------------------------------------------
			
		}// end of if---------------------------------------
		
		
	  // **** 폼에서 입력받은 제품입력정보 값을 
	  //      Service 단으로 넘겨서 테이블에 insert 하기로 한다.
		   
	       // 이미지파일첨부가 없는 경우 또는 이미지파일첨부가 있는 경우로 나누어서
		   // Service 단으로 호출하기
		   int n = 0;
		   int m = 0;
		   int count = 0;
		   
		   if(attachList == null) { // 파일이 첨부된것이 없다면
			   n = service.addProduct(productMap);
		   }
		   else if(attachList != null) { // 파일이 첨부된것이 있다면
			   n = service.addProduct(productMap);
			   
			   for(int i=0; i<productImageMapList.size(); i++) {
				   m = service.addProductimage(productImageMapList.get(i));
				   if(m==1) count++;
			   }
			   
			   if(productImageMapList.size() == count) {
				   n=1;
			   }
			   else {
				   n=0;
			   }
		   }
		   			   
		   String msg = "";
		   String loc = "";
		   if(n==1) {
			   msg = "제품입력 성공!!";
			   loc = "/board/product/listProduct.action;";
		   }
		   else {
			   msg = "제품입력 실패!!";
			   loc = "javascript:history.back();";
		   }
		    		
		   req.setAttribute("msg", msg);
		   req.setAttribute("loc", loc);
			
		   return "msg";
	}
	
	
	// ======= 제품목록 보기 ======= //
	@RequestMapping(value="/product/listProduct.action", method={RequestMethod.GET} ) 
	public String listProduct(HttpServletRequest req) {
	
		List<HashMap<String, String>> productList = service.getListProduct();
		
		req.setAttribute("productList", productList);
		
		return "product/listProduct.tiles1";
		// ==>  /Board/src/main/webapp/WEB-INF/views/tiles1/product/listProduct.jsp  파일을 생성한다.	
	}
	
	
	// ======= 제품상세 보기 ======= //
	@RequestMapping(value="/product/viewProduct.action", method={RequestMethod.GET} ) 
	public String viewProduct(HttpServletRequest req) {
	
		String fk_prodseq = req.getParameter("fk_prodseq");
		
		HashMap<String, String> viewProductMap = service.getviewProduct(fk_prodseq); // 제품정보
		req.setAttribute("viewProductMap", viewProductMap);
		
		List<HashMap<String, String>> viewProductImageList = service.getviewProductImage(fk_prodseq); // 제품다중이미지파일
		req.setAttribute("viewProductImageList", viewProductImageList);
		
		return "product/view.tiles1";
		// ==>  /Board/src/main/webapp/WEB-INF/views/tiles1/product/view.jsp  파일을 생성한다.	
	}
	
	
	// >>> 썸네일 이미지 클릭시 spring_productimag 테이블에 저장된 원래크기의 이미지 파일이름을  
    //     조회해 와서 JSON 타입으로 변경하여 넘겨주는 요청 생성하기 <<<
    @RequestMapping(value="/product/getLargeImgFilename.action", method={RequestMethod.GET} ) 
    @ResponseBody
    public HashMap<String, String> getLargeImgFilename(HttpServletRequest req) {
		
		String fk_prodseq = req.getParameter("fk_prodseq");               // 제품번호(fk_prodseq)
		String thumbnailFileName = req.getParameter("thumbnailFileName"); // 썸네일파일명
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("fk_prodseq", fk_prodseq);
		map.put("thumbnailFileName", thumbnailFileName);
		
		
		String imgFilename = service.getLargeImgFilename(map); 
		// spring_productimage 테이블에서 
		// 제품번호(fk_prodseq), 썸네일파일명(thumbnailFileName)에 해당하는 imagefilename 컬럼의 값(201706110056542344396781698764.jpg) 가져오기 
		
		HashMap<String, String> returnmap = new HashMap<String, String>(); 
		
		returnmap.put("IMGFILENAME", imgFilename);
		
		return returnmap;
		
	}// end of getLargeImgFilename(HttpServletRequest req)------------------
    
	

	// ====== 제품입고 폼페이지 요청하기 =======
	@RequestMapping(value="/product/storeProduct.action", method={RequestMethod.GET} ) 
	public String requireLogin_productStore(HttpServletRequest req, HttpServletResponse res) {
		
		HttpSession session = req.getSession();
		MemberVO loginuser = (MemberVO)session.getAttribute("loginuser");
		int gradelevel = loginuser.getGradelevel();
		
		if(gradelevel < 10) {
			session.invalidate(); // 강제로 로그아웃 시키기
			
			String msg = "접근이 불가합니다.";
			String loc = "javascript:history.back();";
			
			req.setAttribute("msg", msg);
			req.setAttribute("loc", loc);
			
			return "msg";
		}
		else {
			List<HashMap<String, String>> prodseqList = service.getProdseqList();
			req.setAttribute("prodseqList", prodseqList);
		
			return "product/storeProduct.tiles1";
		}
	}
	
	
	// ====== 제품입고 완료 요청하기 ======
	@RequestMapping(value="/product/storeProductEnd.action", method={RequestMethod.POST} ) 
	public String productStoreEnd(HttpServletRequest req, HttpServletResponse res) 
			throws Throwable {
		
		String prodseq = req.getParameter("prodseq");
		String ibgoqty = req.getParameter("ibgoqty");
		
		HashMap<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("PRODSEQ", prodseq);
		paraMap.put("IBGOQTY", ibgoqty);
		
		int n = service.addproductStore(paraMap);
				   			   
		String msg = "";
		String loc = "";
		
		if(n==1) {
		   msg = "입고 성공!!";
		   loc = "/board/product/listProduct.action;";
		   
		// ======= ***** 제품 입고가 완료되었다라는 email 보내기 시작 ***** ======= //
		   	// GoogleMail mail = new GoogleMail();
		    // 위에서 선언된 mail 이라는 의존객체를 사용할 것임.
		   	
		    // 제품번호에 해당하는 제품명, 제조사를 얻어옴.
		   	HashMap<String, String> viewProductMap = service.getviewProduct(prodseq);
		   	String prodname = viewProductMap.get("PRODNAME");       // 제품명
		   	String prodcompany = viewProductMap.get("PRODCOMPANY"); // 제조사명
				
		    // 제품번호에 해당하는 썸네일이미지 파일명을 얻어옴.
			List<HashMap<String, String>> viewProductImageList = service.getviewProductImage(prodseq);
			String[] thumbnailfilenameArr = new String[viewProductImageList.size()]; 
						
			for(int i=0; i<viewProductImageList.size(); i++) {
				thumbnailfilenameArr[i] = viewProductImageList.get(i).get("THUMBNAILFILENAME");
			}
			
			StringBuilder sb = new StringBuilder(); 
			sb.append("<table style='border: solid gray 1px; border-collapse: collapse; width: 800px;'>");
			sb.append("<tr style='border: solid gray 1px; border-collapse: collapse;'>");
			sb.append("<td style='border: solid gray 1px; border-collapse: collapse; text-align: center; font-weight: bolder; '>제품번호</td>");
			sb.append("<td>"+prodseq+"</td>");
			sb.append("</tr>");
			sb.append("<tr style='border: solid gray 1px; border-collapse: collapse;'>");
			sb.append("<td style='border: solid gray 1px; border-collapse: collapse; text-align: center; font-weight: bolder; '>제품명</td>");
			sb.append("<td>"+prodname+"<br/>");
				for (int i=0; i<thumbnailfilenameArr.length; i++) {
			   	  	sb.append("<img src=\"http://127.0.0.1:9090/board/resources/files/"+thumbnailfilenameArr[i]+"\"&nbsp;/>");
				}
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("<tr style='border: solid gray 1px; border-collapse: collapse;'>");
			sb.append("<td style='border: solid gray 1px; border-collapse: collapse; text-align: center; font-weight: bolder; '>제조사</td>");
			sb.append("<td>"+prodcompany+"</td>");
			sb.append("</tr>");
			sb.append("<tr style='border: solid gray 1px; border-collapse: collapse;'>");
			sb.append("<td style='border: solid gray 1px; border-collapse: collapse; text-align: center; font-weight: bolder; '>입고량</td>"); 
			sb.append("<td>"+ibgoqty+"</td>");
			sb.append("</tr>");
			sb.append("</table>");
			
		   	sb.append("<br/>입고 되었습니다.");
		   	
		   	String emailContents = sb.toString();
		   	
		   	HttpSession session = req.getSession();
		   	String emailAddress = ((MemberVO)session.getAttribute("loginuser")).getEmail();
		   	
		   	mail.sendmail_IbgoFinish(emailAddress, emailContents);
		 // ======= ***** 제품입고가 완료되었다라는 email 보내기 끝 ***** ======= //
		   	
		}
		else {
		   msg = "입고 실패!!";
		   loc = "javascript:history.back();";
		}
		    		
		req.setAttribute("msg", msg);
		req.setAttribute("loc", loc);
			
		return "msg";
	}
	
	
}









