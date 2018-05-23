package com.sun.media.sound;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.sound.midi.VoiceStatus;
import javax.sound.sampled.AudioFormat;

public final class SoftVoice
  extends VoiceStatus
{
  public int exclusiveClass = 0;
  public boolean releaseTriggered = false;
  private int noteOn_noteNumber = 0;
  private int noteOn_velocity = 0;
  private int noteOff_velocity = 0;
  private int delay = 0;
  ModelChannelMixer channelmixer = null;
  double tunedKey = 0.0D;
  SoftTuning tuning = null;
  SoftChannel stealer_channel = null;
  ModelConnectionBlock[] stealer_extendedConnectionBlocks = null;
  SoftPerformer stealer_performer = null;
  ModelChannelMixer stealer_channelmixer = null;
  int stealer_voiceID = -1;
  int stealer_noteNumber = 0;
  int stealer_velocity = 0;
  boolean stealer_releaseTriggered = false;
  int voiceID = -1;
  boolean sustain = false;
  boolean sostenuto = false;
  boolean portamento = false;
  private final SoftFilter filter_left;
  private final SoftFilter filter_right;
  private final SoftProcess eg = new SoftEnvelopeGenerator();
  private final SoftProcess lfo = new SoftLowFrequencyOscillator();
  Map<String, SoftControl> objects = new HashMap();
  SoftSynthesizer synthesizer;
  SoftInstrument instrument;
  SoftPerformer performer;
  SoftChannel softchannel = null;
  boolean on = false;
  private boolean audiostarted = false;
  private boolean started = false;
  private boolean stopping = false;
  private float osc_attenuation = 0.0F;
  private ModelOscillatorStream osc_stream;
  private int osc_stream_nrofchannels;
  private float[][] osc_buff = new float[2][];
  private boolean osc_stream_off_transmitted = false;
  private boolean out_mixer_end = false;
  private float out_mixer_left = 0.0F;
  private float out_mixer_right = 0.0F;
  private float out_mixer_effect1 = 0.0F;
  private float out_mixer_effect2 = 0.0F;
  private float last_out_mixer_left = 0.0F;
  private float last_out_mixer_right = 0.0F;
  private float last_out_mixer_effect1 = 0.0F;
  private float last_out_mixer_effect2 = 0.0F;
  ModelConnectionBlock[] extendedConnectionBlocks = null;
  private ModelConnectionBlock[] connections;
  private double[] connections_last = new double[50];
  private double[][][] connections_src = new double[50][3][];
  private int[][] connections_src_kc = new int[50][3];
  private double[][] connections_dst = new double[50][];
  private boolean soundoff = false;
  private float lastMuteValue = 0.0F;
  private float lastSoloMuteValue = 0.0F;
  double[] co_noteon_keynumber = new double[1];
  double[] co_noteon_velocity = new double[1];
  double[] co_noteon_on = new double[1];
  private final SoftControl co_noteon = new SoftControl()
  {
    double[] keynumber = co_noteon_keynumber;
    double[] velocity = co_noteon_velocity;
    double[] on = co_noteon_on;
    
    public double[] get(int paramAnonymousInt, String paramAnonymousString)
    {
      if (paramAnonymousString == null) {
        return null;
      }
      if (paramAnonymousString.equals("keynumber")) {
        return keynumber;
      }
      if (paramAnonymousString.equals("velocity")) {
        return velocity;
      }
      if (paramAnonymousString.equals("on")) {
        return on;
      }
      return null;
    }
  };
  private final double[] co_mixer_active = new double[1];
  private final double[] co_mixer_gain = new double[1];
  private final double[] co_mixer_pan = new double[1];
  private final double[] co_mixer_balance = new double[1];
  private final double[] co_mixer_reverb = new double[1];
  private final double[] co_mixer_chorus = new double[1];
  private final SoftControl co_mixer = new SoftControl()
  {
    double[] active = co_mixer_active;
    double[] gain = co_mixer_gain;
    double[] pan = co_mixer_pan;
    double[] balance = co_mixer_balance;
    double[] reverb = co_mixer_reverb;
    double[] chorus = co_mixer_chorus;
    
    public double[] get(int paramAnonymousInt, String paramAnonymousString)
    {
      if (paramAnonymousString == null) {
        return null;
      }
      if (paramAnonymousString.equals("active")) {
        return active;
      }
      if (paramAnonymousString.equals("gain")) {
        return gain;
      }
      if (paramAnonymousString.equals("pan")) {
        return pan;
      }
      if (paramAnonymousString.equals("balance")) {
        return balance;
      }
      if (paramAnonymousString.equals("reverb")) {
        return reverb;
      }
      if (paramAnonymousString.equals("chorus")) {
        return chorus;
      }
      return null;
    }
  };
  private final double[] co_osc_pitch = new double[1];
  private final SoftControl co_osc = new SoftControl()
  {
    double[] pitch = co_osc_pitch;
    
    public double[] get(int paramAnonymousInt, String paramAnonymousString)
    {
      if (paramAnonymousString == null) {
        return null;
      }
      if (paramAnonymousString.equals("pitch")) {
        return pitch;
      }
      return null;
    }
  };
  private final double[] co_filter_freq = new double[1];
  private final double[] co_filter_type = new double[1];
  private final double[] co_filter_q = new double[1];
  private final SoftControl co_filter = new SoftControl()
  {
    double[] freq = co_filter_freq;
    double[] ftype = co_filter_type;
    double[] q = co_filter_q;
    
    public double[] get(int paramAnonymousInt, String paramAnonymousString)
    {
      if (paramAnonymousString == null) {
        return null;
      }
      if (paramAnonymousString.equals("freq")) {
        return freq;
      }
      if (paramAnonymousString.equals("type")) {
        return ftype;
      }
      if (paramAnonymousString.equals("q")) {
        return q;
      }
      return null;
    }
  };
  SoftResamplerStreamer resampler;
  private final int nrofchannels;
  
  public SoftVoice(SoftSynthesizer paramSoftSynthesizer)
  {
    synthesizer = paramSoftSynthesizer;
    filter_left = new SoftFilter(paramSoftSynthesizer.getFormat().getSampleRate());
    filter_right = new SoftFilter(paramSoftSynthesizer.getFormat().getSampleRate());
    nrofchannels = paramSoftSynthesizer.getFormat().getChannels();
  }
  
  private int getValueKC(ModelIdentifier paramModelIdentifier)
  {
    if (paramModelIdentifier.getObject().equals("midi_cc"))
    {
      int i = Integer.parseInt(paramModelIdentifier.getVariable());
      if ((i != 0) && (i != 32) && (i < 120)) {
        return i;
      }
    }
    else if (paramModelIdentifier.getObject().equals("midi_rpn"))
    {
      if (paramModelIdentifier.getVariable().equals("1")) {
        return 120;
      }
      if (paramModelIdentifier.getVariable().equals("2")) {
        return 121;
      }
    }
    return -1;
  }
  
  private double[] getValue(ModelIdentifier paramModelIdentifier)
  {
    SoftControl localSoftControl = (SoftControl)objects.get(paramModelIdentifier.getObject());
    if (localSoftControl == null) {
      return null;
    }
    return localSoftControl.get(paramModelIdentifier.getInstance(), paramModelIdentifier.getVariable());
  }
  
  private double transformValue(double paramDouble, ModelSource paramModelSource)
  {
    if (paramModelSource.getTransform() != null) {
      return paramModelSource.getTransform().transform(paramDouble);
    }
    return paramDouble;
  }
  
  private double transformValue(double paramDouble, ModelDestination paramModelDestination)
  {
    if (paramModelDestination.getTransform() != null) {
      return paramModelDestination.getTransform().transform(paramDouble);
    }
    return paramDouble;
  }
  
  private double processKeyBasedController(double paramDouble, int paramInt)
  {
    if (paramInt == -1) {
      return paramDouble;
    }
    if ((softchannel.keybasedcontroller_active != null) && (softchannel.keybasedcontroller_active[note] != null) && (softchannel.keybasedcontroller_active[note][paramInt] != 0))
    {
      double d = softchannel.keybasedcontroller_value[note][paramInt];
      if ((paramInt == 10) || (paramInt == 91) || (paramInt == 93)) {
        return d;
      }
      paramDouble += d * 2.0D - 1.0D;
      if (paramDouble > 1.0D) {
        paramDouble = 1.0D;
      } else if (paramDouble < 0.0D) {
        paramDouble = 0.0D;
      }
    }
    return paramDouble;
  }
  
  private void processConnection(int paramInt)
  {
    ModelConnectionBlock localModelConnectionBlock = connections[paramInt];
    double[][] arrayOfDouble = connections_src[paramInt];
    double[] arrayOfDouble1 = connections_dst[paramInt];
    if ((arrayOfDouble1 == null) || (Double.isInfinite(arrayOfDouble1[0]))) {
      return;
    }
    double d = localModelConnectionBlock.getScale();
    ModelSource[] arrayOfModelSource;
    if (softchannel.keybasedcontroller_active == null)
    {
      arrayOfModelSource = localModelConnectionBlock.getSources();
      for (int i = 0; i < arrayOfModelSource.length; i++)
      {
        d *= transformValue(arrayOfDouble[i][0], arrayOfModelSource[i]);
        if (d == 0.0D) {
          break;
        }
      }
    }
    else
    {
      arrayOfModelSource = localModelConnectionBlock.getSources();
      int[] arrayOfInt = connections_src_kc[paramInt];
      for (int j = 0; j < arrayOfModelSource.length; j++)
      {
        d *= transformValue(processKeyBasedController(arrayOfDouble[j][0], arrayOfInt[j]), arrayOfModelSource[j]);
        if (d == 0.0D) {
          break;
        }
      }
    }
    d = transformValue(d, localModelConnectionBlock.getDestination());
    arrayOfDouble1[0] = (arrayOfDouble1[0] - connections_last[paramInt] + d);
    connections_last[paramInt] = d;
  }
  
  void updateTuning(SoftTuning paramSoftTuning)
  {
    tuning = paramSoftTuning;
    tunedKey = (tuning.getTuning(note) / 100.0D);
    if (!portamento)
    {
      co_noteon_keynumber[0] = (tunedKey * 0.0078125D);
      if (performer == null) {
        return;
      }
      int[] arrayOfInt = performer.midi_connections[4];
      if (arrayOfInt == null) {
        return;
      }
      for (int i = 0; i < arrayOfInt.length; i++) {
        processConnection(arrayOfInt[i]);
      }
    }
  }
  
  void setNote(int paramInt)
  {
    note = paramInt;
    tunedKey = (tuning.getTuning(paramInt) / 100.0D);
  }
  
  void noteOn(int paramInt1, int paramInt2, int paramInt3)
  {
    sustain = false;
    sostenuto = false;
    portamento = false;
    soundoff = false;
    on = true;
    active = true;
    started = true;
    noteOn_noteNumber = paramInt1;
    noteOn_velocity = paramInt2;
    delay = paramInt3;
    lastMuteValue = 0.0F;
    lastSoloMuteValue = 0.0F;
    setNote(paramInt1);
    if (performer.forcedKeynumber) {
      co_noteon_keynumber[0] = 0.0D;
    } else {
      co_noteon_keynumber[0] = (tunedKey * 0.0078125D);
    }
    if (performer.forcedVelocity) {
      co_noteon_velocity[0] = 0.0D;
    } else {
      co_noteon_velocity[0] = (paramInt2 * 0.0078125F);
    }
    co_mixer_active[0] = 0.0D;
    co_mixer_gain[0] = 0.0D;
    co_mixer_pan[0] = 0.0D;
    co_mixer_balance[0] = 0.0D;
    co_mixer_reverb[0] = 0.0D;
    co_mixer_chorus[0] = 0.0D;
    co_osc_pitch[0] = 0.0D;
    co_filter_freq[0] = 0.0D;
    co_filter_q[0] = 0.0D;
    co_filter_type[0] = 0.0D;
    co_noteon_on[0] = 1.0D;
    eg.reset();
    lfo.reset();
    filter_left.reset();
    filter_right.reset();
    objects.put("master", synthesizer.getMainMixer().co_master);
    objects.put("eg", eg);
    objects.put("lfo", lfo);
    objects.put("noteon", co_noteon);
    objects.put("osc", co_osc);
    objects.put("mixer", co_mixer);
    objects.put("filter", co_filter);
    connections = performer.connections;
    if ((connections_last == null) || (connections_last.length < connections.length)) {
      connections_last = new double[connections.length];
    }
    if ((connections_src == null) || (connections_src.length < connections.length))
    {
      connections_src = new double[connections.length][][];
      connections_src_kc = new int[connections.length][];
    }
    if ((connections_dst == null) || (connections_dst.length < connections.length)) {
      connections_dst = new double[connections.length][];
    }
    Object localObject1;
    for (int i = 0; i < connections.length; i++)
    {
      ModelConnectionBlock localModelConnectionBlock = connections[i];
      connections_last[i] = 0.0D;
      if (localModelConnectionBlock.getSources() != null)
      {
        ModelSource[] arrayOfModelSource = localModelConnectionBlock.getSources();
        if ((connections_src[i] == null) || (connections_src[i].length < arrayOfModelSource.length))
        {
          connections_src[i] = new double[arrayOfModelSource.length][];
          connections_src_kc[i] = new int[arrayOfModelSource.length];
        }
        localObject1 = connections_src[i];
        int[] arrayOfInt = connections_src_kc[i];
        connections_src[i] = localObject1;
        for (int m = 0; m < arrayOfModelSource.length; m++)
        {
          arrayOfInt[m] = getValueKC(arrayOfModelSource[m].getIdentifier());
          localObject1[m] = getValue(arrayOfModelSource[m].getIdentifier());
        }
      }
      if (localModelConnectionBlock.getDestination() != null) {
        connections_dst[i] = getValue(localModelConnectionBlock.getDestination().getIdentifier());
      } else {
        connections_dst[i] = null;
      }
    }
    for (i = 0; i < connections.length; i++) {
      processConnection(i);
    }
    if (extendedConnectionBlocks != null) {
      for (localObject1 : extendedConnectionBlocks)
      {
        double d1 = 0.0D;
        Object localObject3;
        double d2;
        ModelTransform localModelTransform2;
        if (softchannel.keybasedcontroller_active == null) {
          for (localObject3 : ((ModelConnectionBlock)localObject1).getSources())
          {
            d2 = getValue(localObject3.getIdentifier())[0];
            localModelTransform2 = ((ModelSource)localObject3).getTransform();
            if (localModelTransform2 == null) {
              d1 += d2;
            } else {
              d1 += localModelTransform2.transform(d2);
            }
          }
        } else {
          for (localObject3 : ((ModelConnectionBlock)localObject1).getSources())
          {
            d2 = getValue(localObject3.getIdentifier())[0];
            d2 = processKeyBasedController(d2, getValueKC(((ModelSource)localObject3).getIdentifier()));
            localModelTransform2 = ((ModelSource)localObject3).getTransform();
            if (localModelTransform2 == null) {
              d1 += d2;
            } else {
              d1 += localModelTransform2.transform(d2);
            }
          }
        }
        ??? = ((ModelConnectionBlock)localObject1).getDestination();
        ModelTransform localModelTransform1 = ((ModelDestination)???).getTransform();
        if (localModelTransform1 != null) {
          d1 = localModelTransform1.transform(d1);
        }
        getValue(((ModelDestination)???).getIdentifier())[0] += d1;
      }
    }
    eg.init(synthesizer);
    lfo.init(synthesizer);
  }
  
  void setPolyPressure(int paramInt)
  {
    if (performer == null) {
      return;
    }
    int[] arrayOfInt = performer.midi_connections[2];
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void setChannelPressure(int paramInt)
  {
    if (performer == null) {
      return;
    }
    int[] arrayOfInt = performer.midi_connections[1];
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void controlChange(int paramInt1, int paramInt2)
  {
    if (performer == null) {
      return;
    }
    int[] arrayOfInt = performer.midi_ctrl_connections[paramInt1];
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void nrpnChange(int paramInt1, int paramInt2)
  {
    if (performer == null) {
      return;
    }
    int[] arrayOfInt = (int[])performer.midi_nrpn_connections.get(Integer.valueOf(paramInt1));
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void rpnChange(int paramInt1, int paramInt2)
  {
    if (performer == null) {
      return;
    }
    int[] arrayOfInt = (int[])performer.midi_rpn_connections.get(Integer.valueOf(paramInt1));
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void setPitchBend(int paramInt)
  {
    if (performer == null) {
      return;
    }
    int[] arrayOfInt = performer.midi_connections[0];
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void setMute(boolean paramBoolean)
  {
    co_mixer_gain[0] -= lastMuteValue;
    lastMuteValue = (paramBoolean ? -960.0F : 0.0F);
    co_mixer_gain[0] += lastMuteValue;
  }
  
  void setSoloMute(boolean paramBoolean)
  {
    co_mixer_gain[0] -= lastSoloMuteValue;
    lastSoloMuteValue = (paramBoolean ? -960.0F : 0.0F);
    co_mixer_gain[0] += lastSoloMuteValue;
  }
  
  void shutdown()
  {
    if (co_noteon_on[0] < -0.5D) {
      return;
    }
    on = false;
    co_noteon_on[0] = -1.0D;
    if (performer == null) {
      return;
    }
    int[] arrayOfInt = performer.midi_connections[3];
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void soundOff()
  {
    on = false;
    soundoff = true;
  }
  
  void noteOff(int paramInt)
  {
    if (!on) {
      return;
    }
    on = false;
    noteOff_velocity = paramInt;
    if (softchannel.sustain)
    {
      sustain = true;
      return;
    }
    if (sostenuto) {
      return;
    }
    co_noteon_on[0] = 0.0D;
    if (performer == null) {
      return;
    }
    int[] arrayOfInt = performer.midi_connections[3];
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void redamp()
  {
    if (co_noteon_on[0] > 0.5D) {
      return;
    }
    if (co_noteon_on[0] < -0.5D) {
      return;
    }
    sustain = true;
    co_noteon_on[0] = 1.0D;
    if (performer == null) {
      return;
    }
    int[] arrayOfInt = performer.midi_connections[3];
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void processControlLogic()
  {
    if (stopping)
    {
      active = false;
      stopping = false;
      audiostarted = false;
      instrument = null;
      performer = null;
      connections = null;
      extendedConnectionBlocks = null;
      channelmixer = null;
      if (osc_stream != null) {
        try
        {
          osc_stream.close();
        }
        catch (IOException localIOException1) {}
      }
      if (stealer_channel != null)
      {
        stealer_channel.initVoice(this, stealer_performer, stealer_voiceID, stealer_noteNumber, stealer_velocity, 0, stealer_extendedConnectionBlocks, stealer_channelmixer, stealer_releaseTriggered);
        stealer_releaseTriggered = false;
        stealer_channel = null;
        stealer_performer = null;
        stealer_voiceID = -1;
        stealer_noteNumber = 0;
        stealer_velocity = 0;
        stealer_extendedConnectionBlocks = null;
        stealer_channelmixer = null;
      }
    }
    if (started)
    {
      audiostarted = true;
      ModelOscillator localModelOscillator = performer.oscillators[0];
      osc_stream_off_transmitted = false;
      if ((localModelOscillator instanceof ModelWavetable)) {
        try
        {
          resampler.open((ModelWavetable)localModelOscillator, synthesizer.getFormat().getSampleRate());
          osc_stream = resampler;
        }
        catch (IOException localIOException2) {}
      } else {
        osc_stream = localModelOscillator.open(synthesizer.getFormat().getSampleRate());
      }
      osc_attenuation = localModelOscillator.getAttenuation();
      osc_stream_nrofchannels = localModelOscillator.getChannels();
      if ((osc_buff == null) || (osc_buff.length < osc_stream_nrofchannels)) {
        osc_buff = new float[osc_stream_nrofchannels][];
      }
      if (osc_stream != null) {
        osc_stream.noteOn(softchannel, this, noteOn_noteNumber, noteOn_velocity);
      }
    }
    if (audiostarted)
    {
      if (portamento)
      {
        double d1 = tunedKey - co_noteon_keynumber[0] * 128.0D;
        double d3 = Math.abs(d1);
        if (d3 < 1.0E-10D)
        {
          co_noteon_keynumber[0] = (tunedKey * 0.0078125D);
          portamento = false;
        }
        else
        {
          if (d3 > softchannel.portamento_time) {
            d1 = Math.signum(d1) * softchannel.portamento_time;
          }
          co_noteon_keynumber[0] += d1 * 0.0078125D;
        }
        int[] arrayOfInt = performer.midi_connections[4];
        if (arrayOfInt == null) {
          return;
        }
        for (int j = 0; j < arrayOfInt.length; j++) {
          processConnection(arrayOfInt[j]);
        }
      }
      eg.processControlLogic();
      lfo.processControlLogic();
      for (int i = 0; i < performer.ctrl_connections.length; i++) {
        processConnection(performer.ctrl_connections[i]);
      }
      osc_stream.setPitch((float)co_osc_pitch[0]);
      i = (int)co_filter_type[0];
      double d2;
      if (co_filter_freq[0] == 13500.0D) {
        d2 = 19912.126958213175D;
      } else {
        d2 = 440.0D * Math.exp((co_filter_freq[0] - 6900.0D) * (Math.log(2.0D) / 1200.0D));
      }
      double d4 = co_filter_q[0] / 10.0D;
      filter_left.setFilterType(i);
      filter_left.setFrequency(d2);
      filter_left.setResonance(d4);
      filter_right.setFilterType(i);
      filter_right.setFrequency(d2);
      filter_right.setResonance(d4);
      float f = (float)Math.exp((-osc_attenuation + co_mixer_gain[0]) * (Math.log(10.0D) / 200.0D));
      if (co_mixer_gain[0] <= -960.0D) {
        f = 0.0F;
      }
      if (soundoff)
      {
        stopping = true;
        f = 0.0F;
      }
      volume = ((int)(Math.sqrt(f) * 128.0D));
      double d5 = co_mixer_pan[0] * 0.001D;
      if (d5 < 0.0D) {
        d5 = 0.0D;
      } else if (d5 > 1.0D) {
        d5 = 1.0D;
      }
      if (d5 == 0.5D)
      {
        out_mixer_left = (f * 0.70710677F);
        out_mixer_right = out_mixer_left;
      }
      else
      {
        out_mixer_left = (f * (float)Math.cos(d5 * 3.141592653589793D * 0.5D));
        out_mixer_right = (f * (float)Math.sin(d5 * 3.141592653589793D * 0.5D));
      }
      double d6 = co_mixer_balance[0] * 0.001D;
      if (d6 != 0.5D) {
        if (d6 > 0.5D) {
          out_mixer_left = ((float)(out_mixer_left * ((1.0D - d6) * 2.0D)));
        } else {
          out_mixer_right = ((float)(out_mixer_right * (d6 * 2.0D)));
        }
      }
      if (synthesizer.reverb_on)
      {
        out_mixer_effect1 = ((float)(co_mixer_reverb[0] * 0.001D));
        out_mixer_effect1 *= f;
      }
      else
      {
        out_mixer_effect1 = 0.0F;
      }
      if (synthesizer.chorus_on)
      {
        out_mixer_effect2 = ((float)(co_mixer_chorus[0] * 0.001D));
        out_mixer_effect2 *= f;
      }
      else
      {
        out_mixer_effect2 = 0.0F;
      }
      out_mixer_end = (co_mixer_active[0] < 0.5D);
      if ((!on) && (!osc_stream_off_transmitted))
      {
        osc_stream_off_transmitted = true;
        if (osc_stream != null) {
          osc_stream.noteOff(noteOff_velocity);
        }
      }
    }
    if (started)
    {
      last_out_mixer_left = out_mixer_left;
      last_out_mixer_right = out_mixer_right;
      last_out_mixer_effect1 = out_mixer_effect1;
      last_out_mixer_effect2 = out_mixer_effect2;
      started = false;
    }
  }
  
  void mixAudioStream(SoftAudioBuffer paramSoftAudioBuffer1, SoftAudioBuffer paramSoftAudioBuffer2, SoftAudioBuffer paramSoftAudioBuffer3, float paramFloat1, float paramFloat2)
  {
    int i = paramSoftAudioBuffer1.getSize();
    if ((paramFloat1 < 1.0E-9D) && (paramFloat2 < 1.0E-9D)) {
      return;
    }
    float[] arrayOfFloat7;
    int n;
    if ((paramSoftAudioBuffer3 != null) && (delay != 0))
    {
      if (paramFloat1 == paramFloat2)
      {
        float[] arrayOfFloat1 = paramSoftAudioBuffer2.array();
        float[] arrayOfFloat3 = paramSoftAudioBuffer1.array();
        int j = 0;
        for (int m = delay; m < i; m++) {
          arrayOfFloat1[m] += arrayOfFloat3[(j++)] * paramFloat2;
        }
        arrayOfFloat1 = paramSoftAudioBuffer3.array();
        for (m = 0; m < delay; m++) {
          arrayOfFloat1[m] += arrayOfFloat3[(j++)] * paramFloat2;
        }
      }
      else
      {
        float f1 = paramFloat1;
        float f3 = (paramFloat2 - paramFloat1) / i;
        float[] arrayOfFloat5 = paramSoftAudioBuffer2.array();
        arrayOfFloat7 = paramSoftAudioBuffer1.array();
        n = 0;
        for (int i1 = delay; i1 < i; i1++)
        {
          f1 += f3;
          arrayOfFloat5[i1] += arrayOfFloat7[(n++)] * f1;
        }
        arrayOfFloat5 = paramSoftAudioBuffer3.array();
        for (i1 = 0; i1 < delay; i1++)
        {
          f1 += f3;
          arrayOfFloat5[i1] += arrayOfFloat7[(n++)] * f1;
        }
      }
    }
    else if (paramFloat1 == paramFloat2)
    {
      float[] arrayOfFloat2 = paramSoftAudioBuffer2.array();
      float[] arrayOfFloat4 = paramSoftAudioBuffer1.array();
      for (int k = 0; k < i; k++) {
        arrayOfFloat2[k] += arrayOfFloat4[k] * paramFloat2;
      }
    }
    else
    {
      float f2 = paramFloat1;
      float f4 = (paramFloat2 - paramFloat1) / i;
      float[] arrayOfFloat6 = paramSoftAudioBuffer2.array();
      arrayOfFloat7 = paramSoftAudioBuffer1.array();
      for (n = 0; n < i; n++)
      {
        f2 += f4;
        arrayOfFloat6[n] += arrayOfFloat7[n] * f2;
      }
    }
  }
  
  void processAudioLogic(SoftAudioBuffer[] paramArrayOfSoftAudioBuffer)
  {
    if (!audiostarted) {
      return;
    }
    int i = paramArrayOfSoftAudioBuffer[0].getSize();
    try
    {
      osc_buff[0] = paramArrayOfSoftAudioBuffer[10].array();
      if (nrofchannels != 1) {
        osc_buff[1] = paramArrayOfSoftAudioBuffer[11].array();
      }
      int j = osc_stream.read(osc_buff, 0, i);
      if (j == -1)
      {
        stopping = true;
        return;
      }
      if (j != i)
      {
        Arrays.fill(osc_buff[0], j, i, 0.0F);
        if (nrofchannels != 1) {
          Arrays.fill(osc_buff[1], j, i, 0.0F);
        }
      }
    }
    catch (IOException localIOException) {}
    SoftAudioBuffer localSoftAudioBuffer1 = paramArrayOfSoftAudioBuffer[0];
    SoftAudioBuffer localSoftAudioBuffer2 = paramArrayOfSoftAudioBuffer[1];
    SoftAudioBuffer localSoftAudioBuffer3 = paramArrayOfSoftAudioBuffer[2];
    SoftAudioBuffer localSoftAudioBuffer4 = paramArrayOfSoftAudioBuffer[6];
    SoftAudioBuffer localSoftAudioBuffer5 = paramArrayOfSoftAudioBuffer[7];
    SoftAudioBuffer localSoftAudioBuffer6 = paramArrayOfSoftAudioBuffer[3];
    SoftAudioBuffer localSoftAudioBuffer7 = paramArrayOfSoftAudioBuffer[4];
    SoftAudioBuffer localSoftAudioBuffer8 = paramArrayOfSoftAudioBuffer[5];
    SoftAudioBuffer localSoftAudioBuffer9 = paramArrayOfSoftAudioBuffer[8];
    SoftAudioBuffer localSoftAudioBuffer10 = paramArrayOfSoftAudioBuffer[9];
    SoftAudioBuffer localSoftAudioBuffer11 = paramArrayOfSoftAudioBuffer[10];
    SoftAudioBuffer localSoftAudioBuffer12 = paramArrayOfSoftAudioBuffer[11];
    if (osc_stream_nrofchannels == 1) {
      localSoftAudioBuffer12 = null;
    }
    if (!Double.isInfinite(co_filter_freq[0]))
    {
      filter_left.processAudio(localSoftAudioBuffer11);
      if (localSoftAudioBuffer12 != null) {
        filter_right.processAudio(localSoftAudioBuffer12);
      }
    }
    if (nrofchannels == 1)
    {
      out_mixer_left = ((out_mixer_left + out_mixer_right) / 2.0F);
      mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer1, localSoftAudioBuffer6, last_out_mixer_left, out_mixer_left);
      if (localSoftAudioBuffer12 != null) {
        mixAudioStream(localSoftAudioBuffer12, localSoftAudioBuffer1, localSoftAudioBuffer6, last_out_mixer_left, out_mixer_left);
      }
    }
    else if ((localSoftAudioBuffer12 == null) && (last_out_mixer_left == last_out_mixer_right) && (out_mixer_left == out_mixer_right))
    {
      mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer3, localSoftAudioBuffer8, last_out_mixer_left, out_mixer_left);
    }
    else
    {
      mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer1, localSoftAudioBuffer6, last_out_mixer_left, out_mixer_left);
      if (localSoftAudioBuffer12 != null) {
        mixAudioStream(localSoftAudioBuffer12, localSoftAudioBuffer2, localSoftAudioBuffer7, last_out_mixer_right, out_mixer_right);
      } else {
        mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer2, localSoftAudioBuffer7, last_out_mixer_right, out_mixer_right);
      }
    }
    if (localSoftAudioBuffer12 == null)
    {
      mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer4, localSoftAudioBuffer9, last_out_mixer_effect1, out_mixer_effect1);
      mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer5, localSoftAudioBuffer10, last_out_mixer_effect2, out_mixer_effect2);
    }
    else
    {
      mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer4, localSoftAudioBuffer9, last_out_mixer_effect1 * 0.5F, out_mixer_effect1 * 0.5F);
      mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer5, localSoftAudioBuffer10, last_out_mixer_effect2 * 0.5F, out_mixer_effect2 * 0.5F);
      mixAudioStream(localSoftAudioBuffer12, localSoftAudioBuffer4, localSoftAudioBuffer9, last_out_mixer_effect1 * 0.5F, out_mixer_effect1 * 0.5F);
      mixAudioStream(localSoftAudioBuffer12, localSoftAudioBuffer5, localSoftAudioBuffer10, last_out_mixer_effect2 * 0.5F, out_mixer_effect2 * 0.5F);
    }
    last_out_mixer_left = out_mixer_left;
    last_out_mixer_right = out_mixer_right;
    last_out_mixer_effect1 = out_mixer_effect1;
    last_out_mixer_effect2 = out_mixer_effect2;
    if (out_mixer_end) {
      stopping = true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftVoice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */