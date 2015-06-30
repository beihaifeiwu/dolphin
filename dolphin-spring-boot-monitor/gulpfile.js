var gulp = require('gulp');

gulp.task('css', function () {
    var postcss = require('gulp-postcss');
    return gulp.src('src/**/*.css')
        .pipe(postcss([require('cssnext')(), require('cssnano')()]))
        .pipe(gulp.dest('target/classes/resources/'));
});