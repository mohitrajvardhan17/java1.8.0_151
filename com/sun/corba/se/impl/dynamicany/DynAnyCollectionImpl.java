package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

abstract class DynAnyCollectionImpl
  extends DynAnyConstructedImpl
{
  Any[] anys = null;
  
  private DynAnyCollectionImpl()
  {
    this(null, (Any)null, false);
  }
  
  protected DynAnyCollectionImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    super(paramORB, paramAny, paramBoolean);
  }
  
  protected DynAnyCollectionImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    super(paramORB, paramTypeCode);
  }
  
  protected void createDefaultComponentAt(int paramInt, TypeCode paramTypeCode)
  {
    try
    {
      components[paramInt] = DynAnyUtil.createMostDerivedDynAny(paramTypeCode, orb);
    }
    catch (InconsistentTypeCode localInconsistentTypeCode) {}
    anys[paramInt] = getAny(components[paramInt]);
  }
  
  protected TypeCode getContentType()
  {
    try
    {
      return any.type().content_type();
    }
    catch (BadKind localBadKind) {}
    return null;
  }
  
  protected int getBound()
  {
    try
    {
      return any.type().length();
    }
    catch (BadKind localBadKind) {}
    return 0;
  }
  
  public Any[] get_elements()
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    return checkInitComponents() ? anys : null;
  }
  
  protected abstract void checkValue(Object[] paramArrayOfObject)
    throws InvalidValue;
  
  public void set_elements(Any[] paramArrayOfAny)
    throws TypeMismatch, InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    checkValue(paramArrayOfAny);
    components = new DynAny[paramArrayOfAny.length];
    anys = paramArrayOfAny;
    TypeCode localTypeCode = getContentType();
    for (int i = 0; i < paramArrayOfAny.length; i++) {
      if (paramArrayOfAny[i] != null)
      {
        if (!paramArrayOfAny[i].type().equal(localTypeCode))
        {
          clearData();
          throw new TypeMismatch();
        }
        try
        {
          components[i] = DynAnyUtil.createMostDerivedDynAny(paramArrayOfAny[i], orb, false);
        }
        catch (InconsistentTypeCode localInconsistentTypeCode)
        {
          throw new InvalidValue();
        }
      }
      else
      {
        clearData();
        throw new InvalidValue();
      }
    }
    index = (paramArrayOfAny.length == 0 ? -1 : 0);
    representations = 4;
  }
  
  public DynAny[] get_elements_as_dyn_any()
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    return checkInitComponents() ? components : null;
  }
  
  public void set_elements_as_dyn_any(DynAny[] paramArrayOfDynAny)
    throws TypeMismatch, InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    checkValue(paramArrayOfDynAny);
    components = (paramArrayOfDynAny == null ? emptyComponents : paramArrayOfDynAny);
    anys = new Any[paramArrayOfDynAny.length];
    TypeCode localTypeCode = getContentType();
    for (int i = 0; i < paramArrayOfDynAny.length; i++) {
      if (paramArrayOfDynAny[i] != null)
      {
        if (!paramArrayOfDynAny[i].type().equal(localTypeCode))
        {
          clearData();
          throw new TypeMismatch();
        }
        anys[i] = getAny(paramArrayOfDynAny[i]);
      }
      else
      {
        clearData();
        throw new InvalidValue();
      }
    }
    index = (paramArrayOfDynAny.length == 0 ? -1 : 0);
    representations = 4;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\dynamicany\DynAnyCollectionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */