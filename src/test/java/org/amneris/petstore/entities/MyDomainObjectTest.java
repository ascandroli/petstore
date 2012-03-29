package org.amneris.petstore.entities;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.Serializable;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Class for MyDomainObject tests.
 * <p/>
 * This represents best practice for Hibernate integration tests.
 * <p/>
 * It executes each test method in its own transaction, which is automatically
 * rolled back by default.
 * <p/>
 * related info:
 * http://www.theserverside.com/tt/articles/article.tss?l=PersistentDomain
 */
public class MyDomainObjectTest {

	private Session session;
	private Transaction transaction;
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void beforeAnything() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.test.cfg.xml");
		configuration.addAnnotatedClass(MyDomainObject.class);
		sessionFactory = configuration.buildSessionFactory();
	}

	@BeforeMethod
	public void beforeEachTest() {
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
	}

	@AfterMethod
	public void afterEachTest() {
		if (transaction != null)
			if (transaction.isActive())
				transaction.rollback();
	}

	@Test
	public void simple_save() {
		MyDomainObject myDomainObject = new MyDomainObject();
		myDomainObject.setName("some name");

		session.save(myDomainObject);

		Assert.assertEquals(myDomainObject.getName(), "some name");
		assertNotNull(myDomainObject.getId());
	}

	@Test
	public void save_object_without_name_should_fail() {
		MyDomainObject myDomainObject = new MyDomainObject();
		try {
			session.save(myDomainObject);
			fail();
		} catch (ConstraintViolationException e) {
			assertTrue(e.getConstraintViolations().size() == 1);
			ConstraintViolation c = (ConstraintViolation) e
					.getConstraintViolations().toArray()[0];
			assertEquals("name can't be null", c.getMessage());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void retrieve() {
		MyDomainObject myDomainObject = new MyDomainObject();
		myDomainObject.setName("some name");

		session.save(myDomainObject);

		assertEquals(myDomainObject.getName(), "some name");
		assertNotNull(myDomainObject.getId());

		MyDomainObject myReturnedDomainObject;
		myReturnedDomainObject = (MyDomainObject) session.get(
				MyDomainObject.class, myDomainObject.getId());

		assertEquals(myDomainObject, myReturnedDomainObject);
		assertEquals(myDomainObject.getName(), myReturnedDomainObject.getName());

	}

	@Test
	public void update() {
		MyDomainObject myDomainObject = new MyDomainObject();
		myDomainObject.setName("some name");

		session.save(myDomainObject);

		assertEquals(myDomainObject.getName(), "some name");
		assertNotNull(myDomainObject.getId());

		MyDomainObject myReturnedDomainObject;

		myReturnedDomainObject = (MyDomainObject) session.get(
				MyDomainObject.class, myDomainObject.getId());

		assertEquals(myDomainObject, myReturnedDomainObject);
		assertEquals(myDomainObject.getName(), myReturnedDomainObject.getName());

		myReturnedDomainObject.setName("some other name");
		Serializable id = myReturnedDomainObject.getId();

		session.save(myReturnedDomainObject);

		assertEquals(id, myReturnedDomainObject.getId());
		assertEquals("some other name", myReturnedDomainObject.getName());
	}

	@Test
	public void delete() {
		MyDomainObject myDomainObject = new MyDomainObject();
		myDomainObject.setName("some name");

		session.save(myDomainObject);

		assertEquals(myDomainObject.getName(), "some name");
		assertNotNull(myDomainObject.getId());

		Serializable id = myDomainObject.getId();

		session.delete(myDomainObject);
		MyDomainObject myReturnedDomainObject = null;
		myReturnedDomainObject = (MyDomainObject) session.get(
				MyDomainObject.class, id);

		/*
		 * try { } catch (HibernateObjectRetrievalFailureException e) { }
		 */

		assertNull(myReturnedDomainObject);
	}

	@Test
	public void get_all_instances() {
		MyDomainObject firstDomainObject = new MyDomainObject();
		firstDomainObject.setName("this is the first one");

		session.save(firstDomainObject);

		MyDomainObject secondDomainObject = new MyDomainObject();
		secondDomainObject.setName("this is the second object");

		session.save(secondDomainObject);

		List<MyDomainObject> objectList = (List<MyDomainObject>) session
				.createCriteria(MyDomainObject.class).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstDomainObject);
		assertTrue(i >= 0);

		assertFalse(objectList.isEmpty());
		int j = objectList.indexOf(secondDomainObject);
		assertTrue(j >= 0);

		MyDomainObject myReturnedDomainObject;

		myReturnedDomainObject = objectList.get(i);

		assertEquals(firstDomainObject, myReturnedDomainObject);
		assertEquals(firstDomainObject.getName(),
				myReturnedDomainObject.getName());
	}

	@Test
	public void search_by_detached_criteria() {
		MyDomainObject firstDomainObject = new MyDomainObject();
		firstDomainObject.setName("this is the first one");

		session.save(firstDomainObject);

		MyDomainObject secondDomainObject = new MyDomainObject();
		secondDomainObject.setName("this is the second object");

		session.save(secondDomainObject);

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MyDomainObject.class);
		criteria.add(Restrictions.like("name", "first", MatchMode.ANYWHERE));

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<MyDomainObject> objectList = (List<MyDomainObject>) criteria
				.getExecutableCriteria(session).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstDomainObject);
		assertTrue(i >= 0);

		int j = objectList.indexOf(secondDomainObject);
		assertTrue(j == -1);

		MyDomainObject myReturnedDomainObject;

		myReturnedDomainObject = objectList.get(i);

		assertEquals(firstDomainObject, myReturnedDomainObject);
		assertEquals(firstDomainObject.getName(),
				myReturnedDomainObject.getName());
	}
}