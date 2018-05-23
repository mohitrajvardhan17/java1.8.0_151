package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynValue;

public class DynValueImpl
  extends DynValueCommonImpl
  implements DynValue
{
  private DynValueImpl()
  {
    this(null, (Any)null, false);
  }
  
  protected DynValueImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    super(paramORB, paramAny, paramBoolean);
  }
  
  protected DynValueImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    super(paramORB, paramTypeCode);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\dynamicany\DynValueImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */