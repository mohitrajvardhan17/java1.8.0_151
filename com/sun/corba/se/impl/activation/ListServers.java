package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class ListServers
  implements CommandHandler
{
  static final int illegalServerId = -1;
  
  ListServers() {}
  
  public String getCommandName()
  {
    return "list";
  }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean)
  {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.list"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.list1"));
    }
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream)
  {
    int i = -1;
    int j = 0;
    j = paramArrayOfString.length != 0 ? 1 : 0;
    if ((paramArrayOfString.length == 2) && (paramArrayOfString[0].equals("-serverid"))) {
      i = Integer.valueOf(paramArrayOfString[1]).intValue();
    }
    if ((i == -1) && (j != 0)) {
      return true;
    }
    try
    {
      Repository localRepository = RepositoryHelper.narrow(paramORB.resolve_initial_references("ServerRepository"));
      ServerDef localServerDef;
      if (j != 0)
      {
        try
        {
          localServerDef = localRepository.getServer(i);
          paramPrintStream.println();
          printServerDef(localServerDef, i, paramPrintStream);
          paramPrintStream.println();
        }
        catch (ServerNotRegistered localServerNotRegistered1)
        {
          paramPrintStream.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
        }
      }
      else
      {
        int[] arrayOfInt = localRepository.listRegisteredServers();
        paramPrintStream.println(CorbaResourceUtil.getText("servertool.list2"));
        sortServers(arrayOfInt);
        for (int k = 0; k < arrayOfInt.length; k++) {
          try
          {
            localServerDef = localRepository.getServer(arrayOfInt[k]);
            paramPrintStream.println("\t   " + arrayOfInt[k] + "\t\t" + serverName + "\t\t" + applicationName);
          }
          catch (ServerNotRegistered localServerNotRegistered2) {}
        }
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return false;
  }
  
  static void printServerDef(ServerDef paramServerDef, int paramInt, PrintStream paramPrintStream)
  {
    paramPrintStream.println(CorbaResourceUtil.getText("servertool.appname", applicationName));
    paramPrintStream.println(CorbaResourceUtil.getText("servertool.name", serverName));
    paramPrintStream.println(CorbaResourceUtil.getText("servertool.classpath", serverClassPath));
    paramPrintStream.println(CorbaResourceUtil.getText("servertool.args", serverArgs));
    paramPrintStream.println(CorbaResourceUtil.getText("servertool.vmargs", serverVmArgs));
    paramPrintStream.println(CorbaResourceUtil.getText("servertool.serverid", paramInt));
  }
  
  static void sortServers(int[] paramArrayOfInt)
  {
    int i = paramArrayOfInt.length;
    for (int k = 0; k < i; k++)
    {
      int j = k;
      for (int m = k + 1; m < i; m++) {
        if (paramArrayOfInt[m] < paramArrayOfInt[j]) {
          j = m;
        }
      }
      if (j != k)
      {
        m = paramArrayOfInt[k];
        paramArrayOfInt[k] = paramArrayOfInt[j];
        paramArrayOfInt[j] = m;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\activation\ListServers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */