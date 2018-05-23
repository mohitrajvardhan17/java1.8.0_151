package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx.Binding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class NamespaceContexHelper
  implements NamespaceContextEx
{
  private static int DEFAULT_SIZE = 8;
  private String[] prefixes = new String[DEFAULT_SIZE];
  private String[] namespaceURIs = new String[DEFAULT_SIZE];
  private int namespacePosition;
  private int[] contexts = new int[DEFAULT_SIZE];
  private int contextPosition;
  
  public NamespaceContexHelper()
  {
    prefixes[0] = "xml";
    namespaceURIs[0] = "http://www.w3.org/XML/1998/namespace";
    prefixes[1] = "xmlns";
    namespaceURIs[1] = "http://www.w3.org/2000/xmlns/";
    namespacePosition = 2;
  }
  
  public String getNamespaceURI(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    paramString = paramString.intern();
    for (int i = namespacePosition - 1; i >= 0; i--)
    {
      String str = prefixes[i];
      if (str == paramString) {
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
      if ((str1 == paramString) || (str1.equals(paramString)))
      {
        String str2 = prefixes[i];
        i++;
        while (i < namespacePosition)
        {
          if (str2 == prefixes[i]) {
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
    label106:
    for (int i = namespacePosition - 1; i >= 0; i--)
    {
      String str1 = namespaceURIs[i];
      if ((str1 == paramString) || (str1.equals(paramString)))
      {
        String str2 = prefixes[i];
        for (int j = i + 1; j < namespacePosition; j++) {
          if (str2 == prefixes[j]) {
            break label106;
          }
        }
        localArrayList.add(str2);
      }
    }
    return localArrayList.iterator();
  }
  
  public Iterator<NamespaceContextEx.Binding> iterator()
  {
    if (namespacePosition == 2) {
      return Collections.EMPTY_LIST.iterator();
    }
    ArrayList localArrayList = new ArrayList(namespacePosition);
    for (int i = namespacePosition - 1; i >= 2; i--)
    {
      String str = prefixes[i];
      for (int j = i + 1; (j < namespacePosition) && (str != prefixes[j]); j++) {
        localArrayList.add(new NamespaceBindingImpl(i));
      }
    }
    return localArrayList.iterator();
  }
  
  public void declareDefaultNamespace(String paramString)
  {
    declareNamespace("", paramString);
  }
  
  public void declareNamespace(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException();
    }
    paramString1 = paramString1.intern();
    if ((paramString1 == "xml") || (paramString1 == "xmlns")) {
      return;
    }
    if (paramString2 != null) {
      paramString2 = paramString2.intern();
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
    contexts[(contextPosition++)] = namespacePosition;
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
      namespacePosition = contexts[(--contextPosition)];
    }
  }
  
  public void resetContexts()
  {
    namespacePosition = 2;
  }
  
  private final class NamespaceBindingImpl
    implements NamespaceContextEx.Binding
  {
    int index;
    
    NamespaceBindingImpl(int paramInt)
    {
      index = paramInt;
    }
    
    public String getPrefix()
    {
      return prefixes[index];
    }
    
    public String getNamespaceURI()
    {
      return namespaceURIs[index];
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\buffer\stax\NamespaceContexHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */