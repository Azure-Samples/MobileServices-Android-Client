# Mobile Services - The Android Client
This is an Android application which demonstrates how to connect to [Windows Azure Mobile Services](https://www.windowsazure.com/en-us/develop/mobile/).  The client has a dependency on setting up a Mobile Service in the Windows Azure portal.  The application allows users to view a list of todo items, mark them as complete, and add new ones.  This sample was built using Eclipse and the Android SDK.

Below you will find requirements and deployment instructions.

## Requirements
* Eclipse - This sample was built on Eclipse 3.7 though newer versions should work.  [Get Eclipse here](http://www.eclipse.org/downloads/).
* Android ADT - The ADT plugin for Eclipse was version 20 at build though newer versions should work.  [Get ADT here](http://developer.android.com/sdk/installing/installing-adt.html).
* Android SDK - The SDK was at version 20 at build and the app was compiled against API SDK version 15.  [Get the SDK here](http://developer.android.com/sdk/index.html).
* Windows Azure Account - Needed to create the Mobile Service.  [Sign up for a free trial](https://www.windowsazure.com/en-us/pricing/free-trial/).

## Additional Resources
Click the links below for more information on the technologies used in this sample.
* Blog Post - [Moblie Services and Android](http://chrisrisner.com/Windows-Azure-Mobile-Services-and-Android).

#Specifying your mobile service's subdomain and App ID
Once you've set up your Mobile Service in the Windows Azure portal, you will need to enter your site's subdomain into the source/src/com/msdpe/mymobilservice/Constants.java file.  Replace all of the your-subdomain with the subdomain of the site you set up.

	public static final String kGetTodosUrl = "https://yoursubdomain.azure-mobile.net/tables/TodoItem?$filter=(complete%20eq%20false)";
	public static final String kAddTodoUrl =  "https://yoursubdomain.azure-mobile.net/tables/TodoItem";
	public static final String kUpdateTodoUrl = "https://yoursubdomain.azure-mobile.net/tables/TodoItem/";

Finally, copy the APP ID from the portal into the constants.m file:

	public static final String kMobileServiceAppId = "yourappid";

## Contact

For additional questions or feedback, please contact the [team](mailto:chrisner@microsoft.com).