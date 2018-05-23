package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;

public final class SoftChannel
  implements MidiChannel, ModelDirectedPlayer
{
  private static boolean[] dontResetControls = new boolean[''];
  private static final int RPN_NULL_VALUE = 16383;
  private int rpn_control = 16383;
  private int nrpn_control = 16383;
  double portamento_time = 1.0D;
  int[] portamento_lastnote = new int[''];
  int portamento_lastnote_ix = 0;
  private boolean portamento = false;
  private boolean mono = false;
  private boolean mute = false;
  private boolean solo = false;
  private boolean solomute = false;
  private final Object control_mutex;
  private int channel;
  private SoftVoice[] voices;
  private int bank;
  private int program;
  private SoftSynthesizer synthesizer;
  private SoftMainMixer mainmixer;
  private int[] polypressure = new int[''];
  private int channelpressure = 0;
  private int[] controller = new int[''];
  private int pitchbend;
  private double[] co_midi_pitch = new double[1];
  private double[] co_midi_channel_pressure = new double[1];
  SoftTuning tuning = new SoftTuning();
  int tuning_bank = 0;
  int tuning_program = 0;
  SoftInstrument current_instrument = null;
  ModelChannelMixer current_mixer = null;
  ModelDirector current_director = null;
  int cds_control_number = -1;
  ModelConnectionBlock[] cds_control_connections = null;
  ModelConnectionBlock[] cds_channelpressure_connections = null;
  ModelConnectionBlock[] cds_polypressure_connections = null;
  boolean sustain = false;
  boolean[][] keybasedcontroller_active = (boolean[][])null;
  double[][] keybasedcontroller_value = (double[][])null;
  private SoftControl[] co_midi = new SoftControl[''];
  private double[][] co_midi_cc_cc;
  private SoftControl co_midi_cc;
  Map<Integer, int[]> co_midi_rpn_rpn_i;
  Map<Integer, double[]> co_midi_rpn_rpn;
  private SoftControl co_midi_rpn;
  Map<Integer, int[]> co_midi_nrpn_nrpn_i;
  Map<Integer, double[]> co_midi_nrpn_nrpn;
  private SoftControl co_midi_nrpn;
  private int[] lastVelocity;
  private int prevVoiceID;
  private boolean firstVoice;
  private int voiceNo;
  private int play_noteNumber;
  private int play_velocity;
  private int play_delay;
  private boolean play_releasetriggered;
  
  private static int restrict7Bit(int paramInt)
  {
    if (paramInt < 0) {
      return 0;
    }
    if (paramInt > 127) {
      return 127;
    }
    return paramInt;
  }
  
  private static int restrict14Bit(int paramInt)
  {
    if (paramInt < 0) {
      return 0;
    }
    if (paramInt > 16256) {
      return 16256;
    }
    return paramInt;
  }
  
  public SoftChannel(SoftSynthesizer paramSoftSynthesizer, int paramInt)
  {
    for (int i = 0; i < co_midi.length; i++) {
      co_midi[i] = new MidiControlObject(null);
    }
    co_midi_cc_cc = new double[''][1];
    co_midi_cc = new SoftControl()
    {
      double[][] cc = co_midi_cc_cc;
      
      public double[] get(int paramAnonymousInt, String paramAnonymousString)
      {
        if (paramAnonymousString == null) {
          return null;
        }
        return cc[Integer.parseInt(paramAnonymousString)];
      }
    };
    co_midi_rpn_rpn_i = new HashMap();
    co_midi_rpn_rpn = new HashMap();
    co_midi_rpn = new SoftControl()
    {
      Map<Integer, double[]> rpn = co_midi_rpn_rpn;
      
      public double[] get(int paramAnonymousInt, String paramAnonymousString)
      {
        if (paramAnonymousString == null) {
          return null;
        }
        int i = Integer.parseInt(paramAnonymousString);
        double[] arrayOfDouble = (double[])rpn.get(Integer.valueOf(i));
        if (arrayOfDouble == null)
        {
          arrayOfDouble = new double[1];
          rpn.put(Integer.valueOf(i), arrayOfDouble);
        }
        return arrayOfDouble;
      }
    };
    co_midi_nrpn_nrpn_i = new HashMap();
    co_midi_nrpn_nrpn = new HashMap();
    co_midi_nrpn = new SoftControl()
    {
      Map<Integer, double[]> nrpn = co_midi_nrpn_nrpn;
      
      public double[] get(int paramAnonymousInt, String paramAnonymousString)
      {
        if (paramAnonymousString == null) {
          return null;
        }
        int i = Integer.parseInt(paramAnonymousString);
        double[] arrayOfDouble = (double[])nrpn.get(Integer.valueOf(i));
        if (arrayOfDouble == null)
        {
          arrayOfDouble = new double[1];
          nrpn.put(Integer.valueOf(i), arrayOfDouble);
        }
        return arrayOfDouble;
      }
    };
    lastVelocity = new int[''];
    firstVoice = true;
    voiceNo = 0;
    play_noteNumber = 0;
    play_velocity = 0;
    play_delay = 0;
    play_releasetriggered = false;
    channel = paramInt;
    voices = paramSoftSynthesizer.getVoices();
    synthesizer = paramSoftSynthesizer;
    mainmixer = paramSoftSynthesizer.getMainMixer();
    control_mutex = control_mutex;
    resetAllControllers(true);
  }
  
  private int findFreeVoice(int paramInt)
  {
    if (paramInt == -1) {
      return -1;
    }
    for (int i = paramInt; i < voices.length; i++) {
      if (!voices[i].active) {
        return i;
      }
    }
    i = synthesizer.getVoiceAllocationMode();
    if (i == 1)
    {
      j = channel;
      for (int k = 0; k < voices.length; k++) {
        if (voices[k].stealer_channel == null) {
          if (j == 9) {
            j = voices[k].channel;
          } else if ((voices[k].channel != 9) && (voices[k].channel > j)) {
            j = voices[k].channel;
          }
        }
      }
      k = -1;
      SoftVoice localSoftVoice2 = null;
      for (int n = 0; n < voices.length; n++) {
        if ((voices[n].channel == j) && (voices[n].stealer_channel == null) && (!voices[n].on))
        {
          if (localSoftVoice2 == null)
          {
            localSoftVoice2 = voices[n];
            k = n;
          }
          if (voices[n].voiceID < voiceID)
          {
            localSoftVoice2 = voices[n];
            k = n;
          }
        }
      }
      if (k == -1) {
        for (n = 0; n < voices.length; n++) {
          if ((voices[n].channel == j) && (voices[n].stealer_channel == null))
          {
            if (localSoftVoice2 == null)
            {
              localSoftVoice2 = voices[n];
              k = n;
            }
            if (voices[n].voiceID < voiceID)
            {
              localSoftVoice2 = voices[n];
              k = n;
            }
          }
        }
      }
      return k;
    }
    int j = -1;
    SoftVoice localSoftVoice1 = null;
    for (int m = 0; m < voices.length; m++) {
      if ((voices[m].stealer_channel == null) && (!voices[m].on))
      {
        if (localSoftVoice1 == null)
        {
          localSoftVoice1 = voices[m];
          j = m;
        }
        if (voices[m].voiceID < voiceID)
        {
          localSoftVoice1 = voices[m];
          j = m;
        }
      }
    }
    if (j == -1) {
      for (m = 0; m < voices.length; m++) {
        if (voices[m].stealer_channel == null)
        {
          if (localSoftVoice1 == null)
          {
            localSoftVoice1 = voices[m];
            j = m;
          }
          if (voices[m].voiceID < voiceID)
          {
            localSoftVoice1 = voices[m];
            j = m;
          }
        }
      }
    }
    return j;
  }
  
  void initVoice(SoftVoice paramSoftVoice, SoftPerformer paramSoftPerformer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ModelConnectionBlock[] paramArrayOfModelConnectionBlock, ModelChannelMixer paramModelChannelMixer, boolean paramBoolean)
  {
    if (active)
    {
      stealer_channel = this;
      stealer_performer = paramSoftPerformer;
      stealer_voiceID = paramInt1;
      stealer_noteNumber = paramInt2;
      stealer_velocity = paramInt3;
      stealer_extendedConnectionBlocks = paramArrayOfModelConnectionBlock;
      stealer_channelmixer = paramModelChannelMixer;
      stealer_releaseTriggered = paramBoolean;
      for (int i = 0; i < voices.length; i++) {
        if ((voices[i].active) && (voices[i].voiceID == voiceID)) {
          voices[i].soundOff();
        }
      }
      return;
    }
    extendedConnectionBlocks = paramArrayOfModelConnectionBlock;
    channelmixer = paramModelChannelMixer;
    releaseTriggered = paramBoolean;
    voiceID = paramInt1;
    tuning = tuning;
    exclusiveClass = exclusiveClass;
    softchannel = this;
    channel = channel;
    bank = bank;
    program = program;
    instrument = current_instrument;
    performer = paramSoftPerformer;
    objects.clear();
    objects.put("midi", co_midi[paramInt2]);
    objects.put("midi_cc", co_midi_cc);
    objects.put("midi_rpn", co_midi_rpn);
    objects.put("midi_nrpn", co_midi_nrpn);
    paramSoftVoice.noteOn(paramInt2, paramInt3, paramInt4);
    paramSoftVoice.setMute(mute);
    paramSoftVoice.setSoloMute(solomute);
    if (paramBoolean) {
      return;
    }
    if (controller[84] != 0)
    {
      co_noteon_keynumber[0] = (tuning.getTuning(controller[84]) / 100.0D * 0.0078125D);
      portamento = true;
      controlChange(84, 0);
    }
    else if (portamento)
    {
      if (mono)
      {
        if (portamento_lastnote[0] != -1)
        {
          co_noteon_keynumber[0] = (tuning.getTuning(portamento_lastnote[0]) / 100.0D * 0.0078125D);
          portamento = true;
          controlChange(84, 0);
        }
        portamento_lastnote[0] = paramInt2;
      }
      else if (portamento_lastnote_ix != 0)
      {
        portamento_lastnote_ix -= 1;
        co_noteon_keynumber[0] = (tuning.getTuning(portamento_lastnote[portamento_lastnote_ix]) / 100.0D * 0.0078125D);
        portamento = true;
      }
    }
  }
  
  public void noteOn(int paramInt1, int paramInt2)
  {
    noteOn(paramInt1, paramInt2, 0);
  }
  
  void noteOn(int paramInt1, int paramInt2, int paramInt3)
  {
    paramInt1 = restrict7Bit(paramInt1);
    paramInt2 = restrict7Bit(paramInt2);
    noteOn_internal(paramInt1, paramInt2, paramInt3);
    if (current_mixer != null) {
      current_mixer.noteOn(paramInt1, paramInt2);
    }
  }
  
  private void noteOn_internal(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 == 0)
    {
      noteOff_internal(paramInt1, 64);
      return;
    }
    synchronized (control_mutex)
    {
      if (sustain)
      {
        sustain = false;
        for (i = 0; i < voices.length; i++) {
          if (((voices[i].sustain) || (voices[i].on)) && (voices[i].channel == channel) && (voices[i].active) && (voices[i].note == paramInt1))
          {
            voices[i].sustain = false;
            voices[i].on = true;
            voices[i].noteOff(0);
          }
        }
        sustain = true;
      }
      mainmixer.activity();
      if (mono)
      {
        int j;
        if (portamento)
        {
          i = 0;
          for (j = 0; j < voices.length; j++) {
            if ((voices[j].on) && (voices[j].channel == channel) && (voices[j].active) && (!voices[j].releaseTriggered))
            {
              voices[j].portamento = true;
              voices[j].setNote(paramInt1);
              i = 1;
            }
          }
          if (i != 0)
          {
            portamento_lastnote[0] = paramInt1;
            return;
          }
        }
        if (controller[84] != 0)
        {
          i = 0;
          for (j = 0; j < voices.length; j++) {
            if ((voices[j].on) && (voices[j].channel == channel) && (voices[j].active) && (voices[j].note == controller[84]) && (!voices[j].releaseTriggered))
            {
              voices[j].portamento = true;
              voices[j].setNote(paramInt1);
              i = 1;
            }
          }
          controlChange(84, 0);
          if (i != 0) {
            return;
          }
        }
      }
      if (mono) {
        allNotesOff();
      }
      if (current_instrument == null)
      {
        current_instrument = synthesizer.findInstrument(program, bank, channel);
        if (current_instrument == null) {
          return;
        }
        if (current_mixer != null) {
          mainmixer.stopMixer(current_mixer);
        }
        current_mixer = current_instrument.getSourceInstrument().getChannelMixer(this, synthesizer.getFormat());
        if (current_mixer != null) {
          mainmixer.registerMixer(current_mixer);
        }
        current_director = current_instrument.getDirector(this, this);
        applyInstrumentCustomization();
      }
      prevVoiceID = (synthesizer.voiceIDCounter++);
      firstVoice = true;
      voiceNo = 0;
      int i = (int)Math.round(tuning.getTuning(paramInt1) / 100.0D);
      play_noteNumber = paramInt1;
      play_velocity = paramInt2;
      play_delay = paramInt3;
      play_releasetriggered = false;
      lastVelocity[paramInt1] = paramInt2;
      current_director.noteOn(i, paramInt2);
    }
  }
  
  public void noteOff(int paramInt1, int paramInt2)
  {
    paramInt1 = restrict7Bit(paramInt1);
    paramInt2 = restrict7Bit(paramInt2);
    noteOff_internal(paramInt1, paramInt2);
    if (current_mixer != null) {
      current_mixer.noteOff(paramInt1, paramInt2);
    }
  }
  
  private void noteOff_internal(int paramInt1, int paramInt2)
  {
    synchronized (control_mutex)
    {
      if ((!mono) && (portamento) && (portamento_lastnote_ix != 127))
      {
        portamento_lastnote[portamento_lastnote_ix] = paramInt1;
        portamento_lastnote_ix += 1;
      }
      mainmixer.activity();
      for (int i = 0; i < voices.length; i++)
      {
        if ((voices[i].on) && (voices[i].channel == channel) && (voices[i].note == paramInt1) && (!voices[i].releaseTriggered)) {
          voices[i].noteOff(paramInt2);
        }
        if ((voices[i].stealer_channel == this) && (voices[i].stealer_noteNumber == paramInt1))
        {
          SoftVoice localSoftVoice = voices[i];
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
      if (current_instrument == null)
      {
        current_instrument = synthesizer.findInstrument(program, bank, channel);
        if (current_instrument == null) {
          return;
        }
        if (current_mixer != null) {
          mainmixer.stopMixer(current_mixer);
        }
        current_mixer = current_instrument.getSourceInstrument().getChannelMixer(this, synthesizer.getFormat());
        if (current_mixer != null) {
          mainmixer.registerMixer(current_mixer);
        }
        current_director = current_instrument.getDirector(this, this);
        applyInstrumentCustomization();
      }
      prevVoiceID = (synthesizer.voiceIDCounter++);
      firstVoice = true;
      voiceNo = 0;
      i = (int)Math.round(tuning.getTuning(paramInt1) / 100.0D);
      play_noteNumber = paramInt1;
      play_velocity = lastVelocity[paramInt1];
      play_releasetriggered = true;
      play_delay = 0;
      current_director.noteOff(i, paramInt2);
    }
  }
  
  public void play(int paramInt, ModelConnectionBlock[] paramArrayOfModelConnectionBlock)
  {
    int i = play_noteNumber;
    int j = play_velocity;
    int k = play_delay;
    boolean bool = play_releasetriggered;
    SoftPerformer localSoftPerformer = current_instrument.getPerformer(paramInt);
    if (firstVoice)
    {
      firstVoice = false;
      if (exclusiveClass != 0)
      {
        int m = exclusiveClass;
        for (int n = 0; n < voices.length; n++) {
          if ((voices[n].active) && (voices[n].channel == channel) && (voices[n].exclusiveClass == m) && ((!selfNonExclusive) || (voices[n].note != i))) {
            voices[n].shutdown();
          }
        }
      }
    }
    voiceNo = findFreeVoice(voiceNo);
    if (voiceNo == -1) {
      return;
    }
    initVoice(voices[voiceNo], localSoftPerformer, prevVoiceID, i, j, k, paramArrayOfModelConnectionBlock, current_mixer, bool);
  }
  
  public void noteOff(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 127)) {
      return;
    }
    noteOff_internal(paramInt, 64);
  }
  
  public void setPolyPressure(int paramInt1, int paramInt2)
  {
    paramInt1 = restrict7Bit(paramInt1);
    paramInt2 = restrict7Bit(paramInt2);
    if (current_mixer != null) {
      current_mixer.setPolyPressure(paramInt1, paramInt2);
    }
    synchronized (control_mutex)
    {
      mainmixer.activity();
      co_midi[paramInt1].get(0, "poly_pressure")[0] = (paramInt2 * 0.0078125D);
      polypressure[paramInt1] = paramInt2;
      for (int i = 0; i < voices.length; i++) {
        if ((voices[i].active) && (voices[i].note == paramInt1)) {
          voices[i].setPolyPressure(paramInt2);
        }
      }
    }
  }
  
  /* Error */
  public int getPolyPressure(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 653	com/sun/media/sound/SoftChannel:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 633	com/sun/media/sound/SoftChannel:polypressure	[I
    //   11: iload_1
    //   12: iaload
    //   13: aload_2
    //   14: monitorexit
    //   15: ireturn
    //   16: astore_3
    //   17: aload_2
    //   18: monitorexit
    //   19: aload_3
    //   20: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	21	0	this	SoftChannel
    //   0	21	1	paramInt	int
    //   5	13	2	Ljava/lang/Object;	Object
    //   16	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	15	16	finally
    //   16	19	16	finally
  }
  
  public void setChannelPressure(int paramInt)
  {
    paramInt = restrict7Bit(paramInt);
    if (current_mixer != null) {
      current_mixer.setChannelPressure(paramInt);
    }
    synchronized (control_mutex)
    {
      mainmixer.activity();
      co_midi_channel_pressure[0] = (paramInt * 0.0078125D);
      channelpressure = paramInt;
      for (int i = 0; i < voices.length; i++) {
        if (voices[i].active) {
          voices[i].setChannelPressure(paramInt);
        }
      }
    }
  }
  
  /* Error */
  public int getChannelPressure()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 653	com/sun/media/sound/SoftChannel:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 608	com/sun/media/sound/SoftChannel:channelpressure	I
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	SoftChannel
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  void applyInstrumentCustomization()
  {
    if ((cds_control_connections == null) && (cds_channelpressure_connections == null) && (cds_polypressure_connections == null)) {
      return;
    }
    ModelInstrument localModelInstrument = current_instrument.getSourceInstrument();
    ModelPerformer[] arrayOfModelPerformer1 = localModelInstrument.getPerformers();
    ModelPerformer[] arrayOfModelPerformer2 = new ModelPerformer[arrayOfModelPerformer1.length];
    for (int i = 0; i < arrayOfModelPerformer2.length; i++)
    {
      ModelPerformer localModelPerformer1 = arrayOfModelPerformer1[i];
      ModelPerformer localModelPerformer2 = new ModelPerformer();
      localModelPerformer2.setName(localModelPerformer1.getName());
      localModelPerformer2.setExclusiveClass(localModelPerformer1.getExclusiveClass());
      localModelPerformer2.setKeyFrom(localModelPerformer1.getKeyFrom());
      localModelPerformer2.setKeyTo(localModelPerformer1.getKeyTo());
      localModelPerformer2.setVelFrom(localModelPerformer1.getVelFrom());
      localModelPerformer2.setVelTo(localModelPerformer1.getVelTo());
      localModelPerformer2.getOscillators().addAll(localModelPerformer1.getOscillators());
      localModelPerformer2.getConnectionBlocks().addAll(localModelPerformer1.getConnectionBlocks());
      arrayOfModelPerformer2[i] = localModelPerformer2;
      List localList = localModelPerformer2.getConnectionBlocks();
      Object localObject1;
      Object localObject2;
      int i1;
      if (cds_control_connections != null)
      {
        localObject1 = Integer.toString(cds_control_number);
        localObject2 = localList.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          ModelConnectionBlock localModelConnectionBlock2 = (ModelConnectionBlock)((Iterator)localObject2).next();
          ModelSource[] arrayOfModelSource2 = localModelConnectionBlock2.getSources();
          i1 = 0;
          if (arrayOfModelSource2 != null) {
            for (int i2 = 0; i2 < arrayOfModelSource2.length; i2++)
            {
              ModelSource localModelSource = arrayOfModelSource2[i2];
              if (("midi_cc".equals(localModelSource.getIdentifier().getObject())) && (((String)localObject1).equals(localModelSource.getIdentifier().getVariable()))) {
                i1 = 1;
              }
            }
          }
          if (i1 != 0) {
            ((Iterator)localObject2).remove();
          }
        }
        for (int m = 0; m < cds_control_connections.length; m++) {
          localList.add(cds_control_connections[m]);
        }
      }
      ModelSource[] arrayOfModelSource1;
      int n;
      Object localObject3;
      if (cds_polypressure_connections != null)
      {
        localObject1 = localList.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (ModelConnectionBlock)((Iterator)localObject1).next();
          arrayOfModelSource1 = ((ModelConnectionBlock)localObject2).getSources();
          n = 0;
          if (arrayOfModelSource1 != null) {
            for (i1 = 0; i1 < arrayOfModelSource1.length; i1++)
            {
              localObject3 = arrayOfModelSource1[i1];
              if (("midi".equals(((ModelSource)localObject3).getIdentifier().getObject())) && ("poly_pressure".equals(((ModelSource)localObject3).getIdentifier().getVariable()))) {
                n = 1;
              }
            }
          }
          if (n != 0) {
            ((Iterator)localObject1).remove();
          }
        }
        for (int j = 0; j < cds_polypressure_connections.length; j++) {
          localList.add(cds_polypressure_connections[j]);
        }
      }
      if (cds_channelpressure_connections != null)
      {
        localObject1 = localList.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          ModelConnectionBlock localModelConnectionBlock1 = (ModelConnectionBlock)((Iterator)localObject1).next();
          arrayOfModelSource1 = localModelConnectionBlock1.getSources();
          n = 0;
          if (arrayOfModelSource1 != null) {
            for (i1 = 0; i1 < arrayOfModelSource1.length; i1++)
            {
              localObject3 = arrayOfModelSource1[i1].getIdentifier();
              if (("midi".equals(((ModelIdentifier)localObject3).getObject())) && ("channel_pressure".equals(((ModelIdentifier)localObject3).getVariable()))) {
                n = 1;
              }
            }
          }
          if (n != 0) {
            ((Iterator)localObject1).remove();
          }
        }
        for (int k = 0; k < cds_channelpressure_connections.length; k++) {
          localList.add(cds_channelpressure_connections[k]);
        }
      }
    }
    current_instrument = new SoftInstrument(localModelInstrument, arrayOfModelPerformer2);
  }
  
  private ModelConnectionBlock[] createModelConnections(ModelIdentifier paramModelIdentifier, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramArrayOfInt1.length; i++)
    {
      int j = paramArrayOfInt1[i];
      int k = paramArrayOfInt2[i];
      final double d;
      Object localObject;
      if (j == 0)
      {
        d = (k - 64) * 100;
        localObject = new ModelConnectionBlock(new ModelSource(paramModelIdentifier, false, false, 0), d, new ModelDestination(new ModelIdentifier("osc", "pitch")));
        localArrayList.add(localObject);
      }
      if (j == 1)
      {
        d = (k / 64.0D - 1.0D) * 9600.0D;
        if (d > 0.0D) {
          localObject = new ModelConnectionBlock(new ModelSource(paramModelIdentifier, true, false, 0), -d, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
        } else {
          localObject = new ModelConnectionBlock(new ModelSource(paramModelIdentifier, false, false, 0), d, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
        }
        localArrayList.add(localObject);
      }
      ModelConnectionBlock localModelConnectionBlock;
      if (j == 2)
      {
        d = k / 64.0D;
        localObject = new ModelTransform()
        {
          double s = d;
          
          public double transform(double paramAnonymousDouble)
          {
            if (s < 1.0D) {
              paramAnonymousDouble = s + paramAnonymousDouble * (1.0D - s);
            } else if (s > 1.0D) {
              paramAnonymousDouble = 1.0D + paramAnonymousDouble * (s - 1.0D);
            } else {
              return 0.0D;
            }
            return -(0.4166666666666667D / Math.log(10.0D)) * Math.log(paramAnonymousDouble);
          }
        };
        localModelConnectionBlock = new ModelConnectionBlock(new ModelSource(paramModelIdentifier, (ModelTransform)localObject), -960.0D, new ModelDestination(ModelDestination.DESTINATION_GAIN));
        localArrayList.add(localModelConnectionBlock);
      }
      if (j == 3)
      {
        d = (k / 64.0D - 1.0D) * 9600.0D;
        localObject = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true, 0), new ModelSource(paramModelIdentifier, false, false, 0), d, new ModelDestination(ModelDestination.DESTINATION_PITCH));
        localArrayList.add(localObject);
      }
      if (j == 4)
      {
        d = k / 128.0D * 2400.0D;
        localObject = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true, 0), new ModelSource(paramModelIdentifier, false, false, 0), d, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
        localArrayList.add(localObject);
      }
      if (j == 5)
      {
        d = k / 127.0D;
        localObject = new ModelTransform()
        {
          double s = d;
          
          public double transform(double paramAnonymousDouble)
          {
            return -(0.4166666666666667D / Math.log(10.0D)) * Math.log(1.0D - paramAnonymousDouble * s);
          }
        };
        localModelConnectionBlock = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, false, 0), new ModelSource(paramModelIdentifier, (ModelTransform)localObject), -960.0D, new ModelDestination(ModelDestination.DESTINATION_GAIN));
        localArrayList.add(localModelConnectionBlock);
      }
    }
    return (ModelConnectionBlock[])localArrayList.toArray(new ModelConnectionBlock[localArrayList.size()]);
  }
  
  public void mapPolyPressureToDestination(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    current_instrument = null;
    if (paramArrayOfInt1.length == 0)
    {
      cds_polypressure_connections = null;
      return;
    }
    cds_polypressure_connections = createModelConnections(new ModelIdentifier("midi", "poly_pressure"), paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public void mapChannelPressureToDestination(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    current_instrument = null;
    if (paramArrayOfInt1.length == 0)
    {
      cds_channelpressure_connections = null;
      return;
    }
    cds_channelpressure_connections = createModelConnections(new ModelIdentifier("midi", "channel_pressure"), paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public void mapControlToDestination(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if (((paramInt < 1) || (paramInt > 31)) && ((paramInt < 64) || (paramInt > 95)))
    {
      cds_control_connections = null;
      return;
    }
    current_instrument = null;
    cds_control_number = paramInt;
    if (paramArrayOfInt1.length == 0)
    {
      cds_control_connections = null;
      return;
    }
    cds_control_connections = createModelConnections(new ModelIdentifier("midi_cc", Integer.toString(paramInt)), paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public void controlChangePerNote(int paramInt1, int paramInt2, int paramInt3)
  {
    if (keybasedcontroller_active == null)
    {
      keybasedcontroller_active = new boolean[''][];
      keybasedcontroller_value = new double[''][];
    }
    if (keybasedcontroller_active[paramInt1] == null)
    {
      keybasedcontroller_active[paramInt1] = new boolean[''];
      Arrays.fill(keybasedcontroller_active[paramInt1], false);
      keybasedcontroller_value[paramInt1] = new double[''];
      Arrays.fill(keybasedcontroller_value[paramInt1], 0.0D);
    }
    if (paramInt3 == -1)
    {
      keybasedcontroller_active[paramInt1][paramInt2] = 0;
    }
    else
    {
      keybasedcontroller_active[paramInt1][paramInt2] = 1;
      keybasedcontroller_value[paramInt1][paramInt2] = (paramInt3 / 128.0D);
    }
    int i;
    if (paramInt2 < 120) {
      for (i = 0; i < voices.length; i++) {
        if (voices[i].active) {
          voices[i].controlChange(paramInt2, -1);
        }
      }
    } else if (paramInt2 == 120) {
      for (i = 0; i < voices.length; i++) {
        if (voices[i].active) {
          voices[i].rpnChange(1, -1);
        }
      }
    } else if (paramInt2 == 121) {
      for (i = 0; i < voices.length; i++) {
        if (voices[i].active) {
          voices[i].rpnChange(2, -1);
        }
      }
    }
  }
  
  public int getControlPerNote(int paramInt1, int paramInt2)
  {
    if (keybasedcontroller_active == null) {
      return -1;
    }
    if (keybasedcontroller_active[paramInt1] == null) {
      return -1;
    }
    if (keybasedcontroller_active[paramInt1][paramInt2] == 0) {
      return -1;
    }
    return (int)(keybasedcontroller_value[paramInt1][paramInt2] * 128.0D);
  }
  
  public void controlChange(int paramInt1, int paramInt2)
  {
    paramInt1 = restrict7Bit(paramInt1);
    paramInt2 = restrict7Bit(paramInt2);
    if (current_mixer != null) {
      current_mixer.controlChange(paramInt1, paramInt2);
    }
    synchronized (control_mutex)
    {
      int k;
      int m;
      switch (paramInt1)
      {
      case 5: 
        double d = -Math.asin(paramInt2 / 128.0D * 2.0D - 1.0D) / 3.141592653589793D + 0.5D;
        d = Math.pow(100000.0D, d) / 100.0D;
        d /= 100.0D;
        d *= 1000.0D;
        d /= synthesizer.getControlRate();
        portamento_time = d;
        break;
      case 6: 
      case 38: 
      case 96: 
      case 97: 
        int j = 0;
        int[] arrayOfInt;
        if (nrpn_control != 16383)
        {
          arrayOfInt = (int[])co_midi_nrpn_nrpn_i.get(Integer.valueOf(nrpn_control));
          if (arrayOfInt != null) {
            j = arrayOfInt[0];
          }
        }
        if (rpn_control != 16383)
        {
          arrayOfInt = (int[])co_midi_rpn_rpn_i.get(Integer.valueOf(rpn_control));
          if (arrayOfInt != null) {
            j = arrayOfInt[0];
          }
        }
        if (paramInt1 == 6)
        {
          j = (j & 0x7F) + (paramInt2 << 7);
        }
        else if (paramInt1 == 38)
        {
          j = (j & 0x3F80) + paramInt2;
        }
        else if ((paramInt1 == 96) || (paramInt1 == 97))
        {
          k = 1;
          if ((rpn_control == 2) || (rpn_control == 3) || (rpn_control == 4)) {
            k = 128;
          }
          if (paramInt1 == 96) {
            j += k;
          }
          if (paramInt1 == 97) {
            j -= k;
          }
        }
        if (nrpn_control != 16383) {
          nrpnChange(nrpn_control, j);
        }
        if (rpn_control != 16383) {
          rpnChange(rpn_control, j);
        }
        break;
      case 64: 
        k = paramInt2 >= 64 ? 1 : 0;
        if (sustain != k)
        {
          sustain = k;
          if (k == 0) {
            for (m = 0; m < voices.length; m++) {
              if ((voices[m].active) && (voices[m].sustain) && (voices[m].channel == channel))
              {
                voices[m].sustain = false;
                if (!voices[m].on)
                {
                  voices[m].on = true;
                  voices[m].noteOff(0);
                }
              }
            }
          } else {
            for (m = 0; m < voices.length; m++) {
              if ((voices[m].active) && (voices[m].channel == channel)) {
                voices[m].redamp();
              }
            }
          }
        }
        break;
      case 65: 
        portamento = (paramInt2 >= 64);
        portamento_lastnote[0] = -1;
        portamento_lastnote_ix = 0;
        break;
      case 66: 
        k = paramInt2 >= 64 ? 1 : 0;
        if (k != 0) {
          for (m = 0; m < voices.length; m++) {
            if ((voices[m].active) && (voices[m].on) && (voices[m].channel == channel)) {
              voices[m].sostenuto = true;
            }
          }
        }
        if (k == 0) {
          for (m = 0; m < voices.length; m++) {
            if ((voices[m].active) && (voices[m].sostenuto) && (voices[m].channel == channel))
            {
              voices[m].sostenuto = false;
              if (!voices[m].on)
              {
                voices[m].on = true;
                voices[m].noteOff(0);
              }
            }
          }
        }
        break;
      case 98: 
        nrpn_control = ((nrpn_control & 0x3F80) + paramInt2);
        rpn_control = 16383;
        break;
      case 99: 
        nrpn_control = ((nrpn_control & 0x7F) + (paramInt2 << 7));
        rpn_control = 16383;
        break;
      case 100: 
        rpn_control = ((rpn_control & 0x3F80) + paramInt2);
        nrpn_control = 16383;
        break;
      case 101: 
        rpn_control = ((rpn_control & 0x7F) + (paramInt2 << 7));
        nrpn_control = 16383;
        break;
      case 120: 
        allSoundOff();
        break;
      case 121: 
        resetAllControllers(paramInt2 == 127);
        break;
      case 122: 
        localControl(paramInt2 >= 64);
        break;
      case 123: 
        allNotesOff();
        break;
      case 124: 
        setOmni(false);
        break;
      case 125: 
        setOmni(true);
        break;
      case 126: 
        if (paramInt2 == 1) {
          setMono(true);
        }
        break;
      case 127: 
        setMono(false);
        break;
      }
      co_midi_cc_cc[paramInt1][0] = (paramInt2 * 0.0078125D);
      if (paramInt1 == 0)
      {
        bank = (paramInt2 << 7);
        return;
      }
      if (paramInt1 == 32)
      {
        bank = ((bank & 0x3F80) + paramInt2);
        return;
      }
      controller[paramInt1] = paramInt2;
      if (paramInt1 < 32) {
        controller[(paramInt1 + 32)] = 0;
      }
      for (int i = 0; i < voices.length; i++) {
        if (voices[i].active) {
          voices[i].controlChange(paramInt1, paramInt2);
        }
      }
    }
  }
  
  /* Error */
  public int getController(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 653	com/sun/media/sound/SoftChannel:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 631	com/sun/media/sound/SoftChannel:controller	[I
    //   11: iload_1
    //   12: iaload
    //   13: bipush 127
    //   15: iand
    //   16: aload_2
    //   17: monitorexit
    //   18: ireturn
    //   19: astore_3
    //   20: aload_2
    //   21: monitorexit
    //   22: aload_3
    //   23: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	24	0	this	SoftChannel
    //   0	24	1	paramInt	int
    //   5	16	2	Ljava/lang/Object;	Object
    //   19	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	18	19	finally
    //   19	22	19	finally
  }
  
  public void tuningChange(int paramInt)
  {
    tuningChange(0, paramInt);
  }
  
  public void tuningChange(int paramInt1, int paramInt2)
  {
    synchronized (control_mutex)
    {
      tuning = synthesizer.getTuning(new Patch(paramInt1, paramInt2));
    }
  }
  
  public void programChange(int paramInt)
  {
    programChange(bank, paramInt);
  }
  
  public void programChange(int paramInt1, int paramInt2)
  {
    paramInt1 = restrict14Bit(paramInt1);
    paramInt2 = restrict7Bit(paramInt2);
    synchronized (control_mutex)
    {
      mainmixer.activity();
      if ((bank != paramInt1) || (program != paramInt2))
      {
        bank = paramInt1;
        program = paramInt2;
        current_instrument = null;
      }
    }
  }
  
  /* Error */
  public int getProgram()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 653	com/sun/media/sound/SoftChannel:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 616	com/sun/media/sound/SoftChannel:program	I
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	SoftChannel
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public void setPitchBend(int paramInt)
  {
    paramInt = restrict14Bit(paramInt);
    if (current_mixer != null) {
      current_mixer.setPitchBend(paramInt);
    }
    synchronized (control_mutex)
    {
      mainmixer.activity();
      co_midi_pitch[0] = (paramInt * 6.103515625E-5D);
      pitchbend = paramInt;
      for (int i = 0; i < voices.length; i++) {
        if (voices[i].active) {
          voices[i].setPitchBend(paramInt);
        }
      }
    }
  }
  
  /* Error */
  public int getPitchBend()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 653	com/sun/media/sound/SoftChannel:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 610	com/sun/media/sound/SoftChannel:pitchbend	I
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	SoftChannel
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public void nrpnChange(int paramInt1, int paramInt2)
  {
    if (synthesizer.getGeneralMidiMode() == 0)
    {
      if (paramInt1 == 136) {
        controlChange(76, paramInt2 >> 7);
      }
      if (paramInt1 == 137) {
        controlChange(77, paramInt2 >> 7);
      }
      if (paramInt1 == 138) {
        controlChange(78, paramInt2 >> 7);
      }
      if (paramInt1 == 160) {
        controlChange(74, paramInt2 >> 7);
      }
      if (paramInt1 == 161) {
        controlChange(71, paramInt2 >> 7);
      }
      if (paramInt1 == 227) {
        controlChange(73, paramInt2 >> 7);
      }
      if (paramInt1 == 228) {
        controlChange(75, paramInt2 >> 7);
      }
      if (paramInt1 == 230) {
        controlChange(72, paramInt2 >> 7);
      }
      if (paramInt1 >> 7 == 24) {
        controlChangePerNote(paramInt1 % 128, 120, paramInt2 >> 7);
      }
      if (paramInt1 >> 7 == 26) {
        controlChangePerNote(paramInt1 % 128, 7, paramInt2 >> 7);
      }
      if (paramInt1 >> 7 == 28) {
        controlChangePerNote(paramInt1 % 128, 10, paramInt2 >> 7);
      }
      if (paramInt1 >> 7 == 29) {
        controlChangePerNote(paramInt1 % 128, 91, paramInt2 >> 7);
      }
      if (paramInt1 >> 7 == 30) {
        controlChangePerNote(paramInt1 % 128, 93, paramInt2 >> 7);
      }
    }
    int[] arrayOfInt = (int[])co_midi_nrpn_nrpn_i.get(Integer.valueOf(paramInt1));
    double[] arrayOfDouble = (double[])co_midi_nrpn_nrpn.get(Integer.valueOf(paramInt1));
    if (arrayOfInt == null)
    {
      arrayOfInt = new int[1];
      co_midi_nrpn_nrpn_i.put(Integer.valueOf(paramInt1), arrayOfInt);
    }
    if (arrayOfDouble == null)
    {
      arrayOfDouble = new double[1];
      co_midi_nrpn_nrpn.put(Integer.valueOf(paramInt1), arrayOfDouble);
    }
    arrayOfInt[0] = paramInt2;
    arrayOfDouble[0] = (arrayOfInt[0] * 6.103515625E-5D);
    for (int i = 0; i < voices.length; i++) {
      if (voices[i].active) {
        voices[i].nrpnChange(paramInt1, arrayOfInt[0]);
      }
    }
  }
  
  public void rpnChange(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 3)
    {
      tuning_program = (paramInt2 >> 7 & 0x7F);
      tuningChange(tuning_bank, tuning_program);
    }
    if (paramInt1 == 4) {
      tuning_bank = (paramInt2 >> 7 & 0x7F);
    }
    int[] arrayOfInt = (int[])co_midi_rpn_rpn_i.get(Integer.valueOf(paramInt1));
    double[] arrayOfDouble = (double[])co_midi_rpn_rpn.get(Integer.valueOf(paramInt1));
    if (arrayOfInt == null)
    {
      arrayOfInt = new int[1];
      co_midi_rpn_rpn_i.put(Integer.valueOf(paramInt1), arrayOfInt);
    }
    if (arrayOfDouble == null)
    {
      arrayOfDouble = new double[1];
      co_midi_rpn_rpn.put(Integer.valueOf(paramInt1), arrayOfDouble);
    }
    arrayOfInt[0] = paramInt2;
    arrayOfDouble[0] = (arrayOfInt[0] * 6.103515625E-5D);
    for (int i = 0; i < voices.length; i++) {
      if (voices[i].active) {
        voices[i].rpnChange(paramInt1, arrayOfInt[0]);
      }
    }
  }
  
  public void resetAllControllers()
  {
    resetAllControllers(false);
  }
  
  public void resetAllControllers(boolean paramBoolean)
  {
    synchronized (control_mutex)
    {
      mainmixer.activity();
      for (int i = 0; i < 128; i++) {
        setPolyPressure(i, 0);
      }
      setChannelPressure(0);
      setPitchBend(8192);
      for (i = 0; i < 128; i++) {
        if (dontResetControls[i] == 0) {
          controlChange(i, 0);
        }
      }
      controlChange(71, 64);
      controlChange(72, 64);
      controlChange(73, 64);
      controlChange(74, 64);
      controlChange(75, 64);
      controlChange(76, 64);
      controlChange(77, 64);
      controlChange(78, 64);
      controlChange(8, 64);
      controlChange(11, 127);
      controlChange(98, 127);
      controlChange(99, 127);
      controlChange(100, 127);
      controlChange(101, 127);
      if (paramBoolean)
      {
        keybasedcontroller_active = ((boolean[][])null);
        keybasedcontroller_value = ((double[][])null);
        controlChange(7, 100);
        controlChange(10, 64);
        controlChange(91, 40);
        Iterator localIterator = co_midi_rpn_rpn.keySet().iterator();
        int j;
        while (localIterator.hasNext())
        {
          j = ((Integer)localIterator.next()).intValue();
          if ((j != 3) && (j != 4)) {
            rpnChange(j, 0);
          }
        }
        localIterator = co_midi_nrpn_nrpn.keySet().iterator();
        while (localIterator.hasNext())
        {
          j = ((Integer)localIterator.next()).intValue();
          nrpnChange(j, 0);
        }
        rpnChange(0, 256);
        rpnChange(1, 8192);
        rpnChange(2, 8192);
        rpnChange(5, 64);
        tuning_bank = 0;
        tuning_program = 0;
        tuning = new SoftTuning();
      }
    }
  }
  
  public void allNotesOff()
  {
    if (current_mixer != null) {
      current_mixer.allNotesOff();
    }
    synchronized (control_mutex)
    {
      for (int i = 0; i < voices.length; i++) {
        if ((voices[i].on) && (voices[i].channel == channel) && (!voices[i].releaseTriggered)) {
          voices[i].noteOff(0);
        }
      }
    }
  }
  
  public void allSoundOff()
  {
    if (current_mixer != null) {
      current_mixer.allSoundOff();
    }
    synchronized (control_mutex)
    {
      for (int i = 0; i < voices.length; i++) {
        if ((voices[i].on) && (voices[i].channel == channel)) {
          voices[i].soundOff();
        }
      }
    }
  }
  
  public boolean localControl(boolean paramBoolean)
  {
    return false;
  }
  
  public void setMono(boolean paramBoolean)
  {
    if (current_mixer != null) {
      current_mixer.setMono(paramBoolean);
    }
    synchronized (control_mutex)
    {
      allNotesOff();
      mono = paramBoolean;
    }
  }
  
  /* Error */
  public boolean getMono()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 653	com/sun/media/sound/SoftChannel:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 622	com/sun/media/sound/SoftChannel:mono	Z
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	SoftChannel
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public void setOmni(boolean paramBoolean)
  {
    if (current_mixer != null) {
      current_mixer.setOmni(paramBoolean);
    }
    allNotesOff();
  }
  
  public boolean getOmni()
  {
    return false;
  }
  
  public void setMute(boolean paramBoolean)
  {
    if (current_mixer != null) {
      current_mixer.setMute(paramBoolean);
    }
    synchronized (control_mutex)
    {
      mute = paramBoolean;
      for (int i = 0; i < voices.length; i++) {
        if ((voices[i].active) && (voices[i].channel == channel)) {
          voices[i].setMute(paramBoolean);
        }
      }
    }
  }
  
  /* Error */
  public boolean getMute()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 653	com/sun/media/sound/SoftChannel:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 623	com/sun/media/sound/SoftChannel:mute	Z
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	SoftChannel
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public void setSolo(boolean paramBoolean)
  {
    if (current_mixer != null) {
      current_mixer.setSolo(paramBoolean);
    }
    synchronized (control_mutex)
    {
      solo = paramBoolean;
      int i = 0;
      SoftChannel localSoftChannel;
      for (localSoftChannel : synthesizer.channels) {
        if (solo)
        {
          i = 1;
          break;
        }
      }
      if (i == 0)
      {
        for (localSoftChannel : synthesizer.channels) {
          localSoftChannel.setSoloMute(false);
        }
        return;
      }
      for (localSoftChannel : synthesizer.channels) {
        localSoftChannel.setSoloMute(!solo);
      }
    }
  }
  
  private void setSoloMute(boolean paramBoolean)
  {
    synchronized (control_mutex)
    {
      if (solomute == paramBoolean) {
        return;
      }
      solomute = paramBoolean;
      for (int i = 0; i < voices.length; i++) {
        if ((voices[i].active) && (voices[i].channel == channel)) {
          voices[i].setSoloMute(solomute);
        }
      }
    }
  }
  
  /* Error */
  public boolean getSolo()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 653	com/sun/media/sound/SoftChannel:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 626	com/sun/media/sound/SoftChannel:solo	Z
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	SoftChannel
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  static
  {
    for (int i = 0; i < dontResetControls.length; i++) {
      dontResetControls[i] = false;
    }
    dontResetControls[0] = true;
    dontResetControls[32] = true;
    dontResetControls[7] = true;
    dontResetControls[8] = true;
    dontResetControls[10] = true;
    dontResetControls[11] = true;
    dontResetControls[91] = true;
    dontResetControls[92] = true;
    dontResetControls[93] = true;
    dontResetControls[94] = true;
    dontResetControls[95] = true;
    dontResetControls[70] = true;
    dontResetControls[71] = true;
    dontResetControls[72] = true;
    dontResetControls[73] = true;
    dontResetControls[74] = true;
    dontResetControls[75] = true;
    dontResetControls[76] = true;
    dontResetControls[77] = true;
    dontResetControls[78] = true;
    dontResetControls[79] = true;
    dontResetControls[120] = true;
    dontResetControls[121] = true;
    dontResetControls[122] = true;
    dontResetControls[123] = true;
    dontResetControls[124] = true;
    dontResetControls[125] = true;
    dontResetControls[126] = true;
    dontResetControls[127] = true;
    dontResetControls[6] = true;
    dontResetControls[38] = true;
    dontResetControls[96] = true;
    dontResetControls[97] = true;
    dontResetControls[98] = true;
    dontResetControls[99] = true;
    dontResetControls[100] = true;
    dontResetControls[101] = true;
  }
  
  private class MidiControlObject
    implements SoftControl
  {
    double[] pitch = co_midi_pitch;
    double[] channel_pressure = co_midi_channel_pressure;
    double[] poly_pressure = new double[1];
    
    private MidiControlObject() {}
    
    public double[] get(int paramInt, String paramString)
    {
      if (paramString == null) {
        return null;
      }
      if (paramString.equals("pitch")) {
        return pitch;
      }
      if (paramString.equals("channel_pressure")) {
        return channel_pressure;
      }
      if (paramString.equals("poly_pressure")) {
        return poly_pressure;
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */