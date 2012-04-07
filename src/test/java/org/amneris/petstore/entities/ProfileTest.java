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
 * Class for Profile tests.
 * <p/>
 * This represents best practice for Hibernate integration tests.
 * <p/>
 * It executes each test method in its own transaction, which is automatically
 * rolled back by default.
 * <p/>
 * related info:
 * http://www.theserverside.com/tt/articles/article.tss?l=PersistentDomain
 */
public class ProfileTest {

	private Session session;
	private Transaction transaction;
	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void beforeAnything() {
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.test.cfg.xml");
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
		Profile profile = new Profile();
		profile.setName("some name");

		session.save(profile);

		Assert.assertEquals(profile.getName(), "some name");
		assertNotNull(profile.getId());
	}

	@Test
	public void save_object_without_name_should_fail() {
		Profile profile = new Profile();
		try {
			session.save(profile);
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
		Profile profile = new Profile();
		profile.setName("some name");

		session.save(profile);

		assertEquals(profile.getName(), "some name");
		assertNotNull(profile.getId());

		Profile returnedProfile;
		returnedProfile = (Profile) session.get(Profile.class,
				profile.getId());

		assertEquals(profile, returnedProfile);
		assertEquals(profile.getName(), returnedProfile.getName());

	}

	@Test
	public void update() {
		Profile profile = new Profile();
		profile.setName("some name");

		session.save(profile);

		assertEquals(profile.getName(), "some name");
		assertNotNull(profile.getId());

		Profile returnedProfile;
		returnedProfile = (Profile) session.get(Profile.class,
				profile.getId());

		assertEquals(profile, returnedProfile);
		assertEquals(profile.getName(), returnedProfile.getName());

		returnedProfile.setName("some other name");
		Serializable id = returnedProfile.getId();

		session.save(returnedProfile);

		assertEquals(id, returnedProfile.getId());
		assertEquals("some other name", returnedProfile.getName());
	}

	@Test
	public void delete() {
		Profile profile = new Profile();
		profile.setName("some name");

		session.save(profile);

		assertEquals(profile.getName(), "some name");
		assertNotNull(profile.getId());

		Serializable id = profile.getId();

		session.delete(profile);

		Profile returnedProfile = null;
		returnedProfile = (Profile) session.get(Profile.class, id);

		/*
		 * try { } catch (HibernateObjectRetrievalFailureException e) { }
		 */

		assertNull(returnedProfile);
	}

	@Test
	public void get_all_instances() {
		Profile firstProfile = new Profile();
		firstProfile.setName("this is the first one");

		session.save(firstProfile);

		Profile secondProfile = new Profile();
		secondProfile.setName("this is the second object");

		session.save(secondProfile);

		List<Profile> objectList = (List<Profile>) session.createCriteria(
				Profile.class).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstProfile);
		assertTrue(i >= 0);

		assertFalse(objectList.isEmpty());
		int j = objectList.indexOf(secondProfile);
		assertTrue(j >= 0);

		Profile profile;
		profile = objectList.get(i);

		assertEquals(firstProfile, profile);
		assertEquals(firstProfile.getName(), profile.getName());
	}

	@Test
	public void search_by_detached_criteria() {
		Profile firstProfile = new Profile();
		firstProfile.setName("this is the first one");

		session.save(firstProfile);

		Profile secondProfile = new Profile();
		secondProfile.setName("this is the second object");

		session.save(secondProfile);

		DetachedCriteria criteria = DetachedCriteria.forClass(Profile.class);
		criteria.add(Restrictions.like("name", "first", MatchMode.ANYWHERE));

		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<Profile> objectList = (List<Profile>) criteria
				.getExecutableCriteria(session).list();

		assertFalse(objectList.isEmpty());
		int i = objectList.indexOf(firstProfile);
		assertTrue(i >= 0);

		int j = objectList.indexOf(secondProfile);
		assertTrue(j == -1);

		Profile profile;

		profile = objectList.get(i);

		assertEquals(firstProfile, profile);
		assertEquals(firstProfile.getName(), profile.getName());
	}
}