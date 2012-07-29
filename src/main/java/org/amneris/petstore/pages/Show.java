package org.amneris.petstore.pages;


import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.jpa.annotations.CommitAfter;
import org.apache.tapestry5.services.ContextValueEncoder;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.tynamo.routing.annotations.At;
import org.tynamo.services.DescriptorService;
import org.tynamo.services.PersistenceService;
import org.tynamo.util.TynamoMessages;
import org.tynamo.util.Utils;

/**
 * Page for displayig the properties of an object.
 */
@At("/{0}/{1}")
public class Show
{

	@Inject
	private ContextValueEncoder contextValueEncoder;

	@Inject
	private Messages messages;

	@Inject
	private PersistenceService persitenceService;

	@Inject
	private DescriptorService descriptorService;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	@Property(write = false)
	private Class beanType;

	@Property
	private Object bean;

	@OnEvent(EventConstants.ACTIVATE)
	Object activate(Class clazz, String id)
	{

		if (clazz == null) return Utils.new404(messages);

		this.bean = contextValueEncoder.toValue(clazz, id);
		this.beanType = clazz;

		if (bean == null) return Utils.new404(messages);

		return null;
	}

	@CleanupRender
	void cleanup()
	{
		bean = null;
		beanType = null;
	}

	/**
	 * This tells Tapestry to put type & id into the URL, making it bookmarkable.
	 */
	@OnEvent(EventConstants.PASSIVATE)
	Object[] passivate()
	{
		return new Object[]{beanType, bean};
	}

	public Object[] getEditPageContext()
	{
		return new Object[]{beanType, bean};
	}

	public String getEditLinkMessage()
	{
		return TynamoMessages.edit(messages, beanType);
	}

	public String getTitle()
	{
		return TynamoMessages.show(messages, bean.toString());
	}

	@CommitAfter
	@OnEvent("delete")
	Link delete()
	{
		persitenceService.remove(bean);
		return pageRenderLinkSource.createPageRenderLinkWithContext(List.class, beanType);
	}

	public boolean isDeleteAllowed()
	{
		return descriptorService.getClassDescriptor(beanType).isAllowRemove();
	}

	public String getListAllLinkMessage()
	{
		return TynamoMessages.listAll(messages, beanType);
	}

}
