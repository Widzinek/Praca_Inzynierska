package com.eti.wiki.parsers;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metamodel.relational.Database;
import org.slf4j.LoggerFactory;

import com.eti.wiki.database.DatabaseSession;
import com.eti.wiki.model.Page;
import com.eti.wiki.model.Reference;

public class PageParser {
	// private HashMap<String, Set<Long>> workingTitles;
	public HashMap<String, Long> resultMap = new HashMap<>();

	public PageParser(int batch) {
		for (int i = 0; i < 1; i++) {
			List<Page> resultPages = selectFromDatabase(i+(1*batch));

			for (Page p : resultPages) {
				resultMap.put(p.getTitle(), p.getId());
			}
			LoggerFactory.getLogger(getClass()).info("Parsed to map");
			System.gc();
		}
	}

	public void parseReferences(long pageId, String pageContent) {
		String substring = pageContent;
		String referenceSubstring = null, referenceTitle = null;

		int referenceStart = substring.indexOf("[["), referenceEnd = -1;
		while (referenceStart > 0) {
			referenceEnd = substring.indexOf("]]");
			if (referenceEnd > 0) {
				try {
					while (referenceStart > referenceEnd) {
						substring = substring.substring(referenceEnd + 2);

						referenceEnd = substring.indexOf("]]");
						if (substring.isEmpty()) {
							break;
						}
					}
					if (!substring.isEmpty()) {
						referenceSubstring = substring.substring(referenceStart, referenceEnd);
					}
				} catch (StringIndexOutOfBoundsException aioobe) {
					LoggerFactory.getLogger(getClass()).info("aioobe");
				}
				if (referenceSubstring != null) {
					int pipeIndex = referenceSubstring.indexOf("|");
					if (pipeIndex > 0) {
						try {
							referenceTitle = referenceSubstring.substring(2, pipeIndex);
						} catch (StringIndexOutOfBoundsException aioobe) {

						}
					} else {
						if (!referenceSubstring.isEmpty()) {
							try {
								referenceTitle = referenceSubstring.substring(2);
							} catch (StringIndexOutOfBoundsException aioobe) {
								// LoggerFactory.getLogger(getClass()).info("aioobe");
							}
						}
					}

					Long found = resultMap.get(referenceTitle);
					if (found != null) {
						saveReferenceToDatabase(pageId, found);
					}
				}

			} else {
				break;
			}
			try {
				substring = substring.substring(referenceEnd + 2);
			} catch (Exception e) {
				break;
			}
			referenceStart = substring.indexOf("[[");
		}
	}

	private void saveReferenceToDatabase(long pageId, long referenceId) {
		// LoggerFactory.getLogger(getClass()).info("Wstawiam rekord");
		Session session = DatabaseSession.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		try {
			if (pageId != -1 && referenceId != -1) {
				session.save(new Reference(pageId, referenceId));
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Error saving reference into database.");
			e.printStackTrace();
		} finally {
			transaction.commit();
			if (session != null) {
				session.close();
			}
		}
	}

	/**
	 * Pobranie X tys. rekordów na raz.
	 * 
	 * @param titles
	 * @return
	 */
	private List<Page> selectFromDatabase(int i) {
		Session session = DatabaseSession.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		try {
			Criteria c = session.createCriteria(Page.class);
			LoggerFactory.getLogger(getClass()).info("Fetching");
			List<Page> resultPages = (List<Page>) c.setFirstResult(i * 1000000).setMaxResults(1000000).list();
			LoggerFactory.getLogger(getClass()).info("Done");

			return resultPages;
		} catch (Exception e) {
			System.out.println("Exception:" + e.getMessage());
		} finally {
			transaction.commit();
			if (session != null) {
				session.close();
			}
		}
		return null;
	}
}
