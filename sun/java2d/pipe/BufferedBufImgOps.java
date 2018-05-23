package sun.java2d.pipe;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.IndexColorModel;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.ShortLookupTable;
import sun.java2d.SurfaceData;

public class BufferedBufImgOps
{
  public BufferedBufImgOps() {}
  
  public static void enableBufImgOp(RenderQueue paramRenderQueue, SurfaceData paramSurfaceData, BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp)
  {
    if ((paramBufferedImageOp instanceof ConvolveOp)) {
      enableConvolveOp(paramRenderQueue, paramSurfaceData, (ConvolveOp)paramBufferedImageOp);
    } else if ((paramBufferedImageOp instanceof RescaleOp)) {
      enableRescaleOp(paramRenderQueue, paramSurfaceData, paramBufferedImage, (RescaleOp)paramBufferedImageOp);
    } else if ((paramBufferedImageOp instanceof LookupOp)) {
      enableLookupOp(paramRenderQueue, paramSurfaceData, paramBufferedImage, (LookupOp)paramBufferedImageOp);
    } else {
      throw new InternalError("Unknown BufferedImageOp");
    }
  }
  
  public static void disableBufImgOp(RenderQueue paramRenderQueue, BufferedImageOp paramBufferedImageOp)
  {
    if ((paramBufferedImageOp instanceof ConvolveOp)) {
      disableConvolveOp(paramRenderQueue);
    } else if ((paramBufferedImageOp instanceof RescaleOp)) {
      disableRescaleOp(paramRenderQueue);
    } else if ((paramBufferedImageOp instanceof LookupOp)) {
      disableLookupOp(paramRenderQueue);
    } else {
      throw new InternalError("Unknown BufferedImageOp");
    }
  }
  
  public static boolean isConvolveOpValid(ConvolveOp paramConvolveOp)
  {
    Kernel localKernel = paramConvolveOp.getKernel();
    int i = localKernel.getWidth();
    int j = localKernel.getHeight();
    return ((i == 3) && (j == 3)) || ((i == 5) && (j == 5));
  }
  
  private static void enableConvolveOp(RenderQueue paramRenderQueue, SurfaceData paramSurfaceData, ConvolveOp paramConvolveOp)
  {
    int i = paramConvolveOp.getEdgeCondition() == 0 ? 1 : 0;
    Kernel localKernel = paramConvolveOp.getKernel();
    int j = localKernel.getWidth();
    int k = localKernel.getHeight();
    int m = j * k;
    int n = 4;
    int i1 = 24 + m * n;
    RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
    paramRenderQueue.ensureCapacityAndAlignment(i1, 4);
    localRenderBuffer.putInt(120);
    localRenderBuffer.putLong(paramSurfaceData.getNativeOps());
    localRenderBuffer.putInt(i != 0 ? 1 : 0);
    localRenderBuffer.putInt(j);
    localRenderBuffer.putInt(k);
    localRenderBuffer.put(localKernel.getKernelData(null));
  }
  
  private static void disableConvolveOp(RenderQueue paramRenderQueue)
  {
    RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
    paramRenderQueue.ensureCapacity(4);
    localRenderBuffer.putInt(121);
  }
  
  public static boolean isRescaleOpValid(RescaleOp paramRescaleOp, BufferedImage paramBufferedImage)
  {
    int i = paramRescaleOp.getNumFactors();
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    if ((localColorModel instanceof IndexColorModel)) {
      throw new IllegalArgumentException("Rescaling cannot be performed on an indexed image");
    }
    if ((i != 1) && (i != localColorModel.getNumColorComponents()) && (i != localColorModel.getNumComponents())) {
      throw new IllegalArgumentException("Number of scaling constants does not equal the number of of color or color/alpha  components");
    }
    int j = localColorModel.getColorSpace().getType();
    if ((j != 5) && (j != 6)) {
      return false;
    }
    return (i != 2) && (i <= 4);
  }
  
  private static void enableRescaleOp(RenderQueue paramRenderQueue, SurfaceData paramSurfaceData, BufferedImage paramBufferedImage, RescaleOp paramRescaleOp)
  {
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    int i = (localColorModel.hasAlpha()) && (localColorModel.isAlphaPremultiplied()) ? 1 : 0;
    int j = paramRescaleOp.getNumFactors();
    float[] arrayOfFloat1 = paramRescaleOp.getScaleFactors(null);
    float[] arrayOfFloat2 = paramRescaleOp.getOffsets(null);
    float[] arrayOfFloat3;
    float[] arrayOfFloat4;
    if (j == 1)
    {
      arrayOfFloat3 = new float[4];
      arrayOfFloat4 = new float[4];
      for (k = 0; k < 3; k++)
      {
        arrayOfFloat3[k] = arrayOfFloat1[0];
        arrayOfFloat4[k] = arrayOfFloat2[0];
      }
      arrayOfFloat3[3] = 1.0F;
      arrayOfFloat4[3] = 0.0F;
    }
    else if (j == 3)
    {
      arrayOfFloat3 = new float[4];
      arrayOfFloat4 = new float[4];
      for (k = 0; k < 3; k++)
      {
        arrayOfFloat3[k] = arrayOfFloat1[k];
        arrayOfFloat4[k] = arrayOfFloat2[k];
      }
      arrayOfFloat3[3] = 1.0F;
      arrayOfFloat4[3] = 0.0F;
    }
    else
    {
      arrayOfFloat3 = arrayOfFloat1;
      arrayOfFloat4 = arrayOfFloat2;
    }
    int n;
    if (localColorModel.getNumComponents() == 1)
    {
      k = localColorModel.getComponentSize(0);
      m = (1 << k) - 1;
      for (n = 0; n < 3; n++) {
        arrayOfFloat4[n] /= m;
      }
    }
    else
    {
      for (k = 0; k < localColorModel.getNumComponents(); k++)
      {
        m = localColorModel.getComponentSize(k);
        n = (1 << m) - 1;
        arrayOfFloat4[k] /= n;
      }
    }
    int k = 4;
    int m = 16 + 4 * k * 2;
    RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
    paramRenderQueue.ensureCapacityAndAlignment(m, 4);
    localRenderBuffer.putInt(122);
    localRenderBuffer.putLong(paramSurfaceData.getNativeOps());
    localRenderBuffer.putInt(i != 0 ? 1 : 0);
    localRenderBuffer.put(arrayOfFloat3);
    localRenderBuffer.put(arrayOfFloat4);
  }
  
  private static void disableRescaleOp(RenderQueue paramRenderQueue)
  {
    RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
    paramRenderQueue.ensureCapacity(4);
    localRenderBuffer.putInt(123);
  }
  
  public static boolean isLookupOpValid(LookupOp paramLookupOp, BufferedImage paramBufferedImage)
  {
    LookupTable localLookupTable = paramLookupOp.getTable();
    int i = localLookupTable.getNumComponents();
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    if ((localColorModel instanceof IndexColorModel)) {
      throw new IllegalArgumentException("LookupOp cannot be performed on an indexed image");
    }
    if ((i != 1) && (i != localColorModel.getNumComponents()) && (i != localColorModel.getNumColorComponents())) {
      throw new IllegalArgumentException("Number of arrays in the  lookup table (" + i + ") is not compatible with the src image: " + paramBufferedImage);
    }
    int j = localColorModel.getColorSpace().getType();
    if ((j != 5) && (j != 6)) {
      return false;
    }
    if ((i == 2) || (i > 4)) {
      return false;
    }
    Object localObject;
    int k;
    if ((localLookupTable instanceof ByteLookupTable))
    {
      localObject = ((ByteLookupTable)localLookupTable).getTable();
      for (k = 1; k < localObject.length; k++) {
        if ((localObject[k].length > 256) || (localObject[k].length != localObject[(k - 1)].length)) {
          return false;
        }
      }
    }
    else if ((localLookupTable instanceof ShortLookupTable))
    {
      localObject = ((ShortLookupTable)localLookupTable).getTable();
      for (k = 1; k < localObject.length; k++) {
        if ((localObject[k].length > 256) || (localObject[k].length != localObject[(k - 1)].length)) {
          return false;
        }
      }
    }
    else
    {
      return false;
    }
    return true;
  }
  
  private static void enableLookupOp(RenderQueue paramRenderQueue, SurfaceData paramSurfaceData, BufferedImage paramBufferedImage, LookupOp paramLookupOp)
  {
    int i = (paramBufferedImage.getColorModel().hasAlpha()) && (paramBufferedImage.isAlphaPremultiplied()) ? 1 : 0;
    LookupTable localLookupTable = paramLookupOp.getTable();
    int j = localLookupTable.getNumComponents();
    int k = localLookupTable.getOffset();
    Object localObject1;
    int m;
    int n;
    int i1;
    if ((localLookupTable instanceof ShortLookupTable))
    {
      localObject1 = ((ShortLookupTable)localLookupTable).getTable();
      m = localObject1[0].length;
      n = 2;
      i1 = 1;
    }
    else
    {
      localObject1 = ((ByteLookupTable)localLookupTable).getTable();
      m = localObject1[0].length;
      n = 1;
      i1 = 0;
    }
    int i2 = j * m * n;
    int i3 = i2 + 3 & 0xFFFFFFFC;
    int i4 = i3 - i2;
    int i5 = 32 + i3;
    RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
    paramRenderQueue.ensureCapacityAndAlignment(i5, 4);
    localRenderBuffer.putInt(124);
    localRenderBuffer.putLong(paramSurfaceData.getNativeOps());
    localRenderBuffer.putInt(i != 0 ? 1 : 0);
    localRenderBuffer.putInt(i1 != 0 ? 1 : 0);
    localRenderBuffer.putInt(j);
    localRenderBuffer.putInt(m);
    localRenderBuffer.putInt(k);
    Object localObject2;
    int i6;
    if (i1 != 0)
    {
      localObject2 = ((ShortLookupTable)localLookupTable).getTable();
      for (i6 = 0; i6 < j; i6++) {
        localRenderBuffer.put(localObject2[i6]);
      }
    }
    else
    {
      localObject2 = ((ByteLookupTable)localLookupTable).getTable();
      for (i6 = 0; i6 < j; i6++) {
        localRenderBuffer.put(localObject2[i6]);
      }
    }
    if (i4 != 0) {
      localRenderBuffer.position(localRenderBuffer.position() + i4);
    }
  }
  
  private static void disableLookupOp(RenderQueue paramRenderQueue)
  {
    RenderBuffer localRenderBuffer = paramRenderQueue.getBuffer();
    paramRenderQueue.ensureCapacity(4);
    localRenderBuffer.putInt(125);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\BufferedBufImgOps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */