package com.sun.org.apache.xml.internal.security.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DOMNamespaceContext
  implements NamespaceContext
{
  private Map<String, String> namespaceMap = new HashMap();
  
  public DOMNamespaceContext(Node paramNode)
  {
    addNamespaces(paramNode);
  }
  
  public String getNamespaceURI(String paramString)
  {
    return (String)namespaceMap.get(paramString);
  }
  
  public String getPrefix(String paramString)
  {
    Iterator localIterator = namespaceMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = (String)namespaceMap.get(str1);
      if (str2.equals(paramString)) {
        return str1;
      }
    }
    return null;
  }
  
  public Iterator<String> getPrefixes(String paramString)
  {
    return namespaceMap.keySet().iterator();
  }
  
  private void addNamespaces(Node paramNode)
  {
    if (paramNode.getParentNode() != null) {
      addNamespaces(paramNode.getParentNode());
    }
    if ((paramNode instanceof Element))
    {
      Element localElement = (Element)paramNode;
      NamedNodeMap localNamedNodeMap = localElement.getAttributes();
      for (int i = 0; i < localNamedNodeMap.getLength(); i++)
      {
        Attr localAttr = (Attr)localNamedNodeMap.item(i);
        if ("xmlns".equals(localAttr.getPrefix())) {
          namespaceMap.put(localAttr.getLocalName(), localAttr.getValue());
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\DOMNamespaceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */