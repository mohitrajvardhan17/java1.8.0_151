package org.omg.CORBA;

@Deprecated
public abstract interface DynEnum
  extends Object, DynAny
{
  public abstract String value_as_string();
  
  public abstract void value_as_string(String paramString);
  
  public abstract int value_as_ulong();
  
  public abstract void value_as_ulong(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\DynEnum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */