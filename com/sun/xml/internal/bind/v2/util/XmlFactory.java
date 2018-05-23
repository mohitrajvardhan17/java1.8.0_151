package com.sun.xml.internal.bind.v2.util;

import com.sun.xml.internal.bind.v2.Messages;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class XmlFactory
{
  public static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
  public static final String ACCESS_EXTERNAL_DTD = "http://javax.xml.XMLConstants/property/accessExternalDTD";
  private static final Logger LOGGER = Logger.getLogger(XmlFactory.class.getName());
  private static final String DISABLE_XML_SECURITY = "com.sun.xml.internal.bind.disableXmlSecurity";
  private static final boolean XML_SECURITY_DISABLED = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Boolean run()
    {
      return Boolean.valueOf(Boolean.getBoolean("com.sun.xml.internal.bind.disableXmlSecurity"));
    }
  })).booleanValue();
  
  public XmlFactory() {}
  
  private static boolean isXMLSecurityDisabled(boolean paramBoolean)
  {
    return (XML_SECURITY_DISABLED) || (paramBoolean);
  }
  
  public static SchemaFactory createSchemaFactory(String paramString, boolean paramBoolean)
    throws IllegalStateException
  {
    try
    {
      SchemaFactory localSchemaFactory = SchemaFactory.newInstance(paramString);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "SchemaFactory instance: {0}", localSchemaFactory);
      }
      localSchemaFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(paramBoolean));
      return localSchemaFactory;
    }
    catch (SAXNotRecognizedException localSAXNotRecognizedException)
    {
      LOGGER.log(Level.SEVERE, null, localSAXNotRecognizedException);
      throw new IllegalStateException(localSAXNotRecognizedException);
    }
    catch (SAXNotSupportedException localSAXNotSupportedException)
    {
      LOGGER.log(Level.SEVERE, null, localSAXNotSupportedException);
      throw new IllegalStateException(localSAXNotSupportedException);
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      LOGGER.log(Level.SEVERE, null, localAbstractMethodError);
      throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(new Object[0]), localAbstractMethodError);
    }
  }
  
  public static SAXParserFactory createParserFactory(boolean paramBoolean)
    throws IllegalStateException
  {
    try
    {
      SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "SAXParserFactory instance: {0}", localSAXParserFactory);
      }
      localSAXParserFactory.setNamespaceAware(true);
      localSAXParserFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(paramBoolean));
      return localSAXParserFactory;
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      LOGGER.log(Level.SEVERE, null, localParserConfigurationException);
      throw new IllegalStateException(localParserConfigurationException);
    }
    catch (SAXNotRecognizedException localSAXNotRecognizedException)
    {
      LOGGER.log(Level.SEVERE, null, localSAXNotRecognizedException);
      throw new IllegalStateException(localSAXNotRecognizedException);
    }
    catch (SAXNotSupportedException localSAXNotSupportedException)
    {
      LOGGER.log(Level.SEVERE, null, localSAXNotSupportedException);
      throw new IllegalStateException(localSAXNotSupportedException);
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      LOGGER.log(Level.SEVERE, null, localAbstractMethodError);
      throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(new Object[0]), localAbstractMethodError);
    }
  }
  
  public static XPathFactory createXPathFactory(boolean paramBoolean)
    throws IllegalStateException
  {
    try
    {
      XPathFactory localXPathFactory = XPathFactory.newInstance();
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "XPathFactory instance: {0}", localXPathFactory);
      }
      localXPathFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(paramBoolean));
      return localXPathFactory;
    }
    catch (XPathFactoryConfigurationException localXPathFactoryConfigurationException)
    {
      LOGGER.log(Level.SEVERE, null, localXPathFactoryConfigurationException);
      throw new IllegalStateException(localXPathFactoryConfigurationException);
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      LOGGER.log(Level.SEVERE, null, localAbstractMethodError);
      throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(new Object[0]), localAbstractMethodError);
    }
  }
  
  public static TransformerFactory createTransformerFactory(boolean paramBoolean)
    throws IllegalStateException
  {
    try
    {
      TransformerFactory localTransformerFactory = TransformerFactory.newInstance();
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "TransformerFactory instance: {0}", localTransformerFactory);
      }
      localTransformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(paramBoolean));
      return localTransformerFactory;
    }
    catch (TransformerConfigurationException localTransformerConfigurationException)
    {
      LOGGER.log(Level.SEVERE, null, localTransformerConfigurationException);
      throw new IllegalStateException(localTransformerConfigurationException);
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      LOGGER.log(Level.SEVERE, null, localAbstractMethodError);
      throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(new Object[0]), localAbstractMethodError);
    }
  }
  
  public static DocumentBuilderFactory createDocumentBuilderFactory(boolean paramBoolean)
    throws IllegalStateException
  {
    try
    {
      DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, "DocumentBuilderFactory instance: {0}", localDocumentBuilderFactory);
      }
      localDocumentBuilderFactory.setNamespaceAware(true);
      localDocumentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", !isXMLSecurityDisabled(paramBoolean));
      return localDocumentBuilderFactory;
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      LOGGER.log(Level.SEVERE, null, localParserConfigurationException);
      throw new IllegalStateException(localParserConfigurationException);
    }
    catch (AbstractMethodError localAbstractMethodError)
    {
      LOGGER.log(Level.SEVERE, null, localAbstractMethodError);
      throw new IllegalStateException(Messages.INVALID_JAXP_IMPLEMENTATION.format(new Object[0]), localAbstractMethodError);
    }
  }
  
  public static SchemaFactory allowExternalAccess(SchemaFactory paramSchemaFactory, String paramString, boolean paramBoolean)
  {
    if (isXMLSecurityDisabled(paramBoolean))
    {
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, Messages.JAXP_XML_SECURITY_DISABLED.format(new Object[0]));
      }
      return paramSchemaFactory;
    }
    if (System.getProperty("javax.xml.accessExternalSchema") != null)
    {
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, Messages.JAXP_EXTERNAL_ACCESS_CONFIGURED.format(new Object[0]));
      }
      return paramSchemaFactory;
    }
    try
    {
      paramSchemaFactory.setProperty("http://javax.xml.XMLConstants/property/accessExternalSchema", paramString);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, Messages.JAXP_SUPPORTED_PROPERTY.format(new Object[] { "http://javax.xml.XMLConstants/property/accessExternalSchema" }));
      }
    }
    catch (SAXException localSAXException)
    {
      if (LOGGER.isLoggable(Level.CONFIG)) {
        LOGGER.log(Level.CONFIG, Messages.JAXP_UNSUPPORTED_PROPERTY.format(new Object[] { "http://javax.xml.XMLConstants/property/accessExternalSchema" }), localSAXException);
      }
    }
    return paramSchemaFactory;
  }
  
  public static SchemaFactory allowExternalDTDAccess(SchemaFactory paramSchemaFactory, String paramString, boolean paramBoolean)
  {
    if (isXMLSecurityDisabled(paramBoolean))
    {
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, Messages.JAXP_XML_SECURITY_DISABLED.format(new Object[0]));
      }
      return paramSchemaFactory;
    }
    if (System.getProperty("javax.xml.accessExternalDTD") != null)
    {
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, Messages.JAXP_EXTERNAL_ACCESS_CONFIGURED.format(new Object[0]));
      }
      return paramSchemaFactory;
    }
    try
    {
      paramSchemaFactory.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", paramString);
      if (LOGGER.isLoggable(Level.FINE)) {
        LOGGER.log(Level.FINE, Messages.JAXP_SUPPORTED_PROPERTY.format(new Object[] { "http://javax.xml.XMLConstants/property/accessExternalDTD" }));
      }
    }
    catch (SAXException localSAXException)
    {
      if (LOGGER.isLoggable(Level.CONFIG)) {
        LOGGER.log(Level.CONFIG, Messages.JAXP_UNSUPPORTED_PROPERTY.format(new Object[] { "http://javax.xml.XMLConstants/property/accessExternalDTD" }), localSAXException);
      }
    }
    return paramSchemaFactory;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\util\XmlFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */