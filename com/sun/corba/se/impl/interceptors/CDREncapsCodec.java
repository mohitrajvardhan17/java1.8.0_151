package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.TypeCode;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecPackage.FormatMismatch;
import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
import org.omg.IOP.CodecPackage.TypeMismatch;
import sun.corba.EncapsInputStreamFactory;
import sun.corba.OutputStreamFactory;

public final class CDREncapsCodec
  extends LocalObject
  implements Codec
{
  private org.omg.CORBA.ORB orb;
  ORBUtilSystemException wrapper;
  private GIOPVersion giopVersion;
  
  public CDREncapsCodec(org.omg.CORBA.ORB paramORB, int paramInt1, int paramInt2)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)paramORB, "rpc.protocol");
    giopVersion = GIOPVersion.getInstance((byte)paramInt1, (byte)paramInt2);
  }
  
  public byte[] encode(Any paramAny)
    throws InvalidTypeForEncoding
  {
    if (paramAny == null) {
      throw wrapper.nullParam();
    }
    return encodeImpl(paramAny, true);
  }
  
  public Any decode(byte[] paramArrayOfByte)
    throws FormatMismatch
  {
    if (paramArrayOfByte == null) {
      throw wrapper.nullParam();
    }
    return decodeImpl(paramArrayOfByte, null);
  }
  
  public byte[] encode_value(Any paramAny)
    throws InvalidTypeForEncoding
  {
    if (paramAny == null) {
      throw wrapper.nullParam();
    }
    return encodeImpl(paramAny, false);
  }
  
  public Any decode_value(byte[] paramArrayOfByte, TypeCode paramTypeCode)
    throws FormatMismatch, TypeMismatch
  {
    if (paramArrayOfByte == null) {
      throw wrapper.nullParam();
    }
    if (paramTypeCode == null) {
      throw wrapper.nullParam();
    }
    return decodeImpl(paramArrayOfByte, paramTypeCode);
  }
  
  private byte[] encodeImpl(Any paramAny, boolean paramBoolean)
    throws InvalidTypeForEncoding
  {
    if (paramAny == null) {
      throw wrapper.nullParam();
    }
    EncapsOutputStream localEncapsOutputStream = OutputStreamFactory.newEncapsOutputStream((com.sun.corba.se.spi.orb.ORB)orb, giopVersion);
    localEncapsOutputStream.putEndian();
    if (paramBoolean) {
      localEncapsOutputStream.write_TypeCode(paramAny.type());
    }
    paramAny.write_value(localEncapsOutputStream);
    return localEncapsOutputStream.toByteArray();
  }
  
  private Any decodeImpl(byte[] paramArrayOfByte, TypeCode paramTypeCode)
    throws FormatMismatch
  {
    if (paramArrayOfByte == null) {
      throw wrapper.nullParam();
    }
    AnyImpl localAnyImpl = null;
    try
    {
      EncapsInputStream localEncapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(orb, paramArrayOfByte, paramArrayOfByte.length, giopVersion);
      localEncapsInputStream.consumeEndian();
      if (paramTypeCode == null) {
        paramTypeCode = localEncapsInputStream.read_TypeCode();
      }
      localAnyImpl = new AnyImpl((com.sun.corba.se.spi.orb.ORB)orb);
      localAnyImpl.read_value(localEncapsInputStream, paramTypeCode);
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new FormatMismatch();
    }
    return localAnyImpl;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\interceptors\CDREncapsCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */