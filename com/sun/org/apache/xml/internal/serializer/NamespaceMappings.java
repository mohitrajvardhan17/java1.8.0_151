package com.sun.org.apache.xml.internal.serializer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class NamespaceMappings
{
  private int count;
  private HashMap m_namespaces = new HashMap();
  private Stack m_nodeStack = new Stack();
  private static final String EMPTYSTRING = "";
  private static final String XML_PREFIX = "xml";
  
  public NamespaceMappings()
  {
    initNamespaces();
  }
  
  private void initNamespaces()
  {
    Stack localStack;
    m_namespaces.put("", localStack = new Stack());
    localStack.push(new MappingRecord("", "", 0));
    m_namespaces.put("xml", localStack = new Stack());
    localStack.push(new MappingRecord("xml", "http://www.w3.org/XML/1998/namespace", 0));
    m_nodeStack.push(new MappingRecord(null, null, -1));
  }
  
  public String lookupNamespace(String paramString)
  {
    Stack localStack = (Stack)m_namespaces.get(paramString);
    return (localStack != null) && (!localStack.isEmpty()) ? peekm_uri : null;
  }
  
  MappingRecord getMappingFromPrefix(String paramString)
  {
    Stack localStack = (Stack)m_namespaces.get(paramString);
    return (localStack != null) && (!localStack.isEmpty()) ? (MappingRecord)localStack.peek() : null;
  }
  
  public String lookupPrefix(String paramString)
  {
    Object localObject = null;
    Iterator localIterator = m_namespaces.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = lookupNamespace(str1);
      if ((str2 != null) && (str2.equals(paramString)))
      {
        localObject = str1;
        break;
      }
    }
    return (String)localObject;
  }
  
  MappingRecord getMappingFromURI(String paramString)
  {
    Object localObject = null;
    Iterator localIterator = m_namespaces.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      MappingRecord localMappingRecord = getMappingFromPrefix(str);
      if ((localMappingRecord != null) && (m_uri.equals(paramString)))
      {
        localObject = localMappingRecord;
        break;
      }
    }
    return (MappingRecord)localObject;
  }
  
  boolean popNamespace(String paramString)
  {
    if (paramString.startsWith("xml")) {
      return false;
    }
    Stack localStack;
    if ((localStack = (Stack)m_namespaces.get(paramString)) != null)
    {
      localStack.pop();
      return true;
    }
    return false;
  }
  
  boolean pushNamespace(String paramString1, String paramString2, int paramInt)
  {
    if (paramString1.startsWith("xml")) {
      return false;
    }
    Stack localStack;
    if ((localStack = (Stack)m_namespaces.get(paramString1)) == null) {
      m_namespaces.put(paramString1, localStack = new Stack());
    }
    if ((!localStack.empty()) && (paramString2.equals(peekm_uri))) {
      return false;
    }
    MappingRecord localMappingRecord = new MappingRecord(paramString1, paramString2, paramInt);
    localStack.push(localMappingRecord);
    m_nodeStack.push(localMappingRecord);
    return true;
  }
  
  void popNamespaces(int paramInt, ContentHandler paramContentHandler)
  {
    for (;;)
    {
      if (m_nodeStack.isEmpty()) {
        return;
      }
      MappingRecord localMappingRecord = (MappingRecord)m_nodeStack.peek();
      int i = m_declarationDepth;
      if (i < paramInt) {
        return;
      }
      localMappingRecord = (MappingRecord)m_nodeStack.pop();
      String str = m_prefix;
      popNamespace(str);
      if (paramContentHandler != null) {
        try
        {
          paramContentHandler.endPrefixMapping(str);
        }
        catch (SAXException localSAXException) {}
      }
    }
  }
  
  public String generateNextPrefix()
  {
    return "ns" + count++;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    NamespaceMappings localNamespaceMappings = new NamespaceMappings();
    m_nodeStack = ((Stack)m_nodeStack.clone());
    m_namespaces = ((HashMap)m_namespaces.clone());
    count = count;
    return localNamespaceMappings;
  }
  
  final void reset()
  {
    count = 0;
    m_namespaces.clear();
    m_nodeStack.clear();
    initNamespaces();
  }
  
  class MappingRecord
  {
    final String m_prefix;
    final String m_uri;
    final int m_declarationDepth;
    
    MappingRecord(String paramString1, String paramString2, int paramInt)
    {
      m_prefix = paramString1;
      m_uri = paramString2;
      m_declarationDepth = paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\NamespaceMappings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */