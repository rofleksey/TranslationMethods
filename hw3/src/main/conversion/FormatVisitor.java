package conversion;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import parser.CBaseVisitor;
import parser.CParser;

public class FormatVisitor extends CBaseVisitor<Void> {
    private StringBuilder builder;
    private int curIndent;

    public FormatVisitor() {
        builder = new StringBuilder();
        curIndent = 0;
    }

    public String result() {
        return builder.toString();
    }

    private void newLine() {
        builder.append("\n");
        for (int i = 0; i < curIndent; i++) {
            builder.append("  ");
        }
    }

    private void newLine(int changeIndent) {
        curIndent += changeIndent;
        newLine();
    }

    @Override
    public Void visitTerminal(TerminalNode node) {
        if (node.getSymbol().getType() != Token.EOF) {
            builder.append(node.getText());
        }
        return null;
    }

    @Override
    public Void visitAssList1(CParser.AssList1Context ctx) {
        visit(ctx.e1);
        builder.append(", ");
        visit(ctx.e2);
        return null;
    }

    @Override
    public Void visitMultOp(CParser.MultOpContext ctx) {
        visit(ctx.e1);
        builder.append(" ").append(ctx.op.getText()).append(" ");
        visit(ctx.e2);
        return null;
    }

    @Override
    public Void visitAddOp(CParser.AddOpContext ctx) {
        visit(ctx.e1);
        builder.append(" ").append(ctx.op.getText()).append(" ");
        visit(ctx.e2);
        return null;
    }

    @Override
    public Void visitRelOp(CParser.RelOpContext ctx) {
        visit(ctx.e1);
        builder.append(" ").append(ctx.op.getText()).append(" ");
        visit(ctx.e2);
        return null;
    }

    @Override
    public Void visitEqOp(CParser.EqOpContext ctx) {
        visit(ctx.e1);
        builder.append(" ").append(ctx.op.getText()).append(" ");
        visit(ctx.e2);
        return null;
    }

    @Override
    public Void visitAndOp(CParser.AndOpContext ctx) {
        visit(ctx.e1);
        builder.append(" ").append(ctx.op.getText()).append(" ");
        visit(ctx.e2);
        return null;
    }

    @Override
    public Void visitOrOp(CParser.OrOpContext ctx) {
        visit(ctx.e1);
        builder.append(" ").append(ctx.op.getText()).append(" ");
        visit(ctx.e2);
        return null;
    }

    @Override
    public Void visitAssExpr1(CParser.AssExpr1Context ctx) {
        visit(ctx.e1);
        builder.append(" ").append(ctx.op.getText()).append(" ");
        visit(ctx.e2);
        return null;
    }

    @Override
    public Void visitDeclaration(CParser.DeclarationContext ctx) {
        visit(ctx.e1);
        builder.append(" ");
        visit(ctx.e2);
        builder.append(";");
        return null;
    }

    @Override
    public Void visitInitDeclEq(CParser.InitDeclEqContext ctx) {
        visit(ctx.e1);
        builder.append(" = ");
        visit(ctx.e2);
        return null;
    }

    @Override
    public Void visitParamListComma(CParser.ParamListCommaContext ctx) {
        visit(ctx.e1);
        builder.append(", ");
        visit(ctx.e2);
        return null;
    }

    @Override
    public Void visitParameterDeclaration(CParser.ParameterDeclarationContext ctx) {
        visit(ctx.e1);
        builder.append(" ");
        visit(ctx.e2);
        return null;
    }

    @Override
    public Void visitCompoundStatement(CParser.CompoundStatementContext ctx) {
        builder.append("{");
        if (ctx.e != null) {
            newLine(1);
            visit(ctx.e);
            newLine(-1);
        }
        builder.append("}");
        if (!ctx.ignoreNextLine) {
            newLine();
        }
        return null;
    }

    @Override
    public Void visitBlockItemListN(CParser.BlockItemListNContext ctx) {
        visit(ctx.e1);
        if (!ctx.e1.ign) {
            newLine();
        }
        visit(ctx.e2);
        return null;
    }

    @Override
    public Void visitSelectionStatement(CParser.SelectionStatementContext ctx) {
        builder.append("if (");
        visit(ctx.e1);
        builder.append(") ");
        ctx.e2.ignoreNextLine = true;
        visit(ctx.e2);
        if (ctx.e3 != null) {
            builder.append(" else ");
            ctx.e3.ignoreNextLine = true;
            visit(ctx.e3);
        }
        return null;
    }

    @Override
    public Void visitIterationStatement(CParser.IterationStatementContext ctx) {
        builder.append(ctx.name.getText());
        builder.append(" (");
        visit(ctx.e1 != null ? ctx.e1 : ctx.f1);
        builder.append(") ");
        ctx.e2.ignoreNextLine = true;
        visit(ctx.e2);
        return null;
    }

    @Override
    public Void visitForCondition(CParser.ForConditionContext ctx) {
        if (ctx.d != null || ctx.a != null) {
            visit(ctx.d != null ? ctx.d : ctx.a);
        }
        builder.append(";").append(ctx.e1 != null ? " " : "");
        if (ctx.e1 != null) {
            visit(ctx.e1);
        }
        builder.append(";").append(ctx.e2 != null ? " " : "");
        if (ctx.e2 != null) {
            visit(ctx.e2);
        }
        return null;
    }

    @Override
    public Void visitForDeclaration(CParser.ForDeclarationContext ctx) {
        visit(ctx.e1);
        builder.append(" ");
        visit(ctx.e2);
        return null;
    }


    @Override
    public Void visitForExpr1(CParser.ForExpr1Context ctx) {
        visit(ctx.e1);
        builder.append(", ");
        visit(ctx.e2);
        return null;
    }

    @Override
    public Void visitJumpReturn(CParser.JumpReturnContext ctx) {
        builder.append("return");
        if (ctx.e1 != null) {
            builder.append(" ");
            visit(ctx.e1);
        }
        builder.append(";");
        return null;
    }

    @Override
    public Void visitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        visit(ctx.e1);
        builder.append(" ");
        visit(ctx.e2);
        builder.append(" ");
        visit(ctx.e3);
        return null;
    }

    @Override
    public Void visitStructSpecifier(CParser.StructSpecifierContext ctx) {
        builder.append("struct ").append(ctx.n.getText()).append(" ").append("{");
        if (ctx.list != null) {
            newLine(1);
            visit(ctx.list);
            newLine(-1);
        }
        builder.append("}");
        newLine();
        return null;
    }

    @Override
    public Void visitStructDec1(CParser.StructDec1Context ctx) {
        visit(ctx.e1);
        newLine();
        visit(ctx.e2);
        return null;
    }

    @Override
    public Void visitExprEmpty(CParser.ExprEmptyContext ctx) {
        return null;
    }

}
