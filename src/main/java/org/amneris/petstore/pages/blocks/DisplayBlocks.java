package org.amneris.petstore.pages.blocks;

import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.services.PropertyOutputContext;

import java.util.Collection;

public class DisplayBlocks
{
	@Environmental
	@Property(write = false)
	private PropertyOutputContext context;

	@Property
	private Object loopIterator;

	@Property
	private int loopIndex;

	public Boolean getCheck()
	{
		return (Boolean) context.getPropertyValue();
	}

	public Object[] getShowPageContext()
	{
		return new Object[]{context.getPropertyValue().getClass(), context.getPropertyValue()};
	}

	public Object[] getLoopShowPageContext()
	{
		return new Object[]{loopIterator.getClass(), loopIterator};
	}

	public boolean isLastElement()
	{
		return loopIndex >= ((Collection) context.getPropertyValue()).size() - 1;
	}


}
