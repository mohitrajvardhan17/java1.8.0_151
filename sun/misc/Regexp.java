package sun.misc;

public class Regexp
{
  public boolean ignoreCase;
  public String exp;
  public String prefix;
  public String suffix;
  public boolean exact;
  public int prefixLen;
  public int suffixLen;
  public int totalLen;
  public String[] mids;
  
  public Regexp(String paramString)
  {
    exp = paramString;
    int i = paramString.indexOf('*');
    int j = paramString.lastIndexOf('*');
    if (i < 0)
    {
      totalLen = paramString.length();
      exact = true;
    }
    else
    {
      prefixLen = i;
      if (i == 0) {
        prefix = null;
      } else {
        prefix = paramString.substring(0, i);
      }
      suffixLen = (paramString.length() - j - 1);
      if (suffixLen == 0) {
        suffix = null;
      } else {
        suffix = paramString.substring(j + 1);
      }
      int k = 0;
      for (int m = i; (m < j) && (m >= 0); m = paramString.indexOf('*', m + 1)) {
        k++;
      }
      totalLen = (prefixLen + suffixLen);
      if (k > 0)
      {
        mids = new String[k];
        m = i;
        for (int n = 0; n < k; n++)
        {
          m++;
          int i1 = paramString.indexOf('*', m);
          if (m < i1)
          {
            mids[n] = paramString.substring(m, i1);
            totalLen += mids[n].length();
          }
          m = i1;
        }
      }
    }
  }
  
  final boolean matches(String paramString)
  {
    return matches(paramString, 0, paramString.length());
  }
  
  boolean matches(String paramString, int paramInt1, int paramInt2)
  {
    if (exact) {
      return (paramInt2 == totalLen) && (exp.regionMatches(ignoreCase, 0, paramString, paramInt1, paramInt2));
    }
    if (paramInt2 < totalLen) {
      return false;
    }
    if (((prefixLen > 0) && (!prefix.regionMatches(ignoreCase, 0, paramString, paramInt1, prefixLen))) || ((suffixLen > 0) && (!suffix.regionMatches(ignoreCase, 0, paramString, paramInt1 + paramInt2 - suffixLen, suffixLen)))) {
      return false;
    }
    if (mids == null) {
      return true;
    }
    int i = mids.length;
    int j = paramInt1 + prefixLen;
    int k = paramInt1 + paramInt2 - suffixLen;
    for (int m = 0; m < i; m++)
    {
      String str = mids[m];
      int n = str.length();
      while ((j + n <= k) && (!str.regionMatches(ignoreCase, 0, paramString, j, n))) {
        j++;
      }
      if (j + n > k) {
        return false;
      }
      j += n;
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\Regexp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */