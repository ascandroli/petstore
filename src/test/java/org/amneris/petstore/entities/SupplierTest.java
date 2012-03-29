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
 * Class for Supplier tests.
 * <p/>
 * This represents best practice for Hibernate integration tests.
 * <p/>
 * It executes each test method in its own transaction, which is automatically
 * rolled back by default.
 * <p/>
 * related info:
 * http://www.theserverside.com/tt/articles/article.tss?l=PersistentDomain
 */
public class SupplierTest {

	private Session session;
	private Transaction transaction;
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void beforeAnything() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.test.cfg.xml");
		configuration.addAnnotatedClass(Supplier.class);
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
		Supplier supplier = new Supplier();
		supplier.setName("some name");

		session.save(supplier);

		Assert.assertEquals(supplier.getName(), "some name");
		assertNotNull(supplier.getId());
	}

	@Test
	public void save_object_without_name_should_fail() {
		Supplier supplier = new Supplier();
		try {
			session.save(supplier);
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
		Supplier supplier = new Supplier();
		supplier.setName("some name");

		session.save(supplier);

		assertEquals(supplier.getName(), "some name");
		assertNotNull(supplier.getId());

		Supplier returnedSupplier;
		returnedSupplier = (Supplier) session.get(Supplier.class,
				supplier.getId());

		assertEquals(supplier, returnedSupplier);
		assertEquals(supplier.getName(), returnedSupplier.getName());

	}

	@Test
	public void update() {
		Supplier supplier = new Supplier();
		supplier.setName("some name");

		session.save(supplier);

		assertEquals(supplier.getName(), "some name");
		assertNotNull(supplier.getId());

		Supplier returnedSupplier;
		returnedSupplier = (Supplier) session.get(Supplier.class,
				supplier.getId());

		assertEquals(supplier, returnedSupplier);
		assertEquals(supplier.getName(), returnedSupplier.getName());

		returnedSupplier.setName("some other name");
		Serializable id = returnedSupplier.getId();

		session.save(returnedSupplier);

		assertEquals(id, returnedSupplier.getId());
		assertEquals("some other name", returnedSupplier.getName());
	}

	@Test
	public void delete() {
		Supplier supplier = new Supplier();
		supplier.setName("some name");

		session.save(supplier);

		assertEquals(supplier.getName(), "some name");
		assertNotNull(supplier.getId());

		Serializable id = supplier.getId();

		session.delete(supplier);

		Supplier returnedSupplier = null;
		returnedSupplier = (Supplier) session.get(Supplier.class, id);

		/*
		 * try { } catch (HibernateObjectRetrievalFailureException e) { }
		 */

		assertNull(returnedSupplier);
	}

	@Test
	public void get_all_instances() {
		Supplier firstSupplier = new Supplier();
		firstSupplier.setName("this is the first one");

		session.save(firstSupplier);

		Supplier secondSupplier = new Supplier();
		secondSupplier.setName("this is the second object");

		session.save(secondSupplier);

		List<Supplier> objectList = (List<Supplier>) session.createCriteria(
				Supplier.class).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstSupplier);
		assertTrue(i >= 0);

		assertFalse(objectList.isEmpty());
		int j = objectList.indexOf(secondSupplier);
		assertTrue(j >= 0);

		Supplier supplier;
		supplier = objectList.get(i);

		assertEquals(firstSupplier, supplier);
		assertEquals(firstSupplier.getName(), supplier.getName());
	}

	@Test
	public void search_by_detached_criteria() {
		Supplier firstSupplier = new Supplier();
		firstSupplier.setName("this is the first one");

		session.save(firstSupplier);

		Supplier secondSupplier = new Supplier();
		secondSupplier.setName("this is the second object");

		session.save(secondSupplier);

		DetachedCriteria criteria = DetachedCriteria.forClass(Supplier.class);
		criteria.add(Restrictions.like("name", "first", MatchMode.ANYWHERE));

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Supplier> objectList = (List<Supplier>) criteria
				.getExecutableCriteria(session).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstSupplier);
		assertTrue(i >= 0);

		int j = objectList.indexOf(secondSupplier);
		assertTrue(j == -1);

		Supplier supplier;

		supplier = objectList.get(i);

		assertEquals(firstSupplier, supplier);
		assertEquals(firstSupplier.getName(), supplier.getName());
	}
}