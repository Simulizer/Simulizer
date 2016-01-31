package javafxprototype.parser;

import javafxprototype.parser.SmallMipsParser.Instruction2vContext;
import javafxprototype.parser.SmallMipsParser.Instruction3Context;

public class MyTreeListener extends SmallMipsBaseListener {
	@Override
	public void enterInstruction3(Instruction3Context ctx) {
		String opcode = ctx.getChild(0).getText();
		Register r1 = Register.getRegister(ctx.getChild(1).getText());
		Register r2 = Register.getRegister(ctx.getChild(3).getText());
		Register r3 = Register.getRegister(ctx.getChild(5).getText());
		
		if (opcode.equals("add")) {
			// Token t = (Token) ctx.getChild(0).getPayload();
			r1.setValue(r2.getValue() + r3.getValue());
		} else {
			System.out.println("TODO: enterInstruction3(" + opcode + ")");
		}
	}

	@Override
	public void enterInstruction2v(Instruction2vContext ctx) {
		String opcode = ctx.getChild(0).getText();
		Register r1 = Register.getRegister(ctx.getChild(1).getText());
		int v = Integer.valueOf(ctx.getChild(3).getText());
		
		if (opcode.equals("li")) {
			r1.setValue(v);
		} else {
			System.out.printf("TODO: enterInstruction2v(%s)%n", opcode);
		}
	}
}
