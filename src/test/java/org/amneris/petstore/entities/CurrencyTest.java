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
 * Class for Currency tests.
 * <p/>
 * This represents best practice for Hibernate integration tests.
 * <p/>
 * It executes each test method in its own transaction, which is automatically
 * rolled back by default.
 * <p/>
 * related info:
 * http://www.theserverside.com/tt/articles/article.tss?l=PersistentDomain
 */
public class CurrencyTest {

	private Session session;
	private Transaction transaction;
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void beforeAnything() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.test.cfg.xml");
		configuration.addAnnotatedClass(Currency.class);
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
		Currency currency = new Currency();
		currency.setName("some name");

		session.save(currency);

		Assert.assertEquals(currency.getName(), "some name");
		assertNotNull(currency.getId());
	}

	@Test
	public void save_object_without_name_should_fail() {
		Currency currency = new Currency();
		try {
			session.save(currency);
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
		Currency currency = new Currency();
		currency.setName("some name");

		session.save(currency);

		assertEquals(currency.getName(), "some name");
		assertNotNull(currency.getId());

		Currency returnedCurrency;
		returnedCurrency = (Currency) session.get(Currency.class,
				currency.getId());

		assertEquals(currency, returnedCurrency);
		assertEquals(currency.getName(), returnedCurrency.getName());

	}

	@Test
	public void update() {
		Currency currency = new Currency();
		currency.setName("some name");

		session.save(currency);

		assertEquals(currency.getName(), "some name");
		assertNotNull(currency.getId());

		Currency returnedCurrency;
		returnedCurrency = (Currency) session.get(Currency.class,
				currency.getId());

		assertEquals(currency, returnedCurrency);
		assertEquals(currency.getName(), returnedCurrency.getName());

		returnedCurrency.setName("some other name");
		Serializable id = returnedCurrency.getId();

		session.save(returnedCurrency);

		assertEquals(id, returnedCurrency.getId());
		assertEquals("some other name", returnedCurrency.getName());
	}

	@Test
	public void delete() {
		Currency currency = new Currency();
		currency.setName("some name");

		session.save(currency);

		assertEquals(currency.getName(), "some name");
		assertNotNull(currency.getId());

		Serializable id = currency.getId();

		session.delete(currency);

		Currency returnedCurrency = null;
		returnedCurrency = (Currency) session.get(Currency.class, id);

		/*
		 * try { } catch (HibernateObjectRetrievalFailureException e) { }
		 */

		assertNull(returnedCurrency);
	}

	@Test
	public void get_all_instances() {
		Currency firstCurrency = new Currency();
		firstCurrency.setName("this is the first one");

		session.save(firstCurrency);

		Currency secondCurrency = new Currency();
		secondCurrency.setName("this is the second object");

		session.save(secondCurrency);

		List<Currency> objectList = (List<Currency>) session.createCriteria(
				Currency.class).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstCurrency);
		assertTrue(i >= 0);

		assertFalse(objectList.isEmpty());
		int j = objectList.indexOf(secondCurrency);
		assertTrue(j >= 0);

		Currency currency;
		currency = objectList.get(i);

		assertEquals(firstCurrency, currency);
		assertEquals(firstCurrency.getName(), currency.getName());
	}

	@Test
	public void search_by_detached_criteria() {
		Currency firstCurrency = new Currency();
		firstCurrency.setName("this is the first one");

		session.save(firstCurrency);

		Currency secondCurrency = new Currency();
		secondCurrency.setName("this is the second object");

		session.save(secondCurrency);

		DetachedCriteria criteria = DetachedCriteria.forClass(Currency.class);
		criteria.add(Restrictions.like("name", "first", MatchMode.ANYWHERE));

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Currency> objectList = (List<Currency>) criteria
				.getExecutableCriteria(session).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstCurrency);
		assertTrue(i >= 0);

		int j = objectList.indexOf(secondCurrency);
		assertTrue(j == -1);

		Currency currency;

		currency = objectList.get(i);

		assertEquals(firstCurrency, currency);
		assertEquals(firstCurrency.getName(), currency.getName());
	}
}