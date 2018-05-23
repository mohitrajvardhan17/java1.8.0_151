package java.awt.datatransfer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

class MimeTypeParameterList
  implements Cloneable
{
  private Hashtable<String, String> parameters = new Hashtable();
  private static final String TSPECIALS = "()<>@,;:\\\"/[]?=";
  
  public MimeTypeParameterList() {}
  
  public MimeTypeParameterList(String paramString)
    throws MimeTypeParseException
  {
    parse(paramString);
  }
  
  public int hashCode()
  {
    int i = 47721858;
    String str = null;
    Enumeration localEnumeration = getNames();
    while (localEnumeration.hasMoreElements())
    {
      str = (String)localEnumeration.nextElement();
      i += str.hashCode();
      i += get(str).hashCode();
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof MimeTypeParameterList)) {
      return false;
    }
    MimeTypeParameterList localMimeTypeParameterList = (MimeTypeParameterList)paramObject;
    if (size() != localMimeTypeParameterList.size()) {
      return false;
    }
    String str1 = null;
    String str2 = null;
    String str3 = null;
    Set localSet = parameters.entrySet();
    Iterator localIterator = localSet.iterator();
    Map.Entry localEntry = null;
    while (localIterator.hasNext())
    {
      localEntry = (Map.Entry)localIterator.next();
      str1 = (String)localEntry.getKey();
      str2 = (String)localEntry.getValue();
      str3 = (String)parameters.get(str1);
      if ((str2 == null) || (str3 == null))
      {
        if (str2 != str3) {
          return false;
        }
      }
      else if (!str2.equals(str3)) {
        return false;
      }
    }
    return true;
  }
  
  protected void parse(String paramString)
    throws MimeTypeParseException
  {
    int i = paramString.length();
    if (i > 0)
    {
      int j = skipWhiteSpace(paramString, 0);
      int k = 0;
      if (j < i)
      {
        char c = paramString.charAt(j);
        while ((j < i) && (c == ';'))
        {
          j++;
          j = skipWhiteSpace(paramString, j);
          if (j < i)
          {
            k = j;
            for (c = paramString.charAt(j); (j < i) && (isTokenChar(c)); c = paramString.charAt(j)) {
              j++;
            }
            String str1 = paramString.substring(k, j).toLowerCase();
            j = skipWhiteSpace(paramString, j);
            if ((j < i) && (paramString.charAt(j) == '='))
            {
              j++;
              j = skipWhiteSpace(paramString, j);
              if (j < i)
              {
                c = paramString.charAt(j);
                int m;
                String str2;
                if (c == '"')
                {
                  j++;
                  k = j;
                  if (j < i)
                  {
                    m = 0;
                    while ((j < i) && (m == 0))
                    {
                      c = paramString.charAt(j);
                      if (c == '\\') {
                        j += 2;
                      } else if (c == '"') {
                        m = 1;
                      } else {
                        j++;
                      }
                    }
                    if (c == '"')
                    {
                      str2 = unquote(paramString.substring(k, j));
                      j++;
                    }
                    else
                    {
                      throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
                    }
                  }
                  else
                  {
                    throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
                  }
                }
                else if (isTokenChar(c))
                {
                  k = j;
                  m = 0;
                  while ((j < i) && (m == 0))
                  {
                    c = paramString.charAt(j);
                    if (isTokenChar(c)) {
                      j++;
                    } else {
                      m = 1;
                    }
                  }
                  str2 = paramString.substring(k, j);
                }
                else
                {
                  throw new MimeTypeParseException("Unexpected character encountered at index " + j);
                }
                parameters.put(str1, str2);
              }
              else
              {
                throw new MimeTypeParseException("Couldn't find a value for parameter named " + str1);
              }
            }
            else
            {
              throw new MimeTypeParseException("Couldn't find the '=' that separates a parameter name from its value.");
            }
          }
          else
          {
            throw new MimeTypeParseException("Couldn't find parameter name");
          }
          j = skipWhiteSpace(paramString, j);
          if (j < i) {
            c = paramString.charAt(j);
          }
        }
        if (j < i) {
          throw new MimeTypeParseException("More characters encountered in input than expected.");
        }
      }
    }
  }
  
  public int size()
  {
    return parameters.size();
  }
  
  public boolean isEmpty()
  {
    return parameters.isEmpty();
  }
  
  public String get(String paramString)
  {
    return (String)parameters.get(paramString.trim().toLowerCase());
  }
  
  public void set(String paramString1, String paramString2)
  {
    parameters.put(paramString1.trim().toLowerCase(), paramString2);
  }
  
  public void remove(String paramString)
  {
    parameters.remove(paramString.trim().toLowerCase());
  }
  
  public Enumeration<String> getNames()
  {
    return parameters.keys();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(parameters.size() * 16);
    Enumeration localEnumeration = parameters.keys();
    while (localEnumeration.hasMoreElements())
    {
      localStringBuilder.append("; ");
      String str = (String)localEnumeration.nextElement();
      localStringBuilder.append(str);
      localStringBuilder.append('=');
      localStringBuilder.append(quote((String)parameters.get(str)));
    }
    return localStringBuilder.toString();
  }
  
  public Object clone()
  {
    MimeTypeParameterList localMimeTypeParameterList = null;
    try
    {
      localMimeTypeParameterList = (MimeTypeParameterList)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    parameters = ((Hashtable)parameters.clone());
    return localMimeTypeParameterList;
  }
  
  private static boolean isTokenChar(char paramChar)
  {
    return (paramChar > ' ') && (paramChar < '') && ("()<>@,;:\\\"/[]?=".indexOf(paramChar) < 0);
  }
  
  private static int skipWhiteSpace(String paramString, int paramInt)
  {
    int i = paramString.length();
    if (paramInt < i) {
      for (char c = paramString.charAt(paramInt); (paramInt < i) && (Character.isWhitespace(c)); c = paramString.charAt(paramInt)) {
        paramInt++;
      }
    }
    return paramInt;
  }
  
  private static String quote(String paramString)
  {
    int i = 0;
    int j = paramString.length();
    for (int k = 0; (k < j) && (i == 0); k++) {
      i = !isTokenChar(paramString.charAt(k)) ? 1 : 0;
    }
    if (i != 0)
    {
      StringBuilder localStringBuilder = new StringBuilder((int)(j * 1.5D));
      localStringBuilder.append('"');
      for (int m = 0; m < j; m++)
      {
        char c = paramString.charAt(m);
        if ((c == '\\') || (c == '"')) {
          localStringBuilder.append('\\');
        }
        localStringBuilder.append(c);
      }
      localStringBuilder.append('"');
      return localStringBuilder.toString();
    }
    return paramString;
  }
  
  private static String unquote(String paramString)
  {
    int i = paramString.length();
    StringBuilder localStringBuilder = new StringBuilder(i);
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      char c = paramString.charAt(k);
      if ((j == 0) && (c != '\\'))
      {
        localStringBuilder.append(c);
      }
      else if (j != 0)
      {
        localStringBuilder.append(c);
        j = 0;
      }
      else
      {
        j = 1;
      }
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\datatransfer\MimeTypeParameterList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */