var gulp = require('gulp');
var webpack = require('gulp-webpack');
var named = require('vinyl-named');

var cssnext = require('cssnext')
var cssnano = require('cssnano')

var srcFiles = ['**/*.vue','**/*.css','**/*.js'];

function mapFiles(list) {
    return list.map(function (file) {
        return 'src/' + file;
    })
}

function webpackConfig(opt) {
    var config = {
        module: {
            loaders: [
                {test: /\.vue$/, loader: 'vue'},
                {test: /\.css$/, loader: "style!css!postcss"}
            ]
        },
        postcss: function () {
            return [cssnext, cssnano];
        }
    };
    if (!opt) return config;
    for (var i in opt) {
        config[i] = opt[i]
    }
    return config
}

gulp.task('bundle', function () {
    return gulp.src(mapFiles(srcFiles))
        .pipe(named())
        .pipe(webpack(webpackConfig()))
        .pipe(gulp.dest('target/classes/resources/'));
});

gulp.task('watch', function () {
    return gulp.src(mapFiles(srcFiles)
        .pipe(named())
        .pipe(webpack(webpackConfig({watch: true, devtool: 'source-map'})))
        .pipe(gulp.dest('target/classes/resources/')));
});

gulp.task("default", ['watch'], function () {
    console.log("done")
})