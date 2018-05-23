package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidatorFilter;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
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

public class XMLNSDocumentScannerImpl
  extends XMLDocumentScannerImpl
{
  protected boolean fBindNamespaces;
  protected boolean fPerformValidation;
  protected boolean fNotAddNSDeclAsAttribute = false;
  private XMLDTDValidatorFilter fDTDValidator;
  private boolean fXmlnsDeclared = false;
  
  public XMLNSDocumentScannerImpl() {}
  
  public void reset(PropertyManager paramPropertyManager)
  {
    setPropertyManager(paramPropertyManager);
    super.reset(paramPropertyManager);
    fBindNamespaces = false;
    fNotAddNSDeclAsAttribute = (!((Boolean)paramPropertyManager.getProperty("add-namespacedecl-as-attrbiute")).booleanValue());
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException
  {
    super.reset(paramXMLComponentManager);
    fNotAddNSDeclAsAttribute = false;
    fPerformValidation = false;
    fBindNamespaces = false;
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
  
  public void setDTDValidator(XMLDTDValidatorFilter paramXMLDTDValidatorFilter)
  {
    fDTDValidator = paramXMLDTDValidatorFilter;
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
    checkDepth((String)localObject);
    if (fBindNamespaces)
    {
      fNamespaceContext.pushContext();
      if ((fScannerState == 26) && (fPerformValidation))
      {
        fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { localObject }, (short)1);
        if ((fDoctypeName == null) || (!fDoctypeName.equals(localObject))) {
          fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { fDoctypeName, localObject }, (short)1);
        }
      }
    }
    fEmptyElement = false;
    fAttributes.removeAllAttributes();
    if (!seekCloseOfStartTag())
    {
      fReadingAttributes = true;
      fAttributeCacheUsedCount = 0;
      fStringBufferIndex = 0;
      fAddDefaultAttr = true;
      fXmlnsDeclared = false;
      do
      {
        scanAttribute(fAttributes);
        if ((fSecurityManager != null) && (!fSecurityManager.isNoLimit(fElementAttributeLimit)) && (fAttributes.getLength() > fElementAttributeLimit)) {
          fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementAttributeLimit", new Object[] { localObject, Integer.valueOf(fElementAttributeLimit) }, (short)2);
        }
      } while (!seekCloseOfStartTag());
      fReadingAttributes = false;
    }
    if (fBindNamespaces)
    {
      if (fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
        fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { fElementQName.rawname }, (short)2);
      }
      String str1 = fElementQName.prefix != null ? fElementQName.prefix : XMLSymbols.EMPTY_STRING;
      fElementQName.uri = fNamespaceContext.getURI(str1);
      fCurrentElement.uri = fElementQName.uri;
      if ((fElementQName.prefix == null) && (fElementQName.uri != null)) {
        fElementQName.prefix = XMLSymbols.EMPTY_STRING;
      }
      if ((fElementQName.prefix != null) && (fElementQName.uri == null)) {
        fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { fElementQName.prefix, fElementQName.rawname }, (short)2);
      }
      int i = fAttributes.getLength();
      for (int j = 0; j < i; j++)
      {
        fAttributes.getName(j, fAttributeQName);
        String str2 = fAttributeQName.prefix != null ? fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
        String str3 = fNamespaceContext.getURI(str2);
        if (((fAttributeQName.uri == null) || (fAttributeQName.uri != str3)) && (str2 != XMLSymbols.EMPTY_STRING))
        {
          fAttributeQName.uri = str3;
          if (str3 == null) {
            fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { fElementQName.rawname, fAttributeQName.rawname, str2 }, (short)2);
          }
          fAttributes.setURI(j, str3);
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
    if (fEmptyElement)
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
    return fEmptyElement;
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
    int i = 0;
    boolean bool1 = (fHasExternalDTD) && (!fStandalone);
    XMLString localXMLString = getString();
    String str1 = fAttributeQName.localpart;
    String str2 = fAttributeQName.prefix != null ? fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
    boolean bool2 = fBindNamespaces & ((str2 == XMLSymbols.PREFIX_XMLNS) || ((str2 == XMLSymbols.EMPTY_STRING) && (str1 == XMLSymbols.PREFIX_XMLNS)));
    scanAttributeValue(localXMLString, fTempString2, fAttributeQName.rawname, paramXMLAttributesImpl, i, bool1, fCurrentElement.rawname, bool2);
    Object localObject = null;
    if ((fBindNamespaces) && (bool2))
    {
      if (length > fXMLNameLimit) {
        fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MaxXMLNameLimit", new Object[] { new String(ch, offset, length), Integer.valueOf(length), Integer.valueOf(fXMLNameLimit), fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.MAX_NAME_LIMIT) }, (short)2);
      }
      String str3 = fSymbolTable.addSymbol(ch, offset, length);
      localObject = str3;
      if ((str2 == XMLSymbols.PREFIX_XMLNS) && (str1 == XMLSymbols.PREFIX_XMLNS)) {
        fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { fAttributeQName }, (short)2);
      }
      if (str3 == NamespaceContext.XMLNS_URI) {
        fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { fAttributeQName }, (short)2);
      }
      if (str1 == XMLSymbols.PREFIX_XML)
      {
        if (str3 != NamespaceContext.XML_URI) {
          fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { fAttributeQName }, (short)2);
        }
      }
      else if (str3 == NamespaceContext.XML_URI) {
        fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { fAttributeQName }, (short)2);
      }
      str2 = str1 != XMLSymbols.PREFIX_XMLNS ? str1 : XMLSymbols.EMPTY_STRING;
      if ((str2 == XMLSymbols.EMPTY_STRING) && (str1 == XMLSymbols.PREFIX_XMLNS)) {
        fAttributeQName.prefix = XMLSymbols.PREFIX_XMLNS;
      }
      if ((str3 == XMLSymbols.EMPTY_STRING) && (str1 != XMLSymbols.PREFIX_XMLNS)) {
        fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "EmptyPrefixedAttName", new Object[] { fAttributeQName }, (short)2);
      }
      if (((NamespaceSupport)fNamespaceContext).containsPrefixInCurrentContext(str2)) {
        reportFatalError("AttributeNotUnique", new Object[] { fCurrentElement.rawname, fAttributeQName.rawname });
      }
      boolean bool3 = fNamespaceContext.declarePrefix(str2, str3.length() != 0 ? str3 : null);
      if (!bool3)
      {
        if (fXmlnsDeclared) {
          reportFatalError("AttributeNotUnique", new Object[] { fCurrentElement.rawname, fAttributeQName.rawname });
        }
        fXmlnsDeclared = true;
      }
      if (fNotAddNSDeclAsAttribute) {
        return;
      }
    }
    if (fBindNamespaces)
    {
      i = paramXMLAttributesImpl.getLength();
      paramXMLAttributesImpl.addAttributeNS(fAttributeQName, XMLSymbols.fCDATASymbol, null);
    }
    else
    {
      int j = paramXMLAttributesImpl.getLength();
      i = paramXMLAttributesImpl.addAttribute(fAttributeQName, XMLSymbols.fCDATASymbol, null);
      if (j == paramXMLAttributesImpl.getLength()) {
        reportFatalError("AttributeNotUnique", new Object[] { fCurrentElement.rawname, fAttributeQName.rawname });
      }
    }
    paramXMLAttributesImpl.setValue(i, (String)localObject, localXMLString);
    paramXMLAttributesImpl.setSpecified(i, true);
    if (fAttributeQName.prefix != null) {
      paramXMLAttributesImpl.setURI(i, fNamespaceContext.getURI(fAttributeQName.prefix));
    }
  }
  
  protected XMLDocumentFragmentScannerImpl.Driver createContentDriver()
  {
    return new NSContentDriver();
  }
  
  protected final class NSContentDriver
    extends XMLDocumentScannerImpl.ContentDriver
  {
    protected NSContentDriver()
    {
      super();
    }
    
    protected boolean scanRootElementHook()
      throws IOException, XNIException
    {
      reconfigurePipeline();
      if (scanStartElement())
      {
        setScannerState(44);
        setDriver(fTrailingMiscDriver);
        return true;
      }
      return false;
    }
    
    private void reconfigurePipeline()
    {
      if ((fNamespaces) && (fDTDValidator == null))
      {
        fBindNamespaces = true;
      }
      else if ((fNamespaces) && (!fDTDValidator.hasGrammar()))
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLNSDocumentScannerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */