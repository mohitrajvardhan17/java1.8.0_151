package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesIteratorImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.Limit;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager.Property;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity.ScannedEntity;
import com.sun.xml.internal.stream.XMLBufferListener;
import com.sun.xml.internal.stream.XMLEntityStorage;
import com.sun.xml.internal.stream.dtd.DTDGrammarUtil;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class XMLDocumentFragmentScannerImpl
  extends XMLScanner
  implements XMLDocumentScanner, XMLComponent, XMLEntityHandler, XMLBufferListener
{
  protected int fElementAttributeLimit;
  protected int fXMLNameLimit;
  protected ExternalSubsetResolver fExternalSubsetResolver;
  protected static final int SCANNER_STATE_START_OF_MARKUP = 21;
  protected static final int SCANNER_STATE_CONTENT = 22;
  protected static final int SCANNER_STATE_PI = 23;
  protected static final int SCANNER_STATE_DOCTYPE = 24;
  protected static final int SCANNER_STATE_XML_DECL = 25;
  protected static final int SCANNER_STATE_ROOT_ELEMENT = 26;
  protected static final int SCANNER_STATE_COMMENT = 27;
  protected static final int SCANNER_STATE_REFERENCE = 28;
  protected static final int SCANNER_STATE_ATTRIBUTE = 29;
  protected static final int SCANNER_STATE_ATTRIBUTE_VALUE = 30;
  protected static final int SCANNER_STATE_END_OF_INPUT = 33;
  protected static final int SCANNER_STATE_TERMINATED = 34;
  protected static final int SCANNER_STATE_CDATA = 35;
  protected static final int SCANNER_STATE_TEXT_DECL = 36;
  protected static final int SCANNER_STATE_CHARACTER_DATA = 37;
  protected static final int SCANNER_STATE_START_ELEMENT_TAG = 38;
  protected static final int SCANNER_STATE_END_ELEMENT_TAG = 39;
  protected static final int SCANNER_STATE_CHAR_REFERENCE = 40;
  protected static final int SCANNER_STATE_BUILT_IN_REFS = 41;
  protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  protected static final String STANDARD_URI_CONFORMANT = "http://apache.org/xml/features/standard-uri-conformant";
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  static final String EXTERNAL_ACCESS_DEFAULT = "all";
  private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/validation", "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://apache.org/xml/features/scanner/notify-char-refs", "report-cdata-event" };
  private static final Boolean[] FEATURE_DEFAULTS = { Boolean.TRUE, null, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE };
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
  private static final Object[] PROPERTY_DEFAULTS = { null, null, null, null };
  private static final char[] cdata = { '[', 'C', 'D', 'A', 'T', 'A', '[' };
  static final char[] xmlDecl = { '<', '?', 'x', 'm', 'l' };
  private static final boolean DEBUG_SCANNER_STATE = false;
  private static final boolean DEBUG_DISPATCHER = false;
  protected static final boolean DEBUG_START_END_ELEMENT = false;
  protected static final boolean DEBUG_NEXT = false;
  protected static final boolean DEBUG = false;
  protected static final boolean DEBUG_COALESCE = false;
  protected XMLDocumentHandler fDocumentHandler;
  protected int fScannerLastState;
  protected XMLEntityStorage fEntityStore;
  protected int[] fEntityStack = new int[4];
  protected int fMarkupDepth;
  protected boolean fEmptyElement;
  protected boolean fReadingAttributes = false;
  protected int fScannerState;
  protected boolean fInScanContent = false;
  protected boolean fLastSectionWasCData = false;
  protected boolean fLastSectionWasEntityReference = false;
  protected boolean fLastSectionWasCharacterData = false;
  protected boolean fHasExternalDTD;
  protected boolean fStandaloneSet;
  protected boolean fStandalone;
  protected String fVersion;
  protected QName fCurrentElement;
  protected ElementStack fElementStack = new ElementStack();
  protected ElementStack2 fElementStack2 = new ElementStack2();
  protected String fPITarget;
  protected XMLString fPIData = new XMLString();
  protected boolean fNotifyBuiltInRefs = false;
  protected boolean fSupportDTD = true;
  protected boolean fReplaceEntityReferences = true;
  protected boolean fSupportExternalEntities = false;
  protected boolean fReportCdataEvent = false;
  protected boolean fIsCoalesce = false;
  protected String fDeclaredEncoding = null;
  protected boolean fDisallowDoctype = false;
  protected String fAccessExternalDTD = "all";
  protected boolean fStrictURI;
  protected Driver fDriver;
  protected Driver fContentDriver = createContentDriver();
  protected QName fElementQName = new QName();
  protected QName fAttributeQName = new QName();
  protected XMLAttributesIteratorImpl fAttributes = new XMLAttributesIteratorImpl();
  protected XMLString fTempString = new XMLString();
  protected XMLString fTempString2 = new XMLString();
  private String[] fStrings = new String[3];
  protected XMLStringBuffer fStringBuffer = new XMLStringBuffer();
  protected XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
  protected XMLStringBuffer fContentBuffer = new XMLStringBuffer();
  private final char[] fSingleChar = new char[1];
  private String fCurrentEntityName = null;
  protected boolean fScanToEnd = false;
  protected DTDGrammarUtil dtdGrammarUtil = null;
  protected boolean fAddDefaultAttr = false;
  protected boolean foundBuiltInRefs = false;
  static final short MAX_DEPTH_LIMIT = 5;
  static final short ELEMENT_ARRAY_LENGTH = 200;
  static final short MAX_POINTER_AT_A_DEPTH = 4;
  static final boolean DEBUG_SKIP_ALGORITHM = false;
  String[] fElementArray = new String['È'];
  short fLastPointerLocation = 0;
  short fElementPointer = 0;
  short[][] fPointerInfo = new short[5][4];
  protected String fElementRawname;
  protected boolean fShouldSkip = false;
  protected boolean fAdd = false;
  protected boolean fSkip = false;
  private Augmentations fTempAugmentations = null;
  protected boolean fUsebuffer;
  
  public XMLDocumentFragmentScannerImpl() {}
  
  public void setInputSource(XMLInputSource paramXMLInputSource)
    throws IOException
  {
    fEntityManager.setEntityHandler(this);
    fEntityManager.startEntity(false, "$fragment$", paramXMLInputSource, false, true);
  }
  
  public boolean scanDocument(boolean paramBoolean)
    throws IOException, XNIException
  {
    fEntityManager.setEntityHandler(this);
    int i = next();
    do
    {
      switch (i)
      {
      case 7: 
        break;
      case 1: 
        break;
      case 4: 
        fEntityScanner.checkNodeCount(fEntityScanner.fCurrentEntity);
        fDocumentHandler.characters(getCharacterData(), null);
        break;
      case 6: 
        break;
      case 9: 
        fEntityScanner.checkNodeCount(fEntityScanner.fCurrentEntity);
        break;
      case 3: 
        fEntityScanner.checkNodeCount(fEntityScanner.fCurrentEntity);
        fDocumentHandler.processingInstruction(getPITarget(), getPIData(), null);
        break;
      case 5: 
        fEntityScanner.checkNodeCount(fEntityScanner.fCurrentEntity);
        fDocumentHandler.comment(getCharacterData(), null);
        break;
      case 11: 
        break;
      case 12: 
        fEntityScanner.checkNodeCount(fEntityScanner.fCurrentEntity);
        fDocumentHandler.startCDATA(null);
        fDocumentHandler.characters(getCharacterData(), null);
        fDocumentHandler.endCDATA(null);
        break;
      case 14: 
        break;
      case 15: 
        break;
      case 13: 
        break;
      case 10: 
        break;
      case 2: 
        break;
      case 8: 
      default: 
        throw new InternalError("processing event: " + i);
      }
      i = next();
    } while ((i != 8) && (paramBoolean));
    if (i == 8)
    {
      fDocumentHandler.endDocument(null);
      return false;
    }
    return true;
  }
  
  public QName getElementQName()
  {
    if (fScannerLastState == 2) {
      fElementQName.setValues(fElementStack.getLastPoppedElement());
    }
    return fElementQName;
  }
  
  public int next()
    throws IOException, XNIException
  {
    return fDriver.next();
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException
  {
    super.reset(paramXMLComponentManager);
    fReportCdataEvent = paramXMLComponentManager.getFeature("report-cdata-event", true);
    fSecurityManager = ((XMLSecurityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/security-manager", null));
    fNotifyBuiltInRefs = paramXMLComponentManager.getFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", false);
    Object localObject = paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver", null);
    fExternalSubsetResolver = ((localObject instanceof ExternalSubsetResolver) ? (ExternalSubsetResolver)localObject : null);
    fReadingAttributes = false;
    fSupportExternalEntities = true;
    fReplaceEntityReferences = true;
    fIsCoalesce = false;
    setScannerState(22);
    setDriver(fContentDriver);
    XMLSecurityPropertyManager localXMLSecurityPropertyManager = (XMLSecurityPropertyManager)paramXMLComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", null);
    fAccessExternalDTD = localXMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    fStrictURI = paramXMLComponentManager.getFeature("http://apache.org/xml/features/standard-uri-conformant", false);
    resetCommon();
  }
  
  public void reset(PropertyManager paramPropertyManager)
  {
    super.reset(paramPropertyManager);
    fNamespaces = ((Boolean)paramPropertyManager.getProperty("javax.xml.stream.isNamespaceAware")).booleanValue();
    fNotifyBuiltInRefs = false;
    Boolean localBoolean1 = (Boolean)paramPropertyManager.getProperty("javax.xml.stream.isReplacingEntityReferences");
    fReplaceEntityReferences = localBoolean1.booleanValue();
    localBoolean1 = (Boolean)paramPropertyManager.getProperty("javax.xml.stream.isSupportingExternalEntities");
    fSupportExternalEntities = localBoolean1.booleanValue();
    Boolean localBoolean2 = (Boolean)paramPropertyManager.getProperty("http://java.sun.com/xml/stream/properties/report-cdata-event");
    if (localBoolean2 != null) {
      fReportCdataEvent = localBoolean2.booleanValue();
    }
    Boolean localBoolean3 = (Boolean)paramPropertyManager.getProperty("javax.xml.stream.isCoalescing");
    if (localBoolean3 != null) {
      fIsCoalesce = localBoolean3.booleanValue();
    }
    fReportCdataEvent = (!fIsCoalesce);
    fReplaceEntityReferences = (fIsCoalesce ? true : fReplaceEntityReferences);
    XMLSecurityPropertyManager localXMLSecurityPropertyManager = (XMLSecurityPropertyManager)paramPropertyManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
    fAccessExternalDTD = localXMLSecurityPropertyManager.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    fSecurityManager = ((XMLSecurityManager)paramPropertyManager.getProperty("http://apache.org/xml/properties/security-manager"));
    resetCommon();
  }
  
  void resetCommon()
  {
    fMarkupDepth = 0;
    fCurrentElement = null;
    fElementStack.clear();
    fHasExternalDTD = false;
    fStandaloneSet = false;
    fStandalone = false;
    fInScanContent = false;
    fShouldSkip = false;
    fAdd = false;
    fSkip = false;
    fEntityStore = fEntityManager.getEntityStore();
    dtdGrammarUtil = null;
    if (fSecurityManager != null)
    {
      fElementAttributeLimit = fSecurityManager.getLimit(XMLSecurityManager.Limit.ELEMENT_ATTRIBUTE_LIMIT);
      fXMLNameLimit = fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT);
    }
    else
    {
      fElementAttributeLimit = 0;
      fXMLNameLimit = XMLSecurityManager.Limit.MAX_NAME_LIMIT.defaultValue();
    }
    fLimitAnalyzer = fEntityManager.fLimitAnalyzer;
  }
  
  public String[] getRecognizedFeatures()
  {
    return (String[])RECOGNIZED_FEATURES.clone();
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {
    super.setFeature(paramString, paramBoolean);
    if (paramString.startsWith("http://apache.org/xml/features/"))
    {
      String str = paramString.substring("http://apache.org/xml/features/".length());
      if (str.equals("scanner/notify-builtin-refs")) {
        fNotifyBuiltInRefs = paramBoolean;
      }
    }
  }
  
  public String[] getRecognizedProperties()
  {
    return (String[])RECOGNIZED_PROPERTIES.clone();
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException
  {
    super.setProperty(paramString, paramObject);
    if (paramString.startsWith("http://apache.org/xml/properties/"))
    {
      int i = paramString.length() - "http://apache.org/xml/properties/".length();
      if ((i == "internal/entity-manager".length()) && (paramString.endsWith("internal/entity-manager")))
      {
        fEntityManager = ((XMLEntityManager)paramObject);
        return;
      }
      if ((i == "internal/entity-resolver".length()) && (paramString.endsWith("internal/entity-resolver")))
      {
        fExternalSubsetResolver = ((paramObject instanceof ExternalSubsetResolver) ? (ExternalSubsetResolver)paramObject : null);
        return;
      }
    }
    Object localObject;
    if (paramString.startsWith("http://apache.org/xml/properties/"))
    {
      localObject = paramString.substring("http://apache.org/xml/properties/".length());
      if (((String)localObject).equals("internal/entity-manager")) {
        fEntityManager = ((XMLEntityManager)paramObject);
      }
      return;
    }
    if (paramString.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager"))
    {
      localObject = (XMLSecurityPropertyManager)paramObject;
      fAccessExternalDTD = ((XMLSecurityPropertyManager)localObject).getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
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
  
  public void startEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if (fEntityDepth == fEntityStack.length)
    {
      int[] arrayOfInt = new int[fEntityStack.length * 2];
      System.arraycopy(fEntityStack, 0, arrayOfInt, 0, fEntityStack.length);
      fEntityStack = arrayOfInt;
    }
    fEntityStack[fEntityDepth] = fMarkupDepth;
    super.startEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations);
    if ((fStandalone) && (fEntityStore.isEntityDeclInExternalSubset(paramString1))) {
      reportFatalError("MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE", new Object[] { paramString1 });
    }
    if ((fDocumentHandler != null) && (!fScanningAttribute) && (!paramString1.equals("[xml]"))) {
      fDocumentHandler.startGeneralEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations);
    }
  }
  
  public void endEntity(String paramString, Augmentations paramAugmentations)
    throws IOException, XNIException
  {
    super.endEntity(paramString, paramAugmentations);
    if (fMarkupDepth != fEntityStack[fEntityDepth]) {
      reportFatalError("MarkupEntityMismatch", null);
    }
    if ((fDocumentHandler != null) && (!fScanningAttribute) && (!paramString.equals("[xml]"))) {
      fDocumentHandler.endGeneralEntity(paramString, paramAugmentations);
    }
  }
  
  protected Driver createContentDriver()
  {
    return new FragmentContentDriver();
  }
  
  protected void scanXMLDeclOrTextDecl(boolean paramBoolean)
    throws IOException, XNIException
  {
    super.scanXMLDeclOrTextDecl(paramBoolean, fStrings);
    fMarkupDepth -= 1;
    String str1 = fStrings[0];
    String str2 = fStrings[1];
    String str3 = fStrings[2];
    fDeclaredEncoding = str2;
    fStandaloneSet = (str3 != null);
    fStandalone = ((fStandaloneSet) && (str3.equals("yes")));
    fEntityManager.setStandalone(fStandalone);
    if (fDocumentHandler != null) {
      if (paramBoolean) {
        fDocumentHandler.textDecl(str1, str2, null);
      } else {
        fDocumentHandler.xmlDecl(str1, str2, str3, null);
      }
    }
    if (str1 != null)
    {
      fEntityScanner.setVersion(str1);
      fEntityScanner.setXMLVersion(str1);
    }
    if ((str2 != null) && (!fEntityScanner.getCurrentEntity().isEncodingExternallySpecified())) {
      fEntityScanner.setEncoding(str2);
    }
  }
  
  public String getPITarget()
  {
    return fPITarget;
  }
  
  public XMLStringBuffer getPIData()
  {
    return fContentBuffer;
  }
  
  public XMLString getCharacterData()
  {
    if (fUsebuffer) {
      return fContentBuffer;
    }
    return fTempString;
  }
  
  protected void scanPIData(String paramString, XMLStringBuffer paramXMLStringBuffer)
    throws IOException, XNIException
  {
    super.scanPIData(paramString, paramXMLStringBuffer);
    fPITarget = paramString;
    fMarkupDepth -= 1;
  }
  
  protected void scanComment()
    throws IOException, XNIException
  {
    fContentBuffer.clear();
    scanComment(fContentBuffer);
    fUsebuffer = true;
    fMarkupDepth -= 1;
  }
  
  public String getComment()
  {
    return fContentBuffer.toString();
  }
  
  void addElement(String paramString)
  {
    if (fElementPointer < 200)
    {
      fElementArray[fElementPointer] = paramString;
      if (fElementStack.fDepth < 5)
      {
        short s1 = storePointerForADepth(fElementPointer);
        if (s1 > 0)
        {
          short s2 = getElementPointer((short)fElementStack.fDepth, (short)(s1 - 1));
          if (paramString == fElementArray[s2])
          {
            fShouldSkip = true;
            fLastPointerLocation = s2;
            resetPointer((short)fElementStack.fDepth, s1);
            fElementArray[fElementPointer] = null;
            return;
          }
          fShouldSkip = false;
        }
      }
      fElementPointer = ((short)(fElementPointer + 1));
    }
  }
  
  void resetPointer(short paramShort1, short paramShort2)
  {
    fPointerInfo[paramShort1][paramShort2] = 0;
  }
  
  short storePointerForADepth(short paramShort)
  {
    short s1 = (short)fElementStack.fDepth;
    for (short s2 = 0; s2 < 4; s2 = (short)(s2 + 1)) {
      if (canStore(s1, s2))
      {
        fPointerInfo[s1][s2] = paramShort;
        return s2;
      }
    }
    return -1;
  }
  
  boolean canStore(short paramShort1, short paramShort2)
  {
    return fPointerInfo[paramShort1][paramShort2] == 0;
  }
  
  short getElementPointer(short paramShort1, short paramShort2)
  {
    return fPointerInfo[paramShort1][paramShort2];
  }
  
  boolean skipFromTheBuffer(String paramString)
    throws IOException
  {
    if (fEntityScanner.skipString(paramString))
    {
      int i = (char)fEntityScanner.peekChar();
      if ((i == 32) || (i == 47) || (i == 62))
      {
        fElementRawname = paramString;
        return true;
      }
      return false;
    }
    return false;
  }
  
  boolean skipQElement(String paramString)
    throws IOException
  {
    int i = fEntityScanner.getChar(paramString.length());
    if (XMLChar.isName(i)) {
      return false;
    }
    return fEntityScanner.skipString(paramString);
  }
  
  protected boolean skipElement()
    throws IOException
  {
    if (!fShouldSkip) {
      return false;
    }
    if (fLastPointerLocation != 0)
    {
      String str = fElementArray[(fLastPointerLocation + 1)];
      if ((str != null) && (skipFromTheBuffer(str)))
      {
        fLastPointerLocation = ((short)(fLastPointerLocation + 1));
        return true;
      }
      fLastPointerLocation = 0;
    }
    return (fShouldSkip) && (skipElement((short)0));
  }
  
  boolean skipElement(short paramShort)
    throws IOException
  {
    short s1 = (short)fElementStack.fDepth;
    if (s1 > 5) {
      return fShouldSkip = 0;
    }
    for (short s2 = paramShort; s2 < 4; s2 = (short)(s2 + 1))
    {
      short s3 = getElementPointer(s1, s2);
      if (s3 == 0) {
        return fShouldSkip = 0;
      }
      if ((fElementArray[s3] != null) && (skipFromTheBuffer(fElementArray[s3])))
      {
        fLastPointerLocation = s3;
        return fShouldSkip = 1;
      }
    }
    return fShouldSkip = 0;
  }
  
  protected boolean scanStartElement()
    throws IOException, XNIException
  {
    if ((fSkip) && (!fAdd))
    {
      localObject = fElementStack.getNext();
      fSkip = fEntityScanner.skipString(rawname);
      if (fSkip)
      {
        fElementStack.push();
        fElementQName = ((QName)localObject);
      }
      else
      {
        fElementStack.reposition();
      }
    }
    if ((!fSkip) || (fAdd))
    {
      fElementQName = fElementStack.nextElement();
      if (fNamespaces)
      {
        fEntityScanner.scanQName(fElementQName, XMLScanner.NameType.ELEMENTSTART);
      }
      else
      {
        localObject = fEntityScanner.scanName(XMLScanner.NameType.ELEMENTSTART);
        fElementQName.setValues(null, (String)localObject, (String)localObject, null);
      }
    }
    if (fAdd) {
      fElementStack.matchElement(fElementQName);
    }
    fCurrentElement = fElementQName;
    Object localObject = fElementQName.rawname;
    fEmptyElement = false;
    fAttributes.removeAllAttributes();
    checkDepth((String)localObject);
    if (!seekCloseOfStartTag())
    {
      fReadingAttributes = true;
      fAttributeCacheUsedCount = 0;
      fStringBufferIndex = 0;
      fAddDefaultAttr = true;
      do
      {
        scanAttribute(fAttributes);
        if ((fSecurityManager != null) && (!fSecurityManager.isNoLimit(fElementAttributeLimit)) && (fAttributes.getLength() > fElementAttributeLimit)) {
          fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementAttributeLimit", new Object[] { localObject, Integer.valueOf(fElementAttributeLimit) }, (short)2);
        }
      } while (!seekCloseOfStartTag());
      fReadingAttributes = false;
    }
    if (fEmptyElement)
    {
      fMarkupDepth -= 1;
      if (fMarkupDepth < fEntityStack[(fEntityDepth - 1)]) {
        reportFatalError("ElementEntityMismatch", new Object[] { fCurrentElement.rawname });
      }
      if (fDocumentHandler != null) {
        fDocumentHandler.emptyElement(fElementQName, fAttributes, null);
      }
      fElementStack.popElement();
    }
    else
    {
      if (dtdGrammarUtil != null) {
        dtdGrammarUtil.startElement(fElementQName, fAttributes);
      }
      if (fDocumentHandler != null) {
        fDocumentHandler.startElement(fElementQName, fAttributes, null);
      }
    }
    return fEmptyElement;
  }
  
  protected boolean seekCloseOfStartTag()
    throws IOException, XNIException
  {
    boolean bool = fEntityScanner.skipSpaces();
    int i = fEntityScanner.peekChar();
    if (i == 62)
    {
      fEntityScanner.scanChar(null);
      return true;
    }
    if (i == 47)
    {
      fEntityScanner.scanChar(null);
      if (!fEntityScanner.skipChar(62, XMLScanner.NameType.ELEMENTEND)) {
        reportFatalError("ElementUnterminated", new Object[] { fElementQName.rawname });
      }
      fEmptyElement = true;
      return true;
    }
    if (((!isValidNameStartChar(i)) || (!bool)) && ((!isValidNameStartHighSurrogate(i)) || (!bool))) {
      reportFatalError("ElementUnterminated", new Object[] { fElementQName.rawname });
    }
    return false;
  }
  
  public boolean hasAttributes()
  {
    return fAttributes.getLength() > 0;
  }
  
  public XMLAttributesIteratorImpl getAttributeIterator()
  {
    if ((dtdGrammarUtil != null) && (fAddDefaultAttr))
    {
      dtdGrammarUtil.addDTDDefaultAttrs(fElementQName, fAttributes);
      fAddDefaultAttr = false;
    }
    return fAttributes;
  }
  
  public boolean standaloneSet()
  {
    return fStandaloneSet;
  }
  
  public boolean isStandAlone()
  {
    return fStandalone;
  }
  
  protected void scanAttribute(XMLAttributes paramXMLAttributes)
    throws IOException, XNIException
  {
    if (fNamespaces)
    {
      fEntityScanner.scanQName(fAttributeQName, XMLScanner.NameType.ATTRIBUTENAME);
    }
    else
    {
      String str = fEntityScanner.scanName(XMLScanner.NameType.ATTRIBUTENAME);
      fAttributeQName.setValues(null, str, str, null);
    }
    fEntityScanner.skipSpaces();
    if (!fEntityScanner.skipChar(61, XMLScanner.NameType.ATTRIBUTE)) {
      reportFatalError("EqRequiredInAttribute", new Object[] { fCurrentElement.rawname, fAttributeQName.rawname });
    }
    fEntityScanner.skipSpaces();
    int i = 0;
    boolean bool = (fHasExternalDTD) && (!fStandalone);
    XMLString localXMLString = getString();
    scanAttributeValue(localXMLString, fTempString2, fAttributeQName.rawname, paramXMLAttributes, i, bool, fCurrentElement.rawname, false);
    int j = paramXMLAttributes.getLength();
    i = paramXMLAttributes.addAttribute(fAttributeQName, XMLSymbols.fCDATASymbol, null);
    if (j == paramXMLAttributes.getLength()) {
      reportFatalError("AttributeNotUnique", new Object[] { fCurrentElement.rawname, fAttributeQName.rawname });
    }
    paramXMLAttributes.setValue(i, null, localXMLString);
    paramXMLAttributes.setSpecified(i, true);
  }
  
  protected int scanContent(XMLStringBuffer paramXMLStringBuffer)
    throws IOException, XNIException
  {
    fTempString.length = 0;
    int i = fEntityScanner.scanContent(fTempString);
    paramXMLStringBuffer.append(fTempString);
    fTempString.length = 0;
    if (i == 13)
    {
      fEntityScanner.scanChar(null);
      paramXMLStringBuffer.append((char)i);
      i = -1;
    }
    else if (i == 93)
    {
      paramXMLStringBuffer.append((char)fEntityScanner.scanChar(null));
      fInScanContent = true;
      if (fEntityScanner.skipChar(93, null))
      {
        paramXMLStringBuffer.append(']');
        while (fEntityScanner.skipChar(93, null)) {
          paramXMLStringBuffer.append(']');
        }
        if (fEntityScanner.skipChar(62, null)) {
          reportFatalError("CDEndInContent", null);
        }
      }
      fInScanContent = false;
      i = -1;
    }
    if ((fDocumentHandler != null) && (length > 0)) {}
    return i;
  }
  
  protected boolean scanCDATASection(XMLStringBuffer paramXMLStringBuffer, boolean paramBoolean)
    throws IOException, XNIException
  {
    if (fDocumentHandler != null) {}
    while (fEntityScanner.scanData("]]>", paramXMLStringBuffer))
    {
      int i = fEntityScanner.peekChar();
      if ((i != -1) && (isInvalidLiteral(i))) {
        if (XMLChar.isHighSurrogate(i))
        {
          scanSurrogates(paramXMLStringBuffer);
        }
        else
        {
          reportFatalError("InvalidCharInCDSect", new Object[] { Integer.toString(i, 16) });
          fEntityScanner.scanChar(null);
        }
      }
      if (fDocumentHandler == null) {}
    }
    fMarkupDepth -= 1;
    if (((fDocumentHandler == null) || (length <= 0)) || (fDocumentHandler != null)) {}
    return true;
  }
  
  protected int scanEndElement()
    throws IOException, XNIException
  {
    QName localQName = fElementStack.popElement();
    String str = rawname;
    if (!fEntityScanner.skipString(rawname)) {
      reportFatalError("ETagRequired", new Object[] { str });
    }
    fEntityScanner.skipSpaces();
    if (!fEntityScanner.skipChar(62, XMLScanner.NameType.ELEMENTEND)) {
      reportFatalError("ETagUnterminated", new Object[] { str });
    }
    fMarkupDepth -= 1;
    fMarkupDepth -= 1;
    if (fMarkupDepth < fEntityStack[(fEntityDepth - 1)]) {
      reportFatalError("ElementEntityMismatch", new Object[] { str });
    }
    if (fDocumentHandler != null) {
      fDocumentHandler.endElement(localQName, null);
    }
    if (dtdGrammarUtil != null) {
      dtdGrammarUtil.endElement(localQName);
    }
    return fMarkupDepth;
  }
  
  protected void scanCharReference()
    throws IOException, XNIException
  {
    fStringBuffer2.clear();
    int i = scanCharReferenceValue(fStringBuffer2, null);
    fMarkupDepth -= 1;
    if ((i != -1) && (fDocumentHandler != null))
    {
      if (fNotifyCharRefs) {
        fDocumentHandler.startGeneralEntity(fCharRefLiteral, null, null, null);
      }
      Augmentations localAugmentations = null;
      if ((fValidation) && (i <= 32))
      {
        if (fTempAugmentations != null) {
          fTempAugmentations.removeAllItems();
        } else {
          fTempAugmentations = new AugmentationsImpl();
        }
        localAugmentations = fTempAugmentations;
        localAugmentations.putItem("CHAR_REF_PROBABLE_WS", Boolean.TRUE);
      }
      if (fNotifyCharRefs) {
        fDocumentHandler.endGeneralEntity(fCharRefLiteral, null);
      }
    }
  }
  
  protected void scanEntityReference(XMLStringBuffer paramXMLStringBuffer)
    throws IOException, XNIException
  {
    String str = fEntityScanner.scanName(XMLScanner.NameType.REFERENCE);
    if (str == null)
    {
      reportFatalError("NameRequiredInReference", null);
      return;
    }
    if (!fEntityScanner.skipChar(59, XMLScanner.NameType.REFERENCE)) {
      reportFatalError("SemicolonRequiredInReference", new Object[] { str });
    }
    if (fEntityStore.isUnparsedEntity(str)) {
      reportFatalError("ReferenceToUnparsedEntity", new Object[] { str });
    }
    fMarkupDepth -= 1;
    fCurrentEntityName = str;
    if (str == fAmpSymbol)
    {
      handleCharacter('&', fAmpSymbol, paramXMLStringBuffer);
      fScannerState = 41;
      return;
    }
    if (str == fLtSymbol)
    {
      handleCharacter('<', fLtSymbol, paramXMLStringBuffer);
      fScannerState = 41;
      return;
    }
    if (str == fGtSymbol)
    {
      handleCharacter('>', fGtSymbol, paramXMLStringBuffer);
      fScannerState = 41;
      return;
    }
    if (str == fQuotSymbol)
    {
      handleCharacter('"', fQuotSymbol, paramXMLStringBuffer);
      fScannerState = 41;
      return;
    }
    if (str == fAposSymbol)
    {
      handleCharacter('\'', fAposSymbol, paramXMLStringBuffer);
      fScannerState = 41;
      return;
    }
    boolean bool = fEntityStore.isExternalEntity(str);
    if (((bool) && (!fSupportExternalEntities)) || ((!bool) && (!fReplaceEntityReferences)) || (foundBuiltInRefs))
    {
      fScannerState = 28;
      return;
    }
    if (!fEntityStore.isDeclaredEntity(str))
    {
      if ((!fSupportDTD) && (fReplaceEntityReferences))
      {
        reportFatalError("EntityNotDeclared", new Object[] { str });
        return;
      }
      if ((fHasExternalDTD) && (!fStandalone))
      {
        if (fValidation) {
          fErrorReporter.reportError(fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { str }, (short)1);
        }
      }
      else {
        reportFatalError("EntityNotDeclared", new Object[] { str });
      }
    }
    fEntityManager.startEntity(true, str, false);
  }
  
  void checkDepth(String paramString)
  {
    fLimitAnalyzer.addValue(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT, paramString, fElementStack.fDepth);
    if (fSecurityManager.isOverLimit(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT, fLimitAnalyzer))
    {
      fSecurityManager.debugPrint(fLimitAnalyzer);
      reportFatalError("MaxElementDepthLimit", new Object[] { paramString, Integer.valueOf(fLimitAnalyzer.getTotalValue(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT)), Integer.valueOf(fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT)), "maxElementDepth" });
    }
  }
  
  private void handleCharacter(char paramChar, String paramString, XMLStringBuffer paramXMLStringBuffer)
    throws XNIException
  {
    foundBuiltInRefs = true;
    checkEntityLimit(false, fEntityScanner.fCurrentEntity.name, 1);
    paramXMLStringBuffer.append(paramChar);
    if (fDocumentHandler != null)
    {
      fSingleChar[0] = paramChar;
      if (fNotifyBuiltInRefs) {
        fDocumentHandler.startGeneralEntity(paramString, null, null, null);
      }
      fTempString.setValues(fSingleChar, 0, 1);
      if (fNotifyBuiltInRefs) {
        fDocumentHandler.endGeneralEntity(paramString, null);
      }
    }
  }
  
  protected final void setScannerState(int paramInt)
  {
    fScannerState = paramInt;
  }
  
  protected final void setDriver(Driver paramDriver)
  {
    fDriver = paramDriver;
  }
  
  protected String getScannerStateName(int paramInt)
  {
    switch (paramInt)
    {
    case 24: 
      return "SCANNER_STATE_DOCTYPE";
    case 26: 
      return "SCANNER_STATE_ROOT_ELEMENT";
    case 21: 
      return "SCANNER_STATE_START_OF_MARKUP";
    case 27: 
      return "SCANNER_STATE_COMMENT";
    case 23: 
      return "SCANNER_STATE_PI";
    case 22: 
      return "SCANNER_STATE_CONTENT";
    case 28: 
      return "SCANNER_STATE_REFERENCE";
    case 33: 
      return "SCANNER_STATE_END_OF_INPUT";
    case 34: 
      return "SCANNER_STATE_TERMINATED";
    case 35: 
      return "SCANNER_STATE_CDATA";
    case 36: 
      return "SCANNER_STATE_TEXT_DECL";
    case 29: 
      return "SCANNER_STATE_ATTRIBUTE";
    case 30: 
      return "SCANNER_STATE_ATTRIBUTE_VALUE";
    case 38: 
      return "SCANNER_STATE_START_ELEMENT_TAG";
    case 39: 
      return "SCANNER_STATE_END_ELEMENT_TAG";
    case 37: 
      return "SCANNER_STATE_CHARACTER_DATA";
    }
    return "??? (" + paramInt + ')';
  }
  
  public String getEntityName()
  {
    return fCurrentEntityName;
  }
  
  public String getDriverName(Driver paramDriver)
  {
    return "null";
  }
  
  String checkAccess(String paramString1, String paramString2)
    throws IOException
  {
    String str1 = fEntityScanner.getBaseSystemId();
    String str2 = XMLEntityManager.expandSystemId(paramString1, str1, fStrictURI);
    return SecuritySupport.checkAccess(str2, paramString2, "all");
  }
  
  static void pr(String paramString)
  {
    System.out.println(paramString);
  }
  
  protected XMLString getString()
  {
    if ((fAttributeCacheUsedCount < initialCacheCount) || (fAttributeCacheUsedCount < attributeValueCache.size())) {
      return (XMLString)attributeValueCache.get(fAttributeCacheUsedCount++);
    }
    XMLString localXMLString = new XMLString();
    fAttributeCacheUsedCount += 1;
    attributeValueCache.add(localXMLString);
    return localXMLString;
  }
  
  public void refresh()
  {
    refresh(0);
  }
  
  public void refresh(int paramInt)
  {
    if (fReadingAttributes) {
      fAttributes.refresh();
    }
    if (fScannerState == 37)
    {
      fContentBuffer.append(fTempString);
      fTempString.length = 0;
      fUsebuffer = true;
    }
  }
  
  protected static abstract interface Driver
  {
    public abstract int next()
      throws IOException, XNIException;
  }
  
  protected static final class Element
  {
    public QName qname;
    public char[] fRawname;
    public Element next;
    
    public Element(QName paramQName, Element paramElement)
    {
      qname.setValues(paramQName);
      fRawname = rawname.toCharArray();
      next = paramElement;
    }
  }
  
  protected class ElementStack
  {
    protected QName[] fElements = new QName[20];
    protected int[] fInt = new int[20];
    protected int fDepth;
    protected int fCount;
    protected int fPosition;
    protected int fMark;
    protected int fLastDepth;
    
    public ElementStack()
    {
      for (int i = 0; i < fElements.length; i++) {
        fElements[i] = new QName();
      }
    }
    
    public QName pushElement(QName paramQName)
    {
      if (fDepth == fElements.length)
      {
        QName[] arrayOfQName = new QName[fElements.length * 2];
        System.arraycopy(fElements, 0, arrayOfQName, 0, fDepth);
        fElements = arrayOfQName;
        for (int i = fDepth; i < fElements.length; i++) {
          fElements[i] = new QName();
        }
      }
      fElements[fDepth].setValues(paramQName);
      return fElements[(fDepth++)];
    }
    
    public QName getNext()
    {
      if (fPosition == fCount) {
        fPosition = fMark;
      }
      return fElements[fPosition];
    }
    
    public void push()
    {
      fInt[(++fDepth)] = (fPosition++);
    }
    
    public boolean matchElement(QName paramQName)
    {
      boolean bool = false;
      if ((fLastDepth > fDepth) && (fDepth <= 3)) {
        if (rawname == fElements[(fDepth - 1)].rawname)
        {
          fAdd = false;
          fMark = (fDepth - 1);
          fPosition = fMark;
          bool = true;
          fCount -= 1;
        }
        else
        {
          fAdd = true;
        }
      }
      if (bool) {
        fInt[fDepth] = (fPosition++);
      } else {
        fInt[fDepth] = (fCount - 1);
      }
      if (fCount == fElements.length)
      {
        fSkip = false;
        fAdd = false;
        reposition();
        return false;
      }
      fLastDepth = fDepth;
      return bool;
    }
    
    public QName nextElement()
    {
      if (fSkip)
      {
        fDepth += 1;
        return fElements[(fCount++)];
      }
      if (fDepth == fElements.length)
      {
        QName[] arrayOfQName = new QName[fElements.length * 2];
        System.arraycopy(fElements, 0, arrayOfQName, 0, fDepth);
        fElements = arrayOfQName;
        for (int i = fDepth; i < fElements.length; i++) {
          fElements[i] = new QName();
        }
      }
      return fElements[(fDepth++)];
    }
    
    public QName popElement()
    {
      if ((fSkip) || (fAdd)) {
        return fElements[fInt[(fDepth--)]];
      }
      return fElements[(--fDepth)];
    }
    
    public void reposition()
    {
      for (int i = 2; i <= fDepth; i++) {
        fElements[(i - 1)] = fElements[fInt[i]];
      }
    }
    
    public void clear()
    {
      fDepth = 0;
      fLastDepth = 0;
      fCount = 0;
      fPosition = (fMark = 1);
    }
    
    public QName getLastPoppedElement()
    {
      return fElements[fDepth];
    }
  }
  
  protected class ElementStack2
  {
    protected QName[] fQName = new QName[20];
    protected int fDepth;
    protected int fCount;
    protected int fPosition;
    protected int fMark;
    protected int fLastDepth;
    
    public ElementStack2()
    {
      for (int i = 0; i < fQName.length; i++) {
        fQName[i] = new QName();
      }
      fMark = (fPosition = 1);
    }
    
    public void resize()
    {
      int i = fQName.length;
      QName[] arrayOfQName = new QName[i * 2];
      System.arraycopy(fQName, 0, arrayOfQName, 0, i);
      fQName = arrayOfQName;
      for (int j = i; j < fQName.length; j++) {
        fQName[j] = new QName();
      }
    }
    
    public boolean matchElement(QName paramQName)
    {
      boolean bool = false;
      if ((fLastDepth > fDepth) && (fDepth <= 2)) {
        if (rawname == fQName[fDepth].rawname)
        {
          fAdd = false;
          fMark = (fDepth - 1);
          fPosition = (fMark + 1);
          bool = true;
          fCount -= 1;
        }
        else
        {
          fAdd = true;
        }
      }
      fLastDepth = (fDepth++);
      return bool;
    }
    
    public QName nextElement()
    {
      if (fCount == fQName.length)
      {
        fShouldSkip = false;
        fAdd = false;
        return fQName[(--fCount)];
      }
      return fQName[(fCount++)];
    }
    
    public QName getNext()
    {
      if (fPosition == fCount) {
        fPosition = fMark;
      }
      return fQName[(fPosition++)];
    }
    
    public int popElement()
    {
      return fDepth--;
    }
    
    public void clear()
    {
      fLastDepth = 0;
      fDepth = 0;
      fCount = 0;
      fPosition = (fMark = 1);
    }
  }
  
  protected class FragmentContentDriver
    implements XMLDocumentFragmentScannerImpl.Driver
  {
    protected FragmentContentDriver() {}
    
    private void startOfMarkup()
      throws IOException
    {
      fMarkupDepth += 1;
      int i = fEntityScanner.peekChar();
      if ((isValidNameStartChar(i)) || (isValidNameStartHighSurrogate(i))) {
        setScannerState(38);
      } else {
        switch (i)
        {
        case 63: 
          setScannerState(23);
          fEntityScanner.skipChar(i, null);
          break;
        case 33: 
          fEntityScanner.skipChar(i, null);
          if (fEntityScanner.skipChar(45, null))
          {
            if (!fEntityScanner.skipChar(45, XMLScanner.NameType.COMMENT)) {
              reportFatalError("InvalidCommentStart", null);
            }
            setScannerState(27);
          }
          else if (fEntityScanner.skipString(XMLDocumentFragmentScannerImpl.cdata))
          {
            setScannerState(35);
          }
          else if (!scanForDoctypeHook())
          {
            reportFatalError("MarkupNotRecognizedInContent", null);
          }
          break;
        case 47: 
          setScannerState(39);
          fEntityScanner.skipChar(i, XMLScanner.NameType.ELEMENTEND);
          break;
        default: 
          reportFatalError("MarkupNotRecognizedInContent", null);
        }
      }
    }
    
    private void startOfContent()
      throws IOException
    {
      if (fEntityScanner.skipChar(60, null)) {
        setScannerState(21);
      } else if (fEntityScanner.skipChar(38, XMLScanner.NameType.REFERENCE)) {
        setScannerState(28);
      } else {
        setScannerState(37);
      }
    }
    
    public void decideSubState()
      throws IOException
    {
      while ((fScannerState == 22) || (fScannerState == 21)) {
        switch (fScannerState)
        {
        case 22: 
          startOfContent();
          break;
        case 21: 
          startOfMarkup();
        }
      }
    }
    
    public int next()
      throws IOException, XNIException
    {
      try
      {
        for (;;)
        {
          int i;
          switch (fScannerState)
          {
          case 22: 
            i = fEntityScanner.peekChar();
            if (i == 60)
            {
              fEntityScanner.scanChar(null);
              setScannerState(21);
            }
            else if (i == 38)
            {
              fEntityScanner.scanChar(XMLScanner.NameType.REFERENCE);
              setScannerState(28);
            }
            else
            {
              setScannerState(37);
            }
            break;
          case 21: 
            startOfMarkup();
          }
          if (fIsCoalesce)
          {
            fUsebuffer = true;
            if (fLastSectionWasCharacterData)
            {
              if ((fScannerState != 35) && (fScannerState != 28) && (fScannerState != 37))
              {
                fLastSectionWasCharacterData = false;
                return 4;
              }
            }
            else if (((fLastSectionWasCData) || (fLastSectionWasEntityReference)) && (fScannerState != 35) && (fScannerState != 28) && (fScannerState != 37))
            {
              fLastSectionWasCData = false;
              fLastSectionWasEntityReference = false;
              return 4;
            }
          }
          switch (fScannerState)
          {
          case 7: 
            return 7;
          case 38: 
            fEmptyElement = scanStartElement();
            if (fEmptyElement) {
              setScannerState(39);
            } else {
              setScannerState(22);
            }
            return 1;
          case 37: 
            fUsebuffer = ((fLastSectionWasEntityReference) || (fLastSectionWasCData) || (fLastSectionWasCharacterData));
            if ((fIsCoalesce) && ((fLastSectionWasEntityReference) || (fLastSectionWasCData) || (fLastSectionWasCharacterData)))
            {
              fLastSectionWasEntityReference = false;
              fLastSectionWasCData = false;
              fLastSectionWasCharacterData = true;
              fUsebuffer = true;
            }
            else
            {
              fContentBuffer.clear();
            }
            fTempString.length = 0;
            i = fEntityScanner.scanContent(fTempString);
            if (fEntityScanner.skipChar(60, null))
            {
              if (fEntityScanner.skipChar(47, XMLScanner.NameType.ELEMENTEND))
              {
                fMarkupDepth += 1;
                fLastSectionWasCharacterData = false;
                setScannerState(39);
              }
              else if (XMLChar.isNameStart(fEntityScanner.peekChar()))
              {
                fMarkupDepth += 1;
                fLastSectionWasCharacterData = false;
                setScannerState(38);
              }
              else
              {
                setScannerState(21);
                if (fIsCoalesce)
                {
                  fUsebuffer = true;
                  fLastSectionWasCharacterData = true;
                  fContentBuffer.append(fTempString);
                  fTempString.length = 0;
                  continue;
                }
              }
              if (fUsebuffer)
              {
                fContentBuffer.append(fTempString);
                fTempString.length = 0;
              }
              if ((dtdGrammarUtil != null) && (dtdGrammarUtil.isIgnorableWhiteSpace(fContentBuffer))) {
                return 6;
              }
              return 4;
            }
            else
            {
              fUsebuffer = true;
              fContentBuffer.append(fTempString);
              fTempString.length = 0;
              if (i == 13)
              {
                fEntityScanner.scanChar(null);
                fUsebuffer = true;
                fContentBuffer.append((char)i);
                i = -1;
              }
              else if (i == 93)
              {
                fUsebuffer = true;
                fContentBuffer.append((char)fEntityScanner.scanChar(null));
                fInScanContent = true;
                if (fEntityScanner.skipChar(93, null))
                {
                  fContentBuffer.append(']');
                  while (fEntityScanner.skipChar(93, null)) {
                    fContentBuffer.append(']');
                  }
                  if (fEntityScanner.skipChar(62, null)) {
                    reportFatalError("CDEndInContent", null);
                  }
                }
                i = -1;
                fInScanContent = false;
              }
              do
              {
                if (i == 60)
                {
                  fEntityScanner.scanChar(null);
                  setScannerState(21);
                  break;
                }
                if (i == 38)
                {
                  fEntityScanner.scanChar(XMLScanner.NameType.REFERENCE);
                  setScannerState(28);
                  break;
                }
                if ((i != -1) && (isInvalidLiteral(i)))
                {
                  if (XMLChar.isHighSurrogate(i))
                  {
                    scanSurrogates(fContentBuffer);
                    setScannerState(22);
                    break;
                  }
                  reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(i, 16) });
                  fEntityScanner.scanChar(null);
                  break;
                }
                i = scanContent(fContentBuffer);
              } while (fIsCoalesce);
              setScannerState(22);
              if (fIsCoalesce)
              {
                fLastSectionWasCharacterData = true;
              }
              else
              {
                if ((dtdGrammarUtil != null) && (dtdGrammarUtil.isIgnorableWhiteSpace(fContentBuffer))) {
                  return 6;
                }
                return 4;
              }
            }
            break;
          case 39: 
            if (fEmptyElement)
            {
              fEmptyElement = false;
              setScannerState(22);
              return (fMarkupDepth == 0) && (elementDepthIsZeroHook()) ? 2 : 2;
            }
            if ((scanEndElement() == 0) && (elementDepthIsZeroHook())) {
              return 2;
            }
            setScannerState(22);
            return 2;
          case 27: 
            scanComment();
            setScannerState(22);
            return 5;
          case 23: 
            fContentBuffer.clear();
            scanPI(fContentBuffer);
            setScannerState(22);
            return 3;
          case 35: 
            if ((fIsCoalesce) && ((fLastSectionWasEntityReference) || (fLastSectionWasCData) || (fLastSectionWasCharacterData)))
            {
              fLastSectionWasCData = true;
              fLastSectionWasEntityReference = false;
              fLastSectionWasCharacterData = false;
            }
            else
            {
              fContentBuffer.clear();
            }
            fUsebuffer = true;
            scanCDATASection(fContentBuffer, true);
            setScannerState(22);
            if (fIsCoalesce)
            {
              fLastSectionWasCData = true;
            }
            else
            {
              if (fReportCdataEvent) {
                return 12;
              }
              return 4;
            }
            break;
          case 28: 
            fMarkupDepth += 1;
            foundBuiltInRefs = false;
            if ((fIsCoalesce) && ((fLastSectionWasEntityReference) || (fLastSectionWasCData) || (fLastSectionWasCharacterData)))
            {
              fLastSectionWasEntityReference = true;
              fLastSectionWasCData = false;
              fLastSectionWasCharacterData = false;
            }
            else
            {
              fContentBuffer.clear();
            }
            fUsebuffer = true;
            if (fEntityScanner.skipChar(35, XMLScanner.NameType.REFERENCE))
            {
              scanCharReferenceValue(fContentBuffer, null);
              fMarkupDepth -= 1;
              if (!fIsCoalesce)
              {
                setScannerState(22);
                return 4;
              }
            }
            else
            {
              scanEntityReference(fContentBuffer);
              if ((fScannerState == 41) && (!fIsCoalesce))
              {
                setScannerState(22);
                return 4;
              }
              if (fScannerState == 36)
              {
                fLastSectionWasEntityReference = true;
                continue;
              }
              if (fScannerState == 28)
              {
                setScannerState(22);
                if ((fReplaceEntityReferences) && (fEntityStore.isDeclaredEntity(fCurrentEntityName))) {
                  continue;
                }
                return 9;
              }
            }
            setScannerState(22);
            fLastSectionWasEntityReference = true;
            break;
          case 36: 
            if (fEntityScanner.skipString("<?xml"))
            {
              fMarkupDepth += 1;
              if (isValidNameChar(fEntityScanner.peekChar()))
              {
                fStringBuffer.clear();
                fStringBuffer.append("xml");
                if (fNamespaces) {
                  while (isValidNCName(fEntityScanner.peekChar())) {
                    fStringBuffer.append((char)fEntityScanner.scanChar(null));
                  }
                }
                while (isValidNameChar(fEntityScanner.peekChar())) {
                  fStringBuffer.append((char)fEntityScanner.scanChar(null));
                }
                String str = fSymbolTable.addSymbol(fStringBuffer.ch, fStringBuffer.offset, fStringBuffer.length);
                fContentBuffer.clear();
                scanPIData(str, fContentBuffer);
              }
              else
              {
                scanXMLDeclOrTextDecl(true);
              }
            }
            fEntityManager.fCurrentEntity.mayReadChunks = true;
            setScannerState(22);
          }
        }
        if (scanRootElementHook())
        {
          fEmptyElement = true;
          return 1;
        }
        setScannerState(22);
        return 1;
        fContentBuffer.clear();
        scanCharReferenceValue(fContentBuffer, null);
        fMarkupDepth -= 1;
        setScannerState(22);
        return 4;
        throw new XNIException("Scanner State " + fScannerState + " not Recognized ");
      }
      catch (EOFException localEOFException)
      {
        endOfFileHook(localEOFException);
      }
      return -1;
    }
    
    protected boolean scanForDoctypeHook()
      throws IOException, XNIException
    {
      return false;
    }
    
    protected boolean elementDepthIsZeroHook()
      throws IOException, XNIException
    {
      return false;
    }
    
    protected boolean scanRootElementHook()
      throws IOException, XNIException
    {
      return false;
    }
    
    protected void endOfFileHook(EOFException paramEOFException)
      throws IOException, XNIException
    {
      if (fMarkupDepth != 0) {
        reportFatalError("PrematureEOF", null);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLDocumentFragmentScannerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */