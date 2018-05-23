package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;

public class DynAnyFactoryImpl
  extends LocalObject
  implements DynAnyFactory
{
  private ORB orb;
  private String[] __ids = { "IDL:omg.org/DynamicAny/DynAnyFactory:1.0" };
  
  private DynAnyFactoryImpl()
  {
    orb = null;
  }
  
  public DynAnyFactoryImpl(ORB paramORB)
  {
    orb = paramORB;
  }
  
  public DynAny create_dyn_any(Any paramAny)
    throws InconsistentTypeCode
  {
    return DynAnyUtil.createMostDerivedDynAny(paramAny, orb, true);
  }
  
  public DynAny create_dyn_any_from_type_code(TypeCode paramTypeCode)
    throws InconsistentTypeCode
  {
    return DynAnyUtil.createMostDerivedDynAny(paramTypeCode, orb);
  }
  
  public String[] _ids()
  {
    return (String[])__ids.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\dynamicany\DynAnyFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */