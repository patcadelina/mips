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
	   	
	   	var registerCollection = new RegisterCollection();
	    var sampleRegister = new RegisterModel();
	    sampleRegister.set('registerName', 'R0');
	    sampleRegister.set('registerValue', '0000');
	    registerCollection.add(sampleRegister);
	   	var registerView = new RegisterView({
	   		el: "body",
	   		collection: registerCollection
	   	});
	   	registerView.render();
   }

});
