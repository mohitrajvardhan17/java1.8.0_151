package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Patch;
import javax.sound.midi.ShortMessage;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class SoftMainMixer
{
  public static final int CHANNEL_LEFT = 0;
  public static final int CHANNEL_RIGHT = 1;
  public static final int CHANNEL_MONO = 2;
  public static final int CHANNEL_DELAY_LEFT = 3;
  public static final int CHANNEL_DELAY_RIGHT = 4;
  public static final int CHANNEL_DELAY_MONO = 5;
  public static final int CHANNEL_EFFECT1 = 6;
  public static final int CHANNEL_EFFECT2 = 7;
  public static final int CHANNEL_DELAY_EFFECT1 = 8;
  public static final int CHANNEL_DELAY_EFFECT2 = 9;
  public static final int CHANNEL_LEFT_DRY = 10;
  public static final int CHANNEL_RIGHT_DRY = 11;
  public static final int CHANNEL_SCRATCH1 = 12;
  public static final int CHANNEL_SCRATCH2 = 13;
  boolean active_sensing_on = false;
  private long msec_last_activity = -1L;
  private boolean pusher_silent = false;
  private int pusher_silent_count = 0;
  private long sample_pos = 0L;
  boolean readfully = true;
  private final Object control_mutex;
  private SoftSynthesizer synth;
  private float samplerate = 44100.0F;
  private int nrofchannels = 2;
  private SoftVoice[] voicestatus = null;
  private SoftAudioBuffer[] buffers;
  private SoftReverb reverb;
  private SoftAudioProcessor chorus;
  private SoftAudioProcessor agc;
  private long msec_buffer_len = 0L;
  private int buffer_len = 0;
  TreeMap<Long, Object> midimessages = new TreeMap();
  private int delay_midievent = 0;
  private int max_delay_midievent = 0;
  double last_volume_left = 1.0D;
  double last_volume_right = 1.0D;
  private double[] co_master_balance = new double[1];
  private double[] co_master_volume = new double[1];
  private double[] co_master_coarse_tuning = new double[1];
  private double[] co_master_fine_tuning = new double[1];
  private AudioInputStream ais;
  private Set<SoftChannelMixerContainer> registeredMixers = null;
  private Set<ModelChannelMixer> stoppedMixers = null;
  private SoftChannelMixerContainer[] cur_registeredMixers = null;
  SoftControl co_master = new SoftControl()
  {
    double[] balance = co_master_balance;
    double[] volume = co_master_volume;
    double[] coarse_tuning = co_master_coarse_tuning;
    double[] fine_tuning = co_master_fine_tuning;
    
    public double[] get(int paramAnonymousInt, String paramAnonymousString)
    {
      if (paramAnonymousString == null) {
        return null;
      }
      if (paramAnonymousString.equals("balance")) {
        return balance;
      }
      if (paramAnonymousString.equals("volume")) {
        return volume;
      }
      if (paramAnonymousString.equals("coarse_tuning")) {
        return coarse_tuning;
      }
      if (paramAnonymousString.equals("fine_tuning")) {
        return fine_tuning;
      }
      return null;
    }
  };
  
  private void processSystemExclusiveMessage(byte[] paramArrayOfByte)
  {
    synchronized (synth.control_mutex)
    {
      activity();
      int i;
      int j;
      int k;
      int i1;
      int i6;
      if ((paramArrayOfByte[1] & 0xFF) == 126)
      {
        i = paramArrayOfByte[2] & 0xFF;
        if ((i == 127) || (i == synth.getDeviceID()))
        {
          j = paramArrayOfByte[3] & 0xFF;
          switch (j)
          {
          case 8: 
            k = paramArrayOfByte[4] & 0xFF;
            SoftTuning localSoftTuning;
            switch (k)
            {
            case 1: 
              localSoftTuning = synth.getTuning(new Patch(0, paramArrayOfByte[5] & 0xFF));
              localSoftTuning.load(paramArrayOfByte);
              break;
            case 4: 
            case 5: 
            case 6: 
            case 7: 
              localSoftTuning = synth.getTuning(new Patch(paramArrayOfByte[5] & 0xFF, paramArrayOfByte[6] & 0xFF));
              localSoftTuning.load(paramArrayOfByte);
              break;
            case 8: 
            case 9: 
              localSoftTuning = new SoftTuning(paramArrayOfByte);
              i1 = (paramArrayOfByte[5] & 0xFF) * 16384 + (paramArrayOfByte[6] & 0xFF) * 128 + (paramArrayOfByte[7] & 0xFF);
              SoftChannel[] arrayOfSoftChannel1 = synth.channels;
              for (i6 = 0; i6 < arrayOfSoftChannel1.length; i6++) {
                if ((i1 & 1 << i6) != 0) {
                  tuning = localSoftTuning;
                }
              }
            }
            break;
          case 9: 
            k = paramArrayOfByte[4] & 0xFF;
            switch (k)
            {
            case 1: 
              synth.setGeneralMidiMode(1);
              reset();
              break;
            case 2: 
              synth.setGeneralMidiMode(0);
              reset();
              break;
            case 3: 
              synth.setGeneralMidiMode(2);
              reset();
            }
            break;
          case 10: 
            k = paramArrayOfByte[4] & 0xFF;
            switch (k)
            {
            case 1: 
              if (synth.getGeneralMidiMode() == 0) {
                synth.setGeneralMidiMode(1);
              }
              synth.voice_allocation_mode = 1;
              reset();
              break;
            case 2: 
              synth.setGeneralMidiMode(0);
              synth.voice_allocation_mode = 0;
              reset();
              break;
            case 3: 
              synth.voice_allocation_mode = 0;
              break;
            case 4: 
              synth.voice_allocation_mode = 1;
            }
            break;
          }
        }
      }
      if ((paramArrayOfByte[1] & 0xFF) == Byte.MAX_VALUE)
      {
        i = paramArrayOfByte[2] & 0xFF;
        if ((i == 127) || (i == synth.getDeviceID()))
        {
          j = paramArrayOfByte[3] & 0xFF;
          int i4;
          int i8;
          Object localObject1;
          int i7;
          int i10;
          switch (j)
          {
          case 4: 
            k = paramArrayOfByte[4] & 0xFF;
            switch (k)
            {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
              int m = (paramArrayOfByte[5] & 0x7F) + (paramArrayOfByte[6] & 0x7F) * 128;
              if (k == 1) {
                setVolume(m);
              } else if (k == 2) {
                setBalance(m);
              } else if (k == 3) {
                setFineTuning(m);
              } else if (k == 4) {
                setCoarseTuning(m);
              }
              break;
            case 5: 
              i1 = 5;
              i4 = paramArrayOfByte[(i1++)] & 0xFF;
              i6 = paramArrayOfByte[(i1++)] & 0xFF;
              i8 = paramArrayOfByte[(i1++)] & 0xFF;
              int[] arrayOfInt2 = new int[i4];
              for (int i11 = 0; i11 < i4; i11++)
              {
                int i12 = paramArrayOfByte[(i1++)] & 0xFF;
                int i13 = paramArrayOfByte[(i1++)] & 0xFF;
                arrayOfInt2[i11] = (i12 * 128 + i13);
              }
              i11 = (paramArrayOfByte.length - 1 - i1) / (i6 + i8);
              long[] arrayOfLong1 = new long[i11];
              long[] arrayOfLong2 = new long[i11];
              for (int i14 = 0; i14 < i11; i14++)
              {
                arrayOfLong2[i14] = 0L;
                for (int i15 = 0; i15 < i6; i15++) {
                  arrayOfLong1[i14] = (arrayOfLong1[i14] * 128L + (paramArrayOfByte[(i1++)] & 0xFF));
                }
                for (i15 = 0; i15 < i8; i15++) {
                  arrayOfLong2[i14] = (arrayOfLong2[i14] * 128L + (paramArrayOfByte[(i1++)] & 0xFF));
                }
              }
              globalParameterControlChange(arrayOfInt2, arrayOfLong1, arrayOfLong2);
            }
            break;
          case 8: 
            k = paramArrayOfByte[4] & 0xFF;
            SoftVoice[] arrayOfSoftVoice1;
            switch (k)
            {
            case 2: 
              localObject1 = synth.getTuning(new Patch(0, paramArrayOfByte[5] & 0xFF));
              ((SoftTuning)localObject1).load(paramArrayOfByte);
              arrayOfSoftVoice1 = synth.getVoices();
              for (i4 = 0; i4 < arrayOfSoftVoice1.length; i4++) {
                if ((active) && (tuning == localObject1)) {
                  arrayOfSoftVoice1[i4].updateTuning((SoftTuning)localObject1);
                }
              }
              break;
            case 7: 
              localObject1 = synth.getTuning(new Patch(paramArrayOfByte[5] & 0xFF, paramArrayOfByte[6] & 0xFF));
              ((SoftTuning)localObject1).load(paramArrayOfByte);
              arrayOfSoftVoice1 = synth.getVoices();
              for (i4 = 0; i4 < arrayOfSoftVoice1.length; i4++) {
                if ((active) && (tuning == localObject1)) {
                  arrayOfSoftVoice1[i4].updateTuning((SoftTuning)localObject1);
                }
              }
              break;
            case 8: 
            case 9: 
              localObject1 = new SoftTuning(paramArrayOfByte);
              int i2 = (paramArrayOfByte[5] & 0xFF) * 16384 + (paramArrayOfByte[6] & 0xFF) * 128 + (paramArrayOfByte[7] & 0xFF);
              SoftChannel[] arrayOfSoftChannel2 = synth.channels;
              for (i6 = 0; i6 < arrayOfSoftChannel2.length; i6++) {
                if ((i2 & 1 << i6) != 0) {
                  tuning = ((SoftTuning)localObject1);
                }
              }
              SoftVoice[] arrayOfSoftVoice2 = synth.getVoices();
              for (i8 = 0; i8 < arrayOfSoftVoice2.length; i8++) {
                if ((active) && ((i2 & 1 << channel) != 0)) {
                  arrayOfSoftVoice2[i8].updateTuning((SoftTuning)localObject1);
                }
              }
            }
            break;
          case 9: 
            k = paramArrayOfByte[4] & 0xFF;
            int[] arrayOfInt1;
            int i5;
            SoftChannel localSoftChannel2;
            switch (k)
            {
            case 1: 
              localObject1 = new int[(paramArrayOfByte.length - 7) / 2];
              arrayOfInt1 = new int[(paramArrayOfByte.length - 7) / 2];
              i5 = 0;
              for (i7 = 6; i7 < paramArrayOfByte.length - 1; i7 += 2)
              {
                paramArrayOfByte[i7] &= 0xFF;
                arrayOfInt1[i5] = (paramArrayOfByte[(i7 + 1)] & 0xFF);
                i5++;
              }
              i7 = paramArrayOfByte[5] & 0xFF;
              localSoftChannel2 = synth.channels[i7];
              localSoftChannel2.mapChannelPressureToDestination((int[])localObject1, arrayOfInt1);
              break;
            case 2: 
              localObject1 = new int[(paramArrayOfByte.length - 7) / 2];
              arrayOfInt1 = new int[(paramArrayOfByte.length - 7) / 2];
              i5 = 0;
              for (i7 = 6; i7 < paramArrayOfByte.length - 1; i7 += 2)
              {
                paramArrayOfByte[i7] &= 0xFF;
                arrayOfInt1[i5] = (paramArrayOfByte[(i7 + 1)] & 0xFF);
                i5++;
              }
              i7 = paramArrayOfByte[5] & 0xFF;
              localSoftChannel2 = synth.channels[i7];
              localSoftChannel2.mapPolyPressureToDestination((int[])localObject1, arrayOfInt1);
              break;
            case 3: 
              localObject1 = new int[(paramArrayOfByte.length - 7) / 2];
              arrayOfInt1 = new int[(paramArrayOfByte.length - 7) / 2];
              i5 = 0;
              for (i7 = 7; i7 < paramArrayOfByte.length - 1; i7 += 2)
              {
                paramArrayOfByte[i7] &= 0xFF;
                arrayOfInt1[i5] = (paramArrayOfByte[(i7 + 1)] & 0xFF);
                i5++;
              }
              i7 = paramArrayOfByte[5] & 0xFF;
              localSoftChannel2 = synth.channels[i7];
              i10 = paramArrayOfByte[6] & 0xFF;
              localSoftChannel2.mapControlToDestination(i10, (int[])localObject1, arrayOfInt1);
            }
            break;
          case 10: 
            k = paramArrayOfByte[4] & 0xFF;
            switch (k)
            {
            case 1: 
              int n = paramArrayOfByte[5] & 0xFF;
              int i3 = paramArrayOfByte[6] & 0xFF;
              SoftChannel localSoftChannel1 = synth.channels[n];
              for (i7 = 7; i7 < paramArrayOfByte.length - 1; i7 += 2)
              {
                int i9 = paramArrayOfByte[i7] & 0xFF;
                i10 = paramArrayOfByte[(i7 + 1)] & 0xFF;
                localSoftChannel1.controlChangePerNote(i3, i9, i10);
              }
              break;
            }
            break;
          }
        }
      }
    }
  }
  
  private void processMessages(long paramLong)
  {
    Iterator localIterator = midimessages.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (((Long)localEntry.getKey()).longValue() >= paramLong + msec_buffer_len) {
        return;
      }
      long l = ((Long)localEntry.getKey()).longValue() - paramLong;
      delay_midievent = ((int)(l * (samplerate / 1000000.0D) + 0.5D));
      if (delay_midievent > max_delay_midievent) {
        delay_midievent = max_delay_midievent;
      }
      if (delay_midievent < 0) {
        delay_midievent = 0;
      }
      processMessage(localEntry.getValue());
      localIterator.remove();
    }
    delay_midievent = 0;
  }
  
  void processAudioBuffers()
  {
    if ((synth.weakstream != null) && (synth.weakstream.silent_samples != 0L))
    {
      sample_pos += synth.weakstream.silent_samples;
      synth.weakstream.silent_samples = 0L;
    }
    for (int i = 0; i < buffers.length; i++) {
      if ((i != 3) && (i != 4) && (i != 5) && (i != 8) && (i != 9)) {
        buffers[i].clear();
      }
    }
    if (!buffers[3].isSilent()) {
      buffers[0].swap(buffers[3]);
    }
    if (!buffers[4].isSilent()) {
      buffers[1].swap(buffers[4]);
    }
    if (!buffers[5].isSilent()) {
      buffers[2].swap(buffers[5]);
    }
    if (!buffers[8].isSilent()) {
      buffers[6].swap(buffers[8]);
    }
    if (!buffers[9].isSilent()) {
      buffers[7].swap(buffers[9]);
    }
    double d1;
    double d2;
    SoftChannelMixerContainer[] arrayOfSoftChannelMixerContainer1;
    synchronized (control_mutex)
    {
      long l = (sample_pos * (1000000.0D / samplerate));
      processMessages(l);
      if ((active_sensing_on) && (l - msec_last_activity > 1000000L))
      {
        active_sensing_on = false;
        for (SoftChannel localSoftChannel : synth.channels) {
          localSoftChannel.allSoundOff();
        }
      }
      for (int n = 0; n < voicestatus.length; n++) {
        if (voicestatus[n].active) {
          voicestatus[n].processControlLogic();
        }
      }
      sample_pos += buffer_len;
      double d3 = co_master_volume[0];
      d1 = d3;
      d2 = d3;
      double d4 = co_master_balance[0];
      if (d4 > 0.5D) {
        d1 *= (1.0D - d4) * 2.0D;
      } else {
        d2 *= d4 * 2.0D;
      }
      chorus.processControlLogic();
      reverb.processControlLogic();
      agc.processControlLogic();
      if ((cur_registeredMixers == null) && (registeredMixers != null))
      {
        cur_registeredMixers = new SoftChannelMixerContainer[registeredMixers.size()];
        registeredMixers.toArray(cur_registeredMixers);
      }
      arrayOfSoftChannelMixerContainer1 = cur_registeredMixers;
      if ((arrayOfSoftChannelMixerContainer1 != null) && (arrayOfSoftChannelMixerContainer1.length == 0)) {
        arrayOfSoftChannelMixerContainer1 = null;
      }
    }
    Object localObject1;
    Object localObject2;
    if (arrayOfSoftChannelMixerContainer1 != null)
    {
      ??? = buffers[0];
      localObject1 = buffers[1];
      SoftAudioBuffer localSoftAudioBuffer1 = buffers[2];
      localObject2 = buffers[3];
      SoftAudioBuffer localSoftAudioBuffer2 = buffers[4];
      SoftAudioBuffer localSoftAudioBuffer3 = buffers[5];
      int i7 = buffers[0].getSize();
      float[][] arrayOfFloat2 = new float[nrofchannels][];
      float[][] arrayOfFloat3 = new float[nrofchannels][];
      arrayOfFloat3[0] = ((SoftAudioBuffer)???).array();
      if (nrofchannels != 1) {
        arrayOfFloat3[1] = ((SoftAudioBuffer)localObject1).array();
      }
      for (SoftChannelMixerContainer localSoftChannelMixerContainer : arrayOfSoftChannelMixerContainer1)
      {
        buffers[0] = buffers[0];
        buffers[1] = buffers[1];
        buffers[2] = buffers[2];
        buffers[3] = buffers[3];
        buffers[4] = buffers[4];
        buffers[5] = buffers[5];
        buffers[0].clear();
        buffers[1].clear();
        buffers[2].clear();
        if (!buffers[3].isSilent()) {
          buffers[0].swap(buffers[3]);
        }
        if (!buffers[4].isSilent()) {
          buffers[1].swap(buffers[4]);
        }
        if (!buffers[5].isSilent()) {
          buffers[2].swap(buffers[5]);
        }
        arrayOfFloat2[0] = buffers[0].array();
        if (nrofchannels != 1) {
          arrayOfFloat2[1] = buffers[1].array();
        }
        int i10 = 0;
        for (int i11 = 0; i11 < voicestatus.length; i11++) {
          if ((voicestatus[i11].active) && (voicestatus[i11].channelmixer == mixer))
          {
            voicestatus[i11].processAudioLogic(buffers);
            i10 = 1;
          }
        }
        float[] arrayOfFloat5;
        int i14;
        if (!buffers[2].isSilent())
        {
          float[] arrayOfFloat4 = buffers[2].array();
          arrayOfFloat5 = buffers[0].array();
          if (nrofchannels != 1)
          {
            float[] arrayOfFloat6 = buffers[1].array();
            for (i14 = 0; i14 < i7; i14++)
            {
              float f4 = arrayOfFloat4[i14];
              arrayOfFloat5[i14] += f4;
              arrayOfFloat6[i14] += f4;
            }
          }
          else
          {
            for (int i13 = 0; i13 < i7; i13++) {
              arrayOfFloat5[i13] += arrayOfFloat4[i13];
            }
          }
        }
        if (!mixer.process(arrayOfFloat2, 0, i7)) {
          synchronized (control_mutex)
          {
            registeredMixers.remove(localSoftChannelMixerContainer);
            cur_registeredMixers = null;
          }
        }
        for (int i12 = 0; i12 < arrayOfFloat2.length; i12++)
        {
          arrayOfFloat5 = arrayOfFloat2[i12];
          float[] arrayOfFloat7 = arrayOfFloat3[i12];
          for (i14 = 0; i14 < i7; i14++) {
            arrayOfFloat7[i14] += arrayOfFloat5[i14];
          }
        }
        if (i10 == 0) {
          synchronized (control_mutex)
          {
            if ((stoppedMixers != null) && (stoppedMixers.contains(localSoftChannelMixerContainer)))
            {
              stoppedMixers.remove(localSoftChannelMixerContainer);
              mixer.stop();
            }
          }
        }
      }
      buffers[0] = ???;
      buffers[1] = localObject1;
      buffers[2] = localSoftAudioBuffer1;
      buffers[3] = localObject2;
      buffers[4] = localSoftAudioBuffer2;
      buffers[5] = localSoftAudioBuffer3;
    }
    for (int j = 0; j < voicestatus.length; j++) {
      if ((voicestatus[j].active) && (voicestatus[j].channelmixer == null)) {
        voicestatus[j].processAudioLogic(buffers);
      }
    }
    float[] arrayOfFloat1;
    int m;
    if (!buffers[2].isSilent())
    {
      arrayOfFloat1 = buffers[2].array();
      localObject1 = buffers[0].array();
      m = buffers[0].getSize();
      if (nrofchannels != 1)
      {
        localObject2 = buffers[1].array();
        for (int i3 = 0; i3 < m; i3++)
        {
          float f3 = arrayOfFloat1[i3];
          localObject1[i3] += f3;
          localObject2[i3] += f3;
        }
      }
      else
      {
        for (int i1 = 0; i1 < m; i1++) {
          localObject1[i1] += arrayOfFloat1[i1];
        }
      }
    }
    if (synth.chorus_on) {
      chorus.processAudio();
    }
    if (synth.reverb_on) {
      reverb.processAudio();
    }
    if (nrofchannels == 1) {
      d1 = (d1 + d2) / 2.0D;
    }
    float f1;
    if ((last_volume_left != d1) || (last_volume_right != d2))
    {
      arrayOfFloat1 = buffers[0].array();
      localObject1 = buffers[1].array();
      m = buffers[0].getSize();
      f1 = (float)(last_volume_left * last_volume_left);
      float f2 = (float)((d1 * d1 - f1) / m);
      for (int i6 = 0; i6 < m; i6++)
      {
        f1 += f2;
        arrayOfFloat1[i6] *= f1;
      }
      if (nrofchannels != 1)
      {
        f1 = (float)(last_volume_right * last_volume_right);
        f2 = (float)((d2 * d2 - f1) / m);
        for (i6 = 0; i6 < m; i6++)
        {
          f1 += f2;
          int tmp1770_1768 = i6;
          Object tmp1770_1766 = localObject1;
          tmp1770_1766[tmp1770_1768] = ((float)(tmp1770_1766[tmp1770_1768] * d2));
        }
      }
      last_volume_left = d1;
      last_volume_right = d2;
    }
    else if ((d1 != 1.0D) || (d2 != 1.0D))
    {
      arrayOfFloat1 = buffers[0].array();
      localObject1 = buffers[1].array();
      m = buffers[0].getSize();
      f1 = (float)(d1 * d1);
      for (int i4 = 0; i4 < m; i4++) {
        arrayOfFloat1[i4] *= f1;
      }
      if (nrofchannels != 1)
      {
        f1 = (float)(d2 * d2);
        for (i4 = 0; i4 < m; i4++) {
          localObject1[i4] *= f1;
        }
      }
    }
    if ((buffers[0].isSilent()) && (buffers[1].isSilent()))
    {
      int k;
      synchronized (control_mutex)
      {
        k = midimessages.size();
      }
      if (k == 0)
      {
        pusher_silent_count += 1;
        if (pusher_silent_count > 5)
        {
          pusher_silent_count = 0;
          synchronized (control_mutex)
          {
            pusher_silent = true;
            if (synth.weakstream != null) {
              synth.weakstream.setInputStream(null);
            }
          }
        }
      }
    }
    else
    {
      pusher_silent_count = 0;
    }
    if (synth.agc_on) {
      agc.processAudio();
    }
  }
  
  public void activity()
  {
    long l = 0L;
    if (pusher_silent)
    {
      pusher_silent = false;
      if (synth.weakstream != null)
      {
        synth.weakstream.setInputStream(ais);
        l = synth.weakstream.silent_samples;
      }
    }
    msec_last_activity = (((sample_pos + l) * (1000000.0D / samplerate)));
  }
  
  public void stopMixer(ModelChannelMixer paramModelChannelMixer)
  {
    if (stoppedMixers == null) {
      stoppedMixers = new HashSet();
    }
    stoppedMixers.add(paramModelChannelMixer);
  }
  
  public void registerMixer(ModelChannelMixer paramModelChannelMixer)
  {
    if (registeredMixers == null) {
      registeredMixers = new HashSet();
    }
    SoftChannelMixerContainer localSoftChannelMixerContainer = new SoftChannelMixerContainer(null);
    buffers = new SoftAudioBuffer[6];
    for (int i = 0; i < buffers.length; i++) {
      buffers[i] = new SoftAudioBuffer(buffer_len, synth.getFormat());
    }
    mixer = paramModelChannelMixer;
    registeredMixers.add(localSoftChannelMixerContainer);
    cur_registeredMixers = null;
  }
  
  public SoftMainMixer(SoftSynthesizer paramSoftSynthesizer)
  {
    synth = paramSoftSynthesizer;
    sample_pos = 0L;
    co_master_balance[0] = 0.5D;
    co_master_volume[0] = 1.0D;
    co_master_coarse_tuning[0] = 0.5D;
    co_master_fine_tuning[0] = 0.5D;
    msec_buffer_len = ((1000000.0D / paramSoftSynthesizer.getControlRate()));
    samplerate = paramSoftSynthesizer.getFormat().getSampleRate();
    nrofchannels = paramSoftSynthesizer.getFormat().getChannels();
    int i = (int)(paramSoftSynthesizer.getFormat().getSampleRate() / paramSoftSynthesizer.getControlRate());
    buffer_len = i;
    max_delay_midievent = i;
    control_mutex = control_mutex;
    buffers = new SoftAudioBuffer[14];
    for (int j = 0; j < buffers.length; j++) {
      buffers[j] = new SoftAudioBuffer(i, paramSoftSynthesizer.getFormat());
    }
    voicestatus = paramSoftSynthesizer.getVoices();
    reverb = new SoftReverb();
    chorus = new SoftChorus();
    agc = new SoftLimiter();
    float f1 = paramSoftSynthesizer.getFormat().getSampleRate();
    float f2 = paramSoftSynthesizer.getControlRate();
    reverb.init(f1, f2);
    chorus.init(f1, f2);
    agc.init(f1, f2);
    reverb.setLightMode(reverb_light);
    reverb.setMixMode(true);
    chorus.setMixMode(true);
    agc.setMixMode(false);
    chorus.setInput(0, buffers[7]);
    chorus.setOutput(0, buffers[0]);
    if (nrofchannels != 1) {
      chorus.setOutput(1, buffers[1]);
    }
    chorus.setOutput(2, buffers[6]);
    reverb.setInput(0, buffers[6]);
    reverb.setOutput(0, buffers[0]);
    if (nrofchannels != 1) {
      reverb.setOutput(1, buffers[1]);
    }
    agc.setInput(0, buffers[0]);
    if (nrofchannels != 1) {
      agc.setInput(1, buffers[1]);
    }
    agc.setOutput(0, buffers[0]);
    if (nrofchannels != 1) {
      agc.setOutput(1, buffers[1]);
    }
    InputStream local2 = new InputStream()
    {
      private final SoftAudioBuffer[] buffers = buffers;
      private final int nrofchannels = synth.getFormat().getChannels();
      private final int buffersize = buffers[0].getSize();
      private final byte[] bbuffer = new byte[buffersize * (synth.getFormat().getSampleSizeInBits() / 8) * nrofchannels];
      private int bbuffer_pos = 0;
      private final byte[] single = new byte[1];
      
      public void fillBuffer()
      {
        processAudioBuffers();
        for (int i = 0; i < nrofchannels; i++) {
          buffers[i].get(bbuffer, i);
        }
        bbuffer_pos = 0;
      }
      
      public int read(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        int i = bbuffer.length;
        int j = paramAnonymousInt1 + paramAnonymousInt2;
        int k = paramAnonymousInt1;
        byte[] arrayOfByte = bbuffer;
        while (paramAnonymousInt1 < j) {
          if (available() == 0)
          {
            fillBuffer();
          }
          else
          {
            int m = bbuffer_pos;
            while ((paramAnonymousInt1 < j) && (m < i)) {
              paramAnonymousArrayOfByte[(paramAnonymousInt1++)] = arrayOfByte[(m++)];
            }
            bbuffer_pos = m;
            if (!readfully) {
              return paramAnonymousInt1 - k;
            }
          }
        }
        return paramAnonymousInt2;
      }
      
      public int read()
        throws IOException
      {
        int i = read(single);
        if (i == -1) {
          return -1;
        }
        return single[0] & 0xFF;
      }
      
      public int available()
      {
        return bbuffer.length - bbuffer_pos;
      }
      
      public void close()
      {
        synth.close();
      }
    };
    ais = new AudioInputStream(local2, paramSoftSynthesizer.getFormat(), -1L);
  }
  
  public AudioInputStream getInputStream()
  {
    return ais;
  }
  
  public void reset()
  {
    SoftChannel[] arrayOfSoftChannel = synth.channels;
    for (int i = 0; i < arrayOfSoftChannel.length; i++)
    {
      arrayOfSoftChannel[i].allSoundOff();
      arrayOfSoftChannel[i].resetAllControllers(true);
      if (synth.getGeneralMidiMode() == 2)
      {
        if (i == 9) {
          arrayOfSoftChannel[i].programChange(0, 15360);
        } else {
          arrayOfSoftChannel[i].programChange(0, 15488);
        }
      }
      else {
        arrayOfSoftChannel[i].programChange(0, 0);
      }
    }
    setVolume(16383);
    setBalance(8192);
    setCoarseTuning(8192);
    setFineTuning(8192);
    globalParameterControlChange(new int[] { 129 }, new long[] { 0L }, new long[] { 4L });
    globalParameterControlChange(new int[] { 130 }, new long[] { 0L }, new long[] { 2L });
  }
  
  public void setVolume(int paramInt)
  {
    synchronized (control_mutex)
    {
      co_master_volume[0] = (paramInt / 16384.0D);
    }
  }
  
  public void setBalance(int paramInt)
  {
    synchronized (control_mutex)
    {
      co_master_balance[0] = (paramInt / 16384.0D);
    }
  }
  
  public void setFineTuning(int paramInt)
  {
    synchronized (control_mutex)
    {
      co_master_fine_tuning[0] = (paramInt / 16384.0D);
    }
  }
  
  public void setCoarseTuning(int paramInt)
  {
    synchronized (control_mutex)
    {
      co_master_coarse_tuning[0] = (paramInt / 16384.0D);
    }
  }
  
  /* Error */
  public int getVolume()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 474	com/sun/media/sound/SoftMainMixer:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 465	com/sun/media/sound/SoftMainMixer:co_master_volume	[D
    //   11: iconst_0
    //   12: daload
    //   13: ldc2_w 245
    //   16: dmul
    //   17: d2i
    //   18: aload_1
    //   19: monitorexit
    //   20: ireturn
    //   21: astore_2
    //   22: aload_1
    //   23: monitorexit
    //   24: aload_2
    //   25: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	26	0	this	SoftMainMixer
    //   5	18	1	Ljava/lang/Object;	Object
    //   21	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	20	21	finally
    //   21	24	21	finally
  }
  
  /* Error */
  public int getBalance()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 474	com/sun/media/sound/SoftMainMixer:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 462	com/sun/media/sound/SoftMainMixer:co_master_balance	[D
    //   11: iconst_0
    //   12: daload
    //   13: ldc2_w 245
    //   16: dmul
    //   17: d2i
    //   18: aload_1
    //   19: monitorexit
    //   20: ireturn
    //   21: astore_2
    //   22: aload_1
    //   23: monitorexit
    //   24: aload_2
    //   25: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	26	0	this	SoftMainMixer
    //   5	18	1	Ljava/lang/Object;	Object
    //   21	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	20	21	finally
    //   21	24	21	finally
  }
  
  /* Error */
  public int getFineTuning()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 474	com/sun/media/sound/SoftMainMixer:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 464	com/sun/media/sound/SoftMainMixer:co_master_fine_tuning	[D
    //   11: iconst_0
    //   12: daload
    //   13: ldc2_w 245
    //   16: dmul
    //   17: d2i
    //   18: aload_1
    //   19: monitorexit
    //   20: ireturn
    //   21: astore_2
    //   22: aload_1
    //   23: monitorexit
    //   24: aload_2
    //   25: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	26	0	this	SoftMainMixer
    //   5	18	1	Ljava/lang/Object;	Object
    //   21	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	20	21	finally
    //   21	24	21	finally
  }
  
  /* Error */
  public int getCoarseTuning()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 474	com/sun/media/sound/SoftMainMixer:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 463	com/sun/media/sound/SoftMainMixer:co_master_coarse_tuning	[D
    //   11: iconst_0
    //   12: daload
    //   13: ldc2_w 245
    //   16: dmul
    //   17: d2i
    //   18: aload_1
    //   19: monitorexit
    //   20: ireturn
    //   21: astore_2
    //   22: aload_1
    //   23: monitorexit
    //   24: aload_2
    //   25: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	26	0	this	SoftMainMixer
    //   5	18	1	Ljava/lang/Object;	Object
    //   21	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	20	21	finally
    //   21	24	21	finally
  }
  
  public void globalParameterControlChange(int[] paramArrayOfInt, long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    if (paramArrayOfInt.length == 0) {
      return;
    }
    synchronized (control_mutex)
    {
      int i;
      if (paramArrayOfInt[0] == 129) {
        for (i = 0; i < paramArrayOfLong2.length; i++) {
          reverb.globalParameterControlChange(paramArrayOfInt, paramArrayOfLong1[i], paramArrayOfLong2[i]);
        }
      }
      if (paramArrayOfInt[0] == 130) {
        for (i = 0; i < paramArrayOfLong2.length; i++) {
          chorus.globalParameterControlChange(paramArrayOfInt, paramArrayOfLong1[i], paramArrayOfLong2[i]);
        }
      }
    }
  }
  
  public void processMessage(Object paramObject)
  {
    if ((paramObject instanceof byte[])) {
      processMessage((byte[])paramObject);
    }
    if ((paramObject instanceof MidiMessage)) {
      processMessage((MidiMessage)paramObject);
    }
  }
  
  public void processMessage(MidiMessage paramMidiMessage)
  {
    if ((paramMidiMessage instanceof ShortMessage))
    {
      ShortMessage localShortMessage = (ShortMessage)paramMidiMessage;
      processMessage(localShortMessage.getChannel(), localShortMessage.getCommand(), localShortMessage.getData1(), localShortMessage.getData2());
      return;
    }
    processMessage(paramMidiMessage.getMessage());
  }
  
  public void processMessage(byte[] paramArrayOfByte)
  {
    int i = 0;
    if (paramArrayOfByte.length > 0) {
      i = paramArrayOfByte[0] & 0xFF;
    }
    if (i == 240)
    {
      processSystemExclusiveMessage(paramArrayOfByte);
      return;
    }
    int j = i & 0xF0;
    int k = i & 0xF;
    int m;
    if (paramArrayOfByte.length > 1) {
      m = paramArrayOfByte[1] & 0xFF;
    } else {
      m = 0;
    }
    int n;
    if (paramArrayOfByte.length > 2) {
      n = paramArrayOfByte[2] & 0xFF;
    } else {
      n = 0;
    }
    processMessage(k, j, m, n);
  }
  
  public void processMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    synchronized (synth.control_mutex)
    {
      activity();
    }
    if (paramInt2 == 240)
    {
      int i = paramInt2 | paramInt1;
      switch (i)
      {
      case 254: 
        synchronized (synth.control_mutex)
        {
          active_sensing_on = true;
        }
        break;
      }
      return;
    }
    SoftChannel[] arrayOfSoftChannel = synth.channels;
    if (paramInt1 >= arrayOfSoftChannel.length) {
      return;
    }
    ??? = arrayOfSoftChannel[paramInt1];
    switch (paramInt2)
    {
    case 144: 
      if (delay_midievent != 0) {
        ((SoftChannel)???).noteOn(paramInt3, paramInt4, delay_midievent);
      } else {
        ((SoftChannel)???).noteOn(paramInt3, paramInt4);
      }
      break;
    case 128: 
      ((SoftChannel)???).noteOff(paramInt3, paramInt4);
      break;
    case 160: 
      ((SoftChannel)???).setPolyPressure(paramInt3, paramInt4);
      break;
    case 176: 
      ((SoftChannel)???).controlChange(paramInt3, paramInt4);
      break;
    case 192: 
      ((SoftChannel)???).programChange(paramInt3);
      break;
    case 208: 
      ((SoftChannel)???).setChannelPressure(paramInt3);
      break;
    case 224: 
      ((SoftChannel)???).setPitchBend(paramInt3 + paramInt4 * 128);
      break;
    }
  }
  
  public long getMicrosecondPosition()
  {
    if ((pusher_silent) && (synth.weakstream != null)) {
      return ((sample_pos + synth.weakstream.silent_samples) * (1000000.0D / samplerate));
    }
    return (sample_pos * (1000000.0D / samplerate));
  }
  
  public void close() {}
  
  private class SoftChannelMixerContainer
  {
    ModelChannelMixer mixer;
    SoftAudioBuffer[] buffers;
    
    private SoftChannelMixerContainer() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftMainMixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */