mips
====

Minified 64-bit MIPS


Server Side
------------------------------

API

------------------   Generic Input/Output from API   -------------------
Input: [Header: Content-Type application/json]
Output: 200 OK
        400 Bad Request
        404 Not Found
        500 Internal Server Error
Note: null values from server are not returned to JSON output
e.g. for Register object {"name": <name>, "value": value}, value is not displayed in JSON return if "value" is null


PUT     ../system/init
Usage:  initialize memory and registers
Input:  NA
Output: NA


GET     ../registers
Usage:  find all registers
Input:  NA
Output: [{"name": "R0", "value": "0000000000000000000000000000000000000000000000000000000000000000"}, {"name": "R1"}, ...]


GET     ../registers/<id>
Usage:  find register
Input:  {"name": "R1"}
Output: {"name": "R1", "value": "0000000000000000000000000000000000000000000000000000000000000001"}


PUT     ../registers/<id> 
Usage:  update register (accepts register json)
Input:  {"name": "R1", "value": "0000000000000000000000000000000000000000000000000000000000000101"}
Output: {"name": "R1", "value": "0000000000000000000000000000000000000000000000000000000000000101"}


GET     ../memory?from=<startAddress>&to=<endAddress>
Usage:  find memory in range 'from' to 'to'
Input:  NA
Output: [{"address": "2000", "value": "00000111"}]


PUT     ../memory/<address>
Usage:  update memory at address (accepts memory json)
Input:  {"address": "2004", "value": "00010001"}
Output: {"address": "2004", "value": "00010001"}


POST    ../memory
Usage:  create instruction (accepts instruction collection json)
Input:  [{"line": 1, "command": "DADDU R1, R0, R2"}, {"line": 2, "command": "BNEZ R1, L1}, ..]
Output: NA


Client Side
------------------------------
Single Page application that is powered by Backbone.js

Uses the following:

Backbone.js
Jquery
Underscore
Twitter Bootstrap
Ace Code Editor

css - css files
fonts - fonts files
js - js script files
|
|______libs - required 3rd party js libraries
|
|______app - main folder for ember application 
|        |
|        |_____model - model scripts
|        |
|        |_____routes -router scripts
|        |
|        |_____view - view scripts
|        
|______test - folder for unit tests
|
|______index.html - Main Window of the app
