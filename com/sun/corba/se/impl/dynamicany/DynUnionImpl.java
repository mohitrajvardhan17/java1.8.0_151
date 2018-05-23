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
import org.omg.DynamicAny.DynUnion;

public class DynUnionImpl
  extends DynAnyConstructedImpl
  implements DynUnion
{
  DynAny discriminator = null;
  DynAny currentMember = null;
  int currentMemberIndex = -1;
  
  private DynUnionImpl()
  {
    this(null, (Any)null, false);
  }
  
  protected DynUnionImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    super(paramORB, paramAny, paramBoolean);
  }
  
  protected DynUnionImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    super(paramORB, paramTypeCode);
  }
  
  protected boolean initializeComponentsFromAny()
  {
    try
    {
      InputStream localInputStream = any.create_input_stream();
      Any localAny1 = DynAnyUtil.extractAnyFromStream(discriminatorType(), localInputStream, orb);
      discriminator = DynAnyUtil.createMostDerivedDynAny(localAny1, orb, false);
      currentMemberIndex = currentUnionMemberIndex(localAny1);
      Any localAny2 = DynAnyUtil.extractAnyFromStream(memberType(currentMemberIndex), localInputStream, orb);
      currentMember = DynAnyUtil.createMostDerivedDynAny(localAny2, orb, false);
      components = new DynAny[] { discriminator, currentMember };
    }
    catch (InconsistentTypeCode localInconsistentTypeCode) {}
    return true;
  }
  
  protected boolean initializeComponentsFromTypeCode()
  {
    try
    {
      discriminator = DynAnyUtil.createMostDerivedDynAny(memberLabel(0), orb, false);
      index = 0;
      currentMemberIndex = 0;
      currentMember = DynAnyUtil.createMostDerivedDynAny(memberType(0), orb);
      components = new DynAny[] { discriminator, currentMember };
    }
    catch (InconsistentTypeCode localInconsistentTypeCode) {}
    return true;
  }
  
  private TypeCode discriminatorType()
  {
    TypeCode localTypeCode = null;
    try
    {
      localTypeCode = any.type().discriminator_type();
    }
    catch (BadKind localBadKind) {}
    return localTypeCode;
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
  
  private Any memberLabel(int paramInt)
  {
    Any localAny = null;
    try
    {
      localAny = any.type().member_label(paramInt);
    }
    catch (BadKind localBadKind) {}catch (Bounds localBounds) {}
    return localAny;
  }
  
  private TypeCode memberType(int paramInt)
  {
    TypeCode localTypeCode = null;
    try
    {
      localTypeCode = any.type().member_type(paramInt);
    }
    catch (BadKind localBadKind) {}catch (Bounds localBounds) {}
    return localTypeCode;
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
  
  private int defaultIndex()
  {
    int i = -1;
    try
    {
      i = any.type().default_index();
    }
    catch (BadKind localBadKind) {}
    return i;
  }
  
  private int currentUnionMemberIndex(Any paramAny)
  {
    int i = memberCount();
    for (int j = 0; j < i; j++)
    {
      Any localAny = memberLabel(j);
      if (localAny.equal(paramAny)) {
        return j;
      }
    }
    if (defaultIndex() != -1) {
      return defaultIndex();
    }
    return -1;
  }
  
  protected void clearData()
  {
    super.clearData();
    discriminator = null;
    currentMember.destroy();
    currentMember = null;
    currentMemberIndex = -1;
  }
  
  public DynAny get_discriminator()
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    return checkInitComponents() ? discriminator : null;
  }
  
  public void set_discriminator(DynAny paramDynAny)
    throws TypeMismatch
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    if (!paramDynAny.type().equal(discriminatorType())) {
      throw new TypeMismatch();
    }
    paramDynAny = DynAnyUtil.convertToNative(paramDynAny, orb);
    Any localAny = getAny(paramDynAny);
    int i = currentUnionMemberIndex(localAny);
    if (i == -1)
    {
      clearData();
      index = 0;
    }
    else
    {
      checkInitComponents();
      if ((currentMemberIndex == -1) || (i != currentMemberIndex))
      {
        clearData();
        index = 1;
        currentMemberIndex = i;
        try
        {
          currentMember = DynAnyUtil.createMostDerivedDynAny(memberType(currentMemberIndex), orb);
        }
        catch (InconsistentTypeCode localInconsistentTypeCode) {}
        discriminator = paramDynAny;
        components = new DynAny[] { discriminator, currentMember };
        representations = 4;
      }
    }
  }
  
  public void set_to_default_member()
    throws TypeMismatch
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    int i = defaultIndex();
    if (i == -1) {
      throw new TypeMismatch();
    }
    try
    {
      clearData();
      index = 1;
      currentMemberIndex = i;
      currentMember = DynAnyUtil.createMostDerivedDynAny(memberType(i), orb);
      components = new DynAny[] { discriminator, currentMember };
      Any localAny = orb.create_any();
      localAny.insert_octet((byte)0);
      discriminator = DynAnyUtil.createMostDerivedDynAny(localAny, orb, false);
      representations = 4;
    }
    catch (InconsistentTypeCode localInconsistentTypeCode) {}
  }
  
  public void set_to_no_active_member()
    throws TypeMismatch
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    if (defaultIndex() != -1) {
      throw new TypeMismatch();
    }
    checkInitComponents();
    Any localAny = getAny(discriminator);
    localAny.type(localAny.type());
    index = 0;
    currentMemberIndex = -1;
    currentMember.destroy();
    currentMember = null;
    components[0] = discriminator;
    representations = 4;
  }
  
  public boolean has_no_active_member()
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    if (defaultIndex() != -1) {
      return false;
    }
    checkInitComponents();
    return currentMemberIndex == -1;
  }
  
  public TCKind discriminator_kind()
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    return discriminatorType().kind();
  }
  
  public DynAny member()
    throws InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    if ((!checkInitComponents()) || (currentMemberIndex == -1)) {
      throw new InvalidValue();
    }
    return currentMember;
  }
  
  public String member_name()
    throws InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    if ((!checkInitComponents()) || (currentMemberIndex == -1)) {
      throw new InvalidValue();
    }
    String str = memberName(currentMemberIndex);
    return str == null ? "" : str;
  }
  
  public TCKind member_kind()
    throws InvalidValue
  {
    if (status == 2) {
      throw wrapper.dynAnyDestroyed();
    }
    if ((!checkInitComponents()) || (currentMemberIndex == -1)) {
      throw new InvalidValue();
    }
    return memberType(currentMemberIndex).kind();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\dynamicany\DynUnionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */