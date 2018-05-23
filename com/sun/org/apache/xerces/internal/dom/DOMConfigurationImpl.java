package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.DOMErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.ls.LSResourceResolver;

public class DOMConfigurationImpl
  extends ParserConfigurationSettings
  implements XMLParserConfiguration, DOMConfiguration
{
  protected static final String XERCES_VALIDATION = "http://xml.org/sax/features/validation";
  protected static final String XERCES_NAMESPACES = "http://xml.org/sax/features/namespaces";
  protected static final String SCHEMA = "http://apache.org/xml/features/validation/schema";
  protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
  protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
  protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
  protected static final String SEND_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
  protected static final String DTD_VALIDATOR_FACTORY_PROPERTY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
  protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
  protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  XMLDocumentHandler fDocumentHandler;
  protected short features = 0;
  protected static final short NAMESPACES = 1;
  protected static final short DTNORMALIZATION = 2;
  protected static final short ENTITIES = 4;
  protected static final short CDATA = 8;
  protected static final short SPLITCDATA = 16;
  protected static final short COMMENTS = 32;
  protected static final short VALIDATE = 64;
  protected static final short PSVI = 128;
  protected static final short WELLFORMED = 256;
  protected static final short NSDECL = 512;
  protected static final short INFOSET_TRUE_PARAMS = 801;
  protected static final short INFOSET_FALSE_PARAMS = 14;
  protected static final short INFOSET_MASK = 815;
  protected SymbolTable fSymbolTable;
  protected ArrayList fComponents;
  protected ValidationManager fValidationManager;
  protected Locale fLocale;
  protected XMLErrorReporter fErrorReporter;
  protected final DOMErrorHandlerWrapper fErrorHandlerWrapper = new DOMErrorHandlerWrapper();
  private DOMStringList fRecognizedParameters;
  
  protected DOMConfigurationImpl()
  {
    this(null, null);
  }
  
  protected DOMConfigurationImpl(SymbolTable paramSymbolTable)
  {
    this(paramSymbolTable, null);
  }
  
  protected DOMConfigurationImpl(SymbolTable paramSymbolTable, XMLComponentManager paramXMLComponentManager)
  {
    super(paramXMLComponentManager);
    fFeatures = new HashMap();
    fProperties = new HashMap();
    String[] arrayOfString1 = { "http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/validation/dynamic", "http://apache.org/xml/features/validation/schema/normalized-value", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates" };
    addRecognizedFeatures(arrayOfString1);
    setFeature("http://xml.org/sax/features/validation", false);
    setFeature("http://apache.org/xml/features/validation/schema", false);
    setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
    setFeature("http://apache.org/xml/features/validation/dynamic", false);
    setFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
    setFeature("http://xml.org/sax/features/namespaces", true);
    setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
    setFeature("http://apache.org/xml/features/namespace-growth", false);
    String[] arrayOfString2 = { "http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/internal/grammar-pool", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation/schema/dv-factory", "http://apache.org/xml/properties/security-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
    addRecognizedProperties(arrayOfString2);
    features = ((short)(features | 0x1));
    features = ((short)(features | 0x4));
    features = ((short)(features | 0x20));
    features = ((short)(features | 0x8));
    features = ((short)(features | 0x10));
    features = ((short)(features | 0x100));
    features = ((short)(features | 0x200));
    if (paramSymbolTable == null) {
      paramSymbolTable = new SymbolTable();
    }
    fSymbolTable = paramSymbolTable;
    fComponents = new ArrayList();
    setProperty("http://apache.org/xml/properties/internal/symbol-table", fSymbolTable);
    fErrorReporter = new XMLErrorReporter();
    setProperty("http://apache.org/xml/properties/internal/error-reporter", fErrorReporter);
    addComponent(fErrorReporter);
    setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", DTDDVFactory.getInstance());
    XMLEntityManager localXMLEntityManager = new XMLEntityManager();
    setProperty("http://apache.org/xml/properties/internal/entity-manager", localXMLEntityManager);
    addComponent(localXMLEntityManager);
    fValidationManager = createValidationManager();
    setProperty("http://apache.org/xml/properties/internal/validation-manager", fValidationManager);
    setProperty("http://apache.org/xml/properties/security-manager", new XMLSecurityManager(true));
    setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", new XMLSecurityPropertyManager());
    Object localObject;
    if (fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null)
    {
      localObject = new XMLMessageFormatter();
      fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", (MessageFormatter)localObject);
      fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", (MessageFormatter)localObject);
    }
    if (fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null)
    {
      localObject = null;
      try
      {
        localObject = (MessageFormatter)ObjectFactory.newInstance("com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter", true);
      }
      catch (Exception localException) {}
      if (localObject != null) {
        fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", (MessageFormatter)localObject);
      }
    }
    try
    {
      setLocale(Locale.getDefault());
    }
    catch (XNIException localXNIException) {}
  }
  
  public void parse(XMLInputSource paramXMLInputSource)
    throws XNIException, IOException
  {}
  
  public void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler)
  {
    fDocumentHandler = paramXMLDocumentHandler;
  }
  
  public XMLDocumentHandler getDocumentHandler()
  {
    return fDocumentHandler;
  }
  
  public void setDTDHandler(XMLDTDHandler paramXMLDTDHandler) {}
  
  public XMLDTDHandler getDTDHandler()
  {
    return null;
  }
  
  public void setDTDContentModelHandler(XMLDTDContentModelHandler paramXMLDTDContentModelHandler) {}
  
  public XMLDTDContentModelHandler getDTDContentModelHandler()
  {
    return null;
  }
  
  public void setEntityResolver(XMLEntityResolver paramXMLEntityResolver)
  {
    if (paramXMLEntityResolver != null) {
      fProperties.put("http://apache.org/xml/properties/internal/entity-resolver", paramXMLEntityResolver);
    }
  }
  
  public XMLEntityResolver getEntityResolver()
  {
    return (XMLEntityResolver)fProperties.get("http://apache.org/xml/properties/internal/entity-resolver");
  }
  
  public void setErrorHandler(XMLErrorHandler paramXMLErrorHandler)
  {
    if (paramXMLErrorHandler != null) {
      fProperties.put("http://apache.org/xml/properties/internal/error-handler", paramXMLErrorHandler);
    }
  }
  
  public XMLErrorHandler getErrorHandler()
  {
    return (XMLErrorHandler)fProperties.get("http://apache.org/xml/properties/internal/error-handler");
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {
    super.setFeature(paramString, paramBoolean);
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException
  {
    super.setProperty(paramString, paramObject);
  }
  
  public void setLocale(Locale paramLocale)
    throws XNIException
  {
    fLocale = paramLocale;
    fErrorReporter.setLocale(paramLocale);
  }
  
  public Locale getLocale()
  {
    return fLocale;
  }
  
  public void setParameter(String paramString, Object paramObject)
    throws DOMException
  {
    int i = 1;
    if ((paramObject instanceof Boolean))
    {
      boolean bool = ((Boolean)paramObject).booleanValue();
      if (paramString.equalsIgnoreCase("comments"))
      {
        features = ((short)(bool ? features | 0x20 : features & 0xFFFFFFDF));
      }
      else if (paramString.equalsIgnoreCase("datatype-normalization"))
      {
        setFeature("http://apache.org/xml/features/validation/schema/normalized-value", bool);
        features = ((short)(bool ? features | 0x2 : features & 0xFFFFFFFD));
        if (bool) {
          features = ((short)(features | 0x40));
        }
      }
      else if (paramString.equalsIgnoreCase("namespaces"))
      {
        features = ((short)(bool ? features | 0x1 : features & 0xFFFFFFFE));
      }
      else if (paramString.equalsIgnoreCase("cdata-sections"))
      {
        features = ((short)(bool ? features | 0x8 : features & 0xFFFFFFF7));
      }
      else if (paramString.equalsIgnoreCase("entities"))
      {
        features = ((short)(bool ? features | 0x4 : features & 0xFFFFFFFB));
      }
      else if (paramString.equalsIgnoreCase("split-cdata-sections"))
      {
        features = ((short)(bool ? features | 0x10 : features & 0xFFFFFFEF));
      }
      else if (paramString.equalsIgnoreCase("validate"))
      {
        features = ((short)(bool ? features | 0x40 : features & 0xFFFFFFBF));
      }
      else if (paramString.equalsIgnoreCase("well-formed"))
      {
        features = ((short)(bool ? features | 0x100 : features & 0xFEFF));
      }
      else if (paramString.equalsIgnoreCase("namespace-declarations"))
      {
        features = ((short)(bool ? features | 0x200 : features & 0xFDFF));
      }
      else if (paramString.equalsIgnoreCase("infoset"))
      {
        if (bool)
        {
          features = ((short)(features | 0x321));
          features = ((short)(features & 0xFFFFFFF1));
          setFeature("http://apache.org/xml/features/validation/schema/normalized-value", false);
        }
      }
      else
      {
        String str5;
        if ((paramString.equalsIgnoreCase("normalize-characters")) || (paramString.equalsIgnoreCase("canonical-form")) || (paramString.equalsIgnoreCase("validate-if-schema")) || (paramString.equalsIgnoreCase("check-character-normalization")))
        {
          if (bool)
          {
            str5 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
            throw new DOMException((short)9, str5);
          }
        }
        else if (paramString.equalsIgnoreCase("element-content-whitespace"))
        {
          if (!bool)
          {
            str5 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
            throw new DOMException((short)9, str5);
          }
        }
        else if (paramString.equalsIgnoreCase("http://apache.org/xml/features/validation/schema/augment-psvi"))
        {
          if (!bool)
          {
            str5 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
            throw new DOMException((short)9, str5);
          }
        }
        else if (paramString.equalsIgnoreCase("psvi")) {
          features = ((short)(bool ? features | 0x80 : features & 0xFF7F));
        } else {
          i = 0;
        }
      }
    }
    if ((i == 0) || (!(paramObject instanceof Boolean)))
    {
      i = 1;
      if (paramString.equalsIgnoreCase("error-handler"))
      {
        if (((paramObject instanceof DOMErrorHandler)) || (paramObject == null))
        {
          fErrorHandlerWrapper.setErrorHandler((DOMErrorHandler)paramObject);
          setErrorHandler(fErrorHandlerWrapper);
        }
        else
        {
          String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { paramString });
          throw new DOMException((short)17, str1);
        }
      }
      else if (paramString.equalsIgnoreCase("resource-resolver"))
      {
        if (((paramObject instanceof LSResourceResolver)) || (paramObject == null))
        {
          try
          {
            setEntityResolver(new DOMEntityResolverWrapper((LSResourceResolver)paramObject));
          }
          catch (XMLConfigurationException localXMLConfigurationException1) {}
        }
        else
        {
          String str2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { paramString });
          throw new DOMException((short)17, str2);
        }
      }
      else if (paramString.equalsIgnoreCase("schema-location"))
      {
        if (((paramObject instanceof String)) || (paramObject == null))
        {
          try
          {
            setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", paramObject);
          }
          catch (XMLConfigurationException localXMLConfigurationException2) {}
        }
        else
        {
          String str3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { paramString });
          throw new DOMException((short)17, str3);
        }
      }
      else
      {
        String str4;
        if (paramString.equalsIgnoreCase("schema-type"))
        {
          if (((paramObject instanceof String)) || (paramObject == null))
          {
            try
            {
              if (paramObject == null) {
                setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", null);
              } else if (paramObject.equals(Constants.NS_XMLSCHEMA)) {
                setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_XMLSCHEMA);
              } else if (paramObject.equals(Constants.NS_DTD)) {
                setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_DTD);
              }
            }
            catch (XMLConfigurationException localXMLConfigurationException3) {}
          }
          else
          {
            str4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { paramString });
            throw new DOMException((short)17, str4);
          }
        }
        else if (paramString.equalsIgnoreCase("http://apache.org/xml/properties/internal/symbol-table"))
        {
          if ((paramObject instanceof SymbolTable))
          {
            setProperty("http://apache.org/xml/properties/internal/symbol-table", paramObject);
          }
          else
          {
            str4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { paramString });
            throw new DOMException((short)17, str4);
          }
        }
        else if (paramString.equalsIgnoreCase("http://apache.org/xml/properties/internal/grammar-pool"))
        {
          if ((paramObject instanceof XMLGrammarPool))
          {
            setProperty("http://apache.org/xml/properties/internal/grammar-pool", paramObject);
          }
          else
          {
            str4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[] { paramString });
            throw new DOMException((short)17, str4);
          }
        }
        else
        {
          str4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { paramString });
          throw new DOMException((short)8, str4);
        }
      }
    }
  }
  
  public Object getParameter(String paramString)
    throws DOMException
  {
    if (paramString.equalsIgnoreCase("comments")) {
      return (features & 0x20) != 0 ? Boolean.TRUE : Boolean.FALSE;
    }
    if (paramString.equalsIgnoreCase("namespaces")) {
      return (features & 0x1) != 0 ? Boolean.TRUE : Boolean.FALSE;
    }
    if (paramString.equalsIgnoreCase("datatype-normalization")) {
      return (features & 0x2) != 0 ? Boolean.TRUE : Boolean.FALSE;
    }
    if (paramString.equalsIgnoreCase("cdata-sections")) {
      return (features & 0x8) != 0 ? Boolean.TRUE : Boolean.FALSE;
    }
    if (paramString.equalsIgnoreCase("entities")) {
      return (features & 0x4) != 0 ? Boolean.TRUE : Boolean.FALSE;
    }
    if (paramString.equalsIgnoreCase("split-cdata-sections")) {
      return (features & 0x10) != 0 ? Boolean.TRUE : Boolean.FALSE;
    }
    if (paramString.equalsIgnoreCase("validate")) {
      return (features & 0x40) != 0 ? Boolean.TRUE : Boolean.FALSE;
    }
    if (paramString.equalsIgnoreCase("well-formed")) {
      return (features & 0x100) != 0 ? Boolean.TRUE : Boolean.FALSE;
    }
    if (paramString.equalsIgnoreCase("namespace-declarations")) {
      return (features & 0x200) != 0 ? Boolean.TRUE : Boolean.FALSE;
    }
    if (paramString.equalsIgnoreCase("infoset")) {
      return (features & 0x32F) == 801 ? Boolean.TRUE : Boolean.FALSE;
    }
    if ((paramString.equalsIgnoreCase("normalize-characters")) || (paramString.equalsIgnoreCase("canonical-form")) || (paramString.equalsIgnoreCase("validate-if-schema")) || (paramString.equalsIgnoreCase("check-character-normalization"))) {
      return Boolean.FALSE;
    }
    if (paramString.equalsIgnoreCase("http://apache.org/xml/features/validation/schema/augment-psvi")) {
      return Boolean.TRUE;
    }
    if (paramString.equalsIgnoreCase("psvi")) {
      return (features & 0x80) != 0 ? Boolean.TRUE : Boolean.FALSE;
    }
    if (paramString.equalsIgnoreCase("element-content-whitespace")) {
      return Boolean.TRUE;
    }
    if (paramString.equalsIgnoreCase("error-handler")) {
      return fErrorHandlerWrapper.getErrorHandler();
    }
    if (paramString.equalsIgnoreCase("resource-resolver"))
    {
      localObject = getEntityResolver();
      if ((localObject != null) && ((localObject instanceof DOMEntityResolverWrapper))) {
        return ((DOMEntityResolverWrapper)localObject).getEntityResolver();
      }
      return null;
    }
    if (paramString.equalsIgnoreCase("schema-type")) {
      return getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
    }
    if (paramString.equalsIgnoreCase("schema-location")) {
      return getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource");
    }
    if (paramString.equalsIgnoreCase("http://apache.org/xml/properties/internal/symbol-table")) {
      return getProperty("http://apache.org/xml/properties/internal/symbol-table");
    }
    if (paramString.equalsIgnoreCase("http://apache.org/xml/properties/internal/grammar-pool")) {
      return getProperty("http://apache.org/xml/properties/internal/grammar-pool");
    }
    Object localObject = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[] { paramString });
    throw new DOMException((short)8, (String)localObject);
  }
  
  public boolean canSetParameter(String paramString, Object paramObject)
  {
    if (paramObject == null) {
      return true;
    }
    if ((paramObject instanceof Boolean))
    {
      if ((paramString.equalsIgnoreCase("comments")) || (paramString.equalsIgnoreCase("datatype-normalization")) || (paramString.equalsIgnoreCase("cdata-sections")) || (paramString.equalsIgnoreCase("entities")) || (paramString.equalsIgnoreCase("split-cdata-sections")) || (paramString.equalsIgnoreCase("namespaces")) || (paramString.equalsIgnoreCase("validate")) || (paramString.equalsIgnoreCase("well-formed")) || (paramString.equalsIgnoreCase("infoset")) || (paramString.equalsIgnoreCase("namespace-declarations"))) {
        return true;
      }
      if ((paramString.equalsIgnoreCase("normalize-characters")) || (paramString.equalsIgnoreCase("canonical-form")) || (paramString.equalsIgnoreCase("validate-if-schema")) || (paramString.equalsIgnoreCase("check-character-normalization"))) {
        return !paramObject.equals(Boolean.TRUE);
      }
      if ((paramString.equalsIgnoreCase("element-content-whitespace")) || (paramString.equalsIgnoreCase("http://apache.org/xml/features/validation/schema/augment-psvi"))) {
        return paramObject.equals(Boolean.TRUE);
      }
      return false;
    }
    if (paramString.equalsIgnoreCase("error-handler")) {
      return (paramObject instanceof DOMErrorHandler);
    }
    if (paramString.equalsIgnoreCase("resource-resolver")) {
      return (paramObject instanceof LSResourceResolver);
    }
    if (paramString.equalsIgnoreCase("schema-location")) {
      return (paramObject instanceof String);
    }
    if (paramString.equalsIgnoreCase("schema-type")) {
      return ((paramObject instanceof String)) && (paramObject.equals(Constants.NS_XMLSCHEMA));
    }
    if (paramString.equalsIgnoreCase("http://apache.org/xml/properties/internal/symbol-table")) {
      return (paramObject instanceof SymbolTable);
    }
    if (paramString.equalsIgnoreCase("http://apache.org/xml/properties/internal/grammar-pool")) {
      return (paramObject instanceof XMLGrammarPool);
    }
    return false;
  }
  
  public DOMStringList getParameterNames()
  {
    if (fRecognizedParameters == null)
    {
      Vector localVector = new Vector();
      localVector.add("comments");
      localVector.add("datatype-normalization");
      localVector.add("cdata-sections");
      localVector.add("entities");
      localVector.add("split-cdata-sections");
      localVector.add("namespaces");
      localVector.add("validate");
      localVector.add("infoset");
      localVector.add("normalize-characters");
      localVector.add("canonical-form");
      localVector.add("validate-if-schema");
      localVector.add("check-character-normalization");
      localVector.add("well-formed");
      localVector.add("namespace-declarations");
      localVector.add("element-content-whitespace");
      localVector.add("error-handler");
      localVector.add("schema-type");
      localVector.add("schema-location");
      localVector.add("resource-resolver");
      localVector.add("http://apache.org/xml/properties/internal/grammar-pool");
      localVector.add("http://apache.org/xml/properties/internal/symbol-table");
      localVector.add("http://apache.org/xml/features/validation/schema/augment-psvi");
      fRecognizedParameters = new DOMStringListImpl(localVector);
    }
    return fRecognizedParameters;
  }
  
  protected void reset()
    throws XNIException
  {
    if (fValidationManager != null) {
      fValidationManager.reset();
    }
    int i = fComponents.size();
    for (int j = 0; j < i; j++)
    {
      XMLComponent localXMLComponent = (XMLComponent)fComponents.get(j);
      localXMLComponent.reset(this);
    }
  }
  
  protected PropertyState checkProperty(String paramString)
    throws XMLConfigurationException
  {
    if (paramString.startsWith("http://xml.org/sax/properties/"))
    {
      int i = paramString.length() - "http://xml.org/sax/properties/".length();
      if ((i == "xml-string".length()) && (paramString.endsWith("xml-string"))) {
        return PropertyState.NOT_SUPPORTED;
      }
    }
    return super.checkProperty(paramString);
  }
  
  protected void addComponent(XMLComponent paramXMLComponent)
  {
    if (fComponents.contains(paramXMLComponent)) {
      return;
    }
    fComponents.add(paramXMLComponent);
    String[] arrayOfString1 = paramXMLComponent.getRecognizedFeatures();
    addRecognizedFeatures(arrayOfString1);
    String[] arrayOfString2 = paramXMLComponent.getRecognizedProperties();
    addRecognizedProperties(arrayOfString2);
  }
  
  protected ValidationManager createValidationManager()
  {
    return new ValidationManager();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMConfigurationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */