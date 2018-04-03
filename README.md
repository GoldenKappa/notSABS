# Simple AdBlocker for Samsung (SABS)
[![Logo](https://imgur.com/fNCaCMl.png "SABS")](https://github.com/LayoutXML/SABS/releases)

[![forthebadge](http://forthebadge.com/images/badges/gluten-free.svg)](http://forthebadge.com)
[![forthebadge](http://forthebadge.com/images/badges/powered-by-electricity.svg)](http://forthebadge.com)

## [Download!](https://github.com/LayoutXML/SABS/releases)

SABS is a system-wide, rootless AdBlocker, package disabler, permission manager and more. It works by using Samsung's KNOX SDK, therefore only works on Samsung devices. Because of a sheer amount of Samsung phones, all with different Android and KNOX versions, screen sizes and other specifications, SABS only focuses on Galaxy S8, S8+ and Note 8 devices with latest Android version, though should work on all Samsung devices with Android version 5.0 or higher. What makes SABS stand out from other adblockers is that it can make reversable system level changes because of the Samsung's KNOX tools, and doesn't run in the background. With it, you can easily block url domains, disable system apps that you can't in settings, remove permissions from apps that you can't usually control.

[![Screenshot1](https://imgur.com/OyYfMtZ.png)](https://github.com/LayoutXML/SABS/releases)
[![Screenshot2](https://imgur.com/EZPirSP.png)](https://github.com/LayoutXML/SABS/releases)
[![Screenshot3](https://i.imgur.com/4X3o9CR.png)](https://github.com/LayoutXML/SABS/releases)

## Table of contents
* [Donate](https://github.com/LayoutXML/SABS#donate)
* [Setup](https://github.com/LayoutXML/SABS#setup)
* [Issues](https://github.com/LayoutXML/SABS#issues)
* [FAQ](https://github.com/LayoutXML/SABS#faq)
* [Feedback](https://github.com/LayoutXML/SABS#feedback)
* [Contribute](https://github.com/LayoutXML/SABS#contribute)

## Donate
You can now donate to the LayoutXML on **[Google Play](https://play.google.com/store/apps/details?id=com.layoutxml.support)** or **[PayPal](https://www.paypal.me/RJankunas)**. I'm a school student who is about to go to university. Every dollar helps.

## Setup
Setup for the SABS takes some time and technical skills. You are expected to be able to follow the steps and have some knowledge about how some things work.
### Setup for the first time
There are two things you have to do to get a working version of SABS - get a Samsung license key, and change the package name for your copy of SABS. Steps below will help you but be careful - one step done incorrectly will make the app not work!
#### Step 1 - Getting a Samsung license key
Every user of SABS needs to receive a license key directly from Samsung.
1. Sign in [here](https://seap.samsung.com/enrollment) with your Samsung account.
2. Go [here](https://seap.samsung.com/license-keys/create#section-knox-standard-sdk).
3. Go to "Legacy SDKs" (Steps 3-7 are demonstrated in [the picture here](https://i.imgur.com/LTAdkpW.png)).
4. Go to "Knox Standard SDK".
5. Select "Enterprise license key".
6. Write any word in alias.
7. Press "Generate license key".
8. Copy the key. **If the key starts with letters "KLM", you didn't follow the steps correctly.** Complete the steps 3-7 again.

#### Step 2 - Changing the package name
You can choose to change a package name of the app with or without a computer. Before following the steps below, uninstall any KNOX adblocker you already have installed on your phone such as Adhell, Adhell 2 or Disconnect Pro.
##### A - With a computer
1. Download and extract the latest source code of the SABS app [here](https://github.com/LayoutXML/SABS/archive/master.zip).
2. Download, install and open Android Studio from [here](https://developer.android.com/studio/index.html).
3. Select "Open an existing Android Studio project" and choose the extracted folder
4. You will be asked to install SDK and other files. Follow the instructions on the screen.
5. When everything is finished and the project is fully loaded (progress bar is gone at the bottom), open the "Gradle Scripts" and then "Build.Gradle (Module:app)" from the left side of your screen.
6. Find the line "applicationId "com.layoutxml.sabs". Usually it's the 24th one.
7. Change the "com.layoutxml.sabs" to a random or unique sequence of letters and digits. It is highly recommended to choose the same length (3.9.4 symbols).
8. An alert will appear at the top of the screen. Select "Sync Now".
9. Wait for the project to load once again.
10. Enable USB Debugging on your phone - Go to Settings > About phone > Software information > Press rapidly about 10 times on Build Number until a message "You are now a developer!" appears. > Go back to Settings > Developer Options > Turn on USB Debugging.
11. Connect your phone to the computer. On your phone a popup will appear. Select to allow a computer to use USB Debugging.
12. In Android Studio press a Play button or Shift+F10.
13. Select your phone and press OK.
14. SABS app will appear on your phone. Open it and follow instructions on the screen. When it asks for a license key - paste the one you have received in Step 1.

##### B - With your phone
The are multiple ways to change the package name of the app. Following guide focuses on editing the package name with "APK Editor Pro" app. You can also use different apps such as App Cloner or Lucky Patcher (keep in mind that using other features of Lucky Patcher might be illegal!) but select to only edit Manifest file.
1. Download the latest version (apk file) [here](https://github.com/LayoutXML/SABS/releases).
2. Open "APK Editor Pro" app.
3. Select apk file (previously downloaded).
4. Select "Common edit".
5. Edit only the field "Package name"! as shown [here](https://i.imgur.com/Jn7eu2O.png).
6. Change the "com.layoutxml.sabs" to a random or unique sequence of letters and digits. It is highly recommended to choose the same length (3.9.4 symbols).
7. Select "Direct rename", "Rename the package name in resources.arsc". Do **not** select the other options.
8. Press Save.
9. Press Install and Install again.
10. SABS app will appear on your phone. Open it and follow instructions on the screen. When it asks for a license key - paste the one you have received in Step 1.

### Setup for the update.
You will receive the notification when a new version of SABS is released when you open the app. SABS will not check for updates in the background so make sure to regularly open the app.
#### Step 1 - Getting a Samsung license key
You may already have a Samsung license key. Open the old version which is already installed on your phone, go to SABS Settings > About > Copy KNOX key.
If your key expired, you can generate a new one after revoking the old one. The process should be similar to the one you have completed previously.
#### Step 2 - Changing the package name
To update the app, you don't need to uninstall the old version. Simply rename the new package name to the same you used before.
##### A - With a computer
Assuming you haven't uninstall Android Studio, follow the steps below.
1. Download and extract the latest source code of the SABS app [here](https://github.com/LayoutXML/SABS/archive/master.zip).
2. Open Android Studio and select "Open an existing Android Studio project" and choose the extracted folder.
3. You may be asked to install SDK and other files. Follow the instructions on the screen.
4. When everything is finished and the project is fully loaded (progress bar is gone at the bottom), open the "Gradle Scripts" and then "Build.Gradle (Module:app)" from the left side of your screen.
5. Find the line "applicationId "com.layoutxml.sabs". Usually it's the 24th one.
6. Change the "com.layoutxml.sabs" to the sequence of letters and digits you used before. You can see what was your previously used package name in SABS Settings > About > Copy Package Name.
7. An alert will appear at the top of the screen. Select "Sync Now".
8. Wait for the project to load once again.
9. Connect your phone to the computer.
10. In Android Studio press a Play button or Shift+F10.
11. Select your phone and press OK.
12. SABS app will appear on your phone. Open it and follow instructions on the screen. If it asks for a license key - paste the one you have received in Step 1.

##### B - With your phone
1. Download the latest version (apk file) [here](https://github.com/LayoutXML/SABS/releases).
2. Open "APK Editor Pro" app.
3. Select apk file (previously downloaded).
4. Select "Common edit".
5. Edit only the field "Package name"! as shown [here](https://i.imgur.com/Jn7eu2O.png).
6. Change the "com.layoutxml.sabs" to the sequence of letters and digits you used before. You can see what was your previously used package name in SABS Settings > About > Copy Package Name.
7. Select "Direct rename", "Rename the package name in resources.arsc". Do **not** select the other options.
8. Press Save.
9. Press Install and Install again.
10. SABS app will appear on your phone. Open it and follow instructions on the screen. If it asks for a license key - paste the one you have received in Step 1.

## Issues
* Sometimes license cannot be activated. Make sure you have the key that **does not** start with letters "KLM". If after multiple tries and app restarts you still receive a license activation error, reinstall the app with a different package name.
* Sometimes adblocker stops working but other parts of the app continue to work if more than one adblocker is in use. If it happens to you, turn off blocking in all apps that use KNOX SDK such as Adhell, Adhell 2 or Disconnect pro, and SABS. Turn on blocking in SABS again. Wait a couple of minutes before concluding that adblocking doesn't work again (it takes some time to block all domains).
* Ads are not blocked in Youtube. Youtube serves its ads from the same domain as videos so there's no way of blocking them.
* Ads may not be blocked in Chrome. If that is the case for you, go to SABS Settings > Miscellaneous > Turn on "Block Port 53". Chrome recently had some changes in how it works but luckily you can fix it.
* Some apps that need internet or websites might work incorrectly if their domain is blocked. Whitelist them in settings. It is recommended to whitelist some Google apps such as Allo, Duo, Music.

## FAQ

### Is using SABS legal?
Yes, getting a free license key from Samsung is legal and using it in SABS does not violate any terms and conditions assuming you call yourself a developer.

Blocking ads itself is also not illegal but it's a moral grey area. Ask yourself - do you want to support app developers and website creators? If yes, then don't use an adblocker or whitelist those apps or websites in settings. Remember that by not blocking ads you are letting companies track websites you use and spy on you.

### Why do I have to get my own key?
For me to get a production license key means creating a company, becoming a Samsung's partner or buying an expensive license key from Samsung's partners. I can't afford that and developing SABS is just my hobby.

### Will this work on my device?
SABS only works for some Samsung devices. If you use a Galaxy S8, S8+ or Note 8, you don't need to worry. If you are using a different Samsung device, try and see it yourself.

### Where does it block ads?
SABS blocks ads in many apps including browsers. If you still see some ads, you can send a message to the developer on [Reddit](reddit.com/u/LayoutXML) with a list of apps where ads are not blocked.

### Where is the "hosts" file with blocked domains?
It's on the Github page. File is named standard-package.txt. You can add your own sources like [Adaway](https://adaway.org/hosts.txt) in app settings. Source file must only have url domains, 127.0.0.1s and comments in lines starting with #. Standard package is updated independently to app version.

### Can I use it with Disconnect Pro?
It is not recommended as both SABS and Disconnect Pro use Samsung's Firewall. SABS has all the features of Disconnect Pro and more.

### Does it drain battery?
No, not at all. SABS might even improve your battery as apps cannot constantly communicate with ad and tracker domains. SABS doesn't run in background because it doesn't need to.

### Do I have to be rooted?
No. SABS might not even work if you are rooted.

## Feedback
I'm eagerly waiting for your feedback on [Reddit](reddit.com/u/LayoutXML), on the [Issue tracker](https://github.com/LayoutXML/SABS/issues) and on [XDA-developers](https://forum.xda-developers.com/android/apps-games/app-sabs-simple-adblocker-samsung-t3751722). Message me with your suggestions, bug reports and list of apps where ads are not blocked. Please limit the feedback on issue tracker to issues only.

## Contribute
I welcome any help. Translations are not my priority at the moment and could wait at least for beta releases.
As a student (at school) with no background in Android development I'm struggling with a few things such as
* Permission search (list search)
* TXT file chooser and saver (for import/export of disabled packages)
* Sorting of apps, permissions
* Background ad domains updater, app updater.

These wonderful people have contributed to the open source project SABS or its predecessor Adhell either directly or indirectly:
* LayoutXML (me!)
* raiym
* ca0ss
* MilanParikh
* tarquinn18
* deVorteX
* david082321
* RJankunas
* neFAST
* SanderGit
* plantoschka
* SecretlyTaco
* Mike B.
* mmotti
* fusionjack
* COUNTLESS more who reported the issues, asked for features and sent the code snippets on Github, Reddit and XDA-developers.

---

SABS is licensed under the "MIT License". https://github.com/LayoutXML/SABS/blob/master/LICENSE.
