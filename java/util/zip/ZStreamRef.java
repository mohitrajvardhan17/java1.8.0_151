package java.util.zip;

class ZStreamRef
{
  private volatile long address;
  
  ZStreamRef(long paramLong)
  {
    address = paramLong;
  }
  
  long address()
  {
    return address;
  }
  
  void clear()
  {
    address = 0L;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\ZStreamRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */