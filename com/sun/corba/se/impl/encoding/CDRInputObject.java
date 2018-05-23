package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.org.omg.SendingContext.CodeBase;
import java.nio.ByteBuffer;

public class CDRInputObject
  extends CDRInputStream
  implements InputObject
{
  private CorbaConnection corbaConnection;
  private Message header;
  private boolean unmarshaledHeader;
  private ORB orb;
  private ORBUtilSystemException wrapper;
  private OMGSystemException omgWrapper;
  
  public CDRInputObject(ORB paramORB, CorbaConnection paramCorbaConnection, ByteBuffer paramByteBuffer, Message paramMessage)
  {
    super(paramORB, paramByteBuffer, paramMessage.getSize(), paramMessage.isLittleEndian(), paramMessage.getGIOPVersion(), paramMessage.getEncodingVersion(), BufferManagerFactory.newBufferManagerRead(paramMessage.getGIOPVersion(), paramMessage.getEncodingVersion(), paramORB));
    corbaConnection = paramCorbaConnection;
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.encoding");
    omgWrapper = OMGSystemException.get(paramORB, "rpc.encoding");
    if (transportDebugFlag) {
      dprint(".CDRInputObject constructor:");
    }
    getBufferManager().init(paramMessage);
    header = paramMessage;
    unmarshaledHeader = false;
    setIndex(12);
    setBufferLength(paramMessage.getSize());
  }
  
  public final CorbaConnection getConnection()
  {
    return corbaConnection;
  }
  
  public Message getMessageHeader()
  {
    return header;
  }
  
  public void unmarshalHeader()
  {
    if (!unmarshaledHeader) {
      try
      {
        if (orbtransportDebugFlag) {
          dprint(".unmarshalHeader->: " + getMessageHeader());
        }
        getMessageHeader().read(this);
        unmarshaledHeader = true;
      }
      catch (RuntimeException localRuntimeException)
      {
        if (orbtransportDebugFlag) {
          dprint(".unmarshalHeader: !!ERROR!!: " + getMessageHeader() + ": " + localRuntimeException);
        }
        throw localRuntimeException;
      }
      finally
      {
        if (orbtransportDebugFlag) {
          dprint(".unmarshalHeader<-: " + getMessageHeader());
        }
      }
    }
  }
  
  public final boolean unmarshaledHeader()
  {
    return unmarshaledHeader;
  }
  
  protected CodeSetConversion.BTCConverter createCharBTCConverter()
  {
    CodeSetComponentInfo.CodeSetContext localCodeSetContext = getCodeSets();
    if (localCodeSetContext == null) {
      return super.createCharBTCConverter();
    }
    OSFCodeSetRegistry.Entry localEntry = OSFCodeSetRegistry.lookupEntry(localCodeSetContext.getCharCodeSet());
    if (localEntry == null) {
      throw wrapper.unknownCodeset(localEntry);
    }
    return CodeSetConversion.impl().getBTCConverter(localEntry, isLittleEndian());
  }
  
  protected CodeSetConversion.BTCConverter createWCharBTCConverter()
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
    if ((localEntry == OSFCodeSetRegistry.UTF_16) && (getGIOPVersion().equals(GIOPVersion.V1_2))) {
      return CodeSetConversion.impl().getBTCConverter(localEntry, false);
    }
    return CodeSetConversion.impl().getBTCConverter(localEntry, isLittleEndian());
  }
  
  private CodeSetComponentInfo.CodeSetContext getCodeSets()
  {
    if (getConnection() == null) {
      return CodeSetComponentInfo.LOCAL_CODE_SETS;
    }
    return getConnection().getCodeSetContext();
  }
  
  public final CodeBase getCodeBase()
  {
    if (getConnection() == null) {
      return null;
    }
    return getConnection().getCodeBase();
  }
  
  public CDRInputStream dup()
  {
    return null;
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("CDRInputObject", paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\encoding\CDRInputObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */