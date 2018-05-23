package sun.awt.image;

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;

public class BufImgSurfaceData
  extends SurfaceData
{
  BufferedImage bufImg;
  private BufferedImageGraphicsConfig graphicsConfig;
  RenderLoops solidloops;
  private static final int DCM_RGBX_RED_MASK = -16777216;
  private static final int DCM_RGBX_GREEN_MASK = 16711680;
  private static final int DCM_RGBX_BLUE_MASK = 65280;
  private static final int DCM_555X_RED_MASK = 63488;
  private static final int DCM_555X_GREEN_MASK = 1984;
  private static final int DCM_555X_BLUE_MASK = 62;
  private static final int DCM_4444_RED_MASK = 3840;
  private static final int DCM_4444_GREEN_MASK = 240;
  private static final int DCM_4444_BLUE_MASK = 15;
  private static final int DCM_4444_ALPHA_MASK = 61440;
  private static final int DCM_ARGBBM_ALPHA_MASK = 16777216;
  private static final int DCM_ARGBBM_RED_MASK = 16711680;
  private static final int DCM_ARGBBM_GREEN_MASK = 65280;
  private static final int DCM_ARGBBM_BLUE_MASK = 255;
  private static final int CACHE_SIZE = 5;
  private static RenderLoops[] loopcache = new RenderLoops[5];
  private static SurfaceType[] typecache = new SurfaceType[5];
  
  private static native void initIDs(Class paramClass1, Class paramClass2);
  
  public static SurfaceData createData(BufferedImage paramBufferedImage)
  {
    if (paramBufferedImage == null) {
      throw new NullPointerException("BufferedImage cannot be null");
    }
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    int i = paramBufferedImage.getType();
    Object localObject1;
    Object localObject2;
    switch (i)
    {
    case 4: 
      localObject1 = createDataIC(paramBufferedImage, SurfaceType.IntBgr);
      break;
    case 1: 
      localObject1 = createDataIC(paramBufferedImage, SurfaceType.IntRgb);
      break;
    case 2: 
      localObject1 = createDataIC(paramBufferedImage, SurfaceType.IntArgb);
      break;
    case 3: 
      localObject1 = createDataIC(paramBufferedImage, SurfaceType.IntArgbPre);
      break;
    case 5: 
      localObject1 = createDataBC(paramBufferedImage, SurfaceType.ThreeByteBgr, 2);
      break;
    case 6: 
      localObject1 = createDataBC(paramBufferedImage, SurfaceType.FourByteAbgr, 3);
      break;
    case 7: 
      localObject1 = createDataBC(paramBufferedImage, SurfaceType.FourByteAbgrPre, 3);
      break;
    case 8: 
      localObject1 = createDataSC(paramBufferedImage, SurfaceType.Ushort565Rgb, null);
      break;
    case 9: 
      localObject1 = createDataSC(paramBufferedImage, SurfaceType.Ushort555Rgb, null);
      break;
    case 13: 
      switch (localColorModel.getTransparency())
      {
      case 1: 
        if (isOpaqueGray((IndexColorModel)localColorModel)) {
          localObject2 = SurfaceType.Index8Gray;
        } else {
          localObject2 = SurfaceType.ByteIndexedOpaque;
        }
        break;
      case 2: 
        localObject2 = SurfaceType.ByteIndexedBm;
        break;
      case 3: 
        localObject2 = SurfaceType.ByteIndexed;
        break;
      default: 
        throw new InternalError("Unrecognized transparency");
      }
      localObject1 = createDataBC(paramBufferedImage, (SurfaceType)localObject2, 0);
      break;
    case 10: 
      localObject1 = createDataBC(paramBufferedImage, SurfaceType.ByteGray, 0);
      break;
    case 11: 
      localObject1 = createDataSC(paramBufferedImage, SurfaceType.UshortGray, null);
      break;
    case 12: 
      SampleModel localSampleModel = paramBufferedImage.getRaster().getSampleModel();
      switch (localSampleModel.getSampleSize(0))
      {
      case 1: 
        localObject2 = SurfaceType.ByteBinary1Bit;
        break;
      case 2: 
        localObject2 = SurfaceType.ByteBinary2Bit;
        break;
      case 4: 
        localObject2 = SurfaceType.ByteBinary4Bit;
        break;
      case 3: 
      default: 
        throw new InternalError("Unrecognized pixel size");
      }
      localObject1 = createDataBP(paramBufferedImage, (SurfaceType)localObject2);
      break;
    case 0: 
    default: 
      localObject2 = paramBufferedImage.getRaster();
      int j = ((Raster)localObject2).getNumBands();
      SurfaceType localSurfaceType;
      Object localObject3;
      int m;
      int n;
      int i1;
      if (((localObject2 instanceof IntegerComponentRaster)) && (((Raster)localObject2).getNumDataElements() == 1) && (((IntegerComponentRaster)localObject2).getPixelStride() == 1))
      {
        localSurfaceType = SurfaceType.AnyInt;
        if ((localColorModel instanceof DirectColorModel))
        {
          localObject3 = (DirectColorModel)localColorModel;
          int k = ((DirectColorModel)localObject3).getAlphaMask();
          m = ((DirectColorModel)localObject3).getRedMask();
          n = ((DirectColorModel)localObject3).getGreenMask();
          i1 = ((DirectColorModel)localObject3).getBlueMask();
          if ((j == 3) && (k == 0) && (m == -16777216) && (n == 16711680) && (i1 == 65280)) {
            localSurfaceType = SurfaceType.IntRgbx;
          } else if ((j == 4) && (k == 16777216) && (m == 16711680) && (n == 65280) && (i1 == 255)) {
            localSurfaceType = SurfaceType.IntArgbBm;
          } else {
            localSurfaceType = SurfaceType.AnyDcm;
          }
        }
        localObject1 = createDataIC(paramBufferedImage, localSurfaceType);
      }
      else if (((localObject2 instanceof ShortComponentRaster)) && (((Raster)localObject2).getNumDataElements() == 1) && (((ShortComponentRaster)localObject2).getPixelStride() == 1))
      {
        localSurfaceType = SurfaceType.AnyShort;
        localObject3 = null;
        if ((localColorModel instanceof DirectColorModel))
        {
          DirectColorModel localDirectColorModel = (DirectColorModel)localColorModel;
          m = localDirectColorModel.getAlphaMask();
          n = localDirectColorModel.getRedMask();
          i1 = localDirectColorModel.getGreenMask();
          int i2 = localDirectColorModel.getBlueMask();
          if ((j == 3) && (m == 0) && (n == 63488) && (i1 == 1984) && (i2 == 62)) {
            localSurfaceType = SurfaceType.Ushort555Rgbx;
          } else if ((j == 4) && (m == 61440) && (n == 3840) && (i1 == 240) && (i2 == 15)) {
            localSurfaceType = SurfaceType.Ushort4444Argb;
          }
        }
        else if ((localColorModel instanceof IndexColorModel))
        {
          localObject3 = (IndexColorModel)localColorModel;
          if (((IndexColorModel)localObject3).getPixelSize() == 12)
          {
            if (isOpaqueGray((IndexColorModel)localObject3)) {
              localSurfaceType = SurfaceType.Index12Gray;
            } else {
              localSurfaceType = SurfaceType.UshortIndexed;
            }
          }
          else {
            localObject3 = null;
          }
        }
        localObject1 = createDataSC(paramBufferedImage, localSurfaceType, (IndexColorModel)localObject3);
      }
      else
      {
        localObject1 = new BufImgSurfaceData(((Raster)localObject2).getDataBuffer(), paramBufferedImage, SurfaceType.Custom);
      }
      break;
    }
    ((BufImgSurfaceData)localObject1).initSolidLoops();
    return (SurfaceData)localObject1;
  }
  
  public static SurfaceData createData(Raster paramRaster, ColorModel paramColorModel)
  {
    throw new InternalError("SurfaceData not implemented for Raster/CM");
  }
  
  public static SurfaceData createDataIC(BufferedImage paramBufferedImage, SurfaceType paramSurfaceType)
  {
    IntegerComponentRaster localIntegerComponentRaster = (IntegerComponentRaster)paramBufferedImage.getRaster();
    BufImgSurfaceData localBufImgSurfaceData = new BufImgSurfaceData(localIntegerComponentRaster.getDataBuffer(), paramBufferedImage, paramSurfaceType);
    localBufImgSurfaceData.initRaster(localIntegerComponentRaster.getDataStorage(), localIntegerComponentRaster.getDataOffset(0) * 4, 0, localIntegerComponentRaster.getWidth(), localIntegerComponentRaster.getHeight(), localIntegerComponentRaster.getPixelStride() * 4, localIntegerComponentRaster.getScanlineStride() * 4, null);
    return localBufImgSurfaceData;
  }
  
  public static SurfaceData createDataSC(BufferedImage paramBufferedImage, SurfaceType paramSurfaceType, IndexColorModel paramIndexColorModel)
  {
    ShortComponentRaster localShortComponentRaster = (ShortComponentRaster)paramBufferedImage.getRaster();
    BufImgSurfaceData localBufImgSurfaceData = new BufImgSurfaceData(localShortComponentRaster.getDataBuffer(), paramBufferedImage, paramSurfaceType);
    localBufImgSurfaceData.initRaster(localShortComponentRaster.getDataStorage(), localShortComponentRaster.getDataOffset(0) * 2, 0, localShortComponentRaster.getWidth(), localShortComponentRaster.getHeight(), localShortComponentRaster.getPixelStride() * 2, localShortComponentRaster.getScanlineStride() * 2, paramIndexColorModel);
    return localBufImgSurfaceData;
  }
  
  public static SurfaceData createDataBC(BufferedImage paramBufferedImage, SurfaceType paramSurfaceType, int paramInt)
  {
    ByteComponentRaster localByteComponentRaster = (ByteComponentRaster)paramBufferedImage.getRaster();
    BufImgSurfaceData localBufImgSurfaceData = new BufImgSurfaceData(localByteComponentRaster.getDataBuffer(), paramBufferedImage, paramSurfaceType);
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    IndexColorModel localIndexColorModel = (localColorModel instanceof IndexColorModel) ? (IndexColorModel)localColorModel : null;
    localBufImgSurfaceData.initRaster(localByteComponentRaster.getDataStorage(), localByteComponentRaster.getDataOffset(paramInt), 0, localByteComponentRaster.getWidth(), localByteComponentRaster.getHeight(), localByteComponentRaster.getPixelStride(), localByteComponentRaster.getScanlineStride(), localIndexColorModel);
    return localBufImgSurfaceData;
  }
  
  public static SurfaceData createDataBP(BufferedImage paramBufferedImage, SurfaceType paramSurfaceType)
  {
    BytePackedRaster localBytePackedRaster = (BytePackedRaster)paramBufferedImage.getRaster();
    BufImgSurfaceData localBufImgSurfaceData = new BufImgSurfaceData(localBytePackedRaster.getDataBuffer(), paramBufferedImage, paramSurfaceType);
    ColorModel localColorModel = paramBufferedImage.getColorModel();
    IndexColorModel localIndexColorModel = (localColorModel instanceof IndexColorModel) ? (IndexColorModel)localColorModel : null;
    localBufImgSurfaceData.initRaster(localBytePackedRaster.getDataStorage(), localBytePackedRaster.getDataBitOffset() / 8, localBytePackedRaster.getDataBitOffset() & 0x7, localBytePackedRaster.getWidth(), localBytePackedRaster.getHeight(), 0, localBytePackedRaster.getScanlineStride(), localIndexColorModel);
    return localBufImgSurfaceData;
  }
  
  public RenderLoops getRenderLoops(SunGraphics2D paramSunGraphics2D)
  {
    if ((paintState <= 1) && (compositeState <= 0)) {
      return solidloops;
    }
    return super.getRenderLoops(paramSunGraphics2D);
  }
  
  public Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return bufImg.getRaster();
  }
  
  protected native void initRaster(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, IndexColorModel paramIndexColorModel);
  
  public BufImgSurfaceData(DataBuffer paramDataBuffer, BufferedImage paramBufferedImage, SurfaceType paramSurfaceType)
  {
    super(SunWritableRaster.stealTrackable(paramDataBuffer), paramSurfaceType, paramBufferedImage.getColorModel());
    bufImg = paramBufferedImage;
  }
  
  protected BufImgSurfaceData(SurfaceType paramSurfaceType, ColorModel paramColorModel)
  {
    super(paramSurfaceType, paramColorModel);
  }
  
  public void initSolidLoops()
  {
    solidloops = getSolidLoops(getSurfaceType());
  }
  
  public static synchronized RenderLoops getSolidLoops(SurfaceType paramSurfaceType)
  {
    for (int i = 4; i >= 0; i--)
    {
      SurfaceType localSurfaceType = typecache[i];
      if (localSurfaceType == paramSurfaceType) {
        return loopcache[i];
      }
      if (localSurfaceType == null) {
        break;
      }
    }
    RenderLoops localRenderLoops = makeRenderLoops(SurfaceType.OpaqueColor, CompositeType.SrcNoEa, paramSurfaceType);
    System.arraycopy(loopcache, 1, loopcache, 0, 4);
    System.arraycopy(typecache, 1, typecache, 0, 4);
    loopcache[4] = localRenderLoops;
    typecache[4] = paramSurfaceType;
    return localRenderLoops;
  }
  
  public SurfaceData getReplacement()
  {
    return restoreContents(bufImg);
  }
  
  public synchronized GraphicsConfiguration getDeviceConfiguration()
  {
    if (graphicsConfig == null) {
      graphicsConfig = BufferedImageGraphicsConfig.getConfig(bufImg);
    }
    return graphicsConfig;
  }
  
  public Rectangle getBounds()
  {
    return new Rectangle(bufImg.getWidth(), bufImg.getHeight());
  }
  
  protected void checkCustomComposite() {}
  
  private static native void freeNativeICMData(long paramLong);
  
  public Object getDestination()
  {
    return bufImg;
  }
  
  static
  {
    initIDs(IndexColorModel.class, ICMColorData.class);
  }
  
  public static final class ICMColorData
  {
    private long pData = 0L;
    
    private ICMColorData(long paramLong)
    {
      pData = paramLong;
    }
    
    public void finalize()
    {
      if (pData != 0L)
      {
        BufImgSurfaceData.freeNativeICMData(pData);
        pData = 0L;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\BufImgSurfaceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */