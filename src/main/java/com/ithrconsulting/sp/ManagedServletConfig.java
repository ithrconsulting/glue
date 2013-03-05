package com.ithrconsulting.sp;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class ManagedServletConfig implements ServletConfig {

	private ServletConfig config;
	private Map<String, String> params;

	public ManagedServletConfig(ServletConfig config, Map<String, String> initParameters) {
		this.config = config;
		this.params = initParameters;
	}

	public String getInitParameter(String key) {
		if (params.containsKey(key))
			return params.get(key);
		else
			return config.getInitParameter(key);
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getInitParameterNames() {
		return config.getInitParameterNames();
	}

	public ServletContext getServletContext() {
		return config.getServletContext();
	}

	public String getServletName() {
		return config.getServletName();
	}
}