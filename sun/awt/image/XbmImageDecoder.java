package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XbmImageDecoder
  extends ImageDecoder
{
  private static byte[] XbmColormap = { -1, -1, -1, 0, 0, 0 };
  private static int XbmHints = 30;
  
  public XbmImageDecoder(InputStreamImageSource paramInputStreamImageSource, InputStream paramInputStream)
  {
    super(paramInputStreamImageSource, paramInputStream);
    if (!(input instanceof BufferedInputStream)) {
      input = new BufferedInputStream(input, 80);
    }
  }
  
  private static void error(String paramString)
    throws ImageFormatException
  {
    throw new ImageFormatException(paramString);
  }
  
  public void produceImage()
    throws IOException, ImageFormatException
  {
    char[] arrayOfChar = new char[80];
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 1;
    byte[] arrayOfByte = null;
    Object localObject = null;
    int i;
    while ((!aborted) && ((i = input.read()) != -1)) {
      if (((97 <= i) && (i <= 122)) || ((65 <= i) && (i <= 90)) || ((48 <= i) && (i <= 57)) || (i == 35) || (i == 95))
      {
        if (j < 78) {
          arrayOfChar[(j++)] = ((char)i);
        }
      }
      else if (j > 0)
      {
        int i4 = j;
        j = 0;
        if (i3 != 0)
        {
          if ((i4 != 7) || (arrayOfChar[0] != '#') || (arrayOfChar[1] != 'd') || (arrayOfChar[2] != 'e') || (arrayOfChar[3] != 'f') || (arrayOfChar[4] != 'i') || (arrayOfChar[5] != 'n') || (arrayOfChar[6] != 'e')) {
            error("Not an XBM file");
          }
          i3 = 0;
        }
        if (arrayOfChar[(i4 - 1)] == 'h')
        {
          k = 1;
        }
        else if ((arrayOfChar[(i4 - 1)] == 't') && (i4 > 1) && (arrayOfChar[(i4 - 2)] == 'h'))
        {
          k = 2;
        }
        else
        {
          int i5;
          int i6;
          if ((i4 > 2) && (k < 0) && (arrayOfChar[0] == '0') && (arrayOfChar[1] == 'x'))
          {
            i5 = 0;
            for (i6 = 2; i6 < i4; i6++)
            {
              i = arrayOfChar[i6];
              if ((48 <= i) && (i <= 57)) {
                i -= 48;
              } else if ((65 <= i) && (i <= 90)) {
                i = i - 65 + 10;
              } else if ((97 <= i) && (i <= 122)) {
                i = i - 97 + 10;
              } else {
                i = 0;
              }
              i5 = i5 * 16 + i;
            }
            i6 = 1;
            while (i6 <= 128)
            {
              if (i1 < n) {
                if ((i5 & i6) != 0) {
                  arrayOfByte[i1] = 1;
                } else {
                  arrayOfByte[i1] = 0;
                }
              }
              i1++;
              i6 <<= 1;
            }
            if (i1 >= n)
            {
              if (setPixels(0, i2, n, 1, (ColorModel)localObject, arrayOfByte, 0, n) <= 0) {
                return;
              }
              i1 = 0;
              if (i2++ >= m) {
                break;
              }
            }
          }
          else
          {
            i5 = 0;
            for (i6 = 0; i6 < i4; i6++) {
              if (('0' <= (i = arrayOfChar[i6])) && (i <= 57))
              {
                i5 = i5 * 10 + i - 48;
              }
              else
              {
                i5 = -1;
                break;
              }
            }
            if ((i5 > 0) && (k > 0))
            {
              if (k == 1) {
                n = i5;
              } else {
                m = i5;
              }
              if ((n == 0) || (m == 0))
              {
                k = 0;
              }
              else
              {
                localObject = new IndexColorModel(8, 2, XbmColormap, 0, false, 0);
                setDimensions(n, m);
                setColorModel((ColorModel)localObject);
                setHints(XbmHints);
                headerComplete();
                arrayOfByte = new byte[n];
                k = -1;
              }
            }
          }
        }
      }
    }
    input.close();
    imageComplete(3, true);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\XbmImageDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */