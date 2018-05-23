package com.sun.media.sound;

import java.io.IOException;
import java.util.List;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.midi.VoiceStatus;

public abstract class ModelAbstractOscillator
  implements ModelOscillator, ModelOscillatorStream, Soundbank
{
  protected float pitch = 6000.0F;
  protected float samplerate;
  protected MidiChannel channel;
  protected VoiceStatus voice;
  protected int noteNumber;
  protected int velocity;
  protected boolean on = false;
  
  public ModelAbstractOscillator() {}
  
  public void init() {}
  
  public void close()
    throws IOException
  {}
  
  public void noteOff(int paramInt)
  {
    on = false;
  }
  
  public void noteOn(MidiChannel paramMidiChannel, VoiceStatus paramVoiceStatus, int paramInt1, int paramInt2)
  {
    channel = paramMidiChannel;
    voice = paramVoiceStatus;
    noteNumber = paramInt1;
    velocity = paramInt2;
    on = true;
  }
  
  public int read(float[][] paramArrayOfFloat, int paramInt1, int paramInt2)
    throws IOException
  {
    return -1;
  }
  
  public MidiChannel getChannel()
  {
    return channel;
  }
  
  public VoiceStatus getVoice()
  {
    return voice;
  }
  
  public int getNoteNumber()
  {
    return noteNumber;
  }
  
  public int getVelocity()
  {
    return velocity;
  }
  
  public boolean isOn()
  {
    return on;
  }
  
  public void setPitch(float paramFloat)
  {
    pitch = paramFloat;
  }
  
  public float getPitch()
  {
    return pitch;
  }
  
  public void setSampleRate(float paramFloat)
  {
    samplerate = paramFloat;
  }
  
  public float getSampleRate()
  {
    return samplerate;
  }
  
  public float getAttenuation()
  {
    return 0.0F;
  }
  
  public int getChannels()
  {
    return 1;
  }
  
  public String getName()
  {
    return getClass().getName();
  }
  
  public Patch getPatch()
  {
    return new Patch(0, 0);
  }
  
  public ModelOscillatorStream open(float paramFloat)
  {
    ModelAbstractOscillator localModelAbstractOscillator;
    try
    {
      localModelAbstractOscillator = (ModelAbstractOscillator)getClass().newInstance();
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new IllegalArgumentException(localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new IllegalArgumentException(localIllegalAccessException);
    }
    localModelAbstractOscillator.setSampleRate(paramFloat);
    localModelAbstractOscillator.init();
    return localModelAbstractOscillator;
  }
  
  public ModelPerformer getPerformer()
  {
    ModelPerformer localModelPerformer = new ModelPerformer();
    localModelPerformer.getOscillators().add(this);
    return localModelPerformer;
  }
  
  public ModelInstrument getInstrument()
  {
    SimpleInstrument localSimpleInstrument = new SimpleInstrument();
    localSimpleInstrument.setName(getName());
    localSimpleInstrument.add(getPerformer());
    localSimpleInstrument.setPatch(getPatch());
    return localSimpleInstrument;
  }
  
  public Soundbank getSoundBank()
  {
    SimpleSoundbank localSimpleSoundbank = new SimpleSoundbank();
    localSimpleSoundbank.addInstrument(getInstrument());
    return localSimpleSoundbank;
  }
  
  public String getDescription()
  {
    return getName();
  }
  
  public Instrument getInstrument(Patch paramPatch)
  {
    ModelInstrument localModelInstrument = getInstrument();
    Patch localPatch = localModelInstrument.getPatch();
    if (localPatch.getBank() != paramPatch.getBank()) {
      return null;
    }
    if (localPatch.getProgram() != paramPatch.getProgram()) {
      return null;
    }
    if (((localPatch instanceof ModelPatch)) && ((paramPatch instanceof ModelPatch)) && (((ModelPatch)localPatch).isPercussion() != ((ModelPatch)paramPatch).isPercussion())) {
      return null;
    }
    return localModelInstrument;
  }
  
  public Instrument[] getInstruments()
  {
    return new Instrument[] { getInstrument() };
  }
  
  public SoundbankResource[] getResources()
  {
    return new SoundbankResource[0];
  }
  
  public String getVendor()
  {
    return null;
  }
  
  public String getVersion()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\ModelAbstractOscillator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */