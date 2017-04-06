package com.eti.wiki.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="wikipage")
public class Page {
	
	@Column(name="title")
	private String title;
	
	@Id
	@Column(name="id")
	private long id;
	
//	@Column(name="content")
//	private String contnet;
	
	public Page() {
		// TODO Auto-generated constructor stub
	}

	public Page(String title, int id/*, String contnet*/) {
		super();
		this.title = title;
		this.id = id;
//		this.contnet = contnet;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
//
//	public String getContnet() {
//		return contnet;
//	}
//
//	public void setContnet(String contnet) {
//		this.contnet = contnet;
//	}
}
