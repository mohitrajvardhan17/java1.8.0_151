package javax.sound.midi;

import com.sun.media.sound.MidiUtils;
import java.util.Vector;

public class Sequence
{
  public static final float PPQ = 0.0F;
  public static final float SMPTE_24 = 24.0F;
  public static final float SMPTE_25 = 25.0F;
  public static final float SMPTE_30DROP = 29.97F;
  public static final float SMPTE_30 = 30.0F;
  protected float divisionType;
  protected int resolution;
  protected Vector<Track> tracks = new Vector();
  
  public Sequence(float paramFloat, int paramInt)
    throws InvalidMidiDataException
  {
    if (paramFloat == 0.0F) {
      divisionType = 0.0F;
    } else if (paramFloat == 24.0F) {
      divisionType = 24.0F;
    } else if (paramFloat == 25.0F) {
      divisionType = 25.0F;
    } else if (paramFloat == 29.97F) {
      divisionType = 29.97F;
    } else if (paramFloat == 30.0F) {
      divisionType = 30.0F;
    } else {
      throw new InvalidMidiDataException("Unsupported division type: " + paramFloat);
    }
    resolution = paramInt;
  }
  
  public Sequence(float paramFloat, int paramInt1, int paramInt2)
    throws InvalidMidiDataException
  {
    if (paramFloat == 0.0F) {
      divisionType = 0.0F;
    } else if (paramFloat == 24.0F) {
      divisionType = 24.0F;
    } else if (paramFloat == 25.0F) {
      divisionType = 25.0F;
    } else if (paramFloat == 29.97F) {
      divisionType = 29.97F;
    } else if (paramFloat == 30.0F) {
      divisionType = 30.0F;
    } else {
      throw new InvalidMidiDataException("Unsupported division type: " + paramFloat);
    }
    resolution = paramInt1;
    for (int i = 0; i < paramInt2; i++) {
      tracks.addElement(new Track());
    }
  }
  
  public float getDivisionType()
  {
    return divisionType;
  }
  
  public int getResolution()
  {
    return resolution;
  }
  
  public Track createTrack()
  {
    Track localTrack = new Track();
    tracks.addElement(localTrack);
    return localTrack;
  }
  
  /* Error */
  public boolean deleteTrack(Track paramTrack)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 103	javax/sound/midi/Sequence:tracks	Ljava/util/Vector;
    //   4: dup
    //   5: astore_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 103	javax/sound/midi/Sequence:tracks	Ljava/util/Vector;
    //   11: aload_1
    //   12: invokevirtual 114	java/util/Vector:removeElement	(Ljava/lang/Object;)Z
    //   15: aload_2
    //   16: monitorexit
    //   17: ireturn
    //   18: astore_3
    //   19: aload_2
    //   20: monitorexit
    //   21: aload_3
    //   22: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	23	0	this	Sequence
    //   0	23	1	paramTrack	Track
    //   5	15	2	Ljava/lang/Object;	Object
    //   18	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	17	18	finally
    //   18	21	18	finally
  }
  
  public Track[] getTracks()
  {
    return (Track[])tracks.toArray(new Track[tracks.size()]);
  }
  
  public long getMicrosecondLength()
  {
    return MidiUtils.tick2microsecond(this, getTickLength(), null);
  }
  
  public long getTickLength()
  {
    long l1 = 0L;
    synchronized (tracks)
    {
      for (int i = 0; i < tracks.size(); i++)
      {
        long l2 = ((Track)tracks.elementAt(i)).ticks();
        if (l2 > l1) {
          l1 = l2;
        }
      }
      return l1;
    }
  }
  
  public Patch[] getPatchList()
  {
    return new Patch[0];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\Sequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */