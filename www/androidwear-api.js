module.exports = {    
    getMessages: function(success, error) {  
        cordova.exec(success, error, 'AndroidWearApi', 'getMessages', []);
    },
    sendMessage: function(msg, success, error){
        cordova.exec(success, error, 'AndroidWearApi', 'sendMessage', [msg]);
    }
}