package org.amneris.petstore.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Context;
import org.apache.tapestry5.services.Environment;


@IncludeStylesheet({"context:themes/tapestryskin/theme.css"})
public class Layout
{

	@Inject
	private Environment environment;

	@Inject
	private Context context;

	@Inject
	private RenderSupport renderSupport;

	@Property
	@Parameter(required = true)
	private String title;

	@Property(write = false)
	@Parameter(value = "block:subMenuBlock", defaultPrefix = BindingConstants.LITERAL)
	private Block subMenuBlock;

	@Property(write = false)
	@Parameter(value = "block:navBlock", defaultPrefix = BindingConstants.LITERAL)
	private Block navBlock;

	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.MESSAGE)
	private String heading;

	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.MESSAGE)
	private String menu;

	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
	private String bodyId;

}
