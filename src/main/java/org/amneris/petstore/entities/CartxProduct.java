package org.amneris.petstore.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="cart_x_products")
public class CartxProduct implements Serializable
{
	@Id
	@NonVisual
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cart_id")
	private Cart cart;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="product_id")
	private Product product;

	private int quantity;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CartxProduct that = (CartxProduct) o;

		return getCart() != null || getProduct() != null ? getCart().equals(that.getCart()) && getProduct().equals(that.getProduct()) : that.getCart() == null && that.getProduct() == null;
	}

	@Override
	public int hashCode() {
		return getCart() != null ? getCart().hashCode() : 0;
	}

	public String toString()
	{
		return getCart().toString()+" "+getProduct().toString();
	}
}
