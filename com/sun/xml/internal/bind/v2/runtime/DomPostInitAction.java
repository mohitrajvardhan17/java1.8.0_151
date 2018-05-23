package com.sun.xml.internal.bind.v2.runtime;

import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

final class DomPostInitAction
  implements Runnable
{
  private final Node node;
  private final XMLSerializer serializer;
  
  DomPostInitAction(Node paramNode, XMLSerializer paramXMLSerializer)
  {
    node = paramNode;
    serializer = paramXMLSerializer;
  }
  
  public void run()
  {
    HashSet localHashSet = new HashSet();
    for (Node localNode = node; (localNode != null) && (localNode.getNodeType() == 1); localNode = localNode.getParentNode())
    {
      NamedNodeMap localNamedNodeMap = localNode.getAttributes();
      if (localNamedNodeMap != null) {
        for (int i = 0; i < localNamedNodeMap.getLength(); i++)
        {
          Attr localAttr = (Attr)localNamedNodeMap.item(i);
          String str1 = localAttr.getNamespaceURI();
          if ((str1 != null) && (str1.equals("http://www.w3.org/2000/xmlns/")))
          {
            String str2 = localAttr.getLocalName();
            if (str2 != null)
            {
              if (str2.equals("xmlns")) {
                str2 = "";
              }
              String str3 = localAttr.getValue();
              if ((str3 != null) && (localHashSet.add(str2))) {
                serializer.addInscopeBinding(str3, str2);
              }
            }
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\DomPostInitAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */