package sun.text.normalizer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class NormalizerImpl
{
  static final NormalizerImpl IMPL;
  static final int UNSIGNED_BYTE_MASK = 255;
  static final long UNSIGNED_INT_MASK = 4294967295L;
  private static final String DATA_FILE_NAME = "/sun/text/resources/unorm.icu";
  public static final int QC_NFC = 17;
  public static final int QC_NFKC = 34;
  public static final int QC_NFD = 4;
  public static final int QC_NFKD = 8;
  public static final int QC_ANY_NO = 15;
  public static final int QC_MAYBE = 16;
  public static final int QC_ANY_MAYBE = 48;
  public static final int QC_MASK = 63;
  private static final int COMBINES_FWD = 64;
  private static final int COMBINES_BACK = 128;
  public static final int COMBINES_ANY = 192;
  private static final int CC_SHIFT = 8;
  public static final int CC_MASK = 65280;
  private static final int EXTRA_SHIFT = 16;
  private static final long MIN_SPECIAL = 4227858432L;
  private static final long SURROGATES_TOP = 4293918720L;
  private static final long MIN_HANGUL = 4293918720L;
  private static final long JAMO_V_TOP = 4294115328L;
  static final int INDEX_TRIE_SIZE = 0;
  static final int INDEX_CHAR_COUNT = 1;
  static final int INDEX_COMBINE_DATA_COUNT = 2;
  public static final int INDEX_MIN_NFC_NO_MAYBE = 6;
  public static final int INDEX_MIN_NFKC_NO_MAYBE = 7;
  public static final int INDEX_MIN_NFD_NO_MAYBE = 8;
  public static final int INDEX_MIN_NFKD_NO_MAYBE = 9;
  static final int INDEX_FCD_TRIE_SIZE = 10;
  static final int INDEX_AUX_TRIE_SIZE = 11;
  static final int INDEX_TOP = 32;
  private static final int AUX_UNSAFE_SHIFT = 11;
  private static final int AUX_COMP_EX_SHIFT = 10;
  private static final int AUX_NFC_SKIPPABLE_F_SHIFT = 12;
  private static final int AUX_MAX_FNC = 1024;
  private static final int AUX_UNSAFE_MASK = 2048;
  private static final int AUX_FNC_MASK = 1023;
  private static final int AUX_COMP_EX_MASK = 1024;
  private static final long AUX_NFC_SKIP_F_MASK = 4096L;
  private static final int MAX_BUFFER_SIZE = 20;
  private static FCDTrieImpl fcdTrieImpl;
  private static NormTrieImpl normTrieImpl;
  private static AuxTrieImpl auxTrieImpl;
  private static int[] indexes;
  private static char[] combiningTable;
  private static char[] extraData;
  private static boolean isDataLoaded;
  private static boolean isFormatVersion_2_1;
  private static boolean isFormatVersion_2_2;
  private static byte[] unicodeVersion;
  private static final int DATA_BUFFER_SIZE = 25000;
  public static final int MIN_WITH_LEAD_CC = 768;
  private static final int DECOMP_FLAG_LENGTH_HAS_CC = 128;
  private static final int DECOMP_LENGTH_MASK = 127;
  private static final int BMP_INDEX_LENGTH = 2048;
  private static final int SURROGATE_BLOCK_BITS = 5;
  public static final int JAMO_L_BASE = 4352;
  public static final int JAMO_V_BASE = 4449;
  public static final int JAMO_T_BASE = 4519;
  public static final int HANGUL_BASE = 44032;
  public static final int JAMO_L_COUNT = 19;
  public static final int JAMO_V_COUNT = 21;
  public static final int JAMO_T_COUNT = 28;
  public static final int HANGUL_COUNT = 11172;
  private static final int OPTIONS_NX_MASK = 31;
  private static final int OPTIONS_UNICODE_MASK = 224;
  public static final int OPTIONS_SETS_MASK = 255;
  private static final UnicodeSet[] nxCache = new UnicodeSet['Ā'];
  private static final int NX_HANGUL = 1;
  private static final int NX_CJK_COMPAT = 2;
  public static final int BEFORE_PRI_29 = 256;
  public static final int OPTIONS_COMPAT = 4096;
  public static final int OPTIONS_COMPOSE_CONTIGUOUS = 8192;
  public static final int WITHOUT_CORRIGENDUM4_CORRECTIONS = 262144;
  private static final char[][] corrigendum4MappingTable = { { 55364, 57194 }, { '弳' }, { '䎫' }, { '窮' }, { '䵗' } };
  
  public static int getFromIndexesArr(int paramInt)
  {
    return indexes[paramInt];
  }
  
  private NormalizerImpl()
    throws IOException
  {
    if (!isDataLoaded)
    {
      InputStream localInputStream = ICUData.getRequiredStream("/sun/text/resources/unorm.icu");
      BufferedInputStream localBufferedInputStream = new BufferedInputStream(localInputStream, 25000);
      NormalizerDataReader localNormalizerDataReader = new NormalizerDataReader(localBufferedInputStream);
      indexes = localNormalizerDataReader.readIndexes(32);
      byte[] arrayOfByte1 = new byte[indexes[0]];
      int i = indexes[2];
      combiningTable = new char[i];
      int j = indexes[1];
      extraData = new char[j];
      byte[] arrayOfByte2 = new byte[indexes[10]];
      byte[] arrayOfByte3 = new byte[indexes[11]];
      fcdTrieImpl = new FCDTrieImpl();
      normTrieImpl = new NormTrieImpl();
      auxTrieImpl = new AuxTrieImpl();
      localNormalizerDataReader.read(arrayOfByte1, arrayOfByte2, arrayOfByte3, extraData, combiningTable);
      NormTrieImpl.normTrie = new IntTrie(new ByteArrayInputStream(arrayOfByte1), normTrieImpl);
      FCDTrieImpl.fcdTrie = new CharTrie(new ByteArrayInputStream(arrayOfByte2), fcdTrieImpl);
      AuxTrieImpl.auxTrie = new CharTrie(new ByteArrayInputStream(arrayOfByte3), auxTrieImpl);
      isDataLoaded = true;
      byte[] arrayOfByte4 = localNormalizerDataReader.getDataFormatVersion();
      isFormatVersion_2_1 = (arrayOfByte4[0] > 2) || ((arrayOfByte4[0] == 2) && (arrayOfByte4[1] >= 1));
      isFormatVersion_2_2 = (arrayOfByte4[0] > 2) || ((arrayOfByte4[0] == 2) && (arrayOfByte4[1] >= 2));
      unicodeVersion = localNormalizerDataReader.getUnicodeVersion();
      localBufferedInputStream.close();
    }
  }
  
  private static boolean isHangulWithoutJamoT(char paramChar)
  {
    paramChar = (char)(paramChar - 44032);
    return (paramChar < '⮤') && (paramChar % '\034' == 0);
  }
  
  private static boolean isNorm32Regular(long paramLong)
  {
    return paramLong < 4227858432L;
  }
  
  private static boolean isNorm32LeadSurrogate(long paramLong)
  {
    return (4227858432L <= paramLong) && (paramLong < 4293918720L);
  }
  
  private static boolean isNorm32HangulOrJamo(long paramLong)
  {
    return paramLong >= 4293918720L;
  }
  
  private static boolean isJamoVTNorm32JamoV(long paramLong)
  {
    return paramLong < 4294115328L;
  }
  
  public static long getNorm32(char paramChar)
  {
    return 0xFFFFFFFF & NormTrieImpl.normTrie.getLeadValue(paramChar);
  }
  
  public static long getNorm32FromSurrogatePair(long paramLong, char paramChar)
  {
    return 0xFFFFFFFF & NormTrieImpl.normTrie.getTrailValue((int)paramLong, paramChar);
  }
  
  private static long getNorm32(int paramInt)
  {
    return 0xFFFFFFFF & NormTrieImpl.normTrie.getCodePointValue(paramInt);
  }
  
  private static long getNorm32(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    long l = getNorm32(paramArrayOfChar[paramInt1]);
    if (((l & paramInt2) > 0L) && (isNorm32LeadSurrogate(l))) {
      l = getNorm32FromSurrogatePair(l, paramArrayOfChar[(paramInt1 + 1)]);
    }
    return l;
  }
  
  public static VersionInfo getUnicodeVersion()
  {
    return VersionInfo.getInstance(unicodeVersion[0], unicodeVersion[1], unicodeVersion[2], unicodeVersion[3]);
  }
  
  public static char getFCD16(char paramChar)
  {
    return FCDTrieImpl.fcdTrie.getLeadValue(paramChar);
  }
  
  public static char getFCD16FromSurrogatePair(char paramChar1, char paramChar2)
  {
    return FCDTrieImpl.fcdTrie.getTrailValue(paramChar1, paramChar2);
  }
  
  public static int getFCD16(int paramInt)
  {
    return FCDTrieImpl.fcdTrie.getCodePointValue(paramInt);
  }
  
  private static int getExtraDataIndex(long paramLong)
  {
    return (int)(paramLong >> 16);
  }
  
  private static int decompose(long paramLong, int paramInt, DecomposeArgs paramDecomposeArgs)
  {
    int i = getExtraDataIndex(paramLong);
    length = extraData[(i++)];
    if (((paramLong & paramInt & 0x8) != 0L) && (length >= 256))
    {
      i += (length >> 7 & 0x1) + (length & 0x7F);
      length >>= 8;
    }
    if ((length & 0x80) > 0)
    {
      int j = extraData[(i++)];
      cc = (0xFF & j >> 8);
      trailCC = (0xFF & j);
    }
    else
    {
      cc = (trailCC = 0);
    }
    length &= 0x7F;
    return i;
  }
  
  private static int decompose(long paramLong, DecomposeArgs paramDecomposeArgs)
  {
    int i = getExtraDataIndex(paramLong);
    length = extraData[(i++)];
    if ((length & 0x80) > 0)
    {
      int j = extraData[(i++)];
      cc = (0xFF & j >> 8);
      trailCC = (0xFF & j);
    }
    else
    {
      cc = (trailCC = 0);
    }
    length &= 0x7F;
    return i;
  }
  
  private static int getNextCC(NextCCArgs paramNextCCArgs)
  {
    c = source[(next++)];
    long l = getNorm32(c);
    if ((l & 0xFF00) == 0L)
    {
      c2 = '\000';
      return 0;
    }
    if (!isNorm32LeadSurrogate(l))
    {
      c2 = '\000';
    }
    else if ((next != limit) && (UTF16.isTrailSurrogate(c2 = source[next])))
    {
      next += 1;
      l = getNorm32FromSurrogatePair(l, c2);
    }
    else
    {
      c2 = '\000';
      return 0;
    }
    return (int)(0xFF & l >> 8);
  }
  
  private static long getPrevNorm32(PrevArgs paramPrevArgs, int paramInt1, int paramInt2)
  {
    c = src[(--current)];
    c2 = '\000';
    if (c < paramInt1) {
      return 0L;
    }
    if (!UTF16.isSurrogate(c)) {
      return getNorm32(c);
    }
    if (UTF16.isLeadSurrogate(c)) {
      return 0L;
    }
    if ((current != start) && (UTF16.isLeadSurrogate(c2 = src[(current - 1)])))
    {
      current -= 1;
      long l = getNorm32(c2);
      if ((l & paramInt2) == 0L) {
        return 0L;
      }
      return getNorm32FromSurrogatePair(l, c);
    }
    c2 = '\000';
    return 0L;
  }
  
  private static int getPrevCC(PrevArgs paramPrevArgs)
  {
    return (int)(0xFF & getPrevNorm32(paramPrevArgs, 768, 65280) >> 8);
  }
  
  public static boolean isNFDSafe(long paramLong, int paramInt1, int paramInt2)
  {
    if ((paramLong & paramInt1) == 0L) {
      return true;
    }
    if ((isNorm32Regular(paramLong)) && ((paramLong & paramInt2) != 0L))
    {
      DecomposeArgs localDecomposeArgs = new DecomposeArgs(null);
      decompose(paramLong, paramInt2, localDecomposeArgs);
      return cc == 0;
    }
    return (paramLong & 0xFF00) == 0L;
  }
  
  public static boolean isTrueStarter(long paramLong, int paramInt1, int paramInt2)
  {
    if ((paramLong & paramInt1) == 0L) {
      return true;
    }
    if ((paramLong & paramInt2) != 0L)
    {
      DecomposeArgs localDecomposeArgs = new DecomposeArgs(null);
      int i = decompose(paramLong, paramInt2, localDecomposeArgs);
      if (cc == 0)
      {
        int j = paramInt1 & 0x3F;
        if ((getNorm32(extraData, i, j) & j) == 0L) {
          return true;
        }
      }
    }
    return false;
  }
  
  private static int insertOrdered(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, char paramChar1, char paramChar2, int paramInt4)
  {
    int n = paramInt4;
    if ((paramInt1 < paramInt2) && (paramInt4 != 0))
    {
      int i;
      int j = i = paramInt2;
      PrevArgs localPrevArgs = new PrevArgs(null);
      current = paramInt2;
      start = paramInt1;
      src = paramArrayOfChar;
      int m = getPrevCC(localPrevArgs);
      j = current;
      if (paramInt4 < m)
      {
        n = m;
        for (i = j; paramInt1 < j; i = j)
        {
          m = getPrevCC(localPrevArgs);
          j = current;
          if (paramInt4 >= m) {
            break;
          }
        }
        int k = paramInt3;
        do
        {
          paramArrayOfChar[(--k)] = paramArrayOfChar[(--paramInt2)];
        } while (i != paramInt2);
      }
    }
    paramArrayOfChar[paramInt2] = paramChar1;
    if (paramChar2 != 0) {
      paramArrayOfChar[(paramInt2 + 1)] = paramChar2;
    }
    return n;
  }
  
  private static int mergeOrdered(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    int k = 0;
    int m = paramInt2 == paramInt3 ? 1 : 0;
    NextCCArgs localNextCCArgs = new NextCCArgs(null);
    source = paramArrayOfChar2;
    next = paramInt3;
    limit = paramInt4;
    if ((paramInt1 != paramInt2) || (!paramBoolean)) {
      while (next < limit)
      {
        int j = getNextCC(localNextCCArgs);
        if (j == 0)
        {
          k = 0;
          if (m != 0)
          {
            paramInt2 = next;
          }
          else
          {
            paramArrayOfChar2[(paramInt2++)] = c;
            if (c2 != 0) {
              paramArrayOfChar2[(paramInt2++)] = c2;
            }
          }
          if (paramBoolean) {
            break;
          }
          paramInt1 = paramInt2;
        }
        else
        {
          int i = paramInt2 + (c2 == 0 ? 1 : 2);
          k = insertOrdered(paramArrayOfChar1, paramInt1, paramInt2, i, c, c2, j);
          paramInt2 = i;
        }
      }
    }
    if (next == limit) {
      return k;
    }
    if (m == 0)
    {
      do
      {
        paramArrayOfChar1[(paramInt2++)] = paramArrayOfChar2[(next++)];
      } while (next != limit);
      limit = paramInt2;
    }
    PrevArgs localPrevArgs = new PrevArgs(null);
    src = paramArrayOfChar2;
    start = paramInt1;
    current = limit;
    return getPrevCC(localPrevArgs);
  }
  
  private static int mergeOrdered(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4)
  {
    return mergeOrdered(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4, true);
  }
  
  public static NormalizerBase.QuickCheckResult quickCheck(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, UnicodeSet paramUnicodeSet)
  {
    ComposePartArgs localComposePartArgs = new ComposePartArgs(null);
    int m = paramInt1;
    if (!isDataLoaded) {
      return NormalizerBase.MAYBE;
    }
    int i = 0xFF00 | paramInt4;
    NormalizerBase.QuickCheckResult localQuickCheckResult = NormalizerBase.YES;
    int k = 0;
    for (;;)
    {
      if (paramInt1 == paramInt2) {
        return localQuickCheckResult;
      }
      char c1;
      long l1;
      if (((c1 = paramArrayOfChar[(paramInt1++)]) < paramInt3) || (((l1 = getNorm32(c1)) & i) == 0L))
      {
        k = 0;
      }
      else
      {
        char c2;
        if (isNorm32LeadSurrogate(l1))
        {
          if ((paramInt1 != paramInt2) && (UTF16.isTrailSurrogate(c2 = paramArrayOfChar[paramInt1])))
          {
            paramInt1++;
            l1 = getNorm32FromSurrogatePair(l1, c2);
          }
          else
          {
            l1 = 0L;
            c2 = '\000';
          }
        }
        else {
          c2 = '\000';
        }
        if (nx_contains(paramUnicodeSet, c1, c2)) {
          l1 = 0L;
        }
        int j = (char)(int)(l1 >> 8 & 0xFF);
        if ((j != 0) && (j < k)) {
          return NormalizerBase.NO;
        }
        k = j;
        long l2 = l1 & paramInt4;
        if ((l2 & 0xF) >= 1L)
        {
          localQuickCheckResult = NormalizerBase.NO;
          break;
        }
        if (l2 != 0L) {
          if (paramBoolean)
          {
            localQuickCheckResult = NormalizerBase.MAYBE;
          }
          else
          {
            int i1 = paramInt4 << 2 & 0xF;
            int n = paramInt1 - 1;
            if (UTF16.isTrailSurrogate(paramArrayOfChar[n])) {
              n--;
            }
            n = findPreviousStarter(paramArrayOfChar, m, n, i, i1, (char)paramInt3);
            paramInt1 = findNextStarter(paramArrayOfChar, paramInt1, paramInt2, paramInt4, i1, (char)paramInt3);
            prevCC = k;
            char[] arrayOfChar = composePart(localComposePartArgs, n, paramArrayOfChar, paramInt1, paramInt2, paramInt5, paramUnicodeSet);
            if (0 != strCompare(arrayOfChar, 0, length, paramArrayOfChar, n, paramInt1, false))
            {
              localQuickCheckResult = NormalizerBase.NO;
              break;
            }
          }
        }
      }
    }
    return localQuickCheckResult;
  }
  
  public static int decompose(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, boolean paramBoolean, int[] paramArrayOfInt, UnicodeSet paramUnicodeSet)
  {
    char[] arrayOfChar1 = new char[3];
    int i7 = paramInt3;
    int i8 = paramInt1;
    int i2;
    int k;
    if (!paramBoolean)
    {
      i2 = (char)indexes[8];
      k = 4;
    }
    else
    {
      i2 = (char)indexes[9];
      k = 8;
    }
    int j = 0xFF00 | k;
    int m = 0;
    int i4 = 0;
    long l = 0L;
    int i1 = 0;
    int i6 = 0;
    int i5;
    int i3 = i5 = -1;
    for (;;)
    {
      int i = i8;
      while ((i8 != paramInt2) && (((i1 = paramArrayOfChar1[i8]) < i2) || (((l = getNorm32(i1)) & j) == 0L)))
      {
        i4 = 0;
        i8++;
      }
      int n;
      if (i8 != i)
      {
        n = i8 - i;
        if (i7 + n <= paramInt4) {
          System.arraycopy(paramArrayOfChar1, i, paramArrayOfChar2, i7, n);
        }
        i7 += n;
        m = i7;
      }
      if (i8 == paramInt2) {
        break;
      }
      i8++;
      char c2;
      char[] arrayOfChar2;
      char c1;
      if (isNorm32HangulOrJamo(l))
      {
        if (nx_contains(paramUnicodeSet, i1))
        {
          c2 = '\000';
          arrayOfChar2 = null;
          n = 1;
        }
        else
        {
          arrayOfChar2 = arrayOfChar1;
          i6 = 0;
          i3 = i5 = 0;
          c1 = (char)(i1 - 44032);
          c2 = (char)(c1 % '\034');
          c1 = (char)(c1 / '\034');
          if (c2 > 0)
          {
            arrayOfChar1[2] = ((char)('ᆧ' + c2));
            n = 3;
          }
          else
          {
            n = 2;
          }
          arrayOfChar1[1] = ((char)(4449 + c1 % '\025'));
          arrayOfChar1[0] = ((char)(4352 + c1 / '\025'));
        }
      }
      else
      {
        if (isNorm32Regular(l))
        {
          c2 = '\000';
          n = 1;
        }
        else if ((i8 != paramInt2) && (UTF16.isTrailSurrogate(c2 = paramArrayOfChar1[i8])))
        {
          i8++;
          n = 2;
          l = getNorm32FromSurrogatePair(l, c2);
        }
        else
        {
          c2 = '\000';
          n = 1;
          l = 0L;
        }
        if (nx_contains(paramUnicodeSet, c1, c2))
        {
          i3 = i5 = 0;
          arrayOfChar2 = null;
        }
        else if ((l & k) == 0L)
        {
          i3 = i5 = (int)(0xFF & l >> 8);
          arrayOfChar2 = null;
          i6 = -1;
        }
        else
        {
          DecomposeArgs localDecomposeArgs = new DecomposeArgs(null);
          i6 = decompose(l, k, localDecomposeArgs);
          arrayOfChar2 = extraData;
          n = length;
          i3 = cc;
          i5 = trailCC;
          if (n == 1)
          {
            c1 = arrayOfChar2[i6];
            c2 = '\000';
            arrayOfChar2 = null;
            i6 = -1;
          }
        }
      }
      if (i7 + n <= paramInt4)
      {
        int i9 = i7;
        if (arrayOfChar2 == null)
        {
          if ((i3 != 0) && (i3 < i4))
          {
            i7 += n;
            i5 = insertOrdered(paramArrayOfChar2, m, i9, i7, c1, c2, i3);
          }
          else
          {
            paramArrayOfChar2[(i7++)] = c1;
            if (c2 != 0) {
              paramArrayOfChar2[(i7++)] = c2;
            }
          }
        }
        else if ((i3 != 0) && (i3 < i4))
        {
          i7 += n;
          i5 = mergeOrdered(paramArrayOfChar2, m, i9, arrayOfChar2, i6, i6 + n);
        }
        else
        {
          do
          {
            paramArrayOfChar2[(i7++)] = arrayOfChar2[(i6++)];
            n--;
          } while (n > 0);
        }
      }
      else
      {
        i7 += n;
      }
      i4 = i5;
      if (i4 == 0) {
        m = i7;
      }
    }
    paramArrayOfInt[0] = i4;
    return i7 - paramInt3;
  }
  
  private static int getNextCombining(NextCombiningArgs paramNextCombiningArgs, int paramInt, UnicodeSet paramUnicodeSet)
  {
    c = source[(start++)];
    long l = getNorm32(c);
    c2 = '\000';
    combiningIndex = 0;
    cc = '\000';
    if ((l & 0xFFC0) == 0L) {
      return 0;
    }
    if (!isNorm32Regular(l))
    {
      if (isNorm32HangulOrJamo(l))
      {
        combiningIndex = ((int)(0xFFFFFFFF & (0xFFF0 | l >> 16)));
        return (int)(l & 0xC0);
      }
      if ((start != paramInt) && (UTF16.isTrailSurrogate(c2 = source[start])))
      {
        start += 1;
        l = getNorm32FromSurrogatePair(l, c2);
      }
      else
      {
        c2 = '\000';
        return 0;
      }
    }
    if (nx_contains(paramUnicodeSet, c, c2)) {
      return 0;
    }
    cc = ((char)(int)(l >> 8 & 0xFF));
    int i = (int)(l & 0xC0);
    if (i != 0)
    {
      int j = getExtraDataIndex(l);
      combiningIndex = (j > 0 ? extraData[(j - 1)] : 0);
    }
    return i;
  }
  
  private static int getCombiningIndexFromStarter(char paramChar1, char paramChar2)
  {
    long l = getNorm32(paramChar1);
    if (paramChar2 != 0) {
      l = getNorm32FromSurrogatePair(l, paramChar2);
    }
    return extraData[(getExtraDataIndex(l) - 1)];
  }
  
  private static int combine(char[] paramArrayOfChar, int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    if (paramArrayOfInt.length < 2) {
      throw new IllegalArgumentException();
    }
    int i;
    for (;;)
    {
      i = paramArrayOfChar[(paramInt1++)];
      if (i >= paramInt2) {
        break;
      }
      paramInt1 += ((paramArrayOfChar[paramInt1] & 0x8000) != 0 ? 2 : 1);
    }
    if ((i & 0x7FFF) == paramInt2)
    {
      int j = paramArrayOfChar[paramInt1];
      i = (int)(0xFFFFFFFF & (j & 0x2000) + 1);
      int k;
      if ((j & 0x8000) != 0)
      {
        if ((j & 0x4000) != 0)
        {
          j = (int)(0xFFFFFFFF & (j & 0x3FF | 0xD800));
          k = paramArrayOfChar[(paramInt1 + 1)];
        }
        else
        {
          j = paramArrayOfChar[(paramInt1 + 1)];
          k = 0;
        }
      }
      else
      {
        j &= 0x1FFF;
        k = 0;
      }
      paramArrayOfInt[0] = j;
      paramArrayOfInt[1] = k;
      return i;
    }
    return 0;
  }
  
  private static char recompose(RecomposeArgs paramRecomposeArgs, int paramInt, UnicodeSet paramUnicodeSet)
  {
    int i3 = 0;
    int i4 = 0;
    int[] arrayOfInt = new int[2];
    int i7 = -1;
    int n = 0;
    int i6 = 0;
    int i5 = 0;
    NextCombiningArgs localNextCombiningArgs = new NextCombiningArgs(null);
    source = source;
    cc = '\000';
    c2 = '\000';
    for (;;)
    {
      start = start;
      int m = getNextCombining(localNextCombiningArgs, limit, paramUnicodeSet);
      int i1 = combiningIndex;
      start = start;
      if (((m & 0x80) != 0) && (i7 != -1))
      {
        int i;
        int j;
        int k;
        if ((i1 & 0x8000) != 0)
        {
          if (((paramInt & 0x100) != 0) || (i5 == 0))
          {
            i = -1;
            m = 0;
            c2 = source[i7];
            if (i1 == 65522)
            {
              c2 = ((char)(c2 - 'ᄀ'));
              if (c2 < '\023')
              {
                i = start - 1;
                c = ((char)(44032 + (c2 * '\025' + (c - 'ᅡ')) * 28));
                if ((start != limit) && ((c2 = (char)(source[start] - 'ᆧ')) < '\034'))
                {
                  start += 1;
                  NextCombiningArgs tmp261_259 = localNextCombiningArgs;
                  261259c = ((char)(261259c + c2));
                }
                else
                {
                  m = 64;
                }
                if (!nx_contains(paramUnicodeSet, c))
                {
                  source[i7] = c;
                }
                else
                {
                  if (!isHangulWithoutJamoT(c)) {
                    start -= 1;
                  }
                  i = start;
                }
              }
            }
            else if (isHangulWithoutJamoT(c2))
            {
              NextCombiningArgs tmp351_349 = localNextCombiningArgs;
              351349c2 = ((char)(351349c2 + (c - 'ᆧ')));
              if (!nx_contains(paramUnicodeSet, c2))
              {
                i = start - 1;
                source[i7] = c2;
              }
            }
            if (i != -1)
            {
              j = i;
              k = start;
              while (k < limit) {
                source[(j++)] = source[(k++)];
              }
              start = i;
              limit = j;
            }
            c2 = '\000';
            if (m != 0)
            {
              if (start == limit) {
                return (char)i5;
              }
              n = 65520;
            }
          }
        }
        else
        {
          int i2;
          if (((n & 0x8000) == 0) && ((paramInt & 0x100) != 0 ? (i5 != cc) || (i5 == 0) : (i5 < cc) || (i5 == 0)) && (0 != (i2 = combine(combiningTable, n, i1, arrayOfInt))) && (!nx_contains(paramUnicodeSet, (char)i3, (char)i4)))
          {
            i3 = arrayOfInt[0];
            i4 = arrayOfInt[1];
            i = c2 == 0 ? start - 1 : start - 2;
            source[i7] = ((char)i3);
            if (i6 != 0)
            {
              if (i4 != 0)
              {
                source[(i7 + 1)] = ((char)i4);
              }
              else
              {
                i6 = 0;
                j = i7 + 1;
                k = j + 1;
                while (k < i) {
                  source[(j++)] = source[(k++)];
                }
                i--;
              }
            }
            else if (i4 != 0)
            {
              i6 = 1;
              source[(i7 + 1)] = ((char)i4);
            }
            if (i < start)
            {
              j = i;
              k = start;
              while (k < limit) {
                source[(j++)] = source[(k++)];
              }
              start = i;
              limit = j;
            }
            if (start == limit) {
              return (char)i5;
            }
            if (i2 > 1)
            {
              n = getCombiningIndexFromStarter((char)i3, (char)i4);
              continue;
            }
            i7 = -1;
            continue;
          }
        }
      }
      i5 = cc;
      if (start == limit) {
        return (char)i5;
      }
      if (cc == 0)
      {
        if ((m & 0x40) != 0)
        {
          if (c2 == 0)
          {
            i6 = 0;
            i7 = start - 1;
          }
          else
          {
            i6 = 0;
            i7 = start - 2;
          }
          n = i1;
        }
        else
        {
          i7 = -1;
        }
      }
      else if ((paramInt & 0x2000) != 0) {
        i7 = -1;
      }
    }
  }
  
  private static int findPreviousStarter(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, char paramChar)
  {
    PrevArgs localPrevArgs = new PrevArgs(null);
    src = paramArrayOfChar;
    start = paramInt1;
    current = paramInt2;
    while (start < current)
    {
      long l = getPrevNorm32(localPrevArgs, paramChar, paramInt3 | paramInt4);
      if (isTrueStarter(l, paramInt3, paramInt4)) {
        break;
      }
    }
    return current;
  }
  
  private static int findNextStarter(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, char paramChar)
  {
    int j = 0xFF00 | paramInt3;
    DecomposeArgs localDecomposeArgs = new DecomposeArgs(null);
    while (paramInt1 != paramInt2)
    {
      char c1 = paramArrayOfChar[paramInt1];
      if (c1 < paramChar) {
        break;
      }
      long l = getNorm32(c1);
      if ((l & j) == 0L) {
        break;
      }
      char c2;
      if (isNorm32LeadSurrogate(l))
      {
        if ((paramInt1 + 1 == paramInt2) || (!UTF16.isTrailSurrogate(c2 = paramArrayOfChar[(paramInt1 + 1)]))) {
          break;
        }
        l = getNorm32FromSurrogatePair(l, c2);
        if ((l & j) == 0L) {
          break;
        }
      }
      else
      {
        c2 = '\000';
      }
      if ((l & paramInt4) != 0L)
      {
        int i = decompose(l, paramInt4, localDecomposeArgs);
        if ((cc == 0) && ((getNorm32(extraData, i, paramInt3) & paramInt3) == 0L)) {
          break;
        }
      }
      paramInt1 += (c2 == 0 ? 1 : 2);
    }
    return paramInt1;
  }
  
  private static char[] composePart(ComposePartArgs paramComposePartArgs, int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, int paramInt4, UnicodeSet paramUnicodeSet)
  {
    boolean bool = (paramInt4 & 0x1000) != 0;
    int[] arrayOfInt = new int[1];
    for (char[] arrayOfChar = new char[(paramInt3 - paramInt1) * 20];; arrayOfChar = new char[length])
    {
      length = decompose(paramArrayOfChar, paramInt1, paramInt2, arrayOfChar, 0, arrayOfChar.length, bool, arrayOfInt, paramUnicodeSet);
      if (length <= arrayOfChar.length) {
        break;
      }
    }
    int i = length;
    if (length >= 2)
    {
      RecomposeArgs localRecomposeArgs = new RecomposeArgs(null);
      source = arrayOfChar;
      start = 0;
      limit = i;
      prevCC = recompose(localRecomposeArgs, paramInt4, paramUnicodeSet);
      i = limit;
    }
    length = i;
    return arrayOfChar;
  }
  
  private static boolean composeHangul(char paramChar1, char paramChar2, long paramLong, char[] paramArrayOfChar1, int[] paramArrayOfInt, int paramInt1, boolean paramBoolean, char[] paramArrayOfChar2, int paramInt2, UnicodeSet paramUnicodeSet)
  {
    int i = paramArrayOfInt[0];
    if (isJamoVTNorm32JamoV(paramLong))
    {
      paramChar1 = (char)(paramChar1 - 'ᄀ');
      if (paramChar1 < '\023')
      {
        paramChar2 = (char)(44032 + (paramChar1 * '\025' + (paramChar2 - 'ᅡ')) * 28);
        if (i != paramInt1)
        {
          char c1 = paramArrayOfChar1[i];
          char c2;
          if ((c2 = (char)(c1 - 'ᆧ')) < '\034')
          {
            i++;
            paramChar2 = (char)(paramChar2 + c2);
          }
          else if (paramBoolean)
          {
            paramLong = getNorm32(c1);
            if ((isNorm32Regular(paramLong)) && ((paramLong & 0x8) != 0L))
            {
              DecomposeArgs localDecomposeArgs = new DecomposeArgs(null);
              int j = decompose(paramLong, 8, localDecomposeArgs);
              if ((length == 1) && ((c2 = (char)(extraData[j] - 'ᆧ')) < '\034'))
              {
                i++;
                paramChar2 = (char)(paramChar2 + c2);
              }
            }
          }
        }
        if (nx_contains(paramUnicodeSet, paramChar2))
        {
          if (!isHangulWithoutJamoT(paramChar2)) {
            i--;
          }
          return false;
        }
        paramArrayOfChar2[paramInt2] = paramChar2;
        paramArrayOfInt[0] = i;
        return true;
      }
    }
    else if (isHangulWithoutJamoT(paramChar1))
    {
      paramChar2 = (char)(paramChar1 + (paramChar2 - 'ᆧ'));
      if (nx_contains(paramUnicodeSet, paramChar2)) {
        return false;
      }
      paramArrayOfChar2[paramInt2] = paramChar2;
      paramArrayOfInt[0] = i;
      return true;
    }
    return false;
  }
  
  public static int compose(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, int paramInt5, UnicodeSet paramUnicodeSet)
  {
    int[] arrayOfInt = new int[1];
    int i4 = paramInt3;
    int i5 = paramInt1;
    char c3;
    int m;
    if ((paramInt5 & 0x1000) != 0)
    {
      c3 = (char)indexes[7];
      m = 34;
    }
    else
    {
      c3 = (char)indexes[6];
      m = 17;
    }
    int j = i5;
    int k = 0xFF00 | m;
    int n = 0;
    int i3 = 0;
    long l = 0L;
    char c1 = '\000';
    for (;;)
    {
      int i = i5;
      while ((i5 != paramInt2) && (((c1 = paramArrayOfChar1[i5]) < c3) || (((l = getNorm32(c1)) & k) == 0L)))
      {
        i3 = 0;
        i5++;
      }
      int i1;
      if (i5 != i)
      {
        i1 = i5 - i;
        if (i4 + i1 <= paramInt4) {
          System.arraycopy(paramArrayOfChar1, i, paramArrayOfChar2, i4, i1);
        }
        i4 += i1;
        n = i4;
        j = i5 - 1;
        if ((UTF16.isTrailSurrogate(paramArrayOfChar1[j])) && (i < j) && (UTF16.isLeadSurrogate(paramArrayOfChar1[(j - 1)]))) {
          j--;
        }
        i = i5;
      }
      if (i5 == paramInt2) {
        break;
      }
      i5++;
      int i2;
      char c2;
      if (isNorm32HangulOrJamo(l))
      {
        i3 = i2 = 0;
        n = i4;
        arrayOfInt[0] = i5;
        if (i4 > 0) {
          if (composeHangul(paramArrayOfChar1[(i - 1)], c1, l, paramArrayOfChar1, arrayOfInt, paramInt2, (paramInt5 & 0x1000) != 0, paramArrayOfChar2, i4 <= paramInt4 ? i4 - 1 : 0, paramUnicodeSet))
          {
            i5 = arrayOfInt[0];
            j = i5;
            continue;
          }
        }
        i5 = arrayOfInt[0];
        c2 = '\000';
        i1 = 1;
        j = i;
      }
      else
      {
        if (isNorm32Regular(l))
        {
          c2 = '\000';
          i1 = 1;
        }
        else if ((i5 != paramInt2) && (UTF16.isTrailSurrogate(c2 = paramArrayOfChar1[i5])))
        {
          i5++;
          i1 = 2;
          l = getNorm32FromSurrogatePair(l, c2);
        }
        else
        {
          c2 = '\000';
          i1 = 1;
          l = 0L;
        }
        ComposePartArgs localComposePartArgs = new ComposePartArgs(null);
        if (nx_contains(paramUnicodeSet, c1, c2))
        {
          i2 = 0;
        }
        else if ((l & m) == 0L)
        {
          i2 = (int)(0xFF & l >> 8);
        }
        else
        {
          int i7 = m << 2 & 0xF;
          if (isTrueStarter(l, 0xFF00 | m, i7)) {
            j = i;
          } else {
            i4 -= i - j;
          }
          i5 = findNextStarter(paramArrayOfChar1, i5, paramInt2, m, i7, c3);
          prevCC = i3;
          length = i1;
          char[] arrayOfChar = composePart(localComposePartArgs, j, paramArrayOfChar1, i5, paramInt2, paramInt5, paramUnicodeSet);
          if (arrayOfChar == null) {
            break;
          }
          i3 = prevCC;
          i1 = length;
          if (i4 + length <= paramInt4)
          {
            int i8 = 0;
            while (i8 < length)
            {
              paramArrayOfChar2[(i4++)] = arrayOfChar[(i8++)];
              i1--;
            }
          }
          else
          {
            i4 += i1;
          }
          j = i5;
          continue;
        }
      }
      if (i4 + i1 <= paramInt4)
      {
        if ((i2 != 0) && (i2 < i3))
        {
          int i6 = i4;
          i4 += i1;
          i3 = insertOrdered(paramArrayOfChar2, n, i6, i4, c1, c2, i2);
        }
        else
        {
          paramArrayOfChar2[(i4++)] = c1;
          if (c2 != 0) {
            paramArrayOfChar2[(i4++)] = c2;
          }
          i3 = i2;
        }
      }
      else
      {
        i4 += i1;
        i3 = i2;
      }
    }
    return i4 - paramInt3;
  }
  
  public static int getCombiningClass(int paramInt)
  {
    long l = getNorm32(paramInt);
    return (int)(l >> 8 & 0xFF);
  }
  
  public static boolean isFullCompositionExclusion(int paramInt)
  {
    if (isFormatVersion_2_1)
    {
      int i = AuxTrieImpl.auxTrie.getCodePointValue(paramInt);
      return (i & 0x400) != 0;
    }
    return false;
  }
  
  public static boolean isCanonSafeStart(int paramInt)
  {
    if (isFormatVersion_2_1)
    {
      int i = AuxTrieImpl.auxTrie.getCodePointValue(paramInt);
      return (i & 0x800) == 0;
    }
    return false;
  }
  
  public static boolean isNFSkippable(int paramInt, NormalizerBase.Mode paramMode, long paramLong)
  {
    paramLong &= 0xFFFFFFFF;
    long l = getNorm32(paramInt);
    if ((l & paramLong) != 0L) {
      return false;
    }
    if ((paramMode == NormalizerBase.NFD) || (paramMode == NormalizerBase.NFKD) || (paramMode == NormalizerBase.NONE)) {
      return true;
    }
    if ((l & 0x4) == 0L) {
      return true;
    }
    if (isNorm32HangulOrJamo(l)) {
      return !isHangulWithoutJamoT((char)paramInt);
    }
    if (!isFormatVersion_2_2) {
      return false;
    }
    int i = AuxTrieImpl.auxTrie.getCodePointValue(paramInt);
    return (i & 0x1000) == 0L;
  }
  
  public static UnicodeSet addPropertyStarts(UnicodeSet paramUnicodeSet)
  {
    TrieIterator localTrieIterator1 = new TrieIterator(NormTrieImpl.normTrie);
    RangeValueIterator.Element localElement1 = new RangeValueIterator.Element();
    while (localTrieIterator1.next(localElement1)) {
      paramUnicodeSet.add(start);
    }
    TrieIterator localTrieIterator2 = new TrieIterator(FCDTrieImpl.fcdTrie);
    RangeValueIterator.Element localElement2 = new RangeValueIterator.Element();
    while (localTrieIterator2.next(localElement2)) {
      paramUnicodeSet.add(start);
    }
    if (isFormatVersion_2_1)
    {
      TrieIterator localTrieIterator3 = new TrieIterator(AuxTrieImpl.auxTrie);
      RangeValueIterator.Element localElement3 = new RangeValueIterator.Element();
      while (localTrieIterator3.next(localElement3)) {
        paramUnicodeSet.add(start);
      }
    }
    for (int i = 44032; i < 55204; i += 28)
    {
      paramUnicodeSet.add(i);
      paramUnicodeSet.add(i + 1);
    }
    paramUnicodeSet.add(55204);
    return paramUnicodeSet;
  }
  
  public static final int quickCheck(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = { 0, 0, 4, 8, 17, 34 };
    int i = (int)getNorm32(paramInt1) & arrayOfInt[paramInt2];
    if (i == 0) {
      return 1;
    }
    if ((i & 0xF) != 0) {
      return 0;
    }
    return 2;
  }
  
  private static int strCompare(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    int i = paramInt1;
    int j = paramInt3;
    int i4 = paramInt2 - paramInt1;
    int i5 = paramInt4 - paramInt3;
    int i6;
    if (i4 < i5)
    {
      i6 = -1;
      k = i + i4;
    }
    else if (i4 == i5)
    {
      i6 = 0;
      k = i + i4;
    }
    else
    {
      i6 = 1;
      k = i + i5;
    }
    if (paramArrayOfChar1 == paramArrayOfChar2) {
      return i6;
    }
    int n;
    int i2;
    for (;;)
    {
      if (paramInt1 == k) {
        return i6;
      }
      n = paramArrayOfChar1[paramInt1];
      i2 = paramArrayOfChar2[paramInt3];
      if (n != i2) {
        break;
      }
      paramInt1++;
      paramInt3++;
    }
    int k = i + i4;
    int m = j + i5;
    int i1;
    int i3;
    if ((n >= 55296) && (i2 >= 55296) && (paramBoolean))
    {
      if (((n > 56319) || (paramInt1 + 1 == k) || (!UTF16.isTrailSurrogate(paramArrayOfChar1[(paramInt1 + 1)]))) && ((!UTF16.isTrailSurrogate(n)) || (i == paramInt1) || (!UTF16.isLeadSurrogate(paramArrayOfChar1[(paramInt1 - 1)])))) {
        i1 = (char)(n - 10240);
      }
      if (((i2 > 56319) || (paramInt3 + 1 == m) || (!UTF16.isTrailSurrogate(paramArrayOfChar2[(paramInt3 + 1)]))) && ((!UTF16.isTrailSurrogate(i2)) || (j == paramInt3) || (!UTF16.isLeadSurrogate(paramArrayOfChar2[(paramInt3 - 1)])))) {
        i3 = (char)(i2 - 10240);
      }
    }
    return i1 - i3;
  }
  
  private static final synchronized UnicodeSet internalGetNXHangul()
  {
    if (nxCache[1] == null) {
      nxCache[1] = new UnicodeSet(44032, 55203);
    }
    return nxCache[1];
  }
  
  private static final synchronized UnicodeSet internalGetNXCJKCompat()
  {
    if (nxCache[2] == null)
    {
      UnicodeSet localUnicodeSet1 = new UnicodeSet("[:Ideographic:]");
      UnicodeSet localUnicodeSet2 = new UnicodeSet();
      UnicodeSetIterator localUnicodeSetIterator = new UnicodeSetIterator(localUnicodeSet1);
      while ((localUnicodeSetIterator.nextRange()) && (codepoint != UnicodeSetIterator.IS_STRING))
      {
        int i = codepoint;
        int j = codepointEnd;
        while (i <= j)
        {
          long l = getNorm32(i);
          if ((l & 0x4) > 0L) {
            localUnicodeSet2.add(i);
          }
          i++;
        }
      }
      nxCache[2] = localUnicodeSet2;
    }
    return nxCache[2];
  }
  
  private static final synchronized UnicodeSet internalGetNXUnicode(int paramInt)
  {
    paramInt &= 0xE0;
    if (paramInt == 0) {
      return null;
    }
    if (nxCache[paramInt] == null)
    {
      UnicodeSet localUnicodeSet = new UnicodeSet();
      switch (paramInt)
      {
      case 32: 
        localUnicodeSet.applyPattern("[:^Age=3.2:]");
        break;
      default: 
        return null;
      }
      nxCache[paramInt] = localUnicodeSet;
    }
    return nxCache[paramInt];
  }
  
  private static final synchronized UnicodeSet internalGetNX(int paramInt)
  {
    paramInt &= 0xFF;
    if (nxCache[paramInt] == null)
    {
      if (paramInt == 1) {
        return internalGetNXHangul();
      }
      if (paramInt == 2) {
        return internalGetNXCJKCompat();
      }
      if (((paramInt & 0xE0) != 0) && ((paramInt & 0x1F) == 0)) {
        return internalGetNXUnicode(paramInt);
      }
      UnicodeSet localUnicodeSet1 = new UnicodeSet();
      UnicodeSet localUnicodeSet2;
      if (((paramInt & 0x1) != 0) && (null != (localUnicodeSet2 = internalGetNXHangul()))) {
        localUnicodeSet1.addAll(localUnicodeSet2);
      }
      if (((paramInt & 0x2) != 0) && (null != (localUnicodeSet2 = internalGetNXCJKCompat()))) {
        localUnicodeSet1.addAll(localUnicodeSet2);
      }
      if (((paramInt & 0xE0) != 0) && (null != (localUnicodeSet2 = internalGetNXUnicode(paramInt)))) {
        localUnicodeSet1.addAll(localUnicodeSet2);
      }
      nxCache[paramInt] = localUnicodeSet1;
    }
    return nxCache[paramInt];
  }
  
  public static final UnicodeSet getNX(int paramInt)
  {
    if ((paramInt &= 0xFF) == 0) {
      return null;
    }
    return internalGetNX(paramInt);
  }
  
  private static final boolean nx_contains(UnicodeSet paramUnicodeSet, int paramInt)
  {
    return (paramUnicodeSet != null) && (paramUnicodeSet.contains(paramInt));
  }
  
  private static final boolean nx_contains(UnicodeSet paramUnicodeSet, char paramChar1, char paramChar2)
  {
    if (paramUnicodeSet != null) {}
    return paramUnicodeSet.contains(paramChar2 == 0 ? paramChar1 : UCharacterProperty.getRawSupplementary(paramChar1, paramChar2));
  }
  
  public static int getDecompose(int[] paramArrayOfInt, String[] paramArrayOfString)
  {
    DecomposeArgs localDecomposeArgs = new DecomposeArgs(null);
    int i = 0;
    long l = 0L;
    int j = -1;
    int k = 0;
    int m = 0;
    for (;;)
    {
      j++;
      if (j >= 195102) {
        break;
      }
      if (j == 12543) {
        j = 63744;
      } else if (j == 65536) {
        j = 119134;
      } else if (j == 119233) {
        j = 194560;
      }
      l = getNorm32(j);
      if (((l & 0x4) != 0L) && (m < paramArrayOfInt.length))
      {
        paramArrayOfInt[m] = j;
        k = decompose(l, localDecomposeArgs);
        paramArrayOfString[(m++)] = new String(extraData, k, length);
      }
    }
    return m;
  }
  
  private static boolean needSingleQuotation(char paramChar)
  {
    return ((paramChar >= '\t') && (paramChar <= '\r')) || ((paramChar >= ' ') && (paramChar <= '/')) || ((paramChar >= ':') && (paramChar <= '@')) || ((paramChar >= '[') && (paramChar <= '`')) || ((paramChar >= '{') && (paramChar <= '~'));
  }
  
  public static String canonicalDecomposeWithSingleQuotation(String paramString)
  {
    char[] arrayOfChar1 = paramString.toCharArray();
    int i = 0;
    int j = arrayOfChar1.length;
    Object localObject1 = new char[arrayOfChar1.length * 3];
    int k = 0;
    int m = localObject1.length;
    char[] arrayOfChar2 = new char[3];
    int i2 = 4;
    int i6 = (char)indexes[8];
    int i1 = 0xFF00 | i2;
    int i3 = 0;
    int i8 = 0;
    long l = 0L;
    int i5 = 0;
    int i10 = 0;
    int i9;
    int i7 = i9 = -1;
    for (;;)
    {
      int n = i;
      while ((i != j) && (((i5 = arrayOfChar1[i]) < i6) || (((l = getNorm32(i5)) & i1) == 0L) || ((i5 >= 44032) && (i5 <= 55203))))
      {
        i8 = 0;
        i++;
      }
      int i4;
      if (i != n)
      {
        i4 = i - n;
        if (k + i4 <= m) {
          System.arraycopy(arrayOfChar1, n, localObject1, k, i4);
        }
        k += i4;
        i3 = k;
      }
      if (i == j) {
        break;
      }
      i++;
      char c2;
      if (isNorm32Regular(l))
      {
        c2 = '\000';
        i4 = 1;
      }
      else if ((i != j) && (Character.isLowSurrogate(c2 = arrayOfChar1[i])))
      {
        i++;
        i4 = 2;
        l = getNorm32FromSurrogatePair(l, c2);
      }
      else
      {
        c2 = '\000';
        i4 = 1;
        l = 0L;
      }
      char[] arrayOfChar3;
      char c1;
      if ((l & i2) == 0L)
      {
        i7 = i9 = (int)(0xFF & l >> 8);
        arrayOfChar3 = null;
        i10 = -1;
      }
      else
      {
        localObject2 = new DecomposeArgs(null);
        i10 = decompose(l, i2, (DecomposeArgs)localObject2);
        arrayOfChar3 = extraData;
        i4 = length;
        i7 = cc;
        i9 = trailCC;
        if (i4 == 1)
        {
          c1 = arrayOfChar3[i10];
          c2 = '\000';
          arrayOfChar3 = null;
          i10 = -1;
        }
      }
      if (k + i4 * 3 >= m)
      {
        localObject2 = new char[m * 2];
        System.arraycopy(localObject1, 0, localObject2, 0, k);
        localObject1 = localObject2;
        m = localObject1.length;
      }
      Object localObject2 = k;
      if (arrayOfChar3 == null)
      {
        if (needSingleQuotation(c1))
        {
          localObject1[(k++)] = 39;
          localObject1[(k++)] = c1;
          localObject1[(k++)] = 39;
          i9 = 0;
        }
        else if ((i7 != 0) && (i7 < i8))
        {
          k += i4;
          i9 = insertOrdered((char[])localObject1, i3, localObject2, k, c1, c2, i7);
        }
        else
        {
          localObject1[(k++)] = c1;
          if (c2 != 0) {
            localObject1[(k++)] = c2;
          }
        }
      }
      else if (needSingleQuotation(arrayOfChar3[i10]))
      {
        localObject1[(k++)] = 39;
        localObject1[(k++)] = arrayOfChar3[(i10++)];
        localObject1[(k++)] = 39;
        i4--;
        do
        {
          localObject1[(k++)] = arrayOfChar3[(i10++)];
          i4--;
        } while (i4 > 0);
      }
      else if ((i7 != 0) && (i7 < i8))
      {
        k += i4;
        i9 = mergeOrdered((char[])localObject1, i3, localObject2, arrayOfChar3, i10, i10 + i4);
      }
      else
      {
        do
        {
          localObject1[(k++)] = arrayOfChar3[(i10++)];
          i4--;
        } while (i4 > 0);
      }
      i8 = i9;
      if (i8 == 0) {
        i3 = k;
      }
    }
    return new String((char[])localObject1, 0, k);
  }
  
  public static String convert(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = -1;
    StringBuffer localStringBuffer = new StringBuffer();
    UCharacterIterator localUCharacterIterator = UCharacterIterator.getInstance(paramString);
    while ((i = localUCharacterIterator.nextCodePoint()) != -1) {
      switch (i)
      {
      case 194664: 
        localStringBuffer.append(corrigendum4MappingTable[0]);
        break;
      case 194676: 
        localStringBuffer.append(corrigendum4MappingTable[1]);
        break;
      case 194847: 
        localStringBuffer.append(corrigendum4MappingTable[2]);
        break;
      case 194911: 
        localStringBuffer.append(corrigendum4MappingTable[3]);
        break;
      case 195007: 
        localStringBuffer.append(corrigendum4MappingTable[4]);
        break;
      default: 
        UTF16.append(localStringBuffer, i);
      }
    }
    return localStringBuffer.toString();
  }
  
  static
  {
    try
    {
      IMPL = new NormalizerImpl();
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException.getMessage());
    }
  }
  
  static final class AuxTrieImpl
    implements Trie.DataManipulate
  {
    static CharTrie auxTrie = null;
    
    AuxTrieImpl() {}
    
    public int getFoldingOffset(int paramInt)
    {
      return (paramInt & 0x3FF) << 5;
    }
  }
  
  private static final class ComposePartArgs
  {
    int prevCC;
    int length;
    
    private ComposePartArgs() {}
  }
  
  private static final class DecomposeArgs
  {
    int cc;
    int trailCC;
    int length;
    
    private DecomposeArgs() {}
  }
  
  static final class FCDTrieImpl
    implements Trie.DataManipulate
  {
    static CharTrie fcdTrie = null;
    
    FCDTrieImpl() {}
    
    public int getFoldingOffset(int paramInt)
    {
      return paramInt;
    }
  }
  
  private static final class NextCCArgs
  {
    char[] source;
    int next;
    int limit;
    char c;
    char c2;
    
    private NextCCArgs() {}
  }
  
  private static final class NextCombiningArgs
  {
    char[] source;
    int start;
    char c;
    char c2;
    int combiningIndex;
    char cc;
    
    private NextCombiningArgs() {}
  }
  
  static final class NormTrieImpl
    implements Trie.DataManipulate
  {
    static IntTrie normTrie = null;
    
    NormTrieImpl() {}
    
    public int getFoldingOffset(int paramInt)
    {
      return 2048 + (paramInt >> 11 & 0x7FE0);
    }
  }
  
  private static final class PrevArgs
  {
    char[] src;
    int start;
    int current;
    char c;
    char c2;
    
    private PrevArgs() {}
  }
  
  private static final class RecomposeArgs
  {
    char[] source;
    int start;
    int limit;
    
    private RecomposeArgs() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\NormalizerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */