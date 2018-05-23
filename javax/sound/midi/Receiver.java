package javax.sound.midi;

public abstract interface Receiver
  extends AutoCloseable
{
  public abstract void send(MidiMessage paramMidiMessage, long paramLong);
  
  public abstract void close();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\Receiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */