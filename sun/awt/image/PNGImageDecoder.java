package sun.awt.image;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class PNGImageDecoder
  extends ImageDecoder
{
  private static final int GRAY = 0;
  private static final int PALETTE = 1;
  private static final int COLOR = 2;
  private static final int ALPHA = 4;
  private static final int bKGDChunk = 1649100612;
  private static final int cHRMChunk = 1665684045;
  private static final int gAMAChunk = 1732332865;
  private static final int hISTChunk = 1749635924;
  private static final int IDATChunk = 1229209940;
  private static final int IENDChunk = 1229278788;
  private static final int IHDRChunk = 1229472850;
  private static final int PLTEChunk = 1347179589;
  private static final int pHYsChunk = 1883789683;
  private static final int sBITChunk = 1933723988;
  private static final int tEXtChunk = 1950701684;
  private static final int tIMEChunk = 1950960965;
  private static final int tRNSChunk = 1951551059;
  private static final int zTXtChunk = 2052348020;
  private int width;
  private int height;
  private int bitDepth;
  private int colorType;
  private int compressionMethod;
  private int filterMethod;
  private int interlaceMethod;
  private int gamma = 100000;
  private Hashtable properties;
  private ColorModel cm;
  private byte[] red_map;
  private byte[] green_map;
  private byte[] blue_map;
  private byte[] alpha_map;
  private int transparentPixel = -1;
  private byte[] transparentPixel_16 = null;
  private static ColorModel[] greyModels = new ColorModel[4];
  private static final byte[] startingRow = { 0, 0, 0, 4, 0, 2, 0, 1 };
  private static final byte[] startingCol = { 0, 0, 4, 0, 2, 0, 1, 0 };
  private static final byte[] rowIncrement = { 1, 8, 8, 8, 4, 4, 2, 2 };
  private static final byte[] colIncrement = { 1, 8, 8, 4, 4, 2, 2, 1 };
  private static final byte[] blockHeight = { 1, 8, 8, 4, 4, 2, 2, 1 };
  private static final byte[] blockWidth = { 1, 8, 4, 4, 2, 2, 1, 1 };
  int pos;
  int limit;
  int chunkStart;
  int chunkKey;
  int chunkLength;
  int chunkCRC;
  boolean seenEOF;
  private static final byte[] signature = { -119, 80, 78, 71, 13, 10, 26, 10 };
  PNGFilterInputStream inputStream;
  InputStream underlyingInputStream;
  byte[] inbuf = new byte['က'];
  private static boolean checkCRC = true;
  private static final int[] crc_table = new int['Ā'];
  
  private void property(String paramString, Object paramObject)
  {
    if (paramObject == null) {
      return;
    }
    if (properties == null) {
      properties = new Hashtable();
    }
    properties.put(paramString, paramObject);
  }
  
  private void property(String paramString, float paramFloat)
  {
    property(paramString, new Float(paramFloat));
  }
  
  private final void pngassert(boolean paramBoolean)
    throws IOException
  {
    if (!paramBoolean)
    {
      PNGException localPNGException = new PNGException("Broken file");
      localPNGException.printStackTrace();
      throw localPNGException;
    }
  }
  
  protected boolean handleChunk(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    throws IOException
  {
    int i;
    int j;
    switch (paramInt1)
    {
    case 1649100612: 
      Color localColor = null;
      switch (colorType)
      {
      case 2: 
      case 6: 
        pngassert(paramInt3 == 6);
        localColor = new Color(paramArrayOfByte[paramInt2] & 0xFF, paramArrayOfByte[(paramInt2 + 2)] & 0xFF, paramArrayOfByte[(paramInt2 + 4)] & 0xFF);
        break;
      case 3: 
      case 7: 
        pngassert(paramInt3 == 1);
        i = paramArrayOfByte[paramInt2] & 0xFF;
        pngassert((red_map != null) && (i < red_map.length));
        localColor = new Color(red_map[i] & 0xFF, green_map[i] & 0xFF, blue_map[i] & 0xFF);
        break;
      case 0: 
      case 4: 
        pngassert(paramInt3 == 2);
        j = paramArrayOfByte[paramInt2] & 0xFF;
        localColor = new Color(j, j, j);
      }
      if (localColor != null) {
        property("background", localColor);
      }
      break;
    case 1665684045: 
      property("chromaticities", new Chromaticities(getInt(paramInt2), getInt(paramInt2 + 4), getInt(paramInt2 + 8), getInt(paramInt2 + 12), getInt(paramInt2 + 16), getInt(paramInt2 + 20), getInt(paramInt2 + 24), getInt(paramInt2 + 28)));
      break;
    case 1732332865: 
      if (paramInt3 != 4) {
        throw new PNGException("bogus gAMA");
      }
      gamma = getInt(paramInt2);
      if (gamma != 100000) {
        property("gamma", gamma / 100000.0F);
      }
      break;
    case 1749635924: 
      break;
    case 1229209940: 
      return false;
    case 1229278788: 
      break;
    case 1229472850: 
      if ((paramInt3 != 13) || ((width = getInt(paramInt2)) == 0) || ((height = getInt(paramInt2 + 4)) == 0)) {
        throw new PNGException("bogus IHDR");
      }
      bitDepth = getByte(paramInt2 + 8);
      colorType = getByte(paramInt2 + 9);
      compressionMethod = getByte(paramInt2 + 10);
      filterMethod = getByte(paramInt2 + 11);
      interlaceMethod = getByte(paramInt2 + 12);
      break;
    case 1347179589: 
      i = paramInt3 / 3;
      red_map = new byte[i];
      green_map = new byte[i];
      blue_map = new byte[i];
      j = 0;
      for (int m = paramInt2; j < i; m += 3)
      {
        red_map[j] = paramArrayOfByte[m];
        green_map[j] = paramArrayOfByte[(m + 1)];
        blue_map[j] = paramArrayOfByte[(m + 2)];
        j++;
      }
      break;
    case 1883789683: 
      break;
    case 1933723988: 
      break;
    case 1950701684: 
      for (i = 0; (i < paramInt3) && (paramArrayOfByte[(paramInt2 + i)] != 0); i++) {}
      if (i < paramInt3)
      {
        String str1 = new String(paramArrayOfByte, paramInt2, i);
        String str2 = new String(paramArrayOfByte, paramInt2 + i + 1, paramInt3 - i - 1);
        property(str1, str2);
      }
      break;
    case 1950960965: 
      property("modtime", new GregorianCalendar(getShort(paramInt2 + 0), getByte(paramInt2 + 2) - 1, getByte(paramInt2 + 3), getByte(paramInt2 + 4), getByte(paramInt2 + 5), getByte(paramInt2 + 6)).getTime());
      break;
    case 1951551059: 
      int n;
      switch (colorType)
      {
      case 3: 
      case 7: 
        int k = paramInt3;
        if (red_map != null) {
          k = red_map.length;
        }
        alpha_map = new byte[k];
        System.arraycopy(paramArrayOfByte, paramInt2, alpha_map, 0, paramInt3 < k ? paramInt3 : k);
        for (;;)
        {
          k--;
          if (k < paramInt3) {
            break;
          }
          alpha_map[k] = -1;
        }
      case 2: 
      case 6: 
        pngassert(paramInt3 == 6);
        if (bitDepth == 16)
        {
          transparentPixel_16 = new byte[6];
          for (n = 0; n < 6; n++) {
            transparentPixel_16[n] = ((byte)getByte(paramInt2 + n));
          }
        }
        else
        {
          transparentPixel = ((getShort(paramInt2 + 0) & 0xFF) << 16 | (getShort(paramInt2 + 2) & 0xFF) << 8 | getShort(paramInt2 + 4) & 0xFF);
        }
        break;
      case 0: 
      case 4: 
        pngassert(paramInt3 == 2);
        n = getShort(paramInt2);
        n = 0xFF & (bitDepth == 16 ? n >> 8 : n);
        transparentPixel = (n << 16 | n << 8 | n);
      }
      break;
    }
    return true;
  }
  
  public void produceImage()
    throws IOException, ImageFormatException
  {
    try
    {
      for (int i = 0; i < signature.length; i++) {
        if ((signature[i] & 0xFF) != underlyingInputStream.read()) {
          throw new PNGException("Chunk signature mismatch");
        }
      }
      BufferedInputStream localBufferedInputStream = new BufferedInputStream(new InflaterInputStream(inputStream, new Inflater()));
      getData();
      byte[] arrayOfByte1 = null;
      int[] arrayOfInt = null;
      int j = width;
      int m = 0;
      switch (bitDepth)
      {
      case 1: 
        m = 0;
        break;
      case 2: 
        m = 1;
        break;
      case 4: 
        m = 2;
        break;
      case 8: 
        m = 3;
        break;
      case 16: 
        m = 4;
        break;
      default: 
        throw new PNGException("invalid depth");
      }
      int k;
      if (interlaceMethod != 0)
      {
        j *= height;
        k = width;
      }
      else
      {
        k = 0;
      }
      int n = colorType | bitDepth << 3;
      int i1 = (1 << (bitDepth >= 8 ? 8 : bitDepth)) - 1;
      switch (colorType)
      {
      case 3: 
      case 7: 
        if (red_map == null) {
          throw new PNGException("palette expected");
        }
        if (alpha_map == null) {
          cm = new IndexColorModel(bitDepth, red_map.length, red_map, green_map, blue_map);
        } else {
          cm = new IndexColorModel(bitDepth, red_map.length, red_map, green_map, blue_map, alpha_map);
        }
        arrayOfByte1 = new byte[j];
        break;
      case 0: 
        i2 = m >= 4 ? 3 : m;
        if ((cm = greyModels[i2]) == null)
        {
          i3 = 1 << (1 << i2);
          byte[] arrayOfByte2 = new byte[i3];
          for (i5 = 0; i5 < i3; i5++) {
            arrayOfByte2[i5] = ((byte)(255 * i5 / (i3 - 1)));
          }
          if (transparentPixel == -1) {
            cm = new IndexColorModel(bitDepth, arrayOfByte2.length, arrayOfByte2, arrayOfByte2, arrayOfByte2);
          } else {
            cm = new IndexColorModel(bitDepth, arrayOfByte2.length, arrayOfByte2, arrayOfByte2, arrayOfByte2, transparentPixel & 0xFF);
          }
          greyModels[i2] = cm;
        }
        arrayOfByte1 = new byte[j];
        break;
      case 2: 
      case 4: 
      case 6: 
        cm = ColorModel.getRGBdefault();
        arrayOfInt = new int[j];
        break;
      case 1: 
      case 5: 
      default: 
        throw new PNGException("invalid color type");
      }
      setDimensions(width, height);
      setColorModel(cm);
      int i2 = interlaceMethod != 0 ? 6 : 30;
      setHints(i2);
      headerComplete();
      int i3 = (colorType & 0x1) != 0 ? 1 : ((colorType & 0x2) != 0 ? 3 : 1) + ((colorType & 0x4) != 0 ? 1 : 0);
      int i4 = i3 * bitDepth;
      int i5 = i4 + 7 >> 3;
      int i6;
      int i7;
      if (interlaceMethod == 0)
      {
        i6 = -1;
        i7 = 0;
      }
      else
      {
        i6 = 0;
        i7 = 7;
      }
      for (;;)
      {
        i6++;
        if (i6 > i7) {
          break;
        }
        int i8 = startingRow[i6];
        int i9 = rowIncrement[i6];
        int i10 = colIncrement[i6];
        int i11 = blockWidth[i6];
        int i12 = blockHeight[i6];
        int i13 = startingCol[i6];
        int i14 = (width - i13 + (i10 - 1)) / i10;
        int i15 = i14 * i4 + 7 >> 3;
        if (i15 != 0)
        {
          int i16 = interlaceMethod == 0 ? i9 * width : 0;
          int i17 = k * i8;
          int i18 = 1;
          Object localObject1 = new byte[i15];
          Object localObject2 = new byte[i15];
          while (i8 < height)
          {
            int i19 = localBufferedInputStream.read();
            int i20 = 0;
            while (i20 < i15)
            {
              i21 = localBufferedInputStream.read((byte[])localObject1, i20, i15 - i20);
              if (i21 <= 0) {
                throw new PNGException("missing data");
              }
              i20 += i21;
            }
            filterRow((byte[])localObject1, i18 != 0 ? null : (byte[])localObject2, i19, i15, i5);
            i20 = i13;
            int i21 = 0;
            int i22 = 0;
            while (i20 < width)
            {
              if (arrayOfInt != null)
              {
                int i24;
                switch (n)
                {
                case 70: 
                  arrayOfInt[(i20 + i17)] = ((localObject1[i21] & 0xFF) << 16 | (localObject1[(i21 + 1)] & 0xFF) << 8 | localObject1[(i21 + 2)] & 0xFF | (localObject1[(i21 + 3)] & 0xFF) << 24);
                  i21 += 4;
                  break;
                case 134: 
                  arrayOfInt[(i20 + i17)] = ((localObject1[i21] & 0xFF) << 16 | (localObject1[(i21 + 2)] & 0xFF) << 8 | localObject1[(i21 + 4)] & 0xFF | (localObject1[(i21 + 6)] & 0xFF) << 24);
                  i21 += 8;
                  break;
                case 66: 
                  i22 = (localObject1[i21] & 0xFF) << 16 | (localObject1[(i21 + 1)] & 0xFF) << 8 | localObject1[(i21 + 2)] & 0xFF;
                  if (i22 != transparentPixel) {
                    i22 |= 0xFF000000;
                  }
                  arrayOfInt[(i20 + i17)] = i22;
                  i21 += 3;
                  break;
                case 130: 
                  i22 = (localObject1[i21] & 0xFF) << 16 | (localObject1[(i21 + 2)] & 0xFF) << 8 | localObject1[(i21 + 4)] & 0xFF;
                  int i23 = transparentPixel_16 != null ? 1 : 0;
                  for (i24 = 0; (i23 != 0) && (i24 < 6); i24++) {
                    i23 &= ((localObject1[(i21 + i24)] & 0xFF) == (transparentPixel_16[i24] & 0xFF) ? 1 : 0);
                  }
                  if (i23 == 0) {
                    i22 |= 0xFF000000;
                  }
                  arrayOfInt[(i20 + i17)] = i22;
                  i21 += 6;
                  break;
                case 68: 
                  i24 = localObject1[i21] & 0xFF;
                  arrayOfInt[(i20 + i17)] = (i24 << 16 | i24 << 8 | i24 | (localObject1[(i21 + 1)] & 0xFF) << 24);
                  i21 += 2;
                  break;
                case 132: 
                  i24 = localObject1[i21] & 0xFF;
                  arrayOfInt[(i20 + i17)] = (i24 << 16 | i24 << 8 | i24 | (localObject1[(i21 + 2)] & 0xFF) << 24);
                  i21 += 4;
                  break;
                default: 
                  throw new PNGException("illegal type/depth");
                }
              }
              else
              {
                switch (bitDepth)
                {
                case 1: 
                  arrayOfByte1[(i20 + i17)] = ((byte)(localObject1[(i21 >> 3)] >> 7 - (i21 & 0x7) & 0x1));
                  i21++;
                  break;
                case 2: 
                  arrayOfByte1[(i20 + i17)] = ((byte)(localObject1[(i21 >> 2)] >> (3 - (i21 & 0x3)) * 2 & 0x3));
                  i21++;
                  break;
                case 4: 
                  arrayOfByte1[(i20 + i17)] = ((byte)(localObject1[(i21 >> 1)] >> (1 - (i21 & 0x1)) * 4 & 0xF));
                  i21++;
                  break;
                case 8: 
                  arrayOfByte1[(i20 + i17)] = localObject1[(i21++)];
                  break;
                case 16: 
                  arrayOfByte1[(i20 + i17)] = localObject1[i21];
                  i21 += 2;
                  break;
                default: 
                  throw new PNGException("illegal type/depth");
                }
              }
              i20 += i10;
            }
            if (interlaceMethod == 0) {
              if (arrayOfInt != null) {
                sendPixels(0, i8, width, 1, arrayOfInt, 0, width);
              } else {
                sendPixels(0, i8, width, 1, arrayOfByte1, 0, width);
              }
            }
            i8 += i9;
            i17 += i9 * k;
            Object localObject3 = localObject1;
            localObject1 = localObject2;
            localObject2 = localObject3;
            i18 = 0;
          }
          if (interlaceMethod != 0) {
            if (arrayOfInt != null) {
              sendPixels(0, 0, width, height, arrayOfInt, 0, width);
            } else {
              sendPixels(0, 0, width, height, arrayOfByte1, 0, width);
            }
          }
        }
      }
      imageComplete(3, true);
      return;
    }
    catch (IOException localIOException)
    {
      if (!aborted)
      {
        property("error", localIOException);
        imageComplete(3, true);
        throw localIOException;
      }
    }
    finally
    {
      try
      {
        close();
      }
      catch (Throwable localThrowable3) {}
    }
  }
  
  private boolean sendPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6)
  {
    int i = setPixels(paramInt1, paramInt2, paramInt3, paramInt4, cm, paramArrayOfInt, paramInt5, paramInt6);
    if (i <= 0) {
      aborted = true;
    }
    return !aborted;
  }
  
  private boolean sendPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
  {
    int i = setPixels(paramInt1, paramInt2, paramInt3, paramInt4, cm, paramArrayOfByte, paramInt5, paramInt6);
    if (i <= 0) {
      aborted = true;
    }
    return !aborted;
  }
  
  private void filterRow(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    int i = 0;
    switch (paramInt1)
    {
    case 0: 
      break;
    case 1: 
      i = paramInt3;
    case 2: 
    case 3: 
    case 4: 
    default: 
      while (i < paramInt2)
      {
        int tmp57_55 = i;
        byte[] tmp57_54 = paramArrayOfByte1;
        tmp57_54[tmp57_55] = ((byte)(tmp57_54[tmp57_55] + paramArrayOfByte1[(i - paramInt3)]));
        i++;
        continue;
        if (paramArrayOfByte2 != null) {
          while (i < paramInt2)
          {
            int tmp89_87 = i;
            byte[] tmp89_86 = paramArrayOfByte1;
            tmp89_86[tmp89_87] = ((byte)(tmp89_86[tmp89_87] + paramArrayOfByte2[i]));
            i++;
            continue;
            if (paramArrayOfByte2 != null)
            {
              while (i < paramInt3)
              {
                int tmp118_116 = i;
                byte[] tmp118_115 = paramArrayOfByte1;
                tmp118_115[tmp118_116] = ((byte)(tmp118_115[tmp118_116] + ((0xFF & paramArrayOfByte2[i]) >> 1)));
                i++;
              }
              while (i < paramInt2)
              {
                int tmp149_147 = i;
                byte[] tmp149_146 = paramArrayOfByte1;
                tmp149_146[tmp149_147] = ((byte)(tmp149_146[tmp149_147] + ((paramArrayOfByte2[i] & 0xFF) + (paramArrayOfByte1[(i - paramInt3)] & 0xFF) >> 1)));
                i++;
              }
            }
            i = paramInt3;
            while (i < paramInt2)
            {
              int tmp196_194 = i;
              byte[] tmp196_193 = paramArrayOfByte1;
              tmp196_193[tmp196_194] = ((byte)(tmp196_193[tmp196_194] + ((paramArrayOfByte1[(i - paramInt3)] & 0xFF) >> 1)));
              i++;
              continue;
              if (paramArrayOfByte2 != null)
              {
                while (i < paramInt3)
                {
                  int tmp234_232 = i;
                  byte[] tmp234_231 = paramArrayOfByte1;
                  tmp234_231[tmp234_232] = ((byte)(tmp234_231[tmp234_232] + paramArrayOfByte2[i]));
                  i++;
                }
                while (i < paramInt2)
                {
                  int j = paramArrayOfByte1[(i - paramInt3)] & 0xFF;
                  int k = paramArrayOfByte2[i] & 0xFF;
                  int m = paramArrayOfByte2[(i - paramInt3)] & 0xFF;
                  int n = j + k - m;
                  int i1 = n > j ? n - j : j - n;
                  int i2 = n > k ? n - k : k - n;
                  int i3 = n > m ? n - m : m - n;
                  int tmp371_369 = i;
                  byte[] tmp371_368 = paramArrayOfByte1;
                  tmp371_368[tmp371_369] = ((byte)(tmp371_368[tmp371_369] + (i2 <= i3 ? k : (i1 <= i2) && (i1 <= i3) ? j : m)));
                  i++;
                }
              }
              i = paramInt3;
              while (i < paramInt2)
              {
                int tmp429_427 = i;
                byte[] tmp429_426 = paramArrayOfByte1;
                tmp429_426[tmp429_427] = ((byte)(tmp429_426[tmp429_427] + paramArrayOfByte1[(i - paramInt3)]));
                i++;
                continue;
                throw new PNGException("Illegal filter");
              }
            }
          }
        }
      }
    }
  }
  
  public PNGImageDecoder(InputStreamImageSource paramInputStreamImageSource, InputStream paramInputStream)
    throws IOException
  {
    super(paramInputStreamImageSource, paramInputStream);
    inputStream = new PNGFilterInputStream(this, paramInputStream);
    underlyingInputStream = inputStream.underlyingInputStream;
  }
  
  private void fill()
    throws IOException
  {
    if (!seenEOF)
    {
      if ((pos > 0) && (pos < limit))
      {
        System.arraycopy(inbuf, pos, inbuf, 0, limit - pos);
        limit -= pos;
        pos = 0;
      }
      else if (pos >= limit)
      {
        pos = 0;
        limit = 0;
      }
      int i = inbuf.length;
      while (limit < i)
      {
        int j = underlyingInputStream.read(inbuf, limit, i - limit);
        if (j <= 0)
        {
          seenEOF = true;
          break;
        }
        limit += j;
      }
    }
  }
  
  private boolean need(int paramInt)
    throws IOException
  {
    if (limit - pos >= paramInt) {
      return true;
    }
    fill();
    if (limit - pos >= paramInt) {
      return true;
    }
    if (seenEOF) {
      return false;
    }
    byte[] arrayOfByte = new byte[paramInt + 100];
    System.arraycopy(inbuf, pos, arrayOfByte, 0, limit - pos);
    limit -= pos;
    pos = 0;
    inbuf = arrayOfByte;
    fill();
    return limit - pos >= paramInt;
  }
  
  private final int getInt(int paramInt)
  {
    return (inbuf[paramInt] & 0xFF) << 24 | (inbuf[(paramInt + 1)] & 0xFF) << 16 | (inbuf[(paramInt + 2)] & 0xFF) << 8 | inbuf[(paramInt + 3)] & 0xFF;
  }
  
  private final int getShort(int paramInt)
  {
    return (short)((inbuf[paramInt] & 0xFF) << 8 | inbuf[(paramInt + 1)] & 0xFF);
  }
  
  private final int getByte(int paramInt)
  {
    return inbuf[paramInt] & 0xFF;
  }
  
  private final boolean getChunk()
    throws IOException
  {
    chunkLength = 0;
    if (!need(8)) {
      return false;
    }
    chunkLength = getInt(pos);
    chunkKey = getInt(pos + 4);
    if (chunkLength < 0) {
      throw new PNGException("bogus length: " + chunkLength);
    }
    if (!need(chunkLength + 12)) {
      return false;
    }
    chunkCRC = getInt(pos + 8 + chunkLength);
    chunkStart = (pos + 8);
    int i = crc(inbuf, pos + 4, chunkLength + 4);
    if ((chunkCRC != i) && (checkCRC)) {
      throw new PNGException("crc corruption");
    }
    pos += chunkLength + 12;
    return true;
  }
  
  private void readAll()
    throws IOException
  {
    while (getChunk()) {
      handleChunk(chunkKey, inbuf, chunkStart, chunkLength);
    }
  }
  
  boolean getData()
    throws IOException
  {
    while ((chunkLength == 0) && (getChunk())) {
      if (handleChunk(chunkKey, inbuf, chunkStart, chunkLength)) {
        chunkLength = 0;
      }
    }
    return chunkLength > 0;
  }
  
  public static boolean getCheckCRC()
  {
    return checkCRC;
  }
  
  public static void setCheckCRC(boolean paramBoolean)
  {
    checkCRC = paramBoolean;
  }
  
  protected void wrc(int paramInt)
  {
    paramInt &= 0xFF;
    if ((paramInt <= 32) || (paramInt > 122)) {
      paramInt = 63;
    }
    System.out.write(paramInt);
  }
  
  protected void wrk(int paramInt)
  {
    wrc(paramInt >> 24);
    wrc(paramInt >> 16);
    wrc(paramInt >> 8);
    wrc(paramInt);
  }
  
  public void print()
  {
    wrk(chunkKey);
    System.out.print(" " + chunkLength + "\n");
  }
  
  private static int update_crc(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    for (int i = paramInt1;; i = crc_table[((i ^ paramArrayOfByte[(paramInt2++)]) & 0xFF)] ^ i >>> 8)
    {
      paramInt3--;
      if (paramInt3 < 0) {
        break;
      }
    }
    return i;
  }
  
  private static int crc(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return update_crc(-1, paramArrayOfByte, paramInt1, paramInt2) ^ 0xFFFFFFFF;
  }
  
  static
  {
    for (int i = 0; i < 256; i++)
    {
      int j = i;
      for (int k = 0; k < 8; k++) {
        if ((j & 0x1) != 0) {
          j = 0xEDB88320 ^ j >>> 1;
        } else {
          j >>>= 1;
        }
      }
      crc_table[i] = j;
    }
  }
  
  public static class Chromaticities
  {
    public float whiteX;
    public float whiteY;
    public float redX;
    public float redY;
    public float greenX;
    public float greenY;
    public float blueX;
    public float blueY;
    
    Chromaticities(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
    {
      whiteX = (paramInt1 / 100000.0F);
      whiteY = (paramInt2 / 100000.0F);
      redX = (paramInt3 / 100000.0F);
      redY = (paramInt4 / 100000.0F);
      greenX = (paramInt5 / 100000.0F);
      greenY = (paramInt6 / 100000.0F);
      blueX = (paramInt7 / 100000.0F);
      blueY = (paramInt8 / 100000.0F);
    }
    
    public String toString()
    {
      return "Chromaticities(white=" + whiteX + "," + whiteY + ";red=" + redX + "," + redY + ";green=" + greenX + "," + greenY + ";blue=" + blueX + "," + blueY + ")";
    }
  }
  
  public class PNGException
    extends IOException
  {
    PNGException(String paramString)
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\PNGImageDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */