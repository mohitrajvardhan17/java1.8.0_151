package sun.security.provider;

import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.ProviderException;

public final class MD4
  extends DigestBase
{
  private int[] state = new int[4];
  private int[] x = new int[16];
  private static final int S11 = 3;
  private static final int S12 = 7;
  private static final int S13 = 11;
  private static final int S14 = 19;
  private static final int S21 = 3;
  private static final int S22 = 5;
  private static final int S23 = 9;
  private static final int S24 = 13;
  private static final int S31 = 3;
  private static final int S32 = 9;
  private static final int S33 = 11;
  private static final int S34 = 15;
  private static final Provider md4Provider = new Provider("MD4Provider", 1.8D, "MD4 MessageDigest")
  {
    private static final long serialVersionUID = -8850464997518327965L;
  };
  
  public static MessageDigest getInstance()
  {
    try
    {
      return MessageDigest.getInstance("MD4", md4Provider);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new ProviderException(localNoSuchAlgorithmException);
    }
  }
  
  public MD4()
  {
    super("MD4", 16, 64);
    implReset();
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    MD4 localMD4 = (MD4)super.clone();
    state = ((int[])state.clone());
    x = new int[16];
    return localMD4;
  }
  
  void implReset()
  {
    state[0] = 1732584193;
    state[1] = -271733879;
    state[2] = -1732584194;
    state[3] = 271733878;
  }
  
  void implDigest(byte[] paramArrayOfByte, int paramInt)
  {
    long l = bytesProcessed << 3;
    int i = (int)bytesProcessed & 0x3F;
    int j = i < 56 ? 56 - i : 120 - i;
    engineUpdate(padding, 0, j);
    ByteArrayAccess.i2bLittle4((int)l, buffer, 56);
    ByteArrayAccess.i2bLittle4((int)(l >>> 32), buffer, 60);
    implCompress(buffer, 0);
    ByteArrayAccess.i2bLittle(state, 0, paramArrayOfByte, paramInt, 16);
  }
  
  private static int FF(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    paramInt1 += (paramInt2 & paramInt3 | (paramInt2 ^ 0xFFFFFFFF) & paramInt4) + paramInt5;
    return paramInt1 << paramInt6 | paramInt1 >>> 32 - paramInt6;
  }
  
  private static int GG(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    paramInt1 += (paramInt2 & paramInt3 | paramInt2 & paramInt4 | paramInt3 & paramInt4) + paramInt5 + 1518500249;
    return paramInt1 << paramInt6 | paramInt1 >>> 32 - paramInt6;
  }
  
  private static int HH(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    paramInt1 += (paramInt2 ^ paramInt3 ^ paramInt4) + paramInt5 + 1859775393;
    return paramInt1 << paramInt6 | paramInt1 >>> 32 - paramInt6;
  }
  
  void implCompress(byte[] paramArrayOfByte, int paramInt)
  {
    ByteArrayAccess.b2iLittle64(paramArrayOfByte, paramInt, x);
    int i = state[0];
    int j = state[1];
    int k = state[2];
    int m = state[3];
    i = FF(i, j, k, m, x[0], 3);
    m = FF(m, i, j, k, x[1], 7);
    k = FF(k, m, i, j, x[2], 11);
    j = FF(j, k, m, i, x[3], 19);
    i = FF(i, j, k, m, x[4], 3);
    m = FF(m, i, j, k, x[5], 7);
    k = FF(k, m, i, j, x[6], 11);
    j = FF(j, k, m, i, x[7], 19);
    i = FF(i, j, k, m, x[8], 3);
    m = FF(m, i, j, k, x[9], 7);
    k = FF(k, m, i, j, x[10], 11);
    j = FF(j, k, m, i, x[11], 19);
    i = FF(i, j, k, m, x[12], 3);
    m = FF(m, i, j, k, x[13], 7);
    k = FF(k, m, i, j, x[14], 11);
    j = FF(j, k, m, i, x[15], 19);
    i = GG(i, j, k, m, x[0], 3);
    m = GG(m, i, j, k, x[4], 5);
    k = GG(k, m, i, j, x[8], 9);
    j = GG(j, k, m, i, x[12], 13);
    i = GG(i, j, k, m, x[1], 3);
    m = GG(m, i, j, k, x[5], 5);
    k = GG(k, m, i, j, x[9], 9);
    j = GG(j, k, m, i, x[13], 13);
    i = GG(i, j, k, m, x[2], 3);
    m = GG(m, i, j, k, x[6], 5);
    k = GG(k, m, i, j, x[10], 9);
    j = GG(j, k, m, i, x[14], 13);
    i = GG(i, j, k, m, x[3], 3);
    m = GG(m, i, j, k, x[7], 5);
    k = GG(k, m, i, j, x[11], 9);
    j = GG(j, k, m, i, x[15], 13);
    i = HH(i, j, k, m, x[0], 3);
    m = HH(m, i, j, k, x[8], 9);
    k = HH(k, m, i, j, x[4], 11);
    j = HH(j, k, m, i, x[12], 15);
    i = HH(i, j, k, m, x[2], 3);
    m = HH(m, i, j, k, x[10], 9);
    k = HH(k, m, i, j, x[6], 11);
    j = HH(j, k, m, i, x[14], 15);
    i = HH(i, j, k, m, x[1], 3);
    m = HH(m, i, j, k, x[9], 9);
    k = HH(k, m, i, j, x[5], 11);
    j = HH(j, k, m, i, x[13], 15);
    i = HH(i, j, k, m, x[3], 3);
    m = HH(m, i, j, k, x[11], 9);
    k = HH(k, m, i, j, x[7], 11);
    j = HH(j, k, m, i, x[15], 15);
    state[0] += i;
    state[1] += j;
    state[2] += k;
    state[3] += m;
  }
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        MD4.md4Provider.put("MessageDigest.MD4", "sun.security.provider.MD4");
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\MD4.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */