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
"class"                                { return symbol(sym.CLASS); }
"int"                                  { return symbol(sym.INT); }
"float"                                { return symbol(sym.FLOAT); }
"boolean"                              { return symbol(sym.BOOLEAN); }
"if"                                   { return symbol(sym.IF); }
"else"                                 { return symbol(sym.ELSE); }
"for"                                  { return symbol(sym.FOR); }
"while"                                { return symbol(sym.WHILE); }
"break"                                { return symbol(sym.BREAK); }
"continue"                             { return symbol(sym.CONTINUE); }
"extern"                               { return symbol(sym.EXTERN); }
"return"                               { return symbol(sym.RETURN); }
"void"                                 { return symbol(sym.VOID); }

/* Literals */
{int_literal}                          { return symbol(sym.INT_LITERAL, new Integer(yytext()));}
{float_literal}                        { return symbol(sym.FLOAT_LITERAL, new Float(yytext()));}
"true"                                 { return symbol(sym.TRUE,true); }
"false"                                { return symbol(sym.FALSE,false);}

/* Operators */
"+"                                    { return symbol(sym.PLUS); }
"-"                                    { return symbol(sym.MINUS); }
"*"                                    { return symbol(sym.TIMES); }
"/"                                    { return symbol(sym.DIVIDE); }
"%"                                    { return symbol(sym.MOD); }

"<"                                    { return symbol(sym.LESS); }
">"                                    { return symbol(sym.GTR); }
"="                                    { return symbol(sym.EQ); }

"+="                                   { return symbol(sym.PLUS_EQ);}
"-="                                   { return symbol(sym.MINUS_EQ);}
"<="                                   { return symbol(sym.LESS_EQ);}
">="                                   { return symbol(sym.GTR_EQ);}
"=="                                   { return symbol(sym.EQ_EQ); }
"!"                                    { return symbol(sym.NOT); }
"!="                                   { return symbol(sym.NOT_EQ);}
"&&"                                   { return symbol(sym.AND); }
"||"                                   { return symbol(sym.OR); }


/* Delimiters */
"("                                    { return symbol(sym.L_PAREN); }
")"                                    { return symbol(sym.R_PAREN); }
"{"                                    { return symbol(sym.L_BRACE); }
"}"                                    { return symbol(sym.R_BRACE); } 
"["                                    { return symbol(sym.L_BRACKET); }
"]"                                    { return symbol(sym.R_BRACKET); }

"."                                    { return symbol(sym.DOT); }
","                                    { return symbol(sym.COMMA); }
";"                                    { return symbol(sym.SEMI_COLON); }

/* Identifiers */
{id}                                   { return symbol(sym.ID); }

/* Comments */
{comment}                              { /* Ignore comment */ }
{white_space}                          { /* Ignore whitespace */ }

/* Error */
[^]                                    { throw new Error("Illegal character <"+yytext()+">"); }