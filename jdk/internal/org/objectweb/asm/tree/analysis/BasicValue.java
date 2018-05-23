package jdk.internal.org.objectweb.asm.tree.analysis;

import jdk.internal.org.objectweb.asm.Type;

public class BasicValue
  implements Value
{
  public static final BasicValue UNINITIALIZED_VALUE = new BasicValue(null);
  public static final BasicValue INT_VALUE = new BasicValue(Type.INT_TYPE);
  public static final BasicValue FLOAT_VALUE = new BasicValue(Type.FLOAT_TYPE);
  public static final BasicValue LONG_VALUE = new BasicValue(Type.LONG_TYPE);
  public static final BasicValue DOUBLE_VALUE = new BasicValue(Type.DOUBLE_TYPE);
  public static final BasicValue REFERENCE_VALUE = new BasicValue(Type.getObjectType("java/lang/Object"));
  public static final BasicValue RETURNADDRESS_VALUE = new BasicValue(Type.VOID_TYPE);
  private final Type type;
  
  public BasicValue(Type paramType)
  {
    type = paramType;
  }
  
  public Type getType()
  {
    return type;
  }
  
  public int getSize()
  {
    return (type == Type.LONG_TYPE) || (type == Type.DOUBLE_TYPE) ? 2 : 1;
  }
  
  public boolean isReference()
  {
    return (type != null) && ((type.getSort() == 10) || (type.getSort() == 9));
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof BasicValue))
    {
      if (type == null) {
        return type == null;
      }
      return type.equals(type);
    }
    return false;
  }
  
  public int hashCode()
  {
    return type == null ? 0 : type.hashCode();
  }
  
  public String toString()
  {
    if (this == UNINITIALIZED_VALUE) {
      return ".";
    }
    if (this == RETURNADDRESS_VALUE) {
      return "A";
    }
    if (this == REFERENCE_VALUE) {
      return "R";
    }
    return type.getDescriptor();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\BasicValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */