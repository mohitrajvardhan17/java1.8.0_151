package com.sun.jmx.snmp.IPAcl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

final class ASCII_CharStream
{
  public static final boolean staticFlag = false;
  int bufsize;
  int available;
  int tokenBegin;
  public int bufpos = -1;
  private int[] bufline;
  private int[] bufcolumn;
  private int column = 0;
  private int line = 1;
  private boolean prevCharIsCR = false;
  private boolean prevCharIsLF = false;
  private Reader inputStream;
  private char[] buffer;
  private int maxNextCharInd = 0;
  private int inBuf = 0;
  
  private final void ExpandBuff(boolean paramBoolean)
  {
    char[] arrayOfChar = new char[bufsize + 2048];
    int[] arrayOfInt1 = new int[bufsize + 2048];
    int[] arrayOfInt2 = new int[bufsize + 2048];
    try
    {
      if (paramBoolean)
      {
        System.arraycopy(buffer, tokenBegin, arrayOfChar, 0, bufsize - tokenBegin);
        System.arraycopy(buffer, 0, arrayOfChar, bufsize - tokenBegin, bufpos);
        buffer = arrayOfChar;
        System.arraycopy(bufline, tokenBegin, arrayOfInt1, 0, bufsize - tokenBegin);
        System.arraycopy(bufline, 0, arrayOfInt1, bufsize - tokenBegin, bufpos);
        bufline = arrayOfInt1;
        System.arraycopy(bufcolumn, tokenBegin, arrayOfInt2, 0, bufsize - tokenBegin);
        System.arraycopy(bufcolumn, 0, arrayOfInt2, bufsize - tokenBegin, bufpos);
        bufcolumn = arrayOfInt2;
        maxNextCharInd = (bufpos += bufsize - tokenBegin);
      }
      else
      {
        System.arraycopy(buffer, tokenBegin, arrayOfChar, 0, bufsize - tokenBegin);
        buffer = arrayOfChar;
        System.arraycopy(bufline, tokenBegin, arrayOfInt1, 0, bufsize - tokenBegin);
        bufline = arrayOfInt1;
        System.arraycopy(bufcolumn, tokenBegin, arrayOfInt2, 0, bufsize - tokenBegin);
        bufcolumn = arrayOfInt2;
        maxNextCharInd = (bufpos -= tokenBegin);
      }
    }
    catch (Throwable localThrowable)
    {
      throw new Error(localThrowable.getMessage());
    }
    bufsize += 2048;
    available = bufsize;
    tokenBegin = 0;
  }
  
  private final void FillBuff()
    throws IOException
  {
    if (maxNextCharInd == available) {
      if (available == bufsize)
      {
        if (tokenBegin > 2048)
        {
          bufpos = (maxNextCharInd = 0);
          available = tokenBegin;
        }
        else if (tokenBegin < 0)
        {
          bufpos = (maxNextCharInd = 0);
        }
        else
        {
          ExpandBuff(false);
        }
      }
      else if (available > tokenBegin) {
        available = bufsize;
      } else if (tokenBegin - available < 2048) {
        ExpandBuff(true);
      } else {
        available = tokenBegin;
      }
    }
    try
    {
      int i;
      if ((i = inputStream.read(buffer, maxNextCharInd, available - maxNextCharInd)) == -1)
      {
        inputStream.close();
        throw new IOException();
      }
      maxNextCharInd += i;
      return;
    }
    catch (IOException localIOException)
    {
      bufpos -= 1;
      backup(0);
      if (tokenBegin == -1) {
        tokenBegin = bufpos;
      }
      throw localIOException;
    }
  }
  
  public final char BeginToken()
    throws IOException
  {
    tokenBegin = -1;
    char c = readChar();
    tokenBegin = bufpos;
    return c;
  }
  
  private final void UpdateLineColumn(char paramChar)
  {
    column += 1;
    if (prevCharIsLF)
    {
      prevCharIsLF = false;
      line += (column = 1);
    }
    else if (prevCharIsCR)
    {
      prevCharIsCR = false;
      if (paramChar == '\n') {
        prevCharIsLF = true;
      } else {
        line += (column = 1);
      }
    }
    switch (paramChar)
    {
    case '\r': 
      prevCharIsCR = true;
      break;
    case '\n': 
      prevCharIsLF = true;
      break;
    case '\t': 
      column -= 1;
      column += 8 - (column & 0x7);
      break;
    }
    bufline[bufpos] = line;
    bufcolumn[bufpos] = column;
  }
  
  public final char readChar()
    throws IOException
  {
    if (inBuf > 0)
    {
      inBuf -= 1;
      return (char)(0xFF & buffer[(++bufpos)]);
    }
    if (++bufpos >= maxNextCharInd) {
      FillBuff();
    }
    char c = (char)(0xFF & buffer[bufpos]);
    UpdateLineColumn(c);
    return c;
  }
  
  @Deprecated
  public final int getColumn()
  {
    return bufcolumn[bufpos];
  }
  
  @Deprecated
  public final int getLine()
  {
    return bufline[bufpos];
  }
  
  public final int getEndColumn()
  {
    return bufcolumn[bufpos];
  }
  
  public final int getEndLine()
  {
    return bufline[bufpos];
  }
  
  public final int getBeginColumn()
  {
    return bufcolumn[tokenBegin];
  }
  
  public final int getBeginLine()
  {
    return bufline[tokenBegin];
  }
  
  public final void backup(int paramInt)
  {
    inBuf += paramInt;
    if (bufpos -= paramInt < 0) {
      bufpos += bufsize;
    }
  }
  
  public ASCII_CharStream(Reader paramReader, int paramInt1, int paramInt2, int paramInt3)
  {
    inputStream = paramReader;
    line = paramInt1;
    column = (paramInt2 - 1);
    available = (bufsize = paramInt3);
    buffer = new char[paramInt3];
    bufline = new int[paramInt3];
    bufcolumn = new int[paramInt3];
  }
  
  public ASCII_CharStream(Reader paramReader, int paramInt1, int paramInt2)
  {
    this(paramReader, paramInt1, paramInt2, 4096);
  }
  
  public void ReInit(Reader paramReader, int paramInt1, int paramInt2, int paramInt3)
  {
    inputStream = paramReader;
    line = paramInt1;
    column = (paramInt2 - 1);
    if ((buffer == null) || (paramInt3 != buffer.length))
    {
      available = (bufsize = paramInt3);
      buffer = new char[paramInt3];
      bufline = new int[paramInt3];
      bufcolumn = new int[paramInt3];
    }
    prevCharIsLF = (prevCharIsCR = 0);
    tokenBegin = (inBuf = maxNextCharInd = 0);
    bufpos = -1;
  }
  
  public void ReInit(Reader paramReader, int paramInt1, int paramInt2)
  {
    ReInit(paramReader, paramInt1, paramInt2, 4096);
  }
  
  public ASCII_CharStream(InputStream paramInputStream, int paramInt1, int paramInt2, int paramInt3)
  {
    this(new InputStreamReader(paramInputStream), paramInt1, paramInt2, 4096);
  }
  
  public ASCII_CharStream(InputStream paramInputStream, int paramInt1, int paramInt2)
  {
    this(paramInputStream, paramInt1, paramInt2, 4096);
  }
  
  public void ReInit(InputStream paramInputStream, int paramInt1, int paramInt2, int paramInt3)
  {
    ReInit(new InputStreamReader(paramInputStream), paramInt1, paramInt2, 4096);
  }
  
  public void ReInit(InputStream paramInputStream, int paramInt1, int paramInt2)
  {
    ReInit(paramInputStream, paramInt1, paramInt2, 4096);
  }
  
  public final String GetImage()
  {
    if (bufpos >= tokenBegin) {
      return new String(buffer, tokenBegin, bufpos - tokenBegin + 1);
    }
    return new String(buffer, tokenBegin, bufsize - tokenBegin) + new String(buffer, 0, bufpos + 1);
  }
  
  public final char[] GetSuffix(int paramInt)
  {
    char[] arrayOfChar = new char[paramInt];
    if (bufpos + 1 >= paramInt)
    {
      System.arraycopy(buffer, bufpos - paramInt + 1, arrayOfChar, 0, paramInt);
    }
    else
    {
      System.arraycopy(buffer, bufsize - (paramInt - bufpos - 1), arrayOfChar, 0, paramInt - bufpos - 1);
      System.arraycopy(buffer, 0, arrayOfChar, paramInt - bufpos - 1, bufpos + 1);
    }
    return arrayOfChar;
  }
  
  public void Done()
  {
    buffer = null;
    bufline = null;
    bufcolumn = null;
  }
  
  public void adjustBeginLineColumn(int paramInt1, int paramInt2)
  {
    int i = tokenBegin;
    int j;
    if (bufpos >= tokenBegin) {
      j = bufpos - tokenBegin + inBuf + 1;
    } else {
      j = bufsize - tokenBegin + bufpos + 1 + inBuf;
    }
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    while ((k < j) && (bufline[(m = i % bufsize)] == bufline[(n = ++i % bufsize)]))
    {
      bufline[m] = paramInt1;
      i1 = i2 + bufcolumn[n] - bufcolumn[m];
      bufcolumn[m] = (paramInt2 + i2);
      i2 = i1;
      k++;
    }
    if (k < j)
    {
      bufline[m] = (paramInt1++);
      bufcolumn[m] = (paramInt2 + i2);
      while (k++ < j) {
        if (bufline[(m = i % bufsize)] != bufline[(++i % bufsize)]) {
          bufline[m] = (paramInt1++);
        } else {
          bufline[m] = paramInt1;
        }
      }
    }
    line = bufline[m];
    column = bufcolumn[m];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\ASCII_CharStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */