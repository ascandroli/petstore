package org.amneris.petstore.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.jpa.EntityManagerSource;
import org.apache.tapestry5.jpa.JpaSymbols;
import org.apache.tapestry5.jpa.PersistenceUnitConfigurer;
import org.apache.tapestry5.jpa.TapestryPersistenceUnitInfo;

import javax.persistence.spi.PersistenceUnitTransactionType;

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

	@Contribute(EntityManagerSource.class)
	public static void configurePersistenceUnit(MappedConfiguration<String, PersistenceUnitConfigurer> cfg)
	{
		PersistenceUnitConfigurer configurer = new PersistenceUnitConfigurer()
		{
			@Override
			public void configure(TapestryPersistenceUnitInfo unitInfo)
			{
				unitInfo.transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
						.persistenceProviderClassName("org.eclipse.persistence.jpa.PersistenceProvider")
						.excludeUnlistedClasses(false).addProperty("javax.persistence.jdbc.user", "sa")
						.addProperty("javax.persistence.jdbc.password", "")
						.addProperty("javax.persistence.jdbc.driver", "org.h2.Driver")
						.addProperty("javax.persistence.jdbc.url", "jdbc:h2:pestore;AUTO_SERVER=TRUE")
						.addProperty("eclipselink.ddl-generation", "create-or-extend-tables")
						.addProperty("eclipselink.logging.level", "FINE");
			}
		};
		cfg.add("petstore", configurer);
	}

}
