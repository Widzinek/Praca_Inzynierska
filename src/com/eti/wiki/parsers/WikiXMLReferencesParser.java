package com.eti.wiki.parsers;

import java.io.StringReader;
import java.util.Observable;
import java.util.Observer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.eti.wiki.model.Page;

public class WikiXMLReferencesParser implements Observer {
	private PageParser pageParser;
	private DocumentBuilder builder;

	public WikiXMLReferencesParser(int batch) {
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			LoggerFactory.getLogger(getClass()).error("Parser not created error.");
		}

		pageParser = new PageParser(batch);
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
					parsePageForReferences(nodeList);
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

	private void parsePageForReferences(NodeList nodeList) {
		Long id = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				if (currentNode.getNodeName().equals("id")) {
					// idNode = currentNode;
					try {
						id = Long.parseLong(currentNode.getTextContent());
						break;
					} catch (NumberFormatException e) {
						LoggerFactory.getLogger(getClass()).error("Parser not created error.");
					}
				}
			}
		}

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				if (currentNode.getNodeName().equals("revision")) {
					// contentNode=extractContent(currentNode);
					pageParser.parseReferences(id, extractContent(currentNode).getTextContent());
				}
			}
		}
	}
}
