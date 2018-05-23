package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.ElementChange;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BoxView;
import javax.swing.text.CompositeView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.GlyphView;
import javax.swing.text.JTextComponent;
import javax.swing.text.ParagraphView;
import javax.swing.text.PlainView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.WrappedPlainView;

public class BasicTextAreaUI
  extends BasicTextUI
{
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicTextAreaUI();
  }
  
  public BasicTextAreaUI() {}
  
  protected String getPropertyPrefix()
  {
    return "TextArea";
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
  }
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    super.propertyChange(paramPropertyChangeEvent);
    if ((paramPropertyChangeEvent.getPropertyName().equals("lineWrap")) || (paramPropertyChangeEvent.getPropertyName().equals("wrapStyleWord")) || (paramPropertyChangeEvent.getPropertyName().equals("tabSize"))) {
      modelChanged();
    } else if ("editable".equals(paramPropertyChangeEvent.getPropertyName())) {
      updateFocusTraversalKeys();
    }
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    return super.getPreferredSize(paramJComponent);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    return super.getMinimumSize(paramJComponent);
  }
  
  public View create(Element paramElement)
  {
    Document localDocument = paramElement.getDocument();
    Object localObject1 = localDocument.getProperty("i18n");
    if ((localObject1 != null) && (localObject1.equals(Boolean.TRUE))) {
      return createI18N(paramElement);
    }
    JTextComponent localJTextComponent = getComponent();
    if ((localJTextComponent instanceof JTextArea))
    {
      JTextArea localJTextArea = (JTextArea)localJTextComponent;
      Object localObject2;
      if (localJTextArea.getLineWrap()) {
        localObject2 = new WrappedPlainView(paramElement, localJTextArea.getWrapStyleWord());
      } else {
        localObject2 = new PlainView(paramElement);
      }
      return (View)localObject2;
    }
    return null;
  }
  
  View createI18N(Element paramElement)
  {
    String str = paramElement.getName();
    if (str != null)
    {
      if (str.equals("content")) {
        return new PlainParagraph(paramElement);
      }
      if (str.equals("paragraph")) {
        return new BoxView(paramElement, 1);
      }
    }
    return null;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    Object localObject1 = ((JTextComponent)paramJComponent).getDocument().getProperty("i18n");
    Insets localInsets = paramJComponent.getInsets();
    if (Boolean.TRUE.equals(localObject1))
    {
      localObject2 = getRootView((JTextComponent)paramJComponent);
      if (((View)localObject2).getViewCount() > 0)
      {
        paramInt2 = paramInt2 - top - bottom;
        int i = top;
        int j = BasicHTML.getBaseline(((View)localObject2).getView(0), paramInt1 - left - right, paramInt2);
        if (j < 0) {
          return -1;
        }
        return i + j;
      }
      return -1;
    }
    Object localObject2 = paramJComponent.getFontMetrics(paramJComponent.getFont());
    return top + ((FontMetrics)localObject2).getAscent();
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    super.getBaselineResizeBehavior(paramJComponent);
    return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
  }
  
  static class PlainParagraph
    extends ParagraphView
  {
    PlainParagraph(Element paramElement)
    {
      super();
      layoutPool = new LogicalView(paramElement);
      layoutPool.setParent(this);
    }
    
    public void setParent(View paramView)
    {
      super.setParent(paramView);
      if (paramView != null) {
        setPropertiesFromAttributes();
      }
    }
    
    protected void setPropertiesFromAttributes()
    {
      Container localContainer = getContainer();
      if ((localContainer != null) && (!localContainer.getComponentOrientation().isLeftToRight())) {
        setJustification(2);
      } else {
        setJustification(0);
      }
    }
    
    public int getFlowSpan(int paramInt)
    {
      Container localContainer = getContainer();
      if ((localContainer instanceof JTextArea))
      {
        JTextArea localJTextArea = (JTextArea)localContainer;
        if (!localJTextArea.getLineWrap()) {
          return Integer.MAX_VALUE;
        }
      }
      return super.getFlowSpan(paramInt);
    }
    
    protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements)
    {
      SizeRequirements localSizeRequirements = super.calculateMinorAxisRequirements(paramInt, paramSizeRequirements);
      Container localContainer = getContainer();
      if ((localContainer instanceof JTextArea))
      {
        JTextArea localJTextArea = (JTextArea)localContainer;
        if (!localJTextArea.getLineWrap())
        {
          minimum = preferred;
        }
        else
        {
          minimum = 0;
          preferred = getWidth();
          if (preferred == Integer.MAX_VALUE) {
            preferred = 100;
          }
        }
      }
      return localSizeRequirements;
    }
    
    public void setSize(float paramFloat1, float paramFloat2)
    {
      if ((int)paramFloat1 != getWidth()) {
        preferenceChanged(null, true, true);
      }
      super.setSize(paramFloat1, paramFloat2);
    }
    
    static class LogicalView
      extends CompositeView
    {
      LogicalView(Element paramElement)
      {
        super();
      }
      
      protected int getViewIndexAtPosition(int paramInt)
      {
        Element localElement = getElement();
        if (localElement.getElementCount() > 0) {
          return localElement.getElementIndex(paramInt);
        }
        return 0;
      }
      
      protected boolean updateChildren(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, ViewFactory paramViewFactory)
      {
        return false;
      }
      
      protected void loadChildren(ViewFactory paramViewFactory)
      {
        Element localElement = getElement();
        if (localElement.getElementCount() > 0)
        {
          super.loadChildren(paramViewFactory);
        }
        else
        {
          GlyphView localGlyphView = new GlyphView(localElement);
          append(localGlyphView);
        }
      }
      
      public float getPreferredSpan(int paramInt)
      {
        if (getViewCount() != 1) {
          throw new Error("One child view is assumed.");
        }
        View localView = getView(0);
        return localView.getPreferredSpan(paramInt);
      }
      
      protected void forwardUpdateToView(View paramView, DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory)
      {
        paramView.setParent(this);
        super.forwardUpdateToView(paramView, paramDocumentEvent, paramShape, paramViewFactory);
      }
      
      public void paint(Graphics paramGraphics, Shape paramShape) {}
      
      protected boolean isBefore(int paramInt1, int paramInt2, Rectangle paramRectangle)
      {
        return false;
      }
      
      protected boolean isAfter(int paramInt1, int paramInt2, Rectangle paramRectangle)
      {
        return false;
      }
      
      protected View getViewAtPoint(int paramInt1, int paramInt2, Rectangle paramRectangle)
      {
        return null;
      }
      
      protected void childAllocation(int paramInt, Rectangle paramRectangle) {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicTextAreaUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */