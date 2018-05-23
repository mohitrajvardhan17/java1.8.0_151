package com.sun.org.apache.xalan.internal.xsltc.trax;

import java.util.Vector;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public abstract class SAX2StAXBaseWriter
  extends DefaultHandler
  implements LexicalHandler
{
  protected boolean isCDATA;
  protected StringBuffer CDATABuffer;
  protected Vector namespaces;
  protected Locator docLocator;
  protected XMLReporter reporter;
  
  public SAX2StAXBaseWriter() {}
  
  public SAX2StAXBaseWriter(XMLReporter paramXMLReporter)
  {
    reporter = paramXMLReporter;
  }
  
  public void setXMLReporter(XMLReporter paramXMLReporter)
  {
    reporter = paramXMLReporter;
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    docLocator = paramLocator;
  }
  
  public Location getCurrentLocation()
  {
    if (docLocator != null) {
      return new SAXLocation(docLocator, null);
    }
    return null;
  }
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {
    reportException("ERROR", paramSAXParseException);
  }
  
  public void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    reportException("FATAL", paramSAXParseException);
  }
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {
    reportException("WARNING", paramSAXParseException);
  }
  
  public void startDocument()
    throws SAXException
  {
    namespaces = new Vector(2);
  }
  
  public void endDocument()
    throws SAXException
  {
    namespaces = null;
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    namespaces = null;
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    namespaces = null;
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    if (paramString1 == null) {
      paramString1 = "";
    } else if (paramString1.equals("xml")) {
      return;
    }
    if (namespaces == null) {
      namespaces = new Vector(2);
    }
    namespaces.addElement(paramString1);
    namespaces.addElement(paramString2);
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {}
  
  public void startCDATA()
    throws SAXException
  {
    isCDATA = true;
    if (CDATABuffer == null) {
      CDATABuffer = new StringBuffer();
    } else {
      CDATABuffer.setLength(0);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (isCDATA) {
      CDATABuffer.append(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void endCDATA()
    throws SAXException
  {
    isCDATA = false;
    CDATABuffer.setLength(0);
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void endDTD()
    throws SAXException
  {}
  
  public void endEntity(String paramString)
    throws SAXException
  {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void startEntity(String paramString)
    throws SAXException
  {}
  
  protected void reportException(String paramString, SAXException paramSAXException)
    throws SAXException
  {
    if (reporter != null) {
      try
      {
        reporter.report(paramSAXException.getMessage(), paramString, paramSAXException, getCurrentLocation());
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new SAXException(localXMLStreamException);
      }
    }
  }
  
  public static final void parseQName(String paramString, String[] paramArrayOfString)
  {
    int i = paramString.indexOf(':');
    String str1;
    String str2;
    if (i >= 0)
    {
      str1 = paramString.substring(0, i);
      str2 = paramString.substring(i + 1);
    }
    else
    {
      str1 = "";
      str2 = paramString;
    }
    paramArrayOfString[0] = str1;
    paramArrayOfString[1] = str2;
  }
  
  private static final class SAXLocation
    implements Location
  {
    private int lineNumber;
    private int columnNumber;
    private String publicId;
    private String systemId;
    
    private SAXLocation(Locator paramLocator)
    {
      lineNumber = paramLocator.getLineNumber();
      columnNumber = paramLocator.getColumnNumber();
      publicId = paramLocator.getPublicId();
      systemId = paramLocator.getSystemId();
    }
    
    public int getLineNumber()
    {
      return lineNumber;
    }
    
    public int getColumnNumber()
    {
      return columnNumber;
    }
    
    public int getCharacterOffset()
    {
      return -1;
    }
    
    public String getPublicId()
    {
      return publicId;
    }
    
    public String getSystemId()
    {
      return systemId;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\SAX2StAXBaseWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */