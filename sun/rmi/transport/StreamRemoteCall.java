package sun.rmi.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.rmi.MarshalException;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteCall;
import sun.rmi.runtime.Log;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.tcp.TCPEndpoint;

public class StreamRemoteCall
  implements RemoteCall
{
  private ConnectionInputStream in = null;
  private ConnectionOutputStream out = null;
  private Connection conn;
  private boolean resultStarted = false;
  private Exception serverException = null;
  
  public StreamRemoteCall(Connection paramConnection)
  {
    conn = paramConnection;
  }
  
  public StreamRemoteCall(Connection paramConnection, ObjID paramObjID, int paramInt, long paramLong)
    throws RemoteException
  {
    try
    {
      conn = paramConnection;
      Transport.transportLog.log(Log.VERBOSE, "write remote call header...");
      conn.getOutputStream().write(80);
      getOutputStream();
      paramObjID.write(out);
      out.writeInt(paramInt);
      out.writeLong(paramLong);
    }
    catch (IOException localIOException)
    {
      throw new MarshalException("Error marshaling call header", localIOException);
    }
  }
  
  public Connection getConnection()
  {
    return conn;
  }
  
  public ObjectOutput getOutputStream()
    throws IOException
  {
    return getOutputStream(false);
  }
  
  private ObjectOutput getOutputStream(boolean paramBoolean)
    throws IOException
  {
    if (out == null)
    {
      Transport.transportLog.log(Log.VERBOSE, "getting output stream");
      out = new ConnectionOutputStream(conn, paramBoolean);
    }
    return out;
  }
  
  /* Error */
  public void releaseOutputStream()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 235	sun/rmi/transport/StreamRemoteCall:out	Lsun/rmi/transport/ConnectionOutputStream;
    //   4: ifnull +30 -> 34
    //   7: aload_0
    //   8: getfield 235	sun/rmi/transport/StreamRemoteCall:out	Lsun/rmi/transport/ConnectionOutputStream;
    //   11: invokevirtual 269	sun/rmi/transport/ConnectionOutputStream:flush	()V
    //   14: aload_0
    //   15: getfield 235	sun/rmi/transport/StreamRemoteCall:out	Lsun/rmi/transport/ConnectionOutputStream;
    //   18: invokevirtual 268	sun/rmi/transport/ConnectionOutputStream:done	()V
    //   21: goto +13 -> 34
    //   24: astore_1
    //   25: aload_0
    //   26: getfield 235	sun/rmi/transport/StreamRemoteCall:out	Lsun/rmi/transport/ConnectionOutputStream;
    //   29: invokevirtual 268	sun/rmi/transport/ConnectionOutputStream:done	()V
    //   32: aload_1
    //   33: athrow
    //   34: aload_0
    //   35: getfield 233	sun/rmi/transport/StreamRemoteCall:conn	Lsun/rmi/transport/Connection;
    //   38: invokeinterface 287 1 0
    //   43: aload_0
    //   44: aconst_null
    //   45: putfield 235	sun/rmi/transport/StreamRemoteCall:out	Lsun/rmi/transport/ConnectionOutputStream;
    //   48: goto +11 -> 59
    //   51: astore_2
    //   52: aload_0
    //   53: aconst_null
    //   54: putfield 235	sun/rmi/transport/StreamRemoteCall:out	Lsun/rmi/transport/ConnectionOutputStream;
    //   57: aload_2
    //   58: athrow
    //   59: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	60	0	this	StreamRemoteCall
    //   24	9	1	localObject1	Object
    //   51	7	2	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   7	14	24	finally
    //   0	43	51	finally
  }
  
  public ObjectInput getInputStream()
    throws IOException
  {
    if (in == null)
    {
      Transport.transportLog.log(Log.VERBOSE, "getting input stream");
      in = new ConnectionInputStream(conn.getInputStream());
    }
    return in;
  }
  
  /* Error */
  public void releaseInputStream()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 234	sun/rmi/transport/StreamRemoteCall:in	Lsun/rmi/transport/ConnectionInputStream;
    //   4: ifnull +32 -> 36
    //   7: aload_0
    //   8: getfield 234	sun/rmi/transport/StreamRemoteCall:in	Lsun/rmi/transport/ConnectionInputStream;
    //   11: invokevirtual 262	sun/rmi/transport/ConnectionInputStream:done	()V
    //   14: goto +4 -> 18
    //   17: astore_1
    //   18: aload_0
    //   19: getfield 234	sun/rmi/transport/StreamRemoteCall:in	Lsun/rmi/transport/ConnectionInputStream;
    //   22: invokevirtual 264	sun/rmi/transport/ConnectionInputStream:registerRefs	()V
    //   25: aload_0
    //   26: getfield 234	sun/rmi/transport/StreamRemoteCall:in	Lsun/rmi/transport/ConnectionInputStream;
    //   29: aload_0
    //   30: getfield 233	sun/rmi/transport/StreamRemoteCall:conn	Lsun/rmi/transport/Connection;
    //   33: invokevirtual 267	sun/rmi/transport/ConnectionInputStream:done	(Lsun/rmi/transport/Connection;)V
    //   36: aload_0
    //   37: getfield 233	sun/rmi/transport/StreamRemoteCall:conn	Lsun/rmi/transport/Connection;
    //   40: invokeinterface 286 1 0
    //   45: aload_0
    //   46: aconst_null
    //   47: putfield 234	sun/rmi/transport/StreamRemoteCall:in	Lsun/rmi/transport/ConnectionInputStream;
    //   50: goto +11 -> 61
    //   53: astore_2
    //   54: aload_0
    //   55: aconst_null
    //   56: putfield 234	sun/rmi/transport/StreamRemoteCall:in	Lsun/rmi/transport/ConnectionInputStream;
    //   59: aload_2
    //   60: athrow
    //   61: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	62	0	this	StreamRemoteCall
    //   17	1	1	localRuntimeException	RuntimeException
    //   53	7	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	14	17	java/lang/RuntimeException
    //   0	45	53	finally
  }
  
  public void discardPendingRefs()
  {
    in.discardRefs();
  }
  
  public ObjectOutput getResultStream(boolean paramBoolean)
    throws IOException
  {
    if (resultStarted) {
      throw new StreamCorruptedException("result already in progress");
    }
    resultStarted = true;
    DataOutputStream localDataOutputStream = new DataOutputStream(conn.getOutputStream());
    localDataOutputStream.writeByte(81);
    getOutputStream(true);
    if (paramBoolean) {
      out.writeByte(1);
    } else {
      out.writeByte(2);
    }
    out.writeID();
    return out;
  }
  
  public void executeCall()
    throws Exception
  {
    DGCAckHandler localDGCAckHandler = null;
    int i;
    try
    {
      if (out != null) {
        localDGCAckHandler = out.getDGCAckHandler();
      }
      releaseOutputStream();
      DataInputStream localDataInputStream = new DataInputStream(conn.getInputStream());
      int j = localDataInputStream.readByte();
      if (j != 81)
      {
        if (Transport.transportLog.isLoggable(Log.BRIEF)) {
          Transport.transportLog.log(Log.BRIEF, "transport return code invalid: " + j);
        }
        throw new UnmarshalException("Transport return code invalid");
      }
      getInputStream();
      i = in.readByte();
      in.readID();
    }
    catch (UnmarshalException localUnmarshalException)
    {
      throw localUnmarshalException;
    }
    catch (IOException localIOException)
    {
      throw new UnmarshalException("Error unmarshaling return header", localIOException);
    }
    finally
    {
      if (localDGCAckHandler != null) {
        localDGCAckHandler.release();
      }
    }
    switch (i)
    {
    case 1: 
      break;
    case 2: 
      Object localObject1;
      try
      {
        localObject1 = in.readObject();
      }
      catch (Exception localException)
      {
        throw new UnmarshalException("Error unmarshaling return", localException);
      }
      if ((localObject1 instanceof Exception)) {
        exceptionReceivedFromServer((Exception)localObject1);
      } else {
        throw new UnmarshalException("Return type not Exception");
      }
      break;
    }
    if (Transport.transportLog.isLoggable(Log.BRIEF)) {
      Transport.transportLog.log(Log.BRIEF, "return code invalid: " + i);
    }
    throw new UnmarshalException("Return code invalid");
  }
  
  protected void exceptionReceivedFromServer(Exception paramException)
    throws Exception
  {
    serverException = paramException;
    StackTraceElement[] arrayOfStackTraceElement1 = paramException.getStackTrace();
    StackTraceElement[] arrayOfStackTraceElement2 = new Throwable().getStackTrace();
    StackTraceElement[] arrayOfStackTraceElement3 = new StackTraceElement[arrayOfStackTraceElement1.length + arrayOfStackTraceElement2.length];
    System.arraycopy(arrayOfStackTraceElement1, 0, arrayOfStackTraceElement3, 0, arrayOfStackTraceElement1.length);
    System.arraycopy(arrayOfStackTraceElement2, 0, arrayOfStackTraceElement3, arrayOfStackTraceElement1.length, arrayOfStackTraceElement2.length);
    paramException.setStackTrace(arrayOfStackTraceElement3);
    if (UnicastRef.clientCallLog.isLoggable(Log.BRIEF))
    {
      TCPEndpoint localTCPEndpoint = (TCPEndpoint)conn.getChannel().getEndpoint();
      UnicastRef.clientCallLog.log(Log.BRIEF, "outbound call received exception: [" + localTCPEndpoint.getHost() + ":" + localTCPEndpoint.getPort() + "] exception: ", paramException);
    }
    throw paramException;
  }
  
  public Exception getServerException()
  {
    return serverException;
  }
  
  public void done()
    throws IOException
  {
    releaseInputStream();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\StreamRemoteCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */