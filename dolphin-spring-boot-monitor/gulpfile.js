var basePaths = {
    src: 'src/assets/', 
    dest: { assets: 'build/assets/', statics: 'build/static/' },
    maven: { assets: 'target/classes/assets/', statics: 'target/classes/static/' }
};
var paths = {
    context: '/assets/',
    imgs: { src: basePaths.src + 'imgs/', dest: basePaths.dest.assets + 'imgs/' },
    jade: { src: basePaths.src + 'jade/*.jade', dest: basePaths.dest.statics },
    sprite: { src: basePaths.src + 'sprite/*.svg', dest: 'build/' },
    vue: { src: basePaths.src + "vue/**/*.vue", dest: basePaths.dest.assets },
};

var gulp = require('gulp');
var gutil = require('gulp-util');

var plugins = require("gulp-load-plugins")({
    pattern: ['gulp-*', 'gulp.*'],
    replaceString: /\bgulp[\-.]/
});

var webpack = require('webpack');

var vue = require('vue-loader');
var del = require('del');

var bourbon = require('node-bourbon').includePaths;

function webpackConfig(opt) {
    var ExtractTextPlugin = require('extract-text-webpack-plugin');
    var scssLoader = ExtractTextPlugin.extract('style','css!sass?outputStyle=expanded&includePaths[]=' + bourbon);
    var config = {
        output: {
            publicPath: paths.context,
            filename: "monitor.bundle.js"
        },
        plugins: [ new ExtractTextPlugin("monitor.css") ],
        module: {
            loaders: [
                {test: /\.vue$/, loader: vue.withLoaders({scss: scssLoader})},
                {test: /\.scss$/, exclude: /node_modules/, loader: scssLoader},
                {
                    test: /\.(jpe?g|png|gif|svg)$/i,
                    loaders: [
                        'file?name=[name].[ext]',
                        'image-webpack?{progressive:true, optimizationLevel: 7, interlaced: false, pngquant:{quality: "65-90", speed: 4}}'
                    ]
                }
            ]
        },
        devtool: 'source-map',
    };
    if (!opt) return config;
    for (var i in opt) {
        config[i] = opt[i]
    }
    return config
}

gulp.task('clean',function(){
    del([basePaths.dest.assets, basePaths.maven.assets]);
    gutil.log("clean assets directory");
    del([basePaths.dest.statics, basePaths.maven.statics]);
    gutil.log("clean static directory");
});

gulp.task('process-svgs', ['clean'], function(){

    return gulp.src([paths.sprite.src])
        .pipe(plugins.svgo())
        .pipe(plugins.svgSprite({
            shape: { id: { generator: "icon-%s" }},
            mode: { inline: true, symbol: true },
            svg: { xmlDeclaration: false, }
        }))
        .pipe(gulp.dest(paths.sprite.dest))
});

gulp.task('compile',['process-svgs'], function () {
    return gulp.src(basePaths.src + "monitor.js")
        .pipe(plugins.plumber())
        .pipe(plugins.webpack(webpackConfig(),webpack))
        .pipe(gulp.dest(basePaths.dest.assets));
});

gulp.task('process-css',['compile'], function(){
    return gulp.src(basePaths.dest.assets + "monitor.css")
        .pipe(plugins.sourcemaps.init())
        .pipe(plugins.minifyCss())
        .pipe(plugins.sourcemaps.write())
        .pipe(gulp.dest(basePaths.dest.assets))
});

gulp.task('process-jade',['process-css'], function(){
    var locals = { context: paths.context };
    return gulp.src(paths.jade.src)
        .pipe(plugins.jade({ 'locals': locals, pretty: true, basedir: paths.sprite.dest.substring(0, paths.sprite.dest.length) }))
        .pipe(gulp.dest(paths.jade.dest));
});


gulp.task('move-imgs',['process-jade'], function(){
    var imgs = ['*.svg','*.jpg','*.jpeg','*.gif','*.png'].map(function(ext){return basePaths.dest.assets + ext;});
    return gulp.src(imgs)
        .pipe(gulp.dest(paths.imgs.dest))
        .on('end', function(){
            gutil.log("moving images");
            del(imgs);
        });
});

gulp.task('clean-temp', ['move-imgs'], function(){
    gutil.log("clean all temp resources");
    del(paths.sprite.dest + "symbol");
})

gulp.task('copy-build', ['clean-temp'], function(){
    gutil.log('copy build resources to maven directory');
    gulp.src(basePaths.dest.assets + "**/*").pipe(gulp.dest(basePaths.maven.assets));
    gulp.src(basePaths.dest.statics + "**/*").pipe(gulp.dest(basePaths.maven.statics));

});


gulp.task('watch',function () {
    return gulp.watch([
            basePaths.src + "*.js", 
            basePaths.src + '*.svg', 
            basePaths.src + '*.scss', 
            basePaths.src + '*.vue',
            paths.imgs.src,
            paths.sprite.src,
            paths.jade.src,
            paths.vue.src
        ],['copy-build']);
});

gulp.task("default", ['copy-build'], function () {
    gutil.log("assets build done");
})