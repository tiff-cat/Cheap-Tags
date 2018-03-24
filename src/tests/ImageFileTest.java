package tests;


import model.ImageFile;
import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * test all of important properties in imageFile object.
 */
public class ImageFileTest {
    @Test
    public void testgeneralReName2() throws Exception {
        File testFile = new File("src/tests/test.txt");
        ImageFile img = new ImageFile(testFile);
        img.generalReName("newName");
        assert(img.getCurrentName().equals("newName"));
    }

    @Test
    public void testupdateTagHistory() throws Exception {
    }

    @Test
    public void testequals() throws Exception {
        File testFile = new File("src/test/tests.txt");
        ImageFile img = new ImageFile(testFile);
        File testFile2 = new File ("src/test/tests2/txt");
        ImageFile img2 = new ImageFile(testFile2);
        assert(img != img2);
    }

    @Test
    public void compareTo() throws Exception {
    }
    private static final int timeOut = 3000;

    @Test(timeout = timeOut)
    public void testGeneralRename(){
        File testFile = new File("src/resources/images/logo_2.jpg");
        ImageFile testImage = new ImageFile(testFile);
        testImage.generalReName("@Tag1 @Tag2 logo_2");
        assertEquals("@Tag1 @Tag2 logo_2",testImage.getCurrentName());
    }

    @Test (timeout = timeOut)
    public void testImageEqual(){
        File testFile = new File("src/resources/images/logo_2.jpg");
        File testFIle2 = new File("src/resources/images/logo_2.jpg");
        ImageFile testImage = new ImageFile(testFile);
        ImageFile testImage2 = new ImageFile(testFIle2);
        assertEquals(testFile,testFIle2);
    }

    @Test (timeout = timeOut)
    public void testImageSequence(){
        File testFile = new File("src/resources/images/aogo_2.jpg");
        File testFIle2 = new File("src/resources/images/logo_2.jpg");
        ImageFile testImage = new ImageFile(testFile);
        ImageFile testImage2 = new ImageFile(testFIle2);
        assertTrue(testFile.compareTo(testFIle2) < 0);
    }


}
