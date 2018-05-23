package sun.java2d.loops;

import java.awt.geom.Path2D.Float;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class ProcessPath
{
  public static final int PH_MODE_DRAW_CLIP = 0;
  public static final int PH_MODE_FILL_CLIP = 1;
  public static EndSubPathHandler noopEndSubPathHandler = new EndSubPathHandler()
  {
    public void processEndSubPath() {}
  };
  private static final float UPPER_BND = 8.5070587E37F;
  private static final float LOWER_BND = -8.5070587E37F;
  private static final int FWD_PREC = 7;
  private static final int MDP_PREC = 10;
  private static final int MDP_MULT = 1024;
  private static final int MDP_HALF_MULT = 512;
  private static final int UPPER_OUT_BND = 1048576;
  private static final int LOWER_OUT_BND = -1048576;
  private static final float CALC_UBND = 1048576.0F;
  private static final float CALC_LBND = -1048576.0F;
  public static final int EPSFX = 1;
  public static final float EPSF = 9.765625E-4F;
  private static final int MDP_W_MASK = -1024;
  private static final int MDP_F_MASK = 1023;
  private static final int MAX_CUB_SIZE = 256;
  private static final int MAX_QUAD_SIZE = 1024;
  private static final int DF_CUB_STEPS = 3;
  private static final int DF_QUAD_STEPS = 2;
  private static final int DF_CUB_SHIFT = 6;
  private static final int DF_QUAD_SHIFT = 1;
  private static final int DF_CUB_COUNT = 8;
  private static final int DF_QUAD_COUNT = 4;
  private static final int DF_CUB_DEC_BND = 262144;
  private static final int DF_CUB_INC_BND = 32768;
  private static final int DF_QUAD_DEC_BND = 8192;
  private static final int DF_QUAD_INC_BND = 1024;
  private static final int CUB_A_SHIFT = 7;
  private static final int CUB_B_SHIFT = 11;
  private static final int CUB_C_SHIFT = 13;
  private static final int CUB_A_MDP_MULT = 128;
  private static final int CUB_B_MDP_MULT = 2048;
  private static final int CUB_C_MDP_MULT = 8192;
  private static final int QUAD_A_SHIFT = 7;
  private static final int QUAD_B_SHIFT = 9;
  private static final int QUAD_A_MDP_MULT = 128;
  private static final int QUAD_B_MDP_MULT = 512;
  private static final int CRES_MIN_CLIPPED = 0;
  private static final int CRES_MAX_CLIPPED = 1;
  private static final int CRES_NOT_CLIPPED = 3;
  private static final int CRES_INVISIBLE = 4;
  private static final int DF_MAX_POINT = 256;
  
  public ProcessPath() {}
  
  public static boolean fillPath(DrawHandler paramDrawHandler, Path2D.Float paramFloat, int paramInt1, int paramInt2)
  {
    FillProcessHandler localFillProcessHandler = new FillProcessHandler(paramDrawHandler);
    if (!doProcessPath(localFillProcessHandler, paramFloat, paramInt1, paramInt2)) {
      return false;
    }
    FillPolygon(localFillProcessHandler, paramFloat.getWindingRule());
    return true;
  }
  
  public static boolean drawPath(DrawHandler paramDrawHandler, EndSubPathHandler paramEndSubPathHandler, Path2D.Float paramFloat, int paramInt1, int paramInt2)
  {
    return doProcessPath(new DrawProcessHandler(paramDrawHandler, paramEndSubPathHandler), paramFloat, paramInt1, paramInt2);
  }
  
  public static boolean drawPath(DrawHandler paramDrawHandler, Path2D.Float paramFloat, int paramInt1, int paramInt2)
  {
    return doProcessPath(new DrawProcessHandler(paramDrawHandler, noopEndSubPathHandler), paramFloat, paramInt1, paramInt2);
  }
  
  private static float CLIP(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, double paramDouble)
  {
    return (float)(paramFloat2 + (paramDouble - paramFloat1) * (paramFloat4 - paramFloat2) / (paramFloat3 - paramFloat1));
  }
  
  private static int CLIP(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble)
  {
    return (int)(paramInt2 + (paramDouble - paramInt1) * (paramInt4 - paramInt2) / (paramInt3 - paramInt1));
  }
  
  private static boolean IS_CLIPPED(int paramInt)
  {
    return (paramInt == 0) || (paramInt == 1);
  }
  
  private static int TESTANDCLIP(float paramFloat1, float paramFloat2, float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = 3;
    if ((paramArrayOfFloat[paramInt1] < paramFloat1) || (paramArrayOfFloat[paramInt1] > paramFloat2))
    {
      double d;
      if (paramArrayOfFloat[paramInt1] < paramFloat1)
      {
        if (paramArrayOfFloat[paramInt3] < paramFloat1) {
          return 4;
        }
        i = 0;
        d = paramFloat1;
      }
      else
      {
        if (paramArrayOfFloat[paramInt3] > paramFloat2) {
          return 4;
        }
        i = 1;
        d = paramFloat2;
      }
      paramArrayOfFloat[paramInt2] = CLIP(paramArrayOfFloat[paramInt1], paramArrayOfFloat[paramInt2], paramArrayOfFloat[paramInt3], paramArrayOfFloat[paramInt4], d);
      paramArrayOfFloat[paramInt1] = ((float)d);
    }
    return i;
  }
  
  private static int TESTANDCLIP(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    int i = 3;
    if ((paramArrayOfInt[paramInt3] < paramInt1) || (paramArrayOfInt[paramInt3] > paramInt2))
    {
      double d;
      if (paramArrayOfInt[paramInt3] < paramInt1)
      {
        if (paramArrayOfInt[paramInt5] < paramInt1) {
          return 4;
        }
        i = 0;
        d = paramInt1;
      }
      else
      {
        if (paramArrayOfInt[paramInt5] > paramInt2) {
          return 4;
        }
        i = 1;
        d = paramInt2;
      }
      paramArrayOfInt[paramInt4] = CLIP(paramArrayOfInt[paramInt3], paramArrayOfInt[paramInt4], paramArrayOfInt[paramInt5], paramArrayOfInt[paramInt6], d);
      paramArrayOfInt[paramInt3] = ((int)d);
    }
    return i;
  }
  
  private static int CLIPCLAMP(float paramFloat1, float paramFloat2, float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    paramArrayOfFloat[paramInt5] = paramArrayOfFloat[paramInt1];
    paramArrayOfFloat[paramInt6] = paramArrayOfFloat[paramInt2];
    int i = TESTANDCLIP(paramFloat1, paramFloat2, paramArrayOfFloat, paramInt1, paramInt2, paramInt3, paramInt4);
    if (i == 0)
    {
      paramArrayOfFloat[paramInt5] = paramArrayOfFloat[paramInt1];
    }
    else if (i == 1)
    {
      paramArrayOfFloat[paramInt5] = paramArrayOfFloat[paramInt1];
      i = 1;
    }
    else if (i == 4)
    {
      if (paramArrayOfFloat[paramInt1] > paramFloat2)
      {
        i = 4;
      }
      else
      {
        paramArrayOfFloat[paramInt1] = paramFloat1;
        paramArrayOfFloat[paramInt3] = paramFloat1;
        i = 3;
      }
    }
    return i;
  }
  
  private static int CLIPCLAMP(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
  {
    paramArrayOfInt[paramInt7] = paramArrayOfInt[paramInt3];
    paramArrayOfInt[paramInt8] = paramArrayOfInt[paramInt4];
    int i = TESTANDCLIP(paramInt1, paramInt2, paramArrayOfInt, paramInt3, paramInt4, paramInt5, paramInt6);
    if (i == 0)
    {
      paramArrayOfInt[paramInt7] = paramArrayOfInt[paramInt3];
    }
    else if (i == 1)
    {
      paramArrayOfInt[paramInt7] = paramArrayOfInt[paramInt3];
      i = 1;
    }
    else if (i == 4)
    {
      if (paramArrayOfInt[paramInt3] > paramInt2)
      {
        i = 4;
      }
      else
      {
        paramArrayOfInt[paramInt3] = paramInt1;
        paramArrayOfInt[paramInt5] = paramInt1;
        i = 3;
      }
    }
    return i;
  }
  
  private static void DrawMonotonicQuad(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, boolean paramBoolean, int[] paramArrayOfInt)
  {
    int i = (int)(paramArrayOfFloat[0] * 1024.0F);
    int j = (int)(paramArrayOfFloat[1] * 1024.0F);
    int k = (int)(paramArrayOfFloat[4] * 1024.0F);
    int m = (int)(paramArrayOfFloat[5] * 1024.0F);
    int n = (i & 0x3FF) << 1;
    int i1 = (j & 0x3FF) << 1;
    int i2 = 4;
    int i3 = 1;
    int i4 = (int)((paramArrayOfFloat[0] - 2.0F * paramArrayOfFloat[2] + paramArrayOfFloat[4]) * 128.0F);
    int i5 = (int)((paramArrayOfFloat[1] - 2.0F * paramArrayOfFloat[3] + paramArrayOfFloat[5]) * 128.0F);
    int i6 = (int)((-2.0F * paramArrayOfFloat[0] + 2.0F * paramArrayOfFloat[2]) * 512.0F);
    int i7 = (int)((-2.0F * paramArrayOfFloat[1] + 2.0F * paramArrayOfFloat[3]) * 512.0F);
    int i8 = 2 * i4;
    int i9 = 2 * i5;
    int i10 = i4 + i6;
    int i11 = i5 + i7;
    int i14 = i;
    int i15 = j;
    int i16 = Math.max(Math.abs(i8), Math.abs(i9));
    int i17 = k - i;
    int i18 = m - j;
    int i19 = i & 0xFC00;
    int i20 = j & 0xFC00;
    while (i16 > 8192)
    {
      i10 = (i10 << 1) - i4;
      i11 = (i11 << 1) - i5;
      i2 <<= 1;
      i16 >>= 2;
      n <<= 2;
      i1 <<= 2;
      i3 += 2;
    }
    while (i2-- > 1)
    {
      n += i10;
      i1 += i11;
      i10 += i8;
      i11 += i9;
      int i12 = i14;
      int i13 = i15;
      i14 = i19 + (n >> i3);
      i15 = i20 + (i1 >> i3);
      if ((k - i14 ^ i17) < 0) {
        i14 = k;
      }
      if ((m - i15 ^ i18) < 0) {
        i15 = m;
      }
      paramProcessHandler.processFixedLine(i12, i13, i14, i15, paramArrayOfInt, paramBoolean, false);
    }
    paramProcessHandler.processFixedLine(i14, i15, k, m, paramArrayOfInt, paramBoolean, false);
  }
  
  private static void ProcessMonotonicQuad(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, int[] paramArrayOfInt)
  {
    float[] arrayOfFloat = new float[6];
    float f3;
    float f1 = f3 = paramArrayOfFloat[0];
    float f4;
    float f2 = f4 = paramArrayOfFloat[1];
    for (int i = 2; i < 6; i += 2)
    {
      f1 = f1 > paramArrayOfFloat[i] ? paramArrayOfFloat[i] : f1;
      f3 = f3 < paramArrayOfFloat[i] ? paramArrayOfFloat[i] : f3;
      f2 = f2 > paramArrayOfFloat[(i + 1)] ? paramArrayOfFloat[(i + 1)] : f2;
      f4 = f4 < paramArrayOfFloat[(i + 1)] ? paramArrayOfFloat[(i + 1)] : f4;
    }
    if (clipMode == 0)
    {
      if ((dhnd.xMaxf >= f1) && (dhnd.xMinf <= f3) && (dhnd.yMaxf >= f2) && (dhnd.yMinf <= f4)) {}
    }
    else
    {
      if ((dhnd.yMaxf < f2) || (dhnd.yMinf > f4) || (dhnd.xMaxf < f1)) {
        return;
      }
      if (dhnd.xMinf > f3) {
        paramArrayOfFloat[0] = (paramArrayOfFloat[2] = paramArrayOfFloat[4] = dhnd.xMinf);
      }
    }
    if ((f3 - f1 > 1024.0F) || (f4 - f2 > 1024.0F))
    {
      arrayOfFloat[4] = paramArrayOfFloat[4];
      arrayOfFloat[5] = paramArrayOfFloat[5];
      arrayOfFloat[2] = ((paramArrayOfFloat[2] + paramArrayOfFloat[4]) / 2.0F);
      arrayOfFloat[3] = ((paramArrayOfFloat[3] + paramArrayOfFloat[5]) / 2.0F);
      paramArrayOfFloat[2] = ((paramArrayOfFloat[0] + paramArrayOfFloat[2]) / 2.0F);
      paramArrayOfFloat[3] = ((paramArrayOfFloat[1] + paramArrayOfFloat[3]) / 2.0F);
      paramArrayOfFloat[4] = (arrayOfFloat[0] = (paramArrayOfFloat[2] + arrayOfFloat[2]) / 2.0F);
      paramArrayOfFloat[5] = (arrayOfFloat[1] = (paramArrayOfFloat[3] + arrayOfFloat[3]) / 2.0F);
      ProcessMonotonicQuad(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt);
      ProcessMonotonicQuad(paramProcessHandler, arrayOfFloat, paramArrayOfInt);
    }
    else
    {
      DrawMonotonicQuad(paramProcessHandler, paramArrayOfFloat, (dhnd.xMinf >= f1) || (dhnd.xMaxf <= f3) || (dhnd.yMinf >= f2) || (dhnd.yMaxf <= f4), paramArrayOfInt);
    }
  }
  
  private static void ProcessQuad(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, int[] paramArrayOfInt)
  {
    double[] arrayOfDouble = new double[2];
    int i = 0;
    double d2;
    double d3;
    double d1;
    if (((paramArrayOfFloat[0] > paramArrayOfFloat[2]) || (paramArrayOfFloat[2] > paramArrayOfFloat[4])) && ((paramArrayOfFloat[0] < paramArrayOfFloat[2]) || (paramArrayOfFloat[2] < paramArrayOfFloat[4])))
    {
      d2 = paramArrayOfFloat[0] - 2.0F * paramArrayOfFloat[2] + paramArrayOfFloat[4];
      if (d2 != 0.0D)
      {
        d3 = paramArrayOfFloat[0] - paramArrayOfFloat[2];
        d1 = d3 / d2;
        if ((d1 < 1.0D) && (d1 > 0.0D)) {
          arrayOfDouble[(i++)] = d1;
        }
      }
    }
    if (((paramArrayOfFloat[1] > paramArrayOfFloat[3]) || (paramArrayOfFloat[3] > paramArrayOfFloat[5])) && ((paramArrayOfFloat[1] < paramArrayOfFloat[3]) || (paramArrayOfFloat[3] < paramArrayOfFloat[5])))
    {
      d2 = paramArrayOfFloat[1] - 2.0F * paramArrayOfFloat[3] + paramArrayOfFloat[5];
      if (d2 != 0.0D)
      {
        d3 = paramArrayOfFloat[1] - paramArrayOfFloat[3];
        d1 = d3 / d2;
        if ((d1 < 1.0D) && (d1 > 0.0D)) {
          if (i > 0)
          {
            if (arrayOfDouble[0] > d1)
            {
              arrayOfDouble[(i++)] = arrayOfDouble[0];
              arrayOfDouble[0] = d1;
            }
            else if (arrayOfDouble[0] < d1)
            {
              arrayOfDouble[(i++)] = d1;
            }
          }
          else {
            arrayOfDouble[(i++)] = d1;
          }
        }
      }
    }
    switch (i)
    {
    case 0: 
      break;
    case 1: 
      ProcessFirstMonotonicPartOfQuad(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt, (float)arrayOfDouble[0]);
      break;
    case 2: 
      ProcessFirstMonotonicPartOfQuad(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt, (float)arrayOfDouble[0]);
      d1 = arrayOfDouble[1] - arrayOfDouble[0];
      if (d1 > 0.0D) {
        ProcessFirstMonotonicPartOfQuad(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt, (float)(d1 / (1.0D - arrayOfDouble[0])));
      }
      break;
    }
    ProcessMonotonicQuad(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt);
  }
  
  private static void ProcessFirstMonotonicPartOfQuad(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, int[] paramArrayOfInt, float paramFloat)
  {
    float[] arrayOfFloat = new float[6];
    arrayOfFloat[0] = paramArrayOfFloat[0];
    arrayOfFloat[1] = paramArrayOfFloat[1];
    arrayOfFloat[2] = (paramArrayOfFloat[0] + paramFloat * (paramArrayOfFloat[2] - paramArrayOfFloat[0]));
    arrayOfFloat[3] = (paramArrayOfFloat[1] + paramFloat * (paramArrayOfFloat[3] - paramArrayOfFloat[1]));
    paramArrayOfFloat[2] += paramFloat * (paramArrayOfFloat[4] - paramArrayOfFloat[2]);
    paramArrayOfFloat[3] += paramFloat * (paramArrayOfFloat[5] - paramArrayOfFloat[3]);
    paramArrayOfFloat[0] = (arrayOfFloat[4] = arrayOfFloat[2] + paramFloat * (paramArrayOfFloat[2] - arrayOfFloat[2]));
    paramArrayOfFloat[1] = (arrayOfFloat[5] = arrayOfFloat[3] + paramFloat * (paramArrayOfFloat[3] - arrayOfFloat[3]));
    ProcessMonotonicQuad(paramProcessHandler, arrayOfFloat, paramArrayOfInt);
  }
  
  private static void DrawMonotonicCubic(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, boolean paramBoolean, int[] paramArrayOfInt)
  {
    int i = (int)(paramArrayOfFloat[0] * 1024.0F);
    int j = (int)(paramArrayOfFloat[1] * 1024.0F);
    int k = (int)(paramArrayOfFloat[6] * 1024.0F);
    int m = (int)(paramArrayOfFloat[7] * 1024.0F);
    int n = (i & 0x3FF) << 6;
    int i1 = (j & 0x3FF) << 6;
    int i2 = 32768;
    int i3 = 262144;
    int i4 = 8;
    int i5 = 6;
    int i6 = (int)((-paramArrayOfFloat[0] + 3.0F * paramArrayOfFloat[2] - 3.0F * paramArrayOfFloat[4] + paramArrayOfFloat[6]) * 128.0F);
    int i7 = (int)((-paramArrayOfFloat[1] + 3.0F * paramArrayOfFloat[3] - 3.0F * paramArrayOfFloat[5] + paramArrayOfFloat[7]) * 128.0F);
    int i8 = (int)((3.0F * paramArrayOfFloat[0] - 6.0F * paramArrayOfFloat[2] + 3.0F * paramArrayOfFloat[4]) * 2048.0F);
    int i9 = (int)((3.0F * paramArrayOfFloat[1] - 6.0F * paramArrayOfFloat[3] + 3.0F * paramArrayOfFloat[5]) * 2048.0F);
    int i10 = (int)((-3.0F * paramArrayOfFloat[0] + 3.0F * paramArrayOfFloat[2]) * 8192.0F);
    int i11 = (int)((-3.0F * paramArrayOfFloat[1] + 3.0F * paramArrayOfFloat[3]) * 8192.0F);
    int i12 = 6 * i6;
    int i13 = 6 * i7;
    int i14 = i12 + i8;
    int i15 = i13 + i9;
    int i16 = i6 + (i8 >> 1) + i10;
    int i17 = i7 + (i9 >> 1) + i11;
    int i20 = i;
    int i21 = j;
    int i22 = i & 0xFC00;
    int i23 = j & 0xFC00;
    int i24 = k - i;
    int i25 = m - j;
    while (i4 > 0)
    {
      while ((Math.abs(i14) > i3) || (Math.abs(i15) > i3))
      {
        i14 = (i14 << 1) - i12;
        i15 = (i15 << 1) - i13;
        i16 = (i16 << 2) - (i14 >> 1);
        i17 = (i17 << 2) - (i15 >> 1);
        i4 <<= 1;
        i3 <<= 3;
        i2 <<= 3;
        n <<= 3;
        i1 <<= 3;
        i5 += 3;
      }
      while (((i4 & 0x1) == 0) && (i5 > 6) && (Math.abs(i16) <= i2) && (Math.abs(i17) <= i2))
      {
        i16 = (i16 >> 2) + (i14 >> 3);
        i17 = (i17 >> 2) + (i15 >> 3);
        i14 = i14 + i12 >> 1;
        i15 = i15 + i13 >> 1;
        i4 >>= 1;
        i3 >>= 3;
        i2 >>= 3;
        n >>= 3;
        i1 >>= 3;
        i5 -= 3;
      }
      i4--;
      if (i4 > 0)
      {
        n += i16;
        i1 += i17;
        i16 += i14;
        i17 += i15;
        i14 += i12;
        i15 += i13;
        int i18 = i20;
        int i19 = i21;
        i20 = i22 + (n >> i5);
        i21 = i23 + (i1 >> i5);
        if ((k - i20 ^ i24) < 0) {
          i20 = k;
        }
        if ((m - i21 ^ i25) < 0) {
          i21 = m;
        }
        paramProcessHandler.processFixedLine(i18, i19, i20, i21, paramArrayOfInt, paramBoolean, false);
      }
      else
      {
        paramProcessHandler.processFixedLine(i20, i21, k, m, paramArrayOfInt, paramBoolean, false);
      }
    }
  }
  
  private static void ProcessMonotonicCubic(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, int[] paramArrayOfInt)
  {
    float[] arrayOfFloat = new float[8];
    float f4;
    float f3 = f4 = paramArrayOfFloat[0];
    float f6;
    float f5 = f6 = paramArrayOfFloat[1];
    for (int i = 2; i < 8; i += 2)
    {
      f3 = f3 > paramArrayOfFloat[i] ? paramArrayOfFloat[i] : f3;
      f4 = f4 < paramArrayOfFloat[i] ? paramArrayOfFloat[i] : f4;
      f5 = f5 > paramArrayOfFloat[(i + 1)] ? paramArrayOfFloat[(i + 1)] : f5;
      f6 = f6 < paramArrayOfFloat[(i + 1)] ? paramArrayOfFloat[(i + 1)] : f6;
    }
    if (clipMode == 0)
    {
      if ((dhnd.xMaxf >= f3) && (dhnd.xMinf <= f4) && (dhnd.yMaxf >= f5) && (dhnd.yMinf <= f6)) {}
    }
    else
    {
      if ((dhnd.yMaxf < f5) || (dhnd.yMinf > f6) || (dhnd.xMaxf < f3)) {
        return;
      }
      if (dhnd.xMinf > f4) {
        paramArrayOfFloat[0] = (paramArrayOfFloat[2] = paramArrayOfFloat[4] = paramArrayOfFloat[6] = dhnd.xMinf);
      }
    }
    if ((f4 - f3 > 256.0F) || (f6 - f5 > 256.0F))
    {
      arrayOfFloat[6] = paramArrayOfFloat[6];
      arrayOfFloat[7] = paramArrayOfFloat[7];
      arrayOfFloat[4] = ((paramArrayOfFloat[4] + paramArrayOfFloat[6]) / 2.0F);
      arrayOfFloat[5] = ((paramArrayOfFloat[5] + paramArrayOfFloat[7]) / 2.0F);
      float f1 = (paramArrayOfFloat[2] + paramArrayOfFloat[4]) / 2.0F;
      float f2 = (paramArrayOfFloat[3] + paramArrayOfFloat[5]) / 2.0F;
      arrayOfFloat[2] = ((f1 + arrayOfFloat[4]) / 2.0F);
      arrayOfFloat[3] = ((f2 + arrayOfFloat[5]) / 2.0F);
      paramArrayOfFloat[2] = ((paramArrayOfFloat[0] + paramArrayOfFloat[2]) / 2.0F);
      paramArrayOfFloat[3] = ((paramArrayOfFloat[1] + paramArrayOfFloat[3]) / 2.0F);
      paramArrayOfFloat[4] = ((paramArrayOfFloat[2] + f1) / 2.0F);
      paramArrayOfFloat[5] = ((paramArrayOfFloat[3] + f2) / 2.0F);
      paramArrayOfFloat[6] = (arrayOfFloat[0] = (paramArrayOfFloat[4] + arrayOfFloat[2]) / 2.0F);
      paramArrayOfFloat[7] = (arrayOfFloat[1] = (paramArrayOfFloat[5] + arrayOfFloat[3]) / 2.0F);
      ProcessMonotonicCubic(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt);
      ProcessMonotonicCubic(paramProcessHandler, arrayOfFloat, paramArrayOfInt);
    }
    else
    {
      DrawMonotonicCubic(paramProcessHandler, paramArrayOfFloat, (dhnd.xMinf > f3) || (dhnd.xMaxf < f4) || (dhnd.yMinf > f5) || (dhnd.yMaxf < f6), paramArrayOfInt);
    }
  }
  
  private static void ProcessCubic(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, int[] paramArrayOfInt)
  {
    double[] arrayOfDouble1 = new double[4];
    double[] arrayOfDouble2 = new double[3];
    double[] arrayOfDouble3 = new double[2];
    int i = 0;
    int j;
    int k;
    if (((paramArrayOfFloat[0] > paramArrayOfFloat[2]) || (paramArrayOfFloat[2] > paramArrayOfFloat[4]) || (paramArrayOfFloat[4] > paramArrayOfFloat[6])) && ((paramArrayOfFloat[0] < paramArrayOfFloat[2]) || (paramArrayOfFloat[2] < paramArrayOfFloat[4]) || (paramArrayOfFloat[4] < paramArrayOfFloat[6])))
    {
      arrayOfDouble2[2] = (-paramArrayOfFloat[0] + 3.0F * paramArrayOfFloat[2] - 3.0F * paramArrayOfFloat[4] + paramArrayOfFloat[6]);
      arrayOfDouble2[1] = (2.0F * (paramArrayOfFloat[0] - 2.0F * paramArrayOfFloat[2] + paramArrayOfFloat[4]));
      arrayOfDouble2[0] = (-paramArrayOfFloat[0] + paramArrayOfFloat[2]);
      j = QuadCurve2D.solveQuadratic(arrayOfDouble2, arrayOfDouble3);
      for (k = 0; k < j; k++) {
        if ((arrayOfDouble3[k] > 0.0D) && (arrayOfDouble3[k] < 1.0D)) {
          arrayOfDouble1[(i++)] = arrayOfDouble3[k];
        }
      }
    }
    if (((paramArrayOfFloat[1] > paramArrayOfFloat[3]) || (paramArrayOfFloat[3] > paramArrayOfFloat[5]) || (paramArrayOfFloat[5] > paramArrayOfFloat[7])) && ((paramArrayOfFloat[1] < paramArrayOfFloat[3]) || (paramArrayOfFloat[3] < paramArrayOfFloat[5]) || (paramArrayOfFloat[5] < paramArrayOfFloat[7])))
    {
      arrayOfDouble2[2] = (-paramArrayOfFloat[1] + 3.0F * paramArrayOfFloat[3] - 3.0F * paramArrayOfFloat[5] + paramArrayOfFloat[7]);
      arrayOfDouble2[1] = (2.0F * (paramArrayOfFloat[1] - 2.0F * paramArrayOfFloat[3] + paramArrayOfFloat[5]));
      arrayOfDouble2[0] = (-paramArrayOfFloat[1] + paramArrayOfFloat[3]);
      j = QuadCurve2D.solveQuadratic(arrayOfDouble2, arrayOfDouble3);
      for (k = 0; k < j; k++) {
        if ((arrayOfDouble3[k] > 0.0D) && (arrayOfDouble3[k] < 1.0D)) {
          arrayOfDouble1[(i++)] = arrayOfDouble3[k];
        }
      }
    }
    if (i > 0)
    {
      Arrays.sort(arrayOfDouble1, 0, i);
      ProcessFirstMonotonicPartOfCubic(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt, (float)arrayOfDouble1[0]);
      for (j = 1; j < i; j++)
      {
        double d = arrayOfDouble1[j] - arrayOfDouble1[(j - 1)];
        if (d > 0.0D) {
          ProcessFirstMonotonicPartOfCubic(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt, (float)(d / (1.0D - arrayOfDouble1[(j - 1)])));
        }
      }
    }
    ProcessMonotonicCubic(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt);
  }
  
  private static void ProcessFirstMonotonicPartOfCubic(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, int[] paramArrayOfInt, float paramFloat)
  {
    float[] arrayOfFloat = new float[8];
    arrayOfFloat[0] = paramArrayOfFloat[0];
    arrayOfFloat[1] = paramArrayOfFloat[1];
    float f1 = paramArrayOfFloat[2] + paramFloat * (paramArrayOfFloat[4] - paramArrayOfFloat[2]);
    float f2 = paramArrayOfFloat[3] + paramFloat * (paramArrayOfFloat[5] - paramArrayOfFloat[3]);
    arrayOfFloat[2] = (paramArrayOfFloat[0] + paramFloat * (paramArrayOfFloat[2] - paramArrayOfFloat[0]));
    arrayOfFloat[3] = (paramArrayOfFloat[1] + paramFloat * (paramArrayOfFloat[3] - paramArrayOfFloat[1]));
    arrayOfFloat[4] = (arrayOfFloat[2] + paramFloat * (f1 - arrayOfFloat[2]));
    arrayOfFloat[5] = (arrayOfFloat[3] + paramFloat * (f2 - arrayOfFloat[3]));
    paramArrayOfFloat[4] += paramFloat * (paramArrayOfFloat[6] - paramArrayOfFloat[4]);
    paramArrayOfFloat[5] += paramFloat * (paramArrayOfFloat[7] - paramArrayOfFloat[5]);
    paramArrayOfFloat[2] = (f1 + paramFloat * (paramArrayOfFloat[4] - f1));
    paramArrayOfFloat[3] = (f2 + paramFloat * (paramArrayOfFloat[5] - f2));
    paramArrayOfFloat[0] = (arrayOfFloat[6] = arrayOfFloat[4] + paramFloat * (paramArrayOfFloat[2] - arrayOfFloat[4]));
    paramArrayOfFloat[1] = (arrayOfFloat[7] = arrayOfFloat[5] + paramFloat * (paramArrayOfFloat[3] - arrayOfFloat[5]));
    ProcessMonotonicCubic(paramProcessHandler, arrayOfFloat, paramArrayOfInt);
  }
  
  private static void ProcessLine(ProcessHandler paramProcessHandler, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int[] paramArrayOfInt)
  {
    boolean bool1 = false;
    float[] arrayOfFloat = { paramFloat1, paramFloat2, paramFloat3, paramFloat4, 0.0F, 0.0F };
    float f1 = dhnd.xMinf;
    float f2 = dhnd.yMinf;
    float f3 = dhnd.xMaxf;
    float f4 = dhnd.yMaxf;
    int i2 = TESTANDCLIP(f2, f4, arrayOfFloat, 1, 0, 3, 2);
    if (i2 == 4) {
      return;
    }
    bool1 = IS_CLIPPED(i2);
    i2 = TESTANDCLIP(f2, f4, arrayOfFloat, 3, 2, 1, 0);
    if (i2 == 4) {
      return;
    }
    boolean bool2 = IS_CLIPPED(i2);
    bool1 = (bool1) || (bool2);
    int i;
    int j;
    int k;
    int m;
    if (clipMode == 0)
    {
      i2 = TESTANDCLIP(f1, f3, arrayOfFloat, 0, 1, 2, 3);
      if (i2 == 4) {
        return;
      }
      bool1 = (bool1) || (IS_CLIPPED(i2));
      i2 = TESTANDCLIP(f1, f3, arrayOfFloat, 2, 3, 0, 1);
      if (i2 == 4) {
        return;
      }
      bool2 = (bool2) || (IS_CLIPPED(i2));
      bool1 = (bool1) || (bool2);
      i = (int)(arrayOfFloat[0] * 1024.0F);
      j = (int)(arrayOfFloat[1] * 1024.0F);
      k = (int)(arrayOfFloat[2] * 1024.0F);
      m = (int)(arrayOfFloat[3] * 1024.0F);
      paramProcessHandler.processFixedLine(i, j, k, m, paramArrayOfInt, bool1, bool2);
    }
    else
    {
      i2 = CLIPCLAMP(f1, f3, arrayOfFloat, 0, 1, 2, 3, 4, 5);
      i = (int)(arrayOfFloat[0] * 1024.0F);
      j = (int)(arrayOfFloat[1] * 1024.0F);
      int n;
      int i1;
      if (i2 == 0)
      {
        n = (int)(arrayOfFloat[4] * 1024.0F);
        i1 = (int)(arrayOfFloat[5] * 1024.0F);
        paramProcessHandler.processFixedLine(n, i1, i, j, paramArrayOfInt, false, bool2);
      }
      else if (i2 == 4)
      {
        return;
      }
      i2 = CLIPCLAMP(f1, f3, arrayOfFloat, 2, 3, 0, 1, 4, 5);
      bool2 = (bool2) || (i2 == 1);
      k = (int)(arrayOfFloat[2] * 1024.0F);
      m = (int)(arrayOfFloat[3] * 1024.0F);
      paramProcessHandler.processFixedLine(i, j, k, m, paramArrayOfInt, false, bool2);
      if (i2 == 0)
      {
        n = (int)(arrayOfFloat[4] * 1024.0F);
        i1 = (int)(arrayOfFloat[5] * 1024.0F);
        paramProcessHandler.processFixedLine(k, m, n, i1, paramArrayOfInt, false, bool2);
      }
    }
  }
  
  private static boolean doProcessPath(ProcessHandler paramProcessHandler, Path2D.Float paramFloat, float paramFloat1, float paramFloat2)
  {
    float[] arrayOfFloat1 = new float[8];
    float[] arrayOfFloat2 = new float[8];
    float[] arrayOfFloat3 = { 0.0F, 0.0F };
    float[] arrayOfFloat4 = new float[2];
    int[] arrayOfInt = new int[5];
    int i = 0;
    int j = 0;
    arrayOfInt[0] = 0;
    dhnd.adjustBounds(-1048576, -1048576, 1048576, 1048576);
    if (dhnd.strokeControl == 2)
    {
      arrayOfFloat3[0] = -0.5F;
      arrayOfFloat3[1] = -0.5F;
      paramFloat1 = (float)(paramFloat1 - 0.5D);
      paramFloat2 = (float)(paramFloat2 - 0.5D);
    }
    PathIterator localPathIterator = paramFloat.getPathIterator(null);
    while (!localPathIterator.isDone())
    {
      float f1;
      float f2;
      switch (localPathIterator.currentSegment(arrayOfFloat1))
      {
      case 0: 
        if ((i != 0) && (j == 0))
        {
          if ((clipMode == 1) && ((arrayOfFloat2[0] != arrayOfFloat3[0]) || (arrayOfFloat2[1] != arrayOfFloat3[1]))) {
            ProcessLine(paramProcessHandler, arrayOfFloat2[0], arrayOfFloat2[1], arrayOfFloat3[0], arrayOfFloat3[1], arrayOfInt);
          }
          paramProcessHandler.processEndSubPath();
        }
        arrayOfFloat1[0] += paramFloat1;
        arrayOfFloat1[1] += paramFloat2;
        if ((arrayOfFloat2[0] < 8.5070587E37F) && (arrayOfFloat2[0] > -8.5070587E37F) && (arrayOfFloat2[1] < 8.5070587E37F) && (arrayOfFloat2[1] > -8.5070587E37F))
        {
          i = 1;
          j = 0;
          arrayOfFloat3[0] = arrayOfFloat2[0];
          arrayOfFloat3[1] = arrayOfFloat2[1];
        }
        else
        {
          j = 1;
        }
        arrayOfInt[0] = 0;
        break;
      case 1: 
        f1 = arrayOfFloat2[2] = arrayOfFloat1[0] + paramFloat1;
        f2 = arrayOfFloat2[3] = arrayOfFloat1[1] + paramFloat2;
        if ((f1 < 8.5070587E37F) && (f1 > -8.5070587E37F) && (f2 < 8.5070587E37F) && (f2 > -8.5070587E37F)) {
          if (j != 0)
          {
            arrayOfFloat2[0] = (arrayOfFloat3[0] = f1);
            arrayOfFloat2[1] = (arrayOfFloat3[1] = f2);
            i = 1;
            j = 0;
          }
          else
          {
            ProcessLine(paramProcessHandler, arrayOfFloat2[0], arrayOfFloat2[1], arrayOfFloat2[2], arrayOfFloat2[3], arrayOfInt);
            arrayOfFloat2[0] = f1;
            arrayOfFloat2[1] = f2;
          }
        }
        break;
      case 2: 
        arrayOfFloat2[2] = (arrayOfFloat1[0] + paramFloat1);
        arrayOfFloat2[3] = (arrayOfFloat1[1] + paramFloat2);
        f1 = arrayOfFloat2[4] = arrayOfFloat1[2] + paramFloat1;
        f2 = arrayOfFloat2[5] = arrayOfFloat1[3] + paramFloat2;
        if ((f1 < 8.5070587E37F) && (f1 > -8.5070587E37F) && (f2 < 8.5070587E37F) && (f2 > -8.5070587E37F)) {
          if (j != 0)
          {
            arrayOfFloat2[0] = (arrayOfFloat3[0] = f1);
            arrayOfFloat2[1] = (arrayOfFloat3[1] = f2);
            i = 1;
            j = 0;
          }
          else
          {
            if ((arrayOfFloat2[2] < 8.5070587E37F) && (arrayOfFloat2[2] > -8.5070587E37F) && (arrayOfFloat2[3] < 8.5070587E37F) && (arrayOfFloat2[3] > -8.5070587E37F)) {
              ProcessQuad(paramProcessHandler, arrayOfFloat2, arrayOfInt);
            } else {
              ProcessLine(paramProcessHandler, arrayOfFloat2[0], arrayOfFloat2[1], arrayOfFloat2[4], arrayOfFloat2[5], arrayOfInt);
            }
            arrayOfFloat2[0] = f1;
            arrayOfFloat2[1] = f2;
          }
        }
        break;
      case 3: 
        arrayOfFloat2[2] = (arrayOfFloat1[0] + paramFloat1);
        arrayOfFloat2[3] = (arrayOfFloat1[1] + paramFloat2);
        arrayOfFloat2[4] = (arrayOfFloat1[2] + paramFloat1);
        arrayOfFloat2[5] = (arrayOfFloat1[3] + paramFloat2);
        f1 = arrayOfFloat2[6] = arrayOfFloat1[4] + paramFloat1;
        f2 = arrayOfFloat2[7] = arrayOfFloat1[5] + paramFloat2;
        if ((f1 < 8.5070587E37F) && (f1 > -8.5070587E37F) && (f2 < 8.5070587E37F) && (f2 > -8.5070587E37F)) {
          if (j != 0)
          {
            arrayOfFloat2[0] = (arrayOfFloat3[0] = arrayOfFloat2[6]);
            arrayOfFloat2[1] = (arrayOfFloat3[1] = arrayOfFloat2[7]);
            i = 1;
            j = 0;
          }
          else
          {
            if ((arrayOfFloat2[2] < 8.5070587E37F) && (arrayOfFloat2[2] > -8.5070587E37F) && (arrayOfFloat2[3] < 8.5070587E37F) && (arrayOfFloat2[3] > -8.5070587E37F) && (arrayOfFloat2[4] < 8.5070587E37F) && (arrayOfFloat2[4] > -8.5070587E37F) && (arrayOfFloat2[5] < 8.5070587E37F) && (arrayOfFloat2[5] > -8.5070587E37F)) {
              ProcessCubic(paramProcessHandler, arrayOfFloat2, arrayOfInt);
            } else {
              ProcessLine(paramProcessHandler, arrayOfFloat2[0], arrayOfFloat2[1], arrayOfFloat2[6], arrayOfFloat2[7], arrayOfInt);
            }
            arrayOfFloat2[0] = f1;
            arrayOfFloat2[1] = f2;
          }
        }
        break;
      case 4: 
        if ((i != 0) && (j == 0))
        {
          j = 0;
          if ((arrayOfFloat2[0] != arrayOfFloat3[0]) || (arrayOfFloat2[1] != arrayOfFloat3[1]))
          {
            ProcessLine(paramProcessHandler, arrayOfFloat2[0], arrayOfFloat2[1], arrayOfFloat3[0], arrayOfFloat3[1], arrayOfInt);
            arrayOfFloat2[0] = arrayOfFloat3[0];
            arrayOfFloat2[1] = arrayOfFloat3[1];
          }
          paramProcessHandler.processEndSubPath();
        }
        break;
      }
      localPathIterator.next();
    }
    if ((i & (j == 0 ? 1 : 0)) != 0)
    {
      if ((clipMode == 1) && ((arrayOfFloat2[0] != arrayOfFloat3[0]) || (arrayOfFloat2[1] != arrayOfFloat3[1]))) {
        ProcessLine(paramProcessHandler, arrayOfFloat2[0], arrayOfFloat2[1], arrayOfFloat3[0], arrayOfFloat3[1], arrayOfInt);
      }
      paramProcessHandler.processEndSubPath();
    }
    return true;
  }
  
  private static void FillPolygon(FillProcessHandler paramFillProcessHandler, int paramInt)
  {
    int n = dhnd.xMax - 1;
    FillData localFillData = fd;
    int i1 = plgYMin;
    int i2 = plgYMax;
    int i3 = (i2 - i1 >> 10) + 4;
    int i4 = i1 - 1 & 0xFC00;
    int i6 = paramInt == 1 ? -1 : 1;
    List localList = plgPnts;
    int k = localList.size();
    if (k <= 1) {
      return;
    }
    Point[] arrayOfPoint = new Point[i3];
    Point localPoint1 = (Point)localList.get(0);
    prev = null;
    for (int i7 = 0; i7 < k - 1; i7++)
    {
      localPoint1 = (Point)localList.get(i7);
      Point localPoint3 = (Point)localList.get(i7 + 1);
      int i9 = y - i4 - 1 >> 10;
      nextByY = arrayOfPoint[i9];
      arrayOfPoint[i9] = localPoint1;
      next = localPoint3;
      prev = localPoint1;
    }
    Point localPoint2 = (Point)localList.get(k - 1);
    int i8 = y - i4 - 1 >> 10;
    nextByY = arrayOfPoint[i8];
    arrayOfPoint[i8] = localPoint2;
    ActiveEdgeList localActiveEdgeList = new ActiveEdgeList(null);
    int j = i4 + 1024;
    for (int i = 0; (j <= i2) && (i < i3); i++)
    {
      for (Point localPoint4 = arrayOfPoint[i]; localPoint4 != null; localPoint4 = nextByY)
      {
        if ((prev != null) && (!prev.lastPoint)) {
          if ((prev.edge != null) && (prev.y <= j))
          {
            localActiveEdgeList.delete(prev.edge);
            prev.edge = null;
          }
          else if (prev.y > j)
          {
            localActiveEdgeList.insert(prev, j);
          }
        }
        if ((!lastPoint) && (next != null)) {
          if ((edge != null) && (next.y <= j))
          {
            localActiveEdgeList.delete(edge);
            edge = null;
          }
          else if (next.y > j)
          {
            localActiveEdgeList.insert(localPoint4, j);
          }
        }
      }
      if (!localActiveEdgeList.isEmpty())
      {
        localActiveEdgeList.sort();
        int i5 = 0;
        int m = 0;
        int i11;
        int i10 = i11 = dhnd.xMin;
        for (Edge localEdge = head; localEdge != null; localEdge = next)
        {
          i5 += dir;
          if (((i5 & i6) != 0) && (m == 0))
          {
            i10 = x + 1024 - 1 >> 10;
            m = 1;
          }
          if (((i5 & i6) == 0) && (m != 0))
          {
            i11 = x - 1 >> 10;
            if (i10 <= i11) {
              dhnd.drawScanline(i10, i11, j >> 10);
            }
            m = 0;
          }
          x += dx;
        }
        if ((m != 0) && (i10 <= n)) {
          dhnd.drawScanline(i10, n, j >> 10);
        }
      }
      j += 1024;
    }
  }
  
  private static class ActiveEdgeList
  {
    ProcessPath.Edge head;
    
    private ActiveEdgeList() {}
    
    public boolean isEmpty()
    {
      return head == null;
    }
    
    public void insert(ProcessPath.Point paramPoint, int paramInt)
    {
      ProcessPath.Point localPoint = next;
      int i = x;
      int j = y;
      int k = x;
      int m = y;
      if (j == m) {
        return;
      }
      int n = k - i;
      int i1 = m - j;
      int i3;
      int i4;
      int i5;
      if (j < m)
      {
        i3 = i;
        i4 = paramInt - j;
        i5 = -1;
      }
      else
      {
        i3 = k;
        i4 = paramInt - m;
        i5 = 1;
      }
      int i2;
      if ((n > 1048576.0F) || (n < -1048576.0F))
      {
        i2 = (int)(n * 1024.0D / i1);
        i3 += (int)(n * i4 / i1);
      }
      else
      {
        i2 = (n << 10) / i1;
        i3 += n * i4 / i1;
      }
      ProcessPath.Edge localEdge = new ProcessPath.Edge(paramPoint, i3, i2, i5);
      next = head;
      prev = null;
      if (head != null) {
        head.prev = localEdge;
      }
      head = (edge = localEdge);
    }
    
    public void delete(ProcessPath.Edge paramEdge)
    {
      ProcessPath.Edge localEdge1 = prev;
      ProcessPath.Edge localEdge2 = next;
      if (localEdge1 != null) {
        next = localEdge2;
      } else {
        head = localEdge2;
      }
      if (localEdge2 != null) {
        prev = localEdge1;
      }
    }
    
    public void sort()
    {
      Object localObject2 = null;
      int i = 1;
      while ((localObject2 != head.next) && (i != 0))
      {
        Object localObject1 = localEdge1 = head;
        localEdge2 = next;
        i = 0;
        while (localEdge1 != localObject2)
        {
          if (x >= x)
          {
            i = 1;
            ProcessPath.Edge localEdge3;
            if (localEdge1 == head)
            {
              localEdge3 = next;
              next = localEdge1;
              next = localEdge3;
              head = localEdge2;
              localObject1 = localEdge2;
            }
            else
            {
              localEdge3 = next;
              next = localEdge1;
              next = localEdge3;
              next = localEdge2;
              localObject1 = localEdge2;
            }
          }
          else
          {
            localObject1 = localEdge1;
            localEdge1 = next;
          }
          localEdge2 = next;
          if (localEdge2 == localObject2) {
            localObject2 = localEdge1;
          }
        }
      }
      ProcessPath.Edge localEdge1 = head;
      ProcessPath.Edge localEdge2 = null;
      while (localEdge1 != null)
      {
        prev = localEdge2;
        localEdge2 = localEdge1;
        localEdge1 = next;
      }
    }
  }
  
  public static abstract class DrawHandler
  {
    public int xMin;
    public int yMin;
    public int xMax;
    public int yMax;
    public float xMinf;
    public float yMinf;
    public float xMaxf;
    public float yMaxf;
    public int strokeControl;
    
    public DrawHandler(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      setBounds(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    
    public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      xMin = paramInt1;
      yMin = paramInt2;
      xMax = paramInt3;
      yMax = paramInt4;
      xMinf = (paramInt1 - 0.5F);
      yMinf = (paramInt2 - 0.5F);
      xMaxf = (paramInt3 - 0.5F - 9.765625E-4F);
      yMaxf = (paramInt4 - 0.5F - 9.765625E-4F);
    }
    
    public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      strokeControl = paramInt5;
      setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public void adjustBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (xMin > paramInt1) {
        paramInt1 = xMin;
      }
      if (xMax < paramInt3) {
        paramInt3 = xMax;
      }
      if (yMin > paramInt2) {
        paramInt2 = yMin;
      }
      if (yMax < paramInt4) {
        paramInt4 = yMax;
      }
      setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public DrawHandler(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this(paramInt1, paramInt2, paramInt3, paramInt4, 0);
    }
    
    public abstract void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    
    public abstract void drawPixel(int paramInt1, int paramInt2);
    
    public abstract void drawScanline(int paramInt1, int paramInt2, int paramInt3);
  }
  
  private static class DrawProcessHandler
    extends ProcessPath.ProcessHandler
  {
    ProcessPath.EndSubPathHandler processESP;
    
    public DrawProcessHandler(ProcessPath.DrawHandler paramDrawHandler, ProcessPath.EndSubPathHandler paramEndSubPathHandler)
    {
      super(0);
      dhnd = paramDrawHandler;
      processESP = paramEndSubPathHandler;
    }
    
    public void processEndSubPath()
    {
      processESP.processEndSubPath();
    }
    
    void PROCESS_LINE(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean, int[] paramArrayOfInt)
    {
      int i = paramInt1 >> 10;
      int j = paramInt2 >> 10;
      int k = paramInt3 >> 10;
      int m = paramInt4 >> 10;
      if ((i ^ k | j ^ m) == 0)
      {
        if ((paramBoolean) && ((dhnd.yMin > j) || (dhnd.yMax <= j) || (dhnd.xMin > i) || (dhnd.xMax <= i))) {
          return;
        }
        if (paramArrayOfInt[0] == 0)
        {
          paramArrayOfInt[0] = 1;
          paramArrayOfInt[1] = i;
          paramArrayOfInt[2] = j;
          paramArrayOfInt[3] = i;
          paramArrayOfInt[4] = j;
          dhnd.drawPixel(i, j);
        }
        else if (((i != paramArrayOfInt[3]) || (j != paramArrayOfInt[4])) && ((i != paramArrayOfInt[1]) || (j != paramArrayOfInt[2])))
        {
          dhnd.drawPixel(i, j);
          paramArrayOfInt[3] = i;
          paramArrayOfInt[4] = j;
        }
        return;
      }
      if (((!paramBoolean) || ((dhnd.yMin <= j) && (dhnd.yMax > j) && (dhnd.xMin <= i) && (dhnd.xMax > i))) && (paramArrayOfInt[0] == 1) && (((paramArrayOfInt[1] == i) && (paramArrayOfInt[2] == j)) || ((paramArrayOfInt[3] == i) && (paramArrayOfInt[4] == j)))) {
        dhnd.drawPixel(i, j);
      }
      dhnd.drawLine(i, j, k, m);
      if (paramArrayOfInt[0] == 0)
      {
        paramArrayOfInt[0] = 1;
        paramArrayOfInt[1] = i;
        paramArrayOfInt[2] = j;
        paramArrayOfInt[3] = i;
        paramArrayOfInt[4] = j;
      }
      if (((paramArrayOfInt[1] == k) && (paramArrayOfInt[2] == m)) || ((paramArrayOfInt[3] == k) && (paramArrayOfInt[4] == m)))
      {
        if ((paramBoolean) && ((dhnd.yMin > m) || (dhnd.yMax <= m) || (dhnd.xMin > k) || (dhnd.xMax <= k))) {
          return;
        }
        dhnd.drawPixel(k, m);
      }
      paramArrayOfInt[3] = k;
      paramArrayOfInt[4] = m;
    }
    
    void PROCESS_POINT(int paramInt1, int paramInt2, boolean paramBoolean, int[] paramArrayOfInt)
    {
      int i = paramInt1 >> 10;
      int j = paramInt2 >> 10;
      if ((paramBoolean) && ((dhnd.yMin > j) || (dhnd.yMax <= j) || (dhnd.xMin > i) || (dhnd.xMax <= i))) {
        return;
      }
      if (paramArrayOfInt[0] == 0)
      {
        paramArrayOfInt[0] = 1;
        paramArrayOfInt[1] = i;
        paramArrayOfInt[2] = j;
        paramArrayOfInt[3] = i;
        paramArrayOfInt[4] = j;
        dhnd.drawPixel(i, j);
      }
      else if (((i != paramArrayOfInt[3]) || (j != paramArrayOfInt[4])) && ((i != paramArrayOfInt[1]) || (j != paramArrayOfInt[2])))
      {
        dhnd.drawPixel(i, j);
        paramArrayOfInt[3] = i;
        paramArrayOfInt[4] = j;
      }
    }
    
    public void processFixedLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      int i = paramInt1 ^ paramInt3 | paramInt2 ^ paramInt4;
      if ((i & 0xFC00) == 0)
      {
        if (i == 0) {
          PROCESS_POINT(paramInt1 + 512, paramInt2 + 512, paramBoolean1, paramArrayOfInt);
        }
        return;
      }
      int j;
      int m;
      int k;
      int n;
      if ((paramInt1 == paramInt3) || (paramInt2 == paramInt4))
      {
        j = paramInt1 + 512;
        m = paramInt3 + 512;
        k = paramInt2 + 512;
        n = paramInt4 + 512;
      }
      else
      {
        int i1 = paramInt3 - paramInt1;
        int i2 = paramInt4 - paramInt2;
        int i3 = paramInt1 & 0xFC00;
        int i4 = paramInt2 & 0xFC00;
        int i5 = paramInt3 & 0xFC00;
        int i6 = paramInt4 & 0xFC00;
        int i7;
        int i8;
        int i9;
        if ((i3 == paramInt1) || (i4 == paramInt2))
        {
          j = paramInt1 + 512;
          k = paramInt2 + 512;
        }
        else
        {
          i7 = paramInt1 < paramInt3 ? i3 + 1024 : i3;
          i8 = paramInt2 < paramInt4 ? i4 + 1024 : i4;
          i9 = paramInt2 + (i7 - paramInt1) * i2 / i1;
          if ((i9 >= i4) && (i9 <= i4 + 1024))
          {
            j = i7;
            k = i9 + 512;
          }
          else
          {
            i9 = paramInt1 + (i8 - paramInt2) * i1 / i2;
            j = i9 + 512;
            k = i8;
          }
        }
        if ((i5 == paramInt3) || (i6 == paramInt4))
        {
          m = paramInt3 + 512;
          n = paramInt4 + 512;
        }
        else
        {
          i7 = paramInt1 > paramInt3 ? i5 + 1024 : i5;
          i8 = paramInt2 > paramInt4 ? i6 + 1024 : i6;
          i9 = paramInt4 + (i7 - paramInt3) * i2 / i1;
          if ((i9 >= i6) && (i9 <= i6 + 1024))
          {
            m = i7;
            n = i9 + 512;
          }
          else
          {
            i9 = paramInt3 + (i8 - paramInt4) * i1 / i2;
            m = i9 + 512;
            n = i8;
          }
        }
      }
      PROCESS_LINE(j, k, m, n, paramBoolean1, paramArrayOfInt);
    }
  }
  
  private static class Edge
  {
    int x;
    int dx;
    ProcessPath.Point p;
    int dir;
    Edge prev;
    Edge next;
    
    public Edge(ProcessPath.Point paramPoint, int paramInt1, int paramInt2, int paramInt3)
    {
      p = paramPoint;
      x = paramInt1;
      dx = paramInt2;
      dir = paramInt3;
    }
  }
  
  public static abstract interface EndSubPathHandler
  {
    public abstract void processEndSubPath();
  }
  
  private static class FillData
  {
    List<ProcessPath.Point> plgPnts = new Vector(256);
    public int plgYMin;
    public int plgYMax;
    
    public FillData() {}
    
    public void addPoint(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      if (plgPnts.size() == 0)
      {
        plgYMin = (plgYMax = paramInt2);
      }
      else
      {
        plgYMin = (plgYMin > paramInt2 ? paramInt2 : plgYMin);
        plgYMax = (plgYMax < paramInt2 ? paramInt2 : plgYMax);
      }
      plgPnts.add(new ProcessPath.Point(paramInt1, paramInt2, paramBoolean));
    }
    
    public boolean isEmpty()
    {
      return plgPnts.size() == 0;
    }
    
    public boolean isEnded()
    {
      return plgPnts.get(plgPnts.size() - 1)).lastPoint;
    }
    
    public boolean setEnded()
    {
      return plgPnts.get(plgPnts.size() - 1)).lastPoint = 1;
    }
  }
  
  private static class FillProcessHandler
    extends ProcessPath.ProcessHandler
  {
    ProcessPath.FillData fd = new ProcessPath.FillData();
    
    public void processFixedLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      if (paramBoolean1)
      {
        int[] arrayOfInt = { paramInt1, paramInt2, paramInt3, paramInt4, 0, 0 };
        int i = (int)(dhnd.xMinf * 1024.0F);
        int j = (int)(dhnd.xMaxf * 1024.0F);
        int k = (int)(dhnd.yMinf * 1024.0F);
        int m = (int)(dhnd.yMaxf * 1024.0F);
        int n = ProcessPath.TESTANDCLIP(k, m, arrayOfInt, 1, 0, 3, 2);
        if (n == 4) {
          return;
        }
        n = ProcessPath.TESTANDCLIP(k, m, arrayOfInt, 3, 2, 1, 0);
        if (n == 4) {
          return;
        }
        boolean bool = ProcessPath.IS_CLIPPED(n);
        n = ProcessPath.CLIPCLAMP(i, j, arrayOfInt, 0, 1, 2, 3, 4, 5);
        if (n == 0) {
          processFixedLine(arrayOfInt[4], arrayOfInt[5], arrayOfInt[0], arrayOfInt[1], paramArrayOfInt, false, bool);
        } else if (n == 4) {
          return;
        }
        n = ProcessPath.CLIPCLAMP(i, j, arrayOfInt, 2, 3, 0, 1, 4, 5);
        bool = (bool) || (n == 1);
        processFixedLine(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3], paramArrayOfInt, false, bool);
        if (n == 0) {
          processFixedLine(arrayOfInt[2], arrayOfInt[3], arrayOfInt[4], arrayOfInt[5], paramArrayOfInt, false, bool);
        }
        return;
      }
      if ((fd.isEmpty()) || (fd.isEnded())) {
        fd.addPoint(paramInt1, paramInt2, false);
      }
      fd.addPoint(paramInt3, paramInt4, false);
      if (paramBoolean2) {
        fd.setEnded();
      }
    }
    
    FillProcessHandler(ProcessPath.DrawHandler paramDrawHandler)
    {
      super(1);
    }
    
    public void processEndSubPath()
    {
      if (!fd.isEmpty()) {
        fd.setEnded();
      }
    }
  }
  
  private static class Point
  {
    public int x;
    public int y;
    public boolean lastPoint;
    public Point prev;
    public Point next;
    public Point nextByY;
    public ProcessPath.Edge edge;
    
    public Point(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      x = paramInt1;
      y = paramInt2;
      lastPoint = paramBoolean;
    }
  }
  
  public static abstract class ProcessHandler
    implements ProcessPath.EndSubPathHandler
  {
    ProcessPath.DrawHandler dhnd;
    int clipMode;
    
    public ProcessHandler(ProcessPath.DrawHandler paramDrawHandler, int paramInt)
    {
      dhnd = paramDrawHandler;
      clipMode = paramInt;
    }
    
    public abstract void processFixedLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, boolean paramBoolean1, boolean paramBoolean2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\ProcessPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */