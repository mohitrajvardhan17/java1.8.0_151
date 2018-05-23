package jdk.internal.org.objectweb.asm.commons;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import jdk.internal.org.objectweb.asm.Type;

public class Method
{
  private final String name;
  private final String desc;
  private static final Map<String, String> DESCRIPTORS = new HashMap();
  
  public Method(String paramString1, String paramString2)
  {
    name = paramString1;
    desc = paramString2;
  }
  
  public Method(String paramString, Type paramType, Type[] paramArrayOfType)
  {
    this(paramString, Type.getMethodDescriptor(paramType, paramArrayOfType));
  }
  
  public static Method getMethod(java.lang.reflect.Method paramMethod)
  {
    return new Method(paramMethod.getName(), Type.getMethodDescriptor(paramMethod));
  }
  
  public static Method getMethod(Constructor<?> paramConstructor)
  {
    return new Method("<init>", Type.getConstructorDescriptor(paramConstructor));
  }
  
  public static Method getMethod(String paramString)
    throws IllegalArgumentException
  {
    return getMethod(paramString, false);
  }
  
  public static Method getMethod(String paramString, boolean paramBoolean)
    throws IllegalArgumentException
  {
    int i = paramString.indexOf(' ');
    int j = paramString.indexOf('(', i) + 1;
    int k = paramString.indexOf(')', j);
    if ((i == -1) || (j == -1) || (k == -1)) {
      throw new IllegalArgumentException();
    }
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(i + 1, j - 1).trim();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('(');
    int m;
    do
    {
      m = paramString.indexOf(',', j);
      String str3;
      if (m == -1)
      {
        str3 = map(paramString.substring(j, k).trim(), paramBoolean);
      }
      else
      {
        str3 = map(paramString.substring(j, m).trim(), paramBoolean);
        j = m + 1;
      }
      localStringBuilder.append(str3);
    } while (m != -1);
    localStringBuilder.append(')');
    localStringBuilder.append(map(str1, paramBoolean));
    return new Method(str2, localStringBuilder.toString());
  }
  
  private static String map(String paramString, boolean paramBoolean)
  {
    if ("".equals(paramString)) {
      return paramString;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    while ((i = paramString.indexOf("[]", i) + 1) > 0) {
      localStringBuilder.append('[');
    }
    String str1 = paramString.substring(0, paramString.length() - localStringBuilder.length() * 2);
    String str2 = (String)DESCRIPTORS.get(str1);
    if (str2 != null)
    {
      localStringBuilder.append(str2);
    }
    else
    {
      localStringBuilder.append('L');
      if (str1.indexOf('.') < 0)
      {
        if (!paramBoolean) {
          localStringBuilder.append("java/lang/");
        }
        localStringBuilder.append(str1);
      }
      else
      {
        localStringBuilder.append(str1.replace('.', '/'));
      }
      localStringBuilder.append(';');
    }
    return localStringBuilder.toString();
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getDescriptor()
  {
    return desc;
  }
  
  public Type getReturnType()
  {
    return Type.getReturnType(desc);
  }
  
  public Type[] getArgumentTypes()
  {
    return Type.getArgumentTypes(desc);
  }
  
  public String toString()
  {
    return name + desc;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Method)) {
      return false;
    }
    Method localMethod = (Method)paramObject;
    return (name.equals(name)) && (desc.equals(desc));
  }
  
  public int hashCode()
  {
    return name.hashCode() ^ desc.hashCode();
  }
  
  static
  {
    DESCRIPTORS.put("void", "V");
    DESCRIPTORS.put("byte", "B");
    DESCRIPTORS.put("char", "C");
    DESCRIPTORS.put("double", "D");
    DESCRIPTORS.put("float", "F");
    DESCRIPTORS.put("int", "I");
    DESCRIPTORS.put("long", "J");
    DESCRIPTORS.put("short", "S");
    DESCRIPTORS.put("boolean", "Z");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\commons\Method.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */