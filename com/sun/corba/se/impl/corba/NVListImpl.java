package com.sun.corba.se.impl.corba;

import com.sun.corba.se.spi.orb.ORB;
import java.util.Vector;
import org.omg.CORBA.Any;
import org.omg.CORBA.Bounds;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;

public class NVListImpl
  extends NVList
{
  private final int INITIAL_CAPACITY = 4;
  private final int CAPACITY_INCREMENT = 2;
  private Vector _namedValues;
  private ORB orb;
  
  public NVListImpl(ORB paramORB)
  {
    orb = paramORB;
    _namedValues = new Vector(4, 2);
  }
  
  public NVListImpl(ORB paramORB, int paramInt)
  {
    orb = paramORB;
    _namedValues = new Vector(paramInt);
  }
  
  public int count()
  {
    return _namedValues.size();
  }
  
  public NamedValue add(int paramInt)
  {
    NamedValueImpl localNamedValueImpl = new NamedValueImpl(orb, "", new AnyImpl(orb), paramInt);
    _namedValues.addElement(localNamedValueImpl);
    return localNamedValueImpl;
  }
  
  public NamedValue add_item(String paramString, int paramInt)
  {
    NamedValueImpl localNamedValueImpl = new NamedValueImpl(orb, paramString, new AnyImpl(orb), paramInt);
    _namedValues.addElement(localNamedValueImpl);
    return localNamedValueImpl;
  }
  
  public NamedValue add_value(String paramString, Any paramAny, int paramInt)
  {
    NamedValueImpl localNamedValueImpl = new NamedValueImpl(orb, paramString, paramAny, paramInt);
    _namedValues.addElement(localNamedValueImpl);
    return localNamedValueImpl;
  }
  
  public NamedValue item(int paramInt)
    throws Bounds
  {
    try
    {
      return (NamedValue)_namedValues.elementAt(paramInt);
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
      _namedValues.removeElementAt(paramInt);
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new Bounds();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\corba\NVListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */