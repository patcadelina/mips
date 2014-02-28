editor = '';

var labelStack = new Array();
var registers = new Array();
var iTypeKeywords = ["BEQZ", "LD", "SD", "AND", "DSRV", "SLT"];
var rTypeKeywords = ["DADDU", "DSUBU", "OR", "DSLLV", "SLT"];
var jTypeKeywords = ["J"];
var errorStack = new Array();

for(var registerNum=0; registerNum<=31; registerNum++){
	registers.push("R"+(''+registerNum));
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
    editor.getSession().setMode("ace/mode/assembly_x86");
}

function parseSourceCode(){
	errorStack = new Array();//empty the error stack
	//iterate over the source code line by line, lexical analysis
	var lines = editor.session.doc.getAllLines();
	for (var i=0; i<lines.length; i++) {
	    var noSpacesBetweenTokens = lines[i].replace(/\s+/g, " "); //trim all spaces between a token and replace it with a space
	    var noLeadingAndTrailingSpaces = noSpacesBetweenTokens.replace(/^\s+|\s+$/g, ""); //remove all leading and trailing spaces
	    var tokenizedString = noLeadingAndTrailingSpaces.split(" ");
	    validateSyntax(tokenizedString,i);
	}
	console.log(errorStack);
}

function validateSyntax(tokenizedString, lineNumber){

	//start label validation
	
	//see if it has a label and validate it, label is at [0] and it suppose to end with :
	if(tokenizedString[0].charAt(tokenizedString[0].length-1)==":"){
		var isValid = validateLabel(tokenizedString[0]);
		if(!isValid){ 
			errorStack.push("An Invalid Label is found in line " + lineNumber);
			return isValid; // syntax error due to an invalid label
		}else{
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
		
	}else{
		errorStack.push("An Invalid instruction is found at line " + lineNumber);
	}
		
	//end instruction type validation
		
	}
}

function validateLabel(label){
	//check if the label begins with a letter
	if(!isAlpha(label.charAt(0))){
		return false;
	}
	//extract the label name
	var labelName = label.substring(0,label.length-1);
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
			return false;
		}
	}
	labelStack.push(labelName);
	return true;
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

function validateHex(hexString){
	var isValidHex = false;
	if(hexString.charAt(0)!="#"||hexString.length!=5){
		return false;
	}
	for(var i=1; i<hexString.length; i++){
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

