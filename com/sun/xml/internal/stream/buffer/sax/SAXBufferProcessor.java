package com.sun.xml.internal.stream.buffer.sax;

import com.sun.xml.internal.stream.buffer.AbstractProcessor;
import com.sun.xml.internal.stream.buffer.AttributesHolder;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.LocatorImpl;

public class SAXBufferProcessor
  extends AbstractProcessor
  implements XMLReader
{
  protected EntityResolver _entityResolver = DEFAULT_LEXICAL_HANDLER;
  protected DTDHandler _dtdHandler = DEFAULT_LEXICAL_HANDLER;
  protected ContentHandler _contentHandler = DEFAULT_LEXICAL_HANDLER;
  protected ErrorHandler _errorHandler = DEFAULT_LEXICAL_HANDLER;
  protected LexicalHandler _lexicalHandler = DEFAULT_LEXICAL_HANDLER;
  protected boolean _namespacePrefixesFeature = false;
  protected AttributesHolder _attributes = new AttributesHolder();
  protected String[] _namespacePrefixes = new String[16];
  protected int _namespacePrefixesIndex;
  protected int[] _namespaceAttributesStartingStack = new int[16];
  protected int[] _namespaceAttributesStack = new int[16];
  protected int _namespaceAttributesStackIndex;
  private static final DefaultWithLexicalHandler DEFAULT_LEXICAL_HANDLER = new DefaultWithLexicalHandler();
  
  public SAXBufferProcessor() {}
  
  /**
   * @deprecated
   */
  public SAXBufferProcessor(XMLStreamBuffer paramXMLStreamBuffer)
  {
    setXMLStreamBuffer(paramXMLStreamBuffer);
  }
  
  public SAXBufferProcessor(XMLStreamBuffer paramXMLStreamBuffer, boolean paramBoolean)
  {
    setXMLStreamBuffer(paramXMLStreamBuffer, paramBoolean);
  }
  
  public boolean getFeature(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString.equals("http://xml.org/sax/features/namespaces")) {
      return true;
    }
    if (paramString.equals("http://xml.org/sax/features/namespace-prefixes")) {
      return _namespacePrefixesFeature;
    }
    if (paramString.equals("http://xml.org/sax/features/external-general-entities")) {
      return true;
    }
    if (paramString.equals("http://xml.org/sax/features/external-parameter-entities")) {
      return true;
    }
    if (paramString.equals("http://xml.org/sax/features/string-interning")) {
      return _stringInterningFeature;
    }
    throw new SAXNotRecognizedException("Feature not supported: " + paramString);
  }
  
  public void setFeature(String paramString, boolean paramBoolean)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString.equals("http://xml.org/sax/features/namespaces"))
    {
      if (!paramBoolean) {
        throw new SAXNotSupportedException(paramString + ":" + paramBoolean);
      }
    }
    else if (paramString.equals("http://xml.org/sax/features/namespace-prefixes")) {
      _namespacePrefixesFeature = paramBoolean;
    } else if ((!paramString.equals("http://xml.org/sax/features/external-general-entities")) && (!paramString.equals("http://xml.org/sax/features/external-parameter-entities"))) {
      if (paramString.equals("http://xml.org/sax/features/string-interning"))
      {
        if (paramBoolean != _stringInterningFeature) {
          throw new SAXNotSupportedException(paramString + ":" + paramBoolean);
        }
      }
      else {
        throw new SAXNotRecognizedException("Feature not supported: " + paramString);
      }
    }
  }
  
  public Object getProperty(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString.equals("http://xml.org/sax/properties/lexical-handler")) {
      return getLexicalHandler();
    }
    throw new SAXNotRecognizedException("Property not recognized: " + paramString);
  }
  
  public void setProperty(String paramString, Object paramObject)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString.equals("http://xml.org/sax/properties/lexical-handler"))
    {
      if ((paramObject instanceof LexicalHandler)) {
        setLexicalHandler((LexicalHandler)paramObject);
      } else {
        throw new SAXNotSupportedException("http://xml.org/sax/properties/lexical-handler");
      }
    }
    else {
      throw new SAXNotRecognizedException("Property not recognized: " + paramString);
    }
  }
  
  public void setEntityResolver(EntityResolver paramEntityResolver)
  {
    _entityResolver = paramEntityResolver;
  }
  
  public EntityResolver getEntityResolver()
  {
    return _entityResolver;
  }
  
  public void setDTDHandler(DTDHandler paramDTDHandler)
  {
    _dtdHandler = paramDTDHandler;
  }
  
  public DTDHandler getDTDHandler()
  {
    return _dtdHandler;
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
  {
    _contentHandler = paramContentHandler;
  }
  
  public ContentHandler getContentHandler()
  {
    return _contentHandler;
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler)
  {
    _errorHandler = paramErrorHandler;
  }
  
  public ErrorHandler getErrorHandler()
  {
    return _errorHandler;
  }
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler)
  {
    _lexicalHandler = paramLexicalHandler;
  }
  
  public LexicalHandler getLexicalHandler()
  {
    return _lexicalHandler;
  }
  
  public void parse(InputSource paramInputSource)
    throws IOException, SAXException
  {
    process();
  }
  
  public void parse(String paramString)
    throws IOException, SAXException
  {
    process();
  }
  
  /**
   * @deprecated
   */
  public final void process(XMLStreamBuffer paramXMLStreamBuffer)
    throws SAXException
  {
    setXMLStreamBuffer(paramXMLStreamBuffer);
    process();
  }
  
  public final void process(XMLStreamBuffer paramXMLStreamBuffer, boolean paramBoolean)
    throws SAXException
  {
    setXMLStreamBuffer(paramXMLStreamBuffer);
    process();
  }
  
  /**
   * @deprecated
   */
  public void setXMLStreamBuffer(XMLStreamBuffer paramXMLStreamBuffer)
  {
    setBuffer(paramXMLStreamBuffer);
  }
  
  public void setXMLStreamBuffer(XMLStreamBuffer paramXMLStreamBuffer, boolean paramBoolean)
  {
    if ((!paramBoolean) && (_treeCount > 1)) {
      throw new IllegalStateException("Can't write a forest to a full XML infoset");
    }
    setBuffer(paramXMLStreamBuffer, paramBoolean);
  }
  
  public final void process()
    throws SAXException
  {
    if (!_fragmentMode)
    {
      LocatorImpl localLocatorImpl = new LocatorImpl();
      localLocatorImpl.setSystemId(_buffer.getSystemId());
      localLocatorImpl.setLineNumber(-1);
      localLocatorImpl.setColumnNumber(-1);
      _contentHandler.setDocumentLocator(localLocatorImpl);
      _contentHandler.startDocument();
    }
    while (_treeCount > 0)
    {
      int i = readEiiState();
      String str1;
      String str2;
      switch (i)
      {
      case 1: 
        processDocument();
        _treeCount -= 1;
        break;
      case 17: 
        return;
      case 3: 
        processElement(readStructureString(), readStructureString(), readStructureString(), isInscope());
        _treeCount -= 1;
        break;
      case 4: 
        str1 = readStructureString();
        str2 = readStructureString();
        String str3 = readStructureString();
        processElement(str2, str3, getQName(str1, str3), isInscope());
        _treeCount -= 1;
        break;
      case 5: 
        str1 = readStructureString();
        str2 = readStructureString();
        processElement(str1, str2, str2, isInscope());
        _treeCount -= 1;
        break;
      case 6: 
        str1 = readStructureString();
        processElement("", str1, str1, isInscope());
        _treeCount -= 1;
        break;
      case 12: 
        processCommentAsCharArraySmall();
        break;
      case 13: 
        processCommentAsCharArrayMedium();
        break;
      case 14: 
        processCommentAsCharArrayCopy();
        break;
      case 15: 
        processComment(readContentString());
        break;
      case 16: 
        processProcessingInstruction(readStructureString(), readStructureString());
        break;
      case 2: 
      case 7: 
      case 8: 
      case 9: 
      case 10: 
      case 11: 
      default: 
        throw reportFatalError("Illegal state for DIIs: " + i);
      }
    }
    if (!_fragmentMode) {
      _contentHandler.endDocument();
    }
  }
  
  private void processCommentAsCharArraySmall()
    throws SAXException
  {
    int i = readStructure();
    int j = readContentCharactersBuffer(i);
    processComment(_contentCharactersBuffer, j, i);
  }
  
  private SAXParseException reportFatalError(String paramString)
    throws SAXException
  {
    SAXParseException localSAXParseException = new SAXParseException(paramString, null);
    if (_errorHandler != null) {
      _errorHandler.fatalError(localSAXParseException);
    }
    return localSAXParseException;
  }
  
  private boolean isInscope()
  {
    return _buffer.getInscopeNamespaces().size() > 0;
  }
  
  private void processDocument()
    throws SAXException
  {
    for (;;)
    {
      int i = readEiiState();
      String str1;
      String str2;
      switch (i)
      {
      case 3: 
        processElement(readStructureString(), readStructureString(), readStructureString(), isInscope());
        break;
      case 4: 
        str1 = readStructureString();
        str2 = readStructureString();
        String str3 = readStructureString();
        processElement(str2, str3, getQName(str1, str3), isInscope());
        break;
      case 5: 
        str1 = readStructureString();
        str2 = readStructureString();
        processElement(str1, str2, str2, isInscope());
        break;
      case 6: 
        str1 = readStructureString();
        processElement("", str1, str1, isInscope());
        break;
      case 12: 
        processCommentAsCharArraySmall();
        break;
      case 13: 
        processCommentAsCharArrayMedium();
        break;
      case 14: 
        processCommentAsCharArrayCopy();
        break;
      case 15: 
        processComment(readContentString());
        break;
      case 16: 
        processProcessingInstruction(readStructureString(), readStructureString());
        break;
      case 17: 
        return;
      case 7: 
      case 8: 
      case 9: 
      case 10: 
      case 11: 
      default: 
        throw reportFatalError("Illegal state for child of DII: " + i);
      }
    }
  }
  
  protected void processElement(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
    throws SAXException
  {
    int i = 0;
    int j = 0;
    int k = peekStructure();
    Set localSet = paramBoolean ? new HashSet() : Collections.emptySet();
    if ((k & 0xF0) == 64)
    {
      cacheNamespacePrefixStartingIndex();
      j = 1;
      k = processNamespaceAttributes(k, paramBoolean, localSet);
    }
    if (paramBoolean) {
      readInscopeNamespaces(localSet);
    }
    if ((k & 0xF0) == 48)
    {
      i = 1;
      processAttributes(k);
    }
    _contentHandler.startElement(paramString1, paramString2, paramString3, _attributes);
    if (i != 0) {
      _attributes.clear();
    }
    do
    {
      k = readEiiState();
      String str1;
      String str2;
      int m;
      int n;
      Object localObject;
      switch (k)
      {
      case 3: 
        processElement(readStructureString(), readStructureString(), readStructureString(), false);
        break;
      case 4: 
        str1 = readStructureString();
        str2 = readStructureString();
        String str4 = readStructureString();
        processElement(str2, str4, getQName(str1, str4), false);
        break;
      case 5: 
        str1 = readStructureString();
        str2 = readStructureString();
        processElement(str1, str2, str2, false);
        break;
      case 6: 
        str1 = readStructureString();
        processElement("", str1, str1, false);
        break;
      case 7: 
        m = readStructure();
        n = readContentCharactersBuffer(m);
        _contentHandler.characters(_contentCharactersBuffer, n, m);
        break;
      case 8: 
        m = readStructure16();
        n = readContentCharactersBuffer(m);
        _contentHandler.characters(_contentCharactersBuffer, n, m);
        break;
      case 9: 
        localObject = readContentCharactersCopy();
        _contentHandler.characters((char[])localObject, 0, localObject.length);
        break;
      case 10: 
        localObject = readContentString();
        _contentHandler.characters(((String)localObject).toCharArray(), 0, ((String)localObject).length());
        break;
      case 11: 
        localObject = (CharSequence)readContentObject();
        String str3 = ((CharSequence)localObject).toString();
        _contentHandler.characters(str3.toCharArray(), 0, str3.length());
        break;
      case 12: 
        processCommentAsCharArraySmall();
        break;
      case 13: 
        processCommentAsCharArrayMedium();
        break;
      case 14: 
        processCommentAsCharArrayCopy();
        break;
      case 104: 
        processComment(readContentString());
        break;
      case 16: 
        processProcessingInstruction(readStructureString(), readStructureString());
        break;
      case 17: 
        break;
      default: 
        throw reportFatalError("Illegal state for child of EII: " + k);
      }
    } while (k != 17);
    _contentHandler.endElement(paramString1, paramString2, paramString3);
    if (j != 0) {
      processEndPrefixMapping();
    }
  }
  
  private void readInscopeNamespaces(Set<String> paramSet)
    throws SAXException
  {
    Iterator localIterator = _buffer.getInscopeNamespaces().entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = fixNull((String)localEntry.getKey());
      if (!paramSet.contains(str)) {
        processNamespaceAttribute(str, (String)localEntry.getValue());
      }
    }
  }
  
  private static String fixNull(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    return paramString;
  }
  
  private void processCommentAsCharArrayCopy()
    throws SAXException
  {
    char[] arrayOfChar = readContentCharactersCopy();
    processComment(arrayOfChar, 0, arrayOfChar.length);
  }
  
  private void processCommentAsCharArrayMedium()
    throws SAXException
  {
    int i = readStructure16();
    int j = readContentCharactersBuffer(i);
    processComment(_contentCharactersBuffer, j, i);
  }
  
  private void processEndPrefixMapping()
    throws SAXException
  {
    int i = _namespaceAttributesStack[(--_namespaceAttributesStackIndex)];
    int j = _namespaceAttributesStackIndex >= 0 ? _namespaceAttributesStartingStack[_namespaceAttributesStackIndex] : 0;
    for (int k = i - 1; k >= j; k--) {
      _contentHandler.endPrefixMapping(_namespacePrefixes[k]);
    }
    _namespacePrefixesIndex = j;
  }
  
  private int processNamespaceAttributes(int paramInt, boolean paramBoolean, Set<String> paramSet)
    throws SAXException
  {
    do
    {
      String str;
      switch (getNIIState(paramInt))
      {
      case 1: 
        processNamespaceAttribute("", "");
        if (paramBoolean) {
          paramSet.add("");
        }
        break;
      case 2: 
        str = readStructureString();
        processNamespaceAttribute(str, "");
        if (paramBoolean) {
          paramSet.add(str);
        }
        break;
      case 3: 
        str = readStructureString();
        processNamespaceAttribute(str, readStructureString());
        if (paramBoolean) {
          paramSet.add(str);
        }
        break;
      case 4: 
        processNamespaceAttribute("", readStructureString());
        if (paramBoolean) {
          paramSet.add("");
        }
        break;
      default: 
        throw reportFatalError("Illegal state: " + paramInt);
      }
      readStructure();
      paramInt = peekStructure();
    } while ((paramInt & 0xF0) == 64);
    cacheNamespacePrefixIndex();
    return paramInt;
  }
  
  private void processAttributes(int paramInt)
    throws SAXException
  {
    do
    {
      String str1;
      String str2;
      switch (getAIIState(paramInt))
      {
      case 1: 
        _attributes.addAttributeWithQName(readStructureString(), readStructureString(), readStructureString(), readStructureString(), readContentString());
        break;
      case 2: 
        str1 = readStructureString();
        str2 = readStructureString();
        String str3 = readStructureString();
        _attributes.addAttributeWithQName(str2, str3, getQName(str1, str3), readStructureString(), readContentString());
        break;
      case 3: 
        str1 = readStructureString();
        str2 = readStructureString();
        _attributes.addAttributeWithQName(str1, str2, str2, readStructureString(), readContentString());
        break;
      case 4: 
        str1 = readStructureString();
        _attributes.addAttributeWithQName("", str1, str1, readStructureString(), readContentString());
        break;
      default: 
        throw reportFatalError("Illegal state: " + paramInt);
      }
      readStructure();
      paramInt = peekStructure();
    } while ((paramInt & 0xF0) == 48);
  }
  
  private void processNamespaceAttribute(String paramString1, String paramString2)
    throws SAXException
  {
    _contentHandler.startPrefixMapping(paramString1, paramString2);
    if (_namespacePrefixesFeature) {
      if (paramString1 != "") {
        _attributes.addAttributeWithQName("http://www.w3.org/2000/xmlns/", paramString1, getQName("xmlns", paramString1), "CDATA", paramString2);
      } else {
        _attributes.addAttributeWithQName("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns", "CDATA", paramString2);
      }
    }
    cacheNamespacePrefix(paramString1);
  }
  
  private void cacheNamespacePrefix(String paramString)
  {
    if (_namespacePrefixesIndex == _namespacePrefixes.length)
    {
      String[] arrayOfString = new String[_namespacePrefixesIndex * 3 / 2 + 1];
      System.arraycopy(_namespacePrefixes, 0, arrayOfString, 0, _namespacePrefixesIndex);
      _namespacePrefixes = arrayOfString;
    }
    _namespacePrefixes[(_namespacePrefixesIndex++)] = paramString;
  }
  
  private void cacheNamespacePrefixIndex()
  {
    if (_namespaceAttributesStackIndex == _namespaceAttributesStack.length)
    {
      int[] arrayOfInt = new int[_namespaceAttributesStackIndex * 3 / 2 + 1];
      System.arraycopy(_namespaceAttributesStack, 0, arrayOfInt, 0, _namespaceAttributesStackIndex);
      _namespaceAttributesStack = arrayOfInt;
    }
    _namespaceAttributesStack[(_namespaceAttributesStackIndex++)] = _namespacePrefixesIndex;
  }
  
  private void cacheNamespacePrefixStartingIndex()
  {
    if (_namespaceAttributesStackIndex == _namespaceAttributesStartingStack.length)
    {
      int[] arrayOfInt = new int[_namespaceAttributesStackIndex * 3 / 2 + 1];
      System.arraycopy(_namespaceAttributesStartingStack, 0, arrayOfInt, 0, _namespaceAttributesStackIndex);
      _namespaceAttributesStartingStack = arrayOfInt;
    }
    _namespaceAttributesStartingStack[_namespaceAttributesStackIndex] = _namespacePrefixesIndex;
  }
  
  private void processComment(String paramString)
    throws SAXException
  {
    processComment(paramString.toCharArray(), 0, paramString.length());
  }
  
  private void processComment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    _lexicalHandler.comment(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  private void processProcessingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    _contentHandler.processingInstruction(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\buffer\sax\SAXBufferProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */