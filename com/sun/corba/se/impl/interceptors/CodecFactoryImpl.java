package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import org.omg.CORBA.LocalObject;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecFactory;
import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;
import org.omg.IOP.Encoding;

public final class CodecFactoryImpl
  extends LocalObject
  implements CodecFactory
{
  private org.omg.CORBA.ORB orb;
  private ORBUtilSystemException wrapper;
  private static final int MAX_MINOR_VERSION_SUPPORTED = 2;
  private Codec[] codecs = new Codec[3];
  
  public CodecFactoryImpl(org.omg.CORBA.ORB paramORB)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)paramORB, "rpc.protocol");
    for (int i = 0; i <= 2; i++) {
      codecs[i] = new CDREncapsCodec(paramORB, 1, i);
    }
  }
  
  public Codec create_codec(Encoding paramEncoding)
    throws UnknownEncoding
  {
    if (paramEncoding == null) {
      nullParam();
    }
    Codec localCodec = null;
    if ((format == 0) && (major_version == 1) && (minor_version >= 0) && (minor_version <= 2)) {
      localCodec = codecs[minor_version];
    }
    if (localCodec == null) {
      throw new UnknownEncoding();
    }
    return localCodec;
  }
  
  private void nullParam()
  {
    throw wrapper.nullParam();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\interceptors\CodecFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */