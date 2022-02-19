package FilePhotographer_package;

import java.io.File;

public class MainClass {
    public static void main(String[] args) {

        /*
         * Creating images:
         * 1-(each image size in MB)-(password)-enter n for(general image) OR f for(facebook image) ex--> (1 50 242 n)
         * ----------------------------------------------
         * Getting the File:
         * 0-(Images count)-(password) ex--> (0 2 242)
         */

        //1-USE FOREWORD SLASH ONLY IN THE FILE...
        //2-USE THE SANE FILE EXTENSION AS THE ENCODED IMAGE IN THE NEW FILE VAR.
        File file = new File("C:/Users/AL-AWAL/Desktop/evil-labs-scene.zip");//file to convert to images
        File newFile = new File("C:/Users/AL-AWAL/Desktop/newFile.zip");//get a new copy of the converted file
        File ImageFolder = new File("C:/Users/AL-AWAL/Desktop");//destination of encoded images

        new FilePhotographer(file, ImageFolder, newFile);

    }
}
