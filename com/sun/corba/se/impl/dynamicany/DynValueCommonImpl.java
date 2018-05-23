package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynValueCommon;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.NameValuePair;

abstract class DynValueCommonImpl
  extends DynAnyComplexImpl
  implements DynValueCommon
{
  protected boolean isNull;
  
  private DynValueCommonImpl()
  {
    this(null, (Any)null, false);
    isNull = true;
  }
  
  protected DynValueCommonImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    super(paramORB, paramAny, paramBoolean);
    isNull = checkInitComponents();
  }
  
  protected DynValueCommonImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    super(paramORB, paramTypeCode);
    isNull = true;
  }
  
  public boolean is_null()
  {
    return isNull;
  }
  
  public void set_to_null()
  {
    isNull = true;
    clearData();
  }
  
  public void set_to_value()
  {
    if (isNull) {
      isNull = false;
    }
  }
  
  public NameValuePair[] get_members()
    throws InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    if (isNull) {
      throw new InvalidValue();
    }
    checkInitComponents();
    return nameValuePairs;
  }
  
  public NameDynAnyPair[] get_members_as_dyn_any()
    throws InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    if (isNull) {
      throw new InvalidValue();
    }
    checkInitComponents();
    return nameDynAnyPairs;
  }
  
  public void set_members(NameValuePair[] paramArrayOfNameValuePair)
    throws TypeMismatch, InvalidValue
  {
    super.set_members(paramArrayOfNameValuePair);
    isNull = false;
  }
  
  public void set_members_as_dyn_any(NameDynAnyPair[] paramArrayOfNameDynAnyPair)
    throws TypeMismatch, InvalidValue
  {
    super.set_members_as_dyn_any(paramArrayOfNameDynAnyPair);
    isNull = false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\dynamicany\DynValueCommonImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */