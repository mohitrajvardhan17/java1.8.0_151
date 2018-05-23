package com.sun.xml.internal.stream.buffer;

public abstract class AbstractProcessor
  extends AbstractCreatorProcessor
{
  protected static final int STATE_ILLEGAL = 0;
  protected static final int STATE_DOCUMENT = 1;
  protected static final int STATE_DOCUMENT_FRAGMENT = 2;
  protected static final int STATE_ELEMENT_U_LN_QN = 3;
  protected static final int STATE_ELEMENT_P_U_LN = 4;
  protected static final int STATE_ELEMENT_U_LN = 5;
  protected static final int STATE_ELEMENT_LN = 6;
  protected static final int STATE_TEXT_AS_CHAR_ARRAY_SMALL = 7;
  protected static final int STATE_TEXT_AS_CHAR_ARRAY_MEDIUM = 8;
  protected static final int STATE_TEXT_AS_CHAR_ARRAY_COPY = 9;
  protected static final int STATE_TEXT_AS_STRING = 10;
  protected static final int STATE_TEXT_AS_OBJECT = 11;
  protected static final int STATE_COMMENT_AS_CHAR_ARRAY_SMALL = 12;
  protected static final int STATE_COMMENT_AS_CHAR_ARRAY_MEDIUM = 13;
  protected static final int STATE_COMMENT_AS_CHAR_ARRAY_COPY = 14;
  protected static final int STATE_COMMENT_AS_STRING = 15;
  protected static final int STATE_PROCESSING_INSTRUCTION = 16;
  protected static final int STATE_END = 17;
  private static final int[] _eiiStateTable = new int['Ā'];
  protected static final int STATE_NAMESPACE_ATTRIBUTE = 1;
  protected static final int STATE_NAMESPACE_ATTRIBUTE_P = 2;
  protected static final int STATE_NAMESPACE_ATTRIBUTE_P_U = 3;
  protected static final int STATE_NAMESPACE_ATTRIBUTE_U = 4;
  private static final int[] _niiStateTable = new int['Ā'];
  protected static final int STATE_ATTRIBUTE_U_LN_QN = 1;
  protected static final int STATE_ATTRIBUTE_P_U_LN = 2;
  protected static final int STATE_ATTRIBUTE_U_LN = 3;
  protected static final int STATE_ATTRIBUTE_LN = 4;
  protected static final int STATE_ATTRIBUTE_U_LN_QN_OBJECT = 5;
  protected static final int STATE_ATTRIBUTE_P_U_LN_OBJECT = 6;
  protected static final int STATE_ATTRIBUTE_U_LN_OBJECT = 7;
  protected static final int STATE_ATTRIBUTE_LN_OBJECT = 8;
  private static final int[] _aiiStateTable = new int['Ā'];
  protected XMLStreamBuffer _buffer;
  protected boolean _fragmentMode;
  protected boolean _stringInterningFeature = false;
  protected int _treeCount;
  protected final StringBuilder _qNameBuffer = new StringBuilder();
  
  public AbstractProcessor() {}
  
  /**
   * @deprecated
   */
  protected final void setBuffer(XMLStreamBuffer paramXMLStreamBuffer)
  {
    setBuffer(paramXMLStreamBuffer, paramXMLStreamBuffer.isFragment());
  }
  
  protected final void setBuffer(XMLStreamBuffer paramXMLStreamBuffer, boolean paramBoolean)
  {
    _buffer = paramXMLStreamBuffer;
    _fragmentMode = paramBoolean;
    _currentStructureFragment = _buffer.getStructure();
    _structure = ((byte[])_currentStructureFragment.getArray());
    _structurePtr = _buffer.getStructurePtr();
    _currentStructureStringFragment = _buffer.getStructureStrings();
    _structureStrings = ((String[])_currentStructureStringFragment.getArray());
    _structureStringsPtr = _buffer.getStructureStringsPtr();
    _currentContentCharactersBufferFragment = _buffer.getContentCharactersBuffer();
    _contentCharactersBuffer = ((char[])_currentContentCharactersBufferFragment.getArray());
    _contentCharactersBufferPtr = _buffer.getContentCharactersBufferPtr();
    _currentContentObjectFragment = _buffer.getContentObjects();
    _contentObjects = ((Object[])_currentContentObjectFragment.getArray());
    _contentObjectsPtr = _buffer.getContentObjectsPtr();
    _stringInterningFeature = _buffer.hasInternedStrings();
    _treeCount = _buffer.treeCount;
  }
  
  protected final int peekStructure()
  {
    if (_structurePtr < _structure.length) {
      return _structure[_structurePtr] & 0xFF;
    }
    return readFromNextStructure(0);
  }
  
  protected final int readStructure()
  {
    if (_structurePtr < _structure.length) {
      return _structure[(_structurePtr++)] & 0xFF;
    }
    return readFromNextStructure(1);
  }
  
  protected final int readEiiState()
  {
    return _eiiStateTable[readStructure()];
  }
  
  protected static int getEIIState(int paramInt)
  {
    return _eiiStateTable[paramInt];
  }
  
  protected static int getNIIState(int paramInt)
  {
    return _niiStateTable[paramInt];
  }
  
  protected static int getAIIState(int paramInt)
  {
    return _aiiStateTable[paramInt];
  }
  
  protected final int readStructure16()
  {
    return readStructure() << 8 | readStructure();
  }
  
  private int readFromNextStructure(int paramInt)
  {
    _structurePtr = paramInt;
    _currentStructureFragment = _currentStructureFragment.getNext();
    _structure = ((byte[])_currentStructureFragment.getArray());
    return _structure[0] & 0xFF;
  }
  
  protected final String readStructureString()
  {
    if (_structureStringsPtr < _structureStrings.length) {
      return _structureStrings[(_structureStringsPtr++)];
    }
    _structureStringsPtr = 1;
    _currentStructureStringFragment = _currentStructureStringFragment.getNext();
    _structureStrings = ((String[])_currentStructureStringFragment.getArray());
    return _structureStrings[0];
  }
  
  protected final String readContentString()
  {
    return (String)readContentObject();
  }
  
  protected final char[] readContentCharactersCopy()
  {
    return (char[])readContentObject();
  }
  
  protected final int readContentCharactersBuffer(int paramInt)
  {
    if (_contentCharactersBufferPtr + paramInt < _contentCharactersBuffer.length)
    {
      int i = _contentCharactersBufferPtr;
      _contentCharactersBufferPtr += paramInt;
      return i;
    }
    _contentCharactersBufferPtr = paramInt;
    _currentContentCharactersBufferFragment = _currentContentCharactersBufferFragment.getNext();
    _contentCharactersBuffer = ((char[])_currentContentCharactersBufferFragment.getArray());
    return 0;
  }
  
  protected final Object readContentObject()
  {
    if (_contentObjectsPtr < _contentObjects.length) {
      return _contentObjects[(_contentObjectsPtr++)];
    }
    _contentObjectsPtr = 1;
    _currentContentObjectFragment = _currentContentObjectFragment.getNext();
    _contentObjects = ((Object[])_currentContentObjectFragment.getArray());
    return _contentObjects[0];
  }
  
  protected final String getQName(String paramString1, String paramString2)
  {
    _qNameBuffer.append(paramString1).append(':').append(paramString2);
    String str = _qNameBuffer.toString();
    _qNameBuffer.setLength(0);
    return _stringInterningFeature ? str.intern() : str;
  }
  
  protected final String getPrefixFromQName(String paramString)
  {
    int i = paramString.indexOf(':');
    if (_stringInterningFeature) {
      return i != -1 ? paramString.substring(0, i).intern() : "";
    }
    return i != -1 ? paramString.substring(0, i) : "";
  }
  
  static
  {
    _eiiStateTable[16] = 1;
    _eiiStateTable[17] = 2;
    _eiiStateTable[38] = 3;
    _eiiStateTable[35] = 4;
    _eiiStateTable[34] = 5;
    _eiiStateTable[32] = 6;
    _eiiStateTable[80] = 7;
    _eiiStateTable[81] = 8;
    _eiiStateTable[84] = 9;
    _eiiStateTable[88] = 10;
    _eiiStateTable[92] = 11;
    _eiiStateTable[96] = 12;
    _eiiStateTable[97] = 13;
    _eiiStateTable[100] = 14;
    _eiiStateTable[104] = 15;
    _eiiStateTable[112] = 16;
    _eiiStateTable[''] = 17;
    _niiStateTable[64] = 1;
    _niiStateTable[65] = 2;
    _niiStateTable[67] = 3;
    _niiStateTable[66] = 4;
    _aiiStateTable[54] = 1;
    _aiiStateTable[51] = 2;
    _aiiStateTable[50] = 3;
    _aiiStateTable[48] = 4;
    _aiiStateTable[62] = 5;
    _aiiStateTable[59] = 6;
    _aiiStateTable[58] = 7;
    _aiiStateTable[56] = 8;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\buffer\AbstractProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */