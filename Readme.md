*   [Overview](#Overview)
*   [Import](#Import)
*   [Internal Working](#Internal-Working)
*   [Usage](#Usage)
### Overview

An Android UI library which allows developers to create a cascading
dropdown. The multiple spinners are populated with data from a JSON file
provided in the **module-name/src/main/assets** folder. Check the
[sample APK](app-debug.apk) to see library in usage corresponding to the
JSON file [sample.json](app/src/main/assets/sample.json).

<p align="center">
<kbd>
<img src="screenshots/screenshot_2.png" alt="Device Logger" width="360" height="540">
</kbd>
</p>


### Import

* Clone or Download The project [Dynamic Spinner Sample](
                                https://github.com/Samagra-Development/cascading-dropdown)
                                
* Open your project in Android Studio and import the dynamicspinner
  module into your project. Goto **File-> New-> Import Module**.
  Navigate to where dynamicspinner module is located on the disk and
  select dynamicspinner directory.

* Once import is complete goto the build.gradle of the module in which
  you want to use [**DynamicSpinnerView**](/dynamicspinner/src/main/java/com/sample/tanay/dynamicspinner/DynamicSpinnerView.java)
  
  and add the line 
  
  ` implementation project(path: ':dynamicspinner') `
  
* Do a Gradle sync. You should be able to use the class
  [**DynamicSpinnerView**](/dynamicspinner/src/main/java/com/sample/tanay/dynamicspinner/DynamicSpinnerView.java)
  in your module.




  
### Usage

* Call the method `DynamicSpinnerView.setup(this /* application context
  required to read from assets*/, "sample.json" /* full name of the JSON
  file in the assets folder*/, new DynamicSpinnerView.SetupListener() {
  @Override public void onSetupComplete() {

                    }

                    @Override
                    public void onSetupProcessStart() {

                    }
                } /* an instance of the listener which will be called during the setup process*/,
                2 /*the version code in integer, if you want to use a different file as data
                 data source from the old file then the version code needs to be incremented*/);
`

**Note** see the sample app codebase for details

### Internal-Working
