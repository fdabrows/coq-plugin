package org.univorleans.coq.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.univorleans.coq.psi.CoqTypes;
import com.intellij.psi.TokenType;
import org.univorleans.coq.parser.CoqKeywords;

%%

%class CoqLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

//CRLF= \n|\r|\r\n
Uppercase = [A-Z]
Lowercase = [a-z]
Letter = {Uppercase} | {Lowercase}
IDChar = {Letter} | "_" | "'"
ID = {IDChar} ({IDChar} | {NUMBER})*
NUMBER = 0 | [1-9][0-9]*

// All separators must be in ANYBUTDOT
ANYBUTDOT=[^\.\ \t\f\r\n(){}\[\]|,;:]+
DOCOPEN="(**"
COMOPEN="(*"
COMCLOSE="*)"
LPAR="("
RPAR=")"
WHITE_SPACE=[\ \t\f\r\n]
LSQBRACK="["
RSQBRACK="]"
LBRACK="{"
RBRACK="}"
MID="|"
COLON=":"
SEMICOLON=";"
COMMA=","
DOT="."

ARROW="->"
BIGARROW="=>"
ASSIGN=":="

END_OF_LINE_COMMENT ="#"[^\r\n]*
FDOT="."[^\ \t\f\r\n]

MINUS="-"
PLUS="+"
MULT="*"

%state YYFIXPOINT
%state YYFIXPOINTNAME

%state YYINDUCTIVE
%state YYINDUCTIVENAME

%state YYDEFINITION
%state YYDEFINITIONNAME
%state YYLEMMA
%state YYLEMMANAME

%state YYNAME

%state YYTACTIC

%%

{END_OF_LINE_COMMENT} {return CoqTypes.COMMENT;}


//{CRLF}              {return HoareTypes.CRLF;}


"Definition"                                    {yybegin(YYNAME); return CoqTypes.DEFINITION_KW;}
"Theorem"                                       {yybegin(YYNAME); return CoqTypes.THEOREM_KW;}
"Lemma"                                         {yybegin(YYNAME); return CoqTypes.LEMMA_KW;}
"Remark"                                        {yybegin(YYNAME); return CoqTypes.REMARK_KW;}
"Fact"                                          {yybegin(YYNAME); return CoqTypes.FACT_KW;}
"Corollary"                                     {yybegin(YYNAME); return CoqTypes.COROLLARY_KW;}
"Proposition"                                   {yybegin(YYNAME); return CoqTypes.PROPOSITION_KW;}
"Example"                                       {yybegin(YYNAME); return CoqTypes.EXAMPLE_KW;}
"Fixpoint"                                      {yybegin(YYNAME);return CoqTypes.FIXPOINT_KW;}
"CoFixpoint"                                    {yybegin(YYNAME);return CoqTypes.COFIXPOINT_KW;}
"Inductive"                                     {yybegin(YYNAME);return CoqTypes.INDUCTIVE_KW;}

"Proof"                                         {return CoqTypes.PROOF_KW;}

"Qed"                                           {return CoqTypes.QED;}

"Defined"                                       {return CoqTypes.DEFINED;}
"Admitted"                                      {return CoqTypes.ADMITTED;}

"match"                                         {return CoqTypes.MATCH;}

"with"                                          {return CoqTypes.WITH;}

"end"                                           {return CoqTypes.END;}

{DOCOPEN}                                       {return CoqTypes.COMOPEN;}


{COMOPEN}                                       {return CoqTypes.COMOPEN;}

{COMCLOSE}                                      {return CoqTypes.COMCLOSE;}

{DOT}                                           {return CoqTypes.DOT;}

{COMMA}                                         {return CoqTypes.COMMA;}

{FDOT}                                          {return CoqTypes.FDOT;}

{DOT}                                           {return CoqTypes.DOT;}

{MID}                                           {return CoqTypes.MID;}

{ASSIGN}                                        {return CoqTypes.ASSIGN;}

{ASSIGN}                                        {return CoqTypes.ASSIGN;}

{BIGARROW}                                      {return CoqTypes.BIGARROW;}

{ARROW}                                         {return CoqTypes.ARROW;}

{COLON}                                         {return CoqTypes.COLON;}

{SEMICOLON}                                     {return CoqTypes.SEMICOLON;}

{LPAR}                                          {return CoqTypes.LPAR;}

{RPAR}                                          {return CoqTypes.RPAR;}

{LBRACK}                                        {return CoqTypes.LBRACK;}

{RBRACK}                                        {return CoqTypes.RBRACK;}

{LSQBRACK}                                      {return CoqTypes.LSQBRACK;}

{RSQBRACK}                                      {return CoqTypes.RSQBRACK;}

{WHITE_SPACE}                                   {return TokenType.WHITE_SPACE;}

<YYNAME> {ID}                                   {yybegin(YYINITIAL); return CoqTypes.ID;}

{ID}                                            {
                                                    String str = String.valueOf(yytext());
                                                    IElementType element = CoqKeywords.getIElementType(str);
                                                    if (element != null) return element;
                                                    return CoqTypes.ID;
                                                }

<YYINITIAL>     {ANYBUTDOT}                     {return CoqTypes.ANYBUTDOT;}

{NUMBER}                                        {return CoqTypes.NUMBER;}

.                                               {return TokenType.BAD_CHARACTER;}

