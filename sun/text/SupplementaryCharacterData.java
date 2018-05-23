package sun.text;

public final class SupplementaryCharacterData
  implements Cloneable
{
  private static final byte IGNORE = -1;
  private int[] dataTable;
  
  public SupplementaryCharacterData(int[] paramArrayOfInt)
  {
    dataTable = paramArrayOfInt;
  }
  
  public int getValue(int paramInt)
  {
    assert ((paramInt >= 65536) && (paramInt <= 1114111)) : ("Invalid code point:" + Integer.toHexString(paramInt));
    int i = 0;
    int j = dataTable.length - 1;
    for (;;)
    {
      int k = (i + j) / 2;
      int m = dataTable[k] >> 8;
      int n = dataTable[(k + 1)] >> 8;
      if (paramInt < m)
      {
        j = k;
      }
      else if (paramInt > n - 1)
      {
        i = k;
      }
      else
      {
        int i1 = dataTable[k] & 0xFF;
        return i1 == 255 ? -1 : i1;
      }
    }
  }
  
  public int[] getArray()
  {
    return dataTable;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\SupplementaryCharacterData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */