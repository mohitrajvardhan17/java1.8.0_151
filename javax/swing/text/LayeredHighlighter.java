package javax.swing.text;

import java.awt.Graphics;
import java.awt.Shape;

public abstract class LayeredHighlighter
  implements Highlighter
{
  public LayeredHighlighter() {}
  
  public abstract void paintLayeredHighlights(Graphics paramGraphics, int paramInt1, int paramInt2, Shape paramShape, JTextComponent paramJTextComponent, View paramView);
  
  public static abstract class LayerPainter
    implements Highlighter.HighlightPainter
  {
    public LayerPainter() {}
    
    public abstract Shape paintLayer(Graphics paramGraphics, int paramInt1, int paramInt2, Shape paramShape, JTextComponent paramJTextComponent, View paramView);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\LayeredHighlighter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */