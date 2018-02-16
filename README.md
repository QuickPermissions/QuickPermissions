# QuickPermissions [![Release](https://jitpack.io/v/QuickPermissions/QuickPermissions.svg)](https://jitpack.io/#QuickPermissions/QuickPermissions)

The most easiest way to handle Android Runtime Permissions.



* [Inspiration](#inspiration)
* [Add it to your app](#add-it-to-your-app)
* [How to do it?](#how-to-do-it)
  * [Let the library do all the hard stuff](#let-the-library-do-all-the-hard-stuff)
  * [You manage the rationale message showing](#you-manage-the-rationale-message-showing)
  * [You manage when permissions are permanently denied](#you-manage-when-permissions-are-permanently-denied)
* [Summary](#summary)



## Inspiration

Android runtime permissions was introduced in the Marshmallow (v 6.0) version of Android. It asks for permissions to user when they are running the app instead of asking for all the permissions while installing the app. It gives more control to users as they can give the permissions they want and deny to those who they do not fill comfortable with. 

With this, it has increased the pain in the neck for the developer, to enable/disable features based on what permissions user has granted or denied. The model asking for permissions was design to function asynchronously, which increased the complexity of an app largely. 

To make it with that, google has created it's own library [easypermissions](https://github.com/googlesamples/easypermissions). (*I didn't find it easy, but that's what they said*). Still, you have to do handful of things and it's asynchronous way of handling things make it hard to manage. There are many other libraries as well to help developers easily handle it. **But,** all libraries has to manage it with proxy classes or managing and passing callbacks and all. 

So, to solve this issue this library is created. Asking for permissions synchronous way (*It looks like that, but won't block the main thread, don't worry, no ANRs, promise!*). Give your method an annotation, will hold that method and asks for permission, if currently does not have the mentioned permission. And after the permissions are granted, it will continue executing the method which was on hold. As simple as that.



## Add it to your app

In your, **project**'s `build.gradle` file:

```groovy
buildscript {
    repositories {
        google()
        jcenter()
        maven { url "http://jitpack.io/" } // <-- THIS SHOULD BE ADDED

    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.1'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
	    // ...
        // BELOW LINE THIS SHOULD BE ADDED
        classpath 'com.github.QuickPermissions:QuickPermissions:0.3.0' 
    }
}


allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { url "http://jitpack.io/" }  // <-- THIS SHOULD BE ADDED
    }
}
```

I have added comments which 3 lines should be added. As this library is using jitpack to publish this, you need to add jitpack url. If you already have, do not add that again.



In your **app**'s `build.gradle` file, apply the plugin: 

```
apply plugin: 'com.livinglifetechway.quickpermissions_plugin'
```



Now, read below for using directions.



> Supports both `Java` and `Kotlin`



## How to do it?

There are several ways you can do this.

1. Let the library do all the hard stuff
2. You manage the rationale message showing
3. You manage when permissions are permanently denied



Add this to your `Activity` extending `AppCompatActivity` or `Fragment` from the **support library.** 



### Let the library do all the hard stuff

Add a single annotation, and you are all done. Library will manage everything, will ask for permissions, will show rationale dialog if denied and also ask to user to allow permissions from settings if user has permanently denied some permission(s) required by the method.



```java
@WithPermissions(
    permissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}
)
public void methodWithPermissions() {
    Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
    // 
    // Do the stuff with permissions safely
    //
    // .....
}
```

That's it! You are good to go.

Optionally, you can provide custom messages that will be shown when rationale dialog is shown, or permanently denied dialog is shown. You can pass `rationaleMessage` and/or `permanentlyDeniedMessage` to override default message.

```java
@WithPermissions(
    permissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION},
    rationaleMessage = "Custom rational message",
    permanentlyDeniedMessage = "Custom permanently denied message"
)
public void methodWithPermissions() {
    Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
    // 
    // Do the stuff with permissions safely
    //
    // .....
}
```



**NOTE:** The one thing is to make sure, your function should not return anything (return type should be void), as the asking permissions is asynchronous behavior, that can not be handled in a true synchronous way.



### You manage the rationale message showing

If you don't want the library to manage the rationale dialog showing, and you want to manage it on your own, you can do that by adding a method in your activity/fragment by adding a method with single argument of type `QuickPermissionsRequest` and annotate it with annotation `OnShowRationale` . So, whenever there is need of showing a rationale dialog, you will receive a callback on this method. You can call `proceed()` on it, to continue the asking permissions flow or `cancel()` to cancel the flow.



```java
@WithPermissions(
    permissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION},
)
public void methodWithPermissions() {
    Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
}


@OnShowRationale
public void onShowRational(final QuickPermissionsRequest arg) {
    new AlertDialog.Builder(this)
    .setTitle("Permissions Denied")
    .setMessage("These permissions are required to proceed futher. Please retry.")
    .setPositiveButton("Try Now", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            arg.proceed(); // this will continue asking for the permissions for the denied once
        }
    })
    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            arg.cancel(); // this will cancel flow and the method won't be called
        }
    })
    .show();

}

```



**Note:** These methods with annotations should be public. 



### You manage when permissions are permanently denied



If you don't want the library to manage the case where some permission(s) are permanently denied by asking it to open app settings and allow it from there, you can do that by adding a method in your activity/fragment by adding a method with single argument of type `QuickPermissionsRequest` and annotate it with annotation `OnPermissionsPermanentlyDenied`. So, whenever there is a need of showing a message or asking user to open app settings to grant permanently denied permissions, you can receive a callback on this method. You can call `openAppSettings()` on it to continue flow or `cancel()` to cancel the flow.





```java
@WithPermissions(
    permissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION},
)
public void methodWithPermissions() {
    Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
}


@OnPermissionsPermanentlyDenied
public void whenPermissionsArePermanentlyDenied(final QuickPermissionsRequest arg) {
    new AlertDialog.Builder(this)
    .setTitle("Permissions Permanently Denied")
    .setMessage("These permissions are required to proceed futher. Please allow from app settings")
    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            // this will open app settings for allowing permissions and wait for the results
            arg.openAppSettings(); 
        }
    })
    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            arg.cancel(); // this will cancel flow and the method won't be called
        }
    })
    .show();

}
```



-----

You can also combine the above methods to change your flow. Like, if you want to manage rationale and permissions permanently denied case, you can add both the methods with appropriate annotations on it and you will receive a callback.



On, above methods, there is an additional method callback, if you want to handle something when the when asking permission flow is completed and some permissions are still missing. With permissions still not being granted, the original method can not be called. So you want to show users a message or do something to tell them that this functionality will not work or like that, you can do so with `@OnPermissionsDenied` annotation.

For example,

```java
@OnPermissionsDenied
public void onDenied(QuickPermissionsRequest arg) {
    Toast.makeText(
        this, 
        "Without permissions, this feature will not be available", 
        Toast.LENGTH_SHORT
    ).show();	
}
```



## Summary

Summary of the annotations you can use

| Annotation                        | Method Arguments (if any) | Description                                                  |
| --------------------------------- | ------------------------- | ------------------------------------------------------------ |
| `@WithPermissions`                |                           | Annotate a method with this annotations and pass the `permissions` array. It will handle all the things by itself. Return type should be `void`<br />Arguments you can pass:<br />* `permissions` - list of permissions as array<br />* `rationaleMessage` - A custom message to be shown, when rationale dialog is shown<br />* `permanentlyDeniedMessage` - A custom message to be shown, when permanently denied dialog is shown<br />* `handleRationale` - Default true. Indicates weather rationale case should be handled or not<br />* `handlePermanentlyDenied` - Default true. Indicates weather permanently denied permission(s) case should be handled or not |
| `@OnShowRationale`                | `QuickPermissionsRequest` | Annotate a method with this annotation when you want to handle rationale case. The (public) method should have one argument of type `QuickPermissionsRequest`. If `handleRationale` was set to false, this method will not be called. |
| `@OnPermissionsPermanentlyDenied` | `QuickPermissionsRequest` | Annotate a method with this annotation when you want to handle permanently permissions denied case. The (public) method should have one argument of type `QuickPermissionsRequest`. If `handlePermanentlyDenied`was set to false, this method will not be called. |
| `@OnPermissionsDenied`            | `QuickPermissionsRequest` | (Public) Method with this annotation will be called when, no permissions are not granted when the flow is complete. The method should have one argument of type `QuickPermissionsRequest`. |



Have any questions, or any trouble? Create an issue. 
