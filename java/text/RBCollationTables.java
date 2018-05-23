package java.text;

import java.util.Vector;
import sun.text.IntHashtable;
import sun.text.UCompactIntArray;

final class RBCollationTables
{
  static final int EXPANDCHARINDEX = 2113929216;
  static final int CONTRACTCHARINDEX = 2130706432;
  static final int UNMAPPED = -1;
  static final int PRIMARYORDERMASK = -65536;
  static final int SECONDARYORDERMASK = 65280;
  static final int TERTIARYORDERMASK = 255;
  static final int PRIMARYDIFFERENCEONLY = -65536;
  static final int SECONDARYDIFFERENCEONLY = -256;
  static final int PRIMARYORDERSHIFT = 16;
  static final int SECONDARYORDERSHIFT = 8;
  private String rules = null;
  private boolean frenchSec = false;
  private boolean seAsianSwapping = false;
  private UCompactIntArray mapping = null;
  private Vector<Vector<EntryPair>> contractTable = null;
  private Vector<int[]> expandTable = null;
  private IntHashtable contractFlags = null;
  private short maxSecOrder = 0;
  private short maxTerOrder = 0;
  
  public RBCollationTables(String paramString, int paramInt)
    throws ParseException
  {
    rules = paramString;
    RBTableBuilder localRBTableBuilder = new RBTableBuilder(new BuildAPI(null));
    localRBTableBuilder.build(paramString, paramInt);
  }
  
  public String getRules()
  {
    return rules;
  }
  
  public boolean isFrenchSec()
  {
    return frenchSec;
  }
  
  public boolean isSEAsianSwapping()
  {
    return seAsianSwapping;
  }
  
  Vector<EntryPair> getContractValues(int paramInt)
  {
    int i = mapping.elementAt(paramInt);
    return getContractValuesImpl(i - 2130706432);
  }
  
  private Vector<EntryPair> getContractValuesImpl(int paramInt)
  {
    if (paramInt >= 0) {
      return (Vector)contractTable.elementAt(paramInt);
    }
    return null;
  }
  
  boolean usedInContractSeq(int paramInt)
  {
    return contractFlags.get(paramInt) == 1;
  }
  
  int getMaxExpansion(int paramInt)
  {
    int i = 1;
    if (expandTable != null) {
      for (int j = 0; j < expandTable.size(); j++)
      {
        int[] arrayOfInt = (int[])expandTable.elementAt(j);
        int k = arrayOfInt.length;
        if ((k > i) && (arrayOfInt[(k - 1)] == paramInt)) {
          i = k;
        }
      }
    }
    return i;
  }
  
  final int[] getExpandValueList(int paramInt)
  {
    return (int[])expandTable.elementAt(paramInt - 2113929216);
  }
  
  int getUnicodeOrder(int paramInt)
  {
    return mapping.elementAt(paramInt);
  }
  
  short getMaxSecOrder()
  {
    return maxSecOrder;
  }
  
  short getMaxTerOrder()
  {
    return maxTerOrder;
  }
  
  static void reverse(StringBuffer paramStringBuffer, int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    for (int j = paramInt2 - 1; i < j; j--)
    {
      char c = paramStringBuffer.charAt(i);
      paramStringBuffer.setCharAt(i, paramStringBuffer.charAt(j));
      paramStringBuffer.setCharAt(j, c);
      i++;
    }
  }
  
  static final int getEntry(Vector<EntryPair> paramVector, String paramString, boolean paramBoolean)
  {
    for (int i = 0; i < paramVector.size(); i++)
    {
      EntryPair localEntryPair = (EntryPair)paramVector.elementAt(i);
      if ((fwd == paramBoolean) && (entryName.equals(paramString))) {
        return i;
      }
    }
    return -1;
  }
  
  final class BuildAPI
  {
    private BuildAPI() {}
    
    void fillInTables(boolean paramBoolean1, boolean paramBoolean2, UCompactIntArray paramUCompactIntArray, Vector<Vector<EntryPair>> paramVector, Vector<int[]> paramVector1, IntHashtable paramIntHashtable, short paramShort1, short paramShort2)
    {
      frenchSec = paramBoolean1;
      seAsianSwapping = paramBoolean2;
      mapping = paramUCompactIntArray;
      contractTable = paramVector;
      expandTable = paramVector1;
      contractFlags = paramIntHashtable;
      maxSecOrder = paramShort1;
      maxTerOrder = paramShort2;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\RBCollationTables.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */