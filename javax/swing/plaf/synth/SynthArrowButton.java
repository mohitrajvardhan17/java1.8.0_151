package javax.swing.plaf.synth;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.UIResource;

class SynthArrowButton
  extends JButton
  implements SwingConstants, UIResource
{
  private int direction;
  
  public SynthArrowButton(int paramInt)
  {
    super.setFocusable(false);
    setDirection(paramInt);
    setDefaultCapable(false);
  }
  
  public String getUIClassID()
  {
    return "ArrowButtonUI";
  }
  
  public void updateUI()
  {
    setUI(new SynthArrowButtonUI(null));
  }
  
  public void setDirection(int paramInt)
  {
    direction = paramInt;
    putClientProperty("__arrow_direction__", Integer.valueOf(paramInt));
    repaint();
  }
  
  public int getDirection()
  {
    return direction;
  }
  
  public void setFocusable(boolean paramBoolean) {}
  
  private static class SynthArrowButtonUI
    extends SynthButtonUI
  {
    private SynthArrowButtonUI() {}
    
    protected void installDefaults(AbstractButton paramAbstractButton)
    {
      super.installDefaults(paramAbstractButton);
      updateStyle(paramAbstractButton);
    }
    
    protected void paint(SynthContext paramSynthContext, Graphics paramGraphics)
    {
      SynthArrowButton localSynthArrowButton = (SynthArrowButton)paramSynthContext.getComponent();
      paramSynthContext.getPainter().paintArrowButtonForeground(paramSynthContext, paramGraphics, 0, 0, localSynthArrowButton.getWidth(), localSynthArrowButton.getHeight(), localSynthArrowButton.getDirection());
    }
    
    void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent)
    {
      paramSynthContext.getPainter().paintArrowButtonBackground(paramSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    }
    
    public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramSynthContext.getPainter().paintArrowButtonBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public Dimension getMinimumSize()
    {
      return new Dimension(5, 5);
    }
    
    public Dimension getMaximumSize()
    {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    public Dimension getPreferredSize(JComponent paramJComponent)
    {
      SynthContext localSynthContext = getContext(paramJComponent);
      Dimension localDimension = null;
      if (localSynthContext.getComponent().getName() == "ScrollBar.button") {
        localDimension = (Dimension)localSynthContext.getStyle().get(localSynthContext, "ScrollBar.buttonSize");
      }
      if (localDimension == null)
      {
        int i = localSynthContext.getStyle().getInt(localSynthContext, "ArrowButton.size", 16);
        localDimension = new Dimension(i, i);
      }
      Container localContainer = localSynthContext.getComponent().getParent();
      if (((localContainer instanceof JComponent)) && (!(localContainer instanceof JComboBox)))
      {
        Object localObject = ((JComponent)localContainer).getClientProperty("JComponent.sizeVariant");
        if (localObject != null) {
          if ("large".equals(localObject)) {
            localDimension = new Dimension((int)(width * 1.15D), (int)(height * 1.15D));
          } else if ("small".equals(localObject)) {
            localDimension = new Dimension((int)(width * 0.857D), (int)(height * 0.857D));
          } else if ("mini".equals(localObject)) {
            localDimension = new Dimension((int)(width * 0.714D), (int)(height * 0.714D));
          }
        }
      }
      localSynthContext.dispose();
      return localDimension;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthArrowButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */