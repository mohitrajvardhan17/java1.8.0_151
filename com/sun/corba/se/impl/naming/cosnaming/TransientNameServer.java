package com.sun.corba.se.impl.naming.cosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;
import java.util.Properties;
import org.omg.CORBA.SystemException;

public class TransientNameServer
{
  private static boolean debug = false;
  static NamingSystemException wrapper = NamingSystemException.get("naming");
  
  public static void trace(String paramString)
  {
    if (debug) {
      System.out.println(paramString);
    }
  }
  
  public static void initDebug(String[] paramArrayOfString)
  {
    if (debug) {
      return;
    }
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (paramArrayOfString[i].equalsIgnoreCase("-debug"))
      {
        debug = true;
        return;
      }
    }
    debug = false;
  }
  
  private static org.omg.CORBA.Object initializeRootNamingContext(org.omg.CORBA.ORB paramORB)
  {
    Object localObject = null;
    try
    {
      com.sun.corba.se.spi.orb.ORB localORB = (com.sun.corba.se.spi.orb.ORB)paramORB;
      TransientNameService localTransientNameService = new TransientNameService(localORB);
      return localTransientNameService.initialNamingContext();
    }
    catch (SystemException localSystemException)
    {
      throw wrapper.transNsCannotCreateInitialNcSys(localSystemException);
    }
    catch (Exception localException)
    {
      throw wrapper.transNsCannotCreateInitialNc(localException);
    }
  }
  
  public static void main(String[] paramArrayOfString)
  {
    initDebug(paramArrayOfString);
    int i = 0;
    int j = 0;
    int k = 0;
    try
    {
      trace("Transient name server started with args " + paramArrayOfString);
      Properties localProperties = System.getProperties();
      localProperties.put("com.sun.CORBA.POA.ORBServerId", "1000000");
      localProperties.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
      try
      {
        String str1 = System.getProperty("org.omg.CORBA.ORBInitialPort");
        if ((str1 != null) && (str1.length() > 0))
        {
          k = Integer.parseInt(str1);
          if (k == 0)
          {
            j = 1;
            throw wrapper.transientNameServerBadPort();
          }
        }
        localObject1 = System.getProperty("org.omg.CORBA.ORBInitialHost");
        if (localObject1 != null)
        {
          i = 1;
          throw wrapper.transientNameServerBadHost();
        }
      }
      catch (NumberFormatException localNumberFormatException) {}
      for (int m = 0; m < paramArrayOfString.length; m++)
      {
        if ((paramArrayOfString[m].equals("-ORBInitialPort")) && (m < paramArrayOfString.length - 1))
        {
          k = Integer.parseInt(paramArrayOfString[(m + 1)]);
          if (k == 0)
          {
            j = 1;
            throw wrapper.transientNameServerBadPort();
          }
        }
        if (paramArrayOfString[m].equals("-ORBInitialHost"))
        {
          i = 1;
          throw wrapper.transientNameServerBadHost();
        }
      }
      if (k == 0)
      {
        k = 900;
        localProperties.put("org.omg.CORBA.ORBInitialPort", Integer.toString(k));
      }
      localProperties.put("com.sun.CORBA.POA.ORBPersistentServerPort", Integer.toString(k));
      org.omg.CORBA.ORB localORB = org.omg.CORBA.ORB.init(paramArrayOfString, localProperties);
      trace("ORB object returned from init: " + localORB);
      Object localObject1 = initializeRootNamingContext(localORB);
      ((com.sun.corba.se.org.omg.CORBA.ORB)localORB).register_initial_reference("NamingService", (org.omg.CORBA.Object)localObject1);
      String str2 = null;
      if (localObject1 != null)
      {
        str2 = localORB.object_to_string((org.omg.CORBA.Object)localObject1);
      }
      else
      {
        NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.exception", k));
        NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.usage"));
        System.exit(1);
      }
      trace("name service created");
      System.out.println(CorbaResourceUtil.getText("tnameserv.hs1", str2));
      System.out.println(CorbaResourceUtil.getText("tnameserv.hs2", k));
      System.out.println(CorbaResourceUtil.getText("tnameserv.hs3"));
      Object localObject2 = new Object();
      synchronized (localObject2)
      {
        localObject2.wait();
      }
    }
    catch (Exception localException)
    {
      if (i != 0)
      {
        NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.invalidhostoption"));
      }
      else if (j != 0)
      {
        NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.orbinitialport0"));
      }
      else
      {
        NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.exception", k));
        NamingUtils.errprint(CorbaResourceUtil.getText("tnameserv.usage"));
      }
      localException.printStackTrace();
    }
  }
  
  private TransientNameServer() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\TransientNameServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */