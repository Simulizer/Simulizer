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


// debug bridge
log   = function(msg){debug.log(''+msg);};
alert = function(msg){debug.alert(''+msg);};
assert = function(cond){debug.assertTrue(cond);};

// simulation bridge
stop     = function(){simulation.stop();};
setSpeed = function(s){simulation.setSpeed(s);};

// global bindings for each register are added later
// eg $s0 = {id:Register.s0, get: function to get the current s0 value}


// visualisation bridge

// Utility functions
ret = function(){throw new AnnotationEarlyReturn();};

// override globals (from debugger watch private attribute: engine.global for a full list)
_internal.disabled = function(){print('disabled');};

print = log;
exit  = stop;
quit  = stop;

load  = _internal.disabled; // loads a script file
loadWithNewGlobal = _internal.disabled;
