package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public abstract class KeyIntMap
{
  public static final int NOT_PRESENT = -1;
  static final int DEFAULT_INITIAL_CAPACITY = 16;
  static final int MAXIMUM_CAPACITY = 1048576;
  static final float DEFAULT_LOAD_FACTOR = 0.75F;
  int _readOnlyMapSize;
  int _size;
  int _capacity;
  int _threshold;
  final float _loadFactor;
  
  public KeyIntMap(int paramInt, float paramFloat)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalInitialCapacity", new Object[] { Integer.valueOf(paramInt) }));
    }
    if (paramInt > 1048576) {
      paramInt = 1048576;
    }
    if ((paramFloat <= 0.0F) || (Float.isNaN(paramFloat))) {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalLoadFactor", new Object[] { Float.valueOf(paramFloat) }));
    }
    if (paramInt != 16)
    {
      for (_capacity = 1; _capacity < paramInt; _capacity <<= 1) {}
      _loadFactor = paramFloat;
      _threshold = ((int)(_capacity * _loadFactor));
    }
    else
    {
      _capacity = 16;
      _loadFactor = 0.75F;
      _threshold = 12;
    }
  }
  
  public KeyIntMap(int paramInt)
  {
    this(paramInt, 0.75F);
  }
  
  public KeyIntMap()
  {
    _capacity = 16;
    _loadFactor = 0.75F;
    _threshold = 12;
  }
  
  public final int size()
  {
    return _size + _readOnlyMapSize;
  }
  
  public abstract void clear();
  
  public abstract void setReadOnlyMap(KeyIntMap paramKeyIntMap, boolean paramBoolean);
  
  public static final int hashHash(int paramInt)
  {
    paramInt += (paramInt << 9 ^ 0xFFFFFFFF);
    paramInt ^= paramInt >>> 14;
    paramInt += (paramInt << 4);
    paramInt ^= paramInt >>> 10;
    return paramInt;
  }
  
  public static final int indexFor(int paramInt1, int paramInt2)
  {
    return paramInt1 & paramInt2 - 1;
  }
  
  static class BaseEntry
  {
    final int _hash;
    final int _value;
    
    public BaseEntry(int paramInt1, int paramInt2)
    {
      _hash = paramInt1;
      _value = paramInt2;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\util\KeyIntMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */