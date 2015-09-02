/**
package de.jflex.example.standalone;
*/
import java_cup.runtime.*;
%%

%public
%class CTdsScanner
%cup
%standalone
%unicode
%line
%column
%{
  String name;

  StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}  

  
  digit                     = [0-9]
  int_literal               = {digit}+
  float_literal             = {digit}+"."{digit}+ 

  /* Definitions 
  alpha                     = [a-zA-Z]+
  alpha_num                 = {alpha} | {digit} | _
  
  bool_literal              = true | false
  float_literal             = {digit}+"."{digit}+   

  id                        = {alpha}{alpha_num}*
  literal                   = {int_literal} | {bool_literal} | {float_literal}

 
  cond_op			= && | \|\|

      
  end_of_line   = \r|\n|\r\n
  one_line_comment  = "//" [^]* {end_of_line}?
  more_than_one_line_comment = "/*" ~"*/"
  comment           = {one_line_comment} | {more_than_one_line_comment}
  Comments */ 
%%

{int_literal}                          { return symbol(sym.int_literal, new Integer(yytext()));}
{float_literal}                        { return symbol(sym.float_literal, new Float(yytext()));}
/*
{comment}                              {  }
"name " {alpha} 						           { name = yytext().substring(5); }

[Hh] "ello"        						         { System.out.print(yytext()+" "+name+"!"); }
{bool_literal}							           { System.out.print("BOOL");}
{float_literal}							           { System.out.print("float_literal");}
{id}									                 { System.out.print("id"); }
{cond_op}								{ if (yytext().equals("&&")) { System.out.print("AND");} else {System.out.print("OR"); } }
*/