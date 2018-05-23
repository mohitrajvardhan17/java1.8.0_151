package com.sun.media.sound;

public final class SF2Modulator
{
  public static final int SOURCE_NONE = 0;
  public static final int SOURCE_NOTE_ON_VELOCITY = 2;
  public static final int SOURCE_NOTE_ON_KEYNUMBER = 3;
  public static final int SOURCE_POLY_PRESSURE = 10;
  public static final int SOURCE_CHANNEL_PRESSURE = 13;
  public static final int SOURCE_PITCH_WHEEL = 14;
  public static final int SOURCE_PITCH_SENSITIVITY = 16;
  public static final int SOURCE_MIDI_CONTROL = 128;
  public static final int SOURCE_DIRECTION_MIN_MAX = 0;
  public static final int SOURCE_DIRECTION_MAX_MIN = 256;
  public static final int SOURCE_POLARITY_UNIPOLAR = 0;
  public static final int SOURCE_POLARITY_BIPOLAR = 512;
  public static final int SOURCE_TYPE_LINEAR = 0;
  public static final int SOURCE_TYPE_CONCAVE = 1024;
  public static final int SOURCE_TYPE_CONVEX = 2048;
  public static final int SOURCE_TYPE_SWITCH = 3072;
  public static final int TRANSFORM_LINEAR = 0;
  public static final int TRANSFORM_ABSOLUTE = 2;
  int sourceOperator;
  int destinationOperator;
  short amount;
  int amountSourceOperator;
  int transportOperator;
  
  public SF2Modulator() {}
  
  public short getAmount()
  {
    return amount;
  }
  
  public void setAmount(short paramShort)
  {
    amount = paramShort;
  }
  
  public int getAmountSourceOperator()
  {
    return amountSourceOperator;
  }
  
  public void setAmountSourceOperator(int paramInt)
  {
    amountSourceOperator = paramInt;
  }
  
  public int getTransportOperator()
  {
    return transportOperator;
  }
  
  public void setTransportOperator(int paramInt)
  {
    transportOperator = paramInt;
  }
  
  public int getDestinationOperator()
  {
    return destinationOperator;
  }
  
  public void setDestinationOperator(int paramInt)
  {
    destinationOperator = paramInt;
  }
  
  public int getSourceOperator()
  {
    return sourceOperator;
  }
  
  public void setSourceOperator(int paramInt)
  {
    sourceOperator = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SF2Modulator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */