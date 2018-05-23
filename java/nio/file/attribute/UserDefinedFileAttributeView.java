package java.nio.file.attribute;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public abstract interface UserDefinedFileAttributeView
  extends FileAttributeView
{
  public abstract String name();
  
  public abstract List<String> list()
    throws IOException;
  
  public abstract int size(String paramString)
    throws IOException;
  
  public abstract int read(String paramString, ByteBuffer paramByteBuffer)
    throws IOException;
  
  public abstract int write(String paramString, ByteBuffer paramByteBuffer)
    throws IOException;
  
  public abstract void delete(String paramString)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\file\attribute\UserDefinedFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */