package com.sun.xml.internal.bind.v2.runtime;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;

final class StAXPostInitAction
  implements Runnable
{
  private final XMLStreamWriter xsw;
  private final XMLEventWriter xew;
  private final NamespaceContext nsc;
  private final XMLSerializer serializer;
  
  StAXPostInitAction(XMLStreamWriter paramXMLStreamWriter, XMLSerializer paramXMLSerializer)
  {
    xsw = paramXMLStreamWriter;
    xew = null;
    nsc = null;
    serializer = paramXMLSerializer;
  }
  
  StAXPostInitAction(XMLEventWriter paramXMLEventWriter, XMLSerializer paramXMLSerializer)
  {
    xsw = null;
    xew = paramXMLEventWriter;
    nsc = null;
    serializer = paramXMLSerializer;
  }
  
  StAXPostInitAction(NamespaceContext paramNamespaceContext, XMLSerializer paramXMLSerializer)
  {
    xsw = null;
    xew = null;
    nsc = paramNamespaceContext;
    serializer = paramXMLSerializer;
  }
  
  public void run()
  {
    NamespaceContext localNamespaceContext = nsc;
    if (xsw != null) {
      localNamespaceContext = xsw.getNamespaceContext();
    }
    if (xew != null) {
      localNamespaceContext = xew.getNamespaceContext();
    }
    if (localNamespaceContext == null) {
      return;
    }
    for (String str1 : serializer.grammar.nameList.namespaceURIs)
    {
      String str2 = localNamespaceContext.getPrefix(str1);
      if (str2 != null) {
        serializer.addInscopeBinding(str1, str2);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\StAXPostInitAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */