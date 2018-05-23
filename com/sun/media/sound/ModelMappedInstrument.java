package com.sun.media.sound;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;
import javax.sound.sampled.AudioFormat;

public final class ModelMappedInstrument
  extends ModelInstrument
{
  private final ModelInstrument ins;
  
  public ModelMappedInstrument(ModelInstrument paramModelInstrument, Patch paramPatch)
  {
    super(paramModelInstrument.getSoundbank(), paramPatch, paramModelInstrument.getName(), paramModelInstrument.getDataClass());
    ins = paramModelInstrument;
  }
  
  public Object getData()
  {
    return ins.getData();
  }
  
  public ModelPerformer[] getPerformers()
  {
    return ins.getPerformers();
  }
  
  public ModelDirector getDirector(ModelPerformer[] paramArrayOfModelPerformer, MidiChannel paramMidiChannel, ModelDirectedPlayer paramModelDirectedPlayer)
  {
    return ins.getDirector(paramArrayOfModelPerformer, paramMidiChannel, paramModelDirectedPlayer);
  }
  
  public ModelChannelMixer getChannelMixer(MidiChannel paramMidiChannel, AudioFormat paramAudioFormat)
  {
    return ins.getChannelMixer(paramMidiChannel, paramAudioFormat);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelMappedInstrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */