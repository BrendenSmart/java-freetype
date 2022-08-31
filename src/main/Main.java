package main;

import com.bsmart.javafreetype.FreeType;

import static com.bsmart.javafreetype.FreeType.*;


public class Main {

    public static void main(String[] args) {
        long library = CreateLibrary();
        long face = CreateFace(library, "C:/Windows/Fonts/arial.ttf", 0);
        System.out.println(GetNumFaceGlyphs(face));
        int index = GetCharIndex(face, 'a');
        SetCharSize(face, 16*64, 16*64, 300, 300);
        SetPixelSizes(face, 16, 16);
        System.out.println("Before");
        LoadGlyph(face, index, 0);
        GetGlyphBitmap(face);
        System.out.println("After");
        //RenderGlyph(face, 0);
        
    }

}
