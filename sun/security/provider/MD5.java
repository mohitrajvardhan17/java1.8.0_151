package sun.security.provider;

public final class MD5
  extends DigestBase
{
  private int[] state = new int[4];
  private int[] x = new int[16];
  private static final int S11 = 7;
  private static final int S12 = 12;
  private static final int S13 = 17;
  private static final int S14 = 22;
  private static final int S21 = 5;
  private static final int S22 = 9;
  private static final int S23 = 14;
  private static final int S24 = 20;
  private static final int S31 = 4;
  private static final int S32 = 11;
  private static final int S33 = 16;
  private static final int S34 = 23;
  private static final int S41 = 6;
  private static final int S42 = 10;
  private static final int S43 = 15;
  private static final int S44 = 21;
  
  public MD5()
  {
    super("MD5", 16, 64);
    implReset();
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    MD5 localMD5 = (MD5)super.clone();
    state = ((int[])state.clone());
    x = new int[16];
    return localMD5;
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
  
  private static int FF(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    paramInt1 += (paramInt2 & paramInt3 | (paramInt2 ^ 0xFFFFFFFF) & paramInt4) + paramInt5 + paramInt7;
    return (paramInt1 << paramInt6 | paramInt1 >>> 32 - paramInt6) + paramInt2;
  }
  
  private static int GG(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    paramInt1 += (paramInt2 & paramInt4 | paramInt3 & (paramInt4 ^ 0xFFFFFFFF)) + paramInt5 + paramInt7;
    return (paramInt1 << paramInt6 | paramInt1 >>> 32 - paramInt6) + paramInt2;
  }
  
  private static int HH(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    paramInt1 += (paramInt2 ^ paramInt3 ^ paramInt4) + paramInt5 + paramInt7;
    return (paramInt1 << paramInt6 | paramInt1 >>> 32 - paramInt6) + paramInt2;
  }
  
  private static int II(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    paramInt1 += (paramInt3 ^ (paramInt2 | paramInt4 ^ 0xFFFFFFFF)) + paramInt5 + paramInt7;
    return (paramInt1 << paramInt6 | paramInt1 >>> 32 - paramInt6) + paramInt2;
  }
  
  void implCompress(byte[] paramArrayOfByte, int paramInt)
  {
    ByteArrayAccess.b2iLittle64(paramArrayOfByte, paramInt, x);
    int i = state[0];
    int j = state[1];
    int k = state[2];
    int m = state[3];
    i = FF(i, j, k, m, x[0], 7, -680876936);
    m = FF(m, i, j, k, x[1], 12, -389564586);
    k = FF(k, m, i, j, x[2], 17, 606105819);
    j = FF(j, k, m, i, x[3], 22, -1044525330);
    i = FF(i, j, k, m, x[4], 7, -176418897);
    m = FF(m, i, j, k, x[5], 12, 1200080426);
    k = FF(k, m, i, j, x[6], 17, -1473231341);
    j = FF(j, k, m, i, x[7], 22, -45705983);
    i = FF(i, j, k, m, x[8], 7, 1770035416);
    m = FF(m, i, j, k, x[9], 12, -1958414417);
    k = FF(k, m, i, j, x[10], 17, -42063);
    j = FF(j, k, m, i, x[11], 22, -1990404162);
    i = FF(i, j, k, m, x[12], 7, 1804603682);
    m = FF(m, i, j, k, x[13], 12, -40341101);
    k = FF(k, m, i, j, x[14], 17, -1502002290);
    j = FF(j, k, m, i, x[15], 22, 1236535329);
    i = GG(i, j, k, m, x[1], 5, -165796510);
    m = GG(m, i, j, k, x[6], 9, -1069501632);
    k = GG(k, m, i, j, x[11], 14, 643717713);
    j = GG(j, k, m, i, x[0], 20, -373897302);
    i = GG(i, j, k, m, x[5], 5, -701558691);
    m = GG(m, i, j, k, x[10], 9, 38016083);
    k = GG(k, m, i, j, x[15], 14, -660478335);
    j = GG(j, k, m, i, x[4], 20, -405537848);
    i = GG(i, j, k, m, x[9], 5, 568446438);
    m = GG(m, i, j, k, x[14], 9, -1019803690);
    k = GG(k, m, i, j, x[3], 14, -187363961);
    j = GG(j, k, m, i, x[8], 20, 1163531501);
    i = GG(i, j, k, m, x[13], 5, -1444681467);
    m = GG(m, i, j, k, x[2], 9, -51403784);
    k = GG(k, m, i, j, x[7], 14, 1735328473);
    j = GG(j, k, m, i, x[12], 20, -1926607734);
    i = HH(i, j, k, m, x[5], 4, -378558);
    m = HH(m, i, j, k, x[8], 11, -2022574463);
    k = HH(k, m, i, j, x[11], 16, 1839030562);
    j = HH(j, k, m, i, x[14], 23, -35309556);
    i = HH(i, j, k, m, x[1], 4, -1530992060);
    m = HH(m, i, j, k, x[4], 11, 1272893353);
    k = HH(k, m, i, j, x[7], 16, -155497632);
    j = HH(j, k, m, i, x[10], 23, -1094730640);
    i = HH(i, j, k, m, x[13], 4, 681279174);
    m = HH(m, i, j, k, x[0], 11, -358537222);
    k = HH(k, m, i, j, x[3], 16, -722521979);
    j = HH(j, k, m, i, x[6], 23, 76029189);
    i = HH(i, j, k, m, x[9], 4, -640364487);
    m = HH(m, i, j, k, x[12], 11, -421815835);
    k = HH(k, m, i, j, x[15], 16, 530742520);
    j = HH(j, k, m, i, x[2], 23, -995338651);
    i = II(i, j, k, m, x[0], 6, -198630844);
    m = II(m, i, j, k, x[7], 10, 1126891415);
    k = II(k, m, i, j, x[14], 15, -1416354905);
    j = II(j, k, m, i, x[5], 21, -57434055);
    i = II(i, j, k, m, x[12], 6, 1700485571);
    m = II(m, i, j, k, x[3], 10, -1894986606);
    k = II(k, m, i, j, x[10], 15, -1051523);
    j = II(j, k, m, i, x[1], 21, -2054922799);
    i = II(i, j, k, m, x[8], 6, 1873313359);
    m = II(m, i, j, k, x[15], 10, -30611744);
    k = II(k, m, i, j, x[6], 15, -1560198380);
    j = II(j, k, m, i, x[13], 21, 1309151649);
    i = II(i, j, k, m, x[4], 6, -145523070);
    m = II(m, i, j, k, x[11], 10, -1120210379);
    k = II(k, m, i, j, x[2], 15, 718787259);
    j = II(j, k, m, i, x[9], 21, -343485551);
    state[0] += i;
    state[1] += j;
    state[2] += k;
    state[3] += m;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\MD5.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */