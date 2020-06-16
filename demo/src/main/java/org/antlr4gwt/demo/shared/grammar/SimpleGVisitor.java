// Generated from SimpleG.g4 by ANTLR 4.4

package org.antlr4gwt.demo.shared.grammar;
//This file had been generated by Antlr4.4 the desktop version

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SimpleGParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SimpleGVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SimpleGParser#first_cell}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFirst_cell(@NotNull SimpleGParser.First_cellContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleGParser#root}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoot(@NotNull SimpleGParser.RootContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleGParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(@NotNull SimpleGParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimpleGParser#cell}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCell(@NotNull SimpleGParser.CellContext ctx);
}