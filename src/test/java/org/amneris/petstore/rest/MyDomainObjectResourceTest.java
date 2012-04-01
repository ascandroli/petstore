package org.amneris.petstore.rest;

import org.amneris.petstore.api.MyDomainObjectResource;
import org.amneris.petstore.entities.MyDomainObject;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

import javax.ws.rs.core.Response;

public class MyDomainObjectResourceTest extends AbstractContainerTest
{

	private final static Logger logger = LoggerFactory.getLogger(MyDomainObjectResourceTest.class);

	@Test
	public void post() throws Exception
	{
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());

		MyDomainObjectResource resource = ProxyFactory.create(MyDomainObjectResource.class, BASEURI + "/rest");

		MyDomainObject dummy = new MyDomainObject();
		dummy.setName("dummy");

		ClientResponse response = (ClientResponse) resource.post(dummy);

		Assert.assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
		dummy = (MyDomainObject) response.getEntity(MyDomainObject.class);

		response.releaseConnection();

		MyDomainObject actual = resource.getDomainObject(dummy.getId());

		Assert.assertEquals(actual.getName(), dummy.getName());
	}
}
