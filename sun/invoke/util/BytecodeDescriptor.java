package sun.invoke.util;

import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BytecodeDescriptor
{
  private BytecodeDescriptor() {}
  
  public static List<Class<?>> parseMethod(String paramString, ClassLoader paramClassLoader)
  {
    return parseMethod(paramString, 0, paramString.length(), paramClassLoader);
  }
  
  static List<Class<?>> parseMethod(String paramString, int paramInt1, int paramInt2, ClassLoader paramClassLoader)
  {
    if (paramClassLoader == null) {
      paramClassLoader = ClassLoader.getSystemClassLoader();
    }
    String str = paramString;
    int[] arrayOfInt = { paramInt1 };
    ArrayList localArrayList = new ArrayList();
    if ((arrayOfInt[0] < paramInt2) && (str.charAt(arrayOfInt[0]) == '('))
    {
      arrayOfInt[0] += 1;
      while ((arrayOfInt[0] < paramInt2) && (str.charAt(arrayOfInt[0]) != ')'))
      {
        localClass = parseSig(str, arrayOfInt, paramInt2, paramClassLoader);
        if ((localClass == null) || (localClass == Void.TYPE)) {
          parseError(str, "bad argument type");
        }
        localArrayList.add(localClass);
      }
      arrayOfInt[0] += 1;
    }
    else
    {
      parseError(str, "not a method type");
    }
    Class localClass = parseSig(str, arrayOfInt, paramInt2, paramClassLoader);
    if ((localClass == null) || (arrayOfInt[0] != paramInt2)) {
      parseError(str, "bad return type");
    }
    localArrayList.add(localClass);
    return localArrayList;
  }
  
  private static void parseError(String paramString1, String paramString2)
  {
    throw new IllegalArgumentException("bad signature: " + paramString1 + ": " + paramString2);
  }
  
  private static Class<?> parseSig(String paramString, int[] paramArrayOfInt, int paramInt, ClassLoader paramClassLoader)
  {
    if (paramArrayOfInt[0] == paramInt) {
      return null;
    }
    int tmp12_11 = 0;
    int[] tmp12_10 = paramArrayOfInt;
    int tmp14_13 = tmp12_10[tmp12_11];
    tmp12_10[tmp12_11] = (tmp14_13 + 1);
    char c = paramString.charAt(tmp14_13);
    if (c == 'L')
    {
      int i = paramArrayOfInt[0];
      int j = paramString.indexOf(';', i);
      if (j < 0) {
        return null;
      }
      paramArrayOfInt[0] = (j + 1);
      String str = paramString.substring(i, j).replace('/', '.');
      try
      {
        return paramClassLoader.loadClass(str);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new TypeNotPresentException(str, localClassNotFoundException);
      }
    }
    if (c == '[')
    {
      Class localClass = parseSig(paramString, paramArrayOfInt, paramInt, paramClassLoader);
      if (localClass != null) {
        localClass = Array.newInstance(localClass, 0).getClass();
      }
      return localClass;
    }
    return Wrapper.forBasicType(c).primitiveType();
  }
  
  public static String unparse(Class<?> paramClass)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    unparseSig(paramClass, localStringBuilder);
    return localStringBuilder.toString();
  }
  
  public static String unparse(MethodType paramMethodType)
  {
    return unparseMethod(paramMethodType.returnType(), paramMethodType.parameterList());
  }
  
  public static String unparse(Object paramObject)
  {
    if ((paramObject instanceof Class)) {
      return unparse((Class)paramObject);
    }
    if ((paramObject instanceof MethodType)) {
      return unparse((MethodType)paramObject);
    }
    return (String)paramObject;
  }
  
  public static String unparseMethod(Class<?> paramClass, List<Class<?>> paramList)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('(');
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      unparseSig(localClass, localStringBuilder);
    }
    localStringBuilder.append(')');
    unparseSig(paramClass, localStringBuilder);
    return localStringBuilder.toString();
  }
  
  private static void unparseSig(Class<?> paramClass, StringBuilder paramStringBuilder)
  {
    char c = Wrapper.forBasicType(paramClass).basicTypeChar();
    if (c != 'L')
    {
      paramStringBuilder.append(c);
    }
    else
    {
      int i = !paramClass.isArray() ? 1 : 0;
      if (i != 0) {
        paramStringBuilder.append('L');
      }
      paramStringBuilder.append(paramClass.getName().replace('.', '/'));
      if (i != 0) {
        paramStringBuilder.append(';');
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\invoke\util\BytecodeDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */