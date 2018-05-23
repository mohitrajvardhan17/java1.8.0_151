package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.XMLReader;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;
import jdk.internal.util.xml.SAXParser;

public class SAXParserImpl
  extends SAXParser
{
  private ParserSAX parser = new ParserSAX();
  
  public SAXParserImpl() {}
  
  public XMLReader getXMLReader()
    throws SAXException
  {
    return parser;
  }
  
  public boolean isNamespaceAware()
  {
    return parser.mIsNSAware;
  }
  
  public boolean isValidating()
  {
    return false;
  }
  
  public void parse(InputStream paramInputStream, DefaultHandler paramDefaultHandler)
    throws SAXException, IOException
  {
    parser.parse(paramInputStream, paramDefaultHandler);
  }
  
  public void parse(InputSource paramInputSource, DefaultHandler paramDefaultHandler)
    throws SAXException, IOException
  {
    parser.parse(paramInputSource, paramDefaultHandler);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\util\xml\impl\SAXParserImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */