package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.HTTPInputSource;
import com.sun.org.apache.xerces.internal.util.IntStack;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xpointer.XPointerHandler;
import com.sun.org.apache.xerces.internal.xpointer.XPointerProcessor;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Objects;
import java.util.Stack;
import java.util.StringTokenizer;

public class XIncludeHandler
  implements XMLComponent, XMLDocumentFilter, XMLDTDFilter
{
  public static final String XINCLUDE_DEFAULT_CONFIGURATION = "com.sun.org.apache.xerces.internal.parsers.XIncludeParserConfiguration";
  public static final String HTTP_ACCEPT = "Accept";
  public static final String HTTP_ACCEPT_LANGUAGE = "Accept-Language";
  public static final String XPOINTER = "xpointer";
  public static final String XINCLUDE_NS_URI = "http://www.w3.org/2001/XInclude".intern();
  public static final String XINCLUDE_INCLUDE = "include".intern();
  public static final String XINCLUDE_FALLBACK = "fallback".intern();
  public static final String XINCLUDE_PARSE_XML = "xml".intern();
  public static final String XINCLUDE_PARSE_TEXT = "text".intern();
  public static final String XINCLUDE_ATTR_HREF = "href".intern();
  public static final String XINCLUDE_ATTR_PARSE = "parse".intern();
  public static final String XINCLUDE_ATTR_ENCODING = "encoding".intern();
  public static final String XINCLUDE_ATTR_ACCEPT = "accept".intern();
  public static final String XINCLUDE_ATTR_ACCEPT_LANGUAGE = "accept-language".intern();
  public static final String XINCLUDE_INCLUDED = "[included]".intern();
  public static final String CURRENT_BASE_URI = "currentBaseURI";
  public static final String XINCLUDE_BASE = "base".intern();
  public static final QName XML_BASE_QNAME = new QName(XMLSymbols.PREFIX_XML, XINCLUDE_BASE, (XMLSymbols.PREFIX_XML + ":" + XINCLUDE_BASE).intern(), NamespaceContext.XML_URI);
  public static final String XINCLUDE_LANG = "lang".intern();
  public static final QName XML_LANG_QNAME = new QName(XMLSymbols.PREFIX_XML, XINCLUDE_LANG, (XMLSymbols.PREFIX_XML + ":" + XINCLUDE_LANG).intern(), NamespaceContext.XML_URI);
  public static final QName NEW_NS_ATTR_QNAME = new QName(XMLSymbols.PREFIX_XMLNS, "", XMLSymbols.PREFIX_XMLNS + ":", NamespaceContext.XMLNS_URI);
  private static final int STATE_NORMAL_PROCESSING = 1;
  private static final int STATE_IGNORE = 2;
  private static final int STATE_EXPECT_FALLBACK = 3;
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  protected static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
  protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
  protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
  protected static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
  protected static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  public static final String BUFFER_SIZE = "http://apache.org/xml/properties/input-buffer-size";
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  protected static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/allow-dtd-events-after-endDTD", "http://apache.org/xml/features/xinclude/fixup-base-uris", "http://apache.org/xml/features/xinclude/fixup-language" };
  private static final Boolean[] FEATURE_DEFAULTS = { Boolean.TRUE, Boolean.TRUE, Boolean.TRUE };
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/security-manager", "http://apache.org/xml/properties/input-buffer-size" };
  private static final Object[] PROPERTY_DEFAULTS = { null, null, null, new Integer(8192) };
  protected XMLDocumentHandler fDocumentHandler;
  protected XMLDocumentSource fDocumentSource;
  protected XMLDTDHandler fDTDHandler;
  protected XMLDTDSource fDTDSource;
  protected XIncludeHandler fParentXIncludeHandler;
  protected int fBufferSize = 8192;
  protected String fParentRelativeURI;
  protected XMLParserConfiguration fChildConfig;
  protected XMLParserConfiguration fXIncludeChildConfig;
  protected XMLParserConfiguration fXPointerChildConfig;
  protected XPointerProcessor fXPtrProcessor = null;
  protected XMLLocator fDocLocation;
  protected XIncludeMessageFormatter fXIncludeMessageFormatter = new XIncludeMessageFormatter();
  protected XIncludeNamespaceSupport fNamespaceContext;
  protected SymbolTable fSymbolTable;
  protected XMLErrorReporter fErrorReporter;
  protected XMLEntityResolver fEntityResolver;
  protected XMLSecurityManager fSecurityManager;
  protected XMLSecurityPropertyManager fSecurityPropertyMgr;
  protected XIncludeTextReader fXInclude10TextReader;
  protected XIncludeTextReader fXInclude11TextReader;
  protected XMLResourceIdentifier fCurrentBaseURI;
  protected IntStack fBaseURIScope;
  protected Stack fBaseURI;
  protected Stack fLiteralSystemID;
  protected Stack fExpandedSystemID;
  protected IntStack fLanguageScope;
  protected Stack fLanguageStack;
  protected String fCurrentLanguage;
  protected ParserConfigurationSettings fSettings;
  private int fDepth = 0;
  private int fResultDepth;
  private static final int INITIAL_SIZE = 8;
  private boolean[] fSawInclude = new boolean[8];
  private boolean[] fSawFallback = new boolean[8];
  private int[] fState = new int[8];
  private ArrayList fNotations;
  private ArrayList fUnparsedEntities;
  private boolean fFixupBaseURIs = true;
  private boolean fFixupLanguage = true;
  private boolean fSendUEAndNotationEvents;
  private boolean fIsXML11;
  private boolean fInDTD;
  private boolean fSeenRootElement;
  private boolean fNeedCopyFeatures = true;
  private static final boolean[] gNeedEscaping = new boolean[''];
  private static final char[] gAfterEscaping1 = new char[''];
  private static final char[] gAfterEscaping2 = new char[''];
  private static final char[] gHexChs = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  
  public XIncludeHandler()
  {
    fSawFallback[fDepth] = false;
    fSawInclude[fDepth] = false;
    fState[fDepth] = 1;
    fNotations = new ArrayList();
    fUnparsedEntities = new ArrayList();
    fBaseURIScope = new IntStack();
    fBaseURI = new Stack();
    fLiteralSystemID = new Stack();
    fExpandedSystemID = new Stack();
    fCurrentBaseURI = new XMLResourceIdentifierImpl();
    fLanguageScope = new IntStack();
    fLanguageStack = new Stack();
    fCurrentLanguage = null;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XNIException
  {
    fNamespaceContext = null;
    fDepth = 0;
    fResultDepth = (isRootDocument() ? 0 : fParentXIncludeHandler.getResultDepth());
    fNotations.clear();
    fUnparsedEntities.clear();
    fParentRelativeURI = null;
    fIsXML11 = false;
    fInDTD = false;
    fSeenRootElement = false;
    fBaseURIScope.clear();
    fBaseURI.clear();
    fLiteralSystemID.clear();
    fExpandedSystemID.clear();
    fLanguageScope.clear();
    fLanguageStack.clear();
    for (int i = 0; i < fState.length; i++) {
      fState[i] = 1;
    }
    for (i = 0; i < fSawFallback.length; i++) {
      fSawFallback[i] = false;
    }
    for (i = 0; i < fSawInclude.length; i++) {
      fSawInclude[i] = false;
    }
    try
    {
      if (!paramXMLComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings")) {
        return;
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException1) {}
    fNeedCopyFeatures = true;
    try
    {
      fSendUEAndNotationEvents = paramXMLComponentManager.getFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD");
      if (fChildConfig != null) {
        fChildConfig.setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", fSendUEAndNotationEvents);
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException2) {}
    try
    {
      fFixupBaseURIs = paramXMLComponentManager.getFeature("http://apache.org/xml/features/xinclude/fixup-base-uris");
      if (fChildConfig != null) {
        fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", fFixupBaseURIs);
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException3)
    {
      fFixupBaseURIs = true;
    }
    try
    {
      fFixupLanguage = paramXMLComponentManager.getFeature("http://apache.org/xml/features/xinclude/fixup-language");
      if (fChildConfig != null) {
        fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-language", fFixupLanguage);
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException4)
    {
      fFixupLanguage = true;
    }
    try
    {
      SymbolTable localSymbolTable = (SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
      if (localSymbolTable != null)
      {
        fSymbolTable = localSymbolTable;
        if (fChildConfig != null) {
          fChildConfig.setProperty("http://apache.org/xml/properties/internal/symbol-table", localSymbolTable);
        }
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException5)
    {
      fSymbolTable = null;
    }
    try
    {
      XMLErrorReporter localXMLErrorReporter = (XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
      if (localXMLErrorReporter != null)
      {
        setErrorReporter(localXMLErrorReporter);
        if (fChildConfig != null) {
          fChildConfig.setProperty("http://apache.org/xml/properties/internal/error-reporter", localXMLErrorReporter);
        }
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException6)
    {
      fErrorReporter = null;
    }
    try
    {
      XMLEntityResolver localXMLEntityResolver = (XMLEntityResolver)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
      if (localXMLEntityResolver != null)
      {
        fEntityResolver = localXMLEntityResolver;
        if (fChildConfig != null) {
          fChildConfig.setProperty("http://apache.org/xml/properties/internal/entity-resolver", localXMLEntityResolver);
        }
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException7)
    {
      fEntityResolver = null;
    }
    try
    {
      XMLSecurityManager localXMLSecurityManager = (XMLSecurityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
      if (localXMLSecurityManager != null)
      {
        fSecurityManager = localXMLSecurityManager;
        if (fChildConfig != null) {
          fChildConfig.setProperty("http://apache.org/xml/properties/security-manager", localXMLSecurityManager);
        }
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException8)
    {
      fSecurityManager = null;
    }
    fSecurityPropertyMgr = ((XMLSecurityPropertyManager)paramXMLComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"));
    try
    {
      Integer localInteger = (Integer)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/input-buffer-size");
      if ((localInteger != null) && (localInteger.intValue() > 0))
      {
        fBufferSize = localInteger.intValue();
        if (fChildConfig != null) {
          fChildConfig.setProperty("http://apache.org/xml/properties/input-buffer-size", localInteger);
        }
      }
      else
      {
        fBufferSize = ((Integer)getPropertyDefault("http://apache.org/xml/properties/input-buffer-size")).intValue();
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException9)
    {
      fBufferSize = ((Integer)getPropertyDefault("http://apache.org/xml/properties/input-buffer-size")).intValue();
    }
    if (fXInclude10TextReader != null) {
      fXInclude10TextReader.setBufferSize(fBufferSize);
    }
    if (fXInclude11TextReader != null) {
      fXInclude11TextReader.setBufferSize(fBufferSize);
    }
    fSettings = new ParserConfigurationSettings();
    copyFeatures(paramXMLComponentManager, fSettings);
    try
    {
      if (paramXMLComponentManager.getFeature("http://apache.org/xml/features/validation/schema"))
      {
        fSettings.setFeature("http://apache.org/xml/features/validation/schema", false);
        if (paramXMLComponentManager.getFeature("http://xml.org/sax/features/validation")) {
          fSettings.setFeature("http://apache.org/xml/features/validation/dynamic", true);
        }
      }
    }
    catch (XMLConfigurationException localXMLConfigurationException10) {}
  }
  
  public String[] getRecognizedFeatures()
  {
    return (String[])RECOGNIZED_FEATURES.clone();
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {
    if (paramString.equals("http://xml.org/sax/features/allow-dtd-events-after-endDTD")) {
      fSendUEAndNotationEvents = paramBoolean;
    }
    if (fSettings != null)
    {
      fNeedCopyFeatures = true;
      fSettings.setFeature(paramString, paramBoolean);
    }
  }
  
  public String[] getRecognizedProperties()
  {
    return (String[])RECOGNIZED_PROPERTIES.clone();
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException
  {
    if (paramString.equals("http://apache.org/xml/properties/internal/symbol-table"))
    {
      fSymbolTable = ((SymbolTable)paramObject);
      if (fChildConfig != null) {
        fChildConfig.setProperty(paramString, paramObject);
      }
      return;
    }
    if (paramString.equals("http://apache.org/xml/properties/internal/error-reporter"))
    {
      setErrorReporter((XMLErrorReporter)paramObject);
      if (fChildConfig != null) {
        fChildConfig.setProperty(paramString, paramObject);
      }
      return;
    }
    if (paramString.equals("http://apache.org/xml/properties/internal/entity-resolver"))
    {
      fEntityResolver = ((XMLEntityResolver)paramObject);
      if (fChildConfig != null) {
        fChildConfig.setProperty(paramString, paramObject);
      }
      return;
    }
    if (paramString.equals("http://apache.org/xml/properties/security-manager"))
    {
      fSecurityManager = ((XMLSecurityManager)paramObject);
      if (fChildConfig != null) {
        fChildConfig.setProperty(paramString, paramObject);
      }
      return;
    }
    if (paramString.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"))
    {
      fSecurityPropertyMgr = ((XMLSecurityPropertyManager)paramObject);
      if (fChildConfig != null) {
        fChildConfig.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", paramObject);
      }
      return;
    }
    if (paramString.equals("http://apache.org/xml/properties/input-buffer-size"))
    {
      Integer localInteger = (Integer)paramObject;
      if (fChildConfig != null) {
        fChildConfig.setProperty(paramString, paramObject);
      }
      if ((localInteger != null) && (localInteger.intValue() > 0))
      {
        fBufferSize = localInteger.intValue();
        if (fXInclude10TextReader != null) {
          fXInclude10TextReader.setBufferSize(fBufferSize);
        }
        if (fXInclude11TextReader != null) {
          fXInclude11TextReader.setBufferSize(fBufferSize);
        }
      }
      return;
    }
  }
  
  public Boolean getFeatureDefault(String paramString)
  {
    for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
      if (RECOGNIZED_FEATURES[i].equals(paramString)) {
        return FEATURE_DEFAULTS[i];
      }
    }
    return null;
  }
  
  public Object getPropertyDefault(String paramString)
  {
    for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
      if (RECOGNIZED_PROPERTIES[i].equals(paramString)) {
        return PROPERTY_DEFAULTS[i];
      }
    }
    return null;
  }
  
  public void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler)
  {
    fDocumentHandler = paramXMLDocumentHandler;
  }
  
  public XMLDocumentHandler getDocumentHandler()
  {
    return fDocumentHandler;
  }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations)
    throws XNIException
  {
    fErrorReporter.setDocumentLocator(paramXMLLocator);
    if ((!isRootDocument()) && (fParentXIncludeHandler.searchForRecursiveIncludes(paramXMLLocator))) {
      reportFatalError("RecursiveInclude", new Object[] { paramXMLLocator.getExpandedSystemId() });
    }
    if (!(paramNamespaceContext instanceof XIncludeNamespaceSupport)) {
      reportFatalError("IncompatibleNamespaceContext");
    }
    fNamespaceContext = ((XIncludeNamespaceSupport)paramNamespaceContext);
    fDocLocation = paramXMLLocator;
    fCurrentBaseURI.setBaseSystemId(paramXMLLocator.getBaseSystemId());
    fCurrentBaseURI.setExpandedSystemId(paramXMLLocator.getExpandedSystemId());
    fCurrentBaseURI.setLiteralSystemId(paramXMLLocator.getLiteralSystemId());
    saveBaseURI();
    if (paramAugmentations == null) {
      paramAugmentations = new AugmentationsImpl();
    }
    paramAugmentations.putItem("currentBaseURI", fCurrentBaseURI);
    fCurrentLanguage = XMLSymbols.EMPTY_STRING;
    saveLanguage(fCurrentLanguage);
    if ((isRootDocument()) && (fDocumentHandler != null)) {
      fDocumentHandler.startDocument(paramXMLLocator, paramString, paramNamespaceContext, paramAugmentations);
    }
  }
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations)
    throws XNIException
  {
    fIsXML11 = "1.1".equals(paramString1);
    if ((isRootDocument()) && (fDocumentHandler != null)) {
      fDocumentHandler.xmlDecl(paramString1, paramString2, paramString3, paramAugmentations);
    }
  }
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((isRootDocument()) && (fDocumentHandler != null)) {
      fDocumentHandler.doctypeDecl(paramString1, paramString2, paramString3, paramAugmentations);
    }
  }
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if (!fInDTD)
    {
      if ((fDocumentHandler != null) && (getState() == 1))
      {
        fDepth += 1;
        paramAugmentations = modifyAugmentations(paramAugmentations);
        fDocumentHandler.comment(paramXMLString, paramAugmentations);
        fDepth -= 1;
      }
    }
    else if (fDTDHandler != null) {
      fDTDHandler.comment(paramXMLString, paramAugmentations);
    }
  }
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if (!fInDTD)
    {
      if ((fDocumentHandler != null) && (getState() == 1))
      {
        fDepth += 1;
        paramAugmentations = modifyAugmentations(paramAugmentations);
        fDocumentHandler.processingInstruction(paramString, paramXMLString, paramAugmentations);
        fDepth -= 1;
      }
    }
    else if (fDTDHandler != null) {
      fDTDHandler.processingInstruction(paramString, paramXMLString, paramAugmentations);
    }
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    fDepth += 1;
    int i = getState(fDepth - 1);
    if ((i == 3) && (getState(fDepth - 2) == 3)) {
      setState(2);
    } else {
      setState(i);
    }
    processXMLBaseAttributes(paramXMLAttributes);
    if (fFixupLanguage) {
      processXMLLangAttributes(paramXMLAttributes);
    }
    if (isIncludeElement(paramQName))
    {
      boolean bool = handleIncludeElement(paramXMLAttributes);
      if (bool) {
        setState(2);
      } else {
        setState(3);
      }
    }
    else if (isFallbackElement(paramQName))
    {
      handleFallbackElement();
    }
    else if (hasXIncludeNamespace(paramQName))
    {
      if (getSawInclude(fDepth - 1)) {
        reportFatalError("IncludeChild", new Object[] { rawname });
      }
      if (getSawFallback(fDepth - 1)) {
        reportFatalError("FallbackChild", new Object[] { rawname });
      }
      if (getState() == 1)
      {
        if (fResultDepth++ == 0) {
          checkMultipleRootElements();
        }
        if (fDocumentHandler != null)
        {
          paramAugmentations = modifyAugmentations(paramAugmentations);
          paramXMLAttributes = processAttributes(paramXMLAttributes);
          fDocumentHandler.startElement(paramQName, paramXMLAttributes, paramAugmentations);
        }
      }
    }
    else if (getState() == 1)
    {
      if (fResultDepth++ == 0) {
        checkMultipleRootElements();
      }
      if (fDocumentHandler != null)
      {
        paramAugmentations = modifyAugmentations(paramAugmentations);
        paramXMLAttributes = processAttributes(paramXMLAttributes);
        fDocumentHandler.startElement(paramQName, paramXMLAttributes, paramAugmentations);
      }
    }
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    fDepth += 1;
    int i = getState(fDepth - 1);
    if ((i == 3) && (getState(fDepth - 2) == 3)) {
      setState(2);
    } else {
      setState(i);
    }
    processXMLBaseAttributes(paramXMLAttributes);
    if (fFixupLanguage) {
      processXMLLangAttributes(paramXMLAttributes);
    }
    if (isIncludeElement(paramQName))
    {
      boolean bool = handleIncludeElement(paramXMLAttributes);
      if (bool) {
        setState(2);
      } else {
        reportFatalError("NoFallback", new Object[] { paramXMLAttributes.getValue(null, "href") });
      }
    }
    else if (isFallbackElement(paramQName))
    {
      handleFallbackElement();
    }
    else if (hasXIncludeNamespace(paramQName))
    {
      if (getSawInclude(fDepth - 1)) {
        reportFatalError("IncludeChild", new Object[] { rawname });
      }
      if (getSawFallback(fDepth - 1)) {
        reportFatalError("FallbackChild", new Object[] { rawname });
      }
      if (getState() == 1)
      {
        if (fResultDepth == 0) {
          checkMultipleRootElements();
        }
        if (fDocumentHandler != null)
        {
          paramAugmentations = modifyAugmentations(paramAugmentations);
          paramXMLAttributes = processAttributes(paramXMLAttributes);
          fDocumentHandler.emptyElement(paramQName, paramXMLAttributes, paramAugmentations);
        }
      }
    }
    else if (getState() == 1)
    {
      if (fResultDepth == 0) {
        checkMultipleRootElements();
      }
      if (fDocumentHandler != null)
      {
        paramAugmentations = modifyAugmentations(paramAugmentations);
        paramXMLAttributes = processAttributes(paramXMLAttributes);
        fDocumentHandler.emptyElement(paramQName, paramXMLAttributes, paramAugmentations);
      }
    }
    setSawFallback(fDepth + 1, false);
    setSawInclude(fDepth, false);
    if ((fBaseURIScope.size() > 0) && (fDepth == fBaseURIScope.peek())) {
      restoreBaseURI();
    }
    fDepth -= 1;
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((isIncludeElement(paramQName)) && (getState() == 3) && (!getSawFallback(fDepth + 1))) {
      reportFatalError("NoFallback", new Object[] { "unknown" });
    }
    if (isFallbackElement(paramQName))
    {
      if (getState() == 1) {
        setState(2);
      }
    }
    else if (getState() == 1)
    {
      fResultDepth -= 1;
      if (fDocumentHandler != null) {
        fDocumentHandler.endElement(paramQName, paramAugmentations);
      }
    }
    setSawFallback(fDepth + 1, false);
    setSawInclude(fDepth, false);
    if ((fBaseURIScope.size() > 0) && (fDepth == fBaseURIScope.peek())) {
      restoreBaseURI();
    }
    if ((fLanguageScope.size() > 0) && (fDepth == fLanguageScope.peek())) {
      fCurrentLanguage = restoreLanguage();
    }
    fDepth -= 1;
  }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if (getState() == 1) {
      if (fResultDepth == 0)
      {
        if ((paramAugmentations != null) && (Boolean.TRUE.equals(paramAugmentations.getItem("ENTITY_SKIPPED")))) {
          reportFatalError("UnexpandedEntityReferenceIllegal");
        }
      }
      else if (fDocumentHandler != null) {
        fDocumentHandler.startGeneralEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations);
      }
    }
  }
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (getState() == 1)) {
      fDocumentHandler.textDecl(paramString1, paramString2, paramAugmentations);
    }
  }
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (getState() == 1) && (fResultDepth != 0)) {
      fDocumentHandler.endGeneralEntity(paramString, paramAugmentations);
    }
  }
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if (getState() == 1) {
      if (fResultDepth == 0)
      {
        checkWhitespace(paramXMLString);
      }
      else if (fDocumentHandler != null)
      {
        fDepth += 1;
        paramAugmentations = modifyAugmentations(paramAugmentations);
        fDocumentHandler.characters(paramXMLString, paramAugmentations);
        fDepth -= 1;
      }
    }
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (getState() == 1) && (fResultDepth != 0)) {
      fDocumentHandler.ignorableWhitespace(paramXMLString, paramAugmentations);
    }
  }
  
  public void startCDATA(Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (getState() == 1) && (fResultDepth != 0)) {
      fDocumentHandler.startCDATA(paramAugmentations);
    }
  }
  
  public void endCDATA(Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (getState() == 1) && (fResultDepth != 0)) {
      fDocumentHandler.endCDATA(paramAugmentations);
    }
  }
  
  public void endDocument(Augmentations paramAugmentations)
    throws XNIException
  {
    if (isRootDocument())
    {
      if (!fSeenRootElement) {
        reportFatalError("RootElementRequired");
      }
      if (fDocumentHandler != null) {
        fDocumentHandler.endDocument(paramAugmentations);
      }
    }
  }
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource)
  {
    fDocumentSource = paramXMLDocumentSource;
  }
  
  public XMLDocumentSource getDocumentSource()
  {
    return fDocumentSource;
  }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, String paramString4, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if (fDTDHandler != null) {
      fDTDHandler.attributeDecl(paramString1, paramString2, paramString3, paramArrayOfString, paramString4, paramXMLString1, paramXMLString2, paramAugmentations);
    }
  }
  
  public void elementDecl(String paramString1, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if (fDTDHandler != null) {
      fDTDHandler.elementDecl(paramString1, paramString2, paramAugmentations);
    }
  }
  
  public void endAttlist(Augmentations paramAugmentations)
    throws XNIException
  {
    if (fDTDHandler != null) {
      fDTDHandler.endAttlist(paramAugmentations);
    }
  }
  
  public void endConditional(Augmentations paramAugmentations)
    throws XNIException
  {
    if (fDTDHandler != null) {
      fDTDHandler.endConditional(paramAugmentations);
    }
  }
  
  public void endDTD(Augmentations paramAugmentations)
    throws XNIException
  {
    if (fDTDHandler != null) {
      fDTDHandler.endDTD(paramAugmentations);
    }
    fInDTD = false;
  }
  
  public void endExternalSubset(Augmentations paramAugmentations)
    throws XNIException
  {
    if (fDTDHandler != null) {
      fDTDHandler.endExternalSubset(paramAugmentations);
    }
  }
  
  public void endParameterEntity(String paramString, Augmentations paramAugmentations)
    throws XNIException
  {
    if (fDTDHandler != null) {
      fDTDHandler.endParameterEntity(paramString, paramAugmentations);
    }
  }
  
  public void externalEntityDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations)
    throws XNIException
  {
    if (fDTDHandler != null) {
      fDTDHandler.externalEntityDecl(paramString, paramXMLResourceIdentifier, paramAugmentations);
    }
  }
  
  public XMLDTDSource getDTDSource()
  {
    return fDTDSource;
  }
  
  public void ignoredCharacters(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if (fDTDHandler != null) {
      fDTDHandler.ignoredCharacters(paramXMLString, paramAugmentations);
    }
  }
  
  public void internalEntityDecl(String paramString, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if (fDTDHandler != null) {
      fDTDHandler.internalEntityDecl(paramString, paramXMLString1, paramXMLString2, paramAugmentations);
    }
  }
  
  public void notationDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations)
    throws XNIException
  {
    addNotation(paramString, paramXMLResourceIdentifier, paramAugmentations);
    if (fDTDHandler != null) {
      fDTDHandler.notationDecl(paramString, paramXMLResourceIdentifier, paramAugmentations);
    }
  }
  
  public void setDTDSource(XMLDTDSource paramXMLDTDSource)
  {
    fDTDSource = paramXMLDTDSource;
  }
  
  public void startAttlist(String paramString, Augmentations paramAugmentations)
    throws XNIException
  {
    if (fDTDHandler != null) {
      fDTDHandler.startAttlist(paramString, paramAugmentations);
    }
  }
  
  public void startConditional(short paramShort, Augmentations paramAugmentations)
    throws XNIException
  {
    if (fDTDHandler != null) {
      fDTDHandler.startConditional(paramShort, paramAugmentations);
    }
  }
  
  public void startDTD(XMLLocator paramXMLLocator, Augmentations paramAugmentations)
    throws XNIException
  {
    fInDTD = true;
    if (fDTDHandler != null) {
      fDTDHandler.startDTD(paramXMLLocator, paramAugmentations);
    }
  }
  
  public void startExternalSubset(XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations)
    throws XNIException
  {
    if (fDTDHandler != null) {
      fDTDHandler.startExternalSubset(paramXMLResourceIdentifier, paramAugmentations);
    }
  }
  
  public void startParameterEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if (fDTDHandler != null) {
      fDTDHandler.startParameterEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations);
    }
  }
  
  public void unparsedEntityDecl(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    addUnparsedEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations);
    if (fDTDHandler != null) {
      fDTDHandler.unparsedEntityDecl(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations);
    }
  }
  
  public XMLDTDHandler getDTDHandler()
  {
    return fDTDHandler;
  }
  
  public void setDTDHandler(XMLDTDHandler paramXMLDTDHandler)
  {
    fDTDHandler = paramXMLDTDHandler;
  }
  
  private void setErrorReporter(XMLErrorReporter paramXMLErrorReporter)
  {
    fErrorReporter = paramXMLErrorReporter;
    if (fErrorReporter != null)
    {
      fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xinclude", fXIncludeMessageFormatter);
      if (fDocLocation != null) {
        fErrorReporter.setDocumentLocator(fDocLocation);
      }
    }
  }
  
  protected void handleFallbackElement()
  {
    if (!getSawInclude(fDepth - 1))
    {
      if (getState() == 2) {
        return;
      }
      reportFatalError("FallbackParent");
    }
    setSawInclude(fDepth, false);
    fNamespaceContext.setContextInvalid();
    if (getSawFallback(fDepth)) {
      reportFatalError("MultipleFallbacks");
    } else {
      setSawFallback(fDepth, true);
    }
    if (getState() == 3) {
      setState(1);
    }
  }
  
  protected boolean handleIncludeElement(XMLAttributes paramXMLAttributes)
    throws XNIException
  {
    if (getSawInclude(fDepth - 1)) {
      reportFatalError("IncludeChild", new Object[] { XINCLUDE_INCLUDE });
    }
    if (getState() == 2) {
      return true;
    }
    setSawInclude(fDepth, true);
    fNamespaceContext.setContextInvalid();
    Object localObject1 = paramXMLAttributes.getValue(XINCLUDE_ATTR_HREF);
    String str1 = paramXMLAttributes.getValue(XINCLUDE_ATTR_PARSE);
    String str2 = paramXMLAttributes.getValue("xpointer");
    String str3 = paramXMLAttributes.getValue(XINCLUDE_ATTR_ACCEPT);
    String str4 = paramXMLAttributes.getValue(XINCLUDE_ATTR_ACCEPT_LANGUAGE);
    if (str1 == null) {
      str1 = XINCLUDE_PARSE_XML;
    }
    if (localObject1 == null) {
      localObject1 = XMLSymbols.EMPTY_STRING;
    }
    if ((((String)localObject1).length() == 0) && (XINCLUDE_PARSE_XML.equals(str1))) {
      if (str2 == null)
      {
        reportFatalError("XpointerMissing");
      }
      else
      {
        localObject2 = fErrorReporter != null ? fErrorReporter.getLocale() : null;
        String str5 = fXIncludeMessageFormatter.formatMessage((Locale)localObject2, "XPointerStreamability", null);
        reportResourceError("XMLResourceError", new Object[] { localObject1, str5 });
        return false;
      }
    }
    Object localObject2 = null;
    Object localObject3;
    try
    {
      localObject2 = new URI((String)localObject1, true);
      if (((URI)localObject2).getFragment() != null) {
        reportFatalError("HrefFragmentIdentifierIllegal", new Object[] { localObject1 });
      }
    }
    catch (URI.MalformedURIException localMalformedURIException1)
    {
      localObject3 = escapeHref((String)localObject1);
      if (localObject1 != localObject3)
      {
        localObject1 = localObject3;
        try
        {
          localObject2 = new URI((String)localObject1, true);
          if (((URI)localObject2).getFragment() != null) {
            reportFatalError("HrefFragmentIdentifierIllegal", new Object[] { localObject1 });
          }
        }
        catch (URI.MalformedURIException localMalformedURIException2)
        {
          reportFatalError("HrefSyntacticallyInvalid", new Object[] { localObject1 });
        }
      }
      else
      {
        reportFatalError("HrefSyntacticallyInvalid", new Object[] { localObject1 });
      }
    }
    if ((str3 != null) && (!isValidInHTTPHeader(str3)))
    {
      reportFatalError("AcceptMalformed", null);
      str3 = null;
    }
    if ((str4 != null) && (!isValidInHTTPHeader(str4)))
    {
      reportFatalError("AcceptLanguageMalformed", null);
      str4 = null;
    }
    XMLInputSource localXMLInputSource = null;
    if (fEntityResolver != null) {
      try
      {
        localObject3 = new XMLResourceIdentifierImpl(null, (String)localObject1, fCurrentBaseURI.getExpandedSystemId(), XMLEntityManager.expandSystemId((String)localObject1, fCurrentBaseURI.getExpandedSystemId(), false));
        localXMLInputSource = fEntityResolver.resolveEntity((XMLResourceIdentifier)localObject3);
        if ((localXMLInputSource != null) && (!(localXMLInputSource instanceof HTTPInputSource)) && ((str3 != null) || (str4 != null)) && (localXMLInputSource.getCharacterStream() == null) && (localXMLInputSource.getByteStream() == null)) {
          localXMLInputSource = createInputSource(localXMLInputSource.getPublicId(), localXMLInputSource.getSystemId(), localXMLInputSource.getBaseSystemId(), str3, str4);
        }
      }
      catch (IOException localIOException1)
      {
        reportResourceError("XMLResourceError", new Object[] { localObject1, localIOException1.getMessage() });
        return false;
      }
    }
    if (localXMLInputSource == null) {
      if ((str3 != null) || (str4 != null)) {
        localXMLInputSource = createInputSource(null, (String)localObject1, fCurrentBaseURI.getExpandedSystemId(), str3, str4);
      } else {
        localXMLInputSource = new XMLInputSource(null, (String)localObject1, fCurrentBaseURI.getExpandedSystemId());
      }
    }
    if (str1.equals(XINCLUDE_PARSE_XML))
    {
      Object localObject4;
      if (((str2 != null) && (fXPointerChildConfig == null)) || ((str2 == null) && (fXIncludeChildConfig == null)))
      {
        String str6 = "com.sun.org.apache.xerces.internal.parsers.XIncludeParserConfiguration";
        if (str2 != null) {
          str6 = "com.sun.org.apache.xerces.internal.parsers.XPointerParserConfiguration";
        }
        fChildConfig = ((XMLParserConfiguration)ObjectFactory.newInstance(str6, true));
        if (fSymbolTable != null) {
          fChildConfig.setProperty("http://apache.org/xml/properties/internal/symbol-table", fSymbolTable);
        }
        if (fErrorReporter != null) {
          fChildConfig.setProperty("http://apache.org/xml/properties/internal/error-reporter", fErrorReporter);
        }
        if (fEntityResolver != null) {
          fChildConfig.setProperty("http://apache.org/xml/properties/internal/entity-resolver", fEntityResolver);
        }
        fChildConfig.setProperty("http://apache.org/xml/properties/security-manager", fSecurityManager);
        fChildConfig.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", fSecurityPropertyMgr);
        fChildConfig.setProperty("http://apache.org/xml/properties/input-buffer-size", new Integer(fBufferSize));
        fNeedCopyFeatures = true;
        fChildConfig.setProperty("http://apache.org/xml/properties/internal/namespace-context", fNamespaceContext);
        fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", fFixupBaseURIs);
        fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-language", fFixupLanguage);
        if (str2 != null)
        {
          localObject4 = (XPointerHandler)fChildConfig.getProperty("http://apache.org/xml/properties/internal/xpointer-handler");
          fXPtrProcessor = ((XPointerProcessor)localObject4);
          ((XPointerHandler)fXPtrProcessor).setProperty("http://apache.org/xml/properties/internal/namespace-context", fNamespaceContext);
          ((XPointerHandler)fXPtrProcessor).setProperty("http://apache.org/xml/features/xinclude/fixup-base-uris", Boolean.valueOf(fFixupBaseURIs));
          ((XPointerHandler)fXPtrProcessor).setProperty("http://apache.org/xml/features/xinclude/fixup-language", Boolean.valueOf(fFixupLanguage));
          if (fErrorReporter != null) {
            ((XPointerHandler)fXPtrProcessor).setProperty("http://apache.org/xml/properties/internal/error-reporter", fErrorReporter);
          }
          ((XPointerHandler)localObject4).setParent(this);
          ((XPointerHandler)localObject4).setDocumentHandler(getDocumentHandler());
          fXPointerChildConfig = fChildConfig;
        }
        else
        {
          localObject4 = (XIncludeHandler)fChildConfig.getProperty("http://apache.org/xml/properties/internal/xinclude-handler");
          ((XIncludeHandler)localObject4).setParent(this);
          ((XIncludeHandler)localObject4).setDocumentHandler(getDocumentHandler());
          fXIncludeChildConfig = fChildConfig;
        }
      }
      if (str2 != null)
      {
        fChildConfig = fXPointerChildConfig;
        try
        {
          fXPtrProcessor.parseXPointer(str2);
        }
        catch (XNIException localXNIException1)
        {
          reportResourceError("XMLResourceError", new Object[] { localObject1, localXNIException1.getMessage() });
          return false;
        }
      }
      else
      {
        fChildConfig = fXIncludeChildConfig;
      }
      if (fNeedCopyFeatures) {
        copyFeatures(fSettings, fChildConfig);
      }
      fNeedCopyFeatures = false;
      try
      {
        fNamespaceContext.pushScope();
        fChildConfig.parse(localXMLInputSource);
        if (fErrorReporter != null) {
          fErrorReporter.setDocumentLocator(fDocLocation);
        }
        if ((str2 != null) && (!fXPtrProcessor.isXPointerResolved()))
        {
          Locale localLocale = fErrorReporter != null ? fErrorReporter.getLocale() : null;
          localObject4 = fXIncludeMessageFormatter.formatMessage(localLocale, "XPointerResolutionUnsuccessful", null);
          reportResourceError("XMLResourceError", new Object[] { localObject1, localObject4 });
          boolean bool2 = false;
          return bool2;
        }
      }
      catch (XNIException localXNIException2)
      {
        if (fErrorReporter != null) {
          fErrorReporter.setDocumentLocator(fDocLocation);
        }
        reportFatalError("XMLParseError", new Object[] { localObject1, localXNIException2.getMessage() });
      }
      catch (IOException localIOException2)
      {
        if (fErrorReporter != null) {
          fErrorReporter.setDocumentLocator(fDocLocation);
        }
        reportResourceError("XMLResourceError", new Object[] { localObject1, localIOException2.getMessage() });
        boolean bool1 = false;
        return bool1;
      }
      finally
      {
        fNamespaceContext.popScope();
      }
    }
    else
    {
      if (str1.equals(XINCLUDE_PARSE_TEXT))
      {
        String str7 = paramXMLAttributes.getValue(XINCLUDE_ATTR_ENCODING);
        localXMLInputSource.setEncoding(str7);
        XIncludeTextReader localXIncludeTextReader = null;
        try
        {
          if (!fIsXML11)
          {
            if (fXInclude10TextReader == null) {
              fXInclude10TextReader = new XIncludeTextReader(localXMLInputSource, this, fBufferSize);
            } else {
              fXInclude10TextReader.setInputSource(localXMLInputSource);
            }
            localXIncludeTextReader = fXInclude10TextReader;
          }
          else
          {
            if (fXInclude11TextReader == null) {
              fXInclude11TextReader = new XInclude11TextReader(localXMLInputSource, this, fBufferSize);
            } else {
              fXInclude11TextReader.setInputSource(localXMLInputSource);
            }
            localXIncludeTextReader = fXInclude11TextReader;
          }
          localXIncludeTextReader.setErrorReporter(fErrorReporter);
          localXIncludeTextReader.parse();
        }
        catch (MalformedByteSequenceException localMalformedByteSequenceException)
        {
          fErrorReporter.reportError(localMalformedByteSequenceException.getDomain(), localMalformedByteSequenceException.getKey(), localMalformedByteSequenceException.getArguments(), (short)2);
        }
        catch (CharConversionException localCharConversionException)
        {
          fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2);
        }
        catch (IOException localIOException6)
        {
          reportResourceError("TextResourceError", new Object[] { localObject1, localIOException6.getMessage() });
          boolean bool3 = false;
          return bool3;
        }
        finally
        {
          if (localXIncludeTextReader != null) {
            try
            {
              localXIncludeTextReader.close();
            }
            catch (IOException localIOException8)
            {
              reportResourceError("TextResourceError", new Object[] { localObject1, localIOException8.getMessage() });
              return false;
            }
          }
        }
      }
      reportFatalError("InvalidParseValue", new Object[] { str1 });
    }
    return true;
  }
  
  protected boolean hasXIncludeNamespace(QName paramQName)
  {
    return (uri == XINCLUDE_NS_URI) || (fNamespaceContext.getURI(prefix) == XINCLUDE_NS_URI);
  }
  
  protected boolean isIncludeElement(QName paramQName)
  {
    return (localpart.equals(XINCLUDE_INCLUDE)) && (hasXIncludeNamespace(paramQName));
  }
  
  protected boolean isFallbackElement(QName paramQName)
  {
    return (localpart.equals(XINCLUDE_FALLBACK)) && (hasXIncludeNamespace(paramQName));
  }
  
  protected boolean sameBaseURIAsIncludeParent()
  {
    String str1 = getIncludeParentBaseURI();
    String str2 = fCurrentBaseURI.getExpandedSystemId();
    return (str1 != null) && (str1.equals(str2));
  }
  
  protected boolean sameLanguageAsIncludeParent()
  {
    String str = getIncludeParentLanguage();
    return (str != null) && (str.equalsIgnoreCase(fCurrentLanguage));
  }
  
  protected boolean searchForRecursiveIncludes(XMLLocator paramXMLLocator)
  {
    String str = paramXMLLocator.getExpandedSystemId();
    if (str == null) {
      try
      {
        str = XMLEntityManager.expandSystemId(paramXMLLocator.getLiteralSystemId(), paramXMLLocator.getBaseSystemId(), false);
      }
      catch (URI.MalformedURIException localMalformedURIException)
      {
        reportFatalError("ExpandedSystemId");
      }
    }
    if (str.equals(fCurrentBaseURI.getExpandedSystemId())) {
      return true;
    }
    if (fParentXIncludeHandler == null) {
      return false;
    }
    return fParentXIncludeHandler.searchForRecursiveIncludes(paramXMLLocator);
  }
  
  protected boolean isTopLevelIncludedItem()
  {
    return (isTopLevelIncludedItemViaInclude()) || (isTopLevelIncludedItemViaFallback());
  }
  
  protected boolean isTopLevelIncludedItemViaInclude()
  {
    return (fDepth == 1) && (!isRootDocument());
  }
  
  protected boolean isTopLevelIncludedItemViaFallback()
  {
    return getSawFallback(fDepth - 1);
  }
  
  protected XMLAttributes processAttributes(XMLAttributes paramXMLAttributes)
  {
    String str3;
    String str4;
    Object localObject;
    if (isTopLevelIncludedItem())
    {
      if ((fFixupBaseURIs) && (!sameBaseURIAsIncludeParent()))
      {
        if (paramXMLAttributes == null) {
          paramXMLAttributes = new XMLAttributesImpl();
        }
        String str1 = null;
        try
        {
          str1 = getRelativeBaseURI();
        }
        catch (URI.MalformedURIException localMalformedURIException)
        {
          str1 = fCurrentBaseURI.getExpandedSystemId();
        }
        int k = paramXMLAttributes.addAttribute(XML_BASE_QNAME, XMLSymbols.fCDATASymbol, str1);
        paramXMLAttributes.setSpecified(k, true);
      }
      if ((fFixupLanguage) && (!sameLanguageAsIncludeParent()))
      {
        if (paramXMLAttributes == null) {
          paramXMLAttributes = new XMLAttributesImpl();
        }
        int i = paramXMLAttributes.addAttribute(XML_LANG_QNAME, XMLSymbols.fCDATASymbol, fCurrentLanguage);
        paramXMLAttributes.setSpecified(i, true);
      }
      Enumeration localEnumeration = fNamespaceContext.getAllPrefixes();
      while (localEnumeration.hasMoreElements())
      {
        String str2 = (String)localEnumeration.nextElement();
        str3 = fNamespaceContext.getURIFromIncludeParent(str2);
        str4 = fNamespaceContext.getURI(str2);
        if ((str3 != str4) && (paramXMLAttributes != null))
        {
          int n;
          if (str2 == XMLSymbols.EMPTY_STRING)
          {
            if (paramXMLAttributes.getValue(NamespaceContext.XMLNS_URI, XMLSymbols.PREFIX_XMLNS) == null)
            {
              if (paramXMLAttributes == null) {
                paramXMLAttributes = new XMLAttributesImpl();
              }
              localObject = (QName)NEW_NS_ATTR_QNAME.clone();
              prefix = null;
              localpart = XMLSymbols.PREFIX_XMLNS;
              rawname = XMLSymbols.PREFIX_XMLNS;
              n = paramXMLAttributes.addAttribute((QName)localObject, XMLSymbols.fCDATASymbol, str4 != null ? str4 : XMLSymbols.EMPTY_STRING);
              paramXMLAttributes.setSpecified(n, true);
              fNamespaceContext.declarePrefix(str2, str4);
            }
          }
          else if (paramXMLAttributes.getValue(NamespaceContext.XMLNS_URI, str2) == null)
          {
            if (paramXMLAttributes == null) {
              paramXMLAttributes = new XMLAttributesImpl();
            }
            localObject = (QName)NEW_NS_ATTR_QNAME.clone();
            localpart = str2;
            rawname += str2;
            rawname = (fSymbolTable != null ? fSymbolTable.addSymbol(rawname) : rawname.intern());
            n = paramXMLAttributes.addAttribute((QName)localObject, XMLSymbols.fCDATASymbol, str4 != null ? str4 : XMLSymbols.EMPTY_STRING);
            paramXMLAttributes.setSpecified(n, true);
            fNamespaceContext.declarePrefix(str2, str4);
          }
        }
      }
    }
    if (paramXMLAttributes != null)
    {
      int j = paramXMLAttributes.getLength();
      for (int m = 0; m < j; m++)
      {
        str3 = paramXMLAttributes.getType(m);
        str4 = paramXMLAttributes.getValue(m);
        if (str3 == XMLSymbols.fENTITYSymbol) {
          checkUnparsedEntity(str4);
        }
        if (str3 == XMLSymbols.fENTITIESSymbol)
        {
          localObject = new StringTokenizer(str4);
          while (((StringTokenizer)localObject).hasMoreTokens())
          {
            String str5 = ((StringTokenizer)localObject).nextToken();
            checkUnparsedEntity(str5);
          }
        }
        else if (str3 == XMLSymbols.fNOTATIONSymbol)
        {
          checkNotation(str4);
        }
      }
    }
    return paramXMLAttributes;
  }
  
  protected String getRelativeBaseURI()
    throws URI.MalformedURIException
  {
    int i = getIncludeParentDepth();
    String str1 = getRelativeURI(i);
    if (isRootDocument()) {
      return str1;
    }
    if (str1.equals("")) {
      str1 = fCurrentBaseURI.getLiteralSystemId();
    }
    if (i == 0)
    {
      if (fParentRelativeURI == null) {
        fParentRelativeURI = fParentXIncludeHandler.getRelativeBaseURI();
      }
      if (fParentRelativeURI.equals("")) {
        return str1;
      }
      URI localURI1 = new URI(fParentRelativeURI, true);
      URI localURI2 = new URI(localURI1, str1);
      String str2 = localURI1.getScheme();
      String str3 = localURI2.getScheme();
      if (!Objects.equals(str2, str3)) {
        return str1;
      }
      String str4 = localURI1.getAuthority();
      String str5 = localURI2.getAuthority();
      if (!Objects.equals(str4, str5)) {
        return localURI2.getSchemeSpecificPart();
      }
      String str6 = localURI2.getPath();
      String str7 = localURI2.getQueryString();
      String str8 = localURI2.getFragment();
      if ((str7 != null) || (str8 != null))
      {
        StringBuilder localStringBuilder = new StringBuilder();
        if (str6 != null) {
          localStringBuilder.append(str6);
        }
        if (str7 != null)
        {
          localStringBuilder.append('?');
          localStringBuilder.append(str7);
        }
        if (str8 != null)
        {
          localStringBuilder.append('#');
          localStringBuilder.append(str8);
        }
        return localStringBuilder.toString();
      }
      return str6;
    }
    return str1;
  }
  
  private String getIncludeParentBaseURI()
  {
    int i = getIncludeParentDepth();
    if ((!isRootDocument()) && (i == 0)) {
      return fParentXIncludeHandler.getIncludeParentBaseURI();
    }
    return getBaseURI(i);
  }
  
  private String getIncludeParentLanguage()
  {
    int i = getIncludeParentDepth();
    if ((!isRootDocument()) && (i == 0)) {
      return fParentXIncludeHandler.getIncludeParentLanguage();
    }
    return getLanguage(i);
  }
  
  private int getIncludeParentDepth()
  {
    for (int i = fDepth - 1; i >= 0; i--) {
      if ((!getSawInclude(i)) && (!getSawFallback(i))) {
        return i;
      }
    }
    return 0;
  }
  
  private int getResultDepth()
  {
    return fResultDepth;
  }
  
  protected Augmentations modifyAugmentations(Augmentations paramAugmentations)
  {
    return modifyAugmentations(paramAugmentations, false);
  }
  
  protected Augmentations modifyAugmentations(Augmentations paramAugmentations, boolean paramBoolean)
  {
    if ((paramBoolean) || (isTopLevelIncludedItem()))
    {
      if (paramAugmentations == null) {
        paramAugmentations = new AugmentationsImpl();
      }
      paramAugmentations.putItem(XINCLUDE_INCLUDED, Boolean.TRUE);
    }
    return paramAugmentations;
  }
  
  protected int getState(int paramInt)
  {
    return fState[paramInt];
  }
  
  protected int getState()
  {
    return fState[fDepth];
  }
  
  protected void setState(int paramInt)
  {
    if (fDepth >= fState.length)
    {
      int[] arrayOfInt = new int[fDepth * 2];
      System.arraycopy(fState, 0, arrayOfInt, 0, fState.length);
      fState = arrayOfInt;
    }
    fState[fDepth] = paramInt;
  }
  
  protected void setSawFallback(int paramInt, boolean paramBoolean)
  {
    if (paramInt >= fSawFallback.length)
    {
      boolean[] arrayOfBoolean = new boolean[paramInt * 2];
      System.arraycopy(fSawFallback, 0, arrayOfBoolean, 0, fSawFallback.length);
      fSawFallback = arrayOfBoolean;
    }
    fSawFallback[paramInt] = paramBoolean;
  }
  
  protected boolean getSawFallback(int paramInt)
  {
    if (paramInt >= fSawFallback.length) {
      return false;
    }
    return fSawFallback[paramInt];
  }
  
  protected void setSawInclude(int paramInt, boolean paramBoolean)
  {
    if (paramInt >= fSawInclude.length)
    {
      boolean[] arrayOfBoolean = new boolean[paramInt * 2];
      System.arraycopy(fSawInclude, 0, arrayOfBoolean, 0, fSawInclude.length);
      fSawInclude = arrayOfBoolean;
    }
    fSawInclude[paramInt] = paramBoolean;
  }
  
  protected boolean getSawInclude(int paramInt)
  {
    if (paramInt >= fSawInclude.length) {
      return false;
    }
    return fSawInclude[paramInt];
  }
  
  protected void reportResourceError(String paramString)
  {
    reportFatalError(paramString, null);
  }
  
  protected void reportResourceError(String paramString, Object[] paramArrayOfObject)
  {
    reportError(paramString, paramArrayOfObject, (short)0);
  }
  
  protected void reportFatalError(String paramString)
  {
    reportFatalError(paramString, null);
  }
  
  protected void reportFatalError(String paramString, Object[] paramArrayOfObject)
  {
    reportError(paramString, paramArrayOfObject, (short)2);
  }
  
  private void reportError(String paramString, Object[] paramArrayOfObject, short paramShort)
  {
    if (fErrorReporter != null) {
      fErrorReporter.reportError("http://www.w3.org/TR/xinclude", paramString, paramArrayOfObject, paramShort);
    }
  }
  
  protected void setParent(XIncludeHandler paramXIncludeHandler)
  {
    fParentXIncludeHandler = paramXIncludeHandler;
  }
  
  protected boolean isRootDocument()
  {
    return fParentXIncludeHandler == null;
  }
  
  protected void addUnparsedEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
  {
    UnparsedEntity localUnparsedEntity = new UnparsedEntity();
    name = paramString1;
    systemId = paramXMLResourceIdentifier.getLiteralSystemId();
    publicId = paramXMLResourceIdentifier.getPublicId();
    baseURI = paramXMLResourceIdentifier.getBaseSystemId();
    expandedSystemId = paramXMLResourceIdentifier.getExpandedSystemId();
    notation = paramString2;
    augmentations = paramAugmentations;
    fUnparsedEntities.add(localUnparsedEntity);
  }
  
  protected void addNotation(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations)
  {
    Notation localNotation = new Notation();
    name = paramString;
    systemId = paramXMLResourceIdentifier.getLiteralSystemId();
    publicId = paramXMLResourceIdentifier.getPublicId();
    baseURI = paramXMLResourceIdentifier.getBaseSystemId();
    expandedSystemId = paramXMLResourceIdentifier.getExpandedSystemId();
    augmentations = paramAugmentations;
    fNotations.add(localNotation);
  }
  
  protected void checkUnparsedEntity(String paramString)
  {
    UnparsedEntity localUnparsedEntity = new UnparsedEntity();
    name = paramString;
    int i = fUnparsedEntities.indexOf(localUnparsedEntity);
    if (i != -1)
    {
      localUnparsedEntity = (UnparsedEntity)fUnparsedEntities.get(i);
      checkNotation(notation);
      checkAndSendUnparsedEntity(localUnparsedEntity);
    }
  }
  
  protected void checkNotation(String paramString)
  {
    Notation localNotation = new Notation();
    name = paramString;
    int i = fNotations.indexOf(localNotation);
    if (i != -1)
    {
      localNotation = (Notation)fNotations.get(i);
      checkAndSendNotation(localNotation);
    }
  }
  
  protected void checkAndSendUnparsedEntity(UnparsedEntity paramUnparsedEntity)
  {
    if (isRootDocument())
    {
      int i = fUnparsedEntities.indexOf(paramUnparsedEntity);
      Object localObject;
      if (i == -1)
      {
        localObject = new XMLResourceIdentifierImpl(publicId, systemId, baseURI, expandedSystemId);
        addUnparsedEntity(name, (XMLResourceIdentifier)localObject, notation, augmentations);
        if ((fSendUEAndNotationEvents) && (fDTDHandler != null)) {
          fDTDHandler.unparsedEntityDecl(name, (XMLResourceIdentifier)localObject, notation, augmentations);
        }
      }
      else
      {
        localObject = (UnparsedEntity)fUnparsedEntities.get(i);
        if (!paramUnparsedEntity.isDuplicate(localObject)) {
          reportFatalError("NonDuplicateUnparsedEntity", new Object[] { name });
        }
      }
    }
    else
    {
      fParentXIncludeHandler.checkAndSendUnparsedEntity(paramUnparsedEntity);
    }
  }
  
  protected void checkAndSendNotation(Notation paramNotation)
  {
    if (isRootDocument())
    {
      int i = fNotations.indexOf(paramNotation);
      Object localObject;
      if (i == -1)
      {
        localObject = new XMLResourceIdentifierImpl(publicId, systemId, baseURI, expandedSystemId);
        addNotation(name, (XMLResourceIdentifier)localObject, augmentations);
        if ((fSendUEAndNotationEvents) && (fDTDHandler != null)) {
          fDTDHandler.notationDecl(name, (XMLResourceIdentifier)localObject, augmentations);
        }
      }
      else
      {
        localObject = (Notation)fNotations.get(i);
        if (!paramNotation.isDuplicate(localObject)) {
          reportFatalError("NonDuplicateNotation", new Object[] { name });
        }
      }
    }
    else
    {
      fParentXIncludeHandler.checkAndSendNotation(paramNotation);
    }
  }
  
  private void checkWhitespace(XMLString paramXMLString)
  {
    int i = offset + length;
    for (int j = offset; j < i; j++) {
      if (!XMLChar.isSpace(ch[j]))
      {
        reportFatalError("ContentIllegalAtTopLevel");
        return;
      }
    }
  }
  
  private void checkMultipleRootElements()
  {
    if (getRootElementProcessed()) {
      reportFatalError("MultipleRootElements");
    }
    setRootElementProcessed(true);
  }
  
  private void setRootElementProcessed(boolean paramBoolean)
  {
    if (isRootDocument())
    {
      fSeenRootElement = paramBoolean;
      return;
    }
    fParentXIncludeHandler.setRootElementProcessed(paramBoolean);
  }
  
  private boolean getRootElementProcessed()
  {
    return isRootDocument() ? fSeenRootElement : fParentXIncludeHandler.getRootElementProcessed();
  }
  
  protected void copyFeatures(XMLComponentManager paramXMLComponentManager, ParserConfigurationSettings paramParserConfigurationSettings)
  {
    Enumeration localEnumeration = Constants.getXercesFeatures();
    copyFeatures1(localEnumeration, "http://apache.org/xml/features/", paramXMLComponentManager, paramParserConfigurationSettings);
    localEnumeration = Constants.getSAXFeatures();
    copyFeatures1(localEnumeration, "http://xml.org/sax/features/", paramXMLComponentManager, paramParserConfigurationSettings);
  }
  
  protected void copyFeatures(XMLComponentManager paramXMLComponentManager, XMLParserConfiguration paramXMLParserConfiguration)
  {
    Enumeration localEnumeration = Constants.getXercesFeatures();
    copyFeatures1(localEnumeration, "http://apache.org/xml/features/", paramXMLComponentManager, paramXMLParserConfiguration);
    localEnumeration = Constants.getSAXFeatures();
    copyFeatures1(localEnumeration, "http://xml.org/sax/features/", paramXMLComponentManager, paramXMLParserConfiguration);
  }
  
  private void copyFeatures1(Enumeration paramEnumeration, String paramString, XMLComponentManager paramXMLComponentManager, ParserConfigurationSettings paramParserConfigurationSettings)
  {
    while (paramEnumeration.hasMoreElements())
    {
      String str = paramString + (String)paramEnumeration.nextElement();
      paramParserConfigurationSettings.addRecognizedFeatures(new String[] { str });
      try
      {
        paramParserConfigurationSettings.setFeature(str, paramXMLComponentManager.getFeature(str));
      }
      catch (XMLConfigurationException localXMLConfigurationException) {}
    }
  }
  
  private void copyFeatures1(Enumeration paramEnumeration, String paramString, XMLComponentManager paramXMLComponentManager, XMLParserConfiguration paramXMLParserConfiguration)
  {
    while (paramEnumeration.hasMoreElements())
    {
      String str = paramString + (String)paramEnumeration.nextElement();
      boolean bool = paramXMLComponentManager.getFeature(str);
      try
      {
        paramXMLParserConfiguration.setFeature(str, bool);
      }
      catch (XMLConfigurationException localXMLConfigurationException) {}
    }
  }
  
  protected void saveBaseURI()
  {
    fBaseURIScope.push(fDepth);
    fBaseURI.push(fCurrentBaseURI.getBaseSystemId());
    fLiteralSystemID.push(fCurrentBaseURI.getLiteralSystemId());
    fExpandedSystemID.push(fCurrentBaseURI.getExpandedSystemId());
  }
  
  protected void restoreBaseURI()
  {
    fBaseURI.pop();
    fLiteralSystemID.pop();
    fExpandedSystemID.pop();
    fBaseURIScope.pop();
    fCurrentBaseURI.setBaseSystemId((String)fBaseURI.peek());
    fCurrentBaseURI.setLiteralSystemId((String)fLiteralSystemID.peek());
    fCurrentBaseURI.setExpandedSystemId((String)fExpandedSystemID.peek());
  }
  
  protected void saveLanguage(String paramString)
  {
    fLanguageScope.push(fDepth);
    fLanguageStack.push(paramString);
  }
  
  public String restoreLanguage()
  {
    fLanguageStack.pop();
    fLanguageScope.pop();
    return (String)fLanguageStack.peek();
  }
  
  public String getBaseURI(int paramInt)
  {
    int i = scopeOfBaseURI(paramInt);
    return (String)fExpandedSystemID.elementAt(i);
  }
  
  public String getLanguage(int paramInt)
  {
    int i = scopeOfLanguage(paramInt);
    return (String)fLanguageStack.elementAt(i);
  }
  
  public String getRelativeURI(int paramInt)
    throws URI.MalformedURIException
  {
    int i = scopeOfBaseURI(paramInt) + 1;
    if (i == fBaseURIScope.size()) {
      return "";
    }
    URI localURI = new URI("file", (String)fLiteralSystemID.elementAt(i));
    for (int j = i + 1; j < fBaseURIScope.size(); j++) {
      localURI = new URI(localURI, (String)fLiteralSystemID.elementAt(j));
    }
    return localURI.getPath();
  }
  
  private int scopeOfBaseURI(int paramInt)
  {
    for (int i = fBaseURIScope.size() - 1; i >= 0; i--) {
      if (fBaseURIScope.elementAt(i) <= paramInt) {
        return i;
      }
    }
    return -1;
  }
  
  private int scopeOfLanguage(int paramInt)
  {
    for (int i = fLanguageScope.size() - 1; i >= 0; i--) {
      if (fLanguageScope.elementAt(i) <= paramInt) {
        return i;
      }
    }
    return -1;
  }
  
  protected void processXMLBaseAttributes(XMLAttributes paramXMLAttributes)
  {
    String str1 = paramXMLAttributes.getValue(NamespaceContext.XML_URI, "base");
    if (str1 != null) {
      try
      {
        String str2 = XMLEntityManager.expandSystemId(str1, fCurrentBaseURI.getExpandedSystemId(), false);
        fCurrentBaseURI.setLiteralSystemId(str1);
        fCurrentBaseURI.setBaseSystemId(fCurrentBaseURI.getExpandedSystemId());
        fCurrentBaseURI.setExpandedSystemId(str2);
        saveBaseURI();
      }
      catch (URI.MalformedURIException localMalformedURIException) {}
    }
  }
  
  protected void processXMLLangAttributes(XMLAttributes paramXMLAttributes)
  {
    String str = paramXMLAttributes.getValue(NamespaceContext.XML_URI, "lang");
    if (str != null)
    {
      fCurrentLanguage = str;
      saveLanguage(fCurrentLanguage);
    }
  }
  
  private boolean isValidInHTTPHeader(String paramString)
  {
    for (int j = paramString.length() - 1; j >= 0; j--)
    {
      int i = paramString.charAt(j);
      if ((i < 32) || (i > 126)) {
        return false;
      }
    }
    return true;
  }
  
  private XMLInputSource createInputSource(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    HTTPInputSource localHTTPInputSource = new HTTPInputSource(paramString1, paramString2, paramString3);
    if ((paramString4 != null) && (paramString4.length() > 0)) {
      localHTTPInputSource.setHTTPRequestProperty("Accept", paramString4);
    }
    if ((paramString5 != null) && (paramString5.length() > 0)) {
      localHTTPInputSource.setHTTPRequestProperty("Accept-Language", paramString5);
    }
    return localHTTPInputSource;
  }
  
  private String escapeHref(String paramString)
  {
    int i = paramString.length();
    StringBuilder localStringBuilder = new StringBuilder(i * 3);
    int j;
    for (int k = 0; k < i; k++)
    {
      j = paramString.charAt(k);
      if (j > 126) {
        break;
      }
      if (j < 32) {
        return paramString;
      }
      if (gNeedEscaping[j] != 0)
      {
        localStringBuilder.append('%');
        localStringBuilder.append(gAfterEscaping1[j]);
        localStringBuilder.append(gAfterEscaping2[j]);
      }
      else
      {
        localStringBuilder.append((char)j);
      }
    }
    if (k < i)
    {
      int n;
      for (int m = k; m < i; m++)
      {
        j = paramString.charAt(m);
        if (((j < 32) || (j > 126)) && ((j < 160) || (j > 55295)) && ((j < 63744) || (j > 64975)) && ((j < 65008) || (j > 65519)))
        {
          if (XMLChar.isHighSurrogate(j))
          {
            m++;
            if (m < i)
            {
              n = paramString.charAt(m);
              if (XMLChar.isLowSurrogate(n))
              {
                n = XMLChar.supplemental((char)j, (char)n);
                if ((n < 983040) && ((n & 0xFFFF) <= 65533)) {
                  continue;
                }
              }
            }
          }
          return paramString;
        }
      }
      byte[] arrayOfByte = null;
      try
      {
        arrayOfByte = paramString.substring(k).getBytes("UTF-8");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        return paramString;
      }
      i = arrayOfByte.length;
      for (k = 0; k < i; k++)
      {
        n = arrayOfByte[k];
        if (n < 0)
        {
          j = n + 256;
          localStringBuilder.append('%');
          localStringBuilder.append(gHexChs[(j >> 4)]);
          localStringBuilder.append(gHexChs[(j & 0xF)]);
        }
        else if (gNeedEscaping[n] != 0)
        {
          localStringBuilder.append('%');
          localStringBuilder.append(gAfterEscaping1[n]);
          localStringBuilder.append(gAfterEscaping2[n]);
        }
        else
        {
          localStringBuilder.append((char)n);
        }
      }
    }
    if (localStringBuilder.length() != i) {
      return localStringBuilder.toString();
    }
    return paramString;
  }
  
  static
  {
    for (int j : new char[] { ' ', '<', '>', '"', '{', '}', '|', '\\', '^', '`' })
    {
      gNeedEscaping[j] = true;
      gAfterEscaping1[j] = gHexChs[(j >> 4)];
      gAfterEscaping2[j] = gHexChs[(j & 0xF)];
    }
  }
  
  protected static class Notation
  {
    public String name;
    public String systemId;
    public String baseURI;
    public String publicId;
    public String expandedSystemId;
    public Augmentations augmentations;
    
    protected Notation() {}
    
    public boolean equals(Object paramObject)
    {
      return (paramObject == this) || (((paramObject instanceof Notation)) && (Objects.equals(name, name)));
    }
    
    public int hashCode()
    {
      return Objects.hashCode(name);
    }
    
    public boolean isDuplicate(Object paramObject)
    {
      if ((paramObject != null) && ((paramObject instanceof Notation)))
      {
        Notation localNotation = (Notation)paramObject;
        return (Objects.equals(name, name)) && (Objects.equals(publicId, publicId)) && (Objects.equals(expandedSystemId, expandedSystemId));
      }
      return false;
    }
  }
  
  protected static class UnparsedEntity
  {
    public String name;
    public String systemId;
    public String baseURI;
    public String publicId;
    public String expandedSystemId;
    public String notation;
    public Augmentations augmentations;
    
    protected UnparsedEntity() {}
    
    public boolean equals(Object paramObject)
    {
      return (paramObject == this) || (((paramObject instanceof UnparsedEntity)) && (Objects.equals(name, name)));
    }
    
    public int hashCode()
    {
      return Objects.hashCode(name);
    }
    
    public boolean isDuplicate(Object paramObject)
    {
      if ((paramObject != null) && ((paramObject instanceof UnparsedEntity)))
      {
        UnparsedEntity localUnparsedEntity = (UnparsedEntity)paramObject;
        return (Objects.equals(name, name)) && (Objects.equals(publicId, publicId)) && (Objects.equals(expandedSystemId, expandedSystemId)) && (Objects.equals(notation, notation));
      }
      return false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xinclude\XIncludeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */