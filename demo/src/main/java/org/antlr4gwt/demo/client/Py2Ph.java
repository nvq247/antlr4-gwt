
package org.antlr4gwt.demo.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.*;
import org.antlr4gwt.demo.shared.grammar.Python3Visitor;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Py2Ph extends AbstractParseTreeVisitor<Object> implements Python3Visitor<Object> {

  @Override
  protected Object aggregateResult(Object aggregate, Object nextResult) {
    return (aggregate == null ? "" : aggregate.toString()) + (nextResult == null ? "" : nextResult);
  }

  // ==============================================================================================================
  private Object and(RuleNode node, int i) {
    if (node.getChildCount() < 2)
      return visitChildren(node);
    StringBuilder b = new StringBuilder();
    if (i == node.getChildCount() - 1) {
      return visit(node.getChild(i));
    } else if (i < node.getChildCount()) {

      b.append("_test(").append(visit(node.getChild(i))).append(",$rs)?(").append(and(node, i + 2))
          .append("):$rs");

    }
    return b;
  }

  // test(a)?(test(b)):a
  private Object or(RuleNode node, int i) {
    if (node.getChildCount() < 2)
      return visitChildren(node);

    StringBuilder b = new StringBuilder();
    if (i == node.getChildCount() - 1) {
      return visit(node.getChild(i));
    } else if (i < node.getChildCount()) {

      b.append("_test(").append(visit(node.getChild(i))).append(",$rs)?$rs:(")
          .append(or(node, i + 2)).append(")");

    }
    return b;
  }

  @Override
  /**
   * and_expr: shift_expr ('&' shift_expr)*;
   **/
  public Object visitAnd_expr(And_exprContext ctx) {
    // TODO Auto-generated method stub
    return ops(ctx);
  }

  @Override
  /**
   * and_test: not_test ( ('and'| '&&') not_test)*;
   **/
  public Object visitAnd_test(And_testContext ctx) {
    // TODO Auto-generated method stub
    return and(ctx, 0);
  }

  @Override
  /**
   * annassign: ':' test ('=' test)?;
   **/
  public Object visitAnnassign(AnnassignContext ctx) {
    if (ctx.getChildCount() == 3) {
      return "=" + visit(ctx.getChild(2).getChild(1)); // TODO
    } else {
      return "=_NULL_";
    }
    // return this.visitChildren(ctx);
  }

  @Override
  /**
   * arglist: argument (',' argument)* (',')?;
   * 
   **/
  public Object visitArglist(ArglistContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * argument: ( test (comp_for)? | test '=' test | '**' test | '*' test );
   * 
   **/
  public Object visitArgument(ArgumentContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * arith_expr: term (('+'|'-') term)*;
   **/
  public Object visitArith_expr(Arith_exprContext ctx) {
    // TODO Auto-generated method stub
    return ops(ctx);
  }

  @Override
  /**
   * assert_stmt: 'assert' test (',' test)?;
   * 
   **/
  public Object visitAssert_stmt(Assert_stmtContext ctx) {
    // TODO Auto-generated method stub
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * async_funcdef: ASYNC funcdef;
   **/
  public Object visitAsync_funcdef(Async_funcdefContext ctx) {
    // TODO Auto-generated method stub
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * async_stmt: ASYNC (funcdef | with_stmt | for_stmt);
   **/
  public Object visitAsync_stmt(Async_stmtContext ctx) {
    // TODO Auto-generated method stub
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * atom: ('(' (yield_expr|testlist_comp)? ')' | '[' (testlist_comp)? ']' | '{' (dictorsetmaker)?
   * '}' | NAME | NUMBER | STRING+ | '...' | 'None' | 'True' | 'False' | 'true' | 'false' | 'null'
   * );
   **/
  public Object visitAtom(AtomContext ctx) {

    if (ctx.getChildCount() > 0) {
      ParseTree c0 = ctx.getChild(0);
      if (c0 instanceof TerminalNode && c0.getText().equals("(") || c0.getText().equals("{")) {
        if (ctx.getChildCount() == 2) {
          return "[]";
        }
        if ((ctx.getChild(1).getChildCount() == 1 && c0.getText().equals("(")))
          return "(" + visit(ctx.getChild(1)) + ")";

        if (c0.getChildCount() == 2 && c0.getChild(1) instanceof Comp_forContext) {
          return visit(c0);
        }

        return "[" + visit(ctx.getChild(1)) + "]";

      }
      if (c0 instanceof TerminalNode && c0.getText().equals("[")
          && ctx.getChild(1).getChildCount() == 2
          && ctx.getChild(1).getChild(1) instanceof Comp_forContext) {
        return visit(ctx.getChild(1));
      }
    }

    if (ctx.name != null) {
      var.add(ctx.name.getText());
    }
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * atom_expr: (AWAIT)? atom trailer*;
   **/
  public Object visitAtom_expr(Atom_exprContext ctx) {
    NodeTextList list = new NodeTextList<>();
    StringBuilder bb = new StringBuilder();
    list.add(bb);
    list.add("");
    int cnt = ctx.getChildCount();
    int j = 0;

    ParseTree c0 = ctx.getChild(j);
    if (c0 instanceof TerminalNode && c0.getText().equalsIgnoreCase("AWAIT")) {

      j++;
      c0 = ctx.getChild(j);
    }
    AtomContext atom = (AtomContext) c0;

    if (atom.name != null)
      list.all("$this->", visit(c0));
    else
      list.all("", visit(c0));

    for (int i = j + 1; i < cnt; i++) {

      ParseTree c1 = ctx.getChild(i);

      // a + b + c -> _(_(a)['+'](b))['+'](c);
      // c1.getText()
      if (c1.getChild(0) instanceof TerminalNode && c1.getChild(0).getText().equals("(")) {

        list.add(visit(c1));

      } else {
        bb.append("_(");
        c1.getText();
        if (i == 1) {
          // list.set(1, "$this->");
        }
        if (c1.getChild(0).getText().equals("."))
          list.all(")->", visit(c1.getChild(1)));
        else
          list.all(")[", visit(c1.getChild(1)), "]");

      }

    }
    return list;

  }

  @Override
  /**
   * augassign: ('+=' | '-=' | '*=' | '@=' | '/=' | '%=' | '&=' | '|=' | '^=' | '<<=' | '>>=' |
   * '**=' | '//=');
   **/
  public Object visitAugassign(AugassignContext ctx) {
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * break_stmt: 'break';
   **/
  public Object visitBreak_stmt(Break_stmtContext ctx) {
    // TODO Auto-generated method stub
    return "break;";// this.visitChildren(ctx);
  }

  @Override
  public Object visit(ParseTree c) {
    Object childResult;
    if (c instanceof File_inputContext) {
      childResult = visitFile_input((File_inputContext) c);
    } else if (c instanceof TerminalNode) {
      childResult = visitTerminal((TerminalNode) c);
    } else if (c instanceof Encoding_declContext) {
      childResult = visitEncoding_decl((Encoding_declContext) c);
    } else if (c instanceof SubscriptlistContext) {
      childResult = visitSubscriptlist((SubscriptlistContext) c);
    } else if (c instanceof Compound_stmtContext) {
      childResult = visitCompound_stmt((Compound_stmtContext) c);
    } else if (c instanceof Dotted_as_nameContext) {
      childResult = visitDotted_as_name((Dotted_as_nameContext) c);
    } else if (c instanceof Lambdef_nocondContext) {
      childResult = visitLambdef_nocond((Lambdef_nocondContext) c);
    } else if (c instanceof Continue_stmtContext) {
      childResult = visitContinue_stmt((Continue_stmtContext) c);
    } else if (c instanceof Testlist_star_exprContext) {
      childResult = visitTestlist_star_expr((Testlist_star_exprContext) c);
    } else if (c instanceof TypedargslistContext) {
      childResult = visitTypedargslist((TypedargslistContext) c);
    } else if (c instanceof Single_inputContext) {
      childResult = visitSingle_input((Single_inputContext) c);
    } else if (c instanceof Except_clauseContext) {
      childResult = visitExcept_clause((Except_clauseContext) c);
    } else if (c instanceof DictorsetmakerContext) {
      childResult = visitDictorsetmaker((DictorsetmakerContext) c);
    } else if (c instanceof Import_as_namesContext) {
      childResult = visitImport_as_names((Import_as_namesContext) c);
    } else if (c instanceof Import_as_nameContext) {
      childResult = visitImport_as_name((Import_as_nameContext) c);
    } else if (c instanceof Dotted_as_namesContext) {
      childResult = visitDotted_as_names((Dotted_as_namesContext) c);
    } else if ((c instanceof Testlist_compContext) || (c instanceof VarrayContext)) {
      childResult = visitTestlistComp((ParserRuleContext) c);
    } else if (c instanceof Async_funcdefContext) {
      childResult = visitAsync_funcdef((Async_funcdefContext) c);
    } else if (c instanceof Nonlocal_stmtContext) {
      childResult = visitNonlocal_stmt((Nonlocal_stmtContext) c);
    } else if (c instanceof ExprContext) {
      childResult = visitExpr((ExprContext) c);
    } else if (c instanceof Import_stmtContext) {
      childResult = visitImport_stmt((Import_stmtContext) c);
    } else if (c instanceof FuncdefContext) {
      childResult = visitFuncdef((FuncdefContext) c);
    } else if (c instanceof With_stmtContext) {
      childResult = visitWith_stmt((With_stmtContext) c);
    } else if (c instanceof Star_exprContext) {
      childResult = visitStar_expr((Star_exprContext) c);
    } else if (c instanceof Simple_stmtContext) {
      childResult = visitSimple_stmt((Simple_stmtContext) c);
    } else if (c instanceof Small_stmtContext) {
      childResult = visitSmall_stmt((Small_stmtContext) c);
    } else if (c instanceof Xor_exprContext) {
      childResult = visitXor_expr((Xor_exprContext) c);
    } else if (c instanceof Dotted_nameContext) {
      childResult = visitDotted_name((Dotted_nameContext) c);
    } else if (c instanceof With_itemContext) {
      childResult = visitWith_item((With_itemContext) c);
    } else if (c instanceof Shift_exprContext) {
      childResult = visitShift_expr((Shift_exprContext) c);
    } else if (c instanceof Global_stmtContext) {
      childResult = visitGlobal_stmt((Global_stmtContext) c);
    } else if (c instanceof Try_stmtContext) {
      childResult = visitTry_stmt((Try_stmtContext) c);
    } else if (c instanceof Comp_opContext) {
      childResult = visitComp_op((Comp_opContext) c);
    } else if (c instanceof ParametersContext) {
      childResult = visitParameters((ParametersContext) c);
    } else if (c instanceof If_stmtContext) {
      childResult = visitIf_stmt((If_stmtContext) c);
    } else if (c instanceof StmtContext) {
      childResult = visitStmt((StmtContext) c);
    } else if (c instanceof TfpdefContext) {
      childResult = visitTfpdef((TfpdefContext) c);
    } else if (c instanceof Not_testContext) {
      childResult = visitNot_test((Not_testContext) c);
    } else if (c instanceof Arith_exprContext) {
      childResult = visitArith_expr((Arith_exprContext) c);
    } else if (c instanceof Yield_argContext) {
      childResult = visitYield_arg((Yield_argContext) c);
    } else if (c instanceof Yield_exprContext) {
      childResult = visitYield_expr((Yield_exprContext) c);
    } else if (c instanceof TermContext) {
      childResult = visitTerm((TermContext) c);
    } else if (c instanceof ClassdefContext) {
      childResult = visitClassdef((ClassdefContext) c);
    } else if (c instanceof FactorContext) {
      childResult = visitFactor((FactorContext) c);
    } else if (c instanceof DecoratedContext) {
      childResult = visitDecorated((DecoratedContext) c);
    } else if (c instanceof Assert_stmtContext) {
      childResult = visitAssert_stmt((Assert_stmtContext) c);
    } else if (c instanceof ComparisonContext) {
      childResult = visitComparison((ComparisonContext) c);
    } else if (c instanceof TestContext) {
      childResult = visitTest((TestContext) c);
    } else if (c instanceof Test_nocondContext) {
      childResult = visitTest_nocond((Test_nocondContext) c);
    } else if (c instanceof Comp_iterContext) {
      childResult = visitComp_iter((Comp_iterContext) c);
    } else if (c instanceof Async_stmtContext) {
      childResult = visitAsync_stmt((Async_stmtContext) c);
    } else if (c instanceof PowerContext) {
      childResult = visitPower((PowerContext) c);
    } else if (c instanceof Eval_inputContext) {
      childResult = visitEval_input((Eval_inputContext) c);
    } else if (c instanceof VfpdefContext) {
      childResult = visitVfpdef((VfpdefContext) c);
    } else if (c instanceof Flow_stmtContext) {
      childResult = visitFlow_stmt((Flow_stmtContext) c);
    } else if (c instanceof ExprlistContext) {
      childResult = visitExprlist((ExprlistContext) c);
    } else if (c instanceof ArglistContext) {
      childResult = visitArglist((ArglistContext) c);
    } else if (c instanceof Import_nameContext) {
      childResult = visitImport_name((Import_nameContext) c);
    } else if (c instanceof ArgumentContext) {
      childResult = visitArgument((ArgumentContext) c);
    } else if (c instanceof Raise_stmtContext) {
      childResult = visitRaise_stmt((Raise_stmtContext) c);
    } else if (c instanceof Comp_ifContext) {
      childResult = visitComp_if((Comp_ifContext) c);
    } else if (c instanceof Expr_stmtContext) {
      childResult = visitExpr_stmt((Expr_stmtContext) c);
    } else if (c instanceof Import_fromContext) {
      childResult = visitImport_from((Import_fromContext) c);
    } else if (c instanceof TestlistContext) {
      childResult = visitTestlist((TestlistContext) c);
    } else if (c instanceof Return_stmtContext) {
      childResult = visitReturn_stmt((Return_stmtContext) c);
    } else if (c instanceof SubscriptContext) {
      childResult = visitSubscript((SubscriptContext) c);
    } else if (c instanceof Or_testContext) {
      childResult = visitOr_test((Or_testContext) c);
    } else if (c instanceof Comp_forContext) {
      childResult = visitComp_for((Comp_forContext) c);
    } else if (c instanceof While_stmtContext) {
      childResult = visitWhile_stmt((While_stmtContext) c);
    } else if (c instanceof And_testContext) {
      childResult = visitAnd_test((And_testContext) c);
    } else if (c instanceof DecoratorContext) {
      childResult = visitDecorator((DecoratorContext) c);
    } else if (c instanceof Break_stmtContext) {
      childResult = visitBreak_stmt((Break_stmtContext) c);
    } else if (c instanceof TrailerContext) {
      childResult = visitTrailer((TrailerContext) c);
    } else if (c instanceof And_exprContext) {
      childResult = visitAnd_expr((And_exprContext) c);
    } else if (c instanceof Atom_exprContext) {
      childResult = visitAtom_expr((Atom_exprContext) c);
    } else if (c instanceof LambdefContext) {
      childResult = visitLambdef((LambdefContext) c);
    } else if (c instanceof DecoratorsContext) {
      childResult = visitDecorators((DecoratorsContext) c);
    } else if (c instanceof AnnassignContext) {
      childResult = visitAnnassign((AnnassignContext) c);
    } else if (c instanceof SliceopContext) {
      childResult = visitSliceop((SliceopContext) c);
    } else if (c instanceof AugassignContext) {
      childResult = visitAugassign((AugassignContext) c);
    } else if (c instanceof Del_stmtContext) {
      childResult = visitDel_stmt((Del_stmtContext) c);
    } else if (c instanceof AtomContext) {
      childResult = visitAtom((AtomContext) c);
    } else if (c instanceof SuiteContext) {
      childResult = visitSuite((SuiteContext) c);
    } else if (c instanceof Pass_stmtContext) {
      childResult = visitPass_stmt((Pass_stmtContext) c);
    } else if (c instanceof VarargslistContext) {
      childResult = visitVarargslist((VarargslistContext) c);
    } else if (c instanceof For_stmtContext) {
      childResult = visitFor_stmt((For_stmtContext) c);
    } else if (c instanceof Yield_stmtContext) {
      childResult = visitYield_stmt((Yield_stmtContext) c);
    } else if (c == null) {
      return "";
    } else {
      childResult = c.accept(this);
    }
    return childResult;
  }

  @Override
  public Object visitChildren(RuleNode node) {
    Object result = defaultResult();
    int n = node.getChildCount();
    for (int i = 0; i < n; i++) {
      if (!shouldVisitNextChild(node, result)) {
        break;
      }
      ParseTree c = node.getChild(i);
      Object childResult = visit(c);


      result = aggregateResult(result, childResult);
    }

    return result;
  }

  @Override
  /**
   * classdef: 'class' NAME ('(' (arglist)? ')')? ':' suite;
   * 
   **/
  public Object visitClassdef(ClassdefContext ctx) {
    // TODO Auto-generated method stub
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * comp_for: (ASYNC)? 'for' exprlist 'in' or_test (comp_iter)?;
   **/
  public Object visitComp_for(Comp_forContext ctx) {

    // TODO
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * comp_if: 'if' test_nocond (comp_iter)?;
   * 
   **/
  public Object visitComp_if(Comp_ifContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * comp_iter: comp_for | comp_if;
   **/
  public Object visitComp_iter(Comp_iterContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * comp_op: '<'|'>'|'=='|'>='|'<='|'<>'|'!='|'in'|'not' 'in'|'is'|'is' 'not';
   **/
  public Object visitComp_op(Comp_opContext ctx) {
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * comparison: expr (comp_op expr)*; comp_op: '<'|'>'|'=='|'>='|'<='|'<>'|'!='|'in'|'not'
   * 'in'|'is'|'is' 'not'; 1 + 2 + 3 + 4
   **/
  public Object visitComparison(ComparisonContext ctx) {

    return ops(ctx);

  }

  private Object ops(ParserRuleContext ctx) {
    NodeTextList list = new NodeTextList();
    int cc;
    StringBuilder bb = new StringBuilder();


    if ((cc = ctx.getChildCount()) > 1) {
      list.all(bb);
      list.add(visit(ctx.getChild(0)));
      for (int i = 1; i <= cc - 2; i += 2) {

        bb.append("_(");
        list.all(")", "['", ctx.getChild(i).getText(), "'](_w(", visit(ctx.getChild(i + 1)), "))");;

      }
      return list;
    } else {
      return this.visitChildren(ctx);
    }

  }

  @Override
  /**
   * compound_stmt: if_stmt | while_stmt | for_stmt | try_stmt | with_stmt | funcdef | classdef |
   * decorated | async_stmt;
   **/
  public Object visitCompound_stmt(Compound_stmtContext ctx) {
    // TODO Auto-generated method stub
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * continue_stmt: 'continue';
   **/
  public Object visitContinue_stmt(Continue_stmtContext ctx) {
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * decorated: decorators (classdef | funcdef | async_funcdef);
   * 
   **/
  public Object visitDecorated(DecoratedContext ctx) {
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * decorator: '@' dotted_name ( '(' (arglist)? ')' )? NEWLINE;
   **/
  public Object visitDecorator(DecoratorContext ctx) {
    // TODO Auto-generated method stub
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * decorators: decorator+;
   **/
  public Object visitDecorators(DecoratorsContext ctx) {
    // TODO Auto-generated method stub
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * del_stmt: 'del' exprlist;
   **/
  public Object visitDel_stmt(Del_stmtContext ctx) {
    // TODO Auto-generated method stub
    return "_unset(" + visit(ctx.getChild(1)) + ")";
  }

  @Override
  /**
   * dictorsetmaker: ( ((test ':' test | '**' expr) (comp_for | (',' (test ':' test | '**' expr))*
   * (',')?)) | ((test | star_expr) (comp_for | (',' (test | star_expr))* (',')?)) );
   * 
   **/
  public Object visitDictorsetmaker(DictorsetmakerContext ctx) {
    NodeTextList list = new NodeTextList();
    for (ParseTree a : ctx.children) {
      if (a instanceof TerminalNode && a.getText().equals(":")) {
        list.add("=>");
      } else {
        list.add(visit(a));
      }
    }
    return list;
  }

  @Override
  /**
   * dotted_as_name: dotted_name ('as' NAME)?;
   **/
  public Object visitDotted_as_name(Dotted_as_nameContext ctx) {
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * dotted_as_names: dotted_as_name (',' dotted_as_name)*;
   **/
  public Object visitDotted_as_names(Dotted_as_namesContext ctx) {
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * dotted_name: NAME ('.' NAME)*;
   **/
  public Object visitDotted_name(Dotted_nameContext ctx) {
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * encoding_decl: NAME;
   * 
   **/
  public Object visitEncoding_decl(Encoding_declContext ctx) {
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * eval_input: testlist NEWLINE* EOF;
   * 
   **/
  public Object visitEval_input(Eval_inputContext ctx) {
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * except_clause: 'except' (test ('as' NAME)?)?;
   **/
  public Object visitExcept_clause(Except_clauseContext ctx) {
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * expr: xor_expr ('|' xor_expr)*;
   **/
  public Object visitExpr(ExprContext ctx) {
    // ~
    return ops(ctx);
  }

  @Override
  /**
   * annassign: foo : int = 42 augassign "+=" | "-=" | "*=" | "/=" | "%=" | "**=" | ">>=" | "<<=" |
   * "&=" | "^=" | "|="
   * 
   * expr_stmt: testlist_star_expr (annassign | augassign (yield_expr|testlist) | ('='
   * (yield_expr|testlist_star_expr))*);
   **/
  public Object visitExpr_stmt(Expr_stmtContext ctx) {
    ParseTree c1 = ctx.getChild(1);
    ParseTree c0 = ctx.getChild(0);
    if (c1 instanceof TerminalNode) {


      boolean o1 = false;
      boolean o0 = false;

      ParseTree c2 = ctx.getChild(2);
      o0 = c0.getChildCount() > 1;
      o1 = c2.getChildCount() > 1;



      String as = (o0 ? "[" : "") + visit(c0) + (o0 ? "]" : "");
      String rs = (o1 ? "[" : (as.startsWith("[") ? "_as_array(" : "")) + visit(c2)
          + (o1 ? "]" : (as.startsWith("[") ? ")" : ""));


      return as + " = " + rs;


    } else if (c1 instanceof AnnassignContext) {
      if (c1.getChildCount() == 4) {
        return "$this->_new('" + ctx.getChild(0).getText() + "'," + visit(c1.getChild(3)) + ")";
      }
      return "$this->_new('" + ctx.getChild(0).getText() + "')";
    } else if (c1 instanceof AugassignContext) {
      return visit(ctx.getChild(0)) + " = _w(" + visit(ctx.getChild(0)) + ")['"
          + c1.getText().replace("=", "") + "'](" + visit(ctx.getChild(2)) + ")";
    }
    return visitChildren(ctx);
  }

  private <T> T find(ParseTree c, Class<T> cl) {
    if (c == null)
      return null;
    if (c.getClass() == cl)
      return (T) c;
    if (c.getChildCount() > 0)
      return find(c.getChild(0), cl);
    return null;
  }

  private Object assignList(ParseTree c0) {
    NodeTextList list = new NodeTextList();
    if (c0 == null) {
      return "";
    }
    if (c0 instanceof TerminalNode) {
      return "";
    }
    int index = 0;
    List<ParseTree> cs = ((ParserRuleContext) c0).children;
    int ignore = 0;
    for (ParseTree a : cs) {
      if (ignore > 0) {
        ignore--;
        continue;
      }

      if (a.getText().equals("_")) {
        list.add("");
      } else if (a.getText().equals("*_")) {
        list.add("");

      } else if (a.getText().equals(":")) {
        list.add("=>");
      } else {
        if (cs.size() > index + 1 && (cs.get(index + 1) instanceof Comp_forContext)) {
          ignore++;
          HashSet vars = new HashSet();
          list.add(makeFor(cs.get(index + 1), a, vars, 0));

        } else {
          list.add(visit(a));
        }
      }
      index++;
    }

    return list;
  }

  @Override
  /**
   * exprlist: (expr|star_expr) (',' (expr|star_expr))* (',')?;
   **/
  public Object visitExprlist(ExprlistContext ctx) {
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * factor: ('+'|'-'|'~') factor | power;
   **/
  public Object visitFactor(FactorContext ctx) {
    NodeTextList n = new NodeTextList();
    if (ctx.getChildCount() == 2)
      return n.all("_w(", visit(ctx.getChild(1)), ")['", ctx.getChild(0).getText(), "']();");
    return visitChildren(ctx);
  }

  @Override
  /**
   * file_input: (NEWLINE | stmt)* EOF;
   **/
  public Object visitFile_input(File_inputContext ctx) {
    return visitChildren(ctx);
  }

  @Override
  /**
   * flow_stmt: break_stmt | continue_stmt | return_stmt | raise_stmt | yield_stmt;
   **/
  public Object visitFlow_stmt(Flow_stmtContext ctx) {
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * for_stmt: 'for' exprlist 'in' testlist ':' suite ('else' ':' suite)?;
   **/
  public Object visitFor_stmt(For_stmtContext ctx) {
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * funcdef: 'def' NAME parameters ('->' test)? ':' suite;
   * 
   **/
  public Object visitFuncdef(FuncdefContext ctx) {

    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * global_stmt: 'global' NAME (',' NAME)*;
   **/
  public Object visitGlobal_stmt(Global_stmtContext ctx) {

    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * if_stmt: 'if' test ':' suite ('elif' test ':' suite)* ('else' ':' suite)?;
   **/
  public Object visitIf_stmt(If_stmtContext ctx) {

    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * import_as_name: NAME ('as' NAME)?;
   **/
  public Object visitImport_as_name(Import_as_nameContext ctx) {

    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * import_as_names: import_as_name (',' import_as_name)* (',')?;
   **/
  public Object visitImport_as_names(Import_as_namesContext ctx) {

    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * import_from: ('from' (('.' | '...')* dotted_name | ('.' | '...')+) 'import' ('*' | '('
   * import_as_names ')' | import_as_names));
   **/
  public Object visitImport_from(Import_fromContext ctx) {

    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * import_name: 'import' dotted_as_names;
   **/
  public Object visitImport_name(Import_nameContext ctx) {

    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * import_stmt: import_name | import_from;
   **/
  public Object visitImport_stmt(Import_stmtContext ctx) {

    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * lambdef: ( 'lambda' (varargslist)? ':' test | ( vfpdef | '(' (varargslist)? ')') '=>' test );
   **/
  public Object visitLambdef(LambdefContext ctx) {

    NodeTextList list = new NodeTextList<>();
    ParseTree node0 = ctx.getChild(0);
    String type = ctx.getChild(ctx.getChildCount() - 1).getText();

    if (node0 instanceof TerminalNode && node0.getText().equals("lambda")) {
      if (ctx.getChildCount() == 4)
        list.all("function(", listArgs(ctx.getChild(1)), "){", defaulArgs(ctx.getChild(1)),
            "return ", visit(ctx.getChild(3)), " ;/*1*/}");
      if (ctx.getChildCount() == 3)
        list.all("function(){return ", visit(ctx.getChild(2)), " ;}");
    } else if (node0 instanceof TerminalNode && node0.getText().equals("(")) {
      if (ctx.getChildCount() == 5)
        list.all("function(", listArgs(ctx.getChild(1)), "){", defaulArgs(ctx.getChild(1)),
            "return ", visit(ctx.getChild(4)), " ;/*2*/}");
      if (ctx.getChildCount() == 4)
        list.all("function(){return ", visit(ctx.getChild(3)), " ;}");
    } else if (node0 instanceof VfpdefContext) {
      list.all("function(", visit(ctx.getChild(0)), "){$this->_new('", node0.getText(), "',$",
          node0.getText(), ");return ", visit(ctx.getChild(2)), " ;/*3*/}");
    } else if (node0 instanceof VarrayContext) {
      list.all("function($_a1){$this->_new(", exploreVars((VarrayContext) node0, new HashSet<>()),
          ");", visit(ctx.getChild(0)), "=$_a1", ";", "return ", visit(ctx.getChild(2)),
          " ;/*4*/}");
    }
    return list;
  }

  private Object listArgs(ParseTree a) {
    // return "";
    NodeTextList list = new NodeTextList();
    boolean unpack = false;
    boolean ignore = false;
    int index = 0;
    for (ParseTree c : ((ParserRuleContext) a).children) {

      if (ignore) {
        ignore = false;
        continue;
      }

      if (c instanceof VfpdefContext || c instanceof VarrayContext) {
        index++;
        if (unpack) {
          list.add("...");
        }
        list.add("$_a" + index);



        list.add(",");
        unpack = false;
      } else if (c instanceof TerminalNode
          && ("*".equals(c.getText()) || "...".equals(c.getText()))) {
        unpack = true;
      } else if (c instanceof TerminalNode && "=".equals(c.getText())) {
        ignore = true;
      }
    }
    if (!list.isEmpty()) {
      list.remove(list.size() - 1);
    } ;
    return list;
  }

  private Object defaulArgs(ParseTree a) {
    NodeTextList list = new NodeTextList();
    ParserRuleContext last = null;
    boolean assign = false;
    int index = 0;
    for (ParseTree c : ((VarargslistContext) a).children) {

      if (assign == false && (c instanceof VfpdefContext || c instanceof VarrayContext)) {
        last = (ParserRuleContext) c;
      } else if (c instanceof TerminalNode
          && ("...".equals(c.getText()) || "*".equals(c.getText()))) {
        continue;
      } else {

        if (c instanceof TerminalNode && "=".equals(c.getText())) {
          assign = true;
        } else {
          index++;
          if (c instanceof TerminalNode && ",".equals(c.getText())) {
            if (last != null) {
              list.all("$this->_new(",
                  (last instanceof VfpdefContext) ? "'" + last.getText() + "'"
                      : exploreVars(last, new HashSet<>()),
                  (last instanceof VfpdefContext) ? (",$_a" + index) : "", ");");
            }
          } else {

            list.all("$this->_new(",
                (last instanceof VfpdefContext) ? ("'" + last.getText() + "'")
                    : exploreVars(last, new HashSet<>()),
                ",", (last instanceof VfpdefContext) ? ("$_a" + index) : "null", "??", visit(c),
                ");");

          }
          if (last instanceof VarrayContext) {
            list.all(visit(last), "=", "$_a" + index, ";");
          }
          last = null;
          assign = false;
        }
      }
    }
    if (last != null) {
      index++;
    

        list.all("$this->_new(",
            (last instanceof VfpdefContext) ? ("'" + last.getText() + "'")
                : exploreVars(last, new HashSet<>()),
            ",", (last instanceof VfpdefContext) ? ("$_a" + index) : "null",
            ");");

      
      if (last instanceof VarrayContext) {
        list.all(visit(last), "=", "$_a" + index, ";");
      }
      last = null;
      assign = false;
    }
    // if(!list.isEmpty()) {list.remove(list.size()-1);};
    return list;
  }

  private Set<String> exploreVars(ParserRuleContext last, Set<String> r) {
    if (last == null)
      return r;


    if (last instanceof VfpdefContext) {
      r.add("'" + last.getText() + "'");
    }

    for (ParseTree p : last.children) {
      if (p instanceof ParserRuleContext) {
        ParserRuleContext new_name = (ParserRuleContext) p;
        exploreVars(new_name, r);

      }

    }
    return r;
  }

  @Override
  /**
   * lambdef_nocond: 'lambda' (varargslist)? ':' test_nocond;
   **/
  public Object visitLambdef_nocond(Lambdef_nocondContext ctx) {
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * nonlocal_stmt: 'nonlocal' NAME (',' NAME)*;
   **/
  public Object visitNonlocal_stmt(Nonlocal_stmtContext ctx) {
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * not_test: ('not' | '!') not_test | comparison;
   **/
  public Object visitNot_test(Not_testContext ctx) {
    if (ctx.getChildCount() == 2) {
      return "_not(" + visit(ctx.getChild(1)) + ")";
    }
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * or_test: and_test ( ('or' | '||') and_test)*;
   **/
  public Object visitOr_test(Or_testContext ctx) {
    return or(ctx, 0);
  }

  @Override
  /**
   * parameters: '(' (typedargslist)? ')';
   **/
  public Object visitParameters(ParametersContext ctx) {
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * pass_stmt: 'pass';
   **/
  public Object visitPass_stmt(Pass_stmtContext ctx) {
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * power: atom_expr ('**' factor)?;
   **/
  public Object visitPower(PowerContext ctx) {
    return ops(ctx);
  }

  @Override
  /**
   * raise_stmt: 'raise' (test ('from' test)?)?;
   **/
  public Object visitRaise_stmt(Raise_stmtContext ctx) {
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * return_stmt: 'return' (testlist)?;
   **/
  public Object visitReturn_stmt(Return_stmtContext ctx) {
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * shift_expr: arith_expr (('<<'|'>>') arith_expr)*;
   **/
  public Object visitShift_expr(Shift_exprContext ctx) {
    return ops(ctx);
  }

  @Override
  /**
   * simple_stmt: small_stmt (';' small_stmt)* (';')? NEWLINE;
   **/
  public Object visitSimple_stmt(Simple_stmtContext ctx) {
    return this.visitChildren(ctx).toString().trim() + "; //" + ctx.getText() + "\n";
  }

  @Override
  /**
   * single_input: NEWLINE | simple_stmt | compound_stmt NEWLINE;
   **/
  public Object visitSingle_input(Single_inputContext ctx) {
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * sliceop: ':' (test)?;
   **/
  public Object visitSliceop(SliceopContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * small_stmt: (expr_stmt | del_stmt | pass_stmt | flow_stmt | import_stmt | global_stmt |
   * nonlocal_stmt | assert_stmt);
   **/
  public Object visitSmall_stmt(Small_stmtContext ctx) {
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * star_expr: '*' expr;
   **/
  public Object visitStar_expr(Star_exprContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * stmt: simple_stmt | compound_stmt;
   **/
  public Object visitStmt(StmtContext ctx) {

    return this.visitChildren(ctx);
  }

  @Override
  /**
   * subscript: test | (test)? ':' (test)? (sliceop)?;
   **/
  public Object visitSubscript(SubscriptContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * subscriptlist: subscript (',' subscript)* (',')?;
   **/
  public Object visitSubscriptlist(SubscriptlistContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * suite: simple_stmt | NEWLINE INDENT stmt+ DEDENT;
   * 
   **/
  public Object visitSuite(SuiteContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * term: factor (('*'|'@'|'/'|'%'|'//') factor)*;
   **/
  public Object visitTerm(TermContext ctx) {
    return ops(ctx);
  }

  @Override
  public Object visitTerminal(TerminalNode node) {
    return node.getText();
  }

  @Override
  /**
   * test: or_test ('if' or_test 'else' test)? | or_test '?' or_test ':' test | lambdef;
   **/
  public Object visitTest(TestContext ctx) {
    NodeTextList list = new NodeTextList<>();
    if (ctx.getChildCount() == 5) {
      if (ctx.getChild(1).getText().equals("?")) {
        list.all("_test(", visit(ctx.getChild(0)), ")");
        list.add("?");
        list.add(visit(ctx.getChild(2)));
        list.add(":");
        list.add(visit(ctx.getChild(4)));
      } else {
        list.all("_test(", visit(ctx.getChild(2)), ")");
        list.add("?");
        list.add(visit(ctx.getChild(0)));
        list.add(":");
        list.add(visit(ctx.getChild(4)));
      }
    } else {
      return visitChildren(ctx);
    }
    return list;
  }

  @Override
  /**
   * test_nocond: or_test | lambdef_nocond;
   **/
  public Object visitTest_nocond(Test_nocondContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * testlist: test (',' test)* (',')?;
   **/
  public Object visitTestlist(TestlistContext ctx) {
    // ~
    return this.visitChildren(ctx);
  }


  /**
   * testlist_comp: (test|star_expr) ( comp_for | (',' (test|star_expr))* (',')? );
   **/
  public Object visitTestlistComp(ParserRuleContext ctx) {
    if (ctx.getChildCount() == 2 && (ctx.getChild(1) instanceof Comp_forContext)) {
      HashSet<String> vars = new HashSet<String>();
      return makeFor(ctx.getChild(1), ctx.getChild(0), vars, 0);

    }
    return this.visitChildren(ctx);
  }

  public Object visitTestlist_comp(Testlist_compContext ctx) {
    return visitTestlistComp(ctx);
  }

  Set<String> var = new HashSet<String>();

  private Object makeFor(ParseTree child, ParseTree ret, Set vars, int lv) {
    // for a in b
    NodeTextList list = new NodeTextList();


    if (lv == 0) {

      list.all("(function(){$rs=[];", "");
    }
    if (child.getChildCount() == 4) {
      var.clear();
      Object r1 = visit(child.getChild(1));
      if (child.getChild(1).getChildCount() > 1
          && !(child.getChild(1).getChild(0) instanceof TerminalNode)) {
        r1 = "[" + r1 + "]";
      }
      vars.addAll(var);

      list.all("foreach (", visit(child.getChild(3)), " as ", r1, "){",

          "$rs[]=", visit(ret), ";}");
    }
    if (child.getChildCount() == 5) {
      if (child.getChild(4).getChild(0) instanceof Comp_forContext
          || child.getChild(4).getChild(0) instanceof VarrayContext) {
        var.clear();
        Object r1 = visit(child.getChild(1));
        vars.addAll(var);

        list.all("foreach (", visit(child.getChild(3)), " as ", r1, "){",

            makeFor(child.getChild(4).getChild(0), ret, vars, lv + 1), ";}");


      } else {
        var.clear();
        Object r1 = visit(child.getChild(1));
        vars.addAll(var);
        list.all("foreach (", visit(child.getChild(3)), " as ", r1, "){",

            "if(_test(", visit(child.getChild(4).getChild(0).getChild(1)), "))", "$rs[]=",
            visit(ret), ";}");
      }

    }

    if (lv == 0) {
      list.add(";return $rs;})->call($this->extend())");
      StringBuilder x = new StringBuilder();
      for (Object v : vars) {
        x.append("$this->_new('" + v + "');");
      }
      list.set(1, x);
    }

    return list;
  }

  @Override
  /**
   * testlist_star_expr: (test|star_expr) (',' (test|star_expr))* (',')?;
   **/
  public Object visitTestlist_star_expr(Testlist_star_exprContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * tfpdef: NAME (':' test)?;
   **/
  public Object visitTfpdef(TfpdefContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * trailer: '(' (arglist)? ')' | '[' subscriptlist ']' | '.' NAME;
   **/
  public Object visitTrailer(TrailerContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * try_stmt: ('try' ':' suite ((except_clause ':' suite)+ ('else' ':' suite)? ('finally' ':'
   * suite)? | 'finally' ':' suite));
   **/
  public Object visitTry_stmt(Try_stmtContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * typedargslist: (tfpdef ('=' test)? (',' tfpdef ('=' test)?)* (',' ( '*' (tfpdef)? (',' tfpdef
   * ('=' test)?)* (',' ('**' tfpdef (',')?)?)? | '**' tfpdef (',')?)?)? | '*' (tfpdef)? (',' tfpdef
   * ('=' test)?)* (',' ('**' tfpdef (',')?)?)? | '**' tfpdef (',')?);
   **/
  public Object visitTypedargslist(TypedargslistContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * varargslist: (vfpdef ('=' test)? (',' vfpdef ('=' test)?)* (',' ( '*' (vfpdef)? (',' vfpdef
   * ('=' test)?)* (',' ('**' vfpdef (',')?)?)? | '**' vfpdef (',')?)?)? | '*' (vfpdef)? (',' vfpdef
   * ('=' test)?)* (',' ('**' vfpdef (',')?)?)? | '**' vfpdef (',')? );
   **/
  public Object visitVarargslist(VarargslistContext ctx) {

    return this.visitChildren(ctx);
  }


  @Override
  /**
   * vfpdef: NAME;
   * 
   **/
  public Object visitVfpdef(VfpdefContext ctx) {
    var.add(ctx.getText());
    if (ctx.parent != null && ctx.parent instanceof VarrayContext) {
      return " $this->" + ctx.getText() + " ";
    }



    return " $" + ctx.getText() + " ";
  }

  @Override
  /**
   * while_stmt: 'while' test ':' suite ('else' ':' suite)?;
   **/
  public Object visitWhile_stmt(While_stmtContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * with_item: test ('as' expr)?;
   **/
  public Object visitWith_item(With_itemContext ctx) {
    // TODO Auto-generated method stub
    return this.visitChildren(ctx);
  }

  @Override
  /**
   * with_stmt: 'with' with_item (',' with_item)* ':' suite;
   **/
  public Object visitWith_stmt(With_stmtContext ctx) {
    // TODO Auto-generated method stub
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * xor_expr: and_expr ('^' and_expr)*;
   **/
  public Object visitXor_expr(Xor_exprContext ctx) {
    // TODO Auto-generated method stub
    return ops(ctx);
  }

  @Override
  /**
   * yield_arg: 'from' test | testlist;
   * 
   **/
  public Object visitYield_arg(Yield_argContext ctx) {
    // TODO Auto-generated method stub
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * yield_expr: 'yield' (yield_arg)?;
   **/
  public Object visitYield_expr(Yield_exprContext ctx) {
    // TODO Auto-generated method stub
    return "";// this.visitChildren(ctx);
  }

  @Override
  /**
   * yield_stmt: yield_expr;
   **/
  public Object visitYield_stmt(Yield_stmtContext ctx) {
    // TODO Auto-generated method stub
    return "";// this.visitChildren(ctx);
  }

}
