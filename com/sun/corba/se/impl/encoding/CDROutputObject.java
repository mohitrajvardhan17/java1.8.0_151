package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.encoding.CorbaOutputObject;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaConnection;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.omg.CORBA.portable.InputStream;

public class CDROutputObject
  extends CorbaOutputObject
{
  private Message header;
  private ORB orb;
  private ORBUtilSystemException wrapper;
  private OMGSystemException omgWrapper;
  private CorbaConnection connection;
  
  private CDROutputObject(ORB paramORB, GIOPVersion paramGIOPVersion, Message paramMessage, BufferManagerWrite paramBufferManagerWrite, byte paramByte, CorbaMessageMediator paramCorbaMessageMediator)
  {
    super(paramORB, paramGIOPVersion, paramMessage.getEncodingVersion(), false, paramBufferManagerWrite, paramByte, (paramCorbaMessageMediator != null) && (paramCorbaMessageMediator.getConnection() != null) ? ((CorbaConnection)paramCorbaMessageMediator.getConnection()).shouldUseDirectByteBuffers() : false);
    header = paramMessage;
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.encoding");
    omgWrapper = OMGSystemException.get(paramORB, "rpc.encoding");
    getBufferManager().setOutputObject(this);
    corbaMessageMediator = paramCorbaMessageMediator;
  }
  
  public CDROutputObject(ORB paramORB, MessageMediator paramMessageMediator, Message paramMessage, byte paramByte)
  {
    this(paramORB, ((CorbaMessageMediator)paramMessageMediator).getGIOPVersion(), paramMessage, BufferManagerFactory.newBufferManagerWrite(((CorbaMessageMediator)paramMessageMediator).getGIOPVersion(), paramMessage.getEncodingVersion(), paramORB), paramByte, (CorbaMessageMediator)paramMessageMediator);
  }
  
  public CDROutputObject(ORB paramORB, MessageMediator paramMessageMediator, Message paramMessage, byte paramByte, int paramInt)
  {
    this(paramORB, ((CorbaMessageMediator)paramMessageMediator).getGIOPVersion(), paramMessage, BufferManagerFactory.newBufferManagerWrite(paramInt, paramMessage.getEncodingVersion(), paramORB), paramByte, (CorbaMessageMediator)paramMessageMediator);
  }
  
  public CDROutputObject(ORB paramORB, CorbaMessageMediator paramCorbaMessageMediator, GIOPVersion paramGIOPVersion, CorbaConnection paramCorbaConnection, Message paramMessage, byte paramByte)
  {
    this(paramORB, paramGIOPVersion, paramMessage, BufferManagerFactory.newBufferManagerWrite(paramGIOPVersion, paramMessage.getEncodingVersion(), paramORB), paramByte, paramCorbaMessageMediator);
    connection = paramCorbaConnection;
  }
  
  public Message getMessageHeader()
  {
    return header;
  }
  
  public final void finishSendingMessage()
  {
    getBufferManager().sendMessage();
  }
  
  public void writeTo(CorbaConnection paramCorbaConnection)
    throws IOException
  {
    ByteBufferWithInfo localByteBufferWithInfo = getByteBufferWithInfo();
    getMessageHeader().setSize(byteBuffer, localByteBufferWithInfo.getSize());
    if (orb() != null)
    {
      if (orbtransportDebugFlag) {
        dprint(".writeTo: " + paramCorbaConnection);
      }
      if (orbgiopDebugFlag) {
        CDROutputStream_1_0.printBuffer(localByteBufferWithInfo);
      }
    }
    byteBuffer.position(0).limit(localByteBufferWithInfo.getSize());
    paramCorbaConnection.write(byteBuffer);
  }
  
  public InputStream create_input_stream()
  {
    return null;
  }
  
  public CorbaConnection getConnection()
  {
    if (connection != null) {
      return connection;
    }
    return (CorbaConnection)corbaMessageMediator.getConnection();
  }
  
  public final ByteBufferWithInfo getByteBufferWithInfo()
  {
    return super.getByteBufferWithInfo();
  }
  
  public final void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo)
  {
    super.setByteBufferWithInfo(paramByteBufferWithInfo);
  }
  
  protected CodeSetConversion.CTBConverter createCharCTBConverter()
  {
    CodeSetComponentInfo.CodeSetContext localCodeSetContext = getCodeSets();
    if (localCodeSetContext == null) {
      return super.createCharCTBConverter();
    }
    OSFCodeSetRegistry.Entry localEntry = OSFCodeSetRegistry.lookupEntry(localCodeSetContext.getCharCodeSet());
    if (localEntry == null) {
      throw wrapper.unknownCodeset(localEntry);
    }
    return CodeSetConversion.impl().getCTBConverter(localEntry, isLittleEndian(), false);
  }
  
  protected CodeSetConversion.CTBConverter createWCharCTBConverter()
  {
    CodeSetComponentInfo.CodeSetContext localCodeSetContext = getCodeSets();
    if (localCodeSetContext == null)
    {
      if (getConnection().isServer()) {
        throw omgWrapper.noClientWcharCodesetCtx();
      }
      throw omgWrapper.noServerWcharCodesetCmp();
    }
    OSFCodeSetRegistry.Entry localEntry = OSFCodeSetRegistry.lookupEntry(localCodeSetContext.getWCharCodeSet());
    if (localEntry == null) {
      throw wrapper.unknownCodeset(localEntry);
    }
    boolean bool = ((ORB)orb()).getORBData().useByteOrderMarkers();
    if (localEntry == OSFCodeSetRegistry.UTF_16)
    {
      if (getGIOPVersion().equals(GIOPVersion.V1_2)) {
        return CodeSetConversion.impl().getCTBConverter(localEntry, false, bool);
      }
      if (getGIOPVersion().equals(GIOPVersion.V1_1)) {
        return CodeSetConversion.impl().getCTBConverter(localEntry, isLittleEndian(), false);
      }
    }
    return CodeSetConversion.impl().getCTBConverter(localEntry, isLittleEndian(), bool);
  }
  
  private CodeSetComponentInfo.CodeSetContext getCodeSets()
  {
    if (getConnection() == null) {
      return CodeSetComponentInfo.LOCAL_CODE_SETS;
    }
    return getConnection().getCodeSetContext();
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("CDROutputObject", paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\CDROutputObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */