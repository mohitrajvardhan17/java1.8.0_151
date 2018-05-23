package jdk.internal.org.objectweb.asm;

public class TypeReference
{
  public static final int CLASS_TYPE_PARAMETER = 0;
  public static final int METHOD_TYPE_PARAMETER = 1;
  public static final int CLASS_EXTENDS = 16;
  public static final int CLASS_TYPE_PARAMETER_BOUND = 17;
  public static final int METHOD_TYPE_PARAMETER_BOUND = 18;
  public static final int FIELD = 19;
  public static final int METHOD_RETURN = 20;
  public static final int METHOD_RECEIVER = 21;
  public static final int METHOD_FORMAL_PARAMETER = 22;
  public static final int THROWS = 23;
  public static final int LOCAL_VARIABLE = 64;
  public static final int RESOURCE_VARIABLE = 65;
  public static final int EXCEPTION_PARAMETER = 66;
  public static final int INSTANCEOF = 67;
  public static final int NEW = 68;
  public static final int CONSTRUCTOR_REFERENCE = 69;
  public static final int METHOD_REFERENCE = 70;
  public static final int CAST = 71;
  public static final int CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT = 72;
  public static final int METHOD_INVOCATION_TYPE_ARGUMENT = 73;
  public static final int CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT = 74;
  public static final int METHOD_REFERENCE_TYPE_ARGUMENT = 75;
  private int value;
  
  public TypeReference(int paramInt)
  {
    value = paramInt;
  }
  
  public static TypeReference newTypeReference(int paramInt)
  {
    return new TypeReference(paramInt << 24);
  }
  
  public static TypeReference newTypeParameterReference(int paramInt1, int paramInt2)
  {
    return new TypeReference(paramInt1 << 24 | paramInt2 << 16);
  }
  
  public static TypeReference newTypeParameterBoundReference(int paramInt1, int paramInt2, int paramInt3)
  {
    return new TypeReference(paramInt1 << 24 | paramInt2 << 16 | paramInt3 << 8);
  }
  
  public static TypeReference newSuperTypeReference(int paramInt)
  {
    paramInt &= 0xFFFF;
    return new TypeReference(0x10000000 | paramInt << 8);
  }
  
  public static TypeReference newFormalParameterReference(int paramInt)
  {
    return new TypeReference(0x16000000 | paramInt << 16);
  }
  
  public static TypeReference newExceptionReference(int paramInt)
  {
    return new TypeReference(0x17000000 | paramInt << 8);
  }
  
  public static TypeReference newTryCatchReference(int paramInt)
  {
    return new TypeReference(0x42000000 | paramInt << 8);
  }
  
  public static TypeReference newTypeArgumentReference(int paramInt1, int paramInt2)
  {
    return new TypeReference(paramInt1 << 24 | paramInt2);
  }
  
  public int getSort()
  {
    return value >>> 24;
  }
  
  public int getTypeParameterIndex()
  {
    return (value & 0xFF0000) >> 16;
  }
  
  public int getTypeParameterBoundIndex()
  {
    return (value & 0xFF00) >> 8;
  }
  
  public int getSuperTypeIndex()
  {
    return (short)((value & 0xFFFF00) >> 8);
  }
  
  public int getFormalParameterIndex()
  {
    return (value & 0xFF0000) >> 16;
  }
  
  public int getExceptionIndex()
  {
    return (value & 0xFFFF00) >> 8;
  }
  
  public int getTryCatchBlockIndex()
  {
    return (value & 0xFFFF00) >> 8;
  }
  
  public int getTypeArgumentIndex()
  {
    return value & 0xFF;
  }
  
  public int getValue()
  {
    return value;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\TypeReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */