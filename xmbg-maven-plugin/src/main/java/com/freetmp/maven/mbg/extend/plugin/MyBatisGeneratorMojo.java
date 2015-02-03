package com.freetmp.maven.mbg.extend.plugin;

import com.freetmp.mbg.plugin.*;
import com.freetmp.mbg.plugin.batch.BatchInsertPlugin;
import com.freetmp.mbg.plugin.batch.BatchUpdatePlugin;
import com.freetmp.mbg.plugin.geom.PostgisGeoPlugin;
import com.freetmp.mbg.plugin.page.MySqlPaginationPlugin;
import com.freetmp.mbg.plugin.page.PostgreSQLPaginationPlugin;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.PluginConfiguration;
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
@Mojo(name = "generate",defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class MyBatisGeneratorMojo extends AbstractMojo {
    
    @Parameter(defaultValue ="${project}", required = true, readonly = true)
    private MavenProject project;
    
    @Parameter(property = "mybatis.generator.outputDirectory", 
            defaultValue = "${project.build.directory}/generated-sources/mybatis-generator", required = true)
    private File outputDirectory;

    /**
     * Location of the configuration file.
     */
    @Parameter(property = "mybatis.generator.configurationFile",
            defaultValue = "${basedir}/src/main/resources/generatorConfig.xml",required = true)
    private File configurationFile;

    /**
     * Specifies whether the mojo writes progress messages to the log
     */
    @Parameter(property = "mybatis.generator.verbose",defaultValue = "false")
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
    @Parameter(defaultValue = "false",property = "x.mybatis.generator.disableExtendMethods")
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
    @Parameter(defaultValue = "false", property = "x.mybatis.generator.disableContentMerge")
    private boolean disableContentMerge;

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

    public void execute() throws MojoExecutionException {

        // add resource directories to the classpath.  This is required to support
        // use of a properties file in the build.  Typically, the properties file
        // is in the project's source tree, but the plugin classpath does not
        // include the project classpath.
        @SuppressWarnings("unchecked")
        List<Resource> resources = project.getResources();
        List<String> resourceDirectories = new ArrayList<String>();
        for (Resource resource: resources) {
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

        Set<String> fullyqualifiedTables = new HashSet<String>();
        if (StringUtility.stringHasValue(tableNames)) {
            StringTokenizer st = new StringTokenizer(tableNames, ","); //$NON-NLS-1$
            while (st.hasMoreTokens()) {
                String s = st.nextToken().trim();
                if (s.length() > 0) {
                    fullyqualifiedTables.add(s);
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
            
            extendConfig(config);

            ShellCallback callback = new MavenShellCallback(this, overwrite);

            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
                    callback, warnings);

            myBatisGenerator.generate(new MavenProgressCallback(getLog(),
                    verbose), contextsToRun, fullyqualifiedTables);

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
     * 扩展配置实现XMBG
     * @param config
     */
    private void extendConfig( Configuration config){
        List<Context> contexts = config.getContexts();
        if(contexts == null ) return;
        
        if(!disableNameConversion){
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(ColumnNameConversionPlugin.class.getTypeName());
            pluginConfiguration.addProperty(ColumnNameConversionPlugin.COLUMN_PATTERN_NAME, columnPattern);
            addToContext(contexts, pluginConfiguration);
            if(verbose) getLog().info("enable name conversion service");
        }
        
        if(!disableExtendMethods){
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(BatchInsertPlugin.class.getTypeName());
            addToContext(contexts,pluginConfiguration);
            if(verbose) getLog().info("enable batch insert service");
            
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(BatchUpdatePlugin.class.getTypeName());
            addToContext(contexts,pluginConfiguration);
            if(verbose) getLog().info("enable batch update service");
            
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(UpsertPlugin.class.getTypeName());
            addToContext(contexts,pluginConfiguration);
            if(verbose) getLog().info("enable if existed update record else insert service");
        }
        
        if(!disableGeom){
            for(Context context : contexts){
                chooseGeomPlugin(context);
            }
        }
        
        if(!disablePagination){
            for(Context context : contexts){
                choosePaginationPlugin(context);
            }
        }
        
        if(overwrite){
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(MapperOverwriteEnablePlugin.class.getTypeName());
            addToContext(contexts,pluginConfiguration);
            if(verbose) getLog().info("enable mapper overwrite service");
        }
        
        if(enableQueryDslSupport){
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(QueryDslPlugin.class.getTypeName());
            addToContext(contexts,pluginConfiguration);
            if(verbose) getLog().info("enable querydsl support service");
        }
        
        if(!disableContentMerge){
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(ContentMergePlugin.class.getTypeName());
            pluginConfiguration.addProperty(ContentMergePlugin.ROOTDIR_NAME, outputDirectory.getAbsolutePath());
            if(verbose) getLog().info("enable content merge service");
        }
        
    }

    /**
     * 根据context中的driver配置检测数据库的类别，并为其添加相应的分页插件
     * @param context
     */
    void choosePaginationPlugin(Context context) {
        String dbName = detectDBName(context);
        if (dbName == null) return;
        if (dbName == null || dbName.trim().equals("")) return;
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        switch (dbName){
            case "mysql":
                pluginConfiguration.setConfigurationType(MySqlPaginationPlugin.class.getTypeName());
                context.addPluginConfiguration(pluginConfiguration);
                if(verbose)getLog().info("enable pagination service with mysql for context " + context.getId());
                break;
            case "postgresql":
                pluginConfiguration.setConfigurationType(PostgreSQLPaginationPlugin.class.getTypeName());
                context.addPluginConfiguration(pluginConfiguration);
                if(verbose)getLog().info("enable pagination service with postgresql for context " + context.getId());
                break;
        }
    }
    
    void chooseGeomPlugin(Context context){
        String dbName = detectDBName(context);
        if (dbName == null || dbName.trim().equals("")) return;
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        switch (dbName){
            case "mysql":
                break;
            case "postgresql":
                pluginConfiguration.setConfigurationType(PostgisGeoPlugin.class.getTypeName());
                pluginConfiguration.addProperty(PostgisGeoPlugin.SRID_NAME, srid);
                context.addPluginConfiguration(pluginConfiguration);
                if(verbose)getLog().info("enable geom service with postgresql for context " + context.getId());
                break;
        }
    }

    private String detectDBName(Context context) {
        JDBCConnectionConfiguration jdbcConnectionConfiguration = context.getJdbcConnectionConfiguration();
        String url = jdbcConnectionConfiguration.getConnectionURL();
        int start = url.indexOf(":");
        if(start == -1) return null;
        start += 1;
        int end = url.indexOf(":",start);
        if(end == -1) return null;
        String dbName = url.substring(start,end).toLowerCase();
        return dbName;
    }

    private void addToContext(List<Context> contexts, PluginConfiguration pluginConfiguration) {
        for(Context context : contexts){
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