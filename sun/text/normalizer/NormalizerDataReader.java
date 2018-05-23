package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class NormalizerDataReader
  implements ICUBinary.Authenticate
{
  private DataInputStream dataInputStream;
  private byte[] unicodeVersion = ICUBinary.readHeader(paramInputStream, DATA_FORMAT_ID, this);
  private static final byte[] DATA_FORMAT_ID = { 78, 111, 114, 109 };
  private static final byte[] DATA_FORMAT_VERSION = { 2, 2, 5, 2 };
  
  protected NormalizerDataReader(InputStream paramInputStream)
    throws IOException
  {
    dataInputStream = new DataInputStream(paramInputStream);
  }
  
  protected int[] readIndexes(int paramInt)
    throws IOException
  {
    int[] arrayOfInt = new int[paramInt];
    for (int i = 0; i < paramInt; i++) {
      arrayOfInt[i] = dataInputStream.readInt();
    }
    return arrayOfInt;
  }
  
  protected void read(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, char[] paramArrayOfChar1, char[] paramArrayOfChar2)
    throws IOException
  {
    dataInputStream.readFully(paramArrayOfByte1);
    for (int i = 0; i < paramArrayOfChar1.length; i++) {
      paramArrayOfChar1[i] = dataInputStream.readChar();
    }
    for (i = 0; i < paramArrayOfChar2.length; i++) {
      paramArrayOfChar2[i] = dataInputStream.readChar();
    }
    dataInputStream.readFully(paramArrayOfByte2);
    dataInputStream.readFully(paramArrayOfByte3);
  }
  
  public byte[] getDataFormatVersion()
  {
    return DATA_FORMAT_VERSION;
  }
  
  public boolean isDataVersionAcceptable(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[0] == DATA_FORMAT_VERSION[0]) && (paramArrayOfByte[2] == DATA_FORMAT_VERSION[2]) && (paramArrayOfByte[3] == DATA_FORMAT_VERSION[3]);
  }
  
  public byte[] getUnicodeVersion()
  {
    return unicodeVersion;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\NormalizerDataReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */