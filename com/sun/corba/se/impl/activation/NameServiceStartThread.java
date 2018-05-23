package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.naming.pcosnaming.NameService;
import com.sun.corba.se.spi.orb.ORB;
import java.io.File;
import java.io.PrintStream;
import org.omg.CosNaming.NamingContext;

public class NameServiceStartThread
  extends Thread
{
  private ORB orb;
  private File dbDir;
  
  public NameServiceStartThread(ORB paramORB, File paramFile)
  {
    orb = paramORB;
    dbDir = paramFile;
  }
  
  public void run()
  {
    try
    {
      NameService localNameService = new NameService(orb, dbDir);
      NamingContext localNamingContext = localNameService.initialNamingContext();
      orb.register_initial_reference("NameService", localNamingContext);
    }
    catch (Exception localException)
    {
      System.err.println("NameService did not start successfully");
      localException.printStackTrace();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\activation\NameServiceStartThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */