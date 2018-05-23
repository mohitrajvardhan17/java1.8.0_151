package java.awt.image;

public class ShortLookupTable
  extends LookupTable
{
  short[][] data;
  
  public ShortLookupTable(int paramInt, short[][] paramArrayOfShort)
  {
    super(paramInt, paramArrayOfShort.length);
    numComponents = paramArrayOfShort.length;
    numEntries = paramArrayOfShort[0].length;
    data = new short[numComponents][];
    for (int i = 0; i < numComponents; i++) {
      data[i] = paramArrayOfShort[i];
    }
  }
  
  public ShortLookupTable(int paramInt, short[] paramArrayOfShort)
  {
    super(paramInt, paramArrayOfShort.length);
    numComponents = 1;
    numEntries = paramArrayOfShort.length;
    data = new short[1][];
    data[0] = paramArrayOfShort;
  }
  
  public final short[][] getTable()
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
        j = (paramArrayOfInt1[i] & 0xFFFF) - offset;
        if (j < 0) {
          throw new ArrayIndexOutOfBoundsException("src[" + i + "]-offset is less than zero");
        }
        paramArrayOfInt2[i] = data[0][j];
      }
    } else {
      for (i = 0; i < paramArrayOfInt1.length; i++)
      {
        j = (paramArrayOfInt1[i] & 0xFFFF) - offset;
        if (j < 0) {
          throw new ArrayIndexOutOfBoundsException("src[" + i + "]-offset is less than zero");
        }
        paramArrayOfInt2[i] = data[i][j];
      }
    }
    return paramArrayOfInt2;
  }
  
  public short[] lookupPixel(short[] paramArrayOfShort1, short[] paramArrayOfShort2)
  {
    if (paramArrayOfShort2 == null) {
      paramArrayOfShort2 = new short[paramArrayOfShort1.length];
    }
    int i;
    int j;
    if (numComponents == 1) {
      for (i = 0; i < paramArrayOfShort1.length; i++)
      {
        j = (paramArrayOfShort1[i] & 0xFFFF) - offset;
        if (j < 0) {
          throw new ArrayIndexOutOfBoundsException("src[" + i + "]-offset is less than zero");
        }
        paramArrayOfShort2[i] = data[0][j];
      }
    } else {
      for (i = 0; i < paramArrayOfShort1.length; i++)
      {
        j = (paramArrayOfShort1[i] & 0xFFFF) - offset;
        if (j < 0) {
          throw new ArrayIndexOutOfBoundsException("src[" + i + "]-offset is less than zero");
        }
        paramArrayOfShort2[i] = data[i][j];
      }
    }
    return paramArrayOfShort2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\ShortLookupTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */