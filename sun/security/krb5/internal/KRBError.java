package sun.security.krb5.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.Checksum;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KRBError
  implements Serializable
{
  static final long serialVersionUID = 3643809337475284503L;
  private int pvno;
  private int msgType;
  private KerberosTime cTime;
  private Integer cuSec;
  private KerberosTime sTime;
  private Integer suSec;
  private int errorCode;
  private PrincipalName cname;
  private PrincipalName sname;
  private String eText;
  private byte[] eData;
  private Checksum eCksum;
  private PAData[] pa;
  private static boolean DEBUG = Krb5.DEBUG;
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    try
    {
      init(new DerValue((byte[])paramObjectInputStream.readObject()));
      parseEData(eData);
    }
    catch (Exception localException)
    {
      throw new IOException(localException);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    try
    {
      paramObjectOutputStream.writeObject(asn1Encode());
    }
    catch (Exception localException)
    {
      throw new IOException(localException);
    }
  }
  
  public KRBError(APOptions paramAPOptions, KerberosTime paramKerberosTime1, Integer paramInteger1, KerberosTime paramKerberosTime2, Integer paramInteger2, int paramInt, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, String paramString, byte[] paramArrayOfByte)
    throws IOException, Asn1Exception
  {
    pvno = 5;
    msgType = 30;
    cTime = paramKerberosTime1;
    cuSec = paramInteger1;
    sTime = paramKerberosTime2;
    suSec = paramInteger2;
    errorCode = paramInt;
    cname = paramPrincipalName1;
    sname = paramPrincipalName2;
    eText = paramString;
    eData = paramArrayOfByte;
    parseEData(eData);
  }
  
  public KRBError(APOptions paramAPOptions, KerberosTime paramKerberosTime1, Integer paramInteger1, KerberosTime paramKerberosTime2, Integer paramInteger2, int paramInt, PrincipalName paramPrincipalName1, PrincipalName paramPrincipalName2, String paramString, byte[] paramArrayOfByte, Checksum paramChecksum)
    throws IOException, Asn1Exception
  {
    pvno = 5;
    msgType = 30;
    cTime = paramKerberosTime1;
    cuSec = paramInteger1;
    sTime = paramKerberosTime2;
    suSec = paramInteger2;
    errorCode = paramInt;
    cname = paramPrincipalName1;
    sname = paramPrincipalName2;
    eText = paramString;
    eData = paramArrayOfByte;
    eCksum = paramChecksum;
    parseEData(eData);
  }
  
  public KRBError(byte[] paramArrayOfByte)
    throws Asn1Exception, RealmException, KrbApErrException, IOException
  {
    init(new DerValue(paramArrayOfByte));
    parseEData(eData);
  }
  
  public KRBError(DerValue paramDerValue)
    throws Asn1Exception, RealmException, KrbApErrException, IOException
  {
    init(paramDerValue);
    showDebug();
    parseEData(eData);
  }
  
  private void parseEData(byte[] paramArrayOfByte)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      return;
    }
    if ((errorCode == 25) || (errorCode == 24)) {
      try
      {
        parsePAData(paramArrayOfByte);
      }
      catch (Exception localException)
      {
        if (DEBUG) {
          System.out.println("Unable to parse eData field of KRB-ERROR:\n" + new HexDumpEncoder().encodeBuffer(paramArrayOfByte));
        }
        IOException localIOException = new IOException("Unable to parse eData field of KRB-ERROR");
        localIOException.initCause(localException);
        throw localIOException;
      }
    } else if (DEBUG) {
      System.out.println("Unknown eData field of KRB-ERROR:\n" + new HexDumpEncoder().encodeBuffer(paramArrayOfByte));
    }
  }
  
  private void parsePAData(byte[] paramArrayOfByte)
    throws IOException, Asn1Exception
  {
    DerValue localDerValue1 = new DerValue(paramArrayOfByte);
    ArrayList localArrayList = new ArrayList();
    while (data.available() > 0)
    {
      DerValue localDerValue2 = data.getDerValue();
      PAData localPAData = new PAData(localDerValue2);
      localArrayList.add(localPAData);
      if (DEBUG) {
        System.out.println(localPAData);
      }
    }
    pa = ((PAData[])localArrayList.toArray(new PAData[localArrayList.size()]));
  }
  
  public final KerberosTime getServerTime()
  {
    return sTime;
  }
  
  public final KerberosTime getClientTime()
  {
    return cTime;
  }
  
  public final Integer getServerMicroSeconds()
  {
    return suSec;
  }
  
  public final Integer getClientMicroSeconds()
  {
    return cuSec;
  }
  
  public final int getErrorCode()
  {
    return errorCode;
  }
  
  public final PAData[] getPA()
  {
    return pa;
  }
  
  public final String getErrorString()
  {
    return eText;
  }
  
  private void init(DerValue paramDerValue)
    throws Asn1Exception, RealmException, KrbApErrException, IOException
  {
    if (((paramDerValue.getTag() & 0x1F) != 30) || (paramDerValue.isApplication() != true) || (paramDerValue.isConstructed() != true)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue1 = paramDerValue.getData().getDerValue();
    if (localDerValue1.getTag() != 48) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    if ((localDerValue2.getTag() & 0x1F) == 0)
    {
      pvno = localDerValue2.getData().getBigInteger().intValue();
      if (pvno != 5) {
        throw new KrbApErrException(39);
      }
    }
    else
    {
      throw new Asn1Exception(906);
    }
    localDerValue2 = localDerValue1.getData().getDerValue();
    if ((localDerValue2.getTag() & 0x1F) == 1)
    {
      msgType = localDerValue2.getData().getBigInteger().intValue();
      if (msgType != 30) {
        throw new KrbApErrException(40);
      }
    }
    else
    {
      throw new Asn1Exception(906);
    }
    cTime = KerberosTime.parse(localDerValue1.getData(), (byte)2, true);
    if ((localDerValue1.getData().peekByte() & 0x1F) == 3)
    {
      localDerValue2 = localDerValue1.getData().getDerValue();
      cuSec = new Integer(localDerValue2.getData().getBigInteger().intValue());
    }
    else
    {
      cuSec = null;
    }
    sTime = KerberosTime.parse(localDerValue1.getData(), (byte)4, false);
    localDerValue2 = localDerValue1.getData().getDerValue();
    if ((localDerValue2.getTag() & 0x1F) == 5) {
      suSec = new Integer(localDerValue2.getData().getBigInteger().intValue());
    } else {
      throw new Asn1Exception(906);
    }
    localDerValue2 = localDerValue1.getData().getDerValue();
    if ((localDerValue2.getTag() & 0x1F) == 6) {
      errorCode = localDerValue2.getData().getBigInteger().intValue();
    } else {
      throw new Asn1Exception(906);
    }
    Realm localRealm1 = Realm.parse(localDerValue1.getData(), (byte)7, true);
    cname = PrincipalName.parse(localDerValue1.getData(), (byte)8, true, localRealm1);
    Realm localRealm2 = Realm.parse(localDerValue1.getData(), (byte)9, false);
    sname = PrincipalName.parse(localDerValue1.getData(), (byte)10, false, localRealm2);
    eText = null;
    eData = null;
    eCksum = null;
    if ((localDerValue1.getData().available() > 0) && ((localDerValue1.getData().peekByte() & 0x1F) == 11))
    {
      localDerValue2 = localDerValue1.getData().getDerValue();
      eText = new KerberosString(localDerValue2.getData().getDerValue()).toString();
    }
    if ((localDerValue1.getData().available() > 0) && ((localDerValue1.getData().peekByte() & 0x1F) == 12))
    {
      localDerValue2 = localDerValue1.getData().getDerValue();
      eData = localDerValue2.getData().getOctetString();
    }
    if (localDerValue1.getData().available() > 0) {
      eCksum = Checksum.parse(localDerValue1.getData(), (byte)13, true);
    }
    if (localDerValue1.getData().available() > 0) {
      throw new Asn1Exception(906);
    }
  }
  
  private void showDebug()
  {
    if (DEBUG)
    {
      System.out.println(">>>KRBError:");
      if (cTime != null) {
        System.out.println("\t cTime is " + cTime.toDate().toString() + " " + cTime.toDate().getTime());
      }
      if (cuSec != null) {
        System.out.println("\t cuSec is " + cuSec.intValue());
      }
      System.out.println("\t sTime is " + sTime.toDate().toString() + " " + sTime.toDate().getTime());
      System.out.println("\t suSec is " + suSec);
      System.out.println("\t error code is " + errorCode);
      System.out.println("\t error Message is " + Krb5.getErrorMessage(errorCode));
      if (cname != null) {
        System.out.println("\t cname is " + cname.toString());
      }
      if (sname != null) {
        System.out.println("\t sname is " + sname.toString());
      }
      if (eData != null) {
        System.out.println("\t eData provided.");
      }
      if (eCksum != null) {
        System.out.println("\t checksum provided.");
      }
      System.out.println("\t msgType is " + msgType);
    }
  }
  
  public byte[] asn1Encode()
    throws Asn1Exception, IOException
  {
    DerOutputStream localDerOutputStream1 = new DerOutputStream();
    DerOutputStream localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream1.putInteger(BigInteger.valueOf(pvno));
    localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream1);
    localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(BigInteger.valueOf(msgType));
    localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream1);
    if (cTime != null) {
      localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), cTime.asn1Encode());
    }
    if (cuSec != null)
    {
      localDerOutputStream1 = new DerOutputStream();
      localDerOutputStream1.putInteger(BigInteger.valueOf(cuSec.intValue()));
      localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), localDerOutputStream1);
    }
    localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)4), sTime.asn1Encode());
    localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(BigInteger.valueOf(suSec.intValue()));
    localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)5), localDerOutputStream1);
    localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.putInteger(BigInteger.valueOf(errorCode));
    localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)6), localDerOutputStream1);
    if (cname != null)
    {
      localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)7), cname.getRealm().asn1Encode());
      localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)8), cname.asn1Encode());
    }
    localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)9), sname.getRealm().asn1Encode());
    localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)10), sname.asn1Encode());
    if (eText != null)
    {
      localDerOutputStream1 = new DerOutputStream();
      localDerOutputStream1.putDerValue(new KerberosString(eText).toDerValue());
      localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)11), localDerOutputStream1);
    }
    if (eData != null)
    {
      localDerOutputStream1 = new DerOutputStream();
      localDerOutputStream1.putOctetString(eData);
      localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)12), localDerOutputStream1);
    }
    if (eCksum != null) {
      localDerOutputStream2.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)13), eCksum.asn1Encode());
    }
    localDerOutputStream1 = new DerOutputStream();
    localDerOutputStream1.write((byte)48, localDerOutputStream2);
    localDerOutputStream2 = new DerOutputStream();
    localDerOutputStream2.write(DerValue.createTag((byte)64, true, (byte)30), localDerOutputStream1);
    return localDerOutputStream2.toByteArray();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof KRBError)) {
      return false;
    }
    KRBError localKRBError = (KRBError)paramObject;
    return (pvno == pvno) && (msgType == msgType) && (isEqual(cTime, cTime)) && (isEqual(cuSec, cuSec)) && (isEqual(sTime, sTime)) && (isEqual(suSec, suSec)) && (errorCode == errorCode) && (isEqual(cname, cname)) && (isEqual(sname, sname)) && (isEqual(eText, eText)) && (Arrays.equals(eData, eData)) && (isEqual(eCksum, eCksum));
  }
  
  private static boolean isEqual(Object paramObject1, Object paramObject2)
  {
    return paramObject1 == null ? false : paramObject2 == null ? true : paramObject1.equals(paramObject2);
  }
  
  public int hashCode()
  {
    int i = 17;
    i = 37 * i + pvno;
    i = 37 * i + msgType;
    if (cTime != null) {
      i = 37 * i + cTime.hashCode();
    }
    if (cuSec != null) {
      i = 37 * i + cuSec.hashCode();
    }
    if (sTime != null) {
      i = 37 * i + sTime.hashCode();
    }
    if (suSec != null) {
      i = 37 * i + suSec.hashCode();
    }
    i = 37 * i + errorCode;
    if (cname != null) {
      i = 37 * i + cname.hashCode();
    }
    if (sname != null) {
      i = 37 * i + sname.hashCode();
    }
    if (eText != null) {
      i = 37 * i + eText.hashCode();
    }
    i = 37 * i + Arrays.hashCode(eData);
    if (eCksum != null) {
      i = 37 * i + eCksum.hashCode();
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\KRBError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */