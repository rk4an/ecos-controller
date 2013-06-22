package com.ecos.train;

import android.app.Application;

/*@ReportsCrashes(formKey = "",
mailTo = "erkan2005+ecos@gmail.com",
customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },
logcatArguments = { "-t", "300", "-s", "ECOS" },
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.crash_toast_text)
*/
public class TrainManagerApp extends Application{

	public void onCreate(){
		//ACRA.init(this);
		super.onCreate();
	}
}
