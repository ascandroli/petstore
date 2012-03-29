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
 * Class for Product tests.
 * <p/>
 * This represents best practice for Hibernate integration tests.
 * <p/>
 * It executes each test method in its own transaction, which is automatically rolled back by default.
 * <p/>
 * related info: http://www.theserverside.com/tt/articles/article.tss?l=PersistentDomain
 */
public class ProductTest {

	private Session session;
	private Transaction transaction;
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void beforeAnything() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.test.cfg.xml");
		configuration.addAnnotatedClass(Product.class);
		configuration.addAnnotatedClass(Category.class);
		configuration.addAnnotatedClass(Manufacturer.class);
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
		if (transaction != null) if (transaction.isActive()) transaction.rollback();
	}

	@Test
	public void simple_save() {
		Product product = new Product();
		product.setName("some name");

		session.save(product);

		Assert.assertEquals(product.getName(), "some name");
		assertNotNull(product.getId());
	}

	@Test
	public void save_object_without_name_should_fail() {
		Product product = new Product();
		try {
			session.save(product);
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
		Product product = new Product();
		product.setName("some name");

		session.save(product);

		assertEquals(product.getName(), "some name");
		assertNotNull(product.getId());

		Product returnedProduct;
		returnedProduct = (Product) session.get(Product.class, product.getId());

		assertEquals(product, returnedProduct);
		assertEquals(product.getName(), returnedProduct.getName());

	}

	@Test
	public void update() {
		Product product = new Product();
		product.setName("some name");

		session.save(product);

		assertEquals(product.getName(), "some name");
		assertNotNull(product.getId());

		Product returnedProduct;
		returnedProduct = (Product) session.get(Product.class, product.getId());

		assertEquals(product, returnedProduct);
		assertEquals(product.getName(), returnedProduct.getName());

		returnedProduct.setName("some other name");
		Serializable id = returnedProduct.getId();

		session.save(returnedProduct);

		assertEquals(id, returnedProduct.getId());
		assertEquals("some other name", returnedProduct.getName());
	}

	@Test
	public void delete() {
		Product product = new Product();
		product.setName("some name");

		session.save(product);

		assertEquals(product.getName(), "some name");
		assertNotNull(product.getId());

		Serializable id = product.getId();

		session.delete(product);
		
		Product returnedProduct = null;
		returnedProduct = (Product) session.get(Product.class, id);

/*
		try {
		} catch (HibernateObjectRetrievalFailureException e) {
		}
*/

		assertNull(returnedProduct);
	}

	@Test
	public void get_all_instances() {
		Product firstProduct = new Product();
		firstProduct.setName("this is the first one");

		session.save(firstProduct);

		Product secondProduct = new Product();
		secondProduct.setName("this is the second object");

		session.save(secondProduct);

		List<Product> objectList = (List<Product>) session.createCriteria(Product.class).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstProduct);
		assertTrue(i >= 0);

		assertFalse(objectList.isEmpty());
		int j = objectList.indexOf(secondProduct);
		assertTrue(j >= 0);

		Product product;
		product = objectList.get(i);

		assertEquals(firstProduct, product);
		assertEquals(firstProduct.getName(), product.getName());
	}

	@Test
	public void search_by_detached_criteria() {
		Product firstProduct = new Product();
		firstProduct.setName("this is the first one");

		session.save(firstProduct);

		Product secondProduct = new Product();
		secondProduct.setName("this is the second object");

		session.save(secondProduct);

		DetachedCriteria criteria = DetachedCriteria.forClass(Product.class);
		criteria.add(Restrictions.like("name", "first", MatchMode.ANYWHERE));

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Product> objectList = (List<Product>) criteria.getExecutableCriteria(session).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstProduct);
		assertTrue(i >= 0);

		int j = objectList.indexOf(secondProduct);
		assertTrue(j == -1);

		Product product;

		product = objectList.get(i);

		assertEquals(firstProduct, product);
		assertEquals(firstProduct.getName(), product.getName());
	}
}