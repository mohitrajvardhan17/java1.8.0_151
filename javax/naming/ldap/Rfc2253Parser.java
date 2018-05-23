package javax.naming.ldap;

import java.util.ArrayList;
import java.util.List;
import javax.naming.InvalidNameException;

final class Rfc2253Parser
{
  private final String name;
  private final char[] chars;
  private final int len;
  private int cur = 0;
  
  Rfc2253Parser(String paramString)
  {
    name = paramString;
    len = paramString.length();
    chars = paramString.toCharArray();
  }
  
  List<Rdn> parseDn()
    throws InvalidNameException
  {
    cur = 0;
    ArrayList localArrayList = new ArrayList(len / 3 + 10);
    if (len == 0) {
      return localArrayList;
    }
    localArrayList.add(doParse(new Rdn()));
    while (cur < len) {
      if ((chars[cur] == ',') || (chars[cur] == ';'))
      {
        cur += 1;
        localArrayList.add(0, doParse(new Rdn()));
      }
      else
      {
        throw new InvalidNameException("Invalid name: " + name);
      }
    }
    return localArrayList;
  }
  
  Rdn parseRdn()
    throws InvalidNameException
  {
    return parseRdn(new Rdn());
  }
  
  Rdn parseRdn(Rdn paramRdn)
    throws InvalidNameException
  {
    paramRdn = doParse(paramRdn);
    if (cur < len) {
      throw new InvalidNameException("Invalid RDN: " + name);
    }
    return paramRdn;
  }
  
  private Rdn doParse(Rdn paramRdn)
    throws InvalidNameException
  {
    while (cur < len)
    {
      consumeWhitespace();
      String str1 = parseAttrType();
      consumeWhitespace();
      if ((cur >= len) || (chars[cur] != '=')) {
        throw new InvalidNameException("Invalid name: " + name);
      }
      cur += 1;
      consumeWhitespace();
      String str2 = parseAttrValue();
      consumeWhitespace();
      paramRdn.put(str1, Rdn.unescapeValue(str2));
      if ((cur >= len) || (chars[cur] != '+')) {
        break;
      }
      cur += 1;
    }
    paramRdn.sort();
    return paramRdn;
  }
  
  private String parseAttrType()
    throws InvalidNameException
  {
    int i = cur;
    while (cur < len)
    {
      char c = chars[cur];
      if ((!Character.isLetterOrDigit(c)) && (c != '.') && (c != '-') && (c != ' ')) {
        break;
      }
      cur += 1;
    }
    while ((cur > i) && (chars[(cur - 1)] == ' ')) {
      cur -= 1;
    }
    if (i == cur) {
      throw new InvalidNameException("Invalid name: " + name);
    }
    return new String(chars, i, cur - i);
  }
  
  private String parseAttrValue()
    throws InvalidNameException
  {
    if ((cur < len) && (chars[cur] == '#')) {
      return parseBinaryAttrValue();
    }
    if ((cur < len) && (chars[cur] == '"')) {
      return parseQuotedAttrValue();
    }
    return parseStringAttrValue();
  }
  
  private String parseBinaryAttrValue()
    throws InvalidNameException
  {
    int i = cur;
    for (cur += 1; (cur < len) && (Character.isLetterOrDigit(chars[cur])); cur += 1) {}
    return new String(chars, i, cur - i);
  }
  
  private String parseQuotedAttrValue()
    throws InvalidNameException
  {
    int i = cur;
    for (cur += 1; (cur < len) && (chars[cur] != '"'); cur += 1) {
      if (chars[cur] == '\\') {
        cur += 1;
      }
    }
    if (cur >= len) {
      throw new InvalidNameException("Invalid name: " + name);
    }
    cur += 1;
    return new String(chars, i, cur - i);
  }
  
  private String parseStringAttrValue()
    throws InvalidNameException
  {
    int i = cur;
    int j = -1;
    while ((cur < len) && (!atTerminator()))
    {
      if (chars[cur] == '\\')
      {
        cur += 1;
        j = cur;
      }
      cur += 1;
    }
    if (cur > len) {
      throw new InvalidNameException("Invalid name: " + name);
    }
    for (int k = cur; (k > i) && (isWhitespace(chars[(k - 1)])) && (j != k - 1); k--) {}
    return new String(chars, i, k - i);
  }
  
  private void consumeWhitespace()
  {
    while ((cur < len) && (isWhitespace(chars[cur]))) {
      cur += 1;
    }
  }
  
  private boolean atTerminator()
  {
    return (cur < len) && ((chars[cur] == ',') || (chars[cur] == ';') || (chars[cur] == '+'));
  }
  
  private static boolean isWhitespace(char paramChar)
  {
    return (paramChar == ' ') || (paramChar == '\r');
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\Rfc2253Parser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */