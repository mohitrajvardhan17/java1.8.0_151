package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.utils.XMLString;
import java.util.Map;
import javax.xml.transform.SourceLocator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class AdaptiveResultTreeImpl
  extends SimpleResultTreeImpl
{
  private static int _documentURIIndex = 0;
  private static final String EMPTY_STRING = "".intern();
  private SAXImpl _dom;
  private DTMWSFilter _wsfilter;
  private int _initSize;
  private boolean _buildIdIndex;
  private final AttributesImpl _attributes = new AttributesImpl();
  private String _openElementName;
  
  public AdaptiveResultTreeImpl(XSLTCDTMManager paramXSLTCDTMManager, int paramInt1, DTMWSFilter paramDTMWSFilter, int paramInt2, boolean paramBoolean)
  {
    super(paramXSLTCDTMManager, paramInt1);
    _wsfilter = paramDTMWSFilter;
    _initSize = paramInt2;
    _buildIdIndex = paramBoolean;
  }
  
  public DOM getNestedDOM()
  {
    return _dom;
  }
  
  public int getDocument()
  {
    if (_dom != null) {
      return _dom.getDocument();
    }
    return super.getDocument();
  }
  
  public String getStringValue()
  {
    if (_dom != null) {
      return _dom.getStringValue();
    }
    return super.getStringValue();
  }
  
  public DTMAxisIterator getIterator()
  {
    if (_dom != null) {
      return _dom.getIterator();
    }
    return super.getIterator();
  }
  
  public DTMAxisIterator getChildren(int paramInt)
  {
    if (_dom != null) {
      return _dom.getChildren(paramInt);
    }
    return super.getChildren(paramInt);
  }
  
  public DTMAxisIterator getTypedChildren(int paramInt)
  {
    if (_dom != null) {
      return _dom.getTypedChildren(paramInt);
    }
    return super.getTypedChildren(paramInt);
  }
  
  public DTMAxisIterator getAxisIterator(int paramInt)
  {
    if (_dom != null) {
      return _dom.getAxisIterator(paramInt);
    }
    return super.getAxisIterator(paramInt);
  }
  
  public DTMAxisIterator getTypedAxisIterator(int paramInt1, int paramInt2)
  {
    if (_dom != null) {
      return _dom.getTypedAxisIterator(paramInt1, paramInt2);
    }
    return super.getTypedAxisIterator(paramInt1, paramInt2);
  }
  
  public DTMAxisIterator getNthDescendant(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (_dom != null) {
      return _dom.getNthDescendant(paramInt1, paramInt2, paramBoolean);
    }
    return super.getNthDescendant(paramInt1, paramInt2, paramBoolean);
  }
  
  public DTMAxisIterator getNamespaceAxisIterator(int paramInt1, int paramInt2)
  {
    if (_dom != null) {
      return _dom.getNamespaceAxisIterator(paramInt1, paramInt2);
    }
    return super.getNamespaceAxisIterator(paramInt1, paramInt2);
  }
  
  public DTMAxisIterator getNodeValueIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt, String paramString, boolean paramBoolean)
  {
    if (_dom != null) {
      return _dom.getNodeValueIterator(paramDTMAxisIterator, paramInt, paramString, paramBoolean);
    }
    return super.getNodeValueIterator(paramDTMAxisIterator, paramInt, paramString, paramBoolean);
  }
  
  public DTMAxisIterator orderNodes(DTMAxisIterator paramDTMAxisIterator, int paramInt)
  {
    if (_dom != null) {
      return _dom.orderNodes(paramDTMAxisIterator, paramInt);
    }
    return super.orderNodes(paramDTMAxisIterator, paramInt);
  }
  
  public String getNodeName(int paramInt)
  {
    if (_dom != null) {
      return _dom.getNodeName(paramInt);
    }
    return super.getNodeName(paramInt);
  }
  
  public String getNodeNameX(int paramInt)
  {
    if (_dom != null) {
      return _dom.getNodeNameX(paramInt);
    }
    return super.getNodeNameX(paramInt);
  }
  
  public String getNamespaceName(int paramInt)
  {
    if (_dom != null) {
      return _dom.getNamespaceName(paramInt);
    }
    return super.getNamespaceName(paramInt);
  }
  
  public int getExpandedTypeID(int paramInt)
  {
    if (_dom != null) {
      return _dom.getExpandedTypeID(paramInt);
    }
    return super.getExpandedTypeID(paramInt);
  }
  
  public int getNamespaceType(int paramInt)
  {
    if (_dom != null) {
      return _dom.getNamespaceType(paramInt);
    }
    return super.getNamespaceType(paramInt);
  }
  
  public int getParent(int paramInt)
  {
    if (_dom != null) {
      return _dom.getParent(paramInt);
    }
    return super.getParent(paramInt);
  }
  
  public int getAttributeNode(int paramInt1, int paramInt2)
  {
    if (_dom != null) {
      return _dom.getAttributeNode(paramInt1, paramInt2);
    }
    return super.getAttributeNode(paramInt1, paramInt2);
  }
  
  public String getStringValueX(int paramInt)
  {
    if (_dom != null) {
      return _dom.getStringValueX(paramInt);
    }
    return super.getStringValueX(paramInt);
  }
  
  public void copy(int paramInt, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    if (_dom != null) {
      _dom.copy(paramInt, paramSerializationHandler);
    } else {
      super.copy(paramInt, paramSerializationHandler);
    }
  }
  
  public void copy(DTMAxisIterator paramDTMAxisIterator, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    if (_dom != null) {
      _dom.copy(paramDTMAxisIterator, paramSerializationHandler);
    } else {
      super.copy(paramDTMAxisIterator, paramSerializationHandler);
    }
  }
  
  public String shallowCopy(int paramInt, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    if (_dom != null) {
      return _dom.shallowCopy(paramInt, paramSerializationHandler);
    }
    return super.shallowCopy(paramInt, paramSerializationHandler);
  }
  
  public boolean lessThan(int paramInt1, int paramInt2)
  {
    if (_dom != null) {
      return _dom.lessThan(paramInt1, paramInt2);
    }
    return super.lessThan(paramInt1, paramInt2);
  }
  
  public void characters(int paramInt, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    if (_dom != null) {
      _dom.characters(paramInt, paramSerializationHandler);
    } else {
      super.characters(paramInt, paramSerializationHandler);
    }
  }
  
  public Node makeNode(int paramInt)
  {
    if (_dom != null) {
      return _dom.makeNode(paramInt);
    }
    return super.makeNode(paramInt);
  }
  
  public Node makeNode(DTMAxisIterator paramDTMAxisIterator)
  {
    if (_dom != null) {
      return _dom.makeNode(paramDTMAxisIterator);
    }
    return super.makeNode(paramDTMAxisIterator);
  }
  
  public NodeList makeNodeList(int paramInt)
  {
    if (_dom != null) {
      return _dom.makeNodeList(paramInt);
    }
    return super.makeNodeList(paramInt);
  }
  
  public NodeList makeNodeList(DTMAxisIterator paramDTMAxisIterator)
  {
    if (_dom != null) {
      return _dom.makeNodeList(paramDTMAxisIterator);
    }
    return super.makeNodeList(paramDTMAxisIterator);
  }
  
  public String getLanguage(int paramInt)
  {
    if (_dom != null) {
      return _dom.getLanguage(paramInt);
    }
    return super.getLanguage(paramInt);
  }
  
  public int getSize()
  {
    if (_dom != null) {
      return _dom.getSize();
    }
    return super.getSize();
  }
  
  public String getDocumentURI(int paramInt)
  {
    if (_dom != null) {
      return _dom.getDocumentURI(paramInt);
    }
    return "adaptive_rtf" + _documentURIIndex++;
  }
  
  public void setFilter(StripFilter paramStripFilter)
  {
    if (_dom != null) {
      _dom.setFilter(paramStripFilter);
    } else {
      super.setFilter(paramStripFilter);
    }
  }
  
  public void setupMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt, String[] paramArrayOfString3)
  {
    if (_dom != null) {
      _dom.setupMapping(paramArrayOfString1, paramArrayOfString2, paramArrayOfInt, paramArrayOfString3);
    } else {
      super.setupMapping(paramArrayOfString1, paramArrayOfString2, paramArrayOfInt, paramArrayOfString3);
    }
  }
  
  public boolean isElement(int paramInt)
  {
    if (_dom != null) {
      return _dom.isElement(paramInt);
    }
    return super.isElement(paramInt);
  }
  
  public boolean isAttribute(int paramInt)
  {
    if (_dom != null) {
      return _dom.isAttribute(paramInt);
    }
    return super.isAttribute(paramInt);
  }
  
  public String lookupNamespace(int paramInt, String paramString)
    throws TransletException
  {
    if (_dom != null) {
      return _dom.lookupNamespace(paramInt, paramString);
    }
    return super.lookupNamespace(paramInt, paramString);
  }
  
  public final int getNodeIdent(int paramInt)
  {
    if (_dom != null) {
      return _dom.getNodeIdent(paramInt);
    }
    return super.getNodeIdent(paramInt);
  }
  
  public final int getNodeHandle(int paramInt)
  {
    if (_dom != null) {
      return _dom.getNodeHandle(paramInt);
    }
    return super.getNodeHandle(paramInt);
  }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2)
  {
    if (_dom != null) {
      return _dom.getResultTreeFrag(paramInt1, paramInt2);
    }
    return super.getResultTreeFrag(paramInt1, paramInt2);
  }
  
  public SerializationHandler getOutputDomBuilder()
  {
    return this;
  }
  
  public int getNSType(int paramInt)
  {
    if (_dom != null) {
      return _dom.getNSType(paramInt);
    }
    return super.getNSType(paramInt);
  }
  
  public String getUnparsedEntityURI(String paramString)
  {
    if (_dom != null) {
      return _dom.getUnparsedEntityURI(paramString);
    }
    return super.getUnparsedEntityURI(paramString);
  }
  
  public Map<String, Integer> getElementsWithIDs()
  {
    if (_dom != null) {
      return _dom.getElementsWithIDs();
    }
    return super.getElementsWithIDs();
  }
  
  private void maybeEmitStartElement()
    throws SAXException
  {
    if (_openElementName != null)
    {
      int i;
      if ((i = _openElementName.indexOf(":")) < 0)
      {
        _dom.startElement(null, _openElementName, _openElementName, _attributes);
      }
      else
      {
        String str = _dom.getNamespaceURI(_openElementName.substring(0, i));
        _dom.startElement(str, _openElementName.substring(i + 1), _openElementName, _attributes);
      }
      _openElementName = null;
    }
  }
  
  private void prepareNewDOM()
    throws SAXException
  {
    _dom = ((SAXImpl)_dtmManager.getDTM(null, true, _wsfilter, true, false, false, _initSize, _buildIdIndex));
    _dom.startDocument();
    for (int i = 0; i < _size; i++)
    {
      String str = _textArray[i];
      _dom.characters(str.toCharArray(), 0, str.length());
    }
    _size = 0;
  }
  
  public void startDocument()
    throws SAXException
  {}
  
  public void endDocument()
    throws SAXException
  {
    if (_dom != null) {
      _dom.endDocument();
    } else {
      super.endDocument();
    }
  }
  
  public void characters(String paramString)
    throws SAXException
  {
    if (_dom != null) {
      characters(paramString.toCharArray(), 0, paramString.length());
    } else {
      super.characters(paramString);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (_dom != null)
    {
      maybeEmitStartElement();
      _dom.characters(paramArrayOfChar, paramInt1, paramInt2);
    }
    else
    {
      super.characters(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public boolean setEscaping(boolean paramBoolean)
    throws SAXException
  {
    if (_dom != null) {
      return _dom.setEscaping(paramBoolean);
    }
    return super.setEscaping(paramBoolean);
  }
  
  public void startElement(String paramString)
    throws SAXException
  {
    if (_dom == null) {
      prepareNewDOM();
    }
    maybeEmitStartElement();
    _openElementName = paramString;
    _attributes.clear();
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    startElement(paramString3);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    startElement(paramString3);
  }
  
  public void endElement(String paramString)
    throws SAXException
  {
    maybeEmitStartElement();
    _dom.endElement(null, null, paramString);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    endElement(paramString3);
  }
  
  public void addAttribute(String paramString1, String paramString2)
  {
    int i = paramString1.indexOf(":");
    String str1 = EMPTY_STRING;
    String str2 = paramString1;
    if (i > 0)
    {
      String str3 = paramString1.substring(0, i);
      str2 = paramString1.substring(i + 1);
      str1 = _dom.getNamespaceURI(str3);
    }
    addAttribute(str1, str2, paramString1, "CDATA", paramString2);
  }
  
  public void addUniqueAttribute(String paramString1, String paramString2, int paramInt)
    throws SAXException
  {
    addAttribute(paramString1, paramString2);
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    if (_openElementName != null) {
      _attributes.addAttribute(paramString1, paramString2, paramString3, paramString4, paramString5);
    } else {
      BasisLibrary.runTimeError("STRAY_ATTRIBUTE_ERR", paramString3);
    }
  }
  
  public void namespaceAfterStartElement(String paramString1, String paramString2)
    throws SAXException
  {
    if (_dom == null) {
      prepareNewDOM();
    }
    _dom.startPrefixMapping(paramString1, paramString2);
  }
  
  public void comment(String paramString)
    throws SAXException
  {
    if (_dom == null) {
      prepareNewDOM();
    }
    maybeEmitStartElement();
    char[] arrayOfChar = paramString.toCharArray();
    _dom.comment(arrayOfChar, 0, arrayOfChar.length);
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (_dom == null) {
      prepareNewDOM();
    }
    maybeEmitStartElement();
    _dom.comment(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (_dom == null) {
      prepareNewDOM();
    }
    maybeEmitStartElement();
    _dom.processingInstruction(paramString1, paramString2);
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
  {
    if (_dom != null) {
      _dom.setFeature(paramString, paramBoolean);
    }
  }
  
  public void setProperty(String paramString, Object paramObject)
  {
    if (_dom != null) {
      _dom.setProperty(paramString, paramObject);
    }
  }
  
  public DTMAxisTraverser getAxisTraverser(int paramInt)
  {
    if (_dom != null) {
      return _dom.getAxisTraverser(paramInt);
    }
    return super.getAxisTraverser(paramInt);
  }
  
  public boolean hasChildNodes(int paramInt)
  {
    if (_dom != null) {
      return _dom.hasChildNodes(paramInt);
    }
    return super.hasChildNodes(paramInt);
  }
  
  public int getFirstChild(int paramInt)
  {
    if (_dom != null) {
      return _dom.getFirstChild(paramInt);
    }
    return super.getFirstChild(paramInt);
  }
  
  public int getLastChild(int paramInt)
  {
    if (_dom != null) {
      return _dom.getLastChild(paramInt);
    }
    return super.getLastChild(paramInt);
  }
  
  public int getAttributeNode(int paramInt, String paramString1, String paramString2)
  {
    if (_dom != null) {
      return _dom.getAttributeNode(paramInt, paramString1, paramString2);
    }
    return super.getAttributeNode(paramInt, paramString1, paramString2);
  }
  
  public int getFirstAttribute(int paramInt)
  {
    if (_dom != null) {
      return _dom.getFirstAttribute(paramInt);
    }
    return super.getFirstAttribute(paramInt);
  }
  
  public int getFirstNamespaceNode(int paramInt, boolean paramBoolean)
  {
    if (_dom != null) {
      return _dom.getFirstNamespaceNode(paramInt, paramBoolean);
    }
    return super.getFirstNamespaceNode(paramInt, paramBoolean);
  }
  
  public int getNextSibling(int paramInt)
  {
    if (_dom != null) {
      return _dom.getNextSibling(paramInt);
    }
    return super.getNextSibling(paramInt);
  }
  
  public int getPreviousSibling(int paramInt)
  {
    if (_dom != null) {
      return _dom.getPreviousSibling(paramInt);
    }
    return super.getPreviousSibling(paramInt);
  }
  
  public int getNextAttribute(int paramInt)
  {
    if (_dom != null) {
      return _dom.getNextAttribute(paramInt);
    }
    return super.getNextAttribute(paramInt);
  }
  
  public int getNextNamespaceNode(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (_dom != null) {
      return _dom.getNextNamespaceNode(paramInt1, paramInt2, paramBoolean);
    }
    return super.getNextNamespaceNode(paramInt1, paramInt2, paramBoolean);
  }
  
  public int getOwnerDocument(int paramInt)
  {
    if (_dom != null) {
      return _dom.getOwnerDocument(paramInt);
    }
    return super.getOwnerDocument(paramInt);
  }
  
  public int getDocumentRoot(int paramInt)
  {
    if (_dom != null) {
      return _dom.getDocumentRoot(paramInt);
    }
    return super.getDocumentRoot(paramInt);
  }
  
  public XMLString getStringValue(int paramInt)
  {
    if (_dom != null) {
      return _dom.getStringValue(paramInt);
    }
    return super.getStringValue(paramInt);
  }
  
  public int getStringValueChunkCount(int paramInt)
  {
    if (_dom != null) {
      return _dom.getStringValueChunkCount(paramInt);
    }
    return super.getStringValueChunkCount(paramInt);
  }
  
  public char[] getStringValueChunk(int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    if (_dom != null) {
      return _dom.getStringValueChunk(paramInt1, paramInt2, paramArrayOfInt);
    }
    return super.getStringValueChunk(paramInt1, paramInt2, paramArrayOfInt);
  }
  
  public int getExpandedTypeID(String paramString1, String paramString2, int paramInt)
  {
    if (_dom != null) {
      return _dom.getExpandedTypeID(paramString1, paramString2, paramInt);
    }
    return super.getExpandedTypeID(paramString1, paramString2, paramInt);
  }
  
  public String getLocalNameFromExpandedNameID(int paramInt)
  {
    if (_dom != null) {
      return _dom.getLocalNameFromExpandedNameID(paramInt);
    }
    return super.getLocalNameFromExpandedNameID(paramInt);
  }
  
  public String getNamespaceFromExpandedNameID(int paramInt)
  {
    if (_dom != null) {
      return _dom.getNamespaceFromExpandedNameID(paramInt);
    }
    return super.getNamespaceFromExpandedNameID(paramInt);
  }
  
  public String getLocalName(int paramInt)
  {
    if (_dom != null) {
      return _dom.getLocalName(paramInt);
    }
    return super.getLocalName(paramInt);
  }
  
  public String getPrefix(int paramInt)
  {
    if (_dom != null) {
      return _dom.getPrefix(paramInt);
    }
    return super.getPrefix(paramInt);
  }
  
  public String getNamespaceURI(int paramInt)
  {
    if (_dom != null) {
      return _dom.getNamespaceURI(paramInt);
    }
    return super.getNamespaceURI(paramInt);
  }
  
  public String getNodeValue(int paramInt)
  {
    if (_dom != null) {
      return _dom.getNodeValue(paramInt);
    }
    return super.getNodeValue(paramInt);
  }
  
  public short getNodeType(int paramInt)
  {
    if (_dom != null) {
      return _dom.getNodeType(paramInt);
    }
    return super.getNodeType(paramInt);
  }
  
  public short getLevel(int paramInt)
  {
    if (_dom != null) {
      return _dom.getLevel(paramInt);
    }
    return super.getLevel(paramInt);
  }
  
  public boolean isSupported(String paramString1, String paramString2)
  {
    if (_dom != null) {
      return _dom.isSupported(paramString1, paramString2);
    }
    return super.isSupported(paramString1, paramString2);
  }
  
  public String getDocumentBaseURI()
  {
    if (_dom != null) {
      return _dom.getDocumentBaseURI();
    }
    return super.getDocumentBaseURI();
  }
  
  public void setDocumentBaseURI(String paramString)
  {
    if (_dom != null) {
      _dom.setDocumentBaseURI(paramString);
    } else {
      super.setDocumentBaseURI(paramString);
    }
  }
  
  public String getDocumentSystemIdentifier(int paramInt)
  {
    if (_dom != null) {
      return _dom.getDocumentSystemIdentifier(paramInt);
    }
    return super.getDocumentSystemIdentifier(paramInt);
  }
  
  public String getDocumentEncoding(int paramInt)
  {
    if (_dom != null) {
      return _dom.getDocumentEncoding(paramInt);
    }
    return super.getDocumentEncoding(paramInt);
  }
  
  public String getDocumentStandalone(int paramInt)
  {
    if (_dom != null) {
      return _dom.getDocumentStandalone(paramInt);
    }
    return super.getDocumentStandalone(paramInt);
  }
  
  public String getDocumentVersion(int paramInt)
  {
    if (_dom != null) {
      return _dom.getDocumentVersion(paramInt);
    }
    return super.getDocumentVersion(paramInt);
  }
  
  public boolean getDocumentAllDeclarationsProcessed()
  {
    if (_dom != null) {
      return _dom.getDocumentAllDeclarationsProcessed();
    }
    return super.getDocumentAllDeclarationsProcessed();
  }
  
  public String getDocumentTypeDeclarationSystemIdentifier()
  {
    if (_dom != null) {
      return _dom.getDocumentTypeDeclarationSystemIdentifier();
    }
    return super.getDocumentTypeDeclarationSystemIdentifier();
  }
  
  public String getDocumentTypeDeclarationPublicIdentifier()
  {
    if (_dom != null) {
      return _dom.getDocumentTypeDeclarationPublicIdentifier();
    }
    return super.getDocumentTypeDeclarationPublicIdentifier();
  }
  
  public int getElementById(String paramString)
  {
    if (_dom != null) {
      return _dom.getElementById(paramString);
    }
    return super.getElementById(paramString);
  }
  
  public boolean supportsPreStripping()
  {
    if (_dom != null) {
      return _dom.supportsPreStripping();
    }
    return super.supportsPreStripping();
  }
  
  public boolean isNodeAfter(int paramInt1, int paramInt2)
  {
    if (_dom != null) {
      return _dom.isNodeAfter(paramInt1, paramInt2);
    }
    return super.isNodeAfter(paramInt1, paramInt2);
  }
  
  public boolean isCharacterElementContentWhitespace(int paramInt)
  {
    if (_dom != null) {
      return _dom.isCharacterElementContentWhitespace(paramInt);
    }
    return super.isCharacterElementContentWhitespace(paramInt);
  }
  
  public boolean isDocumentAllDeclarationsProcessed(int paramInt)
  {
    if (_dom != null) {
      return _dom.isDocumentAllDeclarationsProcessed(paramInt);
    }
    return super.isDocumentAllDeclarationsProcessed(paramInt);
  }
  
  public boolean isAttributeSpecified(int paramInt)
  {
    if (_dom != null) {
      return _dom.isAttributeSpecified(paramInt);
    }
    return super.isAttributeSpecified(paramInt);
  }
  
  public void dispatchCharactersEvents(int paramInt, ContentHandler paramContentHandler, boolean paramBoolean)
    throws SAXException
  {
    if (_dom != null) {
      _dom.dispatchCharactersEvents(paramInt, paramContentHandler, paramBoolean);
    } else {
      super.dispatchCharactersEvents(paramInt, paramContentHandler, paramBoolean);
    }
  }
  
  public void dispatchToEvents(int paramInt, ContentHandler paramContentHandler)
    throws SAXException
  {
    if (_dom != null) {
      _dom.dispatchToEvents(paramInt, paramContentHandler);
    } else {
      super.dispatchToEvents(paramInt, paramContentHandler);
    }
  }
  
  public Node getNode(int paramInt)
  {
    if (_dom != null) {
      return _dom.getNode(paramInt);
    }
    return super.getNode(paramInt);
  }
  
  public boolean needsTwoThreads()
  {
    if (_dom != null) {
      return _dom.needsTwoThreads();
    }
    return super.needsTwoThreads();
  }
  
  public ContentHandler getContentHandler()
  {
    if (_dom != null) {
      return _dom.getContentHandler();
    }
    return super.getContentHandler();
  }
  
  public LexicalHandler getLexicalHandler()
  {
    if (_dom != null) {
      return _dom.getLexicalHandler();
    }
    return super.getLexicalHandler();
  }
  
  public EntityResolver getEntityResolver()
  {
    if (_dom != null) {
      return _dom.getEntityResolver();
    }
    return super.getEntityResolver();
  }
  
  public DTDHandler getDTDHandler()
  {
    if (_dom != null) {
      return _dom.getDTDHandler();
    }
    return super.getDTDHandler();
  }
  
  public ErrorHandler getErrorHandler()
  {
    if (_dom != null) {
      return _dom.getErrorHandler();
    }
    return super.getErrorHandler();
  }
  
  public DeclHandler getDeclHandler()
  {
    if (_dom != null) {
      return _dom.getDeclHandler();
    }
    return super.getDeclHandler();
  }
  
  public void appendChild(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (_dom != null) {
      _dom.appendChild(paramInt, paramBoolean1, paramBoolean2);
    } else {
      super.appendChild(paramInt, paramBoolean1, paramBoolean2);
    }
  }
  
  public void appendTextChild(String paramString)
  {
    if (_dom != null) {
      _dom.appendTextChild(paramString);
    } else {
      super.appendTextChild(paramString);
    }
  }
  
  public SourceLocator getSourceLocatorFor(int paramInt)
  {
    if (_dom != null) {
      return _dom.getSourceLocatorFor(paramInt);
    }
    return super.getSourceLocatorFor(paramInt);
  }
  
  public void documentRegistration()
  {
    if (_dom != null) {
      _dom.documentRegistration();
    } else {
      super.documentRegistration();
    }
  }
  
  public void documentRelease()
  {
    if (_dom != null) {
      _dom.documentRelease();
    } else {
      super.documentRelease();
    }
  }
  
  public void release()
  {
    if (_dom != null)
    {
      _dom.release();
      _dom = null;
    }
    super.release();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\AdaptiveResultTreeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */