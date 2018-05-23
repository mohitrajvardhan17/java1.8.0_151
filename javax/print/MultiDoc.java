package javax.print;

import java.io.IOException;

public abstract interface MultiDoc
{
  public abstract Doc getDoc()
    throws IOException;
  
  public abstract MultiDoc next()
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\MultiDoc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */