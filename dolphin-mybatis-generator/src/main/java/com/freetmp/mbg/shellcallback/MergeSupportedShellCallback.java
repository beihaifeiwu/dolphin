package com.freetmp.mbg.shellcallback;

import com.freetmp.mbg.merge.CompilationUnitMerger;
import org.apache.commons.io.FileUtils;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.io.FileUtils.*;

/*
 * Created by pin on 2015/2/7.
 */
public class MergeSupportedShellCallback extends DefaultShellCallback {

  static final Logger LOGGER = LoggerFactory.getLogger(MergeSupportedShellCallback.class);

  /*
   * @param overwrite if true overwrite the existed file
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
    LOGGER.info("merge java source file for {}", existingFileFullPath);
    try {
      mergedFileSource =  CompilationUnitMerger.merge(newFileSource, readFileToString(getFile(existingFileFullPath)));
    } catch (Exception e) {
      LOGGER.info("java source merge failed: {}", e);
      throw new ShellException(e);
    }

    return mergedFileSource;
  }

}
