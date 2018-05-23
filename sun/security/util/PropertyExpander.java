package sun.security.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import sun.net.www.ParseUtil;

public class PropertyExpander
{
  public PropertyExpander() {}
  
  public static String expand(String paramString)
    throws PropertyExpander.ExpandException
  {
    return expand(paramString, false);
  }
  
  public static String expand(String paramString, boolean paramBoolean)
    throws PropertyExpander.ExpandException
  {
    if (paramString == null) {
      return null;
    }
    int i = paramString.indexOf("${", 0);
    if (i == -1) {
      return paramString;
    }
    StringBuffer localStringBuffer = new StringBuffer(paramString.length());
    int j = paramString.length();
    int k = 0;
    while (i < j)
    {
      if (i > k)
      {
        localStringBuffer.append(paramString.substring(k, i));
        k = i;
      }
      int m = i + 2;
      if ((m < j) && (paramString.charAt(m) == '{'))
      {
        m = paramString.indexOf("}}", m);
        if ((m == -1) || (m + 2 == j))
        {
          localStringBuffer.append(paramString.substring(i));
          break;
        }
        m++;
        localStringBuffer.append(paramString.substring(i, m + 1));
      }
      else
      {
        while ((m < j) && (paramString.charAt(m) != '}')) {
          m++;
        }
        if (m == j)
        {
          localStringBuffer.append(paramString.substring(i, m));
          break;
        }
        String str1 = paramString.substring(i + 2, m);
        if (str1.equals("/"))
        {
          localStringBuffer.append(File.separatorChar);
        }
        else
        {
          String str2 = System.getProperty(str1);
          if (str2 != null)
          {
            if (paramBoolean) {
              try
              {
                if ((localStringBuffer.length() > 0) || (!new URI(str2).isAbsolute())) {
                  str2 = ParseUtil.encodePath(str2);
                }
              }
              catch (URISyntaxException localURISyntaxException)
              {
                str2 = ParseUtil.encodePath(str2);
              }
            }
            localStringBuffer.append(str2);
          }
          else
          {
            throw new ExpandException("unable to expand property " + str1);
          }
        }
      }
      k = m + 1;
      i = paramString.indexOf("${", k);
      if (i == -1)
      {
        if (k >= j) {
          break;
        }
        localStringBuffer.append(paramString.substring(k, j));
        break;
      }
    }
    return localStringBuffer.toString();
  }
  
  public static class ExpandException
    extends GeneralSecurityException
  {
    private static final long serialVersionUID = -7941948581406161702L;
    
    public ExpandException(String paramString)
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\PropertyExpander.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */