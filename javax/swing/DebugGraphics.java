package javax.swing;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.PrintStream;
import java.text.AttributedCharacterIterator;
import java.util.Hashtable;

public class DebugGraphics
  extends Graphics
{
  Graphics graphics;
  Image buffer = null;
  int debugOptions;
  int graphicsID = graphicsCount++;
  int xOffset = yOffset = 0;
  int yOffset;
  private static int graphicsCount = 0;
  private static ImageIcon imageLoadingIcon = new ImageIcon();
  public static final int LOG_OPTION = 1;
  public static final int FLASH_OPTION = 2;
  public static final int BUFFERED_OPTION = 4;
  public static final int NONE_OPTION = -1;
  private static final Class debugGraphicsInfoKey = DebugGraphicsInfo.class;
  
  public DebugGraphics() {}
  
  public DebugGraphics(Graphics paramGraphics, JComponent paramJComponent)
  {
    this(paramGraphics);
    setDebugOptions(paramJComponent.shouldDebugGraphics());
  }
  
  public DebugGraphics(Graphics paramGraphics)
  {
    this();
    graphics = paramGraphics;
  }
  
  public Graphics create()
  {
    DebugGraphics localDebugGraphics = new DebugGraphics();
    graphics = graphics.create();
    debugOptions = debugOptions;
    buffer = buffer;
    return localDebugGraphics;
  }
  
  public Graphics create(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    DebugGraphics localDebugGraphics = new DebugGraphics();
    graphics = graphics.create(paramInt1, paramInt2, paramInt3, paramInt4);
    debugOptions = debugOptions;
    buffer = buffer;
    xOffset += paramInt1;
    yOffset += paramInt2;
    return localDebugGraphics;
  }
  
  public static void setFlashColor(Color paramColor)
  {
    infoflashColor = paramColor;
  }
  
  public static Color flashColor()
  {
    return infoflashColor;
  }
  
  public static void setFlashTime(int paramInt)
  {
    infoflashTime = paramInt;
  }
  
  public static int flashTime()
  {
    return infoflashTime;
  }
  
  public static void setFlashCount(int paramInt)
  {
    infoflashCount = paramInt;
  }
  
  public static int flashCount()
  {
    return infoflashCount;
  }
  
  public static void setLogStream(PrintStream paramPrintStream)
  {
    infostream = paramPrintStream;
  }
  
  public static PrintStream logStream()
  {
    return infostream;
  }
  
  public void setFont(Font paramFont)
  {
    if (debugLog()) {
      info().log(toShortString() + " Setting font: " + paramFont);
    }
    graphics.setFont(paramFont);
  }
  
  public Font getFont()
  {
    return graphics.getFont();
  }
  
  public void setColor(Color paramColor)
  {
    if (debugLog()) {
      info().log(toShortString() + " Setting color: " + paramColor);
    }
    graphics.setColor(paramColor);
  }
  
  public Color getColor()
  {
    return graphics.getColor();
  }
  
  public FontMetrics getFontMetrics()
  {
    return graphics.getFontMetrics();
  }
  
  public FontMetrics getFontMetrics(Font paramFont)
  {
    return graphics.getFontMetrics(paramFont);
  }
  
  public void translate(int paramInt1, int paramInt2)
  {
    if (debugLog()) {
      info().log(toShortString() + " Translating by: " + new Point(paramInt1, paramInt2));
    }
    xOffset += paramInt1;
    yOffset += paramInt2;
    graphics.translate(paramInt1, paramInt2);
  }
  
  public void setPaintMode()
  {
    if (debugLog()) {
      info().log(toShortString() + " Setting paint mode");
    }
    graphics.setPaintMode();
  }
  
  public void setXORMode(Color paramColor)
  {
    if (debugLog()) {
      info().log(toShortString() + " Setting XOR mode: " + paramColor);
    }
    graphics.setXORMode(paramColor);
  }
  
  public Rectangle getClipBounds()
  {
    return graphics.getClipBounds();
  }
  
  public void clipRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    graphics.clipRect(paramInt1, paramInt2, paramInt3, paramInt4);
    if (debugLog()) {
      info().log(toShortString() + " Setting clipRect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " New clipRect: " + graphics.getClip());
    }
  }
  
  public void setClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    graphics.setClip(paramInt1, paramInt2, paramInt3, paramInt4);
    if (debugLog()) {
      info().log(toShortString() + " Setting new clipRect: " + graphics.getClip());
    }
  }
  
  public Shape getClip()
  {
    return graphics.getClip();
  }
  
  public void setClip(Shape paramShape)
  {
    graphics.setClip(paramShape);
    if (debugLog()) {
      info().log(toShortString() + " Setting new clipRect: " + graphics.getClip());
    }
  }
  
  public void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Drawing rect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).drawRect(paramInt1, paramInt2, paramInt3, paramInt4);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.drawRect(paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.drawRect(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Filling rect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Clearing rect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).clearRect(paramInt1, paramInt2, paramInt3, paramInt4);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.clearRect(paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.clearRect(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Drawing round rect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " arcWidth: " + paramInt5 + " archHeight: " + paramInt6);
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).drawRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.drawRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.drawRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Filling round rect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " arcWidth: " + paramInt5 + " archHeight: " + paramInt6);
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).fillRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.fillRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.fillRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Drawing line: from " + pointToString(paramInt1, paramInt2) + " to " + pointToString(paramInt3, paramInt4));
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).drawLine(paramInt1, paramInt2, paramInt3, paramInt4);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.drawLine(paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.drawLine(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void draw3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Drawing 3D rect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " Raised bezel: " + paramBoolean);
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).draw3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.draw3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.draw3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
  }
  
  public void fill3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Filling 3D rect: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " Raised bezel: " + paramBoolean);
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).fill3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.fill3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.fill3DRect(paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
  }
  
  public void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Drawing oval: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).drawOval(paramInt1, paramInt2, paramInt3, paramInt4);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.drawOval(paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.drawOval(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Filling oval: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).fillOval(paramInt1, paramInt2, paramInt3, paramInt4);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.fillOval(paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.fillOval(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Drawing arc: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " startAngle: " + paramInt5 + " arcAngle: " + paramInt6);
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).drawArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.drawArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.drawArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Filling arc: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " startAngle: " + paramInt5 + " arcAngle: " + paramInt6);
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).fillArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.fillArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.fillArc(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  public void drawPolyline(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Drawing polyline:  nPoints: " + paramInt + " X's: " + paramArrayOfInt1 + " Y's: " + paramArrayOfInt2);
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).drawPolyline(paramArrayOfInt1, paramArrayOfInt2, paramInt);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.drawPolyline(paramArrayOfInt1, paramArrayOfInt2, paramInt);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.drawPolyline(paramArrayOfInt1, paramArrayOfInt2, paramInt);
  }
  
  public void drawPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Drawing polygon:  nPoints: " + paramInt + " X's: " + paramArrayOfInt1 + " Y's: " + paramArrayOfInt2);
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).drawPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.drawPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.drawPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt);
  }
  
  public void fillPolygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Filling polygon:  nPoints: " + paramInt + " X's: " + paramArrayOfInt1 + " Y's: " + paramArrayOfInt2);
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).fillPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.fillPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.fillPolygon(paramArrayOfInt1, paramArrayOfInt2, paramInt);
  }
  
  public void drawString(String paramString, int paramInt1, int paramInt2)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Drawing string: \"" + paramString + "\" at: " + new Point(paramInt1, paramInt2));
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).drawString(paramString, paramInt1, paramInt2);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.drawString(paramString, paramInt1, paramInt2);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.drawString(paramString, paramInt1, paramInt2);
  }
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      info().log(toShortString() + " Drawing text: \"" + paramAttributedCharacterIterator + "\" at: " + new Point(paramInt1, paramInt2));
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).drawString(paramAttributedCharacterIterator, paramInt1, paramInt2);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.drawString(paramAttributedCharacterIterator, paramInt1, paramInt2);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.drawString(paramAttributedCharacterIterator, paramInt1, paramInt2);
  }
  
  public void drawBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    Font localFont = graphics.getFont();
    if (debugLog()) {
      info().log(toShortString() + " Drawing bytes at: " + new Point(paramInt3, paramInt4));
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).drawBytes(paramArrayOfByte, paramInt1, paramInt2, paramInt3, paramInt4);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.drawBytes(paramArrayOfByte, paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.drawBytes(paramArrayOfByte, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void drawChars(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    Font localFont = graphics.getFont();
    if (debugLog()) {
      info().log(toShortString() + " Drawing chars at " + new Point(paramInt3, paramInt4));
    }
    Object localObject;
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        localObject = debugGraphics();
        ((Graphics)localObject).drawChars(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
        ((Graphics)localObject).dispose();
      }
    }
    else if (debugFlash())
    {
      localObject = getColor();
      int j = flashCount * 2 - 1;
      for (int i = 0; i < j; i++)
      {
        graphics.setColor(i % 2 == 0 ? flashColor : (Color)localObject);
        graphics.drawChars(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
      graphics.setColor((Color)localObject);
    }
    graphics.drawChars(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      localDebugGraphicsInfo.log(toShortString() + " Drawing image: " + paramImage + " at: " + new Point(paramInt1, paramInt2));
    }
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        Graphics localGraphics = debugGraphics();
        localGraphics.drawImage(paramImage, paramInt1, paramInt2, paramImageObserver);
        localGraphics.dispose();
      }
    }
    else if (debugFlash())
    {
      int j = flashCount * 2 - 1;
      ImageProducer localImageProducer = paramImage.getSource();
      FilteredImageSource localFilteredImageSource = new FilteredImageSource(localImageProducer, new DebugGraphicsFilter(flashColor));
      Image localImage1 = Toolkit.getDefaultToolkit().createImage(localFilteredImageSource);
      DebugGraphicsObserver localDebugGraphicsObserver = new DebugGraphicsObserver();
      for (int i = 0; i < j; i++)
      {
        Image localImage2 = i % 2 == 0 ? localImage1 : paramImage;
        loadImage(localImage2);
        graphics.drawImage(localImage2, paramInt1, paramInt2, localDebugGraphicsObserver);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
    }
    return graphics.drawImage(paramImage, paramInt1, paramInt2, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      localDebugGraphicsInfo.log(toShortString() + " Drawing image: " + paramImage + " at: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
    }
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        Graphics localGraphics = debugGraphics();
        localGraphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramImageObserver);
        localGraphics.dispose();
      }
    }
    else if (debugFlash())
    {
      int j = flashCount * 2 - 1;
      ImageProducer localImageProducer = paramImage.getSource();
      FilteredImageSource localFilteredImageSource = new FilteredImageSource(localImageProducer, new DebugGraphicsFilter(flashColor));
      Image localImage1 = Toolkit.getDefaultToolkit().createImage(localFilteredImageSource);
      DebugGraphicsObserver localDebugGraphicsObserver = new DebugGraphicsObserver();
      for (int i = 0; i < j; i++)
      {
        Image localImage2 = i % 2 == 0 ? localImage1 : paramImage;
        loadImage(localImage2);
        graphics.drawImage(localImage2, paramInt1, paramInt2, paramInt3, paramInt4, localDebugGraphicsObserver);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
    }
    return graphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      localDebugGraphicsInfo.log(toShortString() + " Drawing image: " + paramImage + " at: " + new Point(paramInt1, paramInt2) + ", bgcolor: " + paramColor);
    }
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        Graphics localGraphics = debugGraphics();
        localGraphics.drawImage(paramImage, paramInt1, paramInt2, paramColor, paramImageObserver);
        localGraphics.dispose();
      }
    }
    else if (debugFlash())
    {
      int j = flashCount * 2 - 1;
      ImageProducer localImageProducer = paramImage.getSource();
      FilteredImageSource localFilteredImageSource = new FilteredImageSource(localImageProducer, new DebugGraphicsFilter(flashColor));
      Image localImage1 = Toolkit.getDefaultToolkit().createImage(localFilteredImageSource);
      DebugGraphicsObserver localDebugGraphicsObserver = new DebugGraphicsObserver();
      for (int i = 0; i < j; i++)
      {
        Image localImage2 = i % 2 == 0 ? localImage1 : paramImage;
        loadImage(localImage2);
        graphics.drawImage(localImage2, paramInt1, paramInt2, paramColor, localDebugGraphicsObserver);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
    }
    return graphics.drawImage(paramImage, paramInt1, paramInt2, paramColor, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      localDebugGraphicsInfo.log(toShortString() + " Drawing image: " + paramImage + " at: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + ", bgcolor: " + paramColor);
    }
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        Graphics localGraphics = debugGraphics();
        localGraphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
        localGraphics.dispose();
      }
    }
    else if (debugFlash())
    {
      int j = flashCount * 2 - 1;
      ImageProducer localImageProducer = paramImage.getSource();
      FilteredImageSource localFilteredImageSource = new FilteredImageSource(localImageProducer, new DebugGraphicsFilter(flashColor));
      Image localImage1 = Toolkit.getDefaultToolkit().createImage(localFilteredImageSource);
      DebugGraphicsObserver localDebugGraphicsObserver = new DebugGraphicsObserver();
      for (int i = 0; i < j; i++)
      {
        Image localImage2 = i % 2 == 0 ? localImage1 : paramImage;
        loadImage(localImage2);
        graphics.drawImage(localImage2, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, localDebugGraphicsObserver);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
    }
    return graphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramColor, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, ImageObserver paramImageObserver)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      localDebugGraphicsInfo.log(toShortString() + " Drawing image: " + paramImage + " destination: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " source: " + new Rectangle(paramInt5, paramInt6, paramInt7, paramInt8));
    }
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        Graphics localGraphics = debugGraphics();
        localGraphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramImageObserver);
        localGraphics.dispose();
      }
    }
    else if (debugFlash())
    {
      int j = flashCount * 2 - 1;
      ImageProducer localImageProducer = paramImage.getSource();
      FilteredImageSource localFilteredImageSource = new FilteredImageSource(localImageProducer, new DebugGraphicsFilter(flashColor));
      Image localImage1 = Toolkit.getDefaultToolkit().createImage(localFilteredImageSource);
      DebugGraphicsObserver localDebugGraphicsObserver = new DebugGraphicsObserver();
      for (int i = 0; i < j; i++)
      {
        Image localImage2 = i % 2 == 0 ? localImage1 : paramImage;
        loadImage(localImage2);
        graphics.drawImage(localImage2, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, localDebugGraphicsObserver);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
    }
    return graphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugLog()) {
      localDebugGraphicsInfo.log(toShortString() + " Drawing image: " + paramImage + " destination: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " source: " + new Rectangle(paramInt5, paramInt6, paramInt7, paramInt8) + ", bgcolor: " + paramColor);
    }
    if (isDrawingBuffer())
    {
      if (debugBuffered())
      {
        Graphics localGraphics = debugGraphics();
        localGraphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
        localGraphics.dispose();
      }
    }
    else if (debugFlash())
    {
      int j = flashCount * 2 - 1;
      ImageProducer localImageProducer = paramImage.getSource();
      FilteredImageSource localFilteredImageSource = new FilteredImageSource(localImageProducer, new DebugGraphicsFilter(flashColor));
      Image localImage1 = Toolkit.getDefaultToolkit().createImage(localFilteredImageSource);
      DebugGraphicsObserver localDebugGraphicsObserver = new DebugGraphicsObserver();
      for (int i = 0; i < j; i++)
      {
        Image localImage2 = i % 2 == 0 ? localImage1 : paramImage;
        loadImage(localImage2);
        graphics.drawImage(localImage2, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, localDebugGraphicsObserver);
        Toolkit.getDefaultToolkit().sync();
        sleep(flashTime);
      }
    }
    return graphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramColor, paramImageObserver);
  }
  
  static void loadImage(Image paramImage)
  {
    imageLoadingIcon.loadImage(paramImage);
  }
  
  public void copyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    if (debugLog()) {
      info().log(toShortString() + " Copying area from: " + new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4) + " to: " + new Point(paramInt5, paramInt6));
    }
    graphics.copyArea(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
  }
  
  final void sleep(int paramInt)
  {
    try
    {
      Thread.sleep(paramInt);
    }
    catch (Exception localException) {}
  }
  
  public void dispose()
  {
    graphics.dispose();
    graphics = null;
  }
  
  public boolean isDrawingBuffer()
  {
    return buffer != null;
  }
  
  String toShortString()
  {
    return "Graphics" + (isDrawingBuffer() ? "<B>" : "") + "(" + graphicsID + "-" + debugOptions + ")";
  }
  
  String pointToString(int paramInt1, int paramInt2)
  {
    return "(" + paramInt1 + ", " + paramInt2 + ")";
  }
  
  public void setDebugOptions(int paramInt)
  {
    if (paramInt != 0) {
      if (paramInt == -1)
      {
        if (debugOptions != 0)
        {
          System.err.println(toShortString() + " Disabling debug");
          debugOptions = 0;
        }
      }
      else if (debugOptions != paramInt)
      {
        debugOptions |= paramInt;
        if (debugLog()) {
          System.err.println(toShortString() + " Enabling debug");
        }
      }
    }
  }
  
  public int getDebugOptions()
  {
    return debugOptions;
  }
  
  static void setDebugOptions(JComponent paramJComponent, int paramInt)
  {
    info().setDebugOptions(paramJComponent, paramInt);
  }
  
  static int getDebugOptions(JComponent paramJComponent)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (localDebugGraphicsInfo == null) {
      return 0;
    }
    return localDebugGraphicsInfo.getDebugOptions(paramJComponent);
  }
  
  static int shouldComponentDebug(JComponent paramJComponent)
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (localDebugGraphicsInfo == null) {
      return 0;
    }
    Object localObject = paramJComponent;
    int i = 0;
    while ((localObject != null) && ((localObject instanceof JComponent)))
    {
      i |= localDebugGraphicsInfo.getDebugOptions((JComponent)localObject);
      localObject = ((Container)localObject).getParent();
    }
    return i;
  }
  
  static int debugComponentCount()
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if ((localDebugGraphicsInfo != null) && (componentToDebug != null)) {
      return componentToDebug.size();
    }
    return 0;
  }
  
  boolean debugLog()
  {
    return (debugOptions & 0x1) == 1;
  }
  
  boolean debugFlash()
  {
    return (debugOptions & 0x2) == 2;
  }
  
  boolean debugBuffered()
  {
    return (debugOptions & 0x4) == 4;
  }
  
  private Graphics debugGraphics()
  {
    DebugGraphicsInfo localDebugGraphicsInfo = info();
    if (debugFrame == null)
    {
      debugFrame = new JFrame();
      debugFrame.setSize(500, 500);
    }
    JFrame localJFrame = debugFrame;
    localJFrame.show();
    DebugGraphics localDebugGraphics = new DebugGraphics(localJFrame.getGraphics());
    localDebugGraphics.setFont(getFont());
    localDebugGraphics.setColor(getColor());
    localDebugGraphics.translate(xOffset, yOffset);
    localDebugGraphics.setClip(getClipBounds());
    if (debugFlash()) {
      localDebugGraphics.setDebugOptions(2);
    }
    return localDebugGraphics;
  }
  
  static DebugGraphicsInfo info()
  {
    DebugGraphicsInfo localDebugGraphicsInfo = (DebugGraphicsInfo)SwingUtilities.appContextGet(debugGraphicsInfoKey);
    if (localDebugGraphicsInfo == null)
    {
      localDebugGraphicsInfo = new DebugGraphicsInfo();
      SwingUtilities.appContextPut(debugGraphicsInfoKey, localDebugGraphicsInfo);
    }
    return localDebugGraphicsInfo;
  }
  
  static
  {
    JComponent.DEBUG_GRAPHICS_LOADED = true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DebugGraphics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */