package org.amneris.petstore.pages;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.tynamo.components.ModelSearch;
import org.tynamo.routing.annotations.At;
import org.tynamo.services.PersistenceService;
import org.tynamo.util.TynamoMessages;
import org.tynamo.util.Utils;

/**
 * Page for listing elements of a given type.
 *
 * @note:
 * When extending this page for customization purposes, it's better to copy & paste code than trying to use inheritance.
 *
 */
@At("/{0}")
public class List
{

	@Inject
	private PersistenceService persistenceService;

	@Inject
	private Messages messages;

	@Property(write = false)
	private Class beanType;

	@Property
	private Object bean;

	@Inject
	@Property
	private Block resultcountBlock;

	@InjectComponent
	private Grid grid;

	@Component(parameters = "beanType=beanType")
	@Property(write = false)
	private ModelSearch modelSearch;

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

	void onActionFromResetSearchCriteria() {
		modelSearch.resetSearchCriteria();
	}

	@Inject
	private Request request;

	void onSearchTermsChanged() {
		String searchTerms = request.getParameter("param");
		if (searchTerms != null) modelSearch.setSearchTerms(searchTerms);
		// return request.isXHR() ? termZone.getBody() : null;
	}

	public int getBeanCount() {
		return persistenceService.count(beanType);
	}

}
