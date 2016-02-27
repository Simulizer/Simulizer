// debug, simulation, visualisation

nop = function() {} // do nothing

exit = nop;
load = visualisation.load;

print = debug.print;
alert = function(msg){debug.alert(msg);};
