package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.KrbException;
import sun.security.util.DerValue;

public class TGSReq
  extends KDCReq
{
  public TGSReq(PAData[] paramArrayOfPAData, KDCReqBody paramKDCReqBody)
    throws IOException
  {
    super(paramArrayOfPAData, paramKDCReqBody, 12);
  }
  
  public TGSReq(byte[] paramArrayOfByte)
    throws Asn1Exception, IOException, KrbException
  {
    init(new DerValue(paramArrayOfByte));
  }
  
  public TGSReq(DerValue paramDerValue)
    throws Asn1Exception, IOException, KrbException
  {
    init(paramDerValue);
  }
  
  private void init(DerValue paramDerValue)
    throws Asn1Exception, IOException, KrbException
  {
    init(paramDerValue, 12);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\TGSReq.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */