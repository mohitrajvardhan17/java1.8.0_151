package com.sun.org.apache.xml.internal.serializer.utils;

import java.io.IOException;
import java.util.Objects;

final class URI
{
  private static final String RESERVED_CHARACTERS = ";/?:@&=+$,";
  private static final String MARK_CHARACTERS = "-_.!~*'() ";
  private static final String SCHEME_CHARACTERS = "+-.";
  private static final String USERINFO_CHARACTERS = ";:&=+$,";
  private String m_scheme = null;
  private String m_userinfo = null;
  private String m_host = null;
  private int m_port = -1;
  private String m_path = null;
  private String m_queryString = null;
  private String m_fragment = null;
  private static boolean DEBUG = false;
  
  public URI() {}
  
  public URI(URI paramURI)
  {
    initialize(paramURI);
  }
  
  public URI(String paramString)
    throws URI.MalformedURIException
  {
    this((URI)null, paramString);
  }
  
  public URI(URI paramURI, String paramString)
    throws URI.MalformedURIException
  {
    initialize(paramURI, paramString);
  }
  
  public URI(String paramString1, String paramString2)
    throws URI.MalformedURIException
  {
    if ((paramString1 == null) || (paramString1.trim().length() == 0)) {
      throw new MalformedURIException("Cannot construct URI with null/empty scheme!");
    }
    if ((paramString2 == null) || (paramString2.trim().length() == 0)) {
      throw new MalformedURIException("Cannot construct URI with null/empty scheme-specific part!");
    }
    setScheme(paramString1);
    setPath(paramString2);
  }
  
  public URI(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws URI.MalformedURIException
  {
    this(paramString1, null, paramString2, -1, paramString3, paramString4, paramString5);
  }
  
  public URI(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4, String paramString5, String paramString6)
    throws URI.MalformedURIException
  {
    if ((paramString1 == null) || (paramString1.trim().length() == 0)) {
      throw new MalformedURIException(Utils.messages.createMessage("ER_SCHEME_REQUIRED", null));
    }
    if (paramString3 == null)
    {
      if (paramString2 != null) {
        throw new MalformedURIException(Utils.messages.createMessage("ER_NO_USERINFO_IF_NO_HOST", null));
      }
      if (paramInt != -1) {
        throw new MalformedURIException(Utils.messages.createMessage("ER_NO_PORT_IF_NO_HOST", null));
      }
    }
    if (paramString4 != null)
    {
      if ((paramString4.indexOf('?') != -1) && (paramString5 != null)) {
        throw new MalformedURIException(Utils.messages.createMessage("ER_NO_QUERY_STRING_IN_PATH", null));
      }
      if ((paramString4.indexOf('#') != -1) && (paramString6 != null)) {
        throw new MalformedURIException(Utils.messages.createMessage("ER_NO_FRAGMENT_STRING_IN_PATH", null));
      }
    }
    setScheme(paramString1);
    setHost(paramString3);
    setPort(paramInt);
    setUserinfo(paramString2);
    setPath(paramString4);
    setQueryString(paramString5);
    setFragment(paramString6);
  }
  
  private void initialize(URI paramURI)
  {
    m_scheme = paramURI.getScheme();
    m_userinfo = paramURI.getUserinfo();
    m_host = paramURI.getHost();
    m_port = paramURI.getPort();
    m_path = paramURI.getPath();
    m_queryString = paramURI.getQueryString();
    m_fragment = paramURI.getFragment();
  }
  
  private void initialize(URI paramURI, String paramString)
    throws URI.MalformedURIException
  {
    if ((paramURI == null) && ((paramString == null) || (paramString.trim().length() == 0))) {
      throw new MalformedURIException(Utils.messages.createMessage("ER_CANNOT_INIT_URI_EMPTY_PARMS", null));
    }
    if ((paramString == null) || (paramString.trim().length() == 0))
    {
      initialize(paramURI);
      return;
    }
    String str1 = paramString.trim();
    int i = str1.length();
    int j = 0;
    int k = str1.indexOf(':');
    if (k < 0)
    {
      if (paramURI == null) {
        throw new MalformedURIException(Utils.messages.createMessage("ER_NO_SCHEME_IN_URI", new Object[] { str1 }));
      }
    }
    else
    {
      initializeScheme(str1);
      str1 = str1.substring(k + 1);
      i = str1.length();
    }
    if ((j + 1 < i) && (str1.substring(j).startsWith("//")))
    {
      j += 2;
      int m = j;
      int n = 0;
      while (j < i)
      {
        n = str1.charAt(j);
        if ((n == 47) || (n == 63) || (n == 35)) {
          break;
        }
        j++;
      }
      if (j > m) {
        initializeAuthority(str1.substring(m, j));
      } else {
        m_host = "";
      }
    }
    initializePath(str1.substring(j));
    if (paramURI != null)
    {
      if ((m_path.length() == 0) && (m_scheme == null) && (m_host == null))
      {
        m_scheme = paramURI.getScheme();
        m_userinfo = paramURI.getUserinfo();
        m_host = paramURI.getHost();
        m_port = paramURI.getPort();
        m_path = paramURI.getPath();
        if (m_queryString == null) {
          m_queryString = paramURI.getQueryString();
        }
        return;
      }
      if (m_scheme == null) {
        m_scheme = paramURI.getScheme();
      }
      if (m_host == null)
      {
        m_userinfo = paramURI.getUserinfo();
        m_host = paramURI.getHost();
        m_port = paramURI.getPort();
      }
      else
      {
        return;
      }
      if ((m_path.length() > 0) && (m_path.startsWith("/"))) {
        return;
      }
      String str2 = "";
      String str3 = paramURI.getPath();
      if (str3 != null)
      {
        i1 = str3.lastIndexOf('/');
        if (i1 != -1) {
          str2 = str3.substring(0, i1 + 1);
        }
      }
      str2 = str2.concat(m_path);
      j = -1;
      while ((j = str2.indexOf("/./")) != -1) {
        str2 = str2.substring(0, j + 1).concat(str2.substring(j + 3));
      }
      if (str2.endsWith("/.")) {
        str2 = str2.substring(0, str2.length() - 1);
      }
      j = -1;
      int i1 = -1;
      String str4 = null;
      while ((j = str2.indexOf("/../")) > 0)
      {
        str4 = str2.substring(0, str2.indexOf("/../"));
        i1 = str4.lastIndexOf('/');
        if ((i1 != -1) && (!str4.substring(i1++).equals(".."))) {
          str2 = str2.substring(0, i1).concat(str2.substring(j + 4));
        }
      }
      if (str2.endsWith("/.."))
      {
        str4 = str2.substring(0, str2.length() - 3);
        i1 = str4.lastIndexOf('/');
        if (i1 != -1) {
          str2 = str2.substring(0, i1 + 1);
        }
      }
      m_path = str2;
    }
  }
  
  private void initializeScheme(String paramString)
    throws URI.MalformedURIException
  {
    int i = paramString.length();
    int j = 0;
    String str = null;
    int k = 0;
    while (j < i)
    {
      k = paramString.charAt(j);
      if ((k == 58) || (k == 47) || (k == 63) || (k == 35)) {
        break;
      }
      j++;
    }
    str = paramString.substring(0, j);
    if (str.length() == 0) {
      throw new MalformedURIException(Utils.messages.createMessage("ER_NO_SCHEME_INURI", null));
    }
    setScheme(str);
  }
  
  private void initializeAuthority(String paramString)
    throws URI.MalformedURIException
  {
    int i = 0;
    int j = 0;
    int k = paramString.length();
    int m = 0;
    String str1 = null;
    if (paramString.indexOf('@', j) != -1)
    {
      while (i < k)
      {
        m = paramString.charAt(i);
        if (m == 64) {
          break;
        }
        i++;
      }
      str1 = paramString.substring(j, i);
      i++;
    }
    String str2 = null;
    j = i;
    while (i < k)
    {
      m = paramString.charAt(i);
      if (m == 58) {
        break;
      }
      i++;
    }
    str2 = paramString.substring(j, i);
    int n = -1;
    if ((str2.length() > 0) && (m == 58))
    {
      i++;
      j = i;
      while (i < k) {
        i++;
      }
      String str3 = paramString.substring(j, i);
      if (str3.length() > 0)
      {
        for (int i1 = 0; i1 < str3.length(); i1++) {
          if (!isDigit(str3.charAt(i1))) {
            throw new MalformedURIException(str3 + " is invalid. Port should only contain digits!");
          }
        }
        try
        {
          n = Integer.parseInt(str3);
        }
        catch (NumberFormatException localNumberFormatException) {}
      }
    }
    setHost(str2);
    setPort(n);
    setUserinfo(str1);
  }
  
  private void initializePath(String paramString)
    throws URI.MalformedURIException
  {
    if (paramString == null) {
      throw new MalformedURIException("Cannot initialize path from null string!");
    }
    int i = 0;
    int j = 0;
    int k = paramString.length();
    char c = '\000';
    while (i < k)
    {
      c = paramString.charAt(i);
      if ((c == '?') || (c == '#')) {
        break;
      }
      if (c == '%')
      {
        if ((i + 2 >= k) || (!isHex(paramString.charAt(i + 1))) || (!isHex(paramString.charAt(i + 2)))) {
          throw new MalformedURIException(Utils.messages.createMessage("ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE", null));
        }
      }
      else if ((!isReservedCharacter(c)) && (!isUnreservedCharacter(c)) && ('\\' != c)) {
        throw new MalformedURIException(Utils.messages.createMessage("ER_PATH_INVALID_CHAR", new Object[] { String.valueOf(c) }));
      }
      i++;
    }
    m_path = paramString.substring(j, i);
    if (c == '?')
    {
      i++;
      j = i;
      while (i < k)
      {
        c = paramString.charAt(i);
        if (c == '#') {
          break;
        }
        if (c == '%')
        {
          if ((i + 2 >= k) || (!isHex(paramString.charAt(i + 1))) || (!isHex(paramString.charAt(i + 2)))) {
            throw new MalformedURIException("Query string contains invalid escape sequence!");
          }
        }
        else if ((!isReservedCharacter(c)) && (!isUnreservedCharacter(c))) {
          throw new MalformedURIException("Query string contains invalid character:" + c);
        }
        i++;
      }
      m_queryString = paramString.substring(j, i);
    }
    if (c == '#')
    {
      i++;
      j = i;
      while (i < k)
      {
        c = paramString.charAt(i);
        if (c == '%')
        {
          if ((i + 2 >= k) || (!isHex(paramString.charAt(i + 1))) || (!isHex(paramString.charAt(i + 2)))) {
            throw new MalformedURIException("Fragment contains invalid escape sequence!");
          }
        }
        else if ((!isReservedCharacter(c)) && (!isUnreservedCharacter(c))) {
          throw new MalformedURIException("Fragment contains invalid character:" + c);
        }
        i++;
      }
      m_fragment = paramString.substring(j, i);
    }
  }
  
  public String getScheme()
  {
    return m_scheme;
  }
  
  public String getSchemeSpecificPart()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((m_userinfo != null) || (m_host != null) || (m_port != -1)) {
      localStringBuilder.append("//");
    }
    if (m_userinfo != null)
    {
      localStringBuilder.append(m_userinfo);
      localStringBuilder.append('@');
    }
    if (m_host != null) {
      localStringBuilder.append(m_host);
    }
    if (m_port != -1)
    {
      localStringBuilder.append(':');
      localStringBuilder.append(m_port);
    }
    if (m_path != null) {
      localStringBuilder.append(m_path);
    }
    if (m_queryString != null)
    {
      localStringBuilder.append('?');
      localStringBuilder.append(m_queryString);
    }
    if (m_fragment != null)
    {
      localStringBuilder.append('#');
      localStringBuilder.append(m_fragment);
    }
    return localStringBuilder.toString();
  }
  
  public String getUserinfo()
  {
    return m_userinfo;
  }
  
  public String getHost()
  {
    return m_host;
  }
  
  public int getPort()
  {
    return m_port;
  }
  
  public String getPath(boolean paramBoolean1, boolean paramBoolean2)
  {
    StringBuilder localStringBuilder = new StringBuilder(m_path);
    if ((paramBoolean1) && (m_queryString != null))
    {
      localStringBuilder.append('?');
      localStringBuilder.append(m_queryString);
    }
    if ((paramBoolean2) && (m_fragment != null))
    {
      localStringBuilder.append('#');
      localStringBuilder.append(m_fragment);
    }
    return localStringBuilder.toString();
  }
  
  public String getPath()
  {
    return m_path;
  }
  
  public String getQueryString()
  {
    return m_queryString;
  }
  
  public String getFragment()
  {
    return m_fragment;
  }
  
  public void setScheme(String paramString)
    throws URI.MalformedURIException
  {
    if (paramString == null) {
      throw new MalformedURIException(Utils.messages.createMessage("ER_SCHEME_FROM_NULL_STRING", null));
    }
    if (!isConformantSchemeName(paramString)) {
      throw new MalformedURIException(Utils.messages.createMessage("ER_SCHEME_NOT_CONFORMANT", null));
    }
    m_scheme = paramString.toLowerCase();
  }
  
  public void setUserinfo(String paramString)
    throws URI.MalformedURIException
  {
    if (paramString == null)
    {
      m_userinfo = null;
    }
    else
    {
      if (m_host == null) {
        throw new MalformedURIException("Userinfo cannot be set when host is null!");
      }
      int i = 0;
      int j = paramString.length();
      char c = '\000';
      while (i < j)
      {
        c = paramString.charAt(i);
        if (c == '%')
        {
          if ((i + 2 >= j) || (!isHex(paramString.charAt(i + 1))) || (!isHex(paramString.charAt(i + 2)))) {
            throw new MalformedURIException("Userinfo contains invalid escape sequence!");
          }
        }
        else if ((!isUnreservedCharacter(c)) && (";:&=+$,".indexOf(c) == -1)) {
          throw new MalformedURIException("Userinfo contains invalid character:" + c);
        }
        i++;
      }
    }
    m_userinfo = paramString;
  }
  
  public void setHost(String paramString)
    throws URI.MalformedURIException
  {
    if ((paramString == null) || (paramString.trim().length() == 0))
    {
      m_host = paramString;
      m_userinfo = null;
      m_port = -1;
    }
    else if (!isWellFormedAddress(paramString))
    {
      throw new MalformedURIException(Utils.messages.createMessage("ER_HOST_ADDRESS_NOT_WELLFORMED", null));
    }
    m_host = paramString;
  }
  
  public void setPort(int paramInt)
    throws URI.MalformedURIException
  {
    if ((paramInt >= 0) && (paramInt <= 65535))
    {
      if (m_host == null) {
        throw new MalformedURIException(Utils.messages.createMessage("ER_PORT_WHEN_HOST_NULL", null));
      }
    }
    else if (paramInt != -1) {
      throw new MalformedURIException(Utils.messages.createMessage("ER_INVALID_PORT", null));
    }
    m_port = paramInt;
  }
  
  public void setPath(String paramString)
    throws URI.MalformedURIException
  {
    if (paramString == null)
    {
      m_path = null;
      m_queryString = null;
      m_fragment = null;
    }
    else
    {
      initializePath(paramString);
    }
  }
  
  public void appendPath(String paramString)
    throws URI.MalformedURIException
  {
    if ((paramString == null) || (paramString.trim().length() == 0)) {
      return;
    }
    if (!isURIString(paramString)) {
      throw new MalformedURIException(Utils.messages.createMessage("ER_PATH_INVALID_CHAR", new Object[] { paramString }));
    }
    if ((m_path == null) || (m_path.trim().length() == 0))
    {
      if (paramString.startsWith("/")) {
        m_path = paramString;
      } else {
        m_path = ("/" + paramString);
      }
    }
    else if (m_path.endsWith("/"))
    {
      if (paramString.startsWith("/")) {
        m_path = m_path.concat(paramString.substring(1));
      } else {
        m_path = m_path.concat(paramString);
      }
    }
    else if (paramString.startsWith("/")) {
      m_path = m_path.concat(paramString);
    } else {
      m_path = m_path.concat("/" + paramString);
    }
  }
  
  public void setQueryString(String paramString)
    throws URI.MalformedURIException
  {
    if (paramString == null)
    {
      m_queryString = null;
    }
    else
    {
      if (!isGenericURI()) {
        throw new MalformedURIException("Query string can only be set for a generic URI!");
      }
      if (getPath() == null) {
        throw new MalformedURIException("Query string cannot be set when path is null!");
      }
      if (!isURIString(paramString)) {
        throw new MalformedURIException("Query string contains invalid character!");
      }
      m_queryString = paramString;
    }
  }
  
  public void setFragment(String paramString)
    throws URI.MalformedURIException
  {
    if (paramString == null)
    {
      m_fragment = null;
    }
    else
    {
      if (!isGenericURI()) {
        throw new MalformedURIException(Utils.messages.createMessage("ER_FRAG_FOR_GENERIC_URI", null));
      }
      if (getPath() == null) {
        throw new MalformedURIException(Utils.messages.createMessage("ER_FRAG_WHEN_PATH_NULL", null));
      }
      if (!isURIString(paramString)) {
        throw new MalformedURIException(Utils.messages.createMessage("ER_FRAG_INVALID_CHAR", null));
      }
      m_fragment = paramString;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof URI))
    {
      URI localURI = (URI)paramObject;
      if (((m_scheme == null) && (m_scheme == null)) || ((m_scheme != null) && (m_scheme != null) && (m_scheme.equals(m_scheme)) && (((m_userinfo == null) && (m_userinfo == null)) || ((m_userinfo != null) && (m_userinfo != null) && (m_userinfo.equals(m_userinfo)) && (((m_host == null) && (m_host == null)) || ((m_host != null) && (m_host != null) && (m_host.equals(m_host)) && (m_port == m_port) && (((m_path == null) && (m_path == null)) || ((m_path != null) && (m_path != null) && (m_path.equals(m_path)) && (((m_queryString == null) && (m_queryString == null)) || ((m_queryString != null) && (m_queryString != null) && (m_queryString.equals(m_queryString)) && (((m_fragment == null) && (m_fragment == null)) || ((m_fragment != null) && (m_fragment != null) && (m_fragment.equals(m_fragment)))))))))))))) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = 5;
    i = 41 * i + Objects.hashCode(m_scheme);
    i = 41 * i + Objects.hashCode(m_userinfo);
    i = 41 * i + Objects.hashCode(m_host);
    i = 41 * i + m_port;
    i = 41 * i + Objects.hashCode(m_path);
    i = 41 * i + Objects.hashCode(m_queryString);
    i = 41 * i + Objects.hashCode(m_fragment);
    return i;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (m_scheme != null)
    {
      localStringBuilder.append(m_scheme);
      localStringBuilder.append(':');
    }
    localStringBuilder.append(getSchemeSpecificPart());
    return localStringBuilder.toString();
  }
  
  public boolean isGenericURI()
  {
    return m_host != null;
  }
  
  public static boolean isConformantSchemeName(String paramString)
  {
    if ((paramString == null) || (paramString.trim().length() == 0)) {
      return false;
    }
    if (!isAlpha(paramString.charAt(0))) {
      return false;
    }
    for (int i = 1; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if ((!isAlphanum(c)) && ("+-.".indexOf(c) == -1)) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isWellFormedAddress(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    String str = paramString.trim();
    int i = str.length();
    if ((i == 0) || (i > 255)) {
      return false;
    }
    if ((str.startsWith(".")) || (str.startsWith("-"))) {
      return false;
    }
    int j = str.lastIndexOf('.');
    if (str.endsWith(".")) {
      j = str.substring(0, j).lastIndexOf('.');
    }
    int k;
    char c;
    if ((j + 1 < i) && (isDigit(paramString.charAt(j + 1))))
    {
      k = 0;
      for (int m = 0; m < i; m++)
      {
        c = str.charAt(m);
        if (c == '.')
        {
          if ((!isDigit(str.charAt(m - 1))) || ((m + 1 < i) && (!isDigit(str.charAt(m + 1))))) {
            return false;
          }
          k++;
        }
        else if (!isDigit(c))
        {
          return false;
        }
      }
      if (k != 3) {
        return false;
      }
    }
    else
    {
      for (k = 0; k < i; k++)
      {
        c = str.charAt(k);
        if (c == '.')
        {
          if (!isAlphanum(str.charAt(k - 1))) {
            return false;
          }
          if ((k + 1 < i) && (!isAlphanum(str.charAt(k + 1)))) {
            return false;
          }
        }
        else if ((!isAlphanum(c)) && (c != '-'))
        {
          return false;
        }
      }
    }
    return true;
  }
  
  private static boolean isDigit(char paramChar)
  {
    return (paramChar >= '0') && (paramChar <= '9');
  }
  
  private static boolean isHex(char paramChar)
  {
    return (isDigit(paramChar)) || ((paramChar >= 'a') && (paramChar <= 'f')) || ((paramChar >= 'A') && (paramChar <= 'F'));
  }
  
  private static boolean isAlpha(char paramChar)
  {
    return ((paramChar >= 'a') && (paramChar <= 'z')) || ((paramChar >= 'A') && (paramChar <= 'Z'));
  }
  
  private static boolean isAlphanum(char paramChar)
  {
    return (isAlpha(paramChar)) || (isDigit(paramChar));
  }
  
  private static boolean isReservedCharacter(char paramChar)
  {
    return ";/?:@&=+$,".indexOf(paramChar) != -1;
  }
  
  private static boolean isUnreservedCharacter(char paramChar)
  {
    return (isAlphanum(paramChar)) || ("-_.!~*'() ".indexOf(paramChar) != -1);
  }
  
  private static boolean isURIString(String paramString)
  {
    if (paramString == null) {
      return false;
    }
    int i = paramString.length();
    char c = '\000';
    for (int j = 0; j < i; j++)
    {
      c = paramString.charAt(j);
      if (c == '%')
      {
        if ((j + 2 >= i) || (!isHex(paramString.charAt(j + 1))) || (!isHex(paramString.charAt(j + 2)))) {
          return false;
        }
        j += 2;
      }
      else if ((!isReservedCharacter(c)) && (!isUnreservedCharacter(c)))
      {
        return false;
      }
    }
    return true;
  }
  
  public static class MalformedURIException
    extends IOException
  {
    public MalformedURIException() {}
    
    public MalformedURIException(String paramString)
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\utils\URI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */