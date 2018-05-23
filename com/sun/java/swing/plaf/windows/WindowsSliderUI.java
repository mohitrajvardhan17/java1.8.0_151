package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.basic.BasicSliderUI.TrackListener;

public class WindowsSliderUI
  extends BasicSliderUI
{
  private boolean rollover = false;
  private boolean pressed = false;
  
  public WindowsSliderUI(JSlider paramJSlider)
  {
    super(paramJSlider);
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsSliderUI((JSlider)paramJComponent);
  }
  
  protected BasicSliderUI.TrackListener createTrackListener(JSlider paramJSlider)
  {
    return new WindowsTrackListener(null);
  }
  
  public void paintTrack(Graphics paramGraphics)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null)
    {
      int i = slider.getOrientation() == 1 ? 1 : 0;
      TMSchema.Part localPart = i != 0 ? TMSchema.Part.TKP_TRACKVERT : TMSchema.Part.TKP_TRACK;
      XPStyle.Skin localSkin = localXPStyle.getSkin(slider, localPart);
      int j;
      if (i != 0)
      {
        j = (trackRect.width - localSkin.getWidth()) / 2;
        localSkin.paintSkin(paramGraphics, trackRect.x + j, trackRect.y, localSkin.getWidth(), trackRect.height, null);
      }
      else
      {
        j = (trackRect.height - localSkin.getHeight()) / 2;
        localSkin.paintSkin(paramGraphics, trackRect.x, trackRect.y + j, trackRect.width, localSkin.getHeight(), null);
      }
    }
    else
    {
      super.paintTrack(paramGraphics);
    }
  }
  
  protected void paintMinorTickForHorizSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null) {
      paramGraphics.setColor(localXPStyle.getColor(slider, TMSchema.Part.TKP_TICS, null, TMSchema.Prop.COLOR, Color.black));
    }
    super.paintMinorTickForHorizSlider(paramGraphics, paramRectangle, paramInt);
  }
  
  protected void paintMajorTickForHorizSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null) {
      paramGraphics.setColor(localXPStyle.getColor(slider, TMSchema.Part.TKP_TICS, null, TMSchema.Prop.COLOR, Color.black));
    }
    super.paintMajorTickForHorizSlider(paramGraphics, paramRectangle, paramInt);
  }
  
  protected void paintMinorTickForVertSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null) {
      paramGraphics.setColor(localXPStyle.getColor(slider, TMSchema.Part.TKP_TICSVERT, null, TMSchema.Prop.COLOR, Color.black));
    }
    super.paintMinorTickForVertSlider(paramGraphics, paramRectangle, paramInt);
  }
  
  protected void paintMajorTickForVertSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null) {
      paramGraphics.setColor(localXPStyle.getColor(slider, TMSchema.Part.TKP_TICSVERT, null, TMSchema.Prop.COLOR, Color.black));
    }
    super.paintMajorTickForVertSlider(paramGraphics, paramRectangle, paramInt);
  }
  
  public void paintThumb(Graphics paramGraphics)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null)
    {
      TMSchema.Part localPart = getXPThumbPart();
      TMSchema.State localState = TMSchema.State.NORMAL;
      if (slider.hasFocus()) {
        localState = TMSchema.State.FOCUSED;
      }
      if (rollover) {
        localState = TMSchema.State.HOT;
      }
      if (pressed) {
        localState = TMSchema.State.PRESSED;
      }
      if (!slider.isEnabled()) {
        localState = TMSchema.State.DISABLED;
      }
      localXPStyle.getSkin(slider, localPart).paintSkin(paramGraphics, thumbRect.x, thumbRect.y, localState);
    }
    else
    {
      super.paintThumb(paramGraphics);
    }
  }
  
  protected Dimension getThumbSize()
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null)
    {
      Dimension localDimension = new Dimension();
      XPStyle.Skin localSkin = localXPStyle.getSkin(slider, getXPThumbPart());
      width = localSkin.getWidth();
      height = localSkin.getHeight();
      return localDimension;
    }
    return super.getThumbSize();
  }
  
  private TMSchema.Part getXPThumbPart()
  {
    int i = slider.getOrientation() == 1 ? 1 : 0;
    boolean bool = slider.getComponentOrientation().isLeftToRight();
    Boolean localBoolean = (Boolean)slider.getClientProperty("Slider.paintThumbArrowShape");
    TMSchema.Part localPart;
    if (((!slider.getPaintTicks()) && (localBoolean == null)) || (localBoolean == Boolean.FALSE)) {
      localPart = i != 0 ? TMSchema.Part.TKP_THUMBVERT : TMSchema.Part.TKP_THUMB;
    } else {
      localPart = i != 0 ? TMSchema.Part.TKP_THUMBLEFT : bool ? TMSchema.Part.TKP_THUMBRIGHT : TMSchema.Part.TKP_THUMBBOTTOM;
    }
    return localPart;
  }
  
  private class WindowsTrackListener
    extends BasicSliderUI.TrackListener
  {
    private WindowsTrackListener()
    {
      super();
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      updateRollover(thumbRect.contains(paramMouseEvent.getX(), paramMouseEvent.getY()));
      super.mouseMoved(paramMouseEvent);
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      updateRollover(thumbRect.contains(paramMouseEvent.getX(), paramMouseEvent.getY()));
      super.mouseEntered(paramMouseEvent);
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      updateRollover(false);
      super.mouseExited(paramMouseEvent);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      updatePressed(thumbRect.contains(paramMouseEvent.getX(), paramMouseEvent.getY()));
      super.mousePressed(paramMouseEvent);
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      updatePressed(false);
      super.mouseReleased(paramMouseEvent);
    }
    
    public void updatePressed(boolean paramBoolean)
    {
      if (!slider.isEnabled()) {
        return;
      }
      if (pressed != paramBoolean)
      {
        pressed = paramBoolean;
        slider.repaint(thumbRect);
      }
    }
    
    public void updateRollover(boolean paramBoolean)
    {
      if (!slider.isEnabled()) {
        return;
      }
      if (rollover != paramBoolean)
      {
        rollover = paramBoolean;
        slider.repaint(thumbRect);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsSliderUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */