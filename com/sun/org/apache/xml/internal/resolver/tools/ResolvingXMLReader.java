package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ResolvingXMLReader
  extends ResolvingXMLFilter
{
  public static boolean namespaceAware = true;
  public static boolean validating = false;
  
  public ResolvingXMLReader()
  {
    SAXParserFactoryImpl localSAXParserFactoryImpl = catalogManager.useServicesMechanism() ? SAXParserFactory.newInstance() : new SAXParserFactoryImpl();
    localSAXParserFactoryImpl.setNamespaceAware(namespaceAware);
    localSAXParserFactoryImpl.setValidating(validating);
    try
    {
      SAXParser localSAXParser = localSAXParserFactoryImpl.newSAXParser();
      setParent(localSAXParser.getXMLReader());
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public ResolvingXMLReader(CatalogManager paramCatalogManager)
  {
    super(paramCatalogManager);
    SAXParserFactoryImpl localSAXParserFactoryImpl = catalogManager.useServicesMechanism() ? SAXParserFactory.newInstance() : new SAXParserFactoryImpl();
    localSAXParserFactoryImpl.setNamespaceAware(namespaceAware);
    localSAXParserFactoryImpl.setValidating(validating);
    try
    {
      SAXParser localSAXParser = localSAXParserFactoryImpl.newSAXParser();
      setParent(localSAXParser.getXMLReader());
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\resolver\tools\ResolvingXMLReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */