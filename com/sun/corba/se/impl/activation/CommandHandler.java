package com.sun.corba.se.impl.activation;

import java.io.PrintStream;
import org.omg.CORBA.ORB;

public abstract interface CommandHandler
{
  public static final boolean shortHelp = true;
  public static final boolean longHelp = false;
  public static final boolean parseError = true;
  public static final boolean commandDone = false;
  
  public abstract String getCommandName();
  
  public abstract void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean);
  
  public abstract boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\activation\CommandHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */