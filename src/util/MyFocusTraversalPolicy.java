package util;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.Vector;

import javax.swing.text.JTextComponent;


public class MyFocusTraversalPolicy extends FocusTraversalPolicy
{

	
	
	private Vector<Component> components;
	
	
	public MyFocusTraversalPolicy(Vector<Component> components)
	{
		
		this.components = components;
	}
	
	@Override
	public Component getComponentAfter(Container aContainer, Component c)
	{
		if (!containsFocusableComponents())
			return aContainer;
		
		int idx = (components.indexOf(c) + 1) % components.size();
		
		if (isFocusable(components.get(idx)))
			return components.get(idx);
		else
			
			
			return getComponentAfter(aContainer, components.get(idx));
		
	}

	@Override
	public Component getComponentBefore(Container aContainer, Component c)
	{
		
		
		if (!containsFocusableComponents())
			return getFirstComponent(aContainer);
				
//		int idx = components.indexOf(c) - 1;
//		if (idx < 0)
//			idx = components.size() - 1;
		
		// oder:
		int idx = (components.indexOf(c) - 1 + components.size()) % components.size(); 
		
		if (isFocusable(components.get(idx)))
			return components.get(idx);
		else
			
			return getComponentBefore(aContainer, components.get(idx));
		
	}

	@Override
	public Component getDefaultComponent(Container aContainer)
	{
		return getFirstComponent(aContainer);
	}

	@Override
	public Component getFirstComponent(Container aContainer)
	{
		if (!containsFocusableComponents())
			return components.firstElement();
		
		if (isFocusable(components.firstElement()))
			return components.firstElement();
		else
			return getComponentAfter(aContainer, components.firstElement());
	}

	@Override
	public Component getLastComponent(Container aContainer)
	{
		if (isFocusable(components.lastElement()))
			return components.lastElement();
		else
			return getComponentBefore(aContainer, components.lastElement());
	}
	
	
	private boolean isFocusable(Component c)
	{
		
		JTextComponent tc;
		
		
		if (c instanceof  JTextComponent)
		{
			tc = (JTextComponent)c;
			if (!tc.isEditable())
				return false;
		}
		
		
		return c.isVisible() && c.isEnabled() && c.isFocusable();
		
	}
	
	
	
	private boolean containsFocusableComponents()
	{
		boolean retValue = false;
		
		
		for (Component c : components)
		{
			if (isFocusable(c))
			{
				retValue = true;
				break;
			}
		
		}
		
		return retValue;
		
	}
	
	
	
	public void enableAllComponents(boolean b)
	{
		for (Component c : components)
			c.setEnabled(b);
		
	}
	
	
	
}
