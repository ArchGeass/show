package org.geass.jvm;

import org.apache.commons.io.IOUtils;
import org.geass.classpy.classfile.ClassFileParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MiniJVMClassLoader {

    /**
     * Bootstrap类加载器
     */
    public static final MiniJVMClassLoader BOOTSTRAP_CLASSLOADER =
            new MiniJVMClassLoader(new String[]{
                    System.getProperty("java.home") + "/lib/rt.jar"
            }, null);
    /**
     * Ext类加载器
     */
    public static final MiniJVMClassLoader EXT_CLASSLOADER =
            new MiniJVMClassLoader(
                    Stream.of(new File(System.getProperty("java.home") + "/lib/ext")
                            .listFiles())
                            .filter(File::isFile)
                            .filter(file -> file.getName().endsWith(".jar"))
                            .map(File::getName)
                            .toArray(String[]::new),
                    BOOTSTRAP_CLASSLOADER);

    /**
     * Bootstrap类加载器,   rt.jar
     * Ext类加载器,         ext/
     * 应用类加载器,         -classpath
     */
    private Map<String, MiniJVMClass> loadedClasses = new ConcurrentHashMap<>();

    /**
     * 不一定是-classpath
     * 每个entry是一个jar或者package
     * 对于Bootstrap类加载器,是rt.jar
     * 对于Ext类加载器,是ext/目录下的所有jar
     * 对于AppClassLoader,是-classpath指定的jar或package
     */
    private String[] classPath;

    /**
     * null代表启动类加载器
     */
    private MiniJVMClassLoader parent;

    public MiniJVMClassLoader(String[] classPath, MiniJVMClassLoader parent) {
        this.classPath = classPath;
        this.parent = parent;
    }

    public MiniJVMClass loadClass(String className) throws ClassNotFoundException {
        if (loadedClasses.containsKey(className)) {
            return loadedClasses.get(className);
        }

        MiniJVMClass result = null;
        try {
            if (parent == null) {
                //启动类加载器
                result = findAndDefineClass(className);
            } else {
                result = parent.loadClass(className);
            }
        } catch (ClassNotFoundException ignored) {
            if (parent == null) {
                throw ignored;
            }
        }
        if (result == null && parent != null) {
            //父加载器没找到,就自己加载
            result = findAndDefineClass(className);
        }
        //没找到会抛出异常,故此时一定找到了,故缓存起来
        loadedClasses.put(className, result);
        return result;
    }

    protected MiniJVMClass findAndDefineClass(String className) throws ClassNotFoundException {
        byte[] bytes = findClassBytes(className);
        return defineClass(className, bytes);
    }

    private MiniJVMClass defineClass(String name, byte[] bytes) {
        return new MiniJVMClass(name, this, new ClassFileParser().parse(bytes));
    }

    /**
     * classLoader加载字节码
     */
    private byte[] findClassBytes(String className) throws ClassNotFoundException {
        String path = className.replace('.', '/') + ".class";
        for (String entry : classPath) {
            if (new File(entry).isDirectory()) {
                try {
                    return Files.readAllBytes(new File(entry, path).toPath());
                } catch (IOException ignored) {
                    //找不到是正常的,故忽略即可
                }
            } else if (entry.endsWith(".jar")) {
                try {
                    return readBytesFromJar(entry, path);
                } catch (IOException ignored) {
                }
            }
        }
        throw new ClassNotFoundException(className);
    }

    /**
     * 从jar中loadClass
     */
    private byte[] readBytesFromJar(String jar, String path) throws IOException {
        ZipFile zipFile = new ZipFile(jar);
        ZipEntry entry = zipFile.getEntry(path);
        if (entry == null) {
            throw new IOException("Not found: " + path);
        }
        InputStream is = zipFile.getInputStream(entry);
        return IOUtils.toByteArray(is);
    }
}
