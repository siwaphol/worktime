# Project Setup Web #
## Table Of Contents ##

## Introduction ##
The WorkTime web project is a Java project hosted on Google AppEngine. The aim of this project is to bring a rich web application to our users and the possibility to sync the data on the web with the data on their devices (Android, iOS, ...) using RESTFUL webservices.
## Getting Started ##
### AppEngine SDK ###
For this project we use the 1.7 release of the AppEngine SDK. Specifically version 1.7.3.
This version can be downloaded over here: http://googleappengine.googlecode.com/files/appengine-java-sdk-1.7.3.zip
### Source Code ###
### IDE ###
The only IDE we reccomend you to use until now is Eclipse together with the Google plugins installed. More on how to install Eclipse and the correct plugins can be found on the Google AppEngine getting started page: https://developers.google.com/appengine/docs/java/gettingstarted/installing.
Once your Eclipse is installed with all the plugins you can import the project. Make sure you have somewhere check out the code before continuing.
  1. First thing to do is to import the project into Eclipse as a Java Project. On older versions of Eclipse you would want to follow this guide: https://developers.google.com/eclipse/docs/existingprojects. Although this does no longer work with the latest versions of Eclipse such as Indigo. On those versions you need to go to 'File' -> 'New' -> 'Other...' and search for 'Java Project'. Select 'Java Project' and continue. On the next screen there is a checkbox 'Use default location'. Uncheck that one and navigate to the source code (wherever it's located, **just make sure the source is not in the same directory as your workspace directory**, otherwise Eclipse will complain that the project already exists).
  1. Now that you have your project imported, right click it 'Google' -> 'AppEngine Settings'. In the dialog that opens you should check a few things:
    1. In the 'App Engine SDK' section make sure to check the checkbox 'Use Google App Engine' and specify the SDK to be used. Make sure it points to the SDK that is mentioned in the previous steps.
    1. In the 'Datastore' section make sure that 'Use Datanucleus JDO/JPA to access the datastore' is checked and to select the version to be used: 'v2'
    1. Also in the 'Datastore' section make sure the checkbox 'Enable local HRD support' is checked. In case you cannot change the value of the checkbox you should open a file navigator window and go the root folder of the project and edit the '.settings/com.google.appengine.eclipse.core.prefs' file by adding 'gaeHrdEnabled=true' at the end of the file on a new line.
That is basically it. But there might be some extra steps to perform in order to get it all up and running. Check out these bullet points and see what you need:
  * Eclipse might give you an error like 'The output directory for the project should be set to /NAME\_OF\_YOUR\_PROJECT/war/WEB-INF/classes'. If this is the case, right click the error and choose the 'Quick Fix' option. In the dialog that opens select the option 'Set output directory to `<WAR>`/WEB-INF/classes' and press finish.
  * It's possible that you will see error messages saying that certain libraries are missing in your project. Right click one of those messages and choose to 'Quick Fix' it. In the 'Quick Fix' dialog choose the option 'Synchronize `<`WAR`>`/WEB-INF/lib with SDK libraries'. This will add some libraries to your lib-directory. **Make sure that you NEVER EVERY commit those JAR files!**<br /> While you should not commit the extra JAR files it's even better to add them to the ignore list of your SVN client. And while you are doing this make sure you also add .classpath and .project files that Eclipse generated, and also .settings directory. **Just as in the previous step you should NEVER EVER commit these files also, so prevent!**
### Local Development Server ###
When running the project from Eclipse a local development server will be launched for you with your application deployed. Most of the features that are available on the real AppEngine servers is available also on the development server.
But here are some things you have to be careful with because they are not supported:
  * HTTPS: HTTPS is not supported. All pages that are configured to use HTTPS on AppEngine will locally be accessible over plain HTTP.
  * JavaMail: This will not work at all locally, although it will not throw an exception either. It just doesn't work offline!
As you do not have an admin console as for the real AppEngine servers you can still access some features locally (for example to browse through your datastore). This is all available at the local URL: http://localhost:8888/_ah/admin/
## Architecture ##
The application is created on top of the Google AppEngine SDK version 1.7.3. On top of that we used some extra libraries to ease the development:
  * Jersey Webservices (JSON or XML)
    * jersey-client-1.17.jar
    * jersey-core-1.17.jar
    * jersey-json-1.17.jar
    * jersey-server-1.17.jar
    * jersey-servlet-1.17.jar
    * jersey-guice-1.17.1.jar
    * jackson-core-asl-1.9.2.jar
    * jackson-jaxrs-1.9.2.jar
    * jackson-mapper-asl-1.9.2.jar
    * jackson-xc-1.9.2.jar
    * jettison-1.1.jar
    * jsr311-api-1.1.1.jar
    * asm-3.1.jar
  * JDO datastore access
    * twig-persist-2.0-rc.jar
    * guava-13.0.jar
  * Dependency Injection
    * jersey-guice-1.16.jar
    * aopalliance.jar
    * guice-3.0.jar
    * guice-serlvet-3.0.jar
  * Sitebricks
    * annotations-7.0.3.jar
    * async-http-client-1.6.3.jar
    * guava-13.0.jar
    * guice-multibindings-3.0.jar
    * jcip-annotations-1.0.jar
    * jsoup-1.5.2.jar
    * mvel2-2.1.3.jar
    * netty-3.2.4.Final.jar
    * sitebricks-0.8.8.jar
    * sitebricks-annotations-0.8.8.jar
    * sitebricks-client-0.8.8.jar
    * sitebricks-converter-0.8.8.jar
    * slf4j-api-1.6.1.jar
    * xpp3\_min-1.1.4c.jar
    * xstream-1.3.1.jar
  * Templating
    * sitemesh-2.4.2.jar
  * Others
    * commons-lang3-3.1.jar (for support for methods like StringUtils.isBlank(..))
    * commons-validator-1.4.0.jar (for all sorts of validation like email etc)
### Classpath ###
Because of the usage of extra libraries you need to customize the classpath to be used in Eclipse.
One important thing that you can see in the list above is that we depend on asm version 3.1, and that is a problem. Because Google AppEngine depends on asm version 4 we need to remove that one from the lib-directory and place asm 3.1 in there. Also from the classpath make sure that asm 4 is removed and that you have added asm 3.1 yourself.
Also make sure that all other libraries listed above are available on the classpath or the project will not compile.
### Database Connectivity (twig-persist) ###
For storing data on Google AppEngine we use the default datastore. We use this because it's lightweight, default supported and very fast. In order to easily access the database we use DAO's (explained later). But those DAO's use the twig-persist framework (version 1.0.4).
We create a StaticDataStore class from which a DataStore object can be retrieved from within all layers. This is important to be able to use transactions.
This small piece of code allows you to use transactions in any layer:
```
Transaction tx = StaticDataStore.getTransaction();
try {
    // Do some datastore calls (direct or using DAO's...)
} finally {
    if (tx.isActive()) {
        tx.rollback();
    }
}
```
### Layers ###
The entire application is split in different layers so that every layer has it's own responsibilities. This is what is called separation of concerns.
#### Dependency Injection (Google Guice) ####
In order to wire all those layers together we are using Google Guice.
In the class GuiceModule all the interfaces and implementations are mapped. The GuiceConfig instantiates the GuiceModule and creates a new ServletModule for the urlPattern "/rest/`*`".
This enables you inject any service or DAO in any class.
You can simply inject the UserService for example:
```
@Inject
private UserService userService;
```
#### DAO's ####
The Data Access Object layer is being used for access to the database (or in this case the datastore). That is it's soul responsibility. Each DAO will be responsible for the database interactions of exactly one model-object. If, for example, we have a model object User that has multiple Roles, then the UserDAO can be responsible for creating a new user and retrieving a user with all of it's roles. But the UserDAO cannot be responsible for creating (adding) a new role in the datastore. That would be the responsibility of the RoleDAO.
Every DAO that is added to the application should have an interface in which all the methods are declared, and implementation that can execute the methods. But there are three more rules:
  1. The interface needs to extend the BaseDao interface that you have to type to the model-object for which this DAO is intended.
  1. The implementation does not only implement the interface but also extends the BaseDaoImpl class that contains (and exposes) some of the basic methods for your DAO like persist(..), remove(..), findAll(),...
  1. The implementation also needs a default (read: no-arg) constructor that does a super-call with a Class parameter being the class of the model-object for which this DAO is intended.
#### Services ####
The service layer is responsible for executing code that represents a business case. For example to login a user, to register a user, to create a new time registration, to delete one,....
Every service needs to be mapped on a group of business cases. That can be the same as the model but doesn't necessarily needs to. For example we have model class User an a UserService that will be able to login, register, logout,... a user. But it's perfectly possible to have service class like LiveOrDieService and model objects like Person, Clouset, House,... The service can then specify cross-model methods like openClouse(), goToBed(), enterHouse(), leaveHouse(),...
Each service-method can have zero or more DAO-calls to zero or more DAO's. That's very important because we are not tied to strictly work on one model object in a service. We can do all kinds of stuf on different objects.
Each service needs a default (no-arg) constructor. Services that do not use dependency injection need to create all DAO's to be used in the service (and other stuff that otherwise would be injected automatically) in this constructor. But it's recommended to use dependency injection as described above!
#### JSON EndPoints ####
The JSON endpoint classes are classes that will be used by other applications in order to interact with the WorkTime Web application. Which services that are available exactly, and what each of them consumes or produces is explained in the next chapter that is also called (surprise!) 'JSON EndPoints'. Important to mention here is that whenever a JSON Endpoint is reached, it will do some security checks (if you are logged in, if the service that is calling this endpoint is allowed, if you have the correct rights,...), then it will (if needed) transform the incoming data (can be simple String but can JSON as well) to something that the service-layer can work with. Then the service layer is called and the result of the service layer will again be transformed to something that can be sent back (can be again be simple string or JSON or an HTML response).
All JSON endpoints are configured to be accessible over HTTPS and in the rest-directory: https://worktime-web.appspot.com/rest/. What the exact path after the rest-part is can be configured per endpoint using the @Path annotation.
Each webservice-method (method in an endpoint) needs to be annoted with the @POST or @GET annotation and also requires a @Path annotation. The @Consumes should also be specified everytime (except off course if there is no input) and the @Produces is set to specify the return type of the webservice.
All webservices are secured with a service-id. If you want to access one of our webservices you need to request such a service-id first. If you receive a service-id you will be able execute requests. There's not a single webservice that doesn't check for a valid service-id.
### JSON EndPoints ###
As explained before the endpoints will only be accessible over HTTP.
Each endpoint also needs to specify it's specific path using the @Path annotation which needs te be on class level. Below is a list of all webservice-methods that are available with their URL, the required input and the output.
Whenever consuming a webservice that returns JSON it will contain 'exception' fields a boolean resultOk. You should first check the resultOk boolean. If the boolean is false there is something wrong and you should check each exception-object to be null or not. If something is wrong only one of the exception objects will always be set. If the boolean resultOk is true then you can continue with the result or if there are no extra result-parameters you can simply inform the user the action succeeded.
#### Users ####
##### Register #####
  * HTTP-Method: POST
  * URL: https://worktime-web.appspot.com/rest/user/register
  * Input-type: JSON
  * Input parameters:
    * serviceKey : String
    * email : String
    * password : String
    * lastName : String
    * firstName : String
  * Response-type: JSON
  * Response parameters:
    * serviceNotAllowedException : Object
    * userNotLoggedInException : Object
    * userIncorrectRoleException : Object
    * resultOk : Boolean
    * fieldRequiredJSONException : Object
    * registerEmailAlreadyInUseJSONException : Object
    * passwordLengthInvalidJSONException : Object
    * invalidEmailJSONException : Object
    * sessionKey : String
##### Login #####
  * HTTP-Method: POST
  * URL: https://worktime-web.appspot.com/rest/user/login
  * Input-type: JSON
  * Input parameters:
    * serviceKey : String
    * email : String
    * password : String
  * Response-type: JSON
  * Response parameters:
    * serviceNotAllowedException : Object
    * userNotLoggedInException : Object
    * userIncorrectRoleException : Object
    * resultOk : Boolean
    * emailOrPasswordIncorrectJSONException : Object
    * sessionKey : String
##### Logout #####
  * HTTP-Method: GET
  * URL: https://worktime-web.appspot.com/rest/user/logout
  * Input-type: URL Query Parameters
    * serviceKey : String
    * email : String
    * sessionKey : String
  * Response-type: HTTP Response
  * Response status codes:
    * 405 : provided services-id has no access
    * 400 : email or sessionKey not provided
    * 200 : logout ok (also if the provided email and sessionKey are not found or do not match)
##### Profile #####
  * HTTP-Method: GET
  * URL: https://worktime-web.appspot.com/rest/user/profile
  * Input-type: URL Query Parameters
    * serviceKey : String
    * email : String
    * sessionKey : String
  * Response-type: JSON
  * Response parameters:
    * serviceNotAllowedException : Object
    * userNotLoggedInException : Object
    * userIncorrectRoleException : Object
    * resultOk : Boolean
    * firstName : String
    * lastName : String
    * email : String
    * registeredSince : Date
    * loggedInSince : Date
    * role : Enum (USER, ADMIN)
##### Change Password #####
  * HTTP-Method: POST
  * URL: https://worktime-web.appspot.com/rest/user/changePassword
  * Input-type: JSON
  * Input parameters:
    * serviceKey : String
    * email : String
    * sessionKey : String
    * oldPassword : String
    * newPassword : String
  * Response-type: JSON
  * Response parameters:
    * serviceNotAllowedException : Object
    * userNotLoggedInException : Object
    * userIncorrectRoleException : Object
    * resultOk : Boolean
    * emailOrPasswordIncorrectJSONException : Object
    * sessionKey : String
##### Reset Password Request #####
  * HTTP-Method: GET
  * URL: https://worktime-web.appspot.com/rest/user/resetPasswordRequest
  * Input-type: URL Query Parameters
    * serviceKey : String
    * email : String
  * Response-type: HTTP Response
  * Response status codes:
    * 405 : provided services-id has no access
    * 200 : all other cases, also if something is wrong because email is not found or so...
##### Reset Password #####
  * HTTP-Method: POST
  * URL: https://worktime-web.appspot.com/rest/user/resetPassword
  * Input-type: JSON
  * Input parameters:
    * serviceKey : String
    * passwordResetKey : String
    * newPassword : String
  * Response-type: JSON
  * Response parameters:
    * serviceNotAllowedException : Object
    * fieldRequiredJSONException : Object
    * passwordLengthInvalidJSONException : Object
    * invalidPasswordResetKeyJSONException : Object
    * passwordResetKeyAlreadyUsedJSONException : Object
    * passwordResetKeyExpiredJSONException : Object
    * resultOk : Boolean
##### Change Permissions #####
  * **ADMIN ROLE ONLY**
  * HTTP-Method: POST
  * URL: https://worktime-web.appspot.com/rest/user/changePermissions
  * Input-type: JSON
  * Input parameters:
    * serviceKey : String
    * email : String
    * sessionKey : String
    * userToChange : String
    * newRole : Enum (USER, ADMIN)
  * Response-type: JSON
  * Response parameters:
    * serviceNotAllowedException : Object
    * userNotLoggedInException : Object
    * userIncorrectRoleException : Object
    * resultOk : Boolean
    * userNotFoundJSONException : Object
#### Application Services ####
##### Create Service #####
  * **ADMIN ROLE ONLY**
  * HTTP-Method: POST
  * URL: https://worktime-web.appspot.com/rest/user/createService
  * Input-type: JSON
  * Input parameters:
    * serviceKey : String
    * appName : String
    * platform : Enum (ANDROID, IOS, WEB)
    * contact : String
  * Response-type: JSON
  * Response parameters:
    * serviceNotAllowedException : Object
    * userNotLoggedInException : Object
    * userIncorrectRoleException : Object
    * resultOk : Boolean
    * serviceKey : String
##### Remove Service #####
  * **ADMIN ROLE ONLY**
  * HTTP-Method: POST
  * URL: https://worktime-web.appspot.com/rest/user/removeService
  * Input-type: JSON
  * Input parameters:
    * serviceKey : String
    * serviceKeyForRemoval : String
  * Response-type: JSON
  * Response parameters:
    * serviceNotAllowedException : Object
    * userNotLoggedInException : Object
    * userIncorrectRoleException : Object
    * resultOk : Boolean
    * serviceRemovesItselfJSONException : Object (a service cannot be removed if it's used in the request as serviceKey)
#### Synchronization ####
##### Sync All Time Registrations #####
  * HTTP-Method: POST
  * URL: https://worktime-web.appspot.com/rest/sync/all
  * Input-type: JSON
  * Input parameters:
    * serviceKey : String
    * email : String
    * sessionKey : String
    * lastSuccessfulSyncDate : Date (optional)
    * conflictConfiguration : Enum (SERVER or CLIENT)
    * projects : List of Project objects (projects that are not linked to any time registration)
    * tasks : List of Task objects (tasks that are not linked to any time registration)
    * timeRegistrations : List of TimeRegistration objects
  * Response-type: JSON
  * Response parameters:
    * serviceNotAllowedException : Object
    * userNotLoggedInException : Object
    * userIncorrectRoleException : Object
    * resultOk : Boolean
    * syncronisationFailedJSONException : Object
    * synchronisationLockedJSONException : Object
    * corruptDataJSONException : Object
    * syncResult : Object
    * projectsSinceLastSync : List of Project objects
    * tasksSinceLastSync : List of Task objects
    * timeRegistrationsSinceLastSync : List of TimeRegistration objects
## Testing ##
**TBD**