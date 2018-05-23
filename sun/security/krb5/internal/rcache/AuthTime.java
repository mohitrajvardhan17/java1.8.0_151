package sun.security.krb5.internal.rcache;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

public class AuthTime
{
  final int ctime;
  final int cusec;
  final String client;
  final String server;
  
  public AuthTime(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    ctime = paramInt1;
    cusec = paramInt2;
    client = paramString1;
    server = paramString2;
  }
  
  public String toString()
  {
    return String.format("%d/%06d/----/%s", new Object[] { Integer.valueOf(ctime), Integer.valueOf(cusec), client });
  }
  
  private static String readStringWithLength(SeekableByteChannel paramSeekableByteChannel)
    throws IOException
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(4);
    localByteBuffer.order(ByteOrder.nativeOrder());
    paramSeekableByteChannel.read(localByteBuffer);
    localByteBuffer.flip();
    int i = localByteBuffer.getInt();
    if (i > 1024) {
      throw new IOException("Invalid string length");
    }
    localByteBuffer = ByteBuffer.allocate(i);
    if (paramSeekableByteChannel.read(localByteBuffer) != i) {
      throw new IOException("Not enough string");
    }
    byte[] arrayOfByte = localByteBuffer.array();
    return arrayOfByte[(i - 1)] == 0 ? new String(arrayOfByte, 0, i - 1, StandardCharsets.UTF_8) : new String(arrayOfByte, StandardCharsets.UTF_8);
  }
  
  public static AuthTime readFrom(SeekableByteChannel paramSeekableByteChannel)
    throws IOException
  {
    String str1 = readStringWithLength(paramSeekableByteChannel);
    String str2 = readStringWithLength(paramSeekableByteChannel);
    ByteBuffer localByteBuffer = ByteBuffer.allocate(8);
    paramSeekableByteChannel.read(localByteBuffer);
    localByteBuffer.order(ByteOrder.nativeOrder());
    int i = localByteBuffer.getInt(0);
    int j = localByteBuffer.getInt(4);
    if (str1.isEmpty())
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(str2, " :");
      if (localStringTokenizer.countTokens() != 6) {
        throw new IOException("Incorrect rcache style");
      }
      localStringTokenizer.nextToken();
      String str3 = localStringTokenizer.nextToken();
      localStringTokenizer.nextToken();
      str1 = localStringTokenizer.nextToken();
      localStringTokenizer.nextToken();
      str2 = localStringTokenizer.nextToken();
      return new AuthTimeWithHash(str1, str2, j, i, str3);
    }
    return new AuthTime(str1, str2, j, i);
  }
  
  protected byte[] encode0(String paramString1, String paramString2)
  {
    byte[] arrayOfByte1 = paramString1.getBytes(StandardCharsets.UTF_8);
    byte[] arrayOfByte2 = paramString2.getBytes(StandardCharsets.UTF_8);
    byte[] arrayOfByte3 = new byte[1];
    int i = 4 + arrayOfByte1.length + 1 + 4 + arrayOfByte2.length + 1 + 4 + 4;
    ByteBuffer localByteBuffer = ByteBuffer.allocate(i).order(ByteOrder.nativeOrder());
    localByteBuffer.putInt(arrayOfByte1.length + 1).put(arrayOfByte1).put(arrayOfByte3).putInt(arrayOfByte2.length + 1).put(arrayOfByte2).put(arrayOfByte3).putInt(cusec).putInt(ctime);
    return localByteBuffer.array();
  }
  
  public byte[] encode(boolean paramBoolean)
  {
    return encode0(client, server);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\rcache\AuthTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */