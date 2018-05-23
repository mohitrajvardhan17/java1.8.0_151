package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.org.omg.SendingContext.CodeBase;
import java.nio.ByteBuffer;
import org.omg.CORBA.CompletionStatus;
import sun.corba.EncapsInputStreamFactory;

public class EncapsInputStream
  extends CDRInputStream
{
  private ORBUtilSystemException wrapper;
  private CodeBase codeBase;
  
  public EncapsInputStream(org.omg.CORBA.ORB paramORB, byte[] paramArrayOfByte, int paramInt, boolean paramBoolean, GIOPVersion paramGIOPVersion)
  {
    super(paramORB, ByteBuffer.wrap(paramArrayOfByte), paramInt, paramBoolean, paramGIOPVersion, (byte)0, BufferManagerFactory.newBufferManagerRead(0, (byte)0, (com.sun.corba.se.spi.orb.ORB)paramORB));
    wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)paramORB, "rpc.encoding");
    performORBVersionSpecificInit();
  }
  
  public EncapsInputStream(org.omg.CORBA.ORB paramORB, ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean, GIOPVersion paramGIOPVersion)
  {
    super(paramORB, paramByteBuffer, paramInt, paramBoolean, paramGIOPVersion, (byte)0, BufferManagerFactory.newBufferManagerRead(0, (byte)0, (com.sun.corba.se.spi.orb.ORB)paramORB));
    performORBVersionSpecificInit();
  }
  
  public EncapsInputStream(org.omg.CORBA.ORB paramORB, byte[] paramArrayOfByte, int paramInt)
  {
    this(paramORB, paramArrayOfByte, paramInt, GIOPVersion.V1_2);
  }
  
  public EncapsInputStream(EncapsInputStream paramEncapsInputStream)
  {
    super(paramEncapsInputStream);
    wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)paramEncapsInputStream.orb(), "rpc.encoding");
    performORBVersionSpecificInit();
  }
  
  public EncapsInputStream(org.omg.CORBA.ORB paramORB, byte[] paramArrayOfByte, int paramInt, GIOPVersion paramGIOPVersion)
  {
    this(paramORB, paramArrayOfByte, paramInt, false, paramGIOPVersion);
  }
  
  public EncapsInputStream(org.omg.CORBA.ORB paramORB, byte[] paramArrayOfByte, int paramInt, GIOPVersion paramGIOPVersion, CodeBase paramCodeBase)
  {
    super(paramORB, ByteBuffer.wrap(paramArrayOfByte), paramInt, false, paramGIOPVersion, (byte)0, BufferManagerFactory.newBufferManagerRead(0, (byte)0, (com.sun.corba.se.spi.orb.ORB)paramORB));
    codeBase = paramCodeBase;
    performORBVersionSpecificInit();
  }
  
  public CDRInputStream dup()
  {
    return EncapsInputStreamFactory.newEncapsInputStream(this);
  }
  
  protected CodeSetConversion.BTCConverter createCharBTCConverter()
  {
    return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.ISO_8859_1);
  }
  
  protected CodeSetConversion.BTCConverter createWCharBTCConverter()
  {
    if (getGIOPVersion().equals(GIOPVersion.V1_0)) {
      throw wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE);
    }
    if (getGIOPVersion().equals(GIOPVersion.V1_1)) {
      return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16, isLittleEndian());
    }
    return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16, false);
  }
  
  public CodeBase getCodeBase()
  {
    return codeBase;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\EncapsInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */