package org.amneris.petstore.entities;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="cart_x_products")
public class CartxProduct implements Serializable
{
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cart_id")
	private Cart cart;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="product_id")
	private Product product;
	
	private int quantity;

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
