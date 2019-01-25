package com.spring.board.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.spring.board.model.BoardVO;
import com.spring.board.model.CommentVO;
import com.spring.member.model.MemberVO;

//Service단 인터페이스 선언
@Service
public interface InterBoardService {

	// 이미지 파일명 가져오기
	List<String> getImgfilenameList();

	// 로그인 여부 알아오기 및 마지막으로 로그인 한 날짜 기록하기(트랜잭션 처리)
	MemberVO getLoginMemever(HashMap<String, String> map);

	// 파일 첨부가 없는 글쓰기
	int add(BoardVO boardvo);

	// 파일 첨부가 있는 글쓰기
	int add_withFile(BoardVO boardvo);
	
	// 글 목록보기 가져오기(검색조건이 없는 전체 글목록, 페이징 처리 안함) ===
	List<BoardVO> boardListNoSerach();
	
	// 검색어 조건에 해당하는 글 목록 가져오기.(페이징 처리 안함)
	List<BoardVO> boardListWithSerach(HashMap<String, String> paraMap);
	
	// 글 상세보기 가져오기
	// 로그인 하지 않은 상태에서 글을 읽을 때는 조회수 증가가 일어나지 않는다 
	BoardVO getView(String seq, String userid);

	// 조회수 증가 없이 그냥글 한개 만 가져오기
	BoardVO getViewWithNoAddCount(String seq);

	// 글 수정하기
	int edit(HashMap<String, String> paraMap);

	// 글 삭제하기
	int del(HashMap<String, String> paraMap) throws Throwable ;

	//댓글  쓰기
	int addComment(CommentVO commentvo) throws Throwable;

	// 원글의 글번호에 대한 댓글 중 페이지 번호에 해당하는 댓글만 조회해 온다.
	List<CommentVO> listComment(HashMap<String, String> paraMap);

	// 원글 글번호에 해당하는 총갯수 알아오기
	int getCommentTotalCount(HashMap<String, String> paraMap);

	// 검색조건에 만족하는 게시물의 총 갯수 알아오기 
	int getTotalCountWithSearch(HashMap<String, String> paraMap);
	
	// 검색조건이 없는 게시물의 총 갯수 알아오기
	int getTotalCountNoSearch();

	// 검색조건이 없는 것 또는 검색조건이 있는 것  목록 가져오기(페이징 처리함)
	List<BoardVO> boarListPaging(HashMap<String, String> paraMap);

	
	
	
/*	//tblBoard 테이블에서 groupno 컬럼의 최대값 알아오기
	int getGroupnoMax();

*/

}
