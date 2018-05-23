package com.sun.jndi.toolkit.dir;

import java.util.Enumeration;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.InvalidSearchFilterException;

public class SearchFilter
  implements AttrFilter
{
  String filter;
  int pos;
  private StringFilter rootFilter;
  protected static final boolean debug = false;
  protected static final char BEGIN_FILTER_TOKEN = '(';
  protected static final char END_FILTER_TOKEN = ')';
  protected static final char AND_TOKEN = '&';
  protected static final char OR_TOKEN = '|';
  protected static final char NOT_TOKEN = '!';
  protected static final char EQUAL_TOKEN = '=';
  protected static final char APPROX_TOKEN = '~';
  protected static final char LESS_TOKEN = '<';
  protected static final char GREATER_TOKEN = '>';
  protected static final char EXTEND_TOKEN = ':';
  protected static final char WILDCARD_TOKEN = '*';
  static final int EQUAL_MATCH = 1;
  static final int APPROX_MATCH = 2;
  static final int GREATER_MATCH = 3;
  static final int LESS_MATCH = 4;
  
  public SearchFilter(String paramString)
    throws InvalidSearchFilterException
  {
    filter = paramString;
    pos = 0;
    normalizeFilter();
    rootFilter = createNextFilter();
  }
  
  public boolean check(Attributes paramAttributes)
    throws NamingException
  {
    if (paramAttributes == null) {
      return false;
    }
    return rootFilter.check(paramAttributes);
  }
  
  protected void normalizeFilter()
  {
    skipWhiteSpace();
    if (getCurrentChar() != '(') {
      filter = ('(' + filter + ')');
    }
  }
  
  private void skipWhiteSpace()
  {
    while (Character.isWhitespace(getCurrentChar())) {
      consumeChar();
    }
  }
  
  protected StringFilter createNextFilter()
    throws InvalidSearchFilterException
  {
    skipWhiteSpace();
    Object localObject;
    try
    {
      if (getCurrentChar() != '(') {
        throw new InvalidSearchFilterException("expected \"(\" at position " + pos);
      }
      consumeChar();
      skipWhiteSpace();
      switch (getCurrentChar())
      {
      case '&': 
        localObject = new CompoundFilter(true);
        ((StringFilter)localObject).parse();
        break;
      case '|': 
        localObject = new CompoundFilter(false);
        ((StringFilter)localObject).parse();
        break;
      case '!': 
        localObject = new NotFilter();
        ((StringFilter)localObject).parse();
        break;
      default: 
        localObject = new AtomicFilter();
        ((StringFilter)localObject).parse();
      }
      skipWhiteSpace();
      if (getCurrentChar() != ')') {
        throw new InvalidSearchFilterException("expected \")\" at position " + pos);
      }
      consumeChar();
    }
    catch (InvalidSearchFilterException localInvalidSearchFilterException)
    {
      throw localInvalidSearchFilterException;
    }
    catch (Exception localException)
    {
      throw new InvalidSearchFilterException("Unable to parse character " + pos + " in \"" + filter + "\"");
    }
    return (StringFilter)localObject;
  }
  
  protected char getCurrentChar()
  {
    return filter.charAt(pos);
  }
  
  protected char relCharAt(int paramInt)
  {
    return filter.charAt(pos + paramInt);
  }
  
  protected void consumeChar()
  {
    pos += 1;
  }
  
  protected void consumeChars(int paramInt)
  {
    pos += paramInt;
  }
  
  protected int relIndexOf(int paramInt)
  {
    return filter.indexOf(paramInt, pos) - pos;
  }
  
  protected String relSubstring(int paramInt1, int paramInt2)
  {
    return filter.substring(paramInt1 + pos, paramInt2 + pos);
  }
  
  public static String format(Attributes paramAttributes)
    throws NamingException
  {
    if ((paramAttributes == null) || (paramAttributes.size() == 0)) {
      return "objectClass=*";
    }
    String str1 = "(& ";
    NamingEnumeration localNamingEnumeration1 = paramAttributes.getAll();
    while (localNamingEnumeration1.hasMore())
    {
      Attribute localAttribute = (Attribute)localNamingEnumeration1.next();
      if ((localAttribute.size() == 0) || ((localAttribute.size() == 1) && (localAttribute.get() == null)))
      {
        str1 = str1 + "(" + localAttribute.getID() + "=*)";
      }
      else
      {
        NamingEnumeration localNamingEnumeration2 = localAttribute.getAll();
        while (localNamingEnumeration2.hasMore())
        {
          String str2 = getEncodedStringRep(localNamingEnumeration2.next());
          if (str2 != null) {
            str1 = str1 + "(" + localAttribute.getID() + "=" + str2 + ")";
          }
        }
      }
    }
    str1 = str1 + ")";
    return str1;
  }
  
  private static void hexDigit(StringBuffer paramStringBuffer, byte paramByte)
  {
    char c = (char)(paramByte >> 4 & 0xF);
    if (c > '\t') {
      c = (char)(c - '\n' + 65);
    } else {
      c = (char)(c + '0');
    }
    paramStringBuffer.append(c);
    c = (char)(paramByte & 0xF);
    if (c > '\t') {
      c = (char)(c - '\n' + 65);
    } else {
      c = (char)(c + '0');
    }
    paramStringBuffer.append(c);
  }
  
  private static String getEncodedStringRep(Object paramObject)
    throws NamingException
  {
    if (paramObject == null) {
      return null;
    }
    int j;
    if ((paramObject instanceof byte[]))
    {
      byte[] arrayOfByte = (byte[])paramObject;
      localStringBuffer = new StringBuffer(arrayOfByte.length * 3);
      for (j = 0; j < arrayOfByte.length; j++)
      {
        localStringBuffer.append('\\');
        hexDigit(localStringBuffer, arrayOfByte[j]);
      }
      return localStringBuffer.toString();
    }
    String str;
    if (!(paramObject instanceof String)) {
      str = paramObject.toString();
    } else {
      str = (String)paramObject;
    }
    int i = str.length();
    StringBuffer localStringBuffer = new StringBuffer(i);
    for (int k = 0; k < i; k++) {
      switch (j = str.charAt(k))
      {
      case '*': 
        localStringBuffer.append("\\2a");
        break;
      case '(': 
        localStringBuffer.append("\\28");
        break;
      case ')': 
        localStringBuffer.append("\\29");
        break;
      case '\\': 
        localStringBuffer.append("\\5c");
        break;
      case '\000': 
        localStringBuffer.append("\\00");
        break;
      default: 
        localStringBuffer.append(j);
      }
    }
    return localStringBuffer.toString();
  }
  
  public static int findUnescaped(char paramChar, String paramString, int paramInt)
  {
    int i = paramString.length();
    while (paramInt < i)
    {
      int j = paramString.indexOf(paramChar, paramInt);
      if ((j == paramInt) || (j == -1) || (paramString.charAt(j - 1) != '\\')) {
        return j;
      }
      paramInt = j + 1;
    }
    return -1;
  }
  
  public static String format(String paramString, Object[] paramArrayOfObject)
    throws NamingException
  {
    int j = 0;
    int k = 0;
    StringBuffer localStringBuffer = new StringBuffer(paramString.length());
    while ((j = findUnescaped('{', paramString, k)) >= 0)
    {
      int m = j + 1;
      int n = paramString.indexOf('}', m);
      if (n < 0) {
        throw new InvalidSearchFilterException("unbalanced {: " + paramString);
      }
      int i;
      try
      {
        i = Integer.parseInt(paramString.substring(m, n));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new InvalidSearchFilterException("integer expected inside {}: " + paramString);
      }
      if (i >= paramArrayOfObject.length) {
        throw new InvalidSearchFilterException("number exceeds argument list: " + i);
      }
      localStringBuffer.append(paramString.substring(k, j)).append(getEncodedStringRep(paramArrayOfObject[i]));
      k = n + 1;
    }
    if (k < paramString.length()) {
      localStringBuffer.append(paramString.substring(k));
    }
    return localStringBuffer.toString();
  }
  
  public static Attributes selectAttributes(Attributes paramAttributes, String[] paramArrayOfString)
    throws NamingException
  {
    if (paramArrayOfString == null) {
      return paramAttributes;
    }
    BasicAttributes localBasicAttributes = new BasicAttributes();
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      Attribute localAttribute = paramAttributes.get(paramArrayOfString[i]);
      if (localAttribute != null) {
        localBasicAttributes.put(localAttribute);
      }
    }
    return localBasicAttributes;
  }
  
  final class AtomicFilter
    implements SearchFilter.StringFilter
  {
    private String attrID;
    private String value;
    private int matchType;
    
    AtomicFilter() {}
    
    public void parse()
      throws InvalidSearchFilterException
    {
      SearchFilter.this.skipWhiteSpace();
      try
      {
        int i = relIndexOf(41);
        int j = relIndexOf(61);
        int k = relCharAt(j - 1);
        switch (k)
        {
        case 126: 
          matchType = 2;
          attrID = relSubstring(0, j - 1);
          value = relSubstring(j + 1, i);
          break;
        case 62: 
          matchType = 3;
          attrID = relSubstring(0, j - 1);
          value = relSubstring(j + 1, i);
          break;
        case 60: 
          matchType = 4;
          attrID = relSubstring(0, j - 1);
          value = relSubstring(j + 1, i);
          break;
        case 58: 
          throw new OperationNotSupportedException("Extensible match not supported");
        default: 
          matchType = 1;
          attrID = relSubstring(0, j);
          value = relSubstring(j + 1, i);
        }
        attrID = attrID.trim();
        value = value.trim();
        consumeChars(i);
      }
      catch (Exception localException)
      {
        InvalidSearchFilterException localInvalidSearchFilterException = new InvalidSearchFilterException("Unable to parse character " + pos + " in \"" + filter + "\"");
        localInvalidSearchFilterException.setRootCause(localException);
        throw localInvalidSearchFilterException;
      }
    }
    
    public boolean check(Attributes paramAttributes)
    {
      NamingEnumeration localNamingEnumeration;
      try
      {
        Attribute localAttribute = paramAttributes.get(attrID);
        if (localAttribute == null) {
          return false;
        }
        localNamingEnumeration = localAttribute.getAll();
      }
      catch (NamingException localNamingException)
      {
        return false;
      }
      while (localNamingEnumeration.hasMoreElements())
      {
        String str = localNamingEnumeration.nextElement().toString();
        switch (matchType)
        {
        case 1: 
        case 2: 
          if (substringMatch(value, str)) {
            return true;
          }
          break;
        case 3: 
          if (str.compareTo(value) >= 0) {
            return true;
          }
          break;
        case 4: 
          if (str.compareTo(value) <= 0) {
            return true;
          }
          break;
        }
      }
      return false;
    }
    
    private boolean substringMatch(String paramString1, String paramString2)
    {
      if (paramString1.equals(new Character('*').toString())) {
        return true;
      }
      if (paramString1.indexOf('*') == -1) {
        return paramString1.equalsIgnoreCase(paramString2);
      }
      int i = 0;
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString1, "*", false);
      if ((paramString1.charAt(0) != '*') && (!paramString2.toLowerCase(Locale.ENGLISH).startsWith(localStringTokenizer.nextToken().toLowerCase(Locale.ENGLISH)))) {
        return false;
      }
      while (localStringTokenizer.hasMoreTokens())
      {
        String str = localStringTokenizer.nextToken();
        i = paramString2.toLowerCase(Locale.ENGLISH).indexOf(str.toLowerCase(Locale.ENGLISH), i);
        if (i == -1) {
          return false;
        }
        i += str.length();
      }
      return (paramString1.charAt(paramString1.length() - 1) == '*') || (i == paramString2.length());
    }
  }
  
  final class CompoundFilter
    implements SearchFilter.StringFilter
  {
    private Vector<SearchFilter.StringFilter> subFilters = new Vector();
    private boolean polarity;
    
    CompoundFilter(boolean paramBoolean)
    {
      polarity = paramBoolean;
    }
    
    public void parse()
      throws InvalidSearchFilterException
    {
      consumeChar();
      while (getCurrentChar() != ')')
      {
        SearchFilter.StringFilter localStringFilter = createNextFilter();
        subFilters.addElement(localStringFilter);
        SearchFilter.this.skipWhiteSpace();
      }
    }
    
    public boolean check(Attributes paramAttributes)
      throws NamingException
    {
      for (int i = 0; i < subFilters.size(); i++)
      {
        SearchFilter.StringFilter localStringFilter = (SearchFilter.StringFilter)subFilters.elementAt(i);
        if (localStringFilter.check(paramAttributes) != polarity) {
          return !polarity;
        }
      }
      return polarity;
    }
  }
  
  final class NotFilter
    implements SearchFilter.StringFilter
  {
    private SearchFilter.StringFilter filter;
    
    NotFilter() {}
    
    public void parse()
      throws InvalidSearchFilterException
    {
      consumeChar();
      filter = createNextFilter();
    }
    
    public boolean check(Attributes paramAttributes)
      throws NamingException
    {
      return !filter.check(paramAttributes);
    }
  }
  
  static abstract interface StringFilter
    extends AttrFilter
  {
    public abstract void parse()
      throws InvalidSearchFilterException;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\dir\SearchFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */