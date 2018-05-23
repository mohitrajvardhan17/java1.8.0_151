package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantCP;
import com.sun.org.apache.bcel.internal.classfile.ConstantClass;
import com.sun.org.apache.bcel.internal.classfile.ConstantDouble;
import com.sun.org.apache.bcel.internal.classfile.ConstantFieldref;
import com.sun.org.apache.bcel.internal.classfile.ConstantFloat;
import com.sun.org.apache.bcel.internal.classfile.ConstantInteger;
import com.sun.org.apache.bcel.internal.classfile.ConstantInterfaceMethodref;
import com.sun.org.apache.bcel.internal.classfile.ConstantLong;
import com.sun.org.apache.bcel.internal.classfile.ConstantMethodref;
import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantString;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import java.io.Serializable;
import java.util.HashMap;

public class ConstantPoolGen
  implements Serializable
{
  protected int size = 1024;
  protected Constant[] constants = new Constant[size];
  protected int index = 1;
  private static final String METHODREF_DELIM = ":";
  private static final String IMETHODREF_DELIM = "#";
  private static final String FIELDREF_DELIM = "&";
  private static final String NAT_DELIM = "%";
  private HashMap string_table = new HashMap();
  private HashMap class_table = new HashMap();
  private HashMap utf8_table = new HashMap();
  private HashMap n_a_t_table = new HashMap();
  private HashMap cp_table = new HashMap();
  
  public ConstantPoolGen(Constant[] paramArrayOfConstant)
  {
    if (paramArrayOfConstant.length > size)
    {
      size = paramArrayOfConstant.length;
      constants = new Constant[size];
    }
    System.arraycopy(paramArrayOfConstant, 0, constants, 0, paramArrayOfConstant.length);
    if (paramArrayOfConstant.length > 0) {
      index = paramArrayOfConstant.length;
    }
    for (int i = 1; i < index; i++)
    {
      Constant localConstant = constants[i];
      Object localObject1;
      Object localObject2;
      if ((localConstant instanceof ConstantString))
      {
        localObject1 = (ConstantString)localConstant;
        localObject2 = (ConstantUtf8)constants[localObject1.getStringIndex()];
        string_table.put(((ConstantUtf8)localObject2).getBytes(), new Index(i));
      }
      else if ((localConstant instanceof ConstantClass))
      {
        localObject1 = (ConstantClass)localConstant;
        localObject2 = (ConstantUtf8)constants[localObject1.getNameIndex()];
        class_table.put(((ConstantUtf8)localObject2).getBytes(), new Index(i));
      }
      else
      {
        Object localObject3;
        if ((localConstant instanceof ConstantNameAndType))
        {
          localObject1 = (ConstantNameAndType)localConstant;
          localObject2 = (ConstantUtf8)constants[localObject1.getNameIndex()];
          localObject3 = (ConstantUtf8)constants[localObject1.getSignatureIndex()];
          n_a_t_table.put(((ConstantUtf8)localObject2).getBytes() + "%" + ((ConstantUtf8)localObject3).getBytes(), new Index(i));
        }
        else if ((localConstant instanceof ConstantUtf8))
        {
          localObject1 = (ConstantUtf8)localConstant;
          utf8_table.put(((ConstantUtf8)localObject1).getBytes(), new Index(i));
        }
        else if ((localConstant instanceof ConstantCP))
        {
          localObject1 = (ConstantCP)localConstant;
          localObject2 = (ConstantClass)constants[localObject1.getClassIndex()];
          localObject3 = (ConstantNameAndType)constants[localObject1.getNameAndTypeIndex()];
          ConstantUtf8 localConstantUtf8 = (ConstantUtf8)constants[localObject2.getNameIndex()];
          String str1 = localConstantUtf8.getBytes().replace('/', '.');
          localConstantUtf8 = (ConstantUtf8)constants[localObject3.getNameIndex()];
          String str2 = localConstantUtf8.getBytes();
          localConstantUtf8 = (ConstantUtf8)constants[localObject3.getSignatureIndex()];
          String str3 = localConstantUtf8.getBytes();
          String str4 = ":";
          if ((localConstant instanceof ConstantInterfaceMethodref)) {
            str4 = "#";
          } else if ((localConstant instanceof ConstantFieldref)) {
            str4 = "&";
          }
          cp_table.put(str1 + str4 + str2 + str4 + str3, new Index(i));
        }
      }
    }
  }
  
  public ConstantPoolGen(ConstantPool paramConstantPool)
  {
    this(paramConstantPool.getConstantPool());
  }
  
  public ConstantPoolGen() {}
  
  protected void adjustSize()
  {
    if (index + 3 >= size)
    {
      Constant[] arrayOfConstant = constants;
      size *= 2;
      constants = new Constant[size];
      System.arraycopy(arrayOfConstant, 0, constants, 0, index);
    }
  }
  
  public int lookupString(String paramString)
  {
    Index localIndex = (Index)string_table.get(paramString);
    return localIndex != null ? index : -1;
  }
  
  public int addString(String paramString)
  {
    if ((i = lookupString(paramString)) != -1) {
      return i;
    }
    int j = addUtf8(paramString);
    adjustSize();
    ConstantString localConstantString = new ConstantString(j);
    int i = index;
    constants[(index++)] = localConstantString;
    string_table.put(paramString, new Index(i));
    return i;
  }
  
  public int lookupClass(String paramString)
  {
    Index localIndex = (Index)class_table.get(paramString.replace('.', '/'));
    return localIndex != null ? index : -1;
  }
  
  private int addClass_(String paramString)
  {
    if ((i = lookupClass(paramString)) != -1) {
      return i;
    }
    adjustSize();
    ConstantClass localConstantClass = new ConstantClass(addUtf8(paramString));
    int i = index;
    constants[(index++)] = localConstantClass;
    class_table.put(paramString, new Index(i));
    return i;
  }
  
  public int addClass(String paramString)
  {
    return addClass_(paramString.replace('.', '/'));
  }
  
  public int addClass(ObjectType paramObjectType)
  {
    return addClass(paramObjectType.getClassName());
  }
  
  public int addArrayClass(ArrayType paramArrayType)
  {
    return addClass_(paramArrayType.getSignature());
  }
  
  public int lookupInteger(int paramInt)
  {
    for (int i = 1; i < index; i++) {
      if ((constants[i] instanceof ConstantInteger))
      {
        ConstantInteger localConstantInteger = (ConstantInteger)constants[i];
        if (localConstantInteger.getBytes() == paramInt) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public int addInteger(int paramInt)
  {
    if ((i = lookupInteger(paramInt)) != -1) {
      return i;
    }
    adjustSize();
    int i = index;
    constants[(index++)] = new ConstantInteger(paramInt);
    return i;
  }
  
  public int lookupFloat(float paramFloat)
  {
    int i = Float.floatToIntBits(paramFloat);
    for (int j = 1; j < index; j++) {
      if ((constants[j] instanceof ConstantFloat))
      {
        ConstantFloat localConstantFloat = (ConstantFloat)constants[j];
        if (Float.floatToIntBits(localConstantFloat.getBytes()) == i) {
          return j;
        }
      }
    }
    return -1;
  }
  
  public int addFloat(float paramFloat)
  {
    if ((i = lookupFloat(paramFloat)) != -1) {
      return i;
    }
    adjustSize();
    int i = index;
    constants[(index++)] = new ConstantFloat(paramFloat);
    return i;
  }
  
  public int lookupUtf8(String paramString)
  {
    Index localIndex = (Index)utf8_table.get(paramString);
    return localIndex != null ? index : -1;
  }
  
  public int addUtf8(String paramString)
  {
    if ((i = lookupUtf8(paramString)) != -1) {
      return i;
    }
    adjustSize();
    int i = index;
    constants[(index++)] = new ConstantUtf8(paramString);
    utf8_table.put(paramString, new Index(i));
    return i;
  }
  
  public int lookupLong(long paramLong)
  {
    for (int i = 1; i < index; i++) {
      if ((constants[i] instanceof ConstantLong))
      {
        ConstantLong localConstantLong = (ConstantLong)constants[i];
        if (localConstantLong.getBytes() == paramLong) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public int addLong(long paramLong)
  {
    if ((i = lookupLong(paramLong)) != -1) {
      return i;
    }
    adjustSize();
    int i = index;
    constants[index] = new ConstantLong(paramLong);
    index += 2;
    return i;
  }
  
  public int lookupDouble(double paramDouble)
  {
    long l = Double.doubleToLongBits(paramDouble);
    for (int i = 1; i < index; i++) {
      if ((constants[i] instanceof ConstantDouble))
      {
        ConstantDouble localConstantDouble = (ConstantDouble)constants[i];
        if (Double.doubleToLongBits(localConstantDouble.getBytes()) == l) {
          return i;
        }
      }
    }
    return -1;
  }
  
  public int addDouble(double paramDouble)
  {
    if ((i = lookupDouble(paramDouble)) != -1) {
      return i;
    }
    adjustSize();
    int i = index;
    constants[index] = new ConstantDouble(paramDouble);
    index += 2;
    return i;
  }
  
  public int lookupNameAndType(String paramString1, String paramString2)
  {
    Index localIndex = (Index)n_a_t_table.get(paramString1 + "%" + paramString2);
    return localIndex != null ? index : -1;
  }
  
  public int addNameAndType(String paramString1, String paramString2)
  {
    if ((i = lookupNameAndType(paramString1, paramString2)) != -1) {
      return i;
    }
    adjustSize();
    int j = addUtf8(paramString1);
    int k = addUtf8(paramString2);
    int i = index;
    constants[(index++)] = new ConstantNameAndType(j, k);
    n_a_t_table.put(paramString1 + "%" + paramString2, new Index(i));
    return i;
  }
  
  public int lookupMethodref(String paramString1, String paramString2, String paramString3)
  {
    Index localIndex = (Index)cp_table.get(paramString1 + ":" + paramString2 + ":" + paramString3);
    return localIndex != null ? index : -1;
  }
  
  public int lookupMethodref(MethodGen paramMethodGen)
  {
    return lookupMethodref(paramMethodGen.getClassName(), paramMethodGen.getName(), paramMethodGen.getSignature());
  }
  
  public int addMethodref(String paramString1, String paramString2, String paramString3)
  {
    if ((i = lookupMethodref(paramString1, paramString2, paramString3)) != -1) {
      return i;
    }
    adjustSize();
    int k = addNameAndType(paramString2, paramString3);
    int j = addClass(paramString1);
    int i = index;
    constants[(index++)] = new ConstantMethodref(j, k);
    cp_table.put(paramString1 + ":" + paramString2 + ":" + paramString3, new Index(i));
    return i;
  }
  
  public int addMethodref(MethodGen paramMethodGen)
  {
    return addMethodref(paramMethodGen.getClassName(), paramMethodGen.getName(), paramMethodGen.getSignature());
  }
  
  public int lookupInterfaceMethodref(String paramString1, String paramString2, String paramString3)
  {
    Index localIndex = (Index)cp_table.get(paramString1 + "#" + paramString2 + "#" + paramString3);
    return localIndex != null ? index : -1;
  }
  
  public int lookupInterfaceMethodref(MethodGen paramMethodGen)
  {
    return lookupInterfaceMethodref(paramMethodGen.getClassName(), paramMethodGen.getName(), paramMethodGen.getSignature());
  }
  
  public int addInterfaceMethodref(String paramString1, String paramString2, String paramString3)
  {
    if ((i = lookupInterfaceMethodref(paramString1, paramString2, paramString3)) != -1) {
      return i;
    }
    adjustSize();
    int j = addClass(paramString1);
    int k = addNameAndType(paramString2, paramString3);
    int i = index;
    constants[(index++)] = new ConstantInterfaceMethodref(j, k);
    cp_table.put(paramString1 + "#" + paramString2 + "#" + paramString3, new Index(i));
    return i;
  }
  
  public int addInterfaceMethodref(MethodGen paramMethodGen)
  {
    return addInterfaceMethodref(paramMethodGen.getClassName(), paramMethodGen.getName(), paramMethodGen.getSignature());
  }
  
  public int lookupFieldref(String paramString1, String paramString2, String paramString3)
  {
    Index localIndex = (Index)cp_table.get(paramString1 + "&" + paramString2 + "&" + paramString3);
    return localIndex != null ? index : -1;
  }
  
  public int addFieldref(String paramString1, String paramString2, String paramString3)
  {
    if ((i = lookupFieldref(paramString1, paramString2, paramString3)) != -1) {
      return i;
    }
    adjustSize();
    int j = addClass(paramString1);
    int k = addNameAndType(paramString2, paramString3);
    int i = index;
    constants[(index++)] = new ConstantFieldref(j, k);
    cp_table.put(paramString1 + "&" + paramString2 + "&" + paramString3, new Index(i));
    return i;
  }
  
  public Constant getConstant(int paramInt)
  {
    return constants[paramInt];
  }
  
  public void setConstant(int paramInt, Constant paramConstant)
  {
    constants[paramInt] = paramConstant;
  }
  
  public ConstantPool getConstantPool()
  {
    return new ConstantPool(constants);
  }
  
  public int getSize()
  {
    return index;
  }
  
  public ConstantPool getFinalConstantPool()
  {
    Constant[] arrayOfConstant = new Constant[index];
    System.arraycopy(constants, 0, arrayOfConstant, 0, index);
    return new ConstantPool(arrayOfConstant);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 1; i < index; i++) {
      localStringBuffer.append(i + ")" + constants[i] + "\n");
    }
    return localStringBuffer.toString();
  }
  
  public int addConstant(Constant paramConstant, ConstantPoolGen paramConstantPoolGen)
  {
    Constant[] arrayOfConstant = paramConstantPoolGen.getConstantPool().getConstantPool();
    Object localObject1;
    Object localObject2;
    Object localObject3;
    switch (paramConstant.getTag())
    {
    case 8: 
      localObject1 = (ConstantString)paramConstant;
      localObject2 = (ConstantUtf8)arrayOfConstant[localObject1.getStringIndex()];
      return addString(((ConstantUtf8)localObject2).getBytes());
    case 7: 
      localObject1 = (ConstantClass)paramConstant;
      localObject2 = (ConstantUtf8)arrayOfConstant[localObject1.getNameIndex()];
      return addClass(((ConstantUtf8)localObject2).getBytes());
    case 12: 
      localObject1 = (ConstantNameAndType)paramConstant;
      localObject2 = (ConstantUtf8)arrayOfConstant[localObject1.getNameIndex()];
      localObject3 = (ConstantUtf8)arrayOfConstant[localObject1.getSignatureIndex()];
      return addNameAndType(((ConstantUtf8)localObject2).getBytes(), ((ConstantUtf8)localObject3).getBytes());
    case 1: 
      return addUtf8(((ConstantUtf8)paramConstant).getBytes());
    case 6: 
      return addDouble(((ConstantDouble)paramConstant).getBytes());
    case 4: 
      return addFloat(((ConstantFloat)paramConstant).getBytes());
    case 5: 
      return addLong(((ConstantLong)paramConstant).getBytes());
    case 3: 
      return addInteger(((ConstantInteger)paramConstant).getBytes());
    case 9: 
    case 10: 
    case 11: 
      localObject1 = (ConstantCP)paramConstant;
      localObject2 = (ConstantClass)arrayOfConstant[localObject1.getClassIndex()];
      localObject3 = (ConstantNameAndType)arrayOfConstant[localObject1.getNameAndTypeIndex()];
      ConstantUtf8 localConstantUtf8 = (ConstantUtf8)arrayOfConstant[localObject2.getNameIndex()];
      String str1 = localConstantUtf8.getBytes().replace('/', '.');
      localConstantUtf8 = (ConstantUtf8)arrayOfConstant[localObject3.getNameIndex()];
      String str2 = localConstantUtf8.getBytes();
      localConstantUtf8 = (ConstantUtf8)arrayOfConstant[localObject3.getSignatureIndex()];
      String str3 = localConstantUtf8.getBytes();
      switch (paramConstant.getTag())
      {
      case 11: 
        return addInterfaceMethodref(str1, str2, str3);
      case 10: 
        return addMethodref(str1, str2, str3);
      case 9: 
        return addFieldref(str1, str2, str3);
      }
      throw new RuntimeException("Unknown constant type " + paramConstant);
    }
    throw new RuntimeException("Unknown constant type " + paramConstant);
  }
  
  private static class Index
    implements Serializable
  {
    int index;
    
    Index(int paramInt)
    {
      index = paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\ConstantPoolGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */