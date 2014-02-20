package org.amneris.petstore.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

public class Layout
{
	@Inject
	private ComponentResources resources;

	/**
	 * The page title, for the <title> element
	 */
	@Property
	@Parameter(required = true)
	private String title;

	@Property
	@Inject
	@Symbol(SymbolConstants.APPLICATION_VERSION)
	private String appVersion;


	public String getClassForPageName(String pageName) {
		return resources.getPageName().equalsIgnoreCase(pageName) ? "active" : null;
	}

}
