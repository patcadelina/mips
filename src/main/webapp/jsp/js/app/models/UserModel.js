
var UserModel = Backbone.Model.extend({
   
   //default values   
   defaults:{
      userName: '',
      emailAddress: '',
      passWord: '',
      confirmPassword: '',
      editionId: 1, // defaults to community edition
      clusterId: -1,
      termsOfUse: false
   },
   
   //defined validation rules
   validation: {
      userName: {
         required: true
      },
      emailAddress: {
         pattern: 'email'
      },
      passWord: {
         required: true,
         minLength: 6
      },
      confirmPassword: {
         equalTo: 'passWord'
      },
      editionId: {
         min: 0
      },
      clusterId: {
         min: 0
      },
      termsOfUse: {
         acceptance: true
      }
   }
});
