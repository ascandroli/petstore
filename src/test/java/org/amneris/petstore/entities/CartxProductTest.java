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
		configuration.addAnnotatedClass(Customer.class);
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
		session.save(category);
		
		Product product = new Product();
		product.setName("some product");
		product.setCategory(category);
		session.save(product);
		
		Customer customer = new Customer();
		customer.setFirstName("first name");
		customer.setLastName("last name");
		customer.setEmail("email");
		customer.setPassword("password");
		session.save(customer);
		
		Cart cart = new Cart();
		cart.setCustomer(customer);
		session.save(cart);
		
		CartxProduct cartxProduct = new CartxProduct();
		cartxProduct.setCart(cart);
		cartxProduct.setProduct(product);
		cartxProduct.setQuantity(2);
		
		session.save(cartxProduct);

		Assert.assertEquals(cartxProduct.getProduct().getName(), "some product");
		assertNotNull(cartxProduct.getProduct());
	}

	@Test
	public void save_object_without_product_should_fail() {
		CartxProduct cartxProduct = new CartxProduct();
		try {
			session.save(cartxProduct);
			fail();
		} catch (ConstraintViolationException e) {
			assertTrue(e.getConstraintViolations().size() == 3);
			ConstraintViolation c = (ConstraintViolation) e
					.getConstraintViolations().toArray()[0];
			assertEquals("product can't be null", c.getMessage());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void retrieve() {
		Category category = new Category();
		category.setName("some category");
		session.save(category);
		
		Product product = new Product();
		product.setName("some product");
		product.setCategory(category);
		session.save(product);
		
		Customer customer = new Customer();
		customer.setFirstName("first name");
		customer.setLastName("last name");
		customer.setEmail("email");
		customer.setPassword("password");
		session.save(customer);
		
		Cart cart = new Cart();
		cart.setCustomer(customer);
		session.save(cart);
		
		CartxProduct cartxProduct = new CartxProduct();
		cartxProduct.setCart(cart);
		cartxProduct.setProduct(product);
		cartxProduct.setQuantity(2);
		
		session.save(cartxProduct);

		assertEquals(cartxProduct.getProduct().getName(), "some product");
		assertNotNull(cartxProduct.getCart());
		assertNotNull(cartxProduct.getProduct());

		CartxProduct returnedCartxProduct;
		returnedCartxProduct = (CartxProduct) session.get(CartxProduct.class,
				cartxProduct);

		assertEquals(cartxProduct, returnedCartxProduct);
		assertEquals(cartxProduct.getCart(), returnedCartxProduct.getCart());

	}

	@Test
	public void update() {
		Category category = new Category();
		category.setName("some category");
		session.save(category);
		
		Product product = new Product();
		product.setName("some product");
		product.setCategory(category);
		session.save(product);
		
		Customer customer = new Customer();
		customer.setFirstName("first name");
		customer.setLastName("last name");
		customer.setEmail("email");
		customer.setPassword("password");
		session.save(customer);
		
		Cart cart = new Cart();
		cart.setCustomer(customer);
		session.save(cart);
		
		CartxProduct cartxProduct = new CartxProduct();
		cartxProduct.setCart(cart);
		cartxProduct.setProduct(product);
		cartxProduct.setQuantity(2);

		session.save(cartxProduct);

		assertEquals(cartxProduct.getProduct().getName(), "some product");
		assertNotNull(cartxProduct.getProduct());
		assertNotNull(cartxProduct.getCart());

		CartxProduct returnedCartxProduct;
		returnedCartxProduct = (CartxProduct) session.get(CartxProduct.class,
				cartxProduct);

		assertEquals(cartxProduct, returnedCartxProduct);
		assertEquals(cartxProduct.getProduct(), returnedCartxProduct.getProduct());

		returnedCartxProduct.setQuantity(4);
		Serializable id = returnedCartxProduct;

		session.save(returnedCartxProduct);

		assertEquals(id, returnedCartxProduct);
		assertEquals(4, returnedCartxProduct.getQuantity());
	}

	@Test
	public void delete() {
		Category category = new Category();
		category.setName("some category");
		session.save(category);
		
		Product product = new Product();
		product.setName("some product");
		product.setCategory(category);
		session.save(product);
		
		Customer customer = new Customer();
		customer.setFirstName("first name");
		customer.setLastName("last name");
		customer.setEmail("email");
		customer.setPassword("password");
		session.save(customer);
		
		Cart cart = new Cart();
		cart.setCustomer(customer);
		session.save(cart);
		
		CartxProduct cartxProduct = new CartxProduct();
		cartxProduct.setCart(cart);
		cartxProduct.setProduct(product);
		cartxProduct.setQuantity(2);

		session.save(cartxProduct);

		assertEquals(cartxProduct.getProduct().getName(), "some product");
		assertNotNull(cartxProduct.getProduct());
		assertNotNull(cartxProduct.getCart());

		Serializable id = cartxProduct;

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
		Category category = new Category();
		category.setName("some category");
		session.save(category);
		
		Product product = new Product();
		product.setName("some product");
		product.setCategory(category);
		session.save(product);
		
		Customer customer = new Customer();
		customer.setFirstName("first name");
		customer.setLastName("last name");
		customer.setEmail("email");
		customer.setPassword("password");
		session.save(customer);
		
		Cart cart = new Cart();
		cart.setCustomer(customer);
		session.save(cart);
		
		CartxProduct firstCartxProduct = new CartxProduct();
		firstCartxProduct.setCart(cart);
		firstCartxProduct.setProduct(product);
		firstCartxProduct.setQuantity(2);

		session.save(firstCartxProduct);

		Product secondProduct = new Product();
		secondProduct.setName("some other product");
		secondProduct.setCategory(category);
		session.save(secondProduct);

		Customer secondCustomer = new Customer();
		secondCustomer.setFirstName("first name");
		secondCustomer.setLastName("last name");
		secondCustomer.setEmail("email");
		secondCustomer.setPassword("password");
		session.save(secondCustomer);
		
		Cart secondCart = new Cart();
		secondCart.setCustomer(secondCustomer);
		session.save(secondCart);
		
		CartxProduct secondCartxProduct = new CartxProduct();
		secondCartxProduct.setCart(secondCart);
		secondCartxProduct.setProduct(secondProduct);
		secondCartxProduct.setQuantity(4);

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
		assertEquals(firstCartxProduct.getQuantity(), cartxProduct.getQuantity());
	}

	@Test
	public void search_by_detached_criteria() {
		Category category = new Category();
		category.setName("some category");
		session.save(category);
		
		Product product = new Product();
		product.setName("some product");
		product.setCategory(category);
		session.save(product);
		
		Customer customer = new Customer();
		customer.setFirstName("first name");
		customer.setLastName("last name");
		customer.setEmail("email");
		customer.setPassword("password");
		session.save(customer);
		
		Cart cart = new Cart();
		cart.setCustomer(customer);
		session.save(cart);
		
		CartxProduct firstCartxProduct = new CartxProduct();
		firstCartxProduct.setCart(cart);
		firstCartxProduct.setProduct(product);
		firstCartxProduct.setQuantity(2);

		session.save(firstCartxProduct);

		Product secondProduct = new Product();
		secondProduct.setName("some other product");
		secondProduct.setCategory(category);
		session.save(secondProduct);

		Customer secondCustomer = new Customer();
		secondCustomer.setFirstName("first name");
		secondCustomer.setLastName("last name");
		secondCustomer.setEmail("email");
		secondCustomer.setPassword("password");
		session.save(secondCustomer);
		
		Cart secondCart = new Cart();
		secondCart.setCustomer(secondCustomer);
		session.save(secondCart);
		
		CartxProduct secondCartxProduct = new CartxProduct();
		secondCartxProduct.setCart(secondCart);
		secondCartxProduct.setProduct(secondProduct);
		secondCartxProduct.setQuantity(4);

		session.save(secondCartxProduct);

		DetachedCriteria criteria = DetachedCriteria.forClass(CartxProduct.class);
		criteria.add(Restrictions.like("product.name", "other", MatchMode.ANYWHERE));

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
		assertEquals(firstCartxProduct.getQuantity(), cartxProduct.getQuantity());
	}
}