package org.omg.DynamicAny;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Properties;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ServantObject;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public class _DynAnyStub
  extends ObjectImpl
  implements DynAny
{
  public static final Class _opsClass = DynAnyOperations.class;
  private static String[] __ids = { "IDL:omg.org/DynamicAny/DynAny:1.0" };
  
  public _DynAnyStub() {}
  
  public TypeCode type()
  {
    ServantObject localServantObject = _servant_preinvoke("type", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      TypeCode localTypeCode = localDynAnyOperations.type();
      return localTypeCode;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void assign(DynAny paramDynAny)
    throws TypeMismatch
  {
    ServantObject localServantObject = _servant_preinvoke("assign", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.assign(paramDynAny);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void from_any(Any paramAny)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("from_any", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.from_any(paramAny);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public Any to_any()
  {
    ServantObject localServantObject = _servant_preinvoke("to_any", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      Any localAny = localDynAnyOperations.to_any();
      return localAny;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public boolean equal(DynAny paramDynAny)
  {
    ServantObject localServantObject = _servant_preinvoke("equal", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      boolean bool = localDynAnyOperations.equal(paramDynAny);
      return bool;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  /* Error */
  public void destroy()
  {
    // Byte code:
    //   0: aload_0
    //   1: ldc 6
    //   3: getstatic 271	org/omg/DynamicAny/_DynAnyStub:_opsClass	Ljava/lang/Class;
    //   6: invokevirtual 284	org/omg/DynamicAny/_DynAnyStub:_servant_preinvoke	(Ljava/lang/String;Ljava/lang/Class;)Lorg/omg/CORBA/portable/ServantObject;
    //   9: astore_1
    //   10: aload_1
    //   11: getfield 270	org/omg/CORBA/portable/ServantObject:servant	Ljava/lang/Object;
    //   14: checkcast 52	org/omg/DynamicAny/DynAnyOperations
    //   17: astore_2
    //   18: aload_2
    //   19: invokeinterface 297 1 0
    //   24: aload_0
    //   25: aload_1
    //   26: invokevirtual 283	org/omg/DynamicAny/_DynAnyStub:_servant_postinvoke	(Lorg/omg/CORBA/portable/ServantObject;)V
    //   29: goto +11 -> 40
    //   32: astore_3
    //   33: aload_0
    //   34: aload_1
    //   35: invokevirtual 283	org/omg/DynamicAny/_DynAnyStub:_servant_postinvoke	(Lorg/omg/CORBA/portable/ServantObject;)V
    //   38: aload_3
    //   39: athrow
    //   40: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	41	0	this	_DynAnyStub
    //   9	26	1	localServantObject	ServantObject
    //   17	2	2	localDynAnyOperations	DynAnyOperations
    //   32	7	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   18	24	32	finally
  }
  
  public DynAny copy()
  {
    ServantObject localServantObject = _servant_preinvoke("copy", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      DynAny localDynAny = localDynAnyOperations.copy();
      return localDynAny;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_boolean(boolean paramBoolean)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_boolean", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_boolean(paramBoolean);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_octet(byte paramByte)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_octet", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_octet(paramByte);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_char(char paramChar)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_char", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_char(paramChar);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_short(short paramShort)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_short", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_short(paramShort);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_ushort(short paramShort)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_ushort", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_ushort(paramShort);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_long(int paramInt)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_long", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_long(paramInt);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_ulong(int paramInt)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_ulong", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_ulong(paramInt);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_float(float paramFloat)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_float", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_float(paramFloat);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_double(double paramDouble)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_double", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_double(paramDouble);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_string(String paramString)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_string", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_string(paramString);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_reference(org.omg.CORBA.Object paramObject)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_reference", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_reference(paramObject);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_typecode(TypeCode paramTypeCode)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_typecode", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_typecode(paramTypeCode);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_longlong(long paramLong)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_longlong", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_longlong(paramLong);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_ulonglong(long paramLong)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_ulonglong", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_ulonglong(paramLong);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_wchar(char paramChar)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_wchar", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_wchar(paramChar);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_wstring(String paramString)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_wstring", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_wstring(paramString);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_any(Any paramAny)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_any", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_any(paramAny);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_dyn_any(DynAny paramDynAny)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_dyn_any", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_dyn_any(paramDynAny);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public void insert_val(Serializable paramSerializable)
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("insert_val", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      localDynAnyOperations.insert_val(paramSerializable);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public boolean get_boolean()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_boolean", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      boolean bool = localDynAnyOperations.get_boolean();
      return bool;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public byte get_octet()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_octet", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      byte b = localDynAnyOperations.get_octet();
      return b;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public char get_char()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_char", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      char c = localDynAnyOperations.get_char();
      return c;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public short get_short()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_short", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      short s = localDynAnyOperations.get_short();
      return s;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public short get_ushort()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_ushort", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      short s = localDynAnyOperations.get_ushort();
      return s;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public int get_long()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_long", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      int i = localDynAnyOperations.get_long();
      return i;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public int get_ulong()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_ulong", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      int i = localDynAnyOperations.get_ulong();
      return i;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public float get_float()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_float", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      float f = localDynAnyOperations.get_float();
      return f;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public double get_double()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_double", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      double d = localDynAnyOperations.get_double();
      return d;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public String get_string()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_string", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      String str = localDynAnyOperations.get_string();
      return str;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public org.omg.CORBA.Object get_reference()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_reference", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      org.omg.CORBA.Object localObject = localDynAnyOperations.get_reference();
      return localObject;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public TypeCode get_typecode()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_typecode", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      TypeCode localTypeCode = localDynAnyOperations.get_typecode();
      return localTypeCode;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public long get_longlong()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_longlong", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      long l = localDynAnyOperations.get_longlong();
      return l;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public long get_ulonglong()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_ulonglong", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      long l = localDynAnyOperations.get_ulonglong();
      return l;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public char get_wchar()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_wchar", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      char c = localDynAnyOperations.get_wchar();
      return c;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public String get_wstring()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_wstring", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      String str = localDynAnyOperations.get_wstring();
      return str;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public Any get_any()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_any", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      Any localAny = localDynAnyOperations.get_any();
      return localAny;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public DynAny get_dyn_any()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_dyn_any", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      DynAny localDynAny = localDynAnyOperations.get_dyn_any();
      return localDynAny;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public Serializable get_val()
    throws TypeMismatch, InvalidValue
  {
    ServantObject localServantObject = _servant_preinvoke("get_val", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      Serializable localSerializable = localDynAnyOperations.get_val();
      return localSerializable;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public boolean seek(int paramInt)
  {
    ServantObject localServantObject = _servant_preinvoke("seek", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      boolean bool = localDynAnyOperations.seek(paramInt);
      return bool;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  /* Error */
  public void rewind()
  {
    // Byte code:
    //   0: aload_0
    //   1: ldc 48
    //   3: getstatic 271	org/omg/DynamicAny/_DynAnyStub:_opsClass	Ljava/lang/Class;
    //   6: invokevirtual 284	org/omg/DynamicAny/_DynAnyStub:_servant_preinvoke	(Ljava/lang/String;Ljava/lang/Class;)Lorg/omg/CORBA/portable/ServantObject;
    //   9: astore_1
    //   10: aload_1
    //   11: getfield 270	org/omg/CORBA/portable/ServantObject:servant	Ljava/lang/Object;
    //   14: checkcast 52	org/omg/DynamicAny/DynAnyOperations
    //   17: astore_2
    //   18: aload_2
    //   19: invokeinterface 298 1 0
    //   24: aload_0
    //   25: aload_1
    //   26: invokevirtual 283	org/omg/DynamicAny/_DynAnyStub:_servant_postinvoke	(Lorg/omg/CORBA/portable/ServantObject;)V
    //   29: goto +11 -> 40
    //   32: astore_3
    //   33: aload_0
    //   34: aload_1
    //   35: invokevirtual 283	org/omg/DynamicAny/_DynAnyStub:_servant_postinvoke	(Lorg/omg/CORBA/portable/ServantObject;)V
    //   38: aload_3
    //   39: athrow
    //   40: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	41	0	this	_DynAnyStub
    //   9	26	1	localServantObject	ServantObject
    //   17	2	2	localDynAnyOperations	DynAnyOperations
    //   32	7	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   18	24	32	finally
  }
  
  public boolean next()
  {
    ServantObject localServantObject = _servant_preinvoke("next", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      boolean bool = localDynAnyOperations.next();
      return bool;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public int component_count()
  {
    ServantObject localServantObject = _servant_preinvoke("component_count", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      int i = localDynAnyOperations.component_count();
      return i;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public DynAny current_component()
    throws TypeMismatch
  {
    ServantObject localServantObject = _servant_preinvoke("current_component", _opsClass);
    DynAnyOperations localDynAnyOperations = (DynAnyOperations)servant;
    try
    {
      DynAny localDynAny = localDynAnyOperations.current_component();
      return localDynAny;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public String[] _ids()
  {
    return (String[])__ids.clone();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException
  {
    String str = paramObjectInputStream.readUTF();
    String[] arrayOfString = null;
    Properties localProperties = null;
    ORB localORB = ORB.init(arrayOfString, localProperties);
    try
    {
      org.omg.CORBA.Object localObject = localORB.string_to_object(str);
      Delegate localDelegate = ((ObjectImpl)localObject)._get_delegate();
      _set_delegate(localDelegate);
    }
    finally
    {
      localORB.destroy();
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    String[] arrayOfString = null;
    Properties localProperties = null;
    ORB localORB = ORB.init(arrayOfString, localProperties);
    try
    {
      String str = localORB.object_to_string(this);
      paramObjectOutputStream.writeUTF(str);
    }
    finally
    {
      localORB.destroy();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\_DynAnyStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */