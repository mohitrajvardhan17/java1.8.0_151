package com.sun.media.sound;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.sampled.AudioFormat;

public abstract class ModelInstrument
  extends Instrument
{
  protected ModelInstrument(Soundbank paramSoundbank, Patch paramPatch, String paramString, Class<?> paramClass)
  {
    super(paramSoundbank, paramPatch, paramString, paramClass);
  }
  
  public ModelDirector getDirector(ModelPerformer[] paramArrayOfModelPerformer, MidiChannel paramMidiChannel, ModelDirectedPlayer paramModelDirectedPlayer)
  {
    return new ModelStandardIndexedDirector(paramArrayOfModelPerformer, paramModelDirectedPlayer);
  }
  
  public ModelPerformer[] getPerformers()
  {
    return new ModelPerformer[0];
  }
  
  public ModelChannelMixer getChannelMixer(MidiChannel paramMidiChannel, AudioFormat paramAudioFormat)
  {
    return null;
  }
  
  public final Patch getPatchAlias()
  {
    Patch localPatch = getPatch();
    int i = localPatch.getProgram();
    int j = localPatch.getBank();
    if (j != 0) {
      return localPatch;
    }
    boolean bool = false;
    if ((getPatch() instanceof ModelPatch)) {
      bool = ((ModelPatch)getPatch()).isPercussion();
    }
    if (bool) {
      return new Patch(15360, i);
    }
    return new Patch(15488, i);
  }
  
  public final String[] getKeys()
  {
    String[] arrayOfString = new String[''];
    for (ModelPerformer localModelPerformer : getPerformers()) {
      for (int k = localModelPerformer.getKeyFrom(); k <= localModelPerformer.getKeyTo(); k++) {
        if ((k >= 0) && (k < 128) && (arrayOfString[k] == null))
        {
          String str = localModelPerformer.getName();
          if (str == null) {
            str = "untitled";
          }
          arrayOfString[k] = str;
        }
      }
    }
    return arrayOfString;
  }
  
  public final boolean[] getChannels()
  {
    boolean bool = false;
    if ((getPatch() instanceof ModelPatch)) {
      bool = ((ModelPatch)getPatch()).isPercussion();
    }
    if (bool)
    {
      boolean[] arrayOfBoolean1 = new boolean[16];
      for (int j = 0; j < arrayOfBoolean1.length; j++) {
        arrayOfBoolean1[j] = false;
      }
      arrayOfBoolean1[9] = true;
      return arrayOfBoolean1;
    }
    int i = getPatch().getBank();
    if ((i >> 7 == 120) || (i >> 7 == 121))
    {
      arrayOfBoolean2 = new boolean[16];
      for (k = 0; k < arrayOfBoolean2.length; k++) {
        arrayOfBoolean2[k] = true;
      }
      return arrayOfBoolean2;
    }
    boolean[] arrayOfBoolean2 = new boolean[16];
    for (int k = 0; k < arrayOfBoolean2.length; k++) {
      arrayOfBoolean2[k] = true;
    }
    arrayOfBoolean2[9] = false;
    return arrayOfBoolean2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelInstrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */