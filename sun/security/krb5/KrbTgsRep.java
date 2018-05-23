package sun.security.krb5;

import java.io.IOException;
import sun.security.krb5.internal.EncKDCRepPart;
import sun.security.krb5.internal.EncTGSRepPart;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.TGSRep;
import sun.security.krb5.internal.TGSReq;
import sun.security.krb5.internal.Ticket;
import sun.security.util.DerValue;

public class KrbTgsRep
  extends KrbKdcRep
{
  private TGSRep rep;
  private Credentials creds;
  private Ticket secondTicket;
  private static final boolean DEBUG = Krb5.DEBUG;
  
  KrbTgsRep(byte[] paramArrayOfByte, KrbTgsReq paramKrbTgsReq)
    throws KrbException, IOException
  {
    DerValue localDerValue = new DerValue(paramArrayOfByte);
    TGSReq localTGSReq = paramKrbTgsReq.getMessage();
    TGSRep localTGSRep = null;
    try
    {
      localTGSRep = new TGSRep(localDerValue);
    }
    catch (Asn1Exception localAsn1Exception)
    {
      localTGSRep = null;
      localObject1 = new KRBError(localDerValue);
      localObject2 = ((KRBError)localObject1).getErrorString();
      Object localObject3 = null;
      if ((localObject2 != null) && (((String)localObject2).length() > 0)) {
        if (((String)localObject2).charAt(((String)localObject2).length() - 1) == 0) {
          localObject3 = ((String)localObject2).substring(0, ((String)localObject2).length() - 1);
        } else {
          localObject3 = localObject2;
        }
      }
      KrbException localKrbException;
      if (localObject3 == null) {
        localKrbException = new KrbException(((KRBError)localObject1).getErrorCode());
      } else {
        localKrbException = new KrbException(((KRBError)localObject1).getErrorCode(), (String)localObject3);
      }
      localKrbException.initCause(localAsn1Exception);
      throw localKrbException;
    }
    byte[] arrayOfByte = encPart.decrypt(tgsReqKey, paramKrbTgsReq.usedSubkey() ? 9 : 8);
    Object localObject1 = encPart.reset(arrayOfByte);
    localDerValue = new DerValue((byte[])localObject1);
    Object localObject2 = new EncTGSRepPart(localDerValue);
    encKDCRepPart = ((EncKDCRepPart)localObject2);
    check(false, localTGSReq, localTGSRep);
    creds = new Credentials(ticket, cname, sname, key, flags, authtime, starttime, endtime, renewTill, caddr);
    rep = localTGSRep;
    secondTicket = paramKrbTgsReq.getSecondTicket();
  }
  
  public Credentials getCreds()
  {
    return creds;
  }
  
  sun.security.krb5.internal.ccache.Credentials setCredentials()
  {
    return new sun.security.krb5.internal.ccache.Credentials(rep, secondTicket);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\KrbTgsRep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */