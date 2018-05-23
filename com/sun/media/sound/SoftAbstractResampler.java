package com.sun.media.sound;

import java.io.IOException;
import java.util.Arrays;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.VoiceStatus;
import javax.sound.sampled.AudioFormat;

public abstract class SoftAbstractResampler
  implements SoftResampler
{
  public SoftAbstractResampler() {}
  
  public abstract int getPadding();
  
  public abstract void interpolate(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float paramFloat1, float[] paramArrayOfFloat3, float paramFloat2, float[] paramArrayOfFloat4, int[] paramArrayOfInt, int paramInt);
  
  public final SoftResamplerStreamer openStreamer()
  {
    return new ModelAbstractResamplerStream();
  }
  
  private class ModelAbstractResamplerStream
    implements SoftResamplerStreamer
  {
    AudioFloatInputStream stream;
    boolean stream_eof = false;
    int loopmode;
    boolean loopdirection = true;
    float loopstart;
    float looplen;
    float target_pitch;
    float[] current_pitch = new float[1];
    boolean started;
    boolean eof;
    int sector_pos = 0;
    int sector_size = 400;
    int sector_loopstart = -1;
    boolean markset = false;
    int marklimit = 0;
    int streampos = 0;
    int nrofchannels = 2;
    boolean noteOff_flag = false;
    float[][] ibuffer = new float[2][sector_size + pad2];
    boolean ibuffer_order = true;
    float[] sbuffer;
    int pad = getPadding();
    int pad2 = getPadding() * 2;
    float[] ix = new float[1];
    int[] ox = new int[1];
    float samplerateconv = 1.0F;
    float pitchcorrection = 0.0F;
    
    ModelAbstractResamplerStream() {}
    
    public void noteOn(MidiChannel paramMidiChannel, VoiceStatus paramVoiceStatus, int paramInt1, int paramInt2) {}
    
    public void noteOff(int paramInt)
    {
      noteOff_flag = true;
    }
    
    public void open(ModelWavetable paramModelWavetable, float paramFloat)
      throws IOException
    {
      eof = false;
      nrofchannels = paramModelWavetable.getChannels();
      if (ibuffer.length < nrofchannels) {
        ibuffer = new float[nrofchannels][sector_size + pad2];
      }
      stream = paramModelWavetable.openStream();
      streampos = 0;
      stream_eof = false;
      pitchcorrection = paramModelWavetable.getPitchcorrection();
      samplerateconv = (stream.getFormat().getSampleRate() / paramFloat);
      looplen = paramModelWavetable.getLoopLength();
      loopstart = paramModelWavetable.getLoopStart();
      sector_loopstart = ((int)(loopstart / sector_size));
      sector_loopstart -= 1;
      sector_pos = 0;
      if (sector_loopstart < 0) {
        sector_loopstart = 0;
      }
      started = false;
      loopmode = paramModelWavetable.getLoopType();
      if (loopmode != 0)
      {
        markset = false;
        marklimit = (nrofchannels * (int)(looplen + pad2 + 1.0F));
      }
      else
      {
        markset = true;
      }
      target_pitch = samplerateconv;
      current_pitch[0] = samplerateconv;
      ibuffer_order = true;
      loopdirection = true;
      noteOff_flag = false;
      for (int i = 0; i < nrofchannels; i++) {
        Arrays.fill(ibuffer[i], sector_size, sector_size + pad2, 0.0F);
      }
      ix[0] = pad;
      eof = false;
      ix[0] = (sector_size + pad);
      sector_pos = -1;
      streampos = (-sector_size);
      nextBuffer();
    }
    
    public void setPitch(float paramFloat)
    {
      target_pitch = ((float)Math.exp((pitchcorrection + paramFloat) * (Math.log(2.0D) / 1200.0D)) * samplerateconv);
      if (!started) {
        current_pitch[0] = target_pitch;
      }
    }
    
    public void nextBuffer()
      throws IOException
    {
      if ((ix[0] < pad) && (markset))
      {
        stream.reset();
        ix[0] += streampos - sector_loopstart * sector_size;
        sector_pos = sector_loopstart;
        streampos = (sector_pos * sector_size);
        ix[0] += sector_size;
        sector_pos -= 1;
        streampos -= sector_size;
        stream_eof = false;
      }
      if ((ix[0] >= sector_size + pad) && (stream_eof))
      {
        eof = true;
        return;
      }
      int i;
      if (ix[0] >= sector_size * 4 + pad)
      {
        i = (int)((ix[0] - sector_size * 4 + pad) / sector_size);
        ix[0] -= sector_size * i;
        sector_pos += i;
        streampos += sector_size * i;
        stream.skip(sector_size * i);
      }
      while (ix[0] >= sector_size + pad)
      {
        if ((!markset) && (sector_pos + 1 == sector_loopstart))
        {
          stream.mark(marklimit);
          markset = true;
        }
        ix[0] -= sector_size;
        sector_pos += 1;
        streampos += sector_size;
        int k;
        for (i = 0; i < nrofchannels; i++)
        {
          float[] arrayOfFloat1 = ibuffer[i];
          for (k = 0; k < pad2; k++) {
            arrayOfFloat1[k] = arrayOfFloat1[(k + sector_size)];
          }
        }
        int j;
        if (nrofchannels == 1)
        {
          i = stream.read(ibuffer[0], pad2, sector_size);
        }
        else
        {
          j = sector_size * nrofchannels;
          if ((sbuffer == null) || (sbuffer.length < j)) {
            sbuffer = new float[j];
          }
          k = stream.read(sbuffer, 0, j);
          if (k == -1)
          {
            i = -1;
          }
          else
          {
            i = k / nrofchannels;
            for (int m = 0; m < nrofchannels; m++)
            {
              float[] arrayOfFloat2 = ibuffer[m];
              int n = m;
              int i1 = nrofchannels;
              int i2 = pad2;
              int i3 = 0;
              while (i3 < i)
              {
                arrayOfFloat2[i2] = sbuffer[n];
                i3++;
                n += i1;
                i2++;
              }
            }
          }
        }
        if (i == -1)
        {
          i = 0;
          stream_eof = true;
          for (j = 0; j < nrofchannels; j++) {
            Arrays.fill(ibuffer[j], pad2, pad2 + sector_size, 0.0F);
          }
          return;
        }
        if (i != sector_size) {
          for (j = 0; j < nrofchannels; j++) {
            Arrays.fill(ibuffer[j], pad2 + i, pad2 + sector_size, 0.0F);
          }
        }
        ibuffer_order = true;
      }
    }
    
    public void reverseBuffers()
    {
      ibuffer_order = (!ibuffer_order);
      for (int i = 0; i < nrofchannels; i++)
      {
        float[] arrayOfFloat = ibuffer[i];
        int j = arrayOfFloat.length - 1;
        int k = arrayOfFloat.length / 2;
        for (int m = 0; m < k; m++)
        {
          float f = arrayOfFloat[m];
          arrayOfFloat[m] = arrayOfFloat[(j - m)];
          arrayOfFloat[(j - m)] = f;
        }
      }
    }
    
    public int read(float[][] paramArrayOfFloat, int paramInt1, int paramInt2)
      throws IOException
    {
      if (eof) {
        return -1;
      }
      if ((noteOff_flag) && ((loopmode & 0x2) != 0) && (loopdirection)) {
        loopmode = 0;
      }
      float f1 = (target_pitch - current_pitch[0]) / paramInt2;
      float[] arrayOfFloat = current_pitch;
      started = true;
      int[] arrayOfInt = ox;
      arrayOfInt[0] = paramInt1;
      int i = paramInt2 + paramInt1;
      float f2 = sector_size + pad;
      if (!loopdirection) {
        f2 = pad;
      }
      while (arrayOfInt[0] != i)
      {
        nextBuffer();
        float f3;
        int j;
        float f4;
        int k;
        if (!loopdirection)
        {
          if (streampos < loopstart + pad)
          {
            f2 = loopstart - streampos + pad2;
            if (ix[0] <= f2)
            {
              if ((loopmode & 0x4) != 0)
              {
                loopdirection = true;
                f2 = sector_size + pad;
                continue;
              }
              ix[0] += looplen;
              f2 = pad;
              continue;
            }
          }
          if (ibuffer_order != loopdirection) {
            reverseBuffers();
          }
          ix[0] = (sector_size + pad2 - ix[0]);
          f2 = sector_size + pad2 - f2;
          f2 += 1.0F;
          f3 = ix[0];
          j = arrayOfInt[0];
          f4 = arrayOfFloat[0];
          for (k = 0; k < nrofchannels; k++) {
            if (paramArrayOfFloat[k] != null)
            {
              ix[0] = f3;
              arrayOfInt[0] = j;
              arrayOfFloat[0] = f4;
              interpolate(ibuffer[k], ix, f2, arrayOfFloat, f1, paramArrayOfFloat[k], arrayOfInt, i);
            }
          }
          ix[0] = (sector_size + pad2 - ix[0]);
          f2 -= 1.0F;
          f2 = sector_size + pad2 - f2;
          if (eof)
          {
            arrayOfFloat[0] = target_pitch;
            return arrayOfInt[0] - paramInt1;
          }
        }
        else
        {
          if ((loopmode != 0) && (streampos + sector_size > looplen + loopstart + pad))
          {
            f2 = loopstart + looplen - streampos + pad2;
            if (ix[0] >= f2)
            {
              if (((loopmode & 0x4) != 0) || ((loopmode & 0x8) != 0))
              {
                loopdirection = false;
                f2 = pad;
                continue;
              }
              f2 = sector_size + pad;
              ix[0] -= looplen;
              continue;
            }
          }
          if (ibuffer_order != loopdirection) {
            reverseBuffers();
          }
          f3 = ix[0];
          j = arrayOfInt[0];
          f4 = arrayOfFloat[0];
          for (k = 0; k < nrofchannels; k++) {
            if (paramArrayOfFloat[k] != null)
            {
              ix[0] = f3;
              arrayOfInt[0] = j;
              arrayOfFloat[0] = f4;
              interpolate(ibuffer[k], ix, f2, arrayOfFloat, f1, paramArrayOfFloat[k], arrayOfInt, i);
            }
          }
          if (eof)
          {
            arrayOfFloat[0] = target_pitch;
            return arrayOfInt[0] - paramInt1;
          }
        }
      }
      arrayOfFloat[0] = target_pitch;
      return paramInt2;
    }
    
    public void close()
      throws IOException
    {
      stream.close();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftAbstractResampler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */