package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx.Binding;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.internal.stream.buffer.AbstractProcessor;
import com.sun.xml.internal.stream.buffer.AttributesHolder;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferMark;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public class StreamReaderBufferProcessor
  extends AbstractProcessor
  implements XMLStreamReaderEx
{
  private static final int CACHE_SIZE = 16;
  protected ElementStackEntry[] _stack = new ElementStackEntry[16];
  protected ElementStackEntry _stackTop;
  protected int _depth;
  protected String[] _namespaceAIIsPrefix = new String[16];
  protected String[] _namespaceAIIsNamespaceName = new String[16];
  protected int _namespaceAIIsEnd;
  protected InternalNamespaceContext _nsCtx = new InternalNamespaceContext(null);
  protected int _eventType;
  protected AttributesHolder _attributeCache;
  protected CharSequence _charSequence;
  protected char[] _characters;
  protected int _textOffset;
  protected int _textLen;
  protected String _piTarget;
  protected String _piData;
  private static final int PARSING = 1;
  private static final int PENDING_END_DOCUMENT = 2;
  private static final int COMPLETED = 3;
  private int _completionState;
  
  public StreamReaderBufferProcessor()
  {
    for (int i = 0; i < _stack.length; i++) {
      _stack[i] = new ElementStackEntry(null);
    }
    _attributeCache = new AttributesHolder();
  }
  
  public StreamReaderBufferProcessor(XMLStreamBuffer paramXMLStreamBuffer)
    throws XMLStreamException
  {
    this();
    setXMLStreamBuffer(paramXMLStreamBuffer);
  }
  
  public void setXMLStreamBuffer(XMLStreamBuffer paramXMLStreamBuffer)
    throws XMLStreamException
  {
    setBuffer(paramXMLStreamBuffer, paramXMLStreamBuffer.isFragment());
    _completionState = 1;
    _namespaceAIIsEnd = 0;
    _characters = null;
    _charSequence = null;
    _eventType = 7;
  }
  
  public XMLStreamBuffer nextTagAndMark()
    throws XMLStreamException
  {
    for (;;)
    {
      int i = peekStructure();
      Object localObject;
      if ((i & 0xF0) == 32)
      {
        localObject = new HashMap(_namespaceAIIsEnd);
        for (int j = 0; j < _namespaceAIIsEnd; j++) {
          ((Map)localObject).put(_namespaceAIIsPrefix[j], _namespaceAIIsNamespaceName[j]);
        }
        XMLStreamBufferMark localXMLStreamBufferMark = new XMLStreamBufferMark((Map)localObject, this);
        next();
        return localXMLStreamBufferMark;
      }
      if ((i & 0xF0) == 16)
      {
        readStructure();
        localObject = new XMLStreamBufferMark(new HashMap(_namespaceAIIsEnd), this);
        next();
        return (XMLStreamBuffer)localObject;
      }
      if (next() == 2) {
        return null;
      }
    }
  }
  
  public Object getProperty(String paramString)
  {
    return null;
  }
  
  public int next()
    throws XMLStreamException
  {
    switch (_completionState)
    {
    case 3: 
      throw new XMLStreamException("Invalid State");
    case 2: 
      _namespaceAIIsEnd = 0;
      _completionState = 3;
      return _eventType = 8;
    }
    switch (_eventType)
    {
    case 2: 
      if (_depth > 1)
      {
        _depth -= 1;
        popElementStack(_depth);
      }
      else if (_depth == 1)
      {
        _depth -= 1;
      }
      break;
    }
    _characters = null;
    _charSequence = null;
    int i;
    for (;;)
    {
      i = readEiiState();
      switch (i)
      {
      }
    }
    String str1 = readStructureString();
    String str2 = readStructureString();
    String str3 = getPrefixFromQName(readStructureString());
    processElement(str3, str1, str2, isInscope(_depth));
    return _eventType = 1;
    processElement(readStructureString(), readStructureString(), readStructureString(), isInscope(_depth));
    return _eventType = 1;
    processElement(null, readStructureString(), readStructureString(), isInscope(_depth));
    return _eventType = 1;
    processElement(null, null, readStructureString(), isInscope(_depth));
    return _eventType = 1;
    _textLen = readStructure();
    _textOffset = readContentCharactersBuffer(_textLen);
    _characters = _contentCharactersBuffer;
    return _eventType = 4;
    _textLen = readStructure16();
    _textOffset = readContentCharactersBuffer(_textLen);
    _characters = _contentCharactersBuffer;
    return _eventType = 4;
    _characters = readContentCharactersCopy();
    _textLen = _characters.length;
    _textOffset = 0;
    return _eventType = 4;
    _eventType = 4;
    _charSequence = readContentString();
    return _eventType = 4;
    _eventType = 4;
    _charSequence = ((CharSequence)readContentObject());
    return _eventType = 4;
    _textLen = readStructure();
    _textOffset = readContentCharactersBuffer(_textLen);
    _characters = _contentCharactersBuffer;
    return _eventType = 5;
    _textLen = readStructure16();
    _textOffset = readContentCharactersBuffer(_textLen);
    _characters = _contentCharactersBuffer;
    return _eventType = 5;
    _characters = readContentCharactersCopy();
    _textLen = _characters.length;
    _textOffset = 0;
    return _eventType = 5;
    _charSequence = readContentString();
    return _eventType = 5;
    _piTarget = readStructureString();
    _piData = readStructureString();
    return _eventType = 3;
    if (_depth > 1) {
      return _eventType = 2;
    }
    if (_depth == 1)
    {
      if ((_fragmentMode) && (--_treeCount == 0)) {
        _completionState = 2;
      }
      return _eventType = 2;
    }
    _namespaceAIIsEnd = 0;
    _completionState = 3;
    return _eventType = 8;
    throw new XMLStreamException("Internal XSB error: Invalid State=" + i);
  }
  
  public final void require(int paramInt, String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (paramInt != _eventType) {
      throw new XMLStreamException("");
    }
    if ((paramString1 != null) && (!paramString1.equals(getNamespaceURI()))) {
      throw new XMLStreamException("");
    }
    if ((paramString2 != null) && (!paramString2.equals(getLocalName()))) {
      throw new XMLStreamException("");
    }
  }
  
  public final String getElementTextTrim()
    throws XMLStreamException
  {
    return getElementText().trim();
  }
  
  public final String getElementText()
    throws XMLStreamException
  {
    if (_eventType != 1) {
      throw new XMLStreamException("");
    }
    next();
    return getElementText(true);
  }
  
  public final String getElementText(boolean paramBoolean)
    throws XMLStreamException
  {
    if (!paramBoolean) {
      throw new XMLStreamException("");
    }
    int i = getEventType();
    StringBuilder localStringBuilder = new StringBuilder();
    while (i != 2)
    {
      if ((i == 4) || (i == 12) || (i == 6) || (i == 9))
      {
        localStringBuilder.append(getText());
      }
      else if ((i != 3) && (i != 5))
      {
        if (i == 8) {
          throw new XMLStreamException("");
        }
        if (i == 1) {
          throw new XMLStreamException("");
        }
        throw new XMLStreamException("");
      }
      i = next();
    }
    return localStringBuilder.toString();
  }
  
  public final int nextTag()
    throws XMLStreamException
  {
    next();
    return nextTag(true);
  }
  
  public final int nextTag(boolean paramBoolean)
    throws XMLStreamException
  {
    int i = getEventType();
    if (!paramBoolean) {}
    for (i = next(); ((i == 4) && (isWhiteSpace())) || ((i == 12) && (isWhiteSpace())) || (i == 6) || (i == 3) || (i == 5); i = next()) {}
    if ((i != 1) && (i != 2)) {
      throw new XMLStreamException("");
    }
    return i;
  }
  
  public final boolean hasNext()
  {
    return _eventType != 8;
  }
  
  public void close()
    throws XMLStreamException
  {}
  
  public final boolean isStartElement()
  {
    return _eventType == 1;
  }
  
  public final boolean isEndElement()
  {
    return _eventType == 2;
  }
  
  public final boolean isCharacters()
  {
    return _eventType == 4;
  }
  
  public final boolean isWhiteSpace()
  {
    if ((isCharacters()) || (_eventType == 12))
    {
      char[] arrayOfChar = getTextCharacters();
      int i = getTextStart();
      int j = getTextLength();
      for (int k = i; k < j; k++)
      {
        int m = arrayOfChar[k];
        if ((m != 32) && (m != 9) && (m != 13) && (m != 10)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public final String getAttributeValue(String paramString1, String paramString2)
  {
    if (_eventType != 1) {
      throw new IllegalStateException("");
    }
    if (paramString1 == null) {
      paramString1 = "";
    }
    return _attributeCache.getValue(paramString1, paramString2);
  }
  
  public final int getAttributeCount()
  {
    if (_eventType != 1) {
      throw new IllegalStateException("");
    }
    return _attributeCache.getLength();
  }
  
  public final QName getAttributeName(int paramInt)
  {
    if (_eventType != 1) {
      throw new IllegalStateException("");
    }
    String str1 = _attributeCache.getPrefix(paramInt);
    String str2 = _attributeCache.getLocalName(paramInt);
    String str3 = _attributeCache.getURI(paramInt);
    return new QName(str3, str2, str1);
  }
  
  public final String getAttributeNamespace(int paramInt)
  {
    if (_eventType != 1) {
      throw new IllegalStateException("");
    }
    return fixEmptyString(_attributeCache.getURI(paramInt));
  }
  
  public final String getAttributeLocalName(int paramInt)
  {
    if (_eventType != 1) {
      throw new IllegalStateException("");
    }
    return _attributeCache.getLocalName(paramInt);
  }
  
  public final String getAttributePrefix(int paramInt)
  {
    if (_eventType != 1) {
      throw new IllegalStateException("");
    }
    return fixEmptyString(_attributeCache.getPrefix(paramInt));
  }
  
  public final String getAttributeType(int paramInt)
  {
    if (_eventType != 1) {
      throw new IllegalStateException("");
    }
    return _attributeCache.getType(paramInt);
  }
  
  public final String getAttributeValue(int paramInt)
  {
    if (_eventType != 1) {
      throw new IllegalStateException("");
    }
    return _attributeCache.getValue(paramInt);
  }
  
  public final boolean isAttributeSpecified(int paramInt)
  {
    return false;
  }
  
  public final int getNamespaceCount()
  {
    if ((_eventType == 1) || (_eventType == 2)) {
      return _stackTop.namespaceAIIsEnd - _stackTop.namespaceAIIsStart;
    }
    throw new IllegalStateException("");
  }
  
  public final String getNamespacePrefix(int paramInt)
  {
    if ((_eventType == 1) || (_eventType == 2)) {
      return _namespaceAIIsPrefix[(_stackTop.namespaceAIIsStart + paramInt)];
    }
    throw new IllegalStateException("");
  }
  
  public final String getNamespaceURI(int paramInt)
  {
    if ((_eventType == 1) || (_eventType == 2)) {
      return _namespaceAIIsNamespaceName[(_stackTop.namespaceAIIsStart + paramInt)];
    }
    throw new IllegalStateException("");
  }
  
  public final String getNamespaceURI(String paramString)
  {
    return _nsCtx.getNamespaceURI(paramString);
  }
  
  public final NamespaceContextEx getNamespaceContext()
  {
    return _nsCtx;
  }
  
  public final int getEventType()
  {
    return _eventType;
  }
  
  public final String getText()
  {
    if (_characters != null)
    {
      String str = new String(_characters, _textOffset, _textLen);
      _charSequence = str;
      return str;
    }
    if (_charSequence != null) {
      return _charSequence.toString();
    }
    throw new IllegalStateException();
  }
  
  public final char[] getTextCharacters()
  {
    if (_characters != null) {
      return _characters;
    }
    if (_charSequence != null)
    {
      _characters = _charSequence.toString().toCharArray();
      _textLen = _characters.length;
      _textOffset = 0;
      return _characters;
    }
    throw new IllegalStateException();
  }
  
  public final int getTextStart()
  {
    if (_characters != null) {
      return _textOffset;
    }
    if (_charSequence != null) {
      return 0;
    }
    throw new IllegalStateException();
  }
  
  public final int getTextLength()
  {
    if (_characters != null) {
      return _textLen;
    }
    if (_charSequence != null) {
      return _charSequence.length();
    }
    throw new IllegalStateException();
  }
  
  public final int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
    throws XMLStreamException
  {
    if (_characters == null) {
      if (_charSequence != null)
      {
        _characters = _charSequence.toString().toCharArray();
        _textLen = _characters.length;
        _textOffset = 0;
      }
      else
      {
        throw new IllegalStateException("");
      }
    }
    try
    {
      int i = _textLen - paramInt1;
      int j = i > paramInt3 ? paramInt3 : i;
      paramInt1 += _textOffset;
      System.arraycopy(_characters, paramInt1, paramArrayOfChar, paramInt2, j);
      return j;
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new XMLStreamException(localIndexOutOfBoundsException);
    }
  }
  
  public final CharSequence getPCDATA()
  {
    if (_characters != null) {
      return new CharSequenceImpl(_textOffset, _textLen);
    }
    if (_charSequence != null) {
      return _charSequence;
    }
    throw new IllegalStateException();
  }
  
  public final String getEncoding()
  {
    return "UTF-8";
  }
  
  public final boolean hasText()
  {
    return (_characters != null) || (_charSequence != null);
  }
  
  public final Location getLocation()
  {
    return new DummyLocation(null);
  }
  
  public final boolean hasName()
  {
    return (_eventType == 1) || (_eventType == 2);
  }
  
  public final QName getName()
  {
    return _stackTop.getQName();
  }
  
  public final String getLocalName()
  {
    return _stackTop.localName;
  }
  
  public final String getNamespaceURI()
  {
    return _stackTop.uri;
  }
  
  public final String getPrefix()
  {
    return _stackTop.prefix;
  }
  
  public final String getVersion()
  {
    return "1.0";
  }
  
  public final boolean isStandalone()
  {
    return false;
  }
  
  public final boolean standaloneSet()
  {
    return false;
  }
  
  public final String getCharacterEncodingScheme()
  {
    return "UTF-8";
  }
  
  public final String getPITarget()
  {
    if (_eventType == 3) {
      return _piTarget;
    }
    throw new IllegalStateException("");
  }
  
  public final String getPIData()
  {
    if (_eventType == 3) {
      return _piData;
    }
    throw new IllegalStateException("");
  }
  
  protected void processElement(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    pushElementStack();
    _stackTop.set(paramString1, paramString2, paramString3);
    _attributeCache.clear();
    int i = peekStructure();
    if (((i & 0xF0) == 64) || (paramBoolean)) {
      i = processNamespaceAttributes(i, paramBoolean);
    }
    if ((i & 0xF0) == 48) {
      processAttributes(i);
    }
  }
  
  private boolean isInscope(int paramInt)
  {
    return (_buffer.getInscopeNamespaces().size() > 0) && (paramInt == 0);
  }
  
  private void resizeNamespaceAttributes()
  {
    String[] arrayOfString1 = new String[_namespaceAIIsEnd * 2];
    System.arraycopy(_namespaceAIIsPrefix, 0, arrayOfString1, 0, _namespaceAIIsEnd);
    _namespaceAIIsPrefix = arrayOfString1;
    String[] arrayOfString2 = new String[_namespaceAIIsEnd * 2];
    System.arraycopy(_namespaceAIIsNamespaceName, 0, arrayOfString2, 0, _namespaceAIIsEnd);
    _namespaceAIIsNamespaceName = arrayOfString2;
  }
  
  private int processNamespaceAttributes(int paramInt, boolean paramBoolean)
  {
    _stackTop.namespaceAIIsStart = _namespaceAIIsEnd;
    Set localSet = paramBoolean ? new HashSet() : Collections.emptySet();
    while ((paramInt & 0xF0) == 64)
    {
      if (_namespaceAIIsEnd == _namespaceAIIsPrefix.length) {
        resizeNamespaceAttributes();
      }
      switch (getNIIState(paramInt))
      {
      case 1: 
        _namespaceAIIsPrefix[_namespaceAIIsEnd] = (_namespaceAIIsNamespaceName[(_namespaceAIIsEnd++)] = "");
        if (paramBoolean) {
          localSet.add("");
        }
        break;
      case 2: 
        _namespaceAIIsPrefix[_namespaceAIIsEnd] = readStructureString();
        if (paramBoolean) {
          localSet.add(_namespaceAIIsPrefix[_namespaceAIIsEnd]);
        }
        _namespaceAIIsNamespaceName[(_namespaceAIIsEnd++)] = "";
        break;
      case 3: 
        _namespaceAIIsPrefix[_namespaceAIIsEnd] = readStructureString();
        if (paramBoolean) {
          localSet.add(_namespaceAIIsPrefix[_namespaceAIIsEnd]);
        }
        _namespaceAIIsNamespaceName[(_namespaceAIIsEnd++)] = readStructureString();
        break;
      case 4: 
        _namespaceAIIsPrefix[_namespaceAIIsEnd] = "";
        if (paramBoolean) {
          localSet.add("");
        }
        _namespaceAIIsNamespaceName[(_namespaceAIIsEnd++)] = readStructureString();
      }
      readStructure();
      paramInt = peekStructure();
    }
    if (paramBoolean)
    {
      Iterator localIterator = _buffer.getInscopeNamespaces().entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        String str = fixNull((String)localEntry.getKey());
        if (!localSet.contains(str))
        {
          if (_namespaceAIIsEnd == _namespaceAIIsPrefix.length) {
            resizeNamespaceAttributes();
          }
          _namespaceAIIsPrefix[_namespaceAIIsEnd] = str;
          _namespaceAIIsNamespaceName[(_namespaceAIIsEnd++)] = ((String)localEntry.getValue());
        }
      }
    }
    _stackTop.namespaceAIIsEnd = _namespaceAIIsEnd;
    return paramInt;
  }
  
  private static String fixNull(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    return paramString;
  }
  
  private void processAttributes(int paramInt)
  {
    do
    {
      switch (getAIIState(paramInt))
      {
      case 1: 
        String str1 = readStructureString();
        String str2 = readStructureString();
        String str3 = getPrefixFromQName(readStructureString());
        _attributeCache.addAttributeWithPrefix(str3, str1, str2, readStructureString(), readContentString());
        break;
      case 2: 
        _attributeCache.addAttributeWithPrefix(readStructureString(), readStructureString(), readStructureString(), readStructureString(), readContentString());
        break;
      case 3: 
        _attributeCache.addAttributeWithPrefix("", readStructureString(), readStructureString(), readStructureString(), readContentString());
        break;
      case 4: 
        _attributeCache.addAttributeWithPrefix("", "", readStructureString(), readStructureString(), readContentString());
        break;
      default: 
        if (!$assertionsDisabled) {
          throw new AssertionError("Internal XSB Error: wrong attribute state, Item=" + paramInt);
        }
        break;
      }
      readStructure();
      paramInt = peekStructure();
    } while ((paramInt & 0xF0) == 48);
  }
  
  private void pushElementStack()
  {
    if (_depth == _stack.length)
    {
      ElementStackEntry[] arrayOfElementStackEntry = _stack;
      _stack = new ElementStackEntry[_stack.length * 3 / 2 + 1];
      System.arraycopy(arrayOfElementStackEntry, 0, _stack, 0, arrayOfElementStackEntry.length);
      for (int i = arrayOfElementStackEntry.length; i < _stack.length; i++) {
        _stack[i] = new ElementStackEntry(null);
      }
    }
    _stackTop = _stack[(_depth++)];
  }
  
  private void popElementStack(int paramInt)
  {
    _stackTop = _stack[(paramInt - 1)];
    _namespaceAIIsEnd = _stack[paramInt].namespaceAIIsStart;
  }
  
  private static String fixEmptyString(String paramString)
  {
    if (paramString.length() == 0) {
      return null;
    }
    return paramString;
  }
  
  private class CharSequenceImpl
    implements CharSequence
  {
    private final int _offset;
    private final int _length;
    
    CharSequenceImpl(int paramInt1, int paramInt2)
    {
      _offset = paramInt1;
      _length = paramInt2;
    }
    
    public int length()
    {
      return _length;
    }
    
    public char charAt(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < _textLen)) {
        return _characters[(_textOffset + paramInt)];
      }
      throw new IndexOutOfBoundsException();
    }
    
    public CharSequence subSequence(int paramInt1, int paramInt2)
    {
      int i = paramInt2 - paramInt1;
      if ((paramInt2 < 0) || (paramInt1 < 0) || (paramInt2 > i) || (paramInt1 > paramInt2)) {
        throw new IndexOutOfBoundsException();
      }
      return new CharSequenceImpl(StreamReaderBufferProcessor.this, _offset + paramInt1, i);
    }
    
    public String toString()
    {
      return new String(_characters, _offset, _length);
    }
  }
  
  private class DummyLocation
    implements Location
  {
    private DummyLocation() {}
    
    public int getLineNumber()
    {
      return -1;
    }
    
    public int getColumnNumber()
    {
      return -1;
    }
    
    public int getCharacterOffset()
    {
      return -1;
    }
    
    public String getPublicId()
    {
      return null;
    }
    
    public String getSystemId()
    {
      return _buffer.getSystemId();
    }
  }
  
  private final class ElementStackEntry
  {
    String prefix;
    String uri;
    String localName;
    QName qname;
    int namespaceAIIsStart;
    int namespaceAIIsEnd;
    
    private ElementStackEntry() {}
    
    public void set(String paramString1, String paramString2, String paramString3)
    {
      prefix = paramString1;
      uri = paramString2;
      localName = paramString3;
      qname = null;
      namespaceAIIsStart = (namespaceAIIsEnd = _namespaceAIIsEnd);
    }
    
    public QName getQName()
    {
      if (qname == null) {
        qname = new QName(fixNull(uri), localName, fixNull(prefix));
      }
      return qname;
    }
    
    private String fixNull(String paramString)
    {
      return paramString == null ? "" : paramString;
    }
  }
  
  private final class InternalNamespaceContext
    implements NamespaceContextEx
  {
    private InternalNamespaceContext() {}
    
    public String getNamespaceURI(String paramString)
    {
      if (paramString == null) {
        throw new IllegalArgumentException("Prefix cannot be null");
      }
      int i;
      if (_stringInterningFeature)
      {
        paramString = paramString.intern();
        for (i = _namespaceAIIsEnd - 1; i >= 0; i--) {
          if (paramString == _namespaceAIIsPrefix[i]) {
            return _namespaceAIIsNamespaceName[i];
          }
        }
      }
      else
      {
        for (i = _namespaceAIIsEnd - 1; i >= 0; i--) {
          if (paramString.equals(_namespaceAIIsPrefix[i])) {
            return _namespaceAIIsNamespaceName[i];
          }
        }
      }
      if (paramString.equals("xml")) {
        return "http://www.w3.org/XML/1998/namespace";
      }
      if (paramString.equals("xmlns")) {
        return "http://www.w3.org/2000/xmlns/";
      }
      return null;
    }
    
    public String getPrefix(String paramString)
    {
      Iterator localIterator = getPrefixes(paramString);
      if (localIterator.hasNext()) {
        return (String)localIterator.next();
      }
      return null;
    }
    
    public Iterator getPrefixes(final String paramString)
    {
      if (paramString == null) {
        throw new IllegalArgumentException("NamespaceURI cannot be null");
      }
      if (paramString.equals("http://www.w3.org/XML/1998/namespace")) {
        return Collections.singletonList("xml").iterator();
      }
      if (paramString.equals("http://www.w3.org/2000/xmlns/")) {
        return Collections.singletonList("xmlns").iterator();
      }
      new Iterator()
      {
        private int i = _namespaceAIIsEnd - 1;
        private boolean requireFindNext = true;
        private String p;
        
        private String findNext()
        {
          while (i >= 0)
          {
            if ((paramString.equals(_namespaceAIIsNamespaceName[i])) && (getNamespaceURI(_namespaceAIIsPrefix[i]).equals(_namespaceAIIsNamespaceName[i]))) {
              return p = _namespaceAIIsPrefix[i];
            }
            i -= 1;
          }
          return p = null;
        }
        
        public boolean hasNext()
        {
          if (requireFindNext)
          {
            findNext();
            requireFindNext = false;
          }
          return p != null;
        }
        
        public Object next()
        {
          if (requireFindNext) {
            findNext();
          }
          requireFindNext = true;
          if (p == null) {
            throw new NoSuchElementException();
          }
          return p;
        }
        
        public void remove()
        {
          throw new UnsupportedOperationException();
        }
      };
    }
    
    public Iterator<NamespaceContextEx.Binding> iterator()
    {
      new Iterator()
      {
        private final int end = _namespaceAIIsEnd - 1;
        private int current = end;
        private boolean requireFindNext = true;
        private NamespaceContextEx.Binding namespace;
        
        private NamespaceContextEx.Binding findNext()
        {
          while (current >= 0)
          {
            String str = _namespaceAIIsPrefix[current];
            for (int i = end; (i > current) && (!str.equals(_namespaceAIIsPrefix[i])); i--) {}
            if (i == current--) {
              return namespace = new StreamReaderBufferProcessor.InternalNamespaceContext.BindingImpl(StreamReaderBufferProcessor.InternalNamespaceContext.this, str, _namespaceAIIsNamespaceName[current]);
            }
          }
          return namespace = null;
        }
        
        public boolean hasNext()
        {
          if (requireFindNext)
          {
            findNext();
            requireFindNext = false;
          }
          return namespace != null;
        }
        
        public NamespaceContextEx.Binding next()
        {
          if (requireFindNext) {
            findNext();
          }
          requireFindNext = true;
          if (namespace == null) {
            throw new NoSuchElementException();
          }
          return namespace;
        }
        
        public void remove()
        {
          throw new UnsupportedOperationException();
        }
      };
    }
    
    private class BindingImpl
      implements NamespaceContextEx.Binding
    {
      final String _prefix;
      final String _namespaceURI;
      
      BindingImpl(String paramString1, String paramString2)
      {
        _prefix = paramString1;
        _namespaceURI = paramString2;
      }
      
      public String getPrefix()
      {
        return _prefix;
      }
      
      public String getNamespaceURI()
      {
        return _namespaceURI;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\buffer\stax\StreamReaderBufferProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */