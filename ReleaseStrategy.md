# Release Strategy #
This document contains an overview of the basic release strategy we try to keep up for this project.

Every release has a version number. A version number should be of the format x.y.z, x.y.z.c, where x, y and z represent a number and c represents a number or a character.

  * A release of type x.0.0 is called a version release.
  * A release of type x.y.0 is called a major release.
  * A release of type x.y.z is called a module release.
  * A release of type x.y.z.c is called a feature or bugfix release

The hierarchy of releases is:
  * Version Release
    * Major Release
      * Module Release
        * Feature Release
  * Bugfix Release

## Version Releases ##
This kind of release will not be seen frequently, they are even rarely. When we decide to do a version release we go from, for example, version 1.0.0 to 2.0.0. Such a release will only happen if something really big has changed (entire UI has changed for example) and we want to make a statement that this still is the same application, but not at all the same as before!
## Major Releases ##
Major releases will occur more frequently, although not too often! There's also no real rule defined for a major release, it's more a commen sense rule. If there are a lot of new features/modules over the last few months, then we might start to think to do a new major release.
## Module Releases ##
Module releases basically means that we decide that one or more modules have changed enough over the last few weeks/months that we say that it's evolved enough to call it "better". Only at that point in time we do a module release. Those releases will happen a few times per year. In the [Road Map](RoadMap.md) you will see that every module release has a title refering to the module/part of the application that will be affected in this module release.
## Feature|Bugfix Releases ##
The main difference between a feature release and a bugfix release is how the release number is build.
In a feature release the build number will end on a character. Example: 1.2.5.a.
In a bugfix release the build number will end on a number. Example: 1.2.5.1
### Feature Releases ###
This kind of releases will happen a lot. We will limit a feature release to only two issues to be implemented. When those two issues are implemented we do a feature release. If necessary we can add critical bugfixes to this release.
After a set set of feature releases a module release will be done. So a set of feature releases that follow each other in time will aim to build a new module or change some stuff to a module.
Because of limiting the number of issues to be implemented in a feature release we can release quick and respond very fast to user requests.
### Bugfix Releases ###
Over time we will decide to quickly do a bugfix release in between other releases. So for a bugfix release we cannot say that after a set of releases we will do a feature, module or version release. We treath bugfix releases at the lowest level of releases but in fact they can exist on their own!
So a bugfix release could have following version numbers:
1.4.2.2 (bugfix release during a module release)
1.4.0.2 (bugfix release during a major release)
1.0.0.2 (bugfix release during a version release)