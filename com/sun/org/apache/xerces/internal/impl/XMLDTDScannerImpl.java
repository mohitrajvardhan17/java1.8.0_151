package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.Limit;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity.ScannedEntity;
import com.sun.xml.internal.stream.XMLEntityStorage;
import com.sun.xml.internal.stream.dtd.nonvalidating.DTDGrammar;
import java.io.EOFException;
import java.io.IOException;

public class XMLDTDScannerImpl
  extends XMLScanner
  implements XMLDTDScanner, XMLComponent, XMLEntityHandler
{
  protected static final int SCANNER_STATE_END_OF_INPUT = 0;
  protected static final int SCANNER_STATE_TEXT_DECL = 1;
  protected static final int SCANNER_STATE_MARKUP_DECL = 2;
  private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/validation", "http://apache.org/xml/features/scanner/notify-char-refs" };
  private static final Boolean[] FEATURE_DEFAULTS = { null, Boolean.FALSE };
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager" };
  private static final Object[] PROPERTY_DEFAULTS = { null, null, null };
  private static final boolean DEBUG_SCANNER_STATE = false;
  public XMLDTDHandler fDTDHandler = null;
  protected XMLDTDContentModelHandler fDTDContentModelHandler;
  protected int fScannerState;
  protected boolean fStandalone;
  protected boolean fSeenExternalDTD;
  protected boolean fSeenExternalPE;
  private boolean fStartDTDCalled;
  private XMLAttributesImpl fAttributes = new XMLAttributesImpl();
  private int[] fContentStack = new int[5];
  private int fContentDepth;
  private int[] fPEStack = new int[5];
  private boolean[] fPEReport = new boolean[5];
  private int fPEDepth;
  private int fMarkUpDepth;
  private int fExtEntityDepth;
  private int fIncludeSectDepth;
  private String[] fStrings = new String[3];
  private XMLString fString = new XMLString();
  private XMLStringBuffer fStringBuffer = new XMLStringBuffer();
  private XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
  private XMLString fLiteral = new XMLString();
  private XMLString fLiteral2 = new XMLString();
  private String[] fEnumeration = new String[5];
  private int fEnumerationCount;
  private XMLStringBuffer fIgnoreConditionalBuffer = new XMLStringBuffer(128);
  DTDGrammar nvGrammarInfo = null;
  boolean nonValidatingMode = false;
  
  public XMLDTDScannerImpl() {}
  
  public XMLDTDScannerImpl(SymbolTable paramSymbolTable, XMLErrorReporter paramXMLErrorReporter, XMLEntityManager paramXMLEntityManager)
  {
    fSymbolTable = paramSymbolTable;
    fErrorReporter = paramXMLErrorReporter;
    fEntityManager = paramXMLEntityManager;
    paramXMLEntityManager.setProperty("http://apache.org/xml/properties/internal/symbol-table", fSymbolTable);
  }
  
  public void setInputSource(XMLInputSource paramXMLInputSource)
    throws IOException
  {
    if (paramXMLInputSource == null)
    {
      if (fDTDHandler != null)
      {
        fDTDHandler.startDTD(null, null);
        fDTDHandler.endDTD(null);
      }
      if (nonValidatingMode)
      {
        nvGrammarInfo.startDTD(null, null);
        nvGrammarInfo.endDTD(null);
      }
      return;
    }
    fEntityManager.setEntityHandler(this);
    fEntityManager.startDTDEntity(paramXMLInputSource);
  }
  
  public void setLimitAnalyzer(XMLLimitAnalyzer paramXMLLimitAnalyzer)
  {
    fLimitAnalyzer = paramXMLLimitAnalyzer;
  }
  
  public boolean scanDTDExternalSubset(boolean paramBoolean)
    throws IOException, XNIException
  {
    fEntityManager.setEntityHandler(this);
    if (fScannerState == 1)
    {
      fSeenExternalDTD = true;
      boolean bool = scanTextDecl();
      if (fScannerState == 0) {
        return false;
      }
      setScannerState(2);
      if ((bool) && (!paramBoolean)) {
        return true;
      }
    }
    do
    {
      if (!scanDecls(paramBoolean)) {
        return false;
      }
    } while (paramBoolean);
    return true;
  }
  
  public boolean scanDTDInternalSubset(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    throws IOException, XNIException
  {
    fEntityScanner = fEntityManager.getEntityScanner();
    fEntityManager.setEntityHandler(this);
    fStandalone = paramBoolean2;
    if (fScannerState == 1)
    {
      if (fDTDHandler != null)
      {
        fDTDHandler.startDTD(fEntityScanner, null);
        fStartDTDCalled = true;
      }
      if (nonValidatingMode)
      {
        fStartDTDCalled = true;
        nvGrammarInfo.startDTD(fEntityScanner, null);
      }
      setScannerState(2);
    }
    do
    {
      if (!scanDecls(paramBoolean1))
      {
        if ((fDTDHandler != null) && (!paramBoolean3)) {
          fDTDHandler.endDTD(null);
        }
        if ((nonValidatingMode) && (!paramBoolean3)) {
          nvGrammarInfo.endDTD(null);
        }
        setScannerState(1);
        fLimitAnalyzer.reset(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT);
        fLimitAnalyzer.reset(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT);
        return false;
      }
    } while (paramBoolean1);
    return true;
  }
  
  public boolean skipDTD(boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {
      return false;
    }
    fStringBuffer.clear();
    while (fEntityScanner.scanData("]", fStringBuffer))
    {
      int i = fEntityScanner.peekChar();
      if (i != -1)
      {
        if (XMLChar.isHighSurrogate(i)) {
          scanSurrogates(fStringBuffer);
        }
        if (isInvalidLiteral(i))
        {
          reportFatalError("InvalidCharInDTD", new Object[] { Integer.toHexString(i) });
          fEntityScanner.scanChar(null);
        }
      }
    }
    fEntityScanner.fCurrentEntity.position -= 1;
    return true;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException
  {
    super.reset(paramXMLComponentManager);
    init();
  }
  
  public void reset()
  {
    super.reset();
    init();
  }
  
  public void reset(PropertyManager paramPropertyManager)
  {
    setPropertyManager(paramPropertyManager);
    super.reset(paramPropertyManager);
    init();
    nonValidatingMode = true;
    nvGrammarInfo = new DTDGrammar(fSymbolTable);
  }
  
  public String[] getRecognizedFeatures()
  {
    return (String[])RECOGNIZED_FEATURES.clone();
  }
  
  public String[] getRecognizedProperties()
  {
    return (String[])RECOGNIZED_PROPERTIES.clone();
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
  
  public void startEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    super.startEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations);
    boolean bool = paramString1.equals("[dtd]");
    if (bool)
    {
      if ((fDTDHandler != null) && (!fStartDTDCalled)) {
        fDTDHandler.startDTD(fEntityScanner, null);
      }
      if (fDTDHandler != null) {
        fDTDHandler.startExternalSubset(paramXMLResourceIdentifier, null);
      }
      fEntityManager.startExternalSubset();
      fEntityStore.startExternalSubset();
      fExtEntityDepth += 1;
    }
    else if (paramString1.charAt(0) == '%')
    {
      pushPEStack(fMarkUpDepth, fReportEntity);
      if (fEntityScanner.isExternal()) {
        fExtEntityDepth += 1;
      }
    }
    if ((fDTDHandler != null) && (!bool) && (fReportEntity)) {
      fDTDHandler.startParameterEntity(paramString1, paramXMLResourceIdentifier, paramString2, null);
    }
  }
  
  public void endEntity(String paramString, Augmentations paramAugmentations)
    throws XNIException, IOException
  {
    super.endEntity(paramString, paramAugmentations);
    if (fScannerState == 0) {
      return;
    }
    boolean bool1 = fReportEntity;
    if (paramString.startsWith("%"))
    {
      bool1 = peekReportEntity();
      int i = popPEStack();
      if ((i == 0) && (i < fMarkUpDepth)) {
        fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ILL_FORMED_PARAMETER_ENTITY_WHEN_USED_IN_DECL", new Object[] { fEntityManager.fCurrentEntity.name }, (short)2);
      }
      if (i != fMarkUpDepth)
      {
        bool1 = false;
        if (fValidation) {
          fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ImproperDeclarationNesting", new Object[] { paramString }, (short)1);
        }
      }
      if (fEntityScanner.isExternal()) {
        fExtEntityDepth -= 1;
      }
    }
    boolean bool2 = paramString.equals("[dtd]");
    if ((fDTDHandler != null) && (!bool2) && (bool1)) {
      fDTDHandler.endParameterEntity(paramString, null);
    }
    if (bool2)
    {
      if (fIncludeSectDepth != 0) {
        reportFatalError("IncludeSectUnterminated", null);
      }
      fScannerState = 0;
      fEntityManager.endExternalSubset();
      fEntityStore.endExternalSubset();
      if (fDTDHandler != null)
      {
        fDTDHandler.endExternalSubset(null);
        fDTDHandler.endDTD(null);
      }
      fExtEntityDepth -= 1;
    }
    if ((paramAugmentations != null) && (Boolean.TRUE.equals(paramAugmentations.getItem("LAST_ENTITY"))) && ((fMarkUpDepth != 0) || (fExtEntityDepth != 0) || (fIncludeSectDepth != 0))) {
      throw new EOFException();
    }
  }
  
  protected final void setScannerState(int paramInt)
  {
    fScannerState = paramInt;
  }
  
  private static String getScannerStateName(int paramInt)
  {
    return "??? (" + paramInt + ')';
  }
  
  protected final boolean scanningInternalSubset()
  {
    return fExtEntityDepth == 0;
  }
  
  protected void startPE(String paramString, boolean paramBoolean)
    throws IOException, XNIException
  {
    int i = fPEDepth;
    String str = "%" + paramString;
    if ((fValidation) && (!fEntityStore.isDeclaredEntity(str))) {
      fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { paramString }, (short)1);
    }
    fEntityManager.startEntity(false, fSymbolTable.addSymbol(str), paramBoolean);
    if ((i != fPEDepth) && (fEntityScanner.isExternal())) {
      scanTextDecl();
    }
  }
  
  protected final boolean scanTextDecl()
    throws IOException, XNIException
  {
    boolean bool = false;
    if (fEntityScanner.skipString("<?xml"))
    {
      fMarkUpDepth += 1;
      String str1;
      if (isValidNameChar(fEntityScanner.peekChar()))
      {
        fStringBuffer.clear();
        fStringBuffer.append("xml");
        while (isValidNameChar(fEntityScanner.peekChar())) {
          fStringBuffer.append((char)fEntityScanner.scanChar(null));
        }
        str1 = fSymbolTable.addSymbol(fStringBuffer.ch, fStringBuffer.offset, fStringBuffer.length);
        scanPIData(str1, fString);
      }
      else
      {
        str1 = null;
        String str2 = null;
        scanXMLDeclOrTextDecl(true, fStrings);
        bool = true;
        fMarkUpDepth -= 1;
        str1 = fStrings[0];
        str2 = fStrings[1];
        fEntityScanner.setEncoding(str2);
        if (fDTDHandler != null) {
          fDTDHandler.textDecl(str1, str2, null);
        }
      }
    }
    fEntityManager.fCurrentEntity.mayReadChunks = true;
    return bool;
  }
  
  protected final void scanPIData(String paramString, XMLString paramXMLString)
    throws IOException, XNIException
  {
    fMarkUpDepth -= 1;
    if (fDTDHandler != null) {
      fDTDHandler.processingInstruction(paramString, paramXMLString, null);
    }
  }
  
  protected final void scanComment()
    throws IOException, XNIException
  {
    fReportEntity = false;
    scanComment(fStringBuffer);
    fMarkUpDepth -= 1;
    if (fDTDHandler != null) {
      fDTDHandler.comment(fStringBuffer, null);
    }
    fReportEntity = true;
  }
  
  protected final void scanElementDecl()
    throws IOException, XNIException
  {
    fReportEntity = false;
    if (!skipSeparator(true, !scanningInternalSubset())) {
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ELEMENTDECL", null);
    }
    String str1 = fEntityScanner.scanName(XMLScanner.NameType.ELEMENTSTART);
    if (str1 == null) {
      reportFatalError("MSG_ELEMENT_TYPE_REQUIRED_IN_ELEMENTDECL", null);
    }
    if (!skipSeparator(true, !scanningInternalSubset())) {
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_CONTENTSPEC_IN_ELEMENTDECL", new Object[] { str1 });
    }
    if (fDTDContentModelHandler != null) {
      fDTDContentModelHandler.startContentModel(str1, null);
    }
    String str2 = null;
    fReportEntity = true;
    if (fEntityScanner.skipString("EMPTY"))
    {
      str2 = "EMPTY";
      if (fDTDContentModelHandler != null) {
        fDTDContentModelHandler.empty(null);
      }
    }
    else if (fEntityScanner.skipString("ANY"))
    {
      str2 = "ANY";
      if (fDTDContentModelHandler != null) {
        fDTDContentModelHandler.any(null);
      }
    }
    else
    {
      if (!fEntityScanner.skipChar(40, null)) {
        reportFatalError("MSG_OPEN_PAREN_OR_ELEMENT_TYPE_REQUIRED_IN_CHILDREN", new Object[] { str1 });
      }
      if (fDTDContentModelHandler != null) {
        fDTDContentModelHandler.startGroup(null);
      }
      fStringBuffer.clear();
      fStringBuffer.append('(');
      fMarkUpDepth += 1;
      skipSeparator(false, !scanningInternalSubset());
      if (fEntityScanner.skipString("#PCDATA")) {
        scanMixed(str1);
      } else {
        scanChildren(str1);
      }
      str2 = fStringBuffer.toString();
    }
    if (fDTDContentModelHandler != null) {
      fDTDContentModelHandler.endContentModel(null);
    }
    fReportEntity = false;
    skipSeparator(false, !scanningInternalSubset());
    if (!fEntityScanner.skipChar(62, null)) {
      reportFatalError("ElementDeclUnterminated", new Object[] { str1 });
    }
    fReportEntity = true;
    fMarkUpDepth -= 1;
    if (fDTDHandler != null) {
      fDTDHandler.elementDecl(str1, str2, null);
    }
    if (nonValidatingMode) {
      nvGrammarInfo.elementDecl(str1, str2, null);
    }
  }
  
  private final void scanMixed(String paramString)
    throws IOException, XNIException
  {
    String str = null;
    fStringBuffer.append("#PCDATA");
    if (fDTDContentModelHandler != null) {
      fDTDContentModelHandler.pcdata(null);
    }
    skipSeparator(false, !scanningInternalSubset());
    while (fEntityScanner.skipChar(124, null))
    {
      fStringBuffer.append('|');
      if (fDTDContentModelHandler != null) {
        fDTDContentModelHandler.separator((short)0, null);
      }
      skipSeparator(false, !scanningInternalSubset());
      str = fEntityScanner.scanName(XMLScanner.NameType.ENTITY);
      if (str == null) {
        reportFatalError("MSG_ELEMENT_TYPE_REQUIRED_IN_MIXED_CONTENT", new Object[] { paramString });
      }
      fStringBuffer.append(str);
      if (fDTDContentModelHandler != null) {
        fDTDContentModelHandler.element(str, null);
      }
      skipSeparator(false, !scanningInternalSubset());
    }
    if (fEntityScanner.skipString(")*"))
    {
      fStringBuffer.append(")*");
      if (fDTDContentModelHandler != null)
      {
        fDTDContentModelHandler.endGroup(null);
        fDTDContentModelHandler.occurrence((short)3, null);
      }
    }
    else if (str != null)
    {
      reportFatalError("MixedContentUnterminated", new Object[] { paramString });
    }
    else if (fEntityScanner.skipChar(41, null))
    {
      fStringBuffer.append(')');
      if (fDTDContentModelHandler != null) {
        fDTDContentModelHandler.endGroup(null);
      }
    }
    else
    {
      reportFatalError("MSG_CLOSE_PAREN_REQUIRED_IN_CHILDREN", new Object[] { paramString });
    }
    fMarkUpDepth -= 1;
  }
  
  private final void scanChildren(String paramString)
    throws IOException, XNIException
  {
    fContentDepth = 0;
    pushContentStack(0);
    int i = 0;
    for (;;)
    {
      if (fEntityScanner.skipChar(40, null))
      {
        fMarkUpDepth += 1;
        fStringBuffer.append('(');
        if (fDTDContentModelHandler != null) {
          fDTDContentModelHandler.startGroup(null);
        }
        pushContentStack(i);
        i = 0;
        skipSeparator(false, !scanningInternalSubset());
      }
      else
      {
        skipSeparator(false, !scanningInternalSubset());
        String str = fEntityScanner.scanName(XMLScanner.NameType.ELEMENTSTART);
        if (str == null)
        {
          reportFatalError("MSG_OPEN_PAREN_OR_ELEMENT_TYPE_REQUIRED_IN_CHILDREN", new Object[] { paramString });
          return;
        }
        if (fDTDContentModelHandler != null) {
          fDTDContentModelHandler.element(str, null);
        }
        fStringBuffer.append(str);
        int j = fEntityScanner.peekChar();
        short s;
        if ((j == 63) || (j == 42) || (j == 43))
        {
          if (fDTDContentModelHandler != null)
          {
            if (j == 63) {
              s = 2;
            } else if (j == 42) {
              s = 3;
            } else {
              s = 4;
            }
            fDTDContentModelHandler.occurrence(s, null);
          }
          fEntityScanner.scanChar(null);
          fStringBuffer.append((char)j);
        }
        do
        {
          skipSeparator(false, !scanningInternalSubset());
          j = fEntityScanner.peekChar();
          if ((j == 44) && (i != 124))
          {
            i = j;
            if (fDTDContentModelHandler != null) {
              fDTDContentModelHandler.separator((short)1, null);
            }
            fEntityScanner.scanChar(null);
            fStringBuffer.append(',');
            break;
          }
          if ((j == 124) && (i != 44))
          {
            i = j;
            if (fDTDContentModelHandler != null) {
              fDTDContentModelHandler.separator((short)0, null);
            }
            fEntityScanner.scanChar(null);
            fStringBuffer.append('|');
            break;
          }
          if (j != 41) {
            reportFatalError("MSG_CLOSE_PAREN_REQUIRED_IN_CHILDREN", new Object[] { paramString });
          }
          if (fDTDContentModelHandler != null) {
            fDTDContentModelHandler.endGroup(null);
          }
          i = popContentStack();
          if (fEntityScanner.skipString(")?"))
          {
            fStringBuffer.append(")?");
            if (fDTDContentModelHandler != null)
            {
              s = 2;
              fDTDContentModelHandler.occurrence(s, null);
            }
          }
          else if (fEntityScanner.skipString(")+"))
          {
            fStringBuffer.append(")+");
            if (fDTDContentModelHandler != null)
            {
              s = 4;
              fDTDContentModelHandler.occurrence(s, null);
            }
          }
          else if (fEntityScanner.skipString(")*"))
          {
            fStringBuffer.append(")*");
            if (fDTDContentModelHandler != null)
            {
              s = 3;
              fDTDContentModelHandler.occurrence(s, null);
            }
          }
          else
          {
            fEntityScanner.scanChar(null);
            fStringBuffer.append(')');
          }
          fMarkUpDepth -= 1;
        } while (fContentDepth != 0);
        return;
        skipSeparator(false, !scanningInternalSubset());
      }
    }
  }
  
  protected final void scanAttlistDecl()
    throws IOException, XNIException
  {
    fReportEntity = false;
    if (!skipSeparator(true, !scanningInternalSubset())) {
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ATTLISTDECL", null);
    }
    String str1 = fEntityScanner.scanName(XMLScanner.NameType.ELEMENTSTART);
    if (str1 == null) {
      reportFatalError("MSG_ELEMENT_TYPE_REQUIRED_IN_ATTLISTDECL", null);
    }
    if (fDTDHandler != null) {
      fDTDHandler.startAttlist(str1, null);
    }
    if (!skipSeparator(true, !scanningInternalSubset()))
    {
      if (fEntityScanner.skipChar(62, null))
      {
        if (fDTDHandler != null) {
          fDTDHandler.endAttlist(null);
        }
        fMarkUpDepth -= 1;
        return;
      }
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ATTRIBUTE_NAME_IN_ATTDEF", new Object[] { str1 });
    }
    while (!fEntityScanner.skipChar(62, null))
    {
      String str2 = fEntityScanner.scanName(XMLScanner.NameType.ATTRIBUTENAME);
      if (str2 == null) {
        reportFatalError("AttNameRequiredInAttDef", new Object[] { str1 });
      }
      if (!skipSeparator(true, !scanningInternalSubset())) {
        reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ATTTYPE_IN_ATTDEF", new Object[] { str1, str2 });
      }
      String str3 = scanAttType(str1, str2);
      if (!skipSeparator(true, !scanningInternalSubset())) {
        reportFatalError("MSG_SPACE_REQUIRED_BEFORE_DEFAULTDECL_IN_ATTDEF", new Object[] { str1, str2 });
      }
      String str4 = scanAttDefaultDecl(str1, str2, str3, fLiteral, fLiteral2);
      String[] arrayOfString = null;
      if (((fDTDHandler != null) || (nonValidatingMode)) && (fEnumerationCount != 0))
      {
        arrayOfString = new String[fEnumerationCount];
        System.arraycopy(fEnumeration, 0, arrayOfString, 0, fEnumerationCount);
      }
      if ((str4 != null) && ((str4.equals("#REQUIRED")) || (str4.equals("#IMPLIED"))))
      {
        if (fDTDHandler != null) {
          fDTDHandler.attributeDecl(str1, str2, str3, arrayOfString, str4, null, null, null);
        }
        if (nonValidatingMode) {
          nvGrammarInfo.attributeDecl(str1, str2, str3, arrayOfString, str4, null, null, null);
        }
      }
      else
      {
        if (fDTDHandler != null) {
          fDTDHandler.attributeDecl(str1, str2, str3, arrayOfString, str4, fLiteral, fLiteral2, null);
        }
        if (nonValidatingMode) {
          nvGrammarInfo.attributeDecl(str1, str2, str3, arrayOfString, str4, fLiteral, fLiteral2, null);
        }
      }
      skipSeparator(false, !scanningInternalSubset());
    }
    if (fDTDHandler != null) {
      fDTDHandler.endAttlist(null);
    }
    fMarkUpDepth -= 1;
    fReportEntity = true;
  }
  
  private final String scanAttType(String paramString1, String paramString2)
    throws IOException, XNIException
  {
    String str1 = null;
    fEnumerationCount = 0;
    if (fEntityScanner.skipString("CDATA"))
    {
      str1 = "CDATA";
    }
    else if (fEntityScanner.skipString("IDREFS"))
    {
      str1 = "IDREFS";
    }
    else if (fEntityScanner.skipString("IDREF"))
    {
      str1 = "IDREF";
    }
    else if (fEntityScanner.skipString("ID"))
    {
      str1 = "ID";
    }
    else if (fEntityScanner.skipString("ENTITY"))
    {
      str1 = "ENTITY";
    }
    else if (fEntityScanner.skipString("ENTITIES"))
    {
      str1 = "ENTITIES";
    }
    else if (fEntityScanner.skipString("NMTOKENS"))
    {
      str1 = "NMTOKENS";
    }
    else if (fEntityScanner.skipString("NMTOKEN"))
    {
      str1 = "NMTOKEN";
    }
    else
    {
      int i;
      String str2;
      if (fEntityScanner.skipString("NOTATION"))
      {
        str1 = "NOTATION";
        if (!skipSeparator(true, !scanningInternalSubset())) {
          reportFatalError("MSG_SPACE_REQUIRED_AFTER_NOTATION_IN_NOTATIONTYPE", new Object[] { paramString1, paramString2 });
        }
        i = fEntityScanner.scanChar(null);
        if (i != 40) {
          reportFatalError("MSG_OPEN_PAREN_REQUIRED_IN_NOTATIONTYPE", new Object[] { paramString1, paramString2 });
        }
        fMarkUpDepth += 1;
        do
        {
          skipSeparator(false, !scanningInternalSubset());
          str2 = fEntityScanner.scanName(XMLScanner.NameType.ATTRIBUTENAME);
          if (str2 == null) {
            reportFatalError("MSG_NAME_REQUIRED_IN_NOTATIONTYPE", new Object[] { paramString1, paramString2 });
          }
          ensureEnumerationSize(fEnumerationCount + 1);
          fEnumeration[(fEnumerationCount++)] = str2;
          skipSeparator(false, !scanningInternalSubset());
          i = fEntityScanner.scanChar(null);
        } while (i == 124);
        if (i != 41) {
          reportFatalError("NotationTypeUnterminated", new Object[] { paramString1, paramString2 });
        }
        fMarkUpDepth -= 1;
      }
      else
      {
        str1 = "ENUMERATION";
        i = fEntityScanner.scanChar(null);
        if (i != 40) {
          reportFatalError("AttTypeRequiredInAttDef", new Object[] { paramString1, paramString2 });
        }
        fMarkUpDepth += 1;
        do
        {
          skipSeparator(false, !scanningInternalSubset());
          str2 = fEntityScanner.scanNmtoken();
          if (str2 == null) {
            reportFatalError("MSG_NMTOKEN_REQUIRED_IN_ENUMERATION", new Object[] { paramString1, paramString2 });
          }
          ensureEnumerationSize(fEnumerationCount + 1);
          fEnumeration[(fEnumerationCount++)] = str2;
          skipSeparator(false, !scanningInternalSubset());
          i = fEntityScanner.scanChar(null);
        } while (i == 124);
        if (i != 41) {
          reportFatalError("EnumerationUnterminated", new Object[] { paramString1, paramString2 });
        }
        fMarkUpDepth -= 1;
      }
    }
    return str1;
  }
  
  protected final String scanAttDefaultDecl(String paramString1, String paramString2, String paramString3, XMLString paramXMLString1, XMLString paramXMLString2)
    throws IOException, XNIException
  {
    String str = null;
    fString.clear();
    paramXMLString1.clear();
    if (fEntityScanner.skipString("#REQUIRED"))
    {
      str = "#REQUIRED";
    }
    else if (fEntityScanner.skipString("#IMPLIED"))
    {
      str = "#IMPLIED";
    }
    else
    {
      if (fEntityScanner.skipString("#FIXED"))
      {
        str = "#FIXED";
        if (!skipSeparator(true, !scanningInternalSubset())) {
          reportFatalError("MSG_SPACE_REQUIRED_AFTER_FIXED_IN_DEFAULTDECL", new Object[] { paramString1, paramString2 });
        }
      }
      boolean bool = (!fStandalone) && ((fSeenExternalDTD) || (fSeenExternalPE));
      scanAttributeValue(paramXMLString1, paramXMLString2, paramString2, fAttributes, 0, bool, paramString1, false);
    }
    return str;
  }
  
  private final void scanEntityDecl()
    throws IOException, XNIException
  {
    boolean bool1 = false;
    int i = 0;
    fReportEntity = false;
    if (fEntityScanner.skipSpaces())
    {
      if (!fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE))
      {
        bool1 = false;
      }
      else if (skipSeparator(true, !scanningInternalSubset()))
      {
        bool1 = true;
      }
      else if (scanningInternalSubset())
      {
        reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_ENTITYDECL", null);
        bool1 = true;
      }
      else if (fEntityScanner.peekChar() == 37)
      {
        skipSeparator(false, !scanningInternalSubset());
        bool1 = true;
      }
      else
      {
        i = 1;
      }
    }
    else if ((scanningInternalSubset()) || (!fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE)))
    {
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_ENTITYDECL", null);
      bool1 = false;
    }
    else if (fEntityScanner.skipSpaces())
    {
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_PERCENT_IN_PEDECL", null);
      bool1 = false;
    }
    else
    {
      i = 1;
    }
    if (i != 0) {
      for (;;)
      {
        str1 = fEntityScanner.scanName(XMLScanner.NameType.REFERENCE);
        if (str1 == null) {
          reportFatalError("NameRequiredInPEReference", null);
        } else if (!fEntityScanner.skipChar(59, XMLScanner.NameType.REFERENCE)) {
          reportFatalError("SemicolonRequiredInPEReference", new Object[] { str1 });
        } else {
          startPE(str1, false);
        }
        fEntityScanner.skipSpaces();
        if (!fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE)) {
          break;
        }
        if (!bool1)
        {
          if (skipSeparator(true, !scanningInternalSubset()))
          {
            bool1 = true;
            break;
          }
          bool1 = fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE);
        }
      }
    }
    String str1 = fEntityScanner.scanName(XMLScanner.NameType.ENTITY);
    if (str1 == null) {
      reportFatalError("MSG_ENTITY_NAME_REQUIRED_IN_ENTITYDECL", null);
    }
    if (!skipSeparator(true, !scanningInternalSubset())) {
      reportFatalError("MSG_SPACE_REQUIRED_AFTER_ENTITY_NAME_IN_ENTITYDECL", new Object[] { str1 });
    }
    scanExternalID(fStrings, false);
    String str2 = fStrings[0];
    String str3 = fStrings[1];
    if ((bool1) && (str2 != null)) {
      fSeenExternalPE = true;
    }
    String str4 = null;
    boolean bool2 = skipSeparator(true, !scanningInternalSubset());
    if ((!bool1) && (fEntityScanner.skipString("NDATA")))
    {
      if (!bool2) {
        reportFatalError("MSG_SPACE_REQUIRED_BEFORE_NDATA_IN_UNPARSED_ENTITYDECL", new Object[] { str1 });
      }
      if (!skipSeparator(true, !scanningInternalSubset())) {
        reportFatalError("MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_UNPARSED_ENTITYDECL", new Object[] { str1 });
      }
      str4 = fEntityScanner.scanName(XMLScanner.NameType.NOTATION);
      if (str4 == null) {
        reportFatalError("MSG_NOTATION_NAME_REQUIRED_FOR_UNPARSED_ENTITYDECL", new Object[] { str1 });
      }
    }
    if (str2 == null)
    {
      scanEntityValue(str1, bool1, fLiteral, fLiteral2);
      fStringBuffer.clear();
      fStringBuffer2.clear();
      fStringBuffer.append(fLiteral.ch, fLiteral.offset, fLiteral.length);
      fStringBuffer2.append(fLiteral2.ch, fLiteral2.offset, fLiteral2.length);
    }
    skipSeparator(false, !scanningInternalSubset());
    if (!fEntityScanner.skipChar(62, null)) {
      reportFatalError("EntityDeclUnterminated", new Object[] { str1 });
    }
    fMarkUpDepth -= 1;
    if (bool1) {
      str1 = "%" + str1;
    }
    if (str2 != null)
    {
      String str5 = fEntityScanner.getBaseSystemId();
      if (str4 != null) {
        fEntityStore.addUnparsedEntity(str1, str3, str2, str5, str4);
      } else {
        fEntityStore.addExternalEntity(str1, str3, str2, str5);
      }
      if (fDTDHandler != null)
      {
        fResourceIdentifier.setValues(str3, str2, str5, XMLEntityManager.expandSystemId(str2, str5));
        if (str4 != null) {
          fDTDHandler.unparsedEntityDecl(str1, fResourceIdentifier, str4, null);
        } else {
          fDTDHandler.externalEntityDecl(str1, fResourceIdentifier, null);
        }
      }
    }
    else
    {
      fEntityStore.addInternalEntity(str1, fStringBuffer.toString());
      if (fDTDHandler != null) {
        fDTDHandler.internalEntityDecl(str1, fStringBuffer, fStringBuffer2, null);
      }
    }
    fReportEntity = true;
  }
  
  protected final void scanEntityValue(String paramString, boolean paramBoolean, XMLString paramXMLString1, XMLString paramXMLString2)
    throws IOException, XNIException
  {
    int i = fEntityScanner.scanChar(null);
    if ((i != 39) && (i != 34)) {
      reportFatalError("OpenQuoteMissingInDecl", null);
    }
    int j = fEntityDepth;
    Object localObject1 = fString;
    Object localObject2 = fString;
    int k = 0;
    if (fLimitAnalyzer == null) {
      fLimitAnalyzer = fEntityManager.fLimitAnalyzer;
    }
    fLimitAnalyzer.startEntity(paramString);
    if (fEntityScanner.scanLiteral(i, fString, false) != i)
    {
      fStringBuffer.clear();
      fStringBuffer2.clear();
      do
      {
        k = 0;
        int m = fStringBuffer.length;
        fStringBuffer.append(fString);
        fStringBuffer2.append(fString);
        String str;
        if (fEntityScanner.skipChar(38, XMLScanner.NameType.REFERENCE))
        {
          if (fEntityScanner.skipChar(35, XMLScanner.NameType.REFERENCE))
          {
            fStringBuffer2.append("&#");
            scanCharReferenceValue(fStringBuffer, fStringBuffer2);
          }
          else
          {
            fStringBuffer.append('&');
            fStringBuffer2.append('&');
            str = fEntityScanner.scanName(XMLScanner.NameType.REFERENCE);
            if (str == null)
            {
              reportFatalError("NameRequiredInReference", null);
            }
            else
            {
              fStringBuffer.append(str);
              fStringBuffer2.append(str);
            }
            if (!fEntityScanner.skipChar(59, XMLScanner.NameType.REFERENCE))
            {
              reportFatalError("SemicolonRequiredInReference", new Object[] { str });
            }
            else
            {
              fStringBuffer.append(';');
              fStringBuffer2.append(';');
            }
          }
        }
        else
        {
          if (fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE)) {
            for (;;)
            {
              fStringBuffer2.append('%');
              str = fEntityScanner.scanName(XMLScanner.NameType.REFERENCE);
              if (str == null)
              {
                reportFatalError("NameRequiredInPEReference", null);
              }
              else if (!fEntityScanner.skipChar(59, XMLScanner.NameType.REFERENCE))
              {
                reportFatalError("SemicolonRequiredInPEReference", new Object[] { str });
              }
              else
              {
                if (scanningInternalSubset()) {
                  reportFatalError("PEReferenceWithinMarkup", new Object[] { str });
                }
                fStringBuffer2.append(str);
                fStringBuffer2.append(';');
              }
              startPE(str, true);
              fEntityScanner.skipSpaces();
              if (!fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE)) {
                break;
              }
            }
          }
          int n = fEntityScanner.peekChar();
          if (XMLChar.isHighSurrogate(n))
          {
            k++;
            scanSurrogates(fStringBuffer2);
          }
          else if (isInvalidLiteral(n))
          {
            reportFatalError("InvalidCharInLiteral", new Object[] { Integer.toHexString(n) });
            fEntityScanner.scanChar(null);
          }
          else if ((n != i) || (j != fEntityDepth))
          {
            fStringBuffer.append((char)n);
            fStringBuffer2.append((char)n);
            fEntityScanner.scanChar(null);
          }
        }
        checkEntityLimit(paramBoolean, paramString, fStringBuffer.length - m + k);
      } while (fEntityScanner.scanLiteral(i, fString, false) != i);
      checkEntityLimit(paramBoolean, paramString, fString.length);
      fStringBuffer.append(fString);
      fStringBuffer2.append(fString);
      localObject1 = fStringBuffer;
      localObject2 = fStringBuffer2;
    }
    else
    {
      checkEntityLimit(paramBoolean, paramString, (XMLString)localObject1);
    }
    paramXMLString1.setValues((XMLString)localObject1);
    paramXMLString2.setValues((XMLString)localObject2);
    if (fLimitAnalyzer != null) {
      if (paramBoolean) {
        fLimitAnalyzer.endEntity(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT, paramString);
      } else {
        fLimitAnalyzer.endEntity(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, paramString);
      }
    }
    if (!fEntityScanner.skipChar(i, null)) {
      reportFatalError("CloseQuoteMissingInDecl", null);
    }
  }
  
  private final void scanNotationDecl()
    throws IOException, XNIException
  {
    fReportEntity = false;
    if (!skipSeparator(true, !scanningInternalSubset())) {
      reportFatalError("MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_NOTATIONDECL", null);
    }
    String str1 = fEntityScanner.scanName(XMLScanner.NameType.NOTATION);
    if (str1 == null) {
      reportFatalError("MSG_NOTATION_NAME_REQUIRED_IN_NOTATIONDECL", null);
    }
    if (!skipSeparator(true, !scanningInternalSubset())) {
      reportFatalError("MSG_SPACE_REQUIRED_AFTER_NOTATION_NAME_IN_NOTATIONDECL", new Object[] { str1 });
    }
    scanExternalID(fStrings, true);
    String str2 = fStrings[0];
    String str3 = fStrings[1];
    String str4 = fEntityScanner.getBaseSystemId();
    if ((str2 == null) && (str3 == null)) {
      reportFatalError("ExternalIDorPublicIDRequired", new Object[] { str1 });
    }
    skipSeparator(false, !scanningInternalSubset());
    if (!fEntityScanner.skipChar(62, null)) {
      reportFatalError("NotationDeclUnterminated", new Object[] { str1 });
    }
    fMarkUpDepth -= 1;
    fResourceIdentifier.setValues(str3, str2, str4, XMLEntityManager.expandSystemId(str2, str4));
    if (nonValidatingMode) {
      nvGrammarInfo.notationDecl(str1, fResourceIdentifier, null);
    }
    if (fDTDHandler != null) {
      fDTDHandler.notationDecl(str1, fResourceIdentifier, null);
    }
    fReportEntity = true;
  }
  
  private final void scanConditionalSect(int paramInt)
    throws IOException, XNIException
  {
    fReportEntity = false;
    skipSeparator(false, !scanningInternalSubset());
    if (fEntityScanner.skipString("INCLUDE"))
    {
      skipSeparator(false, !scanningInternalSubset());
      if ((paramInt != fPEDepth) && (fValidation)) {
        fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "INVALID_PE_IN_CONDITIONAL", new Object[] { fEntityManager.fCurrentEntity.name }, (short)1);
      }
      if (!fEntityScanner.skipChar(91, null)) {
        reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
      }
      if (fDTDHandler != null) {
        fDTDHandler.startConditional((short)0, null);
      }
      fIncludeSectDepth += 1;
      fReportEntity = true;
    }
    else
    {
      if (fEntityScanner.skipString("IGNORE"))
      {
        skipSeparator(false, !scanningInternalSubset());
        if ((paramInt != fPEDepth) && (fValidation)) {
          fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "INVALID_PE_IN_CONDITIONAL", new Object[] { fEntityManager.fCurrentEntity.name }, (short)1);
        }
        if (fDTDHandler != null) {
          fDTDHandler.startConditional((short)1, null);
        }
        if (!fEntityScanner.skipChar(91, null)) {
          reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
        }
        fReportEntity = true;
        int i = ++fIncludeSectDepth;
        if (fDTDHandler != null) {
          fIgnoreConditionalBuffer.clear();
        }
        for (;;)
        {
          if (fEntityScanner.skipChar(60, null))
          {
            if (fDTDHandler != null) {
              fIgnoreConditionalBuffer.append('<');
            }
            if (fEntityScanner.skipChar(33, null)) {
              if (fEntityScanner.skipChar(91, null))
              {
                if (fDTDHandler != null) {
                  fIgnoreConditionalBuffer.append("![");
                }
                fIncludeSectDepth += 1;
              }
              else if (fDTDHandler != null)
              {
                fIgnoreConditionalBuffer.append("!");
              }
            }
          }
          else if (fEntityScanner.skipChar(93, null))
          {
            if (fDTDHandler != null) {
              fIgnoreConditionalBuffer.append(']');
            }
            if (fEntityScanner.skipChar(93, null))
            {
              if (fDTDHandler != null) {
                fIgnoreConditionalBuffer.append(']');
              }
              while (fEntityScanner.skipChar(93, null)) {
                if (fDTDHandler != null) {
                  fIgnoreConditionalBuffer.append(']');
                }
              }
              if (fEntityScanner.skipChar(62, null))
              {
                if (fIncludeSectDepth-- == i)
                {
                  fMarkUpDepth -= 1;
                  if (fDTDHandler != null)
                  {
                    fLiteral.setValues(fIgnoreConditionalBuffer.ch, 0, fIgnoreConditionalBuffer.length - 2);
                    fDTDHandler.ignoredCharacters(fLiteral, null);
                    fDTDHandler.endConditional(null);
                  }
                  return;
                }
                if (fDTDHandler != null) {
                  fIgnoreConditionalBuffer.append('>');
                }
              }
            }
          }
          else
          {
            int j = fEntityScanner.scanChar(null);
            if (fScannerState == 0)
            {
              reportFatalError("IgnoreSectUnterminated", null);
              return;
            }
            if (fDTDHandler != null) {
              fIgnoreConditionalBuffer.append((char)j);
            }
          }
        }
      }
      reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
    }
  }
  
  protected final boolean scanDecls(boolean paramBoolean)
    throws IOException, XNIException
  {
    skipSeparator(false, true);
    boolean bool = true;
    while ((bool) && (fScannerState == 2))
    {
      bool = paramBoolean;
      if (fEntityScanner.skipChar(60, null))
      {
        fMarkUpDepth += 1;
        if (fEntityScanner.skipChar(63, null))
        {
          fStringBuffer.clear();
          scanPI(fStringBuffer);
          fMarkUpDepth -= 1;
        }
        else if (fEntityScanner.skipChar(33, null))
        {
          if (fEntityScanner.skipChar(45, null))
          {
            if (!fEntityScanner.skipChar(45, null)) {
              reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
            } else {
              scanComment();
            }
          }
          else if (fEntityScanner.skipString("ELEMENT"))
          {
            scanElementDecl();
          }
          else if (fEntityScanner.skipString("ATTLIST"))
          {
            scanAttlistDecl();
          }
          else if (fEntityScanner.skipString("ENTITY"))
          {
            scanEntityDecl();
          }
          else if (fEntityScanner.skipString("NOTATION"))
          {
            scanNotationDecl();
          }
          else if ((fEntityScanner.skipChar(91, null)) && (!scanningInternalSubset()))
          {
            scanConditionalSect(fPEDepth);
          }
          else
          {
            fMarkUpDepth -= 1;
            reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
          }
        }
        else
        {
          fMarkUpDepth -= 1;
          reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
        }
      }
      else if ((fIncludeSectDepth > 0) && (fEntityScanner.skipChar(93, null)))
      {
        if ((!fEntityScanner.skipChar(93, null)) || (!fEntityScanner.skipChar(62, null))) {
          reportFatalError("IncludeSectUnterminated", null);
        }
        if (fDTDHandler != null) {
          fDTDHandler.endConditional(null);
        }
        fIncludeSectDepth -= 1;
        fMarkUpDepth -= 1;
      }
      else
      {
        if ((scanningInternalSubset()) && (fEntityScanner.peekChar() == 93)) {
          return false;
        }
        if (!fEntityScanner.skipSpaces()) {
          reportFatalError("MSG_MARKUP_NOT_RECOGNIZED_IN_DTD", null);
        }
      }
      skipSeparator(false, true);
    }
    return fScannerState != 0;
  }
  
  private boolean skipSeparator(boolean paramBoolean1, boolean paramBoolean2)
    throws IOException, XNIException
  {
    int i = fPEDepth;
    boolean bool = fEntityScanner.skipSpaces();
    if ((!paramBoolean2) || (!fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE))) {
      return (!paramBoolean1) || (bool) || (i != fPEDepth);
    }
    for (;;)
    {
      String str = fEntityScanner.scanName(XMLScanner.NameType.ENTITY);
      if (str == null) {
        reportFatalError("NameRequiredInPEReference", null);
      } else if (!fEntityScanner.skipChar(59, XMLScanner.NameType.REFERENCE)) {
        reportFatalError("SemicolonRequiredInPEReference", new Object[] { str });
      }
      startPE(str, false);
      fEntityScanner.skipSpaces();
      if (!fEntityScanner.skipChar(37, XMLScanner.NameType.REFERENCE)) {
        return true;
      }
    }
  }
  
  private final void pushContentStack(int paramInt)
  {
    if (fContentStack.length == fContentDepth)
    {
      int[] arrayOfInt = new int[fContentDepth * 2];
      System.arraycopy(fContentStack, 0, arrayOfInt, 0, fContentDepth);
      fContentStack = arrayOfInt;
    }
    fContentStack[(fContentDepth++)] = paramInt;
  }
  
  private final int popContentStack()
  {
    return fContentStack[(--fContentDepth)];
  }
  
  private final void pushPEStack(int paramInt, boolean paramBoolean)
  {
    if (fPEStack.length == fPEDepth)
    {
      int[] arrayOfInt = new int[fPEDepth * 2];
      System.arraycopy(fPEStack, 0, arrayOfInt, 0, fPEDepth);
      fPEStack = arrayOfInt;
      boolean[] arrayOfBoolean = new boolean[fPEDepth * 2];
      System.arraycopy(fPEReport, 0, arrayOfBoolean, 0, fPEDepth);
      fPEReport = arrayOfBoolean;
    }
    fPEReport[fPEDepth] = paramBoolean;
    fPEStack[(fPEDepth++)] = paramInt;
  }
  
  private final int popPEStack()
  {
    return fPEStack[(--fPEDepth)];
  }
  
  private final boolean peekReportEntity()
  {
    return fPEReport[(fPEDepth - 1)];
  }
  
  private final void ensureEnumerationSize(int paramInt)
  {
    if (fEnumeration.length == paramInt)
    {
      String[] arrayOfString = new String[paramInt * 2];
      System.arraycopy(fEnumeration, 0, arrayOfString, 0, paramInt);
      fEnumeration = arrayOfString;
    }
  }
  
  private void init()
  {
    fStartDTDCalled = false;
    fExtEntityDepth = 0;
    fIncludeSectDepth = 0;
    fMarkUpDepth = 0;
    fPEDepth = 0;
    fStandalone = false;
    fSeenExternalDTD = false;
    fSeenExternalPE = false;
    setScannerState(1);
    fLimitAnalyzer = fEntityManager.fLimitAnalyzer;
    fSecurityManager = fEntityManager.fSecurityManager;
  }
  
  public DTDGrammar getGrammar()
  {
    return nvGrammarInfo;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLDTDScannerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */