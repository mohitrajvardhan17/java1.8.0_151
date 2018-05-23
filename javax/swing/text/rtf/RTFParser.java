package javax.swing.text.rtf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

abstract class RTFParser
  extends AbstractFilter
{
  public int level = 0;
  private int state = 0;
  private StringBuffer currentCharacters = new StringBuffer();
  private String pendingKeyword = null;
  private int pendingCharacter;
  private long binaryBytesLeft;
  ByteArrayOutputStream binaryBuf;
  private boolean[] savedSpecials;
  protected PrintStream warnings;
  private final int S_text = 0;
  private final int S_backslashed = 1;
  private final int S_token = 2;
  private final int S_parameter = 3;
  private final int S_aftertick = 4;
  private final int S_aftertickc = 5;
  private final int S_inblob = 6;
  static final boolean[] rtfSpecialsTable = (boolean[])noSpecialsTable.clone();
  
  public abstract boolean handleKeyword(String paramString);
  
  public abstract boolean handleKeyword(String paramString, int paramInt);
  
  public abstract void handleText(String paramString);
  
  public void handleText(char paramChar)
  {
    handleText(String.valueOf(paramChar));
  }
  
  public abstract void handleBinaryBlob(byte[] paramArrayOfByte);
  
  public abstract void begingroup();
  
  public abstract void endgroup();
  
  public RTFParser()
  {
    specialsTable = rtfSpecialsTable;
  }
  
  public void writeSpecial(int paramInt)
    throws IOException
  {
    write((char)paramInt);
  }
  
  protected void warning(String paramString)
  {
    if (warnings != null) {
      warnings.println(paramString);
    }
  }
  
  public void write(String paramString)
    throws IOException
  {
    if (state != 0)
    {
      int i = 0;
      int j = paramString.length();
      while ((i < j) && (state != 0))
      {
        write(paramString.charAt(i));
        i++;
      }
      if (i >= j) {
        return;
      }
      paramString = paramString.substring(i);
    }
    if (currentCharacters.length() > 0) {
      currentCharacters.append(paramString);
    } else {
      handleText(paramString);
    }
  }
  
  public void write(char paramChar)
    throws IOException
  {
    boolean bool;
    switch (state)
    {
    case 0: 
      if ((paramChar != '\n') && (paramChar != '\r')) {
        if (paramChar == '{')
        {
          if (currentCharacters.length() > 0)
          {
            handleText(currentCharacters.toString());
            currentCharacters = new StringBuffer();
          }
          level += 1;
          begingroup();
        }
        else if (paramChar == '}')
        {
          if (currentCharacters.length() > 0)
          {
            handleText(currentCharacters.toString());
            currentCharacters = new StringBuffer();
          }
          if (level == 0) {
            throw new IOException("Too many close-groups in RTF text");
          }
          endgroup();
          level -= 1;
        }
        else if (paramChar == '\\')
        {
          if (currentCharacters.length() > 0)
          {
            handleText(currentCharacters.toString());
            currentCharacters = new StringBuffer();
          }
          state = 1;
        }
        else
        {
          currentCharacters.append(paramChar);
        }
      }
      break;
    case 1: 
      if (paramChar == '\'')
      {
        state = 4;
      }
      else if (!Character.isLetter(paramChar))
      {
        char[] arrayOfChar = new char[1];
        arrayOfChar[0] = paramChar;
        if (!handleKeyword(new String(arrayOfChar))) {
          warning("Unknown keyword: " + arrayOfChar + " (" + (byte)paramChar + ")");
        }
        state = 0;
        pendingKeyword = null;
      }
      else
      {
        state = 2;
      }
      break;
    case 2: 
      if (Character.isLetter(paramChar))
      {
        currentCharacters.append(paramChar);
      }
      else
      {
        pendingKeyword = currentCharacters.toString();
        currentCharacters = new StringBuffer();
        if ((Character.isDigit(paramChar)) || (paramChar == '-'))
        {
          state = 3;
          currentCharacters.append(paramChar);
        }
        else
        {
          bool = handleKeyword(pendingKeyword);
          if (!bool) {
            warning("Unknown keyword: " + pendingKeyword);
          }
          pendingKeyword = null;
          state = 0;
          if (!Character.isWhitespace(paramChar)) {
            write(paramChar);
          }
        }
      }
      break;
    case 3: 
      if (Character.isDigit(paramChar))
      {
        currentCharacters.append(paramChar);
      }
      else if (pendingKeyword.equals("bin"))
      {
        long l = Long.parseLong(currentCharacters.toString());
        pendingKeyword = null;
        state = 6;
        binaryBytesLeft = l;
        if (binaryBytesLeft > 2147483647L) {
          binaryBuf = new ByteArrayOutputStream(Integer.MAX_VALUE);
        } else {
          binaryBuf = new ByteArrayOutputStream((int)binaryBytesLeft);
        }
        savedSpecials = specialsTable;
        specialsTable = allSpecialsTable;
      }
      else
      {
        int i = Integer.parseInt(currentCharacters.toString());
        bool = handleKeyword(pendingKeyword, i);
        if (!bool) {
          warning("Unknown keyword: " + pendingKeyword + " (param " + currentCharacters + ")");
        }
        pendingKeyword = null;
        currentCharacters = new StringBuffer();
        state = 0;
        if (!Character.isWhitespace(paramChar)) {
          write(paramChar);
        }
      }
      break;
    case 4: 
      if (Character.digit(paramChar, 16) == -1)
      {
        state = 0;
      }
      else
      {
        pendingCharacter = Character.digit(paramChar, 16);
        state = 5;
      }
      break;
    case 5: 
      state = 0;
      if (Character.digit(paramChar, 16) != -1)
      {
        pendingCharacter = (pendingCharacter * 16 + Character.digit(paramChar, 16));
        paramChar = translationTable[pendingCharacter];
        if (paramChar != 0) {
          handleText(paramChar);
        }
      }
      break;
    case 6: 
      binaryBuf.write(paramChar);
      binaryBytesLeft -= 1L;
      if (binaryBytesLeft == 0L)
      {
        state = 0;
        specialsTable = savedSpecials;
        savedSpecials = null;
        handleBinaryBlob(binaryBuf.toByteArray());
        binaryBuf = null;
      }
      break;
    }
  }
  
  public void flush()
    throws IOException
  {
    super.flush();
    if ((state == 0) && (currentCharacters.length() > 0))
    {
      handleText(currentCharacters.toString());
      currentCharacters = new StringBuffer();
    }
  }
  
  public void close()
    throws IOException
  {
    flush();
    if ((state != 0) || (level > 0))
    {
      warning("Truncated RTF file.");
      while (level > 0)
      {
        endgroup();
        level -= 1;
      }
    }
    super.close();
  }
  
  static
  {
    rtfSpecialsTable[10] = true;
    rtfSpecialsTable[13] = true;
    rtfSpecialsTable[123] = true;
    rtfSpecialsTable[125] = true;
    rtfSpecialsTable[92] = true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\rtf\RTFParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */