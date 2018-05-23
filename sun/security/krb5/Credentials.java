package sun.security.krb5;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Date;
import java.util.Locale;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.CredentialsUtil;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.ccache.CredentialsCache;
import sun.security.krb5.internal.crypto.EType;

public class Credentials
{
  Ticket ticket;
  PrincipalName client;
  PrincipalName server;
  EncryptionKey key;
  TicketFlags flags;
  KerberosTime authTime;
  KerberosTime startTime;
  KerberosTime endTime;
  KerberosTime renewTill;
  HostAddresses cAddr;
  EncryptionKey serviceKey;
  AuthorizationData authzData;
  private static boolean DEBUG = Krb5.DEBUG;
  private static CredentialsCache cache;
  static boolean alreadyLoaded = false;
  private static boolean alreadyTried = false;
  
  private static native Credentials acquireDefaultNativeCreds(int[] paramArrayOfInt);
  
  public Credentials(Ticket paramTicket, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, EncryptionKey paramEncryptionKey, TicketFlags paramTicketFlags, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, HostAddresses paramHostAddresses, AuthorizationData paramAuthorizationData)
  {
    this(paramTicket, paramPrincipalName1, paramPrincipalName2, paramEncryptionKey, paramTicketFlags, paramKerberosTime1, paramKerberosTime2, paramKerberosTime3, paramKerberosTime4, paramHostAddresses);
    authzData = paramAuthorizationData;
  }
  
  public Credentials(Ticket paramTicket, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, EncryptionKey paramEncryptionKey, TicketFlags paramTicketFlags, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, HostAddresses paramHostAddresses)
  {
    ticket = paramTicket;
    client = paramPrincipalName1;
    server = paramPrincipalName2;
    key = paramEncryptionKey;
    flags = paramTicketFlags;
    authTime = paramKerberosTime1;
    startTime = paramKerberosTime2;
    endTime = paramKerberosTime3;
    renewTill = paramKerberosTime4;
    cAddr = paramHostAddresses;
  }
  
  public Credentials(byte[] paramArrayOfByte1, String paramString1, String paramString2, byte[] paramArrayOfByte2, int paramInt, boolean[] paramArrayOfBoolean, Date paramDate1, Date paramDate2, Date paramDate3, Date paramDate4, InetAddress[] paramArrayOfInetAddress)
    throws KrbException, IOException
  {
    this(new Ticket(paramArrayOfByte1), new PrincipalName(paramString1, 1), new PrincipalName(paramString2, 2), new EncryptionKey(paramInt, paramArrayOfByte2), paramArrayOfBoolean == null ? null : new TicketFlags(paramArrayOfBoolean), paramDate1 == null ? null : new KerberosTime(paramDate1), paramDate2 == null ? null : new KerberosTime(paramDate2), paramDate3 == null ? null : new KerberosTime(paramDate3), paramDate4 == null ? null : new KerberosTime(paramDate4), null);
  }
  
  public final PrincipalName getClient()
  {
    return client;
  }
  
  public final PrincipalName getServer()
  {
    return server;
  }
  
  public final EncryptionKey getSessionKey()
  {
    return key;
  }
  
  public final Date getAuthTime()
  {
    if (authTime != null) {
      return authTime.toDate();
    }
    return null;
  }
  
  public final Date getStartTime()
  {
    if (startTime != null) {
      return startTime.toDate();
    }
    return null;
  }
  
  public final Date getEndTime()
  {
    if (endTime != null) {
      return endTime.toDate();
    }
    return null;
  }
  
  public final Date getRenewTill()
  {
    if (renewTill != null) {
      return renewTill.toDate();
    }
    return null;
  }
  
  public final boolean[] getFlags()
  {
    if (flags == null) {
      return null;
    }
    return flags.toBooleanArray();
  }
  
  public final InetAddress[] getClientAddresses()
  {
    if (cAddr == null) {
      return null;
    }
    return cAddr.getInetAddresses();
  }
  
  public final byte[] getEncoded()
  {
    byte[] arrayOfByte = null;
    try
    {
      arrayOfByte = ticket.asn1Encode();
    }
    catch (Asn1Exception localAsn1Exception)
    {
      if (DEBUG) {
        System.out.println(localAsn1Exception);
      }
    }
    catch (IOException localIOException)
    {
      if (DEBUG) {
        System.out.println(localIOException);
      }
    }
    return arrayOfByte;
  }
  
  public boolean isForwardable()
  {
    return flags.get(1);
  }
  
  public boolean isRenewable()
  {
    return flags.get(8);
  }
  
  public Ticket getTicket()
  {
    return ticket;
  }
  
  public TicketFlags getTicketFlags()
  {
    return flags;
  }
  
  public AuthorizationData getAuthzData()
  {
    return authzData;
  }
  
  public boolean checkDelegate()
  {
    return flags.get(13);
  }
  
  public void resetDelegate()
  {
    flags.set(13, false);
  }
  
  public Credentials renew()
    throws KrbException, IOException
  {
    KDCOptions localKDCOptions = new KDCOptions();
    localKDCOptions.set(30, true);
    localKDCOptions.set(8, true);
    return new KrbTgsReq(localKDCOptions, this, server, null, null, null, null, cAddr, null, null, null).sendAndGetCreds();
  }
  
  public static Credentials acquireTGTFromCache(PrincipalName paramPrincipalName, String paramString)
    throws KrbException, IOException
  {
    if (paramString == null)
    {
      localObject1 = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
      if ((((String)localObject1).toUpperCase(Locale.ENGLISH).startsWith("WINDOWS")) || (((String)localObject1).toUpperCase(Locale.ENGLISH).contains("OS X")))
      {
        localObject2 = acquireDefaultCreds();
        if (localObject2 == null)
        {
          if (DEBUG) {
            System.out.println(">>> Found no TGT's in LSA");
          }
          return null;
        }
        if (paramPrincipalName != null)
        {
          if (((Credentials)localObject2).getClient().equals(paramPrincipalName))
          {
            if (DEBUG) {
              System.out.println(">>> Obtained TGT from LSA: " + localObject2);
            }
            return (Credentials)localObject2;
          }
          if (DEBUG) {
            System.out.println(">>> LSA contains TGT for " + ((Credentials)localObject2).getClient() + " not " + paramPrincipalName);
          }
          return null;
        }
        if (DEBUG) {
          System.out.println(">>> Obtained TGT from LSA: " + localObject2);
        }
        return (Credentials)localObject2;
      }
    }
    Object localObject1 = CredentialsCache.getInstance(paramPrincipalName, paramString);
    if (localObject1 == null) {
      return null;
    }
    Object localObject2 = ((CredentialsCache)localObject1).getDefaultCreds();
    if (localObject2 == null) {
      return null;
    }
    if (EType.isSupported(((sun.security.krb5.internal.ccache.Credentials)localObject2).getEType())) {
      return ((sun.security.krb5.internal.ccache.Credentials)localObject2).setKrbCreds();
    }
    if (DEBUG) {
      System.out.println(">>> unsupported key type found the default TGT: " + ((sun.security.krb5.internal.ccache.Credentials)localObject2).getEType());
    }
    return null;
  }
  
  public static synchronized Credentials acquireDefaultCreds()
  {
    Credentials localCredentials = null;
    if (cache == null) {
      cache = CredentialsCache.getInstance();
    }
    if (cache != null)
    {
      sun.security.krb5.internal.ccache.Credentials localCredentials1 = cache.getDefaultCreds();
      if (localCredentials1 != null)
      {
        if (DEBUG) {
          System.out.println(">>> KrbCreds found the default ticket granting ticket in credential cache.");
        }
        if (EType.isSupported(localCredentials1.getEType())) {
          localCredentials = localCredentials1.setKrbCreds();
        } else if (DEBUG) {
          System.out.println(">>> unsupported key type found the default TGT: " + localCredentials1.getEType());
        }
      }
    }
    if (localCredentials == null)
    {
      if (!alreadyTried) {
        try
        {
          ensureLoaded();
        }
        catch (Exception localException)
        {
          if (DEBUG)
          {
            System.out.println("Can not load credentials cache");
            localException.printStackTrace();
          }
          alreadyTried = true;
        }
      }
      if (alreadyLoaded)
      {
        if (DEBUG) {
          System.out.println(">> Acquire default native Credentials");
        }
        try
        {
          localCredentials = acquireDefaultNativeCreds(EType.getDefaults("default_tkt_enctypes"));
        }
        catch (KrbException localKrbException) {}
      }
    }
    return localCredentials;
  }
  
  public static Credentials acquireServiceCreds(String paramString, Credentials paramCredentials)
    throws KrbException, IOException
  {
    return CredentialsUtil.acquireServiceCreds(paramString, paramCredentials);
  }
  
  public static Credentials acquireS4U2selfCreds(PrincipalName paramPrincipalName, Credentials paramCredentials)
    throws KrbException, IOException
  {
    return CredentialsUtil.acquireS4U2selfCreds(paramPrincipalName, paramCredentials);
  }
  
  public static Credentials acquireS4U2proxyCreds(String paramString, Ticket paramTicket, PrincipalName paramPrincipalName, Credentials paramCredentials)
    throws KrbException, IOException
  {
    return CredentialsUtil.acquireS4U2proxyCreds(paramString, paramTicket, paramPrincipalName, paramCredentials);
  }
  
  public CredentialsCache getCache()
  {
    return cache;
  }
  
  public EncryptionKey getServiceKey()
  {
    return serviceKey;
  }
  
  public static void printDebug(Credentials paramCredentials)
  {
    System.out.println(">>> DEBUG: ----Credentials----");
    System.out.println("\tclient: " + client.toString());
    System.out.println("\tserver: " + server.toString());
    System.out.println("\tticket: sname: " + ticket.sname.toString());
    if (startTime != null) {
      System.out.println("\tstartTime: " + startTime.getTime());
    }
    System.out.println("\tendTime: " + endTime.getTime());
    System.out.println("        ----Credentials end----");
  }
  
  static void ensureLoaded()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        if (System.getProperty("os.name").contains("OS X")) {
          System.loadLibrary("osxkrb5");
        } else {
          System.loadLibrary("w2k_lsa_auth");
        }
        return null;
      }
    });
    alreadyLoaded = true;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("Credentials:");
    localStringBuffer.append("\n      client=").append(client);
    localStringBuffer.append("\n      server=").append(server);
    if (authTime != null) {
      localStringBuffer.append("\n    authTime=").append(authTime);
    }
    if (startTime != null) {
      localStringBuffer.append("\n   startTime=").append(startTime);
    }
    localStringBuffer.append("\n     endTime=").append(endTime);
    localStringBuffer.append("\n   renewTill=").append(renewTill);
    localStringBuffer.append("\n       flags=").append(flags);
    localStringBuffer.append("\nEType (skey)=").append(key.getEType());
    localStringBuffer.append("\n   (tkt key)=").append(ticket.encPart.eType);
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\Credentials.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */