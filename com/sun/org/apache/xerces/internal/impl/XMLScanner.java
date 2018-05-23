package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.Limit;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.xml.internal.stream.Entity.ScannedEntity;
import com.sun.xml.internal.stream.XMLEntityStorage;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.stream.events.XMLEvent;

public abstract class XMLScanner
  implements XMLComponent
{
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  protected static final boolean DEBUG_ATTR_NORMALIZATION = false;
  private boolean fNeedNonNormalizedValue = false;
  protected ArrayList<XMLString> attributeValueCache = new ArrayList();
  protected ArrayList<XMLStringBuffer> stringBufferCache = new ArrayList();
  protected int fStringBufferIndex = 0;
  protected boolean fAttributeCacheInitDone = false;
  protected int fAttributeCacheUsedCount = 0;
  protected boolean fValidation = false;
  protected boolean fNamespaces;
  protected boolean fNotifyCharRefs = false;
  protected boolean fParserSettings = true;
  protected PropertyManager fPropertyManager = null;
  protected SymbolTable fSymbolTable;
  protected XMLErrorReporter fErrorReporter;
  protected XMLEntityManager fEntityManager = null;
  protected XMLEntityStorage fEntityStore = null;
  protected XMLSecurityManager fSecurityManager = null;
  protected XMLLimitAnalyzer fLimitAnalyzer = null;
  protected XMLEvent fEvent;
  protected XMLEntityScanner fEntityScanner = null;
  protected int fEntityDepth;
  protected String fCharRefLiteral = null;
  protected boolean fScanningAttribute;
  protected boolean fReportEntity;
  protected static final String fVersionSymbol = "version".intern();
  protected static final String fEncodingSymbol = "encoding".intern();
  protected static final String fStandaloneSymbol = "standalone".intern();
  protected static final String fAmpSymbol = "amp".intern();
  protected static final String fLtSymbol = "lt".intern();
  protected static final String fGtSymbol = "gt".intern();
  protected static final String fQuotSymbol = "quot".intern();
  protected static final String fAposSymbol = "apos".intern();
  private XMLString fString = new XMLString();
  private XMLStringBuffer fStringBuffer = new XMLStringBuffer();
  private XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
  private XMLStringBuffer fStringBuffer3 = new XMLStringBuffer();
  protected XMLResourceIdentifierImpl fResourceIdentifier = new XMLResourceIdentifierImpl();
  int initialCacheCount = 6;
  
  public XMLScanner() {}
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException
  {
    fParserSettings = paramXMLComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
    if (!fParserSettings)
    {
      init();
      return;
    }
    fSymbolTable = ((SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    fErrorReporter = ((XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    fEntityManager = ((XMLEntityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager"));
    fSecurityManager = ((XMLSecurityManager)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/security-manager"));
    fEntityStore = fEntityManager.getEntityStore();
    fValidation = paramXMLComponentManager.getFeature("http://xml.org/sax/features/validation", false);
    fNamespaces = paramXMLComponentManager.getFeature("http://xml.org/sax/features/namespaces", true);
    fNotifyCharRefs = paramXMLComponentManager.getFeature("http://apache.org/xml/features/scanner/notify-char-refs", false);
    init();
  }
  
  protected void setPropertyManager(PropertyManager paramPropertyManager)
  {
    fPropertyManager = paramPropertyManager;
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException
  {
    if (paramString.startsWith("http://apache.org/xml/properties/"))
    {
      String str = paramString.substring("http://apache.org/xml/properties/".length());
      if (str.equals("internal/symbol-table")) {
        fSymbolTable = ((SymbolTable)paramObject);
      } else if (str.equals("internal/error-reporter")) {
        fErrorReporter = ((XMLErrorReporter)paramObject);
      } else if (str.equals("internal/entity-manager")) {
        fEntityManager = ((XMLEntityManager)paramObject);
      }
    }
    if (paramString.equals("http://apache.org/xml/properties/security-manager")) {
      fSecurityManager = ((XMLSecurityManager)paramObject);
    }
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {
    if ("http://xml.org/sax/features/validation".equals(paramString)) {
      fValidation = paramBoolean;
    } else if ("http://apache.org/xml/features/scanner/notify-char-refs".equals(paramString)) {
      fNotifyCharRefs = paramBoolean;
    }
  }
  
  public boolean getFeature(String paramString)
    throws XMLConfigurationException
  {
    if ("http://xml.org/sax/features/validation".equals(paramString)) {
      return fValidation;
    }
    if ("http://apache.org/xml/features/scanner/notify-char-refs".equals(paramString)) {
      return fNotifyCharRefs;
    }
    throw new XMLConfigurationException(Status.NOT_RECOGNIZED, paramString);
  }
  
  protected void reset()
  {
    init();
    fValidation = true;
    fNotifyCharRefs = false;
  }
  
  public void reset(PropertyManager paramPropertyManager)
  {
    init();
    fSymbolTable = ((SymbolTable)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    fErrorReporter = ((XMLErrorReporter)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    fEntityManager = ((XMLEntityManager)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/entity-manager"));
    fEntityStore = fEntityManager.getEntityStore();
    fEntityScanner = fEntityManager.getEntityScanner();
    fSecurityManager = ((XMLSecurityManager)paramPropertyManager.getProperty("http://apache.org/xml/properties/security-manager"));
    fValidation = false;
    fNotifyCharRefs = false;
  }
  
  protected void scanXMLDeclOrTextDecl(boolean paramBoolean, String[] paramArrayOfString)
    throws IOException, XNIException
  {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    int i = 0;
    int j = 1;
    int k = 2;
    int m = 3;
    int n = 0;
    int i1 = 0;
    boolean bool1 = fEntityScanner.skipSpaces();
    Entity.ScannedEntity localScannedEntity1 = fEntityManager.getCurrentEntity();
    boolean bool2 = literal;
    literal = false;
    while (fEntityScanner.peekChar() != 63)
    {
      i1 = 1;
      String str4 = scanPseudoAttribute(paramBoolean, fString);
      switch (n)
      {
      case 0: 
        if (str4.equals(fVersionSymbol))
        {
          if (!bool1) {
            reportFatalError(paramBoolean ? "SpaceRequiredBeforeVersionInTextDecl" : "SpaceRequiredBeforeVersionInXMLDecl", null);
          }
          str1 = fString.toString();
          n = 1;
          if (!versionSupported(str1)) {
            reportFatalError("VersionNotSupported", new Object[] { str1 });
          }
          if (str1.equals("1.1"))
          {
            Entity.ScannedEntity localScannedEntity2 = fEntityManager.getTopLevelEntity();
            if ((localScannedEntity2 != null) && ((version == null) || (version.equals("1.0")))) {
              reportFatalError("VersionMismatch", null);
            }
            fEntityManager.setScannerVersion((short)2);
          }
        }
        else if (str4.equals(fEncodingSymbol))
        {
          if (!paramBoolean) {
            reportFatalError("VersionInfoRequired", null);
          }
          if (!bool1) {
            reportFatalError(paramBoolean ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", null);
          }
          str2 = fString.toString();
          n = paramBoolean ? 3 : 2;
        }
        else if (paramBoolean)
        {
          reportFatalError("EncodingDeclRequired", null);
        }
        else
        {
          reportFatalError("VersionInfoRequired", null);
        }
        break;
      case 1: 
        if (str4.equals(fEncodingSymbol))
        {
          if (!bool1) {
            reportFatalError(paramBoolean ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", null);
          }
          str2 = fString.toString();
          n = paramBoolean ? 3 : 2;
        }
        else if ((!paramBoolean) && (str4.equals(fStandaloneSymbol)))
        {
          if (!bool1) {
            reportFatalError("SpaceRequiredBeforeStandalone", null);
          }
          str3 = fString.toString();
          n = 3;
          if ((!str3.equals("yes")) && (!str3.equals("no"))) {
            reportFatalError("SDDeclInvalid", new Object[] { str3 });
          }
        }
        else
        {
          reportFatalError("EncodingDeclRequired", null);
        }
        break;
      case 2: 
        if (str4.equals(fStandaloneSymbol))
        {
          if (!bool1) {
            reportFatalError("SpaceRequiredBeforeStandalone", null);
          }
          str3 = fString.toString();
          n = 3;
          if ((!str3.equals("yes")) && (!str3.equals("no"))) {
            reportFatalError("SDDeclInvalid", new Object[] { str3 });
          }
        }
        else
        {
          reportFatalError("SDDeclNameInvalid", null);
        }
        break;
      default: 
        reportFatalError("NoMorePseudoAttributes", null);
      }
      bool1 = fEntityScanner.skipSpaces();
    }
    if (bool2) {
      literal = true;
    }
    if ((paramBoolean) && (n != 3)) {
      reportFatalError("MorePseudoAttributes", null);
    }
    if (paramBoolean)
    {
      if ((i1 == 0) && (str2 == null)) {
        reportFatalError("EncodingDeclRequired", null);
      }
    }
    else if ((i1 == 0) && (str1 == null)) {
      reportFatalError("VersionInfoRequired", null);
    }
    if (!fEntityScanner.skipChar(63, null)) {
      reportFatalError("XMLDeclUnterminated", null);
    }
    if (!fEntityScanner.skipChar(62, null)) {
      reportFatalError("XMLDeclUnterminated", null);
    }
    paramArrayOfString[0] = str1;
    paramArrayOfString[1] = str2;
    paramArrayOfString[2] = str3;
  }
  
  protected String scanPseudoAttribute(boolean paramBoolean, XMLString paramXMLString)
    throws IOException, XNIException
  {
    String str1 = scanPseudoAttributeName();
    if (str1 == null) {
      reportFatalError("PseudoAttrNameExpected", null);
    }
    fEntityScanner.skipSpaces();
    if (!fEntityScanner.skipChar(61, null)) {
      reportFatalError(paramBoolean ? "EqRequiredInTextDecl" : "EqRequiredInXMLDecl", new Object[] { str1 });
    }
    fEntityScanner.skipSpaces();
    int i = fEntityScanner.peekChar();
    if ((i != 39) && (i != 34)) {
      reportFatalError(paramBoolean ? "QuoteRequiredInTextDecl" : "QuoteRequiredInXMLDecl", new Object[] { str1 });
    }
    fEntityScanner.scanChar(NameType.ATTRIBUTE);
    int j = fEntityScanner.scanLiteral(i, paramXMLString, false);
    if (j != i)
    {
      fStringBuffer2.clear();
      do
      {
        fStringBuffer2.append(paramXMLString);
        if (j != -1) {
          if ((j == 38) || (j == 37) || (j == 60) || (j == 93))
          {
            fStringBuffer2.append((char)fEntityScanner.scanChar(NameType.ATTRIBUTE));
          }
          else if (XMLChar.isHighSurrogate(j))
          {
            scanSurrogates(fStringBuffer2);
          }
          else if (isInvalidLiteral(j))
          {
            String str2 = paramBoolean ? "InvalidCharInTextDecl" : "InvalidCharInXMLDecl";
            reportFatalError(str2, new Object[] { Integer.toString(j, 16) });
            fEntityScanner.scanChar(null);
          }
        }
        j = fEntityScanner.scanLiteral(i, paramXMLString, false);
      } while (j != i);
      fStringBuffer2.append(paramXMLString);
      paramXMLString.setValues(fStringBuffer2);
    }
    if (!fEntityScanner.skipChar(i, null)) {
      reportFatalError(paramBoolean ? "CloseQuoteMissingInTextDecl" : "CloseQuoteMissingInXMLDecl", new Object[] { str1 });
    }
    return str1;
  }
  
  private String scanPseudoAttributeName()
    throws IOException, XNIException
  {
    int i = fEntityScanner.peekChar();
    switch (i)
    {
    case 118: 
      if (fEntityScanner.skipString(fVersionSymbol)) {
        return fVersionSymbol;
      }
      break;
    case 101: 
      if (fEntityScanner.skipString(fEncodingSymbol)) {
        return fEncodingSymbol;
      }
      break;
    case 115: 
      if (fEntityScanner.skipString(fStandaloneSymbol)) {
        return fStandaloneSymbol;
      }
      break;
    }
    return null;
  }
  
  protected void scanPI(XMLStringBuffer paramXMLStringBuffer)
    throws IOException, XNIException
  {
    fReportEntity = false;
    String str = fEntityScanner.scanName(NameType.PI);
    if (str == null) {
      reportFatalError("PITargetRequired", null);
    }
    scanPIData(str, paramXMLStringBuffer);
    fReportEntity = true;
  }
  
  protected void scanPIData(String paramString, XMLStringBuffer paramXMLStringBuffer)
    throws IOException, XNIException
  {
    int i;
    if (paramString.length() == 3)
    {
      i = Character.toLowerCase(paramString.charAt(0));
      int j = Character.toLowerCase(paramString.charAt(1));
      int k = Character.toLowerCase(paramString.charAt(2));
      if ((i == 120) && (j == 109) && (k == 108)) {
        reportFatalError("ReservedPITarget", null);
      }
    }
    if (!fEntityScanner.skipSpaces())
    {
      if (fEntityScanner.skipString("?>")) {
        return;
      }
      reportFatalError("SpaceRequiredInPI", null);
    }
    if (fEntityScanner.scanData("?>", paramXMLStringBuffer)) {
      do
      {
        i = fEntityScanner.peekChar();
        if (i != -1) {
          if (XMLChar.isHighSurrogate(i))
          {
            scanSurrogates(paramXMLStringBuffer);
          }
          else if (isInvalidLiteral(i))
          {
            reportFatalError("InvalidCharInPI", new Object[] { Integer.toHexString(i) });
            fEntityScanner.scanChar(null);
          }
        }
      } while (fEntityScanner.scanData("?>", paramXMLStringBuffer));
    }
  }
  
  protected void scanComment(XMLStringBuffer paramXMLStringBuffer)
    throws IOException, XNIException
  {
    paramXMLStringBuffer.clear();
    while (fEntityScanner.scanData("--", paramXMLStringBuffer))
    {
      int i = fEntityScanner.peekChar();
      if (i != -1) {
        if (XMLChar.isHighSurrogate(i))
        {
          scanSurrogates(paramXMLStringBuffer);
        }
        else if (isInvalidLiteral(i))
        {
          reportFatalError("InvalidCharInComment", new Object[] { Integer.toHexString(i) });
          fEntityScanner.scanChar(NameType.COMMENT);
        }
      }
    }
    if (!fEntityScanner.skipChar(62, NameType.COMMENT)) {
      reportFatalError("DashDashInComment", null);
    }
  }
  
  protected void scanAttributeValue(XMLString paramXMLString1, XMLString paramXMLString2, String paramString1, XMLAttributes paramXMLAttributes, int paramInt, boolean paramBoolean1, String paramString2, boolean paramBoolean2)
    throws IOException, XNIException
  {
    XMLStringBuffer localXMLStringBuffer = null;
    int i = fEntityScanner.peekChar();
    if ((i != 39) && (i != 34)) {
      reportFatalError("OpenQuoteExpected", new Object[] { paramString2, paramString1 });
    }
    fEntityScanner.scanChar(NameType.ATTRIBUTE);
    int j = fEntityDepth;
    int k = fEntityScanner.scanLiteral(i, paramXMLString1, paramBoolean2);
    if (fNeedNonNormalizedValue)
    {
      fStringBuffer2.clear();
      fStringBuffer2.append(paramXMLString1);
    }
    if (fEntityScanner.whiteSpaceLen > 0) {
      normalizeWhitespace(paramXMLString1);
    }
    if (k != i)
    {
      fScanningAttribute = true;
      localXMLStringBuffer = getStringBuffer();
      localXMLStringBuffer.clear();
      do
      {
        localXMLStringBuffer.append(paramXMLString1);
        if (k == 38)
        {
          fEntityScanner.skipChar(38, NameType.REFERENCE);
          if ((j == fEntityDepth) && (fNeedNonNormalizedValue)) {
            fStringBuffer2.append('&');
          }
          if (fEntityScanner.skipChar(35, NameType.REFERENCE))
          {
            if ((j == fEntityDepth) && (fNeedNonNormalizedValue)) {
              fStringBuffer2.append('#');
            }
            int m;
            if (fNeedNonNormalizedValue) {
              m = scanCharReferenceValue(localXMLStringBuffer, fStringBuffer2);
            } else {
              m = scanCharReferenceValue(localXMLStringBuffer, null);
            }
            if (m == -1) {}
          }
          else
          {
            String str = fEntityScanner.scanName(NameType.ENTITY);
            if (str == null) {
              reportFatalError("NameRequiredInReference", null);
            } else if ((j == fEntityDepth) && (fNeedNonNormalizedValue)) {
              fStringBuffer2.append(str);
            }
            if (!fEntityScanner.skipChar(59, NameType.REFERENCE)) {
              reportFatalError("SemicolonRequiredInReference", new Object[] { str });
            } else if ((j == fEntityDepth) && (fNeedNonNormalizedValue)) {
              fStringBuffer2.append(';');
            }
            if (resolveCharacter(str, localXMLStringBuffer))
            {
              checkEntityLimit(false, fEntityScanner.fCurrentEntity.name, 1);
            }
            else if (fEntityStore.isExternalEntity(str))
            {
              reportFatalError("ReferenceToExternalEntity", new Object[] { str });
            }
            else
            {
              if (!fEntityStore.isDeclaredEntity(str)) {
                if (paramBoolean1)
                {
                  if (fValidation) {
                    fErrorReporter.reportError(fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { str }, (short)1);
                  }
                }
                else {
                  reportFatalError("EntityNotDeclared", new Object[] { str });
                }
              }
              fEntityManager.startEntity(true, str, true);
            }
          }
        }
        else if (k == 60)
        {
          reportFatalError("LessthanInAttValue", new Object[] { paramString2, paramString1 });
          fEntityScanner.scanChar(null);
          if ((j == fEntityDepth) && (fNeedNonNormalizedValue)) {
            fStringBuffer2.append((char)k);
          }
        }
        else if ((k == 37) || (k == 93))
        {
          fEntityScanner.scanChar(null);
          localXMLStringBuffer.append((char)k);
          if ((j == fEntityDepth) && (fNeedNonNormalizedValue)) {
            fStringBuffer2.append((char)k);
          }
        }
        else if ((k == 10) || (k == 13))
        {
          fEntityScanner.scanChar(null);
          localXMLStringBuffer.append(' ');
          if ((j == fEntityDepth) && (fNeedNonNormalizedValue)) {
            fStringBuffer2.append('\n');
          }
        }
        else if ((k != -1) && (XMLChar.isHighSurrogate(k)))
        {
          fStringBuffer3.clear();
          if (scanSurrogates(fStringBuffer3))
          {
            localXMLStringBuffer.append(fStringBuffer3);
            if ((j == fEntityDepth) && (fNeedNonNormalizedValue)) {
              fStringBuffer2.append(fStringBuffer3);
            }
          }
        }
        else if ((k != -1) && (isInvalidLiteral(k)))
        {
          reportFatalError("InvalidCharInAttValue", new Object[] { paramString2, paramString1, Integer.toString(k, 16) });
          fEntityScanner.scanChar(null);
          if ((j == fEntityDepth) && (fNeedNonNormalizedValue)) {
            fStringBuffer2.append((char)k);
          }
        }
        k = fEntityScanner.scanLiteral(i, paramXMLString1, paramBoolean2);
        if ((j == fEntityDepth) && (fNeedNonNormalizedValue)) {
          fStringBuffer2.append(paramXMLString1);
        }
        if (fEntityScanner.whiteSpaceLen > 0) {
          normalizeWhitespace(paramXMLString1);
        }
      } while ((k != i) || (j != fEntityDepth));
      localXMLStringBuffer.append(paramXMLString1);
      paramXMLString1.setValues(localXMLStringBuffer);
      fScanningAttribute = false;
    }
    if (fNeedNonNormalizedValue) {
      paramXMLString2.setValues(fStringBuffer2);
    }
    int n = fEntityScanner.scanChar(NameType.ATTRIBUTE);
    if (n != i) {
      reportFatalError("CloseQuoteExpected", new Object[] { paramString2, paramString1 });
    }
  }
  
  protected boolean resolveCharacter(String paramString, XMLStringBuffer paramXMLStringBuffer)
  {
    if (paramString == fAmpSymbol)
    {
      paramXMLStringBuffer.append('&');
      return true;
    }
    if (paramString == fAposSymbol)
    {
      paramXMLStringBuffer.append('\'');
      return true;
    }
    if (paramString == fLtSymbol)
    {
      paramXMLStringBuffer.append('<');
      return true;
    }
    if (paramString == fGtSymbol)
    {
      checkEntityLimit(false, fEntityScanner.fCurrentEntity.name, 1);
      paramXMLStringBuffer.append('>');
      return true;
    }
    if (paramString == fQuotSymbol)
    {
      checkEntityLimit(false, fEntityScanner.fCurrentEntity.name, 1);
      paramXMLStringBuffer.append('"');
      return true;
    }
    return false;
  }
  
  protected void scanExternalID(String[] paramArrayOfString, boolean paramBoolean)
    throws IOException, XNIException
  {
    String str1 = null;
    String str2 = null;
    if (fEntityScanner.skipString("PUBLIC"))
    {
      if (!fEntityScanner.skipSpaces()) {
        reportFatalError("SpaceRequiredAfterPUBLIC", null);
      }
      scanPubidLiteral(fString);
      str2 = fString.toString();
      if ((!fEntityScanner.skipSpaces()) && (!paramBoolean)) {
        reportFatalError("SpaceRequiredBetweenPublicAndSystem", null);
      }
    }
    if ((str2 != null) || (fEntityScanner.skipString("SYSTEM")))
    {
      if ((str2 == null) && (!fEntityScanner.skipSpaces())) {
        reportFatalError("SpaceRequiredAfterSYSTEM", null);
      }
      int i = fEntityScanner.peekChar();
      if ((i != 39) && (i != 34))
      {
        if ((str2 != null) && (paramBoolean))
        {
          paramArrayOfString[0] = null;
          paramArrayOfString[1] = str2;
          return;
        }
        reportFatalError("QuoteRequiredInSystemID", null);
      }
      fEntityScanner.scanChar(null);
      Object localObject = fString;
      if (fEntityScanner.scanLiteral(i, (XMLString)localObject, false) != i)
      {
        fStringBuffer.clear();
        do
        {
          fStringBuffer.append((XMLString)localObject);
          int j = fEntityScanner.peekChar();
          if ((XMLChar.isMarkup(j)) || (j == 93)) {
            fStringBuffer.append((char)fEntityScanner.scanChar(null));
          } else if ((j != -1) && (isInvalidLiteral(j))) {
            reportFatalError("InvalidCharInSystemID", new Object[] { Integer.toString(j, 16) });
          }
        } while (fEntityScanner.scanLiteral(i, (XMLString)localObject, false) != i);
        fStringBuffer.append((XMLString)localObject);
        localObject = fStringBuffer;
      }
      str1 = ((XMLString)localObject).toString();
      if (!fEntityScanner.skipChar(i, null)) {
        reportFatalError("SystemIDUnterminated", null);
      }
    }
    paramArrayOfString[0] = str1;
    paramArrayOfString[1] = str2;
  }
  
  protected boolean scanPubidLiteral(XMLString paramXMLString)
    throws IOException, XNIException
  {
    int i = fEntityScanner.scanChar(null);
    if ((i != 39) && (i != 34))
    {
      reportFatalError("QuoteRequiredInPublicID", null);
      return false;
    }
    fStringBuffer.clear();
    int j = 1;
    boolean bool = true;
    for (;;)
    {
      int k = fEntityScanner.scanChar(null);
      if ((k == 32) || (k == 10) || (k == 13))
      {
        if (j == 0)
        {
          fStringBuffer.append(' ');
          j = 1;
        }
      }
      else
      {
        if (k == i)
        {
          if (j != 0) {
            fStringBuffer.length -= 1;
          }
          paramXMLString.setValues(fStringBuffer);
          break;
        }
        if (XMLChar.isPubid(k))
        {
          fStringBuffer.append((char)k);
          j = 0;
        }
        else
        {
          if (k == -1)
          {
            reportFatalError("PublicIDUnterminated", null);
            return false;
          }
          bool = false;
          reportFatalError("InvalidCharInPublicID", new Object[] { Integer.toHexString(k) });
        }
      }
    }
    return bool;
  }
  
  protected void normalizeWhitespace(XMLString paramXMLString)
  {
    int i = 0;
    int j = 0;
    int[] arrayOfInt = fEntityScanner.whiteSpaceLookup;
    int k = fEntityScanner.whiteSpaceLen;
    int m = offset + length;
    while (i < k)
    {
      j = arrayOfInt[i];
      if (j < m) {
        ch[j] = ' ';
      }
      i++;
    }
  }
  
  public void startEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    fEntityDepth += 1;
    fEntityScanner = fEntityManager.getEntityScanner();
    fEntityStore = fEntityManager.getEntityStore();
  }
  
  public void endEntity(String paramString, Augmentations paramAugmentations)
    throws IOException, XNIException
  {
    fEntityDepth -= 1;
  }
  
  protected int scanCharReferenceValue(XMLStringBuffer paramXMLStringBuffer1, XMLStringBuffer paramXMLStringBuffer2)
    throws IOException, XNIException
  {
    int i = length;
    int j = 0;
    int m;
    if (fEntityScanner.skipChar(120, NameType.REFERENCE))
    {
      if (paramXMLStringBuffer2 != null) {
        paramXMLStringBuffer2.append('x');
      }
      j = 1;
      fStringBuffer3.clear();
      k = 1;
      m = fEntityScanner.peekChar();
      k = ((m >= 48) && (m <= 57)) || ((m >= 97) && (m <= 102)) || ((m >= 65) && (m <= 70)) ? 1 : 0;
      if (k != 0)
      {
        if (paramXMLStringBuffer2 != null) {
          paramXMLStringBuffer2.append((char)m);
        }
        fEntityScanner.scanChar(NameType.REFERENCE);
        fStringBuffer3.append((char)m);
        do
        {
          m = fEntityScanner.peekChar();
          k = ((m >= 48) && (m <= 57)) || ((m >= 97) && (m <= 102)) || ((m >= 65) && (m <= 70)) ? 1 : 0;
          if (k != 0)
          {
            if (paramXMLStringBuffer2 != null) {
              paramXMLStringBuffer2.append((char)m);
            }
            fEntityScanner.scanChar(NameType.REFERENCE);
            fStringBuffer3.append((char)m);
          }
        } while (k != 0);
      }
      else
      {
        reportFatalError("HexdigitRequiredInCharRef", null);
      }
    }
    else
    {
      fStringBuffer3.clear();
      k = 1;
      m = fEntityScanner.peekChar();
      k = (m >= 48) && (m <= 57) ? 1 : 0;
      if (k != 0)
      {
        if (paramXMLStringBuffer2 != null) {
          paramXMLStringBuffer2.append((char)m);
        }
        fEntityScanner.scanChar(NameType.REFERENCE);
        fStringBuffer3.append((char)m);
        do
        {
          m = fEntityScanner.peekChar();
          k = (m >= 48) && (m <= 57) ? 1 : 0;
          if (k != 0)
          {
            if (paramXMLStringBuffer2 != null) {
              paramXMLStringBuffer2.append((char)m);
            }
            fEntityScanner.scanChar(NameType.REFERENCE);
            fStringBuffer3.append((char)m);
          }
        } while (k != 0);
      }
      else
      {
        reportFatalError("DigitRequiredInCharRef", null);
      }
    }
    if (!fEntityScanner.skipChar(59, NameType.REFERENCE)) {
      reportFatalError("SemicolonRequiredInCharRef", null);
    }
    if (paramXMLStringBuffer2 != null) {
      paramXMLStringBuffer2.append(';');
    }
    int k = -1;
    try
    {
      k = Integer.parseInt(fStringBuffer3.toString(), j != 0 ? 16 : 10);
      if (isInvalid(k))
      {
        StringBuffer localStringBuffer1 = new StringBuffer(fStringBuffer3.length + 1);
        if (j != 0) {
          localStringBuffer1.append('x');
        }
        localStringBuffer1.append(fStringBuffer3.ch, fStringBuffer3.offset, fStringBuffer3.length);
        reportFatalError("InvalidCharRef", new Object[] { localStringBuffer1.toString() });
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      StringBuffer localStringBuffer2 = new StringBuffer(fStringBuffer3.length + 1);
      if (j != 0) {
        localStringBuffer2.append('x');
      }
      localStringBuffer2.append(fStringBuffer3.ch, fStringBuffer3.offset, fStringBuffer3.length);
      reportFatalError("InvalidCharRef", new Object[] { localStringBuffer2.toString() });
    }
    if (!XMLChar.isSupplemental(k))
    {
      paramXMLStringBuffer1.append((char)k);
    }
    else
    {
      paramXMLStringBuffer1.append(XMLChar.highSurrogate(k));
      paramXMLStringBuffer1.append(XMLChar.lowSurrogate(k));
    }
    if ((fNotifyCharRefs) && (k != -1))
    {
      String str = "#" + (j != 0 ? "x" : "") + fStringBuffer3.toString();
      if (!fScanningAttribute) {
        fCharRefLiteral = str;
      }
    }
    if (fEntityScanner.fCurrentEntity.isGE) {
      checkEntityLimit(false, fEntityScanner.fCurrentEntity.name, length - i);
    }
    return k;
  }
  
  protected boolean isInvalid(int paramInt)
  {
    return XMLChar.isInvalid(paramInt);
  }
  
  protected boolean isInvalidLiteral(int paramInt)
  {
    return XMLChar.isInvalid(paramInt);
  }
  
  protected boolean isValidNameChar(int paramInt)
  {
    return XMLChar.isName(paramInt);
  }
  
  protected boolean isValidNCName(int paramInt)
  {
    return XMLChar.isNCName(paramInt);
  }
  
  protected boolean isValidNameStartChar(int paramInt)
  {
    return XMLChar.isNameStart(paramInt);
  }
  
  protected boolean isValidNameStartHighSurrogate(int paramInt)
  {
    return false;
  }
  
  protected boolean versionSupported(String paramString)
  {
    return (paramString.equals("1.0")) || (paramString.equals("1.1"));
  }
  
  protected boolean scanSurrogates(XMLStringBuffer paramXMLStringBuffer)
    throws IOException, XNIException
  {
    int i = fEntityScanner.scanChar(null);
    int j = fEntityScanner.peekChar();
    if (!XMLChar.isLowSurrogate(j))
    {
      reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(i, 16) });
      return false;
    }
    fEntityScanner.scanChar(null);
    int k = XMLChar.supplemental((char)i, (char)j);
    if (isInvalid(k))
    {
      reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(k, 16) });
      return false;
    }
    paramXMLStringBuffer.append((char)i);
    paramXMLStringBuffer.append((char)j);
    return true;
  }
  
  protected void reportFatalError(String paramString, Object[] paramArrayOfObject)
    throws XNIException
  {
    fErrorReporter.reportError(fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", paramString, paramArrayOfObject, (short)2);
  }
  
  private void init()
  {
    fEntityScanner = null;
    fEntityDepth = 0;
    fReportEntity = true;
    fResourceIdentifier.clear();
    if (!fAttributeCacheInitDone)
    {
      for (int i = 0; i < initialCacheCount; i++)
      {
        attributeValueCache.add(new XMLString());
        stringBufferCache.add(new XMLStringBuffer());
      }
      fAttributeCacheInitDone = true;
    }
    fStringBufferIndex = 0;
    fAttributeCacheUsedCount = 0;
  }
  
  XMLStringBuffer getStringBuffer()
  {
    if ((fStringBufferIndex < initialCacheCount) || (fStringBufferIndex < stringBufferCache.size())) {
      return (XMLStringBuffer)stringBufferCache.get(fStringBufferIndex++);
    }
    XMLStringBuffer localXMLStringBuffer = new XMLStringBuffer();
    fStringBufferIndex += 1;
    stringBufferCache.add(localXMLStringBuffer);
    return localXMLStringBuffer;
  }
  
  void checkEntityLimit(boolean paramBoolean, String paramString, XMLString paramXMLString)
  {
    checkEntityLimit(paramBoolean, paramString, length);
  }
  
  void checkEntityLimit(boolean paramBoolean, String paramString, int paramInt)
  {
    if (fLimitAnalyzer == null) {
      fLimitAnalyzer = fEntityManager.fLimitAnalyzer;
    }
    if (paramBoolean)
    {
      fLimitAnalyzer.addValue(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT, "%" + paramString, paramInt);
      if (fSecurityManager.isOverLimit(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT, fLimitAnalyzer))
      {
        fSecurityManager.debugPrint(fLimitAnalyzer);
        reportFatalError("MaxEntitySizeLimit", new Object[] { "%" + paramString, Integer.valueOf(fLimitAnalyzer.getValue(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT)), Integer.valueOf(fSecurityManager.getLimit(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT)), fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT) });
      }
    }
    else
    {
      fLimitAnalyzer.addValue(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, paramString, paramInt);
      if (fSecurityManager.isOverLimit(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, fLimitAnalyzer))
      {
        fSecurityManager.debugPrint(fLimitAnalyzer);
        reportFatalError("MaxEntitySizeLimit", new Object[] { paramString, Integer.valueOf(fLimitAnalyzer.getValue(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT)), Integer.valueOf(fSecurityManager.getLimit(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT)), fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT) });
      }
    }
    if (fSecurityManager.isOverLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT, fLimitAnalyzer))
    {
      fSecurityManager.debugPrint(fLimitAnalyzer);
      reportFatalError("TotalEntitySizeLimit", new Object[] { Integer.valueOf(fLimitAnalyzer.getTotalValue(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT)), Integer.valueOf(fSecurityManager.getLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT)), fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT) });
    }
  }
  
  public static enum NameType
  {
    ATTRIBUTE("attribute"),  ATTRIBUTENAME("attribute name"),  COMMENT("comment"),  DOCTYPE("doctype"),  ELEMENTSTART("startelement"),  ELEMENTEND("endelement"),  ENTITY("entity"),  NOTATION("notation"),  PI("pi"),  REFERENCE("reference");
    
    final String literal;
    
    private NameType(String paramString)
    {
      literal = paramString;
    }
    
    String literal()
    {
      return literal;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLScanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */