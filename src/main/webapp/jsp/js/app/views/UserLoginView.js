
var UserLoginView = Backbone.View.extend({
   //bind validation to view
   initialize: function(){
      Backbone.Validation.bind(this,{
         valid: function(view, attr){
             $('#errorUsername').css('display', 'none');
             $('#errorPassword').css('display', 'none');
             $('#errorPasswordLen').css('display', 'none');
             $('#errorLogin').css('display', 'none');
         },
         invalid: function(view, attr, error){
            $('#success').css('display', 'none');
            if(attr=="userName"){
               $('#errorUsername').css('display', 'block');
            }
           if(attr=="passWord"){
               if(error=="Pass word must be at least 6 characters"){
                  $('#errorPasswordLen').css('display', 'block');
               }else{
                  $('#errorPassword').css('display', 'block');
               }
                
           }
         }
      });
   },

   //bind verify
   events: {
      'click [name~="userLogin"]' : 'login', // Log me in
      'click [name~="register"]' : 'register' // Create Account
   },


   //handler for login account
   login: function(){
      $('#errorLogin').css('display', 'none');// hide login error
      this.model.set({
         userName: $('[name~="userName"]').val(), //this is needed for login
         passWord: $('[name~="passWord"]').val(),  //this is needed for login
         confirmPassword: $('[name~="passWord"]').val(),//dummy data to bypass model validation
         emailAddress: '1@1.com', //dummy data to bypass model validation
         clusterId: 1,   //dummy data to bypass model validation
         termsOfUse: true,   //dummy data to bypass model validation
      },{validate: true});

      if(this.model.isValid()){
        //Insert User Login API Call Here
        var isLoginValid = true;//needs to be negated once there is true validation
         
         if(isLoginValid){
            var sessionHash = ''; // use this to put a unique session has pref from the server
            createSession(sessionHash);// create a sesssion
            Backbone.history.navigate('#success', {trigger:true});//trigger redirect route
         }else{
            $('#errorLogin').css('display', 'block');// show login error
         }
      }
   },

   //handler for account registration redirect

   register: function(){
      Backbone.history.navigate('#register', {trigger:true});
   }
});
