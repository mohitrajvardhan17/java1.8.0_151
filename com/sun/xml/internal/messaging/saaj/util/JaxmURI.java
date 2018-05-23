package com.sun.xml.internal.messaging.saaj.util;

import java.io.IOException;
import java.io.Serializable;

public class JaxmURI
  implements Serializable
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
  
  public JaxmURI() {}
  
  public JaxmURI(JaxmURI paramJaxmURI)
  {
    initialize(paramJaxmURI);
  }
  
  public JaxmURI(String paramString)
    throws JaxmURI.MalformedURIException
  {
    this((JaxmURI)null, paramString);
  }
  
  public JaxmURI(JaxmURI paramJaxmURI, String paramString)
    throws JaxmURI.MalformedURIException
  {
    initialize(paramJaxmURI, paramString);
  }
  
  public JaxmURI(String paramString1, String paramString2)
    throws JaxmURI.MalformedURIException
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
  
  public JaxmURI(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws JaxmURI.MalformedURIException
  {
    this(paramString1, null, paramString2, -1, paramString3, paramString4, paramString5);
  }
  
  public JaxmURI(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4, String paramString5, String paramString6)
    throws JaxmURI.MalformedURIException
  {
    if ((paramString1 == null) || (paramString1.trim().length() == 0)) {
      throw new MalformedURIException("Scheme is required!");
    }
    if (paramString3 == null)
    {
      if (paramString2 != null) {
        throw new MalformedURIException("Userinfo may not be specified if host is not specified!");
      }
      if (paramInt != -1) {
        throw new MalformedURIException("Port may not be specified if host is not specified!");
      }
    }
    if (paramString4 != null)
    {
      if ((paramString4.indexOf('?') != -1) && (paramString5 != null)) {
        throw new MalformedURIException("Query string cannot be specified in path and query string!");
      }
      if ((paramString4.indexOf('#') != -1) && (paramString6 != null)) {
        throw new MalformedURIException("Fragment cannot be specified in both the path and fragment!");
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
  
  private void initialize(JaxmURI paramJaxmURI)
  {
    m_scheme = paramJaxmURI.getScheme();
    m_userinfo = paramJaxmURI.getUserinfo();
    m_host = paramJaxmURI.getHost();
    m_port = paramJaxmURI.getPort();
    m_path = paramJaxmURI.getPath();
    m_queryString = paramJaxmURI.getQueryString();
    m_fragment = paramJaxmURI.getFragment();
  }
  
  private void initialize(JaxmURI paramJaxmURI, String paramString)
    throws JaxmURI.MalformedURIException
  {
    if ((paramJaxmURI == null) && ((paramString == null) || (paramString.trim().length() == 0))) {
      throw new MalformedURIException("Cannot initialize URI with empty parameters.");
    }
    if ((paramString == null) || (paramString.trim().length() == 0))
    {
      initialize(paramJaxmURI);
      return;
    }
    String str1 = paramString.trim();
    int i = str1.length();
    int j = 0;
    int k = str1.indexOf(':');
    int m = str1.indexOf('/');
    int n;
    if ((k < 2) || ((k > m) && (m != -1)))
    {
      n = str1.indexOf('#');
      if ((paramJaxmURI == null) && (n != 0)) {
        throw new MalformedURIException("No scheme found in URI.");
      }
    }
    else
    {
      initializeScheme(str1);
      j = m_scheme.length() + 1;
    }
    if ((j + 1 < i) && (str1.substring(j).startsWith("//")))
    {
      j += 2;
      n = j;
      int i1 = 0;
      while (j < i)
      {
        i1 = str1.charAt(j);
        if ((i1 == 47) || (i1 == 63) || (i1 == 35)) {
          break;
        }
        j++;
      }
      if (j > n) {
        initializeAuthority(str1.substring(n, j));
      } else {
        m_host = "";
      }
    }
    initializePath(str1.substring(j));
    if (paramJaxmURI != null)
    {
      if ((m_path.length() == 0) && (m_scheme == null) && (m_host == null))
      {
        m_scheme = paramJaxmURI.getScheme();
        m_userinfo = paramJaxmURI.getUserinfo();
        m_host = paramJaxmURI.getHost();
        m_port = paramJaxmURI.getPort();
        m_path = paramJaxmURI.getPath();
        if (m_queryString == null) {
          m_queryString = paramJaxmURI.getQueryString();
        }
        return;
      }
      if (m_scheme == null) {
        m_scheme = paramJaxmURI.getScheme();
      } else {
        return;
      }
      if (m_host == null)
      {
        m_userinfo = paramJaxmURI.getUserinfo();
        m_host = paramJaxmURI.getHost();
        m_port = paramJaxmURI.getPort();
      }
      else
      {
        return;
      }
      if ((m_path.length() > 0) && (m_path.startsWith("/"))) {
        return;
      }
      String str2 = "";
      String str3 = paramJaxmURI.getPath();
      if (str3 != null)
      {
        i2 = str3.lastIndexOf('/');
        if (i2 != -1) {
          str2 = str3.substring(0, i2 + 1);
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
      j = 1;
      int i2 = -1;
      String str4 = null;
      while ((j = str2.indexOf("/../", j)) > 0)
      {
        str4 = str2.substring(0, str2.indexOf("/../"));
        i2 = str4.lastIndexOf('/');
        if (i2 != -1)
        {
          if (!str4.substring(i2++).equals("..")) {
            str2 = str2.substring(0, i2).concat(str2.substring(j + 4));
          } else {
            j += 4;
          }
        }
        else {
          j += 4;
        }
      }
      if (str2.endsWith("/.."))
      {
        str4 = str2.substring(0, str2.length() - 3);
        i2 = str4.lastIndexOf('/');
        if (i2 != -1) {
          str2 = str2.substring(0, i2 + 1);
        }
      }
      m_path = str2;
    }
  }
  
  private void initializeScheme(String paramString)
    throws JaxmURI.MalformedURIException
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
      throw new MalformedURIException("No scheme found in URI.");
    }
    setScheme(str);
  }
  
  private void initializeAuthority(String paramString)
    throws JaxmURI.MalformedURIException
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
    throws JaxmURI.MalformedURIException
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
          throw new MalformedURIException("Path contains invalid escape sequence!");
        }
      }
      else if ((!isReservedCharacter(c)) && (!isUnreservedCharacter(c))) {
        throw new MalformedURIException("Path contains invalid character: " + c);
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
    StringBuffer localStringBuffer = new StringBuffer();
    if ((m_userinfo != null) || (m_host != null) || (m_port != -1)) {
      localStringBuffer.append("//");
    }
    if (m_userinfo != null)
    {
      localStringBuffer.append(m_userinfo);
      localStringBuffer.append('@');
    }
    if (m_host != null) {
      localStringBuffer.append(m_host);
    }
    if (m_port != -1)
    {
      localStringBuffer.append(':');
      localStringBuffer.append(m_port);
    }
    if (m_path != null) {
      localStringBuffer.append(m_path);
    }
    if (m_queryString != null)
    {
      localStringBuffer.append('?');
      localStringBuffer.append(m_queryString);
    }
    if (m_fragment != null)
    {
      localStringBuffer.append('#');
      localStringBuffer.append(m_fragment);
    }
    return localStringBuffer.toString();
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
    StringBuffer localStringBuffer = new StringBuffer(m_path);
    if ((paramBoolean1) && (m_queryString != null))
    {
      localStringBuffer.append('?');
      localStringBuffer.append(m_queryString);
    }
    if ((paramBoolean2) && (m_fragment != null))
    {
      localStringBuffer.append('#');
      localStringBuffer.append(m_fragment);
    }
    return localStringBuffer.toString();
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
    throws JaxmURI.MalformedURIException
  {
    if (paramString == null) {
      throw new MalformedURIException("Cannot set scheme from null string!");
    }
    if (!isConformantSchemeName(paramString)) {
      throw new MalformedURIException("The scheme is not conformant.");
    }
    m_scheme = paramString.toLowerCase();
  }
  
  public void setUserinfo(String paramString)
    throws JaxmURI.MalformedURIException
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
    throws JaxmURI.MalformedURIException
  {
    if ((paramString == null) || (paramString.trim().length() == 0))
    {
      m_host = paramString;
      m_userinfo = null;
      m_port = -1;
    }
    else if (!isWellFormedAddress(paramString))
    {
      throw new MalformedURIException("Host is not a well formed address!");
    }
    m_host = paramString;
  }
  
  public void setPort(int paramInt)
    throws JaxmURI.MalformedURIException
  {
    if ((paramInt >= 0) && (paramInt <= 65535))
    {
      if (m_host == null) {
        throw new MalformedURIException("Port cannot be set when host is null!");
      }
    }
    else if (paramInt != -1) {
      throw new MalformedURIException("Invalid port number!");
    }
    m_port = paramInt;
  }
  
  public void setPath(String paramString)
    throws JaxmURI.MalformedURIException
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
    throws JaxmURI.MalformedURIException
  {
    if ((paramString == null) || (paramString.trim().length() == 0)) {
      return;
    }
    if (!isURIString(paramString)) {
      throw new MalformedURIException("Path contains invalid character!");
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
    throws JaxmURI.MalformedURIException
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
    throws JaxmURI.MalformedURIException
  {
    if (paramString == null)
    {
      m_fragment = null;
    }
    else
    {
      if (!isGenericURI()) {
        throw new MalformedURIException("Fragment can only be set for a generic URI!");
      }
      if (getPath() == null) {
        throw new MalformedURIException("Fragment cannot be set when path is null!");
      }
      if (!isURIString(paramString)) {
        throw new MalformedURIException("Fragment contains invalid character!");
      }
      m_fragment = paramString;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof JaxmURI))
    {
      JaxmURI localJaxmURI = (JaxmURI)paramObject;
      if (((m_scheme == null) && (m_scheme == null)) || ((m_scheme != null) && (m_scheme != null) && (m_scheme.equals(m_scheme)) && (((m_userinfo == null) && (m_userinfo == null)) || ((m_userinfo != null) && (m_userinfo != null) && (m_userinfo.equals(m_userinfo)) && (((m_host == null) && (m_host == null)) || ((m_host != null) && (m_host != null) && (m_host.equals(m_host)) && (m_port == m_port) && (((m_path == null) && (m_path == null)) || ((m_path != null) && (m_path != null) && (m_path.equals(m_path)) && (((m_queryString == null) && (m_queryString == null)) || ((m_queryString != null) && (m_queryString != null) && (m_queryString.equals(m_queryString)) && (((m_fragment == null) && (m_fragment == null)) || ((m_fragment != null) && (m_fragment != null) && (m_fragment.equals(m_fragment)))))))))))))) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return 153214;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (m_scheme != null)
    {
      localStringBuffer.append(m_scheme);
      localStringBuffer.append(':');
    }
    localStringBuffer.append(getSchemeSpecificPart());
    return localStringBuffer.toString();
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\util\JaxmURI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */