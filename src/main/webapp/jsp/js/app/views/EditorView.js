
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
		var hasException = parseSourceCode();
		if(finalInstructionStack.length>0){
			$("#statusCompile").removeClass("hidden");
			$("#statusSuccess").addClass("hidden");
			$("#statusCompile").addClass("display");
			if(!hasException){
				var data = new Array();
				for(var i=0; i<finalInstructionStack.length; i++){
					var line = {line : (i+1), command: finalInstructionStack[i]};
					data.push(line);
				}
				$.ajax({
					   url: App.memoryUrl,
					   data: JSON.stringify(data),
					   type: 'POST',
					   contentType: "application/json",
					   success: function(response) {
						    $("#statusCompile").addClass("hidden");
							$("#statusSuccess").removeClass("hidden");
							$("#statusSuccess").addClass("display");
							var pipelineView = new PipelineView({
								el: "body",
							});
							pipelineView.renderOpcodeTable(response);
							//Backbone.history.navigate('#fullExecution', {trigger:true}); 
					   }
				});
			}
		}else{
			//errorStack.push("No Lines to Execute.");
		}
		this.renderStackTrace();
	}
	
});