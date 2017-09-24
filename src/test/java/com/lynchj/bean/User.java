package com.lynchj.bean;

import com.lynchj.annotation.PropertiesName;

public class User {

	@PropertiesName(name = "用户id")
	private String id;
	
	@PropertiesName(name = "用户姓名")
	private String name;
	
	@PropertiesName(name = "用户年龄")
	private Integer age;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
	
}
