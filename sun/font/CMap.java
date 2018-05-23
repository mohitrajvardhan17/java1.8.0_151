package sun.font;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import sun.util.logging.PlatformLogger;

abstract class CMap
{
  static final short ShiftJISEncoding = 2;
  static final short GBKEncoding = 3;
  static final short Big5Encoding = 4;
  static final short WansungEncoding = 5;
  static final short JohabEncoding = 6;
  static final short MSUnicodeSurrogateEncoding = 10;
  static final char noSuchChar = '�';
  static final int SHORTMASK = 65535;
  static final int INTMASK = -1;
  static final char[][] converterMaps = new char[7][];
  char[] xlat;
  public static final NullCMapClass theNullCmap = new NullCMapClass();
  
  CMap() {}
  
  static CMap initialize(TrueTypeFont paramTrueTypeFont)
  {
    CMap localCMap = null;
    int k = -1;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    int i6 = 0;
    int i7 = 0;
    ByteBuffer localByteBuffer = paramTrueTypeFont.getTableBuffer(1668112752);
    int i8 = paramTrueTypeFont.getTableSize(1668112752);
    int i9 = localByteBuffer.getShort(2);
    for (int i10 = 0; i10 < i9; i10++)
    {
      localByteBuffer.position(i10 * 8 + 4);
      int j = localByteBuffer.getShort();
      if (j == 3)
      {
        i7 = 1;
        k = localByteBuffer.getShort();
        int i = localByteBuffer.getInt();
        switch (k)
        {
        case 0: 
          m = i;
          break;
        case 1: 
          n = i;
          break;
        case 2: 
          i1 = i;
          break;
        case 3: 
          i2 = i;
          break;
        case 4: 
          i3 = i;
          break;
        case 5: 
          i4 = i;
          break;
        case 6: 
          i5 = i;
          break;
        case 10: 
          i6 = i;
        }
      }
    }
    if (i7 != 0)
    {
      if (i6 != 0) {
        localCMap = createCMap(localByteBuffer, i6, null);
      } else if (m != 0) {
        localCMap = createCMap(localByteBuffer, m, null);
      } else if (n != 0) {
        localCMap = createCMap(localByteBuffer, n, null);
      } else if (i1 != 0) {
        localCMap = createCMap(localByteBuffer, i1, getConverterMap((short)2));
      } else if (i2 != 0) {
        localCMap = createCMap(localByteBuffer, i2, getConverterMap((short)3));
      } else if (i3 != 0)
      {
        if ((FontUtilities.isSolaris) && (platName != null) && ((platName.startsWith("/usr/openwin/lib/locale/zh_CN.EUC/X11/fonts/TrueType")) || (platName.startsWith("/usr/openwin/lib/locale/zh_CN/X11/fonts/TrueType")) || (platName.startsWith("/usr/openwin/lib/locale/zh/X11/fonts/TrueType")))) {
          localCMap = createCMap(localByteBuffer, i3, getConverterMap((short)3));
        } else {
          localCMap = createCMap(localByteBuffer, i3, getConverterMap((short)4));
        }
      }
      else if (i4 != 0) {
        localCMap = createCMap(localByteBuffer, i4, getConverterMap((short)5));
      } else if (i5 != 0) {
        localCMap = createCMap(localByteBuffer, i5, getConverterMap((short)6));
      }
    }
    else {
      localCMap = createCMap(localByteBuffer, localByteBuffer.getInt(8), null);
    }
    return localCMap;
  }
  
  static char[] getConverter(short paramShort)
  {
    int i = 32768;
    int j = 65535;
    String str;
    switch (paramShort)
    {
    case 2: 
      i = 33088;
      j = 64764;
      str = "SJIS";
      break;
    case 3: 
      i = 33088;
      j = 65184;
      str = "GBK";
      break;
    case 4: 
      i = 41280;
      j = 65278;
      str = "Big5";
      break;
    case 5: 
      i = 41377;
      j = 65246;
      str = "EUC_KR";
      break;
    case 6: 
      i = 33089;
      j = 65022;
      str = "Johab";
      break;
    default: 
      return null;
    }
    try
    {
      char[] arrayOfChar1 = new char[65536];
      for (int k = 0; k < 65536; k++) {
        arrayOfChar1[k] = 65533;
      }
      byte[] arrayOfByte = new byte[(j - i + 1) * 2];
      char[] arrayOfChar2 = new char[j - i + 1];
      int m = 0;
      if (paramShort == 2) {
        for (i1 = i; i1 <= j; i1++)
        {
          int n = i1 >> 8 & 0xFF;
          if ((n >= 161) && (n <= 223))
          {
            arrayOfByte[(m++)] = -1;
            arrayOfByte[(m++)] = -1;
          }
          else
          {
            arrayOfByte[(m++)] = ((byte)n);
            arrayOfByte[(m++)] = ((byte)(i1 & 0xFF));
          }
        }
      } else {
        for (i1 = i; i1 <= j; i1++)
        {
          arrayOfByte[(m++)] = ((byte)(i1 >> 8 & 0xFF));
          arrayOfByte[(m++)] = ((byte)(i1 & 0xFF));
        }
      }
      Charset.forName(str).newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith("\000").decode(ByteBuffer.wrap(arrayOfByte, 0, arrayOfByte.length), CharBuffer.wrap(arrayOfChar2, 0, arrayOfChar2.length), true);
      for (int i1 = 32; i1 <= 126; i1++) {
        arrayOfChar1[i1] = ((char)i1);
      }
      if (paramShort == 2) {
        for (i1 = 161; i1 <= 223; i1++) {
          arrayOfChar1[i1] = ((char)(i1 - 161 + 65377));
        }
      }
      System.arraycopy(arrayOfChar2, 0, arrayOfChar1, i, arrayOfChar2.length);
      char[] arrayOfChar3 = new char[65536];
      for (int i2 = 0; i2 < 65536; i2++) {
        if (arrayOfChar1[i2] != 65533) {
          arrayOfChar3[arrayOfChar1[i2]] = ((char)i2);
        }
      }
      return arrayOfChar3;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  static char[] getConverterMap(short paramShort)
  {
    if (converterMaps[paramShort] == null) {
      converterMaps[paramShort] = getConverter(paramShort);
    }
    return converterMaps[paramShort];
  }
  
  static CMap createCMap(ByteBuffer paramByteBuffer, int paramInt, char[] paramArrayOfChar)
  {
    int i = paramByteBuffer.getChar(paramInt);
    long l;
    if (i < 8) {
      l = paramByteBuffer.getChar(paramInt + 2);
    } else {
      l = paramByteBuffer.getInt(paramInt + 4) & 0xFFFFFFFF;
    }
    if ((paramInt + l > paramByteBuffer.capacity()) && (FontUtilities.isLogging())) {
      FontUtilities.getLogger().warning("Cmap subtable overflows buffer.");
    }
    switch (i)
    {
    case 0: 
      return new CMapFormat0(paramByteBuffer, paramInt);
    case 2: 
      return new CMapFormat2(paramByteBuffer, paramInt, paramArrayOfChar);
    case 4: 
      return new CMapFormat4(paramByteBuffer, paramInt, paramArrayOfChar);
    case 6: 
      return new CMapFormat6(paramByteBuffer, paramInt, paramArrayOfChar);
    case 8: 
      return new CMapFormat8(paramByteBuffer, paramInt, paramArrayOfChar);
    case 10: 
      return new CMapFormat10(paramByteBuffer, paramInt, paramArrayOfChar);
    case 12: 
      return new CMapFormat12(paramByteBuffer, paramInt, paramArrayOfChar);
    }
    throw new RuntimeException("Cmap format unimplemented: " + paramByteBuffer.getChar(paramInt));
  }
  
  abstract char getGlyph(int paramInt);
  
  final int getControlCodeGlyph(int paramInt, boolean paramBoolean)
  {
    if (paramInt < 16)
    {
      switch (paramInt)
      {
      case 9: 
      case 10: 
      case 13: 
        return 65535;
      }
    }
    else if (paramInt >= 8204)
    {
      if ((paramInt <= 8207) || ((paramInt >= 8232) && (paramInt <= 8238)) || ((paramInt >= 8298) && (paramInt <= 8303))) {
        return 65535;
      }
      if ((paramBoolean) && (paramInt >= 65535)) {
        return 0;
      }
    }
    return -1;
  }
  
  static class CMapFormat0
    extends CMap
  {
    byte[] cmap;
    
    CMapFormat0(ByteBuffer paramByteBuffer, int paramInt)
    {
      int i = paramByteBuffer.getChar(paramInt + 2);
      cmap = new byte[i - 6];
      paramByteBuffer.position(paramInt + 6);
      paramByteBuffer.get(cmap);
    }
    
    char getGlyph(int paramInt)
    {
      if (paramInt < 256)
      {
        if (paramInt < 16) {
          switch (paramInt)
          {
          case 9: 
          case 10: 
          case 13: 
            return 65535;
          }
        }
        return (char)(0xFF & cmap[paramInt]);
      }
      return '\000';
    }
  }
  
  static class CMapFormat10
    extends CMap
  {
    long firstCode;
    int entryCount;
    char[] glyphIdArray;
    
    CMapFormat10(ByteBuffer paramByteBuffer, int paramInt, char[] paramArrayOfChar)
    {
      firstCode = (paramByteBuffer.getInt() & 0xFFFFFFFF);
      entryCount = (paramByteBuffer.getInt() & 0xFFFFFFFF);
      paramByteBuffer.position(paramInt + 20);
      CharBuffer localCharBuffer = paramByteBuffer.asCharBuffer();
      glyphIdArray = new char[entryCount];
      for (int i = 0; i < entryCount; i++) {
        glyphIdArray[i] = localCharBuffer.get();
      }
    }
    
    char getGlyph(int paramInt)
    {
      if (xlat != null) {
        throw new RuntimeException("xlat array for cmap fmt=10");
      }
      int i = (int)(paramInt - firstCode);
      if ((i < 0) || (i >= entryCount)) {
        return '\000';
      }
      return glyphIdArray[i];
    }
  }
  
  static class CMapFormat12
    extends CMap
  {
    int numGroups;
    int highBit = 0;
    int power;
    int extra;
    long[] startCharCode;
    long[] endCharCode;
    int[] startGlyphID;
    
    CMapFormat12(ByteBuffer paramByteBuffer, int paramInt, char[] paramArrayOfChar)
    {
      if (paramArrayOfChar != null) {
        throw new RuntimeException("xlat array for cmap fmt=12");
      }
      numGroups = paramByteBuffer.getInt(paramInt + 12);
      startCharCode = new long[numGroups];
      endCharCode = new long[numGroups];
      startGlyphID = new int[numGroups];
      paramByteBuffer.position(paramInt + 16);
      paramByteBuffer = paramByteBuffer.slice();
      IntBuffer localIntBuffer = paramByteBuffer.asIntBuffer();
      for (int i = 0; i < numGroups; i++)
      {
        startCharCode[i] = (localIntBuffer.get() & 0xFFFFFFFF);
        endCharCode[i] = (localIntBuffer.get() & 0xFFFFFFFF);
        startGlyphID[i] = (localIntBuffer.get() & 0xFFFFFFFF);
      }
      i = numGroups;
      if (i >= 65536)
      {
        i >>= 16;
        highBit += 16;
      }
      if (i >= 256)
      {
        i >>= 8;
        highBit += 8;
      }
      if (i >= 16)
      {
        i >>= 4;
        highBit += 4;
      }
      if (i >= 4)
      {
        i >>= 2;
        highBit += 2;
      }
      if (i >= 2)
      {
        i >>= 1;
        highBit += 1;
      }
      power = (1 << highBit);
      extra = (numGroups - power);
    }
    
    char getGlyph(int paramInt)
    {
      int i = getControlCodeGlyph(paramInt, false);
      if (i >= 0) {
        return (char)i;
      }
      int j = power;
      int k = 0;
      if (startCharCode[extra] <= paramInt) {
        k = extra;
      }
      while (j > 1)
      {
        j >>= 1;
        if (startCharCode[(k + j)] <= paramInt) {
          k += j;
        }
      }
      if ((startCharCode[k] <= paramInt) && (endCharCode[k] >= paramInt)) {
        return (char)(int)(startGlyphID[k] + (paramInt - startCharCode[k]));
      }
      return '\000';
    }
  }
  
  static class CMapFormat2
    extends CMap
  {
    char[] subHeaderKey = new char['Ā'];
    char[] firstCodeArray;
    char[] entryCountArray;
    short[] idDeltaArray;
    char[] idRangeOffSetArray;
    char[] glyphIndexArray;
    
    CMapFormat2(ByteBuffer paramByteBuffer, int paramInt, char[] paramArrayOfChar)
    {
      xlat = paramArrayOfChar;
      int i = paramByteBuffer.getChar(paramInt + 2);
      paramByteBuffer.position(paramInt + 6);
      CharBuffer localCharBuffer = paramByteBuffer.asCharBuffer();
      int j = 0;
      for (int k = 0; k < 256; k++)
      {
        subHeaderKey[k] = localCharBuffer.get();
        if (subHeaderKey[k] > j) {
          j = subHeaderKey[k];
        }
      }
      k = (j >> 3) + 1;
      firstCodeArray = new char[k];
      entryCountArray = new char[k];
      idDeltaArray = new short[k];
      idRangeOffSetArray = new char[k];
      for (int m = 0; m < k; m++)
      {
        firstCodeArray[m] = localCharBuffer.get();
        entryCountArray[m] = localCharBuffer.get();
        idDeltaArray[m] = ((short)localCharBuffer.get());
        idRangeOffSetArray[m] = localCharBuffer.get();
      }
      m = (i - 518 - k * 8) / 2;
      glyphIndexArray = new char[m];
      for (int n = 0; n < m; n++) {
        glyphIndexArray[n] = localCharBuffer.get();
      }
    }
    
    char getGlyph(int paramInt)
    {
      int i = getControlCodeGlyph(paramInt, true);
      if (i >= 0) {
        return (char)i;
      }
      if (xlat != null) {
        paramInt = xlat[paramInt];
      }
      int j = (char)(paramInt >> 8);
      int k = (char)(paramInt & 0xFF);
      int m = subHeaderKey[j] >> '\003';
      if (m != 0)
      {
        n = k;
      }
      else
      {
        n = j;
        if (n == 0) {
          n = k;
        }
      }
      int i1 = firstCodeArray[m];
      if (n < i1) {
        return '\000';
      }
      int n = (char)(n - i1);
      if (n < entryCountArray[m])
      {
        int i2 = (idRangeOffSetArray.length - m) * 8 - 6;
        int i3 = (idRangeOffSetArray[m] - i2) / 2;
        int i4 = glyphIndexArray[(i3 + n)];
        if (i4 != 0)
        {
          i4 = (char)(i4 + idDeltaArray[m]);
          return i4;
        }
      }
      return '\000';
    }
  }
  
  static class CMapFormat4
    extends CMap
  {
    int segCount;
    int entrySelector;
    int rangeShift;
    char[] endCount;
    char[] startCount;
    short[] idDelta;
    char[] idRangeOffset;
    char[] glyphIds;
    
    CMapFormat4(ByteBuffer paramByteBuffer, int paramInt, char[] paramArrayOfChar)
    {
      xlat = paramArrayOfChar;
      paramByteBuffer.position(paramInt);
      CharBuffer localCharBuffer = paramByteBuffer.asCharBuffer();
      localCharBuffer.get();
      int i = localCharBuffer.get();
      if (paramInt + i > paramByteBuffer.capacity()) {
        i = paramByteBuffer.capacity() - paramInt;
      }
      localCharBuffer.get();
      segCount = (localCharBuffer.get() / '\002');
      int j = localCharBuffer.get();
      entrySelector = localCharBuffer.get();
      rangeShift = (localCharBuffer.get() / '\002');
      startCount = new char[segCount];
      endCount = new char[segCount];
      idDelta = new short[segCount];
      idRangeOffset = new char[segCount];
      for (int k = 0; k < segCount; k++) {
        endCount[k] = localCharBuffer.get();
      }
      localCharBuffer.get();
      for (k = 0; k < segCount; k++) {
        startCount[k] = localCharBuffer.get();
      }
      for (k = 0; k < segCount; k++) {
        idDelta[k] = ((short)localCharBuffer.get());
      }
      for (k = 0; k < segCount; k++)
      {
        m = localCharBuffer.get();
        idRangeOffset[k] = ((char)(m >> 1 & 0xFFFF));
      }
      k = (segCount * 8 + 16) / 2;
      localCharBuffer.position(k);
      int m = i / 2 - k;
      glyphIds = new char[m];
      for (int n = 0; n < m; n++) {
        glyphIds[n] = localCharBuffer.get();
      }
    }
    
    char getGlyph(int paramInt)
    {
      int i = 0;
      int j = 0;
      int k = getControlCodeGlyph(paramInt, true);
      if (k >= 0) {
        return (char)k;
      }
      if (xlat != null) {
        paramInt = xlat[paramInt];
      }
      int m = 0;
      int n = startCount.length;
      for (i = startCount.length >> 1; m < n; i = m + n >> 1) {
        if (endCount[i] < paramInt) {
          m = i + 1;
        } else {
          n = i;
        }
      }
      if ((paramInt >= startCount[i]) && (paramInt <= endCount[i]))
      {
        int i1 = idRangeOffset[i];
        if (i1 == 0)
        {
          j = (char)(paramInt + idDelta[i]);
        }
        else
        {
          int i2 = i1 - segCount + i + (paramInt - startCount[i]);
          j = glyphIds[i2];
          if (j != 0) {
            j = (char)(j + idDelta[i]);
          }
        }
      }
      if (j != 0) {}
      return j;
    }
  }
  
  static class CMapFormat6
    extends CMap
  {
    char firstCode;
    char entryCount;
    char[] glyphIdArray;
    
    CMapFormat6(ByteBuffer paramByteBuffer, int paramInt, char[] paramArrayOfChar)
    {
      paramByteBuffer.position(paramInt + 6);
      CharBuffer localCharBuffer = paramByteBuffer.asCharBuffer();
      firstCode = localCharBuffer.get();
      entryCount = localCharBuffer.get();
      glyphIdArray = new char[entryCount];
      for (int i = 0; i < entryCount; i++) {
        glyphIdArray[i] = localCharBuffer.get();
      }
    }
    
    char getGlyph(int paramInt)
    {
      int i = getControlCodeGlyph(paramInt, true);
      if (i >= 0) {
        return (char)i;
      }
      if (xlat != null) {
        paramInt = xlat[paramInt];
      }
      paramInt -= firstCode;
      if ((paramInt < 0) || (paramInt >= entryCount)) {
        return '\000';
      }
      return glyphIdArray[paramInt];
    }
  }
  
  static class CMapFormat8
    extends CMap
  {
    byte[] is32 = new byte[' '];
    int nGroups;
    int[] startCharCode;
    int[] endCharCode;
    int[] startGlyphID;
    
    CMapFormat8(ByteBuffer paramByteBuffer, int paramInt, char[] paramArrayOfChar)
    {
      paramByteBuffer.position(12);
      paramByteBuffer.get(is32);
      nGroups = paramByteBuffer.getInt();
      startCharCode = new int[nGroups];
      endCharCode = new int[nGroups];
      startGlyphID = new int[nGroups];
    }
    
    char getGlyph(int paramInt)
    {
      if (xlat != null) {
        throw new RuntimeException("xlat array for cmap fmt=8");
      }
      return '\000';
    }
  }
  
  static class NullCMapClass
    extends CMap
  {
    NullCMapClass() {}
    
    char getGlyph(int paramInt)
    {
      return '\000';
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\CMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */