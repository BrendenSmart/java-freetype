/*
 * MIT License
 *
 * Copyright (c) 2022 Brenden Smart
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.bsmart.javafreetype;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.foreign.ValueLayout.*;

/**
 * @author Brenden Smart
 */
public final class FreeType {

    private static Linker linker;

    private static SymbolLookup freetype;

    private static final MemorySession MEMORY = MemorySession.openShared();

    private static MethodHandle CreateLibrary, DestroyLibrary, CreateFace, GetNumFaceGlyphs, GetFaceAscender;

    private FreeType() {}

    static {
        loadLibrary();
        initIDs();
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static void loadLibrary() {
        final String libraryResource = FreeType.class.getResource("/natives/windows/javafreetype.dll").getPath().replace("%20", " ");


        final String userHome = System.getProperty("user.home");

        Path folder = Path.of(userHome + "/javafreetype");

        Path tempLibrary = Path.of(userHome + "/javafreetype/javafreetype.dll");

        try {
            Files.deleteIfExists(tempLibrary);
            Files.deleteIfExists(folder);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        freetype = SymbolLookup.libraryLookup(tempLibrary, MemorySession.openShared());
        linker = Linker.nativeLinker();
    }

    private static void initIDs() {
        CreateLibrary = linker.downcallHandle(freetype.lookup("CreateLibrary").orElseThrow(), FunctionDescriptor.of(JAVA_LONG));
        DestroyLibrary = linker.downcallHandle(freetype.lookup("DestroyLibrary").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG));
        CreateFace = linker.downcallHandle(freetype.lookup("CreateFace").orElseThrow(), FunctionDescriptor.of(JAVA_LONG, JAVA_LONG, ADDRESS, JAVA_INT));
        GetNumFaceGlyphs = linker.downcallHandle(freetype.lookup("GetNumFaceGlyphs").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG));
        GetFaceAscender = linker.downcallHandle(freetype.lookup("GetFaceAscender").orElseThrow(), FunctionDescriptor.of(JAVA_SHORT, JAVA_LONG));
    }


    public static long CreateLibrary() {
        try {
            return (long) CreateLibrary.invokeExact();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int DestroyLibrary(long library) {
        try {
            return (int) DestroyLibrary.invokeExact(library);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static long CreateFace(long library, String filepath, int index) {
        MemorySegment path = MEMORY.allocateUtf8String(filepath);
        try {
            return (long) CreateFace.invokeExact(library, (Addressable) path, index);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int GetNumFaceGlyphs(long face) {
        try {
            return (int) GetNumFaceGlyphs.invokeExact(face);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static short GetFaceAscender(long face) {
        try {
            return (short) GetFaceAscender.invokeExact(face);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
