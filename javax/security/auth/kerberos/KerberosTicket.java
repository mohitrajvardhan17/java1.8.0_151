package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.security.auth.RefreshFailedException;
import javax.security.auth.Refreshable;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;

public class KerberosTicket
  implements Destroyable, Refreshable, Serializable
{
  private static final long serialVersionUID = 7395334370157380539L;
  private static final int FORWARDABLE_TICKET_FLAG = 1;
  private static final int FORWARDED_TICKET_FLAG = 2;
  private static final int PROXIABLE_TICKET_FLAG = 3;
  private static final int PROXY_TICKET_FLAG = 4;
  private static final int POSTDATED_TICKET_FLAG = 6;
  private static final int RENEWABLE_TICKET_FLAG = 8;
  private static final int INITIAL_TICKET_FLAG = 9;
  private static final int NUM_FLAGS = 32;
  private byte[] asn1Encoding;
  private KeyImpl sessionKey;
  private boolean[] flags;
  private Date authTime;
  private Date startTime;
  private Date endTime;
  private Date renewTill;
  private KerberosPrincipal client;
  private KerberosPrincipal server;
  private InetAddress[] clientAddresses;
  private transient boolean destroyed = false;
  
  public KerberosTicket(byte[] paramArrayOfByte1, KerberosPrincipal paramKerberosPrincipal1, KerberosPrincipal paramKerberosPrincipal2, byte[] paramArrayOfByte2, int paramInt, boolean[] paramArrayOfBoolean, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, InetAddress[] paramArrayOfInetAddress)
  {
    init(paramArrayOfByte1, paramKerberosPrincipal1, paramKerberosPrincipal2, paramArrayOfByte2, paramInt, paramArrayOfBoolean, paramDate1, paramDate2, paramDate3, paramDate4, paramArrayOfInetAddress);
  }
  
  private void init(byte[] paramArrayOfByte1, KerberosPrincipal paramKerberosPrincipal1, KerberosPrincipal paramKerberosPrincipal2, byte[] paramArrayOfByte2, int paramInt, boolean[] paramArrayOfBoolean, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, InetAddress[] paramArrayOfInetAddress)
  {
    if (paramArrayOfByte2 == null) {
      throw new IllegalArgumentException("Session key for ticket cannot be null");
    }
    init(paramArrayOfByte1, paramKerberosPrincipal1, paramKerberosPrincipal2, new KeyImpl(paramArrayOfByte2, paramInt), paramArrayOfBoolean, paramDate1, paramDate2, paramDate3, paramDate4, paramArrayOfInetAddress);
  }
  
  private void init(byte[] paramArrayOfByte, KerberosPrincipal paramKerberosPrincipal1, KerberosPrincipal paramKerberosPrincipal2, KeyImpl paramKeyImpl, boolean[] paramArrayOfBoolean, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, InetAddress[] paramArrayOfInetAddress)
  {
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("ASN.1 encoding of ticket cannot be null");
    }
    asn1Encoding = ((byte[])paramArrayOfByte.clone());
    if (paramKerberosPrincipal1 == null) {
      throw new IllegalArgumentException("Client name in ticket cannot be null");
    }
    client = paramKerberosPrincipal1;
    if (paramKerberosPrincipal2 == null) {
      throw new IllegalArgumentException("Server name in ticket cannot be null");
    }
    server = paramKerberosPrincipal2;
    sessionKey = paramKeyImpl;
    if (paramArrayOfBoolean != null)
    {
      if (paramArrayOfBoolean.length >= 32)
      {
        flags = ((boolean[])paramArrayOfBoolean.clone());
      }
      else
      {
        flags = new boolean[32];
        for (int i = 0; i < paramArrayOfBoolean.length; i++) {
          flags[i] = paramArrayOfBoolean[i];
        }
      }
    }
    else {
      flags = new boolean[32];
    }
    if (flags[8] != 0)
    {
      if (paramDate4 == null) {
        throw new IllegalArgumentException("The renewable period end time cannot be null for renewable tickets.");
      }
      renewTill = new Date(paramDate4.getTime());
    }
    if (paramDate1 != null) {
      authTime = new Date(paramDate1.getTime());
    }
    if (paramDate2 != null) {
      startTime = new Date(paramDate2.getTime());
    } else {
      startTime = authTime;
    }
    if (paramDate3 == null) {
      throw new IllegalArgumentException("End time for ticket validity cannot be null");
    }
    endTime = new Date(paramDate3.getTime());
    if (paramArrayOfInetAddress != null) {
      clientAddresses = ((InetAddress[])paramArrayOfInetAddress.clone());
    }
  }
  
  public final KerberosPrincipal getClient()
  {
    return client;
  }
  
  public final KerberosPrincipal getServer()
  {
    return server;
  }
  
  public final SecretKey getSessionKey()
  {
    if (destroyed) {
      throw new IllegalStateException("This ticket is no longer valid");
    }
    return sessionKey;
  }
  
  public final int getSessionKeyType()
  {
    if (destroyed) {
      throw new IllegalStateException("This ticket is no longer valid");
    }
    return sessionKey.getKeyType();
  }
  
  public final boolean isForwardable()
  {
    return flags[1];
  }
  
  public final boolean isForwarded()
  {
    return flags[2];
  }
  
  public final boolean isProxiable()
  {
    return flags[3];
  }
  
  public final boolean isProxy()
  {
    return flags[4];
  }
  
  public final boolean isPostdated()
  {
    return flags[6];
  }
  
  public final boolean isRenewable()
  {
    return flags[8];
  }
  
  public final boolean isInitial()
  {
    return flags[9];
  }
  
  public final boolean[] getFlags()
  {
    return flags == null ? null : (boolean[])flags.clone();
  }
  
  public final Date getAuthTime()
  {
    return authTime == null ? null : (Date)authTime.clone();
  }
  
  public final Date getStartTime()
  {
    return startTime == null ? null : (Date)startTime.clone();
  }
  
  public final Date getEndTime()
  {
    return (Date)endTime.clone();
  }
  
  public final Date getRenewTill()
  {
    return renewTill == null ? null : (Date)renewTill.clone();
  }
  
  public final InetAddress[] getClientAddresses()
  {
    return clientAddresses == null ? null : (InetAddress[])clientAddresses.clone();
  }
  
  public final byte[] getEncoded()
  {
    if (destroyed) {
      throw new IllegalStateException("This ticket is no longer valid");
    }
    return (byte[])asn1Encoding.clone();
  }
  
  public boolean isCurrent()
  {
    return System.currentTimeMillis() <= getEndTime().getTime();
  }
  
  public void refresh()
    throws RefreshFailedException
  {
    if (destroyed) {
      throw new RefreshFailedException("A destroyed ticket cannot be renewd.");
    }
    if (!isRenewable()) {
      throw new RefreshFailedException("This ticket is not renewable");
    }
    if (System.currentTimeMillis() > getRenewTill().getTime()) {
      throw new RefreshFailedException("This ticket is past its last renewal time.");
    }
    Object localObject1 = null;
    Credentials localCredentials = null;
    try
    {
      localCredentials = new Credentials(asn1Encoding, client.toString(), server.toString(), sessionKey.getEncoded(), sessionKey.getKeyType(), flags, authTime, startTime, endTime, renewTill, clientAddresses);
      localCredentials = localCredentials.renew();
    }
    catch (KrbException localKrbException)
    {
      localObject1 = localKrbException;
    }
    catch (IOException localIOException)
    {
      localObject1 = localIOException;
    }
    if (localObject1 != null)
    {
      RefreshFailedException localRefreshFailedException = new RefreshFailedException("Failed to renew Kerberos Ticket for client " + client + " and server " + server + " - " + ((Throwable)localObject1).getMessage());
      localRefreshFailedException.initCause((Throwable)localObject1);
      throw localRefreshFailedException;
    }
    synchronized (this)
    {
      try
      {
        destroy();
      }
      catch (DestroyFailedException localDestroyFailedException) {}
      init(localCredentials.getEncoded(), new KerberosPrincipal(localCredentials.getClient().getName()), new KerberosPrincipal(localCredentials.getServer().getName(), 2), localCredentials.getSessionKey().getBytes(), localCredentials.getSessionKey().getEType(), localCredentials.getFlags(), localCredentials.getAuthTime(), localCredentials.getStartTime(), localCredentials.getEndTime(), localCredentials.getRenewTill(), localCredentials.getClientAddresses());
      destroyed = false;
    }
  }
  
  public void destroy()
    throws DestroyFailedException
  {
    if (!destroyed)
    {
      Arrays.fill(asn1Encoding, (byte)0);
      client = null;
      server = null;
      sessionKey.destroy();
      flags = null;
      authTime = null;
      startTime = null;
      endTime = null;
      renewTill = null;
      clientAddresses = null;
      destroyed = true;
    }
  }
  
  public boolean isDestroyed()
  {
    return destroyed;
  }
  
  public String toString()
  {
    if (destroyed) {
      throw new IllegalStateException("This ticket is no longer valid");
    }
    StringBuffer localStringBuffer = new StringBuffer();
    if (clientAddresses != null) {
      for (int i = 0; i < clientAddresses.length; i++) {
        localStringBuffer.append("clientAddresses[" + i + "] = " + clientAddresses[i].toString());
      }
    }
    return "Ticket (hex) = \n" + new HexDumpEncoder().encodeBuffer(asn1Encoding) + "\nClient Principal = " + client.toString() + "\nServer Principal = " + server.toString() + "\nSession Key = " + sessionKey.toString() + "\nForwardable Ticket " + flags[1] + "\nForwarded Ticket " + flags[2] + "\nProxiable Ticket " + flags[3] + "\nProxy Ticket " + flags[4] + "\nPostdated Ticket " + flags[6] + "\nRenewable Ticket " + flags[8] + "\nInitial Ticket " + flags[8] + "\nAuth Time = " + String.valueOf(authTime) + "\nStart Time = " + String.valueOf(startTime) + "\nEnd Time = " + endTime.toString() + "\nRenew Till = " + String.valueOf(renewTill) + "\nClient Addresses " + (clientAddresses == null ? " Null " : new StringBuilder().append(localStringBuffer.toString()).append("\n").toString());
  }
  
  public int hashCode()
  {
    int i = 17;
    if (isDestroyed()) {
      return i;
    }
    i = i * 37 + Arrays.hashCode(getEncoded());
    i = i * 37 + endTime.hashCode();
    i = i * 37 + client.hashCode();
    i = i * 37 + server.hashCode();
    i = i * 37 + sessionKey.hashCode();
    if (authTime != null) {
      i = i * 37 + authTime.hashCode();
    }
    if (startTime != null) {
      i = i * 37 + startTime.hashCode();
    }
    if (renewTill != null) {
      i = i * 37 + renewTill.hashCode();
    }
    i = i * 37 + Arrays.hashCode(clientAddresses);
    return i * 37 + Arrays.hashCode(flags);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof KerberosTicket)) {
      return false;
    }
    KerberosTicket localKerberosTicket = (KerberosTicket)paramObject;
    if ((isDestroyed()) || (localKerberosTicket.isDestroyed())) {
      return false;
    }
    if ((!Arrays.equals(getEncoded(), localKerberosTicket.getEncoded())) || (!endTime.equals(localKerberosTicket.getEndTime())) || (!server.equals(localKerberosTicket.getServer())) || (!client.equals(localKerberosTicket.getClient())) || (!sessionKey.equals(localKerberosTicket.getSessionKey())) || (!Arrays.equals(clientAddresses, localKerberosTicket.getClientAddresses())) || (!Arrays.equals(flags, localKerberosTicket.getFlags()))) {
      return false;
    }
    if (authTime == null)
    {
      if (localKerberosTicket.getAuthTime() != null) {
        return false;
      }
    }
    else if (!authTime.equals(localKerberosTicket.getAuthTime())) {
      return false;
    }
    if (startTime == null)
    {
      if (localKerberosTicket.getStartTime() != null) {
        return false;
      }
    }
    else if (!startTime.equals(localKerberosTicket.getStartTime())) {
      return false;
    }
    if (renewTill == null)
    {
      if (localKerberosTicket.getRenewTill() != null) {
        return false;
      }
    }
    else if (!renewTill.equals(localKerberosTicket.getRenewTill())) {
      return false;
    }
    return true;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if (sessionKey == null) {
      throw new InvalidObjectException("Session key cannot be null");
    }
    try
    {
      init(asn1Encoding, client, server, sessionKey, flags, authTime, startTime, endTime, renewTill, clientAddresses);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw ((InvalidObjectException)new InvalidObjectException(localIllegalArgumentException.getMessage()).initCause(localIllegalArgumentException));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\kerberos\KerberosTicket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */