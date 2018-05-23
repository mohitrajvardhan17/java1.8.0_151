package sun.rmi.transport.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.server.LogStream;
import java.security.AccessController;
import java.util.Enumeration;
import java.util.Hashtable;
import sun.rmi.runtime.Log;
import sun.rmi.transport.Connection;
import sun.security.action.GetPropertyAction;

final class ConnectionMultiplexer
{
  static int logLevel = LogStream.parseLevel(getLogLevel());
  static final Log multiplexLog = Log.getLog("sun.rmi.transport.tcp.multiplex", "multiplex", logLevel);
  private static final int OPEN = 225;
  private static final int CLOSE = 226;
  private static final int CLOSEACK = 227;
  private static final int REQUEST = 228;
  private static final int TRANSMIT = 229;
  private TCPChannel channel;
  private InputStream in;
  private OutputStream out;
  private boolean orig;
  private DataInputStream dataIn;
  private DataOutputStream dataOut;
  private Hashtable<Integer, MultiplexConnectionInfo> connectionTable = new Hashtable(7);
  private int numConnections = 0;
  private static final int maxConnections = 256;
  private int lastID = 4097;
  private boolean alive = true;
  
  private static String getLogLevel()
  {
    return (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.transport.tcp.multiplex.logLevel"));
  }
  
  public ConnectionMultiplexer(TCPChannel paramTCPChannel, InputStream paramInputStream, OutputStream paramOutputStream, boolean paramBoolean)
  {
    channel = paramTCPChannel;
    in = paramInputStream;
    out = paramOutputStream;
    orig = paramBoolean;
    dataIn = new DataInputStream(paramInputStream);
    dataOut = new DataOutputStream(paramOutputStream);
  }
  
  public void run()
    throws IOException
  {
    try
    {
      int i;
      for (;;)
      {
        i = dataIn.readUnsignedByte();
        int j;
        MultiplexConnectionInfo localMultiplexConnectionInfo;
        int k;
        switch (i)
        {
        case 225: 
          j = dataIn.readUnsignedShort();
          if (multiplexLog.isLoggable(Log.VERBOSE)) {
            multiplexLog.log(Log.VERBOSE, "operation  OPEN " + j);
          }
          localMultiplexConnectionInfo = (MultiplexConnectionInfo)connectionTable.get(Integer.valueOf(j));
          if (localMultiplexConnectionInfo != null) {
            throw new IOException("OPEN: Connection ID already exists");
          }
          localMultiplexConnectionInfo = new MultiplexConnectionInfo(j);
          in = new MultiplexInputStream(this, localMultiplexConnectionInfo, 2048);
          out = new MultiplexOutputStream(this, localMultiplexConnectionInfo, 2048);
          synchronized (connectionTable)
          {
            connectionTable.put(Integer.valueOf(j), localMultiplexConnectionInfo);
            numConnections += 1;
          }
          ??? = new TCPConnection(channel, in, out);
          channel.acceptMultiplexConnection((Connection)???);
          break;
        case 226: 
          j = dataIn.readUnsignedShort();
          if (multiplexLog.isLoggable(Log.VERBOSE)) {
            multiplexLog.log(Log.VERBOSE, "operation  CLOSE " + j);
          }
          localMultiplexConnectionInfo = (MultiplexConnectionInfo)connectionTable.get(Integer.valueOf(j));
          if (localMultiplexConnectionInfo == null) {
            throw new IOException("CLOSE: Invalid connection ID");
          }
          in.disconnect();
          out.disconnect();
          if (!closed) {
            sendCloseAck(localMultiplexConnectionInfo);
          }
          synchronized (connectionTable)
          {
            connectionTable.remove(Integer.valueOf(j));
            numConnections -= 1;
          }
          break;
        case 227: 
          j = dataIn.readUnsignedShort();
          if (multiplexLog.isLoggable(Log.VERBOSE)) {
            multiplexLog.log(Log.VERBOSE, "operation  CLOSEACK " + j);
          }
          localMultiplexConnectionInfo = (MultiplexConnectionInfo)connectionTable.get(Integer.valueOf(j));
          if (localMultiplexConnectionInfo == null) {
            throw new IOException("CLOSEACK: Invalid connection ID");
          }
          if (!closed) {
            throw new IOException("CLOSEACK: Connection not closed");
          }
          in.disconnect();
          out.disconnect();
          synchronized (connectionTable)
          {
            connectionTable.remove(Integer.valueOf(j));
            numConnections -= 1;
          }
          break;
        case 228: 
          j = dataIn.readUnsignedShort();
          localMultiplexConnectionInfo = (MultiplexConnectionInfo)connectionTable.get(Integer.valueOf(j));
          if (localMultiplexConnectionInfo == null) {
            throw new IOException("REQUEST: Invalid connection ID");
          }
          k = dataIn.readInt();
          if (multiplexLog.isLoggable(Log.VERBOSE)) {
            multiplexLog.log(Log.VERBOSE, "operation  REQUEST " + j + ": " + k);
          }
          out.request(k);
          break;
        case 229: 
          j = dataIn.readUnsignedShort();
          localMultiplexConnectionInfo = (MultiplexConnectionInfo)connectionTable.get(Integer.valueOf(j));
          if (localMultiplexConnectionInfo == null) {
            throw new IOException("SEND: Invalid connection ID");
          }
          k = dataIn.readInt();
          if (multiplexLog.isLoggable(Log.VERBOSE)) {
            multiplexLog.log(Log.VERBOSE, "operation  TRANSMIT " + j + ": " + k);
          }
          in.receive(k, dataIn);
        }
      }
      throw new IOException("Invalid operation: " + Integer.toHexString(i));
    }
    finally
    {
      shutDown();
    }
  }
  
  public synchronized TCPConnection openConnection()
    throws IOException
  {
    int i;
    do
    {
      lastID = (++lastID & 0x7FFF);
      i = lastID;
      if (orig) {
        i |= 0x8000;
      }
    } while (connectionTable.get(Integer.valueOf(i)) != null);
    MultiplexConnectionInfo localMultiplexConnectionInfo = new MultiplexConnectionInfo(i);
    in = new MultiplexInputStream(this, localMultiplexConnectionInfo, 2048);
    out = new MultiplexOutputStream(this, localMultiplexConnectionInfo, 2048);
    synchronized (connectionTable)
    {
      if (!alive) {
        throw new IOException("Multiplexer connection dead");
      }
      if (numConnections >= 256) {
        throw new IOException("Cannot exceed 256 simultaneous multiplexed connections");
      }
      connectionTable.put(Integer.valueOf(i), localMultiplexConnectionInfo);
      numConnections += 1;
    }
    synchronized (dataOut)
    {
      try
      {
        dataOut.writeByte(225);
        dataOut.writeShort(i);
        dataOut.flush();
      }
      catch (IOException localIOException)
      {
        multiplexLog.log(Log.BRIEF, "exception: ", localIOException);
        shutDown();
        throw localIOException;
      }
    }
    return new TCPConnection(channel, in, out);
  }
  
  public void shutDown()
  {
    synchronized (connectionTable)
    {
      if (!alive) {
        return;
      }
      alive = false;
      Enumeration localEnumeration = connectionTable.elements();
      while (localEnumeration.hasMoreElements())
      {
        MultiplexConnectionInfo localMultiplexConnectionInfo = (MultiplexConnectionInfo)localEnumeration.nextElement();
        in.disconnect();
        out.disconnect();
      }
      connectionTable.clear();
      numConnections = 0;
    }
    try
    {
      in.close();
    }
    catch (IOException localIOException1) {}
    try
    {
      out.close();
    }
    catch (IOException localIOException2) {}
  }
  
  void sendRequest(MultiplexConnectionInfo paramMultiplexConnectionInfo, int paramInt)
    throws IOException
  {
    synchronized (dataOut)
    {
      if ((alive) && (!closed)) {
        try
        {
          dataOut.writeByte(228);
          dataOut.writeShort(id);
          dataOut.writeInt(paramInt);
          dataOut.flush();
        }
        catch (IOException localIOException)
        {
          multiplexLog.log(Log.BRIEF, "exception: ", localIOException);
          shutDown();
          throw localIOException;
        }
      }
    }
  }
  
  void sendTransmit(MultiplexConnectionInfo paramMultiplexConnectionInfo, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    synchronized (dataOut)
    {
      if ((alive) && (!closed)) {
        try
        {
          dataOut.writeByte(229);
          dataOut.writeShort(id);
          dataOut.writeInt(paramInt2);
          dataOut.write(paramArrayOfByte, paramInt1, paramInt2);
          dataOut.flush();
        }
        catch (IOException localIOException)
        {
          multiplexLog.log(Log.BRIEF, "exception: ", localIOException);
          shutDown();
          throw localIOException;
        }
      }
    }
  }
  
  void sendClose(MultiplexConnectionInfo paramMultiplexConnectionInfo)
    throws IOException
  {
    out.disconnect();
    synchronized (dataOut)
    {
      if ((alive) && (!closed)) {
        try
        {
          dataOut.writeByte(226);
          dataOut.writeShort(id);
          dataOut.flush();
          closed = true;
        }
        catch (IOException localIOException)
        {
          multiplexLog.log(Log.BRIEF, "exception: ", localIOException);
          shutDown();
          throw localIOException;
        }
      }
    }
  }
  
  void sendCloseAck(MultiplexConnectionInfo paramMultiplexConnectionInfo)
    throws IOException
  {
    synchronized (dataOut)
    {
      if ((alive) && (!closed)) {
        try
        {
          dataOut.writeByte(227);
          dataOut.writeShort(id);
          dataOut.flush();
          closed = true;
        }
        catch (IOException localIOException)
        {
          multiplexLog.log(Log.BRIEF, "exception: ", localIOException);
          shutDown();
          throw localIOException;
        }
      }
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    super.finalize();
    shutDown();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\tcp\ConnectionMultiplexer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */