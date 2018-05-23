package com.sun.media.sound;

import java.util.ArrayList;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

public final class MidiUtils
{
  public static final int DEFAULT_TEMPO_MPQ = 500000;
  public static final int META_END_OF_TRACK_TYPE = 47;
  public static final int META_TEMPO_TYPE = 81;
  
  private MidiUtils() {}
  
  public static boolean isMetaEndOfTrack(MidiMessage paramMidiMessage)
  {
    if ((paramMidiMessage.getLength() != 3) || (paramMidiMessage.getStatus() != 255)) {
      return false;
    }
    byte[] arrayOfByte = paramMidiMessage.getMessage();
    return ((arrayOfByte[1] & 0xFF) == 47) && (arrayOfByte[2] == 0);
  }
  
  public static boolean isMetaTempo(MidiMessage paramMidiMessage)
  {
    if ((paramMidiMessage.getLength() != 6) || (paramMidiMessage.getStatus() != 255)) {
      return false;
    }
    byte[] arrayOfByte = paramMidiMessage.getMessage();
    return ((arrayOfByte[1] & 0xFF) == 81) && (arrayOfByte[2] == 3);
  }
  
  public static int getTempoMPQ(MidiMessage paramMidiMessage)
  {
    if ((paramMidiMessage.getLength() != 6) || (paramMidiMessage.getStatus() != 255)) {
      return -1;
    }
    byte[] arrayOfByte = paramMidiMessage.getMessage();
    if (((arrayOfByte[1] & 0xFF) != 81) || (arrayOfByte[2] != 3)) {
      return -1;
    }
    int i = arrayOfByte[5] & 0xFF | (arrayOfByte[4] & 0xFF) << 8 | (arrayOfByte[3] & 0xFF) << 16;
    return i;
  }
  
  public static double convertTempo(double paramDouble)
  {
    if (paramDouble <= 0.0D) {
      paramDouble = 1.0D;
    }
    return 6.0E7D / paramDouble;
  }
  
  public static long ticks2microsec(long paramLong, double paramDouble, int paramInt)
  {
    return (paramLong * paramDouble / paramInt);
  }
  
  public static long microsec2ticks(long paramLong, double paramDouble, int paramInt)
  {
    return (paramLong * paramInt / paramDouble);
  }
  
  public static long tick2microsecond(Sequence paramSequence, long paramLong, TempoCache paramTempoCache)
  {
    if (paramSequence.getDivisionType() != 0.0F)
    {
      double d = paramLong / (paramSequence.getDivisionType() * paramSequence.getResolution());
      return (1000000.0D * d);
    }
    if (paramTempoCache == null) {
      paramTempoCache = new TempoCache(paramSequence);
    }
    int i = paramSequence.getResolution();
    long[] arrayOfLong = ticks;
    int[] arrayOfInt = tempos;
    int j = arrayOfInt.length;
    int k = snapshotIndex;
    int m = snapshotMicro;
    long l = 0L;
    if ((k <= 0) || (k >= j) || (arrayOfLong[k] > paramLong))
    {
      m = 0;
      k = 0;
    }
    if (j > 0)
    {
      for (int n = k + 1; (n < j) && (arrayOfLong[n] <= paramLong); n++)
      {
        m = (int)(m + ticks2microsec(arrayOfLong[n] - arrayOfLong[(n - 1)], arrayOfInt[(n - 1)], i));
        k = n;
      }
      l = m + ticks2microsec(paramLong - arrayOfLong[k], arrayOfInt[k], i);
    }
    snapshotIndex = k;
    snapshotMicro = m;
    return l;
  }
  
  public static long microsecond2tick(Sequence paramSequence, long paramLong, TempoCache paramTempoCache)
  {
    if (paramSequence.getDivisionType() != 0.0F)
    {
      double d = paramLong * paramSequence.getDivisionType() * paramSequence.getResolution() / 1000000.0D;
      long l1 = d;
      if (paramTempoCache != null) {
        currTempo = ((int)paramTempoCache.getTempoMPQAt(l1));
      }
      return l1;
    }
    if (paramTempoCache == null) {
      paramTempoCache = new TempoCache(paramSequence);
    }
    long[] arrayOfLong = ticks;
    int[] arrayOfInt = tempos;
    int i = arrayOfInt.length;
    int j = paramSequence.getResolution();
    long l2 = 0L;
    long l3 = 0L;
    int k = 0;
    int m = 1;
    if ((paramLong > 0L) && (i > 0))
    {
      while (m < i)
      {
        long l4 = l2 + ticks2microsec(arrayOfLong[m] - arrayOfLong[(m - 1)], arrayOfInt[(m - 1)], j);
        if (l4 > paramLong) {
          break;
        }
        l2 = l4;
        m++;
      }
      l3 = arrayOfLong[(m - 1)] + microsec2ticks(paramLong - l2, arrayOfInt[(m - 1)], j);
    }
    currTempo = arrayOfInt[(m - 1)];
    return l3;
  }
  
  public static int tick2index(Track paramTrack, long paramLong)
  {
    int i = 0;
    if (paramLong > 0L)
    {
      int j = 0;
      int k = paramTrack.size() - 1;
      while (j < k)
      {
        i = j + k >> 1;
        long l = paramTrack.get(i).getTick();
        if (l == paramLong) {
          break;
        }
        if (l < paramLong)
        {
          if (j == k - 1)
          {
            i++;
            break;
          }
          j = i;
        }
        else
        {
          k = i;
        }
      }
    }
    return i;
  }
  
  public static final class TempoCache
  {
    long[] ticks = new long[1];
    int[] tempos = new int[1];
    int snapshotIndex = 0;
    int snapshotMicro = 0;
    int currTempo;
    private boolean firstTempoIsFake = false;
    
    public TempoCache()
    {
      tempos[0] = 500000;
      snapshotIndex = 0;
      snapshotMicro = 0;
    }
    
    public TempoCache(Sequence paramSequence)
    {
      this();
      refresh(paramSequence);
    }
    
    public synchronized void refresh(Sequence paramSequence)
    {
      ArrayList localArrayList = new ArrayList();
      Track[] arrayOfTrack = paramSequence.getTracks();
      MidiEvent localMidiEvent;
      if (arrayOfTrack.length > 0)
      {
        Track localTrack = arrayOfTrack[0];
        j = localTrack.size();
        for (k = 0; k < j; k++)
        {
          localMidiEvent = localTrack.get(k);
          MidiMessage localMidiMessage = localMidiEvent.getMessage();
          if (MidiUtils.isMetaTempo(localMidiMessage)) {
            localArrayList.add(localMidiEvent);
          }
        }
      }
      int i = localArrayList.size() + 1;
      firstTempoIsFake = true;
      if ((i > 1) && (((MidiEvent)localArrayList.get(0)).getTick() == 0L))
      {
        i--;
        firstTempoIsFake = false;
      }
      ticks = new long[i];
      tempos = new int[i];
      int j = 0;
      if (firstTempoIsFake)
      {
        ticks[0] = 0L;
        tempos[0] = 500000;
        j++;
      }
      int k = 0;
      while (k < localArrayList.size())
      {
        localMidiEvent = (MidiEvent)localArrayList.get(k);
        ticks[j] = localMidiEvent.getTick();
        tempos[j] = MidiUtils.getTempoMPQ(localMidiEvent.getMessage());
        k++;
        j++;
      }
      snapshotIndex = 0;
      snapshotMicro = 0;
    }
    
    public int getCurrTempoMPQ()
    {
      return currTempo;
    }
    
    float getTempoMPQAt(long paramLong)
    {
      return getTempoMPQAt(paramLong, -1.0F);
    }
    
    synchronized float getTempoMPQAt(long paramLong, float paramFloat)
    {
      for (int i = 0; i < ticks.length; i++) {
        if (ticks[i] > paramLong)
        {
          if (i > 0) {
            i--;
          }
          if ((paramFloat > 0.0F) && (i == 0) && (firstTempoIsFake)) {
            return paramFloat;
          }
          return tempos[i];
        }
      }
      return tempos[(tempos.length - 1)];
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\MidiUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */