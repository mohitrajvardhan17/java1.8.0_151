package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequencer.SyncMode;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

final class RealTimeSequencer
  extends AbstractMidiDevice
  implements Sequencer, AutoConnectSequencer
{
  private static final boolean DEBUG_PUMP = false;
  private static final boolean DEBUG_PUMP_ALL = false;
  private static final Map<ThreadGroup, EventDispatcher> dispatchers = new WeakHashMap();
  static final RealTimeSequencerInfo info = new RealTimeSequencerInfo(null);
  private static final Sequencer.SyncMode[] masterSyncModes = { Sequencer.SyncMode.INTERNAL_CLOCK };
  private static final Sequencer.SyncMode[] slaveSyncModes = { Sequencer.SyncMode.NO_SYNC };
  private static final Sequencer.SyncMode masterSyncMode = Sequencer.SyncMode.INTERNAL_CLOCK;
  private static final Sequencer.SyncMode slaveSyncMode = Sequencer.SyncMode.NO_SYNC;
  private Sequence sequence = null;
  private double cacheTempoMPQ = -1.0D;
  private float cacheTempoFactor = -1.0F;
  private boolean[] trackMuted = null;
  private boolean[] trackSolo = null;
  private final MidiUtils.TempoCache tempoCache = new MidiUtils.TempoCache();
  private boolean running = false;
  private PlayThread playThread;
  private boolean recording = false;
  private final List recordingTracks = new ArrayList();
  private long loopStart = 0L;
  private long loopEnd = -1L;
  private int loopCount = 0;
  private final ArrayList metaEventListeners = new ArrayList();
  private final ArrayList controllerEventListeners = new ArrayList();
  private boolean autoConnect = false;
  private boolean doAutoConnectAtNextOpen = false;
  Receiver autoConnectedReceiver = null;
  
  RealTimeSequencer()
    throws MidiUnavailableException
  {
    super(info);
  }
  
  public synchronized void setSequence(Sequence paramSequence)
    throws InvalidMidiDataException
  {
    if (paramSequence != sequence)
    {
      if ((sequence != null) && (paramSequence == null))
      {
        setCaches();
        stop();
        trackMuted = null;
        trackSolo = null;
        loopStart = 0L;
        loopEnd = -1L;
        loopCount = 0;
        if (getDataPump() != null)
        {
          getDataPump().setTickPos(0L);
          getDataPump().resetLoopCount();
        }
      }
      if (playThread != null) {
        playThread.setSequence(paramSequence);
      }
      sequence = paramSequence;
      if (paramSequence != null)
      {
        tempoCache.refresh(paramSequence);
        setTickPosition(0L);
        propagateCaches();
      }
    }
    else if (paramSequence != null)
    {
      tempoCache.refresh(paramSequence);
      if (playThread != null) {
        playThread.setSequence(paramSequence);
      }
    }
  }
  
  public synchronized void setSequence(InputStream paramInputStream)
    throws IOException, InvalidMidiDataException
  {
    if (paramInputStream == null)
    {
      setSequence((Sequence)null);
      return;
    }
    Sequence localSequence = MidiSystem.getSequence(paramInputStream);
    setSequence(localSequence);
  }
  
  public Sequence getSequence()
  {
    return sequence;
  }
  
  public synchronized void start()
  {
    if (!isOpen()) {
      throw new IllegalStateException("sequencer not open");
    }
    if (sequence == null) {
      throw new IllegalStateException("sequence not set");
    }
    if (running == true) {
      return;
    }
    implStart();
  }
  
  public synchronized void stop()
  {
    if (!isOpen()) {
      throw new IllegalStateException("sequencer not open");
    }
    stopRecording();
    if (!running) {
      return;
    }
    implStop();
  }
  
  public boolean isRunning()
  {
    return running;
  }
  
  public void startRecording()
  {
    if (!isOpen()) {
      throw new IllegalStateException("Sequencer not open");
    }
    start();
    recording = true;
  }
  
  public void stopRecording()
  {
    if (!isOpen()) {
      throw new IllegalStateException("Sequencer not open");
    }
    recording = false;
  }
  
  public boolean isRecording()
  {
    return recording;
  }
  
  public void recordEnable(Track paramTrack, int paramInt)
  {
    if (!findTrack(paramTrack)) {
      throw new IllegalArgumentException("Track does not exist in the current sequence");
    }
    synchronized (recordingTracks)
    {
      RecordingTrack localRecordingTrack = RecordingTrack.get(recordingTracks, paramTrack);
      if (localRecordingTrack != null) {
        channel = paramInt;
      } else {
        recordingTracks.add(new RecordingTrack(paramTrack, paramInt));
      }
    }
  }
  
  public void recordDisable(Track paramTrack)
  {
    synchronized (recordingTracks)
    {
      RecordingTrack localRecordingTrack = RecordingTrack.get(recordingTracks, paramTrack);
      if (localRecordingTrack != null) {
        recordingTracks.remove(localRecordingTrack);
      }
    }
  }
  
  private boolean findTrack(Track paramTrack)
  {
    boolean bool = false;
    if (sequence != null)
    {
      Track[] arrayOfTrack = sequence.getTracks();
      for (int i = 0; i < arrayOfTrack.length; i++) {
        if (paramTrack == arrayOfTrack[i])
        {
          bool = true;
          break;
        }
      }
    }
    return bool;
  }
  
  public float getTempoInBPM()
  {
    return (float)MidiUtils.convertTempo(getTempoInMPQ());
  }
  
  public void setTempoInBPM(float paramFloat)
  {
    if (paramFloat <= 0.0F) {
      paramFloat = 1.0F;
    }
    setTempoInMPQ((float)MidiUtils.convertTempo(paramFloat));
  }
  
  public float getTempoInMPQ()
  {
    if (needCaching())
    {
      if (cacheTempoMPQ != -1.0D) {
        return (float)cacheTempoMPQ;
      }
      if (sequence != null) {
        return tempoCache.getTempoMPQAt(getTickPosition());
      }
      return 500000.0F;
    }
    return getDataPump().getTempoMPQ();
  }
  
  public void setTempoInMPQ(float paramFloat)
  {
    if (paramFloat <= 0.0F) {
      paramFloat = 1.0F;
    }
    if (needCaching())
    {
      cacheTempoMPQ = paramFloat;
    }
    else
    {
      getDataPump().setTempoMPQ(paramFloat);
      cacheTempoMPQ = -1.0D;
    }
  }
  
  public void setTempoFactor(float paramFloat)
  {
    if (paramFloat <= 0.0F) {
      return;
    }
    if (needCaching())
    {
      cacheTempoFactor = paramFloat;
    }
    else
    {
      getDataPump().setTempoFactor(paramFloat);
      cacheTempoFactor = -1.0F;
    }
  }
  
  public float getTempoFactor()
  {
    if (needCaching())
    {
      if (cacheTempoFactor != -1.0F) {
        return cacheTempoFactor;
      }
      return 1.0F;
    }
    return getDataPump().getTempoFactor();
  }
  
  public long getTickLength()
  {
    if (sequence == null) {
      return 0L;
    }
    return sequence.getTickLength();
  }
  
  public synchronized long getTickPosition()
  {
    if ((getDataPump() == null) || (sequence == null)) {
      return 0L;
    }
    return getDataPump().getTickPos();
  }
  
  public synchronized void setTickPosition(long paramLong)
  {
    if (paramLong < 0L) {
      return;
    }
    if (getDataPump() == null)
    {
      if (paramLong == 0L) {}
    }
    else if (sequence == null)
    {
      if (paramLong == 0L) {}
    }
    else {
      getDataPump().setTickPos(paramLong);
    }
  }
  
  public long getMicrosecondLength()
  {
    if (sequence == null) {
      return 0L;
    }
    return sequence.getMicrosecondLength();
  }
  
  /* Error */
  public long getMicrosecondPosition()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 519	com/sun/media/sound/RealTimeSequencer:getDataPump	()Lcom/sun/media/sound/RealTimeSequencer$DataPump;
    //   4: ifnull +10 -> 14
    //   7: aload_0
    //   8: getfield 477	com/sun/media/sound/RealTimeSequencer:sequence	Ljavax/sound/midi/Sequence;
    //   11: ifnonnull +5 -> 16
    //   14: lconst_0
    //   15: lreturn
    //   16: aload_0
    //   17: getfield 469	com/sun/media/sound/RealTimeSequencer:tempoCache	Lcom/sun/media/sound/MidiUtils$TempoCache;
    //   20: dup
    //   21: astore_1
    //   22: monitorenter
    //   23: aload_0
    //   24: getfield 477	com/sun/media/sound/RealTimeSequencer:sequence	Ljavax/sound/midi/Sequence;
    //   27: aload_0
    //   28: invokespecial 519	com/sun/media/sound/RealTimeSequencer:getDataPump	()Lcom/sun/media/sound/RealTimeSequencer$DataPump;
    //   31: invokevirtual 530	com/sun/media/sound/RealTimeSequencer$DataPump:getTickPos	()J
    //   34: aload_0
    //   35: getfield 469	com/sun/media/sound/RealTimeSequencer:tempoCache	Lcom/sun/media/sound/MidiUtils$TempoCache;
    //   38: invokestatic 493	com/sun/media/sound/MidiUtils:tick2microsecond	(Ljavax/sound/midi/Sequence;JLcom/sun/media/sound/MidiUtils$TempoCache;)J
    //   41: aload_1
    //   42: monitorexit
    //   43: lreturn
    //   44: astore_2
    //   45: aload_1
    //   46: monitorexit
    //   47: aload_2
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	RealTimeSequencer
    //   21	25	1	Ljava/lang/Object;	Object
    //   44	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   23	43	44	finally
    //   44	47	44	finally
  }
  
  public void setMicrosecondPosition(long paramLong)
  {
    if (paramLong < 0L) {
      return;
    }
    if (getDataPump() == null)
    {
      if (paramLong == 0L) {}
    }
    else if (sequence == null)
    {
      if (paramLong == 0L) {}
    }
    else {
      synchronized (tempoCache)
      {
        setTickPosition(MidiUtils.microsecond2tick(sequence, paramLong, tempoCache));
      }
    }
  }
  
  public void setMasterSyncMode(Sequencer.SyncMode paramSyncMode) {}
  
  public Sequencer.SyncMode getMasterSyncMode()
  {
    return masterSyncMode;
  }
  
  public Sequencer.SyncMode[] getMasterSyncModes()
  {
    Sequencer.SyncMode[] arrayOfSyncMode = new Sequencer.SyncMode[masterSyncModes.length];
    System.arraycopy(masterSyncModes, 0, arrayOfSyncMode, 0, masterSyncModes.length);
    return arrayOfSyncMode;
  }
  
  public void setSlaveSyncMode(Sequencer.SyncMode paramSyncMode) {}
  
  public Sequencer.SyncMode getSlaveSyncMode()
  {
    return slaveSyncMode;
  }
  
  public Sequencer.SyncMode[] getSlaveSyncModes()
  {
    Sequencer.SyncMode[] arrayOfSyncMode = new Sequencer.SyncMode[slaveSyncModes.length];
    System.arraycopy(slaveSyncModes, 0, arrayOfSyncMode, 0, slaveSyncModes.length);
    return arrayOfSyncMode;
  }
  
  int getTrackCount()
  {
    Sequence localSequence = getSequence();
    if (localSequence != null) {
      return sequence.getTracks().length;
    }
    return 0;
  }
  
  public synchronized void setTrackMute(int paramInt, boolean paramBoolean)
  {
    int i = getTrackCount();
    if ((paramInt < 0) || (paramInt >= getTrackCount())) {
      return;
    }
    trackMuted = ensureBoolArraySize(trackMuted, i);
    trackMuted[paramInt] = paramBoolean;
    if (getDataPump() != null) {
      getDataPump().muteSoloChanged();
    }
  }
  
  public synchronized boolean getTrackMute(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getTrackCount())) {
      return false;
    }
    if ((trackMuted == null) || (trackMuted.length <= paramInt)) {
      return false;
    }
    return trackMuted[paramInt];
  }
  
  public synchronized void setTrackSolo(int paramInt, boolean paramBoolean)
  {
    int i = getTrackCount();
    if ((paramInt < 0) || (paramInt >= getTrackCount())) {
      return;
    }
    trackSolo = ensureBoolArraySize(trackSolo, i);
    trackSolo[paramInt] = paramBoolean;
    if (getDataPump() != null) {
      getDataPump().muteSoloChanged();
    }
  }
  
  public synchronized boolean getTrackSolo(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getTrackCount())) {
      return false;
    }
    if ((trackSolo == null) || (trackSolo.length <= paramInt)) {
      return false;
    }
    return trackSolo[paramInt];
  }
  
  public boolean addMetaEventListener(MetaEventListener paramMetaEventListener)
  {
    synchronized (metaEventListeners)
    {
      if (!metaEventListeners.contains(paramMetaEventListener)) {
        metaEventListeners.add(paramMetaEventListener);
      }
      return true;
    }
  }
  
  public void removeMetaEventListener(MetaEventListener paramMetaEventListener)
  {
    synchronized (metaEventListeners)
    {
      int i = metaEventListeners.indexOf(paramMetaEventListener);
      if (i >= 0) {
        metaEventListeners.remove(i);
      }
    }
  }
  
  public int[] addControllerEventListener(ControllerEventListener paramControllerEventListener, int[] paramArrayOfInt)
  {
    synchronized (controllerEventListeners)
    {
      ControllerListElement localControllerListElement = null;
      int i = 0;
      for (int j = 0; j < controllerEventListeners.size(); j++)
      {
        localControllerListElement = (ControllerListElement)controllerEventListeners.get(j);
        if (listener.equals(paramControllerEventListener))
        {
          localControllerListElement.addControllers(paramArrayOfInt);
          i = 1;
          break;
        }
      }
      if (i == 0)
      {
        localControllerListElement = new ControllerListElement(paramControllerEventListener, paramArrayOfInt, null);
        controllerEventListeners.add(localControllerListElement);
      }
      return localControllerListElement.getControllers();
    }
  }
  
  public int[] removeControllerEventListener(ControllerEventListener paramControllerEventListener, int[] paramArrayOfInt)
  {
    synchronized (controllerEventListeners)
    {
      ControllerListElement localControllerListElement = null;
      int i = 0;
      for (int j = 0; j < controllerEventListeners.size(); j++)
      {
        localControllerListElement = (ControllerListElement)controllerEventListeners.get(j);
        if (listener.equals(paramControllerEventListener))
        {
          localControllerListElement.removeControllers(paramArrayOfInt);
          i = 1;
          break;
        }
      }
      if (i == 0) {
        return new int[0];
      }
      if (paramArrayOfInt == null)
      {
        j = controllerEventListeners.indexOf(localControllerListElement);
        if (j >= 0) {
          controllerEventListeners.remove(j);
        }
        return new int[0];
      }
      return localControllerListElement.getControllers();
    }
  }
  
  public void setLoopStartPoint(long paramLong)
  {
    if ((paramLong > getTickLength()) || ((loopEnd != -1L) && (paramLong > loopEnd)) || (paramLong < 0L)) {
      throw new IllegalArgumentException("invalid loop start point: " + paramLong);
    }
    loopStart = paramLong;
  }
  
  public long getLoopStartPoint()
  {
    return loopStart;
  }
  
  public void setLoopEndPoint(long paramLong)
  {
    if ((paramLong > getTickLength()) || ((loopStart > paramLong) && (paramLong != -1L)) || (paramLong < -1L)) {
      throw new IllegalArgumentException("invalid loop end point: " + paramLong);
    }
    loopEnd = paramLong;
  }
  
  public long getLoopEndPoint()
  {
    return loopEnd;
  }
  
  public void setLoopCount(int paramInt)
  {
    if ((paramInt != -1) && (paramInt < 0)) {
      throw new IllegalArgumentException("illegal value for loop count: " + paramInt);
    }
    loopCount = paramInt;
    if (getDataPump() != null) {
      getDataPump().resetLoopCount();
    }
  }
  
  public int getLoopCount()
  {
    return loopCount;
  }
  
  protected void implOpen()
    throws MidiUnavailableException
  {
    playThread = new PlayThread();
    if (sequence != null) {
      playThread.setSequence(sequence);
    }
    propagateCaches();
    if (doAutoConnectAtNextOpen) {
      doAutoConnect();
    }
  }
  
  /* Error */
  private void doAutoConnect()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: invokestatic 568	javax/sound/midi/MidiSystem:getSynthesizer	()Ljavax/sound/midi/Synthesizer;
    //   5: astore_2
    //   6: aload_2
    //   7: instanceof 259
    //   10: ifeq +16 -> 26
    //   13: aload_2
    //   14: checkcast 259	com/sun/media/sound/ReferenceCountingDevice
    //   17: invokeinterface 574 1 0
    //   22: astore_1
    //   23: goto +42 -> 65
    //   26: aload_2
    //   27: invokeinterface 581 1 0
    //   32: aload_2
    //   33: invokeinterface 582 1 0
    //   38: astore_1
    //   39: aload_1
    //   40: ifnonnull +25 -> 65
    //   43: aload_2
    //   44: invokeinterface 580 1 0
    //   49: goto +16 -> 65
    //   52: astore_3
    //   53: aload_1
    //   54: ifnonnull +9 -> 63
    //   57: aload_2
    //   58: invokeinterface 580 1 0
    //   63: aload_3
    //   64: athrow
    //   65: goto +4 -> 69
    //   68: astore_2
    //   69: aload_1
    //   70: ifnonnull +11 -> 81
    //   73: invokestatic 567	javax/sound/midi/MidiSystem:getReceiver	()Ljavax/sound/midi/Receiver;
    //   76: astore_1
    //   77: goto +4 -> 81
    //   80: astore_2
    //   81: aload_1
    //   82: ifnull +22 -> 104
    //   85: aload_0
    //   86: aload_1
    //   87: putfield 476	com/sun/media/sound/RealTimeSequencer:autoConnectedReceiver	Ljavax/sound/midi/Receiver;
    //   90: aload_0
    //   91: invokevirtual 523	com/sun/media/sound/RealTimeSequencer:getTransmitter	()Ljavax/sound/midi/Transmitter;
    //   94: aload_1
    //   95: invokeinterface 583 2 0
    //   100: goto +4 -> 104
    //   103: astore_2
    //   104: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	105	0	this	RealTimeSequencer
    //   1	94	1	localReceiver	Receiver
    //   5	53	2	localSynthesizer	javax.sound.midi.Synthesizer
    //   68	1	2	localException1	Exception
    //   80	1	2	localException2	Exception
    //   103	1	2	localException3	Exception
    //   52	12	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   32	39	52	finally
    //   2	65	68	java/lang/Exception
    //   73	77	80	java/lang/Exception
    //   90	100	103	java/lang/Exception
  }
  
  private synchronized void propagateCaches()
  {
    if ((sequence != null) && (isOpen()))
    {
      if (cacheTempoFactor != -1.0F) {
        setTempoFactor(cacheTempoFactor);
      }
      if (cacheTempoMPQ == -1.0D) {
        setTempoInMPQ(new MidiUtils.TempoCache(sequence).getTempoMPQAt(getTickPosition()));
      } else {
        setTempoInMPQ((float)cacheTempoMPQ);
      }
    }
  }
  
  private synchronized void setCaches()
  {
    cacheTempoFactor = getTempoFactor();
    cacheTempoMPQ = getTempoInMPQ();
  }
  
  protected synchronized void implClose()
  {
    if (playThread != null)
    {
      playThread.close();
      playThread = null;
    }
    super.implClose();
    sequence = null;
    running = false;
    cacheTempoMPQ = -1.0D;
    cacheTempoFactor = -1.0F;
    trackMuted = null;
    trackSolo = null;
    loopStart = 0L;
    loopEnd = -1L;
    loopCount = 0;
    doAutoConnectAtNextOpen = autoConnect;
    if (autoConnectedReceiver != null)
    {
      try
      {
        autoConnectedReceiver.close();
      }
      catch (Exception localException) {}
      autoConnectedReceiver = null;
    }
  }
  
  void implStart()
  {
    if (playThread == null) {
      return;
    }
    tempoCache.refresh(sequence);
    if (!running)
    {
      running = true;
      playThread.start();
    }
  }
  
  void implStop()
  {
    if (playThread == null) {
      return;
    }
    recording = false;
    if (running)
    {
      running = false;
      playThread.stop();
    }
  }
  
  private static EventDispatcher getEventDispatcher()
  {
    ThreadGroup localThreadGroup = Thread.currentThread().getThreadGroup();
    synchronized (dispatchers)
    {
      EventDispatcher localEventDispatcher = (EventDispatcher)dispatchers.get(localThreadGroup);
      if (localEventDispatcher == null)
      {
        localEventDispatcher = new EventDispatcher();
        dispatchers.put(localThreadGroup, localEventDispatcher);
        localEventDispatcher.start();
      }
      return localEventDispatcher;
    }
  }
  
  void sendMetaEvents(MidiMessage paramMidiMessage)
  {
    if (metaEventListeners.size() == 0) {
      return;
    }
    getEventDispatcher().sendAudioEvents(paramMidiMessage, metaEventListeners);
  }
  
  void sendControllerEvents(MidiMessage paramMidiMessage)
  {
    int i = controllerEventListeners.size();
    if (i == 0) {
      return;
    }
    if (!(paramMidiMessage instanceof ShortMessage)) {
      return;
    }
    ShortMessage localShortMessage = (ShortMessage)paramMidiMessage;
    int j = localShortMessage.getData1();
    ArrayList localArrayList = new ArrayList();
    for (int k = 0; k < i; k++)
    {
      ControllerListElement localControllerListElement = (ControllerListElement)controllerEventListeners.get(k);
      for (int m = 0; m < controllers.length; m++) {
        if (controllers[m] == j)
        {
          localArrayList.add(listener);
          break;
        }
      }
    }
    getEventDispatcher().sendAudioEvents(paramMidiMessage, localArrayList);
  }
  
  private boolean needCaching()
  {
    return (!isOpen()) || (sequence == null) || (playThread == null);
  }
  
  private DataPump getDataPump()
  {
    if (playThread != null) {
      return playThread.getDataPump();
    }
    return null;
  }
  
  private MidiUtils.TempoCache getTempoCache()
  {
    return tempoCache;
  }
  
  private static boolean[] ensureBoolArraySize(boolean[] paramArrayOfBoolean, int paramInt)
  {
    if (paramArrayOfBoolean == null) {
      return new boolean[paramInt];
    }
    if (paramArrayOfBoolean.length < paramInt)
    {
      boolean[] arrayOfBoolean = new boolean[paramInt];
      System.arraycopy(paramArrayOfBoolean, 0, arrayOfBoolean, 0, paramArrayOfBoolean.length);
      return arrayOfBoolean;
    }
    return paramArrayOfBoolean;
  }
  
  protected boolean hasReceivers()
  {
    return true;
  }
  
  protected Receiver createReceiver()
    throws MidiUnavailableException
  {
    return new SequencerReceiver();
  }
  
  protected boolean hasTransmitters()
  {
    return true;
  }
  
  protected Transmitter createTransmitter()
    throws MidiUnavailableException
  {
    return new SequencerTransmitter(null);
  }
  
  public void setAutoConnect(Receiver paramReceiver)
  {
    autoConnect = (paramReceiver != null);
    autoConnectedReceiver = paramReceiver;
  }
  
  private class ControllerListElement
  {
    int[] controllers;
    final ControllerEventListener listener;
    
    private ControllerListElement(ControllerEventListener paramControllerEventListener, int[] paramArrayOfInt)
    {
      listener = paramControllerEventListener;
      if (paramArrayOfInt == null)
      {
        paramArrayOfInt = new int[''];
        for (int i = 0; i < 128; i++) {
          paramArrayOfInt[i] = i;
        }
      }
      controllers = paramArrayOfInt;
    }
    
    private void addControllers(int[] paramArrayOfInt)
    {
      if (paramArrayOfInt == null)
      {
        controllers = new int[''];
        for (int i = 0; i < 128; i++) {
          controllers[i] = i;
        }
        return;
      }
      int[] arrayOfInt1 = new int[controllers.length + paramArrayOfInt.length];
      for (int k = 0; k < controllers.length; k++) {
        arrayOfInt1[k] = controllers[k];
      }
      int j = controllers.length;
      for (k = 0; k < paramArrayOfInt.length; k++)
      {
        m = 0;
        for (int n = 0; n < controllers.length; n++) {
          if (paramArrayOfInt[k] == controllers[n])
          {
            m = 1;
            break;
          }
        }
        if (m == 0) {
          arrayOfInt1[(j++)] = paramArrayOfInt[k];
        }
      }
      int[] arrayOfInt2 = new int[j];
      for (int m = 0; m < j; m++) {
        arrayOfInt2[m] = arrayOfInt1[m];
      }
      controllers = arrayOfInt2;
    }
    
    private void removeControllers(int[] paramArrayOfInt)
    {
      if (paramArrayOfInt == null)
      {
        controllers = new int[0];
      }
      else
      {
        int[] arrayOfInt1 = new int[controllers.length];
        int i = 0;
        for (int j = 0; j < controllers.length; j++)
        {
          k = 0;
          for (int m = 0; m < paramArrayOfInt.length; m++) {
            if (controllers[j] == paramArrayOfInt[m])
            {
              k = 1;
              break;
            }
          }
          if (k == 0) {
            arrayOfInt1[(i++)] = controllers[j];
          }
        }
        int[] arrayOfInt2 = new int[i];
        for (int k = 0; k < i; k++) {
          arrayOfInt2[k] = arrayOfInt1[k];
        }
        controllers = arrayOfInt2;
      }
    }
    
    private int[] getControllers()
    {
      if (controllers == null) {
        return null;
      }
      int[] arrayOfInt = new int[controllers.length];
      for (int i = 0; i < controllers.length; i++) {
        arrayOfInt[i] = controllers[i];
      }
      return arrayOfInt;
    }
  }
  
  private class DataPump
  {
    private float currTempo;
    private float tempoFactor;
    private float inverseTempoFactor;
    private long ignoreTempoEventAt;
    private int resolution;
    private float divisionType;
    private long checkPointMillis;
    private long checkPointTick;
    private int[] noteOnCache;
    private Track[] tracks;
    private boolean[] trackDisabled;
    private int[] trackReadPos;
    private long lastTick;
    private boolean needReindex = false;
    private int currLoopCounter = 0;
    
    DataPump()
    {
      init();
    }
    
    synchronized void init()
    {
      ignoreTempoEventAt = -1L;
      tempoFactor = 1.0F;
      inverseTempoFactor = 1.0F;
      noteOnCache = new int[''];
      tracks = null;
      trackDisabled = null;
    }
    
    synchronized void setTickPos(long paramLong)
    {
      long l = paramLong;
      lastTick = paramLong;
      if (running) {
        notesOff(false);
      }
      if ((running) || (paramLong > 0L)) {
        chaseEvents(l, paramLong);
      } else {
        needReindex = true;
      }
      if (!hasCachedTempo())
      {
        setTempoMPQ(RealTimeSequencer.this.getTempoCache().getTempoMPQAt(lastTick, currTempo));
        ignoreTempoEventAt = -1L;
      }
      checkPointMillis = 0L;
    }
    
    long getTickPos()
    {
      return lastTick;
    }
    
    boolean hasCachedTempo()
    {
      if (ignoreTempoEventAt != lastTick) {
        ignoreTempoEventAt = -1L;
      }
      return ignoreTempoEventAt >= 0L;
    }
    
    synchronized void setTempoMPQ(float paramFloat)
    {
      if ((paramFloat > 0.0F) && (paramFloat != currTempo))
      {
        ignoreTempoEventAt = lastTick;
        currTempo = paramFloat;
        checkPointMillis = 0L;
      }
    }
    
    float getTempoMPQ()
    {
      return currTempo;
    }
    
    synchronized void setTempoFactor(float paramFloat)
    {
      if ((paramFloat > 0.0F) && (paramFloat != tempoFactor))
      {
        tempoFactor = paramFloat;
        inverseTempoFactor = (1.0F / paramFloat);
        checkPointMillis = 0L;
      }
    }
    
    float getTempoFactor()
    {
      return tempoFactor;
    }
    
    synchronized void muteSoloChanged()
    {
      boolean[] arrayOfBoolean = makeDisabledArray();
      if (running) {
        applyDisabledTracks(trackDisabled, arrayOfBoolean);
      }
      trackDisabled = arrayOfBoolean;
    }
    
    synchronized void setSequence(Sequence paramSequence)
    {
      if (paramSequence == null)
      {
        init();
        return;
      }
      tracks = paramSequence.getTracks();
      muteSoloChanged();
      resolution = paramSequence.getResolution();
      divisionType = paramSequence.getDivisionType();
      trackReadPos = new int[tracks.length];
      checkPointMillis = 0L;
      needReindex = true;
    }
    
    synchronized void resetLoopCount()
    {
      currLoopCounter = loopCount;
    }
    
    void clearNoteOnCache()
    {
      for (int i = 0; i < 128; i++) {
        noteOnCache[i] = 0;
      }
    }
    
    void notesOff(boolean paramBoolean)
    {
      int i = 0;
      for (int j = 0; j < 16; j++)
      {
        int k = 1 << j;
        for (int m = 0; m < 128; m++) {
          if ((noteOnCache[m] & k) != 0)
          {
            noteOnCache[m] ^= k;
            getTransmitterList().sendMessage(0x90 | j | m << 8, -1L);
            i++;
          }
        }
        getTransmitterList().sendMessage(0xB0 | j | 0x7B00, -1L);
        getTransmitterList().sendMessage(0xB0 | j | 0x4000, -1L);
        if (paramBoolean)
        {
          getTransmitterList().sendMessage(0xB0 | j | 0x7900, -1L);
          i++;
        }
      }
    }
    
    private boolean[] makeDisabledArray()
    {
      if (tracks == null) {
        return null;
      }
      boolean[] arrayOfBoolean1 = new boolean[tracks.length];
      boolean[] arrayOfBoolean3;
      boolean[] arrayOfBoolean2;
      synchronized (RealTimeSequencer.this)
      {
        arrayOfBoolean3 = trackMuted;
        arrayOfBoolean2 = trackSolo;
      }
      int i = 0;
      int j;
      if (arrayOfBoolean2 != null) {
        for (j = 0; j < arrayOfBoolean2.length; j++) {
          if (arrayOfBoolean2[j] != 0)
          {
            i = 1;
            break;
          }
        }
      }
      if (i != 0) {
        for (j = 0; j < arrayOfBoolean1.length; j++) {
          arrayOfBoolean1[j] = ((j >= arrayOfBoolean2.length) || (arrayOfBoolean2[j] == 0) ? 1 : false);
        }
      } else {
        for (j = 0; j < arrayOfBoolean1.length; j++) {
          arrayOfBoolean1[j] = ((arrayOfBoolean3 != null) && (j < arrayOfBoolean3.length) && (arrayOfBoolean3[j] != 0) ? 1 : false);
        }
      }
      return arrayOfBoolean1;
    }
    
    private void sendNoteOffIfOn(Track paramTrack, long paramLong)
    {
      int i = paramTrack.size();
      int j = 0;
      try
      {
        for (int k = 0; k < i; k++)
        {
          MidiEvent localMidiEvent = paramTrack.get(k);
          if (localMidiEvent.getTick() > paramLong) {
            break;
          }
          MidiMessage localMidiMessage = localMidiEvent.getMessage();
          int m = localMidiMessage.getStatus();
          int n = localMidiMessage.getLength();
          if ((n == 3) && ((m & 0xF0) == 144))
          {
            int i1 = -1;
            Object localObject;
            if ((localMidiMessage instanceof ShortMessage))
            {
              localObject = (ShortMessage)localMidiMessage;
              if (((ShortMessage)localObject).getData2() > 0) {
                i1 = ((ShortMessage)localObject).getData1();
              }
            }
            else
            {
              localObject = localMidiMessage.getMessage();
              if ((localObject[2] & 0x7F) > 0) {
                i1 = localObject[1] & 0x7F;
              }
            }
            if (i1 >= 0)
            {
              int i2 = 1 << (m & 0xF);
              if ((noteOnCache[i1] & i2) != 0)
              {
                getTransmitterList().sendMessage(m | i1 << 8, -1L);
                noteOnCache[i1] &= (0xFFFF ^ i2);
                j++;
              }
            }
          }
        }
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    }
    
    private void applyDisabledTracks(boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2)
    {
      byte[][] arrayOfByte = (byte[][])null;
      synchronized (RealTimeSequencer.this)
      {
        for (int i = 0; i < paramArrayOfBoolean2.length; i++) {
          if (((paramArrayOfBoolean1 == null) || (i >= paramArrayOfBoolean1.length) || (paramArrayOfBoolean1[i] == 0)) && (paramArrayOfBoolean2[i] != 0))
          {
            if (tracks.length > i) {
              sendNoteOffIfOn(tracks[i], lastTick);
            }
          }
          else if ((paramArrayOfBoolean1 != null) && (i < paramArrayOfBoolean1.length) && (paramArrayOfBoolean1[i] != 0) && (paramArrayOfBoolean2[i] == 0))
          {
            if (arrayOfByte == null) {
              arrayOfByte = new byte[''][16];
            }
            chaseTrackEvents(i, 0L, lastTick, true, arrayOfByte);
          }
        }
      }
    }
    
    private void chaseTrackEvents(int paramInt, long paramLong1, long paramLong2, boolean paramBoolean, byte[][] paramArrayOfByte)
    {
      if (paramLong1 > paramLong2) {
        paramLong1 = 0L;
      }
      byte[] arrayOfByte = new byte[16];
      for (int i = 0; i < 16; i++)
      {
        arrayOfByte[i] = -1;
        for (j = 0; j < 128; j++) {
          paramArrayOfByte[j][i] = -1;
        }
      }
      Track localTrack = tracks[paramInt];
      int j = localTrack.size();
      int i2;
      int i3;
      try
      {
        for (int k = 0; k < j; k++)
        {
          MidiEvent localMidiEvent = localTrack.get(k);
          if (localMidiEvent.getTick() >= paramLong2)
          {
            if ((!paramBoolean) || (paramInt >= trackReadPos.length)) {
              break;
            }
            trackReadPos[paramInt] = (k > 0 ? k - 1 : 0);
            break;
          }
          MidiMessage localMidiMessage = localMidiEvent.getMessage();
          i2 = localMidiMessage.getStatus();
          i3 = localMidiMessage.getLength();
          Object localObject;
          if ((i3 == 3) && ((i2 & 0xF0) == 176)) {
            if ((localMidiMessage instanceof ShortMessage))
            {
              localObject = (ShortMessage)localMidiMessage;
              paramArrayOfByte[(localObject.getData1() & 0x7F)][(i2 & 0xF)] = ((byte)((ShortMessage)localObject).getData2());
            }
            else
            {
              localObject = localMidiMessage.getMessage();
              paramArrayOfByte[(localObject[1] & 0x7F)][(i2 & 0xF)] = localObject[2];
            }
          }
          if ((i3 == 2) && ((i2 & 0xF0) == 192)) {
            if ((localMidiMessage instanceof ShortMessage))
            {
              localObject = (ShortMessage)localMidiMessage;
              arrayOfByte[(i2 & 0xF)] = ((byte)((ShortMessage)localObject).getData1());
            }
            else
            {
              localObject = localMidiMessage.getMessage();
              arrayOfByte[(i2 & 0xF)] = localObject[1];
            }
          }
        }
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
      int m = 0;
      for (int n = 0; n < 16; n++)
      {
        for (int i1 = 0; i1 < 128; i1++)
        {
          i2 = paramArrayOfByte[i1][n];
          if (i2 >= 0)
          {
            i3 = 0xB0 | n | i1 << 8 | i2 << 16;
            getTransmitterList().sendMessage(i3, -1L);
            m++;
          }
        }
        if (arrayOfByte[n] >= 0) {
          getTransmitterList().sendMessage(0xC0 | n | arrayOfByte[n] << 8, -1L);
        }
        if ((arrayOfByte[n] >= 0) || (paramLong1 == 0L) || (paramLong2 == 0L))
        {
          getTransmitterList().sendMessage(0xE0 | n | 0x400000, -1L);
          getTransmitterList().sendMessage(0xB0 | n | 0x4000, -1L);
        }
      }
    }
    
    synchronized void chaseEvents(long paramLong1, long paramLong2)
    {
      byte[][] arrayOfByte = new byte[''][16];
      for (int i = 0; i < tracks.length; i++) {
        if ((trackDisabled == null) || (trackDisabled.length <= i) || (trackDisabled[i] == 0)) {
          chaseTrackEvents(i, paramLong1, paramLong2, true, arrayOfByte);
        }
      }
    }
    
    private long getCurrentTimeMillis()
    {
      return System.nanoTime() / 1000000L;
    }
    
    private long millis2tick(long paramLong)
    {
      if (divisionType != 0.0F)
      {
        double d = paramLong * tempoFactor * divisionType * resolution / 1000.0D;
        return d;
      }
      return MidiUtils.microsec2ticks(paramLong * 1000L, currTempo * inverseTempoFactor, resolution);
    }
    
    private long tick2millis(long paramLong)
    {
      if (divisionType != 0.0F)
      {
        double d = paramLong * 1000.0D / (tempoFactor * divisionType * resolution);
        return d;
      }
      return MidiUtils.ticks2microsec(paramLong, currTempo * inverseTempoFactor, resolution) / 1000L;
    }
    
    private void ReindexTrack(int paramInt, long paramLong)
    {
      if ((paramInt < trackReadPos.length) && (paramInt < tracks.length)) {
        trackReadPos[paramInt] = MidiUtils.tick2index(tracks[paramInt], paramLong);
      }
    }
    
    private boolean dispatchMessage(int paramInt, MidiEvent paramMidiEvent)
    {
      boolean bool = false;
      MidiMessage localMidiMessage = paramMidiEvent.getMessage();
      int i = localMidiMessage.getStatus();
      int j = localMidiMessage.getLength();
      int k;
      if ((i == 255) && (j >= 2))
      {
        if (paramInt == 0)
        {
          k = MidiUtils.getTempoMPQ(localMidiMessage);
          if (k > 0)
          {
            if (paramMidiEvent.getTick() != ignoreTempoEventAt)
            {
              setTempoMPQ(k);
              bool = true;
            }
            ignoreTempoEventAt = -1L;
          }
        }
        sendMetaEvents(localMidiMessage);
      }
      else
      {
        getTransmitterList().sendMessage(localMidiMessage, -1L);
        switch (i & 0xF0)
        {
        case 128: 
          k = ((ShortMessage)localMidiMessage).getData1() & 0x7F;
          noteOnCache[k] &= (0xFFFF ^ 1 << (i & 0xF));
          break;
        case 144: 
          ShortMessage localShortMessage = (ShortMessage)localMidiMessage;
          int m = localShortMessage.getData1() & 0x7F;
          int n = localShortMessage.getData2() & 0x7F;
          if (n > 0) {
            noteOnCache[m] |= 1 << (i & 0xF);
          } else {
            noteOnCache[m] &= (0xFFFF ^ 1 << (i & 0xF));
          }
          break;
        case 176: 
          sendControllerEvents(localMidiMessage);
        }
      }
      return bool;
    }
    
    synchronized boolean pump()
    {
      long l2 = lastTick;
      boolean bool1 = false;
      int i = 0;
      boolean bool2 = false;
      long l1 = getCurrentTimeMillis();
      int j = 0;
      do
      {
        bool1 = false;
        if (needReindex)
        {
          if (trackReadPos.length < tracks.length) {
            trackReadPos = new int[tracks.length];
          }
          for (k = 0; k < tracks.length; k++) {
            ReindexTrack(k, l2);
          }
          needReindex = false;
          checkPointMillis = 0L;
        }
        if (checkPointMillis == 0L)
        {
          l1 = getCurrentTimeMillis();
          checkPointMillis = l1;
          l2 = lastTick;
          checkPointTick = l2;
        }
        else
        {
          l2 = checkPointTick + millis2tick(l1 - checkPointMillis);
          if ((loopEnd != -1L) && (((loopCount > 0) && (currLoopCounter > 0)) || ((loopCount == -1) && (lastTick <= loopEnd) && (l2 >= loopEnd))))
          {
            l2 = loopEnd - 1L;
            i = 1;
          }
          lastTick = l2;
        }
        j = 0;
        for (int k = 0; k < tracks.length; k++)
        {
          try
          {
            int m = trackDisabled[k];
            Track localTrack = tracks[k];
            int n = trackReadPos[k];
            int i1 = localTrack.size();
            MidiEvent localMidiEvent;
            while ((!bool1) && (n < i1) && ((localMidiEvent = localTrack.get(n)).getTick() <= l2))
            {
              if ((n == i1 - 1) && (MidiUtils.isMetaEndOfTrack(localMidiEvent.getMessage())))
              {
                n = i1;
                break;
              }
              n++;
              if ((m == 0) || ((k == 0) && (MidiUtils.isMetaTempo(localMidiEvent.getMessage())))) {
                bool1 = dispatchMessage(k, localMidiEvent);
              }
            }
            if (n >= i1) {
              j++;
            }
            trackReadPos[k] = n;
          }
          catch (Exception localException)
          {
            if ((localException instanceof ArrayIndexOutOfBoundsException))
            {
              needReindex = true;
              bool1 = true;
            }
          }
          if (bool1) {
            break;
          }
        }
        bool2 = j == tracks.length;
        if ((i != 0) || (((loopCount > 0) && (currLoopCounter > 0)) || ((loopCount == -1) && (!bool1) && (loopEnd == -1L) && (bool2))))
        {
          long l3 = checkPointMillis;
          long l4 = loopEnd;
          if (l4 == -1L) {
            l4 = lastTick;
          }
          if (loopCount != -1) {
            currLoopCounter -= 1;
          }
          setTickPos(loopStart);
          checkPointMillis = (l3 + tick2millis(l4 - checkPointTick));
          checkPointTick = loopStart;
          needReindex = false;
          bool1 = false;
          i = 0;
          bool2 = false;
        }
      } while (bool1);
      return bool2;
    }
  }
  
  final class PlayThread
    implements Runnable
  {
    private Thread thread;
    private final Object lock = new Object();
    boolean interrupted = false;
    boolean isPumping = false;
    private final RealTimeSequencer.DataPump dataPump = new RealTimeSequencer.DataPump(RealTimeSequencer.this);
    
    PlayThread()
    {
      int i = 8;
      thread = JSSecurityManager.createThread(this, "Java Sound Sequencer", false, i, true);
    }
    
    RealTimeSequencer.DataPump getDataPump()
    {
      return dataPump;
    }
    
    synchronized void setSequence(Sequence paramSequence)
    {
      dataPump.setSequence(paramSequence);
    }
    
    synchronized void start()
    {
      running = true;
      if (!dataPump.hasCachedTempo())
      {
        long l = getTickPosition();
        dataPump.setTempoMPQ(tempoCache.getTempoMPQAt(l));
      }
      dataPump.checkPointMillis = 0L;
      dataPump.clearNoteOnCache();
      dataPump.needReindex = true;
      dataPump.resetLoopCount();
      synchronized (lock)
      {
        lock.notifyAll();
      }
    }
    
    synchronized void stop()
    {
      playThreadImplStop();
      long l = System.nanoTime() / 1000000L;
      while (isPumping)
      {
        synchronized (lock)
        {
          try
          {
            lock.wait(2000L);
          }
          catch (InterruptedException localInterruptedException) {}
        }
        if (System.nanoTime() / 1000000L - l <= 1900L) {}
      }
    }
    
    void playThreadImplStop()
    {
      running = false;
      synchronized (lock)
      {
        lock.notifyAll();
      }
    }
    
    void close()
    {
      Thread localThread = null;
      synchronized (this)
      {
        interrupted = true;
        localThread = thread;
        thread = null;
      }
      if (localThread != null) {
        synchronized (lock)
        {
          lock.notifyAll();
        }
      }
      if (localThread != null) {
        try
        {
          localThread.join(2000L);
        }
        catch (InterruptedException localInterruptedException) {}
      }
    }
    
    public void run()
    {
      while (!interrupted)
      {
        boolean bool1 = false;
        boolean bool2 = running;
        isPumping = ((!interrupted) && (running));
        while ((!bool1) && (!interrupted) && (running))
        {
          bool1 = dataPump.pump();
          try
          {
            Thread.sleep(1L);
          }
          catch (InterruptedException localInterruptedException) {}
        }
        playThreadImplStop();
        if (bool2) {
          dataPump.notesOff(true);
        }
        if (bool1)
        {
          dataPump.setTickPos(sequence.getTickLength());
          MetaMessage localMetaMessage = new MetaMessage();
          try
          {
            localMetaMessage.setMessage(47, new byte[0], 0);
          }
          catch (InvalidMidiDataException localInvalidMidiDataException) {}
          sendMetaEvents(localMetaMessage);
        }
        synchronized (lock)
        {
          isPumping = false;
          lock.notifyAll();
          while ((!running) && (!interrupted)) {
            try
            {
              lock.wait();
            }
            catch (Exception localException) {}
          }
        }
      }
    }
  }
  
  private static class RealTimeSequencerInfo
    extends MidiDevice.Info
  {
    private static final String name = "Real Time Sequencer";
    private static final String vendor = "Oracle Corporation";
    private static final String description = "Software sequencer";
    private static final String version = "Version 1.0";
    
    private RealTimeSequencerInfo()
    {
      super("Oracle Corporation", "Software sequencer", "Version 1.0");
    }
  }
  
  static class RecordingTrack
  {
    private final Track track;
    private int channel;
    
    RecordingTrack(Track paramTrack, int paramInt)
    {
      track = paramTrack;
      channel = paramInt;
    }
    
    static RecordingTrack get(List paramList, Track paramTrack)
    {
      synchronized (paramList)
      {
        int i = paramList.size();
        for (int j = 0; j < i; j++)
        {
          RecordingTrack localRecordingTrack = (RecordingTrack)paramList.get(j);
          if (track == paramTrack) {
            return localRecordingTrack;
          }
        }
      }
      return null;
    }
    
    static Track get(List paramList, int paramInt)
    {
      synchronized (paramList)
      {
        int i = paramList.size();
        for (int j = 0; j < i; j++)
        {
          RecordingTrack localRecordingTrack = (RecordingTrack)paramList.get(j);
          if ((channel == paramInt) || (channel == -1)) {
            return track;
          }
        }
      }
      return null;
    }
  }
  
  final class SequencerReceiver
    extends AbstractMidiDevice.AbstractReceiver
  {
    SequencerReceiver()
    {
      super();
    }
    
    void implSend(MidiMessage paramMidiMessage, long paramLong)
    {
      if (recording)
      {
        long l = 0L;
        if (paramLong < 0L) {
          l = getTickPosition();
        } else {
          synchronized (tempoCache)
          {
            l = MidiUtils.microsecond2tick(sequence, paramLong, tempoCache);
          }
        }
        ??? = null;
        if (paramMidiMessage.getLength() > 1)
        {
          Object localObject2;
          if ((paramMidiMessage instanceof ShortMessage))
          {
            localObject2 = (ShortMessage)paramMidiMessage;
            if ((((ShortMessage)localObject2).getStatus() & 0xF0) != 240) {
              ??? = RealTimeSequencer.RecordingTrack.get(recordingTracks, ((ShortMessage)localObject2).getChannel());
            }
          }
          else
          {
            ??? = RealTimeSequencer.RecordingTrack.get(recordingTracks, -1);
          }
          if (??? != null)
          {
            if ((paramMidiMessage instanceof ShortMessage)) {
              paramMidiMessage = new FastShortMessage((ShortMessage)paramMidiMessage);
            } else {
              paramMidiMessage = (MidiMessage)paramMidiMessage.clone();
            }
            localObject2 = new MidiEvent(paramMidiMessage, l);
            ((Track)???).add((MidiEvent)localObject2);
          }
        }
      }
    }
  }
  
  private class SequencerTransmitter
    extends AbstractMidiDevice.BasicTransmitter
  {
    private SequencerTransmitter()
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\RealTimeSequencer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */