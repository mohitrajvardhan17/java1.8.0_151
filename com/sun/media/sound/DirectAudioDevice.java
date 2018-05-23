package com.sun.media.sound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.BooleanControl.Type;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;
import javax.sound.sampled.Line;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

final class DirectAudioDevice
  extends AbstractMixer
{
  private static final int CLIP_BUFFER_TIME = 1000;
  private static final int DEFAULT_LINE_BUFFER_TIME = 500;
  private int deviceCountOpened = 0;
  private int deviceCountStarted = 0;
  
  DirectAudioDevice(DirectAudioDeviceProvider.DirectAudioDeviceInfo paramDirectAudioDeviceInfo)
  {
    super(paramDirectAudioDeviceInfo, null, null, null);
    DirectDLI localDirectDLI1 = createDataLineInfo(true);
    if (localDirectDLI1 != null)
    {
      sourceLineInfo = new Line.Info[2];
      sourceLineInfo[0] = localDirectDLI1;
      sourceLineInfo[1] = new DirectDLI(Clip.class, localDirectDLI1.getFormats(), localDirectDLI1.getHardwareFormats(), 32, -1, null);
    }
    else
    {
      sourceLineInfo = new Line.Info[0];
    }
    DirectDLI localDirectDLI2 = createDataLineInfo(false);
    if (localDirectDLI2 != null)
    {
      targetLineInfo = new Line.Info[1];
      targetLineInfo[0] = localDirectDLI2;
    }
    else
    {
      targetLineInfo = new Line.Info[0];
    }
  }
  
  private DirectDLI createDataLineInfo(boolean paramBoolean)
  {
    Vector localVector = new Vector();
    AudioFormat[] arrayOfAudioFormat1 = null;
    AudioFormat[] arrayOfAudioFormat2 = null;
    synchronized (localVector)
    {
      nGetFormats(getMixerIndex(), getDeviceID(), paramBoolean, localVector);
      if (localVector.size() > 0)
      {
        int i = localVector.size();
        int j = i;
        arrayOfAudioFormat1 = new AudioFormat[i];
        boolean bool2;
        for (int k = 0; k < i; k++)
        {
          AudioFormat localAudioFormat1 = (AudioFormat)localVector.elementAt(k);
          arrayOfAudioFormat1[k] = localAudioFormat1;
          int n = localAudioFormat1.getSampleSizeInBits();
          boolean bool1 = localAudioFormat1.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
          bool2 = localAudioFormat1.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
          if ((bool1) || (bool2)) {
            j++;
          }
        }
        arrayOfAudioFormat2 = new AudioFormat[j];
        k = 0;
        for (int m = 0; m < i; m++)
        {
          AudioFormat localAudioFormat2 = arrayOfAudioFormat1[m];
          arrayOfAudioFormat2[(k++)] = localAudioFormat2;
          int i1 = localAudioFormat2.getSampleSizeInBits();
          bool2 = localAudioFormat2.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
          boolean bool3 = localAudioFormat2.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
          if (i1 == 8)
          {
            if (bool2) {
              arrayOfAudioFormat2[(k++)] = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, localAudioFormat2.getSampleRate(), i1, localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getSampleRate(), localAudioFormat2.isBigEndian());
            } else if (bool3) {
              arrayOfAudioFormat2[(k++)] = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, localAudioFormat2.getSampleRate(), i1, localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getSampleRate(), localAudioFormat2.isBigEndian());
            }
          }
          else if ((i1 > 8) && ((bool2) || (bool3))) {
            arrayOfAudioFormat2[(k++)] = new AudioFormat(localAudioFormat2.getEncoding(), localAudioFormat2.getSampleRate(), i1, localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getSampleRate(), !localAudioFormat2.isBigEndian());
          }
        }
      }
    }
    if (arrayOfAudioFormat2 != null) {
      return new DirectDLI(paramBoolean ? SourceDataLine.class : TargetDataLine.class, arrayOfAudioFormat2, arrayOfAudioFormat1, 32, -1, null);
    }
    return null;
  }
  
  public Line getLine(Line.Info paramInfo)
    throws LineUnavailableException
  {
    Line.Info localInfo = getLineInfo(paramInfo);
    if (localInfo == null) {
      throw new IllegalArgumentException("Line unsupported: " + paramInfo);
    }
    if ((localInfo instanceof DataLine.Info))
    {
      DataLine.Info localInfo1 = (DataLine.Info)localInfo;
      int i = -1;
      AudioFormat[] arrayOfAudioFormat = null;
      if ((paramInfo instanceof DataLine.Info))
      {
        arrayOfAudioFormat = ((DataLine.Info)paramInfo).getFormats();
        i = ((DataLine.Info)paramInfo).getMaxBufferSize();
      }
      AudioFormat localAudioFormat;
      if ((arrayOfAudioFormat == null) || (arrayOfAudioFormat.length == 0))
      {
        localAudioFormat = null;
      }
      else
      {
        localAudioFormat = arrayOfAudioFormat[(arrayOfAudioFormat.length - 1)];
        if (!Toolkit.isFullySpecifiedPCMFormat(localAudioFormat)) {
          localAudioFormat = null;
        }
      }
      if (localInfo1.getLineClass().isAssignableFrom(DirectSDL.class)) {
        return new DirectSDL(localInfo1, localAudioFormat, i, this, null);
      }
      if (localInfo1.getLineClass().isAssignableFrom(DirectClip.class)) {
        return new DirectClip(localInfo1, localAudioFormat, i, this, null);
      }
      if (localInfo1.getLineClass().isAssignableFrom(DirectTDL.class)) {
        return new DirectTDL(localInfo1, localAudioFormat, i, this, null);
      }
    }
    throw new IllegalArgumentException("Line unsupported: " + paramInfo);
  }
  
  public int getMaxLines(Line.Info paramInfo)
  {
    Line.Info localInfo = getLineInfo(paramInfo);
    if (localInfo == null) {
      return 0;
    }
    if ((localInfo instanceof DataLine.Info)) {
      return getMaxSimulLines();
    }
    return 0;
  }
  
  protected void implOpen()
    throws LineUnavailableException
  {}
  
  protected void implClose() {}
  
  protected void implStart() {}
  
  protected void implStop() {}
  
  int getMixerIndex()
  {
    return ((DirectAudioDeviceProvider.DirectAudioDeviceInfo)getMixerInfo()).getIndex();
  }
  
  int getDeviceID()
  {
    return ((DirectAudioDeviceProvider.DirectAudioDeviceInfo)getMixerInfo()).getDeviceID();
  }
  
  int getMaxSimulLines()
  {
    return ((DirectAudioDeviceProvider.DirectAudioDeviceInfo)getMixerInfo()).getMaxSimulLines();
  }
  
  private static void addFormat(Vector paramVector, int paramInt1, int paramInt2, int paramInt3, float paramFloat, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
  {
    AudioFormat.Encoding localEncoding = null;
    switch (paramInt4)
    {
    case 0: 
      localEncoding = paramBoolean1 ? AudioFormat.Encoding.PCM_SIGNED : AudioFormat.Encoding.PCM_UNSIGNED;
      break;
    case 1: 
      localEncoding = AudioFormat.Encoding.ULAW;
      if (paramInt1 != 8)
      {
        paramInt1 = 8;
        paramInt2 = paramInt3;
      }
      break;
    case 2: 
      localEncoding = AudioFormat.Encoding.ALAW;
      if (paramInt1 != 8)
      {
        paramInt1 = 8;
        paramInt2 = paramInt3;
      }
      break;
    }
    if (localEncoding == null) {
      return;
    }
    if (paramInt2 <= 0) {
      if (paramInt3 > 0) {
        paramInt2 = (paramInt1 + 7) / 8 * paramInt3;
      } else {
        paramInt2 = -1;
      }
    }
    paramVector.add(new AudioFormat(localEncoding, paramFloat, paramInt1, paramInt3, paramInt2, paramFloat, paramBoolean2));
  }
  
  protected static AudioFormat getSignOrEndianChangedFormat(AudioFormat paramAudioFormat)
  {
    boolean bool1 = paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
    boolean bool2 = paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED);
    if ((paramAudioFormat.getSampleSizeInBits() > 8) && (bool1)) {
      return new AudioFormat(paramAudioFormat.getEncoding(), paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), !paramAudioFormat.isBigEndian());
    }
    if ((paramAudioFormat.getSampleSizeInBits() == 8) && ((bool1) || (bool2))) {
      return new AudioFormat(bool1 ? AudioFormat.Encoding.PCM_UNSIGNED : AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), paramAudioFormat.isBigEndian());
    }
    return null;
  }
  
  private static native void nGetFormats(int paramInt1, int paramInt2, boolean paramBoolean, Vector paramVector);
  
  private static native long nOpen(int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, float paramFloat, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean2, boolean paramBoolean3, int paramInt7)
    throws LineUnavailableException;
  
  private static native void nStart(long paramLong, boolean paramBoolean);
  
  private static native void nStop(long paramLong, boolean paramBoolean);
  
  private static native void nClose(long paramLong, boolean paramBoolean);
  
  private static native int nWrite(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, float paramFloat1, float paramFloat2);
  
  private static native int nRead(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3);
  
  private static native int nGetBufferSize(long paramLong, boolean paramBoolean);
  
  private static native boolean nIsStillDraining(long paramLong, boolean paramBoolean);
  
  private static native void nFlush(long paramLong, boolean paramBoolean);
  
  private static native int nAvailable(long paramLong, boolean paramBoolean);
  
  private static native long nGetBytePosition(long paramLong1, boolean paramBoolean, long paramLong2);
  
  private static native void nSetBytePosition(long paramLong1, boolean paramBoolean, long paramLong2);
  
  private static native boolean nRequiresServicing(long paramLong, boolean paramBoolean);
  
  private static native void nService(long paramLong, boolean paramBoolean);
  
  private static class DirectBAOS
    extends ByteArrayOutputStream
  {
    DirectBAOS() {}
    
    public byte[] getInternalBuffer()
    {
      return buf;
    }
  }
  
  private static final class DirectClip
    extends DirectAudioDevice.DirectDL
    implements Clip, Runnable, AutoClosingClip
  {
    private Thread thread;
    private byte[] audioData = null;
    private int frameSize;
    private int m_lengthInFrames;
    private int loopCount;
    private int clipBytePosition;
    private int newFramePosition;
    private int loopStartFrame;
    private int loopEndFrame;
    private boolean autoclosing = false;
    
    private DirectClip(DataLine.Info paramInfo, AudioFormat paramAudioFormat, int paramInt, DirectAudioDevice paramDirectAudioDevice)
    {
      super(paramDirectAudioDevice, paramAudioFormat, paramInt, paramDirectAudioDevice.getMixerIndex(), paramDirectAudioDevice.getDeviceID(), true);
    }
    
    public void open(AudioFormat paramAudioFormat, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws LineUnavailableException
    {
      Toolkit.isFullySpecifiedAudioFormat(paramAudioFormat);
      byte[] arrayOfByte = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
      open(paramAudioFormat, arrayOfByte, paramInt2 / paramAudioFormat.getFrameSize());
    }
    
    private void open(AudioFormat paramAudioFormat, byte[] paramArrayOfByte, int paramInt)
      throws LineUnavailableException
    {
      Toolkit.isFullySpecifiedAudioFormat(paramAudioFormat);
      synchronized (mixer)
      {
        if (isOpen()) {
          throw new IllegalStateException("Clip is already open with format " + getFormat() + " and frame lengh of " + getFrameLength());
        }
        audioData = paramArrayOfByte;
        frameSize = paramAudioFormat.getFrameSize();
        m_lengthInFrames = paramInt;
        bytePosition = 0L;
        clipBytePosition = 0;
        newFramePosition = -1;
        loopStartFrame = 0;
        loopEndFrame = (paramInt - 1);
        loopCount = 0;
        try
        {
          open(paramAudioFormat, (int)Toolkit.millis2bytes(paramAudioFormat, 1000L));
        }
        catch (LineUnavailableException localLineUnavailableException)
        {
          audioData = null;
          throw localLineUnavailableException;
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          audioData = null;
          throw localIllegalArgumentException;
        }
        int i = 6;
        thread = JSSecurityManager.createThread(this, "Direct Clip", true, i, false);
        thread.start();
      }
      if (isAutoClosing()) {
        getEventDispatcher().autoClosingClipOpened(this);
      }
    }
    
    public void open(AudioInputStream paramAudioInputStream)
      throws LineUnavailableException, IOException
    {
      Toolkit.isFullySpecifiedAudioFormat(format);
      synchronized (mixer)
      {
        byte[] arrayOfByte1 = null;
        if (isOpen()) {
          throw new IllegalStateException("Clip is already open with format " + getFormat() + " and frame lengh of " + getFrameLength());
        }
        int i = (int)paramAudioInputStream.getFrameLength();
        int j = 0;
        int k;
        if (i != -1)
        {
          k = i * paramAudioInputStream.getFormat().getFrameSize();
          arrayOfByte1 = new byte[k];
          int m = k;
          int n = 0;
          while ((m > 0) && (n >= 0))
          {
            n = paramAudioInputStream.read(arrayOfByte1, j, m);
            if (n > 0)
            {
              j += n;
              m -= n;
            }
            else if (n == 0)
            {
              Thread.yield();
            }
          }
        }
        else
        {
          k = 16384;
          DirectAudioDevice.DirectBAOS localDirectBAOS = new DirectAudioDevice.DirectBAOS();
          byte[] arrayOfByte2 = new byte[k];
          int i1 = 0;
          while (i1 >= 0)
          {
            i1 = paramAudioInputStream.read(arrayOfByte2, 0, arrayOfByte2.length);
            if (i1 > 0)
            {
              localDirectBAOS.write(arrayOfByte2, 0, i1);
              j += i1;
            }
            else if (i1 == 0)
            {
              Thread.yield();
            }
          }
          arrayOfByte1 = localDirectBAOS.getInternalBuffer();
        }
        i = j / paramAudioInputStream.getFormat().getFrameSize();
        open(paramAudioInputStream.getFormat(), arrayOfByte1, i);
      }
    }
    
    public int getFrameLength()
    {
      return m_lengthInFrames;
    }
    
    public long getMicrosecondLength()
    {
      return Toolkit.frames2micros(getFormat(), getFrameLength());
    }
    
    public void setFramePosition(int paramInt)
    {
      if (paramInt < 0) {
        paramInt = 0;
      } else if (paramInt >= getFrameLength()) {
        paramInt = getFrameLength();
      }
      if (doIO)
      {
        newFramePosition = paramInt;
      }
      else
      {
        clipBytePosition = (paramInt * frameSize);
        newFramePosition = -1;
      }
      bytePosition = (paramInt * frameSize);
      flush();
      synchronized (lockNative)
      {
        DirectAudioDevice.nSetBytePosition(id, isSource, paramInt * frameSize);
      }
    }
    
    public long getLongFramePosition()
    {
      return super.getLongFramePosition();
    }
    
    public synchronized void setMicrosecondPosition(long paramLong)
    {
      long l = Toolkit.micros2frames(getFormat(), paramLong);
      setFramePosition((int)l);
    }
    
    public void setLoopPoints(int paramInt1, int paramInt2)
    {
      if ((paramInt1 < 0) || (paramInt1 >= getFrameLength())) {
        throw new IllegalArgumentException("illegal value for start: " + paramInt1);
      }
      if (paramInt2 >= getFrameLength()) {
        throw new IllegalArgumentException("illegal value for end: " + paramInt2);
      }
      if (paramInt2 == -1)
      {
        paramInt2 = getFrameLength() - 1;
        if (paramInt2 < 0) {
          paramInt2 = 0;
        }
      }
      if (paramInt2 < paramInt1) {
        throw new IllegalArgumentException("End position " + paramInt2 + "  preceeds start position " + paramInt1);
      }
      loopStartFrame = paramInt1;
      loopEndFrame = paramInt2;
    }
    
    public void loop(int paramInt)
    {
      loopCount = paramInt;
      start();
    }
    
    void implOpen(AudioFormat paramAudioFormat, int paramInt)
      throws LineUnavailableException
    {
      if (audioData == null) {
        throw new IllegalArgumentException("illegal call to open() in interface Clip");
      }
      super.implOpen(paramAudioFormat, paramInt);
    }
    
    void implClose()
    {
      Thread localThread = thread;
      thread = null;
      doIO = false;
      if (localThread != null)
      {
        synchronized (lock)
        {
          lock.notifyAll();
        }
        try
        {
          localThread.join(2000L);
        }
        catch (InterruptedException localInterruptedException) {}
      }
      super.implClose();
      audioData = null;
      newFramePosition = -1;
      getEventDispatcher().autoClosingClipClosed(this);
    }
    
    void implStart()
    {
      super.implStart();
    }
    
    void implStop()
    {
      super.implStop();
      loopCount = 0;
    }
    
    public void run()
    {
      if (thread != null)
      {
        synchronized (lock)
        {
          if (!doIO) {
            try
            {
              lock.wait();
            }
            catch (InterruptedException localInterruptedException) {}
          }
        }
        while (doIO)
        {
          if (newFramePosition >= 0)
          {
            clipBytePosition = (newFramePosition * frameSize);
            newFramePosition = -1;
          }
          int i = getFrameLength() - 1;
          if ((loopCount > 0) || (loopCount == -1)) {
            i = loopEndFrame;
          }
          long l = clipBytePosition / frameSize;
          int j = (int)(i - l + 1L);
          int k = j * frameSize;
          if (k > getBufferSize()) {
            k = Toolkit.align(getBufferSize(), frameSize);
          }
          int m = write(audioData, clipBytePosition, k);
          clipBytePosition += m;
          if ((doIO) && (newFramePosition < 0) && (m >= 0))
          {
            l = clipBytePosition / frameSize;
            if (l > i) {
              if ((loopCount > 0) || (loopCount == -1))
              {
                if (loopCount != -1) {
                  loopCount -= 1;
                }
                newFramePosition = loopStartFrame;
              }
              else
              {
                drain();
                stop();
              }
            }
          }
        }
      }
    }
    
    public boolean isAutoClosing()
    {
      return autoclosing;
    }
    
    public void setAutoClosing(boolean paramBoolean)
    {
      if (paramBoolean != autoclosing)
      {
        if (isOpen()) {
          if (paramBoolean) {
            getEventDispatcher().autoClosingClipOpened(this);
          } else {
            getEventDispatcher().autoClosingClipClosed(this);
          }
        }
        autoclosing = paramBoolean;
      }
    }
    
    protected boolean requiresServicing()
    {
      return false;
    }
  }
  
  private static class DirectDL
    extends AbstractDataLine
    implements EventDispatcher.LineMonitor
  {
    protected final int mixerIndex;
    protected final int deviceID;
    protected long id;
    protected int waitTime;
    protected volatile boolean flushing = false;
    protected final boolean isSource;
    protected volatile long bytePosition;
    protected volatile boolean doIO = false;
    protected volatile boolean stoppedWritten = false;
    protected volatile boolean drained = false;
    protected boolean monitoring = false;
    protected int softwareConversionSize = 0;
    protected AudioFormat hardwareFormat;
    private final Gain gainControl = new Gain(null);
    private final Mute muteControl = new Mute(null);
    private final Balance balanceControl = new Balance(null);
    private final Pan panControl = new Pan(null);
    private float leftGain;
    private float rightGain;
    protected volatile boolean noService = false;
    protected final Object lockNative = new Object();
    
    protected DirectDL(DataLine.Info paramInfo, DirectAudioDevice paramDirectAudioDevice, AudioFormat paramAudioFormat, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      super(paramDirectAudioDevice, null, paramAudioFormat, paramInt1);
      mixerIndex = paramInt2;
      deviceID = paramInt3;
      waitTime = 10;
      isSource = paramBoolean;
    }
    
    void implOpen(AudioFormat paramAudioFormat, int paramInt)
      throws LineUnavailableException
    {
      Toolkit.isFullySpecifiedAudioFormat(paramAudioFormat);
      if (!isSource) {
        JSSecurityManager.checkRecordPermission();
      }
      int i = 0;
      if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.ULAW)) {
        i = 1;
      } else if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.ALAW)) {
        i = 2;
      }
      if (paramInt <= -1) {
        paramInt = (int)Toolkit.millis2bytes(paramAudioFormat, 500L);
      }
      DirectAudioDevice.DirectDLI localDirectDLI = null;
      if ((info instanceof DirectAudioDevice.DirectDLI)) {
        localDirectDLI = (DirectAudioDevice.DirectDLI)info;
      }
      if (isSource) {
        if ((!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) && (!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)))
        {
          controls = new Control[0];
        }
        else if ((paramAudioFormat.getChannels() > 2) || (paramAudioFormat.getSampleSizeInBits() > 16))
        {
          controls = new Control[0];
        }
        else
        {
          if (paramAudioFormat.getChannels() == 1)
          {
            controls = new Control[2];
          }
          else
          {
            controls = new Control[4];
            controls[2] = balanceControl;
            controls[3] = panControl;
          }
          controls[0] = gainControl;
          controls[1] = muteControl;
        }
      }
      hardwareFormat = paramAudioFormat;
      softwareConversionSize = 0;
      if ((localDirectDLI != null) && (!localDirectDLI.isFormatSupportedInHardware(paramAudioFormat)))
      {
        AudioFormat localAudioFormat = DirectAudioDevice.getSignOrEndianChangedFormat(paramAudioFormat);
        if (localDirectDLI.isFormatSupportedInHardware(localAudioFormat))
        {
          hardwareFormat = localAudioFormat;
          softwareConversionSize = (paramAudioFormat.getFrameSize() / paramAudioFormat.getChannels());
        }
      }
      paramInt = paramInt / paramAudioFormat.getFrameSize() * paramAudioFormat.getFrameSize();
      id = DirectAudioDevice.nOpen(mixerIndex, deviceID, isSource, i, hardwareFormat.getSampleRate(), hardwareFormat.getSampleSizeInBits(), hardwareFormat.getFrameSize(), hardwareFormat.getChannels(), hardwareFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED), hardwareFormat.isBigEndian(), paramInt);
      if (id == 0L) {
        throw new LineUnavailableException("line with format " + paramAudioFormat + " not supported.");
      }
      bufferSize = DirectAudioDevice.nGetBufferSize(id, isSource);
      if (bufferSize < 1) {
        bufferSize = paramInt;
      }
      format = paramAudioFormat;
      waitTime = ((int)Toolkit.bytes2millis(paramAudioFormat, bufferSize) / 4);
      if (waitTime < 10) {
        waitTime = 1;
      } else if (waitTime > 1000) {
        waitTime = 1000;
      }
      bytePosition = 0L;
      stoppedWritten = false;
      doIO = false;
      calcVolume();
    }
    
    void implStart()
    {
      if (!isSource) {
        JSSecurityManager.checkRecordPermission();
      }
      synchronized (lockNative)
      {
        DirectAudioDevice.nStart(id, isSource);
      }
      monitoring = requiresServicing();
      if (monitoring) {
        getEventDispatcher().addLineMonitor(this);
      }
      doIO = true;
      if ((isSource) && (stoppedWritten))
      {
        setStarted(true);
        setActive(true);
      }
    }
    
    void implStop()
    {
      if (!isSource) {
        JSSecurityManager.checkRecordPermission();
      }
      if (monitoring)
      {
        getEventDispatcher().removeLineMonitor(this);
        monitoring = false;
      }
      synchronized (lockNative)
      {
        DirectAudioDevice.nStop(id, isSource);
      }
      synchronized (lock)
      {
        doIO = false;
        lock.notifyAll();
      }
      setActive(false);
      setStarted(false);
      stoppedWritten = false;
    }
    
    void implClose()
    {
      if (!isSource) {
        JSSecurityManager.checkRecordPermission();
      }
      if (monitoring)
      {
        getEventDispatcher().removeLineMonitor(this);
        monitoring = false;
      }
      doIO = false;
      long l = id;
      id = 0L;
      synchronized (lockNative)
      {
        DirectAudioDevice.nClose(l, isSource);
      }
      bytePosition = 0L;
      softwareConversionSize = 0;
    }
    
    public int available()
    {
      if (id == 0L) {
        return 0;
      }
      int i;
      synchronized (lockNative)
      {
        i = DirectAudioDevice.nAvailable(id, isSource);
      }
      return i;
    }
    
    public void drain()
    {
      noService = true;
      int i = 0;
      long l = getLongFramePosition();
      int j = 0;
      while (!drained)
      {
        synchronized (lockNative)
        {
          if ((id == 0L) || (!doIO) || (!DirectAudioDevice.nIsStillDraining(id, isSource))) {
            break;
          }
        }
        if (i % 5 == 4)
        {
          ??? = getLongFramePosition();
          j |= (??? != l ? 1 : 0);
          if (i % 50 > 45)
          {
            if (j == 0) {
              break;
            }
            j = 0;
            l = ???;
          }
        }
        i++;
        synchronized (lock)
        {
          try
          {
            lock.wait(10L);
          }
          catch (InterruptedException localInterruptedException) {}
        }
      }
      if ((doIO) && (id != 0L)) {
        drained = true;
      }
      noService = false;
    }
    
    public void flush()
    {
      if (id != 0L)
      {
        flushing = true;
        synchronized (lock)
        {
          lock.notifyAll();
        }
        synchronized (lockNative)
        {
          if (id != 0L) {
            DirectAudioDevice.nFlush(id, isSource);
          }
        }
        drained = true;
      }
    }
    
    public long getLongFramePosition()
    {
      long l;
      synchronized (lockNative)
      {
        l = DirectAudioDevice.nGetBytePosition(id, isSource, bytePosition);
      }
      if (l < 0L) {
        l = 0L;
      }
      return l / getFormat().getFrameSize();
    }
    
    public int write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      flushing = false;
      if (paramInt2 == 0) {
        return 0;
      }
      if (paramInt2 < 0) {
        throw new IllegalArgumentException("illegal len: " + paramInt2);
      }
      if (paramInt2 % getFormat().getFrameSize() != 0) {
        throw new IllegalArgumentException("illegal request to write non-integral number of frames (" + paramInt2 + " bytes, frameSize = " + getFormat().getFrameSize() + " bytes)");
      }
      if (paramInt1 < 0) {
        throw new ArrayIndexOutOfBoundsException(paramInt1);
      }
      if (paramInt1 + paramInt2 > paramArrayOfByte.length) {
        throw new ArrayIndexOutOfBoundsException(paramArrayOfByte.length);
      }
      if ((!isActive()) && (doIO))
      {
        setActive(true);
        setStarted(true);
      }
      int i = 0;
      while (!flushing)
      {
        int j;
        synchronized (lockNative)
        {
          j = DirectAudioDevice.nWrite(id, paramArrayOfByte, paramInt1, paramInt2, softwareConversionSize, leftGain, rightGain);
          if (j < 0) {
            break;
          }
          bytePosition += j;
          if (j > 0) {
            drained = false;
          }
        }
        paramInt2 -= j;
        i += j;
        if ((!doIO) || (paramInt2 <= 0)) {
          break;
        }
        paramInt1 += j;
        synchronized (lock)
        {
          try
          {
            lock.wait(waitTime);
          }
          catch (InterruptedException localInterruptedException) {}
        }
      }
      if ((i > 0) && (!doIO)) {
        stoppedWritten = true;
      }
      return i;
    }
    
    protected boolean requiresServicing()
    {
      return DirectAudioDevice.nRequiresServicing(id, isSource);
    }
    
    public void checkLine()
    {
      synchronized (lockNative)
      {
        if ((monitoring) && (doIO) && (id != 0L) && (!flushing) && (!noService)) {
          DirectAudioDevice.nService(id, isSource);
        }
      }
    }
    
    private void calcVolume()
    {
      if (getFormat() == null) {
        return;
      }
      if (muteControl.getValue())
      {
        leftGain = 0.0F;
        rightGain = 0.0F;
        return;
      }
      float f1 = gainControl.getLinearGain();
      if (getFormat().getChannels() == 1)
      {
        leftGain = f1;
        rightGain = f1;
      }
      else
      {
        float f2 = balanceControl.getValue();
        if (f2 < 0.0F)
        {
          leftGain = f1;
          rightGain = (f1 * (f2 + 1.0F));
        }
        else
        {
          leftGain = (f1 * (1.0F - f2));
          rightGain = f1;
        }
      }
    }
    
    private final class Balance
      extends FloatControl
    {
      private Balance()
      {
        super(-1.0F, 1.0F, 0.0078125F, -1, 0.0F, "", "Left", "Center", "Right");
      }
      
      public void setValue(float paramFloat)
      {
        setValueImpl(paramFloat);
        panControl.setValueImpl(paramFloat);
        DirectAudioDevice.DirectDL.this.calcVolume();
      }
      
      void setValueImpl(float paramFloat)
      {
        super.setValue(paramFloat);
      }
    }
    
    protected final class Gain
      extends FloatControl
    {
      private float linearGain = 1.0F;
      
      private Gain()
      {
        super(Toolkit.linearToDB(0.0F), Toolkit.linearToDB(2.0F), Math.abs(Toolkit.linearToDB(1.0F) - Toolkit.linearToDB(0.0F)) / 128.0F, -1, 0.0F, "dB", "Minimum", "", "Maximum");
      }
      
      public void setValue(float paramFloat)
      {
        float f = Toolkit.dBToLinear(paramFloat);
        super.setValue(Toolkit.linearToDB(f));
        linearGain = f;
        DirectAudioDevice.DirectDL.this.calcVolume();
      }
      
      float getLinearGain()
      {
        return linearGain;
      }
    }
    
    private final class Mute
      extends BooleanControl
    {
      private Mute()
      {
        super(false, "True", "False");
      }
      
      public void setValue(boolean paramBoolean)
      {
        super.setValue(paramBoolean);
        DirectAudioDevice.DirectDL.this.calcVolume();
      }
    }
    
    private final class Pan
      extends FloatControl
    {
      private Pan()
      {
        super(-1.0F, 1.0F, 0.0078125F, -1, 0.0F, "", "Left", "Center", "Right");
      }
      
      public void setValue(float paramFloat)
      {
        setValueImpl(paramFloat);
        balanceControl.setValueImpl(paramFloat);
        DirectAudioDevice.DirectDL.this.calcVolume();
      }
      
      void setValueImpl(float paramFloat)
      {
        super.setValue(paramFloat);
      }
    }
  }
  
  private static final class DirectDLI
    extends DataLine.Info
  {
    final AudioFormat[] hardwareFormats;
    
    private DirectDLI(Class paramClass, AudioFormat[] paramArrayOfAudioFormat1, AudioFormat[] paramArrayOfAudioFormat2, int paramInt1, int paramInt2)
    {
      super(paramArrayOfAudioFormat1, paramInt1, paramInt2);
      hardwareFormats = paramArrayOfAudioFormat2;
    }
    
    public boolean isFormatSupportedInHardware(AudioFormat paramAudioFormat)
    {
      if (paramAudioFormat == null) {
        return false;
      }
      for (int i = 0; i < hardwareFormats.length; i++) {
        if (paramAudioFormat.matches(hardwareFormats[i])) {
          return true;
        }
      }
      return false;
    }
    
    private AudioFormat[] getHardwareFormats()
    {
      return hardwareFormats;
    }
  }
  
  private static final class DirectSDL
    extends DirectAudioDevice.DirectDL
    implements SourceDataLine
  {
    private DirectSDL(DataLine.Info paramInfo, AudioFormat paramAudioFormat, int paramInt, DirectAudioDevice paramDirectAudioDevice)
    {
      super(paramDirectAudioDevice, paramAudioFormat, paramInt, paramDirectAudioDevice.getMixerIndex(), paramDirectAudioDevice.getDeviceID(), true);
    }
  }
  
  private static final class DirectTDL
    extends DirectAudioDevice.DirectDL
    implements TargetDataLine
  {
    private DirectTDL(DataLine.Info paramInfo, AudioFormat paramAudioFormat, int paramInt, DirectAudioDevice paramDirectAudioDevice)
    {
      super(paramDirectAudioDevice, paramAudioFormat, paramInt, paramDirectAudioDevice.getMixerIndex(), paramDirectAudioDevice.getDeviceID(), false);
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      flushing = false;
      if (paramInt2 == 0) {
        return 0;
      }
      if (paramInt2 < 0) {
        throw new IllegalArgumentException("illegal len: " + paramInt2);
      }
      if (paramInt2 % getFormat().getFrameSize() != 0) {
        throw new IllegalArgumentException("illegal request to read non-integral number of frames (" + paramInt2 + " bytes, frameSize = " + getFormat().getFrameSize() + " bytes)");
      }
      if (paramInt1 < 0) {
        throw new ArrayIndexOutOfBoundsException(paramInt1);
      }
      if (paramInt1 + paramInt2 > paramArrayOfByte.length) {
        throw new ArrayIndexOutOfBoundsException(paramArrayOfByte.length);
      }
      if ((!isActive()) && (doIO))
      {
        setActive(true);
        setStarted(true);
      }
      int i = 0;
      while ((doIO) && (!flushing))
      {
        int j;
        synchronized (lockNative)
        {
          j = DirectAudioDevice.nRead(id, paramArrayOfByte, paramInt1, paramInt2, softwareConversionSize);
          if (j < 0) {
            break;
          }
          bytePosition += j;
          if (j > 0) {
            drained = false;
          }
        }
        paramInt2 -= j;
        i += j;
        if (paramInt2 <= 0) {
          break;
        }
        paramInt1 += j;
        synchronized (lock)
        {
          try
          {
            lock.wait(waitTime);
          }
          catch (InterruptedException localInterruptedException) {}
        }
      }
      if (flushing) {
        i = 0;
      }
      return i;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\DirectAudioDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */