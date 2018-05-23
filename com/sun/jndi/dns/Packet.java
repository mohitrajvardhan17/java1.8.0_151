package com.sun.jndi.dns;

class Packet
{
  byte[] buf;
  
  Packet(int paramInt)
  {
    buf = new byte[paramInt];
  }
  
  Packet(byte[] paramArrayOfByte, int paramInt)
  {
    buf = new byte[paramInt];
    System.arraycopy(paramArrayOfByte, 0, buf, 0, paramInt);
  }
  
  void putInt(int paramInt1, int paramInt2)
  {
    buf[(paramInt2 + 0)] = ((byte)(paramInt1 >> 24));
    buf[(paramInt2 + 1)] = ((byte)(paramInt1 >> 16));
    buf[(paramInt2 + 2)] = ((byte)(paramInt1 >> 8));
    buf[(paramInt2 + 3)] = ((byte)paramInt1);
  }
  
  void putShort(int paramInt1, int paramInt2)
  {
    buf[(paramInt2 + 0)] = ((byte)(paramInt1 >> 8));
    buf[(paramInt2 + 1)] = ((byte)paramInt1);
  }
  
  void putByte(int paramInt1, int paramInt2)
  {
    buf[paramInt2] = ((byte)paramInt1);
  }
  
  void putBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    System.arraycopy(paramArrayOfByte, paramInt1, buf, paramInt2, paramInt3);
  }
  
  int length()
  {
    return buf.length;
  }
  
  byte[] getData()
  {
    return buf;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\dns\Packet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */