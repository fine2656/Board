package com.spring.gsontest;

import java.util.List;

/*
	 직원명
	,나이
	,가족정보(List 형태) 
 */
public class Member {

	private String name;
	private int age;
	private List<Family> familys;
	
	public Member() { }
	
	public Member(String name, int age, List<Family> familys) {
		this.name = name;
		this.age = age;
		this.familys = familys;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public List<Family> getFamilys() {
		return familys;
	}

	public void setFamilys(List<Family> familys) {
		this.familys = familys;
	}
	
	
}
