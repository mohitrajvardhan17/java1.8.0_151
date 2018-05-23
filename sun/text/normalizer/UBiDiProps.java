package sun.text.normalizer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class UBiDiProps
{
  private static UBiDiProps gBdp = null;
  private static UBiDiProps gBdpDummy = null;
  private int[] indexes;
  private int[] mirrors;
  private byte[] jgArray;
  private CharTrie trie;
  private static final String DATA_FILE_NAME = "/sun/text/resources/ubidi.icu";
  private static final byte[] FMT = { 66, 105, 68, 105 };
  private static final int IX_INDEX_TOP = 0;
  private static final int IX_MIRROR_LENGTH = 3;
  private static final int IX_JG_START = 4;
  private static final int IX_JG_LIMIT = 5;
  private static final int IX_TOP = 16;
  private static final int CLASS_MASK = 31;
  
  public UBiDiProps()
    throws IOException
  {
    InputStream localInputStream = ICUData.getStream("/sun/text/resources/ubidi.icu");
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(localInputStream, 4096);
    readData(localBufferedInputStream);
    localBufferedInputStream.close();
    localInputStream.close();
  }
  
  private void readData(InputStream paramInputStream)
    throws IOException
  {
    DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
    ICUBinary.readHeader(localDataInputStream, FMT, new IsAcceptable(null));
    int j = localDataInputStream.readInt();
    if (j < 0) {
      throw new IOException("indexes[0] too small in /sun/text/resources/ubidi.icu");
    }
    indexes = new int[j];
    indexes[0] = j;
    for (int i = 1; i < j; i++) {
      indexes[i] = localDataInputStream.readInt();
    }
    trie = new CharTrie(localDataInputStream, null);
    j = indexes[3];
    if (j > 0)
    {
      mirrors = new int[j];
      for (i = 0; i < j; i++) {
        mirrors[i] = localDataInputStream.readInt();
      }
    }
    j = indexes[5] - indexes[4];
    jgArray = new byte[j];
    for (i = 0; i < j; i++) {
      jgArray[i] = localDataInputStream.readByte();
    }
  }
  
  public static final synchronized UBiDiProps getSingleton()
    throws IOException
  {
    if (gBdp == null) {
      gBdp = new UBiDiProps();
    }
    return gBdp;
  }
  
  private UBiDiProps(boolean paramBoolean)
  {
    indexes = new int[16];
    indexes[0] = 16;
    trie = new CharTrie(0, 0, null);
  }
  
  public static final synchronized UBiDiProps getDummy()
  {
    if (gBdpDummy == null) {
      gBdpDummy = new UBiDiProps(true);
    }
    return gBdpDummy;
  }
  
  public final int getClass(int paramInt)
  {
    return getClassFromProps(trie.getCodePointValue(paramInt));
  }
  
  private static final int getClassFromProps(int paramInt)
  {
    return paramInt & 0x1F;
  }
  
  private final class IsAcceptable
    implements ICUBinary.Authenticate
  {
    private IsAcceptable() {}
    
    public boolean isDataVersionAcceptable(byte[] paramArrayOfByte)
    {
      return (paramArrayOfByte[0] == 1) && (paramArrayOfByte[2] == 5) && (paramArrayOfByte[3] == 2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\UBiDiProps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */