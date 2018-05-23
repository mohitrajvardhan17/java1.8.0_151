package com.sun.corba.se.impl.oa.poa;

import java.util.HashMap;
import java.util.Map;
import org.omg.PortableServer.POAPackage.WrongPolicy;

class SingleObjectMap
  extends ActiveObjectMap
{
  private Map entryToKey = new HashMap();
  
  public SingleObjectMap(POAImpl paramPOAImpl)
  {
    super(paramPOAImpl);
  }
  
  public ActiveObjectMap.Key getKey(AOMEntry paramAOMEntry)
    throws WrongPolicy
  {
    return (ActiveObjectMap.Key)entryToKey.get(paramAOMEntry);
  }
  
  protected void putEntry(ActiveObjectMap.Key paramKey, AOMEntry paramAOMEntry)
  {
    super.putEntry(paramKey, paramAOMEntry);
    entryToKey.put(paramAOMEntry, paramKey);
  }
  
  public boolean hasMultipleIDs(AOMEntry paramAOMEntry)
  {
    return false;
  }
  
  protected void removeEntry(AOMEntry paramAOMEntry, ActiveObjectMap.Key paramKey)
  {
    entryToKey.remove(paramAOMEntry);
  }
  
  public void clear()
  {
    super.clear();
    entryToKey.clear();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\SingleObjectMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */