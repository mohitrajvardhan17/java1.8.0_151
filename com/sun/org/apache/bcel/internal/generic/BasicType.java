package com.sun.org.apache.bcel.internal.generic;

public final class BasicType
  extends Type
{
  BasicType(byte paramByte)
  {
    super(paramByte, com.sun.org.apache.bcel.internal.Constants.SHORT_TYPE_NAMES[paramByte]);
    if ((paramByte < 4) || (paramByte > 12)) {
      throw new ClassGenException("Invalid type: " + paramByte);
    }
  }
  
  public static final BasicType getType(byte paramByte)
  {
    switch (paramByte)
    {
    case 12: 
      return VOID;
    case 4: 
      return BOOLEAN;
    case 8: 
      return BYTE;
    case 9: 
      return SHORT;
    case 5: 
      return CHAR;
    case 10: 
      return INT;
    case 11: 
      return LONG;
    case 7: 
      return DOUBLE;
    case 6: 
      return FLOAT;
    }
    throw new ClassGenException("Invalid type: " + paramByte);
  }
  
  public boolean equals(Object paramObject)
  {
    return type == type;
  }
  
  public int hashCode()
  {
    return type;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\BasicType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */