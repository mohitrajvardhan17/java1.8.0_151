package java.util.jar;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import sun.misc.ASCIICaseInsensitiveComparator;
import sun.util.logging.PlatformLogger;

public class Attributes
  implements Map<Object, Object>, Cloneable
{
  protected Map<Object, Object> map;
  
  public Attributes()
  {
    this(11);
  }
  
  public Attributes(int paramInt)
  {
    map = new HashMap(paramInt);
  }
  
  public Attributes(Attributes paramAttributes)
  {
    map = new HashMap(paramAttributes);
  }
  
  public Object get(Object paramObject)
  {
    return map.get(paramObject);
  }
  
  public String getValue(String paramString)
  {
    return (String)get(new Name(paramString));
  }
  
  public String getValue(Name paramName)
  {
    return (String)get(paramName);
  }
  
  public Object put(Object paramObject1, Object paramObject2)
  {
    return map.put((Name)paramObject1, (String)paramObject2);
  }
  
  public String putValue(String paramString1, String paramString2)
  {
    return (String)put(new Name(paramString1), paramString2);
  }
  
  public Object remove(Object paramObject)
  {
    return map.remove(paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return map.containsValue(paramObject);
  }
  
  public boolean containsKey(Object paramObject)
  {
    return map.containsKey(paramObject);
  }
  
  public void putAll(Map<?, ?> paramMap)
  {
    if (!Attributes.class.isInstance(paramMap)) {
      throw new ClassCastException();
    }
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      put(localEntry.getKey(), localEntry.getValue());
    }
  }
  
  public void clear()
  {
    map.clear();
  }
  
  public int size()
  {
    return map.size();
  }
  
  public boolean isEmpty()
  {
    return map.isEmpty();
  }
  
  public Set<Object> keySet()
  {
    return map.keySet();
  }
  
  public Collection<Object> values()
  {
    return map.values();
  }
  
  public Set<Map.Entry<Object, Object>> entrySet()
  {
    return map.entrySet();
  }
  
  public boolean equals(Object paramObject)
  {
    return map.equals(paramObject);
  }
  
  public int hashCode()
  {
    return map.hashCode();
  }
  
  public Object clone()
  {
    return new Attributes(this);
  }
  
  void write(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    Iterator localIterator = entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      StringBuffer localStringBuffer = new StringBuffer(((Name)localEntry.getKey()).toString());
      localStringBuffer.append(": ");
      String str = (String)localEntry.getValue();
      if (str != null)
      {
        byte[] arrayOfByte = str.getBytes("UTF8");
        str = new String(arrayOfByte, 0, 0, arrayOfByte.length);
      }
      localStringBuffer.append(str);
      localStringBuffer.append("\r\n");
      Manifest.make72Safe(localStringBuffer);
      paramDataOutputStream.writeBytes(localStringBuffer.toString());
    }
    paramDataOutputStream.writeBytes("\r\n");
  }
  
  void writeMain(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    String str1 = Name.MANIFEST_VERSION.toString();
    String str2 = getValue(str1);
    if (str2 == null)
    {
      str1 = Name.SIGNATURE_VERSION.toString();
      str2 = getValue(str1);
    }
    if (str2 != null) {
      paramDataOutputStream.writeBytes(str1 + ": " + str2 + "\r\n");
    }
    Iterator localIterator = entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str3 = ((Name)localEntry.getKey()).toString();
      if ((str2 != null) && (!str3.equalsIgnoreCase(str1)))
      {
        StringBuffer localStringBuffer = new StringBuffer(str3);
        localStringBuffer.append(": ");
        String str4 = (String)localEntry.getValue();
        if (str4 != null)
        {
          byte[] arrayOfByte = str4.getBytes("UTF8");
          str4 = new String(arrayOfByte, 0, 0, arrayOfByte.length);
        }
        localStringBuffer.append(str4);
        localStringBuffer.append("\r\n");
        Manifest.make72Safe(localStringBuffer);
        paramDataOutputStream.writeBytes(localStringBuffer.toString());
      }
    }
    paramDataOutputStream.writeBytes("\r\n");
  }
  
  void read(Manifest.FastInputStream paramFastInputStream, byte[] paramArrayOfByte)
    throws IOException
  {
    String str1 = null;
    String str2 = null;
    Object localObject = null;
    int i;
    while ((i = paramFastInputStream.readLine(paramArrayOfByte)) != -1)
    {
      int j = 0;
      if (paramArrayOfByte[(--i)] != 10) {
        throw new IOException("line too long");
      }
      if ((i > 0) && (paramArrayOfByte[(i - 1)] == 13)) {
        i--;
      }
      if (i == 0) {
        break;
      }
      int k = 0;
      if (paramArrayOfByte[0] == 32)
      {
        if (str1 == null) {
          throw new IOException("misplaced continuation line");
        }
        j = 1;
        byte[] arrayOfByte = new byte[localObject.length + i - 1];
        System.arraycopy(localObject, 0, arrayOfByte, 0, localObject.length);
        System.arraycopy(paramArrayOfByte, 1, arrayOfByte, localObject.length, i - 1);
        if (paramFastInputStream.peek() == 32)
        {
          localObject = arrayOfByte;
          continue;
        }
        str2 = new String(arrayOfByte, 0, arrayOfByte.length, "UTF8");
        localObject = null;
      }
      else
      {
        while (paramArrayOfByte[(k++)] != 58) {
          if (k >= i) {
            throw new IOException("invalid header field");
          }
        }
        if (paramArrayOfByte[(k++)] != 32) {
          throw new IOException("invalid header field");
        }
        str1 = new String(paramArrayOfByte, 0, 0, k - 2);
        if (paramFastInputStream.peek() == 32)
        {
          localObject = new byte[i - k];
          System.arraycopy(paramArrayOfByte, k, localObject, 0, i - k);
          continue;
        }
        str2 = new String(paramArrayOfByte, k, i - k, "UTF8");
      }
      try
      {
        if ((putValue(str1, str2) != null) && (j == 0)) {
          PlatformLogger.getLogger("java.util.jar").warning("Duplicate name in Manifest: " + str1 + ".\nEnsure that the manifest does not have duplicate entries, and\nthat blank lines separate individual sections in both your\nmanifest and in the META-INF/MANIFEST.MF entry in the jar file.");
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw new IOException("invalid header field name: " + str1);
      }
    }
  }
  
  public static class Name
  {
    private String name;
    private int hashCode = -1;
    public static final Name MANIFEST_VERSION = new Name("Manifest-Version");
    public static final Name SIGNATURE_VERSION = new Name("Signature-Version");
    public static final Name CONTENT_TYPE = new Name("Content-Type");
    public static final Name CLASS_PATH = new Name("Class-Path");
    public static final Name MAIN_CLASS = new Name("Main-Class");
    public static final Name SEALED = new Name("Sealed");
    public static final Name EXTENSION_LIST = new Name("Extension-List");
    public static final Name EXTENSION_NAME = new Name("Extension-Name");
    @Deprecated
    public static final Name EXTENSION_INSTALLATION = new Name("Extension-Installation");
    public static final Name IMPLEMENTATION_TITLE = new Name("Implementation-Title");
    public static final Name IMPLEMENTATION_VERSION = new Name("Implementation-Version");
    public static final Name IMPLEMENTATION_VENDOR = new Name("Implementation-Vendor");
    @Deprecated
    public static final Name IMPLEMENTATION_VENDOR_ID = new Name("Implementation-Vendor-Id");
    @Deprecated
    public static final Name IMPLEMENTATION_URL = new Name("Implementation-URL");
    public static final Name SPECIFICATION_TITLE = new Name("Specification-Title");
    public static final Name SPECIFICATION_VERSION = new Name("Specification-Version");
    public static final Name SPECIFICATION_VENDOR = new Name("Specification-Vendor");
    
    public Name(String paramString)
    {
      if (paramString == null) {
        throw new NullPointerException("name");
      }
      if (!isValid(paramString)) {
        throw new IllegalArgumentException(paramString);
      }
      name = paramString.intern();
    }
    
    private static boolean isValid(String paramString)
    {
      int i = paramString.length();
      if ((i > 70) || (i == 0)) {
        return false;
      }
      for (int j = 0; j < i; j++) {
        if (!isValid(paramString.charAt(j))) {
          return false;
        }
      }
      return true;
    }
    
    private static boolean isValid(char paramChar)
    {
      return (isAlpha(paramChar)) || (isDigit(paramChar)) || (paramChar == '_') || (paramChar == '-');
    }
    
    private static boolean isAlpha(char paramChar)
    {
      return ((paramChar >= 'a') && (paramChar <= 'z')) || ((paramChar >= 'A') && (paramChar <= 'Z'));
    }
    
    private static boolean isDigit(char paramChar)
    {
      return (paramChar >= '0') && (paramChar <= '9');
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof Name))
      {
        Comparator localComparator = ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER;
        return localComparator.compare(name, name) == 0;
      }
      return false;
    }
    
    public int hashCode()
    {
      if (hashCode == -1) {
        hashCode = ASCIICaseInsensitiveComparator.lowerCaseHashCode(name);
      }
      return hashCode;
    }
    
    public String toString()
    {
      return name;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\jar\Attributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */