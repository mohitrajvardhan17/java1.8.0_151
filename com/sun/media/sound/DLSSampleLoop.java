package com.sun.media.sound;

public final class DLSSampleLoop
{
  public static final int LOOP_TYPE_FORWARD = 0;
  public static final int LOOP_TYPE_RELEASE = 1;
  long type;
  long start;
  long length;
  
  public DLSSampleLoop() {}
  
  public long getLength()
  {
    return length;
  }
  
  public void setLength(long paramLong)
  {
    length = paramLong;
  }
  
  public long getStart()
  {
    return start;
  }
  
  public void setStart(long paramLong)
  {
    start = paramLong;
  }
  
  public long getType()
  {
    return type;
  }
  
  public void setType(long paramLong)
  {
    type = paramLong;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\DLSSampleLoop.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */