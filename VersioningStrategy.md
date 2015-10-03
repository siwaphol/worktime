# Versioning Strategy #
This document describes our vision on versioning the application on the Google Code platform.
## Table Of Contents ##

## Source Code ##
All the code is released under the Apache License 2.0, so the entire project is open sourced! The source code of the application is available in the [Source Tab](http://code.google.com/p/worktime/source). Over there you have the options to do a checkout of the code, just browse through the code or see the entire change log. The system used for versioning the code is [Subversion](http://subversion.apache.org/).
You can do an entire checkout of the code but you cannot commit. Altough if you found an issue or have an idea to improve the application you can work on the code you checked out, play around and apply your changes and then submit an issue in the issue tracker and attach a patch to the issue. We can then accept your patch.
**If you submit a patch, do not forget to mention on which source-directory you have been working (trunk, a specific tag or brach) and on which revision this patch should work.**
## Subversion (SVN) ##
### Trunk ###
All the code we are currently working on (for a new release) is available in the [trunk](http://code.google.com/p/worktime/source/browse/#svn%2Ftrunk) directory. So that is were all the new work is going on and the interesting stuff is happening.
### Branches ###
In the [Branches](http://code.google.com/p/worktime/source/browse/#svn%2Fbranches) directory you will find a directory for each new release that is made. These branches are not under development, only under maintenance. Whenever a new release is made, a new branch is created. New branches are, however, only created for mainstream releases. For a bug release the branch is updated, but no extra branch is created.
### File Structure ###
Example file structure:
  * svn
    * trunk
      * android-app
        * src
        * AndroidManifest.xml
        * ...
      * iphone-app
        * ...
    * branches
      * 1.1.1
        * android-app
          * src
          * AndroidManifest.xml
          * ...
        * iphone-app
          * ...
      * 1.1.2.a
        * android-app
          * src
          * AndroidManifest.xml
          * ...
        * iphone-app
          * ...
      * 1.1.2.b
        * android-app
          * src
          * AndroidManifest.xml
          * ...
        * iphone-app
          * ...
      * 1.1.2
        * android-app
          * src
          * AndroidManifest.xml
          * ...
        * iphone-app
          * ...
      * 1.1.3
        * android-app
          * src
          * AndroidManifest.xml
          * ...
        * iphone-app
          * ...
## Contribute ##
As mentioned before you can check out the source code but by default you cannot contribute and commit any code. However there are two ways you can contribute:
  1. **Create patches**
  1. **Become a project member**
### Project Setup ###
In either way (described in the next points) you will need to set up the project. Therefore it's not a bad idea to read the [Project Setup](ProjectSetup.md) documentation.
### Create Patches ###
If you have a great idea for the application or found a bug and you want to contribute and help us move forward faster, then you can! All you need is a good IDE and some common sense, and there you go... Most IDE's (Eclipse, IntelliJ, Netbeans,...) have good plugins or default support for SVN and they should support the creation of patches.
So here's a step-by-step plan what you need to do:
  1. Get the source code (Always get the source code from the [trunk](http://code.google.com/p/worktime/source/browse/#svn%2Ftrunk) to make changes) and configure the project based on the [Project Setup](ProjectSetup.md) wiki page
  1. Load your developer skills and start coding
  1. Once you are done coding, make sure you test your code by deploying the application on an emulator
  1. Next you can create a patch
    1. In Eclipse: right click the project and choose the 'team' option, next choose 'Create Patch...'
    1. Next you should create a file on your local file system, as extension choose for 'txt' or 'patch' and save it
  1. Now create an issue on Google code (or select an issue that is already there) and add your patch to it
Next we will evaluate your patch (is the issue valid, is your patch working, does it fix/improve the issue...) and implement it if everything is ok.
### Become a member ###
You can also become an active member of the application and help us developing new features on a frequently base. Becoming a member means that you can submit changes and you will ask you to be responsible to some parts of the application/development process.
What you need to do in order to become a project member:
  1. Get the source code (Always get the source code from the [trunk](http://code.google.com/p/worktime/source/browse/#svn%2Ftrunk) to make changes) and configure the project based on the [Project Setup](ProjectSetup.md) wiki page
  1. Create an issue in the [Google Code Issue Tracker](http://code.google.com/p/worktime/issues/entry)
    1. On top select the template 'Member Request'
    1. Answers the questions in the description
  1. While you wait for a response from us you can start looking around in the code, start coding, test stuff,...
We will try to answer your member request as soon as possible, but don't think we can respond to every request in 10 minutes ;)