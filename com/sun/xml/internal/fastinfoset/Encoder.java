package com.sun.xml.internal.fastinfoset;

import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithm;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.fastinfoset.org.apache.xerces.util.XMLChar;
import com.sun.xml.internal.fastinfoset.util.CharArrayIntMap;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap.Entry;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import com.sun.xml.internal.fastinfoset.vocab.SerializerVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.org.jvnet.fastinfoset.ExternalVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSerializer;
import com.sun.xml.internal.org.jvnet.fastinfoset.VocabularyApplicationData;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.helpers.DefaultHandler;

public abstract class Encoder
  extends DefaultHandler
  implements FastInfosetSerializer
{
  public static final String CHARACTER_ENCODING_SCHEME_SYSTEM_PROPERTY = "com.sun.xml.internal.fastinfoset.serializer.character-encoding-scheme";
  protected static final String _characterEncodingSchemeSystemDefault = ;
  private static int[] NUMERIC_CHARACTERS_TABLE = new int[maxCharacter("0123456789-+.E ") + 1];
  private static int[] DATE_TIME_CHARACTERS_TABLE = new int[maxCharacter("0123456789-:TZ ") + 1];
  private boolean _ignoreDTD;
  private boolean _ignoreComments;
  private boolean _ignoreProcessingInstructions;
  private boolean _ignoreWhiteSpaceTextContent;
  private boolean _useLocalNameAsKeyForQualifiedNameLookup;
  private boolean _encodingStringsAsUtf8 = true;
  private int _nonIdentifyingStringOnThirdBitCES;
  private int _nonIdentifyingStringOnFirstBitCES;
  private Map _registeredEncodingAlgorithms = new HashMap();
  protected SerializerVocabulary _v;
  protected VocabularyApplicationData _vData;
  private boolean _vIsInternal;
  protected boolean _terminate = false;
  protected int _b;
  protected OutputStream _s;
  protected char[] _charBuffer = new char['Ȁ'];
  protected byte[] _octetBuffer = new byte['Ѐ'];
  protected int _octetBufferIndex;
  protected int _markIndex = -1;
  protected int minAttributeValueSize = 0;
  protected int maxAttributeValueSize = 32;
  protected int attributeValueMapTotalCharactersConstraint = 1073741823;
  protected int minCharacterContentChunkSize = 0;
  protected int maxCharacterContentChunkSize = 32;
  protected int characterContentChunkMapTotalCharactersConstraint = 1073741823;
  private int _bitsLeftInOctet;
  private EncodingBufferOutputStream _encodingBufferOutputStream = new EncodingBufferOutputStream(null);
  private byte[] _encodingBuffer = new byte['Ȁ'];
  private int _encodingBufferIndex;
  
  private static String getDefaultEncodingScheme()
  {
    String str = System.getProperty("com.sun.xml.internal.fastinfoset.serializer.character-encoding-scheme", "UTF-8");
    if (str.equals("UTF-16BE")) {
      return "UTF-16BE";
    }
    return "UTF-8";
  }
  
  private static int maxCharacter(String paramString)
  {
    int i = 0;
    for (int j = 0; j < paramString.length(); j++) {
      if (i < paramString.charAt(j)) {
        i = paramString.charAt(j);
      }
    }
    return i;
  }
  
  protected Encoder()
  {
    setCharacterEncodingScheme(_characterEncodingSchemeSystemDefault);
  }
  
  protected Encoder(boolean paramBoolean)
  {
    setCharacterEncodingScheme(_characterEncodingSchemeSystemDefault);
    _useLocalNameAsKeyForQualifiedNameLookup = paramBoolean;
  }
  
  public final void setIgnoreDTD(boolean paramBoolean)
  {
    _ignoreDTD = paramBoolean;
  }
  
  public final boolean getIgnoreDTD()
  {
    return _ignoreDTD;
  }
  
  public final void setIgnoreComments(boolean paramBoolean)
  {
    _ignoreComments = paramBoolean;
  }
  
  public final boolean getIgnoreComments()
  {
    return _ignoreComments;
  }
  
  public final void setIgnoreProcesingInstructions(boolean paramBoolean)
  {
    _ignoreProcessingInstructions = paramBoolean;
  }
  
  public final boolean getIgnoreProcesingInstructions()
  {
    return _ignoreProcessingInstructions;
  }
  
  public final void setIgnoreWhiteSpaceTextContent(boolean paramBoolean)
  {
    _ignoreWhiteSpaceTextContent = paramBoolean;
  }
  
  public final boolean getIgnoreWhiteSpaceTextContent()
  {
    return _ignoreWhiteSpaceTextContent;
  }
  
  public void setCharacterEncodingScheme(String paramString)
  {
    if (paramString.equals("UTF-16BE"))
    {
      _encodingStringsAsUtf8 = false;
      _nonIdentifyingStringOnThirdBitCES = 132;
      _nonIdentifyingStringOnFirstBitCES = 16;
    }
    else
    {
      _encodingStringsAsUtf8 = true;
      _nonIdentifyingStringOnThirdBitCES = 128;
      _nonIdentifyingStringOnFirstBitCES = 0;
    }
  }
  
  public String getCharacterEncodingScheme()
  {
    return _encodingStringsAsUtf8 ? "UTF-8" : "UTF-16BE";
  }
  
  public void setRegisteredEncodingAlgorithms(Map paramMap)
  {
    _registeredEncodingAlgorithms = paramMap;
    if (_registeredEncodingAlgorithms == null) {
      _registeredEncodingAlgorithms = new HashMap();
    }
  }
  
  public Map getRegisteredEncodingAlgorithms()
  {
    return _registeredEncodingAlgorithms;
  }
  
  public int getMinCharacterContentChunkSize()
  {
    return minCharacterContentChunkSize;
  }
  
  public void setMinCharacterContentChunkSize(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = 0;
    }
    minCharacterContentChunkSize = paramInt;
  }
  
  public int getMaxCharacterContentChunkSize()
  {
    return maxCharacterContentChunkSize;
  }
  
  public void setMaxCharacterContentChunkSize(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = 0;
    }
    maxCharacterContentChunkSize = paramInt;
  }
  
  public int getCharacterContentChunkMapMemoryLimit()
  {
    return characterContentChunkMapTotalCharactersConstraint * 2;
  }
  
  public void setCharacterContentChunkMapMemoryLimit(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = 0;
    }
    characterContentChunkMapTotalCharactersConstraint = (paramInt / 2);
  }
  
  public boolean isCharacterContentChunkLengthMatchesLimit(int paramInt)
  {
    return (paramInt >= minCharacterContentChunkSize) && (paramInt < maxCharacterContentChunkSize);
  }
  
  public boolean canAddCharacterContentToTable(int paramInt, CharArrayIntMap paramCharArrayIntMap)
  {
    return paramCharArrayIntMap.getTotalCharacterCount() + paramInt < characterContentChunkMapTotalCharactersConstraint;
  }
  
  public int getMinAttributeValueSize()
  {
    return minAttributeValueSize;
  }
  
  public void setMinAttributeValueSize(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = 0;
    }
    minAttributeValueSize = paramInt;
  }
  
  public int getMaxAttributeValueSize()
  {
    return maxAttributeValueSize;
  }
  
  public void setMaxAttributeValueSize(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = 0;
    }
    maxAttributeValueSize = paramInt;
  }
  
  public void setAttributeValueMapMemoryLimit(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = 0;
    }
    attributeValueMapTotalCharactersConstraint = (paramInt / 2);
  }
  
  public int getAttributeValueMapMemoryLimit()
  {
    return attributeValueMapTotalCharactersConstraint * 2;
  }
  
  public boolean isAttributeValueLengthMatchesLimit(int paramInt)
  {
    return (paramInt >= minAttributeValueSize) && (paramInt < maxAttributeValueSize);
  }
  
  public boolean canAddAttributeToTable(int paramInt)
  {
    return _v.attributeValue.getTotalCharacterCount() + paramInt < attributeValueMapTotalCharactersConstraint;
  }
  
  public void setExternalVocabulary(ExternalVocabulary paramExternalVocabulary)
  {
    _v = new SerializerVocabulary();
    SerializerVocabulary localSerializerVocabulary = new SerializerVocabulary(vocabulary, _useLocalNameAsKeyForQualifiedNameLookup);
    _v.setExternalVocabulary(URI, localSerializerVocabulary, false);
    _vIsInternal = true;
  }
  
  public void setVocabularyApplicationData(VocabularyApplicationData paramVocabularyApplicationData)
  {
    _vData = paramVocabularyApplicationData;
  }
  
  public VocabularyApplicationData getVocabularyApplicationData()
  {
    return _vData;
  }
  
  public void reset()
  {
    _terminate = false;
  }
  
  public void setOutputStream(OutputStream paramOutputStream)
  {
    _octetBufferIndex = 0;
    _markIndex = -1;
    _s = paramOutputStream;
  }
  
  public void setVocabulary(SerializerVocabulary paramSerializerVocabulary)
  {
    _v = paramSerializerVocabulary;
    _vIsInternal = false;
  }
  
  protected final void encodeHeader(boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {
      _s.write(EncodingConstants.XML_DECLARATION_VALUES[0]);
    }
    _s.write(EncodingConstants.BINARY_HEADER);
  }
  
  protected final void encodeInitialVocabulary()
    throws IOException
  {
    if (_v == null)
    {
      _v = new SerializerVocabulary();
      _vIsInternal = true;
    }
    else if (_vIsInternal)
    {
      _v.clear();
      if (_vData != null) {
        _vData.clear();
      }
    }
    if ((!_v.hasInitialVocabulary()) && (!_v.hasExternalVocabulary()))
    {
      write(0);
    }
    else if (_v.hasInitialVocabulary())
    {
      _b = 32;
      write(_b);
      SerializerVocabulary localSerializerVocabulary = _v.getReadOnlyVocabulary();
      if (localSerializerVocabulary.hasExternalVocabulary())
      {
        _b = 16;
        write(_b);
        write(0);
      }
      if (localSerializerVocabulary.hasExternalVocabulary()) {
        encodeNonEmptyOctetStringOnSecondBit(_v.getExternalVocabularyURI());
      }
    }
    else if (_v.hasExternalVocabulary())
    {
      _b = 32;
      write(_b);
      _b = 16;
      write(_b);
      write(0);
      encodeNonEmptyOctetStringOnSecondBit(_v.getExternalVocabularyURI());
    }
  }
  
  protected final void encodeDocumentTermination()
    throws IOException
  {
    encodeElementTermination();
    encodeTermination();
    _flush();
    _s.flush();
  }
  
  protected final void encodeElementTermination()
    throws IOException
  {
    _terminate = true;
    switch (_b)
    {
    case 240: 
      _b = 255;
      break;
    case 255: 
      write(255);
    default: 
      _b = 240;
    }
  }
  
  protected final void encodeTermination()
    throws IOException
  {
    if (_terminate)
    {
      write(_b);
      _b = 0;
      _terminate = false;
    }
  }
  
  protected final void encodeNamespaceAttribute(String paramString1, String paramString2)
    throws IOException
  {
    _b = 204;
    if (paramString1.length() > 0) {
      _b |= 0x2;
    }
    if (paramString2.length() > 0) {
      _b |= 0x1;
    }
    write(_b);
    if (paramString1.length() > 0) {
      encodeIdentifyingNonEmptyStringOnFirstBit(paramString1, _v.prefix);
    }
    if (paramString2.length() > 0) {
      encodeIdentifyingNonEmptyStringOnFirstBit(paramString2, _v.namespaceName);
    }
  }
  
  protected final void encodeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    boolean bool = isCharacterContentChunkLengthMatchesLimit(paramInt2);
    encodeNonIdentifyingStringOnThirdBit(paramArrayOfChar, paramInt1, paramInt2, _v.characterContentChunk, bool, true);
  }
  
  protected final void encodeCharactersNoClone(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    boolean bool = isCharacterContentChunkLengthMatchesLimit(paramInt2);
    encodeNonIdentifyingStringOnThirdBit(paramArrayOfChar, paramInt1, paramInt2, _v.characterContentChunk, bool, false);
  }
  
  protected final void encodeNumericFourBitCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    encodeFourBitCharacters(0, NUMERIC_CHARACTERS_TABLE, paramArrayOfChar, paramInt1, paramInt2, paramBoolean);
  }
  
  protected final void encodeDateTimeFourBitCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    encodeFourBitCharacters(1, DATE_TIME_CHARACTERS_TABLE, paramArrayOfChar, paramInt1, paramInt2, paramBoolean);
  }
  
  protected final void encodeFourBitCharacters(int paramInt1, int[] paramArrayOfInt, char[] paramArrayOfChar, int paramInt2, int paramInt3, boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    if (paramBoolean)
    {
      boolean bool = canAddCharacterContentToTable(paramInt3, _v.characterContentChunk);
      int i = bool ? _v.characterContentChunk.obtainIndex(paramArrayOfChar, paramInt2, paramInt3, true) : _v.characterContentChunk.get(paramArrayOfChar, paramInt2, paramInt3);
      if (i != -1)
      {
        _b = 160;
        encodeNonZeroIntegerOnFourthBit(i);
        return;
      }
      if (bool) {
        _b = 152;
      } else {
        _b = 136;
      }
    }
    else
    {
      _b = 136;
    }
    write(_b);
    _b = (paramInt1 << 2);
    encodeNonEmptyFourBitCharacterStringOnSeventhBit(paramArrayOfInt, paramArrayOfChar, paramInt2, paramInt3);
  }
  
  protected final void encodeAlphabetCharacters(String paramString, char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
    throws FastInfosetException, IOException
  {
    if (paramBoolean)
    {
      boolean bool = canAddCharacterContentToTable(paramInt2, _v.characterContentChunk);
      int j = bool ? _v.characterContentChunk.obtainIndex(paramArrayOfChar, paramInt1, paramInt2, true) : _v.characterContentChunk.get(paramArrayOfChar, paramInt1, paramInt2);
      if (j != -1)
      {
        _b = 160;
        encodeNonZeroIntegerOnFourthBit(j);
        return;
      }
      if (bool) {
        _b = 152;
      } else {
        _b = 136;
      }
    }
    else
    {
      _b = 136;
    }
    int i = _v.restrictedAlphabet.get(paramString);
    if (i == -1) {
      throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.restrictedAlphabetNotPresent"));
    }
    i += 32;
    _b |= (i & 0xC0) >> 6;
    write(_b);
    _b = ((i & 0x3F) << 2);
    encodeNonEmptyNBitCharacterStringOnSeventhBit(paramString, paramArrayOfChar, paramInt1, paramInt2);
  }
  
  protected final void encodeProcessingInstruction(String paramString1, String paramString2)
    throws IOException
  {
    write(225);
    encodeIdentifyingNonEmptyStringOnFirstBit(paramString1, _v.otherNCName);
    boolean bool = isCharacterContentChunkLengthMatchesLimit(paramString2.length());
    encodeNonIdentifyingStringOnFirstBit(paramString2, _v.otherString, bool);
  }
  
  protected final void encodeDocumentTypeDeclaration(String paramString1, String paramString2)
    throws IOException
  {
    _b = 196;
    if ((paramString1 != null) && (paramString1.length() > 0)) {
      _b |= 0x2;
    }
    if ((paramString2 != null) && (paramString2.length() > 0)) {
      _b |= 0x1;
    }
    write(_b);
    if ((paramString1 != null) && (paramString1.length() > 0)) {
      encodeIdentifyingNonEmptyStringOnFirstBit(paramString1, _v.otherURI);
    }
    if ((paramString2 != null) && (paramString2.length() > 0)) {
      encodeIdentifyingNonEmptyStringOnFirstBit(paramString2, _v.otherURI);
    }
  }
  
  protected final void encodeComment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    write(226);
    boolean bool = isCharacterContentChunkLengthMatchesLimit(paramInt2);
    encodeNonIdentifyingStringOnFirstBit(paramArrayOfChar, paramInt1, paramInt2, _v.otherString, bool, true);
  }
  
  protected final void encodeCommentNoClone(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    write(226);
    boolean bool = isCharacterContentChunkLengthMatchesLimit(paramInt2);
    encodeNonIdentifyingStringOnFirstBit(paramArrayOfChar, paramInt1, paramInt2, _v.otherString, bool, false);
  }
  
  protected final void encodeElementQualifiedNameOnThirdBit(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    LocalNameQualifiedNamesMap.Entry localEntry = _v.elementName.obtainEntry(paramString3);
    if (_valueIndex > 0)
    {
      QualifiedName[] arrayOfQualifiedName = _value;
      for (int i = 0; i < _valueIndex; i++) {
        if (((paramString2 == prefix) || (paramString2.equals(prefix))) && ((paramString1 == namespaceName) || (paramString1.equals(namespaceName))))
        {
          encodeNonZeroIntegerOnThirdBit(index);
          return;
        }
      }
    }
    encodeLiteralElementQualifiedNameOnThirdBit(paramString1, paramString2, paramString3, localEntry);
  }
  
  protected final void encodeLiteralElementQualifiedNameOnThirdBit(String paramString1, String paramString2, String paramString3, LocalNameQualifiedNamesMap.Entry paramEntry)
    throws IOException
  {
    QualifiedName localQualifiedName = new QualifiedName(paramString2, paramString1, paramString3, "", _v.elementName.getNextIndex());
    paramEntry.addQualifiedName(localQualifiedName);
    int i = -1;
    int j = -1;
    if (paramString1.length() > 0)
    {
      i = _v.namespaceName.get(paramString1);
      if (i == -1) {
        throw new IOException(CommonResourceBundle.getInstance().getString("message.namespaceURINotIndexed", new Object[] { paramString1 }));
      }
      if (paramString2.length() > 0)
      {
        j = _v.prefix.get(paramString2);
        if (j == -1) {
          throw new IOException(CommonResourceBundle.getInstance().getString("message.prefixNotIndexed", new Object[] { paramString2 }));
        }
      }
    }
    int k = _v.localName.obtainIndex(paramString3);
    _b |= 0x3C;
    if (i >= 0)
    {
      _b |= 0x1;
      if (j >= 0) {
        _b |= 0x2;
      }
    }
    write(_b);
    if (i >= 0)
    {
      if (j >= 0) {
        encodeNonZeroIntegerOnSecondBitFirstBitOne(j);
      }
      encodeNonZeroIntegerOnSecondBitFirstBitOne(i);
    }
    if (k >= 0) {
      encodeNonZeroIntegerOnSecondBitFirstBitOne(k);
    } else {
      encodeNonEmptyOctetStringOnSecondBit(paramString3);
    }
  }
  
  protected final void encodeAttributeQualifiedNameOnSecondBit(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    LocalNameQualifiedNamesMap.Entry localEntry = _v.attributeName.obtainEntry(paramString3);
    if (_valueIndex > 0)
    {
      QualifiedName[] arrayOfQualifiedName = _value;
      for (int i = 0; i < _valueIndex; i++) {
        if (((paramString2 == prefix) || (paramString2.equals(prefix))) && ((paramString1 == namespaceName) || (paramString1.equals(namespaceName))))
        {
          encodeNonZeroIntegerOnSecondBitFirstBitZero(index);
          return;
        }
      }
    }
    encodeLiteralAttributeQualifiedNameOnSecondBit(paramString1, paramString2, paramString3, localEntry);
  }
  
  protected final boolean encodeLiteralAttributeQualifiedNameOnSecondBit(String paramString1, String paramString2, String paramString3, LocalNameQualifiedNamesMap.Entry paramEntry)
    throws IOException
  {
    int i = -1;
    int j = -1;
    if (paramString1.length() > 0)
    {
      i = _v.namespaceName.get(paramString1);
      if (i == -1)
      {
        if ((paramString1 == "http://www.w3.org/2000/xmlns/") || (paramString1.equals("http://www.w3.org/2000/xmlns/"))) {
          return false;
        }
        throw new IOException(CommonResourceBundle.getInstance().getString("message.namespaceURINotIndexed", new Object[] { paramString1 }));
      }
      if (paramString2.length() > 0)
      {
        j = _v.prefix.get(paramString2);
        if (j == -1) {
          throw new IOException(CommonResourceBundle.getInstance().getString("message.prefixNotIndexed", new Object[] { paramString2 }));
        }
      }
    }
    int k = _v.localName.obtainIndex(paramString3);
    QualifiedName localQualifiedName = new QualifiedName(paramString2, paramString1, paramString3, "", _v.attributeName.getNextIndex());
    paramEntry.addQualifiedName(localQualifiedName);
    _b = 120;
    if (paramString1.length() > 0)
    {
      _b |= 0x1;
      if (paramString2.length() > 0) {
        _b |= 0x2;
      }
    }
    write(_b);
    if (i >= 0)
    {
      if (j >= 0) {
        encodeNonZeroIntegerOnSecondBitFirstBitOne(j);
      }
      encodeNonZeroIntegerOnSecondBitFirstBitOne(i);
    }
    else if (paramString1 != "")
    {
      encodeNonEmptyOctetStringOnSecondBit("xml");
      encodeNonEmptyOctetStringOnSecondBit("http://www.w3.org/XML/1998/namespace");
    }
    if (k >= 0) {
      encodeNonZeroIntegerOnSecondBitFirstBitOne(k);
    } else {
      encodeNonEmptyOctetStringOnSecondBit(paramString3);
    }
    return true;
  }
  
  protected final void encodeNonIdentifyingStringOnFirstBit(String paramString, StringIntMap paramStringIntMap, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    if ((paramString == null) || (paramString.length() == 0))
    {
      write(255);
    }
    else if ((paramBoolean1) || (paramBoolean2))
    {
      int i = (paramBoolean2) || (canAddAttributeToTable(paramString.length())) ? 1 : 0;
      int j = i != 0 ? paramStringIntMap.obtainIndex(paramString) : paramStringIntMap.get(paramString);
      if (j != -1)
      {
        encodeNonZeroIntegerOnSecondBitFirstBitOne(j);
      }
      else if (i != 0)
      {
        _b = (0x40 | _nonIdentifyingStringOnFirstBitCES);
        encodeNonEmptyCharacterStringOnFifthBit(paramString);
      }
      else
      {
        _b = _nonIdentifyingStringOnFirstBitCES;
        encodeNonEmptyCharacterStringOnFifthBit(paramString);
      }
    }
    else
    {
      _b = _nonIdentifyingStringOnFirstBitCES;
      encodeNonEmptyCharacterStringOnFifthBit(paramString);
    }
  }
  
  protected final void encodeNonIdentifyingStringOnFirstBit(String paramString, CharArrayIntMap paramCharArrayIntMap, boolean paramBoolean)
    throws IOException
  {
    if ((paramString == null) || (paramString.length() == 0))
    {
      write(255);
    }
    else if (paramBoolean)
    {
      char[] arrayOfChar = paramString.toCharArray();
      int i = paramString.length();
      boolean bool = canAddCharacterContentToTable(i, paramCharArrayIntMap);
      int j = bool ? paramCharArrayIntMap.obtainIndex(arrayOfChar, 0, i, false) : paramCharArrayIntMap.get(arrayOfChar, 0, i);
      if (j != -1)
      {
        encodeNonZeroIntegerOnSecondBitFirstBitOne(j);
      }
      else if (bool)
      {
        _b = (0x40 | _nonIdentifyingStringOnFirstBitCES);
        encodeNonEmptyCharacterStringOnFifthBit(arrayOfChar, 0, i);
      }
      else
      {
        _b = _nonIdentifyingStringOnFirstBitCES;
        encodeNonEmptyCharacterStringOnFifthBit(paramString);
      }
    }
    else
    {
      _b = _nonIdentifyingStringOnFirstBitCES;
      encodeNonEmptyCharacterStringOnFifthBit(paramString);
    }
  }
  
  protected final void encodeNonIdentifyingStringOnFirstBit(char[] paramArrayOfChar, int paramInt1, int paramInt2, CharArrayIntMap paramCharArrayIntMap, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    if (paramInt2 == 0)
    {
      write(255);
    }
    else if (paramBoolean1)
    {
      boolean bool = canAddCharacterContentToTable(paramInt2, paramCharArrayIntMap);
      int i = bool ? paramCharArrayIntMap.obtainIndex(paramArrayOfChar, paramInt1, paramInt2, paramBoolean2) : paramCharArrayIntMap.get(paramArrayOfChar, paramInt1, paramInt2);
      if (i != -1)
      {
        encodeNonZeroIntegerOnSecondBitFirstBitOne(i);
      }
      else if (bool)
      {
        _b = (0x40 | _nonIdentifyingStringOnFirstBitCES);
        encodeNonEmptyCharacterStringOnFifthBit(paramArrayOfChar, paramInt1, paramInt2);
      }
      else
      {
        _b = _nonIdentifyingStringOnFirstBitCES;
        encodeNonEmptyCharacterStringOnFifthBit(paramArrayOfChar, paramInt1, paramInt2);
      }
    }
    else
    {
      _b = _nonIdentifyingStringOnFirstBitCES;
      encodeNonEmptyCharacterStringOnFifthBit(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  protected final void encodeNumericNonIdentifyingStringOnFirstBit(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException, FastInfosetException
  {
    encodeNonIdentifyingStringOnFirstBit(0, NUMERIC_CHARACTERS_TABLE, paramString, paramBoolean1, paramBoolean2);
  }
  
  protected final void encodeDateTimeNonIdentifyingStringOnFirstBit(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException, FastInfosetException
  {
    encodeNonIdentifyingStringOnFirstBit(1, DATE_TIME_CHARACTERS_TABLE, paramString, paramBoolean1, paramBoolean2);
  }
  
  protected final void encodeNonIdentifyingStringOnFirstBit(int paramInt, int[] paramArrayOfInt, String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException, FastInfosetException
  {
    if ((paramString == null) || (paramString.length() == 0))
    {
      write(255);
      return;
    }
    if ((paramBoolean1) || (paramBoolean2))
    {
      i = (paramBoolean2) || (canAddAttributeToTable(paramString.length())) ? 1 : 0;
      j = i != 0 ? _v.attributeValue.obtainIndex(paramString) : _v.attributeValue.get(paramString);
      if (j != -1)
      {
        encodeNonZeroIntegerOnSecondBitFirstBitOne(j);
        return;
      }
      if (i != 0) {
        _b = 96;
      } else {
        _b = 32;
      }
    }
    else
    {
      _b = 32;
    }
    write(_b | (paramInt & 0xF0) >> 4);
    _b = ((paramInt & 0xF) << 4);
    int i = paramString.length();
    int j = i / 2;
    int k = i % 2;
    encodeNonZeroOctetStringLengthOnFifthBit(j + k);
    encodeNonEmptyFourBitCharacterString(paramArrayOfInt, paramString.toCharArray(), 0, j, k);
  }
  
  protected final void encodeNonIdentifyingStringOnFirstBit(String paramString, int paramInt, Object paramObject)
    throws FastInfosetException, IOException
  {
    if (paramString != null)
    {
      paramInt = _v.encodingAlgorithm.get(paramString);
      if (paramInt == -1) {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.EncodingAlgorithmURI", new Object[] { paramString }));
      }
      paramInt += 32;
      EncodingAlgorithm localEncodingAlgorithm = (EncodingAlgorithm)_registeredEncodingAlgorithms.get(paramString);
      if (localEncodingAlgorithm != null)
      {
        encodeAIIObjectAlgorithmData(paramInt, paramObject, localEncodingAlgorithm);
      }
      else if ((paramObject instanceof byte[]))
      {
        byte[] arrayOfByte2 = (byte[])paramObject;
        encodeAIIOctetAlgorithmData(paramInt, arrayOfByte2, 0, arrayOfByte2.length);
      }
      else
      {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
      }
    }
    else if (paramInt <= 9)
    {
      int i = 0;
      switch (paramInt)
      {
      case 0: 
      case 1: 
        i = ((byte[])paramObject).length;
        break;
      case 2: 
        i = ((short[])paramObject).length;
        break;
      case 3: 
        i = ((int[])paramObject).length;
        break;
      case 4: 
      case 8: 
        i = ((long[])paramObject).length;
        break;
      case 5: 
        i = ((boolean[])paramObject).length;
        break;
      case 6: 
        i = ((float[])paramObject).length;
        break;
      case 7: 
        i = ((double[])paramObject).length;
        break;
      case 9: 
        throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.CDATA"));
      default: 
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.UnsupportedBuiltInAlgorithm", new Object[] { Integer.valueOf(paramInt) }));
      }
      encodeAIIBuiltInAlgorithmData(paramInt, paramObject, 0, i);
    }
    else if (paramInt >= 32)
    {
      if ((paramObject instanceof byte[]))
      {
        byte[] arrayOfByte1 = (byte[])paramObject;
        encodeAIIOctetAlgorithmData(paramInt, arrayOfByte1, 0, arrayOfByte1.length);
      }
      else
      {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
      }
    }
    else
    {
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
    }
  }
  
  protected final void encodeAIIOctetAlgorithmData(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    throws IOException
  {
    write(0x30 | (paramInt1 & 0xF0) >> 4);
    _b = ((paramInt1 & 0xF) << 4);
    encodeNonZeroOctetStringLengthOnFifthBit(paramInt3);
    write(paramArrayOfByte, paramInt2, paramInt3);
  }
  
  protected final void encodeAIIObjectAlgorithmData(int paramInt, Object paramObject, EncodingAlgorithm paramEncodingAlgorithm)
    throws FastInfosetException, IOException
  {
    write(0x30 | (paramInt & 0xF0) >> 4);
    _b = ((paramInt & 0xF) << 4);
    _encodingBufferOutputStream.reset();
    paramEncodingAlgorithm.encodeToOutputStream(paramObject, _encodingBufferOutputStream);
    encodeNonZeroOctetStringLengthOnFifthBit(_encodingBufferIndex);
    write(_encodingBuffer, _encodingBufferIndex);
  }
  
  protected final void encodeAIIBuiltInAlgorithmData(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
    throws IOException
  {
    write(0x30 | (paramInt1 & 0xF0) >> 4);
    _b = ((paramInt1 & 0xF) << 4);
    int i = BuiltInEncodingAlgorithmFactory.getAlgorithm(paramInt1).getOctetLengthFromPrimitiveLength(paramInt3);
    encodeNonZeroOctetStringLengthOnFifthBit(i);
    ensureSize(i);
    BuiltInEncodingAlgorithmFactory.getAlgorithm(paramInt1).encodeToBytes(paramObject, paramInt2, paramInt3, _octetBuffer, _octetBufferIndex);
    _octetBufferIndex += i;
  }
  
  protected final void encodeNonIdentifyingStringOnThirdBit(char[] paramArrayOfChar, int paramInt1, int paramInt2, CharArrayIntMap paramCharArrayIntMap, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    if (paramBoolean1)
    {
      boolean bool = canAddCharacterContentToTable(paramInt2, paramCharArrayIntMap);
      int i = bool ? paramCharArrayIntMap.obtainIndex(paramArrayOfChar, paramInt1, paramInt2, paramBoolean2) : paramCharArrayIntMap.get(paramArrayOfChar, paramInt1, paramInt2);
      if (i != -1)
      {
        _b = 160;
        encodeNonZeroIntegerOnFourthBit(i);
      }
      else if (bool)
      {
        _b = (0x10 | _nonIdentifyingStringOnThirdBitCES);
        encodeNonEmptyCharacterStringOnSeventhBit(paramArrayOfChar, paramInt1, paramInt2);
      }
      else
      {
        _b = _nonIdentifyingStringOnThirdBitCES;
        encodeNonEmptyCharacterStringOnSeventhBit(paramArrayOfChar, paramInt1, paramInt2);
      }
    }
    else
    {
      _b = _nonIdentifyingStringOnThirdBitCES;
      encodeNonEmptyCharacterStringOnSeventhBit(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  protected final void encodeNonIdentifyingStringOnThirdBit(String paramString, int paramInt, Object paramObject)
    throws FastInfosetException, IOException
  {
    if (paramString != null)
    {
      paramInt = _v.encodingAlgorithm.get(paramString);
      if (paramInt == -1) {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.EncodingAlgorithmURI", new Object[] { paramString }));
      }
      paramInt += 32;
      EncodingAlgorithm localEncodingAlgorithm = (EncodingAlgorithm)_registeredEncodingAlgorithms.get(paramString);
      if (localEncodingAlgorithm != null)
      {
        encodeCIIObjectAlgorithmData(paramInt, paramObject, localEncodingAlgorithm);
      }
      else if ((paramObject instanceof byte[]))
      {
        byte[] arrayOfByte2 = (byte[])paramObject;
        encodeCIIOctetAlgorithmData(paramInt, arrayOfByte2, 0, arrayOfByte2.length);
      }
      else
      {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
      }
    }
    else if (paramInt <= 9)
    {
      int i = 0;
      switch (paramInt)
      {
      case 0: 
      case 1: 
        i = ((byte[])paramObject).length;
        break;
      case 2: 
        i = ((short[])paramObject).length;
        break;
      case 3: 
        i = ((int[])paramObject).length;
        break;
      case 4: 
      case 8: 
        i = ((long[])paramObject).length;
        break;
      case 5: 
        i = ((boolean[])paramObject).length;
        break;
      case 6: 
        i = ((float[])paramObject).length;
        break;
      case 7: 
        i = ((double[])paramObject).length;
        break;
      case 9: 
        throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.CDATA"));
      default: 
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.UnsupportedBuiltInAlgorithm", new Object[] { Integer.valueOf(paramInt) }));
      }
      encodeCIIBuiltInAlgorithmData(paramInt, paramObject, 0, i);
    }
    else if (paramInt >= 32)
    {
      if ((paramObject instanceof byte[]))
      {
        byte[] arrayOfByte1 = (byte[])paramObject;
        encodeCIIOctetAlgorithmData(paramInt, arrayOfByte1, 0, arrayOfByte1.length);
      }
      else
      {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
      }
    }
    else
    {
      throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
    }
  }
  
  protected final void encodeNonIdentifyingStringOnThirdBit(String paramString, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    throws FastInfosetException, IOException
  {
    if (paramString != null)
    {
      paramInt1 = _v.encodingAlgorithm.get(paramString);
      if (paramInt1 == -1) {
        throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.EncodingAlgorithmURI", new Object[] { paramString }));
      }
      paramInt1 += 32;
    }
    encodeCIIOctetAlgorithmData(paramInt1, paramArrayOfByte, paramInt2, paramInt3);
  }
  
  protected final void encodeCIIOctetAlgorithmData(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    throws IOException
  {
    write(0x8C | (paramInt1 & 0xC0) >> 6);
    _b = ((paramInt1 & 0x3F) << 2);
    encodeNonZeroOctetStringLengthOnSenventhBit(paramInt3);
    write(paramArrayOfByte, paramInt2, paramInt3);
  }
  
  protected final void encodeCIIObjectAlgorithmData(int paramInt, Object paramObject, EncodingAlgorithm paramEncodingAlgorithm)
    throws FastInfosetException, IOException
  {
    write(0x8C | (paramInt & 0xC0) >> 6);
    _b = ((paramInt & 0x3F) << 2);
    _encodingBufferOutputStream.reset();
    paramEncodingAlgorithm.encodeToOutputStream(paramObject, _encodingBufferOutputStream);
    encodeNonZeroOctetStringLengthOnSenventhBit(_encodingBufferIndex);
    write(_encodingBuffer, _encodingBufferIndex);
  }
  
  protected final void encodeCIIBuiltInAlgorithmData(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
    throws FastInfosetException, IOException
  {
    write(0x8C | (paramInt1 & 0xC0) >> 6);
    _b = ((paramInt1 & 0x3F) << 2);
    int i = BuiltInEncodingAlgorithmFactory.getAlgorithm(paramInt1).getOctetLengthFromPrimitiveLength(paramInt3);
    encodeNonZeroOctetStringLengthOnSenventhBit(i);
    ensureSize(i);
    BuiltInEncodingAlgorithmFactory.getAlgorithm(paramInt1).encodeToBytes(paramObject, paramInt2, paramInt3, _octetBuffer, _octetBufferIndex);
    _octetBufferIndex += i;
  }
  
  protected final void encodeCIIBuiltInAlgorithmDataAsCDATA(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws FastInfosetException, IOException
  {
    write(140);
    _b = 36;
    paramInt2 = encodeUTF8String(paramArrayOfChar, paramInt1, paramInt2);
    encodeNonZeroOctetStringLengthOnSenventhBit(paramInt2);
    write(_encodingBuffer, paramInt2);
  }
  
  protected final void encodeIdentifyingNonEmptyStringOnFirstBit(String paramString, StringIntMap paramStringIntMap)
    throws IOException
  {
    int i = paramStringIntMap.obtainIndex(paramString);
    if (i == -1) {
      encodeNonEmptyOctetStringOnSecondBit(paramString);
    } else {
      encodeNonZeroIntegerOnSecondBitFirstBitOne(i);
    }
  }
  
  protected final void encodeNonEmptyOctetStringOnSecondBit(String paramString)
    throws IOException
  {
    int i = encodeUTF8String(paramString);
    encodeNonZeroOctetStringLengthOnSecondBit(i);
    write(_encodingBuffer, i);
  }
  
  protected final void encodeNonZeroOctetStringLengthOnSecondBit(int paramInt)
    throws IOException
  {
    if (paramInt < 65)
    {
      write(paramInt - 1);
    }
    else if (paramInt < 321)
    {
      write(64);
      write(paramInt - 65);
    }
    else
    {
      write(96);
      paramInt -= 321;
      write(paramInt >>> 24);
      write(paramInt >> 16 & 0xFF);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    }
  }
  
  protected final void encodeNonEmptyCharacterStringOnFifthBit(String paramString)
    throws IOException
  {
    int i = _encodingStringsAsUtf8 ? encodeUTF8String(paramString) : encodeUtf16String(paramString);
    encodeNonZeroOctetStringLengthOnFifthBit(i);
    write(_encodingBuffer, i);
  }
  
  protected final void encodeNonEmptyCharacterStringOnFifthBit(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    paramInt2 = _encodingStringsAsUtf8 ? encodeUTF8String(paramArrayOfChar, paramInt1, paramInt2) : encodeUtf16String(paramArrayOfChar, paramInt1, paramInt2);
    encodeNonZeroOctetStringLengthOnFifthBit(paramInt2);
    write(_encodingBuffer, paramInt2);
  }
  
  protected final void encodeNonZeroOctetStringLengthOnFifthBit(int paramInt)
    throws IOException
  {
    if (paramInt < 9)
    {
      write(_b | paramInt - 1);
    }
    else if (paramInt < 265)
    {
      write(_b | 0x8);
      write(paramInt - 9);
    }
    else
    {
      write(_b | 0xC);
      paramInt -= 265;
      write(paramInt >>> 24);
      write(paramInt >> 16 & 0xFF);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    }
  }
  
  protected final void encodeNonEmptyCharacterStringOnSeventhBit(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    paramInt2 = _encodingStringsAsUtf8 ? encodeUTF8String(paramArrayOfChar, paramInt1, paramInt2) : encodeUtf16String(paramArrayOfChar, paramInt1, paramInt2);
    encodeNonZeroOctetStringLengthOnSenventhBit(paramInt2);
    write(_encodingBuffer, paramInt2);
  }
  
  protected final void encodeNonEmptyFourBitCharacterStringOnSeventhBit(int[] paramArrayOfInt, char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws FastInfosetException, IOException
  {
    int i = paramInt2 / 2;
    int j = paramInt2 % 2;
    encodeNonZeroOctetStringLengthOnSenventhBit(i + j);
    encodeNonEmptyFourBitCharacterString(paramArrayOfInt, paramArrayOfChar, paramInt1, i, j);
  }
  
  protected final void encodeNonEmptyFourBitCharacterString(int[] paramArrayOfInt, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3)
    throws FastInfosetException, IOException
  {
    ensureSize(paramInt2 + paramInt3);
    int i = 0;
    for (int j = 0; j < paramInt2; j++)
    {
      i = paramArrayOfInt[paramArrayOfChar[(paramInt1++)]] << 4 | paramArrayOfInt[paramArrayOfChar[(paramInt1++)]];
      if (i < 0) {
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.characterOutofAlphabetRange"));
      }
      _octetBuffer[(_octetBufferIndex++)] = ((byte)i);
    }
    if (paramInt3 == 1)
    {
      i = paramArrayOfInt[paramArrayOfChar[paramInt1]] << 4 | 0xF;
      if (i < 0) {
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.characterOutofAlphabetRange"));
      }
      _octetBuffer[(_octetBufferIndex++)] = ((byte)i);
    }
  }
  
  protected final void encodeNonEmptyNBitCharacterStringOnSeventhBit(String paramString, char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws FastInfosetException, IOException
  {
    for (int i = 1; 1 << i <= paramString.length(); i++) {}
    int j = paramInt2 * i;
    int k = j / 8;
    int m = j % 8;
    int n = k + (m > 0 ? 1 : 0);
    encodeNonZeroOctetStringLengthOnSenventhBit(n);
    resetBits();
    ensureSize(n);
    int i1 = 0;
    for (int i2 = 0; i2 < paramInt2; i2++)
    {
      int i3 = paramArrayOfChar[(paramInt1 + i2)];
      for (i1 = 0; (i1 < paramString.length()) && (i3 != paramString.charAt(i1)); i1++) {}
      if (i1 == paramString.length()) {
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.characterOutofAlphabetRange"));
      }
      writeBits(i, i1);
    }
    if (m > 0)
    {
      _b |= (1 << 8 - m) - 1;
      write(_b);
    }
  }
  
  private final void resetBits()
  {
    _bitsLeftInOctet = 8;
    _b = 0;
  }
  
  private final void writeBits(int paramInt1, int paramInt2)
    throws IOException
  {
    while (paramInt1 > 0)
    {
      int i = (paramInt2 & 1 << --paramInt1) > 0 ? 1 : 0;
      _b |= i << --_bitsLeftInOctet;
      if (_bitsLeftInOctet == 0)
      {
        write(_b);
        _bitsLeftInOctet = 8;
        _b = 0;
      }
    }
  }
  
  protected final void encodeNonZeroOctetStringLengthOnSenventhBit(int paramInt)
    throws IOException
  {
    if (paramInt < 3)
    {
      write(_b | paramInt - 1);
    }
    else if (paramInt < 259)
    {
      write(_b | 0x2);
      write(paramInt - 3);
    }
    else
    {
      write(_b | 0x3);
      paramInt -= 259;
      write(paramInt >>> 24);
      write(paramInt >> 16 & 0xFF);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    }
  }
  
  protected final void encodeNonZeroIntegerOnSecondBitFirstBitOne(int paramInt)
    throws IOException
  {
    if (paramInt < 64)
    {
      write(0x80 | paramInt);
    }
    else if (paramInt < 8256)
    {
      paramInt -= 64;
      _b = (0xC0 | paramInt >> 8);
      write(_b);
      write(paramInt & 0xFF);
    }
    else if (paramInt < 1048576)
    {
      paramInt -= 8256;
      _b = (0xE0 | paramInt >> 16);
      write(_b);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    }
    else
    {
      throw new IOException(CommonResourceBundle.getInstance().getString("message.integerMaxSize", new Object[] { Integer.valueOf(1048576) }));
    }
  }
  
  protected final void encodeNonZeroIntegerOnSecondBitFirstBitZero(int paramInt)
    throws IOException
  {
    if (paramInt < 64)
    {
      write(paramInt);
    }
    else if (paramInt < 8256)
    {
      paramInt -= 64;
      _b = (0x40 | paramInt >> 8);
      write(_b);
      write(paramInt & 0xFF);
    }
    else
    {
      paramInt -= 8256;
      _b = (0x60 | paramInt >> 16);
      write(_b);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    }
  }
  
  protected final void encodeNonZeroIntegerOnThirdBit(int paramInt)
    throws IOException
  {
    if (paramInt < 32)
    {
      write(_b | paramInt);
    }
    else if (paramInt < 2080)
    {
      paramInt -= 32;
      _b |= 0x20 | paramInt >> 8;
      write(_b);
      write(paramInt & 0xFF);
    }
    else if (paramInt < 526368)
    {
      paramInt -= 2080;
      _b |= 0x28 | paramInt >> 16;
      write(_b);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    }
    else
    {
      paramInt -= 526368;
      _b |= 0x30;
      write(_b);
      write(paramInt >> 16);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    }
  }
  
  protected final void encodeNonZeroIntegerOnFourthBit(int paramInt)
    throws IOException
  {
    if (paramInt < 16)
    {
      write(_b | paramInt);
    }
    else if (paramInt < 1040)
    {
      paramInt -= 16;
      _b |= 0x10 | paramInt >> 8;
      write(_b);
      write(paramInt & 0xFF);
    }
    else if (paramInt < 263184)
    {
      paramInt -= 1040;
      _b |= 0x14 | paramInt >> 16;
      write(_b);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    }
    else
    {
      paramInt -= 263184;
      _b |= 0x18;
      write(_b);
      write(paramInt >> 16);
      write(paramInt >> 8 & 0xFF);
      write(paramInt & 0xFF);
    }
  }
  
  protected final void encodeNonEmptyUTF8StringAsOctetString(int paramInt, String paramString, int[] paramArrayOfInt)
    throws IOException
  {
    char[] arrayOfChar = paramString.toCharArray();
    encodeNonEmptyUTF8StringAsOctetString(paramInt, arrayOfChar, 0, arrayOfChar.length, paramArrayOfInt);
  }
  
  protected final void encodeNonEmptyUTF8StringAsOctetString(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, int[] paramArrayOfInt)
    throws IOException
  {
    paramInt3 = encodeUTF8String(paramArrayOfChar, paramInt2, paramInt3);
    encodeNonZeroOctetStringLength(paramInt1, paramInt3, paramArrayOfInt);
    write(_encodingBuffer, paramInt3);
  }
  
  protected final void encodeNonZeroOctetStringLength(int paramInt1, int paramInt2, int[] paramArrayOfInt)
    throws IOException
  {
    if (paramInt2 < paramArrayOfInt[0])
    {
      write(paramInt1 | paramInt2 - 1);
    }
    else if (paramInt2 < paramArrayOfInt[1])
    {
      write(paramInt1 | paramArrayOfInt[2]);
      write(paramInt2 - paramArrayOfInt[0]);
    }
    else
    {
      write(paramInt1 | paramArrayOfInt[3]);
      paramInt2 -= paramArrayOfInt[1];
      write(paramInt2 >>> 24);
      write(paramInt2 >> 16 & 0xFF);
      write(paramInt2 >> 8 & 0xFF);
      write(paramInt2 & 0xFF);
    }
  }
  
  protected final void encodeNonZeroInteger(int paramInt1, int paramInt2, int[] paramArrayOfInt)
    throws IOException
  {
    if (paramInt2 < paramArrayOfInt[0])
    {
      write(paramInt1 | paramInt2);
    }
    else if (paramInt2 < paramArrayOfInt[1])
    {
      paramInt2 -= paramArrayOfInt[0];
      write(paramInt1 | paramArrayOfInt[3] | paramInt2 >> 8);
      write(paramInt2 & 0xFF);
    }
    else if (paramInt2 < paramArrayOfInt[2])
    {
      paramInt2 -= paramArrayOfInt[1];
      write(paramInt1 | paramArrayOfInt[4] | paramInt2 >> 16);
      write(paramInt2 >> 8 & 0xFF);
      write(paramInt2 & 0xFF);
    }
    else if (paramInt2 < 1048576)
    {
      paramInt2 -= paramArrayOfInt[2];
      write(paramInt1 | paramArrayOfInt[5]);
      write(paramInt2 >> 16);
      write(paramInt2 >> 8 & 0xFF);
      write(paramInt2 & 0xFF);
    }
    else
    {
      throw new IOException(CommonResourceBundle.getInstance().getString("message.integerMaxSize", new Object[] { Integer.valueOf(1048576) }));
    }
  }
  
  protected final void mark()
  {
    _markIndex = _octetBufferIndex;
  }
  
  protected final void resetMark()
  {
    _markIndex = -1;
  }
  
  protected final boolean hasMark()
  {
    return _markIndex != -1;
  }
  
  protected final void write(int paramInt)
    throws IOException
  {
    if (_octetBufferIndex < _octetBuffer.length)
    {
      _octetBuffer[(_octetBufferIndex++)] = ((byte)paramInt);
    }
    else if (_markIndex == -1)
    {
      _s.write(_octetBuffer);
      _octetBufferIndex = 1;
      _octetBuffer[0] = ((byte)paramInt);
    }
    else
    {
      resize(_octetBuffer.length * 3 / 2);
      _octetBuffer[(_octetBufferIndex++)] = ((byte)paramInt);
    }
  }
  
  protected final void write(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramInt);
  }
  
  protected final void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (_octetBufferIndex + paramInt2 < _octetBuffer.length)
    {
      System.arraycopy(paramArrayOfByte, paramInt1, _octetBuffer, _octetBufferIndex, paramInt2);
      _octetBufferIndex += paramInt2;
    }
    else if (_markIndex == -1)
    {
      _s.write(_octetBuffer, 0, _octetBufferIndex);
      _s.write(paramArrayOfByte, paramInt1, paramInt2);
      _octetBufferIndex = 0;
    }
    else
    {
      resize((_octetBuffer.length + paramInt2) * 3 / 2 + 1);
      System.arraycopy(paramArrayOfByte, paramInt1, _octetBuffer, _octetBufferIndex, paramInt2);
      _octetBufferIndex += paramInt2;
    }
  }
  
  private void ensureSize(int paramInt)
  {
    if (_octetBufferIndex + paramInt > _octetBuffer.length) {
      resize((_octetBufferIndex + paramInt) * 3 / 2 + 1);
    }
  }
  
  private void resize(int paramInt)
  {
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(_octetBuffer, 0, arrayOfByte, 0, _octetBufferIndex);
    _octetBuffer = arrayOfByte;
  }
  
  private void _flush()
    throws IOException
  {
    if (_octetBufferIndex > 0)
    {
      _s.write(_octetBuffer, 0, _octetBufferIndex);
      _octetBufferIndex = 0;
    }
  }
  
  protected final int encodeUTF8String(String paramString)
    throws IOException
  {
    int i = paramString.length();
    if (i < _charBuffer.length)
    {
      paramString.getChars(0, i, _charBuffer, 0);
      return encodeUTF8String(_charBuffer, 0, i);
    }
    char[] arrayOfChar = paramString.toCharArray();
    return encodeUTF8String(arrayOfChar, 0, i);
  }
  
  private void ensureEncodingBufferSizeForUtf8String(int paramInt)
  {
    int i = 4 * paramInt;
    if (_encodingBuffer.length < i) {
      _encodingBuffer = new byte[i];
    }
  }
  
  protected final int encodeUTF8String(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    ensureEncodingBufferSizeForUtf8String(paramInt2);
    int j = paramInt1 + paramInt2;
    while (j != paramInt1)
    {
      int k = paramArrayOfChar[(paramInt1++)];
      if (k < 128)
      {
        _encodingBuffer[(i++)] = ((byte)k);
      }
      else if (k < 2048)
      {
        _encodingBuffer[(i++)] = ((byte)(0xC0 | k >> 6));
        _encodingBuffer[(i++)] = ((byte)(0x80 | k & 0x3F));
      }
      else if (k <= 65535)
      {
        if ((!XMLChar.isHighSurrogate(k)) && (!XMLChar.isLowSurrogate(k)))
        {
          _encodingBuffer[(i++)] = ((byte)(0xE0 | k >> 12));
          _encodingBuffer[(i++)] = ((byte)(0x80 | k >> 6 & 0x3F));
          _encodingBuffer[(i++)] = ((byte)(0x80 | k & 0x3F));
        }
        else
        {
          encodeCharacterAsUtf8FourByte(k, paramArrayOfChar, paramInt1, j, i);
          i += 4;
          paramInt1++;
        }
      }
    }
    return i;
  }
  
  private void encodeCharacterAsUtf8FourByte(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, int paramInt4)
    throws IOException
  {
    if (paramInt2 == paramInt3) {
      throw new IOException("");
    }
    int i = paramArrayOfChar[paramInt2];
    if (!XMLChar.isLowSurrogate(i)) {
      throw new IOException("");
    }
    int j = ((paramInt1 & 0x3FF) << 10 | i & 0x3FF) + 65536;
    if ((j < 0) || (j >= 2097152)) {
      throw new IOException("");
    }
    _encodingBuffer[(paramInt4++)] = ((byte)(0xF0 | j >> 18));
    _encodingBuffer[(paramInt4++)] = ((byte)(0x80 | j >> 12 & 0x3F));
    _encodingBuffer[(paramInt4++)] = ((byte)(0x80 | j >> 6 & 0x3F));
    _encodingBuffer[(paramInt4++)] = ((byte)(0x80 | j & 0x3F));
  }
  
  protected final int encodeUtf16String(String paramString)
    throws IOException
  {
    int i = paramString.length();
    if (i < _charBuffer.length)
    {
      paramString.getChars(0, i, _charBuffer, 0);
      return encodeUtf16String(_charBuffer, 0, i);
    }
    char[] arrayOfChar = paramString.toCharArray();
    return encodeUtf16String(arrayOfChar, 0, i);
  }
  
  private void ensureEncodingBufferSizeForUtf16String(int paramInt)
  {
    int i = 2 * paramInt;
    if (_encodingBuffer.length < i) {
      _encodingBuffer = new byte[i];
    }
  }
  
  protected final int encodeUtf16String(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    ensureEncodingBufferSizeForUtf16String(paramInt2);
    int j = paramInt1 + paramInt2;
    for (int k = paramInt1; k < j; k++)
    {
      int m = paramArrayOfChar[k];
      _encodingBuffer[(i++)] = ((byte)(m >> 8));
      _encodingBuffer[(i++)] = ((byte)(m & 0xFF));
    }
    return i;
  }
  
  public static String getPrefixFromQualifiedName(String paramString)
  {
    int i = paramString.indexOf(':');
    String str = "";
    if (i != -1) {
      str = paramString.substring(0, i);
    }
    return str;
  }
  
  public static boolean isWhiteSpace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (!XMLChar.isSpace(paramArrayOfChar[paramInt1])) {
      return false;
    }
    int i = paramInt1 + paramInt2;
    do
    {
      paramInt1++;
    } while ((paramInt1 < i) && (XMLChar.isSpace(paramArrayOfChar[paramInt1])));
    return paramInt1 == i;
  }
  
  public static boolean isWhiteSpace(String paramString)
  {
    if (!XMLChar.isSpace(paramString.charAt(0))) {
      return false;
    }
    int i = paramString.length();
    int j = 1;
    while ((j < i) && (XMLChar.isSpace(paramString.charAt(j++)))) {}
    return j == i;
  }
  
  static
  {
    for (int i = 0; i < NUMERIC_CHARACTERS_TABLE.length; i++) {
      NUMERIC_CHARACTERS_TABLE[i] = -1;
    }
    for (i = 0; i < DATE_TIME_CHARACTERS_TABLE.length; i++) {
      DATE_TIME_CHARACTERS_TABLE[i] = -1;
    }
    for (i = 0; i < "0123456789-+.E ".length(); i++) {
      NUMERIC_CHARACTERS_TABLE["0123456789-+.E ".charAt(i)] = i;
    }
    for (i = 0; i < "0123456789-:TZ ".length(); i++) {
      DATE_TIME_CHARACTERS_TABLE["0123456789-:TZ ".charAt(i)] = i;
    }
  }
  
  private class EncodingBufferOutputStream
    extends OutputStream
  {
    private EncodingBufferOutputStream() {}
    
    public void write(int paramInt)
      throws IOException
    {
      if (_encodingBufferIndex < _encodingBuffer.length)
      {
        _encodingBuffer[Encoder.access$108(Encoder.this)] = ((byte)paramInt);
      }
      else
      {
        byte[] arrayOfByte = new byte[Math.max(_encodingBuffer.length << 1, _encodingBufferIndex)];
        System.arraycopy(_encodingBuffer, 0, arrayOfByte, 0, _encodingBufferIndex);
        _encodingBuffer = arrayOfByte;
        _encodingBuffer[Encoder.access$108(Encoder.this)] = ((byte)paramInt);
      }
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0)) {
        throw new IndexOutOfBoundsException();
      }
      if (paramInt2 == 0) {
        return;
      }
      int i = _encodingBufferIndex + paramInt2;
      if (i > _encodingBuffer.length)
      {
        byte[] arrayOfByte = new byte[Math.max(_encodingBuffer.length << 1, i)];
        System.arraycopy(_encodingBuffer, 0, arrayOfByte, 0, _encodingBufferIndex);
        _encodingBuffer = arrayOfByte;
      }
      System.arraycopy(paramArrayOfByte, paramInt1, _encodingBuffer, _encodingBufferIndex, paramInt2);
      _encodingBufferIndex = i;
    }
    
    public int getLength()
    {
      return _encodingBufferIndex;
    }
    
    public void reset()
    {
      _encodingBufferIndex = 0;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\Encoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */