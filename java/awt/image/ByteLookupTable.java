package java.awt.image;

public class ByteLookupTable
  extends LookupTable
{
  byte[][] data;
  
  public ByteLookupTable(int paramInt, byte[][] paramArrayOfByte)
  {
    super(paramInt, paramArrayOfByte.length);
    numComponents = paramArrayOfByte.length;
    numEntries = paramArrayOfByte[0].length;
    data = new byte[numComponents][];
    for (int i = 0; i < numComponents; i++) {
      data[i] = paramArrayOfByte[i];
    }
  }
  
  public ByteLookupTable(int paramInt, byte[] paramArrayOfByte)
  {
    super(paramInt, paramArrayOfByte.length);
    numComponents = 1;
    numEntries = paramArrayOfByte.length;
    data = new byte[1][];
    data[0] = paramArrayOfByte;
  }
  
  public final byte[][] getTable()
  {
    return data;
  }
  
  public int[] lookupPixel(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if (paramArrayOfInt2 == null) {
      paramArrayOfInt2 = new int[paramArrayOfInt1.length];
    }
    int i;
    int j;
    if (numComponents == 1) {
      for (i = 0; i < paramArrayOfInt1.length; i++)
      {
        j = paramArrayOfInt1[i] - offset;
        if (j < 0) {
          throw new ArrayIndexOutOfBoundsException("src[" + i + "]-offset is less than zero");
        }
        paramArrayOfInt2[i] = data[0][j];
      }
    } else {
      for (i = 0; i < paramArrayOfInt1.length; i++)
      {
        j = paramArrayOfInt1[i] - offset;
        if (j < 0) {
          throw new ArrayIndexOutOfBoundsException("src[" + i + "]-offset is less than zero");
        }
        paramArrayOfInt2[i] = data[i][j];
      }
    }
    return paramArrayOfInt2;
  }
  
  public byte[] lookupPixel(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if (paramArrayOfByte2 == null) {
      paramArrayOfByte2 = new byte[paramArrayOfByte1.length];
    }
    int i;
    int j;
    if (numComponents == 1) {
      for (i = 0; i < paramArrayOfByte1.length; i++)
      {
        j = (paramArrayOfByte1[i] & 0xFF) - offset;
        if (j < 0) {
          throw new ArrayIndexOutOfBoundsException("src[" + i + "]-offset is less than zero");
        }
        paramArrayOfByte2[i] = data[0][j];
      }
    } else {
      for (i = 0; i < paramArrayOfByte1.length; i++)
      {
        j = (paramArrayOfByte1[i] & 0xFF) - offset;
        if (j < 0) {
          throw new ArrayIndexOutOfBoundsException("src[" + i + "]-offset is less than zero");
        }
        paramArrayOfByte2[i] = data[i][j];
      }
    }
    return paramArrayOfByte2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\ByteLookupTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */