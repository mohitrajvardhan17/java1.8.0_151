package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;
import sun.security.util.DerValue;

public class ASRep
  extends KDCRep
{
  public ASRep(PAData[] paramArrayOfPAData, PrincipalName paramPrincipalName, Ticket paramTicket, EncryptedData paramEncryptedData)
    throws IOException
  {
    super(paramArrayOfPAData, paramPrincipalName, paramTicket, paramEncryptedData, 11);
  }
  
  public ASRep(byte[] paramArrayOfByte)
    throws Asn1Exception, RealmException, KrbApErrException, IOException
  {
    init(new DerValue(paramArrayOfByte));
  }
  
  public ASRep(DerValue paramDerValue)
    throws Asn1Exception, RealmException, KrbApErrException, IOException
  {
    init(paramDerValue);
  }
  
  private void init(DerValue paramDerValue)
    throws Asn1Exception, RealmException, KrbApErrException, IOException
  {
    init(paramDerValue, 11);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\ASRep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */