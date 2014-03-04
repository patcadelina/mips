
var EditorView = Backbone.View.extend({
	
	currentClockCycle: 1,
	registers: '',
	mips: '',
	plMap: '',
	hasExecutedLastProccess: false,
	isFinishedExecuting: false,
	isCurrentlyExecuting: false,
	executionInterval: '',

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
	
	setRefreshView: function(registerView, mipsRegisterView){
		this.registers = registerView;
		this.mips = mipsRegisterView;
	},
	
	events: {
		'click [name~="compile"]' : 'compileSource',
		'click [name~="nextCc"]' : 'stepThrough'
		
	},
	
	compileSource: function(){
		var hasException = parseSourceCode();
		if(finalInstructionStack.length>0){
			var self = this;
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
							$("#statusCc").popover('hide');
							$("#statusCc").html('<i class="fa fa-clock-o fa-2x"></i> 0');
							var pipelineView = new PipelineView({
								el: "body",
							});
							self.plMap = pipelineView;
							pipelineView.renderOpcodeTable(response);
							pipelineView.initializePipelineMap();
							pipelineView.renderPipelineMap();
							//Backbone.history.navigate('#fullExecution', {trigger:true}); 
							var mode = $("#mode").val();
							if(mode=="full"){
								this.currentClockCycle = 1;
								self.executionInterval = setInterval(function(){
									if(!self.isCurrentlyExecuting){
										self.stepThrough();
									}
									if(self.isFinishedExecuting){
										clearInterval(self.executionInterval);
									}
									console.log(self.currentClockCycle);
								},1000);
							}else{
							    $('.ccControl').prop("disabled", false); 
								this.currentClockCycle = 1;
							}
					   }
				});
			}
		}else{
			//errorStack.push("No Lines to Execute.");
		}
		this.renderStackTrace();
	},
	
	stepThrough: function(){
		var self = this;
		var doExec = true;
		self.isCurrentlyExecuting = true;
		$.ajax({
			   url: App.executionUrl+"/"+self.currentClockCycle,
			   type: 'POST',
			   contentType: "application/json",
			   dataType: "json",
			   success: function(pipelineMap) {
				self.isCurrentlyExecuting = false;
				var lastPc = toHex($("#lastInstruction").val(),4);
					pipelineMap.processes.forEach(function(process){
						if(lastPc==process.address && process.status=="WB"){
							doExec = false;
							if(!self.hasExecutedLastProccess){
								$("#statusCc").html('<i class="fa fa-clock-o fa-2x"></i> ' + self.currentClockCycle);
								 self.registers.render();
								 self.mips.render();
								 self.updatePipelineMap(pipelineMap);
								 self.plMap.renderPipelineMap();
							}
							self.hasExecutedLastProccess = true;
						}
				});
				if(doExec && !self.hasExecutedLastProccess){
					 $("#statusCc").html('<i class="fa fa-clock-o fa-2x"></i> ' + self.currentClockCycle);
					 self.currentClockCycle = self.currentClockCycle+1;
					 self.registers.render();
					 self.mips.render();
					 self.updatePipelineMap(pipelineMap);
					 self.plMap.renderPipelineMap();
				}else{
					$("#statusCc").popover('show');
					self.isFinishedExecuting = true;
				}
			  },
			  error: function(error){
				  self.isFinishedExecuting = true;
				  window.alert('Trying to access null store. Please check your register and memory');
				  errorStack.push("Trying to access null store. Please check your register and memory");
				  self.renderStackTrace();
			  }
		});		
	},
	
	updatePipelineMap: function(processMap){
		var updatedInstruction = new Array();
		pipelineMap.forEach(function(process){
			// update ones with pipeline proccess
			
			var pLineNum = process.lineNumber;
			var pPipeLine = process.pipeLine;
			processMap.processes.forEach(function(clockRun){
				if(clockRun.address==pLineNum){
					updatedInstruction.push(pLineNum);
					pPipeLine.push(clockRun.status);
				}
			});
			process.pipeLine = pPipeLine;
		});
		
		pipelineMap.forEach(function(process){
			// update ones with pipeline proccess
			var pLineNum = process.lineNumber;
			var pPipeLine = process.pipeLine;
			var isUpdated = false;
			updatedInstruction.forEach(function(instruction){
				if(instruction==pLineNum){
					isUpdated = true;
				}
			});
			if(!isUpdated){
				pPipeLine.push(" ");
			}
			process.pipeLine = pPipeLine;
		});
		
	}
	
});