package org.amneris.petstore.pages;


import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ContextValueEncoder;
import org.hibernate.validator.InvalidStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tynamo.builder.BuilderDirector;
import org.tynamo.descriptor.CollectionDescriptor;
import org.tynamo.descriptor.TynamoClassDescriptor;
import org.tynamo.descriptor.TynamoPropertyDescriptor;
import org.tynamo.hibernate.pages.HibernateEditPage;
import org.tynamo.util.DisplayNameUtils;
import org.tynamo.util.Utils;

/**
 * Add Composition Page
 */

public class AddC extends HibernateEditPage
{

	@Inject
	private Logger logger;

	@Inject
	private BuilderDirector builderDirector;

	@Inject
	private ContextValueEncoder contextValueEncoder;
	
	@Property(write = false)
	private CollectionDescriptor collectionDescriptor;

	@Property(write = false)
	private Object parentBean;

	final void onActivate(Class clazz, String parentId, String property) throws Exception
	{
		TynamoPropertyDescriptor propertyDescriptor =
				getDescriptorService().getClassDescriptor(clazz).getPropertyDescriptor(property);
		collectionDescriptor = ((CollectionDescriptor) propertyDescriptor);

		TynamoClassDescriptor classDescriptor =
				getDescriptorService().getClassDescriptor(collectionDescriptor.getElementType());

		Object bean = builderDirector.createNewInstance(classDescriptor.getType());

		BeanModel beanModel = createBeanModel(classDescriptor.getType());

		activate(bean, classDescriptor, beanModel);
		parentBean = contextValueEncoder.toValue(clazz, parentId);
	}

	@Log
	@Override
	protected Object onSuccess()
	{
		try
		{
			Utils.executeOgnlExpression(collectionDescriptor.getAddExpression(), getBean(), parentBean);
			getPersitenceService().save(parentBean);
			return back();

		} catch (InvalidStateException ise)
		{
//			hibernateValidationDelegate.record(getClassDescriptor(), ise, getForm().getDefaultTracker(), getMessages());
		} catch (Exception e)
		{
//			missing ExceptionUtils (Lang 2.3 API)
//			form.recordError(ExceptionUtil.getRootCause(e));
			getForm().recordError(e.getMessage());
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		return null;
	}


	/**
	 * This tells Tapestry to put type & id into the URL, making it bookmarkable.
	 *
	 * @return
	 */
	protected Object[] onPassivate()
	{
		return new Object[]{collectionDescriptor.getBeanType(), parentBean, collectionDescriptor.getName()};
	}

	protected void cleanupRender()
	{
		super.cleanupRender();
		parentBean = null;
		collectionDescriptor = null;
	}

	public String getTitle()
	{
		return getMessages()
				.format(Utils.EDIT_MESSAGE, DisplayNameUtils.getDisplayName(getClassDescriptor(), getMessages()));
	}


	public Link back()
	{
		return getPageRenderLinkSource().createPageRenderLinkWithContext(Edit.class, collectionDescriptor.getBeanType(), parentBean);
	}

	@Override
	protected String getSuccessMessageKey()
	{
		return Utils.ADDED_MESSAGE;
	}

}