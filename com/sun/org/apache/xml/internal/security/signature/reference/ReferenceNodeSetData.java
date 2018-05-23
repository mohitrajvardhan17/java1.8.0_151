package com.sun.org.apache.xml.internal.security.signature.reference;

import java.util.Iterator;
import org.w3c.dom.Node;

public abstract interface ReferenceNodeSetData
  extends ReferenceData
{
  public abstract Iterator<Node> iterator();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\signature\reference\ReferenceNodeSetData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */