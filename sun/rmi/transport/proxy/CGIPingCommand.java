package sun.rmi.transport.proxy;

import java.io.PrintStream;

final class CGIPingCommand
  implements CGICommandHandler
{
  CGIPingCommand() {}
  
  public String getName()
  {
    return "ping";
  }
  
  public void execute(String paramString)
  {
    System.out.println("Status: 200 OK");
    System.out.println("Content-type: application/octet-stream");
    System.out.println("Content-length: 0");
    System.out.println("");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\proxy\CGIPingCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */