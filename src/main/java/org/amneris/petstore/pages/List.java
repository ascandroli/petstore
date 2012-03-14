package org.amneris.petstore.pages;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.TynamoGridDataSource;
import org.tynamo.util.TynamoMessages;
import org.tynamo.routing.annotations.At;
import org.tynamo.services.PersistenceService;
import org.tynamo.util.Utils;

/**
 * Page for listing elements of a given type.
 */
@At("/{0}")
public class List
{

	@Inject
	private PersistenceService persitenceService;

	@Inject
	private Messages messages;

	@Property(write = false)
	private Class beanType;

	@Property
	private Object bean;

	@Component
	private Grid grid;

	@OnEvent(EventConstants.ACTIVATE)
	Object onActivate(Class clazz)
	{
		if (clazz == null) return Utils.new404(messages);
		this.beanType = clazz;
		return null;
	}

	@OnEvent(EventConstants.PASSIVATE)
	Object[] passivate()
	{
		return new Object[]{beanType};
	}

	/**
	 * This is where you can perform any one-time per-render setup for your component. This is a good place to read
	 * component parameters and use them to set temporary instance variables.
	 * More info: http://tapestry.apache.org/tapestry5.1/guide/rendering.html
	 * {@see org.apache.tapestry5.annotations.SetupRender}
	 */
	@SetupRender
	void setupRender()
	{
		grid.reset();
	}

	/**
	 * The source of data for the Grid to display.
	 * This will usually be a List or array but can also be an explicit GridDataSource
	 */
	public GridDataSource getSource()
	{
		return new TynamoGridDataSource(persitenceService, beanType);
	}

	public Object[] getShowPageContext()
	{
		return new Object[]{beanType, bean};
	}

	public String getTitle()
	{
		return TynamoMessages.list(messages, beanType);
	}

	public String getNewLinkMessage()
	{
		return TynamoMessages.add(messages, beanType);
	}

}
