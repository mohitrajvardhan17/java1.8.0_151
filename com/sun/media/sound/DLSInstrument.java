package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sound.midi.Patch;

public final class DLSInstrument
  extends ModelInstrument
{
  int preset = 0;
  int bank = 0;
  boolean druminstrument = false;
  byte[] guid = null;
  DLSInfo info = new DLSInfo();
  List<DLSRegion> regions = new ArrayList();
  List<DLSModulator> modulators = new ArrayList();
  
  public DLSInstrument()
  {
    super(null, null, null, null);
  }
  
  public DLSInstrument(DLSSoundbank paramDLSSoundbank)
  {
    super(paramDLSSoundbank, null, null, null);
  }
  
  public DLSInfo getInfo()
  {
    return info;
  }
  
  public String getName()
  {
    return info.name;
  }
  
  public void setName(String paramString)
  {
    info.name = paramString;
  }
  
  public ModelPatch getPatch()
  {
    return new ModelPatch(bank, preset, druminstrument);
  }
  
  public void setPatch(Patch paramPatch)
  {
    if (((paramPatch instanceof ModelPatch)) && (((ModelPatch)paramPatch).isPercussion()))
    {
      druminstrument = true;
      bank = paramPatch.getBank();
      preset = paramPatch.getProgram();
    }
    else
    {
      druminstrument = false;
      bank = paramPatch.getBank();
      preset = paramPatch.getProgram();
    }
  }
  
  public Object getData()
  {
    return null;
  }
  
  public List<DLSRegion> getRegions()
  {
    return regions;
  }
  
  public List<DLSModulator> getModulators()
  {
    return modulators;
  }
  
  public String toString()
  {
    if (druminstrument) {
      return "Drumkit: " + info.name + " bank #" + bank + " preset #" + preset;
    }
    return "Instrument: " + info.name + " bank #" + bank + " preset #" + preset;
  }
  
  private ModelIdentifier convertToModelDest(int paramInt)
  {
    if (paramInt == 0) {
      return null;
    }
    if (paramInt == 1) {
      return ModelDestination.DESTINATION_GAIN;
    }
    if (paramInt == 3) {
      return ModelDestination.DESTINATION_PITCH;
    }
    if (paramInt == 4) {
      return ModelDestination.DESTINATION_PAN;
    }
    if (paramInt == 260) {
      return ModelDestination.DESTINATION_LFO1_FREQ;
    }
    if (paramInt == 261) {
      return ModelDestination.DESTINATION_LFO1_DELAY;
    }
    if (paramInt == 518) {
      return ModelDestination.DESTINATION_EG1_ATTACK;
    }
    if (paramInt == 519) {
      return ModelDestination.DESTINATION_EG1_DECAY;
    }
    if (paramInt == 521) {
      return ModelDestination.DESTINATION_EG1_RELEASE;
    }
    if (paramInt == 522) {
      return ModelDestination.DESTINATION_EG1_SUSTAIN;
    }
    if (paramInt == 778) {
      return ModelDestination.DESTINATION_EG2_ATTACK;
    }
    if (paramInt == 779) {
      return ModelDestination.DESTINATION_EG2_DECAY;
    }
    if (paramInt == 781) {
      return ModelDestination.DESTINATION_EG2_RELEASE;
    }
    if (paramInt == 782) {
      return ModelDestination.DESTINATION_EG2_SUSTAIN;
    }
    if (paramInt == 5) {
      return ModelDestination.DESTINATION_KEYNUMBER;
    }
    if (paramInt == 128) {
      return ModelDestination.DESTINATION_CHORUS;
    }
    if (paramInt == 129) {
      return ModelDestination.DESTINATION_REVERB;
    }
    if (paramInt == 276) {
      return ModelDestination.DESTINATION_LFO2_FREQ;
    }
    if (paramInt == 277) {
      return ModelDestination.DESTINATION_LFO2_DELAY;
    }
    if (paramInt == 523) {
      return ModelDestination.DESTINATION_EG1_DELAY;
    }
    if (paramInt == 524) {
      return ModelDestination.DESTINATION_EG1_HOLD;
    }
    if (paramInt == 525) {
      return ModelDestination.DESTINATION_EG1_SHUTDOWN;
    }
    if (paramInt == 783) {
      return ModelDestination.DESTINATION_EG2_DELAY;
    }
    if (paramInt == 784) {
      return ModelDestination.DESTINATION_EG2_HOLD;
    }
    if (paramInt == 1280) {
      return ModelDestination.DESTINATION_FILTER_FREQ;
    }
    if (paramInt == 1281) {
      return ModelDestination.DESTINATION_FILTER_Q;
    }
    return null;
  }
  
  private ModelIdentifier convertToModelSrc(int paramInt)
  {
    if (paramInt == 0) {
      return null;
    }
    if (paramInt == 1) {
      return ModelSource.SOURCE_LFO1;
    }
    if (paramInt == 2) {
      return ModelSource.SOURCE_NOTEON_VELOCITY;
    }
    if (paramInt == 3) {
      return ModelSource.SOURCE_NOTEON_KEYNUMBER;
    }
    if (paramInt == 4) {
      return ModelSource.SOURCE_EG1;
    }
    if (paramInt == 5) {
      return ModelSource.SOURCE_EG2;
    }
    if (paramInt == 6) {
      return ModelSource.SOURCE_MIDI_PITCH;
    }
    if (paramInt == 129) {
      return new ModelIdentifier("midi_cc", "1", 0);
    }
    if (paramInt == 135) {
      return new ModelIdentifier("midi_cc", "7", 0);
    }
    if (paramInt == 138) {
      return new ModelIdentifier("midi_cc", "10", 0);
    }
    if (paramInt == 139) {
      return new ModelIdentifier("midi_cc", "11", 0);
    }
    if (paramInt == 256) {
      return new ModelIdentifier("midi_rpn", "0", 0);
    }
    if (paramInt == 257) {
      return new ModelIdentifier("midi_rpn", "1", 0);
    }
    if (paramInt == 7) {
      return ModelSource.SOURCE_MIDI_POLY_PRESSURE;
    }
    if (paramInt == 8) {
      return ModelSource.SOURCE_MIDI_CHANNEL_PRESSURE;
    }
    if (paramInt == 9) {
      return ModelSource.SOURCE_LFO2;
    }
    if (paramInt == 10) {
      return ModelSource.SOURCE_MIDI_CHANNEL_PRESSURE;
    }
    if (paramInt == 219) {
      return new ModelIdentifier("midi_cc", "91", 0);
    }
    if (paramInt == 221) {
      return new ModelIdentifier("midi_cc", "93", 0);
    }
    return null;
  }
  
  private ModelConnectionBlock convertToModel(DLSModulator paramDLSModulator)
  {
    ModelIdentifier localModelIdentifier1 = convertToModelSrc(paramDLSModulator.getSource());
    ModelIdentifier localModelIdentifier2 = convertToModelSrc(paramDLSModulator.getControl());
    ModelIdentifier localModelIdentifier3 = convertToModelDest(paramDLSModulator.getDestination());
    int i = paramDLSModulator.getScale();
    double d;
    if (i == Integer.MIN_VALUE) {
      d = Double.NEGATIVE_INFINITY;
    } else {
      d = i / 65536.0D;
    }
    if (localModelIdentifier3 != null)
    {
      Object localObject1 = null;
      Object localObject2 = null;
      ModelConnectionBlock localModelConnectionBlock = new ModelConnectionBlock();
      if (localModelIdentifier2 != null)
      {
        localObject3 = new ModelSource();
        if (localModelIdentifier2 == ModelSource.SOURCE_MIDI_PITCH) {
          ((ModelStandardTransform)((ModelSource)localObject3).getTransform()).setPolarity(true);
        } else if ((localModelIdentifier2 == ModelSource.SOURCE_LFO1) || (localModelIdentifier2 == ModelSource.SOURCE_LFO2)) {
          ((ModelStandardTransform)((ModelSource)localObject3).getTransform()).setPolarity(true);
        }
        ((ModelSource)localObject3).setIdentifier(localModelIdentifier2);
        localModelConnectionBlock.addSource((ModelSource)localObject3);
        localObject2 = localObject3;
      }
      if (localModelIdentifier1 != null)
      {
        localObject3 = new ModelSource();
        if (localModelIdentifier1 == ModelSource.SOURCE_MIDI_PITCH) {
          ((ModelStandardTransform)((ModelSource)localObject3).getTransform()).setPolarity(true);
        } else if ((localModelIdentifier1 == ModelSource.SOURCE_LFO1) || (localModelIdentifier1 == ModelSource.SOURCE_LFO2)) {
          ((ModelStandardTransform)((ModelSource)localObject3).getTransform()).setPolarity(true);
        }
        ((ModelSource)localObject3).setIdentifier(localModelIdentifier1);
        localModelConnectionBlock.addSource((ModelSource)localObject3);
        localObject1 = localObject3;
      }
      Object localObject3 = new ModelDestination();
      ((ModelDestination)localObject3).setIdentifier(localModelIdentifier3);
      localModelConnectionBlock.setDestination((ModelDestination)localObject3);
      if (paramDLSModulator.getVersion() == 1)
      {
        if (paramDLSModulator.getTransform() == 1)
        {
          if (localObject1 != null)
          {
            ((ModelStandardTransform)((ModelSource)localObject1).getTransform()).setTransform(1);
            ((ModelStandardTransform)((ModelSource)localObject1).getTransform()).setDirection(true);
          }
          if (localObject2 != null)
          {
            ((ModelStandardTransform)((ModelSource)localObject2).getTransform()).setTransform(1);
            ((ModelStandardTransform)((ModelSource)localObject2).getTransform()).setDirection(true);
          }
        }
      }
      else if (paramDLSModulator.getVersion() == 2)
      {
        int j = paramDLSModulator.getTransform();
        int k = j >> 15 & 0x1;
        int m = j >> 14 & 0x1;
        int n = j >> 10 & 0x8;
        int i1 = j >> 9 & 0x1;
        int i2 = j >> 8 & 0x1;
        int i3 = j >> 4 & 0x8;
        int i4;
        if (localObject1 != null)
        {
          i4 = 0;
          if (n == 3) {
            i4 = 3;
          }
          if (n == 1) {
            i4 = 1;
          }
          if (n == 2) {
            i4 = 2;
          }
          ((ModelStandardTransform)((ModelSource)localObject1).getTransform()).setTransform(i4);
          ((ModelStandardTransform)((ModelSource)localObject1).getTransform()).setPolarity(m == 1);
          ((ModelStandardTransform)((ModelSource)localObject1).getTransform()).setDirection(k == 1);
        }
        if (localObject2 != null)
        {
          i4 = 0;
          if (i3 == 3) {
            i4 = 3;
          }
          if (i3 == 1) {
            i4 = 1;
          }
          if (i3 == 2) {
            i4 = 2;
          }
          ((ModelStandardTransform)((ModelSource)localObject2).getTransform()).setTransform(i4);
          ((ModelStandardTransform)((ModelSource)localObject2).getTransform()).setPolarity(i2 == 1);
          ((ModelStandardTransform)((ModelSource)localObject2).getTransform()).setDirection(i1 == 1);
        }
      }
      localModelConnectionBlock.setScale(d);
      return localModelConnectionBlock;
    }
    return null;
  }
  
  public ModelPerformer[] getPerformers()
  {
    ArrayList localArrayList = new ArrayList();
    HashMap localHashMap = new HashMap();
    Object localObject1 = getModulators().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (DLSModulator)((Iterator)localObject1).next();
      localHashMap.put(((DLSModulator)localObject2).getSource() + "x" + ((DLSModulator)localObject2).getControl() + "=" + ((DLSModulator)localObject2).getDestination(), localObject2);
    }
    localObject1 = new HashMap();
    Object localObject2 = regions.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      DLSRegion localDLSRegion = (DLSRegion)((Iterator)localObject2).next();
      ModelPerformer localModelPerformer = new ModelPerformer();
      localModelPerformer.setName(localDLSRegion.getSample().getName());
      localModelPerformer.setSelfNonExclusive((localDLSRegion.getFusoptions() & 0x1) != 0);
      localModelPerformer.setExclusiveClass(localDLSRegion.getExclusiveClass());
      localModelPerformer.setKeyFrom(localDLSRegion.getKeyfrom());
      localModelPerformer.setKeyTo(localDLSRegion.getKeyto());
      localModelPerformer.setVelFrom(localDLSRegion.getVelfrom());
      localModelPerformer.setVelTo(localDLSRegion.getVelto());
      ((Map)localObject1).clear();
      ((Map)localObject1).putAll(localHashMap);
      Object localObject3 = localDLSRegion.getModulators().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = (DLSModulator)((Iterator)localObject3).next();
        ((Map)localObject1).put(((DLSModulator)localObject4).getSource() + "x" + ((DLSModulator)localObject4).getControl() + "=" + ((DLSModulator)localObject4).getDestination(), localObject4);
      }
      localObject3 = localModelPerformer.getConnectionBlocks();
      Object localObject4 = ((Map)localObject1).values().iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (DLSModulator)((Iterator)localObject4).next();
        localObject6 = convertToModel((DLSModulator)localObject5);
        if (localObject6 != null) {
          ((List)localObject3).add(localObject6);
        }
      }
      localObject4 = localDLSRegion.getSample();
      Object localObject5 = localDLSRegion.getSampleoptions();
      if (localObject5 == null) {
        localObject5 = ((DLSSample)localObject4).getSampleoptions();
      }
      Object localObject6 = ((DLSSample)localObject4).getDataBuffer();
      float f = -unitynote * 100 + finetune;
      ModelByteBufferWavetable localModelByteBufferWavetable = new ModelByteBufferWavetable((ModelByteBuffer)localObject6, ((DLSSample)localObject4).getFormat(), f);
      localModelByteBufferWavetable.setAttenuation(localModelByteBufferWavetable.getAttenuation() / 65536.0F);
      if (((DLSSampleOptions)localObject5).getLoops().size() != 0)
      {
        DLSSampleLoop localDLSSampleLoop = (DLSSampleLoop)((DLSSampleOptions)localObject5).getLoops().get(0);
        localModelByteBufferWavetable.setLoopStart((int)localDLSSampleLoop.getStart());
        localModelByteBufferWavetable.setLoopLength((int)localDLSSampleLoop.getLength());
        if (localDLSSampleLoop.getType() == 0L) {
          localModelByteBufferWavetable.setLoopType(1);
        }
        if (localDLSSampleLoop.getType() == 1L) {
          localModelByteBufferWavetable.setLoopType(2);
        } else {
          localModelByteBufferWavetable.setLoopType(1);
        }
      }
      localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(1.0D, new ModelDestination(new ModelIdentifier("filter", "type", 1))));
      localModelPerformer.getOscillators().add(localModelByteBufferWavetable);
      localArrayList.add(localModelPerformer);
    }
    return (ModelPerformer[])localArrayList.toArray(new ModelPerformer[localArrayList.size()]);
  }
  
  public byte[] getGuid()
  {
    return guid == null ? null : Arrays.copyOf(guid, guid.length);
  }
  
  public void setGuid(byte[] paramArrayOfByte)
  {
    guid = (paramArrayOfByte == null ? null : Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\DLSInstrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */