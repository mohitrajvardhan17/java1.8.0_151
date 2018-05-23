package com.sun.imageio.plugins.wbmp;

import com.sun.imageio.plugins.common.I18N;
import com.sun.imageio.plugins.common.ReaderUtil;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class WBMPImageReader
  extends ImageReader
{
  private ImageInputStream iis = null;
  private boolean gotHeader = false;
  private int width;
  private int height;
  private int wbmpType;
  private WBMPMetadata metadata;
  
  public WBMPImageReader(ImageReaderSpi paramImageReaderSpi)
  {
    super(paramImageReaderSpi);
  }
  
  public void setInput(Object paramObject, boolean paramBoolean1, boolean paramBoolean2)
  {
    super.setInput(paramObject, paramBoolean1, paramBoolean2);
    iis = ((ImageInputStream)paramObject);
    gotHeader = false;
  }
  
  public int getNumImages(boolean paramBoolean)
    throws IOException
  {
    if (iis == null) {
      throw new IllegalStateException(I18N.getString("GetNumImages0"));
    }
    if ((seekForwardOnly) && (paramBoolean)) {
      throw new IllegalStateException(I18N.getString("GetNumImages1"));
    }
    return 1;
  }
  
  public int getWidth(int paramInt)
    throws IOException
  {
    checkIndex(paramInt);
    readHeader();
    return width;
  }
  
  public int getHeight(int paramInt)
    throws IOException
  {
    checkIndex(paramInt);
    readHeader();
    return height;
  }
  
  public boolean isRandomAccessEasy(int paramInt)
    throws IOException
  {
    checkIndex(paramInt);
    return true;
  }
  
  private void checkIndex(int paramInt)
  {
    if (paramInt != 0) {
      throw new IndexOutOfBoundsException(I18N.getString("WBMPImageReader0"));
    }
  }
  
  public void readHeader()
    throws IOException
  {
    if (gotHeader) {
      return;
    }
    if (iis == null) {
      throw new IllegalStateException("Input source not set!");
    }
    metadata = new WBMPMetadata();
    wbmpType = iis.readByte();
    int i = iis.readByte();
    if ((i != 0) || (!isValidWbmpType(wbmpType))) {
      throw new IIOException(I18N.getString("WBMPImageReader2"));
    }
    metadata.wbmpType = wbmpType;
    width = ReaderUtil.readMultiByteInteger(iis);
    metadata.width = width;
    height = ReaderUtil.readMultiByteInteger(iis);
    metadata.height = height;
    gotHeader = true;
  }
  
  public Iterator getImageTypes(int paramInt)
    throws IOException
  {
    checkIndex(paramInt);
    readHeader();
    BufferedImage localBufferedImage = new BufferedImage(1, 1, 12);
    ArrayList localArrayList = new ArrayList(1);
    localArrayList.add(new ImageTypeSpecifier(localBufferedImage));
    return localArrayList.iterator();
  }
  
  public ImageReadParam getDefaultReadParam()
  {
    return new ImageReadParam();
  }
  
  public IIOMetadata getImageMetadata(int paramInt)
    throws IOException
  {
    checkIndex(paramInt);
    if (metadata == null) {
      readHeader();
    }
    return metadata;
  }
  
  public IIOMetadata getStreamMetadata()
    throws IOException
  {
    return null;
  }
  
  public BufferedImage read(int paramInt, ImageReadParam paramImageReadParam)
    throws IOException
  {
    if (iis == null) {
      throw new IllegalStateException(I18N.getString("WBMPImageReader1"));
    }
    checkIndex(paramInt);
    clearAbortRequest();
    processImageStarted(paramInt);
    if (paramImageReadParam == null) {
      paramImageReadParam = getDefaultReadParam();
    }
    readHeader();
    Rectangle localRectangle1 = new Rectangle(0, 0, 0, 0);
    Rectangle localRectangle2 = new Rectangle(0, 0, 0, 0);
    computeRegions(paramImageReadParam, width, height, paramImageReadParam.getDestination(), localRectangle1, localRectangle2);
    int i = paramImageReadParam.getSourceXSubsampling();
    int j = paramImageReadParam.getSourceYSubsampling();
    int k = paramImageReadParam.getSubsamplingXOffset();
    int m = paramImageReadParam.getSubsamplingYOffset();
    BufferedImage localBufferedImage = paramImageReadParam.getDestination();
    if (localBufferedImage == null) {
      localBufferedImage = new BufferedImage(x + width, y + height, 12);
    }
    int n = (localRectangle2.equals(new Rectangle(0, 0, width, height))) && (localRectangle2.equals(new Rectangle(0, 0, localBufferedImage.getWidth(), localBufferedImage.getHeight()))) ? 1 : 0;
    WritableRaster localWritableRaster = localBufferedImage.getWritableTile(0, 0);
    MultiPixelPackedSampleModel localMultiPixelPackedSampleModel = (MultiPixelPackedSampleModel)localBufferedImage.getSampleModel();
    if (n != 0)
    {
      if (abortRequested())
      {
        processReadAborted();
        return localBufferedImage;
      }
      iis.read(((DataBufferByte)localWritableRaster.getDataBuffer()).getData(), 0, height * localMultiPixelPackedSampleModel.getScanlineStride());
      processImageUpdate(localBufferedImage, 0, 0, width, height, 1, 1, new int[] { 0 });
      processImageProgress(100.0F);
    }
    else
    {
      int i1 = (width + 7) / 8;
      byte[] arrayOfByte1 = new byte[i1];
      byte[] arrayOfByte2 = ((DataBufferByte)localWritableRaster.getDataBuffer()).getData();
      int i2 = localMultiPixelPackedSampleModel.getScanlineStride();
      iis.skipBytes(i1 * y);
      int i3 = i1 * (j - 1);
      int[] arrayOfInt1 = new int[width];
      int[] arrayOfInt2 = new int[width];
      int[] arrayOfInt3 = new int[width];
      int[] arrayOfInt4 = new int[width];
      int i4 = x;
      int i5 = x;
      int i6 = 0;
      while (i4 < x + width)
      {
        arrayOfInt3[i6] = (i5 >> 3);
        arrayOfInt1[i6] = (7 - (i5 & 0x7));
        arrayOfInt4[i6] = (i4 >> 3);
        arrayOfInt2[i6] = (7 - (i4 & 0x7));
        i4++;
        i6++;
        i5 += i;
      }
      i4 = 0;
      i5 = y;
      i6 = y * i2;
      while ((i4 < height) && (!abortRequested()))
      {
        iis.read(arrayOfByte1, 0, i1);
        for (int i7 = 0; i7 < width; i7++)
        {
          int i8 = arrayOfByte1[arrayOfInt3[i7]] >> arrayOfInt1[i7] & 0x1;
          int tmp609_608 = (i6 + arrayOfInt4[i7]);
          byte[] tmp609_599 = arrayOfByte2;
          tmp609_599[tmp609_608] = ((byte)(tmp609_599[tmp609_608] | i8 << arrayOfInt2[i7]));
        }
        i6 += i2;
        iis.skipBytes(i3);
        processImageUpdate(localBufferedImage, 0, i4, width, 1, 1, 1, new int[] { 0 });
        processImageProgress(100.0F * i4 / height);
        i4++;
        i5 += j;
      }
    }
    if (abortRequested()) {
      processReadAborted();
    } else {
      processImageComplete();
    }
    return localBufferedImage;
  }
  
  public boolean canReadRaster()
  {
    return true;
  }
  
  public Raster readRaster(int paramInt, ImageReadParam paramImageReadParam)
    throws IOException
  {
    BufferedImage localBufferedImage = read(paramInt, paramImageReadParam);
    return localBufferedImage.getData();
  }
  
  public void reset()
  {
    super.reset();
    iis = null;
    gotHeader = false;
  }
  
  boolean isValidWbmpType(int paramInt)
  {
    return paramInt == 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\wbmp\WBMPImageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */