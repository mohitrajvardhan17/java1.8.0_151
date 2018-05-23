package com.sun.org.apache.xml.internal.security.signature;

import org.w3c.dom.Node;

public abstract interface NodeFilter
{
  public abstract int isNodeInclude(Node paramNode);
  
  public abstract int isNodeIncludeDO(Node paramNode, int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\signature\NodeFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */