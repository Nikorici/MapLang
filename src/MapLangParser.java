// Generated from MapLang.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MapLangParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		MAP=1, SIZE=2, TILE=3, PLAYER=4, AT=5, LBRACE=6, RBRACE=7, LPAREN=8, RPAREN=9, 
		COMMA=10, SEMI=11, ID=12, NUMBER=13, WS=14;
	public static final int
		RULE_program = 0, RULE_mapDecl = 1, RULE_mapBody = 2, RULE_sizeDecl = 3, 
		RULE_statement = 4, RULE_tileStmt = 5, RULE_playerStmt = 6;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "mapDecl", "mapBody", "sizeDecl", "statement", "tileStmt", 
			"playerStmt"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'map'", "'size'", "'tile'", "'player'", "'at'", "'{'", "'}'", 
			"'('", "')'", "','", "';'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "MAP", "SIZE", "TILE", "PLAYER", "AT", "LBRACE", "RBRACE", "LPAREN", 
			"RPAREN", "COMMA", "SEMI", "ID", "NUMBER", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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
	public String getGrammarFileName() { return "MapLang.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MapLangParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramContext extends ParserRuleContext {
		public MapDeclContext mapDecl() {
			return getRuleContext(MapDeclContext.class,0);
		}
		public TerminalNode EOF() { return getToken(MapLangParser.EOF, 0); }
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MapLangListener ) ((MapLangListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MapLangListener ) ((MapLangListener)listener).exitProgram(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MapLangVisitor ) return ((MapLangVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(14);
			mapDecl();
			setState(15);
			match(EOF);
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

	@SuppressWarnings("CheckReturnValue")
	public static class MapDeclContext extends ParserRuleContext {
		public TerminalNode MAP() { return getToken(MapLangParser.MAP, 0); }
		public TerminalNode ID() { return getToken(MapLangParser.ID, 0); }
		public TerminalNode LBRACE() { return getToken(MapLangParser.LBRACE, 0); }
		public MapBodyContext mapBody() {
			return getRuleContext(MapBodyContext.class,0);
		}
		public TerminalNode RBRACE() { return getToken(MapLangParser.RBRACE, 0); }
		public MapDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mapDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MapLangListener ) ((MapLangListener)listener).enterMapDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MapLangListener ) ((MapLangListener)listener).exitMapDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MapLangVisitor ) return ((MapLangVisitor<? extends T>)visitor).visitMapDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MapDeclContext mapDecl() throws RecognitionException {
		MapDeclContext _localctx = new MapDeclContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_mapDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(17);
			match(MAP);
			setState(18);
			match(ID);
			setState(19);
			match(LBRACE);
			setState(20);
			mapBody();
			setState(21);
			match(RBRACE);
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

	@SuppressWarnings("CheckReturnValue")
	public static class MapBodyContext extends ParserRuleContext {
		public SizeDeclContext sizeDecl() {
			return getRuleContext(SizeDeclContext.class,0);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public MapBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mapBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MapLangListener ) ((MapLangListener)listener).enterMapBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MapLangListener ) ((MapLangListener)listener).exitMapBody(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MapLangVisitor ) return ((MapLangVisitor<? extends T>)visitor).visitMapBody(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MapBodyContext mapBody() throws RecognitionException {
		MapBodyContext _localctx = new MapBodyContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_mapBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(23);
			sizeDecl();
			setState(27);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TILE || _la==PLAYER) {
				{
				{
				setState(24);
				statement();
				}
				}
				setState(29);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	@SuppressWarnings("CheckReturnValue")
	public static class SizeDeclContext extends ParserRuleContext {
		public TerminalNode SIZE() { return getToken(MapLangParser.SIZE, 0); }
		public List<TerminalNode> NUMBER() { return getTokens(MapLangParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(MapLangParser.NUMBER, i);
		}
		public TerminalNode COMMA() { return getToken(MapLangParser.COMMA, 0); }
		public TerminalNode SEMI() { return getToken(MapLangParser.SEMI, 0); }
		public SizeDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sizeDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MapLangListener ) ((MapLangListener)listener).enterSizeDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MapLangListener ) ((MapLangListener)listener).exitSizeDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MapLangVisitor ) return ((MapLangVisitor<? extends T>)visitor).visitSizeDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SizeDeclContext sizeDecl() throws RecognitionException {
		SizeDeclContext _localctx = new SizeDeclContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_sizeDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			match(SIZE);
			setState(31);
			match(NUMBER);
			setState(32);
			match(COMMA);
			setState(33);
			match(NUMBER);
			setState(34);
			match(SEMI);
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

	@SuppressWarnings("CheckReturnValue")
	public static class StatementContext extends ParserRuleContext {
		public TileStmtContext tileStmt() {
			return getRuleContext(TileStmtContext.class,0);
		}
		public PlayerStmtContext playerStmt() {
			return getRuleContext(PlayerStmtContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MapLangListener ) ((MapLangListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MapLangListener ) ((MapLangListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MapLangVisitor ) return ((MapLangVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_statement);
		try {
			setState(38);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TILE:
				enterOuterAlt(_localctx, 1);
				{
				setState(36);
				tileStmt();
				}
				break;
			case PLAYER:
				enterOuterAlt(_localctx, 2);
				{
				setState(37);
				playerStmt();
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

	@SuppressWarnings("CheckReturnValue")
	public static class TileStmtContext extends ParserRuleContext {
		public TerminalNode TILE() { return getToken(MapLangParser.TILE, 0); }
		public TerminalNode ID() { return getToken(MapLangParser.ID, 0); }
		public TerminalNode AT() { return getToken(MapLangParser.AT, 0); }
		public TerminalNode LPAREN() { return getToken(MapLangParser.LPAREN, 0); }
		public List<TerminalNode> NUMBER() { return getTokens(MapLangParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(MapLangParser.NUMBER, i);
		}
		public TerminalNode COMMA() { return getToken(MapLangParser.COMMA, 0); }
		public TerminalNode RPAREN() { return getToken(MapLangParser.RPAREN, 0); }
		public TerminalNode SEMI() { return getToken(MapLangParser.SEMI, 0); }
		public TileStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tileStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MapLangListener ) ((MapLangListener)listener).enterTileStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MapLangListener ) ((MapLangListener)listener).exitTileStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MapLangVisitor ) return ((MapLangVisitor<? extends T>)visitor).visitTileStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TileStmtContext tileStmt() throws RecognitionException {
		TileStmtContext _localctx = new TileStmtContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_tileStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			match(TILE);
			setState(41);
			match(ID);
			setState(42);
			match(AT);
			setState(43);
			match(LPAREN);
			setState(44);
			match(NUMBER);
			setState(45);
			match(COMMA);
			setState(46);
			match(NUMBER);
			setState(47);
			match(RPAREN);
			setState(48);
			match(SEMI);
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

	@SuppressWarnings("CheckReturnValue")
	public static class PlayerStmtContext extends ParserRuleContext {
		public TerminalNode PLAYER() { return getToken(MapLangParser.PLAYER, 0); }
		public TerminalNode AT() { return getToken(MapLangParser.AT, 0); }
		public TerminalNode LPAREN() { return getToken(MapLangParser.LPAREN, 0); }
		public List<TerminalNode> NUMBER() { return getTokens(MapLangParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(MapLangParser.NUMBER, i);
		}
		public TerminalNode COMMA() { return getToken(MapLangParser.COMMA, 0); }
		public TerminalNode RPAREN() { return getToken(MapLangParser.RPAREN, 0); }
		public TerminalNode SEMI() { return getToken(MapLangParser.SEMI, 0); }
		public PlayerStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_playerStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MapLangListener ) ((MapLangListener)listener).enterPlayerStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MapLangListener ) ((MapLangListener)listener).exitPlayerStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MapLangVisitor ) return ((MapLangVisitor<? extends T>)visitor).visitPlayerStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PlayerStmtContext playerStmt() throws RecognitionException {
		PlayerStmtContext _localctx = new PlayerStmtContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_playerStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			match(PLAYER);
			setState(51);
			match(AT);
			setState(52);
			match(LPAREN);
			setState(53);
			match(NUMBER);
			setState(54);
			match(COMMA);
			setState(55);
			match(NUMBER);
			setState(56);
			match(RPAREN);
			setState(57);
			match(SEMI);
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
		"\u0004\u0001\u000e<\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0002\u0001\u0002\u0005\u0002\u001a\b\u0002\n\u0002\f\u0002"+
		"\u001d\t\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0004\u0001\u0004\u0003\u0004\'\b\u0004\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0000\u0000\u0007\u0000\u0002\u0004\u0006\b\n\f\u0000\u0000"+
		"6\u0000\u000e\u0001\u0000\u0000\u0000\u0002\u0011\u0001\u0000\u0000\u0000"+
		"\u0004\u0017\u0001\u0000\u0000\u0000\u0006\u001e\u0001\u0000\u0000\u0000"+
		"\b&\u0001\u0000\u0000\u0000\n(\u0001\u0000\u0000\u0000\f2\u0001\u0000"+
		"\u0000\u0000\u000e\u000f\u0003\u0002\u0001\u0000\u000f\u0010\u0005\u0000"+
		"\u0000\u0001\u0010\u0001\u0001\u0000\u0000\u0000\u0011\u0012\u0005\u0001"+
		"\u0000\u0000\u0012\u0013\u0005\f\u0000\u0000\u0013\u0014\u0005\u0006\u0000"+
		"\u0000\u0014\u0015\u0003\u0004\u0002\u0000\u0015\u0016\u0005\u0007\u0000"+
		"\u0000\u0016\u0003\u0001\u0000\u0000\u0000\u0017\u001b\u0003\u0006\u0003"+
		"\u0000\u0018\u001a\u0003\b\u0004\u0000\u0019\u0018\u0001\u0000\u0000\u0000"+
		"\u001a\u001d\u0001\u0000\u0000\u0000\u001b\u0019\u0001\u0000\u0000\u0000"+
		"\u001b\u001c\u0001\u0000\u0000\u0000\u001c\u0005\u0001\u0000\u0000\u0000"+
		"\u001d\u001b\u0001\u0000\u0000\u0000\u001e\u001f\u0005\u0002\u0000\u0000"+
		"\u001f \u0005\r\u0000\u0000 !\u0005\n\u0000\u0000!\"\u0005\r\u0000\u0000"+
		"\"#\u0005\u000b\u0000\u0000#\u0007\u0001\u0000\u0000\u0000$\'\u0003\n"+
		"\u0005\u0000%\'\u0003\f\u0006\u0000&$\u0001\u0000\u0000\u0000&%\u0001"+
		"\u0000\u0000\u0000\'\t\u0001\u0000\u0000\u0000()\u0005\u0003\u0000\u0000"+
		")*\u0005\f\u0000\u0000*+\u0005\u0005\u0000\u0000+,\u0005\b\u0000\u0000"+
		",-\u0005\r\u0000\u0000-.\u0005\n\u0000\u0000./\u0005\r\u0000\u0000/0\u0005"+
		"\t\u0000\u000001\u0005\u000b\u0000\u00001\u000b\u0001\u0000\u0000\u0000"+
		"23\u0005\u0004\u0000\u000034\u0005\u0005\u0000\u000045\u0005\b\u0000\u0000"+
		"56\u0005\r\u0000\u000067\u0005\n\u0000\u000078\u0005\r\u0000\u000089\u0005"+
		"\t\u0000\u00009:\u0005\u000b\u0000\u0000:\r\u0001\u0000\u0000\u0000\u0002"+
		"\u001b&";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}