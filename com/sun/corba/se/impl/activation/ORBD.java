package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.legacy.connection.SocketFactoryAcceptorImpl;
import com.sun.corba.se.impl.naming.cosnaming.TransientNameService;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.impl.transport.SocketOrChannelAcceptorImpl;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.Locator;
import com.sun.corba.se.spi.activation.LocatorHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import java.io.File;
import java.io.PrintStream;
import java.util.Properties;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.INTERNAL;

public class ORBD
{
  private int initSvcPort;
  protected File dbDir;
  private String dbDirName;
  protected Locator locator;
  protected Activator activator;
  protected RepositoryImpl repository;
  private static String[][] orbServers = { { "" } };
  
  public ORBD() {}
  
  protected void initializeBootNaming(ORB paramORB)
  {
    initSvcPort = paramORB.getORBData().getORBInitialPort();
    Object localObject;
    if (paramORB.getORBData().getLegacySocketFactory() == null) {
      localObject = new SocketOrChannelAcceptorImpl(paramORB, initSvcPort, "BOOT_NAMING", "IIOP_CLEAR_TEXT");
    } else {
      localObject = new SocketFactoryAcceptorImpl(paramORB, initSvcPort, "BOOT_NAMING", "IIOP_CLEAR_TEXT");
    }
    paramORB.getCorbaTransportManager().registerAcceptor((Acceptor)localObject);
  }
  
  protected ORB createORB(String[] paramArrayOfString)
  {
    Properties localProperties = System.getProperties();
    localProperties.put("com.sun.CORBA.POA.ORBServerId", "1000");
    localProperties.put("com.sun.CORBA.POA.ORBPersistentServerPort", localProperties.getProperty("com.sun.CORBA.activation.Port", Integer.toString(1049)));
    localProperties.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
    return (ORB)ORB.init(paramArrayOfString, localProperties);
  }
  
  private void run(String[] paramArrayOfString)
  {
    try
    {
      processArgs(paramArrayOfString);
      ORB localORB = createORB(paramArrayOfString);
      if (orbdDebugFlag) {
        System.out.println("ORBD begins initialization.");
      }
      boolean bool = createSystemDirs("orb.db");
      startActivationObjects(localORB);
      if (bool) {
        installOrbServers(getRepository(), getActivator());
      }
      if (orbdDebugFlag)
      {
        System.out.println("ORBD is ready.");
        System.out.println("ORBD serverid: " + System.getProperty("com.sun.CORBA.POA.ORBServerId"));
        System.out.println("activation dbdir: " + System.getProperty("com.sun.CORBA.activation.DbDir"));
        System.out.println("activation port: " + System.getProperty("com.sun.CORBA.activation.Port"));
        localObject = System.getProperty("com.sun.CORBA.activation.ServerPollingTime");
        if (localObject == null) {
          localObject = Integer.toString(1000);
        }
        System.out.println("activation Server Polling Time: " + (String)localObject + " milli-seconds ");
        String str = System.getProperty("com.sun.CORBA.activation.ServerStartupDelay");
        if (str == null) {
          str = Integer.toString(1000);
        }
        System.out.println("activation Server Startup Delay: " + str + " milli-seconds ");
      }
      Object localObject = new NameServiceStartThread(localORB, dbDir);
      ((NameServiceStartThread)localObject).start();
      localORB.run();
    }
    catch (COMM_FAILURE localCOMM_FAILURE)
    {
      System.out.println(CorbaResourceUtil.getText("orbd.commfailure"));
      System.out.println(localCOMM_FAILURE);
      localCOMM_FAILURE.printStackTrace();
    }
    catch (INTERNAL localINTERNAL)
    {
      System.out.println(CorbaResourceUtil.getText("orbd.internalexception"));
      System.out.println(localINTERNAL);
      localINTERNAL.printStackTrace();
    }
    catch (Exception localException)
    {
      System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
      System.out.println(localException);
      localException.printStackTrace();
    }
  }
  
  private void processArgs(String[] paramArrayOfString)
  {
    Properties localProperties = System.getProperties();
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (paramArrayOfString[i].equals("-port"))
      {
        if (i + 1 < paramArrayOfString.length) {
          localProperties.put("com.sun.CORBA.activation.Port", paramArrayOfString[(++i)]);
        } else {
          System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
        }
      }
      else if (paramArrayOfString[i].equals("-defaultdb"))
      {
        if (i + 1 < paramArrayOfString.length) {
          localProperties.put("com.sun.CORBA.activation.DbDir", paramArrayOfString[(++i)]);
        } else {
          System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
        }
      }
      else if (paramArrayOfString[i].equals("-serverid"))
      {
        if (i + 1 < paramArrayOfString.length) {
          localProperties.put("com.sun.CORBA.POA.ORBServerId", paramArrayOfString[(++i)]);
        } else {
          System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
        }
      }
      else if (paramArrayOfString[i].equals("-serverPollingTime"))
      {
        if (i + 1 < paramArrayOfString.length) {
          localProperties.put("com.sun.CORBA.activation.ServerPollingTime", paramArrayOfString[(++i)]);
        } else {
          System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
        }
      }
      else if (paramArrayOfString[i].equals("-serverStartupDelay")) {
        if (i + 1 < paramArrayOfString.length) {
          localProperties.put("com.sun.CORBA.activation.ServerStartupDelay", paramArrayOfString[(++i)]);
        } else {
          System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
        }
      }
    }
  }
  
  protected boolean createSystemDirs(String paramString)
  {
    boolean bool = false;
    Properties localProperties = System.getProperties();
    String str = localProperties.getProperty("file.separator");
    dbDir = new File(localProperties.getProperty("com.sun.CORBA.activation.DbDir", localProperties.getProperty("user.dir") + str + paramString));
    dbDirName = dbDir.getAbsolutePath();
    localProperties.put("com.sun.CORBA.activation.DbDir", dbDirName);
    if (!dbDir.exists())
    {
      dbDir.mkdir();
      bool = true;
    }
    File localFile = new File(dbDir, "logs");
    if (!localFile.exists()) {
      localFile.mkdir();
    }
    return bool;
  }
  
  protected File getDbDir()
  {
    return dbDir;
  }
  
  protected String getDbDirName()
  {
    return dbDirName;
  }
  
  protected void startActivationObjects(ORB paramORB)
    throws Exception
  {
    initializeBootNaming(paramORB);
    repository = new RepositoryImpl(paramORB, dbDir, orbdDebugFlag);
    paramORB.register_initial_reference("ServerRepository", repository);
    ServerManagerImpl localServerManagerImpl = new ServerManagerImpl(paramORB, paramORB.getCorbaTransportManager(), repository, getDbDirName(), orbdDebugFlag);
    locator = LocatorHelper.narrow(localServerManagerImpl);
    paramORB.register_initial_reference("ServerLocator", locator);
    activator = ActivatorHelper.narrow(localServerManagerImpl);
    paramORB.register_initial_reference("ServerActivator", activator);
    TransientNameService localTransientNameService = new TransientNameService(paramORB, "TNameService");
  }
  
  protected Locator getLocator()
  {
    return locator;
  }
  
  protected Activator getActivator()
  {
    return activator;
  }
  
  protected RepositoryImpl getRepository()
  {
    return repository;
  }
  
  protected void installOrbServers(RepositoryImpl paramRepositoryImpl, Activator paramActivator)
  {
    for (int j = 0; j < orbServers.length; j++) {
      try
      {
        String[] arrayOfString = orbServers[j];
        ServerDef localServerDef = new ServerDef(arrayOfString[1], arrayOfString[2], arrayOfString[3], arrayOfString[4], arrayOfString[5]);
        int i = Integer.valueOf(orbServers[j][0]).intValue();
        paramRepositoryImpl.registerServer(localServerDef, i);
        paramActivator.activate(i);
      }
      catch (Exception localException) {}
    }
  }
  
  public static void main(String[] paramArrayOfString)
  {
    ORBD localORBD = new ORBD();
    localORBD.run(paramArrayOfString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\activation\ORBD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */