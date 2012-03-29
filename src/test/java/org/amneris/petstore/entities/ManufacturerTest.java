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
 * Class for Manufacturer tests.
 * <p/>
 * This represents best practice for Hibernate integration tests.
 * <p/>
 * It executes each test method in its own transaction, which is automatically rolled back by default.
 * <p/>
 * related info: http://www.theserverside.com/tt/articles/article.tss?l=PersistentDomain
 */
public class ManufacturerTest {

	private Session session;
	private Transaction transaction;
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void beforeAnything() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.test.cfg.xml");
		configuration.addAnnotatedClass(Manufacturer.class);
		sessionFactory = configuration.buildSessionFactory();
	}

	@BeforeMethod
	public void beforeEachTest() {
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
	}

	@AfterMethod
	public void afterEachTest() {
		if (transaction != null) if (transaction.isActive()) transaction.rollback();
	}

	@Test
	public void simple_save() {
		Manufacturer manufacturer = new Manufacturer();
		manufacturer.setName("some name");

		session.save(manufacturer);

		Assert.assertEquals(manufacturer.getName(), "some name");
		assertNotNull(manufacturer.getId());
	}

	@Test
	public void save_object_without_name_should_fail() {
		Manufacturer manufacturer = new Manufacturer();
		try {
			session.save(manufacturer);
			fail();
		} catch (ConstraintViolationException e) {
			assertTrue(e.getConstraintViolations().size() == 1);
			ConstraintViolation c = (ConstraintViolation) e.getConstraintViolations().toArray()[0];
			assertEquals("name can't be null", c.getMessage());
		} catch (Exception e) {
			fail();
		}
	}


	@Test
	public void retrieve() {
		Manufacturer manufacturer = new Manufacturer();
		manufacturer.setName("some name");

		session.save(manufacturer);

		assertEquals(manufacturer.getName(), "some name");
		assertNotNull(manufacturer.getId());

		Manufacturer returnedManufacturer;
		returnedManufacturer = (Manufacturer) session.get(Manufacturer.class, manufacturer.getId());

		assertEquals(manufacturer, returnedManufacturer);
		assertEquals(manufacturer.getName(), returnedManufacturer.getName());

	}

	@Test
	public void update() {
		Manufacturer manufacturer = new Manufacturer();
		manufacturer.setName("some name");

		session.save(manufacturer);

		assertEquals(manufacturer.getName(), "some name");
		assertNotNull(manufacturer.getId());

		Manufacturer returnedManufacturer;
		returnedManufacturer = (Manufacturer) session.get(Manufacturer.class, manufacturer.getId());

		assertEquals(manufacturer, returnedManufacturer);
		assertEquals(manufacturer.getName(), returnedManufacturer.getName());

		returnedManufacturer.setName("some other name");
		Serializable id = returnedManufacturer.getId();

		session.save(returnedManufacturer);

		assertEquals(id, returnedManufacturer.getId());
		assertEquals("some other name", returnedManufacturer.getName());
	}

	@Test
	public void delete() {
		Manufacturer manufacturer = new Manufacturer();
		manufacturer.setName("some name");

		session.save(manufacturer);

		assertEquals(manufacturer.getName(), "some name");
		assertNotNull(manufacturer.getId());

		Serializable id = manufacturer.getId();

		session.delete(manufacturer);
		
		Manufacturer returnedManufacturer = null;
		returnedManufacturer = (Manufacturer) session.get(Manufacturer.class, id);

/*
		try {
		} catch (HibernateObjectRetrievalFailureException e) {
		}
*/

		assertNull(returnedManufacturer);
	}

	@Test
	public void get_all_instances() {
		Manufacturer firstManufacturer = new Manufacturer();
		firstManufacturer.setName("this is the first one");

		session.save(firstManufacturer);

		Manufacturer secondManufacturer = new Manufacturer();
		secondManufacturer.setName("this is the second object");

		session.save(secondManufacturer);

		List<Manufacturer> objectList = (List<Manufacturer>) session.createCriteria(Manufacturer.class).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstManufacturer);
		assertTrue(i >= 0);

		assertFalse(objectList.isEmpty());
		int j = objectList.indexOf(secondManufacturer);
		assertTrue(j >= 0);

		Manufacturer manufacturer;
		manufacturer = objectList.get(i);

		assertEquals(firstManufacturer, manufacturer);
		assertEquals(firstManufacturer.getName(), manufacturer.getName());
	}

	@Test
	public void search_by_detached_criteria() {
		Manufacturer firstManufacturer = new Manufacturer();
		firstManufacturer.setName("this is the first one");

		session.save(firstManufacturer);

		Manufacturer secondManufacturer = new Manufacturer();
		secondManufacturer.setName("this is the second object");

		session.save(secondManufacturer);

		DetachedCriteria criteria = DetachedCriteria.forClass(Manufacturer.class);
		criteria.add(Restrictions.like("name", "first", MatchMode.ANYWHERE));

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Manufacturer> objectList = (List<Manufacturer>) criteria.getExecutableCriteria(session).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstManufacturer);
		assertTrue(i >= 0);

		int j = objectList.indexOf(secondManufacturer);
		assertTrue(j == -1);

		Manufacturer manufacturer;

		manufacturer = objectList.get(i);

		assertEquals(firstManufacturer, manufacturer);
		assertEquals(firstManufacturer.getName(), manufacturer.getName());
	}
}