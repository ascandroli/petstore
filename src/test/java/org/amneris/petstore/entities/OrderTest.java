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
import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Class for Order tests.
 * <p/>
 * This represents best practice for Hibernate integration tests.
 * <p/>
 * It executes each test method in its own transaction, which is automatically
 * rolled back by default.
 * <p/>
 * related info:
 * http://www.theserverside.com/tt/articles/article.tss?l=PersistentDomain
 */
public class OrderTest {

	private Session session;
	private Transaction transaction;
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void beforeAnything() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.test.cfg.xml");
		configuration.addAnnotatedClass(Order.class);
		configuration.addAnnotatedClass(Customer.class);
		configuration.addAnnotatedClass(Cart.class);
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
		Customer customer = new Customer();
		customer.setFirstName("Alejandro");
		customer.setLastName("Doglioli");

		Currency currency = new Currency();
		currency.setName("Euro");
		currency.setIsoCode("EUR");
		currency.setConversionRate(1.0);

		Cart cart = new Cart();

		Order order = new Order();
		order.setShippingNumber("0020302");
		order.setPayment("credit card");
		order.setCustomer(customer);
		order.setCurrency(currency);
		order.setCart(cart);

		session.save(order);

		Assert.assertEquals(order.getShippingNumber(), "0020302");
		assertNotNull(order.getId());
	}

	@Test
	public void save_object_without_shipping_number_should_fail() {
		Order order = new Order();
		try {
			session.save(order);
			fail();
		} catch (ConstraintViolationException e) {
			assertTrue(e.getConstraintViolations().size() >= 1);
			HashMap hash = new HashMap();
			hash.put("payment can't be null", "payment can't be null");
			hash.put("shipping number can't be null",
					"shipping number can't be null");
			hash.put("null", "null");

			for (int i = 0; i < e.getConstraintViolations().size(); i++) {
				ConstraintViolation c = (ConstraintViolation) e
						.getConstraintViolations().toArray()[i];
				assertEquals(hash.get(c.getMessage()), c.getMessage());
			}

		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void retrieve() {
		Customer customer = new Customer();
		customer.setFirstName("Alejandro");
		customer.setLastName("Doglioli");

		Currency currency = new Currency();
		currency.setName("Euro");
		currency.setIsoCode("EUR");
		currency.setConversionRate(1.0);

		Cart cart = new Cart();

		Order order = new Order();
		order.setShippingNumber("0020302");
		order.setPayment("credit card");
		order.setCustomer(customer);
		order.setCurrency(currency);
		order.setCart(cart);

		session.save(order);

		assertEquals(order.getShippingNumber(), "0020302");
		assertNotNull(order.getId());

		Order returnedOrder;
		returnedOrder = (Order) session.get(Order.class, order.getId());

		assertEquals(order, returnedOrder);
		assertEquals(order.getShippingNumber(),
				returnedOrder.getShippingNumber());

	}

	@Test
	public void update() {
		Customer customer = new Customer();
		customer.setFirstName("Alejandro");
		customer.setLastName("Doglioli");

		Currency currency = new Currency();
		currency.setName("Euro");
		currency.setIsoCode("EUR");
		currency.setConversionRate(1.0);

		Cart cart = new Cart();

		Order order = new Order();
		order.setShippingNumber("0020302");
		order.setPayment("credit card");
		order.setCustomer(customer);
		order.setCurrency(currency);
		order.setCart(cart);

		session.save(order);

		assertEquals(order.getShippingNumber(), "0020302");
		assertNotNull(order.getId());

		Order returnedOrder;
		returnedOrder = (Order) session.get(Order.class, order.getId());

		assertEquals(order, returnedOrder);
		assertEquals(order.getShippingNumber(),
				returnedOrder.getShippingNumber());

		returnedOrder.setShippingNumber("0020333");
		Serializable id = returnedOrder.getId();

		session.save(returnedOrder);

		assertEquals(id, returnedOrder.getId());
		assertEquals("0020333", returnedOrder.getShippingNumber());
	}

	@Test
	public void delete() {
		Customer customer = new Customer();
		customer.setFirstName("Alejandro");
		customer.setLastName("Doglioli");

		Currency currency = new Currency();
		currency.setName("Euro");
		currency.setIsoCode("EUR");
		currency.setConversionRate(1.0);

		Cart cart = new Cart();

		Order order = new Order();
		order.setShippingNumber("0020302");
		order.setPayment("credit card");
		order.setCustomer(customer);
		order.setCurrency(currency);
		order.setCart(cart);

		session.save(order);

		assertEquals(order.getShippingNumber(), "0020302");
		assertNotNull(order.getId());

		Serializable id = order.getId();

		session.delete(order);

		Order returnedOrder = null;
		returnedOrder = (Order) session.get(Order.class, id);

		/*
		 * try { } catch (HibernateObjectRetrievalFailureException e) { }
		 */

		assertNull(returnedOrder);
	}

	@Test
	public void get_all_instances() {
		Customer customer = new Customer();
		customer.setFirstName("Alejandro");
		customer.setLastName("Doglioli");

		Currency currency = new Currency();
		currency.setName("Euro");
		currency.setIsoCode("EUR");
		currency.setConversionRate(1.0);

		Cart cart = new Cart();

		Order firstOrder = new Order();
		firstOrder.setShippingNumber("0020302");
		firstOrder.setPayment("credit card");
		firstOrder.setCustomer(customer);
		firstOrder.setCurrency(currency);
		firstOrder.setCart(cart);

		session.save(firstOrder);

		Order secondOrder = new Order();
		secondOrder.setShippingNumber("0020304");
		secondOrder.setPayment("credit card");
		secondOrder.setCustomer(customer);
		secondOrder.setCurrency(currency);
		secondOrder.setCart(cart);

		session.save(secondOrder);

		List<Order> objectList = (List<Order>) session.createCriteria(
				Order.class).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstOrder);
		assertTrue(i >= 0);

		assertFalse(objectList.isEmpty());
		int j = objectList.indexOf(secondOrder);
		assertTrue(j >= 0);

		Order order;
		order = objectList.get(i);

		assertEquals(firstOrder, order);
		assertEquals(firstOrder.getShippingNumber(), order.getShippingNumber());
	}

	@Test
	public void search_by_detached_criteria() {
		Customer customer = new Customer();
		customer.setFirstName("Alejandro");
		customer.setLastName("Doglioli");

		Currency currency = new Currency();
		currency.setName("Euro");
		currency.setIsoCode("EUR");
		currency.setConversionRate(1.0);

		Cart cart = new Cart();

		Order firstOrder = new Order();
		firstOrder.setShippingNumber("0020302");
		firstOrder.setPayment("credit card");
		firstOrder.setCustomer(customer);
		firstOrder.setCurrency(currency);
		firstOrder.setCart(cart);

		session.save(firstOrder);

		Order secondOrder = new Order();
		secondOrder.setShippingNumber("0020304");
		secondOrder.setPayment("credit card");
		secondOrder.setCustomer(customer);
		secondOrder.setCurrency(currency);
		secondOrder.setCart(cart);

		session.save(secondOrder);

		DetachedCriteria criteria = DetachedCriteria.forClass(Order.class);
		criteria.add(Restrictions.like("shippingNumber", "0020302",
				MatchMode.ANYWHERE));

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Order> objectList = (List<Order>) criteria.getExecutableCriteria(
				session).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstOrder);
		assertTrue(i >= 0);

		int j = objectList.indexOf(secondOrder);
		assertTrue(j == -1);

		Order order;

		order = objectList.get(i);

		assertEquals(firstOrder, order);
		assertEquals(firstOrder.getShippingNumber(), order.getShippingNumber());
	}
}