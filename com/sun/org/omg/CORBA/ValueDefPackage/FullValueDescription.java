package com.sun.org.omg.CORBA.ValueDefPackage;

import com.sun.org.omg.CORBA.AttributeDescription;
import com.sun.org.omg.CORBA.Initializer;
import com.sun.org.omg.CORBA.OperationDescription;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.portable.IDLEntity;

public final class FullValueDescription
  implements IDLEntity
{
  public String name = null;
  public String id = null;
  public boolean is_abstract = false;
  public boolean is_custom = false;
  public String defined_in = null;
  public String version = null;
  public OperationDescription[] operations = null;
  public AttributeDescription[] attributes = null;
  public ValueMember[] members = null;
  public Initializer[] initializers = null;
  public String[] supported_interfaces = null;
  public String[] abstract_base_values = null;
  public boolean is_truncatable = false;
  public String base_value = null;
  public TypeCode type = null;
  
  public FullValueDescription() {}
  
  public FullValueDescription(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, String paramString3, String paramString4, OperationDescription[] paramArrayOfOperationDescription, AttributeDescription[] paramArrayOfAttributeDescription, ValueMember[] paramArrayOfValueMember, Initializer[] paramArrayOfInitializer, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean3, String paramString5, TypeCode paramTypeCode)
  {
    name = paramString1;
    id = paramString2;
    is_abstract = paramBoolean1;
    is_custom = paramBoolean2;
    defined_in = paramString3;
    version = paramString4;
    operations = paramArrayOfOperationDescription;
    attributes = paramArrayOfAttributeDescription;
    members = paramArrayOfValueMember;
    initializers = paramArrayOfInitializer;
    supported_interfaces = paramArrayOfString1;
    abstract_base_values = paramArrayOfString2;
    is_truncatable = paramBoolean3;
    base_value = paramString5;
    type = paramTypeCode;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\ValueDefPackage\FullValueDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */