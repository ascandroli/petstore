package org.amneris.petstore.pages;


import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.jpa.annotations.CommitAfter;
import org.apache.tapestry5.services.ContextValueEncoder;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.tynamo.util.TynamoMessages;
import org.tynamo.routing.annotations.At;
import org.tynamo.services.PersistenceService;
import org.tynamo.util.Utils;

/**
 * Page for editing and updating objects.
 */
@At("/{0}/{1}/edit")
public class Edit
{
	@Inject
	private ContextValueEncoder contextValueEncoder;

	@Inject
	private Messages messages;

	@Inject
	private PersistenceService persitenceService;

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

	@Log
	@CommitAfter
	@OnEvent(EventConstants.SUCCESS)
	Link success()
	{
		persitenceService.save(bean);
		return back();
	}

	@OnEvent("cancel")
	Link back()
	{
		return pageRenderLinkSource.createPageRenderLinkWithContext(Show.class, beanType, bean);
	}

	public String getListAllLinkMessage()
	{
		return TynamoMessages.listAll(messages, beanType);
	}

	public String getTitle()
	{
		return TynamoMessages.edit(messages, beanType);
	}

}
