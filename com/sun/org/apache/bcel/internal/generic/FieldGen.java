package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantObject;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantValue;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.util.ArrayList;
import java.util.Iterator;

public class FieldGen
  extends FieldGenOrMethodGen
{
  private Object value = null;
  private ArrayList observers;
  
  public FieldGen(int paramInt, Type paramType, String paramString, ConstantPoolGen paramConstantPoolGen)
  {
    setAccessFlags(paramInt);
    setType(paramType);
    setName(paramString);
    setConstantPool(paramConstantPoolGen);
  }
  
  public FieldGen(Field paramField, ConstantPoolGen paramConstantPoolGen)
  {
    this(paramField.getAccessFlags(), Type.getType(paramField.getSignature()), paramField.getName(), paramConstantPoolGen);
    Attribute[] arrayOfAttribute = paramField.getAttributes();
    for (int i = 0; i < arrayOfAttribute.length; i++) {
      if ((arrayOfAttribute[i] instanceof ConstantValue)) {
        setValue(((ConstantValue)arrayOfAttribute[i]).getConstantValueIndex());
      } else {
        addAttribute(arrayOfAttribute[i]);
      }
    }
  }
  
  private void setValue(int paramInt)
  {
    ConstantPool localConstantPool = cp.getConstantPool();
    Constant localConstant = localConstantPool.getConstant(paramInt);
    value = ((ConstantObject)localConstant).getConstantValue(localConstantPool);
  }
  
  public void setInitValue(String paramString)
  {
    checkType(new ObjectType("java.lang.String"));
    if (paramString != null) {
      value = paramString;
    }
  }
  
  public void setInitValue(long paramLong)
  {
    checkType(Type.LONG);
    if (paramLong != 0L) {
      value = new Long(paramLong);
    }
  }
  
  public void setInitValue(int paramInt)
  {
    checkType(Type.INT);
    if (paramInt != 0) {
      value = new Integer(paramInt);
    }
  }
  
  public void setInitValue(short paramShort)
  {
    checkType(Type.SHORT);
    if (paramShort != 0) {
      value = new Integer(paramShort);
    }
  }
  
  public void setInitValue(char paramChar)
  {
    checkType(Type.CHAR);
    if (paramChar != 0) {
      value = new Integer(paramChar);
    }
  }
  
  public void setInitValue(byte paramByte)
  {
    checkType(Type.BYTE);
    if (paramByte != 0) {
      value = new Integer(paramByte);
    }
  }
  
  public void setInitValue(boolean paramBoolean)
  {
    checkType(Type.BOOLEAN);
    if (paramBoolean) {
      value = new Integer(1);
    }
  }
  
  public void setInitValue(float paramFloat)
  {
    checkType(Type.FLOAT);
    if (paramFloat != 0.0D) {
      value = new Float(paramFloat);
    }
  }
  
  public void setInitValue(double paramDouble)
  {
    checkType(Type.DOUBLE);
    if (paramDouble != 0.0D) {
      value = new Double(paramDouble);
    }
  }
  
  public void cancelInitValue()
  {
    value = null;
  }
  
  private void checkType(Type paramType)
  {
    if (type == null) {
      throw new ClassGenException("You haven't defined the type of the field yet");
    }
    if (!isFinal()) {
      throw new ClassGenException("Only final fields may have an initial value!");
    }
    if (!type.equals(paramType)) {
      throw new ClassGenException("Types are not compatible: " + type + " vs. " + paramType);
    }
  }
  
  public Field getField()
  {
    String str = getSignature();
    int i = cp.addUtf8(name);
    int j = cp.addUtf8(str);
    if (value != null)
    {
      checkType(type);
      int k = addConstant();
      addAttribute(new ConstantValue(cp.addUtf8("ConstantValue"), 2, k, cp.getConstantPool()));
    }
    return new Field(access_flags, i, j, getAttributes(), cp.getConstantPool());
  }
  
  private int addConstant()
  {
    switch (type.getType())
    {
    case 4: 
    case 5: 
    case 8: 
    case 9: 
    case 10: 
      return cp.addInteger(((Integer)value).intValue());
    case 6: 
      return cp.addFloat(((Float)value).floatValue());
    case 7: 
      return cp.addDouble(((Double)value).doubleValue());
    case 11: 
      return cp.addLong(((Long)value).longValue());
    case 14: 
      return cp.addString((String)value);
    }
    throw new RuntimeException("Oops: Unhandled : " + type.getType());
  }
  
  public String getSignature()
  {
    return type.getSignature();
  }
  
  public void addObserver(FieldObserver paramFieldObserver)
  {
    if (observers == null) {
      observers = new ArrayList();
    }
    observers.add(paramFieldObserver);
  }
  
  public void removeObserver(FieldObserver paramFieldObserver)
  {
    if (observers != null) {
      observers.remove(paramFieldObserver);
    }
  }
  
  public void update()
  {
    if (observers != null)
    {
      Iterator localIterator = observers.iterator();
      while (localIterator.hasNext()) {
        ((FieldObserver)localIterator.next()).notify(this);
      }
    }
  }
  
  public String getInitValue()
  {
    if (value != null) {
      return value.toString();
    }
    return null;
  }
  
  public final String toString()
  {
    String str3 = Utility.accessToString(access_flags);
    str3 = str3 + " ";
    String str2 = type.toString();
    String str1 = getName();
    StringBuffer localStringBuffer = new StringBuffer(str3 + str2 + " " + str1);
    String str4 = getInitValue();
    if (str4 != null) {
      localStringBuffer.append(" = " + str4);
    }
    return localStringBuffer.toString();
  }
  
  public FieldGen copy(ConstantPoolGen paramConstantPoolGen)
  {
    FieldGen localFieldGen = (FieldGen)clone();
    localFieldGen.setConstantPool(paramConstantPoolGen);
    return localFieldGen;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\FieldGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */