package org.amneris.petstore.rest;

import org.amneris.petstore.entities.MyDomainObject;
import org.tynamo.services.PersistenceService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/mydomainobject")
public class MyDomainObjectResource
{

	private PersistenceService persistenceService;

	public MyDomainObjectResource(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	@GET
	@Produces("application/json")
	public List<MyDomainObject> getAllDomains()
	{
		return (persistenceService.getInstances(MyDomainObject.class));
	}

	@POST
	@Consumes("application/json")
	public Response post(MyDomainObject domainObject)
	{
		persistenceService.save(domainObject);
		return Response.ok().build();
	}

	@GET
	@Path("{id}")
	@Produces("application/json")
	public MyDomainObject getDomainObject(@PathParam("id") Long id)
	{
		MyDomainObject domainObject = persistenceService.getInstance(MyDomainObject.class, id);
		if (domainObject == null)
		{
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return domainObject;
	}

}
