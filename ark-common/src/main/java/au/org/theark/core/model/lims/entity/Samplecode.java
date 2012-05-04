/*******************************************************************************
 * Copyright (c) 2011  University of Western Australia. All rights reserved.
 * 
 * This file is part of The Ark.
 * 
 * The Ark is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * The Ark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package au.org.theark.core.model.lims.entity;

// Generated 15/06/2011 1:22:58 PM by Hibernate Tools 3.3.0.GA

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import au.org.theark.core.model.Constants;

/**
 * Samplecode generated by hbm2java
 */
@Entity
@Table(name = "samplecode", schema = Constants.LIMS_TABLE_SCHEMA)
public class Samplecode implements java.io.Serializable {


	private static final long serialVersionUID = 1L;
	private Long		id;
	private int			studyId;
	private String		sampletype;
	private String		samplesubtype;
	private Integer	sampletypeId;
	private String		code;
	private Integer	order;
	private String		childcode;

	public Samplecode() {
	}

	public Samplecode(Long id, int studyId, String sampletype) {
		this.id = id;
		this.studyId = studyId;
		this.sampletype = sampletype;
	}

	public Samplecode(Long id, int studyId, String sampletype, String samplesubtype, Integer sampletypeId, String code, Integer order, String childcode) {
		this.id = id;
		this.studyId = studyId;
		this.sampletype = sampletype;
		this.samplesubtype = samplesubtype;
		this.sampletypeId = sampletypeId;
		this.code = code;
		this.order = order;
		this.childcode = childcode;
	}

	@Id
	@SequenceGenerator(name = "samplecode_generator", sequenceName = "SAMPLECODE_SEQUENCE")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "samplecode_generator")
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "STUDY_ID", nullable = false)
	public int getStudyId() {
		return this.studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	@Column(name = "SAMPLETYPE", nullable = false, length = 100)
	public String getSampletype() {
		return this.sampletype;
	}

	public void setSampletype(String sampletype) {
		this.sampletype = sampletype;
	}

	@Column(name = "SAMPLESUBTYPE", length = 50)
	public String getSamplesubtype() {
		return this.samplesubtype;
	}

	public void setSamplesubtype(String samplesubtype) {
		this.samplesubtype = samplesubtype;
	}

	@Column(name = "SAMPLETYPE_ID")
	public Integer getSampletypeId() {
		return this.sampletypeId;
	}

	public void setSampletypeId(Integer sampletypeId) {
		this.sampletypeId = sampletypeId;
	}

	@Column(name = "CODE", length = 4)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "ORDER")
	public Integer getOrder() {
		return this.order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Column(name = "CHILDCODE", length = 4)
	public String getChildcode() {
		return this.childcode;
	}

	public void setChildcode(String childcode) {
		this.childcode = childcode;
	}

}
