package org.amneris.petstore.entities;

import org.tynamo.descriptor.annotation.beaneditor.BeanModel;
import org.tynamo.descriptor.annotation.beaneditor.BeanModels;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "users")
@XmlRootElement(name = "user")
@BeanModels({
		@BeanModel(reorder = "id") // == @ReorderProperties("id")
})
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Pattern(regexp = "[A-z|\\s]+", message = "{error.letterOrSpace}")
	private String name;

	private boolean checked;

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

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		User that = (User) o;

		return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
	}

	public int hashCode() {
		return (getId() != null ? getId().hashCode() : 0);
	}

	public String toString() {
		return getName();
	}
}
