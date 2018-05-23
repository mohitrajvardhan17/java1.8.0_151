package sun.security.x509;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class GeneralNames
{
  private final List<GeneralName> names = new ArrayList();
  
  public GeneralNames(DerValue paramDerValue)
    throws IOException
  {
    this();
    if (tag != 48) {
      throw new IOException("Invalid encoding for GeneralNames.");
    }
    if (data.available() == 0) {
      throw new IOException("No data available in passed DER encoded value.");
    }
    while (data.available() != 0)
    {
      DerValue localDerValue = data.getDerValue();
      GeneralName localGeneralName = new GeneralName(localDerValue);
      add(localGeneralName);
    }
  }
  
  public GeneralNames() {}
  
  public GeneralNames add(GeneralName paramGeneralName)
  {
    if (paramGeneralName == null) {
      throw new NullPointerException();
    }
    names.add(paramGeneralName);
    return this;
  }
  
  public GeneralName get(int paramInt)
  {
    return (GeneralName)names.get(paramInt);
  }
  
  public boolean isEmpty()
  {
    return names.isEmpty();
  }
  
  public int size()
  {
    return names.size();
  }
  
  public Iterator<GeneralName> iterator()
  {
    return names.iterator();
  }
  
  public List<GeneralName> names()
  {
    return names;
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    if (isEmpty()) {
      return;
    }
    DerOutputStream localDerOutputStream = new DerOutputStream();
    Iterator localIterator = names.iterator();
    while (localIterator.hasNext())
    {
      GeneralName localGeneralName = (GeneralName)localIterator.next();
      localGeneralName.encode(localDerOutputStream);
    }
    paramDerOutputStream.write((byte)48, localDerOutputStream);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof GeneralNames)) {
      return false;
    }
    GeneralNames localGeneralNames = (GeneralNames)paramObject;
    return names.equals(names);
  }
  
  public int hashCode()
  {
    return names.hashCode();
  }
  
  public String toString()
  {
    return names.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\GeneralNames.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */