var gulp = require('gulp');
var glupWebpack = require('gulp-webpack');
var webpack = require('webpack');

var cssnano = require('cssnano');
var cssnext = require('cssnext');
var cssimport = require('postcss-import');

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
                {
                    test: /\.(jpe?g|png|gif|svg)$/i,
                    loaders: [
                        'file?hash=sha512&digest=hex&name=[hash].[ext]',
                        'image-webpack?{progressive:true, optimizationLevel: 7, interlaced: false, pngquant:{quality: "65-90", speed: 4}}'
                    ]
                }
            ]
        },
        devtool: 'source-map',
        postcss: function () {
            return [cssimport({path:["src/assets/"], transform:cssnext}), cssnano(), cssnext()];
        }
    };
    if (!opt) return config;
    for (var i in opt) {
        config[i] = opt[i]
    }
    return config
}

gulp.task('clean',function(){
    del(['target/classes/assets/']);
    gutil.log("clean target/classes/assets directory");
    del(['target/classes/static/']);
    gutil.log("clean target/classes/static directory");
})

gulp.task('compile',['clean'], function () {
    return gulp.src(mapFiles(srcFiles))
        .pipe(plumber())
        .pipe(glupWebpack(webpackConfig(),webpack))
        .pipe(gulp.dest('target/classes/assets/'));
});

gulp.task('move-imgs',['compile'], function(){
    var imgs = ['*.svg','*.jpg','*.jpeg','*.gif','*.png','*.svg'].map(function(ext){return 'target/classes/assets/' + ext;});
    return gulp.src(imgs)
        .pipe(gulp.dest('target/classes/assets/imgs/'))
        .on('end', function(){
            gutil.log("moving images");
            del(imgs);
        });
});

gulp.task('move-html',['move-imgs'], function(){
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