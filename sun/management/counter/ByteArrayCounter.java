package sun.management.counter;

public abstract interface ByteArrayCounter
  extends Counter
{
  public abstract byte[] byteArrayValue();
  
  public abstract byte byteAt(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\counter\ByteArrayCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */