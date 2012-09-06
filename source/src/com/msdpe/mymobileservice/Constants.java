// ---------------------------------------------------------------------------------- 
// Microsoft Developer & Platform Evangelism 
//  
// Copyright (c) Microsoft Corporation. All rights reserved. 
//  
// THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND,  
// EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES  
// OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE. 
// ---------------------------------------------------------------------------------- 
// The example companies, organizations, products, domain names, 
// e-mail addresses, logos, people, places, and events depicted 
// herein are fictitious.  No association with any real company, 
// organization, product, domain name, email address, logo, person, 
// places, or events is intended or should be inferred. 
// ----------------------------------------------------------------------------------

package com.msdpe.mymobileservice;

public class Constants {
	public static final String kGetTodosUrl = "https://yoursubdomain.azure-mobile.net/tables/TodoItem?$filter=(complete%20eq%20false)";
	public static final String kAddTodoUrl =  "https://yoursubdomain.azure-mobile.net/tables/TodoItem";
	public static final String kUpdateTodoUrl = "https://yoursubdomain.azure-mobile.net/tables/TodoItem/";
	public static final String kMobileServiceAppId = "yourappid";
}
