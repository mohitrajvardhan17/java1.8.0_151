package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.XML11DTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XML11DocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XML11NSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLEntityHandler;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLVersionDetector;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11DTDProcessor;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11DTDValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.XML11NSDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDProcessor;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLNSDTDValidator;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLPullParserConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class XML11Configuration
  extends ParserConfigurationSettings
  implements XMLPullParserConfiguration, XML11Configurable
{
  protected static final String XML11_DATATYPE_VALIDATOR_FACTORY = "com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl";
  protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
  protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
  protected static final String WARN_ON_UNDECLARED_ELEMDEF = "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef";
  protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
  protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
  protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
  protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
  protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
  protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
  protected static final String SCHEMA_AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
  protected static final String XMLSCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
  protected static final String XMLSCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
  protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
  protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
  protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
  protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
  protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
  protected static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
  protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
  protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  protected static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
  protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
  protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
  protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
  protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  protected static final String DTD_PROCESSOR = "http://apache.org/xml/properties/internal/dtd-processor";
  protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
  protected static final String NAMESPACE_BINDER = "http://apache.org/xml/properties/internal/namespace-binder";
  protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  protected static final String LOCALE = "http://apache.org/xml/properties/locale";
  protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  protected static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
  protected SymbolTable fSymbolTable;
  protected XMLInputSource fInputSource;
  protected ValidationManager fValidationManager;
  protected XMLVersionDetector fVersionDetector;
  protected XMLLocator fLocator;
  protected Locale fLocale;
  protected ArrayList<XMLComponent> fComponents = new ArrayList();
  protected ArrayList<XMLComponent> fXML11Components = null;
  protected ArrayList<XMLComponent> fCommonComponents = null;
  protected XMLDocumentHandler fDocumentHandler;
  protected XMLDTDHandler fDTDHandler;
  protected XMLDTDContentModelHandler fDTDContentModelHandler;
  protected XMLDocumentSource fLastComponent;
  protected boolean fParseInProgress = false;
  protected boolean fConfigUpdated = false;
  protected DTDDVFactory fDatatypeValidatorFactory;
  protected XMLNSDocumentScannerImpl fNamespaceScanner;
  protected XMLDocumentScannerImpl fNonNSScanner;
  protected XMLDTDValidator fDTDValidator;
  protected XMLDTDValidator fNonNSDTDValidator;
  protected XMLDTDScanner fDTDScanner;
  protected XMLDTDProcessor fDTDProcessor;
  protected DTDDVFactory fXML11DatatypeFactory = null;
  protected XML11NSDocumentScannerImpl fXML11NSDocScanner = null;
  protected XML11DocumentScannerImpl fXML11DocScanner = null;
  protected XML11NSDTDValidator fXML11NSDTDValidator = null;
  protected XML11DTDValidator fXML11DTDValidator = null;
  protected XML11DTDScannerImpl fXML11DTDScanner = null;
  protected XML11DTDProcessor fXML11DTDProcessor = null;
  protected XMLGrammarPool fGrammarPool;
  protected XMLErrorReporter fErrorReporter;
  protected XMLEntityManager fEntityManager;
  protected XMLSchemaValidator fSchemaValidator;
  protected XMLDocumentScanner fCurrentScanner;
  protected DTDDVFactory fCurrentDVFactory;
  protected XMLDTDScanner fCurrentDTDScanner;
  private boolean f11Initialized = false;
  
  public XML11Configuration()
  {
    this(null, null, null);
  }
  
  public XML11Configuration(SymbolTable paramSymbolTable)
  {
    this(paramSymbolTable, null, null);
  }
  
  public XML11Configuration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool)
  {
    this(paramSymbolTable, paramXMLGrammarPool, null);
  }
  
  public XML11Configuration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool, XMLComponentManager paramXMLComponentManager)
  {
    super(paramXMLComponentManager);
    fFeatures = new HashMap();
    fProperties = new HashMap();
    String[] arrayOfString1 = { "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://xml.org/sax/features/validation", "http://xml.org/sax/features/namespaces", "http://apache.org/xml/features/validation/schema/normalized-value", "http://apache.org/xml/features/validation/schema/element-default", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/schema-full-checking", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities", "http://apache.org/xml/features/internal/parser-settings", "http://javax.xml.XMLConstants/feature/secure-processing" };
    addRecognizedFeatures(arrayOfString1);
    fFeatures.put("http://xml.org/sax/features/validation", Boolean.FALSE);
    fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
    fFeatures.put("http://xml.org/sax/features/external-general-entities", Boolean.TRUE);
    fFeatures.put("http://xml.org/sax/features/external-parameter-entities", Boolean.TRUE);
    fFeatures.put("http://apache.org/xml/features/continue-after-fatal-error", Boolean.FALSE);
    fFeatures.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.TRUE);
    fFeatures.put("http://apache.org/xml/features/validation/schema/element-default", Boolean.TRUE);
    fFeatures.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.TRUE);
    fFeatures.put("http://apache.org/xml/features/validation/schema/augment-psvi", Boolean.TRUE);
    fFeatures.put("http://apache.org/xml/features/generate-synthetic-annotations", Boolean.FALSE);
    fFeatures.put("http://apache.org/xml/features/validate-annotations", Boolean.FALSE);
    fFeatures.put("http://apache.org/xml/features/honour-all-schemaLocations", Boolean.FALSE);
    fFeatures.put("http://apache.org/xml/features/namespace-growth", Boolean.FALSE);
    fFeatures.put("http://apache.org/xml/features/internal/tolerate-duplicates", Boolean.FALSE);
    fFeatures.put("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", Boolean.FALSE);
    fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
    fFeatures.put("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
    String[] arrayOfString2 = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/dtd-processor", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/internal/validator/schema", "http://xml.org/sax/properties/xml-string", "http://apache.org/xml/properties/internal/grammar-pool", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://apache.org/xml/properties/locale", "http://apache.org/xml/properties/internal/validation/schema/dv-factory", "http://apache.org/xml/properties/security-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
    addRecognizedProperties(arrayOfString2);
    if (paramSymbolTable == null) {
      paramSymbolTable = new SymbolTable();
    }
    fSymbolTable = paramSymbolTable;
    fProperties.put("http://apache.org/xml/properties/internal/symbol-table", fSymbolTable);
    fGrammarPool = paramXMLGrammarPool;
    if (fGrammarPool != null) {
      fProperties.put("http://apache.org/xml/properties/internal/grammar-pool", fGrammarPool);
    }
    fEntityManager = new XMLEntityManager();
    fProperties.put("http://apache.org/xml/properties/internal/entity-manager", fEntityManager);
    addCommonComponent(fEntityManager);
    fErrorReporter = new XMLErrorReporter();
    fErrorReporter.setDocumentLocator(fEntityManager.getEntityScanner());
    fProperties.put("http://apache.org/xml/properties/internal/error-reporter", fErrorReporter);
    addCommonComponent(fErrorReporter);
    fNamespaceScanner = new XMLNSDocumentScannerImpl();
    fProperties.put("http://apache.org/xml/properties/internal/document-scanner", fNamespaceScanner);
    addComponent(fNamespaceScanner);
    fDTDScanner = new XMLDTDScannerImpl();
    fProperties.put("http://apache.org/xml/properties/internal/dtd-scanner", fDTDScanner);
    addComponent((XMLComponent)fDTDScanner);
    fDTDProcessor = new XMLDTDProcessor();
    fProperties.put("http://apache.org/xml/properties/internal/dtd-processor", fDTDProcessor);
    addComponent(fDTDProcessor);
    fDTDValidator = new XMLNSDTDValidator();
    fProperties.put("http://apache.org/xml/properties/internal/validator/dtd", fDTDValidator);
    addComponent(fDTDValidator);
    fDatatypeValidatorFactory = DTDDVFactory.getInstance();
    fProperties.put("http://apache.org/xml/properties/internal/datatype-validator-factory", fDatatypeValidatorFactory);
    fValidationManager = new ValidationManager();
    fProperties.put("http://apache.org/xml/properties/internal/validation-manager", fValidationManager);
    fVersionDetector = new XMLVersionDetector();
    if (fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null)
    {
      XMLMessageFormatter localXMLMessageFormatter = new XMLMessageFormatter();
      fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", localXMLMessageFormatter);
      fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", localXMLMessageFormatter);
    }
    try
    {
      setLocale(Locale.getDefault());
    }
    catch (XNIException localXNIException) {}
    fConfigUpdated = false;
  }
  
  public void setInputSource(XMLInputSource paramXMLInputSource)
    throws XMLConfigurationException, IOException
  {
    fInputSource = paramXMLInputSource;
  }
  
  public void setLocale(Locale paramLocale)
    throws XNIException
  {
    fLocale = paramLocale;
    fErrorReporter.setLocale(paramLocale);
  }
  
  public void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler)
  {
    fDocumentHandler = paramXMLDocumentHandler;
    if (fLastComponent != null)
    {
      fLastComponent.setDocumentHandler(fDocumentHandler);
      if (fDocumentHandler != null) {
        fDocumentHandler.setDocumentSource(fLastComponent);
      }
    }
  }
  
  public XMLDocumentHandler getDocumentHandler()
  {
    return fDocumentHandler;
  }
  
  public void setDTDHandler(XMLDTDHandler paramXMLDTDHandler)
  {
    fDTDHandler = paramXMLDTDHandler;
  }
  
  public XMLDTDHandler getDTDHandler()
  {
    return fDTDHandler;
  }
  
  public void setDTDContentModelHandler(XMLDTDContentModelHandler paramXMLDTDContentModelHandler)
  {
    fDTDContentModelHandler = paramXMLDTDContentModelHandler;
  }
  
  public XMLDTDContentModelHandler getDTDContentModelHandler()
  {
    return fDTDContentModelHandler;
  }
  
  public void setEntityResolver(XMLEntityResolver paramXMLEntityResolver)
  {
    fProperties.put("http://apache.org/xml/properties/internal/entity-resolver", paramXMLEntityResolver);
  }
  
  public XMLEntityResolver getEntityResolver()
  {
    return (XMLEntityResolver)fProperties.get("http://apache.org/xml/properties/internal/entity-resolver");
  }
  
  public void setErrorHandler(XMLErrorHandler paramXMLErrorHandler)
  {
    fProperties.put("http://apache.org/xml/properties/internal/error-handler", paramXMLErrorHandler);
  }
  
  public XMLErrorHandler getErrorHandler()
  {
    return (XMLErrorHandler)fProperties.get("http://apache.org/xml/properties/internal/error-handler");
  }
  
  public void cleanup()
  {
    fEntityManager.closeReaders();
  }
  
  public void parse(XMLInputSource paramXMLInputSource)
    throws XNIException, IOException
  {
    if (fParseInProgress) {
      throw new XNIException("FWK005 parse may not be called while parsing.");
    }
    fParseInProgress = true;
    try
    {
      setInputSource(paramXMLInputSource);
      parse(true);
    }
    catch (XNIException localXNIException)
    {
      throw localXNIException;
    }
    catch (IOException localIOException)
    {
      throw localIOException;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Exception localException)
    {
      throw new XNIException(localException);
    }
    finally
    {
      fParseInProgress = false;
      cleanup();
    }
  }
  
  public boolean parse(boolean paramBoolean)
    throws XNIException, IOException
  {
    if (fInputSource != null) {
      try
      {
        fValidationManager.reset();
        fVersionDetector.reset(this);
        fConfigUpdated = true;
        resetCommon();
        short s = fVersionDetector.determineDocVersion(fInputSource);
        if (s == 2)
        {
          initXML11Components();
          configureXML11Pipeline();
          resetXML11();
        }
        else
        {
          configurePipeline();
          reset();
        }
        fConfigUpdated = false;
        fVersionDetector.startDocumentParsing((XMLEntityHandler)fCurrentScanner, s);
        fInputSource = null;
      }
      catch (XNIException localXNIException1)
      {
        throw localXNIException1;
      }
      catch (IOException localIOException1)
      {
        throw localIOException1;
      }
      catch (RuntimeException localRuntimeException1)
      {
        throw localRuntimeException1;
      }
      catch (Exception localException1)
      {
        throw new XNIException(localException1);
      }
    }
    try
    {
      return fCurrentScanner.scanDocument(paramBoolean);
    }
    catch (XNIException localXNIException2)
    {
      throw localXNIException2;
    }
    catch (IOException localIOException2)
    {
      throw localIOException2;
    }
    catch (RuntimeException localRuntimeException2)
    {
      throw localRuntimeException2;
    }
    catch (Exception localException2)
    {
      throw new XNIException(localException2);
    }
  }
  
  public FeatureState getFeatureState(String paramString)
    throws XMLConfigurationException
  {
    if (paramString.equals("http://apache.org/xml/features/internal/parser-settings")) {
      return FeatureState.is(fConfigUpdated);
    }
    return super.getFeatureState(paramString);
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {
    fConfigUpdated = true;
    int i = fComponents.size();
    XMLComponent localXMLComponent;
    for (int j = 0; j < i; j++)
    {
      localXMLComponent = (XMLComponent)fComponents.get(j);
      localXMLComponent.setFeature(paramString, paramBoolean);
    }
    i = fCommonComponents.size();
    for (j = 0; j < i; j++)
    {
      localXMLComponent = (XMLComponent)fCommonComponents.get(j);
      localXMLComponent.setFeature(paramString, paramBoolean);
    }
    i = fXML11Components.size();
    for (j = 0; j < i; j++)
    {
      localXMLComponent = (XMLComponent)fXML11Components.get(j);
      try
      {
        localXMLComponent.setFeature(paramString, paramBoolean);
      }
      catch (Exception localException) {}
    }
    super.setFeature(paramString, paramBoolean);
  }
  
  public PropertyState getPropertyState(String paramString)
    throws XMLConfigurationException
  {
    if ("http://apache.org/xml/properties/locale".equals(paramString)) {
      return PropertyState.is(getLocale());
    }
    return super.getPropertyState(paramString);
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException
  {
    fConfigUpdated = true;
    if ("http://apache.org/xml/properties/locale".equals(paramString)) {
      setLocale((Locale)paramObject);
    }
    int i = fComponents.size();
    XMLComponent localXMLComponent;
    for (int j = 0; j < i; j++)
    {
      localXMLComponent = (XMLComponent)fComponents.get(j);
      localXMLComponent.setProperty(paramString, paramObject);
    }
    i = fCommonComponents.size();
    for (j = 0; j < i; j++)
    {
      localXMLComponent = (XMLComponent)fCommonComponents.get(j);
      localXMLComponent.setProperty(paramString, paramObject);
    }
    i = fXML11Components.size();
    for (j = 0; j < i; j++)
    {
      localXMLComponent = (XMLComponent)fXML11Components.get(j);
      try
      {
        localXMLComponent.setProperty(paramString, paramObject);
      }
      catch (Exception localException) {}
    }
    super.setProperty(paramString, paramObject);
  }
  
  public Locale getLocale()
  {
    return fLocale;
  }
  
  protected void reset()
    throws XNIException
  {
    int i = fComponents.size();
    for (int j = 0; j < i; j++)
    {
      XMLComponent localXMLComponent = (XMLComponent)fComponents.get(j);
      localXMLComponent.reset(this);
    }
  }
  
  protected void resetCommon()
    throws XNIException
  {
    int i = fCommonComponents.size();
    for (int j = 0; j < i; j++)
    {
      XMLComponent localXMLComponent = (XMLComponent)fCommonComponents.get(j);
      localXMLComponent.reset(this);
    }
  }
  
  protected void resetXML11()
    throws XNIException
  {
    int i = fXML11Components.size();
    for (int j = 0; j < i; j++)
    {
      XMLComponent localXMLComponent = (XMLComponent)fXML11Components.get(j);
      localXMLComponent.reset(this);
    }
  }
  
  protected void configureXML11Pipeline()
  {
    if (fCurrentDVFactory != fXML11DatatypeFactory)
    {
      fCurrentDVFactory = fXML11DatatypeFactory;
      setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", fCurrentDVFactory);
    }
    if (fCurrentDTDScanner != fXML11DTDScanner)
    {
      fCurrentDTDScanner = fXML11DTDScanner;
      setProperty("http://apache.org/xml/properties/internal/dtd-scanner", fCurrentDTDScanner);
      setProperty("http://apache.org/xml/properties/internal/dtd-processor", fXML11DTDProcessor);
    }
    fXML11DTDScanner.setDTDHandler(fXML11DTDProcessor);
    fXML11DTDProcessor.setDTDSource(fXML11DTDScanner);
    fXML11DTDProcessor.setDTDHandler(fDTDHandler);
    if (fDTDHandler != null) {
      fDTDHandler.setDTDSource(fXML11DTDProcessor);
    }
    fXML11DTDScanner.setDTDContentModelHandler(fXML11DTDProcessor);
    fXML11DTDProcessor.setDTDContentModelSource(fXML11DTDScanner);
    fXML11DTDProcessor.setDTDContentModelHandler(fDTDContentModelHandler);
    if (fDTDContentModelHandler != null) {
      fDTDContentModelHandler.setDTDContentModelSource(fXML11DTDProcessor);
    }
    if (fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE)
    {
      if (fCurrentScanner != fXML11NSDocScanner)
      {
        fCurrentScanner = fXML11NSDocScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", fXML11NSDocScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", fXML11NSDTDValidator);
      }
      fXML11NSDocScanner.setDTDValidator(fXML11NSDTDValidator);
      fXML11NSDocScanner.setDocumentHandler(fXML11NSDTDValidator);
      fXML11NSDTDValidator.setDocumentSource(fXML11NSDocScanner);
      fXML11NSDTDValidator.setDocumentHandler(fDocumentHandler);
      if (fDocumentHandler != null) {
        fDocumentHandler.setDocumentSource(fXML11NSDTDValidator);
      }
      fLastComponent = fXML11NSDTDValidator;
    }
    else
    {
      if (fXML11DocScanner == null)
      {
        fXML11DocScanner = new XML11DocumentScannerImpl();
        addXML11Component(fXML11DocScanner);
        fXML11DTDValidator = new XML11DTDValidator();
        addXML11Component(fXML11DTDValidator);
      }
      if (fCurrentScanner != fXML11DocScanner)
      {
        fCurrentScanner = fXML11DocScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", fXML11DocScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", fXML11DTDValidator);
      }
      fXML11DocScanner.setDocumentHandler(fXML11DTDValidator);
      fXML11DTDValidator.setDocumentSource(fXML11DocScanner);
      fXML11DTDValidator.setDocumentHandler(fDocumentHandler);
      if (fDocumentHandler != null) {
        fDocumentHandler.setDocumentSource(fXML11DTDValidator);
      }
      fLastComponent = fXML11DTDValidator;
    }
    if (fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE)
    {
      if (fSchemaValidator == null)
      {
        fSchemaValidator = new XMLSchemaValidator();
        setProperty("http://apache.org/xml/properties/internal/validator/schema", fSchemaValidator);
        addCommonComponent(fSchemaValidator);
        fSchemaValidator.reset(this);
        if (fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null)
        {
          XSMessageFormatter localXSMessageFormatter = new XSMessageFormatter();
          fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", localXSMessageFormatter);
        }
      }
      fLastComponent.setDocumentHandler(fSchemaValidator);
      fSchemaValidator.setDocumentSource(fLastComponent);
      fSchemaValidator.setDocumentHandler(fDocumentHandler);
      if (fDocumentHandler != null) {
        fDocumentHandler.setDocumentSource(fSchemaValidator);
      }
      fLastComponent = fSchemaValidator;
    }
  }
  
  protected void configurePipeline()
  {
    if (fCurrentDVFactory != fDatatypeValidatorFactory)
    {
      fCurrentDVFactory = fDatatypeValidatorFactory;
      setProperty("http://apache.org/xml/properties/internal/datatype-validator-factory", fCurrentDVFactory);
    }
    if (fCurrentDTDScanner != fDTDScanner)
    {
      fCurrentDTDScanner = fDTDScanner;
      setProperty("http://apache.org/xml/properties/internal/dtd-scanner", fCurrentDTDScanner);
      setProperty("http://apache.org/xml/properties/internal/dtd-processor", fDTDProcessor);
    }
    fDTDScanner.setDTDHandler(fDTDProcessor);
    fDTDProcessor.setDTDSource(fDTDScanner);
    fDTDProcessor.setDTDHandler(fDTDHandler);
    if (fDTDHandler != null) {
      fDTDHandler.setDTDSource(fDTDProcessor);
    }
    fDTDScanner.setDTDContentModelHandler(fDTDProcessor);
    fDTDProcessor.setDTDContentModelSource(fDTDScanner);
    fDTDProcessor.setDTDContentModelHandler(fDTDContentModelHandler);
    if (fDTDContentModelHandler != null) {
      fDTDContentModelHandler.setDTDContentModelSource(fDTDProcessor);
    }
    if (fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE)
    {
      if (fCurrentScanner != fNamespaceScanner)
      {
        fCurrentScanner = fNamespaceScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", fNamespaceScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", fDTDValidator);
      }
      fNamespaceScanner.setDTDValidator(fDTDValidator);
      fNamespaceScanner.setDocumentHandler(fDTDValidator);
      fDTDValidator.setDocumentSource(fNamespaceScanner);
      fDTDValidator.setDocumentHandler(fDocumentHandler);
      if (fDocumentHandler != null) {
        fDocumentHandler.setDocumentSource(fDTDValidator);
      }
      fLastComponent = fDTDValidator;
    }
    else
    {
      if (fNonNSScanner == null)
      {
        fNonNSScanner = new XMLDocumentScannerImpl();
        fNonNSDTDValidator = new XMLDTDValidator();
        addComponent(fNonNSScanner);
        addComponent(fNonNSDTDValidator);
      }
      if (fCurrentScanner != fNonNSScanner)
      {
        fCurrentScanner = fNonNSScanner;
        setProperty("http://apache.org/xml/properties/internal/document-scanner", fNonNSScanner);
        setProperty("http://apache.org/xml/properties/internal/validator/dtd", fNonNSDTDValidator);
      }
      fNonNSScanner.setDocumentHandler(fNonNSDTDValidator);
      fNonNSDTDValidator.setDocumentSource(fNonNSScanner);
      fNonNSDTDValidator.setDocumentHandler(fDocumentHandler);
      if (fDocumentHandler != null) {
        fDocumentHandler.setDocumentSource(fNonNSDTDValidator);
      }
      fLastComponent = fNonNSDTDValidator;
    }
    if (fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE)
    {
      if (fSchemaValidator == null)
      {
        fSchemaValidator = new XMLSchemaValidator();
        setProperty("http://apache.org/xml/properties/internal/validator/schema", fSchemaValidator);
        addCommonComponent(fSchemaValidator);
        fSchemaValidator.reset(this);
        if (fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null)
        {
          XSMessageFormatter localXSMessageFormatter = new XSMessageFormatter();
          fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", localXSMessageFormatter);
        }
      }
      fLastComponent.setDocumentHandler(fSchemaValidator);
      fSchemaValidator.setDocumentSource(fLastComponent);
      fSchemaValidator.setDocumentHandler(fDocumentHandler);
      if (fDocumentHandler != null) {
        fDocumentHandler.setDocumentSource(fSchemaValidator);
      }
      fLastComponent = fSchemaValidator;
    }
  }
  
  protected FeatureState checkFeature(String paramString)
    throws XMLConfigurationException
  {
    if (paramString.startsWith("http://apache.org/xml/features/"))
    {
      int i = paramString.length() - "http://apache.org/xml/features/".length();
      if ((i == "validation/dynamic".length()) && (paramString.endsWith("validation/dynamic"))) {
        return FeatureState.RECOGNIZED;
      }
      if ((i == "validation/default-attribute-values".length()) && (paramString.endsWith("validation/default-attribute-values"))) {
        return FeatureState.NOT_SUPPORTED;
      }
      if ((i == "validation/validate-content-models".length()) && (paramString.endsWith("validation/validate-content-models"))) {
        return FeatureState.NOT_SUPPORTED;
      }
      if ((i == "nonvalidating/load-dtd-grammar".length()) && (paramString.endsWith("nonvalidating/load-dtd-grammar"))) {
        return FeatureState.RECOGNIZED;
      }
      if ((i == "nonvalidating/load-external-dtd".length()) && (paramString.endsWith("nonvalidating/load-external-dtd"))) {
        return FeatureState.RECOGNIZED;
      }
      if ((i == "validation/validate-datatypes".length()) && (paramString.endsWith("validation/validate-datatypes"))) {
        return FeatureState.NOT_SUPPORTED;
      }
      if ((i == "validation/schema".length()) && (paramString.endsWith("validation/schema"))) {
        return FeatureState.RECOGNIZED;
      }
      if ((i == "validation/schema-full-checking".length()) && (paramString.endsWith("validation/schema-full-checking"))) {
        return FeatureState.RECOGNIZED;
      }
      if ((i == "validation/schema/normalized-value".length()) && (paramString.endsWith("validation/schema/normalized-value"))) {
        return FeatureState.RECOGNIZED;
      }
      if ((i == "validation/schema/element-default".length()) && (paramString.endsWith("validation/schema/element-default"))) {
        return FeatureState.RECOGNIZED;
      }
      if ((i == "internal/parser-settings".length()) && (paramString.endsWith("internal/parser-settings"))) {
        return FeatureState.NOT_SUPPORTED;
      }
    }
    return super.checkFeature(paramString);
  }
  
  protected PropertyState checkProperty(String paramString)
    throws XMLConfigurationException
  {
    int i;
    if (paramString.startsWith("http://apache.org/xml/properties/"))
    {
      i = paramString.length() - "http://apache.org/xml/properties/".length();
      if ((i == "internal/dtd-scanner".length()) && (paramString.endsWith("internal/dtd-scanner"))) {
        return PropertyState.RECOGNIZED;
      }
      if ((i == "schema/external-schemaLocation".length()) && (paramString.endsWith("schema/external-schemaLocation"))) {
        return PropertyState.RECOGNIZED;
      }
      if ((i == "schema/external-noNamespaceSchemaLocation".length()) && (paramString.endsWith("schema/external-noNamespaceSchemaLocation"))) {
        return PropertyState.RECOGNIZED;
      }
    }
    if (paramString.startsWith("http://java.sun.com/xml/jaxp/properties/"))
    {
      i = paramString.length() - "http://java.sun.com/xml/jaxp/properties/".length();
      if ((i == "schemaSource".length()) && (paramString.endsWith("schemaSource"))) {
        return PropertyState.RECOGNIZED;
      }
    }
    if (paramString.startsWith("http://xml.org/sax/properties/"))
    {
      i = paramString.length() - "http://xml.org/sax/properties/".length();
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
    addRecognizedParamsAndSetDefaults(paramXMLComponent);
  }
  
  protected void addCommonComponent(XMLComponent paramXMLComponent)
  {
    if (fCommonComponents.contains(paramXMLComponent)) {
      return;
    }
    fCommonComponents.add(paramXMLComponent);
    addRecognizedParamsAndSetDefaults(paramXMLComponent);
  }
  
  protected void addXML11Component(XMLComponent paramXMLComponent)
  {
    if (fXML11Components.contains(paramXMLComponent)) {
      return;
    }
    fXML11Components.add(paramXMLComponent);
    addRecognizedParamsAndSetDefaults(paramXMLComponent);
  }
  
  protected void addRecognizedParamsAndSetDefaults(XMLComponent paramXMLComponent)
  {
    String[] arrayOfString1 = paramXMLComponent.getRecognizedFeatures();
    addRecognizedFeatures(arrayOfString1);
    String[] arrayOfString2 = paramXMLComponent.getRecognizedProperties();
    addRecognizedProperties(arrayOfString2);
    int i;
    String str;
    Object localObject;
    if (arrayOfString1 != null) {
      for (i = 0; i < arrayOfString1.length; i++)
      {
        str = arrayOfString1[i];
        localObject = paramXMLComponent.getFeatureDefault(str);
        if ((localObject != null) && (!fFeatures.containsKey(str)))
        {
          fFeatures.put(str, localObject);
          fConfigUpdated = true;
        }
      }
    }
    if (arrayOfString2 != null) {
      for (i = 0; i < arrayOfString2.length; i++)
      {
        str = arrayOfString2[i];
        localObject = paramXMLComponent.getPropertyDefault(str);
        if ((localObject != null) && (!fProperties.containsKey(str)))
        {
          fProperties.put(str, localObject);
          fConfigUpdated = true;
        }
      }
    }
  }
  
  private void initXML11Components()
  {
    if (!f11Initialized)
    {
      fXML11DatatypeFactory = DTDDVFactory.getInstance("com.sun.org.apache.xerces.internal.impl.dv.dtd.XML11DTDDVFactoryImpl");
      fXML11DTDScanner = new XML11DTDScannerImpl();
      addXML11Component(fXML11DTDScanner);
      fXML11DTDProcessor = new XML11DTDProcessor();
      addXML11Component(fXML11DTDProcessor);
      fXML11NSDocScanner = new XML11NSDocumentScannerImpl();
      addXML11Component(fXML11NSDocScanner);
      fXML11NSDTDValidator = new XML11NSDTDValidator();
      addXML11Component(fXML11NSDTDValidator);
      f11Initialized = true;
    }
  }
  
  FeatureState getFeatureState0(String paramString)
    throws XMLConfigurationException
  {
    return super.getFeatureState(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\parsers\XML11Configuration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */