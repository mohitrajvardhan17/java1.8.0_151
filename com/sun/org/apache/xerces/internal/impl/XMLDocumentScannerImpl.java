package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity.ScannedEntity;
import com.sun.xml.internal.stream.StaxXMLInputSource;
import com.sun.xml.internal.stream.XMLEntityStorage;
import com.sun.xml.internal.stream.dtd.DTDGrammarUtil;
import java.io.EOFException;
import java.io.IOException;
import java.util.NoSuchElementException;

public class XMLDocumentScannerImpl
  extends XMLDocumentFragmentScannerImpl
{
  protected static final int SCANNER_STATE_XML_DECL = 42;
  protected static final int SCANNER_STATE_PROLOG = 43;
  protected static final int SCANNER_STATE_TRAILING_MISC = 44;
  protected static final int SCANNER_STATE_DTD_INTERNAL_DECLS = 45;
  protected static final int SCANNER_STATE_DTD_EXTERNAL = 46;
  protected static final int SCANNER_STATE_DTD_EXTERNAL_DECLS = 47;
  protected static final int SCANNER_STATE_NO_SUCH_ELEMENT_EXCEPTION = 48;
  protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
  protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  protected static final String DISALLOW_DOCTYPE_DECL_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
  protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  protected static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
  private static final String[] RECOGNIZED_FEATURES = { "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://apache.org/xml/features/disallow-doctype-decl" };
  private static final Boolean[] FEATURE_DEFAULTS = { Boolean.TRUE, Boolean.FALSE };
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/validation-manager" };
  private static final Object[] PROPERTY_DEFAULTS = { null, null };
  protected XMLDTDScanner fDTDScanner = null;
  protected ValidationManager fValidationManager;
  protected XMLStringBuffer fDTDDecl = null;
  protected boolean fReadingDTD = false;
  protected boolean fAddedListener = false;
  protected String fDoctypeName;
  protected String fDoctypePublicId;
  protected String fDoctypeSystemId;
  protected NamespaceContext fNamespaceContext = new NamespaceSupport();
  protected boolean fLoadExternalDTD = true;
  protected boolean fSeenDoctypeDecl;
  protected boolean fScanEndElement;
  protected XMLDocumentFragmentScannerImpl.Driver fXMLDeclDriver = new XMLDeclDriver();
  protected XMLDocumentFragmentScannerImpl.Driver fPrologDriver = new PrologDriver();
  protected XMLDocumentFragmentScannerImpl.Driver fDTDDriver = null;
  protected XMLDocumentFragmentScannerImpl.Driver fTrailingMiscDriver = new TrailingMiscDriver();
  protected int fStartPos = 0;
  protected int fEndPos = 0;
  protected boolean fSeenInternalSubset = false;
  private String[] fStrings = new String[3];
  private XMLInputSource fExternalSubsetSource = null;
  private final XMLDTDDescription fDTDDescription = new XMLDTDDescription(null, null, null, null, null);
  private static final char[] DOCTYPE = { 'D', 'O', 'C', 'T', 'Y', 'P', 'E' };
  private static final char[] COMMENTSTRING = { '-', '-' };
  
  public XMLDocumentScannerImpl() {}
  
  public void setInputSource(XMLInputSource paramXMLInputSource)
    throws IOException
  {
    fEntityManager.setEntityHandler(this);
    fEntityManager.startDocumentEntity(paramXMLInputSource);
    setScannerState(7);
  }
  
  public int getScannetState()
  {
    return fScannerState;
  }
  
  public void reset(PropertyManager paramPropertyManager)
  {
    super.reset(paramPropertyManager);
    fDoctypeName = null;
    fDoctypePublicId = null;
    fDoctypeSystemId = null;
    fSeenDoctypeDecl = false;
    fNamespaceContext.reset();
    fSupportDTD = ((Boolean)paramPropertyManager.getProperty("javax.xml.stream.supportDTD")).booleanValue();
    fLoadExternalDTD = (!((Boolean)paramPropertyManager.getProperty("http://java.sun.com/xml/stream/properties/ignore-external-dtd")).booleanValue());
    setScannerState(7);
    setDriver(fXMLDeclDriver);
    fSeenInternalSubset = false;
    if (fDTDScanner != null) {
      ((XMLDTDScannerImpl)fDTDScanner).reset(paramPropertyManager);
    }
    fEndPos = 0;
    fStartPos = 0;
    if (fDTDDecl != null) {
      fDTDDecl.clear();
    }
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException
  {
    super.reset(paramXMLComponentManager);
    fDoctypeName = null;
    fDoctypePublicId = null;
    fDoctypeSystemId = null;
    fSeenDoctypeDecl = false;
    fExternalSubsetSource = null;
    fLoadExternalDTD = paramXMLComponentManager.getFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
    fDisallowDoctype = paramXMLComponentManager.getFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
    fNamespaces = paramXMLComponentManager.getFeature("http://xml.org/sax/features/namespaces", true);
    fSeenInternalSubset = false;
    fDTDScanner = ((XMLDTDScanner)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/dtd-scanner"));
    fValidationManager = ((ValidationManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager", null));
    try
    {
      fNamespaceContext = ((NamespaceContext)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context"));
    }
    catch (XMLConfigurationException localXMLConfigurationException) {}
    if (fNamespaceContext == null) {
      fNamespaceContext = new NamespaceSupport();
    }
    fNamespaceContext.reset();
    fEndPos = 0;
    fStartPos = 0;
    if (fDTDDecl != null) {
      fDTDDecl.clear();
    }
    setScannerState(42);
    setDriver(fXMLDeclDriver);
  }
  
  public String[] getRecognizedFeatures()
  {
    String[] arrayOfString1 = super.getRecognizedFeatures();
    int i = arrayOfString1 != null ? arrayOfString1.length : 0;
    String[] arrayOfString2 = new String[i + RECOGNIZED_FEATURES.length];
    if (arrayOfString1 != null) {
      System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, arrayOfString1.length);
    }
    System.arraycopy(RECOGNIZED_FEATURES, 0, arrayOfString2, i, RECOGNIZED_FEATURES.length);
    return arrayOfString2;
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {
    super.setFeature(paramString, paramBoolean);
    if (paramString.startsWith("http://apache.org/xml/features/"))
    {
      int i = paramString.length() - "http://apache.org/xml/features/".length();
      if ((i == "nonvalidating/load-external-dtd".length()) && (paramString.endsWith("nonvalidating/load-external-dtd")))
      {
        fLoadExternalDTD = paramBoolean;
        return;
      }
      if ((i == "disallow-doctype-decl".length()) && (paramString.endsWith("disallow-doctype-decl")))
      {
        fDisallowDoctype = paramBoolean;
        return;
      }
    }
  }
  
  public String[] getRecognizedProperties()
  {
    String[] arrayOfString1 = super.getRecognizedProperties();
    int i = arrayOfString1 != null ? arrayOfString1.length : 0;
    String[] arrayOfString2 = new String[i + RECOGNIZED_PROPERTIES.length];
    if (arrayOfString1 != null) {
      System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, arrayOfString1.length);
    }
    System.arraycopy(RECOGNIZED_PROPERTIES, 0, arrayOfString2, i, RECOGNIZED_PROPERTIES.length);
    return arrayOfString2;
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException
  {
    super.setProperty(paramString, paramObject);
    if (paramString.startsWith("http://apache.org/xml/properties/"))
    {
      int i = paramString.length() - "http://apache.org/xml/properties/".length();
      if ((i == "internal/dtd-scanner".length()) && (paramString.endsWith("internal/dtd-scanner"))) {
        fDTDScanner = ((XMLDTDScanner)paramObject);
      }
      if ((i == "internal/namespace-context".length()) && (paramString.endsWith("internal/namespace-context")) && (paramObject != null)) {
        fNamespaceContext = ((NamespaceContext)paramObject);
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
    return super.getFeatureDefault(paramString);
  }
  
  public Object getPropertyDefault(String paramString)
  {
    for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
      if (RECOGNIZED_PROPERTIES[i].equals(paramString)) {
        return PROPERTY_DEFAULTS[i];
      }
    }
    return super.getPropertyDefault(paramString);
  }
  
  public void startEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    super.startEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations);
    fEntityScanner.registerListener(this);
    if ((!paramString1.equals("[xml]")) && (fEntityScanner.isExternal()) && ((paramAugmentations == null) || (!((Boolean)paramAugmentations.getItem("ENTITY_SKIPPED")).booleanValue()))) {
      setScannerState(36);
    }
    if ((fDocumentHandler != null) && (paramString1.equals("[xml]"))) {
      fDocumentHandler.startDocument(fEntityScanner, paramString2, fNamespaceContext, null);
    }
  }
  
  public void endEntity(String paramString, Augmentations paramAugmentations)
    throws IOException, XNIException
  {
    super.endEntity(paramString, paramAugmentations);
    if (paramString.equals("[xml]")) {
      if ((fMarkupDepth == 0) && (fDriver == fTrailingMiscDriver)) {
        setScannerState(34);
      } else {
        throw new EOFException();
      }
    }
  }
  
  public XMLStringBuffer getDTDDecl()
  {
    Entity.ScannedEntity localScannedEntity = fEntityScanner.getCurrentEntity();
    fDTDDecl.append(ch, fStartPos, fEndPos - fStartPos);
    if (fSeenInternalSubset) {
      fDTDDecl.append("]>");
    }
    return fDTDDecl;
  }
  
  public String getCharacterEncodingScheme()
  {
    return fDeclaredEncoding;
  }
  
  public int next()
    throws IOException, XNIException
  {
    return fDriver.next();
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return fNamespaceContext;
  }
  
  protected XMLDocumentFragmentScannerImpl.Driver createContentDriver()
  {
    return new ContentDriver();
  }
  
  protected boolean scanDoctypeDecl(boolean paramBoolean)
    throws IOException, XNIException
  {
    if (!fEntityScanner.skipSpaces()) {
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ROOT_ELEMENT_TYPE_IN_DOCTYPEDECL", null);
    }
    fDoctypeName = fEntityScanner.scanName(XMLScanner.NameType.DOCTYPE);
    if (fDoctypeName == null) {
      reportFatalError("MSG_ROOT_ELEMENT_TYPE_REQUIRED", null);
    }
    if (fEntityScanner.skipSpaces())
    {
      scanExternalID(fStrings, false);
      fDoctypeSystemId = fStrings[0];
      fDoctypePublicId = fStrings[1];
      fEntityScanner.skipSpaces();
    }
    fHasExternalDTD = (fDoctypeSystemId != null);
    if ((paramBoolean) && (!fHasExternalDTD) && (fExternalSubsetResolver != null))
    {
      fDTDDescription.setValues(null, null, fEntityManager.getCurrentResourceIdentifier().getExpandedSystemId(), null);
      fDTDDescription.setRootName(fDoctypeName);
      fExternalSubsetSource = fExternalSubsetResolver.getExternalSubset(fDTDDescription);
      fHasExternalDTD = (fExternalSubsetSource != null);
    }
    if ((paramBoolean) && (fDocumentHandler != null)) {
      if (fExternalSubsetSource == null) {
        fDocumentHandler.doctypeDecl(fDoctypeName, fDoctypePublicId, fDoctypeSystemId, null);
      } else {
        fDocumentHandler.doctypeDecl(fDoctypeName, fExternalSubsetSource.getPublicId(), fExternalSubsetSource.getSystemId(), null);
      }
    }
    boolean bool = true;
    if (!fEntityScanner.skipChar(91, null))
    {
      bool = false;
      fEntityScanner.skipSpaces();
      if (!fEntityScanner.skipChar(62, null)) {
        reportFatalError("DoctypedeclUnterminated", new Object[] { fDoctypeName });
      }
      fMarkupDepth -= 1;
    }
    return bool;
  }
  
  protected void setEndDTDScanState()
  {
    setScannerState(43);
    setDriver(fPrologDriver);
    fEntityManager.setEntityHandler(this);
    fReadingDTD = false;
  }
  
  protected String getScannerStateName(int paramInt)
  {
    switch (paramInt)
    {
    case 42: 
      return "SCANNER_STATE_XML_DECL";
    case 43: 
      return "SCANNER_STATE_PROLOG";
    case 44: 
      return "SCANNER_STATE_TRAILING_MISC";
    case 45: 
      return "SCANNER_STATE_DTD_INTERNAL_DECLS";
    case 46: 
      return "SCANNER_STATE_DTD_EXTERNAL";
    case 47: 
      return "SCANNER_STATE_DTD_EXTERNAL_DECLS";
    }
    return super.getScannerStateName(paramInt);
  }
  
  public void refresh(int paramInt)
  {
    super.refresh(paramInt);
    if (fReadingDTD)
    {
      Entity.ScannedEntity localScannedEntity = fEntityScanner.getCurrentEntity();
      if ((localScannedEntity instanceof Entity.ScannedEntity)) {
        fEndPos = position;
      }
      fDTDDecl.append(ch, fStartPos, fEndPos - fStartPos);
      fStartPos = paramInt;
    }
  }
  
  protected class ContentDriver
    extends XMLDocumentFragmentScannerImpl.FragmentContentDriver
  {
    protected ContentDriver()
    {
      super();
    }
    
    protected boolean scanForDoctypeHook()
      throws IOException, XNIException
    {
      if (fEntityScanner.skipString(XMLDocumentScannerImpl.DOCTYPE))
      {
        setScannerState(24);
        return true;
      }
      return false;
    }
    
    protected boolean elementDepthIsZeroHook()
      throws IOException, XNIException
    {
      setScannerState(44);
      setDriver(fTrailingMiscDriver);
      return true;
    }
    
    protected boolean scanRootElementHook()
      throws IOException, XNIException
    {
      if (scanStartElement())
      {
        setScannerState(44);
        setDriver(fTrailingMiscDriver);
        return true;
      }
      return false;
    }
    
    protected void endOfFileHook(EOFException paramEOFException)
      throws IOException, XNIException
    {
      reportFatalError("PrematureEOF", null);
    }
    
    /* Error */
    protected void resolveExternalSubsetAndRead()
      throws IOException, XNIException
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   4: invokestatic 154	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:access$200	(Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;)Lcom/sun/org/apache/xerces/internal/impl/dtd/XMLDTDDescription;
      //   7: aconst_null
      //   8: aconst_null
      //   9: aload_0
      //   10: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   13: getfield 139	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fEntityManager	Lcom/sun/org/apache/xerces/internal/impl/XMLEntityManager;
      //   16: invokevirtual 157	com/sun/org/apache/xerces/internal/impl/XMLEntityManager:getCurrentResourceIdentifier	()Lcom/sun/org/apache/xerces/internal/xni/XMLResourceIdentifier;
      //   19: invokeinterface 165 1 0
      //   24: aconst_null
      //   25: invokevirtual 160	com/sun/org/apache/xerces/internal/impl/dtd/XMLDTDDescription:setValues	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
      //   28: aload_0
      //   29: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   32: invokestatic 154	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:access$200	(Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;)Lcom/sun/org/apache/xerces/internal/impl/dtd/XMLDTDDescription;
      //   35: aload_0
      //   36: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   39: getfield 141	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fElementQName	Lcom/sun/org/apache/xerces/internal/xni/QName;
      //   42: getfield 148	com/sun/org/apache/xerces/internal/xni/QName:rawname	Ljava/lang/String;
      //   45: invokevirtual 159	com/sun/org/apache/xerces/internal/impl/dtd/XMLDTDDescription:setRootName	(Ljava/lang/String;)V
      //   48: aload_0
      //   49: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   52: getfield 137	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fExternalSubsetResolver	Lcom/sun/org/apache/xerces/internal/impl/ExternalSubsetResolver;
      //   55: aload_0
      //   56: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   59: invokestatic 154	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:access$200	(Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;)Lcom/sun/org/apache/xerces/internal/impl/dtd/XMLDTDDescription;
      //   62: invokeinterface 163 2 0
      //   67: astore_1
      //   68: aload_1
      //   69: ifnull +152 -> 221
      //   72: aload_0
      //   73: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   76: aload_0
      //   77: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   80: getfield 141	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fElementQName	Lcom/sun/org/apache/xerces/internal/xni/QName;
      //   83: getfield 148	com/sun/org/apache/xerces/internal/xni/QName:rawname	Ljava/lang/String;
      //   86: putfield 144	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fDoctypeName	Ljava/lang/String;
      //   89: aload_0
      //   90: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   93: aload_1
      //   94: invokevirtual 161	com/sun/org/apache/xerces/internal/xni/parser/XMLInputSource:getPublicId	()Ljava/lang/String;
      //   97: putfield 145	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fDoctypePublicId	Ljava/lang/String;
      //   100: aload_0
      //   101: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   104: aload_1
      //   105: invokevirtual 162	com/sun/org/apache/xerces/internal/xni/parser/XMLInputSource:getSystemId	()Ljava/lang/String;
      //   108: putfield 146	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fDoctypeSystemId	Ljava/lang/String;
      //   111: aload_0
      //   112: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   115: getfield 142	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fDocumentHandler	Lcom/sun/org/apache/xerces/internal/xni/XMLDocumentHandler;
      //   118: ifnull +37 -> 155
      //   121: aload_0
      //   122: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   125: getfield 142	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fDocumentHandler	Lcom/sun/org/apache/xerces/internal/xni/XMLDocumentHandler;
      //   128: aload_0
      //   129: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   132: getfield 144	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fDoctypeName	Ljava/lang/String;
      //   135: aload_0
      //   136: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   139: getfield 145	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fDoctypePublicId	Ljava/lang/String;
      //   142: aload_0
      //   143: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   146: getfield 146	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fDoctypeSystemId	Ljava/lang/String;
      //   149: aconst_null
      //   150: invokeinterface 164 5 0
      //   155: aload_0
      //   156: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   159: getfield 143	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fDTDScanner	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLDTDScanner;
      //   162: aload_1
      //   163: invokeinterface 167 2 0
      //   168: aload_0
      //   169: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   172: getfield 143	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fDTDScanner	Lcom/sun/org/apache/xerces/internal/xni/parser/XMLDTDScanner;
      //   175: iconst_1
      //   176: invokeinterface 166 2 0
      //   181: ifeq +6 -> 187
      //   184: goto -16 -> 168
      //   187: aload_0
      //   188: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   191: getfield 139	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fEntityManager	Lcom/sun/org/apache/xerces/internal/impl/XMLEntityManager;
      //   194: aload_0
      //   195: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   198: invokevirtual 156	com/sun/org/apache/xerces/internal/impl/XMLEntityManager:setEntityHandler	(Lcom/sun/org/apache/xerces/internal/impl/XMLEntityHandler;)V
      //   201: goto +20 -> 221
      //   204: astore_2
      //   205: aload_0
      //   206: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   209: getfield 139	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl:fEntityManager	Lcom/sun/org/apache/xerces/internal/impl/XMLEntityManager;
      //   212: aload_0
      //   213: getfield 147	com/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl$ContentDriver:this$0	Lcom/sun/org/apache/xerces/internal/impl/XMLDocumentScannerImpl;
      //   216: invokevirtual 156	com/sun/org/apache/xerces/internal/impl/XMLEntityManager:setEntityHandler	(Lcom/sun/org/apache/xerces/internal/impl/XMLEntityHandler;)V
      //   219: aload_2
      //   220: athrow
      //   221: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	222	0	this	ContentDriver
      //   67	96	1	localXMLInputSource	XMLInputSource
      //   204	16	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   155	187	204	finally
    }
  }
  
  protected final class DTDDriver
    implements XMLDocumentFragmentScannerImpl.Driver
  {
    protected DTDDriver() {}
    
    public int next()
      throws IOException, XNIException
    {
      dispatch(true);
      if (fPropertyManager != null) {
        dtdGrammarUtil = new DTDGrammarUtil(((XMLDTDScannerImpl)fDTDScanner).getGrammar(), fSymbolTable, fNamespaceContext);
      }
      return 11;
    }
    
    public boolean dispatch(boolean paramBoolean)
      throws IOException, XNIException
    {
      fEntityManager.setEntityHandler(null);
      try
      {
        XMLResourceIdentifierImpl localXMLResourceIdentifierImpl = new XMLResourceIdentifierImpl();
        if (fDTDScanner == null)
        {
          if ((fEntityManager.getEntityScanner() instanceof XML11EntityScanner)) {
            fDTDScanner = new XML11DTDScannerImpl();
          } else {
            fDTDScanner = new XMLDTDScannerImpl();
          }
          ((XMLDTDScannerImpl)fDTDScanner).reset(fPropertyManager);
        }
        fDTDScanner.setLimitAnalyzer(fLimitAnalyzer);
        int i;
        do
        {
          i = 0;
          Object localObject1;
          boolean bool3;
          switch (fScannerState)
          {
          case 45: 
            boolean bool2 = false;
            if (!fDTDScanner.skipDTD(fSupportDTD))
            {
              boolean bool4 = true;
              bool2 = fDTDScanner.scanDTDInternalSubset(bool4, fStandalone, (fHasExternalDTD) && (fLoadExternalDTD));
            }
            localObject1 = fEntityScanner.getCurrentEntity();
            if ((localObject1 instanceof Entity.ScannedEntity)) {
              fEndPos = position;
            }
            fReadingDTD = false;
            if (!bool2)
            {
              if (!fEntityScanner.skipChar(93, null)) {
                reportFatalError("DoctypedeclNotClosed", new Object[] { fDoctypeName });
              }
              fEntityScanner.skipSpaces();
              if (!fEntityScanner.skipChar(62, null)) {
                reportFatalError("DoctypedeclUnterminated", new Object[] { fDoctypeName });
              }
              fMarkupDepth -= 1;
              if (!fSupportDTD)
              {
                fEntityStore = fEntityManager.getEntityStore();
                fEntityStore.reset();
              }
              else if ((fDoctypeSystemId != null) && ((fValidation) || (fLoadExternalDTD)))
              {
                setScannerState(46);
                continue;
              }
              setEndDTDScanState();
              boolean bool6 = true;
              return bool6;
            }
            break;
          case 46: 
            localXMLResourceIdentifierImpl.setValues(fDoctypePublicId, fDoctypeSystemId, null, null);
            XMLInputSource localXMLInputSource = null;
            localObject1 = fEntityManager.resolveEntityAsPerStax(localXMLResourceIdentifierImpl);
            if (!((StaxXMLInputSource)localObject1).hasResolver())
            {
              String str = checkAccess(fDoctypeSystemId, fAccessExternalDTD);
              if (str != null) {
                reportFatalError("AccessExternalDTD", new Object[] { SecuritySupport.sanitizePath(fDoctypeSystemId), str });
              }
            }
            localXMLInputSource = ((StaxXMLInputSource)localObject1).getXMLInputSource();
            fDTDScanner.setInputSource(localXMLInputSource);
            if (fEntityScanner.fCurrentEntity != null) {
              setScannerState(47);
            } else {
              setScannerState(43);
            }
            i = 1;
            break;
          case 47: 
            bool3 = true;
            boolean bool5 = fDTDScanner.scanDTDExternalSubset(bool3);
            if (!bool5)
            {
              setEndDTDScanState();
              boolean bool7 = true;
              return bool7;
            }
            break;
          case 43: 
            setEndDTDScanState();
            bool3 = true;
            return bool3;
          case 44: 
          default: 
            throw new XNIException("DTDDriver#dispatch: scanner state=" + fScannerState + " (" + getScannerStateName(fScannerState) + ')');
          }
        } while ((paramBoolean) || (i != 0));
      }
      catch (EOFException localEOFException)
      {
        localEOFException.printStackTrace();
        reportFatalError("PrematureEOF", null);
        boolean bool1 = false;
        return bool1;
      }
      finally
      {
        fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
      }
      return true;
    }
  }
  
  protected final class PrologDriver
    implements XMLDocumentFragmentScannerImpl.Driver
  {
    protected PrologDriver() {}
    
    public int next()
      throws IOException, XNIException
    {
      try
      {
        Entity.ScannedEntity localScannedEntity;
        do
        {
          switch (fScannerState)
          {
          case 43: 
            fEntityScanner.skipSpaces();
            if (fEntityScanner.skipChar(60, null)) {
              setScannerState(21);
            } else if (fEntityScanner.skipChar(38, XMLScanner.NameType.REFERENCE)) {
              setScannerState(28);
            } else {
              setScannerState(22);
            }
            break;
          case 21: 
            fMarkupDepth += 1;
            if ((isValidNameStartChar(fEntityScanner.peekChar())) || (isValidNameStartHighSurrogate(fEntityScanner.peekChar())))
            {
              setScannerState(26);
              setDriver(fContentDriver);
              return fContentDriver.next();
            }
            if (fEntityScanner.skipChar(33, null))
            {
              if (fEntityScanner.skipChar(45, null))
              {
                if (!fEntityScanner.skipChar(45, null)) {
                  reportFatalError("InvalidCommentStart", null);
                }
                setScannerState(27);
              }
              else if (fEntityScanner.skipString(XMLDocumentScannerImpl.DOCTYPE))
              {
                setScannerState(24);
                localScannedEntity = fEntityScanner.getCurrentEntity();
                if ((localScannedEntity instanceof Entity.ScannedEntity)) {
                  fStartPos = position;
                }
                fReadingDTD = true;
                if (fDTDDecl == null) {
                  fDTDDecl = new XMLStringBuffer();
                }
                fDTDDecl.append("<!DOCTYPE");
              }
              else
              {
                reportFatalError("MarkupNotRecognizedInProlog", null);
              }
            }
            else if (fEntityScanner.skipChar(63, null)) {
              setScannerState(23);
            } else {
              reportFatalError("MarkupNotRecognizedInProlog", null);
            }
            break;
          }
        } while ((fScannerState == 43) || (fScannerState == 21));
        switch (fScannerState)
        {
        case 27: 
          scanComment();
          setScannerState(43);
          return 5;
        case 23: 
          fContentBuffer.clear();
          scanPI(fContentBuffer);
          setScannerState(43);
          return 3;
        case 24: 
          if (fDisallowDoctype) {
            reportFatalError("DoctypeNotAllowed", null);
          }
          if (fSeenDoctypeDecl) {
            reportFatalError("AlreadySeenDoctype", null);
          }
          fSeenDoctypeDecl = true;
          if (scanDoctypeDecl(fSupportDTD))
          {
            setScannerState(45);
            fSeenInternalSubset = true;
            if (fDTDDriver == null) {
              fDTDDriver = new XMLDocumentScannerImpl.DTDDriver(XMLDocumentScannerImpl.this);
            }
            setDriver(fContentDriver);
            return fDTDDriver.next();
          }
          if (fSeenDoctypeDecl)
          {
            localScannedEntity = fEntityScanner.getCurrentEntity();
            if ((localScannedEntity instanceof Entity.ScannedEntity)) {
              fEndPos = position;
            }
            fReadingDTD = false;
          }
          if (fDoctypeSystemId != null)
          {
            if (((fValidation) || (fLoadExternalDTD)) && ((fValidationManager == null) || (!fValidationManager.isCachedDTD())))
            {
              if (fSupportDTD) {
                setScannerState(46);
              } else {
                setScannerState(43);
              }
              setDriver(fContentDriver);
              if (fDTDDriver == null) {
                fDTDDriver = new XMLDocumentScannerImpl.DTDDriver(XMLDocumentScannerImpl.this);
              }
              return fDTDDriver.next();
            }
          }
          else if ((fExternalSubsetSource != null) && ((fValidation) || (fLoadExternalDTD)) && ((fValidationManager == null) || (!fValidationManager.isCachedDTD())))
          {
            fDTDScanner.setInputSource(fExternalSubsetSource);
            fExternalSubsetSource = null;
            if (fSupportDTD) {
              setScannerState(47);
            } else {
              setScannerState(43);
            }
            setDriver(fContentDriver);
            if (fDTDDriver == null) {
              fDTDDriver = new XMLDocumentScannerImpl.DTDDriver(XMLDocumentScannerImpl.this);
            }
            return fDTDDriver.next();
          }
          if (fDTDScanner != null) {
            fDTDScanner.setInputSource(null);
          }
          setScannerState(43);
          return 11;
        case 22: 
          reportFatalError("ContentIllegalInProlog", null);
          fEntityScanner.scanChar(null);
        case 28: 
          reportFatalError("ReferenceIllegalInProlog", null);
        }
      }
      catch (EOFException localEOFException)
      {
        reportFatalError("PrematureEOF", null);
        return -1;
      }
      return -1;
    }
  }
  
  protected final class TrailingMiscDriver
    implements XMLDocumentFragmentScannerImpl.Driver
  {
    protected TrailingMiscDriver() {}
    
    public int next()
      throws IOException, XNIException
    {
      if (fEmptyElement)
      {
        fEmptyElement = false;
        return 2;
      }
      try
      {
        if (fScannerState == 34) {
          return 8;
        }
        do
        {
          switch (fScannerState)
          {
          case 44: 
            fEntityScanner.skipSpaces();
            if (fScannerState == 34) {
              return 8;
            }
            if (fEntityScanner.skipChar(60, null)) {
              setScannerState(21);
            } else {
              setScannerState(22);
            }
            break;
          case 21: 
            fMarkupDepth += 1;
            if (fEntityScanner.skipChar(63, null))
            {
              setScannerState(23);
            }
            else if (fEntityScanner.skipChar(33, null))
            {
              setScannerState(27);
            }
            else if (fEntityScanner.skipChar(47, null))
            {
              reportFatalError("MarkupNotRecognizedInMisc", null);
            }
            else if ((isValidNameStartChar(fEntityScanner.peekChar())) || (isValidNameStartHighSurrogate(fEntityScanner.peekChar())))
            {
              reportFatalError("MarkupNotRecognizedInMisc", null);
              scanStartElement();
              setScannerState(22);
            }
            else
            {
              reportFatalError("MarkupNotRecognizedInMisc", null);
            }
            break;
          }
        } while ((fScannerState == 21) || (fScannerState == 44));
        switch (fScannerState)
        {
        case 23: 
          fContentBuffer.clear();
          scanPI(fContentBuffer);
          setScannerState(44);
          return 3;
        case 27: 
          if (!fEntityScanner.skipString(XMLDocumentScannerImpl.COMMENTSTRING)) {
            reportFatalError("InvalidCommentStart", null);
          }
          scanComment();
          setScannerState(44);
          return 5;
        case 22: 
          int i = fEntityScanner.peekChar();
          if (i == -1)
          {
            setScannerState(34);
            return 8;
          }
          reportFatalError("ContentIllegalInTrailingMisc", null);
          fEntityScanner.scanChar(null);
          setScannerState(44);
          return 4;
        case 28: 
          reportFatalError("ReferenceIllegalInTrailingMisc", null);
          setScannerState(44);
          return 9;
        case 34: 
          setScannerState(48);
          return 8;
        case 48: 
          throw new NoSuchElementException("No more events to be parsed");
        }
        throw new XNIException("Scanner State " + fScannerState + " not Recognized ");
      }
      catch (EOFException localEOFException)
      {
        if (fMarkupDepth != 0)
        {
          reportFatalError("PrematureEOF", null);
          return -1;
        }
        setScannerState(34);
      }
      return 8;
    }
  }
  
  protected final class XMLDeclDriver
    implements XMLDocumentFragmentScannerImpl.Driver
  {
    protected XMLDeclDriver() {}
    
    public int next()
      throws IOException, XNIException
    {
      setScannerState(43);
      setDriver(fPrologDriver);
      try
      {
        if (fEntityScanner.skipString(XMLDocumentFragmentScannerImpl.xmlDecl))
        {
          fMarkupDepth += 1;
          if (XMLChar.isName(fEntityScanner.peekChar()))
          {
            fStringBuffer.clear();
            fStringBuffer.append("xml");
            while (XMLChar.isName(fEntityScanner.peekChar())) {
              fStringBuffer.append((char)fEntityScanner.scanChar(null));
            }
            String str = fSymbolTable.addSymbol(fStringBuffer.ch, fStringBuffer.offset, fStringBuffer.length);
            fContentBuffer.clear();
            scanPIData(str, fContentBuffer);
            fEntityManager.fCurrentEntity.mayReadChunks = true;
            return 3;
          }
          scanXMLDeclOrTextDecl(false);
          fEntityManager.fCurrentEntity.mayReadChunks = true;
          return 7;
        }
        fEntityManager.fCurrentEntity.mayReadChunks = true;
        return 7;
      }
      catch (EOFException localEOFException)
      {
        reportFatalError("PrematureEOF", null);
      }
      return -1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLDocumentScannerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */