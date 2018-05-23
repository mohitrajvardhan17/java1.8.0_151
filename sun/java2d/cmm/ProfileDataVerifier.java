package sun.java2d.cmm;

public class ProfileDataVerifier
{
  private static final int MAX_TAG_COUNT = 100;
  private static final int HEADER_SIZE = 128;
  private static final int TOC_OFFSET = 132;
  private static final int TOC_RECORD_SIZE = 12;
  private static final int PROFILE_FILE_SIGNATURE = 1633907568;
  
  public ProfileDataVerifier() {}
  
  public static void verify(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      throw new IllegalArgumentException("Invalid ICC Profile Data");
    }
    if (paramArrayOfByte.length < 132) {
      throw new IllegalArgumentException("Invalid ICC Profile Data");
    }
    int i = readInt32(paramArrayOfByte, 0);
    int j = readInt32(paramArrayOfByte, 128);
    if ((j < 0) || (j > 100)) {
      throw new IllegalArgumentException("Invalid ICC Profile Data");
    }
    if ((i < 132 + j * 12) || (i > paramArrayOfByte.length)) {
      throw new IllegalArgumentException("Invalid ICC Profile Data");
    }
    int k = readInt32(paramArrayOfByte, 36);
    if (1633907568 != k) {
      throw new IllegalArgumentException("Invalid ICC Profile Data");
    }
    for (int m = 0; m < j; m++)
    {
      int n = getTagOffset(m, paramArrayOfByte);
      int i1 = getTagSize(m, paramArrayOfByte);
      if ((n < 132) || (n > i)) {
        throw new IllegalArgumentException("Invalid ICC Profile Data");
      }
      if ((i1 < 0) || (i1 > Integer.MAX_VALUE - n) || (i1 + n > i)) {
        throw new IllegalArgumentException("Invalid ICC Profile Data");
      }
    }
  }
  
  private static int getTagOffset(int paramInt, byte[] paramArrayOfByte)
  {
    int i = 132 + paramInt * 12 + 4;
    return readInt32(paramArrayOfByte, i);
  }
  
  private static int getTagSize(int paramInt, byte[] paramArrayOfByte)
  {
    int i = 132 + paramInt * 12 + 8;
    return readInt32(paramArrayOfByte, i);
  }
  
  private static int readInt32(byte[] paramArrayOfByte, int paramInt)
  {
    int i = 0;
    for (int j = 0; j < 4; j++)
    {
      i <<= 8;
      i |= 0xFF & paramArrayOfByte[(paramInt++)];
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\cmm\ProfileDataVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */