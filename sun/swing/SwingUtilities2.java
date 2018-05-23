package sun.swing;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.PrintGraphics;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PrinterGraphics;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIDefaults.LazyValue;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.font.FontDesignMetrics;
import sun.font.FontUtilities;
import sun.java2d.SunGraphicsEnvironment;
import sun.print.ProxyPrintGraphics;
import sun.security.util.SecurityConstants.AWT;

public class SwingUtilities2
{
  public static final Object LAF_STATE_KEY;
  public static final Object MENU_SELECTION_MANAGER_LISTENER_KEY;
  private static LSBCacheEntry[] fontCache = new LSBCacheEntry[6];
  private static final int CACHE_SIZE = 6;
  private static int nextIndex;
  private static LSBCacheEntry searchKey;
  private static final int MIN_CHAR_INDEX = 87;
  private static final int MAX_CHAR_INDEX = 88;
  public static final FontRenderContext DEFAULT_FRC;
  public static final Object AA_TEXT_PROPERTY_KEY;
  public static final String IMPLIED_CR = "CR";
  private static final StringBuilder SKIP_CLICK_COUNT;
  public static final Object COMPONENT_UI_PROPERTY_KEY;
  public static final StringUIClientPropertyKey BASICMENUITEMUI_MAX_TEXT_OFFSET;
  private static Field inputEvent_CanAccessSystemClipboard_Field;
  private static final String UntrustedClipboardAccess = "UNTRUSTED_CLIPBOARD_ACCESS_KEY";
  private static final int CHAR_BUFFER_SIZE = 100;
  private static final Object charsBufferLock;
  private static char[] charsBuffer;
  
  public SwingUtilities2() {}
  
  private static int syncCharsBuffer(String paramString)
  {
    int i = paramString.length();
    if ((charsBuffer == null) || (charsBuffer.length < i)) {
      charsBuffer = paramString.toCharArray();
    } else {
      paramString.getChars(0, i, charsBuffer, 0);
    }
    return i;
  }
  
  public static final boolean isComplexLayout(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    return FontUtilities.isComplexText(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public static AATextInfo drawTextAntialiased(JComponent paramJComponent)
  {
    if (paramJComponent != null) {
      return (AATextInfo)paramJComponent.getClientProperty(AA_TEXT_PROPERTY_KEY);
    }
    return null;
  }
  
  public static int getLeftSideBearing(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return 0;
    }
    return getLeftSideBearing(paramJComponent, paramFontMetrics, paramString.charAt(0));
  }
  
  public static int getLeftSideBearing(JComponent paramJComponent, FontMetrics paramFontMetrics, char paramChar)
  {
    int i = paramChar;
    if ((i < 88) && (i >= 87))
    {
      Object localObject1 = null;
      FontRenderContext localFontRenderContext = getFontRenderContext(paramJComponent, paramFontMetrics);
      Font localFont = paramFontMetrics.getFont();
      synchronized (SwingUtilities2.class)
      {
        Object localObject2 = null;
        if (searchKey == null) {
          searchKey = new LSBCacheEntry(localFontRenderContext, localFont);
        } else {
          searchKey.reset(localFontRenderContext, localFont);
        }
        for (LSBCacheEntry localLSBCacheEntry : fontCache) {
          if (searchKey.equals(localLSBCacheEntry))
          {
            localObject2 = localLSBCacheEntry;
            break;
          }
        }
        if (localObject2 == null)
        {
          localObject2 = searchKey;
          fontCache[nextIndex] = searchKey;
          searchKey = null;
          nextIndex = (nextIndex + 1) % 6;
        }
        return ((LSBCacheEntry)localObject2).getLeftSideBearing(paramChar);
      }
    }
    return 0;
  }
  
  public static FontMetrics getFontMetrics(JComponent paramJComponent, Graphics paramGraphics)
  {
    return getFontMetrics(paramJComponent, paramGraphics, paramGraphics.getFont());
  }
  
  public static FontMetrics getFontMetrics(JComponent paramJComponent, Graphics paramGraphics, Font paramFont)
  {
    if (paramJComponent != null) {
      return paramJComponent.getFontMetrics(paramFont);
    }
    return Toolkit.getDefaultToolkit().getFontMetrics(paramFont);
  }
  
  public static int stringWidth(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString)
  {
    if ((paramString == null) || (paramString.equals(""))) {
      return 0;
    }
    boolean bool = (paramJComponent != null) && (paramJComponent.getClientProperty(TextAttribute.NUMERIC_SHAPING) != null);
    if (bool) {
      synchronized (charsBufferLock)
      {
        int i = syncCharsBuffer(paramString);
        bool = isComplexLayout(charsBuffer, 0, i);
      }
    }
    if (bool)
    {
      ??? = createTextLayout(paramJComponent, paramString, paramFontMetrics.getFont(), paramFontMetrics.getFontRenderContext());
      return (int)((TextLayout)???).getAdvance();
    }
    return paramFontMetrics.stringWidth(paramString);
  }
  
  public static String clipStringIfNecessary(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString, int paramInt)
  {
    if ((paramString == null) || (paramString.equals(""))) {
      return "";
    }
    int i = stringWidth(paramJComponent, paramFontMetrics, paramString);
    if (i > paramInt) {
      return clipString(paramJComponent, paramFontMetrics, paramString, paramInt);
    }
    return paramString;
  }
  
  public static String clipString(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString, int paramInt)
  {
    String str = "...";
    paramInt -= stringWidth(paramJComponent, paramFontMetrics, str);
    if (paramInt <= 0) {
      return str;
    }
    boolean bool;
    synchronized (charsBufferLock)
    {
      int i = syncCharsBuffer(paramString);
      bool = isComplexLayout(charsBuffer, 0, i);
      if (!bool)
      {
        int j = 0;
        for (int k = 0; k < i; k++)
        {
          j += paramFontMetrics.charWidth(charsBuffer[k]);
          if (j > paramInt)
          {
            paramString = paramString.substring(0, k);
            break;
          }
        }
      }
    }
    if (bool)
    {
      ??? = new AttributedString(paramString);
      if (paramJComponent != null) {
        ((AttributedString)???).addAttribute(TextAttribute.NUMERIC_SHAPING, paramJComponent.getClientProperty(TextAttribute.NUMERIC_SHAPING));
      }
      LineBreakMeasurer localLineBreakMeasurer = new LineBreakMeasurer(((AttributedString)???).getIterator(), BreakIterator.getCharacterInstance(), getFontRenderContext(paramJComponent, paramFontMetrics));
      paramString = paramString.substring(0, localLineBreakMeasurer.nextOffset(paramInt));
    }
    return paramString + str;
  }
  
  public static void drawString(JComponent paramJComponent, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2)
  {
    if ((paramString == null) || (paramString.length() <= 0)) {
      return;
    }
    Object localObject1;
    Object localObject2;
    if (isPrinting(paramGraphics))
    {
      localObject1 = getGraphics2D(paramGraphics);
      if (localObject1 != null)
      {
        localObject2 = trimTrailingSpaces(paramString);
        if (!((String)localObject2).isEmpty())
        {
          float f = (float)((Graphics2D)localObject1).getFont().getStringBounds((String)localObject2, DEFAULT_FRC).getWidth();
          TextLayout localTextLayout1 = createTextLayout(paramJComponent, paramString, ((Graphics2D)localObject1).getFont(), ((Graphics2D)localObject1).getFontRenderContext());
          localTextLayout1 = localTextLayout1.getJustifiedLayout(f);
          Color localColor = ((Graphics2D)localObject1).getColor();
          if ((localColor instanceof PrintColorUIResource)) {
            ((Graphics2D)localObject1).setColor(((PrintColorUIResource)localColor).getPrintColor());
          }
          localTextLayout1.draw((Graphics2D)localObject1, paramInt1, paramInt2);
          ((Graphics2D)localObject1).setColor(localColor);
        }
        return;
      }
    }
    if ((paramGraphics instanceof Graphics2D))
    {
      localObject1 = drawTextAntialiased(paramJComponent);
      localObject2 = (Graphics2D)paramGraphics;
      boolean bool = (paramJComponent != null) && (paramJComponent.getClientProperty(TextAttribute.NUMERIC_SHAPING) != null);
      if (bool) {
        synchronized (charsBufferLock)
        {
          int i = syncCharsBuffer(paramString);
          bool = isComplexLayout(charsBuffer, 0, i);
        }
      }
      if (localObject1 != null)
      {
        ??? = null;
        Object localObject3 = ((Graphics2D)localObject2).getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
        if (aaHint != localObject3) {
          ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aaHint);
        } else {
          localObject3 = null;
        }
        if (lcdContrastHint != null)
        {
          ??? = ((Graphics2D)localObject2).getRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST);
          if (lcdContrastHint.equals(???)) {
            ??? = null;
          } else {
            ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, lcdContrastHint);
          }
        }
        if (bool)
        {
          TextLayout localTextLayout2 = createTextLayout(paramJComponent, paramString, ((Graphics2D)localObject2).getFont(), ((Graphics2D)localObject2).getFontRenderContext());
          localTextLayout2.draw((Graphics2D)localObject2, paramInt1, paramInt2);
        }
        else
        {
          paramGraphics.drawString(paramString, paramInt1, paramInt2);
        }
        if (localObject3 != null) {
          ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, localObject3);
        }
        if (??? != null) {
          ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, ???);
        }
        return;
      }
      if (bool)
      {
        ??? = createTextLayout(paramJComponent, paramString, ((Graphics2D)localObject2).getFont(), ((Graphics2D)localObject2).getFontRenderContext());
        ((TextLayout)???).draw((Graphics2D)localObject2, paramInt1, paramInt2);
        return;
      }
    }
    paramGraphics.drawString(paramString, paramInt1, paramInt2);
  }
  
  public static void drawStringUnderlineCharAt(JComponent paramJComponent, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramString == null) || (paramString.length() <= 0)) {
      return;
    }
    drawString(paramJComponent, paramGraphics, paramString, paramInt2, paramInt3);
    int i = paramString.length();
    if ((paramInt1 >= 0) && (paramInt1 < i))
    {
      int j = paramInt3;
      int k = 1;
      int m = 0;
      int n = 0;
      boolean bool1 = isPrinting(paramGraphics);
      boolean bool2 = bool1;
      if (!bool2) {
        synchronized (charsBufferLock)
        {
          syncCharsBuffer(paramString);
          bool2 = isComplexLayout(charsBuffer, 0, i);
        }
      }
      if (!bool2)
      {
        ??? = paramGraphics.getFontMetrics();
        m = paramInt2 + stringWidth(paramJComponent, (FontMetrics)???, paramString.substring(0, paramInt1));
        n = ((FontMetrics)???).charWidth(paramString.charAt(paramInt1));
      }
      else
      {
        ??? = getGraphics2D(paramGraphics);
        if (??? != null)
        {
          TextLayout localTextLayout = createTextLayout(paramJComponent, paramString, ((Graphics2D)???).getFont(), ((Graphics2D)???).getFontRenderContext());
          if (bool1)
          {
            float f = (float)((Graphics2D)???).getFont().getStringBounds(paramString, DEFAULT_FRC).getWidth();
            localTextLayout = localTextLayout.getJustifiedLayout(f);
          }
          TextHitInfo localTextHitInfo1 = TextHitInfo.leading(paramInt1);
          TextHitInfo localTextHitInfo2 = TextHitInfo.trailing(paramInt1);
          Shape localShape = localTextLayout.getVisualHighlightShape(localTextHitInfo1, localTextHitInfo2);
          Rectangle localRectangle = localShape.getBounds();
          m = paramInt2 + x;
          n = width;
        }
      }
      paramGraphics.fillRect(m, j + 1, n, k);
    }
  }
  
  public static int loc2IndexFileList(JList paramJList, Point paramPoint)
  {
    int i = paramJList.locationToIndex(paramPoint);
    if (i != -1)
    {
      Object localObject = paramJList.getClientProperty("List.isFileList");
      if (((localObject instanceof Boolean)) && (((Boolean)localObject).booleanValue()) && (!pointIsInActualBounds(paramJList, i, paramPoint))) {
        i = -1;
      }
    }
    return i;
  }
  
  private static boolean pointIsInActualBounds(JList paramJList, int paramInt, Point paramPoint)
  {
    ListCellRenderer localListCellRenderer = paramJList.getCellRenderer();
    ListModel localListModel = paramJList.getModel();
    Object localObject = localListModel.getElementAt(paramInt);
    Component localComponent = localListCellRenderer.getListCellRendererComponent(paramJList, localObject, paramInt, false, false);
    Dimension localDimension = localComponent.getPreferredSize();
    Rectangle localRectangle = paramJList.getCellBounds(paramInt, paramInt);
    if (!localComponent.getComponentOrientation().isLeftToRight()) {
      x += width - width;
    }
    width = width;
    return localRectangle.contains(paramPoint);
  }
  
  public static boolean pointOutsidePrefSize(JTable paramJTable, int paramInt1, int paramInt2, Point paramPoint)
  {
    if ((paramJTable.convertColumnIndexToModel(paramInt2) != 0) || (paramInt1 == -1)) {
      return true;
    }
    TableCellRenderer localTableCellRenderer = paramJTable.getCellRenderer(paramInt1, paramInt2);
    Object localObject = paramJTable.getValueAt(paramInt1, paramInt2);
    Component localComponent = localTableCellRenderer.getTableCellRendererComponent(paramJTable, localObject, false, false, paramInt1, paramInt2);
    Dimension localDimension = localComponent.getPreferredSize();
    Rectangle localRectangle = paramJTable.getCellRect(paramInt1, paramInt2, false);
    width = width;
    height = height;
    assert ((x >= x) && (y >= y));
    return (x > x + width) || (y > y + height);
  }
  
  public static void setLeadAnchorWithoutSelection(ListSelectionModel paramListSelectionModel, int paramInt1, int paramInt2)
  {
    if (paramInt2 == -1) {
      paramInt2 = paramInt1;
    }
    if (paramInt1 == -1)
    {
      paramListSelectionModel.setAnchorSelectionIndex(-1);
      paramListSelectionModel.setLeadSelectionIndex(-1);
    }
    else
    {
      if (paramListSelectionModel.isSelectedIndex(paramInt1)) {
        paramListSelectionModel.addSelectionInterval(paramInt1, paramInt1);
      } else {
        paramListSelectionModel.removeSelectionInterval(paramInt1, paramInt1);
      }
      paramListSelectionModel.setAnchorSelectionIndex(paramInt2);
    }
  }
  
  public static boolean shouldIgnore(MouseEvent paramMouseEvent, JComponent paramJComponent)
  {
    return (paramJComponent == null) || (!paramJComponent.isEnabled()) || (!SwingUtilities.isLeftMouseButton(paramMouseEvent)) || (paramMouseEvent.isConsumed());
  }
  
  public static void adjustFocus(JComponent paramJComponent)
  {
    if ((!paramJComponent.hasFocus()) && (paramJComponent.isRequestFocusEnabled())) {
      paramJComponent.requestFocus();
    }
  }
  
  public static int drawChars(JComponent paramJComponent, Graphics paramGraphics, char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramInt2 <= 0) {
      return paramInt3;
    }
    int i = paramInt3 + getFontMetrics(paramJComponent, paramGraphics).charsWidth(paramArrayOfChar, paramInt1, paramInt2);
    Object localObject2;
    Object localObject3;
    Object localObject4;
    if (isPrinting(paramGraphics))
    {
      localObject1 = getGraphics2D(paramGraphics);
      if (localObject1 != null)
      {
        localObject2 = ((Graphics2D)localObject1).getFontRenderContext();
        localObject3 = getFontRenderContext(paramJComponent);
        if ((localObject3 != null) && (!isFontRenderContextPrintCompatible((FontRenderContext)localObject2, (FontRenderContext)localObject3)))
        {
          localObject4 = new String(paramArrayOfChar, paramInt1, paramInt2);
          TextLayout localTextLayout = new TextLayout((String)localObject4, ((Graphics2D)localObject1).getFont(), (FontRenderContext)localObject2);
          String str = trimTrailingSpaces((String)localObject4);
          if (!str.isEmpty())
          {
            float f = (float)((Graphics2D)localObject1).getFont().getStringBounds(str, (FontRenderContext)localObject3).getWidth();
            localTextLayout = localTextLayout.getJustifiedLayout(f);
            Color localColor = ((Graphics2D)localObject1).getColor();
            if ((localColor instanceof PrintColorUIResource)) {
              ((Graphics2D)localObject1).setColor(((PrintColorUIResource)localColor).getPrintColor());
            }
            localTextLayout.draw((Graphics2D)localObject1, paramInt3, paramInt4);
            ((Graphics2D)localObject1).setColor(localColor);
          }
          return i;
        }
      }
    }
    Object localObject1 = drawTextAntialiased(paramJComponent);
    if ((localObject1 != null) && ((paramGraphics instanceof Graphics2D)))
    {
      localObject2 = (Graphics2D)paramGraphics;
      localObject3 = null;
      localObject4 = ((Graphics2D)localObject2).getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
      if ((aaHint != null) && (aaHint != localObject4)) {
        ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aaHint);
      } else {
        localObject4 = null;
      }
      if (lcdContrastHint != null)
      {
        localObject3 = ((Graphics2D)localObject2).getRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST);
        if (lcdContrastHint.equals(localObject3)) {
          localObject3 = null;
        } else {
          ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, lcdContrastHint);
        }
      }
      paramGraphics.drawChars(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
      if (localObject4 != null) {
        ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, localObject4);
      }
      if (localObject3 != null) {
        ((Graphics2D)localObject2).setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, localObject3);
      }
    }
    else
    {
      paramGraphics.drawChars(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    return i;
  }
  
  public static float drawString(JComponent paramJComponent, Graphics paramGraphics, AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2)
  {
    boolean bool = isPrinting(paramGraphics);
    Color localColor = paramGraphics.getColor();
    if ((bool) && ((localColor instanceof PrintColorUIResource))) {
      paramGraphics.setColor(((PrintColorUIResource)localColor).getPrintColor());
    }
    Graphics2D localGraphics2D = getGraphics2D(paramGraphics);
    float f1;
    if (localGraphics2D == null)
    {
      paramGraphics.drawString(paramAttributedCharacterIterator, paramInt1, paramInt2);
      f1 = paramInt1;
    }
    else
    {
      FontRenderContext localFontRenderContext1;
      if (bool)
      {
        localFontRenderContext1 = getFontRenderContext(paramJComponent);
        if ((localFontRenderContext1.isAntiAliased()) || (localFontRenderContext1.usesFractionalMetrics())) {
          localFontRenderContext1 = new FontRenderContext(localFontRenderContext1.getTransform(), false, false);
        }
      }
      else if ((localFontRenderContext1 = getFRCProperty(paramJComponent)) == null)
      {
        localFontRenderContext1 = localGraphics2D.getFontRenderContext();
      }
      TextLayout localTextLayout;
      if (bool)
      {
        FontRenderContext localFontRenderContext2 = localGraphics2D.getFontRenderContext();
        if (!isFontRenderContextPrintCompatible(localFontRenderContext1, localFontRenderContext2))
        {
          localTextLayout = new TextLayout(paramAttributedCharacterIterator, localFontRenderContext2);
          AttributedCharacterIterator localAttributedCharacterIterator = getTrimmedTrailingSpacesIterator(paramAttributedCharacterIterator);
          if (localAttributedCharacterIterator != null)
          {
            float f2 = new TextLayout(localAttributedCharacterIterator, localFontRenderContext1).getAdvance();
            localTextLayout = localTextLayout.getJustifiedLayout(f2);
          }
        }
        else
        {
          localTextLayout = new TextLayout(paramAttributedCharacterIterator, localFontRenderContext1);
        }
      }
      else
      {
        localTextLayout = new TextLayout(paramAttributedCharacterIterator, localFontRenderContext1);
      }
      localTextLayout.draw(localGraphics2D, paramInt1, paramInt2);
      f1 = localTextLayout.getAdvance();
    }
    if (bool) {
      paramGraphics.setColor(localColor);
    }
    return f1;
  }
  
  public static void drawVLine(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt3 < paramInt2)
    {
      int i = paramInt3;
      paramInt3 = paramInt2;
      paramInt2 = i;
    }
    paramGraphics.fillRect(paramInt1, paramInt2, 1, paramInt3 - paramInt2 + 1);
  }
  
  public static void drawHLine(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 < paramInt1)
    {
      int i = paramInt2;
      paramInt2 = paramInt1;
      paramInt1 = i;
    }
    paramGraphics.fillRect(paramInt1, paramInt3, paramInt2 - paramInt1 + 1, 1);
  }
  
  public static void drawRect(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt3 < 0) || (paramInt4 < 0)) {
      return;
    }
    if ((paramInt4 == 0) || (paramInt3 == 0))
    {
      paramGraphics.fillRect(paramInt1, paramInt2, paramInt3 + 1, paramInt4 + 1);
    }
    else
    {
      paramGraphics.fillRect(paramInt1, paramInt2, paramInt3, 1);
      paramGraphics.fillRect(paramInt1 + paramInt3, paramInt2, 1, paramInt4);
      paramGraphics.fillRect(paramInt1 + 1, paramInt2 + paramInt4, paramInt3, 1);
      paramGraphics.fillRect(paramInt1, paramInt2 + 1, 1, paramInt4);
    }
  }
  
  private static TextLayout createTextLayout(JComponent paramJComponent, String paramString, Font paramFont, FontRenderContext paramFontRenderContext)
  {
    Object localObject = paramJComponent == null ? null : paramJComponent.getClientProperty(TextAttribute.NUMERIC_SHAPING);
    if (localObject == null) {
      return new TextLayout(paramString, paramFont, paramFontRenderContext);
    }
    HashMap localHashMap = new HashMap();
    localHashMap.put(TextAttribute.FONT, paramFont);
    localHashMap.put(TextAttribute.NUMERIC_SHAPING, localObject);
    return new TextLayout(paramString, localHashMap, paramFontRenderContext);
  }
  
  private static boolean isFontRenderContextPrintCompatible(FontRenderContext paramFontRenderContext1, FontRenderContext paramFontRenderContext2)
  {
    if (paramFontRenderContext1 == paramFontRenderContext2) {
      return true;
    }
    if ((paramFontRenderContext1 == null) || (paramFontRenderContext2 == null)) {
      return false;
    }
    if (paramFontRenderContext1.getFractionalMetricsHint() != paramFontRenderContext2.getFractionalMetricsHint()) {
      return false;
    }
    if ((!paramFontRenderContext1.isTransformed()) && (!paramFontRenderContext2.isTransformed())) {
      return true;
    }
    double[] arrayOfDouble1 = new double[4];
    double[] arrayOfDouble2 = new double[4];
    paramFontRenderContext1.getTransform().getMatrix(arrayOfDouble1);
    paramFontRenderContext2.getTransform().getMatrix(arrayOfDouble2);
    return (arrayOfDouble1[0] == arrayOfDouble2[0]) && (arrayOfDouble1[1] == arrayOfDouble2[1]) && (arrayOfDouble1[2] == arrayOfDouble2[2]) && (arrayOfDouble1[3] == arrayOfDouble2[3]);
  }
  
  public static Graphics2D getGraphics2D(Graphics paramGraphics)
  {
    if ((paramGraphics instanceof Graphics2D)) {
      return (Graphics2D)paramGraphics;
    }
    if ((paramGraphics instanceof ProxyPrintGraphics)) {
      return (Graphics2D)((ProxyPrintGraphics)paramGraphics).getGraphics();
    }
    return null;
  }
  
  public static FontRenderContext getFontRenderContext(Component paramComponent)
  {
    assert (paramComponent != null);
    if (paramComponent == null) {
      return DEFAULT_FRC;
    }
    return paramComponent.getFontMetrics(paramComponent.getFont()).getFontRenderContext();
  }
  
  private static FontRenderContext getFontRenderContext(Component paramComponent, FontMetrics paramFontMetrics)
  {
    assert ((paramFontMetrics != null) || (paramComponent != null));
    return paramFontMetrics != null ? paramFontMetrics.getFontRenderContext() : getFontRenderContext(paramComponent);
  }
  
  public static FontMetrics getFontMetrics(JComponent paramJComponent, Font paramFont)
  {
    FontRenderContext localFontRenderContext = getFRCProperty(paramJComponent);
    if (localFontRenderContext == null) {
      localFontRenderContext = DEFAULT_FRC;
    }
    return FontDesignMetrics.getMetrics(paramFont, localFontRenderContext);
  }
  
  private static FontRenderContext getFRCProperty(JComponent paramJComponent)
  {
    if (paramJComponent != null)
    {
      AATextInfo localAATextInfo = (AATextInfo)paramJComponent.getClientProperty(AA_TEXT_PROPERTY_KEY);
      if (localAATextInfo != null) {
        return frc;
      }
    }
    return null;
  }
  
  static boolean isPrinting(Graphics paramGraphics)
  {
    return ((paramGraphics instanceof PrinterGraphics)) || ((paramGraphics instanceof PrintGraphics));
  }
  
  private static String trimTrailingSpaces(String paramString)
  {
    for (int i = paramString.length() - 1; (i >= 0) && (Character.isWhitespace(paramString.charAt(i))); i--) {}
    return paramString.substring(0, i + 1);
  }
  
  private static AttributedCharacterIterator getTrimmedTrailingSpacesIterator(AttributedCharacterIterator paramAttributedCharacterIterator)
  {
    int i = paramAttributedCharacterIterator.getIndex();
    int k;
    for (int j = paramAttributedCharacterIterator.last(); (j != 65535) && (Character.isWhitespace(j)); k = paramAttributedCharacterIterator.previous()) {}
    if (k != 65535)
    {
      int m = paramAttributedCharacterIterator.getIndex();
      if (m == paramAttributedCharacterIterator.getEndIndex() - 1)
      {
        paramAttributedCharacterIterator.setIndex(i);
        return paramAttributedCharacterIterator;
      }
      AttributedString localAttributedString = new AttributedString(paramAttributedCharacterIterator, paramAttributedCharacterIterator.getBeginIndex(), m + 1);
      return localAttributedString.getIterator();
    }
    return null;
  }
  
  public static boolean useSelectedTextColor(Highlighter.Highlight paramHighlight, JTextComponent paramJTextComponent)
  {
    Highlighter.HighlightPainter localHighlightPainter = paramHighlight.getPainter();
    String str = localHighlightPainter.getClass().getName();
    if ((str.indexOf("javax.swing.text.DefaultHighlighter") != 0) && (str.indexOf("com.sun.java.swing.plaf.windows.WindowsTextUI") != 0)) {
      return false;
    }
    try
    {
      DefaultHighlighter.DefaultHighlightPainter localDefaultHighlightPainter = (DefaultHighlighter.DefaultHighlightPainter)localHighlightPainter;
      if ((localDefaultHighlightPainter.getColor() != null) && (!localDefaultHighlightPainter.getColor().equals(paramJTextComponent.getSelectionColor()))) {
        return false;
      }
    }
    catch (ClassCastException localClassCastException)
    {
      return false;
    }
    return true;
  }
  
  public static boolean canAccessSystemClipboard()
  {
    boolean bool = false;
    if (!GraphicsEnvironment.isHeadless())
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager == null)
      {
        bool = true;
      }
      else
      {
        try
        {
          localSecurityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
          bool = true;
        }
        catch (SecurityException localSecurityException) {}
        if ((bool) && (!isTrustedContext())) {
          bool = canCurrentEventAccessSystemClipboard(true);
        }
      }
    }
    return bool;
  }
  
  public static boolean canCurrentEventAccessSystemClipboard()
  {
    return (isTrustedContext()) || (canCurrentEventAccessSystemClipboard(false));
  }
  
  public static boolean canEventAccessSystemClipboard(AWTEvent paramAWTEvent)
  {
    return (isTrustedContext()) || (canEventAccessSystemClipboard(paramAWTEvent, false));
  }
  
  private static synchronized boolean inputEvent_canAccessSystemClipboard(InputEvent paramInputEvent)
  {
    if (inputEvent_CanAccessSystemClipboard_Field == null) {
      inputEvent_CanAccessSystemClipboard_Field = (Field)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Field run()
        {
          try
          {
            Field localField = InputEvent.class.getDeclaredField("canAccessSystemClipboard");
            localField.setAccessible(true);
            return localField;
          }
          catch (SecurityException localSecurityException) {}catch (NoSuchFieldException localNoSuchFieldException) {}
          return null;
        }
      });
    }
    if (inputEvent_CanAccessSystemClipboard_Field == null) {
      return false;
    }
    boolean bool = false;
    try
    {
      bool = inputEvent_CanAccessSystemClipboard_Field.getBoolean(paramInputEvent);
    }
    catch (IllegalAccessException localIllegalAccessException) {}
    return bool;
  }
  
  private static boolean isAccessClipboardGesture(InputEvent paramInputEvent)
  {
    boolean bool = false;
    if ((paramInputEvent instanceof KeyEvent))
    {
      KeyEvent localKeyEvent = (KeyEvent)paramInputEvent;
      int i = localKeyEvent.getKeyCode();
      int j = localKeyEvent.getModifiers();
      switch (i)
      {
      case 67: 
      case 86: 
      case 88: 
        bool = j == 2;
        break;
      case 155: 
        bool = (j == 2) || (j == 1);
        break;
      case 65485: 
      case 65487: 
      case 65489: 
        bool = true;
        break;
      case 127: 
        bool = j == 1;
      }
    }
    return bool;
  }
  
  private static boolean canEventAccessSystemClipboard(AWTEvent paramAWTEvent, boolean paramBoolean)
  {
    if (EventQueue.isDispatchThread())
    {
      if (((paramAWTEvent instanceof InputEvent)) && ((!paramBoolean) || (isAccessClipboardGesture((InputEvent)paramAWTEvent)))) {
        return inputEvent_canAccessSystemClipboard((InputEvent)paramAWTEvent);
      }
      return false;
    }
    return true;
  }
  
  public static void checkAccess(int paramInt)
  {
    if ((System.getSecurityManager() != null) && (!Modifier.isPublic(paramInt))) {
      throw new SecurityException("Resource is not accessible");
    }
  }
  
  private static boolean canCurrentEventAccessSystemClipboard(boolean paramBoolean)
  {
    AWTEvent localAWTEvent = EventQueue.getCurrentEvent();
    return canEventAccessSystemClipboard(localAWTEvent, paramBoolean);
  }
  
  private static boolean isTrustedContext()
  {
    return (System.getSecurityManager() == null) || (AppContext.getAppContext().get("UNTRUSTED_CLIPBOARD_ACCESS_KEY") == null);
  }
  
  public static String displayPropertiesToCSS(Font paramFont, Color paramColor)
  {
    StringBuffer localStringBuffer = new StringBuffer("body {");
    if (paramFont != null)
    {
      localStringBuffer.append(" font-family: ");
      localStringBuffer.append(paramFont.getFamily());
      localStringBuffer.append(" ; ");
      localStringBuffer.append(" font-size: ");
      localStringBuffer.append(paramFont.getSize());
      localStringBuffer.append("pt ;");
      if (paramFont.isBold()) {
        localStringBuffer.append(" font-weight: 700 ; ");
      }
      if (paramFont.isItalic()) {
        localStringBuffer.append(" font-style: italic ; ");
      }
    }
    if (paramColor != null)
    {
      localStringBuffer.append(" color: #");
      if (paramColor.getRed() < 16) {
        localStringBuffer.append('0');
      }
      localStringBuffer.append(Integer.toHexString(paramColor.getRed()));
      if (paramColor.getGreen() < 16) {
        localStringBuffer.append('0');
      }
      localStringBuffer.append(Integer.toHexString(paramColor.getGreen()));
      if (paramColor.getBlue() < 16) {
        localStringBuffer.append('0');
      }
      localStringBuffer.append(Integer.toHexString(paramColor.getBlue()));
      localStringBuffer.append(" ; ");
    }
    localStringBuffer.append(" }");
    return localStringBuffer.toString();
  }
  
  public static Object makeIcon(Class<?> paramClass1, final Class<?> paramClass2, final String paramString)
  {
    new UIDefaults.LazyValue()
    {
      public Object createValue(UIDefaults paramAnonymousUIDefaults)
      {
        byte[] arrayOfByte = (byte[])AccessController.doPrivileged(new PrivilegedAction()
        {
          public byte[] run()
          {
            try
            {
              InputStream localInputStream = null;
              for (Class localClass = val$baseClass; localClass != null; localClass = localClass.getSuperclass())
              {
                localInputStream = localClass.getResourceAsStream(val$imageFile);
                if ((localInputStream != null) || (localClass == val$rootClass)) {
                  break;
                }
              }
              if (localInputStream == null) {
                return null;
              }
              BufferedInputStream localBufferedInputStream = new BufferedInputStream(localInputStream);
              ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(1024);
              byte[] arrayOfByte = new byte['Ѐ'];
              int i;
              while ((i = localBufferedInputStream.read(arrayOfByte)) > 0) {
                localByteArrayOutputStream.write(arrayOfByte, 0, i);
              }
              localBufferedInputStream.close();
              localByteArrayOutputStream.flush();
              return localByteArrayOutputStream.toByteArray();
            }
            catch (IOException localIOException)
            {
              System.err.println(localIOException.toString());
            }
            return null;
          }
        });
        if (arrayOfByte == null) {
          return null;
        }
        if (arrayOfByte.length == 0)
        {
          System.err.println("warning: " + paramString + " is zero-length");
          return null;
        }
        return new ImageIconUIResource(arrayOfByte);
      }
    };
  }
  
  public static boolean isLocalDisplay()
  {
    GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    boolean bool;
    if ((localGraphicsEnvironment instanceof SunGraphicsEnvironment)) {
      bool = ((SunGraphicsEnvironment)localGraphicsEnvironment).isDisplayLocal();
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static int getUIDefaultsInt(Object paramObject)
  {
    return getUIDefaultsInt(paramObject, 0);
  }
  
  public static int getUIDefaultsInt(Object paramObject, Locale paramLocale)
  {
    return getUIDefaultsInt(paramObject, paramLocale, 0);
  }
  
  public static int getUIDefaultsInt(Object paramObject, int paramInt)
  {
    return getUIDefaultsInt(paramObject, null, paramInt);
  }
  
  public static int getUIDefaultsInt(Object paramObject, Locale paramLocale, int paramInt)
  {
    Object localObject = UIManager.get(paramObject, paramLocale);
    if ((localObject instanceof Integer)) {
      return ((Integer)localObject).intValue();
    }
    if ((localObject instanceof String)) {
      try
      {
        return Integer.parseInt((String)localObject);
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return paramInt;
  }
  
  public static Component compositeRequestFocus(Component paramComponent)
  {
    if ((paramComponent instanceof Container))
    {
      Container localContainer = (Container)paramComponent;
      Object localObject2;
      if (localContainer.isFocusCycleRoot())
      {
        localObject1 = localContainer.getFocusTraversalPolicy();
        localObject2 = ((FocusTraversalPolicy)localObject1).getDefaultComponent(localContainer);
        if (localObject2 != null)
        {
          ((Component)localObject2).requestFocus();
          return (Component)localObject2;
        }
      }
      Object localObject1 = localContainer.getFocusCycleRootAncestor();
      if (localObject1 != null)
      {
        localObject2 = ((Container)localObject1).getFocusTraversalPolicy();
        Component localComponent = ((FocusTraversalPolicy)localObject2).getComponentAfter((Container)localObject1, localContainer);
        if ((localComponent != null) && (SwingUtilities.isDescendingFrom(localComponent, localContainer)))
        {
          localComponent.requestFocus();
          return localComponent;
        }
      }
    }
    if (paramComponent.isFocusable())
    {
      paramComponent.requestFocus();
      return paramComponent;
    }
    return null;
  }
  
  public static boolean tabbedPaneChangeFocusTo(Component paramComponent)
  {
    if (paramComponent != null)
    {
      if (paramComponent.isFocusTraversable())
      {
        compositeRequestFocus(paramComponent);
        return true;
      }
      if (((paramComponent instanceof JComponent)) && (((JComponent)paramComponent).requestDefaultFocus())) {
        return true;
      }
    }
    return false;
  }
  
  public static <V> Future<V> submit(Callable<V> paramCallable)
  {
    if (paramCallable == null) {
      throw new NullPointerException();
    }
    FutureTask localFutureTask = new FutureTask(paramCallable);
    execute(localFutureTask);
    return localFutureTask;
  }
  
  public static <V> Future<V> submit(Runnable paramRunnable, V paramV)
  {
    if (paramRunnable == null) {
      throw new NullPointerException();
    }
    FutureTask localFutureTask = new FutureTask(paramRunnable, paramV);
    execute(localFutureTask);
    return localFutureTask;
  }
  
  private static void execute(Runnable paramRunnable)
  {
    SwingUtilities.invokeLater(paramRunnable);
  }
  
  public static void setSkipClickCount(Component paramComponent, int paramInt)
  {
    if (((paramComponent instanceof JTextComponent)) && ((((JTextComponent)paramComponent).getCaret() instanceof DefaultCaret))) {
      ((JTextComponent)paramComponent).putClientProperty(SKIP_CLICK_COUNT, Integer.valueOf(paramInt));
    }
  }
  
  public static int getAdjustedClickCount(JTextComponent paramJTextComponent, MouseEvent paramMouseEvent)
  {
    int i = paramMouseEvent.getClickCount();
    if (i == 1)
    {
      paramJTextComponent.putClientProperty(SKIP_CLICK_COUNT, null);
    }
    else
    {
      Integer localInteger = (Integer)paramJTextComponent.getClientProperty(SKIP_CLICK_COUNT);
      if (localInteger != null) {
        return i - localInteger.intValue();
      }
    }
    return i;
  }
  
  private static Section liesIn(Rectangle paramRectangle, Point paramPoint, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    int i;
    int j;
    int k;
    boolean bool;
    if (paramBoolean1)
    {
      i = x;
      j = x;
      k = width;
      bool = paramBoolean2;
    }
    else
    {
      i = y;
      j = y;
      k = height;
      bool = true;
    }
    if (paramBoolean3)
    {
      m = k >= 30 ? 10 : k / 3;
      if (j < i + m) {
        return bool ? Section.LEADING : Section.TRAILING;
      }
      if (j >= i + k - m) {
        return bool ? Section.TRAILING : Section.LEADING;
      }
      return Section.MIDDLE;
    }
    int m = i + k / 2;
    if (bool) {
      return j >= m ? Section.TRAILING : Section.LEADING;
    }
    return j < m ? Section.TRAILING : Section.LEADING;
  }
  
  public static Section liesInHorizontal(Rectangle paramRectangle, Point paramPoint, boolean paramBoolean1, boolean paramBoolean2)
  {
    return liesIn(paramRectangle, paramPoint, true, paramBoolean1, paramBoolean2);
  }
  
  public static Section liesInVertical(Rectangle paramRectangle, Point paramPoint, boolean paramBoolean)
  {
    return liesIn(paramRectangle, paramPoint, false, false, paramBoolean);
  }
  
  public static int convertColumnIndexToModel(TableColumnModel paramTableColumnModel, int paramInt)
  {
    if (paramInt < 0) {
      return paramInt;
    }
    return paramTableColumnModel.getColumn(paramInt).getModelIndex();
  }
  
  public static int convertColumnIndexToView(TableColumnModel paramTableColumnModel, int paramInt)
  {
    if (paramInt < 0) {
      return paramInt;
    }
    for (int i = 0; i < paramTableColumnModel.getColumnCount(); i++) {
      if (paramTableColumnModel.getColumn(i).getModelIndex() == paramInt) {
        return i;
      }
    }
    return -1;
  }
  
  public static int getSystemMnemonicKeyMask()
  {
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    if ((localToolkit instanceof SunToolkit)) {
      return ((SunToolkit)localToolkit).getFocusAcceleratorKeyMask();
    }
    return 8;
  }
  
  public static TreePath getTreePath(TreeModelEvent paramTreeModelEvent, TreeModel paramTreeModel)
  {
    TreePath localTreePath = paramTreeModelEvent.getTreePath();
    if ((localTreePath == null) && (paramTreeModel != null))
    {
      Object localObject = paramTreeModel.getRoot();
      if (localObject != null) {
        localTreePath = new TreePath(localObject);
      }
    }
    return localTreePath;
  }
  
  static
  {
    LAF_STATE_KEY = new StringBuffer("LookAndFeel State");
    MENU_SELECTION_MANAGER_LISTENER_KEY = new StringBuffer("MenuSelectionManager listener key");
    DEFAULT_FRC = new FontRenderContext(null, false, false);
    AA_TEXT_PROPERTY_KEY = new StringBuffer("AATextInfoPropertyKey");
    SKIP_CLICK_COUNT = new StringBuilder("skipClickCount");
    COMPONENT_UI_PROPERTY_KEY = new StringBuffer("ComponentUIPropertyKey");
    BASICMENUITEMUI_MAX_TEXT_OFFSET = new StringUIClientPropertyKey("maxTextOffset");
    inputEvent_CanAccessSystemClipboard_Field = null;
    charsBufferLock = new Object();
    charsBuffer = new char[100];
  }
  
  public static class AATextInfo
  {
    Object aaHint;
    Integer lcdContrastHint;
    FontRenderContext frc;
    
    private static AATextInfo getAATextInfoFromMap(Map paramMap)
    {
      Object localObject1 = paramMap.get(RenderingHints.KEY_TEXT_ANTIALIASING);
      Object localObject2 = paramMap.get(RenderingHints.KEY_TEXT_LCD_CONTRAST);
      if ((localObject1 == null) || (localObject1 == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF) || (localObject1 == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT)) {
        return null;
      }
      return new AATextInfo(localObject1, (Integer)localObject2);
    }
    
    public static AATextInfo getAATextInfo(boolean paramBoolean)
    {
      SunToolkit.setAAFontSettingsCondition(paramBoolean);
      Toolkit localToolkit = Toolkit.getDefaultToolkit();
      Object localObject = localToolkit.getDesktopProperty("awt.font.desktophints");
      if ((localObject instanceof Map)) {
        return getAATextInfoFromMap((Map)localObject);
      }
      return null;
    }
    
    public AATextInfo(Object paramObject, Integer paramInteger)
    {
      if (paramObject == null) {
        throw new InternalError("null not allowed here");
      }
      if ((paramObject == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF) || (paramObject == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT)) {
        throw new InternalError("AA must be on");
      }
      aaHint = paramObject;
      lcdContrastHint = paramInteger;
      frc = new FontRenderContext(null, paramObject, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
    }
  }
  
  private static class LSBCacheEntry
  {
    private static final byte UNSET = 127;
    private static final char[] oneChar = new char[1];
    private byte[] lsbCache = new byte[1];
    private Font font;
    private FontRenderContext frc;
    
    public LSBCacheEntry(FontRenderContext paramFontRenderContext, Font paramFont)
    {
      reset(paramFontRenderContext, paramFont);
    }
    
    public void reset(FontRenderContext paramFontRenderContext, Font paramFont)
    {
      font = paramFont;
      frc = paramFontRenderContext;
      for (int i = lsbCache.length - 1; i >= 0; i--) {
        lsbCache[i] = Byte.MAX_VALUE;
      }
    }
    
    public int getLeftSideBearing(char paramChar)
    {
      int i = paramChar - 'W';
      assert ((i >= 0) && (i < 1));
      int j = lsbCache[i];
      if (j == 127)
      {
        oneChar[0] = paramChar;
        GlyphVector localGlyphVector = font.createGlyphVector(frc, oneChar);
        j = (byte)getGlyphPixelBounds0frc, 0.0F, 0.0F).x;
        if (j < 0)
        {
          Object localObject = frc.getAntiAliasingHint();
          if ((localObject == RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB) || (localObject == RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR)) {
            j = (byte)(j + 1);
          }
        }
        lsbCache[i] = j;
      }
      return j;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if (!(paramObject instanceof LSBCacheEntry)) {
        return false;
      }
      LSBCacheEntry localLSBCacheEntry = (LSBCacheEntry)paramObject;
      return (font.equals(font)) && (frc.equals(frc));
    }
    
    public int hashCode()
    {
      int i = 17;
      if (font != null) {
        i = 37 * i + font.hashCode();
      }
      if (frc != null) {
        i = 37 * i + frc.hashCode();
      }
      return i;
    }
  }
  
  public static abstract interface RepaintListener
  {
    public abstract void repaintPerformed(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  }
  
  public static enum Section
  {
    LEADING,  MIDDLE,  TRAILING;
    
    private Section() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\SwingUtilities2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */