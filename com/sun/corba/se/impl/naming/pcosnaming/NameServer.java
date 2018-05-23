package com.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.InitialNameService;
import com.sun.corba.se.spi.activation.InitialNameServiceHelper;
import java.io.File;
import java.io.PrintStream;
import java.util.Properties;
import org.omg.CosNaming.NamingContext;

public class NameServer
{
  private com.sun.corba.se.spi.orb.ORB orb;
  private File dbDir;
  private static final String dbName = "names.db";
  
  public static void main(String[] paramArrayOfString)
  {
    NameServer localNameServer = new NameServer(paramArrayOfString);
    localNameServer.run();
  }
  
  protected NameServer(String[] paramArrayOfString)
  {
    Properties localProperties = System.getProperties();
    localProperties.put("com.sun.CORBA.POA.ORBServerId", "1000");
    localProperties.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
    orb = ((com.sun.corba.se.spi.orb.ORB)org.omg.CORBA.ORB.init(paramArrayOfString, localProperties));
    String str = localProperties.getProperty("com.sun.CORBA.activation.DbDir") + localProperties.getProperty("file.separator") + "names.db" + localProperties.getProperty("file.separator");
    dbDir = new File(str);
    if (!dbDir.exists()) {
      dbDir.mkdir();
    }
  }
  
  protected void run()
  {
    try
    {
      NameService localNameService = new NameService(orb, dbDir);
      NamingContext localNamingContext = localNameService.initialNamingContext();
      InitialNameService localInitialNameService = InitialNameServiceHelper.narrow(orb.resolve_initial_references("InitialNameService"));
      localInitialNameService.bind("NameService", localNamingContext, true);
      System.out.println(CorbaResourceUtil.getText("pnameserv.success"));
      orb.run();
    }
    catch (Exception localException)
    {
      localException.printStackTrace(System.err);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\pcosnaming\NameServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */