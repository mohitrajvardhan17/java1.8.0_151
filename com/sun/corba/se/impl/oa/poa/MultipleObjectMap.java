package com.sun.corba.se.impl.oa.poa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.omg.PortableServer.POAPackage.WrongPolicy;

class MultipleObjectMap
  extends ActiveObjectMap
{
  private Map entryToKeys = new HashMap();
  
  public MultipleObjectMap(POAImpl paramPOAImpl)
  {
    super(paramPOAImpl);
  }
  
  public ActiveObjectMap.Key getKey(AOMEntry paramAOMEntry)
    throws WrongPolicy
  {
    throw new WrongPolicy();
  }
  
  protected void putEntry(ActiveObjectMap.Key paramKey, AOMEntry paramAOMEntry)
  {
    super.putEntry(paramKey, paramAOMEntry);
    Object localObject = (Set)entryToKeys.get(paramAOMEntry);
    if (localObject == null)
    {
      localObject = new HashSet();
      entryToKeys.put(paramAOMEntry, localObject);
    }
    ((Set)localObject).add(paramKey);
  }
  
  public boolean hasMultipleIDs(AOMEntry paramAOMEntry)
  {
    Set localSet = (Set)entryToKeys.get(paramAOMEntry);
    if (localSet == null) {
      return false;
    }
    return localSet.size() > 1;
  }
  
  protected void removeEntry(AOMEntry paramAOMEntry, ActiveObjectMap.Key paramKey)
  {
    Set localSet = (Set)entryToKeys.get(paramAOMEntry);
    if (localSet != null)
    {
      localSet.remove(paramKey);
      if (localSet.isEmpty()) {
        entryToKeys.remove(paramAOMEntry);
      }
    }
  }
  
  public void clear()
  {
    super.clear();
    entryToKeys.clear();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\poa\MultipleObjectMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */