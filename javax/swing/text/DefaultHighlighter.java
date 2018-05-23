package javax.swing.text;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Vector;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TextUI;

public class DefaultHighlighter
  extends LayeredHighlighter
{
  private static final Highlighter.Highlight[] noHighlights = new Highlighter.Highlight[0];
  private Vector<HighlightInfo> highlights = new Vector();
  private JTextComponent component;
  private boolean drawsLayeredHighlights = true;
  private SafeDamager safeDamager = new SafeDamager();
  public static final LayeredHighlighter.LayerPainter DefaultPainter = new DefaultHighlightPainter(null);
  
  public DefaultHighlighter() {}
  
  public void paint(Graphics paramGraphics)
  {
    int i = highlights.size();
    for (int j = 0; j < i; j++)
    {
      HighlightInfo localHighlightInfo = (HighlightInfo)highlights.elementAt(j);
      if (!(localHighlightInfo instanceof LayeredHighlightInfo))
      {
        Rectangle localRectangle = component.getBounds();
        Insets localInsets = component.getInsets();
        x = left;
        y = top;
        width -= left + right;
        height -= top + bottom;
        while (j < i)
        {
          localHighlightInfo = (HighlightInfo)highlights.elementAt(j);
          if (!(localHighlightInfo instanceof LayeredHighlightInfo))
          {
            Highlighter.HighlightPainter localHighlightPainter = localHighlightInfo.getPainter();
            localHighlightPainter.paint(paramGraphics, localHighlightInfo.getStartOffset(), localHighlightInfo.getEndOffset(), localRectangle, component);
          }
          j++;
        }
      }
    }
  }
  
  public void install(JTextComponent paramJTextComponent)
  {
    component = paramJTextComponent;
    removeAllHighlights();
  }
  
  public void deinstall(JTextComponent paramJTextComponent)
  {
    component = null;
  }
  
  public Object addHighlight(int paramInt1, int paramInt2, Highlighter.HighlightPainter paramHighlightPainter)
    throws BadLocationException
  {
    if (paramInt1 < 0) {
      throw new BadLocationException("Invalid start offset", paramInt1);
    }
    if (paramInt2 < paramInt1) {
      throw new BadLocationException("Invalid end offset", paramInt2);
    }
    Document localDocument = component.getDocument();
    HighlightInfo localHighlightInfo = (getDrawsLayeredHighlights()) && ((paramHighlightPainter instanceof LayeredHighlighter.LayerPainter)) ? new LayeredHighlightInfo() : new HighlightInfo();
    painter = paramHighlightPainter;
    p0 = localDocument.createPosition(paramInt1);
    p1 = localDocument.createPosition(paramInt2);
    highlights.addElement(localHighlightInfo);
    safeDamageRange(paramInt1, paramInt2);
    return localHighlightInfo;
  }
  
  public void removeHighlight(Object paramObject)
  {
    Object localObject;
    if ((paramObject instanceof LayeredHighlightInfo))
    {
      localObject = (LayeredHighlightInfo)paramObject;
      if ((width > 0) && (height > 0)) {
        component.repaint(x, y, width, height);
      }
    }
    else
    {
      localObject = (HighlightInfo)paramObject;
      safeDamageRange(p0, p1);
    }
    highlights.removeElement(paramObject);
  }
  
  public void removeAllHighlights()
  {
    TextUI localTextUI = component.getUI();
    int i;
    int j;
    int k;
    int m;
    if (getDrawsLayeredHighlights())
    {
      i = highlights.size();
      if (i != 0)
      {
        j = 0;
        k = 0;
        m = 0;
        int n = 0;
        int i1 = -1;
        int i2 = -1;
        for (int i3 = 0; i3 < i; i3++)
        {
          HighlightInfo localHighlightInfo2 = (HighlightInfo)highlights.elementAt(i3);
          if ((localHighlightInfo2 instanceof LayeredHighlightInfo))
          {
            LayeredHighlightInfo localLayeredHighlightInfo = (LayeredHighlightInfo)localHighlightInfo2;
            j = Math.min(j, x);
            k = Math.min(k, y);
            m = Math.max(m, x + width);
            n = Math.max(n, y + height);
          }
          else if (i1 == -1)
          {
            i1 = p0.getOffset();
            i2 = p1.getOffset();
          }
          else
          {
            i1 = Math.min(i1, p0.getOffset());
            i2 = Math.max(i2, p1.getOffset());
          }
        }
        if ((j != m) && (k != n)) {
          component.repaint(j, k, m - j, n - k);
        }
        if (i1 != -1) {
          try
          {
            safeDamageRange(i1, i2);
          }
          catch (BadLocationException localBadLocationException2) {}
        }
        highlights.removeAllElements();
      }
    }
    else if (localTextUI != null)
    {
      i = highlights.size();
      if (i != 0)
      {
        j = Integer.MAX_VALUE;
        k = 0;
        for (m = 0; m < i; m++)
        {
          HighlightInfo localHighlightInfo1 = (HighlightInfo)highlights.elementAt(m);
          j = Math.min(j, p0.getOffset());
          k = Math.max(k, p1.getOffset());
        }
        try
        {
          safeDamageRange(j, k);
        }
        catch (BadLocationException localBadLocationException1) {}
        highlights.removeAllElements();
      }
    }
  }
  
  public void changeHighlight(Object paramObject, int paramInt1, int paramInt2)
    throws BadLocationException
  {
    if (paramInt1 < 0) {
      throw new BadLocationException("Invalid beginning of the range", paramInt1);
    }
    if (paramInt2 < paramInt1) {
      throw new BadLocationException("Invalid end of the range", paramInt2);
    }
    Document localDocument = component.getDocument();
    Object localObject;
    if ((paramObject instanceof LayeredHighlightInfo))
    {
      localObject = (LayeredHighlightInfo)paramObject;
      if ((width > 0) && (height > 0)) {
        component.repaint(x, y, width, height);
      }
      width = (height = 0);
      p0 = localDocument.createPosition(paramInt1);
      p1 = localDocument.createPosition(paramInt2);
      safeDamageRange(Math.min(paramInt1, paramInt2), Math.max(paramInt1, paramInt2));
    }
    else
    {
      localObject = (HighlightInfo)paramObject;
      int i = p0.getOffset();
      int j = p1.getOffset();
      if (paramInt1 == i)
      {
        safeDamageRange(Math.min(j, paramInt2), Math.max(j, paramInt2));
      }
      else if (paramInt2 == j)
      {
        safeDamageRange(Math.min(paramInt1, i), Math.max(paramInt1, i));
      }
      else
      {
        safeDamageRange(i, j);
        safeDamageRange(paramInt1, paramInt2);
      }
      p0 = localDocument.createPosition(paramInt1);
      p1 = localDocument.createPosition(paramInt2);
    }
  }
  
  public Highlighter.Highlight[] getHighlights()
  {
    int i = highlights.size();
    if (i == 0) {
      return noHighlights;
    }
    Highlighter.Highlight[] arrayOfHighlight = new Highlighter.Highlight[i];
    highlights.copyInto(arrayOfHighlight);
    return arrayOfHighlight;
  }
  
  public void paintLayeredHighlights(Graphics paramGraphics, int paramInt1, int paramInt2, Shape paramShape, JTextComponent paramJTextComponent, View paramView)
  {
    for (int i = highlights.size() - 1; i >= 0; i--)
    {
      HighlightInfo localHighlightInfo = (HighlightInfo)highlights.elementAt(i);
      if ((localHighlightInfo instanceof LayeredHighlightInfo))
      {
        LayeredHighlightInfo localLayeredHighlightInfo = (LayeredHighlightInfo)localHighlightInfo;
        int j = localLayeredHighlightInfo.getStartOffset();
        int k = localLayeredHighlightInfo.getEndOffset();
        if (((paramInt1 < j) && (paramInt2 > j)) || ((paramInt1 >= j) && (paramInt1 < k))) {
          localLayeredHighlightInfo.paintLayeredHighlights(paramGraphics, paramInt1, paramInt2, paramShape, paramJTextComponent, paramView);
        }
      }
    }
  }
  
  private void safeDamageRange(Position paramPosition1, Position paramPosition2)
  {
    safeDamager.damageRange(paramPosition1, paramPosition2);
  }
  
  private void safeDamageRange(int paramInt1, int paramInt2)
    throws BadLocationException
  {
    Document localDocument = component.getDocument();
    safeDamageRange(localDocument.createPosition(paramInt1), localDocument.createPosition(paramInt2));
  }
  
  public void setDrawsLayeredHighlights(boolean paramBoolean)
  {
    drawsLayeredHighlights = paramBoolean;
  }
  
  public boolean getDrawsLayeredHighlights()
  {
    return drawsLayeredHighlights;
  }
  
  public static class DefaultHighlightPainter
    extends LayeredHighlighter.LayerPainter
  {
    private Color color;
    
    public DefaultHighlightPainter(Color paramColor)
    {
      color = paramColor;
    }
    
    public Color getColor()
    {
      return color;
    }
    
    public void paint(Graphics paramGraphics, int paramInt1, int paramInt2, Shape paramShape, JTextComponent paramJTextComponent)
    {
      Rectangle localRectangle1 = paramShape.getBounds();
      try
      {
        TextUI localTextUI = paramJTextComponent.getUI();
        Rectangle localRectangle2 = localTextUI.modelToView(paramJTextComponent, paramInt1);
        Rectangle localRectangle3 = localTextUI.modelToView(paramJTextComponent, paramInt2);
        Color localColor = getColor();
        if (localColor == null) {
          paramGraphics.setColor(paramJTextComponent.getSelectionColor());
        } else {
          paramGraphics.setColor(localColor);
        }
        if (y == y)
        {
          Rectangle localRectangle4 = localRectangle2.union(localRectangle3);
          paramGraphics.fillRect(x, y, width, height);
        }
        else
        {
          int i = x + width - x;
          paramGraphics.fillRect(x, y, i, height);
          if (y + height != y) {
            paramGraphics.fillRect(x, y + height, width, y - (y + height));
          }
          paramGraphics.fillRect(x, y, x - x, height);
        }
      }
      catch (BadLocationException localBadLocationException) {}
    }
    
    public Shape paintLayer(Graphics paramGraphics, int paramInt1, int paramInt2, Shape paramShape, JTextComponent paramJTextComponent, View paramView)
    {
      Color localColor = getColor();
      if (localColor == null) {
        paramGraphics.setColor(paramJTextComponent.getSelectionColor());
      } else {
        paramGraphics.setColor(localColor);
      }
      Rectangle localRectangle;
      if ((paramInt1 == paramView.getStartOffset()) && (paramInt2 == paramView.getEndOffset()))
      {
        if ((paramShape instanceof Rectangle)) {
          localRectangle = (Rectangle)paramShape;
        } else {
          localRectangle = paramShape.getBounds();
        }
      }
      else {
        try
        {
          Shape localShape = paramView.modelToView(paramInt1, Position.Bias.Forward, paramInt2, Position.Bias.Backward, paramShape);
          localRectangle = (localShape instanceof Rectangle) ? (Rectangle)localShape : localShape.getBounds();
        }
        catch (BadLocationException localBadLocationException)
        {
          localRectangle = null;
        }
      }
      if (localRectangle != null)
      {
        width = Math.max(width, 1);
        paramGraphics.fillRect(x, y, width, height);
      }
      return localRectangle;
    }
  }
  
  class HighlightInfo
    implements Highlighter.Highlight
  {
    Position p0;
    Position p1;
    Highlighter.HighlightPainter painter;
    
    HighlightInfo() {}
    
    public int getStartOffset()
    {
      return p0.getOffset();
    }
    
    public int getEndOffset()
    {
      return p1.getOffset();
    }
    
    public Highlighter.HighlightPainter getPainter()
    {
      return painter;
    }
  }
  
  class LayeredHighlightInfo
    extends DefaultHighlighter.HighlightInfo
  {
    int x;
    int y;
    int width;
    int height;
    
    LayeredHighlightInfo()
    {
      super();
    }
    
    void union(Shape paramShape)
    {
      if (paramShape == null) {
        return;
      }
      Rectangle localRectangle;
      if ((paramShape instanceof Rectangle)) {
        localRectangle = (Rectangle)paramShape;
      } else {
        localRectangle = paramShape.getBounds();
      }
      if ((width == 0) || (height == 0))
      {
        x = x;
        y = y;
        width = width;
        height = height;
      }
      else
      {
        width = Math.max(x + width, x + width);
        height = Math.max(y + height, y + height);
        x = Math.min(x, x);
        width -= x;
        y = Math.min(y, y);
        height -= y;
      }
    }
    
    void paintLayeredHighlights(Graphics paramGraphics, int paramInt1, int paramInt2, Shape paramShape, JTextComponent paramJTextComponent, View paramView)
    {
      int i = getStartOffset();
      int j = getEndOffset();
      paramInt1 = Math.max(i, paramInt1);
      paramInt2 = Math.min(j, paramInt2);
      union(((LayeredHighlighter.LayerPainter)painter).paintLayer(paramGraphics, paramInt1, paramInt2, paramShape, paramJTextComponent, paramView));
    }
  }
  
  class SafeDamager
    implements Runnable
  {
    private Vector<Position> p0 = new Vector(10);
    private Vector<Position> p1 = new Vector(10);
    private Document lastDoc = null;
    
    SafeDamager() {}
    
    public synchronized void run()
    {
      if (component != null)
      {
        TextUI localTextUI = component.getUI();
        if ((localTextUI != null) && (lastDoc == component.getDocument()))
        {
          int i = p0.size();
          for (int j = 0; j < i; j++) {
            localTextUI.damageRange(component, ((Position)p0.get(j)).getOffset(), ((Position)p1.get(j)).getOffset());
          }
        }
      }
      p0.clear();
      p1.clear();
      lastDoc = null;
    }
    
    public synchronized void damageRange(Position paramPosition1, Position paramPosition2)
    {
      if (component == null)
      {
        p0.clear();
        lastDoc = null;
        return;
      }
      boolean bool = p0.isEmpty();
      Document localDocument = component.getDocument();
      if (localDocument != lastDoc)
      {
        if (!p0.isEmpty())
        {
          p0.clear();
          p1.clear();
        }
        lastDoc = localDocument;
      }
      p0.add(paramPosition1);
      p1.add(paramPosition2);
      if (bool) {
        SwingUtilities.invokeLater(this);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\DefaultHighlighter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */