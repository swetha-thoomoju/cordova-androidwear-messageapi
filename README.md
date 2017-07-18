# cordova-androidwear-messageapi
A Cordova plugin for the communication with an AndroidWear device. 
It allows you to send and receive messages from the nodes to which the device is connected.
The service works also if the application gets killed.

## Installation
With Cordova CLI, from npm:
```
$ cordova plugin add https://github.com/smartcommunitylab/cordova-androidwear-messageapi
```

## Platform

* Android

## Using

```javascript
AndroidWearApi.getMessages(success, error)

```
```javascript
AndroidWearApi.sendMessage(msg, success, error)
```

### Example
  ```javascript
  var app = {
    initialize: function () {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },

    onDeviceReady: function () {
        this.receivedEvent('deviceready');

        document.getElementById("btnsend").addEventListener("click", function () { send('hello'); }, false);
        
        AndroidWearApi.getMessages(function (data) {
            alert(data);
        });
    },

    receivedEvent: function (id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');
    },
};
app.initialize();

function send(msg) {
    AndroidWearApi.sendMessage(msg, function (data) {
        Console.log(data);
    });
}
  ```
