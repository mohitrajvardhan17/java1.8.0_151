package org.omg.CORBA;

public abstract class ExceptionList
{
  public ExceptionList() {}
  
  public abstract int count();
  
  public abstract void add(TypeCode paramTypeCode);
  
  public abstract TypeCode item(int paramInt)
    throws Bounds;
  
  public abstract void remove(int paramInt)
    throws Bounds;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ExceptionList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */