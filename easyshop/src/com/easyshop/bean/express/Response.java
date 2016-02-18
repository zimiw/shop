package com.easyshop.bean.express;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author luocz
 */
@XStreamAlias("Response")
public class Response {

	@XStreamAsAttribute
	private String service;

	private String Head;

	private Body Body;

	private Error ERROR;

	public Response(String service, String Head) {
		this.service = service;
		this.Head = Head;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getHead() {
		return Head;
	}

	public void setHead(String head) {
		Head = head;
	}

	public Body getBody() {
		return Body;
	}

	public void setBody(Body body) {
		Body = body;
	}

	public Error getERROR() {
		return ERROR;
	}

	public void setERROR(Error eRROR) {
		ERROR = eRROR;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Response [service=");
		builder.append(service);
		builder.append(", Head=");
		builder.append(Head);
		builder.append(", Body=");
		builder.append(Body);
		builder.append(", ERROR=");
		builder.append(ERROR);
		builder.append("]");
		return builder.toString();
	}

}
