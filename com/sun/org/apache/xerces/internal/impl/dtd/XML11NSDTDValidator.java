package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;

public class XML11NSDTDValidator
  extends XML11DTDValidator
{
  private QName fAttributeQName = new QName();
  
  public XML11NSDTDValidator() {}
  
  protected final void startNamespaceScope(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    fNamespaceContext.pushContext();
    if (prefix == XMLSymbols.PREFIX_XMLNS) {
      fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { rawname }, (short)2);
    }
    int i = paramXMLAttributes.getLength();
    String str3;
    String str4;
    for (int j = 0; j < i; j++)
    {
      String str2 = paramXMLAttributes.getLocalName(j);
      str3 = paramXMLAttributes.getPrefix(j);
      if ((str3 == XMLSymbols.PREFIX_XMLNS) || ((str3 == XMLSymbols.EMPTY_STRING) && (str2 == XMLSymbols.PREFIX_XMLNS)))
      {
        str4 = fSymbolTable.addSymbol(paramXMLAttributes.getValue(j));
        if ((str3 == XMLSymbols.PREFIX_XMLNS) && (str2 == XMLSymbols.PREFIX_XMLNS)) {
          fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { paramXMLAttributes.getQName(j) }, (short)2);
        }
        if (str4 == NamespaceContext.XMLNS_URI) {
          fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { paramXMLAttributes.getQName(j) }, (short)2);
        }
        if (str2 == XMLSymbols.PREFIX_XML)
        {
          if (str4 != NamespaceContext.XML_URI) {
            fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { paramXMLAttributes.getQName(j) }, (short)2);
          }
        }
        else if (str4 == NamespaceContext.XML_URI) {
          fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { paramXMLAttributes.getQName(j) }, (short)2);
        }
        str3 = str2 != XMLSymbols.PREFIX_XMLNS ? str2 : XMLSymbols.EMPTY_STRING;
        fNamespaceContext.declarePrefix(str3, str4.length() != 0 ? str4 : null);
      }
    }
    String str1 = prefix != null ? prefix : XMLSymbols.EMPTY_STRING;
    uri = fNamespaceContext.getURI(str1);
    if ((prefix == null) && (uri != null)) {
      prefix = XMLSymbols.EMPTY_STRING;
    }
    if ((prefix != null) && (uri == null)) {
      fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { prefix, rawname }, (short)2);
    }
    for (int k = 0; k < i; k++)
    {
      paramXMLAttributes.getName(k, fAttributeQName);
      str3 = fAttributeQName.prefix != null ? fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
      str4 = fAttributeQName.rawname;
      if (str4 == XMLSymbols.PREFIX_XMLNS)
      {
        fAttributeQName.uri = fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS);
        paramXMLAttributes.setName(k, fAttributeQName);
      }
      else if (str3 != XMLSymbols.EMPTY_STRING)
      {
        fAttributeQName.uri = fNamespaceContext.getURI(str3);
        if (fAttributeQName.uri == null) {
          fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { rawname, str4, str3 }, (short)2);
        }
        paramXMLAttributes.setName(k, fAttributeQName);
      }
    }
    k = paramXMLAttributes.getLength();
    for (int m = 0; m < k - 1; m++)
    {
      str4 = paramXMLAttributes.getURI(m);
      if ((str4 != null) && (str4 != NamespaceContext.XMLNS_URI))
      {
        String str5 = paramXMLAttributes.getLocalName(m);
        for (int n = m + 1; n < k; n++)
        {
          String str6 = paramXMLAttributes.getLocalName(n);
          String str7 = paramXMLAttributes.getURI(n);
          if ((str5 == str6) && (str4 == str7)) {
            fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { rawname, str5, str4 }, (short)2);
          }
        }
      }
    }
  }
  
  protected void endNamespaceScope(QName paramQName, Augmentations paramAugmentations, boolean paramBoolean)
    throws XNIException
  {
    String str = prefix != null ? prefix : XMLSymbols.EMPTY_STRING;
    uri = fNamespaceContext.getURI(str);
    if (uri != null) {
      prefix = str;
    }
    if ((fDocumentHandler != null) && (!paramBoolean)) {
      fDocumentHandler.endElement(paramQName, paramAugmentations);
    }
    fNamespaceContext.popContext();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\XML11NSDTDValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */