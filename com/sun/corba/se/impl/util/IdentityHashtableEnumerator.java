package com.sun.corba.se.impl.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

class IdentityHashtableEnumerator
  implements Enumeration
{
  boolean keys;
  int index;
  IdentityHashtableEntry[] table;
  IdentityHashtableEntry entry;
  
  IdentityHashtableEnumerator(IdentityHashtableEntry[] paramArrayOfIdentityHashtableEntry, boolean paramBoolean)
  {
    table = paramArrayOfIdentityHashtableEntry;
    keys = paramBoolean;
    index = paramArrayOfIdentityHashtableEntry.length;
  }
  
  public boolean hasMoreElements()
  {
    if (entry != null) {
      return true;
    }
    while (index-- > 0) {
      if ((entry = table[index]) != null) {
        return true;
      }
    }
    return false;
  }
  
  public Object nextElement()
  {
    while ((entry == null) && (index-- > 0) && ((entry = table[index]) == null)) {}
    if (entry != null)
    {
      IdentityHashtableEntry localIdentityHashtableEntry = entry;
      entry = next;
      return keys ? key : value;
    }
    throw new NoSuchElementException("IdentityHashtableEnumerator");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\util\IdentityHashtableEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */