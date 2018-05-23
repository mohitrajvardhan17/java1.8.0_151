package com.sun.xml.internal.fastinfoset.sax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.DecoderStateTables;
import com.sun.xml.internal.fastinfoset.EncodingConstants;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.algorithm.BooleanEncodingAlgorithm;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithm;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmState;
import com.sun.xml.internal.fastinfoset.algorithm.DoubleEncodingAlgorithm;
import com.sun.xml.internal.fastinfoset.algorithm.FloatEncodingAlgorithm;
import com.sun.xml.internal.fastinfoset.algorithm.IntEncodingAlgorithm;
import com.sun.xml.internal.fastinfoset.algorithm.LongEncodingAlgorithm;
import com.sun.xml.internal.fastinfoset.algorithm.ShortEncodingAlgorithm;
import com.sun.xml.internal.fastinfoset.algorithm.UUIDEncodingAlgorithm;
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
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmContentHandler;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.FastInfosetReader;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXDocumentParser
  extends Decoder
  implements FastInfosetReader
{
  private static final Logger logger = Logger.getLogger(SAXDocumentParser.class.getName());
  protected boolean _namespacePrefixesFeature = false;
  protected EntityResolver _entityResolver;
  protected DTDHandler _dtdHandler;
  protected ContentHandler _contentHandler;
  protected ErrorHandler _errorHandler;
  protected LexicalHandler _lexicalHandler;
  protected DeclHandler _declHandler;
  protected EncodingAlgorithmContentHandler _algorithmHandler;
  protected PrimitiveTypeContentHandler _primitiveHandler;
  protected BuiltInEncodingAlgorithmState builtInAlgorithmState = new BuiltInEncodingAlgorithmState();
  protected AttributesHolder _attributes;
  protected int[] _namespacePrefixes = new int[16];
  protected int _namespacePrefixesIndex;
  protected boolean _clearAttributes = false;
  
  public SAXDocumentParser()
  {
    DefaultHandler localDefaultHandler = new DefaultHandler();
    _attributes = new AttributesHolder(_registeredEncodingAlgorithms);
    _entityResolver = localDefaultHandler;
    _dtdHandler = localDefaultHandler;
    _contentHandler = localDefaultHandler;
    _errorHandler = localDefaultHandler;
    _lexicalHandler = new LexicalHandlerImpl(null);
    _declHandler = new DeclHandlerImpl(null);
  }
  
  protected void resetOnError()
  {
    _clearAttributes = false;
    _attributes.clear();
    _namespacePrefixesIndex = 0;
    if (_v != null) {
      _v.prefix.clearCompletely();
    }
    _duplicateAttributeVerifier.clear();
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
    if ((paramString.equals("http://xml.org/sax/features/string-interning")) || (paramString.equals("http://jvnet.org/fastinfoset/parser/properties/string-interning"))) {
      return getStringInterning();
    }
    throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.featureNotSupported") + paramString);
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
    } else if ((paramString.equals("http://xml.org/sax/features/string-interning")) || (paramString.equals("http://jvnet.org/fastinfoset/parser/properties/string-interning"))) {
      setStringInterning(paramBoolean);
    } else {
      throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.featureNotSupported") + paramString);
    }
  }
  
  public Object getProperty(String paramString)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (paramString.equals("http://xml.org/sax/properties/lexical-handler")) {
      return getLexicalHandler();
    }
    if (paramString.equals("http://xml.org/sax/properties/declaration-handler")) {
      return getDeclHandler();
    }
    if (paramString.equals("http://jvnet.org/fastinfoset/parser/properties/external-vocabularies")) {
      return getExternalVocabularies();
    }
    if (paramString.equals("http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms")) {
      return getRegisteredEncodingAlgorithms();
    }
    if (paramString.equals("http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler")) {
      return getEncodingAlgorithmContentHandler();
    }
    if (paramString.equals("http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler")) {
      return getPrimitiveTypeContentHandler();
    }
    throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.propertyNotRecognized", new Object[] { paramString }));
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
    else if (paramString.equals("http://xml.org/sax/properties/declaration-handler"))
    {
      if ((paramObject instanceof DeclHandler)) {
        setDeclHandler((DeclHandler)paramObject);
      } else {
        throw new SAXNotSupportedException("http://xml.org/sax/properties/lexical-handler");
      }
    }
    else if (paramString.equals("http://jvnet.org/fastinfoset/parser/properties/external-vocabularies"))
    {
      if ((paramObject instanceof Map)) {
        setExternalVocabularies((Map)paramObject);
      } else {
        throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/parser/properties/external-vocabularies");
      }
    }
    else if (paramString.equals("http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms"))
    {
      if ((paramObject instanceof Map)) {
        setRegisteredEncodingAlgorithms((Map)paramObject);
      } else {
        throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms");
      }
    }
    else if (paramString.equals("http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler"))
    {
      if ((paramObject instanceof EncodingAlgorithmContentHandler)) {
        setEncodingAlgorithmContentHandler((EncodingAlgorithmContentHandler)paramObject);
      } else {
        throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler");
      }
    }
    else if (paramString.equals("http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler"))
    {
      if ((paramObject instanceof PrimitiveTypeContentHandler)) {
        setPrimitiveTypeContentHandler((PrimitiveTypeContentHandler)paramObject);
      } else {
        throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler");
      }
    }
    else if (paramString.equals("http://jvnet.org/fastinfoset/parser/properties/buffer-size"))
    {
      if ((paramObject instanceof Integer)) {
        setBufferSize(((Integer)paramObject).intValue());
      } else {
        throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/parser/properties/buffer-size");
      }
    }
    else {
      throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.propertyNotRecognized", new Object[] { paramString }));
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
  
  public void parse(InputSource paramInputSource)
    throws IOException, SAXException
  {
    try
    {
      InputStream localInputStream = paramInputSource.getByteStream();
      if (localInputStream == null)
      {
        String str = paramInputSource.getSystemId();
        if (str == null) {
          throw new SAXException(CommonResourceBundle.getInstance().getString("message.inputSource"));
        }
        parse(str);
      }
      else
      {
        parse(localInputStream);
      }
    }
    catch (FastInfosetException localFastInfosetException)
    {
      logger.log(Level.FINE, "parsing error", localFastInfosetException);
      throw new SAXException(localFastInfosetException);
    }
  }
  
  public void parse(String paramString)
    throws IOException, SAXException
  {
    try
    {
      paramString = SystemIdResolver.getAbsoluteURI(paramString);
      parse(new URL(paramString).openStream());
    }
    catch (FastInfosetException localFastInfosetException)
    {
      logger.log(Level.FINE, "parsing error", localFastInfosetException);
      throw new SAXException(localFastInfosetException);
    }
  }
  
  public final void parse(InputStream paramInputStream)
    throws IOException, FastInfosetException, SAXException
  {
    setInputStream(paramInputStream);
    parse();
  }
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler)
  {
    _lexicalHandler = paramLexicalHandler;
  }
  
  public LexicalHandler getLexicalHandler()
  {
    return _lexicalHandler;
  }
  
  public void setDeclHandler(DeclHandler paramDeclHandler)
  {
    _declHandler = paramDeclHandler;
  }
  
  public DeclHandler getDeclHandler()
  {
    return _declHandler;
  }
  
  public void setEncodingAlgorithmContentHandler(EncodingAlgorithmContentHandler paramEncodingAlgorithmContentHandler)
  {
    _algorithmHandler = paramEncodingAlgorithmContentHandler;
  }
  
  public EncodingAlgorithmContentHandler getEncodingAlgorithmContentHandler()
  {
    return _algorithmHandler;
  }
  
  public void setPrimitiveTypeContentHandler(PrimitiveTypeContentHandler paramPrimitiveTypeContentHandler)
  {
    _primitiveHandler = paramPrimitiveTypeContentHandler;
  }
  
  public PrimitiveTypeContentHandler getPrimitiveTypeContentHandler()
  {
    return _primitiveHandler;
  }
  
  public final void parse()
    throws FastInfosetException, IOException
  {
    if (_octetBuffer.length < _bufferSize) {
      _octetBuffer = new byte[_bufferSize];
    }
    try
    {
      reset();
      decodeHeader();
      if (_parseFragments) {
        processDIIFragment();
      } else {
        processDII();
      }
    }
    catch (RuntimeException localRuntimeException)
    {
      try
      {
        _errorHandler.fatalError(new SAXParseException(localRuntimeException.getClass().getName(), null, localRuntimeException));
      }
      catch (Exception localException1) {}
      resetOnError();
      throw new FastInfosetException(localRuntimeException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      try
      {
        _errorHandler.fatalError(new SAXParseException(localFastInfosetException.getClass().getName(), null, localFastInfosetException));
      }
      catch (Exception localException2) {}
      resetOnError();
      throw localFastInfosetException;
    }
    catch (IOException localIOException)
    {
      try
      {
        _errorHandler.fatalError(new SAXParseException(localIOException.getClass().getName(), null, localIOException));
      }
      catch (Exception localException3) {}
      resetOnError();
      throw localIOException;
    }
  }
  
  protected final void processDII()
    throws FastInfosetException, IOException
  {
    try
    {
      _contentHandler.startDocument();
    }
    catch (SAXException localSAXException1)
    {
      throw new FastInfosetException("processDII", localSAXException1);
    }
    _b = read();
    if (_b > 0) {
      processDIIOptionalProperties();
    }
    int i = 0;
    int j = 0;
    while ((!_terminate) || (i == 0))
    {
      _b = read();
      Object localObject;
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
        localObject = decodeLiteralQualifiedName(_b & 0x3, _elementNameTable.getNext());
        _elementNameTable.add((QualifiedName)localObject);
        processEII((QualifiedName)localObject, (_b & 0x40) > 0);
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
        localObject = (_b & 0x2) > 0 ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";
        String str = (_b & 0x1) > 0 ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";
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
        if (_notations != null) {
          _notations.clear();
        }
        if (_unparsedEntities != null) {
          _unparsedEntities.clear();
        }
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
    try
    {
      _contentHandler.endDocument();
    }
    catch (SAXException localSAXException2)
    {
      throw new FastInfosetException("processDII", localSAXException2);
    }
  }
  
  protected final void processDIIFragment()
    throws FastInfosetException, IOException
  {
    try
    {
      _contentHandler.startDocument();
    }
    catch (SAXException localSAXException1)
    {
      throw new FastInfosetException("processDII", localSAXException1);
    }
    _b = read();
    if (_b > 0) {
      processDIIOptionalProperties();
    }
    while (!_terminate)
    {
      _b = read();
      boolean bool;
      int i;
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
        QualifiedName localQualifiedName = decodeLiteralQualifiedName(_b & 0x3, _elementNameTable.getNext());
        _elementNameTable.add(localQualifiedName);
        processEII(localQualifiedName, (_b & 0x40) > 0);
        break;
      case 4: 
        processEIIWithNamespaces();
        break;
      case 6: 
        _octetBufferLength = ((_b & 0x1) + 1);
        processUtf8CharacterString();
        break;
      case 7: 
        _octetBufferLength = (read() + 3);
        processUtf8CharacterString();
        break;
      case 8: 
        _octetBufferLength = ((read() << 24 | read() << 16 | read() << 8 | read()) + 259);
        processUtf8CharacterString();
        break;
      case 9: 
        _octetBufferLength = ((_b & 0x1) + 1);
        decodeUtf16StringAsCharBuffer();
        if ((_b & 0x10) > 0) {
          _characterContentChunkTable.add(_charBuffer, _charBufferLength);
        }
        try
        {
          _contentHandler.characters(_charBuffer, 0, _charBufferLength);
        }
        catch (SAXException localSAXException2)
        {
          throw new FastInfosetException("processCII", localSAXException2);
        }
      case 10: 
        _octetBufferLength = (read() + 3);
        decodeUtf16StringAsCharBuffer();
        if ((_b & 0x10) > 0) {
          _characterContentChunkTable.add(_charBuffer, _charBufferLength);
        }
        try
        {
          _contentHandler.characters(_charBuffer, 0, _charBufferLength);
        }
        catch (SAXException localSAXException3)
        {
          throw new FastInfosetException("processCII", localSAXException3);
        }
      case 11: 
        _octetBufferLength = ((read() << 24 | read() << 16 | read() << 8 | read()) + 259);
        decodeUtf16StringAsCharBuffer();
        if ((_b & 0x10) > 0) {
          _characterContentChunkTable.add(_charBuffer, _charBufferLength);
        }
        try
        {
          _contentHandler.characters(_charBuffer, 0, _charBufferLength);
        }
        catch (SAXException localSAXException4)
        {
          throw new FastInfosetException("processCII", localSAXException4);
        }
      case 12: 
        bool = (_b & 0x10) > 0;
        _identifier = ((_b & 0x2) << 6);
        _b = read();
        _identifier |= (_b & 0xFC) >> 2;
        decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(_b);
        decodeRestrictedAlphabetAsCharBuffer();
        if (bool) {
          _characterContentChunkTable.add(_charBuffer, _charBufferLength);
        }
        try
        {
          _contentHandler.characters(_charBuffer, 0, _charBufferLength);
        }
        catch (SAXException localSAXException6)
        {
          throw new FastInfosetException("processCII", localSAXException6);
        }
      case 13: 
        bool = (_b & 0x10) > 0;
        _identifier = ((_b & 0x2) << 6);
        _b = read();
        _identifier |= (_b & 0xFC) >> 2;
        decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(_b);
        processCIIEncodingAlgorithm(bool);
        break;
      case 14: 
        i = _b & 0xF;
        try
        {
          _contentHandler.characters(_characterContentChunkTable._array, _characterContentChunkTable._offset[i], _characterContentChunkTable._length[i]);
        }
        catch (SAXException localSAXException7)
        {
          throw new FastInfosetException("processCII", localSAXException7);
        }
      case 15: 
        i = ((_b & 0x3) << 8 | read()) + 16;
        try
        {
          _contentHandler.characters(_characterContentChunkTable._array, _characterContentChunkTable._offset[i], _characterContentChunkTable._length[i]);
        }
        catch (SAXException localSAXException8)
        {
          throw new FastInfosetException("processCII", localSAXException8);
        }
      case 16: 
        i = ((_b & 0x3) << 16 | read() << 8 | read()) + 1040;
        try
        {
          _contentHandler.characters(_characterContentChunkTable._array, _characterContentChunkTable._offset[i], _characterContentChunkTable._length[i]);
        }
        catch (SAXException localSAXException9)
        {
          throw new FastInfosetException("processCII", localSAXException9);
        }
      case 17: 
        i = (read() << 16 | read() << 8 | read()) + 263184;
        try
        {
          _contentHandler.characters(_characterContentChunkTable._array, _characterContentChunkTable._offset[i], _characterContentChunkTable._length[i]);
        }
        catch (SAXException localSAXException10)
        {
          throw new FastInfosetException("processCII", localSAXException10);
        }
      case 18: 
        processCommentII();
        break;
      case 19: 
        processProcessingII();
        break;
      case 21: 
        String str1 = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);
        String str2 = (_b & 0x2) > 0 ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";
        String str3 = (_b & 0x1) > 0 ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";
        try
        {
          _contentHandler.skippedEntity(str1);
        }
        catch (SAXException localSAXException11)
        {
          throw new FastInfosetException("processUnexpandedEntityReferenceII", localSAXException11);
        }
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
    try
    {
      _contentHandler.endDocument();
    }
    catch (SAXException localSAXException5)
    {
      throw new FastInfosetException("processDII", localSAXException5);
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
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameOfEIINotInScope"));
    }
    if (paramBoolean) {
      processAIIs();
    }
    try
    {
      _contentHandler.startElement(namespaceName, localName, qName, _attributes);
    }
    catch (SAXException localSAXException1)
    {
      logger.log(Level.FINE, "processEII error", localSAXException1);
      throw new FastInfosetException("processEII", localSAXException1);
    }
    if (_clearAttributes)
    {
      _attributes.clear();
      _clearAttributes = false;
    }
    while (!_terminate)
    {
      _b = read();
      boolean bool;
      int i;
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
        QualifiedName localQualifiedName = decodeLiteralQualifiedName(_b & 0x3, _elementNameTable.getNext());
        _elementNameTable.add(localQualifiedName);
        processEII(localQualifiedName, (_b & 0x40) > 0);
        break;
      case 4: 
        processEIIWithNamespaces();
        break;
      case 6: 
        _octetBufferLength = ((_b & 0x1) + 1);
        processUtf8CharacterString();
        break;
      case 7: 
        _octetBufferLength = (read() + 3);
        processUtf8CharacterString();
        break;
      case 8: 
        _octetBufferLength = ((read() << 24 | read() << 16 | read() << 8 | read()) + 259);
        processUtf8CharacterString();
        break;
      case 9: 
        _octetBufferLength = ((_b & 0x1) + 1);
        decodeUtf16StringAsCharBuffer();
        if ((_b & 0x10) > 0) {
          _characterContentChunkTable.add(_charBuffer, _charBufferLength);
        }
        try
        {
          _contentHandler.characters(_charBuffer, 0, _charBufferLength);
        }
        catch (SAXException localSAXException2)
        {
          throw new FastInfosetException("processCII", localSAXException2);
        }
      case 10: 
        _octetBufferLength = (read() + 3);
        decodeUtf16StringAsCharBuffer();
        if ((_b & 0x10) > 0) {
          _characterContentChunkTable.add(_charBuffer, _charBufferLength);
        }
        try
        {
          _contentHandler.characters(_charBuffer, 0, _charBufferLength);
        }
        catch (SAXException localSAXException3)
        {
          throw new FastInfosetException("processCII", localSAXException3);
        }
      case 11: 
        _octetBufferLength = ((read() << 24 | read() << 16 | read() << 8 | read()) + 259);
        decodeUtf16StringAsCharBuffer();
        if ((_b & 0x10) > 0) {
          _characterContentChunkTable.add(_charBuffer, _charBufferLength);
        }
        try
        {
          _contentHandler.characters(_charBuffer, 0, _charBufferLength);
        }
        catch (SAXException localSAXException4)
        {
          throw new FastInfosetException("processCII", localSAXException4);
        }
      case 12: 
        bool = (_b & 0x10) > 0;
        _identifier = ((_b & 0x2) << 6);
        _b = read();
        _identifier |= (_b & 0xFC) >> 2;
        decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(_b);
        decodeRestrictedAlphabetAsCharBuffer();
        if (bool) {
          _characterContentChunkTable.add(_charBuffer, _charBufferLength);
        }
        try
        {
          _contentHandler.characters(_charBuffer, 0, _charBufferLength);
        }
        catch (SAXException localSAXException6)
        {
          throw new FastInfosetException("processCII", localSAXException6);
        }
      case 13: 
        bool = (_b & 0x10) > 0;
        _identifier = ((_b & 0x2) << 6);
        _b = read();
        _identifier |= (_b & 0xFC) >> 2;
        decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(_b);
        processCIIEncodingAlgorithm(bool);
        break;
      case 14: 
        i = _b & 0xF;
        try
        {
          _contentHandler.characters(_characterContentChunkTable._array, _characterContentChunkTable._offset[i], _characterContentChunkTable._length[i]);
        }
        catch (SAXException localSAXException7)
        {
          throw new FastInfosetException("processCII", localSAXException7);
        }
      case 15: 
        i = ((_b & 0x3) << 8 | read()) + 16;
        try
        {
          _contentHandler.characters(_characterContentChunkTable._array, _characterContentChunkTable._offset[i], _characterContentChunkTable._length[i]);
        }
        catch (SAXException localSAXException8)
        {
          throw new FastInfosetException("processCII", localSAXException8);
        }
      case 16: 
        i = ((_b & 0x3) << 16 | read() << 8 | read()) + 1040;
        try
        {
          _contentHandler.characters(_characterContentChunkTable._array, _characterContentChunkTable._offset[i], _characterContentChunkTable._length[i]);
        }
        catch (SAXException localSAXException9)
        {
          throw new FastInfosetException("processCII", localSAXException9);
        }
      case 17: 
        i = (read() << 16 | read() << 8 | read()) + 263184;
        try
        {
          _contentHandler.characters(_characterContentChunkTable._array, _characterContentChunkTable._offset[i], _characterContentChunkTable._length[i]);
        }
        catch (SAXException localSAXException10)
        {
          throw new FastInfosetException("processCII", localSAXException10);
        }
      case 18: 
        processCommentII();
        break;
      case 19: 
        processProcessingII();
        break;
      case 21: 
        String str1 = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);
        String str2 = (_b & 0x2) > 0 ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";
        String str3 = (_b & 0x1) > 0 ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";
        try
        {
          _contentHandler.skippedEntity(str1);
        }
        catch (SAXException localSAXException11)
        {
          throw new FastInfosetException("processUnexpandedEntityReferenceII", localSAXException11);
        }
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
    try
    {
      _contentHandler.endElement(namespaceName, localName, qName);
    }
    catch (SAXException localSAXException5)
    {
      throw new FastInfosetException("processEII", localSAXException5);
    }
  }
  
  private final void processUtf8CharacterString()
    throws FastInfosetException, IOException
  {
    if ((_b & 0x10) > 0)
    {
      _characterContentChunkTable.ensureSize(_octetBufferLength);
      int i = _characterContentChunkTable._arrayIndex;
      decodeUtf8StringAsCharBuffer(_characterContentChunkTable._array, i);
      _characterContentChunkTable.add(_charBufferLength);
      try
      {
        _contentHandler.characters(_characterContentChunkTable._array, i, _charBufferLength);
      }
      catch (SAXException localSAXException2)
      {
        throw new FastInfosetException("processCII", localSAXException2);
      }
    }
    else
    {
      decodeUtf8StringAsCharBuffer();
      try
      {
        _contentHandler.characters(_charBuffer, 0, _charBufferLength);
      }
      catch (SAXException localSAXException1)
      {
        throw new FastInfosetException("processCII", localSAXException1);
      }
    }
  }
  
  protected final void processEIIWithNamespaces()
    throws FastInfosetException, IOException
  {
    boolean bool = (_b & 0x40) > 0;
    _clearAttributes = (_namespacePrefixesFeature);
    if (++_prefixTable._declarationId == Integer.MAX_VALUE) {
      _prefixTable.clearDeclarationIds();
    }
    String str1 = "";
    String str2 = "";
    int i = _namespacePrefixesIndex;
    for (int j = read(); (j & 0xFC) == 204; j = read())
    {
      if (_namespacePrefixesIndex == _namespacePrefixes.length)
      {
        int[] arrayOfInt = new int[_namespacePrefixesIndex * 3 / 2 + 1];
        System.arraycopy(_namespacePrefixes, 0, arrayOfInt, 0, _namespacePrefixesIndex);
        _namespacePrefixes = arrayOfInt;
      }
      switch (j & 0x3)
      {
      case 0: 
        str1 = str2 = "";
        _namespaceNameIndex = (_prefixIndex = _namespacePrefixes[(_namespacePrefixesIndex++)] = -1);
        break;
      case 1: 
        str1 = "";
        str2 = decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(false);
        _prefixIndex = (_namespacePrefixes[(_namespacePrefixesIndex++)] = -1);
        break;
      case 2: 
        str1 = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(false);
        str2 = "";
        _namespaceNameIndex = -1;
        _namespacePrefixes[(_namespacePrefixesIndex++)] = _prefixIndex;
        break;
      case 3: 
        str1 = decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(true);
        str2 = decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(true);
        _namespacePrefixes[(_namespacePrefixesIndex++)] = _prefixIndex;
      }
      _prefixTable.pushScope(_prefixIndex, _namespaceNameIndex);
      if (_namespacePrefixesFeature) {
        if (str1 != "") {
          _attributes.addAttribute(new QualifiedName("xmlns", "http://www.w3.org/2000/xmlns/", str1), str2);
        } else {
          _attributes.addAttribute(EncodingConstants.DEFAULT_NAMESPACE_DECLARATION, str2);
        }
      }
      try
      {
        _contentHandler.startPrefixMapping(str1, str2);
      }
      catch (SAXException localSAXException1)
      {
        throw new IOException("processStartNamespaceAII");
      }
    }
    if (j != 240) {
      throw new IOException(CommonResourceBundle.getInstance().getString("message.EIInamespaceNameNotTerminatedCorrectly"));
    }
    int k = _namespacePrefixesIndex;
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
      QualifiedName localQualifiedName = decodeLiteralQualifiedName(_b & 0x3, _elementNameTable.getNext());
      _elementNameTable.add(localQualifiedName);
      processEII(localQualifiedName, bool);
      break;
    case 1: 
    case 4: 
    default: 
      throw new IOException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEIIAfterAIIs"));
    }
    try
    {
      for (int m = k - 1; m >= i; m--)
      {
        int n = _namespacePrefixes[m];
        _prefixTable.popScope(n);
        str1 = n == -1 ? "" : n > 0 ? _prefixTable.get(n - 1) : "xml";
        _contentHandler.endPrefixMapping(str1);
      }
      _namespacePrefixesIndex = i;
    }
    catch (SAXException localSAXException2)
    {
      throw new IOException("processStartNamespaceAII");
    }
  }
  
  protected final void processAIIs()
    throws FastInfosetException, IOException
  {
    _clearAttributes = true;
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
        localQualifiedName = decodeLiteralQualifiedName(i & 0x3, _attributeNameTable.getNext());
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
      i = read();
      String str;
      int k;
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
        j = (i & 0x40) > 0 ? 1 : 0;
        _identifier = ((i & 0xF) << 4);
        i = read();
        _identifier |= (i & 0xF0) >> 4;
        decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(i);
        str = decodeRestrictedAlphabetAsString();
        if (j != 0) {
          _attributeValueTable.add(str);
        }
        _attributes.addAttribute(localQualifiedName, str);
        break;
      case 7: 
        j = (i & 0x40) > 0 ? 1 : 0;
        _identifier = ((i & 0xF) << 4);
        i = read();
        _identifier |= (i & 0xF0) >> 4;
        decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(i);
        processAIIEncodingAlgorithm(localQualifiedName, j);
        break;
      case 8: 
        _attributes.addAttribute(localQualifiedName, _attributeValueTable._array[(i & 0x3F)]);
        break;
      case 9: 
        k = ((i & 0x1F) << 8 | read()) + 64;
        _attributes.addAttribute(localQualifiedName, _attributeValueTable._array[k]);
        break;
      case 10: 
        k = ((i & 0xF) << 16 | read() << 8 | read()) + 8256;
        _attributes.addAttribute(localQualifiedName, _attributeValueTable._array[k]);
        break;
      case 11: 
        _attributes.addAttribute(localQualifiedName, "");
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
    switch (decodeNonIdentifyingStringOnFirstBit())
    {
    case 0: 
      if (_addToTable) {
        _v.otherString.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
      }
      try
      {
        _lexicalHandler.comment(_charBuffer, 0, _charBufferLength);
      }
      catch (SAXException localSAXException1)
      {
        throw new FastInfosetException("processCommentII", localSAXException1);
      }
    case 2: 
      throw new IOException(CommonResourceBundle.getInstance().getString("message.commentIIAlgorithmNotSupported"));
    case 1: 
      CharArray localCharArray = _v.otherString.get(_integer);
      try
      {
        _lexicalHandler.comment(ch, start, length);
      }
      catch (SAXException localSAXException2)
      {
        throw new FastInfosetException("processCommentII", localSAXException2);
      }
    case 3: 
      try
      {
        _lexicalHandler.comment(_charBuffer, 0, 0);
      }
      catch (SAXException localSAXException3)
      {
        throw new FastInfosetException("processCommentII", localSAXException3);
      }
    }
  }
  
  protected final void processProcessingII()
    throws FastInfosetException, IOException
  {
    String str1 = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);
    switch (decodeNonIdentifyingStringOnFirstBit())
    {
    case 0: 
      String str2 = new String(_charBuffer, 0, _charBufferLength);
      if (_addToTable) {
        _v.otherString.add(new CharArrayString(str2));
      }
      try
      {
        _contentHandler.processingInstruction(str1, str2);
      }
      catch (SAXException localSAXException1)
      {
        throw new FastInfosetException("processProcessingII", localSAXException1);
      }
    case 2: 
      throw new IOException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
    case 1: 
      try
      {
        _contentHandler.processingInstruction(str1, _v.otherString.get(_integer).toString());
      }
      catch (SAXException localSAXException2)
      {
        throw new FastInfosetException("processProcessingII", localSAXException2);
      }
    case 3: 
      try
      {
        _contentHandler.processingInstruction(str1, "");
      }
      catch (SAXException localSAXException3)
      {
        throw new FastInfosetException("processProcessingII", localSAXException3);
      }
    }
  }
  
  protected final void processCIIEncodingAlgorithm(boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    if (_identifier < 9)
    {
      Object localObject1;
      if (_primitiveHandler != null)
      {
        processCIIBuiltInEncodingAlgorithmAsPrimitive();
      }
      else if (_algorithmHandler != null)
      {
        localObject1 = processBuiltInEncodingAlgorithmAsObject();
        try
        {
          _algorithmHandler.object(null, _identifier, localObject1);
        }
        catch (SAXException localSAXException2)
        {
          throw new FastInfosetException(localSAXException2);
        }
      }
      else
      {
        localObject1 = new StringBuffer();
        processBuiltInEncodingAlgorithmAsCharacters((StringBuffer)localObject1);
        try
        {
          _contentHandler.characters(((StringBuffer)localObject1).toString().toCharArray(), 0, ((StringBuffer)localObject1).length());
        }
        catch (SAXException localSAXException3)
        {
          throw new FastInfosetException(localSAXException3);
        }
      }
      if (paramBoolean)
      {
        localObject1 = new StringBuffer();
        processBuiltInEncodingAlgorithmAsCharacters((StringBuffer)localObject1);
        _characterContentChunkTable.add(((StringBuffer)localObject1).toString().toCharArray(), ((StringBuffer)localObject1).length());
      }
    }
    else if (_identifier == 9)
    {
      _octetBufferOffset -= _octetBufferLength;
      decodeUtf8StringIntoCharBuffer();
      try
      {
        _lexicalHandler.startCDATA();
        _contentHandler.characters(_charBuffer, 0, _charBufferLength);
        _lexicalHandler.endCDATA();
      }
      catch (SAXException localSAXException1)
      {
        throw new FastInfosetException(localSAXException1);
      }
      if (paramBoolean) {
        _characterContentChunkTable.add(_charBuffer, _charBufferLength);
      }
    }
    else if ((_identifier >= 32) && (_algorithmHandler != null))
    {
      String str = _v.encodingAlgorithm.get(_identifier - 32);
      if (str == null) {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[] { Integer.valueOf(_identifier) }));
      }
      EncodingAlgorithm localEncodingAlgorithm = (EncodingAlgorithm)_registeredEncodingAlgorithms.get(str);
      if (localEncodingAlgorithm != null)
      {
        Object localObject2 = localEncodingAlgorithm.decodeFromBytes(_octetBuffer, _octetBufferStart, _octetBufferLength);
        try
        {
          _algorithmHandler.object(str, _identifier, localObject2);
        }
        catch (SAXException localSAXException5)
        {
          throw new FastInfosetException(localSAXException5);
        }
      }
      else
      {
        try
        {
          _algorithmHandler.octets(str, _identifier, _octetBuffer, _octetBufferStart, _octetBufferLength);
        }
        catch (SAXException localSAXException4)
        {
          throw new FastInfosetException(localSAXException4);
        }
      }
      if (paramBoolean) {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.addToTableNotSupported"));
      }
    }
    else
    {
      if (_identifier >= 32) {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
      }
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
    }
  }
  
  protected final void processCIIBuiltInEncodingAlgorithmAsPrimitive()
    throws FastInfosetException, IOException
  {
    try
    {
      int i;
      Object localObject;
      switch (_identifier)
      {
      case 0: 
      case 1: 
        _primitiveHandler.bytes(_octetBuffer, _octetBufferStart, _octetBufferLength);
        break;
      case 2: 
        i = BuiltInEncodingAlgorithmFactory.shortEncodingAlgorithm.getPrimtiveLengthFromOctetLength(_octetBufferLength);
        if (i > builtInAlgorithmState.shortArray.length)
        {
          localObject = new short[i * 3 / 2 + 1];
          System.arraycopy(builtInAlgorithmState.shortArray, 0, localObject, 0, builtInAlgorithmState.shortArray.length);
          builtInAlgorithmState.shortArray = ((short[])localObject);
        }
        BuiltInEncodingAlgorithmFactory.shortEncodingAlgorithm.decodeFromBytesToShortArray(builtInAlgorithmState.shortArray, 0, _octetBuffer, _octetBufferStart, _octetBufferLength);
        _primitiveHandler.shorts(builtInAlgorithmState.shortArray, 0, i);
        break;
      case 3: 
        i = BuiltInEncodingAlgorithmFactory.intEncodingAlgorithm.getPrimtiveLengthFromOctetLength(_octetBufferLength);
        if (i > builtInAlgorithmState.intArray.length)
        {
          localObject = new int[i * 3 / 2 + 1];
          System.arraycopy(builtInAlgorithmState.intArray, 0, localObject, 0, builtInAlgorithmState.intArray.length);
          builtInAlgorithmState.intArray = ((int[])localObject);
        }
        BuiltInEncodingAlgorithmFactory.intEncodingAlgorithm.decodeFromBytesToIntArray(builtInAlgorithmState.intArray, 0, _octetBuffer, _octetBufferStart, _octetBufferLength);
        _primitiveHandler.ints(builtInAlgorithmState.intArray, 0, i);
        break;
      case 4: 
        i = BuiltInEncodingAlgorithmFactory.longEncodingAlgorithm.getPrimtiveLengthFromOctetLength(_octetBufferLength);
        if (i > builtInAlgorithmState.longArray.length)
        {
          localObject = new long[i * 3 / 2 + 1];
          System.arraycopy(builtInAlgorithmState.longArray, 0, localObject, 0, builtInAlgorithmState.longArray.length);
          builtInAlgorithmState.longArray = ((long[])localObject);
        }
        BuiltInEncodingAlgorithmFactory.longEncodingAlgorithm.decodeFromBytesToLongArray(builtInAlgorithmState.longArray, 0, _octetBuffer, _octetBufferStart, _octetBufferLength);
        _primitiveHandler.longs(builtInAlgorithmState.longArray, 0, i);
        break;
      case 5: 
        i = BuiltInEncodingAlgorithmFactory.booleanEncodingAlgorithm.getPrimtiveLengthFromOctetLength(_octetBufferLength, _octetBuffer[_octetBufferStart] & 0xFF);
        if (i > builtInAlgorithmState.booleanArray.length)
        {
          localObject = new boolean[i * 3 / 2 + 1];
          System.arraycopy(builtInAlgorithmState.booleanArray, 0, localObject, 0, builtInAlgorithmState.booleanArray.length);
          builtInAlgorithmState.booleanArray = ((boolean[])localObject);
        }
        BuiltInEncodingAlgorithmFactory.booleanEncodingAlgorithm.decodeFromBytesToBooleanArray(builtInAlgorithmState.booleanArray, 0, i, _octetBuffer, _octetBufferStart, _octetBufferLength);
        _primitiveHandler.booleans(builtInAlgorithmState.booleanArray, 0, i);
        break;
      case 6: 
        i = BuiltInEncodingAlgorithmFactory.floatEncodingAlgorithm.getPrimtiveLengthFromOctetLength(_octetBufferLength);
        if (i > builtInAlgorithmState.floatArray.length)
        {
          localObject = new float[i * 3 / 2 + 1];
          System.arraycopy(builtInAlgorithmState.floatArray, 0, localObject, 0, builtInAlgorithmState.floatArray.length);
          builtInAlgorithmState.floatArray = ((float[])localObject);
        }
        BuiltInEncodingAlgorithmFactory.floatEncodingAlgorithm.decodeFromBytesToFloatArray(builtInAlgorithmState.floatArray, 0, _octetBuffer, _octetBufferStart, _octetBufferLength);
        _primitiveHandler.floats(builtInAlgorithmState.floatArray, 0, i);
        break;
      case 7: 
        i = BuiltInEncodingAlgorithmFactory.doubleEncodingAlgorithm.getPrimtiveLengthFromOctetLength(_octetBufferLength);
        if (i > builtInAlgorithmState.doubleArray.length)
        {
          localObject = new double[i * 3 / 2 + 1];
          System.arraycopy(builtInAlgorithmState.doubleArray, 0, localObject, 0, builtInAlgorithmState.doubleArray.length);
          builtInAlgorithmState.doubleArray = ((double[])localObject);
        }
        BuiltInEncodingAlgorithmFactory.doubleEncodingAlgorithm.decodeFromBytesToDoubleArray(builtInAlgorithmState.doubleArray, 0, _octetBuffer, _octetBufferStart, _octetBufferLength);
        _primitiveHandler.doubles(builtInAlgorithmState.doubleArray, 0, i);
        break;
      case 8: 
        i = BuiltInEncodingAlgorithmFactory.uuidEncodingAlgorithm.getPrimtiveLengthFromOctetLength(_octetBufferLength);
        if (i > builtInAlgorithmState.longArray.length)
        {
          localObject = new long[i * 3 / 2 + 1];
          System.arraycopy(builtInAlgorithmState.longArray, 0, localObject, 0, builtInAlgorithmState.longArray.length);
          builtInAlgorithmState.longArray = ((long[])localObject);
        }
        BuiltInEncodingAlgorithmFactory.uuidEncodingAlgorithm.decodeFromBytesToLongArray(builtInAlgorithmState.longArray, 0, _octetBuffer, _octetBufferStart, _octetBufferLength);
        _primitiveHandler.uuids(builtInAlgorithmState.longArray, 0, i);
        break;
      case 9: 
        throw new UnsupportedOperationException("CDATA");
      default: 
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.unsupportedAlgorithm", new Object[] { Integer.valueOf(_identifier) }));
      }
    }
    catch (SAXException localSAXException)
    {
      throw new FastInfosetException(localSAXException);
    }
  }
  
  protected final void processAIIEncodingAlgorithm(QualifiedName paramQualifiedName, boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    Object localObject1;
    if (_identifier < 9)
    {
      if ((_primitiveHandler != null) || (_algorithmHandler != null))
      {
        localObject1 = processBuiltInEncodingAlgorithmAsObject();
        _attributes.addAttributeWithAlgorithmData(paramQualifiedName, null, _identifier, localObject1);
      }
      else
      {
        localObject1 = new StringBuffer();
        processBuiltInEncodingAlgorithmAsCharacters((StringBuffer)localObject1);
        _attributes.addAttribute(paramQualifiedName, ((StringBuffer)localObject1).toString());
      }
    }
    else if ((_identifier >= 32) && (_algorithmHandler != null))
    {
      localObject1 = _v.encodingAlgorithm.get(_identifier - 32);
      if (localObject1 == null) {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[] { Integer.valueOf(_identifier) }));
      }
      EncodingAlgorithm localEncodingAlgorithm = (EncodingAlgorithm)_registeredEncodingAlgorithms.get(localObject1);
      Object localObject2;
      if (localEncodingAlgorithm != null)
      {
        localObject2 = localEncodingAlgorithm.decodeFromBytes(_octetBuffer, _octetBufferStart, _octetBufferLength);
        _attributes.addAttributeWithAlgorithmData(paramQualifiedName, (String)localObject1, _identifier, localObject2);
      }
      else
      {
        localObject2 = new byte[_octetBufferLength];
        System.arraycopy(_octetBuffer, _octetBufferStart, localObject2, 0, _octetBufferLength);
        _attributes.addAttributeWithAlgorithmData(paramQualifiedName, (String)localObject1, _identifier, localObject2);
      }
    }
    else
    {
      if (_identifier >= 32) {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
      }
      if (_identifier == 9) {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
      }
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
    }
    if (paramBoolean) {
      _attributeValueTable.add(_attributes.getValue(_attributes.getIndex(qName)));
    }
  }
  
  protected final void processBuiltInEncodingAlgorithmAsCharacters(StringBuffer paramStringBuffer)
    throws FastInfosetException, IOException
  {
    Object localObject = BuiltInEncodingAlgorithmFactory.getAlgorithm(_identifier).decodeFromBytes(_octetBuffer, _octetBufferStart, _octetBufferLength);
    BuiltInEncodingAlgorithmFactory.getAlgorithm(_identifier).convertToCharacters(localObject, paramStringBuffer);
  }
  
  protected final Object processBuiltInEncodingAlgorithmAsObject()
    throws FastInfosetException, IOException
  {
    return BuiltInEncodingAlgorithmFactory.getAlgorithm(_identifier).decodeFromBytes(_octetBuffer, _octetBufferStart, _octetBufferLength);
  }
  
  private static final class DeclHandlerImpl
    implements DeclHandler
  {
    private DeclHandlerImpl() {}
    
    public void elementDecl(String paramString1, String paramString2)
      throws SAXException
    {}
    
    public void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
      throws SAXException
    {}
    
    public void internalEntityDecl(String paramString1, String paramString2)
      throws SAXException
    {}
    
    public void externalEntityDecl(String paramString1, String paramString2, String paramString3)
      throws SAXException
    {}
  }
  
  private static final class LexicalHandlerImpl
    implements LexicalHandler
  {
    private LexicalHandlerImpl() {}
    
    public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) {}
    
    public void startDTD(String paramString1, String paramString2, String paramString3) {}
    
    public void endDTD() {}
    
    public void startEntity(String paramString) {}
    
    public void endEntity(String paramString) {}
    
    public void startCDATA() {}
    
    public void endCDATA() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\sax\SAXDocumentParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */