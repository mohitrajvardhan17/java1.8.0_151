package javax.swing.plaf.nimbus;

import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import sun.awt.AppContext;

abstract class Effect
{
  Effect() {}
  
  abstract EffectType getEffectType();
  
  abstract float getOpacity();
  
  abstract BufferedImage applyEffect(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2, int paramInt1, int paramInt2);
  
  protected static ArrayCache getArrayCache()
  {
    ArrayCache localArrayCache = (ArrayCache)AppContext.getAppContext().get(ArrayCache.class);
    if (localArrayCache == null)
    {
      localArrayCache = new ArrayCache();
      AppContext.getAppContext().put(ArrayCache.class, localArrayCache);
    }
    return localArrayCache;
  }
  
  protected static class ArrayCache
  {
    private SoftReference<int[]> tmpIntArray = null;
    private SoftReference<byte[]> tmpByteArray1 = null;
    private SoftReference<byte[]> tmpByteArray2 = null;
    private SoftReference<byte[]> tmpByteArray3 = null;
    
    protected ArrayCache() {}
    
    protected int[] getTmpIntArray(int paramInt)
    {
      int[] arrayOfInt;
      if ((tmpIntArray == null) || ((arrayOfInt = (int[])tmpIntArray.get()) == null) || (arrayOfInt.length < paramInt))
      {
        arrayOfInt = new int[paramInt];
        tmpIntArray = new SoftReference(arrayOfInt);
      }
      return arrayOfInt;
    }
    
    protected byte[] getTmpByteArray1(int paramInt)
    {
      byte[] arrayOfByte;
      if ((tmpByteArray1 == null) || ((arrayOfByte = (byte[])tmpByteArray1.get()) == null) || (arrayOfByte.length < paramInt))
      {
        arrayOfByte = new byte[paramInt];
        tmpByteArray1 = new SoftReference(arrayOfByte);
      }
      return arrayOfByte;
    }
    
    protected byte[] getTmpByteArray2(int paramInt)
    {
      byte[] arrayOfByte;
      if ((tmpByteArray2 == null) || ((arrayOfByte = (byte[])tmpByteArray2.get()) == null) || (arrayOfByte.length < paramInt))
      {
        arrayOfByte = new byte[paramInt];
        tmpByteArray2 = new SoftReference(arrayOfByte);
      }
      return arrayOfByte;
    }
    
    protected byte[] getTmpByteArray3(int paramInt)
    {
      byte[] arrayOfByte;
      if ((tmpByteArray3 == null) || ((arrayOfByte = (byte[])tmpByteArray3.get()) == null) || (arrayOfByte.length < paramInt))
      {
        arrayOfByte = new byte[paramInt];
        tmpByteArray3 = new SoftReference(arrayOfByte);
      }
      return arrayOfByte;
    }
  }
  
  static enum EffectType
  {
    UNDER,  BLENDED,  OVER;
    
    private EffectType() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\Effect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */