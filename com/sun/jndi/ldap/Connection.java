package com.sun.jndi.ldap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Arrays;
import javax.naming.CommunicationException;
import javax.naming.InterruptedNamingException;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.ldap.Control;
import javax.net.ssl.SSLSocket;
import sun.misc.IOUtils;

public final class Connection
  implements Runnable
{
  private static final boolean debug = false;
  private static final int dump = 0;
  private final Thread worker;
  private boolean v3 = true;
  public final String host;
  public final int port;
  private boolean bound = false;
  private OutputStream traceFile = null;
  private String traceTagIn = null;
  private String traceTagOut = null;
  public InputStream inStream;
  public OutputStream outStream;
  public Socket sock;
  private final LdapClient parent;
  private int outMsgId = 0;
  private LdapRequest pendingRequests = null;
  volatile IOException closureReason = null;
  volatile boolean useable = true;
  int readTimeout;
  int connectTimeout;
  private Object pauseLock = new Object();
  private boolean paused = false;
  
  void setV3(boolean paramBoolean)
  {
    v3 = paramBoolean;
  }
  
  void setBound()
  {
    bound = true;
  }
  
  Connection(LdapClient paramLdapClient, String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, OutputStream paramOutputStream)
    throws NamingException
  {
    host = paramString1;
    port = paramInt1;
    parent = paramLdapClient;
    readTimeout = paramInt3;
    connectTimeout = paramInt2;
    if (paramOutputStream != null)
    {
      traceFile = paramOutputStream;
      traceTagIn = ("<- " + paramString1 + ":" + paramInt1 + "\n\n");
      traceTagOut = ("-> " + paramString1 + ":" + paramInt1 + "\n\n");
    }
    try
    {
      sock = createSocket(paramString1, paramInt1, paramString2, paramInt2);
      inStream = new BufferedInputStream(sock.getInputStream());
      outStream = new BufferedOutputStream(sock.getOutputStream());
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      localObject = localInvocationTargetException.getTargetException();
      CommunicationException localCommunicationException = new CommunicationException(paramString1 + ":" + paramInt1);
      localCommunicationException.setRootCause((Throwable)localObject);
      throw localCommunicationException;
    }
    catch (Exception localException)
    {
      Object localObject = new CommunicationException(paramString1 + ":" + paramInt1);
      ((CommunicationException)localObject).setRootCause(localException);
      throw ((Throwable)localObject);
    }
    worker = Obj.helper.createThread(this);
    worker.setDaemon(true);
    worker.start();
  }
  
  private Object createInetSocketAddress(String paramString, int paramInt)
    throws NoSuchMethodException
  {
    try
    {
      Class localClass = Class.forName("java.net.InetSocketAddress");
      Constructor localConstructor = localClass.getConstructor(new Class[] { String.class, Integer.TYPE });
      return localConstructor.newInstance(new Object[] { paramString, new Integer(paramInt) });
    }
    catch (ClassNotFoundException|InstantiationException|InvocationTargetException|IllegalAccessException localClassNotFoundException)
    {
      throw new NoSuchMethodException();
    }
  }
  
  private Socket createSocket(String paramString1, int paramInt1, String paramString2, int paramInt2)
    throws Exception
  {
    Socket localSocket = null;
    Object localObject1;
    Method localMethod1;
    Object localObject2;
    if (paramString2 != null)
    {
      localObject1 = Obj.helper.loadClass(paramString2);
      localMethod1 = ((Class)localObject1).getMethod("getDefault", new Class[0]);
      localObject2 = localMethod1.invoke(null, new Object[0]);
      Method localMethod2 = null;
      if (paramInt2 > 0) {
        try
        {
          localMethod2 = ((Class)localObject1).getMethod("createSocket", new Class[0]);
          Method localMethod3 = Socket.class.getMethod("connect", new Class[] { Class.forName("java.net.SocketAddress"), Integer.TYPE });
          Object localObject3 = createInetSocketAddress(paramString1, paramInt1);
          localSocket = (Socket)localMethod2.invoke(localObject2, new Object[0]);
          localMethod3.invoke(localSocket, new Object[] { localObject3, new Integer(paramInt2) });
        }
        catch (NoSuchMethodException localNoSuchMethodException2) {}
      }
      if (localSocket == null)
      {
        localMethod2 = ((Class)localObject1).getMethod("createSocket", new Class[] { String.class, Integer.TYPE });
        localSocket = (Socket)localMethod2.invoke(localObject2, new Object[] { paramString1, new Integer(paramInt1) });
      }
    }
    else
    {
      if (paramInt2 > 0) {
        try
        {
          localObject1 = Socket.class.getConstructor(new Class[0]);
          localMethod1 = Socket.class.getMethod("connect", new Class[] { Class.forName("java.net.SocketAddress"), Integer.TYPE });
          localObject2 = createInetSocketAddress(paramString1, paramInt1);
          localSocket = (Socket)((Constructor)localObject1).newInstance(new Object[0]);
          localMethod1.invoke(localSocket, new Object[] { localObject2, new Integer(paramInt2) });
        }
        catch (NoSuchMethodException localNoSuchMethodException1) {}
      }
      if (localSocket == null) {
        localSocket = new Socket(paramString1, paramInt1);
      }
    }
    if ((paramInt2 > 0) && ((localSocket instanceof SSLSocket)))
    {
      SSLSocket localSSLSocket = (SSLSocket)localSocket;
      int i = localSSLSocket.getSoTimeout();
      localSSLSocket.setSoTimeout(paramInt2);
      localSSLSocket.startHandshake();
      localSSLSocket.setSoTimeout(i);
    }
    return localSocket;
  }
  
  synchronized int getMsgId()
  {
    return ++outMsgId;
  }
  
  LdapRequest writeRequest(BerEncoder paramBerEncoder, int paramInt)
    throws IOException
  {
    return writeRequest(paramBerEncoder, paramInt, false, -1);
  }
  
  LdapRequest writeRequest(BerEncoder paramBerEncoder, int paramInt, boolean paramBoolean)
    throws IOException
  {
    return writeRequest(paramBerEncoder, paramInt, paramBoolean, -1);
  }
  
  LdapRequest writeRequest(BerEncoder paramBerEncoder, int paramInt1, boolean paramBoolean, int paramInt2)
    throws IOException
  {
    LdapRequest localLdapRequest = new LdapRequest(paramInt1, paramBoolean, paramInt2);
    addRequest(localLdapRequest);
    if (traceFile != null) {
      Ber.dumpBER(traceFile, traceTagOut, paramBerEncoder.getBuf(), 0, paramBerEncoder.getDataLen());
    }
    unpauseReader();
    try
    {
      synchronized (this)
      {
        outStream.write(paramBerEncoder.getBuf(), 0, paramBerEncoder.getDataLen());
        outStream.flush();
      }
    }
    catch (IOException localIOException)
    {
      cleanup(null, true);
      throw (closureReason = localIOException);
    }
    return localLdapRequest;
  }
  
  BerDecoder readReply(LdapRequest paramLdapRequest)
    throws IOException, NamingException
  {
    long l1 = 0L;
    long l2 = 0L;
    BerDecoder localBerDecoder;
    while (((localBerDecoder = paramLdapRequest.getReplyBer()) == null) && ((readTimeout <= 0) || (l1 < readTimeout))) {
      try
      {
        synchronized (this)
        {
          if (sock == null) {
            throw new ServiceUnavailableException(host + ":" + port + "; socket closed");
          }
        }
        synchronized (paramLdapRequest)
        {
          localBerDecoder = paramLdapRequest.getReplyBer();
          if (localBerDecoder == null)
          {
            if (readTimeout > 0)
            {
              long l3 = System.nanoTime();
              paramLdapRequest.wait(readTimeout - l1);
              l2 += System.nanoTime() - l3;
              l1 += l2 / 1000000L;
              l2 %= 1000000L;
            }
            else
            {
              paramLdapRequest.wait();
            }
          }
          else {
            break;
          }
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new InterruptedNamingException("Interrupted during LDAP operation");
      }
    }
    if ((localBerDecoder == null) && (l1 >= readTimeout))
    {
      abandonRequest(paramLdapRequest, null);
      throw new NamingException("LDAP response read timed out, timeout used:" + readTimeout + "ms.");
    }
    return localBerDecoder;
  }
  
  private synchronized void addRequest(LdapRequest paramLdapRequest)
  {
    LdapRequest localLdapRequest = pendingRequests;
    if (localLdapRequest == null)
    {
      pendingRequests = paramLdapRequest;
      next = null;
    }
    else
    {
      next = pendingRequests;
      pendingRequests = paramLdapRequest;
    }
  }
  
  synchronized LdapRequest findRequest(int paramInt)
  {
    for (LdapRequest localLdapRequest = pendingRequests; localLdapRequest != null; localLdapRequest = next) {
      if (msgId == paramInt) {
        return localLdapRequest;
      }
    }
    return null;
  }
  
  synchronized void removeRequest(LdapRequest paramLdapRequest)
  {
    LdapRequest localLdapRequest1 = pendingRequests;
    LdapRequest localLdapRequest2 = null;
    while (localLdapRequest1 != null)
    {
      if (localLdapRequest1 == paramLdapRequest)
      {
        localLdapRequest1.cancel();
        if (localLdapRequest2 != null) {
          next = next;
        } else {
          pendingRequests = next;
        }
        next = null;
      }
      localLdapRequest2 = localLdapRequest1;
      localLdapRequest1 = next;
    }
  }
  
  void abandonRequest(LdapRequest paramLdapRequest, Control[] paramArrayOfControl)
  {
    removeRequest(paramLdapRequest);
    BerEncoder localBerEncoder = new BerEncoder(256);
    int i = getMsgId();
    try
    {
      localBerEncoder.beginSeq(48);
      localBerEncoder.encodeInt(i);
      localBerEncoder.encodeInt(msgId, 80);
      if (v3) {
        LdapClient.encodeControls(localBerEncoder, paramArrayOfControl);
      }
      localBerEncoder.endSeq();
      if (traceFile != null) {
        Ber.dumpBER(traceFile, traceTagOut, localBerEncoder.getBuf(), 0, localBerEncoder.getDataLen());
      }
      synchronized (this)
      {
        outStream.write(localBerEncoder.getBuf(), 0, localBerEncoder.getDataLen());
        outStream.flush();
      }
    }
    catch (IOException localIOException) {}
  }
  
  synchronized void abandonOutstandingReqs(Control[] paramArrayOfControl)
  {
    LdapRequest localLdapRequest = pendingRequests;
    while (localLdapRequest != null)
    {
      abandonRequest(localLdapRequest, paramArrayOfControl);
      pendingRequests = (localLdapRequest = next);
    }
  }
  
  private void ldapUnbind(Control[] paramArrayOfControl)
  {
    BerEncoder localBerEncoder = new BerEncoder(256);
    int i = getMsgId();
    try
    {
      localBerEncoder.beginSeq(48);
      localBerEncoder.encodeInt(i);
      localBerEncoder.encodeByte(66);
      localBerEncoder.encodeByte(0);
      if (v3) {
        LdapClient.encodeControls(localBerEncoder, paramArrayOfControl);
      }
      localBerEncoder.endSeq();
      if (traceFile != null) {
        Ber.dumpBER(traceFile, traceTagOut, localBerEncoder.getBuf(), 0, localBerEncoder.getDataLen());
      }
      synchronized (this)
      {
        outStream.write(localBerEncoder.getBuf(), 0, localBerEncoder.getDataLen());
        outStream.flush();
      }
    }
    catch (IOException localIOException) {}
  }
  
  void cleanup(Control[] paramArrayOfControl, boolean paramBoolean)
  {
    boolean bool = false;
    synchronized (this)
    {
      useable = false;
      LdapRequest localLdapRequest1;
      if (sock != null)
      {
        try
        {
          if (!paramBoolean) {
            abandonOutstandingReqs(paramArrayOfControl);
          }
          if (bound) {
            ldapUnbind(paramArrayOfControl);
          }
        }
        finally
        {
          try
          {
            outStream.flush();
            sock.close();
            unpauseReader();
          }
          catch (IOException localIOException2) {}
          if (!paramBoolean) {
            for (LdapRequest localLdapRequest2 = pendingRequests; localLdapRequest2 != null; localLdapRequest2 = next) {
              localLdapRequest2.cancel();
            }
          }
          sock = null;
        }
        bool = paramBoolean;
      }
      if (bool)
      {
        localLdapRequest1 = pendingRequests;
        while (localLdapRequest1 != null) {
          synchronized (localLdapRequest1)
          {
            localLdapRequest1.notify();
            localLdapRequest1 = next;
          }
        }
      }
    }
    if (bool) {
      parent.processConnectionClosure();
    }
  }
  
  public synchronized void replaceStreams(InputStream paramInputStream, OutputStream paramOutputStream)
  {
    inStream = paramInputStream;
    try
    {
      outStream.flush();
    }
    catch (IOException localIOException) {}
    outStream = paramOutputStream;
  }
  
  private synchronized InputStream getInputStream()
  {
    return inStream;
  }
  
  private void unpauseReader()
    throws IOException
  {
    synchronized (pauseLock)
    {
      if (paused)
      {
        paused = false;
        pauseLock.notify();
      }
    }
  }
  
  private void pauseReader()
    throws IOException
  {
    paused = true;
    try
    {
      while (paused) {
        pauseLock.wait();
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new InterruptedIOException("Pause/unpause reader has problems.");
    }
  }
  
  public void run()
  {
    InputStream localInputStream = null;
    try
    {
      try
      {
        for (;;)
        {
          byte[] arrayOfByte1 = new byte[''];
          int m = 0;
          int n = 0;
          int i1 = 0;
          localInputStream = getInputStream();
          int j = localInputStream.read(arrayOfByte1, m, 1);
          if (j < 0)
          {
            if (localInputStream == getInputStream()) {
              break;
            }
          }
          else if (arrayOfByte1[(m++)] == 48)
          {
            j = localInputStream.read(arrayOfByte1, m, 1);
            if (j < 0) {
              break;
            }
            n = arrayOfByte1[(m++)];
            if ((n & 0x80) == 128)
            {
              i1 = n & 0x7F;
              j = 0;
              int i2 = 0;
              while (j < i1)
              {
                int k = localInputStream.read(arrayOfByte1, m + j, i1 - j);
                if (k < 0)
                {
                  i2 = 1;
                  break;
                }
                j += k;
              }
              if (i2 != 0) {
                break;
              }
              n = 0;
              for (int i3 = 0; i3 < i1; i3++) {
                n = (n << 8) + (arrayOfByte1[(m + i3)] & 0xFF);
              }
              m += j;
            }
            byte[] arrayOfByte2 = IOUtils.readFully(localInputStream, n, false);
            arrayOfByte1 = Arrays.copyOf(arrayOfByte1, m + arrayOfByte2.length);
            System.arraycopy(arrayOfByte2, 0, arrayOfByte1, m, arrayOfByte2.length);
            m += arrayOfByte2.length;
            try
            {
              BerDecoder localBerDecoder = new BerDecoder(arrayOfByte1, 0, m);
              if (traceFile != null) {
                Ber.dumpBER(traceFile, traceTagIn, arrayOfByte1, 0, m);
              }
              localBerDecoder.parseSeq(null);
              int i = localBerDecoder.parseInt();
              localBerDecoder.reset();
              boolean bool = false;
              if (i == 0)
              {
                parent.processUnsolicited(localBerDecoder);
              }
              else
              {
                LdapRequest localLdapRequest = findRequest(i);
                if (localLdapRequest != null) {
                  synchronized (pauseLock)
                  {
                    bool = localLdapRequest.addReplyBer(localBerDecoder);
                    if (bool) {
                      pauseReader();
                    }
                  }
                }
              }
            }
            catch (Ber.DecodeException localDecodeException) {}
          }
        }
      }
      catch (IOException localIOException1)
      {
        if (localInputStream == getInputStream()) {
          throw localIOException1;
        }
      }
    }
    catch (IOException localIOException2)
    {
      closureReason = localIOException2;
    }
    finally
    {
      cleanup(null, true);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\Connection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */