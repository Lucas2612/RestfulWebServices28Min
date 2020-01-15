package com.rest.webservices.restfulwebservices.user;

public class Post {

	private Integer id;
	private Integer userId;
	private String text;
	
	public Post(Integer id, Integer userId, String text) {
		super();
		this.id = id;
		this.userId = userId;
		this.text = text;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	
}
