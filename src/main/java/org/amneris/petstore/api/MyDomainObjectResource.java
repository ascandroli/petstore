package org.amneris.petstore.api;

import org.amneris.petstore.entities.MyDomainObject;
import org.apache.tapestry5.jpa.annotations.CommitAfter;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/mydomainobject")
public interface MyDomainObjectResource
{
	@GET
	@Produces("application/json")
	List<MyDomainObject> getAllDomains();

	@POST
	@Consumes("application/json")
	@CommitAfter
	Response post(MyDomainObject domainObject);

	@GET
	@Path("{id}")
	@Produces("application/json")
	MyDomainObject getDomainObject(@PathParam("id") Long id);
}
