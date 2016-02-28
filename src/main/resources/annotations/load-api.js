// un-qualified abbreviations for the bridges' methods
// cannot assign methods directly, must wrap with lambda
// debug, simulation, visualisation

// for internal use
_internal = {}

_internal.nop = function(){};

log   = function(msg){debug.log(''+msg);};
alert = function(msg){debug.alert(''+msg);};

// override globals (from debugger watch private attribute: engine.global for a full list)
_internal.disabled = function(){print("disabled");};
print = log;
exit = _internal.disabled;
quit = _internal.disabled;
load = _internal.disabled; // loads a script file
loadWithNewGlobal = _internal.disabled;
