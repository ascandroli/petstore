package org.amneris.petstore;

import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.Registry;
import org.eclipse.jetty.webapp.WebAppContext;
import org.tynamo.test.AbstractContainerTest;

public class PetstoreBaseIntegrationTest extends AbstractContainerTest
{
	private Registry registry;

	@Override
	public WebAppContext buildContext()
	{
		WebAppContext context = super.buildContext();
		registry = (Registry) context.getServletContext().getAttribute(TapestryFilter.REGISTRY_CONTEXT_NAME);
		return context;
	}

	/**
	 * Returns the Registry that was created for the application.
	 */
	public Registry getRegistry()
	{
		return registry;
	}

}
