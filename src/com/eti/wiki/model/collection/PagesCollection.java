package com.eti.wiki.model.collection;

import java.util.LinkedList;
import java.util.PriorityQueue;

import com.eti.wiki.Configuration;
import com.eti.wiki.model.PageContent;

public class PagesCollection {

	public PriorityQueue<PageContent> list;

	public PagesCollection() {
		list = new PriorityQueue<>(new PagesComparator());
	}

	public void add(PageContent toAdd) {
		if(toAdd.getKeywordCount() <Configuration.OCCURENCES_THRESHOLD){
			return;
		}
		PageContent head = list.peek();
//		if(toAdd.getKeywordCount() > 6){
//			System.out.println("huj");
//		}
		if (head != null && head.compareTo(toAdd) != -1) {
			return;
		}
		list.add(toAdd);

		while (list.size() > Configuration.MAX_QUEUE_SIZE) {
			list.remove();
		}
	}

	public PriorityQueue<PageContent> elements() {
		return list;
	}

}
