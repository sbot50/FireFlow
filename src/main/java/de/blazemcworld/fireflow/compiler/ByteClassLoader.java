package de.blazemcworld.fireflow.compiler;

public class ByteClassLoader extends ClassLoader {

    public ByteClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> define(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length);
    }

}
