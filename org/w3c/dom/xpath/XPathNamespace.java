package org.w3c.dom.xpath;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract interface XPathNamespace
  extends Node
{
  public static final short XPATH_NAMESPACE_NODE = 13;
  
  public abstract Element getOwnerElement();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\xpath\XPathNamespace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */