package com.spring.gsontest;

public class Family {

	private String relation;
	private String name;
	private int age;
	
	public Family() { }
	
	public Family(String relation, String name, int age) {
		this.relation = relation;
		this.name = name;
		this.age = age;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
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
	
	
}
