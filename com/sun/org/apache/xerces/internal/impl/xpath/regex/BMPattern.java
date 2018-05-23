package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.text.CharacterIterator;

public class BMPattern
{
  char[] pattern;
  int[] shiftTable;
  boolean ignoreCase;
  
  public BMPattern(String paramString, boolean paramBoolean)
  {
    this(paramString, 256, paramBoolean);
  }
  
  public BMPattern(String paramString, int paramInt, boolean paramBoolean)
  {
    pattern = paramString.toCharArray();
    shiftTable = new int[paramInt];
    ignoreCase = paramBoolean;
    int i = pattern.length;
    for (int j = 0; j < shiftTable.length; j++) {
      shiftTable[j] = i;
    }
    for (j = 0; j < i; j++)
    {
      int k = pattern[j];
      int i1 = i - j - 1;
      int i2 = k % shiftTable.length;
      if (i1 < shiftTable[i2]) {
        shiftTable[i2] = i1;
      }
      if (ignoreCase)
      {
        int m = Character.toUpperCase(k);
        i2 = m % shiftTable.length;
        if (i1 < shiftTable[i2]) {
          shiftTable[i2] = i1;
        }
        int n = Character.toLowerCase(m);
        i2 = n % shiftTable.length;
        if (i1 < shiftTable[i2]) {
          shiftTable[i2] = i1;
        }
      }
    }
  }
  
  public int matches(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2)
  {
    if (ignoreCase) {
      return matchesIgnoreCase(paramCharacterIterator, paramInt1, paramInt2);
    }
    int i = pattern.length;
    if (i == 0) {
      return paramInt1;
    }
    int j = paramInt1 + i;
    while (j <= paramInt2)
    {
      int k = i;
      int m = j + 1;
      int n;
      do
      {
        if ((n = paramCharacterIterator.setIndex(--j)) != pattern[(--k)]) {
          break;
        }
        if (k == 0) {
          return j;
        }
      } while (k > 0);
      j += shiftTable[(n % shiftTable.length)] + 1;
      if (j < m) {
        j = m;
      }
    }
    return -1;
  }
  
  public int matches(String paramString, int paramInt1, int paramInt2)
  {
    if (ignoreCase) {
      return matchesIgnoreCase(paramString, paramInt1, paramInt2);
    }
    int i = pattern.length;
    if (i == 0) {
      return paramInt1;
    }
    int j = paramInt1 + i;
    while (j <= paramInt2)
    {
      int k = i;
      int m = j + 1;
      int n;
      do
      {
        if ((n = paramString.charAt(--j)) != pattern[(--k)]) {
          break;
        }
        if (k == 0) {
          return j;
        }
      } while (k > 0);
      j += shiftTable[(n % shiftTable.length)] + 1;
      if (j < m) {
        j = m;
      }
    }
    return -1;
  }
  
  public int matches(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (ignoreCase) {
      return matchesIgnoreCase(paramArrayOfChar, paramInt1, paramInt2);
    }
    int i = pattern.length;
    if (i == 0) {
      return paramInt1;
    }
    int j = paramInt1 + i;
    while (j <= paramInt2)
    {
      int k = i;
      int m = j + 1;
      int n;
      do
      {
        if ((n = paramArrayOfChar[(--j)]) != pattern[(--k)]) {
          break;
        }
        if (k == 0) {
          return j;
        }
      } while (k > 0);
      j += shiftTable[(n % shiftTable.length)] + 1;
      if (j < m) {
        j = m;
      }
    }
    return -1;
  }
  
  int matchesIgnoreCase(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2)
  {
    int i = pattern.length;
    if (i == 0) {
      return paramInt1;
    }
    int j = paramInt1 + i;
    while (j <= paramInt2)
    {
      int k = i;
      int m = j + 1;
      int n;
      do
      {
        char c1 = n = paramCharacterIterator.setIndex(--j);
        char c2 = pattern[(--k)];
        if (c1 != c2)
        {
          c1 = Character.toUpperCase(c1);
          c2 = Character.toUpperCase(c2);
          if ((c1 != c2) && (Character.toLowerCase(c1) != Character.toLowerCase(c2))) {
            break;
          }
        }
        if (k == 0) {
          return j;
        }
      } while (k > 0);
      j += shiftTable[(n % shiftTable.length)] + 1;
      if (j < m) {
        j = m;
      }
    }
    return -1;
  }
  
  int matchesIgnoreCase(String paramString, int paramInt1, int paramInt2)
  {
    int i = pattern.length;
    if (i == 0) {
      return paramInt1;
    }
    int j = paramInt1 + i;
    while (j <= paramInt2)
    {
      int k = i;
      int m = j + 1;
      int n;
      do
      {
        char c1 = n = paramString.charAt(--j);
        char c2 = pattern[(--k)];
        if (c1 != c2)
        {
          c1 = Character.toUpperCase(c1);
          c2 = Character.toUpperCase(c2);
          if ((c1 != c2) && (Character.toLowerCase(c1) != Character.toLowerCase(c2))) {
            break;
          }
        }
        if (k == 0) {
          return j;
        }
      } while (k > 0);
      j += shiftTable[(n % shiftTable.length)] + 1;
      if (j < m) {
        j = m;
      }
    }
    return -1;
  }
  
  int matchesIgnoreCase(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = pattern.length;
    if (i == 0) {
      return paramInt1;
    }
    int j = paramInt1 + i;
    while (j <= paramInt2)
    {
      int k = i;
      int m = j + 1;
      int n;
      do
      {
        char c1 = n = paramArrayOfChar[(--j)];
        char c2 = pattern[(--k)];
        if (c1 != c2)
        {
          c1 = Character.toUpperCase(c1);
          c2 = Character.toUpperCase(c2);
          if ((c1 != c2) && (Character.toLowerCase(c1) != Character.toLowerCase(c2))) {
            break;
          }
        }
        if (k == 0) {
          return j;
        }
      } while (k > 0);
      j += shiftTable[(n % shiftTable.length)] + 1;
      if (j < m) {
        j = m;
      }
    }
    return -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\regex\BMPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */