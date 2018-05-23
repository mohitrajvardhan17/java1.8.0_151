package sun.security.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.spec.ECField;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

public class ECUtil
{
  public static ECPoint decodePoint(byte[] paramArrayOfByte, EllipticCurve paramEllipticCurve)
    throws IOException
  {
    if ((paramArrayOfByte.length == 0) || (paramArrayOfByte[0] != 4)) {
      throw new IOException("Only uncompressed point format supported");
    }
    int i = (paramArrayOfByte.length - 1) / 2;
    if (i != paramEllipticCurve.getField().getFieldSize() + 7 >> 3) {
      throw new IOException("Point does not match field size");
    }
    byte[] arrayOfByte1 = Arrays.copyOfRange(paramArrayOfByte, 1, 1 + i);
    byte[] arrayOfByte2 = Arrays.copyOfRange(paramArrayOfByte, i + 1, i + 1 + i);
    return new ECPoint(new BigInteger(1, arrayOfByte1), new BigInteger(1, arrayOfByte2));
  }
  
  public static byte[] encodePoint(ECPoint paramECPoint, EllipticCurve paramEllipticCurve)
  {
    int i = paramEllipticCurve.getField().getFieldSize() + 7 >> 3;
    byte[] arrayOfByte1 = trimZeroes(paramECPoint.getAffineX().toByteArray());
    byte[] arrayOfByte2 = trimZeroes(paramECPoint.getAffineY().toByteArray());
    if ((arrayOfByte1.length > i) || (arrayOfByte2.length > i)) {
      throw new RuntimeException("Point coordinates do not match field size");
    }
    byte[] arrayOfByte3 = new byte[1 + (i << 1)];
    arrayOfByte3[0] = 4;
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, i - arrayOfByte1.length + 1, arrayOfByte1.length);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte3.length - arrayOfByte2.length, arrayOfByte2.length);
    return arrayOfByte3;
  }
  
  public static byte[] trimZeroes(byte[] paramArrayOfByte)
  {
    for (int i = 0; (i < paramArrayOfByte.length - 1) && (paramArrayOfByte[i] == 0); i++) {}
    if (i == 0) {
      return paramArrayOfByte;
    }
    return Arrays.copyOfRange(paramArrayOfByte, i, paramArrayOfByte.length);
  }
  
  private static AlgorithmParameters getECParameters(Provider paramProvider)
  {
    try
    {
      if (paramProvider != null) {
        return AlgorithmParameters.getInstance("EC", paramProvider);
      }
      return AlgorithmParameters.getInstance("EC");
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new RuntimeException(localNoSuchAlgorithmException);
    }
  }
  
  public static byte[] encodeECParameterSpec(Provider paramProvider, ECParameterSpec paramECParameterSpec)
  {
    AlgorithmParameters localAlgorithmParameters = getECParameters(paramProvider);
    try
    {
      localAlgorithmParameters.init(paramECParameterSpec);
    }
    catch (InvalidParameterSpecException localInvalidParameterSpecException)
    {
      throw new RuntimeException("Not a known named curve: " + paramECParameterSpec);
    }
    try
    {
      return localAlgorithmParameters.getEncoded();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  public static ECParameterSpec getECParameterSpec(Provider paramProvider, ECParameterSpec paramECParameterSpec)
  {
    AlgorithmParameters localAlgorithmParameters = getECParameters(paramProvider);
    try
    {
      localAlgorithmParameters.init(paramECParameterSpec);
      return (ECParameterSpec)localAlgorithmParameters.getParameterSpec(ECParameterSpec.class);
    }
    catch (InvalidParameterSpecException localInvalidParameterSpecException) {}
    return null;
  }
  
  public static ECParameterSpec getECParameterSpec(Provider paramProvider, byte[] paramArrayOfByte)
    throws IOException
  {
    AlgorithmParameters localAlgorithmParameters = getECParameters(paramProvider);
    localAlgorithmParameters.init(paramArrayOfByte);
    try
    {
      return (ECParameterSpec)localAlgorithmParameters.getParameterSpec(ECParameterSpec.class);
    }
    catch (InvalidParameterSpecException localInvalidParameterSpecException) {}
    return null;
  }
  
  public static ECParameterSpec getECParameterSpec(Provider paramProvider, String paramString)
  {
    AlgorithmParameters localAlgorithmParameters = getECParameters(paramProvider);
    try
    {
      localAlgorithmParameters.init(new ECGenParameterSpec(paramString));
      return (ECParameterSpec)localAlgorithmParameters.getParameterSpec(ECParameterSpec.class);
    }
    catch (InvalidParameterSpecException localInvalidParameterSpecException) {}
    return null;
  }
  
  public static ECParameterSpec getECParameterSpec(Provider paramProvider, int paramInt)
  {
    AlgorithmParameters localAlgorithmParameters = getECParameters(paramProvider);
    try
    {
      localAlgorithmParameters.init(new ECKeySizeParameterSpec(paramInt));
      return (ECParameterSpec)localAlgorithmParameters.getParameterSpec(ECParameterSpec.class);
    }
    catch (InvalidParameterSpecException localInvalidParameterSpecException) {}
    return null;
  }
  
  public static String getCurveName(Provider paramProvider, ECParameterSpec paramECParameterSpec)
  {
    AlgorithmParameters localAlgorithmParameters = getECParameters(paramProvider);
    ECGenParameterSpec localECGenParameterSpec;
    try
    {
      localAlgorithmParameters.init(paramECParameterSpec);
      localECGenParameterSpec = (ECGenParameterSpec)localAlgorithmParameters.getParameterSpec(ECGenParameterSpec.class);
    }
    catch (InvalidParameterSpecException localInvalidParameterSpecException)
    {
      return null;
    }
    if (localECGenParameterSpec == null) {
      return null;
    }
    return localECGenParameterSpec.getName();
  }
  
  private ECUtil() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\ECUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */