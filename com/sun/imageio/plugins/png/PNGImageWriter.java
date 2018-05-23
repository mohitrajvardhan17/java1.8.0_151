package com.sun.imageio.plugins.png;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.DeflaterOutputStream;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class PNGImageWriter
  extends ImageWriter
{
  ImageOutputStream stream = null;
  PNGMetadata metadata = null;
  int sourceXOffset = 0;
  int sourceYOffset = 0;
  int sourceWidth = 0;
  int sourceHeight = 0;
  int[] sourceBands = null;
  int periodX = 1;
  int periodY = 1;
  int numBands;
  int bpp;
  RowFilter rowFilter = new RowFilter();
  byte[] prevRow = null;
  byte[] currRow = null;
  byte[][] filteredRows = (byte[][])null;
  int[] sampleSize = null;
  int scalingBitDepth = -1;
  byte[][] scale = (byte[][])null;
  byte[] scale0 = null;
  byte[][] scaleh = (byte[][])null;
  byte[][] scalel = (byte[][])null;
  int totalPixels;
  int pixelsDone;
  private static int[] allowedProgressivePasses = { 1, 7 };
  
  public PNGImageWriter(ImageWriterSpi paramImageWriterSpi)
  {
    super(paramImageWriterSpi);
  }
  
  public void setOutput(Object paramObject)
  {
    super.setOutput(paramObject);
    if (paramObject != null)
    {
      if (!(paramObject instanceof ImageOutputStream)) {
        throw new IllegalArgumentException("output not an ImageOutputStream!");
      }
      stream = ((ImageOutputStream)paramObject);
    }
    else
    {
      stream = null;
    }
  }
  
  public ImageWriteParam getDefaultWriteParam()
  {
    return new PNGImageWriteParam(getLocale());
  }
  
  public IIOMetadata getDefaultStreamMetadata(ImageWriteParam paramImageWriteParam)
  {
    return null;
  }
  
  public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam)
  {
    PNGMetadata localPNGMetadata = new PNGMetadata();
    localPNGMetadata.initialize(paramImageTypeSpecifier, paramImageTypeSpecifier.getSampleModel().getNumBands());
    return localPNGMetadata;
  }
  
  public IIOMetadata convertStreamMetadata(IIOMetadata paramIIOMetadata, ImageWriteParam paramImageWriteParam)
  {
    return null;
  }
  
  public IIOMetadata convertImageMetadata(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam)
  {
    if ((paramIIOMetadata instanceof PNGMetadata)) {
      return (PNGMetadata)((PNGMetadata)paramIIOMetadata).clone();
    }
    return new PNGMetadata(paramIIOMetadata);
  }
  
  private void write_magic()
    throws IOException
  {
    byte[] arrayOfByte = { -119, 80, 78, 71, 13, 10, 26, 10 };
    stream.write(arrayOfByte);
  }
  
  private void write_IHDR()
    throws IOException
  {
    ChunkStream localChunkStream = new ChunkStream(1229472850, stream);
    localChunkStream.writeInt(metadata.IHDR_width);
    localChunkStream.writeInt(metadata.IHDR_height);
    localChunkStream.writeByte(metadata.IHDR_bitDepth);
    localChunkStream.writeByte(metadata.IHDR_colorType);
    if (metadata.IHDR_compressionMethod != 0) {
      throw new IIOException("Only compression method 0 is defined in PNG 1.1");
    }
    localChunkStream.writeByte(metadata.IHDR_compressionMethod);
    if (metadata.IHDR_filterMethod != 0) {
      throw new IIOException("Only filter method 0 is defined in PNG 1.1");
    }
    localChunkStream.writeByte(metadata.IHDR_filterMethod);
    if ((metadata.IHDR_interlaceMethod < 0) || (metadata.IHDR_interlaceMethod > 1)) {
      throw new IIOException("Only interlace methods 0 (node) and 1 (adam7) are defined in PNG 1.1");
    }
    localChunkStream.writeByte(metadata.IHDR_interlaceMethod);
    localChunkStream.finish();
  }
  
  private void write_cHRM()
    throws IOException
  {
    if (metadata.cHRM_present)
    {
      ChunkStream localChunkStream = new ChunkStream(1665684045, stream);
      localChunkStream.writeInt(metadata.cHRM_whitePointX);
      localChunkStream.writeInt(metadata.cHRM_whitePointY);
      localChunkStream.writeInt(metadata.cHRM_redX);
      localChunkStream.writeInt(metadata.cHRM_redY);
      localChunkStream.writeInt(metadata.cHRM_greenX);
      localChunkStream.writeInt(metadata.cHRM_greenY);
      localChunkStream.writeInt(metadata.cHRM_blueX);
      localChunkStream.writeInt(metadata.cHRM_blueY);
      localChunkStream.finish();
    }
  }
  
  private void write_gAMA()
    throws IOException
  {
    if (metadata.gAMA_present)
    {
      ChunkStream localChunkStream = new ChunkStream(1732332865, stream);
      localChunkStream.writeInt(metadata.gAMA_gamma);
      localChunkStream.finish();
    }
  }
  
  private void write_iCCP()
    throws IOException
  {
    if (metadata.iCCP_present)
    {
      ChunkStream localChunkStream = new ChunkStream(1766015824, stream);
      localChunkStream.writeBytes(metadata.iCCP_profileName);
      localChunkStream.writeByte(0);
      localChunkStream.writeByte(metadata.iCCP_compressionMethod);
      localChunkStream.write(metadata.iCCP_compressedProfile);
      localChunkStream.finish();
    }
  }
  
  private void write_sBIT()
    throws IOException
  {
    if (metadata.sBIT_present)
    {
      ChunkStream localChunkStream = new ChunkStream(1933723988, stream);
      int i = metadata.IHDR_colorType;
      if (metadata.sBIT_colorType != i)
      {
        processWarningOccurred(0, "sBIT metadata has wrong color type.\nThe chunk will not be written.");
        return;
      }
      if ((i == 0) || (i == 4))
      {
        localChunkStream.writeByte(metadata.sBIT_grayBits);
      }
      else if ((i == 2) || (i == 3) || (i == 6))
      {
        localChunkStream.writeByte(metadata.sBIT_redBits);
        localChunkStream.writeByte(metadata.sBIT_greenBits);
        localChunkStream.writeByte(metadata.sBIT_blueBits);
      }
      if ((i == 4) || (i == 6)) {
        localChunkStream.writeByte(metadata.sBIT_alphaBits);
      }
      localChunkStream.finish();
    }
  }
  
  private void write_sRGB()
    throws IOException
  {
    if (metadata.sRGB_present)
    {
      ChunkStream localChunkStream = new ChunkStream(1934772034, stream);
      localChunkStream.writeByte(metadata.sRGB_renderingIntent);
      localChunkStream.finish();
    }
  }
  
  private void write_PLTE()
    throws IOException
  {
    if (metadata.PLTE_present)
    {
      if ((metadata.IHDR_colorType == 0) || (metadata.IHDR_colorType == 4))
      {
        processWarningOccurred(0, "A PLTE chunk may not appear in a gray or gray alpha image.\nThe chunk will not be written");
        return;
      }
      ChunkStream localChunkStream = new ChunkStream(1347179589, stream);
      int i = metadata.PLTE_red.length;
      byte[] arrayOfByte = new byte[i * 3];
      int j = 0;
      for (int k = 0; k < i; k++)
      {
        arrayOfByte[(j++)] = metadata.PLTE_red[k];
        arrayOfByte[(j++)] = metadata.PLTE_green[k];
        arrayOfByte[(j++)] = metadata.PLTE_blue[k];
      }
      localChunkStream.write(arrayOfByte);
      localChunkStream.finish();
    }
  }
  
  private void write_hIST()
    throws IOException, IIOException
  {
    if (metadata.hIST_present)
    {
      ChunkStream localChunkStream = new ChunkStream(1749635924, stream);
      if (!metadata.PLTE_present) {
        throw new IIOException("hIST chunk without PLTE chunk!");
      }
      localChunkStream.writeChars(metadata.hIST_histogram, 0, metadata.hIST_histogram.length);
      localChunkStream.finish();
    }
  }
  
  private void write_tRNS()
    throws IOException, IIOException
  {
    if (metadata.tRNS_present)
    {
      ChunkStream localChunkStream = new ChunkStream(1951551059, stream);
      int i = metadata.IHDR_colorType;
      int j = metadata.tRNS_colorType;
      int k = metadata.tRNS_red;
      int m = metadata.tRNS_green;
      int n = metadata.tRNS_blue;
      if ((i == 2) && (j == 0))
      {
        j = i;
        k = m = n = metadata.tRNS_gray;
      }
      if (j != i)
      {
        processWarningOccurred(0, "tRNS metadata has incompatible color type.\nThe chunk will not be written.");
        return;
      }
      if (i == 3)
      {
        if (!metadata.PLTE_present) {
          throw new IIOException("tRNS chunk without PLTE chunk!");
        }
        localChunkStream.write(metadata.tRNS_alpha);
      }
      else if (i == 0)
      {
        localChunkStream.writeShort(metadata.tRNS_gray);
      }
      else if (i == 2)
      {
        localChunkStream.writeShort(k);
        localChunkStream.writeShort(m);
        localChunkStream.writeShort(n);
      }
      else
      {
        throw new IIOException("tRNS chunk for color type 4 or 6!");
      }
      localChunkStream.finish();
    }
  }
  
  private void write_bKGD()
    throws IOException
  {
    if (metadata.bKGD_present)
    {
      ChunkStream localChunkStream = new ChunkStream(1649100612, stream);
      int i = metadata.IHDR_colorType & 0x3;
      int j = metadata.bKGD_colorType;
      int k = metadata.bKGD_red;
      int m = metadata.bKGD_red;
      int n = metadata.bKGD_red;
      if ((i == 2) && (j == 0))
      {
        j = i;
        k = m = n = metadata.bKGD_gray;
      }
      if (j != i)
      {
        processWarningOccurred(0, "bKGD metadata has incompatible color type.\nThe chunk will not be written.");
        return;
      }
      if (i == 3)
      {
        localChunkStream.writeByte(metadata.bKGD_index);
      }
      else if ((i == 0) || (i == 4))
      {
        localChunkStream.writeShort(metadata.bKGD_gray);
      }
      else
      {
        localChunkStream.writeShort(k);
        localChunkStream.writeShort(m);
        localChunkStream.writeShort(n);
      }
      localChunkStream.finish();
    }
  }
  
  private void write_pHYs()
    throws IOException
  {
    if (metadata.pHYs_present)
    {
      ChunkStream localChunkStream = new ChunkStream(1883789683, stream);
      localChunkStream.writeInt(metadata.pHYs_pixelsPerUnitXAxis);
      localChunkStream.writeInt(metadata.pHYs_pixelsPerUnitYAxis);
      localChunkStream.writeByte(metadata.pHYs_unitSpecifier);
      localChunkStream.finish();
    }
  }
  
  private void write_sPLT()
    throws IOException
  {
    if (metadata.sPLT_present)
    {
      ChunkStream localChunkStream = new ChunkStream(1934642260, stream);
      localChunkStream.writeBytes(metadata.sPLT_paletteName);
      localChunkStream.writeByte(0);
      localChunkStream.writeByte(metadata.sPLT_sampleDepth);
      int i = metadata.sPLT_red.length;
      int j;
      if (metadata.sPLT_sampleDepth == 8) {
        for (j = 0; j < i; j++)
        {
          localChunkStream.writeByte(metadata.sPLT_red[j]);
          localChunkStream.writeByte(metadata.sPLT_green[j]);
          localChunkStream.writeByte(metadata.sPLT_blue[j]);
          localChunkStream.writeByte(metadata.sPLT_alpha[j]);
          localChunkStream.writeShort(metadata.sPLT_frequency[j]);
        }
      } else {
        for (j = 0; j < i; j++)
        {
          localChunkStream.writeShort(metadata.sPLT_red[j]);
          localChunkStream.writeShort(metadata.sPLT_green[j]);
          localChunkStream.writeShort(metadata.sPLT_blue[j]);
          localChunkStream.writeShort(metadata.sPLT_alpha[j]);
          localChunkStream.writeShort(metadata.sPLT_frequency[j]);
        }
      }
      localChunkStream.finish();
    }
  }
  
  private void write_tIME()
    throws IOException
  {
    if (metadata.tIME_present)
    {
      ChunkStream localChunkStream = new ChunkStream(1950960965, stream);
      localChunkStream.writeShort(metadata.tIME_year);
      localChunkStream.writeByte(metadata.tIME_month);
      localChunkStream.writeByte(metadata.tIME_day);
      localChunkStream.writeByte(metadata.tIME_hour);
      localChunkStream.writeByte(metadata.tIME_minute);
      localChunkStream.writeByte(metadata.tIME_second);
      localChunkStream.finish();
    }
  }
  
  private void write_tEXt()
    throws IOException
  {
    Iterator localIterator1 = metadata.tEXt_keyword.iterator();
    Iterator localIterator2 = metadata.tEXt_text.iterator();
    while (localIterator1.hasNext())
    {
      ChunkStream localChunkStream = new ChunkStream(1950701684, stream);
      String str1 = (String)localIterator1.next();
      localChunkStream.writeBytes(str1);
      localChunkStream.writeByte(0);
      String str2 = (String)localIterator2.next();
      localChunkStream.writeBytes(str2);
      localChunkStream.finish();
    }
  }
  
  private byte[] deflate(byte[] paramArrayOfByte)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    DeflaterOutputStream localDeflaterOutputStream = new DeflaterOutputStream(localByteArrayOutputStream);
    localDeflaterOutputStream.write(paramArrayOfByte);
    localDeflaterOutputStream.close();
    return localByteArrayOutputStream.toByteArray();
  }
  
  private void write_iTXt()
    throws IOException
  {
    Iterator localIterator1 = metadata.iTXt_keyword.iterator();
    Iterator localIterator2 = metadata.iTXt_compressionFlag.iterator();
    Iterator localIterator3 = metadata.iTXt_compressionMethod.iterator();
    Iterator localIterator4 = metadata.iTXt_languageTag.iterator();
    Iterator localIterator5 = metadata.iTXt_translatedKeyword.iterator();
    Iterator localIterator6 = metadata.iTXt_text.iterator();
    while (localIterator1.hasNext())
    {
      ChunkStream localChunkStream = new ChunkStream(1767135348, stream);
      localChunkStream.writeBytes((String)localIterator1.next());
      localChunkStream.writeByte(0);
      Boolean localBoolean = (Boolean)localIterator2.next();
      localChunkStream.writeByte(localBoolean.booleanValue() ? 1 : 0);
      localChunkStream.writeByte(((Integer)localIterator3.next()).intValue());
      localChunkStream.writeBytes((String)localIterator4.next());
      localChunkStream.writeByte(0);
      localChunkStream.write(((String)localIterator5.next()).getBytes("UTF8"));
      localChunkStream.writeByte(0);
      String str = (String)localIterator6.next();
      if (localBoolean.booleanValue()) {
        localChunkStream.write(deflate(str.getBytes("UTF8")));
      } else {
        localChunkStream.write(str.getBytes("UTF8"));
      }
      localChunkStream.finish();
    }
  }
  
  private void write_zTXt()
    throws IOException
  {
    Iterator localIterator1 = metadata.zTXt_keyword.iterator();
    Iterator localIterator2 = metadata.zTXt_compressionMethod.iterator();
    Iterator localIterator3 = metadata.zTXt_text.iterator();
    while (localIterator1.hasNext())
    {
      ChunkStream localChunkStream = new ChunkStream(2052348020, stream);
      String str1 = (String)localIterator1.next();
      localChunkStream.writeBytes(str1);
      localChunkStream.writeByte(0);
      int i = ((Integer)localIterator2.next()).intValue();
      localChunkStream.writeByte(i);
      String str2 = (String)localIterator3.next();
      localChunkStream.write(deflate(str2.getBytes("ISO-8859-1")));
      localChunkStream.finish();
    }
  }
  
  private void writeUnknownChunks()
    throws IOException
  {
    Iterator localIterator1 = metadata.unknownChunkType.iterator();
    Iterator localIterator2 = metadata.unknownChunkData.iterator();
    while ((localIterator1.hasNext()) && (localIterator2.hasNext()))
    {
      String str = (String)localIterator1.next();
      ChunkStream localChunkStream = new ChunkStream(chunkType(str), stream);
      byte[] arrayOfByte = (byte[])localIterator2.next();
      localChunkStream.write(arrayOfByte);
      localChunkStream.finish();
    }
  }
  
  private static int chunkType(String paramString)
  {
    int i = paramString.charAt(0);
    int j = paramString.charAt(1);
    int k = paramString.charAt(2);
    int m = paramString.charAt(3);
    int n = i << 24 | j << 16 | k << 8 | m;
    return n;
  }
  
  private void encodePass(ImageOutputStream paramImageOutputStream, RenderedImage paramRenderedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws IOException
  {
    int i = sourceXOffset;
    int j = sourceYOffset;
    int k = sourceWidth;
    int m = sourceHeight;
    paramInt1 *= periodX;
    paramInt3 *= periodX;
    paramInt2 *= periodY;
    paramInt4 *= periodY;
    int n = (k - paramInt1 + paramInt3 - 1) / paramInt3;
    int i1 = (m - paramInt2 + paramInt4 - 1) / paramInt4;
    if ((n == 0) || (i1 == 0)) {
      return;
    }
    paramInt1 *= numBands;
    paramInt3 *= numBands;
    int i2 = 8 / metadata.IHDR_bitDepth;
    int i3 = k * numBands;
    int[] arrayOfInt = new int[i3];
    int i4 = n * numBands;
    if (metadata.IHDR_bitDepth < 8) {
      i4 = (i4 + i2 - 1) / i2;
    } else if (metadata.IHDR_bitDepth == 16) {
      i4 *= 2;
    }
    IndexColorModel localIndexColorModel = null;
    if ((metadata.IHDR_colorType == 4) && ((paramRenderedImage.getColorModel() instanceof IndexColorModel)))
    {
      i4 *= 2;
      localIndexColorModel = (IndexColorModel)paramRenderedImage.getColorModel();
    }
    currRow = new byte[i4 + bpp];
    prevRow = new byte[i4 + bpp];
    filteredRows = new byte[5][i4 + bpp];
    int i5 = metadata.IHDR_bitDepth;
    int i6 = j + paramInt2;
    while (i6 < j + m)
    {
      Rectangle localRectangle = new Rectangle(i, i6, k, 1);
      Raster localRaster = paramRenderedImage.getData(localRectangle);
      if (sourceBands != null) {
        localRaster = localRaster.createChild(i, i6, k, 1, i, i6, sourceBands);
      }
      localRaster.getPixels(i, i6, k, 1, arrayOfInt);
      if (paramRenderedImage.getColorModel().isAlphaPremultiplied())
      {
        localObject = localRaster.createCompatibleWritableRaster();
        ((WritableRaster)localObject).setPixels(((WritableRaster)localObject).getMinX(), ((WritableRaster)localObject).getMinY(), ((WritableRaster)localObject).getWidth(), ((WritableRaster)localObject).getHeight(), arrayOfInt);
        paramRenderedImage.getColorModel().coerceData((WritableRaster)localObject, false);
        ((WritableRaster)localObject).getPixels(((WritableRaster)localObject).getMinX(), ((WritableRaster)localObject).getMinY(), ((WritableRaster)localObject).getWidth(), ((WritableRaster)localObject).getHeight(), arrayOfInt);
      }
      Object localObject = metadata.PLTE_order;
      if (localObject != null) {
        for (i7 = 0; i7 < i3; i7++) {
          arrayOfInt[i7] = localObject[arrayOfInt[i7]];
        }
      }
      int i7 = bpp;
      int i8 = 0;
      int i9 = 0;
      int i11;
      int i12;
      switch (i5)
      {
      case 1: 
      case 2: 
      case 4: 
        i10 = i2 - 1;
        i11 = paramInt1;
        while (i11 < i3)
        {
          i12 = scale0[arrayOfInt[i11]];
          i9 = i9 << i5 | i12;
          if ((i8++ & i10) == i10)
          {
            currRow[(i7++)] = ((byte)i9);
            i9 = 0;
            i8 = 0;
          }
          i11 += paramInt3;
        }
        if ((i8 & i10) != 0)
        {
          i9 <<= (8 / i5 - i8) * i5;
          currRow[(i7++)] = ((byte)i9);
        }
        break;
      case 8: 
        if (numBands == 1)
        {
          i11 = paramInt1;
          while (i11 < i3)
          {
            currRow[(i7++)] = scale0[arrayOfInt[i11]];
            if (localIndexColorModel != null) {
              currRow[(i7++)] = scale0[localIndexColorModel.getAlpha(0xFF & arrayOfInt[i11])];
            }
            i11 += paramInt3;
          }
        }
        else
        {
          i11 = paramInt1;
          while (i11 < i3)
          {
            for (i12 = 0; i12 < numBands; i12++) {
              currRow[(i7++)] = scale[i12][arrayOfInt[(i11 + i12)]];
            }
            i11 += paramInt3;
          }
        }
        break;
      case 16: 
        i11 = paramInt1;
        while (i11 < i3)
        {
          for (i12 = 0; i12 < numBands; i12++)
          {
            currRow[(i7++)] = scaleh[i12][arrayOfInt[(i11 + i12)]];
            currRow[(i7++)] = scalel[i12][arrayOfInt[(i11 + i12)]];
          }
          i11 += paramInt3;
        }
      }
      int i10 = rowFilter.filterRow(metadata.IHDR_colorType, currRow, prevRow, filteredRows, i4, bpp);
      paramImageOutputStream.write(i10);
      paramImageOutputStream.write(filteredRows[i10], bpp, i4);
      byte[] arrayOfByte = currRow;
      currRow = prevRow;
      prevRow = arrayOfByte;
      pixelsDone += n;
      processImageProgress(100.0F * pixelsDone / totalPixels);
      if (abortRequested()) {
        return;
      }
      i6 += paramInt4;
    }
  }
  
  private void write_IDAT(RenderedImage paramRenderedImage)
    throws IOException
  {
    IDATOutputStream localIDATOutputStream = new IDATOutputStream(stream, 32768);
    try
    {
      if (metadata.IHDR_interlaceMethod == 1) {
        for (int i = 0; i < 7; i++)
        {
          encodePass(localIDATOutputStream, paramRenderedImage, PNGImageReader.adam7XOffset[i], PNGImageReader.adam7YOffset[i], PNGImageReader.adam7XSubsampling[i], PNGImageReader.adam7YSubsampling[i]);
          if (abortRequested()) {
            break;
          }
        }
      } else {
        encodePass(localIDATOutputStream, paramRenderedImage, 0, 0, 1, 1);
      }
    }
    finally
    {
      localIDATOutputStream.finish();
    }
  }
  
  private void writeIEND()
    throws IOException
  {
    ChunkStream localChunkStream = new ChunkStream(1229278788, stream);
    localChunkStream.finish();
  }
  
  private boolean equals(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if ((paramArrayOfInt1 == null) || (paramArrayOfInt2 == null)) {
      return false;
    }
    if (paramArrayOfInt1.length != paramArrayOfInt2.length) {
      return false;
    }
    for (int i = 0; i < paramArrayOfInt1.length; i++) {
      if (paramArrayOfInt1[i] != paramArrayOfInt2[i]) {
        return false;
      }
    }
    return true;
  }
  
  private void initializeScaleTables(int[] paramArrayOfInt)
  {
    int i = metadata.IHDR_bitDepth;
    if ((i == scalingBitDepth) && (equals(paramArrayOfInt, sampleSize))) {
      return;
    }
    sampleSize = paramArrayOfInt;
    scalingBitDepth = i;
    int j = (1 << i) - 1;
    int k;
    int m;
    int n;
    int i1;
    if (i <= 8)
    {
      scale = new byte[numBands][];
      for (k = 0; k < numBands; k++)
      {
        m = (1 << paramArrayOfInt[k]) - 1;
        n = m / 2;
        scale[k] = new byte[m + 1];
        for (i1 = 0; i1 <= m; i1++) {
          scale[k][i1] = ((byte)((i1 * j + n) / m));
        }
      }
      scale0 = scale[0];
      scaleh = (scalel = (byte[][])null);
    }
    else
    {
      scaleh = new byte[numBands][];
      scalel = new byte[numBands][];
      for (k = 0; k < numBands; k++)
      {
        m = (1 << paramArrayOfInt[k]) - 1;
        n = m / 2;
        scaleh[k] = new byte[m + 1];
        scalel[k] = new byte[m + 1];
        for (i1 = 0; i1 <= m; i1++)
        {
          int i2 = (i1 * j + n) / m;
          scaleh[k][i1] = ((byte)(i2 >> 8));
          scalel[k][i1] = ((byte)(i2 & 0xFF));
        }
      }
      scale = ((byte[][])null);
      scale0 = null;
    }
  }
  
  public void write(IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam)
    throws IIOException
  {
    if (stream == null) {
      throw new IllegalStateException("output == null!");
    }
    if (paramIIOImage == null) {
      throw new IllegalArgumentException("image == null!");
    }
    if (paramIIOImage.hasRaster()) {
      throw new UnsupportedOperationException("image has a Raster!");
    }
    RenderedImage localRenderedImage = paramIIOImage.getRenderedImage();
    SampleModel localSampleModel = localRenderedImage.getSampleModel();
    numBands = localSampleModel.getNumBands();
    sourceXOffset = localRenderedImage.getMinX();
    sourceYOffset = localRenderedImage.getMinY();
    sourceWidth = localRenderedImage.getWidth();
    sourceHeight = localRenderedImage.getHeight();
    sourceBands = null;
    periodX = 1;
    periodY = 1;
    if (paramImageWriteParam != null)
    {
      Rectangle localRectangle1 = paramImageWriteParam.getSourceRegion();
      if (localRectangle1 != null)
      {
        Rectangle localRectangle2 = new Rectangle(localRenderedImage.getMinX(), localRenderedImage.getMinY(), localRenderedImage.getWidth(), localRenderedImage.getHeight());
        localRectangle1 = localRectangle1.intersection(localRectangle2);
        sourceXOffset = x;
        sourceYOffset = y;
        sourceWidth = width;
        sourceHeight = height;
      }
      j = paramImageWriteParam.getSubsamplingXOffset();
      int k = paramImageWriteParam.getSubsamplingYOffset();
      sourceXOffset += j;
      sourceYOffset += k;
      sourceWidth -= j;
      sourceHeight -= k;
      periodX = paramImageWriteParam.getSourceXSubsampling();
      periodY = paramImageWriteParam.getSourceYSubsampling();
      int[] arrayOfInt = paramImageWriteParam.getSourceBands();
      if (arrayOfInt != null)
      {
        sourceBands = arrayOfInt;
        numBands = sourceBands.length;
      }
    }
    int i = (sourceWidth + periodX - 1) / periodX;
    int j = (sourceHeight + periodY - 1) / periodY;
    if ((i <= 0) || (j <= 0)) {
      throw new IllegalArgumentException("Empty source region!");
    }
    totalPixels = (i * j);
    pixelsDone = 0;
    IIOMetadata localIIOMetadata = paramIIOImage.getMetadata();
    if (localIIOMetadata != null) {
      metadata = ((PNGMetadata)convertImageMetadata(localIIOMetadata, ImageTypeSpecifier.createFromRenderedImage(localRenderedImage), null));
    } else {
      metadata = new PNGMetadata();
    }
    if (paramImageWriteParam != null) {
      switch (paramImageWriteParam.getProgressiveMode())
      {
      case 1: 
        metadata.IHDR_interlaceMethod = 1;
        break;
      case 0: 
        metadata.IHDR_interlaceMethod = 0;
      }
    }
    metadata.initialize(new ImageTypeSpecifier(localRenderedImage), numBands);
    metadata.IHDR_width = i;
    metadata.IHDR_height = j;
    bpp = (numBands * (metadata.IHDR_bitDepth == 16 ? 2 : 1));
    initializeScaleTables(localSampleModel.getSampleSize());
    clearAbortRequest();
    processImageStarted(0);
    try
    {
      write_magic();
      write_IHDR();
      write_cHRM();
      write_gAMA();
      write_iCCP();
      write_sBIT();
      write_sRGB();
      write_PLTE();
      write_hIST();
      write_tRNS();
      write_bKGD();
      write_pHYs();
      write_sPLT();
      write_tIME();
      write_tEXt();
      write_iTXt();
      write_zTXt();
      writeUnknownChunks();
      write_IDAT(localRenderedImage);
      if (abortRequested())
      {
        processWriteAborted();
      }
      else
      {
        writeIEND();
        processImageComplete();
      }
    }
    catch (IOException localIOException)
    {
      throw new IIOException("I/O error writing PNG file!", localIOException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\png\PNGImageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */