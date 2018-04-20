# AuthNative
React Native Facebook Login with Native Module




To Setup:


Go to the android/ directory of your react-native project

Create a file named local.properties with this line:

sdk.dir = /Users/USERNAME/Library/Android/sdk

If your on windows:

sdk.dir = C:\\Users\\USERNAME\\AppData\\Local\\Android\\sdk 

Where USERNAME is your OSX username


Open up the strings.xml file located at  res/values. 

Change the fb_app_id  by replacing the 0’s with your Facebook app id.



To Run: 
open terminal, change directory to project

$ npm install

$ react-native run-android



Here are the versions of the pieces to create this project

React native version: 0.55.2

Facebook Android sdk: 4.17.0

Android compileSdkVersion: 23

These versions get along nice with the project I created below with react-native init. As you well know, different version can lead to different results.

