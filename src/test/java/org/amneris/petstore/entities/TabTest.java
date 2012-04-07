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
 * Class for Tab tests.
 * <p/>
 * This represents best practice for Hibernate integration tests.
 * <p/>
 * It executes each test method in its own transaction, which is automatically
 * rolled back by default.
 * <p/>
 * related info:
 * http://www.theserverside.com/tt/articles/article.tss?l=PersistentDomain
 */
public class TabTest {

	private Session session;
	private Transaction transaction;
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void beforeAnything() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.test.cfg.xml");
		configuration.addAnnotatedClass(Tab.class);
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
		Tab tab = new Tab();
		tab.setName("some name");

		session.save(tab);

		Assert.assertEquals(tab.getName(), "some name");
		assertNotNull(tab.getId());
	}

	@Test
	public void save_object_without_name_should_fail() {
		Tab tab = new Tab();
		try {
			session.save(tab);
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
		Tab tab = new Tab();
		tab.setName("some name");

		session.save(tab);

		assertEquals(tab.getName(), "some name");
		assertNotNull(tab.getId());

		Tab returnedTab;
		returnedTab = (Tab) session.get(Tab.class,
				tab.getId());

		assertEquals(tab, returnedTab);
		assertEquals(tab.getName(), returnedTab.getName());

	}

	@Test
	public void update() {
		Tab tab = new Tab();
		tab.setName("some name");

		session.save(tab);

		assertEquals(tab.getName(), "some name");
		assertNotNull(tab.getId());

		Tab returnedTab;
		returnedTab = (Tab) session.get(Tab.class,
				tab.getId());

		assertEquals(tab, returnedTab);
		assertEquals(tab.getName(), returnedTab.getName());

		returnedTab.setName("some other name");
		Serializable id = returnedTab.getId();

		session.save(returnedTab);

		assertEquals(id, returnedTab.getId());
		assertEquals("some other name", returnedTab.getName());
	}

	@Test
	public void delete() {
		Tab tab = new Tab();
		tab.setName("some name");

		session.save(tab);

		assertEquals(tab.getName(), "some name");
		assertNotNull(tab.getId());

		Serializable id = tab.getId();

		session.delete(tab);

		Tab returnedTab = null;
		returnedTab = (Tab) session.get(Tab.class, id);

		/*
		 * try { } catch (HibernateObjectRetrievalFailureException e) { }
		 */

		assertNull(returnedTab);
	}

	@Test
	public void get_all_instances() {
		Tab firstTab = new Tab();
		firstTab.setName("this is the first one");

		session.save(firstTab);

		Tab secondTab = new Tab();
		secondTab.setName("this is the second object");

		session.save(secondTab);

		List<Tab> objectList = (List<Tab>) session.createCriteria(
				Tab.class).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstTab);
		assertTrue(i >= 0);

		assertFalse(objectList.isEmpty());
		int j = objectList.indexOf(secondTab);
		assertTrue(j >= 0);

		Tab tab;
		tab = objectList.get(i);

		assertEquals(firstTab, tab);
		assertEquals(firstTab.getName(), tab.getName());
	}

	@Test
	public void search_by_detached_criteria() {
		Tab firstTab = new Tab();
		firstTab.setName("this is the first one");

		session.save(firstTab);

		Tab secondTab = new Tab();
		secondTab.setName("this is the second object");

		session.save(secondTab);

		DetachedCriteria criteria = DetachedCriteria.forClass(Tab.class);
		criteria.add(Restrictions.like("name", "first", MatchMode.ANYWHERE));

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Tab> objectList = (List<Tab>) criteria
				.getExecutableCriteria(session).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstTab);
		assertTrue(i >= 0);

		int j = objectList.indexOf(secondTab);
		assertTrue(j == -1);

		Tab tab;

		tab = objectList.get(i);

		assertEquals(firstTab, tab);
		assertEquals(firstTab.getName(), tab.getName());
	}
}