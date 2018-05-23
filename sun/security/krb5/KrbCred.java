package sun.security.krb5;

import java.io.IOException;
import java.io.PrintStream;
import sun.security.krb5.internal.EncKrbCredPart;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.KRBCred;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbCredInfo;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;
import sun.security.util.DerValue;

public class KrbCred
{
  private static boolean DEBUG = Krb5.DEBUG;
  private byte[] obuf = null;
  private KRBCred credMessg = null;
  private Ticket ticket = null;
  private EncKrbCredPart encPart = null;
  private Credentials creds = null;
  private KerberosTime timeStamp = null;
  
  public KrbCred(Credentials paramCredentials1, Credentials paramCredentials2, EncryptionKey paramEncryptionKey)
    throws KrbException, IOException
  {
    PrincipalName localPrincipalName1 = paramCredentials1.getClient();
    PrincipalName localPrincipalName2 = paramCredentials1.getServer();
    PrincipalName localPrincipalName3 = paramCredentials2.getServer();
    if (!paramCredentials2.getClient().equals(localPrincipalName1)) {
      throw new KrbException(60, "Client principal does not match");
    }
    KDCOptions localKDCOptions = new KDCOptions();
    localKDCOptions.set(2, true);
    localKDCOptions.set(1, true);
    HostAddresses localHostAddresses = null;
    if (localPrincipalName3.getNameType() == 3) {
      localHostAddresses = new HostAddresses(localPrincipalName3);
    }
    KrbTgsReq localKrbTgsReq = new KrbTgsReq(localKDCOptions, paramCredentials1, localPrincipalName2, null, null, null, null, localHostAddresses, null, null, null);
    credMessg = createMessage(localKrbTgsReq.sendAndGetCreds(), paramEncryptionKey);
    obuf = credMessg.asn1Encode();
  }
  
  KRBCred createMessage(Credentials paramCredentials, EncryptionKey paramEncryptionKey)
    throws KrbException, IOException
  {
    EncryptionKey localEncryptionKey = paramCredentials.getSessionKey();
    PrincipalName localPrincipalName1 = paramCredentials.getClient();
    Realm localRealm = localPrincipalName1.getRealm();
    PrincipalName localPrincipalName2 = paramCredentials.getServer();
    KrbCredInfo localKrbCredInfo = new KrbCredInfo(localEncryptionKey, localPrincipalName1, flags, authTime, startTime, endTime, renewTill, localPrincipalName2, cAddr);
    timeStamp = KerberosTime.now();
    KrbCredInfo[] arrayOfKrbCredInfo = { localKrbCredInfo };
    EncKrbCredPart localEncKrbCredPart = new EncKrbCredPart(arrayOfKrbCredInfo, timeStamp, null, null, null, null);
    EncryptedData localEncryptedData = new EncryptedData(paramEncryptionKey, localEncKrbCredPart.asn1Encode(), 14);
    Ticket[] arrayOfTicket = { ticket };
    credMessg = new KRBCred(arrayOfTicket, localEncryptedData);
    return credMessg;
  }
  
  public KrbCred(byte[] paramArrayOfByte, EncryptionKey paramEncryptionKey)
    throws KrbException, IOException
  {
    credMessg = new KRBCred(paramArrayOfByte);
    ticket = credMessg.tickets[0];
    if (credMessg.encPart.getEType() == 0) {
      paramEncryptionKey = EncryptionKey.NULL_KEY;
    }
    byte[] arrayOfByte1 = credMessg.encPart.decrypt(paramEncryptionKey, 14);
    byte[] arrayOfByte2 = credMessg.encPart.reset(arrayOfByte1);
    DerValue localDerValue = new DerValue(arrayOfByte2);
    EncKrbCredPart localEncKrbCredPart = new EncKrbCredPart(localDerValue);
    timeStamp = timeStamp;
    KrbCredInfo localKrbCredInfo = ticketInfo[0];
    EncryptionKey localEncryptionKey = key;
    PrincipalName localPrincipalName1 = pname;
    TicketFlags localTicketFlags = flags;
    KerberosTime localKerberosTime1 = authtime;
    KerberosTime localKerberosTime2 = starttime;
    KerberosTime localKerberosTime3 = endtime;
    KerberosTime localKerberosTime4 = renewTill;
    PrincipalName localPrincipalName2 = sname;
    HostAddresses localHostAddresses = caddr;
    if (DEBUG) {
      System.out.println(">>>Delegated Creds have pname=" + localPrincipalName1 + " sname=" + localPrincipalName2 + " authtime=" + localKerberosTime1 + " starttime=" + localKerberosTime2 + " endtime=" + localKerberosTime3 + "renewTill=" + localKerberosTime4);
    }
    creds = new Credentials(ticket, localPrincipalName1, localPrincipalName2, localEncryptionKey, localTicketFlags, localKerberosTime1, localKerberosTime2, localKerberosTime3, localKerberosTime4, localHostAddresses);
  }
  
  public Credentials[] getDelegatedCreds()
  {
    Credentials[] arrayOfCredentials = { creds };
    return arrayOfCredentials;
  }
  
  public byte[] getMessage()
  {
    return obuf;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\KrbCred.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */