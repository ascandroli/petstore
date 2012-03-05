package org.amneris.petstore.entities;

import org.hibernate.validator.NotNull;
import org.tynamo.descriptor.annotation.PropertyDescriptor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MyDomainObject
{
	private Long id;

	private String name;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@PropertyDescriptor(index = 0)
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	@NotNull(message = "name can't be null")
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MyDomainObject that = (MyDomainObject) o;

		return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
	}

	public int hashCode()
	{
		return (getId() != null ? getId().hashCode() : 0);
	}

	public String toString()
	{
		return getName();
	}
}
