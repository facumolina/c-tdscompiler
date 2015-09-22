/* 
 * This file defines a lexical analyzer for the C-Tds programming language. 
 */
import java_cup.runtime.*;
%%

%public
%class CTdsScanner
%cupsym CTdsSymbol
%cup
%unicode
%line
%column

%{

  /**
  * Return a new Symbol with the given token id, and with the current line and
  * column numbers.
  */
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }


  /**
  * Return a new Symbol with the given token id, the current line and column
  * numbers, and the given token value. 
  */
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }

%}  

  /* Numbers */
  digit                     = [0-9]
  int_literal               = {digit}+
  float_literal             = {digit}+"."{digit}+ 

  /* Characters */
  alpha                     = [a-zA-Z]+
  alpha_num                 = {alpha} | {digit} | _
  id                        = {alpha}{alpha_num}*

  /* To ignore */
  end_of_line   = \r|\n|\r\n
  white_space     = {end_of_line} | [ \t\f]
  one_line_comment  = "//" [^\r\n]* {end_of_line}?
  more_than_one_line_comment = "/*" ~"*/"
  comment           = {one_line_comment} | {more_than_one_line_comment}

%%

/* Keywords */
"class"                                { return symbol(CTdsSymbol.CLASS,yytext()); }
"int"                                  { return symbol(CTdsSymbol.INT,yytext()); }
"float"                                { return symbol(CTdsSymbol.FLOAT,yytext()); }
"boolean"                              { return symbol(CTdsSymbol.BOOLEAN,yytext()); }
"if"                                   { return symbol(CTdsSymbol.IF,yytext()); }
"else"                                 { return symbol(CTdsSymbol.ELSE,yytext()); }
"for"                                  { return symbol(CTdsSymbol.FOR,yytext()); }
"while"                                { return symbol(CTdsSymbol.WHILE,yytext()); }
"break"                                { return symbol(CTdsSymbol.BREAK,yytext()); }
"continue"                             { return symbol(CTdsSymbol.CONTINUE,yytext()); }
"extern"                               { return symbol(CTdsSymbol.EXTERN,yytext()); }
"return"                               { return symbol(CTdsSymbol.RETURN,yytext()); }
"void"                                 { return symbol(CTdsSymbol.VOID,yytext()); }

/* Literals */
{int_literal}                          { return symbol(CTdsSymbol.INT_LITERAL, new IntLiteral(yytext()));}
{float_literal}                        { return symbol(CTdsSymbol.FLOAT_LITERAL, new FloatLiteral(yytext()));}
"true"                                 { return symbol(CTdsSymbol.TRUE, new BooleanLiteral(true)); }
"false"                                { return symbol(CTdsSymbol.FALSE, new BooleanLiteral(false));}

/* Operators */
"+"                                    { return symbol(CTdsSymbol.PLUS,BinOpType.PLUS); }
"-"                                    { return symbol(CTdsSymbol.MINUS,BinOpType.MINUS); }
"*"                                    { return symbol(CTdsSymbol.TIMES,BinOpType.MULTIPLY); }
"/"                                    { return symbol(CTdsSymbol.DIVIDE,BinOpType.DIVIDE); }
"%"                                    { return symbol(CTdsSymbol.MOD,BinOpType.MOD); }

"<"                                    { return symbol(CTdsSymbol.LESS,BinOpType.LE); }
">"                                    { return symbol(CTdsSymbol.GTR,BinOpType.GE); }
"="                                    { return symbol(CTdsSymbol.EQ,yytext()); }

"+="                                   { return symbol(CTdsSymbol.PLUS_EQ,yytext());}
"-="                                   { return symbol(CTdsSymbol.MINUS_EQ,yytext());}
"<="                                   { return symbol(CTdsSymbol.LESS_EQ,yytext());}
">="                                   { return symbol(CTdsSymbol.GTR_EQ,yytext());}
"=="                                   { return symbol(CTdsSymbol.EQ_EQ,yytext()); }
"!"                                    { return symbol(CTdsSymbol.NOT,yytext()); }
"!="                                   { return symbol(CTdsSymbol.NOT_EQ,yytext());}
"&&"                                   { return symbol(CTdsSymbol.AND,yytext()); }
"||"                                   { return symbol(CTdsSymbol.OR,yytext()); }


/* Delimiters */
"("                                    { return symbol(CTdsSymbol.L_PAREN,yytext()); }
")"                                    { return symbol(CTdsSymbol.R_PAREN,yytext()); }
"{"                                    { return symbol(CTdsSymbol.L_BRACE,yytext()); }
"}"                                    { return symbol(CTdsSymbol.R_BRACE,yytext()); } 
"["                                    { return symbol(CTdsSymbol.L_BRACKET,yytext()); }
"]"                                    { return symbol(CTdsSymbol.R_BRACKET,yytext()); }

"."                                    { return symbol(CTdsSymbol.DOT,yytext()); }
","                                    { return symbol(CTdsSymbol.COMMA,yytext()); }
";"                                    { return symbol(CTdsSymbol.SEMI_COLON,yytext()); }

/* Identifiers */
{id}                                   { return symbol(CTdsSymbol.ID,yytext()); }

/* Comments */
{comment}                              { /* Ignore comment */ }
{white_space}                          { /* Ignore whitespace */ }

/* Error */
[^]                                    { return symbol(CTdsSymbol.ERROR,yytext()); }