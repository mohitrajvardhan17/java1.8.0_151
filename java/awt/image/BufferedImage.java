package java.awt.image;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import sun.awt.image.ByteComponentRaster;
import sun.awt.image.BytePackedRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.awt.image.OffScreenImageSource;
import sun.awt.image.ShortComponentRaster;

public class BufferedImage
  extends Image
  implements WritableRenderedImage, Transparency
{
  private int imageType = 0;
  private ColorModel colorModel;
  private final WritableRaster raster;
  private OffScreenImageSource osis;
  private Hashtable<String, Object> properties;
  public static final int TYPE_CUSTOM = 0;
  public static final int TYPE_INT_RGB = 1;
  public static final int TYPE_INT_ARGB = 2;
  public static final int TYPE_INT_ARGB_PRE = 3;
  public static final int TYPE_INT_BGR = 4;
  public static final int TYPE_3BYTE_BGR = 5;
  public static final int TYPE_4BYTE_ABGR = 6;
  public static final int TYPE_4BYTE_ABGR_PRE = 7;
  public static final int TYPE_USHORT_565_RGB = 8;
  public static final int TYPE_USHORT_555_RGB = 9;
  public static final int TYPE_BYTE_GRAY = 10;
  public static final int TYPE_USHORT_GRAY = 11;
  public static final int TYPE_BYTE_BINARY = 12;
  public static final int TYPE_BYTE_INDEXED = 13;
  private static final int DCM_RED_MASK = 16711680;
  private static final int DCM_GREEN_MASK = 65280;
  private static final int DCM_BLUE_MASK = 255;
  private static final int DCM_ALPHA_MASK = -16777216;
  private static final int DCM_565_RED_MASK = 63488;
  private static final int DCM_565_GRN_MASK = 2016;
  private static final int DCM_565_BLU_MASK = 31;
  private static final int DCM_555_RED_MASK = 31744;
  private static final int DCM_555_GRN_MASK = 992;
  private static final int DCM_555_BLU_MASK = 31;
  private static final int DCM_BGR_RED_MASK = 255;
  private static final int DCM_BGR_GRN_MASK = 65280;
  private static final int DCM_BGR_BLU_MASK = 16711680;
  
  private static native void initIDs();
  
  public BufferedImage(int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject;
    int[] arrayOfInt1;
    int[] arrayOfInt2;
    switch (paramInt3)
    {
    case 1: 
      colorModel = new DirectColorModel(24, 16711680, 65280, 255, 0);
      raster = colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
      break;
    case 2: 
      colorModel = ColorModel.getRGBdefault();
      raster = colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
      break;
    case 3: 
      colorModel = new DirectColorModel(ColorSpace.getInstance(1000), 32, 16711680, 65280, 255, -16777216, true, 3);
      raster = colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
      break;
    case 4: 
      colorModel = new DirectColorModel(24, 255, 65280, 16711680);
      raster = colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
      break;
    case 5: 
      localObject = ColorSpace.getInstance(1000);
      arrayOfInt1 = new int[] { 8, 8, 8 };
      arrayOfInt2 = new int[] { 2, 1, 0 };
      colorModel = new ComponentColorModel((ColorSpace)localObject, arrayOfInt1, false, false, 1, 0);
      raster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, paramInt1 * 3, 3, arrayOfInt2, null);
      break;
    case 6: 
      localObject = ColorSpace.getInstance(1000);
      arrayOfInt1 = new int[] { 8, 8, 8, 8 };
      arrayOfInt2 = new int[] { 3, 2, 1, 0 };
      colorModel = new ComponentColorModel((ColorSpace)localObject, arrayOfInt1, true, false, 3, 0);
      raster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, paramInt1 * 4, 4, arrayOfInt2, null);
      break;
    case 7: 
      localObject = ColorSpace.getInstance(1000);
      arrayOfInt1 = new int[] { 8, 8, 8, 8 };
      arrayOfInt2 = new int[] { 3, 2, 1, 0 };
      colorModel = new ComponentColorModel((ColorSpace)localObject, arrayOfInt1, true, true, 3, 0);
      raster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, paramInt1 * 4, 4, arrayOfInt2, null);
      break;
    case 10: 
      localObject = ColorSpace.getInstance(1003);
      arrayOfInt1 = new int[] { 8 };
      colorModel = new ComponentColorModel((ColorSpace)localObject, arrayOfInt1, false, true, 1, 0);
      raster = colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
      break;
    case 11: 
      localObject = ColorSpace.getInstance(1003);
      arrayOfInt1 = new int[] { 16 };
      colorModel = new ComponentColorModel((ColorSpace)localObject, arrayOfInt1, false, true, 1, 1);
      raster = colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
      break;
    case 12: 
      localObject = new byte[] { 0, -1 };
      colorModel = new IndexColorModel(1, 2, (byte[])localObject, (byte[])localObject, (byte[])localObject);
      raster = Raster.createPackedRaster(0, paramInt1, paramInt2, 1, 1, null);
      break;
    case 13: 
      localObject = new int['Ā'];
      int i = 0;
      for (int j = 0; j < 256; j += 51) {
        for (k = 0; k < 256; k += 51) {
          for (int m = 0; m < 256; m += 51) {
            localObject[(i++)] = (j << 16 | k << 8 | m);
          }
        }
      }
      j = 256 / (256 - i);
      int k = j * 3;
      while (i < 256)
      {
        localObject[i] = (k << 16 | k << 8 | k);
        k += j;
        i++;
      }
      colorModel = new IndexColorModel(8, 256, (int[])localObject, 0, false, -1, 0);
      raster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, 1, null);
      break;
    case 8: 
      colorModel = new DirectColorModel(16, 63488, 2016, 31);
      raster = colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
      break;
    case 9: 
      colorModel = new DirectColorModel(15, 31744, 992, 31);
      raster = colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
      break;
    default: 
      throw new IllegalArgumentException("Unknown image type " + paramInt3);
    }
    imageType = paramInt3;
  }
  
  public BufferedImage(int paramInt1, int paramInt2, int paramInt3, IndexColorModel paramIndexColorModel)
  {
    if ((paramIndexColorModel.hasAlpha()) && (paramIndexColorModel.isAlphaPremultiplied())) {
      throw new IllegalArgumentException("This image types do not have premultiplied alpha.");
    }
    switch (paramInt3)
    {
    case 12: 
      int j = paramIndexColorModel.getMapSize();
      int i;
      if (j <= 2) {
        i = 1;
      } else if (j <= 4) {
        i = 2;
      } else if (j <= 16) {
        i = 4;
      } else {
        throw new IllegalArgumentException("Color map for TYPE_BYTE_BINARY must have no more than 16 entries");
      }
      raster = Raster.createPackedRaster(0, paramInt1, paramInt2, 1, i, null);
      break;
    case 13: 
      raster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, 1, null);
      break;
    default: 
      throw new IllegalArgumentException("Invalid image type (" + paramInt3 + ").  Image type must be either TYPE_BYTE_BINARY or  TYPE_BYTE_INDEXED");
    }
    if (!paramIndexColorModel.isCompatibleRaster(raster)) {
      throw new IllegalArgumentException("Incompatible image type and IndexColorModel");
    }
    colorModel = paramIndexColorModel;
    imageType = paramInt3;
  }
  
  public BufferedImage(ColorModel paramColorModel, WritableRaster paramWritableRaster, boolean paramBoolean, Hashtable<?, ?> paramHashtable)
  {
    if (!paramColorModel.isCompatibleRaster(paramWritableRaster)) {
      throw new IllegalArgumentException("Raster " + paramWritableRaster + " is incompatible with ColorModel " + paramColorModel);
    }
    if ((minX != 0) || (minY != 0)) {
      throw new IllegalArgumentException("Raster " + paramWritableRaster + " has minX or minY not equal to zero: " + minX + " " + minY);
    }
    colorModel = paramColorModel;
    raster = paramWritableRaster;
    if ((paramHashtable != null) && (!paramHashtable.isEmpty()))
    {
      properties = new Hashtable();
      Iterator localIterator = paramHashtable.keySet().iterator();
      while (localIterator.hasNext())
      {
        Object localObject1 = localIterator.next();
        if ((localObject1 instanceof String)) {
          properties.put((String)localObject1, paramHashtable.get(localObject1));
        }
      }
    }
    int i = paramWritableRaster.getNumBands();
    boolean bool1 = paramColorModel.isAlphaPremultiplied();
    boolean bool2 = isStandard(paramColorModel, paramWritableRaster);
    coerceData(paramBoolean);
    SampleModel localSampleModel = paramWritableRaster.getSampleModel();
    ColorSpace localColorSpace = paramColorModel.getColorSpace();
    int j = localColorSpace.getType();
    if (j != 5)
    {
      if ((j == 6) && (bool2) && ((paramColorModel instanceof ComponentColorModel)))
      {
        if (((localSampleModel instanceof ComponentSampleModel)) && (((ComponentSampleModel)localSampleModel).getPixelStride() != i)) {
          imageType = 0;
        } else if (((paramWritableRaster instanceof ByteComponentRaster)) && (paramWritableRaster.getNumBands() == 1) && (paramColorModel.getComponentSize(0) == 8) && (((ByteComponentRaster)paramWritableRaster).getPixelStride() == 1)) {
          imageType = 10;
        } else if (((paramWritableRaster instanceof ShortComponentRaster)) && (paramWritableRaster.getNumBands() == 1) && (paramColorModel.getComponentSize(0) == 16) && (((ShortComponentRaster)paramWritableRaster).getPixelStride() == 1)) {
          imageType = 11;
        }
      }
      else {
        imageType = 0;
      }
      return;
    }
    Object localObject2;
    int k;
    Object localObject3;
    int i1;
    if (((paramWritableRaster instanceof IntegerComponentRaster)) && ((i == 3) || (i == 4)))
    {
      localObject2 = (IntegerComponentRaster)paramWritableRaster;
      k = paramColorModel.getPixelSize();
      if ((((IntegerComponentRaster)localObject2).getPixelStride() == 1) && (bool2) && ((paramColorModel instanceof DirectColorModel)) && ((k == 32) || (k == 24)))
      {
        localObject3 = (DirectColorModel)paramColorModel;
        int m = ((DirectColorModel)localObject3).getRedMask();
        int n = ((DirectColorModel)localObject3).getGreenMask();
        i1 = ((DirectColorModel)localObject3).getBlueMask();
        if ((m == 16711680) && (n == 65280) && (i1 == 255))
        {
          if (((DirectColorModel)localObject3).getAlphaMask() == -16777216) {
            imageType = (bool1 ? 3 : 2);
          } else if (!((DirectColorModel)localObject3).hasAlpha()) {
            imageType = 1;
          }
        }
        else if ((m == 255) && (n == 65280) && (i1 == 16711680) && (!((DirectColorModel)localObject3).hasAlpha())) {
          imageType = 4;
        }
      }
    }
    else if (((paramColorModel instanceof IndexColorModel)) && (i == 1) && (bool2) && ((!paramColorModel.hasAlpha()) || (!bool1)))
    {
      localObject2 = (IndexColorModel)paramColorModel;
      k = ((IndexColorModel)localObject2).getPixelSize();
      if ((paramWritableRaster instanceof BytePackedRaster))
      {
        imageType = 12;
      }
      else if ((paramWritableRaster instanceof ByteComponentRaster))
      {
        localObject3 = (ByteComponentRaster)paramWritableRaster;
        if ((((ByteComponentRaster)localObject3).getPixelStride() == 1) && (k <= 8)) {
          imageType = 13;
        }
      }
    }
    else if (((paramWritableRaster instanceof ShortComponentRaster)) && ((paramColorModel instanceof DirectColorModel)) && (bool2) && (i == 3) && (!paramColorModel.hasAlpha()))
    {
      localObject2 = (DirectColorModel)paramColorModel;
      if (((DirectColorModel)localObject2).getRedMask() == 63488)
      {
        if ((((DirectColorModel)localObject2).getGreenMask() == 2016) && (((DirectColorModel)localObject2).getBlueMask() == 31)) {
          imageType = 8;
        }
      }
      else if ((((DirectColorModel)localObject2).getRedMask() == 31744) && (((DirectColorModel)localObject2).getGreenMask() == 992) && (((DirectColorModel)localObject2).getBlueMask() == 31)) {
        imageType = 9;
      }
    }
    else if (((paramWritableRaster instanceof ByteComponentRaster)) && ((paramColorModel instanceof ComponentColorModel)) && (bool2) && ((paramWritableRaster.getSampleModel() instanceof PixelInterleavedSampleModel)) && ((i == 3) || (i == 4)))
    {
      localObject2 = (ComponentColorModel)paramColorModel;
      PixelInterleavedSampleModel localPixelInterleavedSampleModel = (PixelInterleavedSampleModel)paramWritableRaster.getSampleModel();
      localObject3 = (ByteComponentRaster)paramWritableRaster;
      int[] arrayOfInt1 = localPixelInterleavedSampleModel.getBandOffsets();
      if (((ComponentColorModel)localObject2).getNumComponents() != i) {
        throw new RasterFormatException("Number of components in ColorModel (" + ((ComponentColorModel)localObject2).getNumComponents() + ") does not match # in  Raster (" + i + ")");
      }
      int[] arrayOfInt2 = ((ComponentColorModel)localObject2).getComponentSize();
      i1 = 1;
      for (int i2 = 0; i2 < i; i2++) {
        if (arrayOfInt2[i2] != 8)
        {
          i1 = 0;
          break;
        }
      }
      if ((i1 != 0) && (((ByteComponentRaster)localObject3).getPixelStride() == i) && (arrayOfInt1[0] == i - 1) && (arrayOfInt1[1] == i - 2) && (arrayOfInt1[2] == i - 3)) {
        if ((i == 3) && (!((ComponentColorModel)localObject2).hasAlpha())) {
          imageType = 5;
        } else if ((arrayOfInt1[3] == 0) && (((ComponentColorModel)localObject2).hasAlpha())) {
          imageType = (bool1 ? 7 : 6);
        }
      }
    }
  }
  
  private static boolean isStandard(ColorModel paramColorModel, WritableRaster paramWritableRaster)
  {
    Class localClass1 = paramColorModel.getClass();
    final Class localClass2 = paramWritableRaster.getClass();
    final Class localClass3 = paramWritableRaster.getSampleModel().getClass();
    PrivilegedAction local1 = new PrivilegedAction()
    {
      public Boolean run()
      {
        ClassLoader localClassLoader = System.class.getClassLoader();
        return Boolean.valueOf((val$cmClass.getClassLoader() == localClassLoader) && (localClass3.getClassLoader() == localClassLoader) && (localClass2.getClassLoader() == localClassLoader));
      }
    };
    return ((Boolean)AccessController.doPrivileged(local1)).booleanValue();
  }
  
  public int getType()
  {
    return imageType;
  }
  
  public ColorModel getColorModel()
  {
    return colorModel;
  }
  
  public WritableRaster getRaster()
  {
    return raster;
  }
  
  public WritableRaster getAlphaRaster()
  {
    return colorModel.getAlphaRaster(raster);
  }
  
  public int getRGB(int paramInt1, int paramInt2)
  {
    return colorModel.getRGB(raster.getDataElements(paramInt1, paramInt2, null));
  }
  
  public int[] getRGB(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    int i = paramInt5;
    int k = raster.getNumBands();
    int m = raster.getDataBuffer().getDataType();
    Object localObject;
    switch (m)
    {
    case 0: 
      localObject = new byte[k];
      break;
    case 1: 
      localObject = new short[k];
      break;
    case 3: 
      localObject = new int[k];
      break;
    case 4: 
      localObject = new float[k];
      break;
    case 5: 
      localObject = new double[k];
      break;
    case 2: 
    default: 
      throw new IllegalArgumentException("Unknown data buffer type: " + m);
    }
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[paramInt5 + paramInt4 * paramInt6];
    }
    int n = paramInt2;
    while (n < paramInt2 + paramInt4)
    {
      int j = i;
      for (int i1 = paramInt1; i1 < paramInt1 + paramInt3; i1++) {
        paramArrayOfInt[(j++)] = colorModel.getRGB(raster.getDataElements(i1, n, localObject));
      }
      n++;
      i += paramInt6;
    }
    return paramArrayOfInt;
  }
  
  public synchronized void setRGB(int paramInt1, int paramInt2, int paramInt3)
  {
    raster.setDataElements(paramInt1, paramInt2, colorModel.getDataElements(paramInt3, null));
  }
  
  public void setRGB(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    int i = paramInt5;
    Object localObject = null;
    int k = paramInt2;
    while (k < paramInt2 + paramInt4)
    {
      int j = i;
      for (int m = paramInt1; m < paramInt1 + paramInt3; m++)
      {
        localObject = colorModel.getDataElements(paramArrayOfInt[(j++)], localObject);
        raster.setDataElements(m, k, localObject);
      }
      k++;
      i += paramInt6;
    }
  }
  
  public int getWidth()
  {
    return raster.getWidth();
  }
  
  public int getHeight()
  {
    return raster.getHeight();
  }
  
  public int getWidth(ImageObserver paramImageObserver)
  {
    return raster.getWidth();
  }
  
  public int getHeight(ImageObserver paramImageObserver)
  {
    return raster.getHeight();
  }
  
  public ImageProducer getSource()
  {
    if (osis == null)
    {
      if (properties == null) {
        properties = new Hashtable();
      }
      osis = new OffScreenImageSource(this, properties);
    }
    return osis;
  }
  
  public Object getProperty(String paramString, ImageObserver paramImageObserver)
  {
    return getProperty(paramString);
  }
  
  public Object getProperty(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("null property name is not allowed");
    }
    if (properties == null) {
      return Image.UndefinedProperty;
    }
    Object localObject = properties.get(paramString);
    if (localObject == null) {
      localObject = Image.UndefinedProperty;
    }
    return localObject;
  }
  
  public Graphics getGraphics()
  {
    return createGraphics();
  }
  
  public Graphics2D createGraphics()
  {
    GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    return localGraphicsEnvironment.createGraphics(this);
  }
  
  public BufferedImage getSubimage(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return new BufferedImage(colorModel, raster.createWritableChild(paramInt1, paramInt2, paramInt3, paramInt4, 0, 0, null), colorModel.isAlphaPremultiplied(), properties);
  }
  
  public boolean isAlphaPremultiplied()
  {
    return colorModel.isAlphaPremultiplied();
  }
  
  public void coerceData(boolean paramBoolean)
  {
    if ((colorModel.hasAlpha()) && (colorModel.isAlphaPremultiplied() != paramBoolean)) {
      colorModel = colorModel.coerceData(raster, paramBoolean);
    }
  }
  
  public String toString()
  {
    return "BufferedImage@" + Integer.toHexString(hashCode()) + ": type = " + imageType + " " + colorModel + " " + raster;
  }
  
  public Vector<RenderedImage> getSources()
  {
    return null;
  }
  
  public String[] getPropertyNames()
  {
    if ((properties == null) || (properties.isEmpty())) {
      return null;
    }
    Set localSet = properties.keySet();
    return (String[])localSet.toArray(new String[localSet.size()]);
  }
  
  public int getMinX()
  {
    return raster.getMinX();
  }
  
  public int getMinY()
  {
    return raster.getMinY();
  }
  
  public SampleModel getSampleModel()
  {
    return raster.getSampleModel();
  }
  
  public int getNumXTiles()
  {
    return 1;
  }
  
  public int getNumYTiles()
  {
    return 1;
  }
  
  public int getMinTileX()
  {
    return 0;
  }
  
  public int getMinTileY()
  {
    return 0;
  }
  
  public int getTileWidth()
  {
    return raster.getWidth();
  }
  
  public int getTileHeight()
  {
    return raster.getHeight();
  }
  
  public int getTileGridXOffset()
  {
    return raster.getSampleModelTranslateX();
  }
  
  public int getTileGridYOffset()
  {
    return raster.getSampleModelTranslateY();
  }
  
  public Raster getTile(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) && (paramInt2 == 0)) {
      return raster;
    }
    throw new ArrayIndexOutOfBoundsException("BufferedImages only have one tile with index 0,0");
  }
  
  public Raster getData()
  {
    int i = raster.getWidth();
    int j = raster.getHeight();
    int k = raster.getMinX();
    int m = raster.getMinY();
    WritableRaster localWritableRaster = Raster.createWritableRaster(raster.getSampleModel(), new Point(raster.getSampleModelTranslateX(), raster.getSampleModelTranslateY()));
    Object localObject = null;
    for (int n = m; n < m + j; n++)
    {
      localObject = raster.getDataElements(k, n, i, 1, localObject);
      localWritableRaster.setDataElements(k, n, i, 1, localObject);
    }
    return localWritableRaster;
  }
  
  public Raster getData(Rectangle paramRectangle)
  {
    SampleModel localSampleModel1 = raster.getSampleModel();
    SampleModel localSampleModel2 = localSampleModel1.createCompatibleSampleModel(width, height);
    WritableRaster localWritableRaster = Raster.createWritableRaster(localSampleModel2, paramRectangle.getLocation());
    int i = width;
    int j = height;
    int k = x;
    int m = y;
    Object localObject = null;
    for (int n = m; n < m + j; n++)
    {
      localObject = raster.getDataElements(k, n, i, 1, localObject);
      localWritableRaster.setDataElements(k, n, i, 1, localObject);
    }
    return localWritableRaster;
  }
  
  public WritableRaster copyData(WritableRaster paramWritableRaster)
  {
    if (paramWritableRaster == null) {
      return (WritableRaster)getData();
    }
    int i = paramWritableRaster.getWidth();
    int j = paramWritableRaster.getHeight();
    int k = paramWritableRaster.getMinX();
    int m = paramWritableRaster.getMinY();
    Object localObject = null;
    for (int n = m; n < m + j; n++)
    {
      localObject = raster.getDataElements(k, n, i, 1, localObject);
      paramWritableRaster.setDataElements(k, n, i, 1, localObject);
    }
    return paramWritableRaster;
  }
  
  public void setData(Raster paramRaster)
  {
    int i = paramRaster.getWidth();
    int j = paramRaster.getHeight();
    int k = paramRaster.getMinX();
    int m = paramRaster.getMinY();
    int[] arrayOfInt = null;
    Rectangle localRectangle1 = new Rectangle(k, m, i, j);
    Rectangle localRectangle2 = new Rectangle(0, 0, raster.width, raster.height);
    Rectangle localRectangle3 = localRectangle1.intersection(localRectangle2);
    if (localRectangle3.isEmpty()) {
      return;
    }
    i = width;
    j = height;
    k = x;
    m = y;
    for (int n = m; n < m + j; n++)
    {
      arrayOfInt = paramRaster.getPixels(k, n, i, 1, arrayOfInt);
      raster.setPixels(k, n, i, 1, arrayOfInt);
    }
  }
  
  public void addTileObserver(TileObserver paramTileObserver) {}
  
  public void removeTileObserver(TileObserver paramTileObserver) {}
  
  public boolean isTileWritable(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) && (paramInt2 == 0)) {
      return true;
    }
    throw new IllegalArgumentException("Only 1 tile in image");
  }
  
  public Point[] getWritableTileIndices()
  {
    Point[] arrayOfPoint = new Point[1];
    arrayOfPoint[0] = new Point(0, 0);
    return arrayOfPoint;
  }
  
  public boolean hasTileWriters()
  {
    return true;
  }
  
  public WritableRaster getWritableTile(int paramInt1, int paramInt2)
  {
    return raster;
  }
  
  public void releaseWritableTile(int paramInt1, int paramInt2) {}
  
  public int getTransparency()
  {
    return colorModel.getTransparency();
  }
  
  static
  {
    ColorModel.loadLibraries();
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\BufferedImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */