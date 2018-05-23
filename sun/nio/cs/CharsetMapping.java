package sun.nio.cs;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Comparator;

public class CharsetMapping
{
  public static final char UNMAPPABLE_DECODING = '�';
  public static final int UNMAPPABLE_ENCODING = 65533;
  char[] b2cSB;
  char[] b2cDB1;
  char[] b2cDB2;
  int b2Min;
  int b2Max;
  int b1MinDB1;
  int b1MaxDB1;
  int b1MinDB2;
  int b1MaxDB2;
  int dbSegSize;
  char[] c2b;
  char[] c2bIndex;
  char[] b2cSupp;
  char[] c2bSupp;
  Entry[] b2cComp;
  Entry[] c2bComp;
  static Comparator<Entry> comparatorBytes = new Comparator()
  {
    public int compare(CharsetMapping.Entry paramAnonymousEntry1, CharsetMapping.Entry paramAnonymousEntry2)
    {
      return bs - bs;
    }
    
    public boolean equals(Object paramAnonymousObject)
    {
      return this == paramAnonymousObject;
    }
  };
  static Comparator<Entry> comparatorCP = new Comparator()
  {
    public int compare(CharsetMapping.Entry paramAnonymousEntry1, CharsetMapping.Entry paramAnonymousEntry2)
    {
      return cp - cp;
    }
    
    public boolean equals(Object paramAnonymousObject)
    {
      return this == paramAnonymousObject;
    }
  };
  static Comparator<Entry> comparatorComp = new Comparator()
  {
    public int compare(CharsetMapping.Entry paramAnonymousEntry1, CharsetMapping.Entry paramAnonymousEntry2)
    {
      int i = cp - cp;
      if (i == 0) {
        i = cp2 - cp2;
      }
      return i;
    }
    
    public boolean equals(Object paramAnonymousObject)
    {
      return this == paramAnonymousObject;
    }
  };
  private static final int MAP_SINGLEBYTE = 1;
  private static final int MAP_DOUBLEBYTE1 = 2;
  private static final int MAP_DOUBLEBYTE2 = 3;
  private static final int MAP_SUPPLEMENT = 5;
  private static final int MAP_SUPPLEMENT_C2B = 6;
  private static final int MAP_COMPOSITE = 7;
  private static final int MAP_INDEXC2B = 8;
  int off = 0;
  byte[] bb;
  
  public CharsetMapping() {}
  
  public char decodeSingle(int paramInt)
  {
    return b2cSB[paramInt];
  }
  
  public char decodeDouble(int paramInt1, int paramInt2)
  {
    if ((paramInt2 >= b2Min) && (paramInt2 < b2Max))
    {
      paramInt2 -= b2Min;
      if ((paramInt1 >= b1MinDB1) && (paramInt1 <= b1MaxDB1))
      {
        paramInt1 -= b1MinDB1;
        return b2cDB1[(paramInt1 * dbSegSize + paramInt2)];
      }
      if ((paramInt1 >= b1MinDB2) && (paramInt1 <= b1MaxDB2))
      {
        paramInt1 -= b1MinDB2;
        return b2cDB2[(paramInt1 * dbSegSize + paramInt2)];
      }
    }
    return 65533;
  }
  
  public char[] decodeSurrogate(int paramInt, char[] paramArrayOfChar)
  {
    int i = b2cSupp.length / 2;
    int j = Arrays.binarySearch(b2cSupp, 0, i, (char)paramInt);
    if (j >= 0)
    {
      Character.toChars(b2cSupp[(i + j)] + 131072, paramArrayOfChar, 0);
      return paramArrayOfChar;
    }
    return null;
  }
  
  public char[] decodeComposite(Entry paramEntry, char[] paramArrayOfChar)
  {
    int i = findBytes(b2cComp, paramEntry);
    if (i >= 0)
    {
      paramArrayOfChar[0] = ((char)b2cComp[i].cp);
      paramArrayOfChar[1] = ((char)b2cComp[i].cp2);
      return paramArrayOfChar;
    }
    return null;
  }
  
  public int encodeChar(char paramChar)
  {
    int i = c2bIndex[(paramChar >> '\b')];
    if (i == 65535) {
      return 65533;
    }
    return c2b[(i + (paramChar & 0xFF))];
  }
  
  public int encodeSurrogate(char paramChar1, char paramChar2)
  {
    int i = Character.toCodePoint(paramChar1, paramChar2);
    if ((i < 131072) || (i >= 196608)) {
      return 65533;
    }
    int j = c2bSupp.length / 2;
    int k = Arrays.binarySearch(c2bSupp, 0, j, (char)i);
    if (k >= 0) {
      return c2bSupp[(j + k)];
    }
    return 65533;
  }
  
  public boolean isCompositeBase(Entry paramEntry)
  {
    if ((cp <= 12791) && (cp >= 230)) {
      return findCP(c2bComp, paramEntry) >= 0;
    }
    return false;
  }
  
  public int encodeComposite(Entry paramEntry)
  {
    int i = findComp(c2bComp, paramEntry);
    if (i >= 0) {
      return c2bComp[i].bs;
    }
    return 65533;
  }
  
  public static CharsetMapping get(InputStream paramInputStream)
  {
    (CharsetMapping)AccessController.doPrivileged(new PrivilegedAction()
    {
      public CharsetMapping run()
      {
        return new CharsetMapping().load(val$is);
      }
    });
  }
  
  static int findBytes(Entry[] paramArrayOfEntry, Entry paramEntry)
  {
    return Arrays.binarySearch(paramArrayOfEntry, 0, paramArrayOfEntry.length, paramEntry, comparatorBytes);
  }
  
  static int findCP(Entry[] paramArrayOfEntry, Entry paramEntry)
  {
    return Arrays.binarySearch(paramArrayOfEntry, 0, paramArrayOfEntry.length, paramEntry, comparatorCP);
  }
  
  static int findComp(Entry[] paramArrayOfEntry, Entry paramEntry)
  {
    return Arrays.binarySearch(paramArrayOfEntry, 0, paramArrayOfEntry.length, paramEntry, comparatorComp);
  }
  
  private static final boolean readNBytes(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    int i = 0;
    while (paramInt > 0)
    {
      int j = paramInputStream.read(paramArrayOfByte, i, paramInt);
      if (j == -1) {
        return false;
      }
      paramInt -= j;
      i += j;
    }
    return true;
  }
  
  private char[] readCharArray()
  {
    int i = (bb[(off++)] & 0xFF) << 8 | bb[(off++)] & 0xFF;
    char[] arrayOfChar = new char[i];
    for (int j = 0; j < i; j++) {
      arrayOfChar[j] = ((char)((bb[(off++)] & 0xFF) << 8 | bb[(off++)] & 0xFF));
    }
    return arrayOfChar;
  }
  
  void readSINGLEBYTE()
  {
    char[] arrayOfChar = readCharArray();
    for (int i = 0; i < arrayOfChar.length; i++)
    {
      int j = arrayOfChar[i];
      if (j != 65533) {
        c2b[(c2bIndex[(j >> 8)] + (j & 0xFF))] = ((char)i);
      }
    }
    b2cSB = arrayOfChar;
  }
  
  void readINDEXC2B()
  {
    char[] arrayOfChar = readCharArray();
    for (int i = arrayOfChar.length - 1; i >= 0; i--) {
      if ((c2b == null) && (arrayOfChar[i] != '￿'))
      {
        c2b = new char[arrayOfChar[i] + 'Ā'];
        Arrays.fill(c2b, 65533);
        break;
      }
    }
    c2bIndex = arrayOfChar;
  }
  
  char[] readDB(int paramInt1, int paramInt2, int paramInt3)
  {
    char[] arrayOfChar = readCharArray();
    for (int i = 0; i < arrayOfChar.length; i++)
    {
      int j = arrayOfChar[i];
      if (j != 65533)
      {
        int k = i / paramInt3;
        int m = i % paramInt3;
        int n = (k + paramInt1) * 256 + (m + paramInt2);
        c2b[(c2bIndex[(j >> 8)] + (j & 0xFF))] = ((char)n);
      }
    }
    return arrayOfChar;
  }
  
  void readDOUBLEBYTE1()
  {
    b1MinDB1 = ((bb[(off++)] & 0xFF) << 8 | bb[(off++)] & 0xFF);
    b1MaxDB1 = ((bb[(off++)] & 0xFF) << 8 | bb[(off++)] & 0xFF);
    b2Min = ((bb[(off++)] & 0xFF) << 8 | bb[(off++)] & 0xFF);
    b2Max = ((bb[(off++)] & 0xFF) << 8 | bb[(off++)] & 0xFF);
    dbSegSize = (b2Max - b2Min + 1);
    b2cDB1 = readDB(b1MinDB1, b2Min, dbSegSize);
  }
  
  void readDOUBLEBYTE2()
  {
    b1MinDB2 = ((bb[(off++)] & 0xFF) << 8 | bb[(off++)] & 0xFF);
    b1MaxDB2 = ((bb[(off++)] & 0xFF) << 8 | bb[(off++)] & 0xFF);
    b2Min = ((bb[(off++)] & 0xFF) << 8 | bb[(off++)] & 0xFF);
    b2Max = ((bb[(off++)] & 0xFF) << 8 | bb[(off++)] & 0xFF);
    dbSegSize = (b2Max - b2Min + 1);
    b2cDB2 = readDB(b1MinDB2, b2Min, dbSegSize);
  }
  
  void readCOMPOSITE()
  {
    char[] arrayOfChar = readCharArray();
    int i = arrayOfChar.length / 3;
    b2cComp = new Entry[i];
    c2bComp = new Entry[i];
    int j = 0;
    int k = 0;
    while (j < i)
    {
      Entry localEntry = new Entry();
      bs = arrayOfChar[(k++)];
      cp = arrayOfChar[(k++)];
      cp2 = arrayOfChar[(k++)];
      b2cComp[j] = localEntry;
      c2bComp[j] = localEntry;
      j++;
    }
    Arrays.sort(c2bComp, 0, c2bComp.length, comparatorComp);
  }
  
  CharsetMapping load(InputStream paramInputStream)
  {
    try
    {
      int i = (paramInputStream.read() & 0xFF) << 24 | (paramInputStream.read() & 0xFF) << 16 | (paramInputStream.read() & 0xFF) << 8 | paramInputStream.read() & 0xFF;
      bb = new byte[i];
      off = 0;
      if (!readNBytes(paramInputStream, bb, i)) {
        throw new RuntimeException("Corrupted data file");
      }
      paramInputStream.close();
      while (off < i)
      {
        int j = (bb[(off++)] & 0xFF) << 8 | bb[(off++)] & 0xFF;
        switch (j)
        {
        case 8: 
          readINDEXC2B();
          break;
        case 1: 
          readSINGLEBYTE();
          break;
        case 2: 
          readDOUBLEBYTE1();
          break;
        case 3: 
          readDOUBLEBYTE2();
          break;
        case 5: 
          b2cSupp = readCharArray();
          break;
        case 6: 
          c2bSupp = readCharArray();
          break;
        case 7: 
          readCOMPOSITE();
          break;
        case 4: 
        default: 
          throw new RuntimeException("Corrupted data file");
        }
      }
      bb = null;
      return this;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
    return null;
  }
  
  public static class Entry
  {
    public int bs;
    public int cp;
    public int cp2;
    
    public Entry() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\cs\CharsetMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */