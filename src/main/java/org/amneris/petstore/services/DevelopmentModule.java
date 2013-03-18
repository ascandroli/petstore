package org.amneris.petstore.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.jpa.JpaSymbols;

/**
 * This module is automatically included as part of the Tapestry IoC Registry if <em>tapestry.execution-mode</em>
 * includes <code>development</code>.
 */
public class DevelopmentModule
{
	public static void contributeApplicationDefaults(MappedConfiguration<String, Object> configuration)
	{
		// The factory default is true but during the early stages of an application
		// overriding to false is a good idea. In addition, this is often overridden
		// on the command line as -Dtapestry.production-mode=false
		configuration.add(SymbolConstants.PRODUCTION_MODE, false);
		configuration.add(SymbolConstants.COMPRESS_WHITESPACE, false);

		configuration.add(JpaSymbols.EARLY_START_UP, true);
	}
}
