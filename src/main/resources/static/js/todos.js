$(document).ready(function () {

    initTodoList();

    $("#verify").on("click", function () {
        post("../api/verify").done(function (data) {
            if (data.scope) {
                alert("Access Token is VALID");
            } else {
                alert("Access Token is INVALID");
            }
        });
    });

    $("#refreshToken").on("click", function () {
        post("../api/refreshToken").done(function (data) {
            if (data.scope) {
                alert("Access Token has been refreshed");
            } else {
                alert("Access Token has not been refreshed");
            }
        });
    });

    $("#revoke").on("click", function () {
        post("../api/revoke").done(function (data) {
            alert("Access Token has been revoked");
        });
    });

});

var initTodoList = function () {
    get("../api/v1/todos").done(function (data) {
        $('#todo-list').lobiList({
            defaultStyle: 'lobilist-success',
            controls: ['edit'],
            lists: [
                {
                    itemOptions: {
                        id: true,
                        task: '',
                        date: '',
                        completed: false
                    },
                    title: 'TODO',
                    items: data.data
                }
            ],
            actions: {
                'load': '',
                'update': '../api/v1/todos',
                'insert': '',
                'delete': ''
            }
        });
    });
};

var post = function (url) {
    var def = jQuery.Deferred();
    jQuery.ajax({
        type: 'POST',
        url: url,
        success: function (value) {
            def.resolve(value);
        },
        error: function (xhr) {
            def.reject(xhr.responseText);
        }
    });
    return def.promise();
};

var get = function (url) {
    var def = jQuery.Deferred();
    jQuery.ajax({
        type: 'GET',
        url: url,
        success: function (value) {
            def.resolve(value);
        },
        error: function (xhr) {
            def.reject(xhr.responseText);
        }
    });
    return def.promise();
};
