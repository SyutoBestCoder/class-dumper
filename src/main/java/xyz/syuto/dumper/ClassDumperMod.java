/*
    @Author Syuto
    partially based off https://github.com/accessmodifier364/Dumper
*/

package xyz.syuto.dumper;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.io.*;
import java.nio.file.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Mod(modid = "classdumper", name = "Class Dumper", version = "1.0")
public class ClassDumperMod {

    private static Minecraft mc = Minecraft.getMinecraft();
    private static final File DUMP_DIR = new File(mc.mcDataDir, "dump");
    private static final List<String> EXCLUDED_PACKAGES = Arrays.asList(
            "java", "sun", "javax", "jdk", "com/sun",
            "org/spongepowered", "org/jcp"
    );

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new DumpCommand());
        ClientCommandHandler.instance.registerCommand(new PrintLoaders());
    }

    public static void dumpClasses() {
        if (!DUMP_DIR.exists() && !DUMP_DIR.mkdirs()) {
            throw new RuntimeException("Failed to create dump directory.");
        }

        try {
            Set<ClassLoader> classLoaders = new HashSet<>();
            classLoaders.add(ClassLoader.getSystemClassLoader());
            classLoaders.add(Minecraft.class.getClassLoader());
            //add other loaders here if you want nigga

            for (ClassLoader classLoader : classLoaders) {
                dumpClassesFromClassLoader(classLoader);
            }

            System.out.println("Dumped classes!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void dumpClassesFromClassLoader(ClassLoader classLoader) {
        if (classLoader == null) return;
        try {
            Field classesField = ClassLoader.class.getDeclaredField("classes");
            classesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Vector<Class<?>> classes = (java.util.Vector<Class<?>>) classesField.get(classLoader);
            for (Class<?> clazz : classes) {
                if (shouldDump(clazz.getName())) {
                    dumpClass(clazz);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Failed to access classes from class loader: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean shouldDump(String className) {
        return EXCLUDED_PACKAGES.stream().noneMatch(className::startsWith);
    }

    public static void dumpClass(Class<?> clazz) {
        String className = clazz.getName().replace('.', '/') + ".class";
        Path classFilePath = Paths.get(DUMP_DIR.getAbsolutePath(), className);

        try {
            Files.createDirectories(classFilePath.getParent());
            try (InputStream inputStream = clazz.getClassLoader().getResourceAsStream(className)) {
                if (inputStream == null) {
                    System.out.println("Class file not found: " + className);
                    return;
                }
                byte[] classBytes = readInputStream(inputStream);
                Files.write(classFilePath, classBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println("Dumped class: " + className);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }
}
