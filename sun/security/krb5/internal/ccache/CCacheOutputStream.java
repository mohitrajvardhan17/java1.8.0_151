package sun.security.krb5.internal.ccache;

import java.io.IOException;
import java.io.OutputStream;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.util.KrbDataOutputStream;

public class CCacheOutputStream
  extends KrbDataOutputStream
  implements FileCCacheConstants
{
  public CCacheOutputStream(OutputStream paramOutputStream)
  {
    super(paramOutputStream);
  }
  
  public void writeHeader(PrincipalName paramPrincipalName, int paramInt)
    throws IOException
  {
    write((paramInt & 0xFF00) >> 8);
    write(paramInt & 0xFF);
    paramPrincipalName.writePrincipal(this);
  }
  
  public void addCreds(Credentials paramCredentials)
    throws IOException, Asn1Exception
  {
    cname.writePrincipal(this);
    sname.writePrincipal(this);
    key.writeKey(this);
    write32((int)(authtime.getTime() / 1000L));
    if (starttime != null) {
      write32((int)(starttime.getTime() / 1000L));
    } else {
      write32(0);
    }
    write32((int)(endtime.getTime() / 1000L));
    if (renewTill != null) {
      write32((int)(renewTill.getTime() / 1000L));
    } else {
      write32(0);
    }
    if (isEncInSKey) {
      write8(1);
    } else {
      write8(0);
    }
    writeFlags(flags);
    if (caddr == null) {
      write32(0);
    } else {
      caddr.writeAddrs(this);
    }
    if (authorizationData == null) {
      write32(0);
    } else {
      authorizationData.writeAuth(this);
    }
    writeTicket(ticket);
    writeTicket(secondTicket);
  }
  
  void writeTicket(Ticket paramTicket)
    throws IOException, Asn1Exception
  {
    if (paramTicket == null)
    {
      write32(0);
    }
    else
    {
      byte[] arrayOfByte = paramTicket.asn1Encode();
      write32(arrayOfByte.length);
      write(arrayOfByte, 0, arrayOfByte.length);
    }
  }
  
  void writeFlags(TicketFlags paramTicketFlags)
    throws IOException
  {
    int i = 0;
    boolean[] arrayOfBoolean = paramTicketFlags.toBooleanArray();
    if (arrayOfBoolean[1] == 1) {
      i |= 0x40000000;
    }
    if (arrayOfBoolean[2] == 1) {
      i |= 0x20000000;
    }
    if (arrayOfBoolean[3] == 1) {
      i |= 0x10000000;
    }
    if (arrayOfBoolean[4] == 1) {
      i |= 0x8000000;
    }
    if (arrayOfBoolean[5] == 1) {
      i |= 0x4000000;
    }
    if (arrayOfBoolean[6] == 1) {
      i |= 0x2000000;
    }
    if (arrayOfBoolean[7] == 1) {
      i |= 0x1000000;
    }
    if (arrayOfBoolean[8] == 1) {
      i |= 0x800000;
    }
    if (arrayOfBoolean[9] == 1) {
      i |= 0x400000;
    }
    if (arrayOfBoolean[10] == 1) {
      i |= 0x200000;
    }
    if (arrayOfBoolean[11] == 1) {
      i |= 0x100000;
    }
    write32(i);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\ccache\CCacheOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */