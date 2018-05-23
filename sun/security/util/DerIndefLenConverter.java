package sun.security.util;

import java.io.IOException;
import java.util.ArrayList;

class DerIndefLenConverter
{
  private static final int TAG_MASK = 31;
  private static final int FORM_MASK = 32;
  private static final int CLASS_MASK = 192;
  private static final int LEN_LONG = 128;
  private static final int LEN_MASK = 127;
  private static final int SKIP_EOC_BYTES = 2;
  private byte[] data;
  private byte[] newData;
  private int newDataPos;
  private int dataPos;
  private int dataSize;
  private int index;
  private int unresolved = 0;
  private ArrayList<Object> ndefsList = new ArrayList();
  private int numOfTotalLenBytes = 0;
  
  private boolean isEOC(int paramInt)
  {
    return ((paramInt & 0x1F) == 0) && ((paramInt & 0x20) == 0) && ((paramInt & 0xC0) == 0);
  }
  
  static boolean isLongForm(int paramInt)
  {
    return (paramInt & 0x80) == 128;
  }
  
  DerIndefLenConverter() {}
  
  static boolean isIndefinite(int paramInt)
  {
    return (isLongForm(paramInt)) && ((paramInt & 0x7F) == 0);
  }
  
  private void parseTag()
    throws IOException
  {
    if (dataPos == dataSize) {
      return;
    }
    if ((isEOC(data[dataPos])) && (data[(dataPos + 1)] == 0))
    {
      int i = 0;
      Object localObject = null;
      for (int j = ndefsList.size() - 1; j >= 0; j--)
      {
        localObject = ndefsList.get(j);
        if ((localObject instanceof Integer)) {
          break;
        }
        i += ((byte[])localObject).length - 3;
      }
      if (j < 0) {
        throw new IOException("EOC does not have matching indefinite-length tag");
      }
      int k = dataPos - ((Integer)localObject).intValue() + i;
      byte[] arrayOfByte = getLengthBytes(k);
      ndefsList.set(j, arrayOfByte);
      unresolved -= 1;
      numOfTotalLenBytes += arrayOfByte.length - 3;
    }
    dataPos += 1;
  }
  
  private void writeTag()
  {
    if (dataPos == dataSize) {
      return;
    }
    int i = data[(dataPos++)];
    if ((isEOC(i)) && (data[dataPos] == 0))
    {
      dataPos += 1;
      writeTag();
    }
    else
    {
      newData[(newDataPos++)] = ((byte)i);
    }
  }
  
  private int parseLength()
    throws IOException
  {
    int i = 0;
    if (dataPos == dataSize) {
      return i;
    }
    int j = data[(dataPos++)] & 0xFF;
    if (isIndefinite(j))
    {
      ndefsList.add(new Integer(dataPos));
      unresolved += 1;
      return i;
    }
    if (isLongForm(j))
    {
      j &= 0x7F;
      if (j > 4) {
        throw new IOException("Too much data");
      }
      if (dataSize - dataPos < j + 1) {
        throw new IOException("Too little data");
      }
      for (int k = 0; k < j; k++) {
        i = (i << 8) + (data[(dataPos++)] & 0xFF);
      }
      if (i < 0) {
        throw new IOException("Invalid length bytes");
      }
    }
    else
    {
      i = j & 0x7F;
    }
    return i;
  }
  
  private void writeLengthAndValue()
    throws IOException
  {
    if (dataPos == dataSize) {
      return;
    }
    int i = 0;
    int j = data[(dataPos++)] & 0xFF;
    if (isIndefinite(j))
    {
      byte[] arrayOfByte = (byte[])ndefsList.get(index++);
      System.arraycopy(arrayOfByte, 0, newData, newDataPos, arrayOfByte.length);
      newDataPos += arrayOfByte.length;
      return;
    }
    if (isLongForm(j))
    {
      j &= 0x7F;
      for (int k = 0; k < j; k++) {
        i = (i << 8) + (data[(dataPos++)] & 0xFF);
      }
      if (i < 0) {
        throw new IOException("Invalid length bytes");
      }
    }
    else
    {
      i = j & 0x7F;
    }
    writeLength(i);
    writeValue(i);
  }
  
  private void writeLength(int paramInt)
  {
    if (paramInt < 128)
    {
      newData[(newDataPos++)] = ((byte)paramInt);
    }
    else if (paramInt < 256)
    {
      newData[(newDataPos++)] = -127;
      newData[(newDataPos++)] = ((byte)paramInt);
    }
    else if (paramInt < 65536)
    {
      newData[(newDataPos++)] = -126;
      newData[(newDataPos++)] = ((byte)(paramInt >> 8));
      newData[(newDataPos++)] = ((byte)paramInt);
    }
    else if (paramInt < 16777216)
    {
      newData[(newDataPos++)] = -125;
      newData[(newDataPos++)] = ((byte)(paramInt >> 16));
      newData[(newDataPos++)] = ((byte)(paramInt >> 8));
      newData[(newDataPos++)] = ((byte)paramInt);
    }
    else
    {
      newData[(newDataPos++)] = -124;
      newData[(newDataPos++)] = ((byte)(paramInt >> 24));
      newData[(newDataPos++)] = ((byte)(paramInt >> 16));
      newData[(newDataPos++)] = ((byte)(paramInt >> 8));
      newData[(newDataPos++)] = ((byte)paramInt);
    }
  }
  
  private byte[] getLengthBytes(int paramInt)
  {
    int i = 0;
    byte[] arrayOfByte;
    if (paramInt < 128)
    {
      arrayOfByte = new byte[1];
      arrayOfByte[(i++)] = ((byte)paramInt);
    }
    else if (paramInt < 256)
    {
      arrayOfByte = new byte[2];
      arrayOfByte[(i++)] = -127;
      arrayOfByte[(i++)] = ((byte)paramInt);
    }
    else if (paramInt < 65536)
    {
      arrayOfByte = new byte[3];
      arrayOfByte[(i++)] = -126;
      arrayOfByte[(i++)] = ((byte)(paramInt >> 8));
      arrayOfByte[(i++)] = ((byte)paramInt);
    }
    else if (paramInt < 16777216)
    {
      arrayOfByte = new byte[4];
      arrayOfByte[(i++)] = -125;
      arrayOfByte[(i++)] = ((byte)(paramInt >> 16));
      arrayOfByte[(i++)] = ((byte)(paramInt >> 8));
      arrayOfByte[(i++)] = ((byte)paramInt);
    }
    else
    {
      arrayOfByte = new byte[5];
      arrayOfByte[(i++)] = -124;
      arrayOfByte[(i++)] = ((byte)(paramInt >> 24));
      arrayOfByte[(i++)] = ((byte)(paramInt >> 16));
      arrayOfByte[(i++)] = ((byte)(paramInt >> 8));
      arrayOfByte[(i++)] = ((byte)paramInt);
    }
    return arrayOfByte;
  }
  
  private int getNumOfLenBytes(int paramInt)
  {
    int i = 0;
    if (paramInt < 128) {
      i = 1;
    } else if (paramInt < 256) {
      i = 2;
    } else if (paramInt < 65536) {
      i = 3;
    } else if (paramInt < 16777216) {
      i = 4;
    } else {
      i = 5;
    }
    return i;
  }
  
  private void parseValue(int paramInt)
  {
    dataPos += paramInt;
  }
  
  private void writeValue(int paramInt)
  {
    for (int i = 0; i < paramInt; i++) {
      newData[(newDataPos++)] = data[(dataPos++)];
    }
  }
  
  byte[] convert(byte[] paramArrayOfByte)
    throws IOException
  {
    data = paramArrayOfByte;
    dataPos = 0;
    index = 0;
    dataSize = data.length;
    int i = 0;
    int j = 0;
    while (dataPos < dataSize)
    {
      parseTag();
      i = parseLength();
      parseValue(i);
      if (unresolved == 0)
      {
        j = dataSize - dataPos;
        dataSize = dataPos;
      }
    }
    if (unresolved != 0) {
      throw new IOException("not all indef len BER resolved");
    }
    newData = new byte[dataSize + numOfTotalLenBytes + j];
    dataPos = 0;
    newDataPos = 0;
    index = 0;
    while (dataPos < dataSize)
    {
      writeTag();
      writeLengthAndValue();
    }
    System.arraycopy(paramArrayOfByte, dataSize, newData, dataSize + numOfTotalLenBytes, j);
    return newData;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\DerIndefLenConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */