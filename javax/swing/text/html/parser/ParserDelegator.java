package javax.swing.text.html.parser;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.text.html.HTMLEditorKit.Parser;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import sun.awt.AppContext;

public class ParserDelegator
  extends HTMLEditorKit.Parser
  implements Serializable
{
  private static final Object DTD_KEY = new Object();
  
  protected static void setDefaultDTD()
  {
    getDefaultDTD();
  }
  
  private static synchronized DTD getDefaultDTD()
  {
    AppContext localAppContext = AppContext.getAppContext();
    DTD localDTD1 = (DTD)localAppContext.get(DTD_KEY);
    if (localDTD1 == null)
    {
      DTD localDTD2 = null;
      String str = "html32";
      try
      {
        localDTD2 = DTD.getDTD(str);
      }
      catch (IOException localIOException)
      {
        System.out.println("Throw an exception: could not get default dtd: " + str);
      }
      localDTD1 = createDTD(localDTD2, str);
      localAppContext.put(DTD_KEY, localDTD1);
    }
    return localDTD1;
  }
  
  protected static DTD createDTD(DTD paramDTD, String paramString)
  {
    InputStream localInputStream = null;
    int i = 1;
    try
    {
      String str = paramString + ".bdtd";
      localInputStream = getResourceAsStream(str);
      if (localInputStream != null)
      {
        paramDTD.read(new DataInputStream(new BufferedInputStream(localInputStream)));
        DTD.putDTDHash(paramString, paramDTD);
      }
    }
    catch (Exception localException)
    {
      System.out.println(localException);
    }
    return paramDTD;
  }
  
  public ParserDelegator()
  {
    setDefaultDTD();
  }
  
  public void parse(Reader paramReader, HTMLEditorKit.ParserCallback paramParserCallback, boolean paramBoolean)
    throws IOException
  {
    new DocumentParser(getDefaultDTD()).parse(paramReader, paramParserCallback, paramBoolean);
  }
  
  static InputStream getResourceAsStream(String paramString)
  {
    (InputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public InputStream run()
      {
        return ParserDelegator.class.getResourceAsStream(val$name);
      }
    });
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    setDefaultDTD();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\parser\ParserDelegator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */