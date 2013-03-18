package org.amneris.petstore.pages.list;

import org.amneris.petstore.entities.MyDomainObject;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.jpa.JpaGridDataSource;
import org.tynamo.routing.annotations.At;
import org.tynamo.util.TynamoMessages;

import javax.persistence.EntityManager;

@At(value = "/mydomainobject", order = "before:list")
public class MyDomainObjectList
{

	@Inject
	private EntityManager em;

	@Inject
	private Messages messages;

	@Property(write = false)
	private Class beanType = MyDomainObject.class;

	@Property
	private MyDomainObject bean;

	/**
	 * The source of data for the Grid to display.
	 * This will usually be a List or array but can also be an explicit GridDataSource
	 */
	public GridDataSource getSource()
	{
		return new JpaGridDataSource(em, beanType);
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
