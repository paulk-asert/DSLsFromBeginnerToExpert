
import java.security.CodeSource

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*

import org.codehaus.groovy.classgen.*

import org.codehaus.groovy.control.*
import org.codehaus.groovy.control.CompilationUnit.PrimaryClassNodeOperation
import org.codehaus.groovy.control.messages.ExceptionMessage

class CustomerShell {
    Number evaluate(String text) {
        try {
            CustomerCustomClassLoader loader = new CustomerCustomClassLoader()
            Class clazz = loader.parseClass(text)
            Script script = (Script)clazz.newInstance();
            Object result = script.run()
            if (!(result instanceof Number)) throw new IllegalStateException("Script returned a non-number: $result");
            return (Number)result
        } catch (SecurityException ex) {
            throw new SecurityException("Could not evaluate script: $text", ex)
        } catch (MultipleCompilationErrorsException mce) {
            //this allows compilation errors to be seen by the user
            mce.errorCollector.errors.each {
                if (it instanceof ExceptionMessage && it.cause instanceof SecurityException) {
                    throw it.cause
                }
            }
            throw mce
        }
    }
}

class CustomerCustomClassLoader extends GroovyClassLoader {
    protected CompilationUnit createCompilationUnit(CompilerConfiguration config, CodeSource codeSource) {
        CompilationUnit cu = super.createCompilationUnit(config, codeSource)
        // wiring into the SEMANTIC_ANALYSIS phase will provide more type information that the CONVERSION phase.
        cu.addPhaseOperation(new CustomerFilteringNodeOperation(), Phases.SEMANTIC_ANALYSIS)
        return cu
    }
}

private class CustomerFilteringNodeOperation extends PrimaryClassNodeOperation {

    void call(SourceUnit source, GeneratorContext context, ClassNode classNode) {
        ModuleNode ast = source.getAST()

        if (ast.getImports()) { throw new SecurityException("Imports of the form 'import package.ClassName' are not allowed.") }
        if (ast.getStarImports()) { throw new SecurityException("Imports of the form 'import package.*' are not allowed.") }
        if (ast.getStaticImports()) { throw new SecurityException("Static imports of the form 'import static package.ClassName.fieldOrMethodName' are not allowed.") }
        def staticStarImports = ast.getStaticStarImports().keySet() as List
        if (staticStarImports != ['java.lang.Math']) { throw new SecurityException("Only java.lang.Math is allowed for static imports. Found: " + staticStarImports) }

        // do not allow package names
        if (ast.getPackage()) { throw new SecurityException("Package definitions are not allowed.") }

        // do not allow method definitions
        if (ast.getMethods()) { throw new SecurityException("Method definitions are not allowed.") }

        // enforce arithmetic only expressions
        ast.getStatementBlock().visit(new CustomerExpressionEnforcer())
    }
}

private class CustomerExpressionEnforcer implements GroovyCodeVisitor {
    private static final allowedStaticImports = [Math].asImmutable()
    void visitStaticMethodCallExpression(StaticMethodCallExpression smce) {
       if (!allowedStaticImports.contains(smce.ownerType.getTypeClass())) {
          throw new SecurityException("Static method call expressions forbidden in Customer shell.")
       }
    }

    void visitWhileLoop(WhileStatement whileStatement) {
       throw new SecurityException("While statements forbidden in Customer shell.")
    }

    void visitConstructorCallExpression(ConstructorCallExpression constructorCallExpression) {

    }

    void visitTernaryExpression(TernaryExpression ternaryExpression) {

    }

    void visitShortTernaryExpression(ElvisOperatorExpression elvisOperatorExpression) {

    }

    void visitBinaryExpression(BinaryExpression binaryExpression) {

    }

    void visitPrefixExpression(PrefixExpression prefixExpression) {

    }

    void visitPostfixExpression(PostfixExpression postfixExpression) {

    }

    void visitBooleanExpression(BooleanExpression booleanExpression) {

    }

    void visitClosureExpression(ClosureExpression closureExpression) {

    }

    void visitTupleExpression(TupleExpression tupleExpression) {

    }

    void visitMapExpression(MapExpression mapExpression) {

    }

    void visitMapEntryExpression(MapEntryExpression mapEntryExpression) {

    }

    void visitListExpression(ListExpression listExpression) {

    }

    void visitRangeExpression(RangeExpression rangeExpression) {

    }

    void visitPropertyExpression(PropertyExpression propertyExpression) {

    }

    void visitAttributeExpression(AttributeExpression attributeExpression) {

    }

    void visitFieldExpression(FieldExpression fieldExpression) {

    }

    void visitMethodPointerExpression(MethodPointerExpression methodPointerExpression) {

    }

    void visitConstantExpression(ConstantExpression constantExpression) {

    }

    void visitClassExpression(ClassExpression classExpression) {

    }

    void visitVariableExpression(VariableExpression variableExpression) {

    }

    void visitDeclarationExpression(DeclarationExpression declarationExpression) {

    }

    void visitGStringExpression(GStringExpression gStringExpression) {

    }

    void visitArrayExpression(ArrayExpression arrayExpression) {

    }

    void visitSpreadExpression(SpreadExpression spreadExpression) {

    }

    void visitSpreadMapExpression(SpreadMapExpression spreadMapExpression) {

    }

    void visitNotExpression(NotExpression notExpression) {

    }

    void visitUnaryMinusExpression(UnaryMinusExpression unaryMinusExpression) {

    }

    void visitUnaryPlusExpression(UnaryPlusExpression unaryPlusExpression) {

    }

    void visitBitwiseNegationExpression(BitwiseNegationExpression bitwiseNegationExpression) {

    }

    void visitCastExpression(CastExpression castExpression) {

    }

    void visitArgumentlistExpression(ArgumentListExpression argumentListExpression) {

    }

    void visitClosureListExpression(ClosureListExpression closureListExpression) {

    }

    void visitBytecodeExpression(BytecodeExpression bytecodeExpression) {

    }

    void visitBlockStatement(BlockStatement blockStatement) {

    }

    void visitForLoop(ForStatement forStatement) {

    }

    void visitDoWhileLoop(DoWhileStatement doWhileStatement) {

    }

    void visitIfElse(IfStatement ifStatement) {

    }

    void visitExpressionStatement(ExpressionStatement expressionStatement) {

    }

    void visitReturnStatement(ReturnStatement returnStatement) {

    }

    void visitAssertStatement(AssertStatement assertStatement) {

    }

    void visitTryCatchFinally(TryCatchStatement tryCatchStatement) {

    }

    void visitSwitch(SwitchStatement switchStatement) {

    }

    void visitCaseStatement(CaseStatement caseStatement) {

    }

    void visitBreakStatement(BreakStatement breakStatement) {

    }

    void visitContinueStatement(ContinueStatement continueStatement) {

    }

    void visitThrowStatement(ThrowStatement throwStatement) {

    }

    void visitSynchronizedStatement(SynchronizedStatement synchronizedStatement) {

    }

    void visitCatchStatement(CatchStatement catchStatement) {

    }

    void visitMethodCallExpression(MethodCallExpression methodCallExpression) {

    }
}
