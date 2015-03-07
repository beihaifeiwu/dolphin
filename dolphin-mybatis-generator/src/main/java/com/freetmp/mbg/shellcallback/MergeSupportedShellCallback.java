package com.freetmp.mbg.shellcallback;

import com.freetmp.mbg.util.JavaSourceUtils;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by pin on 2015/2/7.
 */
public class MergeSupportedShellCallback extends DefaultShellCallback {

    static final Logger LOGGER = LoggerFactory.getLogger(MergeSupportedShellCallback.class);

    /**
     * @param overwrite
     */
    public MergeSupportedShellCallback(boolean overwrite) {
        super(overwrite);
    }

    @Override
    public boolean isMergeSupported() {
        return true;
    }

    @Override
    public String mergeJavaFile(String newFileSource, String existingFileFullPath, String[] javadocTags, String fileEncoding) throws ShellException {
        String mergedFileSource = newFileSource;
        try {
            CompilationUnit source = JavaSourceUtils.generateAst(newFileSource);
            CompilationUnit destination = JavaSourceUtils.generateAst(FileUtils.readFileToString(new File(existingFileFullPath),fileEncoding));
            if(destination != null && source != null) {
                mergedFileSource = JavaSourceUtils.mergeContent(source, destination);
            }
        } catch (Exception e) {
            throw new ShellException(e);
        }

        return mergedFileSource;
    }

}
