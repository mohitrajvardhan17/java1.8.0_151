package com.sun.corba.se.impl.util;

import java.util.Dictionary;
import java.util.Enumeration;

public final class IdentityHashtable
  extends Dictionary
{
  private transient IdentityHashtableEntry[] table;
  private transient int count;
  private int threshold;
  private float loadFactor;
  
  public IdentityHashtable(int paramInt, float paramFloat)
  {
    if ((paramInt <= 0) || (paramFloat <= 0.0D)) {
      throw new IllegalArgumentException();
    }
    loadFactor = paramFloat;
    table = new IdentityHashtableEntry[paramInt];
    threshold = ((int)(paramInt * paramFloat));
  }
  
  public IdentityHashtable(int paramInt)
  {
    this(paramInt, 0.75F);
  }
  
  public IdentityHashtable()
  {
    this(101, 0.75F);
  }
  
  public int size()
  {
    return count;
  }
  
  public boolean isEmpty()
  {
    return count == 0;
  }
  
  public Enumeration keys()
  {
    return new IdentityHashtableEnumerator(table, true);
  }
  
  public Enumeration elements()
  {
    return new IdentityHashtableEnumerator(table, false);
  }
  
  public boolean contains(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry = table;
    int i = arrayOfIdentityHashtableEntry.length;
    while (i-- > 0) {
      for (IdentityHashtableEntry localIdentityHashtableEntry = arrayOfIdentityHashtableEntry[i]; localIdentityHashtableEntry != null; localIdentityHashtableEntry = next) {
        if (value == paramObject) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean containsKey(Object paramObject)
  {
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry = table;
    int i = System.identityHashCode(paramObject);
    int j = (i & 0x7FFFFFFF) % arrayOfIdentityHashtableEntry.length;
    for (IdentityHashtableEntry localIdentityHashtableEntry = arrayOfIdentityHashtableEntry[j]; localIdentityHashtableEntry != null; localIdentityHashtableEntry = next) {
      if ((hash == i) && (key == paramObject)) {
        return true;
      }
    }
    return false;
  }
  
  public Object get(Object paramObject)
  {
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry = table;
    int i = System.identityHashCode(paramObject);
    int j = (i & 0x7FFFFFFF) % arrayOfIdentityHashtableEntry.length;
    for (IdentityHashtableEntry localIdentityHashtableEntry = arrayOfIdentityHashtableEntry[j]; localIdentityHashtableEntry != null; localIdentityHashtableEntry = next) {
      if ((hash == i) && (key == paramObject)) {
        return value;
      }
    }
    return null;
  }
  
  protected void rehash()
  {
    int i = table.length;
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry1 = table;
    int j = i * 2 + 1;
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry2 = new IdentityHashtableEntry[j];
    threshold = ((int)(j * loadFactor));
    table = arrayOfIdentityHashtableEntry2;
    int k = i;
    while (k-- > 0)
    {
      IdentityHashtableEntry localIdentityHashtableEntry1 = arrayOfIdentityHashtableEntry1[k];
      while (localIdentityHashtableEntry1 != null)
      {
        IdentityHashtableEntry localIdentityHashtableEntry2 = localIdentityHashtableEntry1;
        localIdentityHashtableEntry1 = next;
        int m = (hash & 0x7FFFFFFF) % j;
        next = arrayOfIdentityHashtableEntry2[m];
        arrayOfIdentityHashtableEntry2[m] = localIdentityHashtableEntry2;
      }
    }
  }
  
  public Object put(Object paramObject1, Object paramObject2)
  {
    if (paramObject2 == null) {
      throw new NullPointerException();
    }
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry = table;
    int i = System.identityHashCode(paramObject1);
    int j = (i & 0x7FFFFFFF) % arrayOfIdentityHashtableEntry.length;
    for (IdentityHashtableEntry localIdentityHashtableEntry = arrayOfIdentityHashtableEntry[j]; localIdentityHashtableEntry != null; localIdentityHashtableEntry = next) {
      if ((hash == i) && (key == paramObject1))
      {
        Object localObject = value;
        value = paramObject2;
        return localObject;
      }
    }
    if (count >= threshold)
    {
      rehash();
      return put(paramObject1, paramObject2);
    }
    localIdentityHashtableEntry = new IdentityHashtableEntry();
    hash = i;
    key = paramObject1;
    value = paramObject2;
    next = arrayOfIdentityHashtableEntry[j];
    arrayOfIdentityHashtableEntry[j] = localIdentityHashtableEntry;
    count += 1;
    return null;
  }
  
  public Object remove(Object paramObject)
  {
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry = table;
    int i = System.identityHashCode(paramObject);
    int j = (i & 0x7FFFFFFF) % arrayOfIdentityHashtableEntry.length;
    IdentityHashtableEntry localIdentityHashtableEntry1 = arrayOfIdentityHashtableEntry[j];
    IdentityHashtableEntry localIdentityHashtableEntry2 = null;
    while (localIdentityHashtableEntry1 != null)
    {
      if ((hash == i) && (key == paramObject))
      {
        if (localIdentityHashtableEntry2 != null) {
          next = next;
        } else {
          arrayOfIdentityHashtableEntry[j] = next;
        }
        count -= 1;
        return value;
      }
      localIdentityHashtableEntry2 = localIdentityHashtableEntry1;
      localIdentityHashtableEntry1 = next;
    }
    return null;
  }
  
  public void clear()
  {
    IdentityHashtableEntry[] arrayOfIdentityHashtableEntry = table;
    int i = arrayOfIdentityHashtableEntry.length;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      arrayOfIdentityHashtableEntry[i] = null;
    }
    count = 0;
  }
  
  public String toString()
  {
    int i = size() - 1;
    StringBuffer localStringBuffer = new StringBuffer();
    Enumeration localEnumeration1 = keys();
    Enumeration localEnumeration2 = elements();
    localStringBuffer.append("{");
    for (int j = 0; j <= i; j++)
    {
      String str1 = localEnumeration1.nextElement().toString();
      String str2 = localEnumeration2.nextElement().toString();
      localStringBuffer.append(str1 + "=" + str2);
      if (j < i) {
        localStringBuffer.append(", ");
      }
    }
    localStringBuffer.append("}");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\util\IdentityHashtable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */