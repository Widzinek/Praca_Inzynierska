package com.eti.wiki.parsers;

import java.io.StringReader;
import java.util.Observable;
import java.util.Observer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.eti.wiki.Configuration;
import com.eti.wiki.database.DatabaseSession;
import com.eti.wiki.model.Page;
import com.eti.wiki.model.PageContent;
import com.eti.wiki.model.WikiOccurences;
import com.eti.wiki.model.collection.PagesCollection;

public class WikiXMLKeywordFinder implements Observer {
	private DocumentBuilder builder;
	private PagesCollection collection;

	public WikiXMLKeywordFinder() {
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			LoggerFactory.getLogger(getClass()).error("Parser not created error.");
		}

		collection = new PagesCollection();
	}

	public PagesCollection getQueue() {
		return collection;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof String) {
			String pageString = String.valueOf(arg);
			parsePage(pageString);

			LoggerFactory.getLogger(getClass()).debug("Page parsed.");
		}
	}

	private Node extractContent(Node revisionNode) {
		NodeList nodeList = revisionNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE && currentNode.getNodeName().equals("text")) {
				return currentNode;
			}
		}
		return null;
	}

	private void parsePage(String pageString) {
		try {
			Document doc = loadXMLFromString(pageString);
			if (doc.hasChildNodes()) {
				Node root = doc.getElementsByTagName("page").item(0);
				if (root.hasChildNodes()) {
					// Node idNode = null, titleNode = null, contentNode= null;

					NodeList nodeList = root.getChildNodes();
					PageContent pageContent = parsePageForKeywordCount(nodeList);

					collection.add(pageContent);
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Parser not created error.");
			e.printStackTrace();
		}
	}

	private Document loadXMLFromString(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

	private PageContent parsePageForKeywordCount(NodeList nodeList) {
		Page page = new Page();
		String content = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				if (currentNode.getNodeName().equals("title")) {
					// titleNode = currentNode;
					page.setTitle(currentNode.getTextContent());

				}
				if (currentNode.getNodeName().equals("id")) {
					// idNode = currentNode;
					try {
						page.setId(Integer.parseInt(currentNode.getTextContent()));
					} catch (NumberFormatException e) {
						LoggerFactory.getLogger(getClass()).error("Parser not created error.");
					}
				}
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					if (currentNode.getNodeName().equals("revision")) {
						// contentNode=extractContent(currentNode);
						content = extractContent(currentNode).getTextContent();
					}
				}
			}
		}
		return new PageContent(page, content);
	}

	public void insertIntoDatabase() {
		StringBuilder keywordsCombined = new StringBuilder();
		for (String keyword : Configuration.KEYWORDS) {
			keywordsCombined.append(keyword).append(";");
		}

		for (PageContent contentPage : getQueue().elements()) {
			WikiOccurences occurences = new WikiOccurences(contentPage.getPage().getId(), contentPage.getPageContent(),
					contentPage.getPage().getTitle(), contentPage.getKeywordCount(), keywordsCombined.toString());
			saveOccurencesIntoDatabase(occurences);
		}
	}
	
	private void saveOccurencesIntoDatabase(WikiOccurences pageToSave) {
		Session session = DatabaseSession.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.save(pageToSave);
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error saving page into database.");
			e.printStackTrace();
		} finally {
			transaction.commit();
			if (session != null) {
				session.close();
			}
		}
	}
}
