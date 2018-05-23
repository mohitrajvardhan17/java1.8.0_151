package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.ClassFormatException;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class Type
  implements Serializable
{
  protected byte type;
  protected String signature;
  public static final BasicType VOID = new BasicType((byte)12);
  public static final BasicType BOOLEAN = new BasicType((byte)4);
  public static final BasicType INT = new BasicType((byte)10);
  public static final BasicType SHORT = new BasicType((byte)9);
  public static final BasicType BYTE = new BasicType((byte)8);
  public static final BasicType LONG = new BasicType((byte)11);
  public static final BasicType DOUBLE = new BasicType((byte)7);
  public static final BasicType FLOAT = new BasicType((byte)6);
  public static final BasicType CHAR = new BasicType((byte)5);
  public static final ObjectType OBJECT = new ObjectType("java.lang.Object");
  public static final ObjectType STRING = new ObjectType("java.lang.String");
  public static final ObjectType STRINGBUFFER = new ObjectType("java.lang.StringBuffer");
  public static final ObjectType THROWABLE = new ObjectType("java.lang.Throwable");
  public static final Type[] NO_ARGS = new Type[0];
  public static final ReferenceType NULL = new ReferenceType() {};
  public static final Type UNKNOWN = new Type((byte)15, "<unknown object>") {};
  private static int consumed_chars = 0;
  
  protected Type(byte paramByte, String paramString)
  {
    type = paramByte;
    signature = paramString;
  }
  
  public String getSignature()
  {
    return signature;
  }
  
  public byte getType()
  {
    return type;
  }
  
  public int getSize()
  {
    switch (type)
    {
    case 7: 
    case 11: 
      return 2;
    case 12: 
      return 0;
    }
    return 1;
  }
  
  public String toString()
  {
    return (equals(NULL)) || (type >= 15) ? signature : Utility.signatureToString(signature, false);
  }
  
  public static String getMethodSignature(Type paramType, Type[] paramArrayOfType)
  {
    StringBuffer localStringBuffer = new StringBuffer("(");
    int i = paramArrayOfType == null ? 0 : paramArrayOfType.length;
    for (int j = 0; j < i; j++) {
      localStringBuffer.append(paramArrayOfType[j].getSignature());
    }
    localStringBuffer.append(')');
    localStringBuffer.append(paramType.getSignature());
    return localStringBuffer.toString();
  }
  
  public static final Type getType(String paramString)
    throws StringIndexOutOfBoundsException
  {
    byte b = Utility.typeOfSignature(paramString);
    if (b <= 12)
    {
      consumed_chars = 1;
      return BasicType.getType(b);
    }
    if (b == 13)
    {
      i = 0;
      do
      {
        i++;
      } while (paramString.charAt(i) == '[');
      Type localType = getType(paramString.substring(i));
      consumed_chars += i;
      return new ArrayType(localType, i);
    }
    int i = paramString.indexOf(';');
    if (i < 0) {
      throw new ClassFormatException("Invalid signature: " + paramString);
    }
    consumed_chars = i + 1;
    return new ObjectType(paramString.substring(1, i).replace('/', '.'));
  }
  
  public static Type getReturnType(String paramString)
  {
    try
    {
      int i = paramString.lastIndexOf(')') + 1;
      return getType(paramString.substring(i));
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      throw new ClassFormatException("Invalid method signature: " + paramString);
    }
  }
  
  public static Type[] getArgumentTypes(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      if (paramString.charAt(0) != '(') {
        throw new ClassFormatException("Invalid method signature: " + paramString);
      }
      int i = 1;
      while (paramString.charAt(i) != ')')
      {
        localArrayList.add(getType(paramString.substring(i)));
        i += consumed_chars;
      }
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      throw new ClassFormatException("Invalid method signature: " + paramString);
    }
    Type[] arrayOfType = new Type[localArrayList.size()];
    localArrayList.toArray(arrayOfType);
    return arrayOfType;
  }
  
  public static Type getType(Class paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException("Class must not be null");
    }
    if (paramClass.isArray()) {
      return getType(paramClass.getName());
    }
    if (paramClass.isPrimitive())
    {
      if (paramClass == Integer.TYPE) {
        return INT;
      }
      if (paramClass == Void.TYPE) {
        return VOID;
      }
      if (paramClass == Double.TYPE) {
        return DOUBLE;
      }
      if (paramClass == Float.TYPE) {
        return FLOAT;
      }
      if (paramClass == Boolean.TYPE) {
        return BOOLEAN;
      }
      if (paramClass == Byte.TYPE) {
        return BYTE;
      }
      if (paramClass == Short.TYPE) {
        return SHORT;
      }
      if (paramClass == Byte.TYPE) {
        return BYTE;
      }
      if (paramClass == Long.TYPE) {
        return LONG;
      }
      if (paramClass == Character.TYPE) {
        return CHAR;
      }
      throw new IllegalStateException("Ooops, what primitive type is " + paramClass);
    }
    return new ObjectType(paramClass.getName());
  }
  
  public static String getSignature(Method paramMethod)
  {
    StringBuffer localStringBuffer = new StringBuffer("(");
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    for (int i = 0; i < arrayOfClass.length; i++) {
      localStringBuffer.append(getType(arrayOfClass[i]).getSignature());
    }
    localStringBuffer.append(")");
    localStringBuffer.append(getType(paramMethod.getReturnType()).getSignature());
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\Type.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */