package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import org.w3c.dom.Node;

public abstract interface DOMSerializer
{
  public abstract void serialize(Node paramNode)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\DOMSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */