package com.sun.jndi.dns;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;

public final class DnsName
  implements Name
{
  private String domain = "";
  private ArrayList<String> labels = new ArrayList();
  private short octets = 1;
  private static final long serialVersionUID = 7040187611324710271L;
  
  public DnsName() {}
  
  public DnsName(String paramString)
    throws InvalidNameException
  {
    parse(paramString);
  }
  
  private DnsName(DnsName paramDnsName, int paramInt1, int paramInt2)
  {
    int i = paramDnsName.size() - paramInt2;
    int j = paramDnsName.size() - paramInt1;
    labels.addAll(labels.subList(i, j));
    if (size() == paramDnsName.size())
    {
      domain = domain;
      octets = octets;
    }
    else
    {
      Iterator localIterator = labels.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (str.length() > 0) {
          octets = ((short)(octets + (short)(str.length() + 1)));
        }
      }
    }
  }
  
  public String toString()
  {
    if (domain == null)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      Iterator localIterator = labels.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if ((localStringBuilder.length() > 0) || (str.length() == 0)) {
          localStringBuilder.append('.');
        }
        escape(localStringBuilder, str);
      }
      domain = localStringBuilder.toString();
    }
    return domain;
  }
  
  public boolean isHostName()
  {
    Iterator localIterator = labels.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (!isHostNameLabel(str)) {
        return false;
      }
    }
    return true;
  }
  
  public short getOctets()
  {
    return octets;
  }
  
  public int size()
  {
    return labels.size();
  }
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  public int hashCode()
  {
    int i = 0;
    for (int j = 0; j < size(); j++) {
      i = 31 * i + getKey(j).hashCode();
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((!(paramObject instanceof Name)) || ((paramObject instanceof CompositeName))) {
      return false;
    }
    Name localName = (Name)paramObject;
    return (size() == localName.size()) && (compareTo(paramObject) == 0);
  }
  
  public int compareTo(Object paramObject)
  {
    Name localName = (Name)paramObject;
    return compareRange(0, size(), localName);
  }
  
  public boolean startsWith(Name paramName)
  {
    return (size() >= paramName.size()) && (compareRange(0, paramName.size(), paramName) == 0);
  }
  
  public boolean endsWith(Name paramName)
  {
    return (size() >= paramName.size()) && (compareRange(size() - paramName.size(), size(), paramName) == 0);
  }
  
  public String get(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= size())) {
      throw new ArrayIndexOutOfBoundsException();
    }
    int i = size() - paramInt - 1;
    return (String)labels.get(i);
  }
  
  public Enumeration<String> getAll()
  {
    new Enumeration()
    {
      int pos = 0;
      
      public boolean hasMoreElements()
      {
        return pos < size();
      }
      
      public String nextElement()
      {
        if (pos < size()) {
          return get(pos++);
        }
        throw new NoSuchElementException();
      }
    };
  }
  
  public Name getPrefix(int paramInt)
  {
    return new DnsName(this, 0, paramInt);
  }
  
  public Name getSuffix(int paramInt)
  {
    return new DnsName(this, paramInt, size());
  }
  
  public Object clone()
  {
    return new DnsName(this, 0, size());
  }
  
  public Object remove(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= size())) {
      throw new ArrayIndexOutOfBoundsException();
    }
    int i = size() - paramInt - 1;
    String str = (String)labels.remove(i);
    int j = str.length();
    if (j > 0) {
      octets = ((short)(octets - (short)(j + 1)));
    }
    domain = null;
    return str;
  }
  
  public Name add(String paramString)
    throws InvalidNameException
  {
    return add(size(), paramString);
  }
  
  public Name add(int paramInt, String paramString)
    throws InvalidNameException
  {
    if ((paramInt < 0) || (paramInt > size())) {
      throw new ArrayIndexOutOfBoundsException();
    }
    int i = paramString.length();
    if (((paramInt > 0) && (i == 0)) || ((paramInt == 0) && (hasRootLabel()))) {
      throw new InvalidNameException("Empty label must be the last label in a domain name");
    }
    if (i > 0)
    {
      if (octets + i + 1 >= 256) {
        throw new InvalidNameException("Name too long");
      }
      octets = ((short)(octets + (short)(i + 1)));
    }
    int j = size() - paramInt;
    verifyLabel(paramString);
    labels.add(j, paramString);
    domain = null;
    return this;
  }
  
  public Name addAll(Name paramName)
    throws InvalidNameException
  {
    return addAll(size(), paramName);
  }
  
  public Name addAll(int paramInt, Name paramName)
    throws InvalidNameException
  {
    if ((paramName instanceof DnsName))
    {
      DnsName localDnsName = (DnsName)paramName;
      if (localDnsName.isEmpty()) {
        return this;
      }
      if (((paramInt > 0) && (localDnsName.hasRootLabel())) || ((paramInt == 0) && (hasRootLabel()))) {
        throw new InvalidNameException("Empty label must be the last label in a domain name");
      }
      short s = (short)(octets + octets - 1);
      if (s > 255) {
        throw new InvalidNameException("Name too long");
      }
      octets = s;
      int j = size() - paramInt;
      labels.addAll(j, labels);
      if (isEmpty()) {
        domain = domain;
      } else if ((domain == null) || (domain == null)) {
        domain = null;
      } else if (paramInt == 0) {
        domain = (domain + (domain.equals(".") ? "" : ".") + domain);
      } else if (paramInt == size()) {
        domain = (domain + (domain.equals(".") ? "" : ".") + domain);
      } else {
        domain = null;
      }
    }
    else if ((paramName instanceof CompositeName))
    {
      paramName = (DnsName)paramName;
    }
    else
    {
      for (int i = paramName.size() - 1; i >= 0; i--) {
        add(paramInt, paramName.get(i));
      }
    }
    return this;
  }
  
  boolean hasRootLabel()
  {
    return (!isEmpty()) && (get(0).equals(""));
  }
  
  private int compareRange(int paramInt1, int paramInt2, Name paramName)
  {
    if ((paramName instanceof CompositeName)) {
      paramName = (DnsName)paramName;
    }
    int i = Math.min(paramInt2 - paramInt1, paramName.size());
    for (int j = 0; j < i; j++)
    {
      String str1 = get(j + paramInt1);
      String str2 = paramName.get(j);
      int k = size() - (j + paramInt1) - 1;
      int m = compareLabels(str1, str2);
      if (m != 0) {
        return m;
      }
    }
    return paramInt2 - paramInt1 - paramName.size();
  }
  
  String getKey(int paramInt)
  {
    return keyForLabel(get(paramInt));
  }
  
  private void parse(String paramString)
    throws InvalidNameException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if (c == '\\')
      {
        c = getEscapedOctet(paramString, i++);
        if (isDigit(paramString.charAt(i))) {
          i += 2;
        }
        localStringBuffer.append(c);
      }
      else if (c != '.')
      {
        localStringBuffer.append(c);
      }
      else
      {
        add(0, localStringBuffer.toString());
        localStringBuffer.delete(0, i);
      }
    }
    if ((!paramString.equals("")) && (!paramString.equals("."))) {
      add(0, localStringBuffer.toString());
    }
    domain = paramString;
  }
  
  private static char getEscapedOctet(String paramString, int paramInt)
    throws InvalidNameException
  {
    try
    {
      char c1 = paramString.charAt(++paramInt);
      if (isDigit(c1))
      {
        char c2 = paramString.charAt(++paramInt);
        char c3 = paramString.charAt(++paramInt);
        if ((isDigit(c2)) && (isDigit(c3))) {
          return (char)((c1 - '0') * 100 + (c2 - '0') * 10 + (c3 - '0'));
        }
        throw new InvalidNameException("Invalid escape sequence in " + paramString);
      }
      return c1;
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new InvalidNameException("Invalid escape sequence in " + paramString);
    }
  }
  
  private static void verifyLabel(String paramString)
    throws InvalidNameException
  {
    if (paramString.length() > 63) {
      throw new InvalidNameException("Label exceeds 63 octets: " + paramString);
    }
    for (int i = 0; i < paramString.length(); i++)
    {
      int j = paramString.charAt(i);
      if ((j & 0xFF00) != 0) {
        throw new InvalidNameException("Label has two-byte char: " + paramString);
      }
    }
  }
  
  private static boolean isHostNameLabel(String paramString)
  {
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if (!isHostNameChar(c)) {
        return false;
      }
    }
    return (!paramString.startsWith("-")) && (!paramString.endsWith("-"));
  }
  
  private static boolean isHostNameChar(char paramChar)
  {
    return (paramChar == '-') || ((paramChar >= 'a') && (paramChar <= 'z')) || ((paramChar >= 'A') && (paramChar <= 'Z')) || ((paramChar >= '0') && (paramChar <= '9'));
  }
  
  private static boolean isDigit(char paramChar)
  {
    return (paramChar >= '0') && (paramChar <= '9');
  }
  
  private static void escape(StringBuilder paramStringBuilder, String paramString)
  {
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if ((c == '.') || (c == '\\')) {
        paramStringBuilder.append('\\');
      }
      paramStringBuilder.append(c);
    }
  }
  
  private static int compareLabels(String paramString1, String paramString2)
  {
    int i = Math.min(paramString1.length(), paramString2.length());
    for (int j = 0; j < i; j++)
    {
      int k = paramString1.charAt(j);
      int m = paramString2.charAt(j);
      if ((k >= 65) && (k <= 90)) {
        k = (char)(k + 32);
      }
      if ((m >= 65) && (m <= 90)) {
        m = (char)(m + 32);
      }
      if (k != m) {
        return k - m;
      }
    }
    return paramString1.length() - paramString2.length();
  }
  
  private static String keyForLabel(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramString.length());
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if ((c >= 'A') && (c <= 'Z')) {
        c = (char)(c + ' ');
      }
      localStringBuffer.append(c);
    }
    return localStringBuffer.toString();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeObject(toString());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    try
    {
      parse((String)paramObjectInputStream.readObject());
    }
    catch (InvalidNameException localInvalidNameException)
    {
      throw new StreamCorruptedException("Invalid name: " + domain);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\dns\DnsName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */