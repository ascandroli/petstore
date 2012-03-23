package org.amneris.petstore.entities;

import java.util.Date;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.hibernate.annotations.NaturalId;
import org.tynamo.descriptor.annotation.PropertyDescriptor;

import javax.validation.constraints.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="suppliers")
public class Supplier
{
	@Id
	@NonVisual
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NaturalId
	@NotNull(message = "name can't be null")
	private String name;

	private String description;


	@Column(name ="link_rewrite")
	private String linkRewrite;

	@Column(name = "meta_title")
	private String metaTitle;

	@Column(name = "meta_keywords")
	private String metaKeywords;

	@Column(name = "meta_description")
	private String metaDescription;

	private boolean active;

	@Column(name = "created_on")
	@PropertyDescriptor(readOnly = true)
	private Date createdOn = new Date();

	@Column(name = "updated_on")
	private Date updatedOn = new Date();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLinkRewrite() {
		return linkRewrite;
	}

	public void setLinkRewrite(String linkRewrite) {
		this.linkRewrite = linkRewrite;
	}

	public String getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}

	public String getMetaKeywords() {
		return metaKeywords;
	}

	public void setMetaKeywords(String metaKeywords) {
		this.metaKeywords = metaKeywords;
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Supplier)) return false;

		Supplier supplier = (Supplier) o;

		if (getId() != null ? !getId().equals(supplier.getId()) : supplier.getId() != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	public String toString()
	{
		return getName();
	}
}
