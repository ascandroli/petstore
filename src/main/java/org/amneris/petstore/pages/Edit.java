package org.amneris.petstore.pages;


import org.apache.tapestry5.Link;
import org.tynamo.hibernate.pages.HibernateEditPage;
import org.tynamo.util.DisplayNameUtils;
import org.tynamo.util.Utils;


public class Edit extends HibernateEditPage
{
	public String getTitle()
	{
		return getMessages().format(Utils.EDIT_MESSAGE, DisplayNameUtils.getDisplayName(getClassDescriptor(), getMessages()));
	}


	public Link back()
	{
		return getPageRenderLinkSource().createPageRenderLinkWithContext(Show.class, getClassDescriptor().getType(), getBean());
	}
}
