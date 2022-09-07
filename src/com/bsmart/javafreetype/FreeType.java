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

import java.io.*;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.net.URL;
import java.nio.file.Files;

import static java.lang.foreign.ValueLayout.*;

/**
 * @author Brenden Smart
 */
public final class FreeType {

    private static Linker linker;

    private static SymbolLookup freetype;

    private static final MemorySession MEMORY = MemorySession.openShared();

    private static MethodHandle CreateLibrary, DestroyLibrary, CreateFace, GetNumFaceGlyphs, GetFaceAscender, SetCharSize, SetPixelSizes, GetCharIndex,
            LoadGlyph, RenderGlyph, GetGlyphHorizontalAdvance, GetGlyphVerticalAdvance, GetNumGlyphBitmapRows, GetGlyphBitmapRows, GetGlyphBitmapWidth, GetGlyphBitmap;


    private static boolean isRelease;

    private FreeType() {}

    static {
        loadLibrary();
        initIDs();
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static void loadLibrary() {

        URL url = FreeType.class.getResource("/javafreetype/natives/windows/javafreetype.dll");
        try {
            File tempDir = Files.createTempDirectory("javafreetype").toFile();
            tempDir.deleteOnExit();
            File nativeLibTmpFile = new File(tempDir, "javafreetype.dll");
            nativeLibTmpFile.deleteOnExit();
            try (InputStream in = url.openStream()) {
                Files.copy(in, nativeLibTmpFile.toPath());
            }
            freetype = SymbolLookup.libraryLookup(nativeLibTmpFile.toPath(), MemorySession.openShared());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        linker = Linker.nativeLinker();
    }

    private static void initIDs() {
        CreateLibrary = linker.downcallHandle(freetype.lookup("CreateLibrary").orElseThrow(), FunctionDescriptor.of(JAVA_LONG));
        DestroyLibrary = linker.downcallHandle(freetype.lookup("DestroyLibrary").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG));
        CreateFace = linker.downcallHandle(freetype.lookup("CreateFace").orElseThrow(), FunctionDescriptor.of(JAVA_LONG, JAVA_LONG, ADDRESS, JAVA_INT));
        GetNumFaceGlyphs = linker.downcallHandle(freetype.lookup("GetNumFaceGlyphs").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG));
        GetFaceAscender = linker.downcallHandle(freetype.lookup("GetFaceAscender").orElseThrow(), FunctionDescriptor.of(JAVA_SHORT, JAVA_LONG));
        SetCharSize = linker.downcallHandle(freetype.lookup("SetCharSize").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG, JAVA_INT, JAVA_INT, JAVA_INT, JAVA_INT));
        SetPixelSizes = linker.downcallHandle(freetype.lookup("SetPixelSizes").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG, JAVA_INT, JAVA_INT));
        GetCharIndex = linker.downcallHandle(freetype.lookup("GetCharIndex").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG, JAVA_CHAR));
        LoadGlyph = linker.downcallHandle(freetype.lookup("LoadGlyph").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG, JAVA_INT, JAVA_INT));
        RenderGlyph = linker.downcallHandle(freetype.lookup("RenderGlyph").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG, JAVA_INT));
        GetGlyphHorizontalAdvance = linker.downcallHandle(freetype.lookup("GetGlyphHorizontalAdvance").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG));
        GetGlyphVerticalAdvance = linker.downcallHandle(freetype.lookup("GetGlyphVerticalAdvance").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG));
        GetNumGlyphBitmapRows = linker.downcallHandle(freetype.lookup("GetNumFaceGlyphs").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG));
        GetGlyphBitmapWidth = linker.downcallHandle(freetype.lookup("GetGlyphBitmapWidth").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG));
        GetGlyphBitmap = linker.downcallHandle(freetype.lookup("GetGlyphBitmap").orElseThrow(), FunctionDescriptor.of(JAVA_LONG, JAVA_LONG));
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

    public static int SetCharSize(long face, int width, int height, int horizontalRes, int verticalRes) {
        try {
            return (int) SetCharSize.invokeExact(face, width, height, horizontalRes, verticalRes);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int SetPixelSizes(long face, int width, int height) {
        try {
            return (int) SetPixelSizes.invokeExact(face, width, height);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int GetCharIndex(long face, char codepoint) {
        try {
            return (int) GetCharIndex.invokeExact(face, codepoint);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int LoadGlyph(long face, int glyphIndex, int flags) {
        try {
            return (int) LoadGlyph.invokeExact(face, glyphIndex, flags);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int RenderGlyph(long face, int flags) {
        try {
            return (int) RenderGlyph.invokeExact(face, flags);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int GetGlyphHorizontalAdvance(long face) {
        try {
            return (int) GetGlyphHorizontalAdvance.invokeExact(face);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int GetGlyphVerticalAdvance(long face) {
        try {
            return (int) GetGlyphVerticalAdvance.invokeExact(face);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int GetNumGlyphBitmapRows(long face) {
        try {
            return (int) GetNumGlyphBitmapRows.invokeExact(face);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int GetGlyphBitmapWidth(long face) {
        try {
            return (int) GetGlyphBitmapWidth.invokeExact(face);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static long GetGlyphBitmap(long face) {
        try {
            return (long) GetGlyphBitmap.invokeExact(face);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static long FT_Init_FreeType() {
        return 0;
    }

    public static void FT_Done_FreeType(long library) {

    }


}
