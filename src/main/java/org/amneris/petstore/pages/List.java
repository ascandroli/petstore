package org.amneris.petstore.pages;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.corelib.components.Grid;
import org.tynamo.hibernate.pages.HibernateListPage;

public class List extends HibernateListPage
{
	@Component
	private Grid grid;

	/**
	 * This is where you can perform any one-time per-render setup for your component. This is a good place to read
	 * component parameters and use them to set temporary instance variables.
	 * More info: http://tapestry.apache.org/tapestry5.1/guide/rendering.html
	 * {@see org.apache.tapestry5.annotations.SetupRender}
	 *
	 */
	void setupRender()
	{
		grid.reset();
	}
}
