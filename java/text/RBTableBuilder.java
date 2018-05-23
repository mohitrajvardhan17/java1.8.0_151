package java.text;

import java.util.Vector;
import sun.text.ComposedCharIter;
import sun.text.IntHashtable;
import sun.text.UCompactIntArray;
import sun.text.normalizer.NormalizerImpl;

final class RBTableBuilder
{
  static final int CHARINDEX = 1879048192;
  private static final int IGNORABLEMASK = 65535;
  private static final int PRIMARYORDERINCREMENT = 65536;
  private static final int SECONDARYORDERINCREMENT = 256;
  private static final int TERTIARYORDERINCREMENT = 1;
  private static final int INITIALTABLESIZE = 20;
  private static final int MAXKEYSIZE = 5;
  private RBCollationTables.BuildAPI tables = null;
  private MergeCollation mPattern = null;
  private boolean isOverIgnore = false;
  private char[] keyBuf = new char[5];
  private IntHashtable contractFlags = new IntHashtable(100);
  private boolean frenchSec = false;
  private boolean seAsianSwapping = false;
  private UCompactIntArray mapping = null;
  private Vector<Vector<EntryPair>> contractTable = null;
  private Vector<int[]> expandTable = null;
  private short maxSecOrder = 0;
  private short maxTerOrder = 0;
  
  public RBTableBuilder(RBCollationTables.BuildAPI paramBuildAPI)
  {
    tables = paramBuildAPI;
  }
  
  public void build(String paramString, int paramInt)
    throws ParseException
  {
    int i = 1;
    int j = 0;
    if (paramString.length() == 0) {
      throw new ParseException("Build rules empty.", 0);
    }
    mapping = new UCompactIntArray(-1);
    paramString = NormalizerImpl.canonicalDecomposeWithSingleQuotation(paramString);
    mPattern = new MergeCollation(paramString);
    int k = 0;
    for (j = 0; j < mPattern.getCount(); j++)
    {
      PatternEntry localPatternEntry = mPattern.getItemAt(j);
      if (localPatternEntry != null)
      {
        String str2 = localPatternEntry.getChars();
        if (str2.length() > 1) {
          switch (str2.charAt(str2.length() - 1))
          {
          case '@': 
            frenchSec = true;
            str2 = str2.substring(0, str2.length() - 1);
            break;
          case '!': 
            seAsianSwapping = true;
            str2 = str2.substring(0, str2.length() - 1);
          }
        }
        k = increment(localPatternEntry.getStrength(), k);
        String str1 = localPatternEntry.getExtension();
        if (str1.length() != 0)
        {
          addExpandOrder(str2, str1, k);
        }
        else
        {
          char c;
          if (str2.length() > 1)
          {
            c = str2.charAt(0);
            if ((Character.isHighSurrogate(c)) && (str2.length() == 2)) {
              addOrder(Character.toCodePoint(c, str2.charAt(1)), k);
            } else {
              addContractOrder(str2, k);
            }
          }
          else
          {
            c = str2.charAt(0);
            addOrder(c, k);
          }
        }
      }
    }
    addComposedChars();
    commit();
    mapping.compact();
    tables.fillInTables(frenchSec, seAsianSwapping, mapping, contractTable, expandTable, contractFlags, maxSecOrder, maxTerOrder);
  }
  
  private void addComposedChars()
    throws ParseException
  {
    ComposedCharIter localComposedCharIter = new ComposedCharIter();
    int i;
    while ((i = localComposedCharIter.next()) != -1) {
      if (getCharOrder(i) == -1)
      {
        String str = localComposedCharIter.decomposition();
        int j;
        if (str.length() == 1)
        {
          j = getCharOrder(str.charAt(0));
          if (j != -1) {
            addOrder(i, j);
          }
        }
        else
        {
          int m;
          if (str.length() == 2)
          {
            j = str.charAt(0);
            if (Character.isHighSurrogate(j))
            {
              m = getCharOrder(str.codePointAt(0));
              if (m == -1) {
                continue;
              }
              addOrder(i, m);
              continue;
            }
          }
          int k = getContractOrder(str);
          if (k != -1)
          {
            addOrder(i, k);
          }
          else
          {
            m = 1;
            for (int n = 0; n < str.length(); n++) {
              if (getCharOrder(str.charAt(n)) == -1)
              {
                m = 0;
                break;
              }
            }
            if (m != 0) {
              addExpandOrder(i, str, -1);
            }
          }
        }
      }
    }
  }
  
  private final void commit()
  {
    if (expandTable != null) {
      for (int i = 0; i < expandTable.size(); i++)
      {
        int[] arrayOfInt = (int[])expandTable.elementAt(i);
        for (int j = 0; j < arrayOfInt.length; j++)
        {
          int k = arrayOfInt[j];
          if ((k < 2113929216) && (k > 1879048192))
          {
            int m = k - 1879048192;
            int n = getCharOrder(m);
            if (n == -1) {
              arrayOfInt[j] = (0xFFFF & m);
            } else {
              arrayOfInt[j] = n;
            }
          }
        }
      }
    }
  }
  
  private final int increment(int paramInt1, int paramInt2)
  {
    switch (paramInt1)
    {
    case 0: 
      paramInt2 += 65536;
      paramInt2 &= 0xFFFF0000;
      isOverIgnore = true;
      break;
    case 1: 
      paramInt2 += 256;
      paramInt2 &= 0xFF00;
      if (!isOverIgnore) {
        maxSecOrder = ((short)(maxSecOrder + 1));
      }
      break;
    case 2: 
      paramInt2++;
      if (!isOverIgnore) {
        maxTerOrder = ((short)(maxTerOrder + 1));
      }
      break;
    }
    return paramInt2;
  }
  
  private final void addOrder(int paramInt1, int paramInt2)
  {
    int i = mapping.elementAt(paramInt1);
    if (i >= 2130706432)
    {
      int j = 1;
      if (Character.isSupplementaryCodePoint(paramInt1)) {
        j = Character.toChars(paramInt1, keyBuf, 0);
      } else {
        keyBuf[0] = ((char)paramInt1);
      }
      addContractOrder(new String(keyBuf, 0, j), paramInt2);
    }
    else
    {
      mapping.setElementAt(paramInt1, paramInt2);
    }
  }
  
  private final void addContractOrder(String paramString, int paramInt)
  {
    addContractOrder(paramString, paramInt, true);
  }
  
  private final void addContractOrder(String paramString, int paramInt, boolean paramBoolean)
  {
    if (contractTable == null) {
      contractTable = new Vector(20);
    }
    int i = paramString.codePointAt(0);
    int j = mapping.elementAt(i);
    Vector localVector = getContractValuesImpl(j - 2130706432);
    if (localVector == null)
    {
      k = 2130706432 + contractTable.size();
      localVector = new Vector(20);
      contractTable.addElement(localVector);
      localVector.addElement(new EntryPair(paramString.substring(0, Character.charCount(i)), j));
      mapping.setElementAt(i, k);
    }
    int k = RBCollationTables.getEntry(localVector, paramString, paramBoolean);
    EntryPair localEntryPair;
    if (k != -1)
    {
      localEntryPair = (EntryPair)localVector.elementAt(k);
      value = paramInt;
    }
    else
    {
      localEntryPair = (EntryPair)localVector.lastElement();
      if (paramString.length() > entryName.length()) {
        localVector.addElement(new EntryPair(paramString, paramInt, paramBoolean));
      } else {
        localVector.insertElementAt(new EntryPair(paramString, paramInt, paramBoolean), localVector.size() - 1);
      }
    }
    if ((paramBoolean) && (paramString.length() > 1))
    {
      addContractFlags(paramString);
      addContractOrder(new StringBuffer(paramString).reverse().toString(), paramInt, false);
    }
  }
  
  private int getContractOrder(String paramString)
  {
    int i = -1;
    if (contractTable != null)
    {
      int j = paramString.codePointAt(0);
      Vector localVector = getContractValues(j);
      if (localVector != null)
      {
        int k = RBCollationTables.getEntry(localVector, paramString, true);
        if (k != -1)
        {
          EntryPair localEntryPair = (EntryPair)localVector.elementAt(k);
          i = value;
        }
      }
    }
    return i;
  }
  
  private final int getCharOrder(int paramInt)
  {
    int i = mapping.elementAt(paramInt);
    if (i >= 2130706432)
    {
      Vector localVector = getContractValuesImpl(i - 2130706432);
      EntryPair localEntryPair = (EntryPair)localVector.firstElement();
      i = value;
    }
    return i;
  }
  
  private Vector<EntryPair> getContractValues(int paramInt)
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
  
  private final void addExpandOrder(String paramString1, String paramString2, int paramInt)
    throws ParseException
  {
    int i = addExpansion(paramInt, paramString2);
    if (paramString1.length() > 1)
    {
      char c1 = paramString1.charAt(0);
      if ((Character.isHighSurrogate(c1)) && (paramString1.length() == 2))
      {
        char c2 = paramString1.charAt(1);
        if (Character.isLowSurrogate(c2)) {
          addOrder(Character.toCodePoint(c1, c2), i);
        }
      }
      else
      {
        addContractOrder(paramString1, i);
      }
    }
    else
    {
      addOrder(paramString1.charAt(0), i);
    }
  }
  
  private final void addExpandOrder(int paramInt1, String paramString, int paramInt2)
    throws ParseException
  {
    int i = addExpansion(paramInt2, paramString);
    addOrder(paramInt1, i);
  }
  
  private int addExpansion(int paramInt, String paramString)
  {
    if (expandTable == null) {
      expandTable = new Vector(20);
    }
    int i = paramInt == -1 ? 0 : 1;
    Object localObject = new int[paramString.length() + i];
    if (i == 1) {
      localObject[0] = paramInt;
    }
    int j = i;
    for (int k = 0; k < paramString.length(); k++)
    {
      int n = paramString.charAt(k);
      int i1;
      if (Character.isHighSurrogate(n))
      {
        k++;
        char c;
        if ((k == paramString.length()) || (!Character.isLowSurrogate(c = paramString.charAt(k)))) {
          break;
        }
        i1 = Character.toCodePoint(n, c);
      }
      else
      {
        i1 = n;
      }
      int i2 = getCharOrder(i1);
      if (i2 != -1) {
        localObject[(j++)] = i2;
      } else {
        localObject[(j++)] = (1879048192 + i1);
      }
    }
    if (j < localObject.length)
    {
      int[] arrayOfInt = new int[j];
      for (;;)
      {
        j--;
        if (j < 0) {
          break;
        }
        arrayOfInt[j] = localObject[j];
      }
      localObject = arrayOfInt;
    }
    int m = 2113929216 + expandTable.size();
    expandTable.addElement(localObject);
    return m;
  }
  
  private void addContractFlags(String paramString)
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      char c1 = paramString.charAt(j);
      char c2 = Character.isHighSurrogate(c1) ? Character.toCodePoint(c1, paramString.charAt(++j)) : c1;
      contractFlags.put(c2, 1);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\RBTableBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */