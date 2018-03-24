package tests;

import org.junit.Test;
import utils.FileOperations;
import utils.FileOperations.FileOperationsResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static utils.FileOperations.FileOperationsResponse.*;

/**
 * Created by tash-had on 2017-11-30.
 */
public class TestFileOperations {

    @Test
    public void testRenameFileChangesOriginal(){
        File file = new File("src/tests/testfile.txt");
        makeFiles(file);
        FileOperations.renameFile(file, "newname.txt");
        assert !file.exists();
    }

    @Test
    public void testRenameFileChangesName(){
        File file = new File("src/tests/testfile.txt");
        makeFiles(file);
        FileOperationsResponse response = FileOperations.renameFile(file, "newname.txt");
        File newFile = new File("src/tests/newname.txt");
        assert newFile.exists() && response == SUCCESS;
    }

    @Test
    public void testMoveFileMovesOriginal(){
        File file = new File("src/tests/testfile.txt");
        File newParent = new File("src/tests/subdir");
        makeFiles(file);
        FileOperationsResponse response = FileOperations.moveFile(file, newParent.toPath());
        File newFile = new File(newParent, "testfile.txt");
        assert newFile.exists() && response == SUCCESS;
    }

    @Test
    public void testMoveFileWithTakenNameBothFilesRemain(){
        File folder = new File("src/tests/subdir");
        File file1 = new File(folder, "newname.txt");
        File file2 = new File("src/tests/newname.txt");
        makeFiles(file1, file2);
        FileOperationsResponse response = FileOperations.moveFile(file2, folder.toPath());
        assert file1.exists() && file2.exists() && response == FILENAME_TAKEN;
    }

    @Test
    public void testGetFileSuffix(){
        File folder = new File("src/tests/subdir");
        String suffixedName = FileOperations.getSuffixedFileName(folder, "newname.txt");
        assert suffixedName.equals("newname (1).txt ");
    }


    @Test
    public void testGetFileSuffixWithExistingSuffixedFiles(){
        File folder = new File("src/tests/subdir/");
        File file1 = new File("src/tests/subdir/newfile.txt");
        File file2 = new File("src/tests/subdir/newfile (1).txt");
        File file3 = new File("src/tests/subdir/newfile (2).txt");
        makeFiles(file1, file2, file3);
        String suffixedName = FileOperations.getSuffixedFileName(folder, "newfile.txt");
        assert suffixedName.equals("newfile (3).txt");
    }


    private void makeFiles(File ... files){
        for (File file : files){
            if (file.exists()){
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
