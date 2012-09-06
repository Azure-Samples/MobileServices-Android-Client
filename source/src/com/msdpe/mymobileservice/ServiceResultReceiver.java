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

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ServiceResultReceiver extends ResultReceiver {
	private Receiver mReceiver;
	 
    public ServiceResultReceiver(Handler handler) {
        super(handler);
    }
 
    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }
 
    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultBundle);
    }
 
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultBundle) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultBundle);
        }
    }
}