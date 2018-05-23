package sun.rmi.transport.proxy;

import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

public final class CGIHandler
{
  static int ContentLength;
  static String QueryString;
  static String RequestMethod;
  static String ServerName;
  static int ServerPort;
  private static CGICommandHandler[] commands;
  private static Hashtable<String, CGICommandHandler> commandLookup;
  
  private CGIHandler() {}
  
  public static void main(String[] paramArrayOfString)
  {
    try
    {
      int i = QueryString.indexOf("=");
      String str1;
      String str2;
      if (i == -1)
      {
        str1 = QueryString;
        str2 = "";
      }
      else
      {
        str1 = QueryString.substring(0, i);
        str2 = QueryString.substring(i + 1);
      }
      CGICommandHandler localCGICommandHandler = (CGICommandHandler)commandLookup.get(str1);
      if (localCGICommandHandler != null) {
        try
        {
          localCGICommandHandler.execute(str2);
        }
        catch (CGIClientException localCGIClientException)
        {
          localCGIClientException.printStackTrace();
          returnClientError(localCGIClientException.getMessage());
        }
        catch (CGIServerException localCGIServerException)
        {
          localCGIServerException.printStackTrace();
          returnServerError(localCGIServerException.getMessage());
        }
      } else {
        returnClientError("invalid command.");
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      returnServerError("internal error: " + localException.getMessage());
    }
    System.exit(0);
  }
  
  private static void returnClientError(String paramString)
  {
    System.out.println("Status: 400 Bad Request: " + paramString);
    System.out.println("Content-type: text/html");
    System.out.println("");
    System.out.println("<HTML><HEAD><TITLE>Java RMI Client Error</TITLE></HEAD><BODY>");
    System.out.println("<H1>Java RMI Client Error</H1>");
    System.out.println("");
    System.out.println(paramString);
    System.out.println("</BODY></HTML>");
    System.exit(1);
  }
  
  private static void returnServerError(String paramString)
  {
    System.out.println("Status: 500 Server Error: " + paramString);
    System.out.println("Content-type: text/html");
    System.out.println("");
    System.out.println("<HTML><HEAD><TITLE>Java RMI Server Error</TITLE></HEAD><BODY>");
    System.out.println("<H1>Java RMI Server Error</H1>");
    System.out.println("");
    System.out.println(paramString);
    System.out.println("</BODY></HTML>");
    System.exit(1);
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        CGIHandler.ContentLength = Integer.getInteger("CONTENT_LENGTH", 0).intValue();
        CGIHandler.QueryString = System.getProperty("QUERY_STRING", "");
        CGIHandler.RequestMethod = System.getProperty("REQUEST_METHOD", "");
        CGIHandler.ServerName = System.getProperty("SERVER_NAME", "");
        CGIHandler.ServerPort = Integer.getInteger("SERVER_PORT", 0).intValue();
        return null;
      }
    });
    commands = new CGICommandHandler[] { new CGIForwardCommand(), new CGIGethostnameCommand(), new CGIPingCommand(), new CGITryHostnameCommand() };
    commandLookup = new Hashtable();
    for (int i = 0; i < commands.length; i++) {
      commandLookup.put(commands[i].getName(), commands[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\proxy\CGIHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */