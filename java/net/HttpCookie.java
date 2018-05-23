package java.net;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.TimeZone;
import sun.misc.JavaNetHttpCookieAccess;
import sun.misc.SharedSecrets;

public final class HttpCookie
  implements Cloneable
{
  private final String name;
  private String value;
  private String comment;
  private String commentURL;
  private boolean toDiscard;
  private String domain;
  private long maxAge = -1L;
  private String path;
  private String portlist;
  private boolean secure;
  private boolean httpOnly;
  private int version = 1;
  private final String header;
  private final long whenCreated;
  private static final long MAX_AGE_UNSPECIFIED = -1L;
  private static final String[] COOKIE_DATE_FORMATS = { "EEE',' dd-MMM-yyyy HH:mm:ss 'GMT'", "EEE',' dd MMM yyyy HH:mm:ss 'GMT'", "EEE MMM dd yyyy HH:mm:ss 'GMT'Z", "EEE',' dd-MMM-yy HH:mm:ss 'GMT'", "EEE',' dd MMM yy HH:mm:ss 'GMT'", "EEE MMM dd yy HH:mm:ss 'GMT'Z" };
  private static final String SET_COOKIE = "set-cookie:";
  private static final String SET_COOKIE2 = "set-cookie2:";
  private static final String tspecials = ",; ";
  static final Map<String, CookieAttributeAssignor> assignors = new HashMap();
  static final TimeZone GMT = TimeZone.getTimeZone("GMT");
  
  public HttpCookie(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, null);
  }
  
  private HttpCookie(String paramString1, String paramString2, String paramString3)
  {
    paramString1 = paramString1.trim();
    if ((paramString1.length() == 0) || (!isToken(paramString1)) || (paramString1.charAt(0) == '$')) {
      throw new IllegalArgumentException("Illegal cookie name");
    }
    name = paramString1;
    value = paramString2;
    toDiscard = false;
    secure = false;
    whenCreated = System.currentTimeMillis();
    portlist = null;
    header = paramString3;
  }
  
  public static List<HttpCookie> parse(String paramString)
  {
    return parse(paramString, false);
  }
  
  private static List<HttpCookie> parse(String paramString, boolean paramBoolean)
  {
    int i = guessCookieVersion(paramString);
    if (startsWithIgnoreCase(paramString, "set-cookie2:")) {
      paramString = paramString.substring("set-cookie2:".length());
    } else if (startsWithIgnoreCase(paramString, "set-cookie:")) {
      paramString = paramString.substring("set-cookie:".length());
    }
    ArrayList localArrayList = new ArrayList();
    Object localObject;
    if (i == 0)
    {
      localObject = parseInternal(paramString, paramBoolean);
      ((HttpCookie)localObject).setVersion(0);
      localArrayList.add(localObject);
    }
    else
    {
      localObject = splitMultiCookies(paramString);
      Iterator localIterator = ((List)localObject).iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        HttpCookie localHttpCookie = parseInternal(str, paramBoolean);
        localHttpCookie.setVersion(1);
        localArrayList.add(localHttpCookie);
      }
    }
    return localArrayList;
  }
  
  public boolean hasExpired()
  {
    if (maxAge == 0L) {
      return true;
    }
    if (maxAge == -1L) {
      return false;
    }
    long l = (System.currentTimeMillis() - whenCreated) / 1000L;
    return l > maxAge;
  }
  
  public void setComment(String paramString)
  {
    comment = paramString;
  }
  
  public String getComment()
  {
    return comment;
  }
  
  public void setCommentURL(String paramString)
  {
    commentURL = paramString;
  }
  
  public String getCommentURL()
  {
    return commentURL;
  }
  
  public void setDiscard(boolean paramBoolean)
  {
    toDiscard = paramBoolean;
  }
  
  public boolean getDiscard()
  {
    return toDiscard;
  }
  
  public void setPortlist(String paramString)
  {
    portlist = paramString;
  }
  
  public String getPortlist()
  {
    return portlist;
  }
  
  public void setDomain(String paramString)
  {
    if (paramString != null) {
      domain = paramString.toLowerCase();
    } else {
      domain = paramString;
    }
  }
  
  public String getDomain()
  {
    return domain;
  }
  
  public void setMaxAge(long paramLong)
  {
    maxAge = paramLong;
  }
  
  public long getMaxAge()
  {
    return maxAge;
  }
  
  public void setPath(String paramString)
  {
    path = paramString;
  }
  
  public String getPath()
  {
    return path;
  }
  
  public void setSecure(boolean paramBoolean)
  {
    secure = paramBoolean;
  }
  
  public boolean getSecure()
  {
    return secure;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setValue(String paramString)
  {
    value = paramString;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public int getVersion()
  {
    return version;
  }
  
  public void setVersion(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("cookie version should be 0 or 1");
    }
    version = paramInt;
  }
  
  public boolean isHttpOnly()
  {
    return httpOnly;
  }
  
  public void setHttpOnly(boolean paramBoolean)
  {
    httpOnly = paramBoolean;
  }
  
  public static boolean domainMatches(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      return false;
    }
    boolean bool = ".local".equalsIgnoreCase(paramString1);
    int i = paramString1.indexOf('.');
    if (i == 0) {
      i = paramString1.indexOf('.', 1);
    }
    if ((!bool) && ((i == -1) || (i == paramString1.length() - 1))) {
      return false;
    }
    int j = paramString2.indexOf('.');
    if ((j == -1) && ((bool) || (paramString1.equalsIgnoreCase(paramString2 + ".local")))) {
      return true;
    }
    int k = paramString1.length();
    int m = paramString2.length() - k;
    if (m == 0) {
      return paramString2.equalsIgnoreCase(paramString1);
    }
    if (m > 0)
    {
      String str1 = paramString2.substring(0, m);
      String str2 = paramString2.substring(m);
      return (str1.indexOf('.') == -1) && (str2.equalsIgnoreCase(paramString1));
    }
    if (m == -1) {
      return (paramString1.charAt(0) == '.') && (paramString2.equalsIgnoreCase(paramString1.substring(1)));
    }
    return false;
  }
  
  public String toString()
  {
    if (getVersion() > 0) {
      return toRFC2965HeaderString();
    }
    return toNetscapeHeaderString();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof HttpCookie)) {
      return false;
    }
    HttpCookie localHttpCookie = (HttpCookie)paramObject;
    return (equalsIgnoreCase(getName(), localHttpCookie.getName())) && (equalsIgnoreCase(getDomain(), localHttpCookie.getDomain())) && (Objects.equals(getPath(), localHttpCookie.getPath()));
  }
  
  public int hashCode()
  {
    int i = name.toLowerCase().hashCode();
    int j = domain != null ? domain.toLowerCase().hashCode() : 0;
    int k = path != null ? path.hashCode() : 0;
    return i + j + k;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new RuntimeException(localCloneNotSupportedException.getMessage());
    }
  }
  
  private static boolean isToken(String paramString)
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      int k = paramString.charAt(j);
      if ((k < 32) || (k >= 127) || (",; ".indexOf(k) != -1)) {
        return false;
      }
    }
    return true;
  }
  
  private static HttpCookie parseInternal(String paramString, boolean paramBoolean)
  {
    HttpCookie localHttpCookie = null;
    String str1 = null;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ";");
    String str2;
    String str3;
    try
    {
      str1 = localStringTokenizer.nextToken();
      int i = str1.indexOf('=');
      if (i != -1)
      {
        str2 = str1.substring(0, i).trim();
        str3 = str1.substring(i + 1).trim();
        if (paramBoolean) {
          localHttpCookie = new HttpCookie(str2, stripOffSurroundingQuote(str3), paramString);
        } else {
          localHttpCookie = new HttpCookie(str2, stripOffSurroundingQuote(str3));
        }
      }
      else
      {
        throw new IllegalArgumentException("Invalid cookie name-value pair");
      }
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      throw new IllegalArgumentException("Empty cookie header string");
    }
    while (localStringTokenizer.hasMoreTokens())
    {
      str1 = localStringTokenizer.nextToken();
      int j = str1.indexOf('=');
      if (j != -1)
      {
        str2 = str1.substring(0, j).trim();
        str3 = str1.substring(j + 1).trim();
      }
      else
      {
        str2 = str1.trim();
        str3 = null;
      }
      assignAttribute(localHttpCookie, str2, str3);
    }
    return localHttpCookie;
  }
  
  private static void assignAttribute(HttpCookie paramHttpCookie, String paramString1, String paramString2)
  {
    paramString2 = stripOffSurroundingQuote(paramString2);
    CookieAttributeAssignor localCookieAttributeAssignor = (CookieAttributeAssignor)assignors.get(paramString1.toLowerCase());
    if (localCookieAttributeAssignor != null) {
      localCookieAttributeAssignor.assign(paramHttpCookie, paramString1, paramString2);
    }
  }
  
  private String header()
  {
    return header;
  }
  
  private String toNetscapeHeaderString()
  {
    return getName() + "=" + getValue();
  }
  
  private String toRFC2965HeaderString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(getName()).append("=\"").append(getValue()).append('"');
    if (getPath() != null) {
      localStringBuilder.append(";$Path=\"").append(getPath()).append('"');
    }
    if (getDomain() != null) {
      localStringBuilder.append(";$Domain=\"").append(getDomain()).append('"');
    }
    if (getPortlist() != null) {
      localStringBuilder.append(";$Port=\"").append(getPortlist()).append('"');
    }
    return localStringBuilder.toString();
  }
  
  private long expiryDate2DeltaSeconds(String paramString)
  {
    GregorianCalendar localGregorianCalendar = new GregorianCalendar(GMT);
    int i = 0;
    while (i < COOKIE_DATE_FORMATS.length)
    {
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(COOKIE_DATE_FORMATS[i], Locale.US);
      localGregorianCalendar.set(1970, 0, 1, 0, 0, 0);
      localSimpleDateFormat.setTimeZone(GMT);
      localSimpleDateFormat.setLenient(false);
      localSimpleDateFormat.set2DigitYearStart(localGregorianCalendar.getTime());
      try
      {
        localGregorianCalendar.setTime(localSimpleDateFormat.parse(paramString));
        if (!COOKIE_DATE_FORMATS[i].contains("yyyy"))
        {
          int j = localGregorianCalendar.get(1);
          j %= 100;
          if (j < 70) {
            j += 2000;
          } else {
            j += 1900;
          }
          localGregorianCalendar.set(1, j);
        }
        return (localGregorianCalendar.getTimeInMillis() - whenCreated) / 1000L;
      }
      catch (Exception localException)
      {
        i++;
      }
    }
    return 0L;
  }
  
  private static int guessCookieVersion(String paramString)
  {
    int i = 0;
    paramString = paramString.toLowerCase();
    if (paramString.indexOf("expires=") != -1) {
      i = 0;
    } else if (paramString.indexOf("version=") != -1) {
      i = 1;
    } else if (paramString.indexOf("max-age") != -1) {
      i = 1;
    } else if (startsWithIgnoreCase(paramString, "set-cookie2:")) {
      i = 1;
    }
    return i;
  }
  
  private static String stripOffSurroundingQuote(String paramString)
  {
    if ((paramString != null) && (paramString.length() > 2) && (paramString.charAt(0) == '"') && (paramString.charAt(paramString.length() - 1) == '"')) {
      return paramString.substring(1, paramString.length() - 1);
    }
    if ((paramString != null) && (paramString.length() > 2) && (paramString.charAt(0) == '\'') && (paramString.charAt(paramString.length() - 1) == '\'')) {
      return paramString.substring(1, paramString.length() - 1);
    }
    return paramString;
  }
  
  private static boolean equalsIgnoreCase(String paramString1, String paramString2)
  {
    if (paramString1 == paramString2) {
      return true;
    }
    if ((paramString1 != null) && (paramString2 != null)) {
      return paramString1.equalsIgnoreCase(paramString2);
    }
    return false;
  }
  
  private static boolean startsWithIgnoreCase(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      return false;
    }
    return (paramString1.length() >= paramString2.length()) && (paramString2.equalsIgnoreCase(paramString1.substring(0, paramString2.length())));
  }
  
  private static List<String> splitMultiCookies(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    int j = 0;
    int k = 0;
    while (j < paramString.length())
    {
      int m = paramString.charAt(j);
      if (m == 34) {
        i++;
      }
      if ((m == 44) && (i % 2 == 0))
      {
        localArrayList.add(paramString.substring(k, j));
        k = j + 1;
      }
      j++;
    }
    localArrayList.add(paramString.substring(k));
    return localArrayList;
  }
  
  static
  {
    assignors.put("comment", new CookieAttributeAssignor()
    {
      public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2)
      {
        if (paramAnonymousHttpCookie.getComment() == null) {
          paramAnonymousHttpCookie.setComment(paramAnonymousString2);
        }
      }
    });
    assignors.put("commenturl", new CookieAttributeAssignor()
    {
      public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2)
      {
        if (paramAnonymousHttpCookie.getCommentURL() == null) {
          paramAnonymousHttpCookie.setCommentURL(paramAnonymousString2);
        }
      }
    });
    assignors.put("discard", new CookieAttributeAssignor()
    {
      public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2)
      {
        paramAnonymousHttpCookie.setDiscard(true);
      }
    });
    assignors.put("domain", new CookieAttributeAssignor()
    {
      public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2)
      {
        if (paramAnonymousHttpCookie.getDomain() == null) {
          paramAnonymousHttpCookie.setDomain(paramAnonymousString2);
        }
      }
    });
    assignors.put("max-age", new CookieAttributeAssignor()
    {
      public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2)
      {
        try
        {
          long l = Long.parseLong(paramAnonymousString2);
          if (paramAnonymousHttpCookie.getMaxAge() == -1L) {
            paramAnonymousHttpCookie.setMaxAge(l);
          }
        }
        catch (NumberFormatException localNumberFormatException)
        {
          throw new IllegalArgumentException("Illegal cookie max-age attribute");
        }
      }
    });
    assignors.put("path", new CookieAttributeAssignor()
    {
      public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2)
      {
        if (paramAnonymousHttpCookie.getPath() == null) {
          paramAnonymousHttpCookie.setPath(paramAnonymousString2);
        }
      }
    });
    assignors.put("port", new CookieAttributeAssignor()
    {
      public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2)
      {
        if (paramAnonymousHttpCookie.getPortlist() == null) {
          paramAnonymousHttpCookie.setPortlist(paramAnonymousString2 == null ? "" : paramAnonymousString2);
        }
      }
    });
    assignors.put("secure", new CookieAttributeAssignor()
    {
      public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2)
      {
        paramAnonymousHttpCookie.setSecure(true);
      }
    });
    assignors.put("httponly", new CookieAttributeAssignor()
    {
      public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2)
      {
        paramAnonymousHttpCookie.setHttpOnly(true);
      }
    });
    assignors.put("version", new CookieAttributeAssignor()
    {
      public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2)
      {
        try
        {
          int i = Integer.parseInt(paramAnonymousString2);
          paramAnonymousHttpCookie.setVersion(i);
        }
        catch (NumberFormatException localNumberFormatException) {}
      }
    });
    assignors.put("expires", new CookieAttributeAssignor()
    {
      public void assign(HttpCookie paramAnonymousHttpCookie, String paramAnonymousString1, String paramAnonymousString2)
      {
        if (paramAnonymousHttpCookie.getMaxAge() == -1L) {
          paramAnonymousHttpCookie.setMaxAge(paramAnonymousHttpCookie.expiryDate2DeltaSeconds(paramAnonymousString2));
        }
      }
    });
    SharedSecrets.setJavaNetHttpCookieAccess(new JavaNetHttpCookieAccess()
    {
      public List<HttpCookie> parse(String paramAnonymousString)
      {
        return HttpCookie.parse(paramAnonymousString, true);
      }
      
      public String header(HttpCookie paramAnonymousHttpCookie)
      {
        return header;
      }
    });
  }
  
  static abstract interface CookieAttributeAssignor
  {
    public abstract void assign(HttpCookie paramHttpCookie, String paramString1, String paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\HttpCookie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */