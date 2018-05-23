package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.PrincipalImpl;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.impl.util.RepositoryIdCache;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactory;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactoryFactory;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import org.omg.CORBA.Any;
import org.omg.CORBA.AnySeqHolder;
import org.omg.CORBA.BooleanSeqHolder;
import org.omg.CORBA.CharSeqHolder;
import org.omg.CORBA.DoubleSeqHolder;
import org.omg.CORBA.FloatSeqHolder;
import org.omg.CORBA.LongLongSeqHolder;
import org.omg.CORBA.LongSeqHolder;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA.Principal;
import org.omg.CORBA.ShortSeqHolder;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ULongLongSeqHolder;
import org.omg.CORBA.ULongSeqHolder;
import org.omg.CORBA.UShortSeqHolder;
import org.omg.CORBA.WCharSeqHolder;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.IDLEntity;

public class IDLJavaSerializationInputStream
  extends CDRInputStreamBase
{
  private com.sun.corba.se.spi.orb.ORB orb;
  private int bufSize;
  private ByteBuffer buffer;
  private byte encodingVersion;
  private ObjectInputStream is;
  private _ByteArrayInputStream bis;
  private BufferManagerRead bufferManager;
  private final int directReadLength = 16;
  private boolean markOn;
  private int peekIndex;
  private int peekCount;
  private LinkedList markedItemQ = new LinkedList();
  protected ORBUtilSystemException wrapper;
  
  public IDLJavaSerializationInputStream(byte paramByte)
  {
    encodingVersion = paramByte;
  }
  
  public void init(org.omg.CORBA.ORB paramORB, ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean, BufferManagerRead paramBufferManagerRead)
  {
    orb = ((com.sun.corba.se.spi.orb.ORB)paramORB);
    bufSize = paramInt;
    bufferManager = paramBufferManagerRead;
    buffer = paramByteBuffer;
    wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)paramORB, "rpc.encoding");
    byte[] arrayOfByte;
    if (buffer.hasArray())
    {
      arrayOfByte = buffer.array();
    }
    else
    {
      arrayOfByte = new byte[paramInt];
      buffer.get(arrayOfByte);
    }
    bis = new _ByteArrayInputStream(arrayOfByte);
  }
  
  private void initObjectInputStream()
  {
    if (is != null) {
      throw wrapper.javaStreamInitFailed();
    }
    try
    {
      is = new MarshalObjectInputStream(bis, orb);
    }
    catch (Exception localException)
    {
      throw wrapper.javaStreamInitFailed(localException);
    }
  }
  
  public boolean read_boolean()
  {
    if ((!markOn) && (!markedItemQ.isEmpty())) {
      return ((Boolean)markedItemQ.removeFirst()).booleanValue();
    }
    if ((markOn) && (!markedItemQ.isEmpty()) && (peekIndex < peekCount)) {
      return ((Boolean)markedItemQ.get(peekIndex++)).booleanValue();
    }
    try
    {
      boolean bool = is.readBoolean();
      if (markOn) {
        markedItemQ.addLast(Boolean.valueOf(bool));
      }
      return bool;
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "read_boolean");
    }
  }
  
  public char read_char()
  {
    if ((!markOn) && (!markedItemQ.isEmpty())) {
      return ((Character)markedItemQ.removeFirst()).charValue();
    }
    if ((markOn) && (!markedItemQ.isEmpty()) && (peekIndex < peekCount)) {
      return ((Character)markedItemQ.get(peekIndex++)).charValue();
    }
    try
    {
      char c = is.readChar();
      if (markOn) {
        markedItemQ.addLast(new Character(c));
      }
      return c;
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "read_char");
    }
  }
  
  public char read_wchar()
  {
    return read_char();
  }
  
  public byte read_octet()
  {
    byte b;
    if (bis.getPosition() < 16)
    {
      b = (byte)bis.read();
      if (bis.getPosition() == 16) {
        initObjectInputStream();
      }
      return b;
    }
    if ((!markOn) && (!markedItemQ.isEmpty())) {
      return ((Byte)markedItemQ.removeFirst()).byteValue();
    }
    if ((markOn) && (!markedItemQ.isEmpty()) && (peekIndex < peekCount)) {
      return ((Byte)markedItemQ.get(peekIndex++)).byteValue();
    }
    try
    {
      b = is.readByte();
      if (markOn) {
        markedItemQ.addLast(new Byte(b));
      }
      return b;
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "read_octet");
    }
  }
  
  public short read_short()
  {
    if ((!markOn) && (!markedItemQ.isEmpty())) {
      return ((Short)markedItemQ.removeFirst()).shortValue();
    }
    if ((markOn) && (!markedItemQ.isEmpty()) && (peekIndex < peekCount)) {
      return ((Short)markedItemQ.get(peekIndex++)).shortValue();
    }
    try
    {
      short s = is.readShort();
      if (markOn) {
        markedItemQ.addLast(new Short(s));
      }
      return s;
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "read_short");
    }
  }
  
  public short read_ushort()
  {
    return read_short();
  }
  
  public int read_long()
  {
    int i;
    if (bis.getPosition() < 16)
    {
      i = bis.read() << 24 & 0xFF000000;
      int j = bis.read() << 16 & 0xFF0000;
      int k = bis.read() << 8 & 0xFF00;
      int m = bis.read() << 0 & 0xFF;
      if (bis.getPosition() == 16) {
        initObjectInputStream();
      } else if (bis.getPosition() > 16) {
        wrapper.javaSerializationException("read_long");
      }
      return i | j | k | m;
    }
    if ((!markOn) && (!markedItemQ.isEmpty())) {
      return ((Integer)markedItemQ.removeFirst()).intValue();
    }
    if ((markOn) && (!markedItemQ.isEmpty()) && (peekIndex < peekCount)) {
      return ((Integer)markedItemQ.get(peekIndex++)).intValue();
    }
    try
    {
      i = is.readInt();
      if (markOn) {
        markedItemQ.addLast(new Integer(i));
      }
      return i;
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "read_long");
    }
  }
  
  public int read_ulong()
  {
    return read_long();
  }
  
  public long read_longlong()
  {
    if ((!markOn) && (!markedItemQ.isEmpty())) {
      return ((Long)markedItemQ.removeFirst()).longValue();
    }
    if ((markOn) && (!markedItemQ.isEmpty()) && (peekIndex < peekCount)) {
      return ((Long)markedItemQ.get(peekIndex++)).longValue();
    }
    try
    {
      long l = is.readLong();
      if (markOn) {
        markedItemQ.addLast(new Long(l));
      }
      return l;
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "read_longlong");
    }
  }
  
  public long read_ulonglong()
  {
    return read_longlong();
  }
  
  public float read_float()
  {
    if ((!markOn) && (!markedItemQ.isEmpty())) {
      return ((Float)markedItemQ.removeFirst()).floatValue();
    }
    if ((markOn) && (!markedItemQ.isEmpty()) && (peekIndex < peekCount)) {
      return ((Float)markedItemQ.get(peekIndex++)).floatValue();
    }
    try
    {
      float f = is.readFloat();
      if (markOn) {
        markedItemQ.addLast(new Float(f));
      }
      return f;
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "read_float");
    }
  }
  
  public double read_double()
  {
    if ((!markOn) && (!markedItemQ.isEmpty())) {
      return ((Double)markedItemQ.removeFirst()).doubleValue();
    }
    if ((markOn) && (!markedItemQ.isEmpty()) && (peekIndex < peekCount)) {
      return ((Double)markedItemQ.get(peekIndex++)).doubleValue();
    }
    try
    {
      double d = is.readDouble();
      if (markOn) {
        markedItemQ.addLast(new Double(d));
      }
      return d;
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "read_double");
    }
  }
  
  public String read_string()
  {
    if ((!markOn) && (!markedItemQ.isEmpty())) {
      return (String)markedItemQ.removeFirst();
    }
    if ((markOn) && (!markedItemQ.isEmpty()) && (peekIndex < peekCount)) {
      return (String)markedItemQ.get(peekIndex++);
    }
    try
    {
      String str = is.readUTF();
      if (markOn) {
        markedItemQ.addLast(str);
      }
      return str;
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "read_string");
    }
  }
  
  public String read_wstring()
  {
    if ((!markOn) && (!markedItemQ.isEmpty())) {
      return (String)markedItemQ.removeFirst();
    }
    if ((markOn) && (!markedItemQ.isEmpty()) && (peekIndex < peekCount)) {
      return (String)markedItemQ.get(peekIndex++);
    }
    try
    {
      String str = (String)is.readObject();
      if (markOn) {
        markedItemQ.addLast(str);
      }
      return str;
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "read_wstring");
    }
  }
  
  public void read_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfBoolean[(i + paramInt1)] = read_boolean();
    }
  }
  
  public void read_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfChar[(i + paramInt1)] = read_char();
    }
  }
  
  public void read_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    read_char_array(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void read_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfByte[(i + paramInt1)] = read_octet();
    }
  }
  
  public void read_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfShort[(i + paramInt1)] = read_short();
    }
  }
  
  public void read_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    read_short_array(paramArrayOfShort, paramInt1, paramInt2);
  }
  
  public void read_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfInt[(i + paramInt1)] = read_long();
    }
  }
  
  public void read_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    read_long_array(paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public void read_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfLong[(i + paramInt1)] = read_longlong();
    }
  }
  
  public void read_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    read_longlong_array(paramArrayOfLong, paramInt1, paramInt2);
  }
  
  public void read_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfFloat[(i + paramInt1)] = read_float();
    }
  }
  
  public void read_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfDouble[(i + paramInt1)] = read_double();
    }
  }
  
  public org.omg.CORBA.Object read_Object()
  {
    return read_Object(null);
  }
  
  public TypeCode read_TypeCode()
  {
    TypeCodeImpl localTypeCodeImpl = new TypeCodeImpl(orb);
    localTypeCodeImpl.read_value(parent);
    return localTypeCodeImpl;
  }
  
  public Any read_any()
  {
    Any localAny = orb.create_any();
    TypeCodeImpl localTypeCodeImpl = new TypeCodeImpl(orb);
    try
    {
      localTypeCodeImpl.read_value(parent);
    }
    catch (MARSHAL localMARSHAL)
    {
      if (localTypeCodeImpl.kind().value() != 29) {
        throw localMARSHAL;
      }
      localMARSHAL.printStackTrace();
    }
    localAny.read_value(parent, localTypeCodeImpl);
    return localAny;
  }
  
  public Principal read_Principal()
  {
    int i = read_long();
    byte[] arrayOfByte = new byte[i];
    read_octet_array(arrayOfByte, 0, i);
    PrincipalImpl localPrincipalImpl = new PrincipalImpl();
    localPrincipalImpl.name(arrayOfByte);
    return localPrincipalImpl;
  }
  
  public BigDecimal read_fixed()
  {
    return new BigDecimal(read_fixed_buffer().toString());
  }
  
  private StringBuffer read_fixed_buffer()
  {
    StringBuffer localStringBuffer = new StringBuffer(64);
    int m = 0;
    int n = 1;
    while (n != 0)
    {
      int i = read_octet();
      int j = (i & 0xF0) >> 4;
      int k = i & 0xF;
      if ((m != 0) || (j != 0))
      {
        localStringBuffer.append(Character.forDigit(j, 10));
        m = 1;
      }
      if (k == 12)
      {
        if (m == 0) {
          return new StringBuffer("0.0");
        }
        n = 0;
      }
      else if (k == 13)
      {
        localStringBuffer.insert(0, '-');
        n = 0;
      }
      else
      {
        localStringBuffer.append(Character.forDigit(k, 10));
        m = 1;
      }
    }
    return localStringBuffer;
  }
  
  public org.omg.CORBA.Object read_Object(Class paramClass)
  {
    IOR localIOR = IORFactories.makeIOR(parent);
    if (localIOR.isNil()) {
      return null;
    }
    PresentationManager.StubFactoryFactory localStubFactoryFactory = com.sun.corba.se.spi.orb.ORB.getStubFactoryFactory();
    String str1 = localIOR.getProfile().getCodebase();
    PresentationManager.StubFactory localStubFactory = null;
    if (paramClass == null)
    {
      RepositoryId localRepositoryId = RepositoryId.cache.getId(localIOR.getTypeId());
      String str2 = localRepositoryId.getClassName();
      boolean bool2 = localRepositoryId.isIDLType();
      if ((str2 == null) || (str2.equals(""))) {
        localStubFactory = null;
      } else {
        try
        {
          localStubFactory = localStubFactoryFactory.createStubFactory(str2, bool2, str1, (Class)null, (ClassLoader)null);
        }
        catch (Exception localException)
        {
          localStubFactory = null;
        }
      }
    }
    else if (StubAdapter.isStubClass(paramClass))
    {
      localStubFactory = PresentationDefaults.makeStaticStubFactory(paramClass);
    }
    else
    {
      boolean bool1 = IDLEntity.class.isAssignableFrom(paramClass);
      localStubFactory = localStubFactoryFactory.createStubFactory(paramClass.getName(), bool1, str1, paramClass, paramClass.getClassLoader());
    }
    return CDRInputStream_1_0.internalIORToObject(localIOR, localStubFactory, orb);
  }
  
  public org.omg.CORBA.ORB orb()
  {
    return orb;
  }
  
  public Serializable read_value()
  {
    if ((!markOn) && (!markedItemQ.isEmpty())) {
      return (Serializable)markedItemQ.removeFirst();
    }
    if ((markOn) && (!markedItemQ.isEmpty()) && (peekIndex < peekCount)) {
      return (Serializable)markedItemQ.get(peekIndex++);
    }
    try
    {
      Serializable localSerializable = (Serializable)is.readObject();
      if (markOn) {
        markedItemQ.addLast(localSerializable);
      }
      return localSerializable;
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "read_value");
    }
  }
  
  public Serializable read_value(Class paramClass)
  {
    return read_value();
  }
  
  public Serializable read_value(BoxedValueHelper paramBoxedValueHelper)
  {
    return read_value();
  }
  
  public Serializable read_value(String paramString)
  {
    return read_value();
  }
  
  public Serializable read_value(Serializable paramSerializable)
  {
    return read_value();
  }
  
  public Object read_abstract_interface()
  {
    return read_abstract_interface(null);
  }
  
  public Object read_abstract_interface(Class paramClass)
  {
    boolean bool = read_boolean();
    if (bool) {
      return read_Object(paramClass);
    }
    return read_value();
  }
  
  public void consumeEndian()
  {
    throw wrapper.giopVersionError();
  }
  
  public int getPosition()
  {
    try
    {
      return bis.getPosition();
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "getPosition");
    }
  }
  
  public Object read_Abstract()
  {
    return read_abstract_interface();
  }
  
  public Serializable read_Value()
  {
    return read_value();
  }
  
  public void read_any_array(AnySeqHolder paramAnySeqHolder, int paramInt1, int paramInt2)
  {
    read_any_array(value, paramInt1, paramInt2);
  }
  
  private final void read_any_array(Any[] paramArrayOfAny, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      paramArrayOfAny[(i + paramInt1)] = read_any();
    }
  }
  
  public void read_boolean_array(BooleanSeqHolder paramBooleanSeqHolder, int paramInt1, int paramInt2)
  {
    read_boolean_array(value, paramInt1, paramInt2);
  }
  
  public void read_char_array(CharSeqHolder paramCharSeqHolder, int paramInt1, int paramInt2)
  {
    read_char_array(value, paramInt1, paramInt2);
  }
  
  public void read_wchar_array(WCharSeqHolder paramWCharSeqHolder, int paramInt1, int paramInt2)
  {
    read_wchar_array(value, paramInt1, paramInt2);
  }
  
  public void read_octet_array(OctetSeqHolder paramOctetSeqHolder, int paramInt1, int paramInt2)
  {
    read_octet_array(value, paramInt1, paramInt2);
  }
  
  public void read_short_array(ShortSeqHolder paramShortSeqHolder, int paramInt1, int paramInt2)
  {
    read_short_array(value, paramInt1, paramInt2);
  }
  
  public void read_ushort_array(UShortSeqHolder paramUShortSeqHolder, int paramInt1, int paramInt2)
  {
    read_ushort_array(value, paramInt1, paramInt2);
  }
  
  public void read_long_array(LongSeqHolder paramLongSeqHolder, int paramInt1, int paramInt2)
  {
    read_long_array(value, paramInt1, paramInt2);
  }
  
  public void read_ulong_array(ULongSeqHolder paramULongSeqHolder, int paramInt1, int paramInt2)
  {
    read_ulong_array(value, paramInt1, paramInt2);
  }
  
  public void read_ulonglong_array(ULongLongSeqHolder paramULongLongSeqHolder, int paramInt1, int paramInt2)
  {
    read_ulonglong_array(value, paramInt1, paramInt2);
  }
  
  public void read_longlong_array(LongLongSeqHolder paramLongLongSeqHolder, int paramInt1, int paramInt2)
  {
    read_longlong_array(value, paramInt1, paramInt2);
  }
  
  public void read_float_array(FloatSeqHolder paramFloatSeqHolder, int paramInt1, int paramInt2)
  {
    read_float_array(value, paramInt1, paramInt2);
  }
  
  public void read_double_array(DoubleSeqHolder paramDoubleSeqHolder, int paramInt1, int paramInt2)
  {
    read_double_array(value, paramInt1, paramInt2);
  }
  
  public String[] _truncatable_ids()
  {
    throw wrapper.giopVersionError();
  }
  
  public void mark(int paramInt)
  {
    if ((markOn) || (is == null)) {
      throw wrapper.javaSerializationException("mark");
    }
    markOn = true;
    if (!markedItemQ.isEmpty())
    {
      peekIndex = 0;
      peekCount = markedItemQ.size();
    }
  }
  
  public void reset()
  {
    markOn = false;
    peekIndex = 0;
    peekCount = 0;
  }
  
  public boolean markSupported()
  {
    return true;
  }
  
  public CDRInputStreamBase dup()
  {
    CDRInputStreamBase localCDRInputStreamBase = null;
    try
    {
      localCDRInputStreamBase = (CDRInputStreamBase)getClass().newInstance();
    }
    catch (Exception localException)
    {
      throw wrapper.couldNotDuplicateCdrInputStream(localException);
    }
    localCDRInputStreamBase.init(orb, buffer, bufSize, false, null);
    ((IDLJavaSerializationInputStream)localCDRInputStreamBase).skipBytes(getPosition());
    ((IDLJavaSerializationInputStream)localCDRInputStreamBase).setMarkData(markOn, peekIndex, peekCount, (LinkedList)markedItemQ.clone());
    return localCDRInputStreamBase;
  }
  
  void skipBytes(int paramInt)
  {
    try
    {
      is.skipBytes(paramInt);
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "skipBytes");
    }
  }
  
  void setMarkData(boolean paramBoolean, int paramInt1, int paramInt2, LinkedList paramLinkedList)
  {
    markOn = paramBoolean;
    peekIndex = paramInt1;
    peekCount = paramInt2;
    markedItemQ = paramLinkedList;
  }
  
  public BigDecimal read_fixed(short paramShort1, short paramShort2)
  {
    StringBuffer localStringBuffer = read_fixed_buffer();
    if (paramShort1 != localStringBuffer.length()) {
      throw wrapper.badFixed(new Integer(paramShort1), new Integer(localStringBuffer.length()));
    }
    localStringBuffer.insert(paramShort1 - paramShort2, '.');
    return new BigDecimal(localStringBuffer.toString());
  }
  
  public boolean isLittleEndian()
  {
    throw wrapper.giopVersionError();
  }
  
  void setHeaderPadding(boolean paramBoolean) {}
  
  public ByteBuffer getByteBuffer()
  {
    throw wrapper.giopVersionError();
  }
  
  public void setByteBuffer(ByteBuffer paramByteBuffer)
  {
    throw wrapper.giopVersionError();
  }
  
  public void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    throw wrapper.giopVersionError();
  }
  
  public int getBufferLength()
  {
    return bufSize;
  }
  
  public void setBufferLength(int paramInt) {}
  
  public int getIndex()
  {
    return bis.getPosition();
  }
  
  public void setIndex(int paramInt)
  {
    try
    {
      bis.setPosition(paramInt);
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw wrapper.javaSerializationException(localIndexOutOfBoundsException, "setIndex");
    }
  }
  
  public void orb(org.omg.CORBA.ORB paramORB)
  {
    orb = ((com.sun.corba.se.spi.orb.ORB)paramORB);
  }
  
  public BufferManagerRead getBufferManager()
  {
    return bufferManager;
  }
  
  public GIOPVersion getGIOPVersion()
  {
    return GIOPVersion.V1_2;
  }
  
  CodeBase getCodeBase()
  {
    return parent.getCodeBase();
  }
  
  void printBuffer()
  {
    byte[] arrayOfByte = buffer.array();
    System.out.println("+++++++ Input Buffer ++++++++");
    System.out.println();
    System.out.println("Current position: " + getPosition());
    System.out.println("Total length : " + bufSize);
    System.out.println();
    char[] arrayOfChar = new char[16];
    try
    {
      for (int i = 0; i < arrayOfByte.length; i += 16)
      {
        for (int j = 0; (j < 16) && (j + i < arrayOfByte.length); j++)
        {
          k = arrayOfByte[(i + j)];
          if (k < 0) {
            k = 256 + k;
          }
          String str = Integer.toHexString(k);
          if (str.length() == 1) {
            str = "0" + str;
          }
          System.out.print(str + " ");
        }
        while (j < 16)
        {
          System.out.print("   ");
          j++;
        }
        for (int k = 0; (k < 16) && (k + i < arrayOfByte.length); k++) {
          if (ORBUtility.isPrintable((char)arrayOfByte[(i + k)])) {
            arrayOfChar[k] = ((char)arrayOfByte[(i + k)]);
          } else {
            arrayOfChar[k] = '.';
          }
        }
        System.out.println(new String(arrayOfChar, 0, k));
      }
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    System.out.println("++++++++++++++++++++++++++++++");
  }
  
  void alignOnBoundary(int paramInt)
  {
    throw wrapper.giopVersionError();
  }
  
  void performORBVersionSpecificInit() {}
  
  public void resetCodeSetConverters() {}
  
  public void start_value()
  {
    throw wrapper.giopVersionError();
  }
  
  public void end_value()
  {
    throw wrapper.giopVersionError();
  }
  
  class MarshalObjectInputStream
    extends ObjectInputStream
  {
    com.sun.corba.se.spi.orb.ORB orb;
    
    MarshalObjectInputStream(InputStream paramInputStream, com.sun.corba.se.spi.orb.ORB paramORB)
      throws IOException
    {
      super();
      orb = paramORB;
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          enableResolveObject(true);
          return null;
        }
      });
    }
    
    protected final Object resolveObject(Object paramObject)
      throws IOException
    {
      try
      {
        if (StubAdapter.isStub(paramObject)) {
          StubAdapter.connect(paramObject, orb);
        }
      }
      catch (RemoteException localRemoteException)
      {
        IOException localIOException = new IOException("resolveObject failed");
        localIOException.initCause(localRemoteException);
        throw localIOException;
      }
      return paramObject;
    }
  }
  
  class _ByteArrayInputStream
    extends ByteArrayInputStream
  {
    _ByteArrayInputStream(byte[] paramArrayOfByte)
    {
      super();
    }
    
    int getPosition()
    {
      return pos;
    }
    
    void setPosition(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > count)) {
        throw new IndexOutOfBoundsException();
      }
      pos = paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\IDLJavaSerializationInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */