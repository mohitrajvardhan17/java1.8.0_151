package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.NameValuePair;

abstract class DynAnyComplexImpl
  extends DynAnyConstructedImpl
{
  String[] names = null;
  NameValuePair[] nameValuePairs = null;
  NameDynAnyPair[] nameDynAnyPairs = null;
  
  private DynAnyComplexImpl()
  {
    this(null, (Any)null, false);
  }
  
  protected DynAnyComplexImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    super(paramORB, paramAny, paramBoolean);
  }
  
  protected DynAnyComplexImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    super(paramORB, paramTypeCode);
    index = 0;
  }
  
  public String current_member_name()
    throws TypeMismatch, InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    if ((!checkInitComponents()) || (index < 0) || (index >= names.length)) {
      throw new InvalidValue();
    }
    return names[index];
  }
  
  public TCKind current_member_kind()
    throws TypeMismatch, InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    if ((!checkInitComponents()) || (index < 0) || (index >= components.length)) {
      throw new InvalidValue();
    }
    return components[index].type().kind();
  }
  
  public void set_members(NameValuePair[] paramArrayOfNameValuePair)
    throws TypeMismatch, InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    if ((paramArrayOfNameValuePair == null) || (paramArrayOfNameValuePair.length == 0))
    {
      clearData();
      return;
    }
    DynAny localDynAny = null;
    TypeCode localTypeCode1 = any.type();
    int i = 0;
    try
    {
      i = localTypeCode1.member_count();
    }
    catch (BadKind localBadKind1) {}
    if (i != paramArrayOfNameValuePair.length)
    {
      clearData();
      throw new InvalidValue();
    }
    allocComponents(paramArrayOfNameValuePair);
    for (int j = 0; j < paramArrayOfNameValuePair.length; j++) {
      if (paramArrayOfNameValuePair[j] != null)
      {
        String str1 = id;
        String str2 = null;
        try
        {
          str2 = localTypeCode1.member_name(j);
        }
        catch (BadKind localBadKind2) {}catch (Bounds localBounds1) {}
        if ((!str2.equals(str1)) && (!str1.equals("")))
        {
          clearData();
          throw new TypeMismatch();
        }
        Any localAny = value;
        TypeCode localTypeCode2 = null;
        try
        {
          localTypeCode2 = localTypeCode1.member_type(j);
        }
        catch (BadKind localBadKind3) {}catch (Bounds localBounds2) {}
        if (!localTypeCode2.equal(localAny.type()))
        {
          clearData();
          throw new TypeMismatch();
        }
        try
        {
          localDynAny = DynAnyUtil.createMostDerivedDynAny(localAny, orb, false);
        }
        catch (InconsistentTypeCode localInconsistentTypeCode)
        {
          throw new InvalidValue();
        }
        addComponent(j, str1, localAny, localDynAny);
      }
      else
      {
        clearData();
        throw new InvalidValue();
      }
    }
    index = (paramArrayOfNameValuePair.length == 0 ? -1 : 0);
    representations = 4;
  }
  
  public void set_members_as_dyn_any(NameDynAnyPair[] paramArrayOfNameDynAnyPair)
    throws TypeMismatch, InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    if ((paramArrayOfNameDynAnyPair == null) || (paramArrayOfNameDynAnyPair.length == 0))
    {
      clearData();
      return;
    }
    TypeCode localTypeCode1 = any.type();
    int i = 0;
    try
    {
      i = localTypeCode1.member_count();
    }
    catch (BadKind localBadKind1) {}
    if (i != paramArrayOfNameDynAnyPair.length)
    {
      clearData();
      throw new InvalidValue();
    }
    allocComponents(paramArrayOfNameDynAnyPair);
    for (int j = 0; j < paramArrayOfNameDynAnyPair.length; j++) {
      if (paramArrayOfNameDynAnyPair[j] != null)
      {
        String str1 = id;
        String str2 = null;
        try
        {
          str2 = localTypeCode1.member_name(j);
        }
        catch (BadKind localBadKind2) {}catch (Bounds localBounds1) {}
        if ((!str2.equals(str1)) && (!str1.equals("")))
        {
          clearData();
          throw new TypeMismatch();
        }
        DynAny localDynAny = value;
        Any localAny = getAny(localDynAny);
        TypeCode localTypeCode2 = null;
        try
        {
          localTypeCode2 = localTypeCode1.member_type(j);
        }
        catch (BadKind localBadKind3) {}catch (Bounds localBounds2) {}
        if (!localTypeCode2.equal(localAny.type()))
        {
          clearData();
          throw new TypeMismatch();
        }
        addComponent(j, str1, localAny, localDynAny);
      }
      else
      {
        clearData();
        throw new InvalidValue();
      }
    }
    index = (paramArrayOfNameDynAnyPair.length == 0 ? -1 : 0);
    representations = 4;
  }
  
  private void allocComponents(int paramInt)
  {
    components = new DynAny[paramInt];
    names = new String[paramInt];
    nameValuePairs = new NameValuePair[paramInt];
    nameDynAnyPairs = new NameDynAnyPair[paramInt];
    for (int i = 0; i < paramInt; i++)
    {
      nameValuePairs[i] = new NameValuePair();
      nameDynAnyPairs[i] = new NameDynAnyPair();
    }
  }
  
  private void allocComponents(NameValuePair[] paramArrayOfNameValuePair)
  {
    components = new DynAny[paramArrayOfNameValuePair.length];
    names = new String[paramArrayOfNameValuePair.length];
    nameValuePairs = paramArrayOfNameValuePair;
    nameDynAnyPairs = new NameDynAnyPair[paramArrayOfNameValuePair.length];
    for (int i = 0; i < paramArrayOfNameValuePair.length; i++) {
      nameDynAnyPairs[i] = new NameDynAnyPair();
    }
  }
  
  private void allocComponents(NameDynAnyPair[] paramArrayOfNameDynAnyPair)
  {
    components = new DynAny[paramArrayOfNameDynAnyPair.length];
    names = new String[paramArrayOfNameDynAnyPair.length];
    nameValuePairs = new NameValuePair[paramArrayOfNameDynAnyPair.length];
    for (int i = 0; i < paramArrayOfNameDynAnyPair.length; i++) {
      nameValuePairs[i] = new NameValuePair();
    }
    nameDynAnyPairs = paramArrayOfNameDynAnyPair;
  }
  
  private void addComponent(int paramInt, String paramString, Any paramAny, DynAny paramDynAny)
  {
    components[paramInt] = paramDynAny;
    names[paramInt] = (paramString != null ? paramString : "");
    nameValuePairs[paramInt].id = paramString;
    nameValuePairs[paramInt].value = paramAny;
    nameDynAnyPairs[paramInt].id = paramString;
    nameDynAnyPairs[paramInt].value = paramDynAny;
    if ((paramDynAny instanceof DynAnyImpl)) {
      ((DynAnyImpl)paramDynAny).setStatus((byte)1);
    }
  }
  
  protected boolean initializeComponentsFromAny()
  {
    TypeCode localTypeCode1 = any.type();
    TypeCode localTypeCode2 = null;
    DynAny localDynAny = null;
    String str = null;
    int i = 0;
    try
    {
      i = localTypeCode1.member_count();
    }
    catch (BadKind localBadKind1) {}
    InputStream localInputStream = any.create_input_stream();
    allocComponents(i);
    for (int j = 0; j < i; j++)
    {
      try
      {
        str = localTypeCode1.member_name(j);
        localTypeCode2 = localTypeCode1.member_type(j);
      }
      catch (BadKind localBadKind2) {}catch (Bounds localBounds) {}
      Any localAny = DynAnyUtil.extractAnyFromStream(localTypeCode2, localInputStream, orb);
      try
      {
        localDynAny = DynAnyUtil.createMostDerivedDynAny(localAny, orb, false);
      }
      catch (InconsistentTypeCode localInconsistentTypeCode) {}
      addComponent(j, str, localAny, localDynAny);
    }
    return true;
  }
  
  protected boolean initializeComponentsFromTypeCode()
  {
    TypeCode localTypeCode1 = any.type();
    TypeCode localTypeCode2 = null;
    DynAny localDynAny = null;
    int i = 0;
    try
    {
      i = localTypeCode1.member_count();
    }
    catch (BadKind localBadKind1) {}
    allocComponents(i);
    for (int j = 0; j < i; j++)
    {
      String str = null;
      try
      {
        str = localTypeCode1.member_name(j);
        localTypeCode2 = localTypeCode1.member_type(j);
      }
      catch (BadKind localBadKind2) {}catch (Bounds localBounds) {}
      try
      {
        localDynAny = DynAnyUtil.createMostDerivedDynAny(localTypeCode2, orb);
      }
      catch (InconsistentTypeCode localInconsistentTypeCode) {}
      Any localAny = getAny(localDynAny);
      addComponent(j, str, localAny, localDynAny);
    }
    return true;
  }
  
  protected void clearData()
  {
    super.clearData();
    names = null;
    nameValuePairs = null;
    nameDynAnyPairs = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\dynamicany\DynAnyComplexImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */