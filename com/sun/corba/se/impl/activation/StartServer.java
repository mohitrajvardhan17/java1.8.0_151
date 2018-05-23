package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.ServerAlreadyActive;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class StartServer
  implements CommandHandler
{
  static final int illegalServerId = -1;
  
  StartServer() {}
  
  public String getCommandName()
  {
    return "startup";
  }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean)
  {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.startserver"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.startserver1"));
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
      localActivator.activate(i);
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.startserver2"));
    }
    catch (ServerNotRegistered localServerNotRegistered)
    {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
    }
    catch (ServerAlreadyActive localServerAlreadyActive)
    {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.serverup"));
    }
    catch (ServerHeldDown localServerHeldDown)
    {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.helddown"));
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\activation\StartServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */