package com.sun.java.swing.plaf.windows;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class WindowsProgressBarUI
  extends BasicProgressBarUI
{
  private Rectangle previousFullBox;
  private Insets indeterminateInsets;
  
  public WindowsProgressBarUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsProgressBarUI();
  }
  
  protected void installDefaults()
  {
    super.installDefaults();
    if (XPStyle.getXP() != null)
    {
      LookAndFeel.installProperty(progressBar, "opaque", Boolean.FALSE);
      progressBar.setBorder(null);
      indeterminateInsets = UIManager.getInsets("ProgressBar.indeterminateInsets");
    }
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    int i = super.getBaseline(paramJComponent, paramInt1, paramInt2);
    if ((XPStyle.getXP() != null) && (progressBar.isStringPainted()) && (progressBar.getOrientation() == 0))
    {
      FontMetrics localFontMetrics = progressBar.getFontMetrics(progressBar.getFont());
      int j = progressBar.getInsets().top;
      if (progressBar.isIndeterminate())
      {
        j = -1;
        paramInt2--;
      }
      else
      {
        j = 0;
        paramInt2 -= 3;
      }
      i = j + (paramInt2 + localFontMetrics.getAscent() - localFontMetrics.getLeading() - localFontMetrics.getDescent()) / 2;
    }
    return i;
  }
  
  protected Dimension getPreferredInnerHorizontal()
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null)
    {
      XPStyle.Skin localSkin = localXPStyle.getSkin(progressBar, TMSchema.Part.PP_BAR);
      return new Dimension((int)super.getPreferredInnerHorizontal().getWidth(), localSkin.getHeight());
    }
    return super.getPreferredInnerHorizontal();
  }
  
  protected Dimension getPreferredInnerVertical()
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null)
    {
      XPStyle.Skin localSkin = localXPStyle.getSkin(progressBar, TMSchema.Part.PP_BARVERT);
      return new Dimension(localSkin.getWidth(), (int)super.getPreferredInnerVertical().getHeight());
    }
    return super.getPreferredInnerVertical();
  }
  
  protected void paintDeterminate(Graphics paramGraphics, JComponent paramJComponent)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null)
    {
      boolean bool1 = progressBar.getOrientation() == 1;
      boolean bool2 = WindowsGraphicsUtils.isLeftToRight(paramJComponent);
      int i = progressBar.getWidth();
      int j = progressBar.getHeight() - 1;
      int k = getAmountFull(null, i, j);
      paintXPBackground(paramGraphics, bool1, i, j);
      Object localObject;
      if (progressBar.isStringPainted())
      {
        paramGraphics.setColor(progressBar.getForeground());
        j -= 2;
        i -= 2;
        if ((i <= 0) || (j <= 0)) {
          return;
        }
        localObject = (Graphics2D)paramGraphics;
        ((Graphics2D)localObject).setStroke(new BasicStroke(bool1 ? i : j, 0, 2));
        if (!bool1)
        {
          if (bool2) {
            ((Graphics2D)localObject).drawLine(2, j / 2 + 1, k - 2, j / 2 + 1);
          } else {
            ((Graphics2D)localObject).drawLine(2 + i, j / 2 + 1, 2 + i - (k - 2), j / 2 + 1);
          }
          paintString(paramGraphics, 0, 0, i, j, k, null);
        }
        else
        {
          ((Graphics2D)localObject).drawLine(i / 2 + 1, j + 1, i / 2 + 1, j + 1 - k + 2);
          paintString(paramGraphics, 2, 2, i, j, k, null);
        }
      }
      else
      {
        localObject = localXPStyle.getSkin(progressBar, bool1 ? TMSchema.Part.PP_CHUNKVERT : TMSchema.Part.PP_CHUNK);
        int m;
        if (bool1) {
          m = i - 5;
        } else {
          m = j - 5;
        }
        int n = localXPStyle.getInt(progressBar, TMSchema.Part.PP_PROGRESS, null, TMSchema.Prop.PROGRESSCHUNKSIZE, 2);
        int i1 = localXPStyle.getInt(progressBar, TMSchema.Part.PP_PROGRESS, null, TMSchema.Prop.PROGRESSSPACESIZE, 0);
        int i2 = (k - 4) / (n + i1);
        if ((i1 > 0) && (i2 * (n + i1) + n < k - 4)) {
          i2++;
        }
        for (int i3 = 0; i3 < i2; i3++) {
          if (bool1) {
            ((XPStyle.Skin)localObject).paintSkin(paramGraphics, 3, j - i3 * (n + i1) - n - 2, m, n, null);
          } else if (bool2) {
            ((XPStyle.Skin)localObject).paintSkin(paramGraphics, 4 + i3 * (n + i1), 2, n, m, null);
          } else {
            ((XPStyle.Skin)localObject).paintSkin(paramGraphics, i - (2 + (i3 + 1) * (n + i1)), 2, n, m, null);
          }
        }
      }
    }
    else
    {
      super.paintDeterminate(paramGraphics, paramJComponent);
    }
  }
  
  protected void setAnimationIndex(int paramInt)
  {
    super.setAnimationIndex(paramInt);
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null) {
      if (boxRect != null)
      {
        Rectangle localRectangle = getFullChunkBounds(boxRect);
        if (previousFullBox != null) {
          localRectangle.add(previousFullBox);
        }
        progressBar.repaint(localRectangle);
      }
      else
      {
        progressBar.repaint();
      }
    }
  }
  
  protected int getBoxLength(int paramInt1, int paramInt2)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null) {
      return 6;
    }
    return super.getBoxLength(paramInt1, paramInt2);
  }
  
  protected Rectangle getBox(Rectangle paramRectangle)
  {
    Rectangle localRectangle = super.getBox(paramRectangle);
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null)
    {
      int i = progressBar.getOrientation() == 1 ? 1 : 0;
      TMSchema.Part localPart = i != 0 ? TMSchema.Part.PP_BARVERT : TMSchema.Part.PP_BAR;
      Insets localInsets = indeterminateInsets;
      int j = getAnimationIndex();
      int k = getFrameCount() / 2;
      int m = localXPStyle.getInt(progressBar, TMSchema.Part.PP_PROGRESS, null, TMSchema.Prop.PROGRESSSPACESIZE, 0);
      j %= k;
      int n;
      double d;
      if (i == 0)
      {
        y += top;
        height = (progressBar.getHeight() - top - bottom);
        n = progressBar.getWidth() - left - right;
        n += (width + m) * 2;
        d = n / k;
        x = ((int)(d * j) + left);
      }
      else
      {
        x += left;
        width = (progressBar.getWidth() - left - right);
        n = progressBar.getHeight() - top - bottom;
        n += (height + m) * 2;
        d = n / k;
        y = ((int)(d * j) + top);
      }
    }
    return localRectangle;
  }
  
  protected void paintIndeterminate(Graphics paramGraphics, JComponent paramJComponent)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null)
    {
      boolean bool = progressBar.getOrientation() == 1;
      int i = progressBar.getWidth();
      int j = progressBar.getHeight();
      paintXPBackground(paramGraphics, bool, i, j);
      boxRect = getBox(boxRect);
      if (boxRect != null)
      {
        paramGraphics.setColor(progressBar.getForeground());
        if (!(paramGraphics instanceof Graphics2D)) {
          return;
        }
        paintIndeterminateFrame(boxRect, (Graphics2D)paramGraphics, bool, i, j);
        if (progressBar.isStringPainted()) {
          if (!bool) {
            paintString(paramGraphics, -1, -1, i, j, 0, null);
          } else {
            paintString(paramGraphics, 1, 1, i, j, 0, null);
          }
        }
      }
    }
    else
    {
      super.paintIndeterminate(paramGraphics, paramJComponent);
    }
  }
  
  private Rectangle getFullChunkBounds(Rectangle paramRectangle)
  {
    int i = progressBar.getOrientation() == 1 ? 1 : 0;
    XPStyle localXPStyle = XPStyle.getXP();
    int j = localXPStyle != null ? localXPStyle.getInt(progressBar, TMSchema.Part.PP_PROGRESS, null, TMSchema.Prop.PROGRESSSPACESIZE, 0) : 0;
    if (i == 0)
    {
      k = width + j;
      return new Rectangle(x - k * 2, y, k * 3, height);
    }
    int k = height + j;
    return new Rectangle(x, y - k * 2, width, k * 3);
  }
  
  private void paintIndeterminateFrame(Rectangle paramRectangle, Graphics2D paramGraphics2D, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle == null) {
      return;
    }
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics2D.create();
    TMSchema.Part localPart1 = paramBoolean ? TMSchema.Part.PP_BARVERT : TMSchema.Part.PP_BAR;
    TMSchema.Part localPart2 = paramBoolean ? TMSchema.Part.PP_CHUNKVERT : TMSchema.Part.PP_CHUNK;
    int i = localXPStyle.getInt(progressBar, TMSchema.Part.PP_PROGRESS, null, TMSchema.Prop.PROGRESSSPACESIZE, 0);
    int j = 0;
    int k = 0;
    if (!paramBoolean)
    {
      j = -width - i;
      k = 0;
    }
    else
    {
      j = 0;
      k = -height - i;
    }
    Rectangle localRectangle1 = getFullChunkBounds(paramRectangle);
    previousFullBox = localRectangle1;
    Insets localInsets = indeterminateInsets;
    Rectangle localRectangle2 = new Rectangle(left, top, paramInt1 - left - right, paramInt2 - top - bottom);
    Rectangle localRectangle3 = localRectangle2.intersection(localRectangle1);
    localGraphics2D.clip(localRectangle3);
    XPStyle.Skin localSkin = localXPStyle.getSkin(progressBar, localPart2);
    localGraphics2D.setComposite(AlphaComposite.getInstance(3, 0.8F));
    localSkin.paintSkin(localGraphics2D, x, y, width, height, null);
    paramRectangle.translate(j, k);
    localGraphics2D.setComposite(AlphaComposite.getInstance(3, 0.5F));
    localSkin.paintSkin(localGraphics2D, x, y, width, height, null);
    paramRectangle.translate(j, k);
    localGraphics2D.setComposite(AlphaComposite.getInstance(3, 0.2F));
    localSkin.paintSkin(localGraphics2D, x, y, width, height, null);
    localGraphics2D.dispose();
  }
  
  private void paintXPBackground(Graphics paramGraphics, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle == null) {
      return;
    }
    TMSchema.Part localPart = paramBoolean ? TMSchema.Part.PP_BARVERT : TMSchema.Part.PP_BAR;
    XPStyle.Skin localSkin = localXPStyle.getSkin(progressBar, localPart);
    localSkin.paintSkin(paramGraphics, 0, 0, paramInt1, paramInt2, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\swing\plaf\windows\WindowsProgressBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */