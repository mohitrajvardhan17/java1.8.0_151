package java.awt.image;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import sun.java2d.cmm.CMSManager;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.PCMM;

public abstract class ColorModel
  implements Transparency
{
  private long pData;
  protected int pixel_bits;
  int[] nBits;
  int transparency = 3;
  boolean supportsAlpha = true;
  boolean isAlphaPremultiplied = false;
  int numComponents = -1;
  int numColorComponents = -1;
  ColorSpace colorSpace = ColorSpace.getInstance(1000);
  int colorSpaceType = 5;
  int maxBits;
  boolean is_sRGB = true;
  protected int transferType;
  private static boolean loaded = false;
  private static ColorModel RGBdefault;
  static byte[] l8Tos8 = null;
  static byte[] s8Tol8 = null;
  static byte[] l16Tos8 = null;
  static short[] s8Tol16 = null;
  static Map<ICC_ColorSpace, byte[]> g8Tos8Map = null;
  static Map<ICC_ColorSpace, byte[]> lg16Toog8Map = null;
  static Map<ICC_ColorSpace, byte[]> g16Tos8Map = null;
  static Map<ICC_ColorSpace, short[]> lg16Toog16Map = null;
  
  static void loadLibraries()
  {
    if (!loaded)
    {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          System.loadLibrary("awt");
          return null;
        }
      });
      loaded = true;
    }
  }
  
  private static native void initIDs();
  
  public static ColorModel getRGBdefault()
  {
    if (RGBdefault == null) {
      RGBdefault = new DirectColorModel(32, 16711680, 65280, 255, -16777216);
    }
    return RGBdefault;
  }
  
  public ColorModel(int paramInt)
  {
    pixel_bits = paramInt;
    if (paramInt < 1) {
      throw new IllegalArgumentException("Number of bits must be > 0");
    }
    numComponents = 4;
    numColorComponents = 3;
    maxBits = paramInt;
    transferType = getDefaultTransferType(paramInt);
  }
  
  protected ColorModel(int paramInt1, int[] paramArrayOfInt, ColorSpace paramColorSpace, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3)
  {
    colorSpace = paramColorSpace;
    colorSpaceType = paramColorSpace.getType();
    numColorComponents = paramColorSpace.getNumComponents();
    numComponents = (numColorComponents + (paramBoolean1 ? 1 : 0));
    supportsAlpha = paramBoolean1;
    if (paramArrayOfInt.length < numComponents) {
      throw new IllegalArgumentException("Number of color/alpha components should be " + numComponents + " but length of bits array is " + paramArrayOfInt.length);
    }
    if ((paramInt2 < 1) || (paramInt2 > 3)) {
      throw new IllegalArgumentException("Unknown transparency: " + paramInt2);
    }
    if (!supportsAlpha)
    {
      isAlphaPremultiplied = false;
      transparency = 1;
    }
    else
    {
      isAlphaPremultiplied = paramBoolean2;
      transparency = paramInt2;
    }
    nBits = ((int[])paramArrayOfInt.clone());
    pixel_bits = paramInt1;
    if (paramInt1 <= 0) {
      throw new IllegalArgumentException("Number of pixel bits must be > 0");
    }
    maxBits = 0;
    for (int i = 0; i < paramArrayOfInt.length; i++)
    {
      if (paramArrayOfInt[i] < 0) {
        throw new IllegalArgumentException("Number of bits must be >= 0");
      }
      if (maxBits < paramArrayOfInt[i]) {
        maxBits = paramArrayOfInt[i];
      }
    }
    if (maxBits == 0) {
      throw new IllegalArgumentException("There must be at least one component with > 0 pixel bits.");
    }
    if (paramColorSpace != ColorSpace.getInstance(1000)) {
      is_sRGB = false;
    }
    transferType = paramInt3;
  }
  
  public final boolean hasAlpha()
  {
    return supportsAlpha;
  }
  
  public final boolean isAlphaPremultiplied()
  {
    return isAlphaPremultiplied;
  }
  
  public final int getTransferType()
  {
    return transferType;
  }
  
  public int getPixelSize()
  {
    return pixel_bits;
  }
  
  public int getComponentSize(int paramInt)
  {
    if (nBits == null) {
      throw new NullPointerException("Number of bits array is null.");
    }
    return nBits[paramInt];
  }
  
  public int[] getComponentSize()
  {
    if (nBits != null) {
      return (int[])nBits.clone();
    }
    return null;
  }
  
  public int getTransparency()
  {
    return transparency;
  }
  
  public int getNumComponents()
  {
    return numComponents;
  }
  
  public int getNumColorComponents()
  {
    return numColorComponents;
  }
  
  public abstract int getRed(int paramInt);
  
  public abstract int getGreen(int paramInt);
  
  public abstract int getBlue(int paramInt);
  
  public abstract int getAlpha(int paramInt);
  
  public int getRGB(int paramInt)
  {
    return getAlpha(paramInt) << 24 | getRed(paramInt) << 16 | getGreen(paramInt) << 8 | getBlue(paramInt) << 0;
  }
  
  public int getRed(Object paramObject)
  {
    int i = 0;
    int j = 0;
    switch (transferType)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      i = arrayOfByte[0] & 0xFF;
      j = arrayOfByte.length;
      break;
    case 1: 
      short[] arrayOfShort = (short[])paramObject;
      i = arrayOfShort[0] & 0xFFFF;
      j = arrayOfShort.length;
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      i = arrayOfInt[0];
      j = arrayOfInt.length;
      break;
    case 2: 
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
    }
    if (j == 1) {
      return getRed(i);
    }
    throw new UnsupportedOperationException("This method is not supported by this color model");
  }
  
  public int getGreen(Object paramObject)
  {
    int i = 0;
    int j = 0;
    switch (transferType)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      i = arrayOfByte[0] & 0xFF;
      j = arrayOfByte.length;
      break;
    case 1: 
      short[] arrayOfShort = (short[])paramObject;
      i = arrayOfShort[0] & 0xFFFF;
      j = arrayOfShort.length;
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      i = arrayOfInt[0];
      j = arrayOfInt.length;
      break;
    case 2: 
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
    }
    if (j == 1) {
      return getGreen(i);
    }
    throw new UnsupportedOperationException("This method is not supported by this color model");
  }
  
  public int getBlue(Object paramObject)
  {
    int i = 0;
    int j = 0;
    switch (transferType)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      i = arrayOfByte[0] & 0xFF;
      j = arrayOfByte.length;
      break;
    case 1: 
      short[] arrayOfShort = (short[])paramObject;
      i = arrayOfShort[0] & 0xFFFF;
      j = arrayOfShort.length;
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      i = arrayOfInt[0];
      j = arrayOfInt.length;
      break;
    case 2: 
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
    }
    if (j == 1) {
      return getBlue(i);
    }
    throw new UnsupportedOperationException("This method is not supported by this color model");
  }
  
  public int getAlpha(Object paramObject)
  {
    int i = 0;
    int j = 0;
    switch (transferType)
    {
    case 0: 
      byte[] arrayOfByte = (byte[])paramObject;
      i = arrayOfByte[0] & 0xFF;
      j = arrayOfByte.length;
      break;
    case 1: 
      short[] arrayOfShort = (short[])paramObject;
      i = arrayOfShort[0] & 0xFFFF;
      j = arrayOfShort.length;
      break;
    case 3: 
      int[] arrayOfInt = (int[])paramObject;
      i = arrayOfInt[0];
      j = arrayOfInt.length;
      break;
    case 2: 
    default: 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + transferType);
    }
    if (j == 1) {
      return getAlpha(i);
    }
    throw new UnsupportedOperationException("This method is not supported by this color model");
  }
  
  public int getRGB(Object paramObject)
  {
    return getAlpha(paramObject) << 24 | getRed(paramObject) << 16 | getGreen(paramObject) << 8 | getBlue(paramObject) << 0;
  }
  
  public Object getDataElements(int paramInt, Object paramObject)
  {
    throw new UnsupportedOperationException("This method is not supported by this color model.");
  }
  
  public int[] getComponents(int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    throw new UnsupportedOperationException("This method is not supported by this color model.");
  }
  
  public int[] getComponents(Object paramObject, int[] paramArrayOfInt, int paramInt)
  {
    throw new UnsupportedOperationException("This method is not supported by this color model.");
  }
  
  public int[] getUnnormalizedComponents(float[] paramArrayOfFloat, int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    if (colorSpace == null) {
      throw new UnsupportedOperationException("This method is not supported by this color model.");
    }
    if (nBits == null) {
      throw new UnsupportedOperationException("This method is not supported.  Unable to determine #bits per component.");
    }
    if (paramArrayOfFloat.length - paramInt1 < numComponents) {
      throw new IllegalArgumentException("Incorrect number of components.  Expecting " + numComponents);
    }
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[paramInt2 + numComponents];
    }
    if ((supportsAlpha) && (isAlphaPremultiplied))
    {
      float f = paramArrayOfFloat[(paramInt1 + numColorComponents)];
      for (int j = 0; j < numColorComponents; j++) {
        paramArrayOfInt[(paramInt2 + j)] = ((int)(paramArrayOfFloat[(paramInt1 + j)] * ((1 << nBits[j]) - 1) * f + 0.5F));
      }
      paramArrayOfInt[(paramInt2 + numColorComponents)] = ((int)(f * ((1 << nBits[numColorComponents]) - 1) + 0.5F));
    }
    else
    {
      for (int i = 0; i < numComponents; i++) {
        paramArrayOfInt[(paramInt2 + i)] = ((int)(paramArrayOfFloat[(paramInt1 + i)] * ((1 << nBits[i]) - 1) + 0.5F));
      }
    }
    return paramArrayOfInt;
  }
  
  public float[] getNormalizedComponents(int[] paramArrayOfInt, int paramInt1, float[] paramArrayOfFloat, int paramInt2)
  {
    if (colorSpace == null) {
      throw new UnsupportedOperationException("This method is not supported by this color model.");
    }
    if (nBits == null) {
      throw new UnsupportedOperationException("This method is not supported.  Unable to determine #bits per component.");
    }
    if (paramArrayOfInt.length - paramInt1 < numComponents) {
      throw new IllegalArgumentException("Incorrect number of components.  Expecting " + numComponents);
    }
    if (paramArrayOfFloat == null) {
      paramArrayOfFloat = new float[numComponents + paramInt2];
    }
    if ((supportsAlpha) && (isAlphaPremultiplied))
    {
      float f = paramArrayOfInt[(paramInt1 + numColorComponents)];
      f /= ((1 << nBits[numColorComponents]) - 1);
      int j;
      if (f != 0.0F) {
        for (j = 0; j < numColorComponents; j++) {
          paramArrayOfFloat[(paramInt2 + j)] = (paramArrayOfInt[(paramInt1 + j)] / (f * ((1 << nBits[j]) - 1)));
        }
      } else {
        for (j = 0; j < numColorComponents; j++) {
          paramArrayOfFloat[(paramInt2 + j)] = 0.0F;
        }
      }
      paramArrayOfFloat[(paramInt2 + numColorComponents)] = f;
    }
    else
    {
      for (int i = 0; i < numComponents; i++) {
        paramArrayOfFloat[(paramInt2 + i)] = (paramArrayOfInt[(paramInt1 + i)] / ((1 << nBits[i]) - 1));
      }
    }
    return paramArrayOfFloat;
  }
  
  public int getDataElement(int[] paramArrayOfInt, int paramInt)
  {
    throw new UnsupportedOperationException("This method is not supported by this color model.");
  }
  
  public Object getDataElements(int[] paramArrayOfInt, int paramInt, Object paramObject)
  {
    throw new UnsupportedOperationException("This method has not been implemented for this color model.");
  }
  
  public int getDataElement(float[] paramArrayOfFloat, int paramInt)
  {
    int[] arrayOfInt = getUnnormalizedComponents(paramArrayOfFloat, paramInt, null, 0);
    return getDataElement(arrayOfInt, 0);
  }
  
  public Object getDataElements(float[] paramArrayOfFloat, int paramInt, Object paramObject)
  {
    int[] arrayOfInt = getUnnormalizedComponents(paramArrayOfFloat, paramInt, null, 0);
    return getDataElements(arrayOfInt, 0, paramObject);
  }
  
  public float[] getNormalizedComponents(Object paramObject, float[] paramArrayOfFloat, int paramInt)
  {
    int[] arrayOfInt = getComponents(paramObject, null, 0);
    return getNormalizedComponents(arrayOfInt, 0, paramArrayOfFloat, paramInt);
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ColorModel)) {
      return false;
    }
    ColorModel localColorModel = (ColorModel)paramObject;
    if (this == localColorModel) {
      return true;
    }
    if ((supportsAlpha != localColorModel.hasAlpha()) || (isAlphaPremultiplied != localColorModel.isAlphaPremultiplied()) || (pixel_bits != localColorModel.getPixelSize()) || (transparency != localColorModel.getTransparency()) || (numComponents != localColorModel.getNumComponents())) {
      return false;
    }
    int[] arrayOfInt = localColorModel.getComponentSize();
    if ((nBits != null) && (arrayOfInt != null)) {
      for (int i = 0; i < numComponents; i++) {
        if (nBits[i] != arrayOfInt[i]) {
          return false;
        }
      }
    } else {
      return (nBits == null) && (arrayOfInt == null);
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 0;
    i = (supportsAlpha ? 2 : 3) + (isAlphaPremultiplied ? 4 : 5) + pixel_bits * 6 + transparency * 7 + numComponents * 8;
    if (nBits != null) {
      for (int j = 0; j < numComponents; j++) {
        i += nBits[j] * (j + 9);
      }
    }
    return i;
  }
  
  public final ColorSpace getColorSpace()
  {
    return colorSpace;
  }
  
  public ColorModel coerceData(WritableRaster paramWritableRaster, boolean paramBoolean)
  {
    throw new UnsupportedOperationException("This method is not supported by this color model");
  }
  
  public boolean isCompatibleRaster(Raster paramRaster)
  {
    throw new UnsupportedOperationException("This method has not been implemented for this ColorModel.");
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2)
  {
    throw new UnsupportedOperationException("This method is not supported by this color model");
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2)
  {
    throw new UnsupportedOperationException("This method is not supported by this color model");
  }
  
  public boolean isCompatibleSampleModel(SampleModel paramSampleModel)
  {
    throw new UnsupportedOperationException("This method is not supported by this color model");
  }
  
  public void finalize() {}
  
  public WritableRaster getAlphaRaster(WritableRaster paramWritableRaster)
  {
    return null;
  }
  
  public String toString()
  {
    return new String("ColorModel: #pixelBits = " + pixel_bits + " numComponents = " + numComponents + " color space = " + colorSpace + " transparency = " + transparency + " has alpha = " + supportsAlpha + " isAlphaPre = " + isAlphaPremultiplied);
  }
  
  static int getDefaultTransferType(int paramInt)
  {
    if (paramInt <= 8) {
      return 0;
    }
    if (paramInt <= 16) {
      return 1;
    }
    if (paramInt <= 32) {
      return 3;
    }
    return 32;
  }
  
  static boolean isLinearRGBspace(ColorSpace paramColorSpace)
  {
    return paramColorSpace == CMSManager.LINEAR_RGBspace;
  }
  
  static boolean isLinearGRAYspace(ColorSpace paramColorSpace)
  {
    return paramColorSpace == CMSManager.GRAYspace;
  }
  
  static byte[] getLinearRGB8TosRGB8LUT()
  {
    if (l8Tos8 == null)
    {
      l8Tos8 = new byte['Ā'];
      for (int i = 0; i <= 255; i++)
      {
        float f1 = i / 255.0F;
        float f2;
        if (f1 <= 0.0031308F) {
          f2 = f1 * 12.92F;
        } else {
          f2 = 1.055F * (float)Math.pow(f1, 0.4166666666666667D) - 0.055F;
        }
        l8Tos8[i] = ((byte)Math.round(f2 * 255.0F));
      }
    }
    return l8Tos8;
  }
  
  static byte[] getsRGB8ToLinearRGB8LUT()
  {
    if (s8Tol8 == null)
    {
      s8Tol8 = new byte['Ā'];
      for (int i = 0; i <= 255; i++)
      {
        float f1 = i / 255.0F;
        float f2;
        if (f1 <= 0.04045F) {
          f2 = f1 / 12.92F;
        } else {
          f2 = (float)Math.pow((f1 + 0.055F) / 1.055F, 2.4D);
        }
        s8Tol8[i] = ((byte)Math.round(f2 * 255.0F));
      }
    }
    return s8Tol8;
  }
  
  static byte[] getLinearRGB16TosRGB8LUT()
  {
    if (l16Tos8 == null)
    {
      l16Tos8 = new byte[65536];
      for (int i = 0; i <= 65535; i++)
      {
        float f1 = i / 65535.0F;
        float f2;
        if (f1 <= 0.0031308F) {
          f2 = f1 * 12.92F;
        } else {
          f2 = 1.055F * (float)Math.pow(f1, 0.4166666666666667D) - 0.055F;
        }
        l16Tos8[i] = ((byte)Math.round(f2 * 255.0F));
      }
    }
    return l16Tos8;
  }
  
  static short[] getsRGB8ToLinearRGB16LUT()
  {
    if (s8Tol16 == null)
    {
      s8Tol16 = new short['Ā'];
      for (int i = 0; i <= 255; i++)
      {
        float f1 = i / 255.0F;
        float f2;
        if (f1 <= 0.04045F) {
          f2 = f1 / 12.92F;
        } else {
          f2 = (float)Math.pow((f1 + 0.055F) / 1.055F, 2.4D);
        }
        s8Tol16[i] = ((short)Math.round(f2 * 65535.0F));
      }
    }
    return s8Tol16;
  }
  
  static byte[] getGray8TosRGB8LUT(ICC_ColorSpace paramICC_ColorSpace)
  {
    if (isLinearGRAYspace(paramICC_ColorSpace)) {
      return getLinearRGB8TosRGB8LUT();
    }
    if (g8Tos8Map != null)
    {
      arrayOfByte1 = (byte[])g8Tos8Map.get(paramICC_ColorSpace);
      if (arrayOfByte1 != null) {
        return arrayOfByte1;
      }
    }
    byte[] arrayOfByte1 = new byte['Ā'];
    for (int i = 0; i <= 255; i++) {
      arrayOfByte1[i] = ((byte)i);
    }
    ColorTransform[] arrayOfColorTransform = new ColorTransform[2];
    PCMM localPCMM = CMSManager.getModule();
    ICC_ColorSpace localICC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1000);
    arrayOfColorTransform[0] = localPCMM.createTransform(paramICC_ColorSpace.getProfile(), -1, 1);
    arrayOfColorTransform[1] = localPCMM.createTransform(localICC_ColorSpace.getProfile(), -1, 2);
    ColorTransform localColorTransform = localPCMM.createTransform(arrayOfColorTransform);
    byte[] arrayOfByte2 = localColorTransform.colorConvert(arrayOfByte1, null);
    int j = 0;
    for (int k = 2; j <= 255; k += 3)
    {
      arrayOfByte1[j] = arrayOfByte2[k];
      j++;
    }
    if (g8Tos8Map == null) {
      g8Tos8Map = Collections.synchronizedMap(new WeakHashMap(2));
    }
    g8Tos8Map.put(paramICC_ColorSpace, arrayOfByte1);
    return arrayOfByte1;
  }
  
  static byte[] getLinearGray16ToOtherGray8LUT(ICC_ColorSpace paramICC_ColorSpace)
  {
    if (lg16Toog8Map != null)
    {
      localObject = (byte[])lg16Toog8Map.get(paramICC_ColorSpace);
      if (localObject != null) {
        return (byte[])localObject;
      }
    }
    Object localObject = new short[65536];
    for (int i = 0; i <= 65535; i++) {
      localObject[i] = ((short)i);
    }
    ColorTransform[] arrayOfColorTransform = new ColorTransform[2];
    PCMM localPCMM = CMSManager.getModule();
    ICC_ColorSpace localICC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1003);
    arrayOfColorTransform[0] = localPCMM.createTransform(localICC_ColorSpace.getProfile(), -1, 1);
    arrayOfColorTransform[1] = localPCMM.createTransform(paramICC_ColorSpace.getProfile(), -1, 2);
    ColorTransform localColorTransform = localPCMM.createTransform(arrayOfColorTransform);
    localObject = localColorTransform.colorConvert((short[])localObject, null);
    byte[] arrayOfByte = new byte[65536];
    for (int j = 0; j <= 65535; j++) {
      arrayOfByte[j] = ((byte)(int)((localObject[j] & 0xFFFF) * 0.0038910506F + 0.5F));
    }
    if (lg16Toog8Map == null) {
      lg16Toog8Map = Collections.synchronizedMap(new WeakHashMap(2));
    }
    lg16Toog8Map.put(paramICC_ColorSpace, arrayOfByte);
    return arrayOfByte;
  }
  
  static byte[] getGray16TosRGB8LUT(ICC_ColorSpace paramICC_ColorSpace)
  {
    if (isLinearGRAYspace(paramICC_ColorSpace)) {
      return getLinearRGB16TosRGB8LUT();
    }
    if (g16Tos8Map != null)
    {
      localObject = (byte[])g16Tos8Map.get(paramICC_ColorSpace);
      if (localObject != null) {
        return (byte[])localObject;
      }
    }
    Object localObject = new short[65536];
    for (int i = 0; i <= 65535; i++) {
      localObject[i] = ((short)i);
    }
    ColorTransform[] arrayOfColorTransform = new ColorTransform[2];
    PCMM localPCMM = CMSManager.getModule();
    ICC_ColorSpace localICC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1000);
    arrayOfColorTransform[0] = localPCMM.createTransform(paramICC_ColorSpace.getProfile(), -1, 1);
    arrayOfColorTransform[1] = localPCMM.createTransform(localICC_ColorSpace.getProfile(), -1, 2);
    ColorTransform localColorTransform = localPCMM.createTransform(arrayOfColorTransform);
    localObject = localColorTransform.colorConvert((short[])localObject, null);
    byte[] arrayOfByte = new byte[65536];
    int j = 0;
    for (int k = 2; j <= 65535; k += 3)
    {
      arrayOfByte[j] = ((byte)(int)((localObject[k] & 0xFFFF) * 0.0038910506F + 0.5F));
      j++;
    }
    if (g16Tos8Map == null) {
      g16Tos8Map = Collections.synchronizedMap(new WeakHashMap(2));
    }
    g16Tos8Map.put(paramICC_ColorSpace, arrayOfByte);
    return arrayOfByte;
  }
  
  static short[] getLinearGray16ToOtherGray16LUT(ICC_ColorSpace paramICC_ColorSpace)
  {
    if (lg16Toog16Map != null)
    {
      arrayOfShort1 = (short[])lg16Toog16Map.get(paramICC_ColorSpace);
      if (arrayOfShort1 != null) {
        return arrayOfShort1;
      }
    }
    short[] arrayOfShort1 = new short[65536];
    for (int i = 0; i <= 65535; i++) {
      arrayOfShort1[i] = ((short)i);
    }
    ColorTransform[] arrayOfColorTransform = new ColorTransform[2];
    PCMM localPCMM = CMSManager.getModule();
    ICC_ColorSpace localICC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1003);
    arrayOfColorTransform[0] = localPCMM.createTransform(localICC_ColorSpace.getProfile(), -1, 1);
    arrayOfColorTransform[1] = localPCMM.createTransform(paramICC_ColorSpace.getProfile(), -1, 2);
    ColorTransform localColorTransform = localPCMM.createTransform(arrayOfColorTransform);
    short[] arrayOfShort2 = localColorTransform.colorConvert(arrayOfShort1, null);
    if (lg16Toog16Map == null) {
      lg16Toog16Map = Collections.synchronizedMap(new WeakHashMap(2));
    }
    lg16Toog16Map.put(paramICC_ColorSpace, arrayOfShort2);
    return arrayOfShort2;
  }
  
  static
  {
    loadLibraries();
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\ColorModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */