package sun.misc;

public class CRC16
{
  public int value = 0;
  
  public CRC16() {}
  
  public void update(byte paramByte)
  {
    int i = paramByte;
    for (int k = 7; k >= 0; k--)
    {
      i <<= 1;
      int j = i >>> 8 & 0x1;
      if ((value & 0x8000) != 0) {
        value = ((value << 1) + j ^ 0x1021);
      } else {
        value = ((value << 1) + j);
      }
    }
    value &= 0xFFFF;
  }
  
  public void reset()
  {
    value = 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\CRC16.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */