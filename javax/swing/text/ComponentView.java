package javax.swing.text;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import javax.swing.SwingUtilities;

public class ComponentView
  extends View
{
  private Component createdC;
  private Invalidator c;
  
  public ComponentView(Element paramElement)
  {
    super(paramElement);
  }
  
  protected Component createComponent()
  {
    AttributeSet localAttributeSet = getElement().getAttributes();
    Component localComponent = StyleConstants.getComponent(localAttributeSet);
    return localComponent;
  }
  
  public final Component getComponent()
  {
    return createdC;
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape)
  {
    if (c != null)
    {
      Rectangle localRectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
      c.setBounds(x, y, width, height);
    }
  }
  
  public float getPreferredSpan(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("Invalid axis: " + paramInt);
    }
    if (c != null)
    {
      Dimension localDimension = c.getPreferredSize();
      if (paramInt == 0) {
        return width;
      }
      return height;
    }
    return 0.0F;
  }
  
  public float getMinimumSpan(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("Invalid axis: " + paramInt);
    }
    if (c != null)
    {
      Dimension localDimension = c.getMinimumSize();
      if (paramInt == 0) {
        return width;
      }
      return height;
    }
    return 0.0F;
  }
  
  public float getMaximumSpan(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("Invalid axis: " + paramInt);
    }
    if (c != null)
    {
      Dimension localDimension = c.getMaximumSize();
      if (paramInt == 0) {
        return width;
      }
      return height;
    }
    return 0.0F;
  }
  
  public float getAlignment(int paramInt)
  {
    if (c != null) {
      switch (paramInt)
      {
      case 0: 
        return c.getAlignmentX();
      case 1: 
        return c.getAlignmentY();
      }
    }
    return super.getAlignment(paramInt);
  }
  
  public void setParent(View paramView)
  {
    super.setParent(paramView);
    if (SwingUtilities.isEventDispatchThread())
    {
      setComponentParent();
    }
    else
    {
      Runnable local1 = new Runnable()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: aload_0
          //   1: getfield 49	javax/swing/text/ComponentView$1:this$0	Ljavax/swing/text/ComponentView;
          //   4: invokevirtual 56	javax/swing/text/ComponentView:getDocument	()Ljavax/swing/text/Document;
          //   7: astore_1
          //   8: aload_1
          //   9: instanceof 29
          //   12: ifeq +10 -> 22
          //   15: aload_1
          //   16: checkcast 29	javax/swing/text/AbstractDocument
          //   19: invokevirtual 52	javax/swing/text/AbstractDocument:readLock	()V
          //   22: aload_0
          //   23: getfield 49	javax/swing/text/ComponentView$1:this$0	Ljavax/swing/text/ComponentView;
          //   26: invokevirtual 54	javax/swing/text/ComponentView:setComponentParent	()V
          //   29: aload_0
          //   30: getfield 49	javax/swing/text/ComponentView$1:this$0	Ljavax/swing/text/ComponentView;
          //   33: invokevirtual 55	javax/swing/text/ComponentView:getContainer	()Ljava/awt/Container;
          //   36: astore_2
          //   37: aload_2
          //   38: ifnull +17 -> 55
          //   41: aload_0
          //   42: getfield 49	javax/swing/text/ComponentView$1:this$0	Ljavax/swing/text/ComponentView;
          //   45: aconst_null
          //   46: iconst_1
          //   47: iconst_1
          //   48: invokevirtual 57	javax/swing/text/ComponentView:preferenceChanged	(Ljavax/swing/text/View;ZZ)V
          //   51: aload_2
          //   52: invokevirtual 50	java/awt/Container:repaint	()V
          //   55: aload_1
          //   56: instanceof 29
          //   59: ifeq +30 -> 89
          //   62: aload_1
          //   63: checkcast 29	javax/swing/text/AbstractDocument
          //   66: invokevirtual 53	javax/swing/text/AbstractDocument:readUnlock	()V
          //   69: goto +20 -> 89
          //   72: astore_3
          //   73: aload_1
          //   74: instanceof 29
          //   77: ifeq +10 -> 87
          //   80: aload_1
          //   81: checkcast 29	javax/swing/text/AbstractDocument
          //   84: invokevirtual 53	javax/swing/text/AbstractDocument:readUnlock	()V
          //   87: aload_3
          //   88: athrow
          //   89: return
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	90	0	this	1
          //   7	74	1	localDocument	Document
          //   36	16	2	localContainer	Container
          //   72	16	3	localObject	Object
          // Exception table:
          //   from	to	target	type
          //   8	55	72	finally
        }
      };
      SwingUtilities.invokeLater(local1);
    }
  }
  
  void setComponentParent()
  {
    View localView = getParent();
    Container localContainer;
    if (localView != null)
    {
      localContainer = getContainer();
      if (localContainer != null)
      {
        if (c == null)
        {
          Component localComponent = createComponent();
          if (localComponent != null)
          {
            createdC = localComponent;
            c = new Invalidator(localComponent);
          }
        }
        if ((c != null) && (c.getParent() == null))
        {
          localContainer.add(c, this);
          localContainer.addPropertyChangeListener("enabled", c);
        }
      }
    }
    else if (c != null)
    {
      localContainer = c.getParent();
      if (localContainer != null)
      {
        localContainer.remove(c);
        localContainer.removePropertyChangeListener("enabled", c);
      }
    }
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias)
    throws BadLocationException
  {
    int i = getStartOffset();
    int j = getEndOffset();
    if ((paramInt >= i) && (paramInt <= j))
    {
      Rectangle localRectangle = paramShape.getBounds();
      if (paramInt == j) {
        x += width;
      }
      width = 0;
      return localRectangle;
    }
    throw new BadLocationException(paramInt + " not in range " + i + "," + j, paramInt);
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias)
  {
    Rectangle localRectangle = (Rectangle)paramShape;
    if (paramFloat1 < x + width / 2)
    {
      paramArrayOfBias[0] = Position.Bias.Forward;
      return getStartOffset();
    }
    paramArrayOfBias[0] = Position.Bias.Backward;
    return getEndOffset();
  }
  
  class Invalidator
    extends Container
    implements PropertyChangeListener
  {
    Dimension min;
    Dimension pref;
    Dimension max;
    float yalign;
    float xalign;
    
    Invalidator(Component paramComponent)
    {
      setLayout(null);
      add(paramComponent);
      cacheChildSizes();
    }
    
    public void invalidate()
    {
      super.invalidate();
      if (getParent() != null) {
        preferenceChanged(null, true, true);
      }
    }
    
    public void doLayout()
    {
      cacheChildSizes();
    }
    
    public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
      if (getComponentCount() > 0) {
        getComponent(0).setSize(paramInt3, paramInt4);
      }
      cacheChildSizes();
    }
    
    public void validateIfNecessary()
    {
      if (!isValid()) {
        validate();
      }
    }
    
    private void cacheChildSizes()
    {
      if (getComponentCount() > 0)
      {
        Component localComponent = getComponent(0);
        min = localComponent.getMinimumSize();
        pref = localComponent.getPreferredSize();
        max = localComponent.getMaximumSize();
        yalign = localComponent.getAlignmentY();
        xalign = localComponent.getAlignmentX();
      }
      else
      {
        min = (pref = max = new Dimension(0, 0));
      }
    }
    
    public void setVisible(boolean paramBoolean)
    {
      super.setVisible(paramBoolean);
      if (getComponentCount() > 0) {
        getComponent(0).setVisible(paramBoolean);
      }
    }
    
    public boolean isShowing()
    {
      return true;
    }
    
    public Dimension getMinimumSize()
    {
      validateIfNecessary();
      return min;
    }
    
    public Dimension getPreferredSize()
    {
      validateIfNecessary();
      return pref;
    }
    
    public Dimension getMaximumSize()
    {
      validateIfNecessary();
      return max;
    }
    
    public float getAlignmentX()
    {
      validateIfNecessary();
      return xalign;
    }
    
    public float getAlignmentY()
    {
      validateIfNecessary();
      return yalign;
    }
    
    public Set<AWTKeyStroke> getFocusTraversalKeys(int paramInt)
    {
      return KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(paramInt);
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      Boolean localBoolean = (Boolean)paramPropertyChangeEvent.getNewValue();
      if (getComponentCount() > 0) {
        getComponent(0).setEnabled(localBoolean.booleanValue());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\ComponentView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */