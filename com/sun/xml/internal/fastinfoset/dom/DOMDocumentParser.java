package com.sun.xml.internal.fastinfoset.dom;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.DecoderStateTables;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithm;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class DOMDocumentParser
  extends Decoder
{
  protected Document _document;
  protected Node _currentNode;
  protected Element _currentElement;
  protected Attr[] _namespaceAttributes = new Attr[16];
  protected int _namespaceAttributesIndex;
  protected int[] _namespacePrefixes = new int[16];
  protected int _namespacePrefixesIndex;
  
  public DOMDocumentParser() {}
  
  public void parse(Document paramDocument, InputStream paramInputStream)
    throws FastInfosetException, IOException
  {
    _currentNode = (_document = paramDocument);
    _namespaceAttributesIndex = 0;
    parse(paramInputStream);
  }
  
  protected final void parse(InputStream paramInputStream)
    throws FastInfosetException, IOException
  {
    setInputStream(paramInputStream);
    parse();
  }
  
  protected void resetOnError()
  {
    _namespacePrefixesIndex = 0;
    if (_v == null) {
      _prefixTable.clearCompletely();
    }
    _duplicateAttributeVerifier.clear();
  }
  
  protected final void parse()
    throws FastInfosetException, IOException
  {
    try
    {
      reset();
      decodeHeader();
      processDII();
    }
    catch (RuntimeException localRuntimeException)
    {
      resetOnError();
      throw new FastInfosetException(localRuntimeException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      resetOnError();
      throw localFastInfosetException;
    }
    catch (IOException localIOException)
    {
      resetOnError();
      throw localIOException;
    }
  }
  
  protected final void processDII()
    throws FastInfosetException, IOException
  {
    _b = read();
    if (_b > 0) {
      processDIIOptionalProperties();
    }
    int i = 0;
    int j = 0;
    while ((!_terminate) || (i == 0))
    {
      _b = read();
      QualifiedName localQualifiedName;
      switch (DecoderStateTables.DII(_b))
      {
      case 0: 
        processEII(_elementNameTable._array[_b], false);
        i = 1;
        break;
      case 1: 
        processEII(_elementNameTable._array[(_b & 0x1F)], true);
        i = 1;
        break;
      case 2: 
        processEII(decodeEIIIndexMedium(), (_b & 0x40) > 0);
        i = 1;
        break;
      case 3: 
        processEII(decodeEIIIndexLarge(), (_b & 0x40) > 0);
        i = 1;
        break;
      case 5: 
        localQualifiedName = processLiteralQualifiedName(_b & 0x3, _elementNameTable.getNext());
        _elementNameTable.add(localQualifiedName);
        processEII(localQualifiedName, (_b & 0x40) > 0);
        i = 1;
        break;
      case 4: 
        processEIIWithNamespaces();
        i = 1;
        break;
      case 20: 
        if (j != 0) {
          throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.secondOccurenceOfDTDII"));
        }
        j = 1;
        localQualifiedName = (_b & 0x2) > 0 ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : null;
        Object localObject = (_b & 0x1) > 0 ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : null;
        for (_b = read(); _b == 225; _b = read()) {
          switch (decodeNonIdentifyingStringOnFirstBit())
          {
          case 0: 
            if (_addToTable) {
              _v.otherString.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
            }
            break;
          case 2: 
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
          case 1: 
            
          }
        }
        if ((_b & 0xF0) != 240) {
          throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingInstructionIIsNotTerminatedCorrectly"));
        }
        if (_b == 255) {
          _terminate = true;
        }
        _notations.clear();
        _unparsedEntities.clear();
        break;
      case 18: 
        processCommentII();
        break;
      case 19: 
        processProcessingII();
        break;
      case 23: 
        _doubleTerminate = true;
      case 22: 
        _terminate = true;
        break;
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
      case 21: 
      default: 
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
      }
    }
    while (!_terminate)
    {
      _b = read();
      switch (DecoderStateTables.DII(_b))
      {
      case 18: 
        processCommentII();
        break;
      case 19: 
        processProcessingII();
        break;
      case 23: 
        _doubleTerminate = true;
      case 22: 
        _terminate = true;
        break;
      case 20: 
      case 21: 
      default: 
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
      }
    }
  }
  
  protected final void processDIIOptionalProperties()
    throws FastInfosetException, IOException
  {
    if (_b == 32)
    {
      decodeInitialVocabulary();
      return;
    }
    if ((_b & 0x40) > 0) {
      decodeAdditionalData();
    }
    if ((_b & 0x20) > 0) {
      decodeInitialVocabulary();
    }
    if ((_b & 0x10) > 0) {
      decodeNotations();
    }
    if ((_b & 0x8) > 0) {
      decodeUnparsedEntities();
    }
    if ((_b & 0x4) > 0) {
      decodeCharacterEncodingScheme();
    }
    if ((_b & 0x2) > 0) {
      read();
    }
    if ((_b & 0x1) > 0) {
      decodeVersion();
    }
  }
  
  protected final void processEII(QualifiedName paramQualifiedName, boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    if (_prefixTable._currentInScope[prefixIndex] != namespaceNameIndex) {
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qnameOfEIINotInScope"));
    }
    Node localNode = _currentNode;
    _currentNode = (_currentElement = createElement(namespaceName, qName, localName));
    if (_namespaceAttributesIndex > 0)
    {
      for (int i = 0; i < _namespaceAttributesIndex; i++)
      {
        _currentElement.setAttributeNode(_namespaceAttributes[i]);
        _namespaceAttributes[i] = null;
      }
      _namespaceAttributesIndex = 0;
    }
    if (paramBoolean) {
      processAIIs();
    }
    localNode.appendChild(_currentElement);
    while (!_terminate)
    {
      _b = read();
      Object localObject1;
      int j;
      String str3;
      int k;
      switch (DecoderStateTables.EII(_b))
      {
      case 0: 
        processEII(_elementNameTable._array[_b], false);
        break;
      case 1: 
        processEII(_elementNameTable._array[(_b & 0x1F)], true);
        break;
      case 2: 
        processEII(decodeEIIIndexMedium(), (_b & 0x40) > 0);
        break;
      case 3: 
        processEII(decodeEIIIndexLarge(), (_b & 0x40) > 0);
        break;
      case 5: 
        localObject1 = processLiteralQualifiedName(_b & 0x3, _elementNameTable.getNext());
        _elementNameTable.add((QualifiedName)localObject1);
        processEII((QualifiedName)localObject1, (_b & 0x40) > 0);
        break;
      case 4: 
        processEIIWithNamespaces();
        break;
      case 6: 
        _octetBufferLength = ((_b & 0x1) + 1);
        appendOrCreateTextData(processUtf8CharacterString());
        break;
      case 7: 
        _octetBufferLength = (read() + 3);
        appendOrCreateTextData(processUtf8CharacterString());
        break;
      case 8: 
        _octetBufferLength = (read() << 24 | read() << 16 | read() << 8 | read());
        _octetBufferLength += 259;
        appendOrCreateTextData(processUtf8CharacterString());
        break;
      case 9: 
        _octetBufferLength = ((_b & 0x1) + 1);
        localObject1 = decodeUtf16StringAsString();
        if ((_b & 0x10) > 0) {
          _characterContentChunkTable.add(_charBuffer, _charBufferLength);
        }
        appendOrCreateTextData((String)localObject1);
        break;
      case 10: 
        _octetBufferLength = (read() + 3);
        localObject1 = decodeUtf16StringAsString();
        if ((_b & 0x10) > 0) {
          _characterContentChunkTable.add(_charBuffer, _charBufferLength);
        }
        appendOrCreateTextData((String)localObject1);
        break;
      case 11: 
        _octetBufferLength = (read() << 24 | read() << 16 | read() << 8 | read());
        _octetBufferLength += 259;
        localObject1 = decodeUtf16StringAsString();
        if ((_b & 0x10) > 0) {
          _characterContentChunkTable.add(_charBuffer, _charBufferLength);
        }
        appendOrCreateTextData((String)localObject1);
        break;
      case 12: 
        j = (_b & 0x10) > 0 ? 1 : 0;
        _identifier = ((_b & 0x2) << 6);
        _b = read();
        _identifier |= (_b & 0xFC) >> 2;
        decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(_b);
        str3 = decodeRestrictedAlphabetAsString();
        if (j != 0) {
          _characterContentChunkTable.add(_charBuffer, _charBufferLength);
        }
        appendOrCreateTextData(str3);
        break;
      case 13: 
        j = (_b & 0x10) > 0 ? 1 : 0;
        _identifier = ((_b & 0x2) << 6);
        _b = read();
        _identifier |= (_b & 0xFC) >> 2;
        decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(_b);
        str3 = convertEncodingAlgorithmDataToCharacters(false);
        if (j != 0) {
          _characterContentChunkTable.add(str3.toCharArray(), str3.length());
        }
        appendOrCreateTextData(str3);
        break;
      case 14: 
        String str1 = _characterContentChunkTable.getString(_b & 0xF);
        appendOrCreateTextData(str1);
        break;
      case 15: 
        k = ((_b & 0x3) << 8 | read()) + 16;
        str3 = _characterContentChunkTable.getString(k);
        appendOrCreateTextData(str3);
        break;
      case 16: 
        k = (_b & 0x3) << 16 | read() << 8 | read();
        k += 1040;
        str3 = _characterContentChunkTable.getString(k);
        appendOrCreateTextData(str3);
        break;
      case 17: 
        k = read() << 16 | read() << 8 | read();
        k += 263184;
        str3 = _characterContentChunkTable.getString(k);
        appendOrCreateTextData(str3);
        break;
      case 18: 
        processCommentII();
        break;
      case 19: 
        processProcessingII();
        break;
      case 21: 
        String str2 = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);
        str3 = (_b & 0x2) > 0 ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : null;
        Object localObject2 = (_b & 0x1) > 0 ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : null;
        break;
      case 23: 
        _doubleTerminate = true;
      case 22: 
        _terminate = true;
        break;
      case 20: 
      default: 
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
      }
    }
    _terminate = _doubleTerminate;
    _doubleTerminate = false;
    _currentNode = localNode;
  }
  
  private void appendOrCreateTextData(String paramString)
  {
    Node localNode = _currentNode.getLastChild();
    if ((localNode instanceof Text)) {
      ((Text)localNode).appendData(paramString);
    } else {
      _currentNode.appendChild(_document.createTextNode(paramString));
    }
  }
  
  private final String processUtf8CharacterString()
    throws FastInfosetException, IOException
  {
    if ((_b & 0x10) > 0)
    {
      _characterContentChunkTable.ensureSize(_octetBufferLength);
      int i = _characterContentChunkTable._arrayIndex;
      decodeUtf8StringAsCharBuffer(_characterContentChunkTable._array, i);
      _characterContentChunkTable.add(_charBufferLength);
      return _characterContentChunkTable.getString(_characterContentChunkTable._cachedIndex);
    }
    decodeUtf8StringAsCharBuffer();
    return new String(_charBuffer, 0, _charBufferLength);
  }
  
  protected final void processEIIWithNamespaces()
    throws FastInfosetException, IOException
  {
    boolean bool = (_b & 0x40) > 0;
    if (++_prefixTable._declarationId == Integer.MAX_VALUE) {
      _prefixTable.clearDeclarationIds();
    }
    Attr localAttr = null;
    QualifiedName localQualifiedName1 = _namespacePrefixesIndex;
    for (int i = read(); (i & 0xFC) == 204; i = read())
    {
      Object localObject;
      if (_namespaceAttributesIndex == _namespaceAttributes.length)
      {
        localObject = new Attr[_namespaceAttributesIndex * 3 / 2 + 1];
        System.arraycopy(_namespaceAttributes, 0, localObject, 0, _namespaceAttributesIndex);
        _namespaceAttributes = ((Attr[])localObject);
      }
      if (_namespacePrefixesIndex == _namespacePrefixes.length)
      {
        localObject = new int[_namespacePrefixesIndex * 3 / 2 + 1];
        System.arraycopy(_namespacePrefixes, 0, localObject, 0, _namespacePrefixesIndex);
        _namespacePrefixes = ((int[])localObject);
      }
      String str;
      switch (i & 0x3)
      {
      case 0: 
        localAttr = createAttribute("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns");
        localAttr.setValue("");
        _prefixIndex = (_namespaceNameIndex = _namespacePrefixes[(_namespacePrefixesIndex++)] = -1);
        break;
      case 1: 
        localAttr = createAttribute("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns");
        localAttr.setValue(decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(false));
        _prefixIndex = (_namespacePrefixes[(_namespacePrefixesIndex++)] = -1);
        break;
      case 2: 
        str = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(false);
        localAttr = createAttribute("http://www.w3.org/2000/xmlns/", createQualifiedNameString(str), str);
        localAttr.setValue("");
        _namespaceNameIndex = -1;
        _namespacePrefixes[(_namespacePrefixesIndex++)] = _prefixIndex;
        break;
      case 3: 
        str = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(true);
        localAttr = createAttribute("http://www.w3.org/2000/xmlns/", createQualifiedNameString(str), str);
        localAttr.setValue(decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(true));
        _namespacePrefixes[(_namespacePrefixesIndex++)] = _prefixIndex;
      }
      _prefixTable.pushScope(_prefixIndex, _namespaceNameIndex);
      _namespaceAttributes[(_namespaceAttributesIndex++)] = localAttr;
    }
    if (i != 240) {
      throw new IOException(CommonResourceBundle.getInstance().getString("message.EIInamespaceNameNotTerminatedCorrectly"));
    }
    QualifiedName localQualifiedName2 = _namespacePrefixesIndex;
    _b = read();
    switch (DecoderStateTables.EII(_b))
    {
    case 0: 
      processEII(_elementNameTable._array[_b], bool);
      break;
    case 2: 
      processEII(decodeEIIIndexMedium(), bool);
      break;
    case 3: 
      processEII(decodeEIIIndexLarge(), bool);
      break;
    case 5: 
      localQualifiedName3 = processLiteralQualifiedName(_b & 0x3, _elementNameTable.getNext());
      _elementNameTable.add(localQualifiedName3);
      processEII(localQualifiedName3, bool);
      break;
    case 1: 
    case 4: 
    default: 
      throw new IOException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEIIAfterAIIs"));
    }
    for (QualifiedName localQualifiedName3 = localQualifiedName1; localQualifiedName3 < localQualifiedName2; localQualifiedName3++) {
      _prefixTable.popScope(_namespacePrefixes[localQualifiedName3]);
    }
    _namespacePrefixesIndex = localQualifiedName1;
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
      return paramQualifiedName.set(null, null, decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName), -1, -1, _identifier, null);
    case 1: 
      return paramQualifiedName.set(null, decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName), -1, _namespaceNameIndex, _identifier, null);
    case 2: 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
    case 3: 
      return paramQualifiedName.set(decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName), _prefixIndex, _namespaceNameIndex, _identifier, _charBuffer);
    }
    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
  }
  
  protected final QualifiedName processLiteralQualifiedName(int paramInt)
    throws FastInfosetException, IOException
  {
    switch (paramInt)
    {
    case 0: 
      return new QualifiedName(null, null, decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName), -1, -1, _identifier, null);
    case 1: 
      return new QualifiedName(null, decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName), -1, _namespaceNameIndex, _identifier, null);
    case 2: 
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
    case 3: 
      return new QualifiedName(decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName), _prefixIndex, _namespaceNameIndex, _identifier, _charBuffer);
    }
    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
  }
  
  protected final void processAIIs()
    throws FastInfosetException, IOException
  {
    if (++_duplicateAttributeVerifier._currentIteration == Integer.MAX_VALUE) {
      _duplicateAttributeVerifier.clear();
    }
    do
    {
      int i = read();
      QualifiedName localQualifiedName;
      int j;
      switch (DecoderStateTables.AII(i))
      {
      case 0: 
        localQualifiedName = _attributeNameTable._array[i];
        break;
      case 1: 
        j = ((i & 0x1F) << 8 | read()) + 64;
        localQualifiedName = _attributeNameTable._array[j];
        break;
      case 2: 
        j = ((i & 0xF) << 16 | read() << 8 | read()) + 8256;
        localQualifiedName = _attributeNameTable._array[j];
        break;
      case 3: 
        localQualifiedName = processLiteralQualifiedName(i & 0x3, _attributeNameTable.getNext());
        localQualifiedName.createAttributeValues(256);
        _attributeNameTable.add(localQualifiedName);
        break;
      case 5: 
        _doubleTerminate = true;
      case 4: 
        _terminate = true;
        break;
      default: 
        throw new IOException(CommonResourceBundle.getInstance().getString("message.decodingAIIs"));
      }
      if ((prefixIndex > 0) && (_prefixTable._currentInScope[prefixIndex] != namespaceNameIndex)) {
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.AIIqNameNotInScope"));
      }
      _duplicateAttributeVerifier.checkForDuplicateAttribute(attributeHash, attributeId);
      Attr localAttr = createAttribute(namespaceName, qName, localName);
      i = read();
      int k;
      String str;
      int m;
      switch (DecoderStateTables.NISTRING(i))
      {
      case 0: 
        k = (i & 0x40) > 0 ? 1 : 0;
        _octetBufferLength = ((i & 0x7) + 1);
        str = decodeUtf8StringAsString();
        if (k != 0) {
          _attributeValueTable.add(str);
        }
        localAttr.setValue(str);
        _currentElement.setAttributeNode(localAttr);
        break;
      case 1: 
        k = (i & 0x40) > 0 ? 1 : 0;
        _octetBufferLength = (read() + 9);
        str = decodeUtf8StringAsString();
        if (k != 0) {
          _attributeValueTable.add(str);
        }
        localAttr.setValue(str);
        _currentElement.setAttributeNode(localAttr);
        break;
      case 2: 
        k = (i & 0x40) > 0 ? 1 : 0;
        m = read() << 24 | read() << 16 | read() << 8 | read();
        _octetBufferLength = (m + 265);
        str = decodeUtf8StringAsString();
        if (k != 0) {
          _attributeValueTable.add(str);
        }
        localAttr.setValue(str);
        _currentElement.setAttributeNode(localAttr);
        break;
      case 3: 
        k = (i & 0x40) > 0 ? 1 : 0;
        _octetBufferLength = ((i & 0x7) + 1);
        str = decodeUtf16StringAsString();
        if (k != 0) {
          _attributeValueTable.add(str);
        }
        localAttr.setValue(str);
        _currentElement.setAttributeNode(localAttr);
        break;
      case 4: 
        k = (i & 0x40) > 0 ? 1 : 0;
        _octetBufferLength = (read() + 9);
        str = decodeUtf16StringAsString();
        if (k != 0) {
          _attributeValueTable.add(str);
        }
        localAttr.setValue(str);
        _currentElement.setAttributeNode(localAttr);
        break;
      case 5: 
        k = (i & 0x40) > 0 ? 1 : 0;
        m = read() << 24 | read() << 16 | read() << 8 | read();
        _octetBufferLength = (m + 265);
        str = decodeUtf16StringAsString();
        if (k != 0) {
          _attributeValueTable.add(str);
        }
        localAttr.setValue(str);
        _currentElement.setAttributeNode(localAttr);
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
        localAttr.setValue(str);
        _currentElement.setAttributeNode(localAttr);
        break;
      case 7: 
        k = (i & 0x40) > 0 ? 1 : 0;
        _identifier = ((i & 0xF) << 4);
        i = read();
        _identifier |= (i & 0xF0) >> 4;
        decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(i);
        str = convertEncodingAlgorithmDataToCharacters(true);
        if (k != 0) {
          _attributeValueTable.add(str);
        }
        localAttr.setValue(str);
        _currentElement.setAttributeNode(localAttr);
        break;
      case 8: 
        str = _attributeValueTable._array[(i & 0x3F)];
        localAttr.setValue(str);
        _currentElement.setAttributeNode(localAttr);
        break;
      case 9: 
        k = ((i & 0x1F) << 8 | read()) + 64;
        str = _attributeValueTable._array[k];
        localAttr.setValue(str);
        _currentElement.setAttributeNode(localAttr);
        break;
      case 10: 
        k = ((i & 0xF) << 16 | read() << 8 | read()) + 8256;
        str = _attributeValueTable._array[k];
        localAttr.setValue(str);
        _currentElement.setAttributeNode(localAttr);
        break;
      case 11: 
        localAttr.setValue("");
        _currentElement.setAttributeNode(localAttr);
        break;
      default: 
        throw new IOException(CommonResourceBundle.getInstance().getString("message.decodingAIIValue"));
      }
    } while (!_terminate);
    _duplicateAttributeVerifier._poolCurrent = _duplicateAttributeVerifier._poolHead;
    _terminate = _doubleTerminate;
    _doubleTerminate = false;
  }
  
  protected final void processCommentII()
    throws FastInfosetException, IOException
  {
    String str;
    switch (decodeNonIdentifyingStringOnFirstBit())
    {
    case 0: 
      str = new String(_charBuffer, 0, _charBufferLength);
      if (_addToTable) {
        _v.otherString.add(new CharArrayString(str, false));
      }
      _currentNode.appendChild(_document.createComment(str));
      break;
    case 2: 
      throw new IOException(CommonResourceBundle.getInstance().getString("message.commentIIAlgorithmNotSupported"));
    case 1: 
      str = _v.otherString.get(_integer).toString();
      _currentNode.appendChild(_document.createComment(str));
      break;
    case 3: 
      _currentNode.appendChild(_document.createComment(""));
    }
  }
  
  protected final void processProcessingII()
    throws FastInfosetException, IOException
  {
    String str1 = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);
    String str2;
    switch (decodeNonIdentifyingStringOnFirstBit())
    {
    case 0: 
      str2 = new String(_charBuffer, 0, _charBufferLength);
      if (_addToTable) {
        _v.otherString.add(new CharArrayString(str2, false));
      }
      _currentNode.appendChild(_document.createProcessingInstruction(str1, str2));
      break;
    case 2: 
      throw new IOException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
    case 1: 
      str2 = _v.otherString.get(_integer).toString();
      _currentNode.appendChild(_document.createProcessingInstruction(str1, str2));
      break;
    case 3: 
      _currentNode.appendChild(_document.createProcessingInstruction(str1, ""));
    }
  }
  
  protected Element createElement(String paramString1, String paramString2, String paramString3)
  {
    return _document.createElementNS(paramString1, paramString2);
  }
  
  protected Attr createAttribute(String paramString1, String paramString2, String paramString3)
  {
    return _document.createAttributeNS(paramString1, paramString2);
  }
  
  protected String convertEncodingAlgorithmDataToCharacters(boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Object localObject1;
    if (_identifier < 9)
    {
      localObject1 = BuiltInEncodingAlgorithmFactory.getAlgorithm(_identifier).decodeFromBytes(_octetBuffer, _octetBufferStart, _octetBufferLength);
      BuiltInEncodingAlgorithmFactory.getAlgorithm(_identifier).convertToCharacters(localObject1, localStringBuffer);
    }
    else
    {
      if (_identifier == 9)
      {
        if (!paramBoolean)
        {
          _octetBufferOffset -= _octetBufferLength;
          return decodeUtf8StringAsString();
        }
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
      }
      if (_identifier >= 32)
      {
        localObject1 = _v.encodingAlgorithm.get(_identifier - 32);
        EncodingAlgorithm localEncodingAlgorithm = (EncodingAlgorithm)_registeredEncodingAlgorithms.get(localObject1);
        if (localEncodingAlgorithm != null)
        {
          Object localObject2 = localEncodingAlgorithm.decodeFromBytes(_octetBuffer, _octetBufferStart, _octetBufferLength);
          localEncodingAlgorithm.convertToCharacters(localObject2, localStringBuffer);
        }
        else
        {
          throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
        }
      }
    }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\dom\DOMDocumentParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */