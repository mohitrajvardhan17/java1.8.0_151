package com.sun.imageio.plugins.png;

import com.sun.imageio.plugins.common.ReaderUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import sun.awt.image.ByteInterleavedRaster;

public class PNGImageReader
  extends ImageReader
{
  static final int IHDR_TYPE = 1229472850;
  static final int PLTE_TYPE = 1347179589;
  static final int IDAT_TYPE = 1229209940;
  static final int IEND_TYPE = 1229278788;
  static final int bKGD_TYPE = 1649100612;
  static final int cHRM_TYPE = 1665684045;
  static final int gAMA_TYPE = 1732332865;
  static final int hIST_TYPE = 1749635924;
  static final int iCCP_TYPE = 1766015824;
  static final int iTXt_TYPE = 1767135348;
  static final int pHYs_TYPE = 1883789683;
  static final int sBIT_TYPE = 1933723988;
  static final int sPLT_TYPE = 1934642260;
  static final int sRGB_TYPE = 1934772034;
  static final int tEXt_TYPE = 1950701684;
  static final int tIME_TYPE = 1950960965;
  static final int tRNS_TYPE = 1951551059;
  static final int zTXt_TYPE = 2052348020;
  static final int PNG_COLOR_GRAY = 0;
  static final int PNG_COLOR_RGB = 2;
  static final int PNG_COLOR_PALETTE = 3;
  static final int PNG_COLOR_GRAY_ALPHA = 4;
  static final int PNG_COLOR_RGB_ALPHA = 6;
  static final int[] inputBandsForColorType = { 1, -1, 3, 1, 2, -1, 4 };
  static final int PNG_FILTER_NONE = 0;
  static final int PNG_FILTER_SUB = 1;
  static final int PNG_FILTER_UP = 2;
  static final int PNG_FILTER_AVERAGE = 3;
  static final int PNG_FILTER_PAETH = 4;
  static final int[] adam7XOffset = { 0, 4, 0, 2, 0, 1, 0 };
  static final int[] adam7YOffset = { 0, 0, 4, 0, 2, 0, 1 };
  static final int[] adam7XSubsampling = { 8, 8, 4, 4, 2, 2, 1, 1 };
  static final int[] adam7YSubsampling = { 8, 8, 8, 4, 4, 2, 2, 1 };
  private static final boolean debug = true;
  ImageInputStream stream = null;
  boolean gotHeader = false;
  boolean gotMetadata = false;
  ImageReadParam lastParam = null;
  long imageStartPosition = -1L;
  Rectangle sourceRegion = null;
  int sourceXSubsampling = -1;
  int sourceYSubsampling = -1;
  int sourceMinProgressivePass = 0;
  int sourceMaxProgressivePass = 6;
  int[] sourceBands = null;
  int[] destinationBands = null;
  Point destinationOffset = new Point(0, 0);
  PNGMetadata metadata = new PNGMetadata();
  DataInputStream pixelStream = null;
  BufferedImage theImage = null;
  int pixelsDone = 0;
  int totalPixels;
  private static final int[][] bandOffsets = { null, { 0 }, { 0, 1 }, { 0, 1, 2 }, { 0, 1, 2, 3 } };
  
  public PNGImageReader(ImageReaderSpi paramImageReaderSpi)
  {
    super(paramImageReaderSpi);
  }
  
  public void setInput(Object paramObject, boolean paramBoolean1, boolean paramBoolean2)
  {
    super.setInput(paramObject, paramBoolean1, paramBoolean2);
    stream = ((ImageInputStream)paramObject);
    resetStreamSettings();
  }
  
  private String readNullTerminatedString(String paramString, int paramInt)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int j = 0;
    int i;
    while ((paramInt > j++) && ((i = stream.read()) != 0))
    {
      if (i == -1) {
        throw new EOFException();
      }
      localByteArrayOutputStream.write(i);
    }
    return new String(localByteArrayOutputStream.toByteArray(), paramString);
  }
  
  private void readHeader()
    throws IIOException
  {
    if (gotHeader) {
      return;
    }
    if (stream == null) {
      throw new IllegalStateException("Input source not set!");
    }
    try
    {
      byte[] arrayOfByte = new byte[8];
      stream.readFully(arrayOfByte);
      if ((arrayOfByte[0] != -119) || (arrayOfByte[1] != 80) || (arrayOfByte[2] != 78) || (arrayOfByte[3] != 71) || (arrayOfByte[4] != 13) || (arrayOfByte[5] != 10) || (arrayOfByte[6] != 26) || (arrayOfByte[7] != 10)) {
        throw new IIOException("Bad PNG signature!");
      }
      int i = stream.readInt();
      if (i != 13) {
        throw new IIOException("Bad length for IHDR chunk!");
      }
      int j = stream.readInt();
      if (j != 1229472850) {
        throw new IIOException("Bad type for IHDR chunk!");
      }
      metadata = new PNGMetadata();
      int k = stream.readInt();
      int m = stream.readInt();
      stream.readFully(arrayOfByte, 0, 5);
      int n = arrayOfByte[0] & 0xFF;
      int i1 = arrayOfByte[1] & 0xFF;
      int i2 = arrayOfByte[2] & 0xFF;
      int i3 = arrayOfByte[3] & 0xFF;
      int i4 = arrayOfByte[4] & 0xFF;
      stream.skipBytes(4);
      stream.flushBefore(stream.getStreamPosition());
      if (k == 0) {
        throw new IIOException("Image width == 0!");
      }
      if (m == 0) {
        throw new IIOException("Image height == 0!");
      }
      if ((n != 1) && (n != 2) && (n != 4) && (n != 8) && (n != 16)) {
        throw new IIOException("Bit depth must be 1, 2, 4, 8, or 16!");
      }
      if ((i1 != 0) && (i1 != 2) && (i1 != 3) && (i1 != 4) && (i1 != 6)) {
        throw new IIOException("Color type must be 0, 2, 3, 4, or 6!");
      }
      if ((i1 == 3) && (n == 16)) {
        throw new IIOException("Bad color type/bit depth combination!");
      }
      if (((i1 == 2) || (i1 == 6) || (i1 == 4)) && (n != 8) && (n != 16)) {
        throw new IIOException("Bad color type/bit depth combination!");
      }
      if (i2 != 0) {
        throw new IIOException("Unknown compression method (not 0)!");
      }
      if (i3 != 0) {
        throw new IIOException("Unknown filter method (not 0)!");
      }
      if ((i4 != 0) && (i4 != 1)) {
        throw new IIOException("Unknown interlace method (not 0 or 1)!");
      }
      metadata.IHDR_present = true;
      metadata.IHDR_width = k;
      metadata.IHDR_height = m;
      metadata.IHDR_bitDepth = n;
      metadata.IHDR_colorType = i1;
      metadata.IHDR_compressionMethod = i2;
      metadata.IHDR_filterMethod = i3;
      metadata.IHDR_interlaceMethod = i4;
      gotHeader = true;
    }
    catch (IOException localIOException)
    {
      throw new IIOException("I/O error reading PNG header!", localIOException);
    }
  }
  
  private void parse_PLTE_chunk(int paramInt)
    throws IOException
  {
    if (metadata.PLTE_present)
    {
      processWarningOccurred("A PNG image may not contain more than one PLTE chunk.\nThe chunk wil be ignored.");
      return;
    }
    if ((metadata.IHDR_colorType == 0) || (metadata.IHDR_colorType == 4))
    {
      processWarningOccurred("A PNG gray or gray alpha image cannot have a PLTE chunk.\nThe chunk wil be ignored.");
      return;
    }
    byte[] arrayOfByte = new byte[paramInt];
    stream.readFully(arrayOfByte);
    int i = paramInt / 3;
    int j;
    if (metadata.IHDR_colorType == 3)
    {
      j = 1 << metadata.IHDR_bitDepth;
      if (i > j)
      {
        processWarningOccurred("PLTE chunk contains too many entries for bit depth, ignoring extras.");
        i = j;
      }
      i = Math.min(i, j);
    }
    if (i > 16) {
      j = 256;
    } else if (i > 4) {
      j = 16;
    } else if (i > 2) {
      j = 4;
    } else {
      j = 2;
    }
    metadata.PLTE_present = true;
    metadata.PLTE_red = new byte[j];
    metadata.PLTE_green = new byte[j];
    metadata.PLTE_blue = new byte[j];
    int k = 0;
    for (int m = 0; m < i; m++)
    {
      metadata.PLTE_red[m] = arrayOfByte[(k++)];
      metadata.PLTE_green[m] = arrayOfByte[(k++)];
      metadata.PLTE_blue[m] = arrayOfByte[(k++)];
    }
  }
  
  private void parse_bKGD_chunk()
    throws IOException
  {
    if (metadata.IHDR_colorType == 3)
    {
      metadata.bKGD_colorType = 3;
      metadata.bKGD_index = stream.readUnsignedByte();
    }
    else if ((metadata.IHDR_colorType == 0) || (metadata.IHDR_colorType == 4))
    {
      metadata.bKGD_colorType = 0;
      metadata.bKGD_gray = stream.readUnsignedShort();
    }
    else
    {
      metadata.bKGD_colorType = 2;
      metadata.bKGD_red = stream.readUnsignedShort();
      metadata.bKGD_green = stream.readUnsignedShort();
      metadata.bKGD_blue = stream.readUnsignedShort();
    }
    metadata.bKGD_present = true;
  }
  
  private void parse_cHRM_chunk()
    throws IOException
  {
    metadata.cHRM_whitePointX = stream.readInt();
    metadata.cHRM_whitePointY = stream.readInt();
    metadata.cHRM_redX = stream.readInt();
    metadata.cHRM_redY = stream.readInt();
    metadata.cHRM_greenX = stream.readInt();
    metadata.cHRM_greenY = stream.readInt();
    metadata.cHRM_blueX = stream.readInt();
    metadata.cHRM_blueY = stream.readInt();
    metadata.cHRM_present = true;
  }
  
  private void parse_gAMA_chunk()
    throws IOException
  {
    int i = stream.readInt();
    metadata.gAMA_gamma = i;
    metadata.gAMA_present = true;
  }
  
  private void parse_hIST_chunk(int paramInt)
    throws IOException, IIOException
  {
    if (!metadata.PLTE_present) {
      throw new IIOException("hIST chunk without prior PLTE chunk!");
    }
    metadata.hIST_histogram = new char[paramInt / 2];
    stream.readFully(metadata.hIST_histogram, 0, metadata.hIST_histogram.length);
    metadata.hIST_present = true;
  }
  
  private void parse_iCCP_chunk(int paramInt)
    throws IOException
  {
    String str = readNullTerminatedString("ISO-8859-1", 80);
    metadata.iCCP_profileName = str;
    metadata.iCCP_compressionMethod = stream.readUnsignedByte();
    byte[] arrayOfByte = new byte[paramInt - str.length() - 2];
    stream.readFully(arrayOfByte);
    metadata.iCCP_compressedProfile = arrayOfByte;
    metadata.iCCP_present = true;
  }
  
  private void parse_iTXt_chunk(int paramInt)
    throws IOException
  {
    long l1 = stream.getStreamPosition();
    String str1 = readNullTerminatedString("ISO-8859-1", 80);
    metadata.iTXt_keyword.add(str1);
    int i = stream.readUnsignedByte();
    metadata.iTXt_compressionFlag.add(Boolean.valueOf(i == 1));
    int j = stream.readUnsignedByte();
    metadata.iTXt_compressionMethod.add(Integer.valueOf(j));
    String str2 = readNullTerminatedString("UTF8", 80);
    metadata.iTXt_languageTag.add(str2);
    long l2 = stream.getStreamPosition();
    int k = (int)(l1 + paramInt - l2);
    String str3 = readNullTerminatedString("UTF8", k);
    metadata.iTXt_translatedKeyword.add(str3);
    l2 = stream.getStreamPosition();
    byte[] arrayOfByte = new byte[(int)(l1 + paramInt - l2)];
    stream.readFully(arrayOfByte);
    String str4;
    if (i == 1) {
      str4 = new String(inflate(arrayOfByte), "UTF8");
    } else {
      str4 = new String(arrayOfByte, "UTF8");
    }
    metadata.iTXt_text.add(str4);
  }
  
  private void parse_pHYs_chunk()
    throws IOException
  {
    metadata.pHYs_pixelsPerUnitXAxis = stream.readInt();
    metadata.pHYs_pixelsPerUnitYAxis = stream.readInt();
    metadata.pHYs_unitSpecifier = stream.readUnsignedByte();
    metadata.pHYs_present = true;
  }
  
  private void parse_sBIT_chunk()
    throws IOException
  {
    int i = metadata.IHDR_colorType;
    if ((i == 0) || (i == 4))
    {
      metadata.sBIT_grayBits = stream.readUnsignedByte();
    }
    else if ((i == 2) || (i == 3) || (i == 6))
    {
      metadata.sBIT_redBits = stream.readUnsignedByte();
      metadata.sBIT_greenBits = stream.readUnsignedByte();
      metadata.sBIT_blueBits = stream.readUnsignedByte();
    }
    if ((i == 4) || (i == 6)) {
      metadata.sBIT_alphaBits = stream.readUnsignedByte();
    }
    metadata.sBIT_colorType = i;
    metadata.sBIT_present = true;
  }
  
  private void parse_sPLT_chunk(int paramInt)
    throws IOException, IIOException
  {
    metadata.sPLT_paletteName = readNullTerminatedString("ISO-8859-1", 80);
    paramInt -= metadata.sPLT_paletteName.length() + 1;
    int i = stream.readUnsignedByte();
    metadata.sPLT_sampleDepth = i;
    int j = paramInt / (4 * (i / 8) + 2);
    metadata.sPLT_red = new int[j];
    metadata.sPLT_green = new int[j];
    metadata.sPLT_blue = new int[j];
    metadata.sPLT_alpha = new int[j];
    metadata.sPLT_frequency = new int[j];
    int k;
    if (i == 8) {
      for (k = 0; k < j; k++)
      {
        metadata.sPLT_red[k] = stream.readUnsignedByte();
        metadata.sPLT_green[k] = stream.readUnsignedByte();
        metadata.sPLT_blue[k] = stream.readUnsignedByte();
        metadata.sPLT_alpha[k] = stream.readUnsignedByte();
        metadata.sPLT_frequency[k] = stream.readUnsignedShort();
      }
    } else if (i == 16) {
      for (k = 0; k < j; k++)
      {
        metadata.sPLT_red[k] = stream.readUnsignedShort();
        metadata.sPLT_green[k] = stream.readUnsignedShort();
        metadata.sPLT_blue[k] = stream.readUnsignedShort();
        metadata.sPLT_alpha[k] = stream.readUnsignedShort();
        metadata.sPLT_frequency[k] = stream.readUnsignedShort();
      }
    } else {
      throw new IIOException("sPLT sample depth not 8 or 16!");
    }
    metadata.sPLT_present = true;
  }
  
  private void parse_sRGB_chunk()
    throws IOException
  {
    metadata.sRGB_renderingIntent = stream.readUnsignedByte();
    metadata.sRGB_present = true;
  }
  
  private void parse_tEXt_chunk(int paramInt)
    throws IOException
  {
    String str = readNullTerminatedString("ISO-8859-1", 80);
    metadata.tEXt_keyword.add(str);
    byte[] arrayOfByte = new byte[paramInt - str.length() - 1];
    stream.readFully(arrayOfByte);
    metadata.tEXt_text.add(new String(arrayOfByte, "ISO-8859-1"));
  }
  
  private void parse_tIME_chunk()
    throws IOException
  {
    metadata.tIME_year = stream.readUnsignedShort();
    metadata.tIME_month = stream.readUnsignedByte();
    metadata.tIME_day = stream.readUnsignedByte();
    metadata.tIME_hour = stream.readUnsignedByte();
    metadata.tIME_minute = stream.readUnsignedByte();
    metadata.tIME_second = stream.readUnsignedByte();
    metadata.tIME_present = true;
  }
  
  private void parse_tRNS_chunk(int paramInt)
    throws IOException
  {
    int i = metadata.IHDR_colorType;
    if (i == 3)
    {
      if (!metadata.PLTE_present)
      {
        processWarningOccurred("tRNS chunk without prior PLTE chunk, ignoring it.");
        return;
      }
      int j = metadata.PLTE_red.length;
      int k = paramInt;
      if (k > j)
      {
        processWarningOccurred("tRNS chunk has more entries than prior PLTE chunk, ignoring extras.");
        k = j;
      }
      metadata.tRNS_alpha = new byte[k];
      metadata.tRNS_colorType = 3;
      stream.read(metadata.tRNS_alpha, 0, k);
      stream.skipBytes(paramInt - k);
    }
    else if (i == 0)
    {
      if (paramInt != 2)
      {
        processWarningOccurred("tRNS chunk for gray image must have length 2, ignoring chunk.");
        stream.skipBytes(paramInt);
        return;
      }
      metadata.tRNS_gray = stream.readUnsignedShort();
      metadata.tRNS_colorType = 0;
    }
    else if (i == 2)
    {
      if (paramInt != 6)
      {
        processWarningOccurred("tRNS chunk for RGB image must have length 6, ignoring chunk.");
        stream.skipBytes(paramInt);
        return;
      }
      metadata.tRNS_red = stream.readUnsignedShort();
      metadata.tRNS_green = stream.readUnsignedShort();
      metadata.tRNS_blue = stream.readUnsignedShort();
      metadata.tRNS_colorType = 2;
    }
    else
    {
      processWarningOccurred("Gray+Alpha and RGBS images may not have a tRNS chunk, ignoring it.");
      return;
    }
    metadata.tRNS_present = true;
  }
  
  private static byte[] inflate(byte[] paramArrayOfByte)
    throws IOException
  {
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    InflaterInputStream localInflaterInputStream = new InflaterInputStream(localByteArrayInputStream);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      int i;
      while ((i = localInflaterInputStream.read()) != -1) {
        localByteArrayOutputStream.write(i);
      }
    }
    finally
    {
      localInflaterInputStream.close();
    }
    return localByteArrayOutputStream.toByteArray();
  }
  
  private void parse_zTXt_chunk(int paramInt)
    throws IOException
  {
    String str = readNullTerminatedString("ISO-8859-1", 80);
    metadata.zTXt_keyword.add(str);
    int i = stream.readUnsignedByte();
    metadata.zTXt_compressionMethod.add(new Integer(i));
    byte[] arrayOfByte = new byte[paramInt - str.length() - 2];
    stream.readFully(arrayOfByte);
    metadata.zTXt_text.add(new String(inflate(arrayOfByte), "ISO-8859-1"));
  }
  
  private void readMetadata()
    throws IIOException
  {
    if (gotMetadata) {
      return;
    }
    readHeader();
    int i = metadata.IHDR_colorType;
    int m;
    if ((ignoreMetadata) && (i != 3))
    {
      try
      {
        for (;;)
        {
          int j = stream.readInt();
          m = stream.readInt();
          if (m == 1229209940)
          {
            stream.skipBytes(-8);
            imageStartPosition = stream.getStreamPosition();
            break;
          }
          stream.skipBytes(j + 4);
        }
      }
      catch (IOException localIOException1)
      {
        throw new IIOException("Error skipping PNG metadata", localIOException1);
      }
      gotMetadata = true;
      return;
    }
    try
    {
      for (;;)
      {
        int k = stream.readInt();
        m = stream.readInt();
        if (k < 0) {
          throw new IIOException("Invalid chunk lenght " + k);
        }
        int n;
        try
        {
          stream.mark();
          stream.seek(stream.getStreamPosition() + k);
          n = stream.readInt();
          stream.reset();
        }
        catch (IOException localIOException3)
        {
          throw new IIOException("Invalid chunk length " + k);
        }
        switch (m)
        {
        case 1229209940: 
          stream.skipBytes(-8);
          imageStartPosition = stream.getStreamPosition();
          break;
        case 1347179589: 
          parse_PLTE_chunk(k);
          break;
        case 1649100612: 
          parse_bKGD_chunk();
          break;
        case 1665684045: 
          parse_cHRM_chunk();
          break;
        case 1732332865: 
          parse_gAMA_chunk();
          break;
        case 1749635924: 
          parse_hIST_chunk(k);
          break;
        case 1766015824: 
          parse_iCCP_chunk(k);
          break;
        case 1767135348: 
          if (ignoreMetadata) {
            stream.skipBytes(k);
          } else {
            parse_iTXt_chunk(k);
          }
          break;
        case 1883789683: 
          parse_pHYs_chunk();
          break;
        case 1933723988: 
          parse_sBIT_chunk();
          break;
        case 1934642260: 
          parse_sPLT_chunk(k);
          break;
        case 1934772034: 
          parse_sRGB_chunk();
          break;
        case 1950701684: 
          parse_tEXt_chunk(k);
          break;
        case 1950960965: 
          parse_tIME_chunk();
          break;
        case 1951551059: 
          parse_tRNS_chunk(k);
          break;
        case 2052348020: 
          if (ignoreMetadata) {
            stream.skipBytes(k);
          } else {
            parse_zTXt_chunk(k);
          }
          break;
        default: 
          byte[] arrayOfByte = new byte[k];
          stream.readFully(arrayOfByte);
          StringBuilder localStringBuilder = new StringBuilder(4);
          localStringBuilder.append((char)(m >>> 24));
          localStringBuilder.append((char)(m >> 16 & 0xFF));
          localStringBuilder.append((char)(m >> 8 & 0xFF));
          localStringBuilder.append((char)(m & 0xFF));
          int i1 = m >>> 28;
          if (i1 == 0) {
            processWarningOccurred("Encountered unknown chunk with critical bit set!");
          }
          metadata.unknownChunkType.add(localStringBuilder.toString());
          metadata.unknownChunkData.add(arrayOfByte);
        }
        if (n != stream.readInt()) {
          throw new IIOException("Failed to read a chunk of type " + m);
        }
        stream.flushBefore(stream.getStreamPosition());
      }
    }
    catch (IOException localIOException2)
    {
      throw new IIOException("Error reading PNG metadata", localIOException2);
    }
    gotMetadata = true;
  }
  
  private static void decodeSubFilter(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    for (int i = paramInt3; i < paramInt2; i++)
    {
      int j = paramArrayOfByte[(i + paramInt1)] & 0xFF;
      j += (paramArrayOfByte[(i + paramInt1 - paramInt3)] & 0xFF);
      paramArrayOfByte[(i + paramInt1)] = ((byte)j);
    }
  }
  
  private static void decodeUpFilter(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3)
  {
    for (int i = 0; i < paramInt3; i++)
    {
      int j = paramArrayOfByte1[(i + paramInt1)] & 0xFF;
      int k = paramArrayOfByte2[(i + paramInt2)] & 0xFF;
      paramArrayOfByte1[(i + paramInt1)] = ((byte)(j + k));
    }
  }
  
  private static void decodeAverageFilter(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3, int paramInt4)
  {
    int i;
    int k;
    for (int m = 0; m < paramInt4; m++)
    {
      i = paramArrayOfByte1[(m + paramInt1)] & 0xFF;
      k = paramArrayOfByte2[(m + paramInt2)] & 0xFF;
      paramArrayOfByte1[(m + paramInt1)] = ((byte)(i + k / 2));
    }
    for (m = paramInt4; m < paramInt3; m++)
    {
      i = paramArrayOfByte1[(m + paramInt1)] & 0xFF;
      int j = paramArrayOfByte1[(m + paramInt1 - paramInt4)] & 0xFF;
      k = paramArrayOfByte2[(m + paramInt2)] & 0xFF;
      paramArrayOfByte1[(m + paramInt1)] = ((byte)(i + (j + k) / 2));
    }
  }
  
  private static int paethPredictor(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt1 + paramInt2 - paramInt3;
    int j = Math.abs(i - paramInt1);
    int k = Math.abs(i - paramInt2);
    int m = Math.abs(i - paramInt3);
    if ((j <= k) && (j <= m)) {
      return paramInt1;
    }
    if (k <= m) {
      return paramInt2;
    }
    return paramInt3;
  }
  
  private static void decodePaethFilter(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3, int paramInt4)
  {
    int i;
    int k;
    for (int n = 0; n < paramInt4; n++)
    {
      i = paramArrayOfByte1[(n + paramInt1)] & 0xFF;
      k = paramArrayOfByte2[(n + paramInt2)] & 0xFF;
      paramArrayOfByte1[(n + paramInt1)] = ((byte)(i + k));
    }
    for (n = paramInt4; n < paramInt3; n++)
    {
      i = paramArrayOfByte1[(n + paramInt1)] & 0xFF;
      int j = paramArrayOfByte1[(n + paramInt1 - paramInt4)] & 0xFF;
      k = paramArrayOfByte2[(n + paramInt2)] & 0xFF;
      int m = paramArrayOfByte2[(n + paramInt2 - paramInt4)] & 0xFF;
      paramArrayOfByte1[(n + paramInt1)] = ((byte)(i + paethPredictor(j, k, m)));
    }
  }
  
  private WritableRaster createRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    WritableRaster localWritableRaster = null;
    Point localPoint = new Point(0, 0);
    Object localObject;
    if ((paramInt5 < 8) && (paramInt3 == 1))
    {
      localObject = new DataBufferByte(paramInt2 * paramInt4);
      localWritableRaster = Raster.createPackedRaster((DataBuffer)localObject, paramInt1, paramInt2, paramInt5, localPoint);
    }
    else if (paramInt5 <= 8)
    {
      localObject = new DataBufferByte(paramInt2 * paramInt4);
      localWritableRaster = Raster.createInterleavedRaster((DataBuffer)localObject, paramInt1, paramInt2, paramInt4, paramInt3, bandOffsets[paramInt3], localPoint);
    }
    else
    {
      localObject = new DataBufferUShort(paramInt2 * paramInt4);
      localWritableRaster = Raster.createInterleavedRaster((DataBuffer)localObject, paramInt1, paramInt2, paramInt4, paramInt3, bandOffsets[paramInt3], localPoint);
    }
    return localWritableRaster;
  }
  
  private void skipPass(int paramInt1, int paramInt2)
    throws IOException, IIOException
  {
    if ((paramInt1 == 0) || (paramInt2 == 0)) {
      return;
    }
    int i = inputBandsForColorType[metadata.IHDR_colorType];
    int j = (i * paramInt1 * metadata.IHDR_bitDepth + 7) / 8;
    for (int k = 0; k < paramInt2; k++)
    {
      pixelStream.skipBytes(1 + j);
      if (abortRequested()) {
        return;
      }
    }
  }
  
  private void updateImageProgress(int paramInt)
  {
    pixelsDone += paramInt;
    processImageProgress(100.0F * pixelsDone / totalPixels);
  }
  
  private void decodePass(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
    throws IOException
  {
    if ((paramInt6 == 0) || (paramInt7 == 0)) {
      return;
    }
    WritableRaster localWritableRaster1 = theImage.getWritableTile(0, 0);
    int i = localWritableRaster1.getMinX();
    int j = i + localWritableRaster1.getWidth() - 1;
    int k = localWritableRaster1.getMinY();
    int m = k + localWritableRaster1.getHeight() - 1;
    int[] arrayOfInt1 = ReaderUtil.computeUpdatedPixels(sourceRegion, destinationOffset, i, k, j, m, sourceXSubsampling, sourceYSubsampling, paramInt2, paramInt3, paramInt6, paramInt7, paramInt4, paramInt5);
    int n = arrayOfInt1[0];
    int i1 = arrayOfInt1[1];
    int i2 = arrayOfInt1[2];
    int i3 = arrayOfInt1[4];
    int i4 = arrayOfInt1[5];
    int i5 = metadata.IHDR_bitDepth;
    int i6 = inputBandsForColorType[metadata.IHDR_colorType];
    int i7 = i5 == 16 ? 2 : 1;
    i7 *= i6;
    int i8 = (i6 * paramInt6 * i5 + 7) / 8;
    int i9 = i5 == 16 ? i8 / 2 : i8;
    if (i2 == 0)
    {
      for (i10 = 0; i10 < paramInt7; i10++)
      {
        updateImageProgress(paramInt6);
        pixelStream.skipBytes(1 + i8);
      }
      return;
    }
    int i10 = (n - destinationOffset.x) * sourceXSubsampling + sourceRegion.x;
    int i11 = (i10 - paramInt2) / paramInt4;
    int i12 = i3 * sourceXSubsampling / paramInt4;
    byte[] arrayOfByte = null;
    short[] arrayOfShort = null;
    Object localObject1 = new byte[i8];
    Object localObject2 = new byte[i8];
    WritableRaster localWritableRaster2 = createRaster(paramInt6, 1, i6, i9, i5);
    int[] arrayOfInt2 = localWritableRaster2.getPixel(0, 0, (int[])null);
    DataBuffer localDataBuffer = localWritableRaster2.getDataBuffer();
    int i13 = localDataBuffer.getDataType();
    if (i13 == 0) {
      arrayOfByte = ((DataBufferByte)localDataBuffer).getData();
    } else {
      arrayOfShort = ((DataBufferUShort)localDataBuffer).getData();
    }
    processPassStarted(theImage, paramInt1, sourceMinProgressivePass, sourceMaxProgressivePass, n, i1, i3, i4, destinationBands);
    if (sourceBands != null) {
      localWritableRaster2 = localWritableRaster2.createWritableChild(0, 0, localWritableRaster2.getWidth(), 1, 0, 0, sourceBands);
    }
    if (destinationBands != null) {
      localWritableRaster1 = localWritableRaster1.createWritableChild(0, 0, localWritableRaster1.getWidth(), localWritableRaster1.getHeight(), 0, 0, destinationBands);
    }
    int i14 = 0;
    int[] arrayOfInt3 = localWritableRaster1.getSampleModel().getSampleSize();
    int i15 = arrayOfInt3.length;
    for (int i16 = 0; i16 < i15; i16++) {
      if (arrayOfInt3[i16] != i5)
      {
        i14 = 1;
        break;
      }
    }
    int[][] arrayOfInt = (int[][])null;
    int i19;
    int i22;
    if (i14 != 0)
    {
      i17 = (1 << i5) - 1;
      i18 = i17 / 2;
      arrayOfInt = new int[i15][];
      for (i19 = 0; i19 < i15; i19++)
      {
        int i20 = (1 << arrayOfInt3[i19]) - 1;
        arrayOfInt[i19] = new int[i17 + 1];
        for (i22 = 0; i22 <= i17; i22++) {
          arrayOfInt[i19][i22] = ((i22 * i20 + i18) / i17);
        }
      }
    }
    int i17 = (i12 == 1) && (i3 == 1) && (i14 == 0) && ((localWritableRaster1 instanceof ByteInterleavedRaster)) ? 1 : 0;
    if (i17 != 0) {
      localWritableRaster2 = localWritableRaster2.createWritableChild(i11, 0, i2, 1, 0, 0, null);
    }
    for (int i18 = 0; i18 < paramInt7; i18++)
    {
      updateImageProgress(paramInt6);
      i19 = pixelStream.read();
      try
      {
        Object localObject3 = localObject2;
        localObject2 = localObject1;
        localObject1 = localObject3;
        pixelStream.readFully((byte[])localObject1, 0, i8);
      }
      catch (ZipException localZipException)
      {
        throw localZipException;
      }
      switch (i19)
      {
      case 0: 
        break;
      case 1: 
        decodeSubFilter((byte[])localObject1, 0, i8, i7);
        break;
      case 2: 
        decodeUpFilter((byte[])localObject1, 0, (byte[])localObject2, 0, i8);
        break;
      case 3: 
        decodeAverageFilter((byte[])localObject1, 0, (byte[])localObject2, 0, i8, i7);
        break;
      case 4: 
        decodePaethFilter((byte[])localObject1, 0, (byte[])localObject2, 0, i8, i7);
        break;
      default: 
        throw new IIOException("Unknown row filter type (= " + i19 + ")!");
      }
      if (i5 < 16)
      {
        System.arraycopy(localObject1, 0, arrayOfByte, 0, i8);
      }
      else
      {
        i21 = 0;
        for (i22 = 0; i22 < i9; i22++)
        {
          arrayOfShort[i22] = ((short)(localObject1[i21] << 8 | localObject1[(i21 + 1)] & 0xFF));
          i21 += 2;
        }
      }
      int i21 = i18 * paramInt5 + paramInt3;
      if ((i21 >= sourceRegion.y) && (i21 < sourceRegion.y + sourceRegion.height) && ((i21 - sourceRegion.y) % sourceYSubsampling == 0))
      {
        i22 = destinationOffset.y + (i21 - sourceRegion.y) / sourceYSubsampling;
        if (i22 >= k)
        {
          if (i22 > m) {
            break;
          }
          if (i17 != 0)
          {
            localWritableRaster1.setRect(n, i22, localWritableRaster2);
          }
          else
          {
            int i23 = i11;
            int i24 = n;
            while (i24 < n + i2)
            {
              localWritableRaster2.getPixel(i23, 0, arrayOfInt2);
              if (i14 != 0) {
                for (int i25 = 0; i25 < i15; i25++) {
                  arrayOfInt2[i25] = arrayOfInt[i25][arrayOfInt2[i25]];
                }
              }
              localWritableRaster1.setPixel(i24, i22, arrayOfInt2);
              i23 += i12;
              i24 += i3;
            }
          }
          processImageUpdate(theImage, n, i22, i2, 1, i3, i4, destinationBands);
          if (abortRequested()) {
            return;
          }
        }
      }
    }
    processPassComplete(theImage);
  }
  
  private void decodeImage()
    throws IOException, IIOException
  {
    int i = metadata.IHDR_width;
    int j = metadata.IHDR_height;
    pixelsDone = 0;
    totalPixels = (i * j);
    clearAbortRequest();
    if (metadata.IHDR_interlaceMethod == 0) {
      decodePass(0, 0, 0, 1, 1, i, j);
    } else {
      for (int k = 0; k <= sourceMaxProgressivePass; k++)
      {
        int m = adam7XOffset[k];
        int n = adam7YOffset[k];
        int i1 = adam7XSubsampling[k];
        int i2 = adam7YSubsampling[k];
        int i3 = adam7XSubsampling[(k + 1)] - 1;
        int i4 = adam7YSubsampling[(k + 1)] - 1;
        if (k >= sourceMinProgressivePass) {
          decodePass(k, m, n, i1, i2, (i + i3) / i1, (j + i4) / i2);
        } else {
          skipPass((i + i3) / i1, (j + i4) / i2);
        }
        if (abortRequested()) {
          return;
        }
      }
    }
  }
  
  private void readImage(ImageReadParam paramImageReadParam)
    throws IIOException
  {
    readMetadata();
    int i = metadata.IHDR_width;
    int j = metadata.IHDR_height;
    sourceXSubsampling = 1;
    sourceYSubsampling = 1;
    sourceMinProgressivePass = 0;
    sourceMaxProgressivePass = 6;
    sourceBands = null;
    destinationBands = null;
    destinationOffset = new Point(0, 0);
    if (paramImageReadParam != null)
    {
      sourceXSubsampling = paramImageReadParam.getSourceXSubsampling();
      sourceYSubsampling = paramImageReadParam.getSourceYSubsampling();
      sourceMinProgressivePass = Math.max(paramImageReadParam.getSourceMinProgressivePass(), 0);
      sourceMaxProgressivePass = Math.min(paramImageReadParam.getSourceMaxProgressivePass(), 6);
      sourceBands = paramImageReadParam.getSourceBands();
      destinationBands = paramImageReadParam.getDestinationBands();
      destinationOffset = paramImageReadParam.getDestinationOffset();
    }
    Inflater localInflater = null;
    try
    {
      stream.seek(imageStartPosition);
      PNGImageDataEnumeration localPNGImageDataEnumeration = new PNGImageDataEnumeration(stream);
      Object localObject1 = new SequenceInputStream(localPNGImageDataEnumeration);
      localInflater = new Inflater();
      localObject1 = new InflaterInputStream((InputStream)localObject1, localInflater);
      localObject1 = new BufferedInputStream((InputStream)localObject1);
      pixelStream = new DataInputStream((InputStream)localObject1);
      theImage = getDestination(paramImageReadParam, getImageTypes(0), i, j);
      Rectangle localRectangle = new Rectangle(0, 0, 0, 0);
      sourceRegion = new Rectangle(0, 0, 0, 0);
      computeRegions(paramImageReadParam, i, j, theImage, sourceRegion, localRectangle);
      destinationOffset.setLocation(localRectangle.getLocation());
      int k = metadata.IHDR_colorType;
      checkReadParamBandSettings(paramImageReadParam, inputBandsForColorType[k], theImage.getSampleModel().getNumBands());
      processImageStarted(0);
      decodeImage();
      if (abortRequested()) {
        processReadAborted();
      } else {
        processImageComplete();
      }
    }
    catch (IOException localIOException)
    {
      throw new IIOException("Error reading PNG image data", localIOException);
    }
    finally
    {
      if (localInflater != null) {
        localInflater.end();
      }
    }
  }
  
  public int getNumImages(boolean paramBoolean)
    throws IIOException
  {
    if (stream == null) {
      throw new IllegalStateException("No input source set!");
    }
    if ((seekForwardOnly) && (paramBoolean)) {
      throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!");
    }
    return 1;
  }
  
  public int getWidth(int paramInt)
    throws IIOException
  {
    if (paramInt != 0) {
      throw new IndexOutOfBoundsException("imageIndex != 0!");
    }
    readHeader();
    return metadata.IHDR_width;
  }
  
  public int getHeight(int paramInt)
    throws IIOException
  {
    if (paramInt != 0) {
      throw new IndexOutOfBoundsException("imageIndex != 0!");
    }
    readHeader();
    return metadata.IHDR_height;
  }
  
  public Iterator<ImageTypeSpecifier> getImageTypes(int paramInt)
    throws IIOException
  {
    if (paramInt != 0) {
      throw new IndexOutOfBoundsException("imageIndex != 0!");
    }
    readHeader();
    ArrayList localArrayList = new ArrayList(1);
    int i = metadata.IHDR_bitDepth;
    int j = metadata.IHDR_colorType;
    int k;
    if (i <= 8) {
      k = 0;
    } else {
      k = 1;
    }
    ColorSpace localColorSpace1;
    int[] arrayOfInt;
    switch (j)
    {
    case 0: 
      localArrayList.add(ImageTypeSpecifier.createGrayscale(i, k, false));
      break;
    case 2: 
      if (i == 8)
      {
        localArrayList.add(ImageTypeSpecifier.createFromBufferedImageType(5));
        localArrayList.add(ImageTypeSpecifier.createFromBufferedImageType(1));
        localArrayList.add(ImageTypeSpecifier.createFromBufferedImageType(4));
      }
      localColorSpace1 = ColorSpace.getInstance(1000);
      arrayOfInt = new int[3];
      arrayOfInt[0] = 0;
      arrayOfInt[1] = 1;
      arrayOfInt[2] = 2;
      localArrayList.add(ImageTypeSpecifier.createInterleaved(localColorSpace1, arrayOfInt, k, false, false));
      break;
    case 3: 
      readMetadata();
      int m = 1 << i;
      byte[] arrayOfByte1 = metadata.PLTE_red;
      byte[] arrayOfByte2 = metadata.PLTE_green;
      byte[] arrayOfByte3 = metadata.PLTE_blue;
      if (metadata.PLTE_red.length < m)
      {
        arrayOfByte1 = Arrays.copyOf(metadata.PLTE_red, m);
        Arrays.fill(arrayOfByte1, metadata.PLTE_red.length, m, metadata.PLTE_red[(metadata.PLTE_red.length - 1)]);
        arrayOfByte2 = Arrays.copyOf(metadata.PLTE_green, m);
        Arrays.fill(arrayOfByte2, metadata.PLTE_green.length, m, metadata.PLTE_green[(metadata.PLTE_green.length - 1)]);
        arrayOfByte3 = Arrays.copyOf(metadata.PLTE_blue, m);
        Arrays.fill(arrayOfByte3, metadata.PLTE_blue.length, m, metadata.PLTE_blue[(metadata.PLTE_blue.length - 1)]);
      }
      byte[] arrayOfByte4 = null;
      if ((metadata.tRNS_present) && (metadata.tRNS_alpha != null)) {
        if (metadata.tRNS_alpha.length == arrayOfByte1.length)
        {
          arrayOfByte4 = metadata.tRNS_alpha;
        }
        else
        {
          arrayOfByte4 = Arrays.copyOf(metadata.tRNS_alpha, arrayOfByte1.length);
          Arrays.fill(arrayOfByte4, metadata.tRNS_alpha.length, arrayOfByte1.length, (byte)-1);
        }
      }
      localArrayList.add(ImageTypeSpecifier.createIndexed(arrayOfByte1, arrayOfByte2, arrayOfByte3, arrayOfByte4, i, 0));
      break;
    case 4: 
      ColorSpace localColorSpace2 = ColorSpace.getInstance(1003);
      arrayOfInt = new int[2];
      arrayOfInt[0] = 0;
      arrayOfInt[1] = 1;
      localArrayList.add(ImageTypeSpecifier.createInterleaved(localColorSpace2, arrayOfInt, k, true, false));
      break;
    case 6: 
      if (i == 8)
      {
        localArrayList.add(ImageTypeSpecifier.createFromBufferedImageType(6));
        localArrayList.add(ImageTypeSpecifier.createFromBufferedImageType(2));
      }
      localColorSpace1 = ColorSpace.getInstance(1000);
      arrayOfInt = new int[4];
      arrayOfInt[0] = 0;
      arrayOfInt[1] = 1;
      arrayOfInt[2] = 2;
      arrayOfInt[3] = 3;
      localArrayList.add(ImageTypeSpecifier.createInterleaved(localColorSpace1, arrayOfInt, k, true, false));
      break;
    }
    return localArrayList.iterator();
  }
  
  public ImageTypeSpecifier getRawImageType(int paramInt)
    throws IOException
  {
    Iterator localIterator = getImageTypes(paramInt);
    ImageTypeSpecifier localImageTypeSpecifier = null;
    do
    {
      localImageTypeSpecifier = (ImageTypeSpecifier)localIterator.next();
    } while (localIterator.hasNext());
    return localImageTypeSpecifier;
  }
  
  public ImageReadParam getDefaultReadParam()
  {
    return new ImageReadParam();
  }
  
  public IIOMetadata getStreamMetadata()
    throws IIOException
  {
    return null;
  }
  
  public IIOMetadata getImageMetadata(int paramInt)
    throws IIOException
  {
    if (paramInt != 0) {
      throw new IndexOutOfBoundsException("imageIndex != 0!");
    }
    readMetadata();
    return metadata;
  }
  
  public BufferedImage read(int paramInt, ImageReadParam paramImageReadParam)
    throws IIOException
  {
    if (paramInt != 0) {
      throw new IndexOutOfBoundsException("imageIndex != 0!");
    }
    readImage(paramImageReadParam);
    return theImage;
  }
  
  public void reset()
  {
    super.reset();
    resetStreamSettings();
  }
  
  private void resetStreamSettings()
  {
    gotHeader = false;
    gotMetadata = false;
    metadata = null;
    pixelStream = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\png\PNGImageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */