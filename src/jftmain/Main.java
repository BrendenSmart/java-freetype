package jftmain;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SegmentScope;

import static java.lang.foreign.ValueLayout.*;
import static com.bsmart.javafreetype.FreeType.*;

// Replace memorysegments with longs because memorysegments suck
public class Main {

    public static void main(String[] args) {
        System.out.println("Hola bueno dia");
        long library = FT_Init_FreeType();
        SegmentScope scope = SegmentScope.global();
        SegmentAllocator.nativeAllocator(scope).allocateUtf8String("C:\\Windows\\Fonts\\Arial.ttf");
        System.out.println("Hola bueno noche");
        long face = FT_New_Face(library, SegmentAllocator.nativeAllocator(scope).allocateUtf8String("C:\\Windows\\Fonts\\Arial.ttf"), 0);

        System.out.println(FT_Library_Version_Major(library));
        System.out.println(FT_Library_Version_Minor(library));
        System.out.println(FT_Library_Version_Patch(library));
        SegmentAllocator mem = SegmentAllocator.nativeAllocator(SegmentScope.auto());
        MemorySegment major = mem.allocate(JAVA_INT);
        MemorySegment minor = mem.allocate(JAVA_INT);
        MemorySegment patch = mem.allocate(JAVA_INT);

        FT_Library_Version(library, major, minor, patch);
        System.out.println(major.get(JAVA_INT, 0));
        System.out.println(minor.get(JAVA_INT, 0));
        System.out.println(patch.get(JAVA_INT, 0));


        FT_Set_Pixel_Sizes(face, 0, 48);
        FT_Load_Char(face, 'a', (int) FT_LOAD_RENDER);
        FT_Done_Face(face);
        FT_Done_FreeType(library);
    }

}
