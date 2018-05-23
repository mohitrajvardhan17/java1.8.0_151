package sun.security.pkcs10;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X500Name;
import sun.security.x509.X509Key;

public class PKCS10
{
  private X500Name subject;
  private PublicKey subjectPublicKeyInfo;
  private String sigAlg;
  private PKCS10Attributes attributeSet;
  private byte[] encoded;
  
  public PKCS10(PublicKey paramPublicKey)
  {
    subjectPublicKeyInfo = paramPublicKey;
    attributeSet = new PKCS10Attributes();
  }
  
  public PKCS10(PublicKey paramPublicKey, PKCS10Attributes paramPKCS10Attributes)
  {
    subjectPublicKeyInfo = paramPublicKey;
    attributeSet = paramPKCS10Attributes;
  }
  
  public PKCS10(byte[] paramArrayOfByte)
    throws IOException, SignatureException, NoSuchAlgorithmException
  {
    encoded = paramArrayOfByte;
    DerInputStream localDerInputStream = new DerInputStream(paramArrayOfByte);
    DerValue[] arrayOfDerValue = localDerInputStream.getSequence(3);
    if (arrayOfDerValue.length != 3) {
      throw new IllegalArgumentException("not a PKCS #10 request");
    }
    paramArrayOfByte = arrayOfDerValue[0].toByteArray();
    AlgorithmId localAlgorithmId = AlgorithmId.parse(arrayOfDerValue[1]);
    byte[] arrayOfByte = arrayOfDerValue[2].getBitString();
    BigInteger localBigInteger = 0data.getBigInteger();
    if (!localBigInteger.equals(BigInteger.ZERO)) {
      throw new IllegalArgumentException("not PKCS #10 v1");
    }
    subject = new X500Name(0data);
    subjectPublicKeyInfo = X509Key.parse(0data.getDerValue());
    if (0data.available() != 0) {
      attributeSet = new PKCS10Attributes(0data);
    } else {
      attributeSet = new PKCS10Attributes();
    }
    if (0data.available() != 0) {
      throw new IllegalArgumentException("illegal PKCS #10 data");
    }
    try
    {
      sigAlg = localAlgorithmId.getName();
      Signature localSignature = Signature.getInstance(sigAlg);
      localSignature.initVerify(subjectPublicKeyInfo);
      localSignature.update(paramArrayOfByte);
      if (!localSignature.verify(arrayOfByte)) {
        throw new SignatureException("Invalid PKCS #10 signature");
      }
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      throw new SignatureException("invalid key");
    }
  }
  
  public void encodeAndSign(X500Name paramX500Name, Signature paramSignature)
    throws CertificateException, IOException, SignatureException
  {
    if (encoded != null) {
      throw new SignatureException("request is already signed");
    }
    subject = paramX500Name;
    Object localObject = new DerOutputStream();
    ((DerOutputStream)localObject).putInteger(BigInteger.ZERO);
    paramX500Name.encode((DerOutputStream)localObject);
    ((DerOutputStream)localObject).write(subjectPublicKeyInfo.getEncoded());
    attributeSet.encode((OutputStream)localObject);
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.write((byte)48, (DerOutputStream)localObject);
    byte[] arrayOfByte1 = localDerOutputStream.toByteArray();
    localObject = localDerOutputStream;
    paramSignature.update(arrayOfByte1, 0, arrayOfByte1.length);
    byte[] arrayOfByte2 = paramSignature.sign();
    sigAlg = paramSignature.getAlgorithm();
    AlgorithmId localAlgorithmId = null;
    try
    {
      localAlgorithmId = AlgorithmId.get(paramSignature.getAlgorithm());
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new SignatureException(localNoSuchAlgorithmException);
    }
    localAlgorithmId.encode((DerOutputStream)localObject);
    ((DerOutputStream)localObject).putBitString(arrayOfByte2);
    localDerOutputStream = new DerOutputStream();
    localDerOutputStream.write((byte)48, (DerOutputStream)localObject);
    encoded = localDerOutputStream.toByteArray();
  }
  
  public X500Name getSubjectName()
  {
    return subject;
  }
  
  public PublicKey getSubjectPublicKeyInfo()
  {
    return subjectPublicKeyInfo;
  }
  
  public String getSigAlg()
  {
    return sigAlg;
  }
  
  public PKCS10Attributes getAttributes()
  {
    return attributeSet;
  }
  
  public byte[] getEncoded()
  {
    if (encoded != null) {
      return (byte[])encoded.clone();
    }
    return null;
  }
  
  public void print(PrintStream paramPrintStream)
    throws IOException, SignatureException
  {
    if (encoded == null) {
      throw new SignatureException("Cert request was not signed");
    }
    byte[] arrayOfByte = { 13, 10 };
    paramPrintStream.println("-----BEGIN NEW CERTIFICATE REQUEST-----");
    paramPrintStream.println(Base64.getMimeEncoder(64, arrayOfByte).encodeToString(encoded));
    paramPrintStream.println("-----END NEW CERTIFICATE REQUEST-----");
  }
  
  public String toString()
  {
    return "[PKCS #10 certificate request:\n" + subjectPublicKeyInfo.toString() + " subject: <" + subject + ">\n attributes: " + attributeSet.toString() + "\n]";
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof PKCS10)) {
      return false;
    }
    if (encoded == null) {
      return false;
    }
    byte[] arrayOfByte = ((PKCS10)paramObject).getEncoded();
    if (arrayOfByte == null) {
      return false;
    }
    return Arrays.equals(encoded, arrayOfByte);
  }
  
  public int hashCode()
  {
    int i = 0;
    if (encoded != null) {
      for (int j = 1; j < encoded.length; j++) {
        i += encoded[j] * j;
      }
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\pkcs10\PKCS10.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */