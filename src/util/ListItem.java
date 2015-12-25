package util;



public class ListItem
{
	
	private Object valueMember;
	private Object displayMember;
	
	public ListItem(Object valueMember, Object displayMember)
	{
		this.valueMember = valueMember;
		this.displayMember = displayMember;
		
	}


	public Object getValueMember()
	{
		return valueMember;
	}

	public Object getDisplayMember()
	{
		return displayMember;
	}

	@Override
	public String toString()
	{
		return getDisplayMember().toString();
	}
	
	
	
	
	

}
