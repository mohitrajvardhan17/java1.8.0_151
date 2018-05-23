package sun.security.krb5.internal;

import sun.security.krb5.KrbException;

public class KrbErrException
  extends KrbException
{
  private static final long serialVersionUID = 2186533836785448317L;
  
  public KrbErrException(int paramInt)
  {
    super(paramInt);
  }
  
  public KrbErrException(int paramInt, String paramString)
  {
    super(paramInt, paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\KrbErrException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */