package com.oracle.webservices.internal.api.databinding;

import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.model.ExternalMetadataReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.ws.WebServiceFeature;

public class ExternalMetadataFeature
  extends WebServiceFeature
{
  private static final String ID = "com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature";
  private boolean enabled = true;
  private List<String> resourceNames;
  private List<File> files;
  private MetadataReader reader;
  
  private ExternalMetadataFeature() {}
  
  public void addResources(String... paramVarArgs)
  {
    if (resourceNames == null) {
      resourceNames = new ArrayList();
    }
    Collections.addAll(resourceNames, paramVarArgs);
  }
  
  public List<String> getResourceNames()
  {
    return resourceNames;
  }
  
  public void addFiles(File... paramVarArgs)
  {
    if (files == null) {
      files = new ArrayList();
    }
    Collections.addAll(files, paramVarArgs);
  }
  
  public List<File> getFiles()
  {
    return files;
  }
  
  public boolean isEnabled()
  {
    return enabled;
  }
  
  private void setEnabled(boolean paramBoolean)
  {
    enabled = paramBoolean;
  }
  
  public String getID()
  {
    return "com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature";
  }
  
  public MetadataReader getMetadataReader(ClassLoader paramClassLoader, boolean paramBoolean)
  {
    if ((reader != null) && (enabled)) {
      return reader;
    }
    return enabled ? new ExternalMetadataReader(files, resourceNames, paramClassLoader, true, paramBoolean) : null;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    ExternalMetadataFeature localExternalMetadataFeature = (ExternalMetadataFeature)paramObject;
    if (enabled != enabled) {
      return false;
    }
    if (files != null ? !files.equals(files) : files != null) {
      return false;
    }
    return resourceNames != null ? resourceNames.equals(resourceNames) : resourceNames == null;
  }
  
  public int hashCode()
  {
    int i = enabled ? 1 : 0;
    i = 31 * i + (resourceNames != null ? resourceNames.hashCode() : 0);
    i = 31 * i + (files != null ? files.hashCode() : 0);
    return i;
  }
  
  public String toString()
  {
    return "[" + getID() + ", enabled=" + enabled + ", resourceNames=" + resourceNames + ", files=" + files + ']';
  }
  
  public static Builder builder()
  {
    return new Builder(new ExternalMetadataFeature());
  }
  
  public static final class Builder
  {
    private final ExternalMetadataFeature o;
    
    Builder(ExternalMetadataFeature paramExternalMetadataFeature)
    {
      o = paramExternalMetadataFeature;
    }
    
    public ExternalMetadataFeature build()
    {
      return o;
    }
    
    public Builder addResources(String... paramVarArgs)
    {
      o.addResources(paramVarArgs);
      return this;
    }
    
    public Builder addFiles(File... paramVarArgs)
    {
      o.addFiles(paramVarArgs);
      return this;
    }
    
    public Builder setEnabled(boolean paramBoolean)
    {
      o.setEnabled(paramBoolean);
      return this;
    }
    
    public Builder setReader(MetadataReader paramMetadataReader)
    {
      o.reader = paramMetadataReader;
      return this;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\webservices\internal\api\databinding\ExternalMetadataFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */