package com.amg.framework.cloud.grpc.processor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;


public class CompilationUnits extends VoidVisitorAdapter {
    private CompilationUnit cu;

    private Set<MethodDeclaration> allEmement = new HashSet<>();

    public CompilationUnits(File file) throws Exception {
        cu = JavaParser.parse(file, "UTF-8");
    }

    public CompilationUnits(String path) {
        try {
            cu = JavaParser.parse(Files.newInputStream(Paths.get(path)));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public CompilationUnit getCompilationUnit() {
        return cu;
    }

    public String getSourceCode() {
        return cu.toString();
    }

    public Set<MethodDeclaration> getAllMethod() {
        this.visit(cu, null);
        return allEmement;
    }

    @Override
    public void visit(MethodDeclaration n, Object arg) {
        allEmement.add(n);
    }
}
