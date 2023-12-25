let adminObject = {
    init: function() {
        let _this = this;

        $("#btn-update").on("click", (event) => {
            event.preventDefault();
            _this.updateUser();
        });
        $(".btn-delete").on("click", function(event) {
            event.preventDefault();
            let userId = $(this).data('user-delete-id');
            _this.deleteUser(userId);
        });

    },

    updateUser: function() {
        let user = { // user 객체 선언
            id: $("#id").val(),
            username: $("#username").val(),
            password: $("#password").val(),
            email: $("#email").val(),
            authority: {
                          authorityName: $("#authorityName").val()
            }
        }
        console.log(user);
        console.log($("#authorityName").val());

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
    },
    deleteUser: function(userId) {
            $.ajax({
                type: "DELETE",
                url: "/admin/usermanage/" + userId,
                contentType: "application/json; charset=utf-8"
            }).done(function(response) {
                alert("유저가 성공적으로 삭제되었습니다.");
                location.href = "/admin/usermanage";
            }).fail(function(error) {
                alert("에러 발생 : " + error);
            });
        }
}

adminObject.init();