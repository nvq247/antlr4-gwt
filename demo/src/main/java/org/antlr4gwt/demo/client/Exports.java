package org.antlr4gwt.demo.client;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl; 
import org.antlr4gwt.demo.shared.grammar.Python3Lexer;
import org.antlr4gwt.demo.shared.grammar.Python3Parser;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.File_inputContext;

import com.google.gwt.core.client.EntryPoint;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;


public class Exports implements EntryPoint {

  public void onModuleLoad() {
    new XPy();
  }
}

@JsType(namespace =JsPackage.GLOBAL)
 class   XPy{
  @JsMethod
  public static String toJs(String txt) {
    Python3Lexer lexer = new Python3Lexer(new ANTLRInputStream(txt));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    Python3Parser parser = new Python3Parser(tokens);
    parser.setBuildParseTree(true);
    File_inputContext tree = parser.file_input();
    ParseTree n;
    if( tree.getChildCount()>0 && (n=tree.getChild(tree.getChildCount()-1)) instanceof TerminalNodeImpl  && n.getText().equalsIgnoreCase("<EOF>") ) {
      tree.children.remove(tree.getChildCount()-1);
        
    }
    return String.valueOf(tree.accept(new Py2Ph()));
  }
}