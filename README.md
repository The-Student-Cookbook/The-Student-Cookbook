# The Student Cookbook

## Checking out, building, and running the application

In order to clone the repository and run the code, following the steps outlined below:

1. Acquire Android Studio and the Android SDK
  a. Download from http://developer.android.com/sdk/index.html
  
  [step1.jpg]
  
  b. Launch the installed application and choose "Standard" as the install type.
  
  [step2.jpg]
  
  c. Take the defaults and click finish to start the installation of the SDK.
  
  [step3.jpg]
  
2. Acquire the code from Github:  
  a. Release URL: https://github.com/The-Student-Cookbook/The-Student-Cookbook/releases/tag/v0.9  
  b. Download the source code as a zip and unzip it into a local directory
  d. **Important: Delete The-Student-Cookbook.iml file from the top level of the project**
  e. Open Android Studio and click "Open an existing Android Studio Project"
  f. Select the "The-Student-Cookbook-0.9" folder as your project directory

3. Open Android Studio  
  a. Open the project The-Student-Cookbook  
4. Build and run the code  
  a. Build -> Make project  
  b. Run -> Run 'app'  
    i. When prompted, create a new virtual Android emulator or attach an Android phone via USB
    ii. The app should launch on the emulator or the actual phone

4. Build the Project
  a. Once the project is imported, from the top menu, select "build" --> "make project"
  
  [step4.jpg]
  
5. Run the Project
  a. Select the green "run" icon from the top bar in Android Studio
  
  [step5.jpg]
  
  b. A "device chooser" dialog will pop up.  Click the "..." button near the bottom to open the virtual device manager.
  
  [step6.jpg]
  
  c. "Create Virtual Device...", then select the Nexus 6 emulator.
  d. Choose the download link next to the Marshmallow release for x86_64.  
  e. It will download and then eventually you'll end back at the "device chooser" dialog
  
  [step7.jpg]
  
  f. Choose the Nexus 6 emulator you just downloaded, click ok
  g. The application will now launch.  It should launch and open up into the application.
  
  [step8.jpg]

## Running the unit test suite

1. Open Android Studio  
  a. Open the project The-Student-Cookbook  
2. Build the code  
  a. Build -> Make project  
3. Run Unit tests  
  a. Locate the "Build Variants" pane on the bottom left side of the screen  
  b. Set "Test Artifact" to "Unit Tests"  
  c. Locate the "Project" pane on the left side of the screen  
  d. Using the file hierarchy explorer, navigate to app/java/cs506.studentcookbook (androidTest)/  
  e. Right click on cs506.studentcookbook (androidTest) -> Run 'Tests in 'cs506.studentcookbook  
  f. Test results appear in the "Run" pane on the bottom of the screen.   
4. Run Instrumentation Tests    
  a. Locate the "Build Variants" pane on the bottom left side of the screen   
  b. Set "Test Artifact" to "Android Instrumentation Tests"  
  c. Locate the "Project" pane on the left side of the screen  
  d. Using the file hierarchy explorer, navigate to app/java/cs506.studentcookbook (androidTest)/  
  e. Right click on cs506.studentcookbook (androidTest) -> Run 'Tests in 'cs506.studentcookbook  
  f. Test results appear in the "Run" pane on the bottom of the screen.  

## Tagging the Code
```
git tag -a vXX.XX
```

An editor will open where you will need to put a message about the release in.
Quit the editor and the tag will be made.
To push the tag to the remote repository, use:
```
git push origin --tags
``` 
