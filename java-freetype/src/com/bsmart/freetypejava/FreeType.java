package com.bsmart.freetypejava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OptionalDataException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FreeType {


    private FreeType() {}

    static {
        loadLibrary();
        initIDs();
    }

    private static void loadLibrary() {
        final String libraryResource = FreeType.class.getResource("/natives/windows/freetype.dll").getPath().replace("%20", " ");


        final String userHome = System.getProperty("user.home");

        Path folder = Path.of(userHome + "/java-freetype");

        Path tempLibrary = Path.of(userHome + "/java-freetype/freetype.dll");

        try {
            Files.createDirectory(folder);
        } catch (FileAlreadyExistsException ignore) {}
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Files.copy(new FileInputStream(libraryResource), tempLibrary);
        } catch (IOException e) {
            e.printStackTrace();
        }

        folder.toFile().deleteOnExit();
        tempLibrary.toFile().deleteOnExit();
    }

    private static void initIDs() {

    }

    public static void test() {}


}
