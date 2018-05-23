package sun.text.bidi;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.Bidi;
import java.util.Arrays;
import java.util.MissingResourceException;
import sun.text.normalizer.UBiDiProps;
import sun.text.normalizer.UCharacter;
import sun.text.normalizer.UTF16;

public class BidiBase
{
  public static final byte INTERNAL_LEVEL_DEFAULT_LTR = 126;
  public static final byte INTERNAL_LEVEL_DEFAULT_RTL = 127;
  public static final byte MAX_EXPLICIT_LEVEL = 61;
  public static final byte INTERNAL_LEVEL_OVERRIDE = -128;
  public static final int MAP_NOWHERE = -1;
  public static final byte MIXED = 2;
  public static final short DO_MIRRORING = 2;
  private static final short REORDER_DEFAULT = 0;
  private static final short REORDER_NUMBERS_SPECIAL = 1;
  private static final short REORDER_GROUP_NUMBERS_WITH_R = 2;
  private static final short REORDER_RUNS_ONLY = 3;
  private static final short REORDER_INVERSE_NUMBERS_AS_L = 4;
  private static final short REORDER_INVERSE_LIKE_DIRECT = 5;
  private static final short REORDER_INVERSE_FOR_NUMBERS_SPECIAL = 6;
  private static final short REORDER_LAST_LOGICAL_TO_VISUAL = 1;
  private static final int OPTION_INSERT_MARKS = 1;
  private static final int OPTION_REMOVE_CONTROLS = 2;
  private static final int OPTION_STREAMING = 4;
  private static final byte L = 0;
  private static final byte R = 1;
  private static final byte EN = 2;
  private static final byte ES = 3;
  private static final byte ET = 4;
  private static final byte AN = 5;
  private static final byte CS = 6;
  static final byte B = 7;
  private static final byte S = 8;
  private static final byte WS = 9;
  private static final byte ON = 10;
  private static final byte LRE = 11;
  private static final byte LRO = 12;
  private static final byte AL = 13;
  private static final byte RLE = 14;
  private static final byte RLO = 15;
  private static final byte PDF = 16;
  private static final byte NSM = 17;
  private static final byte BN = 18;
  private static final int MASK_R_AL = 8194;
  private static final char CR = '\r';
  private static final char LF = '\n';
  static final int LRM_BEFORE = 1;
  static final int LRM_AFTER = 2;
  static final int RLM_BEFORE = 4;
  static final int RLM_AFTER = 8;
  BidiBase paraBidi;
  final UBiDiProps bdp;
  char[] text;
  int originalLength;
  public int length;
  int resultLength;
  boolean mayAllocateText;
  boolean mayAllocateRuns;
  byte[] dirPropsMemory = new byte[1];
  byte[] levelsMemory = new byte[1];
  byte[] dirProps;
  byte[] levels;
  boolean orderParagraphsLTR;
  byte paraLevel;
  byte defaultParaLevel;
  ImpTabPair impTabPair;
  byte direction;
  int flags;
  int lastArabicPos;
  int trailingWSStart;
  int paraCount;
  int[] parasMemory = new int[1];
  int[] paras;
  int[] simpleParas = { 0 };
  int runCount;
  BidiRun[] runsMemory = new BidiRun[0];
  BidiRun[] runs;
  BidiRun[] simpleRuns = { new BidiRun() };
  int[] logicalToVisualRunsMap;
  boolean isGoodLogicalToVisualRunsMap;
  InsertPoints insertPoints = new InsertPoints();
  int controlCount;
  static final byte CONTEXT_RTL_SHIFT = 6;
  static final byte CONTEXT_RTL = 64;
  static final int DirPropFlagMultiRuns = DirPropFlag();
  static final int[] DirPropFlagLR = { DirPropFlag(0), DirPropFlag(1) };
  static final int[] DirPropFlagE = { DirPropFlag(11), DirPropFlag(14) };
  static final int[] DirPropFlagO = { DirPropFlag(12), DirPropFlag(15) };
  static final int MASK_LTR = DirPropFlag((byte)0) | DirPropFlag((byte)2) | DirPropFlag((byte)5) | DirPropFlag((byte)11) | DirPropFlag((byte)12);
  static final int MASK_RTL = DirPropFlag((byte)1) | DirPropFlag((byte)13) | DirPropFlag((byte)14) | DirPropFlag((byte)15);
  private static final int MASK_LRX = DirPropFlag((byte)11) | DirPropFlag((byte)12);
  private static final int MASK_RLX = DirPropFlag((byte)14) | DirPropFlag((byte)15);
  private static final int MASK_EXPLICIT = MASK_LRX | MASK_RLX | DirPropFlag((byte)16);
  private static final int MASK_BN_EXPLICIT = DirPropFlag((byte)18) | MASK_EXPLICIT;
  private static final int MASK_B_S = DirPropFlag((byte)7) | DirPropFlag((byte)8);
  static final int MASK_WS = MASK_B_S | DirPropFlag((byte)9) | MASK_BN_EXPLICIT;
  private static final int MASK_N = DirPropFlag((byte)10) | MASK_WS;
  private static final int MASK_POSSIBLE_N = DirPropFlag((byte)6) | DirPropFlag((byte)3) | DirPropFlag((byte)4) | MASK_N;
  static final int MASK_EMBEDDING = DirPropFlag((byte)17) | MASK_POSSIBLE_N;
  private static final int IMPTABPROPS_COLUMNS = 14;
  private static final int IMPTABPROPS_RES = 13;
  private static final short[] groupProp = { 0, 1, 2, 7, 8, 3, 9, 6, 5, 4, 4, 10, 10, 12, 10, 10, 10, 11, 10 };
  private static final short _L = 0;
  private static final short _R = 1;
  private static final short _EN = 2;
  private static final short _AN = 3;
  private static final short _ON = 4;
  private static final short _S = 5;
  private static final short _B = 6;
  private static final short[][] impTabProps = { { 1, 2, 4, 5, 7, 15, 17, 7, 9, 7, 0, 7, 3, 4 }, { 1, 34, 36, 37, 39, 47, 49, 39, 41, 39, 1, 1, 35, 0 }, { 33, 2, 36, 37, 39, 47, 49, 39, 41, 39, 2, 2, 35, 1 }, { 33, 34, 38, 38, 40, 48, 49, 40, 40, 40, 3, 3, 3, 1 }, { 33, 34, 4, 37, 39, 47, 49, 74, 11, 74, 4, 4, 35, 2 }, { 33, 34, 36, 5, 39, 47, 49, 39, 41, 76, 5, 5, 35, 3 }, { 33, 34, 6, 6, 40, 48, 49, 40, 40, 77, 6, 6, 35, 3 }, { 33, 34, 36, 37, 7, 47, 49, 7, 78, 7, 7, 7, 35, 4 }, { 33, 34, 38, 38, 8, 48, 49, 8, 8, 8, 8, 8, 35, 4 }, { 33, 34, 4, 37, 7, 47, 49, 7, 9, 7, 9, 9, 35, 4 }, { 97, 98, 4, 101, 135, 111, 113, 135, 142, 135, 10, 135, 99, 2 }, { 33, 34, 4, 37, 39, 47, 49, 39, 11, 39, 11, 11, 35, 2 }, { 97, 98, 100, 5, 135, 111, 113, 135, 142, 135, 12, 135, 99, 3 }, { 97, 98, 6, 6, 136, 112, 113, 136, 136, 136, 13, 136, 99, 3 }, { 33, 34, 132, 37, 7, 47, 49, 7, 14, 7, 14, 14, 35, 4 }, { 33, 34, 36, 37, 39, 15, 49, 39, 41, 39, 15, 39, 35, 5 }, { 33, 34, 38, 38, 40, 16, 49, 40, 40, 40, 16, 40, 35, 5 }, { 33, 34, 36, 37, 39, 47, 17, 39, 41, 39, 17, 39, 35, 6 } };
  private static final int IMPTABLEVELS_COLUMNS = 8;
  private static final int IMPTABLEVELS_RES = 7;
  private static final byte[][] impTabL_DEFAULT = { { 0, 1, 0, 2, 0, 0, 0, 0 }, { 0, 1, 3, 3, 20, 20, 0, 1 }, { 0, 1, 0, 2, 21, 21, 0, 2 }, { 0, 1, 3, 3, 20, 20, 0, 2 }, { 32, 1, 3, 3, 4, 4, 32, 1 }, { 32, 1, 32, 2, 5, 5, 32, 1 } };
  private static final byte[][] impTabR_DEFAULT = { { 1, 0, 2, 2, 0, 0, 0, 0 }, { 1, 0, 1, 3, 20, 20, 0, 1 }, { 1, 0, 2, 2, 0, 0, 0, 1 }, { 1, 0, 1, 3, 5, 5, 0, 1 }, { 33, 0, 33, 3, 4, 4, 0, 0 }, { 1, 0, 1, 3, 5, 5, 0, 0 } };
  private static final short[] impAct0 = { 0, 1, 2, 3, 4, 5, 6 };
  private static final ImpTabPair impTab_DEFAULT = new ImpTabPair(impTabL_DEFAULT, impTabR_DEFAULT, impAct0, impAct0);
  private static final byte[][] impTabL_NUMBERS_SPECIAL = { { 0, 2, 1, 1, 0, 0, 0, 0 }, { 0, 2, 1, 1, 0, 0, 0, 2 }, { 0, 2, 4, 4, 19, 0, 0, 1 }, { 32, 2, 4, 4, 3, 3, 32, 1 }, { 0, 2, 4, 4, 19, 19, 0, 2 } };
  private static final ImpTabPair impTab_NUMBERS_SPECIAL = new ImpTabPair(impTabL_NUMBERS_SPECIAL, impTabR_DEFAULT, impAct0, impAct0);
  private static final byte[][] impTabL_GROUP_NUMBERS_WITH_R = { { 0, 3, 17, 17, 0, 0, 0, 0 }, { 32, 3, 1, 1, 2, 32, 32, 2 }, { 32, 3, 1, 1, 2, 32, 32, 1 }, { 0, 3, 5, 5, 20, 0, 0, 1 }, { 32, 3, 5, 5, 4, 32, 32, 1 }, { 0, 3, 5, 5, 20, 0, 0, 2 } };
  private static final byte[][] impTabR_GROUP_NUMBERS_WITH_R = { { 2, 0, 1, 1, 0, 0, 0, 0 }, { 2, 0, 1, 1, 0, 0, 0, 1 }, { 2, 0, 20, 20, 19, 0, 0, 1 }, { 34, 0, 4, 4, 3, 0, 0, 0 }, { 34, 0, 4, 4, 3, 0, 0, 1 } };
  private static final ImpTabPair impTab_GROUP_NUMBERS_WITH_R = new ImpTabPair(impTabL_GROUP_NUMBERS_WITH_R, impTabR_GROUP_NUMBERS_WITH_R, impAct0, impAct0);
  private static final byte[][] impTabL_INVERSE_NUMBERS_AS_L = { { 0, 1, 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 20, 20, 0, 1 }, { 0, 1, 0, 0, 21, 21, 0, 2 }, { 0, 1, 0, 0, 20, 20, 0, 2 }, { 32, 1, 32, 32, 4, 4, 32, 1 }, { 32, 1, 32, 32, 5, 5, 32, 1 } };
  private static final byte[][] impTabR_INVERSE_NUMBERS_AS_L = { { 1, 0, 1, 1, 0, 0, 0, 0 }, { 1, 0, 1, 1, 20, 20, 0, 1 }, { 1, 0, 1, 1, 0, 0, 0, 1 }, { 1, 0, 1, 1, 5, 5, 0, 1 }, { 33, 0, 33, 33, 4, 4, 0, 0 }, { 1, 0, 1, 1, 5, 5, 0, 0 } };
  private static final ImpTabPair impTab_INVERSE_NUMBERS_AS_L = new ImpTabPair(impTabL_INVERSE_NUMBERS_AS_L, impTabR_INVERSE_NUMBERS_AS_L, impAct0, impAct0);
  private static final byte[][] impTabR_INVERSE_LIKE_DIRECT = { { 1, 0, 2, 2, 0, 0, 0, 0 }, { 1, 0, 1, 2, 19, 19, 0, 1 }, { 1, 0, 2, 2, 0, 0, 0, 1 }, { 33, 48, 6, 4, 3, 3, 48, 0 }, { 33, 48, 6, 4, 5, 5, 48, 3 }, { 33, 48, 6, 4, 5, 5, 48, 2 }, { 33, 48, 6, 4, 3, 3, 48, 1 } };
  private static final short[] impAct1 = { 0, 1, 11, 12 };
  private static final ImpTabPair impTab_INVERSE_LIKE_DIRECT = new ImpTabPair(impTabL_DEFAULT, impTabR_INVERSE_LIKE_DIRECT, impAct0, impAct1);
  private static final byte[][] impTabL_INVERSE_LIKE_DIRECT_WITH_MARKS = { { 0, 99, 0, 1, 0, 0, 0, 0 }, { 0, 99, 0, 1, 18, 48, 0, 4 }, { 32, 99, 32, 1, 2, 48, 32, 3 }, { 0, 99, 85, 86, 20, 48, 0, 3 }, { 48, 67, 85, 86, 4, 48, 48, 3 }, { 48, 67, 5, 86, 20, 48, 48, 4 }, { 48, 67, 85, 6, 20, 48, 48, 4 } };
  private static final byte[][] impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS = { { 19, 0, 1, 1, 0, 0, 0, 0 }, { 35, 0, 1, 1, 2, 64, 0, 1 }, { 35, 0, 1, 1, 2, 64, 0, 0 }, { 3, 0, 3, 54, 20, 64, 0, 1 }, { 83, 64, 5, 54, 4, 64, 64, 0 }, { 83, 64, 5, 54, 4, 64, 64, 1 }, { 83, 64, 6, 6, 4, 64, 64, 3 } };
  private static final short[] impAct2 = { 0, 1, 7, 8, 9, 10 };
  private static final ImpTabPair impTab_INVERSE_LIKE_DIRECT_WITH_MARKS = new ImpTabPair(impTabL_INVERSE_LIKE_DIRECT_WITH_MARKS, impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS, impAct0, impAct2);
  private static final ImpTabPair impTab_INVERSE_FOR_NUMBERS_SPECIAL = new ImpTabPair(impTabL_NUMBERS_SPECIAL, impTabR_INVERSE_LIKE_DIRECT, impAct0, impAct1);
  private static final byte[][] impTabL_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS = { { 0, 98, 1, 1, 0, 0, 0, 0 }, { 0, 98, 1, 1, 0, 48, 0, 4 }, { 0, 98, 84, 84, 19, 48, 0, 3 }, { 48, 66, 84, 84, 3, 48, 48, 3 }, { 48, 66, 4, 4, 19, 48, 48, 4 } };
  private static final ImpTabPair impTab_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS = new ImpTabPair(impTabL_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS, impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS, impAct0, impAct2);
  static final int FIRSTALLOC = 10;
  private static final int INTERNAL_DIRECTION_DEFAULT_LEFT_TO_RIGHT = 126;
  private static final int INTERMAL_DIRECTION_DEFAULT_RIGHT_TO_LEFT = 127;
  
  static int DirPropFlag(byte paramByte)
  {
    return 1 << paramByte;
  }
  
  static byte NoContextRTL(byte paramByte)
  {
    return (byte)(paramByte & 0xFFFFFFBF);
  }
  
  static int DirPropFlagNC(byte paramByte)
  {
    return 1 << (paramByte & 0xFFFFFFBF);
  }
  
  static final int DirPropFlagLR(byte paramByte)
  {
    return DirPropFlagLR[(paramByte & 0x1)];
  }
  
  static final int DirPropFlagE(byte paramByte)
  {
    return DirPropFlagE[(paramByte & 0x1)];
  }
  
  static final int DirPropFlagO(byte paramByte)
  {
    return DirPropFlagO[(paramByte & 0x1)];
  }
  
  private static byte GetLRFromLevel(byte paramByte)
  {
    return (byte)(paramByte & 0x1);
  }
  
  private static boolean IsDefaultLevel(byte paramByte)
  {
    return (paramByte & 0x7E) == 126;
  }
  
  byte GetParaLevelAt(int paramInt)
  {
    return defaultParaLevel != 0 ? (byte)(dirProps[paramInt] >> 6) : paraLevel;
  }
  
  static boolean IsBidiControlChar(int paramInt)
  {
    return ((paramInt & 0xFFFFFFFC) == 8204) || ((paramInt >= 8234) && (paramInt <= 8238));
  }
  
