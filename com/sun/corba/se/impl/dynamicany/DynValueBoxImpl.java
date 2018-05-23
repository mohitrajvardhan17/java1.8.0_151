package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynValueBox;

public class DynValueBoxImpl
  extends DynValueCommonImpl
  implements DynValueBox
{
  private DynValueBoxImpl()
  {
    this(null, (Any)null, false);
  }
  
  protected DynValueBoxImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    super(paramORB, paramAny, paramBoolean);
  }
  
  protected DynValueBoxImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    super(paramORB, paramTypeCode);
  }
  
  public Any get_boxed_value()
    throws InvalidValue
  {
    if (isNull) {
      throw new InvalidValue();
    }
    checkInitAny();
    return any;
  }
  
  public void set_boxed_value(Any paramAny)
    throws TypeMismatch
  {
    if ((!isNull) && (!paramAny.type().equal(type()))) {
      throw new TypeMismatch();
    }
    clearData();
    any = paramAny;
    representations = 2;
    index = 0;
    isNull = false;
  }
  
  public DynAny get_boxed_value_as_dyn_any()
    throws InvalidValue
  {
    if (isNull) {
      throw new InvalidValue();
    }
    checkInitComponents();
    return components[0];
  }
  
  public void set_boxed_value_as_dyn_any(DynAny paramDynAny)
    throws TypeMismatch
  {
    if ((!isNull) && (!paramDynAny.type().equal(type()))) {
      throw new TypeMismatch();
    }
    clearData();
    components = new DynAny[] { paramDynAny };
    representations = 4;
    index = 0;
    isNull = false;
  }
  
  protected boolean initializeComponentsFromAny()
  {
    try
    {
      components = new DynAny[] { DynAnyUtil.createMostDerivedDynAny(any, orb, false) };
    }
    catch (InconsistentTypeCode localInconsistentTypeCode)
    {
      return false;
    }
    return true;
  }
  
  protected boolean initializeComponentsFromTypeCode()
  {
    try
    {
      any = DynAnyUtil.createDefaultAnyOfType(any.type(), orb);
      components = new DynAny[] { DynAnyUtil.createMostDerivedDynAny(any, orb, false) };
    }
    catch (InconsistentTypeCode localInconsistentTypeCode)
    {
      return false;
    }
    return true;
  }
  
  protected boolean initializeAnyFromComponents()
  {
    any = getAny(components[0]);
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\dynamicany\DynValueBoxImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */