
var MenuView = Backbone.View.extend({
   events:{
	   'click [name~="fexe"]' : 'doFullExecution',
	   'click [name~="stexe"]' : 'doSingleStepExecution',
	   'click [name~="clear"]' : 'clearCache'
   },
   
   doFullExecution: function(){
	   $("#fexe").addClass("active");
	   $("#stexe").removeClass("active");
	   $("#statusCc").fadeOut();
	   $('.ccControl').prop("disabled", true); 
	   $("#mode").val("full");
   },
   
   doSingleStepExecution: function(e){
	   $("#stexe").addClass("active");
	   $("#fexe").removeClass("active");
	   $("#statusCc").fadeIn();
	   $("#mode").val("step");
   },
   
   clearCache: function(){
	   location.href = "";
   }
   
});
