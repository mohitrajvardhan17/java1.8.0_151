package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynStruct;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.NameValuePair;

public class DynStructImpl
  extends DynAnyComplexImpl
  implements DynStruct
{
  private DynStructImpl()
  {
    this(null, (Any)null, false);
  }
  
  protected DynStructImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    super(paramORB, paramAny, paramBoolean);
  }
  
  protected DynStructImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    super(paramORB, paramTypeCode);
    index = 0;
  }
  
  public NameValuePair[] get_members()
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    checkInitComponents();
    return nameValuePairs;
  }
  
  public NameDynAnyPair[] get_members_as_dyn_any()
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    checkInitComponents();
    return nameDynAnyPairs;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\dynamicany\DynStructImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */