package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.Painter;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthPainter;

class SynthPainterImpl
  extends SynthPainter
{
  private NimbusStyle style;
  
  SynthPainterImpl(NimbusStyle paramNimbusStyle)
  {
    style = paramNimbusStyle;
  }
  
  private void paint(Painter paramPainter, SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, AffineTransform paramAffineTransform)
  {
    if (paramPainter != null)
    {
      Object localObject;
      if ((paramGraphics instanceof Graphics2D))
      {
        localObject = (Graphics2D)paramGraphics;
        if (paramAffineTransform != null) {
          ((Graphics2D)localObject).transform(paramAffineTransform);
        }
        ((Graphics2D)localObject).translate(paramInt1, paramInt2);
        paramPainter.paint((Graphics2D)localObject, paramSynthContext.getComponent(), paramInt3, paramInt4);
        ((Graphics2D)localObject).translate(-paramInt1, -paramInt2);
        if (paramAffineTransform != null) {
          try
          {
            ((Graphics2D)localObject).transform(paramAffineTransform.createInverse());
          }
          catch (NoninvertibleTransformException localNoninvertibleTransformException)
          {
            localNoninvertibleTransformException.printStackTrace();
          }
        }
      }
      else
      {
        localObject = new BufferedImage(paramInt3, paramInt4, 2);
        Graphics2D localGraphics2D = ((BufferedImage)localObject).createGraphics();
        if (paramAffineTransform != null) {
          localGraphics2D.transform(paramAffineTransform);
        }
        paramPainter.paint(localGraphics2D, paramSynthContext.getComponent(), paramInt3, paramInt4);
        localGraphics2D.dispose();
        paramGraphics.drawImage((Image)localObject, paramInt1, paramInt2, null);
        localObject = null;
      }
    }
  }
  
  private void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, AffineTransform paramAffineTransform)
  {
    JComponent localJComponent = paramSynthContext.getComponent();
    Object localObject = localJComponent != null ? localJComponent.getBackground() : null;
    if ((localObject == null) || (((Color)localObject).getAlpha() > 0))
    {
      Painter localPainter = style.getBackgroundPainter(paramSynthContext);
      if (localPainter != null) {
        paint(localPainter, paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramAffineTransform);
      }
    }
  }
  
  private void paintForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, AffineTransform paramAffineTransform)
  {
    Painter localPainter = style.getForegroundPainter(paramSynthContext);
    if (localPainter != null) {
      paint(localPainter, paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramAffineTransform);
    }
  }
  
  private void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, AffineTransform paramAffineTransform)
  {
    Painter localPainter = style.getBorderPainter(paramSynthContext);
    if (localPainter != null) {
      paint(localPainter, paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramAffineTransform);
    }
  }
  
  private void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    JComponent localJComponent = paramSynthContext.getComponent();
    boolean bool = localJComponent.getComponentOrientation().isLeftToRight();
    if ((paramSynthContext.getComponent() instanceof JSlider)) {
      bool = true;
    }
    AffineTransform localAffineTransform;
    if ((paramInt5 == 1) && (bool))
    {
      localAffineTransform = new AffineTransform();
      localAffineTransform.scale(-1.0D, 1.0D);
      localAffineTransform.rotate(Math.toRadians(90.0D));
      paintBackground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, localAffineTransform);
    }
    else if (paramInt5 == 1)
    {
      localAffineTransform = new AffineTransform();
      localAffineTransform.rotate(Math.toRadians(90.0D));
      localAffineTransform.translate(0.0D, -(paramInt1 + paramInt3));
      paintBackground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, localAffineTransform);
    }
    else if ((paramInt5 == 0) && (bool))
    {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
    else
    {
      localAffineTransform = new AffineTransform();
      localAffineTransform.translate(paramInt1, paramInt2);
      localAffineTransform.scale(-1.0D, 1.0D);
      localAffineTransform.translate(-paramInt3, 0.0D);
      paintBackground(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, localAffineTransform);
    }
  }
  
  private void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    JComponent localJComponent = paramSynthContext.getComponent();
    boolean bool = localJComponent.getComponentOrientation().isLeftToRight();
    AffineTransform localAffineTransform;
    if ((paramInt5 == 1) && (bool))
    {
      localAffineTransform = new AffineTransform();
      localAffineTransform.scale(-1.0D, 1.0D);
      localAffineTransform.rotate(Math.toRadians(90.0D));
      paintBorder(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, localAffineTransform);
    }
    else if (paramInt5 == 1)
    {
      localAffineTransform = new AffineTransform();
      localAffineTransform.rotate(Math.toRadians(90.0D));
      localAffineTransform.translate(0.0D, -(paramInt1 + paramInt3));
      paintBorder(paramSynthContext, paramGraphics, paramInt2, 0, paramInt4, paramInt3, localAffineTransform);
    }
    else if ((paramInt5 == 0) && (bool))
    {
      paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
    else
    {
      paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
  }
  
  private void paintForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    JComponent localJComponent = paramSynthContext.getComponent();
    boolean bool = localJComponent.getComponentOrientation().isLeftToRight();
    AffineTransform localAffineTransform;
    if ((paramInt5 == 1) && (bool))
    {
      localAffineTransform = new AffineTransform();
      localAffineTransform.scale(-1.0D, 1.0D);
      localAffineTransform.rotate(Math.toRadians(90.0D));
      paintForeground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, localAffineTransform);
    }
    else if (paramInt5 == 1)
    {
      localAffineTransform = new AffineTransform();
      localAffineTransform.rotate(Math.toRadians(90.0D));
      localAffineTransform.translate(0.0D, -(paramInt1 + paramInt3));
      paintForeground(paramSynthContext, paramGraphics, paramInt2, 0, paramInt4, paramInt3, localAffineTransform);
    }
    else if ((paramInt5 == 0) && (bool))
    {
      paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
    else
    {
      paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
  }
  
  public void paintArrowButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramSynthContext.getComponent().getComponentOrientation().isLeftToRight())
    {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
    else
    {
      AffineTransform localAffineTransform = new AffineTransform();
      localAffineTransform.translate(paramInt1, paramInt2);
      localAffineTransform.scale(-1.0D, 1.0D);
      localAffineTransform.translate(-paramInt3, 0.0D);
      paintBackground(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, localAffineTransform);
    }
  }
  
  public void paintArrowButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintArrowButtonForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    String str = paramSynthContext.getComponent().getName();
    boolean bool = paramSynthContext.getComponent().getComponentOrientation().isLeftToRight();
    AffineTransform localAffineTransform;
    if (("Spinner.nextButton".equals(str)) || ("Spinner.previousButton".equals(str)))
    {
      if (bool)
      {
        paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
      }
      else
      {
        localAffineTransform = new AffineTransform();
        localAffineTransform.translate(paramInt3, 0.0D);
        localAffineTransform.scale(-1.0D, 1.0D);
        paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, localAffineTransform);
      }
    }
    else if (paramInt5 == 7)
    {
      paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
    else if (paramInt5 == 1)
    {
      if (bool)
      {
        localAffineTransform = new AffineTransform();
        localAffineTransform.scale(-1.0D, 1.0D);
        localAffineTransform.rotate(Math.toRadians(90.0D));
        paintForeground(paramSynthContext, paramGraphics, paramInt2, 0, paramInt4, paramInt3, localAffineTransform);
      }
      else
      {
        localAffineTransform = new AffineTransform();
        localAffineTransform.rotate(Math.toRadians(90.0D));
        localAffineTransform.translate(0.0D, -(paramInt1 + paramInt3));
        paintForeground(paramSynthContext, paramGraphics, paramInt2, 0, paramInt4, paramInt3, localAffineTransform);
      }
    }
    else if (paramInt5 == 3)
    {
      localAffineTransform = new AffineTransform();
      localAffineTransform.translate(paramInt3, 0.0D);
      localAffineTransform.scale(-1.0D, 1.0D);
      paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, localAffineTransform);
    }
    else if (paramInt5 == 5)
    {
      if (bool)
      {
        localAffineTransform = new AffineTransform();
        localAffineTransform.rotate(Math.toRadians(-90.0D));
        localAffineTransform.translate(-paramInt4, 0.0D);
        paintForeground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, localAffineTransform);
      }
      else
      {
        localAffineTransform = new AffineTransform();
        localAffineTransform.scale(-1.0D, 1.0D);
        localAffineTransform.rotate(Math.toRadians(-90.0D));
        localAffineTransform.translate(-(paramInt4 + paramInt2), -(paramInt3 + paramInt1));
        paintForeground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, localAffineTransform);
      }
    }
  }
  
  public void paintButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintCheckBoxMenuItemBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintCheckBoxMenuItemBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintCheckBoxBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintCheckBoxBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintColorChooserBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintColorChooserBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintComboBoxBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramSynthContext.getComponent().getComponentOrientation().isLeftToRight())
    {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
    else
    {
      AffineTransform localAffineTransform = new AffineTransform();
      localAffineTransform.translate(paramInt1, paramInt2);
      localAffineTransform.scale(-1.0D, 1.0D);
      localAffineTransform.translate(-paramInt3, 0.0D);
      paintBackground(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, localAffineTransform);
    }
  }
  
  public void paintComboBoxBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintDesktopIconBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintDesktopIconBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintDesktopPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintDesktopPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintEditorPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintEditorPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintFileChooserBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintFileChooserBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintFormattedTextFieldBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramSynthContext.getComponent().getComponentOrientation().isLeftToRight())
    {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
    else
    {
      AffineTransform localAffineTransform = new AffineTransform();
      localAffineTransform.translate(paramInt1, paramInt2);
      localAffineTransform.scale(-1.0D, 1.0D);
      localAffineTransform.translate(-paramInt3, 0.0D);
      paintBackground(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, localAffineTransform);
    }
  }
  
  public void paintFormattedTextFieldBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramSynthContext.getComponent().getComponentOrientation().isLeftToRight())
    {
      paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
    else
    {
      AffineTransform localAffineTransform = new AffineTransform();
      localAffineTransform.translate(paramInt1, paramInt2);
      localAffineTransform.scale(-1.0D, 1.0D);
      localAffineTransform.translate(-paramInt3, 0.0D);
      paintBorder(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, localAffineTransform);
    }
  }
  
  public void paintInternalFrameTitlePaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintInternalFrameTitlePaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintInternalFrameBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintInternalFrameBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintLabelBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintLabelBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintListBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintListBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintMenuBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintMenuBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintMenuItemBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintMenuItemBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintMenuBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintMenuBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintOptionPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintOptionPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintPanelBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintPanelBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintPasswordFieldBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintPasswordFieldBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintPopupMenuBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintPopupMenuBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintProgressBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintProgressBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintProgressBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintProgressBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintProgressBarForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintRadioButtonMenuItemBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintRadioButtonMenuItemBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintRadioButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintRadioButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintRootPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintRootPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintScrollBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintScrollBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintScrollBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintScrollBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintScrollBarThumbBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintScrollBarThumbBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintScrollBarTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintScrollBarTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintScrollBarTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintScrollBarTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintScrollPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintScrollPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintSeparatorBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintSeparatorBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintSeparatorBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintSeparatorBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintSeparatorForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintSliderBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintSliderBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintSliderBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintSliderBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintSliderThumbBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (paramSynthContext.getComponent().getClientProperty("Slider.paintThumbArrowShape") == Boolean.TRUE)
    {
      if (paramInt5 == 0) {
        paramInt5 = 1;
      } else {
        paramInt5 = 0;
      }
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
    else
    {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
    }
  }
  
  public void paintSliderThumbBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintSliderTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintSliderTrackBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintSliderTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintSliderTrackBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintSpinnerBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintSpinnerBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintSplitPaneDividerBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintSplitPaneDividerBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (paramInt5 == 1)
    {
      AffineTransform localAffineTransform = new AffineTransform();
      localAffineTransform.scale(-1.0D, 1.0D);
      localAffineTransform.rotate(Math.toRadians(90.0D));
      paintBackground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, localAffineTransform);
    }
    else
    {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
  }
  
  public void paintSplitPaneDividerForeground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintForeground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintSplitPaneDragDivider(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintSplitPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintSplitPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTabbedPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTabbedPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTabbedPaneTabAreaBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTabbedPaneTabAreaBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    AffineTransform localAffineTransform;
    if (paramInt5 == 2)
    {
      localAffineTransform = new AffineTransform();
      localAffineTransform.scale(-1.0D, 1.0D);
      localAffineTransform.rotate(Math.toRadians(90.0D));
      paintBackground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, localAffineTransform);
    }
    else if (paramInt5 == 4)
    {
      localAffineTransform = new AffineTransform();
      localAffineTransform.rotate(Math.toRadians(90.0D));
      localAffineTransform.translate(0.0D, -(paramInt1 + paramInt3));
      paintBackground(paramSynthContext, paramGraphics, paramInt2, 0, paramInt4, paramInt3, localAffineTransform);
    }
    else if (paramInt5 == 3)
    {
      localAffineTransform = new AffineTransform();
      localAffineTransform.translate(paramInt1, paramInt2);
      localAffineTransform.scale(1.0D, -1.0D);
      localAffineTransform.translate(0.0D, -paramInt4);
      paintBackground(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, localAffineTransform);
    }
    else
    {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
  }
  
  public void paintTabbedPaneTabAreaBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTabbedPaneTabAreaBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTabbedPaneTabBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTabbedPaneTabBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    AffineTransform localAffineTransform;
    if (paramInt6 == 2)
    {
      localAffineTransform = new AffineTransform();
      localAffineTransform.scale(-1.0D, 1.0D);
      localAffineTransform.rotate(Math.toRadians(90.0D));
      paintBackground(paramSynthContext, paramGraphics, paramInt2, paramInt1, paramInt4, paramInt3, localAffineTransform);
    }
    else if (paramInt6 == 4)
    {
      localAffineTransform = new AffineTransform();
      localAffineTransform.rotate(Math.toRadians(90.0D));
      localAffineTransform.translate(0.0D, -(paramInt1 + paramInt3));
      paintBackground(paramSynthContext, paramGraphics, paramInt2, 0, paramInt4, paramInt3, localAffineTransform);
    }
    else if (paramInt6 == 3)
    {
      localAffineTransform = new AffineTransform();
      localAffineTransform.translate(paramInt1, paramInt2);
      localAffineTransform.scale(1.0D, -1.0D);
      localAffineTransform.translate(0.0D, -paramInt4);
      paintBackground(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, localAffineTransform);
    }
    else
    {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
  }
  
  public void paintTabbedPaneTabBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTabbedPaneTabBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTabbedPaneContentBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTabbedPaneContentBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTableHeaderBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTableHeaderBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTableBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTableBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTextAreaBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTextAreaBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTextPaneBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTextPaneBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTextFieldBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramSynthContext.getComponent().getComponentOrientation().isLeftToRight())
    {
      paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
    else
    {
      AffineTransform localAffineTransform = new AffineTransform();
      localAffineTransform.translate(paramInt1, paramInt2);
      localAffineTransform.scale(-1.0D, 1.0D);
      localAffineTransform.translate(-paramInt3, 0.0D);
      paintBackground(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, localAffineTransform);
    }
  }
  
  public void paintTextFieldBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramSynthContext.getComponent().getComponentOrientation().isLeftToRight())
    {
      paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
    }
    else
    {
      AffineTransform localAffineTransform = new AffineTransform();
      localAffineTransform.translate(paramInt1, paramInt2);
      localAffineTransform.scale(-1.0D, 1.0D);
      localAffineTransform.translate(-paramInt3, 0.0D);
      paintBorder(paramSynthContext, paramGraphics, 0, 0, paramInt3, paramInt4, localAffineTransform);
    }
  }
  
  public void paintToggleButtonBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintToggleButtonBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintToolBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintToolBarBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintToolBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintToolBarBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintToolBarContentBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintToolBarContentBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintToolBarContentBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintToolBarContentBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintToolBarDragWindowBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintToolBarDragWindowBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintToolBarDragWindowBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintToolBarDragWindowBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void paintToolTipBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintToolTipBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTreeBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTreeBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTreeCellBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTreeCellBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintTreeCellFocus(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void paintViewportBackground(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBackground(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
  
  public void paintViewportBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\SynthPainterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */