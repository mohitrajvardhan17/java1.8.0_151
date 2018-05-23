package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.XMLDTDScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.XMLNSDocumentScannerImpl;
import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLPullParserConfiguration;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class NonValidatingConfiguration
  extends BasicParserConfiguration
  implements XMLPullParserConfiguration
{
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
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
  protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
  protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
  protected static final String NAMESPACE_BINDER = "http://apache.org/xml/properties/internal/namespace-binder";
  protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  protected static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
  protected static final String LOCALE = "http://apache.org/xml/properties/locale";
  protected static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  private static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
  protected XMLGrammarPool fGrammarPool;
  protected DTDDVFactory fDatatypeValidatorFactory;
  protected XMLErrorReporter fErrorReporter;
  protected XMLEntityManager fEntityManager;
  protected XMLDocumentScanner fScanner;
  protected XMLInputSource fInputSource;
  protected XMLDTDScanner fDTDScanner;
  protected ValidationManager fValidationManager;
  private XMLNSDocumentScannerImpl fNamespaceScanner;
  private XMLDocumentScannerImpl fNonNSScanner;
  protected boolean fConfigUpdated = false;
  protected XMLLocator fLocator;
  protected boolean fParseInProgress = false;
  
  public NonValidatingConfiguration()
  {
    this(null, null, null);
  }
  
  public NonValidatingConfiguration(SymbolTable paramSymbolTable)
  {
    this(paramSymbolTable, null, null);
  }
  
  public NonValidatingConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool)
  {
    this(paramSymbolTable, paramXMLGrammarPool, null);
  }
  
  public NonValidatingConfiguration(SymbolTable paramSymbolTable, XMLGrammarPool paramXMLGrammarPool, XMLComponentManager paramXMLComponentManager)
  {
    super(paramSymbolTable, paramXMLComponentManager);
    String[] arrayOfString1 = { "http://apache.org/xml/features/internal/parser-settings", "http://xml.org/sax/features/namespaces", "http://apache.org/xml/features/continue-after-fatal-error" };
    addRecognizedFeatures(arrayOfString1);
    fFeatures.put("http://apache.org/xml/features/continue-after-fatal-error", Boolean.FALSE);
    fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
    fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
    String[] arrayOfString2 = { "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/document-scanner", "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/internal/namespace-binder", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/locale", "http://apache.org/xml/properties/security-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
    addRecognizedProperties(arrayOfString2);
    fGrammarPool = paramXMLGrammarPool;
    if (fGrammarPool != null) {
      fProperties.put("http://apache.org/xml/properties/internal/grammar-pool", fGrammarPool);
    }
    fEntityManager = createEntityManager();
    fProperties.put("http://apache.org/xml/properties/internal/entity-manager", fEntityManager);
    addComponent(fEntityManager);
    fErrorReporter = createErrorReporter();
    fErrorReporter.setDocumentLocator(fEntityManager.getEntityScanner());
    fProperties.put("http://apache.org/xml/properties/internal/error-reporter", fErrorReporter);
    addComponent(fErrorReporter);
    fDTDScanner = createDTDScanner();
    if (fDTDScanner != null)
    {
      fProperties.put("http://apache.org/xml/properties/internal/dtd-scanner", fDTDScanner);
      if ((fDTDScanner instanceof XMLComponent)) {
        addComponent((XMLComponent)fDTDScanner);
      }
    }
    fDatatypeValidatorFactory = createDatatypeValidatorFactory();
    if (fDatatypeValidatorFactory != null) {
      fProperties.put("http://apache.org/xml/properties/internal/datatype-validator-factory", fDatatypeValidatorFactory);
    }
    fValidationManager = createValidationManager();
    if (fValidationManager != null) {
      fProperties.put("http://apache.org/xml/properties/internal/validation-manager", fValidationManager);
    }
    if (fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null)
    {
      XMLMessageFormatter localXMLMessageFormatter = new XMLMessageFormatter();
      fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", localXMLMessageFormatter);
      fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", localXMLMessageFormatter);
    }
    fConfigUpdated = false;
    try
    {
      setLocale(Locale.getDefault());
    }
    catch (XNIException localXNIException) {}
    setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", new XMLSecurityPropertyManager());
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {
    fConfigUpdated = true;
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
    super.setProperty(paramString, paramObject);
  }
  
  public void setLocale(Locale paramLocale)
    throws XNIException
  {
    super.setLocale(paramLocale);
    fErrorReporter.setLocale(paramLocale);
  }
  
  public FeatureState getFeatureState(String paramString)
    throws XMLConfigurationException
  {
    if (paramString.equals("http://apache.org/xml/features/internal/parser-settings")) {
      return FeatureState.is(fConfigUpdated);
    }
    return super.getFeatureState(paramString);
  }
  
  public void setInputSource(XMLInputSource paramXMLInputSource)
    throws XMLConfigurationException, IOException
  {
    fInputSource = paramXMLInputSource;
  }
  
  public boolean parse(boolean paramBoolean)
    throws XNIException, IOException
  {
    if (fInputSource != null) {
      try
      {
        reset();
        fScanner.setInputSource(fInputSource);
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
      return fScanner.scanDocument(paramBoolean);
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
  
  protected void reset()
    throws XNIException
  {
    if (fValidationManager != null) {
      fValidationManager.reset();
    }
    configurePipeline();
    super.reset();
  }
  
  protected void configurePipeline()
  {
    if (fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE)
    {
      if (fNamespaceScanner == null)
      {
        fNamespaceScanner = new XMLNSDocumentScannerImpl();
        addComponent(fNamespaceScanner);
      }
      fProperties.put("http://apache.org/xml/properties/internal/document-scanner", fNamespaceScanner);
      fNamespaceScanner.setDTDValidator(null);
      fScanner = fNamespaceScanner;
    }
    else
    {
      if (fNonNSScanner == null)
      {
        fNonNSScanner = new XMLDocumentScannerImpl();
        addComponent(fNonNSScanner);
      }
      fProperties.put("http://apache.org/xml/properties/internal/document-scanner", fNonNSScanner);
      fScanner = fNonNSScanner;
    }
    fScanner.setDocumentHandler(fDocumentHandler);
    fLastComponent = fScanner;
    if (fDTDScanner != null)
    {
      fDTDScanner.setDTDHandler(fDTDHandler);
      fDTDScanner.setDTDContentModelHandler(fDTDContentModelHandler);
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
    }
    if (paramString.startsWith("http://java.sun.com/xml/jaxp/properties/"))
    {
      i = paramString.length() - "http://java.sun.com/xml/jaxp/properties/".length();
      if ((i == "schemaSource".length()) && (paramString.endsWith("schemaSource"))) {
        return PropertyState.RECOGNIZED;
      }
    }
    return super.checkProperty(paramString);
  }
  
  protected XMLEntityManager createEntityManager()
  {
    return new XMLEntityManager();
  }
  
  protected XMLErrorReporter createErrorReporter()
  {
    return new XMLErrorReporter();
  }
  
  protected XMLDocumentScanner createDocumentScanner()
  {
    return null;
  }
  
  protected XMLDTDScanner createDTDScanner()
  {
    return new XMLDTDScannerImpl();
  }
  
  protected DTDDVFactory createDatatypeValidatorFactory()
  {
    return DTDDVFactory.getInstance();
  }
  
  protected ValidationManager createValidationManager()
  {
    return new ValidationManager();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\parsers\NonValidatingConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */