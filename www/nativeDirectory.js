/*global cordova, module*/

module.exports = {
    getDirectory: function (name, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "NativeDirectory", "getDirectory", [name]);
    },
    openDocumentTree: function(name, successCallback, errorCallback){
    	cordova.exec(successCallback, errorCallback, "NativeDirectory", "openDocumentTree", [name]);
    },
    createDirectory: function(name, successCallback, errorCallback){
    	cordova.exec(successCallback, errorCallback, "NativeDirectory", "createDirectory", [name]);
    },
    startDownload: function(name, successCallback, errorCallback){
    	cordova.exec(successCallback, errorCallback, "NativeDirectory", "startDownload", [name]);
    },
    abortDownload: function(name, successCallback, errorCallback){
    	cordova.exec(successCallback, errorCallback, "NativeDirectory", "abortDownload", [name]);
    },
    getFreeSpace: function(name, successCallback, errorCallback){
        cordova.exec(successCallback, errorCallback, "NativeDirectory", "getFreeSpace", [name]);
    }
};