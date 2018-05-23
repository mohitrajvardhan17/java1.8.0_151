package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.Permission;

public class MBeanPermission
  extends Permission
{
  private static final long serialVersionUID = -2416928705275160661L;
  private static final int AddNotificationListener = 1;
  private static final int GetAttribute = 2;
  private static final int GetClassLoader = 4;
  private static final int GetClassLoaderFor = 8;
  private static final int GetClassLoaderRepository = 16;
  private static final int GetDomains = 32;
  private static final int GetMBeanInfo = 64;
  private static final int GetObjectInstance = 128;
  private static final int Instantiate = 256;
  private static final int Invoke = 512;
  private static final int IsInstanceOf = 1024;
  private static final int QueryMBeans = 2048;
  private static final int QueryNames = 4096;
  private static final int RegisterMBean = 8192;
  private static final int RemoveNotificationListener = 16384;
  private static final int SetAttribute = 32768;
  private static final int UnregisterMBean = 65536;
  private static final int NONE = 0;
  private static final int ALL = 131071;
  private String actions;
  private transient int mask;
  private transient String classNamePrefix;
  private transient boolean classNameExactMatch;
  private transient String member;
  private transient ObjectName objectName;
  
  private void parseActions()
  {
    if (actions == null) {
      throw new IllegalArgumentException("MBeanPermission: actions can't be null");
    }
    if (actions.equals("")) {
      throw new IllegalArgumentException("MBeanPermission: actions can't be empty");
    }
    int i = getMask(actions);
    if ((i & 0x1FFFF) != i) {
      throw new IllegalArgumentException("Invalid actions mask");
    }
    if (i == 0) {
      throw new IllegalArgumentException("Invalid actions mask");
    }
    mask = i;
  }
  
  private void parseName()
  {
    String str1 = getName();
    if (str1 == null) {
      throw new IllegalArgumentException("MBeanPermission name cannot be null");
    }
    if (str1.equals("")) {
      throw new IllegalArgumentException("MBeanPermission name cannot be empty");
    }
    int i = str1.indexOf("[");
    if (i == -1)
    {
      objectName = ObjectName.WILDCARD;
    }
    else
    {
      if (!str1.endsWith("]")) {
        throw new IllegalArgumentException("MBeanPermission: The ObjectName in the target name must be included in square brackets");
      }
      try
      {
        String str2 = str1.substring(i + 1, str1.length() - 1);
        if (str2.equals("")) {
          objectName = ObjectName.WILDCARD;
        } else if (str2.equals("-")) {
          objectName = null;
        } else {
          objectName = new ObjectName(str2);
        }
      }
      catch (MalformedObjectNameException localMalformedObjectNameException)
      {
        throw new IllegalArgumentException("MBeanPermission: The target name does not specify a valid ObjectName", localMalformedObjectNameException);
      }
      str1 = str1.substring(0, i);
    }
    int j = str1.indexOf("#");
    if (j == -1)
    {
      setMember("*");
    }
    else
    {
      String str3 = str1.substring(j + 1);
      setMember(str3);
      str1 = str1.substring(0, j);
    }
    setClassName(str1);
  }
  
  private void initName(String paramString1, String paramString2, ObjectName paramObjectName)
  {
    setClassName(paramString1);
    setMember(paramString2);
    objectName = paramObjectName;
  }
  
  private void setClassName(String paramString)
  {
    if ((paramString == null) || (paramString.equals("-")))
    {
      classNamePrefix = null;
      classNameExactMatch = false;
    }
    else if ((paramString.equals("")) || (paramString.equals("*")))
    {
      classNamePrefix = "";
      classNameExactMatch = false;
    }
    else if (paramString.endsWith(".*"))
    {
      classNamePrefix = paramString.substring(0, paramString.length() - 1);
      classNameExactMatch = false;
    }
    else
    {
      classNamePrefix = paramString;
      classNameExactMatch = true;
    }
  }
  
  private void setMember(String paramString)
  {
    if ((paramString == null) || (paramString.equals("-"))) {
      member = null;
    } else if (paramString.equals("")) {
      member = "*";
    } else {
      member = paramString;
    }
  }
  
  public MBeanPermission(String paramString1, String paramString2)
  {
    super(paramString1);
    parseName();
    actions = paramString2;
    parseActions();
  }
  
  public MBeanPermission(String paramString1, String paramString2, ObjectName paramObjectName, String paramString3)
  {
    super(makeName(paramString1, paramString2, paramObjectName));
    initName(paramString1, paramString2, paramObjectName);
    actions = paramString3;
    parseActions();
  }
  
  private static String makeName(String paramString1, String paramString2, ObjectName paramObjectName)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramString1 == null) {
      paramString1 = "-";
    }
    localStringBuilder.append(paramString1);
    if (paramString2 == null) {
      paramString2 = "-";
    }
    localStringBuilder.append("#" + paramString2);
    if (paramObjectName == null) {
      localStringBuilder.append("[-]");
    } else {
      localStringBuilder.append("[").append(paramObjectName.getCanonicalName()).append("]");
    }
    if (localStringBuilder.length() == 0) {
      return "*";
    }
    return localStringBuilder.toString();
  }
  
  public String getActions()
  {
    if (actions == null) {
      actions = getActions(mask);
    }
    return actions;
  }
  
  private static String getActions(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    if ((paramInt & 0x1) == 1)
    {
      i = 1;
      localStringBuilder.append("addNotificationListener");
    }
    if ((paramInt & 0x2) == 2)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("getAttribute");
    }
    if ((paramInt & 0x4) == 4)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("getClassLoader");
    }
    if ((paramInt & 0x8) == 8)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("getClassLoaderFor");
    }
    if ((paramInt & 0x10) == 16)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("getClassLoaderRepository");
    }
    if ((paramInt & 0x20) == 32)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("getDomains");
    }
    if ((paramInt & 0x40) == 64)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("getMBeanInfo");
    }
    if ((paramInt & 0x80) == 128)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("getObjectInstance");
    }
    if ((paramInt & 0x100) == 256)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("instantiate");
    }
    if ((paramInt & 0x200) == 512)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("invoke");
    }
    if ((paramInt & 0x400) == 1024)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("isInstanceOf");
    }
    if ((paramInt & 0x800) == 2048)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("queryMBeans");
    }
    if ((paramInt & 0x1000) == 4096)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("queryNames");
    }
    if ((paramInt & 0x2000) == 8192)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("registerMBean");
    }
    if ((paramInt & 0x4000) == 16384)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("removeNotificationListener");
    }
    if ((paramInt & 0x8000) == 32768)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("setAttribute");
    }
    if ((paramInt & 0x10000) == 65536)
    {
      if (i != 0) {
        localStringBuilder.append(',');
      } else {
        i = 1;
      }
      localStringBuilder.append("unregisterMBean");
    }
    return localStringBuilder.toString();
  }
  
  public int hashCode()
  {
    return getName().hashCode() + getActions().hashCode();
  }
  
  private static int getMask(String paramString)
  {
    int i = 0;
    if (paramString == null) {
      return i;
    }
    if (paramString.equals("*")) {
      return 131071;
    }
    char[] arrayOfChar = paramString.toCharArray();
    int j = arrayOfChar.length - 1;
    if (j < 0) {
      return i;
    }
    while (j != -1)
    {
      int k;
      while ((j != -1) && (((k = arrayOfChar[j]) == ' ') || (k == 13) || (k == 10) || (k == 12) || (k == 9))) {
        j--;
      }
      int m;
      if ((j >= 25) && (arrayOfChar[(j - 25)] == 'r') && (arrayOfChar[(j - 24)] == 'e') && (arrayOfChar[(j - 23)] == 'm') && (arrayOfChar[(j - 22)] == 'o') && (arrayOfChar[(j - 21)] == 'v') && (arrayOfChar[(j - 20)] == 'e') && (arrayOfChar[(j - 19)] == 'N') && (arrayOfChar[(j - 18)] == 'o') && (arrayOfChar[(j - 17)] == 't') && (arrayOfChar[(j - 16)] == 'i') && (arrayOfChar[(j - 15)] == 'f') && (arrayOfChar[(j - 14)] == 'i') && (arrayOfChar[(j - 13)] == 'c') && (arrayOfChar[(j - 12)] == 'a') && (arrayOfChar[(j - 11)] == 't') && (arrayOfChar[(j - 10)] == 'i') && (arrayOfChar[(j - 9)] == 'o') && (arrayOfChar[(j - 8)] == 'n') && (arrayOfChar[(j - 7)] == 'L') && (arrayOfChar[(j - 6)] == 'i') && (arrayOfChar[(j - 5)] == 's') && (arrayOfChar[(j - 4)] == 't') && (arrayOfChar[(j - 3)] == 'e') && (arrayOfChar[(j - 2)] == 'n') && (arrayOfChar[(j - 1)] == 'e') && (arrayOfChar[j] == 'r'))
      {
        m = 26;
        i |= 0x4000;
      }
      else if ((j >= 23) && (arrayOfChar[(j - 23)] == 'g') && (arrayOfChar[(j - 22)] == 'e') && (arrayOfChar[(j - 21)] == 't') && (arrayOfChar[(j - 20)] == 'C') && (arrayOfChar[(j - 19)] == 'l') && (arrayOfChar[(j - 18)] == 'a') && (arrayOfChar[(j - 17)] == 's') && (arrayOfChar[(j - 16)] == 's') && (arrayOfChar[(j - 15)] == 'L') && (arrayOfChar[(j - 14)] == 'o') && (arrayOfChar[(j - 13)] == 'a') && (arrayOfChar[(j - 12)] == 'd') && (arrayOfChar[(j - 11)] == 'e') && (arrayOfChar[(j - 10)] == 'r') && (arrayOfChar[(j - 9)] == 'R') && (arrayOfChar[(j - 8)] == 'e') && (arrayOfChar[(j - 7)] == 'p') && (arrayOfChar[(j - 6)] == 'o') && (arrayOfChar[(j - 5)] == 's') && (arrayOfChar[(j - 4)] == 'i') && (arrayOfChar[(j - 3)] == 't') && (arrayOfChar[(j - 2)] == 'o') && (arrayOfChar[(j - 1)] == 'r') && (arrayOfChar[j] == 'y'))
      {
        m = 24;
        i |= 0x10;
      }
      else if ((j >= 22) && (arrayOfChar[(j - 22)] == 'a') && (arrayOfChar[(j - 21)] == 'd') && (arrayOfChar[(j - 20)] == 'd') && (arrayOfChar[(j - 19)] == 'N') && (arrayOfChar[(j - 18)] == 'o') && (arrayOfChar[(j - 17)] == 't') && (arrayOfChar[(j - 16)] == 'i') && (arrayOfChar[(j - 15)] == 'f') && (arrayOfChar[(j - 14)] == 'i') && (arrayOfChar[(j - 13)] == 'c') && (arrayOfChar[(j - 12)] == 'a') && (arrayOfChar[(j - 11)] == 't') && (arrayOfChar[(j - 10)] == 'i') && (arrayOfChar[(j - 9)] == 'o') && (arrayOfChar[(j - 8)] == 'n') && (arrayOfChar[(j - 7)] == 'L') && (arrayOfChar[(j - 6)] == 'i') && (arrayOfChar[(j - 5)] == 's') && (arrayOfChar[(j - 4)] == 't') && (arrayOfChar[(j - 3)] == 'e') && (arrayOfChar[(j - 2)] == 'n') && (arrayOfChar[(j - 1)] == 'e') && (arrayOfChar[j] == 'r'))
      {
        m = 23;
        i |= 0x1;
      }
      else if ((j >= 16) && (arrayOfChar[(j - 16)] == 'g') && (arrayOfChar[(j - 15)] == 'e') && (arrayOfChar[(j - 14)] == 't') && (arrayOfChar[(j - 13)] == 'C') && (arrayOfChar[(j - 12)] == 'l') && (arrayOfChar[(j - 11)] == 'a') && (arrayOfChar[(j - 10)] == 's') && (arrayOfChar[(j - 9)] == 's') && (arrayOfChar[(j - 8)] == 'L') && (arrayOfChar[(j - 7)] == 'o') && (arrayOfChar[(j - 6)] == 'a') && (arrayOfChar[(j - 5)] == 'd') && (arrayOfChar[(j - 4)] == 'e') && (arrayOfChar[(j - 3)] == 'r') && (arrayOfChar[(j - 2)] == 'F') && (arrayOfChar[(j - 1)] == 'o') && (arrayOfChar[j] == 'r'))
      {
        m = 17;
        i |= 0x8;
      }
      else if ((j >= 16) && (arrayOfChar[(j - 16)] == 'g') && (arrayOfChar[(j - 15)] == 'e') && (arrayOfChar[(j - 14)] == 't') && (arrayOfChar[(j - 13)] == 'O') && (arrayOfChar[(j - 12)] == 'b') && (arrayOfChar[(j - 11)] == 'j') && (arrayOfChar[(j - 10)] == 'e') && (arrayOfChar[(j - 9)] == 'c') && (arrayOfChar[(j - 8)] == 't') && (arrayOfChar[(j - 7)] == 'I') && (arrayOfChar[(j - 6)] == 'n') && (arrayOfChar[(j - 5)] == 's') && (arrayOfChar[(j - 4)] == 't') && (arrayOfChar[(j - 3)] == 'a') && (arrayOfChar[(j - 2)] == 'n') && (arrayOfChar[(j - 1)] == 'c') && (arrayOfChar[j] == 'e'))
      {
        m = 17;
        i |= 0x80;
      }
      else if ((j >= 14) && (arrayOfChar[(j - 14)] == 'u') && (arrayOfChar[(j - 13)] == 'n') && (arrayOfChar[(j - 12)] == 'r') && (arrayOfChar[(j - 11)] == 'e') && (arrayOfChar[(j - 10)] == 'g') && (arrayOfChar[(j - 9)] == 'i') && (arrayOfChar[(j - 8)] == 's') && (arrayOfChar[(j - 7)] == 't') && (arrayOfChar[(j - 6)] == 'e') && (arrayOfChar[(j - 5)] == 'r') && (arrayOfChar[(j - 4)] == 'M') && (arrayOfChar[(j - 3)] == 'B') && (arrayOfChar[(j - 2)] == 'e') && (arrayOfChar[(j - 1)] == 'a') && (arrayOfChar[j] == 'n'))
      {
        m = 15;
        i |= 0x10000;
      }
      else if ((j >= 13) && (arrayOfChar[(j - 13)] == 'g') && (arrayOfChar[(j - 12)] == 'e') && (arrayOfChar[(j - 11)] == 't') && (arrayOfChar[(j - 10)] == 'C') && (arrayOfChar[(j - 9)] == 'l') && (arrayOfChar[(j - 8)] == 'a') && (arrayOfChar[(j - 7)] == 's') && (arrayOfChar[(j - 6)] == 's') && (arrayOfChar[(j - 5)] == 'L') && (arrayOfChar[(j - 4)] == 'o') && (arrayOfChar[(j - 3)] == 'a') && (arrayOfChar[(j - 2)] == 'd') && (arrayOfChar[(j - 1)] == 'e') && (arrayOfChar[j] == 'r'))
      {
        m = 14;
        i |= 0x4;
      }
      else if ((j >= 12) && (arrayOfChar[(j - 12)] == 'r') && (arrayOfChar[(j - 11)] == 'e') && (arrayOfChar[(j - 10)] == 'g') && (arrayOfChar[(j - 9)] == 'i') && (arrayOfChar[(j - 8)] == 's') && (arrayOfChar[(j - 7)] == 't') && (arrayOfChar[(j - 6)] == 'e') && (arrayOfChar[(j - 5)] == 'r') && (arrayOfChar[(j - 4)] == 'M') && (arrayOfChar[(j - 3)] == 'B') && (arrayOfChar[(j - 2)] == 'e') && (arrayOfChar[(j - 1)] == 'a') && (arrayOfChar[j] == 'n'))
      {
        m = 13;
        i |= 0x2000;
      }
      else if ((j >= 11) && (arrayOfChar[(j - 11)] == 'g') && (arrayOfChar[(j - 10)] == 'e') && (arrayOfChar[(j - 9)] == 't') && (arrayOfChar[(j - 8)] == 'A') && (arrayOfChar[(j - 7)] == 't') && (arrayOfChar[(j - 6)] == 't') && (arrayOfChar[(j - 5)] == 'r') && (arrayOfChar[(j - 4)] == 'i') && (arrayOfChar[(j - 3)] == 'b') && (arrayOfChar[(j - 2)] == 'u') && (arrayOfChar[(j - 1)] == 't') && (arrayOfChar[j] == 'e'))
      {
        m = 12;
        i |= 0x2;
      }
      else if ((j >= 11) && (arrayOfChar[(j - 11)] == 'g') && (arrayOfChar[(j - 10)] == 'e') && (arrayOfChar[(j - 9)] == 't') && (arrayOfChar[(j - 8)] == 'M') && (arrayOfChar[(j - 7)] == 'B') && (arrayOfChar[(j - 6)] == 'e') && (arrayOfChar[(j - 5)] == 'a') && (arrayOfChar[(j - 4)] == 'n') && (arrayOfChar[(j - 3)] == 'I') && (arrayOfChar[(j - 2)] == 'n') && (arrayOfChar[(j - 1)] == 'f') && (arrayOfChar[j] == 'o'))
      {
        m = 12;
        i |= 0x40;
      }
      else if ((j >= 11) && (arrayOfChar[(j - 11)] == 'i') && (arrayOfChar[(j - 10)] == 's') && (arrayOfChar[(j - 9)] == 'I') && (arrayOfChar[(j - 8)] == 'n') && (arrayOfChar[(j - 7)] == 's') && (arrayOfChar[(j - 6)] == 't') && (arrayOfChar[(j - 5)] == 'a') && (arrayOfChar[(j - 4)] == 'n') && (arrayOfChar[(j - 3)] == 'c') && (arrayOfChar[(j - 2)] == 'e') && (arrayOfChar[(j - 1)] == 'O') && (arrayOfChar[j] == 'f'))
      {
        m = 12;
        i |= 0x400;
      }
      else if ((j >= 11) && (arrayOfChar[(j - 11)] == 's') && (arrayOfChar[(j - 10)] == 'e') && (arrayOfChar[(j - 9)] == 't') && (arrayOfChar[(j - 8)] == 'A') && (arrayOfChar[(j - 7)] == 't') && (arrayOfChar[(j - 6)] == 't') && (arrayOfChar[(j - 5)] == 'r') && (arrayOfChar[(j - 4)] == 'i') && (arrayOfChar[(j - 3)] == 'b') && (arrayOfChar[(j - 2)] == 'u') && (arrayOfChar[(j - 1)] == 't') && (arrayOfChar[j] == 'e'))
      {
        m = 12;
        i |= 0x8000;
      }
      else if ((j >= 10) && (arrayOfChar[(j - 10)] == 'i') && (arrayOfChar[(j - 9)] == 'n') && (arrayOfChar[(j - 8)] == 's') && (arrayOfChar[(j - 7)] == 't') && (arrayOfChar[(j - 6)] == 'a') && (arrayOfChar[(j - 5)] == 'n') && (arrayOfChar[(j - 4)] == 't') && (arrayOfChar[(j - 3)] == 'i') && (arrayOfChar[(j - 2)] == 'a') && (arrayOfChar[(j - 1)] == 't') && (arrayOfChar[j] == 'e'))
      {
        m = 11;
        i |= 0x100;
      }
      else if ((j >= 10) && (arrayOfChar[(j - 10)] == 'q') && (arrayOfChar[(j - 9)] == 'u') && (arrayOfChar[(j - 8)] == 'e') && (arrayOfChar[(j - 7)] == 'r') && (arrayOfChar[(j - 6)] == 'y') && (arrayOfChar[(j - 5)] == 'M') && (arrayOfChar[(j - 4)] == 'B') && (arrayOfChar[(j - 3)] == 'e') && (arrayOfChar[(j - 2)] == 'a') && (arrayOfChar[(j - 1)] == 'n') && (arrayOfChar[j] == 's'))
      {
        m = 11;
        i |= 0x800;
      }
      else if ((j >= 9) && (arrayOfChar[(j - 9)] == 'g') && (arrayOfChar[(j - 8)] == 'e') && (arrayOfChar[(j - 7)] == 't') && (arrayOfChar[(j - 6)] == 'D') && (arrayOfChar[(j - 5)] == 'o') && (arrayOfChar[(j - 4)] == 'm') && (arrayOfChar[(j - 3)] == 'a') && (arrayOfChar[(j - 2)] == 'i') && (arrayOfChar[(j - 1)] == 'n') && (arrayOfChar[j] == 's'))
      {
        m = 10;
        i |= 0x20;
      }
      else if ((j >= 9) && (arrayOfChar[(j - 9)] == 'q') && (arrayOfChar[(j - 8)] == 'u') && (arrayOfChar[(j - 7)] == 'e') && (arrayOfChar[(j - 6)] == 'r') && (arrayOfChar[(j - 5)] == 'y') && (arrayOfChar[(j - 4)] == 'N') && (arrayOfChar[(j - 3)] == 'a') && (arrayOfChar[(j - 2)] == 'm') && (arrayOfChar[(j - 1)] == 'e') && (arrayOfChar[j] == 's'))
      {
        m = 10;
        i |= 0x1000;
      }
      else if ((j >= 5) && (arrayOfChar[(j - 5)] == 'i') && (arrayOfChar[(j - 4)] == 'n') && (arrayOfChar[(j - 3)] == 'v') && (arrayOfChar[(j - 2)] == 'o') && (arrayOfChar[(j - 1)] == 'k') && (arrayOfChar[j] == 'e'))
      {
        m = 6;
        i |= 0x200;
      }
      else
      {
        throw new IllegalArgumentException("Invalid permission: " + paramString);
      }
      int n = 0;
      while ((j >= m) && (n == 0))
      {
        switch (arrayOfChar[(j - m)])
        {
        case ',': 
          n = 1;
          break;
        case '\t': 
        case '\n': 
        case '\f': 
        case '\r': 
        case ' ': 
          break;
        default: 
          throw new IllegalArgumentException("Invalid permission: " + paramString);
        }
        j--;
      }
      j -= m;
    }
    return i;
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof MBeanPermission)) {
      return false;
    }
    MBeanPermission localMBeanPermission = (MBeanPermission)paramPermission;
    if ((mask & 0x800) == 2048)
    {
      if (((mask | 0x1000) & mask) != mask) {
        return false;
      }
    }
    else if ((mask & mask) != mask) {
      return false;
    }
    if (classNamePrefix != null)
    {
      if (classNamePrefix == null) {
        return false;
      }
      if (classNameExactMatch)
      {
        if (!classNameExactMatch) {
          return false;
        }
        if (!classNamePrefix.equals(classNamePrefix)) {
          return false;
        }
      }
      else if (!classNamePrefix.startsWith(classNamePrefix))
      {
        return false;
      }
    }
    if (member != null)
    {
      if (member == null) {
        return false;
      }
      if ((!member.equals("*")) && (!member.equals(member))) {
        return false;
      }
    }
    if (objectName != null)
    {
      if (objectName == null) {
        return false;
      }
      if ((!objectName.apply(objectName)) && (!objectName.equals(objectName))) {
        return false;
      }
    }
    return true;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof MBeanPermission)) {
      return false;
    }
    MBeanPermission localMBeanPermission = (MBeanPermission)paramObject;
    return (mask == mask) && (getName().equals(localMBeanPermission.getName()));
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    parseName();
    parseActions();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\MBeanPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */