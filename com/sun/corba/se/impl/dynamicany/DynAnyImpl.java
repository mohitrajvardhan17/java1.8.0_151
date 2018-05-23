package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

abstract class DynAnyImpl
  extends LocalObject
  implements DynAny
{
  protected static final int NO_INDEX = -1;
  protected static final byte STATUS_DESTROYABLE = 0;
  protected static final byte STATUS_UNDESTROYABLE = 1;
  protected static final byte STATUS_DESTROYED = 2;
  protected ORB orb = null;
  protected ORBUtilSystemException wrapper;
  protected Any any = null;
  protected byte status = 0;
  protected int index = -1;
  private String[] __ids = { "IDL:omg.org/DynamicAny/DynAny:1.0" };
  
  protected DynAnyImpl()
  {
    wrapper = ORBUtilSystemException.get("rpc.presentation");
  }
  
  protected DynAnyImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.presentation");
    if (paramBoolean) {
      any = DynAnyUtil.copy(paramAny, paramORB);
    } else {
      any = paramAny;
    }
    index = -1;
  }
  
  protected DynAnyImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.presentation");
    any = DynAnyUtil.createDefaultAnyOfType(paramTypeCode, paramORB);
  }
  
  protected DynAnyFactory factory()
  {
    try
    {
      return (DynAnyFactory)orb.resolve_initial_references("DynAnyFactory");
    }
    catch (InvalidName localInvalidName)
    {
      throw new RuntimeException("Unable to find DynAnyFactory");
    }
  }
  
  protected Any getAny()
  {
    return any;
  }
  
  protected Any getAny(DynAny paramDynAny)
  {
    if ((paramDynAny instanceof DynAnyImpl)) {
      return ((DynAnyImpl)paramDynAny).getAny();
    }
    return paramDynAny.to_any();
  }
  
  protected void writeAny(OutputStream paramOutputStream)
  {
    any.write_value(paramOutputStream);
  }
  
  protected void setStatus(byte paramByte)
  {
    status = paramByte;
  }
  
  protected void clearData()
  {
    any.type(any.type());
  }
  
  public TypeCode type()
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    return any.type();
  }
  
  public void assign(DynAny paramDynAny)
    throws TypeMismatch
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    if ((any != null) && (!any.type().equal(paramDynAny.type()))) {
      throw new TypeMismatch();
    }
    any = paramDynAny.to_any();
  }
  
  public void from_any(Any paramAny)
    throws TypeMismatch, InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    if ((any != null) && (!any.type().equal(paramAny.type()))) {
      throw new TypeMismatch();
    }
    Any localAny = null;
    try
    {
      localAny = DynAnyUtil.copy(paramAny, orb);
    }
    catch (Exception localException)
    {
      throw new InvalidValue();
    }
    if (!DynAnyUtil.isInitialized(localAny)) {
      throw new InvalidValue();
    }
    any = localAny;
  }
  
  public abstract Any to_any();
  
  public abstract boolean equal(DynAny paramDynAny);
  
  public abstract void destroy();
  
  public abstract DynAny copy();
  
  public String[] _ids()
  {
    return (String[])__ids.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\dynamicany\DynAnyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */