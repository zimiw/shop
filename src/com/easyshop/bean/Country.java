package com.easyshop.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("country")
public class Country {
	@Id
	private int countryId;
	@Column
	private String name;
	@Column
	private String code;
}
