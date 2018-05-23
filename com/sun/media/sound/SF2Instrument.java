package com.sun.media.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.sound.midi.Patch;

public final class SF2Instrument
  extends ModelInstrument
{
  String name = "";
  int preset = 0;
  int bank = 0;
  long library = 0L;
  long genre = 0L;
  long morphology = 0L;
  SF2GlobalRegion globalregion = null;
  List<SF2InstrumentRegion> regions = new ArrayList();
  
  public SF2Instrument()
  {
    super(null, null, null, null);
  }
  
  public SF2Instrument(SF2Soundbank paramSF2Soundbank)
  {
    super(paramSF2Soundbank, null, null, null);
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public Patch getPatch()
  {
    if (bank == 128) {
      return new ModelPatch(0, preset, true);
    }
    return new ModelPatch(bank << 7, preset, false);
  }
  
  public void setPatch(Patch paramPatch)
  {
    if (((paramPatch instanceof ModelPatch)) && (((ModelPatch)paramPatch).isPercussion()))
    {
      bank = 128;
      preset = paramPatch.getProgram();
    }
    else
    {
      bank = (paramPatch.getBank() >> 7);
      preset = paramPatch.getProgram();
    }
  }
  
  public Object getData()
  {
    return null;
  }
  
  public long getGenre()
  {
    return genre;
  }
  
  public void setGenre(long paramLong)
  {
    genre = paramLong;
  }
  
  public long getLibrary()
  {
    return library;
  }
  
  public void setLibrary(long paramLong)
  {
    library = paramLong;
  }
  
  public long getMorphology()
  {
    return morphology;
  }
  
  public void setMorphology(long paramLong)
  {
    morphology = paramLong;
  }
  
  public List<SF2InstrumentRegion> getRegions()
  {
    return regions;
  }
  
  public SF2GlobalRegion getGlobalRegion()
  {
    return globalregion;
  }
  
  public void setGlobalZone(SF2GlobalRegion paramSF2GlobalRegion)
  {
    globalregion = paramSF2GlobalRegion;
  }
  
  public String toString()
  {
    if (bank == 128) {
      return "Drumkit: " + name + " preset #" + preset;
    }
    return "Instrument: " + name + " bank #" + bank + " preset #" + preset;
  }
  
  public ModelPerformer[] getPerformers()
  {
    int i = 0;
    Object localObject1 = regions.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      SF2InstrumentRegion localSF2InstrumentRegion1 = (SF2InstrumentRegion)((Iterator)localObject1).next();
      i += localSF2InstrumentRegion1.getLayer().getRegions().size();
    }
    localObject1 = new ModelPerformer[i];
    int j = 0;
    SF2GlobalRegion localSF2GlobalRegion1 = globalregion;
    Iterator localIterator1 = regions.iterator();
    while (localIterator1.hasNext())
    {
      SF2InstrumentRegion localSF2InstrumentRegion2 = (SF2InstrumentRegion)localIterator1.next();
      HashMap localHashMap1 = new HashMap();
      localHashMap1.putAll(localSF2InstrumentRegion2.getGenerators());
      if (localSF2GlobalRegion1 != null) {
        localHashMap1.putAll(localSF2GlobalRegion1.getGenerators());
      }
      SF2Layer localSF2Layer = localSF2InstrumentRegion2.getLayer();
      SF2GlobalRegion localSF2GlobalRegion2 = localSF2Layer.getGlobalRegion();
      Iterator localIterator2 = localSF2Layer.getRegions().iterator();
      while (localIterator2.hasNext())
      {
        SF2LayerRegion localSF2LayerRegion = (SF2LayerRegion)localIterator2.next();
        ModelPerformer localModelPerformer = new ModelPerformer();
        if (localSF2LayerRegion.getSample() != null) {
          localModelPerformer.setName(localSF2LayerRegion.getSample().getName());
        } else {
          localModelPerformer.setName(localSF2Layer.getName());
        }
        localObject1[(j++)] = localModelPerformer;
        int k = 0;
        int m = 127;
        int n = 0;
        int i1 = 127;
        if (localSF2LayerRegion.contains(57)) {
          localModelPerformer.setExclusiveClass(localSF2LayerRegion.getInteger(57));
        }
        byte[] arrayOfByte;
        if (localSF2LayerRegion.contains(43))
        {
          arrayOfByte = localSF2LayerRegion.getBytes(43);
          if ((arrayOfByte[0] >= 0) && (arrayOfByte[0] > k)) {
            k = arrayOfByte[0];
          }
          if ((arrayOfByte[1] >= 0) && (arrayOfByte[1] < m)) {
            m = arrayOfByte[1];
          }
        }
        if (localSF2LayerRegion.contains(44))
        {
          arrayOfByte = localSF2LayerRegion.getBytes(44);
          if ((arrayOfByte[0] >= 0) && (arrayOfByte[0] > n)) {
            n = arrayOfByte[0];
          }
          if ((arrayOfByte[1] >= 0) && (arrayOfByte[1] < i1)) {
            i1 = arrayOfByte[1];
          }
        }
        if (localSF2InstrumentRegion2.contains(43))
        {
          arrayOfByte = localSF2InstrumentRegion2.getBytes(43);
          if (arrayOfByte[0] > k) {
            k = arrayOfByte[0];
          }
          if (arrayOfByte[1] < m) {
            m = arrayOfByte[1];
          }
        }
        if (localSF2InstrumentRegion2.contains(44))
        {
          arrayOfByte = localSF2InstrumentRegion2.getBytes(44);
          if (arrayOfByte[0] > n) {
            n = arrayOfByte[0];
          }
          if (arrayOfByte[1] < i1) {
            i1 = arrayOfByte[1];
          }
        }
        localModelPerformer.setKeyFrom(k);
        localModelPerformer.setKeyTo(m);
        localModelPerformer.setVelFrom(n);
        localModelPerformer.setVelTo(i1);
        int i2 = localSF2LayerRegion.getShort(0);
        int i3 = localSF2LayerRegion.getShort(1);
        int i4 = localSF2LayerRegion.getShort(2);
        int i5 = localSF2LayerRegion.getShort(3);
        i2 += localSF2LayerRegion.getShort(4) * 32768;
        i3 += localSF2LayerRegion.getShort(12) * 32768;
        i4 += localSF2LayerRegion.getShort(45) * 32768;
        i5 += localSF2LayerRegion.getShort(50) * 32768;
        i4 -= i2;
        i5 -= i2;
        SF2Sample localSF2Sample = localSF2LayerRegion.getSample();
        int i6 = originalPitch;
        if (localSF2LayerRegion.getShort(58) != -1) {
          i6 = localSF2LayerRegion.getShort(58);
        }
        float f1 = -i6 * 100 + pitchCorrection;
        ModelByteBuffer localModelByteBuffer1 = localSF2Sample.getDataBuffer();
        ModelByteBuffer localModelByteBuffer2 = localSF2Sample.getData24Buffer();
        if ((i2 != 0) || (i3 != 0))
        {
          localModelByteBuffer1 = localModelByteBuffer1.subbuffer(i2 * 2, localModelByteBuffer1.capacity() + i3 * 2);
          if (localModelByteBuffer2 != null) {
            localModelByteBuffer2 = localModelByteBuffer2.subbuffer(i2, localModelByteBuffer2.capacity() + i3);
          }
        }
        ModelByteBufferWavetable localModelByteBufferWavetable = new ModelByteBufferWavetable(localModelByteBuffer1, localSF2Sample.getFormat(), f1);
        if (localModelByteBuffer2 != null) {
          localModelByteBufferWavetable.set8BitExtensionBuffer(localModelByteBuffer2);
        }
        HashMap localHashMap2 = new HashMap();
        if (localSF2GlobalRegion2 != null) {
          localHashMap2.putAll(localSF2GlobalRegion2.getGenerators());
        }
        localHashMap2.putAll(localSF2LayerRegion.getGenerators());
        Iterator localIterator3 = localHashMap1.entrySet().iterator();
        while (localIterator3.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator3.next();
          if (!localHashMap2.containsKey(localEntry.getKey())) {
            s2 = localSF2LayerRegion.getShort(((Integer)localEntry.getKey()).intValue());
          } else {
            s2 = ((Short)localHashMap2.get(localEntry.getKey())).shortValue();
          }
          s2 = (short)(s2 + ((Short)localEntry.getValue()).shortValue());
          localHashMap2.put(localEntry.getKey(), Short.valueOf(s2));
        }
        int i7 = getGeneratorValue(localHashMap2, 54);
        if (((i7 == 1) || (i7 == 3)) && (startLoop >= 0L) && (endLoop > 0L))
        {
          localModelByteBufferWavetable.setLoopStart((int)(startLoop + i4));
          localModelByteBufferWavetable.setLoopLength((int)(endLoop - startLoop + i5 - i4));
          if (i7 == 1) {
            localModelByteBufferWavetable.setLoopType(1);
          }
          if (i7 == 3) {
            localModelByteBufferWavetable.setLoopType(2);
          }
        }
        localModelPerformer.getOscillators().add(localModelByteBufferWavetable);
        short s1 = getGeneratorValue(localHashMap2, 33);
        short s2 = getGeneratorValue(localHashMap2, 34);
        int i8 = getGeneratorValue(localHashMap2, 35);
        int i9 = getGeneratorValue(localHashMap2, 36);
        short s3 = getGeneratorValue(localHashMap2, 37);
        short s4 = getGeneratorValue(localHashMap2, 38);
        float f2;
        ModelIdentifier localModelIdentifier1;
        ModelIdentifier localModelIdentifier2;
        if (i8 != 53536)
        {
          s5 = getGeneratorValue(localHashMap2, 39);
          i8 = (short)(i8 + 60 * s5);
          f2 = -s5 * 128;
          localModelIdentifier1 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
          localModelIdentifier2 = ModelDestination.DESTINATION_EG1_HOLD;
          localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(localModelIdentifier1), f2, new ModelDestination(localModelIdentifier2)));
        }
        if (i9 != 53536)
        {
          s5 = getGeneratorValue(localHashMap2, 40);
          i9 = (short)(i9 + 60 * s5);
          f2 = -s5 * 128;
          localModelIdentifier1 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
          localModelIdentifier2 = ModelDestination.DESTINATION_EG1_DECAY;
          localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(localModelIdentifier1), f2, new ModelDestination(localModelIdentifier2)));
        }
        addTimecentValue(localModelPerformer, ModelDestination.DESTINATION_EG1_DELAY, s1);
        addTimecentValue(localModelPerformer, ModelDestination.DESTINATION_EG1_ATTACK, s2);
        addTimecentValue(localModelPerformer, ModelDestination.DESTINATION_EG1_HOLD, i8);
        addTimecentValue(localModelPerformer, ModelDestination.DESTINATION_EG1_DECAY, i9);
        s3 = (short)(1000 - s3);
        if (s3 < 0) {
          s3 = 0;
        }
        if (s3 > 1000) {
          s3 = 1000;
        }
        addValue(localModelPerformer, ModelDestination.DESTINATION_EG1_SUSTAIN, s3);
        addTimecentValue(localModelPerformer, ModelDestination.DESTINATION_EG1_RELEASE, s4);
        if ((getGeneratorValue(localHashMap2, 11) != 0) || (getGeneratorValue(localHashMap2, 7) != 0))
        {
          s5 = getGeneratorValue(localHashMap2, 25);
          s6 = getGeneratorValue(localHashMap2, 26);
          int i10 = getGeneratorValue(localHashMap2, 27);
          int i12 = getGeneratorValue(localHashMap2, 28);
          int i13 = getGeneratorValue(localHashMap2, 29);
          short s9 = getGeneratorValue(localHashMap2, 30);
          int i14;
          float f3;
          ModelIdentifier localModelIdentifier4;
          ModelIdentifier localModelIdentifier5;
          if (i10 != 53536)
          {
            i14 = getGeneratorValue(localHashMap2, 31);
            i10 = (short)(i10 + 60 * i14);
            f3 = -i14 * 128;
            localModelIdentifier4 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
            localModelIdentifier5 = ModelDestination.DESTINATION_EG2_HOLD;
            localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(localModelIdentifier4), f3, new ModelDestination(localModelIdentifier5)));
          }
          if (i12 != 53536)
          {
            i14 = getGeneratorValue(localHashMap2, 32);
            i12 = (short)(i12 + 60 * i14);
            f3 = -i14 * 128;
            localModelIdentifier4 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
            localModelIdentifier5 = ModelDestination.DESTINATION_EG2_DECAY;
            localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(localModelIdentifier4), f3, new ModelDestination(localModelIdentifier5)));
          }
          addTimecentValue(localModelPerformer, ModelDestination.DESTINATION_EG2_DELAY, s5);
          addTimecentValue(localModelPerformer, ModelDestination.DESTINATION_EG2_ATTACK, s6);
          addTimecentValue(localModelPerformer, ModelDestination.DESTINATION_EG2_HOLD, i10);
          addTimecentValue(localModelPerformer, ModelDestination.DESTINATION_EG2_DECAY, i12);
          if (i13 < 0) {
            i13 = 0;
          }
          if (i13 > 1000) {
            i13 = 1000;
          }
          addValue(localModelPerformer, ModelDestination.DESTINATION_EG2_SUSTAIN, 1000 - i13);
          addTimecentValue(localModelPerformer, ModelDestination.DESTINATION_EG2_RELEASE, s9);
          double d2;
          if (getGeneratorValue(localHashMap2, 11) != 0)
          {
            d2 = getGeneratorValue(localHashMap2, 11);
            localModelIdentifier4 = ModelSource.SOURCE_EG2;
            localModelIdentifier5 = ModelDestination.DESTINATION_FILTER_FREQ;
            localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(localModelIdentifier4), d2, new ModelDestination(localModelIdentifier5)));
          }
          if (getGeneratorValue(localHashMap2, 7) != 0)
          {
            d2 = getGeneratorValue(localHashMap2, 7);
            localModelIdentifier4 = ModelSource.SOURCE_EG2;
            localModelIdentifier5 = ModelDestination.DESTINATION_PITCH;
            localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(localModelIdentifier4), d2, new ModelDestination(localModelIdentifier5)));
          }
        }
        if ((getGeneratorValue(localHashMap2, 10) != 0) || (getGeneratorValue(localHashMap2, 5) != 0) || (getGeneratorValue(localHashMap2, 13) != 0))
        {
          s5 = getGeneratorValue(localHashMap2, 22);
          s6 = getGeneratorValue(localHashMap2, 21);
          addTimecentValue(localModelPerformer, ModelDestination.DESTINATION_LFO1_DELAY, s6);
          addValue(localModelPerformer, ModelDestination.DESTINATION_LFO1_FREQ, s5);
        }
        short s5 = getGeneratorValue(localHashMap2, 24);
        short s6 = getGeneratorValue(localHashMap2, 23);
        addTimecentValue(localModelPerformer, ModelDestination.DESTINATION_LFO2_DELAY, s6);
        addValue(localModelPerformer, ModelDestination.DESTINATION_LFO2_FREQ, s5);
        double d1;
        Object localObject2;
        ModelIdentifier localModelIdentifier3;
        if (getGeneratorValue(localHashMap2, 6) != 0)
        {
          d1 = getGeneratorValue(localHashMap2, 6);
          localObject2 = ModelSource.SOURCE_LFO2;
          localModelIdentifier3 = ModelDestination.DESTINATION_PITCH;
          localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource((ModelIdentifier)localObject2, false, true), d1, new ModelDestination(localModelIdentifier3)));
        }
        if (getGeneratorValue(localHashMap2, 10) != 0)
        {
          d1 = getGeneratorValue(localHashMap2, 10);
          localObject2 = ModelSource.SOURCE_LFO1;
          localModelIdentifier3 = ModelDestination.DESTINATION_FILTER_FREQ;
          localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource((ModelIdentifier)localObject2, false, true), d1, new ModelDestination(localModelIdentifier3)));
        }
        if (getGeneratorValue(localHashMap2, 5) != 0)
        {
          d1 = getGeneratorValue(localHashMap2, 5);
          localObject2 = ModelSource.SOURCE_LFO1;
          localModelIdentifier3 = ModelDestination.DESTINATION_PITCH;
          localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource((ModelIdentifier)localObject2, false, true), d1, new ModelDestination(localModelIdentifier3)));
        }
        if (getGeneratorValue(localHashMap2, 13) != 0)
        {
          d1 = getGeneratorValue(localHashMap2, 13);
          localObject2 = ModelSource.SOURCE_LFO1;
          localModelIdentifier3 = ModelDestination.DESTINATION_GAIN;
          localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource((ModelIdentifier)localObject2, false, true), d1, new ModelDestination(localModelIdentifier3)));
        }
        if (localSF2LayerRegion.getShort(46) != -1)
        {
          d1 = localSF2LayerRegion.getShort(46) / 128.0D;
          addValue(localModelPerformer, ModelDestination.DESTINATION_KEYNUMBER, d1);
        }
        if (localSF2LayerRegion.getShort(47) != -1)
        {
          d1 = localSF2LayerRegion.getShort(47) / 128.0D;
          addValue(localModelPerformer, ModelDestination.DESTINATION_VELOCITY, d1);
        }
        short s8;
        if (getGeneratorValue(localHashMap2, 8) < 13500)
        {
          short s7 = getGeneratorValue(localHashMap2, 8);
          s8 = getGeneratorValue(localHashMap2, 9);
          addValue(localModelPerformer, ModelDestination.DESTINATION_FILTER_FREQ, s7);
          addValue(localModelPerformer, ModelDestination.DESTINATION_FILTER_Q, s8);
        }
        int i11 = 100 * getGeneratorValue(localHashMap2, 51);
        i11 += getGeneratorValue(localHashMap2, 52);
        if (i11 != 0) {
          addValue(localModelPerformer, ModelDestination.DESTINATION_PITCH, (short)i11);
        }
        if (getGeneratorValue(localHashMap2, 17) != 0)
        {
          s8 = getGeneratorValue(localHashMap2, 17);
          addValue(localModelPerformer, ModelDestination.DESTINATION_PAN, s8);
        }
        if (getGeneratorValue(localHashMap2, 48) != 0)
        {
          s8 = getGeneratorValue(localHashMap2, 48);
          addValue(localModelPerformer, ModelDestination.DESTINATION_GAIN, -0.376287F * s8);
        }
        if (getGeneratorValue(localHashMap2, 15) != 0)
        {
          s8 = getGeneratorValue(localHashMap2, 15);
          addValue(localModelPerformer, ModelDestination.DESTINATION_CHORUS, s8);
        }
        if (getGeneratorValue(localHashMap2, 16) != 0)
        {
          s8 = getGeneratorValue(localHashMap2, 16);
          addValue(localModelPerformer, ModelDestination.DESTINATION_REVERB, s8);
        }
        if (getGeneratorValue(localHashMap2, 56) != 100)
        {
          s8 = getGeneratorValue(localHashMap2, 56);
          if (s8 == 0)
          {
            localObject2 = ModelDestination.DESTINATION_PITCH;
            localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(null, i6 * 100, new ModelDestination((ModelIdentifier)localObject2)));
          }
          else
          {
            localObject2 = ModelDestination.DESTINATION_PITCH;
            localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(null, i6 * (100 - s8), new ModelDestination((ModelIdentifier)localObject2)));
          }
          localObject2 = ModelSource.SOURCE_NOTEON_KEYNUMBER;
          localModelIdentifier3 = ModelDestination.DESTINATION_PITCH;
          localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource((ModelIdentifier)localObject2), 128 * s8, new ModelDestination(localModelIdentifier3)));
        }
        localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_NOTEON_VELOCITY, new ModelTransform()
        {
          public double transform(double paramAnonymousDouble)
          {
            if (paramAnonymousDouble < 0.5D) {
              return 1.0D - paramAnonymousDouble * 2.0D;
            }
            return 0.0D;
          }
        }), -2400.0D, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ)));
        localModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO2, false, true, 0), new ModelSource(new ModelIdentifier("midi_cc", "1", 0), false, false, 0), 50.0D, new ModelDestination(ModelDestination.DESTINATION_PITCH)));
        if (localSF2Layer.getGlobalRegion() != null)
        {
          localIterator4 = localSF2Layer.getGlobalRegion().getModulators().iterator();
          while (localIterator4.hasNext())
          {
            localObject2 = (SF2Modulator)localIterator4.next();
            convertModulator(localModelPerformer, (SF2Modulator)localObject2);
          }
        }
        Iterator localIterator4 = localSF2LayerRegion.getModulators().iterator();
        while (localIterator4.hasNext())
        {
          localObject2 = (SF2Modulator)localIterator4.next();
          convertModulator(localModelPerformer, (SF2Modulator)localObject2);
        }
        if (localSF2GlobalRegion1 != null)
        {
          localIterator4 = localSF2GlobalRegion1.getModulators().iterator();
          while (localIterator4.hasNext())
          {
            localObject2 = (SF2Modulator)localIterator4.next();
            convertModulator(localModelPerformer, (SF2Modulator)localObject2);
          }
        }
        localIterator4 = localSF2InstrumentRegion2.getModulators().iterator();
        while (localIterator4.hasNext())
        {
          localObject2 = (SF2Modulator)localIterator4.next();
          convertModulator(localModelPerformer, (SF2Modulator)localObject2);
        }
      }
    }
    return (ModelPerformer[])localObject1;
  }
  
  private void convertModulator(ModelPerformer paramModelPerformer, SF2Modulator paramSF2Modulator)
  {
    ModelSource localModelSource1 = convertSource(paramSF2Modulator.getSourceOperator());
    ModelSource localModelSource2 = convertSource(paramSF2Modulator.getAmountSourceOperator());
    if ((localModelSource1 == null) && (paramSF2Modulator.getSourceOperator() != 0)) {
      return;
    }
    if ((localModelSource2 == null) && (paramSF2Modulator.getAmountSourceOperator() != 0)) {
      return;
    }
    double d = paramSF2Modulator.getAmount();
    double[] arrayOfDouble = new double[1];
    ModelSource[] arrayOfModelSource = new ModelSource[1];
    arrayOfDouble[0] = 1.0D;
    ModelDestination localModelDestination = convertDestination(paramSF2Modulator.getDestinationOperator(), arrayOfDouble, arrayOfModelSource);
    d *= arrayOfDouble[0];
    if (localModelDestination == null) {
      return;
    }
    if (paramSF2Modulator.getTransportOperator() == 2) {
      ((ModelStandardTransform)localModelDestination.getTransform()).setTransform(4);
    }
    ModelConnectionBlock localModelConnectionBlock = new ModelConnectionBlock(localModelSource1, localModelSource2, d, localModelDestination);
    if (arrayOfModelSource[0] != null) {
      localModelConnectionBlock.addSource(arrayOfModelSource[0]);
    }
    paramModelPerformer.getConnectionBlocks().add(localModelConnectionBlock);
  }
  
  private static ModelSource convertSource(int paramInt)
  {
    if (paramInt == 0) {
      return null;
    }
    ModelIdentifier localModelIdentifier = null;
    int i = paramInt & 0x7F;
    if ((paramInt & 0x80) != 0)
    {
      localModelIdentifier = new ModelIdentifier("midi_cc", Integer.toString(i));
    }
    else
    {
      if (i == 2) {
        localModelIdentifier = ModelSource.SOURCE_NOTEON_VELOCITY;
      }
      if (i == 3) {
        localModelIdentifier = ModelSource.SOURCE_NOTEON_KEYNUMBER;
      }
      if (i == 10) {
        localModelIdentifier = ModelSource.SOURCE_MIDI_POLY_PRESSURE;
      }
      if (i == 13) {
        localModelIdentifier = ModelSource.SOURCE_MIDI_CHANNEL_PRESSURE;
      }
      if (i == 14) {
        localModelIdentifier = ModelSource.SOURCE_MIDI_PITCH;
      }
      if (i == 16) {
        localModelIdentifier = new ModelIdentifier("midi_rpn", "0");
      }
    }
    if (localModelIdentifier == null) {
      return null;
    }
    ModelSource localModelSource = new ModelSource(localModelIdentifier);
    ModelStandardTransform localModelStandardTransform = (ModelStandardTransform)localModelSource.getTransform();
    if ((0x100 & paramInt) != 0) {
      localModelStandardTransform.setDirection(true);
    } else {
      localModelStandardTransform.setDirection(false);
    }
    if ((0x200 & paramInt) != 0) {
      localModelStandardTransform.setPolarity(true);
    } else {
      localModelStandardTransform.setPolarity(false);
    }
    if ((0x400 & paramInt) != 0) {
      localModelStandardTransform.setTransform(1);
    }
    if ((0x800 & paramInt) != 0) {
      localModelStandardTransform.setTransform(2);
    }
    if ((0xC00 & paramInt) != 0) {
      localModelStandardTransform.setTransform(3);
    }
    return localModelSource;
  }
  
  static ModelDestination convertDestination(int paramInt, double[] paramArrayOfDouble, ModelSource[] paramArrayOfModelSource)
  {
    ModelIdentifier localModelIdentifier = null;
    switch (paramInt)
    {
    case 8: 
      localModelIdentifier = ModelDestination.DESTINATION_FILTER_FREQ;
      break;
    case 9: 
      localModelIdentifier = ModelDestination.DESTINATION_FILTER_Q;
      break;
    case 15: 
      localModelIdentifier = ModelDestination.DESTINATION_CHORUS;
      break;
    case 16: 
      localModelIdentifier = ModelDestination.DESTINATION_REVERB;
      break;
    case 17: 
      localModelIdentifier = ModelDestination.DESTINATION_PAN;
      break;
    case 21: 
      localModelIdentifier = ModelDestination.DESTINATION_LFO1_DELAY;
      break;
    case 22: 
      localModelIdentifier = ModelDestination.DESTINATION_LFO1_FREQ;
      break;
    case 23: 
      localModelIdentifier = ModelDestination.DESTINATION_LFO2_DELAY;
      break;
    case 24: 
      localModelIdentifier = ModelDestination.DESTINATION_LFO2_FREQ;
      break;
    case 25: 
      localModelIdentifier = ModelDestination.DESTINATION_EG2_DELAY;
      break;
    case 26: 
      localModelIdentifier = ModelDestination.DESTINATION_EG2_ATTACK;
      break;
    case 27: 
      localModelIdentifier = ModelDestination.DESTINATION_EG2_HOLD;
      break;
    case 28: 
      localModelIdentifier = ModelDestination.DESTINATION_EG2_DECAY;
      break;
    case 29: 
      localModelIdentifier = ModelDestination.DESTINATION_EG2_SUSTAIN;
      paramArrayOfDouble[0] = -1.0D;
      break;
    case 30: 
      localModelIdentifier = ModelDestination.DESTINATION_EG2_RELEASE;
      break;
    case 33: 
      localModelIdentifier = ModelDestination.DESTINATION_EG1_DELAY;
      break;
    case 34: 
      localModelIdentifier = ModelDestination.DESTINATION_EG1_ATTACK;
      break;
    case 35: 
      localModelIdentifier = ModelDestination.DESTINATION_EG1_HOLD;
      break;
    case 36: 
      localModelIdentifier = ModelDestination.DESTINATION_EG1_DECAY;
      break;
    case 37: 
      localModelIdentifier = ModelDestination.DESTINATION_EG1_SUSTAIN;
      paramArrayOfDouble[0] = -1.0D;
      break;
    case 38: 
      localModelIdentifier = ModelDestination.DESTINATION_EG1_RELEASE;
      break;
    case 46: 
      localModelIdentifier = ModelDestination.DESTINATION_KEYNUMBER;
      break;
    case 47: 
      localModelIdentifier = ModelDestination.DESTINATION_VELOCITY;
      break;
    case 51: 
      paramArrayOfDouble[0] = 100.0D;
      localModelIdentifier = ModelDestination.DESTINATION_PITCH;
      break;
    case 52: 
      localModelIdentifier = ModelDestination.DESTINATION_PITCH;
      break;
    case 48: 
      localModelIdentifier = ModelDestination.DESTINATION_GAIN;
      paramArrayOfDouble[0] = -0.3762870132923126D;
      break;
    case 6: 
      localModelIdentifier = ModelDestination.DESTINATION_PITCH;
      paramArrayOfModelSource[0] = new ModelSource(ModelSource.SOURCE_LFO2, false, true);
      break;
    case 5: 
      localModelIdentifier = ModelDestination.DESTINATION_PITCH;
      paramArrayOfModelSource[0] = new ModelSource(ModelSource.SOURCE_LFO1, false, true);
      break;
    case 10: 
      localModelIdentifier = ModelDestination.DESTINATION_FILTER_FREQ;
      paramArrayOfModelSource[0] = new ModelSource(ModelSource.SOURCE_LFO1, false, true);
      break;
    case 13: 
      localModelIdentifier = ModelDestination.DESTINATION_GAIN;
      paramArrayOfDouble[0] = -0.3762870132923126D;
      paramArrayOfModelSource[0] = new ModelSource(ModelSource.SOURCE_LFO1, false, true);
      break;
    case 7: 
      localModelIdentifier = ModelDestination.DESTINATION_PITCH;
      paramArrayOfModelSource[0] = new ModelSource(ModelSource.SOURCE_EG2, false, true);
      break;
    case 11: 
      localModelIdentifier = ModelDestination.DESTINATION_FILTER_FREQ;
      paramArrayOfModelSource[0] = new ModelSource(ModelSource.SOURCE_EG2, false, true);
      break;
    }
    if (localModelIdentifier != null) {
      return new ModelDestination(localModelIdentifier);
    }
    return null;
  }
  
  private void addTimecentValue(ModelPerformer paramModelPerformer, ModelIdentifier paramModelIdentifier, short paramShort)
  {
    double d;
    if (paramShort == 53536) {
      d = Double.NEGATIVE_INFINITY;
    } else {
      d = paramShort;
    }
    paramModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(d, new ModelDestination(paramModelIdentifier)));
  }
  
  private void addValue(ModelPerformer paramModelPerformer, ModelIdentifier paramModelIdentifier, short paramShort)
  {
    double d = paramShort;
    paramModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(d, new ModelDestination(paramModelIdentifier)));
  }
  
  private void addValue(ModelPerformer paramModelPerformer, ModelIdentifier paramModelIdentifier, double paramDouble)
  {
    double d = paramDouble;
    paramModelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(d, new ModelDestination(paramModelIdentifier)));
  }
  
  private short getGeneratorValue(Map<Integer, Short> paramMap, int paramInt)
  {
    if (paramMap.containsKey(Integer.valueOf(paramInt))) {
      return ((Short)paramMap.get(Integer.valueOf(paramInt))).shortValue();
    }
    return SF2Region.getDefaultValue(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SF2Instrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */