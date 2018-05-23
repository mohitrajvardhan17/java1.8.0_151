package javax.swing.text;

import java.awt.Container;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.BreakIterator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import sun.font.BidiUtils;
import sun.swing.SwingUtilities2;

class TextLayoutStrategy
  extends FlowView.FlowStrategy
{
  private LineBreakMeasurer measurer;
  private AttributedSegment text = new AttributedSegment();
  
  public TextLayoutStrategy() {}
  
  public void insertUpdate(FlowView paramFlowView, DocumentEvent paramDocumentEvent, Rectangle paramRectangle)
  {
    sync(paramFlowView);
    super.insertUpdate(paramFlowView, paramDocumentEvent, paramRectangle);
  }
  
  public void removeUpdate(FlowView paramFlowView, DocumentEvent paramDocumentEvent, Rectangle paramRectangle)
  {
    sync(paramFlowView);
    super.removeUpdate(paramFlowView, paramDocumentEvent, paramRectangle);
  }
  
  public void changedUpdate(FlowView paramFlowView, DocumentEvent paramDocumentEvent, Rectangle paramRectangle)
  {
    sync(paramFlowView);
    super.changedUpdate(paramFlowView, paramDocumentEvent, paramRectangle);
  }
  
  public void layout(FlowView paramFlowView)
  {
    super.layout(paramFlowView);
  }
  
  protected int layoutRow(FlowView paramFlowView, int paramInt1, int paramInt2)
  {
    int i = super.layoutRow(paramFlowView, paramInt1, paramInt2);
    View localView1 = paramFlowView.getView(paramInt1);
    Document localDocument = paramFlowView.getDocument();
    Object localObject = localDocument.getProperty("i18n");
    if ((localObject != null) && (localObject.equals(Boolean.TRUE)))
    {
      int j = localView1.getViewCount();
      if (j > 1)
      {
        AbstractDocument localAbstractDocument = (AbstractDocument)paramFlowView.getDocument();
        Element localElement1 = localAbstractDocument.getBidiRootElement();
        byte[] arrayOfByte = new byte[j];
        View[] arrayOfView = new View[j];
        for (int k = 0; k < j; k++)
        {
          View localView2 = localView1.getView(k);
          int m = localElement1.getElementIndex(localView2.getStartOffset());
          Element localElement2 = localElement1.getElement(m);
          arrayOfByte[k] = ((byte)StyleConstants.getBidiLevel(localElement2.getAttributes()));
          arrayOfView[k] = localView2;
        }
        BidiUtils.reorderVisually(arrayOfByte, arrayOfView);
        localView1.replace(0, j, arrayOfView);
      }
    }
    return i;
  }
  
  protected void adjustRow(FlowView paramFlowView, int paramInt1, int paramInt2, int paramInt3) {}
  
  protected View createView(FlowView paramFlowView, int paramInt1, int paramInt2, int paramInt3)
  {
    View localView1 = getLogicalView(paramFlowView);
    View localView2 = paramFlowView.getView(paramInt3);
    boolean bool = viewBuffer.size() != 0;
    int i = localView1.getViewIndex(paramInt1, Position.Bias.Forward);
    View localView3 = localView1.getView(i);
    int j = getLimitingOffset(localView3, paramInt1, paramInt2, bool);
    if (j == paramInt1) {
      return null;
    }
    View localView4;
    if ((paramInt1 == localView3.getStartOffset()) && (j == localView3.getEndOffset())) {
      localView4 = localView3;
    } else {
      localView4 = localView3.createFragment(paramInt1, j);
    }
    if (((localView4 instanceof GlyphView)) && (measurer != null))
    {
      int k = 0;
      int m = localView4.getStartOffset();
      int n = localView4.getEndOffset();
      if (n - m == 1)
      {
        localObject = ((GlyphView)localView4).getText(m, n);
        int i1 = ((Segment)localObject).first();
        if (i1 == 9) {
          k = 1;
        }
      }
      Object localObject = k != 0 ? null : measurer.nextLayout(paramInt2, text.toIteratorIndex(j), bool);
      if (localObject != null) {
        ((GlyphView)localView4).setGlyphPainter(new GlyphPainter2((TextLayout)localObject));
      }
    }
    return localView4;
  }
  
  int getLimitingOffset(View paramView, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = paramView.getEndOffset();
    Document localDocument = paramView.getDocument();
    Object localObject;
    if ((localDocument instanceof AbstractDocument))
    {
      localObject = (AbstractDocument)localDocument;
      Element localElement1 = ((AbstractDocument)localObject).getBidiRootElement();
      if (localElement1.getElementCount() > 1)
      {
        int m = localElement1.getElementIndex(paramInt1);
        Element localElement2 = localElement1.getElement(m);
        i = Math.min(localElement2.getEndOffset(), i);
      }
    }
    if ((paramView instanceof GlyphView))
    {
      localObject = ((GlyphView)paramView).getText(paramInt1, i);
      k = ((Segment)localObject).first();
      if (k == 9) {
        i = paramInt1 + 1;
      } else {
        for (k = ((Segment)localObject).next(); k != 65535; k = ((Segment)localObject).next()) {
          if (k == 9)
          {
            i = paramInt1 + ((Segment)localObject).getIndex() - ((Segment)localObject).getBeginIndex();
            break;
          }
        }
      }
    }
    int j = text.toIteratorIndex(i);
    if (measurer != null)
    {
      k = text.toIteratorIndex(paramInt1);
      if (measurer.getPosition() != k) {
        measurer.setPosition(k);
      }
      j = measurer.nextOffset(paramInt2, j, paramBoolean);
    }
    int k = text.toModelPosition(j);
    return k;
  }
  
  void sync(FlowView paramFlowView)
  {
    View localView1 = getLogicalView(paramFlowView);
    text.setView(localView1);
    Container localContainer1 = paramFlowView.getContainer();
    FontRenderContext localFontRenderContext = SwingUtilities2.getFontRenderContext(localContainer1);
    Container localContainer2 = paramFlowView.getContainer();
    BreakIterator localBreakIterator;
    if (localContainer2 != null) {
      localBreakIterator = BreakIterator.getLineInstance(localContainer2.getLocale());
    } else {
      localBreakIterator = BreakIterator.getLineInstance();
    }
    Object localObject = null;
    if ((localContainer2 instanceof JComponent)) {
      localObject = ((JComponent)localContainer2).getClientProperty(TextAttribute.NUMERIC_SHAPING);
    }
    text.setShaper(localObject);
    measurer = new LineBreakMeasurer(text, localBreakIterator, localFontRenderContext);
    int i = localView1.getViewCount();
    for (int j = 0; j < i; j++)
    {
      View localView2 = localView1.getView(j);
      if ((localView2 instanceof GlyphView))
      {
        int k = localView2.getStartOffset();
        int m = localView2.getEndOffset();
        measurer.setPosition(text.toIteratorIndex(k));
        TextLayout localTextLayout = measurer.nextLayout(Float.MAX_VALUE, text.toIteratorIndex(m), false);
        ((GlyphView)localView2).setGlyphPainter(new GlyphPainter2(localTextLayout));
      }
    }
    measurer.setPosition(text.getBeginIndex());
  }
  
  static class AttributedSegment
    extends Segment
    implements AttributedCharacterIterator
  {
    View v;
    static Set<AttributedCharacterIterator.Attribute> keys = new HashSet();
    private Object shaper = null;
    
    AttributedSegment() {}
    
    View getView()
    {
      return v;
    }
    
    void setView(View paramView)
    {
      v = paramView;
      Document localDocument = paramView.getDocument();
      int i = paramView.getStartOffset();
      int j = paramView.getEndOffset();
      try
      {
        localDocument.getText(i, j - i, this);
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new IllegalArgumentException("Invalid view");
      }
      first();
    }
    
    int getFontBoundary(int paramInt1, int paramInt2)
    {
      View localView = v.getView(paramInt1);
      Font localFont1 = getFont(paramInt1);
      paramInt1 += paramInt2;
      while ((paramInt1 >= 0) && (paramInt1 < v.getViewCount()))
      {
        Font localFont2 = getFont(paramInt1);
        if (localFont2 != localFont1) {
          break;
        }
        localView = v.getView(paramInt1);
        paramInt1 += paramInt2;
      }
      return paramInt2 < 0 ? localView.getStartOffset() : localView.getEndOffset();
    }
    
    Font getFont(int paramInt)
    {
      View localView = v.getView(paramInt);
      if ((localView instanceof GlyphView)) {
        return ((GlyphView)localView).getFont();
      }
      return null;
    }
    
    int toModelPosition(int paramInt)
    {
      return v.getStartOffset() + (paramInt - getBeginIndex());
    }
    
    int toIteratorIndex(int paramInt)
    {
      return paramInt - v.getStartOffset() + getBeginIndex();
    }
    
    private void setShaper(Object paramObject)
    {
      shaper = paramObject;
    }
    
    public int getRunStart()
    {
      int i = toModelPosition(getIndex());
      int j = v.getViewIndex(i, Position.Bias.Forward);
      View localView = v.getView(j);
      return toIteratorIndex(localView.getStartOffset());
    }
    
    public int getRunStart(AttributedCharacterIterator.Attribute paramAttribute)
    {
      if ((paramAttribute instanceof TextAttribute))
      {
        int i = toModelPosition(getIndex());
        int j = v.getViewIndex(i, Position.Bias.Forward);
        if (paramAttribute == TextAttribute.FONT) {
          return toIteratorIndex(getFontBoundary(j, -1));
        }
      }
      return getBeginIndex();
    }
    
    public int getRunStart(Set<? extends AttributedCharacterIterator.Attribute> paramSet)
    {
      int i = getBeginIndex();
      Object[] arrayOfObject = paramSet.toArray();
      for (int j = 0; j < arrayOfObject.length; j++)
      {
        TextAttribute localTextAttribute = (TextAttribute)arrayOfObject[j];
        i = Math.max(getRunStart(localTextAttribute), i);
      }
      return Math.min(getIndex(), i);
    }
    
    public int getRunLimit()
    {
      int i = toModelPosition(getIndex());
      int j = v.getViewIndex(i, Position.Bias.Forward);
      View localView = v.getView(j);
      return toIteratorIndex(localView.getEndOffset());
    }
    
    public int getRunLimit(AttributedCharacterIterator.Attribute paramAttribute)
    {
      if ((paramAttribute instanceof TextAttribute))
      {
        int i = toModelPosition(getIndex());
        int j = v.getViewIndex(i, Position.Bias.Forward);
        if (paramAttribute == TextAttribute.FONT) {
          return toIteratorIndex(getFontBoundary(j, 1));
        }
      }
      return getEndIndex();
    }
    
    public int getRunLimit(Set<? extends AttributedCharacterIterator.Attribute> paramSet)
    {
      int i = getEndIndex();
      Object[] arrayOfObject = paramSet.toArray();
      for (int j = 0; j < arrayOfObject.length; j++)
      {
        TextAttribute localTextAttribute = (TextAttribute)arrayOfObject[j];
        i = Math.min(getRunLimit(localTextAttribute), i);
      }
      return Math.max(getIndex(), i);
    }
    
    public Map<AttributedCharacterIterator.Attribute, Object> getAttributes()
    {
      Object[] arrayOfObject = keys.toArray();
      Hashtable localHashtable = new Hashtable();
      for (int i = 0; i < arrayOfObject.length; i++)
      {
        TextAttribute localTextAttribute = (TextAttribute)arrayOfObject[i];
        Object localObject = getAttribute(localTextAttribute);
        if (localObject != null) {
          localHashtable.put(localTextAttribute, localObject);
        }
      }
      return localHashtable;
    }
    
    public Object getAttribute(AttributedCharacterIterator.Attribute paramAttribute)
    {
      int i = toModelPosition(getIndex());
      int j = v.getViewIndex(i, Position.Bias.Forward);
      if (paramAttribute == TextAttribute.FONT) {
        return getFont(j);
      }
      if (paramAttribute == TextAttribute.RUN_DIRECTION) {
        return v.getDocument().getProperty(TextAttribute.RUN_DIRECTION);
      }
      if (paramAttribute == TextAttribute.NUMERIC_SHAPING) {
        return shaper;
      }
      return null;
    }
    
    public Set<AttributedCharacterIterator.Attribute> getAllAttributeKeys()
    {
      return keys;
    }
    
    static
    {
      keys.add(TextAttribute.FONT);
      keys.add(TextAttribute.RUN_DIRECTION);
      keys.add(TextAttribute.NUMERIC_SHAPING);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\TextLayoutStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */