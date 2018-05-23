package com.sun.xml.internal.stream.buffer;

import java.util.Map;

public class XMLStreamBufferMark
  extends XMLStreamBuffer
{
  public XMLStreamBufferMark(Map<String, String> paramMap, AbstractCreatorProcessor paramAbstractCreatorProcessor)
  {
    if (paramMap != null) {
      _inscopeNamespaces = paramMap;
    }
    _structure = _currentStructureFragment;
    _structurePtr = _structurePtr;
    _structureStrings = _currentStructureStringFragment;
    _structureStringsPtr = _structureStringsPtr;
    _contentCharactersBuffer = _currentContentCharactersBufferFragment;
    _contentCharactersBufferPtr = _contentCharactersBufferPtr;
    _contentObjects = _currentContentObjectFragment;
    _contentObjectsPtr = _contentObjectsPtr;
    treeCount = 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\buffer\XMLStreamBufferMark.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */