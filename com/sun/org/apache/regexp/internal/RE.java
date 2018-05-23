package com.sun.org.apache.regexp.internal;

import java.io.Serializable;
import java.util.Vector;

public class RE
  implements Serializable
{
  public static final int MATCH_NORMAL = 0;
  public static final int MATCH_CASEINDEPENDENT = 1;
  public static final int MATCH_MULTILINE = 2;
  public static final int MATCH_SINGLELINE = 4;
  static final char OP_END = 'E';
  static final char OP_BOL = '^';
  static final char OP_EOL = '$';
  static final char OP_ANY = '.';
  static final char OP_ANYOF = '[';
  static final char OP_BRANCH = '|';
  static final char OP_ATOM = 'A';
  static final char OP_STAR = '*';
  static final char OP_PLUS = '+';
  static final char OP_MAYBE = '?';
  static final char OP_ESCAPE = '\\';
  static final char OP_OPEN = '(';
  static final char OP_OPEN_CLUSTER = '<';
  static final char OP_CLOSE = ')';
  static final char OP_CLOSE_CLUSTER = '>';
  static final char OP_BACKREF = '#';
  static final char OP_GOTO = 'G';
  static final char OP_NOTHING = 'N';
  static final char OP_RELUCTANTSTAR = '8';
  static final char OP_RELUCTANTPLUS = '=';
  static final char OP_RELUCTANTMAYBE = '/';
  static final char OP_POSIXCLASS = 'P';
  static final char E_ALNUM = 'w';
  static final char E_NALNUM = 'W';
  static final char E_BOUND = 'b';
  static final char E_NBOUND = 'B';
  static final char E_SPACE = 's';
  static final char E_NSPACE = 'S';
  static final char E_DIGIT = 'd';
  static final char E_NDIGIT = 'D';
  static final char POSIX_CLASS_ALNUM = 'w';
  static final char POSIX_CLASS_ALPHA = 'a';
  static final char POSIX_CLASS_BLANK = 'b';
  static final char POSIX_CLASS_CNTRL = 'c';
  static final char POSIX_CLASS_DIGIT = 'd';
  static final char POSIX_CLASS_GRAPH = 'g';
  static final char POSIX_CLASS_LOWER = 'l';
  static final char POSIX_CLASS_PRINT = 'p';
  static final char POSIX_CLASS_PUNCT = '!';
  static final char POSIX_CLASS_SPACE = 's';
  static final char POSIX_CLASS_UPPER = 'u';
  static final char POSIX_CLASS_XDIGIT = 'x';
  static final char POSIX_CLASS_JSTART = 'j';
  static final char POSIX_CLASS_JPART = 'k';
  static final int maxNode = 65536;
  static final int MAX_PAREN = 16;
  static final int offsetOpcode = 0;
  static final int offsetOpdata = 1;
  static final int offsetNext = 2;
  static final int nodeSize = 3;
  REProgram program;
  transient CharacterIterator search;
  int matchFlags;
  int maxParen = 16;
  transient int parenCount;
  transient int start0;
  transient int end0;
  transient int start1;
  transient int end1;
  transient int start2;
  transient int end2;
  transient int[] startn;
  transient int[] endn;
  transient int[] startBackref;
  transient int[] endBackref;
  public static final int REPLACE_ALL = 0;
  public static final int REPLACE_FIRSTONLY = 1;
  public static final int REPLACE_BACKREFERENCES = 2;
  
  public RE(String paramString)
    throws RESyntaxException
  {
    this(paramString, 0);
  }
  
  public RE(String paramString, int paramInt)
    throws RESyntaxException
  {
    this(new RECompiler().compile(paramString));
    setMatchFlags(paramInt);
  }
  
  public RE(REProgram paramREProgram, int paramInt)
  {
    setProgram(paramREProgram);
    setMatchFlags(paramInt);
  }
  
  public RE(REProgram paramREProgram)
  {
    this(paramREProgram, 0);
  }
  
  public RE()
  {
    this((REProgram)null, 0);
  }
  
  public static String simplePatternToFullRegularExpression(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      switch (c)
      {
      case '*': 
        localStringBuffer.append(".*");
        break;
      case '$': 
      case '(': 
      case ')': 
      case '+': 
      case '.': 
      case '?': 
      case '[': 
      case '\\': 
      case ']': 
      case '^': 
      case '{': 
      case '|': 
      case '}': 
        localStringBuffer.append('\\');
      default: 
        localStringBuffer.append(c);
      }
    }
    return localStringBuffer.toString();
  }
  
  public void setMatchFlags(int paramInt)
  {
    matchFlags = paramInt;
  }
  
  public int getMatchFlags()
  {
    return matchFlags;
  }
  
  public void setProgram(REProgram paramREProgram)
  {
    program = paramREProgram;
    if ((paramREProgram != null) && (maxParens != -1)) {
      maxParen = maxParens;
    } else {
      maxParen = 16;
    }
  }
  
  public REProgram getProgram()
  {
    return program;
  }
  
  public int getParenCount()
  {
    return parenCount;
  }
  
  public String getParen(int paramInt)
  {
    int i;
    if ((paramInt < parenCount) && ((i = getParenStart(paramInt)) >= 0)) {
      return search.substring(i, getParenEnd(paramInt));
    }
    return null;
  }
  
  public final int getParenStart(int paramInt)
  {
    if (paramInt < parenCount)
    {
      switch (paramInt)
      {
      case 0: 
        return start0;
      case 1: 
        return start1;
      case 2: 
        return start2;
      }
      if (startn == null) {
        allocParens();
      }
      return startn[paramInt];
    }
    return -1;
  }
  
  public final int getParenEnd(int paramInt)
  {
    if (paramInt < parenCount)
    {
      switch (paramInt)
      {
      case 0: 
        return end0;
      case 1: 
        return end1;
      case 2: 
        return end2;
      }
      if (endn == null) {
        allocParens();
      }
      return endn[paramInt];
    }
    return -1;
  }
  
  public final int getParenLength(int paramInt)
  {
    if (paramInt < parenCount) {
      return getParenEnd(paramInt) - getParenStart(paramInt);
    }
    return -1;
  }
  
  protected final void setParenStart(int paramInt1, int paramInt2)
  {
    if (paramInt1 < parenCount) {
      switch (paramInt1)
      {
      case 0: 
        start0 = paramInt2;
        break;
      case 1: 
        start1 = paramInt2;
        break;
      case 2: 
        start2 = paramInt2;
        break;
      default: 
        if (startn == null) {
          allocParens();
        }
        startn[paramInt1] = paramInt2;
      }
    }
  }
  
  protected final void setParenEnd(int paramInt1, int paramInt2)
  {
    if (paramInt1 < parenCount) {
      switch (paramInt1)
      {
      case 0: 
        end0 = paramInt2;
        break;
      case 1: 
        end1 = paramInt2;
        break;
      case 2: 
        end2 = paramInt2;
        break;
      default: 
        if (endn == null) {
          allocParens();
        }
        endn[paramInt1] = paramInt2;
      }
    }
  }
  
  protected void internalError(String paramString)
    throws Error
  {
    throw new Error("RE internal error: " + paramString);
  }
  
  private final void allocParens()
  {
    startn = new int[maxParen];
    endn = new int[maxParen];
    for (int i = 0; i < maxParen; i++)
    {
      startn[i] = -1;
      endn[i] = -1;
    }
  }
  
  protected int matchNodes(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt3;
    char[] arrayOfChar = program.instruction;
    int i1 = paramInt1;
    while (i1 < paramInt2)
    {
      int k = arrayOfChar[(i1 + 0)];
      int j = i1 + (short)arrayOfChar[(i1 + 2)];
      int m = arrayOfChar[(i1 + 1)];
      int i2;
      int n;
      int i10;
      int i3;
      int i7;
      int i4;
      switch (k)
      {
      case 47: 
        i2 = 0;
        do
        {
          if ((n = matchNodes(j, 65536, i)) != -1) {
            return n;
          }
        } while ((i2++ == 0) && ((i = matchNodes(i1 + 3, j, i)) != -1));
        return -1;
      case 61: 
        while ((i = matchNodes(i1 + 3, j, i)) != -1) {
          if ((n = matchNodes(j, 65536, i)) != -1) {
            return n;
          }
        }
        return -1;
      case 56: 
        do
        {
          if ((n = matchNodes(j, 65536, i)) != -1) {
            return n;
          }
        } while ((i = matchNodes(i1 + 3, j, i)) != -1);
        return -1;
      case 40: 
        if ((program.flags & 0x1) != 0) {
          startBackref[m] = i;
        }
        if ((n = matchNodes(j, 65536, i)) != -1)
        {
          if (m + 1 > parenCount) {
            parenCount = (m + 1);
          }
          if (getParenStart(m) == -1) {
            setParenStart(m, i);
          }
        }
        return n;
      case 41: 
        if ((program.flags & 0x1) != 0) {
          endBackref[m] = i;
        }
        if ((n = matchNodes(j, 65536, i)) != -1)
        {
          if (m + 1 > parenCount) {
            parenCount = (m + 1);
          }
          if (getParenEnd(m) == -1) {
            setParenEnd(m, i);
          }
        }
        return n;
      case 60: 
      case 62: 
        return matchNodes(j, 65536, i);
      case 35: 
        i2 = startBackref[m];
        int i6 = endBackref[m];
        if ((i2 == -1) || (i6 == -1)) {
          return -1;
        }
        if (i2 != i6)
        {
          int i8 = i6 - i2;
          if (search.isEnd(i + i8 - 1)) {
            return -1;
          }
          i10 = (matchFlags & 0x1) != 0 ? 1 : 0;
          for (int i12 = 0; i12 < i8; i12++) {
            if (compareChars(search.charAt(i++), search.charAt(i2 + i12), i10) != 0) {
              return -1;
            }
          }
        }
        break;
      case 94: 
        if (i != 0) {
          if ((matchFlags & 0x2) == 2)
          {
            if ((i <= 0) || (!isNewline(i - 1))) {
              return -1;
            }
          }
          else {
            return -1;
          }
        }
      case 36: 
        if ((!search.isEnd(0)) && (!search.isEnd(i))) {
          if ((matchFlags & 0x2) == 2)
          {
            if (!isNewline(i)) {
              return -1;
            }
          }
          else {
            return -1;
          }
        }
      case 92: 
        switch (m)
        {
        case 66: 
        case 98: 
          i2 = i == 0 ? '\n' : search.charAt(i - 1);
          char c1 = search.isEnd(i) ? '\n' : search.charAt(i);
          if ((Character.isLetterOrDigit(i2) == Character.isLetterOrDigit(c1) ? 1 : 0) == (m == 98 ? 1 : 0)) {
            return -1;
          }
          break;
        case 68: 
        case 83: 
        case 87: 
        case 100: 
        case 115: 
        case 119: 
          if (search.isEnd(i)) {
            return -1;
          }
          i3 = search.charAt(i);
          switch (m)
          {
          case 87: 
          case 119: 
            if (((Character.isLetterOrDigit(i3)) || (i3 == 95) ? 1 : 0) != (m == 119 ? 1 : 0)) {
              return -1;
            }
            break;
          case 68: 
          case 100: 
            if (Character.isDigit(i3) != (m == 100)) {
              return -1;
            }
            break;
          case 83: 
          case 115: 
            if (Character.isWhitespace(i3) != (m == 115)) {
              return -1;
            }
            break;
          }
          i++;
          break;
        default: 
          internalError("Unrecognized escape '" + m + "'");
        }
        break;
      case 46: 
        if ((matchFlags & 0x4) == 4)
        {
          if (search.isEnd(i)) {
            return -1;
          }
        }
        else if ((search.isEnd(i)) || (isNewline(i))) {
          return -1;
        }
        i++;
        break;
      case 65: 
        if (search.isEnd(i)) {
          return -1;
        }
        i3 = m;
        i7 = i1 + 3;
        if (search.isEnd(i3 + i - 1)) {
          return -1;
        }
        boolean bool = (matchFlags & 0x1) != 0;
        for (i10 = 0; i10 < i3; i10++) {
          if (compareChars(search.charAt(i++), arrayOfChar[(i7 + i10)], bool) != 0) {
            return -1;
          }
        }
        break;
      case 80: 
        if (search.isEnd(i)) {
          return -1;
        }
        switch (m)
        {
        case 119: 
          if (!Character.isLetterOrDigit(search.charAt(i))) {
            return -1;
          }
          break;
        case 97: 
          if (!Character.isLetter(search.charAt(i))) {
            return -1;
          }
          break;
        case 100: 
          if (!Character.isDigit(search.charAt(i))) {
            return -1;
          }
          break;
        case 98: 
          if (!Character.isSpaceChar(search.charAt(i))) {
            return -1;
          }
          break;
        case 115: 
          if (!Character.isWhitespace(search.charAt(i))) {
            return -1;
          }
          break;
        case 99: 
          if (Character.getType(search.charAt(i)) != 15) {
            return -1;
          }
          break;
        case 103: 
          switch (Character.getType(search.charAt(i)))
          {
          case 25: 
          case 26: 
          case 27: 
          case 28: 
            break;
          default: 
            return -1;
          }
          break;
        case 108: 
          if (Character.getType(search.charAt(i)) != 2) {
            return -1;
          }
          break;
        case 117: 
          if (Character.getType(search.charAt(i)) != 1) {
            return -1;
          }
          break;
        case 112: 
          if (Character.getType(search.charAt(i)) == 15) {
            return -1;
          }
          break;
        case 33: 
          i4 = Character.getType(search.charAt(i));
          switch (i4)
          {
          case 20: 
          case 21: 
          case 22: 
          case 23: 
          case 24: 
            break;
          default: 
            return -1;
          }
          break;
        case 120: 
          i4 = ((search.charAt(i) >= '0') && (search.charAt(i) <= '9')) || ((search.charAt(i) >= 'a') && (search.charAt(i) <= 'f')) || ((search.charAt(i) >= 'A') && (search.charAt(i) <= 'F')) ? 1 : 0;
          if (i4 == 0) {
            return -1;
          }
          break;
        case 106: 
          if (!Character.isJavaIdentifierStart(search.charAt(i))) {
            return -1;
          }
          break;
        case 107: 
          if (!Character.isJavaIdentifierPart(search.charAt(i))) {
            return -1;
          }
          break;
        default: 
          internalError("Bad posix class");
        }
        i++;
        break;
      case 91: 
        if (search.isEnd(i)) {
          return -1;
        }
        i4 = search.charAt(i);
        i7 = (matchFlags & 0x1) != 0 ? 1 : 0;
        int i9 = i1 + 3;
        int i11 = i9 + m * 2;
        int i13 = 0;
        int i14 = i9;
        while ((i13 == 0) && (i14 < i11))
        {
          char c2 = arrayOfChar[(i14++)];
          char c3 = arrayOfChar[(i14++)];
          i13 = (compareChars(i4, c2, i7) >= 0) && (compareChars(i4, c3, i7) <= 0) ? 1 : 0;
        }
        if (i13 == 0) {
          return -1;
        }
        i++;
        break;
      case 124: 
        if (arrayOfChar[(j + 0)] != '|')
        {
          i1 += 3;
        }
        else
        {
          int i5;
          do
          {
            if ((n = matchNodes(i1 + 3, 65536, i)) != -1) {
              return n;
            }
            i5 = (short)arrayOfChar[(i1 + 2)];
            i1 += i5;
          } while ((i5 != 0) && (arrayOfChar[(i1 + 0)] == '|'));
          return -1;
        }
        break;
      case 71: 
      case 78: 
        break;
      case 69: 
        setParenEnd(0, i);
        return i;
      default: 
        internalError("Invalid opcode '" + k + "'");
        i1 = j;
      }
    }
    internalError("Corrupt program");
    return -1;
  }
  
  protected boolean matchAt(int paramInt)
  {
    start0 = -1;
    end0 = -1;
    start1 = -1;
    end1 = -1;
    start2 = -1;
    end2 = -1;
    startn = null;
    endn = null;
    parenCount = 1;
    setParenStart(0, paramInt);
    if ((program.flags & 0x1) != 0)
    {
      startBackref = new int[maxParen];
      endBackref = new int[maxParen];
    }
    int i;
    if ((i = matchNodes(0, 65536, paramInt)) != -1)
    {
      setParenEnd(0, i);
      return true;
    }
    parenCount = 0;
    return false;
  }
  
  public boolean match(String paramString, int paramInt)
  {
    return match(new StringCharacterIterator(paramString), paramInt);
  }
  
  public boolean match(CharacterIterator paramCharacterIterator, int paramInt)
  {
    if (program == null) {
      internalError("No RE program to run!");
    }
    search = paramCharacterIterator;
    if (program.prefix == null)
    {
      while (!paramCharacterIterator.isEnd(paramInt - 1))
      {
        if (matchAt(paramInt)) {
          return true;
        }
        paramInt++;
      }
      return false;
    }
    boolean bool = (matchFlags & 0x1) != 0;
    char[] arrayOfChar = program.prefix;
    while (!paramCharacterIterator.isEnd(paramInt + arrayOfChar.length - 1))
    {
      int i = paramInt;
      int j = 0;
      int k;
      do
      {
        k = compareChars(paramCharacterIterator.charAt(i++), arrayOfChar[(j++)], bool) == 0 ? 1 : 0;
      } while ((k != 0) && (j < arrayOfChar.length));
      if ((j == arrayOfChar.length) && (matchAt(paramInt))) {
        return true;
      }
      paramInt++;
    }
    return false;
  }
  
  public boolean match(String paramString)
  {
    return match(paramString, 0);
  }
  
  public String[] split(String paramString)
  {
    Vector localVector = new Vector();
    int i = 0;
    int j = paramString.length();
    while ((i < j) && (match(paramString, i)))
    {
      int k = getParenStart(0);
      int m = getParenEnd(0);
      if (m == i)
      {
        localVector.addElement(paramString.substring(i, k + 1));
        m++;
      }
      else
      {
        localVector.addElement(paramString.substring(i, k));
      }
      i = m;
    }
    String str = paramString.substring(i);
    if (str.length() != 0) {
      localVector.addElement(str);
    }
    String[] arrayOfString = new String[localVector.size()];
    localVector.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  public String subst(String paramString1, String paramString2)
  {
    return subst(paramString1, paramString2, 0);
  }
  
  public String subst(String paramString1, String paramString2, int paramInt)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    int j = paramString1.length();
    while ((i < j) && (match(paramString1, i)))
    {
      localStringBuffer.append(paramString1.substring(i, getParenStart(0)));
      if ((paramInt & 0x2) != 0)
      {
        k = 0;
        int m = -2;
        int n = paramString2.length();
        int i1 = 0;
        while ((k = paramString2.indexOf("$", k)) >= 0)
        {
          if (((k == 0) || (paramString2.charAt(k - 1) != '\\')) && (k + 1 < n))
          {
            int i2 = paramString2.charAt(k + 1);
            if ((i2 >= 48) && (i2 <= 57))
            {
              if (i1 == 0)
              {
                localStringBuffer.append(paramString2.substring(0, k));
                i1 = 1;
              }
              else
              {
                localStringBuffer.append(paramString2.substring(m + 2, k));
              }
              localStringBuffer.append(getParen(i2 - 48));
              m = k;
            }
          }
          k++;
        }
        localStringBuffer.append(paramString2.substring(m + 2, n));
      }
      else
      {
        localStringBuffer.append(paramString2);
      }
      int k = getParenEnd(0);
      if (k == i) {
        k++;
      }
      i = k;
      if ((paramInt & 0x1) != 0) {
        break;
      }
    }
    if (i < j) {
      localStringBuffer.append(paramString1.substring(i));
    }
    return localStringBuffer.toString();
  }
  
  public String[] grep(Object[] paramArrayOfObject)
  {
    Vector localVector = new Vector();
    for (int i = 0; i < paramArrayOfObject.length; i++)
    {
      String str = paramArrayOfObject[i].toString();
      if (match(str)) {
        localVector.addElement(str);
      }
    }
    String[] arrayOfString = new String[localVector.size()];
    localVector.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  private boolean isNewline(int paramInt)
  {
    int i = search.charAt(paramInt);
    return (i == 10) || (i == 13) || (i == 133) || (i == 8232) || (i == 8233);
  }
  
  private int compareChars(char paramChar1, char paramChar2, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramChar1 = Character.toLowerCase(paramChar1);
      paramChar2 = Character.toLowerCase(paramChar2);
    }
    return paramChar1 - paramChar2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\regexp\internal\RE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */