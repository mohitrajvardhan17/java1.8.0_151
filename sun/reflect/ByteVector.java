package sun.reflect;

abstract interface ByteVector
{
  public abstract int getLength();
  
  public abstract byte get(int paramInt);
  
  public abstract void put(int paramInt, byte paramByte);
  
  public abstract void add(byte paramByte);
  
  public abstract void trim();
  
  public abstract byte[] getData();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\ByteVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */