package com.sun.org.apache.xml.internal.utils;

import java.text.CollationElementIterator;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Locale;

public class StringComparable
  implements Comparable
{
  public static final int UNKNOWN_CASE = -1;
  public static final int UPPER_CASE = 1;
  public static final int LOWER_CASE = 2;
  private String m_text;
  private Locale m_locale;
  private RuleBasedCollator m_collator;
  private String m_caseOrder;
  private int m_mask = -1;
  
  public StringComparable(String paramString1, Locale paramLocale, Collator paramCollator, String paramString2)
  {
    m_text = paramString1;
    m_locale = paramLocale;
    m_collator = ((RuleBasedCollator)paramCollator);
    m_caseOrder = paramString2;
    m_mask = getMask(m_collator.getStrength());
  }
  
  public static final Comparable getComparator(String paramString1, Locale paramLocale, Collator paramCollator, String paramString2)
  {
    if ((paramString2 == null) || (paramString2.length() == 0)) {
      return ((RuleBasedCollator)paramCollator).getCollationKey(paramString1);
    }
    return new StringComparable(paramString1, paramLocale, paramCollator, paramString2);
  }
  
  public final String toString()
  {
    return m_text;
  }
  
  public int compareTo(Object paramObject)
  {
    String str = ((StringComparable)paramObject).toString();
    if (m_text.equals(str)) {
      return 0;
    }
    int i = m_collator.getStrength();
    int j = 0;
    if ((i == 0) || (i == 1))
    {
      j = m_collator.compare(m_text, str);
    }
    else
    {
      m_collator.setStrength(1);
      j = m_collator.compare(m_text, str);
      m_collator.setStrength(i);
    }
    if (j != 0) {
      return j;
    }
    j = getCaseDiff(m_text, str);
    if (j != 0) {
      return j;
    }
    return m_collator.compare(m_text, str);
  }
  
  private final int getCaseDiff(String paramString1, String paramString2)
  {
    int i = m_collator.getStrength();
    int j = m_collator.getDecomposition();
    m_collator.setStrength(2);
    m_collator.setDecomposition(1);
    int[] arrayOfInt = getFirstCaseDiff(paramString1, paramString2, m_locale);
    m_collator.setStrength(i);
    m_collator.setDecomposition(j);
    if (arrayOfInt != null)
    {
      if (m_caseOrder.equals("upper-first"))
      {
        if (arrayOfInt[0] == 1) {
          return -1;
        }
        return 1;
      }
      if (arrayOfInt[0] == 2) {
        return -1;
      }
      return 1;
    }
    return 0;
  }
  
  private final int[] getFirstCaseDiff(String paramString1, String paramString2, Locale paramLocale)
  {
    CollationElementIterator localCollationElementIterator1 = m_collator.getCollationElementIterator(paramString1);
    CollationElementIterator localCollationElementIterator2 = m_collator.getCollationElementIterator(paramString2);
    int i = -1;
    int j = -1;
    int k = -1;
    int m = -1;
    int n = getElement(-1);
    int i1 = 0;
    int i2 = 0;
    int i3 = 1;
    int i4 = 1;
    int[] arrayOfInt;
    do
    {
      String str1;
      String str2;
      String str3;
      String str4;
      do
      {
        do
        {
          for (;;)
          {
            if (i3 != 0)
            {
              k = localCollationElementIterator2.getOffset();
              i1 = getElement(localCollationElementIterator2.next());
              m = localCollationElementIterator2.getOffset();
            }
            if (i4 != 0)
            {
              i = localCollationElementIterator1.getOffset();
              i2 = getElement(localCollationElementIterator1.next());
              j = localCollationElementIterator1.getOffset();
            }
            i4 = i3 = 1;
            if ((i1 == n) || (i2 == n)) {
              return null;
            }
            if (i2 == 0)
            {
              i3 = 0;
            }
            else
            {
              if (i1 != 0) {
                break;
              }
              i4 = 0;
            }
          }
        } while ((i2 == i1) || (k >= m) || (i >= j));
        str1 = paramString1.substring(i, j);
        str2 = paramString2.substring(k, m);
        str3 = str1.toUpperCase(paramLocale);
        str4 = str2.toUpperCase(paramLocale);
      } while (m_collator.compare(str3, str4) != 0);
      arrayOfInt = new int[] { -1, -1 };
      if (m_collator.compare(str1, str3) == 0) {
        arrayOfInt[0] = 1;
      } else if (m_collator.compare(str1, str1.toLowerCase(paramLocale)) == 0) {
        arrayOfInt[0] = 2;
      }
      if (m_collator.compare(str2, str4) == 0) {
        arrayOfInt[1] = 1;
      } else if (m_collator.compare(str2, str2.toLowerCase(paramLocale)) == 0) {
        arrayOfInt[1] = 2;
      }
    } while (((arrayOfInt[0] != 1) || (arrayOfInt[1] != 2)) && ((arrayOfInt[0] != 2) || (arrayOfInt[1] != 1)));
    return arrayOfInt;
  }
  
  private static final int getMask(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return -65536;
    case 1: 
      return 65280;
    }
    return -1;
  }
  
  private final int getElement(int paramInt)
  {
    return paramInt & m_mask;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\StringComparable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */