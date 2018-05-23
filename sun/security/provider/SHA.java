package sun.security.provider;

public final class SHA
  extends DigestBase
{
  private int[] W = new int[80];
  private int[] state = new int[5];
  private static final int round1_kt = 1518500249;
  private static final int round2_kt = 1859775393;
  private static final int round3_kt = -1894007588;
  private static final int round4_kt = -899497514;
  
  public SHA()
  {
    super("SHA-1", 20, 64);
    implReset();
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    SHA localSHA = (SHA)super.clone();
    state = ((int[])state.clone());
    W = new int[80];
    return localSHA;
  }
  
  void implReset()
  {
    state[0] = 1732584193;
    state[1] = -271733879;
    state[2] = -1732584194;
    state[3] = 271733878;
    state[4] = -1009589776;
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
    ByteArrayAccess.i2bBig(state, 0, paramArrayOfByte, paramInt, 20);
  }
  
  void implCompress(byte[] paramArrayOfByte, int paramInt)
  {
    ByteArrayAccess.b2iBig64(paramArrayOfByte, paramInt, W);
    for (int i = 16; i <= 79; i++)
    {
      j = W[(i - 3)] ^ W[(i - 8)] ^ W[(i - 14)] ^ W[(i - 16)];
      W[i] = (j << 1 | j >>> 31);
    }
    i = state[0];
    int j = state[1];
    int k = state[2];
    int m = state[3];
    int n = state[4];
    int i2;
    for (int i1 = 0; i1 < 20; i1++)
    {
      i2 = (i << 5 | i >>> 27) + (j & k | (j ^ 0xFFFFFFFF) & m) + n + W[i1] + 1518500249;
      n = m;
      m = k;
      k = j << 30 | j >>> 2;
      j = i;
      i = i2;
    }
    for (i1 = 20; i1 < 40; i1++)
    {
      i2 = (i << 5 | i >>> 27) + (j ^ k ^ m) + n + W[i1] + 1859775393;
      n = m;
      m = k;
      k = j << 30 | j >>> 2;
      j = i;
      i = i2;
    }
    for (i1 = 40; i1 < 60; i1++)
    {
      i2 = (i << 5 | i >>> 27) + (j & k | j & m | k & m) + n + W[i1] + -1894007588;
      n = m;
      m = k;
      k = j << 30 | j >>> 2;
      j = i;
      i = i2;
    }
    for (i1 = 60; i1 < 80; i1++)
    {
      i2 = (i << 5 | i >>> 27) + (j ^ k ^ m) + n + W[i1] + -899497514;
      n = m;
      m = k;
      k = j << 30 | j >>> 2;
      j = i;
      i = i2;
    }
    state[0] += i;
    state[1] += j;
    state[2] += k;
    state[3] += m;
    state[4] += n;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\SHA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */