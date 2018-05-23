package com.sun.corba.se.impl.corba;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.NamedValue;

public class NamedValueImpl
  extends NamedValue
{
  private String _name;
  private Any _value;
  private int _flags;
  private ORB _orb;
  
  public NamedValueImpl(ORB paramORB)
  {
    _orb = paramORB;
    _value = new AnyImpl(_orb);
  }
  
  public NamedValueImpl(ORB paramORB, String paramString, Any paramAny, int paramInt)
  {
    _orb = paramORB;
    _name = paramString;
    _value = paramAny;
    _flags = paramInt;
  }
  
  public String name()
  {
    return _name;
  }
  
  public Any value()
  {
    return _value;
  }
  
  public int flags()
  {
    return _flags;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\corba\NamedValueImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */