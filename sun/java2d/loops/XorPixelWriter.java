package sun.java2d.loops;

import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

abstract class XorPixelWriter
  extends PixelWriter
{
  protected ColorModel dstCM;
  
  XorPixelWriter() {}
  
  public void writePixel(int paramInt1, int paramInt2)
  {
    Object localObject = dstRast.getDataElements(paramInt1, paramInt2, null);
    xorPixel(localObject);
    dstRast.setDataElements(paramInt1, paramInt2, localObject);
  }
  
  protected abstract void xorPixel(Object paramObject);
  
  public static class ByteData
    extends XorPixelWriter
  {
    byte[] xorData;
    
    ByteData(Object paramObject1, Object paramObject2)
    {
      xorData = ((byte[])paramObject1);
      xorPixel(paramObject2);
      xorData = ((byte[])paramObject2);
    }
    
    protected void xorPixel(Object paramObject)
    {
      byte[] arrayOfByte = (byte[])paramObject;
      for (int i = 0; i < arrayOfByte.length; i++)
      {
        int tmp18_17 = i;
        byte[] tmp18_16 = arrayOfByte;
        tmp18_16[tmp18_17] = ((byte)(tmp18_16[tmp18_17] ^ xorData[i]));
      }
    }
  }
  
  public static class DoubleData
    extends XorPixelWriter
  {
    long[] xorData;
    
    DoubleData(Object paramObject1, Object paramObject2)
    {
      double[] arrayOfDouble1 = (double[])paramObject1;
      double[] arrayOfDouble2 = (double[])paramObject2;
      xorData = new long[arrayOfDouble1.length];
      for (int i = 0; i < arrayOfDouble1.length; i++) {
        xorData[i] = (Double.doubleToLongBits(arrayOfDouble1[i]) ^ Double.doubleToLongBits(arrayOfDouble2[i]));
      }
    }
    
    protected void xorPixel(Object paramObject)
    {
      double[] arrayOfDouble = (double[])paramObject;
      for (int i = 0; i < arrayOfDouble.length; i++)
      {
        long l = Double.doubleToLongBits(arrayOfDouble[i]) ^ xorData[i];
        arrayOfDouble[i] = Double.longBitsToDouble(l);
      }
    }
  }
  
  public static class FloatData
    extends XorPixelWriter
  {
    int[] xorData;
    
    FloatData(Object paramObject1, Object paramObject2)
    {
      float[] arrayOfFloat1 = (float[])paramObject1;
      float[] arrayOfFloat2 = (float[])paramObject2;
      xorData = new int[arrayOfFloat1.length];
      for (int i = 0; i < arrayOfFloat1.length; i++) {
        xorData[i] = (Float.floatToIntBits(arrayOfFloat1[i]) ^ Float.floatToIntBits(arrayOfFloat2[i]));
      }
    }
    
    protected void xorPixel(Object paramObject)
    {
      float[] arrayOfFloat = (float[])paramObject;
      for (int i = 0; i < arrayOfFloat.length; i++)
      {
        int j = Float.floatToIntBits(arrayOfFloat[i]) ^ xorData[i];
        arrayOfFloat[i] = Float.intBitsToFloat(j);
      }
    }
  }
  
  public static class IntData
    extends XorPixelWriter
  {
    int[] xorData;
    
    IntData(Object paramObject1, Object paramObject2)
    {
      xorData = ((int[])paramObject1);
      xorPixel(paramObject2);
      xorData = ((int[])paramObject2);
    }
    
    protected void xorPixel(Object paramObject)
    {
      int[] arrayOfInt = (int[])paramObject;
      for (int i = 0; i < arrayOfInt.length; i++) {
        arrayOfInt[i] ^= xorData[i];
      }
    }
  }
  
  public static class ShortData
    extends XorPixelWriter
  {
    short[] xorData;
    
    ShortData(Object paramObject1, Object paramObject2)
    {
      xorData = ((short[])paramObject1);
      xorPixel(paramObject2);
      xorData = ((short[])paramObject2);
    }
    
    protected void xorPixel(Object paramObject)
    {
      short[] arrayOfShort = (short[])paramObject;
      for (int i = 0; i < arrayOfShort.length; i++)
      {
        int tmp18_17 = i;
        short[] tmp18_16 = arrayOfShort;
        tmp18_16[tmp18_17] = ((short)(tmp18_16[tmp18_17] ^ xorData[i]));
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\XorPixelWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */