package com.sun.xml.internal.org.jvnet.fastinfoset.stax;

import javax.xml.stream.XMLStreamException;

public abstract interface FastInfosetStreamReader
{
  public abstract int peekNext()
    throws XMLStreamException;
  
  public abstract int accessNamespaceCount();
  
  public abstract String accessLocalName();
  
  public abstract String accessNamespaceURI();
  
  public abstract String accessPrefix();
  
  public abstract char[] accessTextCharacters();
  
  public abstract int accessTextStart();
  
  public abstract int accessTextLength();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\stax\FastInfosetStreamReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */