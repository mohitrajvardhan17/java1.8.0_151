package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class SAXParserFactoryImpl
  extends SAXParserFactory
{
  private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
  private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
  private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
  private Map<String, Boolean> features;
  private Schema grammar;
  private boolean isXIncludeAware;
  private boolean fSecureProcess = true;
  
  public SAXParserFactoryImpl() {}
  
  public SAXParser newSAXParser()
    throws ParserConfigurationException
  {
    SAXParserImpl localSAXParserImpl;
    try
    {
      localSAXParserImpl = new SAXParserImpl(this, features, fSecureProcess);
    }
    catch (SAXException localSAXException)
    {
      throw new ParserConfigurationException(localSAXException.getMessage());
    }
    return localSAXParserImpl;
  }
  
  private SAXParserImpl newSAXParserImpl()
    throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException
  {
    SAXParserImpl localSAXParserImpl;
    try
    {
      localSAXParserImpl = new SAXParserImpl(this, features);
    }
    catch (SAXNotSupportedException localSAXNotSupportedException)
    {
      throw localSAXNotSupportedException;
    }
    catch (SAXNotRecognizedException localSAXNotRecognizedException)
    {
      throw localSAXNotRecognizedException;
    }
    catch (SAXException localSAXException)
    {
      throw new ParserConfigurationException(localSAXException.getMessage());
    }
    return localSAXParserImpl;
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing"))
    {
      if ((System.getSecurityManager() != null) && (!paramBoolean)) {
        throw new ParserConfigurationException(SAXMessageFormatter.formatMessage(null, "jaxp-secureprocessing-feature", null));
      }
      fSecureProcess = paramBoolean;
      putInFeatures(paramString, paramBoolean);
      return;
    }
    putInFeatures(paramString, paramBoolean);
    try
    {
      newSAXParserImpl();
    }
    catch (SAXNotSupportedException localSAXNotSupportedException)
    {
      features.remove(paramString);
      throw localSAXNotSupportedException;
    }
    catch (SAXNotRecognizedException localSAXNotRecognizedException)
    {
      features.remove(paramString);
      throw localSAXNotRecognizedException;
    }
  }
  
  public boolean getFeature(String paramString)
    throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
      return fSecureProcess;
    }
    return newSAXParserImpl().getXMLReader().getFeature(paramString);
  }
  
  public Schema getSchema()
  {
    return grammar;
  }
  
  public void setSchema(Schema paramSchema)
  {
    grammar = paramSchema;
  }
  
  public boolean isXIncludeAware()
  {
    return getFromFeatures("http://apache.org/xml/features/xinclude");
  }
  
  public void setXIncludeAware(boolean paramBoolean)
  {
    putInFeatures("http://apache.org/xml/features/xinclude", paramBoolean);
  }
  
  public void setValidating(boolean paramBoolean)
  {
    putInFeatures("http://xml.org/sax/features/validation", paramBoolean);
  }
  
  public boolean isValidating()
  {
    return getFromFeatures("http://xml.org/sax/features/validation");
  }
  
  private void putInFeatures(String paramString, boolean paramBoolean)
  {
    if (features == null) {
      features = new HashMap();
    }
    features.put(paramString, paramBoolean ? Boolean.TRUE : Boolean.FALSE);
  }
  
  private boolean getFromFeatures(String paramString)
  {
    if (features == null) {
      return false;
    }
    Boolean localBoolean = (Boolean)features.get(paramString);
    return localBoolean == null ? false : localBoolean.booleanValue();
  }
  
  public boolean isNamespaceAware()
  {
    return getFromFeatures("http://xml.org/sax/features/namespaces");
  }
  
  public void setNamespaceAware(boolean paramBoolean)
  {
    putInFeatures("http://xml.org/sax/features/namespaces", paramBoolean);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\SAXParserFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */