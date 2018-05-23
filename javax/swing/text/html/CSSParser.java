package javax.swing.text.html;

import java.io.IOException;
import java.io.Reader;

class CSSParser
{
  private static final int IDENTIFIER = 1;
  private static final int BRACKET_OPEN = 2;
  private static final int BRACKET_CLOSE = 3;
  private static final int BRACE_OPEN = 4;
  private static final int BRACE_CLOSE = 5;
  private static final int PAREN_OPEN = 6;
  private static final int PAREN_CLOSE = 7;
  private static final int END = -1;
  private static final char[] charMapping = { '\000', '\000', '[', ']', '{', '}', '(', ')', '\000' };
  private boolean didPushChar;
  private int pushedChar;
  private StringBuffer unitBuffer = new StringBuffer();
  private int[] unitStack = new int[2];
  private int stackCount;
  private Reader reader;
  private boolean encounteredRuleSet;
  private CSSParserCallback callback;
  private char[] tokenBuffer = new char[80];
  private int tokenBufferLength;
  private boolean readWS;
  
  CSSParser() {}
  
  void parse(Reader paramReader, CSSParserCallback paramCSSParserCallback, boolean paramBoolean)
    throws IOException
  {
    callback = paramCSSParserCallback;
    stackCount = (tokenBufferLength = 0);
    reader = paramReader;
    encounteredRuleSet = false;
    try
    {
      if (paramBoolean) {
        parseDeclarationBlock();
      } else {
        while (getNextStatement()) {}
      }
    }
    finally
    {
      paramCSSParserCallback = null;
      paramReader = null;
    }
  }
  
  private boolean getNextStatement()
    throws IOException
  {
    unitBuffer.setLength(0);
    int i = nextToken('\000');
    switch (i)
    {
    case 1: 
      if (tokenBufferLength > 0) {
        if (tokenBuffer[0] == '@')
        {
          parseAtRule();
        }
        else
        {
          encounteredRuleSet = true;
          parseRuleSet();
        }
      }
      return true;
    case 2: 
    case 4: 
    case 6: 
      parseTillClosed(i);
      return true;
    case 3: 
    case 5: 
    case 7: 
      throw new RuntimeException("Unexpected top level block close");
    case -1: 
      return false;
    }
    return true;
  }
  
  private void parseAtRule()
    throws IOException
  {
    int i = 0;
    int j = (tokenBufferLength == 7) && (tokenBuffer[0] == '@') && (tokenBuffer[1] == 'i') && (tokenBuffer[2] == 'm') && (tokenBuffer[3] == 'p') && (tokenBuffer[4] == 'o') && (tokenBuffer[5] == 'r') && (tokenBuffer[6] == 't') ? 1 : 0;
    unitBuffer.setLength(0);
    while (i == 0)
    {
      int k = nextToken(';');
      switch (k)
      {
      case 1: 
        if ((tokenBufferLength > 0) && (tokenBuffer[(tokenBufferLength - 1)] == ';'))
        {
          tokenBufferLength -= 1;
          i = 1;
        }
        if (tokenBufferLength > 0)
        {
          if ((unitBuffer.length() > 0) && (readWS)) {
            unitBuffer.append(' ');
          }
          unitBuffer.append(tokenBuffer, 0, tokenBufferLength);
        }
        break;
      case 4: 
        if ((unitBuffer.length() > 0) && (readWS)) {
          unitBuffer.append(' ');
        }
        unitBuffer.append(charMapping[k]);
        parseTillClosed(k);
        i = 1;
        int m = readWS();
        if ((m != -1) && (m != 59)) {
          pushChar(m);
        }
        break;
      case 2: 
      case 6: 
        unitBuffer.append(charMapping[k]);
        parseTillClosed(k);
        break;
      case 3: 
      case 5: 
      case 7: 
        throw new RuntimeException("Unexpected close in @ rule");
      case -1: 
        i = 1;
      }
    }
    if ((j != 0) && (!encounteredRuleSet)) {
      callback.handleImport(unitBuffer.toString());
    }
  }
  
  private void parseRuleSet()
    throws IOException
  {
    if (parseSelectors())
    {
      callback.startRule();
      parseDeclarationBlock();
      callback.endRule();
    }
  }
  
  private boolean parseSelectors()
    throws IOException
  {
    if (tokenBufferLength > 0) {
      callback.handleSelector(new String(tokenBuffer, 0, tokenBufferLength));
    }
    unitBuffer.setLength(0);
    for (;;)
    {
      int i;
      if ((i = nextToken('\000')) == 1)
      {
        if (tokenBufferLength > 0) {
          callback.handleSelector(new String(tokenBuffer, 0, tokenBufferLength));
        }
      }
      else {
        switch (i)
        {
        case 4: 
          return true;
        case 2: 
        case 6: 
          parseTillClosed(i);
          unitBuffer.setLength(0);
          break;
        case 3: 
        case 5: 
        case 7: 
          throw new RuntimeException("Unexpected block close in selector");
        case -1: 
          return false;
        }
      }
    }
  }
  
  private void parseDeclarationBlock()
    throws IOException
  {
    for (;;)
    {
      int i = parseDeclaration();
      switch (i)
      {
      case -1: 
      case 5: 
        return;
      case 3: 
      case 7: 
        throw new RuntimeException("Unexpected close in declaration block");
      }
    }
  }
  
  private int parseDeclaration()
    throws IOException
  {
    if ((i = parseIdentifiers(':', false)) != 1) {
      return i;
    }
    for (int j = unitBuffer.length() - 1; j >= 0; j--) {
      unitBuffer.setCharAt(j, Character.toLowerCase(unitBuffer.charAt(j)));
    }
    callback.handleProperty(unitBuffer.toString());
    int i = parseIdentifiers(';', true);
    callback.handleValue(unitBuffer.toString());
    return i;
  }
  
  private int parseIdentifiers(char paramChar, boolean paramBoolean)
    throws IOException
  {
    unitBuffer.setLength(0);
    for (;;)
    {
      int i = nextToken(paramChar);
      switch (i)
      {
      case 1: 
        if (tokenBufferLength > 0)
        {
          if (tokenBuffer[(tokenBufferLength - 1)] == paramChar)
          {
            if (--tokenBufferLength > 0)
            {
              if ((readWS) && (unitBuffer.length() > 0)) {
                unitBuffer.append(' ');
              }
              unitBuffer.append(tokenBuffer, 0, tokenBufferLength);
            }
            return 1;
          }
          if ((readWS) && (unitBuffer.length() > 0)) {
            unitBuffer.append(' ');
          }
          unitBuffer.append(tokenBuffer, 0, tokenBufferLength);
        }
        break;
      case 2: 
      case 4: 
      case 6: 
        int j = unitBuffer.length();
        if (paramBoolean) {
          unitBuffer.append(charMapping[i]);
        }
        parseTillClosed(i);
        if (!paramBoolean) {
          unitBuffer.setLength(j);
        }
        break;
      case -1: 
      case 3: 
      case 5: 
      case 7: 
        return i;
      }
    }
  }
  
  private void parseTillClosed(int paramInt)
    throws IOException
  {
    int j = 0;
    startBlock(paramInt);
    while (j == 0)
    {
      int i = nextToken('\000');
      switch (i)
      {
      case 1: 
        if ((unitBuffer.length() > 0) && (readWS)) {
          unitBuffer.append(' ');
        }
        if (tokenBufferLength > 0) {
          unitBuffer.append(tokenBuffer, 0, tokenBufferLength);
        }
        break;
      case 2: 
      case 4: 
      case 6: 
        if ((unitBuffer.length() > 0) && (readWS)) {
          unitBuffer.append(' ');
        }
        unitBuffer.append(charMapping[i]);
        startBlock(i);
        break;
      case 3: 
      case 5: 
      case 7: 
        if ((unitBuffer.length() > 0) && (readWS)) {
          unitBuffer.append(' ');
        }
        unitBuffer.append(charMapping[i]);
        endBlock(i);
        if (!inBlock()) {
          j = 1;
        }
        break;
      case -1: 
        throw new RuntimeException("Unclosed block");
      }
    }
  }
  
  private int nextToken(char paramChar)
    throws IOException
  {
    readWS = false;
    int i = readWS();
    switch (i)
    {
    case 39: 
      readTill('\'');
      if (tokenBufferLength > 0) {
        tokenBufferLength -= 1;
      }
      return 1;
    case 34: 
      readTill('"');
      if (tokenBufferLength > 0) {
        tokenBufferLength -= 1;
      }
      return 1;
    case 91: 
      return 2;
    case 93: 
      return 3;
    case 123: 
      return 4;
    case 125: 
      return 5;
    case 40: 
      return 6;
    case 41: 
      return 7;
    case -1: 
      return -1;
    }
    pushChar(i);
    getIdentifier(paramChar);
    return 1;
  }
  
  private boolean getIdentifier(char paramChar)
    throws IOException
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    int i1 = paramChar;
    int i3 = 0;
    tokenBufferLength = 0;
    while (j == 0)
    {
      int n = readChar();
      int i2;
      switch (n)
      {
      case 92: 
        i2 = 1;
        break;
      case 48: 
      case 49: 
      case 50: 
      case 51: 
      case 52: 
      case 53: 
      case 54: 
      case 55: 
      case 56: 
      case 57: 
        i2 = 2;
        i3 = n - 48;
        break;
      case 97: 
      case 98: 
      case 99: 
      case 100: 
      case 101: 
      case 102: 
        i2 = 2;
        i3 = n - 97 + 10;
        break;
      case 65: 
      case 66: 
      case 67: 
      case 68: 
      case 69: 
      case 70: 
        i2 = 2;
        i3 = n - 65 + 10;
        break;
      case 9: 
      case 10: 
      case 13: 
      case 32: 
      case 34: 
      case 39: 
      case 40: 
      case 41: 
      case 91: 
      case 93: 
      case 123: 
      case 125: 
        i2 = 3;
        break;
      case 47: 
        i2 = 4;
        break;
      case -1: 
        j = 1;
        i2 = 0;
        break;
      case 0: 
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
      case 6: 
      case 7: 
      case 8: 
      case 11: 
      case 12: 
      case 14: 
      case 15: 
      case 16: 
      case 17: 
      case 18: 
      case 19: 
      case 20: 
      case 21: 
      case 22: 
      case 23: 
      case 24: 
      case 25: 
      case 26: 
      case 27: 
      case 28: 
      case 29: 
      case 30: 
      case 31: 
      case 33: 
      case 35: 
      case 36: 
      case 37: 
      case 38: 
      case 42: 
      case 43: 
      case 44: 
      case 45: 
      case 46: 
      case 58: 
      case 59: 
      case 60: 
      case 61: 
      case 62: 
      case 63: 
      case 64: 
      case 71: 
      case 72: 
      case 73: 
      case 74: 
      case 75: 
      case 76: 
      case 77: 
      case 78: 
      case 79: 
      case 80: 
      case 81: 
      case 82: 
      case 83: 
      case 84: 
      case 85: 
      case 86: 
      case 87: 
      case 88: 
      case 89: 
      case 90: 
      case 94: 
      case 95: 
      case 96: 
      case 103: 
      case 104: 
      case 105: 
      case 106: 
      case 107: 
      case 108: 
      case 109: 
      case 110: 
      case 111: 
      case 112: 
      case 113: 
      case 114: 
      case 115: 
      case 116: 
      case 117: 
      case 118: 
      case 119: 
      case 120: 
      case 121: 
      case 122: 
      case 124: 
      default: 
        i2 = 0;
      }
      if (i != 0)
      {
        if (i2 == 2)
        {
          m = m * 16 + i3;
          k++;
          if (k == 4)
          {
            i = 0;
            append((char)m);
          }
        }
        else
        {
          i = 0;
          if (k > 0)
          {
            append((char)m);
            pushChar(n);
          }
          else if (j == 0)
          {
            append((char)n);
          }
        }
      }
      else if (j == 0) {
        if (i2 == 1)
        {
          i = 1;
          m = k = 0;
        }
        else if (i2 == 3)
        {
          j = 1;
          pushChar(n);
        }
        else if (i2 == 4)
        {
          n = readChar();
          if (n == 42)
          {
            j = 1;
            readComment();
            readWS = true;
          }
          else
          {
            append('/');
            if (n == -1) {
              j = 1;
            } else {
              pushChar(n);
            }
          }
        }
        else
        {
          append((char)n);
          if (n == i1) {
            j = 1;
          }
        }
      }
    }
    return tokenBufferLength > 0;
  }
  
  private void readTill(char paramChar)
    throws IOException
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int n = 0;
    int i1 = paramChar;
    int i3 = 0;
    tokenBufferLength = 0;
    while (n == 0)
    {
      int m = readChar();
      int i2;
      switch (m)
      {
      case 92: 
        i2 = 1;
        break;
      case 48: 
      case 49: 
      case 50: 
      case 51: 
      case 52: 
      case 53: 
      case 54: 
      case 55: 
      case 56: 
      case 57: 
        i2 = 2;
        i3 = m - 48;
        break;
      case 97: 
      case 98: 
      case 99: 
      case 100: 
      case 101: 
      case 102: 
        i2 = 2;
        i3 = m - 97 + 10;
        break;
      case 65: 
      case 66: 
      case 67: 
      case 68: 
      case 69: 
      case 70: 
        i2 = 2;
        i3 = m - 65 + 10;
        break;
      case -1: 
        throw new RuntimeException("Unclosed " + paramChar);
      case 0: 
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
      case 6: 
      case 7: 
      case 8: 
      case 9: 
      case 10: 
      case 11: 
      case 12: 
      case 13: 
      case 14: 
      case 15: 
      case 16: 
      case 17: 
      case 18: 
      case 19: 
      case 20: 
      case 21: 
      case 22: 
      case 23: 
      case 24: 
      case 25: 
      case 26: 
      case 27: 
      case 28: 
      case 29: 
      case 30: 
      case 31: 
      case 32: 
      case 33: 
      case 34: 
      case 35: 
      case 36: 
      case 37: 
      case 38: 
      case 39: 
      case 40: 
      case 41: 
      case 42: 
      case 43: 
      case 44: 
      case 45: 
      case 46: 
      case 47: 
      case 58: 
      case 59: 
      case 60: 
      case 61: 
      case 62: 
      case 63: 
      case 64: 
      case 71: 
      case 72: 
      case 73: 
      case 74: 
      case 75: 
      case 76: 
      case 77: 
      case 78: 
      case 79: 
      case 80: 
      case 81: 
      case 82: 
      case 83: 
      case 84: 
      case 85: 
      case 86: 
      case 87: 
      case 88: 
      case 89: 
      case 90: 
      case 91: 
      case 93: 
      case 94: 
      case 95: 
      case 96: 
      default: 
        i2 = 0;
      }
      if (i != 0)
      {
        if (i2 == 2)
        {
          k = k * 16 + i3;
          j++;
          if (j == 4)
          {
            i = 0;
            append((char)k);
          }
        }
        else if (j > 0)
        {
          append((char)k);
          if (i2 == 1)
          {
            i = 1;
            k = j = 0;
          }
          else
          {
            if (m == i1) {
              n = 1;
            }
            append((char)m);
            i = 0;
          }
        }
        else
        {
          append((char)m);
          i = 0;
        }
      }
      else if (i2 == 1)
      {
        i = 1;
        k = j = 0;
      }
      else
      {
        if (m == i1) {
          n = 1;
        }
        append((char)m);
      }
    }
  }
  
  private void append(char paramChar)
  {
    if (tokenBufferLength == tokenBuffer.length)
    {
      char[] arrayOfChar = new char[tokenBuffer.length * 2];
      System.arraycopy(tokenBuffer, 0, arrayOfChar, 0, tokenBuffer.length);
      tokenBuffer = arrayOfChar;
    }
    tokenBuffer[(tokenBufferLength++)] = paramChar;
  }
  
  private void readComment()
    throws IOException
  {
    for (;;)
    {
      int i = readChar();
      switch (i)
      {
      case -1: 
        throw new RuntimeException("Unclosed comment");
      case 42: 
        i = readChar();
        if (i == 47) {
          return;
        }
        if (i == -1) {
          throw new RuntimeException("Unclosed comment");
        }
        pushChar(i);
      }
    }
  }
  
  private void startBlock(int paramInt)
  {
    if (stackCount == unitStack.length)
    {
      int[] arrayOfInt = new int[stackCount * 2];
      System.arraycopy(unitStack, 0, arrayOfInt, 0, stackCount);
      unitStack = arrayOfInt;
    }
    unitStack[(stackCount++)] = paramInt;
  }
  
  private void endBlock(int paramInt)
  {
    int i;
    switch (paramInt)
    {
    case 3: 
      i = 2;
      break;
    case 5: 
      i = 4;
      break;
    case 7: 
      i = 6;
      break;
    case 4: 
    case 6: 
    default: 
      i = -1;
    }
    if ((stackCount > 0) && (unitStack[(stackCount - 1)] == i)) {
      stackCount -= 1;
    } else {
      throw new RuntimeException("Unmatched block");
    }
  }
  
  private boolean inBlock()
  {
    return stackCount > 0;
  }
  
  private int readWS()
    throws IOException
  {
    int i;
    while (((i = readChar()) != -1) && (Character.isWhitespace((char)i))) {
      readWS = true;
    }
    return i;
  }
  
  private int readChar()
    throws IOException
  {
    if (didPushChar)
    {
      didPushChar = false;
      return pushedChar;
    }
    return reader.read();
  }
  
  private void pushChar(int paramInt)
  {
    if (didPushChar) {
      throw new RuntimeException("Can not handle look ahead of more than one character");
    }
    didPushChar = true;
    pushedChar = paramInt;
  }
  
  static abstract interface CSSParserCallback
  {
    public abstract void handleImport(String paramString);
    
    public abstract void handleSelector(String paramString);
    
    public abstract void startRule();
    
    public abstract void handleProperty(String paramString);
    
    public abstract void handleValue(String paramString);
    
    public abstract void endRule();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\CSSParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */