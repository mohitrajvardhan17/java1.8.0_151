package java.text;

import java.util.Vector;
import sun.text.CollatorUtilities;
import sun.text.normalizer.NormalizerBase;
import sun.text.normalizer.NormalizerBase.Mode;

public final class CollationElementIterator
{
  public static final int NULLORDER = -1;
  static final int UNMAPPEDCHARVALUE = 2147418112;
  private NormalizerBase text = null;
  private int[] buffer = null;
  private int expIndex = 0;
  private StringBuffer key = new StringBuffer(5);
  private int swapOrder = 0;
  private RBCollationTables ordering;
  private RuleBasedCollator owner;
  
  CollationElementIterator(String paramString, RuleBasedCollator paramRuleBasedCollator)
  {
    owner = paramRuleBasedCollator;
    ordering = paramRuleBasedCollator.getTables();
    if (paramString.length() != 0)
    {
      NormalizerBase.Mode localMode = CollatorUtilities.toNormalizerMode(paramRuleBasedCollator.getDecomposition());
      text = new NormalizerBase(paramString, localMode);
    }
  }
  
  CollationElementIterator(CharacterIterator paramCharacterIterator, RuleBasedCollator paramRuleBasedCollator)
  {
    owner = paramRuleBasedCollator;
    ordering = paramRuleBasedCollator.getTables();
    NormalizerBase.Mode localMode = CollatorUtilities.toNormalizerMode(paramRuleBasedCollator.getDecomposition());
    text = new NormalizerBase(paramCharacterIterator, localMode);
  }
  
  public void reset()
  {
    if (text != null)
    {
      text.reset();
      NormalizerBase.Mode localMode = CollatorUtilities.toNormalizerMode(owner.getDecomposition());
      text.setMode(localMode);
    }
    buffer = null;
    expIndex = 0;
    swapOrder = 0;
  }
  
  public int next()
  {
    if (text == null) {
      return -1;
    }
    NormalizerBase.Mode localMode1 = text.getMode();
    NormalizerBase.Mode localMode2 = CollatorUtilities.toNormalizerMode(owner.getDecomposition());
    if (localMode1 != localMode2) {
      text.setMode(localMode2);
    }
    if (buffer != null)
    {
      if (expIndex < buffer.length) {
        return strengthOrder(buffer[(expIndex++)]);
      }
      buffer = null;
      expIndex = 0;
    }
    else if (swapOrder != 0)
    {
      if (Character.isSupplementaryCodePoint(swapOrder))
      {
        char[] arrayOfChar = Character.toChars(swapOrder);
        swapOrder = arrayOfChar[1];
        return arrayOfChar[0] << '\020';
      }
      i = swapOrder << 16;
      swapOrder = 0;
      return i;
    }
    int i = text.next();
    if (i == -1) {
      return -1;
    }
    int j = ordering.getUnicodeOrder(i);
    if (j == -1)
    {
      swapOrder = i;
      return 2147418112;
    }
    if (j >= 2130706432) {
      j = nextContractChar(i);
    }
    if (j >= 2113929216)
    {
      buffer = ordering.getExpandValueList(j);
      expIndex = 0;
      j = buffer[(expIndex++)];
    }
    if (ordering.isSEAsianSwapping())
    {
      int k;
      if (isThaiPreVowel(i))
      {
        k = text.next();
        if (isThaiBaseConsonant(k))
        {
          buffer = makeReorderedBuffer(k, j, buffer, true);
          j = buffer[0];
          expIndex = 1;
        }
        else if (k != -1)
        {
          text.previous();
        }
      }
      if (isLaoPreVowel(i))
      {
        k = text.next();
        if (isLaoBaseConsonant(k))
        {
          buffer = makeReorderedBuffer(k, j, buffer, true);
          j = buffer[0];
          expIndex = 1;
        }
        else if (k != -1)
        {
          text.previous();
        }
      }
    }
    return strengthOrder(j);
  }
  
  public int previous()
  {
    if (text == null) {
      return -1;
    }
    NormalizerBase.Mode localMode1 = text.getMode();
    NormalizerBase.Mode localMode2 = CollatorUtilities.toNormalizerMode(owner.getDecomposition());
    if (localMode1 != localMode2) {
      text.setMode(localMode2);
    }
    if (buffer != null)
    {
      if (expIndex > 0) {
        return strengthOrder(buffer[(--expIndex)]);
      }
      buffer = null;
      expIndex = 0;
    }
    else if (swapOrder != 0)
    {
      if (Character.isSupplementaryCodePoint(swapOrder))
      {
        char[] arrayOfChar = Character.toChars(swapOrder);
        swapOrder = arrayOfChar[1];
        return arrayOfChar[0] << '\020';
      }
      i = swapOrder << 16;
      swapOrder = 0;
      return i;
    }
    int i = text.previous();
    if (i == -1) {
      return -1;
    }
    int j = ordering.getUnicodeOrder(i);
    if (j == -1)
    {
      swapOrder = 2147418112;
      return i;
    }
    if (j >= 2130706432) {
      j = prevContractChar(i);
    }
    if (j >= 2113929216)
    {
      buffer = ordering.getExpandValueList(j);
      expIndex = buffer.length;
      j = buffer[(--expIndex)];
    }
    if (ordering.isSEAsianSwapping())
    {
      int k;
      if (isThaiBaseConsonant(i))
      {
        k = text.previous();
        if (isThaiPreVowel(k))
        {
          buffer = makeReorderedBuffer(k, j, buffer, false);
          expIndex = (buffer.length - 1);
          j = buffer[expIndex];
        }
        else
        {
          text.next();
        }
      }
      if (isLaoBaseConsonant(i))
      {
        k = text.previous();
        if (isLaoPreVowel(k))
        {
          buffer = makeReorderedBuffer(k, j, buffer, false);
          expIndex = (buffer.length - 1);
          j = buffer[expIndex];
        }
        else
        {
          text.next();
        }
      }
    }
    return strengthOrder(j);
  }
  
  public static final int primaryOrder(int paramInt)
  {
    paramInt &= 0xFFFF0000;
    return paramInt >>> 16;
  }
  
  public static final short secondaryOrder(int paramInt)
  {
    paramInt &= 0xFF00;
    return (short)(paramInt >> 8);
  }
  
  public static final short tertiaryOrder(int paramInt)
  {
    return (short)(paramInt &= 0xFF);
  }
  
  final int strengthOrder(int paramInt)
  {
    int i = owner.getStrength();
    if (i == 0) {
      paramInt &= 0xFFFF0000;
    } else if (i == 1) {
      paramInt &= 0xFF00;
    }
    return paramInt;
  }
  
  public void setOffset(int paramInt)
  {
    if (text != null) {
      if ((paramInt < text.getBeginIndex()) || (paramInt >= text.getEndIndex()))
      {
        text.setIndexOnly(paramInt);
      }
      else
      {
        int i = text.setIndex(paramInt);
        if (ordering.usedInContractSeq(i))
        {
          while (ordering.usedInContractSeq(i)) {
            i = text.previous();
          }
          int j = text.getIndex();
          while (text.getIndex() <= paramInt)
          {
            j = text.getIndex();
            next();
          }
          text.setIndexOnly(j);
        }
      }
    }
    buffer = null;
    expIndex = 0;
    swapOrder = 0;
  }
  
  public int getOffset()
  {
    return text != null ? text.getIndex() : 0;
  }
  
  public int getMaxExpansion(int paramInt)
  {
    return ordering.getMaxExpansion(paramInt);
  }
  
  public void setText(String paramString)
  {
    buffer = null;
    swapOrder = 0;
    expIndex = 0;
    NormalizerBase.Mode localMode = CollatorUtilities.toNormalizerMode(owner.getDecomposition());
    if (text == null)
    {
      text = new NormalizerBase(paramString, localMode);
    }
    else
    {
      text.setMode(localMode);
      text.setText(paramString);
    }
  }
  
  public void setText(CharacterIterator paramCharacterIterator)
  {
    buffer = null;
    swapOrder = 0;
    expIndex = 0;
    NormalizerBase.Mode localMode = CollatorUtilities.toNormalizerMode(owner.getDecomposition());
    if (text == null)
    {
      text = new NormalizerBase(paramCharacterIterator, localMode);
    }
    else
    {
      text.setMode(localMode);
      text.setText(paramCharacterIterator);
    }
  }
  
  private static final boolean isThaiPreVowel(int paramInt)
  {
    return (paramInt >= 3648) && (paramInt <= 3652);
  }
  
  private static final boolean isThaiBaseConsonant(int paramInt)
  {
    return (paramInt >= 3585) && (paramInt <= 3630);
  }
  
  private static final boolean isLaoPreVowel(int paramInt)
  {
    return (paramInt >= 3776) && (paramInt <= 3780);
  }
  
  private static final boolean isLaoBaseConsonant(int paramInt)
  {
    return (paramInt >= 3713) && (paramInt <= 3758);
  }
  
  private int[] makeReorderedBuffer(int paramInt1, int paramInt2, int[] paramArrayOfInt, boolean paramBoolean)
  {
    int i = ordering.getUnicodeOrder(paramInt1);
    if (i >= 2130706432) {
      i = paramBoolean ? nextContractChar(paramInt1) : prevContractChar(paramInt1);
    }
    int[] arrayOfInt2 = null;
    if (i >= 2113929216) {
      arrayOfInt2 = ordering.getExpandValueList(i);
    }
    int j;
    if (!paramBoolean)
    {
      j = i;
      i = paramInt2;
      paramInt2 = j;
      int[] arrayOfInt3 = arrayOfInt2;
      arrayOfInt2 = paramArrayOfInt;
      paramArrayOfInt = arrayOfInt3;
    }
    int[] arrayOfInt1;
    if ((arrayOfInt2 == null) && (paramArrayOfInt == null))
    {
      arrayOfInt1 = new int[2];
      arrayOfInt1[0] = i;
      arrayOfInt1[1] = paramInt2;
    }
    else
    {
      j = arrayOfInt2 == null ? 1 : arrayOfInt2.length;
      int k = paramArrayOfInt == null ? 1 : paramArrayOfInt.length;
      arrayOfInt1 = new int[j + k];
      if (arrayOfInt2 == null) {
        arrayOfInt1[0] = i;
      } else {
        System.arraycopy(arrayOfInt2, 0, arrayOfInt1, 0, j);
      }
      if (paramArrayOfInt == null) {
        arrayOfInt1[j] = paramInt2;
      } else {
        System.arraycopy(paramArrayOfInt, 0, arrayOfInt1, j, k);
      }
    }
    return arrayOfInt1;
  }
  
  static final boolean isIgnorable(int paramInt)
  {
    return primaryOrder(paramInt) == 0;
  }
  
  private int nextContractChar(int paramInt)
  {
    Vector localVector = ordering.getContractValues(paramInt);
    EntryPair localEntryPair = (EntryPair)localVector.firstElement();
    int i = value;
    localEntryPair = (EntryPair)localVector.lastElement();
    int j = entryName.length();
    NormalizerBase localNormalizerBase = (NormalizerBase)text.clone();
    localNormalizerBase.previous();
    key.setLength(0);
    for (int k = localNormalizerBase.next(); (j > 0) && (k != -1); k = localNormalizerBase.next()) {
      if (Character.isSupplementaryCodePoint(k))
      {
        key.append(Character.toChars(k));
        j -= 2;
      }
      else
      {
        key.append((char)k);
        j--;
      }
    }
    String str = key.toString();
    j = 1;
    for (int m = localVector.size() - 1; m > 0; m--)
    {
      localEntryPair = (EntryPair)localVector.elementAt(m);
      if ((fwd) && (str.startsWith(entryName)) && (entryName.length() > j))
      {
        j = entryName.length();
        i = value;
      }
    }
    while (j > 1)
    {
      k = text.next();
      j -= Character.charCount(k);
    }
    return i;
  }
  
  private int prevContractChar(int paramInt)
  {
    Vector localVector = ordering.getContractValues(paramInt);
    EntryPair localEntryPair = (EntryPair)localVector.firstElement();
    int i = value;
    localEntryPair = (EntryPair)localVector.lastElement();
    int j = entryName.length();
    NormalizerBase localNormalizerBase = (NormalizerBase)text.clone();
    localNormalizerBase.next();
    key.setLength(0);
    for (int k = localNormalizerBase.previous(); (j > 0) && (k != -1); k = localNormalizerBase.previous()) {
      if (Character.isSupplementaryCodePoint(k))
      {
        key.append(Character.toChars(k));
        j -= 2;
      }
      else
      {
        key.append((char)k);
        j--;
      }
    }
    String str = key.toString();
    j = 1;
    for (int m = localVector.size() - 1; m > 0; m--)
    {
      localEntryPair = (EntryPair)localVector.elementAt(m);
      if ((!fwd) && (str.startsWith(entryName)) && (entryName.length() > j))
      {
        j = entryName.length();
        i = value;
      }
    }
    while (j > 1)
    {
      k = text.previous();
      j -= Character.charCount(k);
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\CollationElementIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */