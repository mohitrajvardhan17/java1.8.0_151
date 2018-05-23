package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.BoxedValueHelper;

final class IDLJavaSerializationOutputStream
  extends CDROutputStreamBase
{
  private com.sun.corba.se.spi.orb.ORB orb;
  private byte encodingVersion;
  private ObjectOutputStream os;
  private _ByteArrayOutputStream bos;
  private BufferManagerWrite bufferManager;
  private final int directWriteLength = 16;
  protected ORBUtilSystemException wrapper;
  
  public IDLJavaSerializationOutputStream(byte paramByte)
  {
    encodingVersion = paramByte;
  }
  
  public void init(org.omg.CORBA.ORB paramORB, boolean paramBoolean1, BufferManagerWrite paramBufferManagerWrite, byte paramByte, boolean paramBoolean2)
  {
    orb = ((com.sun.corba.se.spi.orb.ORB)paramORB);
    bufferManager = paramBufferManagerWrite;
    wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)paramORB, "rpc.encoding");
    bos = new _ByteArrayOutputStream(1024);
  }
  
  private void initObjectOutputStream()
  {
    if (os != null) {
      throw wrapper.javaStreamInitFailed();
    }
    try
    {
      os = new MarshalObjectOutputStream(bos, orb);
    }
    catch (Exception localException)
    {
      throw wrapper.javaStreamInitFailed(localException);
    }
  }
  
  public final void write_boolean(boolean paramBoolean)
  {
    try
    {
      os.writeBoolean(paramBoolean);
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "write_boolean");
    }
  }
  
  public final void write_char(char paramChar)
  {
    try
    {
      os.writeChar(paramChar);
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "write_char");
    }
  }
  
  public final void write_wchar(char paramChar)
  {
    write_char(paramChar);
  }
  
  public final void write_octet(byte paramByte)
  {
    if (bos.size() < 16)
    {
      bos.write(paramByte);
      if (bos.size() == 16) {
        initObjectOutputStream();
      }
      return;
    }
    try
    {
      os.writeByte(paramByte);
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "write_octet");
    }
  }
  
  public final void write_short(short paramShort)
  {
    try
    {
      os.writeShort(paramShort);
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "write_short");
    }
  }
  
  public final void write_ushort(short paramShort)
  {
    write_short(paramShort);
  }
  
  public final void write_long(int paramInt)
  {
    if (bos.size() < 16)
    {
      bos.write((byte)(paramInt >>> 24 & 0xFF));
      bos.write((byte)(paramInt >>> 16 & 0xFF));
      bos.write((byte)(paramInt >>> 8 & 0xFF));
      bos.write((byte)(paramInt >>> 0 & 0xFF));
      if (bos.size() == 16) {
        initObjectOutputStream();
      } else if (bos.size() > 16) {
        wrapper.javaSerializationException("write_long");
      }
      return;
    }
    try
    {
      os.writeInt(paramInt);
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "write_long");
    }
  }
  
  public final void write_ulong(int paramInt)
  {
    write_long(paramInt);
  }
  
  public final void write_longlong(long paramLong)
  {
    try
    {
      os.writeLong(paramLong);
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "write_longlong");
    }
  }
  
  public final void write_ulonglong(long paramLong)
  {
    write_longlong(paramLong);
  }
  
  public final void write_float(float paramFloat)
  {
    try
    {
      os.writeFloat(paramFloat);
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "write_float");
    }
  }
  
  public final void write_double(double paramDouble)
  {
    try
    {
      os.writeDouble(paramDouble);
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "write_double");
    }
  }
  
  public final void write_string(String paramString)
  {
    try
    {
      os.writeUTF(paramString);
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "write_string");
    }
  }
  
  public final void write_wstring(String paramString)
  {
    try
    {
      os.writeObject(paramString);
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "write_wstring");
    }
  }
  
  public final void write_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      write_boolean(paramArrayOfBoolean[(paramInt1 + i)]);
    }
  }
  
  public final void write_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      write_char(paramArrayOfChar[(paramInt1 + i)]);
    }
  }
  
  public final void write_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    write_char_array(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public final void write_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      os.write(paramArrayOfByte, paramInt1, paramInt2);
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "write_octet_array");
    }
  }
  
  public final void write_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      write_short(paramArrayOfShort[(paramInt1 + i)]);
    }
  }
  
  public final void write_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    write_short_array(paramArrayOfShort, paramInt1, paramInt2);
  }
  
  public final void write_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      write_long(paramArrayOfInt[(paramInt1 + i)]);
    }
  }
  
  public final void write_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    write_long_array(paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public final void write_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      write_longlong(paramArrayOfLong[(paramInt1 + i)]);
    }
  }
  
  public final void write_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    write_longlong_array(paramArrayOfLong, paramInt1, paramInt2);
  }
  
  public final void write_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      write_float(paramArrayOfFloat[(paramInt1 + i)]);
    }
  }
  
  public final void write_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      write_double(paramArrayOfDouble[(paramInt1 + i)]);
    }
  }
  
  public final void write_Object(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null)
    {
      localIOR = IORFactories.makeIOR(orb);
      localIOR.write(parent);
      return;
    }
    if ((paramObject instanceof LocalObject)) {
      throw wrapper.writeLocalObject(CompletionStatus.COMPLETED_MAYBE);
    }
    IOR localIOR = ORBUtility.connectAndGetIOR(orb, paramObject);
    localIOR.write(parent);
  }
  
  public final void write_TypeCode(TypeCode paramTypeCode)
  {
    if (paramTypeCode == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    TypeCodeImpl localTypeCodeImpl;
    if ((paramTypeCode instanceof TypeCodeImpl)) {
      localTypeCodeImpl = (TypeCodeImpl)paramTypeCode;
    } else {
      localTypeCodeImpl = new TypeCodeImpl(orb, paramTypeCode);
    }
    localTypeCodeImpl.write_value(parent);
  }
  
  public final void write_any(Any paramAny)
  {
    if (paramAny == null) {
      throw wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    write_TypeCode(paramAny.type());
    paramAny.write_value(parent);
  }
  
  public final void write_Principal(Principal paramPrincipal)
  {
    write_long(paramPrincipal.name().length);
    write_octet_array(paramPrincipal.name(), 0, paramPrincipal.name().length);
  }
  
  public final void write_fixed(BigDecimal paramBigDecimal)
  {
    write_fixed(paramBigDecimal.toString(), paramBigDecimal.signum());
  }
  
  private void write_fixed(String paramString, int paramInt)
  {
    int i = paramString.length();
    int j = 0;
    int m = 0;
    char c;
    for (int n = 0; n < i; n++)
    {
      c = paramString.charAt(n);
      if ((c != '-') && (c != '+') && (c != '.')) {
        m++;
      }
    }
    byte b;
    for (n = 0; n < i; n++)
    {
      c = paramString.charAt(n);
      if ((c != '-') && (c != '+') && (c != '.'))
      {
        int k = (byte)Character.digit(c, 10);
        if (k == -1) {
          throw wrapper.badDigitInFixed(CompletionStatus.COMPLETED_MAYBE);
        }
        if (m % 2 == 0)
        {
          j = (byte)(j | k);
          write_octet(j);
          j = 0;
        }
        else
        {
          b = (byte)(j | k << 4);
        }
        m--;
      }
    }
    if (paramInt == -1) {
      b = (byte)(b | 0xD);
    } else {
      b = (byte)(b | 0xC);
    }
    write_octet(b);
  }
  
  public final org.omg.CORBA.ORB orb()
  {
    return orb;
  }
  
  public final void write_value(Serializable paramSerializable)
  {
    write_value(paramSerializable, (String)null);
  }
  
  public final void write_value(Serializable paramSerializable, Class paramClass)
  {
    write_value(paramSerializable);
  }
  
  public final void write_value(Serializable paramSerializable, String paramString)
  {
    try
    {
      os.writeObject(paramSerializable);
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "write_value");
    }
  }
  
  public final void write_value(Serializable paramSerializable, BoxedValueHelper paramBoxedValueHelper)
  {
    write_value(paramSerializable, (String)null);
  }
  
  public final void write_abstract_interface(Object paramObject)
  {
    boolean bool = false;
    org.omg.CORBA.Object localObject = null;
    if ((paramObject != null) && ((paramObject instanceof org.omg.CORBA.Object)))
    {
      localObject = (org.omg.CORBA.Object)paramObject;
      bool = true;
    }
    write_boolean(bool);
    if (bool) {
      write_Object(localObject);
    } else {
      try
      {
        write_value((Serializable)paramObject);
      }
      catch (ClassCastException localClassCastException)
      {
        if ((paramObject instanceof Serializable)) {
          throw localClassCastException;
        }
        ORBUtility.throwNotSerializableForCorba(paramObject.getClass().getName());
      }
    }
  }
  
  public final void start_block()
  {
    throw wrapper.giopVersionError();
  }
  
  public final void end_block()
  {
    throw wrapper.giopVersionError();
  }
  
  public final void putEndian()
  {
    throw wrapper.giopVersionError();
  }
  
  public void writeTo(java.io.OutputStream paramOutputStream)
    throws IOException
  {
    try
    {
      os.flush();
      bos.writeTo(paramOutputStream);
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "writeTo");
    }
  }
  
  public final byte[] toByteArray()
  {
    try
    {
      os.flush();
      return bos.toByteArray();
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "toByteArray");
    }
  }
  
  public final void write_Abstract(Object paramObject)
  {
    write_abstract_interface(paramObject);
  }
  
  public final void write_Value(Serializable paramSerializable)
  {
    write_value(paramSerializable);
  }
  
  public final void write_any_array(Any[] paramArrayOfAny, int paramInt1, int paramInt2)
  {
    for (int i = 0; i < paramInt2; i++) {
      write_any(paramArrayOfAny[(paramInt1 + i)]);
    }
  }
  
  public final String[] _truncatable_ids()
  {
    throw wrapper.giopVersionError();
  }
  
  public final int getSize()
  {
    try
    {
      os.flush();
      return bos.size();
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "write_boolean");
    }
  }
  
  public final int getIndex()
  {
    return getSize();
  }
  
  protected int getRealIndex(int paramInt)
  {
    return getSize();
  }
  
  public final void setIndex(int paramInt)
  {
    throw wrapper.giopVersionError();
  }
  
  public final ByteBuffer getByteBuffer()
  {
    throw wrapper.giopVersionError();
  }
  
  public final void setByteBuffer(ByteBuffer paramByteBuffer)
  {
    throw wrapper.giopVersionError();
  }
  
  public final boolean isLittleEndian()
  {
    return false;
  }
  
  public ByteBufferWithInfo getByteBufferWithInfo()
  {
    try
    {
      os.flush();
    }
    catch (Exception localException)
    {
      throw wrapper.javaSerializationException(localException, "getByteBufferWithInfo");
    }
    ByteBuffer localByteBuffer = ByteBuffer.wrap(bos.getByteArray());
    localByteBuffer.limit(bos.size());
    return new ByteBufferWithInfo(orb, localByteBuffer, bos.size());
  }
  
  public void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    throw wrapper.giopVersionError();
  }
  
  public final BufferManagerWrite getBufferManager()
  {
    return bufferManager;
  }
  
  public final void write_fixed(BigDecimal paramBigDecimal, short paramShort1, short paramShort2)
  {
    String str1 = paramBigDecimal.toString();
    if ((str1.charAt(0) == '-') || (str1.charAt(0) == '+')) {
      str1 = str1.substring(1);
    }
    int i = str1.indexOf('.');
    String str2;
    String str3;
    if (i == -1)
    {
      str2 = str1;
      str3 = null;
    }
    else if (i == 0)
    {
      str2 = null;
      str3 = str1;
    }
    else
    {
      str2 = str1.substring(0, i);
      str3 = str1.substring(i + 1);
    }
    StringBuffer localStringBuffer = new StringBuffer(paramShort1);
    if (str3 != null) {
      localStringBuffer.append(str3);
    }
    while (localStringBuffer.length() < paramShort2) {
      localStringBuffer.append('0');
    }
    if (str2 != null) {
      localStringBuffer.insert(0, str2);
    }
    while (localStringBuffer.length() < paramShort1) {
      localStringBuffer.insert(0, '0');
    }
    write_fixed(localStringBuffer.toString(), paramBigDecimal.signum());
  }
  
  public final void writeOctetSequenceTo(org.omg.CORBA.portable.OutputStream paramOutputStream)
  {
    byte[] arrayOfByte = toByteArray();
    paramOutputStream.write_long(arrayOfByte.length);
    paramOutputStream.write_octet_array(arrayOfByte, 0, arrayOfByte.length);
  }
  
  public final GIOPVersion getGIOPVersion()
  {
    return GIOPVersion.V1_2;
  }
  
  public final void writeIndirection(int paramInt1, int paramInt2)
  {
    throw wrapper.giopVersionError();
  }
  
  void freeInternalCaches() {}
  
  void printBuffer()
  {
    byte[] arrayOfByte = toByteArray();
    System.out.println("+++++++ Output Buffer ++++++++");
    System.out.println();
    System.out.println("Current position: " + arrayOfByte.length);
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
  
  public void alignOnBoundary(int paramInt)
  {
    throw wrapper.giopVersionError();
  }
  
  public void setHeaderPadding(boolean paramBoolean) {}
  
  public void start_value(String paramString)
  {
    throw wrapper.giopVersionError();
  }
  
  public void end_value()
  {
    throw wrapper.giopVersionError();
  }
  
  class MarshalObjectOutputStream
    extends ObjectOutputStream
  {
    com.sun.corba.se.spi.orb.ORB orb;
    
    MarshalObjectOutputStream(java.io.OutputStream paramOutputStream, com.sun.corba.se.spi.orb.ORB paramORB)
      throws IOException
    {
      super();
      orb = paramORB;
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          enableReplaceObject(true);
          return null;
        }
      });
    }
    
    protected final Object replaceObject(Object paramObject)
      throws IOException
    {
      try
      {
        if (((paramObject instanceof Remote)) && (!StubAdapter.isStub(paramObject))) {
          return Utility.autoConnect(paramObject, orb, true);
        }
      }
      catch (Exception localException)
      {
        IOException localIOException = new IOException("replaceObject failed");
        localIOException.initCause(localException);
        throw localIOException;
      }
      return paramObject;
    }
  }
  
  class _ByteArrayOutputStream
    extends ByteArrayOutputStream
  {
    _ByteArrayOutputStream(int paramInt)
    {
      super();
    }
    
    byte[] getByteArray()
    {
      return buf;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\IDLJavaSerializationOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */