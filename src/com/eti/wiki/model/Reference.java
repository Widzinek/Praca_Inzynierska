package com.eti.wiki.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "wikireference")
public class Reference {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	@Column(name = "page_id")
	private long page_id;

	@Column(name = "reference_id")
	private long ref_id;

	public Reference(long page_id, long ref_id) {
		super();
		this.page_id = page_id;
		this.ref_id = ref_id;
	}

	public long getPage_id() {
		return page_id;
	}

	public void setPage_id(long page_id) {
		this.page_id = page_id;
	}

	public long getRef_id() {
		return ref_id;
	}

	public void setRef_id(long ref_id) {
		this.ref_id = ref_id;
	}
}
