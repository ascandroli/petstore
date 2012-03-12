package org.amneris.petstore.entities;

import java.util.Date;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.hibernate.validator.NotNull;
import org.tynamo.descriptor.annotation.PropertyDescriptor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="PRODUCTS",uniqueConstraints = @UniqueConstraint(columnNames={"name"}))
public class Product
{
	@Id
	@NonVisual
	@GeneratedValue(strategy = GenerationType.AUTO)
	@PropertyDescriptor(index = 0)
	private Long id_product;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_category")
	private Category category;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_manufacturer")
	private Manufacturer manufacturer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_supplier")
	private Supplier supplier;

	private int quantity;
	private Long price;
	private Long reduction_price;
	
	@NotNull(message = "name can't be null")
	private String name;
	private String description;
	private String link_rewrite;
	private String meta_title;
	private String meta_keywords;
	private String meta_description;
	private boolean active;
	private Date date_add;
	private Date date_update;

	public Long getId_product() {
		return id_product;
	}

	public void setId_product(Long id_product) {
		this.id_product = id_product;
	}
	
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Long getReduction_price() {
		return reduction_price;
	}

	public void setReduction_price(Long reduction_price) {
		this.reduction_price = reduction_price;
	}

	public String getDescription() {
		return description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink_rewrite() {
		return link_rewrite;
	}

	public void setLink_rewrite(String link_rewrite) {
		this.link_rewrite = link_rewrite;
	}

	public String getMeta_title() {
		return meta_title;
	}

	public void setMeta_title(String meta_title) {
		this.meta_title = meta_title;
	}

	public String getMeta_keywords() {
		return meta_keywords;
	}

	public void setMeta_keywords(String meta_keywords) {
		this.meta_keywords = meta_keywords;
	}

	public String getMeta_description() {
		return meta_description;
	}

	public void setMeta_description(String meta_description) {
		this.meta_description = meta_description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getDate_add() {
		return date_add;
	}

	public void setDate_add(Date date_add) {
		this.date_add = date_add;
	}

	public Date getDate_update() {
		return date_update;
	}

	public void setDate_update(Date date_update) {
		this.date_update = date_update;
	}


	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Product that = (Product) o;

		return getId_product() != null ? getId_product().equals(that.getId_product()) : that.getId_product() == null;
	}

	public int hashCode()
	{
		return (getId_product() != null ? getId_product().hashCode() : 0);
	}

	public String toString()
	{
		return getName();
	}
}
