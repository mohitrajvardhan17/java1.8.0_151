package sun.util.locale.provider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.MissingResourceException;
import sun.text.CompactByteArray;
import sun.text.SupplementaryCharacterData;

class BreakDictionary
{
  private static int supportedVersion = 1;
  private CompactByteArray columnMap = null;
  private SupplementaryCharacterData supplementaryCharColumnMap = null;
  private int numCols;
  private int numColGroups;
  private short[] table = null;
  private short[] rowIndex = null;
  private int[] rowIndexFlags = null;
  private short[] rowIndexFlagsIndex = null;
  private byte[] rowIndexShifts = null;
  
  BreakDictionary(String paramString)
    throws IOException, MissingResourceException
  {
    readDictionaryFile(paramString);
  }
  
  private void readDictionaryFile(final String paramString)
    throws IOException, MissingResourceException
  {
    BufferedInputStream localBufferedInputStream;
    try
    {
      localBufferedInputStream = (BufferedInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public BufferedInputStream run()
          throws Exception
        {
          return new BufferedInputStream(getClass().getResourceAsStream("/sun/text/resources/" + paramString));
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw new InternalError(localPrivilegedActionException.toString(), localPrivilegedActionException);
    }
    byte[] arrayOfByte1 = new byte[8];
    if (localBufferedInputStream.read(arrayOfByte1) != 8) {
      throw new MissingResourceException("Wrong data length", paramString, "");
    }
    int i = RuleBasedBreakIterator.getInt(arrayOfByte1, 0);
    if (i != supportedVersion) {
      throw new MissingResourceException("Dictionary version(" + i + ") is unsupported", paramString, "");
    }
    int j = RuleBasedBreakIterator.getInt(arrayOfByte1, 4);
    arrayOfByte1 = new byte[j];
    if (localBufferedInputStream.read(arrayOfByte1) != j) {
      throw new MissingResourceException("Wrong data length", paramString, "");
    }
    localBufferedInputStream.close();
    int m = 0;
    int k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
    m += 4;
    short[] arrayOfShort = new short[k];
    int n = 0;
    while (n < k)
    {
      arrayOfShort[n] = RuleBasedBreakIterator.getShort(arrayOfByte1, m);
      n++;
      m += 2;
    }
    k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
    m += 4;
    byte[] arrayOfByte2 = new byte[k];
    int i1 = 0;
    while (i1 < k)
    {
      arrayOfByte2[i1] = arrayOfByte1[m];
      i1++;
      m++;
    }
    columnMap = new CompactByteArray(arrayOfShort, arrayOfByte2);
    numCols = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
    m += 4;
    numColGroups = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
    m += 4;
    k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
    m += 4;
    rowIndex = new short[k];
    i1 = 0;
    while (i1 < k)
    {
      rowIndex[i1] = RuleBasedBreakIterator.getShort(arrayOfByte1, m);
      i1++;
      m += 2;
    }
    k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
    m += 4;
    rowIndexFlagsIndex = new short[k];
    i1 = 0;
    while (i1 < k)
    {
      rowIndexFlagsIndex[i1] = RuleBasedBreakIterator.getShort(arrayOfByte1, m);
      i1++;
      m += 2;
    }
    k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
    m += 4;
    rowIndexFlags = new int[k];
    i1 = 0;
    while (i1 < k)
    {
      rowIndexFlags[i1] = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
      i1++;
      m += 4;
    }
    k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
    m += 4;
    rowIndexShifts = new byte[k];
    i1 = 0;
    while (i1 < k)
    {
      rowIndexShifts[i1] = arrayOfByte1[m];
      i1++;
      m++;
    }
    k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
    m += 4;
    table = new short[k];
    i1 = 0;
    while (i1 < k)
    {
      table[i1] = RuleBasedBreakIterator.getShort(arrayOfByte1, m);
      i1++;
      m += 2;
    }
    k = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
    m += 4;
    int[] arrayOfInt = new int[k];
    int i2 = 0;
    while (i2 < k)
    {
      arrayOfInt[i2] = RuleBasedBreakIterator.getInt(arrayOfByte1, m);
      i2++;
      m += 4;
    }
    supplementaryCharColumnMap = new SupplementaryCharacterData(arrayOfInt);
  }
  
  public final short getNextStateFromCharacter(int paramInt1, int paramInt2)
  {
    int i;
    if (paramInt2 < 65536) {
      i = columnMap.elementAt((char)paramInt2);
    } else {
      i = supplementaryCharColumnMap.getValue(paramInt2);
    }
    return getNextState(paramInt1, i);
  }
  
  public final short getNextState(int paramInt1, int paramInt2)
  {
    if (cellIsPopulated(paramInt1, paramInt2)) {
      return internalAt(rowIndex[paramInt1], paramInt2 + rowIndexShifts[paramInt1]);
    }
    return 0;
  }
  
  private boolean cellIsPopulated(int paramInt1, int paramInt2)
  {
    if (rowIndexFlagsIndex[paramInt1] < 0) {
      return paramInt2 == -rowIndexFlagsIndex[paramInt1];
    }
    int i = rowIndexFlags[(rowIndexFlagsIndex[paramInt1] + (paramInt2 >> 5))];
    return (i & 1 << (paramInt2 & 0x1F)) != 0;
  }
  
  private short internalAt(int paramInt1, int paramInt2)
  {
    return table[(paramInt1 * numCols + paramInt2)];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\BreakDictionary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */