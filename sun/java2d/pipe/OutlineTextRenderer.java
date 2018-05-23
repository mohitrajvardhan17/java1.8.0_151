package sun.java2d.pipe;

import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.FontInfo;

public class OutlineTextRenderer
  implements TextPipe
{
  public static final int THRESHHOLD = 100;
  
  public OutlineTextRenderer() {}
  
  public void drawChars(SunGraphics2D paramSunGraphics2D, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    String str = new String(paramArrayOfChar, paramInt1, paramInt2);
    drawString(paramSunGraphics2D, str, paramInt3, paramInt4);
  }
  
  public void drawString(SunGraphics2D paramSunGraphics2D, String paramString, double paramDouble1, double paramDouble2)
  {
    if ("".equals(paramString)) {
      return;
    }
    TextLayout localTextLayout = new TextLayout(paramString, paramSunGraphics2D.getFont(), paramSunGraphics2D.getFontRenderContext());
    Shape localShape = localTextLayout.getOutline(AffineTransform.getTranslateInstance(paramDouble1, paramDouble2));
    int i = getFontInfoaaHint;
    int j = -1;
    if ((i != 1) && (antialiasHint != 2))
    {
      j = antialiasHint;
      antialiasHint = 2;
      paramSunGraphics2D.validatePipe();
    }
    else if ((i == 1) && (antialiasHint != 1))
    {
      j = antialiasHint;
      antialiasHint = 1;
      paramSunGraphics2D.validatePipe();
    }
    paramSunGraphics2D.fill(localShape);
    if (j != -1)
    {
      antialiasHint = j;
      paramSunGraphics2D.validatePipe();
    }
  }
  
  public void drawGlyphVector(SunGraphics2D paramSunGraphics2D, GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2)
  {
    Shape localShape = paramGlyphVector.getOutline(paramFloat1, paramFloat2);
    int i = -1;
    FontRenderContext localFontRenderContext = paramGlyphVector.getFontRenderContext();
    boolean bool = localFontRenderContext.isAntiAliased();
    if ((bool) && (getGVFontInfogetFontaaHint == 1)) {
      bool = false;
    }
    if ((bool) && (antialiasHint != 2))
    {
      i = antialiasHint;
      antialiasHint = 2;
      paramSunGraphics2D.validatePipe();
    }
    else if ((!bool) && (antialiasHint != 1))
    {
      i = antialiasHint;
      antialiasHint = 1;
      paramSunGraphics2D.validatePipe();
    }
    paramSunGraphics2D.fill(localShape);
    if (i != -1)
    {
      antialiasHint = i;
      paramSunGraphics2D.validatePipe();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\OutlineTextRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */