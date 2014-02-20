package org.amneris.petstore.pages.collections;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.ContextValueEncoder;
import org.tynamo.descriptor.CollectionDescriptor;
import org.tynamo.descriptor.TynamoPropertyDescriptor;
import org.tynamo.routing.annotations.At;
import org.tynamo.services.DescriptorService;
import org.tynamo.util.TynamoMessages;
import org.tynamo.util.Utils;

@At(value = "/{0}/{1}/{2}", order = "after:Edit")
public class ListC
{

	@Inject
	private Messages messages;

	@Inject
	private DescriptorService descriptorService;

	@Inject
	private ContextValueEncoder contextValueEncoder;

	@Inject
	private PropertyAccess adapter;

	@Property(write = false)
	private Class beanType;

	@Property
	private Object bean;

	@Property(write = false)
	private Object parentBean;

	@Property
	private String propertyName;

	@Component
	private Grid grid;

	@OnEvent(EventConstants.ACTIVATE)
	Object activate(Class clazz, String parentId, String property)
	{

		if (clazz != null)
		{

			TynamoPropertyDescriptor propertyDescriptor = descriptorService.getClassDescriptor(clazz).getPropertyDescriptor(property);

			if (propertyDescriptor != null)
			{
				CollectionDescriptor collectionDescriptor = ((CollectionDescriptor) propertyDescriptor);
				this.beanType = collectionDescriptor.getElementType();
				this.parentBean = contextValueEncoder.toValue(clazz, parentId);
				this.propertyName = property;
				if (parentBean != null) return null; // I know this is counterintuitive
			}

		}
		return Utils.new404(messages);
	}

	@OnEvent(EventConstants.PASSIVATE)
	Object[] passivate()
	{
		return new Object[]{parentBean.getClass(), parentBean, propertyName};
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
	public Object getSource()
	{
		return adapter.get(parentBean, propertyName);
	}

	public Object[] getShowPageContext()
	{
		return new Object[]{beanType, bean};
	}

	public Object[] getParentShowPageContext()
	{
		return new Object[]{parentBean.getClass(), parentBean};
	}


	public Object[] getAddElementPageContext()
	{
		return new Object[]{parentBean.getClass(), parentBean, propertyName};
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