  public void verifyValidPara()
  {
    if (this != paraBidi) {
      throw new IllegalStateException("");
    }
  }
  
  public void verifyValidParaOrLine()
  {
    BidiBase localBidiBase = paraBidi;
    if (this == localBidiBase) {
      return;
    }
    if ((localBidiBase == null) || (localBidiBase != paraBidi)) {
      throw new IllegalStateException();
    }
  }
  
  public void verifyRange(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 < paramInt2) || (paramInt1 >= paramInt3)) {
      throw new IllegalArgumentException("Value " + paramInt1 + " is out of range " + paramInt2 + " to " + paramInt3);
    }
  }
  
  public void verifyIndex(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 < paramInt2) || (paramInt1 >= paramInt3)) {
      throw new ArrayIndexOutOfBoundsException("Index " + paramInt1 + " is out of range " + paramInt2 + " to " + paramInt3);
    }
  }
  
  public BidiBase(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0)) {
      throw new IllegalArgumentException();
    }
    try
    {
      bdp = UBiDiProps.getSingleton();
    }
    catch (IOException localIOException)
    {
      throw new MissingResourceException(localIOException.getMessage(), "(BidiProps)", "");
    }
    if (paramInt1 > 0)
    {
      getInitialDirPropsMemory(paramInt1);
      getInitialLevelsMemory(paramInt1);
    }
    else
    {
      mayAllocateText = true;
    }
    if (paramInt2 > 0)
    {
      if (paramInt2 > 1) {
        getInitialRunsMemory(paramInt2);
      }
    }
    else {
      mayAllocateRuns = true;
    }
  }
  
  private Object getMemory(String paramString, Object paramObject, Class<?> paramClass, boolean paramBoolean, int paramInt)
  {
    int i = Array.getLength(paramObject);
    if (paramInt == i) {
      return paramObject;
    }
    if (!paramBoolean)
    {
      if (paramInt <= i) {
        return paramObject;
      }
      throw new OutOfMemoryError("Failed to allocate memory for " + paramString);
    }
    try
    {
      return Array.newInstance(paramClass, paramInt);
    }
    catch (Exception localException)
    {
      throw new OutOfMemoryError("Failed to allocate memory for " + paramString);
    }
  }
  
  private void getDirPropsMemory(boolean paramBoolean, int paramInt)
  {
    Object localObject = getMemory("DirProps", dirPropsMemory, Byte.TYPE, paramBoolean, paramInt);
    dirPropsMemory = ((byte[])localObject);
  }
  
  void getDirPropsMemory(int paramInt)
  {
    getDirPropsMemory(mayAllocateText, paramInt);
  }
  
  private void getLevelsMemory(boolean paramBoolean, int paramInt)
  {
    Object localObject = getMemory("Levels", levelsMemory, Byte.TYPE, paramBoolean, paramInt);
    levelsMemory = ((byte[])localObject);
  }
  
  void getLevelsMemory(int paramInt)
  {
    getLevelsMemory(mayAllocateText, paramInt);
  }
  
  private void getRunsMemory(boolean paramBoolean, int paramInt)
  {
    Object localObject = getMemory("Runs", runsMemory, BidiRun.class, paramBoolean, paramInt);
    runsMemory = ((BidiRun[])localObject);
  }
  
  void getRunsMemory(int paramInt)
  {
    getRunsMemory(mayAllocateRuns, paramInt);
  }
  
  private void getInitialDirPropsMemory(int paramInt)
  {
    getDirPropsMemory(true, paramInt);
  }
  
  private void getInitialLevelsMemory(int paramInt)
  {
    getLevelsMemory(true, paramInt);
  }
  
  private void getInitialParasMemory(int paramInt)
  {
    Object localObject = getMemory("Paras", parasMemory, Integer.TYPE, true, paramInt);
    parasMemory = ((int[])localObject);
  }
  
  private void getInitialRunsMemory(int paramInt)
  {
    getRunsMemory(true, paramInt);
  }
  
  private void getDirProps()
  {
    int i = 0;
    flags = 0;
    byte b2 = 0;
    boolean bool = IsDefaultLevel(paraLevel);
    lastArabicPos = -1;
    controlCount = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    byte b3;
    int n;
    if (bool)
    {
      b2 = (paraLevel & 0x1) != 0 ? 64 : 0;
      b3 = b2;
      i2 = b2;
      n = 1;
    }
    else
    {
      n = 0;
      b3 = 0;
    }
    i = 0;
    while (i < originalLength)
    {
      int j = i;
      int m = UTF16.charAt(text, 0, originalLength, i);
      i += Character.charCount(m);
      int k = i - 1;
      byte b1 = (byte)bdp.getClass(m);
      flags |= DirPropFlag(b1);
      dirProps[k] = ((byte)(b1 | b3));
      if (k > j)
      {
        flags |= DirPropFlag((byte)18);
        do
        {
          dirProps[(--k)] = ((byte)(0x12 | b3));
        } while (k > j);
      }
      if (n == 1)
      {
        if (b1 == 0)
        {
          n = 2;
          if (b3 == 0) {
            continue;
          }
          b3 = 0;
          for (k = i1; k < i; k++)
          {
            int tmp231_230 = k;
            byte[] tmp231_227 = dirProps;
            tmp231_227[tmp231_230] = ((byte)(tmp231_227[tmp231_230] & 0xFFFFFFBF));
          }
          continue;
        }
        if ((b1 == 1) || (b1 == 13))
        {
          n = 2;
          if (tmp231_230 != 0) {
            continue;
          }
          tmp231_230 = 64;
          for (k = i1; k < i; k++)
          {
            int tmp282_281 = k;
            byte[] tmp282_278 = dirProps;
            tmp282_278[tmp282_281] = ((byte)(tmp282_278[tmp282_281] | 0x40));
          }
          continue;
        }
      }
      if (b1 == 0)
      {
        tmp231_227 = 0;
        tmp282_281 = i;
      }
      else if (b1 == 1)
      {
        tmp231_227 = 64;
      }
      else if (b1 == 13)
      {
        tmp231_227 = 64;
        lastArabicPos = (i - 1);
      }
      else if ((b1 == 7) && (i < originalLength))
      {
        if ((m != 13) || (text[i] != '\n')) {
          paraCount += 1;
        }
        if (bool)
        {
          n = 1;
          i1 = i;
          tmp231_230 = b2;
          tmp231_227 = b2;
        }
      }
    }
    if (bool) {
      paraLevel = GetParaLevelAt(0);
    }
    flags |= DirPropFlagLR(paraLevel);
    if ((orderParagraphsLTR) && ((flags & DirPropFlag((byte)7)) != 0)) {
      flags |= DirPropFlag((byte)0);
    }
  }
  
  private byte directionFromFlags()
  {
    if (((flags & MASK_RTL) == 0) && (((flags & DirPropFlag((byte)5)) == 0) || ((flags & MASK_POSSIBLE_N) == 0))) {
      return 0;
    }
    if ((flags & MASK_LTR) == 0) {
      return 1;
    }
    return 2;
  }
  
  private byte resolveExplicitLevels()
  {
    int i = 0;
    byte b2 = GetParaLevelAt(0);
    int j = 0;
    byte b3 = directionFromFlags();
    if ((b3 == 2) || (paraCount != 1))
    {
      if ((paraCount == 1) && ((flags & MASK_EXPLICIT) == 0)) {
        i = 0;
      }
      while (i < length)
      {
        levels[i] = b2;
        i++;
        continue;
        byte b4 = b2;
        int k = 0;
        byte[] arrayOfByte = new byte[61];
        int m = 0;
        int n = 0;
        flags = 0;
        for (i = 0; i < length; i++)
        {
          byte b1 = NoContextRTL(dirProps[i]);
          byte b5;
          switch (b1)
          {
          case 11: 
          case 12: 
            b5 = (byte)(b4 + 2 & 0x7E);
            if (b5 <= 61)
            {
              arrayOfByte[k] = b4;
              k = (byte)(k + 1);
              b4 = b5;
              if (b1 == 12) {
                b4 = (byte)(b4 | 0xFFFFFF80);
              }
            }
            else if ((b4 & 0x7F) == 61)
            {
              n++;
            }
            else
            {
              m++;
            }
            flags |= DirPropFlag((byte)18);
            break;
          case 14: 
          case 15: 
            b5 = (byte)((b4 & 0x7F) + 1 | 0x1);
            if (b5 <= 61)
            {
              arrayOfByte[k] = b4;
              k = (byte)(k + 1);
              b4 = b5;
              if (b1 == 15) {
                b4 = (byte)(b4 | 0xFFFFFF80);
              }
            }
            else
            {
              n++;
            }
            flags |= DirPropFlag((byte)18);
            break;
          case 16: 
            if (n > 0)
            {
              n--;
            }
            else if ((m > 0) && ((b4 & 0x7F) != 61))
            {
              m--;
            }
            else if (k > 0)
            {
              k = (byte)(k - 1);
              b4 = arrayOfByte[k];
            }
            flags |= DirPropFlag((byte)18);
            break;
          case 7: 
            k = 0;
            m = 0;
            n = 0;
            b2 = GetParaLevelAt(i);
            if (i + 1 < length)
            {
              b4 = GetParaLevelAt(i + 1);
              if ((text[i] != '\r') || (text[(i + 1)] != '\n')) {
                paras[(j++)] = (i + 1);
              }
            }
            flags |= DirPropFlag((byte)7);
            break;
          case 18: 
            flags |= DirPropFlag((byte)18);
            break;
          case 8: 
          case 9: 
          case 10: 
          case 13: 
          case 17: 
          default: 
            if (b2 != b4)
            {
              b2 = b4;
              if ((b2 & 0xFFFFFF80) != 0) {
                flags |= DirPropFlagO(b2) | DirPropFlagMultiRuns;
              } else {
                flags |= DirPropFlagE(b2) | DirPropFlagMultiRuns;
              }
            }
            if ((b2 & 0xFFFFFF80) == 0) {
              flags |= DirPropFlag(b1);
            }
            break;
          }
          levels[i] = b2;
        }
        if ((flags & MASK_EMBEDDING) != 0) {
          flags |= DirPropFlagLR(paraLevel);
        }
        if ((orderParagraphsLTR) && ((flags & DirPropFlag((byte)7)) != 0)) {
          flags |= DirPropFlag((byte)0);
        }
        b3 = directionFromFlags();
      }
    }
    return b3;
  }
  
  private byte checkExplicitLevels()
  {
    flags = 0;
    int j = 0;
    for (int i = 0; i < length; i++)
    {
      if (levels[i] == 0) {
        levels[i] = paraLevel;
      }
      if (61 < (levels[i] & 0x7F)) {
        if ((levels[i] & 0xFFFFFF80) != 0) {
          levels[i] = ((byte)(paraLevel | 0xFFFFFF80));
        } else {
          levels[i] = paraLevel;
        }
      }
      byte b2 = levels[i];
      byte b1 = NoContextRTL(dirProps[i]);
      if ((b2 & 0xFFFFFF80) != 0)
      {
        b2 = (byte)(b2 & 0x7F);
        flags |= DirPropFlagO(b2);
      }
      else
      {
        flags |= DirPropFlagE(b2) | DirPropFlag(b1);
      }
      if (((b2 < GetParaLevelAt(i)) && ((0 != b2) || (b1 != 7))) || (61 < b2)) {
        throw new IllegalArgumentException("level " + b2 + " out of bounds at index " + i);
      }
      if ((b1 == 7) && (i + 1 < length) && ((text[i] != '\r') || (text[(i + 1)] != '\n'))) {
        paras[(j++)] = (i + 1);
      }
    }
    if ((flags & MASK_EMBEDDING) != 0) {
      flags |= DirPropFlagLR(paraLevel);
    }
    return directionFromFlags();
  }
  
  private static short GetStateProps(short paramShort)
  {
    return (short)(paramShort & 0x1F);
  }
  
  private static short GetActionProps(short paramShort)
  {
    return (short)(paramShort >> 5);
  }
  
  private static short GetState(byte paramByte)
  {
    return (short)(paramByte & 0xF);
  }
  
  private static short GetAction(byte paramByte)
  {
    return (short)(paramByte >> 4);
  }
  
  private void addPoint(int paramInt1, int paramInt2)
  {
    Point localPoint = new Point();
    int i = insertPoints.points.length;
    if (i == 0)
    {
      insertPoints.points = new Point[10];
      i = 10;
    }
    if (insertPoints.size >= i)
    {
      Point[] arrayOfPoint = insertPoints.points;
      insertPoints.points = new Point[i * 2];
      System.arraycopy(arrayOfPoint, 0, insertPoints.points, 0, i);
    }
    pos = paramInt1;
    flag = paramInt2;
    insertPoints.points[insertPoints.size] = localPoint;
    insertPoints.size += 1;
  }
  
  private void processPropertySeq(LevState paramLevState, short paramShort, int paramInt1, int paramInt2)
  {
    byte[][] arrayOfByte = impTab;
    short[] arrayOfShort = impAct;
    int n = paramInt1;
    int i = state;
    byte b = arrayOfByte[i][paramShort];
    state = GetState(b);
    int j = arrayOfShort[GetAction(b)];
    int m = arrayOfByte[state][7];
    int k;
    int i1;
    if (j != 0) {
      switch (j)
      {
      case 1: 
        startON = n;
        break;
      case 2: 
        paramInt1 = startON;
        break;
      case 3: 
        if (startL2EN >= 0) {
          addPoint(startL2EN, 1);
        }
        startL2EN = -1;
        if ((insertPoints.points.length == 0) || (insertPoints.size <= insertPoints.confirmed))
        {
          lastStrongRTL = -1;
          k = arrayOfByte[i][7];
          if (((k & 0x1) != 0) && (startON > 0)) {
            paramInt1 = startON;
          }
          if (paramShort == 5)
          {
            addPoint(n, 1);
            insertPoints.confirmed = insertPoints.size;
          }
        }
        else
        {
          for (i1 = lastStrongRTL + 1; i1 < n; i1++) {
            levels[i1] = ((byte)(levels[i1] - 2 & 0xFFFFFFFE));
          }
          insertPoints.confirmed = insertPoints.size;
          lastStrongRTL = -1;
          if (paramShort == 5)
          {
            addPoint(n, 1);
            insertPoints.confirmed = insertPoints.size;
          }
        }
        break;
      case 4: 
        if (insertPoints.points.length > 0) {
          insertPoints.size = insertPoints.confirmed;
        }
        startON = -1;
        startL2EN = -1;
        lastStrongRTL = (paramInt2 - 1);
        break;
      case 5: 
        if ((paramShort == 3) && (NoContextRTL(dirProps[n]) == 5))
        {
          if (startL2EN == -1)
          {
            lastStrongRTL = (paramInt2 - 1);
          }
          else
          {
            if (startL2EN >= 0)
            {
              addPoint(startL2EN, 1);
              startL2EN = -2;
            }
            addPoint(n, 1);
          }
        }
        else if (startL2EN == -1) {
          startL2EN = n;
        }
        break;
      case 6: 
        lastStrongRTL = (paramInt2 - 1);
        startON = -1;
        break;
      case 7: 
        for (i1 = n - 1; (i1 >= 0) && ((levels[i1] & 0x1) == 0); i1--) {}
        if (i1 >= 0)
        {
          addPoint(i1, 4);
          insertPoints.confirmed = insertPoints.size;
        }
        startON = n;
        break;
      case 8: 
        addPoint(n, 1);
        addPoint(n, 2);
        break;
      case 9: 
        insertPoints.size = insertPoints.confirmed;
        if (paramShort == 5)
        {
          addPoint(n, 4);
          insertPoints.confirmed = insertPoints.size;
        }
        break;
      case 10: 
        k = (byte)(runLevel + m);
        for (i1 = startON; i1 < n; i1++) {
          if (levels[i1] < k) {
            levels[i1] = k;
          }
        }
        insertPoints.confirmed = insertPoints.size;
        startON = n;
        break;
      case 11: 
        k = runLevel;
        i1 = n - 1;
      case 12: 
      default: 
        while (i1 >= startON)
        {
          if (levels[i1] == k + 3)
          {
            while (levels[i1] == k + 3)
            {
              int tmp754_751 = (i1--);
              byte[] tmp754_746 = levels;
              tmp754_746[tmp754_751] = ((byte)(tmp754_746[tmp754_751] - 2));
            }
            while (levels[i1] == k) {
              i1--;
            }
          }
          if (levels[i1] == k + 2) {
            levels[i1] = k;
          } else {
            levels[i1] = ((byte)(k + 1));
          }
          i1--;
          continue;
          k = (byte)(runLevel + 1);
          i1 = n - 1;
          while (i1 >= startON)
          {
            if (levels[i1] > k)
            {
              int tmp867_865 = i1;
              byte[] tmp867_862 = levels;
              tmp867_862[tmp867_865] = ((byte)(tmp867_862[tmp867_865] - 2));
            }
            i1--;
            continue;
            throw new IllegalStateException("Internal ICU error in processPropertySeq");
          }
        }
      }
    }
    if ((m != 0) || (paramInt1 < n))
    {
      k = (byte)(runLevel + m);
      for (i1 = paramInt1; i1 < paramInt2; i1++) {
        levels[i1] = k;
      }
    }
  }
  
  private void resolveImplicitLevels(int paramInt1, int paramInt2, short paramShort1, short paramShort2)
  {
    LevState localLevState = new LevState(null);
    int i3 = 1;
    int i4 = -1;
    startL2EN = -1;
    lastStrongRTL = -1;
    state = 0;
    runLevel = levels[paramInt1];
    impTab = impTabPair.imptab[(runLevel & 0x1)];
    impAct = impTabPair.impact[(runLevel & 0x1)];
    processPropertySeq(localLevState, paramShort1, paramInt1, paramInt1);
    int n;
    if (dirProps[paramInt1] == 17) {
      n = (short)(1 + paramShort1);
    } else {
      n = 0;
    }
    int j = paramInt1;
    int k = 0;
    for (int i = paramInt1; i <= paramInt2; i++)
    {
      int i2;
      if (i >= paramInt2)
      {
        i2 = paramShort2;
      }
      else
      {
        int i5 = (short)NoContextRTL(dirProps[i]);
        i2 = groupProp[i5];
      }
      int m = n;
      short s2 = impTabProps[m][i2];
      n = GetStateProps(s2);
      int i1 = GetActionProps(s2);
      if ((i == paramInt2) && (i1 == 0)) {
        i1 = 1;
      }
      if (i1 != 0)
      {
        short s1 = impTabProps[m][13];
        switch (i1)
        {
        case 1: 
          processPropertySeq(localLevState, s1, j, i);
          j = i;
          break;
        case 2: 
          k = i;
          break;
        case 3: 
          processPropertySeq(localLevState, s1, j, k);
          processPropertySeq(localLevState, (short)4, k, i);
          j = i;
          break;
        case 4: 
          processPropertySeq(localLevState, s1, j, k);
          j = k;
          k = i;
          break;
        default: 
          throw new IllegalStateException("Internal ICU error in resolveImplicitLevels");
        }
      }
    }
    processPropertySeq(localLevState, paramShort2, paramInt2, paramInt2);
  }
  
  private void adjustWSLevels()
  {
    if ((flags & MASK_WS) != 0)
    {
      int i = trailingWSStart;
      for (;;)
      {
        if (i <= 0) {
          return;
        }
        int j;
        while ((i > 0) && (((j = DirPropFlagNC(dirProps[(--i)])) & MASK_WS) != 0)) {
          if ((orderParagraphsLTR) && ((j & DirPropFlag((byte)7)) != 0)) {
            levels[i] = 0;
          } else {
            levels[i] = GetParaLevelAt(i);
          }
        }
        while (i > 0)
        {
          j = DirPropFlagNC(dirProps[(--i)]);
          if ((j & MASK_BN_EXPLICIT) == 0) {
            break label128;
          }
          levels[i] = levels[(i + 1)];
        }
        continue;
        label128:
        if ((orderParagraphsLTR) && ((j & DirPropFlag((byte)7)) != 0))
        {
          levels[i] = 0;
        }
        else
        {
          if ((j & MASK_B_S) == 0) {
            break;
          }
          levels[i] = GetParaLevelAt(i);
        }
      }
    }
  }
  
  private int Bidi_Min(int paramInt1, int paramInt2)
  {
    return paramInt1 < paramInt2 ? paramInt1 : paramInt2;
  }
  
  private int Bidi_Abs(int paramInt)
  {
    return paramInt >= 0 ? paramInt : -paramInt;
  }
  
  void setPara(String paramString, byte paramByte, byte[] paramArrayOfByte)
  {
    if (paramString == null) {
      setPara(new char[0], paramByte, paramArrayOfByte);
    } else {
      setPara(paramString.toCharArray(), paramByte, paramArrayOfByte);
    }
  }
  
  public void setPara(char[] paramArrayOfChar, byte paramByte, byte[] paramArrayOfByte)
  {
    if (paramByte < 126) {
      verifyRange(paramByte, 0, 62);
    }
    if (paramArrayOfChar == null) {
      paramArrayOfChar = new char[0];
    }
    paraBidi = null;
    text = paramArrayOfChar;
    length = (originalLength = resultLength = text.length);
    paraLevel = paramByte;
    direction = 0;
    paraCount = 1;
    dirProps = new byte[0];
    levels = new byte[0];
    runs = new BidiRun[0];
    isGoodLogicalToVisualRunsMap = false;
    insertPoints.size = 0;
    insertPoints.confirmed = 0;
    if (IsDefaultLevel(paramByte)) {
      defaultParaLevel = paramByte;
    } else {
      defaultParaLevel = 0;
    }
    if (length == 0)
    {
      if (IsDefaultLevel(paramByte))
      {
        paraLevel = ((byte)(paraLevel & 0x1));
        defaultParaLevel = 0;
      }
      if ((paraLevel & 0x1) != 0)
      {
        flags = DirPropFlag((byte)1);
        direction = 1;
      }
      else
      {
        flags = DirPropFlag((byte)0);
        direction = 0;
      }
      runCount = 0;
      paraCount = 0;
      paraBidi = this;
      return;
    }
    runCount = -1;
    getDirPropsMemory(length);
    dirProps = dirPropsMemory;
    getDirProps();
    trailingWSStart = length;
    if (paraCount > 1)
    {
      getInitialParasMemory(paraCount);
      paras = parasMemory;
      paras[(paraCount - 1)] = length;
    }
    else
    {
      paras = simpleParas;
      simpleParas[0] = length;
    }
    if (paramArrayOfByte == null)
    {
      getLevelsMemory(length);
      levels = levelsMemory;
      direction = resolveExplicitLevels();
    }
    else
    {
      levels = paramArrayOfByte;
      direction = checkExplicitLevels();
    }
    switch (direction)
    {
    case 0: 
      paramByte = (byte)(paramByte + 1 & 0xFFFFFFFE);
      trailingWSStart = 0;
      break;
    case 1: 
      paramByte = (byte)(paramByte | 0x1);
      trailingWSStart = 0;
      break;
    default: 
      impTabPair = impTab_DEFAULT;
      if ((paramArrayOfByte == null) && (paraCount <= 1) && ((flags & DirPropFlagMultiRuns) == 0))
      {
        resolveImplicitLevels(0, length, (short)GetLRFromLevel(GetParaLevelAt(0)), (short)GetLRFromLevel(GetParaLevelAt(length - 1)));
      }
      else
      {
        int j = 0;
        byte b1 = GetParaLevelAt(0);
        byte b2 = levels[0];
        short s2;
        if (b1 < b2) {
          s2 = (short)GetLRFromLevel(b2);
        } else {
          s2 = (short)GetLRFromLevel(b1);
        }
        do
        {
          int i = j;
          b1 = b2;
          short s1;
          if ((i > 0) && (NoContextRTL(dirProps[(i - 1)]) == 7)) {
            s1 = (short)GetLRFromLevel(GetParaLevelAt(i));
          } else {
            s1 = s2;
          }
          do
          {
            j++;
          } while ((j < length) && (levels[j] == b1));
          if (j < length) {
            b2 = levels[j];
          } else {
            b2 = GetParaLevelAt(length - 1);
          }
          if ((b1 & 0x7F) < (b2 & 0x7F)) {
            s2 = (short)GetLRFromLevel(b2);
          } else {
            s2 = (short)GetLRFromLevel(b1);
          }
          if ((b1 & 0xFFFFFF80) == 0) {
            resolveImplicitLevels(i, j, s1, s2);
          } else {
            do
            {
              int tmp691_688 = (i++);
              byte[] tmp691_683 = levels;
              tmp691_683[tmp691_688] = ((byte)(tmp691_683[tmp691_688] & 0x7F));
            } while (i < j);
          }
        } while (j < length);
      }
      adjustWSLevels();
    }
    resultLength += insertPoints.size;
    paraBidi = this;
  }
  
  public void setPara(AttributedCharacterIterator paramAttributedCharacterIterator)
  {
    int i = paramAttributedCharacterIterator.first();
    Boolean localBoolean = (Boolean)paramAttributedCharacterIterator.getAttribute(TextAttributeConstants.RUN_DIRECTION);
    Object localObject1 = paramAttributedCharacterIterator.getAttribute(TextAttributeConstants.NUMERIC_SHAPING);
    byte b;
    if (localBoolean == null) {
      b = 126;
    } else {
      b = localBoolean.equals(TextAttributeConstants.RUN_DIRECTION_LTR) ? 0 : 1;
    }
    Object localObject2 = null;
    int j = paramAttributedCharacterIterator.getEndIndex() - paramAttributedCharacterIterator.getBeginIndex();
    byte[] arrayOfByte = new byte[j];
    char[] arrayOfChar = new char[j];
    for (int k = 0; i != 65535; k++)
    {
      arrayOfChar[k] = i;
      Integer localInteger = (Integer)paramAttributedCharacterIterator.getAttribute(TextAttributeConstants.BIDI_EMBEDDING);
      if (localInteger != null)
      {
        int m = localInteger.byteValue();
        if (m != 0) {
          if (m < 0)
          {
            localObject2 = arrayOfByte;
            arrayOfByte[k] = ((byte)(0 - m | 0xFFFFFF80));
          }
          else
          {
            localObject2 = arrayOfByte;
            arrayOfByte[k] = m;
          }
        }
      }
      i = paramAttributedCharacterIterator.next();
    }
    if (localObject1 != null) {
      NumericShapings.shape(localObject1, arrayOfChar, 0, j);
    }
    setPara(arrayOfChar, b, (byte[])localObject2);
  }
  
  private void orderParagraphsLTR(boolean paramBoolean)
  {
    orderParagraphsLTR = paramBoolean;
  }
  
  private byte getDirection()
  {
    verifyValidParaOrLine();
    return direction;
  }
  
  public int getLength()
  {
    verifyValidParaOrLine();
    return originalLength;
  }
  
  public byte getParaLevel()
  {
    verifyValidParaOrLine();
    return paraLevel;
  }
  
  public int getParagraphIndex(int paramInt)
  {
    verifyValidParaOrLine();
    BidiBase localBidiBase = paraBidi;
    verifyRange(paramInt, 0, length);
    for (int i = 0; paramInt >= paras[i]; i++) {}
    return i;
  }
  
  public Bidi setLine(Bidi paramBidi1, BidiBase paramBidiBase1, Bidi paramBidi2, BidiBase paramBidiBase2, int paramInt1, int paramInt2)
  {
    verifyValidPara();
    verifyRange(paramInt1, 0, paramInt2);
    verifyRange(paramInt2, 0, length + 1);
    return BidiLine.setLine(paramBidi1, this, paramBidi2, paramBidiBase2, paramInt1, paramInt2);
  }
  
  public byte getLevelAt(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= length)) {
      return (byte)getBaseLevel();
    }
    verifyValidParaOrLine();
    verifyRange(paramInt, 0, length);
    return BidiLine.getLevelAt(this, paramInt);
  }
  
  private byte[] getLevels()
  {
    verifyValidParaOrLine();
    if (length <= 0) {
      return new byte[0];
    }
    return BidiLine.getLevels(this);
  }
  
  public int countRuns()
  {
    verifyValidParaOrLine();
    BidiLine.getRuns(this);
    return runCount;
  }
  
  private int[] getVisualMap()
  {
    countRuns();
    if (resultLength <= 0) {
      return new int[0];
    }
    return BidiLine.getVisualMap(this);
  }
  
  private static int[] reorderVisual(byte[] paramArrayOfByte)
  {
    return BidiLine.reorderVisual(paramArrayOfByte);
  }
  
  public BidiBase(char[] paramArrayOfChar, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, int paramInt4)
  {
    this(0, 0);
    byte b;
    switch (paramInt4)
    {
    case 0: 
    default: 
      b = 0;
      break;
    case 1: 
      b = 1;
      break;
    case -2: 
      b = 126;
      break;
    case -1: 
      b = Byte.MAX_VALUE;
    }
    byte[] arrayOfByte;
    if (paramArrayOfByte == null)
    {
      arrayOfByte = null;
    }
    else
    {
      arrayOfByte = new byte[paramInt3];
      for (int j = 0; j < paramInt3; j++)
      {
        int i = paramArrayOfByte[(j + paramInt2)];
        if (i < 0)
        {
          i = (byte)(-i | 0xFFFFFF80);
        }
        else if (i == 0)
        {
          i = b;
          if (b > 61) {
            i = (byte)(i & 0x1);
          }
        }
        arrayOfByte[j] = i;
      }
    }
    if ((paramInt1 == 0) && (paramInt2 == 0) && (paramInt3 == paramArrayOfChar.length))
    {
      setPara(paramArrayOfChar, b, arrayOfByte);
    }
    else
    {
      char[] arrayOfChar = new char[paramInt3];
      System.arraycopy(paramArrayOfChar, paramInt1, arrayOfChar, 0, paramInt3);
      setPara(arrayOfChar, b, arrayOfByte);
    }
  }
  
  public boolean isMixed()
  {
    return (!isLeftToRight()) && (!isRightToLeft());
  }
  
  public boolean isLeftToRight()
  {
    return (getDirection() == 0) && ((paraLevel & 0x1) == 0);
  }
  
  public boolean isRightToLeft()
  {
    return (getDirection() == 1) && ((paraLevel & 0x1) == 1);
  }
  
  public boolean baseIsLeftToRight()
  {
    return getParaLevel() == 0;
  }
  
  public int getBaseLevel()
  {
    return getParaLevel();
  }
  
  private void getLogicalToVisualRunsMap()
  {
    if (isGoodLogicalToVisualRunsMap) {
      return;
    }
    int i = countRuns();
    if ((logicalToVisualRunsMap == null) || (logicalToVisualRunsMap.length < i)) {
      logicalToVisualRunsMap = new int[i];
    }
    long[] arrayOfLong = new long[i];
    for (int j = 0; j < i; j++) {
      arrayOfLong[j] = ((runs[j].start << 32) + j);
    }
    Arrays.sort(arrayOfLong);
    for (j = 0; j < i; j++) {
      logicalToVisualRunsMap[j] = ((int)(arrayOfLong[j] & 0xFFFFFFFFFFFFFFFF));
    }
    arrayOfLong = null;
    isGoodLogicalToVisualRunsMap = true;
  }
  
  public int getRunLevel(int paramInt)
  {
    verifyValidParaOrLine();
    BidiLine.getRuns(this);
    if ((paramInt < 0) || (paramInt >= runCount)) {
      return getParaLevel();
    }
    getLogicalToVisualRunsMap();
    return runs[logicalToVisualRunsMap[paramInt]].level;
  }
  
  public int getRunStart(int paramInt)
  {
    verifyValidParaOrLine();
    BidiLine.getRuns(this);
    if (runCount == 1) {
      return 0;
    }
    if (paramInt == runCount) {
      return length;
    }
    verifyIndex(paramInt, 0, runCount);
    getLogicalToVisualRunsMap();
    return runs[logicalToVisualRunsMap[paramInt]].start;
  }
  
  public int getRunLimit(int paramInt)
  {
    verifyValidParaOrLine();
    BidiLine.getRuns(this);
    if (runCount == 1) {
      return length;
    }
    verifyIndex(paramInt, 0, runCount);
    getLogicalToVisualRunsMap();
    int i = logicalToVisualRunsMap[paramInt];
    int j = i == 0 ? runs[i].limit : runs[i].limit - runs[(i - 1)].limit;
    return runs[i].start + j;
  }
  
  public static boolean requiresBidi(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if ((0 > paramInt1) || (paramInt1 > paramInt2) || (paramInt2 > paramArrayOfChar.length)) {
      throw new IllegalArgumentException("Value start " + paramInt1 + " is out of range 0 to " + paramInt2);
    }
    for (int i = paramInt1; i < paramInt2; i++) {
      if ((Character.isHighSurrogate(paramArrayOfChar[i])) && (i < paramInt2 - 1) && (Character.isLowSurrogate(paramArrayOfChar[(i + 1)])))
      {
        if ((1 << UCharacter.getDirection(Character.codePointAt(paramArrayOfChar, i)) & 0xE022) != 0) {
          return true;
        }
      }
      else if ((1 << UCharacter.getDirection(paramArrayOfChar[i]) & 0xE022) != 0) {
        return true;
      }
    }
    return false;
  }
  
  public static void reorderVisually(byte[] paramArrayOfByte, int paramInt1, Object[] paramArrayOfObject, int paramInt2, int paramInt3)
  {
    if ((0 > paramInt1) || (paramArrayOfByte.length <= paramInt1)) {
      throw new IllegalArgumentException("Value levelStart " + paramInt1 + " is out of range 0 to " + (paramArrayOfByte.length - 1));
    }
    if ((0 > paramInt2) || (paramArrayOfObject.length <= paramInt2)) {
      throw new IllegalArgumentException("Value objectStart " + paramInt1 + " is out of range 0 to " + (paramArrayOfObject.length - 1));
    }
    if ((0 > paramInt3) || (paramArrayOfObject.length < paramInt2 + paramInt3)) {
      throw new IllegalArgumentException("Value count " + paramInt1 + " is out of range 0 to " + (paramArrayOfObject.length - paramInt2));
    }
    byte[] arrayOfByte = new byte[paramInt3];
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt3);
    int[] arrayOfInt = reorderVisual(arrayOfByte);
    Object[] arrayOfObject = new Object[paramInt3];
    System.arraycopy(paramArrayOfObject, paramInt2, arrayOfObject, 0, paramInt3);
    for (int i = 0; i < paramInt3; i++) {
      paramArrayOfObject[(paramInt2 + i)] = arrayOfObject[arrayOfInt[i]];
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(getClass().getName());
    localStringBuilder.append("[dir: ");
    localStringBuilder.append(direction);
    localStringBuilder.append(" baselevel: ");
    localStringBuilder.append(paraLevel);
    localStringBuilder.append(" length: ");
    localStringBuilder.append(length);
    localStringBuilder.append(" runs: ");
    if (levels == null)
    {
      localStringBuilder.append("none");
    }
    else
    {
      localStringBuilder.append('[');
      localStringBuilder.append(levels[0]);
      for (i = 1; i < levels.length; i++)
      {
        localStringBuilder.append(' ');
        localStringBuilder.append(levels[i]);
      }
      localStringBuilder.append(']');
    }
    localStringBuilder.append(" text: [0x");
    localStringBuilder.append(Integer.toHexString(text[0]));
    for (int i = 1; i < text.length; i++)
    {
      localStringBuilder.append(" 0x");
      localStringBuilder.append(Integer.toHexString(text[i]));
    }
    localStringBuilder.append("]]");
    return localStringBuilder.toString();
  }
  
  private static class ImpTabPair
  {
    byte[][][] imptab;
    short[][] impact;
    
    ImpTabPair(byte[][] paramArrayOfByte1, byte[][] paramArrayOfByte2, short[] paramArrayOfShort1, short[] paramArrayOfShort2)
    {
      imptab = new byte[][][] { paramArrayOfByte1, paramArrayOfByte2 };
      impact = new short[][] { paramArrayOfShort1, paramArrayOfShort2 };
    }
  }
  
  class InsertPoints
  {
    int size;
    int confirmed;
    BidiBase.Point[] points = new BidiBase.Point[0];
    
    InsertPoints() {}
  }
  
  private class LevState
  {
    byte[][] impTab;
    short[] impAct;
    int startON;
    int startL2EN;
    int lastStrongRTL;
    short state;
    byte runLevel;
    
    private LevState() {}
  }
  
  private static class NumericShapings
  {
    private static final Class<?> clazz = getClass("java.awt.font.NumericShaper");
    private static final Method shapeMethod = getMethod(clazz, "shape", new Class[] { char[].class, Integer.TYPE, Integer.TYPE });
    
    private NumericShapings() {}
    
    private static Class<?> getClass(String paramString)
    {
      try
      {
        return Class.forName(paramString, true, null);
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
      return null;
    }
    
    private static Method getMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
    {
      if (paramClass != null) {
        try
        {
          return paramClass.getMethod(paramString, paramVarArgs);
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          throw new AssertionError(localNoSuchMethodException);
        }
      }
      return null;
    }
    
    static void shape(Object paramObject, char[] paramArrayOfChar, int paramInt1, int paramInt2)
    {
      if (shapeMethod == null) {
        throw new AssertionError("Should not get here");
      }
      try
      {
        shapeMethod.invoke(paramObject, new Object[] { paramArrayOfChar, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getCause();
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
        throw new AssertionError(localInvocationTargetException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new AssertionError(localIllegalAccessException);
      }
    }
  }
  
  class Point
  {
    int pos;
    int flag;
    
    Point() {}
  }
  
  private static class TextAttributeConstants
  {
    private static final Class<?> clazz = getClass("java.awt.font.TextAttribute");
    static final AttributedCharacterIterator.Attribute RUN_DIRECTION = getTextAttribute("RUN_DIRECTION");
    static final AttributedCharacterIterator.Attribute NUMERIC_SHAPING = getTextAttribute("NUMERIC_SHAPING");
    static final AttributedCharacterIterator.Attribute BIDI_EMBEDDING = getTextAttribute("BIDI_EMBEDDING");
    static final Boolean RUN_DIRECTION_LTR = clazz == null ? Boolean.FALSE : (Boolean)getStaticField(clazz, "RUN_DIRECTION_LTR");
    
    private TextAttributeConstants() {}
    
    private static Class<?> getClass(String paramString)
    {
      try
      {
        return Class.forName(paramString, true, null);
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
      return null;
    }
    
    private static Object getStaticField(Class<?> paramClass, String paramString)
    {
      try
      {
        Field localField = paramClass.getField(paramString);
        return localField.get(null);
      }
      catch (NoSuchFieldException|IllegalAccessException localNoSuchFieldException)
      {
        throw new AssertionError(localNoSuchFieldException);
      }
    }
    
    private static AttributedCharacterIterator.Attribute getTextAttribute(String paramString)
    {
      if (clazz == null) {
        new AttributedCharacterIterator.Attribute(paramString) {};
      }
      return (AttributedCharacterIterator.Attribute)getStaticField(clazz, paramString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\bidi\BidiBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */