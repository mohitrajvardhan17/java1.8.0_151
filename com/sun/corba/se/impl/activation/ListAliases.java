package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class ListAliases
  implements CommandHandler
{
  ListAliases() {}
  
  public String getCommandName()
  {
    return "listappnames";
  }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean)
  {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.listappnames"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.listappnames1"));
    }
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream)
  {
    try
    {
      Repository localRepository = RepositoryHelper.narrow(paramORB.resolve_initial_references("ServerRepository"));
      String[] arrayOfString = localRepository.getApplicationNames();
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.listappnames2"));
      paramPrintStream.println();
      for (int i = 0; i < arrayOfString.length; i++) {
        paramPrintStream.println("\t" + arrayOfString[i]);
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\activation\ListAliases.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */