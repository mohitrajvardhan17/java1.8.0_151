package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sun.swing.SwingUtilities2;

class DefaultPreviewPanel
  extends JPanel
{
  private int squareSize = 25;
  private int squareGap = 5;
  private int innerGap = 5;
  private int textGap = 5;
  private Font font = new Font("Dialog", 0, 12);
  private String sampleText;
  private int swatchWidth = 50;
  private Color oldColor = null;
  
  DefaultPreviewPanel() {}
  
  private JColorChooser getColorChooser()
  {
    return (JColorChooser)SwingUtilities.getAncestorOfClass(JColorChooser.class, this);
  }
  
  public Dimension getPreferredSize()
  {
    Object localObject = getColorChooser();
    if (localObject == null) {
      localObject = this;
    }
    FontMetrics localFontMetrics = ((JComponent)localObject).getFontMetrics(getFont());
    int i = localFontMetrics.getAscent();
    int j = localFontMetrics.getHeight();
    int k = SwingUtilities2.stringWidth((JComponent)localObject, localFontMetrics, getSampleText());
    int m = j * 3 + textGap * 3;
    int n = squareSize * 3 + squareGap * 2 + swatchWidth + k + textGap * 3;
    return new Dimension(n, m);
  }
  
  public void paintComponent(Graphics paramGraphics)
  {
    if (oldColor == null) {
      oldColor = getForeground();
    }
    paramGraphics.setColor(getBackground());
    paramGraphics.fillRect(0, 0, getWidth(), getHeight());
    int i;
    int j;
    if (getComponentOrientation().isLeftToRight())
    {
      i = paintSquares(paramGraphics, 0);
      j = paintText(paramGraphics, i);
      paintSwatch(paramGraphics, i + j);
    }
    else
    {
      i = paintSwatch(paramGraphics, 0);
      j = paintText(paramGraphics, i);
      paintSquares(paramGraphics, i + j);
    }
  }
  
  private int paintSwatch(Graphics paramGraphics, int paramInt)
  {
    int i = paramInt;
    paramGraphics.setColor(oldColor);
    paramGraphics.fillRect(i, 0, swatchWidth, squareSize + squareGap / 2);
    paramGraphics.setColor(getForeground());
    paramGraphics.fillRect(i, squareSize + squareGap / 2, swatchWidth, squareSize + squareGap / 2);
    return i + swatchWidth;
  }
  
  private int paintText(Graphics paramGraphics, int paramInt)
  {
    paramGraphics.setFont(getFont());
    Object localObject = getColorChooser();
    if (localObject == null) {
      localObject = this;
    }
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics((JComponent)localObject, paramGraphics);
    int i = localFontMetrics.getAscent();
    int j = localFontMetrics.getHeight();
    int k = SwingUtilities2.stringWidth((JComponent)localObject, localFontMetrics, getSampleText());
    int m = paramInt + textGap;
    Color localColor = getForeground();
    paramGraphics.setColor(localColor);
    SwingUtilities2.drawString((JComponent)localObject, paramGraphics, getSampleText(), m + textGap / 2, i + 2);
    paramGraphics.fillRect(m, j + textGap, k + textGap, j + 2);
    paramGraphics.setColor(Color.black);
    SwingUtilities2.drawString((JComponent)localObject, paramGraphics, getSampleText(), m + textGap / 2, j + i + textGap + 2);
    paramGraphics.setColor(Color.white);
    paramGraphics.fillRect(m, (j + textGap) * 2, k + textGap, j + 2);
    paramGraphics.setColor(localColor);
    SwingUtilities2.drawString((JComponent)localObject, paramGraphics, getSampleText(), m + textGap / 2, (j + textGap) * 2 + i + 2);
    return k + textGap * 3;
  }
  
  private int paintSquares(Graphics paramGraphics, int paramInt)
  {
    int i = paramInt;
    Color localColor = getForeground();
    paramGraphics.setColor(Color.white);
    paramGraphics.fillRect(i, 0, squareSize, squareSize);
    paramGraphics.setColor(localColor);
    paramGraphics.fillRect(i + innerGap, innerGap, squareSize - innerGap * 2, squareSize - innerGap * 2);
    paramGraphics.setColor(Color.white);
    paramGraphics.fillRect(i + innerGap * 2, innerGap * 2, squareSize - innerGap * 4, squareSize - innerGap * 4);
    paramGraphics.setColor(localColor);
    paramGraphics.fillRect(i, squareSize + squareGap, squareSize, squareSize);
    paramGraphics.translate(squareSize + squareGap, 0);
    paramGraphics.setColor(Color.black);
    paramGraphics.fillRect(i, 0, squareSize, squareSize);
    paramGraphics.setColor(localColor);
    paramGraphics.fillRect(i + innerGap, innerGap, squareSize - innerGap * 2, squareSize - innerGap * 2);
    paramGraphics.setColor(Color.white);
    paramGraphics.fillRect(i + innerGap * 2, innerGap * 2, squareSize - innerGap * 4, squareSize - innerGap * 4);
    paramGraphics.translate(-(squareSize + squareGap), 0);
    paramGraphics.translate(squareSize + squareGap, squareSize + squareGap);
    paramGraphics.setColor(Color.white);
    paramGraphics.fillRect(i, 0, squareSize, squareSize);
    paramGraphics.setColor(localColor);
    paramGraphics.fillRect(i + innerGap, innerGap, squareSize - innerGap * 2, squareSize - innerGap * 2);
    paramGraphics.translate(-(squareSize + squareGap), -(squareSize + squareGap));
    paramGraphics.translate((squareSize + squareGap) * 2, 0);
    paramGraphics.setColor(Color.white);
    paramGraphics.fillRect(i, 0, squareSize, squareSize);
    paramGraphics.setColor(localColor);
    paramGraphics.fillRect(i + innerGap, innerGap, squareSize - innerGap * 2, squareSize - innerGap * 2);
    paramGraphics.setColor(Color.black);
    paramGraphics.fillRect(i + innerGap * 2, innerGap * 2, squareSize - innerGap * 4, squareSize - innerGap * 4);
    paramGraphics.translate(-((squareSize + squareGap) * 2), 0);
    paramGraphics.translate((squareSize + squareGap) * 2, squareSize + squareGap);
    paramGraphics.setColor(Color.black);
    paramGraphics.fillRect(i, 0, squareSize, squareSize);
    paramGraphics.setColor(localColor);
    paramGraphics.fillRect(i + innerGap, innerGap, squareSize - innerGap * 2, squareSize - innerGap * 2);
    paramGraphics.translate(-((squareSize + squareGap) * 2), -(squareSize + squareGap));
    return squareSize * 3 + squareGap * 2;
  }
  
  private String getSampleText()
  {
    if (sampleText == null) {
      sampleText = UIManager.getString("ColorChooser.sampleText", getLocale());
    }
    return sampleText;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\colorchooser\DefaultPreviewPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */