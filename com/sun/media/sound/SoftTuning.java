package com.sun.media.sound;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import javax.sound.midi.Patch;

public final class SoftTuning
{
  private String name = null;
  private final double[] tuning = new double[''];
  private Patch patch = null;
  
  public SoftTuning()
  {
    name = "12-TET";
    for (int i = 0; i < tuning.length; i++) {
      tuning[i] = (i * 100);
    }
  }
  
  public SoftTuning(byte[] paramArrayOfByte)
  {
    for (int i = 0; i < tuning.length; i++) {
      tuning[i] = (i * 100);
    }
    load(paramArrayOfByte);
  }
  
  public SoftTuning(Patch paramPatch)
  {
    patch = paramPatch;
    name = "12-TET";
    for (int i = 0; i < tuning.length; i++) {
      tuning[i] = (i * 100);
    }
  }
  
  public SoftTuning(Patch paramPatch, byte[] paramArrayOfByte)
  {
    patch = paramPatch;
    for (int i = 0; i < tuning.length; i++) {
      tuning[i] = (i * 100);
    }
    load(paramArrayOfByte);
  }
  
  private boolean checksumOK(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte[1] & 0xFF;
    for (int j = 2; j < paramArrayOfByte.length - 2; j++) {
      i ^= paramArrayOfByte[j] & 0xFF;
    }
    return (paramArrayOfByte[(paramArrayOfByte.length - 2)] & 0xFF) == (i & 0x7F);
  }
  
  public void load(byte[] paramArrayOfByte)
  {
    if (((paramArrayOfByte[1] & 0xFF) == 126) || ((paramArrayOfByte[1] & 0xFF) == Byte.MAX_VALUE))
    {
      int i = paramArrayOfByte[3] & 0xFF;
      switch (i)
      {
      case 8: 
        int j = paramArrayOfByte[4] & 0xFF;
        int k;
        int i1;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        Object localObject;
        switch (j)
        {
        case 1: 
          try
          {
            name = new String(paramArrayOfByte, 6, 16, "ascii");
          }
          catch (UnsupportedEncodingException localUnsupportedEncodingException1)
          {
            name = null;
          }
          k = 22;
          for (i1 = 0; i1 < 128; i1++)
          {
            i2 = paramArrayOfByte[(k++)] & 0xFF;
            i3 = paramArrayOfByte[(k++)] & 0xFF;
            i4 = paramArrayOfByte[(k++)] & 0xFF;
            if ((i2 != 127) || (i3 != 127) || (i4 != 127)) {
              tuning[i1] = (100.0D * ((i2 * 16384 + i3 * 128 + i4) / 16384.0D));
            }
          }
          break;
        case 2: 
          k = paramArrayOfByte[6] & 0xFF;
          i1 = 7;
          for (i2 = 0; i2 < k; i2++)
          {
            i3 = paramArrayOfByte[(i1++)] & 0xFF;
            i4 = paramArrayOfByte[(i1++)] & 0xFF;
            i5 = paramArrayOfByte[(i1++)] & 0xFF;
            i6 = paramArrayOfByte[(i1++)] & 0xFF;
            if ((i4 != 127) || (i5 != 127) || (i6 != 127)) {
              tuning[i3] = (100.0D * ((i4 * 16384 + i5 * 128 + i6) / 16384.0D));
            }
          }
          break;
        case 4: 
          if (checksumOK(paramArrayOfByte))
          {
            try
            {
              name = new String(paramArrayOfByte, 7, 16, "ascii");
            }
            catch (UnsupportedEncodingException localUnsupportedEncodingException2)
            {
              name = null;
            }
            int m = 23;
            for (i1 = 0; i1 < 128; i1++)
            {
              i2 = paramArrayOfByte[(m++)] & 0xFF;
              i3 = paramArrayOfByte[(m++)] & 0xFF;
              i4 = paramArrayOfByte[(m++)] & 0xFF;
              if ((i2 != 127) || (i3 != 127) || (i4 != 127)) {
                tuning[i1] = (100.0D * ((i2 * 16384 + i3 * 128 + i4) / 16384.0D));
              }
            }
          }
          break;
        case 5: 
          if (checksumOK(paramArrayOfByte))
          {
            try
            {
              name = new String(paramArrayOfByte, 7, 16, "ascii");
            }
            catch (UnsupportedEncodingException localUnsupportedEncodingException3)
            {
              name = null;
            }
            int[] arrayOfInt = new int[12];
            for (i1 = 0; i1 < 12; i1++) {
              arrayOfInt[i1] = ((paramArrayOfByte[(i1 + 23)] & 0xFF) - 64);
            }
            for (i1 = 0; i1 < tuning.length; i1++) {
              tuning[i1] = (i1 * 100 + arrayOfInt[(i1 % 12)]);
            }
          }
          break;
        case 6: 
          if (checksumOK(paramArrayOfByte))
          {
            try
            {
              name = new String(paramArrayOfByte, 7, 16, "ascii");
            }
            catch (UnsupportedEncodingException localUnsupportedEncodingException4)
            {
              name = null;
            }
            double[] arrayOfDouble = new double[12];
            for (i1 = 0; i1 < 12; i1++)
            {
              i2 = (paramArrayOfByte[(i1 * 2 + 23)] & 0xFF) * 128 + (paramArrayOfByte[(i1 * 2 + 24)] & 0xFF);
              arrayOfDouble[i1] = ((i2 / 8192.0D - 1.0D) * 100.0D);
            }
            for (i1 = 0; i1 < tuning.length; i1++) {
              tuning[i1] = (i1 * 100 + arrayOfDouble[(i1 % 12)]);
            }
          }
          break;
        case 7: 
          int n = paramArrayOfByte[7] & 0xFF;
          i1 = 8;
          for (i2 = 0; i2 < n; i2++)
          {
            i3 = paramArrayOfByte[(i1++)] & 0xFF;
            i4 = paramArrayOfByte[(i1++)] & 0xFF;
            i5 = paramArrayOfByte[(i1++)] & 0xFF;
            i6 = paramArrayOfByte[(i1++)] & 0xFF;
            if ((i4 != 127) || (i5 != 127) || (i6 != 127)) {
              tuning[i3] = (100.0D * ((i4 * 16384 + i5 * 128 + i6) / 16384.0D));
            }
          }
          break;
        case 8: 
          localObject = new int[12];
          for (i3 = 0; i3 < 12; i3++) {
            localObject[i3] = ((paramArrayOfByte[(i3 + 8)] & 0xFF) - 64);
          }
          for (i3 = 0; i3 < tuning.length; i3++) {
            tuning[i3] = (i3 * 100 + localObject[(i3 % 12)]);
          }
          break;
        case 9: 
          localObject = new double[12];
          for (i3 = 0; i3 < 12; i3++)
          {
            i4 = (paramArrayOfByte[(i3 * 2 + 8)] & 0xFF) * 128 + (paramArrayOfByte[(i3 * 2 + 9)] & 0xFF);
            localObject[i3] = ((i4 / 8192.0D - 1.0D) * 100.0D);
          }
          for (i3 = 0; i3 < tuning.length; i3++) {
            tuning[i3] = (i3 * 100 + localObject[(i3 % 12)]);
          }
        }
        break;
      }
    }
  }
  
  public double[] getTuning()
  {
    return Arrays.copyOf(tuning, tuning.length);
  }
  
  public double getTuning(int paramInt)
  {
    return tuning[paramInt];
  }
  
  public Patch getPatch()
  {
    return patch;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftTuning.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */