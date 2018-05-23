package com.sun.xml.internal.fastinfoset.sax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.Encoder;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap.Entry;
import com.sun.xml.internal.fastinfoset.vocab.SerializerVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.FastInfosetWriter;
import java.io.IOException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SAXDocumentSerializer
  extends Encoder
  implements FastInfosetWriter
{
  protected boolean _elementHasNamespaces = false;
  protected boolean _charactersAsCDATA = false;
  
  protected SAXDocumentSerializer(boolean paramBoolean)
  {
    super(paramBoolean);
  }
  
  public SAXDocumentSerializer() {}
  
  public void reset()
  {
    super.reset();
    _elementHasNamespaces = false;
    _charactersAsCDATA = false;
  }
  
  public final void startDocument()
    throws SAXException
  {
    try
    {
      reset();
      encodeHeader(false);
      encodeInitialVocabulary();
    }
    catch (IOException localIOException)
    {
      throw new SAXException("startDocument", localIOException);
    }
  }
  
  public final void endDocument()
    throws SAXException
  {
    try
    {
      encodeDocumentTermination();
    }
    catch (IOException localIOException)
    {
      throw new SAXException("endDocument", localIOException);
    }
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    try
    {
      if (!_elementHasNamespaces)
      {
        encodeTermination();
        mark();
        _elementHasNamespaces = true;
        write(56);
      }
      encodeNamespaceAttribute(paramString1, paramString2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException("startElement", localIOException);
    }
  }
  
  public final void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    int i = (paramAttributes != null) && (paramAttributes.getLength() > 0) ? countAttributes(paramAttributes) : 0;
    try
    {
      if (_elementHasNamespaces)
      {
        _elementHasNamespaces = false;
        if (i > 0)
        {
          int tmp52_49 = _markIndex;
          byte[] tmp52_45 = _octetBuffer;
          tmp52_45[tmp52_49] = ((byte)(tmp52_45[tmp52_49] | 0x40));
        }
        resetMark();
        write(240);
        _b = 0;
      }
      else
      {
        encodeTermination();
        _b = 0;
        if (i > 0) {
          _b |= 0x40;
        }
      }
      encodeElement(paramString1, paramString3, paramString2);
      if (i > 0) {
        encodeAttributes(paramAttributes);
      }
    }
    catch (IOException localIOException)
    {
      throw new SAXException("startElement", localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new SAXException("startElement", localFastInfosetException);
    }
  }
  
  public final void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    try
    {
      encodeElementTermination();
    }
    catch (IOException localIOException)
    {
      throw new SAXException("endElement", localIOException);
    }
  }
  
  public final void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (paramInt2 <= 0) {
      return;
    }
    if ((getIgnoreWhiteSpaceTextContent()) && (isWhiteSpace(paramArrayOfChar, paramInt1, paramInt2))) {
      return;
    }
    try
    {
      encodeTermination();
      if (!_charactersAsCDATA) {
        encodeCharacters(paramArrayOfChar, paramInt1, paramInt2);
      } else {
        encodeCIIBuiltInAlgorithmDataAsCDATA(paramArrayOfChar, paramInt1, paramInt2);
      }
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new SAXException(localFastInfosetException);
    }
  }
  
  public final void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (getIgnoreWhiteSpaceTextContent()) {
      return;
    }
    characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public final void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    try
    {
      if (getIgnoreProcesingInstructions()) {
        return;
      }
      if (paramString1.length() == 0) {
        throw new SAXException(CommonResourceBundle.getInstance().getString("message.processingInstructionTargetIsEmpty"));
      }
      encodeTermination();
      encodeProcessingInstruction(paramString1, paramString2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException("processingInstruction", localIOException);
    }
  }
  
  public final void setDocumentLocator(Locator paramLocator) {}
  
  public final void skippedEntity(String paramString)
    throws SAXException
  {}
  
  public final void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    try
    {
      if (getIgnoreComments()) {
        return;
      }
      encodeTermination();
      encodeComment(paramArrayOfChar, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException("startElement", localIOException);
    }
  }
  
  public final void startCDATA()
    throws SAXException
  {
    _charactersAsCDATA = true;
  }
  
  public final void endCDATA()
    throws SAXException
  {
    _charactersAsCDATA = false;
  }
  
  public final void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (getIgnoreDTD()) {
      return;
    }
    try
    {
      encodeTermination();
      encodeDocumentTypeDeclaration(paramString2, paramString3);
      encodeElementTermination();
    }
    catch (IOException localIOException)
    {
      throw new SAXException("startDTD", localIOException);
    }
  }
  
  public final void endDTD()
    throws SAXException
  {}
  
  public final void startEntity(String paramString)
    throws SAXException
  {}
  
  public final void endEntity(String paramString)
    throws SAXException
  {}
  
  public final void octets(String paramString, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    throws SAXException
  {
    if (paramInt3 <= 0) {
      return;
    }
    try
    {
      encodeTermination();
      encodeNonIdentifyingStringOnThirdBit(paramString, paramInt1, paramArrayOfByte, paramInt2, paramInt3);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new SAXException(localFastInfosetException);
    }
  }
  
  public final void object(String paramString, int paramInt, Object paramObject)
    throws SAXException
  {
    try
    {
      encodeTermination();
      encodeNonIdentifyingStringOnThirdBit(paramString, paramInt, paramObject);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new SAXException(localFastInfosetException);
    }
  }
  
  public final void bytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (paramInt2 <= 0) {
      return;
    }
    try
    {
      encodeTermination();
      encodeCIIOctetAlgorithmData(1, paramArrayOfByte, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
  }
  
  public final void shorts(short[] paramArrayOfShort, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (paramInt2 <= 0) {
      return;
    }
    try
    {
      encodeTermination();
      encodeCIIBuiltInAlgorithmData(2, paramArrayOfShort, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new SAXException(localFastInfosetException);
    }
  }
  
  public final void ints(int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (paramInt2 <= 0) {
      return;
    }
    try
    {
      encodeTermination();
      encodeCIIBuiltInAlgorithmData(3, paramArrayOfInt, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new SAXException(localFastInfosetException);
    }
  }
  
  public final void longs(long[] paramArrayOfLong, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (paramInt2 <= 0) {
      return;
    }
    try
    {
      encodeTermination();
      encodeCIIBuiltInAlgorithmData(4, paramArrayOfLong, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new SAXException(localFastInfosetException);
    }
  }
  
  public final void booleans(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (paramInt2 <= 0) {
      return;
    }
    try
    {
      encodeTermination();
      encodeCIIBuiltInAlgorithmData(5, paramArrayOfBoolean, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new SAXException(localFastInfosetException);
    }
  }
  
  public final void floats(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (paramInt2 <= 0) {
      return;
    }
    try
    {
      encodeTermination();
      encodeCIIBuiltInAlgorithmData(6, paramArrayOfFloat, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new SAXException(localFastInfosetException);
    }
  }
  
  public final void doubles(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (paramInt2 <= 0) {
      return;
    }
    try
    {
      encodeTermination();
      encodeCIIBuiltInAlgorithmData(7, paramArrayOfDouble, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new SAXException(localFastInfosetException);
    }
  }
  
  public void uuids(long[] paramArrayOfLong, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (paramInt2 <= 0) {
      return;
    }
    try
    {
      encodeTermination();
      encodeCIIBuiltInAlgorithmData(8, paramArrayOfLong, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new SAXException(localFastInfosetException);
    }
  }
  
  public void numericCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (paramInt2 <= 0) {
      return;
    }
    try
    {
      encodeTermination();
      boolean bool = isCharacterContentChunkLengthMatchesLimit(paramInt2);
      encodeNumericFourBitCharacters(paramArrayOfChar, paramInt1, paramInt2, bool);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new SAXException(localFastInfosetException);
    }
  }
  
  public void dateTimeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (paramInt2 <= 0) {
      return;
    }
    try
    {
      encodeTermination();
      boolean bool = isCharacterContentChunkLengthMatchesLimit(paramInt2);
      encodeDateTimeFourBitCharacters(paramArrayOfChar, paramInt1, paramInt2, bool);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new SAXException(localFastInfosetException);
    }
  }
  
  public void alphabetCharacters(String paramString, char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (paramInt2 <= 0) {
      return;
    }
    try
    {
      encodeTermination();
      boolean bool = isCharacterContentChunkLengthMatchesLimit(paramInt2);
      encodeAlphabetCharacters(paramString, paramArrayOfChar, paramInt1, paramInt2, bool);
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new SAXException(localFastInfosetException);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean)
    throws SAXException
  {
    if (paramInt2 <= 0) {
      return;
    }
    if ((getIgnoreWhiteSpaceTextContent()) && (isWhiteSpace(paramArrayOfChar, paramInt1, paramInt2))) {
      return;
    }
    try
    {
      encodeTermination();
      if (!_charactersAsCDATA) {
        encodeNonIdentifyingStringOnThirdBit(paramArrayOfChar, paramInt1, paramInt2, _v.characterContentChunk, paramBoolean, true);
      } else {
        encodeCIIBuiltInAlgorithmDataAsCDATA(paramArrayOfChar, paramInt1, paramInt2);
      }
    }
    catch (IOException localIOException)
    {
      throw new SAXException(localIOException);
    }
    catch (FastInfosetException localFastInfosetException)
    {
      throw new SAXException(localFastInfosetException);
    }
  }
  
  protected final int countAttributes(Attributes paramAttributes)
  {
    int i = 0;
    for (int j = 0; j < paramAttributes.getLength(); j++)
    {
      String str = paramAttributes.getURI(j);
      if ((str != "http://www.w3.org/2000/xmlns/") && (!str.equals("http://www.w3.org/2000/xmlns/"))) {
        i++;
      }
    }
    return i;
  }
  
  protected void encodeAttributes(Attributes paramAttributes)
    throws IOException, FastInfosetException
  {
    String str1;
    boolean bool1;
    if ((paramAttributes instanceof EncodingAlgorithmAttributes))
    {
      EncodingAlgorithmAttributes localEncodingAlgorithmAttributes = (EncodingAlgorithmAttributes)paramAttributes;
      for (int j = 0; j < localEncodingAlgorithmAttributes.getLength(); j++) {
        if (encodeAttribute(paramAttributes.getURI(j), paramAttributes.getQName(j), paramAttributes.getLocalName(j)))
        {
          Object localObject = localEncodingAlgorithmAttributes.getAlgorithmData(j);
          if (localObject == null)
          {
            str1 = localEncodingAlgorithmAttributes.getValue(j);
            bool1 = isAttributeValueLengthMatchesLimit(str1.length());
            boolean bool2 = localEncodingAlgorithmAttributes.getToIndex(j);
            String str2 = localEncodingAlgorithmAttributes.getAlpababet(j);
            if (str2 == null) {
              encodeNonIdentifyingStringOnFirstBit(str1, _v.attributeValue, bool1, bool2);
            } else if (str2 == "0123456789-:TZ ") {
              encodeDateTimeNonIdentifyingStringOnFirstBit(str1, bool1, bool2);
            } else if (str2 == "0123456789-+.E ") {
              encodeNumericNonIdentifyingStringOnFirstBit(str1, bool1, bool2);
            } else {
              encodeNonIdentifyingStringOnFirstBit(str1, _v.attributeValue, bool1, bool2);
            }
          }
          else
          {
            encodeNonIdentifyingStringOnFirstBit(localEncodingAlgorithmAttributes.getAlgorithmURI(j), localEncodingAlgorithmAttributes.getAlgorithmIndex(j), localObject);
          }
        }
      }
    }
    else
    {
      for (int i = 0; i < paramAttributes.getLength(); i++) {
        if (encodeAttribute(paramAttributes.getURI(i), paramAttributes.getQName(i), paramAttributes.getLocalName(i)))
        {
          str1 = paramAttributes.getValue(i);
          bool1 = isAttributeValueLengthMatchesLimit(str1.length());
          encodeNonIdentifyingStringOnFirstBit(str1, _v.attributeValue, bool1, false);
        }
      }
    }
    _b = 240;
    _terminate = true;
  }
  
  protected void encodeElement(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    LocalNameQualifiedNamesMap.Entry localEntry = _v.elementName.obtainEntry(paramString2);
    if (_valueIndex > 0)
    {
      QualifiedName[] arrayOfQualifiedName = _value;
      for (int i = 0; i < _valueIndex; i++)
      {
        QualifiedName localQualifiedName = arrayOfQualifiedName[i];
        if ((paramString1 == namespaceName) || (paramString1.equals(namespaceName)))
        {
          encodeNonZeroIntegerOnThirdBit(index);
          return;
        }
      }
    }
    encodeLiteralElementQualifiedNameOnThirdBit(paramString1, getPrefixFromQualifiedName(paramString2), paramString3, localEntry);
  }
  
  protected boolean encodeAttribute(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    LocalNameQualifiedNamesMap.Entry localEntry = _v.attributeName.obtainEntry(paramString2);
    if (_valueIndex > 0)
    {
      QualifiedName[] arrayOfQualifiedName = _value;
      for (int i = 0; i < _valueIndex; i++) {
        if ((paramString1 == namespaceName) || (paramString1.equals(namespaceName)))
        {
          encodeNonZeroIntegerOnSecondBitFirstBitZero(index);
          return true;
        }
      }
    }
    return encodeLiteralAttributeQualifiedNameOnSecondBit(paramString1, getPrefixFromQualifiedName(paramString2), paramString3, localEntry);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\sax\SAXDocumentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */