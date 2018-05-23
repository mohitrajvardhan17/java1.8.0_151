package javax.naming.ldap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

public class Rdn
  implements Serializable, Comparable<Object>
{
  private transient ArrayList<RdnEntry> entries;
  private static final int DEFAULT_SIZE = 1;
  private static final long serialVersionUID = -5994465067210009656L;
  private static final String escapees = ",=+<>#;\"\\";
  
  public Rdn(Attributes paramAttributes)
    throws InvalidNameException
  {
    if (paramAttributes.size() == 0) {
      throw new InvalidNameException("Attributes cannot be empty");
    }
    entries = new ArrayList(paramAttributes.size());
    NamingEnumeration localNamingEnumeration = paramAttributes.getAll();
    try
    {
      for (int i = 0; localNamingEnumeration.hasMore(); i++)
      {
        localObject = new RdnEntry(null);
        Attribute localAttribute = (Attribute)localNamingEnumeration.next();
        type = localAttribute.getID();
        value = localAttribute.get();
        entries.add(i, localObject);
      }
    }
    catch (NamingException localNamingException)
    {
      Object localObject = new InvalidNameException(localNamingException.getMessage());
      ((InvalidNameException)localObject).initCause(localNamingException);
      throw ((Throwable)localObject);
    }
    sort();
  }
  
  public Rdn(String paramString)
    throws InvalidNameException
  {
    entries = new ArrayList(1);
    new Rfc2253Parser(paramString).parseRdn(this);
  }
  
  public Rdn(Rdn paramRdn)
  {
    entries = new ArrayList(entries.size());
    entries.addAll(entries);
  }
  
  public Rdn(String paramString, Object paramObject)
    throws InvalidNameException
  {
    if (paramObject == null) {
      throw new NullPointerException("Cannot set value to null");
    }
    if ((paramString.equals("")) || (isEmptyValue(paramObject))) {
      throw new InvalidNameException("type or value cannot be empty, type:" + paramString + " value:" + paramObject);
    }
    entries = new ArrayList(1);
    put(paramString, paramObject);
  }
  
  private boolean isEmptyValue(Object paramObject)
  {
    return (((paramObject instanceof String)) && (paramObject.equals(""))) || (((paramObject instanceof byte[])) && (((byte[])paramObject).length == 0));
  }
  
  Rdn()
  {
    entries = new ArrayList(1);
  }
  
  Rdn put(String paramString, Object paramObject)
  {
    RdnEntry localRdnEntry = new RdnEntry(null);
    type = paramString;
    if ((paramObject instanceof byte[])) {
      value = ((byte[])paramObject).clone();
    } else {
      value = paramObject;
    }
    entries.add(localRdnEntry);
    return this;
  }
  
  void sort()
  {
    if (entries.size() > 1) {
      Collections.sort(entries);
    }
  }
  
  public Object getValue()
  {
    return ((RdnEntry)entries.get(0)).getValue();
  }
  
  public String getType()
  {
    return ((RdnEntry)entries.get(0)).getType();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = entries.size();
    if (i > 0) {
      localStringBuilder.append(entries.get(0));
    }
    for (int j = 1; j < i; j++)
    {
      localStringBuilder.append('+');
      localStringBuilder.append(entries.get(j));
    }
    return localStringBuilder.toString();
  }
  
  public int compareTo(Object paramObject)
  {
    if (!(paramObject instanceof Rdn)) {
      throw new ClassCastException("The obj is not a Rdn");
    }
    if (paramObject == this) {
      return 0;
    }
    Rdn localRdn = (Rdn)paramObject;
    int i = Math.min(entries.size(), entries.size());
    for (int j = 0; j < i; j++)
    {
      int k = ((RdnEntry)entries.get(j)).compareTo((RdnEntry)entries.get(j));
      if (k != 0) {
        return k;
      }
    }
    return entries.size() - entries.size();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof Rdn)) {
      return false;
    }
    Rdn localRdn = (Rdn)paramObject;
    if (entries.size() != localRdn.size()) {
      return false;
    }
    for (int i = 0; i < entries.size(); i++) {
      if (!((RdnEntry)entries.get(i)).equals(entries.get(i))) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 0;
    for (int j = 0; j < entries.size(); j++) {
      i += ((RdnEntry)entries.get(j)).hashCode();
    }
    return i;
  }
  
  public Attributes toAttributes()
  {
    BasicAttributes localBasicAttributes = new BasicAttributes(true);
    for (int i = 0; i < entries.size(); i++)
    {
      RdnEntry localRdnEntry = (RdnEntry)entries.get(i);
      Attribute localAttribute = localBasicAttributes.put(localRdnEntry.getType(), localRdnEntry.getValue());
      if (localAttribute != null)
      {
        localAttribute.add(localRdnEntry.getValue());
        localBasicAttributes.put(localAttribute);
      }
    }
    return localBasicAttributes;
  }
  
  public int size()
  {
    return entries.size();
  }
  
  public static String escapeValue(Object paramObject)
  {
    return (paramObject instanceof byte[]) ? escapeBinaryValue((byte[])paramObject) : escapeStringValue((String)paramObject);
  }
  
  private static String escapeStringValue(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    StringBuilder localStringBuilder = new StringBuilder(2 * paramString.length());
    for (int i = 0; (i < arrayOfChar.length) && (isWhitespace(arrayOfChar[i])); i++) {}
    for (int j = arrayOfChar.length - 1; (j >= 0) && (isWhitespace(arrayOfChar[j])); j--) {}
    for (int k = 0; k < arrayOfChar.length; k++)
    {
      char c = arrayOfChar[k];
      if ((k < i) || (k > j) || (",=+<>#;\"\\".indexOf(c) >= 0)) {
        localStringBuilder.append('\\');
      }
      localStringBuilder.append(c);
    }
    return localStringBuilder.toString();
  }
  
  private static String escapeBinaryValue(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder(1 + 2 * paramArrayOfByte.length);
    localStringBuilder.append("#");
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      int j = paramArrayOfByte[i];
      localStringBuilder.append(Character.forDigit(0xF & j >>> 4, 16));
      localStringBuilder.append(Character.forDigit(0xF & j, 16));
    }
    return localStringBuilder.toString();
  }
  
  public static Object unescapeValue(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    int i = 0;
    int j = arrayOfChar.length;
    while ((i < j) && (isWhitespace(arrayOfChar[i]))) {
      i++;
    }
    while ((i < j) && (isWhitespace(arrayOfChar[(j - 1)]))) {
      j--;
    }
    if ((j != arrayOfChar.length) && (i < j) && (arrayOfChar[(j - 1)] == '\\')) {
      j++;
    }
    if (i >= j) {
      return "";
    }
    if (arrayOfChar[i] == '#') {
      return decodeHexPairs(arrayOfChar, ++i, j);
    }
    if ((arrayOfChar[i] == '"') && (arrayOfChar[(j - 1)] == '"'))
    {
      i++;
      j--;
    }
    StringBuilder localStringBuilder = new StringBuilder(j - i);
    int k = -1;
    for (int m = i; m < j; m++) {
      if ((arrayOfChar[m] == '\\') && (m + 1 < j))
      {
        if (!Character.isLetterOrDigit(arrayOfChar[(m + 1)]))
        {
          m++;
          localStringBuilder.append(arrayOfChar[m]);
          k = m;
        }
        else
        {
          byte[] arrayOfByte = getUtf8Octets(arrayOfChar, m, j);
          if (arrayOfByte.length > 0)
          {
            try
            {
              localStringBuilder.append(new String(arrayOfByte, "UTF8"));
            }
            catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
            m += arrayOfByte.length * 3 - 1;
          }
          else
          {
            throw new IllegalArgumentException("Not a valid attribute string value:" + paramString + ",improper usage of backslash");
          }
        }
      }
      else {
        localStringBuilder.append(arrayOfChar[m]);
      }
    }
    m = localStringBuilder.length();
    if ((isWhitespace(localStringBuilder.charAt(m - 1))) && (k != j - 1)) {
      localStringBuilder.setLength(m - 1);
    }
    return localStringBuilder.toString();
  }
  
  private static byte[] decodeHexPairs(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte = new byte[(paramInt2 - paramInt1) / 2];
    for (int i = 0; paramInt1 + 1 < paramInt2; i++)
    {
      int j = Character.digit(paramArrayOfChar[paramInt1], 16);
      int k = Character.digit(paramArrayOfChar[(paramInt1 + 1)], 16);
      if ((j < 0) || (k < 0)) {
        break;
      }
      arrayOfByte[i] = ((byte)((j << 4) + k));
      paramInt1 += 2;
    }
    if (paramInt1 != paramInt2) {
      throw new IllegalArgumentException("Illegal attribute value: " + new String(paramArrayOfChar));
    }
    return arrayOfByte;
  }
  
  private static byte[] getUtf8Octets(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte1 = new byte[(paramInt2 - paramInt1) / 3];
    int i = 0;
    while ((paramInt1 + 2 < paramInt2) && (paramArrayOfChar[(paramInt1++)] == '\\'))
    {
      int j = Character.digit(paramArrayOfChar[(paramInt1++)], 16);
      int k = Character.digit(paramArrayOfChar[(paramInt1++)], 16);
      if ((j < 0) || (k < 0)) {
        break;
      }
      arrayOfByte1[(i++)] = ((byte)((j << 4) + k));
    }
    if (i == arrayOfByte1.length) {
      return arrayOfByte1;
    }
    byte[] arrayOfByte2 = new byte[i];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
    return arrayOfByte2;
  }
  
  private static boolean isWhitespace(char paramChar)
  {
    return (paramChar == ' ') || (paramChar == '\r');
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(toString());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    entries = new ArrayList(1);
    String str = (String)paramObjectInputStream.readObject();
    try
    {
      new Rfc2253Parser(str).parseRdn(this);
    }
    catch (InvalidNameException localInvalidNameException)
    {
      throw new StreamCorruptedException("Invalid name: " + str);
    }
  }
  
  private static class RdnEntry
    implements Comparable<RdnEntry>
  {
    private String type;
    private Object value;
    private String comparable = null;
    
    private RdnEntry() {}
    
    String getType()
    {
      return type;
    }
    
    Object getValue()
    {
      return value;
    }
    
    public int compareTo(RdnEntry paramRdnEntry)
    {
      int i = type.compareToIgnoreCase(type);
      if (i != 0) {
        return i;
      }
      if (value.equals(value)) {
        return 0;
      }
      return getValueComparable().compareTo(paramRdnEntry.getValueComparable());
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if (!(paramObject instanceof RdnEntry)) {
        return false;
      }
      RdnEntry localRdnEntry = (RdnEntry)paramObject;
      return (type.equalsIgnoreCase(type)) && (getValueComparable().equals(localRdnEntry.getValueComparable()));
    }
    
    public int hashCode()
    {
      return type.toUpperCase(Locale.ENGLISH).hashCode() + getValueComparable().hashCode();
    }
    
    public String toString()
    {
      return type + "=" + Rdn.escapeValue(value);
    }
    
    private String getValueComparable()
    {
      if (comparable != null) {
        return comparable;
      }
      if ((value instanceof byte[])) {
        comparable = Rdn.escapeBinaryValue((byte[])value);
      } else {
        comparable = ((String)value).toUpperCase(Locale.ENGLISH);
      }
      return comparable;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\Rdn.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */