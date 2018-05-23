package javax.sound.midi;

public abstract interface Transmitter
  extends AutoCloseable
{
  public abstract void setReceiver(Receiver paramReceiver);
  
  public abstract Receiver getReceiver();
  
  public abstract void close();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\Transmitter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */