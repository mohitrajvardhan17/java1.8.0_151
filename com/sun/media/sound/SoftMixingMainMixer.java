package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class SoftMixingMainMixer
{
  public static final int CHANNEL_LEFT = 0;
  public static final int CHANNEL_RIGHT = 1;
  public static final int CHANNEL_EFFECT1 = 2;
  public static final int CHANNEL_EFFECT2 = 3;
  public static final int CHANNEL_EFFECT3 = 4;
  public static final int CHANNEL_EFFECT4 = 5;
  public static final int CHANNEL_LEFT_DRY = 10;
  public static final int CHANNEL_RIGHT_DRY = 11;
  public static final int CHANNEL_SCRATCH1 = 12;
  public static final int CHANNEL_SCRATCH2 = 13;
  public static final int CHANNEL_CHANNELMIXER_LEFT = 14;
  public static final int CHANNEL_CHANNELMIXER_RIGHT = 15;
  private final SoftMixingMixer mixer;
  private final AudioInputStream ais;
  private final SoftAudioBuffer[] buffers;
  private final SoftAudioProcessor reverb;
  private final SoftAudioProcessor chorus;
  private final SoftAudioProcessor agc;
  private final int nrofchannels;
  private final Object control_mutex;
  private final List<SoftMixingDataLine> openLinesList = new ArrayList();
  private SoftMixingDataLine[] openLines = new SoftMixingDataLine[0];
  
  public AudioInputStream getInputStream()
  {
    return ais;
  }
  
  void processAudioBuffers()
  {
    for (int i = 0; i < buffers.length; i++) {
      buffers[i].clear();
    }
    SoftMixingDataLine[] arrayOfSoftMixingDataLine;
    synchronized (control_mutex)
    {
      arrayOfSoftMixingDataLine = openLines;
      for (int k = 0; k < arrayOfSoftMixingDataLine.length; k++) {
        arrayOfSoftMixingDataLine[k].processControlLogic();
      }
      chorus.processControlLogic();
      reverb.processControlLogic();
      agc.processControlLogic();
    }
    for (int j = 0; j < arrayOfSoftMixingDataLine.length; j++) {
      arrayOfSoftMixingDataLine[j].processAudioLogic(buffers);
    }
    chorus.processAudio();
    reverb.processAudio();
    agc.processAudio();
  }
  
  public SoftMixingMainMixer(SoftMixingMixer paramSoftMixingMixer)
  {
    mixer = paramSoftMixingMixer;
    nrofchannels = paramSoftMixingMixer.getFormat().getChannels();
    int i = (int)(paramSoftMixingMixer.getFormat().getSampleRate() / paramSoftMixingMixer.getControlRate());
    control_mutex = control_mutex;
    buffers = new SoftAudioBuffer[16];
    for (int j = 0; j < buffers.length; j++) {
      buffers[j] = new SoftAudioBuffer(i, paramSoftMixingMixer.getFormat());
    }
    reverb = new SoftReverb();
    chorus = new SoftChorus();
    agc = new SoftLimiter();
    float f1 = paramSoftMixingMixer.getFormat().getSampleRate();
    float f2 = paramSoftMixingMixer.getControlRate();
    reverb.init(f1, f2);
    chorus.init(f1, f2);
    agc.init(f1, f2);
    reverb.setMixMode(true);
    chorus.setMixMode(true);
    agc.setMixMode(false);
    chorus.setInput(0, buffers[3]);
    chorus.setOutput(0, buffers[0]);
    if (nrofchannels != 1) {
      chorus.setOutput(1, buffers[1]);
    }
    chorus.setOutput(2, buffers[2]);
    reverb.setInput(0, buffers[2]);
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
    InputStream local1 = new InputStream()
    {
      private final SoftAudioBuffer[] buffers = buffers;
      private final int nrofchannels = mixer.getFormat().getChannels();
      private final int buffersize = buffers[0].getSize();
      private final byte[] bbuffer = new byte[buffersize * (mixer.getFormat().getSampleSizeInBits() / 8) * nrofchannels];
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
        byte[] arrayOfByte = bbuffer;
        while (paramAnonymousInt1 < j) {
          if (available() == 0)
          {
            fillBuffer();
          }
          else
          {
            int k = bbuffer_pos;
            while ((paramAnonymousInt1 < j) && (k < i)) {
              paramAnonymousArrayOfByte[(paramAnonymousInt1++)] = arrayOfByte[(k++)];
            }
            bbuffer_pos = k;
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
        mixer.close();
      }
    };
    ais = new AudioInputStream(local1, paramSoftMixingMixer.getFormat(), -1L);
  }
  
  public void openLine(SoftMixingDataLine paramSoftMixingDataLine)
  {
    synchronized (control_mutex)
    {
      openLinesList.add(paramSoftMixingDataLine);
      openLines = ((SoftMixingDataLine[])openLinesList.toArray(new SoftMixingDataLine[openLinesList.size()]));
    }
  }
  
  public void closeLine(SoftMixingDataLine paramSoftMixingDataLine)
  {
    synchronized (control_mutex)
    {
      openLinesList.remove(paramSoftMixingDataLine);
      openLines = ((SoftMixingDataLine[])openLinesList.toArray(new SoftMixingDataLine[openLinesList.size()]));
      if ((openLines.length == 0) && (mixer.implicitOpen)) {
        mixer.close();
      }
    }
  }
  
  /* Error */
  public SoftMixingDataLine[] getOpenLines()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 167	com/sun/media/sound/SoftMixingMainMixer:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 165	com/sun/media/sound/SoftMixingMainMixer:openLines	[Lcom/sun/media/sound/SoftMixingDataLine;
    //   11: aload_1
    //   12: monitorexit
    //   13: areturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	SoftMixingMainMixer
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public void close()
  {
    SoftMixingDataLine[] arrayOfSoftMixingDataLine = openLines;
    for (int i = 0; i < arrayOfSoftMixingDataLine.length; i++) {
      arrayOfSoftMixingDataLine[i].close();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftMixingMainMixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */