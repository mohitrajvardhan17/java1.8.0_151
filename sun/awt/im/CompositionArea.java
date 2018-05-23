package sun.awt.im;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public final class CompositionArea
  extends JPanel
  implements InputMethodListener
{
  private CompositionAreaHandler handler;
  private TextLayout composedTextLayout;
  private TextHitInfo caret = null;
  private JFrame compositionWindow;
  private static final int TEXT_ORIGIN_X = 5;
  private static final int TEXT_ORIGIN_Y = 15;
  private static final int PASSIVE_WIDTH = 480;
  private static final int WIDTH_MARGIN = 10;
  private static final int HEIGHT_MARGIN = 3;
  private static final long serialVersionUID = -1057247068746557444L;
  
  CompositionArea()
  {
    String str = Toolkit.getProperty("AWT.CompositionWindowTitle", "Input Window");
    compositionWindow = ((JFrame)InputMethodContext.createInputMethodWindow(str, null, true));
    setOpaque(true);
    setBorder(LineBorder.createGrayLineBorder());
    setForeground(Color.black);
    setBackground(Color.white);
    enableInputMethods(true);
    enableEvents(8L);
    compositionWindow.getContentPane().add(this);
    compositionWindow.addWindowListener(new FrameWindowAdapter());
    addInputMethodListener(this);
    compositionWindow.enableInputMethods(false);
    compositionWindow.pack();
    Dimension localDimension1 = compositionWindow.getSize();
    Dimension localDimension2 = getToolkit().getScreenSize();
    compositionWindow.setLocation(width - width - 20, height - height - 100);
    compositionWindow.setVisible(false);
  }
  
  synchronized void setHandlerInfo(CompositionAreaHandler paramCompositionAreaHandler, InputContext paramInputContext)
  {
    handler = paramCompositionAreaHandler;
    ((InputMethodWindow)compositionWindow).setInputContext(paramInputContext);
  }
  
  public InputMethodRequests getInputMethodRequests()
  {
    return handler;
  }
  
  private Rectangle getCaretRectangle(TextHitInfo paramTextHitInfo)
  {
    int i = 0;
    TextLayout localTextLayout = composedTextLayout;
    if (localTextLayout != null) {
      i = Math.round(localTextLayout.getCaretInfo(paramTextHitInfo)[0]);
    }
    Graphics localGraphics = getGraphics();
    FontMetrics localFontMetrics = null;
    try
    {
      localFontMetrics = localGraphics.getFontMetrics();
    }
    finally
    {
      localGraphics.dispose();
    }
    return new Rectangle(5 + i, 15 - localFontMetrics.getAscent(), 0, localFontMetrics.getAscent() + localFontMetrics.getDescent());
  }
  
  public void paint(Graphics paramGraphics)
  {
    super.paint(paramGraphics);
    paramGraphics.setColor(getForeground());
    TextLayout localTextLayout = composedTextLayout;
    if (localTextLayout != null) {
      localTextLayout.draw((Graphics2D)paramGraphics, 5.0F, 15.0F);
    }
    if (caret != null)
    {
      Rectangle localRectangle = getCaretRectangle(caret);
      paramGraphics.setXORMode(getBackground());
      paramGraphics.fillRect(x, y, 1, height);
      paramGraphics.setPaintMode();
    }
  }
  
  void setCompositionAreaVisible(boolean paramBoolean)
  {
    compositionWindow.setVisible(paramBoolean);
  }
  
  boolean isCompositionAreaVisible()
  {
    return compositionWindow.isVisible();
  }
  
  public void inputMethodTextChanged(InputMethodEvent paramInputMethodEvent)
  {
    handler.inputMethodTextChanged(paramInputMethodEvent);
  }
  
  public void caretPositionChanged(InputMethodEvent paramInputMethodEvent)
  {
    handler.caretPositionChanged(paramInputMethodEvent);
  }
  
  void setText(AttributedCharacterIterator paramAttributedCharacterIterator, TextHitInfo paramTextHitInfo)
  {
    composedTextLayout = null;
    if (paramAttributedCharacterIterator == null)
    {
      compositionWindow.setVisible(false);
      caret = null;
    }
    else
    {
      if (!compositionWindow.isVisible()) {
        compositionWindow.setVisible(true);
      }
      Graphics localGraphics = getGraphics();
      if (localGraphics == null) {
        return;
      }
      try
      {
        updateWindowLocation();
        FontRenderContext localFontRenderContext = ((Graphics2D)localGraphics).getFontRenderContext();
        composedTextLayout = new TextLayout(paramAttributedCharacterIterator, localFontRenderContext);
        Rectangle2D localRectangle2D1 = composedTextLayout.getBounds();
        caret = paramTextHitInfo;
        FontMetrics localFontMetrics = localGraphics.getFontMetrics();
        Rectangle2D localRectangle2D2 = localFontMetrics.getMaxCharBounds(localGraphics);
        int i = (int)localRectangle2D2.getHeight() + 3;
        int j = i + compositionWindow.getInsets().top + compositionWindow.getInsets().bottom;
        InputMethodRequests localInputMethodRequests = handler.getClientInputMethodRequests();
        int k = localInputMethodRequests == null ? 480 : (int)localRectangle2D1.getWidth() + 10;
        int m = k + compositionWindow.getInsets().left + compositionWindow.getInsets().right;
        setPreferredSize(new Dimension(k, i));
        compositionWindow.setSize(new Dimension(m, j));
        paint(localGraphics);
      }
      finally
      {
        localGraphics.dispose();
      }
    }
  }
  
  /* Error */
  void setCaret(TextHitInfo paramTextHitInfo)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: putfield 304	sun/awt/im/CompositionArea:caret	Ljava/awt/font/TextHitInfo;
    //   5: aload_0
    //   6: getfield 306	sun/awt/im/CompositionArea:compositionWindow	Ljavax/swing/JFrame;
    //   9: invokevirtual 338	javax/swing/JFrame:isVisible	()Z
    //   12: ifeq +27 -> 39
    //   15: aload_0
    //   16: invokevirtual 361	sun/awt/im/CompositionArea:getGraphics	()Ljava/awt/Graphics;
    //   19: astore_2
    //   20: aload_0
    //   21: aload_2
    //   22: invokevirtual 362	sun/awt/im/CompositionArea:paint	(Ljava/awt/Graphics;)V
    //   25: aload_2
    //   26: invokevirtual 313	java/awt/Graphics:dispose	()V
    //   29: goto +10 -> 39
    //   32: astore_3
    //   33: aload_2
    //   34: invokevirtual 313	java/awt/Graphics:dispose	()V
    //   37: aload_3
    //   38: athrow
    //   39: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	40	0	this	CompositionArea
    //   0	40	1	paramTextHitInfo	TextHitInfo
    //   19	15	2	localGraphics	Graphics
    //   32	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   20	25	32	finally
  }
  
  void updateWindowLocation()
  {
    InputMethodRequests localInputMethodRequests = handler.getClientInputMethodRequests();
    if (localInputMethodRequests == null) {
      return;
    }
    Point localPoint = new Point();
    Rectangle localRectangle = localInputMethodRequests.getTextLocation(null);
    Dimension localDimension1 = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension localDimension2 = compositionWindow.getSize();
    if (x + width > width) {
      x = (width - width);
    } else {
      x = x;
    }
    if (y + height + 2 + height > height) {
      y = (y - 2 - height);
    } else {
      y = (y + height + 2);
    }
    compositionWindow.setLocation(localPoint);
  }
  
  Rectangle getTextLocation(TextHitInfo paramTextHitInfo)
  {
    Rectangle localRectangle = getCaretRectangle(paramTextHitInfo);
    Point localPoint = getLocationOnScreen();
    localRectangle.translate(x, y);
    return localRectangle;
  }
  
  TextHitInfo getLocationOffset(int paramInt1, int paramInt2)
  {
    TextLayout localTextLayout = composedTextLayout;
    if (localTextLayout == null) {
      return null;
    }
    Point localPoint = getLocationOnScreen();
    paramInt1 -= x + 5;
    paramInt2 -= y + 15;
    if (localTextLayout.getBounds().contains(paramInt1, paramInt2)) {
      return localTextLayout.hitTestChar(paramInt1, paramInt2);
    }
    return null;
  }
  
  void setCompositionAreaUndecorated(boolean paramBoolean)
  {
    if (compositionWindow.isDisplayable()) {
      compositionWindow.removeNotify();
    }
    compositionWindow.setUndecorated(paramBoolean);
    compositionWindow.pack();
  }
  
  class FrameWindowAdapter
    extends WindowAdapter
  {
    FrameWindowAdapter() {}
    
    public void windowActivated(WindowEvent paramWindowEvent)
    {
      requestFocus();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\im\CompositionArea.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */