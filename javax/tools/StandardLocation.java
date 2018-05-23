package javax.tools;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum StandardLocation
  implements JavaFileManager.Location
{
  CLASS_OUTPUT,  SOURCE_OUTPUT,  CLASS_PATH,  SOURCE_PATH,  ANNOTATION_PROCESSOR_PATH,  PLATFORM_CLASS_PATH,  NATIVE_HEADER_OUTPUT;
  
  private static final ConcurrentMap<String, JavaFileManager.Location> locations = new ConcurrentHashMap();
  
  private StandardLocation() {}
  
  public static JavaFileManager.Location locationFor(String paramString)
  {
    if (locations.isEmpty()) {
      for (StandardLocation localStandardLocation : values()) {
        locations.putIfAbsent(localStandardLocation.getName(), localStandardLocation);
      }
    }
    locations.putIfAbsent(paramString.toString(), new JavaFileManager.Location()
    {
      public String getName()
      {
        return val$name;
      }
      
      public boolean isOutputLocation()
      {
        return val$name.endsWith("_OUTPUT");
      }
    });
    return (JavaFileManager.Location)locations.get(paramString);
  }
  
  public String getName()
  {
    return name();
  }
  
  public boolean isOutputLocation()
  {
    switch (this)
    {
    case CLASS_OUTPUT: 
    case SOURCE_OUTPUT: 
    case NATIVE_HEADER_OUTPUT: 
      return true;
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\tools\StandardLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */