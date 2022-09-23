package jftmain;

import java.lang.foreign.Addressable;
import java.lang.foreign.MemoryAddress;
import java.lang.foreign.MemorySession;

import static java.lang.foreign.ValueLayout.*;
import static com.bsmart.javafreetype.FreeType.*;


public class Main {

    public static void main(String[] args) {
        long library = FT_Init_FreeType();
        long face = FT_New_Face(library, MemorySession.openConfined().allocateUtf8String("C:\\Windows\\Fonts\\Arial.ttf"), 0);
        System.out.println(FT_Library_Version_Major(library));
        System.out.println(FT_Library_Version_Minor(library));
        System.out.println(FT_Library_Version_Patch(library));
        MemorySession mem = MemorySession.openConfined();
        Addressable major = mem.allocate(JAVA_INT);
        Addressable minor = mem.allocate(JAVA_INT);
        Addressable patch = mem.allocate(JAVA_INT);

        FT_Library_Version(library, major, minor, patch);
        System.out.println(major.address().get(JAVA_INT, 0));
        System.out.println(minor.address().get(JAVA_INT, 0));
        System.out.println(patch.address().get(JAVA_INT, 0));

        mem.close();
        FT_Set_Pixel_Sizes(face, 0, 48);
        FT_Load_Char(face, 'a', (int) FT_LOAD_RENDER);
        FT_Done_Face(face);
        FT_Done_FreeType(library);
    }

}
