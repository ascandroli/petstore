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
 * Class for Employee tests.
 * <p/>
 * This represents best practice for Hibernate integration tests.
 * <p/>
 * It executes each test method in its own transaction, which is automatically
 * rolled back by default.
 * <p/>
 * related info:
 * http://www.theserverside.com/tt/articles/article.tss?l=PersistentDomain
 */
public class EmployeeTest {

	private Session session;
	private Transaction transaction;
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void beforeAnything() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.test.cfg.xml");
		configuration.addAnnotatedClass(Employee.class);
		configuration.addAnnotatedClass(Profile.class);
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
		Employee employee = new Employee();
		employee.setFirstName("some name");
		employee.setLastName("some last name");

		session.save(employee);

		Assert.assertEquals(employee.getFirstName(), "some name");
		assertNotNull(employee.getId());
	}

	@Test
	public void save_object_without_name_should_fail() {
		Employee employee = new Employee();
		try {
			session.save(employee);
			fail();
		} catch (ConstraintViolationException e) {
			assertTrue(e.getConstraintViolations().size() == 2);
			
			HashMap hash = new HashMap();
			hash.put("last name can't be null", "last name can't be null");
			hash.put("name can't be null", "name can't be null");
			
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
		Employee employee = new Employee();
		employee.setFirstName("some name");
		employee.setLastName("some last name");

		session.save(employee);

		assertEquals(employee.getFirstName(), "some name");
		assertNotNull(employee.getId());

		Employee returnedEmployee;
		returnedEmployee = (Employee) session.get(Employee.class,
				employee.getId());

		assertEquals(employee, returnedEmployee);
		assertEquals(employee.getFirstName(), returnedEmployee.getFirstName());

	}

	@Test
	public void update() {
		Employee employee = new Employee();
		employee.setFirstName("some name");
		employee.setLastName("some last name");

		session.save(employee);

		assertEquals(employee.getFirstName(), "some name");
		assertNotNull(employee.getId());

		Employee returnedEmployee;
		returnedEmployee = (Employee) session.get(Employee.class,
				employee.getId());

		assertEquals(employee, returnedEmployee);
		assertEquals(employee.getFirstName(), returnedEmployee.getFirstName());

		returnedEmployee.setFirstName("some other name");
		returnedEmployee.setLastName("some other last name");
		Serializable id = returnedEmployee.getId();

		session.save(returnedEmployee);

		assertEquals(id, returnedEmployee.getId());
		assertEquals("some other name", returnedEmployee.getFirstName());
	}

	@Test
	public void delete() {
		Employee employee = new Employee();
		employee.setFirstName("some name");
		employee.setLastName("some last name");
		
		session.save(employee);

		assertEquals(employee.getFirstName(), "some name");
		assertNotNull(employee.getId());

		Serializable id = employee.getId();

		session.delete(employee);

		Employee returnedEmployee = null;
		returnedEmployee = (Employee) session.get(Employee.class, id);

		/*
		 * try { } catch (HibernateObjectRetrievalFailureException e) { }
		 */

		assertNull(returnedEmployee);
	}

	@Test
	public void get_all_instances() {
		Employee firstEmployee = new Employee();
		firstEmployee.setFirstName("this is the first one");
		firstEmployee.setLastName("this is the last one");

		session.save(firstEmployee);

		Employee secondEmployee = new Employee();
		secondEmployee.setFirstName("this is the second object");
		secondEmployee.setLastName("this is the second object");

		session.save(secondEmployee);

		List<Employee> objectList = (List<Employee>) session.createCriteria(
				Employee.class).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstEmployee);
		assertTrue(i >= 0);

		assertFalse(objectList.isEmpty());
		int j = objectList.indexOf(secondEmployee);
		assertTrue(j >= 0);

		Employee employee;
		employee = objectList.get(i);

		assertEquals(firstEmployee, employee);
		assertEquals(firstEmployee.getFirstName(), employee.getFirstName());
	}

	@Test
	public void search_by_detached_criteria() {
		Employee firstEmployee = new Employee();
		firstEmployee.setFirstName("this is the first one");
		firstEmployee.setLastName("this is the first one");

		session.save(firstEmployee);

		Employee secondEmployee = new Employee();
		secondEmployee.setFirstName("this is the second object");
		secondEmployee.setLastName("this is the second object");

		session.save(secondEmployee);

		DetachedCriteria criteria = DetachedCriteria.forClass(Employee.class);
		criteria.add(Restrictions.like("firstName", "first", MatchMode.ANYWHERE));

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Employee> objectList = (List<Employee>) criteria
				.getExecutableCriteria(session).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstEmployee);
		assertTrue(i >= 0);

		int j = objectList.indexOf(secondEmployee);
		assertTrue(j == -1);

		Employee employee;

		employee = objectList.get(i);

		assertEquals(firstEmployee, employee);
		assertEquals(firstEmployee.getFirstName(), employee.getFirstName());
	}
}