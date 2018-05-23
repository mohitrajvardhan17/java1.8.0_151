package sun.security.krb5.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import sun.misc.IOUtils;

class TCPClient
  extends NetClient
{
  private Socket tcpSocket = new Socket();
  private BufferedOutputStream out;
  private BufferedInputStream in;
  
  TCPClient(String paramString, int paramInt1, int paramInt2)
    throws IOException
  {
    tcpSocket.connect(new InetSocketAddress(paramString, paramInt1), paramInt2);
    out = new BufferedOutputStream(tcpSocket.getOutputStream());
    in = new BufferedInputStream(tcpSocket.getInputStream());
    tcpSocket.setSoTimeout(paramInt2);
  }
  
  public void send(byte[] paramArrayOfByte)
    throws IOException
  {
    byte[] arrayOfByte = new byte[4];
    intToNetworkByteOrder(paramArrayOfByte.length, arrayOfByte, 0, 4);
    out.write(arrayOfByte);
    out.write(paramArrayOfByte);
    out.flush();
  }
  
  public byte[] receive()
    throws IOException
  {
    byte[] arrayOfByte = new byte[4];
    int i = readFully(arrayOfByte, 4);
    if (i != 4)
    {
      if (Krb5.DEBUG) {
        System.out.println(">>>DEBUG: TCPClient could not read length field");
      }
      return null;
    }
    int j = networkByteOrderToInt(arrayOfByte, 0, 4);
    if (Krb5.DEBUG) {
      System.out.println(">>>DEBUG: TCPClient reading " + j + " bytes");
    }
    if (j <= 0)
    {
      if (Krb5.DEBUG) {
        System.out.println(">>>DEBUG: TCPClient zero or negative length field: " + j);
      }
      return null;
    }
    try
    {
      return IOUtils.readFully(in, j, true);
    }
    catch (IOException localIOException)
    {
      if (Krb5.DEBUG) {
        System.out.println(">>>DEBUG: TCPClient could not read complete packet (" + j + "/" + i + ")");
      }
    }
    return null;
  }
  
  public void close()
    throws IOException
  {
    tcpSocket.close();
  }
  
  private int readFully(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    int j = 0;
    while (paramInt > 0)
    {
      int i = in.read(paramArrayOfByte, j, paramInt);
      if (i == -1) {
        return j == 0 ? -1 : j;
      }
      j += i;
      paramInt -= i;
    }
    return j;
  }
  
  private static int networkByteOrderToInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 > 4) {
      throw new IllegalArgumentException("Cannot handle more than 4 bytes");
    }
    int i = 0;
    for (int j = 0; j < paramInt2; j++)
    {
      i <<= 8;
      i |= paramArrayOfByte[(paramInt1 + j)] & 0xFF;
    }
    return i;
  }
  
  private static void intToNetworkByteOrder(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    if (paramInt3 > 4) {
      throw new IllegalArgumentException("Cannot handle more than 4 bytes");
    }
    for (int i = paramInt3 - 1; i >= 0; i--)
    {
      paramArrayOfByte[(paramInt2 + i)] = ((byte)(paramInt1 & 0xFF));
      paramInt1 >>>= 8;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\TCPClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */