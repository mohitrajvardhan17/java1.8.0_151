package sun.java2d.loops;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public final class GeneralRenderer
{
  static final int OUTCODE_TOP = 1;
  static final int OUTCODE_BOTTOM = 2;
  static final int OUTCODE_LEFT = 4;
  static final int OUTCODE_RIGHT = 8;
  
  public GeneralRenderer() {}
  
  public static void register()
  {
    Class localClass = GeneralRenderer.class;
    GraphicsPrimitive[] arrayOfGraphicsPrimitive = { new GraphicsPrimitiveProxy(localClass, "SetFillRectANY", FillRect.methodSignature, FillRect.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(localClass, "SetFillPathANY", FillPath.methodSignature, FillPath.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(localClass, "SetFillSpansANY", FillSpans.methodSignature, FillSpans.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(localClass, "SetDrawLineANY", DrawLine.methodSignature, DrawLine.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(localClass, "SetDrawPolygonsANY", DrawPolygons.methodSignature, DrawPolygons.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(localClass, "SetDrawPathANY", DrawPath.methodSignature, DrawPath.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(localClass, "SetDrawRectANY", DrawRect.methodSignature, DrawRect.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(localClass, "XorFillRectANY", FillRect.methodSignature, FillRect.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(localClass, "XorFillPathANY", FillPath.methodSignature, FillPath.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(localClass, "XorFillSpansANY", FillSpans.methodSignature, FillSpans.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(localClass, "XorDrawLineANY", DrawLine.methodSignature, DrawLine.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(localClass, "XorDrawPolygonsANY", DrawPolygons.methodSignature, DrawPolygons.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(localClass, "XorDrawPathANY", DrawPath.methodSignature, DrawPath.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(localClass, "XorDrawRectANY", DrawRect.methodSignature, DrawRect.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(localClass, "XorDrawGlyphListANY", DrawGlyphList.methodSignature, DrawGlyphList.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(localClass, "XorDrawGlyphListAAANY", DrawGlyphListAA.methodSignature, DrawGlyphListAA.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any) };
    GraphicsPrimitiveMgr.register(arrayOfGraphicsPrimitive);
  }
  
  static void doDrawPoly(SurfaceData paramSurfaceData, PixelWriter paramPixelWriter, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, Region paramRegion, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    int[] arrayOfInt = null;
    if (paramInt2 <= 0) {
      return;
    }
    int k;
    int i = k = paramArrayOfInt1[paramInt1] + paramInt3;
    int m;
    int j = m = paramArrayOfInt2[paramInt1] + paramInt4;
    for (;;)
    {
      paramInt2--;
      if (paramInt2 <= 0) {
        break;
      }
      paramInt1++;
      int n = paramArrayOfInt1[paramInt1] + paramInt3;
      int i1 = paramArrayOfInt2[paramInt1] + paramInt4;
      arrayOfInt = doDrawLine(paramSurfaceData, paramPixelWriter, arrayOfInt, paramRegion, k, m, n, i1);
      k = n;
      m = i1;
    }
    if ((paramBoolean) && ((k != i) || (m != j))) {
      arrayOfInt = doDrawLine(paramSurfaceData, paramPixelWriter, arrayOfInt, paramRegion, k, m, i, j);
    }
  }
  
  static void doSetRect(SurfaceData paramSurfaceData, PixelWriter paramPixelWriter, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    WritableRaster localWritableRaster = (WritableRaster)paramSurfaceData.getRaster(paramInt1, paramInt2, paramInt3 - paramInt1, paramInt4 - paramInt2);
    paramPixelWriter.setRaster(localWritableRaster);
    while (paramInt2 < paramInt4)
    {
      for (int i = paramInt1; i < paramInt3; i++) {
        paramPixelWriter.writePixel(i, paramInt2);
      }
      paramInt2++;
    }
  }
  
  static int[] doDrawLine(SurfaceData paramSurfaceData, PixelWriter paramPixelWriter, int[] paramArrayOfInt, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[8];
    }
    paramArrayOfInt[0] = paramInt1;
    paramArrayOfInt[1] = paramInt2;
    paramArrayOfInt[2] = paramInt3;
    paramArrayOfInt[3] = paramInt4;
    if (!adjustLine(paramArrayOfInt, paramRegion.getLoX(), paramRegion.getLoY(), paramRegion.getHiX(), paramRegion.getHiY())) {
      return paramArrayOfInt;
    }
    int i = paramArrayOfInt[0];
    int j = paramArrayOfInt[1];
    int k = paramArrayOfInt[2];
    int m = paramArrayOfInt[3];
    WritableRaster localWritableRaster = (WritableRaster)paramSurfaceData.getRaster(Math.min(i, k), Math.min(j, m), Math.abs(i - k) + 1, Math.abs(j - m) + 1);
    paramPixelWriter.setRaster(localWritableRaster);
    if (i == k)
    {
      if (j > m) {
        do
        {
          paramPixelWriter.writePixel(i, j);
          j--;
        } while (j >= m);
      } else {
        do
        {
          paramPixelWriter.writePixel(i, j);
          j++;
        } while (j <= m);
      }
    }
    else if (j == m)
    {
      if (i > k) {
        do
        {
          paramPixelWriter.writePixel(i, j);
          i--;
        } while (i >= k);
      } else {
        do
        {
          paramPixelWriter.writePixel(i, j);
          i++;
        } while (i <= k);
      }
    }
    else
    {
      int n = paramArrayOfInt[4];
      int i1 = paramArrayOfInt[5];
      int i2 = paramArrayOfInt[6];
      int i3 = paramArrayOfInt[7];
      int i10;
      int i8;
      int i7;
      int i5;
      int i6;
      int i4;
      if (i2 >= i3)
      {
        i10 = 1;
        i8 = i3 * 2;
        i7 = i2 * 2;
        i5 = n < 0 ? -1 : 1;
        i6 = i1 < 0 ? -1 : 1;
        i2 = -i2;
        i4 = k - i;
      }
      else
      {
        i10 = 0;
        i8 = i2 * 2;
        i7 = i3 * 2;
        i5 = i1 < 0 ? -1 : 1;
        i6 = n < 0 ? -1 : 1;
        i3 = -i3;
        i4 = m - j;
      }
      int i9 = -(i7 / 2);
      int i11;
      if (j != paramInt2)
      {
        i11 = j - paramInt2;
        if (i11 < 0) {
          i11 = -i11;
        }
        i9 += i11 * i2 * 2;
      }
      if (i != paramInt1)
      {
        i11 = i - paramInt1;
        if (i11 < 0) {
          i11 = -i11;
        }
        i9 += i11 * i3 * 2;
      }
      if (i4 < 0) {
        i4 = -i4;
      }
      if (i10 != 0) {
        do
        {
          paramPixelWriter.writePixel(i, j);
          i += i5;
          i9 += i8;
          if (i9 >= 0)
          {
            j += i6;
            i9 -= i7;
          }
          i4--;
        } while (i4 >= 0);
      } else {
        do
        {
          paramPixelWriter.writePixel(i, j);
          j += i5;
          i9 += i8;
          if (i9 >= 0)
          {
            i += i6;
            i9 -= i7;
          }
          i4--;
        } while (i4 >= 0);
      }
    }
    return paramArrayOfInt;
  }
  
  public static void doDrawRect(PixelWriter paramPixelWriter, SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt3 < 0) || (paramInt4 < 0)) {
      return;
    }
    int i = Region.dimAdd(Region.dimAdd(paramInt1, paramInt3), 1);
    int j = Region.dimAdd(Region.dimAdd(paramInt2, paramInt4), 1);
    Region localRegion = paramSunGraphics2D.getCompClip().getBoundsIntersectionXYXY(paramInt1, paramInt2, i, j);
    if (localRegion.isEmpty()) {
      return;
    }
    int k = localRegion.getLoX();
    int m = localRegion.getLoY();
    int n = localRegion.getHiX();
    int i1 = localRegion.getHiY();
    if ((paramInt3 < 2) || (paramInt4 < 2))
    {
      doSetRect(paramSurfaceData, paramPixelWriter, k, m, n, i1);
      return;
    }
    if (m == paramInt2) {
      doSetRect(paramSurfaceData, paramPixelWriter, k, m, n, m + 1);
    }
    if (k == paramInt1) {
      doSetRect(paramSurfaceData, paramPixelWriter, k, m + 1, k + 1, i1 - 1);
    }
    if (n == i) {
      doSetRect(paramSurfaceData, paramPixelWriter, n - 1, m + 1, n, i1 - 1);
    }
    if (i1 == j) {
      doSetRect(paramSurfaceData, paramPixelWriter, k, i1 - 1, n, i1);
    }
  }
  
  static void doDrawGlyphList(SurfaceData paramSurfaceData, PixelWriter paramPixelWriter, GlyphList paramGlyphList, Region paramRegion)
  {
    int[] arrayOfInt1 = paramGlyphList.getBounds();
    paramRegion.clipBoxToBounds(arrayOfInt1);
    int i = arrayOfInt1[0];
    int j = arrayOfInt1[1];
    int k = arrayOfInt1[2];
    int m = arrayOfInt1[3];
    WritableRaster localWritableRaster = (WritableRaster)paramSurfaceData.getRaster(i, j, k - i, m - j);
    paramPixelWriter.setRaster(localWritableRaster);
    int n = paramGlyphList.getNumGlyphs();
    for (int i1 = 0; i1 < n; i1++)
    {
      paramGlyphList.setGlyphIndex(i1);
      int[] arrayOfInt2 = paramGlyphList.getMetrics();
      int i2 = arrayOfInt2[0];
      int i3 = arrayOfInt2[1];
      int i4 = arrayOfInt2[2];
      int i5 = i2 + i4;
      int i6 = i3 + arrayOfInt2[3];
      int i7 = 0;
      if (i2 < i)
      {
        i7 = i - i2;
        i2 = i;
      }
      if (i3 < j)
      {
        i7 += (j - i3) * i4;
        i3 = j;
      }
      if (i5 > k) {
        i5 = k;
      }
      if (i6 > m) {
        i6 = m;
      }
      if ((i5 > i2) && (i6 > i3))
      {
        byte[] arrayOfByte = paramGlyphList.getGrayBits();
        i4 -= i5 - i2;
        for (int i8 = i3; i8 < i6; i8++)
        {
          for (int i9 = i2; i9 < i5; i9++) {
            if (arrayOfByte[(i7++)] < 0) {
              paramPixelWriter.writePixel(i9, i8);
            }
          }
          i7 += i4;
        }
      }
    }
  }
  
  static int outcode(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    int i;
    if (paramInt2 < paramInt4) {
      i = 1;
    } else if (paramInt2 > paramInt6) {
      i = 2;
    } else {
      i = 0;
    }
    if (paramInt1 < paramInt3) {
      i |= 0x4;
    } else if (paramInt1 > paramInt5) {
      i |= 0x8;
    }
    return i;
  }
  
  public static boolean adjustLine(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt3 - 1;
    int j = paramInt4 - 1;
    int k = paramArrayOfInt[0];
    int m = paramArrayOfInt[1];
    int n = paramArrayOfInt[2];
    int i1 = paramArrayOfInt[3];
    if ((i < paramInt1) || (j < paramInt2)) {
      return false;
    }
    int i2;
    if (k == n)
    {
      if ((k < paramInt1) || (k > i)) {
        return false;
      }
      if (m > i1)
      {
        i2 = m;
        m = i1;
        i1 = i2;
      }
      if (m < paramInt2) {
        m = paramInt2;
      }
      if (i1 > j) {
        i1 = j;
      }
      if (m > i1) {
        return false;
      }
      paramArrayOfInt[1] = m;
      paramArrayOfInt[3] = i1;
    }
    else if (m == i1)
    {
      if ((m < paramInt2) || (m > j)) {
        return false;
      }
      if (k > n)
      {
        i2 = k;
        k = n;
        n = i2;
      }
      if (k < paramInt1) {
        k = paramInt1;
      }
      if (n > i) {
        n = i;
      }
      if (k > n) {
        return false;
      }
      paramArrayOfInt[0] = k;
      paramArrayOfInt[2] = n;
    }
    else
    {
      int i4 = n - k;
      int i5 = i1 - m;
      int i6 = i4 < 0 ? -i4 : i4;
      int i7 = i5 < 0 ? -i5 : i5;
      int i8 = i6 >= i7 ? 1 : 0;
      i2 = outcode(k, m, paramInt1, paramInt2, i, j);
      int i3 = outcode(n, i1, paramInt1, paramInt2, i, j);
      while ((i2 | i3) != 0)
      {
        if ((i2 & i3) != 0) {
          return false;
        }
        int i10;
        int i9;
        if (i2 != 0)
        {
          if (0 != (i2 & 0x3))
          {
            if (0 != (i2 & 0x1)) {
              m = paramInt2;
            } else {
              m = j;
            }
            i10 = m - paramArrayOfInt[1];
            if (i10 < 0) {
              i10 = -i10;
            }
            i9 = 2 * i10 * i6 + i7;
            if (i8 != 0) {
              i9 += i7 - i6 - 1;
            }
            i9 /= 2 * i7;
            if (i4 < 0) {
              i9 = -i9;
            }
            k = paramArrayOfInt[0] + i9;
          }
          else if (0 != (i2 & 0xC))
          {
            if (0 != (i2 & 0x4)) {
              k = paramInt1;
            } else {
              k = i;
            }
            i9 = k - paramArrayOfInt[0];
            if (i9 < 0) {
              i9 = -i9;
            }
            i10 = 2 * i9 * i7 + i6;
            if (i8 == 0) {
              i10 += i6 - i7 - 1;
            }
            i10 /= 2 * i6;
            if (i5 < 0) {
              i10 = -i10;
            }
            m = paramArrayOfInt[1] + i10;
          }
          i2 = outcode(k, m, paramInt1, paramInt2, i, j);
        }
        else
        {
          if (0 != (i3 & 0x3))
          {
            if (0 != (i3 & 0x1)) {
              i1 = paramInt2;
            } else {
              i1 = j;
            }
            i10 = i1 - paramArrayOfInt[3];
            if (i10 < 0) {
              i10 = -i10;
            }
            i9 = 2 * i10 * i6 + i7;
            if (i8 != 0) {
              i9 += i7 - i6;
            } else {
              i9--;
            }
            i9 /= 2 * i7;
            if (i4 > 0) {
              i9 = -i9;
            }
            n = paramArrayOfInt[2] + i9;
          }
          else if (0 != (i3 & 0xC))
          {
            if (0 != (i3 & 0x4)) {
              n = paramInt1;
            } else {
              n = i;
            }
            i9 = n - paramArrayOfInt[2];
            if (i9 < 0) {
              i9 = -i9;
            }
            i10 = 2 * i9 * i7 + i6;
            if (i8 != 0) {
              i10--;
            } else {
              i10 += i6 - i7;
            }
            i10 /= 2 * i6;
            if (i5 > 0) {
              i10 = -i10;
            }
            i1 = paramArrayOfInt[3] + i10;
          }
          i3 = outcode(n, i1, paramInt1, paramInt2, i, j);
        }
      }
      paramArrayOfInt[0] = k;
      paramArrayOfInt[1] = m;
      paramArrayOfInt[2] = n;
      paramArrayOfInt[3] = i1;
      paramArrayOfInt[4] = i4;
      paramArrayOfInt[5] = i5;
      paramArrayOfInt[6] = i6;
      paramArrayOfInt[7] = i7;
    }
    return true;
  }
  
  static PixelWriter createSolidPixelWriter(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData)
  {
    ColorModel localColorModel = paramSurfaceData.getColorModel();
    Object localObject = localColorModel.getDataElements(eargb, null);
    return new SolidPixelWriter(localObject);
  }
  
  static PixelWriter createXorPixelWriter(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData)
  {
    ColorModel localColorModel = paramSurfaceData.getColorModel();
    Object localObject1 = localColorModel.getDataElements(eargb, null);
    XORComposite localXORComposite = (XORComposite)paramSunGraphics2D.getComposite();
    int i = localXORComposite.getXorColor().getRGB();
    Object localObject2 = localColorModel.getDataElements(i, null);
    switch (localColorModel.getTransferType())
    {
    case 0: 
      return new XorPixelWriter.ByteData(localObject1, localObject2);
    case 1: 
    case 2: 
      return new XorPixelWriter.ShortData(localObject1, localObject2);
    case 3: 
      return new XorPixelWriter.IntData(localObject1, localObject2);
    case 4: 
      return new XorPixelWriter.FloatData(localObject1, localObject2);
    case 5: 
      return new XorPixelWriter.DoubleData(localObject1, localObject2);
    }
    throw new InternalError("Unsupported XOR pixel type");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\GeneralRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */