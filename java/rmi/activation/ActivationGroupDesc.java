package java.rmi.activation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.util.Arrays;
import java.util.Properties;

public final class ActivationGroupDesc
  implements Serializable
{
  private String className;
  private String location;
  private MarshalledObject<?> data;
  private CommandEnvironment env;
  private Properties props;
  private static final long serialVersionUID = -4936225423168276595L;
  
  public ActivationGroupDesc(Properties paramProperties, CommandEnvironment paramCommandEnvironment)
  {
    this(null, null, null, paramProperties, paramCommandEnvironment);
  }
  
  public ActivationGroupDesc(String paramString1, String paramString2, MarshalledObject<?> paramMarshalledObject, Properties paramProperties, CommandEnvironment paramCommandEnvironment)
  {
    props = paramProperties;
    env = paramCommandEnvironment;
    data = paramMarshalledObject;
    location = paramString2;
    className = paramString1;
  }
  
  public String getClassName()
  {
    return className;
  }
  
  public String getLocation()
  {
    return location;
  }
  
  public MarshalledObject<?> getData()
  {
    return data;
  }
  
  public Properties getPropertyOverrides()
  {
    return props != null ? (Properties)props.clone() : null;
  }
  
  public CommandEnvironment getCommandEnvironment()
  {
    return env;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ActivationGroupDesc))
    {
      ActivationGroupDesc localActivationGroupDesc = (ActivationGroupDesc)paramObject;
      return (className == null ? className == null : className.equals(className)) && (location == null ? location == null : location.equals(location)) && (data == null ? data == null : data.equals(data)) && (env == null ? env == null : env.equals(env)) && (props == null ? props == null : props.equals(props));
    }
    return false;
  }
  
  public int hashCode()
  {
    return (location == null ? 0 : location.hashCode() << 24) ^ (env == null ? 0 : env.hashCode() << 16) ^ (className == null ? 0 : className.hashCode() << 8) ^ (data == null ? 0 : data.hashCode());
  }
  
  public static class CommandEnvironment
    implements Serializable
  {
    private static final long serialVersionUID = 6165754737887770191L;
    private String command;
    private String[] options;
    
    public CommandEnvironment(String paramString, String[] paramArrayOfString)
    {
      command = paramString;
      if (paramArrayOfString == null)
      {
        options = new String[0];
      }
      else
      {
        options = new String[paramArrayOfString.length];
        System.arraycopy(paramArrayOfString, 0, options, 0, paramArrayOfString.length);
      }
    }
    
    public String getCommandPath()
    {
      return command;
    }
    
    public String[] getCommandOptions()
    {
      return (String[])options.clone();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof CommandEnvironment))
      {
        CommandEnvironment localCommandEnvironment = (CommandEnvironment)paramObject;
        return (command == null ? command == null : command.equals(command)) && (Arrays.equals(options, options));
      }
      return false;
    }
    
    public int hashCode()
    {
      return command == null ? 0 : command.hashCode();
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      paramObjectInputStream.defaultReadObject();
      if (options == null) {
        options = new String[0];
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\activation\ActivationGroupDesc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */