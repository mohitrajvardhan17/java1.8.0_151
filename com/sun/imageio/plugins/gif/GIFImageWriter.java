package com.sun.imageio.plugins.gif;

import com.sun.imageio.plugins.common.LZWCompressor;
import com.sun.imageio.plugins.common.PaletteBuilder;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.awt.image.ByteComponentRaster;

public class GIFImageWriter
  extends ImageWriter
{
  private static final boolean DEBUG = false;
  static final String STANDARD_METADATA_NAME = "javax_imageio_1.0";
  static final String STREAM_METADATA_NAME = "javax_imageio_gif_stream_1.0";
  static final String IMAGE_METADATA_NAME = "javax_imageio_gif_image_1.0";
  private ImageOutputStream stream = null;
  private boolean isWritingSequence = false;
  private boolean wroteSequenceHeader = false;
  private GIFWritableStreamMetadata theStreamMetadata = null;
  private int imageIndex = 0;
  
  private static int getNumBits(int paramInt)
    throws IOException
  {
    int i;
    switch (paramInt)
    {
    case 2: 
      i = 1;
      break;
    case 4: 
      i = 2;
      break;
    case 8: 
      i = 3;
      break;
    case 16: 
      i = 4;
      break;
    case 32: 
      i = 5;
      break;
    case 64: 
      i = 6;
      break;
    case 128: 
      i = 7;
      break;
    case 256: 
      i = 8;
      break;
    default: 
      throw new IOException("Bad palette length: " + paramInt + "!");
    }
    return i;
  }
  
  private static void computeRegions(Rectangle paramRectangle, Dimension paramDimension, ImageWriteParam paramImageWriteParam)
  {
    int i = 1;
    int j = 1;
    if (paramImageWriteParam != null)
    {
      int[] arrayOfInt = paramImageWriteParam.getSourceBands();
      if ((arrayOfInt != null) && ((arrayOfInt.length != 1) || (arrayOfInt[0] != 0))) {
        throw new IllegalArgumentException("Cannot sub-band image!");
      }
      Rectangle localRectangle = paramImageWriteParam.getSourceRegion();
      if (localRectangle != null)
      {
        localRectangle = localRectangle.intersection(paramRectangle);
        paramRectangle.setBounds(localRectangle);
      }
      int k = paramImageWriteParam.getSubsamplingXOffset();
      int m = paramImageWriteParam.getSubsamplingYOffset();
      x += k;
      y += m;
      width -= k;
      height -= m;
      i = paramImageWriteParam.getSourceXSubsampling();
      j = paramImageWriteParam.getSourceYSubsampling();
    }
    paramDimension.setSize((width + i - 1) / i, (height + j - 1) / j);
    if ((width <= 0) || (height <= 0)) {
      throw new IllegalArgumentException("Empty source region!");
    }
  }
  
  private static byte[] createColorTable(ColorModel paramColorModel, SampleModel paramSampleModel)
  {
    int j;
    int k;
    byte[] arrayOfByte1;
    if ((paramColorModel instanceof IndexColorModel))
    {
      IndexColorModel localIndexColorModel = (IndexColorModel)paramColorModel;
      j = localIndexColorModel.getMapSize();
      k = getGifPaletteSize(j);
      byte[] arrayOfByte2 = new byte[k];
      byte[] arrayOfByte3 = new byte[k];
      byte[] arrayOfByte4 = new byte[k];
      localIndexColorModel.getReds(arrayOfByte2);
      localIndexColorModel.getGreens(arrayOfByte3);
      localIndexColorModel.getBlues(arrayOfByte4);
      for (int m = j; m < k; m++)
      {
        arrayOfByte2[m] = arrayOfByte2[0];
        arrayOfByte3[m] = arrayOfByte3[0];
        arrayOfByte4[m] = arrayOfByte4[0];
      }
      arrayOfByte1 = new byte[3 * k];
      m = 0;
      for (int n = 0; n < k; n++)
      {
        arrayOfByte1[(m++)] = arrayOfByte2[n];
        arrayOfByte1[(m++)] = arrayOfByte3[n];
        arrayOfByte1[(m++)] = arrayOfByte4[n];
      }
    }
    else if (paramSampleModel.getNumBands() == 1)
    {
      int i = paramSampleModel.getSampleSize()[0];
      if (i > 8) {
        i = 8;
      }
      j = 3 * (1 << i);
      arrayOfByte1 = new byte[j];
      for (k = 0; k < j; k++) {
        arrayOfByte1[k] = ((byte)(k / 3));
      }
    }
    else
    {
      arrayOfByte1 = null;
    }
    return arrayOfByte1;
  }
  
  private static int getGifPaletteSize(int paramInt)
  {
    if (paramInt <= 2) {
      return 2;
    }
    paramInt -= 1;
    paramInt |= paramInt >> 1;
    paramInt |= paramInt >> 2;
    paramInt |= paramInt >> 4;
    paramInt |= paramInt >> 8;
    paramInt |= paramInt >> 16;
    return paramInt + 1;
  }
  
  public GIFImageWriter(GIFImageWriterSpi paramGIFImageWriterSpi)
  {
    super(paramGIFImageWriterSpi);
  }
  
  public boolean canWriteSequence()
  {
    return true;
  }
  
  private void convertMetadata(String paramString, IIOMetadata paramIIOMetadata1, IIOMetadata paramIIOMetadata2)
  {
    String str1 = null;
    String str2 = paramIIOMetadata1.getNativeMetadataFormatName();
    Object localObject;
    if ((str2 != null) && (str2.equals(paramString)))
    {
      str1 = paramString;
    }
    else
    {
      localObject = paramIIOMetadata1.getExtraMetadataFormatNames();
      if (localObject != null) {
        for (int i = 0; i < localObject.length; i++) {
          if (localObject[i].equals(paramString))
          {
            str1 = paramString;
            break;
          }
        }
      }
    }
    if ((str1 == null) && (paramIIOMetadata1.isStandardMetadataFormatSupported())) {
      str1 = "javax_imageio_1.0";
    }
    if (str1 != null) {
      try
      {
        localObject = paramIIOMetadata1.getAsTree(str1);
        paramIIOMetadata2.mergeTree(str1, (Node)localObject);
      }
      catch (IIOInvalidTreeException localIIOInvalidTreeException) {}
    }
  }
  
  public IIOMetadata convertStreamMetadata(IIOMetadata paramIIOMetadata, ImageWriteParam paramImageWriteParam)
  {
    if (paramIIOMetadata == null) {
      throw new IllegalArgumentException("inData == null!");
    }
    IIOMetadata localIIOMetadata = getDefaultStreamMetadata(paramImageWriteParam);
    convertMetadata("javax_imageio_gif_stream_1.0", paramIIOMetadata, localIIOMetadata);
    return localIIOMetadata;
  }
  
  public IIOMetadata convertImageMetadata(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam)
  {
    if (paramIIOMetadata == null) {
      throw new IllegalArgumentException("inData == null!");
    }
    if (paramImageTypeSpecifier == null) {
      throw new IllegalArgumentException("imageType == null!");
    }
    GIFWritableImageMetadata localGIFWritableImageMetadata = (GIFWritableImageMetadata)getDefaultImageMetadata(paramImageTypeSpecifier, paramImageWriteParam);
    boolean bool = interlaceFlag;
    convertMetadata("javax_imageio_gif_image_1.0", paramIIOMetadata, localGIFWritableImageMetadata);
    if ((paramImageWriteParam != null) && (paramImageWriteParam.canWriteProgressive()) && (paramImageWriteParam.getProgressiveMode() != 3)) {
      interlaceFlag = bool;
    }
    return localGIFWritableImageMetadata;
  }
  
  public void endWriteSequence()
    throws IOException
  {
    if (stream == null) {
      throw new IllegalStateException("output == null!");
    }
    if (!isWritingSequence) {
      throw new IllegalStateException("prepareWriteSequence() was not invoked!");
    }
    writeTrailer();
    resetLocal();
  }
  
  public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam)
  {
    GIFWritableImageMetadata localGIFWritableImageMetadata = new GIFWritableImageMetadata();
    SampleModel localSampleModel = paramImageTypeSpecifier.getSampleModel();
    Rectangle localRectangle = new Rectangle(localSampleModel.getWidth(), localSampleModel.getHeight());
    Dimension localDimension = new Dimension();
    computeRegions(localRectangle, localDimension, paramImageWriteParam);
    imageWidth = width;
    imageHeight = height;
    if ((paramImageWriteParam != null) && (paramImageWriteParam.canWriteProgressive()) && (paramImageWriteParam.getProgressiveMode() == 0)) {
      interlaceFlag = false;
    } else {
      interlaceFlag = true;
    }
    ColorModel localColorModel = paramImageTypeSpecifier.getColorModel();
    localColorTable = createColorTable(localColorModel, localSampleModel);
    if ((localColorModel instanceof IndexColorModel))
    {
      int i = ((IndexColorModel)localColorModel).getTransparentPixel();
      if (i != -1)
      {
        transparentColorFlag = true;
        transparentColorIndex = i;
      }
    }
    return localGIFWritableImageMetadata;
  }
  
  public IIOMetadata getDefaultStreamMetadata(ImageWriteParam paramImageWriteParam)
  {
    GIFWritableStreamMetadata localGIFWritableStreamMetadata = new GIFWritableStreamMetadata();
    version = "89a";
    return localGIFWritableStreamMetadata;
  }
  
  public ImageWriteParam getDefaultWriteParam()
  {
    return new GIFImageWriteParam(getLocale());
  }
  
  public void prepareWriteSequence(IIOMetadata paramIIOMetadata)
    throws IOException
  {
    if (stream == null) {
      throw new IllegalStateException("Output is not set.");
    }
    resetLocal();
    if (paramIIOMetadata == null)
    {
      theStreamMetadata = ((GIFWritableStreamMetadata)getDefaultStreamMetadata(null));
    }
    else
    {
      theStreamMetadata = new GIFWritableStreamMetadata();
      convertMetadata("javax_imageio_gif_stream_1.0", paramIIOMetadata, theStreamMetadata);
    }
    isWritingSequence = true;
  }
  
  public void reset()
  {
    super.reset();
    resetLocal();
  }
  
  private void resetLocal()
  {
    isWritingSequence = false;
    wroteSequenceHeader = false;
    theStreamMetadata = null;
    imageIndex = 0;
  }
  
  public void setOutput(Object paramObject)
  {
    super.setOutput(paramObject);
    if (paramObject != null)
    {
      if (!(paramObject instanceof ImageOutputStream)) {
        throw new IllegalArgumentException("output is not an ImageOutputStream");
      }
      stream = ((ImageOutputStream)paramObject);
      stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
    }
    else
    {
      stream = null;
    }
  }
  
  public void write(IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    if (stream == null) {
      throw new IllegalStateException("output == null!");
    }
    if (paramIIOImage == null) {
      throw new IllegalArgumentException("iioimage == null!");
    }
    if (paramIIOImage.hasRaster()) {
      throw new UnsupportedOperationException("canWriteRasters() == false!");
    }
    resetLocal();
    GIFWritableStreamMetadata localGIFWritableStreamMetadata;
    if (paramIIOMetadata == null) {
      localGIFWritableStreamMetadata = (GIFWritableStreamMetadata)getDefaultStreamMetadata(paramImageWriteParam);
    } else {
      localGIFWritableStreamMetadata = (GIFWritableStreamMetadata)convertStreamMetadata(paramIIOMetadata, paramImageWriteParam);
    }
    write(true, true, localGIFWritableStreamMetadata, paramIIOImage, paramImageWriteParam);
  }
  
  public void writeToSequence(IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    if (stream == null) {
      throw new IllegalStateException("output == null!");
    }
    if (paramIIOImage == null) {
      throw new IllegalArgumentException("image == null!");
    }
    if (paramIIOImage.hasRaster()) {
      throw new UnsupportedOperationException("canWriteRasters() == false!");
    }
    if (!isWritingSequence) {
      throw new IllegalStateException("prepareWriteSequence() was not invoked!");
    }
    write(!wroteSequenceHeader, false, theStreamMetadata, paramIIOImage, paramImageWriteParam);
    if (!wroteSequenceHeader) {
      wroteSequenceHeader = true;
    }
    imageIndex += 1;
  }
  
  private boolean needToCreateIndex(RenderedImage paramRenderedImage)
  {
    SampleModel localSampleModel = paramRenderedImage.getSampleModel();
    ColorModel localColorModel = paramRenderedImage.getColorModel();
    return (localSampleModel.getNumBands() != 1) || (localSampleModel.getSampleSize()[0] > 8) || (localColorModel.getComponentSize()[0] > 8);
  }
  
  private void write(boolean paramBoolean1, boolean paramBoolean2, IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam)
    throws IOException
  {
    clearAbortRequest();
    RenderedImage localRenderedImage = paramIIOImage.getRenderedImage();
    if (needToCreateIndex(localRenderedImage))
    {
      localRenderedImage = PaletteBuilder.createIndexedImage(localRenderedImage);
      paramIIOImage.setRenderedImage(localRenderedImage);
    }
    ColorModel localColorModel = localRenderedImage.getColorModel();
    SampleModel localSampleModel = localRenderedImage.getSampleModel();
    Rectangle localRectangle = new Rectangle(localRenderedImage.getMinX(), localRenderedImage.getMinY(), localRenderedImage.getWidth(), localRenderedImage.getHeight());
    Dimension localDimension = new Dimension();
    computeRegions(localRectangle, localDimension, paramImageWriteParam);
    GIFWritableImageMetadata localGIFWritableImageMetadata = null;
    if (paramIIOImage.getMetadata() != null)
    {
      localGIFWritableImageMetadata = new GIFWritableImageMetadata();
      convertMetadata("javax_imageio_gif_image_1.0", paramIIOImage.getMetadata(), localGIFWritableImageMetadata);
      if (localColorTable == null)
      {
        localColorTable = createColorTable(localColorModel, localSampleModel);
        if ((localColorModel instanceof IndexColorModel))
        {
          localObject = (IndexColorModel)localColorModel;
          int i = ((IndexColorModel)localObject).getTransparentPixel();
          transparentColorFlag = (i != -1);
          if (transparentColorFlag) {
            transparentColorIndex = i;
          }
        }
      }
    }
    Object localObject = null;
    if (paramBoolean1)
    {
      if (paramIIOMetadata == null) {
        throw new IllegalArgumentException("Cannot write null header!");
      }
      GIFWritableStreamMetadata localGIFWritableStreamMetadata = (GIFWritableStreamMetadata)paramIIOMetadata;
      if (version == null) {
        version = "89a";
      }
      if (logicalScreenWidth == -1) {
        logicalScreenWidth = width;
      }
      if (logicalScreenHeight == -1) {
        logicalScreenHeight = height;
      }
      if (colorResolution == -1) {
        colorResolution = (localColorModel != null ? localColorModel.getComponentSize()[0] : localSampleModel.getSampleSize()[0]);
      }
      if (globalColorTable == null) {
        if ((isWritingSequence) && (localGIFWritableImageMetadata != null) && (localColorTable != null)) {
          globalColorTable = localColorTable;
        } else if ((localGIFWritableImageMetadata == null) || (localColorTable == null)) {
          globalColorTable = createColorTable(localColorModel, localSampleModel);
        }
      }
      localObject = globalColorTable;
      int j;
      if (localObject != null) {
        j = getNumBits(localObject.length / 3);
      } else if ((localGIFWritableImageMetadata != null) && (localColorTable != null)) {
        j = getNumBits(localColorTable.length / 3);
      } else {
        j = localSampleModel.getSampleSize(0);
      }
      writeHeader(localGIFWritableStreamMetadata, j);
    }
    else if (isWritingSequence)
    {
      localObject = theStreamMetadata.globalColorTable;
    }
    else
    {
      throw new IllegalArgumentException("Must write header for single image!");
    }
    writeImage(paramIIOImage.getRenderedImage(), localGIFWritableImageMetadata, paramImageWriteParam, (byte[])localObject, localRectangle, localDimension);
    if (paramBoolean2) {
      writeTrailer();
    }
  }
  
  private void writeImage(RenderedImage paramRenderedImage, GIFWritableImageMetadata paramGIFWritableImageMetadata, ImageWriteParam paramImageWriteParam, byte[] paramArrayOfByte, Rectangle paramRectangle, Dimension paramDimension)
    throws IOException
  {
    ColorModel localColorModel = paramRenderedImage.getColorModel();
    SampleModel localSampleModel = paramRenderedImage.getSampleModel();
    boolean bool;
    if (paramGIFWritableImageMetadata == null)
    {
      paramGIFWritableImageMetadata = (GIFWritableImageMetadata)getDefaultImageMetadata(new ImageTypeSpecifier(paramRenderedImage), paramImageWriteParam);
      bool = transparentColorFlag;
    }
    else
    {
      NodeList localNodeList = null;
      try
      {
        IIOMetadataNode localIIOMetadataNode = (IIOMetadataNode)paramGIFWritableImageMetadata.getAsTree("javax_imageio_gif_image_1.0");
        localNodeList = localIIOMetadataNode.getElementsByTagName("GraphicControlExtension");
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
      bool = (localNodeList != null) && (localNodeList.getLength() > 0);
      if ((paramImageWriteParam != null) && (paramImageWriteParam.canWriteProgressive())) {
        if (paramImageWriteParam.getProgressiveMode() == 0) {
          interlaceFlag = false;
        } else if (paramImageWriteParam.getProgressiveMode() == 1) {
          interlaceFlag = true;
        }
      }
    }
    if (Arrays.equals(paramArrayOfByte, localColorTable)) {
      localColorTable = null;
    }
    imageWidth = width;
    imageHeight = height;
    if (bool) {
      writeGraphicControlExtension(paramGIFWritableImageMetadata);
    }
    writePlainTextExtension(paramGIFWritableImageMetadata);
    writeApplicationExtension(paramGIFWritableImageMetadata);
    writeCommentExtension(paramGIFWritableImageMetadata);
    int i = getNumBits(localColorTable == null ? paramArrayOfByte.length / 3 : paramArrayOfByte == null ? localSampleModel.getSampleSize(0) : localColorTable.length / 3);
    writeImageDescriptor(paramGIFWritableImageMetadata, i);
    writeRasterData(paramRenderedImage, paramRectangle, paramDimension, paramImageWriteParam, interlaceFlag);
  }
  
  private void writeRows(RenderedImage paramRenderedImage, LZWCompressor paramLZWCompressor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11)
    throws IOException
  {
    int[] arrayOfInt = new int[paramInt5];
    byte[] arrayOfByte = new byte[paramInt8];
    Raster localRaster = (paramRenderedImage.getNumXTiles() == 1) && (paramRenderedImage.getNumYTiles() == 1) ? paramRenderedImage.getTile(0, 0) : paramRenderedImage.getData();
    int i = paramInt6;
    while (i < paramInt9)
    {
      if (paramInt10 % paramInt11 == 0)
      {
        if (abortRequested())
        {
          processWriteAborted();
          return;
        }
        processImageProgress(paramInt10 * 100.0F / paramInt9);
      }
      localRaster.getSamples(paramInt1, paramInt3, paramInt5, 1, 0, arrayOfInt);
      int j = 0;
      int k = 0;
      while (j < paramInt8)
      {
        arrayOfByte[j] = ((byte)arrayOfInt[k]);
        j++;
        k += paramInt2;
      }
      paramLZWCompressor.compress(arrayOfByte, 0, paramInt8);
      paramInt10++;
      paramInt3 += paramInt4;
      i += paramInt7;
    }
  }
  
  private void writeRowsOpt(byte[] paramArrayOfByte, int paramInt1, int paramInt2, LZWCompressor paramLZWCompressor, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
    throws IOException
  {
    paramInt1 += paramInt3 * paramInt2;
    paramInt2 *= paramInt4;
    int i = paramInt3;
    while (i < paramInt6)
    {
      if (paramInt7 % paramInt8 == 0)
      {
        if (abortRequested())
        {
          processWriteAborted();
          return;
        }
        processImageProgress(paramInt7 * 100.0F / paramInt6);
      }
      paramLZWCompressor.compress(paramArrayOfByte, paramInt1, paramInt5);
      paramInt7++;
      paramInt1 += paramInt2;
      i += paramInt4;
    }
  }
  
  private void writeRasterData(RenderedImage paramRenderedImage, Rectangle paramRectangle, Dimension paramDimension, ImageWriteParam paramImageWriteParam, boolean paramBoolean)
    throws IOException
  {
    int i = x;
    int j = y;
    int k = width;
    int m = height;
    int n = width;
    int i1 = height;
    int i2;
    int i3;
    if (paramImageWriteParam == null)
    {
      i2 = 1;
      i3 = 1;
    }
    else
    {
      i2 = paramImageWriteParam.getSourceXSubsampling();
      i3 = paramImageWriteParam.getSourceYSubsampling();
    }
    SampleModel localSampleModel = paramRenderedImage.getSampleModel();
    int i4 = localSampleModel.getSampleSize()[0];
    int i5 = i4;
    if (i5 == 1) {
      i5++;
    }
    stream.write(i5);
    LZWCompressor localLZWCompressor = new LZWCompressor(stream, i5, false);
    int i6 = (i2 == 1) && (i3 == 1) && (paramRenderedImage.getNumXTiles() == 1) && (paramRenderedImage.getNumYTiles() == 1) && ((localSampleModel instanceof ComponentSampleModel)) && ((paramRenderedImage.getTile(0, 0) instanceof ByteComponentRaster)) && ((paramRenderedImage.getTile(0, 0).getDataBuffer() instanceof DataBufferByte)) ? 1 : 0;
    int i7 = 0;
    int i8 = Math.max(i1 / 20, 1);
    processImageStarted(imageIndex);
    Object localObject;
    byte[] arrayOfByte;
    ComponentSampleModel localComponentSampleModel;
    int i9;
    int i10;
    if (paramBoolean)
    {
      if (i6 != 0)
      {
        localObject = (ByteComponentRaster)paramRenderedImage.getTile(0, 0);
        arrayOfByte = ((DataBufferByte)((ByteComponentRaster)localObject).getDataBuffer()).getData();
        localComponentSampleModel = (ComponentSampleModel)((ByteComponentRaster)localObject).getSampleModel();
        i9 = localComponentSampleModel.getOffset(i, j, 0);
        i9 += ((ByteComponentRaster)localObject).getDataOffset(0);
        i10 = localComponentSampleModel.getScanlineStride();
        writeRowsOpt(arrayOfByte, i9, i10, localLZWCompressor, 0, 8, n, i1, i7, i8);
        if (abortRequested()) {
          return;
        }
        i7 += i1 / 8;
        writeRowsOpt(arrayOfByte, i9, i10, localLZWCompressor, 4, 8, n, i1, i7, i8);
        if (abortRequested()) {
          return;
        }
        i7 += (i1 - 4) / 8;
        writeRowsOpt(arrayOfByte, i9, i10, localLZWCompressor, 2, 4, n, i1, i7, i8);
        if (abortRequested()) {
          return;
        }
        i7 += (i1 - 2) / 4;
        writeRowsOpt(arrayOfByte, i9, i10, localLZWCompressor, 1, 2, n, i1, i7, i8);
      }
      else
      {
        writeRows(paramRenderedImage, localLZWCompressor, i, i2, j, 8 * i3, k, 0, 8, n, i1, i7, i8);
        if (abortRequested()) {
          return;
        }
        i7 += i1 / 8;
        writeRows(paramRenderedImage, localLZWCompressor, i, i2, j + 4 * i3, 8 * i3, k, 4, 8, n, i1, i7, i8);
        if (abortRequested()) {
          return;
        }
        i7 += (i1 - 4) / 8;
        writeRows(paramRenderedImage, localLZWCompressor, i, i2, j + 2 * i3, 4 * i3, k, 2, 4, n, i1, i7, i8);
        if (abortRequested()) {
          return;
        }
        i7 += (i1 - 2) / 4;
        writeRows(paramRenderedImage, localLZWCompressor, i, i2, j + i3, 2 * i3, k, 1, 2, n, i1, i7, i8);
      }
    }
    else if (i6 != 0)
    {
      localObject = paramRenderedImage.getTile(0, 0);
      arrayOfByte = ((DataBufferByte)((Raster)localObject).getDataBuffer()).getData();
      localComponentSampleModel = (ComponentSampleModel)((Raster)localObject).getSampleModel();
      i9 = localComponentSampleModel.getOffset(i, j, 0);
      i10 = localComponentSampleModel.getScanlineStride();
      writeRowsOpt(arrayOfByte, i9, i10, localLZWCompressor, 0, 1, n, i1, i7, i8);
    }
    else
    {
      writeRows(paramRenderedImage, localLZWCompressor, i, i2, j, i3, k, 0, 1, n, i1, i7, i8);
    }
    if (abortRequested()) {
      return;
    }
    processImageProgress(100.0F);
    localLZWCompressor.flush();
    stream.write(0);
    processImageComplete();
  }
  
  private void writeHeader(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, int paramInt6, byte[] paramArrayOfByte)
    throws IOException
  {
    try
    {
      stream.writeBytes("GIF" + paramString);
      stream.writeShort((short)paramInt1);
      stream.writeShort((short)paramInt2);
      int i = paramArrayOfByte != null ? 128 : 0;
      i |= (paramInt3 - 1 & 0x7) << 4;
      if (paramBoolean) {
        i |= 0x8;
      }
      i |= paramInt6 - 1;
      stream.write(i);
      stream.write(paramInt5);
      stream.write(paramInt4);
      if (paramArrayOfByte != null) {
        stream.write(paramArrayOfByte);
      }
    }
    catch (IOException localIOException)
    {
      throw new IIOException("I/O error writing header!", localIOException);
    }
  }
  
  private void writeHeader(IIOMetadata paramIIOMetadata, int paramInt)
    throws IOException
  {
    GIFWritableStreamMetadata localGIFWritableStreamMetadata;
    if ((paramIIOMetadata instanceof GIFWritableStreamMetadata))
    {
      localGIFWritableStreamMetadata = (GIFWritableStreamMetadata)paramIIOMetadata;
    }
    else
    {
      localGIFWritableStreamMetadata = new GIFWritableStreamMetadata();
      Node localNode = paramIIOMetadata.getAsTree("javax_imageio_gif_stream_1.0");
      localGIFWritableStreamMetadata.setFromTree("javax_imageio_gif_stream_1.0", localNode);
    }
    writeHeader(version, logicalScreenWidth, logicalScreenHeight, colorResolution, pixelAspectRatio, backgroundColorIndex, sortFlag, paramInt, globalColorTable);
  }
  
  private void writeGraphicControlExtension(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3)
    throws IOException
  {
    try
    {
      stream.write(33);
      stream.write(249);
      stream.write(4);
      int i = (paramInt1 & 0x3) << 2;
      if (paramBoolean1) {
        i |= 0x2;
      }
      if (paramBoolean2) {
        i |= 0x1;
      }
      stream.write(i);
      stream.writeShort((short)paramInt2);
      stream.write(paramInt3);
      stream.write(0);
    }
    catch (IOException localIOException)
    {
      throw new IIOException("I/O error writing Graphic Control Extension!", localIOException);
    }
  }
  
  private void writeGraphicControlExtension(GIFWritableImageMetadata paramGIFWritableImageMetadata)
    throws IOException
  {
    writeGraphicControlExtension(disposalMethod, userInputFlag, transparentColorFlag, delayTime, transparentColorIndex);
  }
  
  private void writeBlocks(byte[] paramArrayOfByte)
    throws IOException
  {
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length > 0))
    {
      int i = 0;
      while (i < paramArrayOfByte.length)
      {
        int j = Math.min(paramArrayOfByte.length - i, 255);
        stream.write(j);
        stream.write(paramArrayOfByte, i, j);
        i += j;
      }
    }
  }
  
  private void writePlainTextExtension(GIFWritableImageMetadata paramGIFWritableImageMetadata)
    throws IOException
  {
    if (hasPlainTextExtension) {
      try
      {
        stream.write(33);
        stream.write(1);
        stream.write(12);
        stream.writeShort(textGridLeft);
        stream.writeShort(textGridTop);
        stream.writeShort(textGridWidth);
        stream.writeShort(textGridHeight);
        stream.write(characterCellWidth);
        stream.write(characterCellHeight);
        stream.write(textForegroundColor);
        stream.write(textBackgroundColor);
        writeBlocks(text);
        stream.write(0);
      }
      catch (IOException localIOException)
      {
        throw new IIOException("I/O error writing Plain Text Extension!", localIOException);
      }
    }
  }
  
  private void writeApplicationExtension(GIFWritableImageMetadata paramGIFWritableImageMetadata)
    throws IOException
  {
    if (applicationIDs != null)
    {
      Iterator localIterator1 = applicationIDs.iterator();
      Iterator localIterator2 = authenticationCodes.iterator();
      Iterator localIterator3 = applicationData.iterator();
      while (localIterator1.hasNext()) {
        try
        {
          stream.write(33);
          stream.write(255);
          stream.write(11);
          stream.write((byte[])localIterator1.next(), 0, 8);
          stream.write((byte[])localIterator2.next(), 0, 3);
          writeBlocks((byte[])localIterator3.next());
          stream.write(0);
        }
        catch (IOException localIOException)
        {
          throw new IIOException("I/O error writing Application Extension!", localIOException);
        }
      }
    }
  }
  
  private void writeCommentExtension(GIFWritableImageMetadata paramGIFWritableImageMetadata)
    throws IOException
  {
    if (comments != null) {
      try
      {
        Iterator localIterator = comments.iterator();
        while (localIterator.hasNext())
        {
          stream.write(33);
          stream.write(254);
          writeBlocks((byte[])localIterator.next());
          stream.write(0);
        }
      }
      catch (IOException localIOException)
      {
        throw new IIOException("I/O error writing Comment Extension!", localIOException);
      }
    }
  }
  
  private void writeImageDescriptor(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, int paramInt5, byte[] paramArrayOfByte)
    throws IOException
  {
    try
    {
      stream.write(44);
      stream.writeShort((short)paramInt1);
      stream.writeShort((short)paramInt2);
      stream.writeShort((short)paramInt3);
      stream.writeShort((short)paramInt4);
      int i = paramArrayOfByte != null ? 128 : 0;
      if (paramBoolean1) {
        i |= 0x40;
      }
      if (paramBoolean2) {
        i |= 0x8;
      }
      i |= paramInt5 - 1;
      stream.write(i);
      if (paramArrayOfByte != null) {
        stream.write(paramArrayOfByte);
      }
    }
    catch (IOException localIOException)
    {
      throw new IIOException("I/O error writing Image Descriptor!", localIOException);
    }
  }
  
  private void writeImageDescriptor(GIFWritableImageMetadata paramGIFWritableImageMetadata, int paramInt)
    throws IOException
  {
    writeImageDescriptor(imageLeftPosition, imageTopPosition, imageWidth, imageHeight, interlaceFlag, sortFlag, paramInt, localColorTable);
  }
  
  private void writeTrailer()
    throws IOException
  {
    stream.write(59);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\gif\GIFImageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */