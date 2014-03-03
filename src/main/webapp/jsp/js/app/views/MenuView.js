
var MenuView = Backbone.View.extend({
   events:{
	   'click [name~="fexe"]' : 'doFullExecution',
	   'click [name~="stexe"]' : 'doSingleStepExecution'
   },
   
   doFullExecution: function(){
	   $("#fexe").addClass("active");
	   $("#stexe").removeClass("active");
	   $("#statusCc").fadeOut();
	   $('.ccControl').prop("disabled", true);  
   },
   
   doSingleStepExecution: function(e){
	   $("#stexe").addClass("active");
	   $("#fexe").removeClass("active");
	   $("#statusCc").fadeIn();
	   $('.ccControl').prop("disabled", false); 
   }
});
