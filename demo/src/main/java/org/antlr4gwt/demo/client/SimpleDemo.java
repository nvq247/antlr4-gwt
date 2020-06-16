package org.antlr4gwt.demo.client;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream; 
import org.antlr4gwt.demo.shared.grammar.Python3Lexer;
import org.antlr4gwt.demo.shared.grammar.Python3Parser;
import org.antlr4gwt.demo.shared.grammar.Python3Parser.File_inputContext;
import org.antlr4gwt.demo.shared.grammar.Python3ToJs;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SimpleDemo implements EntryPoint {

	public void onModuleLoad() {
		String good = "(a b (c d e f) (a 'mystring' 9))";
		
		// You type your code here
		final TextArea code = new TextArea();
		final TextArea time = new TextArea();
		code.setWidth("100%");
		code.setText(good);
		
		// Result label (ok or ko)
		final Label result= new Label();
		Button checkButton = new Button("Check", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Python3Parser parser = dummyParser(code.getText(),Integer.parseInt(time.getValue().trim()));
				if (parser.getNumberOfSyntaxErrors() > 0) {
					result.setText("KO -- There is " + parser.getNumberOfSyntaxErrors() + " syntax errors");
				} else {
					result.setText("OK -- No syntax error");
				}
			}
		});
		time.setValue("1000");
		// Should not use vertical panel
		VerticalPanel panel = new VerticalPanel();
		panel.add(code);
		panel.add(time);
		panel.add(checkButton);
		panel.add(result);
		RootPanel.get("main").add(panel);
	}
	
	Python3Parser dummyParser(String txt,int tn) {
		long t=System.currentTimeMillis();
		Python3Parser parser=null;
		File_inputContext tree=null;
		for(int i=0;i<tn;i++) {
			Python3Lexer lexer = new Python3Lexer(new ANTLRInputStream(txt));
			
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			 parser = new Python3Parser(tokens);
		 
			// Actually launch the parsing
			parser.setBuildParseTree(true);
			
			 tree = parser.file_input();
		}
		Object js = tree.accept(new Py2Ph());
		
		t=System.currentTimeMillis()-t;
		Window.alert(t+" "+js.toString());
		
		return parser;
	}
}
