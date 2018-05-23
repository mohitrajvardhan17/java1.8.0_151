package com.sun.corba.se.impl.corba;

import java.util.Vector;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.TypeCode;

public class ExceptionListImpl
  extends ExceptionList
{
  private final int INITIAL_CAPACITY = 2;
  private final int CAPACITY_INCREMENT = 2;
  private Vector _exceptions = new Vector(2, 2);
  
  public ExceptionListImpl() {}
  
  public int count()
  {
    return _exceptions.size();
  }
  
  public void add(TypeCode paramTypeCode)
  {
    _exceptions.addElement(paramTypeCode);
  }
  
  public TypeCode item(int paramInt)
    throws Bounds
  {
    try
    {
      return (TypeCode)_exceptions.elementAt(paramInt);
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new Bounds();
    }
  }
  
  public void remove(int paramInt)
    throws Bounds
  {
    try
    {
      _exceptions.removeElementAt(paramInt);
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new Bounds();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\corba\ExceptionListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */