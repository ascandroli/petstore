package org.amneris.petstore.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Context;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.ComponentResources;
import org.amneris.petstore.entities.MyDomainObject;


@Import(stylesheet = {"context:themes/rm/theme.css"})
public class Layout
{

	@Inject
	private Environment environment;

	@Inject
	private Context context;

	@Property
	@Parameter(required = true)
	private String title;

	@Property(write = false)
	@Parameter
	private Block sidebar;

	@Property(write = false)
	@Parameter
	private Block head;

	@Inject
	private ComponentResources resources;

	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.MESSAGE)
	private String heading;

	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.MESSAGE)
	private String menu;

	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
	private String bodyId;

	@Property(write = false)
	@Parameter
	private Block tabs;

	public boolean isSidebarBound()
	{
		return resources.isBound("sidebar");
	}

	public boolean isHeadBound()
	{
		return resources.isBound("head");
	}

	public String getMainCssClass()
	{
		return isSidebarBound() ? "" : "nosidebar";
	}

	public boolean isTabsBound()
	{
		return resources.isBound("tabs");
	}

	public Class getMyDomainObject()
	{
		return MyDomainObject.class;
	}

}
