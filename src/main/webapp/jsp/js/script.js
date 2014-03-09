editor = '';

var labelStack = new Array();
var registers = new Array();
var finalInstructionStack = new Array();
var iTypeKeywords = ["BNEZ", "LD", "SD", "DADDIU", "ANDI"];
var rTypeKeywords = ["DADDU", "DSUBU", "OR", "DSLLV", "SLT"];
var jTypeKeywords = ["J"];
var errorStack = new Array();
var pipelineMap = new Array();

for(var registerNum=0; registerNum<=31; registerNum++){
	registers.push("R"+(''+registerNum));
	if(registerNum>=0 && registerNum<=9){ // for R01 - R09
		registers.push("R0"+(''+registerNum));
	}
}
//tells if a string is a number
function isNumeric(str){
	var isNumber = false;
	if(str.match(/^[0-9]+$/)){
		isNumber = true;
	}
	return isNumber;
}
//tells if the string is a letter
function isAlpha(str){
	var isAlphabet = false;
	if(str.match(/^[a-zA-Z]+$/)){
		isAlphabet = true;
	}
	return isAlphabet;
}
//initializes a new editor
function initializeEditor(){
	editor = ace.edit("editorText");
    editor.setTheme("ace/theme/chrome");
    editor.setHighlightActiveLine(true);
    editor.getSession().setMode("ace/mode/assembly_x86");
}

function parseSourceCode(){
	errorStack = new Array();//empty the error stack
	labelStack = new Array();//empty the label stack
	finalInstructionStack = new Array();//empty the instruction stack
	//iterate over the source code line by line, lexical analysis
	var lines = editor.session.doc.getAllLines();
	var hasException = false;
	//Initial pass through for Label pre information
	for (var i=0; i<lines.length; i++) {
	    var noSpacesBetweenTokens = lines[i].replace(/\s+/g, " "); //trim all spaces between a token and replace it with a space
	    var noLeadingAndTrailingSpaces = noSpacesBetweenTokens.replace(/^\s+|\s+$/g, ""); //remove all leading and trailing spaces
	    var tokenizedString = noLeadingAndTrailingSpaces.split(" ");
	    if(tokenizedString.length!=0){ // make sure line is not a empty line
	    	var isValid = getLabelPreInformation(tokenizedString,i+1);
		    if(!isValid){
		    	hasException = true;
		    	editor.gotoLine(i+1);
		    	break;
		    }
	    }
	}
	//Second pass through for line by line validation
	if(!hasException){
		for (var i=0; i<lines.length; i++) {
		    var noSpacesBetweenTokens = lines[i].replace(/\s+/g, " "); //trim all spaces between a token and replace it with a space
		    var noLeadingAndTrailingSpaces = noSpacesBetweenTokens.replace(/^\s+|\s+$/g, ""); //remove all leading and trailing spaces
		    var tokenizedString = noLeadingAndTrailingSpaces.split(" ");
		    if(tokenizedString.length>1){ // make sure line is not a empty line
		    	var isValid = validateSyntax(tokenizedString,i+1);
			    if(!isValid){
			    	editor.gotoLine(i+1);
			    	hasException = true;
			    	break;
			    }
		    }else{
		    	if(tokenizedString[0]!=""){
		    		errorStack.push("Invalid Instruction Exception at line number " + (i+1));
		    		hasException = true;
		    		editor.gotoLine(i+1);
			    	break;
		    	}
		    }
		}
		
		//Third pass through for line by line formatting
		for (var i=0; i<lines.length; i++) {
		    var noSpacesBetweenTokens = lines[i].replace(/\s+/g, " "); //trim all spaces between a token and replace it with a space
		    var noLeadingAndTrailingSpaces = noSpacesBetweenTokens.replace(/^\s+|\s+$/g, ""); //remove all leading and trailing spaces
		    var tokenizedString = noLeadingAndTrailingSpaces.split(" ");
		    if(tokenizedString.length>1){ // make sure line is not a empty line
		    	formatSyntax(tokenizedString);
		    }
		}
	}
	
	if(hasException){
		errorStack.push("Compilation Failed. Please check stack trace for details");
	}else{
		errorStack.push("Compilation Success... Running the program...");	
	}
	return hasException;
}

function getLabelPreInformation(tokenizedString, lineNumber){
	//start label validation
	//see if it has a label and validate it, label is at [0] and it suppose to end with :
	if(tokenizedString[0].charAt(tokenizedString[0].length-1)==":"){
		var isValid = validateLabel(tokenizedString[0], true);
		if(!isValid){ 
			errorStack.push("Invalid Label Exception at line number " + lineNumber);
			return isValid; // syntax error due to an invalid label
		}
		return true;
	}
	return true;
	//end instruction type validation

}

function validateSyntax(tokenizedString, lineNumber){
	var isValid = false;
	//see if it has a label and trim it, label is at [0] and it suppose to end with :
	if(tokenizedString[0].charAt(tokenizedString[0].length-1)==":"){
		var tempTokenizedString = new Array();
		for(var i=1; i<tokenizedString.length; i++){ // extract the label from the old string
			tempTokenizedString[i-1] = tokenizedString[i];
		}
		tokenizedString = tempTokenizedString; // assign the tokenized string with no label to the main tokenized string
	}
		
	//end label validation
	//start instruction type validation
	var instructionType = getInstructionType(tokenizedString[0]);
	if(instructionType!=null){
		if(instructionType=="I"){
			isValid = evaluateIType(tokenizedString, lineNumber);
		}else if(instructionType=="R"){
			isValid = evaluateRType(tokenizedString, lineNumber);
		}else if(instructionType=="J"){
			isValid = evaluateJType(tokenizedString, lineNumber);
		}
	}else{
		errorStack.push("An Invalid instruction is found at line " + lineNumber);
	}
	return isValid;
	//end instruction type validation
}


function validateLabel(label, pushLabel){
	//check if the label begins with a letter
	if(!isAlpha(label.charAt(0))){
		return false;
	}
	//extract the label name
	var labelName = '';
	if(pushLabel){ // if push label is true the label is added to the label stack
		labelName = label.substring(0,label.length-1);
	}else{
		labelName = label.substring(0,label.length);
	}
	//check if the label is a register
	for(var i=0; i<registers.length; i++){
		if(labelName==registers[i]){
			return false;
		}
	}
	//check it against keywords
	var allKeywords = iTypeKeywords.concat(rTypeKeywords, jTypeKeywords);
	for(var i=0; i<allKeywords.length; i++){
		if(labelName==allKeywords[i]){
			return false;
		}
	}
	//check it against any other labels
	for(var i=0; i<labelStack.length; i++){
		if(labelName==labelStack[i]){
			if(pushLabel){ // if push label is true the label is added to the label stack
				return false;
			}else{
				return true;
			}
		}
	}
	if(pushLabel){
		labelStack.push(labelName);
		return true;
	}else{
		return false;
	}
	
}


function validateRegister(register){
	var isValidRegister = false;
	for(var i=0; i<registers.length; i++){ // traverse valid register array
		if(register==registers[i]){
			isValidRegister = true;
			break;
		}
	}
	return isValidRegister;
}

function validateHex(hexString, hasHash){
	var isValidHex = false;
	var i=1;
	if(hasHash){
		if(hexString.charAt(0)!="#"||hexString.length>5||hexString.length<2){
			return false;
		}
	}else{
		if(hexString.length<1){
			return false;
		}
		i=0;
	}
	
	for(;i<hexString.length; i++){
		var currentHexDigit = hexString.charAt(i);
		var validHexDigits = new Array();
		//sets a valid array with a set of valid characters
		if(isAlpha(currentHexDigit)){ // if char is a letter
			validHexDigits = ["A", "B", "C", "D", "E", "F"]; 
			
		}else if(isNumeric(currentHexDigit)){ // if char is a number
			validHexDigits = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"];
		}
		for(var j=0; j<validHexDigits.length; j++){
			isValidHex = false;
			if(currentHexDigit==validHexDigits[j]){ // char exists in the valid array
				isValidHex = true;
				break;
			}
		}
		if(!isValidHex){
			break;
		}
	}
	
	if(isValidHex){
		//check if the imm exceed the data segment 2000-3FFF
		if(hasHash){
			hexString = hexString.substring(1, hexString.length);
		}
	}
	//Uncomment to restrict hex constants to 2000 -3FFF
	//var hexIntValue = parseInt(hexString,16);
	//if(hexIntValue<parseInt("2000", 16) || hexIntValue>parseInt("3FFF", 16)){
	//	isValidHex = false;
	//	errorStack.push("An Immediate overflow exception (expected 2000-3FFF) ");
	//}
	return isValidHex;
}

function getInstructionType(keyword){
	var keywordType = null;
	var isFound = false;
	var keywords = "";
	for(var i=0; i<3; i++){
		if(i==0){
			keywords = iTypeKeywords;
			for(var j=0; j<keywords.length; j++){
				if(keywords[j]==keyword){
					isFound = true;
					keywordType = "I";
					break;
				}
			}
		}else if(i==1){
			keywords = rTypeKeywords;
			for(var j=0; j<keywords.length; j++){
				if(keywords[j]==keyword){
					isFound = true;
					keywordType = "R";
					break;
				}
			}
		}else{
			keywords = jTypeKeywords;
			for(var j=0; j<keywords.length; j++){
				if(keywords[j]==keyword){
					isFound = true;
					keywordType = "J";
					break;
				}
			}
		}
		if(isFound){
			break;
		}
	}
	return keywordType;
}


/*Syntax Validator
 * 
 */
function evaluateIType(tokenizedString, lineNumber){
	//Itypes has the following from (NOT INCLUDING Branches, Loads and Stores)
	var isValid = true;
	var instruction = tokenizedString[0];
	if(instruction=="LD" || instruction=="SD"){
		//instruction rd, imm(register)
		if(tokenizedString.length!=3){
			isValid = false;
			errorStack.push("Invalid format exception at line number " + lineNumber);
		}
		if(tokenizedString[1].charAt(tokenizedString[1].length-1)!=","){
			isValid = false;
			errorStack.push("Invalid format exception at line number (expected ,) " + lineNumber);
		}else{
			var isValidRegister = validateRegister(tokenizedString[1].substring(0,tokenizedString[1].length-1));
			if(!isValidRegister){
				isValid = false;
				errorStack.push("Invalid register constant at line number " + lineNumber);
			}else{
				if(tokenizedString[2].indexOf("(")==-1||tokenizedString[2].indexOf(")")==-1|| (tokenizedString[2].indexOf("(")>tokenizedString[2].indexOf(")"))){
					isValid = false;
					errorStack.push("Invalid imm offset at line number " + lineNumber);
				}else{
					//check the offset with the following pattern 0000(register)
					var regExp = /\(([^)]+)\)/;
					var matches = regExp.exec(tokenizedString[2]);
					var register = matches[1];
					var imm = tokenizedString[2].substring(0,tokenizedString[2].indexOf("("));
					var endImm = tokenizedString[2].substring(tokenizedString[2].indexOf(")")+1)
					if(endImm==""){
						var isValidRegister = validateRegister(register);
						if(!isValidRegister){
							isValid = false;
							errorStack.push("Invalid register offset " + lineNumber);
						}else{
							var isValidImm = validateHex(imm, false);
							if(!isValidImm){
								isValid = false;
								errorStack.push("Invalid imm offset at line number " + lineNumber);
							}
						}
					}else{
						isValid = false;
						errorStack.push("Invalid format exception at line number " + lineNumber);
					}
				}
			}
		}
		
	}else if(instruction=="BNEZ"){
		//branches has BRANCH register, label
		if(tokenizedString.length!=3){
			isValid = false;
			errorStack.push("Invalid format exception at line number " + lineNumber);
		}else{
				// if the register does not have a , after
			if(tokenizedString[1].charAt(tokenizedString[1].length-1)!=","){
				isValid = false;
				errorStack.push("Invalid format exception at line number (expected ,) " + lineNumber);
			}else{
				// check the register
				var isValidRegister = validateRegister(tokenizedString[1].substring(0,tokenizedString[1].length-1));
				if(!isValidRegister){
					isValid = false;
					errorStack.push("Invalid register constant at line number " + lineNumber);
				}else{
					//check the label
					isValid = validateLabel(tokenizedString[2],false);
					if(!isValid){
						errorStack.push("Invalid Jump Label Exception at line number " + lineNumber);
					}
				}
			}
		}	
	}else{
		//other itypes have instruction rd, rs, #imm
		if(tokenizedString.length!=4){
			isValid = false;
			errorStack.push("Invalid format exception at line number " + lineNumber);
		}else{
			//check the registers
			for(var i=1; i<tokenizedString.length-1; i++){
				if(tokenizedString[i].charAt(tokenizedString[i].length-1)!=","){
					isValid = false;
					errorStack.push("Invalid format exception at line number (expected ,) " + lineNumber);
					break;
				}
				var isValidRegister = validateRegister(tokenizedString[i].substring(0,tokenizedString[i].length-1));
				if(!isValidRegister){
					isValid = false;
					errorStack.push("Invalid register constant at line number " + lineNumber);
					break;
				}
			}
			if(isValid){
				//check the imm
				var isValidImm = validateHex(tokenizedString[3], true);
				if(!isValidImm){
					isValid = false;
					errorStack.push("Invalid imm constant at line number " + lineNumber);
				}
			}
		}
	}
	return isValid;
}
function evaluateRType(tokenizedString, lineNumber){
	// J Types has the following form INSTRUCTION rd, rs, rt
	//check if it has 3 tokens
	var isValid = true;
	if(tokenizedString.length==4){
		//check registers and commas
		for(var i=1; i<tokenizedString.length; i++){
			if(i==3){ //the last register must not have a comma
				var isValidRegister = validateRegister(tokenizedString[i]);
				if(!isValidRegister){
					isValid = false;
					errorStack.push("Invalid register constant at line number " + lineNumber);
					break;
				}
			}else{
				if(tokenizedString[i].charAt(tokenizedString[i].length-1)!=","){
					isValid = false;
					errorStack.push("Invalid format exception at line number (expected ,) " + lineNumber);
					break;
				}
				var isValidRegister = validateRegister(tokenizedString[i].substring(0,tokenizedString[i].length-1));
				if(!isValidRegister){
					isValid = false;
					errorStack.push("Invalid register constant at line number " + lineNumber);
					break;
				}
			}
		}
	}else{
		isValid = false;
		errorStack.push("Invalid format exception at line number " + lineNumber);
	}
	return isValid;
}
function evaluateJType(tokenizedString, lineNumber){
	// J Types has the following form J <LABEL>
	//check if it has 2 tokens
	var isValid = false;
	if(tokenizedString.length==2){
		//check the label if it exists
		isValid = validateLabel(tokenizedString[1],false);
		if(!isValid){
			errorStack.push("Invalid Jump Label Exception at line number " + lineNumber);
		}
	}else{
		isValid = false;
		errorStack.push("Invalid format exception at line number " + lineNumber);
	}
	return isValid;
}

function formatRegister(register){
	var hasComma = register.charAt(register.length-1)==',' ? true : false;
	if(hasComma){
		register = register.substring(0, register.length-1);
	}
	var registerNumber = parseInt(register.substring(1, register.length));
	var formattedRegister = "R" + (''+registerNumber);
	if(hasComma){
		formattedRegister += ",";
	}
	return formattedRegister;
}

function formatHex(imm){
	var hasHash = imm.charAt(0)=='#' ? true : false;
	var formattedHex = "";
	if(hasHash){
		imm = imm.substring(1,imm.length);
		formattedHex = "#";
	}
	//pad 0's
	for(var i=imm.length; i<4; i++){
		formattedHex += "0";
	}
	return formattedHex+imm;
}

function getErrorStack(){
	return errorStack;
}

function formatSyntax(tokenizedString, lineNumber){
	//see if it has a label and trim it, label is at [0] and it suppose to end with :
	var hasLabel = false;
	var lineLabel = "";
	if(tokenizedString[0].charAt(tokenizedString[0].length-1)==":"){
		hasLabel = true;
		lineLabel = tokenizedString[0];
		var tempTokenizedString = new Array();
		for(var i=1; i<tokenizedString.length; i++){ // extract the label from the old string
			tempTokenizedString[i-1] = tokenizedString[i];
		}
		tokenizedString = tempTokenizedString; // assign the tokenized string with no label to the main tokenized string
	}
		
	//end label validation
	//start instruction type validation
	var instruction = tokenizedString[0];
	var instructionType = getInstructionType(instruction);
	var finalString = instruction + " ";
	if(instructionType!=null){
		if(instructionType=="I"){
			if(instruction=="LD" || instruction=="SD"){
				var rd = formatRegister(tokenizedString[1]);
				var regExp = /\(([^)]+)\)/;
				var matches = regExp.exec(tokenizedString[2]);
				var register = matches[1];
				var imm = tokenizedString[2].substring(0,tokenizedString[2].indexOf("("));
				var registerOffset = formatRegister(register);
				var immOffset = formatHex(imm);
				finalString += rd + " " + immOffset+"("+registerOffset+")";
			}else if(instruction=="BNEZ"){
				var rd = formatRegister(tokenizedString[1]);
				var label = tokenizedString[2];
				finalString += rd + " " + label;
			}else{
				var rd = formatRegister(tokenizedString[1]);
				var rs = formatRegister(tokenizedString[2]);
				var imm = formatHex(tokenizedString[3]);
				finalString += rd + " " + rs + " " + imm;
			}
		}else if(instructionType=="R"){
			var rd = formatRegister(tokenizedString[1]);
			var rs = formatRegister(tokenizedString[2]);
			var rt = formatRegister(tokenizedString[3]);
			finalString += rd + " " + rs + " " + rt;
		}else if(instructionType=="J"){
			var label = tokenizedString[1];
			finalString += label;
		}
		if(hasLabel){
			finalString = lineLabel + " " + finalString;
		}
		finalInstructionStack.push(finalString);
	}else{
		errorStack.push("An Invalid instruction is found at line " + lineNumber);
	}
}

function toHex(binary, padTo){
	var num = parseInt(binary, 2).toString(16);
	var str = ''+num;
	var finalString = "";
	for(var i=str.length; i<padTo; i++){
		finalString += "0";
	}
	finalString+=str;
	return finalString.toUpperCase();
}

function toBinary(hex){
	var str = "";
	for(var i=0; i<hex.length; i++){
		var num = ''+parseInt(hex.charAt(i), 16).toString(2);
		var finalBinString = "";
		for(var j=num.length; j<4; j++){
			finalBinString +="0";
		}
		finalBinString+=num;
		str += finalBinString;
	}
	
	return str;
}

