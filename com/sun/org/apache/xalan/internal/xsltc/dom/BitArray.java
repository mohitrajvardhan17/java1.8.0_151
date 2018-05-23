package com.sun.org.apache.xalan.internal.xsltc.dom;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BitArray
  implements Externalizable
{
  static final long serialVersionUID = -4876019880708377663L;
  private int[] _bits;
  private int _bitSize;
  private int _intSize;
  private int _mask;
  private static final int[] _masks = { Integer.MIN_VALUE, 1073741824, 536870912, 268435456, 134217728, 67108864, 33554432, 16777216, 8388608, 4194304, 2097152, 1048576, 524288, 262144, 131072, 65536, 32768, 16384, 8192, 4096, 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1 };
  private static final boolean DEBUG_ASSERTIONS = false;
  private int _pos = Integer.MAX_VALUE;
  private int _node = 0;
  private int _int = 0;
  private int _bit = 0;
  int _first = Integer.MAX_VALUE;
  int _last = Integer.MIN_VALUE;
  
  public BitArray()
  {
    this(32);
  }
  
  public BitArray(int paramInt)
  {
    if (paramInt < 32) {
      paramInt = 32;
    }
    _bitSize = paramInt;
    _intSize = ((_bitSize >>> 5) + 1);
    _bits = new int[_intSize + 1];
  }
  
  public BitArray(int paramInt, int[] paramArrayOfInt)
  {
    if (paramInt < 32) {
      paramInt = 32;
    }
    _bitSize = paramInt;
    _intSize = ((_bitSize >>> 5) + 1);
    _bits = paramArrayOfInt;
  }
  
  public void setMask(int paramInt)
  {
    _mask = paramInt;
  }
  
  public int getMask()
  {
    return _mask;
  }
  
  public final int size()
  {
    return _bitSize;
  }
  
  public final boolean getBit(int paramInt)
  {
    return (_bits[(paramInt >>> 5)] & _masks[(paramInt % 32)]) != 0;
  }
  
  public final int getNextBit(int paramInt)
  {
    for (int i = paramInt >>> 5; i <= _intSize; i++)
    {
      int j = _bits[i];
      if (j != 0) {
        for (int k = paramInt % 32; k < 32; k++) {
          if ((j & _masks[k]) != 0) {
            return (i << 5) + k;
          }
        }
      }
      paramInt = 0;
    }
    return -1;
  }
  
  public final int getBitNumber(int paramInt)
  {
    if (paramInt == _pos) {
      return _node;
    }
    if (paramInt < _pos) {}
    for (_int = (_bit = _pos = 0); _int <= _intSize; _int += 1)
    {
      int i = _bits[_int];
      if (i != 0)
      {
        while (_bit < 32)
        {
          if (((i & _masks[_bit]) != 0) && (++_pos == paramInt))
          {
            _node = ((_int << 5) + _bit - 1);
            return _node;
          }
          _bit += 1;
        }
        _bit = 0;
      }
    }
    return 0;
  }
  
  public final int[] data()
  {
    return _bits;
  }
  
  public final void setBit(int paramInt)
  {
    if (paramInt >= _bitSize) {
      return;
    }
    int i = paramInt >>> 5;
    if (i < _first) {
      _first = i;
    }
    if (i > _last) {
      _last = i;
    }
    _bits[i] |= _masks[(paramInt % 32)];
  }
  
  public final BitArray merge(BitArray paramBitArray)
  {
    if (_last == -1)
    {
      _bits = _bits;
    }
    else if (_last != -1)
    {
      int i = _first < _first ? _first : _first;
      int j = _last > _last ? _last : _last;
      int k;
      if (_intSize > _intSize)
      {
        if (j > _intSize) {
          j = _intSize;
        }
        for (k = i; k <= j; k++) {
          _bits[k] |= _bits[k];
        }
        _bits = _bits;
      }
      else
      {
        if (j > _intSize) {
          j = _intSize;
        }
        for (k = i; k <= j; k++) {
          _bits[k] |= _bits[k];
        }
      }
    }
    return this;
  }
  
  public final void resize(int paramInt)
  {
    if (paramInt > _bitSize)
    {
      _intSize = ((paramInt >>> 5) + 1);
      int[] arrayOfInt = new int[_intSize + 1];
      System.arraycopy(_bits, 0, arrayOfInt, 0, (_bitSize >>> 5) + 1);
      _bits = arrayOfInt;
      _bitSize = paramInt;
    }
  }
  
  public BitArray cloneArray()
  {
    return new BitArray(_intSize, _bits);
  }
  
  public void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException
  {
    paramObjectOutput.writeInt(_bitSize);
    paramObjectOutput.writeInt(_mask);
    paramObjectOutput.writeObject(_bits);
    paramObjectOutput.flush();
  }
  
  public void readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    _bitSize = paramObjectInput.readInt();
    _intSize = ((_bitSize >>> 5) + 1);
    _mask = paramObjectInput.readInt();
    _bits = ((int[])paramObjectInput.readObject());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\BitArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */