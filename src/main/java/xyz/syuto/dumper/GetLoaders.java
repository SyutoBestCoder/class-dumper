package xyz.syuto.dumper;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class GetLoaders {

    public static void printClassLoaders() {
        printClassLoader("Bootstrap ClassLoader", getBootstrapClassLoader());
        printClassLoader("Platform ClassLoader", getPlatformClassLoader());
        printClassLoader("System ClassLoader", ClassLoader.getSystemClassLoader());

        try {
            printAllClassLoaders();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printClassLoader(String label, ClassLoader classLoader) {
        System.out.println(label + ": " + classLoader);
    }

    private static ClassLoader getBootstrapClassLoader() {
        return null;
    }

    private static ClassLoader getPlatformClassLoader() {
        try {
            Class<?> platformClassLoaderClass = Class.forName("java.lang.ClassLoader$PlatformClassLoader");
            return (ClassLoader) platformClassLoaderClass.getDeclaredMethod("getPlatformClassLoader").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void printAllClassLoaders() throws Exception {
        Set<ClassLoader> seen = new HashSet<>();
        ClassLoader loader = ClassLoader.getSystemClassLoader();

        while (loader != null) {
            if (seen.contains(loader)) {
                break;
            }
            seen.add(loader);
            printClassLoader("ClassLoader", loader);
            try {
                Field classesField = ClassLoader.class.getDeclaredField("classes");
                classesField.setAccessible(true);
                @SuppressWarnings("unchecked")
                java.util.Vector<Class<?>> classes = (java.util.Vector<Class<?>>) classesField.get(loader);
                for (Class<?> clazz : classes) {
                    printClassLoader("Class loaded", clazz.getClassLoader());
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            loader = loader.getParent();
        }
    }
}
