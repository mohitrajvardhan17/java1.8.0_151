package com.sun.org.apache.xml.internal.security.utils;

import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract interface XPathAPI
{
  public abstract NodeList selectNodeList(Node paramNode1, Node paramNode2, String paramString, Node paramNode3)
    throws TransformerException;
  
  public abstract boolean evaluate(Node paramNode1, Node paramNode2, String paramString, Node paramNode3)
    throws TransformerException;
  
  public abstract void clear();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\utils\XPathAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */