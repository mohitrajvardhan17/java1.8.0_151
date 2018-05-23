package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class JAXPPrefixResolver
  implements PrefixResolver
{
  private NamespaceContext namespaceContext;
  public static final String S_XMLNAMESPACEURI = "http://www.w3.org/XML/1998/namespace";
  
  public JAXPPrefixResolver(NamespaceContext paramNamespaceContext)
  {
    namespaceContext = paramNamespaceContext;
  }
  
  public String getNamespaceForPrefix(String paramString)
  {
    return namespaceContext.getNamespaceURI(paramString);
  }
  
  public String getBaseIdentifier()
  {
    return null;
  }
  
  public boolean handlesNullPrefixes()
  {
    return false;
  }
  
  public String getNamespaceForPrefix(String paramString, Node paramNode)
  {
    Node localNode1 = paramNode;
    String str1 = null;
    if (paramString.equals("xml"))
    {
      str1 = "http://www.w3.org/XML/1998/namespace";
    }
    else
    {
      int i;
      while ((null != localNode1) && (null == str1) && (((i = localNode1.getNodeType()) == 1) || (i == 5)))
      {
        if (i == 1)
        {
          NamedNodeMap localNamedNodeMap = localNode1.getAttributes();
          for (int j = 0; j < localNamedNodeMap.getLength(); j++)
          {
            Node localNode2 = localNamedNodeMap.item(j);
            String str2 = localNode2.getNodeName();
            boolean bool = str2.startsWith("xmlns:");
            if ((bool) || (str2.equals("xmlns")))
            {
              int k = str2.indexOf(':');
              String str3 = bool ? str2.substring(k + 1) : "";
              if (str3.equals(paramString))
              {
                str1 = localNode2.getNodeValue();
                break;
              }
            }
          }
        }
        localNode1 = localNode1.getParentNode();
      }
    }
    return str1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\jaxp\JAXPPrefixResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */