package com.spring.board.service;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spring.board.model.BoardVO;
import com.spring.board.model.CommentVO;
import com.spring.board.model.InterBoardDAO;
import com.spring.common.AES256;
import com.spring.member.model.MemberVO;

//===== #31. 서비스 선언  =====
@Service
public class BoardService implements InterBoardService {

	//===== #34. 의존객체 주입하기  =====
	@Autowired
	private InterBoardDAO dao; // 보통 interface를 쓴다

	//===== #37. 이미지 파일명 가져오기  ===
	@Override
	public List<String> getImgfilenameList() {
		List<String> imgList= dao.getImgfilenameList();
		return imgList;
	}
	//==== #45. 양방향 암호와 알고리즘인 AES256 를 사용하여 복호화 하기위한 클래스 의존객체 주입하기(DI:Dependency Injection) ====
	@Autowired
	private AES256 aes;	
	// === #42.로그인 여부 알아오기 및 마지막으로 로그인 한 날짜 기록하기(트랜잭션 처리) ====
	@Override
	public MemberVO getLoginMemever(HashMap<String, String> map) {
		MemberVO loginuser= dao.getLoginMemever(map);
	    ///////////////////////////////////////////////
	// === #48. aes 의존 객체를 사용하여 로그인되어진 사용자(loginuser)의 이메일값을 복호화 하도록 한다.
		if(loginuser == null) {
			return loginuser;
		}else if(loginuser != null && loginuser.getLastlogindategap() >=12 ){
			// 마지막으로 로그인 한 날짜가 현재일로부텨 1년(12)개월이 지났으면 해당 로그인 계정을 비활성화 한다
			loginuser.setIdleStatus(true);// 휴면 계정
		}else {
			if(loginuser.getPwdchangegap() >=6) {
				// 비밀번호 변경이 6개월 이상인 경우
				// 마지막으로 변경한 날짜가 현재시각으로 6개월이 지나지 않았으면 false 로 한다
				loginuser.setRequirePwdChange(true);				
			}
			dao.setLastLoginDate(map);
			// 마지막으로 로그인한 날짜/시간 기록하기
			try {
			loginuser.setEmail(aes.decrypt(loginuser.getEmail()));
			} catch (UnsupportedEncodingException | GeneralSecurityException e) {
				e.printStackTrace();
			}
		}
		
		return loginuser;
	}
	
	// === #54. 글쓰기(파일 첨부가 없는 글쓰기) ====
	@Override
	public int add(BoardVO boardvo) {
		
		 /*== #127. 글쓰기가 원글쓰기인지 아니면 답변글 쓰기 인지를 구분하여 
		  	  tblBoard 테이블에 inert를 해주어야 한다	
		  	   원글 쓰기이라면 tblBoard 테이블에 groupno 컬럼의 값은 groupno 
		  	   컬럼의 최대값(max) +1 로 insert 해주어야 한다 
		  	  답변글 쓰기 이라면 넘겨받은 값을 그대로 insert 해주어야 한다*/
		// ==== 원글 쓰기인지 답변 글 쓰기인지 구분하기 ====
		if(boardvo.getFk_seq() == null || boardvo.getFk_seq().trim().isEmpty()){ // 원글 쓰기 일 경우
			// 항상 먼저  == null 을 먼저 적어준다. -=> NullPointException 이 올 수 있기 때문에
			int groupno = dao.getGroupnoMax()+1; // database 에서 groupno의 최대값 읽어온다
			boardvo.setGroupno(String.valueOf(groupno));
		}
		
		int n = dao.add(boardvo);
		return n;
	}

	// ==== #141. 파일첨부가 있는 글쓰기(tblBOard 테이블에 insert)
	@Override
	public int add_withFile(BoardVO boardvo) {
	   /* 글쓰기가 원글쓰기인지 아니면 답변글 쓰기 인지를 구분하여 
	  	  tblBoard 테이블에 inert를 해주어야 한다	
	  	   원글 쓰기이라면 tblBoard 테이블에 groupno 컬럼의 값은 groupno 
	  	   컬럼의 최대값(max) +1 로 insert 해주어야 한다 
	  	   답변글 쓰기 이라면 넘겨받은 값을 그대로 insert 해주어야 한다*/
	
		// ==== 원글 쓰기인지 답변 글 쓰기인지 구분하기 ====
	if(boardvo.getFk_seq() == null || boardvo.getFk_seq().trim().isEmpty()){ // 원글 쓰기 일 경우
		// 항상 먼저  == null 을 먼저 적어준다. -=> NullPointException 이 올 수 있기 때문에
		int groupno = dao.getGroupnoMax()+1; // database 에서 groupno의 최대값 읽어온다
		boardvo.setGroupno(String.valueOf(groupno));
	}
	
	int n = dao.add_withFile(boardvo); // 첨부 파일이 있는 경우
	return n;
	}

	
	// ===== #58. 글 목록보기 가져오기(검색조건이 없는 전체 글목록, 페이징 처리 안함) ====
	@Override
	public List<BoardVO> boardListNoSerach() {
		List<BoardVO> boardList = dao.boardListNoSerach();		
		return boardList;
	}
	
	// ==== #107. 검색어 조건에 해당하는 글 목록 가져오기.(페이징 처리 안함)
	@Override
	public List<BoardVO> boardListWithSerach(HashMap<String, String> paraMap) {
		List<BoardVO> boardList = dao.boardListWithSerach(paraMap);
		return boardList;
	}
	
	// ==== #62. 글 상세보기 가져오기 ====
	// 로그인 하지 않은 상태에서 글을 읽을 때는 조회수 증가가 일어나지 않는다
	// 먼저 글 조회수 증가 여부 결정
	@Override
	public BoardVO getView(String seq, String userid) {
		
		BoardVO boardvo = dao.getView(seq);
		// 해당 글 하나를 가져온다.
		if(userid != null && !boardvo.getFk_userid().equals(userid)) {// 익명이 아니고, 내가 쓴 글이 아닐떄 
			// 조회수 증가는 로그인 되어져 있는 상태에서 다른 사람이 작성한 글을 읽었을 때만 증가한다.
			 dao.setAddReadCount(seq);
			 boardvo = dao.getView(seq);
		}else {
			
		}
		

		return boardvo;
	}

	// ==== #69. 조회수 증가 없이 그냥글 한개 만 가져오기 ====
	@Override
	public BoardVO getViewWithNoAddCount(String seq) {
		BoardVO boardvo = dao.getView(seq); 
		return boardvo;
	}

	
	// ==== #72. 글 수정하기 ====
	@Override
	public int edit(HashMap<String, String> paraMap) {
		// 글번호에 대한 암호가 사용자가 입력한 암호와 일치하면 true 반환.
		// 글번호에 대한 암호가 사용자가 입력한 암호와 일치하지 않으면 false 반환.
		
		Boolean checkpw = dao.checkPW(paraMap);//암호가 일지하는지 검사.
		
		int result =0;
		if(checkpw == true) {
			// 글 한개 수정하기
			result = dao.updateContent(paraMap);
		}
		
		return result;
	}

	// ==== #79. 글 한개 삭제하기(트랜잭션 처리). ==== 
	//@Override
	//public int del(HashMap<String, String> paraMap) {
	
	// ==== #96.트랜잭션 처리를 위해서 먼저 위의 줄을주석처리 한 후 아래와 같이 한다. 
/*	@Transactional(propagation=Propagation.REQUIRED, isolation= Isolation.READ_COMMITTED, rollbackFor={Throwable.class})
	public int del(HashMap<String, String> paraMap) throws Throwable {
		// 글번호에 대한 글 암호가 일치하면 true, 틀리면 false 를 반환
		Boolean checkpw = dao.checkPW(paraMap);
		int count = 0;// 원글의 딸린 댓글의 갯수 유무 확인
		
		int result1 = 0;
		int result2 = 0;
		
		if(checkpw == true) {// 암호가 일치 할 때
			count = dao.isExistsComment(paraMap); 	// ==== #99. 원글에 딸린 댓글이 있는지 없는지 확인하기 ====
			result1 = dao.deleteContent(paraMap);// 원글 한개 삭제하기
			
			//==== #100. 원글에 딸린 댓글 삭제하기 ====
			if(count >0) {// 원글에 딸린 댓글이 하나라도 있는 경우라면
			 result2 = dao.delComment(paraMap);
			}

		}
		
		if(result1 > 0 && (count >0 && result2 >0) || (result1 > 0 && count == 0)) {
			
		}
		return result;
	}*/
	// === #98. 트랜잭션 처리를 위해서 먼저 위의 줄을 주석처리를 하고서 아래와 같이 한다.(글삭제를 update로 하기때문에 이런식으로 해야함) ===
	   @Override
	   @Transactional(propagation=Propagation.REQUIRED, isolation= Isolation.READ_COMMITTED, rollbackFor={Throwable.class})
	   public int del(HashMap<String, String> paraMap) throws Throwable {
	      
	      // 글번호에 대한 암호가 일치하면 true, 일치하지 않으면 false 반환함.
	      boolean checkpw = dao.checkPW(paraMap);
	      
	      int count = 0;   // 원글에 달린 댓글의 갯수 유무확인용
	      
	      int result1 = 0;
	      int result2 = 0;
	      int n = 0;	      
	      
	      if(checkpw == true) {	         
	         // === #99. 원글에 달린 댓글의 유무확인
	         count = dao.isExistsComment(paraMap);	         
	         // 원글 1개 삭제하기
	         result1 = dao.deleteContent(paraMap);	         
	         if(count > 0) {
	            // === #100. 원글에 달린 댓글 삭제하기
	            result2 = dao.delComment(paraMap);
	         }
	      }
	      
	      if( (result1 > 0 && (count > 0 && result2 > 0)) 
	            || (result1 > 0 && count == 0) ) {
	         // 댓글이 있는 경우	         
	         n = 1;	         
	      }	      
	      return n;
	   }

	//	===== #86. 댓글쓰기 =====
		// tblComment 테이블에 insert 된 다음에 
		// tblBoard 테이블에 commentCount 컬럼이 1증가(update) 하도록 요청한다.
		// 즉, 2개이상의 DML 처리를 해야하므로 Transaction 처리를 해야 한다.
		// >>>>> 트랜잭션처리를 해야할 메소드에 @Transactional 어노테이션을 설정하면 된다. 
		// rollbackFor={Throwable.class} 은 롤백을 해야할 범위를 말하는데 Throwable.class 은 error 및 exception 을 포함한 최상위 루트이다. 즉, 해당 메소드 실행시 발생하는 모든 error 및 exception 에 대해서 롤백을 하겠다는 말이다.
		@Override
		@Transactional(propagation=Propagation.REQUIRED, isolation= Isolation.READ_COMMITTED, rollbackFor={Throwable.class})
		public int addComment(CommentVO commentvo) throws Throwable {
			int result = 0;
			int n = 0;
			n = dao.addComment(commentvo);
			if(n==1) {
			result = dao.updateCommentCount(commentvo.getParentSeq());// 댓글의 갯수 증가
			}
			
			return result;
		}
		// ==== #93.-1 원글의 글번호에 대한 댓글 중 페이지 번호에 해당하는 댓글만 조회해 온다.
		@Override
		public List<CommentVO> listComment(HashMap<String, String> paraMap) {
			List<CommentVO> commentList = dao.listComment(paraMap);
			return commentList;
		}

		// ==== #93.-2 원글의 글번호에 대한 댓글 중 페이지 번호에 해당하는 댓글만 조회해 온다.
		@Override
		public int getCommentTotalCount(HashMap<String, String> paraMap) {
			int totalCount = dao.getCommentTotalCount(paraMap);
			return totalCount;
		}

		
		// ==== #111. 검색조건에 만족하는 게시물의 총 갯수 알아오기 ==== 
		@Override
		public int getTotalCountWithSearch(HashMap<String, String> paraMap) {
			int count = dao.getTotalCountWithSearch(paraMap);
			return count;
		}
		
		// ==== #112. 검색조건이 없는 게시물의 총 갯수 알아오기 ====

		@Override
		public int getTotalCountNoSearch() {
			int count = dao.getTotalCountNoSearch();
			return count;
		}

		
		// ==== #117. 검색조건이 없는 것 또는 검색조건이 있는 것  목록 가져오기(페이징 처리함)
		@Override
		public List<BoardVO> boarListPaging(HashMap<String, String> paraMap) {
			List<BoardVO> boardList = dao.boarListPaging(paraMap);
			return boardList;
		}
		// tblBoard 테이블에서 groupno 컬럼의 최대값 알아오기
/*		@Override
		public int getGroupnoMax() {
			int max = dao.getGroupnoMax();
			return max;
		}
*/


}

