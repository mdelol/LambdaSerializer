package org.bwsw.lambdaserializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * Created by krickiy_sp on 29.03.16.
 */
public class ClassLoaderObjectInputStream extends ObjectInputStream {

    private ClassLoader classLoader;

    public ClassLoaderObjectInputStream(ClassLoader classLoader, InputStream in) throws IOException {
        super(in);
        this.classLoader = classLoader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException{

        try{
            String name = desc.getName();
            return Class.forName(name, false, classLoader);
        }
        catch(ClassNotFoundException e){
            return super.resolveClass(desc);
        }
    }
}
