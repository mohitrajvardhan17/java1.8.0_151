package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

final class SnmpSocket
  implements Runnable
{
  private DatagramSocket _socket = null;
  private SnmpResponseHandler _dgramHdlr = null;
  private Thread _sockThread = null;
  private byte[] _buffer = null;
  private transient boolean isClosing = false;
  int _socketPort = 0;
  int responseBufSize = 1024;
  
  public SnmpSocket(SnmpResponseHandler paramSnmpResponseHandler, InetAddress paramInetAddress, int paramInt)
    throws SocketException
  {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "constructor", "Creating new SNMP datagram socket");
    }
    _socket = new DatagramSocket(0, paramInetAddress);
    _socketPort = _socket.getLocalPort();
    responseBufSize = paramInt;
    _buffer = new byte[responseBufSize];
    _dgramHdlr = paramSnmpResponseHandler;
    _sockThread = new Thread(this, "SnmpSocket");
    _sockThread.start();
  }
  
  public synchronized void sendPacket(byte[] paramArrayOfByte, int paramInt1, InetAddress paramInetAddress, int paramInt2)
    throws IOException
  {
    DatagramPacket localDatagramPacket = new DatagramPacket(paramArrayOfByte, paramInt1, paramInetAddress, paramInt2);
    sendPacket(localDatagramPacket);
  }
  
  public synchronized void sendPacket(DatagramPacket paramDatagramPacket)
    throws IOException
  {
    try
    {
      if (isValid())
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "sendPacket", "Sending DatagramPacket. Length = " + paramDatagramPacket.getLength() + " through socket = " + _socket.toString());
        }
        _socket.send(paramDatagramPacket);
      }
      else
      {
        throw new IOException("Invalid state of SNMP datagram socket.");
      }
    }
    catch (IOException localIOException)
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "sendPacket", "I/O error while sending", localIOException);
      }
      throw localIOException;
    }
  }
  
  public synchronized boolean isValid()
  {
    return (_socket != null) && (_sockThread != null) && (_sockThread.isAlive());
  }
  
  public synchronized void close()
  {
    isClosing = true;
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "close", "Closing and destroying the SNMP datagram socket -> " + toString());
    }
    try
    {
      DatagramSocket localDatagramSocket = new DatagramSocket(0);
      byte[] arrayOfByte = new byte[1];
      DatagramPacket localDatagramPacket = new DatagramPacket(arrayOfByte, 1, InetAddress.getLocalHost(), _socketPort);
      localDatagramSocket.send(localDatagramPacket);
      localDatagramSocket.close();
    }
    catch (Exception localException) {}
    if (_socket != null)
    {
      _socket.close();
      _socket = null;
    }
    if ((_sockThread != null) && (_sockThread.isAlive()))
    {
      _sockThread.interrupt();
      try
      {
        _sockThread.join();
      }
      catch (InterruptedException localInterruptedException) {}
      _sockThread = null;
    }
  }
  
  public void run()
  {
    Thread.currentThread().setPriority(8);
    for (;;)
    {
      try
      {
        DatagramPacket localDatagramPacket = new DatagramPacket(_buffer, _buffer.length);
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "run", "[" + Thread.currentThread().toString() + "]:Blocking for receiving packet");
        }
        _socket.receive(localDatagramPacket);
        if (isClosing) {
          break;
        }
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "run", "[" + Thread.currentThread().toString() + "]:Received a packet");
        }
        if (localDatagramPacket.getLength() > 0)
        {
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "run", "[" + Thread.currentThread().toString() + "]:Received a packet from : " + localDatagramPacket.getAddress().toString() + ", Length = " + localDatagramPacket.getLength());
          }
          handleDatagram(localDatagramPacket);
          if (isClosing) {
            break;
          }
        }
      }
      catch (IOException localIOException)
      {
        if (isClosing) {
          break;
        }
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "run", "IOEXception while receiving datagram", localIOException);
        }
      }
      catch (Exception localException)
      {
        if (isClosing) {
          break;
        }
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "run", "Exception in socket thread...", localException);
        }
      }
      catch (ThreadDeath localThreadDeath)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "run", "Socket Thread DEAD..." + toString(), localThreadDeath);
        }
        close();
        throw localThreadDeath;
      }
      catch (Error localError)
      {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "run", "Got unexpected error", localError);
        }
        handleJavaError(localError);
      }
    }
  }
  
  protected synchronized void finalize()
  {
    close();
  }
  
  private synchronized void handleJavaError(Throwable paramThrowable)
  {
    if ((paramThrowable instanceof OutOfMemoryError))
    {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "handleJavaError", "OutOfMemory error", paramThrowable);
      }
      Thread.yield();
      return;
    }
    if (_socket != null)
    {
      _socket.close();
      _socket = null;
    }
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "handleJavaError", "Global Internal error");
    }
    Thread.yield();
  }
  
  private synchronized void handleDatagram(DatagramPacket paramDatagramPacket)
  {
    _dgramHdlr.processDatagram(paramDatagramPacket);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\daemon\SnmpSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */