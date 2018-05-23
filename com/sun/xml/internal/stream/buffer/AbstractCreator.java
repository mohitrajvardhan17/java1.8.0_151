package com.sun.xml.internal.stream.buffer;

public class AbstractCreator
  extends AbstractCreatorProcessor
{
  protected MutableXMLStreamBuffer _buffer;
  
  public AbstractCreator() {}
  
  public void setXMLStreamBuffer(MutableXMLStreamBuffer paramMutableXMLStreamBuffer)
  {
    if (paramMutableXMLStreamBuffer == null) {
      throw new NullPointerException("buffer cannot be null");
    }
    setBuffer(paramMutableXMLStreamBuffer);
  }
  
  public MutableXMLStreamBuffer getXMLStreamBuffer()
  {
    return _buffer;
  }
  
  protected final void createBuffer()
  {
    setBuffer(new MutableXMLStreamBuffer());
  }
  
  protected final void increaseTreeCount()
  {
    _buffer.treeCount += 1;
  }
  
  protected final void setBuffer(MutableXMLStreamBuffer paramMutableXMLStreamBuffer)
  {
    _buffer = paramMutableXMLStreamBuffer;
    _currentStructureFragment = _buffer.getStructure();
    _structure = ((byte[])_currentStructureFragment.getArray());
    _structurePtr = 0;
    _currentStructureStringFragment = _buffer.getStructureStrings();
    _structureStrings = ((String[])_currentStructureStringFragment.getArray());
    _structureStringsPtr = 0;
    _currentContentCharactersBufferFragment = _buffer.getContentCharactersBuffer();
    _contentCharactersBuffer = ((char[])_currentContentCharactersBufferFragment.getArray());
    _contentCharactersBufferPtr = 0;
    _currentContentObjectFragment = _buffer.getContentObjects();
    _contentObjects = ((Object[])_currentContentObjectFragment.getArray());
    _contentObjectsPtr = 0;
  }
  
  protected final void setHasInternedStrings(boolean paramBoolean)
  {
    _buffer.setHasInternedStrings(paramBoolean);
  }
  
  protected final void storeStructure(int paramInt)
  {
    _structure[(_structurePtr++)] = ((byte)paramInt);
    if (_structurePtr == _structure.length) {
      resizeStructure();
    }
  }
  
  protected final void resizeStructure()
  {
    _structurePtr = 0;
    if (_currentStructureFragment.getNext() != null)
    {
      _currentStructureFragment = _currentStructureFragment.getNext();
      _structure = ((byte[])_currentStructureFragment.getArray());
    }
    else
    {
      _structure = new byte[_structure.length];
      _currentStructureFragment = new FragmentedArray(_structure, _currentStructureFragment);
    }
  }
  
  protected final void storeStructureString(String paramString)
  {
    _structureStrings[(_structureStringsPtr++)] = paramString;
    if (_structureStringsPtr == _structureStrings.length) {
      resizeStructureStrings();
    }
  }
  
  protected final void resizeStructureStrings()
  {
    _structureStringsPtr = 0;
    if (_currentStructureStringFragment.getNext() != null)
    {
      _currentStructureStringFragment = _currentStructureStringFragment.getNext();
      _structureStrings = ((String[])_currentStructureStringFragment.getArray());
    }
    else
    {
      _structureStrings = new String[_structureStrings.length];
      _currentStructureStringFragment = new FragmentedArray(_structureStrings, _currentStructureStringFragment);
    }
  }
  
  protected final void storeContentString(String paramString)
  {
    storeContentObject(paramString);
  }
  
  protected final void storeContentCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
  {
    if (_contentCharactersBufferPtr + paramInt3 >= _contentCharactersBuffer.length)
    {
      if (paramInt3 >= 512)
      {
        storeStructure(paramInt1 | 0x4);
        storeContentCharactersCopy(paramArrayOfChar, paramInt2, paramInt3);
        return;
      }
      resizeContentCharacters();
    }
    if (paramInt3 < 256)
    {
      storeStructure(paramInt1);
      storeStructure(paramInt3);
      System.arraycopy(paramArrayOfChar, paramInt2, _contentCharactersBuffer, _contentCharactersBufferPtr, paramInt3);
      _contentCharactersBufferPtr += paramInt3;
    }
    else if (paramInt3 < 65536)
    {
      storeStructure(paramInt1 | 0x1);
      storeStructure(paramInt3 >> 8);
      storeStructure(paramInt3 & 0xFF);
      System.arraycopy(paramArrayOfChar, paramInt2, _contentCharactersBuffer, _contentCharactersBufferPtr, paramInt3);
      _contentCharactersBufferPtr += paramInt3;
    }
    else
    {
      storeStructure(paramInt1 | 0x4);
      storeContentCharactersCopy(paramArrayOfChar, paramInt2, paramInt3);
    }
  }
  
  protected final void resizeContentCharacters()
  {
    _contentCharactersBufferPtr = 0;
    if (_currentContentCharactersBufferFragment.getNext() != null)
    {
      _currentContentCharactersBufferFragment = _currentContentCharactersBufferFragment.getNext();
      _contentCharactersBuffer = ((char[])_currentContentCharactersBufferFragment.getArray());
    }
    else
    {
      _contentCharactersBuffer = new char[_contentCharactersBuffer.length];
      _currentContentCharactersBufferFragment = new FragmentedArray(_contentCharactersBuffer, _currentContentCharactersBufferFragment);
    }
  }
  
  protected final void storeContentCharactersCopy(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    char[] arrayOfChar = new char[paramInt2];
    System.arraycopy(paramArrayOfChar, paramInt1, arrayOfChar, 0, paramInt2);
    storeContentObject(arrayOfChar);
  }
  
  protected final Object peekAtContentObject()
  {
    return _contentObjects[_contentObjectsPtr];
  }
  
  protected final void storeContentObject(Object paramObject)
  {
    _contentObjects[(_contentObjectsPtr++)] = paramObject;
    if (_contentObjectsPtr == _contentObjects.length) {
      resizeContentObjects();
    }
  }
  
  protected final void resizeContentObjects()
  {
    _contentObjectsPtr = 0;
    if (_currentContentObjectFragment.getNext() != null)
    {
      _currentContentObjectFragment = _currentContentObjectFragment.getNext();
      _contentObjects = ((Object[])_currentContentObjectFragment.getArray());
    }
    else
    {
      _contentObjects = new Object[_contentObjects.length];
      _currentContentObjectFragment = new FragmentedArray(_contentObjects, _currentContentObjectFragment);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\buffer\AbstractCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */