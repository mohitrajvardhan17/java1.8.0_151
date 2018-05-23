package com.sun.xml.internal.fastinfoset.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;

public final class NamespaceContextImplementation
  implements NamespaceContext
{
  private static int DEFAULT_SIZE = 8;
  private String[] prefixes = new String[DEFAULT_SIZE];
  private String[] namespaceURIs = new String[DEFAULT_SIZE];
  private int namespacePosition;
  private int[] contexts = new int[DEFAULT_SIZE];
  private int contextPosition;
  private int currentContext;
  
  public NamespaceContextImplementation()
  {
    prefixes[0] = "xml";
    namespaceURIs[0] = "http://www.w3.org/XML/1998/namespace";
    prefixes[1] = "xmlns";
    namespaceURIs[1] = "http://www.w3.org/2000/xmlns/";
    currentContext = (namespacePosition = 2);
  }
  
  public String getNamespaceURI(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    for (int i = namespacePosition - 1; i >= 0; i--)
    {
      String str = prefixes[i];
      if (str.equals(paramString)) {
        return namespaceURIs[i];
      }
    }
    return "";
  }
  
  public String getPrefix(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    for (int i = namespacePosition - 1; i >= 0; i--)
    {
      String str1 = namespaceURIs[i];
      if (str1.equals(paramString))
      {
        String str2 = prefixes[i];
        int j = 0;
        for (int k = i + 1; k < namespacePosition; k++) {
          if (str2.equals(prefixes[k]))
          {
            j = 1;
            break;
          }
        }
        if (j == 0) {
          return str2;
        }
      }
    }
    return null;
  }
  
  public String getNonDefaultPrefix(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    for (int i = namespacePosition - 1; i >= 0; i--)
    {
      String str1 = namespaceURIs[i];
      if ((str1.equals(paramString)) && (prefixes[i].length() > 0))
      {
        String str2 = prefixes[i];
        i++;
        while (i < namespacePosition)
        {
          if (str2.equals(prefixes[i])) {
            return null;
          }
          i++;
        }
        return str2;
      }
    }
    return null;
  }
  
  public Iterator getPrefixes(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    ArrayList localArrayList = new ArrayList();
    label103:
    for (int i = namespacePosition - 1; i >= 0; i--)
    {
      String str1 = namespaceURIs[i];
      if (str1.equals(paramString))
      {
        String str2 = prefixes[i];
        for (int j = i + 1; j < namespacePosition; j++) {
          if (str2.equals(prefixes[j])) {
            break label103;
          }
        }
        localArrayList.add(str2);
      }
    }
    return localArrayList.iterator();
  }
  
  public String getPrefix(int paramInt)
  {
    return prefixes[paramInt];
  }
  
  public String getNamespaceURI(int paramInt)
  {
    return namespaceURIs[paramInt];
  }
  
  public int getCurrentContextStartIndex()
  {
    return currentContext;
  }
  
  public int getCurrentContextEndIndex()
  {
    return namespacePosition;
  }
  
  public boolean isCurrentContextEmpty()
  {
    return currentContext == namespacePosition;
  }
  
  public void declarePrefix(String paramString1, String paramString2)
  {
    paramString1 = paramString1.intern();
    paramString2 = paramString2.intern();
    if ((paramString1 == "xml") || (paramString1 == "xmlns")) {
      return;
    }
    for (int i = currentContext; i < namespacePosition; i++)
    {
      String str = prefixes[i];
      if (str == paramString1)
      {
        prefixes[i] = paramString1;
        namespaceURIs[i] = paramString2;
        return;
      }
    }
    if (namespacePosition == namespaceURIs.length) {
      resizeNamespaces();
    }
    prefixes[namespacePosition] = paramString1;
    namespaceURIs[(namespacePosition++)] = paramString2;
  }
  
  private void resizeNamespaces()
  {
    int i = namespaceURIs.length * 3 / 2 + 1;
    String[] arrayOfString1 = new String[i];
    System.arraycopy(prefixes, 0, arrayOfString1, 0, prefixes.length);
    prefixes = arrayOfString1;
    String[] arrayOfString2 = new String[i];
    System.arraycopy(namespaceURIs, 0, arrayOfString2, 0, namespaceURIs.length);
    namespaceURIs = arrayOfString2;
  }
  
  public void pushContext()
  {
    if (contextPosition == contexts.length) {
      resizeContexts();
    }
    contexts[(contextPosition++)] = (currentContext = namespacePosition);
  }
  
  private void resizeContexts()
  {
    int[] arrayOfInt = new int[contexts.length * 3 / 2 + 1];
    System.arraycopy(contexts, 0, arrayOfInt, 0, contexts.length);
    contexts = arrayOfInt;
  }
  
  public void popContext()
  {
    if (contextPosition > 0) {
      namespacePosition = (currentContext = contexts[(--contextPosition)]);
    }
  }
  
  public void reset()
  {
    currentContext = (namespacePosition = 2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\util\NamespaceContextImplementation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */