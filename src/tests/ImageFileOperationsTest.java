//package tests;
//
//import managers.ImageFileOperationsManager;
//import model.ImageFile;
//import org.junit.Test;
//
//import java.io.File;
//
//import static org.junit.Assert.*;
//
//public class ImageFileOperationsManagerTest {
//    @Test
//    public void renameImageFile() throws Exception {
//        if (!new File("src/forunittest").exists()) {
//            File checkFile = new File("src/forunittest");
//            System.out.println("Make directory successfully: " + checkFile.mkdir());
//            File newFile = new File("src/forunittest/movedircheck");
//            System.out.println("Make new file successfully: " +newFile.mkdir());
//
//        }
//        else if(!new File("src/forunittest/movedircheck").exists()){
//            File newFile = new File("src/forunittest/movedircheck");
//            System.out.println("Make new file successfully: " +newFile.mkdir());
//        }
//
//        File newFile = new File("src/forunittest/testImage.jpg");
//        System.out.println("Make new file successfully: " +newFile.createNewFile());
//        ImageFile testImage = new ImageFile(newFile);
//        ImageFileOperationsManager.renameImageFile(testImage,"@T1 @T2 testImage.jpg");
//        File afterRename = new File("src/forunittest/@T1 @T2 testImage.jpg");
//        assertTrue(afterRename.exists());
//    }
//
//    @Test
//    public void moveImageFile() throws Exception {
//    }
//
//    @Test
//    public void fetchImageFiles() throws Exception {
//    }
//
//}