package com.eti.wiki.parsers;

import com.eti.wiki.Configuration;
import com.eti.wiki.model.PageContent;
import com.eti.wiki.model.WikiOccurences;

public class WikiXML {
	private WikiXMLReader reader;
	private WikiXMLPagesParser parserPag;
	private WikiXMLReferencesParser parserRef;
	private WikiXMLKeywordFinder parserKeyword;

	public WikiXML() {
		reader = new WikiXMLReader("D:/Wikipedia/enwiki-latest-pages-meta-current.xml");
		parserPag = new WikiXMLPagesParser();
		parserKeyword = new WikiXMLKeywordFinder();

		// first phase
		// reader.addObserver(parserPag);
		// reader.read();
		// reader.deleteObserver(parserPag);

		// second phase
		reader.addObserver(parserKeyword);
		reader.read();
		parserKeyword.insertIntoDatabase();
		reader.deleteObserver(parserKeyword);

		// third phase
		// parserRef = new WikiXMLReferencesParser(0);
		// reader.addObserver(parserRef);
		// reader.read();
		// reader.deleteObserver(parserRef);
		//
		// parserRef = new WikiXMLReferencesParser(1);
		// reader.addObserver(parserRef);
		// reader.read();
		// reader.deleteObserver(parserRef);

		// parserRef = new WikiXMLReferencesParser(2);
		// reader.addObserver(parserRef);
		// reader.read();
		// reader.deleteObserver(parserRef);
		System.exit(0);
	}
}
