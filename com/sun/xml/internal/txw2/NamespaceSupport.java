package com.sun.xml.internal.txw2;

import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

final class NamespaceSupport
{
  public static final String XMLNS = "http://www.w3.org/XML/1998/namespace";
  public static final String NSDECL = "http://www.w3.org/xmlns/2000/";
  private static final Enumeration EMPTY_ENUMERATION = new Vector().elements();
  private Context[] contexts;
  private Context currentContext;
  private int contextPos;
  private boolean namespaceDeclUris;
  
  public NamespaceSupport()
  {
    reset();
  }
  
  public void reset()
  {
    contexts = new Context[32];
    namespaceDeclUris = false;
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
      i *= 2;
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
    contexts[contextPos].clear();
    contextPos -= 1;
    if (contextPos < 0) {
      throw new EmptyStackException();
    }
    currentContext = contexts[contextPos];
  }
  
  public boolean declarePrefix(String paramString1, String paramString2)
  {
    if ((paramString1.equals("xml")) || (paramString1.equals("xmlns"))) {
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
  
  public Enumeration getPrefixes()
  {
    return currentContext.getPrefixes();
  }
  
  public String getPrefix(String paramString)
  {
    return currentContext.getPrefix(paramString);
  }
  
  public Enumeration getPrefixes(String paramString)
  {
    Vector localVector = new Vector();
    Enumeration localEnumeration = getPrefixes();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      if (paramString.equals(getURI(str))) {
        localVector.addElement(str);
      }
    }
    return localVector.elements();
  }
  
  public Enumeration getDeclaredPrefixes()
  {
    return currentContext.getDeclaredPrefixes();
  }
  
  public void setNamespaceDeclUris(boolean paramBoolean)
  {
    if (contextPos != 0) {
      throw new IllegalStateException();
    }
    if (paramBoolean == namespaceDeclUris) {
      return;
    }
    namespaceDeclUris = paramBoolean;
    if (paramBoolean)
    {
      currentContext.declarePrefix("xmlns", "http://www.w3.org/xmlns/2000/");
    }
    else
    {
      contexts[contextPos] = (currentContext = new Context());
      currentContext.declarePrefix("xml", "http://www.w3.org/XML/1998/namespace");
    }
  }
  
  public boolean isNamespaceDeclUris()
  {
    return namespaceDeclUris;
  }
  
  final class Context
  {
    Hashtable prefixTable;
    Hashtable uriTable;
    Hashtable elementNameTable;
    Hashtable attributeNameTable;
    String defaultNS = "";
    private Vector declarations = null;
    private boolean declSeen = false;
    private Context parent = null;
    
    Context()
    {
      copyTables();
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
      declSeen = false;
    }
    
    void clear()
    {
      parent = null;
      prefixTable = null;
      uriTable = null;
      elementNameTable = null;
      attributeNameTable = null;
      defaultNS = "";
    }
    
    void declarePrefix(String paramString1, String paramString2)
    {
      if (!declSeen) {
        copyTables();
      }
      if (declarations == null) {
        declarations = new Vector();
      }
      paramString1 = paramString1.intern();
      paramString2 = paramString2.intern();
      if ("".equals(paramString1))
      {
        defaultNS = paramString2;
      }
      else
      {
        prefixTable.put(paramString1, paramString2);
        uriTable.put(paramString2, paramString1);
      }
      declarations.addElement(paramString1);
    }
    
    String[] processName(String paramString, boolean paramBoolean)
    {
      Hashtable localHashtable;
      if (paramBoolean) {
        localHashtable = attributeNameTable;
      } else {
        localHashtable = elementNameTable;
      }
      String[] arrayOfString = (String[])localHashtable.get(paramString);
      if (arrayOfString != null) {
        return arrayOfString;
      }
      arrayOfString = new String[3];
      arrayOfString[2] = paramString.intern();
      int i = paramString.indexOf(':');
      if (i == -1)
      {
        if (paramBoolean)
        {
          if ((paramString == "xmlns") && (namespaceDeclUris)) {
            arrayOfString[0] = "http://www.w3.org/xmlns/2000/";
          } else {
            arrayOfString[0] = "";
          }
        }
        else {
          arrayOfString[0] = defaultNS;
        }
        arrayOfString[1] = arrayOfString[2];
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
        if ((str3 == null) || ((!paramBoolean) && ("xmlns".equals(str1)))) {
          return null;
        }
        arrayOfString[0] = str3;
        arrayOfString[1] = str2.intern();
      }
      localHashtable.put(arrayOfString[2], arrayOfString);
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
      if (uriTable != null)
      {
        String str1 = (String)uriTable.get(paramString);
        if (str1 == null) {
          return null;
        }
        String str2 = (String)prefixTable.get(str1);
        if (paramString.equals(str2)) {
          return str1;
        }
      }
      return null;
    }
    
    Enumeration getDeclaredPrefixes()
    {
      if (declarations == null) {
        return NamespaceSupport.EMPTY_ENUMERATION;
      }
      return declarations.elements();
    }
    
    Enumeration getPrefixes()
    {
      if (prefixTable == null) {
        return NamespaceSupport.EMPTY_ENUMERATION;
      }
      return prefixTable.keys();
    }
    
    private void copyTables()
    {
      if (prefixTable != null) {
        prefixTable = ((Hashtable)prefixTable.clone());
      } else {
        prefixTable = new Hashtable();
      }
      if (uriTable != null) {
        uriTable = ((Hashtable)uriTable.clone());
      } else {
        uriTable = new Hashtable();
      }
      elementNameTable = new Hashtable();
      attributeNameTable = new Hashtable();
      declSeen = true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\NamespaceSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */