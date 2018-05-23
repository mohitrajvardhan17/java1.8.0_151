package java.io;

import java.lang.reflect.Field;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public class ObjectStreamField
  implements Comparable<Object>
{
  private final String name;
  private final String signature;
  private final Class<?> type;
  private final boolean unshared;
  private final Field field;
  private int offset = 0;
  
  public ObjectStreamField(String paramString, Class<?> paramClass)
  {
    this(paramString, paramClass, false);
  }
  
  public ObjectStreamField(String paramString, Class<?> paramClass, boolean paramBoolean)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    name = paramString;
    type = paramClass;
    unshared = paramBoolean;
    signature = getClassSignature(paramClass).intern();
    field = null;
  }
  
  ObjectStreamField(String paramString1, String paramString2, boolean paramBoolean)
  {
    if (paramString1 == null) {
      throw new NullPointerException();
    }
    name = paramString1;
    signature = paramString2.intern();
    unshared = paramBoolean;
    field = null;
    switch (paramString2.charAt(0))
    {
    case 'Z': 
      type = Boolean.TYPE;
      break;
    case 'B': 
      type = Byte.TYPE;
      break;
    case 'C': 
      type = Character.TYPE;
      break;
    case 'S': 
      type = Short.TYPE;
      break;
    case 'I': 
      type = Integer.TYPE;
      break;
    case 'J': 
      type = Long.TYPE;
      break;
    case 'F': 
      type = Float.TYPE;
      break;
    case 'D': 
      type = Double.TYPE;
      break;
    case 'L': 
    case '[': 
      type = Object.class;
      break;
    case 'E': 
    case 'G': 
    case 'H': 
    case 'K': 
    case 'M': 
    case 'N': 
    case 'O': 
    case 'P': 
    case 'Q': 
    case 'R': 
    case 'T': 
    case 'U': 
    case 'V': 
    case 'W': 
    case 'X': 
    case 'Y': 
    default: 
      throw new IllegalArgumentException("illegal signature");
    }
  }
  
  ObjectStreamField(Field paramField, boolean paramBoolean1, boolean paramBoolean2)
  {
    field = paramField;
    unshared = paramBoolean1;
    name = paramField.getName();
    Class localClass = paramField.getType();
    type = ((paramBoolean2) || (localClass.isPrimitive()) ? localClass : Object.class);
    signature = getClassSignature(localClass).intern();
  }
  
  public String getName()
  {
    return name;
  }
  
  @CallerSensitive
  public Class<?> getType()
  {
    if (System.getSecurityManager() != null)
    {
      Class localClass = Reflection.getCallerClass();
      if (ReflectUtil.needsPackageAccessCheck(localClass.getClassLoader(), type.getClassLoader())) {
        ReflectUtil.checkPackageAccess(type);
      }
    }
    return type;
  }
  
  public char getTypeCode()
  {
    return signature.charAt(0);
  }
  
  public String getTypeString()
  {
    return isPrimitive() ? null : signature;
  }
  
  public int getOffset()
  {
    return offset;
  }
  
  protected void setOffset(int paramInt)
  {
    offset = paramInt;
  }
  
  public boolean isPrimitive()
  {
    int i = signature.charAt(0);
    return (i != 76) && (i != 91);
  }
  
  public boolean isUnshared()
  {
    return unshared;
  }
  
  public int compareTo(Object paramObject)
  {
    ObjectStreamField localObjectStreamField = (ObjectStreamField)paramObject;
    boolean bool = isPrimitive();
    if (bool != localObjectStreamField.isPrimitive()) {
      return bool ? -1 : 1;
    }
    return name.compareTo(name);
  }
  
  public String toString()
  {
    return signature + ' ' + name;
  }
  
  Field getField()
  {
    return field;
  }
  
  String getSignature()
  {
    return signature;
  }
  
  private static String getClassSignature(Class<?> paramClass)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    while (paramClass.isArray())
    {
      localStringBuilder.append('[');
      paramClass = paramClass.getComponentType();
    }
    if (paramClass.isPrimitive())
    {
      if (paramClass == Integer.TYPE) {
        localStringBuilder.append('I');
      } else if (paramClass == Byte.TYPE) {
        localStringBuilder.append('B');
      } else if (paramClass == Long.TYPE) {
        localStringBuilder.append('J');
      } else if (paramClass == Float.TYPE) {
        localStringBuilder.append('F');
      } else if (paramClass == Double.TYPE) {
        localStringBuilder.append('D');
      } else if (paramClass == Short.TYPE) {
        localStringBuilder.append('S');
      } else if (paramClass == Character.TYPE) {
        localStringBuilder.append('C');
      } else if (paramClass == Boolean.TYPE) {
        localStringBuilder.append('Z');
      } else if (paramClass == Void.TYPE) {
        localStringBuilder.append('V');
      } else {
        throw new InternalError();
      }
    }
    else {
      localStringBuilder.append('L' + paramClass.getName().replace('.', '/') + ';');
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\ObjectStreamField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */