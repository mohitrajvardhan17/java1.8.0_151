package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.impl.ior.iiop.AlternateIIOPAddressComponentImpl;
import com.sun.corba.se.impl.ior.iiop.CodeSetsComponentImpl;
import com.sun.corba.se.impl.ior.iiop.IIOPAddressImpl;
import com.sun.corba.se.impl.ior.iiop.IIOPProfileImpl;
import com.sun.corba.se.impl.ior.iiop.IIOPProfileTemplateImpl;
import com.sun.corba.se.impl.ior.iiop.JavaCodebaseComponentImpl;
import com.sun.corba.se.impl.ior.iiop.JavaSerializationComponent;
import com.sun.corba.se.impl.ior.iiop.MaxStreamFormatVersionComponentImpl;
import com.sun.corba.se.impl.ior.iiop.ORBTypeComponentImpl;
import com.sun.corba.se.impl.ior.iiop.RequestPartitioningComponentImpl;
import com.sun.corba.se.spi.ior.EncapsulationFactoryBase;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.ior.IdentifiableFactory;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.IOP.TaggedProfile;

public abstract class IIOPFactories
{
  private IIOPFactories() {}
  
  public static IdentifiableFactory makeRequestPartitioningComponentFactory()
  {
    new EncapsulationFactoryBase(1398099457)
    {
      public Identifiable readContents(InputStream paramAnonymousInputStream)
      {
        int i = paramAnonymousInputStream.read_ulong();
        RequestPartitioningComponentImpl localRequestPartitioningComponentImpl = new RequestPartitioningComponentImpl(i);
        return localRequestPartitioningComponentImpl;
      }
    };
  }
  
  public static RequestPartitioningComponent makeRequestPartitioningComponent(int paramInt)
  {
    return new RequestPartitioningComponentImpl(paramInt);
  }
  
  public static IdentifiableFactory makeAlternateIIOPAddressComponentFactory()
  {
    new EncapsulationFactoryBase(3)
    {
      public Identifiable readContents(InputStream paramAnonymousInputStream)
      {
        IIOPAddressImpl localIIOPAddressImpl = new IIOPAddressImpl(paramAnonymousInputStream);
        AlternateIIOPAddressComponentImpl localAlternateIIOPAddressComponentImpl = new AlternateIIOPAddressComponentImpl(localIIOPAddressImpl);
        return localAlternateIIOPAddressComponentImpl;
      }
    };
  }
  
  public static AlternateIIOPAddressComponent makeAlternateIIOPAddressComponent(IIOPAddress paramIIOPAddress)
  {
    return new AlternateIIOPAddressComponentImpl(paramIIOPAddress);
  }
  
  public static IdentifiableFactory makeCodeSetsComponentFactory()
  {
    new EncapsulationFactoryBase(1)
    {
      public Identifiable readContents(InputStream paramAnonymousInputStream)
      {
        return new CodeSetsComponentImpl(paramAnonymousInputStream);
      }
    };
  }
  
  public static CodeSetsComponent makeCodeSetsComponent(ORB paramORB)
  {
    return new CodeSetsComponentImpl(paramORB);
  }
  
  public static IdentifiableFactory makeJavaCodebaseComponentFactory()
  {
    new EncapsulationFactoryBase(25)
    {
      public Identifiable readContents(InputStream paramAnonymousInputStream)
      {
        String str = paramAnonymousInputStream.read_string();
        JavaCodebaseComponentImpl localJavaCodebaseComponentImpl = new JavaCodebaseComponentImpl(str);
        return localJavaCodebaseComponentImpl;
      }
    };
  }
  
  public static JavaCodebaseComponent makeJavaCodebaseComponent(String paramString)
  {
    return new JavaCodebaseComponentImpl(paramString);
  }
  
  public static IdentifiableFactory makeORBTypeComponentFactory()
  {
    new EncapsulationFactoryBase(0)
    {
      public Identifiable readContents(InputStream paramAnonymousInputStream)
      {
        int i = paramAnonymousInputStream.read_ulong();
        ORBTypeComponentImpl localORBTypeComponentImpl = new ORBTypeComponentImpl(i);
        return localORBTypeComponentImpl;
      }
    };
  }
  
  public static ORBTypeComponent makeORBTypeComponent(int paramInt)
  {
    return new ORBTypeComponentImpl(paramInt);
  }
  
  public static IdentifiableFactory makeMaxStreamFormatVersionComponentFactory()
  {
    new EncapsulationFactoryBase(38)
    {
      public Identifiable readContents(InputStream paramAnonymousInputStream)
      {
        byte b = paramAnonymousInputStream.read_octet();
        MaxStreamFormatVersionComponentImpl localMaxStreamFormatVersionComponentImpl = new MaxStreamFormatVersionComponentImpl(b);
        return localMaxStreamFormatVersionComponentImpl;
      }
    };
  }
  
  public static MaxStreamFormatVersionComponent makeMaxStreamFormatVersionComponent()
  {
    return new MaxStreamFormatVersionComponentImpl();
  }
  
  public static IdentifiableFactory makeJavaSerializationComponentFactory()
  {
    new EncapsulationFactoryBase(1398099458)
    {
      public Identifiable readContents(InputStream paramAnonymousInputStream)
      {
        byte b = paramAnonymousInputStream.read_octet();
        JavaSerializationComponent localJavaSerializationComponent = new JavaSerializationComponent(b);
        return localJavaSerializationComponent;
      }
    };
  }
  
  public static JavaSerializationComponent makeJavaSerializationComponent()
  {
    return JavaSerializationComponent.singleton();
  }
  
  public static IdentifiableFactory makeIIOPProfileFactory()
  {
    new EncapsulationFactoryBase(0)
    {
      public Identifiable readContents(InputStream paramAnonymousInputStream)
      {
        IIOPProfileImpl localIIOPProfileImpl = new IIOPProfileImpl(paramAnonymousInputStream);
        return localIIOPProfileImpl;
      }
    };
  }
  
  public static IIOPProfile makeIIOPProfile(ORB paramORB, ObjectKeyTemplate paramObjectKeyTemplate, ObjectId paramObjectId, IIOPProfileTemplate paramIIOPProfileTemplate)
  {
    return new IIOPProfileImpl(paramORB, paramObjectKeyTemplate, paramObjectId, paramIIOPProfileTemplate);
  }
  
  public static IIOPProfile makeIIOPProfile(ORB paramORB, TaggedProfile paramTaggedProfile)
  {
    return new IIOPProfileImpl(paramORB, paramTaggedProfile);
  }
  
  public static IdentifiableFactory makeIIOPProfileTemplateFactory()
  {
    new EncapsulationFactoryBase(0)
    {
      public Identifiable readContents(InputStream paramAnonymousInputStream)
      {
        IIOPProfileTemplateImpl localIIOPProfileTemplateImpl = new IIOPProfileTemplateImpl(paramAnonymousInputStream);
        return localIIOPProfileTemplateImpl;
      }
    };
  }
  
  public static IIOPProfileTemplate makeIIOPProfileTemplate(ORB paramORB, GIOPVersion paramGIOPVersion, IIOPAddress paramIIOPAddress)
  {
    return new IIOPProfileTemplateImpl(paramORB, paramGIOPVersion, paramIIOPAddress);
  }
  
  public static IIOPAddress makeIIOPAddress(ORB paramORB, String paramString, int paramInt)
  {
    return new IIOPAddressImpl(paramORB, paramString, paramInt);
  }
  
  public static IIOPAddress makeIIOPAddress(InputStream paramInputStream)
  {
    return new IIOPAddressImpl(paramInputStream);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\iiop\IIOPFactories.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */