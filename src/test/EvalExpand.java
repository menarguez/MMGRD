package test;
import java.io.PrintStream;
import java.io.StringWriter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.util.WriterOutputStream;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.eval.DoubleEvaluator;

import edu.jas.kern.ComputerThreads;

public class EvalExpand {
  public static void main(String[] args) {
    // Static initialization of the MathEclipse engine instead of null 
    // you can set a file name to overload the default initial
    // rules. This step should be called only once at program setup:
    F.initSymbols(null);
    ScriptEngineManager fScriptManager = new ScriptEngineManager();
    ScriptEngine fScriptEngine = fScriptManager.getEngineByExtension("m");
    EvalUtilities util = new EvalUtilities();
    IExpr result;

    try {
      StringBufferWriter buf = new StringBufferWriter();
      String input = "Expand[(AX^2+BX)^2]";
    

      
      
	  ASTNode node = null;
	  input = "x*3^7625597484987";
      try {
			Parser parser = new Parser(true);
			node = parser.parse(input);
		} catch (SyntaxError se) {
			Parser parser = new Parser(false);
			node = parser.parse(input);
		}
      
      IExpr inExpr = AST2Expr.CONST.convert(node);
      inExpr = F.N(inExpr);
      EvalEngine engine = new EvalEngine();
      buf = new StringBufferWriter();
      IExpr evaluationResult = engine.evaluate(inExpr);
      util.evaluate(inExpr);
      result = util.evaluate(input);
      OutputFormFactory.get().convert(buf, result);
//      output = buf.toString();
//      Parser p = new Parser();
//      ASTNode obj = p.parse(input);
//      System.out.println(obj.toString());
//      System.out.println("Expanded form for " + input + " is " + output);
//      
//      buf = new StringBufferWriter();
//      input = "Factor["+output+"]";
//      result = util.evaluate(input);
//      OutputFormFactory.get().convert(buf, result);
//      output = buf.toString();
//      System.out.println("Factored form for " + input + " is " + output);
    } catch (final Exception e) {
      e.printStackTrace();
    } finally {
      // Call terminate() only one time at the end of the program  
      ComputerThreads.terminate();
    }

  }
}