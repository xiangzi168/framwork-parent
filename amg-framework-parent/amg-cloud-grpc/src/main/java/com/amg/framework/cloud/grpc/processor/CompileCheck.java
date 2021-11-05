package com.amg.framework.cloud.grpc.processor;

import com.amg.framework.boot.base.exception.handle.ExceptionHandle;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.Type;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class CompileCheck {

    public static Map<String, String> sorcuesMap = new ConcurrentHashMap();

    public static List<String > parmList = new ArrayList() {{
        add("requestId_ = \"\";");
        add("errorCode_ = \"\";");
        add("msg_ = \"\";");
        add("success_ = false;");
    }};

    public static void colloc(String sourcePath) {
        List<File> fileList = (List<File>) FileUtils.listFiles(new File(sourcePath), null, true);
        fileList.stream().forEach(file -> {
            try {
                collocSorcues(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private static void collocSorcues(File file) throws Exception {
        if (file.getPath().endsWith(".java")) {
            CompilationUnits compilationUnits = new CompilationUnits(file);
            CompilationUnit compilationUnit = compilationUnits.getCompilationUnit();
            List<TypeDeclaration> types = compilationUnit.getTypes();
            if (CollectionUtils.isNotEmpty(types)) {
                for (TypeDeclaration t : types) {
                    sorcuesMap.put((compilationUnit.getPackage().getName().toString()) + "." + t.getName(), file.getPath());
                }
            }
        }
    }


    public static String getSorcues(String typeName, String filepath) throws Exception {
        CompilationUnits compilationUnits = new CompilationUnits(new File(filepath));
        CompilationUnit compilationUnit = compilationUnits.getCompilationUnit();
        return compilationUnit.toString();
    }


    public static boolean checkMethod(String typeName, String filepath, String serverName) {
        try {
            CompilationUnits compilationUnits = new CompilationUnits(new File(filepath));
            CompilationUnit compilationUnit = compilationUnits.getCompilationUnit();
            List<TypeDeclaration> typeDeclarations = compilationUnit.getTypes();
            if (((ClassOrInterfaceDeclaration)typeDeclarations.get(0)).getExtends().size() == 0) {
                throw new CompileException("Class: " + typeName + " must extend a GRPC ServiceImplBase");
            }
            String protoPath = "com.amg.fulfillment.cloud." + serverName + ".api.proto.";
            String grpcPath = "com.amg.fulfillment.cloud." + serverName + ".api.grpc.";
            for (MethodDeclaration m : compilationUnits.getAllMethod()) {
                List<AnnotationExpr> annotationExprs = m.getAnnotations();
                Type returntype = m.getType();
                List<Parameter> parameters = m.getParameters();
                if (parameters.size() >= 2) {
                    Parameter parameter = parameters.get(parameters.size() - 1);
                    // 判断方法参数
                    if ((parameter.getType().toString().startsWith("StreamObserver") ||
                            parameter.getType().toString().startsWith("io.grpc.stub.StreamObserver")) &&
                            "void".equals(returntype.toString()) && jiexizhujie(annotationExprs).contains("@Override")) {
                        // 校验返回语句
                        BlockStmt body = m.getBody();
                        List<Statement> statements = body.getStmts();
                        if (CollectionUtils.isEmpty(statements)) {
                            throw new CompileException("GRPC Service Class: " + typeName + " Method: '" + m.getName() + "' cannot be empty and must contain return statement 'returnGrpcResult(...)'");
                        }
                        Statement statement = statements.get(statements.size() - 1);
                        if (statement instanceof TryStmt) {
                            if (!panduanStam(statement)) {
                                throw new CompileException("GRPC Service Class: " + typeName + " Method: '" + m.getName() + "' must contain return statement 'returnGrpcResult(...)'");
                            }
                        } else {
                            statement = (ExpressionStmt)statement;
                            if (!((ExpressionStmt) statement).getExpression().toString().startsWith("returnGrpcResult(") &&
                                    !((ExpressionStmt) statement).getExpression().toString().startsWith("GrpcContext.returnGrpcResult(")) {
                                throw new CompileException("GRPC Service Class: " + typeName + " Method: '" + m.getName() + "' must contain return statement 'returnGrpcResult(...)'");
                            }
                        }

                        String type = parameter.getType().toString()
                                .replaceAll("io.grpc.stub.StreamObserver", "")
                                .replaceAll("StreamObserver", "")
                                .replaceAll("<", "")
                                .replaceAll(">", "")
                                .replaceAll(protoPath, "");
                        if (StringUtils.isBlank(type)) {
                            throw new CompileException("GRPC Service Class: " + typeName + " Method: " + m.getName() + " must contain parameters generic 'StreamObserver<...>'");
                        }
                        if (type.contains(".")) {
                            // 有.的是直接引入了.前面的类
                            String yinrulei = protoPath + type.split("\\.")[0];
                            String shitilei = type.split("\\.")[1];
                            String leifilepath = sorcuesMap.get(yinrulei); // 引入类的文件路径
                            if (StringUtils.isBlank(leifilepath)) {
                                throw new CompileException("Class " + typeName + " of Method " + m.getName() + " parameters the proto entity '" + shitilei + "' path must be in '" + protoPath +"...'");
                            }
                            return checkMethod1(typeName, leifilepath, shitilei, true);
                        } else {
                            String yinrulei = protoPath + type;
                            String shitilei = type;
                            String leifilepath = sorcuesMap.get(yinrulei); // 引入类的文件路径
                            if (StringUtils.isBlank(leifilepath)) {
                                throw new CompileException("Class " + typeName + " of Method " + m.getName() + " parameters the proto entity '" + shitilei + "' path must be in '" + protoPath +"...'");
                            }
                            return checkMethod1(typeName, leifilepath, shitilei, false);
                        }
                    }
                }
            }
            return false;
        } catch (Exception exception) {
            throw new CompileException(ExceptionHandle.getExceptionMessage(exception));
        }
    }


    private static boolean checkMethod1(String typeName, String leifilepath, String shitilei, boolean multiLevel) throws Exception {
        CompilationUnits compilayinru = new CompilationUnits(new File(leifilepath));
        CompilationUnit compilat = compilayinru.getCompilationUnit();
        if (multiLevel) {
            List<TypeDeclaration> types = compilat.getTypes();
            for (TypeDeclaration tt : types) {
                List<BodyDeclaration> bodyDeclarations = tt.getMembers();
                for (BodyDeclaration bb : bodyDeclarations) {
                    List<Node> ch = bb.getChildrenNodes();
                    for (Node n : ch) {
                        if (n.toString().startsWith("private " + shitilei + "() {")) {
                            List<Node> nodess = n.getChildrenNodes();
                            if (nodess.size() == 1) {
                                List<Node> protoparm = nodess.get(0).getChildrenNodes();
                                return checkprotoparm(protoparm, bb);
                            }
                            throw new CompileException("Class " + typeName + " of proto entity must contain parameters 'string requestId、 string errorCode、 string msg、 bool success、 * data'" +
                                    " and Method must contain parameters generic 'StreamObserver<...>'");
                        }
                    }
                }
            }
        } else {
            TypeDeclaration type = compilat.getTypes().get(0);
            List<BodyDeclaration> bodyDeclarations = type.getMembers();
            for (BodyDeclaration n : bodyDeclarations) {
                if (n.toString().startsWith("private " + shitilei + "() {")) {
                    List<Node> nodess = n.getChildrenNodes();
                    if (nodess.size() == 1) {
                        List<Node> protoparm = nodess.get(0).getChildrenNodes();
                        return checkprotoparm(protoparm, n);
                    }
                    throw new CompileException("Class " + typeName + " of proto entity must contain parameters 'string requestId、 string errorCode、 string msg、 bool success、 * data'" +
                            " and Method must contain parameters generic 'StreamObserver<...>'");
                }
            }
        }
        return false;
    }


    private static boolean checkprotoparm(List<Node> protoparm, BodyDeclaration bodyDeclaration) {
        if (protoparm.size() == 5) {
            int count = 0;
            for (Node nnn : protoparm) {
                if (nnn.toString().split("\\_")[0].equals("data")) {
                    count ++;
                } else {
                    if (parmList.contains(nnn.toString())){
                        count ++;
                    }
                }
            }
            if (count == 5) {
                return true;
            }
            return false;
        } else if (protoparm.size() == 4) {
            for (Node nnn : protoparm) {
                if (!parmList.contains(nnn.toString())){
                    return false;
                }
            }
            List<Node> ch = bodyDeclaration.getChildrenNodes();
            for (Node n : ch) {
                if (CollectionUtils.isNotEmpty(n.getChildrenNodes())) {
                    Node node = n.getChildrenNodes().get(n.getChildrenNodes().size() - 1);
                    if (node != null && "data_".equals(node.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private static List<String> jiexizhujie(List<AnnotationExpr> annotationExprs) {
        List<String> list = new ArrayList<>();
        for (AnnotationExpr annotationExpr : annotationExprs) {
            list.add(annotationExpr.toString());
        }
        return list;
    }


    private static boolean panduanStam(Statement statement) {
        if (statement instanceof TryStmt) {
            List<Statement> list = ((TryStmt) statement).getTryBlock().getStmts();
            statement = list.get(list.size() - 1);
            if ((statement instanceof TryStmt)) {
                return panduanStam((statement));
            } else {
                statement = (ExpressionStmt)statement;
                return (((ExpressionStmt) statement).getExpression().toString().startsWith("returnGrpcResult(") ||
                        ((ExpressionStmt) statement).getExpression().toString().startsWith("GrpcContext.returnGrpcResult("));
            }
        } else {
            statement = (ExpressionStmt)statement;
            return (((ExpressionStmt) statement).getExpression().toString().startsWith("returnGrpcResult(") ||
                    ((ExpressionStmt) statement).getExpression().toString().startsWith("GrpcContext.returnGrpcResult("));
        }
    }

}
