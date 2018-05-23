package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xml.internal.utils.XMLReaderManager;
import java.io.IOException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

public class TrAXFilter
  extends XMLFilterImpl
{
  private Templates _templates;
  private TransformerImpl _transformer;
  private TransformerHandlerImpl _transformerHandler;
  private boolean _useServicesMechanism = true;
  
  public TrAXFilter(Templates paramTemplates)
    throws TransformerConfigurationException
  {
    _templates = paramTemplates;
    _transformer = ((TransformerImpl)paramTemplates.newTransformer());
    _transformerHandler = new TransformerHandlerImpl(_transformer);
    _useServicesMechanism = _transformer.useServicesMechnism();
  }
  
  public Transformer getTransformer()
  {
    return _transformer;
  }
  
  private void createParent()
    throws SAXException
  {
    XMLReader localXMLReader = null;
    try
    {
      SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
      localSAXParserFactory.setNamespaceAware(true);
      if (_transformer.isSecureProcessing()) {
        try
        {
          localSAXParserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (SAXException localSAXException) {}
      }
      SAXParser localSAXParser = localSAXParserFactory.newSAXParser();
      localXMLReader = localSAXParser.getXMLReader();
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new SAXException(localParserConfigurationException);
    }
    catch (FactoryConfigurationError localFactoryConfigurationError)
    {
      throw new SAXException(localFactoryConfigurationError.toString());
    }
    if (localXMLReader == null) {
      localXMLReader = XMLReaderFactory.createXMLReader();
    }
    setParent(localXMLReader);
  }
  
  public void parse(InputSource paramInputSource)
    throws SAXException, IOException
  {
    XMLReader localXMLReader = null;
    try
    {
      if (getParent() == null) {
        try
        {
          localXMLReader = XMLReaderManager.getInstance(_useServicesMechanism).getXMLReader();
          setParent(localXMLReader);
        }
        catch (SAXException localSAXException)
        {
          throw new SAXException(localSAXException.toString());
        }
      }
      getParent().parse(paramInputSource);
    }
    finally
    {
      if (localXMLReader != null) {
        XMLReaderManager.getInstance(_useServicesMechanism).releaseXMLReader(localXMLReader);
      }
    }
  }
  
  public void parse(String paramString)
    throws SAXException, IOException
  {
    parse(new InputSource(paramString));
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
  {
    _transformerHandler.setResult(new SAXResult(paramContentHandler));
    if (getParent() == null) {
      try
      {
        createParent();
      }
      catch (SAXException localSAXException)
      {
        return;
      }
    }
    getParent().setContentHandler(_transformerHandler);
  }
  
  public void setErrorListener(ErrorListener paramErrorListener) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\TrAXFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */