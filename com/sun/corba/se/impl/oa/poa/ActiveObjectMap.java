package com.sun.corba.se.impl.oa.poa;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;

public abstract class ActiveObjectMap
{
  protected POAImpl poa;
  private Map keyToEntry = new HashMap();
  private Map entryToServant = new HashMap();
  private Map servantToEntry = new HashMap();
  
  protected ActiveObjectMap(POAImpl paramPOAImpl)
  {
    poa = paramPOAImpl;
  }
  
  public static ActiveObjectMap create(POAImpl paramPOAImpl, boolean paramBoolean)
  {
    if (paramBoolean) {
      return new MultipleObjectMap(paramPOAImpl);
    }
    return new SingleObjectMap(paramPOAImpl);
  }
  
  public final boolean contains(Servant paramServant)
  {
    return servantToEntry.containsKey(paramServant);
  }
  
  public final boolean containsKey(Key paramKey)
  {
    return keyToEntry.containsKey(paramKey);
  }
  
  public final AOMEntry get(Key paramKey)
  {
    AOMEntry localAOMEntry = (AOMEntry)keyToEntry.get(paramKey);
    if (localAOMEntry == null)
    {
      localAOMEntry = new AOMEntry(poa);
      putEntry(paramKey, localAOMEntry);
    }
    return localAOMEntry;
  }
  
  public final Servant getServant(AOMEntry paramAOMEntry)
  {
    return (Servant)entryToServant.get(paramAOMEntry);
  }
  
  public abstract Key getKey(AOMEntry paramAOMEntry)
    throws WrongPolicy;
  
  public Key getKey(Servant paramServant)
    throws WrongPolicy
  {
    AOMEntry localAOMEntry = (AOMEntry)servantToEntry.get(paramServant);
    return getKey(localAOMEntry);
  }
  
  protected void putEntry(Key paramKey, AOMEntry paramAOMEntry)
  {
    keyToEntry.put(paramKey, paramAOMEntry);
  }
  
  public final void putServant(Servant paramServant, AOMEntry paramAOMEntry)
  {
    entryToServant.put(paramAOMEntry, paramServant);
    servantToEntry.put(paramServant, paramAOMEntry);
  }
  
  protected abstract void removeEntry(AOMEntry paramAOMEntry, Key paramKey);
  
  public final void remove(Key paramKey)
  {
    AOMEntry localAOMEntry = (AOMEntry)keyToEntry.remove(paramKey);
    Servant localServant = (Servant)entryToServant.remove(localAOMEntry);
    if (localServant != null) {
      servantToEntry.remove(localServant);
    }
    removeEntry(localAOMEntry, paramKey);
  }
  
  public abstract boolean hasMultipleIDs(AOMEntry paramAOMEntry);
  
  protected void clear()
  {
    keyToEntry.clear();
  }
  
  public final Set keySet()
  {
    return keyToEntry.keySet();
  }
  
  public static class Key
  {
    public byte[] id;
    
    Key(byte[] paramArrayOfByte)
    {
      id = paramArrayOfByte;
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      for (int i = 0; i < id.length; i++)
      {
        localStringBuffer.append(Integer.toString(id[i], 16));
        if (i != id.length - 1) {
          localStringBuffer.append(":");
        }
      }
      return localStringBuffer.toString();
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Key)) {
        return false;
      }
      Key localKey = (Key)paramObject;
      if (id.length != id.length) {
        return false;
      }
      for (int i = 0; i < id.length; i++) {
        if (id[i] != id[i]) {
          return false;
        }
      }
      return true;
    }
    
    public int hashCode()
    {
      int i = 0;
      for (int j = 0; j < id.length; j++) {
        i = 31 * i + id[j];
      }
      return i;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\ActiveObjectMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */