package sun.rmi.transport.proxy;

import java.io.PrintStream;

final class CGIGethostnameCommand
  implements CGICommandHandler
{
  CGIGethostnameCommand() {}
  
  public String getName()
  {
    return "gethostname";
  }
  
  public void execute(String paramString)
  {
    System.out.println("Status: 200 OK");
    System.out.println("Content-type: application/octet-stream");
    System.out.println("Content-length: " + CGIHandler.ServerName.length());
    System.out.println("");
    System.out.print(CGIHandler.ServerName);
    System.out.flush();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\proxy\CGIGethostnameCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */