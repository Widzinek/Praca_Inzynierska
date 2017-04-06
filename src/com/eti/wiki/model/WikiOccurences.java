package com.eti.wiki.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="wikioccurences")
public class WikiOccurences {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column(name="content")
	private String content;
	
	@Column(name="title")
	private String title;

	@Column(name="occurences")
	private int occurences;

	@Column(name="keywords")
	private String keywords;
	
	public WikiOccurences(long id, String content, String title, int occurences, String keywords) {
		super();
		this.id = id;
		this.content = content;
		this.title = title;
		this.occurences = occurences;
		this.keywords = keywords;
	}

	public int getOccurences() {
		return occurences;
	}

	public void setOccurences(int occurences) {
		this.occurences = occurences;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
