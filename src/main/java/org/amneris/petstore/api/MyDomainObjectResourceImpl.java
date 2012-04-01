package org.amneris.petstore.api;

import org.amneris.petstore.entities.MyDomainObject;
import org.tynamo.services.PersistenceService;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

public class MyDomainObjectResourceImpl implements MyDomainObjectResource
{

	private PersistenceService persistenceService;

	public MyDomainObjectResourceImpl(PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	@Override
	public List<MyDomainObject> getAllDomains()
	{
		return (persistenceService.getInstances(MyDomainObject.class));
	}

	@Override
	public Response post(MyDomainObject domainObject)
	{
		persistenceService.save(domainObject);
		return Response.ok().build();
	}

	@Override
	public MyDomainObject getDomainObject(Long id)
	{
		MyDomainObject domainObject = persistenceService.getInstance(MyDomainObject.class, id);
		if (domainObject == null)
		{
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return domainObject;
	}

}
