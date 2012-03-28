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
 * Class for Category tests.
 * <p/>
 * This represents best practice for Hibernate integration tests.
 * <p/>
 * It executes each test method in its own transaction, which is automatically rolled back by default.
 * <p/>
 * related info: http://www.theserverside.com/tt/articles/article.tss?l=PersistentDomain
 */
public class CategoryTest {

	private Session session;
	private Transaction transaction;
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void beforeAnything() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.test.cfg.xml");
		configuration.addAnnotatedClass(Category.class);
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
		Category category = new Category();
		category.setName("some name");

		session.save(category);

		Assert.assertEquals(category.getName(), "some name");
		assertNotNull(category.getId());
	}

	@Test
	public void save_object_without_name_should_fail() {
		Category category = new Category();
		try {
			session.save(category);
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
		Category category = new Category();
		category.setName("some name");

		session.save(category);

		assertEquals(category.getName(), "some name");
		assertNotNull(category.getId());

		Category returnedCategory;
		returnedCategory = (Category) session.get(Category.class, category.getId());

		assertEquals(category, returnedCategory);
		assertEquals(category.getName(), returnedCategory.getName());

	}

	@Test
	public void update() {
		Category category = new Category();
		category.setName("some name");

		session.save(category);

		assertEquals(category.getName(), "some name");
		assertNotNull(category.getId());

		Category returnedCategory;
		returnedCategory = (Category) session.get(Category.class, category.getId());

		assertEquals(category, returnedCategory);
		assertEquals(category.getName(), returnedCategory.getName());

		returnedCategory.setName("some other name");
		Serializable id = returnedCategory.getId();

		session.save(returnedCategory);

		assertEquals(id, returnedCategory.getId());
		assertEquals("some other name", returnedCategory.getName());
	}

	@Test
	public void delete() {
		Category category = new Category();
		category.setName("some name");

		session.save(category);

		assertEquals(category.getName(), "some name");
		assertNotNull(category.getId());

		Serializable id = category.getId();

		session.delete(category);
		
		Category returnedCategory = null;
		returnedCategory = (Category) session.get(Category.class, id);

/*
		try {
		} catch (HibernateObjectRetrievalFailureException e) {
		}
*/

		assertNull(returnedCategory);
	}

	@Test
	public void get_all_instances() {
		Category firstCategory = new Category();
		firstCategory.setName("this is the first one");

		session.save(firstCategory);

		Category secondCategory = new Category();
		secondCategory.setName("this is the second object");

		session.save(secondCategory);

		List<Category> objectList = (List<Category>) session.createCriteria(Category.class).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstCategory);
		assertTrue(i >= 0);

		assertFalse(objectList.isEmpty());
		int j = objectList.indexOf(secondCategory);
		assertTrue(j >= 0);

		Category category;
		category = objectList.get(i);

		assertEquals(firstCategory, category);
		assertEquals(firstCategory.getName(), category.getName());
	}

	@Test
	public void search_by_detached_criteria() {
		Category firstCategory = new Category();
		firstCategory.setName("this is the first one");

		session.save(firstCategory);

		Category secondCategory = new Category();
		secondCategory.setName("this is the second object");

		session.save(secondCategory);

		DetachedCriteria criteria = DetachedCriteria.forClass(Category.class);
		criteria.add(Restrictions.like("name", "first", MatchMode.ANYWHERE));

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Category> objectList = (List<Category>) criteria.getExecutableCriteria(session).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstCategory);
		assertTrue(i >= 0);

		int j = objectList.indexOf(secondCategory);
		assertTrue(j == -1);

		Category category;

		category = objectList.get(i);

		assertEquals(firstCategory, category);
		assertEquals(firstCategory.getName(), category.getName());
	}
}