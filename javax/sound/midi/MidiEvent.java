package javax.sound.midi;

public class MidiEvent
{
  private final MidiMessage message;
  private long tick;
  
  public MidiEvent(MidiMessage paramMidiMessage, long paramLong)
  {
    message = paramMidiMessage;
    tick = paramLong;
  }
  
  public MidiMessage getMessage()
  {
    return message;
  }
  
  public void setTick(long paramLong)
  {
    tick = paramLong;
  }
  
  public long getTick()
  {
    return tick;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\MidiEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */