package sun.font;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GraphicAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

public final class GraphicComponent
  implements TextLineComponent, Decoration.Label
{
  public static final float GRAPHIC_LEADING = 2.0F;
  private GraphicAttribute graphic;
  private int graphicCount;
  private int[] charsLtoV;
  private byte[] levels;
  private Rectangle2D visualBounds = null;
  private float graphicAdvance;
  private AffineTransform baseTx;
  private CoreMetrics cm;
  private Decoration decorator;
  
  public GraphicComponent(GraphicAttribute paramGraphicAttribute, Decoration paramDecoration, int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2, AffineTransform paramAffineTransform)
  {
    if (paramInt2 <= paramInt1) {
      throw new IllegalArgumentException("0 or negative length in GraphicComponent");
    }
    graphic = paramGraphicAttribute;
    graphicAdvance = paramGraphicAttribute.getAdvance();
    decorator = paramDecoration;
    cm = createCoreMetrics(paramGraphicAttribute);
    baseTx = paramAffineTransform;
    initLocalOrdering(paramArrayOfInt, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  private GraphicComponent(GraphicComponent paramGraphicComponent, int paramInt1, int paramInt2, int paramInt3)
  {
    graphic = graphic;
    graphicAdvance = graphicAdvance;
    decorator = decorator;
    cm = cm;
    baseTx = baseTx;
    int[] arrayOfInt = null;
    byte[] arrayOfByte = null;
    if (paramInt3 == 2)
    {
      arrayOfInt = charsLtoV;
      arrayOfByte = levels;
    }
    else if ((paramInt3 == 0) || (paramInt3 == 1))
    {
      paramInt2 -= paramInt1;
      paramInt1 = 0;
      if (paramInt3 == 1)
      {
        arrayOfInt = new int[paramInt2];
        arrayOfByte = new byte[paramInt2];
        for (int i = 0; i < paramInt2; i++)
        {
          arrayOfInt[i] = (paramInt2 - i - 1);
          arrayOfByte[i] = 1;
        }
      }
    }
    else
    {
      throw new IllegalArgumentException("Invalid direction flag");
    }
    initLocalOrdering(arrayOfInt, arrayOfByte, paramInt1, paramInt2);
  }
  
  private void initLocalOrdering(int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    graphicCount = (paramInt2 - paramInt1);
    if ((paramArrayOfInt == null) || (paramArrayOfInt.length == graphicCount)) {
      charsLtoV = paramArrayOfInt;
    } else {
      charsLtoV = BidiUtils.createNormalizedMap(paramArrayOfInt, paramArrayOfByte, paramInt1, paramInt2);
    }
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == graphicCount))
    {
      levels = paramArrayOfByte;
    }
    else
    {
      levels = new byte[graphicCount];
      System.arraycopy(paramArrayOfByte, paramInt1, levels, 0, graphicCount);
    }
  }
  
  public boolean isSimple()
  {
    return false;
  }
  
  public Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2)
  {
    throw new InternalError("do not call if isSimple returns false");
  }
  
  public Rectangle2D handleGetVisualBounds()
  {
    Rectangle2D localRectangle2D = graphic.getBounds();
    float f = (float)localRectangle2D.getWidth() + graphicAdvance * (graphicCount - 1);
    return new Rectangle2D.Float((float)localRectangle2D.getX(), (float)localRectangle2D.getY(), f, (float)localRectangle2D.getHeight());
  }
  
  public CoreMetrics getCoreMetrics()
  {
    return cm;
  }
  
  public static CoreMetrics createCoreMetrics(GraphicAttribute paramGraphicAttribute)
  {
    return new CoreMetrics(paramGraphicAttribute.getAscent(), paramGraphicAttribute.getDescent(), 2.0F, paramGraphicAttribute.getAscent() + paramGraphicAttribute.getDescent() + 2.0F, paramGraphicAttribute.getAlignment(), new float[] { 0.0F, -paramGraphicAttribute.getAscent() / 2.0F, -paramGraphicAttribute.getAscent() }, -paramGraphicAttribute.getAscent() / 2.0F, paramGraphicAttribute.getAscent() / 12.0F, paramGraphicAttribute.getDescent() / 3.0F, paramGraphicAttribute.getAscent() / 12.0F, 0.0F, 0.0F);
  }
  
  public float getItalicAngle()
  {
    return 0.0F;
  }
  
  public Rectangle2D getVisualBounds()
  {
    if (visualBounds == null) {
      visualBounds = decorator.getVisualBounds(this);
    }
    Rectangle2D.Float localFloat = new Rectangle2D.Float();
    localFloat.setRect(visualBounds);
    return localFloat;
  }
  
  public Shape handleGetOutline(float paramFloat1, float paramFloat2)
  {
    double[] arrayOfDouble = { 1.0D, 0.0D, 0.0D, 1.0D, paramFloat1, paramFloat2 };
    if (graphicCount == 1)
    {
      localObject = new AffineTransform(arrayOfDouble);
      return graphic.getOutline((AffineTransform)localObject);
    }
    Object localObject = new GeneralPath();
    for (int i = 0; i < graphicCount; i++)
    {
      AffineTransform localAffineTransform = new AffineTransform(arrayOfDouble);
      ((GeneralPath)localObject).append(graphic.getOutline(localAffineTransform), false);
      arrayOfDouble[4] += graphicAdvance;
    }
    return (Shape)localObject;
  }
  
  public AffineTransform getBaselineTransform()
  {
    return baseTx;
  }
  
  public Shape getOutline(float paramFloat1, float paramFloat2)
  {
    return decorator.getOutline(this, paramFloat1, paramFloat2);
  }
  
  public void handleDraw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2)
  {
    for (int i = 0; i < graphicCount; i++)
    {
      graphic.draw(paramGraphics2D, paramFloat1, paramFloat2);
      paramFloat1 += graphicAdvance;
    }
  }
  
  public void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2)
  {
    decorator.drawTextAndDecorations(this, paramGraphics2D, paramFloat1, paramFloat2);
  }
  
  public Rectangle2D getCharVisualBounds(int paramInt)
  {
    return decorator.getCharVisualBounds(this, paramInt);
  }
  
  public int getNumCharacters()
  {
    return graphicCount;
  }
  
  public float getCharX(int paramInt)
  {
    int i = charsLtoV == null ? paramInt : charsLtoV[paramInt];
    return graphicAdvance * i;
  }
  
  public float getCharY(int paramInt)
  {
    return 0.0F;
  }
  
  public float getCharAdvance(int paramInt)
  {
    return graphicAdvance;
  }
  
  public boolean caretAtOffsetIsValid(int paramInt)
  {
    return true;
  }
  
  public Rectangle2D handleGetCharVisualBounds(int paramInt)
  {
    Rectangle2D localRectangle2D = graphic.getBounds();
    Rectangle2D.Float localFloat = new Rectangle2D.Float();
    localFloat.setRect(localRectangle2D);
    x += graphicAdvance * paramInt;
    return localFloat;
  }
  
  public int getLineBreakIndex(int paramInt, float paramFloat)
  {
    int i = (int)(paramFloat / graphicAdvance);
    if (i > graphicCount - paramInt) {
      i = graphicCount - paramInt;
    }
    return i;
  }
  
  public float getAdvanceBetween(int paramInt1, int paramInt2)
  {
    return graphicAdvance * (paramInt2 - paramInt1);
  }
  
  public Rectangle2D getLogicalBounds()
  {
    float f1 = 0.0F;
    float f2 = -cm.ascent;
    float f3 = graphicAdvance * graphicCount;
    float f4 = cm.descent - f2;
    return new Rectangle2D.Float(f1, f2, f3, f4);
  }
  
  public float getAdvance()
  {
    return graphicAdvance * graphicCount;
  }
  
  public Rectangle2D getItalicBounds()
  {
    return getLogicalBounds();
  }
  
  public TextLineComponent getSubset(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 < 0) || (paramInt2 > graphicCount) || (paramInt1 >= paramInt2)) {
      throw new IllegalArgumentException("Invalid range.  start=" + paramInt1 + "; limit=" + paramInt2);
    }
    if ((paramInt1 == 0) && (paramInt2 == graphicCount) && (paramInt3 == 2)) {
      return this;
    }
    return new GraphicComponent(this, paramInt1, paramInt2, paramInt3);
  }
  
  public String toString()
  {
    return "[graphic=" + graphic + ":count=" + getNumCharacters() + "]";
  }
  
  public int getNumJustificationInfos()
  {
    return 0;
  }
  
  public void getJustificationInfos(GlyphJustificationInfo[] paramArrayOfGlyphJustificationInfo, int paramInt1, int paramInt2, int paramInt3) {}
  
  public TextLineComponent applyJustificationDeltas(float[] paramArrayOfFloat, int paramInt, boolean[] paramArrayOfBoolean)
  {
    return this;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\GraphicComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */