package jdk.internal.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.XMLReader;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;

public abstract class SAXParser
{
  protected SAXParser() {}
  
  public void parse(InputStream paramInputStream, DefaultHandler paramDefaultHandler)
    throws SAXException, IOException
  {
    if (paramInputStream == null) {
      throw new IllegalArgumentException("InputStream cannot be null");
    }
    InputSource localInputSource = new InputSource(paramInputStream);
    parse(localInputSource, paramDefaultHandler);
  }
  
  public void parse(String paramString, DefaultHandler paramDefaultHandler)
    throws SAXException, IOException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("uri cannot be null");
    }
    InputSource localInputSource = new InputSource(paramString);
    parse(localInputSource, paramDefaultHandler);
  }
  
  public void parse(File paramFile, DefaultHandler paramDefaultHandler)
    throws SAXException, IOException
  {
    if (paramFile == null) {
      throw new IllegalArgumentException("File cannot be null");
    }
    InputSource localInputSource = new InputSource(paramFile.toURI().toASCIIString());
    parse(localInputSource, paramDefaultHandler);
  }
  
  public void parse(InputSource paramInputSource, DefaultHandler paramDefaultHandler)
    throws SAXException, IOException
  {
    if (paramInputSource == null) {
      throw new IllegalArgumentException("InputSource cannot be null");
    }
    XMLReader localXMLReader = getXMLReader();
    if (paramDefaultHandler != null)
    {
      localXMLReader.setContentHandler(paramDefaultHandler);
      localXMLReader.setEntityResolver(paramDefaultHandler);
      localXMLReader.setErrorHandler(paramDefaultHandler);
      localXMLReader.setDTDHandler(paramDefaultHandler);
    }
    localXMLReader.parse(paramInputSource);
  }
  
  public abstract XMLReader getXMLReader()
    throws SAXException;
  
  public abstract boolean isNamespaceAware();
  
  public abstract boolean isValidating();
  
  public boolean isXIncludeAware()
  {
    throw new UnsupportedOperationException("This parser does not support specification \"" + getClass().getPackage().getSpecificationTitle() + "\" version \"" + getClass().getPackage().getSpecificationVersion() + "\"");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\util\xml\SAXParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */