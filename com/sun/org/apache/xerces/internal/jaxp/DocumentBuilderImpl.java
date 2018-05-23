package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.dom.DOMImplementationImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
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
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.validation.Schema;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class DocumentBuilderImpl
  extends DocumentBuilder
  implements JAXPConstants
{
  private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
  private static final String INCLUDE_IGNORABLE_WHITESPACE = "http://apache.org/xml/features/dom/include-ignorable-whitespace";
  private static final String CREATE_ENTITY_REF_NODES_FEATURE = "http://apache.org/xml/features/dom/create-entity-ref-nodes";
  private static final String INCLUDE_COMMENTS_FEATURE = "http://apache.org/xml/features/include-comments";
  private static final String CREATE_CDATA_NODES_FEATURE = "http://apache.org/xml/features/create-cdata-nodes";
  private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
  private static final String XMLSCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
  private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  public static final String ACCESS_EXTERNAL_DTD = "http://javax.xml.XMLConstants/property/accessExternalDTD";
  public static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
  private final DOMParser domParser = new DOMParser();
  private final Schema grammar;
  private final XMLComponent fSchemaValidator;
  private final XMLComponentManager fSchemaValidatorComponentManager;
  private final ValidationManager fSchemaValidationManager;
  private final UnparsedEntityHandler fUnparsedEntityHandler;
  private final ErrorHandler fInitErrorHandler;
  private final EntityResolver fInitEntityResolver;
  private XMLSecurityManager fSecurityManager;
  private XMLSecurityPropertyManager fSecurityPropertyMgr;
  
  DocumentBuilderImpl(DocumentBuilderFactoryImpl paramDocumentBuilderFactoryImpl, Map<String, Object> paramMap, Map<String, Boolean> paramMap1)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    this(paramDocumentBuilderFactoryImpl, paramMap, paramMap1, false);
  }
  
  DocumentBuilderImpl(DocumentBuilderFactoryImpl paramDocumentBuilderFactoryImpl, Map<String, Object> paramMap, Map<String, Boolean> paramMap1, boolean paramBoolean)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramDocumentBuilderFactoryImpl.isValidating())
    {
      fInitErrorHandler = new DefaultValidationErrorHandler(domParser.getXMLParserConfiguration().getLocale());
      setErrorHandler(fInitErrorHandler);
    }
    else
    {
      fInitErrorHandler = domParser.getErrorHandler();
    }
    domParser.setFeature("http://xml.org/sax/features/validation", paramDocumentBuilderFactoryImpl.isValidating());
    domParser.setFeature("http://xml.org/sax/features/namespaces", paramDocumentBuilderFactoryImpl.isNamespaceAware());
    domParser.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", !paramDocumentBuilderFactoryImpl.isIgnoringElementContentWhitespace());
    domParser.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", !paramDocumentBuilderFactoryImpl.isExpandEntityReferences());
    domParser.setFeature("http://apache.org/xml/features/include-comments", !paramDocumentBuilderFactoryImpl.isIgnoringComments());
    domParser.setFeature("http://apache.org/xml/features/create-cdata-nodes", !paramDocumentBuilderFactoryImpl.isCoalescing());
    if (paramDocumentBuilderFactoryImpl.isXIncludeAware()) {
      domParser.setFeature("http://apache.org/xml/features/xinclude", true);
    }
    fSecurityPropertyMgr = new XMLSecurityPropertyManager();
    domParser.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", fSecurityPropertyMgr);
    fSecurityManager = new XMLSecurityManager(paramBoolean);
    domParser.setProperty("http://apache.org/xml/properties/security-manager", fSecurityManager);
    Object localObject1;
    if ((paramBoolean) && (paramMap1 != null))
    {
      localObject1 = (Boolean)paramMap1.get("http://javax.xml.XMLConstants/feature/secure-processing");
      if ((localObject1 != null) && (((Boolean)localObject1).booleanValue()) && (Constants.IS_JDK8_OR_ABOVE))
      {
        fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, XMLSecurityPropertyManager.State.FSP, "");
        fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, XMLSecurityPropertyManager.State.FSP, "");
      }
    }
    grammar = paramDocumentBuilderFactoryImpl.getSchema();
    if (grammar != null)
    {
      localObject1 = domParser.getXMLParserConfiguration();
      Object localObject2 = null;
      if ((grammar instanceof XSGrammarPoolContainer))
      {
        localObject2 = new XMLSchemaValidator();
        fSchemaValidationManager = new ValidationManager();
        fUnparsedEntityHandler = new UnparsedEntityHandler(fSchemaValidationManager);
        ((XMLParserConfiguration)localObject1).setDTDHandler(fUnparsedEntityHandler);
        fUnparsedEntityHandler.setDTDHandler(domParser);
        domParser.setDTDSource(fUnparsedEntityHandler);
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
      setFeatures(paramMap1);
      ((XMLParserConfiguration)localObject1).setDocumentHandler((XMLDocumentHandler)localObject2);
      ((XMLDocumentSource)localObject2).setDocumentHandler(domParser);
      domParser.setDocumentSource((XMLDocumentSource)localObject2);
      fSchemaValidator = ((XMLComponent)localObject2);
    }
    else
    {
      fSchemaValidationManager = null;
      fUnparsedEntityHandler = null;
      fSchemaValidatorComponentManager = null;
      fSchemaValidator = null;
      setFeatures(paramMap1);
    }
    setDocumentBuilderFactoryAttributes(paramMap);
    fInitEntityResolver = domParser.getEntityResolver();
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
        domParser.setFeature((String)localEntry.getKey(), ((Boolean)localEntry.getValue()).booleanValue());
      }
    }
  }
  
  private void setDocumentBuilderFactoryAttributes(Map<String, Object> paramMap)
    throws SAXNotSupportedException, SAXNotRecognizedException
  {
    if (paramMap == null) {
      return;
    }
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str1 = (String)localEntry.getKey();
      Object localObject = localEntry.getValue();
      if ((localObject instanceof Boolean)) {
        domParser.setFeature(str1, ((Boolean)localObject).booleanValue());
      } else if ("http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(str1))
      {
        if (("http://www.w3.org/2001/XMLSchema".equals(localObject)) && (isValidating()))
        {
          domParser.setFeature("http://apache.org/xml/features/validation/schema", true);
          domParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        }
      }
      else if ("http://java.sun.com/xml/jaxp/properties/schemaSource".equals(str1))
      {
        if (isValidating())
        {
          String str2 = (String)paramMap.get("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
          if ((str2 != null) && ("http://www.w3.org/2001/XMLSchema".equals(str2))) {
            domParser.setProperty(str1, localObject);
          } else {
            throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "jaxp-order-not-supported", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://java.sun.com/xml/jaxp/properties/schemaSource" }));
          }
        }
      }
      else if (((fSecurityManager == null) || (!fSecurityManager.setLimit(str1, XMLSecurityManager.State.APIPROPERTY, localObject))) && ((fSecurityPropertyMgr == null) || (!fSecurityPropertyMgr.setValue(str1, XMLSecurityPropertyManager.State.APIPROPERTY, localObject)))) {
        domParser.setProperty(str1, localObject);
      }
    }
  }
  
  public Document newDocument()
  {
    return new DocumentImpl();
  }
  
  public DOMImplementation getDOMImplementation()
  {
    return DOMImplementationImpl.getDOMImplementation();
  }
  
  public Document parse(InputSource paramInputSource)
    throws SAXException, IOException
  {
    if (paramInputSource == null) {
      throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "jaxp-null-input-source", null));
    }
    if (fSchemaValidator != null)
    {
      if (fSchemaValidationManager != null)
      {
        fSchemaValidationManager.reset();
        fUnparsedEntityHandler.reset();
      }
      resetSchemaValidator();
    }
    domParser.parse(paramInputSource);
    Document localDocument = domParser.getDocument();
    domParser.dropDocumentReferences();
    return localDocument;
  }
  
  public boolean isNamespaceAware()
  {
    try
    {
      return domParser.getFeature("http://xml.org/sax/features/namespaces");
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
      return domParser.getFeature("http://xml.org/sax/features/validation");
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
      return domParser.getFeature("http://apache.org/xml/features/xinclude");
    }
    catch (SAXException localSAXException) {}
    return false;
  }
  
  public void setEntityResolver(EntityResolver paramEntityResolver)
  {
    domParser.setEntityResolver(paramEntityResolver);
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler)
  {
    domParser.setErrorHandler(paramErrorHandler);
  }
  
  public Schema getSchema()
  {
    return grammar;
  }
  
  public void reset()
  {
    if (domParser.getErrorHandler() != fInitErrorHandler) {
      domParser.setErrorHandler(fInitErrorHandler);
    }
    if (domParser.getEntityResolver() != fInitEntityResolver) {
      domParser.setEntityResolver(fInitEntityResolver);
    }
  }
  
  DOMParser getDOMParser()
  {
    return domParser;
  }
  
  private void resetSchemaValidator()
    throws SAXException
  {
    try
    {
      fSchemaValidator.reset(fSchemaValidatorComponentManager);
    }
    catch (XMLConfigurationException localXMLConfigurationException)
    {
      throw new SAXException(localXMLConfigurationException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\DocumentBuilderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */