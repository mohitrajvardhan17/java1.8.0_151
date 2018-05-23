package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.io.ValueUtility;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.RepositoryIdFactory;
import com.sun.corba.se.impl.orbutil.RepositoryIdStrings;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.Streamable;

public class AnyImpl
  extends Any
{
  private TypeCodeImpl typeCode;
  protected ORB orb;
  private ORBUtilSystemException wrapper;
  private CDRInputStream stream;
  private long value;
  private Object object;
  private boolean isInitialized = false;
  private static final int DEFAULT_BUFFER_SIZE = 32;
  static boolean[] isStreamed = { false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, true, true, false, false, true, true, true, true, false, false, false, false, false, false, false, false, false, false };
  
  static AnyImpl convertToNative(ORB paramORB, Any paramAny)
  {
    if ((paramAny instanceof AnyImpl)) {
      return (AnyImpl)paramAny;
    }
    AnyImpl localAnyImpl = new AnyImpl(paramORB, paramAny);
    typeCode = TypeCodeImpl.convertToNative(paramORB, typeCode);
    return localAnyImpl;
  }
  
  public AnyImpl(ORB paramORB)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.presentation");
    typeCode = paramORB.get_primitive_tc(0);
    stream = null;
    object = null;
    value = 0L;
    isInitialized = true;
  }
  
  public AnyImpl(ORB paramORB, Any paramAny)
  {
    this(paramORB);
    if ((paramAny instanceof AnyImpl))
    {
      AnyImpl localAnyImpl = (AnyImpl)paramAny;
      typeCode = typeCode;
      value = value;
      object = object;
      isInitialized = isInitialized;
      if (stream != null) {
        stream = stream.dup();
      }
    }
    else
    {
      read_value(paramAny.create_input_stream(), paramAny.type());
    }
  }
  
  public TypeCode type()
  {
    return typeCode;
  }
  
  private TypeCode realType()
  {
    return realType(typeCode);
  }
  
  private TypeCode realType(TypeCode paramTypeCode)
  {
    TypeCode localTypeCode = paramTypeCode;
    try
    {
      while (localTypeCode.kind().value() == 21) {
        localTypeCode = localTypeCode.content_type();
      }
    }
    catch (BadKind localBadKind)
    {
      throw wrapper.badkindCannotOccur(localBadKind);
    }
    return localTypeCode;
  }
  
  public void type(TypeCode paramTypeCode)
  {
    typeCode = TypeCodeImpl.convertToNative(orb, paramTypeCode);
    stream = null;
    value = 0L;
    object = null;
    isInitialized = (paramTypeCode.kind().value() == 0);
  }
  
  public boolean equal(Any paramAny)
  {
    if (paramAny == this) {
      return true;
    }
    if (!typeCode.equal(paramAny.type())) {
      return false;
    }
    TypeCode localTypeCode = realType();
    switch (localTypeCode.kind().value())
    {
    case 0: 
    case 1: 
      return true;
    case 2: 
      return extract_short() == paramAny.extract_short();
    case 3: 
      return extract_long() == paramAny.extract_long();
    case 4: 
      return extract_ushort() == paramAny.extract_ushort();
    case 5: 
      return extract_ulong() == paramAny.extract_ulong();
    case 6: 
      return extract_float() == paramAny.extract_float();
    case 7: 
      return extract_double() == paramAny.extract_double();
    case 8: 
      return extract_boolean() == paramAny.extract_boolean();
    case 9: 
      return extract_char() == paramAny.extract_char();
    case 26: 
      return extract_wchar() == paramAny.extract_wchar();
    case 10: 
      return extract_octet() == paramAny.extract_octet();
    case 11: 
      return extract_any().equal(paramAny.extract_any());
    case 12: 
      return extract_TypeCode().equal(paramAny.extract_TypeCode());
    case 18: 
      return extract_string().equals(paramAny.extract_string());
    case 27: 
      return extract_wstring().equals(paramAny.extract_wstring());
    case 23: 
      return extract_longlong() == paramAny.extract_longlong();
    case 24: 
      return extract_ulonglong() == paramAny.extract_ulonglong();
    case 14: 
      return extract_Object().equals(paramAny.extract_Object());
    case 13: 
      return extract_Principal().equals(paramAny.extract_Principal());
    case 17: 
      return extract_long() == paramAny.extract_long();
    case 28: 
      return extract_fixed().compareTo(paramAny.extract_fixed()) == 0;
    case 15: 
    case 16: 
    case 19: 
    case 20: 
    case 22: 
      org.omg.CORBA.portable.InputStream localInputStream1 = create_input_stream();
      org.omg.CORBA.portable.InputStream localInputStream2 = paramAny.create_input_stream();
      return equalMember(localTypeCode, localInputStream1, localInputStream2);
    case 29: 
    case 30: 
      return extract_Value().equals(paramAny.extract_Value());
    case 21: 
      throw wrapper.errorResolvingAlias();
    case 25: 
      throw wrapper.tkLongDoubleNotSupported();
    }
    throw wrapper.typecodeNotSupported();
  }
  
  private boolean equalMember(TypeCode paramTypeCode, org.omg.CORBA.portable.InputStream paramInputStream1, org.omg.CORBA.portable.InputStream paramInputStream2)
  {
    TypeCode localTypeCode = realType(paramTypeCode);
    try
    {
      int j;
      int m;
      switch (localTypeCode.kind().value())
      {
      case 0: 
      case 1: 
        return true;
      case 2: 
        return paramInputStream1.read_short() == paramInputStream2.read_short();
      case 3: 
        return paramInputStream1.read_long() == paramInputStream2.read_long();
      case 4: 
        return paramInputStream1.read_ushort() == paramInputStream2.read_ushort();
      case 5: 
        return paramInputStream1.read_ulong() == paramInputStream2.read_ulong();
      case 6: 
        return paramInputStream1.read_float() == paramInputStream2.read_float();
      case 7: 
        return paramInputStream1.read_double() == paramInputStream2.read_double();
      case 8: 
        return paramInputStream1.read_boolean() == paramInputStream2.read_boolean();
      case 9: 
        return paramInputStream1.read_char() == paramInputStream2.read_char();
      case 26: 
        return paramInputStream1.read_wchar() == paramInputStream2.read_wchar();
      case 10: 
        return paramInputStream1.read_octet() == paramInputStream2.read_octet();
      case 11: 
        return paramInputStream1.read_any().equal(paramInputStream2.read_any());
      case 12: 
        return paramInputStream1.read_TypeCode().equal(paramInputStream2.read_TypeCode());
      case 18: 
        return paramInputStream1.read_string().equals(paramInputStream2.read_string());
      case 27: 
        return paramInputStream1.read_wstring().equals(paramInputStream2.read_wstring());
      case 23: 
        return paramInputStream1.read_longlong() == paramInputStream2.read_longlong();
      case 24: 
        return paramInputStream1.read_ulonglong() == paramInputStream2.read_ulonglong();
      case 14: 
        return paramInputStream1.read_Object().equals(paramInputStream2.read_Object());
      case 13: 
        return paramInputStream1.read_Principal().equals(paramInputStream2.read_Principal());
      case 17: 
        return paramInputStream1.read_long() == paramInputStream2.read_long();
      case 28: 
        return paramInputStream1.read_fixed().compareTo(paramInputStream2.read_fixed()) == 0;
      case 15: 
      case 22: 
        int i = localTypeCode.member_count();
        for (int k = 0; k < i; k++) {
          if (!equalMember(localTypeCode.member_type(k), paramInputStream1, paramInputStream2)) {
            return false;
          }
        }
        return true;
      case 16: 
        Any localAny1 = orb.create_any();
        Any localAny2 = orb.create_any();
        localAny1.read_value(paramInputStream1, localTypeCode.discriminator_type());
        localAny2.read_value(paramInputStream2, localTypeCode.discriminator_type());
        if (!localAny1.equal(localAny2)) {
          return false;
        }
        TypeCodeImpl localTypeCodeImpl = TypeCodeImpl.convertToNative(orb, localTypeCode);
        int n = localTypeCodeImpl.currentUnionMemberIndex(localAny1);
        if (n == -1) {
          throw wrapper.unionDiscriminatorError();
        }
        return equalMember(localTypeCode.member_type(n), paramInputStream1, paramInputStream2);
      case 19: 
        j = paramInputStream1.read_long();
        paramInputStream2.read_long();
        for (m = 0; m < j; m++) {
          if (!equalMember(localTypeCode.content_type(), paramInputStream1, paramInputStream2)) {
            return false;
          }
        }
        return true;
      case 20: 
        j = localTypeCode.member_count();
        for (m = 0; m < j; m++) {
          if (!equalMember(localTypeCode.content_type(), paramInputStream1, paramInputStream2)) {
            return false;
          }
        }
        return true;
      case 29: 
      case 30: 
        org.omg.CORBA_2_3.portable.InputStream localInputStream1 = (org.omg.CORBA_2_3.portable.InputStream)paramInputStream1;
        org.omg.CORBA_2_3.portable.InputStream localInputStream2 = (org.omg.CORBA_2_3.portable.InputStream)paramInputStream2;
        return localInputStream1.read_value().equals(localInputStream2.read_value());
      case 21: 
        throw wrapper.errorResolvingAlias();
      case 25: 
        throw wrapper.tkLongDoubleNotSupported();
      }
      throw wrapper.typecodeNotSupported();
    }
    catch (BadKind localBadKind)
    {
      throw wrapper.badkindCannotOccur();
    }
    catch (Bounds localBounds)
    {
      throw wrapper.boundsCannotOccur();
    }
  }
  
  public org.omg.CORBA.portable.OutputStream create_output_stream()
  {
    final ORB localORB = orb;
    (org.omg.CORBA.portable.OutputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public AnyImpl.AnyOutputStream run()
      {
        return new AnyImpl.AnyOutputStream(localORB);
      }
    });
  }
  
  public org.omg.CORBA.portable.InputStream create_input_stream()
  {
    if (isStreamed[realType().kind().value()] != 0) {
      return stream.dup();
    }
    org.omg.CORBA.portable.OutputStream localOutputStream = orb.create_output_stream();
    TCUtility.marshalIn(localOutputStream, realType(), value, object);
    return localOutputStream.create_input_stream();
  }
  
  public void read_value(org.omg.CORBA.portable.InputStream paramInputStream, TypeCode paramTypeCode)
  {
    typeCode = TypeCodeImpl.convertToNative(orb, paramTypeCode);
    int i = realType().kind().value();
    if (i >= isStreamed.length) {
      throw wrapper.invalidIsstreamedTckind(CompletionStatus.COMPLETED_MAYBE, new Integer(i));
    }
    Object localObject;
    if (isStreamed[i] != 0)
    {
      if ((paramInputStream instanceof AnyInputStream))
      {
        stream = ((CDRInputStream)paramInputStream);
      }
      else
      {
        localObject = (org.omg.CORBA_2_3.portable.OutputStream)orb.create_output_stream();
        typeCode.copy((org.omg.CORBA_2_3.portable.InputStream)paramInputStream, (org.omg.CORBA.portable.OutputStream)localObject);
        stream = ((CDRInputStream)((org.omg.CORBA_2_3.portable.OutputStream)localObject).create_input_stream());
      }
    }
    else
    {
      localObject = new Object[1];
      localObject[0] = object;
      long[] arrayOfLong = new long[1];
      TCUtility.unmarshalIn(paramInputStream, realType(), arrayOfLong, (Object[])localObject);
      value = arrayOfLong[0];
      object = localObject[0];
      stream = null;
    }
    isInitialized = true;
  }
  
  public void write_value(org.omg.CORBA.portable.OutputStream paramOutputStream)
  {
    if (isStreamed[realType().kind().value()] != 0) {
      typeCode.copy(stream.dup(), paramOutputStream);
    } else {
      TCUtility.marshalIn(paramOutputStream, realType(), value, object);
    }
  }
  
  public void insert_Streamable(Streamable paramStreamable)
  {
    typeCode = TypeCodeImpl.convertToNative(orb, paramStreamable._type());
    object = paramStreamable;
    isInitialized = true;
  }
  
  public Streamable extract_Streamable()
  {
    return (Streamable)object;
  }
  
  public void insert_short(short paramShort)
  {
    typeCode = orb.get_primitive_tc(2);
    value = paramShort;
    isInitialized = true;
  }
  
  private String getTCKindName(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < TypeCodeImpl.kindNames.length)) {
      return TypeCodeImpl.kindNames[paramInt];
    }
    return "UNKNOWN(" + paramInt + ")";
  }
  
  private void checkExtractBadOperation(int paramInt)
  {
    if (!isInitialized) {
      throw wrapper.extractNotInitialized();
    }
    int i = realType().kind().value();
    if (i != paramInt)
    {
      String str1 = getTCKindName(i);
      String str2 = getTCKindName(paramInt);
      throw wrapper.extractWrongType(str2, str1);
    }
  }
  
  private void checkExtractBadOperationList(int[] paramArrayOfInt)
  {
    if (!isInitialized) {
      throw wrapper.extractNotInitialized();
    }
    int i = realType().kind().value();
    for (int j = 0; j < paramArrayOfInt.length; j++) {
      if (i == paramArrayOfInt[j]) {
        return;
      }
    }
    ArrayList localArrayList = new ArrayList();
    for (int k = 0; k < paramArrayOfInt.length; k++) {
      localArrayList.add(getTCKindName(paramArrayOfInt[k]));
    }
    String str = getTCKindName(i);
    throw wrapper.extractWrongTypeList(localArrayList, str);
  }
  
  public short extract_short()
  {
    checkExtractBadOperation(2);
    return (short)(int)value;
  }
  
  public void insert_long(int paramInt)
  {
    int i = realType().kind().value();
    if ((i != 3) && (i != 17)) {
      typeCode = orb.get_primitive_tc(3);
    }
    value = paramInt;
    isInitialized = true;
  }
  
  public int extract_long()
  {
    checkExtractBadOperationList(new int[] { 3, 17 });
    return (int)value;
  }
  
  public void insert_ushort(short paramShort)
  {
    typeCode = orb.get_primitive_tc(4);
    value = paramShort;
    isInitialized = true;
  }
  
  public short extract_ushort()
  {
    checkExtractBadOperation(4);
    return (short)(int)value;
  }
  
  public void insert_ulong(int paramInt)
  {
    typeCode = orb.get_primitive_tc(5);
    value = paramInt;
    isInitialized = true;
  }
  
  public int extract_ulong()
  {
    checkExtractBadOperation(5);
    return (int)value;
  }
  
  public void insert_float(float paramFloat)
  {
    typeCode = orb.get_primitive_tc(6);
    value = Float.floatToIntBits(paramFloat);
    isInitialized = true;
  }
  
  public float extract_float()
  {
    checkExtractBadOperation(6);
    return Float.intBitsToFloat((int)value);
  }
  
  public void insert_double(double paramDouble)
  {
    typeCode = orb.get_primitive_tc(7);
    value = Double.doubleToLongBits(paramDouble);
    isInitialized = true;
  }
  
  public double extract_double()
  {
    checkExtractBadOperation(7);
    return Double.longBitsToDouble(value);
  }
  
  public void insert_longlong(long paramLong)
  {
    typeCode = orb.get_primitive_tc(23);
    value = paramLong;
    isInitialized = true;
  }
  
  public long extract_longlong()
  {
    checkExtractBadOperation(23);
    return value;
  }
  
  public void insert_ulonglong(long paramLong)
  {
    typeCode = orb.get_primitive_tc(24);
    value = paramLong;
    isInitialized = true;
  }
  
  public long extract_ulonglong()
  {
    checkExtractBadOperation(24);
    return value;
  }
  
  public void insert_boolean(boolean paramBoolean)
  {
    typeCode = orb.get_primitive_tc(8);
    value = (paramBoolean ? 1L : 0L);
    isInitialized = true;
  }
  
  public boolean extract_boolean()
  {
    checkExtractBadOperation(8);
    return value != 0L;
  }
  
  public void insert_char(char paramChar)
  {
    typeCode = orb.get_primitive_tc(9);
    value = paramChar;
    isInitialized = true;
  }
  
  public char extract_char()
  {
    checkExtractBadOperation(9);
    return (char)(int)value;
  }
  
  public void insert_wchar(char paramChar)
  {
    typeCode = orb.get_primitive_tc(26);
    value = paramChar;
    isInitialized = true;
  }
  
  public char extract_wchar()
  {
    checkExtractBadOperation(26);
    return (char)(int)value;
  }
  
  public void insert_octet(byte paramByte)
  {
    typeCode = orb.get_primitive_tc(10);
    value = paramByte;
    isInitialized = true;
  }
  
  public byte extract_octet()
  {
    checkExtractBadOperation(10);
    return (byte)(int)value;
  }
  
  public void insert_string(String paramString)
  {
    if (typeCode.kind() == TCKind.tk_string)
    {
      int i = 0;
      try
      {
        i = typeCode.length();
      }
      catch (BadKind localBadKind)
      {
        throw wrapper.badkindCannotOccur();
      }
      if ((i != 0) && (paramString != null) && (paramString.length() > i)) {
        throw wrapper.badStringBounds(new Integer(paramString.length()), new Integer(i));
      }
    }
    else
    {
      typeCode = orb.get_primitive_tc(18);
    }
    object = paramString;
    isInitialized = true;
  }
  
  public String extract_string()
  {
    checkExtractBadOperation(18);
    return (String)object;
  }
  
  public void insert_wstring(String paramString)
  {
    if (typeCode.kind() == TCKind.tk_wstring)
    {
      int i = 0;
      try
      {
        i = typeCode.length();
      }
      catch (BadKind localBadKind)
      {
        throw wrapper.badkindCannotOccur();
      }
      if ((i != 0) && (paramString != null) && (paramString.length() > i)) {
        throw wrapper.badStringBounds(new Integer(paramString.length()), new Integer(i));
      }
    }
    else
    {
      typeCode = orb.get_primitive_tc(27);
    }
    object = paramString;
    isInitialized = true;
  }
  
  public String extract_wstring()
  {
    checkExtractBadOperation(27);
    return (String)object;
  }
  
  public void insert_any(Any paramAny)
  {
    typeCode = orb.get_primitive_tc(11);
    object = paramAny;
    stream = null;
    isInitialized = true;
  }
  
  public Any extract_any()
  {
    checkExtractBadOperation(11);
    return (Any)object;
  }
  
  public void insert_Object(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null)
    {
      typeCode = orb.get_primitive_tc(14);
    }
    else if (StubAdapter.isStub(paramObject))
    {
      String[] arrayOfString = StubAdapter.getTypeIds(paramObject);
      typeCode = new TypeCodeImpl(orb, 14, arrayOfString[0], "");
    }
    else
    {
      throw wrapper.badInsertobjParam(CompletionStatus.COMPLETED_MAYBE, paramObject.getClass().getName());
    }
    object = paramObject;
    isInitialized = true;
  }
  
  public void insert_Object(org.omg.CORBA.Object paramObject, TypeCode paramTypeCode)
  {
    try
    {
      if ((paramTypeCode.id().equals("IDL:omg.org/CORBA/Object:1.0")) || (paramObject._is_a(paramTypeCode.id())))
      {
        typeCode = TypeCodeImpl.convertToNative(orb, paramTypeCode);
        object = paramObject;
      }
      else
      {
        throw wrapper.insertObjectIncompatible();
      }
    }
    catch (Exception localException)
    {
      throw wrapper.insertObjectFailed(localException);
    }
    isInitialized = true;
  }
  
  public org.omg.CORBA.Object extract_Object()
  {
    if (!isInitialized) {
      throw wrapper.extractNotInitialized();
    }
    org.omg.CORBA.Object localObject = null;
    try
    {
      localObject = (org.omg.CORBA.Object)object;
      if ((typeCode.id().equals("IDL:omg.org/CORBA/Object:1.0")) || (localObject._is_a(typeCode.id()))) {
        return localObject;
      }
      throw wrapper.extractObjectIncompatible();
    }
    catch (Exception localException)
    {
      throw wrapper.extractObjectFailed(localException);
    }
  }
  
  public void insert_TypeCode(TypeCode paramTypeCode)
  {
    typeCode = orb.get_primitive_tc(12);
    object = paramTypeCode;
    isInitialized = true;
  }
  
  public TypeCode extract_TypeCode()
  {
    checkExtractBadOperation(12);
    return (TypeCode)object;
  }
  
  @Deprecated
  public void insert_Principal(Principal paramPrincipal)
  {
    typeCode = orb.get_primitive_tc(13);
    object = paramPrincipal;
    isInitialized = true;
  }
  
  @Deprecated
  public Principal extract_Principal()
  {
    checkExtractBadOperation(13);
    return (Principal)object;
  }
  
  public Serializable extract_Value()
  {
    checkExtractBadOperationList(new int[] { 29, 30, 32 });
    return (Serializable)object;
  }
  
  public void insert_Value(Serializable paramSerializable)
  {
    object = paramSerializable;
    TypeCode localTypeCode;
    if (paramSerializable == null) {
      localTypeCode = orb.get_primitive_tc(TCKind.tk_value);
    } else {
      localTypeCode = createTypeCodeForClass(paramSerializable.getClass(), (ORB)ORB.init());
    }
    typeCode = TypeCodeImpl.convertToNative(orb, localTypeCode);
    isInitialized = true;
  }
  
  public void insert_Value(Serializable paramSerializable, TypeCode paramTypeCode)
  {
    object = paramSerializable;
    typeCode = TypeCodeImpl.convertToNative(orb, paramTypeCode);
    isInitialized = true;
  }
  
  public void insert_fixed(BigDecimal paramBigDecimal)
  {
    typeCode = TypeCodeImpl.convertToNative(orb, orb.create_fixed_tc(TypeCodeImpl.digits(paramBigDecimal), TypeCodeImpl.scale(paramBigDecimal)));
    object = paramBigDecimal;
    isInitialized = true;
  }
  
  public void insert_fixed(BigDecimal paramBigDecimal, TypeCode paramTypeCode)
  {
    try
    {
      if ((TypeCodeImpl.digits(paramBigDecimal) > paramTypeCode.fixed_digits()) || (TypeCodeImpl.scale(paramBigDecimal) > paramTypeCode.fixed_scale())) {
        throw wrapper.fixedNotMatch();
      }
    }
    catch (BadKind localBadKind)
    {
      throw wrapper.fixedBadTypecode(localBadKind);
    }
    typeCode = TypeCodeImpl.convertToNative(orb, paramTypeCode);
    object = paramBigDecimal;
    isInitialized = true;
  }
  
  public BigDecimal extract_fixed()
  {
    checkExtractBadOperation(28);
    return (BigDecimal)object;
  }
  
  public TypeCode createTypeCodeForClass(Class paramClass, ORB paramORB)
  {
    TypeCodeImpl localTypeCodeImpl = paramORB.getTypeCodeForClass(paramClass);
    if (localTypeCodeImpl != null) {
      return localTypeCodeImpl;
    }
    RepositoryIdStrings localRepositoryIdStrings = RepositoryIdFactory.getRepIdStringsFactory();
    Object localObject1;
    Object localObject2;
    if (paramClass.isArray())
    {
      localObject1 = paramClass.getComponentType();
      if (((Class)localObject1).isPrimitive()) {
        localObject2 = getPrimitiveTypeCodeForClass((Class)localObject1, paramORB);
      } else {
        localObject2 = createTypeCodeForClass((Class)localObject1, paramORB);
      }
      TypeCode localTypeCode = paramORB.create_sequence_tc(0, (TypeCode)localObject2);
      String str = localRepositoryIdStrings.createForJavaType(paramClass);
      return paramORB.create_value_box_tc(str, "Sequence", localTypeCode);
    }
    if (paramClass == String.class)
    {
      localObject1 = paramORB.create_string_tc(0);
      localObject2 = localRepositoryIdStrings.createForJavaType(paramClass);
      return paramORB.create_value_box_tc((String)localObject2, "StringValue", (TypeCode)localObject1);
    }
    localTypeCodeImpl = (TypeCodeImpl)ValueUtility.createTypeCodeForClass(paramORB, paramClass, ORBUtility.createValueHandler());
    localTypeCodeImpl.setCaching(true);
    paramORB.setTypeCodeForClass(paramClass, localTypeCodeImpl);
    return localTypeCodeImpl;
  }
  
  private TypeCode getPrimitiveTypeCodeForClass(Class paramClass, ORB paramORB)
  {
    if (paramClass == Integer.TYPE) {
      return paramORB.get_primitive_tc(TCKind.tk_long);
    }
    if (paramClass == Byte.TYPE) {
      return paramORB.get_primitive_tc(TCKind.tk_octet);
    }
    if (paramClass == Long.TYPE) {
      return paramORB.get_primitive_tc(TCKind.tk_longlong);
    }
    if (paramClass == Float.TYPE) {
      return paramORB.get_primitive_tc(TCKind.tk_float);
    }
    if (paramClass == Double.TYPE) {
      return paramORB.get_primitive_tc(TCKind.tk_double);
    }
    if (paramClass == Short.TYPE) {
      return paramORB.get_primitive_tc(TCKind.tk_short);
    }
    if (paramClass == Character.TYPE)
    {
      if ((ORBVersionFactory.getFOREIGN().compareTo(paramORB.getORBVersion()) == 0) || (ORBVersionFactory.getNEWER().compareTo(paramORB.getORBVersion()) <= 0)) {
        return paramORB.get_primitive_tc(TCKind.tk_wchar);
      }
      return paramORB.get_primitive_tc(TCKind.tk_char);
    }
    if (paramClass == Boolean.TYPE) {
      return paramORB.get_primitive_tc(TCKind.tk_boolean);
    }
    return paramORB.get_primitive_tc(TCKind.tk_any);
  }
  
  public Any extractAny(TypeCode paramTypeCode, ORB paramORB)
  {
    Any localAny = paramORB.create_any();
    org.omg.CORBA.portable.OutputStream localOutputStream = localAny.create_output_stream();
    TypeCodeImpl.convertToNative(paramORB, paramTypeCode).copy(stream, localOutputStream);
    localAny.read_value(localOutputStream.create_input_stream(), paramTypeCode);
    return localAny;
  }
  
  public static Any extractAnyFromStream(TypeCode paramTypeCode, org.omg.CORBA.portable.InputStream paramInputStream, ORB paramORB)
  {
    Any localAny = paramORB.create_any();
    org.omg.CORBA.portable.OutputStream localOutputStream = localAny.create_output_stream();
    TypeCodeImpl.convertToNative(paramORB, paramTypeCode).copy(paramInputStream, localOutputStream);
    localAny.read_value(localOutputStream.create_input_stream(), paramTypeCode);
    return localAny;
  }
  
  public boolean isInitialized()
  {
    return isInitialized;
  }
  
  private static final class AnyInputStream
    extends EncapsInputStream
  {
    public AnyInputStream(EncapsInputStream paramEncapsInputStream)
    {
      super();
    }
  }
  
  private static final class AnyOutputStream
    extends EncapsOutputStream
  {
    public AnyOutputStream(ORB paramORB)
    {
      super();
    }
    
    public org.omg.CORBA.portable.InputStream create_input_stream()
    {
      final org.omg.CORBA.portable.InputStream localInputStream = super.create_input_stream();
      AnyImpl.AnyInputStream localAnyInputStream = (AnyImpl.AnyInputStream)AccessController.doPrivileged(new PrivilegedAction()
      {
        public AnyImpl.AnyInputStream run()
        {
          return new AnyImpl.AnyInputStream((EncapsInputStream)localInputStream);
        }
      });
      return localAnyInputStream;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\corba\AnyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */