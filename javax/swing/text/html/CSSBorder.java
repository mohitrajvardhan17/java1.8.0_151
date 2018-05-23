package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.AbstractBorder;
import javax.swing.text.AttributeSet;

class CSSBorder
  extends AbstractBorder
{
  static final int COLOR = 0;
  static final int STYLE = 1;
  static final int WIDTH = 2;
  static final int TOP = 0;
  static final int RIGHT = 1;
  static final int BOTTOM = 2;
  static final int LEFT = 3;
  static final CSS.Attribute[][] ATTRIBUTES = { { CSS.Attribute.BORDER_TOP_COLOR, CSS.Attribute.BORDER_RIGHT_COLOR, CSS.Attribute.BORDER_BOTTOM_COLOR, CSS.Attribute.BORDER_LEFT_COLOR }, { CSS.Attribute.BORDER_TOP_STYLE, CSS.Attribute.BORDER_RIGHT_STYLE, CSS.Attribute.BORDER_BOTTOM_STYLE, CSS.Attribute.BORDER_LEFT_STYLE }, { CSS.Attribute.BORDER_TOP_WIDTH, CSS.Attribute.BORDER_RIGHT_WIDTH, CSS.Attribute.BORDER_BOTTOM_WIDTH, CSS.Attribute.BORDER_LEFT_WIDTH } };
  static final CSS.CssValue[] PARSERS = { new CSS.ColorValue(), new CSS.BorderStyle(), new CSS.BorderWidthValue(null, 0) };
  static final Object[] DEFAULTS = { CSS.Attribute.BORDER_COLOR, PARSERS[1].parseCssValue(CSS.Attribute.BORDER_STYLE.getDefaultValue()), PARSERS[2].parseCssValue(CSS.Attribute.BORDER_WIDTH.getDefaultValue()) };
  final AttributeSet attrs;
  static Map<CSS.Value, BorderPainter> borderPainters = new HashMap();
  
  CSSBorder(AttributeSet paramAttributeSet)
  {
    attrs = paramAttributeSet;
  }
  
  private Color getBorderColor(int paramInt)
  {
    Object localObject = attrs.getAttribute(ATTRIBUTES[0][paramInt]);
    CSS.ColorValue localColorValue;
    if ((localObject instanceof CSS.ColorValue))
    {
      localColorValue = (CSS.ColorValue)localObject;
    }
    else
    {
      localColorValue = (CSS.ColorValue)attrs.getAttribute(CSS.Attribute.COLOR);
      if (localColorValue == null) {
        localColorValue = (CSS.ColorValue)PARSERS[0].parseCssValue(CSS.Attribute.COLOR.getDefaultValue());
      }
    }
    return localColorValue.getValue();
  }
  
  private int getBorderWidth(int paramInt)
  {
    int i = 0;
    CSS.BorderStyle localBorderStyle = (CSS.BorderStyle)attrs.getAttribute(ATTRIBUTES[1][paramInt]);
    if ((localBorderStyle != null) && (localBorderStyle.getValue() != CSS.Value.NONE))
    {
      CSS.LengthValue localLengthValue = (CSS.LengthValue)attrs.getAttribute(ATTRIBUTES[2][paramInt]);
      if (localLengthValue == null) {
        localLengthValue = (CSS.LengthValue)DEFAULTS[2];
      }
      i = (int)localLengthValue.getValue(true);
    }
    return i;
  }
  
  private int[] getWidths()
  {
    int[] arrayOfInt = new int[4];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = getBorderWidth(i);
    }
    return arrayOfInt;
  }
  
  private CSS.Value getBorderStyle(int paramInt)
  {
    CSS.BorderStyle localBorderStyle = (CSS.BorderStyle)attrs.getAttribute(ATTRIBUTES[1][paramInt]);
    if (localBorderStyle == null) {
      localBorderStyle = (CSS.BorderStyle)DEFAULTS[1];
    }
    return localBorderStyle.getValue();
  }
  
  private Polygon getBorderShape(int paramInt)
  {
    Polygon localPolygon = null;
    int[] arrayOfInt = getWidths();
    if (arrayOfInt[paramInt] != 0)
    {
      localPolygon = new Polygon(new int[4], new int[4], 0);
      localPolygon.addPoint(0, 0);
      localPolygon.addPoint(-arrayOfInt[((paramInt + 3) % 4)], -arrayOfInt[paramInt]);
      localPolygon.addPoint(arrayOfInt[((paramInt + 1) % 4)], -arrayOfInt[paramInt]);
      localPolygon.addPoint(0, 0);
    }
    return localPolygon;
  }
  
  private BorderPainter getBorderPainter(int paramInt)
  {
    CSS.Value localValue = getBorderStyle(paramInt);
    return (BorderPainter)borderPainters.get(localValue);
  }
  
  static Color getAdjustedColor(Color paramColor, double paramDouble)
  {
    double d1 = 1.0D - Math.min(Math.abs(paramDouble), 1.0D);
    double d2 = paramDouble > 0.0D ? 255.0D * (1.0D - d1) : 0.0D;
    return new Color((int)(paramColor.getRed() * d1 + d2), (int)(paramColor.getGreen() * d1 + d2), (int)(paramColor.getBlue() * d1 + d2));
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
  {
    int[] arrayOfInt = getWidths();
    paramInsets.set(arrayOfInt[0], arrayOfInt[3], arrayOfInt[2], arrayOfInt[1]);
    return paramInsets;
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!(paramGraphics instanceof Graphics2D)) {
      return;
    }
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics.create();
    int[] arrayOfInt = getWidths();
    int i = paramInt1 + arrayOfInt[3];
    int j = paramInt2 + arrayOfInt[0];
    int k = paramInt3 - (arrayOfInt[1] + arrayOfInt[3]);
    int m = paramInt4 - (arrayOfInt[0] + arrayOfInt[2]);
    int[][] arrayOfInt1 = { { i, j }, { i + k, j }, { i + k, j + m }, { i, j + m } };
    for (int n = 0; n < 4; n++)
    {
      CSS.Value localValue = getBorderStyle(n);
      Polygon localPolygon = getBorderShape(n);
      if ((localValue != CSS.Value.NONE) && (localPolygon != null))
      {
        int i1 = n % 2 == 0 ? k : m;
        xpoints[2] += i1;
        xpoints[3] += i1;
        Color localColor = getBorderColor(n);
        BorderPainter localBorderPainter = getBorderPainter(n);
        double d = n * 3.141592653589793D / 2.0D;
        localGraphics2D.setClip(paramGraphics.getClip());
        localGraphics2D.translate(arrayOfInt1[n][0], arrayOfInt1[n][1]);
        localGraphics2D.rotate(d);
        localGraphics2D.clip(localPolygon);
        localBorderPainter.paint(localPolygon, localGraphics2D, localColor, n);
        localGraphics2D.rotate(-d);
        localGraphics2D.translate(-arrayOfInt1[n][0], -arrayOfInt1[n][1]);
      }
    }
    localGraphics2D.dispose();
  }
  
  static void registerBorderPainter(CSS.Value paramValue, BorderPainter paramBorderPainter)
  {
    borderPainters.put(paramValue, paramBorderPainter);
  }
  
  static
  {
    registerBorderPainter(CSS.Value.NONE, new NullPainter());
    registerBorderPainter(CSS.Value.HIDDEN, new NullPainter());
    registerBorderPainter(CSS.Value.SOLID, new SolidPainter());
    registerBorderPainter(CSS.Value.DOUBLE, new DoublePainter());
    registerBorderPainter(CSS.Value.DOTTED, new DottedDashedPainter(1));
    registerBorderPainter(CSS.Value.DASHED, new DottedDashedPainter(3));
    registerBorderPainter(CSS.Value.GROOVE, new GrooveRidgePainter(CSS.Value.GROOVE));
    registerBorderPainter(CSS.Value.RIDGE, new GrooveRidgePainter(CSS.Value.RIDGE));
    registerBorderPainter(CSS.Value.INSET, new InsetOutsetPainter(CSS.Value.INSET));
    registerBorderPainter(CSS.Value.OUTSET, new InsetOutsetPainter(CSS.Value.OUTSET));
  }
  
  static abstract interface BorderPainter
  {
    public abstract void paint(Polygon paramPolygon, Graphics paramGraphics, Color paramColor, int paramInt);
  }
  
  static class DottedDashedPainter
    extends CSSBorder.StrokePainter
  {
    final int factor;
    
    DottedDashedPainter(int paramInt)
    {
      factor = paramInt;
    }
    
    public void paint(Polygon paramPolygon, Graphics paramGraphics, Color paramColor, int paramInt)
    {
      Rectangle localRectangle = paramPolygon.getBounds();
      int i = height * factor;
      int[] arrayOfInt = { i, i };
      Color[] arrayOfColor = { paramColor, null };
      paintStrokes(localRectangle, paramGraphics, 0, arrayOfInt, arrayOfColor);
    }
  }
  
  static class DoublePainter
    extends CSSBorder.StrokePainter
  {
    DoublePainter() {}
    
    public void paint(Polygon paramPolygon, Graphics paramGraphics, Color paramColor, int paramInt)
    {
      Rectangle localRectangle = paramPolygon.getBounds();
      int i = Math.max(height / 3, 1);
      int[] arrayOfInt = { i, i };
      Color[] arrayOfColor = { paramColor, null };
      paintStrokes(localRectangle, paramGraphics, 1, arrayOfInt, arrayOfColor);
    }
  }
  
  static class GrooveRidgePainter
    extends CSSBorder.ShadowLightPainter
  {
    final CSS.Value type;
    
    GrooveRidgePainter(CSS.Value paramValue)
    {
      type = paramValue;
    }
    
    public void paint(Polygon paramPolygon, Graphics paramGraphics, Color paramColor, int paramInt)
    {
      Rectangle localRectangle = paramPolygon.getBounds();
      int i = Math.max(height / 2, 1);
      int[] arrayOfInt = { i, i };
      Color[] arrayOfColor = { getLightColor(paramColor), ((paramInt + 1) % 4 < 2 ? 1 : 0) == (type == CSS.Value.GROOVE ? 1 : 0) ? new Color[] { getShadowColor(paramColor), getLightColor(paramColor) } : getShadowColor(paramColor) };
      paintStrokes(localRectangle, paramGraphics, 1, arrayOfInt, arrayOfColor);
    }
  }
  
  static class InsetOutsetPainter
    extends CSSBorder.ShadowLightPainter
  {
    CSS.Value type;
    
    InsetOutsetPainter(CSS.Value paramValue)
    {
      type = paramValue;
    }
    
    public void paint(Polygon paramPolygon, Graphics paramGraphics, Color paramColor, int paramInt)
    {
      paramGraphics.setColor(((paramInt + 1) % 4 < 2 ? 1 : 0) == (type == CSS.Value.INSET ? 1 : 0) ? getShadowColor(paramColor) : getLightColor(paramColor));
      paramGraphics.fillPolygon(paramPolygon);
    }
  }
  
  static class NullPainter
    implements CSSBorder.BorderPainter
  {
    NullPainter() {}
    
    public void paint(Polygon paramPolygon, Graphics paramGraphics, Color paramColor, int paramInt) {}
  }
  
  static abstract class ShadowLightPainter
    extends CSSBorder.StrokePainter
  {
    ShadowLightPainter() {}
    
    static Color getShadowColor(Color paramColor)
    {
      return CSSBorder.getAdjustedColor(paramColor, -0.3D);
    }
    
    static Color getLightColor(Color paramColor)
    {
      return CSSBorder.getAdjustedColor(paramColor, 0.7D);
    }
  }
  
  static class SolidPainter
    implements CSSBorder.BorderPainter
  {
    SolidPainter() {}
    
    public void paint(Polygon paramPolygon, Graphics paramGraphics, Color paramColor, int paramInt)
    {
      paramGraphics.setColor(paramColor);
      paramGraphics.fillPolygon(paramPolygon);
    }
  }
  
  static abstract class StrokePainter
    implements CSSBorder.BorderPainter
  {
    StrokePainter() {}
    
    void paintStrokes(Rectangle paramRectangle, Graphics paramGraphics, int paramInt, int[] paramArrayOfInt, Color[] paramArrayOfColor)
    {
      int i = paramInt == 0 ? 1 : 0;
      int j = 0;
      int k = i != 0 ? width : height;
      while (j < k) {
        for (int m = 0; (m < paramArrayOfInt.length) && (j < k); m++)
        {
          int n = paramArrayOfInt[m];
          Color localColor = paramArrayOfColor[m];
          if (localColor != null)
          {
            int i1 = x + (i != 0 ? j : 0);
            int i2 = y + (i != 0 ? 0 : j);
            int i3 = i != 0 ? n : width;
            int i4 = i != 0 ? height : n;
            paramGraphics.setColor(localColor);
            paramGraphics.fillRect(i1, i2, i3, i4);
          }
          j += n;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\CSSBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */