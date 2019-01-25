package com.spring.board.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.spring.board.model.BoardVO;
import com.spring.board.model.CommentVO;
import com.spring.board.model.PhotoVO;
import com.spring.board.service.InterBoardService;
import com.spring.common.AES256;
import com.spring.common.FileManager;
import com.spring.common.LargeThumbnailManager;
import com.spring.common.MyUtil;
import com.spring.common.SHA256;
import com.spring.common.ThumbnailManager;
import com.spring.member.model.MemberVO;

//===== #30. 컨트롤러 선언  =====
@Controller
@Component
/* XML에서 빈을 만드는 대신에 클래스명 앞에 @Component 어노테이션을 적어주면 해당 클래스는 bean으로 자동 등록된다. 
그리고 bean의 이름(첫글자는 소문자)은 해당 클래스명이 된다. */
public class BoardController {
	
	//===== #35. 의존 객체 주입하기 (DI:Dependency Injection)  =====
	@Autowired
	private InterBoardService service;
	
	//==== #45. 양방향 암호와 알고리즘인 AES256 를 사용하여 복호화 하기위한 클래스 의존객체 주입하기(DI:Dependency Injection) ====
	@Autowired
	private AES256 aes;
	
	// ==== #139. 파일 업로드및 파일 다운로드를 해주는 FileManager 클래스 의존객체 주입하기(DI:Dependency Injection)  ====
	@Autowired
	private FileManager fileManager;
	
	// ==== #스마트에디터3. 수마트에디터 사용시 사진첨부를 할 경우 원본사진의 크기가 아주 클 경우 이미지 width 의 크기를 적절하게 줄여주는 클래스 객체 DI 다 
	@Autowired
	private LargeThumbnailManager largeThumbnailManager;
	
	
	// ==== #36.메인페이지 요청. ====
	@RequestMapping(value="/index.action",method={RequestMethod.GET})
	public String index(HttpServletRequest req) {
		//이미지 파일명 가져오기
		List<String> imgfilenameList = service.getImgfilenameList();
		req.setAttribute("imgfilenameList", imgfilenameList);
		
		return "main/index.tiles1";
		///WEB-INF/views/tiles1/main/index.jsp 파일을 생성한다.
	}
	
	// ==== #40. 로그인 폼 페이지 요청. ====
	@RequestMapping(value="/login.action",method={RequestMethod.GET})
	public String login(HttpServletRequest req) {
		//이미지 파일명 가져오기
		List<String> imgfilenameList = service.getImgfilenameList();
		req.setAttribute("imgfilenameList", imgfilenameList);
		
		return "login/loginform.tiles1";
		// /WEB-INF/views/tiles1/login/loginform.jsp 파일을 생성한다.
	} 
	
	// ===$41.로그인 여부 알아오기 및 마지막으로로이한 날ㅈ짜
	@RequestMapping(value="/loginEnd.action",method={RequestMethod.POST})
	public String loginEnd(HttpServletRequest req) {
		String userid = req.getParameter("userid");
				String pwd = req.getParameter("pwd");
				
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("USERID",userid);
				map.put("PWD",SHA256.encrypt(pwd));// 입력받은 패스워드를 암호화 한다(SHA256 은 static 이기 때문에 클래스 명으로 쓴다.)
				
				MemberVO loginuser = service.getLoginMemever(map);
				
				HttpSession session =req.getSession();
				if(loginuser == null) {
					String msg = "아이디/암호를 확인하세요.";
					String loc = "javascript:history.back()";
					req.setAttribute("msg", "msg");
					req.setAttribute("loc", loc);
					return "msg";
				}else if(loginuser != null && loginuser.isIdleStatus() == true){
					// 로그인 을 안한지 1년이 지나서 휴면 상태로 빠진 경우
					String msg = "로그인을 하지 않은 지 1년이 넘어 휴면계정이 되었습니다. 관리자에게 문의하세요.";
					String loc = "javascript:history.back()";
					req.setAttribute("msg", msg);
					req.setAttribute("loc", loc);
					return "msg";
				}else if(loginuser != null && loginuser.isRequirePwdChange() == true){
					// 암호를 최근 6개월 이내의 변경하지 않았을 경우
					String msg = "암호를 최근 6개월 이내에 변경하지 않으셨습니다. 암호를 변경을 위해 나의 정보보기로 이동하겠습니다";
					String loc = req.getContextPath()+"/myinfo.action";
					req.setAttribute("msg", msg);
					req.setAttribute("loc", loc);
					session.setAttribute("loginuser",loginuser);
					return "msg";
				}else {
					// 아무런 이상없이 로그인 하는 경우
					session.setAttribute("loginuser", loginuser);
					if(session.getAttribute("gobackURL") != null) {
						// 세션에 저장된 돌아갈 페이지의 주소 gobackURL
						String gobackURL = (String)session.getAttribute("gobackURL");
						req.setAttribute("gobackURL", gobackURL);
						session.removeAttribute("gobackURL");
						return "login/loginEnd.tiles1";
						///WEB-INF/views/tiles1/login/loginEnd.jsp 파일 생성
					}
					
				}
				
		return "login/loginform.tiles1";
		// /WEB-INF/views/tiles1/login/loginform.jsp 파일을 생성한다.
	} 
		@RequestMapping(value="/myinfo.action", method={RequestMethod.GET})
		public String myinfo(HttpServletRequest req) {
			return "login/myinfo.tiles1";
		    //  /Board/src/main/webapp/WEB-INF/views/tiles1/login/myinfo.jsp 파일을 생성한다.
		}



	// ===== #50. 로그아웃 완료 요청. =====
		@RequestMapping(value="/logout.action", method={RequestMethod.GET})
		public String logout(HttpServletRequest req, HttpSession session) {
			
			 session.invalidate();
		  	
			 String msg = "로그아웃 되었습니다."; 
			 String ctxPath = req.getContextPath();
			 String loc = ctxPath+"/index.action";
				
			 req.setAttribute("msg", msg);
			 req.setAttribute("loc", loc);
				
			 return "msg";
		}
		
	// ===== #51. 글쓰기 폼 페이지 요청. =====   
		@RequestMapping(value="/add.action", method={RequestMethod.GET})
		public String requireLogin_add(HttpServletRequest req,HttpServletResponse res) {
			
			// ===== #125. 답변 글 쓰기가 추가된 경우 시작 ====
				String fk_seq = req.getParameter("fk_seq");
				String groupno = req.getParameter("groupno");
				String depthno = req.getParameter("depthno");
				
				req.setAttribute("fk_seq", fk_seq);
				req.setAttribute("groupno", groupno);				
				req.setAttribute("depthno", depthno);				
			// ===== #125. 답변 글 쓰기가 추가된 경우 끝 ====
			 return "/board/add.tiles1";
		}
		
		
	// ===== #53. 글쓰기 완료 요청 ====
		@RequestMapping(value="/addEnd.action", method={RequestMethod.POST})
		/*public String addEnd(BoardVO boardvo,HttpServletRequest req) {*/
		/* ==== #136. 파일첨부가 된 글쓰기 이므로 먼저 위의 public String addEnd(BoardVO boardvo,HttpServletRequest req) { 을 주석처리한 후
		   아래와 같이 한다.
		   MultipartHttpServletRequest req 사용하기 위해서는 먼저 /Board/src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml 파일에서
		  multipartResolver 를 bean 으로 등록 해주어야한다.  */
		public String addEnd(BoardVO boardvo, MultipartHttpServletRequest req){
			
			/*
			   웹페이지에 요청form이 enctype="multipart/form-data" 으로 되어있어서 Multipart 요청(파일처리 요청)이 들어올때 
			   컨트롤러에서는 HttpServletRequest 대신 MultipartHttpServletRequest 인터페이스를 사용해야 한다.
			  MultipartHttpServletRequest 인터페이스는 HttpServletRequest 인터페이스와 MultipartRequest 인터페이스를 상속받고있다.
			   즉, 웹 요청 정보를 얻기 위한 getParameter()와 같은 메소드와 Multipart(파일처리) 관련 메소드를 모두 사용가능하다.
			 
			 ===== 사용자가 쓴 글에 파일이 첨부되어 있는 것인지 아니면 파일첨부가 안된것인지 구분을 지어주어야 한다. =====
			 ========= !!첨부파일이 있는지 없는지 알아오기 시작!! ========= */
			
			MultipartFile attach = boardvo.getAttach();
			
			if(!attach.isEmpty()) { 
				// attach 가 비어있지 않다면,(즉, 첨부파일이 있는 경우)
				/*
				 	1. 사용자가 보낸 파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다.
				 		>>> 파일이 업로드 되어질 특정 경로(폴더) 지정해주기
				 	우리는 WAS의 webapp/resuorces/files 라는 폴더로 지정해주겠다.
								  */
				//WAS의 webapp 의 절대경로를 알아와야 한다.

				HttpSession session =  req.getSession();
				String root = session.getServletContext().getRealPath("/");
				String path = root+"resources"+File.separator+"files"; 
				/*path가 첨부 파일들을 저장할 was(톰캣)의 폴더가 된다*/
				/*root : .metadata/ , File.separator: 운영체제에 따른 구분자(window \ , 리눅스, 유닉스 /)*/
				System.out.println("확인용 path: "+path);
				//확인용 path: C:\springworkspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Board\resources\files
				
				/* 2. 파일첨부를 위한 변수의 설정 및 값을 초기화한 후 파일 올리기 */
				String newFileName = "";
				// WAS(톰캣) 티스크에 저장할 파일명
				byte[] bytes = null;
				
				long fileSize =0;
				// 파일크기를 읽어오기 위한 용도
				
				try {
					bytes = attach.getBytes();
					// getBytes() 는 첨부된 파일을 바이트 단위로 파일을 다 읽어오는 것이다.
					
					newFileName = fileManager.doFileUpload(bytes, attach.getOriginalFilename(), path);
					//attach.getOriginalFilename() : 첨부되어진 파일명
					// 첨부된 파일을 WAS(톰캣) 디스크로 파일올리기를 한다.
					// 파일을 올린 후 예를 들어  20190107091235.png 이러한 파일명을 얻어온다.
					//System.out.println("확인용 newFileName:"+newFileName);
					//확인용 newFileName:201901071128442769720560220086.jpg

					/* 3. BoardVO boardvo 에 filaName 값과 orgFilename 값과 fileSize 값을 넣어주기 */
					boardvo.setFileName(newFileName);
					boardvo.setOrgFilename(attach.getOriginalFilename()); // 첨부한 파일의 원래 파일명
					
					fileSize = attach.getSize(); // 첨부한 파일의 크기, return 타입은 long 이다
					boardvo.setFileSize(String.valueOf(fileSize));
					
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// 첨부되어진 파일을 바이트 타입의 배열로 바꿔준다 => 직렬화
			}
			//  ========= !!첨부파일이 있는지 없는지 알아오기 끝 !! ========= */
			
			//int n = service.add(boardvo);
			
			// ===== #140.파일 첨부가 없는 경우 또는 파일 첨부가 있는 경우 Service단으로 호출하기. ====
			// 먼저 위의 int n = service.add(boardvo); 주석 처리 후 아래처럼 한다
			int n = 0;
			
			if(attach.isEmpty()) { // 파일첨부가 없다면
				n = service.add(boardvo);
			}
			else {// 파일첨부가 있다면
				
				n = service.add_withFile(boardvo);
			}
			String loc ="";
			if(n==1) {
				loc=req.getContextPath()+"/list.action";				
			}else {
				loc=req.getContextPath()+"/index.action";
			}
			req.setAttribute("n", n);
			req.setAttribute("loc", loc);
			return "/board/addEnd.tiles1";
		}
		
	// ===== #57. 글 목록보기 요청 ====
		@RequestMapping(value="/list.action", method={RequestMethod.GET})
		public String list(HttpServletRequest req) {
		/*	List<BoardVO> boardList = null;
			
			// ==== #106.검색어가 포함되었으므로 먼저 위의 boardList = service.boardListNoSerach(); 주석 처리 후 
			String colname = req.getParameter("colname"); // 검색하고자하는 옵션명
			String search = req.getParameter("search"); // 검색하고자 할 검색어
			
			HashMap<String,String> paraMap = new HashMap<String,String>();
			paraMap.put("COLNAME", colname);
			paraMap.put("SEARCH", search);
			
			// 페이징 처리 안한  검색어가 없는 글전체 목록 가져오기
			// boardList = service.boardListNoSerach();
			
			// ==== #110. 페이징 처리 ====
			// 페이징 처리를 통한 글 목록 보여주기는 예를 들어 , 3페이지의 내용을 보고자 한다면
			// http://localhost:9090/board/list.action?colname=name&search=&currnetShowPageNo=38
			// => 이렇게 나와야 한다.
			
			// ----------------------- ==== 페이징 처리를 한 것임 ==== ------------------------------------
			String str_currnetShowPageNo = req.getParameter("currnetShowPageNo");
			
			int totalCount = 0; // 조건에 맞는 총 게시물 건수
			int sizePerPage = 10; // 한페이지당 보여줄 게시물 건수
			int currentShowPageNo =0; // 현재 보여주는 페이지 번호로서, 초기치로는 1페이지로 한다.
			int totalPage = 0; // 총 페이지 수 (웹 브라우저 상의 총 페이지 갯수)
			int startRno = 0; // 시작 행 번호
			int endRno = 0; // 끝 행 번호
			int blockSize = 10; // 페이지 바 에 보여줄 페이지의 갯수 
			
			
			 ==== 총 페이지 수 구하기(totalPage) ====
			 	검색 조건이 없을때(search값이 null 또는 ""인 경우)의 총 페이지 수와 
			 	검색 조건이 있을때(search값이 null 이 아니고 또는 ""  아닌 경우)의 총 페이지 수를 구해야 한다. 
		
			// 먼저, 총 게시물 건수를 구한다.
			// 총 게시물 건수는 검색조건이 있을 때와 없을 때로 나뉘어 진다.
			
			// 검색이 있는 경우 페이징 처리 한것
			if(search != null && !search.trim().equals("") && !search.trim().equals("null")) {
				totalCount = service.getTotalCountWithSearch(paraMap);// 전체 게시물 건수 구하기
			}else {// 검색이 없는 경우 페이징 처리 한것
				totalCount = service.getTotalCountNoSearch(); // 총 게시물 갯수 
				totalPage = (int)Math.ceil((double)totalCount/sizePerPage); // 총 페이지 수 
				
				if(str_currnetShowPageNo == null) { // 게시판 초기화면 일 경우
					currentShowPageNo =1;
				}else { // 특정 페이지를 조회한 경우
					try {// 유저가 currnetShowPageNo 에 문자를 입력 했을 경우 
						currentShowPageNo = Integer.parseInt(str_currnetShowPageNo);
						if(currentShowPageNo <1 || currentShowPageNo>totalPage) {// 유저가 임의의 값을 넣었을 경우
							currentShowPageNo =1;	
						}
						
					} catch (NumberFormatException e) {
						currentShowPageNo =1;
					}
				}
			}
			// **** 가져올 게시글의 범위를 구한다 (공식!!!) ****
				currnetShowPageNo		startRno 		endRno 
						1					1				10
						2					11				20
						3					21				30
				
			
			startRno = ((currentShowPageNo - 1)*sizePerPage)+1;
			endRno = startRno+(sizePerPage-1);
			
			paraMap.put("startRno", String.valueOf(startRno));
			paraMap.put("endRno",String.valueOf(endRno));
			boardList = service.boarListPaging(paraMap);
			
			
			// ==== #120. 페이지 바 만들기 ====
			String pageBar = "<ul>";
			pageBar += MyUtil.getPageBarWithSearch(sizePerPage, blockSize, totalPage, currentShowPageNo, colname, search, null, "list.action");
			pageBar += "</ul>";
			
			
			// ----------------------- ==== 페이징 처리를 한 것임 ==== ------------------------------------
			
			
			// ===== #68. 글조회수(readCount)증가 (DML문 update)는
			            반드시 해당 글제목을 클릭했을 경우에만 글조회수가 증가되고 
			                         이전보기, 다음보기를 했을 경우나 웹브라우저에서 새로고침(F5)을 했을 경우에는 증가가 안되도록 한다.===== 
			              이것을 하기 위해서는 세션을 이용하여 처리한다 
			

			
			// ----------------------- ==== 페이징 처리를 안한 것임 ==== ------------------------------------
					HashMap<String,String> paraMap = new HashMap<String,String>();
					paraMap.put("COLNAME", colname);
					paraMap.put("SEARCH", search);
			
			
			if(search != null && !search.trim().equals("") && !search.trim().equals("null")) {
				// 검색어가 있는 경우
				boardList = service.boardListWithSerach(paraMap);
				 
			}else {
				//검색어가 없는 경우
				boardList = service.boardListNoSerach();
				// 목록 전체를 보여준다.
			}
			

			// 특정 글 제목을 선택하여 상세보기 이후 목록보기 버튼 선택 시 페이징 처리된 해당 페이지로 그대로 돌아가기 위해 돌아갈 페이지를 위해서 뷰단으로 넘겨준다.		
			
			// -----------------------==== 페이징 처리를 안한 것임 ====---------------------------
			String goBackURL = MyUtil.getCurrentURL(req);
			
			HttpSession session = req.getSession();
			req.setAttribute("goBackURL", goBackURL);
			System.out.println(goBackURL);
			req.setAttribute("colname",colname);// 검색어 유지위해서
			req.setAttribute("boardList",boardList); // 검색 결과물/검색하지 않았을 경우
			req.setAttribute("search", search);
			req.setAttribute("pageBar", pageBar);// view 단으로 페이지 바 넘기기
			session.setAttribute("readCountPermission", "yes");			
			
			return "/board/list.tiles1";
		*/
		
			
			  
			  List<BoardVO> boardList = null;
			  
			  // 페이징 처리 안한 검색어가 없는 전체글목록 보여주기
		   // boardList = service.boardListNoSearch();
			  
			// ===== #106. 검색어가 포함되었으므로 먼저위의 
			//             boardList = service.boardListNoSearch(); 을 주석처리한다.
			   String colname = req.getParameter("colname");
			   String search = req.getParameter("search");
			
			   HashMap<String, String> paraMap = new HashMap<String, String>();
			   paraMap.put("COLNAME", colname);
			   paraMap.put("SEARCH", search);
			   
			/*   
		    // === 페이징 처리 안한것임. === 
			   
			   if(search != null &&
				  !search.trim().equals("") && 
				  !search.trim().equals("null")) {
				   // 검색이 있는 경우(페이징 처리 안한 것임)
				   boardList = service.boardListWithSearch(paraMap);
				   
				   req.setAttribute("colname", colname); // view단에서 검색어를 유지시키려고 보낸다. 
				   req.setAttribute("search", search);   // view단에서 검색어를 유지시키려고 보낸다. 
			   }
			   else {
				   // 검색이 없는 경우(페이징 처리 안한 것임) 
				   boardList = service.boardListNoSearch();
			   }
			*/
			   
			// ===== #110. 페이징 처리 ======
			// 페이징 처리를 통한 글목록 보여주기는 예를들어, 3페이지의 내용을 보고자 한다면
			// http://localhost:9090/board/list.action?colname=name&search=&currentShowPageNo=3 와 같이 해주어야 한다. 
			String str_currentShowPageNo = req.getParameter("currentShowPageNo"); 
			
			int totalCount = 0;         // 총게시물 건수
			int sizePerPage = 10;       // 한 페이지당 보여줄 게시물 건수 
			int currentShowPageNo = 0;  // 현재 보여주는 페이지번호로서, 초기치로는 1페이지로 설정함.
			int totalPage = 0;          // 총 페이지수(웹브라우저상에 보여줄 총 페이지 갯수)
			
			int startRno = 0;           // 시작 행 번호
			int endRno = 0;             // 끝 행 번호
			
			int blockSize = 10;         // "페이지바" 에 보여줄 페이지의 갯수 
			
			/*
			    ==== 총 페이지수(totalPage) 구하기 ====
			       검색조건이 없을때(search값이 null 또는 "" 인 경우)의 총페이지 수와
			       검색조건이 있을때(search값이 null 이 아니고 "" 아닌 경우)의 총페이지 수를 구해야 한다. 
			*/
			// 먼저, 총 게시물 건수를 구해야 한다.
			// 총 게시물 건수는 검색조건이 있을때와 없을때로 나뉘어진다.
			if(search != null &&
			  !search.trim().equals("") && 
			  !search.trim().equals("null")) {
			  // 검색이 있는 경우(페이징 처리 한 것임)
			  totalCount = service.getTotalCountWithSearch(paraMap); 
			}
			
			else {
			  // 검색이 없는 경우(페이징 처리 한 것임)
			  totalCount = service.getTotalCountNoSearch();	
			}
			
			totalPage = (int)Math.ceil((double)totalCount/sizePerPage);
			 	        // 23.7 ==> 24.0 ==> 24 
			
			if(str_currentShowPageNo == null) {
				// 게시판 초기화면일 경우
				currentShowPageNo = 1;
			}
			
			else {
			    // 특정페이지를 보고자 조회한 경우 
				try {
					currentShowPageNo = Integer.parseInt(str_currentShowPageNo);
					
					if(currentShowPageNo < 1 || currentShowPageNo > totalPage) {
						currentShowPageNo = 1;
					}
				} catch(NumberFormatException e) {
					currentShowPageNo = 1;
				}
			}
			
			// **** 가져올 게시글의 범위를 구한다.(공식임!!!) **** 
			/*
			     currentShowPageNo    startRno    endRno
			     ========================================
			           1 페이지                      1          10
			           2 페이지                    11          20
			           3 페이지                    21          30
			           ....             ..          ..
			 */
			startRno = ((currentShowPageNo-1)*sizePerPage) + 1;
			endRno = startRno + sizePerPage - 1;
			
			paraMap.put("STARTRNO", String.valueOf(startRno));
			paraMap.put("ENDRNO", String.valueOf(endRno));
			
			boardList = service.boarListPaging(paraMap); 

//			System.out.println("boardList.size() : "+boardList.size());
			
			//===== #120. 페이지바 만들기 =====
			String pagebar = "<ul>";
			pagebar += MyUtil.getPageBarWithSearch(sizePerPage, blockSize, totalPage, currentShowPageNo, colname, search, null, "list.action");  
			pagebar += "</ul>";
			
			   
			// ===== #68. 글조회수(readCount)증가 (DML문 update)는
			/*            반드시 해당 글제목을 클릭했을 경우에만 글조회수가 증가되고 
			                         이전글보기, 다음글보기를 했을 경우나 웹브라우저에서 새로고침(F5)을 했을 경우에는 증가가 안되도록 한다.
			                         이것을 하기 위해서는 우리는 session 을 이용하여 처리한다. ===== 
			*/
			  HttpSession session = req.getSession();
			  session.setAttribute("readCountPermission", "yes");
			  
			  
			  req.setAttribute("boardList", boardList);
			  
			  req.setAttribute("colname", colname); // view단에서 검색어를 유지시키려고 보낸다. 
			  req.setAttribute("search", search);   // view단에서 검색어를 유지시키려고 보낸다.
		      req.setAttribute("pagebar", pagebar); // view단으로 페이지바 넘기기
			  
		      
		      /* 특정 글제목을 클릭하여 상세내용을 본 이후 페이징 처리된 해당 페이지로 그대로 돌아가기 위해
		   	         돌아갈 페이지를 위해서 gobackURL을 뷰단으로 넘겨준다. */
		      String gobackURL = MyUtil.getCurrentURL(req);
		      req.setAttribute("gobackURL", gobackURL);
		      
			  return "board/list.tiles1";
			  
			  
		}
		
		// ==== #61. 글 상세보기 ====
		@RequestMapping(value="/view.action",method= {RequestMethod.GET})
		public String view(HttpServletRequest req) {
			
			String seq = req.getParameter("seq");
			String gobackURL = req.getParameter("gobackURL");
			// 특정 글 제목을 선택하여 상세보기 이후 목록보기 버튼 선택 시 페이징 처리된 해당 페이지로 그대로 돌아가기 위해 돌아갈 페이지를 위해서 뷰단으로 넘겨준다. 
			
			BoardVO boardvo = null; // 글한개를 저장 할 객체
			//로그인 되어진 사용자 정보를 읽어온다
			HttpSession session = req.getSession();
			
			// ===== #67. 글조회수(readCount)증가 (DML문 update)는
			/*  반드시 해당 글제목을 클릭했을 경우에만 글조회수가 증가되고 이전보기, 다음보기를 했을 경우나 웹브라우저에서 새로고침(F5)을 했을 경우에는 증가가 안되도록 한다.===== */
			String readCountPermission = (String)session.getAttribute("readCountPermission");
						
			MemberVO loginuser = (MemberVO)session.getAttribute("loginuser");
			String userid = null;
			
			if(readCountPermission != null && "yes".equals(readCountPermission)) { // 글목록 클릭해서 들어옴
				// 글 한개를 보기위해 http://localhost:9090/board/list.action 을 걸친 후 들어온 경우
				
				if(loginuser != null) {// 로그인 한 경우 에만 userid 변수에 로그인한 사용자 id 값을 넣어준다
					userid = loginuser.getUserid();
				}
				
				boardvo = service.getView(seq,userid);
				// 글 한개를 보기위해 글 목록을 거쳐 온 경우라면 확인 후 session 에서 제거한다.
				session.removeAttribute("readCountPermission");
			}else {
				// 특정 글 한개를 본 이휴 F5 (새로고침)을 한 경우 
				// 또는 특정 글 한개를 본 이후 이전글보기/다음 글보기를 한경우
				// 이럴 경우 글 조회수 증가 없는 한개 글만 보여주도록 한다.
				boardvo = service.getViewWithNoAddCount(seq);
				// 조회수 증가 없이 그냥글 한개 만 가져오기
			}
			
			req.setAttribute("boardvo", boardvo);
			return "/board/view.tiles1";
		}
		
		// ===== #70. 글 수정 페이지 요청 ====
		@RequestMapping(value="/edit.action",method= {RequestMethod.GET})
		public String requireLogin_edit(HttpServletRequest req, HttpServletResponse res) {
		
			// 글 수정해야할 글번호 가져오기
			String seq = req.getParameter("seq");
			
			// 글 수정 해야할 글 전체 내용 가져오기(조회수 증가하지 않음)
			BoardVO boardvo = service.getViewWithNoAddCount(seq);
			HttpSession session = req.getSession();
			MemberVO loginuser = (MemberVO)session.getAttribute("loginuser");
			
			if(!loginuser.getUserid().equals(boardvo.getFk_userid())) {
				String msg = "다른 사용자의 글은 수정이 불가합니다.";
				String loc="javascript:history.back();";
				req.setAttribute("msg", msg);
				req.setAttribute("loc", loc);
				
				return "msg";
				
			}else {
				// 가져온 한개의 글을 request 영역에 저장시켜서 view 단 페이지로 넘긴다.
				req.setAttribute("boardvo", boardvo);
				return "board/edit.tiles1";
			}
		}
		
		// ===== #71. 글 수정 페이지 요청 ====
		@RequestMapping(value="/editEnd.action",method= {RequestMethod.POST})
		public String editEnd(BoardVO boardvo,HttpServletRequest req) {
			//글 수정페이지의 원래 암호와(vo에 있는 암호) 글수정 페이지에서 입력한 암호를 비교해준다
			
			HashMap<String,String> paraMap = new HashMap<String,String>();
			paraMap.put("SEQ", boardvo.getSeq());
			paraMap.put("PW",boardvo.getPw());
			paraMap.put("SUBJECT",boardvo.getSubject());
			paraMap.put("CONTENT",boardvo.getContent());
			 // 수정하려는 글의 암호
			
			int result = service.edit(paraMap);// DB 에 있는 암호와 사용자가 입력한 암호를 비교한다
			// 넘겨받은 값이 1이면 업데이트 성공!, 넘겨받은 값이 0이면 업데이트 실패!
			
			String msg = "";
			String loc = "";
			if(result == 0){
				msg="글 수정하기가 실패했습니다!";
				loc="javascript:history.back();";
			}else{
				msg="글 수정하기 성공했습니다!";
				loc=req.getContextPath()+"/view.action?seq="+boardvo.getSeq();				
			}
			
			req.setAttribute("msg", msg);
			req.setAttribute("loc", loc);
			
			return "msg";
		}
		
		// ===== #77. 글 삭제하기 요청 ====
		@RequestMapping(value="/del.action",method= {RequestMethod.GET})
		public String requireLogin_del(HttpServletRequest req, HttpServletResponse res) {
			// 삭제해야할 글번호 가져오기
			String seq = req.getParameter("seq");
			
			// 삭제해야할 글 내용 전체 가져오기(조회수 증가하지 않음)
			BoardVO boardvo = service.getViewWithNoAddCount(seq);
			HttpSession session = req.getSession();
			MemberVO loginuser = (MemberVO)session.getAttribute("loginuser");
						
			String msg = "";
			String loc = "";
			
			if(!loginuser.getUserid().equals(boardvo.getFk_userid())){
				msg="글 삭제하기가 실패했습니다!";
				loc="javascript:history.back();";
				
				req.setAttribute("msg", msg);
				req.setAttribute("loc", loc);
				return "msg";
			}else{
				// 삭제해야할 글 번호를 request 영역에 저장시켜서 view 단 페이지로 넘긴다.
				req.setAttribute("seq", seq);
				
				// 글 삭제시 글 암호를 입력받아 글 작성할 때 입력한 암호와 비교할수 있도록 view 단에서 만들어 주어야 한다. 
				return "board/del.tiles1";
			}
		}
		// ===== #78. 글 삭제페이지 완료 요청 ====
		@RequestMapping(value="/delEnd.action",method= {RequestMethod.POST})
		public String delEnd(HttpServletRequest req) throws Throwable {
		// 글 삭제를 하려면 삭제 할 글의 글번호와 사용자가 입력한 글암호를 알아와서 삭제할 그의 암호화 일치하는지 알아본다.
			String seq = req.getParameter("seq");
			String pw = req.getParameter("pw");
			HashMap<String,String> paraMap = new HashMap<String,String>();
			paraMap.put("SEQ", seq);
			paraMap.put("PW",pw);
			
			// 암호비교를 해준다
			int result = service.del(paraMap);			
			/*
			 넘겨 받은 값이 1이면 글 삭제 성공!, 넘겨받은 값이 0이면 글 삭제 실패!
			 */
			String msg = "";
			String loc = "";
			if(result == 0){
				msg="글 삭제하기가 실패했습니다!";
				loc="javascript:history.back();";
			}else{
				msg="글 수정하기 성공했습니다!";
				loc=req.getContextPath()+"/list.action";
			}
			req.setAttribute("msg", msg);
			req.setAttribute("loc", loc);
			
			return "msg";
		}
		
		// ===== #85. 댓글 쓰기  ====
		@RequestMapping(value="/addComment.action",method= {RequestMethod.POST})
		@ResponseBody 
		public HashMap<String,String> addComment(CommentVO commentvo) throws Throwable{
			
			HashMap<String,String> returnMap = new HashMap<String,String>();
			
			// 댓글 쓰기 (AJAX로 처리)
			int n = service.addComment(commentvo);
			if(n==1) {
				//댓글 쓰기 (Insert) 및 원게시물(tblBoard 테이블)에 댓글의 갯수(1씩 증가)증가가 성공 했더라면
				returnMap.put("NAME",commentvo.getName());
				returnMap.put("CONTENT",commentvo.getContent());
				returnMap.put("REGDATE",MyUtil.getNowTime());
			}
			return returnMap ;
		}
		
		// ==== #92.-1 댓글 내용가져오기 (페이징 처리 하므로 특정 페이지 (1페이지,2페이지..)에 대한 댓글의 내용가져오기 ====
		@RequestMapping(value="/commentList.action",method= {RequestMethod.GET})
		@ResponseBody 
		public List<HashMap<String,Object>> commentList(HttpServletRequest req){
			List<HashMap<String,Object>> mapList = new ArrayList<HashMap<String,Object>>();
			String seq = req.getParameter("seq"); // 원글의 글 번호를 받아와서 원글에 딸린 댓글을 보여주려고 한다.
			String currentShowPageNo = req.getParameter("currentShowPageNo");
			
/*			if(currentShowPageNo == null || "".equals(currentShowPageNo)) {
				currentShowPageNo = "1";
			}
			*/
			int sizePerPage =5; // 고정 값. 한페이지당 5개의 댓글을 보여 줄 것이다.
			int rno1=Integer.parseInt(currentShowPageNo)*sizePerPage -(sizePerPage-1) ; // 공식! 보고싶은 페이지 * 페이제의 댓글 수 
			int rno2=Integer.parseInt(currentShowPageNo)*sizePerPage;  // 공식!	
			/*
					 페이지 번호 	rno1 	rno2
					==========================
					1페이지 		1		5
					2페이지 		6		10
					3페이지 		11		15
					.
					.
					.
			 */
			HashMap<String,String> paraMap = new HashMap<String,String>();
			paraMap.put("SEQ", seq);
			paraMap.put("RNO1", String.valueOf(rno1));
			paraMap.put("RNO2", String.valueOf(rno2));
			
			// 원글의 글번호에 대한 댓글 중 페이지 번호에 해당하는 댓글만 조회해 온다.
			List<CommentVO> commentList= service.listComment(paraMap);
			
			for(CommentVO cvo : commentList) {
				HashMap<String,Object> map = new HashMap<String,Object>(); 
				map.put("NAME", cvo.getName());
				map.put("CONTENT", cvo.getContent());
				map.put("REGDATE", cvo.getRegDate());
				
				mapList.add(map);
			}			
			return mapList;
		}
	
		// ==== #92.-2 댓글 Total Page 가져오기 ====
		@RequestMapping(value="/getCommentTotalPage.action",method= {RequestMethod.GET})
		@ResponseBody 
		public HashMap<String, Integer> getCommentTotalPage(HttpServletRequest req){
			
			HashMap<String,Integer> returnMap = new HashMap<String,Integer>();
			
			String seq = req.getParameter("seq"); // 원글의 글 번호를 받아와서 원글에 딸린 댓글의 갯수를 알아보려고 한다.
			String sizePerPage = req.getParameter("sizePerPage");// 한페이지당 보여줄 댓글의 갯수
			
			HashMap<String,String> paraMap = new HashMap<String,String>();
			paraMap.put("SEQ", seq);
			paraMap.put("SIZEPERPAGE", sizePerPage);

			// 원글의 글번호에 대한 댓글 중 페이지 번호에 해당하는 댓글만 조회해 온다.
			int totalCount = service.getCommentTotalCount(paraMap); 
			//원글 글번호에 해당하는 댓글의 총 갯수를 알아온다
			
			// ==== 총 페이지 수 (totalPage) 구하기 ====
			
			/*
			  	57.0( totalCount )/5(sizePerPage) == 11.4 
			  	== > 12.0 =>12 
			 */
			int totalPage = (int)Math.ceil((double)totalCount/Integer.parseInt(sizePerPage));
						
			returnMap.put("TOTALPAGE", totalPage);
			
			return returnMap;
			
		}
		
		// ==== #149. 첨부파일 다운로드 받기 ====
		@RequestMapping(value="/download.action",method= {RequestMethod.GET})
		public void requireLogin_download(HttpServletRequest req,HttpServletResponse res) {
			String seq = req.getParameter("seq"); // 첨부된 파일의 글 번호
			
			// 첨부파일이 있는 글 번호에서 201901071234352773671405802655.jpg 처럼 이러한 fileName값을 
			//DB에서 가져와야 한다. 또한, 쉐보레전면.jpg처럼 orgFilename을 알아와야 한다.
			
			BoardVO boardvo = service.getViewWithNoAddCount(seq);
			// 조회수 증가 없이 파일만 다운로드 해준다
			
			String fileName = boardvo.getFileName();
			// 201901071234352773671405802655.jpg 과 같은 것을 가져온다.
			// 이것이 바로 WAS(톰캣) 디스크에 저장된 파일명.
			String orgFilename = boardvo.getOrgFilename();
			// 	쉐보레전면.jpg 과 같은 것을 가져온다.
			// 이것이 바로 WAS(톰캣) 디스크에 저장된 원본 파일명, 파일 다운로드 시 다운로드 되는 파일 명
			
		
			//첨부파일이 저장되어 있는 WAS의 절대경로를 알아와야한다.
			// 그래야만 첨부파일이 저장되어진 곳이서 파일을 다운로드 해온다
			// 이 경로는 파일을 첨부했을 때와 동일한 경로이다.
			HttpSession session =  req.getSession();
			String root = session.getServletContext().getRealPath("/");
			String path = root+"resources"+File.separator+"files"; 
			/*path가 첨부 파일들을 저장된 was(톰캣)의 폴더가 된다*/
			/*root : .metadata/ , File.separator: 운영체제에 따른 구분자(window \ , 리눅스, 유닉스 /)*/
			
			// ***** 다운로드 하기 *****
			// 다운로드가 실패할 경우 메시지를 띄어주기 위해서 boolean 타입 변수 flag를 선언한다.
			boolean flag = false;
			flag = fileManager.doFileDownload(fileName, orgFilename, path, res);
			
			// 다운로드가 성공이면 true를 반환해주고, 다운로드가 실패이면 false를 반환해준다
			if(!flag) {// 다운로드가 실패할 경우 메시지를 띄워준다
				res.setContentType("text/html; Charset=UTF-8");
				try {
					PrintWriter out = res.getWriter();
					// PrintWriter out 웹 브라우저 상에 내용물을 기재해주는 객체
					
					out.println("<script type='text/javascript'>alert('파일다운로드가 실패했습니다!')</script>");
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		// ==== #스마트에디터1. 단일사진 파일 업로드 ====
		@RequestMapping(value="/image/photoUpload.action",method= {RequestMethod.POST})
		public String photoUpload(PhotoVO photovo, HttpServletRequest req) {
		
		
		String callback = photovo.getCallback();
	    String callback_func = photovo.getCallback_func();
	    String file_result = "";
	    
		if(!photovo.getFiledata().isEmpty()) {
			// 파일이 존재한다라면
			
			/*
			   1. 사용자가 보낸 파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다.
			   >>>> 파일이 업로드 되어질 특정 경로(폴더)지정해주기
			        우리는 WAS 의 webapp/resources/photo_upload 라는 폴더로 지정해준다.
			 */
			
			// WAS 의 webapp 의 절대경로를 알아와야 한다. 
			HttpSession session = req.getSession();
			String root = session.getServletContext().getRealPath("/"); 
			String path = root + "resources"+File.separator+"photo_upload";
			// path 가 첨부파일들을 저장할 WAS(톰캣)의 폴더가 된다. 
			
		//	System.out.println(">>>> 확인용 path ==> " + path); 
			// >>>> 확인용 path ==> C:\Springworkspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Board\resources\photo_upload
			
			// 2. 파일첨부를 위한 변수의 설정 및 값을 초기화한 후 파일올리기
			String newFilename = "";
			// WAS(톰캣) 디스크에 저장할 파일명 
			
			byte[] bytes = null;
			// 첨부파일을 WAS(톰캣) 디스크에 저장할때 사용되는 용도 
						
			try {
				bytes = photovo.getFiledata().getBytes(); 
				// getBytes()는 첨부된 파일을 바이트단위로 파일을 다 읽어오는 것이다. 
				/* 2-1. 첨부된 파일을 읽어오는 것
					    첨부한 파일이 강아지.png 이라면
					    이파일을 WAS(톰캣) 디스크에 저장시키기 위해
					    byte[] 타입으로 변경해서 받아들인다.
				*/
				// 2-2. 이제 파일올리기를 한다.
				String original_name = photovo.getFiledata().getOriginalFilename();
				//  photovo.getFiledata().getOriginalFilename() 은 첨부된 파일의 실제 파일명(문자열)을 얻어오는 것이다. 
				newFilename = fileManager.doFileUpload(bytes, original_name, path);
				
		//      System.out.println(">>>> 확인용 newFileName ==> " + newFileName); 
				
				int width = fileManager.getImageWidth(path+File.separator+newFilename);
		//		System.out.println("확인용 >>>>>>>> width : " + width);
				
				if(width > 600) {
					width = 600;
					newFilename = largeThumbnailManager.doCreateThumbnail(newFilename, path);
				}
		//		System.out.println("확인용 >>>>>>>> width : " + width);
				
				String CP = req.getContextPath();  // board
				file_result += "&bNewLine=true&sFileName="+newFilename+"&sWidth="+width+"&sFileURL="+CP+"/resources/photo_upload/"+newFilename; 
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			// 파일이 존재하지 않는다라면
			file_result += "&errstr=error";
		}
	    
		return "redirect:" + callback + "?callback_func="+callback_func+file_result;
		
		}
			
		// ==== #스마트에디터4. 드래그앤드롭을 사용한 다중사진 파일업로드 ====
		@RequestMapping(value="/image/multiplePhotoUpload.action", method={RequestMethod.POST})
		public void multiplePhotoUpload(HttpServletRequest req, HttpServletResponse res) {
			    
				/*
				   1. 사용자가 보낸 파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다.
				   >>>> 파일이 업로드 되어질 특정 경로(폴더)지정해주기
				        우리는 WAS 의 webapp/resources/photo_upload 라는 폴더로 지정해준다.
				 */
				
				// WAS 의 webapp 의 절대경로를 알아와야 한다. 
				HttpSession session = req.getSession();
				String root = session.getServletContext().getRealPath("/"); 
				String path = root + "resources"+File.separator+"photo_upload";
				// path 가 첨부파일들을 저장할 WAS(톰캣)의 폴더가 된다. 
				
			//	System.out.println(">>>> 확인용 path ==> " + path); 
				// >>>> 확인용 path ==> C:\Springworkspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Board\resources\photo_upload   
				
				File dir = new File(path); 
				if(!dir.exists())// 해당 경로의 폴더가 없으면 폴더를 만들어라
					dir.mkdirs();
				
				String strURL = "";
				
				try {
					if(!"OPTIONS".equals(req.getMethod().toUpperCase())) {
			    		String filename = req.getHeader("file-name"); //파일명을 받는다 - 일반 원본파일명
			    		
			    //		System.out.println(">>>> 확인용 filename ==> " + filename); 
			    		// >>>> 확인용 filename ==> berkelekle%ED%8A%B8%EB%9E%9C%EB%94%9405.jpg
			    		
			    		InputStream is = req.getInputStream();
			    	/*
			          	요청 헤더의 content-type이 application/json 이거나 multipart/form-data 형식일 때,
			          	혹은 이름 없이 값만 전달될 때 이 값은 요청 헤더가 아닌 바디를 통해 전달된다. 
			          	이러한 형태의 값을 'payload body'라고 하는데 요청 바디에 직접 쓰여진다 하여 'request body post data'라고도 한다.

		               	서블릿에서 payload body는 Request.getParameter()가 아니라 
		            	Request.getInputStream() 혹은 Request.getReader()를 통해 body를 직접 읽는 방식으로 가져온다. 	
			    	 */
			    		String newFilename = fileManager.doFileUpload(is, filename, path);
			    	
						int width = fileManager.getImageWidth(path+File.separator+newFilename);
		       //		System.out.println(">>>> 확인용 width ==> " + width);
						
						if(width > 600) { // 이미지 크기가 600보다 크다면
							width = 600;
							newFilename = largeThumbnailManager.doCreateThumbnail(newFilename, path); // 이미지 크기를 600으로 한다
						}
				//		System.out.println(">>>> 확인용 width ==> " + width);
						// >>>> 확인용 width ==> 600
						// >>>> 확인용 width ==> 121
			    	
						String CP = req.getContextPath(); // board
					
						strURL += "&bNewLine=true&sFileName="; 
		            	strURL += newFilename;
		            	strURL += "&sWidth="+width;
		            	strURL += "&sFileURL="+CP+"/resources/photo_upload/"+newFilename;
			    	}
				
			    	/// 웹브라우저상에 사진 이미지를 쓰기 ///
					PrintWriter out = res.getWriter();
					out.print(strURL);
				} catch(Exception e){
					e.printStackTrace();
				}
				
			}// end of void multiplePhotoUpload(HttpServletRequest req, HttpServletResponse res)---------------- 


		
}                       

