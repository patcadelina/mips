
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
		this.renderStackTrace();
		var hasException = parseSourceCode();
		if(!hasException){
			var data = new Array();
			for(var i=0; i<finalInstructionStack.length; i++){
				var line = {line : i, command: finalInstructionStack[i]};
				data.push(line);
			}
			$.ajax({
				   url: App.memoryUrl,
				   data: JSON.stringify(data),
				   type: 'POST',
				   contentType: "application/json",
				   success: function(response) {
				     window.alert('sent');
				   }
			});
		}
	}
	
});