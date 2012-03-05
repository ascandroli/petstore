package org.amneris.petstore.pages;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.tynamo.hibernate.pages.HibernateModelPage;
import org.tynamo.util.DisplayNameUtils;
import org.tynamo.util.Utils;


public class Show extends HibernateModelPage
{

	public Object[] getEditPageContext()
	{
		return new Object[]{getClassDescriptor().getType(), getBean()};
	}

	public Link back()
	{
		return getPageRenderLinkSource().createPageRenderLinkWithContext(List.class, getClassDescriptor().getType());
	}

	public String getEditLinkMessage()
	{
		return getMessages().format(Utils.EDIT_MESSAGE, DisplayNameUtils.getDisplayName(getClassDescriptor(), getMessages()));
	}

	public String getTitle()
	{
		return getMessages().format(Utils.SHOW_MESSAGE, getBean().toString(), getMessages());
	}

	public Link onActionFromDelete()
	{
		getPersitenceService().remove(getBean());
		return back();
	}

	@Override
	public BeanModel createBeanModel(Class clazz)
	{
		return getBeanModelSource().createDisplayModel(clazz, getMessages());
	}
}
