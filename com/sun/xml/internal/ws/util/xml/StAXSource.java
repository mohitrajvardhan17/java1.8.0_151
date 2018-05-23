package com.sun.xml.internal.ws.util.xml;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.SAXParseException2;
import com.sun.istack.internal.XMLStreamReaderToContentHandler;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class StAXSource
  extends SAXSource
{
  private final XMLStreamReaderToContentHandler reader;
  private final XMLStreamReader staxReader;
  private final XMLFilterImpl repeater = new XMLFilterImpl();
  private final XMLReader pseudoParser = new XMLReader()
  {
    private LexicalHandler lexicalHandler;
    private EntityResolver entityResolver;
    private DTDHandler dtdHandler;
    private ErrorHandler errorHandler;
    
    public boolean getFeature(String paramAnonymousString)
      throws SAXNotRecognizedException
    {
      throw new SAXNotRecognizedException(paramAnonymousString);
    }
    
    public void setFeature(String paramAnonymousString, boolean paramAnonymousBoolean)
      throws SAXNotRecognizedException
    {
      if (((!paramAnonymousString.equals("http://xml.org/sax/features/namespaces")) || (!paramAnonymousBoolean)) && ((!paramAnonymousString.equals("http://xml.org/sax/features/namespace-prefixes")) || (paramAnonymousBoolean))) {
        throw new SAXNotRecognizedException(paramAnonymousString);
      }
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
    
    public void setEntityResolver(EntityResolver paramAnonymousEntityResolver)
    {
      entityResolver = paramAnonymousEntityResolver;
    }
    
    public EntityResolver getEntityResolver()
    {
      return entityResolver;
    }
    
    public void setDTDHandler(DTDHandler paramAnonymousDTDHandler)
    {
      dtdHandler = paramAnonymousDTDHandler;
    }
    
    public DTDHandler getDTDHandler()
    {
      return dtdHandler;
    }
    
    public void setContentHandler(ContentHandler paramAnonymousContentHandler)
    {
      repeater.setContentHandler(paramAnonymousContentHandler);
    }
    
    public ContentHandler getContentHandler()
    {
      return repeater.getContentHandler();
    }
    
    public void setErrorHandler(ErrorHandler paramAnonymousErrorHandler)
    {
      errorHandler = paramAnonymousErrorHandler;
    }
    
    public ErrorHandler getErrorHandler()
    {
      return errorHandler;
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
        reader.bridge();
        SAXParseException2 localSAXParseException2;
        return;
      }
      catch (XMLStreamException localXMLStreamException2)
      {
        localSAXParseException2 = new SAXParseException2(localXMLStreamException2.getMessage(), null, null, localXMLStreamException2.getLocation() == null ? -1 : localXMLStreamException2.getLocation().getLineNumber(), localXMLStreamException2.getLocation() == null ? -1 : localXMLStreamException2.getLocation().getColumnNumber(), localXMLStreamException2);
        if (errorHandler != null) {
          errorHandler.fatalError(localSAXParseException2);
        }
        throw localSAXParseException2;
      }
      finally
      {
        try
        {
          staxReader.close();
        }
        catch (XMLStreamException localXMLStreamException3) {}
      }
    }
  };
  
  public StAXSource(XMLStreamReader paramXMLStreamReader, boolean paramBoolean)
  {
    this(paramXMLStreamReader, paramBoolean, new String[0]);
  }
  
  public StAXSource(XMLStreamReader paramXMLStreamReader, boolean paramBoolean, @NotNull String[] paramArrayOfString)
  {
    if (paramXMLStreamReader == null) {
      throw new IllegalArgumentException();
    }
    staxReader = paramXMLStreamReader;
    int i = paramXMLStreamReader.getEventType();
    if ((i != 7) && (i != 1)) {
      throw new IllegalStateException();
    }
    reader = new XMLStreamReaderToContentHandler(paramXMLStreamReader, repeater, paramBoolean, false, paramArrayOfString);
    super.setXMLReader(pseudoParser);
    super.setInputSource(new InputSource());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\xml\StAXSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */