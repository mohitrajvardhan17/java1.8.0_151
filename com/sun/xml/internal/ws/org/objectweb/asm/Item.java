package com.sun.xml.internal.ws.org.objectweb.asm;

final class Item
{
  int index;
  int type;
  int intVal;
  long longVal;
  String strVal1;
  String strVal2;
  String strVal3;
  int hashCode;
  Item next;
  
  Item() {}
  
  Item(int paramInt)
  {
    index = paramInt;
  }
  
  Item(int paramInt, Item paramItem)
  {
    index = paramInt;
    type = type;
    intVal = intVal;
    longVal = longVal;
    strVal1 = strVal1;
    strVal2 = strVal2;
    strVal3 = strVal3;
    hashCode = hashCode;
  }
  
  void set(int paramInt)
  {
    type = 3;
    intVal = paramInt;
    hashCode = (0x7FFFFFFF & type + paramInt);
  }
  
  void set(long paramLong)
  {
    type = 5;
    longVal = paramLong;
    hashCode = (0x7FFFFFFF & type + (int)paramLong);
  }
  
  void set(float paramFloat)
  {
    type = 4;
    intVal = Float.floatToRawIntBits(paramFloat);
    hashCode = (0x7FFFFFFF & type + (int)paramFloat);
  }
  
  void set(double paramDouble)
  {
    type = 6;
    longVal = Double.doubleToRawLongBits(paramDouble);
    hashCode = (0x7FFFFFFF & type + (int)paramDouble);
  }
  
  void set(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    type = paramInt;
    strVal1 = paramString1;
    strVal2 = paramString2;
    strVal3 = paramString3;
    switch (paramInt)
    {
    case 1: 
    case 7: 
    case 8: 
    case 13: 
      hashCode = (0x7FFFFFFF & paramInt + paramString1.hashCode());
      return;
    case 12: 
      hashCode = (0x7FFFFFFF & paramInt + paramString1.hashCode() * paramString2.hashCode());
      return;
    }
    hashCode = (0x7FFFFFFF & paramInt + paramString1.hashCode() * paramString2.hashCode() * paramString3.hashCode());
  }
  
  boolean isEqualTo(Item paramItem)
  {
    if (type == type)
    {
      switch (type)
      {
      case 3: 
      case 4: 
        return intVal == intVal;
      case 5: 
      case 6: 
      case 15: 
        return longVal == longVal;
      case 1: 
      case 7: 
      case 8: 
      case 13: 
        return strVal1.equals(strVal1);
      case 14: 
        return (intVal == intVal) && (strVal1.equals(strVal1));
      case 12: 
        return (strVal1.equals(strVal1)) && (strVal2.equals(strVal2));
      }
      return (strVal1.equals(strVal1)) && (strVal2.equals(strVal2)) && (strVal3.equals(strVal3));
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\Item.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */