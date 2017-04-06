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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.eti.wiki.database.DatabaseSession;
import com.eti.wiki.model.Page;

public class WikiXMLPagesParser implements Observer {

	private DocumentBuilder builder;

	public WikiXMLPagesParser() {
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			LoggerFactory.getLogger(getClass()).error("Parser not created error.");
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof String) {
			String pageString = String.valueOf(arg);
			Page page = parsePage(pageString);

			LoggerFactory.getLogger(getClass()).debug("Page parsed.");
		}
	}

	private Page parsePage(String pageString) {
		try {
			Document doc = loadXMLFromString(pageString);
			if (doc.hasChildNodes()) {
				Node root = doc.getElementsByTagName("page").item(0);
				if (root.hasChildNodes()) {
					// Node idNode = null, titleNode = null, contentNode= null;

					NodeList nodeList = root.getChildNodes();
					Page page = parsePageFromNodes(nodeList);

					savePageIntoDatabase(page);
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Parser not created error.");
			e.printStackTrace();
		}

		return null;
	}

	private void savePageIntoDatabase(Page pageToSave) {
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

	private Page parsePageFromNodes(NodeList nodeList) {
		Page page = new Page();
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
			}
		}
		return page;
	}

	private Document loadXMLFromString(String xml) throws Exception {
		// DocumentBuilderFactory factory =
		// DocumentBuilderFactory.newInstance();
		// DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}
}
