package org.amneris.petstore.pages;


import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.jpa.annotations.CommitAfter;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.tynamo.builder.BuilderDirector;
import org.tynamo.util.TynamoMessages;
import org.tynamo.routing.annotations.At;
import org.tynamo.services.PersistenceService;
import org.tynamo.util.Utils;

/**
 * Page for creating and adding objects to the DB.
 */
@At("/{0}/new")
public class Add
{

	@Inject
	private BuilderDirector builderDirector;

	@Inject
	private Messages messages;

	@Inject
	private PersistenceService persitenceService;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	@Inject
	private AlertManager alertManager;

	@Property(write = false)
	private Class beanType;

	@Property
	private Object bean;

	private boolean continueAdding;

	@OnEvent(EventConstants.ACTIVATE)
	Object activate(Class clazz)
	{

		if (clazz == null) return Utils.new404(messages);

		this.bean = builderDirector.createNewInstance(clazz);
		this.beanType = clazz;

		return null;
	}

	@CleanupRender
	void cleanup()
	{
		bean = null;
		beanType = null;
	}

	@OnEvent(EventConstants.PASSIVATE)
	Object[] passivate()
	{
		return new Object[]{beanType};
	}

	@OnEvent(value = "return")
	void onSaveAndReturn() {
		this.continueAdding = false;
	}

	@OnEvent(value = "continue")
	void onSaveAndContinue() {
		this.continueAdding = true;
	}

	@Log
	@CommitAfter
	@OnEvent(EventConstants.SUCCESS)
	Link success()
	{
		persitenceService.save(bean);
		alertManager.info(messages.getFormatter(Utils.ADDED_MESSAGE).format(bean));
		return !continueAdding ? pageRenderLinkSource.createPageRenderLinkWithContext(Show.class, beanType, bean) : null;
	}

	@OnEvent("cancel")
	Link cancel()
	{
		return pageRenderLinkSource.createPageRenderLinkWithContext(List.class, beanType);
	}

	public String getTitle()
	{
		return TynamoMessages.add(messages, beanType);
	}

	public String getListAllLinkMessage()
	{
		return TynamoMessages.listAll(messages, beanType);
	}

}
