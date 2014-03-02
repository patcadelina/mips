var MainRouter = Backbone.Router.extend({
   
   routes:{
	   '' : 'main'
   },
   
   //This the main route of the application
   main: function(){
	   	//Declare the Editor View
	   	var editorModel = new EditorModel({});
	   	var editorView = new EditorView({
	   		el: "body",
	   		model: editorModel
	   	});
	   	editorView.render();
	   	
	   	//Declare the Register View
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
	   	
	   	//Declare the MIPS Register View
	   	var mipsRegisterCollection = new RegisterCollection();
	    var sampleMipsRegister = sampleRegister.clone();
	    sampleMipsRegister.set('registerName', 'IF/ID.IR');
	    sampleMipsRegister.set('registerValue', '0000');
	    mipsRegisterCollection.add(sampleMipsRegister);
	    var mipsInternalRegisterView = new MIPSInternalRegisterView({
	    	el: "body",
	    	collection: mipsRegisterCollection
	    });
	    mipsInternalRegisterView.render();
	    
   }

});
