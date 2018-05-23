package com.sun.xml.internal.fastinfoset.stax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.DecoderStateTables;
import com.sun.xml.internal.fastinfoset.OctetBufferListener;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.algorithm.BASE64EncodingAlgorithm;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithm;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.fastinfoset.org.apache.xerces.util.XMLChar;
import com.sun.xml.internal.fastinfoset.sax.AttributesHolder;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.util.CharArrayArray;
import com.sun.xml.internal.fastinfoset.util.CharArrayString;
import com.sun.xml.internal.fastinfoset.util.ContiguousCharArrayArray;
import com.sun.xml.internal.fastinfoset.util.DuplicateAttributeVerifier;
import com.sun.xml.internal.fastinfoset.util.PrefixArray;
import com.sun.xml.internal.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.stax.FastInfosetStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StAXDocumentParser
  extends Decoder
  implements XMLStreamReader, FastInfosetStreamReader, OctetBufferListener
{
  private static final Logger logger = Logger.getLogger(StAXDocumentParser.class.getName());
  protected static final int INTERNAL_STATE_START_DOCUMENT = 0;
  protected static final int INTERNAL_STATE_START_ELEMENT_TERMINATE = 1;
  protected static final int INTERNAL_STATE_SINGLE_TERMINATE_ELEMENT_WITH_NAMESPACES = 2;
  protected static final int INTERNAL_STATE_DOUBLE_TERMINATE_ELEMENT = 3;
  protected static final int INTERNAL_STATE_END_DOCUMENT = 4;
  protected static final int INTERNAL_STATE_VOID = -1;
  protected int _internalState;
  protected int _eventType;
  protected QualifiedName[] _qNameStack = new QualifiedName[32];
  protected int[] _namespaceAIIsStartStack = new int[32];
  protected int[] _namespaceAIIsEndStack = new int[32];
  protected int _stackCount = -1;
  protected String[] _namespaceAIIsPrefix = new String[32];
  protected String[] _namespaceAIIsNamespaceName = new String[32];
  protected int[] _namespaceAIIsPrefixIndex = new int[32];
  protected int _namespaceAIIsIndex;
  protected int _currentNamespaceAIIsStart;
  protected int _currentNamespaceAIIsEnd;
  protected QualifiedName _qualifiedName;
  protected AttributesHolder _attributes = new AttributesHolder();
  protected boolean _clearAttributes = false;
  protected char[] _characters;
  protected int _charactersOffset;
  protected String _algorithmURI;
  protected int _algorithmId;
  protected boolean _isAlgorithmDataCloned;
  protected byte[] _algorithmData;
  protected int _algorithmDataOffset;
  protected int _algorithmDataLength;
  protected String _piTarget;
  protected String _piData;
  protected NamespaceContextImpl _nsContext = new NamespaceContextImpl();
  protected String _characterEncodingScheme;
  protected StAXManager _manager;
  private byte[] base64TaleBytes = new byte[3];
  private int base64TaleLength;
  
  public StAXDocumentParser()
  {
    reset();
    _manager = new StAXManager(1);
  }
  
  public StAXDocumentParser(InputStream paramInputStream)
  {
    this();
    setInputStream(paramInputStream);
    _manager = new StAXManager(1);
  }
  
  public StAXDocumentParser(InputStream paramInputStream, StAXManager paramStAXManager)
  {
    this(paramInputStream);
    _manager = paramStAXManager;
  }
  
  public void setInputStream(InputStream paramInputStream)
  {
    super.setInputStream(paramInputStream);
    reset();
  }
  
  public void reset()
  {
    super.reset();
    if ((_internalState != 0) && (_internalState != 4))
    {
      for (int i = _namespaceAIIsIndex - 1; i >= 0; i--) {
        _prefixTable.popScopeWithPrefixEntry(_namespaceAIIsPrefixIndex[i]);
      }
      _stackCount = -1;
      _namespaceAIIsIndex = 0;
      _characters = null;
      _algorithmData = null;
    }
    _characterEncodingScheme = "UTF-8";
    _eventType = 7;
    _internalState = 0;
  }
  
  protected void resetOnError()
  {
    super.reset();
    if (_v != null) {
      _prefixTable.clearCompletely();
    }
    _duplicateAttributeVerifier.clear();
    _stackCount = -1;
    _namespaceAIIsIndex = 0;
    _characters = null;
    _algorithmData = null;
    _eventType = 7;
    _internalState = 0;
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    if (_manager != null) {
      return _manager.getProperty(paramString);
    }
    return null;
  }
  
  public int next()
    throws XMLStreamException
  {
    try
    {
      if (_internalState != -1) {
        switch (_internalState)
        {
        case 0: 
          decodeHeader();
          processDII();
          _internalState = -1;
          break;
        case 1: 
          if (_currentNamespaceAIIsEnd > 0)
          {
            for (i = _currentNamespaceAIIsEnd - 1; i >= _currentNamespaceAIIsStart; i--) {
              _prefixTable.popScopeWithPrefixEntry(_namespaceAIIsPrefixIndex[i]);
            }
            _namespaceAIIsIndex = _currentNamespaceAIIsStart;
          }
          popStack();
          _internalState = -1;
          return _eventType = 2;
        case 2: 
          for (i = _currentNamespaceAIIsEnd - 1; i >= _currentNamespaceAIIsStart; i--) {
            _prefixTable.popScopeWithPrefixEntry(_namespaceAIIsPrefixIndex[i]);
          }
          _namespaceAIIsIndex = _currentNamespaceAIIsStart;
          _internalState = -1;
          break;
        case 3: 
          if (_currentNamespaceAIIsEnd > 0)
          {
            for (i = _currentNamespaceAIIsEnd - 1; i >= _currentNamespaceAIIsStart; i--) {
              _prefixTable.popScopeWithPrefixEntry(_namespaceAIIsPrefixIndex[i]);
            }
            _namespaceAIIsIndex = _currentNamespaceAIIsStart;
          }
          if (_stackCount == -1)
          {
            _internalState = 4;
            return _eventType = 8;
          }
          popStack();
          _internalState = (_currentNamespaceAIIsEnd > 0 ? 2 : -1);
          return _eventType = 2;
        case 4: 
          throw new NoSuchElementException(CommonResourceBundle.getInstance().getString("message.noMoreEvents"));
        }
      }
      _characters = null;
      _algorithmData = null;
      _currentNamespaceAIIsEnd = 0;
      int i = read();
      boolean bool;
      int k;
      int j;
      switch (DecoderStateTables.EII(i))
      {
      case 0: 
        processEII(_elementNameTable._array[i], false);
        return _eventType;
      case 1: 
        processEII(_elementNameTable._array[(i & 0x1F)], true);
        return _eventType;
      case 2: 
        processEII(processEIIIndexMedium(i), (i & 0x40) > 0);
        return _eventType;
      case 3: 
        processEII(processEIIIndexLarge(i), (i & 0x40) > 0);
        return _eventType;
      case 5: 
        QualifiedName localQualifiedName = processLiteralQualifiedName(i & 0x3, _elementNameTable.getNext());
        _elementNameTable.add(localQualifiedName);
        processEII(localQualifiedName, (i & 0x40) > 0);
        return _eventType;
      case 4: 
        processEIIWithNamespaces((i & 0x40) > 0);
        return _eventType;
      case 6: 
        _octetBufferLength = ((i & 0x1) + 1);
        processUtf8CharacterString(i);
        return _eventType = 4;
      case 7: 
        _octetBufferLength = (read() + 3);
        processUtf8CharacterString(i);
        return _eventType = 4;
      case 8: 
        _octetBufferLength = ((read() << 24 | read() << 16 | read() << 8 | read()) + 259);
        processUtf8CharacterString(i);
        return _eventType = 4;
      case 9: 
        _octetBufferLength = ((i & 0x1) + 1);
        processUtf16CharacterString(i);
        return _eventType = 4;
      case 10: 
        _octetBufferLength = (read() + 3);
        processUtf16CharacterString(i);
        return _eventType = 4;
      case 11: 
        _octetBufferLength = ((read() << 24 | read() << 16 | read() << 8 | read()) + 259);
        processUtf16CharacterString(i);
        return _eventType = 4;
      case 12: 
        bool = (i & 0x10) > 0;
        _identifier = ((i & 0x2) << 6);
        k = read();
        _identifier |= (k & 0xFC) >> 2;
        decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(k);
        decodeRestrictedAlphabetAsCharBuffer();
        if (bool)
        {
          _charactersOffset = _characterContentChunkTable.add(_charBuffer, _charBufferLength);
          _characters = _characterContentChunkTable._array;
        }
        else
        {
          _characters = _charBuffer;
          _charactersOffset = 0;
        }
        return _eventType = 4;
      case 13: 
        bool = (i & 0x10) > 0;
        _algorithmId = ((i & 0x2) << 6);
        k = read();
        _algorithmId |= (k & 0xFC) >> 2;
        decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(k);
        processCIIEncodingAlgorithm(bool);
        if (_algorithmId == 9) {
          return _eventType = 12;
        }
        return _eventType = 4;
      case 14: 
        j = i & 0xF;
        _characterContentChunkTable._cachedIndex = j;
        _characters = _characterContentChunkTable._array;
        _charactersOffset = _characterContentChunkTable._offset[j];
        _charBufferLength = _characterContentChunkTable._length[j];
        return _eventType = 4;
      case 15: 
        j = ((i & 0x3) << 8 | read()) + 16;
        _characterContentChunkTable._cachedIndex = j;
        _characters = _characterContentChunkTable._array;
        _charactersOffset = _characterContentChunkTable._offset[j];
        _charBufferLength = _characterContentChunkTable._length[j];
        return _eventType = 4;
      case 16: 
        j = ((i & 0x3) << 16 | read() << 8 | read()) + 1040;
        _characterContentChunkTable._cachedIndex = j;
        _characters = _characterContentChunkTable._array;
        _charactersOffset = _characterContentChunkTable._offset[j];
        _charBufferLength = _characterContentChunkTable._length[j];
        return _eventType = 4;
      case 17: 
        j = (read() << 16 | read() << 8 | read()) + 263184;
        _characterContentChunkTable._cachedIndex = j;
        _characters = _characterContentChunkTable._array;
        _charactersOffset = _characterContentChunkTable._offset[j];
        _charBufferLength = _characterContentChunkTable._length[j];
        return _eventType = 4;
      case 18: 
        processCommentII();
        return _eventType;
      case 19: 
        processProcessingII();
        return _eventType;
      case 21: 
        processUnexpandedEntityReference(i);
        return next();
      case 23: 
        if (_stackCount != -1)
        {
          popStack();
          _internalState = 3;
          return _eventType = 2;
        }
        _internalState = 4;
        return _eventType = 8;
      case 22: 
        if (_stackCount != -1)
        {
          popStack();
          if (_currentNamespaceAIIsEnd > 0) {
            _internalState = 2;
          }
          return _eventType = 2;
        }
        _internalState = 4;
        return _eventType = 8;
      }
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
    }
    catch (IOException localIOException)
    {
      resetOnError();
      logger.log(Level.FINE, "next() exception", localIOException);
      throw new XMLStreamException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      resetOnError();
      logger.log(Level.FINE, "next() exception", localFastInfosetException);
      throw new XMLStreamException(localFastInfosetException);
    }
    catch (RuntimeException localRuntimeException)
    {
      resetOnError();
      logger.log(Level.FINE, "next() exception", localRuntimeException);
      throw localRuntimeException;
    }
  }
  
  private final void processUtf8CharacterString(int paramInt)
    throws IOException
  {
    if ((paramInt & 0x10) > 0)
    {
      _characterContentChunkTable.ensureSize(_octetBufferLength);
      _characters = _characterContentChunkTable._array;
      _charactersOffset = _characterContentChunkTable._arrayIndex;
      decodeUtf8StringAsCharBuffer(_characterContentChunkTable._array, _charactersOffset);
      _characterContentChunkTable.add(_charBufferLength);
    }
    else
    {
      decodeUtf8StringAsCharBuffer();
      _characters = _charBuffer;
      _charactersOffset = 0;
    }
  }
  
  private final void processUtf16CharacterString(int paramInt)
    throws IOException
  {
    decodeUtf16StringAsCharBuffer();
    if ((paramInt & 0x10) > 0)
    {
      _charactersOffset = _characterContentChunkTable.add(_charBuffer, _charBufferLength);
      _characters = _characterContentChunkTable._array;
    }
    else
    {
      _characters = _charBuffer;
      _charactersOffset = 0;
    }
  }
  
  private void popStack()
  {
    _qualifiedName = _qNameStack[_stackCount];
    _currentNamespaceAIIsStart = _namespaceAIIsStartStack[_stackCount];
    _currentNamespaceAIIsEnd = _namespaceAIIsEndStack[_stackCount];
    _qNameStack[(_stackCount--)] = null;
  }
  
  public final void require(int paramInt, String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (paramInt != _eventType) {
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.eventTypeNotMatch", new Object[] { getEventTypeString(paramInt) }));
    }
    if ((paramString1 != null) && (!paramString1.equals(getNamespaceURI()))) {
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.namespaceURINotMatch", new Object[] { paramString1 }));
    }
    if ((paramString2 != null) && (!paramString2.equals(getLocalName()))) {
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.localNameNotMatch", new Object[] { paramString2 }));
    }
  }
  
  public final String getElementText()
    throws XMLStreamException
  {
    if (getEventType() != 1) {
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.mustBeOnSTARTELEMENT"), getLocation());
    }
    next();
    return getElementText(true);
  }
  
  public final String getElementText(boolean paramBoolean)
    throws XMLStreamException
  {
    if (!paramBoolean) {
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.mustBeOnSTARTELEMENT"), getLocation());
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
          throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.unexpectedEOF"));
        }
        if (i == 1) {
          throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.getElementTextExpectTextOnly"), getLocation());
        }
        throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.unexpectedEventType") + getEventTypeString(i), getLocation());
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
      throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.expectedStartOrEnd"), getLocation());
    }
    return i;
  }
  
  public final boolean hasNext()
    throws XMLStreamException
  {
    return _eventType != 8;
  }
  
  public void close()
    throws XMLStreamException
  {
    try
    {
      super.closeIfRequired();
    }
    catch (IOException localIOException) {}
  }
  
  public final String getNamespaceURI(String paramString)
  {
    String str = getNamespaceDecl(paramString);
    if (str == null)
    {
      if (paramString == null) {
        throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.nullPrefix"));
      }
      return null;
    }
    return str;
  }
  
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
      for (int k = i; k < i + j; k++) {
        if (!XMLChar.isSpace(arrayOfChar[k])) {
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
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
    }
    if (paramString2 == null) {
      throw new IllegalArgumentException();
    }
    int i;
    if (paramString1 != null) {
      for (i = 0; i < _attributes.getLength(); i++) {
        if ((_attributes.getLocalName(i).equals(paramString2)) && (_attributes.getURI(i).equals(paramString1))) {
          return _attributes.getValue(i);
        }
      }
    } else {
      for (i = 0; i < _attributes.getLength(); i++) {
        if (_attributes.getLocalName(i).equals(paramString2)) {
          return _attributes.getValue(i);
        }
      }
    }
    return null;
  }
  
  public final int getAttributeCount()
  {
    if (_eventType != 1) {
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
    }
    return _attributes.getLength();
  }
  
  public final QName getAttributeName(int paramInt)
  {
    if (_eventType != 1) {
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
    }
    return _attributes.getQualifiedName(paramInt).getQName();
  }
  
  public final String getAttributeNamespace(int paramInt)
  {
    if (_eventType != 1) {
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
    }
    return _attributes.getURI(paramInt);
  }
  
  public final String getAttributeLocalName(int paramInt)
  {
    if (_eventType != 1) {
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
    }
    return _attributes.getLocalName(paramInt);
  }
  
  public final String getAttributePrefix(int paramInt)
  {
    if (_eventType != 1) {
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
    }
    return _attributes.getPrefix(paramInt);
  }
  
  public final String getAttributeType(int paramInt)
  {
    if (_eventType != 1) {
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
    }
    return _attributes.getType(paramInt);
  }
  
  public final String getAttributeValue(int paramInt)
  {
    if (_eventType != 1) {
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
    }
    return _attributes.getValue(paramInt);
  }
  
  public final boolean isAttributeSpecified(int paramInt)
  {
    return false;
  }
  
  public final int getNamespaceCount()
  {
    if ((_eventType == 1) || (_eventType == 2)) {
      return _currentNamespaceAIIsEnd > 0 ? _currentNamespaceAIIsEnd - _currentNamespaceAIIsStart : 0;
    }
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespaceCount"));
  }
  
  public final String getNamespacePrefix(int paramInt)
  {
    if ((_eventType == 1) || (_eventType == 2)) {
      return _namespaceAIIsPrefix[(_currentNamespaceAIIsStart + paramInt)];
    }
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespacePrefix"));
  }
  
  public final String getNamespaceURI(int paramInt)
  {
    if ((_eventType == 1) || (_eventType == 2)) {
      return _namespaceAIIsNamespaceName[(_currentNamespaceAIIsStart + paramInt)];
    }
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespacePrefix"));
  }
  
  public final NamespaceContext getNamespaceContext()
  {
    return _nsContext;
  }
  
  public final int getEventType()
  {
    return _eventType;
  }
  
  public final String getText()
  {
    if (_characters == null) {
      checkTextState();
    }
    if (_characters == _characterContentChunkTable._array) {
      return _characterContentChunkTable.getString(_characterContentChunkTable._cachedIndex);
    }
    return new String(_characters, _charactersOffset, _charBufferLength);
  }
  
  public final char[] getTextCharacters()
  {
    if (_characters == null) {
      checkTextState();
    }
    return _characters;
  }
  
  public final int getTextStart()
  {
    if (_characters == null) {
      checkTextState();
    }
    return _charactersOffset;
  }
  
  public final int getTextLength()
  {
    if (_characters == null) {
      checkTextState();
    }
    return _charBufferLength;
  }
  
  public final int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
    throws XMLStreamException
  {
    if (_characters == null) {
      checkTextState();
    }
    try
    {
      int i = Math.min(_charBufferLength, paramInt3);
      System.arraycopy(_characters, _charactersOffset + paramInt1, paramArrayOfChar, paramInt2, i);
      return i;
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new XMLStreamException(localIndexOutOfBoundsException);
    }
  }
  
  protected final void checkTextState()
  {
    if (_algorithmData == null) {
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.InvalidStateForText"));
    }
    try
    {
      convertEncodingAlgorithmDataToCharacters();
    }
    catch (Exception localException)
    {
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.InvalidStateForText"));
    }
  }
  
  public final String getEncoding()
  {
    return _characterEncodingScheme;
  }
  
  public final boolean hasText()
  {
    return _characters != null;
  }
  
  public final Location getLocation()
  {
    return EventLocation.getNilLocation();
  }
  
  public final QName getName()
  {
    if ((_eventType == 1) || (_eventType == 2)) {
      return _qualifiedName.getQName();
    }
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetName"));
  }
  
  public final String getLocalName()
  {
    if ((_eventType == 1) || (_eventType == 2)) {
      return _qualifiedName.localName;
    }
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetLocalName"));
  }
  
  public final boolean hasName()
  {
    return (_eventType == 1) || (_eventType == 2);
  }
  
  public final String getNamespaceURI()
  {
    if ((_eventType == 1) || (_eventType == 2)) {
      return _qualifiedName.namespaceName;
    }
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespaceURI"));
  }
  
  public final String getPrefix()
  {
    if ((_eventType == 1) || (_eventType == 2)) {
      return _qualifiedName.prefix;
    }
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetPrefix"));
  }
  
  public final String getVersion()
  {
    return null;
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
    return null;
  }
  
  public final String getPITarget()
  {
    if (_eventType != 3) {
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetPITarget"));
    }
    return _piTarget;
  }
  
  public final String getPIData()
  {
    if (_eventType != 3) {
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetPIData"));
    }
    return _piData;
  }
  
  public final String getNameString()
  {
    if ((_eventType == 1) || (_eventType == 2)) {
      return _qualifiedName.getQNameString();
    }
    throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetName"));
  }
  
  public final String getAttributeNameString(int paramInt)
  {
    if (_eventType != 1) {
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
    }
    return _attributes.getQualifiedName(paramInt).getQNameString();
  }
  
  public final String getTextAlgorithmURI()
  {
    return _algorithmURI;
  }
  
  public final int getTextAlgorithmIndex()
  {
    return _algorithmId;
  }
  
  public final boolean hasTextAlgorithmBytes()
  {
    return _algorithmData != null;
  }
  
  /**
   * @deprecated
   */
  public final byte[] getTextAlgorithmBytes()
  {
    if (_algorithmData == null) {
      return null;
    }
    byte[] arrayOfByte = new byte[_algorithmData.length];
    System.arraycopy(_algorithmData, 0, arrayOfByte, 0, _algorithmData.length);
    return arrayOfByte;
  }
  
  public final byte[] getTextAlgorithmBytesClone()
  {
    if (_algorithmData == null) {
      return null;
    }
    byte[] arrayOfByte = new byte[_algorithmDataLength];
    System.arraycopy(_algorithmData, _algorithmDataOffset, arrayOfByte, 0, _algorithmDataLength);
    return arrayOfByte;
  }
  
  public final int getTextAlgorithmStart()
  {
    return _algorithmDataOffset;
  }
  
  public final int getTextAlgorithmLength()
  {
    return _algorithmDataLength;
  }
  
  public final int getTextAlgorithmBytes(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    throws XMLStreamException
  {
    try
    {
      System.arraycopy(_algorithmData, paramInt1, paramArrayOfByte, paramInt2, paramInt3);
      return paramInt3;
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new XMLStreamException(localIndexOutOfBoundsException);
    }
  }
  
  public final int peekNext()
    throws XMLStreamException
  {
    try
    {
      switch (DecoderStateTables.EII(peek(this)))
      {
      case 0: 
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
        return 1;
      case 6: 
      case 7: 
      case 8: 
      case 9: 
      case 10: 
      case 11: 
      case 12: 
      case 13: 
      case 14: 
      case 15: 
      case 16: 
      case 17: 
        return 4;
      case 18: 
        return 5;
      case 19: 
        return 3;
      case 21: 
        return 9;
      case 22: 
      case 23: 
        return _stackCount != -1 ? 2 : 8;
      }
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new XMLStreamException(localFastInfosetException);
    }
  }
  
  public void onBeforeOctetBufferOverwrite()
  {
    if (_algorithmData != null)
    {
      _algorithmData = getTextAlgorithmBytesClone();
      _algorithmDataOffset = 0;
      _isAlgorithmDataCloned = true;
    }
  }
  
  public final int accessNamespaceCount()
  {
    return _currentNamespaceAIIsEnd > 0 ? _currentNamespaceAIIsEnd - _currentNamespaceAIIsStart : 0;
  }
  
  public final String accessLocalName()
  {
    return _qualifiedName.localName;
  }
  
  public final String accessNamespaceURI()
  {
    return _qualifiedName.namespaceName;
  }
  
  public final String accessPrefix()
  {
    return _qualifiedName.prefix;
  }
  
  public final char[] accessTextCharacters()
  {
    if (_characters == null) {
      return null;
    }
    char[] arrayOfChar = new char[_characters.length];
    System.arraycopy(_characters, 0, arrayOfChar, 0, _characters.length);
    return arrayOfChar;
  }
  
  public final int accessTextStart()
  {
    return _charactersOffset;
  }
  
  public final int accessTextLength()
  {
    return _charBufferLength;
  }
  
  protected final void processDII()
    throws FastInfosetException, IOException
  {
    int i = read();
    if (i > 0) {
      processDIIOptionalProperties(i);
    }
  }
  
  protected final void processDIIOptionalProperties(int paramInt)
    throws FastInfosetException, IOException
  {
    if (paramInt == 32)
    {
      decodeInitialVocabulary();
      return;
    }
    if ((paramInt & 0x40) > 0) {
      decodeAdditionalData();
    }
    if ((paramInt & 0x20) > 0) {
      decodeInitialVocabulary();
    }
    if ((paramInt & 0x10) > 0) {
      decodeNotations();
    }
    if ((paramInt & 0x8) > 0) {
      decodeUnparsedEntities();
    }
    if ((paramInt & 0x4) > 0) {
      _characterEncodingScheme = decodeCharacterEncodingScheme();
    }
    if ((paramInt & 0x2) > 0) {
      int i = read() > 0 ? 1 : 0;
    }
    if ((paramInt & 0x1) > 0) {
      decodeVersion();
    }
  }
  
  protected final void resizeNamespaceAIIs()
  {
    String[] arrayOfString1 = new String[_namespaceAIIsIndex * 2];
    System.arraycopy(_namespaceAIIsPrefix, 0, arrayOfString1, 0, _namespaceAIIsIndex);
    _namespaceAIIsPrefix = arrayOfString1;
    String[] arrayOfString2 = new String[_namespaceAIIsIndex * 2];
    System.arraycopy(_namespaceAIIsNamespaceName, 0, arrayOfString2, 0, _namespaceAIIsIndex);
    _namespaceAIIsNamespaceName = arrayOfString2;
    int[] arrayOfInt = new int[_namespaceAIIsIndex * 2];
    System.arraycopy(_namespaceAIIsPrefixIndex, 0, arrayOfInt, 0, _namespaceAIIsIndex);
    _namespaceAIIsPrefixIndex = arrayOfInt;
  }
  
  protected final void processEIIWithNamespaces(boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    if (++_prefixTable._declarationId == Integer.MAX_VALUE) {
      _prefixTable.clearDeclarationIds();
    }
    _currentNamespaceAIIsStart = _namespaceAIIsIndex;
    String str1 = "";
    String str2 = "";
    for (int i = read(); (i & 0xFC) == 204; i = read())
    {
      if (_namespaceAIIsIndex == _namespaceAIIsPrefix.length) {
        resizeNamespaceAIIs();
      }
      switch (i & 0x3)
      {
      case 0: 
        str1 = str2 = _namespaceAIIsPrefix[_namespaceAIIsIndex] = _namespaceAIIsNamespaceName[_namespaceAIIsIndex] = "";
        _namespaceNameIndex = (_prefixIndex = _namespaceAIIsPrefixIndex[(_namespaceAIIsIndex++)] = -1);
        break;
      case 1: 
        str1 = _namespaceAIIsPrefix[_namespaceAIIsIndex] = "";
        str2 = _namespaceAIIsNamespaceName[_namespaceAIIsIndex] = decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(false);
        _prefixIndex = (_namespaceAIIsPrefixIndex[(_namespaceAIIsIndex++)] = -1);
        break;
      case 2: 
        str1 = _namespaceAIIsPrefix[_namespaceAIIsIndex] = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(false);
        str2 = _namespaceAIIsNamespaceName[_namespaceAIIsIndex] = "";
        _namespaceNameIndex = -1;
        _namespaceAIIsPrefixIndex[(_namespaceAIIsIndex++)] = _prefixIndex;
        break;
      case 3: 
        str1 = _namespaceAIIsPrefix[_namespaceAIIsIndex] = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(true);
        str2 = _namespaceAIIsNamespaceName[_namespaceAIIsIndex] = decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(true);
        _namespaceAIIsPrefixIndex[(_namespaceAIIsIndex++)] = _prefixIndex;
      }
      _prefixTable.pushScopeWithPrefixEntry(str1, str2, _prefixIndex, _namespaceNameIndex);
    }
    if (i != 240) {
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.EIInamespaceNameNotTerminatedCorrectly"));
    }
    _currentNamespaceAIIsEnd = _namespaceAIIsIndex;
    i = read();
    switch (DecoderStateTables.EII(i))
    {
    case 0: 
      processEII(_elementNameTable._array[i], paramBoolean);
      break;
    case 2: 
      processEII(processEIIIndexMedium(i), paramBoolean);
      break;
    case 3: 
      processEII(processEIIIndexLarge(i), paramBoolean);
      break;
    case 5: 
      QualifiedName localQualifiedName = processLiteralQualifiedName(i & 0x3, _elementNameTable.getNext());
      _elementNameTable.add(localQualifiedName);
      processEII(localQualifiedName, paramBoolean);
      break;
    case 1: 
    case 4: 
    default: 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEIIAfterAIIs"));
    }
  }
  
  protected final void processEII(QualifiedName paramQualifiedName, boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    if (_prefixTable._currentInScope[prefixIndex] != namespaceNameIndex) {
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qnameOfEIINotInScope"));
    }
    _eventType = 1;
    _qualifiedName = paramQualifiedName;
    if (_clearAttributes)
    {
      _attributes.clear();
      _clearAttributes = false;
    }
    if (paramBoolean) {
      processAIIs();
    }
    _stackCount += 1;
    if (_stackCount == _qNameStack.length)
    {
      QualifiedName[] arrayOfQualifiedName = new QualifiedName[_qNameStack.length * 2];
      System.arraycopy(_qNameStack, 0, arrayOfQualifiedName, 0, _qNameStack.length);
      _qNameStack = arrayOfQualifiedName;
      int[] arrayOfInt1 = new int[_namespaceAIIsStartStack.length * 2];
      System.arraycopy(_namespaceAIIsStartStack, 0, arrayOfInt1, 0, _namespaceAIIsStartStack.length);
      _namespaceAIIsStartStack = arrayOfInt1;
      int[] arrayOfInt2 = new int[_namespaceAIIsEndStack.length * 2];
      System.arraycopy(_namespaceAIIsEndStack, 0, arrayOfInt2, 0, _namespaceAIIsEndStack.length);
      _namespaceAIIsEndStack = arrayOfInt2;
    }
    _qNameStack[_stackCount] = _qualifiedName;
    _namespaceAIIsStartStack[_stackCount] = _currentNamespaceAIIsStart;
    _namespaceAIIsEndStack[_stackCount] = _currentNamespaceAIIsEnd;
  }
  
  protected final void processAIIs()
    throws FastInfosetException, IOException
  {
    if (++_duplicateAttributeVerifier._currentIteration == Integer.MAX_VALUE) {
      _duplicateAttributeVerifier.clear();
    }
    _clearAttributes = true;
    int j = 0;
    do
    {
      int i = read();
      QualifiedName localQualifiedName;
      int k;
      switch (DecoderStateTables.AII(i))
      {
      case 0: 
        localQualifiedName = _attributeNameTable._array[i];
        break;
      case 1: 
        k = ((i & 0x1F) << 8 | read()) + 64;
        localQualifiedName = _attributeNameTable._array[k];
        break;
      case 2: 
        k = ((i & 0xF) << 16 | read() << 8 | read()) + 8256;
        localQualifiedName = _attributeNameTable._array[k];
        break;
      case 3: 
        localQualifiedName = processLiteralQualifiedName(i & 0x3, _attributeNameTable.getNext());
        localQualifiedName.createAttributeValues(256);
        _attributeNameTable.add(localQualifiedName);
        break;
      case 5: 
        _internalState = 1;
      case 4: 
        j = 1;
        break;
      default: 
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingAIIs"));
      }
      if ((prefixIndex > 0) && (_prefixTable._currentInScope[prefixIndex] != namespaceNameIndex)) {
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.AIIqNameNotInScope"));
      }
      _duplicateAttributeVerifier.checkForDuplicateAttribute(attributeHash, attributeId);
      i = read();
      String str;
      int m;
      switch (DecoderStateTables.NISTRING(i))
      {
      case 0: 
        _octetBufferLength = ((i & 0x7) + 1);
        str = decodeUtf8StringAsString();
        if ((i & 0x40) > 0) {
          _attributeValueTable.add(str);
        }
        _attributes.addAttribute(localQualifiedName, str);
        break;
      case 1: 
        _octetBufferLength = (read() + 9);
        str = decodeUtf8StringAsString();
        if ((i & 0x40) > 0) {
          _attributeValueTable.add(str);
        }
        _attributes.addAttribute(localQualifiedName, str);
        break;
      case 2: 
        _octetBufferLength = ((read() << 24 | read() << 16 | read() << 8 | read()) + 265);
        str = decodeUtf8StringAsString();
        if ((i & 0x40) > 0) {
          _attributeValueTable.add(str);
        }
        _attributes.addAttribute(localQualifiedName, str);
        break;
      case 3: 
        _octetBufferLength = ((i & 0x7) + 1);
        str = decodeUtf16StringAsString();
        if ((i & 0x40) > 0) {
          _attributeValueTable.add(str);
        }
        _attributes.addAttribute(localQualifiedName, str);
        break;
      case 4: 
        _octetBufferLength = (read() + 9);
        str = decodeUtf16StringAsString();
        if ((i & 0x40) > 0) {
          _attributeValueTable.add(str);
        }
        _attributes.addAttribute(localQualifiedName, str);
        break;
      case 5: 
        _octetBufferLength = ((read() << 24 | read() << 16 | read() << 8 | read()) + 265);
        str = decodeUtf16StringAsString();
        if ((i & 0x40) > 0) {
          _attributeValueTable.add(str);
        }
        _attributes.addAttribute(localQualifiedName, str);
        break;
      case 6: 
        k = (i & 0x40) > 0 ? 1 : 0;
        _identifier = ((i & 0xF) << 4);
        i = read();
        _identifier |= (i & 0xF0) >> 4;
        decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(i);
        str = decodeRestrictedAlphabetAsString();
        if (k != 0) {
          _attributeValueTable.add(str);
        }
        _attributes.addAttribute(localQualifiedName, str);
        break;
      case 7: 
        k = (i & 0x40) > 0 ? 1 : 0;
        _identifier = ((i & 0xF) << 4);
        i = read();
        _identifier |= (i & 0xF0) >> 4;
        decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(i);
        processAIIEncodingAlgorithm(localQualifiedName, k);
        break;
      case 8: 
        _attributes.addAttribute(localQualifiedName, _attributeValueTable._array[(i & 0x3F)]);
        break;
      case 9: 
        m = ((i & 0x1F) << 8 | read()) + 64;
        _attributes.addAttribute(localQualifiedName, _attributeValueTable._array[m]);
        break;
      case 10: 
        m = ((i & 0xF) << 16 | read() << 8 | read()) + 8256;
        _attributes.addAttribute(localQualifiedName, _attributeValueTable._array[m]);
        break;
      case 11: 
        _attributes.addAttribute(localQualifiedName, "");
        break;
      default: 
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingAIIValue"));
      }
    } while (j == 0);
    _duplicateAttributeVerifier._poolCurrent = _duplicateAttributeVerifier._poolHead;
  }
  
  protected final QualifiedName processEIIIndexMedium(int paramInt)
    throws FastInfosetException, IOException
  {
    int i = ((paramInt & 0x7) << 8 | read()) + 32;
    return _elementNameTable._array[i];
  }
  
  protected final QualifiedName processEIIIndexLarge(int paramInt)
    throws FastInfosetException, IOException
  {
    int i;
    if ((paramInt & 0x30) == 32) {
      i = ((paramInt & 0x7) << 16 | read() << 8 | read()) + 2080;
    } else {
      i = ((read() & 0xF) << 16 | read() << 8 | read()) + 526368;
    }
    return _elementNameTable._array[i];
  }
  
  protected final QualifiedName processLiteralQualifiedName(int paramInt, QualifiedName paramQualifiedName)
    throws FastInfosetException, IOException
  {
    if (paramQualifiedName == null) {
      paramQualifiedName = new QualifiedName();
    }
    switch (paramInt)
    {
    case 0: 
      return paramQualifiedName.set("", "", decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName), "", 0, -1, -1, _identifier);
    case 1: 
      return paramQualifiedName.set("", decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName), "", 0, -1, _namespaceNameIndex, _identifier);
    case 2: 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
    case 3: 
      return paramQualifiedName.set(decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName), "", 0, _prefixIndex, _namespaceNameIndex, _identifier);
    }
    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
  }
  
  protected final void processCommentII()
    throws FastInfosetException, IOException
  {
    _eventType = 5;
    switch (decodeNonIdentifyingStringOnFirstBit())
    {
    case 0: 
      if (_addToTable) {
        _v.otherString.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
      }
      _characters = _charBuffer;
      _charactersOffset = 0;
      break;
    case 2: 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.commentIIAlgorithmNotSupported"));
    case 1: 
      CharArray localCharArray = _v.otherString.get(_integer);
      _characters = ch;
      _charactersOffset = start;
      _charBufferLength = length;
      break;
    case 3: 
      _characters = _charBuffer;
      _charactersOffset = 0;
      _charBufferLength = 0;
    }
  }
  
  protected final void processProcessingII()
    throws FastInfosetException, IOException
  {
    _eventType = 3;
    _piTarget = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);
    switch (decodeNonIdentifyingStringOnFirstBit())
    {
    case 0: 
      _piData = new String(_charBuffer, 0, _charBufferLength);
      if (_addToTable) {
        _v.otherString.add(new CharArrayString(_piData));
      }
      break;
    case 2: 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
    case 1: 
      _piData = _v.otherString.get(_integer).toString();
      break;
    case 3: 
      _piData = "";
    }
  }
  
  protected final void processUnexpandedEntityReference(int paramInt)
    throws FastInfosetException, IOException
  {
    _eventType = 9;
    String str1 = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);
    String str2 = (paramInt & 0x2) > 0 ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";
    String str3 = (paramInt & 0x1) > 0 ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";
    if (logger.isLoggable(Level.FINEST)) {
      logger.log(Level.FINEST, "processUnexpandedEntityReference: entity_reference_name={0} system_identifier={1}public_identifier={2}", new Object[] { str1, str2, str3 });
    }
  }
  
  protected final void processCIIEncodingAlgorithm(boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    _algorithmData = _octetBuffer;
    _algorithmDataOffset = _octetBufferStart;
    _algorithmDataLength = _octetBufferLength;
    _isAlgorithmDataCloned = false;
    if (_algorithmId >= 32)
    {
      _algorithmURI = _v.encodingAlgorithm.get(_algorithmId - 32);
      if (_algorithmURI == null) {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[] { Integer.valueOf(_identifier) }));
      }
    }
    else if (_algorithmId > 9)
    {
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
    }
    if (paramBoolean)
    {
      convertEncodingAlgorithmDataToCharacters();
      _characterContentChunkTable.add(_characters, _characters.length);
    }
  }
  
  protected final void processAIIEncodingAlgorithm(QualifiedName paramQualifiedName, boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    Object localObject1 = null;
    String str = null;
    if (_identifier >= 32)
    {
      str = _v.encodingAlgorithm.get(_identifier - 32);
      if (str == null) {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[] { Integer.valueOf(_identifier) }));
      }
      if (_registeredEncodingAlgorithms != null) {
        localObject1 = (EncodingAlgorithm)_registeredEncodingAlgorithms.get(str);
      }
    }
    else
    {
      if (_identifier >= 9)
      {
        if (_identifier == 9) {
          throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
        }
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
      }
      localObject1 = BuiltInEncodingAlgorithmFactory.getAlgorithm(_identifier);
    }
    Object localObject2;
    if (localObject1 != null)
    {
      localObject2 = ((EncodingAlgorithm)localObject1).decodeFromBytes(_octetBuffer, _octetBufferStart, _octetBufferLength);
    }
    else
    {
      byte[] arrayOfByte = new byte[_octetBufferLength];
      System.arraycopy(_octetBuffer, _octetBufferStart, arrayOfByte, 0, _octetBufferLength);
      localObject2 = arrayOfByte;
    }
    _attributes.addAttributeWithAlgorithmData(paramQualifiedName, str, _identifier, localObject2);
    if (paramBoolean) {
      _attributeValueTable.add(_attributes.getValue(_attributes.getIndex(qName)));
    }
  }
  
  protected final void convertEncodingAlgorithmDataToCharacters()
    throws FastInfosetException, IOException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (_algorithmId == 1)
    {
      convertBase64AlorithmDataToCharacters(localStringBuffer);
    }
    else
    {
      Object localObject1;
      if (_algorithmId < 9)
      {
        localObject1 = BuiltInEncodingAlgorithmFactory.getAlgorithm(_algorithmId).decodeFromBytes(_algorithmData, _algorithmDataOffset, _algorithmDataLength);
        BuiltInEncodingAlgorithmFactory.getAlgorithm(_algorithmId).convertToCharacters(localObject1, localStringBuffer);
      }
      else
      {
        if (_algorithmId == 9)
        {
          _octetBufferOffset -= _octetBufferLength;
          decodeUtf8StringIntoCharBuffer();
          _characters = _charBuffer;
          _charactersOffset = 0;
          return;
        }
        if (_algorithmId >= 32)
        {
          localObject1 = (EncodingAlgorithm)_registeredEncodingAlgorithms.get(_algorithmURI);
          if (localObject1 != null)
          {
            Object localObject2 = ((EncodingAlgorithm)localObject1).decodeFromBytes(_octetBuffer, _octetBufferStart, _octetBufferLength);
            ((EncodingAlgorithm)localObject1).convertToCharacters(localObject2, localStringBuffer);
          }
          else
          {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
          }
        }
      }
    }
    _characters = new char[localStringBuffer.length()];
    localStringBuffer.getChars(0, localStringBuffer.length(), _characters, 0);
    _charactersOffset = 0;
    _charBufferLength = _characters.length;
  }
  
  protected void convertBase64AlorithmDataToCharacters(StringBuffer paramStringBuffer)
    throws EncodingAlgorithmException, IOException
  {
    int i = 0;
    if (base64TaleLength > 0)
    {
      j = Math.min(3 - base64TaleLength, _algorithmDataLength);
      System.arraycopy(_algorithmData, _algorithmDataOffset, base64TaleBytes, base64TaleLength, j);
      if (base64TaleLength + j == 3)
      {
        base64DecodeWithCloning(paramStringBuffer, base64TaleBytes, 0, 3);
      }
      else
      {
        if (!isBase64Follows())
        {
          base64DecodeWithCloning(paramStringBuffer, base64TaleBytes, 0, base64TaleLength + j);
          return;
        }
        base64TaleLength += j;
        return;
      }
      i = j;
      base64TaleLength = 0;
    }
    int j = isBase64Follows() ? (_algorithmDataLength - i) % 3 : 0;
    if (_isAlgorithmDataCloned) {
      base64DecodeWithoutCloning(paramStringBuffer, _algorithmData, _algorithmDataOffset + i, _algorithmDataLength - i - j);
    } else {
      base64DecodeWithCloning(paramStringBuffer, _algorithmData, _algorithmDataOffset + i, _algorithmDataLength - i - j);
    }
    if (j > 0)
    {
      System.arraycopy(_algorithmData, _algorithmDataOffset + _algorithmDataLength - j, base64TaleBytes, 0, j);
      base64TaleLength = j;
    }
  }
  
  private void base64DecodeWithCloning(StringBuffer paramStringBuffer, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws EncodingAlgorithmException
  {
    Object localObject = BuiltInEncodingAlgorithmFactory.base64EncodingAlgorithm.decodeFromBytes(paramArrayOfByte, paramInt1, paramInt2);
    BuiltInEncodingAlgorithmFactory.base64EncodingAlgorithm.convertToCharacters(localObject, paramStringBuffer);
  }
  
  private void base64DecodeWithoutCloning(StringBuffer paramStringBuffer, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws EncodingAlgorithmException
  {
    BuiltInEncodingAlgorithmFactory.base64EncodingAlgorithm.convertToCharacters(paramArrayOfByte, paramInt1, paramInt2, paramStringBuffer);
  }
  
  public boolean isBase64Follows()
    throws IOException
  {
    int i = peek(this);
    switch (DecoderStateTables.EII(i))
    {
    case 13: 
      int j = (i & 0x2) << 6;
      int k = peek2(this);
      j |= (k & 0xFC) >> 2;
      return j == 1;
    }
    return false;
  }
  
  public final String getNamespaceDecl(String paramString)
  {
    return _prefixTable.getNamespaceFromPrefix(paramString);
  }
  
  public final String getURI(String paramString)
  {
    return getNamespaceDecl(paramString);
  }
  
  public final Iterator getPrefixes()
  {
    return _prefixTable.getPrefixes();
  }
  
  public final AttributesHolder getAttributesHolder()
  {
    return _attributes;
  }
  
  public final void setManager(StAXManager paramStAXManager)
  {
    _manager = paramStAXManager;
  }
  
  static final String getEventTypeString(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return "START_ELEMENT";
    case 2: 
      return "END_ELEMENT";
    case 3: 
      return "PROCESSING_INSTRUCTION";
    case 4: 
      return "CHARACTERS";
    case 5: 
      return "COMMENT";
    case 7: 
      return "START_DOCUMENT";
    case 8: 
      return "END_DOCUMENT";
    case 9: 
      return "ENTITY_REFERENCE";
    case 10: 
      return "ATTRIBUTE";
    case 11: 
      return "DTD";
    case 12: 
      return "CDATA";
    }
    return "UNKNOWN_EVENT_TYPE";
  }
  
  protected class NamespaceContextImpl
    implements NamespaceContext
  {
    protected NamespaceContextImpl() {}
    
    public final String getNamespaceURI(String paramString)
    {
      return _prefixTable.getNamespaceFromPrefix(paramString);
    }
    
    public final String getPrefix(String paramString)
    {
      return _prefixTable.getPrefixFromNamespace(paramString);
    }
    
    public final Iterator getPrefixes(String paramString)
    {
      return _prefixTable.getPrefixesFromNamespace(paramString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\StAXDocumentParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */