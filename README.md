# The Student Cookbook

## Checking out, building, and running the application

In order to clone the repository and run the code, following the steps outlined below:

1. Acquire the code from Github:  
  a. Revision Number: e87d9df  
  b. Revision URL: https://github.com/The-Student-Cookbook/The-Student-Cookbook/tree/66ecbd148df3fa14793570a5a8747aa4b4cd2e2a  
  c. Click "Download ZIP" on the right side of the screen  
2. Acquire Android Studio and the Android SDK
  a. http://developer.android.com/sdk/index.html  
  b. Follow on-screen install instructions
3. Open Android Studio  
  a. Open the project The-Student-Cookbook  
4. Build and run the code  
  a. Build -> Make project  
  b. Run -> Run 'app'  
    i. When prompted, create a new virtual Android emulator or attach an Android phone via USB
    ii. The app should launch on the emulator or the actual phone

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
