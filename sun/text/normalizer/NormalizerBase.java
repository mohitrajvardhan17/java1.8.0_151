package sun.text.normalizer;

import java.text.CharacterIterator;
import java.text.Normalizer.Form;

public final class NormalizerBase
  implements Cloneable
{
  private char[] buffer = new char[100];
  private int bufferStart = 0;
  private int bufferPos = 0;
  private int bufferLimit = 0;
  private UCharacterIterator text;
  private Mode mode = NFC;
  private int options = 0;
  private int currentIndex;
  private int nextIndex;
  public static final int UNICODE_3_2 = 32;
  public static final int DONE = -1;
  public static final Mode NONE = new Mode(1, null);
  public static final Mode NFD = new NFDMode(2, null);
  public static final Mode NFKD = new NFKDMode(3, null);
  public static final Mode NFC = new NFCMode(4, null);
  public static final Mode NFKC = new NFKCMode(5, null);
  public static final QuickCheckResult NO = new QuickCheckResult(0, null);
  public static final QuickCheckResult YES = new QuickCheckResult(1, null);
  public static final QuickCheckResult MAYBE = new QuickCheckResult(2, null);
  private static final int MAX_BUF_SIZE_COMPOSE = 2;
  private static final int MAX_BUF_SIZE_DECOMPOSE = 3;
  public static final int UNICODE_3_2_0_ORIGINAL = 262432;
  public static final int UNICODE_LATEST = 0;
  
  public NormalizerBase(String paramString, Mode paramMode, int paramInt)
  {
    text = UCharacterIterator.getInstance(paramString);
    mode = paramMode;
    options = paramInt;
  }
  
  public NormalizerBase(CharacterIterator paramCharacterIterator, Mode paramMode)
  {
    this(paramCharacterIterator, paramMode, 0);
  }
  
  public NormalizerBase(CharacterIterator paramCharacterIterator, Mode paramMode, int paramInt)
  {
    text = UCharacterIterator.getInstance((CharacterIterator)paramCharacterIterator.clone());
    mode = paramMode;
    options = paramInt;
  }
  
  public Object clone()
  {
    try
    {
      NormalizerBase localNormalizerBase = (NormalizerBase)super.clone();
      text = ((UCharacterIterator)text.clone());
      if (buffer != null)
      {
        buffer = new char[buffer.length];
        System.arraycopy(buffer, 0, buffer, 0, buffer.length);
      }
      return localNormalizerBase;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException.toString(), localCloneNotSupportedException);
    }
  }
  
  public static String compose(String paramString, boolean paramBoolean, int paramInt)
  {
    char[] arrayOfChar1;
    char[] arrayOfChar2;
    if (paramInt == 262432)
    {
      String str = NormalizerImpl.convert(paramString);
      arrayOfChar1 = new char[str.length() * 2];
      arrayOfChar2 = str.toCharArray();
    }
    else
    {
      arrayOfChar1 = new char[paramString.length() * 2];
      arrayOfChar2 = paramString.toCharArray();
    }
    int i = 0;
    UnicodeSet localUnicodeSet = NormalizerImpl.getNX(paramInt);
    paramInt &= 0xCF00;
    if (paramBoolean) {
      paramInt |= 0x1000;
    }
    for (;;)
    {
      i = NormalizerImpl.compose(arrayOfChar2, 0, arrayOfChar2.length, arrayOfChar1, 0, arrayOfChar1.length, paramInt, localUnicodeSet);
      if (i <= arrayOfChar1.length) {
        return new String(arrayOfChar1, 0, i);
      }
      arrayOfChar1 = new char[i];
    }
  }
  
  public static String decompose(String paramString, boolean paramBoolean)
  {
    return decompose(paramString, paramBoolean, 0);
  }
  
  public static String decompose(String paramString, boolean paramBoolean, int paramInt)
  {
    int[] arrayOfInt = new int[1];
    int i = 0;
    UnicodeSet localUnicodeSet = NormalizerImpl.getNX(paramInt);
    if (paramInt == 262432)
    {
      String str = NormalizerImpl.convert(paramString);
      for (arrayOfChar = new char[str.length() * 3];; arrayOfChar = new char[i])
      {
        i = NormalizerImpl.decompose(str.toCharArray(), 0, str.length(), arrayOfChar, 0, arrayOfChar.length, paramBoolean, arrayOfInt, localUnicodeSet);
        if (i <= arrayOfChar.length) {
          return new String(arrayOfChar, 0, i);
        }
      }
    }
    for (char[] arrayOfChar = new char[paramString.length() * 3];; arrayOfChar = new char[i])
    {
      i = NormalizerImpl.decompose(paramString.toCharArray(), 0, paramString.length(), arrayOfChar, 0, arrayOfChar.length, paramBoolean, arrayOfInt, localUnicodeSet);
      if (i <= arrayOfChar.length) {
        return new String(arrayOfChar, 0, i);
      }
    }
  }
  
  public static int normalize(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, Mode paramMode, int paramInt5)
  {
    int i = paramMode.normalize(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4, paramInt5);
    if (i <= paramInt4 - paramInt3) {
      return i;
    }
    throw new IndexOutOfBoundsException(Integer.toString(i));
  }
  
  public int current()
  {
    if ((bufferPos < bufferLimit) || (nextNormalize())) {
      return getCodePointAt(bufferPos);
    }
    return -1;
  }
  
  public int next()
  {
    if ((bufferPos < bufferLimit) || (nextNormalize()))
    {
      int i = getCodePointAt(bufferPos);
      bufferPos += (i > 65535 ? 2 : 1);
      return i;
    }
    return -1;
  }
  
  public int previous()
  {
    if ((bufferPos > 0) || (previousNormalize()))
    {
      int i = getCodePointAt(bufferPos - 1);
      bufferPos -= (i > 65535 ? 2 : 1);
      return i;
    }
    return -1;
  }
  
  public void reset()
  {
    text.setIndex(0);
    currentIndex = (nextIndex = 0);
    clearBuffer();
  }
  
  public void setIndexOnly(int paramInt)
  {
    text.setIndex(paramInt);
    currentIndex = (nextIndex = paramInt);
    clearBuffer();
  }
  
  @Deprecated
  public int setIndex(int paramInt)
  {
    setIndexOnly(paramInt);
    return current();
  }
  
  @Deprecated
  public int getBeginIndex()
  {
    return 0;
  }
  
  @Deprecated
  public int getEndIndex()
  {
    return endIndex();
  }
  
  public int getIndex()
  {
    if (bufferPos < bufferLimit) {
      return currentIndex;
    }
    return nextIndex;
  }
  
  public int endIndex()
  {
    return text.getLength();
  }
  
  public void setMode(Mode paramMode)
  {
    mode = paramMode;
  }
  
  public Mode getMode()
  {
    return mode;
  }
  
  public void setText(String paramString)
  {
    UCharacterIterator localUCharacterIterator = UCharacterIterator.getInstance(paramString);
    if (localUCharacterIterator == null) {
      throw new InternalError("Could not create a new UCharacterIterator");
    }
    text = localUCharacterIterator;
    reset();
  }
  
  public void setText(CharacterIterator paramCharacterIterator)
  {
    UCharacterIterator localUCharacterIterator = UCharacterIterator.getInstance(paramCharacterIterator);
    if (localUCharacterIterator == null) {
      throw new InternalError("Could not create a new UCharacterIterator");
    }
    text = localUCharacterIterator;
    currentIndex = (nextIndex = 0);
    clearBuffer();
  }
  
  private static long getPrevNorm32(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, char[] paramArrayOfChar)
  {
    int i = 0;
    if ((i = paramUCharacterIterator.previous()) == -1) {
      return 0L;
    }
    paramArrayOfChar[0] = ((char)i);
    paramArrayOfChar[1] = '\000';
    if (paramArrayOfChar[0] < paramInt1) {
      return 0L;
    }
    if (!UTF16.isSurrogate(paramArrayOfChar[0])) {
      return NormalizerImpl.getNorm32(paramArrayOfChar[0]);
    }
    if ((UTF16.isLeadSurrogate(paramArrayOfChar[0])) || (paramUCharacterIterator.getIndex() == 0))
    {
      paramArrayOfChar[1] = ((char)paramUCharacterIterator.current());
      return 0L;
    }
    if (UTF16.isLeadSurrogate(paramArrayOfChar[1] = (char)paramUCharacterIterator.previous()))
    {
      long l = NormalizerImpl.getNorm32(paramArrayOfChar[1]);
      if ((l & paramInt2) == 0L) {
        return 0L;
      }
      return NormalizerImpl.getNorm32FromSurrogatePair(l, paramArrayOfChar[0]);
    }
    paramUCharacterIterator.moveIndex(1);
    return 0L;
  }
  
  private static int findPreviousIterationBoundary(UCharacterIterator paramUCharacterIterator, IsPrevBoundary paramIsPrevBoundary, int paramInt1, int paramInt2, char[] paramArrayOfChar, int[] paramArrayOfInt)
  {
    char[] arrayOfChar1 = new char[2];
    paramArrayOfInt[0] = paramArrayOfChar.length;
    arrayOfChar1[0] = '\000';
    while ((paramUCharacterIterator.getIndex() > 0) && (arrayOfChar1[0] != '￿'))
    {
      boolean bool = paramIsPrevBoundary.isPrevBoundary(paramUCharacterIterator, paramInt1, paramInt2, arrayOfChar1);
      if (paramArrayOfInt[0] < (arrayOfChar1[1] == 0 ? 1 : 2))
      {
        char[] arrayOfChar2 = new char[paramArrayOfChar.length * 2];
        System.arraycopy(paramArrayOfChar, paramArrayOfInt[0], arrayOfChar2, arrayOfChar2.length - (paramArrayOfChar.length - paramArrayOfInt[0]), paramArrayOfChar.length - paramArrayOfInt[0]);
        paramArrayOfInt[0] += arrayOfChar2.length - paramArrayOfChar.length;
        paramArrayOfChar = arrayOfChar2;
        arrayOfChar2 = null;
      }
      paramArrayOfChar[(paramArrayOfInt[0] -= 1)] = arrayOfChar1[0];
      if (arrayOfChar1[1] != 0) {
        paramArrayOfChar[(paramArrayOfInt[0] -= 1)] = arrayOfChar1[1];
      }
      if (bool) {
        break;
      }
    }
    return paramArrayOfChar.length - paramArrayOfInt[0];
  }
  
  private static int previous(UCharacterIterator paramUCharacterIterator, char[] paramArrayOfChar, int paramInt1, int paramInt2, Mode paramMode, boolean paramBoolean, boolean[] paramArrayOfBoolean, int paramInt3)
  {
    int i2 = paramInt2 - paramInt1;
    int i = 0;
    if (paramArrayOfBoolean != null) {
      paramArrayOfBoolean[0] = false;
    }
    int i1 = (char)paramMode.getMinC();
    int k = paramMode.getMask();
    IsPrevBoundary localIsPrevBoundary = paramMode.getPrevBoundary();
    if (localIsPrevBoundary == null)
    {
      i = 0;
      int m;
      if ((m = paramUCharacterIterator.previous()) >= 0)
      {
        i = 1;
        if (UTF16.isTrailSurrogate((char)m))
        {
          int n = paramUCharacterIterator.previous();
          if (n != -1) {
            if (UTF16.isLeadSurrogate((char)n))
            {
              if (i2 >= 2)
              {
                paramArrayOfChar[1] = ((char)m);
                i = 2;
              }
              m = n;
            }
            else
            {
              paramUCharacterIterator.moveIndex(1);
            }
          }
        }
        if (i2 > 0) {
          paramArrayOfChar[0] = ((char)m);
        }
      }
      return i;
    }
    char[] arrayOfChar = new char[100];
    int[] arrayOfInt = new int[1];
    int j = findPreviousIterationBoundary(paramUCharacterIterator, localIsPrevBoundary, i1, k, arrayOfChar, arrayOfInt);
    if (j > 0) {
      if (paramBoolean)
      {
        i = normalize(arrayOfChar, arrayOfInt[0], arrayOfInt[0] + j, paramArrayOfChar, paramInt1, paramInt2, paramMode, paramInt3);
        if (paramArrayOfBoolean != null) {
          paramArrayOfBoolean[0] = ((i != j) || (Utility.arrayRegionMatches(arrayOfChar, 0, paramArrayOfChar, paramInt1, paramInt2)) ? 1 : false);
        }
      }
      else if (i2 > 0)
      {
        System.arraycopy(arrayOfChar, arrayOfInt[0], paramArrayOfChar, 0, j < i2 ? j : i2);
      }
    }
    return i;
  }
  
  private static long getNextNorm32(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    paramArrayOfInt[0] = paramUCharacterIterator.next();
    paramArrayOfInt[1] = 0;
    if (paramArrayOfInt[0] < paramInt1) {
      return 0L;
    }
    long l = NormalizerImpl.getNorm32((char)paramArrayOfInt[0]);
    if (UTF16.isLeadSurrogate((char)paramArrayOfInt[0]))
    {
      if ((paramUCharacterIterator.current() != -1) && (UTF16.isTrailSurrogate((char)(paramArrayOfInt[1] = paramUCharacterIterator.current()))))
      {
        paramUCharacterIterator.moveIndex(1);
        if ((l & paramInt2) == 0L) {
          return 0L;
        }
        return NormalizerImpl.getNorm32FromSurrogatePair(l, (char)paramArrayOfInt[1]);
      }
      return 0L;
    }
    return l;
  }
  
  private static int findNextIterationBoundary(UCharacterIterator paramUCharacterIterator, IsNextBoundary paramIsNextBoundary, int paramInt1, int paramInt2, char[] paramArrayOfChar)
  {
    if (paramUCharacterIterator.current() == -1) {
      return 0;
    }
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = paramUCharacterIterator.next();
    paramArrayOfChar[0] = ((char)arrayOfInt[0]);
    int i = 1;
    if ((UTF16.isLeadSurrogate((char)arrayOfInt[0])) && (paramUCharacterIterator.current() != -1)) {
      if (UTF16.isTrailSurrogate((char)(arrayOfInt[1] = paramUCharacterIterator.next()))) {
        paramArrayOfChar[(i++)] = ((char)arrayOfInt[1]);
      } else {
        paramUCharacterIterator.moveIndex(-1);
      }
    }
    while (paramUCharacterIterator.current() != -1)
    {
      if (paramIsNextBoundary.isNextBoundary(paramUCharacterIterator, paramInt1, paramInt2, arrayOfInt))
      {
        paramUCharacterIterator.moveIndex(arrayOfInt[1] == 0 ? -1 : -2);
        break;
      }
      if (i + (arrayOfInt[1] == 0 ? 1 : 2) <= paramArrayOfChar.length)
      {
        paramArrayOfChar[(i++)] = ((char)arrayOfInt[0]);
        if (arrayOfInt[1] != 0) {
          paramArrayOfChar[(i++)] = ((char)arrayOfInt[1]);
        }
      }
      else
      {
        char[] arrayOfChar = new char[paramArrayOfChar.length * 2];
        System.arraycopy(paramArrayOfChar, 0, arrayOfChar, 0, i);
        paramArrayOfChar = arrayOfChar;
        paramArrayOfChar[(i++)] = ((char)arrayOfInt[0]);
        if (arrayOfInt[1] != 0) {
          paramArrayOfChar[(i++)] = ((char)arrayOfInt[1]);
        }
      }
    }
    return i;
  }
  
  private static int next(UCharacterIterator paramUCharacterIterator, char[] paramArrayOfChar, int paramInt1, int paramInt2, Mode paramMode, boolean paramBoolean, boolean[] paramArrayOfBoolean, int paramInt3)
  {
    int i1 = paramInt2 - paramInt1;
    int i2 = 0;
    if (paramArrayOfBoolean != null) {
      paramArrayOfBoolean[0] = false;
    }
    int n = (char)paramMode.getMinC();
    int i = paramMode.getMask();
    IsNextBoundary localIsNextBoundary = paramMode.getNextBoundary();
    if (localIsNextBoundary == null)
    {
      i2 = 0;
      int k = paramUCharacterIterator.next();
      if (k != -1)
      {
        i2 = 1;
        if (UTF16.isLeadSurrogate((char)k))
        {
          int m = paramUCharacterIterator.next();
          if (m != -1) {
            if (UTF16.isTrailSurrogate((char)m))
            {
              if (i1 >= 2)
              {
                paramArrayOfChar[1] = ((char)m);
                i2 = 2;
              }
            }
            else {
              paramUCharacterIterator.moveIndex(-1);
            }
          }
        }
        if (i1 > 0) {
          paramArrayOfChar[0] = ((char)k);
        }
      }
      return i2;
    }
    char[] arrayOfChar = new char[100];
    int[] arrayOfInt = new int[1];
    int j = findNextIterationBoundary(paramUCharacterIterator, localIsNextBoundary, n, i, arrayOfChar);
    if (j > 0) {
      if (paramBoolean)
      {
        i2 = paramMode.normalize(arrayOfChar, arrayOfInt[0], j, paramArrayOfChar, paramInt1, paramInt2, paramInt3);
        if (paramArrayOfBoolean != null) {
          paramArrayOfBoolean[0] = ((i2 != j) || (Utility.arrayRegionMatches(arrayOfChar, arrayOfInt[0], paramArrayOfChar, paramInt1, i2)) ? 1 : false);
        }
      }
      else if (i1 > 0)
      {
        System.arraycopy(arrayOfChar, 0, paramArrayOfChar, paramInt1, Math.min(j, i1));
      }
    }
    return i2;
  }
  
  private void clearBuffer()
  {
    bufferLimit = (bufferStart = bufferPos = 0);
  }
  
  private boolean nextNormalize()
  {
    clearBuffer();
    currentIndex = nextIndex;
    text.setIndex(nextIndex);
    bufferLimit = next(text, buffer, bufferStart, buffer.length, mode, true, null, options);
    nextIndex = text.getIndex();
    return bufferLimit > 0;
  }
  
  private boolean previousNormalize()
  {
    clearBuffer();
    nextIndex = currentIndex;
    text.setIndex(currentIndex);
    bufferLimit = previous(text, buffer, bufferStart, buffer.length, mode, true, null, options);
    currentIndex = text.getIndex();
    bufferPos = bufferLimit;
    return bufferLimit > 0;
  }
  
  private int getCodePointAt(int paramInt)
  {
    if (UTF16.isSurrogate(buffer[paramInt])) {
      if (UTF16.isLeadSurrogate(buffer[paramInt]))
      {
        if ((paramInt + 1 < bufferLimit) && (UTF16.isTrailSurrogate(buffer[(paramInt + 1)]))) {
          return UCharacterProperty.getRawSupplementary(buffer[paramInt], buffer[(paramInt + 1)]);
        }
      }
      else if ((UTF16.isTrailSurrogate(buffer[paramInt])) && (paramInt > 0) && (UTF16.isLeadSurrogate(buffer[(paramInt - 1)]))) {
        return UCharacterProperty.getRawSupplementary(buffer[(paramInt - 1)], buffer[paramInt]);
      }
    }
    return buffer[paramInt];
  }
  
  public static boolean isNFSkippable(int paramInt, Mode paramMode)
  {
    return paramMode.isNFSkippable(paramInt);
  }
  
  public NormalizerBase(String paramString, Mode paramMode)
  {
    this(paramString, paramMode, 0);
  }
  
  public static String normalize(String paramString, Normalizer.Form paramForm)
  {
    return normalize(paramString, paramForm, 0);
  }
  
  public static String normalize(String paramString, Normalizer.Form paramForm, int paramInt)
  {
    int i = paramString.length();
    int j = 1;
    if (i < 80)
    {
      for (int k = 0; k < i; k++) {
        if (paramString.charAt(k) > '')
        {
          j = 0;
          break;
        }
      }
    }
    else
    {
      char[] arrayOfChar = paramString.toCharArray();
      for (int m = 0; m < i; m++) {
        if (arrayOfChar[m] > '')
        {
          j = 0;
          break;
        }
      }
    }
    switch (paramForm)
    {
    case NFC: 
      return j != 0 ? paramString : NFC.normalize(paramString, paramInt);
    case NFD: 
      return j != 0 ? paramString : NFD.normalize(paramString, paramInt);
    case NFKC: 
      return j != 0 ? paramString : NFKC.normalize(paramString, paramInt);
    case NFKD: 
      return j != 0 ? paramString : NFKD.normalize(paramString, paramInt);
    }
    throw new IllegalArgumentException("Unexpected normalization form: " + paramForm);
  }
  
  public static boolean isNormalized(String paramString, Normalizer.Form paramForm)
  {
    return isNormalized(paramString, paramForm, 0);
  }
  
  public static boolean isNormalized(String paramString, Normalizer.Form paramForm, int paramInt)
  {
    switch (paramForm)
    {
    case NFC: 
      return NFC.quickCheck(paramString.toCharArray(), 0, paramString.length(), false, NormalizerImpl.getNX(paramInt)) == YES;
    case NFD: 
      return NFD.quickCheck(paramString.toCharArray(), 0, paramString.length(), false, NormalizerImpl.getNX(paramInt)) == YES;
    case NFKC: 
      return NFKC.quickCheck(paramString.toCharArray(), 0, paramString.length(), false, NormalizerImpl.getNX(paramInt)) == YES;
    case NFKD: 
      return NFKD.quickCheck(paramString.toCharArray(), 0, paramString.length(), false, NormalizerImpl.getNX(paramInt)) == YES;
    }
    throw new IllegalArgumentException("Unexpected normalization form: " + paramForm);
  }
  
  private static abstract interface IsNextBoundary
  {
    public abstract boolean isNextBoundary(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, int[] paramArrayOfInt);
  }
  
  private static final class IsNextNFDSafe
    implements NormalizerBase.IsNextBoundary
  {
    private IsNextNFDSafe() {}
    
    public boolean isNextBoundary(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, int[] paramArrayOfInt)
    {
      return NormalizerImpl.isNFDSafe(NormalizerBase.getNextNorm32(paramUCharacterIterator, paramInt1, paramInt2, paramArrayOfInt), paramInt2, paramInt2 & 0x3F);
    }
  }
  
  private static final class IsNextTrueStarter
    implements NormalizerBase.IsNextBoundary
  {
    private IsNextTrueStarter() {}
    
    public boolean isNextBoundary(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, int[] paramArrayOfInt)
    {
      int i = paramInt2 << 2 & 0xF;
      long l = NormalizerBase.getNextNorm32(paramUCharacterIterator, paramInt1, paramInt2 | i, paramArrayOfInt);
      return NormalizerImpl.isTrueStarter(l, paramInt2, i);
    }
  }
  
  private static abstract interface IsPrevBoundary
  {
    public abstract boolean isPrevBoundary(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, char[] paramArrayOfChar);
  }
  
  private static final class IsPrevNFDSafe
    implements NormalizerBase.IsPrevBoundary
  {
    private IsPrevNFDSafe() {}
    
    public boolean isPrevBoundary(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, char[] paramArrayOfChar)
    {
      return NormalizerImpl.isNFDSafe(NormalizerBase.getPrevNorm32(paramUCharacterIterator, paramInt1, paramInt2, paramArrayOfChar), paramInt2, paramInt2 & 0x3F);
    }
  }
  
  private static final class IsPrevTrueStarter
    implements NormalizerBase.IsPrevBoundary
  {
    private IsPrevTrueStarter() {}
    
    public boolean isPrevBoundary(UCharacterIterator paramUCharacterIterator, int paramInt1, int paramInt2, char[] paramArrayOfChar)
    {
      int i = paramInt2 << 2 & 0xF;
      long l = NormalizerBase.getPrevNorm32(paramUCharacterIterator, paramInt1, paramInt2 | i, paramArrayOfChar);
      return NormalizerImpl.isTrueStarter(l, paramInt2, i);
    }
  }
  
  public static class Mode
  {
    private int modeValue;
    
    private Mode(int paramInt)
    {
      modeValue = paramInt;
    }
    
    protected int normalize(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, UnicodeSet paramUnicodeSet)
    {
      int i = paramInt2 - paramInt1;
      int j = paramInt4 - paramInt3;
      if (i > j) {
        return i;
      }
      System.arraycopy(paramArrayOfChar1, paramInt1, paramArrayOfChar2, paramInt3, i);
      return i;
    }
    
    protected int normalize(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, int paramInt5)
    {
      return normalize(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4, NormalizerImpl.getNX(paramInt5));
    }
    
    protected String normalize(String paramString, int paramInt)
    {
      return paramString;
    }
    
    protected int getMinC()
    {
      return -1;
    }
    
    protected int getMask()
    {
      return -1;
    }
    
    protected NormalizerBase.IsPrevBoundary getPrevBoundary()
    {
      return null;
    }
    
    protected NormalizerBase.IsNextBoundary getNextBoundary()
    {
      return null;
    }
    
    protected NormalizerBase.QuickCheckResult quickCheck(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, UnicodeSet paramUnicodeSet)
    {
      if (paramBoolean) {
        return NormalizerBase.MAYBE;
      }
      return NormalizerBase.NO;
    }
    
    protected boolean isNFSkippable(int paramInt)
    {
      return true;
    }
  }
  
  private static final class NFCMode
    extends NormalizerBase.Mode
  {
    private NFCMode(int paramInt)
    {
      super(null);
    }
    
    protected int normalize(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, UnicodeSet paramUnicodeSet)
    {
      return NormalizerImpl.compose(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4, 0, paramUnicodeSet);
    }
    
    protected String normalize(String paramString, int paramInt)
    {
      return NormalizerBase.compose(paramString, false, paramInt);
    }
    
    protected int getMinC()
    {
      return NormalizerImpl.getFromIndexesArr(6);
    }
    
    protected NormalizerBase.IsPrevBoundary getPrevBoundary()
    {
      return new NormalizerBase.IsPrevTrueStarter(null);
    }
    
    protected NormalizerBase.IsNextBoundary getNextBoundary()
    {
      return new NormalizerBase.IsNextTrueStarter(null);
    }
    
    protected int getMask()
    {
      return 65297;
    }
    
    protected NormalizerBase.QuickCheckResult quickCheck(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, UnicodeSet paramUnicodeSet)
    {
      return NormalizerImpl.quickCheck(paramArrayOfChar, paramInt1, paramInt2, NormalizerImpl.getFromIndexesArr(6), 17, 0, paramBoolean, paramUnicodeSet);
    }
    
    protected boolean isNFSkippable(int paramInt)
    {
      return NormalizerImpl.isNFSkippable(paramInt, this, 65473L);
    }
  }
  
  private static final class NFDMode
    extends NormalizerBase.Mode
  {
    private NFDMode(int paramInt)
    {
      super(null);
    }
    
    protected int normalize(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, UnicodeSet paramUnicodeSet)
    {
      int[] arrayOfInt = new int[1];
      return NormalizerImpl.decompose(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4, false, arrayOfInt, paramUnicodeSet);
    }
    
    protected String normalize(String paramString, int paramInt)
    {
      return NormalizerBase.decompose(paramString, false, paramInt);
    }
    
    protected int getMinC()
    {
      return 768;
    }
    
    protected NormalizerBase.IsPrevBoundary getPrevBoundary()
    {
      return new NormalizerBase.IsPrevNFDSafe(null);
    }
    
    protected NormalizerBase.IsNextBoundary getNextBoundary()
    {
      return new NormalizerBase.IsNextNFDSafe(null);
    }
    
    protected int getMask()
    {
      return 65284;
    }
    
    protected NormalizerBase.QuickCheckResult quickCheck(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, UnicodeSet paramUnicodeSet)
    {
      return NormalizerImpl.quickCheck(paramArrayOfChar, paramInt1, paramInt2, NormalizerImpl.getFromIndexesArr(8), 4, 0, paramBoolean, paramUnicodeSet);
    }
    
    protected boolean isNFSkippable(int paramInt)
    {
      return NormalizerImpl.isNFSkippable(paramInt, this, 65284L);
    }
  }
  
  private static final class NFKCMode
    extends NormalizerBase.Mode
  {
    private NFKCMode(int paramInt)
    {
      super(null);
    }
    
    protected int normalize(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, UnicodeSet paramUnicodeSet)
    {
      return NormalizerImpl.compose(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4, 4096, paramUnicodeSet);
    }
    
    protected String normalize(String paramString, int paramInt)
    {
      return NormalizerBase.compose(paramString, true, paramInt);
    }
    
    protected int getMinC()
    {
      return NormalizerImpl.getFromIndexesArr(7);
    }
    
    protected NormalizerBase.IsPrevBoundary getPrevBoundary()
    {
      return new NormalizerBase.IsPrevTrueStarter(null);
    }
    
    protected NormalizerBase.IsNextBoundary getNextBoundary()
    {
      return new NormalizerBase.IsNextTrueStarter(null);
    }
    
    protected int getMask()
    {
      return 65314;
    }
    
    protected NormalizerBase.QuickCheckResult quickCheck(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, UnicodeSet paramUnicodeSet)
    {
      return NormalizerImpl.quickCheck(paramArrayOfChar, paramInt1, paramInt2, NormalizerImpl.getFromIndexesArr(7), 34, 4096, paramBoolean, paramUnicodeSet);
    }
    
    protected boolean isNFSkippable(int paramInt)
    {
      return NormalizerImpl.isNFSkippable(paramInt, this, 65474L);
    }
  }
  
  private static final class NFKDMode
    extends NormalizerBase.Mode
  {
    private NFKDMode(int paramInt)
    {
      super(null);
    }
    
    protected int normalize(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2, int paramInt3, int paramInt4, UnicodeSet paramUnicodeSet)
    {
      int[] arrayOfInt = new int[1];
      return NormalizerImpl.decompose(paramArrayOfChar1, paramInt1, paramInt2, paramArrayOfChar2, paramInt3, paramInt4, true, arrayOfInt, paramUnicodeSet);
    }
    
    protected String normalize(String paramString, int paramInt)
    {
      return NormalizerBase.decompose(paramString, true, paramInt);
    }
    
    protected int getMinC()
    {
      return 768;
    }
    
    protected NormalizerBase.IsPrevBoundary getPrevBoundary()
    {
      return new NormalizerBase.IsPrevNFDSafe(null);
    }
    
    protected NormalizerBase.IsNextBoundary getNextBoundary()
    {
      return new NormalizerBase.IsNextNFDSafe(null);
    }
    
    protected int getMask()
    {
      return 65288;
    }
    
    protected NormalizerBase.QuickCheckResult quickCheck(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, UnicodeSet paramUnicodeSet)
    {
      return NormalizerImpl.quickCheck(paramArrayOfChar, paramInt1, paramInt2, NormalizerImpl.getFromIndexesArr(9), 8, 4096, paramBoolean, paramUnicodeSet);
    }
    
    protected boolean isNFSkippable(int paramInt)
    {
      return NormalizerImpl.isNFSkippable(paramInt, this, 65288L);
    }
  }
  
  public static final class QuickCheckResult
  {
    private int resultValue;
    
    private QuickCheckResult(int paramInt)
    {
      resultValue = paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\NormalizerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */