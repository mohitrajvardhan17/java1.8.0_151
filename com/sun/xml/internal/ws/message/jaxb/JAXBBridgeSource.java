package com.sun.xml.internal.ws.message.jaxb;

import com.sun.xml.internal.ws.spi.db.XMLBridge;
import javax.xml.bind.JAXBException;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

final class JAXBBridgeSource
  extends SAXSource
{
  private final XMLBridge bridge;
  private final Object contentObject;
  private final XMLReader pseudoParser = new XMLFilterImpl()
  {
    private LexicalHandler lexicalHandler;
    
    public boolean getFeature(String paramAnonymousString)
      throws SAXNotRecognizedException
    {
      if (paramAnonymousString.equals("http://xml.org/sax/features/namespaces")) {
        return true;
      }
      if (paramAnonymousString.equals("http://xml.org/sax/features/namespace-prefixes")) {
        return false;
      }
      throw new SAXNotRecognizedException(paramAnonymousString);
    }
    
    public void setFeature(String paramAnonymousString, boolean paramAnonymousBoolean)
      throws SAXNotRecognizedException
    {
      if ((paramAnonymousString.equals("http://xml.org/sax/features/namespaces")) && (paramAnonymousBoolean)) {
        return;
      }
      if ((paramAnonymousString.equals("http://xml.org/sax/features/namespace-prefixes")) && (!paramAnonymousBoolean)) {
        return;
      }
      throw new SAXNotRecognizedException(paramAnonymousString);
    }
    
    public Object getProperty(String paramAnonymousString)
      throws SAXNotRecognizedException
    {
      if ("http://xml.org/sax/properties/lexical-handler".equals(paramAnonymousString)) {
        return lexicalHandler;
      }
      throw new SAXNotRecognizedException(paramAnonymousString);
    }
    
    public void setProperty(String paramAnonymousString, Object paramAnonymousObject)
      throws SAXNotRecognizedException
    {
      if ("http://xml.org/sax/properties/lexical-handler".equals(paramAnonymousString))
      {
        lexicalHandler = ((LexicalHandler)paramAnonymousObject);
        return;
      }
      throw new SAXNotRecognizedException(paramAnonymousString);
    }
    
    public void parse(InputSource paramAnonymousInputSource)
      throws SAXException
    {
      parse();
    }
    
    public void parse(String paramAnonymousString)
      throws SAXException
    {
      parse();
    }
    
    public void parse()
      throws SAXException
    {
      try
      {
        startDocument();
        bridge.marshal(contentObject, this, null);
        endDocument();
      }
      catch (JAXBException localJAXBException)
      {
        SAXParseException localSAXParseException = new SAXParseException(localJAXBException.getMessage(), null, null, -1, -1, localJAXBException);
        fatalError(localSAXParseException);
        throw localSAXParseException;
      }
    }
  };
  
  public JAXBBridgeSource(XMLBridge paramXMLBridge, Object paramObject)
  {
    bridge = paramXMLBridge;
    contentObject = paramObject;
    super.setXMLReader(pseudoParser);
    super.setInputSource(new InputSource());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\jaxb\JAXBBridgeSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */