package javax.sound.midi;

import java.io.IOException;
import java.io.InputStream;

public abstract interface Sequencer
  extends MidiDevice
{
  public static final int LOOP_CONTINUOUSLY = -1;
  
  public abstract void setSequence(Sequence paramSequence)
    throws InvalidMidiDataException;
  
  public abstract void setSequence(InputStream paramInputStream)
    throws IOException, InvalidMidiDataException;
  
  public abstract Sequence getSequence();
  
  public abstract void start();
  
  public abstract void stop();
  
  public abstract boolean isRunning();
  
  public abstract void startRecording();
  
  public abstract void stopRecording();
  
  public abstract boolean isRecording();
  
  public abstract void recordEnable(Track paramTrack, int paramInt);
  
  public abstract void recordDisable(Track paramTrack);
  
  public abstract float getTempoInBPM();
  
  public abstract void setTempoInBPM(float paramFloat);
  
  public abstract float getTempoInMPQ();
  
  public abstract void setTempoInMPQ(float paramFloat);
  
  public abstract void setTempoFactor(float paramFloat);
  
  public abstract float getTempoFactor();
  
  public abstract long getTickLength();
  
  public abstract long getTickPosition();
  
  public abstract void setTickPosition(long paramLong);
  
  public abstract long getMicrosecondLength();
  
  public abstract long getMicrosecondPosition();
  
  public abstract void setMicrosecondPosition(long paramLong);
  
  public abstract void setMasterSyncMode(SyncMode paramSyncMode);
  
  public abstract SyncMode getMasterSyncMode();
  
  public abstract SyncMode[] getMasterSyncModes();
  
  public abstract void setSlaveSyncMode(SyncMode paramSyncMode);
  
  public abstract SyncMode getSlaveSyncMode();
  
  public abstract SyncMode[] getSlaveSyncModes();
  
  public abstract void setTrackMute(int paramInt, boolean paramBoolean);
  
  public abstract boolean getTrackMute(int paramInt);
  
  public abstract void setTrackSolo(int paramInt, boolean paramBoolean);
  
  public abstract boolean getTrackSolo(int paramInt);
  
  public abstract boolean addMetaEventListener(MetaEventListener paramMetaEventListener);
  
  public abstract void removeMetaEventListener(MetaEventListener paramMetaEventListener);
  
  public abstract int[] addControllerEventListener(ControllerEventListener paramControllerEventListener, int[] paramArrayOfInt);
  
  public abstract int[] removeControllerEventListener(ControllerEventListener paramControllerEventListener, int[] paramArrayOfInt);
  
  public abstract void setLoopStartPoint(long paramLong);
  
  public abstract long getLoopStartPoint();
  
  public abstract void setLoopEndPoint(long paramLong);
  
  public abstract long getLoopEndPoint();
  
  public abstract void setLoopCount(int paramInt);
  
  public abstract int getLoopCount();
  
  public static class SyncMode
  {
    private String name;
    public static final SyncMode INTERNAL_CLOCK = new SyncMode("Internal Clock");
    public static final SyncMode MIDI_SYNC = new SyncMode("MIDI Sync");
    public static final SyncMode MIDI_TIME_CODE = new SyncMode("MIDI Time Code");
    public static final SyncMode NO_SYNC = new SyncMode("No Timing");
    
    protected SyncMode(String paramString)
    {
      name = paramString;
    }
    
    public final boolean equals(Object paramObject)
    {
      return super.equals(paramObject);
    }
    
    public final int hashCode()
    {
      return super.hashCode();
    }
    
    public final String toString()
    {
      return name;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\Sequencer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */