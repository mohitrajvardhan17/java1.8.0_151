package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo.CodeSetComponent;
import com.sun.corba.se.impl.legacy.connection.USLPort;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.DataCollector;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.ParserImplTableBase;
import com.sun.corba.se.spi.orb.StringPair;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
import com.sun.corba.se.spi.transport.IORToSocketInfo;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import java.net.URL;
import org.omg.CORBA.CompletionStatus;
import org.omg.PortableInterceptor.ORBInitializer;

public class ORBDataParserImpl
  extends ParserImplTableBase
  implements ORBData
{
  private ORB orb;
  private ORBUtilSystemException wrapper;
  private String ORBInitialHost;
  private int ORBInitialPort;
  private String ORBServerHost;
  private int ORBServerPort;
  private String listenOnAllInterfaces;
  private com.sun.corba.se.spi.legacy.connection.ORBSocketFactory legacySocketFactory;
  private com.sun.corba.se.spi.transport.ORBSocketFactory socketFactory;
  private USLPort[] userSpecifiedListenPorts;
  private IORToSocketInfo iorToSocketInfo;
  private IIOPPrimaryToContactInfo iiopPrimaryToContactInfo;
  private String orbId;
  private boolean orbServerIdPropertySpecified;
  private URL servicesURL;
  private String propertyInitRef;
  private boolean allowLocalOptimization;
  private GIOPVersion giopVersion;
  private int highWaterMark;
  private int lowWaterMark;
  private int numberToReclaim;
  private int giopFragmentSize;
  private int giopBufferSize;
  private int giop11BuffMgr;
  private int giop12BuffMgr;
  private short giopTargetAddressPreference;
  private short giopAddressDisposition;
  private boolean useByteOrderMarkers;
  private boolean useByteOrderMarkersInEncaps;
  private boolean alwaysSendCodeSetCtx;
  private boolean persistentPortInitialized;
  private int persistentServerPort;
  private boolean persistentServerIdInitialized;
  private int persistentServerId;
  private boolean serverIsORBActivated;
  private Class badServerIdHandlerClass;
  private CodeSetComponentInfo.CodeSetComponent charData;
  private CodeSetComponentInfo.CodeSetComponent wcharData;
  private ORBInitializer[] orbInitializers;
  private StringPair[] orbInitialReferences;
  private String defaultInitRef;
  private String[] debugFlags;
  private Acceptor[] acceptors;
  private CorbaContactInfoListFactory corbaContactInfoListFactory;
  private String acceptorSocketType;
  private boolean acceptorSocketUseSelectThreadToWait;
  private boolean acceptorSocketUseWorkerThreadForEvent;
  private String connectionSocketType;
  private boolean connectionSocketUseSelectThreadToWait;
  private boolean connectionSocketUseWorkerThreadForEvent;
  private ReadTimeouts readTimeouts;
  private boolean disableDirectByteBufferUse;
  private boolean enableJavaSerialization;
  private boolean useRepId;
  private CodeSetComponentInfo codesets;
  
  public String getORBInitialHost()
  {
    return ORBInitialHost;
  }
  
  public int getORBInitialPort()
  {
    return ORBInitialPort;
  }
  
  public String getORBServerHost()
  {
    return ORBServerHost;
  }
  
  public String getListenOnAllInterfaces()
  {
    return listenOnAllInterfaces;
  }
  
  public int getORBServerPort()
  {
    return ORBServerPort;
  }
  
  public com.sun.corba.se.spi.legacy.connection.ORBSocketFactory getLegacySocketFactory()
  {
    return legacySocketFactory;
  }
  
  public com.sun.corba.se.spi.transport.ORBSocketFactory getSocketFactory()
  {
    return socketFactory;
  }
  
  public USLPort[] getUserSpecifiedListenPorts()
  {
    return userSpecifiedListenPorts;
  }
  
  public IORToSocketInfo getIORToSocketInfo()
  {
    return iorToSocketInfo;
  }
  
  public IIOPPrimaryToContactInfo getIIOPPrimaryToContactInfo()
  {
    return iiopPrimaryToContactInfo;
  }
  
  public String getORBId()
  {
    return orbId;
  }
  
  public boolean getORBServerIdPropertySpecified()
  {
    return orbServerIdPropertySpecified;
  }
  
  public boolean isLocalOptimizationAllowed()
  {
    return allowLocalOptimization;
  }
  
  public GIOPVersion getGIOPVersion()
  {
    return giopVersion;
  }
  
  public int getHighWaterMark()
  {
    return highWaterMark;
  }
  
  public int getLowWaterMark()
  {
    return lowWaterMark;
  }
  
  public int getNumberToReclaim()
  {
    return numberToReclaim;
  }
  
  public int getGIOPFragmentSize()
  {
    return giopFragmentSize;
  }
  
  public int getGIOPBufferSize()
  {
    return giopBufferSize;
  }
  
  public int getGIOPBuffMgrStrategy(GIOPVersion paramGIOPVersion)
  {
    if (paramGIOPVersion != null)
    {
      if (paramGIOPVersion.equals(GIOPVersion.V1_0)) {
        return 0;
      }
      if (paramGIOPVersion.equals(GIOPVersion.V1_1)) {
        return giop11BuffMgr;
      }
      if (paramGIOPVersion.equals(GIOPVersion.V1_2)) {
        return giop12BuffMgr;
      }
    }
    return 0;
  }
  
  public short getGIOPTargetAddressPreference()
  {
    return giopTargetAddressPreference;
  }
  
  public short getGIOPAddressDisposition()
  {
    return giopAddressDisposition;
  }
  
  public boolean useByteOrderMarkers()
  {
    return useByteOrderMarkers;
  }
  
  public boolean useByteOrderMarkersInEncapsulations()
  {
    return useByteOrderMarkersInEncaps;
  }
  
  public boolean alwaysSendCodeSetServiceContext()
  {
    return alwaysSendCodeSetCtx;
  }
  
  public boolean getPersistentPortInitialized()
  {
    return persistentPortInitialized;
  }
  
  public int getPersistentServerPort()
  {
    if (persistentPortInitialized) {
      return persistentServerPort;
    }
    throw wrapper.persistentServerportNotSet(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public boolean getPersistentServerIdInitialized()
  {
    return persistentServerIdInitialized;
  }
  
  public int getPersistentServerId()
  {
    if (persistentServerIdInitialized) {
      return persistentServerId;
    }
    throw wrapper.persistentServeridNotSet(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public boolean getServerIsORBActivated()
  {
    return serverIsORBActivated;
  }
  
  public Class getBadServerIdHandler()
  {
    return badServerIdHandlerClass;
  }
  
  public CodeSetComponentInfo getCodeSetComponentInfo()
  {
    return codesets;
  }
  
  public ORBInitializer[] getORBInitializers()
  {
    return orbInitializers;
  }
  
  public StringPair[] getORBInitialReferences()
  {
    return orbInitialReferences;
  }
  
  public String getORBDefaultInitialReference()
  {
    return defaultInitRef;
  }
  
  public String[] getORBDebugFlags()
  {
    return debugFlags;
  }
  
  public Acceptor[] getAcceptors()
  {
    return acceptors;
  }
  
  public CorbaContactInfoListFactory getCorbaContactInfoListFactory()
  {
    return corbaContactInfoListFactory;
  }
  
  public String acceptorSocketType()
  {
    return acceptorSocketType;
  }
  
  public boolean acceptorSocketUseSelectThreadToWait()
  {
    return acceptorSocketUseSelectThreadToWait;
  }
  
  public boolean acceptorSocketUseWorkerThreadForEvent()
  {
    return acceptorSocketUseWorkerThreadForEvent;
  }
  
  public String connectionSocketType()
  {
    return connectionSocketType;
  }
  
  public boolean connectionSocketUseSelectThreadToWait()
  {
    return connectionSocketUseSelectThreadToWait;
  }
  
  public boolean connectionSocketUseWorkerThreadForEvent()
  {
    return connectionSocketUseWorkerThreadForEvent;
  }
  
  public boolean isJavaSerializationEnabled()
  {
    return enableJavaSerialization;
  }
  
  public ReadTimeouts getTransportTCPReadTimeouts()
  {
    return readTimeouts;
  }
  
  public boolean disableDirectByteBufferUse()
  {
    return disableDirectByteBufferUse;
  }
  
  public boolean useRepId()
  {
    return useRepId;
  }
  
  public ORBDataParserImpl(ORB paramORB, DataCollector paramDataCollector)
  {
    super(ParserTable.get().getParserData());
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "orb.lifecycle");
    init(paramDataCollector);
    complete();
  }
  
  public void complete()
  {
    codesets = new CodeSetComponentInfo(charData, wcharData);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\ORBDataParserImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */