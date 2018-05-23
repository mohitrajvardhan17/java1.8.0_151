package com.sun.xml.internal.bind.v2.schemagen;

public final class Util
{
  private Util() {}
  
  public static String escapeURI(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if (Character.isSpaceChar(c)) {
        localStringBuilder.append("%20");
      } else {
        localStringBuilder.append(c);
      }
    }
    return localStringBuilder.toString();
  }
  
  public static String getParentUriPath(String paramString)
  {
    int i = paramString.lastIndexOf('/');
    if (paramString.endsWith("/"))
    {
      paramString = paramString.substring(0, i);
      i = paramString.lastIndexOf('/');
    }
    return paramString.substring(0, i) + "/";
  }
  
  public static String normalizeUriPath(String paramString)
  {
    if (paramString.endsWith("/")) {
      return paramString;
    }
    int i = paramString.lastIndexOf('/');
    return paramString.substring(0, i + 1);
  }
  
  public static boolean equalsIgnoreCase(String paramString1, String paramString2)
  {
    if (paramString1 == paramString2) {
      return true;
    }
    if ((paramString1 != null) && (paramString2 != null)) {
      return paramString1.equalsIgnoreCase(paramString2);
    }
    return false;
  }
  
  public static boolean equal(String paramString1, String paramString2)
  {
    if (paramString1 == paramString2) {
      return true;
    }
    if ((paramString1 != null) && (paramString2 != null)) {
      return paramString1.equals(paramString2);
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */