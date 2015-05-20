// JavaScript Document
//支持Enter键登录
document.onkeydown = function (e) {
    if ($(".bac").length == 0) {
        if (!e) e = window.event;
        if ((e.keyCode || e.which) == 13) {
            var obtnLogin = document.getElementById("submit_btn")
            obtnLogin.focus();
        }
    }
}

$(function () {
    //提交表单
    $('#submit_btn').click(function () {
        //show_loading();
        var myReg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/; //邮件正则
        var $email = $('#email');
        var $password = $('#password');
        var $captcha = $('#captcha');

        if ($email.val() == '') {
            show_err_msg('邮箱还没填呢！');
            $email.focus();
            return false;
        } else if (!myReg.test($('#email').val())) {
            show_err_msg('您的邮箱格式错咯！');
            $email.focus();
            return false;
        } else if ($password.val() == '') {
            show_err_msg('密码还没填呢！');
            $password.focus();
            return false;
        } else if ($captcha.val() == '') {
            //ajax提交表单，#login_form为表单的ID。 如：$('#login_form').ajaxSubmit(function(data) { ... });
            //show_msg('登录成功咯！  正在为您跳转...','/');
            show_err_msg('验证码还没填呢！');
            $captcha.focus();
            return false;
        }
        $password.val(strEnc($password.val(), $captcha.val()));
        $captcha.val(window.md5($captcha.val()));
        return true;
    });
});