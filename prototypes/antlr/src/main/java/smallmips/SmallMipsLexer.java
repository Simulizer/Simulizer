package smallmips;


// Generated from SmallMips.g4 by ANTLR 4.5
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SmallMipsLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, OPCODE=9, 
		REGISTERID=10, ASSEMBLEROPCODE=11, ANNOTATION=12, STRING=13, NAME=14, 
		NUMBER=15, COMMENT=16, EOL=17, WS=18;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "OPCODE", 
		"REGISTERID", "ASSEMBLEROPCODE", "ANNOTATION", "STRING", "NAME", "NUMBER", 
		"COMMENT", "EOL", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "':'", "'$'", "','", "'*'", "'+'", "'-'", "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, "OPCODE", "REGISTERID", 
		"ASSEMBLEROPCODE", "ANNOTATION", "STRING", "NAME", "NUMBER", "COMMENT", 
		"EOL", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public SmallMipsLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SmallMips.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\24\u00d5\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3"+
		"\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\nQ\n\n\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\5\13\u0093\n\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\f\5\f\u00a4\n\f\3\r\3\r\7\r\u00a8\n\r\f\r\16"+
		"\r\u00ab\13\r\3\r\3\r\3\16\3\16\7\16\u00b1\n\16\f\16\16\16\u00b4\13\16"+
		"\3\16\3\16\3\17\3\17\7\17\u00ba\n\17\f\17\16\17\u00bd\13\17\3\20\6\20"+
		"\u00c0\n\20\r\20\16\20\u00c1\3\21\3\21\7\21\u00c6\n\21\f\21\16\21\u00c9"+
		"\13\21\3\21\3\21\3\22\5\22\u00ce\n\22\3\22\3\22\3\23\3\23\3\23\3\23\2"+
		"\2\24\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35"+
		"\20\37\21!\22#\23%\24\3\2\b\4\2\f\f\17\17\3\2$$\4\2C\\c|\5\2\62;C\\c|"+
		"\3\2\62;\4\2\13\13\"\"\u0105\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3"+
		"\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2"+
		"\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37"+
		"\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\3\'\3\2\2\2\5)\3\2\2\2\7+\3"+
		"\2\2\2\t-\3\2\2\2\13/\3\2\2\2\r\61\3\2\2\2\17\63\3\2\2\2\21\65\3\2\2\2"+
		"\23P\3\2\2\2\25\u0092\3\2\2\2\27\u00a3\3\2\2\2\31\u00a5\3\2\2\2\33\u00ae"+
		"\3\2\2\2\35\u00b7\3\2\2\2\37\u00bf\3\2\2\2!\u00c3\3\2\2\2#\u00cd\3\2\2"+
		"\2%\u00d1\3\2\2\2\'(\7<\2\2(\4\3\2\2\2)*\7&\2\2*\6\3\2\2\2+,\7.\2\2,\b"+
		"\3\2\2\2-.\7,\2\2.\n\3\2\2\2/\60\7-\2\2\60\f\3\2\2\2\61\62\7/\2\2\62\16"+
		"\3\2\2\2\63\64\7*\2\2\64\20\3\2\2\2\65\66\7+\2\2\66\22\3\2\2\2\678\7c"+
		"\2\289\7f\2\29Q\7f\2\2:;\7u\2\2;<\7w\2\2<Q\7d\2\2=>\7o\2\2>?\7w\2\2?@"+
		"\7n\2\2@Q\7v\2\2AB\7f\2\2BC\7k\2\2CQ\7x\2\2DE\7n\2\2EQ\7y\2\2FG\7c\2\2"+
		"GH\7p\2\2HQ\7f\2\2IJ\7q\2\2JQ\7t\2\2KL\7z\2\2LM\7q\2\2MQ\7t\2\2NO\7n\2"+
		"\2OQ\7k\2\2P\67\3\2\2\2P:\3\2\2\2P=\3\2\2\2PA\3\2\2\2PD\3\2\2\2PF\3\2"+
		"\2\2PI\3\2\2\2PK\3\2\2\2PN\3\2\2\2Q\24\3\2\2\2RS\7|\2\2ST\7g\2\2TU\7t"+
		"\2\2U\u0093\7q\2\2VW\7c\2\2W\u0093\7v\2\2XY\7x\2\2Y\u0093\7\62\2\2Z[\7"+
		"x\2\2[\u0093\7\63\2\2\\]\7c\2\2]\u0093\7\62\2\2^_\7c\2\2_\u0093\7\63\2"+
		"\2`a\7c\2\2a\u0093\7\64\2\2bc\7c\2\2c\u0093\7\65\2\2de\7v\2\2e\u0093\7"+
		"\62\2\2fg\7v\2\2g\u0093\7\63\2\2hi\7v\2\2i\u0093\7\64\2\2jk\7v\2\2k\u0093"+
		"\7\65\2\2lm\7v\2\2m\u0093\7\66\2\2no\7v\2\2o\u0093\7\67\2\2pq\7v\2\2q"+
		"\u0093\78\2\2rs\7v\2\2s\u0093\79\2\2tu\7v\2\2u\u0093\7:\2\2vw\7v\2\2w"+
		"\u0093\7;\2\2xy\7u\2\2y\u0093\7\62\2\2z{\7u\2\2{\u0093\7\63\2\2|}\7u\2"+
		"\2}\u0093\7\64\2\2~\177\7u\2\2\177\u0093\7\65\2\2\u0080\u0081\7u\2\2\u0081"+
		"\u0093\7\66\2\2\u0082\u0083\7u\2\2\u0083\u0093\7\67\2\2\u0084\u0085\7"+
		"u\2\2\u0085\u0093\78\2\2\u0086\u0087\7u\2\2\u0087\u0093\79\2\2\u0088\u0089"+
		"\7m\2\2\u0089\u0093\7\62\2\2\u008a\u008b\7m\2\2\u008b\u0093\7\63\2\2\u008c"+
		"\u008d\7i\2\2\u008d\u0093\7r\2\2\u008e\u008f\7h\2\2\u008f\u0093\7r\2\2"+
		"\u0090\u0091\7t\2\2\u0091\u0093\7c\2\2\u0092R\3\2\2\2\u0092V\3\2\2\2\u0092"+
		"X\3\2\2\2\u0092Z\3\2\2\2\u0092\\\3\2\2\2\u0092^\3\2\2\2\u0092`\3\2\2\2"+
		"\u0092b\3\2\2\2\u0092d\3\2\2\2\u0092f\3\2\2\2\u0092h\3\2\2\2\u0092j\3"+
		"\2\2\2\u0092l\3\2\2\2\u0092n\3\2\2\2\u0092p\3\2\2\2\u0092r\3\2\2\2\u0092"+
		"t\3\2\2\2\u0092v\3\2\2\2\u0092x\3\2\2\2\u0092z\3\2\2\2\u0092|\3\2\2\2"+
		"\u0092~\3\2\2\2\u0092\u0080\3\2\2\2\u0092\u0082\3\2\2\2\u0092\u0084\3"+
		"\2\2\2\u0092\u0086\3\2\2\2\u0092\u0088\3\2\2\2\u0092\u008a\3\2\2\2\u0092"+
		"\u008c\3\2\2\2\u0092\u008e\3\2\2\2\u0092\u0090\3\2\2\2\u0093\26\3\2\2"+
		"\2\u0094\u0095\7Q\2\2\u0095\u0096\7T\2\2\u0096\u00a4\7I\2\2\u0097\u0098"+
		"\7G\2\2\u0098\u0099\7S\2\2\u0099\u00a4\7W\2\2\u009a\u009b\7C\2\2\u009b"+
		"\u009c\7U\2\2\u009c\u00a4\7E\2\2\u009d\u009e\7F\2\2\u009e\u00a4\7U\2\2"+
		"\u009f\u00a0\7F\2\2\u00a0\u00a1\7H\2\2\u00a1\u00a4\7E\2\2\u00a2\u00a4"+
		"\7?\2\2\u00a3\u0094\3\2\2\2\u00a3\u0097\3\2\2\2\u00a3\u009a\3\2\2\2\u00a3"+
		"\u009d\3\2\2\2\u00a3\u009f\3\2\2\2\u00a3\u00a2\3\2\2\2\u00a4\30\3\2\2"+
		"\2\u00a5\u00a9\7B\2\2\u00a6\u00a8\n\2\2\2\u00a7\u00a6\3\2\2\2\u00a8\u00ab"+
		"\3\2\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00ac\3\2\2\2\u00ab"+
		"\u00a9\3\2\2\2\u00ac\u00ad\b\r\2\2\u00ad\32\3\2\2\2\u00ae\u00b2\7$\2\2"+
		"\u00af\u00b1\n\3\2\2\u00b0\u00af\3\2\2\2\u00b1\u00b4\3\2\2\2\u00b2\u00b0"+
		"\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\u00b5\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b5"+
		"\u00b6\7$\2\2\u00b6\34\3\2\2\2\u00b7\u00bb\t\4\2\2\u00b8\u00ba\t\5\2\2"+
		"\u00b9\u00b8\3\2\2\2\u00ba\u00bd\3\2\2\2\u00bb\u00b9\3\2\2\2\u00bb\u00bc"+
		"\3\2\2\2\u00bc\36\3\2\2\2\u00bd\u00bb\3\2\2\2\u00be\u00c0\t\6\2\2\u00bf"+
		"\u00be\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c1\u00c2\3\2"+
		"\2\2\u00c2 \3\2\2\2\u00c3\u00c7\7%\2\2\u00c4\u00c6\n\2\2\2\u00c5\u00c4"+
		"\3\2\2\2\u00c6\u00c9\3\2\2\2\u00c7\u00c5\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8"+
		"\u00ca\3\2\2\2\u00c9\u00c7\3\2\2\2\u00ca\u00cb\b\21\2\2\u00cb\"\3\2\2"+
		"\2\u00cc\u00ce\7\17\2\2\u00cd\u00cc\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce"+
		"\u00cf\3\2\2\2\u00cf\u00d0\7\f\2\2\u00d0$\3\2\2\2\u00d1\u00d2\t\7\2\2"+
		"\u00d2\u00d3\3\2\2\2\u00d3\u00d4\b\23\2\2\u00d4&\3\2\2\2\f\2P\u0092\u00a3"+
		"\u00a9\u00b2\u00bb\u00c1\u00c7\u00cd\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}