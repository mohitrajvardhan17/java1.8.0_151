package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynEnum;

public class DynEnumImpl
  extends DynAnyBasicImpl
  implements DynEnum
{
  int currentEnumeratorIndex = -1;
  
  private DynEnumImpl()
  {
    this(null, (Any)null, false);
  }
  
  protected DynEnumImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    super(paramORB, paramAny, paramBoolean);
    index = -1;
    try
    {
      currentEnumeratorIndex = any.extract_long();
    }
    catch (BAD_OPERATION localBAD_OPERATION)
    {
      currentEnumeratorIndex = 0;
      any.type(any.type());
      any.insert_long(0);
    }
  }
  
  protected DynEnumImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    super(paramORB, paramTypeCode);
    index = -1;
    currentEnumeratorIndex = 0;
    any.insert_long(0);
  }
  
  private int memberCount()
  {
    int i = 0;
    try
    {
      i = any.type().member_count();
    }
    catch (BadKind localBadKind) {}
    return i;
  }
  
  private String memberName(int paramInt)
  {
    String str = null;
    try
    {
      str = any.type().member_name(paramInt);
    }
    catch (BadKind localBadKind) {}catch (Bounds localBounds) {}
    return str;
  }
  
  private int computeCurrentEnumeratorIndex(String paramString)
  {
    int i = memberCount();
    for (int j = 0; j < i; j++) {
      if (memberName(j).equals(paramString)) {
        return j;
      }
    }
    return -1;
  }
  
  public int component_count()
  {
    return 0;
  }
  
  public DynAny current_component()
    throws TypeMismatch
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    throw new TypeMismatch();
  }
  
  public String get_as_string()
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    return memberName(currentEnumeratorIndex);
  }
  
  public void set_as_string(String paramString)
    throws InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    int i = computeCurrentEnumeratorIndex(paramString);
    if (i == -1) {
      throw new InvalidValue();
    }
    currentEnumeratorIndex = i;
    any.insert_long(i);
  }
  
  public int get_as_ulong()
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    return currentEnumeratorIndex;
  }
  
  public void set_as_ulong(int paramInt)
    throws InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    if ((paramInt < 0) || (paramInt >= memberCount())) {
      throw new InvalidValue();
    }
    currentEnumeratorIndex = paramInt;
    any.insert_long(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\dynamicany\DynEnumImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */