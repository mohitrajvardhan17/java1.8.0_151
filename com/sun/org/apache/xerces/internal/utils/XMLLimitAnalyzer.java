package com.sun.org.apache.xerces.internal.utils;

import java.io.PrintStream;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

public final class XMLLimitAnalyzer
{
  private final int[] values = new int[XMLSecurityManager.Limit.values().length];
  private final String[] names = new String[XMLSecurityManager.Limit.values().length];
  private final int[] totalValue = new int[XMLSecurityManager.Limit.values().length];
  private final Map[] caches = new Map[XMLSecurityManager.Limit.values().length];
  private String entityStart;
  private String entityEnd;
  
  public XMLLimitAnalyzer() {}
  
  public void addValue(XMLSecurityManager.Limit paramLimit, String paramString, int paramInt)
  {
    addValue(paramLimit.ordinal(), paramString, paramInt);
  }
  
  public void addValue(int paramInt1, String paramString, int paramInt2)
  {
    if ((paramInt1 == XMLSecurityManager.Limit.ENTITY_EXPANSION_LIMIT.ordinal()) || (paramInt1 == XMLSecurityManager.Limit.MAX_OCCUR_NODE_LIMIT.ordinal()) || (paramInt1 == XMLSecurityManager.Limit.ELEMENT_ATTRIBUTE_LIMIT.ordinal()) || (paramInt1 == XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal()) || (paramInt1 == XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT.ordinal()))
    {
      totalValue[paramInt1] += paramInt2;
      return;
    }
    if ((paramInt1 == XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT.ordinal()) || (paramInt1 == XMLSecurityManager.Limit.MAX_NAME_LIMIT.ordinal()))
    {
      values[paramInt1] = paramInt2;
      totalValue[paramInt1] = paramInt2;
      return;
    }
    Object localObject;
    if (caches[paramInt1] == null)
    {
      localObject = new HashMap(10);
      caches[paramInt1] = localObject;
    }
    else
    {
      localObject = caches[paramInt1];
    }
    int i = paramInt2;
    if (((Map)localObject).containsKey(paramString))
    {
      i += ((Integer)((Map)localObject).get(paramString)).intValue();
      ((Map)localObject).put(paramString, Integer.valueOf(i));
    }
    else
    {
      ((Map)localObject).put(paramString, Integer.valueOf(paramInt2));
    }
    if (i > values[paramInt1])
    {
      values[paramInt1] = i;
      names[paramInt1] = paramString;
    }
    if ((paramInt1 == XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT.ordinal()) || (paramInt1 == XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT.ordinal())) {
      totalValue[XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal()] += paramInt2;
    }
  }
  
  public int getValue(XMLSecurityManager.Limit paramLimit)
  {
    return getValue(paramLimit.ordinal());
  }
  
  public int getValue(int paramInt)
  {
    if (paramInt == XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT.ordinal()) {
      return totalValue[paramInt];
    }
    return values[paramInt];
  }
  
  public int getTotalValue(XMLSecurityManager.Limit paramLimit)
  {
    return totalValue[paramLimit.ordinal()];
  }
  
  public int getTotalValue(int paramInt)
  {
    return totalValue[paramInt];
  }
  
  public int getValueByIndex(int paramInt)
  {
    return values[paramInt];
  }
  
  public void startEntity(String paramString)
  {
    entityStart = paramString;
  }
  
  public boolean isTracking(String paramString)
  {
    if (entityStart == null) {
      return false;
    }
    return entityStart.equals(paramString);
  }
  
  public void endEntity(XMLSecurityManager.Limit paramLimit, String paramString)
  {
    entityStart = "";
    Map localMap = caches[paramLimit.ordinal()];
    if (localMap != null) {
      localMap.remove(paramString);
    }
  }
  
  public void reset(XMLSecurityManager.Limit paramLimit)
  {
    if (paramLimit.ordinal() == XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal())
    {
      totalValue[paramLimit.ordinal()] = 0;
    }
    else if (paramLimit.ordinal() == XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT.ordinal())
    {
      names[paramLimit.ordinal()] = null;
      values[paramLimit.ordinal()] = 0;
      caches[paramLimit.ordinal()] = null;
      totalValue[paramLimit.ordinal()] = 0;
    }
  }
  
  public void debugPrint(XMLSecurityManager paramXMLSecurityManager)
  {
    Formatter localFormatter = new Formatter();
    System.out.println(localFormatter.format("%30s %15s %15s %15s %30s", new Object[] { "Property", "Limit", "Total size", "Size", "Entity Name" }));
    for (XMLSecurityManager.Limit localLimit : XMLSecurityManager.Limit.values())
    {
      localFormatter = new Formatter();
      System.out.println(localFormatter.format("%30s %15d %15d %15d %30s", new Object[] { localLimit.name(), Integer.valueOf(paramXMLSecurityManager.getLimit(localLimit)), Integer.valueOf(totalValue[localLimit.ordinal()]), Integer.valueOf(values[localLimit.ordinal()]), names[localLimit.ordinal()] }));
    }
  }
  
  public static enum NameMap
  {
    ENTITY_EXPANSION_LIMIT("jdk.xml.entityExpansionLimit", "entityExpansionLimit"),  MAX_OCCUR_NODE_LIMIT("jdk.xml.maxOccurLimit", "maxOccurLimit"),  ELEMENT_ATTRIBUTE_LIMIT("jdk.xml.elementAttributeLimit", "elementAttributeLimit");
    
    final String newName;
    final String oldName;
    
    private NameMap(String paramString1, String paramString2)
    {
      newName = paramString1;
      oldName = paramString2;
    }
    
    String getOldName(String paramString)
    {
      if (paramString.equals(newName)) {
        return oldName;
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\utils\XMLLimitAnalyzer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */