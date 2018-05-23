package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidatorFilter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLAttributesIteratorImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.Limit;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.xml.internal.stream.dtd.DTDGrammarUtil;
import java.io.IOException;

public class XML11NSDocumentScannerImpl
  extends XML11DocumentScannerImpl
{
  protected boolean fBindNamespaces;
  protected boolean fPerformValidation;
  private XMLDTDValidatorFilter fDTDValidator;
  private boolean fSawSpace;
  
  public XML11NSDocumentScannerImpl() {}
  
  public void setDTDValidator(XMLDTDValidatorFilter paramXMLDTDValidatorFilter)
  {
    fDTDValidator = paramXMLDTDValidatorFilter;
  }
  
  protected boolean scanStartElement()
    throws IOException, XNIException
  {
    fEntityScanner.scanQName(fElementQName, XMLScanner.NameType.ELEMENTSTART);
    String str1 = fElementQName.rawname;
    if (fBindNamespaces)
    {
      fNamespaceContext.pushContext();
      if ((fScannerState == 26) && (fPerformValidation))
      {
        fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { str1 }, (short)1);
        if ((fDoctypeName == null) || (!fDoctypeName.equals(str1))) {
          fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { fDoctypeName, str1 }, (short)1);
        }
      }
    }
    fCurrentElement = fElementStack.pushElement(fElementQName);
    boolean bool1 = false;
    fAttributes.removeAllAttributes();
    int i;
    for (;;)
    {
      boolean bool2 = fEntityScanner.skipSpaces();
      i = fEntityScanner.peekChar();
      if (i == 62)
      {
        fEntityScanner.scanChar(null);
        break;
      }
      if (i == 47)
      {
        fEntityScanner.scanChar(null);
        if (!fEntityScanner.skipChar(62, null)) {
          reportFatalError("ElementUnterminated", new Object[] { str1 });
        }
        bool1 = true;
        break;
      }
      if (((!isValidNameStartChar(i)) || (!bool2)) && ((!isValidNameStartHighSurrogate(i)) || (!bool2))) {
        reportFatalError("ElementUnterminated", new Object[] { str1 });
      }
      scanAttribute(fAttributes);
      if ((fSecurityManager != null) && (!fSecurityManager.isNoLimit(fElementAttributeLimit)) && (fAttributes.getLength() > fElementAttributeLimit)) {
        fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementAttributeLimit", new Object[] { str1, new Integer(fElementAttributeLimit) }, (short)2);
      }
    }
    if (fBindNamespaces)
    {
      if (fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
        fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { fElementQName.rawname }, (short)2);
      }
      String str2 = fElementQName.prefix != null ? fElementQName.prefix : XMLSymbols.EMPTY_STRING;
      fElementQName.uri = fNamespaceContext.getURI(str2);
      fCurrentElement.uri = fElementQName.uri;
      if ((fElementQName.prefix == null) && (fElementQName.uri != null))
      {
        fElementQName.prefix = XMLSymbols.EMPTY_STRING;
        fCurrentElement.prefix = XMLSymbols.EMPTY_STRING;
      }
      if ((fElementQName.prefix != null) && (fElementQName.uri == null)) {
        fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { fElementQName.prefix, fElementQName.rawname }, (short)2);
      }
      i = fAttributes.getLength();
      for (int j = 0; j < i; j++)
      {
        fAttributes.getName(j, fAttributeQName);
        String str3 = fAttributeQName.prefix != null ? fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
        String str4 = fNamespaceContext.getURI(str3);
        if (((fAttributeQName.uri == null) || (fAttributeQName.uri != str4)) && (str3 != XMLSymbols.EMPTY_STRING))
        {
          fAttributeQName.uri = str4;
          if (str4 == null) {
            fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { fElementQName.rawname, fAttributeQName.rawname, str3 }, (short)2);
          }
          fAttributes.setURI(j, str4);
        }
      }
      if (i > 1)
      {
        QName localQName = fAttributes.checkDuplicatesNS();
        if (localQName != null) {
          if (uri != null) {
            fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { fElementQName.rawname, localpart, uri }, (short)2);
          } else {
            fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNotUnique", new Object[] { fElementQName.rawname, rawname }, (short)2);
          }
        }
      }
    }
    if (bool1)
    {
      fMarkupDepth -= 1;
      if (fMarkupDepth < fEntityStack[(fEntityDepth - 1)]) {
        reportFatalError("ElementEntityMismatch", new Object[] { fCurrentElement.rawname });
      }
      if (fDocumentHandler != null) {
        fDocumentHandler.emptyElement(fElementQName, fAttributes, null);
      }
      fScanEndElement = true;
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
    return bool1;
  }
  
  protected void scanStartElementName()
    throws IOException, XNIException
  {
    fEntityScanner.scanQName(fElementQName, XMLScanner.NameType.ELEMENTSTART);
    fSawSpace = fEntityScanner.skipSpaces();
  }
  
  protected boolean scanStartElementAfterName()
    throws IOException, XNIException
  {
    String str1 = fElementQName.rawname;
    if (fBindNamespaces)
    {
      fNamespaceContext.pushContext();
      if ((fScannerState == 26) && (fPerformValidation))
      {
        fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { str1 }, (short)1);
        if ((fDoctypeName == null) || (!fDoctypeName.equals(str1))) {
          fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { fDoctypeName, str1 }, (short)1);
        }
      }
    }
    fCurrentElement = fElementStack.pushElement(fElementQName);
    boolean bool = false;
    fAttributes.removeAllAttributes();
    for (;;)
    {
      int i = fEntityScanner.peekChar();
      if (i == 62)
      {
        fEntityScanner.scanChar(null);
        break;
      }
      if (i == 47)
      {
        fEntityScanner.scanChar(null);
        if (!fEntityScanner.skipChar(62, null)) {
          reportFatalError("ElementUnterminated", new Object[] { str1 });
        }
        bool = true;
        break;
      }
      if (((!isValidNameStartChar(i)) || (!fSawSpace)) && ((!isValidNameStartHighSurrogate(i)) || (!fSawSpace))) {
        reportFatalError("ElementUnterminated", new Object[] { str1 });
      }
      scanAttribute(fAttributes);
      fSawSpace = fEntityScanner.skipSpaces();
    }
    if (fBindNamespaces)
    {
      if (fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
        fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { fElementQName.rawname }, (short)2);
      }
      String str2 = fElementQName.prefix != null ? fElementQName.prefix : XMLSymbols.EMPTY_STRING;
      fElementQName.uri = fNamespaceContext.getURI(str2);
      fCurrentElement.uri = fElementQName.uri;
      if ((fElementQName.prefix == null) && (fElementQName.uri != null))
      {
        fElementQName.prefix = XMLSymbols.EMPTY_STRING;
        fCurrentElement.prefix = XMLSymbols.EMPTY_STRING;
      }
      if ((fElementQName.prefix != null) && (fElementQName.uri == null)) {
        fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { fElementQName.prefix, fElementQName.rawname }, (short)2);
      }
      int j = fAttributes.getLength();
      for (int k = 0; k < j; k++)
      {
        fAttributes.getName(k, fAttributeQName);
        String str3 = fAttributeQName.prefix != null ? fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
        String str4 = fNamespaceContext.getURI(str3);
        if (((fAttributeQName.uri == null) || (fAttributeQName.uri != str4)) && (str3 != XMLSymbols.EMPTY_STRING))
        {
          fAttributeQName.uri = str4;
          if (str4 == null) {
            fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { fElementQName.rawname, fAttributeQName.rawname, str3 }, (short)2);
          }
          fAttributes.setURI(k, str4);
        }
      }
      if (j > 1)
      {
        QName localQName = fAttributes.checkDuplicatesNS();
        if (localQName != null) {
          if (uri != null) {
            fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { fElementQName.rawname, localpart, uri }, (short)2);
          } else {
            fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNotUnique", new Object[] { fElementQName.rawname, rawname }, (short)2);
          }
        }
      }
    }
    if (fDocumentHandler != null) {
      if (bool)
      {
        fMarkupDepth -= 1;
        if (fMarkupDepth < fEntityStack[(fEntityDepth - 1)]) {
          reportFatalError("ElementEntityMismatch", new Object[] { fCurrentElement.rawname });
        }
        fDocumentHandler.emptyElement(fElementQName, fAttributes, null);
        if (fBindNamespaces) {
          fNamespaceContext.popContext();
        }
        fElementStack.popElement();
      }
      else
      {
        fDocumentHandler.startElement(fElementQName, fAttributes, null);
      }
    }
    return bool;
  }
  
  protected void scanAttribute(XMLAttributesImpl paramXMLAttributesImpl)
    throws IOException, XNIException
  {
    fEntityScanner.scanQName(fAttributeQName, XMLScanner.NameType.ATTRIBUTENAME);
    fEntityScanner.skipSpaces();
    if (!fEntityScanner.skipChar(61, XMLScanner.NameType.ATTRIBUTE)) {
      reportFatalError("EqRequiredInAttribute", new Object[] { fCurrentElement.rawname, fAttributeQName.rawname });
    }
    fEntityScanner.skipSpaces();
    int i;
    if (fBindNamespaces)
    {
      i = paramXMLAttributesImpl.getLength();
      paramXMLAttributesImpl.addAttributeNS(fAttributeQName, XMLSymbols.fCDATASymbol, null);
    }
    else
    {
      j = paramXMLAttributesImpl.getLength();
      i = paramXMLAttributesImpl.addAttribute(fAttributeQName, XMLSymbols.fCDATASymbol, null);
      if (j == paramXMLAttributesImpl.getLength()) {
        reportFatalError("AttributeNotUnique", new Object[] { fCurrentElement.rawname, fAttributeQName.rawname });
      }
    }
    int j = (fHasExternalDTD) && (!fStandalone) ? 1 : 0;
    String str1 = fAttributeQName.localpart;
    String str2 = fAttributeQName.prefix != null ? fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
    boolean bool = fBindNamespaces & ((str2 == XMLSymbols.PREFIX_XMLNS) || ((str2 == XMLSymbols.EMPTY_STRING) && (str1 == XMLSymbols.PREFIX_XMLNS)));
    scanAttributeValue(fTempString, fTempString2, fAttributeQName.rawname, j, fCurrentElement.rawname, bool);
    String str3 = fTempString.toString();
    paramXMLAttributesImpl.setValue(i, str3);
    paramXMLAttributesImpl.setNonNormalizedValue(i, fTempString2.toString());
    paramXMLAttributesImpl.setSpecified(i, true);
    if (fBindNamespaces) {
      if (bool)
      {
        if (str3.length() > fXMLNameLimit) {
          fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MaxXMLNameLimit", new Object[] { str3, Integer.valueOf(str3.length()), Integer.valueOf(fXMLNameLimit), fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.MAX_NAME_LIMIT) }, (short)2);
        }
        String str4 = fSymbolTable.addSymbol(str3);
        if ((str2 == XMLSymbols.PREFIX_XMLNS) && (str1 == XMLSymbols.PREFIX_XMLNS)) {
          fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { fAttributeQName }, (short)2);
        }
        if (str4 == NamespaceContext.XMLNS_URI) {
          fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { fAttributeQName }, (short)2);
        }
        if (str1 == XMLSymbols.PREFIX_XML)
        {
          if (str4 != NamespaceContext.XML_URI) {
            fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { fAttributeQName }, (short)2);
          }
        }
        else if (str4 == NamespaceContext.XML_URI) {
          fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { fAttributeQName }, (short)2);
        }
        str2 = str1 != XMLSymbols.PREFIX_XMLNS ? str1 : XMLSymbols.EMPTY_STRING;
        fNamespaceContext.declarePrefix(str2, str4.length() != 0 ? str4 : null);
        paramXMLAttributesImpl.setURI(i, fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS));
      }
      else if (fAttributeQName.prefix != null)
      {
        paramXMLAttributesImpl.setURI(i, fNamespaceContext.getURI(fAttributeQName.prefix));
      }
    }
  }
  
  protected int scanEndElement()
    throws IOException, XNIException
  {
    QName localQName = fElementStack.popElement();
    if (!fEntityScanner.skipString(rawname)) {
      reportFatalError("ETagRequired", new Object[] { rawname });
    }
    fEntityScanner.skipSpaces();
    if (!fEntityScanner.skipChar(62, XMLScanner.NameType.ELEMENTEND)) {
      reportFatalError("ETagUnterminated", new Object[] { rawname });
    }
    fMarkupDepth -= 1;
    fMarkupDepth -= 1;
    if (fMarkupDepth < fEntityStack[(fEntityDepth - 1)]) {
      reportFatalError("ElementEntityMismatch", new Object[] { rawname });
    }
    if (fDocumentHandler != null) {
      fDocumentHandler.endElement(localQName, null);
    }
    if (dtdGrammarUtil != null) {
      dtdGrammarUtil.endElement(localQName);
    }
    return fMarkupDepth;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException
  {
    super.reset(paramXMLComponentManager);
    fPerformValidation = false;
    fBindNamespaces = false;
  }
  
  protected XMLDocumentFragmentScannerImpl.Driver createContentDriver()
  {
    return new NS11ContentDriver();
  }
  
  public int next()
    throws IOException, XNIException
  {
    if ((fScannerLastState == 2) && (fBindNamespaces))
    {
      fScannerLastState = -1;
      fNamespaceContext.popContext();
    }
    return fScannerLastState = super.next();
  }
  
  protected final class NS11ContentDriver
    extends XMLDocumentScannerImpl.ContentDriver
  {
    protected NS11ContentDriver()
    {
      super();
    }
    
    protected boolean scanRootElementHook()
      throws IOException, XNIException
    {
      if ((fExternalSubsetResolver != null) && (!fSeenDoctypeDecl) && (!fDisallowDoctype) && ((fValidation) || (fLoadExternalDTD)))
      {
        scanStartElementName();
        resolveExternalSubsetAndRead();
        reconfigurePipeline();
        if (scanStartElementAfterName())
        {
          setScannerState(44);
          setDriver(fTrailingMiscDriver);
          return true;
        }
      }
      else
      {
        reconfigurePipeline();
        if (scanStartElement())
        {
          setScannerState(44);
          setDriver(fTrailingMiscDriver);
          return true;
        }
      }
      return false;
    }
    
    private void reconfigurePipeline()
    {
      if (fDTDValidator == null)
      {
        fBindNamespaces = true;
      }
      else if (!fDTDValidator.hasGrammar())
      {
        fBindNamespaces = true;
        fPerformValidation = fDTDValidator.validate();
        XMLDocumentSource localXMLDocumentSource = fDTDValidator.getDocumentSource();
        XMLDocumentHandler localXMLDocumentHandler = fDTDValidator.getDocumentHandler();
        localXMLDocumentSource.setDocumentHandler(localXMLDocumentHandler);
        if (localXMLDocumentHandler != null) {
          localXMLDocumentHandler.setDocumentSource(localXMLDocumentSource);
        }
        fDTDValidator.setDocumentSource(null);
        fDTDValidator.setDocumentHandler(null);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XML11NSDocumentScannerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */