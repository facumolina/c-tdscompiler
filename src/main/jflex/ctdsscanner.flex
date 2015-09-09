/* This file defines a lexical analyzer for the C-TDS programming language. */
import java_cup.runtime.*;
%%

%public
%class CTdsScanner
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
"class"                                { return symbol(sym.CLASS,yytext()); }
"int"                                  { return symbol(sym.INT,yytext()); }
"float"                                { return symbol(sym.FLOAT,yytext()); }
"boolean"                              { return symbol(sym.BOOLEAN,yytext()); }
"if"                                   { return symbol(sym.IF,yytext()); }
"else"                                 { return symbol(sym.ELSE,yytext()); }
"for"                                  { return symbol(sym.FOR,yytext()); }
"while"                                { return symbol(sym.WHILE,yytext()); }
"break"                                { return symbol(sym.BREAK,yytext()); }
"continue"                             { return symbol(sym.CONTINUE,yytext()); }
"extern"                               { return symbol(sym.EXTERN,yytext()); }
"return"                               { return symbol(sym.RETURN,yytext()); }
"void"                                 { return symbol(sym.VOID,yytext()); }

/* Literals */
{int_literal}                          { return symbol(sym.INT_LITERAL, new Integer(yytext()));}
{float_literal}                        { return symbol(sym.FLOAT_LITERAL, new Float(yytext()));}
"true"                                 { return symbol(sym.TRUE,true); }
"false"                                { return symbol(sym.FALSE,false);}

/* Operators */
"+"                                    { return symbol(sym.PLUS,yytext()); }
"-"                                    { return symbol(sym.MINUS,yytext()); }
"*"                                    { return symbol(sym.TIMES,yytext()); }
"/"                                    { return symbol(sym.DIVIDE,yytext()); }
"%"                                    { return symbol(sym.MOD,yytext()); }

"<"                                    { return symbol(sym.LESS,yytext()); }
">"                                    { return symbol(sym.GTR,yytext()); }
"="                                    { return symbol(sym.EQ,yytext()); }

"+="                                   { return symbol(sym.PLUS_EQ,yytext());}
"-="                                   { return symbol(sym.MINUS_EQ,yytext());}
"<="                                   { return symbol(sym.LESS_EQ,yytext());}
">="                                   { return symbol(sym.GTR_EQ,yytext());}
"=="                                   { return symbol(sym.EQ_EQ,yytext()); }
"!"                                    { return symbol(sym.NOT,yytext()); }
"!="                                   { return symbol(sym.NOT_EQ,yytext());}
"&&"                                   { return symbol(sym.AND,yytext()); }
"||"                                   { return symbol(sym.OR,yytext()); }


/* Delimiters */
"("                                    { return symbol(sym.L_PAREN,yytext()); }
")"                                    { return symbol(sym.R_PAREN,yytext()); }
"{"                                    { return symbol(sym.L_BRACE,yytext()); }
"}"                                    { return symbol(sym.R_BRACE,yytext()); } 
"["                                    { return symbol(sym.L_BRACKET,yytext()); }
"]"                                    { return symbol(sym.R_BRACKET,yytext()); }

"."                                    { return symbol(sym.DOT,yytext()); }
","                                    { return symbol(sym.COMMA,yytext()); }
";"                                    { return symbol(sym.SEMI_COLON,yytext()); }

/* Identifiers */
{id}                                   { return symbol(sym.ID,yytext()); }

/* Comments */
{comment}                              { /* Ignore comment */ }
{white_space}                          { /* Ignore whitespace */ }

/* Error */
//[^]                                    { throw new Error("Illegal character <"+yytext()+">"); }
[^]                                    { return symbol(sym.ERROR,yytext()); }