package java.text;

import java.util.ArrayList;

final class MergeCollation
{
  ArrayList<PatternEntry> patterns = new ArrayList();
  private transient PatternEntry saveEntry = null;
  private transient PatternEntry lastEntry = null;
  private transient StringBuffer excess = new StringBuffer();
  private transient byte[] statusArray = new byte[' '];
  private final byte BITARRAYMASK = 1;
  private final int BYTEPOWER = 3;
  private final int BYTEMASK = 7;
  
  public MergeCollation(String paramString)
    throws ParseException
  {
    for (int i = 0; i < statusArray.length; i++) {
      statusArray[i] = 0;
    }
    setPattern(paramString);
  }
  
  public String getPattern()
  {
    return getPattern(true);
  }
  
  public String getPattern(boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    PatternEntry localPatternEntry1 = null;
    ArrayList localArrayList = null;
    PatternEntry localPatternEntry2;
    for (int i = 0; i < patterns.size(); i++)
    {
      localPatternEntry2 = (PatternEntry)patterns.get(i);
      if (extension.length() != 0)
      {
        if (localArrayList == null) {
          localArrayList = new ArrayList();
        }
        localArrayList.add(localPatternEntry2);
      }
      else
      {
        if (localArrayList != null)
        {
          PatternEntry localPatternEntry3 = findLastWithNoExtension(i - 1);
          for (int k = localArrayList.size() - 1; k >= 0; k--)
          {
            localPatternEntry1 = (PatternEntry)localArrayList.get(k);
            localPatternEntry1.addToBuffer(localStringBuffer, false, paramBoolean, localPatternEntry3);
          }
          localArrayList = null;
        }
        localPatternEntry2.addToBuffer(localStringBuffer, false, paramBoolean, null);
      }
    }
    if (localArrayList != null)
    {
      localPatternEntry2 = findLastWithNoExtension(i - 1);
      for (int j = localArrayList.size() - 1; j >= 0; j--)
      {
        localPatternEntry1 = (PatternEntry)localArrayList.get(j);
        localPatternEntry1.addToBuffer(localStringBuffer, false, paramBoolean, localPatternEntry2);
      }
      localArrayList = null;
    }
    return localStringBuffer.toString();
  }
  
  private final PatternEntry findLastWithNoExtension(int paramInt)
  {
    
    while (paramInt >= 0)
    {
      PatternEntry localPatternEntry = (PatternEntry)patterns.get(paramInt);
      if (extension.length() == 0) {
        return localPatternEntry;
      }
      paramInt--;
    }
    return null;
  }
  
  public String emitPattern()
  {
    return emitPattern(true);
  }
  
  public String emitPattern(boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < patterns.size(); i++)
    {
      PatternEntry localPatternEntry = (PatternEntry)patterns.get(i);
      if (localPatternEntry != null) {
        localPatternEntry.addToBuffer(localStringBuffer, true, paramBoolean, null);
      }
    }
    return localStringBuffer.toString();
  }
  
  public void setPattern(String paramString)
    throws ParseException
  {
    patterns.clear();
    addPattern(paramString);
  }
  
  public void addPattern(String paramString)
    throws ParseException
  {
    if (paramString == null) {
      return;
    }
    PatternEntry.Parser localParser = new PatternEntry.Parser(paramString);
    for (PatternEntry localPatternEntry = localParser.next(); localPatternEntry != null; localPatternEntry = localParser.next()) {
      fixEntry(localPatternEntry);
    }
  }
  
  public int getCount()
  {
    return patterns.size();
  }
  
  public PatternEntry getItemAt(int paramInt)
  {
    return (PatternEntry)patterns.get(paramInt);
  }
  
  private final void fixEntry(PatternEntry paramPatternEntry)
    throws ParseException
  {
    if ((lastEntry != null) && (chars.equals(lastEntry.chars)) && (extension.equals(lastEntry.extension)))
    {
      if ((strength != 3) && (strength != -2)) {
        throw new ParseException("The entries " + lastEntry + " and " + paramPatternEntry + " are adjacent in the rules, but have conflicting strengths: A character can't be unequal to itself.", -1);
      }
      return;
    }
    int i = 1;
    if (strength != -2)
    {
      int j = -1;
      if (chars.length() == 1)
      {
        k = chars.charAt(0);
        int m = k >> 3;
        int n = statusArray[m];
        int i1 = (byte)(1 << (k & 0x7));
        if ((n != 0) && ((n & i1) != 0)) {
          j = patterns.lastIndexOf(paramPatternEntry);
        } else {
          statusArray[m] = ((byte)(n | i1));
        }
      }
      else
      {
        j = patterns.lastIndexOf(paramPatternEntry);
      }
      if (j != -1) {
        patterns.remove(j);
      }
      excess.setLength(0);
      int k = findLastEntry(lastEntry, excess);
      if (excess.length() != 0)
      {
        extension = (excess + extension);
        if (k != patterns.size())
        {
          lastEntry = saveEntry;
          i = 0;
        }
      }
      if (k == patterns.size())
      {
        patterns.add(paramPatternEntry);
        saveEntry = paramPatternEntry;
      }
      else
      {
        patterns.add(k, paramPatternEntry);
      }
    }
    if (i != 0) {
      lastEntry = paramPatternEntry;
    }
  }
  
  private final int findLastEntry(PatternEntry paramPatternEntry, StringBuffer paramStringBuffer)
    throws ParseException
  {
    if (paramPatternEntry == null) {
      return 0;
    }
    if (strength != -2)
    {
      i = -1;
      if (chars.length() == 1)
      {
        int j = chars.charAt(0) >> '\003';
        if ((statusArray[j] & '\001' << (chars.charAt(0) & 0x7)) != 0) {
          i = patterns.lastIndexOf(paramPatternEntry);
        }
      }
      else
      {
        i = patterns.lastIndexOf(paramPatternEntry);
      }
      if (i == -1) {
        throw new ParseException("couldn't find last entry: " + paramPatternEntry, i);
      }
      return i + 1;
    }
    for (int i = patterns.size() - 1; i >= 0; i--)
    {
      PatternEntry localPatternEntry = (PatternEntry)patterns.get(i);
      if (chars.regionMatches(0, chars, 0, chars.length()))
      {
        paramStringBuffer.append(chars.substring(chars.length(), chars.length()));
        break;
      }
    }
    if (i == -1) {
      throw new ParseException("couldn't find: " + paramPatternEntry, i);
    }
    return i + 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\MergeCollation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */