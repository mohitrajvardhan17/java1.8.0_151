package sun.security.krb5.internal.ccache;

import sun.security.krb5.EncryptedData;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.EncKDCRepPart;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCRep;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;

public class Credentials
{
  PrincipalName cname;
  PrincipalName sname;
  EncryptionKey key;
  KerberosTime authtime;
  KerberosTime starttime;
  KerberosTime endtime;
  KerberosTime renewTill;
  HostAddresses caddr;
  AuthorizationData authorizationData;
  public boolean isEncInSKey;
  TicketFlags flags;
  Ticket ticket;
  Ticket secondTicket;
  private boolean DEBUG = Krb5.DEBUG;
  
  public Credentials(PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, EncryptionKey paramEncryptionKey, KerberosTime paramKerberosTime1, KerberosTime paramKerberosTime2, KerberosTime paramKerberosTime3, KerberosTime paramKerberosTime4, boolean paramBoolean, TicketFlags paramTicketFlags, HostAddresses paramHostAddresses, AuthorizationData paramAuthorizationData, Ticket paramTicket1, Ticket paramTicket2)
  {
    cname = ((PrincipalName)paramPrincipalName1.clone());
    sname = ((PrincipalName)paramPrincipalName2.clone());
    key = ((EncryptionKey)paramEncryptionKey.clone());
    authtime = paramKerberosTime1;
    starttime = paramKerberosTime2;
    endtime = paramKerberosTime3;
    renewTill = paramKerberosTime4;
    if (paramHostAddresses != null) {
      caddr = ((HostAddresses)paramHostAddresses.clone());
    }
    if (paramAuthorizationData != null) {
      authorizationData = ((AuthorizationData)paramAuthorizationData.clone());
    }
    isEncInSKey = paramBoolean;
    flags = ((TicketFlags)paramTicketFlags.clone());
    ticket = ((Ticket)paramTicket1.clone());
    if (paramTicket2 != null) {
      secondTicket = ((Ticket)paramTicket2.clone());
    }
  }
  
  public Credentials(KDCRep paramKDCRep, Ticket paramTicket, AuthorizationData paramAuthorizationData, boolean paramBoolean)
  {
    if (encKDCRepPart == null) {
      return;
    }
    cname = ((PrincipalName)cname.clone());
    ticket = ((Ticket)ticket.clone());
    key = ((EncryptionKey)encKDCRepPart.key.clone());
    flags = ((TicketFlags)encKDCRepPart.flags.clone());
    authtime = encKDCRepPart.authtime;
    starttime = encKDCRepPart.starttime;
    endtime = encKDCRepPart.endtime;
    renewTill = encKDCRepPart.renewTill;
    sname = ((PrincipalName)encKDCRepPart.sname.clone());
    caddr = ((HostAddresses)encKDCRepPart.caddr.clone());
    secondTicket = ((Ticket)paramTicket.clone());
    authorizationData = ((AuthorizationData)paramAuthorizationData.clone());
    isEncInSKey = paramBoolean;
  }
  
  public Credentials(KDCRep paramKDCRep)
  {
    this(paramKDCRep, null);
  }
  
  public Credentials(KDCRep paramKDCRep, Ticket paramTicket)
  {
    sname = ((PrincipalName)encKDCRepPart.sname.clone());
    cname = ((PrincipalName)cname.clone());
    key = ((EncryptionKey)encKDCRepPart.key.clone());
    authtime = encKDCRepPart.authtime;
    starttime = encKDCRepPart.starttime;
    endtime = encKDCRepPart.endtime;
    renewTill = encKDCRepPart.renewTill;
    flags = encKDCRepPart.flags;
    if (encKDCRepPart.caddr != null) {
      caddr = ((HostAddresses)encKDCRepPart.caddr.clone());
    } else {
      caddr = null;
    }
    ticket = ((Ticket)ticket.clone());
    if (paramTicket != null)
    {
      secondTicket = ((Ticket)paramTicket.clone());
      isEncInSKey = true;
    }
    else
    {
      secondTicket = null;
      isEncInSKey = false;
    }
  }
  
  public boolean isValid()
  {
    boolean bool = true;
    if (endtime.getTime() < System.currentTimeMillis()) {
      bool = false;
    } else if (starttime != null)
    {
      if (starttime.getTime() > System.currentTimeMillis()) {
        bool = false;
      }
    }
    else if (authtime.getTime() > System.currentTimeMillis()) {
      bool = false;
    }
    return bool;
  }
  
  public PrincipalName getServicePrincipal()
    throws RealmException
  {
    return sname;
  }
  
  public sun.security.krb5.Credentials setKrbCreds()
  {
    return new sun.security.krb5.Credentials(ticket, cname, sname, key, flags, authtime, starttime, endtime, renewTill, caddr);
  }
  
  public KerberosTime getStartTime()
  {
    return starttime;
  }
  
  public KerberosTime getAuthTime()
  {
    return authtime;
  }
  
  public KerberosTime getEndTime()
  {
    return endtime;
  }
  
  public KerberosTime getRenewTill()
  {
    return renewTill;
  }
  
  public TicketFlags getTicketFlags()
  {
    return flags;
  }
  
  public int getEType()
  {
    return key.getEType();
  }
  
  public int getTktEType()
  {
    return ticket.encPart.getEType();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\ccache\Credentials.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */