package kr.dja.aldarEconomy.api;

import java.util.UUID;

public class SystemID
{
	public final UUID uuid;
	
	SystemID(UUID uuid)
	{
		this.uuid = uuid;
	}
	
	@Override
	public int hashCode()
	{
	    int result = 1;
	    result = 31 * uuid.hashCode();
	    return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(super.equals(obj))
		{
			if(obj instanceof SystemID)
			{
				SystemID id = (SystemID)obj;
				if(id.uuid.equals(this.uuid)) return true;
			}
		}
		return false;
	}
}
