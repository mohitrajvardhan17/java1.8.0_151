package javax.activation;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

public class MimeTypeParameterList
{
  private Hashtable parameters = new Hashtable();
  private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";
  
  public MimeTypeParameterList() {}
  
  public MimeTypeParameterList(String paramString)
    throws MimeTypeParseException
  {
    parse(paramString);
  }
  
  protected void parse(String paramString)
    throws MimeTypeParseException
  {
    if (paramString == null) {
      return;
    }
    int i = paramString.length();
    if (i <= 0) {
      return;
    }
    char c;
    for (int j = skipWhiteSpace(paramString, 0); (j < i) && ((c = paramString.charAt(j)) == ';'); j = skipWhiteSpace(paramString, j))
    {
      j++;
      j = skipWhiteSpace(paramString, j);
      if (j >= i) {
        return;
      }
      int k = j;
      while ((j < i) && (isTokenChar(paramString.charAt(j)))) {
        j++;
      }
      String str1 = paramString.substring(k, j).toLowerCase(Locale.ENGLISH);
      j = skipWhiteSpace(paramString, j);
      if ((j >= i) || (paramString.charAt(j) != '=')) {
        throw new MimeTypeParseException("Couldn't find the '=' that separates a parameter name from its value.");
      }
      j++;
      j = skipWhiteSpace(paramString, j);
      if (j >= i) {
        throw new MimeTypeParseException("Couldn't find a value for parameter named " + str1);
      }
      c = paramString.charAt(j);
      String str2;
      if (c == '"')
      {
        j++;
        if (j >= i) {
          throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
        }
        k = j;
        while (j < i)
        {
          c = paramString.charAt(j);
          if (c == '"') {
            break;
          }
          if (c == '\\') {
            j++;
          }
          j++;
        }
        if (c != '"') {
          throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
        }
        str2 = unquote(paramString.substring(k, j));
        j++;
      }
      else if (isTokenChar(c))
      {
        k = j;
        while ((j < i) && (isTokenChar(paramString.charAt(j)))) {
          j++;
        }
        str2 = paramString.substring(k, j);
      }
      else
      {
        throw new MimeTypeParseException("Unexpected character encountered at index " + j);
      }
      parameters.put(str1, str2);
    }
    if (j < i) {
      throw new MimeTypeParseException("More characters encountered in input than expected.");
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
    return (String)parameters.get(paramString.trim().toLowerCase(Locale.ENGLISH));
  }
  
  public void set(String paramString1, String paramString2)
  {
    parameters.put(paramString1.trim().toLowerCase(Locale.ENGLISH), paramString2);
  }
  
  public void remove(String paramString)
  {
    parameters.remove(paramString.trim().toLowerCase(Locale.ENGLISH));
  }
  
  public Enumeration getNames()
  {
    return parameters.keys();
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.ensureCapacity(parameters.size() * 16);
    Enumeration localEnumeration = parameters.keys();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      localStringBuffer.append("; ");
      localStringBuffer.append(str);
      localStringBuffer.append('=');
      localStringBuffer.append(quote((String)parameters.get(str)));
    }
    return localStringBuffer.toString();
  }
  
  private static boolean isTokenChar(char paramChar)
  {
    return (paramChar > ' ') && (paramChar < '') && ("()<>@,;:/[]?=\\\"".indexOf(paramChar) < 0);
  }
  
  private static int skipWhiteSpace(String paramString, int paramInt)
  {
    int i = paramString.length();
    while ((paramInt < i) && (Character.isWhitespace(paramString.charAt(paramInt)))) {
      paramInt++;
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
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.ensureCapacity((int)(j * 1.5D));
      localStringBuffer.append('"');
      for (int m = 0; m < j; m++)
      {
        char c = paramString.charAt(m);
        if ((c == '\\') || (c == '"')) {
          localStringBuffer.append('\\');
        }
        localStringBuffer.append(c);
      }
      localStringBuffer.append('"');
      return localStringBuffer.toString();
    }
    return paramString;
  }
  
  private static String unquote(String paramString)
  {
    int i = paramString.length();
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.ensureCapacity(i);
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      char c = paramString.charAt(k);
      if ((j == 0) && (c != '\\'))
      {
        localStringBuffer.append(c);
      }
      else if (j != 0)
      {
        localStringBuffer.append(c);
        j = 0;
      }
      else
      {
        j = 1;
      }
    }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\activation\MimeTypeParameterList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */