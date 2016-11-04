// un-qualified abbreviations for the bridges' methods
// cannot assign methods directly, must wrap with lambda
// debug, simulation, visualisation

// for internal use
_internal = {}

// global objects
Register = Java.type('simulizer.assembler.representation.Register');
reg = Register
convert = Java.type('simulizer.simulation.data.representation.DataConverter');
AnnotationEarlyReturn = Java.type('simulizer.annotations.AnnotationEarlyReturn');

// Javascript Utilities
function subSection(array, start, length) { // alternative to slice
    return array.slice(start, start+length);
}
function split(array, loc) { // returns [first_bit, rest]
    return [array.slice(0, loc), array.slice(loc)];
}
function randInt(a, b) { // gives result in [a, b)
    return a + Math.floor(Math.random() * (b - a + 1)); // from Underscore.js
}
function toJS(thing) {
    // converts from a Java array to a js one. Cannot call slice etc on a Java array
    // see Nashorn documentation for details
    return Java.from(thing);
}


// debug bridge
log   = function(msg){debug.log(''+msg);};
alert = function(msg){debug.alert(''+msg);};
assert = function(cond){debug.assertTrue(cond);};

// simulation bridge
pause    = function(){simulation.pause();};
stop     = function(){simulation.stop();};
setSpeed = function(s){simulation.setSpeed(s);};

// global bindings for each register are added later
// eg $s0 = {id:Register.s0, get: function to get the current s0 value}


// visualisation bridge

// Utility functions
ret = function(){throw new AnnotationEarlyReturn();};
binString = function(n){return '0b'+(n>>>0).toString(2);}  // >>> to coerce to unsigned
hexString = function(n){return '0x'+(n>>>0).toString(16).toUpperCase();} // >>> to coerce to unsigned

// override globals (from debugger watch private attribute: engine.global for a full list)
_internal.disabled = function(){print('disabled');};

print = log;
exit  = stop;
quit  = stop;

load  = _internal.disabled; // loads a script file
loadWithNewGlobal = _internal.disabled;
