package org.antlr4gwt.demo.shared.grammar;

import java.util.ArrayList;
import java.util.Iterator;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.And_exprContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.And_testContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.Arith_exprContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.AtomContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.Comp_forContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.Comp_opContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.ComparisonContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.ExprContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.LambdefContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.Not_testContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.Or_testContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.PowerContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.Shift_exprContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.SubscriptlistContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.TermContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.TestContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.Testlist_compContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.TrailerContext;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.Xor_exprContext;

public class Python3ToJs implements ParseTreeVisitor {
	static int xx = 0;

	@Override
	public Object visit(ParseTree node) {
		if (node instanceof AtomContext) {
			for (int i = 0; i < node.getChildCount(); i++) {
				System.out.println(node.getChild(i).getText());
			}
		}
		return node;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object visitChildren(RuleNode node) {
		if (node == null) {
			return "";
		}
		xx++;
		//System.out.println(node.getClass());

		NodeTextList list = new NodeTextList();

		// a if b else c -> b?a:c
		if (node instanceof TestContext && node.getChildCount()>1) {
			if (node.getChildCount() == 5) {
				list.all("__test(",node.getChild(2).accept(this),")");
				list.add("?");
				list.add(node.getChild(0).accept(this));
				list.add(":");
				list.add(node.getChild(4).accept(this));
			} else {
				// unknow error
			}
		}
		// lambda a1 : a3  -> (a1)=>a3
		// lambda    : a2  -> (  )=>a2
		else if (node instanceof LambdefContext) {
			if(node.getChildCount()==3) {
				
				list.add("()=>");
				list.add(node.getChild(2).accept(this));
				
			}else if(node.getChildCount()==4) {
				list.add("(");
				list.add(node.getChild(1).accept(this) );
				list.add(")=>");
				list.add(node.getChild(3).accept(this));
			}else {
				list.add(node.getText());
			}
			
		}
		// a=abc
		// a[1:] bc, a[:1] a, a[1:2] b
		//==>
		//String.prototype._substr=function(start,end){}
				else if (
						node instanceof TrailerContext && 
						node.getChildCount()==3 && 
						
						node.getChild(1) instanceof SubscriptlistContext &&
						node.getChild(1).getChildCount()== 1 &&
								node.getChild(1).getChild(0).getChildCount()> 1 &&
						(node.getChild(1).getChild(0).getChild(0).getText().equals(":") || node.getChild(1).getChild(0).getChild(1).getText().equals(":") )
						) {
					 
					if(node.getChild(1).getChild(0).getChild(0).getText().equals(":")) {
						list.add("._substr(0,");
						list.add(node.getChild(1).getChild(0).getChild(1).accept(this));
						list.add(")");
					}else {
						list.add("._substr(");
						list.add(node.getChild(1).getChild(0).getChild(0).accept(this));
						if(node.getChild(1).getChild(0).getChildCount()==3)	{
							list.add(",");
							list.add(node.getChild(1).getChild(0).getChild(2).accept(this));
						}
						
						list.add(")");
						
					}
				}
		//not  ==> function not(a){return !a;}
				else if(node instanceof Not_testContext && node.getChildCount()==2) {
					list.add("not(");
					list.add(node.getChild(1).accept(this));
					list.add(")");
					
				}
		       //multi parameters 
				else if(node.getChildCount()>1 && 
						( 
								//node instanceof And_exprContext    ||
								//node instanceof Xor_exprContext    ||
								//node instanceof ExprContext        ||
								//node instanceof PowerContext       ||
								node instanceof Or_testContext     ||
								node instanceof And_testContext    
								
								)
						
						)
				{
					StringBuilder bb=new StringBuilder();
					list.add(bb);
					int cnt = node.getChildCount();
					String func=node.getChild(1).getText();
					if(func.equals("&&"))func="and";
					else if(func.equals("||"))func="or";
					else if(func.equals("|"))func="expr";
					else if(func.equals("^"))func="xor_exp";
					else if(func.equals("$"))func="and_exp";
					else if(func.equals("**"))func="power";
					 
					bb.append("__").append(func).append("(");
					if(!oneAll(node.getChild(0)))bb.append("()=>");
					bb.append(node.getChild(0).accept(this));
					for(int i=1;i<cnt;i++) {
						ParseTree child = node.getChild(i);
						if(child.getChildCount()>0  && !(child instanceof Comp_opContext)) {
							  boolean oa = oneAll(child);
							  bb.append(",");
							  if(!oa)bb.append("()=>");
							  bb.append(child.accept(this));
						}
						
					}
					bb.append(")");
					
				}
		
				else if(node.getChildCount()>1 && 
						(       node instanceof TermContext        ||
								node instanceof Arith_exprContext  ||
								node instanceof Shift_exprContext  ||
								node instanceof ComparisonContext  ||
								node instanceof And_exprContext    ||
								node instanceof Xor_exprContext    ||
								node instanceof ExprContext        ||
								node instanceof PowerContext       ||
								node instanceof Or_testContext     ||
								node instanceof And_testContext    
								
								)
						
						) {
					StringBuilder bb=new StringBuilder();
					list.add(bb);
					int cnt = node.getChildCount();
					for(int i=0;i<cnt;i++) {
						ParseTree child = node.getChild(i);
						if(child.getChildCount()==0 ||  child instanceof Comp_opContext) {
							 
							 bb.append('(');
							 list.add(")");
							 list.add("['" +child.getText()+ "'](");
							 list.add(node.getChild(i+1).accept(this));
							 list.add(")");
							 i++;
						}else {
							 list.add(child.accept(this));
						}
						
					}
					
				}
		// test for a1 in a3 (if a5) -> _.map(_.filter(a3,a1->a5), a1=>test);
		// test for a1 in a3 -> _.map(a3, a1=>test);
		else if(node instanceof AtomContext && node.getChildCount()==3 && node.getChild(1) instanceof Testlist_compContext  && node.getChild(1).getChildCount()==2 && node.getChild(1).getChild(1) instanceof Comp_forContext	 	 ) {
			return node.getChild(1).accept(this);
		}
		else if (node instanceof Testlist_compContext && node.getChildCount() == 2 && node.getChild(1) instanceof Comp_forContext) {
			list.add(makeComFor(node,null));
		} else {

			for (int i = 0; i < node.getChildCount(); i++) {
				ParseTree child = node.getChild(i);
				if (child.getChildCount() > 0) {
					list.add(child.accept(this));
				} else {
					list.add(child.getText());
				}
			}

		}

		return list;
	}

	@SuppressWarnings("unchecked")
	private Object makeComFor(ParseTree node,Object test) {
		NodeTextList list = new NodeTextList();
       ParseTree iter=test==null?node.getChild(1):node;
		// test for a1 in a3 ->   _.map(a3, a1=>test) ||  a3.map(a1=>test);
		if (iter.getChildCount() == 4) {
			
			Object a3 = iter.getChild(3).accept(this);
			Object a1=iter.getChild(1).accept(this);
			Object test1=test!=null?test:node.getChild(0).accept(this);
			list.all(a3,".map(",a1,"=>",test1,")");
		}
		// test for a1 in a3 (if a5) -> _.map(_.filter(a3,a1->a5), a1=>test)  | a3.filter;

		else if (iter.getChildCount() == 5) {
			ParseTree ifOrFor = iter.getChild(4).getChild(0);

			boolean isFor = (ifOrFor instanceof Comp_forContext);
			// makeComFor(node,list);
			if (!isFor) {
				
				
				list.all(
						iter.getChild(3).accept(this),
						".filter(",
						  iter.getChild(1).accept(this),
						"=>__test(",
						  ifOrFor.getChild(1).accept(this),
						")).map(",
						  iter.getChild(1).accept(this),
						"=>",
						  test!=null?test:node.getChild(0).accept(this),
						")");
			}
			
			// [ test for a1 in a3 for a41 in a43   for a441 in a443 if a4441 ]
			// a3.map(a1=>a43.map(a41=>a443.filter(a441=>a4441).map(a441=>test))))
			 //_.map([1,2,3,4],b=>_.map([10,20,30],a=>a+b))
			
			else {
				
				list.all( 
					iter.getChild(3).accept(this),
						".map(",
						iter.getChild(1).accept(this),"=>",
						makeComFor(iter.getChild(4).getChild(0),test!=null?test:node.getChild(0).accept(this)),
					").flat()");
			}
		} else {
			return node.getText();
		}
		return list;
	}

	@Override
	public Object visitTerminal(TerminalNode node) {
		// TODO Auto-generated method stub
		return node;
	}

	@Override
	public Object visitErrorNode(ErrorNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	static ParseTree find(ParseTree node, Class<RuleNode> t) {
		if (node == null)
			return null;
		if (node.getClass() == t)
			return node;
		return find(node.getChild(0), t);
	}
	 static boolean oneAll(ParseTree  node) {
		 while(node.getChildCount()>0) {
			 if(node.getChildCount()>1)return false;
			 
			 node=node.getChild(0);
		 }
		return true;
	}

}

class  NodeTextList <T> extends ArrayList<T> {
	public String toString() {
		Iterator it = iterator();
		if (!it.hasNext())
			return "";
		StringBuilder sb = new StringBuilder();
		for (;;) {
			Object e = it.next();
			sb.append(e == this ? "" : e);
			if (!it.hasNext())
				return sb.toString();
		}
	}
	public NodeTextList all(T... e) {
		 if(e!=null) {
			 for(T t:e)add(t);
		 }
		  return this;
	}
};
