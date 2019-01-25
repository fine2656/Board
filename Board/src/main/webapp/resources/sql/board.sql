----- **** board(게시판) **** -----

create table tilestest_img_advertise
(imgno          number not null
,imgfilename    varchar2(100) not null
,constraint PK_tilestest_img_advertise primary key(imgno)
);

create sequence seq_img_advertise
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

insert into tilestest_img_advertise values(seq_img_advertise.nextval, '미샤.png');
insert into tilestest_img_advertise values(seq_img_advertise.nextval, '원더플레이스.png');
insert into tilestest_img_advertise values(seq_img_advertise.nextval, '레노보.png');
insert into tilestest_img_advertise values(seq_img_advertise.nextval, '동원.png');
commit;

select imgfilename
from tilestest_img_advertise
order by imgno desc;


select *
from jsp_member
order by gradelevel, idx asc;


-- 로그온 되어지는 회원에게 등급레벨을 부여하여 접근권한을 다르게 설정하도록 하겠다.
alter table jsp_member
modify  gradelevel number(2) default 1;
  
update jsp_member set gradelevel=10 where userid in ('admin','suwook');

select idx,userid,name,email,gradelevel
        ,trunc(months_between(sysdate,last_logindate)) AS lastlogindategap
         ,trunc(months_between(sysdate,lastpwdchagedate)) as pwdchangegap      
         ,lastLoginDate
from jsp_member
where status =1 and 
userid='leess';


update jsp_member set lastLoginDate = sysdate
where userid='suwook';
        
create table tblBoard
(seq            number                not null   -- 글번호
,fk_userid      varchar2(20)          not null   -- 사용자ID
,name           Nvarchar2(20)         not null   -- 글쓴이
,subject        Nvarchar2(200)        not null   -- 글제목
,content        Nvarchar2(2000)       not null   -- 글내용    -- clob
,pw             varchar2(20)          not null   -- 글암호
,readCount      number default 0      not null   -- 글조회수
,regDate        date default sysdate  not null   -- 글쓴시간
,status         number(1) default 1   not null   -- 글삭제여부  1:사용가능한글,  0:삭제된글 
,constraint  PK_tblBoard_seq primary key(seq)
,constraint  FK_tblBoard_userid foreign key(fk_userid) references jsp_member(userid)
,constraint  CK_tblBoard_status check( status in(0,1) )
);

create sequence boardSeq
start with 1
increment by 1
nomaxvalue 
nominvalue
nocycle
nocache;

select *
from jsp_member
order by gradelevel desc, idx asc;

select *
from tblBoard;

select seq,fk_userid,name,subject,pw,readcount,regdate
from tblBoard
where status =1
order by seq desc;

select seq,fk_userid,name,subject,pw,readCount,regdate
from tblBoard
where status =1
order by seq desc;




select *
from tblBoard;


------------------------------------------------------------------------
   ----- **** 댓글 테이블 생성 **** -----
  
create table tblComment
(seq           number               not null   -- 댓글번호
,fk_userid     varchar2(20)         not null   -- 사용자ID
,name          varchar2(20)         not null   -- 성명
,content       varchar2(1000)       not null   -- 댓글내용
,regDate       date default sysdate not null   -- 작성일자
,parentSeq     number               not null   -- 원게시물 글번호
,status        number(1) default 1  not null   -- 글삭제여부
                                               -- 1 : 사용가능한 글,  0 : 삭제된 글
                                               -- 댓글은 원글이 삭제되면 자동적으로 삭제되어야 한다.
,constraint PK_tblComment_seq primary key(seq)
,constraint FK_tblComment_userid foreign key(fk_userid)
                                    references jsp_member(userid)
,constraint FK_tblComment_parentSeq foreign key(parentSeq) 
                                      references tblBoard(seq) on delete cascade -- 부모 글이 삭제되어지면은 자식 테이블의 글도 삭제된다
,constraint CK_tblComment_status check( status in(1,0) ) 
);

create sequence commentSeq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

/*
 댓글쓰기(tblComment 테이블)를 성공하면 원게시물(tblBoard 테이블)에
 댓글의 갯수(1씩 증가)를 알려주는 컬럼 commentCount 을 추가하겠다.
*/
alter table tblBoard
add commentCount number default 0 not null;

select *
from tblBoard
order by seq desc;


select *
from tblComment
order by seq desc;

-- === ---- ==== tblBoard 테이블 페이징 처리를 위해 데이터 입력하기 ===== ---- === ---
begin
    for i in 1..100 loop
    insert into tblBoard(seq,fk_userid,name,subject,content,pw,regdate,status,commentCount)
    values(boardSeq.nextval,'leess','이순신','이순신 '||i||'번째 글입니다',i||'. 새해 복 많이 받으세요~~!!!','1234',sysdate,default,default);
end loop;
end;

commit;



update tblBoard set commentCount = (select count(*) from tblComment where parentSeq = 6)
where seq = 6;

select rno,name,content,regdate
from 
(
    select rownum AS rno,name,content,regdate
    from 
    (
        select name,content,to_char(regdate,'yyyy-mm-dd hh24:mi:ss') AS regdate 
        from tblComment
        where parentSeq = 6
        order by seq desc
    )V
)T
where T.rno between 6 and 10;





insert into tblComment(seq,fk_userid,name,content,regdate,parentseq,status)
values(commentSeq.nextval,'suwook','최수욱',101||'. 행운가득 좋은하루 되세요~~',sysdate,6,default);

select seq,fk_userid,name,subject,pw,readCount,regDate,commentCount
from 
(
    select rownum AS rno,seq,fk_userid,name,subject,pw,readCount,regDate,commentCount
    from 
    (
        select seq,fk_userid,name,subject,pw,readCount,regDate,commentCount
        from tblBoard
        order by seq desc
    )v
)T
where rno between 1 and 10;

-----------------------------------------------------------
         ---- *** 답변형 게시판 *** ----
-----------------------------------------------------------

create table tblComment_backup
as
select *
from tblComment
order by seq asc;

create table tblBoard_backup
as
select *
from tblBoard
order by seq asc;

drop table tblBoard purge;




create table tblBoard
(seq           number                not null   -- 글번호
,fk_userid     varchar2(20)          not null   -- 사용자ID
,name          Nvarchar2(20)         not null   -- 글쓴이
,subject       Nvarchar2(200)        not null   -- 글제목
,content       Nvarchar2(2000)       not null   -- 글내용    -- clob
,pw            varchar2(20)          not null   -- 글암호
,readCount     number default 0      not null   -- 글조회수
,regDate       date default sysdate  not null   -- 글쓴시간
,status        number(1) default 1   not null   -- 글삭제여부  1:사용가능한글,  0:삭제된글 
,commentCount  number default 0      not null   -- 댓글수
,groupno       number                not null   -- 답변글쓰기에 있어서 그룹번호
                                                -- 원글(부모글)과 답변글은 동일한 groupno 를 가진다. 
                                                -- 답변글이 아닌 원글(부모글)인 경우 groupno 의 값은 groupno 컬럼의 최대값(max)+1 로 한다.  
                                                
,fk_seq        number default 0      not null   -- fk_seq 컬럼은 절대로 foreign key가 아니다.
                                                -- fk_seq 컬럼은 자신의 글(답변글)에 있어서 
                                                -- 원글(부모글)이 누구인지에 대한 정보값이다.
                                                -- 답변글쓰기에 있어서 답변글이라면 fk_seq 컬럼의 값은 
                                                -- 원글(부모글)의 seq 컬럼의 값을 가지게 되며,
                                                -- 답변글이 아닌 원글일 경우 0 을 가지도록 한다.
                                                
,depthno       number default 0       not null  -- 답변글쓰기에 있어서 답변글 이라면                                                
                                                -- 원글(부모글)의 depthno + 1 을 가지게 되며,
                                                -- 답변글이 아닌 원글일 경우 0 을 가지도록 한다.
,constraint  PK_tblBoard_seq primary key(seq)
,constraint  FK_tblBoard_userid foreign key(fk_userid) references jsp_member(userid)
,constraint  CK_tblBoard_status check( status in(0,1) )
);

drop sequence boardSeq;


create sequence boardSeq
start with 1
increment by 1
nomaxvalue 
nominvalue
nocycle
nocache;


create table tblComment
(seq         number                not null   -- 댓글번호
,fk_userid   varchar2(20)          not null   -- 사용자ID
,name        varchar2(20)          not null   -- 성명
,content     varchar2(1000)        not null   -- 댓글내용
,regDate     date default sysdate  not null   -- 작성일자
,parentSeq   number                not null   -- 원게시물 글번호
,status      number(1) default 1   not null   -- 글삭제여부
                                              -- 1 : 사용가능한 글,  0 : 삭제된 글
                                              -- 댓글은 원글이 삭제되면 자동적으로 삭제되어야 한다.
,constraint PK_tblComment_seq primary key(seq)
,constraint FK_tblComment_userid foreign key(fk_userid)
                                    references jsp_member(userid)
,constraint FK_tblComment_parentSeq foreign key(parentSeq) 
                                      references tblBoard(seq) on delete cascade
,constraint CK_tblComment_status check( status in(1,0) ) 
);

drop sequence commentSeq;

create sequence commentSeq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

select *
from tblBoard
order by seq asc;

insert into tblBoard
select seq, fk_userid, name, subject, content, pw, readcount, regdate, status, commentcount, rownum as groupno, 0, 0
from
(
 select seq, fk_userid, name, subject, content, pw, readcount, regdate, status, commentcount 
 from tblBoard_backup
 order by seq asc
)V;

insert into tblComment
select *
from tblComment_backup
order by seq asc;

-- 시퀀스 맞추기
select max(seq)
from tblBoard; -- 208

drop sequence boardSeq;

create sequence boardSeq
start with 208
increment by 1
nomaxvalue 
nominvalue
nocycle
nocache;

select max(seq)
from tblComment; -- 106

drop sequence commentSeq;

create sequence commentSeq
start with 107
increment by 1
nomaxvalue 
nominvalue
nocycle
nocache;

drop table tblComment_backup purge;
drop table tblBoard_backup purge;

select *
from tblBoard
order by seq asc;

select *
from tblComment
order by seq asc;



select seq,fk_userid,name,subject,pw,readCount,regDate,commentCount,groupno,fk_seq,depthno
		from 
		(
		    select rownum AS rno,seq,fk_userid,name,subject,pw,readCount,regDate,commentCount,groupno,fk_seq,depthno
		    from 
		    (
		        select seq,fk_userid,name,subject,pw,readCount,regDate,commentCount
		        ,groupno,fk_seq,depthno
		        from tblBoard		 
		        where status = 1
	  
	   			start with fk_seq  = 0
	   			connect by prior seq =  fk_seq 
		        order siblings by groupno desc, seq asc
		    )v
		)T
		where rno between 1 and 10;
        
        
        ------ *** 파일 첨부를 위해서 tblBoard 테이블에 컬럼 추가하기 ***-----
        
    alter table tblBoard 
    add fileName varchar2(255);
    -- WAS(톰캣)에 저장될 파일명(20190107091235.png)
    
    alter table tblBoard
    add orgFilename varchar2(255);
    -- 진짜 파일명(강이지.png) 사용자가 업로드 하거나 파일을 다운로드 할대 사용되어 지는 파일명
    
    alter table tblBoard
    add fileSize number;
    -- 파일 크기
    
  -- 최종 게시판 테이블 !--
  -- 
  
create table tblBoard
(seq           number                not null   -- 글번호
,fk_userid     varchar2(20)          not null   -- 사용자ID
,name          Nvarchar2(20)         not null   -- 글쓴이
,subject       Nvarchar2(200)        not null   -- 글제목
,content       Nvarchar2(2000)       not null   -- 글내용    -- clob
,pw            varchar2(20)          not null   -- 글암호
,readCount     number default 0      not null   -- 글조회수
,regDate       date default sysdate  not null   -- 글쓴시간
,status        number(1) default 1   not null   -- 글삭제여부  1:사용가능한글,  0:삭제된글 
,commentCount  number default 0      not null   -- 댓글수
,groupno       number                not null   -- 답변글쓰기에 있어서 그룹번호
                                                -- 원글(부모글)과 답변글은 동일한 groupno 를 가진다. 
                                                -- 답변글이 아닌 원글(부모글)인 경우 groupno 의 값은 groupno 컬럼의 최대값(max)+1 로 한다.  
                                                
,fk_seq        number default 0      not null   -- fk_seq 컬럼은 절대로 foreign key가 아니다.
                                                -- fk_seq 컬럼은 자신의 글(답변글)에 있어서 
                                                -- 원글(부모글)이 누구인지에 대한 정보값이다.
                                                -- 답변글쓰기에 있어서 답변글이라면 fk_seq 컬럼의 값은 
                                                -- 원글(부모글)의 seq 컬럼의 값을 가지게 되며,
                                                -- 답변글이 아닌 원글일 경우 0 을 가지도록 한다.
                                                
,depthno       number default 0       not null  -- 답변글쓰기에 있어서 답변글 이라면                                                
                                                -- 원글(부모글)의 depthno + 1 을 가지게 되며,
                                                -- 답변글이 아닌 원글일 경우 0 을 가지도록 한다.
,fileName       varchar2(255)                   -- WAS(톰캣)에 저장될 파일명(20190107091235.png)
,orgFilename    varchar2(255)                   -- 진짜 파일명(강이지.png) 사용자가 업로드 하거나 파일을 다운로드 할대 사용되어 지는 파일명
,fileSize       number                          -- 파일 크기
,constraint  PK_tblBoard_seq primary key(seq)
,constraint  FK_tblBoard_userid foreign key(fk_userid) references jsp_member(userid)
,constraint  CK_tblBoard_status check( status in(0,1) )
);

select *
from tblBoard;

  ---- **** === 스낵제품 구입에 따른 차트그리기 === **** ----

create table chart_snack
(snackno       number not null          -- 스낵번호(대분류, 시퀀스)
,snackname     varchar2(100) not null   -- 제품명
,constraint PK_chart_snack primary key(snackno)
);

create sequence seq_chart_snack
start with 1000
increment by 1000
nomaxvalue
nominvalue
nocycle
nocache;

insert into chart_snack values(seq_chart_snack.nextval, '감자깡');
insert into chart_snack values(seq_chart_snack.nextval, '새우깡');
insert into chart_snack values(seq_chart_snack.nextval, '양파링');
insert into chart_snack values(seq_chart_snack.nextval, '고구마깡');
insert into chart_snack values(seq_chart_snack.nextval, '빼빼로');
commit;

select *
from chart_snack;


create table chart_snackType         -- 동일한 스낵제품중에서 세분류 되어진 스낵타입 테이블
(typecode    varchar2(50)            -- 타입코드
,typename    varchar2(100)           -- 타입명
,constraint PK_chart_snackType primary key(typecode)
);

insert into chart_snackType values('taste_1', '매운맛');
insert into chart_snackType values('taste_2', '순한맛');
insert into chart_snackType values('taste_3', '달콤맛');
insert into chart_snackType values('taste_4', '고소한맛');
insert into chart_snackType values('taste_5', '순한맛');
insert into chart_snackType values('size_1', '소량');
insert into chart_snackType values('size_2', '중량');
insert into chart_snackType values('size_3', '대량');
insert into chart_snackType values('madein_1', '국산');
insert into chart_snackType values('madein_2', '중국산');
insert into chart_snackType values('makecompany_1', '롯데');
insert into chart_snackType values('makecompany_2', '오리온');
insert into chart_snackType values('makecompany_3', '해태');

commit;

create table chart_snackDetail
(snackDetailno        number        not null          -- 스낵제품 상세번호(소분류, 시퀀스)
,fk_snackno           number        not null          -- 스낵번호(대분류)
,fk_typecode          varchar2(50)  not null          -- 타입코드(중분류)
,constraint PK_chart_snackDetail  primary key(snackDetailno)
,constraint FK_chart_snackDetail_1  foreign key(fk_snackno)
                                       references chart_snack(snackno)
,constraint FK_chart_snackDetail_2  foreign key(fk_typecode)
                                       references chart_snackType(typecode)                                  
);


create sequence seq_chart_snackDetail 
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

insert into chart_snackDetail values(seq_chart_snackDetail.nextval, 1000, 'taste_1');
insert into chart_snackDetail values(seq_chart_snackDetail.nextval, 1000, 'taste_2');
insert into chart_snackDetail values(seq_chart_snackDetail.nextval, 1000, 'taste_3');
insert into chart_snackDetail values(seq_chart_snackDetail.nextval, 2000, 'size_1');
insert into chart_snackDetail values(seq_chart_snackDetail.nextval, 2000, 'size_2');
insert into chart_snackDetail values(seq_chart_snackDetail.nextval, 2000, 'size_3');
insert into chart_snackDetail values(seq_chart_snackDetail.nextval, 3000, 'taste_4');
insert into chart_snackDetail values(seq_chart_snackDetail.nextval, 3000, 'taste_5');
insert into chart_snackDetail values(seq_chart_snackDetail.nextval, 4000, 'madein_1');
insert into chart_snackDetail values(seq_chart_snackDetail.nextval, 4000, 'madein_2');
insert into chart_snackDetail values(seq_chart_snackDetail.nextval, 5000, 'makecompany_1');
insert into chart_snackDetail values(seq_chart_snackDetail.nextval, 5000, 'makecompany_2');
insert into chart_snackDetail values(seq_chart_snackDetail.nextval, 5000, 'makecompany_3');

commit;

select *
from chart_snackDetail;

select snackno, snackname
from chart_snack
where snackno in (select distinct fk_snackno from chart_snackDetail)
order by snackno asc;


select typecode, typename
from chart_snackType
where typecode in(select fk_typecode
                  from chart_snackDetail
                  where fk_snackno = 1000);


create table chart_snackOrder
(orderno     number not null          -- 주문번호(전표, 시퀀스)
,userid      varchar2(20) not null    -- 사용자ID
,orderday    date default sysdate     -- 주문일자
,constraint  PK_chart_snackOrder_orderno primary key(orderno)
,constraint  FK_chart_snackOrder_userid foreign key(userid) 
                                           references jsp_member(userid)
);


create sequence seq_chart_snackOrder
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

/*
insert into chart_snackOrder(orderno, userid, orderday)
values(seq_chart_snackOrder.nextval, 'seoyh', default);
*/

select *
from chart_snackOrder
order by orderno desc;


create table chart_snackOrderDetail 
(orderDetailno      number not null   -- 주문상세번호(시퀀스)
,fk_orderno         number not null   -- 주문번호(전표)
,fk_snackDetailno   number not null   -- 제품상세번호
,oqty               number not null   -- 주문량
,constraint  PK_chart_snackOrderDetail  primary key(orderDetailno)
,constraint  FK_orderDetail_orderno foreign key(fk_orderno)
                                               references chart_snackOrder(orderno)
,constraint  FK_orderDetail_jepumDetailno foreign key(fk_snackDetailno)     
                                               references chart_snackDetail(snackDetailno)
);

create sequence seq_chart_snackOrderDetail
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

/*
insert into chart_snackOrderDetail(orderDetailno, fk_orderno, fk_snackDetailno, oqty)
values(seq_chart_snackOrderDetail.nextval
    , (select max(orderno) from chart_snackOrder where userid = 'seoyh')
    , (select snackDetailno
       from chart_snackDetail
       where fk_snackno = 1000 and fk_typecode = 'taste_1')
    , 3);
*/

select *
from chart_snackOrderDetail 
order by orderDetailno desc;


select rank() over(order by sum(A.oqty) desc) as ranking
     , C.snackname 
     , sum(A.oqty) as totalqty
     , trunc( sum(A.oqty)/(select sum(oqty) from chart_snackOrderDetail) * 100, 1) as percentage
from chart_snackOrderDetail A left join chart_snackDetail B
on A.fk_snackDetailno = B.snackDetailno
left join chart_snack C
on B.fk_snackno = C.snackno
group by C.snackname;


select C.typename as TYPENAME
     , trunc( sum(A.oqty)/( select sum(oqty) 
                            from chart_snackOrderDetail) * 100, 1) as PERCENT
from chart_snackOrderDetail A left join chart_snackDetail B
on A.fk_snackDetailno = B.snackDetailno
left join chart_snackType C
on B.fk_typecode = C.typecode
left join chart_snack D
on B.fk_snackno = D.snackno
where D.snackname = '감자깡'
group by C.typename
order by TYPENAME;

-----------------------------------------------------------------------------------------------
select C.typename as TYPENAME
     , trunc( sum(A.oqty)/( select sum(oqty) 
                            from chart_snackOrderDetail
                            where fk_snackdetailno in (select SD.snackdetailno
                                                       from chart_snack S join chart_snackDetail SD
                                                       on S.snackno = SD.fk_snackno
                                                       where S.snackname = '감자깡') ) * 100, 1) as PERCENT
from chart_snackOrderDetail A left join chart_snackDetail B
on A.fk_snackDetailno = B.snackDetailno
left join chart_snackType C
on B.fk_typecode = C.typecode
left join chart_snack D
on B.fk_snackno = D.snackno
where D.snackname = '감자깡'
group by C.typename
order by TYPENAME;
--------------------------------------------------------------------------------------------


select D.snackname 
     , sum(B.oqty) as totalqty
     , trunc( sum(B.oqty)/(select sum(oqty) from chart_snackOrderDetail) * 100, 1) as percentage
from chart_snackOrder A join chart_snackOrderDetail B 
on A.orderno = B.fk_orderno
join chart_snackDetail C
on B.fk_snackDetailno = C.snackDetailno
left join chart_snack D
on C.fk_snackno = D.snackno
where A.userid = 'seoyh'
group by D.snackname
order by totalqty desc;



select ranking, snackname, typename, totalqty, percentage
from
(
select rank() over(order by sum(A.oqty) desc) as RANKING
     , D.snackname, C.typename, sum(A.oqty) as TOTALQTY
     , trunc( sum(A.oqty)/(select sum(oqty) from chart_snackOrderDetail) * 100, 1) as PERCENTAGE
from chart_snackOrderDetail A left join chart_snackDetail B
on A.fk_snackDetailno = B.snackDetailno
left join chart_snackType C
on B.fk_typecode = C.typecode
left join chart_snack D
on B.fk_snackno = D.snackno
group by D.snackname, C.typename
) V
where V.snackname = '감자깡';      
          