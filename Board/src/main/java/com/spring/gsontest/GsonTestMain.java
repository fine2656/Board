package com.spring.gsontest;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

// Member 클래스와 Family 클래스를 Gson 객체를 이용하여 JSON 표현식으로 변환했다가
// 다시 자바 객체로 되돌리는 연습을 해보도록 한다.

public class GsonTestMain {

	public static void main(String[] args) {
		
		List<Family> familyList1 = new ArrayList<Family>();
		
		familyList1.add(new Family("아버지", "홍아빠", 50)); 
		familyList1.add(new Family("어머니", "김엄마", 45)); 
		familyList1.add(new Family("형", "홍형님", 30)); 
		familyList1.add(new Family("동생", "홍동생", 23)); 
		
		Member member1 = new Member("홍길동", 26, familyList1);
		
		Gson gson = new Gson();
		String json_member1 = gson.toJson(member1); // member1 을 json으로 형태로 바꾸어 줄것이다.
		
		System.out.println("====== JSON 표현식으로 변환 =======");
		System.out.println(json_member1);
	/*
	 ====== JSON 표현식으로 변환 =======
	 {"name":"홍길동","age":26,"familys":[{"relation":"아버지","name":"홍아빠","age":50},{"relation":"어머니","name":"김엄마","age":45},{"relation":"형","name":"홍형님","age":30},{"relation":"동생","name":"홍동생","age":23}]} 
	
	 */
	    String json_member2 = "{\"name\":\"이순신\",\"age\":27,\"familys\":[{\"relation\":\"아버지\",\"name\":\"순신아빠\",\"age\":51},{\"relation\":\"어머니\",\"name\":\"순신엄마\",\"age\":48},{\"relation\":\"동생\",\"name\":\"순신동생\",\"age\":24}]}"; 
		//String 타입의 배열로 만들어준다
	    
		System.out.println("");
		
		System.out.println("====== 자바객체로 복원 =======");
		Member mem1 = gson.fromJson(json_member1, Member.class); // Member.class 의 객체로 보내겠다.
		Member mem2 = gson.fromJson(json_member2, Member.class); 
		
		System.out.println("1.회원명: " + mem1.getName());
		System.out.println("2.나이: " + mem1.getAge());
		System.out.println("3.가족정보: ");
		for(Family fm : mem1.getFamilys()) {
			StringBuilder sb = new StringBuilder();
			sb.append("   가족관계=> ");
			sb.append(fm.getRelation());
			sb.append("   성명=> ");
			sb.append(fm.getName());
			sb.append("   나이=> ");
			sb.append(fm.getAge());
			String familyInfo = sb.toString();
			System.out.println(familyInfo);
		}
		/*====== 자바객체로 복원 =======
	1.회원명: 홍길동
	2.나이: 26
	3.가족정보: 
   	가족관계=> 아버지   성명=> 홍아빠   나이=> 50
   	가족관계=> 어머니   성명=> 김엄마   나이=> 45
   	가족관계=> 형   성명=> 홍형님   나이=> 30
   	가족관계=> 동생   성명=> 홍동생   나이=> 23
   */
		
		System.out.println("########################################");
		
		System.out.println("1.회원명: " + mem2.getName());
		System.out.println("2.나이: " + mem2.getAge());
		System.out.println("3.가족정보: ");
		for(Family fm : mem2.getFamilys()) {
			StringBuilder sb = new StringBuilder();
			sb.append("   가족관계=> ");
			sb.append(fm.getRelation());
			sb.append("   성명=> ");
			sb.append(fm.getName());
			sb.append("   나이=> ");
			sb.append(fm.getAge());
			String familyInfo = sb.toString();
			System.out.println(familyInfo);
		}
		
  /*
	 ====== 자바객체로 복원 =======
	1.회원명: 홍길동
	2.나이: 26
	3.학교정보: 
	   가족관계=> 아버지   성명=> 홍아빠   나이=> 50
	   가족관계=> 어머니   성명=> 김엄마   나이=> 45
	   가족관계=> 형   성명=> 홍형님   나이=> 30
	   가족관계=> 동생   성명=> 홍동생   나이=> 23

   */
		
	}

}
