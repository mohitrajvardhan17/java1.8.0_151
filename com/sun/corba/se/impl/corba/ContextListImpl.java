package com.sun.corba.se.impl.corba;

import java.util.Vector;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.ORB;

public class ContextListImpl
  extends ContextList
{
  private final int INITIAL_CAPACITY = 2;
  private final int CAPACITY_INCREMENT = 2;
  private ORB _orb;
  private Vector _contexts;
  
  public ContextListImpl(ORB paramORB)
  {
    _orb = paramORB;
    _contexts = new Vector(2, 2);
  }
  
  public int count()
  {
    return _contexts.size();
  }
  
  public void add(String paramString)
  {
    _contexts.addElement(paramString);
  }
  
  public String item(int paramInt)
    throws Bounds
  {
    try
    {
      return (String)_contexts.elementAt(paramInt);
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
      _contexts.removeElementAt(paramInt);
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new Bounds();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\corba\ContextListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */