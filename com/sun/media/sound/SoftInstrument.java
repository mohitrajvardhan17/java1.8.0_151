package com.sun.media.sound;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;

public final class SoftInstrument
  extends Instrument
{
  private SoftPerformer[] performers;
  private ModelPerformer[] modelperformers;
  private final Object data;
  private final ModelInstrument ins;
  
  public SoftInstrument(ModelInstrument paramModelInstrument)
  {
    super(paramModelInstrument.getSoundbank(), paramModelInstrument.getPatch(), paramModelInstrument.getName(), paramModelInstrument.getDataClass());
    data = paramModelInstrument.getData();
    ins = paramModelInstrument;
    initPerformers(paramModelInstrument.getPerformers());
  }
  
  public SoftInstrument(ModelInstrument paramModelInstrument, ModelPerformer[] paramArrayOfModelPerformer)
  {
    super(paramModelInstrument.getSoundbank(), paramModelInstrument.getPatch(), paramModelInstrument.getName(), paramModelInstrument.getDataClass());
    data = paramModelInstrument.getData();
    ins = paramModelInstrument;
    initPerformers(paramArrayOfModelPerformer);
  }
  
  private void initPerformers(ModelPerformer[] paramArrayOfModelPerformer)
  {
    modelperformers = paramArrayOfModelPerformer;
    performers = new SoftPerformer[paramArrayOfModelPerformer.length];
    for (int i = 0; i < paramArrayOfModelPerformer.length; i++) {
      performers[i] = new SoftPerformer(paramArrayOfModelPerformer[i]);
    }
  }
  
  public ModelDirector getDirector(MidiChannel paramMidiChannel, ModelDirectedPlayer paramModelDirectedPlayer)
  {
    return ins.getDirector(modelperformers, paramMidiChannel, paramModelDirectedPlayer);
  }
  
  public ModelInstrument getSourceInstrument()
  {
    return ins;
  }
  
  public Object getData()
  {
    return data;
  }
  
  public SoftPerformer getPerformer(int paramInt)
  {
    return performers[paramInt];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftInstrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */