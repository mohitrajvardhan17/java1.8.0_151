package java.text;

public class RuleBasedCollator
  extends Collator
{
  static final int CHARINDEX = 1879048192;
  static final int EXPANDCHARINDEX = 2113929216;
  static final int CONTRACTCHARINDEX = 2130706432;
  static final int UNMAPPED = -1;
  private static final int COLLATIONKEYOFFSET = 1;
  private RBCollationTables tables = null;
  private StringBuffer primResult = null;
  private StringBuffer secResult = null;
  private StringBuffer terResult = null;
  private CollationElementIterator sourceCursor = null;
  private CollationElementIterator targetCursor = null;
  
  public RuleBasedCollator(String paramString)
    throws ParseException
  {
    this(paramString, 1);
  }
  
  RuleBasedCollator(String paramString, int paramInt)
    throws ParseException
  {
    setStrength(2);
    setDecomposition(paramInt);
    tables = new RBCollationTables(paramString, paramInt);
  }
  
  private RuleBasedCollator(RuleBasedCollator paramRuleBasedCollator)
  {
    setStrength(paramRuleBasedCollator.getStrength());
    setDecomposition(paramRuleBasedCollator.getDecomposition());
    tables = tables;
  }
  
  public String getRules()
  {
    return tables.getRules();
  }
  
  public CollationElementIterator getCollationElementIterator(String paramString)
  {
    return new CollationElementIterator(paramString, this);
  }
  
  public CollationElementIterator getCollationElementIterator(CharacterIterator paramCharacterIterator)
  {
    return new CollationElementIterator(paramCharacterIterator, this);
  }
  
  public synchronized int compare(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      throw new NullPointerException();
    }
    int i = 0;
    if (sourceCursor == null) {
      sourceCursor = getCollationElementIterator(paramString1);
    } else {
      sourceCursor.setText(paramString1);
    }
    if (targetCursor == null) {
      targetCursor = getCollationElementIterator(paramString2);
    } else {
      targetCursor.setText(paramString2);
    }
    int j = 0;
    int k = 0;
    int m = getStrength() >= 1 ? 1 : 0;
    int n = m;
    int i1 = getStrength() >= 2 ? 1 : 0;
    int i2 = 1;
    int i3 = 1;
    int i4;
    for (;;)
    {
      if (i2 != 0) {
        j = sourceCursor.next();
      } else {
        i2 = 1;
      }
      if (i3 != 0) {
        k = targetCursor.next();
      } else {
        i3 = 1;
      }
      if ((j == -1) || (k == -1)) {
        break;
      }
      i4 = CollationElementIterator.primaryOrder(j);
      int i5 = CollationElementIterator.primaryOrder(k);
      if (j == k)
      {
        if ((tables.isFrenchSec()) && (i4 != 0) && (n == 0))
        {
          n = m;
          i1 = 0;
        }
      }
      else if (i4 != i5)
      {
        if (j == 0)
        {
          i3 = 0;
        }
        else if (k == 0)
        {
          i2 = 0;
        }
        else if (i4 == 0)
        {
          if (n != 0)
          {
            i = 1;
            n = 0;
          }
          i3 = 0;
        }
        else if (i5 == 0)
        {
          if (n != 0)
          {
            i = -1;
            n = 0;
          }
          i2 = 0;
        }
        else
        {
          if (i4 < i5) {
            return -1;
          }
          return 1;
        }
      }
      else if (n != 0)
      {
        int i6 = CollationElementIterator.secondaryOrder(j);
        int i7 = CollationElementIterator.secondaryOrder(k);
        if (i6 != i7)
        {
          i = i6 < i7 ? -1 : 1;
          n = 0;
        }
        else if (i1 != 0)
        {
          int i8 = CollationElementIterator.tertiaryOrder(j);
          int i9 = CollationElementIterator.tertiaryOrder(k);
          if (i8 != i9)
          {
            i = i8 < i9 ? -1 : 1;
            i1 = 0;
          }
        }
      }
    }
    if (j != -1) {
      do
      {
        if (CollationElementIterator.primaryOrder(j) != 0) {
          return 1;
        }
        if ((CollationElementIterator.secondaryOrder(j) != 0) && (n != 0))
        {
          i = 1;
          n = 0;
        }
      } while ((j = sourceCursor.next()) != -1);
    } else if (k != -1) {
      do
      {
        if (CollationElementIterator.primaryOrder(k) != 0) {
          return -1;
        }
        if ((CollationElementIterator.secondaryOrder(k) != 0) && (n != 0))
        {
          i = -1;
          n = 0;
        }
      } while ((k = targetCursor.next()) != -1);
    }
    if ((i == 0) && (getStrength() == 3))
    {
      i4 = getDecomposition();
      Normalizer.Form localForm;
      if (i4 == 1) {
        localForm = Normalizer.Form.NFD;
      } else if (i4 == 2) {
        localForm = Normalizer.Form.NFKD;
      } else {
        return paramString1.compareTo(paramString2);
      }
      String str1 = Normalizer.normalize(paramString1, localForm);
      String str2 = Normalizer.normalize(paramString2, localForm);
      return str1.compareTo(str2);
    }
    return i;
  }
  
  public synchronized CollationKey getCollationKey(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    if (primResult == null)
    {
      primResult = new StringBuffer();
      secResult = new StringBuffer();
      terResult = new StringBuffer();
    }
    else
    {
      primResult.setLength(0);
      secResult.setLength(0);
      terResult.setLength(0);
    }
    int i = 0;
    int j = getStrength() >= 1 ? 1 : 0;
    int k = getStrength() >= 2 ? 1 : 0;
    int m = -1;
    int n = -1;
    int i1 = 0;
    if (sourceCursor == null) {
      sourceCursor = getCollationElementIterator(paramString);
    } else {
      sourceCursor.setText(paramString);
    }
    while ((i = sourceCursor.next()) != -1)
    {
      m = CollationElementIterator.secondaryOrder(i);
      n = CollationElementIterator.tertiaryOrder(i);
      if (!CollationElementIterator.isIgnorable(i))
      {
        primResult.append((char)(CollationElementIterator.primaryOrder(i) + 1));
        if (j != 0)
        {
          if ((tables.isFrenchSec()) && (i1 < secResult.length())) {
            RBCollationTables.reverse(secResult, i1, secResult.length());
          }
          secResult.append((char)(m + 1));
          i1 = secResult.length();
        }
        if (k != 0) {
          terResult.append((char)(n + 1));
        }
      }
      else
      {
        if ((j != 0) && (m != 0)) {
          secResult.append((char)(m + tables.getMaxSecOrder() + 1));
        }
        if ((k != 0) && (n != 0)) {
          terResult.append((char)(n + tables.getMaxTerOrder() + 1));
        }
      }
    }
    if (tables.isFrenchSec())
    {
      if (i1 < secResult.length()) {
        RBCollationTables.reverse(secResult, i1, secResult.length());
      }
      RBCollationTables.reverse(secResult, 0, secResult.length());
    }
    primResult.append('\000');
    secResult.append('\000');
    secResult.append(terResult.toString());
    primResult.append(secResult.toString());
    if (getStrength() == 3)
    {
      primResult.append('\000');
      int i2 = getDecomposition();
      if (i2 == 1) {
        primResult.append(Normalizer.normalize(paramString, Normalizer.Form.NFD));
      } else if (i2 == 2) {
        primResult.append(Normalizer.normalize(paramString, Normalizer.Form.NFKD));
      } else {
        primResult.append(paramString);
      }
    }
    return new RuleBasedCollationKey(paramString, primResult.toString());
  }
  
  public Object clone()
  {
    if (getClass() == RuleBasedCollator.class) {
      return new RuleBasedCollator(this);
    }
    RuleBasedCollator localRuleBasedCollator = (RuleBasedCollator)super.clone();
    primResult = null;
    secResult = null;
    terResult = null;
    sourceCursor = null;
    targetCursor = null;
    return localRuleBasedCollator;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (!super.equals(paramObject)) {
      return false;
    }
    RuleBasedCollator localRuleBasedCollator = (RuleBasedCollator)paramObject;
    return getRules().equals(localRuleBasedCollator.getRules());
  }
  
  public int hashCode()
  {
    return getRules().hashCode();
  }
  
  RBCollationTables getTables()
  {
    return tables;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\RuleBasedCollator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */