package com.sun.xml.internal.ws.org.objectweb.asm;

public class ByteVector
{
  byte[] data;
  int length;
  
  public ByteVector()
  {
    data = new byte[64];
  }
  
  public ByteVector(int paramInt)
  {
    data = new byte[paramInt];
  }
  
  public ByteVector putByte(int paramInt)
  {
    int i = length;
    if (i + 1 > data.length) {
      enlarge(1);
    }
    data[(i++)] = ((byte)paramInt);
    length = i;
    return this;
  }
  
  ByteVector put11(int paramInt1, int paramInt2)
  {
    int i = length;
    if (i + 2 > data.length) {
      enlarge(2);
    }
    byte[] arrayOfByte = data;
    arrayOfByte[(i++)] = ((byte)paramInt1);
    arrayOfByte[(i++)] = ((byte)paramInt2);
    length = i;
    return this;
  }
  
  public ByteVector putShort(int paramInt)
  {
    int i = length;
    if (i + 2 > data.length) {
      enlarge(2);
    }
    byte[] arrayOfByte = data;
    arrayOfByte[(i++)] = ((byte)(paramInt >>> 8));
    arrayOfByte[(i++)] = ((byte)paramInt);
    length = i;
    return this;
  }
  
  ByteVector put12(int paramInt1, int paramInt2)
  {
    int i = length;
    if (i + 3 > data.length) {
      enlarge(3);
    }
    byte[] arrayOfByte = data;
    arrayOfByte[(i++)] = ((byte)paramInt1);
    arrayOfByte[(i++)] = ((byte)(paramInt2 >>> 8));
    arrayOfByte[(i++)] = ((byte)paramInt2);
    length = i;
    return this;
  }
  
  public ByteVector putInt(int paramInt)
  {
    int i = length;
    if (i + 4 > data.length) {
      enlarge(4);
    }
    byte[] arrayOfByte = data;
    arrayOfByte[(i++)] = ((byte)(paramInt >>> 24));
    arrayOfByte[(i++)] = ((byte)(paramInt >>> 16));
    arrayOfByte[(i++)] = ((byte)(paramInt >>> 8));
    arrayOfByte[(i++)] = ((byte)paramInt);
    length = i;
    return this;
  }
  
  public ByteVector putLong(long paramLong)
  {
    int i = length;
    if (i + 8 > data.length) {
      enlarge(8);
    }
    byte[] arrayOfByte = data;
    int j = (int)(paramLong >>> 32);
    arrayOfByte[(i++)] = ((byte)(j >>> 24));
    arrayOfByte[(i++)] = ((byte)(j >>> 16));
    arrayOfByte[(i++)] = ((byte)(j >>> 8));
    arrayOfByte[(i++)] = ((byte)j);
    j = (int)paramLong;
    arrayOfByte[(i++)] = ((byte)(j >>> 24));
    arrayOfByte[(i++)] = ((byte)(j >>> 16));
    arrayOfByte[(i++)] = ((byte)(j >>> 8));
    arrayOfByte[(i++)] = ((byte)j);
    length = i;
    return this;
  }
  
  public ByteVector putUTF8(String paramString)
  {
    int i = paramString.length();
    if (length + 2 + i > data.length) {
      enlarge(2 + i);
    }
    int j = length;
    byte[] arrayOfByte = data;
    arrayOfByte[(j++)] = ((byte)(i >>> 8));
    arrayOfByte[(j++)] = ((byte)i);
    for (int k = 0; k < i; k++)
    {
      int m = paramString.charAt(k);
      if ((m >= 1) && (m <= 127))
      {
        arrayOfByte[(j++)] = ((byte)m);
      }
      else
      {
        int n = k;
        for (int i1 = k; i1 < i; i1++)
        {
          m = paramString.charAt(i1);
          if ((m >= 1) && (m <= 127)) {
            n++;
          } else if (m > 2047) {
            n += 3;
          } else {
            n += 2;
          }
        }
        arrayOfByte[length] = ((byte)(n >>> 8));
        arrayOfByte[(length + 1)] = ((byte)n);
        if (length + 2 + n > arrayOfByte.length)
        {
          length = j;
          enlarge(2 + n);
          arrayOfByte = data;
        }
        for (i1 = k; i1 < i; i1++)
        {
          m = paramString.charAt(i1);
          if ((m >= 1) && (m <= 127))
          {
            arrayOfByte[(j++)] = ((byte)m);
          }
          else if (m > 2047)
          {
            arrayOfByte[(j++)] = ((byte)(0xE0 | m >> 12 & 0xF));
            arrayOfByte[(j++)] = ((byte)(0x80 | m >> 6 & 0x3F));
            arrayOfByte[(j++)] = ((byte)(0x80 | m & 0x3F));
          }
          else
          {
            arrayOfByte[(j++)] = ((byte)(0xC0 | m >> 6 & 0x1F));
            arrayOfByte[(j++)] = ((byte)(0x80 | m & 0x3F));
          }
        }
        break;
      }
    }
    length = j;
    return this;
  }
  
  public ByteVector putByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (length + paramInt2 > data.length) {
      enlarge(paramInt2);
    }
    if (paramArrayOfByte != null) {
      System.arraycopy(paramArrayOfByte, paramInt1, data, length, paramInt2);
    }
    length += paramInt2;
    return this;
  }
  
  private void enlarge(int paramInt)
  {
    int i = 2 * data.length;
    int j = length + paramInt;
    byte[] arrayOfByte = new byte[i > j ? i : j];
    System.arraycopy(data, 0, arrayOfByte, 0, length);
    data = arrayOfByte;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\ByteVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */