package com.easyshop.core.ehcache;

import java.io.Serializable;

/**
 * 缓存存储对象
 * 
 * @author luocz
 *
 */
public class EhcacheData implements Serializable {

	public EhcacheData(Object data) {
		this.data = data;
	}

	private Object data;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EhcacheData [data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}

}
