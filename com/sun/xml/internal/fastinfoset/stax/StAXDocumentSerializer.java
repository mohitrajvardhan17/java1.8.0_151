package com.sun.xml.internal.fastinfoset.stax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.Encoder;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap.Entry;
import com.sun.xml.internal.fastinfoset.util.NamespaceContextImplementation;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import com.sun.xml.internal.fastinfoset.vocab.SerializerVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.stax.LowLevelFastInfosetStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EmptyStackException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StAXDocumentSerializer
  extends Encoder
  implements XMLStreamWriter, LowLevelFastInfosetStreamWriter
{
  protected StAXManager _manager;
  protected String _encoding;
  protected String _currentLocalName;
  protected String _currentUri;
  protected String _currentPrefix;
  protected boolean _inStartElement = false;
  protected boolean _isEmptyElement = false;
  protected String[] _attributesArray = new String[64];
  protected int _attributesArrayIndex = 0;
  protected boolean[] _nsSupportContextStack = new boolean[32];
  protected int _stackCount = -1;
  protected NamespaceContextImplementation _nsContext = new NamespaceContextImplementation();
  protected String[] _namespacesArray = new String[16];
  protected int _namespacesArrayIndex = 0;
  
  public StAXDocumentSerializer()
  {
    super(true);
    _manager = new StAXManager(2);
  }
  
  public StAXDocumentSerializer(OutputStream paramOutputStream)
  {
    super(true);
    setOutputStream(paramOutputStream);
    _manager = new StAXManager(2);
  }
  
  public StAXDocumentSerializer(OutputStream paramOutputStream, StAXManager paramStAXManager)
  {
    super(true);
    setOutputStream(paramOutputStream);
    _manager = paramStAXManager;
  }
  
  public void reset()
  {
    super.reset();
    _attributesArrayIndex = 0;
    _namespacesArrayIndex = 0;
    _nsContext.reset();
    _stackCount = -1;
    _currentUri = (_currentPrefix = null);
    _currentLocalName = null;
    _inStartElement = (_isEmptyElement = 0);
  }
  
  public void writeStartDocument()
    throws XMLStreamException
  {
    writeStartDocument("finf", "1.0");
  }
  
  public void writeStartDocument(String paramString)
    throws XMLStreamException
  {
    writeStartDocument("finf", paramString);
  }
  
  public void writeStartDocument(String paramString1, String paramString2)
    throws XMLStreamException
  {
    reset();
    try
    {
      encodeHeader(false);
      encodeInitialVocabulary();
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeEndDocument()
    throws XMLStreamException
  {
    try
    {
      while (_stackCount >= 0)
      {
        writeEndElement();
        _stackCount -= 1;
      }
      encodeDocumentTermination();
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void close()
    throws XMLStreamException
  {
    reset();
  }
  
  public void flush()
    throws XMLStreamException
  {
    try
    {
      _s.flush();
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeStartElement(String paramString)
    throws XMLStreamException
  {
    writeStartElement("", paramString, "");
  }
  
  public void writeStartElement(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writeStartElement("", paramString2, paramString1);
  }
  
  public void writeStartElement(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    encodeTerminationAndCurrentElement(false);
    _inStartElement = true;
    _isEmptyElement = false;
    _currentLocalName = paramString2;
    _currentPrefix = paramString1;
    _currentUri = paramString3;
    _stackCount += 1;
    if (_stackCount == _nsSupportContextStack.length)
    {
      boolean[] arrayOfBoolean = new boolean[_stackCount * 2];
      System.arraycopy(_nsSupportContextStack, 0, arrayOfBoolean, 0, _nsSupportContextStack.length);
      _nsSupportContextStack = arrayOfBoolean;
    }
    _nsSupportContextStack[_stackCount] = false;
  }
  
  public void writeEmptyElement(String paramString)
    throws XMLStreamException
  {
    writeEmptyElement("", paramString, "");
  }
  
  public void writeEmptyElement(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writeEmptyElement("", paramString2, paramString1);
  }
  
  public void writeEmptyElement(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    encodeTerminationAndCurrentElement(false);
    _isEmptyElement = (_inStartElement = 1);
    _currentLocalName = paramString2;
    _currentPrefix = paramString1;
    _currentUri = paramString3;
    _stackCount += 1;
    if (_stackCount == _nsSupportContextStack.length)
    {
      boolean[] arrayOfBoolean = new boolean[_stackCount * 2];
      System.arraycopy(_nsSupportContextStack, 0, arrayOfBoolean, 0, _nsSupportContextStack.length);
      _nsSupportContextStack = arrayOfBoolean;
    }
    _nsSupportContextStack[_stackCount] = false;
  }
  
  public void writeEndElement()
    throws XMLStreamException
  {
    if (_inStartElement) {
      encodeTerminationAndCurrentElement(false);
    }
    try
    {
      encodeElementTermination();
      if (_nsSupportContextStack[(_stackCount--)] == 1) {
        _nsContext.popContext();
      }
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
    catch (EmptyStackException localEmptyStackException)
    {
      throw new XMLStreamException(localEmptyStackException);
    }
  }
  
  public void writeAttribute(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writeAttribute("", "", paramString1, paramString2);
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    String str = "";
    if (paramString1.length() > 0)
    {
      str = _nsContext.getNonDefaultPrefix(paramString1);
      if ((str == null) || (str.length() == 0))
      {
        if ((paramString1 == "http://www.w3.org/2000/xmlns/") || (paramString1.equals("http://www.w3.org/2000/xmlns/"))) {
          return;
        }
        throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.URIUnbound", new Object[] { paramString1 }));
      }
    }
    writeAttribute(str, paramString1, paramString2, paramString3);
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMLStreamException
  {
    if (!_inStartElement) {
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.attributeWritingNotAllowed"));
    }
    if ((paramString2 == "http://www.w3.org/2000/xmlns/") || (paramString2.equals("http://www.w3.org/2000/xmlns/"))) {
      return;
    }
    if (_attributesArrayIndex == _attributesArray.length)
    {
      String[] arrayOfString = new String[_attributesArrayIndex * 2];
      System.arraycopy(_attributesArray, 0, arrayOfString, 0, _attributesArrayIndex);
      _attributesArray = arrayOfString;
    }
    _attributesArray[(_attributesArrayIndex++)] = paramString2;
    _attributesArray[(_attributesArrayIndex++)] = paramString1;
    _attributesArray[(_attributesArrayIndex++)] = paramString3;
    _attributesArray[(_attributesArrayIndex++)] = paramString4;
  }
  
  public void writeNamespace(String paramString1, String paramString2)
    throws XMLStreamException
  {
    if ((paramString1 == null) || (paramString1.length() == 0) || (paramString1.equals("xmlns")))
    {
      writeDefaultNamespace(paramString2);
    }
    else
    {
      if (!_inStartElement) {
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.attributeWritingNotAllowed"));
      }
      if (_namespacesArrayIndex == _namespacesArray.length)
      {
        String[] arrayOfString = new String[_namespacesArrayIndex * 2];
        System.arraycopy(_namespacesArray, 0, arrayOfString, 0, _namespacesArrayIndex);
        _namespacesArray = arrayOfString;
      }
      _namespacesArray[(_namespacesArrayIndex++)] = paramString1;
      _namespacesArray[(_namespacesArrayIndex++)] = paramString2;
      setPrefix(paramString1, paramString2);
    }
  }
  
  public void writeDefaultNamespace(String paramString)
    throws XMLStreamException
  {
    if (!_inStartElement) {
      throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.attributeWritingNotAllowed"));
    }
    if (_namespacesArrayIndex == _namespacesArray.length)
    {
      String[] arrayOfString = new String[_namespacesArrayIndex * 2];
      System.arraycopy(_namespacesArray, 0, arrayOfString, 0, _namespacesArrayIndex);
      _namespacesArray = arrayOfString;
    }
    _namespacesArray[(_namespacesArrayIndex++)] = "";
    _namespacesArray[(_namespacesArrayIndex++)] = paramString;
    setPrefix("", paramString);
  }
  
  public void writeComment(String paramString)
    throws XMLStreamException
  {
    try
    {
      if (getIgnoreComments()) {
        return;
      }
      encodeTerminationAndCurrentElement(true);
      encodeComment(paramString.toCharArray(), 0, paramString.length());
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeProcessingInstruction(String paramString)
    throws XMLStreamException
  {
    writeProcessingInstruction(paramString, "");
  }
  
  public void writeProcessingInstruction(String paramString1, String paramString2)
    throws XMLStreamException
  {
    try
    {
      if (getIgnoreProcesingInstructions()) {
        return;
      }
      encodeTerminationAndCurrentElement(true);
      encodeProcessingInstruction(paramString1, paramString2);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeCData(String paramString)
    throws XMLStreamException
  {
    try
    {
      int i = paramString.length();
      if (i == 0) {
        return;
      }
      if (i < _charBuffer.length)
      {
        if ((getIgnoreWhiteSpaceTextContent()) && (isWhiteSpace(paramString))) {
          return;
        }
        encodeTerminationAndCurrentElement(true);
        paramString.getChars(0, i, _charBuffer, 0);
        encodeCIIBuiltInAlgorithmDataAsCDATA(_charBuffer, 0, i);
      }
      else
      {
        char[] arrayOfChar = paramString.toCharArray();
        if ((getIgnoreWhiteSpaceTextContent()) && (isWhiteSpace(arrayOfChar, 0, i))) {
          return;
        }
        encodeTerminationAndCurrentElement(true);
        encodeCIIBuiltInAlgorithmDataAsCDATA(arrayOfChar, 0, i);
      }
    }
    catch (Exception localException)
    {
      throw new XMLStreamException(localException);
    }
  }
  
  public void writeDTD(String paramString)
    throws XMLStreamException
  {
    throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.notImplemented"));
  }
  
  public void writeEntityRef(String paramString)
    throws XMLStreamException
  {
    throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.notImplemented"));
  }
  
  public void writeCharacters(String paramString)
    throws XMLStreamException
  {
    try
    {
      int i = paramString.length();
      if (i == 0) {
        return;
      }
      if (i < _charBuffer.length)
      {
        if ((getIgnoreWhiteSpaceTextContent()) && (isWhiteSpace(paramString))) {
          return;
        }
        encodeTerminationAndCurrentElement(true);
        paramString.getChars(0, i, _charBuffer, 0);
        encodeCharacters(_charBuffer, 0, i);
      }
      else
      {
        char[] arrayOfChar = paramString.toCharArray();
        if ((getIgnoreWhiteSpaceTextContent()) && (isWhiteSpace(arrayOfChar, 0, i))) {
          return;
        }
        encodeTerminationAndCurrentElement(true);
        encodeCharactersNoClone(arrayOfChar, 0, i);
      }
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws XMLStreamException
  {
    try
    {
      if (paramInt2 <= 0) {
        return;
      }
      if ((getIgnoreWhiteSpaceTextContent()) && (isWhiteSpace(paramArrayOfChar, paramInt1, paramInt2))) {
        return;
      }
      encodeTerminationAndCurrentElement(true);
      encodeCharacters(paramArrayOfChar, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public String getPrefix(String paramString)
    throws XMLStreamException
  {
    return _nsContext.getPrefix(paramString);
  }
  
  public void setPrefix(String paramString1, String paramString2)
    throws XMLStreamException
  {
    if ((_stackCount > -1) && (_nsSupportContextStack[_stackCount] == 0))
    {
      _nsSupportContextStack[_stackCount] = true;
      _nsContext.pushContext();
    }
    _nsContext.declarePrefix(paramString1, paramString2);
  }
  
  public void setDefaultNamespace(String paramString)
    throws XMLStreamException
  {
    setPrefix("", paramString);
  }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext)
    throws XMLStreamException
  {
    throw new UnsupportedOperationException("setNamespaceContext");
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return _nsContext;
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    if (_manager != null) {
      return _manager.getProperty(paramString);
    }
    return null;
  }
  
  public void setManager(StAXManager paramStAXManager)
  {
    _manager = paramStAXManager;
  }
  
  public void setEncoding(String paramString)
  {
    _encoding = paramString;
  }
  
  public void writeOctets(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws XMLStreamException
  {
    try
    {
      if (paramInt2 == 0) {
        return;
      }
      encodeTerminationAndCurrentElement(true);
      encodeCIIOctetAlgorithmData(1, paramArrayOfByte, paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  protected void encodeTerminationAndCurrentElement(boolean paramBoolean)
    throws XMLStreamException
  {
    try
    {
      encodeTermination();
      if (_inStartElement)
      {
        _b = 0;
        if (_attributesArrayIndex > 0) {
          _b |= 0x40;
        }
        if (_namespacesArrayIndex > 0)
        {
          write(_b | 0x38);
          int i = 0;
          while (i < _namespacesArrayIndex) {
            encodeNamespaceAttribute(_namespacesArray[(i++)], _namespacesArray[(i++)]);
          }
          _namespacesArrayIndex = 0;
          write(240);
          _b = 0;
        }
        if (_currentPrefix.length() == 0) {
          if (_currentUri.length() == 0)
          {
            _currentUri = _nsContext.getNamespaceURI("");
          }
          else
          {
            String str1 = getPrefix(_currentUri);
            if (str1 != null) {
              _currentPrefix = str1;
            }
          }
        }
        encodeElementQualifiedNameOnThirdBit(_currentUri, _currentPrefix, _currentLocalName);
        int j = 0;
        while (j < _attributesArrayIndex)
        {
          encodeAttributeQualifiedNameOnSecondBit(_attributesArray[(j++)], _attributesArray[(j++)], _attributesArray[(j++)]);
          String str2 = _attributesArray[j];
          _attributesArray[(j++)] = null;
          boolean bool = isAttributeValueLengthMatchesLimit(str2.length());
          encodeNonIdentifyingStringOnFirstBit(str2, _v.attributeValue, bool, false);
          _b = 240;
          _terminate = true;
        }
        _attributesArrayIndex = 0;
        _inStartElement = false;
        if (_isEmptyElement)
        {
          encodeElementTermination();
          if (_nsSupportContextStack[(_stackCount--)] == 1) {
            _nsContext.popContext();
          }
          _isEmptyElement = false;
        }
        if (paramBoolean) {
          encodeTermination();
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
  }
  
  public final void initiateLowLevelWriting()
    throws XMLStreamException
  {
    encodeTerminationAndCurrentElement(false);
  }
  
  public final int getNextElementIndex()
  {
    return _v.elementName.getNextIndex();
  }
  
  public final int getNextAttributeIndex()
  {
    return _v.attributeName.getNextIndex();
  }
  
  public final int getLocalNameIndex()
  {
    return _v.localName.getIndex();
  }
  
  public final int getNextLocalNameIndex()
  {
    return _v.localName.getNextIndex();
  }
  
  public final void writeLowLevelTerminationAndMark()
    throws IOException
  {
    encodeTermination();
    mark();
  }
  
  public final void writeLowLevelStartElementIndexed(int paramInt1, int paramInt2)
    throws IOException
  {
    _b = paramInt1;
    encodeNonZeroIntegerOnThirdBit(paramInt2);
  }
  
  public final boolean writeLowLevelStartElement(int paramInt, String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    boolean bool = encodeElement(paramInt, paramString3, paramString1, paramString2);
    if (!bool) {
      encodeLiteral(paramInt | 0x3C, paramString3, paramString1, paramString2);
    }
    return bool;
  }
  
  public final void writeLowLevelStartNamespaces()
    throws IOException
  {
    write(56);
  }
  
  public final void writeLowLevelNamespace(String paramString1, String paramString2)
    throws IOException
  {
    encodeNamespaceAttribute(paramString1, paramString2);
  }
  
  public final void writeLowLevelEndNamespaces()
    throws IOException
  {
    write(240);
  }
  
  public final void writeLowLevelStartAttributes()
    throws IOException
  {
    if (hasMark())
    {
      int tmp15_12 = _markIndex;
      byte[] tmp15_8 = _octetBuffer;
      tmp15_8[tmp15_12] = ((byte)(tmp15_8[tmp15_12] | 0x40));
      resetMark();
    }
  }
  
  public final void writeLowLevelAttributeIndexed(int paramInt)
    throws IOException
  {
    encodeNonZeroIntegerOnSecondBitFirstBitZero(paramInt);
  }
  
  public final boolean writeLowLevelAttribute(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    boolean bool = encodeAttribute(paramString2, paramString1, paramString3);
    if (!bool) {
      encodeLiteral(120, paramString2, paramString1, paramString3);
    }
    return bool;
  }
  
  public final void writeLowLevelAttributeValue(String paramString)
    throws IOException
  {
    boolean bool = isAttributeValueLengthMatchesLimit(paramString.length());
    encodeNonIdentifyingStringOnFirstBit(paramString, _v.attributeValue, bool, false);
  }
  
  public final void writeLowLevelStartNameLiteral(int paramInt, String paramString1, byte[] paramArrayOfByte, String paramString2)
    throws IOException
  {
    encodeLiteralHeader(paramInt, paramString2, paramString1);
    encodeNonZeroOctetStringLengthOnSecondBit(paramArrayOfByte.length);
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public final void writeLowLevelStartNameLiteral(int paramInt1, String paramString1, int paramInt2, String paramString2)
    throws IOException
  {
    encodeLiteralHeader(paramInt1, paramString2, paramString1);
    encodeNonZeroIntegerOnSecondBitFirstBitOne(paramInt2);
  }
  
  public final void writeLowLevelEndStartElement()
    throws IOException
  {
    if (hasMark())
    {
      resetMark();
    }
    else
    {
      _b = 240;
      _terminate = true;
    }
  }
  
  public final void writeLowLevelEndElement()
    throws IOException
  {
    encodeElementTermination();
  }
  
  public final void writeLowLevelText(char[] paramArrayOfChar, int paramInt)
    throws IOException
  {
    if (paramInt == 0) {
      return;
    }
    encodeTermination();
    encodeCharacters(paramArrayOfChar, 0, paramInt);
  }
  
  public final void writeLowLevelText(String paramString)
    throws IOException
  {
    int i = paramString.length();
    if (i == 0) {
      return;
    }
    encodeTermination();
    if (i < _charBuffer.length)
    {
      paramString.getChars(0, i, _charBuffer, 0);
      encodeCharacters(_charBuffer, 0, i);
    }
    else
    {
      char[] arrayOfChar = paramString.toCharArray();
      encodeCharactersNoClone(arrayOfChar, 0, i);
    }
  }
  
  public final void writeLowLevelOctets(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    if (paramInt == 0) {
      return;
    }
    encodeTermination();
    encodeCIIOctetAlgorithmData(1, paramArrayOfByte, 0, paramInt);
  }
  
  private boolean encodeElement(int paramInt, String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    LocalNameQualifiedNamesMap.Entry localEntry = _v.elementName.obtainEntry(paramString3);
    for (int i = 0; i < _valueIndex; i++)
    {
      QualifiedName localQualifiedName = _value[i];
      if (((paramString2 == prefix) || (paramString2.equals(prefix))) && ((paramString1 == namespaceName) || (paramString1.equals(namespaceName))))
      {
        _b = paramInt;
        encodeNonZeroIntegerOnThirdBit(index);
        return true;
      }
    }
    localEntry.addQualifiedName(new QualifiedName(paramString2, paramString1, paramString3, "", _v.elementName.getNextIndex()));
    return false;
  }
  
  private boolean encodeAttribute(String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    LocalNameQualifiedNamesMap.Entry localEntry = _v.attributeName.obtainEntry(paramString3);
    for (int i = 0; i < _valueIndex; i++)
    {
      QualifiedName localQualifiedName = _value[i];
      if (((paramString2 == prefix) || (paramString2.equals(prefix))) && ((paramString1 == namespaceName) || (paramString1.equals(namespaceName))))
      {
        encodeNonZeroIntegerOnSecondBitFirstBitZero(index);
        return true;
      }
    }
    localEntry.addQualifiedName(new QualifiedName(paramString2, paramString1, paramString3, "", _v.attributeName.getNextIndex()));
    return false;
  }
  
  private void encodeLiteralHeader(int paramInt, String paramString1, String paramString2)
    throws IOException
  {
    if (paramString1 != "")
    {
      paramInt |= 0x1;
      if (paramString2 != "") {
        paramInt |= 0x2;
      }
      write(paramInt);
      if (paramString2 != "") {
        encodeNonZeroIntegerOnSecondBitFirstBitOne(_v.prefix.get(paramString2));
      }
      encodeNonZeroIntegerOnSecondBitFirstBitOne(_v.namespaceName.get(paramString1));
    }
    else
    {
      write(paramInt);
    }
  }
  
  private void encodeLiteral(int paramInt, String paramString1, String paramString2, String paramString3)
    throws IOException
  {
    encodeLiteralHeader(paramInt, paramString1, paramString2);
    int i = _v.localName.obtainIndex(paramString3);
    if (i == -1) {
      encodeNonEmptyOctetStringOnSecondBit(paramString3);
    } else {
      encodeNonZeroIntegerOnSecondBitFirstBitOne(i);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\StAXDocumentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */