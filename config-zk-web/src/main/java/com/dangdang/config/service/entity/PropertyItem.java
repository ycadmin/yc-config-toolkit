package com.dangdang.config.service.entity;

import java.io.Serializable;

/**
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class PropertyItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String value;
	private String crateTimeString;
	private String modifyTimeString;
	private String version;

	public PropertyItem(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getCrateTimeString() {
		return crateTimeString;
	}

	public void setCrateTimeString(String crateTimeString) {
		this.crateTimeString = crateTimeString;
	}

	public String getModifyTimeString() {
		return modifyTimeString;
	}

	public void setModifyTimeString(String modifyTimeString) {
		this.modifyTimeString = modifyTimeString;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
