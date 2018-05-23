package sun.net.www;

import java.util.Iterator;

public class HeaderParser
{
  String raw;
  String[][] tab;
  int nkeys;
  int asize = 10;
  
  public HeaderParser(String paramString)
  {
    raw = paramString;
    tab = new String[asize][2];
    parse();
  }
  
  private HeaderParser() {}
  
  public HeaderParser subsequence(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) && (paramInt2 == nkeys)) {
      return this;
    }
    if ((paramInt1 < 0) || (paramInt1 >= paramInt2) || (paramInt2 > nkeys)) {
      throw new IllegalArgumentException("invalid start or end");
    }
    HeaderParser localHeaderParser = new HeaderParser();
    tab = new String[asize][2];
    asize = asize;
    System.arraycopy(tab, paramInt1, tab, 0, paramInt2 - paramInt1);
    nkeys = (paramInt2 - paramInt1);
    return localHeaderParser;
  }
  
  private void parse()
  {
    if (raw != null)
    {
      raw = raw.trim();
      char[] arrayOfChar = raw.toCharArray();
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 1;
      int n = 0;
      int i1 = arrayOfChar.length;
      while (j < i1)
      {
        int i2 = arrayOfChar[j];
        if ((i2 == 61) && (n == 0))
        {
          tab[k][0] = new String(arrayOfChar, i, j - i).toLowerCase();
          m = 0;
          j++;
          i = j;
        }
        else if (i2 == 34)
        {
          if (n != 0)
          {
            tab[(k++)][1] = new String(arrayOfChar, i, j - i);
            n = 0;
            do
            {
              j++;
            } while ((j < i1) && ((arrayOfChar[j] == ' ') || (arrayOfChar[j] == ',')));
            m = 1;
            i = j;
          }
          else
          {
            n = 1;
            j++;
            i = j;
          }
        }
        else if ((i2 == 32) || (i2 == 44))
        {
          if (n != 0)
          {
            j++;
            continue;
          }
          if (m != 0) {
            tab[(k++)][0] = new String(arrayOfChar, i, j - i).toLowerCase();
          } else {
            tab[(k++)][1] = new String(arrayOfChar, i, j - i);
          }
          while ((j < i1) && ((arrayOfChar[j] == ' ') || (arrayOfChar[j] == ','))) {
            j++;
          }
          m = 1;
          i = j;
        }
        else
        {
          j++;
        }
        if (k == asize)
        {
          asize *= 2;
          String[][] arrayOfString = new String[asize][2];
          System.arraycopy(tab, 0, arrayOfString, 0, tab.length);
          tab = arrayOfString;
        }
      }
      j--;
      if (j > i)
      {
        if (m == 0)
        {
          if (arrayOfChar[j] == '"') {
            tab[(k++)][1] = new String(arrayOfChar, i, j - i);
          } else {
            tab[(k++)][1] = new String(arrayOfChar, i, j - i + 1);
          }
        }
        else {
          tab[(k++)][0] = new String(arrayOfChar, i, j - i + 1).toLowerCase();
        }
      }
      else if (j == i) {
        if (m == 0)
        {
          if (arrayOfChar[j] == '"') {
            tab[(k++)][1] = String.valueOf(arrayOfChar[(j - 1)]);
          } else {
            tab[(k++)][1] = String.valueOf(arrayOfChar[j]);
          }
        }
        else {
          tab[(k++)][0] = String.valueOf(arrayOfChar[j]).toLowerCase();
        }
      }
      nkeys = k;
    }
  }
  
  public String findKey(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > asize)) {
      return null;
    }
    return tab[paramInt][0];
  }
  
  public String findValue(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > asize)) {
      return null;
    }
    return tab[paramInt][1];
  }
  
  public String findValue(String paramString)
  {
    return findValue(paramString, null);
  }
  
  public String findValue(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      return paramString2;
    }
    paramString1 = paramString1.toLowerCase();
    for (int i = 0; i < asize; i++)
    {
      if (tab[i][0] == null) {
        return paramString2;
      }
      if (paramString1.equals(tab[i][0])) {
        return tab[i][1];
      }
    }
    return paramString2;
  }
  
  public Iterator<String> keys()
  {
    return new ParserIterator(false);
  }
  
  public Iterator<String> values()
  {
    return new ParserIterator(true);
  }
  
  public String toString()
  {
    Iterator localIterator = keys();
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("{size=" + asize + " nkeys=" + nkeys + " ");
    for (int i = 0; localIterator.hasNext(); i++)
    {
      String str1 = (String)localIterator.next();
      String str2 = findValue(i);
      if ((str2 != null) && ("".equals(str2))) {
        str2 = null;
      }
      localStringBuffer.append(" {" + str1 + (str2 == null ? "" : new StringBuilder().append(",").append(str2).toString()) + "}");
      if (localIterator.hasNext()) {
        localStringBuffer.append(",");
      }
    }
    localStringBuffer.append(" }");
    return new String(localStringBuffer);
  }
  
  public int findInt(String paramString, int paramInt)
  {
    try
    {
      return Integer.parseInt(findValue(paramString, String.valueOf(paramInt)));
    }
    catch (Throwable localThrowable) {}
    return paramInt;
  }
  
  class ParserIterator
    implements Iterator<String>
  {
    int index;
    boolean returnsValue;
    
    ParserIterator(boolean paramBoolean)
    {
      returnsValue = paramBoolean;
    }
    
    public boolean hasNext()
    {
      return index < nkeys;
    }
    
    public String next()
    {
      return tab[(index++)][0];
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException("remove not supported");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\HeaderParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */