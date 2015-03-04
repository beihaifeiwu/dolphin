package com.freetmp.mbg.shellcallback;

import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


        return mergedFileSource;
    }

}
