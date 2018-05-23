package com.sun.org.apache.xml.internal.serialize;

import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

public abstract interface DOMSerializer
{
  public abstract void serialize(Element paramElement)
    throws IOException;
  
  public abstract void serialize(Document paramDocument)
    throws IOException;
  
  public abstract void serialize(DocumentFragment paramDocumentFragment)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serialize\DOMSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */