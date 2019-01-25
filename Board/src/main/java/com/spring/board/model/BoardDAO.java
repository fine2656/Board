package com.spring.board.model;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.spring.member.model.MemberVO;


//===== #32. DAO 선언  =====
@Repository
public class BoardDAO implements InterBoardDAO {

	//===== #33. 의존 객체 주입하기 (DI:Dependency Injection) =====
	@Autowired
	private SqlSessionTemplate session;

	//==== #38. 이미지 파일명 가져오기 ===
	@Override
	public List<String> getImgfilenameList() {

		List<String> imgList = session.selectList("board.getImgfilenameList");
		return imgList;
	}
	//==== #46. 로그인 여부 알아오기 및 마지막으로 로그인 한 날짜 기록하기(트랜잭션 처리)
	@Override
	public MemberVO getLoginMemever(HashMap<String, String> map) {
		MemberVO loginuser= session.selectOne("board.getLoginMember",map);
		return loginuser;
	}
	
	//==== #47. 마지막으로 로그인한 날짜/시간 기록하기
	@Override
	public void setLastLoginDate(HashMap<String, String> map) {
	
		session.update("board.setLastLoginDate", map);
	}
	
	//==== #55. 글쓰기(파일 추가 없는 글쓰기)
	@Override
	public int add(BoardVO boardvo) {
		int n = session.insert("board.add",boardvo);
		
		return n;
	}
	
	// ==== #142. 글쓰기(파일첨부가 있는 글쓰기)
	@Override
	public int add_withFile(BoardVO boardvo) {
		int n = session.insert("board.add_withFile",boardvo);
		return n;
	}

	
	
	// ===== #60. 글 목록보기 가져오기(검색조건이 없는 전체 글목록, 페이징 처리 안함) ====
	@Override
	public List<BoardVO> boardListNoSerach() {
		
		List<BoardVO> boardList = session.selectList("board.boardListNoSerach");
						
		return boardList;
	}
	
	//  ===== #63. 글 상세보기 가져오기
	@Override
	public BoardVO getView(String seq) {
		BoardVO boardvo = session.selectOne("board.getView", seq);
		return boardvo;
	}
	
	//  ===== #64. 글 조회수(Readcount) 1증가 시키기
	@Override
	public void setAddReadCount(String seq) {
		session.update("board.setAddReadCount",seq);
	}
	
	//  ===== #73. 글 수정 및 글 삭제시 암호일치여부 확인 ====
	@Override
	public Boolean checkPW(HashMap<String, String> paraMap) {
		int n = session.selectOne("board.checkPW",paraMap);
		
		Boolean result = false;
		
		if(n==1) {
			result = true;
		}
		return result;
	}
	
	//  ===== #75. 글 수정 및 글 삭제시 암호일치여부 확인 ====
	@Override
	public int updateContent(HashMap<String, String> paraMap) {
		int n = session.update("board.updateContent",paraMap); // update 자체가 int 이기 때문에 xml에서 resultType을 쓸 필요가 없다
		return n;
	}
	
	//  ===== #76. 글 한개 삭제하기 ====
	@Override
	public int deleteContent(HashMap<String, String> paraMap) {
		int n = session.update("board.deleteContent",paraMap);// 답변형 게시판은 update 로 처리한다;
		return n;
	}
	
	//  ===== #87. 댓글 쓰기====
	@Override
	public int addComment(CommentVO commentvo) {
		int n = session.insert("board.addComment",commentvo);
		return n;
	}
	
	//  ===== #88. 댓글  쓰기 갯수 증가시키기 ====
	@Override
	public int updateCommentCount(String parentSeq) {
		int n = session.update("board.updateCommentCount",parentSeq);
		return n;
	}
	// ==== #94.-1 원글의 글번호에 대한 댓글 중 페이지 번호에 해당하는 댓글만 조회해 온다.
	@Override
	public List<CommentVO> listComment(HashMap<String, String> paraMap) {
		List<CommentVO> commentList = session.selectList("board.listComment", paraMap);
		return commentList;
	}
	
	// ==== #94.-2 원글의 글번호에 대한 댓글의 총 갯수를 알아온다
	@Override
	public int getCommentTotalCount(HashMap<String, String> paraMap) {
		int commentCount = session.selectOne("board.getCommentTotalCount",paraMap);
		return commentCount;
	}
	
	// ==== #101. 원 게시글에 딸린 댓글이 있는지 없는지 확인하기 ====
	@Override
	public int isExistsComment(HashMap<String, String> paraMap) {
		int count = session.selectOne("board.isExistsComment",paraMap);
		return count;
	}
	
	// ==== #102. 원 게시글에 댓글들 삭제하기 ====
	@Override
	public int delComment(HashMap<String, String> paraMap) {
		int n = session.update("board.delComment",paraMap);		
		return n;
	}
	
	// ==== #108. 검색어 조건에 해당하는 글 목록 가져오기.(페이징 처리 안함)
	@Override
	public List<BoardVO> boardListWithSerach(HashMap<String, String> paraMap) {
		List<BoardVO> boardList = session.selectList("board.boardListWithSerach",paraMap);
		return boardList;
	}

	
	// ==== #113. 검색조건에 만족하는 게시물의 총 갯수 알아오기 ==== 
	@Override
	public int getTotalCountWithSearch(HashMap<String, String> paraMap) {
		int count = session.selectOne("board.getTotalCountWithSearch",paraMap);
		return count;
	}
	
	// ==== #114. 검색조건이 없는 게시물의 총 갯수 알아오기 ====
	@Override
	public int getTotalCountNoSearch() {
		int count = session.selectOne("board.getTotalCountNoSearch");
		return count;
	}
	
	// ==== #118. 검색조건이 없는 것 또는 검색조건이 있는 것  목록 가져오기(페이징 처리함)
	@Override
	public List<BoardVO> boarListPaging(HashMap<String, String> paraMap) {
		List<BoardVO> boardList = session.selectList("board.boarListPaging",paraMap);
		return boardList;
	}
	
	// ==== #128.tblBoard 테이블에서 groupno 컬럼의 최대값 알아오기
	@Override
	public int getGroupnoMax() {
		int max = session.selectOne("board.getGroupnoMax");
		return max;
	}

	
	
}
