package com.spring.product.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spring.common.AES256;
import com.spring.mail.GoogleMail;
import com.spring.product.model.InterProductDAO;

@Service
public class ProductService implements InterProductService{

	@Autowired
	private InterProductDAO dao;

	@Autowired
	private GoogleMail mail;
	
	@Autowired
	private AES256 aes;
	
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

	
	  //////////////////////////////////////////////////////////////////////////////////////////////
		/*
		     스케줄은 3가지 종류  cron, fixedDelay, fixedRate 가 있다. 
		   
		   
		   @Scheduled(cron="0 0 0 * * ?")
		   cron 스케줄에 따라서 일정기간에 시작한다. 매일 자정마다 (00:00:00)에 실행한다.
		   
		   >>> cron 표기법 <<<
		   	     
		         문자열의 좌측부터 우측까지 아래처럼 의미가 부여되고 각 항목은 공백 문자로 구분한다.

	                순서는 초 분 시 일 월 요일명 이다.
	         ----------------------------------------------------------------------------------------------------------------    
	   의미             초               분              시             일                         월             요일명                                                                   년도
	         ----------------------------------------------------------------------------------------------------------------
	 사용가능한	0~59     0~59     0~23      1~31           1~12      1~7 (1=>일요일, 2=>월요일, ... 7=>토요일)     1970 ~ 2099 
		값           - * /    - * /    - * /     - * ? / L W    - * /     - * ? / L #
		
		     * 는 모든 수를 의미.
		    
		     ? 는 해당 항목을 사용하지 않음.  
	                   일에서 ?를 사용하면 월중 날짜를 지정하지 않음. 요일명에서 ?를 사용하면 주중 요일을 지정하지 않음.
	                   
	         - 는 기간을 설정. 시에서 10-12이면 10시, 11시, 12시에 동작함.
	                                          분에서 57-59이면 57분, 58분, 59분에 동작함.

	         , 는 특정 시간을 지정함. 요일명에서 2,4,6 은 월,수,금에만 동작함.

	         / 는 시작시간과 반복 간격 설정함. 초위치에 0/15로 설정하면 0초에 시작해서 15초 간격으로 동작함. 
	                                                               분위치에 5/10으로 설정하면 5분에 시작해서 10분 간격으로 동작함.

	         L 는 마지막 기간에 동작하는 것으로 일과 요일명에서만 사용함. 일위치에 사용하면 해당월의 마지막 날에 동작함.
	                                                                                                   요일명에 사용하면 토요일에 동작함.

	         W 는 가장 가까운 평일에 동작하는 것으로 일에서만 사용함.  일위치에 15W로 설정하면 15일이 토요일이면 가장 가까운 14일 금요일에 동작함.
	                                                                                               일위치에 15W로 설정하고 15일이 일요일이면 16일에 동작함.
	                                                                                               일위치에 15W로 설정하고 15일이 평일이면 15일에 동작함.
	                                                                                            
	         LW 는 L과 W의 조합.                             그달의 마지막 평일에 동작함.
	         
	         # 는 몇 번째 주와 요일 설정함. 요일명에서만 사용함.    요일명위치에 6#3이면 3번째 주 금요일에 동작함.
	                                                                                             요일명위치에 4#2이면 2번째 주 수요일에 동작함.

		   
		   ※ cron 스케줄 사용 예
		   0 * * * * *             ==> 매 0초마다 실행(즉, 1분마다 실행함)
		   
		   * 0 * * * *             ==> 매 0분마다 실행(즉, 1시간마다 실행함) , 정각
		    
		   0 * 14 * * *            ==> 14시에 0분~59분까지 1분 마다 실행
		   
		   * 10,50 * * * *         ==> 매 10분, 50분 마다 실행
		                               , : 여러 값 지정 구분에 사용 
		                               
		   0 0/10 14 * * *         ==> 14시 0분 부터 시작하여 10분 간격으로 실행(즉, 6번 실행함)
		                               / : 초기값과 증가치 설정에 사용
		                                * 
		   0 0/10 14,18 * * *      ==> 14시 0분 부터 시작하여 10분 간격으로 실행(6번 실행함) 그리고 
		                           ==> 18시 0분 부터 시작하여 10분 간격으로 실행(6번 실행함)
		                               / : 초기값과 증가치 설정에 사용 
		                               , : 여러 값 지정 구분에 사용 
		   
		   0 0 12 * * *            ==> 매일 12(정오)시에 실행
	       0 15 10 * * *           ==> 매일 오전 10시 15분에 실행
	       0 0 14 * * *            ==> 매일 14시에 실행
	       
	       0 0 0/6 * * *        ==> 매일 0시 6시 12시 18시 마다 실행
	                                   - : 범위 지정에 사용  / : 초기값과 증가치 설정에 사용
	       
	       0 0/5 14-18 * * *    ==> 매일 14시 부터 18시에 시작해서 0분 부터 매 5분간격으로 실행
	                                   / : 증가치 설정에 사용
	       
	       0 0-5 14 * * *          ==> 매일 14시 0분 부터 시작해서 14시 5분까지 1분마다 실행   
	                                - : 범위 지정에 사용

	       0 0 8 * * 2-6           ==> 평일 08:00 실행 (월,화,수,목,금)  
	       
	       0 0 10 * * 1,7          ==> 토,일 10:00 실행 (토,일) 
	       
	       0 0/5 14 * * ?          ==> 아무요일, 매월, 매일 14:00부터 14:05분까지 매분 0초 실행 (6번 실행됨)

	       0 15 10 ? * 6L          ==> 매월 마지막 금요일 아무날의 10:15:00에 실행

	       0 15 10 15 * ?          ==> 아무요일, 매월 15일 10:15:00에 실행 

	       * /1 * * * *            ==> 매 1분마다 실행

	       * /10 * * * *           ==> 매 10분마다 실행 


	   >>> fixedDelay <<<
	        이전에 실행된 task의 종료시간으로부터 정의된 시간만큼 지난 후 다음에 task를 실행함. 단위는 밀리초임.
	    @Scheduled(fixedDelay=1000)

	  >>> fixedRate <<<
	        이전에 실행된 task의 시작 시간으로부터 정의된 시간만큼 지난 후 다음 task를 실행함. 단위는 밀리초임.
	    @Scheduled(fixedRate=1000)

	*/	
		// ==== Spring Scheduler(스프링 스케줄러)를 사용한 email 발송하기 예제 ==== //
		@Scheduled(cron="0 * * * * *")
		@Override
		public void scheduleTestEmailSending() throws Exception {
			// <<주의>> 스케줄러로 사용되어지는 메소드는 반드시 파라미터가 없어야 한다.!!!!
			
			// === 현재시각 나타내기 === //
			Calendar currentDate = Calendar.getInstance();  // 현재날짜와 시간을 얻어온다.
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println("현재시각 => " + dateFormat.format(currentDate.getTime()));
			// 현재시각 => 2019-01-31 15:34:00
			// 현재시각 => 2019-01-31 15:35:00
			// 현재시각 => 2019-01-31 15:36:00
			// 현재시각 => 2019-01-31 15:37:00
			
			
			List<HashMap<String, String>> reservationList = dao.getReservationList();
			
			// 메일 보내기 //
			if(reservationList.size() > 0) {
			   	String[] reservationSeqArr = new String[reservationList.size()]; 
			   	
				for(int i=0; i<reservationList.size(); i++) {
			   		String emailContents = "ID: " + reservationList.get(i).get("USERID") + "<br/> 예약자명: " + reservationList.get(i).get("NAME") +"님의 방문 예약일은 <span style='color:red;'>" + reservationList.get(i).get("RESERVATIONDATE") + "</span> 입니다."; 
			   	    mail.sendmail_Reservation(aes.decrypt(reservationList.get(i).get("EMAIL")), emailContents);
			   		
			   		reservationSeqArr[i] = reservationList.get(i).get("RESERVATIONSEQ");
			   	}
			   	
			   	// 메일 보낸 데이터 표시하기 //
				HashMap<String, String[]> paraMap = new HashMap<String, String[]>();
				paraMap.put("RESERVATIONSEQARR", reservationSeqArr);
				
			   	dao.setMailSendCheck(paraMap);
			}
			
		}


	
}
