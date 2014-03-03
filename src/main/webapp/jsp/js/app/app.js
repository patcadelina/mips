/*
 * 
 * ------------------ Generic Input/Output from API ------------------- Input: [Header: Content-Type application/json] Output: 200 OK 400 Bad Request 404 Not Found 500 Internal Server Error Note: null values from server are not returned to JSON output e.g. for Register object {"name": , "value": value}, value is not displayed in JSON return if "value" is null

PUT ../system/init Usage: initialize memory and registers Input: NA Output: NA
GET ../registers Usage: find all registers Input: NA Output: [{"name": "R0", "value": "0000000000000000000000000000000000000000000000000000000000000000"}, {"name": "R1"}, ...]
GET ../registers/ Usage: find register Input: {"name": "R1"} Output: {"name": "R1", "value": "0000000000000000000000000000000000000000000000000000000000000001"}
PUT ../registers/ Usage: update register (accepts register json) Input: {"name": "R1", "value": "0000000000000000000000000000000000000000000000000000000000000101"} Output: {"name": "R1", "value": "0000000000000000000000000000000000000000000000000000000000000101"}
GET ../memory?from=&to= Usage: find memory in range 'from' to 'to' Input: NA Output: [{"address": "2000", "value": "00000111"}]
PUT ../memory/ Usage: update memory at address (accepts memory json) Input: {"address": "2004", "value": "00010001"} Output: {"address": "2004", "value": "00010001"}
POST ../memory Usage: create instruction (accepts instruction collection json) Input: [{"line": 1, "command": "DADDU R1, R0, R2"}, {"line": 2, "command": "BNEZ R1, L1}, ..] Output: NA
 */
window.App = {
    initUrl : '../api/system/init',
	memoryUrl : '../api/memory',
    registerUrl : '../api/registers',
    pipelineUrl : '../api/clock'
};
