package javax.swing.text.html.parser;

import java.io.PrintWriter;

class NPrintWriter
  extends PrintWriter
{
  private int numLines = 5;
  private int numPrinted = 0;
  
  public NPrintWriter(int paramInt)
  {
    super(System.out);
    numLines = paramInt;
  }
  
  public void println(char[] paramArrayOfChar)
  {
    if (numPrinted >= numLines) {
      return;
    }
    Object localObject = null;
    for (int i = 0; i < paramArrayOfChar.length; i++)
    {
      if (paramArrayOfChar[i] == '\n') {
        numPrinted += 1;
      }
      if (numPrinted == numLines) {
        System.arraycopy(paramArrayOfChar, 0, localObject, 0, i);
      }
    }
    if (localObject != null) {
      super.print((char[])localObject);
    }
    if (numPrinted == numLines) {
      return;
    }
    super.println(paramArrayOfChar);
    numPrinted += 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\parser\NPrintWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */