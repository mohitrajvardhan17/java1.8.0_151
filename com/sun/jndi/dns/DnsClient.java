package com.sun.jndi.dns;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.naming.CommunicationException;
import javax.naming.ConfigurationException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.ServiceUnavailableException;
import sun.security.jca.JCAUtil;

public class DnsClient
{
  private static final int IDENT_OFFSET = 0;
  private static final int FLAGS_OFFSET = 2;
  private static final int NUMQ_OFFSET = 4;
  private static final int NUMANS_OFFSET = 6;
  private static final int NUMAUTH_OFFSET = 8;
  private static final int NUMADD_OFFSET = 10;
  private static final int DNS_HDR_SIZE = 12;
  private static final int NO_ERROR = 0;
  private static final int FORMAT_ERROR = 1;
  private static final int SERVER_FAILURE = 2;
  private static final int NAME_ERROR = 3;
  private static final int NOT_IMPL = 4;
  private static final int REFUSED = 5;
  private static final String[] rcodeDescription = { "No error", "DNS format error", "DNS server failure", "DNS name not found", "DNS operation not supported", "DNS service refused" };
  private static final int DEFAULT_PORT = 53;
  private static final int TRANSACTION_ID_BOUND = 65536;
  private static final SecureRandom random = JCAUtil.getSecureRandom();
  private InetAddress[] servers;
  private int[] serverPorts;
  private int timeout;
  private int retries;
  private DatagramSocket udpSocket;
  private Map<Integer, ResourceRecord> reqs;
  private Map<Integer, byte[]> resps;
  private Object queuesLock = new Object();
  private static final boolean debug = false;
  
  public DnsClient(String[] paramArrayOfString, int paramInt1, int paramInt2)
    throws NamingException
  {
    timeout = paramInt1;
    retries = paramInt2;
    try
    {
      udpSocket = new DatagramSocket();
    }
    catch (SocketException localSocketException)
    {
      ConfigurationException localConfigurationException1 = new ConfigurationException();
      localConfigurationException1.setRootCause(localSocketException);
      throw localConfigurationException1;
    }
    servers = new InetAddress[paramArrayOfString.length];
    serverPorts = new int[paramArrayOfString.length];
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      int j = paramArrayOfString[i].indexOf(':', paramArrayOfString[i].indexOf(93) + 1);
      serverPorts[i] = (j < 0 ? 53 : Integer.parseInt(paramArrayOfString[i].substring(j + 1)));
      String str = j < 0 ? paramArrayOfString[i] : paramArrayOfString[i].substring(0, j);
      try
      {
        servers[i] = InetAddress.getByName(str);
      }
      catch (UnknownHostException localUnknownHostException)
      {
        ConfigurationException localConfigurationException2 = new ConfigurationException("Unknown DNS server: " + str);
        localConfigurationException2.setRootCause(localUnknownHostException);
        throw localConfigurationException2;
      }
    }
    reqs = Collections.synchronizedMap(new HashMap());
    resps = Collections.synchronizedMap(new HashMap());
  }
  
  protected void finalize()
  {
    close();
  }
  
  public void close()
  {
    udpSocket.close();
    synchronized (queuesLock)
    {
      reqs.clear();
      resps.clear();
    }
  }
  
  ResourceRecords query(DnsName paramDnsName, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    throws NamingException
  {
    int i;
    Packet localPacket;
    ResourceRecord localResourceRecord;
    do
    {
      i = random.nextInt(65536);
      localPacket = makeQueryPacket(paramDnsName, i, paramInt1, paramInt2, paramBoolean1);
      localResourceRecord = (ResourceRecord)reqs.putIfAbsent(Integer.valueOf(i), new ResourceRecord(localPacket.getData(), localPacket.length(), 12, true, false));
    } while (localResourceRecord != null);
    Object localObject1 = null;
    boolean[] arrayOfBoolean = new boolean[servers.length];
    try
    {
      for (int j = 0; j < retries; j++) {
        for (int k = 0; k < servers.length; k++) {
          if (arrayOfBoolean[k] == 0) {
            try
            {
              Object localObject2 = null;
              localObject2 = doUdpQuery(localPacket, servers[k], serverPorts[k], j, i);
              if (localObject2 == null)
              {
                if (resps.size() > 0) {
                  localObject2 = lookupResponse(Integer.valueOf(i));
                }
                if (localObject2 == null) {}
              }
              else
              {
                Object localObject3 = new Header((byte[])localObject2, localObject2.length);
                if ((paramBoolean2) && (!authoritative))
                {
                  localObject1 = new NameNotFoundException("DNS response not authoritative");
                  arrayOfBoolean[k] = true;
                }
                else
                {
                  if (truncated) {
                    for (int m = 0; m < servers.length; m++)
                    {
                      int n = (k + m) % servers.length;
                      if (arrayOfBoolean[n] == 0) {
                        try
                        {
                          Tcp localTcp = new Tcp(servers[n], serverPorts[n]);
                          byte[] arrayOfByte;
                          try
                          {
                            arrayOfByte = doTcpQuery(localTcp, localPacket);
                          }
                          finally
                          {
                            localTcp.close();
                          }
                          Header localHeader = new Header(arrayOfByte, arrayOfByte.length);
                          if (query) {
                            throw new CommunicationException("DNS error: expecting response");
                          }
                          checkResponseCode(localHeader);
                          if ((!paramBoolean2) || (authoritative))
                          {
                            localObject3 = localHeader;
                            localObject2 = arrayOfByte;
                            break;
                          }
                          arrayOfBoolean[n] = true;
                        }
                        catch (Exception localException) {}
                      }
                    }
                  }
                  ResourceRecords localResourceRecords = new ResourceRecords((byte[])localObject2, localObject2.length, (Header)localObject3, false);
                  return localResourceRecords;
                }
              }
            }
            catch (IOException localIOException)
            {
              if (localObject1 == null) {
                localObject1 = localIOException;
              }
              if (localIOException.getClass().getName().equals("java.net.PortUnreachableException")) {
                arrayOfBoolean[k] = true;
              }
            }
            catch (NameNotFoundException localNameNotFoundException)
            {
              throw localNameNotFoundException;
            }
            catch (CommunicationException localCommunicationException2)
            {
              if (localObject1 == null) {
                localObject1 = localCommunicationException2;
              }
            }
            catch (NamingException localNamingException)
            {
              if (localObject1 == null) {
                localObject1 = localNamingException;
              }
              arrayOfBoolean[k] = true;
            }
          }
        }
      }
    }
    finally
    {
      reqs.remove(Integer.valueOf(i));
    }
    if ((localObject1 instanceof NamingException)) {
      throw ((NamingException)localObject1);
    }
    CommunicationException localCommunicationException1 = new CommunicationException("DNS error");
    localCommunicationException1.setRootCause((Throwable)localObject1);
    throw localCommunicationException1;
  }
  
  ResourceRecords queryZone(DnsName paramDnsName, int paramInt, boolean paramBoolean)
    throws NamingException
  {
    int i = random.nextInt(65536);
    Packet localPacket = makeQueryPacket(paramDnsName, i, paramInt, 252, paramBoolean);
    Object localObject1 = null;
    int j = 0;
    while (j < servers.length) {
      try
      {
        Tcp localTcp = new Tcp(servers[j], serverPorts[j]);
        try
        {
          byte[] arrayOfByte = doTcpQuery(localTcp, localPacket);
          Header localHeader = new Header(arrayOfByte, arrayOfByte.length);
          checkResponseCode(localHeader);
          ResourceRecords localResourceRecords1 = new ResourceRecords(arrayOfByte, arrayOfByte.length, localHeader, true);
          if (localResourceRecords1.getFirstAnsType() != 6) {
            throw new CommunicationException("DNS error: zone xfer doesn't begin with SOA");
          }
          if ((answer.size() == 1) || (localResourceRecords1.getLastAnsType() != 6)) {
            do
            {
              arrayOfByte = continueTcpQuery(localTcp);
              if (arrayOfByte == null) {
                throw new CommunicationException("DNS error: incomplete zone transfer");
              }
              localHeader = new Header(arrayOfByte, arrayOfByte.length);
              checkResponseCode(localHeader);
              localResourceRecords1.add(arrayOfByte, arrayOfByte.length, localHeader);
            } while (localResourceRecords1.getLastAnsType() != 6);
          }
          answer.removeElementAt(answer.size() - 1);
          ResourceRecords localResourceRecords2 = localResourceRecords1;
          return localResourceRecords2;
        }
        finally
        {
          localTcp.close();
        }
        j++;
      }
      catch (IOException localIOException)
      {
        localObject1 = localIOException;
      }
      catch (NameNotFoundException localNameNotFoundException)
      {
        throw localNameNotFoundException;
      }
      catch (NamingException localNamingException)
      {
        localObject1 = localNamingException;
      }
    }
    if ((localObject1 instanceof NamingException)) {
      throw ((NamingException)localObject1);
    }
    CommunicationException localCommunicationException = new CommunicationException("DNS error during zone transfer");
    localCommunicationException.setRootCause((Throwable)localObject1);
    throw localCommunicationException;
  }
  
  private byte[] doUdpQuery(Packet paramPacket, InetAddress paramInetAddress, int paramInt1, int paramInt2, int paramInt3)
    throws IOException, NamingException
  {
    int i = 50;
    synchronized (udpSocket)
    {
      DatagramPacket localDatagramPacket1 = new DatagramPacket(paramPacket.getData(), paramPacket.length(), paramInetAddress, paramInt1);
      DatagramPacket localDatagramPacket2 = new DatagramPacket(new byte['ὀ'], 8000);
      udpSocket.connect(paramInetAddress, paramInt1);
      int j = timeout * (1 << paramInt2);
      try
      {
        udpSocket.send(localDatagramPacket1);
        int k = j;
        int m = 0;
        do
        {
          udpSocket.setSoTimeout(k);
          long l1 = System.currentTimeMillis();
          udpSocket.receive(localDatagramPacket2);
          long l2 = System.currentTimeMillis();
          byte[] arrayOfByte1 = new byte[localDatagramPacket2.getLength()];
          arrayOfByte1 = localDatagramPacket2.getData();
          if (isMatchResponse(arrayOfByte1, paramInt3))
          {
            byte[] arrayOfByte2 = arrayOfByte1;
            udpSocket.disconnect();
            return arrayOfByte2;
          }
          k = j - (int)(l2 - l1);
        } while (k > i);
      }
      finally
      {
        udpSocket.disconnect();
      }
      return null;
    }
  }
  
  private byte[] doTcpQuery(Tcp paramTcp, Packet paramPacket)
    throws IOException
  {
    int i = paramPacket.length();
    out.write(i >> 8);
    out.write(i);
    out.write(paramPacket.getData(), 0, i);
    out.flush();
    byte[] arrayOfByte = continueTcpQuery(paramTcp);
    if (arrayOfByte == null) {
      throw new IOException("DNS error: no response");
    }
    return arrayOfByte;
  }
  
  private byte[] continueTcpQuery(Tcp paramTcp)
    throws IOException
  {
    int i = in.read();
    if (i == -1) {
      return null;
    }
    int j = in.read();
    if (j == -1) {
      throw new IOException("Corrupted DNS response: bad length");
    }
    int k = i << 8 | j;
    byte[] arrayOfByte = new byte[k];
    int m = 0;
    while (k > 0)
    {
      int n = in.read(arrayOfByte, m, k);
      if (n == -1) {
        throw new IOException("Corrupted DNS response: too little data");
      }
      k -= n;
      m += n;
    }
    return arrayOfByte;
  }
  
  private Packet makeQueryPacket(DnsName paramDnsName, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    int i = paramDnsName.getOctets();
    int j = 12 + i + 4;
    Packet localPacket = new Packet(j);
    int k = paramBoolean ? 256 : 0;
    localPacket.putShort(paramInt1, 0);
    localPacket.putShort(k, 2);
    localPacket.putShort(1, 4);
    localPacket.putShort(0, 6);
    localPacket.putInt(0, 8);
    makeQueryName(paramDnsName, localPacket, 12);
    localPacket.putShort(paramInt3, 12 + i);
    localPacket.putShort(paramInt2, 12 + i + 2);
    return localPacket;
  }
  
  private void makeQueryName(DnsName paramDnsName, Packet paramPacket, int paramInt)
  {
    for (int i = paramDnsName.size() - 1; i >= 0; i--)
    {
      String str = paramDnsName.get(i);
      int j = str.length();
      paramPacket.putByte(j, paramInt++);
      for (int k = 0; k < j; k++) {
        paramPacket.putByte(str.charAt(k), paramInt++);
      }
    }
    if (!paramDnsName.hasRootLabel()) {
      paramPacket.putByte(0, paramInt);
    }
  }
  
  private byte[] lookupResponse(Integer paramInteger)
    throws NamingException
  {
    byte[] arrayOfByte;
    if ((arrayOfByte = (byte[])resps.get(paramInteger)) != null)
    {
      checkResponseCode(new Header(arrayOfByte, arrayOfByte.length));
      synchronized (queuesLock)
      {
        resps.remove(paramInteger);
        reqs.remove(paramInteger);
      }
    }
    return arrayOfByte;
  }
  
  private boolean isMatchResponse(byte[] paramArrayOfByte, int paramInt)
    throws NamingException
  {
    Header localHeader = new Header(paramArrayOfByte, paramArrayOfByte.length);
    if (query) {
      throw new CommunicationException("DNS error: expecting response");
    }
    if (!reqs.containsKey(Integer.valueOf(paramInt))) {
      return false;
    }
    if (xid == paramInt)
    {
      checkResponseCode(localHeader);
      if ((!query) && (numQuestions == 1))
      {
        ResourceRecord localResourceRecord1 = new ResourceRecord(paramArrayOfByte, paramArrayOfByte.length, 12, true, false);
        ResourceRecord localResourceRecord2 = (ResourceRecord)reqs.get(Integer.valueOf(paramInt));
        int i = localResourceRecord2.getType();
        int j = localResourceRecord2.getRrclass();
        DnsName localDnsName = localResourceRecord2.getName();
        if (((i == 255) || (i == localResourceRecord1.getType())) && ((j == 255) || (j == localResourceRecord1.getRrclass())) && (localDnsName.equals(localResourceRecord1.getName())))
        {
          synchronized (queuesLock)
          {
            resps.remove(Integer.valueOf(paramInt));
            reqs.remove(Integer.valueOf(paramInt));
          }
          return true;
        }
      }
      return false;
    }
    synchronized (queuesLock)
    {
      if (reqs.containsKey(Integer.valueOf(xid))) {
        resps.put(Integer.valueOf(xid), paramArrayOfByte);
      }
    }
    return false;
  }
  
  private void checkResponseCode(Header paramHeader)
    throws NamingException
  {
    int i = rcode;
    if (i == 0) {
      return;
    }
    String str = i < rcodeDescription.length ? rcodeDescription[i] : "DNS error";
    str = str + " [response code " + i + "]";
    switch (i)
    {
    case 2: 
      throw new ServiceUnavailableException(str);
    case 3: 
      throw new NameNotFoundException(str);
    case 4: 
    case 5: 
      throw new OperationNotSupportedException(str);
    }
    throw new NamingException(str);
  }
  
  private static void dprint(String paramString) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\dns\DnsClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */