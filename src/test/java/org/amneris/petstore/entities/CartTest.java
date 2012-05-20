package org.amneris.petstore.entities;

import org.hibernate.Criteria;
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
 * Class for Cart tests.
 * <p/>
 * This represents best practice for Hibernate integration tests.
 * <p/>
 * It executes each test method in its own transaction, which is automatically
 * rolled back by default.
 * <p/>
 * related info:
 * http://www.theserverside.com/tt/articles/article.tss?l=PersistentDomain
 */
public class CartTest {

	private Session session;
	private Transaction transaction;
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void beforeAnything() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.test.cfg.xml");
		configuration.addAnnotatedClass(Cart.class);
		configuration.addAnnotatedClass(Customer.class);
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
	
		Customer customer = new Customer();
		customer.setFirstName("some customer");
		customer.setLastName("some customer");
		customer.setEmail("some email");
		customer.setPassword("some password");
		session.save(customer);
		
		Cart cart = new Cart();
		cart.setCustomer(customer);
	
		session.save(cart);

		Assert.assertEquals(cart.getCustomer().getFirstName(), "some customer");
		assertNotNull(cart.getId());
	}

	@Test
	public void save_object_customer_should_fail() {
		Cart cart = new Cart();
		try {
			session.save(cart);
			fail();
		} catch (ConstraintViolationException e) {
			assertTrue(e.getConstraintViolations().size() == 1);
			ConstraintViolation c = (ConstraintViolation) e
					.getConstraintViolations().toArray()[0];
			assertEquals("customer can't be null", c.getMessage());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void retrieve() {
		Customer customer = new Customer();
		customer.setFirstName("some customer");
		customer.setLastName("some customer");
		customer.setEmail("some email");
		customer.setPassword("some password");
		
		session.save(customer);
		
		Cart cart = new Cart();
		cart.setCustomer(customer);
		
		session.save(cart);

		assertEquals(cart.getCustomer().getFirstName(), "some customer");
		assertNotNull(cart.getId());

		Cart returnedCart;
		returnedCart = (Cart) session.get(Cart.class,
				cart.getId());

		assertEquals(cart, returnedCart);
		assertEquals(cart.getId(), returnedCart.getId());
	}

	@Test
	public void update() {
		Customer customer = new Customer();
		customer.setFirstName("some customer");
		customer.setLastName("some customer");
		customer.setEmail("some email");
		customer.setPassword("some password");
		
		session.save(customer);
		
		Cart cart = new Cart();
		cart.setCustomer(customer);
		
		session.save(cart);

		assertEquals(cart.getCustomer().getFirstName(), "some customer");
		assertNotNull(cart.getId());

		Cart returnedCart;
		returnedCart = (Cart) session.get(Cart.class,
				cart.getId());

		assertEquals(cart, returnedCart);
		assertEquals(cart.getCustomer(), returnedCart.getCustomer());

		Customer otherCustomer = new Customer();
		otherCustomer.setFirstName("other customer");
		
		returnedCart.setCustomer(otherCustomer);
		Serializable id = returnedCart.getId();

		session.save(returnedCart);

		assertEquals(id, returnedCart.getId());
		assertEquals("other customer", returnedCart.getCustomer().getFirstName());
	}

	@Test
	public void delete() {
		Customer customer = new Customer();
		customer.setFirstName("some customer");
		customer.setLastName("some customer");
		customer.setEmail("some email");
		customer.setPassword("some password");
		
		session.save(customer);
		
		Cart cart = new Cart();
		cart.setCustomer(customer);
		
		session.save(cart);
		
		assertEquals(cart.getCustomer().getFirstName(), "some customer");
		assertNotNull(cart.getId());

		Serializable id = cart.getId();

		session.delete(cart);

		Cart returnedCart = null;
		returnedCart = (Cart) session.get(Cart.class, id);

		/*
		 * try { } catch (HibernateObjectRetrievalFailureException e) { }
		 */

		assertNull(returnedCart);
	}

	@Test
	public void get_all_instances() {
		Customer firstCustomer = new Customer();
		firstCustomer.setFirstName("some customer");
		firstCustomer.setLastName("some customer");
		firstCustomer.setEmail("some email");
		firstCustomer.setPassword("some password");
		
		session.save(firstCustomer);
		
		Cart firstCart = new Cart();
		firstCart.setCustomer(firstCustomer);
		session.save(firstCart);

		Customer secondCustomer = new Customer();
		secondCustomer.setFirstName("second customer");
		secondCustomer.setLastName("second customer");
		secondCustomer.setEmail("some email");
		secondCustomer.setPassword("some password");
		session.save(secondCustomer);
		
		Cart secondCart = new Cart();
		secondCart.setCustomer(secondCustomer);
		
		session.save(secondCart);

		List<Cart> objectList = (List<Cart>) session.createCriteria(
				Cart.class).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstCart);
		assertTrue(i >= 0);

		assertFalse(objectList.isEmpty());
		int j = objectList.indexOf(secondCart);
		assertTrue(j >= 0);

		Cart cart;
		cart = objectList.get(i);

		assertEquals(firstCart, cart);
		assertEquals(firstCart.getCustomer(), cart.getCustomer());
	}

//	@Test
	public void search_by_detached_criteria() {
		Customer firstCustomer = new Customer();
		firstCustomer.setFirstName("some customer");
		firstCustomer.setLastName("some customer");
		firstCustomer.setEmail("some email");
		firstCustomer.setPassword("some password");
		
		session.save(firstCustomer);
		
		Cart firstCart = new Cart();
		firstCart.setCustomer(firstCustomer);
		session.save(firstCart);

		Customer secondCustomer = new Customer();
		secondCustomer.setFirstName("second customer");
		secondCustomer.setLastName("second customer");
		secondCustomer.setEmail("some email");
		secondCustomer.setPassword("some password");
		session.save(secondCustomer);
		
		Cart secondCart = new Cart();
		secondCart.setCustomer(secondCustomer);
		session.save(secondCart);

		DetachedCriteria criteria = DetachedCriteria.forClass(Cart.class);
		criteria.add(Restrictions.like("customer.first_name", "first", MatchMode.ANYWHERE));

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Cart> objectList = (List<Cart>) criteria
				.getExecutableCriteria(session).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstCart);
		assertTrue(i >= 0);

		int j = objectList.indexOf(secondCart);
		assertTrue(j == -1);

		Cart cart;

		cart = objectList.get(i);

		assertEquals(firstCart, cart);
		assertEquals(firstCart.getCustomer(), cart.getCustomer());
	}
}
