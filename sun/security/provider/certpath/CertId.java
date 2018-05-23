package sun.security.provider.certpath;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;
import sun.security.x509.SerialNumber;

public class CertId
{
  private static final boolean debug = false;
  private static final AlgorithmId SHA1_ALGID = new AlgorithmId(AlgorithmId.SHA_oid);
  private final AlgorithmId hashAlgId;
  private final byte[] issuerNameHash;
  private final byte[] issuerKeyHash;
  private final SerialNumber certSerialNumber;
  private int myhash = -1;
  
  public CertId(X509Certificate paramX509Certificate, SerialNumber paramSerialNumber)
    throws IOException
  {
    this(paramX509Certificate.getSubjectX500Principal(), paramX509Certificate.getPublicKey(), paramSerialNumber);
  }
  
  public CertId(X500Principal paramX500Principal, PublicKey paramPublicKey, SerialNumber paramSerialNumber)
    throws IOException
  {
    MessageDigest localMessageDigest = null;
    try
    {
      localMessageDigest = MessageDigest.getInstance("SHA1");
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new IOException("Unable to create CertId", localNoSuchAlgorithmException);
    }
    hashAlgId = SHA1_ALGID;
    localMessageDigest.update(paramX500Principal.getEncoded());
    issuerNameHash = localMessageDigest.digest();
    byte[] arrayOfByte1 = paramPublicKey.getEncoded();
    DerValue localDerValue = new DerValue(arrayOfByte1);
    DerValue[] arrayOfDerValue = new DerValue[2];
    arrayOfDerValue[0] = data.getDerValue();
    arrayOfDerValue[1] = data.getDerValue();
    byte[] arrayOfByte2 = arrayOfDerValue[1].getBitString();
    localMessageDigest.update(arrayOfByte2);
    issuerKeyHash = localMessageDigest.digest();
    certSerialNumber = paramSerialNumber;
  }
  
  public CertId(DerInputStream paramDerInputStream)
    throws IOException
  {
    hashAlgId = AlgorithmId.parse(paramDerInputStream.getDerValue());
    issuerNameHash = paramDerInputStream.getOctetString();
    issuerKeyHash = paramDerInputStream.getOctetString();
    certSerialNumber = new SerialNumber(paramDerInputStream);
  }
  
  public AlgorithmId getHashAlgorithm()
  {
    return hashAlgId;
  }
  
  public byte[] getIssuerNameHash()
  {
    return issuerNameHash;
  }
  
  public byte[] getIssuerKeyHash()
  {
    return issuerKeyHash;
  }
  
  public BigInteger getSerialNumber()
  {
    return certSerialNumber.getNumber();
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    hashAlgId.encode(localDerOutputStream);
    localDerOutputStream.putOctetString(issuerNameHash);
    localDerOutputStream.putOctetString(issuerKeyHash);
    certSerialNumber.encode(localDerOutputStream);
    paramDerOutputStream.write((byte)48, localDerOutputStream);
  }
  
  public int hashCode()
  {
    if (myhash == -1)
    {
      myhash = hashAlgId.hashCode();
      for (int i = 0; i < issuerNameHash.length; i++) {
        myhash += issuerNameHash[i] * i;
      }
      for (i = 0; i < issuerKeyHash.length; i++) {
        myhash += issuerKeyHash[i] * i;
      }
      myhash += certSerialNumber.getNumber().hashCode();
    }
    return myhash;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (!(paramObject instanceof CertId))) {
      return false;
    }
    CertId localCertId = (CertId)paramObject;
    return (hashAlgId.equals(localCertId.getHashAlgorithm())) && (Arrays.equals(issuerNameHash, localCertId.getIssuerNameHash())) && (Arrays.equals(issuerKeyHash, localCertId.getIssuerKeyHash())) && (certSerialNumber.getNumber().equals(localCertId.getSerialNumber()));
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("CertId \n");
    localStringBuilder.append("Algorithm: " + hashAlgId.toString() + "\n");
    localStringBuilder.append("issuerNameHash \n");
    HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
    localStringBuilder.append(localHexDumpEncoder.encode(issuerNameHash));
    localStringBuilder.append("\nissuerKeyHash: \n");
    localStringBuilder.append(localHexDumpEncoder.encode(issuerKeyHash));
    localStringBuilder.append("\n" + certSerialNumber.toString());
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\CertId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */