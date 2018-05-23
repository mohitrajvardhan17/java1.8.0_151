package com.sun.imageio.plugins.common;

import java.io.PrintStream;

public class LZWStringTable
{
  private static final int RES_CODES = 2;
  private static final short HASH_FREE = -1;
  private static final short NEXT_FIRST = -1;
  private static final int MAXBITS = 12;
  private static final int MAXSTR = 4096;
  private static final short HASHSIZE = 9973;
  private static final short HASHSTEP = 2039;
  byte[] strChr = new byte['က'];
  short[] strNxt = new short['က'];
  short[] strHsh = new short['⛵'];
  short numStrings;
  int[] strLen = new int['က'];
  
  public LZWStringTable() {}
  
  public int addCharString(short paramShort, byte paramByte)
  {
    if (numStrings >= 4096) {
      return 65535;
    }
    for (int i = hash(paramShort, paramByte); strHsh[i] != -1; i = (i + 2039) % 9973) {}
    strHsh[i] = numStrings;
    strChr[numStrings] = paramByte;
    if (paramShort == -1)
    {
      strNxt[numStrings] = -1;
      strLen[numStrings] = 1;
    }
    else
    {
      strNxt[numStrings] = paramShort;
      strLen[numStrings] = (strLen[paramShort] + 1);
    }
    return numStrings++;
  }
  
  public short findCharString(short paramShort, byte paramByte)
  {
    if (paramShort == -1) {
      return (short)(paramByte & 0xFF);
    }
    int j;
    for (int i = hash(paramShort, paramByte); (j = strHsh[i]) != -1; i = (i + 2039) % 9973) {
      if ((strNxt[j] == paramShort) && (strChr[j] == paramByte)) {
        return (short)j;
      }
    }
    return -1;
  }
  
  public void clearTable(int paramInt)
  {
    numStrings = 0;
    for (int i = 0; i < 9973; i++) {
      strHsh[i] = -1;
    }
    i = (1 << paramInt) + 2;
    for (int j = 0; j < i; j++) {
      addCharString((short)-1, (byte)j);
    }
  }
  
  public static int hash(short paramShort, byte paramByte)
  {
    return (((short)(paramByte << 8) ^ paramShort) & 0xFFFF) % 9973;
  }
  
  public int expandCode(byte[] paramArrayOfByte, int paramInt1, short paramShort, int paramInt2)
  {
    if ((paramInt1 == -2) && (paramInt2 == 1)) {
      paramInt2 = 0;
    }
    if ((paramShort == -1) || (paramInt2 == strLen[paramShort])) {
      return 0;
    }
    int j = strLen[paramShort] - paramInt2;
    int k = paramArrayOfByte.length - paramInt1;
    int i;
    if (k > j) {
      i = j;
    } else {
      i = k;
    }
    int m = j - i;
    int n = paramInt1 + i;
    while ((n > paramInt1) && (paramShort != -1))
    {
      m--;
      if (m < 0) {
        paramArrayOfByte[(--n)] = strChr[paramShort];
      }
      paramShort = strNxt[paramShort];
    }
    if (j > i) {
      return -i;
    }
    return i;
  }
  
  public void dump(PrintStream paramPrintStream)
  {
    for (int i = 258; i < numStrings; i++) {
      paramPrintStream.println(" strNxt[" + i + "] = " + strNxt[i] + " strChr " + Integer.toHexString(strChr[i] & 0xFF) + " strLen " + Integer.toHexString(strLen[i]));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\common\LZWStringTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */