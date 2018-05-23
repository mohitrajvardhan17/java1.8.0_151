package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.State;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager.Property;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager.State;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.validation.Schema;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParserImpl
  extends javax.xml.parsers.SAXParser
  implements JAXPConstants, PSVIProvider
{
  private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
  private static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
  private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
  private static final String XMLSCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
  private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  private final JAXPSAXParser xmlReader;
  private String schemaLanguage = null;
  private final Schema grammar;
  private final XMLComponent fSchemaValidator;
  private final XMLComponentManager fSchemaValidatorComponentManager;
  private final ValidationManager fSchemaValidationManager;
  private final UnparsedEntityHandler fUnparsedEntityHandler;
  private final ErrorHandler fInitErrorHandler;
  private final EntityResolver fInitEntityResolver;
  private final XMLSecurityManager fSecurityManager;
  private final XMLSecurityPropertyManager fSecurityPropertyMgr;
  
  SAXParserImpl(SAXParserFactoryImpl paramSAXParserFactoryImpl, Map<String, Boolean> paramMap)
    throws SAXException
  {
    this(paramSAXParserFactoryImpl, paramMap, false);
  }
  
  SAXParserImpl(SAXParserFactoryImpl paramSAXParserFactoryImpl, Map<String, Boolean> paramMap, boolean paramBoolean)
    throws SAXException
  {
    fSecurityManager = new XMLSecurityManager(paramBoolean);
    fSecurityPropertyMgr = new XMLSecurityPropertyManager();
    xmlReader = new JAXPSAXParser(this, fSecurityPropertyMgr, fSecurityManager);
    xmlReader.setFeature0("http://xml.org/sax/features/namespaces", paramSAXParserFactoryImpl.isNamespaceAware());
    xmlReader.setFeature0("http://xml.org/sax/features/namespace-prefixes", !paramSAXParserFactoryImpl.isNamespaceAware());
    if (paramSAXParserFactoryImpl.isXIncludeAware()) {
      xmlReader.setFeature0("http://apache.org/xml/features/xinclude", true);
    }
    xmlReader.setProperty0("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", fSecurityPropertyMgr);
    xmlReader.setProperty0("http://apache.org/xml/properties/security-manager", fSecurityManager);
    Object localObject1;
    if ((paramBoolean) && (paramMap != null))
    {
      localObject1 = (Boolean)paramMap.get("http://javax.xml.XMLConstants/feature/secure-processing");
      if ((localObject1 != null) && (((Boolean)localObject1).booleanValue()) && (Constants.IS_JDK8_OR_ABOVE))
      {
        fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, XMLSecurityPropertyManager.State.FSP, "");
        fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, XMLSecurityPropertyManager.State.FSP, "");
      }
    }
    setFeatures(paramMap);
    if (paramSAXParserFactoryImpl.isValidating())
    {
      fInitErrorHandler = new DefaultValidationErrorHandler(xmlReader.getLocale());
      xmlReader.setErrorHandler(fInitErrorHandler);
    }
    else
    {
      fInitErrorHandler = xmlReader.getErrorHandler();
    }
    xmlReader.setFeature0("http://xml.org/sax/features/validation", paramSAXParserFactoryImpl.isValidating());
    grammar = paramSAXParserFactoryImpl.getSchema();
    if (grammar != null)
    {
      localObject1 = xmlReader.getXMLParserConfiguration();
      Object localObject2 = null;
      if ((grammar instanceof XSGrammarPoolContainer))
      {
        localObject2 = new XMLSchemaValidator();
        fSchemaValidationManager = new ValidationManager();
        fUnparsedEntityHandler = new UnparsedEntityHandler(fSchemaValidationManager);
        ((XMLParserConfiguration)localObject1).setDTDHandler(fUnparsedEntityHandler);
        fUnparsedEntityHandler.setDTDHandler(xmlReader);
        xmlReader.setDTDSource(fUnparsedEntityHandler);
        fSchemaValidatorComponentManager = new SchemaValidatorConfiguration((XMLComponentManager)localObject1, (XSGrammarPoolContainer)grammar, fSchemaValidationManager);
      }
      else
      {
        localObject2 = new JAXPValidatorComponent(grammar.newValidatorHandler());
        fSchemaValidationManager = null;
        fUnparsedEntityHandler = null;
        fSchemaValidatorComponentManager = ((XMLComponentManager)localObject1);
      }
      ((XMLParserConfiguration)localObject1).addRecognizedFeatures(((XMLComponent)localObject2).getRecognizedFeatures());
      ((XMLParserConfiguration)localObject1).addRecognizedProperties(((XMLComponent)localObject2).getRecognizedProperties());
      ((XMLParserConfiguration)localObject1).setDocumentHandler((XMLDocumentHandler)localObject2);
      ((XMLDocumentSource)localObject2).setDocumentHandler(xmlReader);
      xmlReader.setDocumentSource((XMLDocumentSource)localObject2);
      fSchemaValidator = ((XMLComponent)localObject2);
    }
    else
    {
      fSchemaValidationManager = null;
      fUnparsedEntityHandler = null;
      fSchemaValidatorComponentManager = null;
      fSchemaValidator = null;
    }
    fInitEntityResolver = xmlReader.getEntityResolver();
  }
  
  private void setFeatures(Map<String, Boolean> paramMap)
    throws SAXNotSupportedException, SAXNotRecognizedException
  {
    if (paramMap != null)
    {
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        xmlReader.setFeature0((String)localEntry.getKey(), ((Boolean)localEntry.getValue()).booleanValue());
      }
    }
  }
  
  public Parser getParser()
    throws SAXException
  {
    return xmlReader;
  }
  
  public XMLReader getXMLReader()
  {
    return xmlReader;
  }
  
  public boolean isNamespaceAware()
  {
    try
    {
      return xmlReader.getFeature("http://xml.org/sax/features/namespaces");
    }
    catch (SAXException localSAXException)
    {
      throw new IllegalStateException(localSAXException.getMessage());
    }
  }
  
  public boolean isValidating()
  {
    try
    {
      return xmlReader.getFeature("http://xml.org/sax/features/validation");
    }
    catch (SAXException localSAXException)
    {
      throw new IllegalStateException(localSAXException.getMessage());
    }
  }
  
  public boolean isXIncludeAware()
  {
    try
    {
      return xmlReader.getFeature("http://apache.org/xml/features/xinclude");
    }
    catch (SAXException localSAXException) {}
    return false;
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    xmlReader.setProperty(paramString, paramObject);
  }
  
  public Object getProperty(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    return xmlReader.getProperty(paramString);
  }
  
  public void parse(InputSource paramInputSource, DefaultHandler paramDefaultHandler)
    throws SAXException, IOException
  {
    if (paramInputSource == null) {
      throw new IllegalArgumentException();
    }
    if (paramDefaultHandler != null)
    {
      xmlReader.setContentHandler(paramDefaultHandler);
      xmlReader.setEntityResolver(paramDefaultHandler);
      xmlReader.setErrorHandler(paramDefaultHandler);
      xmlReader.setDTDHandler(paramDefaultHandler);
      xmlReader.setDocumentHandler(null);
    }
    xmlReader.parse(paramInputSource);
  }
  
  public void parse(InputSource paramInputSource, HandlerBase paramHandlerBase)
    throws SAXException, IOException
  {
    if (paramInputSource == null) {
      throw new IllegalArgumentException();
    }
    if (paramHandlerBase != null)
    {
      xmlReader.setDocumentHandler(paramHandlerBase);
      xmlReader.setEntityResolver(paramHandlerBase);
      xmlReader.setErrorHandler(paramHandlerBase);
      xmlReader.setDTDHandler(paramHandlerBase);
      xmlReader.setContentHandler(null);
    }
    xmlReader.parse(paramInputSource);
  }
  
  public Schema getSchema()
  {
    return grammar;
  }
  
  public void reset()
  {
    try
    {
      xmlReader.restoreInitState();
    }
    catch (SAXException localSAXException) {}
    xmlReader.setContentHandler(null);
    xmlReader.setDTDHandler(null);
    if (xmlReader.getErrorHandler() != fInitErrorHandler) {
      xmlReader.setErrorHandler(fInitErrorHandler);
    }
    if (xmlReader.getEntityResolver() != fInitEntityResolver) {
      xmlReader.setEntityResolver(fInitEntityResolver);
    }
  }
  
  public ElementPSVI getElementPSVI()
  {
    return xmlReader.getElementPSVI();
  }
  
  public AttributePSVI getAttributePSVI(int paramInt)
  {
    return xmlReader.getAttributePSVI(paramInt);
  }
  
  public AttributePSVI getAttributePSVIByName(String paramString1, String paramString2)
  {
    return xmlReader.getAttributePSVIByName(paramString1, paramString2);
  }
  
  public static class JAXPSAXParser
    extends com.sun.org.apache.xerces.internal.parsers.SAXParser
  {
    private final HashMap fInitFeatures = new HashMap();
    private final HashMap fInitProperties = new HashMap();
    private final SAXParserImpl fSAXParser;
    private XMLSecurityManager fSecurityManager;
    private XMLSecurityPropertyManager fSecurityPropertyMgr;
    
    public JAXPSAXParser()
    {
      this(null, null, null);
    }
    
    JAXPSAXParser(SAXParserImpl paramSAXParserImpl, XMLSecurityPropertyManager paramXMLSecurityPropertyManager, XMLSecurityManager paramXMLSecurityManager)
    {
      fSAXParser = paramSAXParserImpl;
      fSecurityManager = paramXMLSecurityManager;
      fSecurityPropertyMgr = paramXMLSecurityPropertyManager;
      if (fSecurityManager == null)
      {
        fSecurityManager = new XMLSecurityManager(true);
        try
        {
          super.setProperty("http://apache.org/xml/properties/security-manager", fSecurityManager);
        }
        catch (SAXException localSAXException1)
        {
          throw new UnsupportedOperationException(SAXMessageFormatter.formatMessage(fConfiguration.getLocale(), "property-not-recognized", new Object[] { "http://apache.org/xml/properties/security-manager" }), localSAXException1);
        }
      }
      if (fSecurityPropertyMgr == null)
      {
        fSecurityPropertyMgr = new XMLSecurityPropertyManager();
        try
        {
          super.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", fSecurityPropertyMgr);
        }
        catch (SAXException localSAXException2)
        {
          throw new UnsupportedOperationException(SAXMessageFormatter.formatMessage(fConfiguration.getLocale(), "property-not-recognized", new Object[] { "http://apache.org/xml/properties/security-manager" }), localSAXException2);
        }
      }
    }
    
    public synchronized void setFeature(String paramString, boolean paramBoolean)
      throws SAXNotRecognizedException, SAXNotSupportedException
    {
      if (paramString == null) {
        throw new NullPointerException();
      }
      if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing"))
      {
        try
        {
          fSecurityManager.setSecureProcessing(paramBoolean);
          setProperty("http://apache.org/xml/properties/security-manager", fSecurityManager);
        }
        catch (SAXNotRecognizedException localSAXNotRecognizedException)
        {
          if (paramBoolean) {
            throw localSAXNotRecognizedException;
          }
        }
        catch (SAXNotSupportedException localSAXNotSupportedException)
        {
          if (paramBoolean) {
            throw localSAXNotSupportedException;
          }
        }
        return;
      }
      if (!fInitFeatures.containsKey(paramString))
      {
        boolean bool = super.getFeature(paramString);
        fInitFeatures.put(paramString, bool ? Boolean.TRUE : Boolean.FALSE);
      }
      if ((fSAXParser != null) && (fSAXParser.fSchemaValidator != null)) {
        setSchemaValidatorFeature(paramString, paramBoolean);
      }
      super.setFeature(paramString, paramBoolean);
    }
    
    public synchronized boolean getFeature(String paramString)
      throws SAXNotRecognizedException, SAXNotSupportedException
    {
      if (paramString == null) {
        throw new NullPointerException();
      }
      if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
        return fSecurityManager.isSecureProcessing();
      }
      return super.getFeature(paramString);
    }
    
    public synchronized void setProperty(String paramString, Object paramObject)
      throws SAXNotRecognizedException, SAXNotSupportedException
    {
      if (paramString == null) {
        throw new NullPointerException();
      }
      if (fSAXParser != null)
      {
        if ("http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(paramString))
        {
          if (fSAXParser.grammar != null) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(fConfiguration.getLocale(), "schema-already-specified", new Object[] { paramString }));
          }
          if ("http://www.w3.org/2001/XMLSchema".equals(paramObject))
          {
            if (fSAXParser.isValidating())
            {
              fSAXParser.schemaLanguage = "http://www.w3.org/2001/XMLSchema";
              setFeature("http://apache.org/xml/features/validation/schema", true);
              if (!fInitProperties.containsKey("http://java.sun.com/xml/jaxp/properties/schemaLanguage")) {
                fInitProperties.put("http://java.sun.com/xml/jaxp/properties/schemaLanguage", super.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage"));
              }
              super.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            }
          }
          else if (paramObject == null)
          {
            fSAXParser.schemaLanguage = null;
            setFeature("http://apache.org/xml/features/validation/schema", false);
          }
          else
          {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(fConfiguration.getLocale(), "schema-not-supported", null));
          }
          return;
        }
        if ("http://java.sun.com/xml/jaxp/properties/schemaSource".equals(paramString))
        {
          if (fSAXParser.grammar != null) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(fConfiguration.getLocale(), "schema-already-specified", new Object[] { paramString }));
          }
          String str = (String)getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
          if ((str != null) && ("http://www.w3.org/2001/XMLSchema".equals(str)))
          {
            if (!fInitProperties.containsKey("http://java.sun.com/xml/jaxp/properties/schemaSource")) {
              fInitProperties.put("http://java.sun.com/xml/jaxp/properties/schemaSource", super.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource"));
            }
            super.setProperty(paramString, paramObject);
          }
          else
          {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(fConfiguration.getLocale(), "jaxp-order-not-supported", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://java.sun.com/xml/jaxp/properties/schemaSource" }));
          }
          return;
        }
      }
      if ((fSAXParser != null) && (fSAXParser.fSchemaValidator != null)) {
        setSchemaValidatorProperty(paramString, paramObject);
      }
      if (((fSecurityManager == null) || (!fSecurityManager.setLimit(paramString, XMLSecurityManager.State.APIPROPERTY, paramObject))) && ((fSecurityPropertyMgr == null) || (!fSecurityPropertyMgr.setValue(paramString, XMLSecurityPropertyManager.State.APIPROPERTY, paramObject))))
      {
        if (!fInitProperties.containsKey(paramString)) {
          fInitProperties.put(paramString, super.getProperty(paramString));
        }
        super.setProperty(paramString, paramObject);
      }
    }
    
    public synchronized Object getProperty(String paramString)
      throws SAXNotRecognizedException, SAXNotSupportedException
    {
      if (paramString == null) {
        throw new NullPointerException();
      }
      if ((fSAXParser != null) && ("http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(paramString))) {
        return fSAXParser.schemaLanguage;
      }
      Object localObject = fSecurityManager != null ? fSecurityManager.getLimitAsString(paramString) : null;
      if (localObject != null) {
        return localObject;
      }
      localObject = fSecurityPropertyMgr != null ? fSecurityPropertyMgr.getValue(paramString) : null;
      if (localObject != null) {
        return localObject;
      }
      return super.getProperty(paramString);
    }
    
    synchronized void restoreInitState()
      throws SAXNotRecognizedException, SAXNotSupportedException
    {
      Iterator localIterator;
      Map.Entry localEntry;
      String str;
      if (!fInitFeatures.isEmpty())
      {
        localIterator = fInitFeatures.entrySet().iterator();
        while (localIterator.hasNext())
        {
          localEntry = (Map.Entry)localIterator.next();
          str = (String)localEntry.getKey();
          boolean bool = ((Boolean)localEntry.getValue()).booleanValue();
          super.setFeature(str, bool);
        }
        fInitFeatures.clear();
      }
      if (!fInitProperties.isEmpty())
      {
        localIterator = fInitProperties.entrySet().iterator();
        while (localIterator.hasNext())
        {
          localEntry = (Map.Entry)localIterator.next();
          str = (String)localEntry.getKey();
          Object localObject = localEntry.getValue();
          super.setProperty(str, localObject);
        }
        fInitProperties.clear();
      }
    }
    
    public void parse(InputSource paramInputSource)
      throws SAXException, IOException
    {
      if ((fSAXParser != null) && (fSAXParser.fSchemaValidator != null))
      {
        if (fSAXParser.fSchemaValidationManager != null)
        {
          fSAXParser.fSchemaValidationManager.reset();
          fSAXParser.fUnparsedEntityHandler.reset();
        }
        resetSchemaValidator();
      }
      super.parse(paramInputSource);
    }
    
    public void parse(String paramString)
      throws SAXException, IOException
    {
      if ((fSAXParser != null) && (fSAXParser.fSchemaValidator != null))
      {
        if (fSAXParser.fSchemaValidationManager != null)
        {
          fSAXParser.fSchemaValidationManager.reset();
          fSAXParser.fUnparsedEntityHandler.reset();
        }
        resetSchemaValidator();
      }
      super.parse(paramString);
    }
    
    XMLParserConfiguration getXMLParserConfiguration()
    {
      return fConfiguration;
    }
    
    void setFeature0(String paramString, boolean paramBoolean)
      throws SAXNotRecognizedException, SAXNotSupportedException
    {
      super.setFeature(paramString, paramBoolean);
    }
    
    boolean getFeature0(String paramString)
      throws SAXNotRecognizedException, SAXNotSupportedException
    {
      return super.getFeature(paramString);
    }
    
    void setProperty0(String paramString, Object paramObject)
      throws SAXNotRecognizedException, SAXNotSupportedException
    {
      super.setProperty(paramString, paramObject);
    }
    
    Object getProperty0(String paramString)
      throws SAXNotRecognizedException, SAXNotSupportedException
    {
      return super.getProperty(paramString);
    }
    
    Locale getLocale()
    {
      return fConfiguration.getLocale();
    }
    
    private void setSchemaValidatorFeature(String paramString, boolean paramBoolean)
      throws SAXNotRecognizedException, SAXNotSupportedException
    {
      try
      {
        fSAXParser.fSchemaValidator.setFeature(paramString, paramBoolean);
      }
      catch (XMLConfigurationException localXMLConfigurationException)
      {
        String str = localXMLConfigurationException.getIdentifier();
        if (localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED) {
          throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(fConfiguration.getLocale(), "feature-not-recognized", new Object[] { str }));
        }
        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(fConfiguration.getLocale(), "feature-not-supported", new Object[] { str }));
      }
    }
    
    private void setSchemaValidatorProperty(String paramString, Object paramObject)
      throws SAXNotRecognizedException, SAXNotSupportedException
    {
      try
      {
        fSAXParser.fSchemaValidator.setProperty(paramString, paramObject);
      }
      catch (XMLConfigurationException localXMLConfigurationException)
      {
        String str = localXMLConfigurationException.getIdentifier();
        if (localXMLConfigurationException.getType() == Status.NOT_RECOGNIZED) {
          throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(fConfiguration.getLocale(), "property-not-recognized", new Object[] { str }));
        }
        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(fConfiguration.getLocale(), "property-not-supported", new Object[] { str }));
      }
    }
    
    private void resetSchemaValidator()
      throws SAXException
    {
      try
      {
        fSAXParser.fSchemaValidator.reset(fSAXParser.fSchemaValidatorComponentManager);
      }
      catch (XMLConfigurationException localXMLConfigurationException)
      {
        throw new SAXException(localXMLConfigurationException);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\SAXParserImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */