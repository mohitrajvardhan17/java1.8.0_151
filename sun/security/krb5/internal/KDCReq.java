package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.KrbException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KDCReq
{
  public KDCReqBody reqBody;
  private int pvno;
  private int msgType;
  private PAData[] pAData = null;
  
  public KDCReq(PAData[] paramArrayOfPAData, KDCReqBody paramKDCReqBody, int paramInt)
    throws IOException
  {
    pvno = 5;
    msgType = paramInt;
    if (paramArrayOfPAData != null)
    {
      pAData = new PAData[paramArrayOfPAData.length];
      for (int i = 0; i < paramArrayOfPAData.length; i++)
      {
        if (paramArrayOfPAData[i] == null) {
          throw new IOException("Cannot create a KDCRep");
        }
        pAData[i] = ((PAData)paramArrayOfPAData[i].clone());
      }
    }
    reqBody = paramKDCReqBody;
  }
  
  public KDCReq() {}
  
  public KDCReq(byte[] paramArrayOfByte, int paramInt)
    throws Asn1Exception, IOException, KrbException
  {
    init(new DerValue(paramArrayOfByte), paramInt);
  }
  
  public KDCReq(DerValue paramDerValue, int paramInt)
    throws Asn1Exception, IOException, KrbException
  {
    init(paramDerValue, paramInt);
  }
  
  protected void init(DerValue paramDerValue, int paramInt)
    throws Asn1Exception, IOException, KrbException
  {
    if ((paramDerValue.getTag() & 0x1F) != paramInt) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue1 = paramDerValue.getData().getDerValue();
    if (localDerValue1.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    BigInteger localBigInteger;
    if ((localDerValue2.getTag() & 0x1F) == 1)
    {
      localBigInteger = localDerValue2.getData().getBigInteger();
      pvno = localBigInteger.intValue();
      if (pvno != 5) {
        throw new KrbApErrException(39);
      }
    }
    else
    {
      throw new Asn1Exception(906);
    }
    localDerValue2 = localDerValue1.getData().getDerValue();
    if ((localDerValue2.getTag() & 0x1F) == 2)
    {
      localBigInteger = localDerValue2.getData().getBigInteger();
      msgType = localBigInteger.intValue();
      if (msgType != paramInt) {
        throw new KrbApErrException(40);
      }
    }
    else
    {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue3;
    if ((localDerValue1.getData().peekByte() & 0x1F) == 3)
    {
      localDerValue2 = localDerValue1.getData().getDerValue();
      localDerValue3 = localDerValue2.getData().getDerValue();
      if (localDerValue3.getTag() != 48) {
        throw new Asn1Exception(906);
      }
      Vector localVector = new Vector();
      while (localDerValue3.getData().available() > 0) {
        localVector.addElement(new PAData(localDerValue3.getData().getDerValue()));
      }
      if (localVector.size() > 0)
      {
        pAData = new PAData[localVector.size()];
        localVector.copyInto(pAData);
      }
    }
    else
    {
      pAData = null;
    }
    localDerValue2 = localDerValue1.getData().getDerValue();
    if ((localDerValue2.getTag() & 0x1F) == 4)
    {
      localDerValue3 = localDerValue2.getData().getDerValue();
      reqBody = new KDCReqBody(localDerValue3, msgType);
    }
    else
    {
      throw new Asn1Exception(906);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(BigInteger.valueOf(pvno));
    DerOutputStream localDerOutputStream3 = new DerOutputStream();
    localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream1);
    localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(BigInteger.valueOf(msgType));
    localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream1);
    if ((pAData != null) && (pAData.length > 0))
    {
      localDerOutputStream1 = new DerOutputStream();
      for (int i = 0; i < pAData.length; i++) {
        localDerOutputStream1.write(pAData[i].asn1Encode());
      }
      localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.write((byte)48, localDerOutputStream1);
      localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), localDerOutputStream2);
    }
    localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), reqBody.asn1Encode(msgType));
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write((byte)48, localDerOutputStream3);
    localDerOutputStream3 = new DerOutputStream();
    localDerOutputStream3.write(DerValue.createTag((byte)64, true, (byte)msgType), localDerOutputStream2);
    return localDerOutputStream3.toByteArray();
  }
  
  public byte[] asn1EncodeReqBody()
    throws Asn1Exception, IOException
  {
    return reqBody.asn1Encode(msgType);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\KDCReq.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */