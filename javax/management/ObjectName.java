package javax.management;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ObjectName
  implements Comparable<ObjectName>, QueryExp
{
  private static final long oldSerialVersionUID = -5467795090068647408L;
  private static final long newSerialVersionUID = 1081892073854801359L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("domain", String.class), new ObjectStreamField("propertyList", Hashtable.class), new ObjectStreamField("propertyListString", String.class), new ObjectStreamField("canonicalName", String.class), new ObjectStreamField("pattern", Boolean.TYPE), new ObjectStreamField("propertyPattern", Boolean.TYPE) };
  private static final ObjectStreamField[] newSerialPersistentFields = new ObjectStreamField[0];
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat = false;
  private static final Property[] _Empty_property_array = new Property[0];
  private transient String _canonicalName;
  private transient Property[] _kp_array;
  private transient Property[] _ca_array;
  private transient int _domain_length = 0;
  private transient Map<String, String> _propertyList;
  private transient boolean _domain_pattern = false;
  private transient boolean _property_list_pattern = false;
  private transient boolean _property_value_pattern = false;
  public static final ObjectName WILDCARD = Util.newObjectName("*:*");
  
  private void construct(String paramString)
    throws MalformedObjectNameException
  {
    if (paramString == null) {
      throw new NullPointerException("name cannot be null");
    }
    if (paramString.length() == 0)
    {
      _canonicalName = "*:*";
      _kp_array = _Empty_property_array;
      _ca_array = _Empty_property_array;
      _domain_length = 1;
      _propertyList = null;
      _domain_pattern = true;
      _property_list_pattern = true;
      _property_value_pattern = false;
      return;
    }
    char[] arrayOfChar1 = paramString.toCharArray();
    int i = arrayOfChar1.length;
    char[] arrayOfChar2 = new char[i];
    int j = 0;
    int k = 0;
    while (k < i) {
      switch (arrayOfChar1[k])
      {
      case ':': 
        _domain_length = (k++);
        break;
      case '=': 
        k++;
        int n = k;
        do
        {
          if ((n >= i) || (arrayOfChar1[(n++)] == ':')) {
            break;
          }
        } while (n != i);
        throw new MalformedObjectNameException("Domain part must be specified");
      case '\n': 
        throw new MalformedObjectNameException("Invalid character '\\n' in domain name");
      case '*': 
      case '?': 
        _domain_pattern = true;
        k++;
        break;
      default: 
        k++;
      }
    }
    if (k == i) {
      throw new MalformedObjectNameException("Key properties cannot be empty");
    }
    System.arraycopy(arrayOfChar1, 0, arrayOfChar2, 0, _domain_length);
    arrayOfChar2[_domain_length] = ':';
    j = _domain_length + 1;
    HashMap localHashMap = new HashMap();
    int i2 = 0;
    Object localObject2 = new String[10];
    _kp_array = new Property[10];
    _property_list_pattern = false;
    _property_value_pattern = false;
    while (k < i)
    {
      int m = arrayOfChar1[k];
      if (m == 42)
      {
        if (_property_list_pattern) {
          throw new MalformedObjectNameException("Cannot have several '*' characters in pattern property list");
        }
        _property_list_pattern = true;
        k++;
        if ((k < i) && (arrayOfChar1[k] != ',')) {
          throw new MalformedObjectNameException("Invalid character found after '*': end of name or ',' expected");
        }
        if (k == i)
        {
          if (i2 != 0) {
            break;
          }
          _kp_array = _Empty_property_array;
          _ca_array = _Empty_property_array;
          _propertyList = Collections.emptyMap();
          break;
        }
        k++;
      }
      else
      {
        int i3 = k;
        int i4 = i3;
        if (arrayOfChar1[i3] == '=') {
          throw new MalformedObjectNameException("Invalid key (empty)");
        }
        char c;
        while ((i3 < i) && ((c = arrayOfChar1[(i3++)]) != '=')) {
          switch (c)
          {
          case '\n': 
          case '*': 
          case ',': 
          case ':': 
          case '?': 
            String str2 = "" + c;
            throw new MalformedObjectNameException("Invalid character '" + str2 + "' in key part of property");
          }
        }
        if (arrayOfChar1[(i3 - 1)] != '=') {
          throw new MalformedObjectNameException("Unterminated key property part");
        }
        int i6 = i3;
        int i5 = i6 - i4 - 1;
        int i8 = 0;
        int i1;
        int i7;
        Object localObject3;
        if ((i3 < i) && (arrayOfChar1[i3] == '"'))
        {
          i1 = 1;
          for (;;)
          {
            i3++;
            if ((i3 >= i) || ((c = arrayOfChar1[i3]) == '"')) {
              break;
            }
            if (c == '\\')
            {
              i3++;
              if (i3 == i) {
                throw new MalformedObjectNameException("Unterminated quoted value");
              }
              switch (c = arrayOfChar1[i3])
              {
              case '"': 
              case '*': 
              case '?': 
              case '\\': 
              case 'n': 
                break;
              default: 
                throw new MalformedObjectNameException("Invalid escape sequence '\\" + c + "' in quoted value");
              }
            }
            else
            {
              if (c == '\n') {
                throw new MalformedObjectNameException("Newline in quoted value");
              }
              switch (c)
              {
              case '*': 
              case '?': 
                i8 = 1;
              }
            }
          }
          if (i3 == i) {
            throw new MalformedObjectNameException("Unterminated quoted value");
          }
          i3++;
          i7 = i3 - i6;
        }
        else
        {
          i1 = 0;
          while ((i3 < i) && ((c = arrayOfChar1[i3]) != ',')) {
            switch (c)
            {
            case '*': 
            case '?': 
              i8 = 1;
              i3++;
              break;
            case '\n': 
            case '"': 
            case ':': 
            case '=': 
              localObject3 = "" + c;
              throw new MalformedObjectNameException("Invalid character '" + (String)localObject3 + "' in value part of property");
            default: 
              i3++;
            }
          }
          i7 = i3 - i6;
        }
        if (i3 == i - 1)
        {
          if (i1 != 0) {
            throw new MalformedObjectNameException("Invalid ending character `" + arrayOfChar1[i3] + "'");
          }
          throw new MalformedObjectNameException("Invalid ending comma");
        }
        i3++;
        Object localObject1;
        if (i8 == 0)
        {
          localObject1 = new Property(i4, i5, i7);
        }
        else
        {
          _property_value_pattern = true;
          localObject1 = new PatternProperty(i4, i5, i7);
        }
        String str1 = paramString.substring(i4, i4 + i5);
        if (i2 == localObject2.length)
        {
          localObject3 = new String[i2 + 10];
          System.arraycopy(localObject2, 0, localObject3, 0, i2);
          localObject2 = localObject3;
        }
        localObject2[i2] = str1;
        addProperty((Property)localObject1, i2, localHashMap, str1);
        i2++;
        k = i3;
      }
    }
    setCanonicalName(arrayOfChar1, arrayOfChar2, (String[])localObject2, localHashMap, j, i2);
  }
  
  private void construct(String paramString, Map<String, String> paramMap)
    throws MalformedObjectNameException
  {
    if (paramString == null) {
      throw new NullPointerException("domain cannot be null");
    }
    if (paramMap == null) {
      throw new NullPointerException("key property list cannot be null");
    }
    if (paramMap.isEmpty()) {
      throw new MalformedObjectNameException("key property list cannot be empty");
    }
    if (!isDomain(paramString)) {
      throw new MalformedObjectNameException("Invalid domain: " + paramString);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString).append(':');
    _domain_length = paramString.length();
    int i = paramMap.size();
    _kp_array = new Property[i];
    String[] arrayOfString = new String[i];
    HashMap localHashMap = new HashMap();
    int k = 0;
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localObject2 = (Map.Entry)localIterator.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(",");
      }
      localObject3 = (String)((Map.Entry)localObject2).getKey();
      String str;
      try
      {
        str = (String)((Map.Entry)localObject2).getValue();
      }
      catch (ClassCastException localClassCastException)
      {
        throw new MalformedObjectNameException(localClassCastException.getMessage());
      }
      int j = localStringBuilder.length();
      checkKey((String)localObject3);
      localStringBuilder.append((String)localObject3);
      arrayOfString[k] = localObject3;
      localStringBuilder.append("=");
      boolean bool = checkValue(str);
      localStringBuilder.append(str);
      Object localObject1;
      if (!bool)
      {
        localObject1 = new Property(j, ((String)localObject3).length(), str.length());
      }
      else
      {
        _property_value_pattern = true;
        localObject1 = new PatternProperty(j, ((String)localObject3).length(), str.length());
      }
      addProperty((Property)localObject1, k, localHashMap, (String)localObject3);
      k++;
    }
    int m = localStringBuilder.length();
    Object localObject2 = new char[m];
    localStringBuilder.getChars(0, m, (char[])localObject2, 0);
    Object localObject3 = new char[m];
    System.arraycopy(localObject2, 0, localObject3, 0, _domain_length + 1);
    setCanonicalName((char[])localObject2, (char[])localObject3, arrayOfString, localHashMap, _domain_length + 1, _kp_array.length);
  }
  
  private void addProperty(Property paramProperty, int paramInt, Map<String, Property> paramMap, String paramString)
    throws MalformedObjectNameException
  {
    if (paramMap.containsKey(paramString)) {
      throw new MalformedObjectNameException("key `" + paramString + "' already defined");
    }
    if (paramInt == _kp_array.length)
    {
      Property[] arrayOfProperty = new Property[paramInt + 10];
      System.arraycopy(_kp_array, 0, arrayOfProperty, 0, paramInt);
      _kp_array = arrayOfProperty;
    }
    _kp_array[paramInt] = paramProperty;
    paramMap.put(paramString, paramProperty);
  }
  
  private void setCanonicalName(char[] paramArrayOfChar1, char[] paramArrayOfChar2, String[] paramArrayOfString, Map<String, Property> paramMap, int paramInt1, int paramInt2)
  {
    if (_kp_array != _Empty_property_array)
    {
      String[] arrayOfString = new String[paramInt2];
      Property[] arrayOfProperty = new Property[paramInt2];
      System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, paramInt2);
      Arrays.sort(arrayOfString);
      paramArrayOfString = arrayOfString;
      System.arraycopy(_kp_array, 0, arrayOfProperty, 0, paramInt2);
      _kp_array = arrayOfProperty;
      _ca_array = new Property[paramInt2];
      for (int i = 0; i < paramInt2; i++) {
        _ca_array[i] = ((Property)paramMap.get(paramArrayOfString[i]));
      }
      i = paramInt2 - 1;
      for (int k = 0; k <= i; k++)
      {
        Property localProperty = _ca_array[k];
        int j = _key_length + _value_length + 1;
        System.arraycopy(paramArrayOfChar1, _key_index, paramArrayOfChar2, paramInt1, j);
        localProperty.setKeyIndex(paramInt1);
        paramInt1 += j;
        if (k != i)
        {
          paramArrayOfChar2[paramInt1] = ',';
          paramInt1++;
        }
      }
    }
    if (_property_list_pattern)
    {
      if (_kp_array != _Empty_property_array) {
        paramArrayOfChar2[(paramInt1++)] = ',';
      }
      paramArrayOfChar2[(paramInt1++)] = '*';
    }
    _canonicalName = new String(paramArrayOfChar2, 0, paramInt1).intern();
  }
  
  private static int parseKey(char[] paramArrayOfChar, int paramInt)
    throws MalformedObjectNameException
  {
    int i = paramInt;
    int j = paramInt;
    int k = paramArrayOfChar.length;
    while (i < k)
    {
      char c = paramArrayOfChar[(i++)];
      switch (c)
      {
      case '\n': 
      case '*': 
      case ',': 
      case ':': 
      case '?': 
        String str = "" + c;
        throw new MalformedObjectNameException("Invalid character in key: `" + str + "'");
      case '=': 
        j = i - 1;
        break;
      default: 
        if (i >= k) {
          j = i;
        }
        break;
      }
    }
    return j;
  }
  
  private static int[] parseValue(char[] paramArrayOfChar, int paramInt)
    throws MalformedObjectNameException
  {
    int i = 0;
    int j = paramInt;
    int k = paramInt;
    int m = paramArrayOfChar.length;
    int n = paramArrayOfChar[paramInt];
    char c;
    if (n == 34)
    {
      j++;
      if (j == m) {
        throw new MalformedObjectNameException("Invalid quote");
      }
      while (j < m)
      {
        c = paramArrayOfChar[j];
        if (c == '\\')
        {
          j++;
          if (j == m) {
            throw new MalformedObjectNameException("Invalid unterminated quoted character sequence");
          }
          c = paramArrayOfChar[j];
          switch (c)
          {
          case '*': 
          case '?': 
          case '\\': 
          case 'n': 
            break;
          case '"': 
            if (j + 1 != m) {
              break;
            }
            throw new MalformedObjectNameException("Missing termination quote");
          default: 
            throw new MalformedObjectNameException("Invalid quoted character sequence '\\" + c + "'");
          }
        }
        else
        {
          if (c == '\n') {
            throw new MalformedObjectNameException("Newline in quoted value");
          }
          if (c == '"')
          {
            j++;
            break;
          }
          switch (c)
          {
          case '*': 
          case '?': 
            i = 1;
          }
        }
        j++;
        if ((j >= m) && (c != '"')) {
          throw new MalformedObjectNameException("Missing termination quote");
        }
      }
      k = j;
      if ((j < m) && (paramArrayOfChar[(j++)] != ',')) {
        throw new MalformedObjectNameException("Invalid quote");
      }
    }
    else
    {
      while (j < m)
      {
        c = paramArrayOfChar[(j++)];
        switch (c)
        {
        case '*': 
        case '?': 
          i = 1;
          if (j >= m) {
            k = j;
          }
          break;
        case '\n': 
        case ':': 
        case '=': 
          String str = "" + c;
          throw new MalformedObjectNameException("Invalid character `" + str + "' in value");
        case ',': 
          k = j - 1;
          break;
        default: 
          if (j >= m) {
            k = j;
          }
          break;
        }
      }
    }
    return new int[] { k, i != 0 ? 1 : 0 };
  }
  
  private static boolean checkValue(String paramString)
    throws MalformedObjectNameException
  {
    if (paramString == null) {
      throw new NullPointerException("Invalid value (null)");
    }
    int i = paramString.length();
    if (i == 0) {
      return false;
    }
    char[] arrayOfChar = paramString.toCharArray();
    int[] arrayOfInt = parseValue(arrayOfChar, 0);
    int j = arrayOfInt[0];
    boolean bool = arrayOfInt[1] == 1;
    if (j < i) {
      throw new MalformedObjectNameException("Invalid character in value: `" + arrayOfChar[j] + "'");
    }
    return bool;
  }
  
  private static void checkKey(String paramString)
    throws MalformedObjectNameException
  {
    if (paramString == null) {
      throw new NullPointerException("Invalid key (null)");
    }
    int i = paramString.length();
    if (i == 0) {
      throw new MalformedObjectNameException("Invalid key (empty)");
    }
    char[] arrayOfChar = paramString.toCharArray();
    int j = parseKey(arrayOfChar, 0);
    if (j < i) {
      throw new MalformedObjectNameException("Invalid character in value: `" + arrayOfChar[j] + "'");
    }
  }
  
  private boolean isDomain(String paramString)
  {
    if (paramString == null) {
      return true;
    }
    int i = paramString.length();
    int j = 0;
    while (j < i)
    {
      int k = paramString.charAt(j++);
      switch (k)
      {
      case 10: 
      case 58: 
        return false;
      case 42: 
      case 63: 
        _domain_pattern = true;
      }
    }
    return true;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    String str1;
    if (compat)
    {
      ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
      String str2 = (String)localGetField.get("propertyListString", "");
      boolean bool = localGetField.get("propertyPattern", false);
      if (bool) {
        str2 = str2 + ",*";
      }
      str1 = (String)localGetField.get("domain", "default") + ":" + str2;
    }
    else
    {
      paramObjectInputStream.defaultReadObject();
      str1 = (String)paramObjectInputStream.readObject();
    }
    try
    {
      construct(str1);
    }
    catch (NullPointerException localNullPointerException)
    {
      throw new InvalidObjectException(localNullPointerException.toString());
    }
    catch (MalformedObjectNameException localMalformedObjectNameException)
    {
      throw new InvalidObjectException(localMalformedObjectNameException.toString());
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (compat)
    {
      ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
      localPutField.put("domain", _canonicalName.substring(0, _domain_length));
      localPutField.put("propertyList", getKeyPropertyList());
      localPutField.put("propertyListString", getKeyPropertyListString());
      localPutField.put("canonicalName", _canonicalName);
      localPutField.put("pattern", (_domain_pattern) || (_property_list_pattern));
      localPutField.put("propertyPattern", _property_list_pattern);
      paramObjectOutputStream.writeFields();
    }
    else
    {
      paramObjectOutputStream.defaultWriteObject();
      paramObjectOutputStream.writeObject(getSerializedNameString());
    }
  }
  
  public static ObjectName getInstance(String paramString)
    throws MalformedObjectNameException, NullPointerException
  {
    return new ObjectName(paramString);
  }
  
  public static ObjectName getInstance(String paramString1, String paramString2, String paramString3)
    throws MalformedObjectNameException
  {
    return new ObjectName(paramString1, paramString2, paramString3);
  }
  
  public static ObjectName getInstance(String paramString, Hashtable<String, String> paramHashtable)
    throws MalformedObjectNameException
  {
    return new ObjectName(paramString, paramHashtable);
  }
  
  public static ObjectName getInstance(ObjectName paramObjectName)
  {
    if (paramObjectName.getClass().equals(ObjectName.class)) {
      return paramObjectName;
    }
    return Util.newObjectName(paramObjectName.getSerializedNameString());
  }
  
  public ObjectName(String paramString)
    throws MalformedObjectNameException
  {
    construct(paramString);
  }
  
  public ObjectName(String paramString1, String paramString2, String paramString3)
    throws MalformedObjectNameException
  {
    Map localMap = Collections.singletonMap(paramString2, paramString3);
    construct(paramString1, localMap);
  }
  
  public ObjectName(String paramString, Hashtable<String, String> paramHashtable)
    throws MalformedObjectNameException
  {
    construct(paramString, paramHashtable);
  }
  
  public boolean isPattern()
  {
    return (_domain_pattern) || (_property_list_pattern) || (_property_value_pattern);
  }
  
  public boolean isDomainPattern()
  {
    return _domain_pattern;
  }
  
  public boolean isPropertyPattern()
  {
    return (_property_list_pattern) || (_property_value_pattern);
  }
  
  public boolean isPropertyListPattern()
  {
    return _property_list_pattern;
  }
  
  public boolean isPropertyValuePattern()
  {
    return _property_value_pattern;
  }
  
  public boolean isPropertyValuePattern(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("key property can't be null");
    }
    for (int i = 0; i < _ca_array.length; i++)
    {
      Property localProperty = _ca_array[i];
      String str = localProperty.getKeyString(_canonicalName);
      if (str.equals(paramString)) {
        return localProperty instanceof PatternProperty;
      }
    }
    throw new IllegalArgumentException("key property not found");
  }
  
  public String getCanonicalName()
  {
    return _canonicalName;
  }
  
  public String getDomain()
  {
    return _canonicalName.substring(0, _domain_length);
  }
  
  public String getKeyProperty(String paramString)
  {
    return (String)_getKeyPropertyList().get(paramString);
  }
  
  private Map<String, String> _getKeyPropertyList()
  {
    synchronized (this)
    {
      if (_propertyList == null)
      {
        _propertyList = new HashMap();
        int i = _ca_array.length;
        for (int j = i - 1; j >= 0; j--)
        {
          Property localProperty = _ca_array[j];
          _propertyList.put(localProperty.getKeyString(_canonicalName), localProperty.getValueString(_canonicalName));
        }
      }
    }
    return _propertyList;
  }
  
  public Hashtable<String, String> getKeyPropertyList()
  {
    return new Hashtable(_getKeyPropertyList());
  }
  
  public String getKeyPropertyListString()
  {
    if (_kp_array.length == 0) {
      return "";
    }
    int i = _canonicalName.length() - _domain_length - 1 - (_property_list_pattern ? 2 : 0);
    char[] arrayOfChar1 = new char[i];
    char[] arrayOfChar2 = _canonicalName.toCharArray();
    writeKeyPropertyListString(arrayOfChar2, arrayOfChar1, 0);
    return new String(arrayOfChar1);
  }
  
  private String getSerializedNameString()
  {
    int i = _canonicalName.length();
    char[] arrayOfChar1 = new char[i];
    char[] arrayOfChar2 = _canonicalName.toCharArray();
    int j = _domain_length + 1;
    System.arraycopy(arrayOfChar2, 0, arrayOfChar1, 0, j);
    int k = writeKeyPropertyListString(arrayOfChar2, arrayOfChar1, j);
    if (_property_list_pattern) {
      if (k == j)
      {
        arrayOfChar1[k] = '*';
      }
      else
      {
        arrayOfChar1[k] = ',';
        arrayOfChar1[(k + 1)] = '*';
      }
    }
    return new String(arrayOfChar1);
  }
  
  private int writeKeyPropertyListString(char[] paramArrayOfChar1, char[] paramArrayOfChar2, int paramInt)
  {
    if (_kp_array.length == 0) {
      return paramInt;
    }
    char[] arrayOfChar1 = paramArrayOfChar2;
    char[] arrayOfChar2 = paramArrayOfChar1;
    int i = paramInt;
    int j = _kp_array.length;
    int k = j - 1;
    for (int m = 0; m < j; m++)
    {
      Property localProperty = _kp_array[m];
      int n = _key_length + _value_length + 1;
      System.arraycopy(arrayOfChar2, _key_index, arrayOfChar1, i, n);
      i += n;
      if (m < k) {
        arrayOfChar1[(i++)] = ',';
      }
    }
    return i;
  }
  
  public String getCanonicalKeyPropertyListString()
  {
    if (_ca_array.length == 0) {
      return "";
    }
    int i = _canonicalName.length();
    if (_property_list_pattern) {
      i -= 2;
    }
    return _canonicalName.substring(_domain_length + 1, i);
  }
  
  public String toString()
  {
    return getSerializedNameString();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof ObjectName)) {
      return false;
    }
    ObjectName localObjectName = (ObjectName)paramObject;
    String str = _canonicalName;
    return _canonicalName == str;
  }
  
  public int hashCode()
  {
    return _canonicalName.hashCode();
  }
  
  public static String quote(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder("\"");
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      char c = paramString.charAt(j);
      switch (c)
      {
      case '\n': 
        c = 'n';
        localStringBuilder.append('\\');
        break;
      case '"': 
      case '*': 
      case '?': 
      case '\\': 
        localStringBuilder.append('\\');
      }
      localStringBuilder.append(c);
    }
    localStringBuilder.append('"');
    return localStringBuilder.toString();
  }
  
  public static String unquote(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = paramString.length();
    if ((i < 2) || (paramString.charAt(0) != '"') || (paramString.charAt(i - 1) != '"')) {
      throw new IllegalArgumentException("Argument not quoted");
    }
    for (int j = 1; j < i - 1; j++)
    {
      char c = paramString.charAt(j);
      if (c == '\\')
      {
        if (j == i - 2) {
          throw new IllegalArgumentException("Trailing backslash");
        }
        c = paramString.charAt(++j);
        switch (c)
        {
        case 'n': 
          c = '\n';
          break;
        case '"': 
        case '*': 
        case '?': 
        case '\\': 
          break;
        default: 
          throw new IllegalArgumentException("Bad character '" + c + "' after backslash");
        }
      }
      else
      {
        switch (c)
        {
        case '\n': 
        case '"': 
        case '*': 
        case '?': 
          throw new IllegalArgumentException("Invalid unescaped character '" + c + "' in the string to unquote");
        }
      }
      localStringBuilder.append(c);
    }
    return localStringBuilder.toString();
  }
  
  public boolean apply(ObjectName paramObjectName)
  {
    if (paramObjectName == null) {
      throw new NullPointerException();
    }
    if ((_domain_pattern) || (_property_list_pattern) || (_property_value_pattern)) {
      return false;
    }
    if ((!_domain_pattern) && (!_property_list_pattern) && (!_property_value_pattern)) {
      return _canonicalName.equals(_canonicalName);
    }
    return (matchDomains(paramObjectName)) && (matchKeys(paramObjectName));
  }
  
  private final boolean matchDomains(ObjectName paramObjectName)
  {
    if (_domain_pattern) {
      return Util.wildmatch(paramObjectName.getDomain(), getDomain());
    }
    return getDomain().equals(paramObjectName.getDomain());
  }
  
  private final boolean matchKeys(ObjectName paramObjectName)
  {
    if ((_property_value_pattern) && (!_property_list_pattern) && (_ca_array.length != _ca_array.length)) {
      return false;
    }
    if ((_property_value_pattern) || (_property_list_pattern))
    {
      localObject1 = paramObjectName._getKeyPropertyList();
      localObject2 = _ca_array;
      String str1 = _canonicalName;
      for (int i = localObject2.length - 1; i >= 0; i--)
      {
        Object localObject3 = localObject2[i];
        String str2 = ((Property)localObject3).getKeyString(str1);
        String str3 = (String)((Map)localObject1).get(str2);
        if (str3 == null) {
          return false;
        }
        if ((_property_value_pattern) && ((localObject3 instanceof PatternProperty)))
        {
          if (!Util.wildmatch(str3, ((Property)localObject3).getValueString(str1))) {
            return false;
          }
        }
        else if (!str3.equals(((Property)localObject3).getValueString(str1))) {
          return false;
        }
      }
      return true;
    }
    Object localObject1 = paramObjectName.getCanonicalKeyPropertyListString();
    Object localObject2 = getCanonicalKeyPropertyListString();
    return ((String)localObject1).equals(localObject2);
  }
  
  public void setMBeanServer(MBeanServer paramMBeanServer) {}
  
  public int compareTo(ObjectName paramObjectName)
  {
    if (paramObjectName == this) {
      return 0;
    }
    int i = getDomain().compareTo(paramObjectName.getDomain());
    if (i != 0) {
      return i;
    }
    String str1 = getKeyProperty("type");
    String str2 = paramObjectName.getKeyProperty("type");
    if (str1 == null) {
      str1 = "";
    }
    if (str2 == null) {
      str2 = "";
    }
    int j = str1.compareTo(str2);
    if (j != 0) {
      return j;
    }
    return getCanonicalName().compareTo(paramObjectName.getCanonicalName());
  }
  
  static
  {
    try
    {
      GetPropertyAction localGetPropertyAction = new GetPropertyAction("jmx.serial.form");
      String str = (String)AccessController.doPrivileged(localGetPropertyAction);
      compat = (str != null) && (str.equals("1.0"));
    }
    catch (Exception localException) {}
    if (compat)
    {
      serialPersistentFields = oldSerialPersistentFields;
      serialVersionUID = -5467795090068647408L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 1081892073854801359L;
    }
  }
  
  private static class PatternProperty
    extends ObjectName.Property
  {
    PatternProperty(int paramInt1, int paramInt2, int paramInt3)
    {
      super(paramInt2, paramInt3);
    }
  }
  
  private static class Property
  {
    int _key_index;
    int _key_length;
    int _value_length;
    
    Property(int paramInt1, int paramInt2, int paramInt3)
    {
      _key_index = paramInt1;
      _key_length = paramInt2;
      _value_length = paramInt3;
    }
    
    void setKeyIndex(int paramInt)
    {
      _key_index = paramInt;
    }
    
    String getKeyString(String paramString)
    {
      return paramString.substring(_key_index, _key_index + _key_length);
    }
    
    String getValueString(String paramString)
    {
      int i = _key_index + _key_length + 1;
      int j = i + _value_length;
      return paramString.substring(i, j);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\ObjectName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */