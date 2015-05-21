package com.freetmp.maven.mbg.extend.plugin;

import com.freetmp.mbg.comment.CommentGenerator;
import com.freetmp.mbg.plugin.*;
import com.freetmp.mbg.plugin.batch.BatchInsertPlugin;
import com.freetmp.mbg.plugin.batch.BatchUpdatePlugin;
import com.freetmp.mbg.plugin.geom.PostgisGeoPlugin;
import com.freetmp.mbg.plugin.page.*;
import com.freetmp.mbg.plugin.upsert.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.*;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.ClassloaderUtility;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by LiuPin on 2015/1/30.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class MyBatisGeneratorMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  @Parameter(property = "mybatis.generator.outputDirectory",
             defaultValue = "${project.build.directory}/generated-sources/mybatis-generator", required = true)
  private File outputDirectory;

  /**
   * Location of the configuration file.
   */
  @Parameter(property = "mybatis.generator.configurationFile",
             defaultValue = "${basedir}/src/main/resources/generatorConfig.xml", required = true)
  private File configurationFile;

  /**
   * Specifies whether the mojo writes progress messages to the log
   */
  @Parameter(property = "mybatis.generator.verbose", defaultValue = "false")
  private boolean verbose;

  /**
   * Specifies whether the mojo overwrites existing files. Default is false.
   */
  @Parameter(property = "mybatis.generator.overwrite", defaultValue = "false")
  private boolean overwrite;

  /**
   * Location of a SQL script file to run before generating code. If null,
   * then no script will be run. If not null, then jdbcDriver, jdbcURL must be
   * supplied also, and jdbcUserId and jdbcPassword may be supplied.
   */
  @Parameter(property = "mybatis.generator.sqlScript")
  private String sqlScript;

  /**
   * JDBC Driver to use if a sql.script.file is specified
   */
  @Parameter(property = "mybatis.generator.jdbcDriver")
  private String jdbcDriver;

  /**
   * JDBC URL to use if a sql.script.file is specified
   */
  @Parameter(property = "mybatis.generator.jdbcURL")
  private String jdbcURL;

  /**
   * JDBC user ID to use if a sql.script.file is specified
   */
  @Parameter(property = "mybatis.generator.jdbcUserId")
  private String jdbcUserId;

  /**
   * JDBC password to use if a sql.script.file is specified
   */
  @Parameter(property = "mybatis.generator.jdbcPassword")
  private String jdbcPassword;

  /**
   * Comma delimited list of table names to generate
   */
  @Parameter(property = "mybatis.generator.tableNames")
  private String tableNames;

  /**
   * Comma delimited list of contexts to generate
   */
  @Parameter(property = "mybatis.generator.contexts")
  private String contexts;

  /**
   * set true to disable the extend methods(include batch,upsert etc)
   */
  @Parameter(defaultValue = "false", property = "x.mybatis.generator.disableExtendMethods")
  private boolean disableExtendMethods;

  /**
   * set true to disable the geom support
   */
  @Parameter(defaultValue = "false", property = "x.mybatis.generator.disableGeom")
  private boolean disableGeom;

  /**
   * set true to disable the name conversion
   */
  @Parameter(defaultValue = "false", property = "x.mybatis.generator.disableNameConversion")
  private boolean disableNameConversion;

  /**
   * set true to disable the pagination
   */
  @Parameter(defaultValue = "false", property = "x.mybatis.generator.disablePagination")
  private boolean disablePagination;

  /**
   * set true to disable the content merge
   */
  @Parameter(defaultValue = "false", property = "x.mybatis.generator.disableMergeSupport")
  private boolean disableMergeSupport;

  /**
   * set true to enable QueryDsl support
   */
  @Parameter(defaultValue = "false", property = "x.mybatis.generator.enableQueryDslSupport")
  private boolean enableQueryDslSupport;

  /**
   * the regex pattern used to match the word in the column name
   */
  @Parameter(defaultValue = "[a-zA-Z0-9]+", property = "x.mybatis.generator.columnPattern")
  private String columnPattern;

  /**
   * the srid used by the geom to standard space identifier quote
   */
  @Parameter(defaultValue = "3857", property = "x.mybatis.generator.srid")
  private String srid;

  /**
   * the path to the i18n resources directory
   */
  @Parameter(property = "x.mybatis.generator.i18nPath")
  private File i18nPath;

  /**
   * the locale used by the i18n path
   */
  @Parameter(defaultValue = "en_US", property = "x.mybatis.generator.locale")
  private String locale;

  /**
   * the start year of the project used by copyright generated
   */
  @Parameter(property = "x.mybatis.generator.projectStartYear")
  private String projectStartYear;

  public void execute() throws MojoExecutionException {

    printAnnounce();

    // add resource directories to the classpath.  This is required to support
    // use of a properties file in the build.  Typically, the properties file
    // is in the project's source tree, but the plugin classpath does not
    // include the project classpath.
    @SuppressWarnings("unchecked")
    List<Resource> resources = project.getResources();
    List<String> resourceDirectories = new ArrayList<String>();
    for (Resource resource : resources) {
      resourceDirectories.add(resource.getDirectory());
    }
    ClassLoader cl = ClassloaderUtility.getCustomClassloader(resourceDirectories);
    ObjectFactory.addResourceClassLoader(cl);

    if (configurationFile == null) {
      throw new MojoExecutionException(
          Messages.getString("RuntimeError.0")); //$NON-NLS-1$
    }

    List<String> warnings = new ArrayList<String>();

    if (!configurationFile.exists()) {
      throw new MojoExecutionException(Messages.getString(
          "RuntimeError.1", configurationFile.toString())); //$NON-NLS-1$
    }

    runScriptIfNecessary();

    Set<String> fullyQualifiedTables = new HashSet<String>();
    if (StringUtility.stringHasValue(tableNames)) {
      StringTokenizer st = new StringTokenizer(tableNames, ","); //$NON-NLS-1$
      while (st.hasMoreTokens()) {
        String s = st.nextToken().trim();
        if (s.length() > 0) {
          fullyQualifiedTables.add(s);
        }
      }
    }

    Set<String> contextsToRun = new HashSet<String>();
    if (StringUtility.stringHasValue(contexts)) {
      StringTokenizer st = new StringTokenizer(contexts, ","); //$NON-NLS-1$
      while (st.hasMoreTokens()) {
        String s = st.nextToken().trim();
        if (s.length() > 0) {
          contextsToRun.add(s);
        }
      }
    }

    try {
      ConfigurationParser cp = new ConfigurationParser(
          project.getProperties(), warnings);
      Configuration config = cp.parseConfiguration(configurationFile);

      ShellCallback callback = new MavenShellCallback(this, overwrite);

      // 扩展原配置
      extendConfig(config, (MavenShellCallback) callback);

      MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
          callback, warnings);

      myBatisGenerator.generate(new MavenProgressCallback(getLog(),
          verbose), contextsToRun, fullyQualifiedTables);

    } catch (XMLParserException e) {
      for (String error : e.getErrors()) {
        getLog().error(error);
      }

      throw new MojoExecutionException(e.getMessage());
    } catch (SQLException e) {
      throw new MojoExecutionException(e.getMessage());
    } catch (IOException e) {
      throw new MojoExecutionException(e.getMessage());
    } catch (InvalidConfigurationException e) {
      for (String error : e.getErrors()) {
        getLog().error(error);
      }

      throw new MojoExecutionException(e.getMessage());
    } catch (InterruptedException e) {
      // ignore (will never happen with the DefaultShellCallback)
      ;
    }

    for (String error : warnings) {
      getLog().warn(error);
    }

    if (project != null && outputDirectory != null
        && outputDirectory.exists()) {
      project.addCompileSourceRoot(outputDirectory.getAbsolutePath());

      Resource resource = new Resource();
      resource.setDirectory(outputDirectory.getAbsolutePath());
      resource.addInclude("**/*.xml");
      project.addResource(resource);
    }
  }

  /**
   * 打印一些通知
   */
  private void printAnnounce() {
    getLog().info("Welcome using the XMBG!");
    getLog().info("issue report: https://github.com/beihaifeiwu/dolphin/issues");
  }

  /**
   * 扩展配置实现XMBG
   *
   * @param config
   * @param callback
   */
  private void extendConfig(Configuration config, MavenShellCallback callback) {
    List<Context> contexts = config.getContexts();
    if (contexts == null) return;

    // fix java file encoding
    for (Context context : contexts) {
      context.addProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING, "UTF-8");
    }

    if (!disableNameConversion) {
      PluginConfiguration pluginConfiguration = new PluginConfiguration();
      pluginConfiguration.setConfigurationType(ColumnNameConversionPlugin.class.getTypeName());
      pluginConfiguration.addProperty(ColumnNameConversionPlugin.COLUMN_PATTERN_NAME, columnPattern);
      addToContext(contexts, pluginConfiguration);
      if (verbose) getLog().info("enable name conversion service");
    }

    if (!disableExtendMethods) {
      PluginConfiguration pluginConfiguration = new PluginConfiguration();
      pluginConfiguration.setConfigurationType(BatchInsertPlugin.class.getTypeName());
      addToContext(contexts, pluginConfiguration);
      if (verbose) getLog().info("enable batch insert service");

      pluginConfiguration = new PluginConfiguration();
      pluginConfiguration.setConfigurationType(BatchUpdatePlugin.class.getTypeName());
      addToContext(contexts, pluginConfiguration);
      if (verbose) getLog().info("enable batch update service");

      for(Context context : contexts){
        chooseUpsertPlugin(context);
      }
    }

    if (!disableGeom) {
      for (Context context : contexts) {
        chooseGeomPlugin(context);
      }
    }

    if (!disablePagination) {
      for (Context context : contexts) {
        choosePaginationPlugin(context);
      }
    }

    if (overwrite) {
      PluginConfiguration pluginConfiguration = new PluginConfiguration();
      pluginConfiguration.setConfigurationType(MapperOverwriteEnablePlugin.class.getTypeName());
      addToContext(contexts, pluginConfiguration);
      if (verbose) getLog().info("enable mapper overwrite service");
    }

    if (enableQueryDslSupport) {
      PluginConfiguration pluginConfiguration = new PluginConfiguration();
      pluginConfiguration.setConfigurationType(QueryDslPlugin.class.getTypeName());
      addToContext(contexts, pluginConfiguration);
      if (verbose) getLog().info("enable querydsl support service");
    }

    if (!disableMergeSupport) {
      callback.setMergeSupported(true);
      PluginConfiguration pluginConfiguration = new PluginConfiguration();
      pluginConfiguration.setConfigurationType(XMLMergePlugin.class.getTypeName());
      pluginConfiguration.addProperty(XMLMergePlugin.ROOTDIR_NAME, outputDirectory.getAbsolutePath());
      addToContext(contexts, pluginConfiguration);
      if (verbose) getLog().info("enable content merge service");
    }

    // leave the cg for the last
    extendCG(config, contexts);

  }

  /**
   * extend origin mbg the ability for generating comments
   */
  private void extendCG(Configuration config, List<Context> contexts) {
    // just use the extended comment generator

    PluginConfiguration pluginConfiguration = new PluginConfiguration();
    pluginConfiguration.setConfigurationType(CommentsWavePlugin.class.getTypeName());
    addToContext(contexts, pluginConfiguration);

    if (verbose) getLog().info("enable comment wave service");

    for (Context context : config.getContexts()) {
      context.getCommentGeneratorConfiguration().setConfigurationType(CommentGenerator.class.getTypeName());
      if (i18nPath != null && i18nPath.exists()) {
        context.getCommentGeneratorConfiguration().addProperty(CommentGenerator.XMBG_CG_I18N_PATH_KEY, i18nPath.getAbsolutePath());
      }
      if (StringUtils.isEmpty(projectStartYear)) {
        projectStartYear = CommentGenerator.XMBG_CG_PROJECT_START_DEFAULT_YEAR;
      }
      context.getCommentGeneratorConfiguration().addProperty(CommentGenerator.XMBG_CG_PROJECT_START_YEAR, projectStartYear);
      context.getCommentGeneratorConfiguration().addProperty(CommentGenerator.XMBG_CG_I18N_LOCALE_KEY, locale);
    }
    if (verbose) getLog().info("replace the origin comment generator");
  }

  /**
   * 根据context中的driver配置检测数据库的类别，并为其添加相应的分页插件
   *
   * @param context
   */
  void choosePaginationPlugin(Context context) {
    String dbName = detectDBName(context);
    if (dbName == null) return;
    if (dbName == null || dbName.trim().equals("")) return;
    PluginConfiguration pluginConfiguration = new PluginConfiguration();
    switch (dbName) {
      case "mysql":
        pluginConfiguration.setConfigurationType(MySqlPaginationPlugin.class.getTypeName());
        context.addPluginConfiguration(pluginConfiguration);
        if (verbose) getLog().info("enable pagination service with mysql for context " + context.getId());
        break;
      case "postgresql":
        pluginConfiguration.setConfigurationType(PostgreSQLPaginationPlugin.class.getTypeName());
        context.addPluginConfiguration(pluginConfiguration);
        if (verbose) getLog().info("enable pagination service with postgresql for context " + context.getId());
        break;
      case "sqlserver":
        pluginConfiguration.setConfigurationType(SQLServerPaginationPlugin.class.getTypeName());
        context.addPluginConfiguration(pluginConfiguration);
        if (verbose) getLog().info("enable pagination service with sqlserver for context " + context.getId());
        break;
      case "db2":
        pluginConfiguration.setConfigurationType(DB2PaginationPlugin.class.getTypeName());
        context.addPluginConfiguration(pluginConfiguration);
        if (verbose) getLog().info("enable pagination service with db2 for context " + context.getId());
        break;
      case "oracle":
        pluginConfiguration.setConfigurationType(OraclePaginationPlugin.class.getTypeName());
        context.addPluginConfiguration(pluginConfiguration);
        if (verbose) getLog().info("enable pagination service with oracle for context " + context.getId());
        break;
      case "hsqldb":
        pluginConfiguration.setConfigurationType(HsqldbPaginationPlugin.class.getTypeName());
        context.addPluginConfiguration(pluginConfiguration);
        if (verbose) getLog().info("enable pagination service with hsqldb for context " + context.getId());
        break;
    }
  }

  /**
   * 根据context中的driver配置检测数据库的类别，并为其添加相应的Upsert插件
   * @param context
   */
  void chooseUpsertPlugin(Context context) {
    String dbName = detectDBName(context);
    if (dbName == null) return;
    if (dbName == null || dbName.trim().equals("")) return;
    PluginConfiguration pluginConfiguration = new PluginConfiguration();
    switch (dbName){
      case "mysql":
        pluginConfiguration.setConfigurationType(MySqlUpsertPlugin.class.getTypeName());
        context.addPluginConfiguration(pluginConfiguration);
        if(verbose)getLog().info("enable upsert service with mysql for context " + context.getId());
        break;
      case "postgresql":
        pluginConfiguration.setConfigurationType(PostgreSQLUpsertPlugin.class.getTypeName());
        context.addPluginConfiguration(pluginConfiguration);
        if(verbose)getLog().info("enable upsert service with postgresql for context " + context.getId());
        break;
      case "sqlserver":
        pluginConfiguration.setConfigurationType(SQLServerUpsertPlugin.class.getTypeName());
        context.addPluginConfiguration(pluginConfiguration);
        if(verbose)getLog().info("enable upsert service with sqlserver for context " + context.getId());
        break;
      case "db2":
        pluginConfiguration.setConfigurationType(DB2UpsertPlugin.class.getTypeName());
        context.addPluginConfiguration(pluginConfiguration);
        if(verbose)getLog().info("enable upsert service with db2 for context " + context.getId());
        break;
      case "oracle":
        pluginConfiguration.setConfigurationType(OracleUpsertPlugin.class.getTypeName());
        context.addPluginConfiguration(pluginConfiguration);
        if(verbose)getLog().info("enable upsert service with oracle for context " + context.getId());
        break;
      case "hsqldb":
        pluginConfiguration.setConfigurationType(HsqldbUpsertPlugin.class.getTypeName());
        context.addPluginConfiguration(pluginConfiguration);
        if(verbose)getLog().info("enable upsert service with hsqldb for context " + context.getId());
        break;
    }
  }

  void chooseGeomPlugin(Context context) {
    String dbName = detectDBName(context);
    if (dbName == null || dbName.trim().equals("")) return;
    PluginConfiguration pluginConfiguration = new PluginConfiguration();
    switch (dbName) {
      case "mysql":
        break;
      case "postgresql":
        pluginConfiguration.setConfigurationType(PostgisGeoPlugin.class.getTypeName());
        pluginConfiguration.addProperty(PostgisGeoPlugin.SRID_NAME, srid);
        context.addPluginConfiguration(pluginConfiguration);
        if (verbose) getLog().info("enable geom service with postgresql for context " + context.getId());
        break;
    }
  }

  private String detectDBName(Context context) {
    JDBCConnectionConfiguration jdbcConnectionConfiguration = context.getJdbcConnectionConfiguration();
    String url = jdbcConnectionConfiguration.getConnectionURL();
    int start = url.indexOf(":");
    if (start == -1) return null;
    start += 1;
    int end = url.indexOf(":", start);
    if (end == -1) return null;
    String dbName = url.substring(start, end).toLowerCase();
    return dbName;
  }

  private void addToContext(List<Context> contexts, PluginConfiguration pluginConfiguration) {
    for (Context context : contexts) {
      context.addPluginConfiguration(pluginConfiguration);
    }
  }

  private void runScriptIfNecessary() throws MojoExecutionException {
    if (sqlScript == null) {
      return;
    }

    SqlScriptRunner scriptRunner = new SqlScriptRunner(sqlScript,
        jdbcDriver, jdbcURL, jdbcUserId, jdbcPassword);
    scriptRunner.setLog(getLog());
    scriptRunner.executeScript();
  }

  public File getOutputDirectory() {
    return outputDirectory;
  }
}