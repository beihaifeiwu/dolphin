var gulp = require('gulp');
var glupWebpack = require('gulp-webpack');
var webpack = require('webpack');

var cssnano = require('cssnano');
var cssnext = require('cssnext');
var vue = require('vue-loader');
var del = require('del');
var gutil = require('gulp-util');
var plumber = require('gulp-plumber');
var srcFiles = ['**/*.vue', '**/*.css', '**/*.js'];

function mapFiles(list) {
    return list.map(function (file) {
        return 'src/' + file;
    })
}

function webpackConfig(opt) {
    var HtmlWebpackPlugin = require('html-webpack-plugin');
    var config = {
        output: {
            publicPath:"/assets/",
            filename:"monitor.bundle.js"
        },
        plugins: [
            new HtmlWebpackPlugin({
                title: "dolphin-spring-boot-monitor",
                template:"src/assets/template.html",
                inject: 'body'
            })
        ],
        module: {
            loaders: [
                {test: /\.vue$/, loader: vue.withLoaders({postcss: 'style!css!postcss'})},
                {test: /\.css$/, loader: "style!css!postcss"},
                {test: /\.(png|jpg)$/, loader: 'url?limit=8192'}
            ]
        },
        devtool: 'source-map',
        postcss: function () {
            return [cssnano(), cssnext()];
        }
    };
    if (!opt) return config;
    for (var i in opt) {
        config[i] = opt[i]
    }
    return config
}

gulp.task('clean',function(){
    del(['target/classes/assets/'])
})

gulp.task('compile',['clean'], function () {
    return gulp.src(mapFiles(srcFiles))
        .pipe(plumber())
        .pipe(glupWebpack(webpackConfig(),webpack))
        .pipe(gulp.dest('target/classes/assets/'));
});

gulp.task('move-html',['compile'], function(){
    return gulp.src(['target/classes/assets/index.html'])
        .pipe(gulp.dest('target/classes/static/'))
        .on('end',function(){
            gutil.log("moving index.html");
            del(['target/classes/assets/index.html']);
        });
});

gulp.task('watch',function () {
    return gulp.watch(mapFiles(srcFiles),['move-html']);
});

gulp.task("default", ['move-html'], function () {
    gutil.log("assets build done")
})