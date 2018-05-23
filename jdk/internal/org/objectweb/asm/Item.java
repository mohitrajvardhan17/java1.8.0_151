package jdk.internal.org.objectweb.asm;

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
    case 7: 
      intVal = 0;
    case 1: 
    case 8: 
    case 16: 
    case 30: 
      hashCode = (0x7FFFFFFF & paramInt + paramString1.hashCode());
      return;
    case 12: 
      hashCode = (0x7FFFFFFF & paramInt + paramString1.hashCode() * paramString2.hashCode());
      return;
    }
    hashCode = (0x7FFFFFFF & paramInt + paramString1.hashCode() * paramString2.hashCode() * paramString3.hashCode());
  }
  
  void set(String paramString1, String paramString2, int paramInt)
  {
    type = 18;
    longVal = paramInt;
    strVal1 = paramString1;
    strVal2 = paramString2;
    hashCode = (0x7FFFFFFF & 18 + paramInt * strVal1.hashCode() * strVal2.hashCode());
  }
  
  void set(int paramInt1, int paramInt2)
  {
    type = 33;
    intVal = paramInt1;
    hashCode = paramInt2;
  }
  
  boolean isEqualTo(Item paramItem)
  {
    switch (type)
    {
    case 1: 
    case 7: 
    case 8: 
    case 16: 
    case 30: 
      return strVal1.equals(strVal1);
    case 5: 
    case 6: 
    case 32: 
      return longVal == longVal;
    case 3: 
    case 4: 
      return intVal == intVal;
    case 31: 
      return (intVal == intVal) && (strVal1.equals(strVal1));
    case 12: 
      return (strVal1.equals(strVal1)) && (strVal2.equals(strVal2));
    case 18: 
      return (longVal == longVal) && (strVal1.equals(strVal1)) && (strVal2.equals(strVal2));
    }
    return (strVal1.equals(strVal1)) && (strVal2.equals(strVal2)) && (strVal3.equals(strVal3));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\Item.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */