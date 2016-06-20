# cordova-documentsproviders-read-write

##Storage using DocumentsProvider, which provides access to Internal, External & SD Card storage.  

Ref:https://developer.android.com/reference/android/content/Intent.html#ACTION_OPEN_DOCUMENT_TREE

Limitation: Users
Android API Level: 19
Works on Android Version: 4.4+


##Available methods

openDocumentTree() - It opens the Native Folder/Path Selector and returns content uri.


startDownload(Array(fileURL, token, ContentURI, Name, ContentType)) - it starts the download in background and using Native Android Code and keeps sending the success (ie number of bits completed).


abortDownload() - it stops the current downloading item (as this is single thread, we are not passing the reference)


createDirectory(Array(Name, ContentURI)) - for creating the directory (is used when only a folder needs to be created)


getFreeSpace(Array(ContentURI)) - returns total disk space available in bytes.


Ionic App
window.nativeDirectory.<function> (args, successCallback, errorCallback);






To Provide access on Android 4.4 - below 5.0 

All the Android Plugin functions are achieved using the Environment class.
Based on the Android Version, the Java class is switched between the DocumentsProvider and the access using Environment class.

