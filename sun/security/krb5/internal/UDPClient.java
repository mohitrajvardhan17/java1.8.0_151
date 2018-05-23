package sun.security.krb5.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.UnknownHostException;

class UDPClient
  extends NetClient
{
  InetAddress iaddr;
  int iport;
  int bufSize = 65507;
  DatagramSocket dgSocket;
  DatagramPacket dgPacketIn;
  
  UDPClient(String paramString, int paramInt1, int paramInt2)
    throws UnknownHostException, SocketException
  {
    iaddr = InetAddress.getByName(paramString);
    iport = paramInt1;
    dgSocket = new DatagramSocket();
    dgSocket.setSoTimeout(paramInt2);
    dgSocket.connect(iaddr, iport);
  }
  
  public void send(byte[] paramArrayOfByte)
    throws IOException
  {
    DatagramPacket localDatagramPacket = new DatagramPacket(paramArrayOfByte, paramArrayOfByte.length, iaddr, iport);
    dgSocket.send(localDatagramPacket);
  }
  
  public byte[] receive()
    throws IOException
  {
    byte[] arrayOfByte1 = new byte[bufSize];
    dgPacketIn = new DatagramPacket(arrayOfByte1, arrayOfByte1.length);
    try
    {
      dgSocket.receive(dgPacketIn);
    }
    catch (SocketException localSocketException)
    {
      if ((localSocketException instanceof PortUnreachableException)) {
        throw localSocketException;
      }
      dgSocket.receive(dgPacketIn);
    }
    byte[] arrayOfByte2 = new byte[dgPacketIn.getLength()];
    System.arraycopy(dgPacketIn.getData(), 0, arrayOfByte2, 0, dgPacketIn.getLength());
    return arrayOfByte2;
  }
  
  public void close()
  {
    dgSocket.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\UDPClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */