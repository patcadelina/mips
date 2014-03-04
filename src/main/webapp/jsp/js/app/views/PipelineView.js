var PipelineView = Backbone.View.extend({
	
	currentCc: 1,
	
	renderOpcodeTable: function(instructionAddress){
		var html = '';
		var lineCounter = 0;
		var lineNum = 0;
		var mul = 1;
		finalInstructionStack.forEach(function(line){
			var binaryString = "";
			for(var i=lineCounter; i<(4*mul); i++, lineCounter++){
				binaryString += instructionAddress[i].value;
			}
			
			var hexOpcode = toHex(binaryString,8);
			var pc = toHex(parseInt(''+(lineNum*4)).toString(2),2);
			var R05 = binaryString.substring(0, 6);
			var R610 = binaryString.substring(6, 11);
			var R1115 = binaryString.substring(11, 16);
			var R1631 = binaryString.substring(16, binaryString.length);
			
			html += '<tr>';
				html += '<td>' +pc+'</td>';
				html += '<td>' +line+'</td>';
				html += '<td>' +hexOpcode+'</td>';
				html += '<td>' +R05+'</td>';
				html += '<td>' +R610+'</td>';
				html += '<td>' +R1115+'</td>';
				html += '<td>' +R1631+'</td>';
			html += '</tr>';
			
			lineNum++;
			mul++;
			$("#lastInstruction").val(pc);
		});

		$("#opcodeTable").html(html);
	},
	
	initializePipelineMap: function(){
		for(var lineNum=0; lineNum<finalInstructionStack.length; lineNum++){
			var pipeLineStatus = new Array();
			var pc = toHex(parseInt(''+(lineNum*4)).toString(2),4);
			var process = {
					lineNumber : pc,
					instruction : finalInstructionStack[lineNum],
					pipeLine: pipeLineStatus
			};
			pipelineMap.push(process);
		}
		console.log(pipelineMap);
	},
	
	renderPipelineMap: function(){
		var clockCycleHtml = "<tr><td>&nbsp</td>";
			for(var i=0; i<this.currentCc; i++){
				clockCycleHtml +="<td>" + (i+1) + "</td>";
			}
			this.currentCc = this.currentCc + 1;
		clockCycleHtml+= "</tr>";
		$("#clockCycle").html(clockCycleHtml);
		
		var pipelineHtml = "";
		
		for(var i=0; i<pipelineMap.length; i++){
			pipelineHtml +="<tr>";
			pipelineHtml +="<td>" + pipelineMap[i].instruction + "</td>";
			for(var j=0; j<pipelineMap[i].pipeLine.length; j++){
				pipelineHtml +="<td>" + pipelineMap[i].pipeLine[j] + "</td>";
			}
			pipelineHtml +="</tr>";
		}
		
		$("#pipelineDetails").html(pipelineHtml);
	}
});