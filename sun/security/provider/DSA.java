package sun.security.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.DigestException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.util.Arrays;
import sun.security.jca.JCAUtil;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

abstract class DSA
  extends SignatureSpi
{
  private static final boolean debug = false;
  private static final int BLINDING_BITS = 7;
  private static final BigInteger BLINDING_CONSTANT = BigInteger.valueOf(128L);
  private DSAParams params;
  private BigInteger presetP;
  private BigInteger presetQ;
  private BigInteger presetG;
  private BigInteger presetY;
  private BigInteger presetX;
  private SecureRandom signingRandom;
  private final MessageDigest md;
  
  DSA(MessageDigest paramMessageDigest)
  {
    md = paramMessageDigest;
  }
  
  private static void checkKey(DSAParams paramDSAParams, int paramInt, String paramString)
    throws InvalidKeyException
  {
    int i = paramDSAParams.getQ().bitLength();
    if (i > paramInt) {
      throw new InvalidKeyException("The security strength of " + paramString + " digest algorithm is not sufficient for this key size");
    }
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey)
    throws InvalidKeyException
  {
    if (!(paramPrivateKey instanceof DSAPrivateKey)) {
      throw new InvalidKeyException("not a DSA private key: " + paramPrivateKey);
    }
    DSAPrivateKey localDSAPrivateKey = (DSAPrivateKey)paramPrivateKey;
    DSAParams localDSAParams = localDSAPrivateKey.getParams();
    if (localDSAParams == null) {
      throw new InvalidKeyException("DSA private key lacks parameters");
    }
    if (md.getAlgorithm() != "NullDigest20") {
      checkKey(localDSAParams, md.getDigestLength() * 8, md.getAlgorithm());
    }
    params = localDSAParams;
    presetX = localDSAPrivateKey.getX();
    presetY = null;
    presetP = localDSAParams.getP();
    presetQ = localDSAParams.getQ();
    presetG = localDSAParams.getG();
    md.reset();
  }
  
  protected void engineInitVerify(PublicKey paramPublicKey)
    throws InvalidKeyException
  {
    if (!(paramPublicKey instanceof DSAPublicKey)) {
      throw new InvalidKeyException("not a DSA public key: " + paramPublicKey);
    }
    DSAPublicKey localDSAPublicKey = (DSAPublicKey)paramPublicKey;
    DSAParams localDSAParams = localDSAPublicKey.getParams();
    if (localDSAParams == null) {
      throw new InvalidKeyException("DSA public key lacks parameters");
    }
    params = localDSAParams;
    presetY = localDSAPublicKey.getY();
    presetX = null;
    presetP = localDSAParams.getP();
    presetQ = localDSAParams.getQ();
    presetG = localDSAParams.getG();
    md.reset();
  }
  
  protected void engineUpdate(byte paramByte)
  {
    md.update(paramByte);
  }
  
  protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    md.update(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  protected void engineUpdate(ByteBuffer paramByteBuffer)
  {
    md.update(paramByteBuffer);
  }
  
  protected byte[] engineSign()
    throws SignatureException
  {
    BigInteger localBigInteger1 = generateK(presetQ);
    BigInteger localBigInteger2 = generateR(presetP, presetQ, presetG, localBigInteger1);
    BigInteger localBigInteger3 = generateS(presetX, presetQ, localBigInteger2, localBigInteger1);
    try
    {
      DerOutputStream localDerOutputStream = new DerOutputStream(100);
      localDerOutputStream.putInteger(localBigInteger2);
      localDerOutputStream.putInteger(localBigInteger3);
      DerValue localDerValue = new DerValue((byte)48, localDerOutputStream.toByteArray());
      return localDerValue.toByteArray();
    }
    catch (IOException localIOException)
    {
      throw new SignatureException("error encoding signature");
    }
  }
  
  protected boolean engineVerify(byte[] paramArrayOfByte)
    throws SignatureException
  {
    return engineVerify(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  protected boolean engineVerify(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SignatureException
  {
    BigInteger localBigInteger1 = null;
    BigInteger localBigInteger2 = null;
    Object localObject;
    try
    {
      DerInputStream localDerInputStream = new DerInputStream(paramArrayOfByte, paramInt1, paramInt2, false);
      localObject = localDerInputStream.getSequence(2);
      if ((localObject.length != 2) || (localDerInputStream.available() != 0)) {
        throw new IOException("Invalid encoding for signature");
      }
      localBigInteger1 = localObject[0].getBigInteger();
      localBigInteger2 = localObject[1].getBigInteger();
    }
    catch (IOException localIOException)
    {
      throw new SignatureException("Invalid encoding for signature", localIOException);
    }
    if (localBigInteger1.signum() < 0) {
      localBigInteger1 = new BigInteger(1, localBigInteger1.toByteArray());
    }
    if (localBigInteger2.signum() < 0) {
      localBigInteger2 = new BigInteger(1, localBigInteger2.toByteArray());
    }
    if ((localBigInteger1.compareTo(presetQ) == -1) && (localBigInteger2.compareTo(presetQ) == -1))
    {
      BigInteger localBigInteger3 = generateW(presetP, presetQ, presetG, localBigInteger2);
      localObject = generateV(presetY, presetP, presetQ, presetG, localBigInteger3, localBigInteger1);
      return ((BigInteger)localObject).equals(localBigInteger1);
    }
    throw new SignatureException("invalid signature: out of range values");
  }
  
  @Deprecated
  protected void engineSetParameter(String paramString, Object paramObject)
  {
    throw new InvalidParameterException("No parameter accepted");
  }
  
  @Deprecated
  protected Object engineGetParameter(String paramString)
  {
    return null;
  }
  
  private BigInteger generateR(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4)
  {
    SecureRandom localSecureRandom = getSigningRandom();
    BigInteger localBigInteger1 = new BigInteger(7, localSecureRandom);
    localBigInteger1 = localBigInteger1.add(BLINDING_CONSTANT);
    paramBigInteger4 = paramBigInteger4.add(paramBigInteger2.multiply(localBigInteger1));
    BigInteger localBigInteger2 = paramBigInteger3.modPow(paramBigInteger4, paramBigInteger1);
    return localBigInteger2.mod(paramBigInteger2);
  }
  
  private BigInteger generateS(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4)
    throws SignatureException
  {
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = md.digest();
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new SignatureException(localRuntimeException.getMessage());
    }
    int i = paramBigInteger2.bitLength() / 8;
    if (i < arrayOfByte.length) {
      arrayOfByte = Arrays.copyOfRange(arrayOfByte, 0, i);
    }
    BigInteger localBigInteger1 = new BigInteger(1, arrayOfByte);
    BigInteger localBigInteger2 = paramBigInteger4.modInverse(paramBigInteger2);
    return paramBigInteger1.multiply(paramBigInteger3).add(localBigInteger1).multiply(localBigInteger2).mod(paramBigInteger2);
  }
  
  private BigInteger generateW(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4)
  {
    return paramBigInteger4.modInverse(paramBigInteger2);
  }
  
  private BigInteger generateV(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigInteger paramBigInteger6)
    throws SignatureException
  {
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = md.digest();
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new SignatureException(localRuntimeException.getMessage());
    }
    int i = paramBigInteger3.bitLength() / 8;
    if (i < arrayOfByte.length) {
      arrayOfByte = Arrays.copyOfRange(arrayOfByte, 0, i);
    }
    BigInteger localBigInteger1 = new BigInteger(1, arrayOfByte);
    BigInteger localBigInteger2 = localBigInteger1.multiply(paramBigInteger5).mod(paramBigInteger3);
    BigInteger localBigInteger3 = paramBigInteger6.multiply(paramBigInteger5).mod(paramBigInteger3);
    BigInteger localBigInteger4 = paramBigInteger4.modPow(localBigInteger2, paramBigInteger2);
    BigInteger localBigInteger5 = paramBigInteger1.modPow(localBigInteger3, paramBigInteger2);
    BigInteger localBigInteger6 = localBigInteger4.multiply(localBigInteger5);
    BigInteger localBigInteger7 = localBigInteger6.mod(paramBigInteger2);
    return localBigInteger7.mod(paramBigInteger3);
  }
  
  protected BigInteger generateK(BigInteger paramBigInteger)
  {
    SecureRandom localSecureRandom = getSigningRandom();
    byte[] arrayOfByte = new byte[(paramBigInteger.bitLength() + 7) / 8 + 8];
    localSecureRandom.nextBytes(arrayOfByte);
    return new BigInteger(1, arrayOfByte).mod(paramBigInteger.subtract(BigInteger.ONE)).add(BigInteger.ONE);
  }
  
  protected SecureRandom getSigningRandom()
  {
    if (signingRandom == null) {
      if (appRandom != null) {
        signingRandom = appRandom;
      } else {
        signingRandom = JCAUtil.getSecureRandom();
      }
    }
    return signingRandom;
  }
  
  public String toString()
  {
    String str = "DSA Signature";
    if ((presetP != null) && (presetQ != null) && (presetG != null))
    {
      str = str + "\n\tp: " + Debug.toHexString(presetP);
      str = str + "\n\tq: " + Debug.toHexString(presetQ);
      str = str + "\n\tg: " + Debug.toHexString(presetG);
    }
    else
    {
      str = str + "\n\t P, Q or G not initialized.";
    }
    if (presetY != null) {
      str = str + "\n\ty: " + Debug.toHexString(presetY);
    }
    if ((presetY == null) && (presetX == null)) {
      str = str + "\n\tUNINIIALIZED";
    }
    return str;
  }
  
  private static void debug(Exception paramException) {}
  
  private static void debug(String paramString) {}
  
  public static final class RawDSA
    extends DSA
  {
    public RawDSA()
      throws NoSuchAlgorithmException
    {
      super();
    }
    
    public static final class NullDigest20
      extends MessageDigest
    {
      private final byte[] digestBuffer = new byte[20];
      private int ofs = 0;
      
      protected NullDigest20()
      {
        super();
      }
      
      protected void engineUpdate(byte paramByte)
      {
        if (ofs == digestBuffer.length) {
          ofs = Integer.MAX_VALUE;
        } else {
          digestBuffer[(ofs++)] = paramByte;
        }
      }
      
      protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      {
        if (ofs + paramInt2 > digestBuffer.length)
        {
          ofs = Integer.MAX_VALUE;
        }
        else
        {
          System.arraycopy(paramArrayOfByte, paramInt1, digestBuffer, ofs, paramInt2);
          ofs += paramInt2;
        }
      }
      
      protected final void engineUpdate(ByteBuffer paramByteBuffer)
      {
        int i = paramByteBuffer.remaining();
        if (ofs + i > digestBuffer.length)
        {
          ofs = Integer.MAX_VALUE;
        }
        else
        {
          paramByteBuffer.get(digestBuffer, ofs, i);
          ofs += i;
        }
      }
      
      protected byte[] engineDigest()
        throws RuntimeException
      {
        if (ofs != digestBuffer.length) {
          throw new RuntimeException("Data for RawDSA must be exactly 20 bytes long");
        }
        reset();
        return digestBuffer;
      }
      
      protected int engineDigest(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
        throws DigestException
      {
        if (ofs != digestBuffer.length) {
          throw new DigestException("Data for RawDSA must be exactly 20 bytes long");
        }
        if (paramInt2 < digestBuffer.length) {
          throw new DigestException("Output buffer too small; must be at least 20 bytes");
        }
        System.arraycopy(digestBuffer, 0, paramArrayOfByte, paramInt1, digestBuffer.length);
        reset();
        return digestBuffer.length;
      }
      
      protected void engineReset()
      {
        ofs = 0;
      }
      
      protected final int engineGetDigestLength()
      {
        return digestBuffer.length;
      }
    }
  }
  
  public static final class SHA1withDSA
    extends DSA
  {
    public SHA1withDSA()
      throws NoSuchAlgorithmException
    {
      super();
    }
  }
  
  public static final class SHA224withDSA
    extends DSA
  {
    public SHA224withDSA()
      throws NoSuchAlgorithmException
    {
      super();
    }
  }
  
  public static final class SHA256withDSA
    extends DSA
  {
    public SHA256withDSA()
      throws NoSuchAlgorithmException
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\DSA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */