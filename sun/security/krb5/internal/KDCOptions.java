package sun.security.krb5.internal;

import java.io.IOException;
import java.io.PrintStream;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.Config;
import sun.security.krb5.KrbException;
import sun.security.krb5.internal.util.KerberosFlags;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

public class KDCOptions
  extends KerberosFlags
{
  private static final int KDC_OPT_PROXIABLE = 268435456;
  private static final int KDC_OPT_RENEWABLE_OK = 16;
  private static final int KDC_OPT_FORWARDABLE = 1073741824;
  public static final int RESERVED = 0;
  public static final int FORWARDABLE = 1;
  public static final int FORWARDED = 2;
  public static final int PROXIABLE = 3;
  public static final int PROXY = 4;
  public static final int ALLOW_POSTDATE = 5;
  public static final int POSTDATED = 6;
  public static final int UNUSED7 = 7;
  public static final int RENEWABLE = 8;
  public static final int UNUSED9 = 9;
  public static final int UNUSED10 = 10;
  public static final int UNUSED11 = 11;
  public static final int CNAME_IN_ADDL_TKT = 14;
  public static final int RENEWABLE_OK = 27;
  public static final int ENC_TKT_IN_SKEY = 28;
  public static final int RENEW = 30;
  public static final int VALIDATE = 31;
  private static final String[] names = { "RESERVED", "FORWARDABLE", "FORWARDED", "PROXIABLE", "PROXY", "ALLOW_POSTDATE", "POSTDATED", "UNUSED7", "RENEWABLE", "UNUSED9", "UNUSED10", "UNUSED11", null, null, "CNAME_IN_ADDL_TKT", null, null, null, null, null, null, null, null, null, null, null, null, "RENEWABLE_OK", "ENC_TKT_IN_SKEY", null, "RENEW", "VALIDATE" };
  private boolean DEBUG = Krb5.DEBUG;
  
  public static KDCOptions with(int... paramVarArgs)
  {
    KDCOptions localKDCOptions = new KDCOptions();
    for (int k : paramVarArgs) {
      localKDCOptions.set(k, true);
    }
    return localKDCOptions;
  }
  
  public KDCOptions()
  {
    super(32);
    setDefault();
  }
  
  public KDCOptions(int paramInt, byte[] paramArrayOfByte)
    throws Asn1Exception
  {
    super(paramInt, paramArrayOfByte);
    if ((paramInt > paramArrayOfByte.length * 8) || (paramInt > 32)) {
      throw new Asn1Exception(502);
    }
  }
  
  public KDCOptions(boolean[] paramArrayOfBoolean)
    throws Asn1Exception
  {
    super(paramArrayOfBoolean);
    if (paramArrayOfBoolean.length > 32) {
      throw new Asn1Exception(502);
    }
  }
  
  public KDCOptions(DerValue paramDerValue)
    throws Asn1Exception, IOException
  {
    this(paramDerValue.getUnalignedBitString(true).toBooleanArray());
  }
  
  public KDCOptions(byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte.length * 8, paramArrayOfByte);
  }
  
  public static KDCOptions parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
    throws Asn1Exception, IOException
  {
    if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)) {
      return null;
    }
    DerValue localDerValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (localDerValue1.getTag() & 0x1F)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    return new KDCOptions(localDerValue2);
  }
  
  public void set(int paramInt, boolean paramBoolean)
    throws ArrayIndexOutOfBoundsException
  {
    super.set(paramInt, paramBoolean);
  }
  
  public boolean get(int paramInt)
    throws ArrayIndexOutOfBoundsException
  {
    return super.get(paramInt);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("KDCOptions: ");
    for (int i = 0; i < 32; i++) {
      if (get(i)) {
        if (names[i] != null) {
          localStringBuilder.append(names[i]).append(",");
        } else {
          localStringBuilder.append(i).append(",");
        }
      }
    }
    return localStringBuilder.toString();
  }
  
  private void setDefault()
  {
    try
    {
      Config localConfig = Config.getInstance();
      int i = localConfig.getIntValue(new String[] { "libdefaults", "kdc_default_options" });
      if ((i & 0x10) == 16) {
        set(27, true);
      } else if (localConfig.getBooleanValue(new String[] { "libdefaults", "renewable" })) {
        set(27, true);
      }
      if ((i & 0x10000000) == 268435456) {
        set(3, true);
      } else if (localConfig.getBooleanValue(new String[] { "libdefaults", "proxiable" })) {
        set(3, true);
      }
      if ((i & 0x40000000) == 1073741824) {
        set(1, true);
      } else if (localConfig.getBooleanValue(new String[] { "libdefaults", "forwardable" })) {
        set(1, true);
      }
    }
    catch (KrbException localKrbException)
    {
      if (DEBUG)
      {
        System.out.println("Exception in getting default values for KDC Options from the configuration ");
        localKrbException.printStackTrace();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\KDCOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */