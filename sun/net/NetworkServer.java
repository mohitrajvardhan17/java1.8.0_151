package sun.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkServer
  implements Runnable, Cloneable
{
  public Socket clientSocket = null;
  private Thread serverInstance;
  private ServerSocket serverSocket;
  public PrintStream clientOutput;
  public InputStream clientInput;
  
  public void close()
    throws IOException
  {
    clientSocket.close();
    clientSocket = null;
    clientInput = null;
    clientOutput = null;
  }
  
  public boolean clientIsOpen()
  {
    return clientSocket != null;
  }
  
  public final void run()
  {
    if (serverSocket != null)
    {
      Thread.currentThread().setPriority(10);
      try
      {
        for (;;)
        {
          Socket localSocket = serverSocket.accept();
          NetworkServer localNetworkServer = (NetworkServer)clone();
          serverSocket = null;
          clientSocket = localSocket;
          new Thread(localNetworkServer).start();
        }
        try
        {
          clientOutput = new PrintStream(new BufferedOutputStream(clientSocket.getOutputStream()), false, "ISO8859_1");
          clientInput = new BufferedInputStream(clientSocket.getInputStream());
          serviceRequest();
        }
        catch (Exception localException2) {}
      }
      catch (Exception localException1)
      {
        System.out.print("Server failure\n");
        localException1.printStackTrace();
        try
        {
          serverSocket.close();
        }
        catch (IOException localIOException2) {}
        System.out.print("cs=" + serverSocket + "\n");
      }
    }
    try
    {
      close();
    }
    catch (IOException localIOException1) {}
  }
  
  public final void startServer(int paramInt)
    throws IOException
  {
    serverSocket = new ServerSocket(paramInt, 50);
    serverInstance = new Thread(this);
    serverInstance.start();
  }
  
  public void serviceRequest()
    throws IOException
  {
    byte[] arrayOfByte = new byte['Ĭ'];
    clientOutput.print("Echo server " + getClass().getName() + "\n");
    clientOutput.flush();
    int i;
    while ((i = clientInput.read(arrayOfByte, 0, arrayOfByte.length)) >= 0) {
      clientOutput.write(arrayOfByte, 0, i);
    }
  }
  
  public static void main(String[] paramArrayOfString)
  {
    try
    {
      new NetworkServer().startServer(8888);
    }
    catch (IOException localIOException)
    {
      System.out.print("Server failed: " + localIOException + "\n");
    }
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  public NetworkServer() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\NetworkServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */