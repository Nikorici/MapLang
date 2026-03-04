// Generated from MapLang.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MapLangLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		MAP=1, SIZE=2, TILE=3, PLAYER=4, AT=5, LBRACE=6, RBRACE=7, LPAREN=8, RPAREN=9, 
		COMMA=10, SEMI=11, ID=12, NUMBER=13, WS=14;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"MAP", "SIZE", "TILE", "PLAYER", "AT", "LBRACE", "RBRACE", "LPAREN", 
			"RPAREN", "COMMA", "SEMI", "ID", "NUMBER", "WS"
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


	public MapLangLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "MapLang.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u000eT\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\t\u0001"+
		"\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0005\u000bD\b\u000b\n\u000b"+
		"\f\u000bG\t\u000b\u0001\f\u0004\fJ\b\f\u000b\f\f\fK\u0001\r\u0004\rO\b"+
		"\r\u000b\r\f\rP\u0001\r\u0001\r\u0000\u0000\u000e\u0001\u0001\u0003\u0002"+
		"\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013"+
		"\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u0001\u0000\u0004\u0003\u0000"+
		"AZ__az\u0004\u000009AZ__az\u0001\u000009\u0003\u0000\t\n\r\r  V\u0000"+
		"\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000"+
		"\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000"+
		"\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r"+
		"\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011"+
		"\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015"+
		"\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019"+
		"\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0001\u001d"+
		"\u0001\u0000\u0000\u0000\u0003!\u0001\u0000\u0000\u0000\u0005&\u0001\u0000"+
		"\u0000\u0000\u0007+\u0001\u0000\u0000\u0000\t2\u0001\u0000\u0000\u0000"+
		"\u000b5\u0001\u0000\u0000\u0000\r7\u0001\u0000\u0000\u0000\u000f9\u0001"+
		"\u0000\u0000\u0000\u0011;\u0001\u0000\u0000\u0000\u0013=\u0001\u0000\u0000"+
		"\u0000\u0015?\u0001\u0000\u0000\u0000\u0017A\u0001\u0000\u0000\u0000\u0019"+
		"I\u0001\u0000\u0000\u0000\u001bN\u0001\u0000\u0000\u0000\u001d\u001e\u0005"+
		"m\u0000\u0000\u001e\u001f\u0005a\u0000\u0000\u001f \u0005p\u0000\u0000"+
		" \u0002\u0001\u0000\u0000\u0000!\"\u0005s\u0000\u0000\"#\u0005i\u0000"+
		"\u0000#$\u0005z\u0000\u0000$%\u0005e\u0000\u0000%\u0004\u0001\u0000\u0000"+
		"\u0000&\'\u0005t\u0000\u0000\'(\u0005i\u0000\u0000()\u0005l\u0000\u0000"+
		")*\u0005e\u0000\u0000*\u0006\u0001\u0000\u0000\u0000+,\u0005p\u0000\u0000"+
		",-\u0005l\u0000\u0000-.\u0005a\u0000\u0000./\u0005y\u0000\u0000/0\u0005"+
		"e\u0000\u000001\u0005r\u0000\u00001\b\u0001\u0000\u0000\u000023\u0005"+
		"a\u0000\u000034\u0005t\u0000\u00004\n\u0001\u0000\u0000\u000056\u0005"+
		"{\u0000\u00006\f\u0001\u0000\u0000\u000078\u0005}\u0000\u00008\u000e\u0001"+
		"\u0000\u0000\u00009:\u0005(\u0000\u0000:\u0010\u0001\u0000\u0000\u0000"+
		";<\u0005)\u0000\u0000<\u0012\u0001\u0000\u0000\u0000=>\u0005,\u0000\u0000"+
		">\u0014\u0001\u0000\u0000\u0000?@\u0005;\u0000\u0000@\u0016\u0001\u0000"+
		"\u0000\u0000AE\u0007\u0000\u0000\u0000BD\u0007\u0001\u0000\u0000CB\u0001"+
		"\u0000\u0000\u0000DG\u0001\u0000\u0000\u0000EC\u0001\u0000\u0000\u0000"+
		"EF\u0001\u0000\u0000\u0000F\u0018\u0001\u0000\u0000\u0000GE\u0001\u0000"+
		"\u0000\u0000HJ\u0007\u0002\u0000\u0000IH\u0001\u0000\u0000\u0000JK\u0001"+
		"\u0000\u0000\u0000KI\u0001\u0000\u0000\u0000KL\u0001\u0000\u0000\u0000"+
		"L\u001a\u0001\u0000\u0000\u0000MO\u0007\u0003\u0000\u0000NM\u0001\u0000"+
		"\u0000\u0000OP\u0001\u0000\u0000\u0000PN\u0001\u0000\u0000\u0000PQ\u0001"+
		"\u0000\u0000\u0000QR\u0001\u0000\u0000\u0000RS\u0006\r\u0000\u0000S\u001c"+
		"\u0001\u0000\u0000\u0000\u0004\u0000EKP\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}