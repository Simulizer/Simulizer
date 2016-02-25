/**
    Syntax Highlighting for SIMP

    reference: https://ace.c9.io/#nav=higlighter

    note:

    (?: REGEX) is used to match the REGEX but exclude it as a group
    so if only (?:) groups are found then a single string rather than a list of
    classes is given
    regexps are ordered -> the first match is used
**/


// define the highlighting rules for the mode
ace.define(
    'ace/mode/simp_highlight_rules',
    ['require', 'exports', 'module', 'ace/lib/oop', 'ace/mode/text_highlight_rules'],
    function(require, exports, module) {

"use strict";


var SimpHighlightRules = function() {
    this.$rules = {
        'start' : [
            { token: 'comment.assembly',
              regex: '#.*$'
            },
            { token: 'keyword.control.assembly',
              regex: keywordRegex,
              caseInsensitive: false
            },
            { token: ['variable.parameter.register.assembly', // match (literally) $
                      'variable.parameter.register.assembly'],// match register name
              regex: registerRegex,
              caseInsensitive: false
            },
            { token: ['support.function.directive.assembly', // match (literally) .
                      'support.function.directive.assembly'],// match directive
              regex: directiveRegex,
              caseInsensitive: false
            },
            { token: 'constant.character.decimal.assembly',
              regex: '\\b[0-9]+\\b',
            },
            { token: 'constant.character.hexadecimal.assembly',
              regex: '\\b0[xX][a-fA-F0-9]+\\b',
              caseInsensitive: false
            },
            { token: 'string.assembly',
              regex: /"([^\\"]|\\.)*"/
            },
            { token: ['entity.name.function.assembly', // match label name
                      'entity.name.function.assembly'],// match (literally) :
              regex: '\\b([a-zA-Z_][a-zA-Z_]*)(:)'
            },
            { token: 'punctuation.operator',
              regex: '[,+-]',
            },
            { token: 'paren.lparen',
              regex: '[(]',
            },
            { token: 'paren.rparen',
              regex: '[)]',
            },
            // unrecognised text is assumed to be a label
            { token: 'entity.name.function.assembly',
              regex: '\\b([a-zA-Z_][a-zA-Z0-9_]*)',
            }
        ]
    };

    this.normalizeRules();
};

SimpHighlightRules.metaData = {
    name:      'Simp Assembly',
    scopeName: 'source.assembly'
};


var oop = require('../lib/oop');
var TextHighlightRules = require('./text_highlight_rules').TextHighlightRules;
oop.inherits(SimpHighlightRules, TextHighlightRules);

exports.SimpHighlightRules = SimpHighlightRules;

});



// define the folding rules for the mode
ace.define(
    'ace/mode/folding/simp_fold_mode',
    ['require', 'exports', 'module', 'ace/lib/oop',
     'ace/mode/folding/fold_mode', 'ace/range'],
    function(require, exports, module) {

"use strict";

var oop = require("../../lib/oop");
var Range = require("../../range").Range;
var BaseFoldMode = require("./fold_mode").FoldMode;

var FoldMode = exports.FoldMode = function() {};
oop.inherits(FoldMode, BaseFoldMode);

(function() {

    // the start and end markers are the same regex, but they must be the same
    // group (ie if the start is a segment directive, the end cannot be a label)
    this.foldingStartMarker = /([^.]*\.(?:data|text).*)|(^(?:\s|[^#])*[a-zA-Z_][a-zA-Z_]*[:].*)/;
    this.foldingStopMarker = this.foldingStartMarker;

    this.getFoldWidgetRange = function(session, foldStyle, row) {
        var line = session.getLine(row);

        var match = line.match(this.foldingStartMarker);
        // match[0] is the whole regex, match[1] and higher are the matched groups.

        if(match) {
            var matchSegment = (match[1] !== undefined);

            var startColumn = line.length;
            var maxRow   = session.getLength();
            var startRow = row;
            var endRow   = row;

            while(++row < maxRow) {
                var endMatch = session.getLine(row).match(this.foldingStopMarker);

                if(endMatch && ((
                    matchSegment && endMatch[1]
                  ) || (
                    !matchSegment && endMatch[2]
                  )))
                    break;

                endRow = row;
            }

            if(endRow > startRow) {
                // roll back until a line that isn't a comment is found
                // this allows documentation comments before the label
                var re = /^\s*#.*/
                while(session.getLine(endRow).match(re))
                    endRow--;

                var endColumn = session.getLine(endRow).length;
                return new Range(startRow, startColumn, endRow, endColumn);
            }
        }
    };

}).call(FoldMode.prototype);

});


// define the mode
ace.define(
    'ace/mode/simp',
    ['require', 'exports', 'module', 'ace/lib/oop', 'ace/mode/text',
     'ace/mode/simp_highlight_rules'],
    function(require, exports, module) {

"use strict";

var oop = require("../lib/oop");

var TextMode  = require("./text").Mode;

var SimpHighlightRules = require("./simp_highlight_rules").SimpHighlightRules;
var SimpFoldMode = require("./folding/simp_fold_mode").FoldMode;

var SimpMode = function() {
    this.HighlightRules = SimpHighlightRules;
    this.foldingRules = new SimpFoldMode();
};
oop.inherits(SimpMode, TextMode);

(function() {
    this.lineCommentStart = '#';

    this.createWorker = function(session) {
        var worker = new WorkerClient(["ace"], "ace/mode/simp_worker", "SimpWorker");
        worker.attachToDocument(session.getDocument());
        worker.on("errors", function(e) {
            session.setAnnotations(e.data);
        });
        return worker;
    };

}).call(SimpMode.prototype);

exports.Mode = SimpMode;
});


