package java.util.jar;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JarOutputStream
  extends ZipOutputStream
{
  private static final int JAR_MAGIC = 51966;
  private boolean firstEntry = true;
  
  public JarOutputStream(OutputStream paramOutputStream, Manifest paramManifest)
    throws IOException
  {
    super(paramOutputStream);
    if (paramManifest == null) {
      throw new NullPointerException("man");
    }
    ZipEntry localZipEntry = new ZipEntry("META-INF/MANIFEST.MF");
    putNextEntry(localZipEntry);
    paramManifest.write(new BufferedOutputStream(this));
    closeEntry();
  }
  
  public JarOutputStream(OutputStream paramOutputStream)
    throws IOException
  {
    super(paramOutputStream);
  }
  
  public void putNextEntry(ZipEntry paramZipEntry)
    throws IOException
  {
    if (firstEntry)
    {
      Object localObject = paramZipEntry.getExtra();
      if ((localObject == null) || (!hasMagic((byte[])localObject)))
      {
        if (localObject == null)
        {
          localObject = new byte[4];
        }
        else
        {
          byte[] arrayOfByte = new byte[localObject.length + 4];
          System.arraycopy(localObject, 0, arrayOfByte, 4, localObject.length);
          localObject = arrayOfByte;
        }
        set16((byte[])localObject, 0, 51966);
        set16((byte[])localObject, 2, 0);
        paramZipEntry.setExtra((byte[])localObject);
      }
      firstEntry = false;
    }
    super.putNextEntry(paramZipEntry);
  }
  
  private static boolean hasMagic(byte[] paramArrayOfByte)
  {
    try
    {
      int i = 0;
      while (i < paramArrayOfByte.length)
      {
        if (get16(paramArrayOfByte, i) == 51966) {
          return true;
        }
        i += get16(paramArrayOfByte, i + 2) + 4;
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    return false;
  }
  
  private static int get16(byte[] paramArrayOfByte, int paramInt)
  {
    return Byte.toUnsignedInt(paramArrayOfByte[paramInt]) | Byte.toUnsignedInt(paramArrayOfByte[(paramInt + 1)]) << 8;
  }
  
  private static void set16(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte[(paramInt1 + 0)] = ((byte)paramInt2);
    paramArrayOfByte[(paramInt1 + 1)] = ((byte)(paramInt2 >> 8));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\jar\JarOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */