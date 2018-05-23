package javax.swing.text;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;

public abstract class AbstractWriter
{
  private ElementIterator it;
  private Writer out;
  private int indentLevel = 0;
  private int indentSpace = 2;
  private Document doc = null;
  private int maxLineLength = 100;
  private int currLength = 0;
  private int startOffset = 0;
  private int endOffset = 0;
  private int offsetIndent = 0;
  private String lineSeparator;
  private boolean canWrapLines;
  private boolean isLineEmpty;
  private char[] indentChars;
  private char[] tempChars;
  private char[] newlineChars;
  private Segment segment;
  protected static final char NEWLINE = '\n';
  
  protected AbstractWriter(Writer paramWriter, Document paramDocument)
  {
    this(paramWriter, paramDocument, 0, paramDocument.getLength());
  }
  
  protected AbstractWriter(Writer paramWriter, Document paramDocument, int paramInt1, int paramInt2)
  {
    doc = paramDocument;
    it = new ElementIterator(paramDocument.getDefaultRootElement());
    out = paramWriter;
    startOffset = paramInt1;
    endOffset = (paramInt1 + paramInt2);
    Object localObject = paramDocument.getProperty("__EndOfLine__");
    if ((localObject instanceof String))
    {
      setLineSeparator((String)localObject);
    }
    else
    {
      String str = null;
      try
      {
        str = System.getProperty("line.separator");
      }
      catch (SecurityException localSecurityException) {}
      if (str == null) {
        str = "\n";
      }
      setLineSeparator(str);
    }
    canWrapLines = true;
  }
  
  protected AbstractWriter(Writer paramWriter, Element paramElement)
  {
    this(paramWriter, paramElement, 0, paramElement.getEndOffset());
  }
  
  protected AbstractWriter(Writer paramWriter, Element paramElement, int paramInt1, int paramInt2)
  {
    doc = paramElement.getDocument();
    it = new ElementIterator(paramElement);
    out = paramWriter;
    startOffset = paramInt1;
    endOffset = (paramInt1 + paramInt2);
    canWrapLines = true;
  }
  
  public int getStartOffset()
  {
    return startOffset;
  }
  
  public int getEndOffset()
  {
    return endOffset;
  }
  
  protected ElementIterator getElementIterator()
  {
    return it;
  }
  
  protected Writer getWriter()
  {
    return out;
  }
  
  protected Document getDocument()
  {
    return doc;
  }
  
  protected boolean inRange(Element paramElement)
  {
    int i = getStartOffset();
    int j = getEndOffset();
    return ((paramElement.getStartOffset() >= i) && (paramElement.getStartOffset() < j)) || ((i >= paramElement.getStartOffset()) && (i < paramElement.getEndOffset()));
  }
  
  protected abstract void write()
    throws IOException, BadLocationException;
  
  protected String getText(Element paramElement)
    throws BadLocationException
  {
    return doc.getText(paramElement.getStartOffset(), paramElement.getEndOffset() - paramElement.getStartOffset());
  }
  
  protected void text(Element paramElement)
    throws BadLocationException, IOException
  {
    int i = Math.max(getStartOffset(), paramElement.getStartOffset());
    int j = Math.min(getEndOffset(), paramElement.getEndOffset());
    if (i < j)
    {
      if (segment == null) {
        segment = new Segment();
      }
      getDocument().getText(i, j - i, segment);
      if (segment.count > 0) {
        write(segment.array, segment.offset, segment.count);
      }
    }
  }
  
  protected void setLineLength(int paramInt)
  {
    maxLineLength = paramInt;
  }
  
  protected int getLineLength()
  {
    return maxLineLength;
  }
  
  protected void setCurrentLineLength(int paramInt)
  {
    currLength = paramInt;
    isLineEmpty = (currLength == 0);
  }
  
  protected int getCurrentLineLength()
  {
    return currLength;
  }
  
  protected boolean isLineEmpty()
  {
    return isLineEmpty;
  }
  
  protected void setCanWrapLines(boolean paramBoolean)
  {
    canWrapLines = paramBoolean;
  }
  
  protected boolean getCanWrapLines()
  {
    return canWrapLines;
  }
  
  protected void setIndentSpace(int paramInt)
  {
    indentSpace = paramInt;
  }
  
  protected int getIndentSpace()
  {
    return indentSpace;
  }
  
  public void setLineSeparator(String paramString)
  {
    lineSeparator = paramString;
  }
  
  public String getLineSeparator()
  {
    return lineSeparator;
  }
  
  protected void incrIndent()
  {
    if (offsetIndent > 0)
    {
      offsetIndent += 1;
    }
    else if (++indentLevel * getIndentSpace() >= getLineLength())
    {
      offsetIndent += 1;
      indentLevel -= 1;
    }
  }
  
  protected void decrIndent()
  {
    if (offsetIndent > 0) {
      offsetIndent -= 1;
    } else {
      indentLevel -= 1;
    }
  }
  
  protected int getIndentLevel()
  {
    return indentLevel;
  }
  
  protected void indent()
    throws IOException
  {
    int i = getIndentLevel() * getIndentSpace();
    if ((indentChars == null) || (i > indentChars.length))
    {
      indentChars = new char[i];
      for (j = 0; j < i; j++) {
        indentChars[j] = ' ';
      }
    }
    int j = getCurrentLineLength();
    boolean bool = isLineEmpty();
    output(indentChars, 0, i);
    if ((bool) && (j == 0)) {
      isLineEmpty = true;
    }
  }
  
  protected void write(char paramChar)
    throws IOException
  {
    if (tempChars == null) {
      tempChars = new char[''];
    }
    tempChars[0] = paramChar;
    write(tempChars, 0, 1);
  }
  
  protected void write(String paramString)
    throws IOException
  {
    if (paramString == null) {
      return;
    }
    int i = paramString.length();
    if ((tempChars == null) || (tempChars.length < i)) {
      tempChars = new char[i];
    }
    paramString.getChars(0, i, tempChars, 0);
    write(tempChars, 0, i);
  }
  
  protected void writeLineSeparator()
    throws IOException
  {
    String str = getLineSeparator();
    int i = str.length();
    if ((newlineChars == null) || (newlineChars.length < i)) {
      newlineChars = new char[i];
    }
    str.getChars(0, i, newlineChars, 0);
    output(newlineChars, 0, i);
    setCurrentLineLength(0);
  }
  
  protected void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i;
    int j;
    int k;
    if (!getCanWrapLines())
    {
      i = paramInt1;
      j = paramInt1 + paramInt2;
      for (k = indexOf(paramArrayOfChar, '\n', paramInt1, j); k != -1; k = indexOf(paramArrayOfChar, '\n', i, j))
      {
        if (k > i) {
          output(paramArrayOfChar, i, k - i);
        }
        writeLineSeparator();
        i = k + 1;
      }
      if (i < j) {
        output(paramArrayOfChar, i, j - i);
      }
    }
    else
    {
      i = paramInt1;
      j = paramInt1 + paramInt2;
      k = getCurrentLineLength();
      int m = getLineLength();
      while (i < j)
      {
        int n = indexOf(paramArrayOfChar, '\n', i, j);
        int i1 = 0;
        int i2 = 0;
        k = getCurrentLineLength();
        if ((n != -1) && (k + (n - i) < m))
        {
          if (n > i) {
            output(paramArrayOfChar, i, n - i);
          }
          i = n + 1;
          i2 = 1;
        }
        else if ((n == -1) && (k + (j - i) < m))
        {
          if (j > i) {
            output(paramArrayOfChar, i, j - i);
          }
          i = j;
        }
        else
        {
          int i3 = -1;
          int i4 = Math.min(j - i, m - k - 1);
          for (int i5 = 0; i5 < i4; i5++) {
            if (Character.isWhitespace(paramArrayOfChar[(i5 + i)])) {
              i3 = i5;
            }
          }
          if (i3 != -1)
          {
            i3 += i + 1;
            output(paramArrayOfChar, i, i3 - i);
            i = i3;
            i1 = 1;
          }
          else
          {
            i5 = Math.max(0, i4);
            i4 = j - i;
            while (i5 < i4)
            {
              if (Character.isWhitespace(paramArrayOfChar[(i5 + i)]))
              {
                i3 = i5;
                break;
              }
              i5++;
            }
            if (i3 == -1)
            {
              output(paramArrayOfChar, i, j - i);
              i3 = j;
            }
            else
            {
              i3 += i;
              if (paramArrayOfChar[i3] == '\n')
              {
                output(paramArrayOfChar, i, i3++ - i);
                i2 = 1;
              }
              else
              {
                output(paramArrayOfChar, i, ++i3 - i);
                i1 = 1;
              }
            }
            i = i3;
          }
        }
        if ((i2 != 0) || (i1 != 0) || (i < j))
        {
          writeLineSeparator();
          if ((i < j) || (i2 == 0)) {
            indent();
          }
        }
      }
    }
  }
  
  protected void writeAttributes(AttributeSet paramAttributeSet)
    throws IOException
  {
    Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject = localEnumeration.nextElement();
      write(" " + localObject + "=" + paramAttributeSet.getAttribute(localObject));
    }
  }
  
  protected void output(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    getWriter().write(paramArrayOfChar, paramInt1, paramInt2);
    setCurrentLineLength(getCurrentLineLength() + paramInt2);
  }
  
  private int indexOf(char[] paramArrayOfChar, char paramChar, int paramInt1, int paramInt2)
  {
    while (paramInt1 < paramInt2)
    {
      if (paramArrayOfChar[paramInt1] == paramChar) {
        return paramInt1;
      }
      paramInt1++;
    }
    return -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\AbstractWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */