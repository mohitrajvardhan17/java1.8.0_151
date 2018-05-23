package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class ListORBs
  implements CommandHandler
{
  static final int illegalServerId = -1;
  
  ListORBs() {}
  
  public String getCommandName()
  {
    return "orblist";
  }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean)
  {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.orbidmap"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.orbidmap1"));
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
      String[] arrayOfString = localActivator.getORBNames(i);
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.orbidmap2"));
      for (int j = 0; j < arrayOfString.length; j++) {
        paramPrintStream.println("\t " + arrayOfString[j]);
      }
    }
    catch (ServerNotRegistered localServerNotRegistered)
    {
      paramPrintStream.println("\tno such server found.");
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\activation\ListORBs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */