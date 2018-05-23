package sun.awt.windows;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import sun.awt.CustomCursor;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.IntegerComponentRaster;
import sun.awt.image.ToolkitImage;

final class WCustomCursor
  extends CustomCursor
{
  WCustomCursor(Image paramImage, Point paramPoint, String paramString)
    throws IndexOutOfBoundsException
  {
    super(paramImage, paramPoint, paramString);
  }
  
  protected void createNativeCursor(Image paramImage, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    BufferedImage localBufferedImage = new BufferedImage(paramInt1, paramInt2, 1);
    Graphics localGraphics = localBufferedImage.getGraphics();
    try
    {
      if ((paramImage instanceof ToolkitImage))
      {
        localObject1 = ((ToolkitImage)paramImage).getImageRep();
        ((ImageRepresentation)localObject1).reconstruct(32);
      }
      localGraphics.drawImage(paramImage, 0, 0, paramInt1, paramInt2, null);
    }
    finally
    {
      localGraphics.dispose();
    }
    Object localObject1 = localBufferedImage.getRaster();
    DataBuffer localDataBuffer = ((Raster)localObject1).getDataBuffer();
    int[] arrayOfInt = ((DataBufferInt)localDataBuffer).getData();
    byte[] arrayOfByte = new byte[paramInt1 * paramInt2 / 8];
    int i = paramArrayOfInt.length;
    for (int j = 0; j < i; j++)
    {
      int k = j / 8;
      int m = 1 << 7 - j % 8;
      if ((paramArrayOfInt[j] & 0xFF000000) == 0)
      {
        int tmp156_154 = k;
        byte[] tmp156_152 = arrayOfByte;
        tmp156_152[tmp156_154] = ((byte)(tmp156_152[tmp156_154] | m));
      }
    }
    j = ((Raster)localObject1).getWidth();
    if ((localObject1 instanceof IntegerComponentRaster)) {
      j = ((IntegerComponentRaster)localObject1).getScanlineStride();
    }
    createCursorIndirect(((DataBufferInt)localBufferedImage.getRaster().getDataBuffer()).getData(), arrayOfByte, j, ((Raster)localObject1).getWidth(), ((Raster)localObject1).getHeight(), paramInt3, paramInt4);
  }
  
  private native void createCursorIndirect(int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  static native int getCursorWidth();
  
  static native int getCursorHeight();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WCustomCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */