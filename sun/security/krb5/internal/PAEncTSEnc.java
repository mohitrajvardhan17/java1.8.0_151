package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PAEncTSEnc
{
  public KerberosTime pATimeStamp;
  public Integer pAUSec;
  
  public PAEncTSEnc(KerberosTime paramKerberosTime, Integer paramInteger)
  {
    pATimeStamp = paramKerberosTime;
    pAUSec = paramInteger;
  }
  
  public PAEncTSEnc()
  {
    KerberosTime localKerberosTime = KerberosTime.now();
    pATimeStamp = localKerberosTime;
    pAUSec = new Integer(localKerberosTime.getMicroSeconds());
  }
  
  public PAEncTSEnc(DerValue paramDerValue)
    throws Asn1Exception, IOException
  {
    if (paramDerValue.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    pATimeStamp = KerberosTime.parse(paramDerValue.getData(), (byte)0, false);
    if (paramDerValue.getData().available() > 0)
    {
      DerValue localDerValue = paramDerValue.getData().getDerValue();
      if ((localDerValue.getTag() & 0x1F) == 1) {
        pAUSec = new Integer(localDerValue.getData().getBigInteger().intValue());
      } else {
        throw new Asn1Exception(906);
      }
    }
    if (paramDerValue.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), pATimeStamp.asn1Encode());
    if (pAUSec != null)
    {
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putInteger(BigInteger.valueOf(pAUSec.intValue()));
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream2);
    }
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\PAEncTSEnc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */