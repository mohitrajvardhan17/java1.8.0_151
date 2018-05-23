package sun.text;

public final class UCompactIntArray
  implements Cloneable
{
  private static final int PLANEMASK = 196608;
  private static final int PLANESHIFT = 16;
  private static final int PLANECOUNT = 16;
  private static final int CODEPOINTMASK = 65535;
  private static final int UNICODECOUNT = 65536;
  private static final int BLOCKSHIFT = 7;
  private static final int BLOCKCOUNT = 128;
  private static final int INDEXSHIFT = 9;
  private static final int INDEXCOUNT = 512;
  private static final int BLOCKMASK = 127;
  private int defaultValue;
  private int[][] values = new int[16][];
  private short[][] indices = new short[16][];
  private boolean isCompact;
  private boolean[][] blockTouched = new boolean[16][];
  private boolean[] planeTouched = new boolean[16];
  
  public UCompactIntArray() {}
  
  public UCompactIntArray(int paramInt)
  {
    this();
    defaultValue = paramInt;
  }
  
  public int elementAt(int paramInt)
  {
    int i = (paramInt & 0x30000) >> 16;
    if (planeTouched[i] == 0) {
      return defaultValue;
    }
    paramInt &= 0xFFFF;
    return values[i][((indices[i][(paramInt >> 7)] & 0xFFFF) + (paramInt & 0x7F))];
  }
  
  public void setElementAt(int paramInt1, int paramInt2)
  {
    if (isCompact) {
      expand();
    }
    int i = (paramInt1 & 0x30000) >> 16;
    if (planeTouched[i] == 0) {
      initPlane(i);
    }
    paramInt1 &= 0xFFFF;
    values[i][paramInt1] = paramInt2;
    blockTouched[i][(paramInt1 >> 7)] = 1;
  }
  
  public void compact()
  {
    if (isCompact) {
      return;
    }
    for (int i = 0; i < 16; i++) {
      if (planeTouched[i] != 0)
      {
        int j = 0;
        int k = 0;
        int m = -1;
        int n = 0;
        while (n < indices[i].length)
        {
          indices[i][n] = -1;
          if ((blockTouched[i][n] == 0) && (m != -1))
          {
            indices[i][n] = m;
          }
          else
          {
            int i1 = j * 128;
            if (n > j) {
              System.arraycopy(values[i], k, values[i], i1, 128);
            }
            if (blockTouched[i][n] == 0) {
              m = (short)i1;
            }
            indices[i][n] = ((short)i1);
            j++;
          }
          n++;
          k += 128;
        }
        n = j * 128;
        int[] arrayOfInt = new int[n];
        System.arraycopy(values[i], 0, arrayOfInt, 0, n);
        values[i] = arrayOfInt;
        blockTouched[i] = null;
      }
    }
    isCompact = true;
  }
  
  private void expand()
  {
    if (isCompact)
    {
      for (int j = 0; j < 16; j++) {
        if (planeTouched[j] != 0)
        {
          blockTouched[j] = new boolean['Ȁ'];
          int[] arrayOfInt = new int[65536];
          for (int i = 0; i < 65536; i++)
          {
            arrayOfInt[i] = values[j][(indices[j][(i >> 7)] & 65535 + (i & 0x7F))];
            blockTouched[j][(i >> 7)] = 1;
          }
          for (i = 0; i < 512; i++) {
            indices[j][i] = ((short)(i << 7));
          }
          values[j] = arrayOfInt;
        }
      }
      isCompact = false;
    }
  }
  
  private void initPlane(int paramInt)
  {
    values[paramInt] = new int[65536];
    indices[paramInt] = new short['Ȁ'];
    blockTouched[paramInt] = new boolean['Ȁ'];
    planeTouched[paramInt] = true;
    if ((planeTouched[0] != 0) && (paramInt != 0)) {
      System.arraycopy(indices[0], 0, indices[paramInt], 0, 512);
    } else {
      for (i = 0; i < 512; i++) {
        indices[paramInt][i] = ((short)(i << 7));
      }
    }
    for (int i = 0; i < 65536; i++) {
      values[paramInt][i] = defaultValue;
    }
  }
  
  public int getKSize()
  {
    int i = 0;
    for (int j = 0; j < 16; j++) {
      if (planeTouched[j] != 0) {
        i += values[j].length * 4 + indices[j].length * 2;
      }
    }
    return i / 1024;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\UCompactIntArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */