Team members:
Boris Guryev
Eduard Zalyaev


There is no single-click-all-works script. To run, please, use Intellij or other IDE.
Just compile and execute Main.java file from src/ExpressionParser to run code.
Unit tests runs from GUI IDE as easy. Right click on tests/ folder -> Run 'All tests'

Tests are written with JUnit5.0. Dependent library contains in lib/ folder. 

In case of emergency, contact me at TG in any time/season/mood/politic regime (except Ancrchy, I'll busy) @guru_ruru.

Assumptions:
* There is simple program sketch in in.txt file
* All input gives from the in.txt file
* All output stores in out.txt file


Set of tokens:

KEYWORD         - base of the language (such as access parameters, void, etc.)
ANNOTATION
IDENTIFIER      - such as user defined funciton names or variables
NULL_LITERAL
BOOLEAN_LITERAL
STRUCTURE_REFERENCE - funciton execution for example


Punctuation:
DOT             
COLON           - ':'
SEMICOLON       - ';'
COMMA


Brackets:
OPEN_CURL_BRACKET
CLOSE_CURL_BRACKET
OPEN_SQUARE_BRACKET
CLOSE_SQUARE_BRACKET
OPEN_ARROW
CLOSE_ARROW
OPEN_BRACKET
CLOSE_BRACKET


Operands:
OPERATOR
QUICK_OPERATOR
LOGIC_OPERATOR
ELSE_OPERATOR
IF_OPERATOR
UNAR_OPERATOR


Types:
HEX
INTEGER
OCTAL
FLOAT
CHAR
STRING


P.S. sorry for single-click-all-runs script absence.
Have a nice day (or more probably sleepless night)

P.S.S. (for Ivan G)
there are cat prepared as you wished :3