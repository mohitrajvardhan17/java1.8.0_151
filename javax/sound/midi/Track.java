package javax.sound.midi;

import com.sun.media.sound.MidiUtils;
import java.util.ArrayList;
import java.util.HashSet;

public class Track
{
  private ArrayList eventsList = new ArrayList();
  private HashSet set = new HashSet();
  private MidiEvent eotEvent;
  
  Track()
  {
    ImmutableEndOfTrack localImmutableEndOfTrack = new ImmutableEndOfTrack(null);
    eotEvent = new MidiEvent(localImmutableEndOfTrack, 0L);
    eventsList.add(eotEvent);
    set.add(eotEvent);
  }
  
  public boolean add(MidiEvent paramMidiEvent)
  {
    if (paramMidiEvent == null) {
      return false;
    }
    synchronized (eventsList)
    {
      if (!set.contains(paramMidiEvent))
      {
        int i = eventsList.size();
        MidiEvent localMidiEvent = null;
        if (i > 0) {
          localMidiEvent = (MidiEvent)eventsList.get(i - 1);
        }
        if (localMidiEvent != eotEvent)
        {
          if (localMidiEvent != null) {
            eotEvent.setTick(localMidiEvent.getTick());
          } else {
            eotEvent.setTick(0L);
          }
          eventsList.add(eotEvent);
          set.add(eotEvent);
          i = eventsList.size();
        }
        if (MidiUtils.isMetaEndOfTrack(paramMidiEvent.getMessage()))
        {
          if (paramMidiEvent.getTick() > eotEvent.getTick()) {
            eotEvent.setTick(paramMidiEvent.getTick());
          }
          return true;
        }
        set.add(paramMidiEvent);
        for (int j = i; (j > 0) && (paramMidiEvent.getTick() < ((MidiEvent)eventsList.get(j - 1)).getTick()); j--) {}
        if (j == i)
        {
          eventsList.set(i - 1, paramMidiEvent);
          if (eotEvent.getTick() < paramMidiEvent.getTick()) {
            eotEvent.setTick(paramMidiEvent.getTick());
          }
          eventsList.add(eotEvent);
        }
        else
        {
          eventsList.add(j, paramMidiEvent);
        }
        return true;
      }
    }
    return false;
  }
  
  public boolean remove(MidiEvent paramMidiEvent)
  {
    synchronized (eventsList)
    {
      if (set.remove(paramMidiEvent))
      {
        int i = eventsList.indexOf(paramMidiEvent);
        if (i >= 0)
        {
          eventsList.remove(i);
          return true;
        }
      }
    }
    return false;
  }
  
  /* Error */
  public MidiEvent get(int paramInt)
    throws java.lang.ArrayIndexOutOfBoundsException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 83	javax/sound/midi/Track:eventsList	Ljava/util/ArrayList;
    //   4: dup
    //   5: astore_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 83	javax/sound/midi/Track:eventsList	Ljava/util/ArrayList;
    //   11: iload_1
    //   12: invokevirtual 92	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   15: checkcast 45	javax/sound/midi/MidiEvent
    //   18: aload_2
    //   19: monitorexit
    //   20: areturn
    //   21: astore_3
    //   22: aload_2
    //   23: monitorexit
    //   24: aload_3
    //   25: athrow
    //   26: astore_2
    //   27: new 39	java/lang/ArrayIndexOutOfBoundsException
    //   30: dup
    //   31: aload_2
    //   32: invokevirtual 88	java/lang/IndexOutOfBoundsException:getMessage	()Ljava/lang/String;
    //   35: invokespecial 87	java/lang/ArrayIndexOutOfBoundsException:<init>	(Ljava/lang/String;)V
    //   38: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	39	0	this	Track
    //   0	39	1	paramInt	int
    //   5	18	2	Ljava/lang/Object;	Object
    //   26	6	2	localIndexOutOfBoundsException	IndexOutOfBoundsException
    //   21	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	20	21	finally
    //   21	24	21	finally
    //   0	20	26	java/lang/IndexOutOfBoundsException
    //   21	26	26	java/lang/IndexOutOfBoundsException
  }
  
  /* Error */
  public int size()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 83	javax/sound/midi/Track:eventsList	Ljava/util/ArrayList;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 83	javax/sound/midi/Track:eventsList	Ljava/util/ArrayList;
    //   11: invokevirtual 90	java/util/ArrayList:size	()I
    //   14: aload_1
    //   15: monitorexit
    //   16: ireturn
    //   17: astore_2
    //   18: aload_1
    //   19: monitorexit
    //   20: aload_2
    //   21: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	22	0	this	Track
    //   5	14	1	Ljava/lang/Object;	Object
    //   17	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	16	17	finally
    //   17	20	17	finally
  }
  
  public long ticks()
  {
    long l = 0L;
    synchronized (eventsList)
    {
      if (eventsList.size() > 0) {
        l = ((MidiEvent)eventsList.get(eventsList.size() - 1)).getTick();
      }
    }
    return l;
  }
  
  private static class ImmutableEndOfTrack
    extends MetaMessage
  {
    private ImmutableEndOfTrack()
    {
      super();
      data[0] = -1;
      data[1] = 47;
      data[2] = 0;
    }
    
    public void setMessage(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
      throws InvalidMidiDataException
    {
      throw new InvalidMidiDataException("cannot modify end of track message");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\Track.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */