package com.sun.org.apache.bcel.internal.generic;

public final class ArrayType
  extends ReferenceType
{
  private int dimensions;
  private Type basic_type;
  
  public ArrayType(byte paramByte, int paramInt)
  {
    this(BasicType.getType(paramByte), paramInt);
  }
  
  public ArrayType(String paramString, int paramInt)
  {
    this(new ObjectType(paramString), paramInt);
  }
  
  public ArrayType(Type paramType, int paramInt)
  {
    super((byte)13, "<dummy>");
    if ((paramInt < 1) || (paramInt > 255)) {
      throw new ClassGenException("Invalid number of dimensions: " + paramInt);
    }
    switch (paramType.getType())
    {
    case 13: 
      localObject = (ArrayType)paramType;
      dimensions = (paramInt + dimensions);
      basic_type = basic_type;
      break;
    case 12: 
      throw new ClassGenException("Invalid type: void[]");
    default: 
      dimensions = paramInt;
      basic_type = paramType;
    }
    Object localObject = new StringBuffer();
    for (int i = 0; i < dimensions; i++) {
      ((StringBuffer)localObject).append('[');
    }
    ((StringBuffer)localObject).append(basic_type.getSignature());
    signature = ((StringBuffer)localObject).toString();
  }
  
  public Type getBasicType()
  {
    return basic_type;
  }
  
  public Type getElementType()
  {
    if (dimensions == 1) {
      return basic_type;
    }
    return new ArrayType(basic_type, dimensions - 1);
  }
  
  public int getDimensions()
  {
    return dimensions;
  }
  
  public int hashCode()
  {
    return basic_type.hashCode() ^ dimensions;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ArrayType))
    {
      ArrayType localArrayType = (ArrayType)paramObject;
      return (dimensions == dimensions) && (basic_type.equals(basic_type));
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ArrayType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */