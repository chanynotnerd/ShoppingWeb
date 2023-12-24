let adminObject = {
    init: function() {
        let _this = this;

        $("#btn-update").on("click", (event) => {
            event.preventDefault();
            _this.updateUser();
        });
    },

    updateUser: function() {
        let user = { // user 객체 선언
            id: $("#id").val(),
            username: $("#username").val(),
            password: $("#password").val(),
            email: $("#email").val(),
            authority: $("#authority").val()
        }
        console.log(user);
        $.ajax({
            type: "PUT",
            url: "/admin/usermanage/" + user.id,
            data: JSON.stringify(user),
            contentType: "application/json; charset=utf-8"
        }).done(function(response) {
            let status = response["status"];
            if (status === 200) {
                let message = response["data"];
                alert(message);
                location.href = "/admin/usermanage";
            } else {
                let warn = "";
                let errors = response["data"];
                if (errors.username != null) warn += errors.username + "\n";
                if (errors.password != null) warn += errors.password + "\n";
                if (errors.email != null) warn += errors.email + "\n";
                if (errors.role != null) warn += errors.role;
                alert(warn);
            }
        }).fail(function(error) {
            alert("에러 발생 : " + error);
        });
    }
}

adminObject.init();