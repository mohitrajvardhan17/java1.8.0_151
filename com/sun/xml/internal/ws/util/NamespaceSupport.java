package com.sun.xml.internal.ws.util;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class NamespaceSupport
{
  public static final String XMLNS = "http://www.w3.org/XML/1998/namespace";
  private static final Iterable<String> EMPTY_ENUMERATION = new ArrayList();
  private Context[] contexts;
  private Context currentContext;
  private int contextPos;
  
  public NamespaceSupport()
  {
    reset();
  }
  
  public NamespaceSupport(NamespaceSupport paramNamespaceSupport)
  {
    contexts = new Context[contexts.length];
    currentContext = null;
    contextPos = contextPos;
    Object localObject = null;
    for (int i = 0; i < contexts.length; i++)
    {
      Context localContext1 = contexts[i];
      if (localContext1 == null)
      {
        contexts[i] = null;
      }
      else
      {
        Context localContext2 = new Context(localContext1, (Context)localObject);
        contexts[i] = localContext2;
        if (currentContext == localContext1) {
          currentContext = localContext2;
        }
        localObject = localContext2;
      }
    }
  }
  
  public void reset()
  {
    contexts = new Context[32];
    contextPos = 0;
    contexts[contextPos] = (currentContext = new Context());
    currentContext.declarePrefix("xml", "http://www.w3.org/XML/1998/namespace");
  }
  
  public void pushContext()
  {
    int i = contexts.length;
    contextPos += 1;
    if (contextPos >= i)
    {
      Context[] arrayOfContext = new Context[i * 2];
      System.arraycopy(contexts, 0, arrayOfContext, 0, i);
      contexts = arrayOfContext;
    }
    currentContext = contexts[contextPos];
    if (currentContext == null) {
      contexts[contextPos] = (currentContext = new Context());
    }
    if (contextPos > 0) {
      currentContext.setParent(contexts[(contextPos - 1)]);
    }
  }
  
  public void popContext()
  {
    contextPos -= 1;
    if (contextPos < 0) {
      throw new EmptyStackException();
    }
    currentContext = contexts[contextPos];
  }
  
  public void slideContextUp()
  {
    contextPos -= 1;
    currentContext = contexts[contextPos];
  }
  
  public void slideContextDown()
  {
    contextPos += 1;
    if (contexts[contextPos] == null) {
      contexts[contextPos] = contexts[(contextPos - 1)];
    }
    currentContext = contexts[contextPos];
  }
  
  public boolean declarePrefix(String paramString1, String paramString2)
  {
    if (((paramString1.equals("xml")) && (!paramString2.equals("http://www.w3.org/XML/1998/namespace"))) || (paramString1.equals("xmlns"))) {
      return false;
    }
    currentContext.declarePrefix(paramString1, paramString2);
    return true;
  }
  
  public String[] processName(String paramString, String[] paramArrayOfString, boolean paramBoolean)
  {
    String[] arrayOfString = currentContext.processName(paramString, paramBoolean);
    if (arrayOfString == null) {
      return null;
    }
    paramArrayOfString[0] = arrayOfString[0];
    paramArrayOfString[1] = arrayOfString[1];
    paramArrayOfString[2] = arrayOfString[2];
    return paramArrayOfString;
  }
  
  public String getURI(String paramString)
  {
    return currentContext.getURI(paramString);
  }
  
  public Iterable<String> getPrefixes()
  {
    return currentContext.getPrefixes();
  }
  
  public String getPrefix(String paramString)
  {
    return currentContext.getPrefix(paramString);
  }
  
  public Iterator getPrefixes(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = getPrefixes().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (paramString.equals(getURI(str))) {
        localArrayList.add(str);
      }
    }
    return localArrayList.iterator();
  }
  
  public Iterable<String> getDeclaredPrefixes()
  {
    return currentContext.getDeclaredPrefixes();
  }
  
  static final class Context
  {
    HashMap prefixTable;
    HashMap uriTable;
    HashMap elementNameTable;
    HashMap attributeNameTable;
    String defaultNS = null;
    private ArrayList declarations = null;
    private boolean tablesDirty = false;
    private Context parent = null;
    
    Context()
    {
      copyTables();
    }
    
    Context(Context paramContext1, Context paramContext2)
    {
      if (paramContext1 == null)
      {
        copyTables();
        return;
      }
      if ((paramContext2 != null) && (!tablesDirty))
      {
        prefixTable = (prefixTable == parent.prefixTable ? prefixTable : (HashMap)prefixTable.clone());
        uriTable = (uriTable == parent.uriTable ? uriTable : (HashMap)uriTable.clone());
        elementNameTable = (elementNameTable == parent.elementNameTable ? elementNameTable : (HashMap)elementNameTable.clone());
        attributeNameTable = (attributeNameTable == parent.attributeNameTable ? attributeNameTable : (HashMap)attributeNameTable.clone());
        defaultNS = (defaultNS == parent.defaultNS ? defaultNS : defaultNS);
      }
      else
      {
        prefixTable = ((HashMap)prefixTable.clone());
        uriTable = ((HashMap)uriTable.clone());
        elementNameTable = ((HashMap)elementNameTable.clone());
        attributeNameTable = ((HashMap)attributeNameTable.clone());
        defaultNS = defaultNS;
      }
      tablesDirty = tablesDirty;
      parent = paramContext2;
      declarations = (declarations == null ? null : (ArrayList)declarations.clone());
    }
    
    void setParent(Context paramContext)
    {
      parent = paramContext;
      declarations = null;
      prefixTable = prefixTable;
      uriTable = uriTable;
      elementNameTable = elementNameTable;
      attributeNameTable = attributeNameTable;
      defaultNS = defaultNS;
      tablesDirty = false;
    }
    
    void declarePrefix(String paramString1, String paramString2)
    {
      if (!tablesDirty) {
        copyTables();
      }
      if (declarations == null) {
        declarations = new ArrayList();
      }
      paramString1 = paramString1.intern();
      paramString2 = paramString2.intern();
      if ("".equals(paramString1))
      {
        if ("".equals(paramString2)) {
          defaultNS = null;
        } else {
          defaultNS = paramString2;
        }
      }
      else
      {
        prefixTable.put(paramString1, paramString2);
        uriTable.put(paramString2, paramString1);
      }
      declarations.add(paramString1);
    }
    
    String[] processName(String paramString, boolean paramBoolean)
    {
      HashMap localHashMap;
      if (paramBoolean) {
        localHashMap = elementNameTable;
      } else {
        localHashMap = attributeNameTable;
      }
      String[] arrayOfString = (String[])localHashMap.get(paramString);
      if (arrayOfString != null) {
        return arrayOfString;
      }
      arrayOfString = new String[3];
      int i = paramString.indexOf(':');
      if (i == -1)
      {
        if ((paramBoolean) || (defaultNS == null)) {
          arrayOfString[0] = "";
        } else {
          arrayOfString[0] = defaultNS;
        }
        arrayOfString[1] = paramString.intern();
        arrayOfString[2] = arrayOfString[1];
      }
      else
      {
        String str1 = paramString.substring(0, i);
        String str2 = paramString.substring(i + 1);
        String str3;
        if ("".equals(str1)) {
          str3 = defaultNS;
        } else {
          str3 = (String)prefixTable.get(str1);
        }
        if (str3 == null) {
          return null;
        }
        arrayOfString[0] = str3;
        arrayOfString[1] = str2.intern();
        arrayOfString[2] = paramString.intern();
      }
      localHashMap.put(arrayOfString[2], arrayOfString);
      tablesDirty = true;
      return arrayOfString;
    }
    
    String getURI(String paramString)
    {
      if ("".equals(paramString)) {
        return defaultNS;
      }
      if (prefixTable == null) {
        return null;
      }
      return (String)prefixTable.get(paramString);
    }
    
    String getPrefix(String paramString)
    {
      if (uriTable == null) {
        return null;
      }
      return (String)uriTable.get(paramString);
    }
    
    Iterable<String> getDeclaredPrefixes()
    {
      if (declarations == null) {
        return NamespaceSupport.EMPTY_ENUMERATION;
      }
      return declarations;
    }
    
    Iterable<String> getPrefixes()
    {
      if (prefixTable == null) {
        return NamespaceSupport.EMPTY_ENUMERATION;
      }
      return prefixTable.keySet();
    }
    
    private void copyTables()
    {
      if (prefixTable != null) {
        prefixTable = ((HashMap)prefixTable.clone());
      } else {
        prefixTable = new HashMap();
      }
      if (uriTable != null) {
        uriTable = ((HashMap)uriTable.clone());
      } else {
        uriTable = new HashMap();
      }
      elementNameTable = new HashMap();
      attributeNameTable = new HashMap();
      tablesDirty = true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\NamespaceSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */