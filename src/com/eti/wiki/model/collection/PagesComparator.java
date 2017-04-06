package com.eti.wiki.model.collection;

import java.util.Comparator;

import com.eti.wiki.model.PageContent;

public class PagesComparator implements Comparator<PageContent> {
	@Override
	public int compare(PageContent o1, PageContent o2) {
		return o1.compareTo(o2);
	}

}
