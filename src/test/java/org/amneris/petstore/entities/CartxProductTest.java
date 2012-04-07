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
 * Class for CartxProduct tests.
 * <p/>
 * This represents best practice for Hibernate integration tests.
 * <p/>
 * It executes each test method in its own transaction, which is automatically
 * rolled back by default.
 * <p/>
 * related info:
 * http://www.theserverside.com/tt/articles/article.tss?l=PersistentDomain
 */
public class CartxProductTest {

	private Session session;
	private Transaction transaction;
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void beforeAnything() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.test.cfg.xml");
		configuration.addAnnotatedClass(CartxProduct.class);
		configuration.addAnnotatedClass(Cart.class);
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
		if (transaction != null)
			if (transaction.isActive())
				transaction.rollback();
	}

	@Test
	public void simple_save() {
		Category category = new Category();
		category.setName("some category");
		
		Product product = new Product();
		product.setName("some product");
		product.setCategory(category);
		
		Cart cart = new Cart();
	
		CartxProduct cartxProduct = new CartxProduct();
		cartxProduct.setCart(cart);
		cartxProduct.setProduct(product);
		cartxProduct.setQuantity(2);
		
		session.save(cartxProduct);

		Assert.assertEquals(cartxProduct.getProduct().getName(), "some product");
		assertNotNull(cartxProduct.getProduct());
	}

	@Test
	public void save_object_without_name_should_fail() {
		CartxProduct cartxProduct = new CartxProduct();
		try {
			session.save(cartxProduct);
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
		CartxProduct cartxProduct = new CartxProduct();
		cartxProduct.setName("some name");

		session.save(cartxProduct);

		assertEquals(cartxProduct.getName(), "some name");
		assertNotNull(cartxProduct.getId());

		CartxProduct returnedCartxProduct;
		returnedCartxProduct = (CartxProduct) session.get(CartxProduct.class,
				cartxProduct.getId());

		assertEquals(cartxProduct, returnedCartxProduct);
		assertEquals(cartxProduct.getName(), returnedCartxProduct.getName());

	}

	@Test
	public void update() {
		CartxProduct cartxProduct = new CartxProduct();
		cartxProduct.setName("some name");

		session.save(cartxProduct);

		assertEquals(cartxProduct.getName(), "some name");
		assertNotNull(cartxProduct.getId());

		CartxProduct returnedCartxProduct;
		returnedCartxProduct = (CartxProduct) session.get(CartxProduct.class,
				cartxProduct.getId());

		assertEquals(cartxProduct, returnedCartxProduct);
		assertEquals(cartxProduct.getName(), returnedCartxProduct.getName());

		returnedCartxProduct.setName("some other name");
		Serializable id = returnedCartxProduct.getId();

		session.save(returnedCartxProduct);

		assertEquals(id, returnedCartxProduct.getId());
		assertEquals("some other name", returnedCartxProduct.getName());
	}

	@Test
	public void delete() {
		CartxProduct cartxProduct = new CartxProduct();
		cartxProduct.setName("some name");

		session.save(cartxProduct);

		assertEquals(cartxProduct.getName(), "some name");
		assertNotNull(cartxProduct.getId());

		Serializable id = cartxProduct.getId();

		session.delete(cartxProduct);

		CartxProduct returnedCartxProduct = null;
		returnedCartxProduct = (CartxProduct) session.get(CartxProduct.class, id);

		/*
		 * try { } catch (HibernateObjectRetrievalFailureException e) { }
		 */

		assertNull(returnedCartxProduct);
	}

	@Test
	public void get_all_instances() {
		CartxProduct firstCartxProduct = new CartxProduct();
		firstCartxProduct.setName("this is the first one");

		session.save(firstCartxProduct);

		CartxProduct secondCartxProduct = new CartxProduct();
		secondCartxProduct.setName("this is the second object");

		session.save(secondCartxProduct);

		List<CartxProduct> objectList = (List<CartxProduct>) session.createCriteria(
				CartxProduct.class).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstCartxProduct);
		assertTrue(i >= 0);

		assertFalse(objectList.isEmpty());
		int j = objectList.indexOf(secondCartxProduct);
		assertTrue(j >= 0);

		CartxProduct cartxProduct;
		cartxProduct = objectList.get(i);

		assertEquals(firstCartxProduct, cartxProduct);
		assertEquals(firstCartxProduct.getName(), cartxProduct.getName());
	}

	@Test
	public void search_by_detached_criteria() {
		CartxProduct firstCartxProduct = new CartxProduct();
		firstCartxProduct.setName("this is the first one");

		session.save(firstCartxProduct);

		CartxProduct secondCartxProduct = new CartxProduct();
		secondCartxProduct.setName("this is the second object");

		session.save(secondCartxProduct);

		DetachedCriteria criteria = DetachedCriteria.forClass(CartxProduct.class);
		criteria.add(Restrictions.like("name", "first", MatchMode.ANYWHERE));

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<CartxProduct> objectList = (List<CartxProduct>) criteria
				.getExecutableCriteria(session).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstCartxProduct);
		assertTrue(i >= 0);

		int j = objectList.indexOf(secondCartxProduct);
		assertTrue(j == -1);

		CartxProduct cartxProduct;

		cartxProduct = objectList.get(i);

		assertEquals(firstCartxProduct, cartxProduct);
		assertEquals(firstCartxProduct.getName(), cartxProduct.getName());
	}
}