package sun.security.krb5;

import java.io.PrintStream;
import sun.security.krb5.internal.EncKDCRepPart;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.KDCRep;
import sun.security.krb5.internal.KDCReq;
import sun.security.krb5.internal.KDCReqBody;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.TicketFlags;

abstract class KrbKdcRep
{
  KrbKdcRep() {}
  
  static void check(boolean paramBoolean, KDCReq paramKDCReq, KDCRep paramKDCRep)
    throws KrbApErrException
  {
    if ((paramBoolean) && (!reqBody.cname.equals(cname)))
    {
      encKDCRepPart.key.destroy();
      throw new KrbApErrException(41);
    }
    if (!reqBody.sname.equals(encKDCRepPart.sname))
    {
      encKDCRepPart.key.destroy();
      throw new KrbApErrException(41);
    }
    if (reqBody.getNonce() != encKDCRepPart.nonce)
    {
      encKDCRepPart.key.destroy();
      throw new KrbApErrException(41);
    }
    if ((reqBody.addresses != null) && (encKDCRepPart.caddr != null) && (!reqBody.addresses.equals(encKDCRepPart.caddr)))
    {
      encKDCRepPart.key.destroy();
      throw new KrbApErrException(41);
    }
    for (int i = 2; i < 6; i++) {
      if (reqBody.kdcOptions.get(i) != encKDCRepPart.flags.get(i))
      {
        if (Krb5.DEBUG) {
          System.out.println("> KrbKdcRep.check: at #" + i + ". request for " + reqBody.kdcOptions.get(i) + ", received " + encKDCRepPart.flags.get(i));
        }
        throw new KrbApErrException(41);
      }
    }
    if (reqBody.kdcOptions.get(8) != encKDCRepPart.flags.get(8)) {
      throw new KrbApErrException(41);
    }
    if (((reqBody.from == null) || (reqBody.from.isZero())) && (encKDCRepPart.starttime != null) && (!encKDCRepPart.starttime.inClockSkew()))
    {
      encKDCRepPart.key.destroy();
      throw new KrbApErrException(37);
    }
    if ((reqBody.from != null) && (!reqBody.from.isZero()) && (encKDCRepPart.starttime != null) && (!reqBody.from.equals(encKDCRepPart.starttime)))
    {
      encKDCRepPart.key.destroy();
      throw new KrbApErrException(41);
    }
    if ((!reqBody.till.isZero()) && (encKDCRepPart.endtime.greaterThan(reqBody.till)))
    {
      encKDCRepPart.key.destroy();
      throw new KrbApErrException(41);
    }
    if ((reqBody.kdcOptions.get(8)) && (reqBody.rtime != null) && (!reqBody.rtime.isZero()) && ((encKDCRepPart.renewTill == null) || (encKDCRepPart.renewTill.greaterThan(reqBody.rtime))))
    {
      encKDCRepPart.key.destroy();
      throw new KrbApErrException(41);
    }
    if ((reqBody.kdcOptions.get(27)) && (encKDCRepPart.flags.get(8)) && (!reqBody.till.isZero()) && ((encKDCRepPart.renewTill == null) || (encKDCRepPart.renewTill.greaterThan(reqBody.till))))
    {
      encKDCRepPart.key.destroy();
      throw new KrbApErrException(41);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\KrbKdcRep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */