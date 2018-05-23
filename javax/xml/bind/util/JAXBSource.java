package javax.xml.bind.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class JAXBSource
  extends SAXSource
{
  private final Marshaller marshaller;
  private final Object contentObject;
  private final XMLReader pseudoParser = new XMLReader()
  {
    private LexicalHandler lexicalHandler;
    private EntityResolver entityResolver;
    private DTDHandler dtdHandler;
    private XMLFilter repeater = new XMLFilterImpl();
    private ErrorHandler errorHandler;
    
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
        marshaller.marshal(contentObject, (XMLFilterImpl)repeater);
      }
      catch (JAXBException localJAXBException)
      {
        SAXParseException localSAXParseException = new SAXParseException(localJAXBException.getMessage(), null, null, -1, -1, localJAXBException);
        if (errorHandler != null) {
          errorHandler.fatalError(localSAXParseException);
        }
        throw localSAXParseException;
      }
    }
  };
  
  public JAXBSource(JAXBContext paramJAXBContext, Object paramObject)
    throws JAXBException
  {
    this(paramJAXBContext == null ? assertionFailed(Messages.format("JAXBSource.NullContext")) : paramJAXBContext.createMarshaller(), paramObject == null ? assertionFailed(Messages.format("JAXBSource.NullContent")) : paramObject);
  }
  
  public JAXBSource(Marshaller paramMarshaller, Object paramObject)
    throws JAXBException
  {
    if (paramMarshaller == null) {
      throw new JAXBException(Messages.format("JAXBSource.NullMarshaller"));
    }
    if (paramObject == null) {
      throw new JAXBException(Messages.format("JAXBSource.NullContent"));
    }
    marshaller = paramMarshaller;
    contentObject = paramObject;
    super.setXMLReader(pseudoParser);
    super.setInputSource(new InputSource());
  }
  
  private static Marshaller assertionFailed(String paramString)
    throws JAXBException
  {
    throw new JAXBException(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\util\JAXBSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */