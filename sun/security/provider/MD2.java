package sun.security.provider;

import java.util.Arrays;

public final class MD2
  extends DigestBase
{
  private int[] X = new int[48];
  private int[] C = new int[16];
  private byte[] cBytes = new byte[16];
  private static final int[] S = { 41, 46, 67, 201, 162, 216, 124, 1, 61, 54, 84, 161, 236, 240, 6, 19, 98, 167, 5, 243, 192, 199, 115, 140, 152, 147, 43, 217, 188, 76, 130, 202, 30, 155, 87, 60, 253, 212, 224, 22, 103, 66, 111, 24, 138, 23, 229, 18, 190, 78, 196, 214, 218, 158, 222, 73, 160, 251, 245, 142, 187, 47, 238, 122, 169, 104, 121, 145, 21, 178, 7, 63, 148, 194, 16, 137, 11, 34, 95, 33, 128, 127, 93, 154, 90, 144, 50, 39, 53, 62, 204, 231, 191, 247, 151, 3, 255, 25, 48, 179, 72, 165, 181, 209, 215, 94, 146, 42, 172, 86, 170, 198, 79, 184, 56, 210, 150, 164, 125, 182, 118, 252, 107, 226, 156, 116, 4, 241, 69, 157, 112, 89, 100, 113, 135, 32, 134, 91, 207, 101, 230, 45, 168, 2, 27, 96, 37, 173, 174, 176, 185, 246, 28, 70, 97, 105, 52, 64, 126, 15, 85, 71, 163, 35, 221, 81, 175, 58, 195, 92, 249, 206, 186, 197, 234, 38, 44, 83, 13, 110, 133, 40, 132, 9, 211, 223, 205, 244, 65, 129, 77, 82, 106, 220, 55, 200, 108, 193, 171, 250, 36, 225, 123, 8, 12, 189, 177, 74, 120, 136, 149, 139, 227, 99, 232, 109, 233, 203, 213, 254, 59, 0, 29, 57, 242, 239, 183, 14, 102, 88, 208, 228, 166, 119, 114, 248, 235, 117, 75, 10, 49, 68, 80, 180, 143, 237, 31, 26, 219, 153, 141, 51, 159, 17, 131, 20 };
  private static final byte[][] PADDING = new byte[17][];
  
  public MD2()
  {
    super("MD2", 16, 16);
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    MD2 localMD2 = (MD2)super.clone();
    X = ((int[])X.clone());
    C = ((int[])C.clone());
    cBytes = new byte[16];
    return localMD2;
  }
  
  void implReset()
  {
    Arrays.fill(X, 0);
    Arrays.fill(C, 0);
  }
  
  void implDigest(byte[] paramArrayOfByte, int paramInt)
  {
    int i = 16 - ((int)bytesProcessed & 0xF);
    engineUpdate(PADDING[i], 0, i);
    for (int j = 0; j < 16; j++) {
      cBytes[j] = ((byte)C[j]);
    }
    implCompress(cBytes, 0);
    for (j = 0; j < 16; j++) {
      paramArrayOfByte[(paramInt + j)] = ((byte)X[j]);
    }
  }
  
  void implCompress(byte[] paramArrayOfByte, int paramInt)
  {
    for (int i = 0; i < 16; i++)
    {
      j = paramArrayOfByte[(paramInt + i)] & 0xFF;
      X[(16 + i)] = j;
      X[(32 + i)] = (j ^ X[i]);
    }
    i = C[15];
    for (int j = 0; j < 16; j++) {
      i = C[j] ^= S[(X[(16 + j)] ^ i)];
    }
    i = 0;
    for (j = 0; j < 18; j++)
    {
      for (int k = 0; k < 48; k++) {
        i = X[k] ^= S[i];
      }
      i = i + j & 0xFF;
    }
  }
  
  static
  {
    for (int i = 1; i < 17; i++)
    {
      byte[] arrayOfByte = new byte[i];
      Arrays.fill(arrayOfByte, (byte)i);
      PADDING[i] = arrayOfByte;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\MD2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */