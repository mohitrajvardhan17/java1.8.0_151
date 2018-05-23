package java.awt;

import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.CharacterIterator;

public abstract class FontMetrics
  implements Serializable
{
  private static final FontRenderContext DEFAULT_FRC = new FontRenderContext(null, false, false);
  protected Font font;
  private static final long serialVersionUID = 1681126225205050147L;
  
  protected FontMetrics(Font paramFont)
  {
    font = paramFont;
  }
  
  public Font getFont()
  {
    return font;
  }
  
  public FontRenderContext getFontRenderContext()
  {
    return DEFAULT_FRC;
  }
  
  public int getLeading()
  {
    return 0;
  }
  
  public int getAscent()
  {
    return font.getSize();
  }
  
  public int getDescent()
  {
    return 0;
  }
  
  public int getHeight()
  {
    return getLeading() + getAscent() + getDescent();
  }
  
  public int getMaxAscent()
  {
    return getAscent();
  }
  
  public int getMaxDescent()
  {
    return getDescent();
  }
  
  @Deprecated
  public int getMaxDecent()
  {
    return getMaxDescent();
  }
  
  public int getMaxAdvance()
  {
    return -1;
  }
  
  public int charWidth(int paramInt)
  {
    if (!Character.isValidCodePoint(paramInt)) {
      paramInt = 65535;
    }
    if (paramInt < 256) {
      return getWidths()[paramInt];
    }
    char[] arrayOfChar = new char[2];
    int i = Character.toChars(paramInt, arrayOfChar, 0);
    return charsWidth(arrayOfChar, 0, i);
  }
  
  public int charWidth(char paramChar)
  {
    if (paramChar < 'Ā') {
      return getWidths()[paramChar];
    }
    char[] arrayOfChar = { paramChar };
    return charsWidth(arrayOfChar, 0, 1);
  }
  
  public int stringWidth(String paramString)
  {
    int i = paramString.length();
    char[] arrayOfChar = new char[i];
    paramString.getChars(0, i, arrayOfChar, 0);
    return charsWidth(arrayOfChar, 0, i);
  }
  
  public int charsWidth(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    return stringWidth(new String(paramArrayOfChar, paramInt1, paramInt2));
  }
  
  public int bytesWidth(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return stringWidth(new String(paramArrayOfByte, 0, paramInt1, paramInt2));
  }
  
  public int[] getWidths()
  {
    int[] arrayOfInt = new int['Ā'];
    for (char c = '\000'; c < 'Ā'; c = (char)(c + '\001')) {
      arrayOfInt[c] = charWidth(c);
    }
    return arrayOfInt;
  }
  
  public boolean hasUniformLineMetrics()
  {
    return font.hasUniformLineMetrics();
  }
  
  public LineMetrics getLineMetrics(String paramString, Graphics paramGraphics)
  {
    return font.getLineMetrics(paramString, myFRC(paramGraphics));
  }
  
  public LineMetrics getLineMetrics(String paramString, int paramInt1, int paramInt2, Graphics paramGraphics)
  {
    return font.getLineMetrics(paramString, paramInt1, paramInt2, myFRC(paramGraphics));
  }
  
  public LineMetrics getLineMetrics(char[] paramArrayOfChar, int paramInt1, int paramInt2, Graphics paramGraphics)
  {
    return font.getLineMetrics(paramArrayOfChar, paramInt1, paramInt2, myFRC(paramGraphics));
  }
  
  public LineMetrics getLineMetrics(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2, Graphics paramGraphics)
  {
    return font.getLineMetrics(paramCharacterIterator, paramInt1, paramInt2, myFRC(paramGraphics));
  }
  
  public Rectangle2D getStringBounds(String paramString, Graphics paramGraphics)
  {
    return font.getStringBounds(paramString, myFRC(paramGraphics));
  }
  
  public Rectangle2D getStringBounds(String paramString, int paramInt1, int paramInt2, Graphics paramGraphics)
  {
    return font.getStringBounds(paramString, paramInt1, paramInt2, myFRC(paramGraphics));
  }
  
  public Rectangle2D getStringBounds(char[] paramArrayOfChar, int paramInt1, int paramInt2, Graphics paramGraphics)
  {
    return font.getStringBounds(paramArrayOfChar, paramInt1, paramInt2, myFRC(paramGraphics));
  }
  
  public Rectangle2D getStringBounds(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2, Graphics paramGraphics)
  {
    return font.getStringBounds(paramCharacterIterator, paramInt1, paramInt2, myFRC(paramGraphics));
  }
  
  public Rectangle2D getMaxCharBounds(Graphics paramGraphics)
  {
    return font.getMaxCharBounds(myFRC(paramGraphics));
  }
  
  private FontRenderContext myFRC(Graphics paramGraphics)
  {
    if ((paramGraphics instanceof Graphics2D)) {
      return ((Graphics2D)paramGraphics).getFontRenderContext();
    }
    return DEFAULT_FRC;
  }
  
  public String toString()
  {
    return getClass().getName() + "[font=" + getFont() + "ascent=" + getAscent() + ", descent=" + getDescent() + ", height=" + getHeight() + "]";
  }
  
  private static native void initIDs();
  
  static
  {
    
    if (!GraphicsEnvironment.isHeadless()) {
      initIDs();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\FontMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */