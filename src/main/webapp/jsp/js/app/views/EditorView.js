
var EditorView = Backbone.View.extend({

	render: function(){
		initializeEditor();
	},
	
	renderStackTrace: function(){
		var errorStack = getErrorStack();
		var html = "";
		for(var i=0; i<errorStack.length; i++){
			html += errorStack[i] + "<br/>";
		}
		$("#errorConsole").html(html);
	},
	
	events: {
		'click [name~="compile"]' : 'compileSource'
	},
	
	compileSource: function(){
		parseSourceCode();
		this.renderStackTrace();
	}
	
});
