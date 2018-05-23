package sun.net.idn;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import sun.text.normalizer.ICUBinary;
import sun.text.normalizer.ICUBinary.Authenticate;

final class StringPrepDataReader
  implements ICUBinary.Authenticate
{
  private DataInputStream dataInputStream;
  private byte[] unicodeVersion = ICUBinary.readHeader(paramInputStream, DATA_FORMAT_ID, this);
  private static final byte[] DATA_FORMAT_ID = { 83, 80, 82, 80 };
  private static final byte[] DATA_FORMAT_VERSION = { 3, 2, 5, 2 };
  
  public StringPrepDataReader(InputStream paramInputStream)
    throws IOException
  {
    dataInputStream = new DataInputStream(paramInputStream);
  }
  
  public void read(byte[] paramArrayOfByte, char[] paramArrayOfChar)
    throws IOException
  {
    dataInputStream.read(paramArrayOfByte);
    for (int i = 0; i < paramArrayOfChar.length; i++) {
      paramArrayOfChar[i] = dataInputStream.readChar();
    }
  }
  
  public byte[] getDataFormatVersion()
  {
    return DATA_FORMAT_VERSION;
  }
  
  public boolean isDataVersionAcceptable(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[0] == DATA_FORMAT_VERSION[0]) && (paramArrayOfByte[2] == DATA_FORMAT_VERSION[2]) && (paramArrayOfByte[3] == DATA_FORMAT_VERSION[3]);
  }
  
  public int[] readIndexes(int paramInt)
    throws IOException
  {
    int[] arrayOfInt = new int[paramInt];
    for (int i = 0; i < paramInt; i++) {
      arrayOfInt[i] = dataInputStream.readInt();
    }
    return arrayOfInt;
  }
  
  public byte[] getUnicodeVersion()
  {
    return unicodeVersion;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\idn\StringPrepDataReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */