var gulp = require('gulp');
var glupWebpack = require('gulp-webpack');
var webpack = require('webpack');

var cssnano = require('cssnano')
var cssnext = require('cssnext')
var vue = require('vue-loader')

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
    var del = require('del');
    del(['target/classes/assets/'])
})

gulp.task('bundle',['clean'], function () {
    return gulp.src(mapFiles(srcFiles))
        .pipe(glupWebpack(webpackConfig(),webpack))
        .pipe(gulp.dest('target/classes/assets/'));
});

gulp.task('watch',['clean'], function () {
    return gulp.src(mapFiles(srcFiles)
        .pipe(glupWebpack(webpackConfig({watch: true, devtool: 'source-map'})),webpack)
        .pipe(gulp.dest('target/classes/assets/')));
});

gulp.task('server',['clean'], function () {
    var gutil = require("gulp-util");
    var WebpackDevServer = require("webpack-dev-server");
    var compiler = webpack(webpackConfig({watch: true, devtool: 'source-map'}))

    new WebpackDevServer(compiler, {
        // server and middleware options
        contentBase: "target/classes/assets/"
    }).listen(8080, "localhost", function (err) {
            if (err) throw new gutil.PluginError("webpack-dev-server", err);
            // Server listening
            gutil.log("[webpack-dev-server]", "http://localhost:8080/webpack-dev-server/index.html");

            // keep the server alive or continue?
            // callback();
        });
});

gulp.task("default", ['bundle'], function () {
    console.log("done")
})