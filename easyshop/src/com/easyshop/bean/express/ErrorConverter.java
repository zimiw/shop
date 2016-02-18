package com.easyshop.bean.express;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author luocz
 */
public class ErrorConverter implements Converter {

	@Override
	public boolean canConvert(Class type) {
		return type.equals(Error.class);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Error error = (Error) source;
		if (error != null) {
			if (null != error.getCode()) {
				writer.addAttribute("code", error.getCode());
			}
			writer.setValue(error.getValue() == null ? "" : error.getValue());
		}

	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		Error error = new Error();
		String code = reader.getAttribute("code");
		error.setCode(code);
		String value = reader.getValue();
		error.setValue(value);
		return error;
	}

}
