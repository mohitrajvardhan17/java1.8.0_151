package sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Label;
import java.awt.peer.LabelPeer;

final class WLabelPeer
  extends WComponentPeer
  implements LabelPeer
{
  public Dimension getMinimumSize()
  {
    FontMetrics localFontMetrics = getFontMetrics(((Label)target).getFont());
    String str = ((Label)target).getText();
    if (str == null) {
      str = "";
    }
    return new Dimension(localFontMetrics.stringWidth(str) + 14, localFontMetrics.getHeight() + 8);
  }
  
  native void lazyPaint();
  
  synchronized void start()
  {
    super.start();
    lazyPaint();
  }
  
  public boolean shouldClearRectBeforePaint()
  {
    return false;
  }
  
  public native void setText(String paramString);
  
  public native void setAlignment(int paramInt);
  
  WLabelPeer(Label paramLabel)
  {
    super(paramLabel);
  }
  
  native void create(WComponentPeer paramWComponentPeer);
  
  void initialize()
  {
    Label localLabel = (Label)target;
    String str = localLabel.getText();
    if (str != null) {
      setText(str);
    }
    int i = localLabel.getAlignment();
    if (i != 0) {
      setAlignment(i);
    }
    Color localColor = ((Component)target).getBackground();
    if (localColor != null) {
      setBackground(localColor);
    }
    super.initialize();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WLabelPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */