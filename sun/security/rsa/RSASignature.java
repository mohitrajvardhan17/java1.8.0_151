package sun.security.rsa;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.BadPaddingException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

public abstract class RSASignature
  extends SignatureSpi
{
  private static final int baseLength = 8;
  private final ObjectIdentifier digestOID;
  private final int encodedLength;
  private final MessageDigest md;
  private boolean digestReset;
  private RSAPrivateKey privateKey;
  private RSAPublicKey publicKey;
  private RSAPadding padding;
  
  RSASignature(String paramString, ObjectIdentifier paramObjectIdentifier, int paramInt)
  {
    digestOID = paramObjectIdentifier;
    try
    {
      md = MessageDigest.getInstance(paramString);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new ProviderException(localNoSuchAlgorithmException);
    }
    digestReset = true;
    encodedLength = (8 + paramInt + md.getDigestLength());
  }
  
  protected void engineInitVerify(PublicKey paramPublicKey)
    throws InvalidKeyException
  {
    RSAPublicKey localRSAPublicKey = (RSAPublicKey)RSAKeyFactory.toRSAKey(paramPublicKey);
    privateKey = null;
    publicKey = localRSAPublicKey;
    initCommon(localRSAPublicKey, null);
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey)
    throws InvalidKeyException
  {
    engineInitSign(paramPrivateKey, null);
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom)
    throws InvalidKeyException
  {
    RSAPrivateKey localRSAPrivateKey = (RSAPrivateKey)RSAKeyFactory.toRSAKey(paramPrivateKey);
    privateKey = localRSAPrivateKey;
    publicKey = null;
    initCommon(localRSAPrivateKey, paramSecureRandom);
  }
  
  private void initCommon(RSAKey paramRSAKey, SecureRandom paramSecureRandom)
    throws InvalidKeyException
  {
    resetDigest();
    int i = RSACore.getByteLength(paramRSAKey);
    try
    {
      padding = RSAPadding.getInstance(1, i, paramSecureRandom);
    }
    catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException)
    {
      throw new InvalidKeyException(localInvalidAlgorithmParameterException.getMessage());
    }
    int j = padding.getMaxDataSize();
    if (encodedLength > j) {
      throw new InvalidKeyException("Key is too short for this signature algorithm");
    }
  }
  
  private void resetDigest()
  {
    if (!digestReset)
    {
      md.reset();
      digestReset = true;
    }
  }
  
  private byte[] getDigestValue()
  {
    digestReset = true;
    return md.digest();
  }
  
  protected void engineUpdate(byte paramByte)
    throws SignatureException
  {
    md.update(paramByte);
    digestReset = false;
  }
  
  protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SignatureException
  {
    md.update(paramArrayOfByte, paramInt1, paramInt2);
    digestReset = false;
  }
  
  protected void engineUpdate(ByteBuffer paramByteBuffer)
  {
    md.update(paramByteBuffer);
    digestReset = false;
  }
  
  protected byte[] engineSign()
    throws SignatureException
  {
    byte[] arrayOfByte1 = getDigestValue();
    try
    {
      byte[] arrayOfByte2 = encodeSignature(digestOID, arrayOfByte1);
      byte[] arrayOfByte3 = padding.pad(arrayOfByte2);
      byte[] arrayOfByte4 = RSACore.rsa(arrayOfByte3, privateKey, true);
      return arrayOfByte4;
    }
    catch (GeneralSecurityException localGeneralSecurityException)
    {
      throw new SignatureException("Could not sign data", localGeneralSecurityException);
    }
    catch (IOException localIOException)
    {
      throw new SignatureException("Could not encode data", localIOException);
    }
  }
  
  protected boolean engineVerify(byte[] paramArrayOfByte)
    throws SignatureException
  {
    if (paramArrayOfByte.length != RSACore.getByteLength(publicKey)) {
      throw new SignatureException("Signature length not correct: got " + paramArrayOfByte.length + " but was expecting " + RSACore.getByteLength(publicKey));
    }
    byte[] arrayOfByte1 = getDigestValue();
    try
    {
      byte[] arrayOfByte2 = RSACore.rsa(paramArrayOfByte, publicKey);
      byte[] arrayOfByte3 = padding.unpad(arrayOfByte2);
      byte[] arrayOfByte4 = decodeSignature(digestOID, arrayOfByte3);
      return MessageDigest.isEqual(arrayOfByte1, arrayOfByte4);
    }
    catch (BadPaddingException localBadPaddingException)
    {
      return false;
    }
    catch (IOException localIOException)
    {
      throw new SignatureException("Signature encoding error", localIOException);
    }
  }
  
  public static byte[] encodeSignature(ObjectIdentifier paramObjectIdentifier, byte[] paramArrayOfByte)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    new AlgorithmId(paramObjectIdentifier).encode(localDerOutputStream);
    localDerOutputStream.putOctetString(paramArrayOfByte);
    DerValue localDerValue = new DerValue((byte)48, localDerOutputStream.toByteArray());
    return localDerValue.toByteArray();
  }
  
  public static byte[] decodeSignature(ObjectIdentifier paramObjectIdentifier, byte[] paramArrayOfByte)
    throws IOException
  {
    DerInputStream localDerInputStream = new DerInputStream(paramArrayOfByte, 0, paramArrayOfByte.length, false);
    DerValue[] arrayOfDerValue = localDerInputStream.getSequence(2);
    if ((arrayOfDerValue.length != 2) || (localDerInputStream.available() != 0)) {
      throw new IOException("SEQUENCE length error");
    }
    AlgorithmId localAlgorithmId = AlgorithmId.parse(arrayOfDerValue[0]);
    if (!localAlgorithmId.getOID().equals(paramObjectIdentifier)) {
      throw new IOException("ObjectIdentifier mismatch: " + localAlgorithmId.getOID());
    }
    if (localAlgorithmId.getEncodedParams() != null) {
      throw new IOException("Unexpected AlgorithmId parameters");
    }
    byte[] arrayOfByte = arrayOfDerValue[1].getOctetString();
    return arrayOfByte;
  }
  
  @Deprecated
  protected void engineSetParameter(String paramString, Object paramObject)
    throws InvalidParameterException
  {
    throw new UnsupportedOperationException("setParameter() not supported");
  }
  
  @Deprecated
  protected Object engineGetParameter(String paramString)
    throws InvalidParameterException
  {
    throw new UnsupportedOperationException("getParameter() not supported");
  }
  
  public static final class MD2withRSA
    extends RSASignature
  {
    public MD2withRSA()
    {
      super(AlgorithmId.MD2_oid, 10);
    }
  }
  
  public static final class MD5withRSA
    extends RSASignature
  {
    public MD5withRSA()
    {
      super(AlgorithmId.MD5_oid, 10);
    }
  }
  
  public static final class SHA1withRSA
    extends RSASignature
  {
    public SHA1withRSA()
    {
      super(AlgorithmId.SHA_oid, 7);
    }
  }
  
  public static final class SHA224withRSA
    extends RSASignature
  {
    public SHA224withRSA()
    {
      super(AlgorithmId.SHA224_oid, 11);
    }
  }
  
  public static final class SHA256withRSA
    extends RSASignature
  {
    public SHA256withRSA()
    {
      super(AlgorithmId.SHA256_oid, 11);
    }
  }
  
  public static final class SHA384withRSA
    extends RSASignature
  {
    public SHA384withRSA()
    {
      super(AlgorithmId.SHA384_oid, 11);
    }
  }
  
  public static final class SHA512withRSA
    extends RSASignature
  {
    public SHA512withRSA()
    {
      super(AlgorithmId.SHA512_oid, 11);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\rsa\RSASignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */