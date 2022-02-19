package FilePhotographer_package;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FilePhotographer {

    byte[] bytes;
    ArrayList<Byte> Bytes;
    ArrayList<Integer> RGB;
    int[] IntByte;
    InputStream is;
    OutputStream os;
    int fileSize, imageLimit, imageCount, imageLimitInMB;
    String ResolutionType;

    public FilePhotographer(File file, File ImageFolder, File newFile) {
        preparing(file, ImageFolder, newFile);
    }

    public void preparing(File file, File ImageFolder, File newFile) {
        System.out.println(Runtime.getRuntime().freeMemory() + "  " + Runtime.getRuntime().totalMemory() + "  " + Runtime.getRuntime().maxMemory());

        int n;
        Scanner scanner = new Scanner(System.in);
        n = scanner.nextInt();

        if (n == 1) {

            preparingToMakeImage(file, ImageFolder, scanner);

        } else if (n == 0) {

            preparingToMakeFile(newFile, ImageFolder, scanner);

        }
    }

    public void preparingToMakeImage(File file, File ImageFolder, Scanner scanner) {
        try {


            is = new FileInputStream(file);

            fileSize = is.available();
            imageLimitInMB = scanner.nextInt();
            imageLimit = (imageLimitInMB * 1048576) - 6;//the -6 is for 242imageCode, image number, total image count, password
            imageCount = 0;
            String tempPass = String.valueOf(scanner.nextInt());
            if (scanner.hasNext("f")) {
                ResolutionType = scanner.next();
            } else if (scanner.hasNext("n")){
                ResolutionType = scanner.next();
            }else{
                throw new IllegalArgumentException(scanner.next());
            }

            for (int i = 0; i < 255; i++) {
                if (is.available() == 0) {
                    break;
                }
                if (is.available() > imageLimit) {
                    is.read(new byte[imageLimit], 0, imageLimit);
                    imageCount++;
                } else {
                    is.read(new byte[is.available()], 0, is.available());
                    imageCount++;
                }
            }
            is.close();
            is = new FileInputStream(file);
            System.gc();

            System.out.println("fileSize:" + " " + (fileSize * 1048576) + " " + "imageLimitInMB:" + " " + imageLimitInMB + " " + "imageCount:" + " " + imageCount);

            String[] password = {"*", "*", "*", "*"};
            for (int j = 0; j < tempPass.length(); j++) {
                password[j] = String.valueOf(tempPass.charAt(j));
            }

            for (int i = 0; i < 255; i++) {

                System.gc();

                if (is.available() == 0) {
                    break;
                }

                if (is.available() > imageLimit) {
                    bytes = new byte[imageLimit];
                    is.read(bytes, 0, imageLimit);
                } else {
                    bytes = new byte[is.available()];
                    is.read(bytes, 0, is.available());
                }
                System.out.println("Start Image" + i);
                MakeImage(new File(ImageFolder + "/ImageFile" + i + ".png"), password, ResolutionType);


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void preparingToMakeFile(File newFile, File ImageFolder, Scanner scanner) {
        int imageCount = scanner.nextInt();
        int password = scanner.nextInt();
        ArrayList<File> files = new ArrayList<>(imageCount);

        for (int i = 0; i < 255; i++) {
            System.gc();
            if (new File(ImageFolder + "/ImageFile" + i + ".png").exists()) {
                files.add(new File(ImageFolder + "/ImageFile" + i + ".png"));
            } else {
                break;
            }
        }

        try {

            os = new FileOutputStream(newFile);

            ArrayList<File> files1 = ImageChecker(files, password, imageCount);
            for (File ImageFiles : files1) {
                System.out.println("Start " + ImageFiles);
                MakeFile(ImageFiles);
            }
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void MakeImage(File ImageFile, String[] password, String ResType) {

        System.gc();

        try {

            IntByte = new int[bytes.length + 1];

            int zeros = 0;
            for (int j = bytes.length - 1; j >= 0; j--) {
                if (bytes[j] == 0) {
                    zeros++;
                } else {
                    break;
                }
            }
            if (zeros == 0) {
                zeros = -1;
            }
            bytes = zeroRemover(bytes, zeros);

            int[] binaryBytes = toBinary(bytes); //int array with 8bit binary values

            //get image resolution.
            int[] height_width;
            if (ResType.equals("f")) {
                height_width = ImageResolutionForFacebook(binaryBytes);
            } else {
                height_width = ImageResolution(binaryBytes);
            }

            BufferedImage bi = new BufferedImage(height_width[0], height_width[1], BufferedImage.TYPE_INT_RGB);
            int count = 0;
            ArrayList<Integer> tempArray = new ArrayList<>();

            x:
            for (int x = 0; x < height_width[0]; x++) {
                for (int y = 0; y < height_width[1]; y++) {

                    if (x == 0 && y == 0) {
                        tempArray.add(242);

                        StringBuilder currentImageCount = new StringBuilder();
                        for (int i = 0; i < 3; i++) {
                            if (ImageFile.getName().charAt(9 + i) == '.') {
                                break;
                            }
                            currentImageCount.append(ImageFile.getName().charAt(9 + i));
                        }
                        tempArray.add(Integer.parseInt(currentImageCount.toString()));

                        tempArray.add(imageCount);
                    } else if (x == 0 && y == 1) {

                        if (password[0].equals("*")) {
                            tempArray.add(Integer.parseInt("0"));
                            tempArray.add(Integer.parseInt("0"));
                        } else if (password[1].equals("*")) {
                            tempArray.add(Integer.parseInt(password[0]));
                            tempArray.add(Integer.parseInt("0"));
                        } else if (password[2].equals("*")) {
                            tempArray.add(Integer.parseInt(password[0] + password[1]));
                            tempArray.add(Integer.parseInt("0"));
                        } else if (password[3].equals("*")) {
                            tempArray.add(Integer.parseInt(password[0] + password[1]));
                            tempArray.add(Integer.parseInt(String.valueOf(password[2])));
                        }

                        tempArray.add(242);

                    } else {
                        while (tempArray.size() < 3) {
                            if (count == binaryBytes.length) {
                                tempArray.add(0);
                            } else {
                                tempArray.add(binaryBytes[count]);
                                count++;
                            }
                        }
                    }

                    Color color = new Color(tempArray.get(0), tempArray.get(1), tempArray.get(2));
                    bi.setRGB(x, y, color.getRGB());

                    if (count == binaryBytes.length) {
                        break x;
                    }

                    tempArray = new ArrayList<>();

                }
            }

            ImageIO.write(bi, "png", ImageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.gc();

    }

    public void MakeFile(File ImageFile) {
        System.out.println(Runtime.getRuntime().freeMemory() + "  " + Runtime.getRuntime().totalMemory() + "  " + Runtime.getRuntime().maxMemory());

        try {

            BufferedImage bi = ImageIO.read(ImageFile);
            RGB = new ArrayList<>();
            String StringRGB;
            Color color;

            for (int x = 0; x < bi.getWidth(); x++) {
                for (int y = 0; y < bi.getHeight(); y++) {

                    StringRGB = String.valueOf(bi.getRGB(x, y));
                    color = Color.decode(StringRGB);

                    RGB.add(color.getRed());
                    RGB.add(color.getGreen());
                    RGB.add(color.getBlue());

                }
            }

            String[] fileInformation = {

                    RGB.get(0).toString(),
                    RGB.get(1).toString(),
                    RGB.get(2).toString(),
                    RGB.get(3).toString(),
                    RGB.get(4).toString(),
                    RGB.get(5).toString()

            };

            RGB.remove(0);
            RGB.remove(0);
            RGB.remove(0);

            RGB.remove(0);
            RGB.remove(0);
            RGB.remove(0);

            System.out.println(Arrays.toString(fileInformation));

            for (int i = 0; i < RGB.size(); i++) {
                StringBuilder temp;

                temp = new StringBuilder(Integer.toBinaryString(RGB.get(i)));

                if (temp.toString().equals("10000000")) {
                    RGB.set(i, -128);
                } else if (temp.length() == 8 && temp.charAt(0) == '1') {
                    temp.delete(0, 1);
                    RGB.set(i, (Integer.parseInt(temp.toString(), 2) * -1));
                } else {
                    RGB.set(i, Integer.parseInt(temp.toString(), 2));
                }
            }

            bi = null;
            System.gc();
            System.out.println(Runtime.getRuntime().freeMemory() + "  " + Runtime.getRuntime().totalMemory() + "  " + Runtime.getRuntime().maxMemory());

            for (int i = RGB.size() - 1; i >= 0; i--) {
                if (RGB.get(i) == 0) {
                    RGB.remove(i);
                } else if (RGB.get(i) != 0) {
                    break;
                }
            }

            int zeros = RGB.get(RGB.size() - 1);
            RGB.remove(RGB.size() - 1);
            for (int i = 0; i < zeros; i++) {
                RGB.add(0);
            }


            byte[] Bytes = new byte[RGB.size()];
            for (int i = 0; i < RGB.size(); i++) {
                Bytes[i] = (byte) (int) RGB.get(i);
            }
            os.write(Bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<File> ImageChecker(ArrayList<File> files, int password, int imageCount) {

        ArrayList<Integer[][]> ImageInformation = new ArrayList<>();

        //------------------------------------------------------------------------------- imageSaveCodeChecker

        for (int i = 0; i < files.size(); i++) {

            try {

                BufferedImage bi = ImageIO.read(files.get(i));
                boolean imageSaveCodeChecker = Color.decode(String.valueOf(bi.getRGB(0, 0))).getRed() == 242 && Color.decode(String.valueOf(bi.getRGB(0, 1))).getBlue() == 242;

                if (imageSaveCodeChecker) {
                    Integer[][] n = new Integer[2][3];
                    for (int j = 0; j < 2; j++) {

                        String StringRGB = String.valueOf(bi.getRGB(0, j));
                        Color color = Color.decode(StringRGB);

                        n[j] = new Integer[]{
                                color.getRed(),
                                color.getGreen(),
                                color.getBlue()
                        };

                        System.out.println(color.getRed() + " " + color.getGreen() + " " + color.getBlue() + "imageInformation" + j);

                    }

                    ImageInformation.add(n);

                } else {
                    files.remove(i);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //------------------------------------------------------------------------------- passChecker

        System.out.println(ImageInformation.toString() + "before passChecker");

        for (int j = 0; j < ImageInformation.size(); j++) {

            int pass;
            if (ImageInformation.get(j)[1][1] != 0) {
                pass = Integer.parseInt(ImageInformation.get(j)[1][0].toString() + ImageInformation.get(j)[1][1].toString());
            } else {
                pass = ImageInformation.get(j)[1][0];
            }

            boolean imagePasswordChecker = pass == password;
            if (!imagePasswordChecker) {
                ImageInformation.remove(j);
                files.remove(j);
            }
        }

        System.out.println(ImageInformation.toString() + "after passChecker");

        //------------------------------------------------------------------------------- TotalImageCount_Checker

        for (int j = 0; j < ImageInformation.size(); j++) {
            boolean TotalImageCount = ImageInformation.get(j)[0][2] == imageCount;
            if (!TotalImageCount) {
                ImageInformation.remove(j);
                files.remove(j);
            }
        }

        //------------------------------------------------------------------------------- ImageCount_Checker

        for (int j = 0; j < ImageInformation.size() - 1; j++) {

            boolean ImageCount = ImageInformation.get(j)[0][1] > ImageInformation.get(j + 1)[0][1];
            if (ImageCount) {
                Integer[][] temp;
                File fileTemp;

                temp = ImageInformation.get(j);
                ImageInformation.set(j, ImageInformation.get(j + 1));
                ImageInformation.set(j + 1, temp);

                fileTemp = files.get(j);
                files.set(j, files.get(j + 1));
                files.set(j + 1, fileTemp);

                j = -1;
            }

        }

        //------------------------------------------------------------------------------- printing ImageInformation

        for (Integer[][] integers : ImageInformation) {
            System.out.println(integers[0][1]);
            System.out.println(files.toString());
        }

        return files;
    }

    public int[] toBinary(byte[] bytes) {
        System.gc();
        int binaryBytes;
        for (int i = 0; i < bytes.length; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toBinaryString(Math.abs(bytes[i])));
            while (sb.length() != 8) {
                sb.insert(0, "0");
            }
            if (bytes[i] < 0) {
                sb.replace(0, 1, "1");
            }
            binaryBytes = Integer.parseInt(sb.toString());
            IntByte[i] = Integer.parseInt(String.valueOf(binaryBytes), 2);
        }
        return IntByte;
    }

    public int[] ImageResolution(int[] i) {
        double ImageResolution = Math.sqrt((double) (i.length + 6) / 3);//the +6 is for 242imageCode, image number, total image count, password
        int height = 0;
        int width = 0;
        if (ImageResolution == Math.floor(ImageResolution)) {
            height = (int) ImageResolution;
            width = (int) ImageResolution;
        } else if (ImageResolution < Math.floor(ImageResolution) + 0.5) {
            height = (int) Math.floor(ImageResolution);
            width = (int) Math.ceil(ImageResolution);
        } else if (ImageResolution >= Math.floor(ImageResolution) + 0.5) {
            height = (int) Math.ceil(ImageResolution);
            width = (int) Math.ceil(ImageResolution);
        }
        return new int[]{width, height};
    }

    public int[] ImageResolutionForFacebook(int[] i) {
        int height = 0;
        int width = 0;

        if ((i.length + 6) < (1048576)){
            height = 720;
        }else if ((i.length + 6) < (2*1048576)){
            height = 960;
        }else if ((i.length + 6) < (12*1048576)){
            height = 2048;
        }else if ((i.length + 6) < (48*1048576)){
            height = 4096;
        }else{
            //error
            throw new IndexOutOfBoundsException(": "+(i.length + 6)/1048576);
        }

        double ImageResolution = ((double) (i.length + 6) / 3) / height;//the +6 is for 242imageCode, image number, total image count, password

        if (ImageResolution != Math.ceil(ImageResolution)) {
            width = (int) ImageResolution + 1;
        } else {
            width = (int) ImageResolution;
        }

        return new int[]{width, height};
    }

    public byte[] zeroRemover(byte[] BYTES, int zeros) {

        System.out.println(Runtime.getRuntime().freeMemory() + "  " + Runtime.getRuntime().totalMemory() + "  " + Runtime.getRuntime().maxMemory());
        Bytes = new ArrayList<>();
        System.gc();
        System.out.println(Runtime.getRuntime().freeMemory() + "  " + Runtime.getRuntime().totalMemory() + "  " + Runtime.getRuntime().maxMemory());

        for (int i = 0; i < BYTES.length; i++) {
            if (i > BYTES.length / 2) {
                Bytes.add(BYTES[i]);
            }
        }

        int I = Bytes.size();
        int J = BYTES.length - Bytes.size();

        for (int i = Bytes.size() - 1; i >= 0; i--) {
            if (Bytes.get(i) == 0) {
                Bytes.remove(i);
            } else if (Bytes.get(i) != 0) {
                break;
            }
        }

        Bytes.add((byte) zeros);

        BYTES = null;
        System.gc();
        BYTES = new byte[(bytes.length - I) + Bytes.size()];
        int BCount = 0;

        for (int j = 0; j < BYTES.length; j++) {
            if (j < bytes.length - I) {
                BYTES[j] = bytes[j];
            }else{
                BYTES[j] = Bytes.get(BCount);
                BCount++;
            }
        }

        System.gc();

        return BYTES;
    }
}

