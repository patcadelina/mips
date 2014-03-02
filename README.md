mips
====

Minified 64-bit MIPS


Server Side
------------------------------
API
PUT  ../system/init    --initialize memory and registers
GET  ../registers      --find all registers
GET  ../registers/<id> --find register
PUT  ../registers/<id> --update register (accepts register json)
GET  ../memory?from=<startAddress>&to=<endAddress>  --find memory in range 'from' to 'to'
PUT  ../memory/<address>  --update memory at address (accepts memory json)
POST ../memory --create instruction (accepts instruction collection json)


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
