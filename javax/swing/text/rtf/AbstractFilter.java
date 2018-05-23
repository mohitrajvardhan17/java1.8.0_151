package javax.swing.text.rtf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

abstract class AbstractFilter
  extends OutputStream
{
  protected char[] translationTable = latin1TranslationTable;
  protected boolean[] specialsTable = noSpecialsTable;
  static final char[] latin1TranslationTable;
  static final boolean[] noSpecialsTable = new boolean['Ā'];
  static final boolean[] allSpecialsTable;
  
  public void readFromStream(InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte['䀀'];
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (i < 0) {
        break;
      }
      write(arrayOfByte, 0, i);
    }
  }
  
  public void readFromReader(Reader paramReader)
    throws IOException
  {
    char[] arrayOfChar = new char['ࠀ'];
    for (;;)
    {
      int i = paramReader.read(arrayOfChar);
      if (i < 0) {
        break;
      }
      for (int j = 0; j < i; j++) {
        write(arrayOfChar[j]);
      }
    }
  }
  
  public AbstractFilter() {}
  
  public void write(int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      paramInt += 256;
    }
    if (specialsTable[paramInt] != 0)
    {
      writeSpecial(paramInt);
    }
    else
    {
      char c = translationTable[paramInt];
      if (c != 0) {
        write(c);
      }
    }
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    StringBuilder localStringBuilder = null;
    while (paramInt2 > 0)
    {
      int i = (short)paramArrayOfByte[paramInt1];
      if (i < 0) {
        i = (short)(i + 256);
      }
      if (specialsTable[i] != 0)
      {
        if (localStringBuilder != null)
        {
          write(localStringBuilder.toString());
          localStringBuilder = null;
        }
        writeSpecial(i);
      }
      else
      {
        char c = translationTable[i];
        if (c != 0)
        {
          if (localStringBuilder == null) {
            localStringBuilder = new StringBuilder();
          }
          localStringBuilder.append(c);
        }
      }
      paramInt2--;
      paramInt1++;
    }
    if (localStringBuilder != null) {
      write(localStringBuilder.toString());
    }
  }
  
  public void write(String paramString)
    throws IOException
  {
    int j = paramString.length();
    for (int i = 0; i < j; i++) {
      write(paramString.charAt(i));
    }
  }
  
  protected abstract void write(char paramChar)
    throws IOException;
  
  protected abstract void writeSpecial(int paramInt)
    throws IOException;
  
  static
  {
    for (int i = 0; i < 256; i++) {
      noSpecialsTable[i] = false;
    }
    allSpecialsTable = new boolean['Ā'];
    for (i = 0; i < 256; i++) {
      allSpecialsTable[i] = true;
    }
    latin1TranslationTable = new char['Ā'];
    for (i = 0; i < 256; i++) {
      latin1TranslationTable[i] = ((char)i);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\rtf\AbstractFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */