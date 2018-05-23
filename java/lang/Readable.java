package java.lang;

import java.io.IOException;
import java.nio.CharBuffer;

public abstract interface Readable
{
  public abstract int read(CharBuffer paramCharBuffer)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\Readable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */