package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.util.ArrayList;
import java.util.List;

class SymbMap
  implements Cloneable
{
  int free = 23;
  NameSpaceSymbEntry[] entries = new NameSpaceSymbEntry[free];
  String[] keys = new String[free];
  
  SymbMap() {}
  
  void put(String paramString, NameSpaceSymbEntry paramNameSpaceSymbEntry)
  {
    int i = index(paramString);
    String str = keys[i];
    keys[i] = paramString;
    entries[i] = paramNameSpaceSymbEntry;
    if (((str == null) || (!str.equals(paramString))) && (--free == 0))
    {
      free = entries.length;
      int j = free << 2;
      rehash(j);
    }
  }
  
  List<NameSpaceSymbEntry> entrySet()
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < entries.length; i++) {
      if ((entries[i] != null) && (!"".equals(entries[i].uri))) {
        localArrayList.add(entries[i]);
      }
    }
    return localArrayList;
  }
  
  protected int index(Object paramObject)
  {
    String[] arrayOfString = keys;
    int i = arrayOfString.length;
    int j = (paramObject.hashCode() & 0x7FFFFFFF) % i;
    String str = arrayOfString[j];
    if ((str == null) || (str.equals(paramObject))) {
      return j;
    }
    i--;
    do
    {
      j++;
      j = j == i ? 0 : j;
      str = arrayOfString[j];
    } while ((str != null) && (!str.equals(paramObject)));
    return j;
  }
  
  protected void rehash(int paramInt)
  {
    int i = keys.length;
    String[] arrayOfString = keys;
    NameSpaceSymbEntry[] arrayOfNameSpaceSymbEntry = entries;
    keys = new String[paramInt];
    entries = new NameSpaceSymbEntry[paramInt];
    int j = i;
    while (j-- > 0) {
      if (arrayOfString[j] != null)
      {
        String str = arrayOfString[j];
        int k = index(str);
        keys[k] = str;
        entries[k] = arrayOfNameSpaceSymbEntry[j];
      }
    }
  }
  
  NameSpaceSymbEntry get(String paramString)
  {
    return entries[index(paramString)];
  }
  
  protected Object clone()
  {
    try
    {
      SymbMap localSymbMap = (SymbMap)super.clone();
      entries = new NameSpaceSymbEntry[entries.length];
      System.arraycopy(entries, 0, entries, 0, entries.length);
      keys = new String[keys.length];
      System.arraycopy(keys, 0, keys, 0, keys.length);
      return localSymbMap;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localCloneNotSupportedException.printStackTrace();
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\SymbMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */