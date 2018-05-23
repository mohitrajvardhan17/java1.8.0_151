package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DataOutputStream;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ValueOutputStream;

public abstract class CDROutputStream
  extends org.omg.CORBA_2_3.portable.OutputStream
  implements MarshalOutputStream, DataOutputStream, ValueOutputStream
{
  private CDROutputStreamBase impl;
  protected com.sun.corba.se.spi.orb.ORB orb;
  protected ORBUtilSystemException wrapper;
  protected CorbaMessageMediator corbaMessageMediator;
  
  public CDROutputStream(com.sun.corba.se.spi.orb.ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte1, boolean paramBoolean1, BufferManagerWrite paramBufferManagerWrite, byte paramByte2, boolean paramBoolean2)
  {
    impl = OutputStreamFactory.newOutputStream(paramORB, paramGIOPVersion, paramByte1);
    impl.init(paramORB, paramBoolean1, paramBufferManagerWrite, paramByte2, paramBoolean2);
    impl.setParent(this);
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.encoding");
  }
  
  public CDROutputStream(com.sun.corba.se.spi.orb.ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte1, boolean paramBoolean, BufferManagerWrite paramBufferManagerWrite, byte paramByte2)
  {
    this(paramORB, paramGIOPVersion, paramByte1, paramBoolean, paramBufferManagerWrite, paramByte2, true);
  }
  
  public abstract InputStream create_input_stream();
  
  public final void write_boolean(boolean paramBoolean)
  {
    impl.write_boolean(paramBoolean);
  }
  
  public final void write_char(char paramChar)
  {
    impl.write_char(paramChar);
  }
  
  public final void write_wchar(char paramChar)
  {
    impl.write_wchar(paramChar);
  }
  
  public final void write_octet(byte paramByte)
  {
    impl.write_octet(paramByte);
  }
  
  public final void write_short(short paramShort)
  {
    impl.write_short(paramShort);
  }
  
  public final void write_ushort(short paramShort)
  {
    impl.write_ushort(paramShort);
  }
  
  public final void write_long(int paramInt)
  {
    impl.write_long(paramInt);
  }
  
  public final void write_ulong(int paramInt)
  {
    impl.write_ulong(paramInt);
  }
  
  public final void write_longlong(long paramLong)
  {
    impl.write_longlong(paramLong);
  }
  
  public final void write_ulonglong(long paramLong)
  {
    impl.write_ulonglong(paramLong);
  }
  
  public final void write_float(float paramFloat)
  {
    impl.write_float(paramFloat);
  }
  
  public final void write_double(double paramDouble)
  {
    impl.write_double(paramDouble);
  }
  
  public final void write_string(String paramString)
  {
    impl.write_string(paramString);
  }
  
  public final void write_wstring(String paramString)
  {
    impl.write_wstring(paramString);
  }
  
  public final void write_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
  {
    impl.write_boolean_array(paramArrayOfBoolean, paramInt1, paramInt2);
  }
  
  public final void write_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    impl.write_char_array(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public final void write_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    impl.write_wchar_array(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public final void write_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    impl.write_octet_array(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public final void write_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    impl.write_short_array(paramArrayOfShort, paramInt1, paramInt2);
  }
  
  public final void write_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    impl.write_ushort_array(paramArrayOfShort, paramInt1, paramInt2);
  }
  
  public final void write_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    impl.write_long_array(paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public final void write_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    impl.write_ulong_array(paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public final void write_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    impl.write_longlong_array(paramArrayOfLong, paramInt1, paramInt2);
  }
  
  public final void write_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    impl.write_ulonglong_array(paramArrayOfLong, paramInt1, paramInt2);
  }
  
  public final void write_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    impl.write_float_array(paramArrayOfFloat, paramInt1, paramInt2);
  }
  
  public final void write_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    impl.write_double_array(paramArrayOfDouble, paramInt1, paramInt2);
  }
  
  public final void write_Object(org.omg.CORBA.Object paramObject)
  {
    impl.write_Object(paramObject);
  }
  
  public final void write_TypeCode(TypeCode paramTypeCode)
  {
    impl.write_TypeCode(paramTypeCode);
  }
  
  public final void write_any(Any paramAny)
  {
    impl.write_any(paramAny);
  }
  
  public final void write_Principal(Principal paramPrincipal)
  {
    impl.write_Principal(paramPrincipal);
  }
  
  public final void write(int paramInt)
    throws IOException
  {
    impl.write(paramInt);
  }
  
  public final void write_fixed(BigDecimal paramBigDecimal)
  {
    impl.write_fixed(paramBigDecimal);
  }
  
  public final void write_Context(Context paramContext, ContextList paramContextList)
  {
    impl.write_Context(paramContext, paramContextList);
  }
  
  public final org.omg.CORBA.ORB orb()
  {
    return impl.orb();
  }
  
  public final void write_value(Serializable paramSerializable)
  {
    impl.write_value(paramSerializable);
  }
  
  public final void write_value(Serializable paramSerializable, Class paramClass)
  {
    impl.write_value(paramSerializable, paramClass);
  }
  
  public final void write_value(Serializable paramSerializable, String paramString)
  {
    impl.write_value(paramSerializable, paramString);
  }
  
  public final void write_value(Serializable paramSerializable, BoxedValueHelper paramBoxedValueHelper)
  {
    impl.write_value(paramSerializable, paramBoxedValueHelper);
  }
  
  public final void write_abstract_interface(Object paramObject)
  {
    impl.write_abstract_interface(paramObject);
  }
  
  public final void write(byte[] paramArrayOfByte)
    throws IOException
  {
    impl.write(paramArrayOfByte);
  }
  
  public final void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    impl.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public final void flush()
    throws IOException
  {
    impl.flush();
  }
  
  public final void close()
    throws IOException
  {
    impl.close();
  }
  
  public final void start_block()
  {
    impl.start_block();
  }
  
  public final void end_block()
  {
    impl.end_block();
  }
  
  public final void putEndian()
  {
    impl.putEndian();
  }
  
  public void writeTo(java.io.OutputStream paramOutputStream)
    throws IOException
  {
    impl.writeTo(paramOutputStream);
  }
  
  public final byte[] toByteArray()
  {
    return impl.toByteArray();
  }
  
  public final void write_Abstract(Object paramObject)
  {
    impl.write_Abstract(paramObject);
  }
  
  public final void write_Value(Serializable paramSerializable)
  {
    impl.write_Value(paramSerializable);
  }
  
  public final void write_any_array(Any[] paramArrayOfAny, int paramInt1, int paramInt2)
  {
    impl.write_any_array(paramArrayOfAny, paramInt1, paramInt2);
  }
  
  public void setMessageMediator(MessageMediator paramMessageMediator)
  {
    corbaMessageMediator = ((CorbaMessageMediator)paramMessageMediator);
  }
  
  public MessageMediator getMessageMediator()
  {
    return corbaMessageMediator;
  }
  
  public final String[] _truncatable_ids()
  {
    return impl._truncatable_ids();
  }
  
  protected final int getSize()
  {
    return impl.getSize();
  }
  
  protected final int getIndex()
  {
    return impl.getIndex();
  }
  
  protected int getRealIndex(int paramInt)
  {
    return paramInt;
  }
  
  protected final void setIndex(int paramInt)
  {
    impl.setIndex(paramInt);
  }
  
  protected final ByteBuffer getByteBuffer()
  {
    return impl.getByteBuffer();
  }
  
  protected final void setByteBuffer(ByteBuffer paramByteBuffer)
  {
    impl.setByteBuffer(paramByteBuffer);
  }
  
  protected final boolean isSharing(ByteBuffer paramByteBuffer)
  {
    return getByteBuffer() == paramByteBuffer;
  }
  
  public final boolean isLittleEndian()
  {
    return impl.isLittleEndian();
  }
  
  public ByteBufferWithInfo getByteBufferWithInfo()
  {
    return impl.getByteBufferWithInfo();
  }
  
  protected void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    impl.setByteBufferWithInfo(paramByteBufferWithInfo);
  }
  
  public final BufferManagerWrite getBufferManager()
  {
    return impl.getBufferManager();
  }
  
  public final void write_fixed(BigDecimal paramBigDecimal, short paramShort1, short paramShort2)
  {
    impl.write_fixed(paramBigDecimal, paramShort1, paramShort2);
  }
  
  public final void writeOctetSequenceTo(org.omg.CORBA.portable.OutputStream paramOutputStream)
  {
    impl.writeOctetSequenceTo(paramOutputStream);
  }
  
  public final GIOPVersion getGIOPVersion()
  {
    return impl.getGIOPVersion();
  }
  
  public final void writeIndirection(int paramInt1, int paramInt2)
  {
    impl.writeIndirection(paramInt1, paramInt2);
  }
  
  protected CodeSetConversion.CTBConverter createCharCTBConverter()
  {
    return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.ISO_8859_1);
  }
  
  protected abstract CodeSetConversion.CTBConverter createWCharCTBConverter();
  
  protected final void freeInternalCaches()
  {
    impl.freeInternalCaches();
  }
  
  void printBuffer()
  {
    impl.printBuffer();
  }
  
  public void alignOnBoundary(int paramInt)
  {
    impl.alignOnBoundary(paramInt);
  }
  
  public void setHeaderPadding(boolean paramBoolean)
  {
    impl.setHeaderPadding(paramBoolean);
  }
  
  public void start_value(String paramString)
  {
    impl.start_value(paramString);
  }
  
  public void end_value()
  {
    impl.end_value();
  }
  
  private static class OutputStreamFactory
  {
    private OutputStreamFactory() {}
    
    public static CDROutputStreamBase newOutputStream(com.sun.corba.se.spi.orb.ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte)
    {
      switch (paramGIOPVersion.intValue())
      {
      case 256: 
        return new CDROutputStream_1_0();
      case 257: 
        return new CDROutputStream_1_1();
      case 258: 
        if (paramByte != 0) {
          return new IDLJavaSerializationOutputStream(paramByte);
        }
        return new CDROutputStream_1_2();
      }
      ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get(paramORB, "rpc.encoding");
      throw localORBUtilSystemException.unsupportedGiopVersion(paramGIOPVersion);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\CDROutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */