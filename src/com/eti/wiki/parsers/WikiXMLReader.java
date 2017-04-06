package com.eti.wiki.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.LoggerFactory;

public class WikiXMLReader extends Observable {

	private String fileName;
	private BufferedReader fileReader;

	public WikiXMLReader(String file) {
		fileName = file;
	}

	public void read() {
		openFile();
		readFile();
		closeFile();
	}

	private void closeFile() {
		try {
			fileReader.close();
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error opening file: " + e.getMessage());
			e.printStackTrace();
		}
		LoggerFactory.getLogger(getClass()).info("File has been closed.");
	}

	private void openFile() {
		try {
			fileReader = new BufferedReader(new FileReader(fileName));
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error opening file: " + e.getMessage());
			e.printStackTrace();
		}
		LoggerFactory.getLogger(getClass()).info("File has been opened");
	}

	private void readFile() {
		LoggerFactory.getLogger(getClass()).info("File read started.");
		String page = readPage();

		int counter = 0;
		Long timeStart = System.currentTimeMillis();
		while (page != null) {
			setChanged();
			notifyObservers(page);

			page = readPage();
			if (((counter++) % 10000) == 0) {
				LoggerFactory.getLogger(getClass()).info("Parsed: " + counter + " pages. Parse time = "
						+ String.valueOf(System.currentTimeMillis() - timeStart));
				timeStart = System.currentTimeMillis();
				System.gc();
			}
		}
		LoggerFactory.getLogger(getClass()).info("File read finished.");
	}

	private String readPage() {
		StringBuilder builder = new StringBuilder();

		String line = null;
		boolean pageStarted = false, pageEnded = false;
		try {
			line = fileReader.readLine();

			if (line == null) {
				return null;
			}

			while (line != null && !pageEnded) {
				if (line.contains("<page>")) {
					pageStarted = true;
				}
				if (pageStarted) {
					builder.append(line);
				}
				if (line.contains("</page>")) {
					pageEnded = true;
				} else {
					line = fileReader.readLine();
				}
			}
			LoggerFactory.getLogger(getClass()).debug("Page is : " + builder.toString());
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error reading file: " + e.getMessage());
			e.printStackTrace();
		}

		return builder.toString();
	}
}
