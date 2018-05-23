package javax.swing.plaf.synth;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import sun.swing.SwingUtilities2;

public class SynthProgressBarUI
  extends BasicProgressBarUI
  implements SynthUI, PropertyChangeListener
{
  private SynthStyle style;
  private int progressPadding;
  private boolean rotateText;
  private boolean paintOutsideClip;
  private boolean tileWhenIndeterminate;
  private int tileWidth;
  private Dimension minBarSize;
  private int glowWidth;
  
  public SynthProgressBarUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthProgressBarUI();
  }
  
  protected void installListeners()
  {
    super.installListeners();
    progressBar.addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
    progressBar.removePropertyChangeListener(this);
  }
  
  protected void installDefaults()
  {
    updateStyle(progressBar);
  }
  
  private void updateStyle(JProgressBar paramJProgressBar)
  {
    SynthContext localSynthContext = getContext(paramJProgressBar, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    setCellLength(style.getInt(localSynthContext, "ProgressBar.cellLength", 1));
    setCellSpacing(style.getInt(localSynthContext, "ProgressBar.cellSpacing", 0));
    progressPadding = style.getInt(localSynthContext, "ProgressBar.progressPadding", 0);
    paintOutsideClip = style.getBoolean(localSynthContext, "ProgressBar.paintOutsideClip", false);
    rotateText = style.getBoolean(localSynthContext, "ProgressBar.rotateText", false);
    tileWhenIndeterminate = style.getBoolean(localSynthContext, "ProgressBar.tileWhenIndeterminate", false);
    tileWidth = style.getInt(localSynthContext, "ProgressBar.tileWidth", 15);
    String str = (String)progressBar.getClientProperty("JComponent.sizeVariant");
    if (str != null) {
      if ("large".equals(str)) {
        tileWidth = ((int)(tileWidth * 1.15D));
      } else if ("small".equals(str)) {
        tileWidth = ((int)(tileWidth * 0.857D));
      } else if ("mini".equals(str)) {
        tileWidth = ((int)(tileWidth * 0.784D));
      }
    }
    minBarSize = ((Dimension)style.get(localSynthContext, "ProgressBar.minBarSize"));
    glowWidth = style.getInt(localSynthContext, "ProgressBar.glowWidth", 0);
    localSynthContext.dispose();
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(progressBar, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, style, paramInt);
  }
  
  private int getComponentState(JComponent paramJComponent)
  {
    return SynthLookAndFeel.getComponentState(paramJComponent);
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    if ((progressBar.isStringPainted()) && (progressBar.getOrientation() == 0))
    {
      SynthContext localSynthContext = getContext(paramJComponent);
      Font localFont = localSynthContext.getStyle().getFont(localSynthContext);
      FontMetrics localFontMetrics = progressBar.getFontMetrics(localFont);
      localSynthContext.dispose();
      return (paramInt2 - localFontMetrics.getAscent() - localFontMetrics.getDescent()) / 2 + localFontMetrics.getAscent();
    }
    return -1;
  }
  
  protected Rectangle getBox(Rectangle paramRectangle)
  {
    if (tileWhenIndeterminate) {
      return SwingUtilities.calculateInnerArea(progressBar, paramRectangle);
    }
    return super.getBox(paramRectangle);
  }
  
  protected void setAnimationIndex(int paramInt)
  {
    if (paintOutsideClip)
    {
      if (getAnimationIndex() == paramInt) {
        return;
      }
      super.setAnimationIndex(paramInt);
      progressBar.repaint();
    }
    else
    {
      super.setAnimationIndex(paramInt);
    }
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintProgressBarBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), progressBar.getOrientation());
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics)
  {
    JProgressBar localJProgressBar = (JProgressBar)paramSynthContext.getComponent();
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    if (!localJProgressBar.isIndeterminate())
    {
      Insets localInsets = localJProgressBar.getInsets();
      double d2 = localJProgressBar.getPercentComplete();
      if (d2 != 0.0D) {
        if (localJProgressBar.getOrientation() == 0)
        {
          i = left + progressPadding;
          j = top + progressPadding;
          k = (int)(d2 * (localJProgressBar.getWidth() - (left + progressPadding + right + progressPadding)));
          m = localJProgressBar.getHeight() - (top + progressPadding + bottom + progressPadding);
          if (!SynthLookAndFeel.isLeftToRight(localJProgressBar)) {
            i = localJProgressBar.getWidth() - right - k - progressPadding - glowWidth;
          }
        }
        else
        {
          i = left + progressPadding;
          k = localJProgressBar.getWidth() - (left + progressPadding + right + progressPadding);
          m = (int)(d2 * (localJProgressBar.getHeight() - (top + progressPadding + bottom + progressPadding)));
          j = localJProgressBar.getHeight() - bottom - m - progressPadding;
          if (SynthLookAndFeel.isLeftToRight(localJProgressBar)) {
            j -= glowWidth;
          }
        }
      }
    }
    else
    {
      boxRect = getBox(boxRect);
      i = boxRect.x + progressPadding;
      j = boxRect.y + progressPadding;
      k = boxRect.width - progressPadding - progressPadding;
      m = boxRect.height - progressPadding - progressPadding;
    }
    if ((tileWhenIndeterminate) && (localJProgressBar.isIndeterminate()))
    {
      double d1 = getAnimationIndex() / getFrameCount();
      int n = (int)(d1 * tileWidth);
      Shape localShape = paramGraphics.getClip();
      paramGraphics.clipRect(i, j, k, m);
      int i1;
      if (localJProgressBar.getOrientation() == 0)
      {
        i1 = i - tileWidth + n;
        while (i1 <= k)
        {
          paramSynthContext.getPainter().paintProgressBarForeground(paramSynthContext, paramGraphics, i1, j, tileWidth, m, localJProgressBar.getOrientation());
          i1 += tileWidth;
        }
      }
      else
      {
        i1 = j - n;
        while (i1 < m + tileWidth)
        {
          paramSynthContext.getPainter().paintProgressBarForeground(paramSynthContext, paramGraphics, i, i1, k, tileWidth, localJProgressBar.getOrientation());
          i1 += tileWidth;
        }
      }
      paramGraphics.setClip(localShape);
    }
    else if ((minBarSize == null) || ((k >= minBarSize.width) && (m >= minBarSize.height)))
    {
      paramSynthContext.getPainter().paintProgressBarForeground(paramSynthContext, paramGraphics, i, j, k, m, localJProgressBar.getOrientation());
    }
    if (localJProgressBar.isStringPainted()) {
      paintText(paramSynthContext, paramGraphics, localJProgressBar.getString());
    }
  }
  
  protected void paintText(SynthContext paramSynthContext, Graphics paramGraphics, String paramString)
  {
    if (progressBar.isStringPainted())
    {
      SynthStyle localSynthStyle = paramSynthContext.getStyle();
      Font localFont = localSynthStyle.getFont(paramSynthContext);
      FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(progressBar, paramGraphics, localFont);
      int i = localSynthStyle.getGraphicsUtils(paramSynthContext).computeStringWidth(paramSynthContext, localFont, localFontMetrics, paramString);
      Rectangle localRectangle = progressBar.getBounds();
      Object localObject;
      if ((rotateText) && (progressBar.getOrientation() == 1))
      {
        localObject = (Graphics2D)paramGraphics;
        AffineTransform localAffineTransform;
        Point localPoint;
        if (progressBar.getComponentOrientation().isLeftToRight())
        {
          localAffineTransform = AffineTransform.getRotateInstance(-1.5707963267948966D);
          localPoint = new Point((width + localFontMetrics.getAscent() - localFontMetrics.getDescent()) / 2, (height + i) / 2);
        }
        else
        {
          localAffineTransform = AffineTransform.getRotateInstance(1.5707963267948966D);
          localPoint = new Point((width - localFontMetrics.getAscent() + localFontMetrics.getDescent()) / 2, (height - i) / 2);
        }
        if (x < 0) {
          return;
        }
        localFont = localFont.deriveFont(localAffineTransform);
        ((Graphics2D)localObject).setFont(localFont);
        ((Graphics2D)localObject).setColor(localSynthStyle.getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
        localSynthStyle.getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, paramString, x, y, -1);
      }
      else
      {
        localObject = new Rectangle(width / 2 - i / 2, (height - (localFontMetrics.getAscent() + localFontMetrics.getDescent())) / 2, 0, 0);
        if (y < 0) {
          return;
        }
        paramGraphics.setColor(localSynthStyle.getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
        paramGraphics.setFont(localFont);
        localSynthStyle.getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, paramString, x, y, -1);
      }
    }
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintProgressBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, progressBar.getOrientation());
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if ((SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) || ("indeterminate".equals(paramPropertyChangeEvent.getPropertyName()))) {
      updateStyle((JProgressBar)paramPropertyChangeEvent.getSource());
    }
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Dimension localDimension = null;
    Insets localInsets = progressBar.getInsets();
    FontMetrics localFontMetrics = progressBar.getFontMetrics(progressBar.getFont());
    String str1 = progressBar.getString();
    int i = localFontMetrics.getHeight() + localFontMetrics.getDescent();
    int j;
    if (progressBar.getOrientation() == 0)
    {
      localDimension = new Dimension(getPreferredInnerHorizontal());
      if (progressBar.isStringPainted())
      {
        if (i > height) {
          height = i;
        }
        j = SwingUtilities2.stringWidth(progressBar, localFontMetrics, str1);
        if (j > width) {
          width = j;
        }
      }
    }
    else
    {
      localDimension = new Dimension(getPreferredInnerVertical());
      if (progressBar.isStringPainted())
      {
        if (i > width) {
          width = i;
        }
        j = SwingUtilities2.stringWidth(progressBar, localFontMetrics, str1);
        if (j > height) {
          height = j;
        }
      }
    }
    String str2 = (String)progressBar.getClientProperty("JComponent.sizeVariant");
    if (str2 != null) {
      if ("large".equals(str2))
      {
        Dimension tmp221_220 = localDimension;
        221220width = ((int)(221220width * 1.15F));
        Dimension tmp234_233 = localDimension;
        234233height = ((int)(234233height * 1.15F));
      }
      else if ("small".equals(str2))
      {
        Dimension tmp260_259 = localDimension;
        260259width = ((int)(260259width * 0.9F));
        Dimension tmp273_272 = localDimension;
        273272height = ((int)(273272height * 0.9F));
      }
      else if ("mini".equals(str2))
      {
        Dimension tmp299_298 = localDimension;
        299298width = ((int)(299298width * 0.784F));
        Dimension tmp312_311 = localDimension;
        312311height = ((int)(312311height * 0.784F));
      }
    }
    width += left + right;
    height += top + bottom;
    return localDimension;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthProgressBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */