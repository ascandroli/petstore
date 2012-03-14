package org.amneris.petstore.pages;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.ExceptionReporter;
import org.apache.tapestry5.services.FormSupport;
import org.slf4j.Logger;

public class ExceptionReport implements ExceptionReporter
{
	@SuppressWarnings("unused")
	@Inject
	@Symbol(SymbolConstants.PRODUCTION_MODE)
	@Property(write = false)
	private boolean productionMode;

	@SuppressWarnings("unused")
	@Property(write = false)
	private Throwable rootException;

	@Inject
	private Logger logger;

	@Inject
	private Environment environment;

	@SetupRender
	void checkForFormSupport()
	{
		if (environment.peek(FormSupport.class) != null)
		{
			environment.pop(FormSupport.class);
		}
	}

	public void reportException(Throwable exception)
	{
		rootException = exception;
		logger.error(exception.getMessage(), exception);
	}
}
