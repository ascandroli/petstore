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
 * Class for Access tests.
 * <p/>
 * This represents best practice for Hibernate integration tests.
 * <p/>
 * It executes each test method in its own transaction, which is automatically
 * rolled back by default.
 * <p/>
 * related info:
 * http://www.theserverside.com/tt/articles/article.tss?l=PersistentDomain
 */
public class AccessTest {

	private Session session;
	private Transaction transaction;
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void beforeAnything() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.test.cfg.xml");
		configuration.addAnnotatedClass(Access.class);
		configuration.addAnnotatedClass(Tab.class);
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
		Tab tab = new Tab();
		tab.setName("some tab");
		session.save(tab);
		
		Profile profile = new Profile();
		profile.setName("some profile");
		session.save(profile);
		
		Access access = new Access();
		access.setTab(tab);
		access.setProfile(profile);

		session.save(access);

		assertNotNull(access.getProfile());
		assertNotNull(access.getTab());

		Assert.assertEquals(access.getProfile().getName(), "some profile");
		Assert.assertEquals(access.getTab().getName(), "some tab");
	}

	@Test
	public void save_object_without_name_should_fail() {
		Access access = new Access();
		try {
			session.save(access);
			fail();
		} catch (ConstraintViolationException e) {
			assertTrue(e.getConstraintViolations().size() == 1);
			ConstraintViolation c = (ConstraintViolation) e
					.getConstraintViolations().toArray()[0];
			assertEquals("profile can't be null", c.getMessage());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void retrieve() {
		Tab tab = new Tab();
		tab.setName("some tab");
		session.save(tab);
		
		Profile profile = new Profile();
		profile.setName("some profile");
		session.save(profile);
		
		Access access = new Access();
		access.setTab(tab);
		access.setProfile(profile);

		session.save(access);

		assertNotNull(access.getProfile());
		assertNotNull(access.getTab());
		
		assertEquals(access.getProfile().getName(), "some profile");
		assertEquals(access.getTab().getName(), "some tab");
		
		Access returnedAccess;
		returnedAccess = (Access) session.get(Access.class, access);

		assertEquals(access, returnedAccess);
		assertEquals(access.getProfile(), returnedAccess.getProfile());

	}

	@Test
	public void update() {
		Tab tab = new Tab();
		tab.setName("some tab");
		session.save(tab);
		
		Profile profile = new Profile();
		profile.setName("some profile");
		session.save(profile);
		
		Access access = new Access();
		access.setTab(tab);
		access.setProfile(profile);

		session.save(access);

		assertNotNull(access.getProfile());
		assertNotNull(access.getTab());
		
		assertEquals(access.getProfile().getName(), "some profile");
		assertEquals(access.getTab().getName(), "some tab");
		
		Access returnedAccess;
		returnedAccess = (Access) session.get(Access.class, access);

		assertEquals(access, returnedAccess);
		assertEquals(access.getProfile(), returnedAccess.getProfile());

		Profile otherProfile = new Profile();
		otherProfile.setName("some other profile");

		returnedAccess.setProfile(otherProfile);
		Serializable id = returnedAccess;

		session.save(returnedAccess);

		assertEquals(id, returnedAccess);
		assertEquals("some other profile", returnedAccess.getProfile().getName());
	}

	@Test
	public void delete() {
		Tab tab = new Tab();
		tab.setName("some tab");
		session.save(tab);
		
		Profile profile = new Profile();
		profile.setName("some profile");
		session.save(profile);
		
		Access access = new Access();
		access.setTab(tab);
		access.setProfile(profile);
		
		session.save(access);

		assertEquals(access.getProfile().getName(), "some profile");
		assertNotNull(access.getProfile());

		Serializable id = access;

		session.delete(access);

		Access returnedAccess = null;
		returnedAccess = (Access) session.get(Access.class, id);

		/*
		 * try { } catch (HibernateObjectRetrievalFailureException e) { }
		 */

		assertNull(returnedAccess);
	}

	@Test
	public void get_all_instances() {
		Tab firstTab = new Tab();
		firstTab.setName("some tab");
		session.save(firstTab);
		
		Profile firstProfile = new Profile();
		firstProfile.setName("some profile");
		session.save(firstProfile);
		
		Access firstAccess = new Access();
		firstAccess.setTab(firstTab);
		firstAccess.setProfile(firstProfile);
	
		session.save(firstAccess);

		Tab secondTab = new Tab();
		secondTab.setName("some other tab");
		session.save(secondTab);
		
		Profile secondProfile = new Profile();
		secondProfile.setName("some other profile");
		session.save(secondProfile);
		
		Access secondAccess = new Access();
		secondAccess.setTab(secondTab);
		secondAccess.setProfile(secondProfile);
		
		session.save(secondAccess);

		List<Access> objectList = (List<Access>) session.createCriteria(
				Access.class).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstAccess);
		assertTrue(i >= 0);

		assertFalse(objectList.isEmpty());
		int j = objectList.indexOf(secondAccess);
		assertTrue(j >= 0);

		Access access;
		access = objectList.get(i);

		assertEquals(firstAccess, access);
		assertEquals(firstAccess.getProfile(), access.getProfile());
	}

	@Test
	public void search_by_detached_criteria() {
		Tab firstTab = new Tab();
		firstTab.setName("some tab");
		session.save(firstTab);
		
		Profile firstProfile = new Profile();
		firstProfile.setName("some profile");
		session.save(firstProfile);
		
		Access firstAccess = new Access();
		firstAccess.setTab(firstTab);
		firstAccess.setProfile(firstProfile);
	
		session.save(firstAccess);

		Tab secondTab = new Tab();
		secondTab.setName("some other tab");
		session.save(secondTab);
		
		Profile secondProfile = new Profile();
		secondProfile.setName("some other profile");
		session.save(secondProfile);
		
		Access secondAccess = new Access();
		secondAccess.setTab(secondTab);
		secondAccess.setProfile(secondProfile);
		
		session.save(secondAccess);

		DetachedCriteria criteria = DetachedCriteria.forClass(Access.class);
		criteria.add(Restrictions.like("profile name", "other", MatchMode.ANYWHERE));

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Access> objectList = (List<Access>) criteria
				.getExecutableCriteria(session).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstAccess);
		assertTrue(i >= 0);

		int j = objectList.indexOf(secondAccess);
		assertTrue(j == -1);

		Access access;

		access = objectList.get(i);

		assertEquals(firstAccess, access);
		assertEquals(firstAccess.getProfile(), access.getProfile());
	}
}