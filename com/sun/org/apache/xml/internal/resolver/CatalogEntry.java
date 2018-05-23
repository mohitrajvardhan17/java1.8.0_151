package com.sun.org.apache.xml.internal.resolver;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CatalogEntry
{
  protected static AtomicInteger nextEntry = new AtomicInteger(0);
  protected static final Map<String, Integer> entryTypes = new ConcurrentHashMap();
  protected static Vector entryArgs = new Vector();
  protected int entryType = 0;
  protected Vector args = null;
  
  static int addEntryType(String paramString, int paramInt)
  {
    int i = nextEntry.getAndIncrement();
    entryTypes.put(paramString, Integer.valueOf(i));
    entryArgs.add(i, Integer.valueOf(paramInt));
    return i;
  }
  
  public static int getEntryType(String paramString)
    throws CatalogException
  {
    if (!entryTypes.containsKey(paramString)) {
      throw new CatalogException(3);
    }
    Integer localInteger = (Integer)entryTypes.get(paramString);
    if (localInteger == null) {
      throw new CatalogException(3);
    }
    return localInteger.intValue();
  }
  
  public static int getEntryArgCount(String paramString)
    throws CatalogException
  {
    return getEntryArgCount(getEntryType(paramString));
  }
  
  public static int getEntryArgCount(int paramInt)
    throws CatalogException
  {
    try
    {
      Integer localInteger = (Integer)entryArgs.get(paramInt);
      return localInteger.intValue();
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new CatalogException(3);
    }
  }
  
  public CatalogEntry() {}
  
  public CatalogEntry(String paramString, Vector paramVector)
    throws CatalogException
  {
    Integer localInteger1 = (Integer)entryTypes.get(paramString);
    if (localInteger1 == null) {
      throw new CatalogException(3);
    }
    int i = localInteger1.intValue();
    try
    {
      Integer localInteger2 = (Integer)entryArgs.get(i);
      if (localInteger2.intValue() != paramVector.size()) {
        throw new CatalogException(2);
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new CatalogException(3);
    }
    entryType = i;
    args = paramVector;
  }
  
  public CatalogEntry(int paramInt, Vector paramVector)
    throws CatalogException
  {
    try
    {
      Integer localInteger = (Integer)entryArgs.get(paramInt);
      if (localInteger.intValue() != paramVector.size()) {
        throw new CatalogException(2);
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new CatalogException(3);
    }
    entryType = paramInt;
    args = paramVector;
  }
  
  public int getEntryType()
  {
    return entryType;
  }
  
  public String getEntryArg(int paramInt)
  {
    try
    {
      String str = (String)args.get(paramInt);
      return str;
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    return null;
  }
  
  public void setEntryArg(int paramInt, String paramString)
    throws ArrayIndexOutOfBoundsException
  {
    args.set(paramInt, paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\CatalogEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */