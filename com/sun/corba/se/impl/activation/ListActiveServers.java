package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class ListActiveServers
  implements CommandHandler
{
  ListActiveServers() {}
  
  public String getCommandName()
  {
    return "listactive";
  }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean)
  {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.listactive"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.listactive1"));
    }
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream)
  {
    try
    {
      Repository localRepository = RepositoryHelper.narrow(paramORB.resolve_initial_references("ServerRepository"));
      Activator localActivator = ActivatorHelper.narrow(paramORB.resolve_initial_references("ServerActivator"));
      int[] arrayOfInt = localActivator.getActiveServers();
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.list2"));
      ListServers.sortServers(arrayOfInt);
      for (int i = 0; i < arrayOfInt.length; i++) {
        try
        {
          ServerDef localServerDef = localRepository.getServer(arrayOfInt[i]);
          paramPrintStream.println("\t   " + arrayOfInt[i] + "\t\t" + serverName + "\t\t" + applicationName);
        }
        catch (ServerNotRegistered localServerNotRegistered) {}
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\activation\ListActiveServers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */