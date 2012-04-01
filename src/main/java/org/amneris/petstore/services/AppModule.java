package org.amneris.petstore.services;

import org.amneris.petstore.api.MyDomainObjectResource;
import org.apache.shiro.realm.Realm;
import org.apache.tapestry5.hibernate.HibernateSymbols;
import org.apache.tapestry5.hibernate.HibernateTransactionAdvisor;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.services.BeanBlockContribution;
import org.apache.tapestry5.services.BeanBlockSource;
import org.apache.tapestry5.services.DisplayBlockContribution;
import org.tynamo.PageType;
import org.tynamo.builder.Builder;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.services.SecurityFilterChainFactory;
import org.tynamo.security.services.impl.SecurityFilterChain;
import org.tynamo.shiro.extension.realm.text.ExtendedPropertiesRealm;

import java.io.IOException;
import java.util.Properties;

/**
 * This module is automatically included as part of the Tapestry IoC Registry, it's a good place to configure and extend
 * Tynamo, or to place your own service definitions.
 */
public class AppModule
{

	public static void bind(ServiceBinder binder)
	{
		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.
		binder.bind(MyDomainObjectResource.class);
	}

	public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration)
	{
		loadApplicationDefaultsFromProperties("/applicationdefaults.properties", configuration);

		// Tynamo's tapestry-security (Shiro) module configuration
		configuration.add(SecuritySymbols.LOGIN_URL, "/signin");
		configuration.add(SecuritySymbols.UNAUTHORIZED_URL, "/unauthorized");
		configuration.add(SecuritySymbols.SUCCESS_URL, "/home");

		configuration.add(HibernateSymbols.EARLY_START_UP, "false");
	}

	public static void contributeWebSecurityManager(Configuration<Realm> configuration) 
	{
		configuration.add(new ExtendedPropertiesRealm("classpath:shiro-users.properties"));
	}


	public static void contributeSecurityConfiguration(Configuration<SecurityFilterChain> configuration,
	                                                   SecurityFilterChainFactory factory)
	{
		configuration.add(factory.createChain("/signin").add(factory.anon()).build());
		configuration.add(factory.createChain("/").add(factory.roles(), "admin").build());
		configuration.add(factory.createChain("/edit/**").add(factory.perms(), "*:update").build());
		configuration.add(factory.createChain("/show/**").add(factory.perms(), "*:select").build());
		configuration.add(factory.createChain("/add/**").add(factory.perms(), "*:insert").build());
		configuration.add(factory.createChain("/list/**").add(factory.perms(), "*:select").build());
	}
	/**
	 * By default tapestry-hibernate will scan
	 * InternalConstants.TAPESTRY_APP_PACKAGE_PARAM + ".entities" (witch is equal to "org.amneris.petstore.petstore.entities")
	 * for annotated entity classes.
	 *
	 * Contributes the package "org.amneris.petstore.petstore.model" to the configuration, so that it will be
	 * scanned for annotated entity classes.
	 */
	public static void contributeHibernateEntityPackageManager(Configuration<String> configuration)
	{
//		If you want to scan other packages add them here:
//		configuration.add("org.amneris.petstore.petstore.model");
	}

	/**
	 * Contributes Builders to the BuilderDirector's builders map.
	 * Check GOF's <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder pattern</a>
	 */
	public static void contributeBuilderDirector(MappedConfiguration<Class, Builder> configuration)
	{
//		configuration.add(org.tynamo.examples.recipe.model.Recipe.class, new RecipeBuilder());
	}

/*
	@Startup
	public static void init(Logger logger, MigrationManager migrationManager)
	{
		logger.info("Starting up...");
//		migrationManager.migrate();
	}
*/

	/**
	 * Contribution to the BeanBlockSource service to tell the BeanEditForm
	 * component about the editors.
	 */
	@Contribute(BeanBlockSource.class)
	public static void addCustomBlocks(Configuration<BeanBlockContribution> configuration)
	{
		configuration.add(new DisplayBlockContribution("boolean", "blocks/DisplayBlocks", "check"));
	}

	/**
	 * Contributions to the RESTeasy main Application, insert all your RESTeasy singletons services here.
	 */
	@Contribute(javax.ws.rs.core.Application.class)
	public static void configureRestResources(Configuration<Object> singletons, MyDomainObjectResource myDomainObjectResource)
	{
		singletons.add(myDomainObjectResource);
	}

	@Match("*Resource")
	public static void adviseTransactions(HibernateTransactionAdvisor advisor, MethodAdviceReceiver receiver)
	{
		advisor.addTransactionCommitAdvice(receiver);
	}

	private static void loadApplicationDefaultsFromProperties(String properties, MappedConfiguration<String, String> contributions)
	{
		Properties prop = new Properties();

		try
		{
			prop.load(AppModule.class.getResource(properties).openStream());
		} catch (IOException ioe)
		{
			throw new RuntimeException("Unable to load " + properties, ioe);
		}

		for (Object key : prop.keySet())
		{
			String value = prop.getProperty(key.toString());
			contributions.add(key.toString(), value);
		}
	}

}
