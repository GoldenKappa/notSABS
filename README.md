# SABS - [Knox SDK](https://seap.samsung.com/sdk/knox-android) frontend
[![Logo](https://raw.githubusercontent.com/LayoutXML/SABS/ca4c24e3c1e88c323aa9d1651a93119802994b64/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png "SABS")](https://github.com/LayoutXML/SABS/releases)

[![forthebadge](http://forthebadge.com/images/badges/gluten-free.svg)](http://forthebadge.com)
[![forthebadge](http://forthebadge.com/images/badges/powered-by-electricity.svg)](http://forthebadge.com)

## [Download](https://github.com/LayoutXML/SABS/releases) compiled releases

SABS is an open source tool that shows how to use Samsung Knox SDK without root. It lets developers see how various features work and test them. Some of the features are: **system-wide domain blocker, package disabler, permission manager, apps force stopper (examples include theme store), system-wide or app-specific port 53 blocker and more.**

SABS works on all non-rooted (and knox not triggered) Samsung devices with Android Lollipop 5.0 or higher and Samsung Knox version 2.5 or higher. Testers have confirmed that SABS works without any major issues on these devices: Samsung Galaxy **S8, S8+, Note 8, A3 2017, S7, S7 edge, S6, S6 edge, J7 Pro, J7 2016, J5 2016, J5 2015**. Some testers have confirmed that SABS works without any major issues but others have reported phone reboots on these devices: **S9, S9+**.

To develop apps that use Knox SDK or try them out, you need to **get the license key**. Development license key works for up to 10 devices. To develop an app based on SABS or try it out, you have to also **change the package name (id)** to get your own 10 development seats. Instructions are below.

[<img src="https://imgur.com/JTVAiVW.png" width="249" height="512">](https://github.com/LayoutXML/SABS/releases)
[<img src="https://imgur.com/Z8ssvOB.png" width="249" height="512">](https://github.com/LayoutXML/SABS/releases)
[<img src="https://imgur.com/3TbpVrR.png" width="249" height="512">](https://github.com/LayoutXML/SABS/releases)

## As featured in:
![featured in](https://imgur.com/vqRTbc7.png)

- **Lifehacker**:

"Simple Ad-blocker for Samsung (SABS for short) promises to remove all those pesky ads and then some. The app works by tapping into Samsung’s ultra-secure KNOX software. That gives it the ability to disable packages—A.K.A. the terrible bloatware slathered on by your carrier. SABS also includes a system-level permission manager, giving you an extra level of control over what apps can see and do on your phone."

- **XDA Developers**:

"Along with providing an ad blocker service, SABS can also disable system apps that are impossible to disable within the settings application without root. You can also use it to remove permissions from apps that you can’t usually control."

- **Gizmodo**:

"There are many adblockers in the Android App Store. Most eliminate ads or replace them with something that does not bother the eye. Simple Ad-blocker for Samsung (SABS) takes that function a little further and does something very welcome: put control of the applications in our hands."

**Also featured in**: Android Community, The Gioi Tre, iCrowdNewswire, TuttoAndroid, InfoGlitz, PametniTelefoni, GameOfThrone, AllAboutPhones,  AndroidCure, GizBlog and more!

## Table of contents
* [Donate](https://github.com/LayoutXML/SABS#donate)
* [Setup](https://github.com/LayoutXML/SABS#setup)
* [FAQ](https://github.com/LayoutXML/SABS#faq)
* [Contribute](https://github.com/LayoutXML/SABS#contribute)
* [External links](https://github.com/LayoutXML/SABS#external-links)

## Donate
You can donate to LayoutXML on **[Google Play](https://play.google.com/store/apps/details?id=com.layoutxml.support)** or **[PayPal](https://www.paypal.me/RJankunas)**. I'm a school student who is about to go to university. Every dollar helps.

## Setup
Congratulations, you are now a developer who is interested in developing (or trying out) an app that uses Knox sdk.

**The following guide is formatted like this:**
1. Setup for the first time
   1. Getting a Samsung license key
   2. Changing the package name
      1. With a computer, or
      2. With your phone
2. Setup for the update
   1. Getting a Samsung license key
   2. Changing the package name
      1. With a computer, or
      2. With your phone

Following steps 1-i-ii-a takes about 30 minutes. If you follow steps 1-i-ii-b it will take about 2 minutes.
Following steps 2-i-ii-a takes about 2 minutes. If you follow steps 2-i-ii-b it will take about 2 minutes.

Even though the method with a computer takes more the first time, the app will be more stable. Not to mention, that this method is completely free.
The method with a phone takes less time at first but you need to use paid app and the app will be less stable.

### 1. Setup for the first time
#### Step I - Getting a Samsung license key
<details>
  <summary>Click to expand instructions</summary>
Every user of SABS needs to receive a license key directly from Samsung.

Follow along [this video](https://www.youtube.com/watch?v=_FELkMOkP_Q) to make sure you are following the steps correctly. There are several cuts to hide my personal information in the video.

1. Sign in [here](https://seap.samsung.com/enrollment) with your Samsung account.
2. Go [here](https://seap.samsung.com/license-keys/create#section-knox-standard-sdk).
3. Go to "Legacy SDKs" (Steps 3-7 are demonstrated in [the picture here](https://i.imgur.com/LTAdkpW.png)).
4. Go to "Knox Standard SDK".
5. Select "Enterprise license key".
6. Write any word in alias.
7. Press "Generate license key".
8. Copy the key. **If the key starts with letters "KLM", you didn't follow the steps correctly.**
</details>

#### Step II - Changing the package name
You can choose to change a package name of the app with or without a computer. Before following the steps below, uninstall any KNOX adblocker you already have installed on your phone such as Adhell, Adhell 2 or Disconnect Pro.

##### a - With a computer
<details>
  <summary>Click to expand instructions</summary>
Follow along [this video](https://www.youtube.com/watch?v=cWO96NGnsSQ) to make sure you are following the steps correctly.
   
1. Download and extract the latest source code of the SABS app [here](https://github.com/LayoutXML/SABS/archive/master.zip).
2. Download, install and open Android Studio from [here](https://developer.android.com/studio/index.html).
3. Select "Open an existing Android Studio project" and choose the extracted folder
4. When everything is finished and the project is fully loaded (progress bar is gone at the bottom), open the "Gradle Scripts" and then "Build.Gradle (Module:app)" from the left side of your screen.
5. Find the line "applicationId "com.layoutxm1.sabs". Usually it's the 24th one.
6. Change the "com.layoutxm1.sabs" to a random or unique sequence of letters and digits. It is highly recommended to choose the same length (3.9.4 symbols).
7. An alert will appear at the top of the screen. Select "Sync Now".
8. Wait for the project to load once again.
9. Enable USB Debugging on your phone - Go to Settings > About phone > Software information > Press rapidly about 10 times on Build Number until a message "You are now a developer!" appears. > Go back to Settings > Developer Options > Turn on USB Debugging.
10. Connect your phone to the computer. On your phone a popup will appear. Select to allow a computer to use USB Debugging.
11. In Android Studio press a Play button or Shift+F10.
12. Select your phone and press OK.
13. SABS app will appear on your phone. Open it and follow instructions on the screen. When it asks for a license key - paste the one you have received in Step 1.
</details>

##### b - With your phone
<details>
  <summary>Click to expand instructions</summary>
The are multiple ways to change the package name of the app. Following guide focuses on editing the package name with "APK Editor Pro" app. You can also use different apps if such exist. 

Follow along [this video](https://www.youtube.com/watch?v=u4xQjYuiyVc) to make sure you are following the steps correctly.

1. Download the latest version (apk file) [here](https://github.com/LayoutXML/SABS/releases).
2. Open "APK Editor Pro" app.
3. Select apk file (previously downloaded).
4. Select "Common edit".
5. Edit only the field "Package name"! as shown [here](https://i.imgur.com/Jn7eu2O.png).
6. Change the "com.layoutxm1.sabs" to a random or unique sequence of letters. It is highly recommended to choose the same length (3.9.4 symbols). **It must start with com.**
7. Select "Direct rename", "Rename the package name in resources.arsc". Do **not** select the other options.
8. Press Save.
9. Press Install and Install again.
10. SABS app will appear on your phone. Open it and follow instructions on the screen. When it asks for a license key - paste the one you have received in Step 1.
</details>

### 2. Setup for the update.
You will receive the notification when a new version of SABS is released when you open the app. SABS will not check for updates in the background so make sure to regularly open the app.
#### Step I - Getting a Samsung license key
<details>
  <summary>Click to expand instructions</summary>
You may already have a Samsung license key. Open the old version which is already installed on your phone, go to SABS Settings > About > Copy KNOX key.
If your key expired, you can generate a new one after revoking the old one. The process should be similar to the one you have completed previously.
</details>

#### Step II - Changing the package name
To update the app, you don't need to uninstall the old version. Simply rename the new package name to the same you used before.

##### a - With a computer
<details>
  <summary>Click to expand instructions</summary>
Assuming you haven't uninstall Android Studio, follow the steps below.
   
1. Download and extract the latest source code of the SABS app [here](https://github.com/LayoutXML/SABS/archive/master.zip).
2. Open Android Studio and select "Open an existing Android Studio project" and choose the extracted folder.
3. You may be asked to install SDK and other files. Follow the instructions on the screen.
4. When everything is finished and the project is fully loaded (progress bar is gone at the bottom), open the "Gradle Scripts" and then "Build.Gradle (Module:app)" from the left side of your screen.
5. Find the line "applicationId "com.layoutxm1.sabs". Usually it's the 24th one.
6. Change the "com.layoutxm1.sabs" to the sequence of letters and digits you used before. You can see what was your previously used package name in SABS Settings > About > Copy Package Name.
7. An alert will appear at the top of the screen. Select "Sync Now".
8. Wait for the project to load once again.
9. Connect your phone to the computer.
10. In Android Studio press a Play button or Shift+F10.
11. Select your phone and press OK.
12. SABS app will appear on your phone. Open it and follow instructions on the screen. If it asks for a license key - paste the one you have received in Step 1.
</details>

##### b - With your phone
<details>
  <summary>Click to expand instructions</summary>
   
1. Download the latest version (apk file) [here](https://github.com/LayoutXML/SABS/releases).
2. Open "APK Editor Pro" app.
3. Select apk file (previously downloaded).
4. Select "Common edit".
5. Edit only the field "Package name"! as shown [here](https://i.imgur.com/Jn7eu2O.png).
6. Change the "com.layoutxm1.sabs" to the sequence of letters and digits you used before. You can see what was your previously used package name in SABS Settings > About > Copy Package Name.
7. Select "Direct rename", "Rename the package name in resources.arsc". Do **not** select the other options.
8. Press Save.
9. Press Install and Install again.
10. SABS app will appear on your phone. Open it and follow instructions on the screen. If it asks for a license key - paste the one you have received in Step 1.
</details>

## FAQ

### Is using SABS safe?

Yes, SABS is open source, it uses Samsung Knox SDK.

### Why do I have to get my own key?

To use Samsung Knox SDK you need to have a license key. You can get development license for free or become a Samsung partner with a registered business and going through vetting.

### Ads are not blocked on application X. What do I do?

SABS is simply a frontend for Samsung Knox. Standard package in app is very light to show what can be achieved with Samsung Knox. mmotti's package has more domains and may be used to block more domains. You can add other packages (blocklists) such as adaway if you want to block ads. You can also block domains yourself. You can also ask this question on xda thread (linked below) and learn what packages others are using.

### Do I have to be rooted?
No. Not only you don't have to be rooted, SABS only works on devices with knox not tripped.

## External links
[xda-developers thread](https://forum.xda-developers.com/devdb/project/?id=25252)

[LayoutXML on Reddit](http://reddit.com/u/LayoutXML)


---

SABS is licensed under the "MIT License". https://github.com/LayoutXML/SABS/blob/master/LICENSE.
