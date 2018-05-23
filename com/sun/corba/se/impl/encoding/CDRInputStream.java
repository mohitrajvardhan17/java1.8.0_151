package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import org.omg.CORBA.Any;
import org.omg.CORBA.AnySeqHolder;
import org.omg.CORBA.BooleanSeqHolder;
import org.omg.CORBA.CharSeqHolder;
import org.omg.CORBA.Context;
import org.omg.CORBA.DataInputStream;
import org.omg.CORBA.DoubleSeqHolder;
import org.omg.CORBA.FloatSeqHolder;
import org.omg.CORBA.LongLongSeqHolder;
import org.omg.CORBA.LongSeqHolder;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA.Principal;
import org.omg.CORBA.ShortSeqHolder;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ULongLongSeqHolder;
import org.omg.CORBA.ULongSeqHolder;
import org.omg.CORBA.UShortSeqHolder;
import org.omg.CORBA.WCharSeqHolder;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.ValueInputStream;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class CDRInputStream
  extends InputStream
  implements MarshalInputStream, DataInputStream, ValueInputStream
{
  protected CorbaMessageMediator messageMediator;
  private CDRInputStreamBase impl;
  
  public CDRInputStream() {}
  
  public CDRInputStream(CDRInputStream paramCDRInputStream)
  {
    impl = impl.dup();
    impl.setParent(this);
  }
  
  public CDRInputStream(org.omg.CORBA.ORB paramORB, ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean, GIOPVersion paramGIOPVersion, byte paramByte, BufferManagerRead paramBufferManagerRead)
  {
    impl = InputStreamFactory.newInputStream((com.sun.corba.se.spi.orb.ORB)paramORB, paramGIOPVersion, paramByte);
    impl.init(paramORB, paramByteBuffer, paramInt, paramBoolean, paramBufferManagerRead);
    impl.setParent(this);
  }
  
  public final boolean read_boolean()
  {
    return impl.read_boolean();
  }
  
  public final char read_char()
  {
    return impl.read_char();
  }
  
  public final char read_wchar()
  {
    return impl.read_wchar();
  }
  
  public final byte read_octet()
  {
    return impl.read_octet();
  }
  
  public final short read_short()
  {
    return impl.read_short();
  }
  
  public final short read_ushort()
  {
    return impl.read_ushort();
  }
  
  public final int read_long()
  {
    return impl.read_long();
  }
  
  public final int read_ulong()
  {
    return impl.read_ulong();
  }
  
  public final long read_longlong()
  {
    return impl.read_longlong();
  }
  
  public final long read_ulonglong()
  {
    return impl.read_ulonglong();
  }
  
  public final float read_float()
  {
    return impl.read_float();
  }
  
  public final double read_double()
  {
    return impl.read_double();
  }
  
  public final String read_string()
  {
    return impl.read_string();
  }
  
  public final String read_wstring()
  {
    return impl.read_wstring();
  }
  
  public final void read_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
  {
    impl.read_boolean_array(paramArrayOfBoolean, paramInt1, paramInt2);
  }
  
  public final void read_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    impl.read_char_array(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public final void read_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    impl.read_wchar_array(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public final void read_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    impl.read_octet_array(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public final void read_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    impl.read_short_array(paramArrayOfShort, paramInt1, paramInt2);
  }
  
  public final void read_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2)
  {
    impl.read_ushort_array(paramArrayOfShort, paramInt1, paramInt2);
  }
  
  public final void read_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    impl.read_long_array(paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public final void read_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    impl.read_ulong_array(paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public final void read_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    impl.read_longlong_array(paramArrayOfLong, paramInt1, paramInt2);
  }
  
  public final void read_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2)
  {
    impl.read_ulonglong_array(paramArrayOfLong, paramInt1, paramInt2);
  }
  
  public final void read_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    impl.read_float_array(paramArrayOfFloat, paramInt1, paramInt2);
  }
  
  public final void read_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    impl.read_double_array(paramArrayOfDouble, paramInt1, paramInt2);
  }
  
  public final org.omg.CORBA.Object read_Object()
  {
    return impl.read_Object();
  }
  
  public final TypeCode read_TypeCode()
  {
    return impl.read_TypeCode();
  }
  
  public final Any read_any()
  {
    return impl.read_any();
  }
  
  public final Principal read_Principal()
  {
    return impl.read_Principal();
  }
  
  public final int read()
    throws IOException
  {
    return impl.read();
  }
  
  public final BigDecimal read_fixed()
  {
    return impl.read_fixed();
  }
  
  public final Context read_Context()
  {
    return impl.read_Context();
  }
  
  public final org.omg.CORBA.Object read_Object(Class paramClass)
  {
    return impl.read_Object(paramClass);
  }
  
  public final org.omg.CORBA.ORB orb()
  {
    return impl.orb();
  }
  
  public final Serializable read_value()
  {
    return impl.read_value();
  }
  
  public final Serializable read_value(Class paramClass)
  {
    return impl.read_value(paramClass);
  }
  
  public final Serializable read_value(BoxedValueHelper paramBoxedValueHelper)
  {
    return impl.read_value(paramBoxedValueHelper);
  }
  
  public final Serializable read_value(String paramString)
  {
    return impl.read_value(paramString);
  }
  
  public final Serializable read_value(Serializable paramSerializable)
  {
    return impl.read_value(paramSerializable);
  }
  
  public final Object read_abstract_interface()
  {
    return impl.read_abstract_interface();
  }
  
  public final Object read_abstract_interface(Class paramClass)
  {
    return impl.read_abstract_interface(paramClass);
  }
  
  public final void consumeEndian()
  {
    impl.consumeEndian();
  }
  
  public final int getPosition()
  {
    return impl.getPosition();
  }
  
  public final Object read_Abstract()
  {
    return impl.read_Abstract();
  }
  
  public final Serializable read_Value()
  {
    return impl.read_Value();
  }
  
  public final void read_any_array(AnySeqHolder paramAnySeqHolder, int paramInt1, int paramInt2)
  {
    impl.read_any_array(paramAnySeqHolder, paramInt1, paramInt2);
  }
  
  public final void read_boolean_array(BooleanSeqHolder paramBooleanSeqHolder, int paramInt1, int paramInt2)
  {
    impl.read_boolean_array(paramBooleanSeqHolder, paramInt1, paramInt2);
  }
  
  public final void read_char_array(CharSeqHolder paramCharSeqHolder, int paramInt1, int paramInt2)
  {
    impl.read_char_array(paramCharSeqHolder, paramInt1, paramInt2);
  }
  
  public final void read_wchar_array(WCharSeqHolder paramWCharSeqHolder, int paramInt1, int paramInt2)
  {
    impl.read_wchar_array(paramWCharSeqHolder, paramInt1, paramInt2);
  }
  
  public final void read_octet_array(OctetSeqHolder paramOctetSeqHolder, int paramInt1, int paramInt2)
  {
    impl.read_octet_array(paramOctetSeqHolder, paramInt1, paramInt2);
  }
  
  public final void read_short_array(ShortSeqHolder paramShortSeqHolder, int paramInt1, int paramInt2)
  {
    impl.read_short_array(paramShortSeqHolder, paramInt1, paramInt2);
  }
  
  public final void read_ushort_array(UShortSeqHolder paramUShortSeqHolder, int paramInt1, int paramInt2)
  {
    impl.read_ushort_array(paramUShortSeqHolder, paramInt1, paramInt2);
  }
  
  public final void read_long_array(LongSeqHolder paramLongSeqHolder, int paramInt1, int paramInt2)
  {
    impl.read_long_array(paramLongSeqHolder, paramInt1, paramInt2);
  }
  
  public final void read_ulong_array(ULongSeqHolder paramULongSeqHolder, int paramInt1, int paramInt2)
  {
    impl.read_ulong_array(paramULongSeqHolder, paramInt1, paramInt2);
  }
  
  public final void read_ulonglong_array(ULongLongSeqHolder paramULongLongSeqHolder, int paramInt1, int paramInt2)
  {
    impl.read_ulonglong_array(paramULongLongSeqHolder, paramInt1, paramInt2);
  }
  
  public final void read_longlong_array(LongLongSeqHolder paramLongLongSeqHolder, int paramInt1, int paramInt2)
  {
    impl.read_longlong_array(paramLongLongSeqHolder, paramInt1, paramInt2);
  }
  
  public final void read_float_array(FloatSeqHolder paramFloatSeqHolder, int paramInt1, int paramInt2)
  {
    impl.read_float_array(paramFloatSeqHolder, paramInt1, paramInt2);
  }
  
  public final void read_double_array(DoubleSeqHolder paramDoubleSeqHolder, int paramInt1, int paramInt2)
  {
    impl.read_double_array(paramDoubleSeqHolder, paramInt1, paramInt2);
  }
  
  public final String[] _truncatable_ids()
  {
    return impl._truncatable_ids();
  }
  
  public final int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return impl.read(paramArrayOfByte);
  }
  
  public final int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return impl.read(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public final long skip(long paramLong)
    throws IOException
  {
    return impl.skip(paramLong);
  }
  
  public final int available()
    throws IOException
  {
    return impl.available();
  }
  
  public final void close()
    throws IOException
  {
    impl.close();
  }
  
  public final void mark(int paramInt)
  {
    impl.mark(paramInt);
  }
  
  public final void reset()
  {
    impl.reset();
  }
  
  public final boolean markSupported()
  {
    return impl.markSupported();
  }
  
  public abstract CDRInputStream dup();
  
  public final BigDecimal read_fixed(short paramShort1, short paramShort2)
  {
    return impl.read_fixed(paramShort1, paramShort2);
  }
  
  public final boolean isLittleEndian()
  {
    return impl.isLittleEndian();
  }
  
  protected final ByteBuffer getByteBuffer()
  {
    return impl.getByteBuffer();
  }
  
  protected final void setByteBuffer(ByteBuffer paramByteBuffer)
  {
    impl.setByteBuffer(paramByteBuffer);
  }
  
  protected final void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    impl.setByteBufferWithInfo(paramByteBufferWithInfo);
  }
  
  protected final boolean isSharing(ByteBuffer paramByteBuffer)
  {
    return getByteBuffer() == paramByteBuffer;
  }
  
  public final int getBufferLength()
  {
    return impl.getBufferLength();
  }
  
  protected final void setBufferLength(int paramInt)
  {
    impl.setBufferLength(paramInt);
  }
  
  protected final int getIndex()
  {
    return impl.getIndex();
  }
  
  protected final void setIndex(int paramInt)
  {
    impl.setIndex(paramInt);
  }
  
  public final void orb(org.omg.CORBA.ORB paramORB)
  {
    impl.orb(paramORB);
  }
  
  public final GIOPVersion getGIOPVersion()
  {
    return impl.getGIOPVersion();
  }
  
  public final BufferManagerRead getBufferManager()
  {
    return impl.getBufferManager();
  }
  
  public CodeBase getCodeBase()
  {
    return null;
  }
  
  protected CodeSetConversion.BTCConverter createCharBTCConverter()
  {
    return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.ISO_8859_1, impl.isLittleEndian());
  }
  
  protected abstract CodeSetConversion.BTCConverter createWCharBTCConverter();
  
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
  
  public void performORBVersionSpecificInit()
  {
    if (impl != null) {
      impl.performORBVersionSpecificInit();
    }
  }
  
  public void resetCodeSetConverters()
  {
    impl.resetCodeSetConverters();
  }
  
  public void setMessageMediator(MessageMediator paramMessageMediator)
  {
    messageMediator = ((CorbaMessageMediator)paramMessageMediator);
  }
  
  public MessageMediator getMessageMediator()
  {
    return messageMediator;
  }
  
  public void start_value()
  {
    impl.start_value();
  }
  
  public void end_value()
  {
    impl.end_value();
  }
  
  private static class InputStreamFactory
  {
    private InputStreamFactory() {}
    
    public static CDRInputStreamBase newInputStream(com.sun.corba.se.spi.orb.ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte)
    {
      switch (paramGIOPVersion.intValue())
      {
      case 256: 
        return new CDRInputStream_1_0();
      case 257: 
        return new CDRInputStream_1_1();
      case 258: 
        if (paramByte != 0) {
          return new IDLJavaSerializationInputStream(paramByte);
        }
        return new CDRInputStream_1_2();
      }
      ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get(paramORB, "rpc.encoding");
      throw localORBUtilSystemException.unsupportedGiopVersion(paramGIOPVersion);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\CDRInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */