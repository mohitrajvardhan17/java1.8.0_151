package com.sun.org.apache.xml.internal.serializer;

import javax.xml.transform.Transformer;
import org.w3c.dom.Node;

public abstract interface TransformStateSetter
{
  public abstract void setCurrentNode(Node paramNode);
  
  public abstract void resetState(Transformer paramTransformer);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\TransformStateSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */