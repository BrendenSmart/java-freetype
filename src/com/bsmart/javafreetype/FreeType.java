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

    public static final long FT_FACE_FLAG_SCALABLE = (1L << 0);

    public static final long FT_FACE_FLAG_FIXED_SIZES = (1L << 1);

    public static final long FT_FACE_FLAG_FIXED_WIDTH = (1L << 2);

    public static final long FT_FACE_FLAG_SFNT = (1L << 3);

    public static final long FT_FACE_FLAG_HORIZONTAL = (1L << 4);

    public static final long FT_FACE_FLAG_VERTICAL = (1L << 5);

    public static final long FT_FACE_FLAG_KERNING = (1L << 6);

    public static final long FT_FACE_FLAG_FAST_GLYPHS = (1L << 7);

    public static final long FT_FACE_FLAG_MULTIPLE_MASTERS = (1L << 8);

    public static final long FT_FACE_FLAG_GLYPH_NAMES = (1L << 9);

    public static final long FT_FACE_FLAG_EXTERNAL_STREAM = (1L << 10);

    public static final long FT_FACE_FLAG_HINTER = (1L << 11);

    public static final long FT_FACE_FLAG_CID_KEYED = (1L << 12);

    public static final long FT_FACE_FLAG_TRICKY = (1L << 13);

    public static final long FT_FACE_FLAG_COLOR = (1L << 14);

    public static final long FT_LOAD_DEFAULT = 0x0;

    public static final long FT_LOAD_NO_SCALE = (1L << 0);

    public static final long FT_LOAD_NO_HINTING = (1L << 1);

    public static final long FT_LOAD_RENDER = (1L << 2);

    public static final long FT_LOAD_NO_BITMAP = (1L << 3);

    public static final long FT_LOAD_VERTICAL_LAYOUT = (1L << 4);

    public static final long FT_LOAD_FORCE_AUTOHINT = (1L << 5);

    public static final long FT_LOAD_CROP_BITMAP = (1L << 6);

    public static final long FT_LOAD_PEDANTIC = (1L << 7);

    public static final long FT_LOAD_IGNORE_GLOBAL_ADVANCE_WIDTH = (1L << 9);

    public static final long FT_LOAD_NO_RECURSE = (1L << 10);

    public static final long FT_LOAD_IGNORE_TRANSFORM = (1L << 11);

    public static final long FT_LOAD_MONOCHROME = (1L << 12);

    public static final long FT_LOAD_LINEAR_DESIGN = (1L << 13);

    public static final long FT_LOAD_NO_AUTOHINT = (1L << 15);

    public static final long FT_LOAD_COLOR = (1L << 20);

    public static final long FT_LOAD_COMPUTE_METRICS = (1L << 21);

    private static Linker linker;

    private static SymbolLookup freetype;

    private static final MemorySession MEMORY = MemorySession.openShared();

    private static MethodHandle CreateLibrary, DestroyLibrary, CreateFace, GetNumFaceGlyphs, GetFaceAscender, SetCharSize, SetPixelSizes, GetCharIndex,
            LoadGlyph, RenderGlyph, GetGlyphHorizontalAdvance, GetGlyphVerticalAdvance, GetNumGlyphBitmapRows, GetGlyphBitmapRows, GetGlyphBitmapWidth, GetGlyphBitmap,
    FT_Init_FreeType, FT_Done_FreeType, FT_New_Face, FT_Done_Face, FT_Library_Version_Major, FT_Library_Version_Minor, FT_Library_Version_Patch,
            FT_Library_Version, FT_Set_Pixel_Sizes, FT_Load_Char;


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
        FT_Init_FreeType = linker.downcallHandle(freetype.lookup("nFT_Init_FreeType").orElseThrow(), FunctionDescriptor.of(JAVA_LONG));
        FT_Done_FreeType = linker.downcallHandle(freetype.lookup("nFT_Done_FreeType").orElseThrow(), FunctionDescriptor.ofVoid());
        FT_New_Face = linker.downcallHandle(freetype.lookup("nFT_New_Face").orElseThrow(), FunctionDescriptor.of(JAVA_LONG, JAVA_LONG, ADDRESS, JAVA_INT));
        FT_Done_Face = linker.downcallHandle(freetype.lookup("nFT_Done_Face").orElseThrow(), FunctionDescriptor.ofVoid(JAVA_LONG));
        FT_Library_Version_Major = linker.downcallHandle(freetype.lookup("nFT_Library_Version_Major").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG));
        FT_Library_Version_Minor = linker.downcallHandle(freetype.lookup("nFT_Library_Version_Minor").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG));
        FT_Library_Version_Patch = linker.downcallHandle(freetype.lookup("nFT_Library_Version_Patch").orElseThrow(), FunctionDescriptor.of(JAVA_INT, JAVA_LONG));
        FT_Library_Version = linker.downcallHandle(freetype.lookup("nFT_Library_Version").orElseThrow(), FunctionDescriptor.ofVoid(JAVA_LONG, ADDRESS, ADDRESS, ADDRESS));
        FT_Set_Pixel_Sizes = linker.downcallHandle(freetype.lookup("nFT_Set_Pixel_Sizes").orElseThrow(), FunctionDescriptor.ofVoid(JAVA_LONG, JAVA_INT, JAVA_INT));
        FT_Load_Char = linker.downcallHandle(freetype.lookup("nFT_Load_Char").orElseThrow(), FunctionDescriptor.ofVoid(JAVA_LONG, JAVA_LONG, JAVA_INT));
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
        try {
            return (long) FT_Init_FreeType.invokeExact();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void FT_Done_FreeType(long library) {
        try {
            FT_Done_FreeType.invokeExact(library);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static long FT_New_Face(long library, MemoryAddress filepath, int index) {
        try {
            return (long) FT_New_Face.invokeExact(library, filepath, index);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void FT_Done_Face(long face) {
        try {
            FT_Done_Face.invokeExact(face);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int FT_Library_Version_Major(long library) {
        try {
            return (int) FT_Library_Version_Major.invokeExact(library);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int FT_Library_Version_Minor(long library) {
        try {
            return (int) FT_Library_Version_Minor.invokeExact(library);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static int FT_Library_Version_Patch(long library) {
        try {
            return (int) FT_Library_Version_Patch.invokeExact(library);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void FT_Library_Version(long library, Addressable major, Addressable minor, Addressable patch) {
        try {
            FT_Library_Version.invokeExact(library, major, minor, patch);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void FT_Set_Pixel_Sizes(long face, int width, int height) {
        try {
            FT_Set_Pixel_Sizes.invokeExact(face, width, height);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void FT_Load_Char(long face, long codepoint, int flags) {
        try {
            FT_Load_Char.invokeExact(face, codepoint, flags);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
