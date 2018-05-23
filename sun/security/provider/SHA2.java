package sun.security.provider;

abstract class SHA2
  extends DigestBase
{
  private static final int ITERATION = 64;
  private static final int[] ROUND_CONSTS = { 1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998 };
  private int[] W;
  private int[] state;
  private final int[] initialHashes;
  
  SHA2(String paramString, int paramInt, int[] paramArrayOfInt)
  {
    super(paramString, paramInt, 64);
    initialHashes = paramArrayOfInt;
    state = new int[8];
    W = new int[64];
    implReset();
  }
  
  void implReset()
  {
    System.arraycopy(initialHashes, 0, state, 0, state.length);
  }
  
  void implDigest(byte[] paramArrayOfByte, int paramInt)
  {
    long l = bytesProcessed << 3;
    int i = (int)bytesProcessed & 0x3F;
    int j = i < 56 ? 56 - i : 120 - i;
    engineUpdate(padding, 0, j);
    ByteArrayAccess.i2bBig4((int)(l >>> 32), buffer, 56);
    ByteArrayAccess.i2bBig4((int)l, buffer, 60);
    implCompress(buffer, 0);
    ByteArrayAccess.i2bBig(state, 0, paramArrayOfByte, paramInt, engineGetDigestLength());
  }
  
  private static int lf_ch(int paramInt1, int paramInt2, int paramInt3)
  {
    return paramInt1 & paramInt2 ^ (paramInt1 ^ 0xFFFFFFFF) & paramInt3;
  }
  
  private static int lf_maj(int paramInt1, int paramInt2, int paramInt3)
  {
    return paramInt1 & paramInt2 ^ paramInt1 & paramInt3 ^ paramInt2 & paramInt3;
  }
  
  private static int lf_R(int paramInt1, int paramInt2)
  {
    return paramInt1 >>> paramInt2;
  }
  
  private static int lf_S(int paramInt1, int paramInt2)
  {
    return paramInt1 >>> paramInt2 | paramInt1 << 32 - paramInt2;
  }
  
  private static int lf_sigma0(int paramInt)
  {
    return lf_S(paramInt, 2) ^ lf_S(paramInt, 13) ^ lf_S(paramInt, 22);
  }
  
  private static int lf_sigma1(int paramInt)
  {
    return lf_S(paramInt, 6) ^ lf_S(paramInt, 11) ^ lf_S(paramInt, 25);
  }
  
  private static int lf_delta0(int paramInt)
  {
    return lf_S(paramInt, 7) ^ lf_S(paramInt, 18) ^ lf_R(paramInt, 3);
  }
  
  private static int lf_delta1(int paramInt)
  {
    return lf_S(paramInt, 17) ^ lf_S(paramInt, 19) ^ lf_R(paramInt, 10);
  }
  
  void implCompress(byte[] paramArrayOfByte, int paramInt)
  {
    ByteArrayAccess.b2iBig64(paramArrayOfByte, paramInt, W);
    for (int i = 16; i < 64; i++) {
      W[i] = (lf_delta1(W[(i - 2)]) + W[(i - 7)] + lf_delta0(W[(i - 15)]) + W[(i - 16)]);
    }
    i = state[0];
    int j = state[1];
    int k = state[2];
    int m = state[3];
    int n = state[4];
    int i1 = state[5];
    int i2 = state[6];
    int i3 = state[7];
    for (int i4 = 0; i4 < 64; i4++)
    {
      int i5 = i3 + lf_sigma1(n) + lf_ch(n, i1, i2) + ROUND_CONSTS[i4] + W[i4];
      int i6 = lf_sigma0(i) + lf_maj(i, j, k);
      i3 = i2;
      i2 = i1;
      i1 = n;
      n = m + i5;
      m = k;
      k = j;
      j = i;
      i = i5 + i6;
    }
    state[0] += i;
    state[1] += j;
    state[2] += k;
    state[3] += m;
    state[4] += n;
    state[5] += i1;
    state[6] += i2;
    state[7] += i3;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    SHA2 localSHA2 = (SHA2)super.clone();
    state = ((int[])state.clone());
    W = new int[64];
    return localSHA2;
  }
  
  public static final class SHA224
    extends SHA2
  {
    private static final int[] INITIAL_HASHES = { -1056596264, 914150663, 812702999, -150054599, -4191439, 1750603025, 1694076839, -1090891868 };
    
    public SHA224()
    {
      super(28, INITIAL_HASHES);
    }
  }
  
  public static final class SHA256
    extends SHA2
  {
    private static final int[] INITIAL_HASHES = { 1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225 };
    
    public SHA256()
    {
      super(32, INITIAL_HASHES);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\SHA2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */