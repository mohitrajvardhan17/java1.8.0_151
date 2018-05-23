package javax.activation;

import java.beans.Beans;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class CommandInfo
{
  private String verb;
  private String className;
  
  public CommandInfo(String paramString1, String paramString2)
  {
    verb = paramString1;
    className = paramString2;
  }
  
  public String getCommandName()
  {
    return verb;
  }
  
  public String getCommandClass()
  {
    return className;
  }
  
  public Object getCommandObject(DataHandler paramDataHandler, ClassLoader paramClassLoader)
    throws IOException, ClassNotFoundException
  {
    Object localObject = null;
    localObject = Beans.instantiate(paramClassLoader, className);
    if (localObject != null) {
      if ((localObject instanceof CommandObject))
      {
        ((CommandObject)localObject).setCommandContext(verb, paramDataHandler);
      }
      else if (((localObject instanceof Externalizable)) && (paramDataHandler != null))
      {
        InputStream localInputStream = paramDataHandler.getInputStream();
        if (localInputStream != null) {
          ((Externalizable)localObject).readExternal(new ObjectInputStream(localInputStream));
        }
      }
    }
    return localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\activation\CommandInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */