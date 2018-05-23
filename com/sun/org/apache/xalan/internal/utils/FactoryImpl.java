package com.sun.org.apache.xalan.internal.utils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

public class FactoryImpl
{
  static final String DBF = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";
  static final String SF = "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl";
  
  public FactoryImpl() {}
  
  public static DocumentBuilderFactory getDOMFactory(boolean paramBoolean)
  {
    DocumentBuilderFactory localDocumentBuilderFactory = paramBoolean ? DocumentBuilderFactory.newInstance() : DocumentBuilderFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl", FactoryImpl.class.getClassLoader());
    return localDocumentBuilderFactory;
  }
  
  public static SAXParserFactory getSAXFactory(boolean paramBoolean)
  {
    SAXParserFactory localSAXParserFactory = paramBoolean ? SAXParserFactory.newInstance() : SAXParserFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl", FactoryImpl.class.getClassLoader());
    return localSAXParserFactory;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\utils\FactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */