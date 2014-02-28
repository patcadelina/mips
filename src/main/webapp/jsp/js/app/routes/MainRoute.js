var MainRouter = Backbone.Router.extend({
   
   routes:{
	   '' : 'main'
   },
   
   //This the main route of the application
   main: function(){
	   	console.log('Route Initialzed....');
	   	
	   	//Declare the Mode View
	   	//Declare Error Console View
	   	//Declare the Editor View
	   	
	   	var editorModel = new EditorModel({});
	   	
	   	var editorView = new EditorView({
	   		el: "body",
	   		model: editorModel
	   	});
	   	editorView.render();
	   	//Declare the Run Mode View
	   	
   }

});
