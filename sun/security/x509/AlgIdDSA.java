package sun.security.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.ProviderException;
import java.security.interfaces.DSAParams;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public final class AlgIdDSA
  extends AlgorithmId
  implements DSAParams
{
  private static final long serialVersionUID = 3437177836797504046L;
  private BigInteger p;
  private BigInteger q;
  private BigInteger g;
  
  public BigInteger getP()
  {
    return p;
  }
  
  public BigInteger getQ()
  {
    return q;
  }
  
  public BigInteger getG()
  {
    return g;
  }
  
  @Deprecated
  public AlgIdDSA() {}
  
  AlgIdDSA(DerValue paramDerValue)
    throws IOException
  {
    super(paramDerValue.getOID());
  }
  
  public AlgIdDSA(byte[] paramArrayOfByte)
    throws IOException
  {
    super(new DerValue(paramArrayOfByte).getOID());
  }
  
  public AlgIdDSA(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
    throws IOException
  {
    this(new BigInteger(1, paramArrayOfByte1), new BigInteger(1, paramArrayOfByte2), new BigInteger(1, paramArrayOfByte3));
  }
  
  public AlgIdDSA(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3)
  {
    super(DSA_oid);
    if ((paramBigInteger1 != null) || (paramBigInteger2 != null) || (paramBigInteger3 != null))
    {
      if ((paramBigInteger1 == null) || (paramBigInteger2 == null) || (paramBigInteger3 == null)) {
        throw new ProviderException("Invalid parameters for DSS/DSA Algorithm ID");
      }
      try
      {
        p = paramBigInteger1;
        q = paramBigInteger2;
        g = paramBigInteger3;
        initializeParams();
      }
      catch (IOException localIOException)
      {
        throw new ProviderException("Construct DSS/DSA Algorithm ID");
      }
    }
  }
  
  public String getName()
  {
    return "DSA";
  }
  
  private void initializeParams()
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putInteger(p);
    localDerOutputStream.putInteger(q);
    localDerOutputStream.putInteger(g);
    params = new DerValue((byte)48, localDerOutputStream.toByteArray());
  }
  
  protected void decodeParams()
    throws IOException
  {
    if (params == null) {
      throw new IOException("DSA alg params are null");
    }
    if (params.tag != 48) {
      throw new IOException("DSA alg parsing error");
    }
    params.data.reset();
    p = params.data.getBigInteger();
    q = params.data.getBigInteger();
    g = params.data.getBigInteger();
    if (params.data.available() != 0) {
      throw new IOException("AlgIdDSA params, extra=" + params.data.available());
    }
  }
  
  public String toString()
  {
    return paramsToString();
  }
  
  protected String paramsToString()
  {
    if (params == null) {
      return " null\n";
    }
    return "\n    p:\n" + Debug.toHexString(p) + "\n    q:\n" + Debug.toHexString(q) + "\n    g:\n" + Debug.toHexString(g) + "\n";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\AlgIdDSA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */