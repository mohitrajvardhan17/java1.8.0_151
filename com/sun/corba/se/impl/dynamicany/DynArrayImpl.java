package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynArray;

public class DynArrayImpl
  extends DynAnyCollectionImpl
  implements DynArray
{
  private DynArrayImpl()
  {
    this(null, (Any)null, false);
  }
  
  protected DynArrayImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    super(paramORB, paramAny, paramBoolean);
  }
  
  protected DynArrayImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    super(paramORB, paramTypeCode);
  }
  
  protected boolean initializeComponentsFromAny()
  {
    TypeCode localTypeCode1 = any.type();
    int i = getBound();
    TypeCode localTypeCode2 = getContentType();
    InputStream localInputStream;
    try
    {
      localInputStream = any.create_input_stream();
    }
    catch (BAD_OPERATION localBAD_OPERATION)
    {
      return false;
    }
    components = new DynAny[i];
    anys = new Any[i];
    for (int j = 0; j < i; j++)
    {
      anys[j] = DynAnyUtil.extractAnyFromStream(localTypeCode2, localInputStream, orb);
      try
      {
        components[j] = DynAnyUtil.createMostDerivedDynAny(anys[j], orb, false);
      }
      catch (InconsistentTypeCode localInconsistentTypeCode) {}
    }
    return true;
  }
  
  protected boolean initializeComponentsFromTypeCode()
  {
    TypeCode localTypeCode1 = any.type();
    int i = getBound();
    TypeCode localTypeCode2 = getContentType();
    components = new DynAny[i];
    anys = new Any[i];
    for (int j = 0; j < i; j++) {
      createDefaultComponentAt(j, localTypeCode2);
    }
    return true;
  }
  
  protected void checkValue(Object[] paramArrayOfObject)
    throws InvalidValue
  {
    if ((paramArrayOfObject == null) || (paramArrayOfObject.length != getBound())) {
      throw new InvalidValue();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\dynamicany\DynArrayImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */