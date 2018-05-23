package com.sun.org.apache.xerces.internal.dom;

import java.io.Serializable;

class NodeListCache
  implements Serializable
{
  private static final long serialVersionUID = -7927529254918631002L;
  int fLength = -1;
  int fChildIndex = -1;
  ChildNode fChild;
  ParentNode fOwner;
  NodeListCache next;
  
  NodeListCache(ParentNode paramParentNode)
  {
    fOwner = paramParentNode;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\NodeListCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */