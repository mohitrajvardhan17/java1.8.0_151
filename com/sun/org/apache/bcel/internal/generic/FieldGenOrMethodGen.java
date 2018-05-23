package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.AccessFlags;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import java.io.PrintStream;
import java.util.ArrayList;

public abstract class FieldGenOrMethodGen
  extends AccessFlags
  implements NamedAndTyped, Cloneable
{
  protected String name;
  protected Type type;
  protected ConstantPoolGen cp;
  private ArrayList attribute_vec = new ArrayList();
  
  protected FieldGenOrMethodGen() {}
  
  public void setType(Type paramType)
  {
    if (paramType.getType() == 16) {
      throw new IllegalArgumentException("Type can not be " + paramType);
    }
    type = paramType;
  }
  
  public Type getType()
  {
    return type;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public ConstantPoolGen getConstantPool()
  {
    return cp;
  }
  
  public void setConstantPool(ConstantPoolGen paramConstantPoolGen)
  {
    cp = paramConstantPoolGen;
  }
  
  public void addAttribute(Attribute paramAttribute)
  {
    attribute_vec.add(paramAttribute);
  }
  
  public void removeAttribute(Attribute paramAttribute)
  {
    attribute_vec.remove(paramAttribute);
  }
  
  public void removeAttributes()
  {
    attribute_vec.clear();
  }
  
  public Attribute[] getAttributes()
  {
    Attribute[] arrayOfAttribute = new Attribute[attribute_vec.size()];
    attribute_vec.toArray(arrayOfAttribute);
    return arrayOfAttribute;
  }
  
  public abstract String getSignature();
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      System.err.println(localCloneNotSupportedException);
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\generic\FieldGenOrMethodGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */