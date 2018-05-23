package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;

public class EntityReferenceEvent
  extends DummyEvent
  implements EntityReference
{
  private EntityDeclaration fEntityDeclaration;
  private String fEntityName;
  
  public EntityReferenceEvent()
  {
    init();
  }
  
  public EntityReferenceEvent(String paramString, EntityDeclaration paramEntityDeclaration)
  {
    init();
    fEntityName = paramString;
    fEntityDeclaration = paramEntityDeclaration;
  }
  
  public String getName()
  {
    return fEntityName;
  }
  
  public String toString()
  {
    String str = fEntityDeclaration.getReplacementText();
    if (str == null) {
      str = "";
    }
    return "&" + getName() + ";='" + str + "'";
  }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter)
    throws IOException
  {
    paramWriter.write(38);
    paramWriter.write(getName());
    paramWriter.write(59);
  }
  
  public EntityDeclaration getDeclaration()
  {
    return fEntityDeclaration;
  }
  
  protected void init()
  {
    setEventType(9);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\EntityReferenceEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */