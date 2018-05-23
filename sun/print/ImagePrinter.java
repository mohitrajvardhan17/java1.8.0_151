package sun.print;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

class ImagePrinter
  implements Printable
{
  BufferedImage image;
  
  ImagePrinter(InputStream paramInputStream)
  {
    try
    {
      image = ImageIO.read(paramInputStream);
    }
    catch (Exception localException) {}
  }
  
  ImagePrinter(URL paramURL)
  {
    try
    {
      image = ImageIO.read(paramURL);
    }
    catch (Exception localException) {}
  }
  
  public int print(Graphics paramGraphics, PageFormat paramPageFormat, int paramInt)
  {
    if ((paramInt > 0) || (image == null)) {
      return 1;
    }
    ((Graphics2D)paramGraphics).translate(paramPageFormat.getImageableX(), paramPageFormat.getImageableY());
    int i = image.getWidth(null);
    int j = image.getHeight(null);
    int k = (int)paramPageFormat.getImageableWidth();
    int m = (int)paramPageFormat.getImageableHeight();
    int n = i;
    int i1 = j;
    if (n > k)
    {
      i1 = (int)(i1 * (k / n));
      n = k;
    }
    if (i1 > m)
    {
      n = (int)(n * (m / i1));
      i1 = m;
    }
    int i2 = (k - n) / 2;
    int i3 = (m - i1) / 2;
    paramGraphics.drawImage(image, i2, i3, i2 + n, i3 + i1, 0, 0, i, j, null);
    return 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\ImagePrinter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */