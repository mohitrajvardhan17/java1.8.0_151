package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.dom.DOMStringListImpl;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMBuilder;
import com.sun.org.apache.xerces.internal.impl.xs.models.CMNodeFactory;
import com.sun.org.apache.xerces.internal.impl.xs.traversers.XSDHandler;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.DOMErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.DefaultErrorHandler;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager.Property;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarLoader;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.grammars.XSGrammar;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xs.LSInputList;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSLoader;
import com.sun.org.apache.xerces.internal.xs.XSModel;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;

public class XMLSchemaLoader
  implements XMLGrammarLoader, XMLComponent, XSLoader, DOMConfiguration
{
  protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
  protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
  protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
  protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
  protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
  protected static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
  protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
  protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
  protected static final String AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
  protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
  protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
  protected static final String USE_SERVICE_MECHANISM = "http://www.oracle.com/feature/use-service-mechanism";
  private static final String[] RECOGNIZED_FEATURES = { "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/standard-uri-conformant", "http://apache.org/xml/features/disallow-doctype-decl", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "http://www.oracle.com/feature/use-service-mechanism" };
  public static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  public static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
  protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
  protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  protected static final String LOCALE = "http://apache.org/xml/properties/locale";
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  public static final String ACCESS_EXTERNAL_DTD = "http://javax.xml.XMLConstants/property/accessExternalDTD";
  public static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://apache.org/xml/properties/security-manager", "http://apache.org/xml/properties/locale", "http://apache.org/xml/properties/internal/validation/schema/dv-factory", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
  private ParserConfigurationSettings fLoaderConfig = new ParserConfigurationSettings();
  private SymbolTable fSymbolTable = null;
  private XMLErrorReporter fErrorReporter = new XMLErrorReporter();
  private XMLEntityManager fEntityManager = null;
  private XMLEntityResolver fUserEntityResolver = null;
  private XMLGrammarPool fGrammarPool = null;
  private String fExternalSchemas = null;
  private String fExternalNoNSSchema = null;
  private Object fJAXPSource = null;
  private boolean fIsCheckedFully = false;
  private boolean fJAXPProcessed = false;
  private boolean fSettingsChanged = true;
  private XSDHandler fSchemaHandler;
  private XSGrammarBucket fGrammarBucket;
  private XSDeclarationPool fDeclPool = null;
  private SubstitutionGroupHandler fSubGroupHandler;
  private final CMNodeFactory fNodeFactory = new CMNodeFactory();
  private CMBuilder fCMBuilder;
  private XSDDescription fXSDDescription = new XSDDescription();
  private String faccessExternalSchema = "all";
  private Map fJAXPCache;
  private Locale fLocale = Locale.getDefault();
  private DOMStringList fRecognizedParameters = null;
  private DOMErrorHandlerWrapper fErrorHandler = null;
  private DOMEntityResolverWrapper fResourceResolver = null;
  
  public XMLSchemaLoader()
  {
    this(new SymbolTable(), null, new XMLEntityManager(), null, null, null);
  }
  
  public XMLSchemaLoader(SymbolTable paramSymbolTable)
  {
    this(paramSymbolTable, null, new XMLEntityManager(), null, null, null);
  }
  
  XMLSchemaLoader(XMLErrorReporter paramXMLErrorReporter, XSGrammarBucket paramXSGrammarBucket, SubstitutionGroupHandler paramSubstitutionGroupHandler, CMBuilder paramCMBuilder)
  {
    this(null, paramXMLErrorReporter, null, paramXSGrammarBucket, paramSubstitutionGroupHandler, paramCMBuilder);
  }
  
  XMLSchemaLoader(SymbolTable paramSymbolTable, XMLErrorReporter paramXMLErrorReporter, XMLEntityManager paramXMLEntityManager, XSGrammarBucket paramXSGrammarBucket, SubstitutionGroupHandler paramSubstitutionGroupHandler, CMBuilder paramCMBuilder)
  {
    fLoaderConfig.addRecognizedFeatures(RECOGNIZED_FEATURES);
    fLoaderConfig.addRecognizedProperties(RECOGNIZED_PROPERTIES);
    if (paramSymbolTable != null) {
      fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/symbol-table", paramSymbolTable);
    }
    if (paramXMLErrorReporter == null)
    {
      paramXMLErrorReporter = new XMLErrorReporter();
      paramXMLErrorReporter.setLocale(fLocale);
      paramXMLErrorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", new DefaultErrorHandler());
    }
    fErrorReporter = paramXMLErrorReporter;
    if (fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
      fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
    }
    fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/error-reporter", fErrorReporter);
    fEntityManager = paramXMLEntityManager;
    if (fEntityManager != null) {
      fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/entity-manager", fEntityManager);
    }
    fLoaderConfig.setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
    if (paramXSGrammarBucket == null) {
      paramXSGrammarBucket = new XSGrammarBucket();
    }
    fGrammarBucket = paramXSGrammarBucket;
    if (paramSubstitutionGroupHandler == null) {
      paramSubstitutionGroupHandler = new SubstitutionGroupHandler(fGrammarBucket);
    }
    fSubGroupHandler = paramSubstitutionGroupHandler;
    if (paramCMBuilder == null) {
      paramCMBuilder = new CMBuilder(fNodeFactory);
    }
    fCMBuilder = paramCMBuilder;
    fSchemaHandler = new XSDHandler(fGrammarBucket);
    if (fDeclPool != null) {
      fDeclPool.reset();
    }
    fJAXPCache = new HashMap();
    fSettingsChanged = true;
  }
  
  public String[] getRecognizedFeatures()
  {
    return (String[])RECOGNIZED_FEATURES.clone();
  }
  
  public boolean getFeature(String paramString)
    throws XMLConfigurationException
  {
    return fLoaderConfig.getFeature(paramString);
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {
    fSettingsChanged = true;
    if (paramString.equals("http://apache.org/xml/features/continue-after-fatal-error")) {
      fErrorReporter.setFeature("http://apache.org/xml/features/continue-after-fatal-error", paramBoolean);
    } else if (paramString.equals("http://apache.org/xml/features/generate-synthetic-annotations")) {
      fSchemaHandler.setGenerateSyntheticAnnotations(paramBoolean);
    }
    fLoaderConfig.setFeature(paramString, paramBoolean);
  }
  
  public String[] getRecognizedProperties()
  {
    return (String[])RECOGNIZED_PROPERTIES.clone();
  }
  
  public Object getProperty(String paramString)
    throws XMLConfigurationException
  {
    return fLoaderConfig.getProperty(paramString);
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException
  {
    fSettingsChanged = true;
    fLoaderConfig.setProperty(paramString, paramObject);
    if (paramString.equals("http://java.sun.com/xml/jaxp/properties/schemaSource"))
    {
      fJAXPSource = paramObject;
      fJAXPProcessed = false;
    }
    else if (paramString.equals("http://apache.org/xml/properties/internal/grammar-pool"))
    {
      fGrammarPool = ((XMLGrammarPool)paramObject);
    }
    else if (paramString.equals("http://apache.org/xml/properties/schema/external-schemaLocation"))
    {
      fExternalSchemas = ((String)paramObject);
    }
    else if (paramString.equals("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation"))
    {
      fExternalNoNSSchema = ((String)paramObject);
    }
    else if (paramString.equals("http://apache.org/xml/properties/locale"))
    {
      setLocale((Locale)paramObject);
    }
    else if (paramString.equals("http://apache.org/xml/properties/internal/entity-resolver"))
    {
      fEntityManager.setProperty("http://apache.org/xml/properties/internal/entity-resolver", paramObject);
    }
    else if (paramString.equals("http://apache.org/xml/properties/internal/error-reporter"))
    {
      fErrorReporter = ((XMLErrorReporter)paramObject);
      if (fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
        fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
      }
    }
    else if (paramString.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"))
    {
      XMLSecurityPropertyManager localXMLSecurityPropertyManager = (XMLSecurityPropertyManager)paramObject;
      faccessExternalSchema = localXMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA);
    }
  }
  
  public void setLocale(Locale paramLocale)
  {
    fLocale = paramLocale;
    fErrorReporter.setLocale(paramLocale);
  }
  
  public Locale getLocale()
  {
    return fLocale;
  }
  
  public void setErrorHandler(XMLErrorHandler paramXMLErrorHandler)
  {
    fErrorReporter.setProperty("http://apache.org/xml/properties/internal/error-handler", paramXMLErrorHandler);
  }
  
  public XMLErrorHandler getErrorHandler()
  {
    return fErrorReporter.getErrorHandler();
  }
  
  public void setEntityResolver(XMLEntityResolver paramXMLEntityResolver)
  {
    fUserEntityResolver = paramXMLEntityResolver;
    fLoaderConfig.setProperty("http://apache.org/xml/properties/internal/entity-resolver", paramXMLEntityResolver);
    fEntityManager.setProperty("http://apache.org/xml/properties/internal/entity-resolver", paramXMLEntityResolver);
  }
  
  public XMLEntityResolver getEntityResolver()
  {
    return fUserEntityResolver;
  }
  
  public void loadGrammar(XMLInputSource[] paramArrayOfXMLInputSource)
    throws IOException, XNIException
  {
    int i = paramArrayOfXMLInputSource.length;
    for (int j = 0; j < i; j++) {
      loadGrammar(paramArrayOfXMLInputSource[j]);
    }
  }
  
  public Grammar loadGrammar(XMLInputSource paramXMLInputSource)
    throws IOException, XNIException
  {
    reset(fLoaderConfig);
    fSettingsChanged = false;
    XSDDescription localXSDDescription = new XSDDescription();
    fContextType = 3;
    localXSDDescription.setBaseSystemId(paramXMLInputSource.getBaseSystemId());
    localXSDDescription.setLiteralSystemId(paramXMLInputSource.getSystemId());
    HashMap localHashMap = new HashMap();
    processExternalHints(fExternalSchemas, fExternalNoNSSchema, localHashMap, fErrorReporter);
    SchemaGrammar localSchemaGrammar = loadSchema(localXSDDescription, paramXMLInputSource, localHashMap);
    if ((localSchemaGrammar != null) && (fGrammarPool != null))
    {
      fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", fGrammarBucket.getGrammars());
      if ((fIsCheckedFully) && (fJAXPCache.get(localSchemaGrammar) != localSchemaGrammar)) {
        XSConstraints.fullSchemaChecking(fGrammarBucket, fSubGroupHandler, fCMBuilder, fErrorReporter);
      }
    }
    return localSchemaGrammar;
  }
  
  SchemaGrammar loadSchema(XSDDescription paramXSDDescription, XMLInputSource paramXMLInputSource, Map<String, LocationArray> paramMap)
    throws IOException, XNIException
  {
    if (!fJAXPProcessed) {
      processJAXPSchemaSource(paramMap);
    }
    if (paramXSDDescription.isExternal())
    {
      localObject = SecuritySupport.checkAccess(paramXSDDescription.getExpandedSystemId(), faccessExternalSchema, "all");
      if (localObject != null) {
        throw new XNIException(fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.access", new Object[] { SecuritySupport.sanitizePath(paramXSDDescription.getExpandedSystemId()), localObject }, (short)1));
      }
    }
    Object localObject = fSchemaHandler.parseSchema(paramXMLInputSource, paramXSDDescription, paramMap);
    return (SchemaGrammar)localObject;
  }
  
  public static XMLInputSource resolveDocument(XSDDescription paramXSDDescription, Map<String, LocationArray> paramMap, XMLEntityResolver paramXMLEntityResolver)
    throws IOException
  {
    String str = null;
    if ((paramXSDDescription.getContextType() == 2) || (paramXSDDescription.fromInstance()))
    {
      localObject1 = paramXSDDescription.getTargetNamespace();
      Object localObject2 = localObject1 == null ? XMLSymbols.EMPTY_STRING : localObject1;
      LocationArray localLocationArray = (LocationArray)paramMap.get(localObject2);
      if (localLocationArray != null) {
        str = localLocationArray.getFirstLocation();
      }
    }
    if (str == null)
    {
      localObject1 = paramXSDDescription.getLocationHints();
      if ((localObject1 != null) && (localObject1.length > 0)) {
        str = localObject1[0];
      }
    }
    Object localObject1 = XMLEntityManager.expandSystemId(str, paramXSDDescription.getBaseSystemId(), false);
    paramXSDDescription.setLiteralSystemId(str);
    paramXSDDescription.setExpandedSystemId((String)localObject1);
    return paramXMLEntityResolver.resolveEntity(paramXSDDescription);
  }
  
  public static void processExternalHints(String paramString1, String paramString2, Map<String, LocationArray> paramMap, XMLErrorReporter paramXMLErrorReporter)
  {
    if (paramString1 != null) {
      try
      {
        XSAttributeDecl localXSAttributeDecl1 = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_SCHEMALOCATION);
        fType.validate(paramString1, null, null);
        if (!tokenizeSchemaLocationStr(paramString1, paramMap)) {
          paramXMLErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "SchemaLocation", new Object[] { paramString1 }, (short)0);
        }
      }
      catch (InvalidDatatypeValueException localInvalidDatatypeValueException1)
      {
        paramXMLErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", localInvalidDatatypeValueException1.getKey(), localInvalidDatatypeValueException1.getArgs(), (short)0);
      }
    }
    if (paramString2 != null) {
      try
      {
        XSAttributeDecl localXSAttributeDecl2 = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
        fType.validate(paramString2, null, null);
        LocationArray localLocationArray = (LocationArray)paramMap.get(XMLSymbols.EMPTY_STRING);
        if (localLocationArray == null)
        {
          localLocationArray = new LocationArray();
          paramMap.put(XMLSymbols.EMPTY_STRING, localLocationArray);
        }
        localLocationArray.addLocation(paramString2);
      }
      catch (InvalidDatatypeValueException localInvalidDatatypeValueException2)
      {
        paramXMLErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", localInvalidDatatypeValueException2.getKey(), localInvalidDatatypeValueException2.getArgs(), (short)0);
      }
    }
  }
  
  public static boolean tokenizeSchemaLocationStr(String paramString, Map<String, LocationArray> paramMap)
  {
    if (paramString != null)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString, " \n\t\r");
      while (localStringTokenizer.hasMoreTokens())
      {
        String str1 = localStringTokenizer.nextToken();
        if (!localStringTokenizer.hasMoreTokens()) {
          return false;
        }
        String str2 = localStringTokenizer.nextToken();
        LocationArray localLocationArray = (LocationArray)paramMap.get(str1);
        if (localLocationArray == null)
        {
          localLocationArray = new LocationArray();
          paramMap.put(str1, localLocationArray);
        }
        localLocationArray.addLocation(str2);
      }
    }
    return true;
  }
  
  private void processJAXPSchemaSource(Map<String, LocationArray> paramMap)
    throws IOException
  {
    fJAXPProcessed = true;
    if (fJAXPSource == null) {
      return;
    }
    Class localClass = fJAXPSource.getClass().getComponentType();
    XMLInputSource localXMLInputSource = null;
    String str = null;
    if (localClass == null)
    {
      if (((fJAXPSource instanceof InputStream)) || ((fJAXPSource instanceof InputSource)))
      {
        localObject1 = (SchemaGrammar)fJAXPCache.get(fJAXPSource);
        if (localObject1 != null)
        {
          fGrammarBucket.putGrammar((SchemaGrammar)localObject1);
          return;
        }
      }
      fXSDDescription.reset();
      localXMLInputSource = xsdToXMLInputSource(fJAXPSource);
      str = localXMLInputSource.getSystemId();
      fXSDDescription.fContextType = 3;
      if (str != null)
      {
        fXSDDescription.setBaseSystemId(localXMLInputSource.getBaseSystemId());
        fXSDDescription.setLiteralSystemId(str);
        fXSDDescription.setExpandedSystemId(str);
        fXSDDescription.fLocationHints = new String[] { str };
      }
      localObject1 = loadSchema(fXSDDescription, localXMLInputSource, paramMap);
      if (localObject1 != null)
      {
        if (((fJAXPSource instanceof InputStream)) || ((fJAXPSource instanceof InputSource)))
        {
          fJAXPCache.put(fJAXPSource, localObject1);
          if (fIsCheckedFully) {
            XSConstraints.fullSchemaChecking(fGrammarBucket, fSubGroupHandler, fCMBuilder, fErrorReporter);
          }
        }
        fGrammarBucket.putGrammar((SchemaGrammar)localObject1);
      }
      return;
    }
    if ((localClass != Object.class) && (localClass != String.class) && (localClass != File.class) && (localClass != InputStream.class) && (localClass != InputSource.class)) {
      throw new XMLConfigurationException(Status.NOT_SUPPORTED, "\"http://java.sun.com/xml/jaxp/properties/schemaSource\" property cannot have an array of type {" + localClass.getName() + "}. Possible types of the array supported are Object, String, File, InputStream, InputSource.");
    }
    Object localObject1 = (Object[])fJAXPSource;
    Vector localVector = new Vector();
    for (int i = 0; i < localObject1.length; i++)
    {
      if (((localObject1[i] instanceof InputStream)) || ((localObject1[i] instanceof InputSource)))
      {
        localObject2 = (SchemaGrammar)fJAXPCache.get(localObject1[i]);
        if (localObject2 != null)
        {
          fGrammarBucket.putGrammar((SchemaGrammar)localObject2);
          continue;
        }
      }
      fXSDDescription.reset();
      localXMLInputSource = xsdToXMLInputSource(localObject1[i]);
      str = localXMLInputSource.getSystemId();
      fXSDDescription.fContextType = 3;
      if (str != null)
      {
        fXSDDescription.setBaseSystemId(localXMLInputSource.getBaseSystemId());
        fXSDDescription.setLiteralSystemId(str);
        fXSDDescription.setExpandedSystemId(str);
        fXSDDescription.fLocationHints = new String[] { str };
      }
      Object localObject2 = null;
      SchemaGrammar localSchemaGrammar = fSchemaHandler.parseSchema(localXMLInputSource, fXSDDescription, paramMap);
      if (fIsCheckedFully) {
        XSConstraints.fullSchemaChecking(fGrammarBucket, fSubGroupHandler, fCMBuilder, fErrorReporter);
      }
      if (localSchemaGrammar != null)
      {
        localObject2 = localSchemaGrammar.getTargetNamespace();
        if (localVector.contains(localObject2)) {
          throw new IllegalArgumentException(" When using array of Objects as the value of SCHEMA_SOURCE property , no two Schemas should share the same targetNamespace. ");
        }
        localVector.add(localObject2);
        if (((localObject1[i] instanceof InputStream)) || ((localObject1[i] instanceof InputSource))) {
          fJAXPCache.put(localObject1[i], localSchemaGrammar);
        }
        fGrammarBucket.putGrammar(localSchemaGrammar);
      }
    }
  }
  
  private XMLInputSource xsdToXMLInputSource(Object paramObject)
  {
    Object localObject1;
    Object localObject2;
    if ((paramObject instanceof String))
    {
      localObject1 = (String)paramObject;
      fXSDDescription.reset();
      fXSDDescription.setValues(null, (String)localObject1, null, null);
      localObject2 = null;
      try
      {
        localObject2 = fEntityManager.resolveEntity(fXSDDescription);
      }
      catch (IOException localIOException)
      {
        fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[] { localObject1 }, (short)1);
      }
      if (localObject2 == null) {
        return new XMLInputSource(null, (String)localObject1, null);
      }
      return (XMLInputSource)localObject2;
    }
    if ((paramObject instanceof InputSource)) {
      return saxToXMLInputSource((InputSource)paramObject);
    }
    if ((paramObject instanceof InputStream)) {
      return new XMLInputSource(null, null, null, (InputStream)paramObject, null);
    }
    if ((paramObject instanceof File))
    {
      localObject1 = (File)paramObject;
      localObject2 = null;
      try
      {
        localObject2 = new BufferedInputStream(new FileInputStream((File)localObject1));
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[] { ((File)localObject1).toString() }, (short)1);
      }
      return new XMLInputSource(null, null, null, (InputStream)localObject2, null);
    }
    throw new XMLConfigurationException(Status.NOT_SUPPORTED, "\"http://java.sun.com/xml/jaxp/properties/schemaSource\" property cannot have a value of type {" + paramObject.getClass().getName() + "}. Possible types of the value supported are String, File, InputStream, InputSource OR an array of these types.");
  }
  
  private static XMLInputSource saxToXMLInputSource(InputSource paramInputSource)
  {
    String str1 = paramInputSource.getPublicId();
    String str2 = paramInputSource.getSystemId();
    Reader localReader = paramInputSource.getCharacterStream();
    if (localReader != null) {
      return new XMLInputSource(str1, str2, null, localReader, null);
    }
    InputStream localInputStream = paramInputSource.getByteStream();
    if (localInputStream != null) {
      return new XMLInputSource(str1, str2, null, localInputStream, paramInputSource.getEncoding());
    }
    return new XMLInputSource(str1, str2, null);
  }
  
  public Boolean getFeatureDefault(String paramString)
  {
    if (paramString.equals("http://apache.org/xml/features/validation/schema/augment-psvi")) {
      return Boolean.TRUE;
    }
    return null;
  }
  
  public Object getPropertyDefault(String paramString)
  {
    return null;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException
  {
    XMLSecurityPropertyManager localXMLSecurityPropertyManager = (XMLSecurityPropertyManager)paramXMLComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
    if (localXMLSecurityPropertyManager == null)
    {
      localXMLSecurityPropertyManager = new XMLSecurityPropertyManager();
      setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", localXMLSecurityPropertyManager);
    }
    XMLSecurityManager localXMLSecurityManager = (XMLSecurityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
    if (localXMLSecurityManager == null) {
      setProperty("http://apache.org/xml/properties/security-manager", new XMLSecurityManager(true));
    }
    faccessExternalSchema = localXMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA);
    fGrammarBucket.reset();
    fSubGroupHandler.reset();
    boolean bool1 = paramXMLComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
    if ((!bool1) || (!fSettingsChanged))
    {
      fJAXPProcessed = false;
      initGrammarBucket();
      return;
    }
    fNodeFactory.reset(paramXMLComponentManager);
    fEntityManager = ((XMLEntityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager"));
    fErrorReporter = ((XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    SchemaDVFactory localSchemaDVFactory = null;
    localSchemaDVFactory = fSchemaHandler.getDVFactory();
    if (localSchemaDVFactory == null)
    {
      localSchemaDVFactory = SchemaDVFactory.getInstance();
      fSchemaHandler.setDVFactory(localSchemaDVFactory);
    }
    boolean bool2 = paramXMLComponentManager.getFeature("http://apache.org/xml/features/validation/schema/augment-psvi", false);
    if (!bool2)
    {
      if (fDeclPool != null) {
        fDeclPool.reset();
      } else {
        fDeclPool = new XSDeclarationPool();
      }
      fCMBuilder.setDeclPool(fDeclPool);
      fSchemaHandler.setDeclPool(fDeclPool);
      if ((localSchemaDVFactory instanceof SchemaDVFactoryImpl))
      {
        fDeclPool.setDVFactory((SchemaDVFactoryImpl)localSchemaDVFactory);
        ((SchemaDVFactoryImpl)localSchemaDVFactory).setDeclPool(fDeclPool);
      }
    }
    else
    {
      fCMBuilder.setDeclPool(null);
      fSchemaHandler.setDeclPool(null);
    }
    try
    {
      fExternalSchemas = ((String)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/schema/external-schemaLocation"));
      fExternalNoNSSchema = ((String)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation"));
    }
    catch (XMLConfigurationException localXMLConfigurationException1)
    {
      fExternalSchemas = null;
      fExternalNoNSSchema = null;
    }
    fJAXPSource = paramXMLComponentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", null);
    fJAXPProcessed = false;
    fGrammarPool = ((XMLGrammarPool)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/grammar-pool", null));
    initGrammarBucket();
    try
    {
      boolean bool3 = paramXMLComponentManager.getFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
      if (!bool3) {
        fErrorReporter.setFeature("http://apache.org/xml/features/continue-after-fatal-error", bool3);
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException2) {}
    fIsCheckedFully = paramXMLComponentManager.getFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
    fSchemaHandler.setGenerateSyntheticAnnotations(paramXMLComponentManager.getFeature("http://apache.org/xml/features/generate-synthetic-annotations", false));
    fSchemaHandler.reset(paramXMLComponentManager);
  }
  
  private void initGrammarBucket()
  {
    if (fGrammarPool != null)
    {
      Grammar[] arrayOfGrammar = fGrammarPool.retrieveInitialGrammarSet("http://www.w3.org/2001/XMLSchema");
      for (int i = 0; i < arrayOfGrammar.length; i++) {
        if (!fGrammarBucket.putGrammar((SchemaGrammar)arrayOfGrammar[i], true)) {
          fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "GrammarConflict", null, (short)0);
        }
      }
    }
  }
  
  public DOMConfiguration getConfig()
  {
    return this;
  }
  
  public XSModel load(LSInput paramLSInput)
  {
    try
    {
      Grammar localGrammar = loadGrammar(dom2xmlInputSource(paramLSInput));
      return ((XSGrammar)localGrammar).toXSModel();
    }
    catch (Exception localException)
    {
      reportDOMFatalError(localException);
    }
    return null;
  }
  
  public XSModel loadInputList(LSInputList paramLSInputList)
  {
    int i = paramLSInputList.getLength();
    SchemaGrammar[] arrayOfSchemaGrammar = new SchemaGrammar[i];
    for (int j = 0; j < i; j++) {
      try
      {
        arrayOfSchemaGrammar[j] = ((SchemaGrammar)loadGrammar(dom2xmlInputSource(paramLSInputList.item(j))));
      }
      catch (Exception localException)
      {
        reportDOMFatalError(localException);
        return null;
      }
    }
    return new XSModelImpl(arrayOfSchemaGrammar);
  }
  
  public XSModel loadURI(String paramString)
  {
    try
    {
      Grammar localGrammar = loadGrammar(new XMLInputSource(null, paramString, null));
      return ((XSGrammar)localGrammar).toXSModel();
    }
    catch (Exception localException)
    {
      reportDOMFatalError(localException);
    }
    return null;
  }
  
  public XSModel loadURIList(StringList paramStringList)
  {
    int i = paramStringList.getLength();
    SchemaGrammar[] arrayOfSchemaGrammar = new SchemaGrammar[i];
    for (int j = 0; j < i; j++) {
      try
      {
        arrayOfSchemaGrammar[j] = ((SchemaGrammar)loadGrammar(new XMLInputSource(null, paramStringList.item(j), null)));
      }
      catch (Exception localException)
      {
        reportDOMFatalError(localException);
        return null;
      }
    }
    return new XSModelImpl(arrayOfSchemaGrammar);
  }
  
  void reportDOMFatalError(Exception paramException)
  {
    if (fErrorHandler != null)
    {
      DOMErrorImpl localDOMErrorImpl = new DOMErrorImpl();
      fException = paramException;
      fMessage = paramException.getMessage();
      fSeverity = 3;
      fErrorHandler.getErrorHandler().handleError(localDOMErrorImpl);
    }
  }
  
  public boolean canSetParameter(String paramString, Object paramObject)
  {
    if ((paramObject instanceof Boolean)) {
      return (paramString.equals("validate")) || (paramString.equals("http://apache.org/xml/features/validation/schema-full-checking")) || (paramString.equals("http://apache.org/xml/features/validate-annotations")) || (paramString.equals("http://apache.org/xml/features/continue-after-fatal-error")) || (paramString.equals("http://apache.org/xml/features/allow-java-encodings")) || (paramString.equals("http://apache.org/xml/features/standard-uri-conformant")) || (paramString.equals("http://apache.org/xml/features/generate-synthetic-annotations")) || (paramString.equals("http://apache.org/xml/features/honour-all-schemaLocations")) || (paramString.equals("http://apache.org/xml/features/namespace-growth")) || (paramString.equals("http://apache.org/xml/features/internal/tolerate-duplicates")) || (paramString.equals("http://www.oracle.com/feature/use-service-mechanism"));
    }
    return (paramString.equals("error-handler")) || (paramString.equals("resource-resolver")) || (paramString.equals("http://apache.org/xml/properties/internal/symbol-table")) || (paramString.equals("http://apache.org/xml/properties/internal/error-reporter")) || (paramString.equals("http://apache.org/xml/properties/internal/error-handler")) || (paramString.equals("http://apache.org/xml/properties/internal/entity-resolver")) || (paramString.equals("http://apache.org/xml/properties/internal/grammar-pool")) || (paramString.equals("http://apache.org/xml/properties/schema/external-schemaLocation")) || (paramString.equals("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation")) || (paramString.equals("http://java.sun.com/xml/jaxp/properties/schemaSource")) || (paramString.equals("http://apache.org/xml/properties/internal/validation/schema/dv-factory"));
  }
  
  public Object getParameter(String paramString)
    throws DOMException
  {
    if (paramString.equals("error-handler")) {
      return fErrorHandler != null ? fErrorHandler.getErrorHandler() : null;
    }
    if (paramString.equals("resource-resolver")) {
      return fResourceResolver != null ? fResourceResolver.getEntityResolver() : null;
    }
    try
    {
      boolean bool = getFeature(paramString);
      return bool ? Boolean.TRUE : Boolean.FALSE;
    }
    catch (Exception localException1)
    {
      try
      {
        Object localObject = getProperty(paramString);
        return localObject;
      }
      catch (Exception localException2)
      {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
        throw new DOMException((short)9, str);
      }
    }
  }
  
  public DOMStringList getParameterNames()
  {
    if (fRecognizedParameters == null)
    {
      Vector localVector = new Vector();
      localVector.add("validate");
      localVector.add("error-handler");
      localVector.add("resource-resolver");
      localVector.add("http://apache.org/xml/properties/internal/symbol-table");
      localVector.add("http://apache.org/xml/properties/internal/error-reporter");
      localVector.add("http://apache.org/xml/properties/internal/error-handler");
      localVector.add("http://apache.org/xml/properties/internal/entity-resolver");
      localVector.add("http://apache.org/xml/properties/internal/grammar-pool");
      localVector.add("http://apache.org/xml/properties/schema/external-schemaLocation");
      localVector.add("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation");
      localVector.add("http://java.sun.com/xml/jaxp/properties/schemaSource");
      localVector.add("http://apache.org/xml/features/validation/schema-full-checking");
      localVector.add("http://apache.org/xml/features/continue-after-fatal-error");
      localVector.add("http://apache.org/xml/features/allow-java-encodings");
      localVector.add("http://apache.org/xml/features/standard-uri-conformant");
      localVector.add("http://apache.org/xml/features/validate-annotations");
      localVector.add("http://apache.org/xml/features/generate-synthetic-annotations");
      localVector.add("http://apache.org/xml/features/honour-all-schemaLocations");
      localVector.add("http://apache.org/xml/features/namespace-growth");
      localVector.add("http://apache.org/xml/features/internal/tolerate-duplicates");
      localVector.add("http://www.oracle.com/feature/use-service-mechanism");
      fRecognizedParameters = new DOMStringListImpl(localVector);
    }
    return fRecognizedParameters;
  }
  
  public void setParameter(String paramString, Object paramObject)
    throws DOMException
  {
    if ((paramObject instanceof Boolean))
    {
      boolean bool = ((Boolean)paramObject).booleanValue();
      if ((paramString.equals("validate")) && (bool)) {
        return;
      }
      try
      {
        setFeature(paramString, bool);
      }
      catch (Exception localException2)
      {
        String str4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
        throw new DOMException((short)9, str4);
      }
      return;
    }
    if (paramString.equals("error-handler"))
    {
      if ((paramObject instanceof DOMErrorHandler))
      {
        try
        {
          fErrorHandler = new DOMErrorHandlerWrapper((DOMErrorHandler)paramObject);
          setErrorHandler(fErrorHandler);
        }
        catch (XMLConfigurationException localXMLConfigurationException1) {}
      }
      else
      {
        String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
        throw new DOMException((short)9, str1);
      }
      return;
    }
    if (paramString.equals("resource-resolver"))
    {
      if ((paramObject instanceof LSResourceResolver))
      {
        try
        {
          fResourceResolver = new DOMEntityResolverWrapper((LSResourceResolver)paramObject);
          setEntityResolver(fResourceResolver);
        }
        catch (XMLConfigurationException localXMLConfigurationException2) {}
      }
      else
      {
        String str2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
        throw new DOMException((short)9, str2);
      }
      return;
    }
    try
    {
      setProperty(paramString, paramObject);
    }
    catch (Exception localException1)
    {
      String str3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramString });
      throw new DOMException((short)9, str3);
    }
  }
  
  XMLInputSource dom2xmlInputSource(LSInput paramLSInput)
  {
    XMLInputSource localXMLInputSource = null;
    if (paramLSInput.getCharacterStream() != null) {
      localXMLInputSource = new XMLInputSource(paramLSInput.getPublicId(), paramLSInput.getSystemId(), paramLSInput.getBaseURI(), paramLSInput.getCharacterStream(), "UTF-16");
    } else if (paramLSInput.getByteStream() != null) {
      localXMLInputSource = new XMLInputSource(paramLSInput.getPublicId(), paramLSInput.getSystemId(), paramLSInput.getBaseURI(), paramLSInput.getByteStream(), paramLSInput.getEncoding());
    } else if ((paramLSInput.getStringData() != null) && (paramLSInput.getStringData().length() != 0)) {
      localXMLInputSource = new XMLInputSource(paramLSInput.getPublicId(), paramLSInput.getSystemId(), paramLSInput.getBaseURI(), new StringReader(paramLSInput.getStringData()), "UTF-16");
    } else {
      localXMLInputSource = new XMLInputSource(paramLSInput.getPublicId(), paramLSInput.getSystemId(), paramLSInput.getBaseURI());
    }
    return localXMLInputSource;
  }
  
  public static class LocationArray
  {
    int length;
    String[] locations = new String[2];
    
    public LocationArray() {}
    
    public void resize(int paramInt1, int paramInt2)
    {
      String[] arrayOfString = new String[paramInt2];
      System.arraycopy(locations, 0, arrayOfString, 0, Math.min(paramInt1, paramInt2));
      locations = arrayOfString;
      length = Math.min(paramInt1, paramInt2);
    }
    
    public void addLocation(String paramString)
    {
      if (length >= locations.length) {
        resize(length, Math.max(1, length * 2));
      }
      locations[(length++)] = paramString;
    }
    
    public String[] getLocationArray()
    {
      if (length < locations.length) {
        resize(locations.length, length);
      }
      return locations;
    }
    
    public String getFirstLocation()
    {
      return length > 0 ? locations[0] : null;
    }
    
    public int getLength()
    {
      return length;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XMLSchemaLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */