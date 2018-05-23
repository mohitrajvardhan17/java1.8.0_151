package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter;
import com.sun.org.apache.xerces.internal.util.DOMEntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.PropertyState;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.State;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager.Property;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager.State;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;

final class XMLSchemaValidatorComponentManager
  extends ParserConfigurationSettings
  implements XMLComponentManager
{
  private static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
  private static final String VALIDATION = "http://xml.org/sax/features/validation";
  private static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
  private static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
  private static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  private static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  private static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
  private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
  private static final String LOCALE = "http://apache.org/xml/properties/locale";
  private boolean _isSecureMode = false;
  private boolean fConfigUpdated = true;
  private boolean fUseGrammarPoolOnly;
  private final HashMap fComponents = new HashMap();
  private XMLEntityManager fEntityManager = new XMLEntityManager();
  private XMLErrorReporter fErrorReporter;
  private NamespaceContext fNamespaceContext;
  private XMLSchemaValidator fSchemaValidator;
  private ValidationManager fValidationManager;
  private final HashMap fInitFeatures = new HashMap();
  private final HashMap fInitProperties = new HashMap();
  private XMLSecurityManager fInitSecurityManager;
  private final XMLSecurityPropertyManager fSecurityPropertyMgr;
  private ErrorHandler fErrorHandler = null;
  private LSResourceResolver fResourceResolver = null;
  private Locale fLocale = null;
  
  public XMLSchemaValidatorComponentManager(XSGrammarPoolContainer paramXSGrammarPoolContainer)
  {
    fComponents.put("http://apache.org/xml/properties/internal/entity-manager", fEntityManager);
    fErrorReporter = new XMLErrorReporter();
    fComponents.put("http://apache.org/xml/properties/internal/error-reporter", fErrorReporter);
    fNamespaceContext = new NamespaceSupport();
    fComponents.put("http://apache.org/xml/properties/internal/namespace-context", fNamespaceContext);
    fSchemaValidator = new XMLSchemaValidator();
    fComponents.put("http://apache.org/xml/properties/internal/validator/schema", fSchemaValidator);
    fValidationManager = new ValidationManager();
    fComponents.put("http://apache.org/xml/properties/internal/validation-manager", fValidationManager);
    fComponents.put("http://apache.org/xml/properties/internal/entity-resolver", null);
    fComponents.put("http://apache.org/xml/properties/internal/error-handler", null);
    fComponents.put("http://apache.org/xml/properties/internal/symbol-table", new SymbolTable());
    fComponents.put("http://apache.org/xml/properties/internal/grammar-pool", paramXSGrammarPoolContainer.getGrammarPool());
    fUseGrammarPoolOnly = paramXSGrammarPoolContainer.isFullyComposed();
    fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
    addRecognizedParamsAndSetDefaults(fEntityManager, paramXSGrammarPoolContainer);
    addRecognizedParamsAndSetDefaults(fErrorReporter, paramXSGrammarPoolContainer);
    addRecognizedParamsAndSetDefaults(fSchemaValidator, paramXSGrammarPoolContainer);
    boolean bool = paramXSGrammarPoolContainer.getFeature("http://javax.xml.XMLConstants/feature/secure-processing").booleanValue();
    if (System.getSecurityManager() != null)
    {
      _isSecureMode = true;
      bool = true;
    }
    fInitSecurityManager = ((XMLSecurityManager)paramXSGrammarPoolContainer.getProperty("http://apache.org/xml/properties/security-manager"));
    if (fInitSecurityManager != null) {
      fInitSecurityManager.setSecureProcessing(bool);
    } else {
      fInitSecurityManager = new XMLSecurityManager(bool);
    }
    setProperty("http://apache.org/xml/properties/security-manager", fInitSecurityManager);
    fSecurityPropertyMgr = ((XMLSecurityPropertyManager)paramXSGrammarPoolContainer.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"));
    setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", fSecurityPropertyMgr);
  }
  
  public FeatureState getFeatureState(String paramString)
    throws XMLConfigurationException
  {
    if ("http://apache.org/xml/features/internal/parser-settings".equals(paramString)) {
      return FeatureState.is(fConfigUpdated);
    }
    if (("http://xml.org/sax/features/validation".equals(paramString)) || ("http://apache.org/xml/features/validation/schema".equals(paramString))) {
      return FeatureState.is(true);
    }
    if ("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only".equals(paramString)) {
      return FeatureState.is(fUseGrammarPoolOnly);
    }
    if ("http://javax.xml.XMLConstants/feature/secure-processing".equals(paramString)) {
      return FeatureState.is(fInitSecurityManager.isSecureProcessing());
    }
    if ("http://apache.org/xml/features/validation/schema/element-default".equals(paramString)) {
      return FeatureState.is(true);
    }
    return super.getFeatureState(paramString);
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {
    if ("http://apache.org/xml/features/internal/parser-settings".equals(paramString)) {
      throw new XMLConfigurationException(Status.NOT_SUPPORTED, paramString);
    }
    if ((!paramBoolean) && (("http://xml.org/sax/features/validation".equals(paramString)) || ("http://apache.org/xml/features/validation/schema".equals(paramString)))) {
      throw new XMLConfigurationException(Status.NOT_SUPPORTED, paramString);
    }
    if (("http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only".equals(paramString)) && (paramBoolean != fUseGrammarPoolOnly)) {
      throw new XMLConfigurationException(Status.NOT_SUPPORTED, paramString);
    }
    if ("http://javax.xml.XMLConstants/feature/secure-processing".equals(paramString))
    {
      if ((_isSecureMode) && (!paramBoolean)) {
        throw new XMLConfigurationException(Status.NOT_ALLOWED, "http://javax.xml.XMLConstants/feature/secure-processing");
      }
      fInitSecurityManager.setSecureProcessing(paramBoolean);
      setProperty("http://apache.org/xml/properties/security-manager", fInitSecurityManager);
      if ((paramBoolean) && (Constants.IS_JDK8_OR_ABOVE))
      {
        fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, XMLSecurityPropertyManager.State.FSP, "");
        fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, XMLSecurityPropertyManager.State.FSP, "");
        setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", fSecurityPropertyMgr);
      }
      return;
    }
    fConfigUpdated = true;
    fEntityManager.setFeature(paramString, paramBoolean);
    fErrorReporter.setFeature(paramString, paramBoolean);
    fSchemaValidator.setFeature(paramString, paramBoolean);
    if (!fInitFeatures.containsKey(paramString))
    {
      boolean bool = super.getFeature(paramString);
      fInitFeatures.put(paramString, bool ? Boolean.TRUE : Boolean.FALSE);
    }
    super.setFeature(paramString, paramBoolean);
  }
  
  public PropertyState getPropertyState(String paramString)
    throws XMLConfigurationException
  {
    if ("http://apache.org/xml/properties/locale".equals(paramString)) {
      return PropertyState.is(getLocale());
    }
    Object localObject = fComponents.get(paramString);
    if (localObject != null) {
      return PropertyState.is(localObject);
    }
    if (fComponents.containsKey(paramString)) {
      return PropertyState.is(null);
    }
    return super.getPropertyState(paramString);
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException
  {
    if (("http://apache.org/xml/properties/internal/entity-manager".equals(paramString)) || ("http://apache.org/xml/properties/internal/error-reporter".equals(paramString)) || ("http://apache.org/xml/properties/internal/namespace-context".equals(paramString)) || ("http://apache.org/xml/properties/internal/validator/schema".equals(paramString)) || ("http://apache.org/xml/properties/internal/symbol-table".equals(paramString)) || ("http://apache.org/xml/properties/internal/validation-manager".equals(paramString)) || ("http://apache.org/xml/properties/internal/grammar-pool".equals(paramString))) {
      throw new XMLConfigurationException(Status.NOT_SUPPORTED, paramString);
    }
    fConfigUpdated = true;
    fEntityManager.setProperty(paramString, paramObject);
    fErrorReporter.setProperty(paramString, paramObject);
    fSchemaValidator.setProperty(paramString, paramObject);
    if (("http://apache.org/xml/properties/internal/entity-resolver".equals(paramString)) || ("http://apache.org/xml/properties/internal/error-handler".equals(paramString)) || ("http://apache.org/xml/properties/security-manager".equals(paramString)))
    {
      fComponents.put(paramString, paramObject);
      return;
    }
    if ("http://apache.org/xml/properties/locale".equals(paramString))
    {
      setLocale((Locale)paramObject);
      fComponents.put(paramString, paramObject);
      return;
    }
    if (((fInitSecurityManager == null) || (!fInitSecurityManager.setLimit(paramString, XMLSecurityManager.State.APIPROPERTY, paramObject))) && ((fSecurityPropertyMgr == null) || (!fSecurityPropertyMgr.setValue(paramString, XMLSecurityPropertyManager.State.APIPROPERTY, paramObject))))
    {
      if (!fInitProperties.containsKey(paramString)) {
        fInitProperties.put(paramString, super.getProperty(paramString));
      }
      super.setProperty(paramString, paramObject);
    }
  }
  
  public void addRecognizedParamsAndSetDefaults(XMLComponent paramXMLComponent, XSGrammarPoolContainer paramXSGrammarPoolContainer)
  {
    String[] arrayOfString1 = paramXMLComponent.getRecognizedFeatures();
    addRecognizedFeatures(arrayOfString1);
    String[] arrayOfString2 = paramXMLComponent.getRecognizedProperties();
    addRecognizedProperties(arrayOfString2);
    setFeatureDefaults(paramXMLComponent, arrayOfString1, paramXSGrammarPoolContainer);
    setPropertyDefaults(paramXMLComponent, arrayOfString2);
  }
  
  public void reset()
    throws XNIException
  {
    fNamespaceContext.reset();
    fValidationManager.reset();
    fEntityManager.reset(this);
    fErrorReporter.reset(this);
    fSchemaValidator.reset(this);
    fConfigUpdated = false;
  }
  
  void setErrorHandler(ErrorHandler paramErrorHandler)
  {
    fErrorHandler = paramErrorHandler;
    setProperty("http://apache.org/xml/properties/internal/error-handler", paramErrorHandler != null ? new ErrorHandlerWrapper(paramErrorHandler) : new ErrorHandlerWrapper(DraconianErrorHandler.getInstance()));
  }
  
  ErrorHandler getErrorHandler()
  {
    return fErrorHandler;
  }
  
  void setResourceResolver(LSResourceResolver paramLSResourceResolver)
  {
    fResourceResolver = paramLSResourceResolver;
    setProperty("http://apache.org/xml/properties/internal/entity-resolver", new DOMEntityResolverWrapper(paramLSResourceResolver));
  }
  
  LSResourceResolver getResourceResolver()
  {
    return fResourceResolver;
  }
  
  void setLocale(Locale paramLocale)
  {
    fLocale = paramLocale;
    fErrorReporter.setLocale(paramLocale);
  }
  
  Locale getLocale()
  {
    return fLocale;
  }
  
  void restoreInitialState()
  {
    fConfigUpdated = true;
    fComponents.put("http://apache.org/xml/properties/internal/entity-resolver", null);
    fComponents.put("http://apache.org/xml/properties/internal/error-handler", null);
    setLocale(null);
    fComponents.put("http://apache.org/xml/properties/locale", null);
    fComponents.put("http://apache.org/xml/properties/security-manager", fInitSecurityManager);
    setLocale(null);
    fComponents.put("http://apache.org/xml/properties/locale", null);
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
  
  private void setFeatureDefaults(XMLComponent paramXMLComponent, String[] paramArrayOfString, XSGrammarPoolContainer paramXSGrammarPoolContainer)
  {
    if (paramArrayOfString != null) {
      for (int i = 0; i < paramArrayOfString.length; i++)
      {
        String str = paramArrayOfString[i];
        Boolean localBoolean = paramXSGrammarPoolContainer.getFeature(str);
        if (localBoolean == null) {
          localBoolean = paramXMLComponent.getFeatureDefault(str);
        }
        if ((localBoolean != null) && (!fFeatures.containsKey(str)))
        {
          fFeatures.put(str, localBoolean);
          fConfigUpdated = true;
        }
      }
    }
  }
  
  private void setPropertyDefaults(XMLComponent paramXMLComponent, String[] paramArrayOfString)
  {
    if (paramArrayOfString != null) {
      for (int i = 0; i < paramArrayOfString.length; i++)
      {
        String str = paramArrayOfString[i];
        Object localObject = paramXMLComponent.getPropertyDefault(str);
        if ((localObject != null) && (!fProperties.containsKey(str)))
        {
          fProperties.put(str, localObject);
          fConfigUpdated = true;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\XMLSchemaValidatorComponentManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */