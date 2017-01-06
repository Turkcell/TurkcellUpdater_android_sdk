TurkcellUpdater Android SDK
===========================

This library provides mechanism for updating Android applications and displaying messages configured at a configuration file.


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>

<h2>Gradle</h2>
Add it in your root build.gradle at the end of repositories:
<pre>
<code>
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
</code>
</pre>
Add below dependency to your app's build.gradle file.
<pre>
<code>
dependencies {
	compile 'com.github.Turkcell:TurkcellUpdater_android_sdk:1.2.0'
}
</code>
</pre>
<h2>Configuration Guide</h2>
This documents describes usage and structure configuration files used by Turkcell Updater library.
<h3>Usage</h3>

<h4>Updating an Android application to a new version served on Google Play</h4>
<pre>
<code>
{
	"packageName": "com.example.app1",
	"updates": [
		{
			 "descriptions": {
				 "*": {
					 "message": "New version available"
				 }
			 },
			 "targetVersionCode": 1,
			 "targetGooglePlay": true
		}
	]
}
</code>
</pre>

<h4>Updating an Android application to a new version served as APK file</h4>
<pre>
<code>
{
	"packageName": "com.example.app1",
	"updates": [
		{
			 "descriptions": {
				 "*": {
					 "message": "New version available"
				 }
			 },
			 "targetVersionCode": 1,
			 "targetPackageUrl": "http://example.com/app1-v10.apk"
		}
	]
}
</code>
</pre>

<h4>Updating an Android application depending on where current application is installed from</h4>
<pre>
<code>
{
	"packageName": "com.example.app1",
	"updates": [
		{
			"filters": {
				"appInstallerPackageName": "com.android.vending"
			},
			"descriptions": {
				"*": {
					"message": "New version available"
				}
			},
			"targetVersionCode": 1,
			"targetGooglePlay": true
		},
		{
			"filters": {
				"appInstallerPackageName": "!com.android.vending"
			},
			 "descriptions": {
				 "*": {
					 "message": "New version available"
				 }
			 },
			 "targetVersionCode": 1,
			 "targetPackageUrl": "http://example.com/app1-v10.apk"
		}
	]
}
</code>
</pre>

<h4>Forcing an Android application to update</h4>
<pre>
<code>
{
	"packageName": "com.example.app1",
	"updates": [
		{
			 "descriptions": {
				 "*": {
					 "message": "New version available"
				 }
			 },
			 "targetVersionCode": "1",
			 "targetGooglePlay": true,
			 "forceUpdate": true
		}
	]
}
</code>
</pre>

<h4>Forcing an Android application to quit</h4>
<pre>
<code>
{
	"packageName": "com.example.app1",
	"updates": [
		{
			 "descriptions": {
				 "*": {
					 "message": "New version available"
				 }
			 },
			 "forceExit": true
		}
	]
}
</code>
</pre>

<h4>End of support for older Android OS versions</h4>
<pre>
<code>
{
	"packageName": "com.example.app1",
	"updates": [
		{
			"filters": {
				"deviceApiLevel": "&gt;=8"
			},
			"descriptions": {
				 "*": {
					 "message": "New version available"
				 }
			},
			"targetVersionCode": "5",
			"targetGooglePlay": true,
			"forceUpdate": true
		},
		{
			"filters": {
				"deviceApiLevel": "&lt;8"
			},

			 "descriptions": {
				 "*": {
					 "message": "Android version earlier than Android 2.2 are not supported."
				 }
			 },
			 "forceExit": true
		}
	]
}
</code>
</pre>

<h4>Transferring users to another Android application replacing old one</h4>
<pre>
<code>
{
	"packageName": "com.example.app1",
	"updates": [
		{
			"descriptions": {
				 "*": {
					 "message": "New version available"
				 }
			},
			"targetPackageName": "com.example.newapp",
			"targetVersionCode": "5",
			"targetGooglePlay": true,
			"forceUpdate": true
		}
	]
}
</code>
</pre>

<h4>Adding "What is new in this version" message </h4>
<pre>
<code>
{  
   "packageName":"com.example.app1",
   "messages":[  
      {  
         "filters":{  
            "appVersionCode":"1.1.0.0",
            "deviceOsName":"Android"
         },
         "descriptions":{  
            "*":{  
               "title":"What's New in this Version",
               "message":"We made our app even cooler with some incredible bugfixes."
            }
         },
         "maxDisplayCount":1,
         "targetWebsiteUrl":"http://mysite.com?q=learn+more"
      }
   ]
}
</code>
</pre>

<h3>Reference</h3>
Different configuration files are stored per application using Turkcell Updater Library.<br>
Configuration files are UTF-8 encoded JSON documents and should be served with <code>"application/json"</code> content type.
Since configurations may contain vulnerable information like URL of update package they should be only accessible via HTTPS.<br>
Root element of files should conform to <a href="#configurationRoot">Configuration Root</a> specifications below.<br/>
Documents may contain additional keys but Updater library ignores any other key that is not referred in this document.


<!-- configurationRoot -->
<div id="configurationRoot" class="entry">
<h4>Configuration Root</h4>
Root object that contains all data needed by Updater Library to show update messages and other notifications to user.
<h5>Structure:</h5>
Type: Object

<table>
<tr>
<th>Property name</th>
<th>Type</th>
<th>Default value</th>
<th>Platforms</th>
<th>Description</th>
<th>Required</th>
<th>Since</th>
</tr>

<tr>
<td>packageName</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>Platform specific unique identifier of application which configuration is created for. Package name for Android applications or Bundle id for iOS applications.</td>
<td>Yes</td>
<td>1</td>
</tr>

<tr>
<td>updates</td>
<td>Array</td>
<td>null</td>
<td>All</td>
<td>List of update entries with 0 or more elements. See <a href="#updateEntry">Update Entry</a></td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>messages</td>
<td>Array</td>
<td>null</td>
<td>All</td>
<td>List of messages with 0 or more elements. See <a href="#messageEntry">Message Entry</a></td>
<td>No</td>
<td>2</td>
</tr>

</table>

<h5>Example:</h5>
<h5>Android</h5>
<pre>
<code>
{
	"packageName": "com.example.app1",
	"updates": [
		{
			 "descriptions": {
				 "*": {
					 "message": "New version available",
					 "whatIsNew": "Minor bug fixes",
					 "warnings": "New version requires additional privileges"
				 }
			 },
			 "targetVersionCode": 10,
			 "targetPackageUrl": "http://example.com/app1-v10.apk",
			 "forceUpdate": false
		}
	],
	"messages": [
		{
			"descriptions": {
				"*": {
					"title": "Offer",
					"message": "New application is available!",
					"imageUrl": "http://example.com/app2-icon.png"
				}
			},
			"targetPackageName": "com.example.app2",
			"targetGooglePlay": true,
			"maxDisplayCount": 3
		}
	]
}
</code>
</pre>

</div>

<!-- updateEntry -->
<div id="updateEntry">
<h4>Update Entry</h4>
Provides information about how update should be installed and when update should be applied.
<h5>Structure:</h5>
Type: Object

<table>
<tr>
<th>Property name</th>
<th>Type</th>
<th>Default value</th>
<th>Platforms</th>
<th>Description</th>
<th>Required</th>
<th>Since</th>
</tr>

<tr>
<td>filters</td>
<td>Array</td>
<td>null</td>
<td>All</td>
<td>See <a href="#filterEntry">Filter Entry</a></td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>descriptions</td>
<td>Object</td>
<td>null</td>
<td>All</td>
<td>Map of update description entries. Keys (property names) are two letter language codes (see: <a href="http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">ISO 639-1</a>) and values are <a href="#updateDescriptionEntry">Update Description Entries</a>.
If empty strings (&quot;&quot;) or asterisk(&quot;*&quot;) is used as key, it matches with any language.<br><br>
For iOS : If device language is English but the application language is Turkish asterisk(&quot;*&quot;)language code is suggested for displaying Turkish descriptions.</td>
<td>Yes</td>
<td>1</td>
</tr>

<tr>
<td>targetVersionCode</td>
<td>Number</td>
<td>-1</td>
<td>Android</td>
<td>Version code of new version. See <a href="#updateEntryNote1">Note #1</a></td>
<td>Yes for Android application, unless <code>forceExit</code> is <code>true</code></td>
<td>1</td>
</tr>

<tr>
<td>targetGooglePlay</td>
<td>Boolean</td>
<td>false</td>
<td>Android</td>
<td><code>true</code> New version should be installed from Google Play.</td>
<td>See <a href="#updateEntryNote2">Note #2</a></td>
<td>1</td>
</tr>

<tr>
<td>targetPackageName</td>
<td>String</td>
<td>Current application's packageName</td>
<td>Android</td>
<td>Package name of new version.</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>targetPackageUrl</td>
<td>String</td>
<td>null</td>
<td>Android</td>
<td>URL of APK package of new version.</td>
<td>See <a href="#updateEntryNote2">Note #2</a></td>
<td>1</td>
</tr>

<tr>
<td>forceUpdate</td>
<td>Boolean</td>
<td>false</td>
<td>All</td>
<td><code>true</code> if user should not skip this update and continue to use application. When <code>true</code> "Exit application" option will be displayed to user instead of "Remind me later" option.</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>forceExit</td>
<td>Boolean</td>
<td>false</td>
<td>All</td>
<td><code>true</code> if user should not have any option other than exiting application. When <code>true</code> only "Exit application" option will be displayed to user.</td>
<td>No</td>
<td>2</td>
</tr>

<tr>
<td>targetWebsiteUrl</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>For Android : URL of web page that contains new version.<br><br>
For iOS : iTunes or Corporate Repository URL of the application.</td>
<td>See <a href="#updateEntryNote2">Note #2</a></td>
<td>1</td>
</tr></table>

<h5>Notes:</h5>
<ol>
<li id="updateEntryNote1">Version entries will be omitted if <code>targetPackageName</code> is same with current applications package name and <code>targetVersionCode</code> is same with current applications version code. This check is performed in order avoid updates to existing version.</li>
<li id="updateEntryNote2">On Android, any update entry should meet at least one of following conditions. Otherwise it will be omitted.
	<ul>
	<li><code>forceExit</code> is <code>true</code></li>
	<li><code>targetGooglePlay</code> is <code>true</code></li>
	<li><code>targetWebsiteUrl</code> is not <code>null</code> or empty</li>
	<li><code>targetPackageUrl</code> is not <code>null</code> or empty</li>
	</ul>
</li>
</ol>
<h5>Example:</h5>
<pre>
<code>
{
	"filters": {
		"appVersionCode": "&lt;10",
		"deviceOsName": "Android"
	},
	"descriptions": {
		"tr": {
			"message": "Uygulamanın yeni sürümü yayınlandı.",
			"whatIsNew": "Hata düzeltildi",
			"warnings": "Yeni sürüm ek yetkiler gerektirir"
		},
		"*": {
			"message": "New version available",
			"whatIsNew": "Minor bug fixes",
			"warnings": "New version requires additional privileges"
		}
	},
	"targetVersionCode": 10,
	"targetGooglePlay": true
}
</code>
</pre>

</div>



<!-- messageEntry -->
<div id="messageEntry" class="entry">
<h4>Message Entry</h4>
Defines a message to display to user when message should be displayed.
<h5>Structure:</h5>
Type: Object

<table>
<tr>
<th>Property name</th>
<th>Type</th>
<th>Default value</th>
<th>Platforms</th>
<th>Description</th>
<th>Required</th>
<th>Since</th>
</tr>

<tr>
<td>filters</td>
<td>Array</td>
<td>null</td>
<td>All</td>
<td>See <a href="#filterEntry">Filter Entry</a></td>
<td>No</td>
<td>2</td>
</tr>

<tr>
<td>descriptions</td>
<td>Object</td>
<td>null</td>
<td>All</td>
<td>Map of message description entries. Keys (property names) are two letter language codes (see: <a href="http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">ISO 639-1</a>) and values are <a href="#messageDescriptionEntry">Message Description Entries</a>.
If empty strings (&quot;&quot;) or asterisk(&quot;*&quot;) is used as key, it matches with any language.</td>
<td>Yes</td>
<td>2</td>
</tr>

<tr>
<td>id</td>
<td>Number</td>
<td>For Android : Auto generated value using <code>targetGooglePlay</code>, <code>targetPackageName</code>, <code>targetWebsiteUrl</code> and <code>descriptions</code> properties.<br>
<br>For iOS : Auto generated value using <code>targetWebsiteUrl</code> and <code>descriptions</code> properties.</td>
<td>All</td>
<td>Unique ID of message. ID is used when determining last display date and total display count of message.</td>
<td>No</td>
<td>2</td>
</tr>

<tr>
<td>targetGooglePlay</td>
<td>Boolean</td>
<td>false</td>
<td>Android</td>
<td><code>true</code> if message offers references to an application in Google Play. If it is <code>true</code>, <code>targetPackageName</code> should not be null or empty.</td>
<td>No</td>
<td>2</td>
</tr>

<tr>
<td>targetPackageName</td>
<td>String</td>
<td>null</td>
<td>Android</td>
<td>Package name of offered application. If specified one of <code>targetGooglePlay</code> or <code>targetWebsiteUrl</code> properties should be set.</td>
<td>No</td>
<td>2</td>
</tr>

<tr>
<td>targetWebsiteUrl</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>URL of web page that contains offered application.</td>
<td>No</td>
<td>2</td>
</tr>

<tr>
<td>maxDisplayCount</td>
<td>Number</td>
<td>2147483647</td>
<td>All</td>
<td>Maximum display count of message</td>
<td>No</td>
<td>2</td>
</tr>

<tr>
<td>displayBeforeDate</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>If not null, message should not be displayed after this date. For date format details see <a href="#messageEntryNote1">Note #1</a></td>
<td>No</td>
<td>2</td>
</tr>

<tr>
<td>displayAfterDate</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>If not null, message should not be displayed before this date. For date format details see <a href="#messageEntryNote1">Note #1</a></td>
<td>No</td>
<td>2</td>
</tr>

<tr>
<td>displayPeriodInHours</td>
<td>Number</td>
<td>0</td>
<td>All</td>
<td>Minimum duration in hours that should pass before displaying this message again</td>
<td>No</td>
<td>2</td>
</tr>

</table>

<h5>Notes:</h5>
<ol>
<li id="messageEntryNote1"> Following date formats from <a href="http://en.wikipedia.org/wiki/ISO_8601">ISO 8601</a> are supported:
	<ul>
	<li>&quot;yyyy-MM-dd&quot; example: &quot;1969-12-31&quot; &quot;1970-01-01&quot;</li>
	<li>&quot;yyyy-MM-dd HH:mm&quot; example: &quot;1969-12-31 16:00&quot;, &quot;1970-01-01 00:00&quot;</li>
	</ul>
</ol>

<h5>Example:</h5>
<h5>Android</h5>
<pre>
<code>
{
	&quot;filters&quot;: {
		&quot;deviceIsTablet&quot;: &quot;true&quot;
	},
	&quot;descriptions&quot;: {
		&quot;*&quot;: {
			&quot;title&quot;: &quot;Offer&quot;,
			&quot;message&quot;: &quot;New application for your tablet is avaliable!&quot;,
			&quot;imageUrl&quot;: &quot;http://example.com/app2-icon.png&quot;
		}
	},
	&quot;displayAfterDate&quot;: &quot;2013-01-01&quot;,
	&quot;displayBeforeDate&quot;: &quot;2013-06-01&quot;,
	&quot;targetPackageName&quot;: &quot;com.example.app2&quot;,
	&quot;targetGooglePlay&quot;: true,
	&quot;maxDisplayCount&quot;: 10,
	&quot;displayPeriodInHours&quot;: 240
}
</code>
</pre>

</div>

<!-- filterEntry -->
<div id="filterEntry" class="entry">
<h4>Filter Entry</h4>
<a href="#messageEntry">Message Entries</a> and <a href="#updateEntry">Update Entries</a> are applied only
if filter matches with device properties. If "filter" property of a <a href="#messageEntry">Message Entry</a> and a <a href="#updateEntry">Update Entry</a>
is omitted or defined as null no filtering will be applied.<br>
Filter entries consist of key and value pairs.
Keys are property names and values are filtering rules.
<br>

Filtering rules format applies to all values of filter entry:<br>
<ul>
<li>Rules are sequences of rule parts joined with ","</li>
<li>Both rule parts and values are converted to lower case and trimmed before
comparison</li>
<li>Order of rule parts doesn't change the result, example: "!b,a" is same with "a,!b"</li>
<li><code>"*"</code>, <code>null</code> or empty string matches with any value including
<code>null</code></li>
<li><code>"''"</code> matches with <code>null</code> or empty string</li>
<li><code>"!''"</code> matches with any value except <code>null</code> or empty string</li>
<li><code>"![rule part]"</code> excludes any value matches with [rule]</li>
<li><code>"[value]"</code> matches with any value equals to [value]</li>
<li><code>"[prefix]*"</code> matches with any value starting with [prefix]</li>
<li><code>"*[suffix]"</code> matches with any value ending with [suffix]</li>
<li><code>"[prefix]*[suffix]"</code> matches with any value starting with [prefix] and
ending with [suffix]</li>
<li><code>"&gt;[integer]"</code> matches with any value greater than [integer]</li>
<li><code>"&gt;=[integer]"</code> matches with any value greater than or equals to [integer]</li>
<li><code>"&lt;[integer]"</code> matches with any value lesser than [integer]</li>
<li><code>"&lt;=[integer]"</code> matches with any value lesser than or equals to [integer]</li>
<li><code>"&lt;&gt;[integer]"</code> matches with any value not equals to [integer]</li>
</ul>

<h5>Structure:</h5>
Type: Object

<table>
<tr>
<th>Property name</th>
<th>Type</th>
<th>Default value</th>
<th>Platforms</th>
<th>Description</th>
<th>Required</th>
<th>Since</th>
</tr>

<tr>
<td>appPackageName</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>
Filter rule for package name of application.<br>
Example value: "com.sample.app".
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>appVersionName</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>
Version name of application typically in Major.Minor.Revision or Major.Minor format.<br>
Example value: "1.0.0"
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>appVersionCode</td>
<td>String</td>
<td>null</td>
<td>Android</td>
<td>
An integer version code of application which is defined in AndroidManifest.xml.<br>
Example value: "10"
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>appInstallerPackageName</td>
<td>String</td>
<td>null</td>
<td>Android</td>
<td>
Package name of application installed current application. Maybe <code>null</code> if not specified.<br>
Example value: "com.android.vending" of Google play.
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>deviceApiLevel</td>
<td>String</td>
<td>null</td>
<td>Android</td>
<td>
Version code of Android OS. See <a href="http://developer.android.com/reference/android/os/Build.VERSION_CODES.html">Android documentation</a>.<br>
Example value: "10" for Android 2.3.3.
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>deviceOsName</td>
<td>String</td>
<td>null</td>
<td>Android</td>
<td>
Name of operating system of device.<br>
Values: "android", "ios", "windowsphone".
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>deviceOsVersion</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>
Version name of operating system of device.<br>
Example value: "2.3.3".
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>deviceBrand</td>
<td>String</td>
<td>null</td>
<td>Android</td>
<td>
Brand name of device.<br>
Example value: "htc_europe" for HTC Wildfire S.
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>deviceModel</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>For Android : Model name of device.<br>
Example value: "HTC Wildfire S A510e" for HTC Wildfire S.<br><br>
For iOS :<ul>
		<li>"iPod1,1"   for iPod Touch</li>
		<li>"iPod2,1"   for iPod Touch Second Generation</li>
		<li>"iPod3,1"   for iPod Touch Third Generation</li>
		<li>"iPod4,1"   for iPod Touch Fourth Generation</li>
		<li>"iPhone1,1" for iPhone</li>
		<li>"iPhone1,2" for iPhone 3G</li>
		<li>"iPhone2,1" for iPhone 3GS</li>
		<li>"iPad1,1"   for iPad</li>
		<li>"iPad2,1"   fpr iPad 2</li>
		<li>"iPad3,1"   for iPad 3 (aka new iPad)</li>
		<li>"iPhone3,1" for iPhone 4</li>
		<li>"iPhone4,1" for iPhone 4S</li>
		<li>"iPhone5,1" for iPhone 5</li>
		</ul>
If there is a need for giving multiple device models as a value <code>asterik(*)</code> must be used instead of <code>comma(,)</code>,
because <code>comma(,)</code> is a reserved notation for joining rules.<br>
Example	value: "iPhone2*,iPad1*,iPhone5*"
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>deviceProduct</td>
<td>String</td>
<td>null</td>
<td>Android</td>
<td>
Product name of the device.<br>
Example value: "htc_marvel" for HTC Wildfire S.
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>deviceIsTablet</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>
"true" if devices is a tablet, otherwise "false".<br><br>
For Android : Since there is no clear evidence to determine if an Android device
is tablet or not, devices with minimum screen size wider than 600 dpi are considered as tablets.<br>
Example values: "true", "false".
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>deviceLanguage</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>
Two letter language code of device
(see: <a href="http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">ISO 639-1</a>).<br>
Example values: "en", "tr", "fr".
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>x-&lt;Application defined key&gt;</td>
<td>String</td>
<td>null</td>
<td>Android</td>
<td>
"x-" is prefix for application defined keys of arbitrary properties.<br>
Applications may define and add own custom property key-value pairs for application specific filters.
<br>
Example values: "x-foo", "x-bar".
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>deviceMcc</td>
<td>String</td>
<td>null</td>
<td>Android</td>
<td>
Mobile country code of device. See <a href="http://en.wikipedia.org/wiki/Mobile_country_code">Mobile country code</a><br>
Example value: "286" for Turkey.
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>deviceMnc</td>
<td>String</td>
<td>null</td>
<td>Android</td>
<td>
Mobile network code of device. See <a href="http://en.wikipedia.org/wiki/Mobile_country_code">Mobile country code</a><br>
Example value: "1" for Turkcell.
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>updaterLevel</td>
<td>String</td>
<td>null</td>
<td>Android</td>
<td>
An integer number that is used to define updater version used by application.<br>
Example value: "1" for initial version of updater sdk.
</td>
<td>No</td>
<td>1</td>
</tr>

</table>

<h5>Example:</h5>
<pre>
<code>
{
	"deviceOsName": "android",
	"deviceOsVersion": "4.*",
	"appVersionName": "2.4.3, 2.4.4, 2.5.*, 3.*, 4.*, 5.*",
	"deviceIsTablet": "true"
}
</code>
</pre>

</div>

<!-- updateDescriptionEntry -->
<div id="updateDescriptionEntry" class="entry">
<h4>Update Description Entry</h4>
Contains language specific texts that are displayed to user on updates found dialog.
<br>
See <a href="#updateEntry">Update Entry</a>
<br>

<h5>Structure:</h5>
Type: Object

<table>
<tr>
<th>Property name</th>
<th>Type</th>
<th>Default value</th>
<th>Platforms</th>
<th>Description</th>
<th>Required</th>
<th>Since</th>
</tr>

<tr>
<td>message</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>
Summary information describing update contents.
</td>
<td>Yes</td>
<td>1</td>
</tr>

<tr>
<td>whatIsNew</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>
Describes changes and new features of new version.
</td>
<td>No</td>
<td>1</td>
</tr>

<tr>
<td>warnings</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>
Warning text about the update. Any important issues that user should know before updating should be described here.
</td>
<td>No</td>
<td>1</td>
</tr>

</table>

<h5>Example:</h5>
<pre>
<code>
{
	"message": "New version available",
	"whatIsNew": "Minor bug fixes",
	"warnings": "New version requires additional privileges"
}
</code>
</pre>

</div>

<!-- messageDescriptionEntry -->
<div id="messageDescriptionEntry" class="entry">
<h4>Message Description Entry</h4>
Contains language specific texts that are displayed to user on message dialog.
<br>
See <a href="#messageEntry">Message Entry</a>
<br>

<h5>Structure:</h5>
Type: Object

<table>
<tr>
<th>Property name</th>
<th>Type</th>
<th>Default value</th>
<th>Platforms</th>
<th>Description</th>
<th>Required</th>
<th>Since</th>
</tr>

<tr>
<td>title</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>
Text that will be displayed at title of message dialog.
</td>
<td>No</td>
<td>2</td>
</tr>

<tr>
<td>message</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>
Text displayed inside message dialog.
</td>
<td>Yes</td>
<td>2</td>
</tr>

<tr>
<td>imageUrl</td>
<td>String</td>
<td>null</td>
<td>All</td>
<td>
Fully qualified URL of image file that is displayed in message dialog. It should refer to a square PNG or JPEG with preferably at 100x100 pixels size.
</td>
<td>No</td>
<td>2</td>
</tr>

</table>

<h5>Example:</h5>
<pre>
<code>
{
	"title": "Offer",
	"message": "New application is avaliable!",
	"imageUrl": "http://example.com/app2-icon.png"
}
</code>
</pre>

</div>


</body>
</html>
