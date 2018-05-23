package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.BadServerDefinition;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.ServerAlreadyActive;
import com.sun.corba.se.spi.activation.ServerAlreadyRegistered;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class RegisterServer
  implements CommandHandler
{
  RegisterServer() {}
  
  public String getCommandName()
  {
    return "register";
  }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean)
  {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.register"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.register1"));
    }
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream)
  {
    int i = 0;
    String str1 = "";
    String str2 = "";
    String str3 = "";
    String str4 = "";
    String str5 = "";
    int j = 0;
    while (i < paramArrayOfString.length)
    {
      String str6 = paramArrayOfString[(i++)];
      if (str6.equals("-server"))
      {
        if (i < paramArrayOfString.length) {
          str2 = paramArrayOfString[(i++)];
        } else {
          return true;
        }
      }
      else if (str6.equals("-applicationName"))
      {
        if (i < paramArrayOfString.length) {
          str1 = paramArrayOfString[(i++)];
        } else {
          return true;
        }
      }
      else if (str6.equals("-classpath"))
      {
        if (i < paramArrayOfString.length) {
          str3 = paramArrayOfString[(i++)];
        } else {
          return true;
        }
      }
      else if (str6.equals("-args"))
      {
        while ((i < paramArrayOfString.length) && (!paramArrayOfString[i].equals("-vmargs")))
        {
          str4 = str4 + " " + paramArrayOfString[i];
          i++;
        }
        if (str4.equals("")) {
          return true;
        }
      }
      else if (str6.equals("-vmargs"))
      {
        while ((i < paramArrayOfString.length) && (!paramArrayOfString[i].equals("-args")))
        {
          str5 = str5 + " " + paramArrayOfString[i];
          i++;
        }
        if (str5.equals("")) {
          return true;
        }
      }
      else
      {
        return true;
      }
    }
    if (str2.equals("")) {
      return true;
    }
    try
    {
      Repository localRepository = RepositoryHelper.narrow(paramORB.resolve_initial_references("ServerRepository"));
      ServerDef localServerDef = new ServerDef(str1, str2, str3, str4, str5);
      j = localRepository.registerServer(localServerDef);
      Activator localActivator = ActivatorHelper.narrow(paramORB.resolve_initial_references("ServerActivator"));
      localActivator.activate(j);
      localActivator.install(j);
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.register2", j));
    }
    catch (ServerNotRegistered localServerNotRegistered) {}catch (ServerAlreadyActive localServerAlreadyActive) {}catch (ServerHeldDown localServerHeldDown)
    {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.register3", j));
    }
    catch (ServerAlreadyRegistered localServerAlreadyRegistered)
    {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.register4", j));
    }
    catch (BadServerDefinition localBadServerDefinition)
    {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.baddef", reason));
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\activation\RegisterServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */