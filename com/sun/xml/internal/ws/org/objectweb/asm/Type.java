package com.sun.xml.internal.ws.org.objectweb.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Type
{
  public static final int VOID = 0;
  public static final int BOOLEAN = 1;
  public static final int CHAR = 2;
  public static final int BYTE = 3;
  public static final int SHORT = 4;
  public static final int INT = 5;
  public static final int FLOAT = 6;
  public static final int LONG = 7;
  public static final int DOUBLE = 8;
  public static final int ARRAY = 9;
  public static final int OBJECT = 10;
  public static final Type VOID_TYPE = new Type(0);
  public static final Type BOOLEAN_TYPE = new Type(1);
  public static final Type CHAR_TYPE = new Type(2);
  public static final Type BYTE_TYPE = new Type(3);
  public static final Type SHORT_TYPE = new Type(4);
  public static final Type INT_TYPE = new Type(5);
  public static final Type FLOAT_TYPE = new Type(6);
  public static final Type LONG_TYPE = new Type(7);
  public static final Type DOUBLE_TYPE = new Type(8);
  private final int sort;
  private final char[] buf;
  private final int off;
  private final int len;
  
  private Type(int paramInt)
  {
    this(paramInt, null, 0, 1);
  }
  
  private Type(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
  {
    sort = paramInt1;
    buf = paramArrayOfChar;
    off = paramInt2;
    len = paramInt3;
  }
  
  public static Type getType(String paramString)
  {
    return getType(paramString.toCharArray(), 0);
  }
  
  public static Type getObjectType(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    return new Type(arrayOfChar[0] == '[' ? 9 : 10, arrayOfChar, 0, arrayOfChar.length);
  }
  
  public static Type getType(Class paramClass)
  {
    if (paramClass.isPrimitive())
    {
      if (paramClass == Integer.TYPE) {
        return INT_TYPE;
      }
      if (paramClass == Void.TYPE) {
        return VOID_TYPE;
      }
      if (paramClass == Boolean.TYPE) {
        return BOOLEAN_TYPE;
      }
      if (paramClass == Byte.TYPE) {
        return BYTE_TYPE;
      }
      if (paramClass == Character.TYPE) {
        return CHAR_TYPE;
      }
      if (paramClass == Short.TYPE) {
        return SHORT_TYPE;
      }
      if (paramClass == Double.TYPE) {
        return DOUBLE_TYPE;
      }
      if (paramClass == Float.TYPE) {
        return FLOAT_TYPE;
      }
      return LONG_TYPE;
    }
    return getType(getDescriptor(paramClass));
  }
  
  public static Type[] getArgumentTypes(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    int i = 1;
    int j = 0;
    for (;;)
    {
      int k = arrayOfChar[(i++)];
      if (k == 41) {
        break;
      }
      if (k == 76)
      {
        while (arrayOfChar[(i++)] != ';') {}
        j++;
      }
      else if (k != 91)
      {
        j++;
      }
    }
    Type[] arrayOfType = new Type[j];
    i = 1;
    for (j = 0; arrayOfChar[i] != ')'; j++)
    {
      arrayOfType[j] = getType(arrayOfChar, i);
      i += len + (sort == 10 ? 2 : 0);
    }
    return arrayOfType;
  }
  
  public static Type[] getArgumentTypes(Method paramMethod)
  {
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    Type[] arrayOfType = new Type[arrayOfClass.length];
    for (int i = arrayOfClass.length - 1; i >= 0; i--) {
      arrayOfType[i] = getType(arrayOfClass[i]);
    }
    return arrayOfType;
  }
  
  public static Type getReturnType(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    return getType(arrayOfChar, paramString.indexOf(')') + 1);
  }
  
  public static Type getReturnType(Method paramMethod)
  {
    return getType(paramMethod.getReturnType());
  }
  
  private static Type getType(char[] paramArrayOfChar, int paramInt)
  {
    switch (paramArrayOfChar[paramInt])
    {
    case 'V': 
      return VOID_TYPE;
    case 'Z': 
      return BOOLEAN_TYPE;
    case 'C': 
      return CHAR_TYPE;
    case 'B': 
      return BYTE_TYPE;
    case 'S': 
      return SHORT_TYPE;
    case 'I': 
      return INT_TYPE;
    case 'F': 
      return FLOAT_TYPE;
    case 'J': 
      return LONG_TYPE;
    case 'D': 
      return DOUBLE_TYPE;
    case '[': 
      for (i = 1; paramArrayOfChar[(paramInt + i)] == '['; i++) {}
      if (paramArrayOfChar[(paramInt + i)] == 'L')
      {
        i++;
        while (paramArrayOfChar[(paramInt + i)] != ';') {
          i++;
        }
      }
      return new Type(9, paramArrayOfChar, paramInt, i + 1);
    }
    for (int i = 1; paramArrayOfChar[(paramInt + i)] != ';'; i++) {}
    return new Type(10, paramArrayOfChar, paramInt + 1, i - 1);
  }
  
  public int getSort()
  {
    return sort;
  }
  
  public int getDimensions()
  {
    for (int i = 1; buf[(off + i)] == '['; i++) {}
    return i;
  }
  
  public Type getElementType()
  {
    return getType(buf, off + getDimensions());
  }
  
  public String getClassName()
  {
    switch (sort)
    {
    case 0: 
      return "void";
    case 1: 
      return "boolean";
    case 2: 
      return "char";
    case 3: 
      return "byte";
    case 4: 
      return "short";
    case 5: 
      return "int";
    case 6: 
      return "float";
    case 7: 
      return "long";
    case 8: 
      return "double";
    case 9: 
      StringBuffer localStringBuffer = new StringBuffer(getElementType().getClassName());
      for (int i = getDimensions(); i > 0; i--) {
        localStringBuffer.append("[]");
      }
      return localStringBuffer.toString();
    }
    return new String(buf, off, len).replace('/', '.');
  }
  
  public String getInternalName()
  {
    return new String(buf, off, len);
  }
  
  public String getDescriptor()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    getDescriptor(localStringBuffer);
    return localStringBuffer.toString();
  }
  
  public static String getMethodDescriptor(Type paramType, Type[] paramArrayOfType)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append('(');
    for (int i = 0; i < paramArrayOfType.length; i++) {
      paramArrayOfType[i].getDescriptor(localStringBuffer);
    }
    localStringBuffer.append(')');
    paramType.getDescriptor(localStringBuffer);
    return localStringBuffer.toString();
  }
  
  private void getDescriptor(StringBuffer paramStringBuffer)
  {
    switch (sort)
    {
    case 0: 
      paramStringBuffer.append('V');
      return;
    case 1: 
      paramStringBuffer.append('Z');
      return;
    case 2: 
      paramStringBuffer.append('C');
      return;
    case 3: 
      paramStringBuffer.append('B');
      return;
    case 4: 
      paramStringBuffer.append('S');
      return;
    case 5: 
      paramStringBuffer.append('I');
      return;
    case 6: 
      paramStringBuffer.append('F');
      return;
    case 7: 
      paramStringBuffer.append('J');
      return;
    case 8: 
      paramStringBuffer.append('D');
      return;
    case 9: 
      paramStringBuffer.append(buf, off, len);
      return;
    }
    paramStringBuffer.append('L');
    paramStringBuffer.append(buf, off, len);
    paramStringBuffer.append(';');
  }
  
  public static String getInternalName(Class paramClass)
  {
    return paramClass.getName().replace('.', '/');
  }
  
  public static String getDescriptor(Class paramClass)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    getDescriptor(localStringBuffer, paramClass);
    return localStringBuffer.toString();
  }
  
  public static String getConstructorDescriptor(Constructor paramConstructor)
  {
    Class[] arrayOfClass = paramConstructor.getParameterTypes();
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append('(');
    for (int i = 0; i < arrayOfClass.length; i++) {
      getDescriptor(localStringBuffer, arrayOfClass[i]);
    }
    return ")V";
  }
  
  public static String getMethodDescriptor(Method paramMethod)
  {
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append('(');
    for (int i = 0; i < arrayOfClass.length; i++) {
      getDescriptor(localStringBuffer, arrayOfClass[i]);
    }
    localStringBuffer.append(')');
    getDescriptor(localStringBuffer, paramMethod.getReturnType());
    return localStringBuffer.toString();
  }
  
  private static void getDescriptor(StringBuffer paramStringBuffer, Class paramClass)
  {
    for (Class localClass = paramClass;; localClass = localClass.getComponentType())
    {
      if (localClass.isPrimitive())
      {
        char c1;
        if (localClass == Integer.TYPE) {
          c1 = 'I';
        } else if (localClass == Void.TYPE) {
          c1 = 'V';
        } else if (localClass == Boolean.TYPE) {
          c1 = 'Z';
        } else if (localClass == Byte.TYPE) {
          c1 = 'B';
        } else if (localClass == Character.TYPE) {
          c1 = 'C';
        } else if (localClass == Short.TYPE) {
          c1 = 'S';
        } else if (localClass == Double.TYPE) {
          c1 = 'D';
        } else if (localClass == Float.TYPE) {
          c1 = 'F';
        } else {
          c1 = 'J';
        }
        paramStringBuffer.append(c1);
        return;
      }
      if (!localClass.isArray()) {
        break;
      }
      paramStringBuffer.append('[');
    }
    paramStringBuffer.append('L');
    String str = localClass.getName();
    int i = str.length();
    for (int j = 0; j < i; j++)
    {
      char c2 = str.charAt(j);
      paramStringBuffer.append(c2 == '.' ? '/' : c2);
    }
    paramStringBuffer.append(';');
  }
  
  public int getSize()
  {
    return (sort == 7) || (sort == 8) ? 2 : 1;
  }
  
  public int getOpcode(int paramInt)
  {
    if ((paramInt == 46) || (paramInt == 79))
    {
      switch (sort)
      {
      case 1: 
      case 3: 
        return paramInt + 5;
      case 2: 
        return paramInt + 6;
      case 4: 
        return paramInt + 7;
      case 5: 
        return paramInt;
      case 6: 
        return paramInt + 2;
      case 7: 
        return paramInt + 1;
      case 8: 
        return paramInt + 3;
      }
      return paramInt + 4;
    }
    switch (sort)
    {
    case 0: 
      return paramInt + 5;
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
      return paramInt;
    case 6: 
      return paramInt + 2;
    case 7: 
      return paramInt + 1;
    case 8: 
      return paramInt + 3;
    }
    return paramInt + 4;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Type)) {
      return false;
    }
    Type localType = (Type)paramObject;
    if (sort != sort) {
      return false;
    }
    if ((sort == 10) || (sort == 9))
    {
      if (len != len) {
        return false;
      }
      int i = off;
      int j = off;
      int k = i + len;
      while (i < k)
      {
        if (buf[i] != buf[j]) {
          return false;
        }
        i++;
        j++;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 13 * sort;
    if ((sort == 10) || (sort == 9))
    {
      int j = off;
      int k = j + len;
      while (j < k)
      {
        i = 17 * (i + buf[j]);
        j++;
      }
    }
    return i;
  }
  
  public String toString()
  {
    return getDescriptor();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\Type.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */