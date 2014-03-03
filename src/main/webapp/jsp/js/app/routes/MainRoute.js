var MainRouter = Backbone.Router.extend({

	routes:{
		'' : 'main',
		'fullExecution' : 'doFullRun'
	},

	//This the main route of the application
	main: function(){
		$.ajax({
			url: App.initUrl,
			type: 'PUT',
			contentType: "application/json",
			success: function(response) {
				new MenuView({
					el: "body"
				});
				//Declare the Editor View
				var editorModel = new EditorModel({});
				var editorView = new EditorView({
					el: "body",
					model: editorModel
				});
				editorView.render();
				//Declare the MIPS Register View
				var mipsInternalRegisterView = new MIPSInternalRegisterView({
					el: "body"
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
	},
	// refresh mips, and internal registers
	doFullRun: function(){
		var mipsInternalRegisterView = new MIPSInternalRegisterView({
			el: "body"
		});
		mipsInternalRegisterView.render();
		var registerView = new RegisterView({
			el: "body"
		});
		registerView.render();
	}

});
