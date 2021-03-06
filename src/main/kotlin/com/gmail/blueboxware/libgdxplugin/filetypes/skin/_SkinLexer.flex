package com.gmail.blueboxware.libgdxplugin.filetypes.skin;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.intellij.grammar.psi.BnfTypes;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.gmail.blueboxware.libgdxplugin.filetypes.skin.SkinElementTypes.*;

%%

%{
  public _SkinLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _SkinLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

LINE_COMMENT="//".*
BLOCK_COMMENT="/"\*([^*]|\*+[^*/])*(\*+"/")?
CLASSNAME_STRING=[\"']?[a-z][a-zA-Z0-9]*(\.[a-zA-Z][a-zA-Z0-9]*)*\.[A-Z][a-zA-Z0-9$]*[\"']?
DOUBLE_QUOTED_STRING=\"([^\\\"\r\n]|\\[^\r\n])*\"?
SINGLE_QUOTED_STRING='([^\\'\r\n]|\\[^\r\n])*'?
NUMBER=-?(0|[1-9][0-9]*)(\.[0-9]+)?([eE][+-]?[0-9]*)?|-?\.[0-9]+
UNQUOTED_STRING=[a-zA-Z0-9\-_\.#]+
SPACE=[ \t\n\x0B\f\r]+

%%
<YYINITIAL> {
  {WHITE_SPACE}               { return WHITE_SPACE; }

  "{"                         { return L_CURLY; }
  "}"                         { return R_CURLY; }
  "["                         { return L_BRACKET; }
  "]"                         { return R_BRACKET; }
  ","                         { return COMMA; }
  ":"                         { return COLON; }
  "true"                      { return TRUE; }
  "false"                     { return FALSE; }
  "null"                      { return NULL; }

  {LINE_COMMENT}              { return LINE_COMMENT; }
  {BLOCK_COMMENT}             { return BLOCK_COMMENT; }
  {CLASSNAME_STRING}          { return CLASSNAME_STRING; }
  {DOUBLE_QUOTED_STRING}      { return DOUBLE_QUOTED_STRING; }
  {SINGLE_QUOTED_STRING}      { return SINGLE_QUOTED_STRING; }
  {NUMBER}                    { return NUMBER; }
  {UNQUOTED_STRING}           { return UNQUOTED_STRING; }
  {SPACE}                     { return SPACE; }

}

[^] { return BAD_CHARACTER; }
