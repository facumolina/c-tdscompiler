/**
package de.jflex.example.standalone;
*/
%%

%public
%class CTdsScanner
%standalone

%unicode

%{
  String name;

%}  

  /* Definitions */
  digit                     = [0-9]
  alpha                     = [a-zA-Z]+
  alpha_num                 = {alpha} | {digit} | _
  int_literal               = {digit}+
  bool_literal              = true | false
  float_literal             = {digit}+[.]{digit}+   

  id                        = {alpha}{alpha_num}*
  literal                   = {int_literal} | {bool_literal} | {float_literal}

  /* Operators */
  cond_op			= && | \|\|

  /* Comments */    
  end_of_line   = \r|\n|\r\n
  one_line_comment  = "//" [^]* {end_of_line}?
  more_than_one_line_comment = "/*" ~"*/"
  comment           = {one_line_comment} | {more_than_one_line_comment}

%%

{comment}                           { System.out.print(yytext()); }
"name " {alpha} 						{ name = yytext().substring(5); }
{int_literal}							{ System.out.print("INT");}
[Hh] "ello"        						{ System.out.print(yytext()+" "+name+"!"); }
{bool_literal}							{ System.out.print("BOOL");}
{float_literal}							{ System.out.print("FLOAT");}
{id}									{ System.out.print("id"); }
{cond_op}								{ if (yytext().equals("&&")) { System.out.print("AND");} else {System.out.print("OR"); } }
