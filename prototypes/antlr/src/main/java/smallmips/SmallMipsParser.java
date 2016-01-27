package smallmips;
// Generated from SmallMips.g4 by ANTLR 4.5
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SmallMipsParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, OPCODE=9, 
		REGISTERID=10, ASSEMBLEROPCODE=11, ANNOTATION=12, STRING=13, NAME=14, 
		NUMBER=15, COMMENT=16, EOL=17, WS=18;
	public static final int
		RULE_prog = 0, RULE_line = 1, RULE_instruction = 2, RULE_assemblerinstruction = 3, 
		RULE_register = 4, RULE_argumentlist = 5, RULE_argument = 6;
	public static final String[] ruleNames = {
		"prog", "line", "instruction", "assemblerinstruction", "register", "argumentlist", 
		"argument"
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

	@Override
	public String getGrammarFileName() { return "SmallMips.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SmallMipsParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgContext extends ParserRuleContext {
		public List<TerminalNode> EOL() { return getTokens(SmallMipsParser.EOL); }
		public TerminalNode EOL(int i) {
			return getToken(SmallMipsParser.EOL, i);
		}
		public List<LineContext> line() {
			return getRuleContexts(LineContext.class);
		}
		public LineContext line(int i) {
			return getRuleContext(LineContext.class,i);
		}
		public ProgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prog; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SmallMipsListener ) ((SmallMipsListener)listener).enterProg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SmallMipsListener ) ((SmallMipsListener)listener).exitProg(this);
		}
	}

	public final ProgContext prog() throws RecognitionException {
		ProgContext _localctx = new ProgContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_prog);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(15);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__3) | (1L << T__6) | (1L << OPCODE) | (1L << ASSEMBLEROPCODE) | (1L << STRING) | (1L << NAME) | (1L << NUMBER) | (1L << COMMENT))) != 0)) {
					{
					setState(14);
					line();
					}
				}

				setState(17);
				match(EOL);
				}
				}
				setState(20); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__3) | (1L << T__6) | (1L << OPCODE) | (1L << ASSEMBLEROPCODE) | (1L << STRING) | (1L << NAME) | (1L << NUMBER) | (1L << COMMENT) | (1L << EOL))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LineContext extends ParserRuleContext {
		public TerminalNode COMMENT() { return getToken(SmallMipsParser.COMMENT, 0); }
		public InstructionContext instruction() {
			return getRuleContext(InstructionContext.class,0);
		}
		public AssemblerinstructionContext assemblerinstruction() {
			return getRuleContext(AssemblerinstructionContext.class,0);
		}
		public TerminalNode NAME() { return getToken(SmallMipsParser.NAME, 0); }
		public LineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SmallMipsListener ) ((SmallMipsListener)listener).enterLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SmallMipsListener ) ((SmallMipsListener)listener).exitLine(this);
		}
	}

	public final LineContext line() throws RecognitionException {
		LineContext _localctx = new LineContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_line);
		try {
			setState(27);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(22);
				match(COMMENT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(23);
				instruction();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(24);
				assemblerinstruction();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(25);
				match(NAME);
				setState(26);
				match(T__0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InstructionContext extends ParserRuleContext {
		public TerminalNode OPCODE() { return getToken(SmallMipsParser.OPCODE, 0); }
		public ArgumentlistContext argumentlist() {
			return getRuleContext(ArgumentlistContext.class,0);
		}
		public TerminalNode ANNOTATION() { return getToken(SmallMipsParser.ANNOTATION, 0); }
		public TerminalNode COMMENT() { return getToken(SmallMipsParser.COMMENT, 0); }
		public InstructionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instruction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SmallMipsListener ) ((SmallMipsListener)listener).enterInstruction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SmallMipsListener ) ((SmallMipsListener)listener).exitInstruction(this);
		}
	}

	public final InstructionContext instruction() throws RecognitionException {
		InstructionContext _localctx = new InstructionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_instruction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(29);
			match(OPCODE);
			setState(31);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__3) | (1L << T__6) | (1L << STRING) | (1L << NAME) | (1L << NUMBER))) != 0)) {
				{
				setState(30);
				argumentlist();
				}
			}

			setState(34);
			_la = _input.LA(1);
			if (_la==ANNOTATION) {
				{
				setState(33);
				match(ANNOTATION);
				}
			}

			setState(37);
			_la = _input.LA(1);
			if (_la==COMMENT) {
				{
				setState(36);
				match(COMMENT);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssemblerinstructionContext extends ParserRuleContext {
		public TerminalNode ASSEMBLEROPCODE() { return getToken(SmallMipsParser.ASSEMBLEROPCODE, 0); }
		public ArgumentContext argument() {
			return getRuleContext(ArgumentContext.class,0);
		}
		public ArgumentlistContext argumentlist() {
			return getRuleContext(ArgumentlistContext.class,0);
		}
		public TerminalNode ANNOTATION() { return getToken(SmallMipsParser.ANNOTATION, 0); }
		public TerminalNode COMMENT() { return getToken(SmallMipsParser.COMMENT, 0); }
		public AssemblerinstructionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assemblerinstruction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SmallMipsListener ) ((SmallMipsListener)listener).enterAssemblerinstruction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SmallMipsListener ) ((SmallMipsListener)listener).exitAssemblerinstruction(this);
		}
	}

	public final AssemblerinstructionContext assemblerinstruction() throws RecognitionException {
		AssemblerinstructionContext _localctx = new AssemblerinstructionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_assemblerinstruction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__3) | (1L << T__6) | (1L << STRING) | (1L << NAME) | (1L << NUMBER))) != 0)) {
				{
				setState(39);
				argument();
				}
			}

			setState(42);
			match(ASSEMBLEROPCODE);
			setState(44);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__3) | (1L << T__6) | (1L << STRING) | (1L << NAME) | (1L << NUMBER))) != 0)) {
				{
				setState(43);
				argumentlist();
				}
			}

			setState(47);
			_la = _input.LA(1);
			if (_la==ANNOTATION) {
				{
				setState(46);
				match(ANNOTATION);
				}
			}

			setState(50);
			_la = _input.LA(1);
			if (_la==COMMENT) {
				{
				setState(49);
				match(COMMENT);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RegisterContext extends ParserRuleContext {
		public TerminalNode REGISTERID() { return getToken(SmallMipsParser.REGISTERID, 0); }
		public RegisterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_register; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SmallMipsListener ) ((SmallMipsListener)listener).enterRegister(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SmallMipsListener ) ((SmallMipsListener)listener).exitRegister(this);
		}
	}

	public final RegisterContext register() throws RecognitionException {
		RegisterContext _localctx = new RegisterContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_register);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(T__1);
			setState(53);
			match(REGISTERID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgumentlistContext extends ParserRuleContext {
		public ArgumentContext argument() {
			return getRuleContext(ArgumentContext.class,0);
		}
		public ArgumentlistContext argumentlist() {
			return getRuleContext(ArgumentlistContext.class,0);
		}
		public ArgumentlistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentlist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SmallMipsListener ) ((SmallMipsListener)listener).enterArgumentlist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SmallMipsListener ) ((SmallMipsListener)listener).exitArgumentlist(this);
		}
	}

	public final ArgumentlistContext argumentlist() throws RecognitionException {
		ArgumentlistContext _localctx = new ArgumentlistContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_argumentlist);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			argument();
			setState(58);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(56);
				match(T__2);
				setState(57);
				argumentlist();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArgumentContext extends ParserRuleContext {
		public List<RegisterContext> register() {
			return getRuleContexts(RegisterContext.class);
		}
		public RegisterContext register(int i) {
			return getRuleContext(RegisterContext.class,i);
		}
		public TerminalNode NAME() { return getToken(SmallMipsParser.NAME, 0); }
		public TerminalNode STRING() { return getToken(SmallMipsParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(SmallMipsParser.NUMBER, 0); }
		public ArgumentContext argument() {
			return getRuleContext(ArgumentContext.class,0);
		}
		public ArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SmallMipsListener ) ((SmallMipsListener)listener).enterArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SmallMipsListener ) ((SmallMipsListener)listener).exitArgument(this);
		}
	}

	public final ArgumentContext argument() throws RecognitionException {
		ArgumentContext _localctx = new ArgumentContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_argument);
		int _la;
		try {
			setState(75);
			switch (_input.LA(1)) {
			case T__1:
			case T__3:
			case STRING:
			case NAME:
			case NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(65);
				switch (_input.LA(1)) {
				case T__1:
					{
					setState(60);
					register();
					}
					break;
				case NAME:
					{
					setState(61);
					match(NAME);
					}
					break;
				case STRING:
					{
					setState(62);
					match(STRING);
					}
					break;
				case NUMBER:
					{
					setState(63);
					match(NUMBER);
					}
					break;
				case T__3:
					{
					setState(64);
					match(T__3);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(69);
				_la = _input.LA(1);
				if (_la==T__4 || _la==T__5) {
					{
					setState(67);
					_la = _input.LA(1);
					if ( !(_la==T__4 || _la==T__5) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					setState(68);
					register();
					}
				}

				}
				break;
			case T__6:
				enterOuterAlt(_localctx, 2);
				{
				setState(71);
				match(T__6);
				setState(72);
				argument();
				setState(73);
				match(T__7);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\24P\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\5\2\22\n\2\3\2\6\2\25\n"+
		"\2\r\2\16\2\26\3\3\3\3\3\3\3\3\3\3\5\3\36\n\3\3\4\3\4\5\4\"\n\4\3\4\5"+
		"\4%\n\4\3\4\5\4(\n\4\3\5\5\5+\n\5\3\5\3\5\5\5/\n\5\3\5\5\5\62\n\5\3\5"+
		"\5\5\65\n\5\3\6\3\6\3\6\3\7\3\7\3\7\5\7=\n\7\3\b\3\b\3\b\3\b\3\b\5\bD"+
		"\n\b\3\b\3\b\5\bH\n\b\3\b\3\b\3\b\3\b\5\bN\n\b\3\b\2\2\t\2\4\6\b\n\f\16"+
		"\2\3\3\2\7\b[\2\24\3\2\2\2\4\35\3\2\2\2\6\37\3\2\2\2\b*\3\2\2\2\n\66\3"+
		"\2\2\2\f9\3\2\2\2\16M\3\2\2\2\20\22\5\4\3\2\21\20\3\2\2\2\21\22\3\2\2"+
		"\2\22\23\3\2\2\2\23\25\7\23\2\2\24\21\3\2\2\2\25\26\3\2\2\2\26\24\3\2"+
		"\2\2\26\27\3\2\2\2\27\3\3\2\2\2\30\36\7\22\2\2\31\36\5\6\4\2\32\36\5\b"+
		"\5\2\33\34\7\20\2\2\34\36\7\3\2\2\35\30\3\2\2\2\35\31\3\2\2\2\35\32\3"+
		"\2\2\2\35\33\3\2\2\2\36\5\3\2\2\2\37!\7\13\2\2 \"\5\f\7\2! \3\2\2\2!\""+
		"\3\2\2\2\"$\3\2\2\2#%\7\16\2\2$#\3\2\2\2$%\3\2\2\2%\'\3\2\2\2&(\7\22\2"+
		"\2\'&\3\2\2\2\'(\3\2\2\2(\7\3\2\2\2)+\5\16\b\2*)\3\2\2\2*+\3\2\2\2+,\3"+
		"\2\2\2,.\7\r\2\2-/\5\f\7\2.-\3\2\2\2./\3\2\2\2/\61\3\2\2\2\60\62\7\16"+
		"\2\2\61\60\3\2\2\2\61\62\3\2\2\2\62\64\3\2\2\2\63\65\7\22\2\2\64\63\3"+
		"\2\2\2\64\65\3\2\2\2\65\t\3\2\2\2\66\67\7\4\2\2\678\7\f\2\28\13\3\2\2"+
		"\29<\5\16\b\2:;\7\5\2\2;=\5\f\7\2<:\3\2\2\2<=\3\2\2\2=\r\3\2\2\2>D\5\n"+
		"\6\2?D\7\20\2\2@D\7\17\2\2AD\7\21\2\2BD\7\6\2\2C>\3\2\2\2C?\3\2\2\2C@"+
		"\3\2\2\2CA\3\2\2\2CB\3\2\2\2DG\3\2\2\2EF\t\2\2\2FH\5\n\6\2GE\3\2\2\2G"+
		"H\3\2\2\2HN\3\2\2\2IJ\7\t\2\2JK\5\16\b\2KL\7\n\2\2LN\3\2\2\2MC\3\2\2\2"+
		"MI\3\2\2\2N\17\3\2\2\2\20\21\26\35!$\'*.\61\64<CGM";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}