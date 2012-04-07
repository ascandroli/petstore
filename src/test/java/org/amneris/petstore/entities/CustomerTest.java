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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Class for Customer tests.
 * <p/>
 * This represents best practice for Hibernate integration tests.
 * <p/>
 * It executes each test method in its own transaction, which is automatically
 * rolled back by default.
 * <p/>
 * related info:
 * http://www.theserverside.com/tt/articles/article.tss?l=PersistentDomain
 */
public class CustomerTest {

	private Session session;
	private Transaction transaction;
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void beforeAnything() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.test.cfg.xml");
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
		customer.setFirstName("some name");
		customer.setLastName("some lastname");
		customer.setEmail("some email");
		customer.setPassword("some password");
		
		session.save(customer);

		Assert.assertEquals(customer.getFirstName(), "some name");
		assertNotNull(customer.getId());
	}

	@Test
	public void save_object_without_mandatory_fields_should_fail() {
		Customer customer = new Customer();
		try {
			session.save(customer);
			fail();
		} catch (ConstraintViolationException e) {
			assertTrue(e.getConstraintViolations().size() == 4);
			HashMap hash = new HashMap();
			hash.put("last name can't be null", "last name can't be null");
			hash.put("name can't be null", "name can't be null");
			hash.put("email can't be null", "email can't be null");
			hash.put("password can't be null", "password can't be null");
			
			for(int i=0;i<e.getConstraintViolations().size();i++){
				ConstraintViolation c = (ConstraintViolation) e.getConstraintViolations().toArray()[i];
				assertEquals(hash.get(c.getMessage()), c.getMessage());
			}
									
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void retrieve() {
		Customer customer = new Customer();
		customer.setFirstName("some name");
		customer.setLastName("some lastname");
		customer.setEmail("some email");
		customer.setPassword("some password");
		
		session.save(customer);

		assertEquals(customer.getFirstName(), "some name");
		assertNotNull(customer.getId());

		Customer returnedCustomer;
		returnedCustomer = (Customer) session.get(Customer.class,
				customer.getId());

		assertEquals(customer, returnedCustomer);
		assertEquals(customer.getFirstName(), returnedCustomer.getFirstName());
	}

	@Test
	public void update() {
		Customer customer = new Customer();
		customer.setFirstName("some name");
		customer.setLastName("some lastname");
		customer.setEmail("some email");
		customer.setPassword("some password");
		
		session.save(customer);

		assertEquals(customer.getFirstName(), "some name");
		assertNotNull(customer.getId());

		Customer returnedCustomer;
		returnedCustomer = (Customer) session.get(Customer.class,
				customer.getId());

		assertEquals(customer, returnedCustomer);
		assertEquals(customer.getFirstName(), returnedCustomer.getFirstName());

		returnedCustomer.setFirstName("some other name");
		Serializable id = returnedCustomer.getId();

		session.save(returnedCustomer);

		assertEquals(id, returnedCustomer.getId());
		assertEquals("some other name", returnedCustomer.getFirstName());
	}

	@Test
	public void delete() {
		Customer customer = new Customer();
		customer.setFirstName("some name");
		customer.setLastName("some lastname");
		customer.setEmail("some email");
		customer.setPassword("some password");
		
		session.save(customer);

		assertEquals(customer.getFirstName(), "some name");
		assertNotNull(customer.getId());

		Serializable id = customer.getId();

		session.delete(customer);

		Customer returnedCustomer = null;
		returnedCustomer = (Customer) session.get(Customer.class, id);

		/*
		 * try { } catch (HibernateObjectRetrievalFailureException e) { }
		 */

		assertNull(returnedCustomer);
	}

	@Test
	public void get_all_instances() {
		Customer firstCustomer = new Customer();
		firstCustomer.setFirstName("this is the first one");
		firstCustomer.setLastName("some lastname");
		firstCustomer.setEmail("some email");
		firstCustomer.setPassword("some password");
		
		session.save(firstCustomer);

		Customer secondCustomer = new Customer();
		secondCustomer.setFirstName("this is the second object");
		secondCustomer.setLastName("some lastname");
		secondCustomer.setEmail("some email");
		secondCustomer.setPassword("some password");
		
		session.save(secondCustomer);

		List<Customer> objectList = (List<Customer>) session.createCriteria(
				Customer.class).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstCustomer);
		assertTrue(i >= 0);

		assertFalse(objectList.isEmpty());
		int j = objectList.indexOf(secondCustomer);
		assertTrue(j >= 0);

		Customer customer;
		customer = objectList.get(i);

		assertEquals(firstCustomer, customer);
		assertEquals(firstCustomer.getFirstName(), customer.getFirstName());
	}

	@Test
	public void search_by_detached_criteria() {
		Customer firstCustomer = new Customer();
		firstCustomer.setFirstName("this is the first one");
		firstCustomer.setLastName("some lastname");
		firstCustomer.setEmail("some email");
		firstCustomer.setPassword("some password");

		session.save(firstCustomer);

		Customer secondCustomer = new Customer();
		secondCustomer.setFirstName("this is the second object");
		secondCustomer.setLastName("some lastname");
		secondCustomer.setEmail("some email");
		secondCustomer.setPassword("some password");

		session.save(secondCustomer);

		DetachedCriteria criteria = DetachedCriteria.forClass(Customer.class);
		criteria.add(Restrictions.like("firstName", "first", MatchMode.ANYWHERE));

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Customer> objectList = (List<Customer>) criteria
				.getExecutableCriteria(session).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstCustomer);
		assertTrue(i >= 0);

		int j = objectList.indexOf(secondCustomer);
		assertTrue(j == -1);

		Customer customer;
		customer = objectList.get(i);

		assertEquals(firstCustomer, customer);
		assertEquals(firstCustomer.getFirstName(), customer.getFirstName());
	}
}