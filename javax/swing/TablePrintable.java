package javax.swing;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

class TablePrintable
  implements Printable
{
  private JTable table;
  private JTableHeader header;
  private TableColumnModel colModel;
  private int totalColWidth;
  private JTable.PrintMode printMode;
  private MessageFormat headerFormat;
  private MessageFormat footerFormat;
  private int last = -1;
  private int row = 0;
  private int col = 0;
  private final Rectangle clip = new Rectangle(0, 0, 0, 0);
  private final Rectangle hclip = new Rectangle(0, 0, 0, 0);
  private final Rectangle tempRect = new Rectangle(0, 0, 0, 0);
  private static final int H_F_SPACE = 8;
  private static final float HEADER_FONT_SIZE = 18.0F;
  private static final float FOOTER_FONT_SIZE = 12.0F;
  private Font headerFont;
  private Font footerFont;
  
  public TablePrintable(JTable paramJTable, JTable.PrintMode paramPrintMode, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2)
  {
    table = paramJTable;
    header = paramJTable.getTableHeader();
    colModel = paramJTable.getColumnModel();
    totalColWidth = colModel.getTotalColumnWidth();
    if (header != null) {
      hclip.height = header.getHeight();
    }
    printMode = paramPrintMode;
    headerFormat = paramMessageFormat1;
    footerFormat = paramMessageFormat2;
    headerFont = paramJTable.getFont().deriveFont(1, 18.0F);
    footerFont = paramJTable.getFont().deriveFont(0, 12.0F);
  }
  
  public int print(Graphics paramGraphics, PageFormat paramPageFormat, int paramInt)
    throws PrinterException
  {
    int i = (int)paramPageFormat.getImageableWidth();
    int j = (int)paramPageFormat.getImageableHeight();
    if (i <= 0) {
      throw new PrinterException("Width of printable area is too small.");
    }
    Object[] arrayOfObject = { Integer.valueOf(paramInt + 1) };
    String str1 = null;
    if (headerFormat != null) {
      str1 = headerFormat.format(arrayOfObject);
    }
    String str2 = null;
    if (footerFormat != null) {
      str2 = footerFormat.format(arrayOfObject);
    }
    Rectangle2D localRectangle2D1 = null;
    Rectangle2D localRectangle2D2 = null;
    int k = 0;
    int m = 0;
    int n = j;
    if (str1 != null)
    {
      paramGraphics.setFont(headerFont);
      localRectangle2D1 = paramGraphics.getFontMetrics().getStringBounds(str1, paramGraphics);
      k = (int)Math.ceil(localRectangle2D1.getHeight());
      n -= k + 8;
    }
    if (str2 != null)
    {
      paramGraphics.setFont(footerFont);
      localRectangle2D2 = paramGraphics.getFontMetrics().getStringBounds(str2, paramGraphics);
      m = (int)Math.ceil(localRectangle2D2.getHeight());
      n -= m + 8;
    }
    if (n <= 0) {
      throw new PrinterException("Height of printable area is too small.");
    }
    double d = 1.0D;
    if ((printMode == JTable.PrintMode.FIT_WIDTH) && (totalColWidth > i))
    {
      assert (i > 0);
      assert (totalColWidth > 1);
      d = i / totalColWidth;
    }
    assert (d > 0.0D);
    while (last < paramInt)
    {
      if ((row >= table.getRowCount()) && (col == 0)) {
        return 1;
      }
      int i1 = (int)(i / d);
      int i2 = (int)((n - hclip.height) / d);
      findNextClip(i1, i2);
      last += 1;
    }
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics.create();
    localGraphics2D.translate(paramPageFormat.getImageableX(), paramPageFormat.getImageableY());
    if (str2 != null)
    {
      localAffineTransform = localGraphics2D.getTransform();
      localGraphics2D.translate(0, j - m);
      printText(localGraphics2D, str2, localRectangle2D2, footerFont, i);
      localGraphics2D.setTransform(localAffineTransform);
    }
    if (str1 != null)
    {
      printText(localGraphics2D, str1, localRectangle2D1, headerFont, i);
      localGraphics2D.translate(0, k + 8);
    }
    tempRect.x = 0;
    tempRect.y = 0;
    tempRect.width = i;
    tempRect.height = n;
    localGraphics2D.clip(tempRect);
    if (d != 1.0D)
    {
      localGraphics2D.scale(d, d);
    }
    else
    {
      int i3 = (i - clip.width) / 2;
      localGraphics2D.translate(i3, 0);
    }
    AffineTransform localAffineTransform = localGraphics2D.getTransform();
    Shape localShape = localGraphics2D.getClip();
    if (header != null)
    {
      hclip.x = clip.x;
      hclip.width = clip.width;
      localGraphics2D.translate(-hclip.x, 0);
      localGraphics2D.clip(hclip);
      header.print(localGraphics2D);
      localGraphics2D.setTransform(localAffineTransform);
      localGraphics2D.setClip(localShape);
      localGraphics2D.translate(0, hclip.height);
    }
    localGraphics2D.translate(-clip.x, -clip.y);
    localGraphics2D.clip(clip);
    table.print(localGraphics2D);
    localGraphics2D.setTransform(localAffineTransform);
    localGraphics2D.setClip(localShape);
    localGraphics2D.setColor(Color.BLACK);
    localGraphics2D.drawRect(0, 0, clip.width, hclip.height + clip.height);
    localGraphics2D.dispose();
    return 0;
  }
  
  private void printText(Graphics2D paramGraphics2D, String paramString, Rectangle2D paramRectangle2D, Font paramFont, int paramInt)
  {
    int i;
    if (paramRectangle2D.getWidth() < paramInt) {
      i = (int)((paramInt - paramRectangle2D.getWidth()) / 2.0D);
    } else if (table.getComponentOrientation().isLeftToRight()) {
      i = 0;
    } else {
      i = -(int)(Math.ceil(paramRectangle2D.getWidth()) - paramInt);
    }
    int j = (int)Math.ceil(Math.abs(paramRectangle2D.getY()));
    paramGraphics2D.setColor(Color.BLACK);
    paramGraphics2D.setFont(paramFont);
    paramGraphics2D.drawString(paramString, i, j);
  }
  
  private void findNextClip(int paramInt1, int paramInt2)
  {
    boolean bool = table.getComponentOrientation().isLeftToRight();
    if (col == 0)
    {
      if (bool) {
        clip.x = 0;
      } else {
        clip.x = totalColWidth;
      }
      clip.y += clip.height;
      clip.width = 0;
      clip.height = 0;
      i = table.getRowCount();
      j = table.getRowHeight(row);
      do
      {
        clip.height += j;
        if (++row >= i) {
          break;
        }
        j = table.getRowHeight(row);
      } while (clip.height + j <= paramInt2);
    }
    if (printMode == JTable.PrintMode.FIT_WIDTH)
    {
      clip.x = 0;
      clip.width = totalColWidth;
      return;
    }
    if (bool) {
      clip.x += clip.width;
    }
    clip.width = 0;
    int i = table.getColumnCount();
    int j = colModel.getColumn(col).getWidth();
    do
    {
      clip.width += j;
      if (!bool) {
        clip.x -= j;
      }
      if (++col >= i)
      {
        col = 0;
        break;
      }
      j = colModel.getColumn(col).getWidth();
    } while (clip.width + j <= paramInt1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\TablePrintable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */