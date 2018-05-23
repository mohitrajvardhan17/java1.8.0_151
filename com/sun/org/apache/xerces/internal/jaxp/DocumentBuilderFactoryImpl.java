package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class DocumentBuilderFactoryImpl
  extends DocumentBuilderFactory
{
  private Map<String, Object> attributes;
  private Map<String, Boolean> features;
  private Schema grammar;
  private boolean isXIncludeAware;
  private boolean fSecureProcess = true;
  
  public DocumentBuilderFactoryImpl() {}
  
  public DocumentBuilder newDocumentBuilder()
    throws ParserConfigurationException
  {
    if ((grammar != null) && (attributes != null))
    {
      if (attributes.containsKey("http://java.sun.com/xml/jaxp/properties/schemaLanguage")) {
        throw new ParserConfigurationException(SAXMessageFormatter.formatMessage(null, "schema-already-specified", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaLanguage" }));
      }
      if (attributes.containsKey("http://java.sun.com/xml/jaxp/properties/schemaSource")) {
        throw new ParserConfigurationException(SAXMessageFormatter.formatMessage(null, "schema-already-specified", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaSource" }));
      }
    }
    try
    {
      return new DocumentBuilderImpl(this, attributes, features, fSecureProcess);
    }
    catch (SAXException localSAXException)
    {
      throw new ParserConfigurationException(localSAXException.getMessage());
    }
  }
  
  public void setAttribute(String paramString, Object paramObject)
    throws IllegalArgumentException
  {
    if (paramObject == null)
    {
      if (attributes != null) {
        attributes.remove(paramString);
      }
      return;
    }
    if (attributes == null) {
      attributes = new HashMap();
    }
    attributes.put(paramString, paramObject);
    try
    {
      new DocumentBuilderImpl(this, attributes, features);
    }
    catch (Exception localException)
    {
      attributes.remove(paramString);
      throw new IllegalArgumentException(localException.getMessage());
    }
  }
  
  public Object getAttribute(String paramString)
    throws IllegalArgumentException
  {
    if (attributes != null)
    {
      localObject = attributes.get(paramString);
      if (localObject != null) {
        return localObject;
      }
    }
    Object localObject = null;
    try
    {
      localObject = new DocumentBuilderImpl(this, attributes, features).getDOMParser();
      return ((DOMParser)localObject).getProperty(paramString);
    }
    catch (SAXException localSAXException1)
    {
      try
      {
        boolean bool = ((DOMParser)localObject).getFeature(paramString);
        return bool ? Boolean.TRUE : Boolean.FALSE;
      }
      catch (SAXException localSAXException2)
      {
        throw new IllegalArgumentException(localSAXException1.getMessage());
      }
    }
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
    return isXIncludeAware;
  }
  
  public void setXIncludeAware(boolean paramBoolean)
  {
    isXIncludeAware = paramBoolean;
  }
  
  public boolean getFeature(String paramString)
    throws ParserConfigurationException
  {
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
      return fSecureProcess;
    }
    Object localObject;
    if (features != null)
    {
      localObject = (Boolean)features.get(paramString);
      if (localObject != null) {
        return ((Boolean)localObject).booleanValue();
      }
    }
    try
    {
      localObject = new DocumentBuilderImpl(this, attributes, features).getDOMParser();
      return ((DOMParser)localObject).getFeature(paramString);
    }
    catch (SAXException localSAXException)
    {
      throw new ParserConfigurationException(localSAXException.getMessage());
    }
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws ParserConfigurationException
  {
    if (features == null) {
      features = new HashMap();
    }
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing"))
    {
      if ((System.getSecurityManager() != null) && (!paramBoolean)) {
        throw new ParserConfigurationException(SAXMessageFormatter.formatMessage(null, "jaxp-secureprocessing-feature", null));
      }
      fSecureProcess = paramBoolean;
      features.put(paramString, paramBoolean ? Boolean.TRUE : Boolean.FALSE);
      return;
    }
    features.put(paramString, paramBoolean ? Boolean.TRUE : Boolean.FALSE);
    try
    {
      new DocumentBuilderImpl(this, attributes, features);
    }
    catch (SAXNotSupportedException localSAXNotSupportedException)
    {
      features.remove(paramString);
      throw new ParserConfigurationException(localSAXNotSupportedException.getMessage());
    }
    catch (SAXNotRecognizedException localSAXNotRecognizedException)
    {
      features.remove(paramString);
      throw new ParserConfigurationException(localSAXNotRecognizedException.getMessage());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\DocumentBuilderFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */