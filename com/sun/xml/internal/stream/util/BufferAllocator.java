package com.sun.xml.internal.stream.util;

public class BufferAllocator
{
  public static int SMALL_SIZE_LIMIT = 128;
  public static int MEDIUM_SIZE_LIMIT = 2048;
  public static int LARGE_SIZE_LIMIT = 8192;
  char[] smallCharBuffer;
  char[] mediumCharBuffer;
  char[] largeCharBuffer;
  byte[] smallByteBuffer;
  byte[] mediumByteBuffer;
  byte[] largeByteBuffer;
  
  public BufferAllocator() {}
  
  public char[] getCharBuffer(int paramInt)
  {
    char[] arrayOfChar;
    if (paramInt <= SMALL_SIZE_LIMIT)
    {
      arrayOfChar = smallCharBuffer;
      smallCharBuffer = null;
      return arrayOfChar;
    }
    if (paramInt <= MEDIUM_SIZE_LIMIT)
    {
      arrayOfChar = mediumCharBuffer;
      mediumCharBuffer = null;
      return arrayOfChar;
    }
    if (paramInt <= LARGE_SIZE_LIMIT)
    {
      arrayOfChar = largeCharBuffer;
      largeCharBuffer = null;
      return arrayOfChar;
    }
    return null;
  }
  
  public void returnCharBuffer(char[] paramArrayOfChar)
  {
    if (paramArrayOfChar == null) {
      return;
    }
    if (paramArrayOfChar.length <= SMALL_SIZE_LIMIT) {
      smallCharBuffer = paramArrayOfChar;
    } else if (paramArrayOfChar.length <= MEDIUM_SIZE_LIMIT) {
      mediumCharBuffer = paramArrayOfChar;
    } else if (paramArrayOfChar.length <= LARGE_SIZE_LIMIT) {
      largeCharBuffer = paramArrayOfChar;
    }
  }
  
  public byte[] getByteBuffer(int paramInt)
  {
    byte[] arrayOfByte;
    if (paramInt <= SMALL_SIZE_LIMIT)
    {
      arrayOfByte = smallByteBuffer;
      smallByteBuffer = null;
      return arrayOfByte;
    }
    if (paramInt <= MEDIUM_SIZE_LIMIT)
    {
      arrayOfByte = mediumByteBuffer;
      mediumByteBuffer = null;
      return arrayOfByte;
    }
    if (paramInt <= LARGE_SIZE_LIMIT)
    {
      arrayOfByte = largeByteBuffer;
      largeByteBuffer = null;
      return arrayOfByte;
    }
    return null;
  }
  
  public void returnByteBuffer(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return;
    }
    if (paramArrayOfByte.length <= SMALL_SIZE_LIMIT) {
      smallByteBuffer = paramArrayOfByte;
    } else if (paramArrayOfByte.length <= MEDIUM_SIZE_LIMIT) {
      mediumByteBuffer = paramArrayOfByte;
    } else if (paramArrayOfByte.length <= LARGE_SIZE_LIMIT) {
      largeByteBuffer = paramArrayOfByte;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\util\BufferAllocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */