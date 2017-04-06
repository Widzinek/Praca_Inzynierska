package com.eti.wiki.model;

import org.apache.commons.lang3.StringUtils;

import com.eti.wiki.Configuration;

public class PageContent implements Comparable<PageContent> {

	private Page page;
	private int keywordCount;
	private String pageContent;

	public PageContent(Page page, String pageContent) {
		super();
		this.page = page;
		this.pageContent = pageContent;
		this.keywordCount = 0;
		
		for(String keyword : Configuration.KEYWORDS){
			addKeywordCount(keyword);
		}
	}

	public void addKeywordCount(String keyword) {
		keywordCount += StringUtils.countMatches(pageContent, keyword);
	}

	@Override
	public int compareTo(PageContent o) {
		if (o.keywordCount < keywordCount) {
			return 1;
		} else if (o.keywordCount == keywordCount) {
			return 0;
		}
		return -1;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public int getKeywordCount() {
		return keywordCount;
	}

	public void setKeywordCount(int keywordCount) {
		this.keywordCount = keywordCount;
	}

	public String getPageContent() {
		return pageContent;
	}

	public void setPageContent(String pageContent) {
		this.pageContent = pageContent;
	}

}
