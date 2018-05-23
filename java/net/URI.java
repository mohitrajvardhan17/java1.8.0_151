package java.net;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import sun.nio.cs.ThreadLocalCoders;

public final class URI
  implements Comparable<URI>, Serializable
{
  static final long serialVersionUID = -6052424284110960213L;
  private transient String scheme;
  private transient String fragment;
  private transient String authority;
  private transient String userInfo;
  private transient String host;
  private transient int port = -1;
  private transient String path;
  private transient String query;
  private volatile transient String schemeSpecificPart;
  private volatile transient int hash;
  private volatile transient String decodedUserInfo = null;
  private volatile transient String decodedAuthority = null;
  private volatile transient String decodedPath = null;
  private volatile transient String decodedQuery = null;
  private volatile transient String decodedFragment = null;
  private volatile transient String decodedSchemeSpecificPart = null;
  private volatile String string;
  private static final long L_DIGIT = lowMask('0', '9');
  private static final long H_DIGIT = 0L;
  private static final long L_UPALPHA = 0L;
  private static final long H_UPALPHA = highMask('A', 'Z');
  private static final long L_LOWALPHA = 0L;
  private static final long H_LOWALPHA = highMask('a', 'z');
  private static final long L_ALPHA = 0L;
  private static final long H_ALPHA = H_LOWALPHA | H_UPALPHA;
  private static final long L_ALPHANUM = L_DIGIT | 0L;
  private static final long H_ALPHANUM = 0L | H_ALPHA;
  private static final long L_HEX = L_DIGIT;
  private static final long H_HEX = highMask('A', 'F') | highMask('a', 'f');
  private static final long L_MARK = lowMask("-_.!~*'()");
  private static final long H_MARK = highMask("-_.!~*'()");
  private static final long L_UNRESERVED = L_ALPHANUM | L_MARK;
  private static final long H_UNRESERVED = H_ALPHANUM | H_MARK;
  private static final long L_RESERVED = lowMask(";/?:@&=+$,[]");
  private static final long H_RESERVED = highMask(";/?:@&=+$,[]");
  private static final long L_ESCAPED = 1L;
  private static final long H_ESCAPED = 0L;
  private static final long L_URIC = L_RESERVED | L_UNRESERVED | 1L;
  private static final long H_URIC = H_RESERVED | H_UNRESERVED | 0L;
  private static final long L_PCHAR = L_UNRESERVED | 1L | lowMask(":@&=+$,");
  private static final long H_PCHAR = H_UNRESERVED | 0L | highMask(":@&=+$,");
  private static final long L_PATH = L_PCHAR | lowMask(";/");
  private static final long H_PATH = H_PCHAR | highMask(";/");
  private static final long L_DASH = lowMask("-");
  private static final long H_DASH = highMask("-");
  private static final long L_DOT = lowMask(".");
  private static final long H_DOT = highMask(".");
  private static final long L_USERINFO = L_UNRESERVED | 1L | lowMask(";:&=+$,");
  private static final long H_USERINFO = H_UNRESERVED | 0L | highMask(";:&=+$,");
  private static final long L_REG_NAME = L_UNRESERVED | 1L | lowMask("$,;:@&=+");
  private static final long H_REG_NAME = H_UNRESERVED | 0L | highMask("$,;:@&=+");
  private static final long L_SERVER = L_USERINFO | L_ALPHANUM | L_DASH | lowMask(".:@[]");
  private static final long H_SERVER = H_USERINFO | H_ALPHANUM | H_DASH | highMask(".:@[]");
  private static final long L_SERVER_PERCENT = L_SERVER | lowMask("%");
  private static final long H_SERVER_PERCENT = H_SERVER | highMask("%");
  private static final long L_LEFT_BRACKET = lowMask("[");
  private static final long H_LEFT_BRACKET = highMask("[");
  private static final long L_SCHEME = 0L | L_DIGIT | lowMask("+-.");
  private static final long H_SCHEME = H_ALPHA | 0L | highMask("+-.");
  private static final long L_URIC_NO_SLASH = L_UNRESERVED | 1L | lowMask(";?:@&=+$,");
  private static final long H_URIC_NO_SLASH = H_UNRESERVED | 0L | highMask(";?:@&=+$,");
  private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  
  private URI() {}
  
  public URI(String paramString)
    throws URISyntaxException
  {
    new Parser(paramString).parse(false);
  }
  
  public URI(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4, String paramString5, String paramString6)
    throws URISyntaxException
  {
    String str = toString(paramString1, null, null, paramString2, paramString3, paramInt, paramString4, paramString5, paramString6);
    checkPath(str, paramString1, paramString4);
    new Parser(str).parse(true);
  }
  
  public URI(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws URISyntaxException
  {
    String str = toString(paramString1, null, paramString2, null, null, -1, paramString3, paramString4, paramString5);
    checkPath(str, paramString1, paramString3);
    new Parser(str).parse(false);
  }
  
  public URI(String paramString1, String paramString2, String paramString3, String paramString4)
    throws URISyntaxException
  {
    this(paramString1, null, paramString2, -1, paramString3, null, paramString4);
  }
  
  public URI(String paramString1, String paramString2, String paramString3)
    throws URISyntaxException
  {
    new Parser(toString(paramString1, paramString2, null, null, null, -1, null, null, paramString3)).parse(false);
  }
  
  public static URI create(String paramString)
  {
    try
    {
      return new URI(paramString);
    }
    catch (URISyntaxException localURISyntaxException)
    {
      throw new IllegalArgumentException(localURISyntaxException.getMessage(), localURISyntaxException);
    }
  }
  
  public URI parseServerAuthority()
    throws URISyntaxException
  {
    if ((host != null) || (authority == null)) {
      return this;
    }
    defineString();
    new Parser(string).parse(true);
    return this;
  }
  
  public URI normalize()
  {
    return normalize(this);
  }
  
  public URI resolve(URI paramURI)
  {
    return resolve(this, paramURI);
  }
  
  public URI resolve(String paramString)
  {
    return resolve(create(paramString));
  }
  
  public URI relativize(URI paramURI)
  {
    return relativize(this, paramURI);
  }
  
  public URL toURL()
    throws MalformedURLException
  {
    if (!isAbsolute()) {
      throw new IllegalArgumentException("URI is not absolute");
    }
    return new URL(toString());
  }
  
  public String getScheme()
  {
    return scheme;
  }
  
  public boolean isAbsolute()
  {
    return scheme != null;
  }
  
  public boolean isOpaque()
  {
    return path == null;
  }
  
  public String getRawSchemeSpecificPart()
  {
    defineSchemeSpecificPart();
    return schemeSpecificPart;
  }
  
  public String getSchemeSpecificPart()
  {
    if (decodedSchemeSpecificPart == null) {
      decodedSchemeSpecificPart = decode(getRawSchemeSpecificPart());
    }
    return decodedSchemeSpecificPart;
  }
  
  public String getRawAuthority()
  {
    return authority;
  }
  
  public String getAuthority()
  {
    if (decodedAuthority == null) {
      decodedAuthority = decode(authority);
    }
    return decodedAuthority;
  }
  
  public String getRawUserInfo()
  {
    return userInfo;
  }
  
  public String getUserInfo()
  {
    if ((decodedUserInfo == null) && (userInfo != null)) {
      decodedUserInfo = decode(userInfo);
    }
    return decodedUserInfo;
  }
  
  public String getHost()
  {
    return host;
  }
  
  public int getPort()
  {
    return port;
  }
  
  public String getRawPath()
  {
    return path;
  }
  
  public String getPath()
  {
    if ((decodedPath == null) && (path != null)) {
      decodedPath = decode(path);
    }
    return decodedPath;
  }
  
  public String getRawQuery()
  {
    return query;
  }
  
  public String getQuery()
  {
    if ((decodedQuery == null) && (query != null)) {
      decodedQuery = decode(query);
    }
    return decodedQuery;
  }
  
  public String getRawFragment()
  {
    return fragment;
  }
  
  public String getFragment()
  {
    if ((decodedFragment == null) && (fragment != null)) {
      decodedFragment = decode(fragment);
    }
    return decodedFragment;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof URI)) {
      return false;
    }
    URI localURI = (URI)paramObject;
    if (isOpaque() != localURI.isOpaque()) {
      return false;
    }
    if (!equalIgnoringCase(scheme, scheme)) {
      return false;
    }
    if (!equal(fragment, fragment)) {
      return false;
    }
    if (isOpaque()) {
      return equal(schemeSpecificPart, schemeSpecificPart);
    }
    if (!equal(path, path)) {
      return false;
    }
    if (!equal(query, query)) {
      return false;
    }
    if (authority == authority) {
      return true;
    }
    if (host != null)
    {
      if (!equal(userInfo, userInfo)) {
        return false;
      }
      if (!equalIgnoringCase(host, host)) {
        return false;
      }
      if (port != port) {
        return false;
      }
    }
    else if (authority != null)
    {
      if (!equal(authority, authority)) {
        return false;
      }
    }
    else if (authority != authority)
    {
      return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    if (hash != 0) {
      return hash;
    }
    int i = hashIgnoringCase(0, scheme);
    i = hash(i, fragment);
    if (isOpaque())
    {
      i = hash(i, schemeSpecificPart);
    }
    else
    {
      i = hash(i, path);
      i = hash(i, query);
      if (host != null)
      {
        i = hash(i, userInfo);
        i = hashIgnoringCase(i, host);
        i += 1949 * port;
      }
      else
      {
        i = hash(i, authority);
      }
    }
    hash = i;
    return i;
  }
  
  public int compareTo(URI paramURI)
  {
    int i;
    if ((i = compareIgnoringCase(scheme, scheme)) != 0) {
      return i;
    }
    if (isOpaque())
    {
      if (paramURI.isOpaque())
      {
        if ((i = compare(schemeSpecificPart, schemeSpecificPart)) != 0) {
          return i;
        }
        return compare(fragment, fragment);
      }
      return 1;
    }
    if (paramURI.isOpaque()) {
      return -1;
    }
    if ((host != null) && (host != null))
    {
      if ((i = compare(userInfo, userInfo)) != 0) {
        return i;
      }
      if ((i = compareIgnoringCase(host, host)) != 0) {
        return i;
      }
      if ((i = port - port) != 0) {
        return i;
      }
    }
    else if ((i = compare(authority, authority)) != 0)
    {
      return i;
    }
    if ((i = compare(path, path)) != 0) {
      return i;
    }
    if ((i = compare(query, query)) != 0) {
      return i;
    }
    return compare(fragment, fragment);
  }
  
  public String toString()
  {
    defineString();
    return string;
  }
  
  public String toASCIIString()
  {
    defineString();
    return encode(string);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    defineString();
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    port = -1;
    paramObjectInputStream.defaultReadObject();
    try
    {
      new Parser(string).parse(false);
    }
    catch (URISyntaxException localURISyntaxException)
    {
      InvalidObjectException localInvalidObjectException = new InvalidObjectException("Invalid URI");
      localInvalidObjectException.initCause(localURISyntaxException);
      throw localInvalidObjectException;
    }
  }
  
  private static int toLower(char paramChar)
  {
    if ((paramChar >= 'A') && (paramChar <= 'Z')) {
      return paramChar + ' ';
    }
    return paramChar;
  }
  
  private static int toUpper(char paramChar)
  {
    if ((paramChar >= 'a') && (paramChar <= 'z')) {
      return paramChar - ' ';
    }
    return paramChar;
  }
  
  private static boolean equal(String paramString1, String paramString2)
  {
    if (paramString1 == paramString2) {
      return true;
    }
    if ((paramString1 != null) && (paramString2 != null))
    {
      if (paramString1.length() != paramString2.length()) {
        return false;
      }
      if (paramString1.indexOf('%') < 0) {
        return paramString1.equals(paramString2);
      }
      int i = paramString1.length();
      int j = 0;
      while (j < i)
      {
        int k = paramString1.charAt(j);
        int m = paramString2.charAt(j);
        if (k != 37)
        {
          if (k != m) {
            return false;
          }
          j++;
        }
        else
        {
          if (m != 37) {
            return false;
          }
          j++;
          if (toLower(paramString1.charAt(j)) != toLower(paramString2.charAt(j))) {
            return false;
          }
          j++;
          if (toLower(paramString1.charAt(j)) != toLower(paramString2.charAt(j))) {
            return false;
          }
          j++;
        }
      }
      return true;
    }
    return false;
  }
  
  private static boolean equalIgnoringCase(String paramString1, String paramString2)
  {
    if (paramString1 == paramString2) {
      return true;
    }
    if ((paramString1 != null) && (paramString2 != null))
    {
      int i = paramString1.length();
      if (paramString2.length() != i) {
        return false;
      }
      for (int j = 0; j < i; j++) {
        if (toLower(paramString1.charAt(j)) != toLower(paramString2.charAt(j))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  private static int hash(int paramInt, String paramString)
  {
    if (paramString == null) {
      return paramInt;
    }
    return paramString.indexOf('%') < 0 ? paramInt * 127 + paramString.hashCode() : normalizedHash(paramInt, paramString);
  }
  
  private static int normalizedHash(int paramInt, String paramString)
  {
    int i = 0;
    for (int j = 0; j < paramString.length(); j++)
    {
      int k = paramString.charAt(j);
      i = 31 * i + k;
      if (k == 37)
      {
        for (int m = j + 1; m < j + 3; m++) {
          i = 31 * i + toUpper(paramString.charAt(m));
        }
        j += 2;
      }
    }
    return paramInt * 127 + i;
  }
  
  private static int hashIgnoringCase(int paramInt, String paramString)
  {
    if (paramString == null) {
      return paramInt;
    }
    int i = paramInt;
    int j = paramString.length();
    for (int k = 0; k < j; k++) {
      i = 31 * i + toLower(paramString.charAt(k));
    }
    return i;
  }
  
  private static int compare(String paramString1, String paramString2)
  {
    if (paramString1 == paramString2) {
      return 0;
    }
    if (paramString1 != null)
    {
      if (paramString2 != null) {
        return paramString1.compareTo(paramString2);
      }
      return 1;
    }
    return -1;
  }
  
  private static int compareIgnoringCase(String paramString1, String paramString2)
  {
    if (paramString1 == paramString2) {
      return 0;
    }
    if (paramString1 != null)
    {
      if (paramString2 != null)
      {
        int i = paramString1.length();
        int j = paramString2.length();
        int k = i < j ? i : j;
        for (int m = 0; m < k; m++)
        {
          int n = toLower(paramString1.charAt(m)) - toLower(paramString2.charAt(m));
          if (n != 0) {
            return n;
          }
        }
        return i - j;
      }
      return 1;
    }
    return -1;
  }
  
  private static void checkPath(String paramString1, String paramString2, String paramString3)
    throws URISyntaxException
  {
    if ((paramString2 != null) && (paramString3 != null) && (paramString3.length() > 0) && (paramString3.charAt(0) != '/')) {
      throw new URISyntaxException(paramString1, "Relative path in absolute URI");
    }
  }
  
  private void appendAuthority(StringBuffer paramStringBuffer, String paramString1, String paramString2, String paramString3, int paramInt)
  {
    int i;
    if (paramString3 != null)
    {
      paramStringBuffer.append("//");
      if (paramString2 != null)
      {
        paramStringBuffer.append(quote(paramString2, L_USERINFO, H_USERINFO));
        paramStringBuffer.append('@');
      }
      i = (paramString3.indexOf(':') >= 0) && (!paramString3.startsWith("[")) && (!paramString3.endsWith("]")) ? 1 : 0;
      if (i != 0) {
        paramStringBuffer.append('[');
      }
      paramStringBuffer.append(paramString3);
      if (i != 0) {
        paramStringBuffer.append(']');
      }
      if (paramInt != -1)
      {
        paramStringBuffer.append(':');
        paramStringBuffer.append(paramInt);
      }
    }
    else if (paramString1 != null)
    {
      paramStringBuffer.append("//");
      if (paramString1.startsWith("["))
      {
        i = paramString1.indexOf("]");
        String str1 = paramString1;
        String str2 = "";
        if ((i != -1) && (paramString1.indexOf(":") != -1)) {
          if (i == paramString1.length())
          {
            str2 = paramString1;
            str1 = "";
          }
          else
          {
            str2 = paramString1.substring(0, i + 1);
            str1 = paramString1.substring(i + 1);
          }
        }
        paramStringBuffer.append(str2);
        paramStringBuffer.append(quote(str1, L_REG_NAME | L_SERVER, H_REG_NAME | H_SERVER));
      }
      else
      {
        paramStringBuffer.append(quote(paramString1, L_REG_NAME | L_SERVER, H_REG_NAME | H_SERVER));
      }
    }
  }
  
  private void appendSchemeSpecificPart(StringBuffer paramStringBuffer, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt, String paramString5, String paramString6)
  {
    if (paramString1 != null)
    {
      if (paramString1.startsWith("//["))
      {
        int i = paramString1.indexOf("]");
        if ((i != -1) && (paramString1.indexOf(":") != -1))
        {
          String str2;
          String str1;
          if (i == paramString1.length())
          {
            str2 = paramString1;
            str1 = "";
          }
          else
          {
            str2 = paramString1.substring(0, i + 1);
            str1 = paramString1.substring(i + 1);
          }
          paramStringBuffer.append(str2);
          paramStringBuffer.append(quote(str1, L_URIC, H_URIC));
        }
      }
      else
      {
        paramStringBuffer.append(quote(paramString1, L_URIC, H_URIC));
      }
    }
    else
    {
      appendAuthority(paramStringBuffer, paramString2, paramString3, paramString4, paramInt);
      if (paramString5 != null) {
        paramStringBuffer.append(quote(paramString5, L_PATH, H_PATH));
      }
      if (paramString6 != null)
      {
        paramStringBuffer.append('?');
        paramStringBuffer.append(quote(paramString6, L_URIC, H_URIC));
      }
    }
  }
  
  private void appendFragment(StringBuffer paramStringBuffer, String paramString)
  {
    if (paramString != null)
    {
      paramStringBuffer.append('#');
      paramStringBuffer.append(quote(paramString, L_URIC, H_URIC));
    }
  }
  
  private String toString(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt, String paramString6, String paramString7, String paramString8)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (paramString1 != null)
    {
      localStringBuffer.append(paramString1);
      localStringBuffer.append(':');
    }
    appendSchemeSpecificPart(localStringBuffer, paramString2, paramString3, paramString4, paramString5, paramInt, paramString6, paramString7);
    appendFragment(localStringBuffer, paramString8);
    return localStringBuffer.toString();
  }
  
  private void defineSchemeSpecificPart()
  {
    if (schemeSpecificPart != null) {
      return;
    }
    StringBuffer localStringBuffer = new StringBuffer();
    appendSchemeSpecificPart(localStringBuffer, null, getAuthority(), getUserInfo(), host, port, getPath(), getQuery());
    if (localStringBuffer.length() == 0) {
      return;
    }
    schemeSpecificPart = localStringBuffer.toString();
  }
  
  private void defineString()
  {
    if (string != null) {
      return;
    }
    StringBuffer localStringBuffer = new StringBuffer();
    if (scheme != null)
    {
      localStringBuffer.append(scheme);
      localStringBuffer.append(':');
    }
    if (isOpaque())
    {
      localStringBuffer.append(schemeSpecificPart);
    }
    else
    {
      if (host != null)
      {
        localStringBuffer.append("//");
        if (userInfo != null)
        {
          localStringBuffer.append(userInfo);
          localStringBuffer.append('@');
        }
        int i = (host.indexOf(':') >= 0) && (!host.startsWith("[")) && (!host.endsWith("]")) ? 1 : 0;
        if (i != 0) {
          localStringBuffer.append('[');
        }
        localStringBuffer.append(host);
        if (i != 0) {
          localStringBuffer.append(']');
        }
        if (port != -1)
        {
          localStringBuffer.append(':');
          localStringBuffer.append(port);
        }
      }
      else if (authority != null)
      {
        localStringBuffer.append("//");
        localStringBuffer.append(authority);
      }
      if (path != null) {
        localStringBuffer.append(path);
      }
      if (query != null)
      {
        localStringBuffer.append('?');
        localStringBuffer.append(query);
      }
    }
    if (fragment != null)
    {
      localStringBuffer.append('#');
      localStringBuffer.append(fragment);
    }
    string = localStringBuffer.toString();
  }
  
  private static String resolvePath(String paramString1, String paramString2, boolean paramBoolean)
  {
    int i = paramString1.lastIndexOf('/');
    int j = paramString2.length();
    String str = "";
    if (j == 0)
    {
      if (i >= 0) {
        str = paramString1.substring(0, i + 1);
      }
    }
    else
    {
      localObject = new StringBuffer(paramString1.length() + j);
      if (i >= 0) {
        ((StringBuffer)localObject).append(paramString1.substring(0, i + 1));
      }
      ((StringBuffer)localObject).append(paramString2);
      str = ((StringBuffer)localObject).toString();
    }
    Object localObject = normalize(str);
    return (String)localObject;
  }
  
  private static URI resolve(URI paramURI1, URI paramURI2)
  {
    if ((paramURI2.isOpaque()) || (paramURI1.isOpaque())) {
      return paramURI2;
    }
    if ((scheme == null) && (authority == null) && (path.equals("")) && (fragment != null) && (query == null))
    {
      if ((fragment != null) && (fragment.equals(fragment))) {
        return paramURI1;
      }
      localURI = new URI();
      scheme = scheme;
      authority = authority;
      userInfo = userInfo;
      host = host;
      port = port;
      path = path;
      fragment = fragment;
      query = query;
      return localURI;
    }
    if (scheme != null) {
      return paramURI2;
    }
    URI localURI = new URI();
    scheme = scheme;
    query = query;
    fragment = fragment;
    if (authority == null)
    {
      authority = authority;
      host = host;
      userInfo = userInfo;
      port = port;
      String str = path == null ? "" : path;
      if ((str.length() > 0) && (str.charAt(0) == '/')) {
        path = path;
      } else {
        path = resolvePath(path, str, paramURI1.isAbsolute());
      }
    }
    else
    {
      authority = authority;
      host = host;
      userInfo = userInfo;
      host = host;
      port = port;
      path = path;
    }
    return localURI;
  }
  
  private static URI normalize(URI paramURI)
  {
    if ((paramURI.isOpaque()) || (path == null) || (path.length() == 0)) {
      return paramURI;
    }
    String str = normalize(path);
    if (str == path) {
      return paramURI;
    }
    URI localURI = new URI();
    scheme = scheme;
    fragment = fragment;
    authority = authority;
    userInfo = userInfo;
    host = host;
    port = port;
    path = str;
    query = query;
    return localURI;
  }
  
  private static URI relativize(URI paramURI1, URI paramURI2)
  {
    if ((paramURI2.isOpaque()) || (paramURI1.isOpaque())) {
      return paramURI2;
    }
    if ((!equalIgnoringCase(scheme, scheme)) || (!equal(authority, authority))) {
      return paramURI2;
    }
    String str1 = normalize(path);
    String str2 = normalize(path);
    if (!str1.equals(str2))
    {
      if (!str1.endsWith("/")) {
        str1 = str1 + "/";
      }
      if (!str2.startsWith(str1)) {
        return paramURI2;
      }
    }
    URI localURI = new URI();
    path = str2.substring(str1.length());
    query = query;
    fragment = fragment;
    return localURI;
  }
  
  private static int needsNormalization(String paramString)
  {
    int i = 1;
    int j = 0;
    int k = paramString.length() - 1;
    for (int m = 0; (m <= k) && (paramString.charAt(m) == '/'); m++) {}
    if (m > 1) {
      i = 0;
    }
    for (;;)
    {
      if (m > k) {
        break label174;
      }
      if ((paramString.charAt(m) == '.') && ((m == k) || (paramString.charAt(m + 1) == '/') || ((paramString.charAt(m + 1) == '.') && ((m + 1 == k) || (paramString.charAt(m + 2) == '/'))))) {
        i = 0;
      }
      j++;
      if (m <= k)
      {
        if (paramString.charAt(m++) != '/') {
          break;
        }
        while ((m <= k) && (paramString.charAt(m) == '/'))
        {
          i = 0;
          m++;
        }
      }
    }
    label174:
    return i != 0 ? -1 : j;
  }
  
  private static void split(char[] paramArrayOfChar, int[] paramArrayOfInt)
  {
    int i = paramArrayOfChar.length - 1;
    int j = 0;
    int k = 0;
    while ((j <= i) && (paramArrayOfChar[j] == '/'))
    {
      paramArrayOfChar[j] = '\000';
      j++;
      continue;
      break label52;
    }
    for (;;)
    {
      if (j > i) {
        break label103;
      }
      paramArrayOfInt[(k++)] = (j++);
      label52:
      if (j <= i)
      {
        if (paramArrayOfChar[(j++)] != '/') {
          break;
        }
        paramArrayOfChar[(j - 1)] = '\000';
        while ((j <= i) && (paramArrayOfChar[j] == '/')) {
          paramArrayOfChar[(j++)] = '\000';
        }
      }
    }
    label103:
    if (k != paramArrayOfInt.length) {
      throw new InternalError();
    }
  }
  
  private static int join(char[] paramArrayOfChar, int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt.length;
    int j = paramArrayOfChar.length - 1;
    int k = 0;
    if (paramArrayOfChar[k] == 0) {
      paramArrayOfChar[(k++)] = '/';
    }
    for (int m = 0; m < i; m++)
    {
      int n = paramArrayOfInt[m];
      if (n != -1) {
        if (k == n)
        {
          while ((k <= j) && (paramArrayOfChar[k] != 0)) {
            k++;
          }
          if (k <= j) {
            paramArrayOfChar[(k++)] = '/';
          }
        }
        else if (k < n)
        {
          while ((n <= j) && (paramArrayOfChar[n] != 0)) {
            paramArrayOfChar[(k++)] = paramArrayOfChar[(n++)];
          }
          if (n <= j) {
            paramArrayOfChar[(k++)] = '/';
          }
        }
        else
        {
          throw new InternalError();
        }
      }
    }
    return k;
  }
  
  private static void removeDots(char[] paramArrayOfChar, int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt.length;
    int j = paramArrayOfChar.length - 1;
    for (int k = 0; k < i; k++)
    {
      int m = 0;
      int n;
      do
      {
        n = paramArrayOfInt[k];
        if (paramArrayOfChar[n] == '.')
        {
          if (n == j)
          {
            m = 1;
            break;
          }
          if (paramArrayOfChar[(n + 1)] == 0)
          {
            m = 1;
            break;
          }
          if ((paramArrayOfChar[(n + 1)] == '.') && ((n + 1 == j) || (paramArrayOfChar[(n + 2)] == 0)))
          {
            m = 2;
            break;
          }
        }
        k++;
      } while (k < i);
      if ((k > i) || (m == 0)) {
        break;
      }
      if (m == 1)
      {
        paramArrayOfInt[k] = -1;
      }
      else
      {
        for (n = k - 1; (n >= 0) && (paramArrayOfInt[n] == -1); n--) {}
        if (n >= 0)
        {
          int i1 = paramArrayOfInt[n];
          if ((paramArrayOfChar[i1] != '.') || (paramArrayOfChar[(i1 + 1)] != '.') || (paramArrayOfChar[(i1 + 2)] != 0))
          {
            paramArrayOfInt[k] = -1;
            paramArrayOfInt[n] = -1;
          }
        }
      }
    }
  }
  
  private static void maybeAddLeadingDot(char[] paramArrayOfChar, int[] paramArrayOfInt)
  {
    if (paramArrayOfChar[0] == 0) {
      return;
    }
    int i = paramArrayOfInt.length;
    for (int j = 0; (j < i) && (paramArrayOfInt[j] < 0); j++) {}
    if ((j >= i) || (j == 0)) {
      return;
    }
    for (int k = paramArrayOfInt[j]; (k < paramArrayOfChar.length) && (paramArrayOfChar[k] != ':') && (paramArrayOfChar[k] != 0); k++) {}
    if ((k >= paramArrayOfChar.length) || (paramArrayOfChar[k] == 0)) {
      return;
    }
    paramArrayOfChar[0] = '.';
    paramArrayOfChar[1] = '\000';
    paramArrayOfInt[0] = 0;
  }
  
  private static String normalize(String paramString)
  {
    int i = needsNormalization(paramString);
    if (i < 0) {
      return paramString;
    }
    char[] arrayOfChar = paramString.toCharArray();
    int[] arrayOfInt = new int[i];
    split(arrayOfChar, arrayOfInt);
    removeDots(arrayOfChar, arrayOfInt);
    maybeAddLeadingDot(arrayOfChar, arrayOfInt);
    String str = new String(arrayOfChar, 0, join(arrayOfChar, arrayOfInt));
    if (str.equals(paramString)) {
      return paramString;
    }
    return str;
  }
  
  private static long lowMask(String paramString)
  {
    int i = paramString.length();
    long l = 0L;
    for (int j = 0; j < i; j++)
    {
      int k = paramString.charAt(j);
      if (k < 64) {
        l |= 1L << k;
      }
    }
    return l;
  }
  
  private static long highMask(String paramString)
  {
    int i = paramString.length();
    long l = 0L;
    for (int j = 0; j < i; j++)
    {
      int k = paramString.charAt(j);
      if ((k >= 64) && (k < 128)) {
        l |= 1L << k - 64;
      }
    }
    return l;
  }
  
  private static long lowMask(char paramChar1, char paramChar2)
  {
    long l = 0L;
    int i = Math.max(Math.min(paramChar1, 63), 0);
    int j = Math.max(Math.min(paramChar2, 63), 0);
    for (int k = i; k <= j; k++) {
      l |= 1L << k;
    }
    return l;
  }
  
  private static long highMask(char paramChar1, char paramChar2)
  {
    long l = 0L;
    int i = Math.max(Math.min(paramChar1, 127), 64) - 64;
    int j = Math.max(Math.min(paramChar2, 127), 64) - 64;
    for (int k = i; k <= j; k++) {
      l |= 1L << k;
    }
    return l;
  }
  
  private static boolean match(char paramChar, long paramLong1, long paramLong2)
  {
    if (paramChar == 0) {
      return false;
    }
    if (paramChar < '@') {
      return (1L << paramChar & paramLong1) != 0L;
    }
    if (paramChar < '') {
      return (1L << paramChar - '@' & paramLong2) != 0L;
    }
    return false;
  }
  
  private static void appendEscape(StringBuffer paramStringBuffer, byte paramByte)
  {
    paramStringBuffer.append('%');
    paramStringBuffer.append(hexDigits[(paramByte >> 4 & 0xF)]);
    paramStringBuffer.append(hexDigits[(paramByte >> 0 & 0xF)]);
  }
  
  private static void appendEncoded(StringBuffer paramStringBuffer, char paramChar)
  {
    ByteBuffer localByteBuffer = null;
    try
    {
      localByteBuffer = ThreadLocalCoders.encoderFor("UTF-8").encode(CharBuffer.wrap("" + paramChar));
    }
    catch (CharacterCodingException localCharacterCodingException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    while (localByteBuffer.hasRemaining())
    {
      int i = localByteBuffer.get() & 0xFF;
      if (i >= 128) {
        appendEscape(paramStringBuffer, (byte)i);
      } else {
        paramStringBuffer.append((char)i);
      }
    }
  }
  
  private static String quote(String paramString, long paramLong1, long paramLong2)
  {
    int i = paramString.length();
    StringBuffer localStringBuffer = null;
    int j = (paramLong1 & 1L) != 0L ? 1 : 0;
    for (int k = 0; k < paramString.length(); k++)
    {
      char c = paramString.charAt(k);
      if (c < '')
      {
        if (!match(c, paramLong1, paramLong2))
        {
          if (localStringBuffer == null)
          {
            localStringBuffer = new StringBuffer();
            localStringBuffer.append(paramString.substring(0, k));
          }
          appendEscape(localStringBuffer, (byte)c);
        }
        else if (localStringBuffer != null)
        {
          localStringBuffer.append(c);
        }
      }
      else if ((j != 0) && ((Character.isSpaceChar(c)) || (Character.isISOControl(c))))
      {
        if (localStringBuffer == null)
        {
          localStringBuffer = new StringBuffer();
          localStringBuffer.append(paramString.substring(0, k));
        }
        appendEncoded(localStringBuffer, c);
      }
      else if (localStringBuffer != null)
      {
        localStringBuffer.append(c);
      }
    }
    return localStringBuffer == null ? paramString : localStringBuffer.toString();
  }
  
  private static String encode(String paramString)
  {
    int i = paramString.length();
    if (i == 0) {
      return paramString;
    }
    int j = 0;
    while (paramString.charAt(j) < '')
    {
      j++;
      if (j >= i) {
        return paramString;
      }
    }
    String str = Normalizer.normalize(paramString, Normalizer.Form.NFC);
    ByteBuffer localByteBuffer = null;
    try
    {
      localByteBuffer = ThreadLocalCoders.encoderFor("UTF-8").encode(CharBuffer.wrap(str));
    }
    catch (CharacterCodingException localCharacterCodingException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    StringBuffer localStringBuffer = new StringBuffer();
    while (localByteBuffer.hasRemaining())
    {
      int k = localByteBuffer.get() & 0xFF;
      if (k >= 128) {
        appendEscape(localStringBuffer, (byte)k);
      } else {
        localStringBuffer.append((char)k);
      }
    }
    return localStringBuffer.toString();
  }
  
  private static int decode(char paramChar)
  {
    if ((paramChar >= '0') && (paramChar <= '9')) {
      return paramChar - '0';
    }
    if ((paramChar >= 'a') && (paramChar <= 'f')) {
      return paramChar - 'a' + 10;
    }
    if ((paramChar >= 'A') && (paramChar <= 'F')) {
      return paramChar - 'A' + 10;
    }
    if (!$assertionsDisabled) {
      throw new AssertionError();
    }
    return -1;
  }
  
  private static byte decode(char paramChar1, char paramChar2)
  {
    return (byte)((decode(paramChar1) & 0xF) << 4 | (decode(paramChar2) & 0xF) << 0);
  }
  
  private static String decode(String paramString)
  {
    if (paramString == null) {
      return paramString;
    }
    int i = paramString.length();
    if (i == 0) {
      return paramString;
    }
    if (paramString.indexOf('%') < 0) {
      return paramString;
    }
    StringBuffer localStringBuffer = new StringBuffer(i);
    ByteBuffer localByteBuffer = ByteBuffer.allocate(i);
    CharBuffer localCharBuffer = CharBuffer.allocate(i);
    CharsetDecoder localCharsetDecoder = ThreadLocalCoders.decoderFor("UTF-8").onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
    char c = paramString.charAt(0);
    int j = 0;
    int k = 0;
    while (k < i)
    {
      assert (c == paramString.charAt(k));
      if (c == '[') {
        j = 1;
      } else if ((j != 0) && (c == ']')) {
        j = 0;
      }
      if ((c != '%') || (j != 0))
      {
        localStringBuffer.append(c);
        k++;
        if (k >= i) {
          break;
        }
        c = paramString.charAt(k);
      }
      else
      {
        localByteBuffer.clear();
        int m = k;
        for (;;)
        {
          assert (i - k >= 2);
          localByteBuffer.put(decode(paramString.charAt(++k), paramString.charAt(++k)));
          k++;
          if (k < i)
          {
            c = paramString.charAt(k);
            if (c != '%') {
              break;
            }
          }
        }
        localByteBuffer.flip();
        localCharBuffer.clear();
        localCharsetDecoder.reset();
        CoderResult localCoderResult = localCharsetDecoder.decode(localByteBuffer, localCharBuffer, true);
        assert (localCoderResult.isUnderflow());
        localCoderResult = localCharsetDecoder.flush(localCharBuffer);
        assert (localCoderResult.isUnderflow());
        localStringBuffer.append(localCharBuffer.flip().toString());
      }
    }
    return localStringBuffer.toString();
  }
  
  private class Parser
  {
    private String input;
    private boolean requireServerAuthority = false;
    private int ipv6byteCount = 0;
    
    Parser(String paramString)
    {
      input = paramString;
      string = paramString;
    }
    
    private void fail(String paramString)
      throws URISyntaxException
    {
      throw new URISyntaxException(input, paramString);
    }
    
    private void fail(String paramString, int paramInt)
      throws URISyntaxException
    {
      throw new URISyntaxException(input, paramString, paramInt);
    }
    
    private void failExpecting(String paramString, int paramInt)
      throws URISyntaxException
    {
      fail("Expected " + paramString, paramInt);
    }
    
    private void failExpecting(String paramString1, String paramString2, int paramInt)
      throws URISyntaxException
    {
      fail("Expected " + paramString1 + " following " + paramString2, paramInt);
    }
    
    private String substring(int paramInt1, int paramInt2)
    {
      return input.substring(paramInt1, paramInt2);
    }
    
    private char charAt(int paramInt)
    {
      return input.charAt(paramInt);
    }
    
    private boolean at(int paramInt1, int paramInt2, char paramChar)
    {
      return (paramInt1 < paramInt2) && (charAt(paramInt1) == paramChar);
    }
    
    private boolean at(int paramInt1, int paramInt2, String paramString)
    {
      int i = paramInt1;
      int j = paramString.length();
      if (j > paramInt2 - i) {
        return false;
      }
      for (int k = 0; (k < j) && (charAt(i++) == paramString.charAt(k)); k++) {}
      return k == j;
    }
    
    private int scan(int paramInt1, int paramInt2, char paramChar)
    {
      if ((paramInt1 < paramInt2) && (charAt(paramInt1) == paramChar)) {
        return paramInt1 + 1;
      }
      return paramInt1;
    }
    
    private int scan(int paramInt1, int paramInt2, String paramString1, String paramString2)
    {
      for (int i = paramInt1; i < paramInt2; i++)
      {
        int j = charAt(i);
        if (paramString1.indexOf(j) >= 0) {
          return -1;
        }
        if (paramString2.indexOf(j) >= 0) {
          break;
        }
      }
      return i;
    }
    
    private int scanEscape(int paramInt1, int paramInt2, char paramChar)
      throws URISyntaxException
    {
      int i = paramInt1;
      char c = paramChar;
      if (c == '%')
      {
        if ((i + 3 <= paramInt2) && (URI.match(charAt(i + 1), URI.L_HEX, URI.H_HEX)) && (URI.match(charAt(i + 2), URI.L_HEX, URI.H_HEX))) {
          return i + 3;
        }
        fail("Malformed escape pair", i);
      }
      else if ((c > '') && (!Character.isSpaceChar(c)) && (!Character.isISOControl(c)))
      {
        return i + 1;
      }
      return i;
    }
    
    private int scan(int paramInt1, int paramInt2, long paramLong1, long paramLong2)
      throws URISyntaxException
    {
      int i = paramInt1;
      while (i < paramInt2)
      {
        char c = charAt(i);
        if (URI.match(c, paramLong1, paramLong2))
        {
          i++;
        }
        else if ((paramLong1 & 1L) != 0L)
        {
          int j = scanEscape(i, paramInt2, c);
          if (j > i) {
            i = j;
          } else {}
        }
      }
      return i;
    }
    
    private void checkChars(int paramInt1, int paramInt2, long paramLong1, long paramLong2, String paramString)
      throws URISyntaxException
    {
      int i = scan(paramInt1, paramInt2, paramLong1, paramLong2);
      if (i < paramInt2) {
        fail("Illegal character in " + paramString, i);
      }
    }
    
    private void checkChar(int paramInt, long paramLong1, long paramLong2, String paramString)
      throws URISyntaxException
    {
      checkChars(paramInt, paramInt + 1, paramLong1, paramLong2, paramString);
    }
    
    void parse(boolean paramBoolean)
      throws URISyntaxException
    {
      requireServerAuthority = paramBoolean;
      int j = input.length();
      int k = scan(0, j, "/?#", ":");
      int i;
      if ((k >= 0) && (at(k, j, ':')))
      {
        if (k == 0) {
          failExpecting("scheme name", 0);
        }
        checkChar(0, 0L, URI.H_ALPHA, "scheme name");
        checkChars(1, k, URI.L_SCHEME, URI.H_SCHEME, "scheme name");
        scheme = substring(0, k);
        k++;
        i = k;
        if (at(k, j, '/'))
        {
          k = parseHierarchical(k, j);
        }
        else
        {
          int m = scan(k, j, "", "#");
          if (m <= k) {
            failExpecting("scheme-specific part", k);
          }
          checkChars(k, m, URI.L_URIC, URI.H_URIC, "opaque part");
          k = m;
        }
      }
      else
      {
        i = 0;
        k = parseHierarchical(0, j);
      }
      schemeSpecificPart = substring(i, k);
      if (at(k, j, '#'))
      {
        checkChars(k + 1, j, URI.L_URIC, URI.H_URIC, "fragment");
        fragment = substring(k + 1, j);
        k = j;
      }
      if (k < j) {
        fail("end of URI", k);
      }
    }
    
    private int parseHierarchical(int paramInt1, int paramInt2)
      throws URISyntaxException
    {
      int i = paramInt1;
      if ((at(i, paramInt2, '/')) && (at(i + 1, paramInt2, '/')))
      {
        i += 2;
        j = scan(i, paramInt2, "", "/?#");
        if (j > i) {
          i = parseAuthority(i, j);
        } else if (j >= paramInt2) {
          failExpecting("authority", i);
        }
      }
      int j = scan(i, paramInt2, "", "?#");
      checkChars(i, j, URI.L_PATH, URI.H_PATH, "path");
      path = substring(i, j);
      i = j;
      if (at(i, paramInt2, '?'))
      {
        i++;
        j = scan(i, paramInt2, "", "#");
        checkChars(i, j, URI.L_URIC, URI.H_URIC, "query");
        query = substring(i, j);
        i = j;
      }
      return i;
    }
    
    private int parseAuthority(int paramInt1, int paramInt2)
      throws URISyntaxException
    {
      int i = paramInt1;
      int j = i;
      Object localObject = null;
      int k;
      if (scan(i, paramInt2, "", "]") > i) {
        k = scan(i, paramInt2, URI.L_SERVER_PERCENT, URI.H_SERVER_PERCENT) == paramInt2 ? 1 : 0;
      } else {
        k = scan(i, paramInt2, URI.L_SERVER, URI.H_SERVER) == paramInt2 ? 1 : 0;
      }
      int m = scan(i, paramInt2, URI.L_REG_NAME, URI.H_REG_NAME) == paramInt2 ? 1 : 0;
      if ((m != 0) && (k == 0))
      {
        authority = substring(i, paramInt2);
        return paramInt2;
      }
      if (k != 0) {
        try
        {
          j = parseServer(i, paramInt2);
          if (j < paramInt2) {
            failExpecting("end of authority", j);
          }
          authority = substring(i, paramInt2);
        }
        catch (URISyntaxException localURISyntaxException)
        {
          userInfo = null;
          host = null;
          port = -1;
          if (requireServerAuthority) {
            throw localURISyntaxException;
          }
          localObject = localURISyntaxException;
          j = i;
        }
      }
      if (j < paramInt2) {
        if (m != 0)
        {
          authority = substring(i, paramInt2);
        }
        else
        {
          if (localObject != null) {
            throw ((Throwable)localObject);
          }
          fail("Illegal character in authority", j);
        }
      }
      return paramInt2;
    }
    
    private int parseServer(int paramInt1, int paramInt2)
      throws URISyntaxException
    {
      int i = paramInt1;
      int j = scan(i, paramInt2, "/?#", "@");
      if ((j >= i) && (at(j, paramInt2, '@')))
      {
        checkChars(i, j, URI.L_USERINFO, URI.H_USERINFO, "user info");
        userInfo = substring(i, j);
        i = j + 1;
      }
      if (at(i, paramInt2, '['))
      {
        i++;
        j = scan(i, paramInt2, "/?#", "]");
        if ((j > i) && (at(j, paramInt2, ']')))
        {
          int k = scan(i, j, "", "%");
          if (k > i)
          {
            parseIPv6Reference(i, k);
            if (k + 1 == j) {
              fail("scope id expected");
            }
            checkChars(k + 1, j, URI.L_ALPHANUM, URI.H_ALPHANUM, "scope id");
          }
          else
          {
            parseIPv6Reference(i, j);
          }
          host = substring(i - 1, j + 1);
          i = j + 1;
        }
        else
        {
          failExpecting("closing bracket for IPv6 address", j);
        }
      }
      else
      {
        j = parseIPv4Address(i, paramInt2);
        if (j <= i) {
          j = parseHostname(i, paramInt2);
        }
        i = j;
      }
      if (at(i, paramInt2, ':'))
      {
        i++;
        j = scan(i, paramInt2, "", "/");
        if (j > i)
        {
          checkChars(i, j, URI.L_DIGIT, 0L, "port number");
          try
          {
            port = Integer.parseInt(substring(i, j));
          }
          catch (NumberFormatException localNumberFormatException)
          {
            fail("Malformed port number", i);
          }
          i = j;
        }
      }
      if (i < paramInt2) {
        failExpecting("port number", i);
      }
      return i;
    }
    
    private int scanByte(int paramInt1, int paramInt2)
      throws URISyntaxException
    {
      int i = paramInt1;
      int j = scan(i, paramInt2, URI.L_DIGIT, 0L);
      if (j <= i) {
        return j;
      }
      if (Integer.parseInt(substring(i, j)) > 255) {
        return i;
      }
      return j;
    }
    
    private int scanIPv4Address(int paramInt1, int paramInt2, boolean paramBoolean)
      throws URISyntaxException
    {
      int i = paramInt1;
      int k = scan(i, paramInt2, URI.L_DIGIT | URI.L_DOT, 0L | URI.H_DOT);
      if ((k <= i) || ((paramBoolean) && (k != paramInt2))) {
        return -1;
      }
      int j;
      if ((j = scanByte(i, k)) > i)
      {
        i = j;
        if ((j = scan(i, k, '.')) > i)
        {
          i = j;
          if ((j = scanByte(i, k)) > i)
          {
            i = j;
            if ((j = scan(i, k, '.')) > i)
            {
              i = j;
              if ((j = scanByte(i, k)) > i)
              {
                i = j;
                if ((j = scan(i, k, '.')) > i)
                {
                  i = j;
                  if ((j = scanByte(i, k)) > i)
                  {
                    i = j;
                    if (j >= k) {
                      return j;
                    }
                  }
                }
              }
            }
          }
        }
      }
      fail("Malformed IPv4 address", j);
      return -1;
    }
    
    private int takeIPv4Address(int paramInt1, int paramInt2, String paramString)
      throws URISyntaxException
    {
      int i = scanIPv4Address(paramInt1, paramInt2, true);
      if (i <= paramInt1) {
        failExpecting(paramString, paramInt1);
      }
      return i;
    }
    
    private int parseIPv4Address(int paramInt1, int paramInt2)
    {
      int i;
      try
      {
        i = scanIPv4Address(paramInt1, paramInt2, false);
      }
      catch (URISyntaxException localURISyntaxException)
      {
        return -1;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        return -1;
      }
      if ((i > paramInt1) && (i < paramInt2) && (charAt(i) != ':')) {
        i = -1;
      }
      if (i > paramInt1) {
        host = substring(paramInt1, i);
      }
      return i;
    }
    
    private int parseHostname(int paramInt1, int paramInt2)
      throws URISyntaxException
    {
      int i = paramInt1;
      int k = -1;
      do
      {
        int j = scan(i, paramInt2, URI.L_ALPHANUM, URI.H_ALPHANUM);
        if (j <= i) {
          break;
        }
        k = i;
        if (j > i)
        {
          i = j;
          j = scan(i, paramInt2, URI.L_ALPHANUM | URI.L_DASH, URI.H_ALPHANUM | URI.H_DASH);
          if (j > i)
          {
            if (charAt(j - 1) == '-') {
              fail("Illegal character in hostname", j - 1);
            }
            i = j;
          }
        }
        j = scan(i, paramInt2, '.');
        if (j <= i) {
          break;
        }
        i = j;
      } while (i < paramInt2);
      if ((i < paramInt2) && (!at(i, paramInt2, ':'))) {
        fail("Illegal character in hostname", i);
      }
      if (k < 0) {
        failExpecting("hostname", paramInt1);
      }
      if ((k > paramInt1) && (!URI.match(charAt(k), 0L, URI.H_ALPHA))) {
        fail("Illegal character in hostname", k);
      }
      host = substring(paramInt1, i);
      return i;
    }
    
    private int parseIPv6Reference(int paramInt1, int paramInt2)
      throws URISyntaxException
    {
      int i = paramInt1;
      int k = 0;
      int j = scanHexSeq(i, paramInt2);
      if (j > i)
      {
        i = j;
        if (at(i, paramInt2, "::"))
        {
          k = 1;
          i = scanHexPost(i + 2, paramInt2);
        }
        else if (at(i, paramInt2, ':'))
        {
          i = takeIPv4Address(i + 1, paramInt2, "IPv4 address");
          ipv6byteCount += 4;
        }
      }
      else if (at(i, paramInt2, "::"))
      {
        k = 1;
        i = scanHexPost(i + 2, paramInt2);
      }
      if (i < paramInt2) {
        fail("Malformed IPv6 address", paramInt1);
      }
      if (ipv6byteCount > 16) {
        fail("IPv6 address too long", paramInt1);
      }
      if ((k == 0) && (ipv6byteCount < 16)) {
        fail("IPv6 address too short", paramInt1);
      }
      if ((k != 0) && (ipv6byteCount == 16)) {
        fail("Malformed IPv6 address", paramInt1);
      }
      return i;
    }
    
    private int scanHexPost(int paramInt1, int paramInt2)
      throws URISyntaxException
    {
      int i = paramInt1;
      if (i == paramInt2) {
        return i;
      }
      int j = scanHexSeq(i, paramInt2);
      if (j > i)
      {
        i = j;
        if (at(i, paramInt2, ':'))
        {
          i++;
          i = takeIPv4Address(i, paramInt2, "hex digits or IPv4 address");
          ipv6byteCount += 4;
        }
      }
      else
      {
        i = takeIPv4Address(i, paramInt2, "hex digits or IPv4 address");
        ipv6byteCount += 4;
      }
      return i;
    }
    
    private int scanHexSeq(int paramInt1, int paramInt2)
      throws URISyntaxException
    {
      int i = paramInt1;
      int j = scan(i, paramInt2, URI.L_HEX, URI.H_HEX);
      if (j <= i) {
        return -1;
      }
      if (at(j, paramInt2, '.')) {
        return -1;
      }
      if (j > i + 4) {
        fail("IPv6 hexadecimal digit sequence too long", i);
      }
      ipv6byteCount += 2;
      for (i = j; (i < paramInt2) && (at(i, paramInt2, ':')) && (!at(i + 1, paramInt2, ':')); i = j)
      {
        i++;
        j = scan(i, paramInt2, URI.L_HEX, URI.H_HEX);
        if (j <= i) {
          failExpecting("digits for an IPv6 address", i);
        }
        if (at(j, paramInt2, '.'))
        {
          i--;
          break;
        }
        if (j > i + 4) {
          fail("IPv6 hexadecimal digit sequence too long", i);
        }
        ipv6byteCount += 2;
      }
      return i;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\URI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */