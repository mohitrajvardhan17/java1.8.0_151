package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynSequence;

public class DynSequenceImpl
  extends DynAnyCollectionImpl
  implements DynSequence
{
  private DynSequenceImpl()
  {
    this(null, (Any)null, false);
  }
  
  protected DynSequenceImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    super(paramORB, paramAny, paramBoolean);
  }
  
  protected DynSequenceImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    super(paramORB, paramTypeCode);
  }
  
  protected boolean initializeComponentsFromAny()
  {
    TypeCode localTypeCode1 = any.type();
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
    int i = localInputStream.read_long();
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
    components = new DynAny[0];
    anys = new Any[0];
    return true;
  }
  
  protected boolean initializeAnyFromComponents()
  {
    OutputStream localOutputStream = any.create_output_stream();
    localOutputStream.write_long(components.length);
    for (int i = 0; i < components.length; i++) {
      if ((components[i] instanceof DynAnyImpl)) {
        ((DynAnyImpl)components[i]).writeAny(localOutputStream);
      } else {
        components[i].to_any().write_value(localOutputStream);
      }
    }
    any.read_value(localOutputStream.create_input_stream(), any.type());
    return true;
  }
  
  public int get_length()
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    return checkInitComponents() ? components.length : 0;
  }
  
  public void set_length(int paramInt)
    throws InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    int i = getBound();
    if ((i > 0) && (paramInt > i)) {
      throw new InvalidValue();
    }
    checkInitComponents();
    int j = components.length;
    DynAny[] arrayOfDynAny;
    Any[] arrayOfAny;
    if (paramInt > j)
    {
      arrayOfDynAny = new DynAny[paramInt];
      arrayOfAny = new Any[paramInt];
      System.arraycopy(components, 0, arrayOfDynAny, 0, j);
      System.arraycopy(anys, 0, arrayOfAny, 0, j);
      components = arrayOfDynAny;
      anys = arrayOfAny;
      TypeCode localTypeCode = getContentType();
      for (int k = j; k < paramInt; k++) {
        createDefaultComponentAt(k, localTypeCode);
      }
      if (index == -1) {
        index = j;
      }
    }
    else if (paramInt < j)
    {
      arrayOfDynAny = new DynAny[paramInt];
      arrayOfAny = new Any[paramInt];
      System.arraycopy(components, 0, arrayOfDynAny, 0, paramInt);
      System.arraycopy(anys, 0, arrayOfAny, 0, paramInt);
      components = arrayOfDynAny;
      anys = arrayOfAny;
      if ((paramInt == 0) || (index >= paramInt)) {
        index = -1;
      }
    }
    else if ((index == -1) && (paramInt > 0))
    {
      index = 0;
    }
  }
  
  protected void checkValue(Object[] paramArrayOfObject)
    throws InvalidValue
  {
    if ((paramArrayOfObject == null) || (paramArrayOfObject.length == 0))
    {
      clearData();
      index = -1;
      return;
    }
    index = 0;
    int i = getBound();
    if ((i > 0) && (paramArrayOfObject.length > i)) {
      throw new InvalidValue();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\dynamicany\DynSequenceImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */