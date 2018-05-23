package com.sun.xml.internal.org.jvnet.staxex;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public abstract interface XMLStreamReaderEx
  extends XMLStreamReader
{
  public abstract CharSequence getPCDATA()
    throws XMLStreamException;
  
  public abstract NamespaceContextEx getNamespaceContext();
  
  public abstract String getElementTextTrim()
    throws XMLStreamException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\staxex\XMLStreamReaderEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */