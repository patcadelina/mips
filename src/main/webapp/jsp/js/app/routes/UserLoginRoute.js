var ApplicationRouter = Backbone.Router.extend({
   
   routes:{
      '' : 'login', //route for default
      'login' : 'login', //route show user login
      'success' : 'acceptLogin', //route for successfull login
      'register' : 'registerAccount'
   },

   //callback for login route
   login: function(){
     var userModel = new UserModel();
     this.currentUser = userModel;
     var userLoginView = new UserLoginView({
         model: userModel,
         el: 'body'
      });
      //make an instance of terms and privacy for footer control
      var termsAndPrivacy = new TermsAndPrivacyView({
         el:"body"
      });
   },

   //redirect for successfull login
   acceptLogin: function(){
      document.location.href = "profile.html";
   },
   //redirect to register page
   registerAccount: function(){
      document.location.href = "register.html";
   }
});
