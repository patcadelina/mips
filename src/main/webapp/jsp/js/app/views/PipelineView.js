var PipelineView = Backbone.View.extend({
	
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
			
		});

		$("#opcodeTable").html(html);
	},
	
	renderPipelineMap: function(){
		
	}
});