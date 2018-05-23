package sun.net;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TelnetInputStream
  extends FilterInputStream
{
  boolean stickyCRLF = false;
  boolean seenCR = false;
  public boolean binaryMode = false;
  
  public TelnetInputStream(InputStream paramInputStream, boolean paramBoolean)
  {
    super(paramInputStream);
    binaryMode = paramBoolean;
  }
  
  public void setStickyCRLF(boolean paramBoolean)
  {
    stickyCRLF = paramBoolean;
  }
  
  public int read()
    throws IOException
  {
    if (binaryMode) {
      return super.read();
    }
    if (seenCR)
    {
      seenCR = false;
      return 10;
    }
    int i;
    if ((i = super.read()) == 13)
    {
      switch (i = super.read())
      {
      case -1: 
      default: 
        throw new TelnetProtocolException("misplaced CR in input");
      case 0: 
        return 13;
      }
      if (stickyCRLF)
      {
        seenCR = true;
        return 13;
      }
      return 10;
    }
    return i;
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (binaryMode) {
      return super.read(paramArrayOfByte, paramInt1, paramInt2);
    }
    int j = paramInt1;
    for (;;)
    {
      paramInt2--;
      if (paramInt2 < 0) {
        break;
      }
      int i = read();
      if (i == -1) {
        break;
      }
      paramArrayOfByte[(paramInt1++)] = ((byte)i);
    }
    return paramInt1 > j ? paramInt1 - j : -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\TelnetInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */