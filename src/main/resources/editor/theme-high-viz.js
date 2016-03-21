ace.define("ace/theme/high-viz",["require","exports","module","ace/lib/dom"], function(require, exports, module) {
"use strict";

exports.isDark = false;
exports.cssText = ".ace-high-viz .ace_gutter {\
background: #ffffff;\
border-right: 1px solid black;\
color: #000000;\
}\
.ace-high-viz {\
background-color: #FFFFFF;\
color: black;\
}\
.ace-high-viz .ace_fold {\
background-color: rgb(60, 76, 114);\
}\
.ace-high-viz .ace_cursor {\
color: black;\
}\
.ace-high-viz .ace_active-line {\
background: rgb(232, 242, 254);\
}\
.ace-high-viz .ace_marker-layer .ace_selection {\
background: rgb(181, 213, 255);\
}\
.ace-high-viz .ace_marker-layer .ace_selected-word {\
border: 1px solid rgb(181, 213, 255);\
}";

exports.cssClass = "ace-high-viz";

var dom = require("../lib/dom");
dom.importCssString(exports.cssText, exports.cssClass);
});
