# Project Setup Android #
## Table Of Contents ##

## Getting Started ##
### Android SDK ###
### IDE ###
### Source Code ###
### Emulator ###
## Architecture ##
The application is created on top of the Android SDK version 7. On top of that we used two extra libraries to ease the development.
  * RoboGuice which is a dependency framework, build on top of Goolge Guice.
  * OrmLite which is an ORM (Object Relational Mapping) package to make it easier to query the SQLite database on your Android device.
TODO IMAGE
### Layers ###
The image above describes the general architecture of the application. However if we look a bit more in detail to the WorkTime for Android application you will notice that we use a layered-architecture as you can see on the next image.
TODO IMAGE
As you can see on the image we have 7-step procedure when the user performs an action on the screen.
  1. User-action: The uer performs an action the screen (eg: press on button) and the activity is triggered. In that activity user input values can be loaded and used for the next step.
  1. Perform an action: The activity can call a service to perform a certain action (manipulate data, load data, calculate something,...).
  1. Manipulate/Load data: From the service one or more calls to a DAO can be done, which basically is a request to do something (either a data manipulation or data loading) on the database.
  1. Query database: The DAO generates a query (we can build dynamic queries using the OrmLite framework) and the OrmLite framework sends the query to the SQLite database who will send a response back to the DAO.
  1. Response to service: That same response is transfered back to the service.
  1. Response to activity: The service can perform actions/calculations on the data or just leave it as is and will pass it back to the activity.
  1. Change UI: The activity can then, based on the response of the service, change the UI (some elements or the entire screen).
  1. Update UI: The Android device will anticipate on what happens in the activity and update the screen of the device.
For steps 1, 4 and 7 we do not have to do anything. Steps 1 and 7 are handled entirly by the Android device, step 4 is handled by both the device and the OrmLite framework we use.
For the other steps it's quite strateforward. However what you have to keep in mind is that you can never:
  * Call a DAO from an acitivty! An activity will call a service which can call a DAO.
  * Call a service from another service! If a service needs data, it will always need to call a DAO. If that means that you will duplicate logic (performed on the result of the DAO) you will need to create some kind of utility and place the logic in there.
  * Call a dao from another dao! A DAO (Data Access Object) should only query data for exact only one entity! DAO's should not rely on eachother!
### Dependency Injection (Robo Guice) ###
[Google Guice](http://code.google.com/p/google-guice/) is a dependency framework to wire/inject all your services/dao's/...
[Robo Guice](http://code.google.com/p/roboguice) is build on top of that and takes the dependency injection to the Android platform. So with Robo Guice you can easily inject a dao in a service and a service in an activity. But that's not it. Robo Guice takes it just a level further. You can now also inject Android resources, views and extras in your activities. For more info on how to start an Android project with Robo Guice you will have to read their wiki. In this wiki document I will just explain some stuff that is needed to get going with this project.
#### DAO's ####
Important to know is that in the [eu.vranckaert.worktime.guice](http://code.google.com/p/worktime/source/browse/#svn%2Ftrunk%2Fandroid-app%2Fsrc%2Feu%2Fvranckaert%2Fworktime%2Fguice) package we have the [Module](http://code.google.com/p/worktime/source/browse/trunk/android-app/src/eu/vranckaert/worktime/guice/Module.java) class in which we will have to define all the DAO's to be used. In the <i>bindDaos</i> method you will have to bind a DAO-interface on a DAO-implementation.
#### Services ####
Now let's have a look at the [TimeRegistrationServiceImpl](http://code.google.com/p/worktime/source/browse/trunk/android-app/src/eu/vranckaert/worktime/service/impl/TimeRegistrationServiceImpl.java) class. In this class we want to do certain DAO/DB calls, so we need a reference to our, for example, TimeRegistrationDao class. So all we now have to do in this service class to have access to our DAO is:
```
@Inject
private TimeRegistrationDao dao;
```
That's one thing, but off course we want to use our services also in the activities, so we will also have to configure them. So if you ever want to inject a certain implementation somewhere, it has to be binded in the [Module](http://code.google.com/p/worktime/source/browse/trunk/android-app/src/eu/vranckaert/worktime/guice/Module.java) class. For services we have a seperate method <i>bindServices</i> in which we bind the interfaces to their implementations.
#### Activities ####
For activities we will never try to inject them somewhere else (would be bad, very bad design!) so they don't have to be listed in the [Module](http://code.google.com/p/worktime/source/browse/trunk/android-app/src/eu/vranckaert/worktime/guice/Module.java) class. But since we have all the services and dao's mapped in this class we can start injecting and using them in the activities. All you have to do is write this piece of code:
```
@Inject
private TimeRegistrationService timeRegistrationService;
```
##### Resources #####
However, as mentioned earlier, it is possible to also inject resources. If you need a certain drawable or string for example you can easily inject it as follows:
```
@InjectResource(R.drawable.some_drawable)
private Drawable someInjectedDrawable;
```
##### Views #####
If you want to use views that are defined in the layout file you use in this activity you can easily inject them as follows:
```
@InjectView(R.id.registration_button)
private Button registrationButton;
@InjectView(R.id.first_name)
private TextView firstName;
@InjectView(R.id.last_name)
private TextView lastName;
```
##### Extras #####
During navigation between activities if often happens that you want to transfer some data from one activity to another. So on activity A you do something like:
```
intent.putExtra("someData", someData);
```
In the second activity (activity B) you want to access and use that 'someData', so all you have to do is add the next annotation to your activity B:
```
@InjectExtra(value= "someDate")
private Object someDataFromExtras;
```
### Database Connectivity (ORM Lite) ###
## Testing ##