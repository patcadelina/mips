
var EditorView = Backbone.View.extend({

	render: function(){
		initializeEditor();
	},
	
	events: {
		'click [name~="compile"]' : 'compileSource'
	},
	
	compileSource: function(){
		parseSourceCode();
	}
	
});
