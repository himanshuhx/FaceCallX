# FaceCallX 

A Video Caliing Application built in Android Studio 3.6.3 with Firebase RealTime Database Support and [TokBox](https://tokbox.com/developer/) API.



## Features of Application

- This Application uses Firebase phone Authentication as a SignUp method for users.
- Users can create account through their phone number so it comes with [Country Code Picker](https://github.com/hbb20/CountryCodePickerProject).
    `(CCP) is an android library which provides an easy way to search and select country or international phone code.`
- User can `set` and `update` their Profile Image, Username, Status in Settings Activity of the Application.
- App has `Find Friends Activity` where users can look around for different people using the application or thay can `search` a specific user also, through their username through Search Bar.
- User can view different users profile and can send them `Friend Request` through `Add Friend` Button in user's profile activity.
- User gets `Real-time notification` with Firebase for every friend request received through the Notification Activity of application.
- User has the choice to Accept or Decline Friend Request received with `Accept Friend Request` and `Cancel Friend Request` Button in Notifications Activity.
- Request Sender is added automatically to the Contacts Activity of Current User if a user Accepts the friend request.


## Screenshots  :camera:
![pic9](https://user-images.githubusercontent.com/65825310/85925308-27876f00-b8b5-11ea-900e-c50149453397.png)
![pic1](https://user-images.githubusercontent.com/65825310/85925357-8947d900-b8b5-11ea-80de-69e8519dc40a.png)
![pic2](https://user-images.githubusercontent.com/65825310/85925438-1a1eb480-b8b6-11ea-9468-34b88c6ce33b.png)
![pic3](https://user-images.githubusercontent.com/65825310/85925464-36225600-b8b6-11ea-9ba2-592423d8b14b.png)
![pic4](https://user-images.githubusercontent.com/65825310/85925481-50f4ca80-b8b6-11ea-9db3-0f1f00d92981.png)
![pic8](https://user-images.githubusercontent.com/65825310/85925530-9ca77400-b8b6-11ea-96c3-9d5227406722.png)
![pic6](https://user-images.githubusercontent.com/65825310/85925513-797cc480-b8b6-11ea-9660-1b2dfb5da7d7.png)
![pic7](https://user-images.githubusercontent.com/65825310/85925523-86011d00-b8b6-11ea-9a80-c442bc55adfb.png)
![pic5](https://user-images.githubusercontent.com/65825310/85925490-64079a80-b8b6-11ea-93a7-4920fd820b13.png)


## Firebase ScreenCasts 
<img src="https://user-images.githubusercontent.com/65825310/86031851-132da880-ba54-11ea-95ff-4119146429ac.gif" width="800" height="400">
<img src="https://user-images.githubusercontent.com/65825310/86034244-d8c60a80-ba57-11ea-959b-741c1f0c08d5.gif" width="800" height="400">


## Extra Gradle Dependencies
```Java
    implementation 'com.google.firebase:firebase-database:19.3.0'
    implementation 'com.google.firebase:firebase-storage:19.1.1'
    implementation 'com.google.firebase:firebase-auth:19.3.1'
    implementation 'com.firebaseui:firebase-ui-database:6.2.1'

    implementation 'com.hbb20:ccp:2.1.9'
    implementation 'pub.devrel:easypermissions:0.4.0'
    implementation 'com.squareup.picasso:picasso:2.71828'
    implementation 'com.opentok.android:opentok-android-sdk:2.15.3'
    implementation 'pub.devrel:easypermissions:0.4.0'
    implementation 'com.airbnb.android:lottie:3.4.0'
```

