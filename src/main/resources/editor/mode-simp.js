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


// see: https://github.com/ajaxorg/ace/wiki/Creating-or-Extending-an-Edit-Mode
var SimpHighlightRules = function() {
    // after an instruction enter 'operands' state until ; or EOL but these rules apply
    // to both states
    var everywhereRules = [
        { token: ['entity.name.function.assembly', // match label name
                  'entity.name.function.assembly'],// match (literally) :
          regex: '\\b([a-zA-Z_][a-zA-Z_]*)(\\s*:)'
        },
        { token: 'comment.assembly', // comment without @ (definitely does not contain an annotation)
          regex: '#[^@]*$',
        },
        { token: 'comment.assembly',
          regex: '#',
          next:  'comment' // check for annotations in comment body
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
    ];

    var startRules = everywhereRules.slice(0); // make a copy of everywhereRules
    // required: insert instructions after labels (index 0)
    startRules.splice(1/*index*/, 0/*items to delete*/, {
        token: 'keyword.control.assembly', // instruction
        regex: keywordRegex,
        caseInsensitive: false,
        next: 'operands'
    });

    var operandRules = everywhereRules; // no need to make a copy the second time. modify everywhereRules
    // note about matching ^: Instructions match the word boundaries around them so for
    // "syscall\nb:b b" to parse correctly, must match beginning of line as well as end
    operandRules.splice(0/*index*/, 0/*items to delete*/, {
        token: 'end',
        regex: '($|^|;)',
        next: 'start'
    });

    this.$rules = {
        'start' : startRules,
        'comment' : [
            { token: 'comment',
              regex: '([^@]*|@)$', // comment body up until EOL
              next:  'start'
            },
            { token: 'comment',
              regex: '([^@]|(@[^\{]))*' // normal comment body up to an annotation
            }
        ],
        'operands' : operandRules
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
    this.foldingStartMarker = /(^[^.#]*\.(?:data|text).*)|(^(?:\s|[^#])*[a-zA-Z_][a-zA-Z_]*\s*[:].*)/;
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
var JavaScriptHighlightRules =
    require("./javascript_highlight_rules").JavaScriptHighlightRules;
var SimpFoldMode = require("./folding/simp_fold_mode").FoldMode;


// combined Simp + annotation highlight rules
var DocumentHighlightRules = function() {
    SimpHighlightRules.call(this);

    var startRules = [
        { token: 'support.function.directive.assembly.annotation',
          regex: '@\{',
          push: 'js-start'
        }
    ];

    // applies anywhere a javascript identifier is recognised
    var JSExtraRules = [
        { token: ['variable.parameter.register.assembly', // match (literally) $
                  'variable.parameter.register.assembly'],// match register name
          regex: registerRegex,
        }
    ];

    // in the example below only the $1's are highlighted as registers, but not the $0's
    // # @{ var $1 = $1.get() + "$0.get()"; // $0.get() }@
    // # @{ function $0($1) { if($1 == /*$0*/ /$0/) return $1; } }@


    var JSEndRules = [
        { token: 'paren.rparen',
          regex: '\}[^@]', // un-matched curly brace (not an annotation end marker)
        },
        { token: 'support.function.directive.assembly.annotation',
          regex: '\}@$', // annotation right up to EOL
          next:  'start'
        },
        { token: 'support.function.directive.assembly.annotation',
          regex: '\}@', // annotation with comment after
          next:  'comment'
        },
        // this isn't allowed, but if it happens, indicates to the user that
        // they are back in Simp
        { token: '',
          regex: '$',
          next:  'start'
        }
    ];

    // cannot use embedRules for the Ending rules because that lets the JS rules
    // take precedence, which is not appropriate as newlines and } have to be
    // superseded.

    //this.embedRules(JSExtraRules, 'js-', undefined, ['start'], false);

    // this manually performs an action like embedRules, but placing the end rules
    // before the javascript ones
    {
        // jsRules :: {state : [{rule}]}
        var jsRules = new JavaScriptHighlightRules().getRules();

        // prepend the EndRules to every Javascript state to exit at any time
        for(var r in jsRules) {
            jsRules[r].unshift(JSEndRules); // prepend
        }

        // from reading https://github.com/ajaxorg/ace/blob/master/lib/ace/mode/javascript_highlight_rules.js
        // placing JSExtra rules in the states where the variable 'identifierRe' is used
        // this excludes things like strings, literal regex, comments etc
        jsRules['no_regex'].unshift(JSExtraRules);
        jsRules['function_arguments'].unshift(JSExtraRules);

        this.addRules(jsRules, 'js-');
    }


    // able to enter js mode from comments only
    this.$rules['comment'].unshift.apply(this.$rules['comment'], startRules);

    this.normalizeRules();
}
oop.inherits(DocumentHighlightRules, SimpHighlightRules);

var SimpMode = function() {
    this.HighlightRules = DocumentHighlightRules;
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


