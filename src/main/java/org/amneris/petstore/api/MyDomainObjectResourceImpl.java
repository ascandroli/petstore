package org.amneris.petstore.api;

import org.amneris.petstore.entities.MyDomainObject;
import org.hibernate.Session;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

public class MyDomainObjectResourceImpl implements MyDomainObjectResource
{

	private Session session;

	public MyDomainObjectResourceImpl(Session session)
	{
		this.session = session;
	}

	@Override
	public List<MyDomainObject> getAllDomains()
	{
		return session.createCriteria(MyDomainObject.class).list();
	}

	@Override
	public Response post(MyDomainObject domainObject)
	{
		session.save(domainObject);
		return Response.status(Response.Status.CREATED).entity(domainObject).build();
	}

	@Override
	public MyDomainObject getDomainObject(Long id)
	{
		MyDomainObject domainObject = (MyDomainObject) session.get(MyDomainObject.class, id);
		if (domainObject == null)
		{
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return domainObject;
	}

}
