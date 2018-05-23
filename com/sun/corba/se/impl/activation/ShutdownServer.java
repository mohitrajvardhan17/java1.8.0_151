package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.ServerNotActive;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class ShutdownServer
  implements CommandHandler
{
  static final int illegalServerId = -1;
  
  ShutdownServer() {}
  
  public String getCommandName()
  {
    return "shutdown";
  }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean)
  {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.shutdown"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.shutdown1"));
    }
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream)
  {
    int i = -1;
    try
    {
      if (paramArrayOfString.length == 2) {
        if (paramArrayOfString[0].equals("-serverid")) {
          i = Integer.valueOf(paramArrayOfString[1]).intValue();
        } else if (paramArrayOfString[0].equals("-applicationName")) {
          i = ServerTool.getServerIdForAlias(paramORB, paramArrayOfString[1]);
        }
      }
      if (i == -1) {
        return true;
      }
      Activator localActivator = ActivatorHelper.narrow(paramORB.resolve_initial_references("ServerActivator"));
      localActivator.shutdown(i);
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.shutdown2"));
    }
    catch (ServerNotActive localServerNotActive)
    {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.servernotrunning"));
    }
    catch (ServerNotRegistered localServerNotRegistered)
    {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\activation\ShutdownServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */