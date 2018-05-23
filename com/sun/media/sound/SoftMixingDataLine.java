package com.sun.media.sound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.BooleanControl.Type;
import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public abstract class SoftMixingDataLine
  implements DataLine
{
  public static final FloatControl.Type CHORUS_SEND = new FloatControl.Type("Chorus Send") {};
  private final Gain gain_control = new Gain(null);
  private final Mute mute_control = new Mute(null);
  private final Balance balance_control = new Balance(null);
  private final Pan pan_control = new Pan(null);
  private final ReverbSend reverbsend_control = new ReverbSend(null);
  private final ChorusSend chorussend_control = new ChorusSend(null);
  private final ApplyReverb apply_reverb = new ApplyReverb(null);
  private final Control[] controls;
  float leftgain = 1.0F;
  float rightgain = 1.0F;
  float eff1gain = 0.0F;
  float eff2gain = 0.0F;
  List<LineListener> listeners = new ArrayList();
  final Object control_mutex;
  SoftMixingMixer mixer;
  DataLine.Info info;
  
  protected abstract void processControlLogic();
  
  protected abstract void processAudioLogic(SoftAudioBuffer[] paramArrayOfSoftAudioBuffer);
  
  SoftMixingDataLine(SoftMixingMixer paramSoftMixingMixer, DataLine.Info paramInfo)
  {
    mixer = paramSoftMixingMixer;
    info = paramInfo;
    control_mutex = control_mutex;
    controls = new Control[] { gain_control, mute_control, balance_control, pan_control, reverbsend_control, chorussend_control, apply_reverb };
    calcVolume();
  }
  
  final void calcVolume()
  {
    synchronized (control_mutex)
    {
      double d1 = Math.pow(10.0D, gain_control.getValue() / 20.0D);
      if (mute_control.getValue()) {
        d1 = 0.0D;
      }
      leftgain = ((float)d1);
      rightgain = ((float)d1);
      if (mixer.getFormat().getChannels() > 1)
      {
        double d2 = balance_control.getValue();
        if (d2 > 0.0D) {
          leftgain = ((float)(leftgain * (1.0D - d2)));
        } else {
          rightgain = ((float)(rightgain * (1.0D + d2)));
        }
      }
    }
    eff1gain = ((float)Math.pow(10.0D, reverbsend_control.getValue() / 20.0D));
    eff2gain = ((float)Math.pow(10.0D, chorussend_control.getValue() / 20.0D));
    if (!apply_reverb.getValue()) {
      eff1gain = 0.0F;
    }
  }
  
  final void sendEvent(LineEvent paramLineEvent)
  {
    if (listeners.size() == 0) {
      return;
    }
    LineListener[] arrayOfLineListener1 = (LineListener[])listeners.toArray(new LineListener[listeners.size()]);
    for (LineListener localLineListener : arrayOfLineListener1) {
      localLineListener.update(paramLineEvent);
    }
  }
  
  public final void addLineListener(LineListener paramLineListener)
  {
    synchronized (control_mutex)
    {
      listeners.add(paramLineListener);
    }
  }
  
  public final void removeLineListener(LineListener paramLineListener)
  {
    synchronized (control_mutex)
    {
      listeners.add(paramLineListener);
    }
  }
  
  public final Line.Info getLineInfo()
  {
    return info;
  }
  
  public final Control getControl(Control.Type paramType)
  {
    if (paramType != null) {
      for (int i = 0; i < controls.length; i++) {
        if (controls[i].getType() == paramType) {
          return controls[i];
        }
      }
    }
    throw new IllegalArgumentException("Unsupported control type : " + paramType);
  }
  
  public final Control[] getControls()
  {
    return (Control[])Arrays.copyOf(controls, controls.length);
  }
  
  public final boolean isControlSupported(Control.Type paramType)
  {
    if (paramType != null) {
      for (int i = 0; i < controls.length; i++) {
        if (controls[i].getType() == paramType) {
          return true;
        }
      }
    }
    return false;
  }
  
  private final class ApplyReverb
    extends BooleanControl
  {
    private ApplyReverb()
    {
      super(false, "True", "False");
    }
    
    public void setValue(boolean paramBoolean)
    {
      super.setValue(paramBoolean);
      calcVolume();
    }
  }
  
  protected static final class AudioFloatInputStreamResampler
    extends AudioFloatInputStream
  {
    private final AudioFloatInputStream ais;
    private final AudioFormat targetFormat;
    private float[] skipbuffer;
    private SoftAbstractResampler resampler;
    private final float[] pitch = new float[1];
    private final float[] ibuffer2;
    private final float[][] ibuffer;
    private float ibuffer_index = 0.0F;
    private int ibuffer_len = 0;
    private int nrofchannels = 0;
    private float[][] cbuffer;
    private final int buffer_len = 512;
    private final int pad;
    private final int pad2;
    private final float[] ix = new float[1];
    private final int[] ox = new int[1];
    private float[][] mark_ibuffer = (float[][])null;
    private float mark_ibuffer_index = 0.0F;
    private int mark_ibuffer_len = 0;
    
    public AudioFloatInputStreamResampler(AudioFloatInputStream paramAudioFloatInputStream, AudioFormat paramAudioFormat)
    {
      ais = paramAudioFloatInputStream;
      AudioFormat localAudioFormat = paramAudioFloatInputStream.getFormat();
      targetFormat = new AudioFormat(localAudioFormat.getEncoding(), paramAudioFormat.getSampleRate(), localAudioFormat.getSampleSizeInBits(), localAudioFormat.getChannels(), localAudioFormat.getFrameSize(), paramAudioFormat.getSampleRate(), localAudioFormat.isBigEndian());
      nrofchannels = targetFormat.getChannels();
      Object localObject = paramAudioFormat.getProperty("interpolation");
      if ((localObject != null) && ((localObject instanceof String)))
      {
        String str = (String)localObject;
        if (str.equalsIgnoreCase("point")) {
          resampler = new SoftPointResampler();
        }
        if (str.equalsIgnoreCase("linear")) {
          resampler = new SoftLinearResampler2();
        }
        if (str.equalsIgnoreCase("linear1")) {
          resampler = new SoftLinearResampler();
        }
        if (str.equalsIgnoreCase("linear2")) {
          resampler = new SoftLinearResampler2();
        }
        if (str.equalsIgnoreCase("cubic")) {
          resampler = new SoftCubicResampler();
        }
        if (str.equalsIgnoreCase("lanczos")) {
          resampler = new SoftLanczosResampler();
        }
        if (str.equalsIgnoreCase("sinc")) {
          resampler = new SoftSincResampler();
        }
      }
      if (resampler == null) {
        resampler = new SoftLinearResampler2();
      }
      pitch[0] = (localAudioFormat.getSampleRate() / paramAudioFormat.getSampleRate());
      pad = resampler.getPadding();
      pad2 = (pad * 2);
      ibuffer = new float[nrofchannels][512 + pad2];
      ibuffer2 = new float[nrofchannels * 512];
      ibuffer_index = (512 + pad);
      ibuffer_len = 512;
    }
    
    public int available()
      throws IOException
    {
      return 0;
    }
    
    public void close()
      throws IOException
    {
      ais.close();
    }
    
    public AudioFormat getFormat()
    {
      return targetFormat;
    }
    
    public long getFrameLength()
    {
      return -1L;
    }
    
    public void mark(int paramInt)
    {
      ais.mark((int)(paramInt * pitch[0]));
      mark_ibuffer_index = ibuffer_index;
      mark_ibuffer_len = ibuffer_len;
      if (mark_ibuffer == null) {
        mark_ibuffer = new float[ibuffer.length][ibuffer[0].length];
      }
      for (int i = 0; i < ibuffer.length; i++)
      {
        float[] arrayOfFloat1 = ibuffer[i];
        float[] arrayOfFloat2 = mark_ibuffer[i];
        for (int j = 0; j < arrayOfFloat2.length; j++) {
          arrayOfFloat2[j] = arrayOfFloat1[j];
        }
      }
    }
    
    public boolean markSupported()
    {
      return ais.markSupported();
    }
    
    private void readNextBuffer()
      throws IOException
    {
      if (ibuffer_len == -1) {
        return;
      }
      int m;
      int n;
      for (int i = 0; i < nrofchannels; i++)
      {
        float[] arrayOfFloat1 = ibuffer[i];
        int k = ibuffer_len + pad2;
        m = ibuffer_len;
        for (n = 0; m < k; n++)
        {
          arrayOfFloat1[n] = arrayOfFloat1[m];
          m++;
        }
      }
      ibuffer_index -= ibuffer_len;
      ibuffer_len = ais.read(ibuffer2);
      if (ibuffer_len >= 0)
      {
        while (ibuffer_len < ibuffer2.length)
        {
          i = ais.read(ibuffer2, ibuffer_len, ibuffer2.length - ibuffer_len);
          if (i == -1) {
            break;
          }
          ibuffer_len += i;
        }
        Arrays.fill(ibuffer2, ibuffer_len, ibuffer2.length, 0.0F);
        ibuffer_len /= nrofchannels;
      }
      else
      {
        Arrays.fill(ibuffer2, 0, ibuffer2.length, 0.0F);
      }
      i = ibuffer2.length;
      for (int j = 0; j < nrofchannels; j++)
      {
        float[] arrayOfFloat2 = ibuffer[j];
        m = j;
        for (n = pad2; m < i; n++)
        {
          arrayOfFloat2[n] = ibuffer2[m];
          m += nrofchannels;
        }
      }
    }
    
    public int read(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
      throws IOException
    {
      if ((cbuffer == null) || (cbuffer[0].length < paramInt2 / nrofchannels)) {
        cbuffer = new float[nrofchannels][paramInt2 / nrofchannels];
      }
      if (ibuffer_len == -1) {
        return -1;
      }
      if (paramInt2 < 0) {
        return 0;
      }
      int i = paramInt2 / nrofchannels;
      int j = 0;
      int k = ibuffer_len;
      int n;
      float[] arrayOfFloat;
      while (i > 0)
      {
        if (ibuffer_len >= 0)
        {
          if (ibuffer_index >= ibuffer_len + pad) {
            readNextBuffer();
          }
          k = ibuffer_len + pad;
        }
        if (ibuffer_len < 0)
        {
          k = pad2;
          if (ibuffer_index >= k) {
            break;
          }
        }
        if (ibuffer_index < 0.0F) {
          break;
        }
        m = j;
        for (n = 0; n < nrofchannels; n++)
        {
          ix[0] = ibuffer_index;
          ox[0] = j;
          arrayOfFloat = ibuffer[n];
          resampler.interpolate(arrayOfFloat, ix, k, pitch, 0.0F, cbuffer[n], ox, paramInt2 / nrofchannels);
        }
        ibuffer_index = ix[0];
        j = ox[0];
        i -= j - m;
      }
      for (int m = 0; m < nrofchannels; m++)
      {
        n = 0;
        arrayOfFloat = cbuffer[m];
        int i1 = m;
        while (i1 < paramArrayOfFloat.length)
        {
          paramArrayOfFloat[i1] = arrayOfFloat[(n++)];
          i1 += nrofchannels;
        }
      }
      return paramInt2 - i * nrofchannels;
    }
    
    public void reset()
      throws IOException
    {
      ais.reset();
      if (mark_ibuffer == null) {
        return;
      }
      ibuffer_index = mark_ibuffer_index;
      ibuffer_len = mark_ibuffer_len;
      for (int i = 0; i < ibuffer.length; i++)
      {
        float[] arrayOfFloat1 = mark_ibuffer[i];
        float[] arrayOfFloat2 = ibuffer[i];
        for (int j = 0; j < arrayOfFloat2.length; j++) {
          arrayOfFloat2[j] = arrayOfFloat1[j];
        }
      }
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      if (paramLong > 0L) {
        return 0L;
      }
      if (skipbuffer == null) {
        skipbuffer = new float[1024 * targetFormat.getFrameSize()];
      }
      float[] arrayOfFloat = skipbuffer;
      int i;
      for (long l = paramLong; l > 0L; l -= i)
      {
        i = read(arrayOfFloat, 0, (int)Math.min(l, skipbuffer.length));
        if (i < 0)
        {
          if (l != paramLong) {
            break;
          }
          return i;
        }
      }
      return paramLong - l;
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
      super.setValue(paramFloat);
      calcVolume();
    }
  }
  
  private final class ChorusSend
    extends FloatControl
  {
    private ChorusSend()
    {
      super(-80.0F, 6.0206F, 0.625F, -1, -80.0F, "dB", "Minimum", "", "Maximum");
    }
    
    public void setValue(float paramFloat)
    {
      super.setValue(paramFloat);
      balance_control.setValue(paramFloat);
    }
  }
  
  private final class Gain
    extends FloatControl
  {
    private Gain()
    {
      super(-80.0F, 6.0206F, 0.625F, -1, 0.0F, "dB", "Minimum", "", "Maximum");
    }
    
    public void setValue(float paramFloat)
    {
      super.setValue(paramFloat);
      calcVolume();
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
      calcVolume();
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
      super.setValue(paramFloat);
      balance_control.setValue(paramFloat);
    }
    
    public float getValue()
    {
      return balance_control.getValue();
    }
  }
  
  private final class ReverbSend
    extends FloatControl
  {
    private ReverbSend()
    {
      super(-80.0F, 6.0206F, 0.625F, -1, -80.0F, "dB", "Minimum", "", "Maximum");
    }
    
    public void setValue(float paramFloat)
    {
      super.setValue(paramFloat);
      balance_control.setValue(paramFloat);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftMixingDataLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */