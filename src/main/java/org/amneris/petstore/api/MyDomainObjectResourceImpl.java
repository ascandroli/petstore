package org.amneris.petstore.api;

import org.amneris.petstore.entities.MyDomainObject;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

public class MyDomainObjectResourceImpl implements MyDomainObjectResource
{

	private EntityManager entityManager;

	public MyDomainObjectResourceImpl(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	@Override
	public List<MyDomainObject> getAllDomains()
	{
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MyDomainObject> cq = cb.createQuery(MyDomainObject.class);
		Root<MyDomainObject> root = cq.from(MyDomainObject.class);
		cq.select(root);
		TypedQuery<MyDomainObject> q = entityManager.createQuery(cq);
		return q.getResultList();
	}

	@Override
	public Response post(MyDomainObject domainObject)
	{
		entityManager.persist(domainObject);
		return Response.status(Response.Status.CREATED).entity(domainObject).build();
	}

	@Override
	public MyDomainObject getDomainObject(Long id)
	{
		MyDomainObject domainObject = entityManager.find(MyDomainObject.class, id);
		if (domainObject == null)
		{
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return domainObject;
	}

}
