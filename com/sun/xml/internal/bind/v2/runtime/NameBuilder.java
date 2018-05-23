package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.util.QNameMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.namespace.QName;

public final class NameBuilder
{
  private Map<String, Integer> uriIndexMap = new HashMap();
  private Set<String> nonDefaultableNsUris = new HashSet();
  private Map<String, Integer> localNameIndexMap = new HashMap();
  private QNameMap<Integer> elementQNameIndexMap = new QNameMap();
  private QNameMap<Integer> attributeQNameIndexMap = new QNameMap();
  
  public NameBuilder() {}
  
  public Name createElementName(QName paramQName)
  {
    return createElementName(paramQName.getNamespaceURI(), paramQName.getLocalPart());
  }
  
  public Name createElementName(String paramString1, String paramString2)
  {
    return createName(paramString1, paramString2, false, elementQNameIndexMap);
  }
  
  public Name createAttributeName(QName paramQName)
  {
    return createAttributeName(paramQName.getNamespaceURI(), paramQName.getLocalPart());
  }
  
  public Name createAttributeName(String paramString1, String paramString2)
  {
    assert (paramString1.intern() == paramString1);
    assert (paramString2.intern() == paramString2);
    if (paramString1.length() == 0) {
      return new Name(allocIndex(attributeQNameIndexMap, "", paramString2), -1, paramString1, allocIndex(localNameIndexMap, paramString2), paramString2, true);
    }
    nonDefaultableNsUris.add(paramString1);
    return createName(paramString1, paramString2, true, attributeQNameIndexMap);
  }
  
  private Name createName(String paramString1, String paramString2, boolean paramBoolean, QNameMap<Integer> paramQNameMap)
  {
    assert (paramString1.intern() == paramString1);
    assert (paramString2.intern() == paramString2);
    return new Name(allocIndex(paramQNameMap, paramString1, paramString2), allocIndex(uriIndexMap, paramString1), paramString1, allocIndex(localNameIndexMap, paramString2), paramString2, paramBoolean);
  }
  
  private int allocIndex(Map<String, Integer> paramMap, String paramString)
  {
    Integer localInteger = (Integer)paramMap.get(paramString);
    if (localInteger == null)
    {
      localInteger = Integer.valueOf(paramMap.size());
      paramMap.put(paramString, localInteger);
    }
    return localInteger.intValue();
  }
  
  private int allocIndex(QNameMap<Integer> paramQNameMap, String paramString1, String paramString2)
  {
    Integer localInteger = (Integer)paramQNameMap.get(paramString1, paramString2);
    if (localInteger == null)
    {
      localInteger = Integer.valueOf(paramQNameMap.size());
      paramQNameMap.put(paramString1, paramString2, localInteger);
    }
    return localInteger.intValue();
  }
  
  public NameList conclude()
  {
    boolean[] arrayOfBoolean = new boolean[uriIndexMap.size()];
    Object localObject = uriIndexMap.entrySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
      arrayOfBoolean[((Integer)localEntry.getValue()).intValue()] = nonDefaultableNsUris.contains(localEntry.getKey());
    }
    localObject = new NameList(list(uriIndexMap), arrayOfBoolean, list(localNameIndexMap), elementQNameIndexMap.size(), attributeQNameIndexMap.size());
    uriIndexMap = null;
    localNameIndexMap = null;
    return (NameList)localObject;
  }
  
  private String[] list(Map<String, Integer> paramMap)
  {
    String[] arrayOfString = new String[paramMap.size()];
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      arrayOfString[((Integer)localEntry.getValue()).intValue()] = ((String)localEntry.getKey());
    }
    return arrayOfString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\NameBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */