package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class FreshestCRLExtension
  extends CRLDistributionPointsExtension
{
  public static final String NAME = "FreshestCRL";
  
  public FreshestCRLExtension(List<DistributionPoint> paramList)
    throws IOException
  {
    super(PKIXExtensions.FreshestCRL_Id, false, paramList, "FreshestCRL");
  }
  
  public FreshestCRLExtension(Boolean paramBoolean, Object paramObject)
    throws IOException
  {
    super(PKIXExtensions.FreshestCRL_Id, Boolean.valueOf(paramBoolean.booleanValue()), paramObject, "FreshestCRL");
  }
  
  public void encode(OutputStream paramOutputStream)
    throws IOException
  {
    super.encode(paramOutputStream, PKIXExtensions.FreshestCRL_Id, false);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\FreshestCRLExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */