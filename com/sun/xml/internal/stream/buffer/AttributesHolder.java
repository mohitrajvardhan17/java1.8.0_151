package com.sun.xml.internal.stream.buffer;

import org.xml.sax.Attributes;

public final class AttributesHolder
  implements Attributes
{
  private static final int DEFAULT_CAPACITY = 8;
  private static final int ITEM_SIZE = 8;
  private static final int PREFIX = 0;
  private static final int URI = 1;
  private static final int LOCAL_NAME = 2;
  private static final int QNAME = 3;
  private static final int TYPE = 4;
  private static final int VALUE = 5;
  private int _attributeCount;
  private String[] _strings = new String[64];
  
  public AttributesHolder() {}
  
  public final int getLength()
  {
    return _attributeCount;
  }
  
  public final String getPrefix(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < _attributeCount) ? _strings[((paramInt << 3) + 0)] : null;
  }
  
  public final String getLocalName(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < _attributeCount) ? _strings[((paramInt << 3) + 2)] : null;
  }
  
  public final String getQName(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < _attributeCount) ? _strings[((paramInt << 3) + 3)] : null;
  }
  
  public final String getType(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < _attributeCount) ? _strings[((paramInt << 3) + 4)] : null;
  }
  
  public final String getURI(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < _attributeCount) ? _strings[((paramInt << 3) + 1)] : null;
  }
  
  public final String getValue(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < _attributeCount) ? _strings[((paramInt << 3) + 5)] : null;
  }
  
  public final int getIndex(String paramString)
  {
    for (int i = 0; i < _attributeCount; i++) {
      if (paramString.equals(_strings[((i << 3) + 3)])) {
        return i;
      }
    }
    return -1;
  }
  
  public final String getType(String paramString)
  {
    int i = (getIndex(paramString) << 3) + 4;
    return i >= 0 ? _strings[i] : null;
  }
  
  public final String getValue(String paramString)
  {
    int i = (getIndex(paramString) << 3) + 5;
    return i >= 0 ? _strings[i] : null;
  }
  
  public final int getIndex(String paramString1, String paramString2)
  {
    for (int i = 0; i < _attributeCount; i++) {
      if ((paramString2.equals(_strings[((i << 3) + 2)])) && (paramString1.equals(_strings[((i << 3) + 1)]))) {
        return i;
      }
    }
    return -1;
  }
  
  public final String getType(String paramString1, String paramString2)
  {
    int i = (getIndex(paramString1, paramString2) << 3) + 4;
    return i >= 0 ? _strings[i] : null;
  }
  
  public final String getValue(String paramString1, String paramString2)
  {
    int i = (getIndex(paramString1, paramString2) << 3) + 5;
    return i >= 0 ? _strings[i] : null;
  }
  
  public final void clear()
  {
    if (_attributeCount > 0)
    {
      for (int i = 0; i < _attributeCount; i++) {
        _strings[((i << 3) + 5)] = null;
      }
      _attributeCount = 0;
    }
  }
  
  public final void addAttributeWithQName(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    int i = _attributeCount << 3;
    if (i == _strings.length) {
      resize(i);
    }
    _strings[(i + 0)] = null;
    _strings[(i + 1)] = paramString1;
    _strings[(i + 2)] = paramString2;
    _strings[(i + 3)] = paramString3;
    _strings[(i + 4)] = paramString4;
    _strings[(i + 5)] = paramString5;
    _attributeCount += 1;
  }
  
  public final void addAttributeWithPrefix(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    int i = _attributeCount << 3;
    if (i == _strings.length) {
      resize(i);
    }
    _strings[(i + 0)] = paramString1;
    _strings[(i + 1)] = paramString2;
    _strings[(i + 2)] = paramString3;
    _strings[(i + 3)] = null;
    _strings[(i + 4)] = paramString4;
    _strings[(i + 5)] = paramString5;
    _attributeCount += 1;
  }
  
  private void resize(int paramInt)
  {
    int i = paramInt * 2;
    String[] arrayOfString = new String[i];
    System.arraycopy(_strings, 0, arrayOfString, 0, paramInt);
    _strings = arrayOfString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\buffer\AttributesHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */