package com.spring.board.model;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.spring.member.model.MemberVO;

// model단 (DA0)의 인터페이스 선언
@Repository
public interface InterBoardDAO {

	//이미지 파일명 가져오기
	List<String> getImgfilenameList();
	
	//로그인 여부 알아오기 및 마지막으로 로그인 한 날짜 기록하기(트랜잭션 처리)
	MemberVO getLoginMemever(HashMap<String, String> map);

	//마지막으로 로그인한 날짜/시간 기록하기
	void setLastLoginDate(HashMap<String, String> map);

	// 글쓰기(파일 첨부가 없는 글쓰기)
	int add(BoardVO boardvo);
	
	// 글쓰기 (첨부 파일이 있는 글쓰기)
	int add_withFile(BoardVO boardvo);

	// 글 목록보기 가져오기(검색조건이 없는 전체 글목록, 페이징 처리 안함) ====
	List<BoardVO> boardListNoSerach();

	// 검색어 조건에 해당하는 글 목록 가져오기.(페이징 처리 안함)
	List<BoardVO> boardListWithSerach(HashMap<String, String> paraMap);
	
	// 글 상세보기 가져오기
	BoardVO getView(String seq);

	// 글 조회수(Readcount) 1증가 시키기
	void setAddReadCount(String seq);

	// 글 수정 및 글 삭제 시 암호 일치여부 알아오기
	Boolean checkPW(HashMap<String, String> paraMap);

	//
	int updateContent(HashMap<String, String> paraMap);
	
	// 글 한개 삭제하기 
	int deleteContent(HashMap<String, String> paraMap);

	// 댓글 추가하기
	int addComment(CommentVO commentvo);

	// 댓글 쓰기 이후에 댓글의 갯수 (commnetCount 컬럼 1증가 시키키)
	int updateCommentCount(String parentSeq);

	//원글의 글번호에 대한 댓글 중 페이지 번호에 해당하는 댓글만 조회해 온다.
	List<CommentVO> listComment(HashMap<String, String> paraMap);

	// 원글 글번호에 해당하는 총갯수 알아오기
	int getCommentTotalCount(HashMap<String, String> paraMap);

	 // 원글에 딸린 댓글이 있는지 없는지 확인하기 ====
	int isExistsComment(HashMap<String, String> paraMap);

	int delComment(HashMap<String, String> paraMap);

	// 검색조건에 만족하는 게시물의 총 갯수 알아오기 
	int getTotalCountWithSearch(HashMap<String, String> paraMap);

	// 검색조건이 없는 게시물의 총 갯수 알아오기 
	int getTotalCountNoSearch();

	// 검색조건이 없는 것 또는 검색조건이 있는 것  목록 가져오기(페이징 처리함)
	List<BoardVO> boarListPaging(HashMap<String, String> paraMap);
	
	//getGroupnoMax
	int getGroupnoMax();

;




}
