/* 
 * This file defines a lexical analyzer for the C-Tds programming language and 
 * it contains the standalone directive for testing purposes 
 */
%%

%public
%class CTdsScannerStandalone
%standalone
%unicode
%line
%column

%{


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

/* Literals */
{int_literal}                          { System.out.println("INT_LITERAL"); }
{float_literal}                        { System.out.println("FLOAT_LITERAL");}
"true"                                 { System.out.println("TRUE"); }
"false"                                { System.out.println("FALSE");}

/* Keywords */
"class"                                { System.out.println("CLASS"); }
"int"                                  { System.out.println("INT"); }
"float"                                { System.out.println("FLOAT");}
"boolean"                              { System.out.println("BOOLEAN");}
"if"                                   { System.out.println("IF"); }
"else"                                 { System.out.println("ELSE"); }
"for"                                  { System.out.println("FOR"); }
"while"                                { System.out.println("WHILE"); }
"break"                                { System.out.println("BREAK");}
"continue"                             { System.out.println("CONTINUE"); }
"extern"                               { System.out.println("EXTERN"); }
"return"                               { System.out.println("RETURN"); }
"void"                                 { System.out.println("VOID"); }

/* Operators */
"+"                                    { System.out.println("PLUS"); }
"-"                                    { System.out.println("MINUS"); }
"*"                                    { System.out.println("TIMES"); }
"/"                                    { System.out.println("DIVIDE"); }
"%"                                    { System.out.println("MOD"); }

"<"                                    { System.out.println("LESS");}
">"                                    { System.out.println("GTR"); }

"!"                                    { System.out.println("NOT"); }
"&&"                                   { System.out.println("AND"); }
"||"                                   { System.out.println("OR"); }

"="                                    { System.out.println("EQ"); }

/* Delimiters */
"("                                    { System.out.println("L_PAREN"); }
")"                                    { System.out.println("R_PAREN"); }
"{"                                    { System.out.println("L_BRACE"); }
"}"                                    { System.out.println("R_BRACE"); } 
"["                                    { System.out.println("L_BRACKET"); }
"]"                                    { System.out.println("R_BRACKET"); }

"."                                    { System.out.println("DOT");}
","                                    { System.out.println("COMMA"); }
";"                                    { System.out.println("SEMI_COLON"); }

/* Identifiers */
{id}                                   { System.out.println("ID"); }

/* Comments */
{comment}                              { System.out.println("COMMENT"); }
{white_space}                          { /* Ignore */}

/* Error */
[^]                                    { System.out.println("Illegal symbol, '" + yytext() +"'"); }