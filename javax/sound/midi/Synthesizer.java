package javax.sound.midi;

public abstract interface Synthesizer
  extends MidiDevice
{
  public abstract int getMaxPolyphony();
  
  public abstract long getLatency();
  
  public abstract MidiChannel[] getChannels();
  
  public abstract VoiceStatus[] getVoiceStatus();
  
  public abstract boolean isSoundbankSupported(Soundbank paramSoundbank);
  
  public abstract boolean loadInstrument(Instrument paramInstrument);
  
  public abstract void unloadInstrument(Instrument paramInstrument);
  
  public abstract boolean remapInstrument(Instrument paramInstrument1, Instrument paramInstrument2);
  
  public abstract Soundbank getDefaultSoundbank();
  
  public abstract Instrument[] getAvailableInstruments();
  
  public abstract Instrument[] getLoadedInstruments();
  
  public abstract boolean loadAllInstruments(Soundbank paramSoundbank);
  
  public abstract void unloadAllInstruments(Soundbank paramSoundbank);
  
  public abstract boolean loadInstruments(Soundbank paramSoundbank, Patch[] paramArrayOfPatch);
  
  public abstract void unloadInstruments(Soundbank paramSoundbank, Patch[] paramArrayOfPatch);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\Synthesizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */