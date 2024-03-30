# FilePhpotographer
FilePhpotographer is an algorithem to create a set of images that represents a specific file and decode this set to retrieve the original file.

## how to use...
First we should set some variable...

    File file = new File("C:/Users/AL-AWAL/Desktop/evil-labs-scene.zip");
    File ImageFolder = new File("C:/Users/AL-AWAL/Desktop");
    File newFile = new File("C:/Users/AL-AWAL/Desktop/newFile.zip");

the first var `file` used to select the file to be encoded into a set of images, the next var `ImageFolder` used to select the folder wich we will save the final set of encoded images and get them when decoding so this diractory refer to a folder not a file (dont try to set a file in this var) and finally the last var `newFile` used to save the decoded file after retriving it from the encoded set of images.

Now we can run the code to create images and retrieve files...



- **Crerate images from a file:**

	after running the Main class you should enter a set on inputs to select what you want to do and as following...

	to decode a file to a set of images you should use the following set of inputs...

	`1--(each image size in MB)--(password)-enter n for(general image) OR f for(facebook image)`

	for example... `1 50 242 n`

	This input will create a set of images with maximum size of 50MB for each image and with a password that is (242), you will use this password to decode the images to get the original file and finally the (n) letter is to create a normal images or you can enter (f) to create images that can be sent over faceebook massanger service.

- **Decode images to get the original file:**

	to get the a file from the decoded images you will run the same Main class but this time with different inputs and as following...

	Decode inputs...

	`0--(Images count)--(password)`

	for example... `0 2 242`

	Here we enter 0 first to start decoding the images, the next input (2) is refere to the number of images in imageFile directory that you select previously and finally (242) is the password that we encode the first file with.

	After running this time we will see a new file that we select its directory with the newFile var.
