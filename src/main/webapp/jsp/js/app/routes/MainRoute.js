var MainRouter = Backbone.Router.extend({
   
   routes:{
	   '' : 'main'
   },
   
   //This the main route of the application
   main: function(){
		$.ajax({
			   url: App.initUrl,
			   type: 'PUT',
			   contentType: "application/json",
			   success: function(response) {
				  	//Declare the Editor View
				   	var editorModel = new EditorModel({});
				   	var editorView = new EditorView({
				   		el: "body",
				   		model: editorModel
				   	});
				   	editorView.render();
				   	//Declare the MIPS Register View
				    var sampleRegister = new RegisterModel();
				   	
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
				    
				    new MemoryView({
				    	el: "body"
				    });
				    var registerView = new RegisterView({
			    		el: "body"
			    	});
			    	registerView.render();
			   }
		});
	    
   }

});
