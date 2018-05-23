package com.sun.org.apache.xml.internal.serializer.utils;

import org.w3c.dom.Node;

public final class DOM2Helper
{
  public DOM2Helper() {}
  
  public String getLocalNameOfNode(Node paramNode)
  {
    String str = paramNode.getLocalName();
    return null == str ? getLocalNameOfNodeFallback(paramNode) : str;
  }
  
  private String getLocalNameOfNodeFallback(Node paramNode)
  {
    String str = paramNode.getNodeName();
    int i = str.indexOf(':');
    return i < 0 ? str : str.substring(i + 1);
  }
  
  public String getNamespaceOfNode(Node paramNode)
  {
    return paramNode.getNamespaceURI();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\utils\DOM2Helper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */