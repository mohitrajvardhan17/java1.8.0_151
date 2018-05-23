package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;

public class XMLNamespaceBinder
  implements XMLComponent, XMLDocumentFilter
{
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/namespaces" };
  private static final Boolean[] FEATURE_DEFAULTS = { null };
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter" };
  private static final Object[] PROPERTY_DEFAULTS = { null, null };
  protected boolean fNamespaces;
  protected SymbolTable fSymbolTable;
  protected XMLErrorReporter fErrorReporter;
  protected XMLDocumentHandler fDocumentHandler;
  protected XMLDocumentSource fDocumentSource;
  protected boolean fOnlyPassPrefixMappingEvents;
  private NamespaceContext fNamespaceContext;
  private QName fAttributeQName = new QName();
  
  public XMLNamespaceBinder() {}
  
  public void setOnlyPassPrefixMappingEvents(boolean paramBoolean)
  {
    fOnlyPassPrefixMappingEvents = paramBoolean;
  }
  
  public boolean getOnlyPassPrefixMappingEvents()
  {
    return fOnlyPassPrefixMappingEvents;
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XNIException
  {
    fNamespaces = paramXMLComponentManager.getFeature("http://xml.org/sax/features/namespaces", true);
    fSymbolTable = ((SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    fErrorReporter = ((XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
  }
  
  public String[] getRecognizedFeatures()
  {
    return (String[])RECOGNIZED_FEATURES.clone();
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws XMLConfigurationException
  {}
  
  public String[] getRecognizedProperties()
  {
    return (String[])RECOGNIZED_PROPERTIES.clone();
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws XMLConfigurationException
  {
    if (paramString.startsWith("http://apache.org/xml/properties/"))
    {
      int i = paramString.length() - "http://apache.org/xml/properties/".length();
      if ((i == "internal/symbol-table".length()) && (paramString.endsWith("internal/symbol-table"))) {
        fSymbolTable = ((SymbolTable)paramObject);
      } else if ((i == "internal/error-reporter".length()) && (paramString.endsWith("internal/error-reporter"))) {
        fErrorReporter = ((XMLErrorReporter)paramObject);
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
  
  public void setDocumentSource(XMLDocumentSource paramXMLDocumentSource)
  {
    fDocumentSource = paramXMLDocumentSource;
  }
  
  public XMLDocumentSource getDocumentSource()
  {
    return fDocumentSource;
  }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (!fOnlyPassPrefixMappingEvents)) {
      fDocumentHandler.startGeneralEntity(paramString1, paramXMLResourceIdentifier, paramString2, paramAugmentations);
    }
  }
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (!fOnlyPassPrefixMappingEvents)) {
      fDocumentHandler.textDecl(paramString1, paramString2, paramAugmentations);
    }
  }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations)
    throws XNIException
  {
    fNamespaceContext = paramNamespaceContext;
    if ((fDocumentHandler != null) && (!fOnlyPassPrefixMappingEvents)) {
      fDocumentHandler.startDocument(paramXMLLocator, paramString, paramNamespaceContext, paramAugmentations);
    }
  }
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (!fOnlyPassPrefixMappingEvents)) {
      fDocumentHandler.xmlDecl(paramString1, paramString2, paramString3, paramAugmentations);
    }
  }
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (!fOnlyPassPrefixMappingEvents)) {
      fDocumentHandler.doctypeDecl(paramString1, paramString2, paramString3, paramAugmentations);
    }
  }
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (!fOnlyPassPrefixMappingEvents)) {
      fDocumentHandler.comment(paramXMLString, paramAugmentations);
    }
  }
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (!fOnlyPassPrefixMappingEvents)) {
      fDocumentHandler.processingInstruction(paramString, paramXMLString, paramAugmentations);
    }
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    if (fNamespaces) {
      handleStartElement(paramQName, paramXMLAttributes, paramAugmentations, false);
    } else if (fDocumentHandler != null) {
      fDocumentHandler.startElement(paramQName, paramXMLAttributes, paramAugmentations);
    }
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations)
    throws XNIException
  {
    if (fNamespaces)
    {
      handleStartElement(paramQName, paramXMLAttributes, paramAugmentations, true);
      handleEndElement(paramQName, paramAugmentations, true);
    }
    else if (fDocumentHandler != null)
    {
      fDocumentHandler.emptyElement(paramQName, paramXMLAttributes, paramAugmentations);
    }
  }
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (!fOnlyPassPrefixMappingEvents)) {
      fDocumentHandler.characters(paramXMLString, paramAugmentations);
    }
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (!fOnlyPassPrefixMappingEvents)) {
      fDocumentHandler.ignorableWhitespace(paramXMLString, paramAugmentations);
    }
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations)
    throws XNIException
  {
    if (fNamespaces) {
      handleEndElement(paramQName, paramAugmentations, false);
    } else if (fDocumentHandler != null) {
      fDocumentHandler.endElement(paramQName, paramAugmentations);
    }
  }
  
  public void startCDATA(Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (!fOnlyPassPrefixMappingEvents)) {
      fDocumentHandler.startCDATA(paramAugmentations);
    }
  }
  
  public void endCDATA(Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (!fOnlyPassPrefixMappingEvents)) {
      fDocumentHandler.endCDATA(paramAugmentations);
    }
  }
  
  public void endDocument(Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (!fOnlyPassPrefixMappingEvents)) {
      fDocumentHandler.endDocument(paramAugmentations);
    }
  }
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations)
    throws XNIException
  {
    if ((fDocumentHandler != null) && (!fOnlyPassPrefixMappingEvents)) {
      fDocumentHandler.endGeneralEntity(paramString, paramAugmentations);
    }
  }
  
  protected void handleStartElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations, boolean paramBoolean)
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
        if (prefixBoundToNullURI(str4, str2)) {
          fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "EmptyPrefixedAttName", new Object[] { paramXMLAttributes.getQName(j) }, (short)2);
        } else {
          fNamespaceContext.declarePrefix(str3, str4.length() != 0 ? str4 : null);
        }
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
    if ((fDocumentHandler != null) && (!fOnlyPassPrefixMappingEvents)) {
      if (paramBoolean) {
        fDocumentHandler.emptyElement(paramQName, paramXMLAttributes, paramAugmentations);
      } else {
        fDocumentHandler.startElement(paramQName, paramXMLAttributes, paramAugmentations);
      }
    }
  }
  
  protected void handleEndElement(QName paramQName, Augmentations paramAugmentations, boolean paramBoolean)
    throws XNIException
  {
    String str = prefix != null ? prefix : XMLSymbols.EMPTY_STRING;
    uri = fNamespaceContext.getURI(str);
    if (uri != null) {
      prefix = str;
    }
    if ((fDocumentHandler != null) && (!fOnlyPassPrefixMappingEvents) && (!paramBoolean)) {
      fDocumentHandler.endElement(paramQName, paramAugmentations);
    }
    fNamespaceContext.popContext();
  }
  
  protected boolean prefixBoundToNullURI(String paramString1, String paramString2)
  {
    return (paramString1 == XMLSymbols.EMPTY_STRING) && (paramString2 != XMLSymbols.PREFIX_XMLNS);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLNamespaceBinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */